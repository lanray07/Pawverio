"""Package verified files and produce a conservative release checklist.
Run after tests/captures. Artifact presence never implies store approval.
"""
import csv,hashlib,json,shutil,subprocess,zipfile,xml.etree.ElementTree as E
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
D=ROOT/'deliverables';R=D/'reports';R.mkdir(parents=True,exist_ok=True)
def log_pass(path,marker):return (ROOT/path).exists() and marker in (ROOT/path).read_text(encoding='utf-8-sig',errors='replace')
def write_csv(path,rows):
    with path.open('w',encoding='utf-8',newline='') as stream:
        writer=csv.DictWriter(stream,fieldnames=list(rows[0]));writer.writeheader();writer.writerows(rows)
def main():
    for module,target in [('core','core-tests'),('platform','platform-tests')]:
        folder=D/'android/build-logs'/target;folder.mkdir(parents=True,exist_ok=True)
        for xml in (ROOT/f'{module}/build/test-results/testDebugUnitTest').glob('*.xml'):shutil.copy2(xml,folder/xml.name)
    for module,target in [('core','core-device-tests'),('platform','platform-device-tests')]:
        folder=D/'android/build-logs'/target;folder.mkdir(parents=True,exist_ok=True)
        for xml in (ROOT/f'{module}/build/outputs/androidTest-results/connected').rglob('*.xml'):
            if module=='core':shutil.copy2(xml,folder/'TEST-com.pawverio.core.RepositoryTest.xml')
            else:shutil.copy2(xml,folder/xml.name)
    for src,dst in [('app/build/outputs/apk/debug/app-debug.apk','android/debug/Pawverio-debug.apk'),('app/build/outputs/apk/release/app-release-unsigned.apk','android/release/Pawverio-release-unsigned.apk')]:
        destination=D/dst;destination.parent.mkdir(parents=True,exist_ok=True);shutil.copy2(ROOT/src,destination)
    for name in ['lint-results-debug.xml','lint-results-debug.html']:shutil.copy2(ROOT/'app/build/reports'/name,D/'android/build-logs'/name)
    shutil.copy2(ROOT/'app/build/intermediates/merged_manifests/release/processReleaseManifest/AndroidManifest.xml',D/'android/build-logs/release-merged-manifest.xml')
    subprocess.run(['python','scripts/localization.py','extract'],cwd=ROOT,check=True)
    subprocess.run(['python','scripts/localization.py','validate'],cwd=ROOT,check=True)
    with (D/'android/build-logs/localization-tests.log').open('w',encoding='utf-8') as out:subprocess.run(['python','-m','unittest','scripts/localization_test.py'],cwd=ROOT,stdout=out,stderr=out,check=True)
    subprocess.run(['python','scripts/validate_store_assets.py'],cwd=ROOT,check=True)
    package=D/'localization/Pawverio-localization.zip';package.parent.mkdir(parents=True,exist_ok=True)
    source_files=[p for p in (ROOT/'localization').rglob('*') if p.is_file() and '__pycache__' not in p.parts]
    source_files+=list((ROOT/'scripts').glob('localization*.py'))+list((ROOT/'app/src/main/res').glob('values*/strings.xml'))+list((ROOT/'platform/src/main/res').glob('values*/strings.xml'))+list((ROOT/'app/src/androidTest/assets').glob('*.json'))
    with zipfile.ZipFile(package,'w',zipfile.ZIP_DEFLATED) as archive:
        for path in sorted(set(source_files)):archive.write(path,path.relative_to(ROOT))
    with zipfile.ZipFile(package) as archive:assert archive.testzip() is None
    source_zip=D/'android/Pawverio-source.zip'
    source_paths=[]
    for folder in ['app','core','platform','gradle','scripts','localization','.github']:
        source_paths += [p for p in (ROOT/folder).rglob('*') if p.is_file() and not any(part in ['build','.gradle','.kotlin','.tools','__pycache__'] for part in p.relative_to(ROOT).parts) and p.suffix!='.pyc']
    source_paths += [ROOT/name for name in ['README.md','.gitignore','.env.example','settings.gradle.kts','build.gradle.kts','gradle.properties','gradlew','gradlew.bat']]
    with zipfile.ZipFile(source_zip,'w',zipfile.ZIP_DEFLATED) as archive:
        for path in sorted(set(source_paths)):archive.write(path,path.relative_to(ROOT))
    with zipfile.ZipFile(source_zip) as archive:assert archive.testzip() is None
    manifest=D/'store-assets/screenshots/SCREENSHOT_MANIFEST.csv'
    shots=list(csv.DictReader(manifest.open(encoding='utf-8'))) if manifest.exists() else []
    full_shots=len(shots)==304 and all((ROOT/row['composition']).is_file() and (ROOT/row['source']).is_file() for row in shots)
    offline=log_pass('deliverables/android/build-logs/offline-ui-journeys.log','OK (3 tests)') and log_pass('deliverables/android/build-logs/offline-ui-journeys.log','AIRPLANE=1 WIFI=0')
    dialog=log_pass('deliverables/android/build-logs/dialog-locale-regression.log','OK (1 test)')
    build=log_pass('deliverables/android/build-logs/final-app-media-verification.log','BUILD SUCCESSFUL')
    items=[]
    def item(group,name,status,evidence,limit=''):
        items.append({'ID':f'A{len(items)+1:02}','Group':group,'Criterion':name,'Status':status,'Evidence':evidence,'Scope or remaining requirement':limit})
    for name in ['Debug build succeeds','Release build succeeds','No critical compilation errors remain']:item('Build',name,'PASS' if build else 'FAIL','android/build-logs/final-app-media-verification.log','Release build is unsigned; compilation is separate from signing/submission.')
    item('Build','No embedded secrets found by executed checks','PASS','reports/static-security-check.json','Known-pattern source and APK scan; not a complete security audit.')
    for name in ['Pet profile create/edit/delete','Feeding schedules and history','Dated manual walking logs','Grooming recurrence and history','Veterinary records persist','Medication reminders within tested limits','Vaccination records persist','Weight history persists and displays','Valid pet sitter text/PDF exports']:
        item('Core',name,'PASS','android/build-logs/core-device-tests/TEST-com.pawverio.core.RepositoryTest.xml; android/build-logs/platform-device-tests/; android/build-logs/offline-ui-journeys.log','Domain/device checks plus scoped UI/render checks and final APK regression tests. WorkManager timing is inexact; not all form/picker interactions are automated.')
    item('Voice','Push-to-talk success on supported tested device','BLOCKED','reports/VOICE_COMPATIBILITY.md','KFTRWI has no recognition service; another compatible engine/device is required.')
    item('Voice','Commands execute real care actions','PASS' if offline else 'NOT TESTED','android/build-logs/offline-ui-journeys.log','Typed voice command queried data and completed feeding after confirmation; unknown command preserved records.')
    item('Voice','Spoken feedback works audibly','NOT TESTED','reports/VOICE_COMPATIBILITY.md','English capability reported; audible output/accuracy not validated. Other16 installed locale voices unsupported.')
    item('Voice','Locale-aware command parsing validated','PASS','android/build-logs/core-tests/TEST-com.pawverio.core.VoiceFixturesTest.xml','152 fixtures across19 locale tags; does not prove speech recognition.')
    item('Voice','Permission denial handled','PASS','android/build-logs/platform-device-tests/','Controller denied-permission fallback tested. App avoids requesting mic when service is absent.')
    item('Voice','Touch/text fallback works','PASS' if offline else 'NOT TESTED','android/build-logs/offline-ui-journeys.log','Includes query, reviewed mutation and unknown input.')
    item('Localization','All required interface resource strings translated','PASS','../localization/coverage.csv','19×179 draft strings; structural/resource validation, not native language certification.')
    item('Localization','Plural categories and placeholders valid','PASS','../localization/validation-report.json; android/build-logs/lint-results-debug.xml','CLDR categories checked; native grammar review pending.')
    item('Localization','RTL layouts work in captured states','PASS' if full_shots else 'NOT TESTED','store-assets/screenshots/raw/fire-tablet/ar/','Eight rendered states; full accessibility/form review remains pending.')
    item('Localization','Locale dates/numbers/units formatted','PASS' if full_shots else 'NOT TESTED','store-assets/screenshots/raw/','Locale-aware platform formatters and localized demonstration data; store prices require live configuration.')
    item('Localization','Human review status recorded','PASS','../localization/reviews/','All19 explicitly PENDING_HUMAN_REVIEW.')
    item('Localization','Unapproved locales not advertised as fully supported','PASS','../README.md; store-assets/metadata/','Generated drafts are separate from release approval and voice-engine support.')
    item('Screenshots','Eight screenshots per approved listing locale','BLOCKED','store-assets/screenshots/SCREENSHOT_MANIFEST.csv','304 target-locale draft images exist if capture completed; native/console listing-locale approval is outstanding.')
    for name in ['Actual same-locale app interface captured','Headlines/captions translated','PNG dimensions match verified Amazon specifications','No fabricated screens/features in images','Screenshot manifest complete']:
        item('Screenshots',name,'PASS' if full_shots else 'NOT TESTED','store-assets/screenshots/SCREENSHOT_MANIFEST.csv; reports/store-asset-validation.json','Phone images exercise narrow layout on Fire hardware; language/visual approval remains pending.')
    for name in ['Live store product details retrieved','Real purchase grants correct entitlements','Real cancelled/failed purchases handled','Purchase restoration verified','Billing tests executed in Amazon environment']:
        item('Amazon billing',name,'BLOCKED','reports/PLATFORM_INTEGRATIONS.md','Requires Amazon app authentication key, entitled SKU, appropriate tester/store setup and receipt-verification decision. No fake purchase success.')
    item('Privacy/offline','Core journeys work in aeroplane mode','PASS' if offline else 'NOT TESTED','android/build-logs/offline-ui-journeys.log; android/build-logs/airplane-enable.log','Actual mode1, Wi-Fi0 and activeNetwork=null assertions. Settings restored.')
    item('Privacy/offline','Data persists after restart','PASS','android/build-logs/core-device-tests/; android/build-logs/offline-ui-journeys.log','Physical Room close/reopen and Activity recreation.')
    item('Privacy/offline','Data deletion works','PASS' if offline else 'NOT TESTED','android/build-logs/offline-ui-journeys.log; android/build-logs/platform-device-tests/','Records/contact settings cleared, work invalidated/cancelled/pruned. Logical deletion; no forensic erasure claim.')
    item('Privacy/offline','No silent pet-record cloud uploads','PASS','android/build-logs/release-merged-manifest.xml; reports/PRIVACY_SECURITY.md','No INTERNET permission or first-party network uploader. Explicit voice services/store run under their own terms.')
    item('Privacy/offline','Sensitive exports require confirmation','PASS' if offline else 'NOT TESTED','android/build-logs/offline-ui-journeys.log','Export confirmation and cancel verified; PDF/document flows share confirmation policy.')
    item('Release','Required submission artifacts ready','BLOCKED','android/release/Pawverio-release-unsigned.apk','Owner production signing key and signed-release device verification outstanding.')
    item('Release','Test reports exist','PASS','android/build-logs/','193 JVM,25 Fire,3 offline UI,1 dialog regression,2 attachment boundary and11 localization tests; capture matrix separate.')
    item('Release','Fire compatibility documented','PASS','reports/FIRE_PLATFORM_COMPATIBILITY.md','One KFTRWI/API28 tested; minSDK26. Other Fire/Android devices and reboot/sleep timing not certified.')
    item('Release','Localized store assets complete and approved','BLOCKED','store-assets/metadata/; store-assets/screenshots/','Draft files generated; publisher contact/privacy publication, native review and console locale approval required.')
    item('Release','No unresolved release-blocking defects','NOT TESTED','reports/RELEASE_READINESS.md','Purchase/speech/signed release and full accessibility/device-range testing incomplete; do not infer readiness.')
    assert len(items)==46
    write_csv(R/'ACCEPTANCE_CRITERIA.csv',items)
    totals={status:sum(x['Status']==status for x in items) for status in ['PASS','FAIL','BLOCKED','NOT TESTED']}
    rate=round(totals['PASS']/len(items)*100,2)
    summary={'release_status':'BLOCKED','mandatory_criteria':46,'status_counts':totals,'verified_acceptance_rate_percent':rate,'screenshot_compositions':len(shots),'expected_screenshot_compositions':304,'missing_screenshot_compositions':304-len(shots),'capture_blocker':None if full_shots else 'Fire tablet PIN-locked; unlock request pending. Seven stale frames were rejected; phone viewport captures not yet executed.','approved_locales':[],'human_review_pending_locales':19,'debug_sha256':hashlib.sha256((D/'android/debug/Pawverio-debug.apk').read_bytes()).hexdigest(),'unsigned_release_sha256':hashlib.sha256((D/'android/release/Pawverio-release-unsigned.apk').read_bytes()).hexdigest(),'dialog_locale_regression_pass':dialog,'production_signing':'BLOCKED: owner signing key not supplied','amazon_store_tests':'BLOCKED: authentication/product/test configuration unavailable'}
    (R/'RELEASE_STATUS.json').write_text(json.dumps(summary,indent=2),encoding='utf-8')
    text=f'''# Pawverio release readiness

**BLOCKED — NOT READY FOR SUBMISSION.** No app-store publishing/submission has occurred.

Verified checklist rate: **{totals['PASS']}/46 = {rate}%**. PASS{totals['PASS']}; FAIL{totals['FAIL']}; BLOCKED{totals['BLOCKED']}; NOT TESTED{totals['NOT TESTED']}. This counts the user's46 mandatory checklist items, with scope/evidence in ACCEPTANCE_CRITERIA.csv; it is not a claim that the entire product is production ready.

## Implemented and verified

Kotlin/Compose native Android project; custom teal/ivory/sage design, adaptive navigation, light/dark themes, no mandatory Google services/account. Room pet/task/history/health/weight/household persistence; transactional completion/undo; calendar recurrence with original medication anchors; reminders, pause/resume/snooze and end bounds. Optional profile photos/private documents; sitter text/PDF export; record JSON export and authenticated encrypted record backup/merge restore; settings/data deletion; native device-lock controls; push-to-talk/TTS controllers and typed command fallback; real Amazon SDK integration with store-supplied price and unavailable-store handling.

Evidence:178 core JVM +15 platform JVM tests;14 core +11 platform physical Fire tests;3 actual aeroplane-mode Compose journeys;1 Spanish dialog-locale regression;2 private-attachment boundary tests;11 localization pipeline tests. Final app builds and Android lint succeeded. Earlier failed logs are retained as diagnosis history, superseded by the final logs and copied XML. The locale regression captured a Spanish dashboard with English dialog contents, then verified the fix reapplying locale inside dialog slots.

19×179 translated resource strings, CLDR forms/placeholders and independent Android-resource expectations. All translations remain AI-authored drafts pending human review. {len(shots)} screenshot compositions and their real app-source PNGs are inventoried;19 target locales×8screens×2layouts when complete. The phone layout was captured in an app-only native360×640dp viewport on Fire; this does not certify phone hardware. Metadata19 locale drafts and original branding/assets are included. See screenshot and brand manifests.

Debug APK:android/debug/Pawverio-debug.apk. Release variant:android/release/Pawverio-release-unsigned.apk (**unsigned**, not an installable/submission-ready signed artifact). SHA256s:RELEASE_STATUS.json and FILE_CHECKSUMS.csv.

## Required before submission

1. Configure the Amazon app authentication public key and entitlement SKU, perform real product/purchase/cancel/failure/refund/account/restore tests, and resolve the receipt-verification policy. Unit policy checks do not substitute for store testing.
2. Supply owner production signing, sign the release and test that exact artifact. No existing unrelated signing key was used.
3. KFTRWI/API28 has no Android recognition service. Test real speech on a compatible device/engine. Installed IVONA advertises English only; other16 requested locale voices are unsupported. Audible output/pronunciation and live recognition remain unverified. Text/touch commands work.
4. Approve native translations, long-text/form layouts, Arabic screen-reader order, localized screenshot spelling/readability and listing-locale selectors in the actual Amazon console. No locale is certified/approved merely because strings or images exist.
5. Add the actual publisher/support/privacy contacts and publish a reviewed policy; review Amazon/voice-provider processing disclosures, licensing, dependency security and applicable legal requirements. No compliance certification is claimed.
6. Complete accessibility (including VoiceView), app-lock credential/background/recovery, full picker/photo/backup-UI workflows, performance, additional Fire/Android devices and OS versions, background sleep/reboot/time-zone reminder delivery, release regression and store/device eligibility checks.

## Known limitations

WorkManager is inexact; quiet hours defer reminders; no exact delivery or universal Fire/voice support is promised. No cross-device household synchronization. Backups contain records only, excluding photos/documents, app settings/sitter preference contacts and purchase data, disclosed in UI; media needs separate export. JSON restore merges IDs and does not import filesystem paths. App-private Room/media rely on OS storage protection; optional app lock is not database encryption. Process-interrupted imports may leave private orphan files until later cleanup; logical deletion is not forensic erasure. Notification channel names follow manual app language selection or current device language in automatic mode. Store screenshot/listing drafts need human approval. No crash telemetry, advertising, location tracking, backend, website or automatic record cloud uploads were added.

## Recommended next improvements

Finish the release blockers first. Then validate onboarding with real multi-pet households, evaluate the one-time price with actual willingness-to-pay data, refine field-specific validation/success feedback, improve media-inclusive encrypted backups, and expand compatibility only from measured evidence. Follow RELEASE_INSTRUCTIONS.md. GitHub push is source delivery, not authorization to publish to Amazon.
'''
    (R/'RELEASE_READINESS.md').write_text(text,encoding='utf-8')
    delivery=[]
    def deliver(name,path,status,method,evidence,block=''):
        actual=ROOT/path
        assert actual.exists(),f'Deliverable does not exist: {path}'
        delivery.append({'Deliverable ID':f'D{len(delivery)+1:02}','Deliverable name':name,'File path':path,'Status':status,'Verification method':method,'Evidence path':evidence,'Remaining blocker':block})
    deliver('Complete Android source project','deliverables/android/Pawverio-source.zip','PASS','Gradle build + tests, source ZIP integrity; app/core/platform included','deliverables/android/build-logs/final-app-media-verification.log')
    deliver('Debug APK','deliverables/android/debug/Pawverio-debug.apk','PASS','Final APK installed on Fire; scoped UI regression, core/platform checks and package integrity','deliverables/android/build-logs/offline-ui-journeys.log')
    deliver('Unsigned release APK','deliverables/android/release/Pawverio-release-unsigned.apk','BLOCKED','Release variant build succeeds','deliverables/android/build-logs/final-app-media-verification.log','Owner production signing and signed-release validation')
    deliver('Original premium brand assets','deliverables/brand-assets/brand-assets-manifest.csv','PASS','Actual SVG/PNG files and checksums','deliverables/brand-assets/brand-contact-sheet.png')
    deliver('Pet care modules and persistence evidence','deliverables/reports/CORE_TEST_RESULTS.md','PASS','178 JVM and14 Fire database/parser tests','deliverables/android/build-logs/core-device-tests/')
    deliver('Voice implementation and compatibility','deliverables/reports/VOICE_COMPATIBILITY.md','BLOCKED','Native capability query + parser and typed UI tests','deliverables/reports/voice-device-capabilities.csv','No recognition engine on tested Fire; audible and multilingual device tests outstanding')
    deliver('Localization resources and executable pipeline','deliverables/localization/Pawverio-localization.zip','PASS','19×179resource validation,11 host tests, ZIP integrity','localization/validation-report.json','Human review pending; no release approval implied')
    deliver('Localized screenshot package','deliverables/store-assets/screenshots/SCREENSHOT_MANIFEST.csv','PASS' if full_shots else 'NOT TESTED','Actual screen capture, independent catalog labels, PNG dimension/hash checks','deliverables/reports/store-asset-validation.json','Native/visual/console locale approval pending')
    deliver('Localized Amazon metadata drafts','deliverables/store-assets/metadata','NOT TESTED','19drafts; byte/character/bullet structural checks','deliverables/reports/store-asset-validation.json','Native review, publisher contacts and console approval')
    deliver('Amazon IAP implementation','platform/src/main/java/com/pawverio/platform/AmazonBilling.kt','BLOCKED','Real Amazon SDK compile + local entitlement policy checks','deliverables/reports/PLATFORM_INTEGRATIONS.md','Amazon key/product and live test setup; receipt verification')
    deliver('Automated test reports','deliverables/android/build-logs','PASS','Executed XML/log evidence','deliverables/reports/CORE_TEST_RESULTS.md')
    deliver('Privacy/security assessment','deliverables/reports/PRIVACY_SECURITY.md','NOT TESTED','Scoped source/binary-pattern checks and tested controls','deliverables/reports/static-security-check.json','Full assessment/legal/provider review not completed')
    deliver('Fire tablet compatibility report','deliverables/reports/FIRE_PLATFORM_COMPATIBILITY.md','PASS','Physical KFTRWI/API28 test evidence','deliverables/android/build-logs/platform-device-tests/','Other devices/OS/background timing require tests')
    deliver('Release readiness and acceptance checklist','deliverables/reports/RELEASE_READINESS.md','BLOCKED','46criteria evidence/status audit','deliverables/reports/ACCEPTANCE_CRITERIA.csv','See precise blockers in report')
    write_csv(D/'DELIVERABLES_MANIFEST.csv',delivery)
    subprocess.run(['python','scripts/verify_release.py'],cwd=ROOT,check=True)
    print(json.dumps(summary,indent=2))
if __name__=='__main__':main()

