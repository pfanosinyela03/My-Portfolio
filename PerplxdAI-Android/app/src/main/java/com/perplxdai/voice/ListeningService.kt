package com.perplxdai.voice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.perplxdai.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class ListeningService : Service() {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val audioManager by lazy { getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private var client: RealtimeVoiceClient? = null
    private var shouldListen = false
    private var pausedForMedia = false
    private var awaitingWhatsAppReview = false
    private var pausedForWhatsApp = false
    private var pendingUserTranscript = ""
    private val mediaMonitor = object : Runnable {
        override fun run() {
            if (!shouldListen) return
            val playing = isMediaPlaying()
            if (playing != pausedForMedia) {
                pausedForMedia = playing
                updateMute()
                VoiceAssistantSession.setMediaPlaying(playing)
                notificationManager.notify(NOTIFICATION_ID, createNotification())
            }
            mainHandler.postDelayed(this, MEDIA_MONITOR_INTERVAL_MILLIS)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopListeningService()
                return START_NOT_STICKY
            }
            ACTION_PAUSE_FOR_WHATSAPP -> {
                if (!shouldListen) {
                    stopSelf()
                } else {
                    pausedForWhatsApp = true
                    updateMute()
                    VoiceAssistantSession.setWhatsAppHandoffPaused(true)
                    notificationManager.notify(NOTIFICATION_ID, createNotification())
                }
                return START_NOT_STICKY
            }
            ACTION_RESUME_AFTER_WHATSAPP -> {
                if (!shouldListen) {
                    stopSelf()
                } else {
                    pausedForWhatsApp = false
                    awaitingWhatsAppReview = false
                    updateMute()
                    VoiceAssistantSession.setWhatsAppHandoffPaused(false)
                    notificationManager.notify(NOTIFICATION_ID, createNotification())
                }
                return START_NOT_STICKY
            }
        }

        startForeground(NOTIFICATION_ID, createNotification())
        if (!shouldListen) {
            shouldListen = true
            VoiceAssistantSession.setActive(true)
            mainHandler.post(mediaMonitor)
            startRealtimeVoiceSession()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        shouldListen = false
        mainHandler.removeCallbacksAndMessages(null)
        client?.stop()
        client = null
        serviceScope.cancel()
        VoiceAssistantSession.serviceStopped()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun updateMute() {
        client?.micMuted = pausedForWhatsApp || awaitingWhatsAppReview
    }

    private fun startRealtimeVoiceSession() {
        if (!shouldListen || client != null) return
        client = RealtimeVoiceClient(
            onUserTranscript = { pendingUserTranscript = it },
            onAssistantTranscript = { reply ->
                val transcript = pendingUserTranscript.ifBlank { "…" }
                pendingUserTranscript = ""
                mainHandler.post {
                    VoiceAssistantSession.publishTurn(
                        transcript,
                        VoiceCommandResult(reply, "Perplxd listening")
                    )
                }
            },
            onSpeakingChanged = { speaking ->
                mainHandler.post {
                    if (shouldListen && !pausedForWhatsApp) {
                        VoiceAssistantSession.setSpeaking(speaking)
                    }
                }
            },
            onError = { message ->
                mainHandler.post { failListening(message) }
            },
            onToolCall = { name, args ->
                val result = RealtimeTools.run(applicationContext, name, args)
                if (VoiceAssistantSession.state.value.pendingWhatsAppRequest != null) {
                    mainHandler.post {
                        awaitingWhatsAppReview = true
                        updateMute()
                        notificationManager.notify(NOTIFICATION_ID, createNotification())
                    }
                }
                result
            },
            toolScope = serviceScope,
            memoryPrompt = { MemoryStore(applicationContext).promptSection() }
        ).also { it.start() }
    }

    private fun failListening(message: String) {
        if (!shouldListen) return
        disableHandsFreePreference()
        VoiceAssistantSession.reportError(message)
        stopListeningService()
    }

    @Suppress("DEPRECATION")
    private fun isMediaPlaying(): Boolean = audioManager.isMusicActive

    private fun stopListeningService() {
        shouldListen = false
        disableHandsFreePreference()
        client?.stop()
        client = null
        mainHandler.removeCallbacks(mediaMonitor)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun disableHandsFreePreference() {
        getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit()
            .putBoolean(KEY_HANDS_FREE_ENABLED, false)
            .apply()
    }

    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }

    private fun createNotification(): Notification {
        val stopIntent = Intent(this, ListeningService::class.java).setAction(ACTION_STOP)
        val stopPendingIntent = PendingIntent.getService(
            this,
            STOP_PENDING_INTENT_REQUEST,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val appIntent = Intent(this, MainActivity::class.java)
        val appPendingIntent = PendingIntent.getActivity(
            this,
            APP_PENDING_INTENT_REQUEST,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle(
                when {
                    pausedForMedia -> "Music playing · still listening"
                    awaitingWhatsAppReview -> "WhatsApp draft ready"
                    pausedForWhatsApp -> "Listening paused while WhatsApp is open"
                    else -> "Perplxd is listening"
                }
            )
            .setContentText(
                when {
                    pausedForMedia -> "Perplxd is still listening for voice commands."
                    awaitingWhatsAppReview -> "Tap to review the WhatsApp message before opening WhatsApp."
                    pausedForWhatsApp -> "Return to Perplxd to resume hands-free listening."
                    else -> "Tap Stop to turn off the microphone."
                }
            )
            .setContentIntent(appPendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .addAction(android.R.drawable.ic_media_pause, "Stop listening", stopPendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Hands-free listening",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows when Perplxd is actively using the microphone."
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        const val ACTION_START = "com.perplxdai.voice.START_LISTENING"
        const val ACTION_STOP = "com.perplxdai.voice.STOP_LISTENING"
        const val ACTION_PAUSE_FOR_WHATSAPP = "com.perplxdai.voice.PAUSE_FOR_WHATSAPP"
        const val ACTION_RESUME_AFTER_WHATSAPP = "com.perplxdai.voice.RESUME_AFTER_WHATSAPP"
        const val PREFERENCES = "voice_preferences"
        const val KEY_HANDS_FREE_ENABLED = "hands_free_enabled"

        private const val CHANNEL_ID = "perplxd_hands_free"
        private const val NOTIFICATION_ID = 2710
        private const val MEDIA_MONITOR_INTERVAL_MILLIS = 1_000L
        private const val STOP_PENDING_INTENT_REQUEST = 1
        private const val APP_PENDING_INTENT_REQUEST = 2

        fun start(context: Context) {
            val intent = Intent(context, ListeningService::class.java).setAction(ACTION_START)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, ListeningService::class.java))
            VoiceAssistantSession.setActive(false)
        }

        fun pauseForWhatsAppHandoff(context: Context) {
            context.startService(
                Intent(context, ListeningService::class.java).setAction(ACTION_PAUSE_FOR_WHATSAPP)
            )
        }

        fun resumeAfterWhatsAppHandoff(context: Context) {
            context.startService(
                Intent(context, ListeningService::class.java).setAction(ACTION_RESUME_AFTER_WHATSAPP)
            )
        }
    }
}
