"""Check actual files against documented Amazon dimensions and metadata lengths.
Human spelling, claims and console locale approval remain separate.
"""
import json,csv
from pathlib import Path
from PIL import Image
ROOT=Path(__file__).resolve().parents[1]
def main():
    errors=[];metadata=0;images=0
    allowed={(800,480),(1024,600),(1280,720),(1280,800),(1920,1080),(1920,1200),(2560,1600)}
    allowed |= {(h,w) for w,h in list(allowed)}
    for path in (ROOT/'deliverables/store-assets/metadata').glob('*.json'):
        data=json.loads(path.read_text(encoding='utf-8'));metadata+=1
        if len(data['short_description'].encode('utf-8'))>2000:errors.append(str(path)+': short description >2000 bytes')
        if len(data['long_description'])>4000:errors.append(str(path)+': long description >4000 characters')
        if not 3<=len(data['product_feature_bullets'])<=5:errors.append(str(path)+': expected3–5 feature bullets')
    for path in (ROOT/'deliverables/store-assets/screenshots/compositions').glob('*/*/*.png'):
        with Image.open(path) as image:
            images+=1
            if image.size not in allowed:errors.append(str(path)+': unsupported documented dimensions')
            image.verify()
    manifest=ROOT/'deliverables/store-assets/screenshots/SCREENSHOT_MANIFEST.csv'
    rows=list(csv.DictReader(manifest.open(encoding='utf-8'))) if manifest.exists() else []
    report={'status':'FAIL' if errors else 'PASS','metadata_drafts':metadata,'composition_pngs':images,'manifest_rows':len(rows),'errors':errors,'remaining_review':'Native language and visual clipping review; claims; final Amazon console listing locale/device/country selectors. Sizes verified from Amazon appstore-details documentation in marketplace-research.md.'}
    (ROOT/'deliverables/reports/store-asset-validation.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
    print(json.dumps(report))
    if errors:raise SystemExit(1)
if __name__=='__main__':main()
