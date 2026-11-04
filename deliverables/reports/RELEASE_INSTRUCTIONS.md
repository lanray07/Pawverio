# Release instructions

The verified production-signed APK has been uploaded to an unsubmitted Amazon draft. No app review submission, publication or production deployment has occurred.

1. Review RELEASE_READINESS.md and acceptance evidence. Fix any FAIL and complete BLOCKED/NOT TESTED requirements before claiming ready.
2. Supply the developer’s support email, publisher/legal identity and privacy contact; have the privacy drafts reviewed and host the final policy at a real reachable URL. No fabricated contacts are supplied.
3. Configure Amazon app/package `com.pawverio.app`, compatible Fire devices and entitled product `pawverio.premium.lifetime`. Upload the correct app authentication public key to the private build’s assets as `AppstoreAuthenticationKey.pem`. This public verification file is distinct from Amazon developer secrets. Follow current Amazon SDK documentation in the marketplace/platform reports.
4. Test product lookup, actual localized price, purchase success/cancel/fail/pending, restore, refunds, offline cache, account changes and purchase validation using Amazon App Tester/LAT and the correctly configured signing/package identity. No test should be replaced by fabricated entitlement state.
5. Have native reviewers approve the translation and screenshot review matrices. Check the current console’s supported listing languages and distribution markets. App-language coverage does not imply a marketplace listing exists.
6. Re-run `gradlew :app:assembleDebug :app:assembleRelease :core:testDebugUnitTest :platform:testDebugUnitTest :app:lintDebug` and connected tests on the intended Fire range. Test airplane mode, restart, denied microphone and notifications, unavailable speech services, long text, Arabic RTL, landscape, purchase errors and exports. Listen to actual TTS and speak commands on supported engines; parser tests alone are insufficient.
7. Use an owner-controlled production signing key. Generate/store it securely outside the repository if one does not already exist. Back it up; losing it may prevent app updates. Do not use a different app’s existing keystore or the debug key.
8. Produce the unsigned release APK with `gradlew :app:assembleRelease`. Run SDK `zipalign -p -f 4 app-release-unsigned.apk aligned.apk`, then `apksigner sign --ks <private-keystore> --ks-key-alias <owner-alias> --out Pawverio-release.apk aligned.apk`. Let apksigner prompt for passwords; never commit command lines containing them. Run `apksigner verify --verbose --print-certs Pawverio-release.apk` and `Get-FileHash -Algorithm SHA256` (or sha256sum).
9. Install the signed release on test devices and repeat release-critical flows. Verify Amazon eligibility and current submission requirements in the developer console. Check requested permissions, compatibility and privacy disclosures.
10. Request the owner’s explicit approval for the final concrete submission package. Only then upload/submit. A GitHub push does not authorize publishing to Amazon.

## Operational simplicity

There is no hosted backend or analytics collector. CI builds and checks the repository; device tests still require authorized hardware. SQLite/Room data and private files rely on Android’s app sandbox and device storage protection. Portable backups are user initiated and authenticated/encrypted. No automatic cloud backup is enabled.

Keep dependencies current through reviewed updates. Retest Fire OS scheduling after OS updates. Monitor store crash reports after launch only under the developer account’s chosen privacy policy. No external monitoring, paid service or account configuration is silently provisioned.

## Current signing and draft status

The dedicated Pawverio key is owner-local, outside Git, and its password is Windows CurrentUser DPAPI protected. Use `scripts/sign_release.ps1`; preserve the key and arrange private portable recovery before changing machines. The public Amazon authentication key is packaged and is not a private signer. See `submission/SIGNING_AND_UPLOAD_NOTES.txt`, `SIGNED_RELEASE.json`, and the submission draft records. Exact signed-device runtime, live purchasing, native review and legal export certification remain separate requirements. Do not uninstall the owner’s debug app merely to bypass a certificate conflict.
