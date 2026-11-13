import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { Constants } from '@test-data/constants';

const ID = 'AncillariesPage';

/**
 * The booking details/info/overview sections on the Ancillaries page vertical strip containing
 * the UI elements, custom actions and validations. Mirrors qa/reference
 * `components/opera/ancillaries/bookingSummarySection.js` (simplified: multi-room panel
 * composition delegated to `RoomPanelComponent` via index rather than a dedicated getter class).
 */
export class AncillariesBookingSummarySectionComponent {
  private readonly page: Page = global.page;

  private get resolutionId(): string {
    return Constants.RESOLUTION_IDENTIFIER_FOR_DATATESTID_ATTRIBUTE;
  }

  // ######## UI elements/properties ########

  get roomPanelsList(): Locator {
    return this.page.locator(`div[data-testid="${ID}-BookingSummary-${this.resolutionId}-RoomInformation-Wrapper"] > div`);
  }

  get hotelNameLabel(): Locator {
    return this.page.locator(`[data-testid="${ID}-BookingSummary-${this.resolutionId}-HotelInformation-HotelName"]`);
  }

  get mobileVariantExpandButton(): Locator {
    return this.page.locator(`[data-testid="${ID}-BookingSummary-MobileVariant-ExpandButton"]`);
  }

  get continueButton(): Locator {
    return this.page.locator(`button[data-testid="${ID}-BookingSummary-ContinueButton"]`);
  }

  get totalCostAmountLabel(): Locator {
    return this.page.locator(`[data-testid="${ID}-BookingSummary-${this.resolutionId}-TotalCost-CostAmount"]`);
  }

  get arrivalDateLabel(): Locator {
    return this.page.locator(`[data-testid="${ID}-BookingSummary-${this.resolutionId}-StayDatesInformation-ArrivalDate"]`);
  }

  get departureDateLabel(): Locator {
    return this.page.locator(`[data-testid="${ID}-BookingSummary-${this.resolutionId}-StayDatesInformation-DepartureDate"]`);
  }

  get nightsNumberLabel(): Locator {
    return this.page.locator(`[data-testid="${ID}-BookingSummary-${this.resolutionId}-StayDatesInformation-NightsNumber"]`);
  }

  get preselectedMealNotificationLabel(): Locator {
    return this.page.locator(`[data-testid="${ID}-BookingSummary-${this.resolutionId}-PreselectedMealNotification"]`);
  }

  // ######## UI actions/navigation ########

  /** Expand the booking summary on mobile if the expand button is displayed. */
  async expandOnMobile(): Promise<void> {
    const isVisible = await this.mobileVariantExpandButton.isVisible().catch(() => false);
    if (isVisible) {
      await this.mobileVariantExpandButton.click();
    }
  }

  /** Click the Continue button from the booking summary. */
  async clickContinueButton(): Promise<void> {
    await this.continueButton.scrollIntoViewIfNeeded();
    await this.continueButton.click();
  }

  // ######## UI validations ########

  /** Validate whether the preselected-meal notification is displayed. */
  async validatePreselectedMealNotification(isDisplayed: boolean): Promise<void> {
    console.log(`Validate preselected meal notification displayed=${isDisplayed}`);
    if (isDisplayed) {
      await expect(this.preselectedMealNotificationLabel, 'Preselected meal notification').toBeVisible();
      return;
    }
    await expect(this.preselectedMealNotificationLabel, 'Preselected meal notification').not.toBeVisible();
  }

  /** Validate the hotel name shown in the booking summary. */
  async validateHotelName(expectedHotelName: string): Promise<void> {
    await expect(this.hotelNameLabel, 'Booking summary hotel name').toHaveText(expectedHotelName);
  }

  /** Validate the total cost amount contains the expected value. */
  async validateTotalCostAmount(expectedAmount: string): Promise<void> {
    await expect(this.totalCostAmountLabel, 'Booking summary total cost').toContainText(expectedAmount);
  }
}
