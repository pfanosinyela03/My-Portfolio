# Perplxd AI First Test Checklist

Use this checklist before running the app on an Android emulator or real device.

## 1. Android Studio setup
- Install Android Studio
- Install JDK 17
- In Android Studio, set Gradle JDK to Java 17
- In Project Structure, set SDK Location to your Android SDK folder
- Install Android SDK Platform 34 and required build tools
- Sync the project with Gradle files

## 2. App permissions
- The app requests RECORD_AUDIO permission for microphone access
- The app requests notification permission so Android can show its ongoing listening notification
- Audio streams to OpenAI Realtime continuously while Listen is on; stop it to end streaming
- Contacts access is requested only when you ask Perplxd to draft a WhatsApp message
- The app needs network access to search Spotify
- Allow microphone and notification access on the emulator or device when enabling Hands-free

## 3. Spotify setup
- In the Spotify Developer Dashboard, add `http://127.0.0.1:8888/callback` as a redirect URI
- The redirect URI must match the app exactly
- Sign in to Spotify Premium on the test account; development-mode apps require the app owner to have Premium
- If the app is in development mode, add the test account to its user allowlist
- Keep Spotify installed and open on a device for playback controls
- Start hands-free voice mode and authorize Spotify when asked, approving playback, playlist, library, and history access
- If Spotify was connected before these permissions were requested, tap Reconnect Spotify and approve the updated access

## 3a. Spotify voice control
- Ask Perplxd to pause, resume, skip, seek, set volume, toggle shuffle/repeat, or report the currently playing track
- Ask it to list devices and switch playback to a Spotify Connect device
- Ask it to queue a track, list/create a playlist, play a playlist, and add/remove/reorder playlist tracks
- Ask it to save/remove tracks and albums in the Spotify library
- Ask it to report saved tracks/albums, recently played tracks, or top tracks/artists
- Playback control requires Spotify Premium and an active Spotify Connect device

## 3b. WhatsApp messaging
- Say "Text John on WhatsApp and say hi"
- Grant Contacts access when prompted
- If multiple contacts match, choose the correct person
- Confirm WhatsApp opens with the recipient and message prefilled
- Review and tap Send in WhatsApp; Perplxd never silently sends the message

## 4. Voice command test
Try these phrases:
- "Ayo Perplexed, what can you do?"
- "Hey Perplexed, open Bluetooth settings"
- "Hey Perplexed, set a timer for five minutes"
- "Ayo Perplexed, search YouTube for [video topic]"
- "play In My Feelings by Drake on Spotify"
- "play Blinding Lights by The Weeknd"
- "play [song title] by [artist]"

Expected behavior:
- The app opens its own animated "PERPLXD LISTENING" panel; no Google listening popup appears
- When Listen is enabled, the foreground notification says Perplxd is listening
- Speak a command directly; no wake phrase is needed
- Perplxd identifies itself as Perplxd, created by Pfano Sinyela
- Phone actions open the relevant Android app for user review; calls/messages are not sent automatically
- YouTube requests open search results; the user selects the video to play
- "Perplxd, can you play [song] by [artist]" and other polite versions are recognized as Spotify requests
- The app displays recognized words and keeps listening after speaking a reply
- Listening continues after switching away from the app, with a persistent notification and a Stop listening action
- When music plays, Perplxd keeps listening so it can hear a playback-control request
- The saved hands-free setting resumes listening the next time the app is opened
- On first Spotify use, authorization opens in the browser; approve access and return to the app
- The assistant searches Spotify for the requested title and artist
- Spotify opens the matching track, not search results
- The assistant speaks the response using the phone's built-in voice and updates the message feed

## 5. Manual app run
- Start an emulator or connect a device
- Click Run in Android Studio
- Confirm the app launches without crashing
- Tap Listen, grant microphone and notification permissions, then speak a command
- Verify Spotify opens
- Switch to another app and confirm the listening notification remains visible; stop listening from the notification
- Play Spotify music and say a command such as "pause Spotify"

Note: voice cloning is not set up yet. It requires a recording of your voice and a chosen speech-generation provider.

## 6. Troubleshooting
If the app does not launch:
- check SDK path
- sync Gradle again
- ensure Java 17 is selected
- restart Android Studio and reopen the project

## 7. First test success target
A first successful test is:
- app opens
- microphone permission is granted
- hands-free service starts and shows the custom listening panel
- command is recognized
- Spotify opens the exact track; check whether it starts playback
- assistant shows and speaks the command response
