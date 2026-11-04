"""Generate editorial drafts, never approved store submissions or screenshots."""
import csv
import json
from pathlib import Path
from importlib.util import spec_from_file_location,module_from_spec
ROOT=Path(__file__).resolve().parents[1]
spec=spec_from_file_location('pipeline',ROOT/'scripts/localization.py');p=module_from_spec(spec);spec.loader.exec_module(p)
# title, summary, routine, free tier, premium tier, purchase transparency, voice caveat,
# data privacy, health limitation, release notes, support draft, 8 screenshot headlines.
COPY={
'en':[
'Pawverio – Pet Care & Daily Planner',
'Plan feeding, walks and everyday care. Keep pet health records together on your device.',
'Give every pet a calmer routine. Create a profile, plan care and record what has been done. Keep feeding notes, walk logs, grooming reminders and veterinary appointments in one place.',
'Start free with one pet, essential care reminders and veterinary records.',
'Premium adds more pets, medication and vaccination records, weight history, household assignments and pet sitter exports.',
'Premium is an optional one-time purchase. The Amazon store shows the current local price. Restore purchases from Settings. Existing health records remain accessible.',
'Speak a care command or type it instead. Speech recognition and spoken responses depend on the language services available on your device; offline voice is not guaranteed.',
'No account is required. Pet records are stored locally. Exports are your choice. There are no advertising SDKs or automatic location tracking.',
'Pawverio is an organiser. It does not diagnose conditions or recommend diets, medication doses or treatments. Record your vet’s instructions.',
'First release: local pet profiles, care planning, health records and optional voice interaction.',
'For support, include your app version, device model and a description of the issue. Do not send private pet health records. A verified publisher support address must be added before release.',
'Pet Care & Daily Planner|Pet Feeding Reminders|Vet Appointments & Health Records|Pet Medication Tracker|Pet Grooming & Care Schedule|Voice-Powered Pet Care Planner|Offline Pet Care Tracker|Pet Sitter Instructions & Reports'],
'fr':[
'Pawverio – Soins et agenda animalier',
'Organisez repas, promenades et soins. Gardez les dossiers de santé de vos animaux sur votre appareil.',
'Offrez une routine plus sereine à chaque animal. Créez un profil, planifiez les soins et notez ce qui a été fait. Rassemblez repas, promenades, toilettage et rendez-vous vétérinaires.',
'Commencez gratuitement avec un animal, les rappels essentiels et les dossiers vétérinaires.',
'Premium ajoute davantage d’animaux, les médicaments et vaccins, le suivi du poids, les tâches du foyer et les rapports de garde.',
'Premium est un achat unique facultatif. La boutique Amazon affiche le prix local actuel. Restaurez les achats dans les réglages. Les dossiers de santé existants restent accessibles.',
'Dictez une commande de soin ou saisissez-la. La reconnaissance et les réponses vocales dépendent des services linguistiques de votre appareil. Le fonctionnement vocal hors ligne n’est pas garanti.',
'Aucun compte requis. Les dossiers restent sur l’appareil. Vous choisissez les exports. Aucun SDK publicitaire ni suivi automatique de localisation.',
'Pawverio est un outil d’organisation. Il ne pose aucun diagnostic et ne recommande aucun régime, dosage ou traitement. Consignez les instructions de votre vétérinaire.',
'Première version : profils locaux, planification des soins, dossiers de santé et commandes vocales facultatives.',
'Pour obtenir de l’aide, indiquez la version de l’application, le modèle de l’appareil et le problème rencontré. N’envoyez pas de dossiers de santé privés. Une adresse d’assistance vérifiée de l’éditeur doit être ajoutée avant publication.',
'Soins et agenda pour animaux|Rappels des repas de vos animaux|Rendez-vous vétérinaires et santé|Suivi des médicaments de vos animaux|Toilettage et soins planifiés|Organisez les soins à la voix|Suivi des soins hors ligne|Consignes et rapports de garde'],
'es':[
'Pawverio – Cuidados y agenda de mascotas',
'Organiza comidas, paseos y cuidados. Guarda los registros de salud de tus mascotas en tu dispositivo.',
'Una rutina más tranquila para cada mascota. Crea un perfil, planifica los cuidados y registra lo que se ha hecho. Reúne notas de comida, paseos, aseo y citas veterinarias.',
'Empieza gratis con una mascota, recordatorios esenciales y registros veterinarios.',
'Premium añade más mascotas, registros de medicación y vacunas, historial de peso, tareas del hogar e informes para cuidadores.',
'Premium es una compra única opcional. La tienda Amazon muestra el precio local actual. Restaura las compras en Ajustes. Los registros de salud existentes siguen accesibles.',
'Di un comando de cuidado o escríbelo. El reconocimiento y las respuestas de voz dependen de los servicios de idioma del dispositivo; no se garantiza la voz sin conexión.',
'No necesitas cuenta. Los registros se guardan localmente. Tú eliges qué exportar. Sin SDK de publicidad ni seguimiento automático de ubicación.',
'Pawverio es una herramienta de organización. No diagnostica ni recomienda dietas, dosis o tratamientos. Registra las instrucciones de tu veterinario.',
'Primera versión: perfiles locales, planificación de cuidados, registros de salud e interacción de voz opcional.',
'Para obtener ayuda, indica la versión de la aplicación, el modelo del dispositivo y el problema. No envíes registros de salud privados. Debe añadirse una dirección de soporte verificada del editor antes de publicar.',
'Agenda de cuidados para mascotas|Recordatorios de comida para mascotas|Citas veterinarias y registros de salud|Seguimiento de medicación de mascotas|Agenda de aseo y cuidados|Organiza los cuidados con tu voz|Cuidados de mascotas sin conexión|Instrucciones e informes para cuidadores'],
'de':[
'Pawverio – Haustierpflege & Tagesplaner',
'Plane Fütterung, Spaziergänge und Pflege. Bewahre Gesundheitsdaten deiner Haustiere auf deinem Gerät auf.',
'Eine entspanntere Routine für jedes Haustier. Erstelle ein Profil, plane die Pflege und halte erledigte Aufgaben fest. Fütterung, Spaziergänge, Fellpflege und Tierarzttermine an einem Ort.',
'Kostenlos starten mit einem Haustier, grundlegenden Pflegeerinnerungen und Tierarztunterlagen.',
'Premium ergänzt weitere Haustiere, Medikamenten- und Impfunterlagen, Gewichtsverlauf, Haushaltsaufgaben und Betreuungsberichte.',
'Premium ist ein optionaler Einmalkauf. Der Amazon-Store zeigt den aktuellen lokalen Preis. Käufe lassen sich in den Einstellungen wiederherstellen. Bestehende Gesundheitsdaten bleiben zugänglich.',
'Sprich einen Pflegebefehl oder gib ihn ein. Spracherkennung und Sprachausgabe hängen von den Sprachdiensten deines Geräts ab. Offline-Sprachbetrieb ist nicht garantiert.',
'Kein Konto erforderlich. Tierdaten werden lokal gespeichert. Du entscheidest über Exporte. Keine Werbe-SDKs und keine automatische Standortverfolgung.',
'Pawverio dient der Organisation. Es stellt keine Diagnosen und empfiehlt keine Ernährung, Dosierung oder Behandlung. Halte die Anweisungen deiner Tierarztpraxis fest.',
'Erste Version: lokale Haustierprofile, Pflegeplanung, Gesundheitsdaten und optionale Sprachbefehle.',
'Nenne bei Supportanfragen die App-Version, das Gerätemodell und das Problem. Sende keine privaten Gesundheitsdaten. Vor Veröffentlichung muss eine bestätigte Supportadresse des Herausgebers ergänzt werden.',
'Haustierpflege und Tagesplaner|Erinnerungen ans Füttern|Tierarzttermine und Gesundheitsdaten|Medikamente für Haustiere im Blick|Fellpflege und Pflegeroutinen|Haustierpflege per Sprachbefehl|Haustierpflege offline festhalten|Hinweise und Berichte für die Betreuung'],
'it':[
'Pawverio – Cura e agenda per animali',
'Organizza pasti, passeggiate e cure. Conserva i dati sanitari degli animali sul tuo dispositivo.',
'Una routine più serena per ogni animale. Crea un profilo, pianifica le cure e registra le attività completate. Pasti, passeggiate, toelettatura e visite veterinarie in un unico posto.',
'Inizia gratis con un animale, promemoria essenziali e documenti veterinari.',
'Premium aggiunge più animali, farmaci e vaccini, cronologia del peso, incarichi familiari e rapporti per pet sitter.',
'Premium è un acquisto unico facoltativo. Lo store Amazon mostra il prezzo locale attuale. Ripristina gli acquisti dalle impostazioni. I dati sanitari esistenti restano accessibili.',
'Pronuncia un comando di cura o scrivilo. Riconoscimento e risposte vocali dipendono dai servizi linguistici del dispositivo; la voce offline non è garantita.',
'Nessun account necessario. I dati sono salvati sul dispositivo. Decidi tu cosa esportare. Nessun SDK pubblicitario e nessun rilevamento automatico della posizione.',
'Pawverio è uno strumento organizzativo. Non formula diagnosi né consiglia diete, dosaggi o trattamenti. Annota le istruzioni del veterinario.',
'Prima versione: profili locali, pianificazione delle cure, dati sanitari e interazione vocale facoltativa.',
'Per assistenza, indica versione dell’app, modello del dispositivo e problema. Non inviare dati sanitari privati. Prima della pubblicazione va aggiunto un recapito verificato dell’editore.',
'Cura e agenda per animali|Promemoria per i pasti|Visite veterinarie e dati sanitari|Registro dei farmaci per animali|Agenda di toelettatura e cure|Organizza le cure con la voce|Registro delle cure offline|Istruzioni e rapporti per pet sitter'],
'pt':[
'Pawverio – Cuidados e agenda pet',
'Organize alimentação, passeios e cuidados. Guarde os registros de saúde dos pets no seu dispositivo.',
'Uma rotina mais tranquila para cada pet. Crie um perfil, planeje os cuidados e registre as tarefas concluídas. Alimentação, passeios, higiene e consultas veterinárias em um só lugar.',
'Comece grátis com um pet, lembretes essenciais e registros veterinários.',
'Premium inclui mais pets, medicamentos e vacinas, histórico de peso, tarefas da família e relatórios para cuidadores.',
'Premium é uma compra única opcional. A loja Amazon mostra o preço local atual. Restaure compras nas configurações. Os registros de saúde existentes continuam acessíveis.',
'Fale um comando de cuidado ou digite. O reconhecimento e as respostas por voz dependem dos serviços de idioma do dispositivo; a voz offline não é garantida.',
'Não exige conta. Os registros ficam no dispositivo. Você escolhe o que exportar. Sem SDK de publicidade ou rastreamento automático de localização.',
'Pawverio é uma ferramenta de organização. Não faz diagnósticos nem recomenda dietas, dosagens ou tratamentos. Registre as instruções do veterinário.',
'Primeira versão: perfis locais, planejamento de cuidados, registros de saúde e interação por voz opcional.',
'Para suporte, informe a versão do aplicativo, o modelo do dispositivo e o problema. Não envie registros de saúde privados. Um contato de suporte verificado do editor deve ser adicionado antes da publicação.',
'Agenda de cuidados dos pets|Lembretes de alimentação|Consultas veterinárias e saúde|Registro de medicamentos dos pets|Agenda de higiene e cuidados|Cuidados dos pets com sua voz|Registro de cuidados offline|Instruções e relatórios para cuidadores'],
'nl':[
'Pawverio – Huisdierzorg & dagplanner',
'Plan eten, wandelen en verzorging. Bewaar de gezondheidsgegevens van je huisdier op je apparaat.',
'Een rustigere routine voor elk huisdier. Maak een profiel, plan de verzorging en leg voltooide taken vast. Eten, wandelen, verzorging en dierenartsafspraken op één plek.',
'Begin gratis met één huisdier, basisherinneringen en dierenartsgegevens.',
'Premium biedt meer huisdieren, medicatie en vaccinaties, gewichtsverloop, huishoudelijke taken en oppasrapporten.',
'Premium is een optionele eenmalige aankoop. De Amazon-winkel toont de actuele lokale prijs. Herstel aankopen via Instellingen. Bestaande gezondheidsgegevens blijven toegankelijk.',
'Spreek een verzorgingsopdracht of typ deze. Spraakherkenning en gesproken antwoorden hangen af van de taaldiensten op je apparaat; offline spraak is niet gegarandeerd.',
'Geen account nodig. Gegevens worden lokaal opgeslagen. Jij kiest wat je exporteert. Geen advertentie-SDK’s en geen automatische locatiebepaling.',
'Pawverio is een hulpmiddel voor organisatie. Het stelt geen diagnoses en adviseert geen diëten, doseringen of behandelingen. Noteer de instructies van je dierenarts.',
'Eerste versie: lokale profielen, zorgplanning, gezondheidsgegevens en optionele spraakbediening.',
'Vermeld voor hulp de appversie, het apparaatmodel en het probleem. Stuur geen privégezondheidsgegevens. Vóór publicatie moet een geverifieerd supportadres worden toegevoegd.',
'Huisdierzorg en dagplanner|Herinneringen voor eten|Dierenartsafspraken en gezondheid|Medicatie voor huisdieren bijhouden|Planning voor verzorging|Huisdierzorg met je stem|Huisdierzorg offline bijhouden|Instructies en rapporten voor de oppas'],
'pl':[
'Pawverio – Opieka i planer zwierząt',
'Planuj karmienie, spacery i pielęgnację. Przechowuj dokumentację zdrowotną zwierząt na urządzeniu.',
'Spokojniejsza rutyna dla każdego zwierzęcia. Utwórz profil, zaplanuj opiekę i zapisuj ukończone zadania. Karmienie, spacery, pielęgnacja i wizyty weterynaryjne w jednym miejscu.',
'Zacznij bezpłatnie z jednym zwierzęciem, podstawowymi przypomnieniami i dokumentacją weterynaryjną.',
'Premium dodaje więcej zwierząt, leki i szczepienia, historię wagi, zadania domowników i raporty dla opiekunów.',
'Premium to opcjonalny zakup jednorazowy. Sklep Amazon pokazuje aktualną cenę lokalną. Przywróć zakupy w ustawieniach. Istniejąca dokumentacja zdrowotna pozostaje dostępna.',
'Wypowiedz polecenie opieki lub je wpisz. Rozpoznawanie mowy i odpowiedzi głosowe zależą od usług językowych urządzenia. Działanie głosu offline nie jest gwarantowane.',
'Konto nie jest wymagane. Dane są przechowywane lokalnie. Samodzielnie wybierasz eksport. Bez reklamowych SDK i automatycznego śledzenia lokalizacji.',
'Pawverio służy do organizacji. Nie stawia diagnoz ani nie zaleca diet, dawek czy leczenia. Zapisuj zalecenia weterynarza.',
'Pierwsza wersja: lokalne profile, planowanie opieki, dokumentacja zdrowotna i opcjonalne sterowanie głosem.',
'Kontaktując się z pomocą, podaj wersję aplikacji, model urządzenia i opis problemu. Nie wysyłaj prywatnej dokumentacji zdrowotnej. Przed publikacją należy dodać zweryfikowany adres wsparcia wydawcy.',
'Opieka nad zwierzętami i planer|Przypomnienia o karmieniu|Wizyty weterynaryjne i zdrowie|Rejestr leków dla zwierząt|Harmonogram pielęgnacji i opieki|Planuj opiekę głosem|Rejestr opieki bez internetu|Instrukcje i raporty dla opiekunów'],
'sv':[
'Pawverio – Husdjursvård & dagsplanerare',
'Planera mat, promenader och skötsel. Samla husdjurets hälsouppgifter på din enhet.',
'En lugnare rutin för varje husdjur. Skapa en profil, planera omsorgen och registrera utförda uppgifter. Matning, promenader, pälsvård och veterinärbesök på ett ställe.',
'Börja gratis med ett husdjur, grundläggande påminnelser och veterinärjournaler.',
'Premium ger fler husdjur, läkemedel och vaccinationer, vikthistorik, hushållsuppgifter och rapporter till djurvakter.',
'Premium är ett valfritt engångsköp. Amazon-butiken visar aktuellt lokalt pris. Återställ köp i Inställningar. Befintliga hälsouppgifter är fortfarande tillgängliga.',
'Säg ett skötselkommando eller skriv det. Taligenkänning och talade svar beror på enhetens språktjänster. Röstfunktioner offline garanteras inte.',
'Inget konto krävs. Uppgifterna sparas lokalt. Du väljer vad du exporterar. Inga reklam-SDK:er eller automatisk platsspårning.',
'Pawverio är ett organisationsverktyg. Det ställer inga diagnoser och rekommenderar inga dieter, doser eller behandlingar. Anteckna veterinärens instruktioner.',
'Första versionen: lokala profiler, skötselplanering, hälsouppgifter och valfri röststyrning.',
'Ange appversion, enhetsmodell och problembeskrivning när du kontaktar support. Skicka inga privata hälsouppgifter. En verifierad supportadress måste läggas till före publicering.',
'Husdjursvård och dagsplanerare|Påminnelser om matning|Veterinärbesök och hälsouppgifter|Håll koll på husdjurets medicin|Schema för pälsvård och skötsel|Planera omsorgen med rösten|Registrera husdjursvård offline|Instruktioner och rapporter till djurvakt'],
'ja':[
'Pawverio – ペットのお世話・毎日の予定',
'食事、散歩、日々のお世話を計画。ペットの健康記録を端末にまとめて保存。',
'すべてのペットに穏やかな習慣を。プロフィールを作成し、お世話を計画して、完了したことを記録。食事のメモ、散歩、お手入れ、動物病院の予約をまとめて管理できます。',
'1匹のペット、基本のお世話リマインダー、動物病院の記録を無料で利用できます。',
'Premiumでは、複数のペット、投薬・予防接種の記録、体重履歴、家族の担当、シッターレポートを利用できます。',
'Premiumは任意の1回限りの購入です。現在の地域別価格はAmazonストアに表示されます。設定から購入を復元できます。既存の健康記録には引き続きアクセスできます。',
'お世話の指示は音声でも文字でも入力できます。音声認識と読み上げは端末の言語サービスに依存します。オフラインでの音声利用は保証されません。',
'アカウントは不要です。ペットの記録は端末に保存されます。書き出しはご自身で選択できます。広告SDKや自動位置追跡はありません。',
'Pawverioは記録・整理用のアプリです。診断や食事、投薬量、治療の推奨は行いません。獣医師の指示を記録してください。',
'初回リリース：端末内のペットプロフィール、お世話の計画、健康記録、任意の音声操作。',
'サポートにはアプリのバージョン、端末の機種、問題の内容をお知らせください。個人の健康記録は送信しないでください。公開前に発行者の確認済みサポート窓口を追加する必要があります。',
'ペットのお世話と毎日の予定|ペットの食事リマインダー|動物病院の予約と健康記録|ペットの投薬を記録|お手入れとお世話の予定|声でペットのお世話を計画|オフラインでお世話を記録|シッターへの指示とレポート'],
'hi':[
'Pawverio – पालतू देखभाल और दैनिक योजनाकार',
'भोजन, सैर और रोज़ की देखभाल की योजना बनाएँ। स्वास्थ्य रिकॉर्ड डिवाइस पर साथ रखें।',
'हर पालतू के लिए सुकून भरी दिनचर्या। प्रोफ़ाइल बनाएँ, देखभाल की योजना बनाएँ और पूरे हुए काम दर्ज करें। भोजन, सैर, साफ़-सफ़ाई और पशु चिकित्सक की अपॉइंटमेंट एक जगह रखें।',
'एक पालतू, ज़रूरी रिमाइंडर और पशु चिकित्सा रिकॉर्ड के साथ मुफ़्त शुरुआत करें।',
'Premium में अधिक पालतू, दवा और टीकाकरण रिकॉर्ड, वज़न का इतिहास, घर के काम और देखभालकर्ता रिपोर्ट मिलती हैं।',
'Premium वैकल्पिक एकमुश्त खरीदारी है। Amazon स्टोर मौजूदा स्थानीय कीमत दिखाता है। सेटिंग में खरीदारी बहाल करें। पुराने स्वास्थ्य रिकॉर्ड उपलब्ध रहते हैं।',
'देखभाल का निर्देश बोलें या लिखें। आवाज़ पहचान और बोलकर जवाब डिवाइस की भाषा सेवाओं पर निर्भर करते हैं; ऑफ़लाइन वॉइस की गारंटी नहीं है।',
'खाते की ज़रूरत नहीं है। रिकॉर्ड स्थानीय रूप से सहेजे जाते हैं। निर्यात आपकी पसंद है। विज्ञापन SDK या अपने-आप स्थान ट्रैकिंग नहीं है।',
'Pawverio योजना बनाने का साधन है। यह निदान या आहार, खुराक और उपचार की सलाह नहीं देता। पशु चिकित्सक के निर्देश दर्ज करें।',
'पहला संस्करण: स्थानीय पालतू प्रोफ़ाइल, देखभाल योजना, स्वास्थ्य रिकॉर्ड और वैकल्पिक वॉइस सुविधा।',
'सहायता के लिए ऐप संस्करण, डिवाइस मॉडल और समस्या बताएँ। निजी स्वास्थ्य रिकॉर्ड न भेजें। प्रकाशन से पहले प्रकाशक का सत्यापित सहायता पता जोड़ना होगा।',
'पालतू देखभाल और दैनिक योजना|पालतू के भोजन के रिमाइंडर|पशु चिकित्सक की अपॉइंटमेंट और स्वास्थ्य रिकॉर्ड|पालतू की दवा का रिकॉर्ड|साफ़-सफ़ाई और देखभाल की योजना|अपनी आवाज़ से देखभाल की योजना|बिना इंटरनेट पालतू देखभाल का रिकॉर्ड|देखभालकर्ता के निर्देश और रिपोर्ट'],
'ar':[
'Pawverio – رعاية الحيوانات والتخطيط اليومي',
'خطط للإطعام والمشي والرعاية اليومية. احتفظ بالسجلات الصحية لحيوانك على جهازك.',
'روتين أهدأ لكل حيوان أليف. أنشئ ملفًا وخطط للرعاية وسجّل المهام المكتملة. اجمع ملاحظات الطعام والمشي والعناية والمواعيد البيطرية في مكان واحد.',
'ابدأ مجانًا بحيوان أليف واحد وتذكيرات أساسية وسجلات بيطرية.',
'يضيف Premium حيوانات أكثر وسجلات الأدوية والتطعيمات وتاريخ الوزن ومهام أفراد المنزل وتقارير الجليس.',
'Premium شراء اختياري لمرة واحدة. يعرض متجر Amazon السعر المحلي الحالي. استعد المشتريات من الإعدادات. تظل السجلات الصحية الحالية متاحة.',
'انطق أمر رعاية أو اكتبه. يعتمد التعرف على الكلام والردود المنطوقة على خدمات اللغة في جهازك؛ لا نضمن تشغيل الصوت دون إنترنت.',
'لا يلزم حساب. تُحفظ السجلات محليًا. أنت تختار التصدير. لا توجد حزم إعلانات أو تتبع تلقائي للموقع.',
'Pawverio أداة تنظيم. لا يشخّص الحالات ولا يوصي بأنظمة غذائية أو جرعات أو علاجات. سجّل تعليمات الطبيب البيطري.',
'الإصدار الأول: ملفات محلية للحيوانات وتخطيط الرعاية وسجلات صحية وتفاعل صوتي اختياري.',
'للدعم، اذكر إصدار التطبيق وطراز الجهاز ووصف المشكلة. لا ترسل سجلات صحية خاصة. يجب إضافة عنوان دعم موثّق للناشر قبل الإصدار.',
'رعاية الحيوانات والتخطيط اليومي|تذكيرات إطعام الحيوانات الأليفة|المواعيد البيطرية والسجلات الصحية|سجل أدوية الحيوان الأليف|جدول النظافة والرعاية|خطط لرعاية حيوانك بصوتك|سجل الرعاية دون إنترنت|تعليمات وتقارير جليس الحيوان'],
'tr':[
'Pawverio – Evcil Hayvan Bakım ve Gün Planı',
'Besleme, yürüyüş ve günlük bakımı planlayın. Sağlık kayıtlarını cihazınızda bir arada tutun.',
'Her evcil hayvan için daha sakin bir rutin. Profil oluşturun, bakımı planlayın ve tamamlanan işleri kaydedin. Beslenme notları, yürüyüşler, temizlik ve veteriner randevuları bir arada.',
'Bir evcil hayvan, temel hatırlatıcılar ve veteriner kayıtlarıyla ücretsiz başlayın.',
'Premium daha fazla evcil hayvan, ilaç ve aşı kayıtları, ağırlık geçmişi, ev halkı görevleri ve bakıcı raporları ekler.',
'Premium isteğe bağlı, tek seferlik satın alımdır. Amazon mağazası güncel yerel fiyatı gösterir. Satın alımları Ayarlar’dan geri yükleyin. Mevcut sağlık kayıtları erişilebilir kalır.',
'Bakım komutunu söyleyin veya yazın. Konuşma tanıma ve sesli yanıtlar cihazın dil hizmetlerine bağlıdır; çevrimdışı ses kullanımı garanti edilmez.',
'Hesap gerekmez. Kayıtlar yerel olarak saklanır. Dışa aktarmayı siz seçersiniz. Reklam SDK’sı veya otomatik konum takibi yoktur.',
'Pawverio bir düzenleme aracıdır. Tanı koymaz; diyet, doz veya tedavi önermez. Veterinerinizin talimatlarını kaydedin.',
'İlk sürüm: yerel profiller, bakım planlama, sağlık kayıtları ve isteğe bağlı sesli etkileşim.',
'Destek için uygulama sürümü, cihaz modeli ve sorunu belirtin. Özel sağlık kayıtları göndermeyin. Yayından önce yayıncının doğrulanmış destek adresi eklenmelidir.',
'Evcil Hayvan Bakımı ve Günlük Plan|Evcil Hayvan Besleme Hatırlatıcıları|Veteriner Randevuları ve Sağlık Kayıtları|Evcil Hayvan İlaç Takibi|Temizlik ve Bakım Takvimi|Sesinizle Evcil Hayvan Bakım Planı|Çevrimdışı Evcil Hayvan Bakım Kaydı|Bakıcı Talimatları ve Raporları'],
'id':[
'Pawverio – Perawatan Hewan & Agenda Harian',
'Rencanakan makan, jalan-jalan, dan perawatan. Simpan catatan kesehatan hewan di perangkat Anda.',
'Rutinitas lebih tenang untuk setiap hewan. Buat profil, rencanakan perawatan, dan catat tugas yang selesai. Catatan makanan, jalan-jalan, perawatan, dan janji dokter hewan di satu tempat.',
'Mulai gratis dengan satu hewan, pengingat dasar, dan catatan dokter hewan.',
'Premium menambahkan lebih banyak hewan, catatan obat dan vaksinasi, riwayat berat, tugas keluarga, dan laporan pengasuh.',
'Premium adalah pembelian sekali bayar opsional. Toko Amazon menampilkan harga lokal saat ini. Pulihkan pembelian dari Pengaturan. Catatan kesehatan yang ada tetap dapat diakses.',
'Ucapkan perintah perawatan atau ketik. Pengenalan dan tanggapan suara bergantung pada layanan bahasa perangkat; suara offline tidak dijamin.',
'Tidak perlu akun. Catatan disimpan secara lokal. Anda memilih apa yang diekspor. Tanpa SDK iklan atau pelacakan lokasi otomatis.',
'Pawverio adalah alat pengaturan. Aplikasi tidak mendiagnosis atau menyarankan diet, dosis, maupun pengobatan. Catat petunjuk dokter hewan.',
'Rilis pertama: profil lokal, rencana perawatan, catatan kesehatan, dan interaksi suara opsional.',
'Untuk bantuan, sertakan versi aplikasi, model perangkat, dan masalah. Jangan kirim catatan kesehatan pribadi. Alamat dukungan penerbit yang terverifikasi harus ditambahkan sebelum rilis.',
'Perawatan Hewan dan Agenda Harian|Pengingat Makan Hewan|Janji Dokter Hewan dan Catatan Kesehatan|Pencatatan Obat Hewan|Jadwal Kebersihan dan Perawatan|Rencanakan Perawatan dengan Suara|Catatan Perawatan Hewan Offline|Petunjuk dan Laporan Pengasuh']
}

def main():
    folder=ROOT/'deliverables/store-assets/metadata';folder.mkdir(parents=True,exist_ok=True)
    screenshots={}; index=[]
    for locale in p.LOCALES:
        language=locale.split('-')[0];copy=COPY[language].copy();d=p.dictionary(locale)
        if locale=='pt-PT':
            replacements={'pets':'animais','pet':'animal','registros':'registos','registre':'registe','Registre':'Registe','planeje':'planeie','Planeje':'Planeie','você':'o utilizador','Você':'O utilizador','configurações':'definições','aplicativo':'aplicação','suporte':'apoio','rastreamento':'rastreio','compartilh':'partilh'}
            for old,new in replacements.items(): copy=[s.replace(old,new) for s in copy]
        headlines=copy[-1].split('|'); assert len(headlines)==8
        body='\n\n'.join(copy[2:10])
        features=[d['feeding']+' · '+d['walking']+' · '+d['grooming'],d['vet']+' · '+d['medication']+' · '+d['vaccination'],d['weight_history']+' · '+d['household'],d['sitter_mode']+' · '+d['export_pdf'],d['offline_label']]
        keywords=[d[k] for k in ['feeding','walking','grooming','vet','medication','vaccination','weight_history','sitter_mode']]
        listing={'locale':locale,'status':'DRAFT_NOT_APPROVED','human_review':'PENDING','functional_claim_review':'PENDING_END_TO_END_VERIFICATION','display_title':copy[0],'short_description':copy[1],'long_description':body,'product_feature_bullets':features,'keywords':keywords,'release_notes':copy[9],'support_copy':copy[10],'screenshot_headlines':headlines,'screenshot_captions':[d[k] for k in ['today_tasks','feeding','nav_health','medication','grooming','voice_text_hint','offline_label','sitter_mode']],'privacy_policy_draft':{'notice':d['privacy_body'],'export_notice':d['export_confirm'],'data_deletion':d['delete_data'],'medical_notice':d['medical_notice'],'publication_blockers':['Publisher legal identity and support contact','Review actual SDK data processing and Amazon privacy labels','Data subject request handling and applicable legal review']}}
        p.write(folder/f'{locale}.json',listing)
        screenshots[locale]={'headlines':headlines,'captions':listing['screenshot_captions'],'review':'PENDING_HUMAN_REVIEW'}
        index.append({'locale':locale,'file':f'{locale}.json','title_characters':len(copy[0]),'short_bytes_utf8':len(copy[1].encode()),'long_characters':len(body),'short_within_2000_bytes':len(copy[1].encode())<=2000,'long_within_4000_characters':len(body)<=4000,'feature_bullets':len(features),'approval':'PENDING'})
    p.write(ROOT/'localization/screenshot-copy.json',screenshots)
    with (folder/'metadata-validation.csv').open('w',encoding='utf-8',newline='') as fh:
        writer=csv.DictWriter(fh,fieldnames=list(index[0]));writer.writeheader();writer.writerows(index)
    print(f'Generated {len(index)} localized editorial listing drafts and screenshot copy; none approved for submission.')

if __name__=='__main__':main()
