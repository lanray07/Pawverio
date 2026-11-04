"""Validate the real Amazon draft export against the app SKU and purchase-text drafts."""
import hashlib
import json
import re
from decimal import Decimal
from pathlib import Path


def main():
    root = Path(__file__).resolve().parent.parent
    export = root / 'submission/amazon.sdktester.json'
    catalog = json.loads(export.read_text(encoding='utf-8'), parse_float=Decimal)
    drafts = json.loads((root / 'submission/IAP_LOCALIZED_DRAFTS.json').read_text(encoding='utf-8'))
    source = (root / 'platform/src/main/java/com/pawverio/platform/AmazonBilling.kt').read_text(encoding='utf-8')
    sku = drafts['sku']
    errors = []
    if not re.search(r'constructor\(context: Context, sku: String = "' + re.escape(sku) + '"\)', source):
        errors.append('Default app SKU does not match catalog SKU')
    if set(catalog) != {sku}:
        errors.append('Expected exactly one Premium item')
    item = catalog.get(sku, {})
    if item.get('itemType') != 'ENTITLED':
        errors.append('Premium must be a one-time entitlement')
    if item.get('currencyPriceMap', {}).get('GB') != Decimal('4.99'):
        errors.append('GB draft price differs from authorised test price')
    locale_markets = {'en-US': 'US', 'en-GB': 'GB', 'en-AU': 'AU', 'de-DE': 'DE',
                     'es-ES': 'ES', 'fr-FR': 'FR', 'it-IT': 'IT', 'ja-JP': 'JP', 'pt-BR': 'BR'}
    for text in drafts['translations'].values():
        market = locale_markets[text['locale']]
        if item.get('languageTitleMap', {}).get(market) != text['title']:
            errors.append('Title mismatch: ' + text['locale'])
        if item.get('languageDescriptionMap', {}).get(market) != text['description']:
            errors.append('Description mismatch: ' + text['locale'])
    report = {'status': 'FAIL' if errors else 'PASS', 'sku': sku,
              'scope': 'Actual Amazon draft catalog export; not a live product or transaction test',
              'source_sha256': hashlib.sha256(export.read_bytes()).hexdigest(),
              'locales': len(locale_markets), 'native_review': 'PENDING', 'errors': errors}
    (root / 'deliverables/reports/IAP_CATALOG_VALIDATION.json').write_text(json.dumps(report, indent=2) + '\n', encoding='utf-8')
    print(json.dumps(report))
    return 1 if errors else 0


if __name__ == '__main__':
    raise SystemExit(main())
