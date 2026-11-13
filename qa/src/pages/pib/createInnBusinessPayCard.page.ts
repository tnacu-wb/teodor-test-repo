import { expect, type Locator } from "@playwright/test";
import {
  AccountHolderSectionComponent,
  CardDeliveryOptionsSectionComponent,
  CardHolderSectionComponent,
  ConsentSectionComponent,
  CostCenterSelectorSectionComponent,
  IbPayAddCardConfirmationSectionComponent,
  RestrictUsageSectionComponent,
  SetCreditLimitSectionComponent,
  WhoWillUseThisCardSectionComponent,
} from "../../components/pib";
import { Strings } from "../../test-data/strings";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * InnBusiness application > Manage > Card management > InnBusinessPay > Create an InnBusiness Pay card
 */
export class CreateInnBusinessPayPage extends BasePibPage {
  // ######## UI elements/properties ########
  readonly addCardPageTitleLabel: Locator = this.page.getByTestId( "Inn-Business-Pay-Add-Card-title", );
  readonly accountHolderCardInformationForm: Locator = this.page .getByTestId("Add-Card-Account-Holder-InnBusinessPay-Container") .locator("div") .first();
  readonly accountHolderCardAccountNameLabel: Locator = this.accountHolderCardInformationForm.locator("div");
  readonly accountHolderCardAccountBadgeLabel: Locator = this.accountHolderCardInformationForm.locator("span");
  readonly addNewEmployeeButton: Locator = this.page.getByTestId( "Add-Card-New-Employee-Button", );
  readonly addCardContinueButton: Locator = this.page.getByTestId( "Continue-Button-Add-Card", );
  readonly backButton: Locator = this.page.getByTestId( "Inn-Business-Pay-Add-Card-back-icon", );
  // UI components
  readonly cardDeliveryOptionSection =
    new CardDeliveryOptionsSectionComponent();
  readonly accountHolderSection = new AccountHolderSectionComponent();
  readonly cardConfirmationSection =
    new IbPayAddCardConfirmationSectionComponent();
  readonly whoWillUseThisCardSection = new WhoWillUseThisCardSectionComponent();
  readonly cardHolderSection = new CardHolderSectionComponent();
  readonly creditLimitSection = new SetCreditLimitSectionComponent();
  readonly consentSection = new ConsentSectionComponent();
  readonly restrictUsageSection = new RestrictUsageSectionComponent();
  readonly costCenterSelectorSection = new CostCenterSelectorSectionComponent();
  // ######## UI actions/navigation ########
  /** Click on Continue button from add card page. */
  async clickAddCardContinueButton(): Promise<void> {
    console.log("Click on Continue button from add card page");
    await this.addCardContinueButton.click();
  }
  /** Click on Back button. */
  async clickBackButton(): Promise<void> {
    console.log("Click on Back button");
    await this.backButton.click();
  }
  /** Click on Add a new employee button. */
  async clickAddNewEmployeeButton(): Promise<void> {
    console.log("Click on Add a new employee button");
    await this.addNewEmployeeButton.click();
  }
  // ######## UI validations ########
  /** Validate create InnBusiness Pay Card page. */
  async validateCreateInnBusinessPayPage(userRole?: string): Promise<void> {
    const resolvedUserRole = userRole ?? (await Strings.TRAVEL_MANAGER.name);
    console.log(
      `Validate create InnBusiness Pay Card page for ${resolvedUserRole}`,
    );
    await expect( this.addCardPageTitleLabel, "Create InnBusiness Pay page title", ).toHaveText(await IbStrings.CREATE_AN_INNBUSINESS_PAY_CARD.name);
    await expect( this.accountHolderCardInformationForm, "Card form", ).toBeVisible();
    if (
      resolvedUserRole === (await Strings.TRAVEL_MANAGER.name) ||
      resolvedUserRole === (await IbStrings.BOOKER.name)
    ) {
      await expect( this.whoWillUseThisCardSection.whoWillUseThisCardTitleLabel, "Who will use this card title label", ).toHaveText(await IbStrings.ADD_NEW_CARD_WHO_WILL_USE_THIS_CARD.name);
      await expect( this.whoWillUseThisCardSection.meRadioButton, "Me radio button", ).toBeVisible();
      await expect( this.whoWillUseThisCardSection.existingEmployeeRadioButton, "Existing employee radio button", ).toBeVisible();
      return;
    }
    if (
      resolvedUserRole === (await IbStrings.SELF_BOOKER.name) ||
      resolvedUserRole === (await Strings.GUEST.name)
    ) {
      await expect( this.whoWillUseThisCardSection.meRadioButton, "Me radio button", ).not.toBeVisible();
      await expect( this.whoWillUseThisCardSection.existingEmployeeRadioButton, "Existing employee radio button", ).not.toBeVisible();
      await expect( this.addNewEmployeeButton, "Add new employee button", ).not.toBeVisible();
      return;
    }
    throw new Error(`User role "${resolvedUserRole}" is not set correctly`);
  }
  /** Validate Add card continue button. */
  async validateAddCardContinueButton({
    isEnabled = true,
  }: { isEnabled?: boolean } = {}): Promise<void> {
    console.log("Validate Add card continue button");
    await expect( this.addCardContinueButton, "Create InnBusiness Pay Add card continue button", ).toHaveText(await Strings.CONTINUE.name);
    if (isEnabled)
      await expect( this.addCardContinueButton, "Create InnBusiness Pay Add card continue button enabled", ).toBeEnabled();
    else
      await expect( this.addCardContinueButton, "Create InnBusiness Pay Add card continue button disabled", ).toBeDisabled();
  }
}
