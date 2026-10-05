import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";

/** InnBusiness application > Who will use this card section. */
export class WhoWillUseThisCardSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly whoWillUseThisCardTitleLabel: Locator = this.page.getByTestId( "Who-Container-Title", );
  readonly meRadioButton: Locator = this.page.locator( 'div[data-testid="userRadioGroup"] > label:nth-of-type(1) button', );
  readonly existingEmployeeRadioButton: Locator = this.page.locator( 'div[data-testid="userRadioGroup"] > label:nth-of-type(2) button', );
  // ######## UI actions/navigation ########
  /** Click on who will use this card option (Me/Existing employee). */
  async clickWhoWillUseCardOption(
    cardUser: string | Promise<string>,
  ): Promise<void> {
    console.log("Click who will use this card options");
    const option = await cardUser;
    if (option === (await IbStrings.ME.name)) await this.meRadioButton.click();
    else if (option === (await IbStrings.AN_EXISTING_EMPLOYEE.name))
      await this.existingEmployeeRadioButton.click();
    else throw new Error(`Option with ${option} is not available`);
  }
  // ######## UI validations ########
  /** Validate Me/Existing employee radio buttons are checked or not. */
  async validateMeExistingRadioButtons({
    isMeChecked = true,
    isExistingEmployeeChecked = false,
  }: {
    isMeChecked?: boolean;
    isExistingEmployeeChecked?: boolean;
  } = {}): Promise<void> {
    console.log("Validate Me/Existing employee radio buttons");
    await expect(this.meRadioButton, "Me radio button").toHaveAttribute( "aria-checked", isMeChecked ? "true" : "false", );
    await expect( this.existingEmployeeRadioButton, "Existing employee radio button", ).toHaveAttribute( "aria-checked", isExistingEmployeeChecked ? "true" : "false", );
  }
}
