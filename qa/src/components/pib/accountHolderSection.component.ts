import { expect, type Locator, type Page } from "@playwright/test";

/**
 * InnBusiness application > Home / Spending > InnBusiness Pay section -> Account Holder section
 */
export class AccountHolderSectionComponent {
  private readonly page: Page = global.page;

  // ######## properties ########
  static readonly ACCOUNT_HOLDER_CSS = 'div[data-testid="AccountHolder"]';

  // ######## UI elements/properties ########
  readonly accountNameLabel: Locator = this.page.locator( `${AccountHolderSectionComponent.ACCOUNT_HOLDER_CSS} div`, );
  readonly accountDropdown: Locator = this.page.locator( AccountHolderSectionComponent.ACCOUNT_HOLDER_CSS, );
  readonly accountDropdownImg: Locator = this.page.locator( `${AccountHolderSectionComponent.ACCOUNT_HOLDER_CSS} img`, );
  readonly accountNumberLabel: Locator = this.page.locator( `${AccountHolderSectionComponent.ACCOUNT_HOLDER_CSS} span.mt-2`, );
  readonly accountTypesLabels: Locator = this.page.locator( `${AccountHolderSectionComponent.ACCOUNT_HOLDER_CSS} span.rounded-full`, );
  readonly accountHolderDropdownMenu: Locator = this.page.locator('div[role="menu"]');
  readonly availableAccountsLabels: Locator = this.page.locator( 'a[href*="account="] div', );

  /** Get account holder by account name. */
  getAccountHolderByName(accountName: string): Locator {
    return this.page.locator(`//a//div[contains(text(),'${accountName}')]`);
  }

  /** Get account holder by tethered GUID. */
  getAccountHolderByTetheredGuid(tetheredGuid: string): Locator {
    return this.page.locator(`//a[contains(@href ,"${tetheredGuid}")]/div`);
  }

  // ######## UI actions/navigation ########
  /** Click account holder dropdown. */
  async clickAccountHolderDropdown(): Promise<void> {
    console.log("Click Account holder dropdown");
    await this.accountDropdown.click();
  }

  /** Click outside account holder dropdown menu. */
  async clickOutsideAccountHolderDropdown(): Promise<void> {
    console.log("Click outside Account holder dropdown menu");
    await this.accountHolderDropdownMenu.click({ position: { x: -1, y: -1 } });
  }

  /** Select account holder account by name. */
  async selectAccountHolderByName(accountName: string): Promise<void> {
    console.log(`Select Account holder account by name=${accountName}`);
    await this.getAccountHolderByName(accountName).click();
  }

  /** Select account holder account by tethered GUID. */
  async selectAccountHolderByTetheredGuid(tetheredGuid: string): Promise<void> {
    console.log(
      `Select Account holder account by tetheredGuid=${tetheredGuid}`,
    );
    if (!(await this.accountHolderDropdownMenu.isVisible())) {
      await this.clickAccountHolderDropdown();
    }
    await this.getAccountHolderByTetheredGuid(tetheredGuid).click();
    await expect(this.page, "URL was not updated yet").toHaveURL( new RegExp(tetheredGuid), );
  }

  /** Get account holder number. */
  async getAccountNumber(): Promise<string> {
    console.log("Get Account holder number");
    return this.accountNumberLabel.innerText();
  }

  // ######## UI validations ########
  /** Validate account holder details. */
  async validateAccountHolderDetails(
    accountHolder: {
      accountName: string;
      accountNumber: string;
      registrationRoles: string[];
    },
    hasMultipleAccounts = false,
  ): Promise<void> {
    console.log("Validate Account holder details");
    await expect(this.accountNameLabel, "Account name label").toHaveText( accountHolder.accountName, );
    if (hasMultipleAccounts)
      await expect(this.accountDropdownImg, "Account dropdown").toBeVisible();
    else
      await expect( this.accountDropdownImg, "Account dropdown", ).not.toBeVisible();
    await expect(this.accountNumberLabel, "Account number label").toHaveText( accountHolder.accountNumber.replace(/(.{4})/g, "$1 ").trim(), );
    await expect(this.accountTypesLabels, "Account type labels").toHaveCount( accountHolder.registrationRoles.length, );
  }

  /** Validate account holder dropdown. */
  async validateAccountHolderDropdown({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate Account holder dropdown");
    if (!isDisplayed) {
      await expect( this.accountHolderDropdownMenu, "Dropdown menu", ).not.toBeVisible();
      return;
    }
    await expect(this.accountHolderDropdownMenu, "Dropdown menu").toBeVisible();
    const count = await this.availableAccountsLabels.count();
    for (let index = 0; index < count; index += 1) {
      await expect( this.availableAccountsLabels.nth(index), "Available accounts label", ).toBeVisible();
      await expect( this.availableAccountsLabels.nth(index), "Available account text format", ).toHaveText(/^\(\*\d{4}\) [A-Za-z0-9 ]+$/);
    }
  }
}
