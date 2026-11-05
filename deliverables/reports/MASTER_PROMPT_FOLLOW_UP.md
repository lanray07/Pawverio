# Master prompt follow-up

The repository now targets Pawverio 1.0.1, version code 2. Amazon now saves Pawverio-1.0.1-signed.apk as version code 2 in place of 1.0.0. The app and Premium IAP are unsubmitted drafts. The console retained 12 selected Fire tablets.

| Master prompt area | Follow-up |
|---|---|
| Onboarding | The guided flow saves a pet profile, then offers an optional feeding routine with once, daily or weekly recurrence. Reminders start off and can be enabled while creating the routine. A user can skip the routine or the entire guide. Setup uses the production Room repository and DataStore settings. |
| Household care | The task editor can use names from the household roster. Settings groups saved assignments by assignee and links back to each task, including existing assignments when Premium access is unavailable. All records stay on the device. |
| Accessibility and localisation | Settings offers a persisted high-contrast theme in light and dark modes. The loading screen uses static text when Android animations are disabled. The new theme label has draft translations in all 19 application locales. |
| Existing core features, assets and store files | See ACCEPTANCE_CRITERIA.csv for the earlier tests and artifact evidence. The new source and candidate build require their own verification. |

## Verification

- The exact signed 1.0.1 APK passed a clean-install smoke test on KFTRWI / API 28. Its installed SHA-256 matched the file uploaded to Amazon. Guided setup saved a fictional pet and daily feeding routine, both persisted after force-stop and relaunch, and completing the feeding added it to History. The original debug installation, both profile registrations, owner backup file contents and temporary device setting were restored and verified. See [signed Fire test log](../android/build-logs/pawverio-1.0.1-signed-fire.log).

- Debug and release builds passed, along with Android lint and the core and platform JVM test tasks. The signed APK uses the existing production certificate and passed signature, ZIP alignment, native ELF alignment and payload integrity checks. See the [debug APK](../android/debug/Pawverio-1.0.1-debug.apk), [signed candidate](../android/release/Pawverio-1.0.1-signed.apk) and [signing evidence](SIGNED_CANDIDATE_1.0.1.json).
- Three Compose instrumentation tests passed on the KFTRWI running API 28. They exercised first-pet setup and daily feeding, reminder opt-in defaults, activity recreation, skipping setup, opening a household assignment, and saving the high-contrast setting. The separate QA package was removed afterward. Both profiles retained their original Pawverio and instrumentation packages. See [Fire test log](../android/build-logs/pawverio-1.0.1-fire-ui.log).
- Localisation validation passed for 180 strings per locale and all 11 pipeline tests passed. The existing metadata and 304 screenshot compositions passed the structural store asset validator. Native review and any screenshot updates for the candidate are separate.

## Open release requirements

| Requirement | Status | Dependency or next check |
|---|---|---|
| Live Premium purchases and restore | BLOCKED | Amazon App Tester covered the draft SKU. Production checkout, cancellation, restore and revocation require the relevant Amazon test environment. |
| Amazon server receipt verification | BLOCKED | Amazon RVS needs an HTTPS backend and the owner’s server-side shared secret. The app currently validates SDK responses locally and retains an offline entitlement cache. |
| Live push-to-talk and audible multilingual feedback | BLOCKED | The connected KFTRWI has no Android recognition service. English audio synthesis was verified separately. Recognition and audible output need supported speech providers and device testing. |
| Native-language review and Amazon artwork approval | BLOCKED | The full target-locale screenshot and metadata sets are drafts. Native reviews and the remaining Amazon listing locales and artwork need review before submission. |
| Accessibility and device range | NOT TESTED | VoiceView, full RTL form navigation, long-text and contrast review, physical Android phone compatibility and reminders through sleep and reboot need dedicated checks. |
| Provider privacy and third-party notices | BLOCKED | The saved privacy questionnaire and dependency license inventory need final owner/provider and packaged notice review. |

Amazon documents its separate sandbox and production receipt environments and the server-side validation step. [Implement Appstore SDK IAP](https://developer.amazon.com/docs/in-app-purchasing/iap-implement-iap.html)
