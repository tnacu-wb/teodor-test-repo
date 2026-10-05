# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-logged-user-uk-hotel-semi-flex-poa-shorten-period.spec.ts >> End2end: Amend, Logged in user, PI UK Hotel, Semi-Flex, pay now, shorten period >> Test Amend, Logged in user: shorten period and verify changes. TestCase ID: 266200.
- Location: qa/tests/regressions/pi/baseline-e2e-logged-user-uk-hotel-semi-flex-poa-shorten-period.spec.ts:26:8

# Error details

```
Error: postPaymentWebhook failed with status 400 for URL https://api.uat.premierinn.digital/v1/baskets/AQN-7544240b-6d05-4ccb-b50d-df4f4eb77422/payment-webhook: {"errors":[{"message":"Must provide query string.","extensions":{"code":"MISSING_QUERY_STRING"}}]}
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
            - img "English" [ref=e18] [cursor=pointer]
            - generic [ref=e19]:
              - paragraph [ref=e22] [cursor=pointer]: Discover Premier Inn
              - paragraph [ref=e25] [cursor=pointer]: Business
              - paragraph [ref=e27] [cursor=pointer]: Manage booking
              - paragraph [ref=e30] [cursor=pointer]: test Testersons
      - main [ref=e31]:
        - generic [ref=e34]:
          - img "Home Banner Background" [ref=e36]
          - generic [ref=e37]:
            - generic [ref=e38]:
              - heading "Get a great night's sleep" [level=1] [ref=e39]
              - paragraph
            - generic [ref=e43]:
              - combobox "Enter place, postcode or hotel" [ref=e48] [cursor=pointer]
              - textbox "datepicker-input" [ref=e59] [cursor=pointer]:
                - /placeholder: Check In | Check Out
                - text: Today | Tomorrow
              - button "1 adult, 1 room" [ref=e63] [cursor=pointer]
              - button "Search" [ref=e64] [cursor=pointer]
    - generic [ref=e65]:
      - generic [ref=e68]:
        - heading "From a comfy bed to a great night's sleep, you know what you're getting with us" [level=1] [ref=e71]
        - heading "Our Premier Inn hotels offer comfort you can count on thanks to our flexible rates, unlimited breakfast and super-comfy beds - what's not to love?" [level=4] [ref=e74]
        - article [ref=e76]:
          - generic [ref=e77]:
            - generic [ref=e78]:
              - link [ref=e80] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/sleep/premier-plus.html
                - img "Premier Plus rooms" [ref=e81]
              - generic [ref=e82]:
                - heading "Premier Plus rooms" [level=4] [ref=e83]
                - paragraph [ref=e86]: More comfort. More convenience. More connectivity. That’s our Premier Plus rooms. Just perfect if you fancy a little more from your stay. Go on, treat yourself.
                - link [ref=e87] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/sleep/premier-plus.html
                  - button "Premier Plus rooms" [ref=e88]
            - generic [ref=e89]:
              - link [ref=e91] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
                - img "Food & drink" [ref=e92]
              - generic [ref=e93]:
                - heading "Food & drink" [level=4] [ref=e94]
                - paragraph [ref=e97]: People rave about our tempting Premier Inn Breakfast. And in the evenings, you can sit down to a mouth-watering menu of delicious dishes from our restaurant.
                - link [ref=e98] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
                  - button "Ready to tuck in?" [ref=e99]
          - generic [ref=e100]:
            - generic [ref=e101]:
              - link [ref=e103] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
                - img "Family Friendly" [ref=e104]
              - generic [ref=e105]:
                - heading "Family friendly" [level=4] [ref=e106]
                - generic [ref=e107]:
                  - paragraph [ref=e109]: Our family rooms have a super comfy kingsize* bed for the grown-ups, and two pull-out or sofa beds for the kids. Travelling with a baby? We’ll get you a cot too.
                  - generic [ref=e110]: Show more
                - link [ref=e112] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
                  - button "Family friendly" [ref=e113]
            - generic [ref=e114]:
              - link [ref=e116] [cursor=pointer]:
                - /url: https://www.premierinnathome.com/?utm_source=premierinn.com&utm_medium=referral&utm_campaign=Homepage+BF
                - img "Premier Inn Bed" [ref=e117]
              - generic [ref=e118]:
                - heading "Cosy up to FREE bedding bundles" [level=4] [ref=e119]
                - paragraph [ref=e122]: With every bed and mattress order, you can snuggle up to a FREE bedding bundle, making those nights extra cosy and oh so restful! T&Cs apply.*
                - link [ref=e123] [cursor=pointer]:
                  - /url: https://www.premierinnathome.com/?utm_source=premierinn.com&utm_medium=referral&utm_campaign=Homepage+BF
                  - button "Sleep is our thing" [ref=e124]
            - generic [ref=e125]:
              - link [ref=e127] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/business.html
                - img "Business Booker" [ref=e128]
              - generic [ref=e129]:
                - heading "Premier Inn Business" [level=4] [ref=e130]
                - paragraph [ref=e133]: Our free online booking tool gives businesses of all sizes access to a guaranteed 5% and up to 15% discount* off our Flex rate.
                - link [ref=e134] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/business.html
                  - button "Premier Inn Business" [ref=e135]
        - heading "Plan your next getaway" [level=2] [ref=e138]
        - article [ref=e140]:
          - generic [ref=e141]:
            - generic [ref=e142]:
              - link [ref=e144] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/hiking-guide.html
                - img "Hiking Guide" [ref=e145]
              - link [ref=e147] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/hiking-guide.html
                - button "Hiking Guide" [ref=e148]
            - generic [ref=e149]:
              - link [ref=e151] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/cycling-trails.html
                - img "Biking Guide" [ref=e152]
              - link [ref=e154] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/cycling-trails.html
                - button "Biking Guide" [ref=e155]
            - generic [ref=e156]:
              - link [ref=e158] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                - img "Premier Inn Germany" [ref=e159]
              - link [ref=e161] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                - button "Premier Inn Germany" [ref=e162]
            - generic [ref=e163]:
              - link [ref=e165] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
                - img "Hotels in the Middle East" [ref=e166]
              - link [ref=e168] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
                - button "Hotels in the Middle East" [ref=e169]
        - heading "Discover our hotels" [level=2] [ref=e172]
        - article [ref=e174]:
          - generic [ref=e175]:
            - generic [ref=e176]:
              - link [ref=e178] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why.html
                - img "Premier Inn" [ref=e179]
              - generic [ref=e180]:
                - heading "Premier Inn" [level=4] [ref=e181]
                - generic [ref=e182]:
                  - paragraph [ref=e184]: With over 800 hotels across the UK and beyond, we really are everywhere. Sleep in a super comfy bed, enjoy Freeview TV, an en-suite bathroom with power shower and so much more.
                  - generic [ref=e185]: Show more
                - link [ref=e187] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why.html
                  - button "Discover Premier Inn" [ref=e188]
            - generic [ref=e189]:
              - link [ref=e191] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                - img "hub by Premier Inn" [ref=e192]
              - generic [ref=e193]:
                - heading "hub by Premier Inn" [level=4] [ref=e194]
                - generic [ref=e195]:
                  - paragraph [ref=e197]: Smart, stylish rooms across London and Edinburgh at great prices. Clever touchscreen room controls, free Wi-Fi, 40" Freeview TVs, high-powered monsoon showers and super-comfy beds.
                  - generic [ref=e198]: Show more
                - link [ref=e200] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                  - button "Discover hub" [ref=e201]
            - generic [ref=e202]:
              - link [ref=e204] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/zip.html
                - img "ZIP by Premier Inn" [ref=e205]
              - generic [ref=e206]:
                - heading "ZIP by Premier Inn" [level=4] [ref=e207]
                - generic [ref=e208]:
                  - paragraph [ref=e210]: Our idea is simple. Do the essentials brilliantly, then take away everything else. You get a small room, a simple stay and best of all, a price to match – from £25 a night. Now open in Cardiff.
                  - generic [ref=e211]: Show more
                - link [ref=e213] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/zip.html
                  - button "Discover ZIP" [ref=e214]
        - heading "Premier Inn information, news and features" [level=2] [ref=e217]
        - article [ref=e219]:
          - generic [ref=e220]:
            - generic [ref=e221]:
              - link [ref=e223] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/news.html
                - img "Premier Inn news" [ref=e224]
              - generic [ref=e225]:
                - heading "Premier Inn news" [level=4] [ref=e226]
                - paragraph [ref=e229]: Want to hear all the latest updates from Premier Inn, hub and ZIP? Visit our news page to stay up to date with competitions, travel tips and more.
                - link [ref=e230] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/news.html
                  - button "Premier Inn news" [ref=e231]
            - generic [ref=e232]:
              - link [ref=e234] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                - img "New hotels" [ref=e235]
              - generic [ref=e236]:
                - heading "New hotels" [level=4] [ref=e237]
                - paragraph [ref=e240]: From city centres to seafronts and beyond, we’ll be opening new hotels in great locations throughout the year. Check out our list of latest openings!
                - link [ref=e241] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                  - button "New hotels" [ref=e242]
            - generic [ref=e243]:
              - link [ref=e245] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/cleanliness.html
                - img "Committed to cleanliness" [ref=e246]
              - generic [ref=e247]:
                - heading "Committed to cleanliness" [level=4] [ref=e248]
                - paragraph [ref=e251]: To make sure we keep everyone safe, we have a rigorous, daily cleaning regime - an enhanced hygiene promise we call Premier Inn CleanProtect™.
                - link [ref=e252] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/cleanliness.html
                  - button "Premier Inn CleanProtect™" [ref=e253]
      - contentinfo [ref=e254]:
        - generic [ref=e259]:
          - generic [ref=e260]:
            - button "About us" [ref=e261] [cursor=pointer]
            - button "City breaks" [ref=e262] [cursor=pointer]
            - button "Summer breaks" [ref=e263] [cursor=pointer]
            - button "Winter breaks" [ref=e264] [cursor=pointer]
            - button "Business" [ref=e265] [cursor=pointer]
          - generic [ref=e268]:
            - paragraph [ref=e270]: Is it our comfy beds, our seriously tasty food, our great value hotels or our amazing teams that guests love so much? We reckon it’s a bit of everything. Take a look around to find out why we’re a much-loved, award-winning hotel chain, and rest easy knowing our range of rates give you both choice and flexibility.
            - generic [ref=e271]:
              - generic [ref=e273]:
                - generic [ref=e274]: Get in touch
                - list [ref=e275]:
                  - listitem [ref=e276]:
                    - link "Contact us" [ref=e277] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/contact-us.html
                  - listitem [ref=e278]:
                    - link "FAQs" [ref=e279] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/faq.html
                  - listitem [ref=e280]:
                    - link "Group bookings" [ref=e281] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/groups.html
                  - listitem [ref=e282]:
                    - link "Affiliates" [ref=e283] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/premier-inn-affiliate-programme.html
                  - listitem [ref=e284]:
                    - link "International development" [ref=e285] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/business/international-development.html
                  - listitem [ref=e286]:
                    - link "Careers" [ref=e287] [cursor=pointer]:
                      - /url: https://www.whitbreadcareers.com/our-brands/premier-inn/
              - generic [ref=e289]:
                - generic [ref=e290]: Legal
                - list [ref=e291]:
                  - listitem [ref=e292]:
                    - link "Terms and conditions" [ref=e293] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/booking-terms-and-conditions.html
                  - listitem [ref=e294]:
                    - link "Terms of use" [ref=e295] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/terms-of-use.html
                  - listitem [ref=e296]:
                    - link "Privacy policy" [ref=e297] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/privacy-policy.html
                  - listitem [ref=e298]:
                    - link "Cookies notice" [ref=e299] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/how-we-use-cookies.html
                  - listitem [ref=e300]:
                    - link "Good Night Guarantee" [ref=e301] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/good-night-guarantee.html
                  - listitem [ref=e302]:
                    - link "Modern Slavery Act statement" [ref=e303] [cursor=pointer]:
                      - /url: https://cdn.whitbread.co.uk/media/2023/05/23926_MSA-Report-2022-23_Stage2_230510_14.48-Final-High-Res.pdf
              - generic [ref=e305]:
                - generic [ref=e306]: Locations
                - list [ref=e307]:
                  - listitem [ref=e308]:
                    - link "Hotel directory" [ref=e309] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels.html
                  - listitem [ref=e310]:
                    - link "New hotels" [ref=e311] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                  - listitem [ref=e312]:
                    - link "Local guides" [ref=e313] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/city-breaks.html
                  - listitem [ref=e314]:
                    - link "Short breaks" [ref=e315] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/short-breaks.html
                  - listitem [ref=e316]:
                    - link "Hotels in Germany" [ref=e317] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                  - listitem [ref=e318]:
                    - link "Dubai and beyond" [ref=e319] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
              - generic [ref=e321]:
                - generic [ref=e322]: Our hotels
                - list [ref=e323]:
                  - listitem [ref=e324]:
                    - link "Our rooms" [ref=e325] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/our-rooms.html
                  - listitem [ref=e326]:
                    - link "Family friendly" [ref=e327] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
                  - listitem [ref=e328]:
                    - link "Sleep" [ref=e329] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep.html
                  - listitem [ref=e330]:
                    - link "Food & drink" [ref=e331] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
                  - listitem [ref=e332]:
                    - link "hub by Premier Inn" [ref=e333] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                  - listitem [ref=e334]:
                    - link "ZIP by Premier Inn" [ref=e335] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/zip.html
              - generic [ref=e337]:
                - generic [ref=e338]: Find out more
                - list [ref=e339]:
                  - listitem [ref=e340]:
                    - link "About us" [ref=e341] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why.html
                  - listitem [ref=e342]:
                    - link "Rest easy" [ref=e343] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/resteasy.html
                  - listitem [ref=e344]:
                    - link "GOSH Charity" [ref=e345] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/gosh-childrens-charity.html
                  - listitem [ref=e346]:
                    - link "Force for Good" [ref=e347] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/force-for-good.html
                  - listitem [ref=e348]:
                    - link "Disabled access" [ref=e349] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/disabled-access.html
                  - listitem [ref=e350]:
                    - link "News" [ref=e351] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/news.html
              - generic [ref=e353]:
                - generic [ref=e354]: Everything else
                - list [ref=e355]:
                  - listitem [ref=e356]:
                    - link "Our rates" [ref=e357] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/rates.html
                  - listitem [ref=e358]:
                    - link "Offers" [ref=e359] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/offers.html
                  - listitem [ref=e360]:
                    - link "Buy our bed" [ref=e361] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/buy-our-bed.html
                  - listitem [ref=e362]:
                    - link "Mobile apps" [ref=e363] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/apps.html
                  - listitem [ref=e364]:
                    - link "We value difference" [ref=e365] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/diversity-and-inclusion.html
                  - listitem [ref=e366]:
                    - link "Sitemap" [ref=e367] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sitemap.html
        - generic [ref=e370]:
          - paragraph [ref=e371]: "Get #PremierInnspiration, short break ideas and much more delivered straight to your inbox"
          - button "Sign up" [ref=e372] [cursor=pointer]
        - generic [ref=e373]:
          - generic [ref=e375]: © 2026 Premier Inn
          - generic [ref=e377]:
            - link [ref=e378] [cursor=pointer]:
              - /url: https://www.facebook.com/premierinn
              - img "Facebook icon" [ref=e379]
            - link [ref=e380] [cursor=pointer]:
              - /url: https://twitter.com/premierinn
              - img "Twitter icon" [ref=e381]
            - link [ref=e382] [cursor=pointer]:
              - /url: https://www.instagram.com/premierinn
              - img "Instagram icon" [ref=e383]
  - alert [ref=e384]
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
  316 |     return BillingDetails.fromResponse({ billingDetails: firstReservation.billing ?? {} });
  317 |   }
  318 | 
  319 |   /**
  320 |    * Retrieve VAT rules for specific country code and Package code
  321 |    * @param {String} countryCode country code
  322 |    * @param {String} packageCode package code
  323 |   * @returns {Record<string, unknown>} VAT rules response
  324 |    */
  325 |   static async getVatRules(...args: unknown[]): Promise<EntityApiResponse> {
  326 |     const { request, values } = EntityApiCalls.splitArgs(args);
  327 |     const data = EntityApiCalls.asObject(values[0]);
  328 |     const countryCode = String(data.countryCode ?? data.vatRegion ?? values[0] ?? '');
  329 |     const packageCode = String(data.packageCode ?? data.pkgCodeArr ?? values[1] ?? '');
  330 |     const url = `${EntityApiCalls.getEntityApiBaseUrl()}/v1/rules/vat-codes?vatRegion=${encodeURIComponent(countryCode)}&pkgCodeArr=${encodeURIComponent(packageCode)}`;
  331 |     const response = await request.get(url, { headers: { Accept: 'application/json', ...EntityApiCalls.getApiKeyHeaders() } });
  332 |     if (!response.ok()) {
  333 |       throw new Error(`getVatRules failed with status ${response.status()} for URL ${url}`);
  334 |     }
  335 |     return EntityApiCalls.asObject(await response.json());
  336 |   }
  337 | 
  338 |   /**
  339 |    * Retrieve VAT rules for specific country code and Package code
  340 |    * @param {Room} roomType room type object
  341 |   * @returns {Record<string, unknown>} room substitution response
  342 |    */
  343 |   static async getRoomSubstitutions(...args: unknown[]): Promise<EntityApiResponse> {
  344 |     const { request, data } = EntityApiCalls.withContext(args);
  345 |     const roomType = EntityApiCalls.asObject(data.roomType ?? data);
  346 |     const roomTypeObj = EntityApiCalls.asObject(roomType.roomType);
  347 |     const app = String(EntityApiCalls.getBrowserOptions().app ?? '').toLowerCase();
  348 |     const channel = app === 'ib' ? 'BB' : app.toUpperCase();
  349 |     const adults = String(roomType.adultsNumber ?? roomType.adults ?? '');
  350 |     const children = String(roomType.childrenNumber ?? roomType.children ?? '');
  351 |     const roomTypeId = String(roomTypeObj.id ?? roomType.roomType ?? '');
  352 |     const url = `${EntityApiCalls.getEntityApiBaseUrl()}/v1/rules/room-substitutions?adults=${encodeURIComponent(adults)}&children=${encodeURIComponent(children)}&pms=OP&roomType=${encodeURIComponent(roomTypeId)}&channel=${encodeURIComponent(channel)}`;
  353 |     const response = await EntityApiCalls.withRetries(
  354 |       () => request.get(url, { headers: { Accept: 'application/json', ...EntityApiCalls.getApiKeyHeaders() } }),
  355 |       5,
  356 |       (result) => result.status() === 200,
  357 |     );
  358 |     if (!response.ok()) {
  359 |       throw new Error(`getRoomSubstitutions failed with status ${response.status()} for URL ${url}`);
  360 |     }
  361 |     return EntityApiCalls.asObject(await response.json());
  362 |   }
  363 | 
  364 |   /**
  365 |    * Post payment webhook
  366 |    * @param {Object} data data object
  367 |    * @param {String} data.basketReference basket reference id
  368 |    * @param {String} data.bookingReference booking reference id
  369 |    * @param {String} data.countryCode country code
  370 |    * @param {String} data.firstName first name of booker
  371 |    * @param {String} data.lastName last name of booker
  372 |   * @param {CardDetails} data.card card
  373 |   * @returns {Record<string, unknown>} payment webhook response
  374 |    */
  375 |   static async postPaymentWebhook(...args: unknown[]): Promise<EntityApiResponse> {
  376 |     const { request, data } = EntityApiCalls.withContext(args);
  377 |     const basketReference = String(data.basketReference ?? '');
  378 |     const url = `${EntityApiCalls.getEntityApiBaseUrl()}/v1/baskets/${basketReference}/payment-webhook`;
  379 |     const options = EntityApiCalls.getBrowserOptions();
  380 |     const locale = EntityApiCalls.getLocaleData();
  381 |     const card = EntityApiCalls.asObject(data.card);
  382 |     const token = String(data.token ?? card.token ?? '') || await EntityApiCalls.tokeniseCard(request, { card });
  383 |     const payload = {
  384 |       reference: basketReference,
  385 |       paymentId: String(data.paymentId ?? '78271787826D'),
  386 |       paymentStatus: String(data.paymentStatus ?? 'SUCCESS'),
  387 |       bookingReference: data.bookingReference,
  388 |       countryCode: data.countryCode,
  389 |       language: locale.language,
  390 |       firstName: String(data.firstName ?? '').replace(' ', ''),
  391 |       lastName: String(data.lastName ?? '').replace(' ', ''),
  392 |       bookingChannel: String(data.bookingChannel ?? 'PI'),
  393 |       channel: String(data.channel ?? 'WEB'),
  394 |       paymentError: data.paymentError ?? { code: '123', description: 'Error desc' },
  395 |       last4Digits: String(card.number ?? '').replace(/\s/g, '').slice(-4),
  396 |       cardSchemeId: card.cardSchemeId,
  397 |       token,
  398 |       expiry: `${String(card.expiryMonth ?? '')}/${String(card.expiryYear ?? '').slice(-2)}`,
  399 |       fraudCheckDecision: String(data.fraudCheckDecision ?? 'ACCEPT'),
  400 |     };
  401 |     const headers: Record<string, string> = {
  402 |       Accept: '*/*',
  403 |       'Content-Type': 'application/json',
  404 |     };
  405 |     const webhookKey = String(options.paymentWebhookXWhitApiKey ?? '');
  406 |     if (webhookKey) {
  407 |       headers['X-WHIT-API-KEY'] = webhookKey;
  408 |     }
  409 |     const response = await EntityApiCalls.withRetries(
  410 |       () => request.post(url, { headers, data: payload }),
  411 |       7,
  412 |       (result) => result.status() === 202,
  413 |     );
  414 |     if (response.status() !== 202 && !response.ok()) {
  415 |       const responseBody = await response.text().catch(() => '<unreadable>');
> 416 |       throw new Error(
      |             ^ Error: postPaymentWebhook failed with status 400 for URL https://api.uat.premierinn.digital/v1/baskets/AQN-7544240b-6d05-4ccb-b50d-df4f4eb77422/payment-webhook: {"errors":[{"message":"Must provide query string.","extensions":{"code":"MISSING_QUERY_STRING"}}]}
  417 |         `postPaymentWebhook failed with status ${response.status()} for URL ${url}: ${responseBody.slice(0, 1000)}`
  418 |       );
  419 |     }
  420 |     return EntityApiCalls.asObject(await response.json());
  421 |   }
  422 | 
  423 |   /**
  424 |    * Get Cancellation Policies
  425 |    * @param {Object} data object data
  426 |    * @param {String} data.hotelId hotel id
  427 |    * @param {String} data.basketReference basketReference
  428 |    * @param {String} data.ratePlanCode ratePlanCode
  429 |    * @param {String} data.arrivalDate arrival date (YYYY-MM-DD format)
  430 |   * @returns {Record<string, unknown>} cancellation policies response
  431 |    */
  432 |   static async getCancellationPolicies(...args: unknown[]): Promise<EntityApiResponse> {
  433 |     const { request, data } = EntityApiCalls.withContext(args);
  434 |     const hotelId = String(data.hotelId ?? Hotels.DEFAULT_HOTEL.id);
  435 |     const basketReference = String(data.basketReference ?? '');
  436 |     const ratePlanCode = String(data.ratePlanCode ?? 'FLEX');
  437 |     const arrivalDate = String(data.arrivalDate ?? '');
  438 |     const url = `${EntityApiCalls.getEntityApiBaseUrl()}/v1/reservations/cancellationPolicies?hotelId=${encodeURIComponent(hotelId)}&basketReference=${encodeURIComponent(basketReference)}&ratePlanCode=${encodeURIComponent(ratePlanCode)}&arrivalDate=${encodeURIComponent(arrivalDate)}`;
  439 |     const response = await request.get(url, { headers: { Accept: 'application/json', ...EntityApiCalls.getApiKeyHeaders() } });
  440 |     if (!response.ok()) {
  441 |       throw new Error(`getCancellationPolicies failed with status ${response.status()} for URL ${url}`);
  442 |     }
  443 |     return EntityApiCalls.asObject(await response.json());
  444 |   }
  445 | 
  446 |   /**
  447 |    * Get deposit folios
  448 |    * @param {String} reservationCode opera reservation code
  449 |   * @returns {Record<string, unknown> | null} deposit folios response, if found
  450 |    */
  451 |   static async getDepositFolios(...args: unknown[]): Promise<EntityApiResponse | null> {
  452 |     const { request, values } = EntityApiCalls.splitArgs(args);
  453 |     const data = EntityApiCalls.asObject(values[0]);
  454 |     const reservationCode = String(data.reservationCode ?? data.operaReservationCode ?? values[0] ?? '');
  455 |     const url = `${EntityApiCalls.getEntityApiBaseUrl()}/v1/baskets/deposit-folios/${reservationCode}`;
  456 |     const response = await request.get(url, { headers: { Accept: '*/*' } });
  457 |     if (!response.ok()) {
  458 |       return null;
  459 |     }
  460 |     return EntityApiCalls.asObject(await response.json());
  461 |   }
  462 | 
  463 |   /**
  464 |    * Get Booking Allowances
  465 |    * @param {Object} data object data
  466 |    * @param {String} data.hotelId hotel id
  467 |    * @param {String} data.reservationId opera reservation Id
  468 |   * @returns {Record<string, unknown>} booking allowances response
  469 |    */
  470 |   static async getBookingAllowances(...args: unknown[]): Promise<EntityApiResponse> {
  471 |     const { request, data } = EntityApiCalls.withContext(args);
  472 |     const hotelId = String(data.hotelId ?? Hotels.DEFAULT_GERMAN_HOTEL.id);
  473 |     const reservationId = String(data.reservationId ?? '');
  474 |     const url = `${EntityApiCalls.getEntityApiBaseUrl()}/ohip/v1/reservations/bookingAllowances?hotelId=${encodeURIComponent(hotelId)}&reservationId=${encodeURIComponent(reservationId)}`;
  475 |     const response = await EntityApiCalls.withRetries(
  476 |       () => request.get(url, { headers: { Accept: 'application/json', ...EntityApiCalls.getApiKeyHeaders() } }),
  477 |       5,
  478 |       (result) => result.status() === 200,
  479 |     );
  480 |     if (!response.ok()) {
  481 |       throw new Error(`getBookingAllowances failed with status ${response.status()} for URL ${url}`);
  482 |     }
  483 |     return EntityApiCalls.asObject(await response.json());
  484 |   }
  485 | 
  486 |   /**
  487 |    * Get reservations by ids
  488 |    * @param {Object} data object data
  489 |    * @param {String} data.hotelId hotel id
  490 |    * @param {String} data.reservationIds opera reservations Id
  491 |    * @param {String} data.rateInfoNeeded opera rate info needed
  492 |   * @returns {Record<string, unknown>} reservations response
  493 |    */
  494 |   static async getReservationsByIds(...args: unknown[]): Promise<EntityApiResponse> {
  495 |     const { request, data } = EntityApiCalls.withContext(args);
  496 |     const hotelId = String(data.hotelId ?? Hotels.DEFAULT_GERMAN_HOTEL.id);
  497 |     const reservationIds = String(data.reservationIds ?? '');
  498 |     const rateInfoNeeded = String(data.rateInfoNeeded ?? '');
  499 |     const url = `${EntityApiCalls.getEntityApiBaseUrl()}/ohip/v1/reservations/basket?rateInfoNeeded=${encodeURIComponent(rateInfoNeeded)}&reservationIds=${encodeURIComponent(reservationIds)}&hotelId=${encodeURIComponent(hotelId)}`;
  500 |     const response = await request.get(url, { headers: { Accept: 'application/json', ...EntityApiCalls.getApiKeyHeaders() } });
  501 |     if (!response.ok()) {
  502 |       const body = EntityApiCalls.asObject(await response.json().catch(() => ({})));
  503 |       const debugMessage = body.debugMessage;
  504 |       if (typeof debugMessage === 'string' && debugMessage.length > 0) {
  505 |         throw new Error(debugMessage);
  506 |       }
  507 |       throw new Error(`getReservationsByIds failed with status ${response.status()} for URL ${url}`);
  508 |     }
  509 | 
  510 |     return EntityApiCalls.asObject(await response.json());
  511 |   }
  512 | 
  513 |   /**
  514 |    * Validate that Guest from reservation guest has the same data as Booker from Guest Details
  515 |    * @param {Object} data object data
  516 |   * @param {BookingGuest} data.reservationGuest guest from reservation response
```