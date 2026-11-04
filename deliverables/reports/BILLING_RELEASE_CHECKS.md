# Billing and release checks — 2026-11-04

The app and Premium entitlement remain saved drafts. Neither Submit App nor Submit IAP was clicked. The console’s Ready to Submit status reflects required form fields, not production readiness.

| Check | Status | Evidence / limit |
|---|---|---|
| Amazon ENTITLED SKU matches app | PASS | pawverio.premium.lifetime; actual catalog export validated |
| One-time GBP4.99 base price and regional calculation | PASS | Saved draft; 12 marketplace values in IAP_DRAFT_RESULT.json |
| Nine purchase-dialog translations persist correctly | PASS | Readback after reload and exported values match source; all native reviews pending |
| Transparent IAP icons in all nine locales | PASS | All18 downloaded after reload, exact RGBA pixel equality to source; IAP_ARTWORK_VALIDATION.json |
| Debug/release build and Android lint | PASS | billing-release-checks.log, BUILD SUCCESSFUL |
| JVM entitlement/recurrence/crypto checks | PASS | 15 platform tests; 178 core tests also remain valid with unchanged core source |
| Billing callback behavior on Fire | PASS | Eight tests, zero failures/errors; injected callbacks, NOT real transactions |
| APK signing, package and ZIP alignment | PASS | Same dedicated production certificate; signature v2/v3, API26+ |
| Native ELF static 16KiB alignment | PASS | All eight libraries: LOAD alignment plus file/virtual-address congruence; no runtime claim |
| Updated signed APK in Amazon draft | PASS | Pawverio-1.0.0-b1165b97.apk, versionCode1; 12 Fire selections retained |
| Current debug update on physical Fire | PASS | Matching debug signature, install -r succeeded, process present; data preserved |
| Visible current-debug UI launch | NOT TESTED | Fire is PIN-locked; host am-start wait interrupted, no PIN bypass or data clearing |
| Exact production-signed runtime | BLOCKED | Existing Fire uses different debug certificate. Fresh API36 emulator cannot launch with missing hypervisor; software-only emulator exits1 before ADB is available. No OS security/virtualisation settings changed |
| 16KiB device runtime | NOT TESTED | Static alignment is a separate check |
| App Tester sandbox product/purchase/cancel/restore | BLOCKED | Installed and overlay already allowed; temporary debug sandbox-mode authorisation pending |
| Live DevTest / real product and transactions | BLOCKED | Amazon requires a submitted live IAP; owner requires everything to remain unsubmitted |
| Server-side RVS verification | BLOCKED | No backend/secret deployment or live receipt available; no RVS secret embedded in APK |
| Voice, human translations, full accessibility and extra-device checks | BLOCKED | Existing release report remains applicable; no untested feature certified |

## Fixed billing failures

Purchases recovered through purchase updates now receive fulfillment acknowledgment after the complete ledger is applied. A store-service acknowledgment exception is contained, delivered access is retained, and subsequent restore retries acknowledgment. Duplicate restore requests do not reset an in-flight paginated history. Request IDs isolate old pages and failures from a new restore/account. Failed identity refresh prevents a late purchase response granting access against stale identity. Canceled/wrong-SKU receipts are not fulfilled.

Eight device tests cover full-page acknowledgment, fulfillment retry, offline cache preservation on restore failure, direct-purchase fulfillment failure, late identity callbacks, duplicate restore pagination, previous-account failure isolation and stale-page rejection. These test-only SDK response builders and gateway are not part of a production fake-purchase path. They do not prove provider receipt validation or real checkout.

## Receipt verification boundary

The production path uses Amazon SDK3.0.9 and its app-specific public authentication key. Local entitlement checks require current Amazon identity, exact SKU, ENTITLED product type, non-canceled receipt and receipt identity. The SDK performs its own receipt-signature handling; successful real signature validation has not been demonstrated here. Local preferences are an offline cache, not tamper-proof server authority. Server-side RVS is NOT IMPLEMENTED. Amazon recommends verifying receipt IDs from a backend before fulfillment. A backend would require secret management, hosting and an updated privacy assessment; no shared secret was downloaded, committed or embedded. This unresolved commercial/security decision is not disguised as a completed live test.

## Safe test preparation

The actual Amazon catalog export is submission/amazon.sdktester.json. The connected Fire already has com.amazon.sdktestclient and SYSTEM_ALERT_WINDOW=allow. The baseline debug.amazon.sandboxmode is none. No sandbox changes or synthetic transactions were performed. If authorised, use App Tester with the debug build only, record every scenario separately, restore the original sandbox mode, and ensure synthetic Premium access cannot be mistaken for a real paid entitlement. Do not uninstall the user's app or clear its records to reset tests.

## Primary references checked

- [Amazon IAP item creation, pricing and export](https://developer.amazon.com/docs/in-app-purchasing/iap-create-and-submit-iap-items.html)
- [DevTest prerequisites: live IAP](https://developer.amazon.com/docs/app-testing/get-started-appstore-devtest-iap.html)
- [App Tester installation and catalog](https://developer.amazon.com/docs/in-app-purchasing/iap-install-and-configure-app-tester.html)
- [App Tester debug-only environment](https://developer.amazon.com/docs/in-app-purchasing/iap-app-tester-user-guide.html)
- [Receipt Verification Service](https://developer.amazon.com/docs/in-app-purchasing/iap-rvs-for-android-apps.html)
- [SDK implementation and fulfillment](https://developer.amazon.com/docs/in-app-purchasing/iap-implement-iap.html)

No real purchase, IAP submission, app review submission, publishing or new tester access was performed.
