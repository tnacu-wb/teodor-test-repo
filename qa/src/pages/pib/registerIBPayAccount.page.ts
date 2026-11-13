import { expect, type Locator } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * Register your IB Pay account page class
 */
export class RegisterIBPayAccountPage extends BasePibPage {
  readonly url = "business-pay/register";
  // ######## UI elements/properties ########
  readonly pageTitleLabel: Locator = this.page.getByTestId( "RegisterIbPage-title", );
  readonly pageDescriptionLabel: Locator = this.page.getByTestId( "RegisterIbPage-description", );
  readonly registrationCodeLabel: Locator = this.page.getByTestId( "RegisterIbPage-registration-code-form-title", );
  readonly registrationCodeInput: Locator = this.page.getByTestId( "registrationCode-Form-Input", );
  readonly registrationCodeErrorLabel: Locator = this.page.getByTestId( "registrationCode-Error-Tooltip", );
  readonly registerButton: Locator = this.page.getByTestId( "RegisterIbPage-Submit-Button", );
  readonly registrationCodeInfoTitleLabel: Locator = this.page.getByTestId( "RegisterIbPage-registration-code", );
  readonly registrationCodeInfoDescriptionLabel: Locator = this.page.getByTestId("RegisterIbPage-registration-code-subtitle");
  /** Get description list item by index. */
  getDescriptionListItem(
    index: number,
  ): Locator {
    return this.page.locator(
      `ul[data-testid="RegisterIbPage-description-list"] li:nth-child(${index})`,
    );
  }
  // ######## UI actions/navigation ########
  /** Open IB register page. */
  async open(): Promise<void> {
    console.log("Open IB register page");
    await this.openPath(this.url);
    await this.validatePage();
  }
  /** Set registration code. */
  async setRegistrationCodeInput(
    {
      registrationCode,
      pressTab = false,
    }: { registrationCode: string; pressTab?: boolean } = {} as {
      registrationCode: string;
      pressTab?: boolean;
    },
  ): Promise<void> {
    console.log(`Setting registration code: ${registrationCode}`);
    await this.registrationCodeInput.fill(registrationCode);
    if (pressTab) await this.registrationCodeInput.press("Tab");
  }
  /** Click on register button. */
  async clickOnRegisterButton(): Promise<void> {
    console.log("Click on register button");
    await this.registerButton.click();
  }
  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Register IB Pay account page");
    await this.validatePageMarker(
      this.pageTitleLabel,
      "Register IB Pay account",
    );
  }
  /** Validate page elements. */
  async validateRegisterIBPayAccountPageElements(): Promise<void> {
    console.log("Validate Register IB Pay Account page elements");
    await expect( this.pageTitleLabel, "Register your IB Pay account title", ).toHaveText(await IbStrings.PAY_APP_REGISTER.name);
    await expect( this.pageDescriptionLabel, "Register your IB Pay account description", ).toHaveText(await IbStrings.PAY_APP_REGISTER_DESCRIPTION.name);
    for (let index = 1; index <= 2; index += 1)
      await expect( this.getDescriptionListItem(index), `Register IB Pay account description list item ${index}`, ).toHaveText( await IbStrings[ `PAY_APP_REGISTER_DESCRIPTION_INFO_${index}` as | "PAY_APP_REGISTER_DESCRIPTION_INFO_1" | "PAY_APP_REGISTER_DESCRIPTION_INFO_2" ].name, );
    await expect(this.registrationCodeLabel, "Registration code").toHaveText( await IbStrings.PAY_APP_REGISTRATION_CODE.name, );
    await expect( this.registrationCodeInput, "Registration code input value", ).toHaveValue("");
    await expect( this.registrationCodeInput, "Registration code input placeholder", ).toHaveAttribute( "placeholder", await IbStrings.PAY_APP_REGISTRATION_CODE_PLACEHOLDER.name, );
    await expect(this.registerButton, "Register button").toHaveText( await IbStrings.PAY_APP_REGISTER_BUTTON.name, );
    await expect( this.registrationCodeInfoTitleLabel, "Registration code info title", ).toHaveText(await IbStrings.PAY_APP_REGISTRATION_CODE_FIND.name);
    await expect( this.registrationCodeInfoDescriptionLabel, "Registration code info description", ).toHaveText(await IbStrings.PAY_APP_REGISTRATION_CODE_FIND_INFO.name);
  }
  /** Validate registration code error. */
  async validateRegistrationCodeError(
    errorMessage: string,
  ): Promise<void> {
    console.log("Validate registration code error");
    await expect( this.registrationCodeErrorLabel, "Registration code error label", ).toHaveText(errorMessage);
  }
}
