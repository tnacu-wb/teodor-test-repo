import { expect, type Locator } from "@playwright/test";
import { AccountHolderSectionComponent } from "../../components/pib";
import { Strings } from "../../test-data/strings";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

interface CostCentre {
  costCentreCode: string;
  costCentreName: string;
}
interface AccountHolder {
  accountName: string;
  accountNumber: string;
  registrationRoles: string[];
}
/** InnBusiness Manage Cost Centres page */
export class CostCentresPage extends BasePibPage {
  readonly url = "manage/cost-centres";
  // ######## UI elements/properties ########
  readonly costCentresTitleLabel: Locator = this.page.locator( '[data-testid="CostCentresPage-container"] h1', );
  readonly costCentreManagementTitleLabel: Locator = this.page.getByTestId( "InnBusinessTab-title", );
  readonly costCentresTooltipIcon: Locator = this.page.getByTestId( "InnBusinessTab-title-icon", );
  readonly costCentresTooltipLabel: Locator = this.page.locator( '//div[@data-testid="InnBusinessTab-InfoTooltip"]/span[1]', );
  readonly costCentreAddNewButton: Locator = this.page.getByTestId( "CostCentresPage-Add-cost-centre-Button", );
  readonly costCentresDataTable: Locator = this.page.getByTestId( "CostCentresPage-Table-DataTableClient", );
  readonly costCentresTableHeader: Locator = this.page.getByTestId( "CostCentresPage-Table-DataTableClient-header", );
  readonly costCentresTableBody: Locator = this.page.locator( 'table[data-testid="CostCentresPage-Table-DataTableClient"] tbody', );
  readonly costCentresCodeHeaderLabel: Locator = this.page.getByTestId( "CostCentresPage-Table-DataTableClient-head-costCentreCode", );
  readonly costCentresNameHeaderLabel: Locator = this.page.getByTestId( "CostCentresPage-Table-DataTableClient-head-costCentreName", );
  readonly costCentresStatusHeaderLabel: Locator = this.page.getByTestId( "CostCentresPage-Table-DataTableClient-head-status", );
  readonly costCentreCodeLabels: Locator = this.page.locator( 'td[data-testid*="CostCentresPage-Table-DataTableClient-row-costCentreCode-"]', );
  readonly costCentreNameLabels: Locator = this.page.locator( 'td[data-testid*="CostCentresPage-Table-DataTableClient-row-costCentreName-"]', );
  readonly costCentreStatusLabels: Locator = this.page.locator( 'td[data-testid*="CostCentresPage-Table-DataTableClient-row-status-"]', );
  readonly costCentreEditButtons: Locator = this.page.locator( 'div[data-testid*="CostCentresPage-Table-row-actions"]', );
  readonly costCentresRows: Locator = this.page.locator( 'tr[data-testid^="CostCentresPage-Table-DataTableClient-row-"]', );
  readonly accountHolderSection = new AccountHolderSectionComponent();
  /** Get cost centre row by index. */
  getCostCentreRowByIndex(
    index: number,
  ): Locator {
    return this.page.getByTestId(
      `CostCentresPage-Table-DataTableClient-row-${index}`,
    );
  }
  /** Get cost centre code cell by row index. */
  getCostCentreCodeCellByIndex(
    index: number,
  ): Locator {
    return this.page.getByTestId(
      `CostCentresPage-Table-DataTableClient-row-costCentreCode-${index}`,
    );
  }
  /** Get cost centre name cell by row index. */
  getCostCentreNameCellByIndex(
    index: number,
  ): Locator {
    return this.page.getByTestId(
      `CostCentresPage-Table-DataTableClient-row-costCentreName-${index}`,
    );
  }
  /** Get cost centre status cell by row index. */
  getCostCentreStatusCellByIndex(
    index: number,
  ): Locator {
    return this.page.getByTestId(
      `CostCentresPage-Table-DataTableClient-row-status-${index}`,
    );
  }
  /** Get cost centre edit button by cost centre code. */
  getCostCentreEditButtonByCode(
    costCentreCode: string,
  ): Locator {
    return this.page.locator(
      `//img[@data-testid="CostCentresPage-Table-row-action-edit-${costCentreCode}-icon"]/parent::button`,
    );
  }
  // ######## UI actions/navigation ########
  /** Open IB Manage Cost Centres page. */
  async open(): Promise<void> {
    console.log("Open IB Manage Cost Centres page");
    await this.openPath(this.url);
    await this.validatePage();
  }
  /** Click cost centre edit button by code. */
  async clickCostCentreEditButtonByCode({
    costCentreCode,
  }: {
    costCentreCode: string;
  }): Promise<void> {
    console.log(`Click Edit action for cost centre code=${costCentreCode}`);
    const button = this.getCostCentreEditButtonByCode(costCentreCode);
    await button.scrollIntoViewIfNeeded();
    await button.click();
  }
  /** Click cost centre edit button by row index. */
  async clickCostCentreEditButtonByIndex({
    index,
  }: {
    index: number;
  }): Promise<void> {
    console.log(`Click Edit action for cost centre row=${index}`);
    const codeCell = this.getCostCentreCodeCellByIndex(index);
    await expect(codeCell, "Cost centre code cell").toBeVisible();
    await this.clickCostCentreEditButtonByCode({
      costCentreCode: await codeCell.innerText(),
    });
  }
  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Cost Centres page");
    await this.validatePageMarker(this.costCentresTitleLabel, "Cost Centres");
  }
  /** Validate cost centre tooltip. */
  async validateCostCentreTooltip(): Promise<void> {
    console.log("Validate cost centre tooltip");
    await expect( this.costCentreManagementTitleLabel, "Cost centre management title", ).toHaveText(await IbStrings.COST_CENTRE_MANAGEMENT.name);
    await expect( this.costCentresTooltipIcon, "Cost centre tooltip icon", ).toBeVisible();
    await this.costCentresTooltipIcon.hover();
    await expect( this.costCentresTooltipIcon, "Cost centre tooltip state", ).toHaveAttribute("data-state", "delayed-open");
    await expect( this.costCentresTooltipLabel, "Cost centre tooltip label", ).toHaveText(await IbStrings.COST_CENTRE_TOOLTIP.name);
    await this.costCentresTooltipIcon.click();
  }
  /** Validate Add new cost centre button. */
  async validateAddNewCostCentreButton(): Promise<void> {
    console.log("Validate Add new cost centre button");
    await expect( this.costCentreAddNewButton, "Add new cost centre button", ).toHaveText(await IbStrings.ADD_A_NEW_COST_CENTRE.name);
  }
  /** Validate cost centres table. */
  async validateCostCentresTable(
    costCentreDetails: CostCentre[],
  ): Promise<void> {
    console.log("Validate cost centres table");
    await expect( this.costCentresDataTable, "Cost centres data table", ).toBeVisible();
    await expect( this.costCentresTableHeader, "Cost centres table header", ).toBeVisible();
    await expect( this.costCentresTableBody, "Cost centres table body", ).toBeVisible();
    await expect(this.costCentresCodeHeaderLabel, "Code header").toHaveText( await IbStrings.COST_CENTRE_CODE.name, );
    await expect(this.costCentresNameHeaderLabel, "Name header").toHaveText( await IbStrings.COST_CENTRE_NAME.name, );
    await expect(this.costCentresStatusHeaderLabel, "Status header").toHaveText( await IbStrings.COST_CENTRE_STATUS.name, );
    await expect( this.costCentreCodeLabels, "Cost centre code labels count", ).toHaveCount(costCentreDetails.length);
    for (const [index, costCentre] of costCentreDetails.entries()) {
      await expect( this.getCostCentreCodeCellByIndex(index), "Cost centre code cell", ).toHaveText(costCentre.costCentreCode);
      await expect( this.getCostCentreNameCellByIndex(index), "Cost centre name cell", ).toHaveText(costCentre.costCentreName);
      await expect( this.getCostCentreStatusCellByIndex(index), "Cost centre status cell", ).toHaveText(await IbStrings.ACTIVE.name);
      await expect( this.getCostCentreEditButtonByCode(costCentre.costCentreCode), "Cost centre edit button", ).toHaveText(await Strings.EDIT.name);
    }
  }
  /** Validate Cost Centres page data. */
  async validateData(
    accountHolder: AccountHolder,
  ): Promise<void> {
    console.log("Validate Cost Centres page data");
    await expect( this.costCentresTitleLabel, "Cost centres page title", ).toHaveText(await IbStrings.MANAGE_COST_CENTRES.name);
    await this.accountHolderSection.validateAccountHolderDetails(accountHolder);
    await this.validateCostCentreTooltip();
    await this.validateAddNewCostCentreButton();
  }
}
