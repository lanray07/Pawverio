"""Reject stale onboarding/blank physical captures before composing marketing assets.
This is a rendered-frame check, not native language approval or OCR certification.
"""
from pathlib import Path
from collections import Counter
import csv,json,shutil
from PIL import Image
ROOT=Path(__file__).resolve().parents[1]
def main():
    rows=[]
    quarantine=ROOT/'scripts/.tools/quarantined-captures'
    for path in sorted((ROOT/'deliverables/store-assets/screenshots/raw').glob('*/*/*.png')):
        variant,locale=path.parent.parent.name,path.parent.name;index=int(path.stem)
        with Image.open(path) as raw:
            image=raw.convert('RGB');w,h=image.size
            if variant=='fire-tablet' and (w,h)==(1200,1920):
                if index in [6,8]:
                    sample=image.crop((w*3//10,h*3//10,w*7//10,h*7//10))
                    passed=Counter(sample.get_flattened_data())[(229,235,222)]>500
                else:
                    left=image.crop((24,50,200,100));right=image.crop((1000,50,1176,100))
                    passed=max(len(left.getcolors(10000) or []),len(right.getcolors(10000) or []))>12
            else:
                # New compact captures have device-side node + pixel verification.
                passed=len(image.resize((120,180)).getcolors(50000) or [])>12
        rows.append({'path':str(path.relative_to(ROOT)),'locale':locale,'variant':variant,'screen':index,'frame_check':'PASS' if passed else 'FAIL','review':'Native language/claims/visual review pending'})
        if not passed:
            dest=quarantine/path.relative_to(ROOT/'deliverables/store-assets/screenshots/raw')
            dest.parent.mkdir(parents=True,exist_ok=True)
            assert path.resolve().is_relative_to(ROOT.resolve()) and dest.resolve().is_relative_to(ROOT.resolve())
            shutil.move(str(path),str(dest))
    output=ROOT/'deliverables/reports/raw-capture-audit.csv'
    with output.open('w',encoding='utf-8',newline='') as stream:
        writer=csv.DictWriter(stream,fieldnames=list(rows[0]));writer.writeheader();writer.writerows(rows)
    print(json.dumps({'examined':len(rows),'accepted_frames':sum(r['frame_check']=='PASS' for r in rows),'quarantined':sum(r['frame_check']=='FAIL' for r in rows),'quarantine_is_ignored':True}))
if __name__=='__main__':main()
