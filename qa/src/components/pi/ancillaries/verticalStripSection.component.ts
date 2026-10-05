import { type Page, type Locator } from '@playwright/test';
import { BookingUpgradeSectionComponent } from './bookingUpgradeSection.component';
import { AncillariesBookingSummarySectionComponent } from './bookingSummarySection.component';

const ID = 'AncillariesPage';

/**
 * The vertical strip on the Ancillaries page containing the UI elements, custom actions and
 * validations. Mirrors qa/reference `components/opera/ancillaries/verticalStripSection.js`
 * (PI-only: the BB-specific `topContinueButton` dual-button branch is dropped).
 */
export class VerticalStripSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly bottomContinueButton: Locator = this.page.locator(`button[data-testid="${ID}-BookingSummary-ContinueButton"]`);
  readonly notificationInfoIconList: Locator = this.page.locator('div[data-testid="Alert"]');
  readonly notificationInfoTitleLabelList: Locator = this.page.locator('div[data-testid="AlertTitle"]');
  readonly notificationInfoDescriptionLabelList: Locator = this.page.locator('div[data-testid="AlertDescription"]');

  // UI components

  readonly bookingUpgradeSection: BookingUpgradeSectionComponent = new BookingUpgradeSectionComponent();
  readonly bookingSummarySection: AncillariesBookingSummarySectionComponent = new AncillariesBookingSummarySectionComponent();

  // ######## UI actions/navigation ########

  /** Scroll the Continue button into view. */
  async scrollToContinueButton(): Promise<void> {
    await this.bottomContinueButton.scrollIntoViewIfNeeded();
  }

  /** Click the Continue button under the vertical booking summary section. */
  async clickContinueButton(): Promise<void> {
    await this.scrollToContinueButton();
    await this.bottomContinueButton.click();
  }
}
