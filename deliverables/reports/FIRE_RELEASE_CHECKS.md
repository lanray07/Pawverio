# Fire release checks, 5 November 2026

The artifact checked was Pawverio-1.0.0-fe0d7e21.apk, SHA-256 fe0d7e21e3cc206b61240bd35bddee67d487ab48563eef047e2dc51c2bf7d427. The tablet was a KFTRWI running Android API 28.

| Check | Result | Evidence |
|---|---|---|
| Install exact production-signed APK | BLOCKED | Android returned INSTALL_FAILED_UPDATE_INCOMPATIBLE for the in-place attempt. The installed debug and production certificates differ. The package is installed in two profiles. No uninstall occurred. |
| Signed artifact integrity | PASS | APK Signature Scheme v2 and v3, 16 KiB ZIP alignment, eight native libraries with static 16 KiB segment alignment, and 75 matching non-signature payload entries against the unsigned build. The Amazon public key matches the source asset. |
| Release manifest and QA hooks | PASS | Automatic backup disabled. No INTERNET or location permission declared. The launcher is exported and other exported SDK and AndroidX components require Amazon notification, BIND_JOB_SERVICE or DUMP permissions. The qa_route and qa_capture intent keys are absent from packaged DEX. |
| Runtime dependency advisory lookup | PASS | The OSV Maven query returned zero advisory matches across 120 resolved release-runtime coordinates on the date above. The lookup covers those coordinates and available OSV data. |
| Dependency licensing metadata | BLOCKED | Cached POMs declare Apache 2.0 for 93 coordinates and Amazon Program Materials License for one. Another 26 coordinates lack cached POMs. The APK contains okhttp3/internal/publicsuffix/NOTICE. This inventory does not establish redistribution or notice completeness. |
| Scheduled notification | PASS | A real delayed WorkManager job posted the expected notification without invoking the worker directly. The device was awake and the instrumentation process remained alive. |
| English speech synthesis | PASS | The installed engine completed file synthesis and MediaExtractor read an audio sample from its output. Audible quality, live recognition and additional locale voices remain open. |

The supplemental notification and speech tests used the isolated platform instrumentation package. See the XML under ../android/build-logs/fire-supplemental-device-tests/. Sleep, reboot, Doze, VoiceView and the production-signed app's runtime are separate checks.

A private owner-profile backup includes the installed debug APK and the persistent application files. The owner Room database and WAL passed SQLite integrity checking. Android reports the second profile has never launched Pawverio, but its data directory could not be opened for backup. A temporary swap requires explicit approval because it deletes the current installations and their app data. The private backup is outside the repository.

The Amazon console shows the app and Premium IAP as unsubmitted drafts. The app draft contains the same hash-labeled APK and retains 12 Fire tablet selections. Live IAP and production receipt verification remain open. Sandbox billing results are in BILLING_RELEASE_CHECKS.md.

- [Android signing requirements for updates](https://developer.android.com/google/play/app-updates)
- [OSV batch API](https://google.github.io/osv.dev/post-v1-querybatch/)
