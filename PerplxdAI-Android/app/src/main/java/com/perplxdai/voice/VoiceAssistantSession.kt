package com.perplxdai.voice

import android.content.Context
import com.perplxdai.messaging.WhatsAppContact
import com.perplxdai.messaging.WhatsAppMessageRequest
import com.perplxdai.messaging.parseWhatsAppMessageRequest
import com.perplxdai.music.SpotifyMusicController
import com.perplxdai.music.parseSpotifySongRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class VoiceTurn(
    val id: Long,
    val transcript: String,
    val response: String,
    val status: String
)

data class VoiceSessionState(
    val isActive: Boolean = false,
    val isProcessing: Boolean = false,
    val partialTranscript: String = "",
    val statusText: String = "Hands-free listening is off",
    val error: String? = null,
    val lastTurn: VoiceTurn? = null,
    val pendingWhatsAppRequest: WhatsAppMessageRequest? = null,
    val whatsappContacts: List<WhatsAppContact> = emptyList(),
    val whatsappError: String? = null,
    val pausedForWhatsApp: Boolean = false
)

data class VoiceCommandResult(
    val response: String,
    val status: String
)

object VoiceAssistantSession {
    private val _state = MutableStateFlow(VoiceSessionState())
    val state: StateFlow<VoiceSessionState> = _state.asStateFlow()
    private var nextTurnId = 0L

    fun setActive(active: Boolean) {
        _state.value = _state.value.copy(
            isActive = active,
            isProcessing = false,
            partialTranscript = "",
            statusText = if (active) "Perplxd listening" else "Hands-free listening is off",
            error = null
        )
    }

    fun setMediaPlaying(playing: Boolean) {
        _state.value = _state.value.copy(
            isActive = true,
            isProcessing = false,
            partialTranscript = "",
            statusText = if (playing) "Music playing · still listening" else "Perplxd listening"
        )
    }

    fun serviceStopped() {
        val current = _state.value
        _state.value = current.copy(
            isActive = false,
            isProcessing = false,
            partialTranscript = "",
            pausedForWhatsApp = false,
            statusText = if (current.error == null) "Hands-free listening is off" else "Voice input unavailable"
        )
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null, statusText = "Hands-free listening is off")
    }

    fun setPartialTranscript(transcript: String) {
        _state.value = _state.value.copy(partialTranscript = transcript)
    }

    fun setSpeaking(speaking: Boolean) {
        _state.value = _state.value.copy(
            statusText = if (speaking) "Perplxd speaking" else "Perplxd listening"
        )
    }

    fun setProcessing(transcript: String) {
        _state.value = _state.value.copy(
            isProcessing = true,
            partialTranscript = transcript,
            statusText = "Perplxd is thinking"
        )
    }

    fun requestWhatsAppMessage(request: WhatsAppMessageRequest) {
        _state.value = _state.value.copy(
            isActive = true,
            isProcessing = false,
            partialTranscript = "",
            statusText = "WhatsApp message ready to review",
            pendingWhatsAppRequest = request,
            whatsappContacts = emptyList(),
            whatsappError = null,
            pausedForWhatsApp = false
        )
    }

    fun setWhatsAppContacts(contacts: List<WhatsAppContact>) {
        _state.value = _state.value.copy(whatsappContacts = contacts, whatsappError = null)
    }

    fun setWhatsAppError(message: String) {
        _state.value = _state.value.copy(whatsappError = message)
    }

    fun setWhatsAppHandoffPaused(paused: Boolean) {
        _state.value = _state.value.copy(
            pausedForWhatsApp = paused,
            statusText = if (paused) "WhatsApp open · listening paused" else "Perplxd listening"
        )
    }

    fun finishWhatsAppMessage(status: String) {
        _state.value = _state.value.copy(
            pendingWhatsAppRequest = null,
            whatsappContacts = emptyList(),
            whatsappError = null,
            pausedForWhatsApp = true,
            statusText = status
        )
    }

    fun cancelWhatsAppMessage() {
        _state.value = _state.value.copy(
            pendingWhatsAppRequest = null,
            whatsappContacts = emptyList(),
            whatsappError = null,
            statusText = if (_state.value.isActive) "Perplxd listening" else "Hands-free listening is off"
        )
    }

    fun publishTurn(transcript: String, result: VoiceCommandResult) {
        nextTurnId += 1
        _state.value = _state.value.copy(
            isProcessing = false,
            partialTranscript = "",
            statusText = result.status,
            error = null,
            lastTurn = VoiceTurn(nextTurnId, transcript, result.response, result.status)
        )
    }

    fun reportError(message: String) {
        _state.value = _state.value.copy(
            isActive = false,
            isProcessing = false,
            partialTranscript = "",
            statusText = "Voice input unavailable",
            error = message
        )
    }
}

object VoiceCommandProcessor {
    suspend fun process(context: Context, rawText: String): VoiceCommandResult {
        val text = rawText.trim()
        val whatsAppRequest = parseWhatsAppMessageRequest(text)
        if (whatsAppRequest != null) {
            VoiceAssistantSession.requestWhatsAppMessage(whatsAppRequest)
            return VoiceCommandResult(
                response = "I’ve drafted a WhatsApp message for ${whatsAppRequest.recipientName}. Open Perplxd to review and continue.",
                status = "WhatsApp message ready to review"
            )
        }

        val songRequest = parseSpotifySongRequest(text)

        if (songRequest != null) {
            return try {
                val track = SpotifyMusicController(context).playSong(songRequest)
                VoiceCommandResult(
                    response = "Playing ${track.title} by ${track.artist}.",
                    status = "Spotify playback started"
                )
            } catch (error: Exception) {
                VoiceCommandResult(
                    response = "Spotify couldn't play that request: ${error.message ?: "authorization or search failed"}",
                    status = "Spotify request failed"
                )
            }
        }

        return VoiceCommandResult(
            response = "I didn't recognize that command. You can ask me to play a song or draft a WhatsApp message.",
            status = "Command not recognized"
        )
    }
}
