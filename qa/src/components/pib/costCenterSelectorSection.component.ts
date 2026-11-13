import { expect, type Locator, type Page } from "@playwright/test";

/** InnBusiness application > Cost Center Selector section. */
export class CostCenterSelectorSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly costCenterSection: Locator = this.page.locator( '//div[@data-testid="Card-Holder-Section"]/following-sibling::div[4]', );
  readonly assignToCostCenterLabel: Locator = this.costCenterSection .locator(":scope > span") .first();
  readonly costCenterDropdownButton: Locator = this.page.getByTestId( "costCenterOption-IB-Form-Select-Button", );
  readonly costCenterSelectedNameLabel: Locator = this.costCenterDropdownButton.locator("span");
  readonly costCenterDropdownList: Locator = this.page.getByTestId( "costCenterOption-IB-Form-Select-Dropdown", );
  readonly costCenterLabels: Locator = this.costCenterDropdownList.locator("button span");
  readonly assignedCostCenterLabel: Locator = this.costCenterSection .locator(":scope > span") .nth(1);
  /** Get cost center option button by label. */
  getCostCenterOptionButtonByLabel(ccoLabel: string): Locator {
    return this.page.getByTestId(`costCenterOption-${ccoLabel}-Option`);
  }
  // ######## UI actions/navigation ########
  /** Click cost center dropdown button. */
  async clickCostCenterDropdownButton(): Promise<void> {
    console.log("Click cost center dropdown button");
    await this.costCenterDropdownButton.click();
  }
  /** Select cost center by label. */
  async selectCostCenterByLabel(ccoLabel: string): Promise<void> {
    console.log(`Select cost center option with label: ${ccoLabel}`);
    await this.getCostCenterOptionButtonByLabel(ccoLabel).click();
  }
  /** Click cost center dropdown and select cost center by label. */
  async clickToSelectCostCenterOption(ccoLabel: string): Promise<void> {
    console.log("Click to select cost center dropdown option");
    await this.assignToCostCenterLabel.scrollIntoViewIfNeeded();
    await this.clickCostCenterDropdownButton();
    await this.selectCostCenterByLabel(ccoLabel);
  }
  // ######## UI validations ########
  /** Validate cost center selection label. */
  async validateCostCenterSelectionLabel({
    expectedLabel = "",
    hasCostCenterAssigned = false,
  }: {
    expectedLabel?: string | Promise<string>;
    hasCostCenterAssigned?: boolean;
  } = {}): Promise<void> {
    console.log("Validate Cost center selection label");
    await expect( hasCostCenterAssigned ? this.assignedCostCenterLabel : this.costCenterSelectedNameLabel, "Cost center selected name label", ).toHaveText(await expectedLabel);
  }
  /** Validate cost center dropdown list element labels. */
  async validateCostCenterLabels(
    expectedCostCenterLabels: string[] = [],
  ): Promise<void> {
    console.log("Validate Cost center labels");
    await expect(this.costCenterLabels, "Cost center labels count").toHaveCount( expectedCostCenterLabels.length, );
    for (let index = 0; index < expectedCostCenterLabels.length; index += 1)
      await expect( this.costCenterLabels.nth(index), `Cost center option label ${expectedCostCenterLabels[index]}`, ).toContainText(expectedCostCenterLabels[index]);
  }
  /** Validate Cost center selection section. */
  async validateCostCenterSelectionSection({
    isDisplayed = true,
    hasCostCenterAssigned = false,
    expectedLabel = "",
  }: {
    isDisplayed?: boolean;
    hasCostCenterAssigned?: boolean;
    expectedLabel?: string | Promise<string>;
  } = {}): Promise<void> {
    console.log("Validate Cost center selection section");
    if (!isDisplayed) {
      await expect( this.assignToCostCenterLabel, "Assign to cost center label", ).not.toBeVisible();
      return;
    }
    await expect( this.assignToCostCenterLabel, "Assign to cost center label", ).toBeVisible();
    if (!hasCostCenterAssigned)
      await expect( this.costCenterDropdownButton, "Cost center dropdown button", ).toBeVisible();
    await this.validateCostCenterSelectionLabel({
      expectedLabel,
      hasCostCenterAssigned,
    });
  }
}
