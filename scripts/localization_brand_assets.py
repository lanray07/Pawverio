"""Original procedural Pawverio brand vectors plus antialiased raster exports.
No downloaded stock artwork or third-party icons are used by this generator.
SVGs remain editable; PNG files are rendered from the same geometry.
"""
import csv
import hashlib
import json
import math
from pathlib import Path
from PIL import Image,ImageDraw,ImageFont
ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/'deliverables/brand-assets'
TEAL='#286B70';IVORY='#FAF7F0';SAGE='#9AB7A5';CORAL='#D98A78';CHARCOAL='#2B3335'

class Art:
    def __init__(self,size=1024,background=None):
        self.size=size;self.scale=3;self.im=Image.new('RGBA',(size*3,size*3),background or (0,0,0,0));self.draw=ImageDraw.Draw(self.im)
        self.svg=[f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {size} {size}" width="{size}" height="{size}">', '<title>Pawverio original brand artwork</title>']
        if background:self.svg.append(f'<rect width="{size}" height="{size}" fill="{background}"/>')
    def box(self,xy,fill,radius=0,stroke=None,width=1):
        x0,y0,x1,y1=xy
        self.svg.append(f'<rect x="{x0}" y="{y0}" width="{x1-x0}" height="{y1-y0}" rx="{radius}" fill="{fill or "none"}" stroke="{stroke or "none"}" stroke-width="{width}"/>')
        self.draw.rounded_rectangle(tuple(v*3 for v in xy),radius*3,fill=fill,outline=stroke,width=width*3)
    def ellipse(self,xy,fill,stroke=None,width=1):
        x0,y0,x1,y1=xy
        self.svg.append(f'<ellipse cx="{(x0+x1)/2}" cy="{(y0+y1)/2}" rx="{(x1-x0)/2}" ry="{(y1-y0)/2}" fill="{fill or "none"}" stroke="{stroke or "none"}" stroke-width="{width}"/>')
        self.draw.ellipse(tuple(v*3 for v in xy),fill=fill,outline=stroke,width=width*3)
    def line(self,points,color,width=16):
        self.svg.append('<polyline points="'+' '.join(f'{x},{y}' for x,y in points)+f'" fill="none" stroke="{color}" stroke-width="{width}" stroke-linecap="round" stroke-linejoin="round"/>')
        self.draw.line([(x*3,y*3) for x,y in points],fill=color,width=width*3,joint='curve')
        for x,y in (points[0],points[-1]):self.draw.ellipse(((x-width/2)*3,(y-width/2)*3,(x+width/2)*3,(y+width/2)*3),fill=color)
    def polygon(self,points,color):
        self.svg.append('<polygon points="'+' '.join(f'{x},{y}' for x,y in points)+f'" fill="{color}"/>')
        self.draw.polygon([(x*3,y*3) for x,y in points],fill=color)
    def text(self,xy,text,color,size=48):
        x,y=xy;self.svg.append(f'<text x="{x}" y="{y+size}" fill="{color}" font-family="Segoe UI, sans-serif" font-size="{size}" font-weight="600">{text}</text>')
        fontfile=Path('C:/Windows/Fonts/segoeui.ttf')
        font=ImageFont.truetype(str(fontfile),size*3) if fontfile.exists() else ImageFont.load_default()
        self.draw.text((x*3,y*3),text,font=font,fill=color)
    def save(self,name,sizes=(1024,)):
        OUT.mkdir(parents=True,exist_ok=True)
        (OUT/f'{name}.svg').write_text('\n'.join(self.svg+['</svg>'])+'\n',encoding='utf-8')
        for size in sizes:
            self.im.resize((size,size),Image.Resampling.LANCZOS).save(OUT/f'{name}-{size}.png')

def paw(a,cx=512,cy=535,scale=1,color=TEAL):
    def e(x0,y0,x1,y1):a.ellipse((cx+x0*scale,cy+y0*scale,cx+x1*scale,cy+y1*scale),color)
    e(-107,-5,107,156);e(-128,-130,-40,-22);e(-22,-173,66,-55);e(86,-114,168,-12);e(-199,-58,-121,44)

def calendar(a,foreground=TEAL,monochrome=False):
    a.ellipse((145,145,879,879),None,foreground,24)
    a.box((244,279,780,772),None,70,foreground,28)
    a.line([(255,414),(769,414)],foreground,20)
    for x in [364,659]:a.line([(x,230),(x,325)],foreground,34)
    paw(a,510,530,.77,foreground)
    if not monochrome:
        a.ellipse((748,687,883,822),CORAL)
        a.line([(783,754),(810,778),(851,732)],IVORY,15)

def animal(a,kind):
    a.ellipse((130,210,894,920),SAGE)
    if kind=='dog':
        a.ellipse((210,260,390,720),TEAL);a.ellipse((638,260,818,720),TEAL)
        a.ellipse((285,250,745,800),IVORY);a.ellipse((540,290,703,526),CORAL)
    else:
        a.polygon([(263,473),(257,194),(446,361)],TEAL);a.polygon([(580,361),(765,194),(760,473)],TEAL)
        a.polygon([(292,397),(292,260),(389,360)],CORAL);a.polygon([(635,360),(732,260),(730,397)],CORAL)
        a.ellipse((260,325,765,800),IVORY)
    a.ellipse((391,487,425,531),CHARCOAL);a.ellipse((600,487,634,531),CHARCOAL)
    a.ellipse((472,568,552,630),TEAL)
    a.line([(511,627),(511,659),(463,678)],TEAL,10);a.line([(511,659),(559,678)],TEAL,10)
    if kind=='cat':
        for y in [580,626]:a.line([(285,y),(393,y+15)],TEAL,8);a.line([(633,y+15),(741,y)],TEAL,8)

def feature(a,kind):
    a.ellipse((116,116,908,908),IVORY)
    if kind=='feeding':
        a.box((225,525,799,732),TEAL,80);a.ellipse((225,438,799,606),SAGE)
        a.ellipse((263,461,761,566),IVORY)
        for x,y in [(380,489),(445,510),(540,486),(617,515)]:a.ellipse((x,y,x+37,y+28),CORAL)
        paw(a,516,641,.20,IVORY)
    elif kind=='walking':
        for x,y in [(336,333),(565,539)]:
            a.ellipse((x,y,x+106,y+187),TEAL);a.ellipse((x-21,y-67,x+22,y-20),CORAL);a.ellipse((x+38,y-85,x+81,y-38),CORAL);a.ellipse((x+97,y-61,x+140,y-14),CORAL)
        a.line([(240,770),(352,760),(441,800),(584,795),(716,741)],SAGE,20)
    elif kind=='grooming':
        a.box((304,276,720,522),TEAL,45);a.box((459,493,565,780),CORAL,36)
        for x in range(355,697,57):a.line([(x,322),(x,465)],IVORY,20)
        for x,y in [(248,637),(748,346)]:a.line([(x-30,y),(x+30,y)],SAGE,12);a.line([(x,y-30),(x,y+30)],SAGE,12)
    elif kind in ['veterinary-record','vaccination']:
        a.box((270,233,754,797),IVORY,45,TEAL,22);a.box((410,200,614,287),SAGE,20)
        if kind=='veterinary-record':
            a.box((464,350,560,560),CORAL,12);a.box((407,407,617,503),CORAL,12)
        else:
            a.polygon([(511,350),(661,404),(645,539),(511,630),(377,539),(361,404)],SAGE)
            a.line([(446,486),(495,532),(577,442)],TEAL,23)
        a.line([(359,675),(665,675)],TEAL,17);a.line([(359,727),(578,727)],SAGE,17)
    elif kind=='medication':
        a.box((351,246,674,362),TEAL,20);a.box((315,334,710,799),IVORY,60,TEAL,23)
        a.box((328,486,697,661),SAGE,8);a.box((476,509,548,638),IVORY,8);a.box((447,538,577,609),IVORY,8)
    elif kind=='voice':
        a.box((418,226,606,612),TEAL,94)
        a.line([(342,476),(342,583),(377,651),(444,695),(514,709),(584,695),(651,651),(686,583),(686,476)],CORAL,25)
        a.line([(514,711),(514,802)],TEAL,25);a.line([(432,805),(596,805)],TEAL,25)
        a.line([(245,389),(226,434),(219,493),(226,550)],SAGE,20);a.line([(779,389),(798,434),(805,493),(798,550)],SAGE,20)
    elif kind=='premium':
        a.ellipse((232,232,792,792),SAGE)
        paw(a,512,480,.9,TEAL)
        a.polygon([(314,711),(291,583),(390,638),(512,545),(634,638),(733,583),(710,711)],CORAL)
    elif kind=='empty-state':
        a.box((255,319,768,782),None,45,SAGE,18);a.line([(268,446),(751,446)],SAGE,15)
        paw(a,512,534,.53,TEAL)
        for x in [375,650]:a.line([(x,260),(x,355)],CORAL,25)
    elif kind=='onboarding':
        a.ellipse((179,239,845,906),SAGE);a.box((298,279,725,784),IVORY,48)
        paw(a,514,447,.6,TEAL)
        for y,length in [(645,240),(703,174)]:a.line([(394,y),(394+length,y)],CORAL,18)
        a.ellipse((707,234,827,354),TEAL);a.line([(741,294),(765,317),(800,272)],IVORY,12)

def main():
    a=Art(background=IVORY);calendar(a);a.save('pawverio-master-icon',(1024,512,114))
    a=Art();calendar(a);a.save('pawverio-adaptive-foreground',(1024,432))
    a=Art(background=IVORY);a.save('pawverio-adaptive-background',(1024,432))
    a=Art();calendar(a,CHARCOAL,monochrome=True);a.save('pawverio-monochrome',(1024,432))
    a=Art(background=IVORY);calendar(a);a.text((335,885),'Pawverio',TEAL,67);a.save('pawverio-splash',(1024,))
    for kind in ['dog','cat']:
        a=Art();animal(a,kind);a.save('pawverio-'+kind+'-profile',(1024,512))
    for kind in ['feeding','walking','grooming','veterinary-record','medication','vaccination','voice','premium','empty-state','onboarding']:
        a=Art();feature(a,kind);a.save('pawverio-'+kind,(1024,256))
    a=Art();calendar(a);a.save('pawverio-logo-mark',(1024,512))
    # Editable brand lockup intentionally uses a live text element in SVG.
    a=Art();paw(a,512,300,.75);a.text((208,600),'Pawverio',TEAL,138);a.save('pawverio-wordmark',(1024,))
    # Publisher-editable, name-only Amazon promotional art and social banner.
    for name,width,height,tagline in [('pawverio-amazon-promotional',1024,500,False),('pawverio-social-banner',1200,630,True)]:
        banner=Image.new('RGB',(width,height),IVORY);d=ImageDraw.Draw(banner)
        mark=Image.open(OUT/'pawverio-adaptive-foreground-1024.png').convert('RGBA');mark.thumbnail((int(height*.80),int(height*.80)))
        banner.paste(mark,(20,int(height*.10)),mark)
        font=ImageFont.truetype('C:/Windows/Fonts/segoeuib.ttf',int(height*.13));d.text((int(width*.43),int(height*.34)),'Pawverio',font=font,fill=TEAL)
        if tagline:
            f=ImageFont.truetype('C:/Windows/Fonts/segoeui.ttf',int(height*.038));d.text((int(width*.43),int(height*.55)),'Every pet. Every routine.',font=f,fill=CHARCOAL)
        banner.save(OUT/f'{name}.png')
        icon=(OUT/'pawverio-adaptive-foreground.svg').read_text(encoding='utf-8')
        start=icon.index('<title>');content=icon[start:icon.rindex('</svg>')]
        svg=f'<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}" viewBox="0 0 {width} {height}"><rect width="{width}" height="{height}" fill="{IVORY}"/><g transform="translate(20 {height*.1}) scale({height*.8/1024})">{content}</g><text x="{width*.43}" y="{height*.48}" font-family="Segoe UI,sans-serif" font-size="{height*.13}" font-weight="700" fill="{TEAL}">Pawverio</text>'
        if tagline:svg+=f'<text x="{width*.43}" y="{height*.60}" font-family="Segoe UI,sans-serif" font-size="{height*.038}" fill="{CHARCOAL}">Every pet. Every routine.</text>'
        (OUT/f'{name}.svg').write_text(svg+'</svg>\n',encoding='utf-8')
    rows=[]
    for file in sorted(OUT.glob('*')):
        if file.suffix not in ['.png','.svg']:continue
        if file.name=='brand-contact-sheet.png':continue
        row={'file':file.name,'type':file.suffix[1:],'bytes':file.stat().st_size,'sha256':hashlib.sha256(file.read_bytes()).hexdigest(),'origin':'Original procedural geometry' if file.name!='pawverio-companions.png' else 'Generated companion illustration; root workflow','verification':'File present'}
        if file.suffix=='.png':
            with Image.open(file) as im:im.verify()
        rows.append(row)
    with (OUT/'brand-assets-manifest.csv').open('w',encoding='utf-8',newline='') as fh:
        writer=csv.DictWriter(fh,fieldnames=list(rows[0]));writer.writeheader();writer.writerows(rows)
    thumbs=[]
    for file in sorted(OUT.glob('*-256.png')):
        with Image.open(file) as im:thumbs.append((file,im.convert('RGBA').copy()))
    sheet=Image.new('RGB',(1024,380*math.ceil(len(thumbs)/4)),IVORY);draw=ImageDraw.Draw(sheet)
    for i,(path,im) in enumerate(thumbs):
        x=(i%4)*256;y=(i//4)*380;sheet.paste(im,(x,y),im);draw.text((x+12,y+280),path.stem.replace('pawverio-',''),fill=TEAL)
    sheet.save(OUT/'brand-contact-sheet.png')
    print(f'Generated original SVG/PNG asset package; {len(rows)} verified media files (plus contact sheet).')

if __name__=='__main__':main()
