package com.perplxdai

import android.Manifest
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.perplxdai.messaging.WhatsAppContact
import com.perplxdai.messaging.findWhatsAppContacts
import com.perplxdai.messaging.openWhatsAppDraft
import com.perplxdai.ui.theme.PerplxdAITheme
import com.perplxdai.voice.ListeningService
import com.perplxdai.voice.VoiceAssistantSession
import com.perplxdai.viewmodel.AssistantViewModel

class MainActivity : ComponentActivity() {
    private val assistantViewModel by lazy { AssistantViewModel() }

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            requestMicrophonePermissionOrEnable()
        } else {
            VoiceAssistantSession.reportError(
                "Notification permission is required so Android can show when hands-free listening is active."
            )
        }
    }

    private val requestAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            enableHandsFreeListening()
        } else {
            VoiceAssistantSession.reportError("Microphone permission is needed for hands-free listening.")
        }
    }

    private val requestContactsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            prepareWhatsAppMessage()
        } else {
            VoiceAssistantSession.setWhatsAppError(
                "Contacts access is needed to find that person. You can enable it in App settings."
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PerplxdAITheme {
                PerplxdApp(
                    viewModel = assistantViewModel,
                    onMicTap = { toggleHandsFreeListening() },
                    onWhatsAppReview = { startWhatsAppReview() },
                    onWhatsAppContactSelected = { contact -> openWhatsAppDraft(contact) },
                    onWhatsAppCancel = {
                        VoiceAssistantSession.cancelWhatsAppMessage()
                        resumeHandsFreeIfEnabled()
                    }
                )
            }
        }

        val preferences = getSharedPreferences(ListeningService.PREFERENCES, MODE_PRIVATE)
        if (preferences.getBoolean(ListeningService.KEY_HANDS_FREE_ENABLED, false)) {
            if (hasListeningPermissions()) {
                enableHandsFreeListening()
            } else {
                preferences.edit().putBoolean(ListeningService.KEY_HANDS_FREE_ENABLED, false).apply()
                VoiceAssistantSession.reportError(
                    "Microphone or notification permission was revoked. Enable hands-free listening again to continue."
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (VoiceAssistantSession.state.value.pausedForWhatsApp) {
            resumeHandsFreeIfEnabled()
        }
    }

    private fun hasListeningPermissions(): Boolean {
        val microphoneGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        val notificationsGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        return microphoneGranted && notificationsGranted
    }

    private fun toggleHandsFreeListening() {
        val preferences = getSharedPreferences(ListeningService.PREFERENCES, MODE_PRIVATE)
        val isEnabled = preferences.getBoolean(ListeningService.KEY_HANDS_FREE_ENABLED, false)
        if (isEnabled || VoiceAssistantSession.state.value.isActive) {
            preferences.edit().putBoolean(ListeningService.KEY_HANDS_FREE_ENABLED, false).apply()
            ListeningService.stop(this)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }

        requestMicrophonePermissionOrEnable()
    }

    private fun requestMicrophonePermissionOrEnable() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            enableHandsFreeListening()
        } else {
            requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun enableHandsFreeListening() {
        getSharedPreferences(ListeningService.PREFERENCES, MODE_PRIVATE)
            .edit()
            .putBoolean(ListeningService.KEY_HANDS_FREE_ENABLED, true)
            .apply()
        try {
            ListeningService.start(this)
        } catch (error: SecurityException) {
            reportListeningStartFailure(
                error.message ?: "Android did not allow hands-free microphone access."
            )
        } catch (error: IllegalStateException) {
            reportListeningStartFailure(
                error.message ?: "Android could not start the microphone service from the current app state."
            )
        }
    }

    private fun reportListeningStartFailure(message: String) {
        getSharedPreferences(ListeningService.PREFERENCES, MODE_PRIVATE)
            .edit()
            .putBoolean(ListeningService.KEY_HANDS_FREE_ENABLED, false)
            .apply()
        VoiceAssistantSession.reportError(message)
    }

    private fun startWhatsAppReview() {
        if (VoiceAssistantSession.state.value.pendingWhatsAppRequest == null) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            prepareWhatsAppMessage()
        } else {
            requestContactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private fun resumeHandsFreeIfEnabled() {
        val enabled = getSharedPreferences(ListeningService.PREFERENCES, MODE_PRIVATE)
            .getBoolean(ListeningService.KEY_HANDS_FREE_ENABLED, false)
        if (enabled) {
            ListeningService.resumeAfterWhatsAppHandoff(this)
        } else {
            VoiceAssistantSession.setActive(false)
        }
    }

    private fun prepareWhatsAppMessage() {
        val request = VoiceAssistantSession.state.value.pendingWhatsAppRequest ?: return
        val matches = try {
            findWhatsAppContacts(this, request.recipientName)
        } catch (error: SecurityException) {
            VoiceAssistantSession.setWhatsAppError(
                error.message ?: "Perplxd could not read your contacts."
            )
            return
        } catch (error: SQLiteException) {
            VoiceAssistantSession.setWhatsAppError(
                error.message ?: "Perplxd could not search your contacts."
            )
            return
        }

        when (matches.size) {
            0 -> VoiceAssistantSession.setWhatsAppError(
                "I couldn't find ${request.recipientName} in your contacts. Check the name and try again."
            )
            1 -> openWhatsAppDraft(matches.single())
            else -> VoiceAssistantSession.setWhatsAppContacts(matches)
        }
    }

    private fun openWhatsAppDraft(contact: WhatsAppContact) {
        val request = VoiceAssistantSession.state.value.pendingWhatsAppRequest ?: return
        ListeningService.pauseForWhatsAppHandoff(this)
        if (!openWhatsAppDraft(this, contact, request.message)) {
            resumeHandsFreeIfEnabled()
            VoiceAssistantSession.setWhatsAppError(
                "I couldn't open WhatsApp. Make sure WhatsApp is installed and ${contact.name}'s number includes its country code."
            )
            return
        }
        VoiceAssistantSession.finishWhatsAppMessage(
            "WhatsApp draft opened. Review it and tap Send when you're ready."
        )
    }
}
