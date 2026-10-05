import { expect, type Locator } from "@playwright/test";
import { AccountSettingsSectionComponent } from "../../components/pib";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { Strings } from "../../test-data/strings";
import { BasePibPage } from "./basePib.page";

/**
 * Invite employees to add themselves section
 */
export class InviteEmployeesToAddThemselvesSection extends BasePibPage {
  readonly url = "manage/employees/invite";

  // ######## UI elements/properties ########
  readonly inviteEmployeesBackButton: Locator = this.page.locator( 'span [data-testid="InviteEmployees-back-icon"]', );
  readonly inviteEmployeesTitleLabel: Locator = this.page.getByTestId( "InviteEmployees-title", );
  readonly companyEmailInput: Locator = this.page.getByTestId( "CompanyEmail-Form-Input", );
  readonly companyEmailErrorTooltip: Locator = this.page.getByTestId( "CompanyEmail-Error-Tooltip", );
  readonly paymentTypeTitleLabel: Locator = this.page.getByTestId("Card-Name-Heading");
  readonly cardDropdownButton: Locator = this.page.getByTestId( "cardId-IB-Form-Select-Button", );
  readonly cardDropdownLabel: Locator = this.cardDropdownButton.locator("span");
  readonly cardOptions: Locator = this.page.locator( '//div[@data-testid="cardId-IB-Form-Select-Dropdown"]/button', );
  readonly sendInviteButton: Locator = this.page.getByTestId( "InviteEmployees-Send-Invite-Button", );
  readonly weSendActivationEmailTooltipLabel: Locator = this.page.locator( '//div[contains(@class,"bg-tooltipInfo")]/div', );

  // UI components
  readonly accountSettingsSection = new AccountSettingsSectionComponent();

  // ######## UI actions/navigation ########
  /** Open IB Invite Employees page. */
  async open(): Promise<void> {
    console.log("Open IB Invite Employees page");
    await this.openPath(this.url);
    await this.validatePage();
  }

  /** Click on back arrow button. */
  async clickBackArrowButton(): Promise<void> {
    console.log("Click on back arrow button");
    await this.inviteEmployeesBackButton.click();
  }

  /** Click on Send invite button. */
  async clickSendInviteButton(): Promise<void> {
    console.log("Click on Send invite button");
    await this.sendInviteButton.scrollIntoViewIfNeeded();
    await this.sendInviteButton.click();
  }

  /** Set employee email address input. */
  async setEmployeeCompanyEmail(email: string): Promise<void> {
    console.log(`Set Employee Company Email Address: ${email}`);
    await this.companyEmailInput.scrollIntoViewIfNeeded();
    await this.companyEmailInput.fill(email);
  }

  // ######## UI validations ########
  /** Validate Manage employee page elements from InnBusiness Pay tab. */
  async validateInviteEmployeesToAddThemselvesSection(): Promise<void> {
    console.log("Validate Add employees to add themselves section");
    await expect( this.inviteEmployeesBackButton, "Invite employees back button field", ).toBeVisible();
    await expect( this.inviteEmployeesTitleLabel, "Invite employees to add themselves title label", ).toHaveText(await IbStrings.INVITE_EMPLOYEES.name);
    await expect( this.companyEmailInput, "Company email input field", ).toBeVisible();
    await expect( this.paymentTypeTitleLabel, "Payment type title label", ).toHaveText(await Strings.PAYMENT_TYPE.name);
    await expect( this.cardDropdownButton, "Payment type card selection button", ).toBeVisible();
    await expect(this.sendInviteButton, "Send invite button").toBeVisible();
    await this.validateSendActivationEmailTooltip();
  }

  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Invite Employees page");
    await this.validatePageMarker(
      this.inviteEmployeesTitleLabel,
      "Invite Employees",
    );
  }

  /** Validate send activation email info tooltip message. */
  private async validateSendActivationEmailTooltip(): Promise<void> {
    console.log("Validate send activation email tooltip");
    const invitationText = await IbStrings.WE_LL_SEND_AN_INVITATION.name;
    const employeesText = await IbStrings.EMPLOYEES_YOU_WILL_ADD.name;
    const expectedText = `${invitationText.replace(/'/g, "'")}\n${employeesText.replace(/\.\s/, ".\n")}`;
    await expect( this.weSendActivationEmailTooltipLabel, "Send activation email tooltip message", ).toHaveText(expectedText);
  }
}
