package com.perplxdai.messaging

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WhatsAppMessageRequestTest {
    @Test
    fun parsesRequestedVoiceCommand() {
        assertEquals(
            WhatsAppMessageRequest("John", "hi"),
            parseWhatsAppMessageRequest("text John on WhatsApp and say hi")
        )
    }

    @Test
    fun parsesWakeWordAndQuotedMessage() {
        assertEquals(
            WhatsAppMessageRequest("Alex Smith", "I'm running late"),
            parseWhatsAppMessageRequest(
                "Perplxd, message Alex Smith on WhatsApp saying \"I'm running late\""
            )
        )
    }

    @Test
    fun parsesMessageWithoutAndBeforeSay() {
        assertEquals(
            WhatsAppMessageRequest("Mom", "Call me when you get home"),
            parseWhatsAppMessageRequest(
                "send a WhatsApp message to Mom with the message Call me when you get home"
            )
        )
    }

    @Test
    fun rejectsIncompleteOrUnrelatedCommands() {
        assertNull(parseWhatsAppMessageRequest("text John on WhatsApp"))
        assertNull(parseWhatsAppMessageRequest("play a song on Spotify"))
    }
}
