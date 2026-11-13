import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { CcuiComponent } from '../baseCcui.component';

/** Business allowances section from the CCUI payment page. Mirrors the CCUI reference component. */
export class BusinessAllowancesSectionComponent extends CcuiComponent {
	// ######## properties ########

	// ######## UI elements/properties ########

	readonly businessAllowancesContainer: Locator = this.page.locator('div[data-testid="BusinessAllowances-Container"]');
	readonly businessAllowancesTitleLabel: Locator = this.page.locator('p[data-testid="BusinessAllowances-Title"]');
	readonly includeMyDinnerAllowanceCheckBox: Locator = this.page.locator('label[data-testid="BusinessAllowances-ToggleDinnerAllowance"] input');
	readonly includeMyDinnerAllowanceLabel: Locator = this.page.locator('p[data-testid="BusinessAllowances-ToggleDinnerAllowance-Label"]');
	readonly alcoholAllowanceCheckBox: Locator = this.page.locator('label[data-testid="BusinessAllowances-ToggleAlcoholAllowance"] input');
	readonly alcoholAllowanceLabel: Locator = this.page.locator('p[data-testid="BusinessAllowances-ToggleAlcoholAllowance-Label"]');
	readonly mealDealCheckBox: Locator = this.page.locator('label[data-testid="BusinessAllowances-mealDeal_Checkbox"] input');
	readonly mealDealLabel: Locator = this.page.locator('p[data-testid="BusinessAllowances-mealDeal_Label"]');
	readonly premierInnBreakfastCheckBox: Locator = this.page.locator('label[data-testid="BusinessAllowances-premierInnBreakfast_Checkbox"] input');
	readonly premierInnBreakfastLabel: Locator = this.page.locator('p[data-testid="BusinessAllowances-premierInnBreakfast_Label"]');
	readonly continentalBreakfastCheckBox: Locator = this.page.locator('label[data-testid="BusinessAllowances-continentalBreakfast_Checkbox"] input');
	readonly continentalBreakfastLabel: Locator = this.page.locator('p[data-testid="BusinessAllowances-continentalBreakfast_Label"]');
	readonly carParkingCheckBox: Locator = this.page.locator('label[data-testid="BusinessAllowances-carParking_Checkbox"] input');
	readonly carParkingLabel: Locator = this.page.locator('p[data-testid="BusinessAllowances-carParking_Label"]');
	readonly ultimateWifiCheckBox: Locator = this.page.locator('label[data-testid="BusinessAllowances-ultimateWifi_Checkbox"] input');
	readonly ultimateWifiLabel: Locator = this.page.locator('p[data-testid="BusinessAllowances-ultimateWifi_Label"]');
	readonly otherAllowancesTitleLabel: Locator = this.page.locator('p[data-testid="BusinessAllowances-OtherAllowances"]');
	readonly totalDinnerBudgetCurrencyLabel: Locator = this.page.locator('div[data-testid*="BusinessAllowances-budget"]');
	readonly totalDinnerBudgetInput: Locator = this.page.locator('input[data-testid="input-totalDinnerBudgetPersonNight"]');

	/**
	 * Return the business-allowance checkbox associated with a label.
	 * @param label Business-allowance label used to identify the checkbox.
	 */
	async getBusinessAllowanceCheckboxByLabel(label: string | Promise<string>): Promise<Locator> { return this.businessAllowancesContainer.locator('label').filter({ hasText: await label }).locator('input').first(); }

	// ######## UI actions/navigation ########
	/**
	 * Select or clear a business-allowance option.
	 * @param allowanceOption Business-allowance label to select.
	 * @param shouldBeChecked Whether the option should be selected.
	 */
	async selectBusinessAllowanceByLabel(allowanceOption: string | Promise<string>, shouldBeChecked = true): Promise<void> { const label = await allowanceOption; console.log(`Click on Business Allowances option=${label}`); const checkbox = await this.getBusinessAllowanceCheckboxByLabel(label); if ((await checkbox.isChecked()) !== shouldBeChecked) await checkbox.click(); }
	/**
	 * Fill the total dinner-budget field.
	 * @param value Dinner-budget value to enter.
	 */
	async setTotalDinnerBudgetValue(value: string | number): Promise<void> { console.log('Set total dinner budget value'); await this.fillInput(this.totalDinnerBudgetInput, value); }

	// ######## UI validations ########

	/**
	 * Validate whether the business-allowances section is displayed.
	 * @param isDisplayed Whether the section should be displayed.
	 */
	async validateBusinessAllowancesSectionIsDisplayed(isDisplayed = true): Promise<void> { console.log('Validate business allowances section is displayed'); await this.validateDisplayState(this.businessAllowancesContainer, 'Business allowances section', isDisplayed); }
	/** Validate a business allowance checkbox state. */
	async validateBusinessAllowanceIsSelected(label: string | Promise<string>, isSelected: boolean): Promise<void> { const resolvedLabel = await label; console.log(`Validate business allowance=${resolvedLabel}`); await expect(await this.getBusinessAllowanceCheckboxByLabel(resolvedLabel), `${resolvedLabel} selected state`).toBeChecked({ checked: isSelected }); }
	/** Validate the total dinner budget field enabled state. */
	async validateTotalDinnerBudgetFieldIsEnabled(isEnabled = true): Promise<void> { console.log('Validate total dinner budget enabled state'); if (isEnabled) await expect(this.totalDinnerBudgetInput, 'Total dinner budget enabled state').not.toBeDisabled(); else await expect(this.totalDinnerBudgetInput, 'Total dinner budget disabled state').toBeDisabled(); }
	/** Validate the dinner-budget value and placeholder. */
	async validateTotalDinnerBudgetField(value: string, isValid = true): Promise<void> { console.log('Validate total dinner budget field'); await expect(this.totalDinnerBudgetInput, 'Total dinner budget value').toHaveValue(value); await expect(this.totalDinnerBudgetInput, 'Total dinner budget placeholder').toHaveAttribute('placeholder', await Strings.TOTAL_DINNER_BUDGET_CCUI.name); if (!isValid) await expect(this.totalDinnerBudgetInput, 'Invalid dinner budget state').toHaveAttribute('aria-invalid', 'true'); }
	/** Validate the dinner-budget currency attributes. */
	async validateCurrencyIsCorrect(currency: string): Promise<void> { console.log(`Validate business allowance currency ${currency}`); await expect(this.totalDinnerBudgetCurrencyLabel, 'Dinner budget has value').toHaveAttribute('hasValue', 'true'); await expect(this.totalDinnerBudgetCurrencyLabel, 'Dinner budget currency').toHaveAttribute('currency', currency); }
	/** Validate whether the alcohol allowance is enabled. */
	async validateAllowAlcoholIsEnabled(isEnabled: boolean): Promise<void> { console.log(`Validate allow alcohol enabled=${isEnabled}`); await this.validateEnabledState(this.alcoholAllowanceCheckBox, 'Allow alcohol enabled state', isEnabled); }
	/** Validate a specific allowance option and its localized label. */
	async validateBusinessAllowancesOption({ allowanceOption, isSelected = false }: { allowanceOption: string; isSelected?: boolean }): Promise<void> {
		console.log(`Validate business allowance option=${allowanceOption}`);
		const options: Record<string, { checkbox: Locator; label: Locator; text: Promise<string> }> = {
			[await Strings.INCLUDE_MY_DINNER_ALLOWANCE.name]: { checkbox: this.includeMyDinnerAllowanceCheckBox, label: this.includeMyDinnerAllowanceLabel, text: Strings.INCLUDE_MY_DINNER_ALLOWANCE.name },
			[await Strings.ALLOW_ALCOHOL.name]: { checkbox: this.alcoholAllowanceCheckBox, label: this.alcoholAllowanceLabel, text: Strings.ALLOW_ALCOHOL.name },
			[await Strings.MEAL_DEAL_CCUI.name]: { checkbox: this.mealDealCheckBox, label: this.mealDealLabel, text: Strings.MEAL_DEAL_CCUI.name },
			[await Strings.PREMIER_INN_BREAKFAST.name]: { checkbox: this.premierInnBreakfastCheckBox, label: this.premierInnBreakfastLabel, text: Strings.PREMIER_INN_BREAKFAST.name },
			[await Strings.CONTINENTAL_BREAKFAST_ALLOWANCES_CCUI.name]: { checkbox: this.continentalBreakfastCheckBox, label: this.continentalBreakfastLabel, text: Strings.CONTINENTAL_BREAKFAST_ALLOWANCES_CCUI.name },
			[await Strings.CAR_PARKING_CCUI.name]: { checkbox: this.carParkingCheckBox, label: this.carParkingLabel, text: Strings.CAR_PARKING_CCUI.name },
			[await Strings.ULTIMATE_WIFI.name]: { checkbox: this.ultimateWifiCheckBox, label: this.ultimateWifiLabel, text: Strings.ULTIMATE_WIFI.name },
		};
		const option = options[allowanceOption];
		if (!option) throw new Error(`Business allowance option with value ${allowanceOption} is not recognized`);
		await expect(option.checkbox, `${allowanceOption} checkbox`).toBeVisible();
		await expect(option.label, `${allowanceOption} label`).toContainText(await option.text);
		await expect(option.checkbox, `${allowanceOption} selected state`).toBeChecked({ checked: isSelected });
	}
	/** Validate the Include my dinner allowance option. */
	async validateIncludeMyDinnerAllowanceOption(isSelected: boolean): Promise<void> { await this.validateBusinessAllowancesOption({ allowanceOption: await Strings.INCLUDE_MY_DINNER_ALLOWANCE.name, isSelected }); }
	/** Validate the Allow alcohol option. */
	async validateAllowAlcoholAllowanceOption(isSelected: boolean): Promise<void> { await this.validateBusinessAllowancesOption({ allowanceOption: await Strings.ALLOW_ALCOHOL.name, isSelected }); }
	/** Validate the Meal deal option. */
	async validateMealDealAllowanceOption(isSelected: boolean): Promise<void> { await this.validateBusinessAllowancesOption({ allowanceOption: await Strings.MEAL_DEAL_CCUI.name, isSelected }); }
	/** Validate the Premier Inn Breakfast option. */
	async validatePremierInnBreakfastAllowanceOption(isSelected: boolean): Promise<void> { await this.validateBusinessAllowancesOption({ allowanceOption: await Strings.PREMIER_INN_BREAKFAST.name, isSelected }); }
	/** Validate the Continental Breakfast option. */
	async validateContinentalBreakfastAllowanceOption(isSelected: boolean): Promise<void> { await this.validateBusinessAllowancesOption({ allowanceOption: await Strings.CONTINENTAL_BREAKFAST_ALLOWANCES_CCUI.name, isSelected }); }
	/** Validate the Car Parking option. */
	async validateCarParkingAllowanceOption(isSelected: boolean): Promise<void> { await this.validateBusinessAllowancesOption({ allowanceOption: await Strings.CAR_PARKING_CCUI.name, isSelected }); }
	/** Validate the Ultimate Wi-Fi option. */
	async validateUltimateWifiAllowanceOption(isSelected: boolean): Promise<void> { await this.validateBusinessAllowancesOption({ allowanceOption: await Strings.ULTIMATE_WIFI.name, isSelected }); }
}