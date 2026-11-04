package com.perplxdai.voice

import android.content.Context
import org.json.JSONArray

/** Facts the user asked Perplxd to remember, stored only in this app's private storage on the phone. */
class MemoryStore(context: Context) {
    private val preferences = context.applicationContext
        .getSharedPreferences("perplxd_memory", Context.MODE_PRIVATE)

    @Synchronized
    fun all(): List<String> {
        val array = JSONArray(preferences.getString(KEY, "[]"))
        return (0 until array.length()).map { array.getString(it) }
    }

    @Synchronized
    fun remember(fact: String): String {
        val clean = fact.trim().take(MAX_FACT_LENGTH)
        require(clean.isNotEmpty()) { "Tell me what to remember." }
        val facts = all()
        if (facts.any { it.equals(clean, ignoreCase = true) }) return "I already remember that."
        require(facts.size < MAX_FACTS) { "My memory is full. Ask me to forget something first." }
        save(facts + clean)
        return "Got it, I'll remember that."
    }

    @Synchronized
    fun forget(query: String): String {
        val needle = query.trim()
        require(needle.isNotEmpty()) { "Tell me what to forget." }
        val facts = all()
        val remaining = facts.filterNot { it.contains(needle, ignoreCase = true) }
        val removed = facts.size - remaining.size
        if (removed == 0) return "I don't have anything remembered about \"$needle\"."
        save(remaining)
        return "Forgot $removed ${if (removed == 1) "memory" else "memories"}."
    }

    @Synchronized
    fun clear(): String {
        save(emptyList())
        return "I've forgotten everything you asked me to remember."
    }

    fun promptSection(): String {
        val facts = all()
        if (facts.isEmpty()) return ""
        return "Things the user asked you to remember (treat as true unless they correct you): " +
            facts.joinToString(separator = "; ") + ". "
    }

    private fun save(facts: List<String>) {
        preferences.edit().putString(KEY, JSONArray(facts).toString()).apply()
    }

    private companion object {
        const val KEY = "facts"
        const val MAX_FACTS = 100
        const val MAX_FACT_LENGTH = 300
    }
}
