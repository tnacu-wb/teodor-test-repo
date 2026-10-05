# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts >> PI Baseline E2E - Guest PIBA Pay on Arrival Amendment >> Test Book as Guest user: 1 night, 1 room (Double), 2 adults, Flex rate, meals, donations - POA BAC card and Amend by change room type. TestCase ID: 380779.
- Location: qa/tests/regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts:51:7

# Error details

```
Error: locator.scrollIntoViewIfNeeded: Protocol error (DOM.scrollIntoViewIfNeeded): Cannot find context with specified id
Call log:
  - attempting scroll into view action
    - waiting for element to be stable

```

# Page snapshot

```yaml
- generic [active] [ref=f49e1]:
  - generic [ref=f49e3]:
    - banner [ref=f49e4]:
      - generic [ref=f49e7]:
        - link [ref=f49e10] [cursor=pointer]:
          - /url: https://www.uat.premierinn.digital/gb/en/home.html
          - img "Premier Inn Rest Easy" [ref=f49e11]
        - generic [ref=f49e12]:
          - img "English" [ref=f49e15] [cursor=pointer]
          - generic [ref=f49e16]:
            - paragraph [ref=f49e19] [cursor=pointer]: Discover Premier Inn
            - paragraph [ref=f49e22] [cursor=pointer]: Business
            - paragraph [ref=f49e24] [cursor=pointer]: Manage booking
            - generic [ref=f49e26]:
              - button "Log in" [ref=f49e27] [cursor=pointer]
              - button "Sign up" [ref=f49e28] [cursor=pointer]
    - main [ref=f49e29]:
      - generic [ref=f49e31]:
        - group [ref=f49e32] [cursor=pointer]:
          - paragraph [ref=f49e38]: London Heathrow Airport (M4/J4)
          - paragraph [ref=f49e40]: 02 Oct - 03 Oct
          - paragraph [ref=f49e42]: 2 adults, 1 room
        - separator [ref=f49e43]
        - navigation "breadcrumb" [ref=f49e45]:
          - list [ref=f49e46]:
            - listitem [ref=f49e47]:
              - link "Home" [ref=f49e48] [cursor=pointer]:
                - /url: /gb/en/home.html
              - text: /
            - listitem [ref=f49e49]:
              - link "Hotel Directory" [ref=f49e50] [cursor=pointer]:
                - /url: /gb/en/hotels.html
              - text: /
            - listitem [ref=f49e51]:
              - link "England" [ref=f49e52] [cursor=pointer]:
                - /url: /gb/en/hotels/england.html
              - text: /
            - listitem [ref=f49e53]:
              - link "Greater London" [ref=f49e54] [cursor=pointer]:
                - /url: /gb/en/hotels/england/greater-london.html
              - text: /
            - listitem [ref=f49e55]:
              - link "London" [ref=f49e56] [cursor=pointer]:
                - /url: /gb/en/hotels/england/greater-london/london.html
              - text: /
            - listitem [ref=f49e57]:
              - generic [ref=f49e58] [cursor=pointer]: London Heathrow Airport M4j4
        - generic [ref=f49e59]:
          - generic [ref=f49e60]:
            - heading "London Heathrow Airport (M4/J4) hotel" [level=1] [ref=f49e62]
            - generic [ref=f49e63]:
              - generic [ref=f49e64]:
                - img "ta-ratings-img" [ref=f49e65]
                - generic [ref=f49e66] [cursor=pointer]: (2507 reviews)
              - img "Travelers Choice" [ref=f49e67] [cursor=pointer]
            - generic [ref=f49e68]:
              - list [ref=f49e70]:
                - listitem [ref=f49e71]:
                  - generic [ref=f49e72]: New Premier Plus rooms | New restaurant
              - paragraph [ref=f49e75]: A cosy base for stopovers in Surrey, just a 10-minute drive from Heathrow Airport
            - generic [ref=f49e76]: Hotel FacilitiesSee all
            - generic [ref=f49e77]:
              - paragraph [ref=f49e81] [cursor=pointer]: Chargeable onsite parking
              - paragraph [ref=f49e85] [cursor=pointer]: Air conditioning
              - paragraph [ref=f49e89] [cursor=pointer]: Breakfast
              - paragraph [ref=f49e93] [cursor=pointer]: Restaurant
              - paragraph [ref=f49e97] [cursor=pointer]: Free Wi‑Fi
              - paragraph [ref=f49e101] [cursor=pointer]: Accessible
          - generic [ref=f49e103]:
            - generic [ref=f49e104]:
              - generic [ref=f49e105]:
                - generic [ref=f49e106] [cursor=pointer]
                - generic:
                  - img "overlay"
                  - paragraph: New Premier Plus rooms & news restaurant
              - generic [ref=f49e108] [cursor=pointer]
              - generic [ref=f49e110] [cursor=pointer]
            - button "See all photos" [ref=f49e111] [cursor=pointer]
        - generic [ref=f49e115]:
          - heading "Choose your rate" [level=3] [ref=f49e116]
          - paragraph [ref=f49e117]: We know plans can change, so please make sure you book the rate with the right level of flexibility for you.
          - generic [ref=f49e118]:
            - generic [ref=f49e119]:
              - generic [ref=f49e122]:
                - generic [ref=f49e123]:
                  - generic [ref=f49e124]:
                    - generic [ref=f49e125]: Premier Plus room
                    - paragraph [ref=f49e126]: Our enhanced room design. Includes Ultimate Wi-Fi, coffee machine, mini-fridge, bedside USB ports, iron, upgraded workspace & more.
                  - img "Room type Image" [ref=f49e128]
                - generic [ref=f49e130]:
                  - generic [ref=f49e131]:
                    - generic [ref=f49e133]:
                      - generic [ref=f49e135] [cursor=pointer]:
                        - radio "Flex Pay now or on arrival. Fully refundable up to 1 pm on day of arrival" [checked] [ref=f49e136]
                        - generic [ref=f49e139]:
                          - text: Flex
                          - paragraph [ref=f49e140]: Pay now or on arrival. Fully refundable up to 1 pm on day of arrival
                      - generic [ref=f49e141]:
                        - paragraph [ref=f49e143]:
                          - generic "£72" [ref=f49e144]:
                            - generic [ref=f49e145]: £
                            - generic [ref=f49e146]: "72"
                        - paragraph [ref=f49e147]: Total price
                        - generic [ref=f49e148]:
                          - paragraph [ref=f49e149]: 1 room,
                          - paragraph [ref=f49e150]: 1 night
                    - separator [ref=f49e151]
                  - generic [ref=f49e152]:
                    - generic [ref=f49e154]:
                      - generic [ref=f49e156] [cursor=pointer]:
                        - radio "Semi-Flex Pay now, fully refundable up to 3 days before arrival" [ref=f49e157]
                        - generic [ref=f49e160]:
                          - text: Semi-Flex
                          - paragraph [ref=f49e161]: Pay now, fully refundable up to 3 days before arrival
                      - generic [ref=f49e162]:
                        - paragraph [ref=f49e164]:
                          - generic "£63" [ref=f49e165]:
                            - generic [ref=f49e166]: £
                            - generic [ref=f49e167]: "63"
                        - paragraph [ref=f49e168]: Total price
                        - generic [ref=f49e169]:
                          - paragraph [ref=f49e170]: 1 room,
                          - paragraph [ref=f49e171]: 1 night
                    - separator [ref=f49e172]
                  - generic [ref=f49e175]:
                    - generic [ref=f49e177] [cursor=pointer]:
                      - radio "Standard Pay now, non-refundable. Check-in date amendable before 1pm on day of arrival" [ref=f49e178]
                      - generic [ref=f49e181]:
                        - text: Standard
                        - paragraph [ref=f49e182]: Pay now, non-refundable. Check-in date amendable before 1pm on day of arrival
                    - generic [ref=f49e183]:
                      - paragraph [ref=f49e185]:
                        - generic "£52" [ref=f49e186]:
                          - generic [ref=f49e187]: £
                          - generic [ref=f49e188]: "52"
                      - paragraph [ref=f49e189]: Total price
                      - generic [ref=f49e190]:
                        - paragraph [ref=f49e191]: 1 room,
                        - paragraph [ref=f49e192]: 1 night
              - generic [ref=f49e195]:
                - generic [ref=f49e196]:
                  - generic [ref=f49e197]:
                    - generic [ref=f49e198]: Premier Plus room with a view
                    - paragraph [ref=f49e199]: Our enhanced room design with a great view, Ultimate Wi-Fi, coffee machine, mini-fridge, bedside USB ports, iron, upgraded workspace & more.
                  - img "Room type Image" [ref=f49e201]
                - generic [ref=f49e203]:
                  - generic [ref=f49e204]:
                    - generic [ref=f49e206]:
                      - generic [ref=f49e208] [cursor=pointer]:
                        - radio "Flex Pay now or on arrival. Fully refundable up to 1 pm on day of arrival" [ref=f49e209]
                        - generic [ref=f49e212]:
                          - text: Flex
                          - paragraph [ref=f49e213]: Pay now or on arrival. Fully refundable up to 1 pm on day of arrival
                      - generic [ref=f49e214]:
                        - paragraph [ref=f49e216]:
                          - generic "£72" [ref=f49e217]:
                            - generic [ref=f49e218]: £
                            - generic [ref=f49e219]: "72"
                        - paragraph [ref=f49e220]: Total price
                        - generic [ref=f49e221]:
                          - paragraph [ref=f49e222]: 1 room,
                          - paragraph [ref=f49e223]: 1 night
                    - separator [ref=f49e224]
                  - generic [ref=f49e225]:
                    - generic [ref=f49e227]:
                      - generic [ref=f49e229] [cursor=pointer]:
                        - radio "Semi-Flex Pay now, fully refundable up to 3 days before arrival" [ref=f49e230]
                        - generic [ref=f49e233]:
                          - text: Semi-Flex
                          - paragraph [ref=f49e234]: Pay now, fully refundable up to 3 days before arrival
                      - generic [ref=f49e235]:
                        - paragraph [ref=f49e237]:
                          - generic "£63" [ref=f49e238]:
                            - generic [ref=f49e239]: £
                            - generic [ref=f49e240]: "63"
                        - paragraph [ref=f49e241]: Total price
                        - generic [ref=f49e242]:
                          - paragraph [ref=f49e243]: 1 room,
                          - paragraph [ref=f49e244]: 1 night
                    - separator [ref=f49e245]
                  - generic [ref=f49e248]:
                    - generic [ref=f49e250] [cursor=pointer]:
                      - radio "Standard Pay now, non-refundable. Check-in date amendable before 1pm on day of arrival" [ref=f49e251]
                      - generic [ref=f49e254]:
                        - text: Standard
                        - paragraph [ref=f49e255]: Pay now, non-refundable. Check-in date amendable before 1pm on day of arrival
                    - generic [ref=f49e256]:
                      - paragraph [ref=f49e258]:
                        - generic "£52" [ref=f49e259]:
                          - generic [ref=f49e260]: £
                          - generic [ref=f49e261]: "52"
                      - paragraph [ref=f49e262]: Total price
                      - generic [ref=f49e263]:
                        - paragraph [ref=f49e264]: 1 room,
                        - paragraph [ref=f49e265]: 1 night
              - generic [ref=f49e268]:
                - generic [ref=f49e269]:
                  - generic [ref=f49e270]:
                    - generic [ref=f49e271]: Standard room
                    - paragraph [ref=f49e272]: A super-comfy bed, a power shower and free Wi-Fi – our double rooms have everything you’ll need for a great night’s sleep.
                    - paragraph [ref=f49e275]: See details
                  - img "Room type Image" [ref=f49e277]
                - generic [ref=f49e279]:
                  - generic [ref=f49e280]:
                    - generic [ref=f49e282]:
                      - generic [ref=f49e284] [cursor=pointer]:
                        - radio "Flex Pay now or on arrival. Fully refundable up to 1 pm on day of arrival" [ref=f49e285]
                        - generic [ref=f49e288]:
                          - text: Flex
                          - paragraph [ref=f49e289]: Pay now or on arrival. Fully refundable up to 1 pm on day of arrival
                      - generic [ref=f49e290]:
                        - paragraph [ref=f49e292]:
                          - generic "£72" [ref=f49e293]:
                            - generic [ref=f49e294]: £
                            - generic [ref=f49e295]: "72"
                        - paragraph [ref=f49e296]: Total price
                        - generic [ref=f49e297]:
                          - paragraph [ref=f49e298]: 1 room,
                          - paragraph [ref=f49e299]: 1 night
                    - separator [ref=f49e300]
                  - generic [ref=f49e301]:
                    - generic [ref=f49e303]:
                      - generic [ref=f49e305] [cursor=pointer]:
                        - radio "Semi-Flex Pay now, fully refundable up to 3 days before arrival" [ref=f49e306]
                        - generic [ref=f49e309]:
                          - text: Semi-Flex
                          - paragraph [ref=f49e310]: Pay now, fully refundable up to 3 days before arrival
                      - generic [ref=f49e311]:
                        - paragraph [ref=f49e313]:
                          - generic "£63" [ref=f49e314]:
                            - generic [ref=f49e315]: £
                            - generic [ref=f49e316]: "63"
                        - paragraph [ref=f49e317]: Total price
                        - generic [ref=f49e318]:
                          - paragraph [ref=f49e319]: 1 room,
                          - paragraph [ref=f49e320]: 1 night
                    - separator [ref=f49e321]
                  - generic [ref=f49e324]:
                    - generic [ref=f49e326] [cursor=pointer]:
                      - radio "Standard Pay now, non-refundable. Check-in date amendable before 1pm on day of arrival" [ref=f49e327]
                      - generic [ref=f49e330]:
                        - text: Standard
                        - paragraph [ref=f49e331]: Pay now, non-refundable. Check-in date amendable before 1pm on day of arrival
                    - generic [ref=f49e332]:
                      - paragraph [ref=f49e334]:
                        - generic "£52" [ref=f49e335]:
                          - generic [ref=f49e336]: £
                          - generic [ref=f49e337]: "52"
                      - paragraph [ref=f49e338]: Total price
                      - generic [ref=f49e339]:
                        - paragraph [ref=f49e340]: 1 room,
                        - paragraph [ref=f49e341]: 1 night
            - generic [ref=f49e345]:
              - paragraph [ref=f49e346]: Double room
              - paragraph [ref=f49e347]: Flex
              - separator [ref=f49e348]
              - generic [ref=f49e349]:
                - generic [ref=f49e350]:
                  - paragraph [ref=f49e351]: Stay
                  - paragraph [ref=f49e352]: 1 night
                - generic [ref=f49e353]:
                  - paragraph [ref=f49e354]: Price
                  - generic [ref=f49e355]:
                    - link "See breakdown" [ref=f49e356] [cursor=pointer]:
                      - /url: "#"
                    - paragraph [ref=f49e357]: £72
              - separator [ref=f49e358]
              - generic [ref=f49e359]:
                - paragraph [ref=f49e360]: Total
                - paragraph [ref=f49e361]:
                  - generic "£72" [ref=f49e362]:
                    - generic [ref=f49e363]: £
                    - generic [ref=f49e364]: "72"
              - button "Book now" [ref=f49e365] [cursor=pointer]
              - generic [ref=f49e366]: Enter your code
        - generic [ref=f49e371]:
          - heading "Location" [level=3] [ref=f49e372]
          - generic [ref=f49e375]:
            - generic [ref=f49e376]:
              - paragraph [ref=f49e377]: Shepiston Lane, Middlesex,
              - paragraph [ref=f49e378]: UB3 1RW
            - generic [ref=f49e379]:
              - generic [ref=f49e380]: Sat Nav Directions:UB3 1LL
              - generic [ref=f49e381]: What 3 Words:///stiff.amused.pumps
            - generic [ref=f49e382]:
              - paragraph [ref=f49e383]: "Directions:"
              - paragraph [ref=f49e386]: From M4 exit Jtn 4 then follow signs to Uxbridge remaining in left hand lane. Bear left following signs for other routes and Hayes. At give way point, use as a roundabout and take the 4th exit off (sign posted Hayes). The hotel is 200 yards away on the right hand side. (SAT NAV - UB3 1RW) Parking costs 12 GBP per night for Premier Inn guests.
              - paragraph [ref=f49e388] [cursor=pointer]: Read more
            - generic [ref=f49e389]:
              - paragraph [ref=f49e390]: "Transport and local information:"
              - list [ref=f49e392]:
                - listitem [ref=f49e393]:
                  - paragraph [ref=f49e394]: Heathrow Terminals 2&3 - 2.5 miles
                - listitem [ref=f49e395]:
                  - paragraph [ref=f49e396]: Heathrow Terminal 4 - 5.5 miles
                - listitem [ref=f49e397]:
                  - paragraph [ref=f49e398]: Heathrow Terminal 5 - 4.5 miles
        - generic [ref=f49e400]:
          - heading "Parking at Premier Inn London Heathrow Airport (M4/J4) hotel" [level=3] [ref=f49e401]
          - generic [ref=f49e402]: We offer an exclusive guest parking discount of £12 per 24 hours. Simply find a space and pay less to park. This car park is managed by Horizon Parking. Parking is avaliable on a first, come first serve basis. For larger vehicles such as coaches, vans or lorries, the parking fee is £30. This must be arranged with the hotel via email, as we can only accommodate up to 3 larger vehicles at a time. Parking availability can vary, if you have any questions please contact the hotel prior to your stay. Airport parking is available through our partners Holiday Extras at great prices (see frequently asked questions section for more details).
        - generic "Restaurant" [ref=f49e404]:
          - heading "Restaurant" [level=3] [ref=f49e405]
          - img "Thyme Bar & Grill" [ref=f49e407]
          - status [ref=f49e408]:
            - generic [ref=f49e412]: Depending on your selected hotel and dates you stay, we will be offering different breakfast and evening options.
          - generic [ref=f49e414]:
            - tablist [ref=f49e415]:
              - tab [selected] [ref=f49e416] [cursor=pointer]:
                - heading "Breakfast" [level=3] [ref=f49e418]
              - tab [ref=f49e419] [cursor=pointer]:
                - heading "Lunch & Dinner" [level=3] [ref=f49e421]
              - tab [ref=f49e422] [cursor=pointer]:
                - heading "Meal Deal" [level=3] [ref=f49e424]
            - tabpanel "Breakfast" [ref=f49e426]:
              - generic [ref=f49e427]:
                - img "Breakfast" [ref=f49e429]
                - generic [ref=f49e430]:
                  - generic [ref=f49e431]:
                    - paragraph [ref=f49e435]: Mornings have never been so tasty! Build your own breakfast and fill up your plate with freshly cooked favourites like bacon, sausages, eggs and hash browns – plus a tasty selection of veggie and vegan options – and continental delights like fruit, cereal and freshly baked pastries. Plus, when an adult orders a Premier Inn Breakfast, up to two kids eat breakfast for free**
                    - text: Read more
                  - button "Breakfast Menu" [ref=f49e437] [cursor=pointer]
        - generic [ref=f49e439]:
          - heading "Reviews for Premier Inn London Heathrow Airport (M4/J4) hotel" [level=3] [ref=f49e440]
          - generic [ref=f49e441]:
            - button "See reviews" [ref=f49e444] [cursor=pointer]
            - generic [ref=f49e448]:
              - paragraph [ref=f49e449]: Traveller reviews brought to you by
              - generic [ref=f49e451]:
                - paragraph [ref=f49e454]: Location
                - paragraph [ref=f49e458]: Sleep Quality
                - paragraph [ref=f49e462]: Rooms
                - paragraph [ref=f49e466]: Service
                - paragraph [ref=f49e470]: Value
                - paragraph [ref=f49e474]: Cleanliness
              - paragraph [ref=f49e477]: Write Review
        - generic [ref=f49e478]:
          - generic [ref=f49e481]:
            - heading "Hotel description" [level=3] [ref=f49e482]
            - paragraph [ref=f49e484]:
              - text: For comfy, stress-free accommodation before jetting off on holiday, look no further than our Premier Inn London Heathrow Airport (M4/J4) hotel. We’re based just off the M4 near Hayes & Harlington station, just a 10-minute drive from Heathrow Terminals 2 and 3, a 20-minute drive from Heathrow Terminal 4, and a 15-minute drive from Terminal 5. If you have some time before take-off, why not visit Twickenham High Street for some retail therapy or
              - link "Twickenham Stadium" [ref=f49e485] [cursor=pointer]:
                - /url: https://www.premierinn.com/gb/en/things-to-do/twickenham/twickenham-stadium.html
              - text: for a rugby match? Popular Surrey attractions like Thorpe Park and Windsor Castle are just a 20-minute drive away too! Our Premier Inn at London Heathrow Airport is an ideal place to stay before Heathrow flights or if you’re just looking for hotels near Ashford. Plus, thanks to our in-house Thyme restaurant and
              - link "super-comfy beds" [ref=f49e486] [cursor=pointer]:
                - /url: https://www.premierinn.com/gb/en/sleep/our-bed.html
              - text: ", you’ll have everything you need for great-value stopovers."
          - generic [ref=f49e489]:
            - heading "London Heathrow Airport (M4/J4) FAQs" [level=3] [ref=f49e490]
            - generic [ref=f49e492]:
              - button "Can you walk from Heathrow airport to Premier Inn London Heathrow Airport (M4/J4) hotel?" [ref=f49e494] [cursor=pointer]
              - button "How do I get from Heathrow Terminal 4 to your Premier Inn London Heathrow Airport (M4/J4) hotel?" [ref=f49e499] [cursor=pointer]
              - button "Does your Premier Inn London Heathrow Airport (M4/J4) hotel have parking?" [ref=f49e504] [cursor=pointer]
              - button "How do I add airport parking to my booking?" [ref=f49e509] [cursor=pointer]
              - button "Can I book airport lounges, fast passes and other extras?" [ref=f49e514] [cursor=pointer]
              - button "What time is breakfast at your Premier Inn London Heathrow Airport (M4/J4) hotel?" [ref=f49e519] [cursor=pointer]
              - button "Which room amenities are available at your Premier Inn London Heathrow Airport (M4/J4) hotel?" [ref=f49e524] [cursor=pointer]
              - button "Is your Premier Inn London Heathrow Airport (M4/J4) hotel accessible?" [ref=f49e529] [cursor=pointer]
              - button "Why book with Premier Inn?" [ref=f49e534] [cursor=pointer]
              - button "Why is booking with Premier Inn the right choice for families?" [ref=f49e539] [cursor=pointer]
              - button "Why is booking with Premier Inn the right choice for business travellers?" [ref=f49e544] [cursor=pointer]
        - generic [ref=f49e548]:
          - heading "Hotel contact information" [level=3] [ref=f49e549]
          - paragraph [ref=f49e550]: "Phone: 0333 003 1715"
          - generic [ref=f49e551]: Calls to our UK hotels cost no more than calls to standard 01 or 02 numbers and are usually included in landline or mobile call packages. The same applies to calls to our hotels in the Republic of Ireland. For hotels outside the UK and Ireland, standard international call charges may apply – please check with your provider.
    - generic [ref=f49e555]:
      - generic [ref=f49e556]:
        - tablist [ref=f49e557]:
          - tab [selected] [ref=f49e558] [cursor=pointer]:
            - heading "About us" [level=3] [ref=f49e560]
          - tab [ref=f49e561] [cursor=pointer]:
            - heading "City breaks" [level=3] [ref=f49e563]
          - tab [ref=f49e564] [cursor=pointer]:
            - heading "Summer breaks" [level=3] [ref=f49e566]
          - tab [ref=f49e567] [cursor=pointer]:
            - heading "Winter breaks" [level=3] [ref=f49e569]
          - tab [ref=f49e570] [cursor=pointer]:
            - heading "Business" [level=3] [ref=f49e572]
        - tabpanel "About us" [ref=f49e574]:
          - generic [ref=f49e575]:
            - paragraph [ref=f49e577]: Is it our comfy beds, our seriously tasty food, our great value hotels or our amazing teams that guests love so much? We reckon it’s a bit of everything. Take a look around to find out why we’re a much-loved, award-winning hotel chain, and rest easy knowing our range of rates give you both choice and flexibility.
            - generic [ref=f49e578]:
              - generic [ref=f49e579]:
                - paragraph [ref=f49e580]: Get in touch
                - generic [ref=f49e581]:
                  - link "Contact us" [ref=f49e582] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/contact-us.html
                  - link "FAQs" [ref=f49e583] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/faq.html
                  - link "Group bookings" [ref=f49e584] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why/groups.html
                  - link "Affiliates" [ref=f49e585] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/premier-inn-affiliate-programme.html
                  - link "International development" [ref=f49e586] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/business/international-development.html
                  - link "Careers" [ref=f49e587] [cursor=pointer]:
                    - /url: https://www.whitbreadcareers.com/our-brands/premier-inn/
              - generic [ref=f49e588]:
                - paragraph [ref=f49e589]: Legal
                - generic [ref=f49e590]:
                  - link "Terms and conditions" [ref=f49e591] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/terms/booking-terms-and-conditions.html
                  - link "Terms of use" [ref=f49e592] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/terms/terms-of-use.html
                  - link "Privacy policy" [ref=f49e593] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/terms/privacy-policy.html
                  - link "Cookies notice" [ref=f49e594] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/terms/how-we-use-cookies.html
                  - link "Good Night Guarantee" [ref=f49e595] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/sleep/good-night-guarantee.html
                  - link "Modern Slavery Act statement" [ref=f49e596] [cursor=pointer]:
                    - /url: https://cdn.whitbread.co.uk/media/2023/05/23926_MSA-Report-2022-23_Stage2_230510_14.48-Final-High-Res.pdf
              - generic [ref=f49e597]:
                - paragraph [ref=f49e598]: Locations
                - generic [ref=f49e599]:
                  - link "Hotel directory" [ref=f49e600] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/hotels.html
                  - link "New hotels" [ref=f49e601] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                  - link "Local guides" [ref=f49e602] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/city-breaks.html
                  - link "Short breaks" [ref=f49e603] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/short-breaks.html
                  - link "Hotels in Germany" [ref=f49e604] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                  - link "Dubai and beyond" [ref=f49e605] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
              - generic [ref=f49e606]:
                - paragraph [ref=f49e607]: Our hotels
                - generic [ref=f49e608]:
                  - link "Our rooms" [ref=f49e609] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/sleep/our-rooms.html
                  - link "Family friendly" [ref=f49e610] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
                  - link "Sleep" [ref=f49e611] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/sleep.html
                  - link "Food & drink" [ref=f49e612] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
                  - link "hub by Premier Inn" [ref=f49e613] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                  - link "ZIP by Premier Inn" [ref=f49e614] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/zip.html
              - generic [ref=f49e615]:
                - paragraph [ref=f49e616]: Find out more
                - generic [ref=f49e617]:
                  - link "About us" [ref=f49e618] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why.html
                  - link "Rest easy" [ref=f49e619] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/resteasy.html
                  - link "GOSH Charity" [ref=f49e620] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why/gosh-childrens-charity.html
                  - link "Force for Good" [ref=f49e621] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why/force-for-good.html
                  - link "Disabled access" [ref=f49e622] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/terms/disabled-access.html
                  - link "News" [ref=f49e623] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/news.html
              - generic [ref=f49e624]:
                - paragraph [ref=f49e625]: Everything else
                - generic [ref=f49e626]:
                  - link "Our rates" [ref=f49e627] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why/rates.html
                  - link "Offers" [ref=f49e628] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/offers.html
                  - link "Buy our bed" [ref=f49e629] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/sleep/buy-our-bed.html
                  - link "Mobile apps" [ref=f49e630] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why/apps.html
                  - link "We value difference" [ref=f49e631] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/terms/diversity-and-inclusion.html
                  - link "Sitemap" [ref=f49e632] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/sitemap.html
      - generic [ref=f49e633]:
        - generic [ref=f49e634]: © 2026 Premier Inn
        - generic [ref=f49e635]:
          - link [ref=f49e636] [cursor=pointer]:
            - /url: https://www.facebook.com/premierinn
          - link [ref=f49e638] [cursor=pointer]:
            - /url: https://twitter.com/premierinn
          - link [ref=f49e640] [cursor=pointer]:
            - /url: https://www.instagram.com/premierinn
  - alert [ref=f49e642]
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
  22  |   readonly summaryTitleLabel: Locator = this.page.locator('p[data-testid="hdp_basketRoomAndRatePlan"]');
  23  |   readonly summaryNumberOfRoomsLabel: Locator = this.page.locator('p[data-testid="hdp_basketNrOfRooms"]');
  24  |   readonly staySummaryLabel: Locator = this.page.locator('p[data-testid="hdp_basketStayText"]');
  25  |   readonly priceSummaryLabel: Locator = this.page.locator('p[data-testid="hdp_basketPriceText"]');
  26  |   readonly nightsPeriodSummaryLabel: Locator = this.page.locator('p[data-testid="hdp_basketNrOfNightsStay"]');
  27  |   readonly totalSummaryLabel: Locator = this.page.locator('p[data-testid="total-cost-title"]');
  28  |   readonly totalPriceSummaryLabel: Locator = this.page.locator('p[data-testid="mobile-total-cost"], p[data-testid="total-cost"]');
  29  |   readonly cityTaxExemptLabel: Locator = this.page.locator('p[data-testid="hdp_basketCityTaxExempt"]');
  30  |   readonly basketContainer: Locator = this.page.locator(
  31  |     Constants.BROWSER_RESOLUTIONS.isDesktop() ? '[data-testid="basket"]' : '[data-testid="mobile-basket"]'
  32  |   );
  33  |   readonly basketStayPriceContainer: Locator = this.page.locator('[data-testid="hdp_basketStayPrice"]');
  34  |   readonly priceBreakdownRows: Locator = this.page.locator(
  35  |     Constants.BROWSER_RESOLUTIONS.isDesktop()
  36  |       ? '[data-testid="hdp_basketBreakdownRow"]:visible'
  37  |       : '[data-testid="hdp_mobileBasketBreakdownRow"]:visible'
  38  |   );
  39  |   readonly priceValueSummaryLabel: Locator = this.page.locator('[data-testid="total-cost-for-nights"]');
  40  |   readonly summaryRateLabel: Locator = this.page.locator('//p[@data-testid="hdp_basketRoomAndRatePlan"]/parent::div/p[2]');
  41  |   readonly seeBreakdownLink: Locator = this.page.locator(
  42  |     'a[data-testid="hdp_mobileBasketSeeBreakdownLink"]:visible, a[data-testid="hdp_basketSeeBreakdownLink"]:visible'
  43  |   ).first();
  44  |   readonly bookNowButton: Locator = this.basketContainer.locator('button').first();
  45  |   readonly chooseRoomTypeButton: Locator = this.page.locator(
  46  |     '[data-testid="hdp_basketChooseRoomTypeButton"], [data-testid="hdp_mobileBasketChooseRoomTypeButton"]'
  47  |   );
  48  |   readonly keepYourBookingButton: Locator = this.page.locator(
  49  |     'button[data-testid="hdp_roomUpgradeModalPopup-SecondaryButton"], button[data-testid="hdp_roomUpgradeModalPopup_SecondaryButton"]'
  50  |   );
  51  | 
  52  |   /** Extract displayed per-night prices and dates for one selected room. */
  53  |   async extractPricesAndDatesPerNight(options: { rooms: Array<unknown> }): Promise<PricePerNight[][]> {
  54  |     console.log('Extracting prices and dates per night');
  55  |     await expect(this.priceBreakdownRows.first(), 'Price breakdown rows should be displayed').toBeVisible({ timeout: 10000 });
  56  |     if (options.rooms.length !== 1) {
  57  |       throw new Error('extractPricesAndDatesPerNight currently supports the single-room HDP breakdown used by this baseline');
  58  |     }
  59  | 
  60  |     const pricesAndDates: PricePerNight[] = [];
  61  |     const seenRows = new Set<string>();
  62  |     for (let index = 0; index < await this.priceBreakdownRows.count(); index++) {
  63  |       const rowSpans = this.priceBreakdownRows.nth(index).locator('span');
  64  |       const dateText = (await rowSpans.nth(0).innerText()).trim();
  65  |       const normalizedDateText = dateText
  66  |         .replace(/^[A-Za-z]{2,3}\.?(?=\s)/, '')
  67  |         .replace('Mär.', 'Mar')
  68  |         .replace('Mai', 'May')
  69  |         .replace('Okt.', 'Oct')
  70  |         .replace('Dez.', 'Dec');
  71  |       const parsedDate = new Date(normalizedDateText);
  72  |       if (Number.isNaN(parsedDate.getTime())) throw new Error(`Could not parse HDP price breakdown date: ${dateText}`);
  73  |       const pricePerNight = PriceHelpers.getPriceAmountFromUiLabel(await rowSpans.nth(1).innerText());
  74  |       const rowKey = `${parsedDate.toISOString().slice(0, 10)}:${pricePerNight}`;
  75  |       if (seenRows.has(rowKey)) continue;
  76  |       seenRows.add(rowKey);
  77  |       pricesAndDates.push({
  78  |         pricePerNight,
  79  |         date: parsedDate.toISOString().slice(0, 10),
  80  |       });
  81  |     }
  82  |     return [pricesAndDates];
  83  |   }
  84  | 
  85  |   /** Sum displayed per-night prices for each selected room. */
  86  |   async getTotalRoomPrice(options: { rooms: Array<unknown> }): Promise<number[]> {
  87  |     console.log('Getting total room price from HDP breakdown');
  88  |     const pricesPerRoom = await this.extractPricesAndDatesPerNight(options);
  89  |     return pricesPerRoom.map(roomPrices => Number(roomPrices.reduce((total, price) => total + price.pricePerNight, 0).toFixed(2)));
  90  |   }
  91  | 
  92  |   /** Return the total price shown for the selected nights. */
  93  |   async getPriceBreakdownTotal(): Promise<string> {
  94  |     console.log('Getting price breakdown total');
  95  |     await this.priceValueSummaryLabel.waitFor({ state: 'visible', timeout: 30000 });
  96  |     return this.priceValueSummaryLabel.innerText();
  97  |   }
  98  | 
  99  |   // ######## UI actions/navigation ########
  100 | 
  101 |   /** Expand the booking summary price breakdown. */
  102 |   async clickSeeBreakdownLink(): Promise<void> {
  103 |     console.log('Clicking see breakdown link');
  104 |     await this.seeBreakdownLink.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
  105 |     await this.seeBreakdownLink.scrollIntoViewIfNeeded();
  106 |     await this.page.waitForTimeout(2000);
  107 |     await this.seeBreakdownLink.waitFor({ state: 'visible', timeout: 30000 });
  108 |     await this.seeBreakdownLink.evaluate((element) => {
  109 |       const anchor = element as HTMLAnchorElement;
  110 |       const href = anchor.getAttribute('href');
  111 |       anchor.removeAttribute('href');
  112 |       anchor.click();
  113 |       if (href !== null) window.setTimeout(() => anchor.setAttribute('href', href), 0);
  114 |     });
  115 |     await expect(this.priceBreakdownRows.first(), 'Price breakdown rows should be displayed').toBeVisible({ timeout: 10000 });
  116 |   }
  117 | 
  118 |   /** Click Book now from the booking summary. */
  119 |   async clickBookNow(): Promise<void> {
  120 |     console.log('Clicking book now button');
  121 |     await this.bookNowButton.waitFor({ state: 'visible', timeout: 30000 });
> 122 |     await this.bookNowButton.scrollIntoViewIfNeeded();
      |                              ^ Error: locator.scrollIntoViewIfNeeded: Protocol error (DOM.scrollIntoViewIfNeeded): Cannot find context with specified id
  123 |     await this.page.waitForTimeout(2000);
  124 |     await this.bookNowButton.click();
  125 |   }
  126 | 
  127 |   /** Dismiss the optional Premier Plus room-upgrade modal when it is displayed. */
  128 |   async closePremierPlusRoomUpgradeModalIfPresent(): Promise<void> {
  129 |     console.log('Closing Premier Plus upgrade modal if present');
  130 |     try {
  131 |       await this.keepYourBookingButton.waitFor({ state: 'visible', timeout: 5000 });
  132 |       await this.keepYourBookingButton.click();
  133 |     } catch {
  134 |       // The optional upgrade modal is not displayed.
  135 |     }
  136 |   }
  137 | 
  138 |   /** Click the booking summary room-type chooser. */
  139 |   async clickChooseRoomTypeButton(): Promise<void> {
  140 |     console.log("Click on the 'choose room type' button");
  141 |     await this.chooseRoomTypeButton.scrollIntoViewIfNeeded();
  142 |     await this.chooseRoomTypeButton.click();
  143 |   }
  144 | 
  145 |   // ######## UI validations ########
  146 | 
  147 |   /** Validate the basket summary title/room-and-rate-plan label matches the expected text. */
  148 |   async validateSummaryTitle(expectedRateAndRoom: string): Promise<void> {
  149 |     await expect(this.summaryTitleLabel, 'Booking summary rate/room label').toContainText(expectedRateAndRoom);
  150 |   }
  151 | 
  152 |   /** Validate the number of rooms shown in the basket summary. */
  153 |   async validateNumberOfRooms(expectedRoomsCount: number): Promise<void> {
  154 |     await expect(this.summaryNumberOfRoomsLabel, 'Booking summary rooms count').toContainText(`${expectedRoomsCount}`);
  155 |   }
  156 | 
  157 |   /** Validate the last-few-rooms notification is displayed, or not. */
  158 |   async validateLastFewRoomsIsDisplayed(isDisplayed = true): Promise<void> {
  159 |     if (isDisplayed) {
  160 |       await expect(this.lastFewRoomsLabel, 'Last few rooms label').toBeVisible();
  161 |     } else {
  162 |       await expect(this.lastFewRoomsLabel, 'Last few rooms label').not.toBeVisible();
  163 |     }
  164 |   }
  165 | 
  166 |   /** Validate the selected booking summary rate plan. */
  167 |   async validateBookingSummaryRatePlan(options: { ratePlan: string; roomsCount?: number }): Promise<void> {
  168 |     const { ratePlan, roomsCount = 1 } = options;
  169 |     console.log(`Validating booking summary rate plan: ${ratePlan}`);
  170 |     if (!Constants.BROWSER_RESOLUTIONS.isDesktop()) return;
  171 |     if (roomsCount > 1) {
  172 |       await expect(this.summaryNumberOfRoomsLabel, 'Booking summary rooms count should match').toContainText(`${roomsCount}`);
  173 |       await expect(this.summaryNumberOfRoomsLabel, 'Booking summary rate plan should match').toContainText(ratePlan);
  174 |       return;
  175 |     }
  176 |     await expect(this.summaryRateLabel, 'Booking summary selected rate should match').toHaveText(ratePlan);
  177 |   }
  178 | 
  179 |   /** Validate that the booking summary total price is displayed. */
  180 |   async validateTotalPriceRate(isDisplayed = true): Promise<void> {
  181 |     console.log('Validating total price rate');
  182 |     if (isDisplayed) {
  183 |       await expect(this.totalPriceSummaryLabel, 'Total price summary should be displayed').toBeVisible();
  184 |       await expect(this.totalPriceSummaryLabel, 'Total price summary should contain currency and amount').toHaveText(/[£€$]\s*\d+/);
  185 |       return;
  186 |     }
  187 |     await expect(this.totalPriceSummaryLabel, 'Total price summary should not be displayed').not.toBeVisible();
  188 |   }
  189 | 
  190 |   /** Validate the expected per-night breakdown row count and prices. */
  191 |   async validatePricesPerNight(options: { numberOfDays: number; numberOfRooms: number; hotelCurrency: string }): Promise<void> {
  192 |     console.log('Validating prices per night in HDP breakdown');
  193 |     await expect(this.priceBreakdownRows, `Price breakdown should show ${options.numberOfDays} rows per room`).toHaveCount(
  194 |       options.numberOfDays * options.numberOfRooms,
  195 |       { timeout: 10000 }
  196 |     );
  197 |     const expectedSymbol = options.hotelCurrency.toUpperCase() === 'GBP' || options.hotelCurrency.toLowerCase() === 'gb' ? '£' : '€';
  198 |     for (let index = 0; index < await this.priceBreakdownRows.count(); index++) {
  199 |       const priceText = await this.priceBreakdownRows.nth(index).locator('span').nth(1).innerText();
  200 |       expect(priceText, `Price breakdown row ${index} should contain ${expectedSymbol}`).toContain(expectedSymbol);
  201 |       expect(PriceHelpers.getPriceAmountFromUiLabel(priceText), `Price breakdown row ${index} should contain a positive price`).toBeGreaterThan(0);
  202 |     }
  203 |   }
  204 | 
  205 |   /** Validate the booking summary price-breakdown section and total. */
  206 |   async validatePriceBreakdown(): Promise<void> {
  207 |     console.log('Validating price breakdown section');
  208 |     await this.basketStayPriceContainer.waitFor({ state: 'visible', timeout: 30000 });
  209 |     await expect(this.priceValueSummaryLabel, 'Price breakdown total should be displayed').toBeVisible();
  210 |     await expect(this.priceValueSummaryLabel, 'Price breakdown should contain currency and amount').toHaveText(/[£€$]\s*\d+/);
  211 |   }
  212 | }
  213 | 
```