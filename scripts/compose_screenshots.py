"""Offline editable screenshot artwork from real app captures. Chromium shapes all Unicode text."""
import base64, csv, hashlib, html, json, os
from pathlib import Path
from PIL import Image
from playwright.sync_api import sync_playwright
ROOT=Path(__file__).resolve().parents[1]
BASE=ROOT/'deliverables/store-assets/screenshots'
def data(path):return 'data:image/png;base64,'+base64.b64encode(path.read_bytes()).decode()
def main():
    sources=sorted((BASE/'raw').glob('*/*/*.png'))
    if not sources:raise RuntimeError('No real app captures; refusing to fabricate screenshots')
    for old in (BASE/'compositions').glob('*/*/*'):
        if old.suffix in ['.png','.html','.svg'] and old.stem.isdigit():
            source=BASE/'raw'/old.parent.parent.name/old.parent.name/f'{int(old.stem)}.png'
            if not source.exists():
                assert old.resolve().is_relative_to((BASE/'compositions').resolve())
                old.unlink()
    copy=json.loads((ROOT/'localization/screenshot-copy.json').read_text(encoding='utf-8'))
    chrome=next((p for p in [Path('C:/Program Files/Google/Chrome/Application/chrome.exe'),Path('C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe')] if p.exists()),None)
    if chrome is None:raise RuntimeError('Install Chrome/Edge or configure your Chromium path')
    rows=[]
    with sync_playwright() as runtime:
        browser=runtime.chromium.launch(executable_path=str(chrome),headless=True)
        page=browser.new_page()
        for source in sources:
            variant,locale=source.parent.parent.name,source.parent.name;number=int(source.stem)
            headline=copy[locale]['headlines'][number-1];caption=copy[locale]['captions'][number-1]
            w,h=(1200,1920) if variant=='fire-tablet' else (720,1280)
            target=BASE/'compositions'/variant/locale;target.mkdir(parents=True,exist_ok=True)
            css=f'''*{{box-sizing:border-box}}html,body{{margin:0;width:{w}px;height:{h}px;overflow:hidden;background:#FAF7F0;color:#2B3335}}body{{font-family:"Segoe UI","Nirmala UI","Yu Gothic",sans-serif;padding:{h*.035}px {w*.065}px;display:flex;flex-direction:column;gap:{h*.012}px}}.brand{{display:flex;align-items:center;gap:{w*.025}px;color:#286B70;font-size:{w*.035}px;font-weight:650;direction:ltr}}.brand img{{width:{w*.082}px;height:{w*.082}px}}h1{{font-size:{w*.055}px;line-height:1.18;margin:0;font-weight:650;overflow-wrap:anywhere}}p{{font-size:{w*.026}px;line-height:1.35;margin:0;color:#286B70}}.frame{{flex:1;min-height:0;display:flex;align-items:center;justify-content:center}}.frame img{{max-width:100%;max-height:100%;object-fit:contain;border:{w*.006}px solid #286B70;border-radius:{w*.022}px;box-shadow:0 {w*.01}px {w*.025}px #286B7020}}'''
            direction='rtl' if locale=='ar' else 'ltr'
            icon=data(ROOT/'deliverables/brand-assets/pawverio-master-icon-114.png')
            content=f'<!doctype html><html lang="{locale}"><meta charset="utf-8"><style>{css}</style><body dir="{direction}"><div class="brand"><img src="{icon}" alt="">Pawverio</div><h1>{html.escape(headline)}</h1><p>{html.escape(caption)}</p><div class="frame"><img src="{data(source)}" alt="{html.escape(caption)}"></div></body></html>'
            page.set_viewport_size({'width':w,'height':h});page.set_content(content,wait_until='load');page.evaluate('document.fonts.ready')
            file=target/f'{number:02d}.png';page.screenshot(path=str(file));Image.open(file).verify()
            template=content.replace(data(source),os.path.relpath(source,target).replace('\\','/')).replace(icon,os.path.relpath(ROOT/'deliverables/brand-assets/pawverio-master-icon-114.png',target).replace('\\','/'))
            (target/f'{number:02d}.html').write_text(template,encoding='utf-8')
            rows.append({'locale':locale,'variant':variant,'screen':number,'source':str(source.relative_to(ROOT)),'composition':str(file.relative_to(ROOT)),'width':w,'height':h,'sha256':hashlib.sha256(file.read_bytes()).hexdigest(),'status':'NOT TESTED','remaining_review':'Native language, visual clipping and claim review; Amazon listing approval. Phone layout captured on Fire hardware.'})
        browser.close()
    with (BASE/'SCREENSHOT_MANIFEST.csv').open('w',encoding='utf-8',newline='') as stream:
        writer=csv.DictWriter(stream,fieldnames=rows[0]);writer.writeheader();writer.writerows(rows)
    coverage=[]
    for locale in copy:
        for variant in ['fire-tablet','phone-layout']:
            present={r['screen'] for r in rows if r['locale']==locale and r['variant']==variant}
            coverage.append({'locale':locale,'variant':variant,'actual_screens':len(present),'required_screens':8,'missing_screens':','.join(str(n) for n in range(1,9) if n not in present),'capture_status':'PASS' if len(present)==8 else 'BLOCKED','approval_status':'NOT TESTED','remaining_blocker':'Native/visual/console review' if len(present)==8 else 'Unlock Fire tablet for remaining actual captures; native/visual/console review'})
    with (BASE/'SCREENSHOT_COVERAGE.csv').open('w',encoding='utf-8',newline='') as stream:
        writer=csv.DictWriter(stream,fieldnames=coverage[0]);writer.writeheader();writer.writerows(coverage)
    print(f'Created {len(rows)} same-locale PNG compositions and editable artwork templates.')
if __name__=='__main__':main()
