import { expect, type Locator } from "@playwright/test";
import { MemorableWordSectionComponent } from "../../components/pib";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * InnBusiness application > Spending > Link an InnBusiness Pay account
 */
export class LinkAnInnBusinessPayAccountPage extends BasePibPage {
  readonly url = "link-innbusiness-account?linkCode=";
  static readonly LINK_ACCOUNT_CONTAINER_CSS =
    'div[data-testid="LinkInnbusinessAccount-container"]';
  // ######## UI elements/properties ########
  readonly headerLabel: Locator = this.page.locator( `${LinkAnInnBusinessPayAccountPage.LINK_ACCOUNT_CONTAINER_CSS} h1`, );
  readonly backIconImg: Locator = this.page.getByTestId( "LinkInnbusinessAccount-back-icon", );
  readonly descriptionLabel: Locator = this.page.locator( `${LinkAnInnBusinessPayAccountPage.LINK_ACCOUNT_CONTAINER_CSS} div.items-start ~ p`, );
  readonly linkCodeInput: Locator = this.page.getByTestId( "LinkCode-Form-Input", );
  readonly linkCodeErrorNotificationLabel: Locator = this.page.locator( "div.flex.flex-col.text-sm span", );
  readonly linkCodeErrorNotificationLink: Locator = this.linkCodeErrorNotificationLabel.locator("a");
  readonly cardAccountNumberInput: Locator = this.page.getByTestId( "CardNumber-Form-Input", );
  readonly cardAccountNumberDescriptionLabel: Locator = this.page.locator( `${LinkAnInnBusinessPayAccountPage.LINK_ACCOUNT_CONTAINER_CSS} p.text-xs`, );
  readonly cardAccountNumberErrorLabel: Locator = this.page.getByTestId( "CardNumber-Error-Tooltip", );
  readonly createMemorableWordTitleLabel: Locator = this.page.locator( `${LinkAnInnBusinessPayAccountPage.LINK_ACCOUNT_CONTAINER_CSS} h2.text-xl`, );
  readonly createMemorableWordDescriptionLabel: Locator = this.createMemorableWordTitleLabel.locator("xpath=following-sibling::p");
  readonly continueButton: Locator = this.page.getByTestId( "LinkInnbusinessAccount-Button", );
  // UI components
  readonly memorableWordSection = new MemorableWordSectionComponent();
  // ######## UI actions/navigation ########
  /** Open IB Link an InnBusiness Pay account page. */
  async open(
    linkCode: string,
  ): Promise<void> {
    console.log(`Open IB Link an InnBusiness Pay account page for ${linkCode}`);
    await this.openPath(`${this.url}${linkCode}`);
    await this.validatePage();
  }
  /** Navigate to IB Link an InnBusiness Pay account page UAT environment. */
  async navigateToUATLinkAnIBPayAccount(): Promise<void> {
    console.log("Navigate to UAT Link an InnBusiness Pay account page");
    await this.page.goto(this.page.url().replace("dit", "uat"));
  }
  /** Click Back icon. */
  async clickBackIcon(): Promise<void> {
    console.log("Click Back icon");
    await this.backIconImg.click();
    await expect( this.backIconImg, "Back icon after navigation", ).not.toBeVisible();
  }
  /** Click Continue button. */
  async clickContinueButton(): Promise<void> {
    console.log("Click Continue button");
    await this.continueButton.click();
    await expect( this.continueButton, "Continue button after navigation", ).not.toBeVisible();
  }
  /** Click Notification Error Link. */
  async clickNotificationErrorLink(): Promise<void> {
    console.log("Click Notification Error Link");
    await this.linkCodeErrorNotificationLink.scrollIntoViewIfNeeded();
    await this.linkCodeErrorNotificationLink.click();
    await expect( this.linkCodeErrorNotificationLink, "Notification error link after navigation", ).not.toBeVisible();
  }
  /** Set value to link code input. */
  async setLinkCodeValue(
    { value, pressTab = false }: { value: string; pressTab?: boolean } = {} as {
      value: string;
      pressTab?: boolean;
    },
  ): Promise<void> {
    console.log(`Set value - ${value} to link code`);
    await this.linkCodeInput.fill(value);
    if (pressTab) await this.linkCodeInput.press("Tab");
  }
  /** Set value to card/account number input. */
  async setCardAccountNumberValue(
    { value, pressTab = true }: { value: string; pressTab?: boolean } = {} as {
      value: string;
      pressTab?: boolean;
    },
  ): Promise<void> {
    console.log(`Set value - ${value} to card/account number`);
    await this.cardAccountNumberInput.fill(value);
    if (pressTab) await this.cardAccountNumberInput.press("Tab");
  }
  /** Get link code from URL. */
  async getLinkCodeFromUrl(): Promise<
    string | undefined
  > {
    console.log("Get link code from URL");
    return new URL(this.page.url()).searchParams.get("linkCode") ?? undefined;
  }
  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Link an InnBusiness Pay account page");
    await this.validatePageMarker(
      this.headerLabel,
      "Link an InnBusiness Pay account",
    );
  }
  /** Validate the placeholder and value of the link code input. */
  async validateLinkCodeInputValueAndPlaceholder({
    placeholder,
    value = "",
  }: { placeholder?: string; value?: string } = {}): Promise<void> {
    console.log("Validate link code input value and placeholder");
    await expect(this.linkCodeInput, "Link code input value").toHaveValue( value, );
    if (placeholder)
      await expect( this.linkCodeInput, "Link code input placeholder", ).toHaveAttribute("placeholder", placeholder);
  }
  /** Validate the placeholder and value of the card/account number input. */
  async validateCardAccountNumberInputValueAndPlaceholder({
    placeholder,
    value = "",
  }: { placeholder?: string; value?: string } = {}): Promise<void> {
    console.log("Validate Card/Account number input value and placeholder");
    await expect( this.cardAccountNumberInput, "Card/Account number input value", ).toHaveValue(value);
    if (placeholder)
      await expect( this.cardAccountNumberInput, "Card/Account number input placeholder", ).toHaveAttribute("placeholder", placeholder);
  }
  /** Validate Card/Account number input error. */
  async validateCardAccountNumberInputError({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate Card/Account number input error");
    if (isDisplayed) {
      await expect( this.cardAccountNumberErrorLabel, "Card/Account number input error label", ).toBeVisible();
      await expect( this.cardAccountNumberErrorLabel, "Card/Account number input error label text", ).toHaveText( await IbStrings.PLEASE_ENTER_A_VALID_CARD_NUMBER_OR_ACCOUNT_NUMBER.name, );
    } else
      await expect( this.cardAccountNumberErrorLabel, "Card/Account number input error label", ).not.toBeVisible();
  }
  /** Validate Link an Inn Business Pay account page elements. */
  async validateLinkAnInnBusinessPayAccountElements({
    linkCode = "",
  }: { linkCode?: string } = {}): Promise<void> {
    console.log("Validate Link an Inn Business Pay account page elements");
    await expect(this.headerLabel, "Header label").toHaveText( await IbStrings.LINK_AN_INN_BUSINESS_PAY_ACCOUNT.name, );
    await expect(this.backIconImg, "Back icon").toBeVisible();
    await expect(this.descriptionLabel, "Description label").toHaveText( await IbStrings.PLEASE_PROVIDE_THE_DETAILS_BELOW.name, );
    await this.validateLinkCodeInputValueAndPlaceholder({
      value: linkCode === "" ? "undefined" : linkCode,
    });
    await this.validateCardAccountNumberInputValueAndPlaceholder();
    await expect( this.cardAccountNumberDescriptionLabel, "Card/account number description label", ).toHaveText(await IbStrings.IF_YOU_DONT_HAVE_A_CARD.name);
    await expect( this.createMemorableWordTitleLabel, "Create memorable word title label", ).toHaveText(await IbStrings.CREATE_A_MEMORABLE_WORD.name);
    await expect( this.createMemorableWordDescriptionLabel, "Create memorable word description label", ).toHaveText(await IbStrings.THIS_CAN_AUTHORISE_CARD_PAYMENTS.name);
    await this.memorableWordSection.validateMemorableWordElements();
    await expect(this.continueButton, "Continue button").toHaveText( await IbStrings.CONTINUE_AUTH.name, );
  }
  /** Validate Link code error notification. */
  async validateLinkCodeErrorNotification({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate Link code error notification");
    if (isDisplayed) {
      await expect( this.linkCodeErrorNotificationLabel, "Link code error notification label", ).toBeVisible();
      await expect( this.linkCodeErrorNotificationLabel, "Link code error notification label text", ).toHaveText(await IbStrings.LINK_AN_INN_BUSINESS_PAY_ACCOUNT_ERROR.name);
    } else
      await expect( this.linkCodeErrorNotificationLabel, "Link code error notification label", ).not.toBeVisible();
  }
}
