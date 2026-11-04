"""Capture actual QA app interfaces. Requires installed debug APK and androidTest APK.

Phone layout is a debug-only native360x640dp viewport; system display size is untouched.
Does not change or capture other applications. Fictional app data is removed by tests.
"""
import argparse, json, os, subprocess
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
def main():
    parser=argparse.ArgumentParser();parser.add_argument('--serial',required=True);parser.add_argument('--locales',default='en-GB,en-US,en-AU,es-ES,es-419,fr-FR,fr-CA,de-DE,it-IT,pt-BR,pt-PT,nl-NL,pl-PL,sv-SE,ja-JP,hi-IN,ar,tr-TR,id-ID');parser.add_argument('--variant',choices=['fire-tablet','phone-layout','both'],default='both');args=parser.parse_args()
    sdk=Path(os.environ.get('ANDROID_HOME',str(Path.home()/'AppData/Local/Android/Sdk')))
    adb=str(sdk/'platform-tools/adb.exe')
    def run(*cmd,check=True):return subprocess.run([adb,'-s',args.serial,*cmd],check=check,text=True,capture_output=True)
    output=ROOT/'deliverables/store-assets/screenshots';output.mkdir(parents=True,exist_ok=True)
    if 'isStatusBarKeyguard=true' in run('shell','dumpsys','window').stdout:
        raise RuntimeError('BLOCKED: unlock the Fire tablet manually; screenshot capture never bypasses its PIN.')
    size=run('shell','wm','size').stdout; density=run('shell','wm','density').stdout
    if 'Override' in size or 'Override' in density:raise RuntimeError('Device already has display overrides; preserve them manually before capture.')
    rotation=run('shell','settings','get','system','accelerometer_rotation').stdout.strip()
    user_rotation=run('shell','settings','get','system','user_rotation').stdout.strip()
    (output/'capture-device.json').write_text(json.dumps({'model':run('shell','getprop','ro.product.model').stdout.strip(),'api':run('shell','getprop','ro.build.version.sdk').stdout.strip(),'physical_size':size,'density':density,'phone_note':'Phone images exercise the actual narrow Compose layout on a Fire device; not proof of phone hardware compatibility.'},indent=2),encoding='utf-8')
    try:
        run('shell','settings','put','system','accelerometer_rotation','0')
        run('shell','settings','put','system','user_rotation','0')
        for variant in (['fire-tablet','phone-layout'] if args.variant=='both' else [args.variant]):
            # Fire OS can retain the target process after another instrumentation run,
            # including its disposed Compose test-root registry. Start an isolated run.
            run('shell','am','force-stop','com.pawverio.app')
            result=run('shell','am','instrument','-w','-r','-e','class','com.pawverio.app.StoreCaptureTest','-e','locales',args.locales,'-e','variant',variant,'com.pawverio.app.test/androidx.test.runner.AndroidJUnitRunner',check=False)
            (ROOT/f'deliverables/android/build-logs/capture-{variant}.log').write_text(result.stdout+result.stderr,encoding='utf-8')
            if result.returncode or 'FAILURES' in result.stdout or 'OK (1 test)' not in result.stdout:raise RuntimeError(f'{variant} capture failed; see capture log')
            for locale in args.locales.split(','):
                target=output/'raw'/variant/locale;target.mkdir(parents=True,exist_ok=True)
                for index in range(1,9):run('pull',f'/sdcard/Android/data/com.pawverio.app/files/screenshots/{variant}/{locale}/{index}.png',str(target/f'{index}.png'))
    finally:
        run('shell','settings','put','system','user_rotation',user_rotation,check=False)
        run('shell','settings','put','system','accelerometer_rotation',rotation,check=False)
if __name__=='__main__':main()
