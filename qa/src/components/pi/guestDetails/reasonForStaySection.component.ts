import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * The Reason For Stay section (Guest Details) containing the UI elements, custom actions and validations.
 * Mirrors qa/reference `components/opera/guestDetails/reasonForStaySection.js`.
 */
export class ReasonForStaySectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly reasonForStayGroup: Locator = this.page.locator('div[data-testid="GuestDetails-ReasonForStay"]');
  readonly errorMessageLabel: Locator = this.reasonForStayGroup.locator('p').first();
  readonly leisureRadioButton: Locator = this.page.locator('span[data-testid="GuestDetails-ReasonForStay-Leisure"]');
  readonly leisureLabel: Locator = this.leisureRadioButton.locator('xpath=following-sibling::span/p');
  readonly businessRadioButton: Locator = this.page.locator('span[data-testid="GuestDetails-ReasonForStay-Business"]');
  readonly businessLabel: Locator = this.businessRadioButton.locator('xpath=following-sibling::span/p');
  readonly guestDetailsForm: Locator = this.page.locator('#guestDetailsForm');
  readonly titleLabel: Locator = this.guestDetailsForm.locator('div div p');
  readonly mainBannerLabel: Locator = this.page.locator('[data-testid="GuestDetails-CityTax-MainBanner"] [data-testid="AlertDescription"]');
  readonly newTotalNotificationLabel: Locator = this.page.locator('[data-testid="GuestDetails-CityTax-SecondaryBanner"] [data-testid="AlertDescription"]');

  // ######## UI actions/navigation ########

  /** Click the Leisure reason-for-stay radio button. */
  async clickLeisureRadioButton(): Promise<void> {
    console.log('Click leisure Radio Button');
    await this.leisureRadioButton.scrollIntoViewIfNeeded();
    await this.leisureRadioButton.click();
  }

  /** Click the Business reason-for-stay radio button. */
  async clickBusinessRadioButton(): Promise<void> {
    console.log('Click business Radio Button');
    await this.businessRadioButton.scrollIntoViewIfNeeded();
    await this.businessRadioButton.click();
  }

  /** Click the reason for stay radio button matching the given reason. */
  async clickReasonForStayRadioButton(reasonForStay: 'leisure' | 'business'): Promise<void> {
    if (reasonForStay === 'leisure') {
      await this.clickLeisureRadioButton();
    } else {
      await this.clickBusinessRadioButton();
    }
  }

  // ######## UI validations ########

  /** Validate the Reason For Stay error message is displayed. */
  async validateErrorMessageReasonForStay(): Promise<void> {
    await expect(this.errorMessageLabel, 'Error message for Reason for Stay').toHaveText(await Strings.REASON_FOR_STAY_ERROR.name);
  }

  /** Validate the Reason For Stay title. */
  async validateReasonForStayTitle(): Promise<void> {
    console.log('Validate Reason for Stay title');
    await expect(this.titleLabel, 'Reason for Stay title text').toContainText(await Strings.ARE_YOU_STAYING_FOR_LEISURE_OR_BUSINESS.name);
  }

  /** Validate the Reason For Stay radio buttons and their labels/selection state. */
  async validateReasonForStayButtons({
    isLeisureSelected = false,
    isBusinessSelected = false,
  }: { isLeisureSelected?: boolean; isBusinessSelected?: boolean } = {}): Promise<void> {
    console.log(`Validate Reason For Stay buttons: isLeisureSelected=${isLeisureSelected}, isBusinessSelected=${isBusinessSelected}`);

    await expect(this.leisureLabel, 'Leisure label').toHaveText(await Strings.REASON_FOR_STAY_LEISURE_LABEL.name);
    await expect(this.businessLabel, 'Business label').toHaveText(await Strings.REASON_FOR_STAY_BUSINESS_LABEL.name);

    if (isLeisureSelected) {
      await expect(this.leisureRadioButton, 'Leisure Radio Button should be checked').toHaveAttribute('data-checked', '');
      await expect(this.businessRadioButton, 'Business Radio Button should not be checked').not.toHaveAttribute('data-checked', '');
    } else if (isBusinessSelected) {
      await expect(this.leisureRadioButton, 'Leisure Radio Button should not be checked').not.toHaveAttribute('data-checked', '');
      await expect(this.businessRadioButton, 'Business Radio Button should be checked').toHaveAttribute('data-checked', '');
    }
  }

  /** Validate the city tax new-total notification banner visibility. */
  async validateReasonForStayNewTotalNotificationsIsDisplayed(isDisplayed = true): Promise<void> {
    if (isDisplayed) {
      await expect(this.newTotalNotificationLabel, 'reason for stay new total label').toBeVisible();
    } else {
      await expect(this.newTotalNotificationLabel, 'reason for stay new total label').not.toBeVisible();
    }
  }

  /** Validate the city tax main banner visibility. */
  async validateReasonForStayMainBannerIsDisplayed(isDisplayed = true): Promise<void> {
    console.log(`Validate city tax main banner isDisplayed = ${isDisplayed}`);
    if (isDisplayed) {
      await expect(this.mainBannerLabel, 'reason for stay main banner').toBeVisible();
    } else {
      await expect(this.mainBannerLabel, 'reason for stay main banner').not.toBeVisible();
    }
  }

  /** Validate the city tax main banner text for a leisure guest. */
  async validateReasonForStayLeisureCityTaxNotification(): Promise<void> {
    await expect(this.mainBannerLabel, 'Local tax text notification for leisure guests').toContainText(await Strings.CITY_TAX_LEISURE_MESSAGE_NO_EXEMPT.name);
  }

  /** Validate the city tax main banner text for a business guest. */
  async validateReasonForStayBusinessCityTaxNotification(): Promise<void> {
    await expect(this.mainBannerLabel, 'Local tax text notification for business guests').toContainText(await Strings.CITY_TAX_BUSINESS_MESSAGE_NO_EXEMPT.name);
  }
}
