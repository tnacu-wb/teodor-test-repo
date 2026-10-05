# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-logged-user-uk-hotel-semi-flex-poa-shorten-period.spec.ts >> End2end: Amend, Logged in user, PI UK Hotel, Semi-Flex, pay now, shorten period >> Test Amend, Logged in user: shorten period and verify changes. TestCase ID: 266200.
- Location: qa/tests/regressions/pi/baseline-e2e-logged-user-uk-hotel-semi-flex-poa-shorten-period.spec.ts:27:8

# Error details

```
GraphQLError: HTTP 400 (client error, non-retryable) for query "createPaymentMutation": {"errors":[{"message":"invalid type for variable: 'createPaymentCriteria'","extensions":{"name":"createPaymentCriteria","code":"VALIDATION_INVALID_TYPE_VARIABLE"}}]}
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
  224 |       const responseExcerpt = JSON.stringify(body).slice(0, 1000);
> 225 |       throw new GraphQLError(
      |             ^ GraphQLError: HTTP 400 (client error, non-retryable) for query "createPaymentMutation": {"errors":[{"message":"invalid type for variable: 'createPaymentCriteria'","extensions":{"name":"createPaymentCriteria","code":"VALIDATION_INVALID_TYPE_VARIABLE"}}]}
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
  242 |       throw new GraphQLError(
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
```