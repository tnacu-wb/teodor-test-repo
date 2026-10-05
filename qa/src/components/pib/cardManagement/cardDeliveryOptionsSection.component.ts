import { expect, type Locator, type Page } from "@playwright/test";
import { Locales } from "../../../test-data/locales";
import { type DeliveryOption } from "../../../test-data/pib/deliveryOptions";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { Strings } from "../../../test-data/strings";

interface EmployeeDetails {
  title: string;
  firstName: string;
  lastName: string;
}

/**
 * Card Management > InnBusiness Pay section > Create Card > Card Delivery Options section
 */
export class CardDeliveryOptionsSectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly cardDeliveryOptionsTitleLabel: Locator = this.page.getByTestId( "Inn-Business-Pay-Add-Card-title", );
  readonly whereToSendCardTitleLabel: Locator = this.page.locator( 'span[data-testid="Delivery-Title"]', );
  readonly deliveryOptionsList: Locator = this.page.locator( 'span[data-testid="Delivery-Title"] ~ div[data-testid="userRadioGroup"] button', );
  readonly companyRegisteredAddressButton: Locator = this.page.locator( '//button[@id="COMPANY_REGISTERED_ADDRESS"]', );
  readonly companyCorrespondenceAddressButton: Locator = this.page.locator( '//button[@id="COMPANY_CORRESPONDENCE_ADDRESS"]', );
  readonly cardholderAlternativeAddressButton: Locator = this.page.locator( '//button[@id="CARDHOLDER_ALTERNATIVE_ADDRESS"]', );
  readonly createCardButton: Locator = this.page.getByTestId("Add-Card-Submit");
  readonly cardholderAlternativeAddressTooltipLabel: Locator = this.page.locator( "div.bg-notificationAlertBg.border-notificationAlertBorder.mt-12", );
  readonly cardHolderAddressForm: Locator = this.page.locator( "div.form-details-box.mt-4", );
  readonly cardHolderAddressFormTitleLabel: Locator = this.page.locator( '//div[@data-testid="Title-IB-Form-Select"]/parent::node()/span', );
  readonly cardHolderFormTitleLabel: Locator = this.page.getByTestId("Title-Label");
  readonly cardHolderInputTitleLabel: Locator = this.page.locator( 'button[data-testid="Title-IB-Form-Select-Button"] span', );
  readonly cardHolderFormFirstNameLabel: Locator = this.page.getByTestId("First-Name-Label");
  readonly cardHolderInputFirstNameLabel: Locator = this.page.getByTestId( "First-Name-Form-Input", );
  readonly cardHolderFormLastNameLabel: Locator = this.page.getByTestId("Last-Name-Label");
  readonly cardHolderInputLastNameLabel: Locator = this.page.getByTestId( "Last-Name-Form-Input", );
  // ######## UI actions/navigation ########
  /** Click company registered address button. */
  async clickCompanyRegisteredAddressButton(): Promise<void> {
    console.log("Click Send to Company Registered Address button");
    await this.companyRegisteredAddressButton.click();
  }
  /** Click company correspondence address button. */
  async clickCompanyCorrespondenceAddressButton(): Promise<void> {
    console.log("Click Send to Company Correspondence Address button");
    await this.companyCorrespondenceAddressButton.click();
  }
  /** Click cardholder alternative address button. */
  async clickCardholderAlternativeAddressButton(): Promise<void> {
    console.log("Click Send to Cardholder Alternative Address button");
    await this.cardholderAlternativeAddressButton.click();
  }
  /** Click create card button. */
  async clickCreateCardButton({
    shouldGoToNextPage = true,
  }: { shouldGoToNextPage?: boolean } = {}): Promise<void> {
    console.log("Click Create card button");
    await this.createCardButton.click();
    if (shouldGoToNextPage)
      await expect( this.createCardButton, "Create card button should disappear after navigation", ).not.toBeVisible();
  }
  // ######## UI validations ########
  /** Validate card delivery options page title. */
  async validateCardDeliveryOptionsPageTitle(): Promise<void> {
    console.log("Validate Card delivery options page title");
    await expect( this.cardDeliveryOptionsTitleLabel, "Card delivery options page title", ).toHaveText(await IbStrings.CARD_DELIVERY_OPTIONS.name);
  }
  /** Validate registered address. */
  async validateRegisteredAddress(
    deliveryOption: DeliveryOption,
  ): Promise<void> {
    console.log("Validate Company registered address");
    await this.validateDeliveryOption(
      this.companyRegisteredAddressButton,
      await IbStrings.SEND_CARD_TO_REGISTERED_ADDRESS.name,
      deliveryOption,
      true,
    );
  }
  /** Validate correspondence address. */
  async validateCorrespondenceAddress(
    deliveryOption: DeliveryOption,
  ): Promise<void> {
    console.log("Validate Company correspondence address");
    await this.validateDeliveryOption(
      this.companyCorrespondenceAddressButton,
      await IbStrings.SEND_CARD_TO_CORRESPONDENCE_ADDRESS.name,
      deliveryOption,
      true,
    );
  }
  /** Validate alternative address. */
  async validateAlternativeAddress(
    deliveryOption: DeliveryOption,
    whoWillUseCardOption: string,
  ): Promise<void> {
    console.log("Validate Cardholder alternative address");
    const isMe = whoWillUseCardOption === (await IbStrings.ME.name);
    const label =
      await IbStrings.SEND_CARD_TO_CARDHOLDER_ALTERNATIVE_ADDRESS.name;
    await this.validateDeliveryOption(
      this.cardholderAlternativeAddressButton,
      isMe ? `${label} (Not available)` : label,
      deliveryOption,
      !isMe,
    );
  }
  /** Validate card delivery options. */
  async validateCardDeliveryOptions({
    deliveryOptions,
    whoWillUseCardOption,
  }: {
    deliveryOptions: DeliveryOption[];
    whoWillUseCardOption?: string;
  }): Promise<void> {
    console.log("Validate card delivery options");
    const cardUser = whoWillUseCardOption ?? (await IbStrings.ME.name);
    for (const deliveryOption of deliveryOptions) {
      const name = await deliveryOption.name;
      if (name === (await IbStrings.SEND_CARD_TO_REGISTERED_ADDRESS.name))
        await this.validateRegisteredAddress(deliveryOption);
      else if (
        name === (await IbStrings.SEND_CARD_TO_CORRESPONDENCE_ADDRESS.name)
      )
        await this.validateCorrespondenceAddress(deliveryOption);
      else if (
        name ===
        (await IbStrings.SEND_CARD_TO_CARDHOLDER_ALTERNATIVE_ADDRESS.name)
      )
        await this.validateAlternativeAddress(deliveryOption, cardUser);
      else throw new Error(`${name} is not valid`);
    }
  }
  /** Validate card delivery options section. */
  async validateCardDeliveryOptionsSection({
    deliveryOptions,
    whoWillUseCardOption,
  }: {
    deliveryOptions: DeliveryOption[];
    whoWillUseCardOption?: string;
  }): Promise<void> {
    console.log("Validate Card Delivery Options section");
    const noOfOptions = Locales.isEnglishWebsite() ? 2 : 1;
    await expect( this.whereToSendCardTitleLabel, "Where to send card title label", ).toHaveText(await IbStrings.WHERE_TO_SEND_THIS_CARD.name);
    await expect( this.deliveryOptionsList, `Delivery options list length is ${noOfOptions}`, ).toHaveCount(noOfOptions);
    await this.validateCardDeliveryOptions({
      deliveryOptions,
      whoWillUseCardOption,
    });
    await expect(this.createCardButton, "Create card button").toHaveText( await IbStrings.CREATE_CARD.name, );
  }
  /** Validate card holder address section. */
  async validateCardHolderAddressSection(
    employeeDetails: EmployeeDetails,
  ): Promise<void> {
    console.log("Validate CardHolder address section");
    await expect( this.cardholderAlternativeAddressTooltipLabel, "Cardholder alternative address tooltip label", ).toHaveText(await IbStrings.CARDHOLDER_ALTERNATIVE_ADDRESS_TOOLTIP.name);
    await expect( this.cardHolderAddressForm, "Card holder address section", ).toBeVisible();
    await expect( this.cardHolderAddressFormTitleLabel, "Card holder address title label", ).toHaveText(await IbStrings.CARD_HOLDER_ADDRESS.name);
    await expect(this.cardHolderFormTitleLabel, "Form title label").toHaveText( await Strings.TITLE_LEAD_GUEST.name, );
    await expect( this.cardHolderInputTitleLabel, "Card holder title", ).toHaveText(employeeDetails.title);
    await expect( this.cardHolderFormFirstNameLabel, "Form first name label", ).toHaveText( await Strings.FIRST_NAME_ACCOMPANYING_GUEST_DETAILS_SECTION.name, );
    await expect( this.cardHolderInputFirstNameLabel, `Card holder first name "${employeeDetails.firstName}"`, ).toHaveValue(employeeDetails.firstName);
    await expect( this.cardHolderFormLastNameLabel, "Form last name label", ).toHaveText( await Strings.LAST_NAME_ACCOMPANYING_GUEST_DETAILS_SECTION.name, );
    await expect( this.cardHolderInputLastNameLabel, `Card holder last name "${employeeDetails.lastName}"`, ).toHaveValue(employeeDetails.lastName);
  }
  /** Validate delivery option. */
  private async validateDeliveryOption(
    button: Locator,
    label: string,
    option: DeliveryOption,
    shouldBeEnabled: boolean,
  ): Promise<void> {
    if (!option.optionAvailable) {
      await expect(button, `${label} option`).not.toBeVisible();
      return;
    }
    await expect(button, `${label} option`).toBeVisible();
    await expect(button, `${label} option label`).toHaveText(label);
    await expect(button, `${label} option selection`).toHaveAttribute( "data-state", option.buttonSelected ? "checked" : "unchecked", );
    if (shouldBeEnabled)
      await expect(button, `${label} option enabled`).toBeEnabled();
    else await expect(button, `${label} option disabled`).toBeDisabled();
  }
}
