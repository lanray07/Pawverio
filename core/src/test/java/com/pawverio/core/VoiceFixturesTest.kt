package com.pawverio.core

import java.util.Locale
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/** Draft text parser coverage only; these fixtures do not establish speech-engine availability. */
@RunWith(Parameterized::class)
class VoiceFixturesTest(private val localeTag: String, private val intentId: String, private val utterance: String) {
    @Test fun localizedExampleResolvesExpectedIntent() {
        val expected = when (intentId) {
            "feeding_status" -> VoiceIntent.FeedingStatus("max")
            "complete_feeding" -> VoiceIntent.CompleteFeeding("max")
            "next_vet" -> VoiceIntent.NextVet("max")
            "medications" -> VoiceIntent.Medications
            "add_grooming" -> VoiceIntent.AddGrooming
            "read_today" -> VoiceIntent.ReadToday
            "vaccinations" -> VoiceIntent.Vaccinations("max")
            "log_walk" -> VoiceIntent.LogWalk("max", 20)
            else -> error("Unknown fixture intent")
        }
        assertEquals(utterance, expected, VoiceIntentParser().parse(utterance, Locale.forLanguageTag(localeTag), listOf(Pet(id = "max", name = "Max"))))
    }
    companion object {
        @JvmStatic @Parameterized.Parameters(name = "{index}: {0}/{1}")
        fun examples(): Collection<Array<Any>> {
            val source = VoiceFixturesTest::class.java.getResourceAsStream("/voice-commands.json")!!.bufferedReader().use { it.readText() }
            val locales = JSONObject(source).getJSONObject("locales")
            return buildList {
                locales.keys().forEach { tag ->
                    val commands = locales.getJSONObject(tag).getJSONObject("examples")
                    commands.keys().forEach { id -> add(arrayOf<Any>(tag, id, commands.getString(id))) }
                }
            }
        }
    }
}
