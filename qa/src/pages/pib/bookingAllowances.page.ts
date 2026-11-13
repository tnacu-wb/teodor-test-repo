import { expect, type Locator } from "@playwright/test";
import {
  MenuContainerComponent,
  ReviewChangesModalComponent,
  ToastNotificationSectionComponent,
} from "../../components/pib";
import { MealsAndExtrasOptions } from "../../test-data/mealsAndExtrasOptions";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

interface SwitchOptions {
  switchToOnState?: boolean;
}
interface AmountOptions {
  amount: string;
  pressTab?: boolean;
}
interface AmountValidationOptions {
  amount?: string;
  isValid?: boolean;
}
interface ButtonState {
  premierInnBreakfast?: boolean;
  continentalBreakfast?: boolean;
  mealDeal?: boolean;
  hubBreakfast?: boolean;
  ultimateWiFi?: boolean;
  advanceRate?: boolean;
  individualPaymentCards?: boolean;
  includeAlcohol?: boolean;
  carParking?: boolean;
  additionalCosts?: boolean;
}
interface BookingAllowances {
  upsellItemsAllowed: string[];
  maxDinnerBudgets: {
    uKWide: { amount: number };
    greaterLondon: { amount: number };
    ireland: { amount: number };
  };
  allowIndividualCards: boolean;
  allowAlcohol: boolean;
  allowCarParking: boolean;
  allowAdditionalCosts: boolean;
}

/** InnBusiness application > Manage > Booking Allowances */
export class BookingAllowancesPage extends BasePibPage {
  readonly url = "manage/allowances";
  // ######## UI elements/properties ########
  readonly bookingAllowancesTitleLabel: Locator = this.page.getByTestId( "BookingAllowancesPage-container-title", );
  readonly bookingAllowancesDescriptionLabel: Locator = this.page.getByTestId( "BookingAllowancesPage-container-subtitle", );
  readonly setBookingOptionsSectionTitleLabel: Locator = this.page.getByTestId( "BookingOptionsForm-title-booking-options", );
  readonly setBookingOptionsSectionDescriptionLabel: Locator = this.page.getByTestId("BookingOptionsForm-description-booking-options");
  readonly mealsLabel: Locator = this.page.getByTestId( "BookingOptionsForm-meals-title", );
  readonly premierInnBreakfastButton: Locator = this.page.getByTestId( "BookingOptionsForm-premierInnBreakfast-switcher", );
  readonly continentalBreakfastButton: Locator = this.page.getByTestId( "BookingOptionsForm-continentalBreakfast-switcher", );
  readonly mealDealButton: Locator = this.page.getByTestId( "BookingOptionsForm-mealDeal-switcher", );
  readonly hubBreakfastButton: Locator = this.page.getByTestId( "BookingOptionsForm-hubBreakfast-switcher", );
  readonly otherExtrasLabel: Locator = this.page.getByTestId( "BookingOptionsForm-other-extras-title", );
  readonly ultimateWIFiButton: Locator = this.page.getByTestId( "BookingOptionsForm-ultimateWifi-switcher", );
  readonly advanceRateSectionLabel: Locator = this.page.getByTestId( "AdvanceRateForm-title-advance-rate", );
  readonly advanceRateSectionDescriptionLabel: Locator = this.page.locator( 'h4[data-testid="AdvanceRateForm-title-advance-rate"] ~ span', );
  readonly advanceRateButton: Locator = this.page.locator( 'button[data-testid*="AdvanceRateForm-"][data-testid*="-switcher"]', );
  readonly individualPaymentSectionLabel: Locator = this.page.getByTestId( "IndividualPaymentCardsForm-title-individual-cards", );
  readonly individualPaymentSectionDescriptionLabel: Locator = this.page.locator( 'h4[data-testid="IndividualPaymentCardsForm-title-individual-cards"] ~ span', );
  readonly individualPaymentButton: Locator = this.page.locator( 'button[data-testid*="IndividualPaymentCardsForm-"][data-testid*="-switcher"]', );
  readonly prepaidAllowancesSectionTitleLabel: Locator = this.page.locator( '//div[@data-testid="PrePaidAllowancesForm-container"]/div[1]/h4', );
  readonly prepaidAllowancesSectionDescriptionLabel: Locator = this.page.getByTestId( "PrePaidAllowancesForm-description-prepaid-allowances", );
  readonly prepaidAllowancesForm: Locator = this.page.getByTestId( "PrePaidAllowancesForm-inputs-container", );
  readonly unitedKingdomLabel: Locator = this.prepaidAllowancesForm .locator("h4") .nth(0);
  readonly greaterLondonLabel: Locator = this.prepaidAllowancesForm .locator("h4") .nth(1);
  readonly germanyIrelandLabel: Locator = this.prepaidAllowancesForm .locator("h4") .nth(2);
  readonly unitedKingdomErrorTooltipLabel: Locator = this.page.getByTestId( "PrePaidAllowancesForm-unitedKingdom-Error-Tooltip", );
  readonly greaterLondonErrorTooltipLabel: Locator = this.page.getByTestId( "PrePaidAllowancesForm-greaterLondon-Error-Tooltip", );
  readonly germanyIrelandErrorTooltipLabel: Locator = this.page.getByTestId( "PrePaidAllowancesForm-germanyIreland-Error-Tooltip", );
  readonly unitedKingdomAllowanceInput: Locator = this.page.getByTestId( "PrePaidAllowancesForm-unitedKingdom-Form-Input", );
  readonly greaterLondonAllowanceInput: Locator = this.page.getByTestId( "PrePaidAllowancesForm-greaterLondon-Form-Input", );
  readonly germanyIrelandAllowanceInput: Locator = this.page.getByTestId( "PrePaidAllowancesForm-germanyIreland-Form-Input", );
  readonly includeAlcoholButton: Locator = this.page.locator( 'button[data-testid*="PrePaidAllowancesForm-"][data-testid*="-switcher"]', );
  readonly selectExtrasLabel: Locator = this.page.getByTestId( "SelectExtrasForm-title-select-extras", );
  readonly carParkingButton: Locator = this.page.getByTestId( "SelectExtrasForm-carParking-switcher", );
  readonly additionalCostsButton: Locator = this.page.getByTestId( "SelectExtrasForm-additionalCosts-switcher", );
  readonly saveUpdatesButton: Locator = this.page.getByTestId( "BookingAllowances-save-changes-button", );
  readonly toastNotificationSection = new ToastNotificationSectionComponent();
  readonly reviewChangesModal = new ReviewChangesModalComponent();
  readonly sidebarSection = new MenuContainerComponent();
  // ######## UI actions/navigation ########
  /** Open IB Company Details page. */
  async open(): Promise<void> {
    console.log("Open IB Company Details page");
    await this.openPath(this.url);
    await this.validatePage();
  }
  /** Click Save updates button. */
  async clickSaveUpdatesButton(): Promise<void> {
    console.log("Click Save updates button");
    await this.saveUpdatesButton.click();
  }
  private async switchButton(
    button: Locator,
    switchToOnState: boolean,
  ): Promise<void> {
    const isOn = (await button.getAttribute("aria-checked")) === "true";
    if (isOn !== switchToOnState) await button.click();
  }
  /** Click Premier Inn Breakfast switch button. */
  async clickPremierInnBreakfastButton({
    switchToOnState = true,
  }: SwitchOptions = {}): Promise<void> {
    console.log("Click Premier Inn Breakfast button");
    await this.switchButton(this.premierInnBreakfastButton, switchToOnState);
  }
  /** Click Continental Breakfast switch button. */
  async clickContinentalBreakfastButton({
    switchToOnState = true,
  }: SwitchOptions = {}): Promise<void> {
    console.log("Click Continental Breakfast button");
    await this.switchButton(this.continentalBreakfastButton, switchToOnState);
  }
  /** Click Meal Deal switch button. */
  async clickMealDealButton({
    switchToOnState = true,
  }: SwitchOptions = {}): Promise<void> {
    console.log("Click Meal Deal button");
    await this.switchButton(this.mealDealButton, switchToOnState);
  }
  /** Click hub breakfast button. */
  async clickHubBreakfastButton({
    switchToOnState = true,
  }: SwitchOptions = {}): Promise<void> {
    console.log("Click hub Breakfast button");
    await this.switchButton(this.hubBreakfastButton, switchToOnState);
  }
  /** Click ultimate WiFi button. */
  async clickUltimateWiFiButton({
    switchToOnState = true,
  }: SwitchOptions = {}): Promise<void> {
    console.log("Click Ultimate WiFi button");
    await this.switchButton(this.ultimateWIFiButton, switchToOnState);
  }
  /** Click Advance Rate button. */
  async clickAdvanceRateButton({
    switchToOnState = true,
  }: SwitchOptions = {}): Promise<void> {
    console.log("Click Advance Rate button");
    await this.switchButton(this.advanceRateButton, switchToOnState);
  }
  /** Click Individual Payment cards button. */
  async clickIndividualPaymentCardsButton({
    switchToOnState = true,
  }: SwitchOptions = {}): Promise<void> {
    console.log("Click Individual Payment Cards button");
    await this.switchButton(this.individualPaymentButton, switchToOnState);
  }
  /** Click Include Alcohol button. */
  async clickIncludeAlcoholButton({
    switchToOnState = false,
  }: SwitchOptions = {}): Promise<void> {
    console.log("Click Include Alcohol button");
    await this.switchButton(this.includeAlcoholButton, switchToOnState);
  }
  /** Click Car Parking button. */
  async clickCarParkingButton({
    switchToOnState = false,
  }: SwitchOptions = {}): Promise<void> {
    console.log("Click Car Parking button");
    await this.switchButton(this.carParkingButton, switchToOnState);
  }
  /** Click Additional Costs button. */
  async clickAdditionalCostsButton({
    switchToOnState = false,
  }: SwitchOptions = {}): Promise<void> {
    console.log("Click Additional Costs button");
    await this.switchButton(this.additionalCostsButton, switchToOnState);
  }
  private async setAmount(
    input: Locator,
    { amount, pressTab = false }: AmountOptions,
  ): Promise<void> {
    await input.fill(amount);
    if (pressTab) await input.press("Tab");
  }
  /** Set allowance amount for united kingdom input. */
  async setUnitedKingdomAmountInput(
    options: AmountOptions,
  ): Promise<void> {
    console.log(`United Kingdom allowance amount input = ${options.amount}`);
    await this.setAmount(this.unitedKingdomAllowanceInput, options);
  }
  /** Set allowance amount for greater london input. */
  async setGreaterLondonAmountInput(
    options: AmountOptions,
  ): Promise<void> {
    console.log(`Greater London allowance amount input = ${options.amount}`);
    await this.setAmount(this.greaterLondonAllowanceInput, options);
  }
  /** Set allowance amount for germany/ireland input. */
  async setGermanyIrelandAmountInput(
    options: AmountOptions,
  ): Promise<void> {
    console.log(`Germany/Ireland allowance amount input = ${options.amount}`);
    await this.setAmount(this.germanyIrelandAllowanceInput, options);
  }
  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Booking Allowances page");
    await this.validatePageMarker(
      this.bookingAllowancesTitleLabel,
      "Booking Allowances",
    );
  }
  /** Validate the state (selected or not) of meals and extras buttons. */
  async validateMealsAndExtrasButtonsState(
    state: ButtonState = {},
  ): Promise<void> {
    console.log("Validate meals and extras buttons state");
    const buttons: Array<[keyof ButtonState, Locator]> = [
      ["premierInnBreakfast", this.premierInnBreakfastButton],
      ["continentalBreakfast", this.continentalBreakfastButton],
      ["mealDeal", this.mealDealButton],
      ["hubBreakfast", this.hubBreakfastButton],
      ["ultimateWiFi", this.ultimateWIFiButton],
      ["advanceRate", this.advanceRateButton],
      ["individualPaymentCards", this.individualPaymentButton],
      ["includeAlcohol", this.includeAlcoholButton],
      ["carParking", this.carParkingButton],
      ["additionalCosts", this.additionalCostsButton],
    ];
    for (const [key, button] of buttons)
      if (state[key] !== undefined)
        await expect(button, `${key} button state`).toHaveAttribute( "aria-checked", String(state[key]), );
  }
  private async validateAmount(
    input: Locator,
    error: Locator,
    { amount = "", isValid = true }: AmountValidationOptions,
  ): Promise<void> {
    await expect(input, "Allowance amount input value").toHaveValue(amount);
    if (!amount || !isValid)
      await expect(error, "Allowance amount error label").toHaveText( await IbStrings.INVALID_INPUT_VALUE.name, );
    else await expect(error, "Allowance amount error label").not.toBeVisible();
  }
  /** Validate United Kingdom allowance amount input. */
  async validateUnitedKingdomInput(
    options: AmountValidationOptions = {},
  ): Promise<void> {
    console.log("Validate United Kingdom allowance amount input");
    await this.validateAmount(
      this.unitedKingdomAllowanceInput,
      this.unitedKingdomErrorTooltipLabel,
      options,
    );
  }
  /** Validate Greater London allowance amount input. */
  async validateGreaterLondonInput(
    options: AmountValidationOptions,
  ): Promise<void> {
    console.log("Validate Greater London allowance amount input");
    await this.validateAmount(
      this.greaterLondonAllowanceInput,
      this.greaterLondonErrorTooltipLabel,
      options,
    );
  }
  /** Validate Germany / Ireland allowance amount input. */
  async validateGermanyIrelandInput(
    options: AmountValidationOptions,
  ): Promise<void> {
    console.log("Validate Germany / Ireland allowance amount input");
    await this.validateAmount(
      this.germanyIrelandAllowanceInput,
      this.germanyIrelandErrorTooltipLabel,
      options,
    );
  }
  /** Validate dinner allowance section. */
  async validateDinnerAllowanceSection({
    ukAmount,
    londonAmount,
    irelandAmount,
  }: {
    ukAmount: string;
    londonAmount: string;
    irelandAmount: string;
  }): Promise<void> {
    console.log("Validate Dinner allowance section");
    await expect( this.prepaidAllowancesSectionTitleLabel, "Pre-paid allowances section label", ).toHaveText(await IbStrings.PRE_PAID_ALLOWANCE.name);
    await expect( this.prepaidAllowancesSectionDescriptionLabel, "Pre-paid allowances description label", ).toHaveText(await IbStrings.SET_A_PRE_AUTHORIZED_BUDGET.name);
    await expect(this.unitedKingdomLabel, "United Kingdom label").toHaveText( await IbStrings.UNITED_KINGDOM_EXCLUDING_LONDON.name, );
    await this.validateUnitedKingdomInput({ amount: ukAmount });
    await expect(this.greaterLondonLabel, "Greater London label").toHaveText( await IbStrings.GREATER_LONDON.name, );
    await this.validateGreaterLondonInput({ amount: londonAmount });
    await expect( this.germanyIrelandLabel, "Germany / Ireland label", ).toHaveText(await IbStrings.GERMANY_IRELAND.name);
    await this.validateGermanyIrelandInput({ amount: irelandAmount });
  }
  /** Validate Booking allowances page labels and page content. */
  async validateBookingAllowancesLabelsAndContent(
    companyBookingAllowances: BookingAllowances,
  ): Promise<void> {
    console.log("Validate Booking allowances labels and content");
    await expect( this.bookingAllowancesTitleLabel, "Booking allowances page title", ).toHaveText(await IbStrings.BOOKING_ALLOWANCES_TITLE.name);
    await expect( this.bookingAllowancesDescriptionLabel, "Booking allowances description", ).toHaveText(await IbStrings.SET_OPTIONS_AND_EXTRAS.name);
    await expect( this.setBookingOptionsSectionTitleLabel, "Set booking options title", ).toHaveText(await IbStrings.SET_BOOKING_OPTIONS.name);
    await expect( this.setBookingOptionsSectionDescriptionLabel, "Set booking options description", ).toHaveText(await IbStrings.CHOOSE_WHICH_OPTIONS_ARE_AVAILABLE.name);
    await expect(this.mealsLabel, "Meals title").toHaveText( await IbStrings.MEALS.name, );
    await expect(this.otherExtrasLabel, "Other extras label").toHaveText( await IbStrings.OTHER_EXTRAS.name, );
    await expect( this.advanceRateSectionLabel, "Advance rate section label", ).not.toBeVisible();
    await expect( this.individualPaymentSectionLabel, "Individual payment cards section label", ).toHaveText(await IbStrings.INDIVIDUAL_PAYMENT_CARDS.name);
    await expect( this.individualPaymentSectionDescriptionLabel, "Individual payment cards description", ).toHaveText(await IbStrings.INDIVIDUAL_PAYMENT_CARDS_DESCRIPTION.name);
    await expect(this.selectExtrasLabel, "Select extras label").toHaveText( await IbStrings.SELECT_EXTRAS.name, );
    await this.validateDinnerAllowanceSection({
      ukAmount: String(companyBookingAllowances.maxDinnerBudgets.uKWide.amount),
      londonAmount: String(
        companyBookingAllowances.maxDinnerBudgets.greaterLondon.amount,
      ),
      irelandAmount: String(
        companyBookingAllowances.maxDinnerBudgets.ireland.amount,
      ),
    });
    const allowed = companyBookingAllowances.upsellItemsAllowed;
    await this.validateMealsAndExtrasButtonsState({
      premierInnBreakfast: allowed.includes(
        String(MealsAndExtrasOptions.PI_BREAKFAST.value),
      ),
      continentalBreakfast: allowed.includes(
        String(MealsAndExtrasOptions.CONTINENTAL_BREAKFAST.value),
      ),
      mealDeal: allowed.includes(String(MealsAndExtrasOptions.MEAL_DEAL.value)),
      hubBreakfast: allowed.includes(
        String(MealsAndExtrasOptions.HUB_BREAKFAST.value),
      ),
      ultimateWiFi: [
        MealsAndExtrasOptions.ULTIMATE_WIFI1,
        MealsAndExtrasOptions.ULTIMATE_WIFI2,
        MealsAndExtrasOptions.ULTIMATE_WIFI3,
      ].some((option) => allowed.includes(String(option.value))),
      individualPaymentCards: companyBookingAllowances.allowIndividualCards,
      includeAlcohol: companyBookingAllowances.allowAlcohol,
      carParking: companyBookingAllowances.allowCarParking,
      additionalCosts: companyBookingAllowances.allowAdditionalCosts,
    });
    await expect(this.saveUpdatesButton, "Save updates button").toHaveText( await IbStrings.SAVE_UPDATES.name, );
  }
}
