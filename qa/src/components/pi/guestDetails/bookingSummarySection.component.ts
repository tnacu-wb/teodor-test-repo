import { type Page, type Locator, expect } from '@playwright/test';
import { Constants } from '@test-data/constants';

/**
 * The Booking Summary section on the Guest Details page (vertical strip) containing the UI
 * elements, custom actions and validations. Mirrors qa/reference
 * `components/opera/guestDetails/bookingSummarySection.js`.
 */
export class GuestDetailsBookingSummarySectionComponent {
  private readonly page: Page = global.page;

  private get resolutionId(): string {
    return Constants.RESOLUTION_IDENTIFIER_FOR_DATATESTID_ATTRIBUTE;
  }

  // ######## UI elements/properties ########

  get mobileVariantExpandButton(): Locator {
    return this.page.locator('div[data-testid="BookingSummary-MobileVariant-SectionHeader"]');
  }

  get bookingSummaryContainer(): Locator {
    return this.page.locator(`div[data-testid="BookingSummary-${this.resolutionId}-Wrapper"]`);
  }

  get hotelNameLabel(): Locator {
    return this.page.locator(`h5[data-testid="BookingSummary-${this.resolutionId}-HotelInformation-HotelName"]`);
  }

  get rateLabel(): Locator {
    return this.page.locator(`p[data-testid="BookingSummary-${this.resolutionId}-RateInformation-Label"]`);
  }

  get rateStayInfoLabel(): Locator {
    return this.page.locator(`p[data-testid="BookingSummary-${this.resolutionId}-RateInformation-StayInfo"]`);
  }

  get totalCostAmountLabel(): Locator {
    return this.page.locator(`h2[data-testid="BookingSummary-${this.resolutionId}-TotalCost-CostAmount"]`);
  }

  get totalCostCityTaxMessage(): Locator {
    return this.page.locator(`p[data-testid="BookingSummary-${this.resolutionId}-TotalCost-TaxesMessage"]`);
  }

  get arrivalDateLabel(): Locator {
    return this.page.locator(`p[data-testid="BookingSummary-${this.resolutionId}-StayDatesInformation-ArrivalDate"]`);
  }

  get departureDateLabel(): Locator {
    return this.page.locator(`p[data-testid="BookingSummary-${this.resolutionId}-StayDatesInformation-DepartureDate"]`);
  }

  get nightsNumberLabel(): Locator {
    return this.page.locator(`p[data-testid="BookingSummary-${this.resolutionId}-StayDatesInformation-NightsNumber"]`);
  }

  get continueButton(): Locator {
    return this.page.locator('button[data-testid="BookingSummary-ContinueButton"]');
  }

  get adultMealsList(): Locator {
    return this.page.locator(`[data-testid="BookingSummary-${this.resolutionId}-RoomInformation-AdultMeal"]`);
  }

  get childrenMealsList(): Locator {
    return this.page.locator(`[data-testid="BookingSummary-${this.resolutionId}-RoomInformation-ChildrenMeal"]`);
  }

  get noMealsSelectedList(): Locator {
    return this.page.locator(`[data-testid="BookingSummary-${this.resolutionId}-RoomInformation-NoMealsSelected"]`);
  }

  get upgradeToFlexContainer(): Locator {
    return this.page.locator('div[data-testid="UpgradeToFlex-Wrapper"]');
  }

  get upgradeToFlexButton(): Locator {
    return this.page.locator('button[data-testid="UpgradeToFlex-Button"]');
  }

  // ######## UI actions/navigation ########

  /** Expand the booking summary on mobile if the expand header is displayed. */
  async expandOnMobile(): Promise<void> {
    const isVisible = await this.mobileVariantExpandButton.isVisible().catch(() => false);
    if (isVisible) {
      await this.mobileVariantExpandButton.click();
      await expect(this.bookingSummaryContainer).toBeVisible();
    }
  }

  /** Get the numeric total cost from the booking summary. */
  async getTotalCostValue(): Promise<string> {
    await this.totalCostAmountLabel.scrollIntoViewIfNeeded();
    return (await this.totalCostAmountLabel.innerText()).trim();
  }

  /** Click the 'Upgrade to Flex' button, if displayed. */
  async clickUpgradeToFlexButton(): Promise<void> {
    await this.upgradeToFlexButton.scrollIntoViewIfNeeded();
    await this.upgradeToFlexButton.click();
  }

  // ######## UI validations ########

  /** Validate the hotel name shown in the booking summary matches the expected hotel. */
  async validateHotelName(expectedHotelName: string): Promise<void> {
    await expect(this.hotelNameLabel, 'Booking summary hotel name').toHaveText(expectedHotelName);
  }

  /** Validate the number of nights shown in the booking summary. */
  async validateNightsNumber(expectedNights: number): Promise<void> {
    await expect(this.nightsNumberLabel, 'Booking summary nights number').toContainText(`${expectedNights}`);
  }
}
