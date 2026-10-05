# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts >> PI Baseline E2E - Guest PIBA Pay on Arrival Amendment >> Test Book as Guest user: 1 night, 1 room (Double), 2 adults, Flex rate, meals, donations - POA BAC card and Amend by change room type. TestCase ID: 380779.
- Location: qa/tests/regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts:51:8

# Error details

```
Error: Price breakdown should show 1 rows per room

expect(locator).toHaveCount(expected) failed

Locator:  locator('[data-testid="hdp_basketBreakdownRow"]:visible, [data-testid="hdp_mobileBasketBreakdownRow"]:visible')
Expected: 1
Received: 0
Timeout:  10000ms

Call log:
  - Price breakdown should show 1 rows per room with timeout 10000ms
  - waiting for locator('[data-testid="hdp_basketBreakdownRow"]:visible, [data-testid="hdp_mobileBasketBreakdownRow"]:visible')
    5 × locator resolved to 2 elements
      - unexpected value "2"
    - waiting for "https://www.uat.premierinn.digital/de/de/hotels/england/greater-london/london/london-heathrow-airport-m4j4.html?ARRdd=29&ARRmm=09&ARRyyyy=2026&NIGHTS=1&ROOMS=1&ADULT1=2&CHILD1=0&COT1=0&INTTYP1=DB" navigation to finish...
    - navigated to "https://www.uat.premierinn.digital/de/de/hotels/england/greater-london/london/london-heathrow-airport-m4j4.html?ARRdd=29&ARRmm=09&ARRyyyy=2026&NIGHTS=1&ROOMS=1&ADULT1=2&CHILD1=0&COT1=0&INTTYP1=DB"
    14 × locator resolved to 0 elements
       - unexpected value "0"

```

# Page snapshot

```yaml
- generic [active] [ref=f49e1]:
  - generic [ref=f49e3]:
    - banner [ref=f49e4]:
      - generic [ref=f49e7]:
        - link [ref=f49e10] [cursor=pointer]:
          - /url: https://www.uat.premierinn.digital/de/de/home.html
        - generic [ref=f49e12]:
          - img "Deutsch" [ref=f49e15] [cursor=pointer]
          - generic [ref=f49e16]:
            - paragraph [ref=f49e19] [cursor=pointer]: Premier Inn entdecken
            - paragraph [ref=f49e22] [cursor=pointer]: Premier Inn Business
            - paragraph [ref=f49e24] [cursor=pointer]: Ihre Buchungen
            - generic [ref=f49e26]:
              - button "Einloggen" [ref=f49e27] [cursor=pointer]
              - button "Registrieren" [ref=f49e28] [cursor=pointer]
    - main [ref=f49e29]:
      - generic [ref=f49e31]:
        - group [ref=f49e32] [cursor=pointer]:
          - paragraph [ref=f49e38]: London Heathrow Airport (M4/J4)
          - paragraph [ref=f49e40]: 29 Sep. - 30 Sep.
          - paragraph [ref=f49e42]: 2 Erwachsene, 1 Zimmer
        - separator [ref=f49e43]
        - navigation "breadcrumb" [ref=f49e45]:
          - list [ref=f49e46]:
            - listitem [ref=f49e47]:
              - link "Home" [ref=f49e48] [cursor=pointer]:
                - /url: /de/de/home.html
              - text: /
            - listitem [ref=f49e49]:
              - link "Hotelverzeichnis" [ref=f49e50] [cursor=pointer]:
                - /url: /de/de/hotels.html
              - text: /
            - listitem [ref=f49e51]:
              - link "England" [ref=f49e52] [cursor=pointer]:
                - /url: /de/de/hotels/england.html
              - text: /
            - listitem [ref=f49e53]:
              - link "Greater London" [ref=f49e54] [cursor=pointer]:
                - /url: /de/de/hotels/england/greater-london.html
              - text: /
            - listitem [ref=f49e55]:
              - link "London" [ref=f49e56] [cursor=pointer]:
                - /url: /de/de/hotels/england/greater-london/london.html
              - text: /
            - listitem [ref=f49e57]:
              - generic [ref=f49e58] [cursor=pointer]: London Heathrow Airport M4j4
        - generic [ref=f49e59]:
          - generic [ref=f49e60]:
            - heading "London Heathrow Airport (M4/J4) Hotel" [level=1] [ref=f49e62]
            - generic [ref=f49e63]:
              - generic [ref=f49e64]:
                - img "ta-ratings-img" [ref=f49e65]
                - generic [ref=f49e66] [cursor=pointer]: (2506 Bewertungen)
              - img "Travelers Choice" [ref=f49e67] [cursor=pointer]
            - generic [ref=f49e68]:
              - list [ref=f49e70]:
                - listitem [ref=f49e71]:
                  - generic [ref=f49e72]: New Premier Plus rooms | New restaurant
              - paragraph [ref=f49e75]: An diesem ideal gelegenen Zwischenstopp am Flughafen Heathrow nahe der M4 ist Ihnen vor der Reise eine erholsame Nacht sicher
            - generic [ref=f49e76]: HotelausstattungAlles anzeigen
            - generic [ref=f49e77]:
              - paragraph [ref=f49e81] [cursor=pointer]: Kostenpflichtige Parkplätze vor Ort
              - paragraph [ref=f49e85] [cursor=pointer]: Frühstück
              - paragraph [ref=f49e89] [cursor=pointer]: Klimatisierte Zimmer
              - paragraph [ref=f49e93] [cursor=pointer]: Restaurant
              - paragraph [ref=f49e97] [cursor=pointer]: Kostenloses WLAN
              - paragraph [ref=f49e101] [cursor=pointer]: Behindertengerechte Zimmer
          - generic [ref=f49e103]:
            - generic [ref=f49e104]:
              - generic [ref=f49e105]:
                - generic [ref=f49e106] [cursor=pointer]
                - generic:
                  - img "overlay"
                  - paragraph: New Premier Plus rooms & news restaurant
              - generic [ref=f49e108] [cursor=pointer]
              - generic [ref=f49e110] [cursor=pointer]
            - button "Alle Fotos anzeigen" [ref=f49e111] [cursor=pointer]
        - generic [ref=f49e115]:
          - heading "Wählen Sie Ihren Tarif" [level=3] [ref=f49e116]
          - paragraph [ref=f49e117]: Wir wissen, dass sich Pläne kurzfristig ändern können. Bitte stellen Sie sicher, dass Sie einen Tarif mit entsprechender Flexibilität buchen.
          - generic [ref=f49e118]:
            - generic [ref=f49e119]:
              - generic [ref=f49e122]:
                - generic [ref=f49e123]:
                  - generic [ref=f49e124]:
                    - generic [ref=f49e125]: Premier Plus Zimmer
                    - paragraph [ref=f49e126]: Für besondere Gelegenheiten braucht es besondere Zimmer. Egal, ob es sich um eine wichtige Geschäftsreise oder einen festlichen Anlass handelt, mit einem Upgrade auf unser Premier Plus-Zimmer liegen Sie genau richtig und holen das Beste aus Ihrem Aufentha
                  - img "Room type Image" [ref=f49e128]
                - generic [ref=f49e130]:
                  - generic [ref=f49e131]:
                    - generic [ref=f49e133]:
                      - generic [ref=f49e135] [cursor=pointer]:
                        - radio "Flex Zahlung sofort oder bei Anreise. Kostenlose Stornierung und Änderung bis 13:00 Uhr am Anreisetag." [checked] [ref=f49e136]
                        - generic [ref=f49e139]:
                          - text: Flex
                          - paragraph [ref=f49e140]: Zahlung sofort oder bei Anreise. Kostenlose Stornierung und Änderung bis 13:00 Uhr am Anreisetag.
                      - generic [ref=f49e141]:
                        - paragraph [ref=f49e143]:
                          - generic "£90" [ref=f49e144]:
                            - generic [ref=f49e145]: £
                            - generic [ref=f49e146]: "90"
                        - paragraph [ref=f49e147]: Gesamtpreis
                        - generic [ref=f49e148]:
                          - paragraph [ref=f49e149]: 1 Zimmer,
                          - paragraph [ref=f49e150]: 1 Nacht
                    - separator [ref=f49e151]
                  - generic [ref=f49e152]:
                    - generic [ref=f49e154]:
                      - generic [ref=f49e156] [cursor=pointer]:
                        - radio "Semi-Flex Zahlung sofort fällig. Kostenlose Stornierung bis zu drei Tage vor dem Anreisedatum. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag." [ref=f49e157]
                        - generic [ref=f49e160]:
                          - text: Semi-Flex
                          - paragraph [ref=f49e161]: Zahlung sofort fällig. Kostenlose Stornierung bis zu drei Tage vor dem Anreisedatum. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag.
                      - generic [ref=f49e162]:
                        - paragraph [ref=f49e164]:
                          - generic "£77" [ref=f49e165]:
                            - generic [ref=f49e166]: £
                            - generic [ref=f49e167]: "77"
                        - paragraph [ref=f49e168]: Gesamtpreis
                        - generic [ref=f49e169]:
                          - paragraph [ref=f49e170]: 1 Zimmer,
                          - paragraph [ref=f49e171]: 1 Nacht
                    - separator [ref=f49e172]
                  - generic [ref=f49e175]:
                    - generic [ref=f49e177] [cursor=pointer]:
                      - radio "Standard Zahlung sofort fällig. Keine Stornierung möglich. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag." [ref=f49e178]
                      - generic [ref=f49e181]:
                        - text: Standard
                        - paragraph [ref=f49e182]: Zahlung sofort fällig. Keine Stornierung möglich. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag.
                    - generic [ref=f49e183]:
                      - paragraph [ref=f49e185]:
                        - generic "£68" [ref=f49e186]:
                          - generic [ref=f49e187]: £
                          - generic [ref=f49e188]: "68"
                      - paragraph [ref=f49e189]: Gesamtpreis
                      - generic [ref=f49e190]:
                        - paragraph [ref=f49e191]: 1 Zimmer,
                        - paragraph [ref=f49e192]: 1 Nacht
              - generic [ref=f49e195]:
                - generic [ref=f49e196]:
                  - generic [ref=f49e197]:
                    - generic [ref=f49e198]: Premier Plus Zimmer mit Ausblick
                    - paragraph [ref=f49e199]: Unser optimiertes Zimmerdesign mit attraktivem Ausblick, Ultimate Wi-Fi, Kaffeemaschine, Minikühlschrank, USB-Anschlüssen am Bett, Bügeleisen, verbessertem Arbeitsplatz und vielem mehr.
                  - img "Room type Image" [ref=f49e201]
                - generic [ref=f49e203]:
                  - generic [ref=f49e204]:
                    - generic [ref=f49e206]:
                      - generic [ref=f49e208] [cursor=pointer]:
                        - radio "Flex Zahlung sofort oder bei Anreise. Kostenlose Stornierung und Änderung bis 13:00 Uhr am Anreisetag." [ref=f49e209]
                        - generic [ref=f49e212]:
                          - text: Flex
                          - paragraph [ref=f49e213]: Zahlung sofort oder bei Anreise. Kostenlose Stornierung und Änderung bis 13:00 Uhr am Anreisetag.
                      - generic [ref=f49e214]:
                        - paragraph [ref=f49e216]:
                          - generic "£90" [ref=f49e217]:
                            - generic [ref=f49e218]: £
                            - generic [ref=f49e219]: "90"
                        - paragraph [ref=f49e220]: Gesamtpreis
                        - generic [ref=f49e221]:
                          - paragraph [ref=f49e222]: 1 Zimmer,
                          - paragraph [ref=f49e223]: 1 Nacht
                    - separator [ref=f49e224]
                  - generic [ref=f49e225]:
                    - generic [ref=f49e227]:
                      - generic [ref=f49e229] [cursor=pointer]:
                        - radio "Semi-Flex Zahlung sofort fällig. Kostenlose Stornierung bis zu drei Tage vor dem Anreisedatum. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag." [ref=f49e230]
                        - generic [ref=f49e233]:
                          - text: Semi-Flex
                          - paragraph [ref=f49e234]: Zahlung sofort fällig. Kostenlose Stornierung bis zu drei Tage vor dem Anreisedatum. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag.
                      - generic [ref=f49e235]:
                        - paragraph [ref=f49e237]:
                          - generic "£77" [ref=f49e238]:
                            - generic [ref=f49e239]: £
                            - generic [ref=f49e240]: "77"
                        - paragraph [ref=f49e241]: Gesamtpreis
                        - generic [ref=f49e242]:
                          - paragraph [ref=f49e243]: 1 Zimmer,
                          - paragraph [ref=f49e244]: 1 Nacht
                    - separator [ref=f49e245]
                  - generic [ref=f49e248]:
                    - generic [ref=f49e250] [cursor=pointer]:
                      - radio "Standard Zahlung sofort fällig. Keine Stornierung möglich. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag." [ref=f49e251]
                      - generic [ref=f49e254]:
                        - text: Standard
                        - paragraph [ref=f49e255]: Zahlung sofort fällig. Keine Stornierung möglich. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag.
                    - generic [ref=f49e256]:
                      - paragraph [ref=f49e258]:
                        - generic "£68" [ref=f49e259]:
                          - generic [ref=f49e260]: £
                          - generic [ref=f49e261]: "68"
                      - paragraph [ref=f49e262]: Gesamtpreis
                      - generic [ref=f49e263]:
                        - paragraph [ref=f49e264]: 1 Zimmer,
                        - paragraph [ref=f49e265]: 1 Nacht
              - generic [ref=f49e268]:
                - generic [ref=f49e269]:
                  - generic [ref=f49e270]:
                    - generic [ref=f49e271]: Standard Zimmer
                    - paragraph [ref=f49e272]: Unsere Doppelzimmer sind mit einem super bequemen Bett, einer Massagedusche und kostenlosem WLAN ausgestattet und bieten alles, was Sie für einen erholsamen Schlaf benötigen
                    - paragraph [ref=f49e275]: Details ansehen
                  - img "Room type Image" [ref=f49e277]
                - generic [ref=f49e279]:
                  - generic [ref=f49e280]:
                    - generic [ref=f49e282]:
                      - generic [ref=f49e284] [cursor=pointer]:
                        - radio "Flex Zahlung sofort oder bei Anreise. Kostenlose Stornierung und Änderung bis 13:00 Uhr am Anreisetag." [ref=f49e285]
                        - generic [ref=f49e288]:
                          - text: Flex
                          - paragraph [ref=f49e289]: Zahlung sofort oder bei Anreise. Kostenlose Stornierung und Änderung bis 13:00 Uhr am Anreisetag.
                      - generic [ref=f49e290]:
                        - paragraph [ref=f49e292]:
                          - generic "£90" [ref=f49e293]:
                            - generic [ref=f49e294]: £
                            - generic [ref=f49e295]: "90"
                        - paragraph [ref=f49e296]: Gesamtpreis
                        - generic [ref=f49e297]:
                          - paragraph [ref=f49e298]: 1 Zimmer,
                          - paragraph [ref=f49e299]: 1 Nacht
                    - separator [ref=f49e300]
                  - generic [ref=f49e301]:
                    - generic [ref=f49e303]:
                      - generic [ref=f49e305] [cursor=pointer]:
                        - radio "Semi-Flex Zahlung sofort fällig. Kostenlose Stornierung bis zu drei Tage vor dem Anreisedatum. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag." [ref=f49e306]
                        - generic [ref=f49e309]:
                          - text: Semi-Flex
                          - paragraph [ref=f49e310]: Zahlung sofort fällig. Kostenlose Stornierung bis zu drei Tage vor dem Anreisedatum. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag.
                      - generic [ref=f49e311]:
                        - paragraph [ref=f49e313]:
                          - generic "£77" [ref=f49e314]:
                            - generic [ref=f49e315]: £
                            - generic [ref=f49e316]: "77"
                        - paragraph [ref=f49e317]: Gesamtpreis
                        - generic [ref=f49e318]:
                          - paragraph [ref=f49e319]: 1 Zimmer,
                          - paragraph [ref=f49e320]: 1 Nacht
                    - separator [ref=f49e321]
                  - generic [ref=f49e324]:
                    - generic [ref=f49e326] [cursor=pointer]:
                      - radio "Standard Zahlung sofort fällig. Keine Stornierung möglich. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag." [ref=f49e327]
                      - generic [ref=f49e330]:
                        - text: Standard
                        - paragraph [ref=f49e331]: Zahlung sofort fällig. Keine Stornierung möglich. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag.
                    - generic [ref=f49e332]:
                      - paragraph [ref=f49e334]:
                        - generic "£68" [ref=f49e335]:
                          - generic [ref=f49e336]: £
                          - generic [ref=f49e337]: "68"
                      - paragraph [ref=f49e338]: Gesamtpreis
                      - generic [ref=f49e339]:
                        - paragraph [ref=f49e340]: 1 Zimmer,
                        - paragraph [ref=f49e341]: 1 Nacht
            - generic [ref=f49e345]:
              - paragraph [ref=f49e346]: Doppel Zimmer
              - paragraph [ref=f49e347]: Flex
              - separator [ref=f49e348]
              - generic [ref=f49e349]:
                - generic [ref=f49e350]:
                  - paragraph [ref=f49e351]: Aufenthalt
                  - paragraph [ref=f49e352]: 1 Nacht
                - generic [ref=f49e353]:
                  - paragraph [ref=f49e354]: Preis
                  - generic [ref=f49e355]:
                    - link "Tagesraten anzeigen" [ref=f49e356] [cursor=pointer]:
                      - /url: "#"
                    - paragraph [ref=f49e357]: £90
              - separator [ref=f49e358]
              - generic [ref=f49e359]:
                - paragraph [ref=f49e360]: Gesamtpreis
                - paragraph [ref=f49e361]:
                  - generic "£90" [ref=f49e362]:
                    - generic [ref=f49e363]: £
                    - generic [ref=f49e364]: "90"
              - button "Jetzt buchen" [ref=f49e365] [cursor=pointer]
              - generic [ref=f49e366]: Code eingeben
        - generic [ref=f49e371]:
          - heading "Lage" [level=3] [ref=f49e372]
          - generic [ref=f49e375]:
            - generic [ref=f49e376]:
              - paragraph [ref=f49e377]: Shepiston Lane, Middlesex,
              - paragraph [ref=f49e378]: UB3 1RW
            - generic [ref=f49e379]: Anreise mit Navigationsgerät:UB3 1LL
            - generic [ref=f49e381]:
              - paragraph [ref=f49e382]: "Informationen zur Anreise:"
              - paragraph [ref=f49e385]: "Verlassen Sie die M4 an der Ausfahrt J4 und folgen Sie der Beschilderung nach Uxbridge auf der linken Fahrspur. Halten Sie sich links und folgen Sie der Beschilderung Richtung Other Routes und Hayes. Wenn Sie an die Vorfahrtsstraße kommen, machen Sie eine Kehrtwende und nehmen Sie die vierte Ausfahrt in Richtung Haynes. Das Hotel befindet sich nach 180 Metern auf der rechten Seite. (Postleitzahl für Ihr Navigationsgerät: UB3 1LL)."
              - paragraph [ref=f49e387] [cursor=pointer]: Mehr lesen
            - generic [ref=f49e388]:
              - paragraph [ref=f49e389]: "In der Umgebung:"
              - list [ref=f49e391]:
                - listitem [ref=f49e392]:
                  - paragraph [ref=f49e393]: Dieses Hotel befindet sich in Nahverkehrszone 6 in London
                - listitem [ref=f49e394]:
                  - paragraph [ref=f49e395]: "Heathrow (Terminal 2, 3): 4 Kilometer"
                - listitem [ref=f49e396]:
                  - paragraph [ref=f49e397]: "Heathrow (Terminal 4): 7,2 Kilometer"
                - listitem [ref=f49e398]:
                  - paragraph [ref=f49e399]: "Heathrow (Terminal 5): 7,2 Kilometer"
                - listitem [ref=f49e400]:
                  - paragraph [ref=f49e401]: "Heathrow (Terminal 1-3, Bahnhof): 2,5 Kilometer"
                - listitem [ref=f49e402]:
                  - paragraph [ref=f49e403]: Historische Stätte Windsor Castle
              - paragraph [ref=f49e405] [cursor=pointer]: Mehr lesen
        - generic [ref=f49e407]:
          - heading "Parken am Premier Inn London Heathrow Airport (M4/J4) Hotel" [level=3] [ref=f49e408]
          - generic [ref=f49e409]: Wir bieten einen exklusiven Gästerabatt von £12 pro 24 Stunden auf das Parken. Suchen Sie sich einfach einen Parkplatz und zahlen Sie weniger. Der Parkplatz wird von Horizon Parking betrieben. Stellplätze können nicht vorab reserviert werden. Für größere Fahrzeuge wie Busse, Transporter oder LKWs beträgt die Parkgebühr £30. Dies muss vorab per E-Mail mit dem Hotel abgestimmt werden, da lediglich bis zu 3 große Fahrzeuge gleichzeitig untergebracht werden können. Die Verfügbarkeit kann variieren – bei Fragen wenden Sie sich bitte im Voraus direkt an das Hotel. Flughafenparkplätze sind über unsere Partner Holiday Extras zu attraktiven Preisen verfügbar.
        - generic "Restaurant" [ref=f49e411]:
          - heading "Restaurant" [level=3] [ref=f49e412]
          - img "Thyme Bar & Grill" [ref=f49e414]
          - status [ref=f49e415]:
            - generic [ref=f49e419]: Abhängig vom gewählten Hotel und Aufenthaltszeitraum bieten wir unterschiedliche Optionen für Frühstück und Abendessen an.
          - generic [ref=f49e421]:
            - tablist [ref=f49e422]:
              - tab [selected] [ref=f49e423] [cursor=pointer]:
                - heading "Frühstück" [level=3] [ref=f49e425]
              - tab [ref=f49e426] [cursor=pointer]:
                - heading "Abendessen" [level=3] [ref=f49e428]
              - tab [ref=f49e429] [cursor=pointer]:
                - heading "Meal Deal" [level=3] [ref=f49e431]
            - tabpanel "Frühstück" [ref=f49e433]:
              - generic [ref=f49e434]:
                - img "Frühstück" [ref=f49e436]
                - generic [ref=f49e437]:
                  - generic [ref=f49e438]:
                    - paragraph [ref=f49e442]: Genussvoll in den Morgen! Stellen Sie sich Ihr eigenes Frühstück zusammen und füllen Sie Ihren Teller mit frisch zubereiteten Favoriten wie Speck, Würstchen, Eiern und Rösti – leckere vegetarische und vegane Optionen inklusive – sowie kontinentalen Köstlichkeiten wie Obst, Müsli und frischem Gebäck. Und wenn ein Erwachsener ein Premier Inn-Frühstück bestellt, frühstücken bis zu zwei Kinder kostenlos mit.**
                    - text: Mehr lesen
                  - button "Frühstücksmenü" [ref=f49e444] [cursor=pointer]
        - generic [ref=f49e446]:
          - heading "Bewertungen für Premier Inn London Heathrow Airport (M4/J4) Hotel" [level=3] [ref=f49e447]
          - generic [ref=f49e448]:
            - button "Bewertungen anzeigen" [ref=f49e451] [cursor=pointer]
            - generic [ref=f49e455]:
              - paragraph [ref=f49e456]: Bewertungen von Reisenden via
              - generic [ref=f49e458]:
                - paragraph [ref=f49e461]: Lage
                - paragraph [ref=f49e465]: Schlafqualität
                - paragraph [ref=f49e469]: Zimmer
                - paragraph [ref=f49e473]: Service
                - paragraph [ref=f49e477]: Preis/Leistung
                - paragraph [ref=f49e481]: Sauberkeit
              - paragraph [ref=f49e484]: Bewertung schreiben
        - generic [ref=f49e488]:
          - heading "Hotelbeschreibung" [level=3] [ref=f49e489]
          - generic [ref=f49e490]: Abflug oder Ankunft? Wohin es Sie auch zieht, unser Hotel in Heathrow ist die ideale Ausgangsbasis. Mit den guten Verkehrsanbindungen und dem Heathrow Express Rail in wenigen Minuten Entfernung sind auch Geschäfts- und Freizeitziele in ganz London schnell zu erreichen. Falls Sie Zeit zum Entdecken haben, planen Sie ein großes Abenteuer im Legoland. Feuern Sie Ihre Mannschaft im Twickenham Stadion an. Oder holen Sie sich Adrenalin und Nervenkitzel im Thorpe Park. Freuen Sie sich dann auf ein leckeres Essen in unserem Restaurant und eine erholsame Nacht in einem komfortablen, modernen Zimmer mit großzügigem 40-Zoll-Flachbildfernseher, neu eingerichtetem Badezimmer mit extragroßem Duschkopf und superbequemem Bett.
        - generic [ref=f49e491]:
          - heading "Kontaktinformationen des Hotels" [level=3] [ref=f49e492]
          - paragraph [ref=f49e493]: Telefonnummer 0333 003 1715
          - generic [ref=f49e494]: "Hotels in Deutschland: Anrufe aus dem deutschen Festnetz werden zum Inlandstarif abgerechnet (wenn Sie sich in Deutschland befinden), aus dem Ortsnetz zum Ortstarif. Für Anrufe aus dem Mobilfunknetz und aus dem Ausland fallen ggf. weitere Gebühren an. Hotels in UK: Anrufe zu 0871-Nummern kosten 13 Pence pro Minute zuzüglich aller zusätzlichen Gebühren Ihres Telefonanbieters. Anrufe zu 0333-Nummern werden zum Inlandstarif abgerechnet (wenn Sie sich in Großbritannien aufhalten)."
    - generic [ref=f49e498]:
      - generic [ref=f49e502]:
        - generic [ref=f49e503]:
          - paragraph [ref=f49e504]: Premier Inn
          - generic [ref=f49e505]:
            - link "Über uns" [ref=f49e506] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier.html
            - link "Karriere" [ref=f49e507] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/unternehmen/karriere.html
            - link "Stellenanzeigen" [ref=f49e508] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/unternehmen/karriere/stellenanzeigen.html
            - link "Presse" [ref=f49e509] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/presse.html
            - link "Nachhaltigkeit/Menschenrechte" [ref=f49e510] [cursor=pointer]:
              - /url: https://euw1-pubacc-eu-west-1.dit.premierinn.digital/content/dam/pi/websites/desktop/de/unternehmen/erklaerung-whitbread-menschenrechtsstrategie.pdf
            - link "Unternehmen (engl.)" [ref=f49e511] [cursor=pointer]:
              - /url: https://www.whitbread.co.uk/about-us/
        - generic [ref=f49e512]:
          - paragraph [ref=f49e513]: Kontakt
          - generic [ref=f49e514]:
            - link "Kontaktieren Sie uns" [ref=f49e515] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/kontakt.html
            - link "Ihre Buchung bearbeiten" [ref=f49e516] [cursor=pointer]:
              - /url: https://secure2.uat.premierinn.digital/de/de/bookingmanagement.html
            - link "Kontakt für Firmenkunden" [ref=f49e517] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/business/kontakt.html
            - link "Gruppenbuchungen" [ref=f49e518] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/gruppen.html
            - link "FAQs" [ref=f49e519] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen.html
            - link "Feedback" [ref=f49e520] [cursor=pointer]:
              - /url: mailto:feedback-germany@premierinn.com?subject=Feedback%20zu%20Premier%20Inn%20Deutschland%20/%20Feedback%20for%20Premier%20Inn%20Germany&body=Dieses%20E-Mail-Feedback-Formular%20ist%20ausschlie%C3%9Flich%20f%C3%BCr%20Premier%20Inn%20Deutschland%20vorgesehen.%20Wir%20antworten%20innerhalb%20von%203%20-%205%20Werktagen.%20Bitte%20%C3%BCbermitteln%20Sie%20uns%20die%20nachfolgenden%20Angaben%20%E2%80%93%20ohne%20diese%20k%C3%B6nnen%20wir%20Ihr%20Feedback%20aus%20Datenschutzgr%C3%BCnden%20leider%20nicht%20bearbeiten%20oder%20weiterleiten.%0D%0DPlease%20note:%20This%20feedback%20form%20is%20exclusively%20for%20Premier%20Inn%20Germany.%20We%E2%80%99d%20love%20to%20hear%20about%20your%20recent%20experience%20staying%20in%20one%20of%20our%20Premier%20Inn%20hotels%20in%20Germany.%20Please%20provide%20us%20with%20the%20following%20information%20%E2%80%93%20we%E2%80%99ll%20need%20this%20for%20data%20protection%20reasons%20in%20order%20to%20process%20or%20forward%20on%20your%20feedback.%20We%E2%80%99ll%20aim%20to%20respond%20within%20three%20to%20five%20working%20days.%0D%0DName%20/%20Name:%20%0DTelefonnummer%20/%20Telephone%20number:%20%0DPostleitzahl%20/%20Post%20code:%20%0DHotelname%20/%20Hotel%20name:%20%0DBuchungsnummer%20/%20Booking%20number:%20%0DAufenthaltsdatum%20/%20Date%20of%20stay:%20%0DIhre%20Nachricht%20an%20uns%20/%20Your%20feedback:%20%0D.html
        - generic [ref=f49e521]:
          - paragraph [ref=f49e522]: Rechtliches
          - generic [ref=f49e523]:
            - link "Nutzungsbedingungen" [ref=f49e524] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/bedingungen/nutzungsbedingungen.html
            - link "Unsere AGBs" [ref=f49e525] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/bedingungen/allgemeine-geschaeftsbedingungen-deutschland.html
            - link "Cookies" [ref=f49e526] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/bedingungen/cookies.html
            - link "Datenschutz" [ref=f49e527] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/bedingungen/datenschutz.html
            - link "Impressum" [ref=f49e528] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/impressum.html
            - link "Lieferkettensorgfaltspflichten" [ref=f49e529] [cursor=pointer]:
              - /url: https://euw1-pubacc-eu-west-1.dit.premierinn.digital/content/dam/pi/websites/desktop/de/unternehmen/beschwerdeverfahren-lieferkettengesetz.pdf
        - generic [ref=f49e530]:
          - paragraph [ref=f49e531]: Hotels
          - generic [ref=f49e532]:
            - link "Unsere Zimmer" [ref=f49e533] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/schlaf/zimmer.html
            - link "Unsere Betten" [ref=f49e534] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/schlaf/bett.html
            - link "Barrierefreiheit" [ref=f49e535] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/bedingungen/barrierefreiheit.html
            - link "Frühstück & Bar" [ref=f49e536] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/essen.html
            - link "WLAN" [ref=f49e537] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/wlan.html
            - link "Meetingräume" [ref=f49e538] [cursor=pointer]:
              - /url: https://www.premiermeetings.co.uk/
        - generic [ref=f49e539]:
          - paragraph [ref=f49e540]: Gut zu wissen
          - generic [ref=f49e541]:
            - link "Gute-Nacht-Garantie" [ref=f49e542] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/schlaf/gute-nacht-garantie.html
            - link "Mehr für Ihr Geld" [ref=f49e543] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/preis-leistung.html
            - link "Vorteile für Familien" [ref=f49e544] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/familie.html
            - link "So können Sie bezahlen" [ref=f49e545] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen/bezahlung.html
            - link "Buchung ändern/stornieren" [ref=f49e546] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen/buchung-aendern-stornieren.html
            - link "Expansion in Deutschland" [ref=f49e547] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/unternehmen/expansion-in-deutschland.html
        - generic [ref=f49e548]:
          - paragraph [ref=f49e549]: Anderes
          - generic [ref=f49e550]:
            - link "Unsere flexiblen Tarife" [ref=f49e551] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/flexibel-buchen.html
            - link "City Tax" [ref=f49e552] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen/city-tax.html
            - link "Premier Plus-Zimmer" [ref=f49e553] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/schlaf/premier-plus-zimmer.html
            - link "Check-in & Check-out" [ref=f49e554] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen/checkin-checkout.html
            - link "Anreise & Parken" [ref=f49e555] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen/anreise-parken.html
            - link "Sitemap" [ref=f49e556] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/sitemap.html
      - generic [ref=f49e559]:
        - paragraph [ref=f49e560]: Die wichtigsten Premier Inn Hotels
        - generic [ref=f49e561]:
          - generic [ref=f49e562]:
            - link "Hotels Berlin" [ref=f49e563] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/berlin/berlin.html
            - link "Hotels Braunschweig" [ref=f49e564] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/niedersachsen/braunschweig.html
            - link "Hotels Darmstadt" [ref=f49e565] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/hessen/darmstadt.html
            - link "Hotels Dresden" [ref=f49e566] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/sachsen/dresden.html
            - link "Hotels Düsseldorf" [ref=f49e567] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/nordrhein-westfalen/duesseldorf.html
            - link "Hotels Essen" [ref=f49e568] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/nordrhein-westfalen/essen.html
            - link "Hotels Frankfurt" [ref=f49e569] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/hessen/frankfurt.html
          - generic [ref=f49e570]:
            - link "Hotels Freiburg" [ref=f49e571] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/freiburg.html
            - link "Hotels Hamburg" [ref=f49e572] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/hamburg/hamburg.html
            - link "Hotels Hannover" [ref=f49e573] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/niedersachsen/hannover.html
            - link "Hotels Heidelberg" [ref=f49e574] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/heidelberg.html
            - link "Hotels Heilbronn" [ref=f49e575] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/heilbronn.html
            - link "Hotels Karlsruhe" [ref=f49e576] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/karlsruhe.html
            - link "Hotels Köln" [ref=f49e577] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/nordrhein-westfalen/koeln.html
          - generic [ref=f49e578]:
            - link "Hotels Leipzig" [ref=f49e579] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/sachsen/leipzig.html
            - link "Hotels Lindau" [ref=f49e580] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/lindau.html
            - link "Hotels Lübeck" [ref=f49e581] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/schleswig-holstein/luebeck.html
            - link "Hotels Mannheim" [ref=f49e582] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/mannheim.html
            - link "Hotels München" [ref=f49e583] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/muenchen.html
            - link "Hotels Nürnberg" [ref=f49e584] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/nuernberg.html
            - link "Hotels Passau" [ref=f49e585] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/passau.html
          - generic [ref=f49e586]:
            - link "Hotels Regensburg" [ref=f49e587] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/regensburg.html
            - link "Hotels Saarbrücken" [ref=f49e588] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/saarland/saarbruecken.html
            - link "Hotels Stuttgart" [ref=f49e589] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/stuttgart.html
            - link "Hotels Wiesbaden" [ref=f49e590] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/hessen/wiesbaden.html
            - link "Hotels Wolfsburg" [ref=f49e591] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/niedersachsen/wolfsburg.html
            - link "Hotels Wuppertal" [ref=f49e592] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/nordrhein-westfalen/wuppertal.html
            - link "Hotels London" [ref=f49e593] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/england/greater-london/london.html
          - generic [ref=f49e594]:
            - link "Hotels Manchester" [ref=f49e595] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/england/greater-manchester/manchester.html
            - link "Hotels Edinburgh" [ref=f49e596] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/schottland/lothian/edinburgh.html
            - link "Hotels Dublin" [ref=f49e597] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/republik-irland/dublin.html
            - link "Flughafenhotels" [ref=f49e598] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/standorte/flughafenhotels.html
            - link "Unsere besten Hotels" [ref=f49e599] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/beste-hotels.html
            - link "Neue Hotels" [ref=f49e600] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/standorte/neue-hotels.html
            - link "Alle Hotels im Überblick" [ref=f49e601] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels.html
          - generic [ref=f49e602]:
            - link "Reiseführer Berlin" [ref=f49e603] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/berlin.html
            - link "Reiseführer Dresden" [ref=f49e604] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/dresden.html
            - link "Reiseführer Düsseldorf" [ref=f49e605] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/duesseldorf.html
            - link "Reiseführer Hamburg" [ref=f49e606] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/hamburg.html
            - link "Reiseführer Köln" [ref=f49e607] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/koeln.html
            - link "Reiseführer München" [ref=f49e608] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/muenchen.html
            - link "Alle Reiseführer" [ref=f49e609] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/reisefuehrer.html
      - generic [ref=f49e610]:
        - generic [ref=f49e611]: © 2026 Premier Inn
        - generic [ref=f49e612]:
          - link [ref=f49e613] [cursor=pointer]:
            - /url: https://www.facebook.com/PremierInnDeutschland
          - link [ref=f49e615] [cursor=pointer]:
            - /url: https://www.youtube.com/@premierinndeutschland
          - link [ref=f49e617] [cursor=pointer]:
            - /url: https://www.instagram.com/premierinn.de
  - alert [ref=f49e619]
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
  506 |     isDisplayed = true,
  507 |     hotelId,
  508 |   }: {
  509 |     isDisplayed?: boolean;
  510 |     hotelId: string;
  511 |   }): Promise<void> {
  512 |     console.log(`Validate announcement notification displayed=${isDisplayed}`);
  513 |     if (!isDisplayed) {
  514 |       await expect(this.announcementNotificationSection, 'Announcement section should not be visible').not.toBeVisible();
  515 |       return;
  516 |     }
  517 | 
  518 |     const hotelDictionary = await ApiDictionary.fetchHotelDirectoryDictionary({ hotelId });
  519 |     const announcement = hotelDictionary.announcement as { text?: string } | undefined;
  520 |     await expect(this.announcementNotificationSection, 'Announcement section should be visible').toBeVisible();
  521 |     await expect(this.announcementNotificationText, 'Announcement description should match AEM').toContainText(announcement?.text ?? '');
  522 |   }
  523 | 
  524 |   /** Validate required room occupancy query parameters in the current HDP URL. */
  525 |   async validateSearchParamsInCurrentUrl({
  526 |     roomsList,
  527 |   }: {
  528 |     roomsList: Array<{ adultsNumber: number; childrenNumber: number; roomType: { id: string } }>;
  529 |   }): Promise<void> {
  530 |     const currentUrl = new URL(this.page.url());
  531 |     expect(currentUrl.searchParams.get('ROOMS'), 'ROOMS query parameter should match selected rooms').toBe(`${roomsList.length}`);
  532 |     for (const [index, room] of roomsList.entries()) {
  533 |       const roomNumber = index + 1;
  534 |       expect(currentUrl.searchParams.get(`ADULT${roomNumber}`), `ADULT${roomNumber} query parameter should match`).toBe(`${room.adultsNumber}`);
  535 |       expect(currentUrl.searchParams.get(`CHILD${roomNumber}`), `CHILD${roomNumber} query parameter should match`).toBe(`${room.childrenNumber}`);
  536 |       expect(currentUrl.searchParams.get(`INTTYP${roomNumber}`), `INTTYP${roomNumber} query parameter should match`).toBe(room.roomType.id);
  537 |     }
  538 |   }
  539 | 
  540 |   /**
  541 |    * Validate that the Hotel Details Page has fully loaded.
  542 |    * Waits for the page loaded indicator to be visible within 30s.
  543 |    * @throws Error with page name and selector if indicator not found
  544 |    */
  545 |   async validatePage(): Promise<void> {
  546 |     console.log('Validating hotel details page loaded');
  547 |     try {
  548 |       await this.pageLoadedIndicator.waitFor({ state: 'visible', timeout: 30000 });
  549 |     } catch {
  550 |       throw new Error(
  551 |         `HotelDetailsPage did not load within 30s. Selector not found: h1[data-testid="hdp_hotelTitle"]`
  552 |       );
  553 |     }
  554 |   }
  555 | 
  556 |   /**
  557 |    * Validate that the price breakdown section is visible and contains pricing content.
  558 |    * Checks that the stay/price area shows the total cost for nights.
  559 |    */
  560 |   async validatePriceBreakdown(): Promise<void> {
  561 |     console.log('Validating price breakdown section');
  562 |     await this.priceBreakdownSection.waitFor({ state: 'visible', timeout: 30000 });
  563 |     await expect(this.priceBreakdownTotalForNights, 'Price breakdown total should be displayed').toBeVisible({ timeout: 10000 });
  564 |     // Verify the total for nights contains a numeric value (currency symbol + digits)
  565 |     await expect(this.priceBreakdownTotalForNights, 'Price breakdown should contain currency and amount').toHaveText(/[£€$]\s*\d+/, { timeout: 10000 });
  566 |   }
  567 | 
  568 |   /** Validate the selected booking summary rate plan and room count. */
  569 |   async validateBookingSummaryRatePlan(options: { ratePlan: string; roomsCount?: number }): Promise<void> {
  570 |     const { ratePlan, roomsCount = 1 } = options;
  571 |     console.log(`Validating booking summary rate plan: ${ratePlan}`);
  572 |     if (!Constants.BROWSER_RESOLUTIONS.isDesktop()) {
  573 |       return;
  574 |     }
  575 | 
  576 |     if (roomsCount > 1) {
  577 |       await expect(this.bookingSummaryRoomsCountLabel, 'Booking summary rooms count should match').toContainText(`${roomsCount}`, { timeout: 10000 });
  578 |       await expect(this.bookingSummaryRoomsCountLabel, 'Booking summary rate plan should match').toContainText(ratePlan, { timeout: 10000 });
  579 |       return;
  580 |     }
  581 | 
  582 |     await expect(this.bookingSummaryRateLabel, 'Booking summary selected rate should match').toHaveText(ratePlan, { timeout: 10000 });
  583 |   }
  584 | 
  585 |   /** Validate whether the booking summary total price is displayed. */
  586 |   async validateTotalPriceRate(options: { isDisplayed?: boolean } = {}): Promise<void> {
  587 |     const { isDisplayed = true } = options;
  588 |     console.log('Validating total price rate');
  589 |     if (isDisplayed) {
  590 |       await expect(this.bookingSummaryTotalPrice, 'Total price summary should be displayed').toBeVisible({ timeout: 10000 });
  591 |       await expect(this.bookingSummaryTotalPrice, 'Total price summary should contain currency and amount').toHaveText(/[£€$]\s*\d+/, { timeout: 10000 });
  592 |       return;
  593 |     }
  594 | 
  595 |     await expect(this.bookingSummaryTotalPrice, 'Total price summary should not be displayed').not.toBeVisible({ timeout: 10000 });
  596 |   }
  597 | 
  598 |   /**
  599 |    * Validate the per-night price rows shown in the HDP price breakdown.
  600 |    * @param options.numberOfDays - Expected number of nights in the stay
  601 |    * @param options.numberOfRooms - Expected number of rooms in the stay
  602 |    * @param options.hotelCurrency - Hotel country/currency code used to validate the displayed symbol
  603 |    */
  604 |   async validatePricesPerNight(options: { numberOfDays: number; numberOfRooms: number; hotelCurrency: string }): Promise<void> {
  605 |     console.log('Validating prices per night in HDP breakdown');
> 606 |     await expect(this.priceBreakdownRows, `Price breakdown should show ${options.numberOfDays} rows per room`).toHaveCount(
      |                                                                                                                ^ Error: Price breakdown should show 1 rows per room
  607 |       options.numberOfDays * options.numberOfRooms,
  608 |       { timeout: 10000 }
  609 |     );
  610 | 
  611 |     const expectedSymbol = options.hotelCurrency.toUpperCase() === 'GBP' || options.hotelCurrency.toLowerCase() === 'gb' ? '£' : '€';
  612 |     for (let index = 0; index < await this.priceBreakdownRows.count(); index++) {
  613 |       const priceText = await this.priceBreakdownRows.nth(index).locator('span').nth(1).innerText();
  614 |       expect(priceText, `Price breakdown row ${index} should contain ${expectedSymbol}`).toContain(expectedSymbol);
  615 |       expect(PriceHelpers.getPriceAmountFromUiLabel(priceText), `Price breakdown row ${index} should contain a positive price`).toBeGreaterThan(0);
  616 |     }
  617 |   }
  618 | }
  619 | 
```