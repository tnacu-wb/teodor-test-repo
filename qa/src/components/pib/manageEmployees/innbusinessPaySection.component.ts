import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";

/**
 * InnBusiness Pay tab section from manage employees page
 */
export class InnBusinessPaySectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########
  readonly ibPayFirstSectionTitleLabel: Locator = this.page.getByTestId( "InnBusinessPayTab-title-text", );
  readonly ibPayFirstSectionDescriptionLabel: Locator = this.page.locator( 'div[data-testid="InnBusinessPayTab-first-description"] p', );
  readonly ibAccountHolderCard: Locator = this.page.locator( 'div[data-testid="AccountHolder-container"] > div', );
  readonly ibSecondSectionTitleLabel: Locator = this.page.getByTestId( "InnBusinessPayTab-second-title", );
  readonly ibCreateCardTitleLabel: Locator = this.page.getByTestId( "InnBusinessPayTab-widget-title", );
  readonly ibCreateCardDescriptionLabel: Locator = this.page.getByTestId( "InnBusinessPayTab-widget-description", );
  readonly ibCreateCardButton: Locator = this.page.getByTestId( "InnBusinessPayTab-create-innbusiness-pay-card", );

  /** Get account holder card. */
  getAccountHolderCard(index: number): Locator {
    return this.page.getByTestId(`AccountHolder-Account-${index + 1}`);
  }
  /** Get account holder name. */
  getAccountHolderName(index: number): Locator {
    return this.page.getByTestId(`AccountHolder-Account-${index + 1}-name`);
  }
  /** Get account holder number. */
  getAccountHolderNumber(index: number): Locator {
    return this.page.getByTestId(`AccountHolder-Account-${index + 1}-code`);
  }
  /** Get account holder badges. */
  getAccountHolderBadge(index: number): Locator {
    return this.page.getByTestId(`AccountHolder-Account-${index + 1}-badge`);
  }
  /** Get account holder manage employees link. */
  getAccountHolderManageEmployeesLink(index: number): Locator {
    return this.page.locator(
      `div[data-testid="AccountHolder-Account-${index + 1}-manage-employees"] button`,
    );
  }

  // ######## UI actions/navigation ########
  /** Click on Manage Employees link from InnBusiness Pay tab. */
  async clickOnAccountHolderManageEmployeesLink({
    accountHolderCardIndex = 0,
  }: { accountHolderCardIndex?: number } = {}): Promise<void> {
    console.log(
      `Click on Manage Employees link from InnBusiness Pay tab for account holder card ${accountHolderCardIndex}`,
    );
    await this.getAccountHolderManageEmployeesLink(
      accountHolderCardIndex,
    ).click();
  }

  // ######## UI validations ########
  /** Validate Manage employee page elements from InnBusiness Pay tab. */
  async validateManageEmployeesIbPayPageElements({
    isAccountHolder = true,
  }: { isAccountHolder?: boolean } = {}): Promise<void> {
    console.log("Validate Manage employee page elements - InnBusiness Pay tab");
    await expect( this.ibPayFirstSectionTitleLabel, "InnBusiness pay title label", ).toHaveText(await IbStrings.ALLOW_EMPLOYEES_TO_MANAGE.name);
    await expect( this.ibPayFirstSectionDescriptionLabel, "InnBusiness pay description label", ).toHaveText(await IbStrings.ALLOW_EMPLOYEES_TO_MANAGE_DESCRIPTION.name);
    if (isAccountHolder)
      await expect( this.ibAccountHolderCard, "Account holder card element", ).toBeVisible();
    await expect( this.ibSecondSectionTitleLabel, "Create InnBusiness pay title label", ).toHaveText( await IbStrings.NEW_EMPLOYEE_WHO_NEEDS_INN_BUSINESS_PAY_CARD.name, );
    await expect( this.ibCreateCardTitleLabel, "Create InnBusiness pay title label", ).toHaveText( await IbStrings.NEW_EMPLOYEE_WHO_NEEDS_INN_BUSINESS_PAY_CARD.name, );
    await expect( this.ibCreateCardDescriptionLabel, "Create InnBusiness pay description label", ).toHaveText(await IbStrings.REQUEST_A_CARD_FOR_THIS_USER.name);
    await expect( this.ibCreateCardButton, "Create InnBusiness pay button label", ).toHaveText(await IbStrings.CREATE_A_INNBUSINESS_PAY_CARD.name);
  }

  /** Validate account holder cards against getAccountList graphql. */
  async validateAccountHolderCard(
    account: Array<{ accountName: string; accountNumber: string }>,
  ): Promise<void> {
    console.log("Validate card holder data");
    for (let index = 0; index < account.length; index += 1) {
      await expect( this.getAccountHolderName(index), "Account holder name", ).toHaveText(account[index].accountName);
      await expect( this.getAccountHolderNumber(index), "Account holder serial number", ).toHaveText( account[index].accountNumber.replace(/(.{4})/g, "$1 ").trim(), );
    }
  }
}
