# Fire release checks, 5 November 2026

The artifact checked was Pawverio-1.0.0-fe0d7e21.apk, SHA-256 fe0d7e21e3cc206b61240bd35bddee67d487ab48563eef047e2dc51c2bf7d427. The tablet was a KFTRWI running Android API 28.

| Check | Result | Evidence |
|---|---|---|
| Install exact production-signed APK | PASS | Installed clean on KFTRWI / API 28 after an authorized temporary swap. Read-back SHA-256 matched the Amazon draft artifact. |
| Signed app first activity | PASS | ActivityManager recorded creation, resume and first-frame launch completion. The repeat test confirmed Pawverio held window focus after launch. |
| Signed onboarding interaction and persistence | NOT TESTED | UIAutomator did not produce a readable hierarchy on either run. The Fire keyguard was showing after the repeat attempt. |
| Signed artifact integrity | PASS | APK Signature Scheme v2 and v3, 16 KiB ZIP alignment, eight native libraries with static 16 KiB segment alignment, and 75 matching non-signature payload entries against the unsigned build. The Amazon public key matches the source asset. |
| Release manifest and QA hooks | PASS | Automatic backup disabled. No INTERNET or location permission declared. The launcher is exported and other exported SDK and AndroidX components require Amazon notification, BIND_JOB_SERVICE or DUMP permissions. The qa_route and qa_capture intent keys are absent from packaged DEX. |
| Runtime dependency advisory lookup | PASS | The OSV Maven query returned zero advisory matches across 120 resolved release-runtime coordinates on the date above. The lookup covers those coordinates and available OSV data. |
| Dependency license declarations | PASS | All 120 coordinates have license declarations. Local and official POMs, including the Guava parent POM, declare Apache 2.0 for 119 coordinates and Amazon Program Materials License for one. See RELEASE_LICENSE_INVENTORY.json. |
| Packaged third-party notices | BLOCKED | The APK contains okhttp3/internal/publicsuffix/NOTICE. The declaration inventory does not establish redistribution or notice completeness. |
| Scheduled notification | PASS | A real delayed WorkManager job posted the expected notification without invoking the worker directly. The device was awake and the instrumentation process remained alive. |
| English speech synthesis | PASS | The installed engine completed file synthesis and MediaExtractor read an audio sample from its output. Audible quality, live recognition and additional locale voices remain open. |

The supplemental notification and speech tests used the isolated platform instrumentation package. See the XML under ../android/build-logs/fire-supplemental-device-tests/. Sleep, reboot, Doze, VoiceView and the production-signed app's runtime are separate checks.

A private owner-profile backup includes the installed debug APK and the persistent application files. The owner Room database and WAL passed SQLite integrity checking. Android reports the second profile has never launched Pawverio, but its data directory could not be opened for backup. The authorized temporary swap restored the exact debug APK, all backed-up owner files by content hash, and package registration in both profiles. The second profile had no credential-encrypted app data inode before the swap and its prior private files could not be independently verified. The backup remains outside the repository.

The Amazon console shows the app and Premium IAP as unsubmitted drafts. The app draft contains the same hash-labeled APK and retains 12 Fire tablet selections. Live IAP and production receipt verification remain open. Sandbox billing results are in BILLING_RELEASE_CHECKS.md.

- [Android signing requirements for updates](https://developer.android.com/google/play/app-updates)
- [OSV batch API](https://google.github.io/osv.dev/post-v1-querybatch/)

## Follow-up checks

The GitHub Android workflow passed for ce5d1a7. The dedicated Android API 36 QA emulator could not boot because Windows hardware acceleration was unavailable and its software-only fallback exited before ADB connected. Neither emulator attempt changed the Fire tablet. The subsequent physical Fire test verified installation and first activity render. See ../android/build-logs/fire-signed-runtime.log.

- [GitHub Android workflow](https://github.com/lanray07/Pawverio/actions/runs/41984164721)
