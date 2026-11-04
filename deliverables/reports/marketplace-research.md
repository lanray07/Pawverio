# Amazon Appstore research

Checked 4 November 2026. Sources are public Amazon documentation; no developer-console account was used.

Amazon ended Appstore support for generic Android mobile devices on 20 August 2025. Its announcement retains Fire tablet and Fire TV support. Pawverio’s Amazon commercial target must therefore remain compatible Fire tablets; a generic Android APK is a separate distribution concern. [Amazon announcement](https://developer.amazon.com/apps-and-games/blogs/2025/02/upcoming-changes-to-amazon-appstore-for-android-devices-and-coins-program).

Application languages, listing languages, distribution eligibility, speech recognition and text-to-speech are different dimensions. The international-distribution page documents listing languages and marketplaces, but its prose and console illustration differ for some regional labels. Verify the actual account selectors before submission. Arabic, Hindi, Turkish and Indonesian app translations do not create equivalent storefront selectors. Country and supported-device selection remain account tasks. [International distribution](https://developer.amazon.com/docs/app-submission/distribute-your-app-internationally.html).

Tablet assets require 114×114 and 512×512 transparent PNG icons. Screenshots: 3–10, PNG/JPEG, landscape or portrait. Documented landscape dimensions: 800×480, 1024×600, 1280×720, 1280×800, 1920×1080, 1920×1200, 2560×1600. Promotional image: 1024×500. Short description limit: 2,000 bytes; long description: 4,000 characters; 3–5 feature bullets. Keywords are an optional documented field. Use store-provided IAP prices. [Appstore details](https://developer.amazon.com/docs/app-submission/appstore-details.html).

The five requested matrices and a keyword-intent matrix are in `localization/`. All storefront text is an editorial draft. No locale is approved for release. Listing claims must be reconciled with end-to-end verification; no search-volume, ranking, trademark-clearance or store-approval claim is made.

Release dependencies: publisher identity/contact, native language review, console locale/device/country confirmation, Amazon IAP product setup and appropriate store testing. A category recommendation is **Lifestyle**, subject to checking available console categories. Name clearance remains unverified.
