package com.perplxdai.music

data class SpotifySongRequest(val title: String, val artist: String?)

fun parseSpotifySongRequest(command: String): SpotifySongRequest? {
    var cleaned = command.trim()
    if (cleaned.isBlank()) return null

    val prefixes = listOf(
        Regex("(?i)^(?:(?:hey|okay|ok)[,\\s]+)?(?:perplxd|perplxed|perplexed|perplexd)[,\\s:.-]+"),
        Regex("(?i)^(?:please\\s+)?(?:can|could|would|will)\\s+you\\s+"),
        Regex("(?i)^(?:please\\s+)?(?:i\\s+(?:want|wanna|need|would\\s+like)\\s+you\\s+to|i\\s+(?:want|wanna|need)\\s+to|i['’]d\\s+like\\s+you\\s+to)\\s+"),
        Regex("(?i)^(?:please\\s+)?(?:hey\\s+)?")
    )
    for (prefix in prefixes) {
        cleaned = prefix.replaceFirst(cleaned, "")
    }

    val commandPrefix = Regex(
        "(?i)^(?:please\\s+)?(?:start\\s+playing|play|put\\s+on|listen\\s+to|queue)\\s+(?:me\\s+)?"
    )
    if (!commandPrefix.containsMatchIn(cleaned)) return null
    cleaned = commandPrefix.replaceFirst(cleaned, "")

    cleaned = cleaned
        .replace(
            Regex("(?i)\\s+(?:(?:on|in|using)\\s+)?spotify(?:\\s+(?:for\\s+me|please|now))?\\s*$"),
            ""
        )
        .replace(Regex("(?i)\\s+(?:for\\s+me|please|now)\\s*$"), "")
        .trim()
    if (cleaned.isBlank()) return null

    val parts = cleaned.split(Regex("(?i)\\s+by\\s+"), limit = 2)
    val title = parts.first().trim().removeSurrounding("\"", "\"").removeSurrounding("'", "'")
    val artist = parts.getOrNull(1)?.trim()?.takeIf { it.isNotBlank() }
    return title.takeIf { it.isNotBlank() }?.let { SpotifySongRequest(it, artist) }
}

fun selectBestRecognizedTranscript(candidates: List<String>): String? =
    candidates.firstOrNull { parseSpotifySongRequest(it) != null }
        ?: candidates.firstOrNull()
