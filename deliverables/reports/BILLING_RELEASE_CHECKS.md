# Billing and release checks — 2026-11-05

The app and Premium entitlement remain saved drafts. Neither Submit App nor Submit IAP was clicked. The console’s Ready to Submit status reflects required form fields, not production readiness.

| Check | Status | Evidence / limit |
|---|---|---|
| Amazon ENTITLED SKU matches app | PASS | pawverio.premium.lifetime; actual catalog export validated |
| One-time GBP4.99 base price and regional calculation | PASS | Saved draft; 12 marketplace values in IAP_DRAFT_RESULT.json |
| Nine purchase-dialog translations persist correctly | PASS | Readback after reload and exported values match source; all native reviews pending |
| Transparent IAP icons in all nine locales | PASS | All18 downloaded after reload, exact RGBA pixel equality to source; IAP_ARTWORK_VALIDATION.json |
| Debug/release build and Android lint | PASS | app-tester-fix-build.log, BUILD SUCCESSFUL |
| JVM entitlement/recurrence/crypto checks | PASS | 15 platform tests; 178 core tests also remain valid with unchanged core source |
| Billing callback behavior on Fire | PASS | Ten tests with zero failures or errors, using injected callbacks |
| APK signing, package and ZIP alignment | PASS | Same dedicated production certificate; signature v2/v3, API26+ |
| Native ELF static 16KiB alignment | PASS | All eight libraries: LOAD alignment plus file/virtual-address congruence; no runtime claim |
| Updated signed APK in Amazon draft | PASS | Pawverio-1.0.0-fe0d7e21.apk, versionCode1; 12 Fire selections retained |
| Current debug update on physical Fire | PASS | Matching debug signature, install -r succeeded, process present; data preserved |
| Visible current-debug UI launch | NOT TESTED | Fire is PIN-locked; host am-start wait interrupted, no PIN bypass or data clearing |
| Exact production-signed install and launch | PASS | The draft APK installed and reached its first rendered activity on the Fire after an authorized temporary swap. Live billing remains a separate check. See FIRE_RELEASE_CHECKS.md. |
| 16KiB device runtime | NOT TESTED | Static alignment is a separate check |
| App Tester sandbox product/purchase/cancel/restore | PASS | US catalog price, checkout and cancellation, fulfillment, cache-free restore, revocation and forced failure retest on KFTRWI |
| Live DevTest / real product and transactions | BLOCKED | Amazon requires a submitted live IAP; owner requires everything to remain unsubmitted |
| Server-side RVS verification | BLOCKED | No backend/secret deployment or live receipt available; no RVS secret embedded in APK |
| Voice, human translations, full accessibility and extra-device checks | BLOCKED | Existing release report remains applicable; no untested feature certified |

## Fixed billing failures

Purchases recovered through purchase updates now receive fulfillment acknowledgment after the complete ledger is applied. A store-service acknowledgment exception is contained, delivered access is retained, and subsequent restore retries acknowledgment. Duplicate restore requests do not reset an in-flight paginated history. Request IDs isolate old pages and failures from a new restore/account. Failed identity refresh prevents a late purchase response granting access against stale identity. Canceled/wrong-SKU receipts are not fulfilled.

Ten device tests cover missing and blank account IDs on purchase failures, full-page acknowledgment, fulfillment retry, offline cache preservation on restore failure, direct-purchase fulfillment failure, late identity callbacks, duplicate restore pagination, previous-account failure isolation and stale-page rejection. These test-only SDK response builders and gateway are not part of a production fake-purchase path. They do not prove provider receipt validation or real checkout.

## Receipt verification boundary

The production path uses Amazon SDK3.0.9 and its app-specific public authentication key. Local entitlement checks require current Amazon identity, exact SKU, ENTITLED product type, non-canceled receipt and receipt identity. The SDK performs its own receipt-signature handling; successful real signature validation has not been demonstrated here. Local preferences are an offline cache, not tamper-proof server authority. Server-side RVS is NOT IMPLEMENTED. Amazon recommends verifying receipt IDs from a backend before fulfillment. A backend would require secret management, hosting and an updated privacy assessment; no shared secret was downloaded, committed or embedded. This unresolved commercial/security decision is not disguised as a completed live test.

## App Tester run on 5 November 2026

Amazon App Tester was already installed on the KFTRWI Fire tablet with overlay permission allowed. The run used the catalog exported in submission/amazon.sdktester.json. The app displayed US$6.70 for the tester's existing US marketplace. The checkout window belonged to com.amazon.sdktestclient while the device sandbox property was debug.

Checkout cancellation left Premium locked. A completed sandbox purchase activated Premium, and App Tester reported the receipt as FULFILLED. After removing the entitlement preference created by this run and restarting Pawverio, Premium became active again through purchase updates. Canceling that receipt in App Tester and tapping Restore purchases in Pawverio removed Premium access.

A forced FAILED response initially closed checkout without a message. The fix accepts failure responses with missing or blank user IDs and still requires a matching account for successful purchases and already-purchased responses. The paywall also hides the previous error while a retry is in progress.

The final debug build showed the retry message for a forced failure and kept Premium locked. Returning the override to Default let checkout open again. Screenshots are in ../android/build-logs/app-tester-sandbox/.

The original catalog and sandbox property were restored after testing. The restored catalog matched the baseline SHA-256 checksum. The Purchase API response was set back to Default. The test-generated Pawverio entitlement and SDK preference files were removed. App Tester retained the canceled Pawverio test receipt after it processed the cancellation. Other apps' transactions were left intact.

## Primary references checked

- [Amazon IAP item creation, pricing and export](https://developer.amazon.com/docs/in-app-purchasing/iap-create-and-submit-iap-items.html)
- [DevTest prerequisites: live IAP](https://developer.amazon.com/docs/app-testing/get-started-appstore-devtest-iap.html)
- [App Tester installation and catalog](https://developer.amazon.com/docs/in-app-purchasing/iap-install-and-configure-app-tester.html)
- [App Tester debug-only environment](https://developer.amazon.com/docs/in-app-purchasing/iap-app-tester-user-guide.html)
- [Receipt Verification Service](https://developer.amazon.com/docs/in-app-purchasing/iap-rvs-for-android-apps.html)
- [SDK implementation and fulfillment](https://developer.amazon.com/docs/in-app-purchasing/iap-implement-iap.html)

No real purchase, IAP submission, app review submission, publishing or new tester access was performed.
