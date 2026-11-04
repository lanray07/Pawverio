"""Toggle the observed Fire Settings airplane switch for authorized USB QA.
Never reads/saves network names or credentials. Requires an unlocked device.
"""
import argparse,os,re,subprocess,time,xml.etree.ElementTree as E
from pathlib import Path
def main():
    p=argparse.ArgumentParser();p.add_argument('--serial',required=True);p.add_argument('state',choices=['enable','disable']);a=p.parse_args()
    adb=Path(os.environ.get('ANDROID_HOME',str(Path.home()/'AppData/Local/Android/Sdk')))/'platform-tools/adb.exe'
    def run(*cmd):return subprocess.check_output([str(adb),'-s',a.serial,*cmd],text=True)
    desired='1' if a.state=='enable' else '0'
    if run('shell','settings','get','global','airplane_mode_on').strip()==desired:print('Airplane mode already '+desired);return
    run('shell','am','start','-a','android.settings.AIRPLANE_MODE_SETTINGS')
    root=None
    for attempt in range(6):
        run('shell','rm','-f','/data/local/tmp/pawverio-settings.xml')
        run('shell','uiautomator','dump','/data/local/tmp/pawverio-settings.xml')
        try:root=E.fromstring(run('exec-out','cat','/data/local/tmp/pawverio-settings.xml'));break
        except (subprocess.CalledProcessError,E.ParseError):time.sleep(.4)
    if root is None:raise RuntimeError('No fresh Settings hierarchy; no blind click performed')
    parents={c:p for p in root.iter() for c in p}
    node=next((n for n in root.iter('node') if n.get('text','').lower() in ['aeroplane mode','airplane mode']),None)
    if node is None:raise RuntimeError('Observed airplane switch not found; no blind click performed')
    while node.get('clickable')!='true':node=parents[node]
    x0,y0,x1,y1=map(int,re.findall(r'\d+',node.get('bounds')))
    run('shell','input','tap',str((x0+x1)//2),str((y0+y1)//2))
    for _ in range(20):
        actual=run('shell','settings','get','global','airplane_mode_on').strip()
        if actual==desired:print('Observed Settings switch verified airplane_mode_on='+actual);return
        time.sleep(.2)
    raise RuntimeError('Airplane mode did not reach requested state')
if __name__=='__main__':main()
