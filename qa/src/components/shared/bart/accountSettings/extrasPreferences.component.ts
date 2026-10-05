import { type Page, type Locator, expect } from '@playwright/test';

/**
 * 'Extras preferences' section on the Account Settings page, legacy ("bart") Premier Inn web
 * application. Mirrors qa/reference `pages/bart/components/accountSettings/extrasPreferences.js`.
 */
export class ExtrasPreferencesSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly mealsTitleLabel: Locator = this.page.locator('form[name="extras-preferences-form"] h4').first();
  readonly premierInnBreakfastRadioButton: Locator = this.page.locator('form[name="extras-preferences-form"] input#\\31 1');
  readonly continentalBreakfastRadioButton: Locator = this.page.locator('form[name="extras-preferences-form"] input#\\31 2');
  readonly mealDealRadioButton: Locator = this.page.locator('form[name="extras-preferences-form"] input#\\31 7');
  readonly noMealsRadioButton: Locator = this.page.locator('form[name="extras-preferences-form"] input#\\30');
  readonly invoicingTitleLabel: Locator = this.page.locator('form[name="extras-preferences-form"] h4').nth(1);
  readonly emailInvoiceRadioButton: Locator = this.page.locator('form[name="extras-preferences-form"] input#emailInvoice');
  readonly emailInvoiceDescriptionLabel: Locator = this.page.locator('form[name="extras-preferences-form"] input#emailInvoice + span span');
  readonly checkInInvoiceRadioButton: Locator = this.page.locator('form[name="extras-preferences-form"] input#checkInInvoice');
  readonly checkInInvoiceDescriptionLabel: Locator = this.page.locator('form[name="extras-preferences-form"] input#checkInInvoice + span span');
  readonly saveChangesButton: Locator = this.page.locator('form[name="room-requirements-form"] button').first();
  readonly cancelChangesButton: Locator = this.page.locator('form[name="room-requirements-form"] button').nth(1);

  // ######## UI actions/navigation ########

  /** Select the Premier Inn breakfast meal preference. */
  async selectPremierInnBreakfast(): Promise<void> {
    console.log('Select Premier Inn breakfast preference');
    await this.premierInnBreakfastRadioButton.click();
  }

  /** Click 'Save changes'. */
  async clickSaveChanges(): Promise<void> {
    console.log('Click extras preferences Save changes');
    await this.saveChangesButton.click();
  }

  // ######## UI validations ########

  /** Validate the meals preferences title is displayed. */
  async validateMealsTitleIsDisplayed(): Promise<void> {
    console.log('Validate meals preferences title');
    await expect(this.mealsTitleLabel, 'Meals preferences title').toBeVisible();
  }
}
