# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts >> PI Baseline E2E - Guest PIBA Pay on Arrival Amendment >> Test Book as Guest user: 1 night, 1 room (Double), 2 adults, Flex rate, meals, donations - POA BAC card and Amend by change room type. TestCase ID: 380779.
- Location: tests/regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts:51:7

# Error details

```
GraphQLError: GraphQL error (retryable) for query "basket": Expected Iterable, but did not find one for field "BasketItem.details".
```

# Page snapshot

```yaml
- generic [active] [ref=f54e1]:
  - generic [ref=f54e3]:
    - generic [ref=f54e6]:
      - link [ref=f54e9] [cursor=pointer]:
        - /url: https://www.uat.premierinn.digital/gb/en/home.html
      - generic [ref=f54e13]:
        - generic [ref=f54e15]:
          - paragraph [ref=f54e18]: "1"
          - paragraph [ref=f54e19]: Choose your meals
        - generic [ref=f54e20]:
          - separator [ref=f54e21]
          - generic [ref=f54e22]:
            - paragraph [ref=f54e25]: "2"
            - paragraph [ref=f54e26]: Your details
        - generic [ref=f54e27]:
          - separator [ref=f54e28]
          - generic [ref=f54e29]:
            - paragraph [ref=f54e32]: "3"
            - paragraph [ref=f54e33]: Payment details
    - generic [ref=f54e34]:
      - generic [ref=f54e35]:
        - heading "Customise your stay" [level=1] [ref=f54e36]
        - generic [ref=f54e37]:
          - generic [ref=f54e38]:
            - generic [ref=f54e39]:
              - paragraph [ref=f54e40]: Add meals for entire stay
              - button "See menus" [ref=f54e43] [cursor=pointer]
            - generic [ref=f54e44]:
              - heading [level=6] [ref=f54e45]:
                - paragraph [ref=f54e46]: Adult meals
              - paragraph [ref=f54e47]: (2 adults, 1 night)
            - generic [ref=f54e49]:
              - img "Meal menus" [ref=f54e51]
              - generic [ref=f54e52]:
                - generic [ref=f54e53]:
                  - heading "Breakfast Roll Bundle" [level=4] [ref=f54e54]
                  - paragraph [ref=f54e55]: £6.00 per adult/day (£6.00 for 1 night)
                  - paragraph [ref=f54e57]: Your choice of any hot breakfast roll and a drink.
                - link [ref=f54e59] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-grabgo-breakfast.pdf
                  - paragraph [ref=f54e60]: Allergy & Nutrition info
              - generic [ref=f54e64]:
                - generic [ref=f54e65]:
                  - button "Decrease quantity" [disabled] [ref=f54e66]
                  - paragraph [ref=f54e70]: "0"
                  - button "Increase quantity" [ref=f54e71] [cursor=pointer]
                - paragraph [ref=f54e75]: adults
            - generic [ref=f54e77]:
              - img "Meal menus" [ref=f54e79]
              - generic [ref=f54e80]:
                - generic [ref=f54e81]:
                  - heading "Queensway Breakfast" [level=4] [ref=f54e82]
                  - paragraph [ref=f54e83]: £12.00 per adult/day (£12.00 for 1 night)
                  - paragraph [ref=f54e85]: Add a Queensway Inn breakfast to your stay. Choose one item from the breakfast menu, plus unlimited hot drinks and juice. Kids’ options are also available and can be ordered directly from the restaurant.
                - link [ref=f54e87] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-breakfast.pdf
                  - paragraph [ref=f54e88]: Allergy & nutrition info
              - generic [ref=f54e92]:
                - generic [ref=f54e93]:
                  - button "Decrease quantity" [disabled] [ref=f54e94]
                  - paragraph [ref=f54e98]: "0"
                  - button "Increase quantity" [ref=f54e99] [cursor=pointer]
                - paragraph [ref=f54e103]: adults
            - generic [ref=f54e105]:
              - img "Meal menus" [ref=f54e107]
              - generic [ref=f54e108]:
                - generic [ref=f54e109]:
                  - heading "Lighter Bundle" [level=4] [ref=f54e110]
                  - paragraph [ref=f54e111]: £4.50 per adult/day (£4.50 for 1 night)
                  - paragraph [ref=f54e113]: Your choice of porridge, a yoghurt, or fruit pot and a drink.
                - link [ref=f54e115] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-grabgo-breakfast.pdf
                  - paragraph [ref=f54e116]: Allergy & Nutrition info
              - generic [ref=f54e120]:
                - generic [ref=f54e121]:
                  - button "Decrease quantity" [disabled] [ref=f54e122]
                  - paragraph [ref=f54e126]: "0"
                  - button "Increase quantity" [ref=f54e127] [cursor=pointer]
                - paragraph [ref=f54e131]: adults
            - generic [ref=f54e133]:
              - img "Meal menus" [ref=f54e135]
              - generic [ref=f54e136]:
                - generic [ref=f54e137]:
                  - heading "Unlimited Continental Breakfast" [level=4] [ref=f54e138]
                  - paragraph [ref=f54e139]: £9.00 per adult/day (£9.00 for 1 night)
                  - paragraph [ref=f54e141]: A lighter start with tasty pastries, American pancakes, fruit and cereals. Includes smoothies and juices.
                - link [ref=f54e143] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-breakfast.pdf
                  - paragraph [ref=f54e144]: Allergy & nutrition info
              - generic [ref=f54e148]:
                - generic [ref=f54e149]:
                  - button "Decrease quantity" [disabled] [ref=f54e150]
                  - paragraph [ref=f54e154]: "0"
                  - button "Increase quantity" [ref=f54e155] [cursor=pointer]
                - paragraph [ref=f54e159]: adults
            - generic [ref=f54e161]:
              - img "Meal menus" [ref=f54e163]
              - generic [ref=f54e164]:
                - generic [ref=f54e165]:
                  - heading "Continental Bundle" [level=4] [ref=f54e166]
                  - paragraph [ref=f54e167]: £4.00 per adult/day (£4.00 for 1 night)
                  - paragraph [ref=f54e169]: Your choice of bakery item, and a drink.
                - link [ref=f54e171] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-grabgo-breakfast.pdf
                  - paragraph [ref=f54e172]: Allergy & Nutrition info
              - generic [ref=f54e176]:
                - generic [ref=f54e177]:
                  - button "Decrease quantity" [disabled] [ref=f54e178]
                  - paragraph [ref=f54e182]: "0"
                  - button "Increase quantity" [ref=f54e183] [cursor=pointer]
                - paragraph [ref=f54e187]: adults
            - generic [ref=f54e189]:
              - img "Meal menus" [ref=f54e191]
              - generic [ref=f54e192]:
                - generic [ref=f54e193]:
                  - heading "Meal Deal - Dinner, Drink and Unlimited Breakfast" [level=4] [ref=f54e194]
                  - paragraph [ref=f54e195]: £26.99 per adult/day (£26.99 for 1 night)
                  - paragraph [ref=f54e197]: Save up to 20% off your bill with our Meal Deal offer! Enjoy a delicious two-course dinner, a selected drink* and wake up to our famous, unlimited Premier Inn Breakfast the next day.
                - link [ref=f54e199] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-thyme-2.pdf
                  - paragraph [ref=f54e200]: Allergy & nutrition info
              - generic [ref=f54e204]:
                - generic [ref=f54e205]:
                  - button "Decrease quantity" [disabled] [ref=f54e206]
                  - paragraph [ref=f54e210]: "0"
                  - button "Increase quantity" [ref=f54e211] [cursor=pointer]
                - paragraph [ref=f54e215]: adults
            - generic [ref=f54e217]:
              - img "Meal menus" [ref=f54e219]
              - generic [ref=f54e220]:
                - generic [ref=f54e221]:
                  - heading "Dinner & Drink deal" [level=4] [ref=f54e222]
                  - paragraph [ref=f54e223]: £19.99 per adult/day (£19.99 for 1 night)
                  - paragraph [ref=f54e225]: Undecided about breakfast? No problem! Enjoy up to 20% off your bill with our tasty two-course dinner and a drink offer.
                - link [ref=f54e227] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-thyme-2.pdf
                  - paragraph [ref=f54e228]: Allergy & Nutrition info
              - generic [ref=f54e232]:
                - generic [ref=f54e233]:
                  - button "Decrease quantity" [disabled] [ref=f54e234]
                  - paragraph [ref=f54e238]: "0"
                  - button "Increase quantity" [ref=f54e239] [cursor=pointer]
                - paragraph [ref=f54e243]: adults
          - generic [ref=f54e244]:
            - heading [level=5] [ref=f54e245]:
              - paragraph [ref=f54e246]: Menus
            - generic [ref=f54e247]:
              - link "Breakfast menu" [ref=f54e251] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/grab-go/premier-inn-grabgo-breakfast.pdf
              - link "Breakfast menu" [ref=f54e252] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/IGF/the-social-breakfast-menu.pdf
              - link "Breakfast menu" [ref=f54e253] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/Premierinn/premier-inn-breakfast.pdf
              - link "Dinner menu" [ref=f54e254] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/THY/main-menu-2.pdf
          - generic [ref=f54e256]:
            - paragraph [ref=f54e257]: Add extras
            - generic [ref=f54e258]:
              - generic [ref=f54e259]:
                - img "Extras image" [ref=f54e261]
                - generic [ref=f54e262]:
                  - paragraph [ref=f54e263]: Ultimate Wi-Fi
                  - paragraph [ref=f54e264]: £5 per room/day
                  - paragraph [ref=f54e266]: Our most reliable Wi-Fi yet, enjoy an even speedier connection with Ultimate Wi-Fi, ideal for everything from streaming all your TV favourites and films to gaming.
              - generic [ref=f54e267]:
                - paragraph [ref=f54e268]: £5.00
                - button "Add" [ref=f54e269] [cursor=pointer]
            - generic [ref=f54e271]:
              - generic [ref=f54e272]:
                - img "Extras image" [ref=f54e274]
                - generic [ref=f54e275]:
                  - paragraph [ref=f54e276]: Early check-in
                  - generic [ref=f54e277]: Need to check in earlier? Start your stay from 11am instead of the usual 3pm
              - generic [ref=f54e278]:
                - paragraph [ref=f54e279]: £10.00
                - button "Add" [ref=f54e280] [cursor=pointer]
            - generic [ref=f54e282]:
              - generic [ref=f54e283]:
                - img "Extras image" [ref=f54e285]
                - generic [ref=f54e286]:
                  - paragraph [ref=f54e287]: Late checkout
                  - generic [ref=f54e288]: Check out at 2pm instead of 12pm
              - generic [ref=f54e289]:
                - paragraph [ref=f54e290]: £10.00
                - button "Add" [ref=f54e291] [cursor=pointer]
            - generic [ref=f54e293]:
              - generic [ref=f54e294]:
                - img "Extras image" [ref=f54e296]
                - generic [ref=f54e297]:
                  - paragraph [ref=f54e298]: Bottle of prosecco
                  - generic [ref=f54e299]: Enjoy an ice-cold bottle of Prosecco during your stay. Add to your booking now to save on bar price. Collect it or enjoy in our bar.
              - generic [ref=f54e300]:
                - paragraph [ref=f54e301]: £20.00
                - button "Add" [ref=f54e302] [cursor=pointer]
        - button [ref=f54e305] [cursor=pointer]:
          - paragraph [ref=f54e306]: Continue
        - button [ref=f54e307] [cursor=pointer]:
          - paragraph [ref=f54e311]: Back
        - generic [ref=f54e317]:
          - heading "We keep your personal data safe and secure." [level=3] [ref=f54e319]
          - generic [ref=f54e320]:
            - paragraph [ref=f54e322]: "We need to collect and keep some mandatory information in order to process your booking. Full details about how we use your data are set out in our Privacy notice. Premier Inn Hotels Limited (company no. 5137608) is a member of the Whitbread Group, the parent of which is Whitbread Group PLC (company no. 29423). Registered office: Whitbread Court, Houghton Hall Business Park, Porz Avenue, Dunstable LU5 5XE."
            - link "View our Privacy Notice" [ref=f54e323] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/gb/en/terms/privacy-policy.html
          - button [ref=f54e324] [cursor=pointer]:
            - heading "Find out more" [level=3] [ref=f54e325]
      - generic [ref=f54e329]:
        - generic [ref=f54e330]:
          - heading "London Heathrow Airport (M4/J4)" [level=5] [ref=f54e331]
          - generic [ref=f54e333]:
            - heading "Shepiston Lane" [level=6] [ref=f54e334]
            - heading "Middlesex" [level=6] [ref=f54e335]
            - heading "UB3 1RW" [level=6] [ref=f54e336]
        - separator [ref=f54e337]
        - generic [ref=f54e340]:
          - heading "Total cost" [level=2] [ref=f54e342]
          - heading [level=2] [ref=f54e343]:
            - generic "£80" [ref=f54e344]:
              - generic [ref=f54e345]: £
              - generic [ref=f54e346]: "80"
          - paragraph [ref=f54e347]: Includes VAT
        - generic [ref=f54e348]:
          - separator [ref=f54e349]
          - generic [ref=f54e351]:
            - paragraph [ref=f54e352]: "Rate: Flex"
            - paragraph [ref=f54e353]: Pay now or on arrival. Fully refundable up to 1 pm on day of arrival
        - separator [ref=f54e354]
        - generic [ref=f54e355]:
          - generic [ref=f54e356]:
            - paragraph [ref=f54e357]: Checking in
            - paragraph [ref=f54e358]: Mon 23 Nov 2026
          - generic [ref=f54e359]:
            - paragraph [ref=f54e360]: Checking out
            - paragraph [ref=f54e361]: Tue 24 Nov 2026
          - generic [ref=f54e362]:
            - paragraph [ref=f54e363]: Stay
            - paragraph [ref=f54e364]: 1 night
        - generic [ref=f54e366]:
          - separator [ref=f54e368]
          - generic [ref=f54e369]:
            - paragraph [ref=f54e370]:
              - text: Room 1
              - generic [ref=f54e371]: (Double room)
            - paragraph [ref=f54e372]: 2 Adults
            - paragraph [ref=f54e373]: No meals selected
            - paragraph [ref=f54e374]: No extras selected
  - alert [ref=f54e376]: Choose your meals
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