# Platform integrations

## Executed build checks

2026-11-04: Gradle 8.13 `:platform:testDebugUnitTest :platform:compileDebugKotlin` completed BUILD SUCCESSFUL. Six local JVM tests passed: three authenticated encryption tests (UTF-8 round trip/random salt, tamper rejection, wrong-password rejection), two quiet-hour boundary tests, and one entitlement-policy test. XML evidence is copied to `deliverables/android/build-logs/platform-tests/`. These are policy/cryptography tests, not device microphone, scheduling delivery, or Amazon sandbox tests.

The later coordinated `verification4.log` run executed nine platform JVM tests with zero failures/errors/skips. This additionally verifies original medication-clock recurrence after snooze, London daylight-saving transition, and overdue weekly recurrence. The copied XML evidence includes `TEST-com.pawverio.platform.ReminderRecurrenceTest.xml`.

The coordinated `verification5.log` run executed ten platform instrumentation tests on the physical KFTRWI Fire tablet, Android API 28: ten passed, zero failures/errors/skips. XML timestamp is 2026-11-04T12:16:44. Copied XML and individual logcat evidence are in `deliverables/android/build-logs/platform-device-tests/`. Tested assertions cover microphone permission fallback and recognition capability, the 19-locale TTS capability inventory, UTF-8 text/four-page PDF export validity, on-device encryption, WorkManager scheduling/idempotence/cancellation/data deletion, actual notification post/dismiss, disabled-notification fallback, medication end-date guard, and missing Amazon public-key handling. TTS engine capability does not establish recognition accuracy or audible speech quality.

## Voice

`platform/.../VoiceController.kt` implements Android SpeechRecognizer push-to-talk and TextToSpeech. The Activity must request RECORD_AUDIO only after user action, display VoiceState, preview onResult before execution, and call close from onDestroy. No microphone recordings are stored. Speech recognition is delegated to the installed system service and may use a provider network service; the app must disclose that before microphone use. No offline or universal Fire support is claimed.

On the tested KFTRWI, `SpeechRecognizer.isRecognitionAvailable` returned false. Android recognition is BLOCKED on this device configuration; no recognition language is advertised as supported on it. The permission-denial/fallback check passed. TTS initialized successfully using `com.ivona.tts.oem`: en-GB returned 1 (country available), en-US and en-AU returned 0 (base-language fallback), and the remaining 16 requested locales returned -2 (language unsupported). Spoken output completion/audibility and recognition on another device remain NOT TESTED. See `VOICE_COMPATIBILITY.md` for the complete per-locale inventory. Touch/text fallback remains mandatory.

## Reminder delivery

WorkManager unique one-time work produces local notifications and reschedules optional day-based recurrence using the device time zone. Quiet hours can defer notifications to the configured end hour. POST_NOTIFICATIONS and notification enablement are checked. Reminders use no Play Services and no exact-alarm permission. Delivery is inexact and may be delayed by Fire OS battery restrictions, app force-stop, or notification denial. Physical Fire background delivery and reboot tests: NOT TESTED.

On KFTRWI/API28, direct worker execution posted a real notification with the expected title and cancellation dismissed it. WorkManager persisted future work; identical schedules retained their work UUID/generation; cancellation prevented stale-generation revival. Disabled preferences and expired medication end dates suppressed posting. App data deletion canceled and pruned persisted work and cleared generation/fingerprint preferences. These assertions passed. Timed background wake-up, force-stop, reboot and Doze delivery are separate and remain NOT TESTED.

Reminder IDs are core UUID strings. Schedule generations are persisted: cancellation or replacement invalidates the previous generation, preventing a running worker from resurrecting canceled recurring tasks. Cancel also dismisses the posted notification. Identical schedules retain their persisted generation and WorkManager request across UI observation or process restart. The optional `recurrenceAnchorMillis` parameter preserves the user-entered medication clock time after a snooze, including across daylight-saving transitions. The optional `endAtMillis` parameter prevents delivery or recurrence after the user-entered end bound.

The application must call schedule when a task changes, cancel when paused/deleted, and reschedule after timezone changes when wall-clock fidelity is required. The schedule accepts already-localized title/body. Medication dosage and recurrence values are user-entered; no dosage calculation exists.

Notification-channel resource translations exist for Spanish, French, German, Italian, Portuguese, Dutch, Polish, Swedish, Japanese, Hindi, Arabic, Turkish and Indonesian; regional variants inherit the base language. These translations have not received human review. Channel names are OS-persisted and may retain the name from their creation locale.

## Exports and backup

ExportService writes UTF-8 text and multipage PDF to a user-selected content URI. Caller must require explicit sensitive-information confirmation first. Attachments are copied to app-private files, random names, limited to 20 MB, with partial output removed on import failure. EncryptedBackup uses PBKDF2-HMAC-SHA256 (210,000 iterations), random 16-byte salt, AES-256-GCM, random 12-byte nonce, and authenticated format version. Passwords must contain at least 12 characters. The app integrates this primitive with its records-only BackupCodec; photos and documents are explicitly excluded. Full app backup/restore UI tests are separate from the platform encryption tests. The PDF writer uses Android font rendering; complex-script and RTL PDF typography require visual testing before approval.

On KFTRWI/API28, content-URI text round-trip preserved English, Japanese and Arabic. A generated PDF parsed successfully through Android PdfRenderer as four 595×842-point pages. This establishes PDF structural validity; page typography and complex-script output still require visual approval. On-device encrypted backup round-trip preserved UTF-8 and rejected a wrong password. Photos/documents remain excluded from the records-only backup advertised by the app.

## Amazon IAP

Actual pinned dependency: com.amazon.device:amazon-appstore-sdk:3.0.9. Native Amazon PurchasingService and PurchasingListener are used; no Google Play Billing dependency. Price comes exclusively from ProductData. No hardcoded commercial success, simulated receipt, or debug unlock is exposed. Purchase matches exact SKU, ENTITLED product type, non-canceled receipt, and current Amazon user. Restore paginates purchase updates and account changes invalidate the cached entitlement. A failed offline refresh preserves last granted access. Amazon reports purchase cancellation through failure statuses; failure never grants entitlement.

Suggested entitlement product ID: `pawverio.premium.lifetime`. Create this ENTITLED item in the developer console. Add the app-specific PUBLIC key `AppstoreAuthenticationKey.pem` to app/src/main/assets using Amazon's console instructions. Do not put private credentials or RVS shared secrets in the APK. Authentication key and live SKU configuration are external dependencies. Without a nonempty public-key asset, connect returns UNAVAILABLE before starting vendor authentication and preserves the cached entitlement. The application can display store-unavailable status without blocking previously stored care records.

The integration checks trusted SDK callback and receipt attributes locally. Server-side Receipt Verification Service validation is NOT IMPLEMENTED, and must not be claimed. A server verification design would require explicit privacy review because it changes the no-cloud behavior. Amazon App Tester and sandbox end-to-end product retrieval, purchase, cancellation, restoration, pending purchase, revocation, account switch and fulfillment: BLOCKED pending Amazon configuration/device environment. Pure entitlement policy tests are separate from store tests and do not establish real purchasing success.

## Primary references checked 2026-11-04

- https://developer.amazon.com/docs/appstore-sdk/integrate-appstore-sdk.html
- https://developer.amazon.com/docs/appstore-sdk/release-notes.html
- https://developer.amazon.com/docs/in-app-purchasing/iap-implement-iap.html

Amazon's integration guide requires the app-specific public key and recommends Maven Central. Release notes identify 3.0.9 as the May 20, 2026 release. No submission or account changes were performed.

## Final manifest and public-key audit — 2026-11-04

[Amazon's IAP implementation guide](https://developer.amazon.com/docs/in-app-purchasing/iap-implement-iap.html), Manifest requirements and ResponseReceiver sections, specifies package queries for Amazon Appstore/App Tester and the protected exported receiver. It does not state a blanket INTERNET permission requirement. Its getProductData section conditionally requires `android.permission.INTERNET` when displaying the downloaded IAP product icon. Pawverio displays store-provided text/price and does not load `smallIconUrl`; this documented icon requirement does not currently apply. This is documentation evidence, not proof of a successful sandbox purchase without the permission. Billing remains BLOCKED until the actual configured environment is tested.

[Amazon's SDK integration guide](https://developer.amazon.com/docs/appstore-sdk/integrate-appstore-sdk.html), Step 1, specifies `AppstoreAuthenticationKey.pem` in `app/src/main/assets`. Pawverio's preflight opens that exact filename and checks for a nonempty asset. This confirms presence only; it does not validate PEM contents, app association, receipt authenticity, or server-side RVS. Only Amazon's official app-specific public key belongs there; no private key/shared secret belongs in the APK.

A read-only constant-pool scan of the actual pinned 3.0.9 Maven JAR found no literal `android.permission.INTERNET`. Its only `java/net` reference was in ShutdownPrompt URL encoding/browser-help construction. This narrow bytecode inspection supports the manifest assessment but cannot prove all runtime paths, IPC behavior, network absence in external speech/store services, or purchasing readiness. No source, manifest, APK, account, or device configuration was changed during this audit.

## Final verification update

Final copied XML reports 15 JVM and 11 physical platform tests, all passing. Six additional receipt-ledger tests verify duplicate handling, cancellation dominance for the same receipt, later distinct repurchase, account separation and cached-entitlement preservation after incomplete updates. These local policies do not verify a real Amazon purchase or replace Receipt Verification Service validation. The additional physical test covers both explicit and automatic notification language selection.

## Signed draft update

The app-specific Amazon public authentication key is now packaged. A dedicated owner-local production signature verifies, and the APK is accepted in the unsubmitted Amazon draft. The configured SKU and real transaction/restore tests remain outstanding; signing does not prove billing success. Static SDK review identified reachable identifier, receipt, diagnostic and API-use metric paths. The conservative 39-row privacy draft is saved for review; provider retention/secondary purposes are not independently verified. See submission/PRIVACY_QUESTIONNAIRE_ASSESSMENT.txt.
