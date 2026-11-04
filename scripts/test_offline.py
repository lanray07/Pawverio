"""Run actual UI journeys offline over USB and restore tablet radio settings in finally."""
import argparse,os,subprocess,json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
def main():
    p=argparse.ArgumentParser();p.add_argument('--serial',required=True);args=p.parse_args()
    adb=Path(os.environ.get('ANDROID_HOME',str(Path.home()/'AppData/Local/Android/Sdk')))/'platform-tools/adb.exe'
    def run(*cmd):return subprocess.run([str(adb),'-s',args.serial,*cmd],capture_output=True,text=True,check=True).stdout
    previous_airplane=run('shell','settings','get','global','airplane_mode_on').strip()
    previous_wifi=run('shell','settings','get','global','wifi_on').strip()
    try:
        broadcast='Wi-Fi disabled over USB. Airplane setting is preserved; Fire OS rejects protected airplane-mode broadcasts. Each journey independently asserts activeNetwork=null.'
        run('shell','svc','wifi','disable')
        airplane=run('shell','settings','get','global','airplane_mode_on').strip();wifi=run('shell','settings','get','global','wifi_on').strip()
        assert wifi=='0',(airplane,wifi)
        result=run('shell','am','instrument','-w','-r','-e','class','com.pawverio.app.CareJourneyTest','-e','offline','true','com.pawverio.app.test/androidx.test.runner.AndroidJUnitRunner')
        (ROOT/'deliverables/android/build-logs/offline-ui-journeys.log').write_text('AIRPLANE='+airplane+' WIFI='+wifi+'\n'+broadcast+'\n'+result,encoding='utf-8')
        assert 'OK (3 tests)' in result and 'FAILURES' not in result,'UI journeys failed; see offline log'
    finally:
        run('shell','svc','wifi','enable' if previous_wifi=='1' else 'disable')
        (ROOT/'deliverables/reports/offline-device-settings.json').write_text(json.dumps({'previous_airplane':previous_airplane,'previous_wifi':previous_wifi,'restored_airplane':run('shell','settings','get','global','airplane_mode_on').strip(),'restored_wifi':run('shell','settings','get','global','wifi_on').strip()},indent=2),encoding='utf-8')
if __name__=='__main__':main()
