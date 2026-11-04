# Voice compatibility — measured device inventory

Date: 2026-11-04. Device: Amazon KFTRWI, Android API 28. Engine: `com.ivona.tts.oem/com.ivona.ttslib.ics.IVONATextToSpeechService`.

Evidence: `deliverables/android/build-logs/platform-device-tests/TEST-KFTRWI - 9-_platform-.xml` and the `deviceVoiceCapabilityAndPermissionFallback` / `ttsLocaleCapabilityReport` logcat files alongside it. Final platform test suite: eleven tests, zero failures, errors or skips; the additional test covers reminder locale selection.

Android `SpeechRecognizer.isRecognitionAvailable` returned **false**. Recognition is **BLOCKED** on this installed device configuration for all 19 requested locales. No microphone speech accuracy, accent support, network behavior or offline recognition is claimed. Permission-denial fallback passed; text/touch operation must remain available.

TTS initialization returned **0 / SUCCESS**. The following values are actual `isLanguageAvailable` results. A nonnegative result identifies an engine capability, not an audible-speech test. Language-level fallback can use a different regional pronunciation.

| Requested locale | Raw TTS value | Installed capability |
|---|---:|---|
| en-GB | 1 | Language and country available |
| en-US | 0 | English language available; regional voice not confirmed |
| en-AU | 0 | English language available; regional voice not confirmed |
| es-ES | -2 | Not supported by installed engine |
| es-419 | -2 | Not supported by installed engine |
| fr-FR | -2 | Not supported by installed engine |
| fr-CA | -2 | Not supported by installed engine |
| de-DE | -2 | Not supported by installed engine |
| it-IT | -2 | Not supported by installed engine |
| pt-BR | -2 | Not supported by installed engine |
| pt-PT | -2 | Not supported by installed engine |
| nl-NL | -2 | Not supported by installed engine |
| pl-PL | -2 | Not supported by installed engine |
| sv-SE | -2 | Not supported by installed engine |
| ja-JP | -2 | Not supported by installed engine |
| hi-IN | -2 | Not supported by installed engine |
| ar | -2 | Not supported by installed engine |
| tr-TR | -2 | Not supported by installed engine |
| id-ID | -2 | Not supported by installed engine |

Spoken utterance completion, audibility, pronunciation quality and accessibility review: **NOT TESTED**. Recognition execution: **BLOCKED** on this tablet. The parser's localized text fixtures and app localization are separate checks and do not prove microphone support. Additional Android/Fire devices and installed speech engines require their own inventory and end-to-end tests before claiming support.
