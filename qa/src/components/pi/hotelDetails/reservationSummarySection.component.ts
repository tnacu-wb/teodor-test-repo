import { type Page, type Locator, expect } from '@playwright/test';
import { Constants } from '@test-data/constants';
import { PriceHelpers } from '../../../utils';

export type PricePerNight = {
  pricePerNight: number;
  date: string;
};

/**
 * Reservation summary / basket section within the hotel details page containing the UI elements,
 * custom actions and validations. Mirrors qa/reference
 * `components/opera/hotelDetails/reservationSummarySection.js` (simplified: per-night price/date
 * extraction, actions, and validations used by the Hotel Details Page.
 */
export class ReservationSummarySectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly lastFewRoomsLabel: Locator = this.page.locator('p[data-testid="hdp_basketLastFewRooms"]');
  readonly summaryTitleLabel: Locator = this.page.locator('p[data-testid="hdp_basketRoomAndRatePlan"]');
  readonly summaryNumberOfRoomsLabel: Locator = this.page.locator('p[data-testid="hdp_basketNrOfRooms"]');
  readonly staySummaryLabel: Locator = this.page.locator('p[data-testid="hdp_basketStayText"]');
  readonly priceSummaryLabel: Locator = this.page.locator('p[data-testid="hdp_basketPriceText"]');
  readonly nightsPeriodSummaryLabel: Locator = this.page.locator('p[data-testid="hdp_basketNrOfNightsStay"]');
  readonly totalSummaryLabel: Locator = this.page.locator('p[data-testid="total-cost-title"]');
  readonly totalPriceSummaryLabel: Locator = this.page.locator('p[data-testid="mobile-total-cost"], p[data-testid="total-cost"]');
  readonly cityTaxExemptLabel: Locator = this.page.locator('p[data-testid="hdp_basketCityTaxExempt"]');
  readonly basketContainer: Locator = this.page.locator(
    Constants.BROWSER_RESOLUTIONS.isDesktop() ? '[data-testid="basket"]' : '[data-testid="mobile-basket"]'
  );
  readonly basketStayPriceContainer: Locator = this.page.locator('[data-testid="hdp_basketStayPrice"]');
  readonly priceBreakdownRows: Locator = this.basketContainer.locator(
    Constants.BROWSER_RESOLUTIONS.isDesktop()
      ? '[data-testid="hdp_basketBreakdownRow"]:visible'
      : '[data-testid="hdp_mobileBasketBreakdownRow"]:visible'
  );
  readonly priceValueSummaryLabel: Locator = this.page.locator('[data-testid="total-cost-for-nights"]');
  readonly summaryRateLabel: Locator = this.page.locator('//p[@data-testid="hdp_basketRoomAndRatePlan"]/parent::div/p[2]');
  readonly seeBreakdownLink: Locator = this.page.locator(
    'a[data-testid="hdp_mobileBasketSeeBreakdownLink"]:visible, a[data-testid="hdp_basketSeeBreakdownLink"]:visible'
  ).first();
  readonly bookNowButton: Locator = this.page.locator('button[data-testid="hdp_basketBookNowButton"], button[data-testid="hdp_mobileBasketBookNowButton"]').filter({ visible: true }).first();
  readonly chooseRoomTypeButton: Locator = this.page.locator(
    '[data-testid="hdp_basketChooseRoomTypeButton"], [data-testid="hdp_mobileBasketChooseRoomTypeButton"]'
  );
  readonly keepYourBookingButton: Locator = this.page.locator(
    'button[data-testid="hdp_roomUpgradeModalPopup-SecondaryButton"], button[data-testid="hdp_roomUpgradeModalPopup_SecondaryButton"]'
  );

  /** Parse a localized HDP price-breakdown date label into an ISO calendar date. */
  private parsePriceBreakdownDate(dateText: string): string {
    const normalizedDateText = dateText
      .trim()
      .replace(/^[A-Za-z]{2,3}\.?(?=\s)/, '')
      .replace('Mär.', 'Mar')
      .replace('Mai', 'May')
      .replace('Okt.', 'Oct')
      .replace('Dez.', 'Dec');
    const parsedDate = new Date(normalizedDateText);
    if (Number.isNaN(parsedDate.getTime())) throw new Error(`Could not parse HDP price breakdown date: ${dateText}`);
    return `${parsedDate.getFullYear()}-${String(parsedDate.getMonth() + 1).padStart(2, '0')}-${String(parsedDate.getDate()).padStart(2, '0')}`;
  }

  /** Extract displayed per-night prices and dates for one selected room. */
  async extractPricesAndDatesPerNight(options: { rooms: Array<unknown> }): Promise<PricePerNight[][]> {
    console.log('Extracting prices and dates per night');
    await expect(this.priceBreakdownRows.first(), 'Price breakdown rows should be displayed').toBeVisible({ timeout: 10000 });
    if (options.rooms.length !== 1) {
      throw new Error('extractPricesAndDatesPerNight currently supports the single-room HDP breakdown used by this baseline');
    }

    const pricesAndDates: PricePerNight[] = [];
    for (let index = 0; index < await this.priceBreakdownRows.count(); index++) {
      const rowSpans = this.priceBreakdownRows.nth(index).locator('span');
      const dateText = (await rowSpans.nth(0).innerText()).trim();
      const priceText = (await rowSpans.nth(1).innerText()).trim();
      const pricePerNight = PriceHelpers.getPriceAmountFromUiLabel(priceText);
      const parsedPriceAndDate = {
        pricePerNight,
        date: this.parsePriceBreakdownDate(dateText),
      };
      pricesAndDates.push(parsedPriceAndDate);
    }
    return [pricesAndDates];
  }

  /** Sum displayed per-night prices for each selected room. */
  async getTotalRoomPrice(options: { rooms: Array<unknown> }): Promise<number[]> {
    console.log('Getting total room price from HDP breakdown');
    const pricesPerRoom = await this.extractPricesAndDatesPerNight(options);
    return pricesPerRoom.map(roomPrices => Number(roomPrices.reduce((total, price) => total + price.pricePerNight, 0).toFixed(2)));
  }

  /** Return the total price shown for the selected nights. */
  async getPriceBreakdownTotal(): Promise<string> {
    console.log('Getting price breakdown total');
    await this.priceValueSummaryLabel.waitFor({ state: 'visible', timeout: 30000 });
    return this.priceValueSummaryLabel.innerText();
  }

  // ######## UI actions/navigation ########

  /** Expand the booking summary price breakdown. */
  async clickSeeBreakdownLink(): Promise<void> {
    console.log('Clicking see breakdown link');
    await this.seeBreakdownLink.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
    await this.seeBreakdownLink.click();
    await expect(this.priceBreakdownRows.first(), 'Price breakdown rows should be displayed').toBeVisible({ timeout: 10000 });
  }

  /** Click Book now from the booking summary. */
  async clickBookNow(): Promise<void> {
    console.log('Clicking book now button');
    await this.page.waitForLoadState('domcontentloaded');
    await expect(this.bookNowButton, 'Book now button is enabled').toBeEnabled();
    await this.bookNowButton.scrollIntoViewIfNeeded();
    await this.page.waitForTimeout(2000);
    await this.bookNowButton.click();
  }

  /** Dismiss the optional Premier Plus room-upgrade modal when it is displayed. */
  async closePremierPlusRoomUpgradeModalIfPresent(): Promise<void> {
    console.log('Closing Premier Plus upgrade modal if present');
    try {
      await this.keepYourBookingButton.waitFor({ state: 'visible', timeout: 5000 });
      await this.keepYourBookingButton.click();
    } catch {
      // The optional upgrade modal is not displayed.
    }
  }

  /** Click the booking summary room-type chooser. */
  async clickChooseRoomTypeButton(): Promise<void> {
    console.log("Click on the 'choose room type' button");
    await this.chooseRoomTypeButton.scrollIntoViewIfNeeded();
    await this.chooseRoomTypeButton.click();
  }

  // ######## UI validations ########

  /** Validate the basket summary title/room-and-rate-plan label matches the expected text. */
  async validateSummaryTitle(expectedRateAndRoom: string): Promise<void> {
    await expect(this.summaryTitleLabel, 'Booking summary rate/room label').toContainText(expectedRateAndRoom);
  }

  /** Validate the number of rooms shown in the basket summary. */
  async validateNumberOfRooms(expectedRoomsCount: number): Promise<void> {
    await expect(this.summaryNumberOfRoomsLabel, 'Booking summary rooms count').toContainText(`${expectedRoomsCount}`);
  }

  /** Validate the last-few-rooms notification is displayed, or not. */
  async validateLastFewRoomsIsDisplayed(isDisplayed = true): Promise<void> {
    if (isDisplayed) {
      await expect(this.lastFewRoomsLabel, 'Last few rooms label').toBeVisible();
    } else {
      await expect(this.lastFewRoomsLabel, 'Last few rooms label').not.toBeVisible();
    }
  }

  /** Validate the selected booking summary rate plan. */
  async validateBookingSummaryRatePlan(options: { ratePlan: string; roomsCount?: number }): Promise<void> {
    const { ratePlan, roomsCount = 1 } = options;
    console.log(`Validating booking summary rate plan: ${ratePlan}`);
    if (!Constants.BROWSER_RESOLUTIONS.isDesktop()) return;
    if (roomsCount > 1) {
      await expect(this.summaryNumberOfRoomsLabel, 'Booking summary rooms count should match').toContainText(`${roomsCount}`);
      await expect(this.summaryNumberOfRoomsLabel, 'Booking summary rate plan should match').toContainText(ratePlan);
      return;
    }
    await expect(this.summaryRateLabel, 'Booking summary selected rate should match').toHaveText(ratePlan);
  }

  /** Validate that the booking summary total price is displayed. */
  async validateTotalPriceRate(isDisplayed = true): Promise<void> {
    console.log('Validating total price rate');
    if (isDisplayed) {
      await expect(this.totalPriceSummaryLabel, 'Total price summary should be displayed').toBeVisible();
      await expect(this.totalPriceSummaryLabel, 'Total price summary should contain currency and amount').toHaveText(/[£€$]\s*\d+/);
      return;
    }
    await expect(this.totalPriceSummaryLabel, 'Total price summary should not be displayed').not.toBeVisible();
  }

  /** Validate the expected per-night breakdown row count and prices. */
  async validatePricesPerNight(options: { numberOfDays: number; numberOfRooms: number; hotelCurrency: string }): Promise<void> {
    console.log('Validating prices per night in HDP breakdown');
    await expect(this.priceBreakdownRows, `Price breakdown should show ${options.numberOfDays} rows per room`).toHaveCount(
      options.numberOfDays * options.numberOfRooms,
      { timeout: 10000 }
    );
    const expectedSymbol = options.hotelCurrency.toUpperCase() === 'GBP' || options.hotelCurrency.toLowerCase() === 'gb' ? '£' : '€';
    for (let index = 0; index < await this.priceBreakdownRows.count(); index++) {
      const priceText = await this.priceBreakdownRows.nth(index).locator('span').nth(1).innerText();
      expect(priceText, `Price breakdown row ${index} should contain ${expectedSymbol}`).toContain(expectedSymbol);
      expect(PriceHelpers.getPriceAmountFromUiLabel(priceText), `Price breakdown row ${index} should contain a positive price`).toBeGreaterThan(0);
    }
  }

  /** Validate the booking summary price-breakdown section and total. */
  async validatePriceBreakdown(): Promise<void> {
    console.log('Validating price breakdown section');
    await this.basketStayPriceContainer.waitFor({ state: 'visible', timeout: 30000 });
    await expect(this.priceValueSummaryLabel, 'Price breakdown total should be displayed').toBeVisible();
    await expect(this.priceValueSummaryLabel, 'Price breakdown should contain currency and amount').toHaveText(/[£€$]\s*\d+/);
  }
}
