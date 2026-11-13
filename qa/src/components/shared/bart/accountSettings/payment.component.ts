import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * 'Payment' (saved card) section on the Account Settings page, legacy ("bart") Premier Inn web
 * application. Mirrors qa/reference `pages/bart/components/accountSettings/payment.js`.
 */
export class BartAccountPaymentSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly addPaymentCardTitleLabel: Locator = this.page.locator('form[data-test="payment-card-form"] ~ h4');
  readonly paymentCardsImgList: Locator = this.page.locator('div[data-test="payment-cards"] img');
  readonly cardHolderInput: Locator = this.page.locator('input#cardHolderName');
  readonly cardNumberInput: Locator = this.page.locator('input#cardNumber');
  readonly selectMonthDropdown: Locator = this.page.locator('select#expiryMonth');
  readonly selectYearDropdown: Locator = this.page.locator('select#expiryYear');
  readonly usePersonalAddressRadioButton: Locator = this.page.locator('input#usePersonalAddress');
  readonly useDifferentAddressRadioButton: Locator = this.page.locator('input#useBillingAddress');
  readonly postcodeTitleLabel: Locator = this.page.locator('form[data-test="payment-card-form"] h6');
  readonly profilePostcodeInput: Locator = this.page.locator('form[data-test="payment-card-form"] h6 ~ input').first();
  readonly findNewAddressButton: Locator = this.page.locator('div[data-test="AccountSettings"] button').filter({ hasText: Strings.FIND_NEW_ADDRESS.data.default ?? '' });
  readonly homeAddressRadioButton: Locator = this.page.locator('form[data-test="payment-card-form"] input#homeAddress');
  readonly businessAddressRadioButton: Locator = this.page.locator('form[data-test="payment-card-form"] input#businessAddress');
  readonly newPostcodeInput: Locator = this.page.locator('form[data-test="payment-card-form"] input#line4').locator('xpath following::input[1]');
  readonly selectCountryDropdown: Locator = this.page.locator('form[data-test="payment-card-form"] select#countryCode');
  readonly selectCountryDropdownItemsList: Locator = this.page.locator('form[data-test="payment-card-form"] select#countryCode option');
  readonly saveChangesButton: Locator = this.page.locator('div[data-test="AccountSettings"] button').filter({ hasText: 'Save card' });
  readonly cancelChangesButton: Locator = this.page.locator('div[data-test="AccountSettings"] button').filter({ hasText: 'Cancel changes' });
  readonly addressLineOneInput: Locator = this.page.locator('form[data-test="payment-card-form"] input#line1');
  readonly addressLineTwoInput: Locator = this.page.locator('form[data-test="payment-card-form"] input#line2');
  readonly addressLineThreeInput: Locator = this.page.locator('form[data-test="payment-card-form"] input#line3');
  readonly addressLineFourInput: Locator = this.page.locator('form[data-test="payment-card-form"] input#line4');

  // ######## UI actions/navigation ########

  /** Fill the card holder name, number, and expiry month/year. */
  async fillCardDetails({
    cardHolderName,
    cardNumber,
    expiryMonth,
    expiryYear,
  }: { cardHolderName: string; cardNumber: string; expiryMonth: string; expiryYear: string }): Promise<void> {
    console.log('Fill payment card details');
    await this.cardHolderInput.fill(cardHolderName);
    await this.cardNumberInput.fill(cardNumber);
    await this.selectMonthDropdown.selectOption(expiryMonth);
    await this.selectYearDropdown.selectOption(expiryYear);
  }

  // ######## UI validations ########

  /** Validate the 'Add payment card' title is displayed. */
  async validateAddPaymentCardTitleIsDisplayed(): Promise<void> {
    console.log('Validate add payment card title');
    await expect(this.addPaymentCardTitleLabel, 'Add payment card title').toBeVisible();
  }
}
