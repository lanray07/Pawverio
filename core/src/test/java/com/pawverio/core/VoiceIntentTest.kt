package com.pawverio.core

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class VoiceIntentTest {
    private val parser = VoiceIntentParser()
    private val pets = listOf(Pet(id = "bella", name = "Bella"), Pet(id = "max", name = "Max"))
    @Test fun feedingQueryIsReadOnly() { assertEquals(VoiceIntent.FeedingStatus("bella"), parser.parse("Has Bella been fed today?", Locale.UK, pets)) }
    @Test fun feedingActionRequiresPet() { assertEquals(VoiceIntent.Unknown, parser.parse("Mark as fed", Locale.UK, pets)) }
    @Test fun feedingActionResolvesPet() { assertEquals(VoiceIntent.CompleteFeeding("max"), parser.parse("Mark Max as fed.", Locale.US, pets)) }
    @Test fun spokenWalkDuration() { assertEquals(VoiceIntent.LogWalk(null, 20), parser.parse("Log a twenty-minute walk", Locale.UK, pets)) }
    @Test fun frenchMedicationQuery() { assertEquals(VoiceIntent.Medications, parser.parse("Afficher les médicaments", Locale.FRANCE, pets)) }
    @Test fun ambiguousPetDoesNotMutate() { assertEquals(VoiceIntent.Unknown, parser.parse("Mark Max and Bella as fed", Locale.UK, pets)) }
    @Test fun unknownLanguageDoesNotExecuteEnglish() { assertEquals(VoiceIntent.Unknown, parser.parse("Mark Max as fed", Locale.JAPANESE, pets)) }
    @Test fun petNamesMatchWholeWords() { assertEquals(VoiceIntent.Unknown, parser.parse("Mark Maxwell as fed", Locale.UK, pets)) }
    @Test fun eachRequestedLocaleHasMedicationVocabulary() {
        val samples = mapOf("en-GB" to "Show medication reminders", "en-US" to "Show medication reminders", "en-AU" to "Show medication reminders", "es-ES" to "Mostrar medicamentos", "es-419" to "Mostrar medicamentos", "fr-FR" to "Afficher les médicaments", "fr-CA" to "Afficher les médicaments", "de-DE" to "Zeige Medikamente", "it-IT" to "Mostra farmaci", "pt-BR" to "Mostrar medicamentos", "pt-PT" to "Mostrar medicamentos", "nl-NL" to "Toon medicijnen", "pl-PL" to "Pokaż leki", "sv-SE" to "Visa mediciner", "ja-JP" to "薬を表示", "hi-IN" to "दवा दिखाएं", "ar" to "اعرض الأدوية", "tr-TR" to "İlaç göster", "id-ID" to "Tampilkan obat")
        samples.forEach { (locale, input) -> assertEquals(locale, VoiceIntent.Medications, parser.parse(input, Locale.forLanguageTag(locale), pets)) }
    }
    @Test fun japanesePetNameCanBeFollowedByParticle() { assertEquals(VoiceIntent.CompleteFeeding("luna"), parser.parse("ルナの食事を記録", Locale.JAPAN, listOf(Pet(id = "luna", name = "ルナ")))) }
    @Test fun frenchSmartApostropheAndFeminineForms() {
        assertEquals(VoiceIntent.ReadToday, parser.parse("Lire les tâches d’aujourd’hui", Locale.FRANCE, pets))
        assertEquals(VoiceIntent.FeedingStatus("bella"), parser.parse("Bella a été nourrie aujourd’hui ?", Locale.FRANCE, pets))
    }
    @Test fun spanishFeminineForms() { assertEquals(VoiceIntent.CompleteFeeding("bella"), parser.parse("Marcar Bella como alimentada", Locale.forLanguageTag("es-ES"), pets)) }
}
