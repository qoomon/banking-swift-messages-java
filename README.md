# Banking Swift Messages Parser and Composer [![starline](https://starlines.qoo.monster/assets/qoomon/banking-swift-messages-java)](https://github.com/qoomon/starline)

Parser for Financial SWIFT Messages
SWIFT = Society for Worldwide Interbank Financial Telecommunication

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Build Workflow](https://github.com/qoomon/banking-swift-messages-java/workflows/Build/badge.svg)](https://github.com/qoomon/banking-swift-messages-java/actions)
[![Test Coverage](https://api.codeclimate.com/v1/badges/e611239eea560ee9c72c/test_coverage)](https://codeclimate.com/github/qoomon/banking-swift-messages-java/test_coverage)

### Releases

[![Release](https://jitpack.io/v/qoomon/banking-swift-messages-java.svg)](https://jitpack.io/#qoomon/banking-swift-messages-java)

> [!Important]
> From version `2.0.0` on Java 21 is required


#### Supported Message Types (so far)
* **MT940**
* **MT942**

If you need more MT formats just let me know and create a new [issue](https://github.com/qoomon/banking-swift-messages-java/issues)


#### Usage
see [tests](/src/test/java/com/qoomon/banking/swift/message/SwiftMessageReaderTest.java)


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
