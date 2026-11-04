# Pawverio — Pet Care & Daily Planner

Native Kotlin/Compose, offline-first pet care for Fire tablets and compatible Android devices. No account, ads, mandatory Google services, GPS tracking, or record upload.

**Release status: BLOCKED pending verification and store setup.** See [release readiness](deliverables/reports/RELEASE_READINESS.md) for the evidence-backed decision. This repository must not be described as ready for submission until that report passes.

## Build and run

Requires JDK17, Android SDK35, Build Tools35.0.0 and the checked-in Gradle8.13 wrapper. Set `ANDROID_HOME` or create an ignored `local.properties` with `sdk.dir=...`. On Windows:

```powershell
.\gradlew.bat :app:assembleDebug :app:assembleRelease
.\gradlew.bat :core:testDebugUnitTest :platform:testDebugUnitTest :app:lintDebug
$env:ANDROID_SERIAL='<authorised test device>'
.\gradlew.bat :core:connectedDebugAndroidTest :platform:connectedDebugAndroidTest
.\gradlew.bat :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.pawverio.app.CareJourneyTest
python scripts/localization.py validate
python -m unittest scripts/localization_test.py
```

Install `app/build/outputs/apk/debug/app-debug.apk` with `adb install -r`. Release build is deliberately **unsigned** until the owner supplies a production signing key. Never distribute a debug or QA-signed artifact as the production app.

## Architecture

- `app/`: adaptive Compose interface, `CareViewModel`, DataStore preferences, onboarding, care editors, settings and coordinated platform flows.
- `core/`: Room entities/DAO/schema, repository, transactional completion/undo, calendar recurrence, explicit multilingual voice intents, validated JSON backup/restore. Local user text is never translated.
- `platform/`: native SpeechRecognizer/TTS, WorkManager notifications, Amazon SDK purchasing, native Unicode PDF/text exports, PBKDF2/AES-GCM backup encryption and device credential lock.
- `localization/`: English catalog,19 locale drafts, glossary/cache, review tracking, separate app/store/distribution/voice/TTS matrices.
- `scripts/`: resource generation/validation, original asset generation, real-device screenshot capture and editable compositions.
- `deliverables/`: APK copies, verification logs, artwork, metadata drafts, screenshot packages and release reports.

Repositories expose Flow and persist data in Room. The UI reads actual stored state. Task completion, history and recurrence updates are one transaction; undo preserves the original occurrence. Calendar recurrence follows local clock time through daylight-saving changes. Snooze changes notification time without rewriting the user’s medication schedule. WorkManager reminders are inexact and may be delayed by Fire OS power management.

## Product and privacy

Pet profiles, photographs, feeding/walking/grooming/custom routines, assignments, health records and documents, vaccinations and weight history are stored in the app sandbox. Existing health data stays readable when Premium is unavailable. Free data export remains available. Deleting pet data also clears private media and sitter contact/instruction settings. Exported files belong to the user and cannot be recalled by app deletion.

Backup format is versioned JSON encrypted using PBKDF2-HMAC-SHA256(210,000 iterations) and AES256-GCM with fresh salt/nonce. **Photos and documents are excluded from the portable backup**, clearly disclosed in the UI. Restore validates the entire input before transactional merge, strips external paths, and preserves existing local media references. Record IDs merge rather than delete unrelated current records. Passwords are never stored; forgotten passwords cannot be recovered.

Voice is push-to-talk with preview and confirmation for mutations. Typed commands and all touch controls remain available when speech recognition or the selected TTS voice is missing. Installed speech providers may process audio under their own terms; offline recognition is not promised. No raw audio is retained by Pawverio.

The app records user-entered veterinary instructions; it does not calculate feeding quantities, doses, vaccine schedules, diagnoses or treatment.

## Monetization

Amazon SDK3.0.9, entitlement SKU `pawverio.premium.lifetime`, one-time unlock. The suggested £4.99 is a pricing hypothesis; the UI displays the store price only. Configure the actual product and Amazon-issued app authentication public key before testing. No Google Play Billing and no simulated purchase success. Amazon sandbox purchase verification, refunds/account changes/restore, and receipt-verification assessment are release blockers. See [platform integration](deliverables/reports/PLATFORM_INTEGRATIONS.md).

## Localization and screenshots

Target locales: en-GB, en-US, en-AU, es-ES, es-419, fr-FR, fr-CA, de-DE, it-IT, pt-BR, pt-PT, nl-NL, pl-PL, sv-SE, ja-JP, hi-IN, ar, tr-TR, id-ID. Resource coverage is measured; all translations are drafts until approved. Voice-engine availability is a different matrix from command-parser coverage.

`scripts/capture_screenshots.py --serial <device>` runs debug-only instrumentation with fictional stored data, captures eight actual same-locale screens, removes the fixtures and restores orientation preferences. The narrow phone layout uses an app-only native360×640dp viewport on the Fire device; it is not evidence of phone hardware testing. The script refuses to run if the device already has display overrides. `scripts/compose_screenshots.py` generates PNG compositions and editable HTML artwork templates from those real captures, then writes a SHA256 manifest. Native-language/visual/store review remains mandatory.

Amazon Appstore no longer distributes to generic Android mobile devices; primary distribution here is compatible Fire tablets. See [verified marketplace research](deliverables/reports/marketplace-research.md) for supported listing language and artwork requirements. Do not treat all app locales as approved Amazon listing locales.

## Release

Follow [release instructions](deliverables/reports/RELEASE_INSTRUCTIONS.md), complete the evidence checklist, supply owner support/privacy details and sign with the owner’s production key. No publishing or submission is authorized by this repository.


