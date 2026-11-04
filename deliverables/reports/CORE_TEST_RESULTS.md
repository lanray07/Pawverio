# Core test results

**PASS: 178 JVM tests and 14 physical Fire repository/parser tests; zero failures, errors or skipped cases.** Evidence was copied from actual Gradle-generated XML on 4 November 2026. No new build or device action was performed when packaging these results.

| Suite | Executed | Failed | Evidence directory |
|---|---:|---:|---|
| Backup validation and round-trip | 10 | 0 | `../android/build-logs/core-tests/` |
| Calendar recurrence | 4 | 0 | `../android/build-logs/core-tests/` |
| Voice parser edge cases | 12 | 0 | `../android/build-logs/core-tests/` |
| Localized text command fixtures | 152 | 0 | `../android/build-logs/core-tests/` |
| Physical Fire Room/repository/parser suite | 14 | 0 | `../android/build-logs/core-device-tests/` |

Device evidence identifies model KFTRWI, Android 9/API 28, and contains fictional test data only. Only focused JUnit XML was copied; no general device logs, other-app records, account contents, or device inventory was included.

The device tests verify pet CRUD, database reopen, child-record preservation, completion/undo, concurrency, pause/resume, snooze clock preservation, medication date bounds/expiry, walk dates, health records, weight ordering, household members, full logical deletion, and portable backup merge/refusal. The JVM suite tests 19 locale tags across eight command intents; this establishes text parsing, **not live multilingual speech recognition or TTS availability**. UI flows, accessibility, Amazon purchase success and store submission remain separate requirements.

Earlier failed concurrent builds and stale-class parser results are superseded by the final passing XML dated 12:12 UTC (JVM) and 12:15 UTC (repository device suite).

The final physical suite also verifies Indonesian command parsing with Android’s legacy `in` locale alias. All 14 tests passed.
