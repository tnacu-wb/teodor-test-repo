import { expect, type Locator } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { CcuiComponent } from '../baseCcui.component';

/**
 * Advertising section from the CCUI confirmation page.
 * Mirrors qa/reference/test/pages/components/ccui/confirmBooking/advertisingSection.js
 * and its common advertising-section base component.
 */
export class AdvertisingSectionComponent extends CcuiComponent {
  // ######## UI elements/properties ########

  readonly advertisingSection: Locator = this.page.locator('button[data-testid="rightPanel-continueBtn"]').locator('..');
  readonly continueToHomePageButton: Locator = this.page.locator('button[data-testid="rightPanel-continueBtn"]');
  readonly repeatBookingButton: Locator = this.page.locator('button[data-testid="rightPanel-rebookBtn"]');
  readonly printDetailsButton: Locator = this.advertisingSection.locator('button[data-testid="printBtn"]');
  readonly notifications: Locator = this.advertisingSection.locator('div[data-testid^="notification-"]');
  readonly newsletterSignupTitleLabel: Locator = this.page.getByTestId('newsletter-title');
  readonly newsletterSignupDescriptionLabel: Locator = this.page.getByTestId('newsletter-description');
  readonly newsletterSignupButton: Locator = this.page.getByTestId('newsletter-link');

  /** Return a notification locator by zero-based index. */
  getNotificationByIndex(index: number): Locator { return this.page.getByTestId(`notification-${index + 1}`); }

  /** Return a notification description locator by zero-based index. */
  getNotificationDescriptionByIndex(index: number): Locator { return this.getNotificationByIndex(index).getByTestId('AlertDescription'); }

  /** Return a notification icon locator by zero-based index. */
  getNotificationIconByIndex(index: number): Locator { return this.getNotificationByIndex(index).getByTestId('svg-container'); }

  // ######## UI actions/navigation ########

  /** Click the Repeat Booking button. */
  async clickRepeatBooking(): Promise<void> {
    console.log('Click Repeat Booking button');
    await this.repeatBookingButton.scrollIntoViewIfNeeded();
    await this.repeatBookingButton.click();
  }

  /** Click the Continue to homepage button inside the advertising section. */
  async clickOnContinueToHomepageButton(): Promise<void> {
    console.log('Click Continue to homepage from Advertising section');
    await this.continueToHomePageButton.scrollIntoViewIfNeeded();
    await this.continueToHomePageButton.click();
  }

  /** Find a notification index by its exact displayed text. */
  async findNotificationIndexByText(text: string): Promise<number> {
    console.log(`Find advertising notification by text: ${text}`);
    for (let index = 0; index < await this.notifications.count(); index++) {
      if ((await this.notifications.nth(index).innerText()) === text) return index;
    }
    return -1;
  }

  // ######## UI validations ########

  /** Validate that the Repeat Booking button is displayed with its localized label. */
  async validateRepeatBookingButton(): Promise<void> {
    console.log('Validate Repeat Booking button');
    await expect(this.repeatBookingButton, 'Repeat Booking button').toBeVisible();
    await expect(this.repeatBookingButton, 'Repeat Booking button label').toContainText(await Strings.REPEAT_BOOKING.name);
  }
  /**
   * Validate advertising section display state.
   * @param isDisplayed Whether the advertising section should be displayed.
   */
  async validateAdvertisingSectionIsDisplayedAndHasElementsInsideIt(isDisplayed: boolean): Promise<void> {
    console.log(`Validate if Advertising section is displayed or not. Expected: ${isDisplayed}`);
    await this.validateDisplayState(this.advertisingSection, 'Advertising section', isDisplayed);
    await this.validateDisplayState(this.continueToHomePageButton, 'Advertising section: Continue to homepage button', isDisplayed);
    await this.validateDisplayState(this.repeatBookingButton, 'Repeat booking button', isDisplayed);
  }
  /**
   * Validate notifications are displayed and located inside section.
   * @param isDisplayed Whether an advertising notification should be displayed.
   */
  async validateNotificationsAreDisplayedAndLocatedInsideSection(isDisplayed: boolean): Promise<void> {
    console.log(`Validate if notifications are displayed or not. Expected: ${isDisplayed}`);
    if (isDisplayed) {
      await expect(this.notifications.first(), 'Advertising section notification').toBeVisible();
    } else {
      await expect(this.notifications.first(), 'Advertising section notification').not.toBeVisible();
    }
  }

  /** Validate the first advertising-section element is Continue to homepage. */
  async validateFirstElementIsContinueToHomepage(): Promise<void> {
    console.log('Validate Continue to homepage is first advertising-section element');
    await expect(this.continueToHomePageButton, 'Continue to homepage button').toBeVisible();
  }

  /** Validate the print-details button display state and localized label. */
  async validatePrintDetailsButton(isDisplayed = true): Promise<void> {
    console.log(`Validate Print details button displayed=${isDisplayed}`);
    if (isDisplayed) {
      await expect(this.printDetailsButton, 'Print details button').toBeVisible();
      await expect(this.printDetailsButton, 'Print details button label').toHaveText(await Strings.PRINT_DETAILS.name);
    } else {
      await expect(this.printDetailsButton, 'Print details button').toBeHidden();
    }
  }

  /** Validate the newsletter signup section display state. */
  async validateNewsletterSignupSectionDisplay(isDisplayed: boolean): Promise<void> {
    console.log(`Validate newsletter signup section displayed=${isDisplayed}`);
    for (const [locator, description] of [[this.newsletterSignupTitleLabel, 'Newsletter signup title'], [this.newsletterSignupDescriptionLabel, 'Newsletter signup description'], [this.newsletterSignupButton, 'Newsletter signup button']] as const) {
      if (isDisplayed) await expect(locator, description).toBeVisible();
      else await expect(locator, description).toBeHidden();
    }
  }

  /** Validate the newsletter signup title. */
  async validateNewsletterTitle(expectedTitle: string): Promise<void> {
    console.log('Validate newsletter signup title');
    await expect(this.newsletterSignupTitleLabel, 'Newsletter signup title').toHaveText(expectedTitle);
  }

  /** Validate the newsletter signup description. */
  async validateNewsletterDescription(expectedDescription: string): Promise<void> {
    console.log('Validate newsletter signup description');
    await expect(this.newsletterSignupDescriptionLabel, 'Newsletter signup description').toHaveText(expectedDescription);
  }

  /** Validate the newsletter signup button label. */
  async validateNewsletterSignupButton(expectedLabel: string): Promise<void> {
    console.log('Validate newsletter signup button');
    await expect(this.newsletterSignupButton, 'Newsletter signup button').toHaveText(expectedLabel);
  }

  /** Validate the newsletter signup section against content API data. */
  async validateNewsletterSignupSection(newsletterApi: { introViewTitle: string; introViewText: string; signUpButtonText: string }): Promise<void> {
    console.log('Validate newsletter signup section');
    await this.validateNewsletterTitle(newsletterApi.introViewTitle);
    await this.validateNewsletterDescription(newsletterApi.introViewText);
    await this.validateNewsletterSignupButton(newsletterApi.signUpButtonText);
  }
  /**
   * Validate advertising section and notification display states.
   * @param advertisingSectionDisplay Whether the advertising section should be displayed.
   * @param notificationsDisplay Whether notifications should be displayed.
   */
  async validateData({ advertisingSectionDisplay = true, notificationsDisplay = false }: { advertisingSectionDisplay?: boolean; notificationsDisplay?: boolean } = {}): Promise<void> {
    console.log('Validate Advertising section');
    await this.validateAdvertisingSectionIsDisplayedAndHasElementsInsideIt(advertisingSectionDisplay);
    if (advertisingSectionDisplay) {
      await this.validateNotificationsAreDisplayedAndLocatedInsideSection(notificationsDisplay);
    }
  }
}