import { expect, type Locator } from '@playwright/test';
import { BasePage } from './base.page';
import { AdvertisingSectionBaseComponent } from '../../components/shared/confirmBooking/advertisingSectionBase.component';
import { BookingDetailsIntroSectionComponent } from '../../components/shared/confirmBooking/bookingDetailsIntroSection.component';
import { HotelDirectionsSectionComponent } from '../../components/shared/confirmBooking/hotelDirectionsSection.component';
import { PrivacyNoticeSectionComponent } from '../../components/shared/confirmBooking/privacyNoticeSection.component';
import { RoomDetailsSectionBaseComponent } from '../../components/shared/confirmBooking/roomDetailsSectionBase.component';
import { TotalCostSectionBaseComponent } from '../../components/shared/confirmBooking/totalCostSectionBase.component';

/** Common Business Booker confirmation page behavior migrated from the reference base page. */
export abstract class ConfirmBookingPageBase extends BasePage {
  // ######## properties ########
  readonly url = 'confirmation';

  // ######## UI elements/properties ########
  readonly bookingDetailsIntroSection = new BookingDetailsIntroSectionComponent();
  readonly hotelDirectionsSection = new HotelDirectionsSectionComponent();
  readonly privacySection = new PrivacyNoticeSectionComponent();
  readonly roomDetailsSection = new RoomDetailsSectionBaseComponent();
  readonly totalCostSection = new TotalCostSectionBaseComponent();
  readonly advertisingSection = new AdvertisingSectionBaseComponent();

  readonly pageLoadedIndicator: Locator = this.page.locator('[data-testid="ThanksForBooking-Container"]');
  readonly thanksForBookingLabel: Locator = this.page.locator('[data-testid="ThanksForBooking-Title-Name"]');
  readonly basketReferenceIdLabel: Locator = this.page.locator('[data-testid="BookingReferenceDetails-Id"]');
  readonly bookingConfirmationSection: Locator = this.page.locator('[data-testid="BookingReferenceDetails-Label"]').locator('..');
  readonly hotelDirectionsLabel: Locator = this.page.locator('[data-testid="hotelDirections-label"]');
  readonly cityTaxNotification: Locator = this.page.locator('[data-testid="AlertDescription"]');
  readonly cityTaxNotificationLink: Locator = this.cityTaxNotification.locator('a');

  // ######## UI actions/navigation ########
  /** Click the confirmation page action that returns to the homepage. */
  async clickGoToHomePageButton(): Promise<void> {
    console.log('Click on Continue to homepage button');
    const goToHomePageButton = this.page.locator('button[data-testid="continueBtn"], button[data-testid*="BackToDashboardButton"]');
    await goToHomePageButton.scrollIntoViewIfNeeded();
    await goToHomePageButton.click();
  }

  // ######## UI validations ########
  /** Validate that the Business Booker confirmation page has loaded. */
  async validatePage(): Promise<void> {
    console.log('Validate Booking confirmation page was reached');
    await this.totalCostSection.totalCostSectionContainer.waitFor({ state: 'visible', timeout: 90000 });
    await expect(
      this.pageLoadedIndicator.or(this.thanksForBookingLabel).first(),
      'Confirm booking page marker',
    ).toBeVisible({ timeout: 90000 });
  }
}