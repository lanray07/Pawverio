"""Materialise separate app, listing, distribution, speech and review matrices."""
import csv
import json
import re
from pathlib import Path
from importlib.util import spec_from_file_location,module_from_spec
ROOT=Path(__file__).resolve().parents[1]
spec=spec_from_file_location('pipeline',ROOT/'scripts/localization.py');p=module_from_spec(spec);spec.loader.exec_module(p)

def csvfile(name,rows):
    with (ROOT/'localization'/name).open('w',encoding='utf-8',newline='') as fh:
        writer=csv.DictWriter(fh,fieldnames=list(rows[0]));writer.writeheader();writer.writerows(rows)

def main():
    app=[];store=[];recognition=[];tts=[];keywords=[]
    evidence_root=ROOT/'deliverables/android/build-logs/platform-device-tests'
    recognition_log=evidence_root/'logcat-com.pawverio.platform.PlatformDeviceTest-deviceVoiceCapabilityAndPermissionFallback.txt'
    tts_log=evidence_root/'logcat-com.pawverio.platform.PlatformDeviceTest-ttsLocaleCapabilityReport.txt'
    recognition_source=recognition_log.read_text(encoding='utf-8') if recognition_log.exists() else ''
    tts_source=tts_log.read_text(encoding='utf-8') if tts_log.exists() else ''
    measured_device=re.search(r'DEVICE model=([^;]+); api=(\d+); recognitionAvailable=(true|false)',recognition_source)
    measured_tts={locale:int(code) for locale,code in re.findall(r'TTS ([\w-]+) availability=(-?\d+)',tts_source)}
    device=f'{measured_device.group(1)} / API {measured_device.group(2)}' if measured_device else 'NOT TESTED'
    tts_initialized='TTS initialization=0' in tts_source
    for locale in p.LOCALES:
        app.append({'locale':locale,'resource_directory':p.qualifier(locale),'catalog_entries':len(p.dictionary(locale)),'human_review':'PENDING','layout_review':'NOT TESTED','rtl_required':locale=='ar','advertised_fully_supported':False})
        family=locale.split('-')[0]
        if locale=='es-419':listing='es-MX';evidence='Spanish Mexico shown in documentation console screenshot; verify current console'
        elif locale=='fr-CA':listing='fr';evidence='French documented; separate French Canada listing not documented'
        elif family in ['ar','hi','tr','id']:listing='English fallback';evidence='Requested language not in documented listing options'
        else:listing=locale;evidence='Documented language; verify exact console selector'
        store.append({'app_locale':locale,'proposed_listing_selector':listing,'evidence':evidence,'draft_metadata':f'deliverables/store-assets/metadata/{locale}.json','approved_listing_locale':False,'human_review':'PENDING','console_selection':'NOT TESTED'})
        unavailable=bool(measured_device and measured_device.group(3)=='false')
        recognition.append({'locale':locale,'tested_device':device,'recognition_engine_language':'UNAVAILABLE: no SpeechRecognizer service on tested device' if unavailable else 'RUNTIME DETECTION REQUIRED','availability_query':'PASS: recognitionAvailable=false' if unavailable else 'NOT TESTED','speech_input_execution':'BLOCKED: no installed recognition service' if unavailable else 'NOT TESTED','microphone_permission':'REQUEST ON PUSH TO TALK; no recording/audio recognition executed','offline_recognition':'NOT CLAIMED','touch_and_text_fallback':'IMPLEMENTED; device validation tracked separately','evidence':str(recognition_log.relative_to(ROOT)) if measured_device else ''})
        code=measured_tts.get(locale)
        support={1:'LANG_COUNTRY_AVAILABLE',0:'LANG_AVAILABLE: language fallback; exact regional voice not established',-2:'LANG_NOT_SUPPORTED'}.get(code,'NOT TESTED')
        tts.append({'locale':locale,'tested_device':device,'engine':'com.ivona.tts.oem' if tts_initialized else 'NOT TESTED','initialization':'PASS: SUCCESS (0)' if tts_initialized else 'NOT TESTED','availability_code':code if code is not None else 'NOT TESTED','tts_language':support,'availability_query':'PASS' if code is not None else 'NOT TESTED','audio_output_test':'NOT TESTED','fallback':'DISPLAY TEXT','offline_tts':'NOT CLAIMED','evidence':str(tts_log.relative_to(ROOT)) if code is not None else ''})
        d=p.dictionary(locale)
        for key,intent in [('feeding','feeding reminder'),('walking','walking routine'),('grooming','grooming routine'),('vet','vet appointment'),('medication','medication log'),('vaccination','vaccination record'),('weight_history','weight history'),('sitter_mode','pet sitter information')]:
            keywords.append({'locale':locale,'localized_search_term':d[key],'user_intent':intent,'volume':'NOT MEASURED','research_basis':'User brief plus editorial localization; not a keyword volume tool','validation':'PENDING native search-intent review'})
        review=ROOT/'localization/reviews'/f'{locale}.json'
        if not review.exists():p.write(review,{'status':'PENDING_HUMAN_REVIEW','provenance':'AI-authored editorial draft','reviewer':None,'layout_test':'NOT TESTED'})
    csvfile('application-language-matrix.csv',app);csvfile('store-listing-language-matrix.csv',store);csvfile('voice-recognition-language-matrix.csv',recognition);csvfile('text-to-speech-language-matrix.csv',tts);csvfile('keyword-intent-matrix.csv',keywords)
    countries=[('US','amazon.com'),('GB','amazon.co.uk'),('AU','amazon.com.au'),('ES','amazon.es'),('MX','amazon.com.mx'),('FR','amazon.fr'),('CA','amazon.ca'),('DE','amazon.de'),('IT','amazon.it'),('BR','amazon.com.br'),('PT','amazon.es'),('NL','amazon.de'),('PL','VERIFY CONSOLE'),('SE','VERIFY CONSOLE'),('JP','amazon.co.jp'),('IN','amazon.in'),('SA','VERIFY CONSOLE'),('TR','VERIFY CONSOLE'),('ID','VERIFY CONSOLE')]
    csvfile('distribution-country-matrix.csv',[{'candidate_country':country,'documented_or_candidate_marketplace':market,'availability_selected':'NOT CONFIGURED','fire_device_eligibility':'VERIFY IN DEVELOPER CONSOLE','legal_distribution_review':'PENDING','note':'Language does not imply country availability; this is a candidate list, not an exhaustive country registry.'}for country,market in countries])
    print('Wrote separate application/listing/country/recognition/TTS and keyword matrices. No listing locale approved.')

if __name__=='__main__':main()
