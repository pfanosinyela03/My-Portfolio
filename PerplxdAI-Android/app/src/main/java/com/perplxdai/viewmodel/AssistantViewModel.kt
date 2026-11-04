package com.perplxdai.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.perplxdai.data.AssistantCommand
import com.perplxdai.data.defaultCommands
import com.perplxdai.voice.VoiceAssistantSession
import com.perplxdai.voice.VoiceCommandProcessor
import com.perplxdai.voice.VoiceTurn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect

data class AssistantMessage(
    val text: String,
    val isUser: Boolean,
    val time: String
)

data class AssistantUiState(
    val assistantName: String = "Perplxd AI",
    val statusText: String = "Online and ready",
    val commands: List<AssistantCommand> = defaultCommands,
    val lastResponse: String = "Good evening. I’m ready to help with planning, automation, and focused execution.",
    val messages: List<AssistantMessage> = listOf(
        AssistantMessage(text = "Good evening. I’m monitoring your priorities and systems.", isUser = false, time = "Now"),
        AssistantMessage(text = "Review my current tasks and keep the plan focused.", isUser = true, time = "Now")
    )
)

class AssistantViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()
    private var lastVoiceTurnId = 0L

    init {
        viewModelScope.launch {
            VoiceAssistantSession.state.collect { session ->
                val turn = session.lastTurn
                if (turn != null && turn.id > lastVoiceTurnId) {
                    lastVoiceTurnId = turn.id
                    addVoiceTurn(turn)
                }
            }
        }
    }

    fun activateCommand(command: AssistantCommand) {
        val nextText = when (command.id) {
            "status" -> "System health is stable. Core services are synchronized and ready for action."
            "research" -> "I’ve started gathering the most relevant information and am condensing it into the clearest path forward."
            "schedule" -> "Your priorities are organized and queued in the most efficient order for execution."
            "security" -> "Security checks are clear, and all primary systems remain protected and stable."
            "assistant" -> "Perplxd AI is active and ready to help with your next decision or execution step."
            else -> "Perplxd AI is active and ready to help with the next task."
        }

        val userMessage = AssistantMessage(text = "${command.title} requested", isUser = true, time = "Now")
        val assistantMessage = AssistantMessage(text = nextText, isUser = false, time = "Live")

        _uiState.value = _uiState.value.copy(
            statusText = when (command.id) {
                "status" -> "System checks running"
                "research" -> "Research mode engaged"
                "schedule" -> "Task planning active"
                "security" -> "Security sweep complete"
                else -> "Assistant online"
            },
            lastResponse = nextText,
            messages = listOf(userMessage, assistantMessage) + _uiState.value.messages
        )
    }

    fun handleVoiceCommand(context: Context, rawText: String) {
        val text = rawText.trim()
        val userMessage = AssistantMessage(text = text, isUser = true, time = "Now")
        _uiState.value = _uiState.value.copy(
            statusText = "Processing voice command",
            messages = listOf(userMessage) + _uiState.value.messages
        )

        viewModelScope.launch {
            val result = VoiceCommandProcessor.process(context.applicationContext, text)
            _uiState.value = _uiState.value.copy(
                statusText = result.status,
                lastResponse = result.response,
                messages = listOf(
                    AssistantMessage(text = result.response, isUser = false, time = "Live")
                ) + _uiState.value.messages
            )
        }
    }

    private fun addVoiceTurn(turn: VoiceTurn) {
        val userMessage = AssistantMessage(text = turn.transcript, isUser = true, time = "Now")
        val assistantMessage = AssistantMessage(text = turn.response, isUser = false, time = "Live")
        _uiState.value = _uiState.value.copy(
            statusText = turn.status,
            lastResponse = turn.response,
            messages = listOf(userMessage, assistantMessage) + _uiState.value.messages
        )
    }
}
