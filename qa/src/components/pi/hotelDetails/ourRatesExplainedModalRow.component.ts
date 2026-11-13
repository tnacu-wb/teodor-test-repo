import { type Locator, expect } from '@playwright/test';

/**
 * One row within the 'Our rates explained' modal containing the UI elements, custom actions and
 * validations. Mirrors qa/reference `components/opera/hotelDetails/ourRatesExplainedModalRow.js`.
 */
export class OurRatesExplainedModalRowComponent {
  readonly rateNameLabel: Locator;
  readonly rateDescriptionLabel: Locator;

  constructor(rateNameLabel: Locator) {
    this.rateNameLabel = rateNameLabel;
    this.rateDescriptionLabel = rateNameLabel.locator('xpath=following-sibling::*[1]');
  }

  // ######## UI validations ########

  /** Validate this row's title and description against the expected values. */
  async validateData({ title, description }: { title: string; description: string }): Promise<void> {
    await this.validateTitleAgainstExpected(title);
    await this.validateDescriptionAgainstExpected(description);
  }

  /** Validate the rate title text. */
  async validateTitleAgainstExpected(title: string): Promise<void> {
    await expect(this.rateNameLabel, 'Rate title').toHaveText(title);
  }

  /** Validate the rate description text. */
  async validateDescriptionAgainstExpected(description: string): Promise<void> {
    await expect(this.rateDescriptionLabel, 'Rate description').toContainText(description);
  }
}
