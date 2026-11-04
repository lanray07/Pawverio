@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.pawverio.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pawverio.core.*
import java.io.File
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale

@Composable
internal fun Editor(
    title: Int,
    dismiss: () -> Unit,
    save: () -> Unit,
    valid: Boolean = true,
    delete: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    PawverioAlertDialog(
        onDismissRequest = dismiss,
        title = { Text(stringResource(title)) },
        text = {
            Column(
                Modifier.fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content,
            )
        },
        confirmButton = {
            TextButton(onClick = save, enabled = valid) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            Row {
                if (delete != null)
                    TextButton(onClick = delete) {
                        Text(
                            stringResource(R.string.delete),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                TextButton(onClick = dismiss) { Text(stringResource(R.string.cancel)) }
            }
        },
    )
}

@Composable
internal fun Field(id: Int, value: String, change: (String) -> Unit) {
    OutlinedTextField(
        value,
        change,
        label = { Text(stringResource(id)) },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun Choice(
    id: Int,
    current: String,
    choices: List<Pair<String, String>>,
    change: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(stringResource(id), style = MaterialTheme.typography.labelLarge)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(choices.find { it.first == current }?.second.orEmpty())
            }
            DropdownMenu(expanded, { expanded = false }) {
                choices.forEach { (key, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            change(key)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun PetChoice(pets: List<Pet>, current: String, change: (String) -> Unit) =
    Choice(R.string.pet, current, pets.map { it.id to it.name }, change)

@Composable
private fun KindChoice(current: String, kinds: List<String>, change: (String) -> Unit) {
    val labels =
        mapOf(
            CareKind.FEEDING to R.string.feeding,
            CareKind.WALKING to R.string.walking,
            CareKind.GROOMING to R.string.grooming,
            CareKind.MEDICATION to R.string.medication,
            CareKind.VET to R.string.vet,
            CareKind.VACCINATION to R.string.vaccination,
            CareKind.CUSTOM to R.string.custom,
        )
    Choice(
        R.string.custom,
        current,
        kinds.map { it to stringResource(labels.getValue(it)) },
        change,
    )
}

@Composable
internal fun DateTimeField(id: Int, value: Long, locale: Locale, change: (Long) -> Unit) {
    val context = LocalContext.current
    OutlinedButton(
        onClick = {
            val cal = Calendar.getInstance().apply { timeInMillis = value }
            DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        cal.set(year, month, day)
                        TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    cal.set(Calendar.HOUR_OF_DAY, hour)
                                    cal.set(Calendar.MINUTE, minute)
                                    cal.set(Calendar.SECOND, 0)
                                    change(cal.timeInMillis)
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                android.text.format.DateFormat.is24HourFormat(context),
                            )
                            .show()
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH),
                )
                .show()
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            stringResource(id) +
                ": " +
                DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale)
                    .format(java.util.Date(value))
        )
    }
}

@Composable
internal fun PetEditor(
    existing: Pet?,
    prefs: Preferences,
    locale: Locale,
    dismiss: () -> Unit,
    save: (Pet) -> Unit,
    delete: (String) -> Unit,
) {
    var name by remember { mutableStateOf(existing?.name.orEmpty()) }
    var species by remember { mutableStateOf(existing?.species ?: "dog") }
    var breed by remember { mutableStateOf(existing?.breed.orEmpty()) }
    var age by remember { mutableStateOf(existing?.approximateAge.orEmpty()) }
    var chip by remember { mutableStateOf(existing?.microchip.orEmpty()) }
    var allergies by remember { mutableStateOf(existing?.allergies.orEmpty()) }
    var notes by remember { mutableStateOf(existing?.notes.orEmpty()) }
    var photo by remember { mutableStateOf(existing?.photoPath) }
    var weight by remember {
        mutableStateOf(
            existing
                ?.weightKg
                ?.let {
                    java.text.NumberFormat.getNumberInstance(locale)
                        .format(if (prefs.pounds) it * 2.2046226218 else it)
                }
                .orEmpty()
        )
    }
    var error by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val imported = remember { mutableListOf<String>() }
    var submitted by remember { mutableStateOf(false) }
    DisposableEffect(Unit) {
        onDispose {
            imported
                .filter { !submitted || it != photo }
                .forEach { path -> deletePrivate(context, path) }
        }
    }
    val picker =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null)
                try {
                    photo = copyPrivate(context, uri, "photo")
                    imported.add(photo!!)
                } catch (_: Exception) {
                    error = true
                }
        }
    Editor(
        if (existing == null) R.string.add_pet else R.string.edit_pet,
        dismiss,
        {
            val value =
                weight
                    .takeIf { it.isNotBlank() }
                    ?.let { runCatching { parseNumber(it, locale) }.getOrNull() }
            if (weight.isNotBlank() && (value == null || value <= 0)) {
                error = true
            } else {
                submitted = true
                save(
                    (existing ?: Pet(name = name)).copy(
                        name = name,
                        species = species,
                        breed = breed,
                        approximateAge = age,
                        microchip = chip,
                        allergies = allergies,
                        notes = notes,
                        photoPath = photo,
                        weightKg = value?.let { if (prefs.pounds) it / 2.2046226218 else it },
                    )
                )
            }
        },
        name.isNotBlank(),
        existing?.let { { delete(it.id) } },
    ) {
        Field(R.string.name, name) { name = it }
        Choice(
            R.string.species,
            species,
            listOf(
                "dog" to stringResource(R.string.dog),
                "cat" to stringResource(R.string.cat),
                "other" to stringResource(R.string.other),
            ),
        ) {
            species = it
        }
        Field(R.string.breed, breed) { breed = it }
        Field(R.string.age, age) { age = it }
        Field(R.string.weight, weight) { weight = it }
        Field(R.string.microchip, chip) { chip = it }
        Field(R.string.allergies, allergies) { allergies = it }
        Field(R.string.notes, notes) { notes = it }
        OutlinedButton(onClick = { picker.launch(arrayOf("image/*")) }) {
            Text(stringResource(R.string.add_photo))
        }
        if (photo != null) Text(stringResource(R.string.photo) + " ✓")
        if (error)
            Text(stringResource(R.string.invalid_number), color = MaterialTheme.colorScheme.error)
    }
}

@Composable
internal fun TaskEditor(
    existing: CareTask?,
    pets: List<Pet>,
    members: List<HouseholdMember>,
    premium: Boolean,
    locale: Locale,
    dismiss: () -> Unit,
    save: (CareTask) -> Unit,
    delete: (CareTask) -> Unit,
) {
    var pet by remember { mutableStateOf(existing?.petId ?: pets.firstOrNull()?.id.orEmpty()) }
    var title by remember { mutableStateOf(existing?.title.orEmpty()) }
    var kind by remember { mutableStateOf(existing?.kind ?: CareKind.FEEDING) }
    var due by remember {
        mutableLongStateOf(existing?.time ?: System.currentTimeMillis() + 3600000)
    }
    var days by remember { mutableStateOf((existing?.recurrenceDays ?: 0).toString()) }
    var notes by remember { mutableStateOf(existing?.notes.orEmpty()) }
    var assigned by remember { mutableStateOf(existing?.assignedTo.orEmpty()) }
    var dosage by remember { mutableStateOf(existing?.dosage.orEmpty()) }
    var portion by remember { mutableStateOf(existing?.portion.orEmpty()) }
    var meal by remember { mutableStateOf(existing?.mealType.orEmpty()) }
    var minutes by remember { mutableStateOf(existing?.durationMinutes?.toString().orEmpty()) }
    var reminder by remember { mutableStateOf(existing?.remindersEnabled ?: true) }
    var start by remember { mutableStateOf(existing?.startDate) }
    var end by remember { mutableStateOf(existing?.endDate) }
    var error by remember { mutableStateOf(false) }
    Editor(
        R.string.add_task,
        dismiss,
        {
            val recurrence = runCatching { parseNumber(days, locale) }.getOrNull()
            val duration =
                minutes
                    .takeIf { it.isNotBlank() }
                    ?.let { runCatching { parseNumber(it, locale) }.getOrNull() }
            if (
                recurrence == null ||
                    recurrence < 0 ||
                    recurrence % 1.0 != 0.0 ||
                    recurrence > 36500 ||
                    (minutes.isNotBlank() &&
                        (duration == null ||
                            duration <= 0 ||
                            duration % 1.0 != 0.0 ||
                            duration > Int.MAX_VALUE)) ||
                    (start != null && end != null && end!! < start!!) ||
                    (kind == CareKind.MEDICATION &&
                        ((start != null && due < start!!) || (end != null && due > end!!)))
            ) {
                error = true
            } else
                save(
                    (existing ?: CareTask(petId = pet, title = title)).copy(
                        petId = pet,
                        title = title,
                        kind = kind,
                        time = due,
                        recurrenceDays = recurrence.toInt(),
                        notes = notes,
                        assignedTo = assigned,
                        dosage = dosage,
                        portion = portion,
                        mealType = meal,
                        durationMinutes = duration?.toInt(),
                        remindersEnabled = reminder,
                        startDate = start,
                        endDate = end,
                    )
                )
        },
        title.isNotBlank() && pet.isNotBlank(),
        existing?.let { { delete(it) } },
    ) {
        PetChoice(pets, pet) { pet = it }
        KindChoice(kind, CareKind.all) { kind = it }
        Field(R.string.task_title, title) { title = it }
        DateTimeField(R.string.due_date, due, locale) { due = it }
        Field(R.string.interval_days, days) { days = it }
        if (kind == CareKind.FEEDING) {
            Field(R.string.meal_type, meal) { meal = it }
            Field(R.string.portion, portion) { portion = it }
        }
        if (kind == CareKind.MEDICATION) {
            Text(stringResource(R.string.medical_notice))
            Field(R.string.dosage, dosage) { dosage = it }
            OptionalDateTimeField(R.string.start_date, start, locale) { start = it }
            OptionalDateTimeField(R.string.end_date, end, locale) { end = it }
        }
        if (kind == CareKind.WALKING) Field(R.string.duration_minutes, minutes) { minutes = it }
        Field(R.string.notes, notes) { notes = it }
        if (premium) Field(R.string.assigned_to, assigned) { assigned = it }
        Row {
            Text(stringResource(R.string.notifications), Modifier.weight(1f))
            Switch(reminder, { reminder = it })
        }
        if (error)
            Text(stringResource(R.string.invalid_number), color = MaterialTheme.colorScheme.error)
    }
}

@Composable
internal fun RecordEditor(
    existing: HealthRecord?,
    pets: List<Pet>,
    locale: Locale,
    dismiss: () -> Unit,
    save: (HealthRecord) -> Unit,
    delete: (HealthRecord) -> Unit,
) {
    var pet by remember { mutableStateOf(existing?.petId ?: pets.firstOrNull()?.id.orEmpty()) }
    var kind by remember { mutableStateOf(existing?.kind ?: CareKind.VET) }
    var title by remember { mutableStateOf(existing?.title.orEmpty()) }
    var provider by remember { mutableStateOf(existing?.provider.orEmpty()) }
    var contact by remember { mutableStateOf(existing?.contact.orEmpty()) }
    var notes by remember { mutableStateOf(existing?.notes.orEmpty()) }
    var date by remember { mutableLongStateOf(existing?.date ?: System.currentTimeMillis()) }
    var next by remember { mutableStateOf(existing?.nextDueDate) }
    var attachment by remember { mutableStateOf(existing?.attachmentPath) }
    var error by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val imported = remember { mutableListOf<String>() }
    var submitted by remember { mutableStateOf(false) }
    DisposableEffect(Unit) {
        onDispose {
            imported
                .filter { !submitted || it != attachment }
                .forEach { deletePrivate(context, it) }
        }
    }
    val picker =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null)
                try {
                    attachment = copyPrivate(context, uri, "record")
                    imported.add(attachment!!)
                } catch (_: Exception) {
                    error = true
                }
        }
    Editor(
        R.string.add_record,
        dismiss,
        {
            submitted = true
            save(
                (existing ?: HealthRecord(petId = pet, title = title)).copy(
                    petId = pet,
                    kind = kind,
                    title = title,
                    date = date,
                    nextDueDate = next,
                    provider = provider,
                    contact = contact,
                    notes = notes,
                    attachmentPath = attachment,
                )
            )
        },
        title.isNotBlank() && pet.isNotBlank(),
        existing?.let { { delete(it) } },
    ) {
        PetChoice(pets, pet) { pet = it }
        KindChoice(kind, listOf(CareKind.VET, CareKind.VACCINATION)) { kind = it }
        Field(
            if (kind == CareKind.VACCINATION) R.string.vaccine_name else R.string.task_title,
            title,
        ) {
            title = it
        }
        DateTimeField(
            if (kind == CareKind.VACCINATION) R.string.administered_date else R.string.due_date,
            date,
            locale,
        ) {
            date = it
        }
        OptionalDateTimeField(R.string.next_due_date, next, locale) { next = it }
        Field(R.string.clinic, provider) { provider = it }
        Field(R.string.contact, contact) { contact = it }
        Field(R.string.notes, notes) { notes = it }
        OutlinedButton(
            onClick = { picker.launch(arrayOf("application/pdf", "image/*", "text/plain")) }
        ) {
            Text(stringResource(R.string.add_attachment))
        }
        attachment?.let { AttachmentExport(it) }
        Text(stringResource(R.string.medical_notice), style = MaterialTheme.typography.bodySmall)
        if (error) Text(stringResource(R.string.error_generic))
    }
}

@Composable
internal fun EntryEditor(
    title: Int,
    pets: List<Pet>,
    ids: List<Int>,
    dismiss: () -> Unit,
    save: (String, List<String>, Long) -> Unit,
) {
    var pet by remember { mutableStateOf(pets.firstOrNull()?.id.orEmpty()) }
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val values = remember { mutableStateListOf(*Array(ids.size) { "" }) }
    Editor(
        title,
        dismiss,
        { save(pet, values.toList(), date) },
        pet.isNotBlank() && values.first().isNotBlank(),
    ) {
        PetChoice(pets, pet) { pet = it }
        DateTimeField(
            R.string.due_date,
            date,
            LocalContext.current.resources.configuration.locales[0],
        ) {
            date = it
        }
        ids.forEachIndexed { i, id -> Field(id, values[i]) { values[i] = it } }
    }
}

@Composable
internal fun SimpleEditor(
    title: Int,
    ids: List<Int>,
    dismiss: () -> Unit,
    initial: List<String> = emptyList(),
    save: (List<String>) -> Unit,
) {
    val values = remember { mutableStateListOf(*Array(ids.size) { initial.getOrElse(it) { "" } }) }
    Editor(title, dismiss, { save(values.toList()) }) {
        ids.forEachIndexed { i, id -> Field(id, values[i]) { values[i] = it } }
    }
}

internal fun pruneAttachmentFiles(context:Context,references:Collection<String>){
    val root=File(context.filesDir,"attachments").canonicalFile
    // Android user0 commonly aliases /data/user/0 to /data/data. Compare both sides
    // canonically so a saved photo/document cannot be mistaken for an orphan.
    val known=references.map{File(it).canonicalPath}.toSet()
    root.listFiles()?.filter{it.isFile&&it.canonicalFile.parentFile==root&&it.canonicalPath !in known}?.forEach{it.delete()}
}

internal fun copyPrivate(context: Context, uri: android.net.Uri, prefix: String): String {
    require(uri.scheme == "content") { "Choose a document from the system picker" }
    val folder = File(context.filesDir, "attachments").apply { mkdirs() }
    val file =
        File(
            folder,
            "$prefix-${java.util.UUID.randomUUID()}" +
                when (context.contentResolver.getType(uri)) {
                    "application/pdf" -> ".pdf"
                    "image/png" -> ".png"
                    "image/jpeg" -> ".jpg"
                    "text/plain" -> ".txt"
                    else -> ".bin"
                },
        )
    try {
        context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input)
            file.outputStream().use { output ->
                val buffer = ByteArray(8192)
                var total = 0
                while (true) {
                    val count = input.read(buffer)
                    if (count < 0) break
                    total += count
                    require(total <= 20 * 1024 * 1024)
                    output.write(buffer, 0, count)
                }
            }
        }
        return file.absolutePath
    } catch (e: Exception) {
        file.delete()
        throw e
    }
}

private fun deletePrivate(context: Context, path: String) {
    val root = File(context.filesDir, "attachments").canonicalFile
    val file = File(path).canonicalFile
    if (file.parentFile == root) file.delete()
}

@Composable
private fun OptionalDateTimeField(id: Int, value: Long?, locale: Locale, change: (Long?) -> Unit) {
    val context = LocalContext.current
    Column {
        OutlinedButton(
            onClick = {
                val calendar =
                    Calendar.getInstance().apply {
                        timeInMillis = value ?: System.currentTimeMillis()
                    }
                DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            calendar.set(year, month, day)
                            TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                                        calendar.set(Calendar.MINUTE, minute)
                                        change(calendar.timeInMillis)
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    android.text.format.DateFormat.is24HourFormat(context),
                                )
                                .show()
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                    )
                    .show()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                stringResource(id) +
                    ": " +
                    (value?.let {
                        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale)
                            .format(java.util.Date(it))
                    } ?: stringResource(R.string.optional))
            )
        }
        if (value != null)
            TextButton(onClick = { change(null) }) { Text(stringResource(R.string.delete)) }
    }
}

@Composable
private fun AttachmentExport(path: String) {
    val context = LocalContext.current
    var confirm by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/octet-stream")
        ) { uri ->
            if (uri != null)
                try {
                    val root = File(context.filesDir, "attachments").canonicalFile
                    val source = File(path).canonicalFile
                    require(source.parentFile == root)
                    context.contentResolver.openOutputStream(uri, "wt").use { output ->
                        requireNotNull(output)
                        source.inputStream().use { it.copyTo(output) }
                    }
                } catch (_: Exception) {
                    error = true
                }
        }
    TextButton(onClick = { confirm = true }) { Text(stringResource(R.string.attachment_export)) }
    if (confirm)
        PawverioAlertDialog(
            onDismissRequest = { confirm = false },
            text = { Text(stringResource(R.string.export_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirm = false
                        launcher.launch(
                            "Pawverio-document." + File(path).extension.ifBlank { "bin" }
                        )
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirm = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    if (error) Text(stringResource(R.string.error_generic), color = MaterialTheme.colorScheme.error)
}
