import { expect, type Locator } from '@playwright/test';
import { CcuiComponent } from '../baseCcui.component';
import { Strings } from '../../../test-data/strings';

/** Type of caller section from the CCUI payment page. Mirrors the CCUI reference component. */
export class TypeOfCallerSectionComponent extends CcuiComponent {
	// ######## properties ########

	// ######## UI elements/properties ########

	readonly typeOfCallerSection: Locator = this.page.locator('div[data-testid="typeOfCaller"]');
	readonly typeOfCallerTitleLabel: Locator = this.page.locator('h3[data-testid="typeOfCaller_titleHeader"]');
	readonly anyCustomerRadioButton: Locator = this.page.locator('div[data-testid="radio-box-wrapper_typeOfCaller_ANY_CUSTOMER"] input');
	readonly anyCustomerLabel: Locator = this.page.locator('p[data-testid="typeOfCaller_anyCustomer_text"]');
	readonly accessibleCustomerRadioButton: Locator = this.page.locator('div[data-testid="radio-box-wrapper_typeOfCaller_ACCESIBLE_CUSTOMER"] input');
	readonly accessibleCustomerLabel: Locator = this.page.locator('p[data-testid="typeOfCaller_accesibleCustomer_text"]');

	// ######## UI actions/navigation ########

	/** Select the Any customer caller type. */
	async clickTypeOfCallerAnyCustomer(): Promise<void> { console.log('Click type of caller Any customer'); await this.anyCustomerRadioButton.click(); }
	/** Select the Accessible customer caller type. */
	async clickTypeOfCallerAccessibleCustomer(): Promise<void> { console.log('Click type of caller Accessible customer'); await this.accessibleCustomerRadioButton.click(); }
	/**
	 * Select the caller type matching the supplied label.
	 * @param typeOfCaller Caller type label used to choose the corresponding radio button.
	 */
	async clickAndValidateTypeOfCallerSelection(typeOfCaller: string): Promise<void> { console.log(`Click and validate type of caller selection ${typeOfCaller}`); if (typeOfCaller === await Strings.ANY_CUSTOMER.name) { await this.clickTypeOfCallerAnyCustomer(); await expect(this.anyCustomerRadioButton, 'Any customer selection').toBeChecked(); } else if (typeOfCaller === await Strings.ACCESSIBLE_CUSTOMER.name) { await this.clickTypeOfCallerAccessibleCustomer(); await expect(this.accessibleCustomerRadioButton, 'Accessible customer selection').toBeChecked(); } else throw new Error(`Type of caller option with value ${typeOfCaller} is not available`); }

	// ######## UI validations ########

	/**
	 * Validate whether the type-of-caller section is displayed.
	 * @param isDisplayed Whether the section should be displayed.
	 */
	async validateTypeOfCallerIsDisplayed(isDisplayed = true): Promise<void> { console.log('Validate type of caller is displayed'); await this.validateDisplayState(this.typeOfCallerSection, 'Type of caller section', isDisplayed); }
	/** Validate that the type-of-caller section title is visible. */
	async validateTypeOfCallerElementLabels(): Promise<void> { console.log('Validate type of caller element labels'); await expect(this.typeOfCallerTitleLabel, 'Type of caller title').toContainText(await Strings.TYPE_OF_CALLER.name); await expect(this.anyCustomerLabel, 'Any customer label').toContainText(await Strings.ANY_CUSTOMER.name); await expect(this.accessibleCustomerLabel, 'Accessible customer label').toContainText(await Strings.ACCESSIBLE_CUSTOMER.name); }
	/** Validate visibility of the type-of-caller controls. */
	async validateTypeOfCallerElementsAreDisplayed(): Promise<void> { console.log('Validate type of caller elements are displayed'); for (const element of [this.typeOfCallerTitleLabel, this.anyCustomerRadioButton, this.accessibleCustomerRadioButton]) await expect(element, 'Type of caller control').toBeVisible(); }
	/** Validate the default caller selection. */
	async validateTypeOfCallerDefaultSelection(): Promise<void> { console.log('Validate type of caller default selection'); await expect(this.anyCustomerRadioButton, 'Any customer default selection').toBeChecked(); await expect(this.accessibleCustomerRadioButton, 'Accessible customer default selection').not.toBeChecked(); }
	/** Validate type-of-caller visibility, labels, and default selection. */
	async validateTypeOfCallerElements(): Promise<void> { console.log('Validate type of caller elements'); await this.validateTypeOfCallerIsDisplayed(); await this.validateTypeOfCallerElementsAreDisplayed(); await this.validateTypeOfCallerElementLabels(); await this.validateTypeOfCallerDefaultSelection(); }
}