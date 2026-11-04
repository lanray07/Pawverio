# Pawverio localization workflow

`catalog.json` is the source interface catalog. Materialized JSON translations are in `translations/`; Android output is in `app/src/main/res/values*/strings.xml`. There are 19 requested application locales. They are AI-authored editorial drafts, not human-reviewed translations. Related regional locales share appropriate base wording; Portuguese variants have separate catalogs. Private pet records are never translated automatically.

Run on the build host:

```powershell
python scripts/localization.py extract
python scripts/localization.py generate
python scripts/localization.py validate
python scripts/localization_test.py
python localization/store_drafts.py
python localization/voice_drafts.py
python localization/matrices.py
```

The extraction report inventories resource references and literal Compose text candidates. Review candidates in context: a brand name or dynamic user content may be intentionally unchanged. It is a heuristic; manually inspect platform notifications, generated exports and dynamically composed text too.

Validation checks catalog completeness, nonempty values, placeholder preservation, valid XML, resource uniqueness and plural categories. Arabic includes six quantity categories; Polish includes four. Android selects categories by locale. These checks do not prove natural wording, voice recognition, layout fit, screen-reader pronunciation or regional formatting.

Plural category sets follow [Unicode CLDR cardinal rules](https://www.unicode.org/cldr/charts/49/supplemental/language_plural_rules.html). Spanish, French, Italian and Portuguese also include `many`; Japanese and Indonesian have only `other`. Indonesian uses Android’s historical `values-in-rID` resource qualifier while the application’s public BCP47 locale remains `id-ID`. Generated test-only locale expectations compare Android resources with catalog text; host tests also compare every locale resource value and check contextual labels against accidental English fallback.

To translate changed catalog text through a configured service, set host environment variables `PAWVERIO_TRANSLATION_URL` and `PAWVERIO_TRANSLATION_KEY`, then run `python scripts/localization.py translate --locale fr-FR`. The HTTPS API receives catalog text with protected placeholders plus glossary, never application records. It must return `{"translation":"..."}`. Cache entries use a hash of locale, source and glossary. Never put API keys in Android resources or Gradle build constants. Cached machine translations are still pending review.

After an actual human reviews the full catalog, record their declaration with `python scripts/localization.py review --locale fr-FR --reviewer "Reviewer name"`. The review stores a catalog digest and becomes stale if text changes. This command is an audit record, not proof of review by itself. Visual review on real screens and device support are separately tracked.

The legacy AI drafting generators `translation_drafts.py` and `additional_translations.py` can recreate the initial drafts but must not overwrite edited, reviewed catalogs. If rebuilding initial drafts intentionally, run them in that order before `generate`.

For RTL testing, select Arabic inside the app; test onboarding, all editors, dialogs, navigation and exported content in portrait/landscape and with TalkBack. Also test developer-option forced RTL and large font sizes. Do not mark layout PASS from XML validation alone. Capture actual localized UI and compare to `screenshot-copy.json`; root’s screenshot pipeline stores capture evidence separately.

`coverage.csv`, `validation-report.json`, review records and the five independent matrices are release evidence. Currently no target locale is advertised as fully verified. Voice dictionaries support parsing; installed speech/TTS services remain device dependencies. Use command text fallback when a speech engine/language is absent. Non-English walk durations currently require numeric digits in parser examples.

Where available, `matrices.py` imports actual KFTRWI/API28 capability logs. That tablet reported no speech-recognition service. Its IVONA TTS engine reported country-level availability for en-GB, language fallback for en-US/en-AU, and unsupported for the other 16 requested locales. Availability queries do not verify audible output, pronunciation or offline synthesis. No microphone recognition or audio-output test is claimed from these logs.
