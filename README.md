# Banking Swift Messages Parser and Composer

Parser for Financial SWIFT Messages
SWIFT = Society for Worldwide Interbank Financial Telecommunication

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)


[![Build Workflow](https://github.com/qoomon/banking-swift-messages-java/workflows/Build/badge.svg)](https://github.com/qoomon/banking-swift-messages-java/actions)


[![Dependency Monitoring](https://badgen.net/badge/dependency%20monitoring/%20/green?icon=dependabot)](https://github.com/qoomon/banking-swift-messages-java/pulls/app%2Fdependabot-preview)


[![Known Vulnerabilities](https://snyk.io/test/github/qoomon/banking-swift-messages-java/badge.svg)](https://snyk.io/test/github/qoomon/banking-swift-messages-java)


[![Maintainability](https://api.codeclimate.com/v1/badges/e611239eea560ee9c72c/maintainability)](https://codeclimate.com/github/qoomon/banking-swift-messages-java/maintainability)

[![Test Coverage](https://api.codeclimate.com/v1/badges/e611239eea560ee9c72c/test_coverage)](https://codeclimate.com/github/qoomon/banking-swift-messages-java/test_coverage)




### Releases

[![Release](https://jitpack.io/v/qoomon/banking-swift-messages-java.svg)](https://jitpack.io/#qoomon/banking-swift-messages-java)


#### Supported Message Types (so far)
* **MT940**
* **MT942**

If you need more MT formats just let me know and create a new [issue](https://github.com/qoomon/banking-swift-messages-java/issues)


## Dev Notes
[SEPA Verwendugszweck Fields](https://www.hettwer-beratung.de/sepa-spezialwissen/sepa-technische-anforderungen/sepa-gesch%C3%A4ftsvorfallcodes-gvc-mt-940/)
* EREF : Ende-zu-Ende Referenz
* KREF : Kundenreferenz
* MREF : Mandatsreferenz
* BREF : Bankreferenz
* RREF : Retourenreferenz
* CRED : Creditor-ID
* DEBT : Debitor-ID
* COAM : Zinskompensationsbetrag
* OAMT : Ursprünglicher Umsatzbetrag
* SVWZ : Verwendungszweck
* ABWA : Abweichender Auftraggeber
* ABWE : Abweichender Empfänger
* IBAN : IBAN des Auftraggebers
* BIC : BIC des Auftraggebers
