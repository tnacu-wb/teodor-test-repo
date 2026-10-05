import { expect, type Locator } from '@playwright/test';
import { CcuiComponent } from '../baseCcui.component';

/** Booker's reference section from the CCUI payment page. Mirrors the CCUI reference component. */
export class BookersReferenceSectionComponent extends CcuiComponent {
	// ######## properties ########

	// ######## UI elements/properties ########

	readonly bookerReferenceContainer: Locator = this.page.locator('[data-testid*="BookerReference"], [data-testid*="PurchaseOrder"]');
	readonly purchaseOrderNumberInput: Locator = this.bookerReferenceContainer.locator('input').first();
	readonly companyReferenceInput: Locator = this.bookerReferenceContainer.locator('input').nth(1);
	readonly purchaseOrderNumberTitleLabel: Locator = this.bookerReferenceContainer.locator('p[data-testid*="PurchaseOrderNumber"], h3').first();
	readonly purchaseOrderNumberDescriptionLabel: Locator = this.bookerReferenceContainer.locator('p[data-testid*="PurchaseOrderNumber"]').nth(1);
	readonly companyReferenceTitleLabel: Locator = this.bookerReferenceContainer.locator('p[data-testid*="CompanyReference"], h3').last();
	readonly companyReferenceDescriptionLabel: Locator = this.bookerReferenceContainer.locator('p[data-testid*="CompanyReference"]').last();

	// ######## UI actions/navigation ########

	/** Focus the purchase-order number field. */
	async clickPurchaseOrderNumberField(): Promise<void> { console.log('Click purchase order number field'); await this.purchaseOrderNumberInput.click(); }
	/** Focus the company-reference field. */
	async clickCompanyReferenceField(): Promise<void> { console.log('Click company reference field'); await this.companyReferenceInput.click(); }
	/**
	 * Fill the purchase-order number field.
	 * @param value Purchase-order number to enter.
	 */
	async setPurchaseOrderNumberValue(value: string): Promise<void> { console.log('Set purchase order number value'); await this.fillInput(this.purchaseOrderNumberInput, value); }
	/**
	 * Fill the company-reference field.
	 * @param value Company reference to enter.
	 */
	async setCompanyReferenceValue(value: string): Promise<void> { console.log('Set company reference value'); await this.fillInput(this.companyReferenceInput, value); }
	/** Select and focus a booker's reference field by its localized option label. */
	async clickBookerReferenceFieldOptionByLabel(option: string): Promise<void> { if (option === 'Purchase order number') return this.clickPurchaseOrderNumberField(); if (option === 'Company reference') return this.clickCompanyReferenceField(); throw new Error(`Option with value ${option} does not exist`); }
	/** Set a booker's reference value by its localized option label. */
	async setBookerReferenceOptionValue(option: string, value: string): Promise<void> { if (option === 'Purchase order number') return this.setPurchaseOrderNumberValue(value); if (option === 'Company reference') return this.setCompanyReferenceValue(value); throw new Error(`Option with value ${option} does not exist`); }
	/** Click outside the selected booker's reference field. */
	async clickOutsideBookerReferenceFieldOptionByLabel(option: string, value: string): Promise<void> { console.log(`Click outside booker reference field ${option}`); await this.setBookerReferenceOptionValue(option, value); await this.page.keyboard.press('Tab'); }

	// ######## UI validations ########

	/**
	 * Validate whether the booker's reference section is displayed.
	 * @param isDisplayed Whether the section should be displayed.
	 */
	async validateBookerReferenceSectionIsDisplayed(isDisplayed = true): Promise<void> { console.log('Validate Booker reference section is displayed'); await this.validateDisplayState(this.bookerReferenceContainer, 'Booker reference section', isDisplayed); }
	/** Validate the purchase-order field value. */
	async validatePurchaseOrderNumberField(value = ''): Promise<void> { console.log('Validate purchase order number field'); await expect(this.purchaseOrderNumberInput, 'Purchase order number value').toHaveValue(value); }
	/** Validate the company-reference field value. */
	async validateCompanyReferenceField(value = ''): Promise<void> { console.log('Validate company reference field'); await expect(this.companyReferenceInput, 'Company reference value').toHaveValue(value); }
	/** Validate the purchase-order section and value. */
	async validatePurchaseNumberSection(isDisplayed: boolean, orderNumberValue = ''): Promise<void> { console.log('Validate purchase number section'); await this.validateDisplayState(this.purchaseOrderNumberInput, 'Purchase order number field', isDisplayed); if (isDisplayed) await this.validatePurchaseOrderNumberField(orderNumberValue); }
	/** Validate the company-reference section and value. */
	async validateCompanyReferenceSection(isDisplayed: boolean, companyReferenceValue = ''): Promise<void> { console.log('Validate company reference section'); await this.validateDisplayState(this.companyReferenceInput, 'Company reference field', isDisplayed); if (isDisplayed) await this.validateCompanyReferenceField(companyReferenceValue); }
}