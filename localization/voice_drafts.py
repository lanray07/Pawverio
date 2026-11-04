"""Reviewable localized command examples, matched to core VoiceIntent IDs.
Recognition-engine compatibility remains NOT TESTED for every language.
"""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parent
IDS=['feeding_status','complete_feeding','next_vet','medications','add_grooming','read_today','vaccinations','log_walk']
EXAMPLES={
'en':['Has Max been fed today?','Mark Max as fed.','When is Max’s next vet appointment?','Show medication reminders.','Add a grooming reminder.','Read today’s pet-care tasks.','Show Max’s vaccination history.','Log a 20 minute walk for Max.'],
'fr':['Max a été nourri aujourd’hui ?','Marquer Max comme nourri.','Quand est le prochain rendez-vous vétérinaire de Max ?','Afficher les médicaments.','Ajouter un rappel de toilettage.','Lire les soins d’aujourd’hui.','Afficher les vaccinations de Max.','Enregistrer une promenade de 20 minutes pour Max.'],
'es':['¿Max ha comido hoy?','Marcar a Max como alimentado.','¿Cuándo tiene Max cita con el veterinario?','Mostrar medicamentos.','Añadir recordatorio de aseo.','Leer los cuidados de hoy.','Mostrar las vacunas de Max.','Registrar un paseo de 20 minutos con Max.'],
'de':['Hat Max heute Futter bekommen?','Markiere Max als gefüttert.','Wann ist Max beim Tierarzt?','Zeige Medikamente.','Pflege hinzufügen.','Lies die Aufgaben für heute.','Zeige Max die Impfungen.','Protokolliere einen Spaziergang von 20 Minuten mit Max.'],
'it':['Max ha mangiato oggi?','Segna il pasto di Max.','Quando va Max dal veterinario?','Mostra i farmaci.','Aggiungi un promemoria di toelettatura.','Leggi le attività di oggi.','Mostra i vaccini di Max.','Registra una passeggiata di 20 minuti con Max.'],
'pt':['Max já comeu hoje?','Marcar Max como alimentado.','Quando é a consulta de Max com o veterinário?','Mostrar medicamentos.','Adicionar lembrete de higiene.','Ler os cuidados de hoje.','Mostrar as vacinas de Max.','Registrar um passeio de 20 minutos com Max.'],
'nl':['Heeft Max vandaag eten gekregen?','Markeer Max als gevoerd.','Wanneer gaat Max naar de dierenarts?','Toon medicatie.','Voeg verzorging toe.','Lees de taken van vandaag.','Toon de vaccinaties van Max.','Registreer een wandeling van 20 minuten met Max.'],
'pl':['Czy Max został nakarmiony?','Oznacz Max jako nakarmiony.','Kiedy Max idzie do weterynarza?','Pokaż leki.','Dodaj przypomnienie o pielęgnacji.','Przeczytaj dzisiejsze zadania.','Pokaż szczepienia Max.','Zapisz spacer z Max przez 20 minut.'],
'sv':['Har Max matats idag?','Markera Max som matad.','När är Max nästa veterinärbesök?','Visa mediciner.','Lägg till en påminnelse om pälsvård.','Läs dagens uppgifter.','Visa Max vaccinationer.','Logga en promenad på 20 minuter med Max.'],
'ja':['Maxは今日ごはんを食べましたか？','Maxの食事を完了として記録。','Maxの次の動物病院はいつ？','薬を表示。','お手入れの予定を追加。','今日のお世話を読んで。','Maxの予防接種を表示。','Maxの散歩20分を記録。'],
'hi':['क्या Max ने भोजन खाया?','Max का भोजन दर्ज करें।','Max की पशु चिकित्सक से अगली मुलाकात कब है?','दवा दिखाएं।','ग्रूमिंग का रिमाइंडर जोड़ें।','आज के काम पढ़ें।','Max के टीकाकरण दिखाएं।','Max की 20 मिनट की सैर दर्ज करें।'],
'ar':['هل أكل Max اليوم؟','سجل إطعام Max.','متى موعد Max مع الطبيب البيطري؟','اعرض الأدوية.','أضف تذكير العناية.','اقرأ مهام اليوم.','اعرض التطعيمات لحيوان Max.','سجل نزهة لمدة 20 دقيقة لحيوان Max.'],
'tr':['Max beslendi mi?','Max için mama kaydet.','Max için veteriner randevusu ne zaman?','İlaç hatırlatıcılarını göster.','Bakım hatırlatıcısı ekle.','Bugünün görevlerini oku.','Max için aşı kayıtlarını göster.','Max için 20 dakika yürüyüş kaydet.'],
'id':['Apakah Max sudah makan hari ini?','Tandai Max diberi makan.','Kapan janji Max dengan dokter hewan?','Tampilkan obat.','Tambahkan pengingat perawatan.','Bacakan tugas hari ini.','Tampilkan vaksinasi Max.','Catat jalan-jalan 20 menit dengan Max.']
}

if __name__=='__main__':
    locales=['en-GB','en-US','en-AU','es-ES','es-419','fr-FR','fr-CA','de-DE','it-IT','pt-BR','pt-PT','nl-NL','pl-PL','sv-SE','ja-JP','hi-IN','ar','tr-TR','id-ID']
    result={'provenance':'AI-authored drafts, pending native review','numeric_durations':'Use numeric digits; English spoken numbers have limited parser support. No localized spoken-number claim.','test_pet_name':'Max','locales':{locale:{'examples':dict(zip(IDS,EXAMPLES[locale.split('-')[0]])),'recognition':'NOT TESTED','tts':'NOT TESTED','human_review':'PENDING'} for locale in locales}}
    (ROOT/'voice-commands.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
