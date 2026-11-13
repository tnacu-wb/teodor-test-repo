import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * Advertising section from the Confirm Booking / Booking Confirmation page containing the UI
 * elements, custom actions and validations. Mirrors qa/reference
 * `components/common/confirmBooking/advertisingSectionBase.js` +
 * `components/opera/confirmBooking/advertisingSection.js`.
 */
export class AdvertisingSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly advertisingSectionContainer: Locator = this.page.locator('button[data-testid="rightPanel-continueBtn"] >> xpath=..');
  readonly continueToHomePageButton: Locator = this.page.locator('button[data-testid="rightPanel-continueBtn"]');
  readonly notificationItemsList: Locator = this.page.locator('div[data-testid^="notification-"]');
  readonly newsletterSignupTitleLabel: Locator = this.page.locator('p[data-testid="newsletter-title"]');
  readonly newsletterSignupDescriptionLabel: Locator = this.page.locator('p[data-testid="newsletter-description"]');
  readonly newsletterSignupButton: Locator = this.page.locator('button[data-testid="newsletter-link"]');
  readonly printDetailsButton: Locator = this.page.locator('button[data-testid="rightPanel-continueBtn"] >> xpath=../button[@data-testid="printBtn"]');
  readonly findOutMoreButton: Locator = this.page.locator('button[data-testid="promo-link"]');
  readonly promotionImage: Locator = this.page.locator('div[data-testid="promo-image"] img');
  readonly promotionTitleLabel: Locator = this.page.locator('p[data-testid="promo-title"]');
  readonly promotionDescriptionLabel: Locator = this.page.locator('p[data-testid="promo-description"]');

  /** Notification item at the given 0-based index. */
  notificationByIndex(index: number): Locator {
    return this.page.locator(`div[data-testid="notification-${index + 1}"]`);
  }

  /** Notification description at the given 0-based index. */
  notificationDescriptionByIndex(index: number): Locator {
    return this.notificationByIndex(index).locator('div[data-testid="AlertDescription"]');
  }

  /** Notification icon at the given 0-based index. */
  notificationIconByIndex(index: number): Locator {
    return this.notificationByIndex(index).locator('div[data-testid="svg-container"]');
  }

  // ######## UI actions/navigation ########

  /** Click Continue to homepage button from the Advertising section. */
  async clickOnContinueToHomepageButton(): Promise<void> {
    console.log('Click on Continue to homepage from Advertising section');
    await this.continueToHomePageButton.scrollIntoViewIfNeeded();
    await this.continueToHomePageButton.click();
  }

  /** Click Newsletter signup button. */
  async clickOnNewsletterSignupButton(): Promise<void> {
    console.log('Click on Newsletter signup button');
    await this.newsletterSignupButton.scrollIntoViewIfNeeded();
    await this.newsletterSignupButton.click();
  }

  /** Click Find out more button under the promotion panel. */
  async clickOnFindOutMoreButton(): Promise<void> {
    console.log('Click on Find out more button');
    await this.findOutMoreButton.scrollIntoViewIfNeeded();
    await this.findOutMoreButton.click();
  }

  /** Find the notification 0-based index for an expected notification message, or -1 if not found. */
  async findNotificationIndexByText(text: string): Promise<number> {
    console.log(`Find index of notification with text: ${text}`);
    const count = await this.notificationItemsList.count();
    for (let index = 0; index < count; index++) {
      if ((await this.notificationItemsList.nth(index).innerText()) === text) {
        return index;
      }
    }
    return -1;
  }

  // ######## UI validations ########

  /** Validate the 'Payment received' notification is displayed. */
  async validatePaymentReceivedNotification(isDisplayed: boolean): Promise<void> {
    console.log(`Validate Payment received notification. Expected ${isDisplayed ? '' : 'not '}to be displayed`);

    const notificationIndex = await this.findNotificationIndexByText(await Strings.PAYMENT_RECEIVED.name);
    if (isDisplayed) {
      expect(notificationIndex, 'Payment received notification was not found').toBeGreaterThan(-1);
      await expect(this.notificationIconByIndex(notificationIndex), 'Payment received notification icon').toBeVisible();
      await expect(this.notificationByIndex(notificationIndex), 'Payment received notification').toBeVisible();
    } else {
      expect(notificationIndex, "Payment received notification is displayed when it shouldn't").toBe(-1);
    }
  }

  /** Validate the Newsletter signup section is displayed. */
  async validateNewsletterSignupSectionDisplay(isDisplayed: boolean): Promise<void> {
    console.log(`Validate if Newsletter section is displayed or not. Expected: ${isDisplayed}`);
    if (isDisplayed) {
      await expect(this.newsletterSignupTitleLabel, 'Newsletter signup title').toBeVisible();
      await expect(this.newsletterSignupDescriptionLabel, 'Newsletter signup description').toBeVisible();
      await expect(this.newsletterSignupButton, 'Newsletter signup button').toBeVisible();
    } else {
      await expect(this.newsletterSignupTitleLabel, 'Newsletter signup title').not.toBeVisible();
      await expect(this.newsletterSignupDescriptionLabel, 'Newsletter signup description').not.toBeVisible();
      await expect(this.newsletterSignupButton, 'Newsletter signup button').not.toBeVisible();
    }
  }

  /** Validate the Newsletter signup title text. */
  async validateNewsletterTitle(expectedTitle: string): Promise<void> {
    console.log('Validate newsletter signup title');
    await this.newsletterSignupTitleLabel.scrollIntoViewIfNeeded();
    await expect(this.newsletterSignupTitleLabel, 'Newsletter signup title').toHaveText(expectedTitle);
  }

  /** Validate the Newsletter signup description text. */
  async validateNewsletterDescription(expectedDescription: string): Promise<void> {
    console.log('Validate newsletter signup description');
    await this.newsletterSignupDescriptionLabel.scrollIntoViewIfNeeded();
    await expect(this.newsletterSignupDescriptionLabel, 'Newsletter signup description').toHaveText(expectedDescription);
  }

  /** Validate the Advertising section (continue-to-homepage + print details) is displayed. */
  async validateAdvertisingSectionDisplay(isDisplayed: boolean): Promise<void> {
    console.log(`Validate if Advertising section is displayed or not. Expected: ${isDisplayed}`);
    if (isDisplayed) {
      await expect(this.advertisingSectionContainer, 'Advertising section').toBeVisible();
      await expect(this.continueToHomePageButton, 'Advertising section: Continue to homepage button').toBeVisible();
      await expect(this.printDetailsButton, 'Advertising section: Print details button').toBeVisible();
    } else {
      await expect(this.advertisingSectionContainer, 'Advertising section').not.toBeVisible();
      await expect(this.continueToHomePageButton, 'Advertising section: Continue to homepage button').not.toBeVisible();
      await expect(this.printDetailsButton, 'Advertising section: Print details button').not.toBeVisible();
    }
  }

  /** Validate the Promotion panel is displayed. */
  async validatePromotionPanelDisplay(isDisplayed = true): Promise<void> {
    console.log(`Validate promotion panel is displayed or not. Expected: ${isDisplayed}`);
    if (isDisplayed) {
      await expect(this.promotionImage, 'Promotion image').toBeVisible();
      await expect(this.promotionTitleLabel, 'Promotion title').toBeVisible();
      await expect(this.promotionDescriptionLabel, 'Promotion description').toBeVisible();
      await expect(this.findOutMoreButton, 'Find out more button').toBeVisible();
    } else {
      await expect(this.promotionImage, 'Promotion image').not.toBeVisible();
      await expect(this.promotionTitleLabel, 'Promotion title').not.toBeVisible();
      await expect(this.promotionDescriptionLabel, 'Promotion description').not.toBeVisible();
      await expect(this.findOutMoreButton, 'Find out more button').not.toBeVisible();
    }
  }
}
