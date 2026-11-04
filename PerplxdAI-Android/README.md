# Perplxd AI

A Jarvis-inspired Android app shell built with Jetpack Compose.

## Structure

- app/
  - src/main/java/com/perplxdai/MainActivity.kt
  - src/main/res/values/... 

## Notes

This is a starting foundation for a futuristic personal AI assistant.
It includes a dark dashboard UI and an app shell ready for:
- voice input
- AI chat
- task automation
- command panel
- assistant status cards

The dark Perplxd artwork is used for both the launcher icon and the full-screen splash background.

Perplxd is the assistant's fixed name; it was created by Pfano Sinyela. These identity details are included in the instructions for every voice session.

Hands-free voice input runs through an in-app listening panel and an Android microphone foreground service. Grant microphone and notification access, then tap **Listen** to enable it. While enabled, the microphone streams to OpenAI Realtime continuously; there is no wake word. The service can listen while the app is in the background and resumes on later app launches while enabled. Stop it from the panel or persistent notification. Android displays a microphone notification while listening, and the service does not restart itself after a device reboot. Online voice sessions use battery and mobile data. Spoken replies currently use the realtime voice; voice cloning is not configured and needs a voice sample and a selected provider.

Say, for example, "Text John on WhatsApp and say hi." Perplxd asks for Contacts access when needed, lets you choose if more than one contact matches, then opens a prefilled WhatsApp draft. Review and tap Send in WhatsApp; Perplxd does not send messages silently.

Open the project in Android Studio and sync Gradle to begin building the rest of the app.

## Spotify

Ask Perplxd by voice to control Spotify playback, Spotify Connect devices, queues, playlists, saved tracks and albums, and listening history. It can play/pause, skip, seek, change volume, toggle shuffle/repeat, switch devices, inspect/add to the queue, create and edit playlists, manage saved music, and report recently played and top tracks/artists. For example, ask it to "play Blinding Lights by The Weeknd", "pause Spotify", "turn shuffle on", "play my workout playlist", or "save this song".

The integration uses Spotify Authorization Code with PKCE, so no client secret is stored in the Android app. It requests playback, playlist, library, and listening-history permissions when you authorize. If you previously connected before these permissions were added, ask Perplxd to reconnect Spotify and approve the updated access.

Add this exact redirect URI in the Spotify Developer Dashboard:

```text
http://127.0.0.1:8888/callback
```

The user must authorize Perplxd AI before asking it to use Spotify. Playback control requires Spotify Premium and an active Spotify Connect device; keep Spotify open on the phone or another device. Spotify development-mode apps have account and allowlist restrictions, and their app owner must have Premium. Spotify controls and available history may also vary by account, market, and Spotify API access. If access is missing, ask Perplxd to reconnect Spotify and approve the requested permissions. See `FIRST_TEST_CHECKLIST.md` for the test steps.

## Phone actions

Ask Perplxd to open an installed app or supported Android settings page; prepare an alarm, timer, call, or SMS draft; create a calendar event; search maps or navigate; or search YouTube for a requested video. For example: "Open Bluetooth settings", "set an alarm for 7:30", "text this number saying I'll be late", "navigate to the airport", or "find the latest video about space on YouTube". Calls, texts, alarms, timers, and calendar events open the relevant Android app for user review; they are not silently confirmed. YouTube opens search results and the user chooses a video. Android does not let a regular app perform every action in every other app.

## Live voice (OpenAI Realtime)

Add `OPENAI_API_KEY=...` to `local.properties` (git-ignored) and rebuild. After OpenWakeWord hears a wake phrase, the assistant streams speech to gpt-realtime and can search the web, control Spotify, and draft WhatsApp messages. The key is embedded in the APK, so use this only for personal builds.
