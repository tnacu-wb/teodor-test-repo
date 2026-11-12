# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pib/baseline-e2e-manager-uk-hotel-business-flex-poa-amend-change-guest-details.spec.ts >> PIB baseline: manager UK hotel Business Flex POA amendment >> Test Book as travel manager: CNP PoA Booking. Check routing instructions and notes. Amend: change guest details. TestCase ID: 343521 @regression @TC-343521
- Location: tests/regressions/pib/baseline-e2e-manager-uk-hotel-business-flex-poa-amend-change-guest-details.spec.ts:22:7

# Error details

```
GraphQLError: GraphQL error (retryable) for query "basket": Expected Iterable, but did not find one for field "BasketItem.details".
```

# Page snapshot

```yaml
- generic [active] [ref=f26e1]:
  - generic [ref=f26e2]:
    - banner [ref=f26e3]:
      - generic [ref=f26e4]:
        - link [ref=f26e5] [cursor=pointer]:
          - /url: /en-gb/homepage
          - img "Inn Business" [ref=f26e6]
        - generic [ref=f26e8]:
          - generic [ref=f26e9]:
            - text: "1"
            - generic [ref=f26e10]: Guest details
          - generic [ref=f26e12]:
            - text: "2"
            - generic [ref=f26e13]: Payment
    - main [ref=f26e14]:
      - generic [ref=f26e15]:
        - generic [ref=f26e16]:
          - heading "Customise your stay" [level=1] [ref=f26e17]
          - status [ref=f26e19]:
            - generic [ref=f26e23]: We need to collect and keep some mandatory information in order to process your booking.
          - group [ref=f26e27]:
            - generic [ref=f26e33]:
              - heading "Guest details" [level=3] [ref=f26e34]
              - generic [ref=f26e36]:
                - group [ref=f26e38]:
                  - textbox "Search by name or email" [ref=f26e40]
                - paragraph [ref=f26e41] [cursor=pointer]: Enter details manually
          - generic [ref=f26e42]:
            - generic [ref=f26e43]:
              - generic [ref=f26e44]:
                - paragraph [ref=f26e45]: Add meals for entire stay
                - button "See menus" [ref=f26e49] [cursor=pointer]
              - status [ref=f26e51]:
                - generic [ref=f26e55]: Your company has approved the following meal selections
              - generic [ref=f26e58]:
                - heading [level=6] [ref=f26e59]:
                  - paragraph [ref=f26e60]: Adult meals
                - paragraph [ref=f26e61]: (1 adult, 1 night)
              - generic [ref=f26e63]:
                - img "Meal menus" [ref=f26e65]
                - generic [ref=f26e66]:
                  - generic [ref=f26e67]:
                    - heading "Unlimited Premier Inn Breakfast" [level=4] [ref=f26e68]
                    - paragraph [ref=f26e69]: £10.00 per adult/day (£10.00 for 1 night)
                    - paragraph [ref=f26e71]: Add our unlimited breakfast and look forward to freshly cooked bacon, fluffy hash browns, succulent sausages, eggs the way you like them, cereals, fresh fruit, croissants and much more.
                  - link [ref=f26e73] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-breakfast.pdf
                    - paragraph [ref=f26e74]: Allergy & nutrition info
                - generic [ref=f26e78]:
                  - generic [ref=f26e79]:
                    - button "Decrease quantity" [disabled] [ref=f26e80]
                    - paragraph [ref=f26e84]: "0"
                    - button "Increase quantity" [ref=f26e85] [cursor=pointer]
                  - paragraph [ref=f26e89]: adults
              - generic [ref=f26e91]:
                - img "Meal menus" [ref=f26e93]
                - generic [ref=f26e94]:
                  - generic [ref=f26e95]:
                    - heading "Unlimited Continental Breakfast" [level=4] [ref=f26e96]
                    - paragraph [ref=f26e97]: £99.00 per adult/day (£99.00 for 1 night)
                    - paragraph [ref=f26e99]: A lighter start with tasty pastries, American pancakes, fruit and cereals. Includes smoothies and juices.
                  - link [ref=f26e101] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-breakfast.pdf
                    - paragraph [ref=f26e102]: Allergy & nutrition info
                - generic [ref=f26e106]:
                  - generic [ref=f26e107]:
                    - button "Decrease quantity" [disabled] [ref=f26e108]
                    - paragraph [ref=f26e112]: "0"
                    - button "Increase quantity" [ref=f26e113] [cursor=pointer]
                  - paragraph [ref=f26e117]: adults
              - generic [ref=f26e119]:
                - img "Meal menus" [ref=f26e121]
                - generic [ref=f26e122]:
                  - generic [ref=f26e123]:
                    - heading "Meal Deal - Dinner, Drink and Unlimited Breakfast" [level=4] [ref=f26e124]
                    - paragraph [ref=f26e125]: £298.00 per adult/day (£298.00 for 1 night)
                    - paragraph [ref=f26e127]: Save up to 20% off your bill with our Meal Deal offer! Enjoy a delicious two-course dinner, a selected drink* and wake up to our famous, unlimited Premier Inn Breakfast the next day.
                  - link [ref=f26e129] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-thyme-2.pdf
                    - paragraph [ref=f26e130]: Allergy & nutrition info
                - generic [ref=f26e134]:
                  - generic [ref=f26e135]:
                    - button "Decrease quantity" [disabled] [ref=f26e136]
                    - paragraph [ref=f26e140]: "0"
                    - button "Increase quantity" [ref=f26e141] [cursor=pointer]
                  - paragraph [ref=f26e145]: adults
              - generic [ref=f26e147]:
                - img "Meal menus" [ref=f26e149]
                - generic [ref=f26e150]:
                  - generic [ref=f26e151]:
                    - heading "Dinner & Drink deal" [level=4] [ref=f26e152]
                    - paragraph [ref=f26e153]: £20.00 per adult/day (£20.00 for 1 night)
                    - paragraph [ref=f26e155]: Undecided about breakfast? No problem! Enjoy up to 20% off your bill with our tasty two-course dinner and a drink offer.
                  - link [ref=f26e157] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-thyme-2.pdf
                    - paragraph [ref=f26e158]: Allergy & Nutrition info
                - generic [ref=f26e162]:
                  - generic [ref=f26e163]:
                    - button "Decrease quantity" [disabled] [ref=f26e164]
                    - paragraph [ref=f26e168]: "0"
                    - button "Increase quantity" [ref=f26e169] [cursor=pointer]
                  - paragraph [ref=f26e173]: adults
            - generic [ref=f26e174]:
              - heading [level=5] [ref=f26e175]:
                - paragraph [ref=f26e176]: Menus
              - generic [ref=f26e177]:
                - link "Breakfast menu" [ref=f26e181] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/Premierinn/premier-inn-breakfast.pdf
                - link "Dinner menu" [ref=f26e182] [cursor=pointer]:
                  - /url: https://www.uat.premierinn.digital/content/dam/global/restaurants/THY/main-menu-2.pdf
            - generic [ref=f26e184]:
              - paragraph [ref=f26e185]: Add extras
              - generic [ref=f26e186]:
                - generic [ref=f26e187]:
                  - img "Extras image" [ref=f26e189]
                  - generic [ref=f26e190]:
                    - paragraph [ref=f26e191]: Ultimate Wi-Fi
                    - paragraph [ref=f26e192]: £8 per room/day
                    - paragraph [ref=f26e194]: Enjoy a faster connection with Ultimate Wi-Fi, ideal for downloading and streaming on up to three devices.
                - generic [ref=f26e195]:
                  - paragraph [ref=f26e196]: £8.00
                  - button "Add" [ref=f26e197] [cursor=pointer]
              - generic [ref=f26e199]:
                - generic [ref=f26e200]:
                  - img "Extras image" [ref=f26e202]
                  - generic [ref=f26e203]:
                    - paragraph [ref=f26e204]: Early check-in
                    - generic [ref=f26e205]: Need to check in earlier? Start your stay from 11am instead of the usual 3pm
                - generic [ref=f26e206]:
                  - paragraph [ref=f26e207]: £10.00
                  - button "Add" [ref=f26e208] [cursor=pointer]
          - generic [ref=f26e211]:
            - paragraph [ref=f26e212]: Please keep in touch
            - generic [ref=f26e214] [cursor=pointer]:
              - checkbox "By ticking the box, you agree to receive marketing from Premier Inn Hotels Limited about our products, services and promotions, as well as offers from carefully selected partners that may enhance your stay by email and social media platforms. You can withdraw your consent at any time by clicking the unsubscribe link in any email or contacting us directly. For more information about how we handle your personal data, please visit our Privacy Policy." [ref=f26e215]
              - paragraph [ref=f26e218]:
                - text: By ticking the box, you agree to receive marketing from Premier Inn Hotels Limited about our products, services and promotions, as well as offers from carefully selected partners that may enhance your stay by email and social media platforms. You can withdraw your consent at any time by clicking the unsubscribe link in any email or contacting us directly. For more information about how we handle your personal data, please visit our
                - link "Privacy Policy" [ref=f26e219]:
                  - /url: https://business.premierinn.com/en-gb/info/terms/privacy-policy.html
                - text: .
          - button [ref=f26e221] [cursor=pointer]:
            - paragraph [ref=f26e222]: Continue
          - button [ref=f26e223] [cursor=pointer]:
            - paragraph [ref=f26e227]: Back
          - generic [ref=f26e233]:
            - heading "We keep your personal data safe and secure." [level=3] [ref=f26e235]
            - generic [ref=f26e236]:
              - paragraph [ref=f26e238]: "We need to collect and keep some mandatory information in order to process your booking. Full details about how we use your data are set out in our Privacy notice. Premier Inn Hotels Limited (company no. 5137608) is a member of the Whitbread Group, the parent of which is Whitbread Group PLC (company no. 29423). Registered office: Whitbread Court, Houghton Hall Business Park, Porz Avenue, Dunstable LU5 5XE."
              - link "View our Privacy Notice" [ref=f26e239] [cursor=pointer]:
                - /url: https://www.uat.premierinn.digital/gb/en/terms/privacy-policy.html
            - button [ref=f26e240] [cursor=pointer]:
              - heading "Find out more" [level=3] [ref=f26e241]
        - generic [ref=f26e244]:
          - generic [ref=f26e245]:
            - generic [ref=f26e246]:
              - heading "London Gatwick Airport South (London Road)" [level=5] [ref=f26e247]
              - generic [ref=f26e249]:
                - heading "London Road" [level=6] [ref=f26e250]
                - heading "Lowfield Heath" [level=6] [ref=f26e251]
                - heading "Crawley" [level=6] [ref=f26e252]
                - heading "RH10 9ST" [level=6] [ref=f26e253]
            - separator [ref=f26e254]
            - generic [ref=f26e255]:
              - generic [ref=f26e256]:
                - generic [ref=f26e257]:
                  - paragraph [ref=f26e258]: Room price
                  - paragraph [ref=f26e259]: £76.00
                - button "City tax" [ref=f26e261] [cursor=pointer]
                - generic [ref=f26e262]: £1.20
              - generic [ref=f26e264]:
                - heading "Total cost" [level=2] [ref=f26e266]
                - heading [level=2] [ref=f26e267]:
                  - generic "£77.20" [ref=f26e268]:
                    - generic [ref=f26e269]: £
                    - generic [ref=f26e270]: "77"
                    - generic [ref=f26e271]: "20"
                - paragraph [ref=f26e272]: Includes VAT
                - paragraph [ref=f26e273]: Includes taxes and fees
            - generic [ref=f26e274]:
              - separator [ref=f26e275]
              - generic [ref=f26e277]:
                - paragraph [ref=f26e278]: "Rate: Business Flex"
                - paragraph [ref=f26e279]: Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival. Flex rate saving already applied
            - separator [ref=f26e280]
            - generic [ref=f26e281]:
              - generic [ref=f26e282]:
                - paragraph [ref=f26e283]: Checking in
                - paragraph [ref=f26e284]: Wed 18 Nov 2026
              - generic [ref=f26e285]:
                - paragraph [ref=f26e286]: Checking out
                - paragraph [ref=f26e287]: Thu 19 Nov 2026
              - generic [ref=f26e288]:
                - paragraph [ref=f26e289]: Stay
                - paragraph [ref=f26e290]: 1 night
            - generic [ref=f26e292]:
              - separator [ref=f26e294]
              - generic [ref=f26e295]:
                - paragraph [ref=f26e296]:
                  - text: Room 1
                  - generic [ref=f26e297]: (Double room)
                - paragraph [ref=f26e298]: 1 Adult
                - paragraph [ref=f26e299]: No meals selected
                - paragraph [ref=f26e300]: No extras selected
          - button [ref=f26e301] [cursor=pointer]:
            - paragraph [ref=f26e302]: Continue
    - region "Notifications (F8)":
      - list
  - alert [ref=f26e304]
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