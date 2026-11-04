# Core domain verification

Verified on 4 November 2026 from final serialized XML: **178 JVM tests PASS and 13 physical-device repository tests PASS, with 0 failures, errors or skipped cases**. Device: Amazon Fire KFTRWI, Android API 28. Final JVM suite timestamps are 12:12 UTC; device repository suite timestamp is 12:15 UTC. Earlier concurrent/stale builds are superseded by these results.

| Domain behavior | Implementation | Final domain evidence |
|---|---|---|
| Pet CRUD, editing preserves child records | Room Upsert and cascading foreign keys | PASS: physical RepositoryTest, restart/edit/delete |
| Feeding/routine completion and history | Transactional occurrence recording with duplicate protection | PASS: physical RepositoryTest, completion/undo and duplicate protection |
| Calendar recurrence and daylight-saving clock | Explicit ZoneId and calendar days | PASS: RecurrenceTest, 4 tests |
| Pause/resume and snooze | Snooze changes override only, preserving recurrence clock | PASS: physical RepositoryTest, prescribed clock |
| Concurrent completion | Transaction plus future-day/paused guards | PASS: physical RepositoryTest, two coroutines yield one occurrence |
| Medication start/end | Reject reminder outside user dates; final occurrence pauses without creating an unprescribed occurrence | PASS: physical RepositoryTest, invalid dates leave prescription unchanged; expiry and undo |
| Manual walk duration/distance/date | Validated records, no GPS | PASS: physical RepositoryTest; historical date persists, future date refused |
| Vet and vaccine records | Local user-entered dates, provider and attachment reference | PASS: physical RepositoryTest, edit/delete/restart |
| Weight history | Positive finite kilograms, chronological queries | PASS: physical RepositoryTest, invalid weight refusal, chronological order and deletion |
| Household members | Local member table | PASS: physical RepositoryTest, creation/restart/deletion; complete assignment UI is separate |
| Deletion survives restart | Atomic pet/member deletion, child cascades | PASS: physical RepositoryTest |
| Voice text intent parsing | All 19 locale tags resolve language dictionaries; unknown/ambiguous commands fail safely | PASS: 12 VoiceIntentTest cases and 152 localized fixtures (19 locales × 8 intents) |
| Actual microphone and speech playback | Owned by platform module | NOT TESTED here; parser tests do not verify speech engines |
| Portable JSON backup | All entity tables, validation before transactional merge; IDs preserved | PASS: 10 BackupCodecTest cases plus physical restore/restart/refusal tests |

Copied evidence: `../deliverables/android/build-logs/core-tests/` contains four JVM suite XML files (10 backup + 4 recurrence + 12 parser + 152 fixture cases). `../deliverables/android/build-logs/core-device-tests/TEST-com.pawverio.core.RepositoryTest.xml` contains all 13 physical-device cases. No unrelated device logs or personal records were copied.

These are scoped domain results, not a 100% project acceptance rate. Full Compose journeys, accessibility, live speech, store purchases, and submission approval require separate evidence.

Backup data excludes photos, attachments and platform settings; app layer handles those separately. Restore does not resolve incoming filesystem paths and retains existing same-ID local photo/attachment references. Import validates 10 MiB UTF-8 payload size, nesting, 20,000 total records, unique IDs, required pet references, known care kinds, positive finite weights, numeric ranges and task schedule dates before writing. Invalid snapshots leave existing database contents unchanged. Store billing, notifications, screen localization and screenshots require separate parent/platform evidence.

No migration beyond schema version 1 is claimed; version 1 JSON schema is generated at `schemas/com.pawverio.core.PawverioDatabase/1.json`. No destructive fallback is configured. Exported records are private and must receive app-layer sensitive-sharing confirmation and encryption where requested.
