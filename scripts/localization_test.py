"""Host tests for translation integrity. Does not certify language quality."""
import importlib.util
import json
import io
import os
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

spec=importlib.util.spec_from_file_location('pipeline',Path(__file__).with_name('localization.py'))
pipeline=importlib.util.module_from_spec(spec);spec.loader.exec_module(pipeline)

class LocalizationTests(unittest.TestCase):
    def test_required_locales_are_distinct_from_store_languages(self):
        self.assertEqual(len(pipeline.LOCALES),19)
        self.assertIn('es-419',pipeline.LOCALES)
        self.assertIn('ar',pipeline.LOCALES)

    def test_all_catalogs_complete_and_placeholders_preserved(self):
        source=pipeline.read(pipeline.CATALOG)
        for locale in pipeline.LOCALES:
            target=pipeline.dictionary(locale)
            self.assertEqual(set(source),set(target),locale)
            for key in source:
                self.assertEqual(sorted(pipeline.PH.findall(source[key])),sorted(pipeline.PH.findall(target[key])),f'{locale}:{key}')

    def test_resource_qualifiers_include_bcp47_region(self):
        self.assertEqual(pipeline.qualifier('es-419'),'values-b+es+419')
        self.assertEqual(pipeline.qualifier('fr-CA'),'values-b+fr+CA')
        self.assertEqual(pipeline.qualifier('id-ID'),'values-in-rID')

    def test_locale_resource_values_match_expected_catalog_not_english_fallback(self):
        source=pipeline.read(pipeline.CATALOG)
        for locale in pipeline.LOCALES:
            expected=pipeline.dictionary(locale)
            path=pipeline.ROOT/'app/src/main/res'/pipeline.qualifier(locale)/'strings.xml'
            import xml.etree.ElementTree as ET
            xml=ET.parse(path)
            actual={node.attrib['name']:node.text or '' for node in xml.findall('string')}
            def decode_android(text):
                if text.startswith('"') and text.endswith('"'):text=text[1:-1]
                return text.replace('\\n','\n').replace("\\'", "'").replace('\\"','"').replace('\\\\','\\')
            for key,value in expected.items():
                self.assertIn(key,actual,f'{locale}:{key} absent; would use default fallback')
                self.assertEqual(decode_android(actual[key]),value,f'{locale}:{key} differs from locale catalog')
            if not locale.startswith('en-'):
                # pt-BR legitimately borrows "Pets"; do not reject identical
                # brand names, units, acronyms or natural loanwords.
                for key in ['nav_today','add_pet','privacy_body']:
                    self.assertNotEqual(expected[key],source[key],f'{locale}:{key} unexpectedly falls back to English')

    def test_cardinal_plural_categories_follow_cldr(self):
        expected={
            'en-GB':{'one','other'},'en-US':{'one','other'},'en-AU':{'one','other'},
            'es-ES':{'one','many','other'},'es-419':{'one','many','other'},
            'fr-FR':{'one','many','other'},'fr-CA':{'one','many','other'},
            'de-DE':{'one','other'},'it-IT':{'one','many','other'},
            'pt-BR':{'one','many','other'},'pt-PT':{'one','many','other'},
            'nl-NL':{'one','other'},'pl-PL':{'one','few','many','other'},
            'sv-SE':{'one','other'},'ja-JP':{'other'},'hi-IN':{'one','other'},
            'ar':{'zero','one','two','few','many','other'},'tr-TR':{'one','other'},'id-ID':{'other'}
        }
        import xml.etree.ElementTree as ET
        for locale,categories in expected.items():
            path=pipeline.ROOT/'app/src/main/res'/pipeline.qualifier(locale)/'strings.xml'
            plural=ET.parse(path).find(".//plurals[@name='tasks_count']")
            self.assertEqual({node.attrib['quantity'] for node in plural},categories,locale)
            for item in plural:
                self.assertEqual(pipeline.PH.findall(item.text or ''),['%1$d'],f'{locale}:{item.attrib["quantity"]}')

    def test_android_escaping_prevents_markup_and_quote_corruption(self):
        self.assertEqual(pipeline.android_escape('A&B <pet>'), '"A&amp;B &lt;pet&gt;"')
        self.assertIn('\\\"',pipeline.android_escape('say "hello"'))
        self.assertIn("\\'",pipeline.android_escape("pet's care"))

    def test_api_credentials_required(self):
        with patch.dict(os.environ,{},clear=True):
            with self.assertRaisesRegex(ValueError,'Configure'):
                pipeline.translate('fr-FR')

    def test_api_rejects_insecure_endpoint_before_request(self):
        with patch.dict(os.environ,{'PAWVERIO_TRANSLATION_URL':'http://example.invalid','PAWVERIO_TRANSLATION_KEY':'test-only-placeholder'},clear=True):
            with self.assertRaisesRegex(ValueError,'HTTPS'):
                pipeline.translate('fr-FR')

    def test_review_digest_is_recorded(self):
        with tempfile.TemporaryDirectory() as temp:
            loc=Path(temp);(loc/'catalog.json').write_text('{}')
            with patch.object(pipeline,'LOC',loc),patch.object(pipeline,'dictionary',return_value={'name':'Nom'}):
                pipeline.review('fr-FR','Test Reviewer')
                result=json.loads((loc/'reviews/fr-FR.json').read_text())
                self.assertEqual(result['status'],'HUMAN_REVIEWED')
                self.assertEqual(len(result['catalog_sha256']),64)

    def test_api_protects_placeholders_and_reuses_cache(self):
        with tempfile.TemporaryDirectory() as temp:
            loc=Path(temp);source=loc/'catalog.json';source.write_text(json.dumps({'tasks_count_one':'%1$d task'}));(loc/'glossary.json').write_text('{}')
            response=json.dumps({'translation':'__PH_0__ tâche'}).encode()
            with patch.object(pipeline,'LOC',loc),patch.object(pipeline,'CATALOG',source),patch.dict(os.environ,{'PAWVERIO_TRANSLATION_URL':'https://example.invalid/translate','PAWVERIO_TRANSLATION_KEY':'test-only-placeholder'},clear=True),patch.object(pipeline.urllib.request,'urlopen',return_value=io.BytesIO(response)) as request:
                pipeline.translate('fr-FR')
                data=json.loads((loc/'translations/fr-FR.json').read_text(encoding='utf-8'))
                self.assertEqual(data['tasks_count_one'],'%1$d tâche')
                submitted=json.loads(request.call_args.args[0].data)
                self.assertEqual(submitted['text'],'__PH_0__ task')
                self.assertNotIn('test-only-placeholder',json.dumps(submitted))
                pipeline.translate('fr-FR')
                self.assertEqual(request.call_count,1)

    def test_api_rejects_lost_placeholder_without_poisoning_cache(self):
        with tempfile.TemporaryDirectory() as temp:
            loc=Path(temp);source=loc/'catalog.json';source.write_text(json.dumps({'tasks_count_one':'%1$d task'}));(loc/'glossary.json').write_text('{}')
            with patch.object(pipeline,'LOC',loc),patch.object(pipeline,'CATALOG',source),patch.dict(os.environ,{'PAWVERIO_TRANSLATION_URL':'https://example.invalid/translate','PAWVERIO_TRANSLATION_KEY':'test-only-placeholder'},clear=True),patch.object(pipeline.urllib.request,'urlopen',return_value=io.BytesIO(b'{"translation":"lost count"}')):
                with self.assertRaisesRegex(ValueError,'lost protected placeholder'):pipeline.translate('fr-FR')
                self.assertEqual(list((loc/'cache').glob('*.json')),[])

if __name__=='__main__': unittest.main(verbosity=2)
