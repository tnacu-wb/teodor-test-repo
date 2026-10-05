# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/regression-tests.test.ts >> PI Regression Pack >> Hotel search returns availability results @regression @TC-812
- Location: tests/regressions/pi/regression-tests.test.ts:10:7

# Error details

```
Error: expect(locator).toBeVisible() failed

Locator: locator('[data-testid="hdp_basketStayPrice"]')
Expected: visible
Timeout: 15000ms
Error: element(s) not found

Call log:
  - Expect "toBeVisible" with timeout 15000ms
  - waiting for locator('[data-testid="hdp_basketStayPrice"]')

```

```yaml
- banner:
  - link "Premier Inn Rest Easy":
    - /url: https://www.uat.premierinn.digital/gb/en/home.html
    - img "Premier Inn Rest Easy"
  - img "English"
  - paragraph: Discover Premier Inn
  - paragraph: Business
  - paragraph: Manage booking
  - button "Log in"
  - button "Sign up"
- main:
  - combobox "Enter place, postcode or hotel": London Gatwick Airport South (London Road)
  - img
  - img
  - textbox "datepicker-input":
    - /placeholder: Check In | Check Out
    - text: Today | Tomorrow
  - button "1 adult, 1 room":
    - img
    - text: 1 adult, 1 room
  - button "Search"
  - navigation "breadcrumb":
    - list:
      - listitem:
        - link "Home":
          - /url: /gb/en/home.html
        - text: /
      - listitem:
        - link "Hotel Directory":
          - /url: /gb/en/hotels.html
        - text: /
      - listitem:
        - link "England":
          - /url: /gb/en/hotels/england.html
        - text: /
      - listitem:
        - link "West Sussex":
          - /url: /gb/en/hotels/england/west-sussex.html
        - text: /
      - listitem:
        - link "Crawley":
          - /url: /gb/en/hotels/england/west-sussex/crawley.html
        - text: /
      - listitem: London Gatwick Airport South London Road
  - heading "London Gatwick Airport South (London Road) hotel" [level=1]
  - img "ta-ratings-img"
  - text: (2644 reviews)
  - list:
    - listitem: Exclusive Dinner Offer - Website Only
  - paragraph: A 5-minute drive from Gatwick Airport South and North Terminals with free on-site parking during your stay
  - text: Hotel FacilitiesSee all
  - img
  - paragraph: Free parking
  - img
  - paragraph: Breakfast
  - img
  - paragraph: Restaurant
  - img
  - paragraph: Free Wi‑Fi
  - img
  - paragraph: Accessible
  - img
  - paragraph: Add airport parking with Holiday Extras
  - button "See all photos"
  - heading "Location" [level=3]
  - img "Hotel location map"
  - paragraph: London Road, Lowfield Heath, Crawley
  - paragraph: RH10 9ST
  - text: Sat Nav Directions:RH10 9ST What 3 Words:///class.gone.wash
  - paragraph: "Directions:"
  - text: "Crawley exit onto the A2011. At the 2nd roundabout take the 3rd exit continuing on the A2011. At the 3rd roundabout take the 4th exit onto the A23. Stay on the A23 passing 3 sets of traffic lights. At the roundabout stay straight taking the 2nd exit. Follow the dual carriageway until the roundabout. The the 3rd exit (U turn). Stay on the dual carriageway passing Car Valet Company on your left. The hotel is on your left behind the Gatwick Manor. From North Terminal: Exit North Terminal, taking 1st exit towards Shell Garage and passing a Premier Inn. At next roundabout take 1st exit. After the police station turn left. At the traffic lights turn right onto the dual carriageway. Continue until the roundabout and take the 2nd exit for Brighton. At the next roundabout continue straight towards Crawley. Pass a car wash and you will find the hotel and Gatwick Manor pub on your left. From the South Terminal: Exit the South Terminal, taking 1st exit onto dual carriageway towards North Terminal keeping on the inside lane. Take the first exit at the roundabout and immediate left following signs to South Side. At the T-junction turn left. After the police station turn left and at the traffic lights turn right onto dual carriageway. Continue until the roundabout and take 2nd exit towards Brighton. At the next r'bout continue straight towards Crawley. Pass a car wash and you will find the hotel and Gatwick Manor pub on your left."
  - paragraph: Read more
  - paragraph: "Transport and local information:"
  - list:
    - listitem:
      - paragraph: Gatwick North Terminal 3.5miles / 10 min taxi
    - listitem:
      - paragraph: Gatwick Train Station (South Terminal) 5 miles
  - heading "Parking at Premier Inn London Gatwick Airport South (London Road) hotel" [level=3]
  - paragraph: Our London Gatwick Airport South (London Road) hotel has 150 free on-site parking spaces available for the duration of your stay via validation on tablet at arrival. The car park is managed by Britannia Parking.
  - paragraph: "Airport parking is available through our partners Holiday Extras at great prices (see frequently asked questions section for more details). Please note that Holiday Extra parking is not directly on site and available at Gatwick - Maple parking: RH10 9SW, Purple parking: RH11 0QB."
  - heading "Our rooms" [level=3]
  - tablist:
    - tab "Double" [selected]:
      - heading "Double" [level=3]
    - tab "Twin":
      - heading "Twin" [level=3]
    - tab "Family":
      - heading "Family" [level=3]
    - tab "Accessible":
      - heading "Accessible" [level=3]
  - tabpanel "Double":
    - button "View gallery"
    - paragraph: Standard double
    - paragraph: A super-comfy bed, a power shower and free Wi-Fi, our double rooms have everything you'll need for a great night's sleep.
    - img
    - paragraph: Tea & coffee facilities
    - img
    - paragraph: Double or kingsize bed
    - img
    - paragraph: Free Wi-Fi
    - img
    - paragraph: Powerful shower
    - img
    - paragraph: Desk
    - img
    - text: See all facilities
    - button "View gallery"
    - paragraph: Standard double with a view
    - paragraph: A great view, super-comfy bed, power shower and blackout curtains – our double rooms have everything you'll need for a great night's sleep.
    - img
    - paragraph: Free Wi-Fi
    - img
    - paragraph: Tea & coffee facilities
    - img
    - paragraph: Powerful shower
    - img
    - paragraph: Desk
    - img
    - paragraph: Vanity area
    - img
    - text: See all facilities
  - status:
    - img
    - strong: "Disclaimer:"
    - text: Features, layouts, room sizes and designs may vary by hotel and location.
  - heading "Restaurant" [level=3]
  - img "Breakfast room"
  - img "Breakfast"
  - paragraph: Mornings have never been so tasty! Build your own breakfast and fill up your plate with freshly cooked favourites like bacon, sausages, eggs and hash browns – plus a tasty selection of veggie and vegan options – and continental delights like fruit, cereal and freshly baked pastries. Plus, when an adult orders a Premier Inn Breakfast, up to two kids eat breakfast for free**
  - text: Read more
  - button "Breakfast Menu"
  - heading "Reviews for Premier Inn London Gatwick Airport South (London Road) hotel" [level=3]
  - button "See reviews"
  - paragraph: Traveller reviews brought to you by
  - img
  - paragraph: Location
  - img
  - paragraph: Sleep Quality
  - img
  - paragraph: Rooms
  - img
  - paragraph: Service
  - img
  - paragraph: Value
  - img
  - paragraph: Cleanliness
  - img
  - paragraph: Write Review
  - heading "Hotel description" [level=3]
  - paragraph:
    - text: Our London Gatwick Airport South (London Road) hotel makes flights from Gatwick Airport easy. We’re just a 10-minute drive from Gatwick Airport South Terminal. Our hotel has free on-site parking. There’s also a restaurant next to our hotel with a range of delicious breakfast and dinner options. Whether you’re landing or preparing for take-off, you can look forward to a relaxing sleep on one of our comfy double or kingsize
    - link "beds":
      - /url: https://www.premierinn.com/gb/en/sleep/buy-our-bed.html
    - text: .
  - paragraph: If you need anything before your flight, we’re just a 15-minute walk from County Oak Retail Park, which has a range of shops including supermarkets, pharmacies and everything else you might need before you travel. Crawley Leisure Park is just a 10-minute drive from our hotel, with cinemas, bowling alleys and much more to help you unwind before a long journey.
  - paragraph:
    - text: If you’re planning to explore a little before you fly, there’s plenty of things to do in the area. Visit the People’s Cathedral in
    - link "Guildford":
      - /url: https://www.uat.premierinn.digital/gb/en/things-to-do/guildford.html
    - text: ", stop by Crystal Palace in"
    - link "Croydon":
      - /url: https://www.uat.premierinn.digital/gb/en/things-to-do/croydon.html
    - text: ", take a stroll along"
    - link "Brighton Beach and Pier":
      - /url: https://www.uat.premierinn.digital/gb/en/things-to-do/brighton/brighton-beach.html
    - text: ", or see sights like Big Ben and Buckingham Palace in"
    - link "London":
      - /url: https://www.uat.premierinn.digital/gb/en/things-to-do/london.html
    - text: . Find the best places to charge your phone and shop in our
    - link "Gatwick Airport guide":
      - /url: https://www.uat.premierinn.digital/gb/en/things-to-do/london/gatwick-airport.html
    - text: and, if you’re going to be flying somewhere else in the near future, keep our other
    - link "Premier Inn airport hotels":
      - /url: https://www.uat.premierinn.digital/gb/en/why/locations/airport-hotels.html
    - text: in mind. Enjoy your flight!
  - heading "London Gatwick Airport South (London Road) FAQs" [level=3]
  - button "Which room amenities are available at your Premier Inn London Gatwick South (London Road) hotel?"
  - button "Is parking available at your Premier Inn London Gatwick South (London Road) hotel?"
  - button "How do I add airport parking to my booking?"
  - button "Can I book airport lounges, fast passes and other extras?"
  - button "Is your Premier Inn London Gatwick South (London Road) hotel accessible?"
  - button "What time is breakfast at your Premier Inn London Gatwick South (London Road) hotel?"
  - heading "Hotel contact information" [level=3]
  - paragraph: "Phone: 0333 777 7274"
  - text: Calls to our UK hotels cost no more than calls to standard 01 or 02 numbers and are usually included in landline or mobile call packages. The same applies to calls to our hotels in the Republic of Ireland. For hotels outside the UK and Ireland, standard international call charges may apply – please check with your provider.
- tablist:
  - tab "About us" [selected]:
    - heading "About us" [level=3]
  - tab "City breaks":
    - heading "City breaks" [level=3]
  - tab "Summer breaks":
    - heading "Summer breaks" [level=3]
  - tab "Winter breaks":
    - heading "Winter breaks" [level=3]
  - tab "Business":
    - heading "Business" [level=3]
- tabpanel "About us":
  - paragraph: Is it our comfy beds, our seriously tasty food, our great value hotels or our amazing teams that guests love so much? We reckon it’s a bit of everything. Take a look around to find out why we’re a much-loved, award-winning hotel chain, and rest easy knowing our range of rates give you both choice and flexibility.
  - paragraph: Get in touch
  - link "Contact us":
    - /url: https://www.uat.premierinn.digital/gb/en/contact-us.html
  - link "FAQs":
    - /url: https://www.uat.premierinn.digital/gb/en/faq.html
  - link "Group bookings":
    - /url: https://www.uat.premierinn.digital/gb/en/why/groups.html
  - link "Affiliates":
    - /url: https://www.uat.premierinn.digital/gb/en/premier-inn-affiliate-programme.html
  - link "International development":
    - /url: https://www.uat.premierinn.digital/gb/en/business/international-development.html
  - link "Careers":
    - /url: https://www.whitbreadcareers.com/our-brands/premier-inn/
  - paragraph: Legal
  - link "Terms and conditions":
    - /url: https://www.uat.premierinn.digital/gb/en/terms/booking-terms-and-conditions.html
  - link "Terms of use":
    - /url: https://www.uat.premierinn.digital/gb/en/terms/terms-of-use.html
  - link "Privacy policy":
    - /url: https://www.uat.premierinn.digital/gb/en/terms/privacy-policy.html
  - link "Cookies notice":
    - /url: https://www.uat.premierinn.digital/gb/en/terms/how-we-use-cookies.html
  - link "Good Night Guarantee":
    - /url: https://www.uat.premierinn.digital/gb/en/sleep/good-night-guarantee.html
  - link "Modern Slavery Act statement":
    - /url: https://cdn.whitbread.co.uk/media/2023/05/23926_MSA-Report-2022-23_Stage2_230510_14.48-Final-High-Res.pdf
  - paragraph: Locations
  - link "Hotel directory":
    - /url: https://www.uat.premierinn.digital/gb/en/hotels.html
  - link "New hotels":
    - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
  - link "Local guides":
    - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/city-breaks.html
  - link "Short breaks":
    - /url: https://www.uat.premierinn.digital/gb/en/short-breaks.html
  - link "Hotels in Germany":
    - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
  - link "Dubai and beyond":
    - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
  - paragraph: Our hotels
  - link "Our rooms":
    - /url: https://www.uat.premierinn.digital/gb/en/sleep/our-rooms.html
  - link "Family friendly":
    - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
  - link "Sleep":
    - /url: https://www.uat.premierinn.digital/gb/en/sleep.html
  - link "Food & drink":
    - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
  - link "hub by Premier Inn":
    - /url: https://www.uat.premierinn.digital/gb/en/hub.html
  - link "ZIP by Premier Inn":
    - /url: https://www.uat.premierinn.digital/gb/en/zip.html
  - paragraph: Find out more
  - link "About us":
    - /url: https://www.uat.premierinn.digital/gb/en/why.html
  - link "Rest easy":
    - /url: https://www.uat.premierinn.digital/gb/en/resteasy.html
  - link "GOSH Charity":
    - /url: https://www.uat.premierinn.digital/gb/en/why/gosh-childrens-charity.html
  - link "Force for Good":
    - /url: https://www.uat.premierinn.digital/gb/en/why/force-for-good.html
  - link "Disabled access":
    - /url: https://www.uat.premierinn.digital/gb/en/terms/disabled-access.html
  - link "News":
    - /url: https://www.uat.premierinn.digital/gb/en/news.html
  - paragraph: Everything else
  - link "Our rates":
    - /url: https://www.uat.premierinn.digital/gb/en/why/rates.html
  - link "Offers":
    - /url: https://www.uat.premierinn.digital/gb/en/offers.html
  - link "Buy our bed":
    - /url: https://www.uat.premierinn.digital/gb/en/sleep/buy-our-bed.html
  - link "Mobile apps":
    - /url: https://www.uat.premierinn.digital/gb/en/why/apps.html
  - link "We value difference":
    - /url: https://www.uat.premierinn.digital/gb/en/terms/diversity-and-inclusion.html
  - link "Sitemap":
    - /url: https://www.uat.premierinn.digital/gb/en/sitemap.html
- text: © 2026 Premier Inn
- link:
  - /url: https://www.facebook.com/premierinn
  - img
- link:
  - /url: https://twitter.com/premierinn
  - img
- link:
  - /url: https://www.instagram.com/premierinn
  - img
- alert
- region "Notifications-top"
- region "Notifications-top-left"
- region "Notifications-top-right"
- region "Notifications-bottom-left"
- region "Notifications-bottom"
- region "Notifications-bottom-right"
- dialog "Cookies and how we use them":
  - heading "Cookies and how we use them" [level=2]
  - text: We use cookies and similar technologies to help keep our website running smoothly, understand how it's used, improve your experience and show you relevant advertising. If you're happy with this, select 'Accept All'. To find out more or manage your choices, select 'Manage Choices'. If you need more information, please see our
  - link "Cookie Notice":
    - /url: https://www.premierinn.com/gb/en/terms/how-we-use-cookies.html
  - text: and
  - link "Privacy Policy":
    - /url: https://www.premierinn.com/gb/en/terms/privacy-policy.html
  - text: .
  - button "Manage Choices, Opens the preference center dialog": Manage Choices
  - button "Essential Only"
  - button "Accept All"
```

# Test source

```ts
  1  | import { test, expect } from '../../../config/lambdatest/fixture';
  2  | 
  3  | test.describe('PI Regression Pack', () => {
  4  |   test('Manage Booking modal opens from header @regression @TC-811', async ({ page }) => {
  5  |     await page.goto('/');
  6  |     await page.locator('[data-testid="ManageBookingButton"]').click();
  7  |     await expect(page.locator('[data-testid="ManageBookingModal-ModalContent"]')).toBeVisible();
  8  |   });
  9  | 
  10 |   test('Hotel search returns availability results @regression @TC-812', async ({ page }) => {
  11 |     await page.goto('/gb/en/hotels/england/west-sussex/crawley/london-gatwick-airport-south-london-road.html');
  12 |     await expect(page.locator('h1[data-testid="hdp_hotelTitle"]')).toBeVisible({ timeout: 30000 });
> 13 |     await expect(page.locator('[data-testid="hdp_basketStayPrice"]')).toBeVisible();
     |                                                                       ^ Error: expect(locator).toBeVisible() failed
  14 |   });
  15 | });
  16 | 
```