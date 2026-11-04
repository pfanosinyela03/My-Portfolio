package com.perplxdai.phone

import android.provider.AlarmClock
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.provider.Settings
import android.app.SearchManager
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object PhoneActions {
    private const val KEEP_PACKAGE = "com.google.android.keep"
    fun run(context: Context, action: String, arguments: Map<String, String>): String {
        return when (action) {
            "open_app" -> openApp(context, required(arguments, "app"))
            "open_settings" -> openSettings(context, required(arguments, "setting"))
            "set_alarm" -> setAlarm(context, arguments)
            "set_timer" -> setTimer(context, arguments)
            "call" -> dial(context, required(arguments, "phone_number"))
            "text" -> draftText(context, arguments)
            "calendar_event" -> createCalendarEvent(context, arguments)
            "maps" -> openMap(context, required(arguments, "query"), false)
            "navigate" -> openMap(context, required(arguments, "query"), true)
            "youtube" -> openYouTube(context, required(arguments, "query"))
            "note" -> saveNote(context, arguments)
            "type_text" -> typeIntoField(context, required(arguments, "message"), arguments["replace"] == "true")
            else -> throw IllegalArgumentException("That phone action isn't supported.")
        }
    }

    private fun openApp(context: Context, requestedName: String): String {
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val matches = context.packageManager.queryIntentActivities(launcherIntent, 0)
            .mapNotNull { resolve ->
                val label = resolve.loadLabel(context.packageManager)?.toString() ?: return@mapNotNull null
                val launchIntent = context.packageManager.getLaunchIntentForPackage(resolve.activityInfo.packageName)
                    ?: return@mapNotNull null
                Triple(label, resolve.activityInfo.packageName, launchIntent)
            }
        val exact = matches.filter { it.first.equals(requestedName, ignoreCase = true) }
        val candidates = if (exact.isNotEmpty()) exact else matches.filter {
            it.first.contains(requestedName, ignoreCase = true)
        }
        if (candidates.isEmpty()) throw IllegalArgumentException("I couldn't find an app named $requestedName.")
        if (candidates.size > 1) {
            throw IllegalArgumentException(
                "I found several matching apps: ${candidates.take(5).joinToString { it.first }}. Which one?"
            )
        }
        context.startActivity(candidates.single().third.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "Opening ${candidates.single().first}."
    }

    private fun openSettings(context: Context, setting: String): String {
        val intent = when (setting.lowercase()) {
            "wifi" -> Intent(Settings.ACTION_WIFI_SETTINGS)
            "bluetooth" -> Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
            "display" -> Intent(Settings.ACTION_DISPLAY_SETTINGS)
            "sound" -> Intent(Settings.ACTION_SOUND_SETTINGS)
            "location" -> Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            "notifications" -> Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            "app" -> Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
            "battery" -> Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
            else -> throw IllegalArgumentException("I can't open that settings page.")
        }
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "Opening Android $setting settings. You can review and change them there."
    }

    private fun setAlarm(context: Context, arguments: Map<String, String>): String {
        val hour = required(arguments, "hour").toIntOrNull()
            ?: throw IllegalArgumentException("Tell me the alarm time.")
        val minute = arguments["minute"]?.toIntOrNull() ?: 0
        require(hour in 0..23 && minute in 0..59) { "That isn't a valid alarm time." }
        val label = arguments["label"].orEmpty()
        val intent = Intent(AlarmClock.ACTION_SET_ALARM)
            .putExtra(AlarmClock.EXTRA_HOUR, hour)
            .putExtra(AlarmClock.EXTRA_MINUTES, minute)
            .putExtra(AlarmClock.EXTRA_SKIP_UI, false)
        if (label.isNotBlank()) intent.putExtra(AlarmClock.EXTRA_MESSAGE, label)
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "Opening the Clock app to review the ${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} alarm."
    }

    private fun setTimer(context: Context, arguments: Map<String, String>): String {
        val seconds = required(arguments, "seconds").toLongOrNull()
            ?: throw IllegalArgumentException("Tell me how long to set the timer for.")
        require(seconds > 0) { "A timer needs to be longer than zero." }
        val intent = Intent(AlarmClock.ACTION_SET_TIMER)
            .putExtra(AlarmClock.EXTRA_LENGTH, seconds.coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
            .putExtra(AlarmClock.EXTRA_SKIP_UI, false)
        arguments["label"]?.takeIf { it.isNotBlank() }?.let { intent.putExtra(AlarmClock.EXTRA_MESSAGE, it) }
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "Opening the Clock app to review a ${seconds}-second timer."
    }

    private fun typeIntoField(context: Context, text: String, replace: Boolean): String {
        PerplxdAccessibilityService.typeText(text, replace)?.let { return it }
        context.startActivity(
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        return "Typing needs Perplxd's accessibility permission. I opened Accessibility settings; turn on Perplxd AI there, then try again."
    }

    private fun saveNote(context: Context, arguments: Map<String, String>): String {
        val text = required(arguments, "message")
        val title = arguments["title"].orEmpty()
        val send = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, text)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (title.isNotBlank()) send.putExtra(Intent.EXTRA_SUBJECT, title)
        return try {
            context.startActivity(Intent(send).setPackage(KEEP_PACKAGE))
            "Opened Google Keep with the note. Check it there; Keep saves it automatically."
        } catch (_: ActivityNotFoundException) {
            context.startActivity(
                Intent.createChooser(send, "Save note to").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            "Opened the share sheet. Pick your notes app to save the note."
        }
    }

    private fun dial(context: Context, number: String): String {
        val cleaned = number.filter { it.isDigit() || it == '+' || it == '*' || it == '#' }
        require(cleaned.count(Char::isDigit) >= 3) { "I need a valid phone number before opening the dialer." }
        context.startActivity(
            Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", cleaned, null))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        return "Opening the dialer for $cleaned. Review the number and tap Call."
    }

    private fun draftText(context: Context, arguments: Map<String, String>): String {
        val number = arguments["phone_number"].orEmpty().filter { it.isDigit() || it == '+' }
        val message = required(arguments, "message")
        val uri = if (number.isBlank()) Uri.parse("smsto:") else Uri.fromParts("smsto", number, null)
        val intent = Intent(Intent.ACTION_SENDTO, uri).putExtra("sms_body", message)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return if (number.isBlank()) {
            "Opening a text draft. Choose the recipient and review it before sending."
        } else {
            "Opening a text draft to $number. Review it and tap Send."
        }
    }

    private fun createCalendarEvent(context: Context, arguments: Map<String, String>): String {
        val title = required(arguments, "title")
        val start = parseEventTime(required(arguments, "start_time"))
        val end = arguments["end_time"]?.takeIf { it.isNotBlank() }?.let(::parseEventTime)
            ?: start.plusHours(1)
        require(end.isAfter(start)) { "The event end time must be after its start time." }
        val intent = Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, title)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        arguments["description"]?.takeIf { it.isNotBlank() }?.let {
            intent.putExtra(CalendarContract.Events.DESCRIPTION, it)
        }
        context.startActivity(intent)
        return "Opening the calendar to review “$title”. Save it there when you're ready."
    }

    private fun openMap(context: Context, query: String, navigate: Boolean): String {
        val encoded = Uri.encode(query)
        val uri = if (navigate) {
            Uri.parse("google.navigation:q=$encoded")
        } else {
            Uri.parse("geo:0,0?q=$encoded")
        }
        val intent = Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (navigate) intent.setPackage("com.google.android.apps.maps")
        try {
            context.startActivity(intent)
        } catch (error: ActivityNotFoundException) {
            if (navigate) {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$encoded"))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } else {
                throw IllegalArgumentException("No maps app is available on this phone.", error)
            }
        }
        return if (navigate) "Opening navigation to $query." else "Searching maps for $query."
    }

    private fun openYouTube(context: Context, query: String): String {
        val youtubeSearch = Intent(Intent.ACTION_SEARCH)
            .setPackage("com.google.android.youtube")
            .putExtra(SearchManager.QUERY, query)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(youtubeSearch)
        } catch (_: ActivityNotFoundException) {
            val url = Uri.parse("https://www.youtube.com/results")
                .buildUpon().appendQueryParameter("search_query", query).build()
            context.startActivity(Intent(Intent.ACTION_VIEW, url).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        return "Opening YouTube results for $query. Choose a video to play."
    }

    private fun parseEventTime(value: String): LocalDateTime =
        try {
            LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (error: DateTimeParseException) {
            throw IllegalArgumentException(
                "Use a calendar date and time in YYYY-MM-DDTHH:MM format.",
                error
            )
        }

    private fun required(arguments: Map<String, String>, name: String): String =
        arguments[name]?.trim()?.takeIf { it.isNotEmpty() }
            ?: throw IllegalArgumentException("I need the $name to do that.")
}
