# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/tc-468889-guest-piba-poa.spec.ts >> TC-468889: Guest PIBA Pay on Arrival Booking >> TC-468889: Guest user books UK hotel with PIBA Pay on Arrival
- Location: tests/regressions/pi/tc-468889-guest-piba-poa.spec.ts:20:7

# Error details

```
Error: Ancillaries page did not load: page loaded indicator was not visible within 30000ms
```

# Page snapshot

```yaml
- generic [active] [ref=f13e1]:
  - generic [ref=f13e3]:
    - banner [ref=f13e4]:
      - generic [ref=f13e7]:
        - link [ref=f13e10] [cursor=pointer]:
          - /url: https://www.uat.premierinn.digital/gb/en/home.html
          - img "Premier Inn Rest Easy" [ref=f13e11]
        - generic [ref=f13e12]:
          - img "English" [ref=f13e15] [cursor=pointer]
          - generic [ref=f13e16]:
            - paragraph [ref=f13e19] [cursor=pointer]: Discover Premier Inn
            - paragraph [ref=f13e22] [cursor=pointer]: Business
            - paragraph [ref=f13e24] [cursor=pointer]: Manage booking
            - generic [ref=f13e26]:
              - button "Log in" [ref=f13e27] [cursor=pointer]
              - button "Sign up" [ref=f13e28] [cursor=pointer]
    - main [ref=f13e29]:
      - alert [ref=f13e32]:
        - generic [ref=f13e36]: Something went wrong, please try again later
    - generic [ref=f13e40]:
      - generic [ref=f13e41]:
        - tablist [ref=f13e42]:
          - tab [selected] [ref=f13e43] [cursor=pointer]:
            - heading "About us" [level=3] [ref=f13e45]
          - tab [ref=f13e46] [cursor=pointer]:
            - heading "City breaks" [level=3] [ref=f13e48]
          - tab [ref=f13e49] [cursor=pointer]:
            - heading "Summer breaks" [level=3] [ref=f13e51]
          - tab [ref=f13e52] [cursor=pointer]:
            - heading "Winter breaks" [level=3] [ref=f13e54]
          - tab [ref=f13e55] [cursor=pointer]:
            - heading "Business" [level=3] [ref=f13e57]
        - tabpanel "About us" [ref=f13e59]:
          - generic [ref=f13e60]:
            - paragraph [ref=f13e62]: Is it our comfy beds, our seriously tasty food, our great value hotels or our amazing teams that guests love so much? We reckon it’s a bit of everything. Take a look around to find out why we’re a much-loved, award-winning hotel chain, and rest easy knowing our range of rates give you both choice and flexibility.
            - generic [ref=f13e63]:
              - generic [ref=f13e64]:
                - paragraph [ref=f13e65]: Get in touch
                - generic [ref=f13e66]:
                  - link "Contact us" [ref=f13e67] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/contact-us.html
                  - link "FAQs" [ref=f13e68] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/faq.html
                  - link "Group bookings" [ref=f13e69] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why/groups.html
                  - link "Affiliates" [ref=f13e70] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/premier-inn-affiliate-programme.html
                  - link "International development" [ref=f13e71] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/business/international-development.html
                  - link "Careers" [ref=f13e72] [cursor=pointer]:
                    - /url: https://www.whitbreadcareers.com/our-brands/premier-inn/
              - generic [ref=f13e73]:
                - paragraph [ref=f13e74]: Legal
                - generic [ref=f13e75]:
                  - link "Terms and conditions" [ref=f13e76] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/terms/booking-terms-and-conditions.html
                  - link "Terms of use" [ref=f13e77] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/terms/terms-of-use.html
                  - link "Privacy policy" [ref=f13e78] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/terms/privacy-policy.html
                  - link "Cookies notice" [ref=f13e79] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/terms/how-we-use-cookies.html
                  - link "Good Night Guarantee" [ref=f13e80] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/sleep/good-night-guarantee.html
                  - link "Modern Slavery Act statement" [ref=f13e81] [cursor=pointer]:
                    - /url: https://cdn.whitbread.co.uk/media/2023/05/23926_MSA-Report-2022-23_Stage2_230510_14.48-Final-High-Res.pdf
              - generic [ref=f13e82]:
                - paragraph [ref=f13e83]: Locations
                - generic [ref=f13e84]:
                  - link "Hotel directory" [ref=f13e85] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/hotels.html
                  - link "New hotels" [ref=f13e86] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why/locations/new-hotels.html
                  - link "Local guides" [ref=f13e87] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/short-breaks/city-breaks.html
                  - link "Short breaks" [ref=f13e88] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/short-breaks.html
                  - link "Hotels in Germany" [ref=f13e89] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/hotels/germany.html
                  - link "Dubai and beyond" [ref=f13e90] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/hotels/middle-east.html
              - generic [ref=f13e91]:
                - paragraph [ref=f13e92]: Our hotels
                - generic [ref=f13e93]:
                  - link "Our rooms" [ref=f13e94] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/sleep/our-rooms.html
                  - link "Family friendly" [ref=f13e95] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why/family.html
                  - link "Sleep" [ref=f13e96] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/sleep.html
                  - link "Food & drink" [ref=f13e97] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why/food.html
                  - link "hub by Premier Inn" [ref=f13e98] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/hub.html
                  - link "ZIP by Premier Inn" [ref=f13e99] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/zip.html
              - generic [ref=f13e100]:
                - paragraph [ref=f13e101]: Find out more
                - generic [ref=f13e102]:
                  - link "About us" [ref=f13e103] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why.html
                  - link "Rest easy" [ref=f13e104] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/resteasy.html
                  - link "GOSH Charity" [ref=f13e105] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why/gosh-childrens-charity.html
                  - link "Force for Good" [ref=f13e106] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why/force-for-good.html
                  - link "Disabled access" [ref=f13e107] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/terms/disabled-access.html
                  - link "News" [ref=f13e108] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/news.html
              - generic [ref=f13e109]:
                - paragraph [ref=f13e110]: Everything else
                - generic [ref=f13e111]:
                  - link "Our rates" [ref=f13e112] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why/rates.html
                  - link "Offers" [ref=f13e113] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/offers.html
                  - link "Buy our bed" [ref=f13e114] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/sleep/buy-our-bed.html
                  - link "Mobile apps" [ref=f13e115] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/why/apps.html
                  - link "We value difference" [ref=f13e116] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/terms/diversity-and-inclusion.html
                  - link "Sitemap" [ref=f13e117] [cursor=pointer]:
                    - /url: https://www.uat.premierinn.digital/gb/en/sitemap.html
      - generic [ref=f13e118]:
        - generic [ref=f13e119]: © 2026 Premier Inn
        - generic [ref=f13e120]:
          - link [ref=f13e121] [cursor=pointer]:
            - /url: https://www.facebook.com/premierinn
          - link [ref=f13e123] [cursor=pointer]:
            - /url: https://twitter.com/premierinn
          - link [ref=f13e125] [cursor=pointer]:
            - /url: https://www.instagram.com/premierinn
  - alert [ref=f13e127]
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
  314 |     console.log('Remove ECI and validate total cost');
  315 |     return this.changeExtraAndValidateTotalCost(options, 'eci', -1);
  316 |   }
  317 | 
  318 |   /** Add Late check-out and validate the updated booking total. */
  319 |   async addLcoAndValidateTotalCost(options: ExtraCostOptions): Promise<string> {
  320 |     console.log('Add LCO and validate total cost');
  321 |     return this.changeExtraAndValidateTotalCost(options, 'lco', 1);
  322 |   }
  323 | 
  324 |   /** Remove Late check-out and validate the updated booking total. */
  325 |   async removeLcoAndValidateTotalCost(options: ExtraCostOptions): Promise<string> {
  326 |     console.log('Remove LCO and validate total cost');
  327 |     return this.changeExtraAndValidateTotalCost(options, 'lco', -1);
  328 |   }
  329 | 
  330 |   /**
  331 |    * Find an adult meal by title and return its rendered index.
  332 |    * @param title - Meal name to find.
  333 |    * @param exactMatch - Whether the title must match exactly.
  334 |    * @returns The zero-based meal index, or `-1` when no match is rendered.
  335 |    */
  336 |   private async findMealByTitle(title: string, exactMatch: boolean): Promise<number> {
  337 |     const count = await this.adultMealTitles.count();
  338 |     for (let index = 0; index < count; index++) {
  339 |       const mealTitle = (await this.adultMealTitles.nth(index).innerText()).trim();
  340 |       if (exactMatch ? mealTitle === title : mealTitle.toLowerCase().includes(title.toLowerCase())) return index;
  341 |     }
  342 |     return -1;
  343 |   }
  344 | 
  345 |   /**
  346 |    * Fetch and order ancillary meals exactly as they are rendered, then resolve the requested meal.
  347 |    * @returns The API meal and its rendered zero-based index.
  348 |    */
  349 |   private async getMealAndIndex({ hotelID, ratePlanCode, title, numberOfNights, startDate = new Date(), endDate = new Date(startDate.getTime() + numberOfNights * 86400000), adultsNumber, childNumber = 0, exactMatch }: {
  350 |     hotelID: string; ratePlanCode: string; title: string; numberOfNights: number; startDate?: Date; endDate?: Date; adultsNumber: number; childNumber?: number; exactMatch: boolean;
  351 |   }) {
  352 |     const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: hotelID, ratePlanCode, isForUI: false });
  353 |     const meals = await ApiCalls.graphqlGetAncillariesAdultMeals({ hotelId: hotelID, bookingFlowId, nightsNumber: numberOfNights, startDate, endDate, adultsNumber, childrenNumber: childNumber });
  354 |     const sortedMeals = meals
  355 |       .filter((meal): meal is typeof meal & { name: string; price: number } => typeof meal.name === 'string' && typeof meal.price === 'number')
  356 |       .sort((first, second) => first.name.localeCompare(second.name) || (first.order ?? 0) - (second.order ?? 0));
  357 |     const index = sortedMeals.findIndex(meal => exactMatch ? meal.name.trim() === title : meal.name.trim().includes(title));
  358 |     if (index === -1) throw new Error(`"${title}" meal is not displayed on Ancillaries page`);
  359 |     return { meal: sortedMeals[index], index };
  360 |   }
  361 | 
  362 |   /**
  363 |    * Click an adult meal's add or remove control repeatedly.
  364 |    * @param index - Zero-based meal index.
  365 |    * @param count - Number of selections to change.
  366 |    * @param add - Whether to add instead of remove selections.
  367 |    */
  368 |   private async clickMealButton(index: number, count: number, add: boolean): Promise<void> {
  369 |     const button = add ? this.getAddButtonForMealAtIndex(index) : this.getRemoveButtonForMealAtIndex(index);
  370 |     for (let click = 0; click < count; click++) await button.click();
  371 |   }
  372 | 
  373 |   /**
  374 |    * Click the child-meal add or remove control repeatedly.
  375 |    * @param count - Number of child selections to change.
  376 |    * @param add - Whether to add instead of remove selections.
  377 |    */
  378 |   private async clickChildMealButton(count: number, add: boolean): Promise<void> {
  379 |     for (let click = 0; click < count; click++) {
  380 |       if (add) await this.childMealContainer.clickAddMealButton();
  381 |       else await this.childMealContainer.clickRemoveMealButton();
  382 |     }
  383 |   }
  384 | 
  385 |   /**
  386 |    * Change one ECI or LCO item and validate its API price is reflected in the booking total.
  387 |    * @param options - Ancillary availability and booking criteria.
  388 |    * @param extraType - Early check-in or late check-out.
  389 |    * @param direction - `1` to add the extra or `-1` to remove it.
  390 |    * @returns The expected formatted booking total.
  391 |    */
  392 |   private async changeExtraAndValidateTotalCost(options: ExtraCostOptions, extraType: 'eci' | 'lco', direction: 1 | -1): Promise<string> {
  393 |     const startDate = options.startDate ?? new Date();
  394 |     const endDate = options.endDate ?? new Date(startDate.getTime() + options.numberOfNights * 86400000);
  395 |     const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: options.hotelID, ratePlanCode: options.ratePlanCode ?? HotelRates.PI_FLEX.ratePlanCode, isForUI: false });
  396 |     const extras = extraType === 'eci'
  397 |       ? await ApiCalls.graphqlGetAncillariesEciExtras({ hotelId: options.hotelID, bookingFlowId, nightsNumber: options.numberOfNights, startDate, endDate, adultsNumber: options.adultsNumber, childrenNumber: options.childNumber ?? 0 })
  398 |       : await ApiCalls.graphqlGetAncillariesLcoExtras({ hotelId: options.hotelID, bookingFlowId, nightsNumber: options.numberOfNights, startDate, endDate, adultsNumber: options.adultsNumber, childrenNumber: options.childNumber ?? 0 });
  399 |     if (!extras[0] || typeof extras[0].price !== 'number') throw new Error(`No ${extraType.toUpperCase()} extra with a price was returned by the ancillaries API`);
  400 |     const totalBefore = PriceHelpers.getPriceAmountFromUiLabel(await this.getBookingSummaryTotal());
  401 |     if (extraType === 'eci') await this.eciSection.clickAddRemoveEciButton();
  402 |     else await this.lcoSection.clickAddRemoveLCOButton();
  403 |     return this.validateChangedTotal(totalBefore, extras[0].price * direction, options.countryCode);
  404 |   }
  405 | 
  406 |   // ######## UI validations ########
  407 | 
  408 |   /** Validate page loading, reservation registration, and the expected page indicator. */
  409 |   async validatePage(): Promise<void> {
  410 |     console.log('Validating ancillaries page loaded');
  411 |     try {
  412 |       await this.pageLoadedIndicator.waitFor({ state: 'visible', timeout: AncillariesBasePage.PAGE_LOAD_TIMEOUT_MS });
  413 |     } catch {
> 414 |       throw new Error(`Ancillaries page did not load: page loaded indicator was not visible within ${AncillariesBasePage.PAGE_LOAD_TIMEOUT_MS}ms`);
      |             ^ Error: Ancillaries page did not load: page loaded indicator was not visible within 30000ms
  415 |     }
  416 | 
  417 |     await this.page.waitForFunction(
  418 |       () => new URL(window.location.href).searchParams.has('reservationId'),
  419 |       undefined,
  420 |       { timeout: browser.options.actionTimeout }
  421 |     );
  422 |     const basketReferenceId = await this.getBasketReferenceIdFromUrl();
  423 |     const basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReferenceId);
  424 |     ApiReservationCalls.createdReservations.push(basket);
  425 |     console.log(`Created reservation with "${basket.reference}" basket reference for "${basket.hotelId}" hotel id.`);
  426 |   }
  427 | 
  428 |   /** Validate that the booking summary total contains the expected value. */
  429 |   async validateBookingSummaryTotal(expectedTotal: string): Promise<void> {
  430 |     console.log(`Validating booking summary total contains: ${expectedTotal}`);
  431 |     await this.expandSectionOnMobile();
  432 |     await expect(this.bookingSummaryTotalCostAmount, `Booking summary total should contain ${expectedTotal}`).toContainText(expectedTotal);
  433 |   }
  434 | 
  435 |   /** Validate the added adult meals for a room in the booking summary. */
  436 |   async validateAdultMealsPerRoom(roomIndex: number, expectedMeals: string, options: { exactMatch?: boolean } = {}): Promise<void> {
  437 |     const { exactMatch = false } = options;
  438 |     console.log(`Validating adult meals for room ${roomIndex}: ${expectedMeals}`);
  439 |     await this.expandSectionOnMobile();
  440 | 
  441 |     const adultMealLabel = this.page.locator(
  442 |       `[data-testid*="${this.ancillariesProjectIdentifier}-BookingSummary"][data-testid*="RoomInformation-AdultMeal"]`
  443 |     );
  444 |     const roomWrapper = this.page.locator(
  445 |       `[data-testid*="${this.ancillariesProjectIdentifier}-BookingSummary"][data-testid*="RoomInformation-Wrapper"]`
  446 |     ).locator('> div').nth(roomIndex);
  447 |     const roomMealLabel = roomWrapper.locator('[data-testid*="RoomInformation-AdultMeal"]');
  448 |     const targetLabel = await roomMealLabel.count() > 0 ? roomMealLabel.first() : adultMealLabel.nth(roomIndex);
  449 | 
  450 |     if (exactMatch) {
  451 |       await expect(targetLabel, `Adult meals label should exactly match: ${expectedMeals}`).toHaveText(expectedMeals, { timeout: 10000 });
  452 |     } else {
  453 |       await expect(targetLabel, `Adult meals label should contain: ${expectedMeals}`).toContainText(expectedMeals, { timeout: 10000 });
  454 |     }
  455 |   }
  456 | 
  457 |   /** Validate the Continue button's localized label. */
  458 |   async validateContinueButtonLabel(label: string): Promise<void> {
  459 |     console.log(`Validate continue button label: ${label}`);
  460 |     await expect(this.continueButton, 'Continue button label').toContainText(label);
  461 |   }
  462 | 
  463 |   /** Validate the ancillaries route and reservation identifier for a selected hotel and rate. */
  464 |   async validateUrl({ hotel, ratePlanCode }: { hotel: { id: string; threeLetterId: string }; ratePlanCode: string }): Promise<void> {
  465 |     console.log(`Validate ancillaries URL for hotel ${hotel.id}`);
  466 |     const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: hotel.id, ratePlanCode });
  467 |     const currentUrl = new URL(this.page.url());
  468 |     expect(currentUrl.pathname, 'Ancillaries URL should contain the booking flow and route').toContain(`/${bookingFlowId}/${this.url}`);
  469 |     expect(currentUrl.searchParams.get('reservationId'), 'Ancillaries URL reservationId should match hotel code').toBe(hotel.threeLetterId);
  470 |   }
  471 | 
  472 |   /** Validate the ancillaries loading spinner and its message when visible. */
  473 |   async validateLoadingSpinner(isDisplayed: boolean): Promise<void> {
  474 |     console.log(`Validate loading spinner displayed=${isDisplayed}`);
  475 |     if (isDisplayed) {
  476 |       await expect(this.loadingSpinner, 'Loading spinner visibility').toBeVisible();
  477 |       await expect(this.loadingText, 'Booking loading message').toContainText(await Strings.BOOKING_LOADING.name);
  478 |     } else {
  479 |       await expect(this.loadingSpinner, 'Loading spinner visibility').not.toBeVisible();
  480 |     }
  481 |   }
  482 | 
  483 |   /** Validate the booking total including API-provided preselected adult meals. */
  484 |   async validateTotalCostOfPreselectedMeals({ hotelID, ratePlanCode = HotelRates.PI_FLEX.ratePlanCode, basketReference, adultMealsAdded, numberOfNights, preselectedMeal }: {
  485 |     hotelID: string; ratePlanCode?: string; basketReference: string; adultMealsAdded: number; numberOfNights: number; preselectedMeal: string;
  486 |   }): Promise<string> {
  487 |     console.log(`Validate total cost of preselected meal: ${preselectedMeal}`);
  488 |     const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: hotelID, ratePlanCode });
  489 |     const [mealList, bookingInformation] = await Promise.all([
  490 |       ApiCalls.graphqlGetAncillariesAdultMeals({ hotelId: hotelID, bookingFlowId }),
  491 |       ApiContentCalls.graphqlGetBookingInformation({ basketReference }),
  492 |     ]);
  493 |     const meal = mealList.find(item => item.name?.trim() === preselectedMeal);
  494 |     if (!meal || typeof meal.price !== 'number') throw new Error(`Preselected meal "${preselectedMeal}" with a price was not returned by the ancillaries API`);
  495 |     if (typeof bookingInformation.totalCost !== 'number' || !bookingInformation.currencyCode) throw new Error(`Booking information for "${basketReference}" did not include total cost and currency`);
  496 |     const expectedTotal = await Locales.formatPriceBasedOnCurrencyCode(
  497 |       (bookingInformation.totalCost + meal.price * adultMealsAdded * numberOfNights).toFixed(2), bookingInformation.currencyCode
  498 |     );
  499 |     await expect(this.bookingSummaryTotalCostAmount, 'Booking overview total cost including preselected meals').toHaveText(expectedTotal);
  500 |     return expectedTotal;
  501 |   }
  502 | 
  503 |   /**
  504 |    * Validate a booking-total change using the country-specific currency format.
  505 |    * @returns The expected formatted booking total.
  506 |    */
  507 |   private async validateChangedTotal(totalBefore: number, costDifference: number, countryCode = Constants.UK_COUNTRY_CODE): Promise<string> {
  508 |     const currency = countryCode === Constants.UK_COUNTRY_CODE ? Constants.UK_CURRENCY_CODE : Constants.EURO_CURRENCY_CODE;
  509 |     const expectedTotal = await Locales.formatPriceBasedOnCurrencyCode((totalBefore + costDifference).toFixed(2), currency);
  510 |     await expect(this.bookingSummaryTotalCostAmount, 'Booking overview total cost value').toHaveText(expectedTotal);
  511 |     return expectedTotal;
  512 |   }
  513 | }
  514 | 
```