import { expect, type Locator } from '@playwright/test';
import { TotalCostSectionComponent } from '../../components/ccui/confirmBooking/totalCostSection.component';
import { AdvertisingSectionComponent } from '../../components/ccui/confirmBooking/advertisingSection.component';
import { RoomDetailsSectionComponent } from '../../components/ccui/confirmBooking/roomDetailsSection.component';
import { Locales } from '../../test-data/locales';
import { Strings } from '../../test-data/strings';
import { UiUtils } from '../../utils/uiUtils';
import { BasePage } from '../shared/base.page';

/**
 * Confirm booking page from Opera CCUI environment containing the UI elements, custom actions and validations.
 */
export class ConfirmBookingPageCcui extends BasePage {
  // ######## properties ########

  readonly title = Strings.BOOKING_CONFIRMATION.data.default;

  // ######## UI elements/properties ########

  readonly continueToHomepageMainPageButton: Locator = this.page.getByRole('button', { name: Strings.CONTINUE_TO_HOMEPAGE.data.default ?? '' });
  readonly totalCostAmountLabel: Locator = this.page.locator('p[data-testid="TotalCostConfirm-amount"]');
  readonly bookingSummaryCityTax: Locator = this.page.locator('//p[@data-testid="BookingSummary-DesktopVariant-TotalCost-TaxesMessage"]');
  readonly roomDetailContainer: Locator = this.page.locator('//div[@data-testid="RoomDetailsSection"]');
  readonly roomTotalContainer: Locator = this.page.locator('div[data-testid="RoomCardHeader-room1"]');
  readonly cityTaxTextRoomTotalCostLabel: Locator = this.page.locator('//div[@data-testid="RoomCardInfo-room1-CityTaxMessage"]');
  readonly cityTaxTextTotalCostLabel: Locator = this.page.locator('p[data-testid="TotalCostConfirm-TaxesMessage"]');
  readonly roomTotalPriceAmount: Locator = this.page.locator('p[data-testid="RoomCardInfo-room1-RightColumn-RoomTotalPrice-Amount"]');
  readonly loadingSpinner: Locator = this.page.locator('g[transform="translate(50 50)"]');
  readonly loadingText: Locator = this.page.locator('p[class="chakra-text css-1krigk3"]');
  readonly totalCostSectionContainer: Locator = this.page.locator('[data-testid="TotalCostConfirm-container"]');
  readonly hotelDirectionsTitleLabel: Locator = this.page.locator('[data-testid="hotelDirections-label"]');
  readonly cityTaxNotification: Locator = this.page.locator('div[data-testid="AlertDescription"]');
  readonly cityTaxNotificationLink: Locator = this.cityTaxNotification.locator('a');
  readonly bookingReference: Locator = this.page.locator('[data-testid="BookingReferenceDetails-Id"], [data-testid*="BookingReference"][data-testid*="Id"]').first();

  // UI components

  readonly advertisingSection: AdvertisingSectionComponent = new AdvertisingSectionComponent();
  readonly roomDetailsSection: RoomDetailsSectionComponent = new RoomDetailsSectionComponent();
  readonly totalCostSection: TotalCostSectionComponent = new TotalCostSectionComponent();

  // ######## UI actions/navigation ########

  /** Click Go to home page button located bellow total cost. */
  async clickOnContinueToHomepageButton(): Promise<void> {
    console.log('Click on Continue to homepage button');
    await this.continueToHomepageMainPageButton.scrollIntoViewIfNeeded();
    await this.continueToHomepageMainPageButton.click();
  }

  /**
   * Get Booking reference id displayed on booking confirmation page.
   * @returns Booking reference ID text.
   */
  async getBookingReference(): Promise<string> {
    console.log('Get booking reference from CCUI Confirm booking page');
    await this.bookingReference.waitFor({ state: 'visible' });
    return (await this.bookingReference.textContent())?.trim() ?? '';
  }

  // ######## UI validations ########

  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log('Validate CCUI Confirm booking page was reached');
    await expect(this.bookingReference, 'CCUI confirm booking page booking reference').toBeVisible({ timeout: 120000 });
  }

  /** Validates Continue to home page button is located bellow Total Cost Section. */
  async validateTotalCostContinueToHomePageButtonLocation(): Promise<void> {
    console.log('Validates that Go to homepage button is bellow total cost section');
    await UiUtils.validateIsBelow({
      belowElement: this.continueToHomepageMainPageButton,
      aboveElement: this.totalCostSectionContainer,
      elementDescription: 'Continue to homepage button below Total cost section',
    });
  }

  /**
   * Validate the city tax message in room section.
   * @param isCityTaxDisplayed Should it be displayed or not.
   */
  async validateRoomCityTaxMessage(isCityTaxDisplayed = true): Promise<void> {
    console.log('Validate the city tax message in room section');
    await this.roomTotalPriceAmount.scrollIntoViewIfNeeded();
    if (isCityTaxDisplayed) {
      await expect(this.cityTaxTextRoomTotalCostLabel, 'City Tax Text Room total cost section').toBeVisible();
      await expect(this.cityTaxTextRoomTotalCostLabel, 'City tax message text').toHaveText(await Strings.CITY_TAX_MESSAGE.name);
    } else {
      await expect(this.cityTaxTextRoomTotalCostLabel, 'City Tax Text').toBeHidden();
    }
  }

  /**
   * Validate the city tax message in total cost section.
   * @param isCityTaxDisplayed Should it be displayed or not.
   */
  async validateTotalCostCityTaxMessage(isCityTaxDisplayed = true): Promise<void> {
    console.log('Validate the city tax message in total cost section');
    await this.totalCostAmountLabel.scrollIntoViewIfNeeded();
    if (isCityTaxDisplayed) {
      await expect(this.cityTaxTextTotalCostLabel, 'City Tax Text Total Cost section').toBeVisible();
      await expect(this.cityTaxTextTotalCostLabel, 'City Tax Text Total Cost section text').toHaveText(await Strings.CITY_TAX_MESSAGE.name);
    } else {
      await expect(this.cityTaxTextTotalCostLabel, 'City Tax Text Total Cost section').toBeHidden();
    }
  }

  /**
   * Validate the city tax and fees message in booking summary section.
   * @param isCityTaxDisplayed Should it be displayed or not.
   */
  async validateBookingSummaryCityTaxMessage(isCityTaxDisplayed = true): Promise<void> {
    console.log('Validate the city tax and fees message in booking summary section');
    await this.bookingSummaryCityTax.scrollIntoViewIfNeeded();
    if (isCityTaxDisplayed) {
      await expect(this.bookingSummaryCityTax, 'Taxes and fees message').toBeVisible();
      await expect(this.bookingSummaryCityTax, 'Taxes and fees message text').toHaveText(await Strings.CITY_TAX_MESSAGE.name);
    } else {
      await expect(this.bookingSummaryCityTax, 'Taxes and fees message').toBeHidden();
    }
  }

  /**
   * Validate the city tax notification container.
   * @param isDisplayed If it should be displayed or not.
   */
  async validateCityTaxNotification(isDisplayed = true): Promise<void> {
    console.log('Validate the city tax notification container');
    if (isDisplayed) {
      await expect(this.cityTaxNotification, 'City Tax Notification').toBeVisible();
      await expect(this.cityTaxNotification, 'city tax notification text').toContainText(await Strings.CITY_TAX_NOTIFICATION_PI.name);
      const expectedLink = global.browser.options.locale === Locales.DE_DE.name
        ? await Strings.CITY_TAX_NOTIFICATION_LINK.name
        : await Strings.CITY_TAX_NOTIFICATION_LINK_PI.name;
      await expect(this.cityTaxNotificationLink, 'Validate if the link is correct').toHaveAttribute('href', expectedLink);
    } else {
      await expect(this.cityTaxNotification, 'City Tax Notification').toBeHidden();
    }
  }

  /**
   * Validate the progress spinner that leads to the confirmation page.
   * @param isDisplayed Should it be displayed or not.
   */
  async validateProgressSpinner(isDisplayed = true): Promise<void> {
    console.log('Validate the progress spinner that leads to the confirmation page');
    if (isDisplayed) {
      await expect(this.loadingSpinner, 'Loading spinner visibility check').toBeVisible();
      await expect(this.loadingText, 'Loading text visibility check').toBeVisible();
      await expect(this.loadingText, 'Loading text').toHaveText(await Strings.BOOKING_LOADING.name);
    } else {
      await expect(this.loadingSpinner, 'Loading spinner visibility check').toBeHidden();
      await expect(this.loadingText, 'Loading text visibility check').toBeHidden();
    }
  }

  /** Validate the position on Page for Total Cost Section on Confirm Booking Page. */
  async validateTotalCostSectionPosition(): Promise<void> {
    console.log('Validate the position of Total Cost Section on Confirm Booking Page');
    await UiUtils.validateIsBetween({
      belowElement: this.continueToHomepageMainPageButton,
      middleElement: this.totalCostSectionContainer,
      aboveElement: this.hotelDirectionsTitleLabel,
    });
  }
}