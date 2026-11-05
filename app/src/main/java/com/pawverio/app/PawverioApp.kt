@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.pawverio.app

import android.Manifest
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.pawverio.core.*
import com.pawverio.platform.*
import java.io.File
import java.text.DateFormat
import java.text.NumberFormat
import java.time.*
import java.util.Locale
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Teal = Color(0xFF286B70)
private val Ivory = Color(0xFFFAF7F0)
private val Sage = Color(0xFF9AB7A5)
private val Coral = Color(0xFFD98A78)
private val languages =
    listOf(
        "" to "◉",
        "en-GB" to "English (UK)",
        "en-US" to "English (US)",
        "en-AU" to "English (AU)",
        "es-ES" to "Español",
        "es-419" to "Español (Latinoamérica)",
        "fr-FR" to "Français",
        "fr-CA" to "Français (Canada)",
        "de-DE" to "Deutsch",
        "it-IT" to "Italiano",
        "pt-BR" to "Português (Brasil)",
        "pt-PT" to "Português",
        "nl-NL" to "Nederlands",
        "pl-PL" to "Polski",
        "sv-SE" to "Svenska",
        "ja-JP" to "日本語",
        "hi-IN" to "हिन्दी",
        "ar" to "العربية",
        "tr-TR" to "Türkçe",
        "id-ID" to "Bahasa Indonesia",
    )

@Composable
fun PawverioApp(initialRoute: Int = 0) {
    val base = LocalContext.current
    val settings = remember { Settings(base.applicationContext) }
    val loadedPreferences by settings.flow.collectAsStateWithLifecycle(initialValue = null)
    val prefs=loadedPreferences
    if(prefs==null){
        MaterialTheme(colorScheme=pawverioColors(isSystemInDarkTheme())){
            Surface(Modifier.fillMaxSize()){
                Column(Modifier.fillMaxSize(),verticalArrangement=Arrangement.Center,horizontalAlignment=Alignment.CenterHorizontally){
                    Icon(Icons.Outlined.Pets,null,tint=MaterialTheme.colorScheme.primary,modifier=Modifier.size(56.dp))
                    Spacer(Modifier.height(24.dp))
                    if (ValueAnimator.areAnimatorsEnabled()) {
                        CircularProgressIndicator()
                    } else {
                        Text(stringResource(R.string.loading))
                    }
                }
            }
        }
        return
    }
    val configuration = Configuration(LocalConfiguration.current)
    val locale =
        if (prefs.language.isBlank()) java.util.Locale.getDefault()
        else Locale.forLanguageTag(prefs.language)
    configuration.setLocales(LocaleList(locale))
    configuration.setLayoutDirection(locale)
    val localized =
        remember(base, locale.toLanguageTag(), configuration) {
            android.view.ContextThemeWrapper(base, R.style.Theme_Pawverio).apply {
                applyOverrideConfiguration(configuration)
            }
        }
    CompositionLocalProvider(
        LocalContext provides localized,
        LocalConfiguration provides configuration,
        androidx.compose.ui.platform.LocalLayoutDirection provides
            if (configuration.layoutDirection == android.view.View.LAYOUT_DIRECTION_RTL)
                androidx.compose.ui.unit.LayoutDirection.Rtl
            else androidx.compose.ui.unit.LayoutDirection.Ltr,
    ) {
        MaterialTheme(
            colorScheme = pawverioColors(prefs.dark, prefs.highContrast),
            typography = Typography(),
        ) {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                MainContent(base, settings, prefs, locale, initialRoute)
            }
        }
    }
}

@Composable
private fun MainContent(
    base: Context,
    settings: Settings,
    prefs: Preferences,
    locale: Locale,
    initialRoute: Int,
) {
    val scope = rememberCoroutineScope()
    val viewModel: CareViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val repository = viewModel.repository
    val pets by viewModel.pets.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    val health by viewModel.health.collectAsStateWithLifecycle()
    val weights by viewModel.weights.collectAsStateWithLifecycle()
    val members by viewModel.members.collectAsStateWithLifecycle()
    val billing = remember { AmazonBilling(base) }
    val bill by billing.state.collectAsStateWithLifecycle()
    val reminders = remember { ReminderScheduler(base) }
    LaunchedEffect(prefs.language,locale.toLanguageTag()) {
        ReminderPreferences(base).localeTag = prefs.language
    }
    var screen by remember {
        mutableIntStateOf(
            when (initialRoute) {
                2 -> 2
                4 -> 0
                6 -> 1
                else -> 0
            }
        )
    }
    var dialog by remember {
        mutableStateOf<String?>(
            when (initialRoute) {
                5 -> "voice"
                7 -> "sitter"
                else -> null
            }
        )
    }
    var kindFilter by remember {
        mutableStateOf(
            when (initialRoute) {
                1 -> CareKind.FEEDING
                3 -> CareKind.MEDICATION
                4 -> CareKind.GROOMING
                else -> ""
            }
        )
    }
    var healthPetFilter by remember { mutableStateOf<String?>(null) }
    var healthKindFilter by remember { mutableStateOf<String?>(null) }
    var petEdit by remember { mutableStateOf<Pet?>(null) }
    var onboardingPet by remember { mutableStateOf<Pet?>(null) }
    var taskEdit by remember { mutableStateOf<CareTask?>(null) }
    var recordEdit by remember { mutableStateOf<HealthRecord?>(null) }
    var search by remember { mutableStateOf("") }
    var failure by remember { mutableStateOf(false) }
    var voiceText by remember { mutableStateOf("") }
    var listening by remember { mutableStateOf(false) }
    var voiceError by remember { mutableStateOf(false) }
    var voiceState by remember { mutableStateOf(VoiceState.IDLE) }
    var pendingIntent by remember { mutableStateOf<VoiceIntent?>(null) }
    val voice = remember {
        VoiceController(
            base,
            onResult = {
                voiceText = it
                listening = false
            },
            onState = {
                voiceState = it
                listening = it == VoiceState.LISTENING
                voiceError =
                    it in
                        listOf(
                            VoiceState.UNAVAILABLE,
                            VoiceState.ERROR,
                            VoiceState.NO_MATCH,
                            VoiceState.PERMISSION_REQUIRED,
                        )
            },
        )
    }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        billing.connect()
        val observer =
            androidx.lifecycle.LifecycleEventObserver { _, event ->
                if (event == androidx.lifecycle.Lifecycle.Event.ON_STOP) voice.stop()
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            voice.close()
        }
    }
    val mic =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) voice.start(locale.toLanguageTag())
            else {
                voiceState = VoiceState.PERMISSION_REQUIRED
                voiceError = true
            }
        }
    val notifications =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    fun action(block: suspend () -> Unit) {
        scope.launch {
            try {
                block()
            } catch (_: Exception) {
                failure = true
            }
        }
    }
    fun schedule(task: CareTask) {
        if (task.paused || !task.remindersEnabled) reminders.cancel(task.id)
        else
            reminders.schedule(
                task.id,
                task.title,
                pets.find { it.id == task.petId }?.name.orEmpty(),
                (task.snoozedUntil ?: task.time),
                task.recurrenceDays,
                task.endDate,
                task.time,
            )
    }
    var previousTasks by remember { mutableStateOf(emptyMap<String, CareTask>()) }
    var previousHealth by remember { mutableStateOf(emptyMap<String, HealthRecord>()) }
    LaunchedEffect(tasks, health, pets) {
        (previousTasks.keys - tasks.map { it.id }.toSet()).forEach { reminders.cancel(it) }
        (previousHealth.keys - health.map { it.id }.toSet()).forEach { reminders.cancel(it) }
        tasks.filter { previousTasks[it.id] != it }.forEach { schedule(it) }
        health
            .filter { previousHealth[it.id] != it }
            .forEach { record ->
                reminders.cancel(record.id)
                val due =
                    record.nextDueDate ?: if (record.kind == CareKind.VET) record.date else null
                if (due != null && due > System.currentTimeMillis())
                    reminders.schedule(record.id, record.title, record.provider, due)
            }
        previousTasks = tasks.associateBy { it.id }
        previousHealth = health.associateBy { it.id }
    }
    fun gate(next: String) {
        dialog = if (bill.premium) next else "premium"
    }
    suspend fun pruneAttachments() {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val known =
                repository.observePets().first().mapNotNull { it.photoPath }.toSet() +
                    repository.observeHealthRecords().first().mapNotNull { it.attachmentPath }
            pruneAttachmentFiles(base,known)
        }
    }
    val stringsContext = LocalContext.current
    fun say(value: String) {
        voiceText = value
        if (prefs.spoken && !voice.speak(value, locale.toLanguageTag())) voiceError = true
    }
    fun execute(intent: VoiceIntent) {
        val petName = { id: String? -> pets.find { it.id == id }?.name.orEmpty() }
        when (intent) {
            is VoiceIntent.FeedingStatus -> {
                val today = LocalDate.now()
                val found =
                    events.any {
                        it.petId == intent.petId &&
                            it.kind == CareKind.FEEDING &&
                            Instant.ofEpochMilli(it.completedAt)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate() == today
                    }
                say(
                    petName(intent.petId) +
                        ": " +
                        stringsContext.getString(
                            if (found) R.string.completed else R.string.upcoming
                        )
                )
            }
            is VoiceIntent.CompleteFeeding -> {
                val task =
                    tasks
                        .filter {
                            it.petId == intent.petId && it.kind == CareKind.FEEDING && !it.paused
                        }
                        .minByOrNull { it.time }
                if (task != null)
                    action {
                        if (repository.completeTask(task.id) != null) {
                            reminders.cancel(task.id)
                            say(stringsContext.getString(R.string.completed))
                        } else say(stringsContext.getString(R.string.upcoming))
                    }
                else say(stringsContext.getString(R.string.empty_tasks_body))
            }
            is VoiceIntent.NextVet -> {
                val next =
                    health
                        .filter {
                            it.kind == CareKind.VET &&
                                (intent.petId == null || it.petId == intent.petId) &&
                                it.date >= System.currentTimeMillis()
                        }
                        .minByOrNull { it.date }
                say(
                    next?.let { it.title + " · " + date(it.date, locale) }
                        ?: stringsContext.getString(R.string.empty_health)
                )
                screen = 2
            }
            is VoiceIntent.Vaccinations -> {
                screen = 2
                healthPetFilter = intent.petId
                healthKindFilter = CareKind.VACCINATION
                dialog = null
            }
            is VoiceIntent.LogWalk -> {
                val id = intent.petId ?: pets.singleOrNull()?.id
                if (id != null)
                    action {
                        repository.logWalk(id, intent.minutes)
                        say(stringsContext.getString(R.string.completed))
                    }
                else say(stringsContext.getString(R.string.pet))
            }
            VoiceIntent.Medications -> {
                screen = 0
                dialog = null
                kindFilter = CareKind.MEDICATION
            }
            VoiceIntent.AddGrooming -> {
                if (pets.isNotEmpty()) {
                    taskEdit =
                        CareTask(petId = pets.first().id, title = "", kind = CareKind.GROOMING)
                    dialog = "task"
                }
            }
            VoiceIntent.ReadToday -> {
                val today = LocalDate.now()
                say(
                    tasks
                        .filter {
                            !it.paused &&
                                Instant.ofEpochMilli(it.time)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate() <= today
                        }
                        .joinToString(". ") {
                            "${petName(it.petId)}: ${it.title}, ${date(it.time, locale)}"
                        }
                        .ifBlank { stringsContext.getString(R.string.empty_tasks) }
                )
            }
            else -> say(stringsContext.getString(R.string.voice_unknown))
        }
    }
    val exportService = remember { ExportService(base) }
    var exportContents by remember { mutableStateOf("") }
    var backupPassword by remember { mutableStateOf("") }
    var backupRestore by remember { mutableStateOf(false) }
    val backupWrite =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/octet-stream")
        ) { uri ->
            if (uri == null) backupPassword = ""
            else
                action {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        val json = BackupCodec.exportJson(repository)
                        val password = backupPassword.toCharArray()
                        try {
                            val bytes =
                                EncryptedBackup.encrypt(json.toByteArray(Charsets.UTF_8), password)
                            base.contentResolver.openOutputStream(uri, "wt").use {
                                requireNotNull(it).write(bytes)
                            }
                        } finally {
                            password.fill('\u0000')
                            backupPassword = ""
                        }
                    }
                }
        }
    val backupRead =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) backupPassword = ""
            else
                action {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        val password = backupPassword.toCharArray()
                        try {
                            val bytes =
                                base.contentResolver.openInputStream(uri).use {
                                    requireNotNull(it).readBytesLimited(11 * 1024 * 1024)
                                }
                            val json =
                                EncryptedBackup.decrypt(bytes, password).toString(Charsets.UTF_8)
                            BackupCodec.importJson(repository, json)
                        } finally {
                            password.fill('\u0000')
                            backupPassword = ""
                        }
                    }
                }
        }
    val textExport =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) {
            uri ->
            if (uri != null) action { exportService.writeText(uri, exportContents) }
        }
    val dataExport =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/json")
        ) { uri ->
            if (uri != null)
                action {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        exportService.writeText(uri, BackupCodec.exportJson(repository))
                    }
                }
        }
    val pdfExport =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/pdf")
        ) { uri ->
            if (uri != null)
                action { exportService.writePdf(uri, "Pawverio", exportContents.lines()) }
        }
    fun report(): String = buildString {
        appendLine(stringsContext.getString(R.string.sitter_mode))
        appendLine(prefs.instructions)
        appendLine(stringsContext.getString(R.string.emergency_contact) + ": " + prefs.emergency)
        pets.forEach { pet ->
            appendLine("\n${pet.name} · ${pet.breed}")
            appendLine(pet.notes)
            appendLine(stringsContext.getString(R.string.allergies) + ": " + pet.allergies)
            tasks
                .filter { it.petId == pet.id }
                .forEach {
                    appendLine(
                        "${it.title} · ${date(it.time, locale)} · ${it.portion} ${it.dosage} · ${it.notes} · ${it.assignedTo}"
                    )
                }
            health
                .filter { it.petId == pet.id }
                .forEach {
                    appendLine("${it.title} · ${it.provider} · ${it.contact} · ${it.notes}")
                }
        }
    }
    val navIds =
        listOf(
            R.string.nav_today,
            R.string.nav_pets,
            R.string.nav_health,
            R.string.nav_history,
            R.string.nav_settings,
        )
    val navIcons =
        listOf(
            Icons.Outlined.Today,
            Icons.Outlined.Pets,
            Icons.Outlined.FavoriteBorder,
            Icons.Outlined.History,
            Icons.Outlined.Settings,
        )
    val wide = LocalConfiguration.current.screenWidthDp >= 840
    if (!prefs.onboarded) {
        Onboarding(
            prefs,
            settings,
            {
                onboardingPet = pets.firstOrNull()
                dialog = if (onboardingPet == null) "pet" else "onboardingFeeding"
            },
            { action { settings.set("onboarded", true) } },
        )
    } else {
        Row(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            if (wide)
                NavigationRail(
                    Modifier.fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    Spacer(Modifier.height(20.dp))
                    Icon(
                        Icons.Outlined.Pets,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp),
                    )
                    Spacer(Modifier.height(32.dp))
                    navIds.forEachIndexed { i, id ->
                        NavigationRailItem(
                            selected = screen == i,
                            onClick = {
                                screen = i
                                search = ""
                            },
                            icon = { Icon(navIcons[i], null) },
                            label = { Text(stringResource(id)) },
                        )
                    }
                }
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    stringResource(R.string.app_name),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 25.sp,
                                )
                                Text(
                                    stringResource(R.string.offline_label),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { dialog = "voice" }) {
                                Icon(
                                    Icons.Outlined.MicNone,
                                    stringResource(R.string.accessibility_voice),
                                )
                            }
                            IconButton(onClick = { gate("sitter") }) {
                                Icon(
                                    Icons.Outlined.Description,
                                    stringResource(R.string.sitter_mode),
                                )
                            }
                        },
                    )
                },
                bottomBar = {
                    if (!wide)
                        NavigationBar {
                            navIds.forEachIndexed { i, id ->
                                NavigationBarItem(
                                    selected = screen == i,
                                    onClick = {
                                        screen = i
                                        search = ""
                                    },
                                    icon = { Icon(navIcons[i], null) },
                                    label = {
                                        Text(stringResource(id), maxLines = 1, fontSize = 11.sp)
                                    },
                                )
                            }
                        }
                },
                floatingActionButton = {
                    if (screen < 3)
                        ExtendedFloatingActionButton(
                            onClick = {
                                when (screen) {
                                    1 -> {
                                        petEdit = null
                                        if (pets.isEmpty()) dialog = "pet" else gate("pet")
                                    }
                                    2 ->
                                        if (pets.isNotEmpty()) {
                                            recordEdit = null
                                            dialog = "record"
                                        } else dialog = "pet"
                                    else ->
                                        if (pets.isNotEmpty()) {
                                            taskEdit = null
                                            dialog = "task"
                                        } else dialog = "pet"
                                }
                            },
                            icon = { Icon(Icons.Outlined.Add, null) },
                            text = {
                                Text(
                                    stringResource(
                                        if (screen == 1) R.string.add_pet
                                        else if (screen == 2) R.string.add_record
                                        else R.string.add_task
                                    )
                                )
                            },
                        )
                },
            ) { padding ->
                LazyColumn(
                    Modifier.fillMaxSize().padding(padding),
                    contentPadding =
                        PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        Text(
                            stringResource(navIds[screen]),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    when (screen) {
                        0 -> {
                            item { Hero(pets, locale) }
                            if (pets.isEmpty())
                                item {
                                    EmptyState(R.string.empty_pets, R.string.empty_pets_body) {
                                        dialog = "pet"
                                    }
                                }
                            else {
                                item {
                                    SectionTitle(R.string.pet_profile)
                                    Row(
                                        Modifier.horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        pets.forEach { pet ->
                                            PetTile(pet, prefs, locale, Modifier.width(230.dp)) {
                                                petEdit = pet
                                                dialog = "pet"
                                            }
                                        }
                                    }
                                }
                                item {
                                    SectionTitle(R.string.today_tasks)
                                    Text(
                                        androidx.compose.ui.res.pluralStringResource(
                                            R.plurals.tasks_count,
                                            tasks.count { !it.paused },
                                            tasks.count { !it.paused },
                                        ),
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                    Row(
                                        Modifier.horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        listOf(
                                                "" to R.string.nav_today,
                                                CareKind.FEEDING to R.string.feeding,
                                                CareKind.WALKING to R.string.walking,
                                                CareKind.GROOMING to R.string.grooming,
                                                CareKind.MEDICATION to R.string.medication,
                                            )
                                            .forEach { (key, id) ->
                                                FilterChip(
                                                    selected = kindFilter == key,
                                                    onClick = { kindFilter = key },
                                                    label = { Text(stringResource(id)) },
                                                )
                                            }
                                    }
                                }
                                if (tasks.isEmpty())
                                    item {
                                        EmptyState(
                                            R.string.empty_tasks,
                                            R.string.empty_tasks_body,
                                        ) {
                                            taskEdit = null
                                            dialog = "task"
                                        }
                                    }
                                items(
                                    tasks
                                        .filter { kindFilter.isBlank() || it.kind == kindFilter }
                                        .sortedBy { it.time },
                                    key = { it.id },
                                ) { task ->
                                    TaskTile(
                                        task,
                                        pets.find { it.id == task.petId }?.name.orEmpty(),
                                        locale,
                                        {
                                            action {
                                                if (repository.completeTask(task.id) != null)
                                                    reminders.cancel(task.id)
                                            }
                                        },
                                        {
                                            taskEdit = task
                                            dialog = "task"
                                        },
                                        {
                                            action {
                                                repository.snoozeTask(
                                                    task.id,
                                                    System.currentTimeMillis() + 900000,
                                                )
                                                schedule(
                                                    task.copy(
                                                        snoozedUntil =
                                                            System.currentTimeMillis() + 900000
                                                    )
                                                )
                                            }
                                        },
                                        {
                                            action {
                                                repository.pauseTask(task.id, !task.paused)
                                                schedule(task.copy(paused = !task.paused))
                                            }
                                        },
                                    )
                                }
                                item {
                                    OutlinedButton(onClick = { dialog = "walk" }) {
                                        Icon(Icons.Outlined.DirectionsWalk, null)
                                        Text(stringResource(R.string.log_walk))
                                    }
                                }
                            }
                        }
                        1 -> {
                            item {
                                OutlinedTextField(
                                    search,
                                    { search = it },
                                    label = { Text(stringResource(R.string.search)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    leadingIcon = { Icon(Icons.Outlined.Search, null) },
                                )
                            }
                            if (pets.isEmpty())
                                item {
                                    EmptyState(R.string.empty_pets, R.string.empty_pets_body) {
                                        dialog = "pet"
                                    }
                                }
                            items(
                                pets.filter {
                                    it.name.contains(search, true) ||
                                        it.breed.contains(search, true)
                                },
                                key = { it.id },
                            ) { pet ->
                                PetTile(pet, prefs, locale, Modifier.fillMaxWidth()) {
                                    petEdit = pet
                                    dialog = "pet"
                                }
                            }
                        }
                        2 -> {
                            item {
                                Text(
                                    stringResource(R.string.medical_notice),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                            if (health.isEmpty())
                                item { Text(stringResource(R.string.empty_health)) }
                            if (healthPetFilter != null || healthKindFilter != null)
                                item {
                                    TextButton(
                                        onClick = {
                                            healthPetFilter = null
                                            healthKindFilter = null
                                        }
                                    ) {
                                        Text(stringResource(R.string.nav_health))
                                    }
                                }
                            items(
                                health.filter {
                                    (healthPetFilter == null || it.petId == healthPetFilter) &&
                                        (healthKindFilter == null || it.kind == healthKindFilter)
                                },
                                key = { it.id },
                            ) { record ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        recordEdit = record
                                        dialog = "record"
                                    },
                                ) {
                                    Column(Modifier.padding(20.dp)) {
                                        Text(
                                            record.title,
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                        Text(
                                            pets.find { it.id == record.petId }?.name.orEmpty() +
                                                " · " +
                                                date(record.date, locale)
                                        )
                                        Text(record.provider)
                                        if (record.notes.isNotBlank()) Text(record.notes)
                                        record.nextDueDate?.let {
                                            Text(
                                                stringResource(R.string.next_due_date) +
                                                    ": " +
                                                    date(it, locale)
                                            )
                                        }
                                        if (record.attachmentPath != null)
                                            Text(stringResource(R.string.attachment))
                                    }
                                }
                            }
                            item {
                                SectionTitle(R.string.weight_history)
                                OutlinedButton(
                                    onClick = {
                                        if (pets.isEmpty()) dialog = "pet" else gate("weight")
                                    }
                                ) {
                                    Text(stringResource(R.string.add_weight))
                                }
                            }
                            pets.forEach { pet ->
                                val list =
                                    weights.filter { it.petId == pet.id }.sortedBy { it.date }
                                if (list.isNotEmpty()) {
                                    item { WeightChart(pet.name, list, prefs.pounds, locale) }
                                    items(list, key = { it.id }) { entry ->
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                date(entry.date, locale) +
                                                    " · " +
                                                    weight(entry.weightKg, prefs.pounds, locale) +
                                                    " · " +
                                                    entry.notes,
                                                Modifier.weight(1f),
                                            )
                                            IconButton(
                                                onClick = {
                                                    action { repository.deleteWeight(entry.id) }
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Outlined.Delete,
                                                    stringResource(R.string.delete),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        3 -> {
                            if (events.isEmpty())
                                item { Text(stringResource(R.string.empty_history)) }
                            items(events.sortedByDescending { it.completedAt }, key = { it.id }) {
                                event ->
                                Card(Modifier.fillMaxWidth()) {
                                    Row(
                                        Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            Icons.Outlined.CheckCircle,
                                            null,
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                        Spacer(Modifier.width(16.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                event.title.ifBlank {
                                                    stringsContext.getString(R.string.walking)
                                                },
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                            Text(
                                                pets.find { it.id == event.petId }?.name.orEmpty() +
                                                    " · " +
                                                    date(event.completedAt, locale),
                                                style = MaterialTheme.typography.bodySmall,
                                            )
                                            event.durationMinutes?.let {
                                                Text(
                                                    NumberFormat.getIntegerInstance(locale)
                                                        .format(it) +
                                                        " · " +
                                                        stringResource(R.string.duration_minutes)
                                                )
                                            }
                                        }
                                        TextButton(
                                            onClick = {
                                                action { repository.undoCompletion(event.id) }
                                            }
                                        ) {
                                            Text(stringResource(R.string.undo))
                                        }
                                    }
                                }
                            }
                        }
                        4 -> {
                            item {
                                SettingsPanel(
                                    prefs,
                                    settings,
                                    { action { it() } },
                                    { if (it == "backup") gate(it) else dialog = it },
                                    {
                                        if (Build.VERSION.SDK_INT >= 33)
                                            notifications.launch(
                                                Manifest.permission.POST_NOTIFICATIONS
                                            )
                                        else
                                            base.startActivity(
                                                android.content
                                                    .Intent(
                                                        android.provider.Settings
                                                            .ACTION_APP_NOTIFICATION_SETTINGS
                                                    )
                                                    .putExtra(
                                                        android.provider.Settings.EXTRA_APP_PACKAGE,
                                                        base.packageName,
                                                    )
                                            )
                                    },
                                )
                            }
                            item {
                                Card(
                                    onClick = { dialog = "premium" },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Teal),
                                ) {
                                    Column(Modifier.padding(24.dp)) {
                                        Text(
                                            stringResource(R.string.premium),
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleLarge,
                                        )
                                        Text(
                                            stringResource(
                                                if (bill.premium) R.string.premium_active
                                                else R.string.premium_body
                                            ),
                                            color = Color.White,
                                        )
                                        TextButton(onClick = { billing.restore() }) {
                                            Text(
                                                stringResource(R.string.restore_purchases),
                                                color = Color.White,
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                SectionTitle(R.string.household)
                                members.forEach { Text(it.name + " · " + it.contact) }
                                tasks.filter { it.assignedTo.isNotBlank() }
                                    .groupBy { it.assignedTo }
                                    .forEach { (assignee, assignedTasks) ->
                                        Text(assignee, style = MaterialTheme.typography.titleSmall)
                                        assignedTasks.forEach { task ->
                                            TextButton(onClick = {
                                                taskEdit = task
                                                dialog = "task"
                                            }) {
                                                Text(task.title + " · " + date(task.time, locale))
                                            }
                                        }
                                    }
                                OutlinedButton(onClick = { gate("member") }) {
                                    Text(stringResource(R.string.member_name))
                                }
                            }
                            item {
                                Text(
                                    stringResource(R.string.privacy_body),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                            item {
                                OutlinedButton(onClick = { dialog = "exportData" }) {
                                    Text(stringResource(R.string.export_data))
                                }
                            }
                            item {
                                TextButton(onClick = { dialog = "deleteAll" }) {
                                    Text(
                                        stringResource(R.string.delete_data),
                                        color = MaterialTheme.colorScheme.error,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (failure)
        PawverioAlertDialog(
            onDismissRequest = { failure = false },
            title = { Text(stringResource(R.string.error_generic)) },
            confirmButton = {
                TextButton(onClick = { failure = false }) { Text(stringResource(R.string.close)) }
            },
        )
    when (dialog) {
        "pet" ->
            PetEditor(
                petEdit,
                prefs,
                locale,
                { dialog = null },
                { pet ->
                    action {
                        repository.savePet(pet)
                        pruneAttachments()
                        if (!prefs.onboarded) onboardingPet = pet
                        dialog = if (prefs.onboarded) null else "onboardingFeeding"
                        petEdit = null
                    }
                },
                { id -> dialog = "deletePet" },
                onboarding = !prefs.onboarded,
            )
        "onboardingFeeding" ->
            TaskEditor(
                existing = null,
                pets = listOfNotNull(onboardingPet),
                members = emptyList(),
                premium = false,
                locale = locale,
                dismiss = {
                    action {
                        settings.set("onboarded", true)
                        dialog = null
                    }
                },
                save = { task ->
                    action {
                        repository.saveTask(task)
                        settings.set("onboarded", true)
                        dialog = null
                        if (task.remindersEnabled && Build.VERSION.SDK_INT >= 33) {
                            notifications.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                },
                delete = {},
                onboarding = true,
            )
        "deletePet" ->
            Confirm(R.string.delete_pet_confirm, { dialog = "pet" }) {
                petEdit?.let { pet ->
                    action {
                        tasks.filter { it.petId == pet.id }.forEach { reminders.cancel(it.id) }
                        repository.deletePet(pet.id)
                        pruneAttachments()
                        dialog = null
                        petEdit = null
                    }
                }
            }
        "task" ->
            TaskEditor(
                taskEdit,
                pets,
                members,
                bill.premium,
                locale,
                { dialog = null },
                { task ->
                    if (
                        !bill.premium &&
                            ((task.kind in listOf(CareKind.MEDICATION, CareKind.VACCINATION) &&
                                tasks.none { it.id == task.id && it.kind == task.kind }) ||
                                task.recurrenceDays !in listOf(0, 1, 7))
                    )
                        dialog = "premium"
                    else
                        action {
                            repository.saveTask(task)
                            schedule(task)
                            dialog = null
                        }
                },
                { task ->
                    action {
                        reminders.cancel(task.id)
                        repository.deleteTask(task.id)
                        dialog = null
                    }
                },
            )
        "record" ->
            RecordEditor(
                recordEdit,
                pets,
                locale,
                { dialog = null },
                { record ->
                    if (
                        record.kind == CareKind.VACCINATION &&
                            !bill.premium &&
                            health.none { it.id == record.id && it.kind == record.kind }
                    )
                        dialog = "premium"
                    else
                        action {
                            repository.saveHealthRecord(record)
                            pruneAttachments()
                            reminders.cancel(record.id)
                            val due =
                                record.nextDueDate
                                    ?: if (record.kind == CareKind.VET) record.date else null
                            if (due != null && due > System.currentTimeMillis())
                                reminders.schedule(record.id, record.title, record.provider, due)
                            dialog = null
                        }
                },
                { record ->
                    action {
                        repository.deleteHealthRecord(record.id)
                        pruneAttachments()
                        reminders.cancel(record.id)
                        dialog = null
                    }
                },
            )
        "walk" ->
            EntryEditor(
                R.string.log_walk,
                pets,
                listOf(R.string.duration_minutes, R.string.distance, R.string.notes),
                { dialog = null },
            ) { pet, values, at ->
                action {
                    val minutes = parseNumber(values[0], locale)
                    require(minutes > 0 && minutes % 1.0 == 0.0 && minutes <= Int.MAX_VALUE)
                    repository.logWalk(
                        pet,
                        minutes.toInt(),
                        values[1].takeIf { it.isNotBlank() }?.let { parseNumber(it, locale) },
                        values[2],
                        at,
                    )
                    dialog = null
                }
            }
        "weight" ->
            EntryEditor(
                R.string.add_weight,
                pets,
                listOf(R.string.weight, R.string.measurements, R.string.notes),
                { dialog = null },
            ) { pet, values, at ->
                action {
                    val input = parseNumber(values[0], locale)
                    repository.saveWeight(
                        WeightEntry(
                            petId = pet,
                            weightKg = if (prefs.pounds) input / 2.2046226218 else input,
                            measurements = values[1],
                            notes = values[2],
                            date = at,
                        )
                    )
                    dialog = null
                }
            }
        "member" ->
            SimpleEditor(
                R.string.household,
                listOf(R.string.member_name, R.string.contact),
                { dialog = null },
            ) { values ->
                action {
                    repository.saveMember(HouseholdMember(name = values[0], contact = values[1]))
                    dialog = null
                }
            }
        "instructions" ->
            SimpleEditor(
                R.string.sitter_instructions,
                listOf(R.string.sitter_instructions, R.string.emergency_contact),
                { dialog = null },
                listOf(prefs.instructions, prefs.emergency),
            ) { values ->
                action {
                    settings.set("instructions", values[0])
                    settings.set("emergency", values[1])
                    dialog = null
                }
            }
        "sitter" ->
            PawverioAlertDialog(
                onDismissRequest = { dialog = null },
                title = { Text(stringResource(R.string.sitter_mode)) },
                text = {
                    Text(
                        report(),
                        Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState()),
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            exportContents = report()
                            dialog = "exportPdf"
                        }
                    ) {
                        Text(stringResource(R.string.export_pdf))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            exportContents = report()
                            dialog = "exportText"
                        }
                    ) {
                        Text(stringResource(R.string.export_text))
                    }
                },
            )
        "exportPdf" ->
            Confirm(R.string.export_confirm, { dialog = null }) {
                dialog = null
                pdfExport.launch("Pawverio.pdf")
            }
        "exportText" ->
            Confirm(R.string.export_confirm, { dialog = null }) {
                dialog = null
                textExport.launch("Pawverio.txt")
            }
        "exportData" ->
            Confirm(R.string.export_confirm, { dialog = null }) {
                dialog = null
                dataExport.launch("Pawverio.json")
            }
        "deleteAll" ->
            Confirm(R.string.delete_data_confirm, { dialog = null }) {
                action {
                    tasks.forEach { reminders.cancel(it.id) }
                    health.forEach { reminders.cancel(it.id) }
                    repository.deleteAll()
                    reminders.deleteAllData()
                    voiceText = ""
                    exportContents = ""
                    petEdit = null
                    recordEdit = null
                    taskEdit = null
                    settings.set("instructions", "")
                    settings.set("emergency", "")
                    File(base.filesDir, "attachments").deleteRecursively()
                    dialog = null
                }
            }
        "premium" ->
            PawverioAlertDialog(
                onDismissRequest = { dialog = null },
                title = { Text(stringResource(R.string.premium)) },
                text = {
                    Column {
                        Text(stringResource(R.string.premium_body))
                        if (bill.busy) Text(stringResource(R.string.purchase_pending))
                        else when (bill.status) {
                            BillingStatus.ERROR -> Text(stringResource(R.string.purchase_failed))
                            BillingStatus.CANCELLED ->
                                Text(stringResource(R.string.purchase_cancelled))
                            else -> Unit
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(bill.storePrice ?: stringResource(R.string.store_unavailable))
                        if (bill.premium) Text(stringResource(R.string.premium_active))
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { billing.purchase() },
                        enabled = bill.storePrice != null && !bill.premium && !bill.busy,
                    ) {
                        Text(stringResource(R.string.upgrade))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { billing.restore() }) {
                        Text(stringResource(R.string.restore_purchases))
                    }
                },
            )
        "voice" ->
            PawverioAlertDialog(
                onDismissRequest = {
                    voice.stop()
                    dialog = null
                },
                title = { Text(stringResource(R.string.voice_title)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            stringResource(R.string.privacy_body),
                            style = MaterialTheme.typography.bodySmall,
                        )
                        OutlinedTextField(
                            voiceText,
                            { voiceText = it },
                            label = { Text(stringResource(R.string.voice_text_hint)) },
                        )
                        if (voiceError)
                            Text(
                                stringResource(
                                    when (voiceState) {
                                        VoiceState.UNAVAILABLE -> R.string.voice_unavailable
                                        VoiceState.PERMISSION_REQUIRED -> R.string.voice_denied
                                        VoiceState.IDLE -> R.string.voice_tts_unavailable
                                        else -> R.string.voice_error
                                    }
                                )
                            )
                        if (!voice.recognitionAvailable)
                            Text(stringResource(R.string.voice_unavailable))
                        TextButton(
                            onClick = {
                                if (voice.recognitionAvailable)
                                    mic.launch(Manifest.permission.RECORD_AUDIO)
                                else {
                                    voiceState = VoiceState.UNAVAILABLE
                                    voiceError = true
                                }
                            }
                        ) {
                            Icon(Icons.Outlined.MicNone, null)
                            Text(
                                stringResource(
                                    if (listening) R.string.voice_listening
                                    else R.string.voice_start
                                )
                            )
                        }
                        if (listening)
                            TextButton(
                                onClick = {
                                    voice.stop()
                                    listening = false
                                }
                            ) {
                                Text(stringResource(R.string.voice_stop))
                            }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val intent = VoiceIntentParser().parse(voiceText, locale, pets)
                            if (
                                !bill.premium &&
                                    intent !is VoiceIntent.FeedingStatus &&
                                    intent !is VoiceIntent.CompleteFeeding &&
                                    intent != VoiceIntent.ReadToday &&
                                    intent != VoiceIntent.Unknown
                            ) {
                                dialog = "premium"
                            } else if (
                                intent is VoiceIntent.CompleteFeeding ||
                                    intent is VoiceIntent.LogWalk
                            ) {
                                pendingIntent = intent
                                dialog = "voiceConfirm"
                            } else execute(intent)
                        }
                    ) {
                        Text(stringResource(R.string.voice_execute))
                    }
                },
            )
        "voiceConfirm" ->
            Confirm(R.string.voice_confirm, { dialog = "voice" }, voiceText) {
                pendingIntent?.let { execute(it) }
                pendingIntent = null
                dialog = "voice"
            }
        "backup" ->
            PawverioAlertDialog(
                onDismissRequest = {
                    backupPassword = ""
                    dialog = null
                },
                title = { Text(stringResource(R.string.backup)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(stringResource(R.string.backup_records_only))
                        Text(stringResource(R.string.backup_password_hint))
                        OutlinedTextField(
                            backupPassword,
                            { backupPassword = it },
                            label = { Text(stringResource(R.string.backup_password)) },
                            visualTransformation =
                                androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.restore_backup), Modifier.weight(1f))
                            Switch(backupRestore, { backupRestore = it })
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { dialog = "backupConfirm" },
                        enabled = backupPassword.length >= 12,
                    ) {
                        Text(stringResource(R.string.next))
                    }
                },
            )
        "backupConfirm" ->
            Confirm(
                R.string.export_confirm,
                {
                    backupPassword = ""
                    dialog = null
                },
            ) {
                dialog = null
                if (backupRestore) backupRead.launch(arrayOf("application/octet-stream", "*/*"))
                else backupWrite.launch("Pawverio.paw")
            }
    }
}

@Composable
private fun Onboarding(
    prefs: Preferences,
    settings: Settings,
    start: () -> Unit,
    skip: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    Column(
        Modifier.fillMaxSize()
            .safeDrawingPadding()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Image(
            androidx.compose.ui.res.painterResource(R.drawable.pawverio_companions),
            null,
            Modifier.fillMaxWidth().heightIn(max = 240.dp),
        )
        Text(
            stringResource(R.string.onboarding_welcome),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(stringResource(R.string.tagline), style = MaterialTheme.typography.titleMedium)
        LanguagePicker(prefs.language) { scope.launch { settings.set("language", it) } }
        Text(
            stringResource(R.string.onboarding_routines),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(stringResource(R.string.empty_tasks_body))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.pounds), Modifier.weight(1f))
            Switch(prefs.pounds, { scope.launch { settings.set("pounds", it) } })
        }
        Text(
            stringResource(R.string.onboarding_privacy),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(stringResource(R.string.privacy_body))
        Text(stringResource(R.string.medical_notice), style = MaterialTheme.typography.bodySmall)
        Button(onClick = start, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
            Text(stringResource(R.string.get_started))
        }
        TextButton(onClick = skip) { Text(stringResource(R.string.skip)) }
    }
}

@Composable
private fun Hero(pets: List<Pet>, locale: Locale) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Teal),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(28.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    DateFormat.getDateInstance(DateFormat.FULL, locale).format(java.util.Date()),
                    color = Color.White.copy(alpha = .75f),
                    style = MaterialTheme.typography.labelLarge,
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    stringResource(R.string.tagline),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Medium,
                )
            }
            Icon(
                Icons.Outlined.Pets,
                null,
                tint = Sage,
                modifier = Modifier.padding(start = 20.dp).size(70.dp),
            )
        }
    }
}

@Composable
private fun SectionTitle(id: Int) {
    Text(
        stringResource(id),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(vertical = 4.dp),
    )
}

@Composable
private fun EmptyState(title: Int, body: Int, click: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                Icons.Outlined.Pets,
                null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp),
            )
            Text(stringResource(title), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(body))
            TextButton(onClick = click) {
                Text(
                    stringResource(
                        if (title == R.string.empty_pets) R.string.add_pet else R.string.add_task
                    )
                )
            }
        }
    }
}

@Composable
private fun PetTile(
    pet: Pet,
    prefs: Preferences,
    locale: Locale,
    modifier: Modifier,
    click: () -> Unit,
) {
    Card(onClick = click, modifier = modifier, shape = RoundedCornerShape(24.dp)) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            if (pet.photoPath != null)
                AsyncImage(
                    File(pet.photoPath),
                    stringResource(R.string.accessibility_pet_photo),
                    Modifier.size(64.dp),
                )
            else
                Image(
                    androidx.compose.ui.res.painterResource(
                        if (pet.species == "cat") R.drawable.cat_profile else R.drawable.dog_profile
                    ),
                    null,
                    Modifier.size(64.dp),
                )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    pet.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    pet.breed.ifBlank {
                        stringResource(
                            if (pet.species == "cat") R.string.cat
                            else if (pet.species == "dog") R.string.dog else R.string.other
                        )
                    },
                    style = MaterialTheme.typography.bodySmall,
                )
                pet.weightKg?.let {
                    Text(
                        weight(it, prefs.pounds, locale),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskTile(
    task: CareTask,
    pet: String,
    locale: Locale,
    complete: () -> Unit,
    edit: () -> Unit,
    snooze: () -> Unit,
    pause: () -> Unit,
) {
    Card(
        onClick = edit,
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    when (task.kind) {
                        CareKind.FEEDING -> Icons.Outlined.Restaurant
                        CareKind.WALKING -> Icons.Outlined.DirectionsWalk
                        CareKind.MEDICATION -> Icons.Outlined.Medication
                        CareKind.VET -> Icons.Outlined.LocalHospital
                        else -> Icons.Outlined.Event
                    },
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 16.dp).size(30.dp),
                )
                Column(Modifier.weight(1f)) {
                    Text(task.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        pet + " · " + date(task.time, locale),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    if (task.dosage.isNotBlank()) Text(task.dosage)
                    if (task.assignedTo.isNotBlank()) Text(task.assignedTo)
                    if (task.paused) Text(stringResource(R.string.paused))
                }
                if (!task.paused)
                    IconButton(onClick = complete) {
                        Icon(
                            Icons.Outlined.CheckCircleOutline,
                            stringResource(R.string.complete),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
            }
            Row {
                TextButton(onClick = pause) {
                    Text(stringResource(if (task.paused) R.string.resume else R.string.pause))
                }
                if (!task.paused)
                    TextButton(onClick = snooze) { Text(stringResource(R.string.snooze)) }
            }
        }
    }
}

@Composable
private fun WeightChart(name: String, entries: List<WeightEntry>, pounds: Boolean, locale: Locale) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Text(name, style = MaterialTheme.typography.titleMedium)
            val min = entries.minOf { it.weightKg }
            val max = entries.maxOf { it.weightKg }
            val color = MaterialTheme.colorScheme.primary
            Canvas(Modifier.fillMaxWidth().height(120.dp).padding(vertical = 15.dp)) {
                if (entries.size > 1) {
                    entries.zipWithNext().forEach { pair ->
                        drawLine(
                            color,
                            androidx.compose.ui.geometry.Offset(
                                size.width *
                                    ((pair.first.date - entries.first().date).toDouble() /
                                            (entries.last().date - entries.first().date)
                                                .coerceAtLeast(1))
                                        .toFloat(),
                                size.height *
                                    (1 -
                                            ((pair.first.weightKg - min) /
                                                (max - min).coerceAtLeast(1.0)))
                                        .toFloat(),
                            ),
                            androidx.compose.ui.geometry.Offset(
                                size.width *
                                    ((pair.second.date - entries.first().date).toDouble() /
                                            (entries.last().date - entries.first().date)
                                                .coerceAtLeast(1))
                                        .toFloat(),
                                size.height *
                                    (1 -
                                            ((pair.second.weightKg - min) /
                                                (max - min).coerceAtLeast(1.0)))
                                        .toFloat(),
                            ),
                            strokeWidth = 5f,
                        )
                    }
                }
            }
            Text(weight(entries.last().weightKg, pounds, locale))
            Text(
                date(entries.first().date, locale) + " → " + date(entries.last().date, locale),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                weight(min, pounds, locale) + " – " + weight(max, pounds, locale),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun SettingsPanel(
    prefs: Preferences,
    settings: Settings,
    run: (suspend () -> Unit) -> Unit,
    open: (String) -> Unit,
    notifications: () -> Unit,
) {
    val context = LocalContext.current
    val lock = remember { DeviceLock(context) }
    var locked by remember { mutableStateOf(lock.enabled) }
    val reminder = remember { ReminderPreferences(context) }
    var enabled by remember { mutableStateOf(reminder.enabled) }
    var quiet by remember { mutableStateOf(reminder.quietEnabled) }
    var quietStart by remember { mutableStateOf(reminder.quietStart.toString()) }
    var quietEnd by remember { mutableStateOf(reminder.quietEnd.toString()) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LanguagePicker(prefs.language) { run { settings.set("language", it) } }
        listOf(
                Triple(R.string.dark_theme, prefs.dark, "dark"),
                Triple(R.string.high_contrast, prefs.highContrast, "high_contrast"),
                Triple(R.string.pounds, prefs.pounds, "pounds"),
                Triple(R.string.spoken_feedback, prefs.spoken, "spoken"),
            )
            .forEach { (id, value, key) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val label = stringResource(id)
                    Text(label, Modifier.weight(1f))
                    Switch(
                        value,
                        { run { settings.set(key, it) } },
                        modifier = Modifier.semantics { contentDescription = label },
                    )
                }
            }
        OutlinedButton(onClick = notifications) {
            Text(stringResource(R.string.notification_permission))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.notifications), Modifier.weight(1f))
            Switch(
                enabled,
                {
                    enabled = it
                    reminder.enabled = it
                },
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.quiet_hours), Modifier.weight(1f))
            Switch(
                quiet,
                {
                    quiet = it
                    reminder.quietEnabled = it
                },
            )
        }
        if (quiet) {
            Text(stringResource(R.string.quiet_hours_body))
            Field(R.string.quiet_start_hour, quietStart) {
                quietStart = it
                it.toIntOrNull()
                    ?.takeIf { hour -> hour in 0..23 }
                    ?.let { hour -> reminder.quietStart = hour }
            }
            Field(R.string.quiet_end_hour, quietEnd) {
                quietEnd = it
                it.toIntOrNull()
                    ?.takeIf { hour -> hour in 0..23 }
                    ?.let { hour -> reminder.quietEnd = hour }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.app_lock), Modifier.weight(1f))
            Switch(
                locked,
                {
                    locked = it
                    lock.enabled = it
                    var owner: Context = context
                    while (
                        owner is android.content.ContextWrapper && owner !is android.app.Activity
                    ) owner = owner.baseContext
                    (owner as? android.app.Activity)?.window?.let { window ->
                        if (it) window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
                        else window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
                    }
                },
                enabled = lock.available,
            )
        }
        if (!lock.available)
            Text(
                stringResource(R.string.lock_unavailable),
                style = MaterialTheme.typography.bodySmall,
            )
        OutlinedButton(onClick = { open("instructions") }) {
            Text(stringResource(R.string.sitter_instructions))
        }
        OutlinedButton(onClick = { open("backup") }) { Text(stringResource(R.string.backup)) }
    }
}

@Composable
private fun LanguagePicker(current: String, change: (String) -> Unit) {
    val deviceLanguageLabel = stringResource(R.string.device_language)
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Icon(Icons.Outlined.Language, null)
            Spacer(Modifier.width(8.dp))
            Text(
                stringResource(R.string.language) +
                    ": " +
                    (if (current.isBlank()) stringResource(R.string.device_language)
                    else languages.find { it.first == current }?.second ?: current)
            )
        }
        DropdownMenu(expanded, { expanded = false }, modifier = Modifier.heightIn(max = 350.dp)) {
            languages.forEach { (tag, name) ->
                DropdownMenuItem(
                    text = { Text(if (tag.isBlank()) deviceLanguageLabel else name) },
                    onClick = {
                        change(tag)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun Confirm(
    message: Int,
    dismiss: () -> Unit,
    details: String? = null,
    confirm: () -> Unit,
) {
    PawverioAlertDialog(
        onDismissRequest = dismiss,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(message))
                details?.let { Text(it, style = MaterialTheme.typography.titleMedium) }
            }
        },
        confirmButton = {
            TextButton(onClick = confirm) { Text(stringResource(R.string.confirm)) }
        },
        dismissButton = { TextButton(onClick = dismiss) { Text(stringResource(R.string.cancel)) } },
    )
}

private fun date(value: Long, locale: Locale) =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale)
        .format(java.util.Date(value))

private fun weight(kg: Double, pounds: Boolean, locale: Locale) =
    NumberFormat.getNumberInstance(locale)
        .apply { maximumFractionDigits = 2 }
        .format(if (pounds) kg * 2.2046226218 else kg) + if (pounds) " lb" else " kg"

internal fun parseNumber(text: String, locale: Locale): Double {
    val position = java.text.ParsePosition(0)
    val result = NumberFormat.getNumberInstance(locale).parse(text.trim(), position)
    require(result != null && position.index == text.trim().length && result.toDouble().isFinite())
    return result.toDouble()
}

private fun java.io.InputStream.readBytesLimited(max: Int): ByteArray {
    val output = java.io.ByteArrayOutputStream()
    val chunk = ByteArray(8192)
    while (true) {
        val count = read(chunk)
        if (count < 0) break
        require(output.size() + count <= max)
        output.write(chunk, 0, count)
    }
    return output.toByteArray()
}
