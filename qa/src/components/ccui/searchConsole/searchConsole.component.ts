import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import type { SearchCriteriaData } from '../../../test-data/searchCriteria';
import { CcuiComponent } from '../baseCcui.component';
import { CompanySectionComponent } from './companySection.component';
import { RoomsPanelCcuiComponent } from './roomsPanel.component';
import { SearchConsoleComponent as PiSearchConsoleComponent } from '../../pi/searchConsole/searchConsole.component';

type SearchCriteria = Record<string, string | number | Date | undefined> & {
  arrivalDate?: string | Date;
  departureDate?: string | Date;
};

/** Search console component from ccui.premierinn.com. Mirrors the CCUI reference component. */
export class SearchConsoleComponent extends CcuiComponent {
  // ######## UI elements/properties ########

  readonly nightsInput: Locator = this.page.locator('//*[@data-testid="search-component"]/div[3]//input');
  readonly nightsErrorMessageLabel: Locator = this.page.locator('[data-testid*="Nights"][data-testid*="Error"], [id*="error"][id*="night" i]');
  readonly promotionCategoryDropdown: Locator = this.page.locator('button[data-testid="DropdownComp-promotion-menuButton"]');
  readonly contractRateCodeInput: Locator = this.page.locator('input[data-testid="contract-rate"]');
  readonly companyNameInput: Locator = this.page.locator('input[data-testid="input-companyName"]');
  readonly checkCompanyButton: Locator = this.page.locator('button[data-testid="CompanyName-CheckCompanyBtn"]');
  readonly searchComponent: Locator = this.page.locator('//div[@data-testid="search-component"]');
  readonly searchSummaryNumberOfNightsLabel: Locator = this.page.locator('div[data-testid="search-summary-number-of-nights"] p');
  readonly searchSummaryPromotionCategoryFieldLabel: Locator = this.page.locator('div[data-testid="search-summary-promotion-category"]');
  readonly searchSummaryContractRateFieldLabel: Locator = this.page.locator('div[data-testid="search-summary-contract-rate"]');
  readonly notificationInfoNewDatesDescriptionLabel: Locator = this.page.locator('div[data-testid="search-component"] div[role="alert"] p');
  readonly searchConsoleCompanyNameFieldLabel: Locator = this.page.locator('div[data-testid="search-summary-contract-rate"] p');
  readonly companySection: CompanySectionComponent = new CompanySectionComponent();
  readonly roomsPanel: RoomsPanelCcuiComponent = new RoomsPanelCcuiComponent();
  private readonly sharedSearchConsole: PiSearchConsoleComponent = new PiSearchConsoleComponent();

  /** Read the tooltip error message text exposed by the search console. */
  async getTooltipErrorMessage(): Promise<string> { console.log('Get tooltip error message'); const describedBy = await this.nightsErrorMessageLabel.getAttribute('aria-describedby'); const tooltip = describedBy ? this.page.locator(`#${describedBy}`) : this.nightsErrorMessageLabel; return (await tooltip.textContent())?.trim() ?? ''; }

  // ######## UI actions/navigation ########

  /**
   * Search for a hotel using the shared location, date, and room controls, then open the selected hotel.
   * @param searchCriteria Criteria returned by the availability lookup.
   * @param useSuggestedHotelName Whether the first hotel suggestion should be selected.
   */
  async searchHotels({ searchCriteria, useSuggestedHotelName = false }: { searchCriteria: SearchCriteriaData; useSuggestedHotelName?: boolean }): Promise<void> {
    console.log(`Search CCUI hotel=${searchCriteria.location.name}`);
    await this.sharedSearchConsole.performSearch({ searchCriteria, useSuggestedHotelName });
  }

  /**
   * Set nights value.
   * @param numberOfNights Number of nights to enter.
   */
  async setNightsValue(numberOfNights: number): Promise<void> { console.log(`Set nights value: ${numberOfNights}`); await this.nightsInput.click(); await this.nightsInput.fill(''); await this.fillInput(this.nightsInput, numberOfNights); }
  /**
   * Set company value.
   * @param companyName Company name or identifier to enter.
   */
  async setCompanyValue(companyName: string): Promise<void> { console.log(`Set company value: ${companyName}`); await this.fillInput(this.companyNameInput, companyName, false); }
  /** Click Check Company button. */
  async clickCheckCompanyButton(): Promise<void> { console.log('Click Check Company button'); await this.checkCompanyButton.click(); }
  /**
   * Search for company by company name.
   * @param companyName Company name to search for.
   */
  async searchForCompany({ companyName }: { companyName: string }): Promise<void> { console.log(`Search for company: ${companyName}`); await this.setCompanyValue(companyName); await this.clickCheckCompanyButton(); await this.companySection.companyModal.waitFor({ state: 'visible' }); }
  /**
   * Check company by Corp ID.
   * @param companyCorpId Corporate ID to search for.
   */
  async checkCompanyByCorpId({ companyCorpId }: { companyCorpId: string }): Promise<void> { console.log(`Check company by Corp ID: ${companyCorpId}`); await this.setCompanyValue(companyCorpId); await this.validateCheckCompanyButton(true); await this.clickCheckCompanyButton(); await this.companySection.companyModal.waitFor({ state: 'visible' }); }

  // ######## UI validations ########

  /**
   * Validate number of nights.
   * @param expectedValue Expected nights value.
   * @param isSearchConsoleEnabled Whether the nights input should be enabled.
   */
  /** Validate the nights field value and enabled state. */
  async validateNumberOfNights({ expectedValue, isSearchConsoleEnabled = true }: { expectedValue: string | number; isSearchConsoleEnabled?: boolean }): Promise<void> { console.log('Validate number of nights'); if (isSearchConsoleEnabled) { await expect(this.nightsInput, 'Number of nights input value').toHaveValue(String(expectedValue)); await this.validateEnabledState(this.nightsInput, 'Number of nights input enabled state'); } else { await expect(this.searchSummaryNumberOfNightsLabel, 'Search summary number of nights').toContainText(String(expectedValue)); } }
  /**
   * Validate tooltip error message.
   * @param numberOfDays Number of days expected in the error message.
   */
  /** Validate the nights tooltip error message. */
  async validateTooltipErrorMessage(numberOfDays: number): Promise<void> { console.log('Validate tooltip error message'); const expectedMessage = numberOfDays > 364 ? await Strings.NUMBER_OF_NIGHTS_CANNOT.name : await Strings.PLEASE_ENTER_A_VALID_NUMBER_OF_NIGHTS.name; await expect(this.page.locator(`#${await this.nightsErrorMessageLabel.getAttribute('aria-describedby')}`), 'Nights tooltip error message').toHaveText(expectedMessage); }
  /**
   * Validate promotion category dropdown.
   * @param isSearchConsoleEnabled Whether the dropdown should be enabled.
   */
  async validatePromotionCategoryDropdown(isSearchConsoleEnabled = true): Promise<void> { console.log('Validate promotion category dropdown'); if (isSearchConsoleEnabled) { await this.validateDisplayState(this.promotionCategoryDropdown, 'Promotion category dropdown'); await this.validateEnabledState(this.promotionCategoryDropdown, 'Promotion category dropdown enabled state', false); await expect(this.promotionCategoryDropdown, 'Promotion category dropdown label').toContainText(await Strings.PROMOTION_CATEGORY.name); } else { await expect(this.searchSummaryPromotionCategoryFieldLabel, 'Search summary promotion category').toBeVisible(); await expect(this.searchSummaryPromotionCategoryFieldLabel, 'Search summary promotion category label').toHaveText(await Strings.PROMOTION_CATEGORY.name); } }
  /**
   * Validate contract rate code.
   * @param isSearchConsoleEnabled Whether the field should be enabled.
   */
  async validateContractRateCode(isSearchConsoleEnabled = true): Promise<void> { console.log('Validate contract rate code'); if (isSearchConsoleEnabled) { await this.validateDisplayState(this.contractRateCodeInput, 'Contract rate code'); await this.validateEnabledState(this.contractRateCodeInput, 'Contract rate code enabled state', false); await expect(this.contractRateCodeInput, 'Contract rate code placeholder').toHaveAttribute('placeholder', await Strings.CONTRACT_RATE_CODE.name); } else { await expect(this.searchSummaryContractRateFieldLabel, 'Search summary contract rate').toBeVisible(); await expect(this.searchSummaryContractRateFieldLabel, 'Search summary contract rate label').toHaveText(await Strings.CONTRACT_RATE_CODE.name); } }
  /**
   * Validate company name field.
   * @param isSearchConsoleEnabled Whether the field should be enabled.
   */
  async validateCompanyNameField(isSearchConsoleEnabled = true): Promise<void> { console.log('Validate company name field'); if (isSearchConsoleEnabled) { await this.validateDisplayState(this.companyNameInput, 'Company name field'); await this.validateEnabledState(this.companyNameInput, 'Company name field enabled state'); await expect(this.companyNameInput, 'Company name field placeholder').toHaveAttribute('placeholder', await Strings.COMPANY_NAME_SEARCH.name); } else { await expect(this.searchSummaryContractRateFieldLabel, 'Search summary company field').toBeVisible(); await expect(this.searchSummaryContractRateFieldLabel, 'Search summary company field label').toHaveText(`${await Strings.COMPANY_NAME_SEARCH.name} or ${await Strings.COMPANY_ID_CORP_ID_SEARCH.name}`); } }
  /** Validate search console container. */
  /** Validate the search-console container. */
  async validateSearchConsoleContainer(): Promise<void> { console.log('Validate search console container'); await expect(this.searchComponent, 'Search console container').toBeVisible(); }
  /**
   * Validate search console data.
   * @param searchCriteria Search criteria values expected in the console.
   * @param isSearchConsoleEnabled Whether enabled search-console fields should be enabled.
   */
  async validateData({ searchCriteria, isSearchConsoleEnabled }: { searchCriteria: SearchCriteria; isSearchConsoleEnabled?: boolean }): Promise<void> { console.log('Validate Search console data'); await this.validateSearchConsoleContainer(); const nightsValue = searchCriteria.numberOfNights ?? searchCriteria.nights ?? (searchCriteria.arrivalDate !== undefined && searchCriteria.departureDate !== undefined ? Math.round((new Date(searchCriteria.departureDate).getTime() - new Date(searchCriteria.arrivalDate).getTime()) / 86400000) : undefined); if (nightsValue !== undefined) await this.validateNumberOfNights({ expectedValue: nightsValue as string | number, isSearchConsoleEnabled }); }
  /**
   * Validate search console data after invalid nights.
   * @param searchCriteria Search criteria values expected in the console.
   * @param isSearchConsoleEnabled Whether enabled search-console fields should be enabled.
   */
  async validateDataAfterInvalidNights({ searchCriteria, isSearchConsoleEnabled }: { searchCriteria: SearchCriteria; isSearchConsoleEnabled?: boolean }): Promise<void> { console.log('Validate Search console data after invalid nights'); await this.validateData({ searchCriteria, isSearchConsoleEnabled }); await this.validateInvalidNumberOfNights(); }
  /** Validate search component is displayed. */
  /** Validate the search component is displayed. */
  async validateSearchComponentIsDisplayed(): Promise<void> { console.log('Validate search component is displayed'); await expect(this.searchComponent, 'Search component').toBeVisible(); }
  /** Validate location input is displayed. */
  /** Validate the location input is displayed. */
  async validateLocationInputIsDisplayed(): Promise<void> { console.log('Validate location input is displayed'); await expect(this.page.locator('[data-testid*="Location"] input, input[name*="location" i]').first(), 'Location input').toBeVisible(); }
  /** Validate notification for new dates is displayed. */
  /** Validate the new-dates notification is displayed. */
  async validateNotificationNewDatesIsDisplayed(): Promise<void> { console.log('Validate notification for new dates is displayed'); await expect(this.notificationInfoNewDatesDescriptionLabel, 'Notification info new dates description').toBeVisible(); }
  /** Validate invalid number of nights. */
  /** Validate the invalid-nights error state. */
  async validateInvalidNumberOfNights(): Promise<void> { console.log('Validate invalid number of nights'); await expect(this.nightsInput, 'Nights input invalid state').toHaveAttribute('aria-invalid', 'true'); }
  /**
   * Validate number of nights input.
   * @param numberOfNights Expected number of nights.
   */
  /** Validate the nights input value. */
  async validateNumberOfNightsInput(numberOfNights: number): Promise<void> { console.log('Validate number of nights input'); await expect(this.nightsInput, 'Number of nights input').toHaveValue(String(numberOfNights)); }
  /**
   * Validate Check Company button.
   * @param isDisplayed Whether the button should be displayed.
   */
  async validateCheckCompanyButton(isDisplayed: boolean): Promise<void> { console.log('Validate Check Company button'); await this.validateDisplayState(this.checkCompanyButton, 'Check Company button', isDisplayed); }
  /**
   * Validate SRP company name or Corp ID value.
   * @param expectedValue Expected company name or corporate ID.
   */
  /** Validate the SRP company name or Corp ID value. */
  async validateSrpCompanyNameOrCompanyCorpIdValue(expectedValue: string): Promise<void> { console.log('Validate SRP company name or company Corp ID value'); await expect(this.searchConsoleCompanyNameFieldLabel, 'SRP company name or company Corp ID value').toContainText(expectedValue); }
}