package com.pawverio.core

import org.json.JSONArray
import org.json.JSONObject

data class BackupSnapshot(
    val pets: List<Pet>,
    val tasks: List<CareTask>,
    val events: List<CareEvent>,
    val records: List<HealthRecord>,
    val weights: List<WeightEntry>,
    val members: List<HouseholdMember>,
)

data class RestoreResult(
    val pets: Int,
    val tasks: Int,
    val events: Int,
    val records: Int,
    val weights: Int,
    val members: Int,
)

/** Portable versioned data only; filesystem references are deliberately never imported. */
object BackupCodec {
    private const val MAX_TEXT = 10 * 1024 * 1024
    private const val MAX_ROWS = 20_000

    suspend fun exportJson(repository: CareRepository): String = encode(repository.snapshot())

    suspend fun importJson(repository: CareRepository, text: String): RestoreResult {
        val snapshot = decode(text)
        repository.restore(snapshot)
        return RestoreResult(
            snapshot.pets.size,
            snapshot.tasks.size,
            snapshot.events.size,
            snapshot.records.size,
            snapshot.weights.size,
            snapshot.members.size,
        )
    }

    fun encode(source: BackupSnapshot): String {
        val snapshot =
            source.copy(
                pets = source.pets.map { it.copy(photoPath = null) },
                records = source.records.map { it.copy(attachmentPath = null) },
            )
        validate(snapshot)
        fun row(vararg fields: Pair<String, Any?>) =
            JSONObject().apply {
                fields.forEach { (key, value) -> put(key, value ?: JSONObject.NULL) }
            }
        fun rows(items: List<JSONObject>) = JSONArray().apply { items.forEach { put(it) } }
        return row(
                "format" to "pawverio",
                "version" to 1,
                "pets" to
                    rows(
                        snapshot.pets.map {
                            row(
                                "id" to it.id,
                                "name" to it.name,
                                "species" to it.species,
                                "breed" to it.breed,
                                "birthDate" to it.birthDate,
                                "approximateAge" to it.approximateAge,
                                "weightKg" to it.weightKg,
                                "microchip" to it.microchip,
                                "allergies" to it.allergies,
                                "notes" to it.notes,
                                "createdAt" to it.createdAt,
                            )
                        }
                    ),
                "tasks" to
                    rows(
                        snapshot.tasks.map {
                            row(
                                "id" to it.id,
                                "petId" to it.petId,
                                "kind" to it.kind,
                                "title" to it.title,
                                "time" to it.time,
                                "recurrenceDays" to it.recurrenceDays,
                                "notes" to it.notes,
                                "assignedTo" to it.assignedTo,
                                "paused" to it.paused,
                                "durationMinutes" to it.durationMinutes,
                                "distanceKm" to it.distanceKm,
                                "dosage" to it.dosage,
                                "startDate" to it.startDate,
                                "endDate" to it.endDate,
                                "portion" to it.portion,
                                "mealType" to it.mealType,
                                "remindersEnabled" to it.remindersEnabled,
                                "snoozedUntil" to it.snoozedUntil,
                            )
                        }
                    ),
                "events" to
                    rows(
                        snapshot.events.map {
                            row(
                                "id" to it.id,
                                "taskId" to it.taskId,
                                "petId" to it.petId,
                                "kind" to it.kind,
                                "title" to it.title,
                                "completedAt" to it.completedAt,
                                "scheduledAt" to it.scheduledAt,
                                "notes" to it.notes,
                                "assignedTo" to it.assignedTo,
                                "durationMinutes" to it.durationMinutes,
                                "distanceKm" to it.distanceKm,
                            )
                        }
                    ),
                "records" to
                    rows(
                        snapshot.records.map {
                            row(
                                "id" to it.id,
                                "petId" to it.petId,
                                "kind" to it.kind,
                                "title" to it.title,
                                "date" to it.date,
                                "nextDueDate" to it.nextDueDate,
                                "provider" to it.provider,
                                "contact" to it.contact,
                                "notes" to it.notes,
                            )
                        }
                    ),
                "weights" to
                    rows(
                        snapshot.weights.map {
                            row(
                                "id" to it.id,
                                "petId" to it.petId,
                                "weightKg" to it.weightKg,
                                "date" to it.date,
                                "measurements" to it.measurements,
                                "notes" to it.notes,
                            )
                        }
                    ),
                "members" to
                    rows(
                        snapshot.members.map {
                            row(
                                "id" to it.id,
                                "name" to it.name,
                                "contact" to it.contact,
                                "notes" to it.notes,
                            )
                        }
                    ),
            )
            .toString()
            .also {
                require(it.length <= MAX_TEXT && it.toByteArray(Charsets.UTF_8).size <= MAX_TEXT) {
                    "Backup is too large"
                }
            }
    }

    fun decode(text: String): BackupSnapshot {
        require(text.length <= MAX_TEXT) { "Backup is too large" }
        require(text.toByteArray(Charsets.UTF_8).size <= MAX_TEXT) { "Backup is too large" }
        var depth = 0
        var quoted = false
        var escaped = false
        text.forEach { character ->
            if (quoted) {
                if (escaped) escaped = false
                else if (character == '\\') escaped = true else if (character == '"') quoted = false
            } else
                when (character) {
                    '"' -> quoted = true
                    '{',
                    '[' -> {
                        depth++
                        require(depth <= 4) { "Backup nesting is too deep" }
                    }
                    '}',
                    ']' -> depth--
                }
        }
        val root = JSONObject(text)
        val version = root.get("version")
        require(
            root.getString("format") == "pawverio" && version is Number && version.toDouble() == 1.0
        ) {
            "Unsupported backup format"
        }
        var total = 0
        fun <T> read(name: String, transform: (JSONObject) -> T): List<T> {
            val array = root.getJSONArray(name)
            total += array.length()
            require(total <= MAX_ROWS) { "Too many backup records" }
            return (0 until array.length()).map { index ->
                val obj = array.getJSONObject(index)
                obj.keys().forEach { key ->
                    val value = obj.get(key)
                    require(value !is JSONObject && value !is JSONArray) {
                        "Nested record values are not permitted"
                    }
                    require(value !is String || value.length <= 100_000) {
                        "Record field is too large"
                    }
                }
                transform(obj)
            }
        }
        fun JSONObject.text(name: String, default: String = ""): String {
            if (!has(name)) return default
            val value = get(name)
            require(value is String) { "Invalid text field: $name" }
            return value
        }
        fun JSONObject.id(name: String = "id") =
            text(name).also {
                require(it.isNotBlank() && it.length <= 200) { "Invalid identifier" }
            }
        fun JSONObject.long(name: String): Long {
            val value = get(name)
            require(
                value is Number &&
                    value.toDouble().isFinite() &&
                    value.toDouble() == value.toLong().toDouble()
            ) {
                "Invalid integer: $name"
            }
            return value.toLong()
        }
        fun JSONObject.nullLong(name: String): Long? =
            if (!has(name) || isNull(name)) null else long(name)
        fun JSONObject.number(name: String): Double {
            val value = get(name)
            require(value is Number && value.toDouble().isFinite()) { "Invalid number: $name" }
            return value.toDouble()
        }
        fun JSONObject.nullNumber(name: String): Double? =
            if (!has(name) || isNull(name)) null else number(name)
        fun JSONObject.int(name: String): Int =
            long(name).also { require(it in Int.MIN_VALUE..Int.MAX_VALUE) }.toInt()
        fun JSONObject.bool(name: String, default: Boolean = false): Boolean {
            if (!has(name)) return default
            val value = get(name)
            require(value is Boolean) { "Invalid boolean: $name" }
            return value
        }
        val pets =
            read("pets") { o ->
                Pet(
                    id = o.id(),
                    name = o.text("name"),
                    species = o.text("species", "dog"),
                    breed = o.text("breed"),
                    birthDate = o.nullLong("birthDate"),
                    approximateAge = o.text("approximateAge"),
                    weightKg = o.nullNumber("weightKg"),
                    microchip = o.text("microchip"),
                    allergies = o.text("allergies"),
                    notes = o.text("notes"),
                    createdAt = o.long("createdAt"),
                )
            }
        val tasks =
            read("tasks") { o ->
                CareTask(
                    id = o.id(),
                    petId = o.id("petId"),
                    kind = o.text("kind"),
                    title = o.text("title"),
                    time = o.long("time"),
                    recurrenceDays = o.int("recurrenceDays"),
                    notes = o.text("notes"),
                    assignedTo = o.text("assignedTo"),
                    paused = o.bool("paused"),
                    durationMinutes =
                        o.nullLong("durationMinutes")
                            ?.also { require(it in 1..Int.MAX_VALUE) }
                            ?.toInt(),
                    distanceKm = o.nullNumber("distanceKm"),
                    dosage = o.text("dosage"),
                    startDate = o.nullLong("startDate"),
                    endDate = o.nullLong("endDate"),
                    portion = o.text("portion"),
                    mealType = o.text("mealType"),
                    remindersEnabled = o.bool("remindersEnabled", true),
                    snoozedUntil = o.nullLong("snoozedUntil"),
                )
            }
        val events =
            read("events") { o ->
                CareEvent(
                    id = o.id(),
                    taskId = if (!o.has("taskId") || o.isNull("taskId")) null else o.id("taskId"),
                    petId = o.id("petId"),
                    kind = o.text("kind"),
                    title = o.text("title"),
                    completedAt = o.long("completedAt"),
                    scheduledAt = o.nullLong("scheduledAt"),
                    notes = o.text("notes"),
                    assignedTo = o.text("assignedTo"),
                    durationMinutes =
                        o.nullLong("durationMinutes")
                            ?.also { require(it in 1..Int.MAX_VALUE) }
                            ?.toInt(),
                    distanceKm = o.nullNumber("distanceKm"),
                )
            }
        val records =
            read("records") { o ->
                HealthRecord(
                    id = o.id(),
                    petId = o.id("petId"),
                    kind = o.text("kind"),
                    title = o.text("title"),
                    date = o.long("date"),
                    nextDueDate = o.nullLong("nextDueDate"),
                    provider = o.text("provider"),
                    contact = o.text("contact"),
                    notes = o.text("notes"),
                )
            }
        val weights =
            read("weights") { o ->
                WeightEntry(
                    id = o.id(),
                    petId = o.id("petId"),
                    weightKg = o.number("weightKg"),
                    date = o.long("date"),
                    measurements = o.text("measurements"),
                    notes = o.text("notes"),
                )
            }
        val members =
            read("members") { o ->
                HouseholdMember(
                    id = o.id(),
                    name = o.text("name"),
                    contact = o.text("contact"),
                    notes = o.text("notes"),
                )
            }
        return BackupSnapshot(pets, tasks, events, records, weights, members).also(::validate)
    }

    fun validate(snapshot: BackupSnapshot) {
        val groups =
            listOf(
                snapshot.pets.map { it.id },
                snapshot.tasks.map { it.id },
                snapshot.events.map { it.id },
                snapshot.records.map { it.id },
                snapshot.weights.map { it.id },
                snapshot.members.map { it.id },
            )
        require(groups.sumOf { it.size } <= MAX_ROWS)
        groups.forEach { ids ->
            require(ids.size == ids.toSet().size) { "Duplicate record identifiers" }
            require(ids.all { it.isNotBlank() && it.length <= 200 })
        }
        val petIds = snapshot.pets.map { it.id }.toSet()
        fun reference(petId: String) {
            require(petId in petIds) { "Backup refers to a missing pet" }
        }
        fun weight(value: Double?) {
            require(value == null || (value.isFinite() && value > 0)) { "Invalid weight" }
        }
        fun activity(minutes: Int?, distance: Double?) {
            require(minutes == null || minutes > 0)
            require(distance == null || (distance.isFinite() && distance >= 0))
        }
        snapshot.pets.forEach {
            require(it.name.isNotBlank() && it.species.isNotBlank())
            weight(it.weightKg)
            require(it.photoPath == null) { "Backup must not contain filesystem paths" }
        }
        snapshot.tasks.forEach {
            reference(it.petId)
            require(it.kind in CareKind.all && it.title.isNotBlank())
            require(it.recurrenceDays >= 0)
            activity(it.durationMinutes, it.distanceKm)
            require(it.startDate == null || it.endDate == null || it.endDate >= it.startDate)
            if (it.kind == CareKind.MEDICATION) {
                require(it.startDate == null || it.time >= it.startDate)
                require(it.endDate == null || it.time <= it.endDate)
            }
        }
        val tasksById = snapshot.tasks.associateBy { it.id }
        snapshot.events.forEach { event ->
            reference(event.petId)
            require(event.kind in CareKind.all)
            activity(event.durationMinutes, event.distanceKm)
            event.taskId?.let { id ->
                tasksById[id]?.let {
                    require(it.petId == event.petId) { "History belongs to another pet" }
                }
            }
        }
        snapshot.records.forEach {
            reference(it.petId)
            require(it.kind in CareKind.all && it.title.isNotBlank())
            require(it.attachmentPath == null) { "Backup must not contain filesystem paths" }
        }
        snapshot.weights.forEach {
            reference(it.petId)
            weight(it.weightKg)
        }
        snapshot.members.forEach { require(it.name.isNotBlank()) }
    }
}
