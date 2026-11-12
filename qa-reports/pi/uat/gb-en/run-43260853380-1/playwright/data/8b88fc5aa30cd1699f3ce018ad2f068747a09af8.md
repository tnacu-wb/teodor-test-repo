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
- generic [active] [ref=f58e1]:
  - generic [ref=f58e3]:
    - generic [ref=f58e6]:
      - link [ref=f58e9] [cursor=pointer]:
        - /url: https://www.uat.premierinn.digital/gb/en/home.html
      - generic [ref=f58e13]:
        - generic [ref=f58e15]:
          - paragraph [ref=f58e18]: "1"
          - paragraph [ref=f58e19]: Choose your meals
        - generic [ref=f58e20]:
          - separator [ref=f58e21]
          - generic [ref=f58e22]:
            - paragraph [ref=f58e25]: "2"
            - paragraph [ref=f58e26]: Your details
        - generic [ref=f58e27]:
          - separator [ref=f58e28]
          - generic [ref=f58e29]:
            - paragraph [ref=f58e32]: "3"
            - paragraph [ref=f58e33]: Payment details
    - generic [ref=f58e34]:
      - generic [ref=f58e35]:
        - heading "Customise your stay" [level=1] [ref=f58e36]
        - generic [ref=f58e37]:
          - generic [ref=f58e38]:
            - generic [ref=f58e39]:
              - paragraph [ref=f58e40]: Add meals for entire stay
              - button "See menus" [ref=f58e43] [cursor=pointer]
            - generic [ref=f58e44]:
              - heading [level=6] [ref=f58e45]:
                - paragraph [ref=f58e46]: Adult meals
              - paragraph [ref=f58e47]: (2 adults, 1 night)
            - generic [ref=f58e49]:
              - img "Meal menus" [ref=f58e51]
              - generic [ref=f58e52]:
                - generic [ref=f58e53]:
                  - heading "Breakfast Roll Bundle" [level=4] [ref=f58e54]
                  - paragraph [ref=f58e55]: £6.00 per adult/day (£6.00 for 1 night)
                  - paragraph [ref=f58e57]: Your choice of any hot breakfast roll and a drink.
                - link [ref=f58e59] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-grabgo-breakfast.pdf
                  - paragraph [ref=f58e60]: Allergy & Nutrition info
              - generic [ref=f58e64]:
                - generic [ref=f58e65]:
                  - button "Decrease quantity" [disabled] [ref=f58e66]
                  - paragraph [ref=f58e70]: "0"
                  - button "Increase quantity" [ref=f58e71] [cursor=pointer]
                - paragraph [ref=f58e75]: adults
            - generic [ref=f58e77]:
              - img "Meal menus" [ref=f58e79]
              - generic [ref=f58e80]:
                - generic [ref=f58e81]:
                  - heading "Queensway Breakfast" [level=4] [ref=f58e82]
                  - paragraph [ref=f58e83]: £12.00 per adult/day (£12.00 for 1 night)
                  - paragraph [ref=f58e85]: Add a Queensway Inn breakfast to your stay. Choose one item from the breakfast menu, plus unlimited hot drinks and juice. Kids’ options are also available and can be ordered directly from the restaurant.
                - link [ref=f58e87] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-breakfast.pdf
                  - paragraph [ref=f58e88]: Allergy & nutrition info
              - generic [ref=f58e92]:
                - generic [ref=f58e93]:
                  - button "Decrease quantity" [disabled] [ref=f58e94]
                  - paragraph [ref=f58e98]: "0"
                  - button "Increase quantity" [ref=f58e99] [cursor=pointer]
                - paragraph [ref=f58e103]: adults
            - generic [ref=f58e105]:
              - img "Meal menus" [ref=f58e107]
              - generic [ref=f58e108]:
                - generic [ref=f58e109]:
                  - heading "Lighter Bundle" [level=4] [ref=f58e110]
                  - paragraph [ref=f58e111]: £4.50 per adult/day (£4.50 for 1 night)
                  - paragraph [ref=f58e113]: Your choice of porridge, a yoghurt, or fruit pot and a drink.
                - link [ref=f58e115] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-grabgo-breakfast.pdf
                  - paragraph [ref=f58e116]: Allergy & Nutrition info
              - generic [ref=f58e120]:
                - generic [ref=f58e121]:
                  - button "Decrease quantity" [disabled] [ref=f58e122]
                  - paragraph [ref=f58e126]: "0"
                  - button "Increase quantity" [ref=f58e127] [cursor=pointer]
                - paragraph [ref=f58e131]: adults
            - generic [ref=f58e133]:
              - img "Meal menus" [ref=f58e135]
              - generic [ref=f58e136]:
                - generic [ref=f58e137]:
                  - heading "Unlimited Continental Breakfast" [level=4] [ref=f58e138]
                  - paragraph [ref=f58e139]: £9.00 per adult/day (£9.00 for 1 night)
                  - paragraph [ref=f58e141]: A lighter start with tasty pastries, American pancakes, fruit and cereals. Includes smoothies and juices.
                - link [ref=f58e143] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-breakfast.pdf
                  - paragraph [ref=f58e144]: Allergy & nutrition info
              - generic [ref=f58e148]:
                - generic [ref=f58e149]:
                  - button "Decrease quantity" [disabled] [ref=f58e150]
                  - paragraph [ref=f58e154]: "0"
                  - button "Increase quantity" [ref=f58e155] [cursor=pointer]
                - paragraph [ref=f58e159]: adults
            - generic [ref=f58e161]:
              - img "Meal menus" [ref=f58e163]
              - generic [ref=f58e164]:
                - generic [ref=f58e165]:
                  - heading "Continental Bundle" [level=4] [ref=f58e166]
                  - paragraph [ref=f58e167]: £4.00 per adult/day (£4.00 for 1 night)
                  - paragraph [ref=f58e169]: Your choice of bakery item, and a drink.
                - link [ref=f58e171] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-grabgo-breakfast.pdf
                  - paragraph [ref=f58e172]: Allergy & Nutrition info
              - generic [ref=f58e176]:
                - generic [ref=f58e177]:
                  - button "Decrease quantity" [disabled] [ref=f58e178]
                  - paragraph [ref=f58e182]: "0"
                  - button "Increase quantity" [ref=f58e183] [cursor=pointer]
                - paragraph [ref=f58e187]: adults
            - generic [ref=f58e189]:
              - img "Meal menus" [ref=f58e191]
              - generic [ref=f58e192]:
                - generic [ref=f58e193]:
                  - heading "Meal Deal - Dinner, Drink and Unlimited Breakfast" [level=4] [ref=f58e194]
                  - paragraph [ref=f58e195]: £26.99 per adult/day (£26.99 for 1 night)
                  - paragraph [ref=f58e197]: Save up to 20% off your bill with our Meal Deal offer! Enjoy a delicious two-course dinner, a selected drink* and wake up to our famous, unlimited Premier Inn Breakfast the next day.
                - link [ref=f58e199] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-thyme-2.pdf
                  - paragraph [ref=f58e200]: Allergy & nutrition info
              - generic [ref=f58e204]:
                - generic [ref=f58e205]:
                  - button "Decrease quantity" [disabled] [ref=f58e206]
                  - paragraph [ref=f58e210]: "0"
                  - button "Increase quantity" [ref=f58e211] [cursor=pointer]
                - paragraph [ref=f58e215]: adults
            - generic [ref=f58e217]:
              - img "Meal menus" [ref=f58e219]
              - generic [ref=f58e220]:
                - generic [ref=f58e221]:
                  - heading "Dinner & Drink deal" [level=4] [ref=f58e222]
                  - paragraph [ref=f58e223]: £19.99 per adult/day (£19.99 for 1 night)
                  - paragraph [ref=f58e225]: Undecided about breakfast? No problem! Enjoy up to 20% off your bill with our tasty two-course dinner and a drink offer.
                - link [ref=f58e227] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-thyme-2.pdf
                  - paragraph [ref=f58e228]: Allergy & Nutrition info
              - generic [ref=f58e232]:
                - generic [ref=f58e233]:
                  - button "Decrease quantity" [disabled] [ref=f58e234]
                  - paragraph [ref=f58e238]: "0"
                  - button "Increase quantity" [ref=f58e239] [cursor=pointer]
                - paragraph [ref=f58e243]: adults
          - generic [ref=f58e244]:
            - heading [level=5] [ref=f58e245]:
              - paragraph [ref=f58e246]: Menus
            - generic [ref=f58e247]:
              - link "Breakfast menu" [ref=f58e251] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/grab-go/premier-inn-grabgo-breakfast.pdf
              - link "Breakfast menu" [ref=f58e252] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/IGF/the-social-breakfast-menu.pdf
              - link "Breakfast menu" [ref=f58e253] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/Premierinn/premier-inn-breakfast.pdf
              - link "Dinner menu" [ref=f58e254] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/THY/main-menu-2.pdf
          - generic [ref=f58e256]:
            - paragraph [ref=f58e257]: Add extras
            - generic [ref=f58e258]:
              - generic [ref=f58e259]:
                - img "Extras image" [ref=f58e261]
                - generic [ref=f58e262]:
                  - paragraph [ref=f58e263]: Ultimate Wi-Fi
                  - paragraph [ref=f58e264]: £5 per room/day
                  - paragraph [ref=f58e266]: Our most reliable Wi-Fi yet, enjoy an even speedier connection with Ultimate Wi-Fi, ideal for everything from streaming all your TV favourites and films to gaming.
              - generic [ref=f58e267]:
                - paragraph [ref=f58e268]: £5.00
                - button "Add" [ref=f58e269] [cursor=pointer]
            - generic [ref=f58e271]:
              - generic [ref=f58e272]:
                - img "Extras image" [ref=f58e274]
                - generic [ref=f58e275]:
                  - paragraph [ref=f58e276]: Early check-in
                  - generic [ref=f58e277]: Need to check in earlier? Start your stay from 11am instead of the usual 3pm
              - generic [ref=f58e278]:
                - paragraph [ref=f58e279]: £10.00
                - button "Add" [ref=f58e280] [cursor=pointer]
            - generic [ref=f58e282]:
              - generic [ref=f58e283]:
                - img "Extras image" [ref=f58e285]
                - generic [ref=f58e286]:
                  - paragraph [ref=f58e287]: Late checkout
                  - generic [ref=f58e288]: Check out at 2pm instead of 12pm
              - generic [ref=f58e289]:
                - paragraph [ref=f58e290]: £10.00
                - button "Add" [ref=f58e291] [cursor=pointer]
            - generic [ref=f58e293]:
              - generic [ref=f58e294]:
                - img "Extras image" [ref=f58e296]
                - generic [ref=f58e297]:
                  - paragraph [ref=f58e298]: Bottle of prosecco
                  - generic [ref=f58e299]: Enjoy an ice-cold bottle of Prosecco during your stay. Add to your booking now to save on bar price. Collect it or enjoy in our bar.
              - generic [ref=f58e300]:
                - paragraph [ref=f58e301]: £20.00
                - button "Add" [ref=f58e302] [cursor=pointer]
        - button [ref=f58e305] [cursor=pointer]:
          - paragraph [ref=f58e306]: Continue
        - button [ref=f58e307] [cursor=pointer]:
          - paragraph [ref=f58e311]: Back
        - generic [ref=f58e317]:
          - heading "We keep your personal data safe and secure." [level=3] [ref=f58e319]
          - generic [ref=f58e320]:
            - paragraph [ref=f58e322]: "We need to collect and keep some mandatory information in order to process your booking. Full details about how we use your data are set out in our Privacy notice. Premier Inn Hotels Limited (company no. 5137608) is a member of the Whitbread Group, the parent of which is Whitbread Group PLC (company no. 29423). Registered office: Whitbread Court, Houghton Hall Business Park, Porz Avenue, Dunstable LU5 5XE."
            - link "View our Privacy Notice" [ref=f58e323] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/gb/en/terms/privacy-policy.html
          - button [ref=f58e324] [cursor=pointer]:
            - heading "Find out more" [level=3] [ref=f58e325]
      - generic [ref=f58e329]:
        - generic [ref=f58e330]:
          - heading "London Heathrow Airport (M4/J4)" [level=5] [ref=f58e331]
          - generic [ref=f58e333]:
            - heading "Shepiston Lane" [level=6] [ref=f58e334]
            - heading "Middlesex" [level=6] [ref=f58e335]
            - heading "UB3 1RW" [level=6] [ref=f58e336]
        - separator [ref=f58e337]
        - generic [ref=f58e340]:
          - heading "Total cost" [level=2] [ref=f58e342]
          - heading [level=2] [ref=f58e343]:
            - generic "£80" [ref=f58e344]:
              - generic [ref=f58e345]: £
              - generic [ref=f58e346]: "80"
          - paragraph [ref=f58e347]: Includes VAT
        - generic [ref=f58e348]:
          - separator [ref=f58e349]
          - generic [ref=f58e351]:
            - paragraph [ref=f58e352]: "Rate: Flex"
            - paragraph [ref=f58e353]: Pay now or on arrival. Fully refundable up to 1 pm on day of arrival
        - separator [ref=f58e354]
        - generic [ref=f58e355]:
          - generic [ref=f58e356]:
            - paragraph [ref=f58e357]: Checking in
            - paragraph [ref=f58e358]: Tue 17 Nov 2026
          - generic [ref=f58e359]:
            - paragraph [ref=f58e360]: Checking out
            - paragraph [ref=f58e361]: Wed 18 Nov 2026
          - generic [ref=f58e362]:
            - paragraph [ref=f58e363]: Stay
            - paragraph [ref=f58e364]: 1 night
        - generic [ref=f58e366]:
          - separator [ref=f58e368]
          - generic [ref=f58e369]:
            - paragraph [ref=f58e370]:
              - text: Room 1
              - generic [ref=f58e371]: (Double room)
            - paragraph [ref=f58e372]: 2 Adults
            - paragraph [ref=f58e373]: No meals selected
            - paragraph [ref=f58e374]: No extras selected
  - alert [ref=f58e376]: Choose your meals
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