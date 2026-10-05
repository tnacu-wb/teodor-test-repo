import { expect, type Locator } from '@playwright/test';
import { CcuiComponent } from '../baseCcui.component';

/**
 * Account to company details section from the CCUI payment page.
 * Mirrors qa/reference/test/pages/components/ccui/payment/accountToCompanyDetailsSection.js.
 */
export class AccountToCompanyDetailsSectionComponent extends CcuiComponent {
	// ######## properties ########

	// ######## UI elements/properties ########

	readonly accountToCompanyDetailsContainer: Locator = this.page.locator('div[data-testid="accountToCompanyDetails"]');
	readonly accountToCompanyTitleLabel: Locator = this.accountToCompanyDetailsContainer.locator('h3');
	readonly accountToCompanyDetailsDescription: Locator = this.accountToCompanyDetailsContainer.locator('h6');
	readonly accountToCompanyNumberInput: Locator = this.page.locator('input[data-testid="input-accountNumber"]');
	readonly searchAccountNumberButton: Locator = this.page.locator('button[data-testid="search-accountToCompanyDetails"]');
	readonly companyList: Locator = this.page.locator('tr[data-testid*="Table-Row"]');
	readonly companySelectCheckbox: Locator = this.page.locator('td[data-testid="Table-Cell-0-6"] span');
	readonly companyVerifyButton: Locator = this.page.locator('button[data-testid="CompanySelection-ModalVerifyButton"]');
	readonly accountToCompanyDetailsModal: Locator = this.page.locator('section[data-testid="CompanySelection-ModalContent"]');
	readonly modalCloseButton: Locator = this.page.locator('button[data-testid="CompanySelection-ModalCloseIcon"]');
	readonly modalCancelButton: Locator = this.page.locator('button[data-testid="CompanySelection-ModalCancelButton"]');
	readonly accountToCompanyAbandonModal: Locator = this.page.locator('section[data-testid="accountToCompanyOverlay_abandon_booking-ModalContent"]');
	readonly accountToCompanyConfirmAbandonModal: Locator = this.page.locator('section[data-testid="accountToCompanyOverlay_confirm-ModalContent"]');
	readonly companyAddressVerifiedContainer: Locator = this.page.locator('div[data-testid="VerifyCompanyDetails"]');
	readonly companyAddressVerifiedYesRadioButton: Locator = this.page.locator('span[data-testid="VerifyCompanyDetails_verifiedRadio"]');
	readonly companyAddressVerifiedNoRadioButton: Locator = this.page.locator('span[data-testid="VerifyCompanyDetails_notVerifiedRadio"]');
	readonly preAuthorisedChargesContainer: Locator = this.page.locator('div[data-testid^="AccountToCompanyPreAuthorisedCharges-"]');
	readonly preAuthorisedBreakfastLabel: Locator = this.page.locator('label[data-testid="AccountToCompanyPreAuthorisedCharges-option-1-item"]');
	readonly preAuthorisedCarParkingLabel: Locator = this.page.locator('label[data-testid="AccountToCompanyPreAuthorisedCharges-option-2-item"]');
	readonly accountToCompanyNumberInputPlaceholder: Locator = this.accountToCompanyNumberInput;
	readonly accountToCompanyModalTitleLabel: Locator = this.page.locator('p[data-testid="CompanySelection-ModalTitle"]');
	readonly accountToCompanyTableCompanyNameLabel: Locator = this.page.locator('th[data-testid="TableHeader-name"]');
	readonly accountToCompanyTableAddressLabel: Locator = this.page.locator('th[data-testid="TableHeader-address"]');
	readonly accountToCompanyTableTelLabel: Locator = this.page.locator('th[data-testid="TableHeader-telephoneNumber"]');
	readonly accountToCompanyTableCorpIdLabel: Locator = this.page.locator('th[data-testid="TableHeader-corpId"]');
	readonly accountToCompanyTableCompanyIdLabel: Locator = this.page.locator('th[data-testid="TableHeader-companyId"]');
	readonly accountToCompanyTableArNumberLabel: Locator = this.page.locator('th[data-testid="TableHeader-arNumber"]');
	readonly accountToCompanyTableRestrictedLabel: Locator = this.page.locator('th[data-testid="TableHeader-restricted"]');
	readonly accountToCompanyDetailsLabel: Locator = this.page.locator('div[data-testid="accountToCompanyFields-"] h3');
	readonly companyNumber: Locator = this.page.locator('input[data-testid="input-number"]');
	readonly companyName: Locator = this.page.locator('input[data-testid="input-name"]');
	readonly companyAddress: Locator = this.page.locator('input[data-testid="input-address"]');
	readonly companyPostCode: Locator = this.page.locator('input[data-testid="input-postCode"]');
	readonly companyReferenceInput: Locator = this.page.locator('input[data-testid="input-companyReference"]');
	readonly companyChargesNotificationLabel: Locator = this.page.locator('div[data-testid="AccountToCompanyPreAuthorisedCharges"] div[data-testid="AlertDescription"]');
	readonly companyAddressVerifiedLabel: Locator = this.page.locator('h3[data-testid="VerifyCompanyDetails_titleHeader"]');
	readonly companyAddressVerifiedYesRadioInput: Locator = this.page.locator('div[data-testid="radio-box-wrapper_VerifyCompanyDetails_YES"] input');
	readonly companyAddressVerifiedNoRadioInput: Locator = this.page.locator('div[data-testid="radio-box-wrapper_VerifyCompanyDetails_NO"] input');
	readonly companyAddressVerifiedYesLabel: Locator = this.page.locator('p[data-testid="VerifyCompanyDetails_verified"]');
	readonly companyAddressVerifiedNoLabel: Locator = this.page.locator('p[data-testid="VerifyCompanyDetails_notVerified"]');
	readonly preAuthorisedModalTitleLabel: Locator = this.preAuthorisedChargesContainer.locator('h3');
	readonly preAuthorisedNotificationBar: Locator = this.page.locator('div[data-testid="AlertDescription"]');

	/** Return the company-name table cell for a zero-based row index. */
	async getCompanyNameLabelByIndex(companyIndex: number): Promise<Locator> { return this.page.locator(`td[data-testid="Table-Cell-${companyIndex}-0"]`); }
	/** Return the address table cell for a zero-based row index. */
	async getAddressLabelByIndex(companyIndex: number): Promise<Locator> { return this.page.locator(`td[data-testid="Table-Cell-${companyIndex}-1"]`); }
	/** Return the telephone table cell for a zero-based row index. */
	async getTelLabelByIndex(companyIndex: number): Promise<Locator> { return this.page.locator(`td[data-testid="Table-Cell-${companyIndex}-2"]`); }
	/** Return the corporate-id table cell for a zero-based row index. */
	async getCorpIdLabelByIndex(companyIndex: number): Promise<Locator> { return this.page.locator(`td[data-testid="Table-Cell-${companyIndex}-3"]`); }
	/** Return the company-id table cell for a zero-based row index. */
	async getCompanyIdLabelByIndex(companyIndex: number): Promise<Locator> { return this.page.locator(`td[data-testid="Table-Cell-${companyIndex}-4"]`); }
	/** Return the AR-number table cell for a zero-based row index. */
	async getArNumberLabelByIndex(companyIndex: number): Promise<Locator> { return this.page.locator(`td[data-testid="Table-Cell-${companyIndex}-5"]`); }
	/** Return the restricted checkbox for a zero-based row index. */
	async getRestrictedCheckboxByIndex(companyIndex: number): Promise<Locator> { return this.page.locator(`td[data-testid="Table-Cell-${companyIndex}-6"] span`); }
	/** Find the first company row matching a company name. */
	async getCompanyIndexByCompanyName(companyName: string): Promise<number | undefined> { console.log(`Find company index for ${companyName}`); const rows = this.companyList; for (let index = 0; index < await rows.count(); index += 1) if ((await rows.nth(index).innerText()).includes(companyName)) return index; return undefined; }

	// ######## UI actions/navigation ########
	/** Click the account-to-company search button. */
	async clickOnSearchAccountNumberButton(): Promise<void> { console.log("Click on 'Search' account to company button"); await this.searchAccountNumberButton.click(); }
	/** Set the company number input. */
	async setAccountNumberInput(companyNumber: string): Promise<void> { console.log(`Set account number=${companyNumber}`); await this.accountToCompanyNumberInput.fill(companyNumber); }
	/** Search company details by company number. */
	async searchCompanyDetails(companyNumber: string): Promise<void> { console.log('Search company details'); await this.setAccountNumberInput(companyNumber); await this.clickOnSearchAccountNumberButton(); }
	/** Clear the company number input. */
	async clearAccountNumberInput(): Promise<void> { console.log('Clear account number input'); await this.accountToCompanyNumberInput.fill(''); }
	/** Select a company by name and open its verification modal. */
	async selectCompanyByName(companyName: string): Promise<void> { console.log(`Select company by name=${companyName}`); const row = this.companyList.filter({ hasText: companyName }).first(); await row.locator('td[data-testid$="-6"] span, input[type="checkbox"]').click(); await this.companyVerifyButton.click(); }
	/** Fill the company reference field. */
	async setCompanyReferenceInput(companyReference: string): Promise<void> { console.log('Set company reference input'); await this.companyReferenceInput.fill(companyReference); }
	/** Click the other-payment-method option in the abandon overlay. */
	async clickOnOtherMethodButton(): Promise<void> { console.log("Click on 'Other method' button"); await this.page.locator('button[data-testid="accountToCompanyOverlay_retryButton"]').click(); }
	/** Click the abandon option in the abandon overlay. */
	async clickOnAbandonButton(): Promise<void> { console.log("Click on 'Abandon' button"); await this.page.locator('button[data-testid="accountToCompanyOverlay_abandonButton"]').click(); }
	/** Click Retry in the confirm-abandon overlay. */
	async clickOnConfirmRetryButton(): Promise<void> { console.log("Click on confirm 'Retry' button"); await this.page.locator('button[data-testid="accountToCompanyOverlay_confirm_retryButton"]').click(); }
	/** Click Abandon in the confirm-abandon overlay. */
	async clickOnConfirmAbandonButton(): Promise<void> { console.log("Click on confirm 'Abandon' button"); await this.page.locator('button[data-testid="accountToCompanyOverlay_confirm_cancelButton"]').click(); }
	/** Click the verified Yes option. */
	async clickOnCompanyAddressVerifiedYesButton(): Promise<void> { console.log("Click company address verified 'Yes' button"); await this.companyAddressVerifiedYesRadioButton.click(); }
	/** Click the verified No option. */
	async clickOnCompanyAddressVerifiedNoButton(): Promise<void> { console.log("Click company address verified 'No' button"); await this.companyAddressVerifiedNoRadioButton.click(); }

	/** Select pre-authorized breakfast charges. */
	async clickOnPreAuthorizedBreakfastOption(): Promise<void> { console.log("Click on 'Pre-authorized breakfast' option"); await this.preAuthorisedBreakfastLabel.click(); }
	/** Select pre-authorized car-parking charges. */
	async clickOnPreAuthorizedCarParkingOption(): Promise<void> { console.log("Click on 'Pre-authorized car parking' option"); await this.preAuthorisedCarParkingLabel.click(); }
	/** Select both pre-authorized company-charge options. */
	async clickOnPreAuthorizedCompanyCharges(): Promise<void> { await this.clickOnPreAuthorizedBreakfastOption(); await this.clickOnPreAuthorizedCarParkingOption(); }
	/** Click the company verification button. */
	async clickVerifyButton(): Promise<void> { console.log('Click verify button'); await this.companyVerifyButton.click(); }

	// ######## UI validations ########

	/**
	 * Validate whether the Account to Company details section is displayed.
	 * @param isDisplayed Whether the section should be displayed.
	 */
	async validateAccountToCompanyDetailsSectionIsDisplayed(isDisplayed = true): Promise<void> { console.log('Validate Account to Company section is displayed'); await this.validateDisplayState(this.accountToCompanyDetailsContainer, 'Account to Company details section', isDisplayed); }
	/** Validate the company selection modal display state. */
	async validateCompanySelectionModalIsDisplayed(isDisplayed = true): Promise<void> { console.log('Validate company selection modal'); await this.validateDisplayState(this.accountToCompanyDetailsModal, 'Company selection modal', isDisplayed); }
	/** Validate the company verification section display state. */
	async validateCompanyAddressVerifiedSectionIsDisplayed(isDisplayed = true): Promise<void> { console.log('Validate company address verified section'); await this.validateDisplayState(this.companyAddressVerifiedContainer, 'Company address verified section', isDisplayed); }
	/** Validate the pre-authorized charges section display state. */
	async validatePreAuthorisedChargesSectionIsDisplayed(isDisplayed = true): Promise<void> { console.log('Validate pre-authorized charges section'); await this.validateDisplayState(this.preAuthorisedChargesContainer, 'Pre-authorized charges section', isDisplayed); }
	/** Validate Account to Company section labels. */
	async validateAccountToCompanySectionLabels(): Promise<void> { console.log('Validate Account to Company section labels'); await expect(this.accountToCompanyTitleLabel, 'Account to Company title').toBeVisible(); await expect(this.accountToCompanyDetailsDescription, 'Account to Company description').toBeVisible(); }
	/** Validate company verification radio controls and their type. */
	async validateCompanyVerifiedRadioButtons(): Promise<void> { console.log('Validate company verified radio buttons'); await expect(this.companyAddressVerifiedYesRadioInput, 'Company verified yes radio type').toHaveAttribute('type', 'radio'); await expect(this.companyAddressVerifiedNoRadioInput, 'Company verified no radio type').toHaveAttribute('type', 'radio'); }
	/** Validate that neither company-verification option is selected. */
	async validateNoCompanyVerifiedOptionsAreSelected(): Promise<void> { console.log('Validate no company verified options are selected'); await expect(this.companyAddressVerifiedYesRadioInput, 'Company verified yes unselected').not.toBeChecked(); await expect(this.companyAddressVerifiedNoRadioInput, 'Company verified no unselected').not.toBeChecked(); }
	/** Validate the company-reference field value. */
	async validateCompanyReferenceInput(value = ''): Promise<void> { console.log('Validate company reference input'); await expect(this.companyReferenceInput, 'Company reference value').toHaveValue(value); }
	/** Validate the company details modal controls. */
	async validateAccountToCompanyModalDetails(): Promise<void> { console.log('Validate Account to Company modal details'); await expect(this.accountToCompanyDetailsModal, 'Company details modal').toBeVisible(); await expect(this.accountToCompanyModalTitleLabel, 'Company details modal title').toBeVisible(); await expect(this.modalCloseButton, 'Company details modal close button').toBeVisible(); await expect(this.modalCancelButton, 'Company details modal cancel button').toBeVisible(); }
	/** Validate the close and cancel controls. */
	async validateCloseAndCancelButtons(): Promise<void> { console.log('Validate company modal close and cancel buttons'); await expect(this.modalCloseButton, 'Company modal close button').toBeVisible(); await expect(this.modalCancelButton, 'Company modal cancel button').toBeVisible(); }
	/** Validate the company charges notification. */
	async validateCompanyChargesNotification(): Promise<void> { console.log('Validate company charges notification'); await expect(this.companyChargesNotificationLabel, 'Company charges notification').toBeVisible(); }
}