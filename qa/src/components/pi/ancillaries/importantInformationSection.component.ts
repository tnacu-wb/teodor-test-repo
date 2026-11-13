import { type Page, type Locator, expect } from '@playwright/test';

const ID = 'AncillariesPage';

/**
 * The Important Information section on the Ancillaries page containing the UI elements, custom
 * actions and validations. Mirrors qa/reference
 * `components/opera/ancillaries/importantInformationSection.js` (simplified: the reference's
 * class-name-parsing `getNumberOfInfoBlocks()` and full AEM-date-range cross-checking are dropped
 * - the count is exposed directly via locator `.count()` instead of scraping a CSS class name).
 */
export class ImportantInformationSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly alertDescriptionLabelsList: Locator = this.page.locator('div[data-testid="AlertDescription"] div');
  readonly infoMessagesWrapper: Locator = this.page.locator(`[data-testid="${ID}-BookingSummary-InfoMessages"]`);

  // ######## UI actions/navigation ########

  /** Get the number of important-information blocks currently displayed. */
  async getNumberOfInfoBlocks(): Promise<number> {
    return this.alertDescriptionLabelsList.count();
  }

  // ######## UI validations ########

  /** Validate at least one important-information block is displayed with non-empty text. */
  async validateInfoBlocksDisplayed(): Promise<void> {
    const count = await this.getNumberOfInfoBlocks();
    expect(count, 'Important information blocks count').toBeGreaterThan(0);
    await expect(this.alertDescriptionLabelsList.first(), 'Important information text').not.toBeEmpty();
  }
}
