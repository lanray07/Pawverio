"""AI-authored draft translations; not human reviewed. Run to materialise JSON catalogs."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parent
CATALOG=dict(list(json.loads((ROOT/'catalog.json').read_text(encoding='utf-8')).items())[:168])
TRANSLATIONS={}

TRANSLATIONS['fr-FR']='''Pawverio
Chaque animal. Chaque routine. Des soins tout en douceur.
Aujourd’hui
Animaux
Santé
Historique
Réglages
Ajouter un animal
Modifier l’animal
Profil de l’animal
Nom
Espèce
Chien
Chat
Autre
Race
Âge
Date de naissance
Poids
Référence de la puce
Allergies ou sensibilités
Notes
Photo
Ajouter une photo
Enregistrer
Annuler
Supprimer
Modifier
Fermer
Suivant
Retour
Passer
Terminé
Confirmer
Rechercher
Facultatif
Repas
Promenade
Toilettage
Médicament
Rendez-vous vétérinaire
Vaccination
Soin personnalisé
Ajouter un soin
Modifier le soin
Nom de la tâche
Animal
Date prévue
Heure
Répéter
Une fois
Tous les jours
Toutes les semaines
Tous les mois
Répéter tous les (jours)
Marquer comme terminé
Annuler l’action
Reporter de 15 minutes
Mettre en pause
Reprendre
En pause
Terminé
À venir
Soins du jour
Activité récente
Les petites attentions commencent ici
Ajoutez votre premier animal pour organiser ses soins quotidiens.
Une journée sereine en perspective
Ajoutez un rappel de repas, de promenade ou de soin.
Les soins effectués apparaîtront ici.
Rassemblez les dossiers de votre animal.
Chargement…
Un problème est survenu. Veuillez réessayer.
Réessayer
Veuillez saisir un nom.
Saisissez un nombre positif valide.
Supprimer cet animal et tous ses dossiers ? Cette action est irréversible.
Supprimer toutes les données locales des animaux ? Cette action est irréversible.
Portion
Type de repas
Notes alimentaires
Durée (minutes)
Distance
Enregistrer une promenade
Clinique ou praticien
Coordonnées
Pièce jointe
Ajouter un document
Posologie prescrite
Date de début
Date de fin
Nom du vaccin
Date d’administration
Prochaine échéance
Ajouter un dossier de santé
Journal du poids
Ajouter une pesée
Mesures
Attribué à
Soins du foyer
Membre du foyer
Rapport de garde
Consignes du foyer
Contact d’urgence
Exporter en texte
Exporter en PDF
Exporter les données
Ce fichier peut contenir des données de santé et des coordonnées privées. Continuer ?
Export enregistré.
Pawverio Premium
Plus d’animaux, des outils de santé et des rapports de garde avec un achat unique.
Débloquer Premium
Restaurer les achats
La boutique Amazon est indisponible. Vos dossiers existants restent accessibles.
Vérification de votre achat…
L’achat a été annulé.
L’achat n’a pas pu aboutir. Veuillez réessayer.
Premium est actif
Langue
Utiliser la langue de l’appareil
Unités de mesure
Kilogrammes (kg)
Livres (lb)
Apparence
Utiliser le thème de l’appareil
Clair
Sombre
Rappels
Autorisez les notifications pour recevoir les rappels de soins.
Heures calmes
Les rappels sont retardés pendant les heures calmes. Vérifiez directement les soins urgents.
Confidentialité
Les dossiers restent sur cet appareil sauf si vous les exportez. Les services vocaux peuvent traiter l’audio selon les conditions du fournisseur de votre appareil. Pawverio ne conserve aucun enregistrement du microphone.
Supprimer toutes les données des animaux
Sauvegarde chiffrée
Mot de passe de sauvegarde
Verrouillage de l’application
Réponses vocales
Réduire les animations
Prenez soin d’eux à la voix
Touchez pour parler
Arrêter l’écoute
Écoute en cours…
Vérifier le texte reconnu
Exécuter la commande
Saisissez ou dictez une commande de soin
La reconnaissance vocale est indisponible sur cet appareil. Saisissez une commande ou utilisez les écrans de soins.
Autorisez le microphone lorsque vous choisissez de dicter une commande.
Le microphone est désactivé. Saisissez une commande ou autorisez l’accès dans les réglages de l’appareil.
La parole n’a pas été reconnue. Réessayez ou saisissez votre commande.
Cette commande n’a pas été reconnue. Essayez d’ouvrir un écran de soins.
Vérifiez cette action de soin avant de confirmer.
Les réponses vocales sont indisponibles dans cette langue sur cet appareil.
Bienvenue dans Pawverio
Choisissez votre langue
Présentez votre premier animal
Créez une routine attentionnée
Vos soins, votre contrôle
Commencer
Enregistré sur cet appareil
Consignez les instructions de votre vétérinaire. Pawverio ne recommande aucun régime, dosage ou traitement.
Photo de profil de l’animal
Ouvrir l’assistant vocal
Ajouter un élément
AAAA-MM-JJ
HH:MM
%1$d soin
%1$d soins'''

TRANSLATIONS['es-ES']='''Pawverio
Cada mascota. Cada rutina. Cuidados con cariño.
Hoy
Mascotas
Salud
Historial
Ajustes
Añadir mascota
Editar mascota
Perfil de la mascota
Nombre
Especie
Perro
Gato
Otra
Raza
Edad
Fecha de nacimiento
Peso
Referencia del microchip
Alergias o sensibilidades
Notas
Foto
Añadir foto
Guardar
Cancelar
Eliminar
Editar
Cerrar
Siguiente
Atrás
Omitir
Hecho
Confirmar
Buscar
Opcional
Alimentación
Paseo
Aseo
Medicación
Cita veterinaria
Vacunación
Cuidado personalizado
Añadir tarea de cuidado
Editar tarea de cuidado
Nombre de la tarea
Mascota
Fecha prevista
Hora
Repetir
Una vez
Cada día
Cada semana
Cada mes
Repetir cada (días)
Marcar como completada
Deshacer
Posponer 15 minutos
Pausar
Reanudar
En pausa
Completada
Próximamente
Cuidados de hoy
Actividad reciente
Los pequeños cuidados empiezan aquí
Añade tu primera mascota para organizar sus cuidados diarios.
Un día tranquilo por delante
Añade un recordatorio de comida, paseo o cuidado.
Los cuidados completados aparecerán aquí.
Reúne los registros de tu mascota.
Cargando…
Algo ha fallado. Vuelve a intentarlo.
Reintentar
Introduce un nombre.
Introduce un número positivo válido.
¿Eliminar esta mascota y todos sus registros? No se puede deshacer.
¿Eliminar todos los datos locales de mascotas? No se puede deshacer.
Porción
Tipo de comida
Notas de alimentación
Duración (minutos)
Distancia
Registrar paseo
Clínica o profesional
Datos de contacto
Archivo adjunto
Añadir documento
Dosis prescrita
Fecha de inicio
Fecha de fin
Nombre de la vacuna
Fecha de administración
Próxima fecha prevista
Añadir registro de salud
Diario de peso
Añadir registro de peso
Medidas
Asignado a
Cuidados del hogar
Miembro del hogar
Informe para cuidadores
Instrucciones del hogar
Contacto de emergencia
Exportar texto
Exportar PDF
Exportar datos
Este archivo puede incluir datos privados de salud y contacto. ¿Continuar?
Exportación guardada.
Pawverio Premium
Más mascotas, herramientas de salud e informes para cuidadores con un solo pago.
Desbloquear Premium
Restaurar compras
La tienda Amazon no está disponible. Tus registros existentes siguen accesibles.
Comprobando tu compra…
La compra se ha cancelado.
No se ha podido completar la compra. Inténtalo de nuevo.
Premium está activo
Idioma
Usar idioma del dispositivo
Unidades de medida
Kilogramos (kg)
Libras (lb)
Apariencia
Usar tema del dispositivo
Claro
Oscuro
Recordatorios
Permite las notificaciones para recibir recordatorios de cuidado.
Horas de silencio
Los recordatorios se retrasan durante las horas de silencio. Revisa directamente los cuidados urgentes.
Privacidad
Los registros permanecen en este dispositivo salvo que los exportes. Los servicios de voz pueden procesar audio según las condiciones del proveedor del dispositivo. Pawverio no guarda grabaciones del micrófono.
Eliminar todos los datos de mascotas
Copia de seguridad cifrada
Contraseña de la copia
Bloqueo de la aplicación
Respuestas habladas
Reducir movimiento
Cuida con tu voz
Toca para hablar
Dejar de escuchar
Escuchando…
Revisar texto reconocido
Ejecutar comando
Escribe o di un comando de cuidado
El reconocimiento de voz no está disponible en este dispositivo. Escribe un comando o usa las pantallas de cuidados.
Permite el micrófono cuando elijas decir un comando.
El micrófono está desactivado. Escribe un comando o habilita el acceso en los ajustes del dispositivo.
No se ha reconocido la voz. Inténtalo de nuevo o escribe el comando.
No se ha reconocido ese comando. Prueba a abrir una pantalla de cuidados.
Revisa esta acción de cuidado antes de confirmar.
Las respuestas habladas no están disponibles para este idioma en este dispositivo.
Bienvenido a Pawverio
Elige tu idioma
Presenta a tu primera mascota
Crea una rutina de cuidados
Tus cuidados, tu control
Empezar a cuidar
Guardado en este dispositivo
Registra las instrucciones de tu veterinario. Pawverio no recomienda dietas, dosis ni tratamientos.
Foto del perfil de la mascota
Abrir asistente de voz
Añadir un elemento
AAAA-MM-DD
HH:MM
%1$d tarea de cuidado
%1$d tareas de cuidado'''

TRANSLATIONS['de-DE']='''Pawverio
Jedes Tier. Jede Routine. Liebevoll umsorgt.
Heute
Haustiere
Gesundheit
Verlauf
Einstellungen
Haustier hinzufügen
Haustier bearbeiten
Haustierprofil
Name
Tierart
Hund
Katze
Andere
Rasse
Alter
Geburtsdatum
Gewicht
Mikrochipnummer
Allergien oder Unverträglichkeiten
Notizen
Foto
Foto hinzufügen
Speichern
Abbrechen
Löschen
Bearbeiten
Schließen
Weiter
Zurück
Überspringen
Fertig
Bestätigen
Suchen
Optional
Fütterung
Spaziergang
Fellpflege
Medikament
Tierarzttermin
Impfung
Eigene Pflegeaufgabe
Pflegeaufgabe hinzufügen
Pflegeaufgabe bearbeiten
Aufgabenname
Haustier
Fälligkeitsdatum
Uhrzeit
Wiederholen
Einmal
Täglich
Wöchentlich
Monatlich
Wiederholen alle (Tage)
Als erledigt markieren
Rückgängig
Um 15 Minuten verschieben
Pausieren
Fortsetzen
Pausiert
Erledigt
Demnächst
Heutige Pflege
Letzte Aktivitäten
Fürsorge beginnt hier
Füge dein erstes Haustier hinzu und plane seine tägliche Pflege.
Ein entspannter Tag wartet
Füge eine Erinnerung für Fütterung, Spaziergang oder Pflege hinzu.
Erledigte Pflegeaufgaben erscheinen hier.
Bewahre die Unterlagen deines Tieres gemeinsam auf.
Wird geladen…
Etwas ist schiefgelaufen. Bitte erneut versuchen.
Erneut versuchen
Bitte einen Namen eingeben.
Eine gültige positive Zahl eingeben.
Dieses Haustier und alle zugehörigen Daten löschen? Dies kann nicht rückgängig gemacht werden.
Alle lokalen Haustierdaten löschen? Dies kann nicht rückgängig gemacht werden.
Portion
Mahlzeitart
Futternotizen
Dauer (Minuten)
Entfernung
Spaziergang eintragen
Praxis oder Anbieter
Kontaktdaten
Anhang
Dokument hinzufügen
Verschriebene Dosierung
Startdatum
Enddatum
Impfstoffname
Verabreichungsdatum
Nächster Fälligkeitstermin
Gesundheitsunterlage hinzufügen
Gewichtstagebuch
Gewicht eintragen
Maße
Zugewiesen an
Pflege im Haushalt
Haushaltsmitglied
Betreuungsbericht
Haushaltshinweise
Notfallkontakt
Text exportieren
PDF exportieren
Daten exportieren
Diese Datei kann private Gesundheits- und Kontaktdaten enthalten. Fortfahren?
Export gespeichert.
Pawverio Premium
Mehr Haustiere, Gesundheitsfunktionen und Betreuungsberichte mit einmaliger Freischaltung.
Premium freischalten
Käufe wiederherstellen
Der Amazon-Store ist nicht verfügbar. Deine vorhandenen Unterlagen bleiben zugänglich.
Kauf wird geprüft…
Der Kauf wurde abgebrochen.
Der Kauf konnte nicht abgeschlossen werden. Bitte erneut versuchen.
Premium ist aktiv
Sprache
Gerätesprache verwenden
Maßeinheiten
Kilogramm (kg)
Pfund (lb)
Darstellung
Gerätedesign verwenden
Hell
Dunkel
Erinnerungen
Benachrichtigungen erlauben, um Pflegeerinnerungen zu erhalten.
Ruhezeiten
Erinnerungen während der Ruhezeiten werden verschoben. Zeitkritische Pflege direkt prüfen.
Datenschutz
Tierdaten bleiben auf diesem Gerät, sofern du sie nicht exportierst. Sprachdienste können Audio gemäß den Bedingungen deines Geräteanbieters verarbeiten. Pawverio speichert keine Mikrofonaufnahmen.
Alle Haustierdaten löschen
Verschlüsselte Sicherung
Sicherungspasswort
App-Sperre
Gesprochene Rückmeldung
Bewegung reduzieren
Pflege per Sprachbefehl
Zum Sprechen tippen
Zuhören beenden
Ich höre zu…
Erkannten Text prüfen
Befehl ausführen
Pflegebefehl eingeben oder sprechen
Spracherkennung ist auf diesem Gerät nicht verfügbar. Gib einen Befehl ein oder nutze die Pflegeansichten.
Mikrofonzugriff erlauben, wenn du einen Befehl sprechen möchtest.
Der Mikrofonzugriff ist deaktiviert. Gib einen Befehl ein oder aktiviere den Zugriff in den Geräteeinstellungen.
Sprache wurde nicht erkannt. Bitte erneut versuchen oder den Befehl eingeben.
Der Befehl wurde nicht erkannt. Öffne eine Pflegeansicht.
Diese Pflegeaktion vor der Bestätigung prüfen.
Gesprochene Rückmeldungen sind für diese Sprache auf diesem Gerät nicht verfügbar.
Willkommen bei Pawverio
Wähle deine Sprache
Lerne dein erstes Haustier kennen
Gestalte eine liebevolle Routine
Deine Pflege, deine Kontrolle
Mit der Pflege beginnen
Auf diesem Gerät gespeichert
Notiere die Anweisungen deiner Tierarztpraxis. Pawverio empfiehlt keine Ernährung, Dosierung oder Behandlung.
Haustierprofilfoto
Sprachassistent öffnen
Neuen Eintrag hinzufügen
JJJJ-MM-TT
HH:MM
%1$d Pflegeaufgabe
%1$d Pflegeaufgaben'''

TRANSLATIONS['it-IT']='''Pawverio
Ogni animale. Ogni routine. Cure piene d’amore.
Oggi
Animali
Salute
Cronologia
Impostazioni
Aggiungi animale
Modifica animale
Profilo dell’animale
Nome
Specie
Cane
Gatto
Altro
Razza
Età
Data di nascita
Peso
Riferimento microchip
Allergie o sensibilità
Note
Foto
Aggiungi foto
Salva
Annulla
Elimina
Modifica
Chiudi
Avanti
Indietro
Salta
Fatto
Conferma
Cerca
Facoltativo
Alimentazione
Passeggiata
Toelettatura
Farmaco
Visita veterinaria
Vaccinazione
Cura personalizzata
Aggiungi attività di cura
Modifica attività di cura
Nome dell’attività
Animale
Data prevista
Ora
Ripeti
Una volta
Ogni giorno
Ogni settimana
Ogni mese
Ripeti ogni (giorni)
Segna come completata
Annulla azione
Posticipa di 15 minuti
Pausa
Riprendi
In pausa
Completata
In programma
Cure di oggi
Attività recenti
Le piccole attenzioni iniziano qui
Aggiungi il tuo primo animale per organizzare le sue cure quotidiane.
Una giornata serena ti aspetta
Aggiungi un promemoria per pasti, passeggiate o cure.
Le cure completate appariranno qui.
Tieni insieme i documenti del tuo animale.
Caricamento…
Si è verificato un problema. Riprova.
Riprova
Inserisci un nome.
Inserisci un numero positivo valido.
Eliminare questo animale e tutti i suoi dati? L’azione è irreversibile.
Eliminare tutti i dati locali degli animali? L’azione è irreversibile.
Porzione
Tipo di pasto
Note alimentari
Durata (minuti)
Distanza
Registra passeggiata
Clinica o professionista
Recapiti
Allegato
Aggiungi documento
Dosaggio prescritto
Data di inizio
Data di fine
Nome del vaccino
Data di somministrazione
Prossima scadenza
Aggiungi dato sanitario
Diario del peso
Aggiungi pesata
Misure
Assegnato a
Cure in famiglia
Membro della famiglia
Rapporto per pet sitter
Istruzioni domestiche
Contatto di emergenza
Esporta testo
Esporta PDF
Esporta dati
Questo file può includere dati sanitari e recapiti privati. Continuare?
Esportazione salvata.
Pawverio Premium
Più animali, strumenti per la salute e rapporti per pet sitter con un solo acquisto.
Sblocca Premium
Ripristina acquisti
Lo store Amazon non è disponibile. I dati esistenti rimangono accessibili.
Verifica dell’acquisto…
L’acquisto è stato annullato.
Impossibile completare l’acquisto. Riprova.
Premium è attivo
Lingua
Usa lingua del dispositivo
Unità di misura
Chilogrammi (kg)
Libbre (lb)
Aspetto
Usa tema del dispositivo
Chiaro
Scuro
Promemoria
Consenti le notifiche per ricevere promemoria di cura.
Ore di silenzio
I promemoria vengono posticipati durante le ore di silenzio. Controlla direttamente le cure urgenti.
Privacy
I dati degli animali restano su questo dispositivo salvo esportazione. I servizi vocali possono elaborare l’audio secondo i termini del fornitore del dispositivo. Pawverio non salva registrazioni del microfono.
Elimina tutti i dati degli animali
Backup crittografato
Password del backup
Blocco dell’app
Risposte vocali
Riduci animazioni
Prenditi cura con la voce
Tocca per parlare
Interrompi ascolto
In ascolto…
Controlla il testo riconosciuto
Esegui comando
Scrivi o pronuncia un comando di cura
Il riconoscimento vocale non è disponibile su questo dispositivo. Scrivi un comando o usa le schermate di cura.
Consenti l’accesso al microfono quando scegli di pronunciare un comando.
Il microfono è disattivato. Scrivi un comando o abilita l’accesso nelle impostazioni del dispositivo.
Voce non riconosciuta. Riprova o scrivi il comando.
Comando non riconosciuto. Prova ad aprire una schermata di cura.
Controlla questa azione di cura prima di confermare.
Le risposte vocali non sono disponibili per questa lingua su questo dispositivo.
Benvenuto in Pawverio
Scegli la tua lingua
Presenta il tuo primo animale
Crea una routine di cura
Le tue cure, il tuo controllo
Inizia a prendertene cura
Salvato su questo dispositivo
Annota le istruzioni del veterinario. Pawverio non consiglia diete, dosaggi o trattamenti.
Foto profilo dell’animale
Apri assistente vocale
Aggiungi un elemento
AAAA-MM-GG
HH:MM
%1$d attività di cura
%1$d attività di cura'''

TRANSLATIONS['pt-BR']='''Pawverio
Cada pet. Cada rotina. Cuidado com carinho.
Hoje
Pets
Saúde
Histórico
Configurações
Adicionar pet
Editar pet
Perfil do pet
Nome
Espécie
Cachorro
Gato
Outra
Raça
Idade
Data de nascimento
Peso
Referência do microchip
Alergias ou sensibilidades
Notas
Foto
Adicionar foto
Salvar
Cancelar
Excluir
Editar
Fechar
Próximo
Voltar
Pular
Concluído
Confirmar
Buscar
Opcional
Alimentação
Passeio
Higiene
Medicamento
Consulta veterinária
Vacinação
Cuidado personalizado
Adicionar tarefa de cuidado
Editar tarefa de cuidado
Nome da tarefa
Pet
Data prevista
Horário
Repetir
Uma vez
Todos os dias
Toda semana
Todo mês
Repetir a cada (dias)
Marcar como concluída
Desfazer
Adiar por 15 minutos
Pausar
Retomar
Pausada
Concluída
Em breve
Cuidados de hoje
Atividade recente
O carinho começa aqui
Adicione seu primeiro pet para organizar os cuidados diários.
Um dia tranquilo pela frente
Adicione um lembrete de alimentação, passeio ou cuidado.
Os cuidados concluídos aparecerão aqui.
Mantenha os registros do seu pet juntos.
Carregando…
Ocorreu um problema. Tente novamente.
Tentar novamente
Digite um nome.
Digite um número positivo válido.
Excluir este pet e todos os registros associados? Esta ação não pode ser desfeita.
Excluir todos os dados locais dos pets? Esta ação não pode ser desfeita.
Porção
Tipo de refeição
Notas sobre alimentação
Duração (minutos)
Distância
Registrar passeio
Clínica ou profissional
Dados de contato
Anexo
Adicionar documento
Dosagem prescrita
Data de início
Data de término
Nome da vacina
Data de aplicação
Próxima data prevista
Adicionar registro de saúde
Diário de peso
Adicionar pesagem
Medidas
Atribuído a
Cuidados da família
Membro da família
Relatório para cuidador
Instruções da casa
Contato de emergência
Exportar texto
Exportar PDF
Exportar dados
Este arquivo pode incluir dados privados de saúde e contato. Continuar?
Exportação salva.
Pawverio Premium
Mais pets, ferramentas de saúde e relatórios para cuidadores com uma compra única.
Desbloquear Premium
Restaurar compras
A loja Amazon está indisponível. Seus registros existentes continuam acessíveis.
Verificando sua compra…
A compra foi cancelada.
Não foi possível concluir a compra. Tente novamente.
Premium está ativo
Idioma
Usar idioma do dispositivo
Unidades de medida
Quilogramas (kg)
Libras (lb)
Aparência
Usar tema do dispositivo
Claro
Escuro
Lembretes
Permita notificações para receber lembretes de cuidados.
Horário de silêncio
Os lembretes são adiados durante o horário de silêncio. Confira diretamente os cuidados urgentes.
Privacidade
Os registros dos pets ficam neste dispositivo, a menos que você os exporte. Os serviços de voz podem processar áudio conforme os termos do fornecedor do dispositivo. O Pawverio não salva gravações do microfone.
Excluir todos os dados dos pets
Backup criptografado
Senha do backup
Bloqueio do aplicativo
Respostas por voz
Reduzir movimento
Cuide com sua voz
Toque para falar
Parar de ouvir
Ouvindo…
Revisar texto reconhecido
Executar comando
Digite ou fale um comando de cuidado
O reconhecimento de voz não está disponível neste dispositivo. Digite um comando ou use as telas de cuidados.
Permita o acesso ao microfone quando escolher falar um comando.
O acesso ao microfone está desativado. Digite um comando ou habilite o acesso nas configurações do dispositivo.
A fala não foi reconhecida. Tente novamente ou digite o comando.
Não foi possível reconhecer esse comando. Tente abrir uma tela de cuidados.
Revise esta ação de cuidado antes de confirmar.
As respostas por voz não estão disponíveis para este idioma neste dispositivo.
Bem-vindo ao Pawverio
Escolha seu idioma
Apresente seu primeiro pet
Crie uma rotina de cuidados
Seus cuidados, seu controle
Começar a cuidar
Salvo neste dispositivo
Registre as instruções do veterinário. O Pawverio não recomenda dietas, dosagens ou tratamentos.
Foto de perfil do pet
Abrir assistente de voz
Adicionar item
AAAA-MM-DD
HH:MM
%1$d tarefa de cuidado
%1$d tarefas de cuidado'''

TRANSLATIONS['pt-PT']='''Pawverio
Cada animal. Cada rotina. Cuidados com carinho.
Hoje
Animais
Saúde
Histórico
Definições
Adicionar animal
Editar animal
Perfil do animal
Nome
Espécie
Cão
Gato
Outra
Raça
Idade
Data de nascimento
Peso
Referência do microchip
Alergias ou sensibilidades
Notas
Fotografia
Adicionar fotografia
Guardar
Cancelar
Eliminar
Editar
Fechar
Seguinte
Voltar
Ignorar
Concluído
Confirmar
Pesquisar
Opcional
Alimentação
Passeio
Higiene
Medicamento
Consulta veterinária
Vacinação
Cuidado personalizado
Adicionar tarefa de cuidado
Editar tarefa de cuidado
Nome da tarefa
Animal
Data prevista
Hora
Repetir
Uma vez
Todos os dias
Todas as semanas
Todos os meses
Repetir a cada (dias)
Marcar como concluída
Anular
Adiar 15 minutos
Pausar
Retomar
Em pausa
Concluída
Em breve
Cuidados de hoje
Atividade recente
O carinho começa aqui
Adicione o seu primeiro animal para organizar os cuidados diários.
Um dia tranquilo pela frente
Adicione um lembrete de alimentação, passeio ou cuidado.
Os cuidados concluídos serão apresentados aqui.
Reúna os registos do seu animal.
A carregar…
Ocorreu um problema. Tente novamente.
Tentar novamente
Introduza um nome.
Introduza um número positivo válido.
Eliminar este animal e todos os registos associados? Esta ação é irreversível.
Eliminar todos os dados locais dos animais? Esta ação é irreversível.
Porção
Tipo de refeição
Notas sobre alimentação
Duração (minutos)
Distância
Registar passeio
Clínica ou profissional
Dados de contacto
Anexo
Adicionar documento
Dosagem prescrita
Data de início
Data de fim
Nome da vacina
Data de administração
Próxima data prevista
Adicionar registo de saúde
Diário do peso
Adicionar pesagem
Medidas
Atribuído a
Cuidados do agregado familiar
Membro do agregado familiar
Relatório para cuidador
Instruções da casa
Contacto de emergência
Exportar texto
Exportar PDF
Exportar dados
Este ficheiro pode incluir dados privados de saúde e de contacto. Continuar?
Exportação guardada.
Pawverio Premium
Mais animais, ferramentas de saúde e relatórios para cuidadores com uma compra única.
Desbloquear Premium
Restaurar compras
A loja Amazon está indisponível. Os seus registos existentes continuam acessíveis.
A verificar a compra…
A compra foi cancelada.
Não foi possível concluir a compra. Tente novamente.
Premium está ativo
Idioma
Usar idioma do dispositivo
Unidades de medida
Quilogramas (kg)
Libras (lb)
Aspeto
Usar tema do dispositivo
Claro
Escuro
Lembretes
Permita notificações para receber lembretes de cuidados.
Horas de silêncio
Os lembretes são adiados durante as horas de silêncio. Verifique diretamente os cuidados urgentes.
Privacidade
Os registos dos animais ficam neste dispositivo, salvo se os exportar. Os serviços de voz podem processar áudio nos termos do fornecedor do dispositivo. O Pawverio não guarda gravações do microfone.
Eliminar todos os dados dos animais
Cópia de segurança encriptada
Palavra-passe da cópia
Bloqueio da aplicação
Respostas por voz
Reduzir movimento
Cuide com a sua voz
Toque para falar
Parar de ouvir
A ouvir…
Rever texto reconhecido
Executar comando
Escreva ou diga um comando de cuidado
O reconhecimento de voz não está disponível neste dispositivo. Escreva um comando ou use os ecrãs de cuidados.
Permita o acesso ao microfone quando optar por dizer um comando.
O acesso ao microfone está desativado. Escreva um comando ou ative o acesso nas definições do dispositivo.
A fala não foi reconhecida. Tente novamente ou escreva o comando.
Não foi possível reconhecer esse comando. Experimente abrir um ecrã de cuidados.
Reveja esta ação de cuidado antes de confirmar.
As respostas por voz não estão disponíveis para este idioma neste dispositivo.
Bem-vindo ao Pawverio
Escolha o seu idioma
Apresente o seu primeiro animal
Crie uma rotina de cuidados
Os seus cuidados, o seu controlo
Começar a cuidar
Guardado neste dispositivo
Registe as instruções do veterinário. O Pawverio não recomenda dietas, dosagens ou tratamentos.
Fotografia do perfil do animal
Abrir assistente de voz
Adicionar item
AAAA-MM-DD
HH:MM
%1$d tarefa de cuidado
%1$d tarefas de cuidado'''

TRANSLATIONS['nl-NL']='''Pawverio
Elk huisdier. Elke routine. Met liefde verzorgd.
Vandaag
Huisdieren
Gezondheid
Geschiedenis
Instellingen
Huisdier toevoegen
Huisdier bewerken
Huisdierprofiel
Naam
Diersoort
Hond
Kat
Anders
Ras
Leeftijd
Geboortedatum
Gewicht
Chipnummer
Allergieën of gevoeligheden
Notities
Foto
Foto toevoegen
Opslaan
Annuleren
Verwijderen
Bewerken
Sluiten
Volgende
Terug
Overslaan
Klaar
Bevestigen
Zoeken
Optioneel
Voeren
Wandelen
Verzorging
Medicatie
Dierenartsafspraak
Vaccinatie
Eigen verzorging
Verzorgingstaak toevoegen
Verzorgingstaak bewerken
Taaknaam
Huisdier
Geplande datum
Tijd
Herhalen
Eenmalig
Elke dag
Elke week
Elke maand
Herhalen om de (dagen)
Markeren als voltooid
Ongedaan maken
15 minuten uitstellen
Pauzeren
Hervatten
Gepauzeerd
Voltooid
Binnenkort
Verzorging vandaag
Recente activiteit
Goede zorg begint hier
Voeg je eerste huisdier toe om de dagelijkse verzorging te plannen.
Een rustige dag in het vooruitzicht
Voeg een herinnering voor eten, wandelen of verzorging toe.
Voltooide verzorging verschijnt hier.
Bewaar de gegevens van je huisdier bij elkaar.
Laden…
Er ging iets mis. Probeer het opnieuw.
Opnieuw proberen
Voer een naam in.
Voer een geldig positief getal in.
Dit huisdier en alle bijbehorende gegevens verwijderen? Dit kan niet ongedaan worden gemaakt.
Alle lokale huisdiergegevens verwijderen? Dit kan niet ongedaan worden gemaakt.
Portie
Soort maaltijd
Voedingsnotities
Duur (minuten)
Afstand
Wandeling vastleggen
Kliniek of zorgverlener
Contactgegevens
Bijlage
Document toevoegen
Voorgeschreven dosering
Begindatum
Einddatum
Vaccinnaam
Toedieningsdatum
Volgende geplande datum
Gezondheidsgegeven toevoegen
Gewichtsdagboek
Gewicht toevoegen
Afmetingen
Toegewezen aan
Verzorging in het huishouden
Huisgenoot
Oppasrapport
Instructies voor thuis
Noodcontact
Tekst exporteren
PDF exporteren
Gegevens exporteren
Dit bestand kan privégezondheidsgegevens en contactgegevens bevatten. Doorgaan?
Export opgeslagen.
Pawverio Premium
Meer huisdieren, gezondheidsfuncties en oppasrapporten met een eenmalige aankoop.
Premium ontgrendelen
Aankopen herstellen
De Amazon-winkel is niet beschikbaar. Je bestaande gegevens blijven toegankelijk.
Aankoop controleren…
De aankoop is geannuleerd.
De aankoop kon niet worden voltooid. Probeer het opnieuw.
Premium is actief
Taal
Apparaattaal gebruiken
Maateenheden
Kilogram (kg)
Pond (lb)
Weergave
Apparaatthema gebruiken
Licht
Donker
Herinneringen
Sta meldingen toe om verzorgingsherinneringen te ontvangen.
Stille uren
Herinneringen worden tijdens stille uren uitgesteld. Controleer tijdgevoelige zorg rechtstreeks.
Privacy
Huisdiergegevens blijven op dit apparaat tenzij je ze exporteert. Spraakdiensten kunnen audio verwerken volgens de voorwaarden van je apparaatprovider. Pawverio bewaart geen microfoonopnamen.
Alle huisdiergegevens verwijderen
Versleutelde back-up
Back-upwachtwoord
Appvergrendeling
Gesproken feedback
Beweging verminderen
Verzorgen met je stem
Tik om te spreken
Stoppen met luisteren
Luisteren…
Herkende tekst controleren
Opdracht uitvoeren
Typ of spreek een verzorgingsopdracht
Spraakherkenning is niet beschikbaar op dit apparaat. Typ een opdracht of gebruik de verzorgingsschermen.
Sta microfoontoegang toe wanneer je een opdracht wilt inspreken.
Microfoontoegang is uitgeschakeld. Typ een opdracht of schakel toegang in via de apparaatinstellingen.
Spraak is niet herkend. Probeer opnieuw of typ je opdracht.
Deze opdracht is niet herkend. Probeer een verzorgingsscherm te openen.
Controleer deze verzorgingsactie voordat je bevestigt.
Gesproken feedback is voor deze taal niet beschikbaar op dit apparaat.
Welkom bij Pawverio
Kies je taal
Maak kennis met je eerste huisdier
Bouw een zorgzame routine op
Jouw zorg, jouw controle
Begin met verzorgen
Op dit apparaat opgeslagen
Leg instructies van je dierenarts vast. Pawverio adviseert geen diëten, doseringen of behandelingen.
Profielfoto van het huisdier
Spraakassistent openen
Nieuw item toevoegen
JJJJ-MM-DD
UU:MM
%1$d verzorgingstaak
%1$d verzorgingstaken'''

TRANSLATIONS['pl-PL']='''Pawverio
Każde zwierzę. Każdy rytuał. Troskliwa opieka.
Dzisiaj
Zwierzęta
Zdrowie
Historia
Ustawienia
Dodaj zwierzę
Edytuj zwierzę
Profil zwierzęcia
Imię
Gatunek
Pies
Kot
Inny
Rasa
Wiek
Data urodzenia
Waga
Numer mikroczipu
Alergie lub nadwrażliwości
Notatki
Zdjęcie
Dodaj zdjęcie
Zapisz
Anuluj
Usuń
Edytuj
Zamknij
Dalej
Wstecz
Pomiń
Gotowe
Potwierdź
Szukaj
Opcjonalne
Karmienie
Spacer
Pielęgnacja
Lek
Wizyta weterynaryjna
Szczepienie
Własna czynność
Dodaj zadanie opieki
Edytuj zadanie opieki
Nazwa zadania
Zwierzę
Termin
Godzina
Powtarzaj
Jednorazowo
Codziennie
Co tydzień
Co miesiąc
Powtarzaj co (dni)
Oznacz jako ukończone
Cofnij
Odłóż o 15 minut
Wstrzymaj
Wznów
Wstrzymane
Ukończone
Nadchodzące
Dzisiejsza opieka
Ostatnia aktywność
Troska zaczyna się tutaj
Dodaj pierwsze zwierzę, aby zaplanować codzienną opiekę.
Spokojny dzień przed Tobą
Dodaj przypomnienie o karmieniu, spacerze lub pielęgnacji.
Ukończone czynności pojawią się tutaj.
Przechowuj dokumentację zwierzęcia w jednym miejscu.
Wczytywanie…
Coś poszło nie tak. Spróbuj ponownie.
Spróbuj ponownie
Wpisz imię.
Wpisz prawidłową liczbę dodatnią.
Usunąć to zwierzę i całą jego dokumentację? Tej czynności nie można cofnąć.
Usunąć wszystkie lokalne dane zwierząt? Tej czynności nie można cofnąć.
Porcja
Rodzaj posiłku
Notatki o karmieniu
Czas trwania (minuty)
Odległość
Zapisz spacer
Klinika lub specjalista
Dane kontaktowe
Załącznik
Dodaj dokument
Przepisana dawka
Data rozpoczęcia
Data zakończenia
Nazwa szczepionki
Data podania
Następny termin
Dodaj wpis zdrowotny
Dziennik wagi
Dodaj pomiar wagi
Pomiary
Przypisano do
Opieka domowników
Domownik
Raport dla opiekuna
Instrukcje domowe
Kontakt alarmowy
Eksportuj tekst
Eksportuj PDF
Eksportuj dane
Ten plik może zawierać prywatne dane zdrowotne i kontaktowe. Kontynuować?
Eksport zapisany.
Pawverio Premium
Więcej zwierząt, narzędzia zdrowotne i raporty dla opiekunów za jednorazową opłatą.
Odblokuj Premium
Przywróć zakupy
Sklep Amazon jest niedostępny. Istniejące wpisy pozostają dostępne.
Sprawdzanie zakupu…
Zakup został anulowany.
Nie udało się ukończyć zakupu. Spróbuj ponownie.
Premium jest aktywne
Język
Użyj języka urządzenia
Jednostki miar
Kilogramy (kg)
Funty (lb)
Wygląd
Użyj motywu urządzenia
Jasny
Ciemny
Przypomnienia
Zezwól na powiadomienia, aby otrzymywać przypomnienia o opiece.
Godziny ciszy
Przypomnienia są opóźniane w godzinach ciszy. Pilne czynności sprawdzaj bezpośrednio.
Prywatność
Dokumentacja zwierząt pozostaje na tym urządzeniu, chyba że ją wyeksportujesz. Usługi głosowe mogą przetwarzać dźwięk zgodnie z warunkami dostawcy urządzenia. Pawverio nie zapisuje nagrań z mikrofonu.
Usuń wszystkie dane zwierząt
Szyfrowana kopia zapasowa
Hasło kopii zapasowej
Blokada aplikacji
Odpowiedzi głosowe
Ogranicz ruch
Opieka za pomocą głosu
Dotknij, aby mówić
Zatrzymaj nasłuchiwanie
Słucham…
Sprawdź rozpoznany tekst
Wykonaj polecenie
Wpisz lub wypowiedz polecenie opieki
Rozpoznawanie mowy jest niedostępne na tym urządzeniu. Wpisz polecenie lub użyj ekranów opieki.
Zezwól na dostęp do mikrofonu, gdy chcesz wypowiedzieć polecenie.
Dostęp do mikrofonu jest wyłączony. Wpisz polecenie lub włącz dostęp w ustawieniach urządzenia.
Nie rozpoznano mowy. Spróbuj ponownie lub wpisz polecenie.
Nie rozpoznano tego polecenia. Spróbuj otworzyć ekran opieki.
Sprawdź tę czynność opieki przed potwierdzeniem.
Odpowiedzi głosowe w tym języku są niedostępne na tym urządzeniu.
Witamy w Pawverio
Wybierz język
Przedstaw swoje pierwsze zwierzę
Stwórz troskliwą rutynę
Twoja opieka, Twoja kontrola
Zacznij opiekę
Zapisane na tym urządzeniu
Zapisuj zalecenia weterynarza. Pawverio nie zaleca diet, dawek ani leczenia.
Zdjęcie profilowe zwierzęcia
Otwórz asystenta głosowego
Dodaj nowy element
RRRR-MM-DD
GG:MM
%1$d zadanie opieki
%1$d zadań opieki'''

TRANSLATIONS['sv-SE']='''Pawverio
Varje husdjur. Varje rutin. Omsorg med omtanke.
Idag
Husdjur
Hälsa
Historik
Inställningar
Lägg till husdjur
Redigera husdjur
Husdjursprofil
Namn
Art
Hund
Katt
Annan
Ras
Ålder
Födelsedatum
Vikt
Mikrochipnummer
Allergier eller känsligheter
Anteckningar
Foto
Lägg till foto
Spara
Avbryt
Radera
Redigera
Stäng
Nästa
Tillbaka
Hoppa över
Klart
Bekräfta
Sök
Valfritt
Matning
Promenad
Pälsvård
Läkemedel
Veterinärbesök
Vaccination
Egen skötsel
Lägg till skötseluppgift
Redigera skötseluppgift
Uppgiftens namn
Husdjur
Planerat datum
Tid
Upprepa
En gång
Varje dag
Varje vecka
Varje månad
Upprepa var (dagar)
Markera som klar
Ångra
Skjut upp 15 minuter
Pausa
Återuppta
Pausad
Klar
Kommande
Dagens omsorg
Senaste aktivitet
Omtanke börjar här
Lägg till ditt första husdjur för att planera den dagliga omsorgen.
En lugn dag framför dig
Lägg till en påminnelse om mat, promenad eller skötsel.
Utförd skötsel visas här.
Samla husdjurets uppgifter på ett ställe.
Läser in…
Något gick fel. Försök igen.
Försök igen
Ange ett namn.
Ange ett giltigt positivt tal.
Radera detta husdjur och alla tillhörande uppgifter? Detta kan inte ångras.
Radera alla lokala husdjursdata? Detta kan inte ångras.
Portion
Typ av måltid
Matanteckningar
Längd (minuter)
Sträcka
Logga en promenad
Klinik eller vårdgivare
Kontaktuppgifter
Bilaga
Lägg till dokument
Ordinerad dos
Startdatum
Slutdatum
Vaccinets namn
Givet datum
Nästa planerade datum
Lägg till hälsouppgift
Viktdagbok
Lägg till vikt
Mått
Tilldelad till
Hushållets omsorg
Hushållsmedlem
Rapport till djurvakt
Instruktioner för hemmet
Nödkontakt
Exportera text
Exportera PDF
Exportera data
Filen kan innehålla privata hälso- och kontaktuppgifter. Fortsätta?
Exporten sparad.
Pawverio Premium
Fler husdjur, hälsoverktyg och rapporter till djurvakter med ett engångsköp.
Lås upp Premium
Återställ köp
Amazon-butiken är inte tillgänglig. Dina befintliga uppgifter är fortfarande åtkomliga.
Kontrollerar köpet…
Köpet avbröts.
Köpet kunde inte slutföras. Försök igen.
Premium är aktivt
Språk
Använd enhetens språk
Måttenheter
Kilogram (kg)
Pund (lb)
Utseende
Använd enhetens tema
Ljust
Mörkt
Påminnelser
Tillåt aviseringar för att få skötselpåminnelser.
Tysta timmar
Påminnelser fördröjs under tysta timmar. Kontrollera tidskritisk omsorg direkt.
Integritet
Husdjursuppgifter stannar på enheten om du inte exporterar dem. Taltjänster kan behandla ljud enligt enhetsleverantörens villkor. Pawverio sparar inga mikrofoninspelningar.
Radera alla husdjursdata
Krypterad säkerhetskopia
Lösenord för säkerhetskopia
Applås
Talad återkoppling
Minska rörelse
Omsorg med din röst
Tryck för att tala
Sluta lyssna
Lyssnar…
Granska igenkänd text
Utför kommando
Skriv eller säg ett skötselkommando
Taligenkänning saknas på den här enheten. Skriv ett kommando eller använd skötselskärmarna.
Tillåt mikrofonåtkomst när du väljer att säga ett kommando.
Mikrofonåtkomst är avstängd. Skriv ett kommando eller aktivera åtkomst i enhetens inställningar.
Talet kunde inte kännas igen. Försök igen eller skriv kommandot.
Kommandot kunde inte tolkas. Försök öppna en skötselskärm.
Granska denna skötselåtgärd innan du bekräftar.
Talad återkoppling saknas för det här språket på den här enheten.
Välkommen till Pawverio
Välj ditt språk
Presentera ditt första husdjur
Skapa en omtänksam rutin
Din omsorg, din kontroll
Börja vårda
Sparat på den här enheten
Anteckna veterinärens instruktioner. Pawverio rekommenderar inga dieter, doser eller behandlingar.
Husdjurets profilfoto
Öppna röstassistent
Lägg till nytt objekt
ÅÅÅÅ-MM-DD
TT:MM
%1$d skötseluppgift
%1$d skötseluppgifter'''

TRANSLATIONS['ja-JP']='''Pawverio
すべてのペットに、毎日のやさしいお世話を。
今日
ペット
健康
履歴
設定
ペットを追加
ペットを編集
ペットのプロフィール
名前
種類
犬
猫
その他
品種
年齢
生年月日
体重
マイクロチップ番号
アレルギー・過敏症
メモ
写真
写真を追加
保存
キャンセル
削除
編集
閉じる
次へ
戻る
スキップ
完了
確認
検索
任意
食事
散歩
お手入れ
投薬
動物病院の予約
予防接種
その他のお世話
お世話を追加
お世話を編集
お世話の名前
ペット
予定日
時刻
繰り返し
1回のみ
毎日
毎週
毎月
繰り返し間隔（日）
完了にする
元に戻す
15分後に通知
一時停止
再開
一時停止中
完了済み
今後の予定
今日のお世話
最近の記録
ここから始まる小さな思いやり
最初のペットを追加して、毎日のお世話を計画しましょう。
穏やかな一日を
食事、散歩、お世話のリマインダーを追加しましょう。
完了したお世話がここに表示されます。
ペットの記録をまとめて管理しましょう。
読み込み中…
問題が発生しました。もう一度お試しください。
再試行
名前を入力してください。
有効な正の数を入力してください。
このペットと関連するすべての記録を削除しますか？この操作は取り消せません。
端末内のすべてのペットデータを削除しますか？この操作は取り消せません。
分量
食事の種類
食事のメモ
時間（分）
距離
散歩を記録
病院・担当者
連絡先
添付ファイル
書類を追加
処方された用量
開始日
終了日
ワクチン名
接種日
次回の予定日
健康記録を追加
体重日記
体重を記録
測定値
担当者
家族のお世話
家族の名前
ペットシッターレポート
家庭での注意事項
緊急連絡先
テキストを書き出す
PDFを書き出す
データを書き出す
このファイルには個人の健康情報や連絡先が含まれる場合があります。続行しますか？
書き出しを保存しました。
Pawverio Premium
1回の購入で、複数のペット、健康ツール、シッターレポートを利用できます。
Premiumを購入
購入を復元
Amazonストアを利用できません。既存の記録には引き続きアクセスできます。
購入を確認中…
購入がキャンセルされました。
購入を完了できませんでした。もう一度お試しください。
Premiumは有効です
言語
端末の言語を使う
測定単位
キログラム（kg）
ポンド（lb）
外観
端末のテーマを使う
ライト
ダーク
リマインダー
お世話のリマインダーを受け取るには、通知を許可してください。
通知を控える時間
この時間帯の通知は延期されます。時間厳守のお世話は直接確認してください。
プライバシー
書き出しを行わない限り、ペットの記録はこの端末に保存されます。音声サービスは端末提供元の規約に従って音声を処理する場合があります。Pawverioはマイクの録音を保存しません。
すべてのペットデータを削除
暗号化バックアップ
バックアップのパスワード
アプリロック
音声フィードバック
動きを減らす
声でお世話
タップして話す
音声入力を停止
聞き取り中…
認識したテキストを確認
コマンドを実行
お世話の指示を入力または発話
この端末では音声認識を利用できません。コマンドを入力するか、お世話の画面をご利用ください。
コマンドを話すときにマイクへのアクセスを許可してください。
マイクへのアクセスが無効です。コマンドを入力するか、端末の設定でアクセスを有効にしてください。
音声を認識できませんでした。もう一度試すか、コマンドを入力してください。
コマンドを認識できませんでした。お世話の画面を開いてみてください。
確認する前に、このお世話の操作を確認してください。
この端末では、この言語の音声フィードバックを利用できません。
Pawverioへようこそ
言語を選択
最初のペットを紹介
やさしいお世話の習慣を
お世話もデータも、自分で管理
お世話を始める
この端末に保存
獣医師の指示を記録してください。Pawverioは食事、用量、治療を推奨しません。
ペットのプロフィール写真
音声アシスタントを開く
項目を追加
YYYY-MM-DD
HH:MM
お世話%1$d件
お世話%1$d件'''

TRANSLATIONS['hi-IN']='''Pawverio
हर पालतू। हर दिनचर्या। प्यार भरी देखभाल।
आज
पालतू
स्वास्थ्य
इतिहास
सेटिंग
पालतू जोड़ें
पालतू की जानकारी बदलें
पालतू की प्रोफ़ाइल
नाम
प्रजाति
कुत्ता
बिल्ली
अन्य
नस्ल
उम्र
जन्मतिथि
वज़न
माइक्रोचिप संदर्भ
एलर्जी या संवेदनशीलताएँ
नोट
फ़ोटो
फ़ोटो जोड़ें
सहेजें
रद्द करें
मिटाएँ
बदलें
बंद करें
आगे
वापस
छोड़ें
हो गया
पुष्टि करें
खोजें
वैकल्पिक
भोजन
सैर
साफ़-सफ़ाई
दवा
पशु चिकित्सक की अपॉइंटमेंट
टीकाकरण
अपनी देखभाल गतिविधि
देखभाल का काम जोड़ें
देखभाल का काम बदलें
काम का नाम
पालतू
नियत तारीख
समय
दोहराएँ
एक बार
हर दिन
हर हफ़्ते
हर महीने
हर इतने दिनों में दोहराएँ
पूरा हुआ चिह्नित करें
पहले जैसा करें
15 मिनट बाद याद दिलाएँ
रोकें
फिर शुरू करें
रुका हुआ
पूरा हुआ
आने वाला
आज की देखभाल
हाल की गतिविधि
छोटी-सी देखभाल की शुरुआत यहाँ से
रोज़ की देखभाल की योजना बनाने के लिए अपना पहला पालतू जोड़ें।
आगे है एक सुकून भरा दिन
भोजन, सैर या देखभाल का रिमाइंडर जोड़ें।
पूरी की गई देखभाल यहाँ दिखाई देगी।
अपने पालतू के रिकॉर्ड एक जगह रखें।
लोड हो रहा है…
कुछ गड़बड़ हुई। कृपया फिर कोशिश करें।
फिर कोशिश करें
कृपया नाम लिखें।
एक मान्य धनात्मक संख्या लिखें।
इस पालतू और उससे जुड़े सभी रिकॉर्ड मिटाएँ? इसे वापस नहीं किया जा सकता।
पालतुओं का सारा स्थानीय डेटा मिटाएँ? इसे वापस नहीं किया जा सकता।
मात्रा
भोजन का प्रकार
भोजन संबंधी नोट
अवधि (मिनट)
दूरी
सैर दर्ज करें
क्लिनिक या प्रदाता
संपर्क विवरण
अटैचमेंट
दस्तावेज़ जोड़ें
निर्धारित खुराक
शुरू होने की तारीख
समाप्ति की तारीख
टीके का नाम
टीका लगने की तारीख
अगली नियत तारीख
स्वास्थ्य रिकॉर्ड जोड़ें
वज़न डायरी
वज़न दर्ज करें
माप
ज़िम्मेदार व्यक्ति
घर में देखभाल
घर का सदस्य
पालतू देखभालकर्ता की रिपोर्ट
घर के निर्देश
आपातकालीन संपर्क
टेक्स्ट निर्यात करें
PDF निर्यात करें
डेटा निर्यात करें
इस फ़ाइल में निजी स्वास्थ्य और संपर्क विवरण हो सकते हैं। जारी रखें?
निर्यात सहेजा गया।
Pawverio Premium
एक बार खरीदकर अधिक पालतू, स्वास्थ्य उपकरण और देखभालकर्ता रिपोर्ट पाएँ।
Premium अनलॉक करें
खरीदारी बहाल करें
Amazon स्टोर उपलब्ध नहीं है। आपके मौजूदा रिकॉर्ड उपलब्ध रहेंगे।
आपकी खरीदारी जाँची जा रही है…
खरीदारी रद्द कर दी गई।
खरीदारी पूरी नहीं हो सकी। कृपया फिर कोशिश करें।
Premium सक्रिय है
भाषा
डिवाइस की भाषा इस्तेमाल करें
माप की इकाइयाँ
किलोग्राम (kg)
पाउंड (lb)
दिखावट
डिवाइस की थीम इस्तेमाल करें
हल्की
गहरी
रिमाइंडर
पालतू की देखभाल के रिमाइंडर पाने के लिए सूचनाओं की अनुमति दें।
शांत समय
शांत समय के दौरान रिमाइंडर देर से आते हैं। समय-संवेदी देखभाल सीधे जाँचें।
गोपनीयता
पालतू के रिकॉर्ड इस डिवाइस पर रहते हैं, जब तक आप उन्हें निर्यात न करें। वॉइस सेवाएँ आपके डिवाइस प्रदाता की शर्तों के अनुसार ऑडियो प्रोसेस कर सकती हैं। Pawverio माइक्रोफ़ोन की रिकॉर्डिंग नहीं सहेजता।
पालतुओं का सारा डेटा मिटाएँ
एन्क्रिप्ट किया गया बैकअप
बैकअप पासवर्ड
ऐप लॉक
बोलकर जवाब
गतिविधि कम करें
अपनी आवाज़ से देखभाल करें
बोलने के लिए टैप करें
सुनना बंद करें
सुन रहे हैं…
पहचाना गया टेक्स्ट जाँचें
निर्देश चलाएँ
देखभाल का निर्देश लिखें या बोलें
इस डिवाइस पर आवाज़ पहचान उपलब्ध नहीं है। निर्देश लिखें या देखभाल की स्क्रीन इस्तेमाल करें।
जब आप निर्देश बोलना चाहें, तब माइक्रोफ़ोन की अनुमति दें।
माइक्रोफ़ोन की अनुमति बंद है। निर्देश लिखें या डिवाइस की सेटिंग में अनुमति दें।
आवाज़ पहचानी नहीं गई। फिर कोशिश करें या निर्देश लिखें।
यह निर्देश समझ नहीं आया। देखभाल की स्क्रीन खोलकर देखें।
पुष्टि से पहले इस देखभाल कार्रवाई की जाँच करें।
इस डिवाइस पर इस भाषा में बोलकर जवाब उपलब्ध नहीं हैं।
Pawverio में आपका स्वागत है
अपनी भाषा चुनें
अपने पहले पालतू का परिचय दें
देखभाल की दिनचर्या बनाएँ
आपकी देखभाल, आपका नियंत्रण
देखभाल शुरू करें
इस डिवाइस पर सहेजा गया
अपने पशु चिकित्सक के निर्देश दर्ज करें। Pawverio आहार, खुराक या उपचार की सलाह नहीं देता।
पालतू की प्रोफ़ाइल फ़ोटो
वॉइस सहायक खोलें
नई जानकारी जोड़ें
YYYY-MM-DD
HH:MM
देखभाल का %1$d काम
देखभाल के %1$d काम'''

TRANSLATIONS['ar']='''Pawverio
كل حيوان أليف. كل روتين. رعاية بكل حب.
اليوم
الحيوانات الأليفة
الصحة
السجل
الإعدادات
إضافة حيوان أليف
تعديل الحيوان الأليف
ملف الحيوان الأليف
الاسم
النوع
كلب
قط
آخر
السلالة
العمر
تاريخ الميلاد
الوزن
مرجع الشريحة الإلكترونية
الحساسية أو التحسس
ملاحظات
صورة
إضافة صورة
حفظ
إلغاء
حذف
تعديل
إغلاق
التالي
رجوع
تخطي
تم
تأكيد
بحث
اختياري
الإطعام
المشي
العناية والنظافة
الدواء
موعد بيطري
التطعيم
رعاية مخصصة
إضافة مهمة رعاية
تعديل مهمة الرعاية
اسم المهمة
الحيوان الأليف
تاريخ الاستحقاق
الوقت
تكرار
مرة واحدة
كل يوم
كل أسبوع
كل شهر
التكرار كل (أيام)
وضع علامة مكتمل
تراجع
تأجيل لمدة 15 دقيقة
إيقاف مؤقت
استئناف
متوقف مؤقتًا
مكتمل
قادم
رعاية اليوم
النشاط الأخير
تبدأ الرعاية من هنا
أضف حيوانك الأليف الأول لتنظيم رعايته اليومية.
يوم هادئ في انتظارك
أضف تذكيرًا بالإطعام أو المشي أو الرعاية.
ستظهر الرعاية المكتملة هنا.
احتفظ بسجلات حيوانك الأليف معًا.
جارٍ التحميل…
حدث خطأ. يرجى المحاولة مجددًا.
المحاولة مجددًا
يرجى إدخال اسم.
أدخل رقمًا موجبًا صالحًا.
هل تريد حذف هذا الحيوان الأليف وجميع سجلاته؟ لا يمكن التراجع عن ذلك.
هل تريد حذف جميع بيانات الحيوانات الأليفة المحلية؟ لا يمكن التراجع عن ذلك.
الكمية
نوع الوجبة
ملاحظات الطعام
المدة (بالدقائق)
المسافة
تسجيل مشي
العيادة أو مقدم الرعاية
بيانات الاتصال
مرفق
إضافة مستند
الجرعة الموصوفة
تاريخ البدء
تاريخ الانتهاء
اسم اللقاح
تاريخ إعطاء اللقاح
تاريخ الاستحقاق التالي
إضافة سجل صحي
يوميات الوزن
إضافة قياس وزن
القياسات
مسند إلى
رعاية أفراد المنزل
فرد من المنزل
تقرير جليس الحيوان الأليف
تعليمات المنزل
جهة اتصال للطوارئ
تصدير نص
تصدير PDF
تصدير البيانات
قد يتضمن هذا الملف بيانات صحية وبيانات اتصال خاصة. هل تريد المتابعة؟
تم حفظ التصدير.
Pawverio Premium
حيوانات أليفة أكثر وأدوات صحية وتقارير للجليس عبر شراء لمرة واحدة.
فتح Premium
استعادة المشتريات
متجر Amazon غير متاح. تظل سجلاتك الحالية متاحة.
جارٍ التحقق من عملية الشراء…
تم إلغاء عملية الشراء.
تعذر إكمال عملية الشراء. يرجى المحاولة مجددًا.
Premium مفعّل
اللغة
استخدام لغة الجهاز
وحدات القياس
كيلوغرام (kg)
رطل (lb)
المظهر
استخدام سمة الجهاز
فاتح
داكن
التذكيرات
اسمح بالإشعارات لتلقي تذكيرات رعاية الحيوان الأليف.
ساعات الهدوء
تتأخر التذكيرات خلال ساعات الهدوء. تحقق مباشرة من الرعاية المرتبطة بوقت محدد.
الخصوصية
تبقى سجلات الحيوانات الأليفة على هذا الجهاز ما لم تصدّرها. قد تعالج خدمات الصوت التسجيلات وفق شروط مقدم خدمة جهازك. لا يحفظ Pawverio تسجيلات الميكروفون.
حذف جميع بيانات الحيوانات الأليفة
نسخة احتياطية مشفّرة
كلمة مرور النسخة الاحتياطية
قفل التطبيق
الردود المنطوقة
تقليل الحركة
الرعاية بصوتك
انقر للتحدث
إيقاف الاستماع
جارٍ الاستماع…
مراجعة النص الذي تم التعرف عليه
تنفيذ الأمر
اكتب أمر رعاية أو انطقه
التعرف على الكلام غير متاح على هذا الجهاز. يمكنك كتابة أمر أو استخدام شاشات الرعاية.
اسمح بالوصول إلى الميكروفون عندما تختار نطق أمر.
الوصول إلى الميكروفون متوقف. اكتب أمرًا أو فعّل الوصول من إعدادات الجهاز.
لم يتم التعرف على الكلام. حاول مجددًا أو اكتب الأمر.
تعذر فهم هذا الأمر. جرّب فتح شاشة رعاية.
راجع إجراء الرعاية هذا قبل التأكيد.
الردود المنطوقة غير متاحة لهذه اللغة على هذا الجهاز.
مرحبًا بك في Pawverio
اختر لغتك
تعرّف على حيوانك الأليف الأول
أنشئ روتين رعاية
رعايتك تحت سيطرتك
ابدأ الرعاية
محفوظ على هذا الجهاز
سجّل تعليمات الطبيب البيطري. لا يوصي Pawverio بأنظمة غذائية أو جرعات أو علاجات.
صورة ملف الحيوان الأليف
فتح المساعد الصوتي
إضافة عنصر جديد
YYYY-MM-DD
HH:MM
%1$d مهمة رعاية
%1$d مهمة رعاية'''

TRANSLATIONS['tr-TR']='''Pawverio
Her dost. Her rutin. Sevgi dolu bakım.
Bugün
Evcil hayvanlar
Sağlık
Geçmiş
Ayarlar
Evcil hayvan ekle
Evcil hayvanı düzenle
Evcil hayvan profili
Ad
Tür
Köpek
Kedi
Diğer
Irk
Yaş
Doğum tarihi
Ağırlık
Mikroçip numarası
Alerjiler veya hassasiyetler
Notlar
Fotoğraf
Fotoğraf ekle
Kaydet
İptal
Sil
Düzenle
Kapat
İleri
Geri
Atla
Tamam
Onayla
Ara
İsteğe bağlı
Besleme
Yürüyüş
Bakım ve temizlik
İlaç
Veteriner randevusu
Aşı
Özel bakım
Bakım görevi ekle
Bakım görevini düzenle
Görev adı
Evcil hayvan
Planlanan tarih
Saat
Tekrarla
Bir kez
Her gün
Her hafta
Her ay
Tekrar aralığı (gün)
Tamamlandı olarak işaretle
Geri al
15 dakika ertele
Duraklat
Sürdür
Duraklatıldı
Tamamlandı
Yaklaşan
Bugünün bakımı
Son etkinlik
Küçük bir ilgi burada başlar
Günlük bakımını planlamak için ilk evcil hayvanınızı ekleyin.
Önünüzde sakin bir gün var
Besleme, yürüyüş veya bakım hatırlatıcısı ekleyin.
Tamamlanan bakımlar burada görünür.
Evcil hayvanınızın kayıtlarını bir arada tutun.
Yükleniyor…
Bir sorun oluştu. Lütfen tekrar deneyin.
Tekrar dene
Lütfen bir ad girin.
Geçerli bir pozitif sayı girin.
Bu evcil hayvan ve tüm kayıtları silinsin mi? Bu işlem geri alınamaz.
Tüm yerel evcil hayvan verileri silinsin mi? Bu işlem geri alınamaz.
Porsiyon
Öğün türü
Beslenme notları
Süre (dakika)
Mesafe
Yürüyüş kaydet
Klinik veya hizmet sağlayıcı
İletişim bilgileri
Ek
Belge ekle
Reçete edilen doz
Başlangıç tarihi
Bitiş tarihi
Aşı adı
Uygulama tarihi
Sonraki planlanan tarih
Sağlık kaydı ekle
Ağırlık günlüğü
Ağırlık kaydı ekle
Ölçümler
Atanan kişi
Ev halkı bakımı
Ev halkından kişi
Evcil hayvan bakıcısı raporu
Ev talimatları
Acil durum kişisi
Metni dışa aktar
PDF dışa aktar
Verileri dışa aktar
Bu dosya özel sağlık ve iletişim bilgileri içerebilir. Devam edilsin mi?
Dışa aktarma kaydedildi.
Pawverio Premium
Tek seferlik satın almayla daha fazla evcil hayvan, sağlık araçları ve bakıcı raporları.
Premium kilidini aç
Satın alımları geri yükle
Amazon mağazası kullanılamıyor. Mevcut kayıtlarınız erişilebilir kalır.
Satın alımınız kontrol ediliyor…
Satın alım iptal edildi.
Satın alım tamamlanamadı. Lütfen tekrar deneyin.
Premium etkin
Dil
Cihaz dilini kullan
Ölçü birimleri
Kilogram (kg)
Pound (lb)
Görünüm
Cihaz temasını kullan
Açık
Koyu
Hatırlatıcılar
Evcil hayvan bakımı hatırlatıcıları için bildirimlere izin verin.
Sessiz saatler
Sessiz saatlerde hatırlatıcılar geciktirilir. Zamana duyarlı bakımı doğrudan kontrol edin.
Gizlilik
Dışa aktarmadığınız sürece evcil hayvan kayıtları bu cihazda kalır. Ses hizmetleri, cihaz sağlayıcınızın koşullarına göre sesi işleyebilir. Pawverio mikrofon kayıtlarını saklamaz.
Tüm evcil hayvan verilerini sil
Şifreli yedekleme
Yedekleme parolası
Uygulama kilidi
Sesli geri bildirim
Hareketi azalt
Sesinizle bakım
Konuşmak için dokunun
Dinlemeyi durdur
Dinleniyor…
Tanınan metni incele
Komutu çalıştır
Bir bakım komutu yazın veya söyleyin
Bu cihazda konuşma tanıma kullanılamıyor. Komut yazın veya bakım ekranlarını kullanın.
Bir komut söylemeyi seçtiğinizde mikrofon erişimine izin verin.
Mikrofon erişimi kapalı. Komut yazın veya cihaz ayarlarından erişimi açın.
Konuşma tanınmadı. Tekrar deneyin veya komutunuzu yazın.
Bu komut anlaşılamadı. Bir bakım ekranını açmayı deneyin.
Onaylamadan önce bu bakım işlemini inceleyin.
Bu cihazda bu dil için sesli geri bildirim kullanılamıyor.
Pawverio’ya hoş geldiniz
Dilinizi seçin
İlk evcil hayvanınızı tanıtın
Özenli bir rutin oluşturun
Bakımınız, kontrolünüz
Bakıma başla
Bu cihazda saklanır
Veterinerinizin talimatlarını kaydedin. Pawverio diyet, doz veya tedavi önermez.
Evcil hayvan profil fotoğrafı
Sesli asistanı aç
Yeni öğe ekle
YYYY-AA-GG
SS:DD
%1$d bakım görevi
%1$d bakım görevi'''

TRANSLATIONS['id-ID']='''Pawverio
Setiap hewan. Setiap rutinitas. Dirawat sepenuh hati.
Hari ini
Hewan
Kesehatan
Riwayat
Pengaturan
Tambah hewan
Edit hewan
Profil hewan
Nama
Spesies
Anjing
Kucing
Lainnya
Ras
Usia
Tanggal lahir
Berat badan
Nomor microchip
Alergi atau sensitivitas
Catatan
Foto
Tambah foto
Simpan
Batal
Hapus
Edit
Tutup
Berikutnya
Kembali
Lewati
Selesai
Konfirmasi
Cari
Opsional
Pemberian makan
Jalan-jalan
Perawatan
Obat
Janji dokter hewan
Vaksinasi
Perawatan khusus
Tambah tugas perawatan
Edit tugas perawatan
Nama tugas
Hewan
Tanggal terjadwal
Waktu
Ulangi
Sekali
Setiap hari
Setiap minggu
Setiap bulan
Ulangi setiap (hari)
Tandai selesai
Urungkan
Tunda 15 menit
Jeda
Lanjutkan
Dijeda
Selesai
Mendatang
Perawatan hari ini
Aktivitas terbaru
Perhatian kecil dimulai di sini
Tambahkan hewan pertama untuk merencanakan perawatan hariannya.
Hari yang tenang menanti
Tambahkan pengingat makan, jalan-jalan, atau perawatan.
Perawatan yang selesai akan muncul di sini.
Simpan catatan hewan di satu tempat.
Memuat…
Terjadi kesalahan. Silakan coba lagi.
Coba lagi
Masukkan nama.
Masukkan angka positif yang valid.
Hapus hewan ini dan semua catatan terkait? Tindakan ini tidak dapat dibatalkan.
Hapus semua data hewan lokal? Tindakan ini tidak dapat dibatalkan.
Porsi
Jenis makanan
Catatan makanan
Durasi (menit)
Jarak
Catat jalan-jalan
Klinik atau penyedia layanan
Detail kontak
Lampiran
Tambah dokumen
Dosis yang diresepkan
Tanggal mulai
Tanggal selesai
Nama vaksin
Tanggal pemberian
Tanggal berikutnya
Tambah catatan kesehatan
Jurnal berat badan
Tambah catatan berat badan
Pengukuran
Ditugaskan kepada
Perawatan keluarga
Anggota keluarga
Laporan penitipan hewan
Petunjuk rumah
Kontak darurat
Ekspor teks
Ekspor PDF
Ekspor data
File ini mungkin berisi informasi kesehatan dan kontak pribadi. Lanjutkan?
Ekspor disimpan.
Pawverio Premium
Lebih banyak hewan, alat kesehatan, dan laporan pengasuh dengan pembelian sekali bayar.
Buka Premium
Pulihkan pembelian
Toko Amazon tidak tersedia. Catatan Anda yang ada tetap dapat diakses.
Memeriksa pembelian…
Pembelian dibatalkan.
Pembelian tidak dapat diselesaikan. Silakan coba lagi.
Premium aktif
Bahasa
Gunakan bahasa perangkat
Satuan ukuran
Kilogram (kg)
Pon (lb)
Tampilan
Gunakan tema perangkat
Terang
Gelap
Pengingat
Izinkan notifikasi untuk menerima pengingat perawatan hewan.
Jam tenang
Pengingat ditunda selama jam tenang. Periksa langsung perawatan yang peka waktu.
Privasi
Catatan hewan tetap di perangkat ini kecuali Anda mengekspornya. Layanan suara dapat memproses audio sesuai ketentuan penyedia perangkat. Pawverio tidak menyimpan rekaman mikrofon.
Hapus semua data hewan
Cadangan terenkripsi
Kata sandi cadangan
Kunci aplikasi
Umpan balik suara
Kurangi gerakan
Merawat dengan suara
Ketuk untuk berbicara
Berhenti mendengarkan
Mendengarkan…
Tinjau teks yang dikenali
Jalankan perintah
Ketik atau ucapkan perintah perawatan
Pengenalan suara tidak tersedia di perangkat ini. Ketik perintah atau gunakan layar perawatan.
Izinkan akses mikrofon saat Anda memilih mengucapkan perintah.
Akses mikrofon nonaktif. Ketik perintah atau aktifkan akses di pengaturan perangkat.
Ucapan tidak dikenali. Coba lagi atau ketik perintah Anda.
Perintah tersebut tidak dikenali. Coba buka layar perawatan.
Tinjau tindakan perawatan ini sebelum mengonfirmasi.
Umpan balik suara tidak tersedia untuk bahasa ini di perangkat ini.
Selamat datang di Pawverio
Pilih bahasa Anda
Kenalkan hewan pertama Anda
Buat rutinitas penuh perhatian
Perawatan Anda, kendali Anda
Mulai merawat
Disimpan di perangkat ini
Catat petunjuk dokter hewan. Pawverio tidak menyarankan diet, dosis, atau pengobatan.
Foto profil hewan
Buka asisten suara
Tambah item baru
TTTT-BB-HH
JJ:MM
%1$d tugas perawatan
%1$d tugas perawatan'''

def materialize():
    folder=ROOT/'translations';folder.mkdir(exist_ok=True)
    for locale,text in TRANSLATIONS.items():
        values=text.splitlines()
        assert len(values)==len(CATALOG),f'{locale}: {len(values)} instead of {len(CATALOG)}'
        (folder/f'{locale}.json').write_text(json.dumps(dict(zip(CATALOG,values)),ensure_ascii=False,indent=2)+'\n',encoding='utf-8')

if __name__=='__main__': materialize()
