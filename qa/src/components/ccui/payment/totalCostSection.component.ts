import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { PriceHelpers } from '../../../utils';
import { CcuiComponent } from '../baseCcui.component';
import { PaymentTypeSectionComponent } from './paymentTypeSection.component';
import { AccountToCompanyDetailsSectionComponent } from './accountToCompanyDetailsSection.component';
import { CardHolderNameSectionComponent } from './cardHolderNameSection.component';
import { TypeOfCallerSectionComponent } from './typeOfCallerSection.component';
import { BookingSummarySectionComponent } from './bookingSummarySection.component';
import type { CardDetails } from '../../../test-data/cards';

/** Total cost section from the CCUI payment page. */
export class TotalCostSectionComponent extends CcuiComponent {
  // ######## properties ########

  // ######## UI elements/properties ########

  readonly totalCostContainer: Locator = this.page.locator('div[data-testid="totalCostSection"]');
  readonly totalCostDiscountContainer: Locator = this.page.locator('[data-testid*="Discount"]');
  readonly totalCostDiscountLabel: Locator = this.page.locator('p[data-testid*="DiscountName"]');
  readonly totalCostDiscountAmountLabel: Locator = this.page.locator('p[data-testid*="DiscountAmount"], s[data-testid*="DiscountPrice"]');
  readonly totalCostPreviousTotalLabel: Locator = this.page.locator('s[data-testid*="PreviousTotalCostName"]');
  readonly totalCostPreviousAmountLabel: Locator = this.page.locator('s[data-testid*="PreviousTotalCostPrice"]');
  readonly totalCostSectionTitleLabel: Locator = this.totalCostContainer.locator('h3, h2').first();
  readonly hotelNameLabel: Locator = this.totalCostContainer.locator('[data-testid*="HotelName"]');
  readonly roomRatePolicyInfoLabel: Locator = this.page.locator('[data-testid*="roomRatePolicies"][data-testid*="Info"]');
  readonly totalCostAmountLabel: Locator = this.totalCostContainer.locator('[data-testid*="Amount"], p').first();
  readonly bookingSummaryTotalCostAmountLabel: Locator = this.page.locator('h2[data-testid="BookingSummary-DesktopVariant-TotalCost-CostAmount"]');
  readonly confirmBookingButton: Locator = this.page.getByTestId('totalCostSection_confirm-booking-total-cost');
  readonly roomRatePolicyCheckBox: Locator = this.page.locator('label[data-testid="roomRatePolicies_checkbox"]');
  readonly roomRatePolicyCheckBoxInput: Locator = this.roomRatePolicyCheckBox.locator('input');
  readonly roomRatePolicyHyperlink: Locator = this.page.locator('u[data-testid="roomRatePolicies_launchButton"]');
  readonly sendEmailConfirmationRadioButtonLabel: Locator = this.page.getByTestId('radio-box-wrapper_SEND_EMAIL').locator('label');
  readonly noEmailConfirmationRadioButtonLabel: Locator = this.page.getByTestId('radio-box-wrapper_NO_SEND_EMAIL').locator('label');
  readonly sendEmailConfirmationLabel: Locator = this.sendEmailConfirmationRadioButtonLabel;
  readonly noEmailConfirmationLabel: Locator = this.noEmailConfirmationRadioButtonLabel;
  readonly sendEmailConfirmationInput: Locator = this.page.getByTestId('radio-box-wrapper_SEND_EMAIL').locator('input');
  readonly noEmailConfirmationInput: Locator = this.page.getByTestId('radio-box-wrapper_NO_SEND_EMAIL').locator('input');
  readonly emailAddressInput: Locator = this.page.locator('input[type="email"], input[data-testid*="email" i]').first();
  readonly roomRatePolicyModal: Locator = this.page.getByTestId('roomRatePolicies-ModalContent');
  readonly closeModalButton: Locator = this.page.locator('[data-testid*="ModalCloseButton"]');
  readonly accountToCompanyDetailsTitleLabel: Locator = this.page.locator('div[data-testid="AccountToCompanyDetails"] p').first();
  readonly accountToCompanyDetailsDescriptionLabel: Locator = this.page.locator('div[data-testid="AccountToCompanyDetails"] p').nth(1);
  readonly accountNumberInput: Locator = this.page.locator('input[data-testid="input-accountNumber"]');
  readonly searchAccountToCompanyButton: Locator = this.page.locator('button[data-testid="AccountToCompanyDetails-Search"], button[data-testid="search-accountToCompanyDetails"]');
  readonly companyNameInput: Locator = this.page.locator('input[data-testid="input-companyName"]');
  readonly companyNumberInput: Locator = this.page.locator('input[data-testid="input-arNumber"]');
  readonly companyAddressInput: Locator = this.page.locator('input[data-testid="input-address"]');
  readonly companyPostCodeInput: Locator = this.page.locator('input[data-testid="input-postalCode"]');
  readonly paymentTypeSection = new PaymentTypeSectionComponent();
  readonly accountToCompanyDetailsSection = new AccountToCompanyDetailsSectionComponent();
  readonly cardHolderName = new CardHolderNameSectionComponent();
  readonly typeOfCaller = new TypeOfCallerSectionComponent();
  readonly bookingSummaryTotalCost = new BookingSummarySectionComponent();

  /** Read the current booking-summary total. */
  async getTotalCostAmount(): Promise<string> {
    console.log('Get total cost amount');
    return String(await PriceHelpers.getPriceAmountFromUiLabel(await this.bookingSummaryTotalCost.getTotalCostValue()));
  }

  /** Read the new total after a discount or amendment. */
  async getNewTotalCostAmount(): Promise<string> {
    console.log('Get new total cost amount');
    return String(await PriceHelpers.getPriceAmountFromUiLabel(await this.bookingSummaryTotalCost.newTotalCostLabel.innerText()));
  }

  /** Read the currency portion of the booking-summary total. */
  async getCurrencyFromBookingSummary(): Promise<string> {
    console.log('Get currency from booking summary');
    const label = await this.bookingSummaryTotalCost.totalPriceValueLabel.innerText();
    return label.includes('£') ? 'GBP' : label.includes('€') ? 'EUR' : label.replace(/[0-9.,\s-]/g, '');
  }

  /** Read the numeric portion of the booking-summary total. */
  async getAmountFromBookingSummary(): Promise<string> {
    console.log('Get amount from booking summary');
    return String(await PriceHelpers.getPriceAmountFromUiLabel(await this.bookingSummaryTotalCost.totalPriceValueLabel.innerText()));
  }

  /** Read the currency from the Total Cost field. */
  async getCurrencyFromTotalCostField(): Promise<string> {
    console.log('Get currency from total cost field');
    const label = await this.totalCostAmountLabel.innerText();
    return label.trim().startsWith('£') ? '£' : label.trim().endsWith('€') ? '€' : label.replace(/[0-9.,\s-]/g, '');
  }

  // ######## UI actions/navigation ########

  /** Click the account-to-company search button. */
  async clickSearchAccountToCompanyButton(): Promise<void> {
    console.log('Click Search Account to Company button');
    await this.page.getByRole('button', { name: /Search|Find/i }).click();
  }

  /** Search for a company using the account-to-company controls. */
  async searchForCompany({ companyName, accountNumber }: { companyName?: string; accountNumber?: string } = {}): Promise<void> {
    console.log(`Search for company by name=${companyName ?? ''} or account number=${accountNumber ?? ''}`);
    if (!companyName && !accountNumber) throw new Error('Neither company name nor account number can be empty');
    if (companyName) await this.companyNameInput.fill(companyName);
    if (accountNumber) await this.accountNumberInput.fill(accountNumber);
    await this.clickSearchAccountToCompanyButton();
    await expect(this.accountToCompanyDetailsSection.accountToCompanyDetailsContainer, 'Account to company details').toBeVisible();
  }

  /** Wait for Total Cost after selecting Account to Company payment. */
  async openTotalCostSectionUsingAccountToCompanyPaymentType({ companyName, accountNumber }: { companyName?: string; accountNumber?: string } = {}): Promise<void> {
    console.log('Open total cost section using Account to Company payment type');
    await this.paymentTypeSection.selectAccountToCompanyRadioButton();
    await this.searchForCompany({ companyName, accountNumber });
    if (companyName) await this.accountToCompanyDetailsSection.selectCompanyByName(companyName);
    await this.accountToCompanyDetailsSection.clickVerifyButton();
    await expect(this.totalCostContainer, 'Total cost container').toBeVisible();
  }

  /** Wait for Total Cost after selecting a non-guaranteed payment. */
  async openTotalCostSectionUsingNonGuaranteedPaymentType(): Promise<void> {
    console.log('Open total cost section using Non Guaranteed payment type');
    await this.paymentTypeSection.selectNonGuaranteedBookingRadioButton();
    await this.typeOfCaller.clickTypeOfCallerAnyCustomer();
    await expect(this.totalCostContainer, 'Total cost container').toBeVisible();
  }

  /**
   * Open Total Cost after selecting a new card payment and completing Eckoh.
   * @param basketReference Basket reference used by the Eckoh webhook flow.
   * @param paymentOption Payment option to select before opening Eckoh.
   * @param card Card data sent to the payment webhook.
   */
  async openTotalCostSectionUsingNewCardPaymentTypeAndEckohIframe({ basketReference, paymentOption = Strings.PAY_ON_ARRIVAL_CCUI.name, card }: { basketReference: string; paymentOption?: string | Promise<string>; card: CardDetails }): Promise<void> {
    console.log('Open total cost section using New Card payment type and Eckoh iframe');
    await global.ccuiPages.paymentCcuiPage.clickOnSelectedPaymentOption(paymentOption);
    await this.paymentTypeSection.selectNewCreditDebitCardRadioButton();
    await global.ccuiPages.paymentCcuiPage.openEckohIframeAndBypassApiWebhook({ basketReference, card });
    await this.cardHolderName.setCardholderFirstNameLastName();
    await expect(this.totalCostContainer, 'Total cost container').toBeVisible();
  }

  /** Close the room-rate policy modal with the standard page escape action. */
  async closeRoomRatePolicyModalIfDisplayed(): Promise<void> {
    console.log('Close room rate policy modal if displayed');
    if (await this.roomRatePolicyModal.isVisible()) await this.closeModalButton.click();
  }

  /** Set the room-rate policy checkbox to the requested state. */
  async checkUncheckRoomRatePolicyCheckboxBasedOnCurrentState(check: boolean): Promise<void> {
    console.log('Check/uncheck room rate policy checkbox');
    const current = await this.roomRatePolicyCheckBoxInput.isChecked();
    if (current !== check) {
      await this.roomRatePolicyCheckBox.click();
    }
  }

  /** Click a supplied email-confirmation option. */
  async clickOnEmailConfirmationOption(optionToChoose: Locator): Promise<void> {
    console.log('Click on email confirmation option');
    await optionToChoose.click();
  }

  /** Select Send email confirmation. */
  async clickOnSendEmailConfirmation(): Promise<void> {
    console.log('Click on send email confirmation');
    await this.sendEmailConfirmationRadioButtonLabel.click();
  }

  /** Select No email confirmation required. */
  async clickOnNoEmailConfirmationRequired(): Promise<void> {
    console.log('Click on no email confirmation required');
    await this.noEmailConfirmationRadioButtonLabel.click();
  }

  /** Submit the booking from Total Cost. */
  async clickOnConfirmBookingButton(): Promise<void> {
    console.log('Click on Confirm booking button');
    await this.confirmBookingButton.click();
  }

  /** Open the room-rate policy modal. */
  async openRoomRatePolicyModalHyperlink(): Promise<void> {
    console.log('Open room rate policy modal hyperlink');
    await this.roomRatePolicyHyperlink.click();
  }

  /** Fill the email address used for confirmation. */
  async setEmailAddressInput(emailAddress: string): Promise<void> {
    console.log('Set email address input');
    await this.fillInput(this.emailAddressInput, emailAddress);
  }

  // ######## UI validations ########

  /** Validate that the Total Cost section is displayed. */
  async validateTotalCostSectionIsDisplayed(): Promise<void> {
    console.log('Validate total cost section is displayed');
    await expect(this.totalCostContainer, 'Total cost section').toBeVisible();
  }

  /** Validate the room-rate policy modal visibility. */
  async validateRoomRatePolicyModalIsDisplayed(isDisplayed = true): Promise<void> {
    console.log(`Validate room rate policy modal is displayed=${isDisplayed}`);
    await this.validateDisplayState(this.roomRatePolicyModal, 'Room rate policy modal', isDisplayed);
  }

  /** Validate whether Confirm booking is enabled. */
  async validateConfirmBookingButtonIsEnabled(isEnabled = true): Promise<void> {
    console.log(`Validate Confirm booking button is enabled=${isEnabled}`);
    await this.validateEnabledState(this.confirmBookingButton, 'Confirm booking button enabled state', isEnabled);
  }

  /** Validate the email-confirmation controls. */
  async validateEmailConfirmationOptions(isSelected = true): Promise<void> {
    console.log('Validate email confirmation options');
    await expect(this.sendEmailConfirmationRadioButtonLabel, 'Send email confirmation option').toBeVisible();
    if (isSelected) {
      await expect(this.sendEmailConfirmationRadioButtonLabel, 'Send email confirmation selected option').toBeVisible();
    }
  }
  /** Validate common Total Cost content and confirmation controls. */
  async validateData({ totalCostAmount, currency, hotelName, defaultChecked }: { totalCostAmount?: string | number; currency?: string; hotelName?: string; defaultChecked?: boolean } = {}): Promise<void> { console.log('Validate total cost section data'); await this.validateTotalCostSectionIsDisplayed(); if (hotelName) await expect(this.hotelNameLabel, 'Total cost hotel name').toContainText(hotelName); if (currency) await expect(this.totalCostAmountLabel, 'Total cost currency').toContainText(currency); if (totalCostAmount !== undefined) await expect(await PriceHelpers.getPriceAmountFromUiLabel(await this.totalCostAmountLabel.innerText()), 'Total cost amount').toBe(Number(totalCostAmount)); if (defaultChecked !== undefined) await this.validateRoomRatePolicyTextAndCheckBox({ defaultChecked }); await expect(this.confirmBookingButton, 'Confirm booking button label').toContainText(await Strings.CONFIRM_BOOKING.name); }
  /** Validate Total Cost when no discount is applied. */
  async validateTotalCostSectionNoDiscount(data: { totalCostAmount?: string | number; currency?: string; hotelName?: string; defaultChecked?: boolean } = {}): Promise<void> { console.log('Validate total cost section without discount'); await expect(this.totalCostSectionTitleLabel, 'Total cost section title').toBeVisible(); await this.validateData(data); }
  /** Validate Total Cost when a discount is applied. */
  async validateTotalCostSectionWithDiscount(data: { discountAmount?: string | number; totalCostAmount?: string | number; currency?: string; hotelName?: string; defaultChecked?: boolean } = {}): Promise<void> { console.log('Validate total cost section with discount'); await expect(this.totalCostDiscountContainer, 'Total cost discount container').toBeVisible(); const { discountAmount = 0, totalCostAmount, currency, hotelName, defaultChecked } = data; await this.validateData({ totalCostAmount: totalCostAmount === undefined ? undefined : Number(totalCostAmount) - Number(discountAmount), currency, hotelName, defaultChecked }); }
  /** Validate room-rate policy text and checkbox state. */
  async validateRoomRatePolicyTextAndCheckBox({ defaultChecked = false }: { defaultChecked?: boolean }): Promise<void> { console.log('Validate room rate policy checkbox'); await expect(this.roomRatePolicyCheckBox, 'Room rate policy checkbox').toBeVisible(); await expect(this.roomRatePolicyCheckBoxInput, 'Room rate policy checkbox type').toHaveAttribute('type', 'checkbox'); await expect(this.roomRatePolicyCheckBoxInput, 'Room rate policy checkbox state').toBeChecked({ checked: defaultChecked }); }
  /** Validate email confirmation controls and default selection. */
  async validateEmailConfirmationElements(): Promise<void> { console.log('Validate email confirmation elements'); await expect(this.sendEmailConfirmationInput, 'Send email confirmation radio type').toHaveAttribute('type', 'radio'); await expect(this.noEmailConfirmationInput, 'No email confirmation radio type').toHaveAttribute('type', 'radio'); await expect(this.sendEmailConfirmationLabel, 'Send email confirmation label').toBeVisible(); await expect(this.noEmailConfirmationLabel, 'No email confirmation label').toBeVisible(); }
  /** Validate no-email confirmation behavior for the selected payment option. */
  async validateEmailConfirmationFunctionality(paymentOption: string): Promise<void> { console.log(`Validate email confirmation functionality for ${paymentOption}`); await this.validateEmailConfirmationElements(); if (paymentOption === await Strings.PAY_NOW_CCUI.name) await this.validateNoEmailRequiredWhenPayNow(); else await this.validateNoEmailRequiredWhenPayOnArrival(); }
  /** Validate no-email confirmation is disabled for Pay Now. */
  async validateNoEmailRequiredWhenPayNow(): Promise<void> { console.log('Validate no email confirmation for Pay Now'); await this.validateEnabledState(this.noEmailConfirmationInput, 'No email confirmation Pay Now state', false); }
  /** Validate no-email confirmation state for Pay on Arrival. */
  async validateNoEmailRequiredWhenPayOnArrival(): Promise<void> { console.log('Validate no email confirmation for Pay on Arrival'); await expect(this.noEmailConfirmationInput, 'No email confirmation Pay on Arrival option').toBeVisible(); }
  /** Validate no-email confirmation is enabled. */
  async validateNoEmailConfirmationIsEnabled(isEnabled = true): Promise<void> { console.log(`Validate no email confirmation enabled=${isEnabled}`); await this.validateEnabledState(this.noEmailConfirmationInput, 'No email confirmation enabled state', isEnabled); }
}