# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-logged-user-uk-hotel-semi-flex-poa-shorten-period.spec.ts >> End2end: Amend, Logged in user, PI UK Hotel, Semi-Flex, pay now, shorten period >> Test Amend, Logged in user: shorten period and verify changes. TestCase ID: 266200.
- Location: qa/tests/regressions/pi/baseline-e2e-logged-user-uk-hotel-semi-flex-poa-shorten-period.spec.ts:26:8

# Error details

```
TimeoutError: locator.click: Timeout 15000ms exceeded.
Call log:
  - waiting for getByRole('button', { name: /^(Log in|Anmelden)$/i })

```

# Page snapshot

```yaml
- generic [active] [ref=f16e1]:
  - generic [ref=f16e2]:
    - generic [ref=f16e5]:
      - banner [ref=f16e6]:
        - generic [ref=f16e9]:
          - link [ref=f16e12] [cursor=pointer]:
            - /url: https://www.uat.premierinn.digital/gb/en/home.html
            - img "Premier Inn Rest Easy" [ref=f16e14]
          - generic [ref=f16e15]:
            - img "English" [ref=f16e18] [cursor=pointer]
            - generic [ref=f16e19]:
              - paragraph [ref=f16e22] [cursor=pointer]: Discover Premier Inn
              - paragraph [ref=f16e25] [cursor=pointer]: Business
              - paragraph [ref=f16e27] [cursor=pointer]: Manage booking
      - main [ref=f16e28]:
        - generic [ref=f16e31]:
          - img "Home Banner Background" [ref=f16e33]
          - generic [ref=f16e34]:
            - generic [ref=f16e35]:
              - heading "Get a great night's sleep" [level=1] [ref=f16e36]
              - paragraph
            - generic [ref=f16e40]:
              - combobox "Enter place, postcode or hotel" [ref=f16e45] [cursor=pointer]
              - textbox "datepicker-input" [ref=f16e56] [cursor=pointer]:
                - /placeholder: Check In | Check Out
                - text: Today | Tomorrow
              - button "1 adult, 1 room" [ref=f16e60] [cursor=pointer]
              - button "Search" [ref=f16e61] [cursor=pointer]
    - generic [ref=f16e62]:
      - generic [ref=f16e65]:
        - heading "From a comfy bed to a great night's sleep, you know what you're getting with us" [level=1] [ref=f16e68]
        - heading "Our Premier Inn hotels offer comfort you can count on thanks to our flexible rates, unlimited breakfast and super-comfy beds - what's not to love?" [level=4] [ref=f16e71]
        - article [ref=f16e73]:
          - generic [ref=f16e74]:
            - generic [ref=f16e75]:
              - link [ref=f16e77] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/sleep/premier-plus.html
                - img "Premier Plus rooms" [ref=f16e78]
              - generic [ref=f16e79]:
                - heading "Premier Plus rooms" [level=4] [ref=f16e80]
                - paragraph [ref=f16e83]: More comfort. More convenience. More connectivity. That’s our Premier Plus rooms. Just perfect if you fancy a little more from your stay. Go on, treat yourself.
                - link [ref=f16e84] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/sleep/premier-plus.html
                  - button "Premier Plus rooms" [ref=f16e85]
            - generic [ref=f16e86]:
              - link [ref=f16e88] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
                - img "Food & drink" [ref=f16e89]
              - generic [ref=f16e90]:
                - heading "Food & drink" [level=4] [ref=f16e91]
                - paragraph [ref=f16e94]: People rave about our tempting Premier Inn Breakfast. And in the evenings, you can sit down to a mouth-watering menu of delicious dishes from our restaurant.
                - link [ref=f16e95] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
                  - button "Ready to tuck in?" [ref=f16e96]
          - generic [ref=f16e97]:
            - generic [ref=f16e98]:
              - link [ref=f16e100] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
                - img "Family Friendly" [ref=f16e101]
              - generic [ref=f16e102]:
                - heading "Family friendly" [level=4] [ref=f16e103]
                - generic [ref=f16e104]:
                  - paragraph [ref=f16e106]: Our family rooms have a super comfy kingsize* bed for the grown-ups, and two pull-out or sofa beds for the kids. Travelling with a baby? We’ll get you a cot too.
                  - generic [ref=f16e107]: Show more
                - link [ref=f16e109] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
                  - button "Family friendly" [ref=f16e110]
            - generic [ref=f16e111]:
              - link [ref=f16e113] [cursor=pointer]:
                - /url: https://www.premierinnathome.com/?utm_source=premierinn.com&utm_medium=referral&utm_campaign=Homepage+BF
                - img "Premier Inn Bed" [ref=f16e114]
              - generic [ref=f16e115]:
                - heading "Cosy up to FREE bedding bundles" [level=4] [ref=f16e116]
                - paragraph [ref=f16e119]: With every bed and mattress order, you can snuggle up to a FREE bedding bundle, making those nights extra cosy and oh so restful! T&Cs apply.*
                - link [ref=f16e120] [cursor=pointer]:
                  - /url: https://www.premierinnathome.com/?utm_source=premierinn.com&utm_medium=referral&utm_campaign=Homepage+BF
                  - button "Sleep is our thing" [ref=f16e121]
            - generic [ref=f16e122]:
              - link [ref=f16e124] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/business.html
                - img "Business Booker" [ref=f16e125]
              - generic [ref=f16e126]:
                - heading "Premier Inn Business" [level=4] [ref=f16e127]
                - paragraph [ref=f16e130]: Our free online booking tool gives businesses of all sizes access to a guaranteed 5% and up to 15% discount* off our Flex rate.
                - link [ref=f16e131] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/business.html
                  - button "Premier Inn Business" [ref=f16e132]
        - heading "Plan your next getaway" [level=2] [ref=f16e135]
        - article [ref=f16e137]:
          - generic [ref=f16e138]:
            - generic [ref=f16e139]:
              - link [ref=f16e141] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/hiking-guide.html
                - img "Hiking Guide" [ref=f16e142]
              - link [ref=f16e144] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/hiking-guide.html
                - button "Hiking Guide" [ref=f16e145]
            - generic [ref=f16e146]:
              - link [ref=f16e148] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/cycling-trails.html
                - img "Biking Guide" [ref=f16e149]
              - link [ref=f16e151] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/cycling-trails.html
                - button "Biking Guide" [ref=f16e152]
            - generic [ref=f16e153]:
              - link [ref=f16e155] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                - img "Premier Inn Germany" [ref=f16e156]
              - link [ref=f16e158] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                - button "Premier Inn Germany" [ref=f16e159]
            - generic [ref=f16e160]:
              - link [ref=f16e162] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
                - img "Hotels in the Middle East" [ref=f16e163]
              - link [ref=f16e165] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
                - button "Hotels in the Middle East" [ref=f16e166]
        - heading "Discover our hotels" [level=2] [ref=f16e169]
        - article [ref=f16e171]:
          - generic [ref=f16e172]:
            - generic [ref=f16e173]:
              - link [ref=f16e175] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why.html
                - img "Premier Inn" [ref=f16e176]
              - generic [ref=f16e177]:
                - heading "Premier Inn" [level=4] [ref=f16e178]
                - generic [ref=f16e179]:
                  - paragraph [ref=f16e181]: With over 800 hotels across the UK and beyond, we really are everywhere. Sleep in a super comfy bed, enjoy Freeview TV, an en-suite bathroom with power shower and so much more.
                  - generic [ref=f16e182]: Show more
                - link [ref=f16e184] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why.html
                  - button "Discover Premier Inn" [ref=f16e185]
            - generic [ref=f16e186]:
              - link [ref=f16e188] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                - img "hub by Premier Inn" [ref=f16e189]
              - generic [ref=f16e190]:
                - heading "hub by Premier Inn" [level=4] [ref=f16e191]
                - generic [ref=f16e192]:
                  - paragraph [ref=f16e194]: Smart, stylish rooms across London and Edinburgh at great prices. Clever touchscreen room controls, free Wi-Fi, 40" Freeview TVs, high-powered monsoon showers and super-comfy beds.
                  - generic [ref=f16e195]: Show more
                - link [ref=f16e197] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                  - button "Discover hub" [ref=f16e198]
            - generic [ref=f16e199]:
              - link [ref=f16e201] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/zip.html
                - img "ZIP by Premier Inn" [ref=f16e202]
              - generic [ref=f16e203]:
                - heading "ZIP by Premier Inn" [level=4] [ref=f16e204]
                - generic [ref=f16e205]:
                  - paragraph [ref=f16e207]: Our idea is simple. Do the essentials brilliantly, then take away everything else. You get a small room, a simple stay and best of all, a price to match – from £25 a night. Now open in Cardiff.
                  - generic [ref=f16e208]: Show more
                - link [ref=f16e210] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/zip.html
                  - button "Discover ZIP" [ref=f16e211]
        - heading "Premier Inn information, news and features" [level=2] [ref=f16e214]
        - article [ref=f16e216]:
          - generic [ref=f16e217]:
            - generic [ref=f16e218]:
              - link [ref=f16e220] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/news.html
                - img "Premier Inn news" [ref=f16e221]
              - generic [ref=f16e222]:
                - heading "Premier Inn news" [level=4] [ref=f16e223]
                - paragraph [ref=f16e226]: Want to hear all the latest updates from Premier Inn, hub and ZIP? Visit our news page to stay up to date with competitions, travel tips and more.
                - link [ref=f16e227] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/news.html
                  - button "Premier Inn news" [ref=f16e228]
            - generic [ref=f16e229]:
              - link [ref=f16e231] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                - img "New hotels" [ref=f16e232]
              - generic [ref=f16e233]:
                - heading "New hotels" [level=4] [ref=f16e234]
                - paragraph [ref=f16e237]: From city centres to seafronts and beyond, we’ll be opening new hotels in great locations throughout the year. Check out our list of latest openings!
                - link [ref=f16e238] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                  - button "New hotels" [ref=f16e239]
            - generic [ref=f16e240]:
              - link [ref=f16e242] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/cleanliness.html
                - img "Committed to cleanliness" [ref=f16e243]
              - generic [ref=f16e244]:
                - heading "Committed to cleanliness" [level=4] [ref=f16e245]
                - paragraph [ref=f16e248]: To make sure we keep everyone safe, we have a rigorous, daily cleaning regime - an enhanced hygiene promise we call Premier Inn CleanProtect™.
                - link [ref=f16e249] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/cleanliness.html
                  - button "Premier Inn CleanProtect™" [ref=f16e250]
      - contentinfo [ref=f16e251]:
        - generic [ref=f16e256]:
          - generic [ref=f16e257]:
            - button "About us" [ref=f16e258] [cursor=pointer]
            - button "City breaks" [ref=f16e259] [cursor=pointer]
            - button "Summer breaks" [ref=f16e260] [cursor=pointer]
            - button "Winter breaks" [ref=f16e261] [cursor=pointer]
            - button "Business" [ref=f16e262] [cursor=pointer]
          - generic [ref=f16e265]:
            - paragraph [ref=f16e267]: Is it our comfy beds, our seriously tasty food, our great value hotels or our amazing teams that guests love so much? We reckon it’s a bit of everything. Take a look around to find out why we’re a much-loved, award-winning hotel chain, and rest easy knowing our range of rates give you both choice and flexibility.
            - generic [ref=f16e268]:
              - generic [ref=f16e270]:
                - generic [ref=f16e271]: Get in touch
                - list [ref=f16e272]:
                  - listitem [ref=f16e273]:
                    - link "Contact us" [ref=f16e274] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/contact-us.html
                  - listitem [ref=f16e275]:
                    - link "FAQs" [ref=f16e276] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/faq.html
                  - listitem [ref=f16e277]:
                    - link "Group bookings" [ref=f16e278] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/groups.html
                  - listitem [ref=f16e279]:
                    - link "Affiliates" [ref=f16e280] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/premier-inn-affiliate-programme.html
                  - listitem [ref=f16e281]:
                    - link "International development" [ref=f16e282] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/business/international-development.html
                  - listitem [ref=f16e283]:
                    - link "Careers" [ref=f16e284] [cursor=pointer]:
                      - /url: https://www.whitbreadcareers.com/our-brands/premier-inn/
              - generic [ref=f16e286]:
                - generic [ref=f16e287]: Legal
                - list [ref=f16e288]:
                  - listitem [ref=f16e289]:
                    - link "Terms and conditions" [ref=f16e290] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/booking-terms-and-conditions.html
                  - listitem [ref=f16e291]:
                    - link "Terms of use" [ref=f16e292] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/terms-of-use.html
                  - listitem [ref=f16e293]:
                    - link "Privacy policy" [ref=f16e294] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/privacy-policy.html
                  - listitem [ref=f16e295]:
                    - link "Cookies notice" [ref=f16e296] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/how-we-use-cookies.html
                  - listitem [ref=f16e297]:
                    - link "Good Night Guarantee" [ref=f16e298] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/good-night-guarantee.html
                  - listitem [ref=f16e299]:
                    - link "Modern Slavery Act statement" [ref=f16e300] [cursor=pointer]:
                      - /url: https://cdn.whitbread.co.uk/media/2023/05/23926_MSA-Report-2022-23_Stage2_230510_14.48-Final-High-Res.pdf
              - generic [ref=f16e302]:
                - generic [ref=f16e303]: Locations
                - list [ref=f16e304]:
                  - listitem [ref=f16e305]:
                    - link "Hotel directory" [ref=f16e306] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels.html
                  - listitem [ref=f16e307]:
                    - link "New hotels" [ref=f16e308] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                  - listitem [ref=f16e309]:
                    - link "Local guides" [ref=f16e310] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/city-breaks.html
                  - listitem [ref=f16e311]:
                    - link "Short breaks" [ref=f16e312] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/short-breaks.html
                  - listitem [ref=f16e313]:
                    - link "Hotels in Germany" [ref=f16e314] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                  - listitem [ref=f16e315]:
                    - link "Dubai and beyond" [ref=f16e316] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
              - generic [ref=f16e318]:
                - generic [ref=f16e319]: Our hotels
                - list [ref=f16e320]:
                  - listitem [ref=f16e321]:
                    - link "Our rooms" [ref=f16e322] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/our-rooms.html
                  - listitem [ref=f16e323]:
                    - link "Family friendly" [ref=f16e324] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
                  - listitem [ref=f16e325]:
                    - link "Sleep" [ref=f16e326] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep.html
                  - listitem [ref=f16e327]:
                    - link "Food & drink" [ref=f16e328] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
                  - listitem [ref=f16e329]:
                    - link "hub by Premier Inn" [ref=f16e330] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                  - listitem [ref=f16e331]:
                    - link "ZIP by Premier Inn" [ref=f16e332] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/zip.html
              - generic [ref=f16e334]:
                - generic [ref=f16e335]: Find out more
                - list [ref=f16e336]:
                  - listitem [ref=f16e337]:
                    - link "About us" [ref=f16e338] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why.html
                  - listitem [ref=f16e339]:
                    - link "Rest easy" [ref=f16e340] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/resteasy.html
                  - listitem [ref=f16e341]:
                    - link "GOSH Charity" [ref=f16e342] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/gosh-childrens-charity.html
                  - listitem [ref=f16e343]:
                    - link "Force for Good" [ref=f16e344] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/force-for-good.html
                  - listitem [ref=f16e345]:
                    - link "Disabled access" [ref=f16e346] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/disabled-access.html
                  - listitem [ref=f16e347]:
                    - link "News" [ref=f16e348] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/news.html
              - generic [ref=f16e350]:
                - generic [ref=f16e351]: Everything else
                - list [ref=f16e352]:
                  - listitem [ref=f16e353]:
                    - link "Our rates" [ref=f16e354] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/rates.html
                  - listitem [ref=f16e355]:
                    - link "Offers" [ref=f16e356] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/offers.html
                  - listitem [ref=f16e357]:
                    - link "Buy our bed" [ref=f16e358] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/buy-our-bed.html
                  - listitem [ref=f16e359]:
                    - link "Mobile apps" [ref=f16e360] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/apps.html
                  - listitem [ref=f16e361]:
                    - link "We value difference" [ref=f16e362] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/diversity-and-inclusion.html
                  - listitem [ref=f16e363]:
                    - link "Sitemap" [ref=f16e364] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sitemap.html
        - generic [ref=f16e367]:
          - paragraph [ref=f16e368]: "Get #PremierInnspiration, short break ideas and much more delivered straight to your inbox"
          - button "Sign up" [ref=f16e369] [cursor=pointer]
        - generic [ref=f16e370]:
          - generic [ref=f16e372]: © 2026 Premier Inn
          - generic [ref=f16e374]:
            - link [ref=f16e375] [cursor=pointer]:
              - /url: https://www.facebook.com/premierinn
              - img "Facebook icon" [ref=f16e376]
            - link [ref=f16e377] [cursor=pointer]:
              - /url: https://twitter.com/premierinn
              - img "Twitter icon" [ref=f16e378]
            - link [ref=f16e379] [cursor=pointer]:
              - /url: https://www.instagram.com/premierinn
              - img "Instagram icon" [ref=f16e380]
  - alert [ref=f16e381]
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
  1   | import { type Page, type Locator } from '@playwright/test';
  2   | import { Constants } from '../../../test-data/constants';
  3   | import { Locales, getCurrentLocale } from '../../../test-data/locales';
  4   | import { EncryptionUtils } from '../../../utils/encryptionUtils';
  5   | 
  6   | /**
  7   |  * PI Header Component - the global site header for premierinn.com.
  8   |  * Mirrors the PI-relevant subset of qa/reference `header.page.js` +
  9   |  * `components/opera/header/{loginSection,userDropdownMenu}.js`
  10  |  * (business-booker/reporting/IB multi-window flows are BB/IB-only and out of scope for PI).
  11  |  */
  12  | export class HeaderComponent {
  13  |   private readonly page: Page = global.page;
  14  | 
  15  |   // ######## UI elements/properties ########
  16  | 
  17  |   readonly loginLink: Locator = this.page.getByRole('button', { name: /^(Log in|Anmelden)$/i });
  18  |   readonly usernameLink: Locator = this.page.locator(
  19  |     '[data-testid="Global-UserName-Desktop"], [data-testid="Global-SideNav-UserName-NavItem-Mobile"]'
  20  |   );
  21  |   readonly mobileBurgerMenuButton: Locator = this.page.locator('[data-testid="mobile-navigation-menu"]');
  22  |   readonly sidebarContainer: Locator = this.page.locator('section[data-testid="modalSideBar"]');
  23  |   readonly mobileCloseButton: Locator = this.page.locator('[data-testid="closeModal"]');
  24  |   readonly manageBookingsLabel: Locator = this.page.locator(
  25  |     '[data-testid="navigation-link-findBooking"], [id="find-a-booking"], [data-testid="manageBookingPage"], [data-testid="ManageBookingButton"]'
  26  |   );
  27  |   readonly languageSelectorButton: Locator = this.page.locator('[data-testid="pi-languageSelectorContainer"] div');
  28  |   readonly mobileLanguageSelectorButton: Locator = this.page.locator(
  29  |     '[data-testid="defaultSideNav"] [data-testid="navigationItem"] p'
  30  |   );
  31  |   readonly englishLanguageSelectorFlagIcon: Locator = this.page.locator(
  32  |     '[id*="popover-body"] div div:nth-child(1) div img'
  33  |   );
  34  |   readonly germanLanguageSelectorFlagIcon: Locator = this.page.locator(
  35  |     '[id*="popover-body"] div div:nth-child(2) div img'
  36  |   );
  37  | 
  38  |   // Login modal (Header-Auth)
  39  |   readonly loginModal: Locator = this.page.locator('div[data-testid="Header-Auth-ModalBody"]');
  40  |   readonly loginModalEmailInput: Locator = this.loginModal.locator(
  41  |     'label[data-testid="input-email-label"] + div input'
  42  |   );
  43  |   readonly loginModalPasswordInput: Locator = this.loginModal.locator(
  44  |     'div[data-testid="Login-Password"] input'
  45  |   );
  46  |   readonly loginModalLoginButton: Locator = this.page.locator('button[data-testid="Login-ButtonLogin"]');
  47  |   readonly loginModalCloseButton: Locator = this.page.locator('button[data-testid="Header-Auth-ModalCloseButton"]');
  48  |   readonly incorrectCredentialsAlert: Locator = this.page.locator('div[data-testid="Alert"]');
  49  | 
  50  |   // User dropdown menu (shown after clicking usernameLink)
  51  |   readonly logOutButton: Locator = this.page.locator(
  52  |     '[data-testid="Global-Logout-Desktop"], [data-testid="Global-Logout-Mobile"]'
  53  |   );
  54  | 
  55  |   // ######## UI actions/navigation ########
  56  | 
  57  |   /** Check if user is currently logged in (login link still visible). */
  58  |   async checkIfUserIsLoggedIn(): Promise<boolean> {
  59  |     return this.loginLink.isVisible();
  60  |   }
  61  | 
  62  |   /** Click the mobile burger menu to open the sidebar, if not already open. */
  63  |   async clickOnMobileBurger(): Promise<void> {
  64  |     console.log('Click on mobile burger');
  65  |     await this.mobileBurgerMenuButton.click();
  66  |   }
  67  | 
  68  |   /** Open the sidebar on mobile if it is not already displayed. */
  69  |   private async ensureMobileSidebarOpen(): Promise<void> {
  70  |     if (!Constants.BROWSER_RESOLUTIONS.isDesktop() && !(await this.sidebarContainer.isVisible())) {
  71  |       await this.clickOnMobileBurger();
  72  |     }
  73  |   }
  74  | 
  75  |   /** Open the login modal. */
  76  |   async openLoginModal(): Promise<void> {
  77  |     console.log('Open Login modal');
  78  |     await this.ensureMobileSidebarOpen();
> 79  |     await this.loginLink.click();
      |                          ^ TimeoutError: locator.click: Timeout 15000ms exceeded.
  80  |     await this.loginModal.waitFor({ state: 'visible' });
  81  |   }
  82  | 
  83  |   /**
  84  |    * Log in to the account using the credentials provided in the login modal.
  85  |    * @param emailAddress the email address
  86  |    * @param password decoded password
  87  |    */
  88  |   async loginIntoAccount({ emailAddress, password }: { emailAddress: string; password: string }): Promise<void> {
  89  |     await this.loginModalEmailInput.fill(emailAddress);
  90  |     await this.loginModalPasswordInput.fill(password);
  91  |     await this.loginModalLoginButton.click();
  92  |   }
  93  | 
  94  |   /**
  95  |     * Perform login with the leisure customer credentials, retrying until the login control disappears.
  96  |    * Mirrors reference `header.page.js` `loginForLeisureCustomer`.
  97  |    */
  98  |   async loginForLeisureCustomer({
  99  |     emailAddress = Constants.PI_LEISURE_EMAIL,
  100 |     password = Constants.PI_LEISURE_PASSWORD,
  101 |     retry = 5,
  102 |   }: { emailAddress?: string; password?: string; retry?: number } = {}): Promise<void> {
  103 |     if (!(await this.loginLink.isVisible())) {
  104 |       await this.page.reload();
  105 |     }
  106 |     await this.openLoginModal();
  107 |     await this.loginIntoAccount({ emailAddress, password: EncryptionUtils.decode(password) });
  108 | 
  109 |     if (retry > 0) {
  110 |       const isLoggedIn = await this.loginLink
  111 |         .waitFor({ state: 'hidden', timeout: 30000 })
  112 |         .then(() => true)
  113 |         .catch(() => false);
  114 |       if (isLoggedIn) {
  115 |         return;
  116 |       }
  117 | 
  118 |       if (await this.loginModalCloseButton.isVisible()) {
  119 |         await this.loginModalCloseButton.click();
  120 |       }
  121 |       await this.loginForLeisureCustomer({ emailAddress, password, retry: retry - 1 });
  122 |     }
  123 |   }
  124 | 
  125 |   /** Log out the current user if logged in. */
  126 |   async logOutUserIfLoggedIn(): Promise<void> {
  127 |     if (await this.loginLink.isVisible()) {
  128 |       return;
  129 |     }
  130 |     await this.ensureMobileSidebarOpen();
  131 |     if (Constants.BROWSER_RESOLUTIONS.isDesktop()) {
  132 |       await this.usernameLink.click();
  133 |     }
  134 |     await this.logOutButton.click();
  135 |   }
  136 | 
  137 |   /** Click on the language selector button (desktop or mobile). */
  138 |   async clickOnLanguageSelector(): Promise<void> {
  139 |     console.log('Click on language selector button');
  140 |     if (Constants.BROWSER_RESOLUTIONS.isDesktop()) {
  141 |       await this.languageSelectorButton.click();
  142 |     } else {
  143 |       await this.ensureMobileSidebarOpen();
  144 |       await this.mobileLanguageSelectorButton.click();
  145 |     }
  146 |   }
  147 | 
  148 |   /** Click on the language option matching the given locale-string language ("gb-en"/"de-de"). */
  149 |   async clickOnLanguageOptionBasedOnLocale(): Promise<void> {
  150 |     const currentLocale = getCurrentLocale();
  151 |     const flagIcon =
  152 |       currentLocale.language === Locales.GB_EN.language
  153 |         ? this.englishLanguageSelectorFlagIcon
  154 |         : this.germanLanguageSelectorFlagIcon;
  155 |     await flagIcon.click();
  156 |     await this.languageSelectorButton.waitFor({ state: 'attached' });
  157 |   }
  158 | 
  159 |   /** Click the Manage booking link/menu, opening the modal or navigating to the page. */
  160 |   async openManageBookingModal(): Promise<void> {
  161 |     console.log('Open Manage booking modal');
  162 |     await this.ensureMobileSidebarOpen();
  163 |     await this.manageBookingsLabel.click();
  164 |   }
  165 | 
  166 |   // ######## UI validations ########
  167 | 
  168 |   /** Validate an alert is displayed when login credentials are incorrect. */
  169 |   async validateIncorrectCredentialsAlertIsDisplayed(): Promise<void> {
  170 |     await this.incorrectCredentialsAlert.waitFor({ state: 'visible' });
  171 |   }
  172 | }
  173 | 
```