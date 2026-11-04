package com.perplxdai.voice

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import android.util.Base64
import com.perplxdai.BuildConfig
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.TimeUnit

/**
 * Streams microphone audio to the OpenAI Realtime API and plays the spoken reply.
 * Tool calls requested by the model are delegated to [onToolCall] (which runs off the socket thread).
 */
class RealtimeVoiceClient(
    private val onUserTranscript: (String) -> Unit,
    private val onAssistantTranscript: (String) -> Unit,
    private val onSpeakingChanged: (Boolean) -> Unit,
    private val onError: (String) -> Unit,
    private val onToolCall: suspend (name: String, arguments: JSONObject) -> String,
    private val toolScope: kotlinx.coroutines.CoroutineScope,
    private val onResponseFinished: () -> Unit = {},
    private val memoryPrompt: () -> String = { "" }
) {
    private val http = OkHttpClient.Builder()
        .pingInterval(20, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()
    private var socket: WebSocket? = null
    private var recorder: AudioRecord? = null
    private var track: AudioTrack? = null
    private var echoCanceler: AcousticEchoCanceler? = null
    private var micThread: Thread? = null
    private var playbackMonitorThread: Thread? = null
    @Volatile private var writtenAudioFrames = 0L
    @Volatile private var running = false
    @Volatile var micMuted = false
    @Volatile private var closedByUs = false
    @Volatile private var responseHasSpokenOutput = false
    private val pendingToolCalls = AtomicInteger()

    fun start() {
        if (BuildConfig.OPENAI_API_KEY.isBlank()) {
            onError("Add OPENAI_API_KEY to local.properties and rebuild the app.")
            return
        }
        running = true
        closedByUs = false
        val request = Request.Builder()
            .url("wss://api.openai.com/v1/realtime?model=$REALTIME_MODEL")
            .header("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .build()
        socket = http.newWebSocket(request, listener)
    }

    fun stop() {
        closedByUs = true
        running = false
        socket?.close(1000, "bye")
        socket = null
        micThread?.interrupt()
        micThread = null
        playbackMonitorThread?.interrupt()
        playbackMonitorThread = null
        runCatching { recorder?.stop() }
        recorder?.release()
        recorder = null
        echoCanceler?.release()
        echoCanceler = null
        runCatching { track?.stop() }
        track?.release()
        track = null
    }

    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            webSocket.send(sessionConfig().toString())
            startAudio(webSocket)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val event = JSONObject(text)
            when (event.optString("type")) {
                "response.created" -> responseHasSpokenOutput = false
                "response.output_audio.delta" -> {
                    val pcm = Base64.decode(event.getString("delta"), Base64.DEFAULT)
                    onSpeakingChanged(true)
                    val writtenBytes = track?.write(pcm, 0, pcm.size) ?: 0
                    if (writtenBytes > 0) writtenAudioFrames += writtenBytes / 2
                }
                "input_audio_buffer.speech_started" -> {
                    responseHasSpokenOutput = false
                    playbackMonitorThread?.interrupt()
                    playbackMonitorThread = null
                    track?.let { outputTrack ->
                        outputTrack.pause()
                        outputTrack.flush()
                        writtenAudioFrames = Integer.toUnsignedLong(outputTrack.playbackHeadPosition)
                        outputTrack.play()
                    }
                }
                "conversation.item.input_audio_transcription.completed" -> {
                    val transcript = event.optString("transcript").trim()
                    if (transcript.isNotEmpty()) onUserTranscript(transcript)
                }
                "response.output_audio_transcript.done" -> {
                    val transcript = event.optString("transcript").trim()
                    if (transcript.isNotEmpty()) {
                        responseHasSpokenOutput = true
                        onAssistantTranscript(transcript)
                    }
                }
                "response.done" -> {
                    onSpeakingChanged(false)
                    if (responseHasSpokenOutput && pendingToolCalls.get() == 0) {
                        responseHasSpokenOutput = false
                        waitForPlaybackToFinish()
                    }
                }
                "response.function_call_arguments.done" -> handleToolCall(webSocket, event)
                "error" -> {
                    val message = event.optJSONObject("error")?.optString("message").orEmpty()
                    onError(message.ifBlank { "The realtime connection reported an error." })
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            if (closedByUs) return
            val hint = if (response?.code == 401) "OpenAI rejected the API key." else t.message
            onError(hint ?: "Realtime connection failed.")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            if (!closedByUs) onError("Realtime connection closed.")
        }
    }

    private fun handleToolCall(webSocket: WebSocket, event: JSONObject) {
        val callId = event.getString("call_id")
        val name = event.getString("name")
        val args = runCatching { JSONObject(event.optString("arguments", "{}")) }.getOrDefault(JSONObject())
        pendingToolCalls.incrementAndGet()
        toolScope.launch {
            try {
                val output = try {
                    onToolCall(name, args)
                } catch (error: Exception) {
                    "Tool failed: ${error.message ?: "unknown error"}"
                }
                val item = JSONObject()
                    .put("type", "conversation.item.create")
                    .put(
                        "item",
                        JSONObject()
                            .put("type", "function_call_output")
                            .put("call_id", callId)
                            .put("output", output)
                    )
                webSocket.send(item.toString())
                webSocket.send(JSONObject().put("type", "response.create").toString())
            } finally {
                pendingToolCalls.decrementAndGet()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startAudio(webSocket: WebSocket) {
        writtenAudioFrames = 0L
        val minRecord = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        val record = AudioRecord(
            MediaRecorder.AudioSource.VOICE_COMMUNICATION,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            maxOf(minRecord, SAMPLE_RATE / 5 * 2)
        )
        if (record.state != AudioRecord.STATE_INITIALIZED) {
            onError("Microphone could not be opened.")
            return
        }

        if (AcousticEchoCanceler.isAvailable()) {
            echoCanceler = AcousticEchoCanceler.create(record.audioSessionId)?.also { it.enabled = true }
        }
        val minTrack = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
        track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(maxOf(minTrack, SAMPLE_RATE * 2))
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
            .also { it.play() }
        recorder = record
        record.startRecording()

        micThread = Thread {
            val buffer = ByteArray(SAMPLE_RATE / 10 * 2)
            while (running) {
                val read = record.read(buffer, 0, buffer.size)
                if (read <= 0 || micMuted) continue
                val audio = Base64.encodeToString(buffer, 0, read, Base64.NO_WRAP)
                webSocket.send(
                    JSONObject().put("type", "input_audio_buffer.append").put("audio", audio).toString()
                )
            }
        }.also { it.name = "perplxd-mic"; it.start() }
    }

    private fun waitForPlaybackToFinish() {
        playbackMonitorThread?.interrupt()
        val outputTrack = track
        if (outputTrack == null) {
            onResponseFinished()
            return
        }
        val targetFrames = writtenAudioFrames
        playbackMonitorThread = Thread {
            try {
                while (running) {
                    val playedFrames = Integer.toUnsignedLong(outputTrack.playbackHeadPosition)
                    if (playedFrames >= targetFrames) {
                        onResponseFinished()
                        return@Thread
                    }
                    Thread.sleep(25)
                }
            } catch (_: InterruptedException) {
                // The session is stopping or speech interrupted the current reply.
            }
        }.apply {
            name = "PerplxdPlaybackDrain"
            isDaemon = true
            start()
        }
    }

    private fun sessionConfig(): JSONObject {
        val tools = JSONArray()
            .put(
                tool(
                    "web_search",
                    "Search the web for current or factual information (news, weather, sports, prices, people, general knowledge you are unsure of).",
                    JSONObject().put("query", stringProp("What to search for")),
                    listOf("query")
                )
            )
            .put(
                tool(
                    "send_whatsapp_message",
                    "Draft a WhatsApp message. The user reviews it on screen before it is sent.",
                    JSONObject()
                        .put("recipient", stringProp("Contact name"))
                        .put("message", stringProp("Message text")),
                    listOf("recipient", "message")
                )
            )
            .put(
                tool(
                    "spotify_action",
                    "Control the user's Spotify account. Use this for playback, devices, queue, playlists, saved music, and listening history. For requests involving a track or playlist, pass its name rather than guessing an ID. To switch devices or edit playlists, list devices or playlists first if the user's request is ambiguous.",
                    JSONObject()
                        .put("action", enumProp(
                            "The Spotify operation to perform.",
                            listOf(
                                "play", "pause", "next", "previous", "now_playing", "volume", "seek",
                                "shuffle", "repeat", "queue_track", "show_queue", "list_devices",
                                "switch_device", "list_playlists", "create_playlist", "play_playlist",
                                "add_to_playlist", "remove_from_playlist", "move_playlist_track",
                                "save_track", "remove_saved_track", "save_album", "remove_saved_album",
                                "saved_tracks", "saved_albums", "recently_played", "top_tracks",
                                "top_artists", "reconnect"
                            )
                        ))
                        .put("title", stringProp("Track or album title, when needed."))
                        .put("artist", stringProp("Artist name, if known."))
                        .put("playlist", stringProp("Playlist name, for playlist operations."))
                        .put("playlist_id", stringProp("Playlist ID, only when already known."))
                        .put("device", stringProp("Spotify Connect device name."))
                        .put("volume_percent", integerProp("Requested volume from 0 to 100."))
                        .put("position_seconds", integerProp("Playback position in seconds."))
                        .put("enabled", JSONObject().put("type", "boolean").put("description", "Whether shuffle is enabled."))
                        .put("repeat_mode", enumProp("Repeat mode.", listOf("off", "context", "track")))
                        .put("from_position", integerProp("Current playlist item position, starting at 1."))
                        .put("to_position", integerProp("New playlist item position, starting at 1.")),
                    listOf("action")
                )
            )
            .put(
                tool(
                    "phone_action",
                    "Perform a supported Android phone action using its native app or settings page. Calls, texts, alarms, timers, and calendar entries open a user-review screen and must never be claimed completed until the user confirms in that app. YouTube searches open results; the user chooses the video.",
                    JSONObject()
                        .put("action", enumProp(
                            "The phone action.",
                            listOf(
                                "open_app", "open_settings", "set_alarm", "set_timer", "call", "text",
                                "calendar_event", "maps", "navigate", "youtube", "note", "type_text"
                            )
                        ))
                        .put("app", stringProp("Installed app name to open."))
                        .put("setting", enumProp(
                            "Android settings page to open.",
                            listOf("wifi", "bluetooth", "display", "sound", "location", "notifications", "app", "battery")
                        ))
                        .put("hour", integerProp("Alarm hour in 24-hour local time."))
                        .put("minute", integerProp("Alarm minute."))
                        .put("seconds", integerProp("Timer duration in seconds."))
                        .put("label", stringProp("Optional alarm or timer label."))
                        .put("phone_number", stringProp("Phone number for the dialer or SMS draft."))
                        .put("message", stringProp("Text message to prepare for user review, or the exact dictated note content for the note action."))
                        .put("title", stringProp("Calendar event title or optional note title."))
                        .put("start_time", stringProp("Calendar start in local YYYY-MM-DDTHH:MM format."))
                        .put("end_time", stringProp("Optional calendar end in local YYYY-MM-DDTHH:MM format."))
                        .put("description", stringProp("Optional calendar event details."))
                        .put("query", stringProp("Place, destination, or YouTube video search."))
                        .put("replace", stringProp("For type_text only: 'true' to replace the whole text box instead of appending.")),
                    listOf("action")
                )
            )
            .put(
                tool(
                    "memory_action",
                    "Manage the user's long-term memory, stored privately on their phone. Use remember when they ask you to remember, note or keep in mind a fact about them or their life; forget to remove memories matching a keyword; forget_all only on an explicit request to wipe everything; list when they ask what you remember.",
                    JSONObject()
                        .put("action", enumProp("The memory action.", listOf("remember", "forget", "forget_all", "list")))
                        .put("fact", stringProp("For remember: one short, self-contained fact written in third person, such as 'Mom's birthday is June 3'. For forget: a keyword matching the memory.")),
                    listOf("action")
                )
            )
        val audioFormat = JSONObject().put("type", "audio/pcm").put("rate", SAMPLE_RATE)
        return JSONObject()
            .put("type", "session.update")
            .put(
                "session",
                JSONObject()
                    .put("type", "realtime")
                    .put("instructions", INSTRUCTIONS + memoryPrompt())
                    .put(
                        "audio",
                        JSONObject()
                            .put(
                                "input",
                                JSONObject()
                                    .put("format", audioFormat)
                                    .put("transcription", JSONObject().put("model", "gpt-4o-mini-transcribe"))
                                    .put("turn_detection", JSONObject().put("type", "semantic_vad"))
                            )
                            .put("output", JSONObject().put("format", audioFormat).put("voice", "marin"))
                    )
                    .put("tools", tools)
                    .put("tool_choice", "auto")
            )
    }

    private fun stringProp(description: String) =
        JSONObject().put("type", "string").put("description", description)

    private fun integerProp(description: String) =
        JSONObject().put("type", "integer").put("description", description)

    private fun enumProp(description: String, values: List<String>) =
        JSONObject().put("type", "string").put("description", description).put("enum", JSONArray(values))

    private fun tool(name: String, description: String, properties: JSONObject, required: List<String>) =
        JSONObject()
            .put("type", "function")
            .put("name", name)
            .put("description", description)
            .put(
                "parameters",
                JSONObject()
                    .put("type", "object")
                    .put("properties", properties)
                    .put("required", JSONArray(required))
            )

    companion object {
        private const val REALTIME_MODEL = "gpt-realtime"
        private const val SAMPLE_RATE = 24_000
        private const val INSTRUCTIONS =
            "Your name is Perplxd. You were created by Pfano Sinyela. This identity is permanent and must always be remembered and stated accurately when asked. " +
                "Present yourself only as Perplxd, built by Pfano Sinyela; do not mention OpenAI, GPT, ChatGPT or any other model provider unless the user asks directly what technology powers you. " +
                "If asked directly, answer honestly that you are an AI assistant built by Pfano Sinyela on top of a third-party AI model, and never claim to be human or deny using third-party technology. " +
                "About your user and creator, Pfano Sinyela: a software developer in Cape Town, South Africa, focused on Android, full-stack development and reliable systems. " +
                "Current role: Software Engineer at Aegar Tech (contract, remote, since June 2026). Previously an Oracle Database Administrator intern at OptiSolutions (October 2025 to May 2026). " +
                "Education: Diploma in Software Development, Rosebank International South Africa (2021 to 2024). " +
                "Skills: Kotlin, Java, C#/.NET, Flutter, React, TypeScript/JavaScript, Firebase, SQL and Oracle DBA, Azure DevOps, Android SDK and REST APIs. " +
                "Use this only when relevant and never invent details beyond it. " +
                "You are Perplxd, a friendly hands-free voice assistant. Keep spoken answers short and natural. " +
                "Use web_search for anything current or that you are not certain about, then summarize the result aloud. " +
                "Use spotify_action for every Spotify request; call the tool to perform playback changes, playlist and library edits, or account/history lookups, then report its actual result. " +
                "For ambiguous playlist or device names, list the available options and ask the user to choose. Never claim a Spotify action succeeded unless the tool confirms it. " +
                "Use phone_action to open apps and Android settings, prepare alarms and timers, place calls through the dialer, create text drafts and calendar events for user review, search maps, navigate, or find requested videos on YouTube. " +
                "Use memory_action to remember or forget facts when the user asks, and never store passwords, card numbers or other secrets. After remembering, confirm briefly. " +
                "When the user asks you to type or write what they dictate into the app they are in, such as a chat message, call phone_action with action type_text and their words verbatim in message. It fills the selected text box but never sends; tell the user to review and send it themselves. " +
                "When the user dictates a note or asks you to write something down, call phone_action with action note and the dictated text verbatim in message; it opens their notes app with the text, so say it was opened for them to check. " +
                "Android intentionally requires the user to confirm some actions. Never claim to have placed a call, sent a text, saved an event, set an alarm, or started a YouTube video unless the target app confirms it; tell the user when a review screen or YouTube results have opened. " +
                "Use send_whatsapp_message when asked to text someone; for WhatsApp, tell the user the draft is ready to review on screen. Never claim a message was sent."
    }
}
