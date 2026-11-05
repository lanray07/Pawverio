# Fire platform compatibility

Tested physical model: **Amazon KFTRWI**, Android **API 28**. Date: **2026-11-04**. No other Fire model is certified by this report.

Evidence: `deliverables/android/build-logs/platform-device-tests/` copied from the connected Android test output; suite timestamp 2026-11-04T12:16:44, ten tests / zero failures / zero errors / zero skipped. Root UI/Room test evidence is separate.

| Check | Result | Tested scope |
|---|---|---|
| Native platform integration runtime | PASS | Ten instrumentation methods completed |
| Recognition capability detection | PASS | Reports unavailable instead of assuming support |
| Android microphone recognition | BLOCKED | No installed Android recognition service detected |
| Microphone permission fallback | PASS | No-permission request returns a recoverable state |
| TTS initialization and language inventory | PASS | IVONA engine initialized; 19 actual capability results recorded |
| Audible TTS output and pronunciation | NOT TESTED | Capability does not establish speech playback |
| Real local notification post and dismissal | PASS | Direct worker invocation and NotificationManager state |
| Persisted future reminder work | PASS | WorkManager query identifies pending unique work |
| Idempotent scheduling | PASS | Same parameters preserve request UUID and generation |
| Cancelled recurring-work resurrection prevention | PASS | Stale generation cannot enqueue new work |
| Disabled-reminder fallback | PASS | Worker succeeds without posting |
| Medication end-date guard | PASS | Expired course produces no notification or recurrence |
| Reminder data deletion | PASS | Cancel/prune persisted work; clear app generation/fingerprint prefs |
| Fire OS timed background/Doze/reboot/force-stop delivery | NOT TESTED | No scheduled wake-up or reboot test performed |
| UTF-8 text export | PASS | English/Japanese/Arabic content-URI round-trip |
| PDF structural validity | PASS | Four-page PDF opens with PdfRenderer, 595×842 points |
| Complex-script PDF visual layout | NOT TESTED | Structural parsing is insufficient for visual approval |
| Password-encrypted backup runtime | PASS | UTF-8 round-trip and wrong-password rejection |
| Records-only backup full UI workflow | NOT TESTED | Tested encryption primitive; root app tests are separate |
| Missing Amazon key behavior | PASS | Store unavailable without starting vendor authentication |
| Amazon purchase/restore/price/receipt verification | BLOCKED | Public key, draft SKU and App Tester coverage verified. Live purchase, receipt verification and signed runtime remain open. |
| Android API33 notification-permission denial | NOT TESTED | Tested device is API28 |
| Optional device-lock PIN removal recovery | NOT TESTED | Guard implemented; no device PIN manipulation performed |

Notification tests exercised the actual Android notification API but invoked the worker directly; they do not establish precise or long-term delivery. Core app data and reminders require no mandatory Google Play Services. Universal Fire compatibility, universal voice availability, exact-alarm delivery and Amazon purchasing readiness are not claimed.

## Final verification update

Final copied XML supersedes the earlier ten-test baseline: 11 platform and 14 core physical tests passed. The additional checks cover automatic/manual notification locale selection and Indonesian parser locale aliases. The final APK passed 3 real aeroplane-mode UI journeys, 1 Spanish dialog regression and 2 private-attachment boundary tests. All 304 captures passed independent label/frame guards: 19 locales, eight screens, Fire tablet and app-only compact viewport. The compact viewport is not phone hardware certification. Full accessibility, audible TTS and long-term background delivery remain NOT TESTED.

## Production-signing and console targeting update

A separate dedicated production certificate now signs the release. The existing KFTRWI debug app/data were preserved. Exact production-signed runtime remains NOT TESTED. Amazon auto-filtering exposes 12 compatible Fire tablet entries, with Fire TV/Automotive unselected; this is a console compatibility result, not physical certification of those 12. Older incompatible Fire models, Alexa/Amazon Kids tablet families and Fire Phone remain disabled. Microphone hardware is explicitly optional.

Current debug artifact passive smoke: matching-debug-certificate in-place update succeeded, preserving existing data. The launcher activity started and the app process remained alive. No pet records were mutated, and no install/uninstall workaround was used for the production certificate conflict. This is not an end-to-end test of the production-signed APK. See android/build-logs/current-debug-startup-smoke.log.
