# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts >> PI Baseline E2E - Guest PIBA Pay on Arrival Amendment >> Test Book as Guest user: 1 night, 1 room (Double), 2 adults, Flex rate, meals, donations - POA BAC card and Amend by change room type. TestCase ID: 380779.
- Location: tests/regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts:51:7

# Error details

```
TimeoutError: locator.waitFor: Timeout 15000ms exceeded.
Call log:
  - waiting for locator('gmp-advanced-marker').first()

```

# Page snapshot

```yaml
- generic [ref=f21e1]:
  - generic [ref=f21e3]:
    - banner [ref=f21e4]:
      - generic [ref=f21e7]:
        - link [ref=f21e10] [cursor=pointer]:
          - /url: https://www.uat.premierinn.digital/de/de/home.html
        - generic [ref=f21e12]:
          - img "Deutsch" [ref=f21e15] [cursor=pointer]
          - generic [ref=f21e16]:
            - paragraph [ref=f21e19] [cursor=pointer]: Premier Inn entdecken
            - paragraph [ref=f21e22] [cursor=pointer]: Premier Inn Business
            - paragraph [ref=f21e24] [cursor=pointer]: Ihre Buchungen
            - generic [ref=f21e26]:
              - button "Einloggen" [ref=f21e27] [cursor=pointer]
              - button "Registrieren" [ref=f21e28] [cursor=pointer]
    - main [ref=f21e29]:
      - generic [ref=f21e31]:
        - generic [ref=f21e33]:
          - combobox "Ort, Postleitzahl oder Hotelname" [ref=f21e38] [cursor=pointer]
          - textbox "datepicker-input" [ref=f21e49] [cursor=pointer]:
            - /placeholder: Check-In | Abreise
            - text: Heute | Morgen
          - button "1 Erwachsener, 1 Zimmer" [ref=f21e53] [cursor=pointer]
          - button "Suchen" [ref=f21e54] [cursor=pointer]
        - generic [ref=f21e55]:
          - generic [ref=f21e56]:
            - navigation "breadcrumb" [ref=f21e58]:
              - list [ref=f21e59]:
                - listitem [ref=f21e60]:
                  - link "Home" [ref=f21e61] [cursor=pointer]:
                    - /url: /de/de/home.html
                  - text: /
                - listitem [ref=f21e62]:
                  - link "Hotelverzeichnis" [ref=f21e63] [cursor=pointer]:
                    - /url: /de/de/hotels.html
                  - text: /
                - listitem [ref=f21e64]:
                  - link "England" [ref=f21e65] [cursor=pointer]:
                    - /url: /de/de/hotels/england.html
                  - text: /
                - listitem [ref=f21e66]:
                  - link "West Midlands" [ref=f21e67] [cursor=pointer]:
                    - /url: /de/de/hotels/england/west-midlands.html
                  - text: /
                - listitem [ref=f21e68]:
                  - generic [ref=f21e69] [cursor=pointer]: Birmingham
            - heading "Birmingham Hotels" [level=1] [ref=f21e70]
            - paragraph [ref=f21e75]: "Birmingham mag vielleicht nur die zweitgrößte Stadt Englands sein, aber hier ist gar nichts zweitklassig: Schicke Läden und ein faszinierendes Kulturerbe? Ja. Ein pulsierendes Nachtleben und eine lebendige Kulturszene? Klar. Multikulturelle Atmosphäre und Wirtschaftsmotor? Auch das. Buchen Sie ein Zimmer in einem unserer Hotels in Birmingham – wir sind uns sicher, dass die Stadt auch Sie im Nu in ihren Bann ziehen wird."
          - img "Birmingham Hotels" [ref=f21e77]
        - heading "14 Hotels gefunden" [level=6] [ref=f21e78]
        - generic [ref=f21e79]:
          - button "Filter" [ref=f21e82] [cursor=pointer]
          - switch "Karte Raster" [checked] [ref=f21e84] [cursor=pointer]:
            - generic [ref=f21e85]: Karte
            - generic [ref=f21e86]: Raster
        - generic [ref=f21e88]:
          - generic [ref=f21e89]:
            - generic:
              - button "Kurzbefehle"
            - region "Karte" [ref=f21e90]
            - generic [ref=f21e91]:
              - iframe [ref=f21e191]:
                
              - button [ref=f21e192] [cursor=pointer]
              - menubar [ref=f21e193] [cursor=pointer]:
                - menuitemradio [checked] [ref=f21e194]: Karte
                - menuitemradio [ref=f21e195]: Satellit
              - button [ref=f21e196] [cursor=pointer]
              - generic [ref=f21e199] [cursor=pointer]:
                - button [ref=f21e200]
                - button [ref=f21e202]
              - link [ref=f21e204] [cursor=pointer]:
                - /url: https://maps.google.com/maps?ll=52.484607,-1.877282&z=11&t=m&hl=de-DE&gl=US&mapclient=apiv3
              - generic [ref=f21e207]:
                - button [ref=f21e213] [cursor=pointer]: Kurzbefehle
                - generic [ref=f21e214]: Kartendaten ©2026 Google
                - link [ref=f21e223] [cursor=pointer]:
                  - /url: https://www.google.com/intl/de-DE_US/help/terms_maps.html
                  - text: Nutzungsbedingungen
                - link [ref=f21e228] [cursor=pointer]:
                  - /url: https://www.google.com/maps/@52.4846073,-1.8772825,11z/data=!10m1!1e1!12b1?source=apiv3&rapsrc=apiv3
                  - text: Fehler bei Google Maps melden
          - alertdialog "Fehler" [ref=f21e229]:
            - generic [ref=f21e230]:
              - generic [ref=f21e231]:
                - heading "Fehler" [level=2] [ref=f21e232]
                - button "Dialogfeld schließen" [active] [ref=f21e233] [cursor=pointer]
              - generic [ref=f21e235]:
                - generic [ref=f21e236]: Google Maps wurde auf dieser Seite nicht richtig geladen.
                - link "Bist du Inhaber dieser Website?" [ref=f21e238] [cursor=pointer]:
                  - /url: https://developers.google.com/maps/documentation/javascript/error-messages
          - alertdialog "Fehler" [ref=f21e239]:
            - generic [ref=f21e240]:
              - generic [ref=f21e241]:
                - heading "Fehler" [level=2] [ref=f21e242]
                - button "Dialogfeld schließen" [ref=f21e243] [cursor=pointer]
              - generic [ref=f21e245]:
                - generic [ref=f21e246]: Google Maps wurde auf dieser Seite nicht richtig geladen.
                - link "Bist du Inhaber dieser Website?" [ref=f21e248] [cursor=pointer]:
                  - /url: https://developers.google.com/maps/documentation/javascript/error-messages
        - generic [ref=f21e249]:
          - generic [ref=f21e250]:
            - heading "Warum Premier Inn Hotels?" [level=2] [ref=f21e251]
            - paragraph [ref=f21e252]: Wir lieben es, Gastgeber zu sein und bieten Ihnen in unseren Hotels traumhaft guten Schlaf, komfortable Zimmer, ein tolles Design, ein vielfältiges Frühstück und einen besonders herzlichen Service.
            - generic [ref=f21e253]:
              - generic [ref=f21e254]:
                - img "Wir sind überall" [ref=f21e255]
                - heading "Wir sind überall" [level=4] [ref=f21e256]
                - generic [ref=f21e257]: Sie finden uns an über 800 Standorten in Großbritannien und Irland.
              - generic [ref=f21e258]:
                - img "Traumhafte Betten" [ref=f21e259]
                - heading "Traumhafte Betten" [level=4] [ref=f21e260]
                - generic [ref=f21e261]: Schlafen Sie in unseren superbequemen Betten wie auf Wolke sieben.
              - generic [ref=f21e262]:
                - img "Gratis WLAN" [ref=f21e263]
                - heading "Gratis WLAN" [level=4] [ref=f21e264]
                - generic [ref=f21e265]: WLAN ist während Ihres Aufenthalts im gesamten Hotel kostenlos.
              - generic [ref=f21e266]:
                - img "Familienfreundlich" [ref=f21e267]
                - heading "Familienfreundlich" [level=4] [ref=f21e268]
                - generic [ref=f21e269]: Wir haben Familienzimmer und Kinder übernachten und essen gratis* mit.
              - generic [ref=f21e270]:
                - img "Flexible Tarife" [ref=f21e271]
                - heading "Flexible Tarife" [level=4] [ref=f21e272]
                - generic [ref=f21e273]: Wählen Sie einfach aus verschiedenen Buchungs- und Zahlungsoptionen.
              - generic [ref=f21e274]:
                - img "Vielfältiges Frühstück" [ref=f21e275]
                - heading "Vielfältiges Frühstück" [level=4] [ref=f21e276]
                - generic [ref=f21e277]: Genießen Sie unser berühmtes englisches oder kontinentales Frühstück.
          - img "Warum Premier Inn Hotels?" [ref=f21e279]
        - generic [ref=f21e280]:
          - heading "FAQs" [level=2] [ref=f21e281]
          - generic [ref=f21e282]:
            - tablist [ref=f21e283]:
              - tab [selected] [ref=f21e284] [cursor=pointer]:
                - heading "Aktivitäten in der Region" [level=3] [ref=f21e286]
              - tab [ref=f21e287] [cursor=pointer]:
                - heading "Über die Premier Inn Hotels" [level=3] [ref=f21e289]
            - tabpanel "Aktivitäten in der Region" [ref=f21e291]:
              - generic [ref=f21e292]:
                - generic [ref=f21e293]:
                  - button "Was kann man in der Cadbury World erleben?" [ref=f21e295] [cursor=pointer]
                  - button "Warum lohnt sich ein Besuch bei der BBC in Birmingham?" [ref=f21e300] [cursor=pointer]
                  - button "Was ist The Bullring in Birmingham?" [ref=f21e305] [cursor=pointer]
                  - button "Was findet man im The Mailbox in Birmingham?" [ref=f21e310] [cursor=pointer]
                  - button "Was sind die Back to Backs in Birmingham?" [ref=f21e315] [cursor=pointer]
                - generic [ref=f21e319]:
                  - button "Was kann man im Birmingham Museum & Art Gallery alles entdecken?" [ref=f21e321] [cursor=pointer]
                  - button "Was macht das Jewellery Quarter so lohnenswert?" [ref=f21e326] [cursor=pointer]
                  - button "Was gibt es im Pen Museum zu sehen?" [ref=f21e331] [cursor=pointer]
                  - button "Welche Erlebnisse bitet das Wonderful Little World Museum?" [ref=f21e336] [cursor=pointer]
                  - button "Wie kommt man in Birmingham am besten von A nach B?" [ref=f21e341] [cursor=pointer]
        - generic [ref=f21e345]:
          - heading "Mehr entdecken..." [level=2] [ref=f21e346]
          - generic [ref=f21e347]:
            - link [ref=f21e349]:
              - /url: https://www.premierinn.com/de/de/wir-sind-premier/standorte/neue-hotels.html
              - generic [ref=f21e350]:
                - img "Neue Hotels" [ref=f21e351]
                - paragraph [ref=f21e352]: Neue Hotels
            - link [ref=f21e354]:
              - /url: https://www.premierinn.com/de/de/reiseziele/reisefuehrer.html
              - generic [ref=f21e355]:
                - img "Reiseführer" [ref=f21e356]
                - paragraph [ref=f21e357]: Reiseführer
            - link [ref=f21e359]:
              - /url: https://www.premierinn.com/de/de/wir-sind-premier/standorte/flughafenhotels.html
              - generic [ref=f21e360]:
                - img "Flughafenhotels" [ref=f21e361]
                - paragraph [ref=f21e362]: Flughafenhotels
            - link [ref=f21e364]:
              - /url: https://www.premierinn.com/de/de/hotels/beste-hotels.html
              - generic [ref=f21e365]:
                - img "Unsere besten Hotels" [ref=f21e366]
                - paragraph [ref=f21e367]: Unsere besten Hotels
            - text: content
    - generic [ref=f21e370]:
      - generic [ref=f21e374]:
        - generic [ref=f21e375]:
          - paragraph [ref=f21e376]: Premier Inn
          - generic [ref=f21e377]:
            - link "Über uns" [ref=f21e378] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier.html
            - link "Karriere" [ref=f21e379] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/unternehmen/karriere.html
            - link "Stellenanzeigen" [ref=f21e380] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/unternehmen/karriere/stellenanzeigen.html
            - link "Presse" [ref=f21e381] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/presse.html
            - link "Nachhaltigkeit/Menschenrechte" [ref=f21e382] [cursor=pointer]:
              - /url: https://euw1-pubacc-eu-west-1.dit.premierinn.digital/content/dam/pi/websites/desktop/de/unternehmen/erklaerung-whitbread-menschenrechtsstrategie.pdf
            - link "Unternehmen (engl.)" [ref=f21e383] [cursor=pointer]:
              - /url: https://www.whitbread.co.uk/about-us/
        - generic [ref=f21e384]:
          - paragraph [ref=f21e385]: Kontakt
          - generic [ref=f21e386]:
            - link "Kontaktieren Sie uns" [ref=f21e387] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/kontakt.html
            - link "Ihre Buchung bearbeiten" [ref=f21e388] [cursor=pointer]:
              - /url: https://secure2.uat.premierinn.digital/de/de/bookingmanagement.html
            - link "Kontakt für Firmenkunden" [ref=f21e389] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/business/kontakt.html
            - link "Gruppenbuchungen" [ref=f21e390] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/gruppen.html
            - link "FAQs" [ref=f21e391] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen.html
            - link "Feedback" [ref=f21e392] [cursor=pointer]:
              - /url: mailto:feedback-germany@premierinn.com?subject=Feedback%20zu%20Premier%20Inn%20Deutschland%20/%20Feedback%20for%20Premier%20Inn%20Germany&body=Dieses%20E-Mail-Feedback-Formular%20ist%20ausschlie%C3%9Flich%20f%C3%BCr%20Premier%20Inn%20Deutschland%20vorgesehen.%20Wir%20antworten%20innerhalb%20von%203%20-%205%20Werktagen.%20Bitte%20%C3%BCbermitteln%20Sie%20uns%20die%20nachfolgenden%20Angaben%20%E2%80%93%20ohne%20diese%20k%C3%B6nnen%20wir%20Ihr%20Feedback%20aus%20Datenschutzgr%C3%BCnden%20leider%20nicht%20bearbeiten%20oder%20weiterleiten.%0D%0DPlease%20note:%20This%20feedback%20form%20is%20exclusively%20for%20Premier%20Inn%20Germany.%20We%E2%80%99d%20love%20to%20hear%20about%20your%20recent%20experience%20staying%20in%20one%20of%20our%20Premier%20Inn%20hotels%20in%20Germany.%20Please%20provide%20us%20with%20the%20following%20information%20%E2%80%93%20we%E2%80%99ll%20need%20this%20for%20data%20protection%20reasons%20in%20order%20to%20process%20or%20forward%20on%20your%20feedback.%20We%E2%80%99ll%20aim%20to%20respond%20within%20three%20to%20five%20working%20days.%0D%0DName%20/%20Name:%20%0DTelefonnummer%20/%20Telephone%20number:%20%0DPostleitzahl%20/%20Post%20code:%20%0DHotelname%20/%20Hotel%20name:%20%0DBuchungsnummer%20/%20Booking%20number:%20%0DAufenthaltsdatum%20/%20Date%20of%20stay:%20%0DIhre%20Nachricht%20an%20uns%20/%20Your%20feedback:%20%0D.html
        - generic [ref=f21e393]:
          - paragraph [ref=f21e394]: Rechtliches
          - generic [ref=f21e395]:
            - link "Nutzungsbedingungen" [ref=f21e396] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/bedingungen/nutzungsbedingungen.html
            - link "Unsere AGBs" [ref=f21e397] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/bedingungen/allgemeine-geschaeftsbedingungen-deutschland.html
            - link "Cookies" [ref=f21e398] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/bedingungen/cookies.html
            - link "Datenschutz" [ref=f21e399] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/bedingungen/datenschutz.html
            - link "Impressum" [ref=f21e400] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/impressum.html
            - link "Lieferkettensorgfaltspflichten" [ref=f21e401] [cursor=pointer]:
              - /url: https://euw1-pubacc-eu-west-1.dit.premierinn.digital/content/dam/pi/websites/desktop/de/unternehmen/beschwerdeverfahren-lieferkettengesetz.pdf
        - generic [ref=f21e402]:
          - paragraph [ref=f21e403]: Hotels
          - generic [ref=f21e404]:
            - link "Unsere Zimmer" [ref=f21e405] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/schlaf/zimmer.html
            - link "Unsere Betten" [ref=f21e406] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/schlaf/bett.html
            - link "Barrierefreiheit" [ref=f21e407] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/bedingungen/barrierefreiheit.html
            - link "Frühstück & Bar" [ref=f21e408] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/essen.html
            - link "WLAN" [ref=f21e409] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/wlan.html
            - link "Meetingräume" [ref=f21e410] [cursor=pointer]:
              - /url: https://www.premiermeetings.co.uk/
        - generic [ref=f21e411]:
          - paragraph [ref=f21e412]: Gut zu wissen
          - generic [ref=f21e413]:
            - link "Gute-Nacht-Garantie" [ref=f21e414] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/schlaf/gute-nacht-garantie.html
            - link "Mehr für Ihr Geld" [ref=f21e415] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/preis-leistung.html
            - link "Vorteile für Familien" [ref=f21e416] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/familie.html
            - link "So können Sie bezahlen" [ref=f21e417] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen/bezahlung.html
            - link "Buchung ändern/stornieren" [ref=f21e418] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen/buchung-aendern-stornieren.html
            - link "Expansion in Deutschland" [ref=f21e419] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/unternehmen/expansion-in-deutschland.html
        - generic [ref=f21e420]:
          - paragraph [ref=f21e421]: Anderes
          - generic [ref=f21e422]:
            - link "Unsere flexiblen Tarife" [ref=f21e423] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/flexibel-buchen.html
            - link "City Tax" [ref=f21e424] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen/city-tax.html
            - link "Premier Plus-Zimmer" [ref=f21e425] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/schlaf/premier-plus-zimmer.html
            - link "Check-in & Check-out" [ref=f21e426] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen/checkin-checkout.html
            - link "Anreise & Parken" [ref=f21e427] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen/anreise-parken.html
            - link "Sitemap" [ref=f21e428] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/sitemap.html
      - generic [ref=f21e431]:
        - paragraph [ref=f21e432]: Die wichtigsten Premier Inn Hotels
        - generic [ref=f21e433]:
          - generic [ref=f21e434]:
            - link "Hotels Berlin" [ref=f21e435] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/berlin/berlin.html
            - link "Hotels Braunschweig" [ref=f21e436] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/niedersachsen/braunschweig.html
            - link "Hotels Darmstadt" [ref=f21e437] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/hessen/darmstadt.html
            - link "Hotels Dresden" [ref=f21e438] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/sachsen/dresden.html
            - link "Hotels Düsseldorf" [ref=f21e439] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/nordrhein-westfalen/duesseldorf.html
            - link "Hotels Essen" [ref=f21e440] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/nordrhein-westfalen/essen.html
            - link "Hotels Frankfurt" [ref=f21e441] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/hessen/frankfurt.html
          - generic [ref=f21e442]:
            - link "Hotels Freiburg" [ref=f21e443] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/freiburg.html
            - link "Hotels Hamburg" [ref=f21e444] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/hamburg/hamburg.html
            - link "Hotels Hannover" [ref=f21e445] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/niedersachsen/hannover.html
            - link "Hotels Heidelberg" [ref=f21e446] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/heidelberg.html
            - link "Hotels Heilbronn" [ref=f21e447] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/heilbronn.html
            - link "Hotels Karlsruhe" [ref=f21e448] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/karlsruhe.html
            - link "Hotels Köln" [ref=f21e449] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/nordrhein-westfalen/koeln.html
          - generic [ref=f21e450]:
            - link "Hotels Leipzig" [ref=f21e451] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/sachsen/leipzig.html
            - link "Hotels Lindau" [ref=f21e452] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/lindau.html
            - link "Hotels Lübeck" [ref=f21e453] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/schleswig-holstein/luebeck.html
            - link "Hotels Mannheim" [ref=f21e454] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/mannheim.html
            - link "Hotels München" [ref=f21e455] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/muenchen.html
            - link "Hotels Nürnberg" [ref=f21e456] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/nuernberg.html
            - link "Hotels Passau" [ref=f21e457] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/passau.html
          - generic [ref=f21e458]:
            - link "Hotels Regensburg" [ref=f21e459] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/regensburg.html
            - link "Hotels Saarbrücken" [ref=f21e460] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/saarland/saarbruecken.html
            - link "Hotels Stuttgart" [ref=f21e461] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/stuttgart.html
            - link "Hotels Wiesbaden" [ref=f21e462] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/hessen/wiesbaden.html
            - link "Hotels Wolfsburg" [ref=f21e463] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/niedersachsen/wolfsburg.html
            - link "Hotels Wuppertal" [ref=f21e464] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/nordrhein-westfalen/wuppertal.html
            - link "Hotels London" [ref=f21e465] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/england/greater-london/london.html
          - generic [ref=f21e466]:
            - link "Hotels Manchester" [ref=f21e467] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/england/greater-manchester/manchester.html
            - link "Hotels Edinburgh" [ref=f21e468] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/schottland/lothian/edinburgh.html
            - link "Hotels Dublin" [ref=f21e469] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/republik-irland/dublin.html
            - link "Flughafenhotels" [ref=f21e470] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/standorte/flughafenhotels.html
            - link "Unsere besten Hotels" [ref=f21e471] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/beste-hotels.html
            - link "Neue Hotels" [ref=f21e472] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/standorte/neue-hotels.html
            - link "Alle Hotels im Überblick" [ref=f21e473] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels.html
          - generic [ref=f21e474]:
            - link "Reiseführer Berlin" [ref=f21e475] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/berlin.html
            - link "Reiseführer Dresden" [ref=f21e476] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/dresden.html
            - link "Reiseführer Düsseldorf" [ref=f21e477] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/duesseldorf.html
            - link "Reiseführer Hamburg" [ref=f21e478] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/hamburg.html
            - link "Reiseführer Köln" [ref=f21e479] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/koeln.html
            - link "Reiseführer München" [ref=f21e480] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/muenchen.html
            - link "Alle Reiseführer" [ref=f21e481] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/reisefuehrer.html
      - generic [ref=f21e482]:
        - generic [ref=f21e483]: © 2026 Premier Inn
        - generic [ref=f21e484]:
          - link [ref=f21e485] [cursor=pointer]:
            - /url: https://www.facebook.com/PremierInnDeutschland
          - link [ref=f21e487] [cursor=pointer]:
            - /url: https://www.youtube.com/@premierinndeutschland
          - link [ref=f21e489] [cursor=pointer]:
            - /url: https://www.instagram.com/premierinn.de
  - alert [ref=f21e491]: Hotels in Birmingham | Premier Inn | Jetzt direkt buchen
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
  267 |     console.log(`Validating TripAdvisor review link opens to: ${expectedUrl}`);
  268 |     const newPage = await this.clickTripAdvisorReviewLink();
  269 | 
  270 |     // Validate the new tab URL contains the expected path
  271 |     const actualUrl = newPage.url();
  272 |     expect(actualUrl, `URL should contain expected path: ${expectedUrl}`).toContain(expectedUrl);
  273 | 
  274 |     // Close the new tab and return focus to the DLP
  275 |     await newPage.close();
  276 |   }
  277 | 
  278 |   /**
  279 |    * Verify the DLP is currently in map view (not grid view).
  280 |    * Used after back-navigation from HDP to confirm map view state is preserved.
  281 |    */
  282 |   async verifyMapViewIsActive(): Promise<void> {
  283 |     console.log('Verifying map view is active');
  284 |     await expect(this.mapViewElement, 'Map view should be displayed').toBeVisible({ timeout: 10000 });
  285 |   }
  286 | 
  287 |   /**
  288 |    * Click the "View Hotel" button on an open map card to navigate to HDP.
  289 |    * Assumes a map marker has already been clicked and the card is visible.
  290 |    * @returns Promise that resolves when navigation starts
  291 |    */
  292 |   async clickViewHotelOnMapCard(): Promise<void> {
  293 |     console.log('Clicking view hotel button on map card');
  294 |     await this.viewHotelButton.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
  295 |     await this.viewHotelButton.click();
  296 |   }
  297 | 
  298 |   /**
  299 |    * Click a specific map marker by index to open its hotel card.
  300 |    * Skips the region marker (identified by the regionPosition).
  301 |    * @param hotelIndex - 0-based index of the hotel marker (not including region marker)
  302 |    * @param regionPosition - The region marker position string to skip (e.g. "lat,lng")
  303 |    */
  304 |   async clickMapMarker(hotelIndex: number, regionPosition: string): Promise<void> {
  305 |     console.log(`Clicking map marker at hotel index ${hotelIndex}`);
  306 |     await this.mapMarkers.first().waitFor({ state: 'attached', timeout: browser.options.actionTimeout });
  307 | 
  308 |     const markersCount = await this.mapMarkers.count();
  309 |     let hotelCount = 0;
  310 | 
  311 |     for (let i = 0; i < markersCount; i++) {
  312 |       const marker = this.mapMarkers.nth(i);
  313 |       const position = await marker.getAttribute('position');
  314 | 
  315 |       // Skip the region marker
  316 |       if (this.coordinatesMatch(position, regionPosition)) continue;
  317 | 
  318 |       if (hotelCount === hotelIndex) {
  319 |         await marker.evaluate((element: HTMLElement) => element.click());
  320 |         await this.hotelCards.first().waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
  321 |         return;
  322 |       }
  323 |       hotelCount++;
  324 |     }
  325 | 
  326 |     throw new Error(`Map marker at hotel index ${hotelIndex} not found. Total hotel markers: ${hotelCount}`);
  327 |   }
  328 | 
  329 |   /**
  330 |    * Click the map view toggle to switch from grid to map view.
  331 |    */
  332 |   async clickMapView(): Promise<void> {
  333 |     console.log('Switching to map view');
  334 |     await this.mapViewSwitchMapButton.waitFor({ state: 'visible' });
  335 |     await this.mapViewSwitchMapButton.scrollIntoViewIfNeeded();
  336 |     await this.mapViewSwitchMapButton.click();
  337 |     await expect(this.mapViewElement, 'Map view should be displayed after toggle').toBeVisible();
  338 |   }
  339 | 
  340 |   /**
  341 |    * Click the grid view toggle to switch from map to grid view.
  342 |    */
  343 |   async clickGridView(): Promise<void> {
  344 |     console.log('Switching to grid view');
  345 |     await this.mapGridSwitchToggle.waitFor({ state: 'visible' });
  346 |     await this.mapGridSwitchToggle.scrollIntoViewIfNeeded();
  347 |     // Re-click on failure: right after a back-navigation from HDP the toggle can render
  348 |     // before its click handler is rebound, silently swallowing the first click.
  349 |     await expect(async () => {
  350 |       await this.mapGridSwitchToggle.click();
  351 |       await expect(this.page, 'DLP URL should reflect grid view after toggle').toHaveURL(/VIEW=2/, { timeout: 3000 });
  352 |     }).toPass({ timeout: browser.options.actionTimeout });
  353 |     await expect(this.gridViewElement, 'Grid view should be displayed after toggle').toBeVisible();
  354 |     await expect(this.hotelCards.first(), 'Grid view hotel cards should be displayed after toggle').toBeVisible();
  355 |   }
  356 | 
  357 |   /**
  358 |    * Validate hotel cards/pins in map view: verifies markers exist, validates card info on click.
  359 |    * @param hotels - Expected hotels data from the map view API
  360 |    * @param regionPosition - The region marker position string (e.g. "lat,lng")
  361 |    */
  362 |   async validateMapViewCards(
  363 |     hotels: Array<{ name: string; position: string; distanceFromReference?: number; rating?: number }>,
  364 |     regionPosition: string
  365 |   ): Promise<void> {
  366 |     console.log(`Validating map view cards for ${hotels.length} hotels`);
> 367 |     await this.mapMarkers.first().waitFor({ state: 'attached', timeout: browser.options.actionTimeout });
      |                                   ^ TimeoutError: locator.waitFor: Timeout 15000ms exceeded.
  368 | 
  369 |     const markersCount = await this.mapMarkers.count();
  370 |     // Hotels + 1 region marker
  371 |     expect(markersCount, `Map should display ${hotels.length + 1} markers (hotels + region)`).toBe(hotels.length + 1);
  372 | 
  373 |     let hasRegionMarker = false;
  374 | 
  375 |     for (let i = 0; i < markersCount; i++) {
  376 |       const marker = this.mapMarkers.nth(i);
  377 |       const position = await marker.getAttribute('position');
  378 | 
  379 |       if (this.coordinatesMatch(position, regionPosition)) {
  380 |         hasRegionMarker = true;
  381 |         continue;
  382 |       }
  383 | 
  384 |       const hotel = hotels.find(h => this.coordinatesMatch(position, h.position));
  385 |       if (!hotel) continue;
  386 | 
  387 |       // Click the marker to open the card
  388 |       await marker.evaluate((element: HTMLElement) => element.click());
  389 |       await this.hotelCards.first().waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
  390 | 
  391 |       // Validate card elements
  392 |       await expect(this.hotelCardTitle, `Hotel card title should be displayed for ${hotel.name}`).toBeVisible();
  393 |       await expect(this.hotelCardTitle, `Card title should start with ${hotel.name.slice(0, 39)}`).toContainText(hotel.name.slice(0, 39), { timeout: 10000 });
  394 | 
  395 |       await expect(this.hotelCardThumbnail.first(), 'Hotel card thumbnail should be displayed').toBeVisible();
  396 |       await expect(this.viewHotelButton, 'View hotel button should be displayed on card').toBeVisible();
  397 | 
  398 |       // Validate distance display based on hotel data
  399 |       if (hotel.distanceFromReference) {
  400 |         await expect(this.hotelCardDistance.first(), 'Hotel distance should be displayed').toBeVisible();
  401 |       }
  402 |     }
  403 | 
  404 |     expect(hasRegionMarker, 'Map should display region marker').toBe(true);
  405 |   }
  406 | 
  407 |   /**
  408 |    * Validate hotel list elements are displayed in grid view.
  409 |    * Checks that hotel cards have expected sub-elements (thumbnail, title, button).
  410 |    */
  411 |   async validateHotelListElements(expectedHotelNames: string[] = []): Promise<void> {
  412 |     console.log('Validating hotel list elements in grid view');
  413 |     await expect(this.hotelCards.first(), 'Hotel cards should be displayed').toBeVisible({ timeout: browser.options.actionTimeout });
  414 | 
  415 |     const cardCount = await this.hotelCards.count();
  416 |     expect(cardCount, 'At least one hotel card should be displayed').toBeGreaterThan(0);
  417 |     if (expectedHotelNames.length > 0) {
  418 |       expect(cardCount, `Hotel cards count should match expected list: ${expectedHotelNames.length}`).toBe(expectedHotelNames.length);
  419 |       const remainingHotelTitles: string[] = [];
  420 |       for (let i = 0; i < cardCount; i++) {
  421 |         remainingHotelTitles.push((await this.hotelCards.nth(i).locator('p').first().textContent()) ?? '');
  422 |       }
  423 | 
  424 |       for (const hotelName of expectedHotelNames) {
  425 |         const expectedName = hotelName.replace('hub ', '');
  426 |         const hotelIndex = remainingHotelTitles.findIndex((hotelTitle) => {
  427 |           const actualName = hotelTitle.replace('hub ', '');
  428 |           return actualName === expectedName
  429 |             || (expectedName.includes(')') && actualName.includes(expectedName.split(')')[0]));
  430 |         });
  431 | 
  432 |         expect(hotelIndex, `Hotel list should contain ${hotelName}`).not.toBe(-1);
  433 |         remainingHotelTitles.splice(hotelIndex, 1);
  434 |       }
  435 | 
  436 |       expect(remainingHotelTitles, 'Hotel list from UI should match expected hotel list').toHaveLength(0);
  437 |     }
  438 | 
  439 |     // Validate first card has essential elements
  440 |     const firstCard = this.hotelCards.first();
  441 |     await expect(firstCard.locator('[data-testid="DLP-hotel-thumbnail"] img'), 'Hotel card thumbnail should be displayed').toBeVisible();
  442 |     await expect(firstCard.locator('[data-testid="DLP-hotel-button"] button'), 'Hotel card button should be displayed').toBeVisible();
  443 |   }
  444 | 
  445 |   /**
  446 |    * Get the count of visible hotel cards in grid view.
  447 |    * @returns Number of hotel cards currently displayed
  448 |    */
  449 |   async getHotelCardCount(): Promise<number> {
  450 |     console.log('Getting hotel card count');
  451 |     return this.hotelCards.count();
  452 |   }
  453 | 
  454 |   /**
  455 |    * Get the count of map pins (excluding the region marker).
  456 |    * @returns Number of hotel map pins
  457 |    */
  458 |   async getMapPinCount(): Promise<number> {
  459 |     console.log('Getting map pin count');
  460 |     await this.mapMarkers.first().waitFor({ state: 'attached', timeout: browser.options.actionTimeout });
  461 |     const total = await this.mapMarkers.count();
  462 |     // Subtract 1 for the region marker
  463 |     return total - 1;
  464 |   }
  465 | 
  466 |   /**
  467 |    * Click "Show More" to expand the hotel results list.
```