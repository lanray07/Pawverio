package com.pawverio.core

import java.text.Normalizer
import java.util.Locale

sealed interface VoiceIntent {
    data class FeedingStatus(val petId: String) : VoiceIntent

    data class CompleteFeeding(val petId: String) : VoiceIntent

    data class NextVet(val petId: String?) : VoiceIntent

    data class Vaccinations(val petId: String?) : VoiceIntent

    data class LogWalk(val petId: String?, val minutes: Int) : VoiceIntent

    data object Medications : VoiceIntent

    data object AddGrooming : VoiceIntent

    data object ReadToday : VoiceIntent

    data object Unknown : VoiceIntent
}

/** Dictionaries are explicit per locale. Unknown languages never execute an inferred mutation. */
class VoiceIntentParser(private val dictionaries: Map<String, Dictionary> = defaults) {
    data class Dictionary(
        val feeding: List<String>,
        val complete: List<String>,
        val query: List<String>,
        val vet: List<String>,
        val vaccines: List<String>,
        val medication: List<String>,
        val grooming: List<String>,
        val walk: List<String>,
        val today: List<String>,
        val read: List<String>,
    )

    fun parse(text: String, locale: Locale, pets: List<Pet>): VoiceIntent {
        val dictionary =
            dictionaries[locale.toLanguageTag()]
                ?: dictionaries[locale.toLanguageTag().substringBefore('-')]
                ?: dictionaries[locale.language]
                ?: return VoiceIntent.Unknown
        val normal = normalize(text)
        fun has(words: List<String>) =
            words.any { phrase ->
                if (locale.language == "ja") normal.contains(normalize(phrase))
                else
                    Regex(
                            "(?<![\\p{L}\\p{N}])" +
                                Regex.escape(normalize(phrase)) +
                                "(?![\\p{L}\\p{N}])"
                        )
                        .containsMatchIn(normal)
            }
        val namedPets =
            pets.filter { pet ->
                if (locale.language == "ja") normal.contains(normalize(pet.name))
                else
                    Regex(
                            "(?<![\\p{L}\\p{N}])" +
                                Regex.escape(normalize(pet.name)) +
                                "(?![\\p{L}\\p{N}])"
                        )
                        .containsMatchIn(normal)
            }
        if (namedPets.size > 1) return VoiceIntent.Unknown
        val petId = namedPets.singleOrNull()?.id
        return when {
            has(dictionary.feeding) && has(dictionary.query) && petId != null ->
                VoiceIntent.FeedingStatus(petId)
            has(dictionary.feeding) && has(dictionary.complete) && petId != null ->
                VoiceIntent.CompleteFeeding(petId)
            has(dictionary.vet) -> VoiceIntent.NextVet(petId)
            has(dictionary.vaccines) -> VoiceIntent.Vaccinations(petId)
            has(dictionary.medication) -> VoiceIntent.Medications
            has(dictionary.grooming) && has(dictionary.complete) -> VoiceIntent.AddGrooming
            has(dictionary.walk) && has(dictionary.complete) -> {
                val digits =
                    Regex("(?<!\\d)(\\d{1,3})(?!\\d)")
                        .find(normal)
                        ?.groupValues
                        ?.get(1)
                        ?.toIntOrNull()
                val spoken =
                    if (locale.language == "en")
                        englishNumbers.entries
                            .firstOrNull { Regex("\\b${it.key}\\b").containsMatchIn(normal) }
                            ?.value
                    else null
                val minutes = digits ?: spoken
                if (minutes != null && minutes in 1..720)
                    VoiceIntent.LogWalk(petId ?: pets.singleOrNull()?.id, minutes)
                else VoiceIntent.Unknown
            }
            has(dictionary.today) && has(dictionary.read) -> VoiceIntent.ReadToday
            else -> VoiceIntent.Unknown
        }
    }

    companion object {
        private fun normalize(text: String): String =
            Normalizer.normalize(
                    text.lowercase(Locale.ROOT).replace('’', '\'').replace('‘', '\''),
                    Normalizer.Form.NFD,
                )
                .replace(Regex("\\p{M}+"), "")

        private val englishNumbers =
            mapOf(
                "twenty" to 20,
                "thirty" to 30,
                "forty" to 40,
                "fifteen" to 15,
                "ten" to 10,
                "five" to 5,
                "sixty" to 60,
            )
        val defaults =
            mapOf(
                "en" to
                    Dictionary(
                        listOf("fed", "feeding", "meal"),
                        listOf("mark", "log", "add"),
                        listOf("has", "was", "have", "is"),
                        listOf("vet", "veterinary"),
                        listOf("vaccination", "vaccinations", "vaccine"),
                        listOf("medication", "medications"),
                        listOf("grooming"),
                        listOf("walk"),
                        listOf("today", "today's"),
                        listOf("read", "show"),
                    ),
                "fr" to
                    Dictionary(
                        listOf("nourri", "nourrie", "repas"),
                        listOf("marquer", "ajouter", "enregistrer"),
                        listOf("est", "a"),
                        listOf("vétérinaire"),
                        listOf("vaccination", "vaccinations", "vaccins"),
                        listOf("médicament", "médicaments"),
                        listOf("toilettage"),
                        listOf("promenade"),
                        listOf("aujourd'hui"),
                        listOf("lire", "afficher"),
                    ),
                "es" to
                    Dictionary(
                        listOf("comido", "comida", "alimentado", "alimentada"),
                        listOf("marcar", "añadir", "registrar"),
                        listOf("ha", "está"),
                        listOf("veterinario"),
                        listOf("vacunación", "vacunas"),
                        listOf("medicación", "medicamentos"),
                        listOf("aseo"),
                        listOf("paseo"),
                        listOf("hoy"),
                        listOf("leer", "mostrar"),
                    ),
                "de" to
                    Dictionary(
                        listOf("gefüttert", "futter"),
                        listOf("markiere", "hinzufügen", "protokolliere"),
                        listOf("wurde", "ist", "hat"),
                        listOf("tierarzt"),
                        listOf("impfung", "impfungen"),
                        listOf("medikamente", "medikament"),
                        listOf("pflege"),
                        listOf("spaziergang"),
                        listOf("heute"),
                        listOf("lies", "zeige"),
                    ),
                "it" to
                    Dictionary(
                        listOf("mangiato", "pasto"),
                        listOf("segna", "aggiungi", "registra"),
                        listOf("ha", "è"),
                        listOf("veterinario"),
                        listOf("vaccini", "vaccinazioni"),
                        listOf("farmaci", "farmaco"),
                        listOf("toelettatura"),
                        listOf("passeggiata"),
                        listOf("oggi"),
                        listOf("leggi", "mostra"),
                    ),
                "pt" to
                    Dictionary(
                        listOf("comeu", "alimentado", "refeição"),
                        listOf("marcar", "adicionar", "registrar", "registar"),
                        listOf("foi", "já"),
                        listOf("veterinário"),
                        listOf("vacinas", "vacinação"),
                        listOf("medicamentos", "medicação"),
                        listOf("higiene"),
                        listOf("passeio"),
                        listOf("hoje"),
                        listOf("ler", "mostrar"),
                    ),
                "nl" to
                    Dictionary(
                        listOf("gevoerd", "eten"),
                        listOf("markeer", "voeg", "registreer"),
                        listOf("is", "heeft"),
                        listOf("dierenarts"),
                        listOf("vaccinaties", "vaccinatie"),
                        listOf("medicatie", "medicijnen"),
                        listOf("verzorging"),
                        listOf("wandeling"),
                        listOf("vandaag"),
                        listOf("lees", "toon"),
                    ),
                "pl" to
                    Dictionary(
                        listOf("nakarmiony", "jedzenie"),
                        listOf("oznacz", "dodaj", "zapisz"),
                        listOf("czy"),
                        listOf("weterynarza", "weterynarz"),
                        listOf("szczepienia", "szczepień"),
                        listOf("leki", "leków"),
                        listOf("pielęgnacji", "pielęgnacja"),
                        listOf("spacer"),
                        listOf("dzisiaj", "dzisiejsze"),
                        listOf("przeczytaj", "pokaż"),
                    ),
                "sv" to
                    Dictionary(
                        listOf("matats", "matad", "mat"),
                        listOf("markera", "lägg", "logga"),
                        listOf("har", "är"),
                        listOf("veterinär", "veterinärbesök"),
                        listOf("vaccinationer", "vaccination"),
                        listOf("mediciner", "medicin"),
                        listOf("pälsvård", "skötsel"),
                        listOf("promenad"),
                        listOf("idag", "dagens"),
                        listOf("läs", "visa"),
                    ),
                "ja" to
                    Dictionary(
                        listOf("ごはん", "餌", "食事"),
                        listOf("記録", "追加", "完了"),
                        listOf("ましたか", "ですか"),
                        listOf("獣医", "動物病院"),
                        listOf("ワクチン", "予防接種"),
                        listOf("薬", "投薬"),
                        listOf("お手入れ", "グルーミング"),
                        listOf("散歩"),
                        listOf("今日"),
                        listOf("読んで", "表示"),
                    ),
                "hi" to
                    Dictionary(
                        listOf("खाना", "भोजन"),
                        listOf("दर्ज", "जोड़ें", "चिह्नित"),
                        listOf("क्या"),
                        listOf("पशु चिकित्सक"),
                        listOf("टीकाकरण", "टीके"),
                        listOf("दवाएं", "दवा"),
                        listOf("सफाई", "ग्रूमिंग"),
                        listOf("सैर"),
                        listOf("आज"),
                        listOf("पढ़ें", "दिखाएं"),
                    ),
                "ar" to
                    Dictionary(
                        listOf("إطعام", "طعام", "أكل"),
                        listOf("سجل", "أضف", "حدد"),
                        listOf("هل"),
                        listOf("البيطري", "الطبيب البيطري"),
                        listOf("التطعيمات", "التطعيم"),
                        listOf("الأدوية", "الدواء"),
                        listOf("العناية", "التنظيف"),
                        listOf("المشي", "نزهة"),
                        listOf("اليوم"),
                        listOf("اقرأ", "اعرض"),
                    ),
                "tr" to
                    Dictionary(
                        listOf("beslendi", "mama", "yemek"),
                        listOf("işaretle", "ekle", "kaydet"),
                        listOf("mı", "mi", "mu", "mü"),
                        listOf("veteriner"),
                        listOf("aşı", "aşılar"),
                        listOf("ilaç", "ilaçlar"),
                        listOf("bakım"),
                        listOf("yürüyüş"),
                        listOf("bugün", "bugünün"),
                        listOf("oku", "göster"),
                    ),
                "id" to
                    Dictionary(
                        listOf("makan", "diberi makan"),
                        listOf("tandai", "tambahkan", "catat"),
                        listOf("apakah", "sudah"),
                        listOf("dokter hewan"),
                        listOf("vaksin", "vaksinasi"),
                        listOf("obat", "pengobatan"),
                        listOf("perawatan"),
                        listOf("jalan", "jalan-jalan"),
                        listOf("hari ini"),
                        listOf("bacakan", "tampilkan"),
                    ),
            )
    }
}
