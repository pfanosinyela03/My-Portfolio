package com.perplxdai.music

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SpotifySongRequestTest {
    @Test
    fun parsesDirectPlayCommand() {
        assertEquals(
            SpotifySongRequest("Blinding Lights", "The Weeknd"),
            parseSpotifySongRequest("play Blinding Lights by The Weeknd on Spotify")
        )
    }

    @Test
    fun parsesWakeWordAndPoliteBackgroundCommand() {
        assertEquals(
            SpotifySongRequest("In My Feelings", "Drake"),
            parseSpotifySongRequest("Perplxd, can you play In My Feelings by Drake please")
        )
        assertEquals(
            SpotifySongRequest("Numb", "Linkin Park"),
            parseSpotifySongRequest("Perplexed, play Numb by Linkin Park")
        )
    }

    @Test
    fun parsesNaturalListeningRequest() {
        assertEquals(
            SpotifySongRequest("Numb", "Linkin Park"),
            parseSpotifySongRequest("I want you to listen to Numb by Linkin Park on Spotify for me")
        )
    }

    @Test
    fun prefersRecognizedSongCommandOverEarlierSpeechAlternative() {
        assertEquals(
            "Perplxd, play Numb by Linkin Park",
            selectBestRecognizedTranscript(
                listOf("Perplexed", "Perplxd, play Numb by Linkin Park")
            )
        )
    }

    @Test
    fun rejectsNonMusicConversation() {
        assertNull(parseSpotifySongRequest("I heard what you said about my schedule"))
    }
}
