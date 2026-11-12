# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts >> PI Baseline E2E - Guest PIBA Pay on Arrival Amendment >> Test Book as Guest user: 1 night, 1 room (Double), 2 adults, Flex rate, meals, donations - POA BAC card and Amend by change room type. TestCase ID: 380779.
- Location: tests/regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts:51:7

# Error details

```
TimeoutError: locator.scrollIntoViewIfNeeded: Timeout 15000ms exceeded.
Call log:
  - waiting for locator('b, p').filter({ hasText: /^\s*Standard Zimmer\s*$/ }).first().locator('xpath=ancestor::div[@data-testid="hdp_rateCard"]').locator('b[data-testid="hdp_ratePlanName"], span[data-testid="hdp_ratePlanName"]').filter({ hasText: /^\s*Flex\s*$/ }).first()

```

# Page snapshot

```yaml
- generic [active] [ref=f54e1]:
  - generic [ref=f54e3]:
    - banner [ref=f54e4]:
      - generic [ref=f54e7]:
        - link [ref=f54e10] [cursor=pointer]:
          - /url: https://www.uat.premierinn.digital/de/de/home.html
        - generic [ref=f54e12]:
          - button "Deutsch" [ref=f54e14] [cursor=pointer]:
            - img "Deutsch" [ref=f54e15]
          - generic [ref=f54e16]:
            - paragraph [ref=f54e19] [cursor=pointer]: Premier Inn entdecken
            - paragraph [ref=f54e22] [cursor=pointer]: Premier Inn Business
            - paragraph [ref=f54e24] [cursor=pointer]: Ihre Buchungen
            - generic [ref=f54e26]:
              - button "Einloggen" [ref=f54e27] [cursor=pointer]
              - button "Registrieren" [ref=f54e28] [cursor=pointer]
    - main [ref=f54e29]:
      - generic [ref=f54e31]:
        - group [ref=f54e32] [cursor=pointer]:
          - paragraph [ref=f54e38]: London Heathrow Airport (M4/J4)
          - paragraph [ref=f54e40]: 23 Nov. - 24 Nov.
          - paragraph [ref=f54e42]: 2 Erwachsene, 1 Zimmer
        - separator [ref=f54e43]
        - generic [ref=f54e44]: "Sommeraktion: 10 % Rabatt"
        - navigation "breadcrumb" [ref=f54e52]:
          - list [ref=f54e53]:
            - listitem [ref=f54e54]:
              - link "Home" [ref=f54e55] [cursor=pointer]:
                - /url: /de/de/home.html
              - text: /
            - listitem [ref=f54e56]:
              - link "Hotelverzeichnis" [ref=f54e57] [cursor=pointer]:
                - /url: /de/de/hotels.html
              - text: /
            - listitem [ref=f54e58]:
              - link "England" [ref=f54e59] [cursor=pointer]:
                - /url: /de/de/hotels/england.html
              - text: /
            - listitem [ref=f54e60]:
              - link "Greater London" [ref=f54e61] [cursor=pointer]:
                - /url: /de/de/hotels/england/greater-london.html
              - text: /
            - listitem [ref=f54e62]:
              - link "London" [ref=f54e63] [cursor=pointer]:
                - /url: /de/de/hotels/england/greater-london/london.html
              - text: /
            - listitem [ref=f54e64]:
              - generic [ref=f54e65] [cursor=pointer]: London Heathrow Airport M4j4
        - generic [ref=f54e66]:
          - generic [ref=f54e67]:
            - heading "London Heathrow Airport (M4/J4) Hotel" [level=1] [ref=f54e69]
            - generic [ref=f54e70]:
              - generic [ref=f54e71]:
                - img "ta-ratings-img" [ref=f54e72]
                - generic [ref=f54e73] [cursor=pointer]: (2515 Bewertungen)
              - img "Travelers Choice" [ref=f54e74] [cursor=pointer]
            - generic [ref=f54e75]:
              - list [ref=f54e77]:
                - listitem [ref=f54e78]:
                  - generic [ref=f54e79]: New Premier Plus rooms | New restaurant
              - paragraph [ref=f54e82]: An diesem ideal gelegenen Zwischenstopp am Flughafen Heathrow nahe der M4 ist Ihnen vor der Reise eine erholsame Nacht sicher
            - generic [ref=f54e83]: HotelausstattungAlles anzeigen
            - generic [ref=f54e84]:
              - paragraph [ref=f54e88] [cursor=pointer]: Kostenpflichtige Parkplätze vor Ort
              - paragraph [ref=f54e92] [cursor=pointer]: Frühstück
              - paragraph [ref=f54e96] [cursor=pointer]: Klimatisierte Zimmer
              - paragraph [ref=f54e100] [cursor=pointer]: Restaurant
              - paragraph [ref=f54e104] [cursor=pointer]: Kostenloses WLAN
              - paragraph [ref=f54e108] [cursor=pointer]: Behindertengerechte Zimmer
          - generic [ref=f54e110]:
            - generic [ref=f54e111]:
              - generic [ref=f54e112]:
                - generic [ref=f54e113] [cursor=pointer]
                - generic:
                  - img "overlay"
                  - paragraph: New Premier Plus rooms & news restaurant
              - generic [ref=f54e115] [cursor=pointer]
              - generic [ref=f54e117] [cursor=pointer]
            - button "Alle Fotos anzeigen" [ref=f54e118] [cursor=pointer]
        - generic [ref=f54e122]:
          - heading "Wählen Sie Ihren Tarif" [level=3] [ref=f54e123]
          - paragraph [ref=f54e124]: Wir wissen, dass sich Pläne kurzfristig ändern können. Bitte stellen Sie sicher, dass Sie einen Tarif mit entsprechender Flexibilität buchen.
          - generic [ref=f54e125]:
            - generic [ref=f54e126]:
              - generic [ref=f54e129]:
                - generic [ref=f54e130]:
                  - generic [ref=f54e131]:
                    - generic [ref=f54e132]: Premier Plus Zimmer
                    - paragraph [ref=f54e133]: Für besondere Gelegenheiten braucht es besondere Zimmer. Egal, ob es sich um eine wichtige Geschäftsreise oder einen festlichen Anlass handelt, mit einem Upgrade auf unser Premier Plus-Zimmer liegen Sie genau richtig und holen das Beste aus Ihrem Aufentha
                    - paragraph [ref=f54e135]: Details ansehen
                  - img "Room type Image" [ref=f54e137]
                - generic [ref=f54e139]:
                  - generic [ref=f54e140]:
                    - generic [ref=f54e142]:
                      - generic [ref=f54e144] [cursor=pointer]:
                        - radio "Standard - inkl. kostenloses Frühstück Zahlung sofort fällig. Keine Änderungen möglich. Genießen Sie ein kostenloses Frühstück aufs Haus." [checked] [ref=f54e145]
                        - generic [ref=f54e148]:
                          - text: Standard - inkl. kostenloses Frühstück
                          - paragraph [ref=f54e149]: Zahlung sofort fällig. Keine Änderungen möglich. Genießen Sie ein kostenloses Frühstück aufs Haus.
                      - generic [ref=f54e150]:
                        - generic [ref=f54e151]:
                          - paragraph [ref=f54e152]:
                            - generic "£100" [ref=f54e153]:
                              - generic [ref=f54e154]: £
                              - generic [ref=f54e155]: "100"
                          - paragraph [ref=f54e156]:
                            - generic "£90" [ref=f54e157]:
                              - generic [ref=f54e158]: £
                              - generic [ref=f54e159]: "90"
                        - paragraph [ref=f54e160]: Gesamtpreis
                        - generic [ref=f54e161]:
                          - paragraph [ref=f54e162]: 1 Zimmer,
                          - paragraph [ref=f54e163]: 1 Nacht
                    - separator [ref=f54e164]
                  - generic [ref=f54e167]:
                    - generic [ref=f54e169] [cursor=pointer]:
                      - radio "Semi-Flex Zahlung sofort fällig. Kostenlose Stornierung bis zu drei Tage vor dem Anreisedatum. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag." [ref=f54e170]
                      - generic [ref=f54e173]:
                        - text: Semi-Flex
                        - paragraph [ref=f54e174]: Zahlung sofort fällig. Kostenlose Stornierung bis zu drei Tage vor dem Anreisedatum. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag.
                    - generic [ref=f54e175]:
                      - paragraph [ref=f54e177]:
                        - generic "£100" [ref=f54e178]:
                          - generic [ref=f54e179]: £
                          - generic [ref=f54e180]: "100"
                      - paragraph [ref=f54e181]: Gesamtpreis
                      - generic [ref=f54e182]:
                        - paragraph [ref=f54e183]: 1 Zimmer,
                        - paragraph [ref=f54e184]: 1 Nacht
              - generic [ref=f54e187]:
                - generic [ref=f54e188]:
                  - generic [ref=f54e189]:
                    - generic [ref=f54e190]:
                      - paragraph [ref=f54e196]: Nur noch 9 Zimmer für Ihre Suche verfügbar
                      - text: Premier Plus Zimmer mit Ausblick
                    - paragraph [ref=f54e197]: Unser optimiertes Zimmerdesign mit attraktivem Ausblick, Ultimate Wi-Fi, Kaffeemaschine, Minikühlschrank, USB-Anschlüssen am Bett, Bügeleisen, verbessertem Arbeitsplatz und vielem mehr.
                    - paragraph [ref=f54e199]: Details ansehen
                  - img "Room type Image" [ref=f54e201]
                - generic [ref=f54e206]:
                  - generic [ref=f54e208] [cursor=pointer]:
                    - radio "Semi-Flex Zahlung sofort fällig. Kostenlose Stornierung bis zu drei Tage vor dem Anreisedatum. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag." [ref=f54e209]
                    - generic [ref=f54e212]:
                      - text: Semi-Flex
                      - paragraph [ref=f54e213]: Zahlung sofort fällig. Kostenlose Stornierung bis zu drei Tage vor dem Anreisedatum. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag.
                  - generic [ref=f54e214]:
                    - paragraph [ref=f54e216]:
                      - generic "£120" [ref=f54e217]:
                        - generic [ref=f54e218]: £
                        - generic [ref=f54e219]: "120"
                    - paragraph [ref=f54e220]: Gesamtpreis
                    - generic [ref=f54e221]:
                      - paragraph [ref=f54e222]: 1 Zimmer,
                      - paragraph [ref=f54e223]: 1 Nacht
              - generic [ref=f54e226]:
                - generic [ref=f54e227]:
                  - generic [ref=f54e228]:
                    - generic [ref=f54e229]: Standard Zimmer
                    - paragraph [ref=f54e230]: Unsere Doppelzimmer sind mit einem super bequemen Bett, einer Massagedusche und kostenlosem WLAN ausgestattet und bieten alles, was Sie für einen erholsamen Schlaf benötigen
                    - paragraph [ref=f54e232]: Details ansehen
                  - img "Room type Image" [ref=f54e234]
                - generic [ref=f54e236]:
                  - generic [ref=f54e237]:
                    - generic [ref=f54e239]:
                      - generic [ref=f54e241] [cursor=pointer]:
                        - radio "Standard - inkl. kostenloses Frühstück Zahlung sofort fällig. Keine Änderungen möglich. Genießen Sie ein kostenloses Frühstück aufs Haus." [ref=f54e242]
                        - generic [ref=f54e245]:
                          - text: Standard - inkl. kostenloses Frühstück
                          - paragraph [ref=f54e246]: Zahlung sofort fällig. Keine Änderungen möglich. Genießen Sie ein kostenloses Frühstück aufs Haus.
                      - generic [ref=f54e247]:
                        - generic [ref=f54e248]:
                          - paragraph [ref=f54e249]:
                            - generic "£80" [ref=f54e250]:
                              - generic [ref=f54e251]: £
                              - generic [ref=f54e252]: "80"
                          - paragraph [ref=f54e253]:
                            - generic "£72" [ref=f54e254]:
                              - generic [ref=f54e255]: £
                              - generic [ref=f54e256]: "72"
                        - paragraph [ref=f54e257]: Gesamtpreis
                        - generic [ref=f54e258]:
                          - paragraph [ref=f54e259]: 1 Zimmer,
                          - paragraph [ref=f54e260]: 1 Nacht
                    - separator [ref=f54e261]
                  - generic [ref=f54e264]:
                    - generic [ref=f54e266] [cursor=pointer]:
                      - radio "Semi-Flex Zahlung sofort fällig. Kostenlose Stornierung bis zu drei Tage vor dem Anreisedatum. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag." [ref=f54e267]
                      - generic [ref=f54e270]:
                        - text: Semi-Flex
                        - paragraph [ref=f54e271]: Zahlung sofort fällig. Kostenlose Stornierung bis zu drei Tage vor dem Anreisedatum. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag.
                    - generic [ref=f54e272]:
                      - paragraph [ref=f54e274]:
                        - generic "£80" [ref=f54e275]:
                          - generic [ref=f54e276]: £
                          - generic [ref=f54e277]: "80"
                      - paragraph [ref=f54e278]: Gesamtpreis
                      - generic [ref=f54e279]:
                        - paragraph [ref=f54e280]: 1 Zimmer,
                        - paragraph [ref=f54e281]: 1 Nacht
              - generic [ref=f54e284]:
                - generic [ref=f54e285]:
                  - generic [ref=f54e286]:
                    - generic [ref=f54e287]: Standard Zimmer mit Ausblick
                    - paragraph [ref=f54e288]: Eine attraktive Aussicht, ein super bequemes Bett, eine leistungsstarke Dusche und kostenloses WLAN – unsere Doppelzimmer bieten alles, was Sie für einen traumhaften Aufenthalt benötigen.
                    - paragraph [ref=f54e290]: Details ansehen
                  - img "Room type Image" [ref=f54e292]
                - generic [ref=f54e297]:
                  - generic [ref=f54e299] [cursor=pointer]:
                    - radio "Semi-Flex Zahlung sofort fällig. Kostenlose Stornierung bis zu drei Tage vor dem Anreisedatum. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag." [ref=f54e300]
                    - generic [ref=f54e303]:
                      - text: Semi-Flex
                      - paragraph [ref=f54e304]: Zahlung sofort fällig. Kostenlose Stornierung bis zu drei Tage vor dem Anreisedatum. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag.
                  - generic [ref=f54e305]:
                    - paragraph [ref=f54e307]:
                      - generic "£100" [ref=f54e308]:
                        - generic [ref=f54e309]: £
                        - generic [ref=f54e310]: "100"
                    - paragraph [ref=f54e311]: Gesamtpreis
                    - generic [ref=f54e312]:
                      - paragraph [ref=f54e313]: 1 Zimmer,
                      - paragraph [ref=f54e314]: 1 Nacht
            - generic [ref=f54e318]:
              - paragraph [ref=f54e319]: Doppel Zimmer
              - paragraph [ref=f54e320]: Standard - inkl. kostenloses Frühstück
              - separator [ref=f54e321]
              - generic [ref=f54e322]:
                - generic [ref=f54e323]:
                  - paragraph [ref=f54e324]: Aufenthalt
                  - paragraph [ref=f54e325]: 1 Nacht
                - generic [ref=f54e326]:
                  - paragraph [ref=f54e327]: Preis
                  - generic [ref=f54e328]:
                    - link "Tagesraten anzeigen" [ref=f54e329] [cursor=pointer]:
                      - /url: "#"
                    - paragraph [ref=f54e330]: £90
              - separator [ref=f54e331]
              - generic [ref=f54e332]:
                - paragraph [ref=f54e333]: Gesamtpreis
                - paragraph [ref=f54e334]:
                  - generic "£90" [ref=f54e335]:
                    - generic [ref=f54e336]: £
                    - generic [ref=f54e337]: "90"
              - button "Jetzt buchen" [ref=f54e338] [cursor=pointer]
              - generic [ref=f54e339]: Code eingeben
        - generic [ref=f54e344]:
          - heading "Lage" [level=3] [ref=f54e345]
          - generic [ref=f54e348]:
            - generic [ref=f54e349]:
              - paragraph [ref=f54e350]: Shepiston Lane, Middlesex,
              - paragraph [ref=f54e351]: UB3 1RW
            - generic [ref=f54e352]: Anreise mit Navigationsgerät:UB3 1LL
            - generic [ref=f54e354]:
              - paragraph [ref=f54e355]: "Informationen zur Anreise:"
              - paragraph [ref=f54e358]: "Verlassen Sie die M4 an der Ausfahrt J4 und folgen Sie der Beschilderung nach Uxbridge auf der linken Fahrspur. Halten Sie sich links und folgen Sie der Beschilderung Richtung Other Routes und Hayes. Wenn Sie an die Vorfahrtsstraße kommen, machen Sie eine Kehrtwende und nehmen Sie die vierte Ausfahrt in Richtung Haynes. Das Hotel befindet sich nach 180 Metern auf der rechten Seite. (Postleitzahl für Ihr Navigationsgerät: UB3 1LL)."
              - paragraph [ref=f54e360] [cursor=pointer]: Mehr lesen
            - generic [ref=f54e361]:
              - paragraph [ref=f54e362]: "In der Umgebung:"
              - list [ref=f54e364]:
                - listitem [ref=f54e365]:
                  - paragraph [ref=f54e366]: Dieses Hotel befindet sich in Nahverkehrszone 6 in London
                - listitem [ref=f54e367]:
                  - paragraph [ref=f54e368]: "Heathrow (Terminal 2, 3): 4 Kilometer"
                - listitem [ref=f54e369]:
                  - paragraph [ref=f54e370]: "Heathrow (Terminal 4): 7,2 Kilometer"
                - listitem [ref=f54e371]:
                  - paragraph [ref=f54e372]: "Heathrow (Terminal 5): 7,2 Kilometer"
                - listitem [ref=f54e373]:
                  - paragraph [ref=f54e374]: "Heathrow (Terminal 1-3, Bahnhof): 2,5 Kilometer"
                - listitem [ref=f54e375]:
                  - paragraph [ref=f54e376]: Historische Stätte Windsor Castle
              - paragraph [ref=f54e378] [cursor=pointer]: Mehr lesen
        - generic [ref=f54e380]:
          - heading "Parken am Premier Inn London Heathrow Airport (M4/J4) Hotel" [level=3] [ref=f54e381]
          - generic [ref=f54e382]: Wir bieten einen exklusiven Gästerabatt von £12 pro 24 Stunden auf das Parken. Suchen Sie sich einfach einen Parkplatz und zahlen Sie weniger. Der Parkplatz wird von Horizon Parking betrieben. Stellplätze können nicht vorab reserviert werden. Für größere Fahrzeuge wie Busse, Transporter oder LKWs beträgt die Parkgebühr £30. Dies muss vorab per E-Mail mit dem Hotel abgestimmt werden, da lediglich bis zu 3 große Fahrzeuge gleichzeitig untergebracht werden können. Die Verfügbarkeit kann variieren – bei Fragen wenden Sie sich bitte im Voraus direkt an das Hotel. Flughafenparkplätze sind über unsere Partner Holiday Extras zu attraktiven Preisen verfügbar.
        - generic [ref=f54e384]:
          - heading "Unsere Zimmerkategorien" [level=3] [ref=f54e385]
          - generic [ref=f54e386]:
            - tablist [ref=f54e387]:
              - tab [selected] [ref=f54e388] [cursor=pointer]:
                - heading "Doppel" [level=3] [ref=f54e390]
              - tab [ref=f54e391] [cursor=pointer]:
                - heading "Zweibett" [level=3] [ref=f54e393]
              - tab [ref=f54e394] [cursor=pointer]:
                - heading "Familie" [level=3] [ref=f54e396]
              - tab [ref=f54e397] [cursor=pointer]:
                - heading "Rollstuhlgerecht" [level=3] [ref=f54e399]
            - tabpanel "Doppel" [ref=f54e401]:
              - generic [ref=f54e403]:
                - button "Bilder ansehen" [ref=f54e407] [cursor=pointer]
                - generic [ref=f54e408]:
                  - paragraph [ref=f54e409]: Standard Doppel
                  - paragraph [ref=f54e410]: Unsere Doppelzimmer sind mit einem super bequemen Bett, einer leistungsstarken Dusche und kostenlosem WLAN ausgestattet und bieten alles, was Sie für einen erholsamen Schlaf benötigen
                  - generic [ref=f54e412]:
                    - paragraph [ref=f54e416]: Doppelbett
                    - paragraph [ref=f54e420]: Leistungsstarke Dusche
                    - paragraph [ref=f54e424]: Fernseher
                    - paragraph [ref=f54e428]: Wasserkocher mit Tee & Instantkaffee
                    - paragraph [ref=f54e432]: Funktionaler Arbeitsplatz
                    - generic [ref=f54e434]:
                      - generic [ref=f54e436] [cursor=pointer]
                      - generic [ref=f54e439] [cursor=pointer]: Komplette Zimmerausstattung anzeigen
          - button "Tarife anzeigen" [ref=f54e440] [cursor=pointer]
          - status [ref=f54e442]:
            - generic [ref=f54e447]:
              - strong [ref=f54e448]: "Hinweis:"
              - text: Ausstattung, Grundrisse, Zimmergrößen und Designs können je nach Hotel und Standort variieren.
        - generic "Restaurant" [ref=f54e450]:
          - heading "Restaurant" [level=3] [ref=f54e451]
          - img "Thyme Bar & Grill" [ref=f54e453]
          - status [ref=f54e454]:
            - generic [ref=f54e458]: Abhängig vom gewählten Hotel und Aufenthaltszeitraum bieten wir unterschiedliche Optionen für Frühstück und Abendessen an.
          - generic [ref=f54e460]:
            - tablist [ref=f54e461]:
              - tab [selected] [ref=f54e462] [cursor=pointer]:
                - heading "Frühstück" [level=3] [ref=f54e464]
              - tab [ref=f54e465] [cursor=pointer]:
                - heading "Abendessen" [level=3] [ref=f54e467]
              - tab [ref=f54e468] [cursor=pointer]:
                - heading "Meal Deal" [level=3] [ref=f54e470]
            - tabpanel "Frühstück" [ref=f54e472]:
              - generic [ref=f54e473]:
                - img "Frühstück" [ref=f54e475]
                - generic [ref=f54e476]:
                  - generic [ref=f54e477]:
                    - paragraph [ref=f54e481]: Genussvoll in den Morgen! Stellen Sie sich Ihr eigenes Frühstück zusammen und füllen Sie Ihren Teller mit frisch zubereiteten Favoriten wie Speck, Würstchen, Eiern und Rösti – leckere vegetarische und vegane Optionen inklusive – sowie kontinentalen Köstlichkeiten wie Obst, Müsli und frischem Gebäck. Und wenn ein Erwachsener ein Premier Inn-Frühstück bestellt, frühstücken bis zu zwei Kinder kostenlos mit.**
                    - text: Mehr lesen
                  - button "Frühstücksmenü" [ref=f54e483] [cursor=pointer]
        - generic [ref=f54e485]:
          - heading "Bewertungen für Premier Inn London Heathrow Airport (M4/J4) Hotel" [level=3] [ref=f54e486]
          - generic [ref=f54e487]:
            - button "Bewertungen anzeigen" [ref=f54e490] [cursor=pointer]
            - generic [ref=f54e494]:
              - paragraph [ref=f54e495]: Bewertungen von Reisenden via
              - generic [ref=f54e497]:
                - paragraph [ref=f54e500]: Lage
                - paragraph [ref=f54e504]: Schlafqualität
                - paragraph [ref=f54e508]: Zimmer
                - paragraph [ref=f54e512]: Service
                - paragraph [ref=f54e516]: Preis/Leistung
                - paragraph [ref=f54e520]: Sauberkeit
              - paragraph [ref=f54e523]: Bewertung schreiben
        - generic [ref=f54e527]:
          - heading "Hotelbeschreibung" [level=3] [ref=f54e528]
          - generic [ref=f54e529]: Abflug oder Ankunft? Wohin es Sie auch zieht, unser Hotel in Heathrow ist die ideale Ausgangsbasis. Mit den guten Verkehrsanbindungen und dem Heathrow Express Rail in wenigen Minuten Entfernung sind auch Geschäfts- und Freizeitziele in ganz London schnell zu erreichen. Falls Sie Zeit zum Entdecken haben, planen Sie ein großes Abenteuer im Legoland. Feuern Sie Ihre Mannschaft im Twickenham Stadion an. Oder holen Sie sich Adrenalin und Nervenkitzel im Thorpe Park. Freuen Sie sich dann auf ein leckeres Essen in unserem Restaurant und eine erholsame Nacht in einem komfortablen, modernen Zimmer mit großzügigem 40-Zoll-Flachbildfernseher, neu eingerichtetem Badezimmer mit extragroßem Duschkopf und superbequemem Bett.
        - generic [ref=f54e530]:
          - heading "Kontaktinformationen des Hotels" [level=3] [ref=f54e531]
          - paragraph [ref=f54e532]: Telefonnummer 0333 003 1715
          - generic [ref=f54e533]: "Hotels in Deutschland: Anrufe aus dem deutschen Festnetz werden zum Inlandstarif abgerechnet (wenn Sie sich in Deutschland befinden), aus dem Ortsnetz zum Ortstarif. Für Anrufe aus dem Mobilfunknetz und aus dem Ausland fallen ggf. weitere Gebühren an. Hotels in UK: Anrufe zu 0871-Nummern kosten 13 Pence pro Minute zuzüglich aller zusätzlichen Gebühren Ihres Telefonanbieters. Anrufe zu 0333-Nummern werden zum Inlandstarif abgerechnet (wenn Sie sich in Großbritannien aufhalten)."
    - generic [ref=f54e537]:
      - generic [ref=f54e541]:
        - generic [ref=f54e542]:
          - paragraph [ref=f54e543]: Premier Inn
          - list [ref=f54e544]:
            - listitem [ref=f54e545]:
              - link "Über uns" [ref=f54e546] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier.html
            - listitem [ref=f54e547]:
              - link "Karriere" [ref=f54e548] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/unternehmen/karriere.html
            - listitem [ref=f54e549]:
              - link "Stellenanzeigen" [ref=f54e550] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/unternehmen/karriere/stellenanzeigen.html
            - listitem [ref=f54e551]:
              - link "Presse" [ref=f54e552] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/presse.html
            - listitem [ref=f54e553]:
              - link "Nachhaltigkeit/Menschenrechte" [ref=f54e554] [cursor=pointer]:
                - /url: https://euw1-pubacc-eu-west-1.dit.premierinn.digital/content/dam/pi/websites/desktop/de/unternehmen/erklaerung-whitbread-menschenrechtsstrategie.pdf
            - listitem [ref=f54e555]:
              - link "Unternehmen (engl.)" [ref=f54e556] [cursor=pointer]:
                - /url: https://www.whitbread.co.uk/about-us/
        - generic [ref=f54e557]:
          - paragraph [ref=f54e558]: Kontakt
          - list [ref=f54e559]:
            - listitem [ref=f54e560]:
              - link "Kontaktieren Sie uns" [ref=f54e561] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/kontakt.html
            - listitem [ref=f54e562]:
              - link "Ihre Buchung bearbeiten" [ref=f54e563] [cursor=pointer]:
                - /url: https://secure2.uat.premierinn.digital/de/de/bookingmanagement.html
            - listitem [ref=f54e564]:
              - link "Kontakt für Firmenkunden" [ref=f54e565] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/business/kontakt.html
            - listitem [ref=f54e566]:
              - link "Gruppenbuchungen" [ref=f54e567] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/gruppen.html
            - listitem [ref=f54e568]:
              - link "FAQs" [ref=f54e569] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/fragen.html
            - listitem [ref=f54e570]:
              - link "Feedback" [ref=f54e571] [cursor=pointer]:
                - /url: mailto:feedback-germany@premierinn.com?subject=Feedback%20zu%20Premier%20Inn%20Deutschland%20/%20Feedback%20for%20Premier%20Inn%20Germany&body=Dieses%20E-Mail-Feedback-Formular%20ist%20ausschlie%C3%9Flich%20f%C3%BCr%20Premier%20Inn%20Deutschland%20vorgesehen.%20Wir%20antworten%20innerhalb%20von%203%20-%205%20Werktagen.%20Bitte%20%C3%BCbermitteln%20Sie%20uns%20die%20nachfolgenden%20Angaben%20%E2%80%93%20ohne%20diese%20k%C3%B6nnen%20wir%20Ihr%20Feedback%20aus%20Datenschutzgr%C3%BCnden%20leider%20nicht%20bearbeiten%20oder%20weiterleiten.%0D%0DPlease%20note:%20This%20feedback%20form%20is%20exclusively%20for%20Premier%20Inn%20Germany.%20We%E2%80%99d%20love%20to%20hear%20about%20your%20recent%20experience%20staying%20in%20one%20of%20our%20Premier%20Inn%20hotels%20in%20Germany.%20Please%20provide%20us%20with%20the%20following%20information%20%E2%80%93%20we%E2%80%99ll%20need%20this%20for%20data%20protection%20reasons%20in%20order%20to%20process%20or%20forward%20on%20your%20feedback.%20We%E2%80%99ll%20aim%20to%20respond%20within%20three%20to%20five%20working%20days.%0D%0DName%20/%20Name:%20%0DTelefonnummer%20/%20Telephone%20number:%20%0DPostleitzahl%20/%20Post%20code:%20%0DHotelname%20/%20Hotel%20name:%20%0DBuchungsnummer%20/%20Booking%20number:%20%0DAufenthaltsdatum%20/%20Date%20of%20stay:%20%0DIhre%20Nachricht%20an%20uns%20/%20Your%20feedback:%20%0D.html
        - generic [ref=f54e572]:
          - paragraph [ref=f54e573]: Rechtliches
          - list [ref=f54e574]:
            - listitem [ref=f54e575]:
              - link "Nutzungsbedingungen" [ref=f54e576] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/bedingungen/nutzungsbedingungen.html
            - listitem [ref=f54e577]:
              - link "Unsere AGBs" [ref=f54e578] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/bedingungen/allgemeine-geschaeftsbedingungen-deutschland.html
            - listitem [ref=f54e579]:
              - link "Cookies" [ref=f54e580] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/bedingungen/cookies.html
            - listitem [ref=f54e581]:
              - link "Datenschutz" [ref=f54e582] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/bedingungen/datenschutz.html
            - listitem [ref=f54e583]:
              - link "Impressum" [ref=f54e584] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/impressum.html
            - listitem [ref=f54e585]:
              - link "Lieferkettensorgfaltspflichten" [ref=f54e586] [cursor=pointer]:
                - /url: https://euw1-pubacc-eu-west-1.dit.premierinn.digital/content/dam/pi/websites/desktop/de/unternehmen/beschwerdeverfahren-lieferkettengesetz.pdf
        - generic [ref=f54e587]:
          - paragraph [ref=f54e588]: Hotels
          - list [ref=f54e589]:
            - listitem [ref=f54e590]:
              - link "Unsere Zimmer" [ref=f54e591] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/schlaf/zimmer.html
            - listitem [ref=f54e592]:
              - link "Unsere Betten" [ref=f54e593] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/schlaf/bett.html
            - listitem [ref=f54e594]:
              - link "Barrierefreiheit" [ref=f54e595] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/bedingungen/barrierefreiheit.html
            - listitem [ref=f54e596]:
              - link "Frühstück & Bar" [ref=f54e597] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/essen.html
            - listitem [ref=f54e598]:
              - link "WLAN" [ref=f54e599] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/wlan.html
            - listitem [ref=f54e600]:
              - link "Meetingräume" [ref=f54e601] [cursor=pointer]:
                - /url: https://www.premiermeetings.co.uk/
        - generic [ref=f54e602]:
          - paragraph [ref=f54e603]: Gut zu wissen
          - list [ref=f54e604]:
            - listitem [ref=f54e605]:
              - link "Gute-Nacht-Garantie" [ref=f54e606] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/schlaf/gute-nacht-garantie.html
            - listitem [ref=f54e607]:
              - link "Mehr für Ihr Geld" [ref=f54e608] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/preis-leistung.html
            - listitem [ref=f54e609]:
              - link "Vorteile für Familien" [ref=f54e610] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/familie.html
            - listitem [ref=f54e611]:
              - link "So können Sie bezahlen" [ref=f54e612] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/fragen/bezahlung.html
            - listitem [ref=f54e613]:
              - link "Buchung ändern/stornieren" [ref=f54e614] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/fragen/buchung-aendern-stornieren.html
            - listitem [ref=f54e615]:
              - link "Expansion in Deutschland" [ref=f54e616] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/unternehmen/expansion-in-deutschland.html
        - generic [ref=f54e617]:
          - paragraph [ref=f54e618]: Anderes
          - list [ref=f54e619]:
            - listitem [ref=f54e620]:
              - link "Unsere flexiblen Tarife" [ref=f54e621] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/flexibel-buchen.html
            - listitem [ref=f54e622]:
              - link "City Tax" [ref=f54e623] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/fragen/city-tax.html
            - listitem [ref=f54e624]:
              - link "Premier Plus-Zimmer" [ref=f54e625] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/schlaf/premier-plus-zimmer.html
            - listitem [ref=f54e626]:
              - link "Check-in & Check-out" [ref=f54e627] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/fragen/checkin-checkout.html
            - listitem [ref=f54e628]:
              - link "Anreise & Parken" [ref=f54e629] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/fragen/anreise-parken.html
            - listitem [ref=f54e630]:
              - link "Sitemap" [ref=f54e631] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/sitemap.html
      - generic [ref=f54e634]:
        - paragraph [ref=f54e635]: Die wichtigsten Premier Inn Hotels
        - generic [ref=f54e636]:
          - list [ref=f54e637]:
            - listitem [ref=f54e638]:
              - link "Hotels Berlin" [ref=f54e639] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/berlin/berlin.html
            - listitem [ref=f54e640]:
              - link "Hotels Braunschweig" [ref=f54e641] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/niedersachsen/braunschweig.html
            - listitem [ref=f54e642]:
              - link "Hotels Darmstadt" [ref=f54e643] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/hessen/darmstadt.html
            - listitem [ref=f54e644]:
              - link "Hotels Dresden" [ref=f54e645] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/sachsen/dresden.html
            - listitem [ref=f54e646]:
              - link "Hotels Düsseldorf" [ref=f54e647] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/nordrhein-westfalen/duesseldorf.html
            - listitem [ref=f54e648]:
              - link "Hotels Essen" [ref=f54e649] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/nordrhein-westfalen/essen.html
            - listitem [ref=f54e650]:
              - link "Hotels Frankfurt" [ref=f54e651] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/hessen/frankfurt.html
          - list [ref=f54e652]:
            - listitem [ref=f54e653]:
              - link "Hotels Freiburg" [ref=f54e654] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/freiburg.html
            - listitem [ref=f54e655]:
              - link "Hotels Hamburg" [ref=f54e656] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/hamburg/hamburg.html
            - listitem [ref=f54e657]:
              - link "Hotels Hannover" [ref=f54e658] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/niedersachsen/hannover.html
            - listitem [ref=f54e659]:
              - link "Hotels Heidelberg" [ref=f54e660] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/heidelberg.html
            - listitem [ref=f54e661]:
              - link "Hotels Heilbronn" [ref=f54e662] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/heilbronn.html
            - listitem [ref=f54e663]:
              - link "Hotels Karlsruhe" [ref=f54e664] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/karlsruhe.html
            - listitem [ref=f54e665]:
              - link "Hotels Köln" [ref=f54e666] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/nordrhein-westfalen/koeln.html
          - list [ref=f54e667]:
            - listitem [ref=f54e668]:
              - link "Hotels Leipzig" [ref=f54e669] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/sachsen/leipzig.html
            - listitem [ref=f54e670]:
              - link "Hotels Lindau" [ref=f54e671] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/lindau.html
            - listitem [ref=f54e672]:
              - link "Hotels Lübeck" [ref=f54e673] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/schleswig-holstein/luebeck.html
            - listitem [ref=f54e674]:
              - link "Hotels Mannheim" [ref=f54e675] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/mannheim.html
            - listitem [ref=f54e676]:
              - link "Hotels München" [ref=f54e677] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/muenchen.html
            - listitem [ref=f54e678]:
              - link "Hotels Nürnberg" [ref=f54e679] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/nuernberg.html
            - listitem [ref=f54e680]:
              - link "Hotels Passau" [ref=f54e681] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/passau.html
          - list [ref=f54e682]:
            - listitem [ref=f54e683]:
              - link "Hotels Regensburg" [ref=f54e684] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/regensburg.html
            - listitem [ref=f54e685]:
              - link "Hotels Saarbrücken" [ref=f54e686] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/saarland/saarbruecken.html
            - listitem [ref=f54e687]:
              - link "Hotels Stuttgart" [ref=f54e688] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/stuttgart.html
            - listitem [ref=f54e689]:
              - link "Hotels Wiesbaden" [ref=f54e690] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/hessen/wiesbaden.html
            - listitem [ref=f54e691]:
              - link "Hotels Wolfsburg" [ref=f54e692] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/niedersachsen/wolfsburg.html
            - listitem [ref=f54e693]:
              - link "Hotels Wuppertal" [ref=f54e694] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/nordrhein-westfalen/wuppertal.html
            - listitem [ref=f54e695]:
              - link "Hotels London" [ref=f54e696] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/england/greater-london/london.html
          - list [ref=f54e697]:
            - listitem [ref=f54e698]:
              - link "Hotels Manchester" [ref=f54e699] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/england/greater-manchester/manchester.html
            - listitem [ref=f54e700]:
              - link "Hotels Edinburgh" [ref=f54e701] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/schottland/lothian/edinburgh.html
            - listitem [ref=f54e702]:
              - link "Hotels Dublin" [ref=f54e703] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/republik-irland/dublin.html
            - listitem [ref=f54e704]:
              - link "Flughafenhotels" [ref=f54e705] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/standorte/flughafenhotels.html
            - listitem [ref=f54e706]:
              - link "Unsere besten Hotels" [ref=f54e707] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels/beste-hotels.html
            - listitem [ref=f54e708]:
              - link "Neue Hotels" [ref=f54e709] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/standorte/neue-hotels.html
            - listitem [ref=f54e710]:
              - link "Alle Hotels im Überblick" [ref=f54e711] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/hotels.html
          - list [ref=f54e712]:
            - listitem [ref=f54e713]:
              - link "Reiseführer Berlin" [ref=f54e714] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/reiseziele/berlin.html
            - listitem [ref=f54e715]:
              - link "Reiseführer Dresden" [ref=f54e716] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/reiseziele/dresden.html
            - listitem [ref=f54e717]:
              - link "Reiseführer Düsseldorf" [ref=f54e718] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/reiseziele/duesseldorf.html
            - listitem [ref=f54e719]:
              - link "Reiseführer Hamburg" [ref=f54e720] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/reiseziele/hamburg.html
            - listitem [ref=f54e721]:
              - link "Reiseführer Köln" [ref=f54e722] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/reiseziele/koeln.html
            - listitem [ref=f54e723]:
              - link "Reiseführer München" [ref=f54e724] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/reiseziele/muenchen.html
            - listitem [ref=f54e725]:
              - link "Alle Reiseführer" [ref=f54e726] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/de/de/reiseziele/reisefuehrer.html
      - generic [ref=f54e727]:
        - generic [ref=f54e728]: © 2026 Premier Inn
        - generic [ref=f54e729]:
          - link [ref=f54e730] [cursor=pointer]:
            - /url: https://www.facebook.com/PremierInnDeutschland
          - link [ref=f54e732] [cursor=pointer]:
            - /url: https://www.youtube.com/@premierinndeutschland
          - link [ref=f54e734] [cursor=pointer]:
            - /url: https://www.instagram.com/premierinn.de
  - alert [ref=f54e736]
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
  10  |  * extensive premium/standard room-class cross-referencing against `HotelAvailability`/
  11  |  * `RoomClassConfig`/`PmsRoomType` API responses is dropped in favour of the simple DOM-scoped
  12  |  * rate/room lookups already used by `HotelDetailsPage` for its own flex-rate selection - this
  13  |  * component adds the remaining named-rate-button and per-room rate lookups that page doesn't
  14  |  * expose yet).
  15  |  */
  16  | export class ChooseYourRateComponent {
  17  |   private readonly page: Page = global.page;
  18  | 
  19  |   // ######## UI elements/properties ########
  20  | 
  21  |   readonly ratesContainer: Locator = this.page.locator('section[data-testid="hdp_rateSelector-Section"]');
  22  |   readonly chooseYourRateTitleLabel: Locator = this.page.locator('h3[data-testid="hdp_rateSelectorTitle"]');
  23  |   readonly descriptionLabel: Locator = this.page.locator('p[data-testid="hdp_rateSelectorDescription"]');
  24  |   readonly rateItemCardList: Locator = this.page.locator('div[data-testid="hdp_rateItemCard"]');
  25  |   readonly ratesTitleList: Locator = this.page.locator(
  26  |     'div[data-testid="hdp_rateItemCard"] b[data-testid="hdp_ratePlanName"], div[data-testid="hdp_rateItemCard"] span[data-testid="hdp_ratePlanName"]'
  27  |   );
  28  |   readonly ratesPriceList: Locator = this.page.locator('div[data-testid="hdp_rateItemCard"] p[data-testid="hdp_rateItemTotalPrice"]');
  29  |   readonly accessibleRoomNotification: Locator = this.page.locator('div[data-testid="hdp_accessibleRoomNotification"]');
  30  |   readonly ratesContainersList: Locator = this.page.locator('div[data-testid="hdp_rateCard"]');
  31  |   readonly showMoreRatesLabel: Locator = this.page.locator('p[data-testid="hdp_roomTypeShowMoreRatesLink"]');
  32  |   readonly employeeOfferLabel: Locator = this.page.locator('p[data-testid="hdp_employeeOffer"]');
  33  |   readonly selectedRateLabel: Locator = this.page
  34  |     .locator('div[data-testid="hdp_rateItemCard"] label[data-checked]')
  35  |     .locator('xpath=.//*[self::b or self::span][@data-testid="hdp_ratePlanName"]')
  36  |     .first();
  37  |   readonly selectedRoomLabel: Locator = this.page
  38  |     .locator('div[data-testid="hdp_rateCard"]:has(label[data-checked])')
  39  |     .locator('xpath=.//*[self::b or self::p][@data-testid="hdp_roomTypeTitle"]')
  40  |     .first();
  41  | 
  42  |   /** The rate-plan-name button for a given rate (e.g. `HotelRates.PI_FLEX`). */
  43  |   rateButtonByName(rateName: string): Locator {
  44  |     return this.page
  45  |       .locator('b[data-testid="hdp_ratePlanName"], span[data-testid="hdp_ratePlanName"]', { hasText: rateName })
  46  |       .first();
  47  |   }
  48  | 
  49  |   /** The room-type title label for a given room name. */
  50  |   roomNameLabel(roomName: string): Locator {
  51  |     const escapedRoomName = roomName.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  52  |     return this.page.locator('b, p').filter({ hasText: new RegExp(`^\\s*${escapedRoomName}\\s*$`) }).first();
  53  |   }
  54  | 
  55  |   /** The rate-plan-name button for a specific rate within a specific room's rate card. */
  56  |   async rateButtonForSpecificRoom({
  57  |     roomName,
  58  |     rateName,
  59  |   }: { roomName?: string; rateName?: string } = {}): Promise<Locator> {
  60  |     const selectedRoomName = roomName ?? await PmsRoomTypes.PI_STANDARD_ROOM.name.name;
  61  |     const selectedRateName = rateName ?? HotelRates.PI_FLEX.name;
  62  |     const roomCard = this.roomNameLabel(selectedRoomName).locator('xpath=ancestor::div[@data-testid="hdp_rateCard"]');
  63  |     const escapedRateName = selectedRateName.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  64  |     await roomCard.waitFor({ state: 'visible', timeout: 60000 });
  65  |     return roomCard
  66  |       .locator('b[data-testid="hdp_ratePlanName"], span[data-testid="hdp_ratePlanName"]')
  67  |       .filter({ hasText: new RegExp(`^\\s*${escapedRateName}\\s*$`) })
  68  |       .first();
  69  |   }
  70  | 
  71  |   /** Rate section components for each rate card on the page. */
  72  |   async rateSectionsList(): Promise<RateSectionComponent[]> {
  73  |     const count = await this.ratesContainersList.count();
  74  |     return Array.from({ length: count }, (_, index) => new RateSectionComponent(this.ratesContainersList.nth(index)));
  75  |   }
  76  | 
  77  |   /** Return the selected rate-plan name from the selected rate card. */
  78  |   async getSelectedRateName(): Promise<string> {
  79  |     console.log('Getting selected rate name');
  80  |     await this.selectedRateLabel.waitFor({ state: 'visible', timeout: 40000 });
  81  |     return (await this.selectedRateLabel.innerText()).trim();
  82  |   }
  83  | 
  84  |   /** Return the selected room name from the selected rate card. */
  85  |   async getSelectedRoomName(): Promise<string> {
  86  |     console.log('Getting selected room name');
  87  |     await this.selectedRoomLabel.waitFor({ state: 'visible', timeout: 40000 });
  88  |     return (await this.selectedRoomLabel.innerText()).trim();
  89  |   }
  90  | 
  91  |   // ######## UI actions/navigation ########
  92  | 
  93  |   /** Click a named rate (e.g. Flex/Standard/Advance) irrespective of room. */
  94  |   async clickRateByName(rateName: string): Promise<void> {
  95  |     console.log(`Selecting rate: ${rateName}`);
  96  |     const button = this.rateButtonByName(rateName);
  97  |     await button.scrollIntoViewIfNeeded();
  98  |     await button.click();
  99  |   }
  100 | 
  101 |   /** Click a specific rate for a specific room (e.g. Flex on the Double room). */
  102 |   async clickSpecificRateForSpecificRoom({
  103 |     roomName,
  104 |     rateName,
  105 |   }: { roomName?: string; rateName?: string } = {}): Promise<void> {
  106 |     const selectedRoomName = roomName ?? await PmsRoomTypes.PI_STANDARD_ROOM.name.name;
  107 |     const selectedRateName = rateName ?? HotelRates.PI_FLEX.name;
  108 |     console.log(`Selecting rate ${selectedRateName} for room ${selectedRoomName}`);
  109 |     const button = await this.rateButtonForSpecificRoom({ roomName: selectedRoomName, rateName: selectedRateName });
> 110 |     await button.scrollIntoViewIfNeeded();
      |                  ^ TimeoutError: locator.scrollIntoViewIfNeeded: Timeout 15000ms exceeded.
  111 |     await button.click();
  112 |   }
  113 | 
  114 |   /** Select the PI Flex rate option. */
  115 |   async clickFlexRate(): Promise<void> {
  116 |     await this.clickRateByName(await HotelRates.PI_FLEX.name);
  117 |   }
  118 | 
  119 |   // ######## UI validations ########
  120 | 
  121 |   /** Validate the rate item cards are displayed. */
  122 |   async validatePage(): Promise<void> {
  123 |     console.log('Validate choose your rate section');
  124 |     await expect(this.rateItemCardList.first(), 'Rate item cards').toBeVisible();
  125 |   }
  126 | 
  127 |   /** Validate at least one Flex-family rate (Flex/Semi-Flex/Advance) rate button is present. */
  128 |   async validateFlexFamilyRatesPresent(): Promise<void> {
  129 |     const flexRateNames = [HotelRates.PI_FLEX.name, HotelRates.PI_SEMI_FLEX.name, HotelRates.PI_ADVANCE.name];
  130 |     let found = false;
  131 |     for (const rateName of flexRateNames) {
  132 |       if (await this.rateButtonByName(await rateName).isVisible().catch(() => false)) {
  133 |         found = true;
  134 |         break;
  135 |       }
  136 |     }
  137 |     expect(found, 'At least one PI Flex-family rate should be visible').toBe(true);
  138 |   }
  139 | }
  140 | 
```