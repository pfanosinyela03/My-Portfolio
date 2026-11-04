package com.perplxdai.music

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.SecureRandom

data class SpotifyTrackResult(val title: String, val artist: String, val uri: String)
data class SpotifyPlaylist(val id: String, val name: String, val tracksTotal: Int)
data class SpotifyDevice(val id: String?, val name: String, val type: String, val isActive: Boolean)
data class SpotifyPlayback(
    val track: SpotifyTrackResult?,
    val isPlaying: Boolean,
    val progressMs: Int,
    val durationMs: Int,
    val volumePercent: Int?,
    val shuffleState: Boolean,
    val repeatState: String,
    val deviceName: String?
)
data class SpotifyHistoryTrack(val track: SpotifyTrackResult, val detail: String)
data class SpotifyQueue(val currentTrack: SpotifyTrackResult?, val tracks: List<SpotifyTrackResult>)

class SpotifyMusicController(context: Context) {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences("spotify_auth", Context.MODE_PRIVATE)

    suspend fun playSong(request: SpotifySongRequest): SpotifyTrackResult = withContext(Dispatchers.IO) {
        val accessToken = getAccessToken()
        val track = searchTrack(accessToken, request)
        try {
            startTrackPlayback(track.uri)
        } catch (firstError: IOException) {
            // Usually "no active device": launch Spotify so it registers as a device, then retry once.
            runCatching { openTrack(track.uri) }
            delay(4_000L)
            try {
                startTrackPlayback(track.uri)
            } catch (_: IOException) {
                throw IOException(
                    "${firstError.message ?: "Spotify wouldn't start playback"}. " +
                        "Open the Spotify app and play any song once, then try again. Playback control also needs Spotify Premium."
                )
            }
        }
        track
    }

    private fun startTrackPlayback(uri: String) {
        apiRequest("PUT", "/me/player/play", JSONObject().put("uris", org.json.JSONArray().put(uri)))
    }

    suspend fun reauthorize() = withContext(Dispatchers.IO) {
        synchronized(AUTH_LOCK) {
            preferences.edit().clear().apply()
            authorizeAndGetToken()
        }
    }

    suspend fun getPlayback(): SpotifyPlayback? = withContext(Dispatchers.IO) {
        val response = apiRequest("GET", "/me/player")
        if (response.length() == 0) return@withContext null
        val item = response.optJSONObject("item")
        val device = response.optJSONObject("device")
        SpotifyPlayback(
            track = item?.let(::trackFromJson),
            isPlaying = response.optBoolean("is_playing"),
            progressMs = response.optInt("progress_ms"),
            durationMs = item?.optInt("duration_ms") ?: 0,
            volumePercent = device?.takeIf { it.has("volume_percent") && !it.isNull("volume_percent") }
                ?.optInt("volume_percent"),
            shuffleState = response.optBoolean("shuffle_state"),
            repeatState = response.optString("repeat_state", "off"),
            deviceName = device?.optString("name")
        )
    }

    suspend fun getDevices(): List<SpotifyDevice> = withContext(Dispatchers.IO) {
        val devices = apiRequest("GET", "/me/player/devices").optJSONArray("devices")
            ?: return@withContext emptyList()
        (0 until devices.length()).mapNotNull { index ->
            val device = devices.optJSONObject(index) ?: return@mapNotNull null
            SpotifyDevice(
                id = device.optString("id").takeIf { it.isNotBlank() && it != "null" },
                name = device.optString("name"),
                type = device.optString("type"),
                isActive = device.optBoolean("is_active")
            )
        }
    }

    suspend fun getDeviceByName(name: String): SpotifyDevice? {
        val matches = getDevices().filter { it.name.equals(name, ignoreCase = true) }
        if (matches.size > 1) throw IOException("More than one Spotify device is named $name.")
        return matches.singleOrNull()
    }

    suspend fun getPlaylistByName(name: String): SpotifyPlaylist? {
        val matches = getPlaylists().filter { it.name.equals(name, ignoreCase = true) }
        if (matches.size > 1) throw IOException("More than one Spotify playlist is named $name.")
        return matches.singleOrNull()
    }

    suspend fun controlPlayback(action: String) = withContext(Dispatchers.IO) {
        val path = when (action) {
            "play" -> "/me/player/play"
            "pause" -> "/me/player/pause"
            "next" -> "/me/player/next"
            "previous" -> "/me/player/previous"
            else -> throw IllegalArgumentException("Unsupported Spotify playback action.")
        }
        apiRequest(if (action == "play" || action == "pause") "PUT" else "POST", path)
    }

    suspend fun playTrack(uri: String) = withContext(Dispatchers.IO) {
        apiRequest("PUT", "/me/player/play", JSONObject().put("uris", org.json.JSONArray().put(uri)))
    }

    suspend fun playContext(uri: String) = withContext(Dispatchers.IO) {
        apiRequest("PUT", "/me/player/play", JSONObject().put("context_uri", uri))
    }

    suspend fun seekTo(positionMs: Int) = withContext(Dispatchers.IO) {
        val path = Uri.parse("$API_BASE/me/player/seek").buildUpon()
            .appendQueryParameter("position_ms", positionMs.coerceAtLeast(0).toString())
            .build().toString()
        apiRequest("PUT", path)
    }

    suspend fun setVolume(volumePercent: Int) = withContext(Dispatchers.IO) {
        val path = Uri.parse("$API_BASE/me/player/volume").buildUpon()
            .appendQueryParameter("volume_percent", volumePercent.coerceIn(0, 100).toString())
            .build().toString()
        apiRequest("PUT", path)
    }

    suspend fun setShuffle(enabled: Boolean) = withContext(Dispatchers.IO) {
        val path = Uri.parse("$API_BASE/me/player/shuffle").buildUpon()
            .appendQueryParameter("state", enabled.toString())
            .build().toString()
        apiRequest("PUT", path)
    }

    suspend fun setRepeat(mode: String) = withContext(Dispatchers.IO) {
        require(mode in setOf("off", "context", "track")) { "Repeat mode must be off, context, or track." }
        val path = Uri.parse("$API_BASE/me/player/repeat").buildUpon()
            .appendQueryParameter("state", mode)
            .build().toString()
        apiRequest("PUT", path)
    }

    suspend fun transferPlayback(deviceId: String) = withContext(Dispatchers.IO) {
        apiRequest(
            "PUT",
            "/me/player",
            JSONObject().put("device_ids", org.json.JSONArray().put(deviceId)).put("play", true)
        )
    }

    suspend fun addToQueue(uri: String) = withContext(Dispatchers.IO) {
        val path = Uri.parse("$API_BASE/me/player/queue").buildUpon()
            .appendQueryParameter("uri", uri)
            .build().toString()
        apiRequest("POST", path)
    }

    suspend fun getQueue(): SpotifyQueue = withContext(Dispatchers.IO) {
        val response = apiRequest("GET", "/me/player/queue")
        val items = response.optJSONArray("queue")
        SpotifyQueue(
            currentTrack = response.optJSONObject("currently_playing")?.let(::trackFromJson),
            tracks = if (items == null) emptyList() else
                (0 until items.length()).mapNotNull { items.optJSONObject(it)?.let(::trackFromJson) }
        )
    }

    suspend fun searchTracks(query: String): List<SpotifyTrackResult> = withContext(Dispatchers.IO) {
        val url = Uri.parse("$API_BASE/search").buildUpon()
            .appendQueryParameter("q", query)
            .appendQueryParameter("type", "track")
            .appendQueryParameter("limit", "20")
            .build().toString()
        val items = apiRequest("GET", url).optJSONObject("tracks")?.optJSONArray("items")
            ?: return@withContext emptyList()
        (0 until items.length()).mapNotNull { items.optJSONObject(it)?.let(::trackFromJson) }
    }

    suspend fun searchAlbums(query: String): List<SpotifyHistoryTrack> = withContext(Dispatchers.IO) {
        val url = Uri.parse("$API_BASE/search").buildUpon()
            .appendQueryParameter("q", query)
            .appendQueryParameter("type", "album")
            .appendQueryParameter("limit", "20")
            .build().toString()
        val items = apiRequest("GET", url).optJSONObject("albums")?.optJSONArray("items")
            ?: return@withContext emptyList()
        (0 until items.length()).mapNotNull { index ->
            val album = items.optJSONObject(index) ?: return@mapNotNull null
            val artist = album.optJSONArray("artists")?.optJSONObject(0)?.optString("name").orEmpty()
            SpotifyHistoryTrack(
                SpotifyTrackResult(album.optString("name"), artist, album.optString("uri")),
                "Album"
            )
        }
    }

    suspend fun getPlaylists(): List<SpotifyPlaylist> = withContext(Dispatchers.IO) {
        val items = apiRequest("GET", "$API_BASE/me/playlists?limit=50")
            .optJSONArray("items") ?: return@withContext emptyList()
        (0 until items.length()).mapNotNull { index ->
            val item = items.optJSONObject(index) ?: return@mapNotNull null
            val itemCount = item.optJSONObject("items")?.optInt("total")
                ?: item.optJSONObject("tracks")?.optInt("total")
                ?: 0
            SpotifyPlaylist(item.optString("id"), item.optString("name"), itemCount)
        }
    }

    suspend fun createPlaylist(name: String): SpotifyPlaylist = withContext(Dispatchers.IO) {
        val userId = apiRequest("GET", "$API_BASE/me").optString("id")
            .takeIf { it.isNotBlank() } ?: throw IOException("Spotify did not return the account ID.")
        val created = apiRequest(
            "POST",
            "$API_BASE/users/${Uri.encode(userId)}/playlists",
            JSONObject().put("name", name).put("public", false)
        )
        SpotifyPlaylist(created.optString("id"), created.optString("name"), 0)
    }

    suspend fun getPlaylistTracks(playlistId: String): List<SpotifyTrackResult> = withContext(Dispatchers.IO) {
        val items = apiRequest("GET", "$API_BASE/playlists/${Uri.encode(playlistId)}/items?limit=100")
            .optJSONArray("items") ?: return@withContext emptyList()
        (0 until items.length()).mapNotNull { index ->
            val item = items.optJSONObject(index) ?: return@mapNotNull null
            (item.optJSONObject("item") ?: item.optJSONObject("track"))?.let(::trackFromJson)
        }
    }

    suspend fun addTrackToPlaylist(playlistId: String, trackUri: String) = withContext(Dispatchers.IO) {
        apiRequest(
            "POST",
            "$API_BASE/playlists/${Uri.encode(playlistId)}/items",
            JSONObject().put("uris", org.json.JSONArray().put(trackUri))
        )
    }

    suspend fun removeTrackFromPlaylist(playlistId: String, trackUri: String) = withContext(Dispatchers.IO) {
        apiRequest(
            "DELETE",
            "$API_BASE/playlists/${Uri.encode(playlistId)}/items",
            JSONObject().put("items", org.json.JSONArray().put(JSONObject().put("uri", trackUri)))
        )
    }

    suspend fun reorderPlaylistTrack(playlistId: String, fromIndex: Int, toIndex: Int) = withContext(Dispatchers.IO) {
        apiRequest(
            "PUT",
            "$API_BASE/playlists/${Uri.encode(playlistId)}/items",
            JSONObject().put("range_start", fromIndex).put("insert_before", toIndex).put("range_length", 1)
        )
    }

    suspend fun getSavedTracks(): List<SpotifyTrackResult> = withContext(Dispatchers.IO) {
        val items = apiRequest("GET", "$API_BASE/me/tracks?limit=50")
            .optJSONArray("items") ?: return@withContext emptyList()
        (0 until items.length()).mapNotNull { items.optJSONObject(it)?.optJSONObject("track")?.let(::trackFromJson) }
    }

    suspend fun saveTrack(trackUri: String, save: Boolean) = withContext(Dispatchers.IO) {
        val id = Uri.encode(trackUri.substringAfterLast(':'))
        apiRequest(if (save) "PUT" else "DELETE", "$API_BASE/me/tracks?ids=$id")
    }

    suspend fun getSavedAlbums(): List<SpotifyHistoryTrack> = withContext(Dispatchers.IO) {
        val items = apiRequest("GET", "$API_BASE/me/albums?limit=50")
            .optJSONArray("items") ?: return@withContext emptyList()
        (0 until items.length()).mapNotNull { index ->
            val album = items.optJSONObject(index)?.optJSONObject("album") ?: return@mapNotNull null
            val artist = album.optJSONArray("artists")?.optJSONObject(0)?.optString("name").orEmpty()
            SpotifyHistoryTrack(SpotifyTrackResult(album.optString("name"), artist, album.optString("uri")), "Album")
        }
    }

    suspend fun saveAlbum(albumUri: String, save: Boolean) = withContext(Dispatchers.IO) {
        val id = Uri.encode(albumUri.substringAfterLast(':'))
        apiRequest(if (save) "PUT" else "DELETE", "$API_BASE/me/albums?ids=$id")
    }

    suspend fun getRecentlyPlayed(): List<SpotifyHistoryTrack> = withContext(Dispatchers.IO) {
        val items = apiRequest("GET", "$API_BASE/me/player/recently-played?limit=50")
            .optJSONArray("items") ?: return@withContext emptyList()
        (0 until items.length()).mapNotNull { index ->
            val item = items.optJSONObject(index) ?: return@mapNotNull null
            val track = item.optJSONObject("track")?.let(::trackFromJson) ?: return@mapNotNull null
            SpotifyHistoryTrack(track, "Played ${item.optString("played_at")}")
        }
    }

    suspend fun getTopTracks(): List<SpotifyTrackResult> = withContext(Dispatchers.IO) {
        val items = apiRequest("GET", "$API_BASE/me/top/tracks?limit=20")
            .optJSONArray("items") ?: return@withContext emptyList()
        (0 until items.length()).mapNotNull { items.optJSONObject(it)?.let(::trackFromJson) }
    }

    suspend fun getTopArtists(): List<SpotifyHistoryTrack> = withContext(Dispatchers.IO) {
        val items = apiRequest("GET", "$API_BASE/me/top/artists?limit=20")
            .optJSONArray("items") ?: return@withContext emptyList()
        (0 until items.length()).mapNotNull { index ->
            val artist = items.optJSONObject(index) ?: return@mapNotNull null
            SpotifyHistoryTrack(SpotifyTrackResult(artist.optString("name"), "", artist.optString("uri")), "Top artist")
        }
    }

    private fun getAccessToken(): String = synchronized(AUTH_LOCK) {
        if (preferences.getString("granted_scopes", null) != AUTH_SCOPES) {
            preferences.edit().clear().apply()
        }
        val savedToken = preferences.getString("access_token", null)
        val expiresAt = preferences.getLong("expires_at", 0L)
        if (!savedToken.isNullOrBlank() && expiresAt > System.currentTimeMillis() + 60_000L) {
            savedToken
        } else {
            val refreshToken = preferences.getString("refresh_token", null)
            if (refreshToken.isNullOrBlank()) {
                authorizeAndGetToken()
            } else {
                try {
                    refreshAccessToken(refreshToken)
                } catch (_: IOException) {
                    preferences.edit().clear().apply()
                    authorizeAndGetToken()
                }
            }
        }
    }

    private fun authorizeAndGetToken(): String {
        val verifier = randomUrlSafeValue(64)
        val challenge = Base64.encodeToString(
            MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(Charsets.UTF_8)),
            Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
        )
        val state = randomUrlSafeValue(32)
        val server = ServerSocket().apply {
            reuseAddress = true
            bind(InetSocketAddress(InetAddress.getByName("127.0.0.1"), CALLBACK_PORT))
            soTimeout = AUTH_TIMEOUT_MS
        }

        server.use {
            val authUri = Uri.parse(AUTHORIZATION_URL).buildUpon()
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("redirect_uri", REDIRECT_URI)
                .appendQueryParameter("scope", AUTH_SCOPES)
                .appendQueryParameter("state", state)
                .appendQueryParameter("code_challenge_method", "S256")
                .appendQueryParameter("code_challenge", challenge)
                .build()
            appContext.startActivity(
                Intent(Intent.ACTION_VIEW, authUri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )

            val callback = server.accept().use { socket ->
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val requestTarget = reader.readLine()?.split(" ")?.getOrNull(1)
                    ?: throw IOException("Spotify authorization returned an invalid response.")
                while (reader.readLine()?.isNotEmpty() == true) {
                    // Consume request headers before writing the callback response.
                }

                val callbackUri = Uri.parse("http://127.0.0.1:$CALLBACK_PORT$requestTarget")
                                val responseHtml = """
                                        <html>
                                            <body>
                                                <p>Spotify connected. Returning to Perplxd AI...</p>
                                                <a href="perplxdai://spotify-auth-complete">Return to Perplxd AI</a>
                                                <script>window.location.replace('perplxdai://spotify-auth-complete');</script>
                                            </body>
                                        </html>
                                """.trimIndent()
                val responseBytes = responseHtml.toByteArray(Charsets.UTF_8)
                socket.getOutputStream().apply {
                    write("HTTP/1.1 200 OK\r\n".toByteArray())
                    write("Content-Type: text/html; charset=utf-8\r\n".toByteArray())
                    write("Connection: close\r\n".toByteArray())
                    write("Content-Length: ${responseBytes.size}\r\n\r\n".toByteArray())
                    write(responseBytes)
                    flush()
                }

                val returnedState = callbackUri.getQueryParameter("state")
                if (returnedState != state) {
                    throw IOException("Spotify authorization state did not match.")
                }
                callbackUri.getQueryParameter("error")?.let {
                    throw IOException("Spotify authorization was declined.")
                }
                callbackUri.getQueryParameter("code")
                    ?: throw IOException("Spotify did not return an authorization code.")
            }

            return exchangeCode(callback, verifier)
        }
    }

    private fun exchangeCode(code: String, verifier: String): String {
        val response = postForm(
            TOKEN_URL,
            mapOf(
                "client_id" to CLIENT_ID,
                "grant_type" to "authorization_code",
                "code" to code,
                "redirect_uri" to REDIRECT_URI,
                "code_verifier" to verifier
            )
        )
        return saveTokenResponse(response)
    }

    private fun refreshAccessToken(refreshToken: String): String {
        val response = postForm(
            TOKEN_URL,
            mapOf(
                "client_id" to CLIENT_ID,
                "grant_type" to "refresh_token",
                "refresh_token" to refreshToken
            )
        )
        return saveTokenResponse(response)
    }

    private fun saveTokenResponse(response: JSONObject): String {
        val accessToken = response.optString("access_token")
        if (accessToken.isBlank()) {
            throw IOException("Spotify did not return an access token.")
        }
        val editor = preferences.edit()
            .putString("access_token", accessToken)
            .putString("granted_scopes", AUTH_SCOPES)
            .putLong("expires_at", System.currentTimeMillis() + response.optLong("expires_in", 3600L) * 1000L)
        response.optString("refresh_token").takeIf { it.isNotBlank() }?.let {
            editor.putString("refresh_token", it)
        }
        editor.apply()
        return accessToken
    }

    private fun searchTrack(accessToken: String, request: SpotifySongRequest): SpotifyTrackResult {
        val query = buildString {
            append("track:")
            append(request.title)
            request.artist?.let {
                append(" artist:")
                append(it)
            }
        }
        val searchUri = Uri.parse("$API_BASE/search").buildUpon()
            .appendQueryParameter("q", query)
            .appendQueryParameter("type", "track")
            .appendQueryParameter("limit", "10")
            .build()
        val response = getJson(searchUri.toString(), accessToken)
        val tracks = response.optJSONObject("tracks")?.optJSONArray("items")
            ?: throw IOException("Spotify returned no matching tracks.")
        if (tracks.length() == 0) {
            throw IOException("I couldn't find that song on Spotify.")
        }

        val candidates = (0 until tracks.length()).mapNotNull { index ->
            tracks.optJSONObject(index)?.let(::trackFromJson)
        }
        if (candidates.isEmpty()) {
            throw IOException("I couldn't find a playable version of that song.")
        }

        val normalizedTitle = normalize(request.title)
        val normalizedArtist = request.artist?.let(::normalize)
        return candidates.firstOrNull { candidate ->
            normalize(candidate.title) == normalizedTitle &&
                (normalizedArtist == null || normalize(candidate.artist).contains(normalizedArtist))
        } ?: candidates.firstOrNull { candidate ->
            normalizedArtist != null && normalize(candidate.artist).contains(normalizedArtist)
        } ?: candidates.first()
    }

    private fun openTrack(trackUri: String) {
        val trackId = trackUri.substringAfterLast(':')
        val trackUrl = "https://open.spotify.com/track/$trackId"
        val spotifyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(trackUri))
            .setPackage("com.spotify.music")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            appContext.startActivity(spotifyIntent)
        } catch (_: ActivityNotFoundException) {
            appContext.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(trackUrl)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    private fun postForm(url: String, values: Map<String, String>): JSONObject {
        val body = values.entries.joinToString("&") { (key, value) ->
            "${formEncode(key)}=${formEncode(value)}"
        }
        return requestJson(url, "POST", body, null)
    }

    private fun getJson(url: String, accessToken: String): JSONObject = requestJson(url, "GET", null, accessToken)

    private fun apiRequest(method: String, pathOrUrl: String, body: JSONObject? = null): JSONObject {
        val url = if (pathOrUrl.startsWith("https://")) pathOrUrl else "$API_BASE$pathOrUrl"
        return requestJson(url, method, body?.toString(), getAccessToken(), "application/json")
    }

    private fun trackFromJson(item: JSONObject): SpotifyTrackResult? {
        val artists = item.optJSONArray("artists") ?: return null
        val artistNames = (0 until artists.length()).mapNotNull { index ->
            artists.optJSONObject(index)?.optString("name")?.takeIf { it.isNotBlank() }
        }
        val uri = item.optString("uri")
        if (uri.isBlank() || artistNames.isEmpty() || !item.optBoolean("is_playable", true)) return null
        return SpotifyTrackResult(item.optString("name"), artistNames.joinToString(", "), uri)
    }

    private fun requestJson(
        url: String,
        method: String,
        body: String?,
        accessToken: String?,
        contentType: String = "application/x-www-form-urlencoded"
    ): JSONObject {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 15_000
            readTimeout = 15_000
            if (body != null) {
                doOutput = true
                setRequestProperty("Content-Type", contentType)
            } else if (method == "PUT" || method == "POST") {
                doOutput = true
            }
            accessToken?.let { setRequestProperty("Authorization", "Bearer " + it) }
        }

        try {
            if (body != null) {
                connection.outputStream.use { output -> output.write(body.toByteArray(Charsets.UTF_8)) }
            } else if (method == "PUT" || method == "POST") {
                connection.outputStream.use { }
            }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val content = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (status !in 200..299) {
                val detail = runCatching {
                    val json = JSONObject(content)
                    val apiError = json.optJSONObject("error")?.optString("message")
                    apiError?.takeIf { it.isNotBlank() } ?: json.optString("error_description")
                }.getOrNull()
                throw IOException(detail?.takeIf { it.isNotBlank() } ?: "Spotify request failed ($status).")
            }
            return if (content.isBlank()) JSONObject() else JSONObject(content)
        } finally {
            connection.disconnect()
        }
    }

    private fun formEncode(value: String): String = URLEncoder.encode(value, Charsets.UTF_8.name())

    private fun normalize(value: String): String = value.lowercase().replace(Regex("[^a-z0-9]"), "")

    private fun randomUrlSafeValue(size: Int): String {
        val bytes = ByteArray(size).also(SecureRandom()::nextBytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }

    companion object {
        private const val CLIENT_ID = "2e2d10d19a60407fa1206c5a67dd402a"
        private const val CALLBACK_PORT = 8888
        private const val REDIRECT_URI = "http://127.0.0.1:8888/callback"
        private const val AUTHORIZATION_URL = "https://accounts.spotify.com/authorize"
        private const val TOKEN_URL = "https://accounts.spotify.com/api/token"
        private const val API_BASE = "https://api.spotify.com/v1"
        private const val AUTH_SCOPES = "user-read-private user-read-playback-state user-modify-playback-state " +
            "user-read-currently-playing playlist-read-private playlist-read-collaborative " +
            "playlist-modify-private playlist-modify-public user-library-read user-library-modify " +
            "user-read-recently-played user-top-read"
        private const val AUTH_TIMEOUT_MS = 180_000
        private val AUTH_LOCK = Any()
    }
}