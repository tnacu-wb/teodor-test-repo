import { type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * One rate option in the list of rate options containing the UI elements, custom actions and
 * validations. Mirrors qa/reference `components/opera/hotelDetails/rateOption.js` (simplified:
 * relative-position/alignment assertions dropped - no Playwright equivalent to the reference's
 * pixel-distance `UiUtils.validateIsLeftOf`/`validateElementsAreAlignedVertically` helpers).
 */
export class RateOptionComponent {
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  get optionTitleLabel(): Locator {
    return this.container.locator('b[data-testid="hdp_ratePlanName"]');
  }

  get optionDescriptionLabel(): Locator {
    return this.container.locator('p[data-testid="hdp_ratePlanDescription"]');
  }

  get totalPriceLabel(): Locator {
    return this.container.locator('p[data-testid="hdp_rateItemTotalPriceTitle"]');
  }

  get radioButton(): Locator {
    return this.container.locator('span[data-testid="radio-box-inside"]');
  }

  get priceLabel(): Locator {
    return this.container.locator('p[data-testid="hdp_rateItemTotalPrice"]');
  }

  get nrOfRoomsLabel(): Locator {
    return this.container.locator('p[data-testid="hdp_rateItemNrOfRooms"]');
  }

  get nrOfNightsLabel(): Locator {
    return this.container.locator('p[data-testid="hdp_rateItemNrOfNights"]');
  }

  // ######## UI actions/navigation ########

  /** Select this rate option's radio button. */
  async select(): Promise<void> {
    await this.radioButton.scrollIntoViewIfNeeded();
    await this.radioButton.click();
  }

  // ######## UI validations ########

  /** Validate this rate option's title/description/price elements are present and well-formed. */
  async validateData(): Promise<void> {
    console.log('Validate option rate section');
    expect((await this.optionTitleLabel.innerText()).length, 'Option title label').toBeGreaterThan(0);
    expect((await this.optionDescriptionLabel.innerText()).length, 'Option description label').toBeGreaterThan(0);
    expect((await this.nrOfRoomsLabel.innerText()).length, 'Number of rooms label').toBeGreaterThan(0);
    expect((await this.nrOfNightsLabel.innerText()).length, 'Number of nights label').toBeGreaterThan(0);
    await expect(this.totalPriceLabel, `${await Strings.TOTAL_PRICE.name} label`).toContainText(await Strings.TOTAL_PRICE.name);
    await expect(this.radioButton, 'Radio button').toBeEnabled();
  }

  /** Validate the option description against an expected value. */
  async validateOptionDescriptionLabelAgainstExpected(expectedDescription: string): Promise<void> {
    await expect(this.optionDescriptionLabel, 'Option description label').toContainText(expectedDescription);
  }

  /** Validate the option title against an expected value. */
  async validateOptionTitleLabelAgainstExpected(expectedTitle: string): Promise<void> {
    await expect(this.optionTitleLabel, 'Option title label').toHaveText(expectedTitle);
  }

  /** Validate the description mentions terms-and-conditions keywords (pay/cancel/amend). */
  async validateTermsAndConditionsDetails(): Promise<void> {
    console.log('Validate description details');
    const lowerCaseDescription = (await this.optionDescriptionLabel.innerText()).toLowerCase();
    const keywords = [await Strings.PAY.name, await Strings.CANCEL_GENERIC.name, await Strings.AMEND.name].map(keyword => keyword.toLowerCase());
    expect(keywords.some(keyword => lowerCaseDescription.includes(keyword)), 'Description does not contain keywords.').toBe(true);
  }
}
