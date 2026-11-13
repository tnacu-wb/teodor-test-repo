import { type Locator, type Page } from '@playwright/test';

/** Advertising and continue section on the confirmation page. */
export class AdvertisingSectionBaseComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly advertisingSectionContainer: Locator = this.page.locator('button[data-testid="rightPanel-continueBtn"]').locator('..');
  readonly continueToHomePageInsideAdvertisingSectionButton: Locator = this.page.getByTestId('rightPanel-continueBtn');
  readonly notificationItemsList: Locator = this.page.locator('div[data-testid^="notification-"]');
  readonly newsletterSignupTitleLabel: Locator = this.page.getByTestId('newsletter-title');
  readonly newsletterSignupDescriptionLabel: Locator = this.page.getByTestId('newsletter-description');
  readonly newsletterSignupButton: Locator = this.page.getByTestId('newsletter-link');
  readonly printDetailsButton: Locator = this.advertisingSectionContainer.getByTestId('printBtn');

  // ######## UI actions/navigation ########

  // ######## UI validations ########
}