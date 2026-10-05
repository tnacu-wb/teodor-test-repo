import { expect, type Locator, type Page } from "@playwright/test";
import { type Customer } from "../../../api/response/customer";
import {
  MealsAndExtrasOptions,
  type MealsAndExtrasOption,
} from "../../../test-data/mealsAndExtrasOptions";
import { IbStrings } from "../../../test-data/pib/ibStrings";

/**
 * InnBusiness Meals and Extras Preferences Section from My Profile page
 */
export class MealsAndExtrasPreferencesSectionComponent {
  private readonly page: Page = global.page;

  // ######## properties ########

  // ######## UI elements/properties ########
  readonly mealsAndExtrasContainerTitleLabel: Locator = this.page.locator( '//h4[@data-testid="ProfilePage-Meals-And-Extras-Title"]', );
  readonly mealsAndExtrasDescriptionLabel: Locator = this.page.locator( '//p[@data-testid="ProfilePage-Meals-And-Extras-Description"]', );
  readonly mealsHeadingLabel: Locator = this.page.locator( '//h4[@data-testid="Meals-Heading"]', );
  readonly piBreakfastButton: Locator = this.page.locator( '//button[@id="mealOption-1"]', );
  readonly continentalBreakfastButton: Locator = this.page.locator( '//button[@id="mealOption-2"]', );
  readonly mealDealButton: Locator = this.page.locator( '//button[@id="mealOption-3"]', );
  readonly noMealsButton: Locator = this.page.locator( '//button[@id="mealOption-4"]', );
  readonly wifiHeadingLabel: Locator = this.page.locator( '//h4[@data-testid="Wifi-Heading"]', );
  readonly wifiAlwaysButton: Locator = this.page.locator( '//button[@id="wifiOption-1"]', );
  readonly wifiNeverButton: Locator = this.page.locator( '//button[@id="wifiOption-2"]', );
  readonly invoicingHeadingLabel: Locator = this.page.locator( '//h4[@data-testid="Invoicing-Heading"]', );
  readonly invoicingEmailButton: Locator = this.page.locator( '//button[@id="invoicingOption-1"]', );
  readonly invoicingCheckInButton: Locator = this.page.locator( '//button[@id="invoicingOption-2"]', );
  readonly saveChangesButton: Locator = this.page.locator( '//button[@data-testid="ProfilePage-Meals-And-Extras-Save-Button"]', );
  readonly cancelChangesButton: Locator = this.page.locator( '//button[@data-testid="ProfilePage-Meals-And-Extras-Cancel-Button"]', );
  /** Get the meal option button by its number. */
  getMealOptionButtonByOptionNumber(optionNumber: number): Locator {
    return this.page.locator(`//button[@id="mealOption-${optionNumber}"]`);
  }
  /** Get the wifi option button by its number. */
  getWiFiOptionButtonByOptionNumber(optionNumber: number): Locator {
    return this.page.locator(`//button[@id="wifiOption-${optionNumber}"]`);
  }
  /** Get the invoicing option button by its number. */
  getInvoicingOptionButtonByOptionNumber(optionNumber: number): Locator {
    return this.page.locator(`//button[@id="invoicingOption-${optionNumber}"]`);
  }

  // ######## UI actions/navigation ########
  /** Check the meal option by its number. */
  async checkMealOption({
    mealOption,
  }: {
    mealOption: MealsAndExtrasOption;
  }): Promise<void> {
    console.log(`Check meal option: ${await mealOption.option.name}`);
    await this.mealsHeadingLabel.scrollIntoViewIfNeeded();
    await this.getMealOptionButtonByOptionNumber(
      mealOption.optionNumber ?? 0,
    ).click();
  }

  /** Check the Wi-Fi option by its number. */
  async checkWiFiOption({
    wifiOption,
  }: {
    wifiOption: MealsAndExtrasOption;
  }): Promise<void> {
    console.log(`Check Wi-Fi option: ${await wifiOption.option.name}`);
    await this.wifiHeadingLabel.scrollIntoViewIfNeeded();
    await this.getWiFiOptionButtonByOptionNumber(
      wifiOption.optionNumber ?? 0,
    ).click();
  }

  /** Check the invoicing option by its number. */
  async checkInvoicingOption({
    invoicingOption,
  }: {
    invoicingOption: MealsAndExtrasOption;
  }): Promise<void> {
    console.log(`Check invoicing option: ${await invoicingOption.option.name}`);
    await this.invoicingHeadingLabel.scrollIntoViewIfNeeded();
    await this.getInvoicingOptionButtonByOptionNumber(
      invoicingOption.optionNumber ?? 0,
    ).click();
  }

  /** Click on save changes button. */
  async clickSaveChangesButton(): Promise<void> {
    console.log("Click on save changes button");
    await this.saveChangesButton.scrollIntoViewIfNeeded();
    await this.saveChangesButton.click();
  }

  // ######## UI validations ########
  /** Validate Meals and Extras Preferences section. */
  async validateMealsAndExtrasEditableSection({
    profileDetails,
  }: {
    profileDetails: Customer;
  }): Promise<void> {
    console.log("Validate Meals and Extras Preferences editable section");
    await expect( this.mealsAndExtrasContainerTitleLabel, "Meals and extras preferences section heading label", ).toHaveText(await IbStrings.MEALS_AND_EXTRAS_MY_PROFILE_IB.name);
    await expect( this.mealsAndExtrasDescriptionLabel, "Meals and extras preferences section description label", ).toHaveText( await IbStrings.MEALS_AND_EXTRAS_DESCRIPTION_MY_PROFILE_IB.name, );
    await expect(this.mealsHeadingLabel, "Meals label").toHaveText( await IbStrings.MEALS_AND_EXTRAS_MEAL_TITLE_MY_PROFILE_IB.name, );
    await expect( this.piBreakfastButton, "Premier Inn Breakfast radio button", ).toHaveAttribute( "aria-checked", String( profileDetails.bookingPreference?.foodPreference === MealsAndExtrasOptions.PI_BREAKFAST.value, ), );
    await expect( this.continentalBreakfastButton, "Continental Breakfast radio button", ).toHaveAttribute( "aria-checked", String( profileDetails.bookingPreference?.foodPreference === MealsAndExtrasOptions.CONTINENTAL_BREAKFAST.value, ), );
    await expect(this.mealDealButton, "Meal Deal radio button").toHaveAttribute( "aria-checked", String( profileDetails.bookingPreference?.foodPreference === MealsAndExtrasOptions.MEAL_DEAL.value, ), );
    await expect(this.noMealsButton, "No meals radio button").toHaveAttribute( "aria-checked", String( profileDetails.bookingPreference?.foodPreference === MealsAndExtrasOptions.NO_MEAL.value, ), );
    await expect(this.wifiHeadingLabel, "Wi-Fi label").toHaveText( await IbStrings.MEALS_AND_EXTRAS_WIFI_TITLE_MY_PROFILE_IB.name, );
    await expect( this.wifiAlwaysButton, "Always Wi-Fi radio button", ).toHaveAttribute( "aria-checked", String(profileDetails.bookingPreference?.preselectWifi), );
    await expect( this.wifiNeverButton, "Never Wi-Fi radio button", ).toHaveAttribute( "aria-checked", String(!profileDetails.bookingPreference?.preselectWifi), );
    await expect(this.invoicingHeadingLabel, "Invoicing label").toHaveText( await IbStrings.MEALS_AND_EXTRAS_INVOICING_TITLE_MY_PROFILE_IB.name, );
    await expect( this.invoicingEmailButton, "Email invoice radio button", ).toHaveAttribute( "aria-checked", String(profileDetails.paymentPreference?.electronicInvoiceRequired), );
    await expect( this.invoicingCheckInButton, "Check-in invoice radio button", ).toHaveAttribute( "aria-checked", String(!profileDetails.paymentPreference?.electronicInvoiceRequired), );
    await expect(this.saveChangesButton, "Save changes button").toHaveText( await IbStrings.MEALS_AND_EXTRAS_SAVE_BUTTON_MY_PROFILE_IB.name, );
    await expect(this.cancelChangesButton, "Cancel changes button").toHaveText( await IbStrings.MEALS_AND_EXTRAS_CANCEL_BUTTON_MY_PROFILE_IB.name, );
  }
}
