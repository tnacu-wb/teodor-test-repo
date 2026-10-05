# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-logged-user-uk-hotel-semi-flex-poa-shorten-period.spec.ts >> End2end: Amend, Logged in user, PI UK Hotel, Semi-Flex, pay now, shorten period >> Test Amend, Logged in user: shorten period and verify changes. TestCase ID: 266200.
- Location: qa/tests/regressions/pi/baseline-e2e-logged-user-uk-hotel-semi-flex-poa-shorten-period.spec.ts:27:8

# Error details

```
GraphQLError: HTTP 400 (client error, non-retryable) for query "createPaymentMutation"
```

# Page snapshot

```yaml
- generic [ref=e1]:
  - generic [ref=e2]:
    - generic [ref=e5]:
      - banner [ref=e6]:
        - generic [ref=e9]:
          - link [ref=e12] [cursor=pointer]:
            - /url: https://www.uat.premierinn.digital/gb/en/home.html
          - generic [ref=e15]:
            - img [ref=e18] [cursor=pointer]
            - generic [ref=e19]:
              - paragraph [ref=e22] [cursor=pointer]: Discover Premier Inn
              - paragraph [ref=e25] [cursor=pointer]: Business
              - paragraph [ref=e27] [cursor=pointer]: Manage booking
      - main [ref=e28]:
        - generic [ref=e34]:
          - heading [level=1] [ref=e36]: Get a great night's sleep
          - generic [ref=e40]:
            - combobox [ref=e45] [cursor=pointer]
            - textbox [ref=e56] [cursor=pointer]:
              - /placeholder: Check In | Check Out
              - text: Today | Tomorrow
            - button [ref=e60] [cursor=pointer]:
              - generic: 1 adult, 1 room
            - button [ref=e61] [cursor=pointer]: Search
    - generic [ref=e62]:
      - generic [ref=e65]:
        - heading [level=1] [ref=e68]: From a comfy bed to a great night's sleep, you know what you're getting with us
        - heading [level=4] [ref=e71]: Our Premier Inn hotels offer comfort you can count on thanks to our flexible rates, unlimited breakfast and super-comfy beds - what's not to love?
        - article [ref=e73]:
          - generic [ref=e74]:
            - generic [ref=e75]:
              - link [ref=e77] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/sleep/premier-plus.html
              - generic [ref=e79]:
                - heading [level=4] [ref=e80]: Premier Plus rooms
                - paragraph [ref=e83]: More comfort. More convenience. More connectivity. That’s our Premier Plus rooms. Just perfect if you fancy a little more from your stay. Go on, treat yourself.
                - link [ref=e84] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/sleep/premier-plus.html
                  - button [ref=e85]: Premier Plus rooms
            - generic [ref=e86]:
              - link [ref=e88] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
              - generic [ref=e90]:
                - heading [level=4] [ref=e91]: Food & drink
                - paragraph [ref=e94]: People rave about our tempting Premier Inn Breakfast. And in the evenings, you can sit down to a mouth-watering menu of delicious dishes from our restaurant.
                - link [ref=e95] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
                  - button [ref=e96]: Ready to tuck in?
          - generic [ref=e97]:
            - generic [ref=e98]:
              - link [ref=e100] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
              - generic [ref=e102]:
                - heading [level=4] [ref=e103]: Family friendly
                - generic [ref=e104]:
                  - paragraph [ref=e106]: Our family rooms have a super comfy kingsize* bed for the grown-ups, and two pull-out or sofa beds for the kids. Travelling with a baby? We’ll get you a cot too.
                  - generic [ref=e107]: Show more
                - link [ref=e109] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
                  - button [ref=e110]: Family friendly
            - generic [ref=e111]:
              - link [ref=e113] [cursor=pointer]:
                - /url: https://www.premierinnathome.com/?utm_source=premierinn.com&utm_medium=referral&utm_campaign=Homepage+BF
              - generic [ref=e115]:
                - heading [level=4] [ref=e116]: Cosy up to FREE bedding bundles
                - paragraph [ref=e119]: With every bed and mattress order, you can snuggle up to a FREE bedding bundle, making those nights extra cosy and oh so restful! T&Cs apply.*
                - link [ref=e120] [cursor=pointer]:
                  - /url: https://www.premierinnathome.com/?utm_source=premierinn.com&utm_medium=referral&utm_campaign=Homepage+BF
                  - button [ref=e121]: Sleep is our thing
            - generic [ref=e122]:
              - link [ref=e124] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/business.html
              - generic [ref=e126]:
                - heading [level=4] [ref=e127]: Premier Inn Business
                - paragraph [ref=e130]: Our free online booking tool gives businesses of all sizes access to a guaranteed 5% and up to 15% discount* off our Flex rate.
                - link [ref=e131] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/business.html
                  - button [ref=e132]: Premier Inn Business
        - heading [level=2] [ref=e135]: Plan your next getaway
        - article [ref=e137]:
          - generic [ref=e138]:
            - generic [ref=e139]:
              - link [ref=e141] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/hiking-guide.html
              - link [ref=e144] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/hiking-guide.html
                - button [ref=e145]: Hiking Guide
            - generic [ref=e146]:
              - link [ref=e148] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/cycling-trails.html
              - link [ref=e151] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/cycling-trails.html
                - button [ref=e152]: Biking Guide
            - generic [ref=e153]:
              - link [ref=e155] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
              - link [ref=e158] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                - button [ref=e159]: Premier Inn Germany
            - generic [ref=e160]:
              - link [ref=e162] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
              - link [ref=e165] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
                - button [ref=e166]: Hotels in the Middle East
        - heading [level=2] [ref=e169]: Discover our hotels
        - article [ref=e171]:
          - generic [ref=e172]:
            - generic [ref=e173]:
              - link [ref=e175] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why.html
              - generic [ref=e177]:
                - heading [level=4] [ref=e178]: Premier Inn
                - generic [ref=e179]:
                  - paragraph [ref=e181]: With over 800 hotels across the UK and beyond, we really are everywhere. Sleep in a super comfy bed, enjoy Freeview TV, an en-suite bathroom with power shower and so much more.
                  - generic [ref=e182]: Show more
                - link [ref=e184] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why.html
                  - button [ref=e185]: Discover Premier Inn
            - generic [ref=e186]:
              - link [ref=e188] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/hub.html
              - generic [ref=e190]:
                - heading [level=4] [ref=e191]: hub by Premier Inn
                - generic [ref=e192]:
                  - paragraph [ref=e194]: Smart, stylish rooms across London and Edinburgh at great prices. Clever touchscreen room controls, free Wi-Fi, 40" Freeview TVs, high-powered monsoon showers and super-comfy beds.
                  - generic [ref=e195]: Show more
                - link [ref=e197] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                  - button [ref=e198]: Discover hub
            - generic [ref=e199]:
              - link [ref=e201] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/zip.html
              - generic [ref=e203]:
                - heading [level=4] [ref=e204]: ZIP by Premier Inn
                - generic [ref=e205]:
                  - paragraph [ref=e207]: Our idea is simple. Do the essentials brilliantly, then take away everything else. You get a small room, a simple stay and best of all, a price to match – from £25 a night. Now open in Cardiff.
                  - generic [ref=e208]: Show more
                - link [ref=e210] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/zip.html
                  - button [ref=e211]: Discover ZIP
        - heading [level=2] [ref=e214]: Premier Inn information, news and features
        - article [ref=e216]:
          - generic [ref=e217]:
            - generic [ref=e218]:
              - link [ref=e220] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/news.html
              - generic [ref=e222]:
                - heading [level=4] [ref=e223]: Premier Inn news
                - paragraph [ref=e226]: Want to hear all the latest updates from Premier Inn, hub and ZIP? Visit our news page to stay up to date with competitions, travel tips and more.
                - link [ref=e227] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/news.html
                  - button [ref=e228]: Premier Inn news
            - generic [ref=e229]:
              - link [ref=e231] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
              - generic [ref=e233]:
                - heading [level=4] [ref=e234]: New hotels
                - paragraph [ref=e237]: From city centres to seafronts and beyond, we’ll be opening new hotels in great locations throughout the year. Check out our list of latest openings!
                - link [ref=e238] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                  - button [ref=e239]: New hotels
            - generic [ref=e240]:
              - link [ref=e242] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/why/cleanliness.html
              - generic [ref=e244]:
                - heading [level=4] [ref=e245]: Committed to cleanliness
                - paragraph [ref=e248]: To make sure we keep everyone safe, we have a rigorous, daily cleaning regime - an enhanced hygiene promise we call Premier Inn CleanProtect™.
                - link [ref=e249] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/gb/en/why/cleanliness.html
                  - button [ref=e250]: Premier Inn CleanProtect™
      - contentinfo [ref=e251]:
        - generic [ref=e256]:
          - generic [ref=e257]:
            - button [ref=e258] [cursor=pointer]: About us
            - button [ref=e259] [cursor=pointer]: City breaks
            - button [ref=e260] [cursor=pointer]: Summer breaks
            - button [ref=e261] [cursor=pointer]: Winter breaks
            - button [ref=e262] [cursor=pointer]: Business
          - generic [ref=e265]:
            - paragraph [ref=e267]: Is it our comfy beds, our seriously tasty food, our great value hotels or our amazing teams that guests love so much? We reckon it’s a bit of everything. Take a look around to find out why we’re a much-loved, award-winning hotel chain, and rest easy knowing our range of rates give you both choice and flexibility.
            - generic [ref=e268]:
              - generic [ref=e270]:
                - generic [ref=e271]: Get in touch
                - list [ref=e272]:
                  - listitem [ref=e273]:
                    - link [ref=e274] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/contact-us.html
                      - text: Contact us
                  - listitem [ref=e275]:
                    - link [ref=e276] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/faq.html
                      - text: FAQs
                  - listitem [ref=e277]:
                    - link [ref=e278] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/groups.html
                      - text: Group bookings
                  - listitem [ref=e279]:
                    - link [ref=e280] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/premier-inn-affiliate-programme.html
                      - text: Affiliates
                  - listitem [ref=e281]:
                    - link [ref=e282] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/business/international-development.html
                      - text: International development
                  - listitem [ref=e283]:
                    - link [ref=e284] [cursor=pointer]:
                      - /url: https://www.whitbreadcareers.com/our-brands/premier-inn/
                      - text: Careers
              - generic [ref=e286]:
                - generic [ref=e287]: Legal
                - list [ref=e288]:
                  - listitem [ref=e289]:
                    - link [ref=e290] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/booking-terms-and-conditions.html
                      - text: Terms and conditions
                  - listitem [ref=e291]:
                    - link [ref=e292] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/terms-of-use.html
                      - text: Terms of use
                  - listitem [ref=e293]:
                    - link [ref=e294] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/privacy-policy.html
                      - text: Privacy policy
                  - listitem [ref=e295]:
                    - link [ref=e296] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/how-we-use-cookies.html
                      - text: Cookies notice
                  - listitem [ref=e297]:
                    - link [ref=e298] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/good-night-guarantee.html
                      - text: Good Night Guarantee
                  - listitem [ref=e299]:
                    - link [ref=e300] [cursor=pointer]:
                      - /url: https://cdn.whitbread.co.uk/media/2023/05/23926_MSA-Report-2022-23_Stage2_230510_14.48-Final-High-Res.pdf
                      - text: Modern Slavery Act statement
              - generic [ref=e302]:
                - generic [ref=e303]: Locations
                - list [ref=e304]:
                  - listitem [ref=e305]:
                    - link [ref=e306] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels.html
                      - text: Hotel directory
                  - listitem [ref=e307]:
                    - link [ref=e308] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                      - text: New hotels
                  - listitem [ref=e309]:
                    - link [ref=e310] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/city-breaks.html
                      - text: Local guides
                  - listitem [ref=e311]:
                    - link [ref=e312] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/short-breaks.html
                      - text: Short breaks
                  - listitem [ref=e313]:
                    - link [ref=e314] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                      - text: Hotels in Germany
                  - listitem [ref=e315]:
                    - link [ref=e316] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
                      - text: Dubai and beyond
              - generic [ref=e318]:
                - generic [ref=e319]: Our hotels
                - list [ref=e320]:
                  - listitem [ref=e321]:
                    - link [ref=e322] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/our-rooms.html
                      - text: Our rooms
                  - listitem [ref=e323]:
                    - link [ref=e324] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
                      - text: Family friendly
                  - listitem [ref=e325]:
                    - link [ref=e326] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep.html
                      - text: Sleep
                  - listitem [ref=e327]:
                    - link [ref=e328] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
                      - text: Food & drink
                  - listitem [ref=e329]:
                    - link [ref=e330] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                      - text: hub by Premier Inn
                  - listitem [ref=e331]:
                    - link [ref=e332] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/zip.html
                      - text: ZIP by Premier Inn
              - generic [ref=e334]:
                - generic [ref=e335]: Find out more
                - list [ref=e336]:
                  - listitem [ref=e337]:
                    - link [ref=e338] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why.html
                      - text: About us
                  - listitem [ref=e339]:
                    - link [ref=e340] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/resteasy.html
                      - text: Rest easy
                  - listitem [ref=e341]:
                    - link [ref=e342] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/gosh-childrens-charity.html
                      - text: GOSH Charity
                  - listitem [ref=e343]:
                    - link [ref=e344] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/force-for-good.html
                      - text: Force for Good
                  - listitem [ref=e345]:
                    - link [ref=e346] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/disabled-access.html
                      - text: Disabled access
                  - listitem [ref=e347]:
                    - link [ref=e348] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/news.html
                      - text: News
              - generic [ref=e350]:
                - generic [ref=e351]: Everything else
                - list [ref=e352]:
                  - listitem [ref=e353]:
                    - link [ref=e354] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/rates.html
                      - text: Our rates
                  - listitem [ref=e355]:
                    - link [ref=e356] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/offers.html
                      - text: Offers
                  - listitem [ref=e357]:
                    - link [ref=e358] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sleep/buy-our-bed.html
                      - text: Buy our bed
                  - listitem [ref=e359]:
                    - link [ref=e360] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/why/apps.html
                      - text: Mobile apps
                  - listitem [ref=e361]:
                    - link [ref=e362] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/terms/diversity-and-inclusion.html
                      - text: We value difference
                  - listitem [ref=e363]:
                    - link [ref=e364] [cursor=pointer]:
                      - /url: https://www.uat.premierinn.digital/gb/en/sitemap.html
                      - text: Sitemap
        - generic [ref=e367]:
          - paragraph [ref=e368]: "Get #PremierInnspiration, short break ideas and much more delivered straight to your inbox"
          - button [ref=e369] [cursor=pointer]: Sign up
        - generic [ref=e370]:
          - generic [ref=e372]: © 2026 Premier Inn
          - generic [ref=e374]:
            - link [ref=e375] [cursor=pointer]:
              - /url: https://www.facebook.com/premierinn
            - link [ref=e377] [cursor=pointer]:
              - /url: https://twitter.com/premierinn
            - link [ref=e379] [cursor=pointer]:
              - /url: https://www.instagram.com/premierinn
  - alert [ref=e381]
  - generic:
    - region "Notifications-top"
    - region "Notifications-top-left"
    - region "Notifications-top-right"
    - region "Notifications-bottom-left"
    - region "Notifications-bottom"
    - region "Notifications-bottom-right"
  - dialog [ref=e384]:
    - banner [ref=e385]:
      - button [ref=e386] [cursor=pointer]
    - generic [ref=e393]:
      - paragraph [ref=e394]: Log in to your Premier Inn account
      - generic [ref=e396]:
        - group [ref=e397]:
          - generic [ref=e398]:
            - group [ref=e400]:
              - generic [ref=e401]: Email address
              - textbox [ref=e403]:
                - /placeholder: Email address
                - text: uk_0@yopmail.com
            - group [ref=e405]:
              - generic [ref=e406]: Password
              - textbox [ref=e408]:
                - /placeholder: Password
                - text: Password01
            - generic [ref=e409] [cursor=pointer]: Forgotten password?
        - button [active] [ref=e412] [cursor=pointer]: Log in
      - generic [ref=e413]:
        - paragraph [ref=e414]: Don't have an account yet?
        - link [ref=e415] [cursor=pointer]:
          - /url: https://www.uat.premierinn.digital/gb/en/account/register.html
          - text: Sign up here
      - paragraph [ref=e417]: Travelling for business?
      - link [ref=e419] [cursor=pointer]:
        - /url: https://business.uat.premierinn.digital/en-gb/account/login?intcmp=piLogInModalLink
        - text: Log in to Premier Inn Business here
  - dialog [ref=e423]:
    - banner [ref=e424]:
      - generic [ref=e426]:
        - generic [ref=e427]: We'd like to send you notifications
        - button "Close" [ref=e431] [cursor=pointer]
    - generic [ref=e438]:
      - generic [ref=e439]:
        - text: Get updated with our great offers, news and services. These can be turned off at any time. Read our
        - link "Privacy Policy" [ref=e440] [cursor=pointer]:
          - /url: /gb/en/terms/privacy-policy.html
        - text: for more information.
      - generic [ref=e441]:
        - button "Allow" [ref=e442] [cursor=pointer]
        - button "Don't allow" [ref=e443] [cursor=pointer]
```

# Test source

```ts
  124 |         'non-empty URL string',
  125 |         this.baseURL === '' ? '(empty string)' : String(this.baseURL),
  126 |         'baseURL',
  127 |       );
  128 |     }
  129 | 
  130 |     const operationExcerpt = this.extractOperationExcerpt(query);
  131 |     let lastError: GraphQLError | undefined;
  132 | 
  133 |     for (let attempt = 0; attempt <= this.retries; attempt++) {
  134 |       if (attempt > 0) {
  135 |         const delay = this.retryDelay * Math.pow(2, attempt - 1);
  136 |         console.error(
  137 |           `[GraphQLClient] [${new Date().toISOString()}] Retry attempt ${attempt}/${this.retries} for "${operationExcerpt}" — waiting ${delay}ms before next request.`,
  138 |         );
  139 |         await this.sleep(delay);
  140 |       }
  141 | 
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
> 224 |       throw new GraphQLError(
      |             ^ GraphQLError: HTTP 400 (client error, non-retryable) for query "createPaymentMutation"
  225 |         `HTTP ${status} ${isRetryable ? '(server error, retryable)' : '(client error, non-retryable)'} for query "${operationExcerpt}"`,
  226 |         operationExcerpt,
  227 |         variables,
  228 |         undefined,
  229 |         { status, body },
  230 |       );
  231 |     }
  232 | 
  233 |     // Check for GraphQL-level errors
  234 |     const graphqlBody = body as GraphQLResponseBody;
  235 |     if (graphqlBody.errors && graphqlBody.errors.length > 0) {
  236 |       const isRetryable = this.hasRetryableGraphQLError(graphqlBody.errors);
  237 |       const errorMessages = graphqlBody.errors
  238 |         .map((e) => e.message)
  239 |         .join('; ');
  240 | 
  241 |       throw new GraphQLError(
  242 |         `GraphQL error${isRetryable ? ' (retryable)' : ' (non-retryable)'} for query "${operationExcerpt}": ${errorMessages}`,
  243 |         operationExcerpt,
  244 |         variables,
  245 |         graphqlBody.errors,
  246 |         { status, body },
  247 |       );
  248 |     }
  249 | 
  250 |     // Return the data field
  251 |     if (graphqlBody.data === undefined || graphqlBody.data === null) {
  252 |       throw new GraphQLError(
  253 |         `GraphQL response has no "data" field for query "${operationExcerpt}"`,
  254 |         operationExcerpt,
  255 |         variables,
  256 |         undefined,
  257 |         { status, body },
  258 |       );
  259 |     }
  260 | 
  261 |     return graphqlBody.data as T;
  262 |   }
  263 | 
  264 |   /**
  265 |    * Determine if a GraphQLError is retryable (5xx HTTP or 5xx-class GraphQL errorType).
  266 |    */
  267 |   private isRetryableError(error: GraphQLError): boolean {
  268 |     // Check HTTP status in the response
  269 |     const resp = error.response as { status?: number } | undefined;
  270 |     if (resp?.status !== undefined && resp.status >= 500) {
  271 |       return true;
  272 |     }
  273 | 
  274 |     // Check GraphQL errors for 5xx errorType in extensions
  275 |     if (error.errors && Array.isArray(error.errors)) {
  276 |       return this.hasRetryableGraphQLError(error.errors as GraphQLResponseError[]);
  277 |     }
  278 | 
  279 |     return false;
  280 |   }
  281 | 
  282 |   /**
  283 |    * Check if any GraphQL error has a 5xx-class errorType in extensions.
  284 |    */
  285 |   private hasRetryableGraphQLError(errors: GraphQLResponseError[]): boolean {
  286 |     return errors.some((err) => {
  287 |       const errorType = err.extensions?.errorType;
  288 |       if (typeof errorType === 'string') {
  289 |         // Match errorType containing "5" indicating 5xx (e.g., "500", "503", "INTERNAL_SERVER_ERROR")
  290 |         return /^5\d{2}$/.test(errorType) || errorType.startsWith('5');
  291 |       }
  292 |       return false;
  293 |     });
  294 |   }
  295 | 
  296 |   /**
  297 |    * Extract a short excerpt from the query string for use in error messages.
  298 |    * Attempts to find the operation name, falls back to first 80 chars.
  299 |    */
  300 |   private extractOperationExcerpt(query: string): string {
  301 |     // Try to extract the operation name (e.g., "query GetBasket" -> "GetBasket")
  302 |     const match = query.match(/(?:query|mutation|subscription)\s+(\w+)/);
  303 |     if (match) {
  304 |       return match[1];
  305 |     }
  306 |     // Fall back to first 80 characters of the query, trimmed
  307 |     return query.replace(/\s+/g, ' ').trim().slice(0, 80);
  308 |   }
  309 | 
  310 |   /**
  311 |    * Log full details of a failed operation for debugging.
  312 |    * Includes timestamp, URL, operation, sanitized variables, HTTP status, and response body.
  313 |    */
  314 |   private logFailure(
  315 |     operation: string,
  316 |     variables: Record<string, unknown> | undefined,
  317 |     error: GraphQLError,
  318 |   ): void {
  319 |     const resp = error.response as { status?: number; body?: unknown } | undefined;
  320 |     console.error(`[GraphQLClient] [${new Date().toISOString()}] Request failed:`, {
  321 |       url: this.baseURL,
  322 |       operation,
  323 |       variables: this.sanitizeVariables(variables),
  324 |       status: resp?.status,
```