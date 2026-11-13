import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * The booking-upgrade section present in the Ancillaries page vertical strip containing the UI
 * elements, custom actions and validations. Mirrors qa/reference
 * `components/opera/ancillaries/bookingUpgradeSection.js`.
 */
export class BookingUpgradeSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly upgradeButton: Locator = this.page.locator('button.chakra-button', { hasText: 'Upgrade' });
  readonly freelyAmendOrCancelLabel: Locator = this.page.locator('p.chakra-text').filter({ hasText: 'amend' });
  readonly extraPriceLabel: Locator = this.freelyAmendOrCancelLabel.locator('span');

  // ######## UI actions/navigation ########

  /** Click the upgrade button. */
  async clickUpgradeButton(): Promise<void> {
    await this.upgradeButton.click();
  }

  // ######## UI validations ########

  /** Validate the upgrade button and description text/price format. */
  async validateData(): Promise<void> {
    console.log('Validate booking upgrade section from vertical strip');
    await expect(this.upgradeButton, 'Upgrade button clickability').toBeEnabled();
    await expect(this.upgradeButton, 'Upgrade button text').toContainText(await Strings.UPGRADE_TO.name);
    await expect(this.freelyAmendOrCancelLabel, 'Freely amend label text').toContainText(await Strings.FREELY_AMMEND_OR_CANCEL_YOUR_BOOKING_FOR.name);
    const priceText = (await this.extraPriceLabel.innerText()).trim();
    expect(priceText, 'Extra price text format').toMatch(/^[£€]([0-9]*\.?[0-9]+|\.[0-9]+)$/);
  }
}
