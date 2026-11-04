package com.perplxdai.phone

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class PerplxdAccessibilityService : AccessibilityService() {
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        if (instance === this) instance = null
        super.onDestroy()
    }

    private fun insertText(text: String, replace: Boolean): String {
        val root = rootInActiveWindow
            ?: return "I can't see the current screen. Open the chat and tap its text box first."
        val field = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
            ?.takeIf { it.isEditable }
            ?: return "No text box is selected. Tap the message box first, then dictate again."
        if (field.isPassword) return "I won't type into a password field."

        val existing = if (field.isShowingHintText) "" else field.text?.toString().orEmpty()
        val separator = if (replace || existing.isEmpty() || existing.last().isWhitespace()) "" else " "
        val updated = if (replace) text else existing + separator + text
        val args = Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, updated)
        }
        return if (field.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)) {
            "Typed it into the text box. I did not send it; the user should review and send."
        } else {
            "That text box didn't accept typed text."
        }
    }

    companion object {
        @Volatile private var instance: PerplxdAccessibilityService? = null

        fun isEnabled(): Boolean = instance != null

        fun typeText(text: String, replace: Boolean): String? = instance?.insertText(text, replace)
    }
}
