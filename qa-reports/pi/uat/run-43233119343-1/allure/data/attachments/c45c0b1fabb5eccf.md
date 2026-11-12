# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-logged-user-uk-hotel-semi-flex-poa-shorten-period.spec.ts >> End2end: Amend, Logged in user, PI UK Hotel, Semi-Flex, pay now, shorten period >> Test Amend, Logged in user: shorten period and verify changes. TestCase ID: 266200.
- Location: tests/regressions/pi/baseline-e2e-logged-user-uk-hotel-semi-flex-poa-shorten-period.spec.ts:42:7

# Error details

```
GraphQLError: GraphQL error (retryable) for query "basket": Expected Iterable, but did not find one for field "BasketItem.details".
```

# Page snapshot

```yaml
- generic [ref=f11e1]:
  - generic [ref=f11e3]:
    - banner [ref=f11e4]:
      - generic [ref=f11e7]:
        - link [ref=f11e10] [cursor=pointer]:
          - /url: https://www.uat.premierinn.digital/gb/en/home.html
        - generic [ref=f11e12]:
          - button [ref=f11e14] [cursor=pointer]
          - generic [ref=f11e16]:
            - paragraph [ref=f11e19] [cursor=pointer]: Discover Premier Inn
            - paragraph [ref=f11e22] [cursor=pointer]: Business
            - paragraph [ref=f11e24] [cursor=pointer]: Manage booking
            - paragraph [ref=f11e27] [cursor=pointer]: test Testersons
    - main [ref=f11e28]:
      - generic [ref=f11e30]:
        - group [ref=f11e31] [cursor=pointer]:
          - paragraph [ref=f11e37]: London Gatwick Airport South (London Road)
          - paragraph [ref=f11e39]: 12 Nov - 14 Nov
          - paragraph [ref=f11e41]: 1 adult, 1 room
        - separator [ref=f11e42]
        - navigation [ref=f11e44]:
          - list [ref=f11e45]:
            - listitem [ref=f11e46]:
              - link [ref=f11e47] [cursor=pointer]:
                - /url: /gb/en/home.html
                - text: Home
              - text: /
            - listitem [ref=f11e48]:
              - link [ref=f11e49] [cursor=pointer]:
                - /url: /gb/en/hotels.html
                - text: Hotel Directory
              - text: /
            - listitem [ref=f11e50]:
              - link [ref=f11e51] [cursor=pointer]:
                - /url: /gb/en/hotels/england.html
                - text: England
              - text: /
            - listitem [ref=f11e52]:
              - link [ref=f11e53] [cursor=pointer]:
                - /url: /gb/en/hotels/england/west-sussex.html
                - text: West Sussex
              - text: /
            - listitem [ref=f11e54]:
              - link [ref=f11e55] [cursor=pointer]:
                - /url: /gb/en/hotels/england/west-sussex/crawley.html
                - text: Crawley
              - text: /
            - listitem [ref=f11e56]:
              - generic [ref=f11e57] [cursor=pointer]: London Gatwick Airport South London Road
        - generic [ref=f11e58]:
          - generic [ref=f11e59]:
            - heading [level=1] [ref=f11e61]: London Gatwick Airport South (London Road) hotel
            - generic [ref=f11e62]: (2650 reviews)
            - generic [ref=f11e66]:
              - list [ref=f11e68]:
                - listitem [ref=f11e69]:
                  - generic [ref=f11e70]: Exclusive Dinner Offer - Website Only
              - paragraph [ref=f11e72]: A 5-minute drive from Gatwick Airport South and North Terminals with free on-site parking during your stay
            - generic [ref=f11e73]: Hotel FacilitiesSee all
            - generic [ref=f11e74]:
              - paragraph [ref=f11e78] [cursor=pointer]: Free parking
              - paragraph [ref=f11e82] [cursor=pointer]: Breakfast
              - paragraph [ref=f11e86] [cursor=pointer]: Restaurant
              - paragraph [ref=f11e90] [cursor=pointer]: Free Wi‑Fi
              - paragraph [ref=f11e94] [cursor=pointer]: Accessible
              - paragraph [ref=f11e98] [cursor=pointer]: Add airport parking with Holiday Extras
          - generic [ref=f11e100]:
            - generic [ref=f11e101]:
              - generic [ref=f11e103] [cursor=pointer]
              - generic [ref=f11e105] [cursor=pointer]
              - generic [ref=f11e107] [cursor=pointer]
            - button [ref=f11e108] [cursor=pointer]:
              - generic [ref=f11e109]: See all photos
        - generic [ref=f11e112]:
          - heading [level=3] [ref=f11e113]: Choose your rate
          - paragraph [ref=f11e114]: We know plans can change, so please make sure you book the rate with the right level of flexibility for you.
          - status [ref=f11e118]:
            - list [ref=f11e125]:
              - listitem [ref=f11e126]: All accessible rooms at this hotel are double rooms.
              - listitem [ref=f11e127]: Accessible bathrooms at this hotel have lowered baths.
              - listitem [ref=f11e128]: The nearest accessible station is Gatwick Airport (2.1 miles).
          - generic [ref=f11e129]:
            - generic [ref=f11e130]:
              - generic [ref=f11e131]:
                - alert [ref=f11e133]:
                  - generic [ref=f11e137]: "The room type you selected is unavailable. we can offer you: Premier Plus Accessible Double Lowered Bath"
                - generic [ref=f11e140]:
                  - generic [ref=f11e142]:
                    - generic [ref=f11e143]:
                      - paragraph [ref=f11e149]: Only 1 rooms left for your search
                      - text: Premier Plus room
                    - paragraph [ref=f11e150]: Enjoy more space, a double or kingsize bed between 480mm and 520mm in height, wider entry bathrooms with a lowered bath or wet room, plus perks like Ultimate Wi-Fi, coffee machine, mini-fridge, bedside USB ports, iron, upgraded workspace & more.
                    - paragraph [ref=f11e152]: See details
                  - generic [ref=f11e156]:
                    - generic [ref=f11e157]:
                      - generic [ref=f11e159]:
                        - generic [ref=f11e161] [cursor=pointer]:
                          - radio [checked] [ref=f11e162]
                          - generic [ref=f11e165]:
                            - text: Flex
                            - paragraph [ref=f11e166]: Pay now or on arrival. Fully refundable up to 1 pm on day of arrival
                        - generic [ref=f11e167]:
                          - paragraph [ref=f11e169]:
                            - generic [ref=f11e170]:
                              - generic [ref=f11e171]: £
                              - generic [ref=f11e172]: "127"
                              - generic [ref=f11e173]: "40"
                          - paragraph [ref=f11e174]: Total price
                          - generic [ref=f11e175]:
                            - paragraph [ref=f11e176]: 1 room,
                            - paragraph [ref=f11e177]: 2 nights
                      - separator [ref=f11e178]
                    - generic [ref=f11e179]:
                      - generic [ref=f11e181]:
                        - generic [ref=f11e183] [cursor=pointer]:
                          - radio [ref=f11e184]
                          - generic [ref=f11e187]:
                            - text: Standard
                            - paragraph [ref=f11e188]: Pay now, non-refundable. Check-in date amendable before 1pm on day of arrival
                        - generic [ref=f11e189]:
                          - paragraph [ref=f11e191]:
                            - generic [ref=f11e192]:
                              - generic [ref=f11e193]: £
                              - generic [ref=f11e194]: "115"
                          - paragraph [ref=f11e195]: Total price
                          - generic [ref=f11e196]:
                            - paragraph [ref=f11e197]: 1 room,
                            - paragraph [ref=f11e198]: 2 nights
                      - separator [ref=f11e199]
                    - generic [ref=f11e202]:
                      - generic [ref=f11e204] [cursor=pointer]:
                        - radio [ref=f11e205]
                        - generic [ref=f11e208]:
                          - text: Non-Flex
                          - paragraph [ref=f11e209]: Pay now. No changes
                      - generic [ref=f11e210]:
                        - paragraph [ref=f11e212]:
                          - generic [ref=f11e213]:
                            - generic [ref=f11e214]: £
                            - generic [ref=f11e215]: "111"
                        - paragraph [ref=f11e216]: Total price
                        - generic [ref=f11e217]:
                          - paragraph [ref=f11e218]: 1 room,
                          - paragraph [ref=f11e219]: 2 nights
              - generic [ref=f11e222]:
                - generic [ref=f11e224]:
                  - generic [ref=f11e225]: Standard room
                  - paragraph [ref=f11e226]: A super-comfy bed, a power shower and free Wi-Fi – our double rooms have everything you’ll need for a great night’s sleep.
                  - paragraph [ref=f11e228]: See details
                - generic [ref=f11e232]:
                  - generic [ref=f11e233]:
                    - generic [ref=f11e235]:
                      - generic [ref=f11e237] [cursor=pointer]:
                        - radio [ref=f11e238]
                        - generic [ref=f11e241]:
                          - text: Flex
                          - paragraph [ref=f11e242]: Pay now or on arrival. Fully refundable up to 1 pm on day of arrival
                      - generic [ref=f11e243]:
                        - paragraph [ref=f11e245]:
                          - generic [ref=f11e246]:
                            - generic [ref=f11e247]: £
                            - generic [ref=f11e248]: "127"
                            - generic [ref=f11e249]: "40"
                        - paragraph [ref=f11e250]: Total price
                        - generic [ref=f11e251]:
                          - paragraph [ref=f11e252]: 1 room,
                          - paragraph [ref=f11e253]: 2 nights
                    - separator [ref=f11e254]
                  - generic [ref=f11e255]:
                    - generic [ref=f11e257]:
                      - generic [ref=f11e259] [cursor=pointer]:
                        - radio [ref=f11e260]
                        - generic [ref=f11e263]:
                          - text: Standard
                          - paragraph [ref=f11e264]: Pay now, non-refundable. Check-in date amendable before 1pm on day of arrival
                      - generic [ref=f11e265]:
                        - paragraph [ref=f11e267]:
                          - generic [ref=f11e268]:
                            - generic [ref=f11e269]: £
                            - generic [ref=f11e270]: "115"
                        - paragraph [ref=f11e271]: Total price
                        - generic [ref=f11e272]:
                          - paragraph [ref=f11e273]: 1 room,
                          - paragraph [ref=f11e274]: 2 nights
                    - separator [ref=f11e275]
                  - generic [ref=f11e278]:
                    - generic [ref=f11e280] [cursor=pointer]:
                      - radio [ref=f11e281]
                      - generic [ref=f11e284]:
                        - text: Non-Flex
                        - paragraph [ref=f11e285]: Pay now. No changes
                    - generic [ref=f11e286]:
                      - paragraph [ref=f11e288]:
                        - generic [ref=f11e289]:
                          - generic [ref=f11e290]: £
                          - generic [ref=f11e291]: "111"
                      - paragraph [ref=f11e292]: Total price
                      - generic [ref=f11e293]:
                        - paragraph [ref=f11e294]: 1 room,
                        - paragraph [ref=f11e295]: 2 nights
              - generic [ref=f11e298]:
                - generic [ref=f11e300]:
                  - generic [ref=f11e301]:
                    - paragraph [ref=f11e307]: Only 4 rooms left for your search
                    - text: Standard room with View
                  - paragraph [ref=f11e308]: Our Family rooms with a view include a great view, a double or kingsize bed plus a sofa bed and pull-out bed (depending on the number of guests). We also provide travel cots at no extra cost. Room size and set up can vary based on the hotel and the number of guests.
                  - paragraph [ref=f11e310]: See details
                - generic [ref=f11e314]:
                  - generic [ref=f11e315]:
                    - generic [ref=f11e317]:
                      - generic [ref=f11e319] [cursor=pointer]:
                        - radio [ref=f11e320]
                        - generic [ref=f11e323]:
                          - text: Flex
                          - paragraph [ref=f11e324]: Pay now or on arrival. Fully refundable up to 1 pm on day of arrival
                      - generic [ref=f11e325]:
                        - paragraph [ref=f11e327]:
                          - generic [ref=f11e328]:
                            - generic [ref=f11e329]: £
                            - generic [ref=f11e330]: "127"
                            - generic [ref=f11e331]: "40"
                        - paragraph [ref=f11e332]: Total price
                        - generic [ref=f11e333]:
                          - paragraph [ref=f11e334]: 1 room,
                          - paragraph [ref=f11e335]: 2 nights
                    - separator [ref=f11e336]
                  - generic [ref=f11e337]:
                    - generic [ref=f11e339]:
                      - generic [ref=f11e341] [cursor=pointer]:
                        - radio [ref=f11e342]
                        - generic [ref=f11e345]:
                          - text: Standard
                          - paragraph [ref=f11e346]: Pay now, non-refundable. Check-in date amendable before 1pm on day of arrival
                      - generic [ref=f11e347]:
                        - paragraph [ref=f11e349]:
                          - generic [ref=f11e350]:
                            - generic [ref=f11e351]: £
                            - generic [ref=f11e352]: "115"
                        - paragraph [ref=f11e353]: Total price
                        - generic [ref=f11e354]:
                          - paragraph [ref=f11e355]: 1 room,
                          - paragraph [ref=f11e356]: 2 nights
                    - separator [ref=f11e357]
                  - generic [ref=f11e360]:
                    - generic [ref=f11e362] [cursor=pointer]:
                      - radio [ref=f11e363]
                      - generic [ref=f11e366]:
                        - text: Non-Flex
                        - paragraph [ref=f11e367]: Pay now. No changes
                    - generic [ref=f11e368]:
                      - paragraph [ref=f11e370]:
                        - generic [ref=f11e371]:
                          - generic [ref=f11e372]: £
                          - generic [ref=f11e373]: "111"
                      - paragraph [ref=f11e374]: Total price
                      - generic [ref=f11e375]:
                        - paragraph [ref=f11e376]: 1 room,
                        - paragraph [ref=f11e377]: 2 nights
            - generic [ref=f11e381]:
              - paragraph [ref=f11e382]: Premier Plus Accessible Double Lowered Bath
              - paragraph [ref=f11e383]: Flex
              - separator [ref=f11e384]
              - generic [ref=f11e385]:
                - generic [ref=f11e386]:
                  - paragraph [ref=f11e387]: Stay
                  - paragraph [ref=f11e388]: 2 nights
                - generic [ref=f11e389]:
                  - paragraph [ref=f11e390]: Price
                  - generic [ref=f11e391]:
                    - link [ref=f11e392] [cursor=pointer]:
                      - /url: "#"
                      - text: See breakdown
                    - paragraph [ref=f11e393]: £127.40
              - separator [ref=f11e394]
              - generic [ref=f11e395]:
                - paragraph [ref=f11e396]: Total
                - paragraph [ref=f11e397]:
                  - generic [ref=f11e398]:
                    - generic [ref=f11e399]: £
                    - generic [ref=f11e400]: "127"
                    - generic [ref=f11e401]: "40"
              - button [ref=f11e402] [cursor=pointer]: Continue
              - generic [ref=f11e403]: Enter your code
          - status [ref=f11e409]:
            - paragraph [ref=f11e416]:
              - text: There is no air conditioning at this hotel.
              - link [ref=f11e417] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/faq.html
                - text: Learn more about our rooms
        - generic [ref=f11e418]:
          - heading [level=3] [ref=f11e419]: Location
          - generic [ref=f11e422]:
            - generic [ref=f11e423]:
              - paragraph [ref=f11e424]: London Road, Lowfield Heath, Crawley
              - paragraph [ref=f11e425]: RH10 9ST
            - generic [ref=f11e426]:
              - generic [ref=f11e427]: Sat Nav Directions:RH10 9ST
              - generic [ref=f11e428]: What 3 Words:///class.gone.wash
            - generic [ref=f11e429]:
              - paragraph [ref=f11e430]: "Directions:"
              - generic [ref=f11e431]: "Crawley exit onto the A2011. At the 2nd roundabout take the 3rd exit continuing on the A2011. At the 3rd roundabout take the 4th exit onto the A23. Stay on the A23 passing 3 sets of traffic lights. At the roundabout stay straight taking the 2nd exit. Follow the dual carriageway until the roundabout. The the 3rd exit (U turn). Stay on the dual carriageway passing Car Valet Company on your left. The hotel is on your left behind the Gatwick Manor. From North Terminal: Exit North Terminal, taking 1st exit towards Shell Garage and passing a Premier Inn. At next roundabout take 1st exit. After the police station turn left. At the traffic lights turn right onto the dual carriageway. Continue until the roundabout and take the 2nd exit for Brighton. At the next roundabout continue straight towards Crawley. Pass a car wash and you will find the hotel and Gatwick Manor pub on your left. From the South Terminal: Exit the South Terminal, taking 1st exit onto dual carriageway towards North Terminal keeping on the inside lane. Take the first exit at the roundabout and immediate left following signs to South Side. At the T-junction turn left. After the police station turn left and at the traffic lights turn right onto dual carriageway. Continue until the roundabout and take 2nd exit towards Brighton. At the next r'bout continue straight towards Crawley. Pass a car wash and you will find the hotel and Gatwick Manor pub on your left."
              - paragraph [ref=f11e434] [cursor=pointer]: Read more
            - generic [ref=f11e435]:
              - paragraph [ref=f11e436]: "Transport and local information:"
              - list [ref=f11e438]:
                - listitem [ref=f11e439]:
                  - paragraph [ref=f11e440]: Gatwick North Terminal 3.5miles / 10 min taxi
                - listitem [ref=f11e441]:
                  - paragraph [ref=f11e442]: Gatwick Train Station (South Terminal) 5 miles
        - generic [ref=f11e444]:
          - heading [level=3] [ref=f11e445]: Parking at Premier Inn London Gatwick Airport South (London Road) hotel
          - generic [ref=f11e446]:
            - paragraph [ref=f11e447]: Our London Gatwick Airport South (London Road) hotel has 150 free on-site parking spaces available for the duration of your stay via validation on tablet at arrival. The car park is managed by Britannia Parking.
            - paragraph [ref=f11e448]: "Airport parking is available through our partners Holiday Extras at great prices (see frequently asked questions section for more details). Please note that Holiday Extra parking is not directly on site and available at Gatwick - Maple parking: RH10 9SW, Purple parking: RH11 0QB."
        - generic [ref=f11e450]:
          - heading [level=3] [ref=f11e451]: Our rooms
          - generic [ref=f11e452]:
            - tablist [ref=f11e453]:
              - tab [selected] [ref=f11e454] [cursor=pointer]:
                - heading [level=3] [ref=f11e456]: Double
              - tab [ref=f11e457] [cursor=pointer]:
                - heading [level=3] [ref=f11e459]: Twin
              - tab [ref=f11e460] [cursor=pointer]:
                - heading [level=3] [ref=f11e462]: Family
              - tab [ref=f11e463] [cursor=pointer]:
                - heading [level=3] [ref=f11e465]: Accessible
            - tabpanel [ref=f11e467]:
              - generic [ref=f11e468]:
                - generic [ref=f11e469]:
                  - button [ref=f11e473] [cursor=pointer]: View gallery
                  - generic [ref=f11e474]:
                    - paragraph [ref=f11e475]: Standard double
                    - paragraph [ref=f11e476]: A super-comfy bed, a power shower and free Wi-Fi, our double rooms have everything you'll need for a great night's sleep.
                    - generic [ref=f11e478]:
                      - paragraph [ref=f11e482]: Tea & coffee facilities
                      - paragraph [ref=f11e486]: Double or kingsize bed
                      - paragraph [ref=f11e490]: Free Wi-Fi
                      - paragraph [ref=f11e494]: Powerful shower
                      - paragraph [ref=f11e498]: Desk
                      - generic [ref=f11e500]:
                        - generic [ref=f11e502] [cursor=pointer]
                        - generic [ref=f11e505] [cursor=pointer]: See all facilities
                - generic [ref=f11e506]:
                  - button [ref=f11e510] [cursor=pointer]: View gallery
                  - generic [ref=f11e511]:
                    - paragraph [ref=f11e512]: Standard double with a view
                    - paragraph [ref=f11e513]: A great view, super-comfy bed, power shower and blackout curtains – our double rooms have everything you'll need for a great night's sleep.
                    - generic [ref=f11e515]:
                      - paragraph [ref=f11e519]: Free Wi-Fi
                      - paragraph [ref=f11e523]: Tea & coffee facilities
                      - paragraph [ref=f11e527]: Powerful shower
                      - paragraph [ref=f11e531]: Desk
                      - paragraph [ref=f11e535]: Vanity area
                      - generic [ref=f11e537]:
                        - generic [ref=f11e539] [cursor=pointer]
                        - generic [ref=f11e542] [cursor=pointer]: See all facilities
          - button [ref=f11e543] [cursor=pointer]: See rates
          - status [ref=f11e545]:
            - generic [ref=f11e550]:
              - strong [ref=f11e551]: "Disclaimer:"
              - text: Features, layouts, room sizes and designs may vary by hotel and location.
        - generic [ref=f11e553]:
          - heading [level=3] [ref=f11e554]: Restaurant
          - generic [ref=f11e560]:
            - generic [ref=f11e561]:
              - paragraph [ref=f11e565]: Mornings have never been so tasty! Build your own breakfast and fill up your plate with freshly cooked favourites like bacon, sausages, eggs and hash browns – plus a tasty selection of veggie and vegan options – and continental delights like fruit, cereal and freshly baked pastries. Plus, when an adult orders a Premier Inn Breakfast, up to two kids eat breakfast for free**
              - text: Read more
            - button [ref=f11e567] [cursor=pointer]: Breakfast Menu
        - generic [ref=f11e569]:
          - heading [level=3] [ref=f11e570]: Reviews for Premier Inn London Gatwick Airport South (London Road) hotel
          - generic [ref=f11e571]:
            - button [ref=f11e574] [cursor=pointer]:
              - generic [ref=f11e575]: See reviews
            - generic [ref=f11e578]:
              - paragraph [ref=f11e579]: Traveller reviews brought to you by
              - generic [ref=f11e581]:
                - paragraph [ref=f11e584]: Location
                - paragraph [ref=f11e588]: Sleep Quality
                - paragraph [ref=f11e592]: Rooms
                - paragraph [ref=f11e596]: Service
                - paragraph [ref=f11e600]: Value
                - paragraph [ref=f11e604]: Cleanliness
              - paragraph [ref=f11e607]: Write Review
        - generic [ref=f11e608]:
          - generic [ref=f11e611]:
            - heading [level=3] [ref=f11e612]: Hotel description
            - generic [ref=f11e613]:
              - paragraph [ref=f11e614]:
                - text: Our London Gatwick Airport South (London Road) hotel makes flights from Gatwick Airport easy. We’re just a 10-minute drive from Gatwick Airport South Terminal. Our hotel has free on-site parking. There’s also a restaurant next to our hotel with a range of delicious breakfast and dinner options. Whether you’re landing or preparing for take-off, you can look forward to a relaxing sleep on one of our comfy double or kingsize
                - link [ref=f11e615] [cursor=pointer]:
                  - /url: https://www.premierinn.com/gb/en/sleep/buy-our-bed.html
                  - text: beds
                - text: .
              - paragraph [ref=f11e616]: If you need anything before your flight, we’re just a 15-minute walk from County Oak Retail Park, which has a range of shops including supermarkets, pharmacies and everything else you might need before you travel. Crawley Leisure Park is just a 10-minute drive from our hotel, with cinemas, bowling alleys and much more to help you unwind before a long journey.
              - paragraph [ref=f11e617]:
                - text: If you’re planning to explore a little before you fly, there’s plenty of things to do in the area. Visit the People’s Cathedral in
                - link [ref=f11e618] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/things-to-do/guildford.html
                  - text: Guildford
                - text: ", stop by Crystal Palace in"
                - link [ref=f11e619] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/things-to-do/croydon.html
                  - text: Croydon
                - text: ", take a stroll along"
                - link [ref=f11e620] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/things-to-do/brighton/brighton-beach.html
                  - text: Brighton Beach and Pier
                - text: ", or see sights like Big Ben and Buckingham Palace in"
                - link [ref=f11e621] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/things-to-do/london.html
                  - text: London
                - text: . Find the best places to charge your phone and shop in our
                - link [ref=f11e622] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/things-to-do/london/gatwick-airport.html
                  - text: Gatwick Airport guide
                - text: and, if you’re going to be flying somewhere else in the near future, keep our other
                - link [ref=f11e623] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/locations/airport-hotels.html
                  - text: Premier Inn airport hotels
                - text: in mind. Enjoy your flight!
          - generic [ref=f11e626]:
            - heading [level=3] [ref=f11e627]: London Gatwick Airport South (London Road) FAQs
            - generic [ref=f11e629]:
              - button [ref=f11e631] [cursor=pointer]:
                - generic [ref=f11e632]: Which room amenities are available at your Premier Inn London Gatwick South (London Road) hotel?
              - button [ref=f11e636] [cursor=pointer]:
                - generic [ref=f11e637]: Is parking available at your Premier Inn London Gatwick South (London Road) hotel?
              - button [ref=f11e641] [cursor=pointer]:
                - generic [ref=f11e642]: How do I add airport parking to my booking?
              - button [ref=f11e646] [cursor=pointer]:
                - generic [ref=f11e647]: Can I book airport lounges, fast passes and other extras?
              - button [ref=f11e651] [cursor=pointer]:
                - generic [ref=f11e652]: Is your Premier Inn London Gatwick South (London Road) hotel accessible?
              - button [ref=f11e656] [cursor=pointer]:
                - generic [ref=f11e657]: What time is breakfast at your Premier Inn London Gatwick South (London Road) hotel?
        - generic [ref=f11e660]:
          - heading [level=3] [ref=f11e661]: Hotel contact information
          - paragraph [ref=f11e662]: "Phone: 0333 777 7274"
          - generic [ref=f11e663]: Calls to our UK hotels cost no more than calls to standard 01 or 02 numbers and are usually included in landline or mobile call packages. The same applies to calls to our hotels in the Republic of Ireland. For hotels outside the UK and Ireland, standard international call charges may apply – please check with your provider.
    - generic [ref=f11e667]:
      - generic [ref=f11e668]:
        - tablist [ref=f11e669]:
          - tab [selected] [ref=f11e670] [cursor=pointer]:
            - heading [level=3] [ref=f11e672]: About us
          - tab [ref=f11e673] [cursor=pointer]:
            - heading [level=3] [ref=f11e675]: City breaks
          - tab [ref=f11e676] [cursor=pointer]:
            - heading [level=3] [ref=f11e678]: Summer breaks
          - tab [ref=f11e679] [cursor=pointer]:
            - heading [level=3] [ref=f11e681]: Winter breaks
          - tab [ref=f11e682] [cursor=pointer]:
            - heading [level=3] [ref=f11e684]: Business
        - tabpanel [ref=f11e686]:
          - generic [ref=f11e687]:
            - paragraph [ref=f11e689]: Is it our comfy beds, our seriously tasty food, our great value hotels or our amazing teams that guests love so much? We reckon it’s a bit of everything. Take a look around to find out why we’re a much-loved, award-winning hotel chain, and rest easy knowing our range of rates give you both choice and flexibility.
            - generic [ref=f11e690]:
              - generic [ref=f11e691]:
                - paragraph [ref=f11e692]: Get in touch
                - list [ref=f11e693]:
                  - listitem [ref=f11e694]:
                    - link [ref=f11e695] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/contact-us.html
                      - text: Contact us
                  - listitem [ref=f11e696]:
                    - link [ref=f11e697] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/faq.html
                      - text: FAQs
                  - listitem [ref=f11e698]:
                    - link [ref=f11e699] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/groups.html
                      - text: Group bookings
                  - listitem [ref=f11e700]:
                    - link [ref=f11e701] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/premier-inn-affiliate-programme.html
                      - text: Affiliates
                  - listitem [ref=f11e702]:
                    - link [ref=f11e703] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/business/international-development.html
                      - text: International development
                  - listitem [ref=f11e704]:
                    - link [ref=f11e705] [cursor=pointer]:
                      - /url: https://www.whitbreadcareers.com/our-brands/premier-inn/
                      - text: Careers
              - generic [ref=f11e706]:
                - paragraph [ref=f11e707]: Legal
                - list [ref=f11e708]:
                  - listitem [ref=f11e709]:
                    - link [ref=f11e710] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/booking-terms-and-conditions.html
                      - text: Terms and conditions
                  - listitem [ref=f11e711]:
                    - link [ref=f11e712] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/terms-of-use.html
                      - text: Terms of use
                  - listitem [ref=f11e713]:
                    - link [ref=f11e714] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/privacy-policy.html
                      - text: Privacy policy
                  - listitem [ref=f11e715]:
                    - link [ref=f11e716] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/how-we-use-cookies.html
                      - text: Cookies notice
                  - listitem [ref=f11e717]:
                    - link [ref=f11e718] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/good-night-guarantee.html
                      - text: Good Night Guarantee
                  - listitem [ref=f11e719]:
                    - link [ref=f11e720] [cursor=pointer]:
                      - /url: https://cdn.whitbread.co.uk/media/2023/05/23926_MSA-Report-2022-23_Stage2_230510_14.48-Final-High-Res.pdf
                      - text: Modern Slavery Act statement
              - generic [ref=f11e721]:
                - paragraph [ref=f11e722]: Locations
                - list [ref=f11e723]:
                  - listitem [ref=f11e724]:
                    - link [ref=f11e725] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels.html
                      - text: Hotel directory
                  - listitem [ref=f11e726]:
                    - link [ref=f11e727] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                      - text: New hotels
                  - listitem [ref=f11e728]:
                    - link [ref=f11e729] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/city-breaks.html
                      - text: Local guides
                  - listitem [ref=f11e730]:
                    - link [ref=f11e731] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/short-breaks.html
                      - text: Short breaks
                  - listitem [ref=f11e732]:
                    - link [ref=f11e733] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                      - text: Hotels in Germany
                  - listitem [ref=f11e734]:
                    - link [ref=f11e735] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
                      - text: Dubai and beyond
              - generic [ref=f11e736]:
                - paragraph [ref=f11e737]: Our hotels
                - list [ref=f11e738]:
                  - listitem [ref=f11e739]:
                    - link [ref=f11e740] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/our-rooms.html
                      - text: Our rooms
                  - listitem [ref=f11e741]:
                    - link [ref=f11e742] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
                      - text: Family friendly
                  - listitem [ref=f11e743]:
                    - link [ref=f11e744] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep.html
                      - text: Sleep
                  - listitem [ref=f11e745]:
                    - link [ref=f11e746] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
                      - text: Food & drink
                  - listitem [ref=f11e747]:
                    - link [ref=f11e748] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                      - text: hub by Premier Inn
                  - listitem [ref=f11e749]:
                    - link [ref=f11e750] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/zip.html
                      - text: ZIP by Premier Inn
              - generic [ref=f11e751]:
                - paragraph [ref=f11e752]: Find out more
                - list [ref=f11e753]:
                  - listitem [ref=f11e754]:
                    - link [ref=f11e755] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why.html
                      - text: About us
                  - listitem [ref=f11e756]:
                    - link [ref=f11e757] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/resteasy.html
                      - text: Rest easy
                  - listitem [ref=f11e758]:
                    - link [ref=f11e759] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/gosh-childrens-charity.html
                      - text: GOSH Charity
                  - listitem [ref=f11e760]:
                    - link [ref=f11e761] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/force-for-good.html
                      - text: Force for Good
                  - listitem [ref=f11e762]:
                    - link [ref=f11e763] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/disabled-access.html
                      - text: Disabled access
                  - listitem [ref=f11e764]:
                    - link [ref=f11e765] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/news.html
                      - text: News
              - generic [ref=f11e766]:
                - paragraph [ref=f11e767]: Everything else
                - list [ref=f11e768]:
                  - listitem [ref=f11e769]:
                    - link [ref=f11e770] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/rates.html
                      - text: Our rates
                  - listitem [ref=f11e771]:
                    - link [ref=f11e772] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/offers.html
                      - text: Offers
                  - listitem [ref=f11e773]:
                    - link [ref=f11e774] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/buy-our-bed.html
                      - text: Buy our bed
                  - listitem [ref=f11e775]:
                    - link [ref=f11e776] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/apps.html
                      - text: Mobile apps
                  - listitem [ref=f11e777]:
                    - link [ref=f11e778] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/diversity-and-inclusion.html
                      - text: We value difference
                  - listitem [ref=f11e779]:
                    - link [ref=f11e780] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sitemap.html
                      - text: Sitemap
      - generic [ref=f11e781]:
        - generic [ref=f11e782]: © 2026 Premier Inn
        - generic [ref=f11e783]:
          - link [ref=f11e784] [cursor=pointer]:
            - /url: https://www.facebook.com/premierinn
          - link [ref=f11e786] [cursor=pointer]:
            - /url: https://twitter.com/premierinn
          - link [ref=f11e788] [cursor=pointer]:
            - /url: https://www.instagram.com/premierinn
  - alert [ref=f11e790]
  - generic:
    - region "Notifications-top"
    - region "Notifications-top-left"
    - region "Notifications-top-right"
    - region "Notifications-bottom-left"
    - region "Notifications-bottom"
    - region "Notifications-bottom-right"
  - dialog [ref=f11e793]:
    - banner [ref=f11e794]:
      - button "Close" [ref=f11e795] [cursor=pointer]
    - generic [ref=f11e802]:
      - paragraph [ref=f11e803]: Log in to your Premier Inn account
      - generic [ref=f11e805]:
        - group [ref=f11e806]:
          - generic [ref=f11e807]:
            - group [ref=f11e809]:
              - generic [ref=f11e810]: Email address
              - textbox "Email address" [ref=f11e812]: uk_0@yopmail.com
            - group [ref=f11e814]:
              - generic [ref=f11e815]: Password
              - textbox "Password" [ref=f11e817]: Password01
            - generic [ref=f11e818] [cursor=pointer]: Forgotten password?
        - button "Log in" [active] [ref=f11e821] [cursor=pointer]
      - generic [ref=f11e822]:
        - paragraph [ref=f11e823]: Don't have an account yet?
        - link "Sign up here" [ref=f11e824] [cursor=pointer]:
          - /url: https://www.uat.premierinn.digital/gb/en/account/register.html
      - paragraph [ref=f11e826]: Travelling for business?
      - link [ref=f11e828] [cursor=pointer]:
        - /url: https://business.uat.premierinn.digital/en-gb/account/login?intcmp=piLogInModalLink
```

# Test source

```ts
  142 |       try {
  143 |         return await this.executeRequest<T>(query, variables, operationExcerpt);
  144 |       } catch (error) {
  145 |         if (error instanceof GraphQLError) {
  146 |           lastError = error;
  147 | 
  148 |           // Only retry on 5xx / server-side errors
  149 |           if (!this.isRetryableError(error)) {
  150 |             this.logFailure(operationExcerpt, variables, error);
  151 |             throw error;
  152 |           }
  153 | 
  154 |           // Log the retryable error details
  155 |           if (attempt < this.retries) {
  156 |             console.error(
  157 |               `[GraphQLClient] [${new Date().toISOString()}] Retryable error on attempt ${attempt + 1}/${this.retries + 1} for "${operationExcerpt}": ${error.message}`,
  158 |             );
  159 |           }
  160 |         } else {
  161 |           // Unexpected error — wrap and throw immediately (don't retry)
  162 |           const wrapped = new GraphQLError(
  163 |             `Unexpected error executing query "${operationExcerpt}": ${error instanceof Error ? error.message : String(error)}`,
  164 |             operationExcerpt,
  165 |             variables,
  166 |             undefined,
  167 |             undefined,
  168 |           );
  169 |           this.logFailure(operationExcerpt, variables, wrapped);
  170 |           throw wrapped;
  171 |         }
  172 |       }
  173 |     }
  174 | 
  175 |     // All retries exhausted
  176 |     const exhaustedError = lastError ?? new GraphQLError(
  177 |       `All ${this.retries + 1} attempts failed for query "${operationExcerpt}"`,
  178 |       operationExcerpt,
  179 |       variables,
  180 |     );
  181 |     this.logFailure(operationExcerpt, variables, exhaustedError);
  182 |     throw exhaustedError;
  183 |   }
  184 | 
  185 |   // ─── Private Helpers ────────────────────────────────────────────────────────
  186 | 
  187 |   /**
  188 |    * Execute a single GraphQL request (no retry logic).
  189 |    */
  190 |   private async executeRequest<T>(
  191 |     query: string,
  192 |     variables: Record<string, unknown> | undefined,
  193 |     operationExcerpt: string,
  194 |   ): Promise<T> {
  195 |     const response = await this.request.post(this.baseURL, {
  196 |       headers: {
  197 |         'Content-Type': 'application/json',
  198 |         ...this.extraHeaders,
  199 |       },
  200 |       data: { query, variables },
  201 |       timeout: this.timeout,
  202 |     });
  203 | 
  204 |     const status = response.status();
  205 |     let body: unknown;
  206 | 
  207 |     try {
  208 |       body = await response.json();
  209 |     } catch {
  210 |       // Response body is not valid JSON
  211 |       const textBody = await response.text().catch(() => '<unreadable>');
  212 |       throw new GraphQLError(
  213 |         `HTTP ${status} — invalid JSON response for query "${operationExcerpt}": ${textBody.slice(0, 200)}`,
  214 |         operationExcerpt,
  215 |         variables,
  216 |         undefined,
  217 |         { status, body: textBody },
  218 |       );
  219 |     }
  220 | 
  221 |     // Check for HTTP-level errors (4xx / 5xx)
  222 |     if (status >= 400) {
  223 |       const isRetryable = status >= 500;
  224 |       const responseExcerpt = JSON.stringify(body).slice(0, 1000);
  225 |       throw new GraphQLError(
  226 |         `HTTP ${status} ${isRetryable ? '(server error, retryable)' : '(client error, non-retryable)'} for query "${operationExcerpt}": ${responseExcerpt}`,
  227 |         operationExcerpt,
  228 |         variables,
  229 |         undefined,
  230 |         { status, body },
  231 |       );
  232 |     }
  233 | 
  234 |     // Check for GraphQL-level errors
  235 |     const graphqlBody = body as GraphQLResponseBody;
  236 |     if (graphqlBody.errors && graphqlBody.errors.length > 0) {
  237 |       const isRetryable = this.hasRetryableGraphQLError(graphqlBody.errors);
  238 |       const errorMessages = graphqlBody.errors
  239 |         .map((e) => e.message)
  240 |         .join('; ');
  241 | 
> 242 |       throw new GraphQLError(
      |             ^ GraphQLError: GraphQL error (retryable) for query "basket": Expected Iterable, but did not find one for field "BasketItem.details".
  243 |         `GraphQL error${isRetryable ? ' (retryable)' : ' (non-retryable)'} for query "${operationExcerpt}": ${errorMessages}`,
  244 |         operationExcerpt,
  245 |         variables,
  246 |         graphqlBody.errors,
  247 |         { status, body },
  248 |       );
  249 |     }
  250 | 
  251 |     // Return the data field
  252 |     if (graphqlBody.data === undefined || graphqlBody.data === null) {
  253 |       throw new GraphQLError(
  254 |         `GraphQL response has no "data" field for query "${operationExcerpt}"`,
  255 |         operationExcerpt,
  256 |         variables,
  257 |         undefined,
  258 |         { status, body },
  259 |       );
  260 |     }
  261 | 
  262 |     return graphqlBody.data as T;
  263 |   }
  264 | 
  265 |   /**
  266 |    * Determine if a GraphQLError is retryable (5xx HTTP or 5xx-class GraphQL errorType).
  267 |    */
  268 |   private isRetryableError(error: GraphQLError): boolean {
  269 |     // Check HTTP status in the response
  270 |     const resp = error.response as { status?: number } | undefined;
  271 |     if (resp?.status !== undefined && resp.status >= 500) {
  272 |       return true;
  273 |     }
  274 | 
  275 |     // Check GraphQL errors for 5xx errorType in extensions
  276 |     if (error.errors && Array.isArray(error.errors)) {
  277 |       return this.hasRetryableGraphQLError(error.errors as GraphQLResponseError[]);
  278 |     }
  279 | 
  280 |     return false;
  281 |   }
  282 | 
  283 |   /**
  284 |    * Check if any GraphQL error has a 5xx-class errorType in extensions.
  285 |    */
  286 |   private hasRetryableGraphQLError(errors: GraphQLResponseError[]): boolean {
  287 |     return errors.some((err) => {
  288 |       const errorType = err.extensions?.errorType;
  289 |       if (typeof errorType === 'string') {
  290 |         // Match errorType containing "5" indicating 5xx (e.g., "500", "503", "INTERNAL_SERVER_ERROR")
  291 |         return /^5\d{2}$/.test(errorType) || errorType.startsWith('5');
  292 |       }
  293 |       return false;
  294 |     });
  295 |   }
  296 | 
  297 |   /**
  298 |    * Extract a short excerpt from the query string for use in error messages.
  299 |    * Attempts to find the operation name, falls back to first 80 chars.
  300 |    */
  301 |   private extractOperationExcerpt(query: string): string {
  302 |     // Try to extract the operation name (e.g., "query GetBasket" -> "GetBasket")
  303 |     const match = query.match(/(?:query|mutation|subscription)\s+(\w+)/);
  304 |     if (match) {
  305 |       return match[1];
  306 |     }
  307 |     // Fall back to first 80 characters of the query, trimmed
  308 |     return query.replace(/\s+/g, ' ').trim().slice(0, 80);
  309 |   }
  310 | 
  311 |   /**
  312 |    * Log full details of a failed operation for debugging.
  313 |    * Includes timestamp, URL, operation, sanitized variables, HTTP status, and response body.
  314 |    */
  315 |   private logFailure(
  316 |     operation: string,
  317 |     variables: Record<string, unknown> | undefined,
  318 |     error: GraphQLError,
  319 |   ): void {
  320 |     const resp = error.response as { status?: number; body?: unknown } | undefined;
  321 |     console.error(`[GraphQLClient] [${new Date().toISOString()}] Request failed:`, {
  322 |       url: this.baseURL,
  323 |       operation,
  324 |       variables: this.sanitizeVariables(variables),
  325 |       status: resp?.status,
  326 |       responseBody: resp?.body,
  327 |       errors: error.errors,
  328 |       message: error.message,
  329 |     });
  330 |   }
  331 | 
  332 |   /**
  333 |    * Sanitize variables for logging — redact fields that could contain sensitive data
  334 |    * (tokens, passwords, keys) to avoid leaking PII in logs.
  335 |    */
  336 |   private sanitizeVariables(
  337 |     variables: Record<string, unknown> | undefined,
  338 |   ): Record<string, unknown> | undefined {
  339 |     if (!variables) return undefined;
  340 | 
  341 |     const sensitiveKeys = /password|token|secret|key|auth|credential|card/i;
  342 |     const sanitized: Record<string, unknown> = {};
```