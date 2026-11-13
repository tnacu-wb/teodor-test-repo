import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { Strings } from "../../test-data/strings";

/**
 * InnBusiness Account settings Section from Add/Edit employee
 */
export class AccountSettingsSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########
  readonly accountSettingsSectionLabel: Locator = this.page.getByTestId("User-Role-Heading");
  readonly accountStatusLabel: Locator = this.page.getByTestId( "User-Status-Sub-Heading", );
  readonly accountStatusTooltipIcon: Locator = this.page.getByTestId( "User-Status-Info-Tooltip-Icon", );
  readonly accountStatusTooltipLabel: Locator = this.accountStatusTooltipIcon.locator( "xpath=following-sibling::div/div/span[1]", );
  readonly accountStatusDropdownButton: Locator = this.page.locator( '[data-testid="userStatus-IB-Form-Select"] button', );
  readonly accountStatusErrorTooltip: Locator = this.page.getByTestId( "userStatus-Error-Tooltip", );
  readonly accountRoleLabel: Locator = this.page.getByTestId( "User-Role-Sub-Heading", );
  readonly accountRoleTooltipIcon: Locator = this.page.getByTestId( "User-Role-Info-Tooltip-Icon", );
  readonly accountRoleTooltipLabel: Locator = this.page.locator( '[data-testid="User-Role-Info-Tooltip"] > span > div', );
  readonly accountRoleErrorTooltipLabel: Locator = this.page.getByTestId( "userRole-Error-Tooltip", );
  readonly accountRoleList: Locator = this.page.locator( '[data-testid="User-Roles-Container-List"] label button', );
  readonly travelManagerRadioButton: Locator = this.accountRoleList.nth(0);
  readonly travelManagerRadioLabel: Locator = this.page.locator( '[data-testid="User-Roles-Container-List"] label:nth-of-type(1) > div', );
  readonly bookerRadioButton: Locator = this.accountRoleList.nth(1);
  readonly bookerRadioLabel: Locator = this.page.locator( '[data-testid="User-Roles-Container-List"] label:nth-of-type(2) > div', );
  readonly selfBookerRadioButton: Locator = this.accountRoleList.nth(2);
  readonly selfBookerRadioLabel: Locator = this.page.locator( '[data-testid="User-Roles-Container-List"] label:nth-of-type(3) > div', );
  readonly guestRadioButton: Locator = this.accountRoleList.nth(3);
  readonly guestRadioLabel: Locator = this.page.locator( '[data-testid="User-Roles-Container-List"] label:nth-of-type(4) > div', );
  readonly cardNameTitleLabel: Locator = this.page.getByTestId("Card-Name-Heading");
  readonly manageCardsLink: Locator = this.cardNameTitleLabel.locator("a");
  readonly cardDropdownButton: Locator = this.page.getByTestId( "cardId-IB-Form-Select-Button", );
  readonly cardDropdownLabel: Locator = this.cardDropdownButton.locator("span");
  readonly cardList: Locator = this.page.getByTestId( "cardId-IB-Form-Select-Dropdown", );
  readonly cardOption: Locator = this.cardList.locator("button");
  readonly paymentTypeTitleLabel: Locator = this.page.getByTestId( "Payment-Type-Heading", );
  readonly paymentTypeOptionLabel: Locator = this.page.getByTestId( "Payment-Type-Form-Input", );
  readonly paymentNoneOption: Locator = this.page.getByTestId("cardId-1-Option");
  readonly cardOptionsList: Locator = this.page.locator( 'button[data-testid*="cardId-"][data-testid*="-Option"] span div', );

  /** Get card option. */
  getCardOption(index: number): Locator {
    return this.page
      .locator(`button[data-testid*="cardId-"][data-testid*="-Option"]`)
      .nth(index + 1)
      .locator("span div");
  }
  /** Get account status option. */
  getAccountStatusOption(accountStatus: string): Locator {
    return this.page.getByTestId(
      `userStatus-${accountStatus.toUpperCase()}-Option`,
    );
  }

  // ######## UI actions/navigation ########
  /** Get the selected card label. */
  async getSelectedCardLabel(): Promise<string> {
    console.log("Get the selected card label");
    return (await this.cardDropdownLabel.textContent()) ?? "";
  }
  /** Click account status dropdown button. */
  async clickAccountStatusDropdownButton(): Promise<void> {
    console.log("Click account status dropdown button");
    await this.accountStatusDropdownButton.click();
  }
  /** Set employee access role. */
  async clickUserRoleRadioButton({
    userRole,
  }: {
    userRole: string | Promise<string>;
  }): Promise<void> {
    const role = await userRole;
    console.log(`Click user role: ${role}`);
    if (role === (await Strings.TRAVEL_MANAGER.name))
      await this.travelManagerRadioLabel.click();
    else if (role === (await IbStrings.BOOKER.name))
      await this.bookerRadioLabel.click();
    else if (role === (await IbStrings.SELF_BOOKER.name))
      await this.selfBookerRadioLabel.click();
    else if (role === (await Strings.GUEST.name))
      await this.guestRadioLabel.click();
    else throw new Error(`Access type with value ${role} is not available`);
  }
  /** Click employee account status option. */
  async clickAccountStatusOption({
    accountStatus,
  }: {
    accountStatus: string | Promise<string>;
  }): Promise<void> {
    const status = await accountStatus;
    console.log(`Click user role: ${status}`);
    await this.getAccountStatusOption(
      status === (await IbStrings.ACCOUNT_STATUS_DELETE.name)
        ? "PURGED"
        : status,
    ).click();
  }
  /** Select employee account status. */
  async selectEmployeeAccountStatusOption({
    accountStatus,
  }: {
    accountStatus: string | Promise<string>;
  }): Promise<void> {
    console.log("Select employee account status option");
    await this.clickAccountStatusDropdownButton();
    await this.clickAccountStatusOption({ accountStatus });
  }
  /** Click manage cards link from add/edit employee page. */
  async clickManageCardsLink(): Promise<void> {
    console.log("Click manage cards link");
    await this.manageCardsLink.click();
  }
  /** Click card option None. */
  async clickCardOptionNone(): Promise<void> {
    console.log("Click card option None");
    await this.paymentNoneOption.click();
  }
  /** Click card option by index. */
  async clickCardOption(index = 0): Promise<void> {
    console.log(`Click card option ${index}`);
    await this.cardOption.nth(index).click();
  }
  /** Click card name dropdown from add/edit employee page. */
  async clickEmployeeCardDropdown(): Promise<void> {
    console.log("Click employee card dropdown");
    await this.cardDropdownButton.scrollIntoViewIfNeeded();
    await this.cardDropdownButton.click();
    await expect(this.cardList, "Card dropdown list").toBeVisible();
  }
  /** Click to select a card for an employee from add/edit employee page. */
  async clickCardOptionByLabel(cardLabel: string): Promise<void> {
    console.log(`Click on card option with label ${cardLabel}`);
    const count = await this.cardOptionsList.count();
    for (let index = 0; index < count; index += 1) {
      const card = this.cardOptionsList.nth(index);
      if (
        ((await card.textContent()) ?? "").split("****")[0].trim() === cardLabel
      ) {
        await card.click();
        return;
      }
    }
    throw new Error(`Card Option with label ${cardLabel} not found`);
  }
  /** Select employee card option. */
  async selectEmployeeCard(
    cardLabel: string | Promise<string> = IbStrings.NONE.name,
  ): Promise<void> {
    console.log("Select employee card");
    await this.clickEmployeeCardDropdown();
    const label = await cardLabel;
    if (label === (await IbStrings.NONE.name)) await this.clickCardOptionNone();
    else await this.clickCardOptionByLabel(label);
  }

  // ######## UI validations ########
  /** Validate selected user role. */
  async validateUserRoleSelection(
    userRole: string | Promise<string>,
  ): Promise<void> {
    const role = await userRole;
    console.log(`Validate user role selection: ${role}`);
    const expected =
      role === (await Strings.TRAVEL_MANAGER.name)
        ? this.travelManagerRadioButton
        : role === (await IbStrings.BOOKER.name)
          ? this.bookerRadioButton
          : role === (await IbStrings.SELF_BOOKER.name)
            ? this.selfBookerRadioButton
            : this.guestRadioButton;
    await expect(expected, `Selected user role ${role}`).toHaveAttribute( "aria-checked", "true", );
    await expect( this.accountRoleList.locator('[aria-checked="true"]'), "Only one element should be selected", ).toHaveCount(1);
  }
  /** Validate account role tooltip. */
  async validateAccountRoleTooltip(): Promise<void> {
    console.log("Validate account role tooltip");
    await expect( this.accountRoleTooltipIcon, "Account role tooltip icon", ).toBeVisible();
    await this.accountRoleTooltipIcon.hover();
    await expect( this.accountRoleTooltipIcon, "Account role tooltip is open", ).toHaveAttribute("data-state", "delayed-open");
    await expect( this.accountRoleTooltipLabel, "Manage employees tooltip", ).toHaveText( (await IbStrings.ACCOUNT_ROLE_TOOLTIP.name) .replace(":", ":\n\n") .replace(/\.([A-Z])/g, ".\n$1"), );
    await this.accountRoleTooltipIcon.click();
  }
  /** Validate account status tooltip. */
  async validateAccountStatusTooltip(): Promise<void> {
    console.log("Validate account status tooltip");
    await expect( this.accountStatusTooltipIcon, "Account status tooltip icon", ).toBeVisible();
    await this.accountStatusTooltipIcon.hover();
    await expect( this.accountStatusTooltipIcon, "Account status tooltip is open", ).toHaveAttribute("data-state", "delayed-open");
    await expect( this.accountStatusTooltipLabel, "Account status tooltip text", ).toHaveText( (await IbStrings.EMPLOYEE_STATUS_TOOLTIP.name).replace( /\.([A-Z])/g, ".\n$1", ), );
    await this.accountStatusTooltipIcon.click();
  }
  /** Validate account status error tooltip. */
  async validateAccountStatusErrorTooltip(isDisplayed = false): Promise<void> {
    console.log("Validate account status error tooltip");
    if (isDisplayed) {
      await expect( this.accountStatusErrorTooltip, "Account status error tooltip", ).toBeVisible();
      await expect( this.accountStatusErrorTooltip, "Account status tooltip label", ).toHaveText(await IbStrings.THIS_USER_IS_THE_MAIN_CONTACT.name);
    } else
      await expect( this.accountStatusErrorTooltip, "Account status error tooltip", ).not.toBeVisible();
  }
  /** Validate account role section. */
  async validateAccountRoleSection(): Promise<void> {
    console.log("Validate account role section");
    await expect(this.accountRoleLabel, "Account role title label").toHaveText( `${await IbStrings.ACCOUNT_ROLE.name} *`, );
    await expect( this.travelManagerRadioLabel, "Travel manager label", ).toHaveText(await IbStrings.TRAVEL_MANAGER_ADD.name);
    await expect(this.bookerRadioLabel, "Booker label").toHaveText( await IbStrings.BOOKER.name, );
    await expect(this.selfBookerRadioLabel, "Self-booker label").toHaveText( await IbStrings.SELF_BOOKER.name, );
    await expect(this.guestRadioLabel, "Guest label").toHaveText( await Strings.GUEST.name, );
    await this.validateAccountRoleTooltip();
  }
  /** Validate account status section. */
  async validateAccountStatusSection(employeeDetails: {
    employeeStatus: string;
  }): Promise<void> {
    console.log("Validate account status section");
    await expect(this.accountStatusLabel, "Account status title").toHaveText( await IbStrings.ACCOUNT_STATUS.name, );
    await expect( this.accountStatusDropdownButton, "Account status dropdown", ).toBeVisible();
    await this.validateAccountStatusTooltip();
  }
  /** Validate card section. */
  async validateCardSection(): Promise<void> {
    console.log("Validate card section");
    await expect( this.cardNameTitleLabel, "Card name title label", ).toContainText(await IbStrings.CARD_NAME.name);
    await expect(this.manageCardsLink, "Manage cards label").toHaveText( await IbStrings.MANAGE_CARDS.name, );
    await expect(this.cardDropdownButton, "Card dropdown button").toBeVisible();
    await expect( this.paymentTypeOptionLabel, "Payment type field", ).not.toBeVisible();
  }
  /** Validate card option selection. */
  async validateCardSelection(
    selectedCardLabel?: string | Promise<string>,
  ): Promise<void> {
    console.log("Validate card option selection");
    const label = await selectedCardLabel;
    await expect( this.cardDropdownLabel, "Selected card label in Card Name dropdown field", ).toHaveText(label || (await IbStrings.NONE.name));
  }
  /** Validate dropdown card list against response. */
  async validateDropdownCardOptions(
    companyPaymentCards: Array<{ cardLabel: string; cardNumber: string }>,
  ): Promise<void> {
    console.log("Validate dropdown card options");
    await expect( this.paymentNoneOption, "Company cards option - None", ).toHaveText(await IbStrings.NONE.name);
    for (let index = 0; index < companyPaymentCards.length; index += 1)
      await expect(this.getCardOption(index), "Company cards").toHaveText( `${companyPaymentCards[index].cardLabel} ${companyPaymentCards[index].cardNumber.slice(-8)}`, );
  }
  /** Validate account settings section. */
  async validateAccountSettingsData({
    employeeDetails,
    isEdit = false,
  }: {
    employeeDetails: { employeeStatus: string };
    isEdit?: boolean;
  }): Promise<void> {
    console.log("Validate account settings data");
    await expect( this.accountSettingsSectionLabel, "Account settings title label", ).toHaveText(await IbStrings.ACCOUNT_SETTINGS.name);
    if (isEdit) await this.validateAccountStatusSection(employeeDetails);
    await this.validateAccountRoleSection();
    await this.validateCardSection();
  }
  /** Validate account role error tooltip after editing the last travel manager. */
  async validateAccountRoleErrorTooltip(isDisplayed = false): Promise<void> {
    console.log("Validate account role error tooltip");
    if (isDisplayed) {
      await expect( this.accountRoleErrorTooltipLabel, "Account role error tooltip", ).toBeVisible();
      await expect( this.accountRoleErrorTooltipLabel, "Account role error tooltip label", ).toHaveText(await IbStrings.ACCOUNT_ROLE_ERROR.name);
    } else
      await expect( this.accountRoleErrorTooltipLabel, "Account role error tooltip", ).not.toBeVisible();
  }
}
