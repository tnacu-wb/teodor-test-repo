# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts >> PI Baseline E2E - Guest PIBA Pay on Arrival Amendment >> Test Book as Guest user: 1 night, 1 room (Double), 2 adults, Flex rate, meals, donations - POA BAC card and Amend by change room type. TestCase ID: 380779.
- Location: tests/regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts:51:7

# Error details

```
Error: Booking confirmation page did not load within 120s after 3D Secure challenge. Payment processing or redirect may have timed out.
```

# Page snapshot

```yaml
- generic [ref=f53e1]:
  - generic [ref=f53e3]:
    - generic [ref=f53e6]:
      - link [ref=f53e9] [cursor=pointer]:
        - /url: https://www.uat.premierinn.digital/de/de/home.html
      - generic [ref=f53e13]:
        - paragraph [ref=f53e22]: Frühstück wählen
        - generic [ref=f53e23]:
          - separator [ref=f53e24]
          - paragraph [ref=f53e32]: Ihre Daten
        - generic [ref=f53e33]:
          - separator [ref=f53e34]
          - generic [ref=f53e35]:
            - paragraph [ref=f53e38]: "3"
            - paragraph [ref=f53e39]: Buchung abschließen
    - generic [ref=f53e40]:
      - generic [ref=f53e41]:
        - generic [ref=f53e42]:
          - generic [ref=f53e43]:
            - paragraph [ref=f53e44]: Zeitpunkt der Zahlung
            - alert [ref=f53e45]:
              - generic [ref=f53e49]: Entschuldigung, Ihre Transaktion konnte aufgrund eines Fehlers nicht abgeschlossen werden. Bitte versuchen Sie es erneut.
            - paragraph [ref=f53e52]: Bitte wählen Sie aus, wann Sie für Ihre Buchung bezahlen möchten. Um Ihre Angaben zu überprüfen, werden Sie möglicherweise zu Ihrer Bank weitergeleitet
            - radiogroup "Zeitpunkt der Zahlung" [ref=f53e53]:
              - generic [ref=f53e54]:
                - generic [ref=f53e56] [cursor=pointer]:
                  - radio "Jetzt bezahlen Zahlen Sie Ihren Aufenthalt sofort über eine sichere Verbindung" [ref=f53e57]
                  - generic [ref=f53e60]:
                    - paragraph [ref=f53e61]: Jetzt bezahlen
                    - paragraph [ref=f53e62]: Zahlen Sie Ihren Aufenthalt sofort über eine sichere Verbindung
                - generic [ref=f53e64] [cursor=pointer]:
                  - radio "Mit Kreditkarte reservieren und bei Ankunft bezahlen Reservieren Sie Ihre Buchung durch Eingabe Ihrer Kartendetails, es wird jetzt noch keine Abbuchung vorgenommen" [checked] [ref=f53e65]
                  - generic [ref=f53e68]:
                    - paragraph [ref=f53e69]: Mit Kreditkarte reservieren und bei Ankunft bezahlen
                    - paragraph [ref=f53e70]: Reservieren Sie Ihre Buchung durch Eingabe Ihrer Kartendetails, es wird jetzt noch keine Abbuchung vorgenommen
          - generic [ref=f53e71]:
            - paragraph [ref=f53e72]: Zahlungsmethode
            - paragraph [ref=f53e73]: Die Zahlung wird von einem sicheren Drittanbieter abgewickelt.
            - radiogroup "Zahlungsmethode" [ref=f53e74]:
              - generic [ref=f53e76] [cursor=pointer]:
                - radio "Neue Kredit-/Debitkarte Mastercard Credit American Express Diners Club Visa Debit Electron Maestro Mastercard Debit Visa Credit" [checked] [ref=f53e77]
                - generic [ref=f53e81]:
                  - paragraph [ref=f53e83]: Neue Kredit-/Debitkarte
                  - generic [ref=f53e84]:
                    - img "Mastercard Credit" [ref=f53e85]
                    - img "American Express" [ref=f53e86]
                    - img "Diners Club" [ref=f53e87]
                    - img "Visa Debit" [ref=f53e88]
                    - img "Electron" [ref=f53e89]
                    - img "Maestro" [ref=f53e90]
                    - img "Mastercard Debit" [ref=f53e91]
                    - img "Visa Credit" [ref=f53e92]
              - generic [ref=f53e94] [cursor=pointer]:
                - radio "Neue Premier Inn Business Pay-Karte Business Account" [ref=f53e95]
                - generic [ref=f53e99]:
                  - paragraph [ref=f53e101]: Neue Premier Inn Business Pay-Karte
                  - paragraph
                  - img "Business Account" [ref=f53e103]
              - generic [ref=f53e105] [cursor=pointer]:
                - radio "Google Pay / Apple Pay Google Pay" [ref=f53e106]
                - generic [ref=f53e110]:
                  - paragraph [ref=f53e112]: Google Pay / Apple Pay
                  - img "Google Pay" [ref=f53e114]
          - group [ref=f53e117]:
            - generic [ref=f53e118]:
              - paragraph [ref=f53e119]: Rechnungsadresse
              - radiogroup [ref=f53e121]:
                - generic [ref=f53e122]:
                  - generic [ref=f53e124] [cursor=pointer]:
                    - radio "Aktuelle Adresse verwenden RH6 0PH 10 Gatwick Road" [checked] [ref=f53e125]
                    - generic [ref=f53e128]:
                      - paragraph [ref=f53e129]: Aktuelle Adresse verwenden
                      - paragraph [ref=f53e130]: RH6 0PH 10 Gatwick Road
                  - generic [ref=f53e132] [cursor=pointer]:
                    - radio "Andere Adresse verwenden" [ref=f53e133]
                    - paragraph [ref=f53e136]: Andere Adresse verwenden
          - generic [ref=f53e139]:
            - paragraph [ref=f53e140]: Gesamtpreis
            - paragraph [ref=f53e141]:
              - generic "£83" [ref=f53e142]:
                - generic [ref=f53e143]: £
                - generic [ref=f53e144]: "83"
            - paragraph [ref=f53e145]: London Heathrow Airport (M4/J4)
            - separator [ref=f53e146]
            - generic [ref=f53e147]:
              - paragraph [ref=f53e148]: Um diese Buchung abzuschließen, müssen Sie auf der nächsten Seite Ihre Kartendaten eingeben
              - paragraph [ref=f53e150]:
                - text: Mit Ihrer Buchungsbestätigung erklären Sie sich mit unseren
                - link "Allgemeinen Geschäftsbedingungen" [ref=f53e151] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/pi/websites/desktop/de/de/unsecured/bedingungen/allgemeine-geschaeftsbedingungen.html
                - text: einverstanden. Stornierungen müssen vor 13:00 Uhr am Anreisetag erfolgen.
              - button "Weiter zu den Zahlungsdetails" [ref=f53e152] [cursor=pointer]
              - alert [ref=f53e153]:
                - generic [ref=f53e157]: Entschuldigung, Ihre Transaktion konnte aufgrund eines Fehlers nicht abgeschlossen werden. Bitte versuchen Sie es erneut.
          - paragraph [ref=f53e165] [cursor=pointer]: Ein Schritt zurück
        - generic [ref=f53e172]:
          - heading "Ihre personenbezogenen Daten sind bei uns sicher und geschützt." [level=3] [ref=f53e174]
          - generic [ref=f53e175]:
            - paragraph [ref=f53e177]: "Alle Einzelheiten dazu, wie wir Ihre Daten nutzen, finden Sie in unserer Datenschutzerklärung. Premier Inn Hotels Limited (im Gesellschaftsregister des Vereinigten Königreichs unter der Nummer 5137608 registriert) ist ein Mitglied der Whitbread Group, deren Muttergesellschaft Whitbread Group PLC (im Gesellschaftsregister des Vereinigten Königreichs unter der Nummer 29423 registriert) ist. Geschäftssitz: Whitbread Court, Houghton Hall Business Park, Porz Avenue, Dunstable LU5 5XE, Großbritannien."
            - link "Bitte lesen Sie unsere Datenschutzerklärung" [ref=f53e178] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/bedingungen/datenschutz.html
          - button [ref=f53e179] [cursor=pointer]:
            - heading "Erfahren Sie mehr" [level=3] [ref=f53e180]
      - generic [ref=f53e183]:
        - generic [ref=f53e184]:
          - generic [ref=f53e185]:
            - heading "London Heathrow Airport (M4/J4)" [level=5] [ref=f53e186]
            - generic [ref=f53e188]:
              - heading "Shepiston Lane" [level=6] [ref=f53e189]
              - heading "Middlesex" [level=6] [ref=f53e190]
              - heading "UB3 1RW" [level=6] [ref=f53e191]
          - separator [ref=f53e192]
          - generic [ref=f53e195]:
            - heading "Gesamtpreis" [level=2] [ref=f53e197]
            - heading [level=2] [ref=f53e198]:
              - generic "£83" [ref=f53e199]:
                - generic [ref=f53e200]: £
                - generic [ref=f53e201]: "83"
            - paragraph [ref=f53e202]: Inkl. MwSt.
          - generic [ref=f53e203]:
            - separator [ref=f53e204]
            - generic [ref=f53e206]:
              - paragraph [ref=f53e207]: "Tarif: Flex"
              - paragraph [ref=f53e208]: Zahlung sofort oder bei Anreise. Kostenlose Stornierung und Änderung bis 13:00 Uhr am Anreisetag.
          - separator [ref=f53e209]
          - generic [ref=f53e210]:
            - generic [ref=f53e211]:
              - paragraph [ref=f53e212]: Anreise
              - paragraph [ref=f53e213]: So. 18 Okt. 2026
            - generic [ref=f53e214]:
              - paragraph [ref=f53e215]: Abreise
              - paragraph [ref=f53e216]: Mo. 19 Okt. 2026
            - generic [ref=f53e217]:
              - paragraph [ref=f53e218]: Aufenthalt
              - paragraph [ref=f53e219]: 1 Nacht
          - generic [ref=f53e221]:
            - separator [ref=f53e223]
            - generic [ref=f53e224]:
              - paragraph [ref=f53e225]:
                - text: Zimmer 1
                - generic [ref=f53e226]: (Doppel Zimmer)
              - paragraph [ref=f53e227]: 2 Erwachsene
              - paragraph [ref=f53e228]: Keine Extras ausgewählt
              - paragraph [ref=f53e230]: Unbegrenztes Frühstücksbuffet für 2 Erwachsene
        - paragraph [ref=f53e232]:
          - text: Mit Ihrer Buchungsbestätigung erklären Sie sich mit unseren
          - link "Allgemeinen Geschäftsbedingungen" [ref=f53e233] [cursor=pointer]:
            - /url: https://www.uat.premierinn.digital/content/pi/websites/desktop/de/de/unsecured/bedingungen/allgemeine-geschaeftsbedingungen.html
          - text: einverstanden. Stornierungen müssen vor 13:00 Uhr am Anreisetag erfolgen.
        - generic [ref=f53e235]:
          - status [ref=f53e237]:
            - paragraph [ref=f53e244]: Zahlung bei Anreise. Kostenlose Stornierung und Änderung bis 13:00 Uhr am Anreisetag.
          - status [ref=f53e246]:
            - paragraph [ref=f53e253]: "Wenn Sie bei der Anreise in bar bezahlen, werden Sie an der Rezeption um Vorlage eines gültigen Lichtbildausweises gebeten. Folgende Dokumente werden akzeptiert: Reisepass, Führerschein, Personalausweis oder Polizeidienstausweis."
        - alert [ref=f53e255]:
          - generic [ref=f53e259]: Entschuldigung, Ihre Transaktion konnte aufgrund eines Fehlers nicht abgeschlossen werden. Bitte versuchen Sie es erneut.
  - alert [ref=f53e262]: Buchung abschließen
  - generic:
    - region "Notifications-top"
    - region "Notifications-top-left"
    - region "Notifications-top-right"
    - region "Notifications-bottom-left"
    - region "Notifications-bottom"
    - region "Notifications-bottom-right"
```

# Test source

```ts
  29  |   readonly submitButton: Locator = this.page.locator('input#buttonSubmit, input#submit-btn');
  30  |   readonly redirectLink: Locator = this.page.locator('td#clickIfNotRedirected a, a#redirect-link');
  31  | 
  32  |   // ######## UI actions/navigation ########
  33  | 
  34  |   /** Select the Confirm payment option on the 3D Secure host. */
  35  |   async selectConfirmPaymentOption(): Promise<void> {
  36  |     console.log('Select Confirm payment option');
  37  |     await this.confirmPaymentRadio.click();
  38  |   }
  39  | 
  40  |   /** Select the Decline payment option on the 3D Secure host. */
  41  |   async selectDeclinePaymentOption(): Promise<void> {
  42  |     console.log('Select Decline payment option');
  43  |     await this.declinePaymentRadio.click();
  44  |   }
  45  | 
  46  |   /** Submit the selected 3D Secure outcome. */
  47  |   async clickOnSubmitButton(): Promise<void> {
  48  |     console.log('Click 3D Secure submit button');
  49  |     await this.submitButton.click();
  50  |   }
  51  | 
  52  |   /** Follow the manual redirect link when 3D Secure does not redirect automatically. */
  53  |   async clickOnRedirectButton(): Promise<void> {
  54  |     console.log('Click 3D Secure redirect button');
  55  |     await this.redirectLink.first().scrollIntoViewIfNeeded();
  56  |     await this.redirectLink.first().click();
  57  |   }
  58  | 
  59  |   /**
  60  |    * Handle the 3D Secure challenge flow on the Worldline/SIX hosted page.
  61  |    *
  62  |    * - If the 3DS page does not appear (submit button not visible within 10s), returns early.
  63  |    * - If displayed: selects "Confirm payment" radio and clicks Submit.
  64  |    * - After Submit: waits 3s for auto-redirect (submit button disappearing).
  65  |    * - If no auto-redirect: waits up to 10s for redirect link and clicks it.
  66  |    * - If redirect link not found: throws a clear error.
  67  |    */
  68  |   async confirmPayment(): Promise<void> {
  69  |     console.log('Confirming payment via 3D Secure challenge');
  70  |     // Check if 3DS page is displayed — submit button visible within 10s
  71  |     const isThreeDSVisible = await this.submitButton
  72  |       .isVisible()
  73  |       .catch(() => false);
  74  | 
  75  |     if (!isThreeDSVisible) {
  76  |       // Wait up to 10s for submit button to appear
  77  |       try {
  78  |         await this.submitButton.waitFor({ state: 'visible' });
  79  |       } catch {
  80  |         // 3DS page not shown — proceed directly to confirmation page
  81  |         return;
  82  |       }
  83  |     }
  84  | 
  85  |     // 3DS page is displayed — select "Confirm payment" and click Submit
  86  |     await this.selectConfirmPaymentOption();
  87  |     await this.clickOnSubmitButton();
  88  | 
  89  |     // Wait 3s to check if auto-redirect occurred (submit button disappears)
  90  |     try {
  91  |       await this.submitButton.waitFor({ state: 'hidden', timeout: 3000 });
  92  |       // Auto-redirect occurred — done
  93  |       return;
  94  |     } catch {
  95  |       // No auto-redirect — need to find and click redirect link
  96  |     }
  97  | 
  98  |     // Wait up to 10s for redirect link to appear and click it
  99  |     try {
  100 |       await this.redirectLink.first().waitFor({ state: 'visible' });
  101 |       await this.clickOnRedirectButton();
  102 |     } catch {
  103 |       throw new Error(
  104 |         '3D Secure page did not complete the redirect. ' +
  105 |           'The manual redirect link was not found within 10 seconds after the auto-redirect failed.'
  106 |       );
  107 |     }
  108 |   }
  109 | 
  110 |   /**
  111 |    * Wait for navigation to the booking confirmation page after 3DS challenge.
  112 |    * Uses an extended timeout (120s) because payment processing and redirect
  113 |    * back to the merchant can take significant time.
  114 |    *
  115 |    * Waits for the booking confirmation page indicator element to be visible,
  116 |    * which signals that the payment was processed and the redirect completed.
  117 |    * @throws Error if the booking confirmation page does not load within 120s
  118 |    */
  119 |   async waitForConfirmationPage(): Promise<void> {
  120 |     console.log('Waiting for booking confirmation page after 3D Secure');
  121 |     try {
  122 |       // Wait for the booking confirmation container to appear on the page.
  123 |       // This element is the primary indicator that payment succeeded and the
  124 |       // Worldline/SIX redirect back to the merchant site completed.
  125 |       await this.page
  126 |         .locator('[data-testid="ThanksForBooking-Container"]')
  127 |         .waitFor({ state: 'visible', timeout: 120000 });
  128 |     } catch {
> 129 |       throw new Error(
      |             ^ Error: Booking confirmation page did not load within 120s after 3D Secure challenge. Payment processing or redirect may have timed out.
  130 |         'Booking confirmation page did not load within 120s after 3D Secure challenge. ' +
  131 |           'Payment processing or redirect may have timed out.'
  132 |       );
  133 |     }
  134 |   }
  135 | 
  136 |   /** Decline the 3D Secure challenge when it is displayed. */
  137 |   async declinePayment(): Promise<void> {
  138 |     console.log('Declining payment via 3D Secure challenge');
  139 |     if (!(await this.submitButton.isVisible().catch(() => false))) {
  140 |       try {
  141 |         await this.submitButton.waitFor({ state: 'visible', timeout: 10000 });
  142 |       } catch {
  143 |         console.log('3D secure page was not displayed');
  144 |         return;
  145 |       }
  146 |     }
  147 | 
  148 |     await this.selectDeclinePaymentOption();
  149 |     await this.clickOnSubmitButton();
  150 |     if (await this.submitButton.isVisible().catch(() => false)) {
  151 |       await this.redirectLink.first().waitFor({ state: 'visible', timeout: 10000 });
  152 |       await this.clickOnRedirectButton();
  153 |     }
  154 |   }
  155 | 
  156 |   // ######## UI validations ########
  157 | 
  158 |   /** Validate the 3D Secure page is displayed. */
  159 |   async validatePage(): Promise<void> {
  160 |     console.log('Validate 3D Secure page');
  161 |     await expect(this.submitButton, '3D Secure submit button should be visible').toBeVisible();
  162 |     await expect(this.confirmPaymentRadio, '3D Secure confirm-payment option should be visible').toBeVisible();
  163 |   }
  164 | 
  165 |   /** Validate the booking summary appears to the right of the 3D Secure form. */
  166 |   async validateBookingSummaryComponentPosition(): Promise<void> {
  167 |     console.log('Validate 3D Secure booking summary component position');
  168 |     await UiUtils.validateIsLeftOf({
  169 |       leftElement: this.paymentDetailsContainer,
  170 |       rightElement: this.verticalStripSection.bookingSummaryWrapper,
  171 |       maxDistanceBetween: 1100,
  172 |       elementDescription: 'Vertical strip section position on page',
  173 |     });
  174 |   }
  175 | }
  176 | 
```