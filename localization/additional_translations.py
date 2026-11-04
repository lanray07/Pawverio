"""Additional AI-authored interface translations; human review pending."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parent
EXTRA_KEYS=['backup_records_only','restore_backup','backup_password_hint','backup_invalid','lock_unavailable','unlock','backup_restored','enabled']
TEXT={
'en': ['Back up records (photos and documents are not included).','Restore backup','Use at least 12 characters. Keep your password safe; it cannot be recovered.','The backup or password is invalid.','Set up a device screen lock to use app lock.','Unlock','Backup restored.','Enabled'],
'fr-FR':['Sauvegardez les dossiers (photos et documents non inclus).','Restaurer la sauvegarde','Utilisez au moins 12 caractères. Conservez votre mot de passe : il ne peut pas être récupéré.','La sauvegarde ou le mot de passe est invalide.','Configurez le verrouillage de l’écran de l’appareil pour verrouiller l’application.','Déverrouiller','Sauvegarde restaurée.','Activé'],
'es-ES':['Copia de los registros (no incluye fotos ni documentos).','Restaurar copia de seguridad','Usa al menos 12 caracteres. Guarda tu contraseña; no se puede recuperar.','La copia de seguridad o la contraseña no es válida.','Configura el bloqueo de pantalla del dispositivo para bloquear la aplicación.','Desbloquear','Copia de seguridad restaurada.','Activado'],
'de-DE':['Datensicherung (Fotos und Dokumente sind nicht enthalten).','Sicherung wiederherstellen','Mindestens 12 Zeichen verwenden. Passwort sicher aufbewahren; es kann nicht wiederhergestellt werden.','Die Sicherung oder das Passwort ist ungültig.','Richte eine Gerätesperre ein, um die App-Sperre zu verwenden.','Entsperren','Sicherung wiederhergestellt.','Aktiviert'],
'it-IT':['Backup dei dati (foto e documenti non inclusi).','Ripristina backup','Usa almeno 12 caratteri. Conserva la password al sicuro: non può essere recuperata.','Il backup o la password non è valido.','Configura il blocco schermo del dispositivo per usare il blocco dell’app.','Sblocca','Backup ripristinato.','Attivo'],
'pt-BR':['Backup dos registros (fotos e documentos não incluídos).','Restaurar backup','Use pelo menos 12 caracteres. Guarde a senha com segurança; ela não pode ser recuperada.','O backup ou a senha é inválido.','Configure o bloqueio de tela do dispositivo para usar o bloqueio do aplicativo.','Desbloquear','Backup restaurado.','Ativado'],
'pt-PT':['Cópia dos registos (fotografias e documentos não incluídos).','Restaurar cópia de segurança','Use pelo menos 12 carateres. Guarde a palavra-passe em segurança; não pode ser recuperada.','A cópia de segurança ou a palavra-passe é inválida.','Configure o bloqueio de ecrã do dispositivo para usar o bloqueio da aplicação.','Desbloquear','Cópia de segurança restaurada.','Ativado'],
'nl-NL':['Back-up van gegevens (zonder foto’s en documenten).','Back-up herstellen','Gebruik minstens 12 tekens. Bewaar je wachtwoord veilig; het kan niet worden hersteld.','De back-up of het wachtwoord is ongeldig.','Stel schermvergrendeling in om appvergrendeling te gebruiken.','Ontgrendelen','Back-up hersteld.','Ingeschakeld'],
'pl-PL':['Kopia danych (bez zdjęć i dokumentów).','Przywróć kopię zapasową','Użyj co najmniej 12 znaków. Zachowaj hasło w bezpiecznym miejscu; nie można go odzyskać.','Kopia zapasowa lub hasło jest nieprawidłowe.','Ustaw blokadę ekranu urządzenia, aby używać blokady aplikacji.','Odblokuj','Przywrócono kopię zapasową.','Włączone'],
'sv-SE':['Säkerhetskopiera uppgifter (foton och dokument ingår inte).','Återställ säkerhetskopia','Använd minst 12 tecken. Förvara lösenordet säkert; det kan inte återställas.','Säkerhetskopian eller lösenordet är ogiltigt.','Ställ in enhetens skärmlås för att använda applås.','Lås upp','Säkerhetskopian återställd.','Aktiverat'],
'ja-JP':['記録をバックアップ（写真と書類は含まれません）。','バックアップを復元','12文字以上を使用してください。パスワードは復元できないため、安全に保管してください。','バックアップまたはパスワードが無効です。','アプリロックを使うには、端末の画面ロックを設定してください。','ロック解除','バックアップを復元しました。','有効'],
'hi-IN':['रिकॉर्ड का बैकअप लें (फ़ोटो और दस्तावेज़ शामिल नहीं हैं)।','बैकअप बहाल करें','कम से कम 12 अक्षर इस्तेमाल करें। पासवर्ड सुरक्षित रखें; इसे वापस नहीं पाया जा सकता।','बैकअप या पासवर्ड अमान्य है।','ऐप लॉक के लिए डिवाइस पर स्क्रीन लॉक सेट करें।','अनलॉक करें','बैकअप बहाल किया गया।','चालू'],
'ar':['نسخ السجلات احتياطيًا (لا يشمل الصور والمستندات).','استعادة النسخة الاحتياطية','استخدم 12 حرفًا على الأقل. احتفظ بكلمة المرور بأمان؛ لا يمكن استعادتها.','النسخة الاحتياطية أو كلمة المرور غير صالحة.','أعدّ قفل شاشة الجهاز لاستخدام قفل التطبيق.','فتح القفل','تمت استعادة النسخة الاحتياطية.','مفعّل'],
'tr-TR':['Kayıtları yedekleyin (fotoğraflar ve belgeler dahil değildir).','Yedeği geri yükle','En az 12 karakter kullanın. Parolanızı güvende tutun; kurtarılamaz.','Yedek veya parola geçersiz.','Uygulama kilidi için cihaz ekran kilidini ayarlayın.','Kilidi aç','Yedek geri yüklendi.','Etkin'],
'id-ID':['Cadangkan catatan (foto dan dokumen tidak disertakan).','Pulihkan cadangan','Gunakan minimal 12 karakter. Simpan kata sandi dengan aman; kata sandi tidak dapat dipulihkan.','Cadangan atau kata sandi tidak valid.','Siapkan kunci layar perangkat untuk menggunakan kunci aplikasi.','Buka kunci','Cadangan dipulihkan.','Aktif']
}

def materialize():
    catalog=json.loads((ROOT/'catalog.json').read_text(encoding='utf-8'))
    catalog.update(dict(zip(EXTRA_KEYS,TEXT['en'])))
    (ROOT/'catalog.json').write_text(json.dumps(catalog,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    for locale,values in TEXT.items():
        if locale=='en': continue
        path=ROOT/'translations'/f'{locale}.json'
        data=json.loads(path.read_text(encoding='utf-8'))
        data.update(dict(zip(EXTRA_KEYS,values)))
        path.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')

MORE_KEYS=['quiet_start_hour','quiet_end_hour','attachment_export']
MORE={
'en':['Quiet hours start (0–23)','Quiet hours end (0–23)','Export attachment'],
'fr-FR':['Début des heures calmes (0–23)','Fin des heures calmes (0–23)','Exporter la pièce jointe'],
'es-ES':['Inicio del silencio (0–23)','Fin del silencio (0–23)','Exportar archivo adjunto'],
'de-DE':['Beginn der Ruhezeit (0–23)','Ende der Ruhezeit (0–23)','Anhang exportieren'],
'it-IT':['Inizio ore di silenzio (0–23)','Fine ore di silenzio (0–23)','Esporta allegato'],
'pt-BR':['Início do silêncio (0–23)','Fim do silêncio (0–23)','Exportar anexo'],
'pt-PT':['Início das horas de silêncio (0–23)','Fim das horas de silêncio (0–23)','Exportar anexo'],
'nl-NL':['Begin stille uren (0–23)','Einde stille uren (0–23)','Bijlage exporteren'],
'pl-PL':['Początek ciszy (0–23)','Koniec ciszy (0–23)','Eksportuj załącznik'],
'sv-SE':['Tysta timmar börjar (0–23)','Tysta timmar slutar (0–23)','Exportera bilaga'],
'ja-JP':['通知を控える開始時刻（0～23）','通知を控える終了時刻（0～23）','添付ファイルを書き出す'],
'hi-IN':['शांत समय शुरू (0–23)','शांत समय समाप्त (0–23)','अटैचमेंट निर्यात करें'],
'ar':['بداية ساعات الهدوء (0–23)','نهاية ساعات الهدوء (0–23)','تصدير المرفق'],
'tr-TR':['Sessiz saat başlangıcı (0–23)','Sessiz saat bitişi (0–23)','Eki dışa aktar'],
'id-ID':['Mulai jam tenang (0–23)','Akhir jam tenang (0–23)','Ekspor lampiran']
}

def more():
    for locale,values in MORE.items():
        path=ROOT/'catalog.json' if locale=='en' else ROOT/'translations'/f'{locale}.json'
        data=json.loads(path.read_text(encoding='utf-8'))
        data.update(dict(zip(MORE_KEYS,values)))
        if not data['distance'].endswith('(km)'):data['distance']+=' (km)'
        path.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')

def regional():
    # Region overrides are explicit files, preserving each locale's review status.
    for target,base,replacements in [
        ('en-US','en',{'recognised':'recognized','Recognised':'Recognized','cancelled':'canceled','organiser':'organizer'}),
        ('en-AU','en',{}),
        ('es-419','es-ES',{'Añadir':'Agregar','añadir':'agregar','Añade':'Agrega','añade':'agrega','Ajustes':'Configuración','ajustes':'configuración'}),
        ('fr-CA','fr-FR',{'Réglages':'Paramètres','réglages':'paramètres','Touchez':'Touchez'})
    ]:
        base_path=ROOT/'catalog.json' if base=='en' else ROOT/'translations'/f'{base}.json'
        data=json.loads(base_path.read_text(encoding='utf-8'))
        for key,value in data.items():
            for old,new in replacements.items():value=value.replace(old,new)
            data[key]=value
        path=ROOT/'translations'/f'{target}.json'
        path.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')

PRIVACY={
'en':'Pet records are stored on this device. Installed speech recognition and text-to-speech services may process audio and text, including records you choose to have read aloud, under their provider’s terms. Pawverio does not save microphone recordings. You choose whether to export records.',
'fr-FR':'Les dossiers des animaux sont enregistrés sur cet appareil. Les services installés de reconnaissance et de synthèse vocales peuvent traiter l’audio et le texte, y compris les dossiers que vous choisissez de faire lire à voix haute, selon les conditions de leur fournisseur. Pawverio ne conserve aucun enregistrement du microphone. Vous choisissez d’exporter ou non les dossiers.',
'es-ES':'Los registros se guardan en este dispositivo. Los servicios instalados de reconocimiento de voz y texto a voz pueden procesar audio y texto, incluidos los registros que elijas escuchar en voz alta, según las condiciones de su proveedor. Pawverio no guarda grabaciones del micrófono. Tú decides si exportas los registros.',
'de-DE':'Tierdaten werden auf diesem Gerät gespeichert. Installierte Spracherkennungs- und Vorlesedienste können Audio und Text, einschließlich der von dir zum Vorlesen ausgewählten Daten, gemäß den Bedingungen ihres Anbieters verarbeiten. Pawverio speichert keine Mikrofonaufnahmen. Du entscheidest, ob du Daten exportierst.',
'it-IT':'I dati degli animali sono salvati su questo dispositivo. I servizi installati di riconoscimento e sintesi vocale possono elaborare audio e testo, compresi i dati che scegli di far leggere ad alta voce, secondo i termini del loro fornitore. Pawverio non salva registrazioni del microfono. Decidi tu se esportare i dati.',
'pt-BR':'Os registros dos pets são salvos neste dispositivo. Os serviços instalados de reconhecimento e síntese de voz podem processar áudio e texto, incluindo os registros que você escolher ouvir em voz alta, conforme os termos do fornecedor desses serviços. O Pawverio não salva gravações do microfone. Você decide se deseja exportar os registros.',
'pt-PT':'Os registos dos animais são guardados neste dispositivo. Os serviços instalados de reconhecimento e síntese de voz podem processar áudio e texto, incluindo os registos que escolher ouvir em voz alta, nos termos do fornecedor desses serviços. O Pawverio não guarda gravações do microfone. O utilizador decide se pretende exportar os registos.',
'nl-NL':'Huisdiergegevens worden op dit apparaat opgeslagen. Geïnstalleerde spraakherkennings- en voorleesdiensten kunnen audio en tekst verwerken, waaronder gegevens die je laat voorlezen, volgens de voorwaarden van hun aanbieder. Pawverio bewaart geen microfoonopnamen. Jij beslist of je gegevens exporteert.',
'pl-PL':'Dane zwierząt są zapisywane na tym urządzeniu. Zainstalowane usługi rozpoznawania mowy i syntezy mowy mogą przetwarzać dźwięk i tekst, w tym dane wybrane do odczytania na głos, zgodnie z warunkami ich dostawcy. Pawverio nie zapisuje nagrań z mikrofonu. Samodzielnie decydujesz o eksporcie danych.',
'sv-SE':'Husdjursuppgifter sparas på den här enheten. Installerade tjänster för taligenkänning och talsyntes kan behandla ljud och text, inklusive uppgifter som du väljer att få upplästa, enligt leverantörens villkor. Pawverio sparar inga mikrofoninspelningar. Du väljer om uppgifterna ska exporteras.',
'ja-JP':'ペットの記録はこの端末に保存されます。端末にインストールされた音声認識・読み上げサービスは、読み上げを選択した記録を含む音声やテキストを、サービス提供元の規約に従って処理する場合があります。Pawverioはマイクの録音を保存しません。記録を書き出すかどうかはご自身で選択できます。',
'hi-IN':'पालतू के रिकॉर्ड इस डिवाइस पर सहेजे जाते हैं। इंस्टॉल की गई आवाज़ पहचान और टेक्स्ट-टू-स्पीच सेवाएँ अपने प्रदाता की शर्तों के अनुसार ऑडियो और टेक्स्ट को प्रोसेस कर सकती हैं, जिसमें वे रिकॉर्ड भी शामिल हैं जिन्हें आप सुनने के लिए चुनते हैं। Pawverio माइक्रोफ़ोन की रिकॉर्डिंग नहीं सहेजता। रिकॉर्ड निर्यात करना आपकी पसंद है।',
'ar':'تُحفظ سجلات الحيوانات الأليفة على هذا الجهاز. قد تعالج خدمات التعرف على الكلام وتحويل النص إلى كلام المثبتة الصوت والنص، بما في ذلك السجلات التي تختار قراءتها بصوت عالٍ، وفق شروط مقدم تلك الخدمات. لا يحفظ Pawverio تسجيلات الميكروفون. أنت تختار ما إذا كنت تريد تصدير السجلات.',
'tr-TR':'Evcil hayvan kayıtları bu cihazda saklanır. Yüklü konuşma tanıma ve metin okuma hizmetleri, sesli okutmayı seçtiğiniz kayıtlar dahil ses ve metinleri kendi sağlayıcılarının koşullarına göre işleyebilir. Pawverio mikrofon kayıtlarını saklamaz. Kayıtları dışa aktarıp aktarmamayı siz seçersiniz.',
'id-ID':'Catatan hewan disimpan di perangkat ini. Layanan pengenalan suara dan pembacaan teks yang terpasang dapat memproses audio dan teks, termasuk catatan yang Anda pilih untuk dibacakan, sesuai ketentuan penyedia layanan tersebut. Pawverio tidak menyimpan rekaman mikrofon. Anda memilih apakah akan mengekspor catatan.'
}

def privacy():
    for locale,value in PRIVACY.items():
        path=ROOT/'catalog.json' if locale=='en' else ROOT/'translations'/f'{locale}.json'
        data=json.loads(path.read_text(encoding='utf-8'))
        data['privacy_body']=value
        path.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')

if __name__=='__main__':
    materialize()
    more()
    privacy()
    regional()
