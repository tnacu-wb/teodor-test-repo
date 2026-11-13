import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { CcuiComponent } from '../baseCcui.component';

type CompanyDetails = Record<string, string | number | boolean | undefined>;

/** Company section from the CCUI search console. Mirrors the CCUI reference component. */
export class CompanySectionComponent extends CcuiComponent {
  // ######## UI elements/properties ########

  readonly companyModal: Locator = this.page.locator('section[data-testid="CompanySelection-ModalContent"]');
  readonly companyModalTitleLabel: Locator = this.page.locator('p[data-testid="CompanySelection-ModalTitle"]');
  readonly companyTableCompanyNameLabel: Locator = this.page.locator('th[data-testid="TableHeader-name"]');
  readonly companyTableAddressLabel: Locator = this.page.locator('th[data-testid="TableHeader-address"]');
  readonly companyTableTelLabel: Locator = this.page.locator('th[data-testid="TableHeader-telephoneNumber"]');
  readonly companyTableCorpIdLabel: Locator = this.page.locator('th[data-testid="TableHeader-corpId"]');
  readonly companyTableCompanyIdLabel: Locator = this.page.locator('th[data-testid="TableHeader-companyId"]');
  readonly modalCloseButton: Locator = this.page.locator('button[data-testid="CompanySelection-ModalCloseIcon"]');
  readonly modalCancelButton: Locator = this.page.locator('button[data-testid="CompanySelection-ModalCancelButton"]');
  readonly companyList: Locator = this.page.locator('tr[data-testid*="Table-Row"]');
  readonly companyNameLabel: Locator = this.page.locator('[data-testid*="companyName"], [data-testid*="CompanyName"]').first();
  readonly companyAddressLabel: Locator = this.page.locator('[data-testid*="companyAddress"], [data-testid*="Address"]').first();
  readonly companyTelLabel: Locator = this.page.locator('[data-testid*="companyTel"], [data-testid*="Tel"]').first();
  readonly companyProfileTypeLabel: Locator = this.page.locator('[data-testid*="ProfileType"]').first();
  readonly companyProfileTypeValueLabel: Locator = this.page.locator('[data-testid*="ProfileTypeValue"]').first();
  readonly companyCorpIdLabel: Locator = this.page.locator('[data-testid*="CorpId"]').first();
  readonly companyCorpIdValueLabel: Locator = this.page.locator('[data-testid*="CorpIdValue"], [data-testid*="CorpId"]').last();
  readonly companyIdLabel: Locator = this.page.locator('[data-testid*="CompanyId"]').first();
  readonly companyIdValueLabel: Locator = this.page.locator('[data-testid*="CompanyIdValue"], [data-testid*="CompanyId"]').last();
  readonly companyLanguageLabel: Locator = this.page.locator('[data-testid*="Language"]').first();
  readonly companyLanguageValueLabel: Locator = this.page.locator('[data-testid*="LanguageValue"], [data-testid*="Language"]').last();
  readonly companyActiveLabel: Locator = this.page.locator('[data-testid*="Active"]').first();
  readonly companyActiveCheckbox: Locator = this.page.locator('input[type="checkbox"][name*="active" i], [data-testid*="Active"] input').first();
  readonly companyNegotiatedRatesLabel: Locator = this.page.locator('[data-testid*="NegotiatedRates"]').first();
  readonly companyNegotiatedRatesCheckbox: Locator = this.page.locator('input[type="checkbox"][name*="negotiated" i], [data-testid*="NegotiatedRates"] input').first();
  readonly companyVerifyButton: Locator = this.page.locator('button[data-testid="CompanySelection-ModalVerifyButton"]');

  /**
   * Get company name label by row index.
   * @param companyIndex Zero-based company row index.
   */
  getCompanyNameLabelByIndex(companyIndex: number): Locator { return this.page.locator(`td[data-testid="Table-Cell-${companyIndex}-0"]`); }
  /**
   * Get address label by row index.
   * @param companyIndex Zero-based company row index.
   */
  getAddressLabelByIndex(companyIndex: number): Locator { return this.page.locator(`td[data-testid="Table-Cell-${companyIndex}-1"]`); }
  /**
   * Get telephone label by row index.
   * @param companyIndex Zero-based company row index.
   */
  getTelLabelByIndex(companyIndex: number): Locator { return this.page.locator(`td[data-testid="Table-Cell-${companyIndex}-2"]`); }
  /**
   * Get Corp ID label by row index.
   * @param companyIndex Zero-based company row index.
   */
  getCorpIdLabelByIndex(companyIndex: number): Locator { return this.page.locator(`td[data-testid="Table-Cell-${companyIndex}-3"]`); }
  /**
   * Get company ID label by row index.
   * @param companyIndex Zero-based company row index.
   */
  getCompanyIdLabelByIndex(companyIndex: number): Locator { return this.page.locator(`td[data-testid="Table-Cell-${companyIndex}-4"]`); }
  /**
   * Get a company row index by company name.
   * @param companyName Company name to find.
   */
  async getCompanyIndexByCompanyName(companyName: string): Promise<number> { return this.getCompanyIndexByText(companyName); }
  /**
   * Get a company row index by company ID.
   * @param companyId Company ID to find.
   */
  async getCompanyIndexByCompanyId(companyId: string): Promise<number> { return this.getCompanyIndexByText(companyId); }
  /**
   * Get a company row index by Corp ID.
   * @param corpId Corporate ID to find.
   */
  async getCompanyIndexByCorpId(corpId: string): Promise<number> { return this.getCompanyIndexByText(corpId); }

  /** Find the zero-based company row index containing the supplied text. */
  private async getCompanyIndexByText(text: string): Promise<number> {
    const rows = await this.companyList.all();
    for (const [index, row] of rows.entries()) {
      if ((await row.innerText()).includes(text)) return index;
    }
    return -1;
  }

  // ######## UI actions/navigation ########

  /**
   * Select a company by name.
   * @param companyName Company name to select.
   */
  async selectCompanyByName(companyName: string): Promise<void> { console.log(`Select company by name: ${companyName}`); const companyIndex = await this.getCompanyIndexByCompanyName(companyName); await this.getCompanyNameLabelByIndex(companyIndex).click(); await this.companyVerifyButton.waitFor({ state: 'visible' }); }
  /**
   * Select a company by company ID.
   * @param companyId Company ID to select.
   */
  async selectCompanyByCompanyId(companyId: string): Promise<void> { console.log(`Select company by company ID: ${companyId}`); const companyIndex = await this.getCompanyIndexByCompanyId(companyId); await this.getCompanyIdLabelByIndex(companyIndex).click(); await this.companyVerifyButton.waitFor({ state: 'visible' }); }
  /**
   * Select a company by Corp ID.
   * @param corpId Corporate ID to select.
   */
  async selectCompanyByCorpId(corpId: string): Promise<void> { console.log(`Select company by Corp ID: ${corpId}`); const companyIndex = await this.getCompanyIndexByCorpId(corpId); await this.getCorpIdLabelByIndex(companyIndex).click(); await this.companyVerifyButton.waitFor({ state: 'visible' }); }
  /** Click Verify button. */
  async clickVerifyButton(): Promise<void> { console.log('Click Verify button'); await this.companyVerifyButton.click(); await this.companyModal.waitFor({ state: 'hidden' }); }
  /** Click Cancel from profile. */
  async clickCancelFromProfile(): Promise<void> { console.log('Click Cancel from profile'); await this.modalCancelButton.click(); await expect(this.companyModalTitleLabel, 'Company list modal title after profile cancel').toContainText(await Strings.SELECT_COMPANY_FROM_LIST.name); }
  /** Click Close from profile. */
  async clickCloseFromProfile(): Promise<void> { console.log('Click Close from profile'); await this.modalCloseButton.click(); await expect(this.companyModalTitleLabel, 'Company list modal title after profile close').toContainText(await Strings.SELECT_COMPANY_FROM_LIST.name); }
  /** Click Cancel from modal. */
  async clickCancelFromModal(): Promise<void> { console.log('Click Cancel from modal'); await this.modalCancelButton.click(); await this.companyModal.waitFor({ state: 'hidden' }); }
  /** Click Close from modal. */
  async clickCloseFromModal(): Promise<void> { console.log('Click Close from modal'); await this.modalCloseButton.click(); await this.companyModal.waitFor({ state: 'hidden' }); }

  /**
   * Select a company and validate its profile.
   * @param company Company details expected in the profile.
   * @param companyCorpId Optional corporate ID used to select the company.
   */
  async selectCompanyAndValidateProfile({ company, companyCorpId }: { company: CompanyDetails; companyCorpId?: string }): Promise<void> {
    console.log('Select company and validate profile');
    if (companyCorpId) await this.selectCompanyByCorpId(companyCorpId);
    else if (company.companyName) await this.selectCompanyByName(String(company.companyName));
    await this.validateCompanyProfileDetails(company);
  }

  // ######## UI validations ########

  /** Validate the company-selection modal and title. */
  async validateCompanyModalDetails(): Promise<void> { console.log('Validate company modal details'); await expect(this.companyModal, 'Company modal').toBeVisible(); await expect(this.companyModalTitleLabel, 'Company modal title').toContainText(await Strings.SELECT_COMPANY_FROM_LIST.name); await expect(this.companyTableCompanyNameLabel, 'Company name column').toHaveText(await Strings.ACCOUNT_TO_COMPANY_COMPANY_NAME.name); await expect(this.companyTableAddressLabel, 'Company address column').toHaveText(await Strings.ACCOUNT_TO_COMPANY_COLUMN_ADDRESS.name); await expect(this.companyTableTelLabel, 'Company telephone column').toHaveText(await Strings.ACCOUNT_TO_COMPANY_TEL_NO.name); await expect(this.companyTableCorpIdLabel, 'Company Corp ID column').toHaveText(await Strings.ACCOUNT_TO_COMPANY_CORP_ID.name); await expect(this.companyTableCompanyIdLabel, 'Company ID column').toHaveText(await Strings.ACCOUNT_TO_COMPANY_COMPANY_ID.name); expect(await this.companyList.count(), 'Company result row count').toBeLessThanOrEqual(50); await this.validateCloseAndCancelButtons(); }
  /** Validate company modal close and cancel controls. */
  async validateCloseAndCancelButtons(): Promise<void> { console.log('Validate close and cancel buttons'); await expect(this.modalCloseButton, 'Company modal close button').toBeVisible(); await expect(this.modalCloseButton, 'Company modal close button enabled').toBeEnabled(); await expect(this.modalCancelButton, 'Company modal cancel button').toBeVisible(); await expect(this.modalCancelButton, 'Company modal cancel button label').toContainText(await Strings.ACCOUNT_TO_COMPANY_CANCEL_BUTTON.name); }
  /** Validate that the expected company row is displayed. */
  async validateCompanyRowDetails(company: CompanyDetails): Promise<void> { console.log('Validate company row details'); await expect(this.companyList.filter({ hasText: String(company.companyName ?? company.name ?? '') }).first(), 'Company row details').toBeVisible(); }
  /** Validate the selected company profile details. */
  async validateCompanyProfileDetails(company: CompanyDetails): Promise<void> { console.log('Validate company profile details'); await expect(this.companyModal, 'Company profile modal').toBeVisible(); await expect(this.companyModalTitleLabel, 'Company profile title').toContainText(await Strings.ACCOUNT_TO_COMPANY_COMPANY_PROFILE.name); if (company.companyName ?? company.name) await expect(this.companyNameLabel, 'Company profile name').toContainText(String(company.companyName ?? company.name)); if (company.corpId !== undefined) await expect(this.companyCorpIdValueLabel, 'Company profile Corp ID').toContainText(String(company.corpId)); if (company.companyId !== undefined) await expect(this.companyIdValueLabel, 'Company profile company ID').toContainText(String(company.companyId)); if (company.language) await expect(this.companyLanguageValueLabel, 'Company profile language').toContainText(String(company.language).toUpperCase()); if (company.active !== undefined) await expect(this.companyActiveCheckbox, 'Company active checkbox state').toBeChecked({ checked: Boolean(company.active) }); await expect(this.companyVerifyButton, 'Company verify button').toContainText(await Strings.ACCOUNT_TO_COMPANY_VERIFY_CONFIRM.name); await this.validateCloseAndCancelButtons(); }
  /** Validate that the expected company row is visible after returning to the list. */
  async validateReturnToCompanyList(company: CompanyDetails): Promise<void> { console.log('Validate return to company list'); await expect(this.companyList.filter({ hasText: String(company.companyName ?? company.name ?? '') }).first(), 'Company list row').toBeVisible(); }
}