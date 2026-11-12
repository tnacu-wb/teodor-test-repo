# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/tc-468889-guest-piba-poa.spec.ts >> TC-468889: Guest PIBA Pay on Arrival Booking >> TC-468889: Guest user books UK hotel with PIBA Pay on Arrival
- Location: tests/regressions/pi/tc-468889-guest-piba-poa.spec.ts:20:7

# Error details

```
Error: Book now button is enabled

expect(locator).toBeEnabled() failed

Locator: locator('button[data-testid="hdp_basketBookNowButton"], button[data-testid="hdp_mobileBasketBookNowButton"]').filter({ visible: true }).first()
Expected: enabled
Timeout: 15000ms
Error: element(s) not found

Call log:
  - Book now button is enabled with timeout 15000ms
  - waiting for locator('button[data-testid="hdp_basketBookNowButton"], button[data-testid="hdp_mobileBasketBookNowButton"]').filter({ visible: true }).first()

```

```yaml
- alert
- region "Notifications-top"
- region "Notifications-top-left"
- region "Notifications-top-right"
- region "Notifications-bottom-left"
- region "Notifications-bottom"
- region "Notifications-bottom-right"
- dialog "We'd like to send you notifications Close":
  - banner:
    - img
    - text: We'd like to send you notifications
    - button "Close":
      - img
  - text: Get updated with our great offers, news and services. These can be turned off at any time. Read our
  - link "Privacy Policy":
    - /url: /gb/en/terms/privacy-policy.html
  - text: for more information.
  - button "Allow"
  - button "Don't allow"
```

# Test source

```ts
  17  |   private readonly page: Page = global.page;
  18  | 
  19  |   // ######## UI elements/properties ########
  20  | 
  21  |   readonly lastFewRoomsLabel: Locator = this.page.locator('p[data-testid="hdp_basketLastFewRooms"]');
  22  |   readonly summaryTitleLabel: Locator = this.page.locator('p[data-testid="hdp_basketRoomAndRatePlan"]');
  23  |   readonly summaryNumberOfRoomsLabel: Locator = this.page.locator('p[data-testid="hdp_basketNrOfRooms"]');
  24  |   readonly staySummaryLabel: Locator = this.page.locator('p[data-testid="hdp_basketStayText"]');
  25  |   readonly priceSummaryLabel: Locator = this.page.locator('p[data-testid="hdp_basketPriceText"]');
  26  |   readonly nightsPeriodSummaryLabel: Locator = this.page.locator('p[data-testid="hdp_basketNrOfNightsStay"]');
  27  |   readonly totalSummaryLabel: Locator = this.page.locator('p[data-testid="total-cost-title"]');
  28  |   readonly totalPriceSummaryLabel: Locator = this.page.locator('p[data-testid="mobile-total-cost"], p[data-testid="total-cost"]');
  29  |   readonly cityTaxExemptLabel: Locator = this.page.locator('p[data-testid="hdp_basketCityTaxExempt"]');
  30  |   readonly basketContainer: Locator = this.page.locator(
  31  |     Constants.BROWSER_RESOLUTIONS.isDesktop() ? '[data-testid="basket"]' : '[data-testid="mobile-basket"]'
  32  |   );
  33  |   readonly basketStayPriceContainer: Locator = this.page.locator('[data-testid="hdp_basketStayPrice"]');
  34  |   readonly priceBreakdownRows: Locator = this.basketContainer.locator(
  35  |     Constants.BROWSER_RESOLUTIONS.isDesktop()
  36  |       ? '[data-testid="hdp_basketBreakdownRow"]:visible'
  37  |       : '[data-testid="hdp_mobileBasketBreakdownRow"]:visible'
  38  |   );
  39  |   readonly priceValueSummaryLabel: Locator = this.page.locator('[data-testid="total-cost-for-nights"]');
  40  |   readonly summaryRateLabel: Locator = this.page.locator('//p[@data-testid="hdp_basketRoomAndRatePlan"]/parent::div/p[2]');
  41  |   readonly seeBreakdownLink: Locator = this.page.locator(
  42  |     'a[data-testid="hdp_mobileBasketSeeBreakdownLink"]:visible, a[data-testid="hdp_basketSeeBreakdownLink"]:visible'
  43  |   ).first();
  44  |   readonly bookNowButton: Locator = this.page.locator('button[data-testid="hdp_basketBookNowButton"], button[data-testid="hdp_mobileBasketBookNowButton"]').filter({ visible: true }).first();
  45  |   readonly chooseRoomTypeButton: Locator = this.page.locator(
  46  |     '[data-testid="hdp_basketChooseRoomTypeButton"], [data-testid="hdp_mobileBasketChooseRoomTypeButton"]'
  47  |   );
  48  |   readonly keepYourBookingButton: Locator = this.page.locator(
  49  |     'button[data-testid="hdp_roomUpgradeModalPopup-SecondaryButton"], button[data-testid="hdp_roomUpgradeModalPopup_SecondaryButton"]'
  50  |   );
  51  | 
  52  |   /** Parse a localized HDP price-breakdown date label into an ISO calendar date. */
  53  |   private parsePriceBreakdownDate(dateText: string): string {
  54  |     const normalizedDateText = dateText
  55  |       .trim()
  56  |       .replace(/^[A-Za-z]{2,3}\.?(?=\s)/, '')
  57  |       .replace('Mär.', 'Mar')
  58  |       .replace('Mai', 'May')
  59  |       .replace('Okt.', 'Oct')
  60  |       .replace('Dez.', 'Dec');
  61  |     const parsedDate = new Date(normalizedDateText);
  62  |     if (Number.isNaN(parsedDate.getTime())) throw new Error(`Could not parse HDP price breakdown date: ${dateText}`);
  63  |     return `${parsedDate.getFullYear()}-${String(parsedDate.getMonth() + 1).padStart(2, '0')}-${String(parsedDate.getDate()).padStart(2, '0')}`;
  64  |   }
  65  | 
  66  |   /** Extract displayed per-night prices and dates for one selected room. */
  67  |   async extractPricesAndDatesPerNight(options: { rooms: Array<unknown> }): Promise<PricePerNight[][]> {
  68  |     console.log('Extracting prices and dates per night');
  69  |     await expect(this.priceBreakdownRows.first(), 'Price breakdown rows should be displayed').toBeVisible({ timeout: 10000 });
  70  |     if (options.rooms.length !== 1) {
  71  |       throw new Error('extractPricesAndDatesPerNight currently supports the single-room HDP breakdown used by this baseline');
  72  |     }
  73  | 
  74  |     const pricesAndDates: PricePerNight[] = [];
  75  |     for (let index = 0; index < await this.priceBreakdownRows.count(); index++) {
  76  |       const rowSpans = this.priceBreakdownRows.nth(index).locator('span');
  77  |       const dateText = (await rowSpans.nth(0).innerText()).trim();
  78  |       const priceText = (await rowSpans.nth(1).innerText()).trim();
  79  |       const pricePerNight = PriceHelpers.getPriceAmountFromUiLabel(priceText);
  80  |       const parsedPriceAndDate = {
  81  |         pricePerNight,
  82  |         date: this.parsePriceBreakdownDate(dateText),
  83  |       };
  84  |       pricesAndDates.push(parsedPriceAndDate);
  85  |     }
  86  |     return [pricesAndDates];
  87  |   }
  88  | 
  89  |   /** Sum displayed per-night prices for each selected room. */
  90  |   async getTotalRoomPrice(options: { rooms: Array<unknown> }): Promise<number[]> {
  91  |     console.log('Getting total room price from HDP breakdown');
  92  |     const pricesPerRoom = await this.extractPricesAndDatesPerNight(options);
  93  |     return pricesPerRoom.map(roomPrices => Number(roomPrices.reduce((total, price) => total + price.pricePerNight, 0).toFixed(2)));
  94  |   }
  95  | 
  96  |   /** Return the total price shown for the selected nights. */
  97  |   async getPriceBreakdownTotal(): Promise<string> {
  98  |     console.log('Getting price breakdown total');
  99  |     await this.priceValueSummaryLabel.waitFor({ state: 'visible', timeout: 30000 });
  100 |     return this.priceValueSummaryLabel.innerText();
  101 |   }
  102 | 
  103 |   // ######## UI actions/navigation ########
  104 | 
  105 |   /** Expand the booking summary price breakdown. */
  106 |   async clickSeeBreakdownLink(): Promise<void> {
  107 |     console.log('Clicking see breakdown link');
  108 |     await this.seeBreakdownLink.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
  109 |     await this.seeBreakdownLink.click();
  110 |     await expect(this.priceBreakdownRows.first(), 'Price breakdown rows should be displayed').toBeVisible({ timeout: 10000 });
  111 |   }
  112 | 
  113 |   /** Click Book now from the booking summary. */
  114 |   async clickBookNow(): Promise<void> {
  115 |     console.log('Clicking book now button');
  116 |     await this.page.waitForLoadState('domcontentloaded');
> 117 |     await expect(this.bookNowButton, 'Book now button is enabled').toBeEnabled();
      |                                                                    ^ Error: Book now button is enabled
  118 |     await this.bookNowButton.scrollIntoViewIfNeeded();
  119 |     await this.page.waitForTimeout(2000);
  120 |     await this.bookNowButton.click();
  121 |   }
  122 | 
  123 |   /** Dismiss the optional Premier Plus room-upgrade modal when it is displayed. */
  124 |   async closePremierPlusRoomUpgradeModalIfPresent(): Promise<void> {
  125 |     console.log('Closing Premier Plus upgrade modal if present');
  126 |     try {
  127 |       await this.keepYourBookingButton.waitFor({ state: 'visible', timeout: 5000 });
  128 |       await this.keepYourBookingButton.click();
  129 |     } catch {
  130 |       // The optional upgrade modal is not displayed.
  131 |     }
  132 |   }
  133 | 
  134 |   /** Click the booking summary room-type chooser. */
  135 |   async clickChooseRoomTypeButton(): Promise<void> {
  136 |     console.log("Click on the 'choose room type' button");
  137 |     await this.chooseRoomTypeButton.scrollIntoViewIfNeeded();
  138 |     await this.chooseRoomTypeButton.click();
  139 |   }
  140 | 
  141 |   // ######## UI validations ########
  142 | 
  143 |   /** Validate the basket summary title/room-and-rate-plan label matches the expected text. */
  144 |   async validateSummaryTitle(expectedRateAndRoom: string): Promise<void> {
  145 |     await expect(this.summaryTitleLabel, 'Booking summary rate/room label').toContainText(expectedRateAndRoom);
  146 |   }
  147 | 
  148 |   /** Validate the number of rooms shown in the basket summary. */
  149 |   async validateNumberOfRooms(expectedRoomsCount: number): Promise<void> {
  150 |     await expect(this.summaryNumberOfRoomsLabel, 'Booking summary rooms count').toContainText(`${expectedRoomsCount}`);
  151 |   }
  152 | 
  153 |   /** Validate the last-few-rooms notification is displayed, or not. */
  154 |   async validateLastFewRoomsIsDisplayed(isDisplayed = true): Promise<void> {
  155 |     if (isDisplayed) {
  156 |       await expect(this.lastFewRoomsLabel, 'Last few rooms label').toBeVisible();
  157 |     } else {
  158 |       await expect(this.lastFewRoomsLabel, 'Last few rooms label').not.toBeVisible();
  159 |     }
  160 |   }
  161 | 
  162 |   /** Validate the selected booking summary rate plan. */
  163 |   async validateBookingSummaryRatePlan(options: { ratePlan: string; roomsCount?: number }): Promise<void> {
  164 |     const { ratePlan, roomsCount = 1 } = options;
  165 |     console.log(`Validating booking summary rate plan: ${ratePlan}`);
  166 |     if (!Constants.BROWSER_RESOLUTIONS.isDesktop()) return;
  167 |     if (roomsCount > 1) {
  168 |       await expect(this.summaryNumberOfRoomsLabel, 'Booking summary rooms count should match').toContainText(`${roomsCount}`);
  169 |       await expect(this.summaryNumberOfRoomsLabel, 'Booking summary rate plan should match').toContainText(ratePlan);
  170 |       return;
  171 |     }
  172 |     await expect(this.summaryRateLabel, 'Booking summary selected rate should match').toHaveText(ratePlan);
  173 |   }
  174 | 
  175 |   /** Validate that the booking summary total price is displayed. */
  176 |   async validateTotalPriceRate(isDisplayed = true): Promise<void> {
  177 |     console.log('Validating total price rate');
  178 |     if (isDisplayed) {
  179 |       await expect(this.totalPriceSummaryLabel, 'Total price summary should be displayed').toBeVisible();
  180 |       await expect(this.totalPriceSummaryLabel, 'Total price summary should contain currency and amount').toHaveText(/[£€$]\s*\d+/);
  181 |       return;
  182 |     }
  183 |     await expect(this.totalPriceSummaryLabel, 'Total price summary should not be displayed').not.toBeVisible();
  184 |   }
  185 | 
  186 |   /** Validate the expected per-night breakdown row count and prices. */
  187 |   async validatePricesPerNight(options: { numberOfDays: number; numberOfRooms: number; hotelCurrency: string }): Promise<void> {
  188 |     console.log('Validating prices per night in HDP breakdown');
  189 |     await expect(this.priceBreakdownRows, `Price breakdown should show ${options.numberOfDays} rows per room`).toHaveCount(
  190 |       options.numberOfDays * options.numberOfRooms,
  191 |       { timeout: 10000 }
  192 |     );
  193 |     const expectedSymbol = options.hotelCurrency.toUpperCase() === 'GBP' || options.hotelCurrency.toLowerCase() === 'gb' ? '£' : '€';
  194 |     for (let index = 0; index < await this.priceBreakdownRows.count(); index++) {
  195 |       const priceText = await this.priceBreakdownRows.nth(index).locator('span').nth(1).innerText();
  196 |       expect(priceText, `Price breakdown row ${index} should contain ${expectedSymbol}`).toContain(expectedSymbol);
  197 |       expect(PriceHelpers.getPriceAmountFromUiLabel(priceText), `Price breakdown row ${index} should contain a positive price`).toBeGreaterThan(0);
  198 |     }
  199 |   }
  200 | 
  201 |   /** Validate the booking summary price-breakdown section and total. */
  202 |   async validatePriceBreakdown(): Promise<void> {
  203 |     console.log('Validating price breakdown section');
  204 |     await this.basketStayPriceContainer.waitFor({ state: 'visible', timeout: 30000 });
  205 |     await expect(this.priceValueSummaryLabel, 'Price breakdown total should be displayed').toBeVisible();
  206 |     await expect(this.priceValueSummaryLabel, 'Price breakdown should contain currency and amount').toHaveText(/[£€$]\s*\d+/);
  207 |   }
  208 | }
  209 | 
```