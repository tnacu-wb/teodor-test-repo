import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { OurRatesExplainedModalRowComponent } from './ourRatesExplainedModalRow.component';

/**
 * The modal that shows up when the 'Our rates explained' button is actioned, containing the UI
 * elements, custom actions and validations. Mirrors qa/reference
 * `components/opera/hotelDetails/ourRatesExplainedModal.js`.
 */
export class OurRatesExplainedModalComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly modal: Locator = this.page.locator('section[data-testid="ModalContent"]');
  readonly modalTitleLabel: Locator = this.page.locator('div[data-testid="ModalTitle"]');
  readonly closeModalButton: Locator = this.page.locator('button[data-testid="ModalCloseButton"]');
  readonly rateTitlesList: Locator = this.page.locator('[data-testid="ModalBody"] b');

  /** Rate row components for each rate title in the modal body. */
  async rateRowsList(): Promise<OurRatesExplainedModalRowComponent[]> {
    const count = await this.rateTitlesList.count();
    return Array.from({ length: count }, (_, index) => new OurRatesExplainedModalRowComponent(this.rateTitlesList.nth(index)));
  }

  // ######## UI actions/navigation ########

  /** Close the modal via the close button. */
  async closeModal(): Promise<void> {
    await this.closeModalButton.click();
    await expect(this.modal).not.toBeVisible();
  }

  // ######## UI validations ########

  /** Validate the modal title and the rate rows against the given expected titles/descriptions. */
  async validateData({ ratesTitle, ratesDescription }: { ratesTitle: string[]; ratesDescription: string[] }): Promise<void> {
    console.log('Validate modal rates');
    await expect(this.modalTitleLabel, `${await Strings.OUR_RATES.name} is not in the title of modal`).toContainText(await Strings.OUR_RATES.name);

    const rateRows = await this.rateRowsList();
    expect(rateRows.length, 'Both length must be equal').toBe(ratesTitle.length);

    // Simplified vs reference: assumes modal rows are in the same order as ratesTitle/ratesDescription
    // (matching by row title first would require an async Array.find, which isn't supported).
    for (const [index, row] of rateRows.entries()) {
      await row.validateData({ title: ratesTitle[index], description: ratesDescription[index] });
    }
  }
}
