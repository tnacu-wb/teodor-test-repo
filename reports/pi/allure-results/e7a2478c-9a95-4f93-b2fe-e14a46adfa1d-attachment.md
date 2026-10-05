# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts >> PI Baseline E2E - Guest PIBA Pay on Arrival Amendment >> Test Book as Guest user: 1 night, 1 room (Double), 2 adults, Flex rate, meals, donations - POA BAC card and Amend by change room type. TestCase ID: 380779.
- Location: qa/tests/regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts:51:7

# Error details

```
Error: Could not find date 2026-09-06 in calendar after 12 month advances
```

# Page snapshot

```yaml
- generic [ref=f54e1]:
  - generic [ref=f54e3]:
    - banner [ref=f54e4]:
      - generic [ref=f54e7]:
        - link [ref=f54e10] [cursor=pointer]:
          - /url: https://www.uat.premierinn.digital/de/de/home.html
        - generic [ref=f54e12]:
          - img "Deutsch" [ref=f54e15] [cursor=pointer]
          - generic [ref=f54e16]:
            - paragraph [ref=f54e19] [cursor=pointer]: Premier Inn entdecken
            - paragraph [ref=f54e22] [cursor=pointer]: Premier Inn Business
            - paragraph [ref=f54e24] [cursor=pointer]: Ihre Buchungen
            - generic [ref=f54e26]:
              - button "Einloggen" [ref=f54e27] [cursor=pointer]
              - button "Registrieren" [ref=f54e28] [cursor=pointer]
    - main [ref=f54e29]:
      - generic [ref=f54e31]:
        - generic [ref=f54e33]:
          - combobox "Ort, Postleitzahl oder Hotelname" [ref=f54e38] [cursor=pointer]: London Heathrow Airport (M4/J4)
          - generic [ref=f54e44]:
            - textbox "datepicker-input" [ref=f54e49] [cursor=pointer]:
              - /placeholder: Check-In | Abreise
              - text: Heute | Fr. 04 Sep. 2026
            - dialog "Choose Date" [ref=f54e53]:
              - alert [ref=f54e54]: September 2027
              - button "Previous Month" [ref=f54e55] [cursor=pointer]
              - generic [ref=f54e56]:
                - heading "September 2027" [level=2] [ref=f54e58]
                - table [ref=f54e59]:
                  - rowgroup [ref=f54e60]:
                    - row [ref=f54e61]:
                      - columnheader "Montag" [ref=f54e62]: Mo.
                      - columnheader "Dienstag" [ref=f54e64]: Di.
                      - columnheader "Mittwoch" [ref=f54e66]: Mi.
                      - columnheader "Donnerstag" [ref=f54e68]: Do.
                      - columnheader "Freitag" [ref=f54e70]: Fr.
                      - columnheader "Samstag" [ref=f54e72]: Sa.
                      - columnheader "Sonntag" [ref=f54e74]: So.
                  - rowgroup "Month September, 2027" [ref=f54e76]:
                    - row [ref=f54e77]:
                      - gridcell "Choose Mittwoch, 1. September 2027" [active] [ref=f54e78] [cursor=pointer]:
                        - generic [ref=f54e80]: "1"
                      - gridcell "Not available Donnerstag, 2. September 2027" [disabled]:
                        - generic: "2"
                      - gridcell "Not available Freitag, 3. September 2027" [disabled]:
                        - generic: "3"
                      - gridcell "Not available Samstag, 4. September 2027" [disabled]:
                        - generic: "4"
                      - gridcell "Not available Sonntag, 5. September 2027" [disabled]:
                        - generic: "5"
                    - row "Not available Montag, 6. September 2027 Not available Dienstag, 7. September 2027 Not available Mittwoch, 8. September 2027 Not available Donnerstag, 9. September 2027 Not available Freitag, 10. September 2027 Not available Samstag, 11. September 2027 Not available Sonntag, 12. September 2027" [ref=f54e81]:
                      - gridcell "Not available Montag, 6. September 2027" [disabled]:
                        - generic: "6"
                      - gridcell "Not available Dienstag, 7. September 2027" [disabled]:
                        - generic: "7"
                      - gridcell "Not available Mittwoch, 8. September 2027" [disabled]:
                        - generic: "8"
                      - gridcell "Not available Donnerstag, 9. September 2027" [disabled]:
                        - generic: "9"
                      - gridcell "Not available Freitag, 10. September 2027" [disabled]:
                        - generic: "10"
                      - gridcell "Not available Samstag, 11. September 2027" [disabled]:
                        - generic: "11"
                      - gridcell "Not available Sonntag, 12. September 2027" [disabled]:
                        - generic: "12"
                    - row "Not available Montag, 13. September 2027 Not available Dienstag, 14. September 2027 Not available Mittwoch, 15. September 2027 Not available Donnerstag, 16. September 2027 Not available Freitag, 17. September 2027 Not available Samstag, 18. September 2027 Not available Sonntag, 19. September 2027" [ref=f54e82]:
                      - gridcell "Not available Montag, 13. September 2027" [disabled]:
                        - generic: "13"
                      - gridcell "Not available Dienstag, 14. September 2027" [disabled]:
                        - generic: "14"
                      - gridcell "Not available Mittwoch, 15. September 2027" [disabled]:
                        - generic: "15"
                      - gridcell "Not available Donnerstag, 16. September 2027" [disabled]:
                        - generic: "16"
                      - gridcell "Not available Freitag, 17. September 2027" [disabled]:
                        - generic: "17"
                      - gridcell "Not available Samstag, 18. September 2027" [disabled]:
                        - generic: "18"
                      - gridcell "Not available Sonntag, 19. September 2027" [disabled]:
                        - generic: "19"
                    - row "Not available Montag, 20. September 2027 Not available Dienstag, 21. September 2027 Not available Mittwoch, 22. September 2027 Not available Donnerstag, 23. September 2027 Not available Freitag, 24. September 2027 Not available Samstag, 25. September 2027 Not available Sonntag, 26. September 2027" [ref=f54e83]:
                      - gridcell "Not available Montag, 20. September 2027" [disabled]:
                        - generic: "20"
                      - gridcell "Not available Dienstag, 21. September 2027" [disabled]:
                        - generic: "21"
                      - gridcell "Not available Mittwoch, 22. September 2027" [disabled]:
                        - generic: "22"
                      - gridcell "Not available Donnerstag, 23. September 2027" [disabled]:
                        - generic: "23"
                      - gridcell "Not available Freitag, 24. September 2027" [disabled]:
                        - generic: "24"
                      - gridcell "Not available Samstag, 25. September 2027" [disabled]:
                        - generic: "25"
                      - gridcell "Not available Sonntag, 26. September 2027" [disabled]:
                        - generic: "26"
                    - row "Not available Montag, 27. September 2027 Not available Dienstag, 28. September 2027 Not available Mittwoch, 29. September 2027 Not available Donnerstag, 30. September 2027" [ref=f54e84]:
                      - gridcell "Not available Montag, 27. September 2027" [disabled]:
                        - generic: "27"
                      - gridcell "Not available Dienstag, 28. September 2027" [disabled]:
                        - generic: "28"
                      - gridcell "Not available Mittwoch, 29. September 2027" [disabled]:
                        - generic: "29"
                      - gridcell "Not available Donnerstag, 30. September 2027" [disabled]:
                        - generic: "30"
              - generic [ref=f54e85]:
                - button "Zurücksetzen" [ref=f54e86] [cursor=pointer]
                - button "Übernehmen" [ref=f54e87] [cursor=pointer]
          - button "1 Erwachsener, 1 Zimmer" [ref=f54e91] [cursor=pointer]
          - button "Suchen" [ref=f54e92] [cursor=pointer]
        - navigation "breadcrumb" [ref=f54e94]:
          - list [ref=f54e95]:
            - listitem [ref=f54e96]:
              - link "Home" [ref=f54e97] [cursor=pointer]:
                - /url: /de/de/home.html
              - text: /
            - listitem [ref=f54e98]:
              - link "Hotelverzeichnis" [ref=f54e99] [cursor=pointer]:
                - /url: /de/de/hotels.html
              - text: /
            - listitem [ref=f54e100]:
              - link "England" [ref=f54e101] [cursor=pointer]:
                - /url: /de/de/hotels/england.html
              - text: /
            - listitem [ref=f54e102]:
              - link "Greater London" [ref=f54e103] [cursor=pointer]:
                - /url: /de/de/hotels/england/greater-london.html
              - text: /
            - listitem [ref=f54e104]:
              - link "London" [ref=f54e105] [cursor=pointer]:
                - /url: /de/de/hotels/england/greater-london/london.html
              - text: /
            - listitem [ref=f54e106]:
              - generic [ref=f54e107] [cursor=pointer]: London Heathrow Airport M4j4
        - generic [ref=f54e108]:
          - generic [ref=f54e109]:
            - heading "London Heathrow Airport (M4/J4) Hotel" [level=1] [ref=f54e111]
            - generic [ref=f54e112]:
              - generic [ref=f54e113]:
                - img "ta-ratings-img" [ref=f54e114]
                - generic [ref=f54e115] [cursor=pointer]: (2503 Bewertungen)
              - img "Travelers Choice" [ref=f54e116] [cursor=pointer]
            - generic [ref=f54e117]:
              - list [ref=f54e119]:
                - listitem [ref=f54e120]:
                  - generic [ref=f54e121]: New Premier Plus rooms | New restaurant
              - paragraph [ref=f54e124]: An diesem ideal gelegenen Zwischenstopp am Flughafen Heathrow nahe der M4 ist Ihnen vor der Reise eine erholsame Nacht sicher
            - generic [ref=f54e125]:
              - strong [ref=f54e126]: Hotelausstattung
              - text: Alles anzeigen
            - generic [ref=f54e127]:
              - paragraph [ref=f54e131] [cursor=pointer]: Kostenpflichtige Parkplätze vor Ort
              - paragraph [ref=f54e135] [cursor=pointer]: Frühstück
              - paragraph [ref=f54e139] [cursor=pointer]: Klimatisierte Zimmer
              - paragraph [ref=f54e143] [cursor=pointer]: Restaurant
              - paragraph [ref=f54e147] [cursor=pointer]: Kostenloses WLAN
              - paragraph [ref=f54e151] [cursor=pointer]: Behindertengerechte Zimmer
          - generic [ref=f54e153]:
            - generic [ref=f54e154]:
              - generic [ref=f54e155]:
                - generic [ref=f54e156] [cursor=pointer]
                - generic:
                  - img "overlay"
                  - paragraph: New Premier Plus rooms & news restaurant
              - generic [ref=f54e158] [cursor=pointer]
              - generic [ref=f54e160] [cursor=pointer]
            - button "Alle Fotos anzeigen" [ref=f54e161] [cursor=pointer]
        - generic [ref=f54e164]:
          - heading "Wählen Sie Ihren Tarif" [level=3] [ref=f54e165]
          - paragraph [ref=f54e166]: Wir wissen, dass sich Pläne kurzfristig ändern können. Bitte stellen Sie sicher, dass Sie einen Tarif mit entsprechender Flexibilität buchen.
          - generic [ref=f54e167]:
            - generic [ref=f54e168]:
              - generic [ref=f54e171]:
                - generic [ref=f54e172]:
                  - generic [ref=f54e173]:
                    - generic [ref=f54e174]: Premier Plus Zimmer
                    - paragraph [ref=f54e175]: Für besondere Gelegenheiten braucht es besondere Zimmer. Egal, ob es sich um eine wichtige Geschäftsreise oder einen festlichen Anlass handelt, mit einem Upgrade auf unser Premier Plus-Zimmer liegen Sie genau richtig und holen das Beste aus Ihrem Aufentha
                  - img "Room type Image" [ref=f54e177]
                - generic [ref=f54e179]:
                  - generic [ref=f54e180]:
                    - generic [ref=f54e182]:
                      - generic [ref=f54e184] [cursor=pointer]:
                        - radio "Flex Zahlung sofort oder bei Anreise. Kostenlose Stornierung und Änderung bis 13:00 Uhr am Anreisetag." [checked] [ref=f54e185]
                        - generic [ref=f54e188]:
                          - text: Flex
                          - paragraph [ref=f54e189]: Zahlung sofort oder bei Anreise. Kostenlose Stornierung und Änderung bis 13:00 Uhr am Anreisetag.
                      - generic [ref=f54e190]:
                        - paragraph [ref=f54e192]:
                          - generic "£149" [ref=f54e193]:
                            - generic [ref=f54e194]: £
                            - generic [ref=f54e195]: "149"
                        - paragraph [ref=f54e196]: Gesamtpreis
                        - generic [ref=f54e197]:
                          - paragraph [ref=f54e198]: 1 Zimmer,
                          - paragraph [ref=f54e199]: 2 Nächte
                    - separator [ref=f54e200]
                  - generic [ref=f54e203]:
                    - generic [ref=f54e205] [cursor=pointer]:
                      - radio "Standard Zahlung sofort fällig. Keine Stornierung möglich. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag." [ref=f54e206]
                      - generic [ref=f54e209]:
                        - text: Standard
                        - paragraph [ref=f54e210]: Zahlung sofort fällig. Keine Stornierung möglich. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag.
                    - generic [ref=f54e211]:
                      - paragraph [ref=f54e213]:
                        - generic "£125" [ref=f54e214]:
                          - generic [ref=f54e215]: £
                          - generic [ref=f54e216]: "125"
                      - paragraph [ref=f54e217]: Gesamtpreis
                      - generic [ref=f54e218]:
                        - paragraph [ref=f54e219]: 1 Zimmer,
                        - paragraph [ref=f54e220]: 2 Nächte
              - generic [ref=f54e223]:
                - generic [ref=f54e224]:
                  - generic [ref=f54e225]:
                    - generic [ref=f54e226]:
                      - paragraph [ref=f54e232]: Nur noch 5 Zimmer für Ihre Suche verfügbar
                      - text: Premier Plus Zimmer mit Ausblick
                    - paragraph [ref=f54e233]: Unser optimiertes Zimmerdesign mit attraktivem Ausblick, Ultimate Wi-Fi, Kaffeemaschine, Minikühlschrank, USB-Anschlüssen am Bett, Bügeleisen, verbessertem Arbeitsplatz und vielem mehr.
                  - img "Room type Image" [ref=f54e235]
                - generic [ref=f54e237]:
                  - generic [ref=f54e238]:
                    - generic [ref=f54e240]:
                      - generic [ref=f54e242] [cursor=pointer]:
                        - radio "Flex Zahlung sofort oder bei Anreise. Kostenlose Stornierung und Änderung bis 13:00 Uhr am Anreisetag." [ref=f54e243]
                        - generic [ref=f54e246]:
                          - text: Flex
                          - paragraph [ref=f54e247]: Zahlung sofort oder bei Anreise. Kostenlose Stornierung und Änderung bis 13:00 Uhr am Anreisetag.
                      - generic [ref=f54e248]:
                        - paragraph [ref=f54e250]:
                          - generic "£149" [ref=f54e251]:
                            - generic [ref=f54e252]: £
                            - generic [ref=f54e253]: "149"
                        - paragraph [ref=f54e254]: Gesamtpreis
                        - generic [ref=f54e255]:
                          - paragraph [ref=f54e256]: 1 Zimmer,
                          - paragraph [ref=f54e257]: 2 Nächte
                    - separator [ref=f54e258]
                  - generic [ref=f54e261]:
                    - generic [ref=f54e263] [cursor=pointer]:
                      - radio "Standard Zahlung sofort fällig. Keine Stornierung möglich. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag." [ref=f54e264]
                      - generic [ref=f54e267]:
                        - text: Standard
                        - paragraph [ref=f54e268]: Zahlung sofort fällig. Keine Stornierung möglich. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag.
                    - generic [ref=f54e269]:
                      - paragraph [ref=f54e271]:
                        - generic "£125" [ref=f54e272]:
                          - generic [ref=f54e273]: £
                          - generic [ref=f54e274]: "125"
                      - paragraph [ref=f54e275]: Gesamtpreis
                      - generic [ref=f54e276]:
                        - paragraph [ref=f54e277]: 1 Zimmer,
                        - paragraph [ref=f54e278]: 2 Nächte
              - generic [ref=f54e281]:
                - generic [ref=f54e282]:
                  - generic [ref=f54e283]:
                    - generic [ref=f54e284]: Standard Zimmer
                    - paragraph [ref=f54e285]: Unsere Doppelzimmer sind mit einem super bequemen Bett, einer Massagedusche und kostenlosem WLAN ausgestattet und bieten alles, was Sie für einen erholsamen Schlaf benötigen
                    - paragraph [ref=f54e288]: Details ansehen
                  - img "Room type Image" [ref=f54e290]
                - generic [ref=f54e292]:
                  - generic [ref=f54e293]:
                    - generic [ref=f54e295]:
                      - generic [ref=f54e297] [cursor=pointer]:
                        - radio "Flex Zahlung sofort oder bei Anreise. Kostenlose Stornierung und Änderung bis 13:00 Uhr am Anreisetag." [ref=f54e298]
                        - generic [ref=f54e301]:
                          - text: Flex
                          - paragraph [ref=f54e302]: Zahlung sofort oder bei Anreise. Kostenlose Stornierung und Änderung bis 13:00 Uhr am Anreisetag.
                      - generic [ref=f54e303]:
                        - paragraph [ref=f54e305]:
                          - generic "£149" [ref=f54e306]:
                            - generic [ref=f54e307]: £
                            - generic [ref=f54e308]: "149"
                        - paragraph [ref=f54e309]: Gesamtpreis
                        - generic [ref=f54e310]:
                          - paragraph [ref=f54e311]: 1 Zimmer,
                          - paragraph [ref=f54e312]: 2 Nächte
                    - separator [ref=f54e313]
                  - generic [ref=f54e316]:
                    - generic [ref=f54e318] [cursor=pointer]:
                      - radio "Standard Zahlung sofort fällig. Keine Stornierung möglich. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag." [ref=f54e319]
                      - generic [ref=f54e322]:
                        - text: Standard
                        - paragraph [ref=f54e323]: Zahlung sofort fällig. Keine Stornierung möglich. Kostenlose Änderung des Ankunftsdatums bis 13:00 Uhr am Anreisetag.
                    - generic [ref=f54e324]:
                      - paragraph [ref=f54e326]:
                        - generic "£125" [ref=f54e327]:
                          - generic [ref=f54e328]: £
                          - generic [ref=f54e329]: "125"
                      - paragraph [ref=f54e330]: Gesamtpreis
                      - generic [ref=f54e331]:
                        - paragraph [ref=f54e332]: 1 Zimmer,
                        - paragraph [ref=f54e333]: 2 Nächte
            - generic [ref=f54e337]:
              - paragraph [ref=f54e338]: Doppel Zimmer
              - paragraph [ref=f54e339]: Flex
              - separator [ref=f54e340]
              - generic [ref=f54e341]:
                - generic [ref=f54e342]:
                  - paragraph [ref=f54e343]: Aufenthalt
                  - paragraph [ref=f54e344]: 2 Nächte
                - generic [ref=f54e345]:
                  - paragraph [ref=f54e346]: Preis
                  - generic [ref=f54e347]:
                    - link "Tagesraten anzeigen" [ref=f54e348] [cursor=pointer]:
                      - /url: "#"
                    - paragraph [ref=f54e349]: £149
              - separator [ref=f54e350]
              - generic [ref=f54e351]:
                - paragraph [ref=f54e352]: Gesamtpreis
                - paragraph [ref=f54e353]:
                  - generic "£149" [ref=f54e354]:
                    - generic [ref=f54e355]: £
                    - generic [ref=f54e356]: "149"
              - button "Jetzt buchen" [ref=f54e357] [cursor=pointer]
          - status [ref=f54e359]:
            - generic [ref=f54e363]: Der Ausblick aus dem Zimmer kann je nach Zimmertyp, Lage im Hotel, Etage und Ausrichtung des Gebäudes variieren. Wir bemühen uns zwar, Wünschen unserer Gäste nach Möglichkeit zu entsprechen, können jedoch keinen bestimmten Ausblick oder ein spezielles Zimmer garantieren; die Zuteilung erfolgt nach Verfügbarkeit beim Check-in. Auch saisonale Veränderungen, Witterungsbedingungen sowie Bauarbeiten in der Umgebung können die Sicht oder die Qualität des Ausblicks beeinflussen.
        - generic [ref=f54e365]:
          - heading "Lage" [level=3] [ref=f54e366]
          - generic [ref=f54e369]:
            - generic [ref=f54e370]:
              - paragraph [ref=f54e371]: Shepiston Lane, Middlesex,
              - paragraph [ref=f54e372]: UB3 1RW
            - generic [ref=f54e373]: Anreise mit Navigationsgerät:UB3 1LL
            - generic [ref=f54e375]:
              - paragraph [ref=f54e376]: "Informationen zur Anreise:"
              - paragraph [ref=f54e379]: "Verlassen Sie die M4 an der Ausfahrt J4 und folgen Sie der Beschilderung nach Uxbridge auf der linken Fahrspur. Halten Sie sich links und folgen Sie der Beschilderung Richtung Other Routes und Hayes. Wenn Sie an die Vorfahrtsstraße kommen, machen Sie eine Kehrtwende und nehmen Sie die vierte Ausfahrt in Richtung Haynes. Das Hotel befindet sich nach 180 Metern auf der rechten Seite. (Postleitzahl für Ihr Navigationsgerät: UB3 1LL)."
              - paragraph [ref=f54e381] [cursor=pointer]: Mehr lesen
            - generic [ref=f54e382]:
              - paragraph [ref=f54e383]: "In der Umgebung:"
              - list [ref=f54e385]:
                - listitem [ref=f54e386]:
                  - paragraph [ref=f54e387]: Dieses Hotel befindet sich in Nahverkehrszone 6 in London
                - listitem [ref=f54e388]:
                  - paragraph [ref=f54e389]: "Heathrow (Terminal 2, 3): 4 Kilometer"
                - listitem [ref=f54e390]:
                  - paragraph [ref=f54e391]: "Heathrow (Terminal 4): 7,2 Kilometer"
                - listitem [ref=f54e392]:
                  - paragraph [ref=f54e393]: "Heathrow (Terminal 5): 7,2 Kilometer"
                - listitem [ref=f54e394]:
                  - paragraph [ref=f54e395]: "Heathrow (Terminal 1-3, Bahnhof): 2,5 Kilometer"
                - listitem [ref=f54e396]:
                  - paragraph [ref=f54e397]: Historische Stätte Windsor Castle
              - paragraph [ref=f54e399] [cursor=pointer]: Mehr lesen
        - generic [ref=f54e401]:
          - heading "Parken am Premier Inn London Heathrow Airport (M4/J4) Hotel" [level=3] [ref=f54e402]
          - generic [ref=f54e403]: Wir bieten einen exklusiven Gästerabatt von £12 pro 24 Stunden auf das Parken. Suchen Sie sich einfach einen Parkplatz und zahlen Sie weniger. Der Parkplatz wird von Horizon Parking betrieben. Stellplätze können nicht vorab reserviert werden. Für größere Fahrzeuge wie Busse, Transporter oder LKWs beträgt die Parkgebühr £30. Dies muss vorab per E-Mail mit dem Hotel abgestimmt werden, da lediglich bis zu 3 große Fahrzeuge gleichzeitig untergebracht werden können. Die Verfügbarkeit kann variieren – bei Fragen wenden Sie sich bitte im Voraus direkt an das Hotel. Flughafenparkplätze sind über unsere Partner Holiday Extras zu attraktiven Preisen verfügbar.
        - generic "Restaurant" [ref=f54e405]:
          - heading "Restaurant" [level=3] [ref=f54e406]
          - img "Thyme Bar & Grill" [ref=f54e408]
          - status [ref=f54e409]:
            - generic [ref=f54e413]: Abhängig vom gewählten Hotel und Aufenthaltszeitraum bieten wir unterschiedliche Optionen für Frühstück und Abendessen an.
          - generic [ref=f54e415]:
            - tablist [ref=f54e416]:
              - tab [selected] [ref=f54e417] [cursor=pointer]:
                - heading "Frühstück" [level=3] [ref=f54e419]
              - tab [ref=f54e420] [cursor=pointer]:
                - heading "Abendessen" [level=3] [ref=f54e422]
              - tab [ref=f54e423] [cursor=pointer]:
                - heading "Meal Deal" [level=3] [ref=f54e425]
            - tabpanel "Frühstück" [ref=f54e427]:
              - generic [ref=f54e428]:
                - img "Frühstück" [ref=f54e430]
                - generic [ref=f54e431]:
                  - generic [ref=f54e432]:
                    - paragraph [ref=f54e436]: Genussvoll in den Morgen! Stellen Sie sich Ihr eigenes Frühstück zusammen und füllen Sie Ihren Teller mit frisch zubereiteten Favoriten wie Speck, Würstchen, Eiern und Rösti – leckere vegetarische und vegane Optionen inklusive – sowie kontinentalen Köstlichkeiten wie Obst, Müsli und frischem Gebäck. Und wenn ein Erwachsener ein Premier Inn-Frühstück bestellt, frühstücken bis zu zwei Kinder kostenlos mit.**
                    - text: Mehr lesen
                  - button "Frühstücksmenü" [ref=f54e438] [cursor=pointer]
        - generic [ref=f54e440]:
          - heading "Bewertungen für Premier Inn London Heathrow Airport (M4/J4) Hotel" [level=3] [ref=f54e441]
          - generic [ref=f54e442]:
            - button "Bewertungen anzeigen" [ref=f54e445] [cursor=pointer]
            - generic [ref=f54e449]:
              - paragraph [ref=f54e450]: Bewertungen von Reisenden via
              - generic [ref=f54e452]:
                - paragraph [ref=f54e455]: Lage
                - paragraph [ref=f54e459]: Schlafqualität
                - paragraph [ref=f54e463]: Zimmer
                - paragraph [ref=f54e467]: Service
                - paragraph [ref=f54e471]: Preis/Leistung
                - paragraph [ref=f54e475]: Sauberkeit
              - paragraph [ref=f54e478]: Bewertung schreiben
        - generic [ref=f54e482]:
          - heading "Hotelbeschreibung" [level=3] [ref=f54e483]
          - generic [ref=f54e484]: Abflug oder Ankunft? Wohin es Sie auch zieht, unser Hotel in Heathrow ist die ideale Ausgangsbasis. Mit den guten Verkehrsanbindungen und dem Heathrow Express Rail in wenigen Minuten Entfernung sind auch Geschäfts- und Freizeitziele in ganz London schnell zu erreichen. Falls Sie Zeit zum Entdecken haben, planen Sie ein großes Abenteuer im Legoland. Feuern Sie Ihre Mannschaft im Twickenham Stadion an. Oder holen Sie sich Adrenalin und Nervenkitzel im Thorpe Park. Freuen Sie sich dann auf ein leckeres Essen in unserem Restaurant und eine erholsame Nacht in einem komfortablen, modernen Zimmer mit großzügigem 40-Zoll-Flachbildfernseher, neu eingerichtetem Badezimmer mit extragroßem Duschkopf und superbequemem Bett.
        - generic [ref=f54e485]:
          - heading "Kontaktinformationen des Hotels" [level=3] [ref=f54e486]
          - paragraph [ref=f54e487]: Telefonnummer 0333 003 1715
          - generic [ref=f54e488]: "Hotels in Deutschland: Anrufe aus dem deutschen Festnetz werden zum Inlandstarif abgerechnet (wenn Sie sich in Deutschland befinden), aus dem Ortsnetz zum Ortstarif. Für Anrufe aus dem Mobilfunknetz und aus dem Ausland fallen ggf. weitere Gebühren an. Hotels in UK: Anrufe zu 0871-Nummern kosten 13 Pence pro Minute zuzüglich aller zusätzlichen Gebühren Ihres Telefonanbieters. Anrufe zu 0333-Nummern werden zum Inlandstarif abgerechnet (wenn Sie sich in Großbritannien aufhalten)."
    - generic [ref=f54e492]:
      - generic [ref=f54e496]:
        - generic [ref=f54e497]:
          - paragraph [ref=f54e498]: Premier Inn
          - generic [ref=f54e499]:
            - link "Über uns" [ref=f54e500] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier.html
            - link "Karriere" [ref=f54e501] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/unternehmen/karriere.html
            - link "Stellenanzeigen" [ref=f54e502] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/unternehmen/karriere/stellenanzeigen.html
            - link "Presse" [ref=f54e503] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/presse.html
            - link "Nachhaltigkeit/Menschenrechte" [ref=f54e504] [cursor=pointer]:
              - /url: https://euw1-pubacc-eu-west-1.dit.premierinn.digital/content/dam/pi/websites/desktop/de/unternehmen/erklaerung-whitbread-menschenrechtsstrategie.pdf
            - link "Unternehmen (engl.)" [ref=f54e505] [cursor=pointer]:
              - /url: https://www.whitbread.co.uk/about-us/
        - generic [ref=f54e506]:
          - paragraph [ref=f54e507]: Kontakt
          - generic [ref=f54e508]:
            - link "Kontaktieren Sie uns" [ref=f54e509] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/kontakt.html
            - link "Ihre Buchung bearbeiten" [ref=f54e510] [cursor=pointer]:
              - /url: https://secure2.uat.premierinn.digital/de/de/bookingmanagement.html
            - link "Kontakt für Firmenkunden" [ref=f54e511] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/business/kontakt.html
            - link "Gruppenbuchungen" [ref=f54e512] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/gruppen.html
            - link "FAQs" [ref=f54e513] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen.html
            - link "Feedback" [ref=f54e514] [cursor=pointer]:
              - /url: mailto:feedback-germany@premierinn.com?subject=Feedback%20zu%20Premier%20Inn%20Deutschland%20/%20Feedback%20for%20Premier%20Inn%20Germany&body=Dieses%20E-Mail-Feedback-Formular%20ist%20ausschlie%C3%9Flich%20f%C3%BCr%20Premier%20Inn%20Deutschland%20vorgesehen.%20Wir%20antworten%20innerhalb%20von%203%20-%205%20Werktagen.%20Bitte%20%C3%BCbermitteln%20Sie%20uns%20die%20nachfolgenden%20Angaben%20%E2%80%93%20ohne%20diese%20k%C3%B6nnen%20wir%20Ihr%20Feedback%20aus%20Datenschutzgr%C3%BCnden%20leider%20nicht%20bearbeiten%20oder%20weiterleiten.%0D%0DPlease%20note:%20This%20feedback%20form%20is%20exclusively%20for%20Premier%20Inn%20Germany.%20We%E2%80%99d%20love%20to%20hear%20about%20your%20recent%20experience%20staying%20in%20one%20of%20our%20Premier%20Inn%20hotels%20in%20Germany.%20Please%20provide%20us%20with%20the%20following%20information%20%E2%80%93%20we%E2%80%99ll%20need%20this%20for%20data%20protection%20reasons%20in%20order%20to%20process%20or%20forward%20on%20your%20feedback.%20We%E2%80%99ll%20aim%20to%20respond%20within%20three%20to%20five%20working%20days.%0D%0DName%20/%20Name:%20%0DTelefonnummer%20/%20Telephone%20number:%20%0DPostleitzahl%20/%20Post%20code:%20%0DHotelname%20/%20Hotel%20name:%20%0DBuchungsnummer%20/%20Booking%20number:%20%0DAufenthaltsdatum%20/%20Date%20of%20stay:%20%0DIhre%20Nachricht%20an%20uns%20/%20Your%20feedback:%20%0D.html
        - generic [ref=f54e515]:
          - paragraph [ref=f54e516]: Rechtliches
          - generic [ref=f54e517]:
            - link "Nutzungsbedingungen" [ref=f54e518] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/bedingungen/nutzungsbedingungen.html
            - link "Unsere AGBs" [ref=f54e519] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/bedingungen/allgemeine-geschaeftsbedingungen-deutschland.html
            - link "Cookies" [ref=f54e520] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/bedingungen/cookies.html
            - link "Datenschutz" [ref=f54e521] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/bedingungen/datenschutz.html
            - link "Impressum" [ref=f54e522] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/impressum.html
            - link "Lieferkettensorgfaltspflichten" [ref=f54e523] [cursor=pointer]:
              - /url: https://euw1-pubacc-eu-west-1.dit.premierinn.digital/content/dam/pi/websites/desktop/de/unternehmen/beschwerdeverfahren-lieferkettengesetz.pdf
        - generic [ref=f54e524]:
          - paragraph [ref=f54e525]: Hotels
          - generic [ref=f54e526]:
            - link "Unsere Zimmer" [ref=f54e527] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/schlaf/zimmer.html
            - link "Unsere Betten" [ref=f54e528] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/schlaf/bett.html
            - link "Barrierefreiheit" [ref=f54e529] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/bedingungen/barrierefreiheit.html
            - link "Frühstück & Bar" [ref=f54e530] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/essen.html
            - link "WLAN" [ref=f54e531] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/wlan.html
            - link "Meetingräume" [ref=f54e532] [cursor=pointer]:
              - /url: https://www.premiermeetings.co.uk/
        - generic [ref=f54e533]:
          - paragraph [ref=f54e534]: Gut zu wissen
          - generic [ref=f54e535]:
            - link "Gute-Nacht-Garantie" [ref=f54e536] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/schlaf/gute-nacht-garantie.html
            - link "Mehr für Ihr Geld" [ref=f54e537] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/preis-leistung.html
            - link "Vorteile für Familien" [ref=f54e538] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/familie.html
            - link "So können Sie bezahlen" [ref=f54e539] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen/bezahlung.html
            - link "Buchung ändern/stornieren" [ref=f54e540] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen/buchung-aendern-stornieren.html
            - link "Expansion in Deutschland" [ref=f54e541] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/unternehmen/expansion-in-deutschland.html
        - generic [ref=f54e542]:
          - paragraph [ref=f54e543]: Anderes
          - generic [ref=f54e544]:
            - link "Unsere flexiblen Tarife" [ref=f54e545] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/flexibel-buchen.html
            - link "City Tax" [ref=f54e546] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen/city-tax.html
            - link "Premier Plus-Zimmer" [ref=f54e547] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/schlaf/premier-plus-zimmer.html
            - link "Check-in & Check-out" [ref=f54e548] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen/checkin-checkout.html
            - link "Anreise & Parken" [ref=f54e549] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/fragen/anreise-parken.html
            - link "Sitemap" [ref=f54e550] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/sitemap.html
      - generic [ref=f54e553]:
        - paragraph [ref=f54e554]: Die wichtigsten Premier Inn Hotels
        - generic [ref=f54e555]:
          - generic [ref=f54e556]:
            - link "Hotels Berlin" [ref=f54e557] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/berlin/berlin.html
            - link "Hotels Braunschweig" [ref=f54e558] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/niedersachsen/braunschweig.html
            - link "Hotels Darmstadt" [ref=f54e559] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/hessen/darmstadt.html
            - link "Hotels Dresden" [ref=f54e560] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/sachsen/dresden.html
            - link "Hotels Düsseldorf" [ref=f54e561] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/nordrhein-westfalen/duesseldorf.html
            - link "Hotels Essen" [ref=f54e562] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/nordrhein-westfalen/essen.html
            - link "Hotels Frankfurt" [ref=f54e563] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/hessen/frankfurt.html
          - generic [ref=f54e564]:
            - link "Hotels Freiburg" [ref=f54e565] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/freiburg.html
            - link "Hotels Hamburg" [ref=f54e566] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/hamburg/hamburg.html
            - link "Hotels Hannover" [ref=f54e567] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/niedersachsen/hannover.html
            - link "Hotels Heidelberg" [ref=f54e568] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/heidelberg.html
            - link "Hotels Heilbronn" [ref=f54e569] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/heilbronn.html
            - link "Hotels Karlsruhe" [ref=f54e570] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/karlsruhe.html
            - link "Hotels Köln" [ref=f54e571] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/nordrhein-westfalen/koeln.html
          - generic [ref=f54e572]:
            - link "Hotels Leipzig" [ref=f54e573] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/sachsen/leipzig.html
            - link "Hotels Lindau" [ref=f54e574] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/lindau.html
            - link "Hotels Lübeck" [ref=f54e575] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/schleswig-holstein/luebeck.html
            - link "Hotels Mannheim" [ref=f54e576] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/mannheim.html
            - link "Hotels München" [ref=f54e577] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/muenchen.html
            - link "Hotels Nürnberg" [ref=f54e578] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/nuernberg.html
            - link "Hotels Passau" [ref=f54e579] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/passau.html
          - generic [ref=f54e580]:
            - link "Hotels Regensburg" [ref=f54e581] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/bayern/regensburg.html
            - link "Hotels Saarbrücken" [ref=f54e582] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/saarland/saarbruecken.html
            - link "Hotels Stuttgart" [ref=f54e583] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/baden-wuerttemberg/stuttgart.html
            - link "Hotels Wiesbaden" [ref=f54e584] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/hessen/wiesbaden.html
            - link "Hotels Wolfsburg" [ref=f54e585] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/niedersachsen/wolfsburg.html
            - link "Hotels Wuppertal" [ref=f54e586] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/deutschland/nordrhein-westfalen/wuppertal.html
            - link "Hotels London" [ref=f54e587] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/england/greater-london/london.html
          - generic [ref=f54e588]:
            - link "Hotels Manchester" [ref=f54e589] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/england/greater-manchester/manchester.html
            - link "Hotels Edinburgh" [ref=f54e590] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/schottland/lothian/edinburgh.html
            - link "Hotels Dublin" [ref=f54e591] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/republik-irland/dublin.html
            - link "Flughafenhotels" [ref=f54e592] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/standorte/flughafenhotels.html
            - link "Unsere besten Hotels" [ref=f54e593] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels/beste-hotels.html
            - link "Neue Hotels" [ref=f54e594] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/wir-sind-premier/standorte/neue-hotels.html
            - link "Alle Hotels im Überblick" [ref=f54e595] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/hotels.html
          - generic [ref=f54e596]:
            - link "Reiseführer Berlin" [ref=f54e597] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/berlin.html
            - link "Reiseführer Dresden" [ref=f54e598] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/dresden.html
            - link "Reiseführer Düsseldorf" [ref=f54e599] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/duesseldorf.html
            - link "Reiseführer Hamburg" [ref=f54e600] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/hamburg.html
            - link "Reiseführer Köln" [ref=f54e601] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/koeln.html
            - link "Reiseführer München" [ref=f54e602] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/muenchen.html
            - link "Alle Reiseführer" [ref=f54e603] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/de/de/reiseziele/reisefuehrer.html
      - generic [ref=f54e604]:
        - generic [ref=f54e605]: © 2026 Premier Inn
        - generic [ref=f54e606]:
          - link [ref=f54e607] [cursor=pointer]:
            - /url: https://www.facebook.com/PremierInnDeutschland
          - link [ref=f54e609] [cursor=pointer]:
            - /url: https://www.youtube.com/@premierinndeutschland
          - link [ref=f54e611] [cursor=pointer]:
            - /url: https://www.instagram.com/premierinn.de
  - alert [ref=f54e613]
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
  115 |       await this.searchSummaryLocationLabel.waitFor({ state: 'visible', timeout: 2000 });
  116 |       await this.searchSummaryLocationLabel.click();
  117 |     } catch {
  118 |       // Already expanded (e.g. first visit to Homepage) — nothing to do.
  119 |     }
  120 |   }
  121 | 
  122 |   /**
  123 |    * Clear the pre-filled location input.
  124 |    */
  125 |   async clearLocation(): Promise<void> {
  126 |     await this.expandIfCollapsed();
  127 |     await this.locationInput.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
  128 |     await this.locationInput.click();
  129 |     await this.locationClearButton.click();
  130 |   }
  131 | 
  132 |   /**
  133 |    * Type a hotel name and select the first matching autocomplete hotel suggestion.
  134 |    * Retries typing since the debounced autocomplete call occasionally misses the first attempt.
  135 |    */
  136 |   async searchForHotel(hotelName: string): Promise<void> {
  137 |     await this.locationInput.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
  138 |     await this.locationInput.click();
  139 |     await this.page.context().setHTTPCredentials(null); // Clear any HTTP auth credentials to avoid interfering with autocomplete requests
  140 |     await this.locationInput.fill(hotelName.substring(0, hotelName.length - 1));
  141 |     await this.locationInput.press(hotelName.charAt(hotelName.length - 1));
  142 | 
  143 |     const firstSuggestion = this.suggestionsList.locator('li, [role="option"]').first();
  144 |     await firstSuggestion.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
  145 |     await firstSuggestion.click();
  146 |   }
  147 | 
  148 |   /**
  149 |    * Click the submit/search button and wait for the resulting page navigation to settle,
  150 |    * so callers don't race a transitional render with stale availability data.
  151 |    */
  152 |   async submit(): Promise<void> {
  153 |     await this.submitButton.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
  154 |     await this.submitButton.click();
  155 |     await this.page.waitForLoadState('networkidle', { timeout: 30000 }).catch(() => {});
  156 |   }
  157 | 
  158 |   /**
  159 |    * Set check-in and check-out dates using the calendar picker.
  160 |    */
  161 |   async setDates(checkInDate: string, checkOutDate: string): Promise<void> {
  162 |     await this.datesButton.click();
  163 |     await this.selectDate(checkInDate);
  164 |     await this.selectDate(checkOutDate);
  165 |   }
  166 | 
  167 |   /**
  168 |    * Set room configuration (adults/children) via the per-room dropdowns.
  169 |    */
  170 |   async setRooms(rooms: RoomConfig[]): Promise<void> {
  171 |     await this.roomPicker.dropdownButton.click();
  172 |     await this.roomPicker.dropdownPanel.waitFor({ state: 'visible' });
  173 | 
  174 |     for (let i = 0; i < rooms.length; i++) {
  175 |       const room = rooms[i];
  176 |       await this.roomPicker.selectAdults(i, room.adults);
  177 |       await this.roomPicker.selectChildren(i, room.children);
  178 |     }
  179 | 
  180 |     await this.roomPicker.doneButton.click();
  181 |   }
  182 | 
  183 |   /**
  184 |    * Perform a full search: set location, dates, rooms, then submit.
  185 |    */
  186 |   async performSearch(options: {
  187 |     hotelName: string;
  188 |     checkInDate: string;
  189 |     checkOutDate: string;
  190 |     rooms: RoomConfig[];
  191 |   }): Promise<void> {
  192 |     await this.clearLocation();
  193 |     await this.searchForHotel(options.hotelName);
  194 |     await this.setDates(options.checkInDate, options.checkOutDate);
  195 |     await this.setRooms(options.rooms);
  196 |     await this.submit();
  197 |   }
  198 | 
  199 |   // ─── Private Helpers ────────────────────────────────────────────────────────
  200 | 
  201 |   private async selectDate(isoDate: string): Promise<void> {
  202 |     const date = new Date(isoDate);
  203 |     const dayOption = this.calendar.dayOption(date);
  204 | 
  205 |     for (let attempt = 0; attempt < 12; attempt++) {
  206 |       try {
  207 |         await dayOption.waitFor({ state: 'visible', timeout: 1000 });
  208 |         await dayOption.click();
  209 |         return;
  210 |       } catch {
  211 |         await this.calendar.nextMonthButton.click();
  212 |       }
  213 |     }
  214 | 
> 215 |     throw new Error(`Could not find date ${isoDate} in calendar after 12 month advances`);
      |           ^ Error: Could not find date 2026-09-06 in calendar after 12 month advances
  216 |   }
  217 | }
  218 | 
```