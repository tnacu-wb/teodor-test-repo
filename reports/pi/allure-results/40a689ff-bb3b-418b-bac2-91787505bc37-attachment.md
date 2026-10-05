# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/homepage.spec.ts >> PI Homepage - Search Console >> should search for a hotel by name
- Location: qa/tests/regressions/pi/homepage.spec.ts:18:7

# Error details

```
ReferenceError: browser is not defined
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
                - generic [ref=e108]:
                  - paragraph [ref=e110]: Our family rooms have a super comfy kingsize* bed for the grown-ups, and two pull-out or sofa beds for the kids. Travelling with a baby? We’ll get you a cot too.
                  - generic [ref=e111]: Show more
                - link [ref=e113] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
                  - button "Family friendly" [ref=e114]
            - generic [ref=e115]:
              - link [ref=e117] [cursor=pointer]:
                - /url: https://www.premierinnathome.com/?utm_source=premierinn.com&utm_medium=referral&utm_campaign=Homepage+BF
                - img "Premier Inn Bed" [ref=e118]
              - generic [ref=e119]:
                - heading "Cosy up to FREE bedding bundles" [level=4] [ref=e120]
                - paragraph [ref=e123]: With every bed and mattress order, you can snuggle up to a FREE bedding bundle, making those nights extra cosy and oh so restful! T&Cs apply.*
                - link [ref=e124] [cursor=pointer]:
                  - /url: https://www.premierinnathome.com/?utm_source=premierinn.com&utm_medium=referral&utm_campaign=Homepage+BF
                  - button "Sleep is our thing" [ref=e125]
            - generic [ref=e126]:
              - link [ref=e128] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/business.html
                - img "Business Booker" [ref=e129]
              - generic [ref=e130]:
                - heading "Premier Inn Business" [level=4] [ref=e131]
                - paragraph [ref=e134]: Our free online booking tool gives businesses of all sizes access to a guaranteed 5% and up to 15% discount* off our Flex rate.
                - link [ref=e135] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/business.html
                  - button "Premier Inn Business" [ref=e136]
        - heading "Plan your next getaway" [level=2] [ref=e139]
        - article [ref=e141]:
          - generic [ref=e142]:
            - generic [ref=e143]:
              - link [ref=e145] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/hiking-guide.html
                - img "Hiking Guide" [ref=e146]
              - link [ref=e148] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/hiking-guide.html
                - button "Hiking Guide" [ref=e149]
            - generic [ref=e150]:
              - link [ref=e152] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/cycling-trails.html
                - img "Biking Guide" [ref=e153]
              - link [ref=e155] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/cycling-trails.html
                - button "Biking Guide" [ref=e156]
            - generic [ref=e157]:
              - link [ref=e159] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                - img "Premier Inn Germany" [ref=e160]
              - link [ref=e162] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                - button "Premier Inn Germany" [ref=e163]
            - generic [ref=e164]:
              - link [ref=e166] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
                - img "Hotels in the Middle East" [ref=e167]
              - link [ref=e169] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
                - button "Hotels in the Middle East" [ref=e170]
        - heading "Discover our hotels" [level=2] [ref=e173]
        - article [ref=e175]:
          - generic [ref=e176]:
            - generic [ref=e177]:
              - link [ref=e179] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why.html
                - img "Premier Inn" [ref=e180]
              - generic [ref=e181]:
                - heading "Premier Inn" [level=4] [ref=e182]
                - generic [ref=e183]:
                  - paragraph [ref=e185]: With over 800 hotels across the UK and beyond, we really are everywhere. Sleep in a super comfy bed, enjoy Freeview TV, an en-suite bathroom with power shower and so much more.
                  - generic [ref=e186]: Show more
                - link [ref=e188] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why.html
                  - button "Discover Premier Inn" [ref=e189]
            - generic [ref=e190]:
              - link [ref=e192] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                - img "hub by Premier Inn" [ref=e193]
              - generic [ref=e194]:
                - heading "hub by Premier Inn" [level=4] [ref=e195]
                - generic [ref=e196]:
                  - paragraph [ref=e198]: Smart, stylish rooms across London and Edinburgh at great prices. Clever touchscreen room controls, free Wi-Fi, 40" Freeview TVs, high-powered monsoon showers and super-comfy beds.
                  - generic [ref=e199]: Show more
                - link [ref=e201] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                  - button "Discover hub" [ref=e202]
            - generic [ref=e203]:
              - link [ref=e205] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/zip.html
                - img "ZIP by Premier Inn" [ref=e206]
              - generic [ref=e207]:
                - heading "ZIP by Premier Inn" [level=4] [ref=e208]
                - generic [ref=e209]:
                  - paragraph [ref=e211]: Our idea is simple. Do the essentials brilliantly, then take away everything else. You get a small room, a simple stay and best of all, a price to match – from £25 a night. Now open in Cardiff.
                  - generic [ref=e212]: Show more
                - link [ref=e214] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/zip.html
                  - button "Discover ZIP" [ref=e215]
        - heading "Premier Inn information, news and features" [level=2] [ref=e218]
        - article [ref=e220]:
          - generic [ref=e221]:
            - generic [ref=e222]:
              - link [ref=e224] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/news.html
                - img "Premier Inn news" [ref=e225]
              - generic [ref=e226]:
                - heading "Premier Inn news" [level=4] [ref=e227]
                - paragraph [ref=e230]: Want to hear all the latest updates from Premier Inn, hub and ZIP? Visit our news page to stay up to date with competitions, travel tips and more.
                - link [ref=e231] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/news.html
                  - button "Premier Inn news" [ref=e232]
            - generic [ref=e233]:
              - link [ref=e235] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                - img "New hotels" [ref=e236]
              - generic [ref=e237]:
                - heading "New hotels" [level=4] [ref=e238]
                - paragraph [ref=e241]: From city centres to seafronts and beyond, we’ll be opening new hotels in great locations throughout the year. Check out our list of latest openings!
                - link [ref=e242] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                  - button "New hotels" [ref=e243]
            - generic [ref=e244]:
              - link [ref=e246] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/cleanliness.html
                - img "Committed to cleanliness" [ref=e247]
              - generic [ref=e248]:
                - heading "Committed to cleanliness" [level=4] [ref=e249]
                - paragraph [ref=e252]: To make sure we keep everyone safe, we have a rigorous, daily cleaning regime - an enhanced hygiene promise we call Premier Inn CleanProtect™.
                - link [ref=e253] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/cleanliness.html
                  - button "Premier Inn CleanProtect™" [ref=e254]
      - contentinfo [ref=e255]:
        - generic [ref=e260]:
          - generic [ref=e261]:
            - button "About us" [ref=e262] [cursor=pointer]
            - button "City breaks" [ref=e263] [cursor=pointer]
            - button "Summer breaks" [ref=e264] [cursor=pointer]
            - button "Winter breaks" [ref=e265] [cursor=pointer]
            - button "Business" [ref=e266] [cursor=pointer]
          - generic [ref=e269]:
            - paragraph [ref=e271]: Is it our comfy beds, our seriously tasty food, our great value hotels or our amazing teams that guests love so much? We reckon it’s a bit of everything. Take a look around to find out why we’re a much-loved, award-winning hotel chain, and rest easy knowing our range of rates give you both choice and flexibility.
            - generic [ref=e272]:
              - generic [ref=e274]:
                - generic [ref=e275]: Get in touch
                - list [ref=e276]:
                  - listitem [ref=e277]:
                    - link "Contact us" [ref=e278] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/contact-us.html
                  - listitem [ref=e279]:
                    - link "FAQs" [ref=e280] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/faq.html
                  - listitem [ref=e281]:
                    - link "Group bookings" [ref=e282] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/groups.html
                  - listitem [ref=e283]:
                    - link "Affiliates" [ref=e284] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/premier-inn-affiliate-programme.html
                  - listitem [ref=e285]:
                    - link "International development" [ref=e286] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/business/international-development.html
                  - listitem [ref=e287]:
                    - link "Careers" [ref=e288] [cursor=pointer]:
                      - /url: https://www.whitbreadcareers.com/our-brands/premier-inn/
              - generic [ref=e290]:
                - generic [ref=e291]: Legal
                - list [ref=e292]:
                  - listitem [ref=e293]:
                    - link "Terms and conditions" [ref=e294] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/booking-terms-and-conditions.html
                  - listitem [ref=e295]:
                    - link "Terms of use" [ref=e296] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/terms-of-use.html
                  - listitem [ref=e297]:
                    - link "Privacy policy" [ref=e298] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/privacy-policy.html
                  - listitem [ref=e299]:
                    - link "Cookies notice" [ref=e300] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/how-we-use-cookies.html
                  - listitem [ref=e301]:
                    - link "Good Night Guarantee" [ref=e302] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/good-night-guarantee.html
                  - listitem [ref=e303]:
                    - link "Modern Slavery Act statement" [ref=e304] [cursor=pointer]:
                      - /url: https://cdn.whitbread.co.uk/media/2023/05/23926_MSA-Report-2022-23_Stage2_230510_14.48-Final-High-Res.pdf
              - generic [ref=e306]:
                - generic [ref=e307]: Locations
                - list [ref=e308]:
                  - listitem [ref=e309]:
                    - link "Hotel directory" [ref=e310] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels.html
                  - listitem [ref=e311]:
                    - link "New hotels" [ref=e312] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                  - listitem [ref=e313]:
                    - link "Local guides" [ref=e314] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/city-breaks.html
                  - listitem [ref=e315]:
                    - link "Short breaks" [ref=e316] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/short-breaks.html
                  - listitem [ref=e317]:
                    - link "Hotels in Germany" [ref=e318] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                  - listitem [ref=e319]:
                    - link "Dubai and beyond" [ref=e320] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
              - generic [ref=e322]:
                - generic [ref=e323]: Our hotels
                - list [ref=e324]:
                  - listitem [ref=e325]:
                    - link "Our rooms" [ref=e326] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/our-rooms.html
                  - listitem [ref=e327]:
                    - link "Family friendly" [ref=e328] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
                  - listitem [ref=e329]:
                    - link "Sleep" [ref=e330] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep.html
                  - listitem [ref=e331]:
                    - link "Food & drink" [ref=e332] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
                  - listitem [ref=e333]:
                    - link "hub by Premier Inn" [ref=e334] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                  - listitem [ref=e335]:
                    - link "ZIP by Premier Inn" [ref=e336] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/zip.html
              - generic [ref=e338]:
                - generic [ref=e339]: Find out more
                - list [ref=e340]:
                  - listitem [ref=e341]:
                    - link "About us" [ref=e342] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why.html
                  - listitem [ref=e343]:
                    - link "Rest easy" [ref=e344] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/resteasy.html
                  - listitem [ref=e345]:
                    - link "GOSH Charity" [ref=e346] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/gosh-childrens-charity.html
                  - listitem [ref=e347]:
                    - link "Force for Good" [ref=e348] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/force-for-good.html
                  - listitem [ref=e349]:
                    - link "Disabled access" [ref=e350] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/disabled-access.html
                  - listitem [ref=e351]:
                    - link "News" [ref=e352] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/news.html
              - generic [ref=e354]:
                - generic [ref=e355]: Everything else
                - list [ref=e356]:
                  - listitem [ref=e357]:
                    - link "Our rates" [ref=e358] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/rates.html
                  - listitem [ref=e359]:
                    - link "Offers" [ref=e360] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/offers.html
                  - listitem [ref=e361]:
                    - link "Buy our bed" [ref=e362] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/buy-our-bed.html
                  - listitem [ref=e363]:
                    - link "Mobile apps" [ref=e364] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/apps.html
                  - listitem [ref=e365]:
                    - link "We value difference" [ref=e366] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/diversity-and-inclusion.html
                  - listitem [ref=e367]:
                    - link "Sitemap" [ref=e368] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sitemap.html
        - generic [ref=e371]:
          - paragraph [ref=e372]: "Get #PremierInnspiration, short break ideas and much more delivered straight to your inbox"
          - button "Sign up" [ref=e373] [cursor=pointer]
        - generic [ref=e374]:
          - generic [ref=e376]: © 2026 Premier Inn
          - generic [ref=e378]:
            - link [ref=e379] [cursor=pointer]:
              - /url: https://www.facebook.com/premierinn
              - img "Facebook icon" [ref=e380]
            - link [ref=e381] [cursor=pointer]:
              - /url: https://twitter.com/premierinn
              - img "Twitter icon" [ref=e382]
            - link [ref=e383] [cursor=pointer]:
              - /url: https://www.instagram.com/premierinn
              - img "Instagram icon" [ref=e384]
  - alert [ref=e385]
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
  56  | 
  57  | /**
  58  |  * Calendar sub-component within the search console (react-datepicker, PI / non-IB variant).
  59  |  */
  60  | export class CalendarComponent {
  61  |   private readonly page: Page = global.page;
  62  | 
  63  |   // ######## UI elements/properties ########
  64  | 
  65  |   readonly nextMonthButton: Locator;
  66  | 
  67  |   constructor() {
  68  |     this.nextMonthButton = this.page.locator('button[aria-label="Go to next month"], button[aria-label="Next Month"]');
  69  |   }
  70  | 
  71  |   // ######## UI actions/navigation ########
  72  | 
  73  |   /**
  74  |    * Locator for an enabled day option using the localized month name and
  75  |    * react-datepicker's day class, matching the legacy DatePickerBase selector.
  76  |    */
  77  |   dayOption(date: Date): Locator {
  78  |     const localeTag = getCurrentLocale().name === Locales.DE_DE.name ? 'de-DE' : 'en-GB';
  79  |     const monthName = date.toLocaleString(localeTag, { month: 'long' });
  80  |     const day = String(date.getDate()).padStart(2, '0');
  81  |     const year = date.getFullYear();
  82  |     const dayClass = `react-datepicker__day--0${day}`;
  83  |     const datePickerId = `date-picker-day-${day}${date.getMonth() + 1}${year}`;
  84  | 
  85  |     return this.page.locator(
  86  |       `[aria-label*="${monthName}"][class*="${dayClass}"][aria-disabled="false"], button#${datePickerId}`
  87  |     );
  88  |   }
  89  | 
  90  |   // ######## UI validations ########
  91  | }
  92  | 
  93  | /**
  94  |  * PI Search Console Component — the location/date/room search widget
  95  |  * on premierinn.com (Homepage, HDP).
  96  |  * Selectors mirror qa/reference searchConsoleBase.js (PI / non-'ib' branch).
  97  |  */
  98  | export class SearchConsoleComponent {
  99  |   private readonly page: Page = global.page;
  100 | 
  101 |   // ######## UI elements/properties ########
  102 | 
  103 |   /** Collapsed read-only summary shown after a search has been performed. */
  104 |   readonly searchSummaryLocationLabel: Locator;
  105 |   readonly locationInput: Locator;
  106 |   readonly locationClearButton: Locator;
  107 |   readonly suggestionsList: Locator;
  108 |   readonly suggestedHotelsList: Locator;
  109 |   readonly datesButton: Locator;
  110 |   readonly submitButton: Locator;
  111 |   readonly roomPicker: RoomPickerComponent;
  112 |   readonly calendar: CalendarComponent;
  113 | 
  114 |   constructor() {
  115 |     this.searchSummaryLocationLabel = this.page.locator('p[data-testid="search-summary-location"]');
  116 |     this.locationInput = this.page.locator('input[data-testid="locationPicker-locationPlaceholder"]');
  117 |     this.locationClearButton = this.page.locator('div[data-testid="locationPicker-clearLocationButton"]');
  118 |     this.suggestionsList = this.page.locator('[data-testid="locationPicker-autocompleteList"]');
  119 |     this.suggestedHotelsList = this.suggestionsList.locator('[data-testid="locationPicker-hotelsLabel"] ~ li[aria-selected="false"]');
  120 |     this.datesButton = this.page.locator('input[aria-label="datepicker-input"]');
  121 |     this.submitButton = this.page.locator('button[name="search-button"]');
  122 | 
  123 |     this.roomPicker = new RoomPickerComponent();
  124 |     this.calendar = new CalendarComponent();
  125 |   }
  126 | 
  127 |   // ######## UI actions/navigation ########
  128 | 
  129 |   /**
  130 |    * Expand the search console from its collapsed summary state, if present.
  131 |    */
  132 |   async expandIfCollapsed(): Promise<void> {
  133 |     try {
  134 |       await this.searchSummaryLocationLabel.waitFor({ state: 'visible', timeout: 2000 });
  135 |       await this.searchSummaryLocationLabel.click();
  136 |     } catch {
  137 |       // Already expanded (e.g. first visit to Homepage) — nothing to do.
  138 |     }
  139 |   }
  140 | 
  141 |   /**
  142 |    * Clear the pre-filled location input.
  143 |    */
  144 |   async clearLocation(): Promise<void> {
  145 |     await this.expandIfCollapsed();
  146 |     await this.locationInput.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
  147 |     await this.locationInput.click();
  148 |     await this.locationClearButton.click();
  149 |   }
  150 | 
  151 |   /**
  152 |    * Type a hotel name and select the first matching autocomplete hotel suggestion.
  153 |    * Retries typing since the debounced autocomplete call occasionally misses the first attempt.
  154 |    */
  155 |   async searchForHotel(hotelName: string): Promise<void> {
> 156 |     await this.locationInput.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
      |                                                                   ^ ReferenceError: browser is not defined
  157 |     await this.locationInput.click();
  158 |     await this.page.context().setHTTPCredentials(null); // Clear any HTTP auth credentials to avoid interfering with autocomplete requests
  159 |     await this.locationInput.fill(hotelName.substring(0, hotelName.length - 1));
  160 |     await this.locationInput.press(hotelName.charAt(hotelName.length - 1));
  161 | 
  162 |     const firstSuggestion = this.suggestionsList.locator('li, [role="option"]').first();
  163 |     await firstSuggestion.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
  164 |     await firstSuggestion.click();
  165 |   }
  166 | 
  167 |   /**
  168 |    * Click the submit/search button and wait for the resulting page navigation to settle,
  169 |    * so callers don't race a transitional render with stale availability data.
  170 |    */
  171 |   async submit(): Promise<void> {
  172 |     await this.submitButton.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
  173 |     await this.submitButton.click();
  174 |     await this.page.waitForLoadState('networkidle', { timeout: 30000 }).catch(() => {});
  175 |   }
  176 | 
  177 |   /**
  178 |    * Set check-in and check-out dates using the calendar picker.
  179 |    */
  180 |   async setDates(checkInDate: string, checkOutDate: string): Promise<void> {
  181 |     await this.datesButton.click();
  182 |     await this.selectDate(checkInDate);
  183 |     await this.selectDate(checkOutDate);
  184 |   }
  185 | 
  186 |   /**
  187 |    * Set room configuration (adults/children) via the per-room dropdowns.
  188 |    */
  189 |   async setRooms(rooms: RoomConfig[]): Promise<void> {
  190 |     await this.roomPicker.dropdownButton.click();
  191 |     await this.roomPicker.dropdownPanel.waitFor({ state: 'visible' });
  192 | 
  193 |     for (let i = 0; i < rooms.length; i++) {
  194 |       const room = rooms[i];
  195 |       await this.roomPicker.selectAdults(i, room.adults);
  196 |       await this.roomPicker.selectChildren(i, room.children);
  197 |     }
  198 | 
  199 |     await this.roomPicker.doneButton.click();
  200 |   }
  201 | 
  202 |   /**
  203 |    * Perform a full search: set location, dates, rooms, then submit.
  204 |    */
  205 |   async performSearch(options: {
  206 |     hotelName: string;
  207 |     checkInDate: string;
  208 |     checkOutDate: string;
  209 |     rooms: RoomConfig[];
  210 |   }): Promise<void> {
  211 |     await this.clearLocation();
  212 |     await this.searchForHotel(options.hotelName);
  213 |     await this.setDates(options.checkInDate, options.checkOutDate);
  214 |     await this.setRooms(options.rooms);
  215 |     await this.submit();
  216 |   }
  217 | 
  218 |   // ######## UI validations ########
  219 | 
  220 |   // ######## Private helpers ########
  221 | 
  222 |   private async selectDate(isoDate: string): Promise<void> {
  223 |     const date = new Date(isoDate);
  224 |     const dayOption = this.calendar.dayOption(date);
  225 | 
  226 |     for (let attempt = 0; attempt < 12; attempt++) {
  227 |       try {
  228 |         await dayOption.waitFor({ state: 'visible', timeout: 1000 });
  229 |         await dayOption.click();
  230 |         return;
  231 |       } catch {
  232 |         await this.calendar.nextMonthButton.click();
  233 |       }
  234 |     }
  235 | 
  236 |     throw new Error(`Could not find date ${isoDate} in calendar after 12 month advances`);
  237 |   }
  238 | }
  239 | 
```