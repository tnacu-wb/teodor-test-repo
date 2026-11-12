# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts >> PI Baseline E2E - Guest PIBA Pay on Arrival Amendment >> Test Book as Guest user: 1 night, 1 room (Double), 2 adults, Flex rate, meals, donations - POA BAC card and Amend by change room type. TestCase ID: 380779.
- Location: tests/regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts:51:7

# Error details

```
Error: No rooms with meals are available for the period: 2027-02-28 - 2027-03-01
```

# Page snapshot

```yaml
- generic [active] [ref=e1]:
  - generic [ref=e2]:
    - generic [ref=e5]:
      - banner [ref=e6]:
        - generic [ref=e9]:
          - link [ref=e12] [cursor=pointer]:
            - /url: https://www.uat.premierinn.digital/gb/en/home.html
            - img "Premier Inn Rest Easy" [ref=e14]
          - generic [ref=e15]:
            - button "English" [ref=e17] [cursor=pointer]:
              - img "English" [ref=e18]
            - generic [ref=e19]:
              - paragraph [ref=e22] [cursor=pointer]: Discover Premier Inn
              - paragraph [ref=e25] [cursor=pointer]: Business
              - paragraph [ref=e27] [cursor=pointer]: Manage booking
              - generic [ref=e29]:
                - button "Log in" [ref=e30] [cursor=pointer]
                - button "Sign up" [ref=e31] [cursor=pointer]
      - main [ref=e32]:
        - generic [ref=e35]:
          - img "Home Banner Background" [ref=e37]
          - generic [ref=e38]:
            - generic [ref=e39]:
              - heading "Get a great night's sleep" [level=1] [ref=e40]
              - paragraph
            - generic [ref=e44]:
              - combobox "Enter place, postcode or hotel" [ref=e49] [cursor=pointer]
              - textbox "datepicker-input" [ref=e60] [cursor=pointer]:
                - /placeholder: Check In | Check Out
                - text: Today | Tomorrow
              - button "1 adult, 1 room" [ref=e64] [cursor=pointer]
              - button "Search" [ref=e65] [cursor=pointer]
    - generic [ref=e66]:
      - generic [ref=e69]:
        - heading "From a comfy bed to a great night's sleep, you know what you're getting with us" [level=1] [ref=e72]
        - heading "Our Premier Inn hotels offer comfort you can count on thanks to our flexible rates, unlimited breakfast and super-comfy beds - what's not to love?" [level=4] [ref=e75]
        - article [ref=e77]:
          - generic [ref=e78]:
            - generic [ref=e79]:
              - link [ref=e81] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/sleep/premier-plus.html
                - img "Premier Plus rooms" [ref=e82]
              - generic [ref=e83]:
                - heading "Premier Plus rooms" [level=4] [ref=e84]
                - paragraph [ref=e87]: More comfort. More convenience. More connectivity. That’s our Premier Plus rooms. Just perfect if you fancy a little more from your stay. Go on, treat yourself.
                - link [ref=e88] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/sleep/premier-plus.html
                  - button "Premier Plus rooms" [ref=e89]
            - generic [ref=e90]:
              - link [ref=e92] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
                - img "Food & drink" [ref=e93]
              - generic [ref=e94]:
                - heading "Food & drink" [level=4] [ref=e95]
                - paragraph [ref=e98]: People rave about our tempting Premier Inn Breakfast. And in the evenings, you can sit down to a mouth-watering menu of delicious dishes from our restaurant.
                - link [ref=e99] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
                  - button "Ready to tuck in?" [ref=e100]
          - generic [ref=e101]:
            - generic [ref=e102]:
              - link [ref=e104] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
                - img "Family Friendly" [ref=e105]
              - generic [ref=e106]:
                - heading "Family friendly" [level=4] [ref=e107]
                - paragraph [ref=e110]: Our family rooms have a super comfy kingsize* bed for the grown-ups, and two pull-out or sofa beds for the kids. Travelling with a baby? We’ll get you a cot too.
                - link [ref=e111] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
                  - button "Family friendly" [ref=e112]
            - generic [ref=e113]:
              - link [ref=e115] [cursor=pointer]:
                - /url: https://www.premierinnathome.com/?utm_source=premierinn.com&utm_medium=referral&utm_campaign=Homepage+BF
                - img "Premier Inn Bed" [ref=e116]
              - generic [ref=e117]:
                - heading "Cosy up to FREE bedding bundles" [level=4] [ref=e118]
                - paragraph [ref=e121]: With every bed and mattress order, you can snuggle up to a FREE bedding bundle, making those nights extra cosy and oh so restful! T&Cs apply.*
                - link [ref=e122] [cursor=pointer]:
                  - /url: https://www.premierinnathome.com/?utm_source=premierinn.com&utm_medium=referral&utm_campaign=Homepage+BF
                  - button "Sleep is our thing" [ref=e123]
            - generic [ref=e124]:
              - link [ref=e126] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/business.html
                - img "Business Booker" [ref=e127]
              - generic [ref=e128]:
                - heading "Premier Inn Business" [level=4] [ref=e129]
                - paragraph [ref=e132]: Our free online booking tool gives businesses of all sizes access to a guaranteed 5% and up to 15% discount* off our Flex rate.
                - link [ref=e133] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/business.html
                  - button "Premier Inn Business" [ref=e134]
        - heading "Plan your next getaway" [level=2] [ref=e137]
        - article [ref=e139]:
          - generic [ref=e140]:
            - generic [ref=e141]:
              - link [ref=e143] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/hiking-guide.html
                - img "Hiking Guide" [ref=e144]
              - link [ref=e146] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/hiking-guide.html
                - button "Hiking Guide" [ref=e147]
            - generic [ref=e148]:
              - link [ref=e150] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/cycling-trails.html
                - img "Biking Guide" [ref=e151]
              - link [ref=e153] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/cycling-trails.html
                - button "Biking Guide" [ref=e154]
            - generic [ref=e155]:
              - link [ref=e157] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                - img "Premier Inn Germany" [ref=e158]
              - link [ref=e160] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                - button "Premier Inn Germany" [ref=e161]
            - generic [ref=e162]:
              - link [ref=e164] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
                - img "Hotels in the Middle East" [ref=e165]
              - link [ref=e167] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
                - button "Hotels in the Middle East" [ref=e168]
        - heading "Discover our hotels" [level=2] [ref=e171]
        - article [ref=e173]:
          - generic [ref=e174]:
            - generic [ref=e175]:
              - link [ref=e177] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why.html
                - img "Premier Inn" [ref=e178]
              - generic [ref=e179]:
                - heading "Premier Inn" [level=4] [ref=e180]
                - generic [ref=e181]:
                  - paragraph [ref=e183]: With over 800 hotels across the UK and beyond, we really are everywhere. Sleep in a super comfy bed, enjoy Freeview TV, an en-suite bathroom with power shower and so much more.
                  - generic [ref=e184]: Show more
                - link [ref=e186] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why.html
                  - button "Discover Premier Inn" [ref=e187]
            - generic [ref=e188]:
              - link [ref=e190] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                - img "hub by Premier Inn" [ref=e191]
              - generic [ref=e192]:
                - heading "hub by Premier Inn" [level=4] [ref=e193]
                - generic [ref=e194]:
                  - paragraph [ref=e196]: Smart, stylish rooms across London and Edinburgh at great prices. Clever touchscreen room controls, free Wi-Fi, 40" Freeview TVs, high-powered monsoon showers and super-comfy beds.
                  - generic [ref=e197]: Show more
                - link [ref=e199] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                  - button "Discover hub" [ref=e200]
            - generic [ref=e201]:
              - link [ref=e203] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/zip.html
                - img "ZIP by Premier Inn" [ref=e204]
              - generic [ref=e205]:
                - heading "ZIP by Premier Inn" [level=4] [ref=e206]
                - generic [ref=e207]:
                  - paragraph [ref=e209]: Our idea is simple. Do the essentials brilliantly, then take away everything else. You get a small room, a simple stay and best of all, a price to match – from £25 a night. Now open in Cardiff.
                  - generic [ref=e210]: Show more
                - link [ref=e212] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/zip.html
                  - button "Discover ZIP" [ref=e213]
        - heading "Premier Inn information, news and features" [level=2] [ref=e216]
        - article [ref=e218]:
          - generic [ref=e219]:
            - generic [ref=e220]:
              - link [ref=e222] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/news.html
                - img "Premier Inn news" [ref=e223]
              - generic [ref=e224]:
                - heading "Premier Inn news" [level=4] [ref=e225]
                - paragraph [ref=e228]: Want to hear all the latest updates from Premier Inn, hub and ZIP? Visit our news page to stay up to date with competitions, travel tips and more.
                - link [ref=e229] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/news.html
                  - button "Premier Inn news" [ref=e230]
            - generic [ref=e231]:
              - link [ref=e233] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                - img "New hotels" [ref=e234]
              - generic [ref=e235]:
                - heading "New hotels" [level=4] [ref=e236]
                - paragraph [ref=e239]: From city centres to seafronts and beyond, we’ll be opening new hotels in great locations throughout the year. Check out our list of latest openings!
                - link [ref=e240] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                  - button "New hotels" [ref=e241]
            - generic [ref=e242]:
              - link [ref=e244] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/cleanliness.html
                - img "Committed to cleanliness" [ref=e245]
              - generic [ref=e246]:
                - heading "Committed to cleanliness" [level=4] [ref=e247]
                - paragraph [ref=e250]: To make sure we keep everyone safe, we have a rigorous, daily cleaning regime - an enhanced hygiene promise we call Premier Inn CleanProtect™.
                - link [ref=e251] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/cleanliness.html
                  - button "Premier Inn CleanProtect™" [ref=e252]
      - contentinfo [ref=e253]:
        - generic [ref=e258]:
          - generic [ref=e259]:
            - button "About us" [ref=e260] [cursor=pointer]
            - button "City breaks" [ref=e261] [cursor=pointer]
            - button "Summer breaks" [ref=e262] [cursor=pointer]
            - button "Winter breaks" [ref=e263] [cursor=pointer]
            - button "Business" [ref=e264] [cursor=pointer]
          - generic [ref=e267]:
            - paragraph [ref=e269]: Is it our comfy beds, our seriously tasty food, our great value hotels or our amazing teams that guests love so much? We reckon it’s a bit of everything. Take a look around to find out why we’re a much-loved, award-winning hotel chain, and rest easy knowing our range of rates give you both choice and flexibility.
            - generic [ref=e270]:
              - generic [ref=e272]:
                - generic [ref=e273]: Get in touch
                - list [ref=e274]:
                  - listitem [ref=e275]:
                    - link "Contact us" [ref=e276] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/contact-us.html
                  - listitem [ref=e277]:
                    - link "FAQs" [ref=e278] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/faq.html
                  - listitem [ref=e279]:
                    - link "Group bookings" [ref=e280] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/groups.html
                  - listitem [ref=e281]:
                    - link "Affiliates" [ref=e282] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/premier-inn-affiliate-programme.html
                  - listitem [ref=e283]:
                    - link "International development" [ref=e284] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/business/international-development.html
                  - listitem [ref=e285]:
                    - link "Careers" [ref=e286] [cursor=pointer]:
                      - /url: https://www.whitbreadcareers.com/our-brands/premier-inn/
              - generic [ref=e288]:
                - generic [ref=e289]: Legal
                - list [ref=e290]:
                  - listitem [ref=e291]:
                    - link "Terms and conditions" [ref=e292] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/booking-terms-and-conditions.html
                  - listitem [ref=e293]:
                    - link "Terms of use" [ref=e294] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/terms-of-use.html
                  - listitem [ref=e295]:
                    - link "Privacy policy" [ref=e296] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/privacy-policy.html
                  - listitem [ref=e297]:
                    - link "Cookies notice" [ref=e298] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/how-we-use-cookies.html
                  - listitem [ref=e299]:
                    - link "Good Night Guarantee" [ref=e300] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/good-night-guarantee.html
                  - listitem [ref=e301]:
                    - link "Modern Slavery Act statement" [ref=e302] [cursor=pointer]:
                      - /url: https://cdn.whitbread.co.uk/media/2023/05/23926_MSA-Report-2022-23_Stage2_230510_14.48-Final-High-Res.pdf
              - generic [ref=e304]:
                - generic [ref=e305]: Locations
                - list [ref=e306]:
                  - listitem [ref=e307]:
                    - link "Hotel directory" [ref=e308] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels.html
                  - listitem [ref=e309]:
                    - link "New hotels" [ref=e310] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                  - listitem [ref=e311]:
                    - link "Local guides" [ref=e312] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/city-breaks.html
                  - listitem [ref=e313]:
                    - link "Short breaks" [ref=e314] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/short-breaks.html
                  - listitem [ref=e315]:
                    - link "Hotels in Germany" [ref=e316] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                  - listitem [ref=e317]:
                    - link "Dubai and beyond" [ref=e318] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
              - generic [ref=e320]:
                - generic [ref=e321]: Our hotels
                - list [ref=e322]:
                  - listitem [ref=e323]:
                    - link "Our rooms" [ref=e324] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/our-rooms.html
                  - listitem [ref=e325]:
                    - link "Family friendly" [ref=e326] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
                  - listitem [ref=e327]:
                    - link "Sleep" [ref=e328] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep.html
                  - listitem [ref=e329]:
                    - link "Food & drink" [ref=e330] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
                  - listitem [ref=e331]:
                    - link "hub by Premier Inn" [ref=e332] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                  - listitem [ref=e333]:
                    - link "ZIP by Premier Inn" [ref=e334] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/zip.html
              - generic [ref=e336]:
                - generic [ref=e337]: Find out more
                - list [ref=e338]:
                  - listitem [ref=e339]:
                    - link "About us" [ref=e340] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why.html
                  - listitem [ref=e341]:
                    - link "Rest easy" [ref=e342] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/resteasy.html
                  - listitem [ref=e343]:
                    - link "GOSH Charity" [ref=e344] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/gosh-childrens-charity.html
                  - listitem [ref=e345]:
                    - link "Force for Good" [ref=e346] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/force-for-good.html
                  - listitem [ref=e347]:
                    - link "Disabled access" [ref=e348] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/disabled-access.html
                  - listitem [ref=e349]:
                    - link "News" [ref=e350] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/news.html
              - generic [ref=e352]:
                - generic [ref=e353]: Everything else
                - list [ref=e354]:
                  - listitem [ref=e355]:
                    - link "Our rates" [ref=e356] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/rates.html
                  - listitem [ref=e357]:
                    - link "Offers" [ref=e358] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/offers.html
                  - listitem [ref=e359]:
                    - link "Buy our bed" [ref=e360] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/buy-our-bed.html
                  - listitem [ref=e361]:
                    - link "Mobile apps" [ref=e362] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/apps.html
                  - listitem [ref=e363]:
                    - link "We value difference" [ref=e364] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/diversity-and-inclusion.html
                  - listitem [ref=e365]:
                    - link "Sitemap" [ref=e366] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sitemap.html
        - generic [ref=e369]:
          - paragraph [ref=e370]: "Get #PremierInnspiration, short break ideas and much more delivered straight to your inbox"
          - button "Sign up" [ref=e371] [cursor=pointer]
        - generic [ref=e372]:
          - generic [ref=e374]: © 2026 Premier Inn
          - generic [ref=e376]:
            - link [ref=e377] [cursor=pointer]:
              - /url: https://www.facebook.com/premierinn
              - img "Facebook icon" [ref=e378]
            - link [ref=e379] [cursor=pointer]:
              - /url: https://twitter.com/premierinn
              - img "Twitter icon" [ref=e380]
            - link [ref=e381] [cursor=pointer]:
              - /url: https://www.instagram.com/premierinn
              - img "Instagram icon" [ref=e382]
  - alert [ref=e383]
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
  410 |     guestDetails
  411 |   }: AppendGuestToReservationParams = {}): Promise<void> {
  412 |     if (guestDetails) {
  413 |       (guestDetails as any).booker.title = (guestDetails as any).booker.title ?? StringConstants.MR_TITLE?.name;
  414 |       await ApiReservationCalls.graphqlCreateReservationGuest({ hotelId, basketReference, guestDetails });
  415 |     }
  416 |   }
  417 | 
  418 |   /**
  419 |    * Check for hotel availability and return the response
  420 |   * @param {HotelAvailabilityInput} hotelAvailabilityInput - hotel availability input object
  421 |    * @param {String} [ratePlanCode] - rate plan code used to filter availabilities
  422 |    * @param {String|String[]} [pmsRoomType=''] - PMS room type(s) to filter availabilities
  423 |    * @param {Boolean} [loggedUser=false] - if there is a logged in user
  424 |    * @param {Boolean} [shouldHaveMeals=true] - if reservation should have restaurant and meals available
  425 |    * @param {Boolean|null} [shouldHaveEci] - if reservation should have Early Check-in package available
  426 |    * @param {Boolean|null} [shouldHaveLco] - if reservation should have Late Check-out package available
  427 |     * @param {Boolean} [eciLcoAllRooms=false] - if reservation should have Early Check-in and Late-checkout packages for all rooms
  428 |     * @param {Boolean} [exactRatePlanCode=false] - only accept the requested rate plan code
  429 |   * @returns {Promise<HotelAvailability>} hotel availability response with the matching rate plan code
  430 |    */
  431 |   static async getHotelAvailability(
  432 |     hotelAvailabilityInput: HotelAvailabilityInput,
  433 |     ratePlanCode: string = (HotelRate as any)?.PI_FLEX?.ratePlanCode || 'FLEXRATE',
  434 |     pmsRoomType: string | string[] = '',
  435 |     loggedUser: boolean = false,
  436 |     shouldHaveMeals: boolean = true,
  437 |     shouldHaveEci: boolean | null = null,
  438 |     shouldHaveLco: boolean | null = null,
  439 |     eciLcoAllRooms: boolean = false,
  440 |     exactRatePlanCode: boolean = false
  441 |   ): Promise<HotelAvailability> {
  442 |     // number of retries to look for hotels availabilities over 3 months span (90 days)
  443 |     let retries = 90;
  444 |     let serviceErrorIndex = 1;
  445 |     let roomFound: boolean = false;
  446 |     
  447 |     // cancel the hotel reservations older than 1 hour, to have more availability
  448 |     await OhipApiCalls.cancelHotelReservationsForHotel((hotelAvailabilityInput as any).hotelCode);
  449 | 
  450 |     // get availability after opening soon date
  451 |     const hotelInformation = await ApiContentCalls.graphqlGetHotelInformation({ hotelId: (hotelAvailabilityInput as any).hotelCode });
  452 |     const additionalOpeningSoonDays = (hotelInformation as any).hotelOpeningDate && moment((hotelAvailabilityInput as any).arrival).toDate().getTime() < moment((hotelInformation as any).hotelOpeningDate).toDate().getTime() ?
  453 |       (moment((hotelInformation as any).hotelOpeningDate).toDate().getTime() - moment((hotelAvailabilityInput as any).arrival).toDate().getTime()) / (1000 * 3600 * 24) : 0;
  454 |     
  455 |     const bookingFlowId = (shouldHaveEci != null || shouldHaveLco != null || shouldHaveMeals) ? 
  456 |       await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: (hotelAvailabilityInput as any).hotelCode }) : null;
  457 |     
  458 |     (hotelAvailabilityInput as any).arrival = moment((hotelAvailabilityInput as any).arrival).add(additionalOpeningSoonDays, 'days').format(Constants.ISO_DAY_DATE_FORMAT);
  459 |     (hotelAvailabilityInput as any).departure = moment((hotelAvailabilityInput as any).departure).add(additionalOpeningSoonDays, 'days').format(Constants.ISO_DAY_DATE_FORMAT);
  460 | 
  461 |     let hotelAvailability: any = await ApiReservationCalls.graphqlGetSingleHotelAvailability({ hotelAvailabilityInput, failIfError: false, loggedUser });
  462 |     let matchedAncillaryCloseoutItems = false;
  463 |     
  464 |     while ((hotelAvailability.errors && hotelAvailability.errors.length > 0 && (hotelAvailability.errors[0] as any).errorType === '500') || (!roomFound && retries !== 0)) {
  465 |       const ancillaryCloseoutMatchingItems = (hotelInformation as any).ancillaryCloseout ? (hotelInformation as any).ancillaryCloseout.items.filter((item: any) => {
  466 |         const ancillaryCloseoutStartDateTime = moment(item.startDate, Constants.DATE_FORMAT_SLASH).toDate().getTime();
  467 |         const ancillaryCloseoutEndDateTime = moment(item.endDate, Constants.DATE_FORMAT_SLASH).toDate().getTime();
  468 |         const hotelAvailabilityInputArrivalTime = moment((hotelAvailabilityInput as any).arrival).toDate().getTime();
  469 |         const hotelAvailabilityInputDepartureTime = moment((hotelAvailabilityInput as any).departure).toDate().getTime();
  470 |         return item.upsellCodes && item.upsellCodes !== 'No Upselling Codes Found' && (
  471 |           hotelAvailabilityInputArrivalTime >= ancillaryCloseoutStartDateTime && hotelAvailabilityInputArrivalTime <= ancillaryCloseoutEndDateTime ||
  472 |           hotelAvailabilityInputDepartureTime >= ancillaryCloseoutStartDateTime && hotelAvailabilityInputDepartureTime <= ancillaryCloseoutEndDateTime ||
  473 |           ancillaryCloseoutStartDateTime >= hotelAvailabilityInputArrivalTime && ancillaryCloseoutStartDateTime <= hotelAvailabilityInputDepartureTime &&
  474 |           ancillaryCloseoutEndDateTime >= hotelAvailabilityInputArrivalTime && ancillaryCloseoutEndDateTime <= hotelAvailabilityInputDepartureTime
  475 |         );
  476 |       }) : [];
  477 |       
  478 |       const stayingNights = Math.round((moment((hotelAvailabilityInput as any).departure, Constants.ISO_DAY_DATE_FORMAT).toDate().getTime() - moment((hotelAvailabilityInput as any).arrival).toDate().getTime()) / (1000 * 3600 * 24));
  479 |       let totalAdults = 0;
  480 |       let totalChildren = 0;
  481 | 
  482 |       (hotelAvailabilityInput as any).rooms.forEach((room: any) => {
  483 |         totalAdults += room.adultsNumber;
  484 |         totalChildren += room.childrenNumber;
  485 |       });
  486 |       
  487 |       const meals = shouldHaveMeals ? await ApiCalls.graphqlGetMealsPackages({
  488 |         hotelId: (hotelAvailabilityInput as any).hotelCode,
  489 |         nightsNumber: stayingNights,
  490 |         startDate: (hotelAvailabilityInput as any).arrival,
  491 |         endDate: (hotelAvailabilityInput as any).departure,
  492 |         adultsNumber: totalAdults,
  493 |         childrenNumber: totalChildren,
  494 |         bookingFlowId: bookingFlowId
  495 |       }) : null;
  496 |       
  497 |       if (shouldHaveMeals && (ancillaryCloseoutMatchingItems.length > 0 || !(
  498 |         (meals as any).adultMeals.filter((meal: any) => meal.id === Constants.PI_BREAKFAST_PACKAGE_CODE).length > 0 ||
  499 |         (meals as any).adultMeals.filter((meal: any) => meal.id === Constants.BREAKFAST_GERMAN_PACKAGE_CODE).length > 0 ||
  500 |         (meals as any).adultMeals.filter((meal: any) => meal.id === Constants.CONTINENTAL_BREAKFAST_PACKAGE_CODE).length > 0
  501 |       ))) {
  502 |         matchedAncillaryCloseoutItems = true;
  503 |         console.log(`No rooms are available for the period: ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure} because not all the meals are available.`);
  504 |         (hotelAvailabilityInput as any).arrival = moment((hotelAvailabilityInput as any).arrival).add(1, 'days').format(Constants.ISO_DAY_DATE_FORMAT);
  505 |         (hotelAvailabilityInput as any).departure = moment((hotelAvailabilityInput as any).departure).add(1, 'days').format(Constants.ISO_DAY_DATE_FORMAT);
  506 |         retries -= 1;
  507 |         console.log(`Retries left: ${retries}`);
  508 |         if (retries === 0) {
  509 |           await ApiReservationCalls.getHotelInventoryAndLogRooms(hotelAvailabilityInput as any);
> 510 |           throw new Error(`No rooms with meals are available for the period: ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure}`);
      |                 ^ Error: No rooms with meals are available for the period: 2027-02-28 - 2027-03-01
  511 |         }
  512 |         continue;
  513 |       } else {
  514 |         if (matchedAncillaryCloseoutItems) {
  515 |           matchedAncillaryCloseoutItems = false;
  516 |           hotelAvailability = await ApiReservationCalls.graphqlGetSingleHotelAvailability({ hotelAvailabilityInput, failIfError: false, loggedUser });
  517 |           continue;
  518 |         }
  519 |       }
  520 |       
  521 |       await UiUtils.switchToDefaultContent(); // workaround to avoid the browser instance to close if it takes long time to find the availability.
  522 |       
  523 |       let filteredRoomRates = hotelAvailability.errors ? [] : (hotelAvailability as any).roomRates.filter((roomRate: any) => !ratePlanCode || roomRate.ratePlanCode === ratePlanCode);
  524 |       
  525 |       if (!exactRatePlanCode && (['pib'].includes(ApiReservationCalls.getApp()) || ['ccui'].includes(ApiReservationCalls.getApp()) && filteredRoomRates.length === 0) &&
  526 |         (ratePlanCode === '' || ratePlanCode === (HotelRate as any)?.PI_FLEX?.ratePlanCode) && (hotelAvailability as any).roomRates) {
  527 |         // use the Business Flex rate instead of Flex rate for the suppression case.
  528 |         const businessFlexRate = (hotelAvailability as any).roomRates.find((roomRate: any) => roomRate.ratePlanCode === (HotelRate as any)?.BUSINESS_FLEX?.ratePlanCode);
  529 |         filteredRoomRates = businessFlexRate ? [businessFlexRate] : filteredRoomRates;
  530 |       }
  531 |       
  532 |       filteredRoomRates = filteredRoomRates.filter((roomRate: any) =>
  533 |         roomRate.roomTypes.filter((roomType: any, index: number) =>
  534 |           roomType.rooms.length > 0 && roomType.rooms.filter((room: any) =>
  535 |             !pmsRoomType || (Array.isArray(pmsRoomType) && (!pmsRoomType[index] || room.pmsRoomType === pmsRoomType[index])) || room.pmsRoomType === pmsRoomType
  536 |           ).length > 0
  537 |         ).length >= (hotelAvailabilityInput as any).rooms.length
  538 |       );
  539 |       
  540 |       let eciPackageFound = false;
  541 |       let lcoPackageFound = false;
  542 |       
  543 |       if (shouldHaveEci != null || shouldHaveLco != null) {
  544 |         let eciList: any[] | undefined;
  545 |         let lcoList: any[] | undefined;
  546 |         let eciPackageAvailable;
  547 |         let lcoPackageAvailable;
  548 | 
  549 |         if (shouldHaveEci != null) {
  550 |           console.log(`Retrieve ECI packages for dates: ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure}`);
  551 |           eciList = await ApiCalls.graphqlGetAncillariesEciExtras({
  552 |             hotelId: (hotelAvailabilityInput as any).hotelCode,
  553 |             bookingFlowId: bookingFlowId,
  554 |             nightsNumber: stayingNights,
  555 |             startDate: (hotelAvailabilityInput as any).arrival,
  556 |             endDate: (hotelAvailabilityInput as any).departure,
  557 |             adultsNumber: totalAdults,
  558 |             childrenNumber: totalChildren
  559 |           }) as any[];
  560 |           eciPackageAvailable = eciList && eciList.length > 0 ? Number.parseInt((eciList[0] as any).available) : 0;
  561 |           if (eciLcoAllRooms) {
  562 |             eciPackageFound = eciPackageAvailable >= (hotelAvailabilityInput as any).rooms.length;
  563 |           } else {
  564 |             eciPackageFound = eciPackageAvailable > 0;
  565 |           }
  566 |         }
  567 |         
  568 |         if (shouldHaveLco != null) {
  569 |           console.log(`Retrieve LCO packages for dates: ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure}`);
  570 |           lcoList = await ApiCalls.graphqlGetAncillariesLcoExtras({
  571 |             hotelId: (hotelAvailabilityInput as any).hotelCode,
  572 |             bookingFlowId: bookingFlowId,
  573 |             nightsNumber: stayingNights,
  574 |             startDate: (hotelAvailabilityInput as any).arrival,
  575 |             endDate: (hotelAvailabilityInput as any).departure,
  576 |             adultsNumber: totalAdults,
  577 |             childrenNumber: totalChildren
  578 |           }) as any[];
  579 |           lcoPackageAvailable = lcoList && lcoList.length > 0 ? Number.parseInt((lcoList[0] as any).available) : 0;
  580 |           if (eciLcoAllRooms) {
  581 |             lcoPackageFound = lcoPackageAvailable >= (hotelAvailabilityInput as any).rooms.length;
  582 |           } else {
  583 |             lcoPackageFound = lcoPackageAvailable > 0;
  584 |           }
  585 |         }
  586 |       }
  587 |       
  588 |       const hasEnoughRooms = filteredRoomRates.length > 0;
  589 | 
  590 |       if (hotelAvailability.errors || (hotelAvailability as any).available === false || !hasEnoughRooms || 
  591 |         (shouldHaveEci != null && (shouldHaveEci === true && !eciPackageFound || shouldHaveEci === false && eciPackageFound)) || 
  592 |         (shouldHaveLco != null && (shouldHaveLco === true && !lcoPackageFound || shouldHaveLco === false && lcoPackageFound))) {
  593 |         
  594 |         if (hotelAvailability.errors) {
  595 |           if (serviceErrorIndex === 21) {
  596 |             await ApiReservationCalls.getHotelInventoryAndLogRooms(hotelAvailabilityInput as any);
  597 |             throw new Error('PMS Adapter error, probably the service is down or request is invalid!');
  598 |           } else {
  599 |             console.log(`Service returned 500 Errors. Service errors count: ${serviceErrorIndex} / 20`);
  600 |             // add some delay between retries
  601 |             await UiUtils.page.waitForTimeout(2000 * serviceErrorIndex);
  602 |             serviceErrorIndex += 1;
  603 |           }
  604 |         } else if (shouldHaveEci != null && shouldHaveEci === true && !eciPackageFound || shouldHaveLco != null && shouldHaveLco === true && !lcoPackageFound) {
  605 |           if (!eciPackageFound) {
  606 |             console.log(`No ECI packages found for the period ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure}`);
  607 |           }
  608 |           if (!lcoPackageFound) {
  609 |             console.log(`No LCO packages found for the period ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure}`);
  610 |           }
```