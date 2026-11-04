# Pawverio core

`PawverioDatabase.open(context)` opens the local Room database without destructive migration fallback. Version 1 schema is exported in `schemas/` during compilation. Pet, task, completion, health, weight, and household tables live in this module. No network is used.

`CareRepository(database)` exposes observable Room flows and validated suspend writes. `completeTask()` records history and advances calendar recurrence in one transaction. `undoCompletion()` restores the original scheduled occurrence when no later occurrence supersedes it. Editing a pet uses Room Upsert, preserving child records. Deleting a pet cascades its records; deleting all records also removes household members. The app layer owns file cleanup and settings reset.

`Recurrence.nextAfter(scheduledAt, days, after, zone)` advances the local calendar date and retains local wall-clock time across daylight-saving transitions. Missed reminders do not create a backlog. This helper does not recommend medical schedules.

`VoiceIntentParser().parse(text, locale, pets)` produces structured read/query/navigation intents or mutations that the app must preview and confirm. The parser never writes the database. A missing language dictionary yields Unknown. Dictionaries cover the requested language families, with regional fallback. Dictionary presence is not evidence of speech-engine availability, linguistic human review, or device speech testing. Numeric duration recognition is supported in each dictionary; English also has selected spoken numbers. Unknown or ambiguous durations fall back to touch/text. No raw audio is stored.

Tests: `:core:testDebugUnitTest`; Android persistence tests: `:core:connectedDebugAndroidTest`. Execution outcomes are recorded in top-level deliverables, not inferred from test source existence.
