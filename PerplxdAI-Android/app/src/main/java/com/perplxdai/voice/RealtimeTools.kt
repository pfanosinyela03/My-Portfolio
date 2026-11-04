package com.perplxdai.voice

import android.content.Context
import com.perplxdai.BuildConfig
import com.perplxdai.messaging.WhatsAppMessageRequest
import com.perplxdai.music.SpotifyHistoryTrack
import com.perplxdai.music.SpotifyMusicController
import com.perplxdai.music.SpotifySongRequest
import com.perplxdai.music.SpotifyTrackResult
import com.perplxdai.phone.PhoneActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object RealtimeTools {
    private val http = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun run(context: Context, name: String, args: JSONObject): String = when (name) {
        "web_search" -> webSearch(args.optString("query"))
        "send_whatsapp_message" -> {
            val recipient = args.optString("recipient").trim()
            val message = args.optString("message").trim()
            if (recipient.isEmpty() || message.isEmpty()) {
                "Missing recipient or message."
            } else {
                VoiceAssistantSession.requestWhatsAppMessage(WhatsAppMessageRequest(recipient, message))
                "Draft for $recipient is ready for the user to review and send from the Perplxd screen."
            }
        }
        "play_spotify_song" -> {
            val title = args.optString("title").trim()
            val artist = args.optString("artist").trim().ifEmpty { null }
            try {
                val track = SpotifyMusicController(context).playSong(SpotifySongRequest(title, artist))
                "Now playing ${track.title} by ${track.artist}."
            } catch (error: Exception) {
                "Spotify couldn't play that: ${error.message ?: "authorization or search failed"}"
            }
        }
        "spotify_action" -> spotifyAction(context, args)
        "phone_action" -> phoneAction(context, args)
        "memory_action" -> memoryAction(context, args)
        else -> "Unknown tool $name."
    }

    private fun memoryAction(context: Context, args: JSONObject): String {
        val store = MemoryStore(context)
        return try {
            when (args.optString("action")) {
                "remember" -> store.remember(args.optString("fact"))
                "forget" -> store.forget(args.optString("fact"))
                "forget_all" -> store.clear()
                "list" -> store.all().takeIf { it.isNotEmpty() }?.joinToString("; ")
                    ?: "I don't have anything remembered yet."
                else -> "That memory action isn't supported."
            }
        } catch (error: IllegalArgumentException) {
            error.message ?: "I couldn't do that with my memory."
        }
    }

    private fun phoneAction(context: Context, args: JSONObject): String {
        val arguments = buildMap {
            val keys = listOf(
                "app", "setting", "hour", "minute", "label", "seconds", "phone_number",
                "message", "title", "start_time", "end_time", "description", "query", "replace"
            )
            keys.forEach { key ->
                if (args.has(key) && !args.isNull(key)) {
                    put(key, args.optString(key))
                }
            }
        }
        return try {
            PhoneActions.run(context, args.optString("action"), arguments)
        } catch (error: Exception) {
            "I couldn't complete that phone action: ${error.message ?: "Android did not accept the request"}"
        }
    }

    private suspend fun spotifyAction(context: Context, args: JSONObject): String {
        val spotify = SpotifyMusicController(context)
        val action = args.optString("action").trim()
        val title = args.optString("title").trim()
        val artist = args.optString("artist").trim()
        val playlistId = args.optString("playlist_id").trim()
        val playlistName = args.optString("playlist").trim()
        val playlist = if (action in setOf(
                "play_playlist", "add_to_playlist", "remove_from_playlist", "move_playlist_track"
            )
        ) {
            when {
                playlistId.isNotEmpty() -> spotify.getPlaylists().firstOrNull { it.id == playlistId }
                    ?: throw IllegalArgumentException("I couldn't find that Spotify playlist.")
                playlistName.isNotEmpty() -> spotify.getPlaylistByName(playlistName)
                    ?: throw IllegalArgumentException("I couldn't find a playlist named $playlistName.")
                else -> null
            }
        } else {
            null
        }

        fun requireTitle() {
            require(title.isNotBlank()) { "Tell me the track or album name." }
        }

        suspend fun findTrack(): SpotifyTrackResult {
            requireTitle()
            val results = spotify.searchTracks(listOf(title, artist).filter { it.isNotBlank() }.joinToString(" "))
            if (results.isEmpty()) throw IllegalArgumentException("I couldn't find that track on Spotify.")
            return results.firstOrNull {
                it.title.equals(title, ignoreCase = true) &&
                    (artist.isBlank() || it.artist.contains(artist, ignoreCase = true))
            } ?: results.first()
        }

        suspend fun findAlbum(): SpotifyTrackResult {
            requireTitle()
            val results = spotify.searchAlbums(listOf(title, artist).filter { it.isNotBlank() }.joinToString(" "))
            val album = results.firstOrNull {
                it.track.title.equals(title, ignoreCase = true) &&
                    (artist.isBlank() || it.track.artist.contains(artist, ignoreCase = true))
            } ?: results.firstOrNull()
                ?: throw IllegalArgumentException("I couldn't find that album on Spotify.")
            return album.track
        }

        return when (action) {
            "play" -> {
                if (title.isBlank()) {
                    spotify.controlPlayback("play")
                    "Resumed Spotify playback."
                } else {
                    val track = findTrack()
                    spotify.playTrack(track.uri)
                    "Playing ${track.title} by ${track.artist}."
                }
            }
            "pause", "next", "previous" -> {
                spotify.controlPlayback(action)
                when (action) {
                    "pause" -> "Paused Spotify."
                    "next" -> "Skipped to the next track."
                    else -> "Returned to the previous track."
                }
            }
            "now_playing" -> {
                val playback = spotify.getPlayback()
                    ?: return "Spotify has no active playback. Open Spotify on a device and try again."
                val track = playback.track?.let { "${it.title} by ${it.artist}" } ?: "No track is currently playing"
                "$track. ${if (playback.isPlaying) "Playing" else "Paused"}" +
                    (playback.deviceName?.let { " on $it." } ?: ".")
            }
            "volume" -> {
                require(args.has("volume_percent")) { "Tell me a volume from 0 to 100." }
                val volume = args.getInt("volume_percent").coerceIn(0, 100)
                spotify.setVolume(volume)
                "Set Spotify volume to $volume percent."
            }
            "seek" -> {
                require(args.has("position_seconds")) { "Tell me the playback position in seconds." }
                val seconds = args.getInt("position_seconds").coerceAtLeast(0)
                spotify.seekTo(seconds * 1000)
                "Skipped to $seconds seconds."
            }
            "shuffle" -> {
                val enabled = args.optBoolean("enabled", true)
                spotify.setShuffle(enabled)
                "Spotify shuffle ${if (enabled) "on" else "off"}."
            }
            "repeat" -> {
                val mode = args.optString("repeat_mode", "context")
                spotify.setRepeat(mode)
                "Spotify repeat is set to $mode."
            }
            "queue_track" -> {
                val track = findTrack()
                spotify.addToQueue(track.uri)
                "Added ${track.title} by ${track.artist} to the Spotify queue."
            }
            "show_queue" -> {
                val queue = spotify.getQueue()
                queue.tracks.takeIf { it.isNotEmpty() }?.joinToString("; ") { "${it.title} by ${it.artist}" }
                    ?.let { "Up next: $it." } ?: "The Spotify queue is empty."
            }
            "list_devices" -> {
                val devices = spotify.getDevices()
                devices.takeIf { it.isNotEmpty() }?.joinToString("; ") {
                    "${it.name} (${it.type})${if (it.isActive) ", active" else ""}"
                } ?: "No Spotify Connect devices are available. Open Spotify on a device and try again."
            }
            "switch_device" -> {
                val deviceName = args.optString("device").trim()
                require(deviceName.isNotEmpty()) { "Tell me which Spotify device to use." }
                val device = spotify.getDeviceByName(deviceName)
                    ?: throw IllegalArgumentException("I couldn't find a Spotify device named $deviceName.")
                val deviceId = device.id ?: throw IllegalArgumentException("Spotify did not provide an ID for that device.")
                spotify.transferPlayback(deviceId)
                "Switched Spotify playback to ${device.name}."
            }
            "list_playlists" -> {
                val playlists = spotify.getPlaylists()
                playlists.takeIf { it.isNotEmpty() }?.joinToString("; ") {
                    "${it.name} (${it.tracksTotal} tracks)"
                } ?: "You don't have any Spotify playlists."
            }
            "create_playlist" -> {
                val name = args.optString("playlist").trim()
                require(name.isNotEmpty()) { "Tell me a name for the new playlist." }
                val created = spotify.createPlaylist(name)
                "Created the private Spotify playlist ${created.name}."
            }
            "play_playlist" -> {
                val selected = playlist ?: throw IllegalArgumentException("Tell me which playlist to play.")
                spotify.playContext("spotify:playlist:${selected.id}")
                "Playing the playlist ${selected.name}."
            }
            "add_to_playlist" -> {
                val selected = playlist ?: throw IllegalArgumentException("Tell me which playlist to add the track to.")
                val track = findTrack()
                spotify.addTrackToPlaylist(selected.id, track.uri)
                "Added ${track.title} to ${selected.name}."
            }
            "remove_from_playlist" -> {
                val selected = playlist ?: throw IllegalArgumentException("Tell me which playlist to remove the track from.")
                val track = findTrack()
                spotify.removeTrackFromPlaylist(selected.id, track.uri)
                "Removed ${track.title} from ${selected.name}."
            }
            "move_playlist_track" -> {
                val selected = playlist ?: throw IllegalArgumentException("Tell me which playlist to reorder.")
                require(args.has("from_position") && args.has("to_position")) {
                    "Tell me the track's current position and its new position."
                }
                val from = args.getInt("from_position") - 1
                val to = args.getInt("to_position") - 1
                require(from >= 0 && to >= 0) { "Playlist positions start at 1." }
                spotify.reorderPlaylistTrack(selected.id, from, to)
                "Moved the track in ${selected.name}."
            }
            "save_track", "remove_saved_track" -> {
                val track = findTrack()
                spotify.saveTrack(track.uri, action == "save_track")
                if (action == "save_track") "Saved ${track.title} to your Spotify library."
                else "Removed ${track.title} from your Spotify library."
            }
            "save_album", "remove_saved_album" -> {
                val album = findAlbum()
                spotify.saveAlbum(album.uri, action == "save_album")
                if (action == "save_album") "Saved ${album.title} to your Spotify library."
                else "Removed ${album.title} from your Spotify library."
            }
            "saved_tracks" -> describeTracks(spotify.getSavedTracks(), "Your saved tracks")
            "saved_albums" -> describeHistory(spotify.getSavedAlbums(), "Your saved albums")
            "recently_played" -> describeHistory(spotify.getRecentlyPlayed(), "Recently played")
            "top_tracks" -> describeTracks(spotify.getTopTracks(), "Your top tracks")
            "top_artists" -> describeHistory(spotify.getTopArtists(), "Your top artists")
            "reconnect" -> {
                spotify.reauthorize()
                "Spotify is connected with the requested permissions."
            }
            else -> throw IllegalArgumentException("I don't know how to perform that Spotify action.")
        }
    }

    private fun describeTracks(tracks: List<SpotifyTrackResult>, heading: String): String =
        tracks.takeIf { it.isNotEmpty() }?.joinToString("; ") { "${it.title} by ${it.artist}" }
            ?.let { "$heading: $it." } ?: "Spotify returned no items."

    private fun describeHistory(tracks: List<SpotifyHistoryTrack>, heading: String): String =
        tracks.takeIf { it.isNotEmpty() }?.joinToString("; ") {
            if (it.track.artist.isBlank()) it.track.title else "${it.track.title} by ${it.track.artist}"
        }?.let { "$heading: $it." } ?: "Spotify returned no items."

    private suspend fun webSearch(query: String): String = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext "Empty search query."
        val body = JSONObject()
            .put("model", "gpt-4.1-mini")
            .put("tools", JSONArray().put(JSONObject().put("type", "web_search")))
            .put("input", "Answer concisely (under 80 words) using current web results: $query")
            .toString()
            .toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://api.openai.com/v1/responses")
            .header("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .post(body)
            .build()
        try {
            http.newCall(request).execute().use { response ->
                val text = response.body?.string().orEmpty()
                if (!response.isSuccessful) return@use "Web search failed (HTTP ${response.code})."
                val output = JSONObject(text).optJSONArray("output") ?: return@use "No search results."
                val answer = StringBuilder()
                for (i in 0 until output.length()) {
                    val item = output.getJSONObject(i)
                    if (item.optString("type") != "message") continue
                    val content = item.optJSONArray("content") ?: continue
                    for (j in 0 until content.length()) {
                        answer.append(content.getJSONObject(j).optString("text"))
                    }
                }
                answer.toString().ifBlank { "No search results." }
            }
        } catch (error: Exception) {
            "Web search failed: ${error.message ?: "network error"}"
        }
    }
}
