import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { CcuiComponent } from '../baseCcui.component';
import { BookersReferenceSectionComponent } from './bookersReferenceSection.component';
import { BusinessAllowancesSectionComponent } from './businessAllowancesSection.component';
import { CardHolderNameSectionComponent } from './cardHolderNameSection.component';

/** Card present status section from the CCUI payment page. */
export class CardPresentStatusSectionComponent extends CcuiComponent {
	// ######## properties ########

	// ######## UI elements/properties ########

	readonly cardPresentStatusContainer: Locator = this.page.locator('div[data-testid="cardStatusSection"]');
	readonly cardPresentStatusTitleLabel: Locator = this.cardPresentStatusContainer.locator('h3[data-testid="cardStatusSection_title-heading"]');
	readonly cardPresentStatusInfo: Locator = this.cardPresentStatusContainer.locator('h6[data-testid="cardStatusSection_info-heading"]');
	readonly cardPresentLabel: Locator = this.cardPresentStatusContainer.locator('p[data-testid="cardStatusSection_text-Card present"]');
	readonly cardPresentInput: Locator = this.cardPresentStatusContainer.locator('div[data-testid="radio-box-wrapper_Card present"] input');
	readonly cardPresentRadioButton: Locator = this.cardPresentStatusContainer.locator('span[data-testid="cardStatusSection-Card present"]');
	readonly cardNotPresentLabel: Locator = this.cardPresentStatusContainer.locator('p[data-testid="cardStatusSection_text-CNP (Card not present)"]');
	readonly cardNotPresentInput: Locator = this.cardPresentStatusContainer.locator('div[data-testid="radio-box-wrapper_CNP (Card not present)"] input');
	readonly cardNotPresentRadioButton: Locator = this.cardPresentStatusContainer.locator('span[data-testid="cardStatusSection-CNP (Card not present)"]');
	readonly paymentPasswordCheckOverlay: Locator = this.page.locator('section[data-testid="passwordCheckOverlay_passwordCheck-ModalContent"]');
	readonly paymentPasswordCheckTitle: Locator = this.page.locator('p[data-testid="passwordCheckOverlay_passwordCheck-ModalTitle"]');
	readonly paymentPasswordCheckDescription: Locator = this.page.locator('[data-testid="passwordCheckOverlay_passwordCheck_description"]');
	readonly paymentPasswordCheckOkButton: Locator = this.page.locator('button[data-testid="passwordCheckOverlay_okButton"]');
	readonly bookersReference = new BookersReferenceSectionComponent();
	readonly businessAllowances = new BusinessAllowancesSectionComponent();
	readonly cardholderName = new CardHolderNameSectionComponent();

	// ######## UI actions/navigation ########

	/** Click the Card Present payment option. */
	async clickCardPresentButton(): Promise<void> { console.log('Click on Card Present option'); await this.cardPresentLabel.scrollIntoViewIfNeeded(); await this.cardPresentLabel.click(); }
	/** Click the Card Not Present payment option. */
	async clickCardNotPresentButton(): Promise<void> { console.log('Click on Card Not Present option'); await this.cardNotPresentRadioButton.scrollIntoViewIfNeeded(); await this.cardNotPresentRadioButton.click(); }
	/**
	 * Select the requested card-present option and acknowledge the password check when required.
	 * @param cardPresentOption Label of the card-present option to select.
	 */
	async clickOnCardPresentOption(cardPresentOption: string): Promise<void> { console.log(`Click card present option ${cardPresentOption}`); if (cardPresentOption.toLowerCase().includes('not')) { await this.clickCardNotPresentButton(); await this.clickOnPasswordCheckOkButton(); return; } await this.clickCardPresentButton(); }
	/** Confirm the password check dialog. */
	async clickOnPasswordCheckOkButton(): Promise<void> { console.log('Click password check OK button'); await this.paymentPasswordCheckOkButton.click(); }
	/**
	 * Validate the business-allowance and booker's-reference fields shown for card-not-present payment.
	 * @param dinnerBudgetValue Expected dinner budget value.
	 * @param isEnabled Whether the dinner budget is enabled.
	 * @param orderNumberValue Expected purchase-order value.
	 * @param companyReferenceValue Expected company-reference value.
	 */
	async validateBusinessAllowancesFields({ dinnerBudgetValue, isEnabled, orderNumberValue, companyReferenceValue }: { dinnerBudgetValue: string; isEnabled: boolean; orderNumberValue: string; companyReferenceValue: string }): Promise<void> {
		console.log('Validate business allowances fields');
		await this.businessAllowances.validateTotalDinnerBudgetFieldIsEnabled(isEnabled);
		await this.bookersReference.validatePurchaseOrderNumberField(orderNumberValue);
		await this.bookersReference.validateCompanyReferenceField(companyReferenceValue);
		await expect(this.businessAllowances.totalDinnerBudgetInput, 'Total dinner budget value').toHaveValue(dinnerBudgetValue);
	}
	/** Validate the vertical placement of the booker's reference section. */
	async validateBookersReferenceAlignment(): Promise<void> {
		console.log('Validate bookers reference alignment');
		const businessBox = await this.businessAllowances.businessAllowancesContainer.boundingBox();
		const bookerBox = await this.bookersReference.bookerReferenceContainer.boundingBox();
		const cardholderBox = await this.cardholderName.cardholderNameSection.boundingBox();
		await expect(bookerBox?.y ?? -1, 'Bookers reference below business allowances').toBeGreaterThan(businessBox?.y ?? -1);
		await expect(cardholderBox?.y ?? -1, 'Card holder section below bookers reference').toBeGreaterThan(bookerBox?.y ?? -1);
	}

	// ######## UI validations ########

	/** Validate the card-present status section and its initial controls and labels. */
	async validateCardPresentStatusContainer(): Promise<void> { console.log('Validate card present status container'); await expect(this.cardPresentStatusContainer, 'Card present status container').toBeVisible(); await this.validateCardPresentStatus(); await this.validateCardNotPresentIsGreyedOut(); await this.validateCardPresentElementLabels(); }
	/** Validate the visible labels in the card-present status section. */
	async validateCardPresentElementLabels(): Promise<void> { console.log('Validate card present section labels'); await expect(this.cardPresentStatusTitleLabel, 'Card Present Status label').toHaveText(await Strings.CARD_PRESENT_STATUS.name); await expect(this.cardPresentStatusInfo, 'Card Present info check').toHaveText(await Strings.CARD_PRESENT_INFO.name); await expect(this.cardPresentLabel, 'Card Present option label').toHaveText(await Strings.CARD_PRESENT.name); await expect(this.cardNotPresentLabel, 'Card Not Present option label').toHaveText(await Strings.CARD_NOT_PRESENT.name); }
	/**
	 * Validate whether the Card Present option is selected.
	 * @param isSelected Whether the Card Present option should be selected.
	 */
	async validateCardPresentStatus(isSelected = true): Promise<void> { console.log(`Validate if Card Present is ${isSelected} or not`); await expect(this.cardPresentInput, 'Card present input has correct type').toHaveAttribute('type', 'radio'); await expect(this.cardNotPresentInput, 'Card not present input has correct type').toHaveAttribute('type', 'radio'); if (isSelected) await expect(this.cardPresentInput, 'Card Present radio selected state').toBeChecked(); else await expect(this.cardPresentInput, 'Card Present radio selected state').not.toBeChecked(); }
	/** Validate that the Card Not Present option is disabled and unselected. */
	async validateCardNotPresentIsGreyedOut(): Promise<void> { console.log('Validate Card Not Present is greyed out'); await expect(this.cardNotPresentInput, 'Card not present input has correct type').toHaveAttribute('type', 'radio'); await expect(this.cardNotPresentInput, 'Card Not Present radio button is not checked').not.toBeChecked(); await expect(this.cardNotPresentRadioButton, 'Card Not present option is grayed-out').toBeDisabled(); }
	/** Validate password-check dialog elements are displayed. */
	async validatePasswordCheckElementsAreDisplayed(): Promise<void> { console.log('Validate password check elements'); await expect(this.paymentPasswordCheckTitle, 'Password check title').toBeVisible(); await expect(this.paymentPasswordCheckDescription, 'Password check description').toBeVisible(); await expect(this.paymentPasswordCheckOkButton, 'Password check OK button').toBeVisible(); }
	/** Validate password-check overlay display state. */
	async validatePasswordCheckOverlayIsDisplayed(isDisplayed = true): Promise<void> { console.log(`Validate password check overlay=${isDisplayed}`); await this.validateDisplayState(this.paymentPasswordCheckOverlay, 'Password check overlay', isDisplayed); }
}