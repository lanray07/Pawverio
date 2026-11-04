"""Reproducible inventory/static checks. Does not replace device, store or human review."""
import csv, hashlib, json, re, zipfile, xml.etree.ElementTree as ET
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
def main():
    errors=[];count=0
    patterns=[r'(?i)sk-proj-[A-Za-z0-9_-]{20,}',r'AKIA[0-9A-Z]{16}',r'-----BEGIN (?:RSA |EC |OPENSSH )?PRIVATE KEY-----',r'gh[pousr]_[A-Za-z0-9]{30,}']
    for folder in ['app/src','core/src','platform/src','scripts','.github']:
        for path in (ROOT/folder).rglob('*'):
            if not path.is_file() or '.tools' in path.parts or '__pycache__' in path.parts:continue
            if path.suffix not in ['.kt','.xml','.kts','.py','.json','.yml']:continue
            text=path.read_text(encoding='utf-8-sig');count+=1
            for pattern in patterns:
                if re.search(pattern,text):errors.append({'path':str(path.relative_to(ROOT)),'check':'Potential embedded credential; inspect privately'})
    ns='{http://schemas.android.com/apk/res/android}'
    manifest=ET.parse(ROOT/'app/src/main/AndroidManifest.xml').getroot()
    app=manifest.find('application')
    if app.get(ns+'allowBackup')!='false':errors.append({'check':'Automatic Android backup must be disabled'})
    permissions=[x.get(ns+'name') for x in manifest.findall('uses-permission')]
    if any('LOCATION' in name for name in permissions):errors.append({'check':'Unexpected location permission'})
    apks=[]
    for apk in (ROOT/'deliverables/android').rglob('*.apk'):
        scanned=0
        with zipfile.ZipFile(apk) as archive:
            for entry in archive.infolist():
                name=entry.filename.lower()
                if name.endswith(('.jks','.keystore','/.env')):errors.append({'path':str(apk.relative_to(ROOT)),'entry':entry.filename,'check':'Unexpected credential container'})
                if name.endswith(('.dex','.json','.properties','.xml','.pem')):
                    raw=archive.read(entry);scanned+=1
                    for pattern in patterns:
                        if re.search(pattern.encode(),raw):errors.append({'path':str(apk.relative_to(ROOT)),'entry':entry.filename,'check':'Potential packaged credential pattern'})
        apks.append({'file':str(apk.relative_to(ROOT)),'entries_scanned':scanned})
    report={'result':'FAIL' if errors else 'PASS','source_files_scanned':count,'apk_scan':apks,'checks':['Known credential patterns in application/script sources and copied APK dex/config entries','Automatic Android cloud backup disabled','No declared app location permission'],'limitations':['Pattern scanning does not prove absence of all secrets','No penetration test, dependency vulnerability audit or legal compliance assessment','Amazon receipt verification and sandbox purchasing require external configuration'],'errors':errors}
    target=ROOT/'deliverables/reports/static-security-check.json';target.write_text(json.dumps(report,indent=2),encoding='utf-8')
    rows=[]
    for folder in ['deliverables/android','deliverables/brand-assets','deliverables/store-assets','deliverables/localization','deliverables/reports','localization']:
        for path in sorted((ROOT/folder).rglob('*')):
            if path.is_file() and '__pycache__' not in path.parts:
                rows.append({'File path':str(path.relative_to(ROOT)).replace('\\','/'),'Bytes':path.stat().st_size,'SHA256':hashlib.sha256(path.read_bytes()).hexdigest()})
    inventory=ROOT/'deliverables/FILE_CHECKSUMS.csv'
    with inventory.open('w',newline='',encoding='utf-8') as out:
        writer=csv.DictWriter(out,fieldnames=['File path','Bytes','SHA256']);writer.writeheader();writer.writerows(rows)
    print(json.dumps({'static_checks':report['result'],'source_files':count,'inventoried_files':len(rows),'errors':errors}))
    if errors:raise SystemExit(1)
if __name__=='__main__':main()
