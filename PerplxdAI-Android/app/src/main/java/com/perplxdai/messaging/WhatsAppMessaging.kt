package com.perplxdai.messaging

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract

data class WhatsAppMessageRequest(
    val recipientName: String,
    val message: String
)

data class WhatsAppContact(
    val name: String,
    val phoneNumber: String
)

fun parseWhatsAppMessageRequest(command: String): WhatsAppMessageRequest? {
    val text = command.trim()
    val wakeWord = """(?:(?:(?:hey|okay|ok)\s+)?(?:perplxd|perplxed|perplexed|perplexd)[,:\s.-]+)?"""
    val standardCommand = Regex(
        pattern = """(?i)^$wakeWord\s*(?:please\s+)?(?:text|message|send\s+(?:a\s+)?(?:whatsapp\s+)?message\s+to)\s+(.+?)\s+(?:on\s+)?whats?app\s*(?:(?:and\s+)?(?:say|saying|with\s+(?:the\s+)?message)\s+|:\s*)(.+?)\s*$"""
    )
    val sendMessageCommand = Regex(
        pattern = """(?i)^$wakeWord\s*(?:please\s+)?send\s+(?:a\s+)?(?:whatsapp\s+)?message\s+to\s+(.+?)\s+(?:saying|with\s+(?:the\s+)?message|:)\s*(.+?)\s*$"""
    )
    val match = standardCommand.matchEntire(text) ?: sendMessageCommand.matchEntire(text) ?: return null

    val recipient = match.groupValues[1].trim()
        .removeSuffix(",")
        .removeSuffix(" please")
        .trim()
    val message = match.groupValues[2].trim()
        .removeSurrounding("\"", "\"")
        .removeSurrounding("'", "'")

    if (recipient.isBlank() || message.isBlank()) return null
    return WhatsAppMessageRequest(recipient, message)
}

fun findWhatsAppContacts(context: Context, query: String): List<WhatsAppContact> {
    val uri = Uri.withAppendedPath(
        ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
        Uri.encode(query)
    )
    val projection = arrayOf(
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )
    val contacts = mutableListOf<WhatsAppContact>()
    context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
        val nameColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val phoneColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
        while (cursor.moveToNext()) {
            val name = cursor.getString(nameColumn)?.trim().orEmpty()
            val phone = cursor.getString(phoneColumn)?.trim().orEmpty()
            if (name.isNotBlank() && phone.isNotBlank()) {
                contacts += WhatsAppContact(name, phone)
            }
        }
    }

    val normalizedQuery = normalizeContactName(query)
    return contacts.distinctBy { normalizePhoneNumber(it.phoneNumber) }
        .sortedBy {
            val normalizedName = normalizeContactName(it.name)
            when {
                normalizedName == normalizedQuery -> 0
                normalizedName.startsWith(normalizedQuery) -> 1
                else -> 2
            }
        }
        .take(MAX_CONTACT_CHOICES)
}

fun openWhatsAppDraft(context: Context, contact: WhatsAppContact, message: String): Boolean {
    val phone = normalizePhoneNumber(contact.phoneNumber)
    if (phone.length < MIN_PHONE_DIGITS || phone.startsWith("0")) return false

    val uri = Uri.parse("https://wa.me/$phone")
        .buildUpon()
        .appendQueryParameter("text", message)
        .build()
    val whatsappPackages = listOf("com.whatsapp", "com.whatsapp.w4b")
    for (packageName in whatsappPackages) {
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, uri)
                    .setPackage(packageName)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            return true
        } catch (_: ActivityNotFoundException) {
            continue
        }
    }

    return try {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        true
    } catch (_: ActivityNotFoundException) {
        false
    }
}

private fun normalizeContactName(value: String): String =
    value.lowercase().filter(Char::isLetterOrDigit)

private fun normalizePhoneNumber(value: String): String =
    value.filter(Char::isDigit)

private const val MIN_PHONE_DIGITS = 8
private const val MAX_CONTACT_CHOICES = 5
