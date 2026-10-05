import { expect, type Locator } from "@playwright/test";
import {
  ReviewChangesModalComponent,
  ToastNotificationSectionComponent,
} from "../../components/pib";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

interface SwitchOptions {
  switchToOnState?: boolean;
}
interface AmountOptions {
  amount: string;
  pressTab?: boolean;
}
interface AlertPrices {
  ukAmount?: string;
  londonAmount?: string;
  irelandAmount?: string;
}
interface Hotel {
  id: string;
  name: string;
}
interface AlertState {
  sameDayBooking?: boolean;
  weekendArrival?: boolean;
  partWeekend?: boolean;
}
interface CompanyBookingAlerts {
  dayOfArrival: boolean;
  weekendArrival: boolean;
  passThroughWeekend: boolean;
  bookingAlertHotels: string[];
  rateCaps: {
    uKWide: { amount: number };
    greaterLondon: { amount: number };
    ireland: { amount: number };
  };
  recipientEmailAddresses: string[];
  frequency: string;
}

/** InnBusiness application > Manage > Booking Alerts */
export class BookingAlertsPage extends BasePibPage {
  readonly url = "manage/alerts";
  // ######## UI elements/properties ########
  readonly bookingAlertsTitleLabel: Locator = this.page.getByTestId( "AlertsPage-container-title", );
  readonly bookingAlertsDescriptionLabel: Locator = this.page.getByTestId( "AlertsPage-container-subtitle", );
  readonly bookingAlertsLabel: Locator = this.page.getByTestId( "AlertsPage-alerts-title", );
  readonly bookingAlertsSubtitleLabel: Locator = this.page.getByTestId( "AlertsPage-alerts-subtitle", );
  readonly sameDayBookingButton: Locator = this.page.getByTestId( "AlertToggles-switch-sameDayBooking", );
  readonly weekendArrivalButton: Locator = this.page.getByTestId( "AlertToggles-switch-weekendArrival", );
  readonly partWeekendBookingButton: Locator = this.page.getByTestId( "AlertToggles-switch-partWeekendBooking", );
  readonly hotelAlertsLabel: Locator = this.page.getByTestId( "AlertsPage-hotelAlerts-title", );
  readonly hotelAlertsSubtitleLabel: Locator = this.page.getByTestId( "AlertsPage-hotelAlerts-subtitle", );
  readonly searchHotelInput: Locator = this.page.getByTestId("undefined-Input");
  readonly hotelDropdownAlertsContainer: Locator = this.page.getByTestId( "HotelAlertsSelector-search-results", );
  readonly hotelDropdownAlertsOptions: Locator = this.page.locator( 'div[data-testid*="HotelAlertsSelector-search-result-"]', );
  readonly savedHotelAlertsOptions: Locator = this.page.locator( '//div[@data-testid="HotelAlertsSelector-selected-list-container"]//li', );
  readonly priceAlertsLabel: Locator = this.page.getByTestId( "AlertsPage-priceAlerts-title", );
  readonly priceAlertsSubtitleLabel: Locator = this.page.getByTestId( "AlertsPage-priceAlerts-subtitle", );
  readonly unitedKingdomLabel: Locator = this.page.getByTestId( "PriceAlertsFormWrapper-PriceInputs-uk-title", );
  readonly greaterLondonLabel: Locator = this.page.getByTestId( "PriceAlertsFormWrapper-PriceInputs-london-title", );
  readonly germanyIrelandLabel: Locator = this.page.getByTestId( "PriceAlertsFormWrapper-PriceInputs-eu-title", );
  readonly unitedKingdomErrorTooltipLabel: Locator = this.page.getByTestId( "PriceAlertsFormWrapper-PriceInputs-unitedKingdom-Error-Tooltip", );
  readonly greaterLondonErrorTooltipLabel: Locator = this.page.getByTestId( "PriceAlertsFormWrapper-PriceInputs-greaterLondon-Error-Tooltip", );
  readonly germanyIrelandErrorTooltipLabel: Locator = this.page.getByTestId( "PriceAlertsFormWrapper-PriceInputs-germanyIreland-Error-Tooltip", );
  readonly unitedKingdomAlertInput: Locator = this.page.getByTestId( "PriceAlertsFormWrapper-PriceInputs-unitedKingdom-Form-Input", );
  readonly greaterLondonAlertInput: Locator = this.page.getByTestId( "PriceAlertsFormWrapper-PriceInputs-greaterLondon-Form-Input", );
  readonly germanyIrelandAlertInput: Locator = this.page.getByTestId( "PriceAlertsFormWrapper-PriceInputs-germanyIreland-Form-Input", );
  readonly alertRecipientsLabel: Locator = this.page.getByTestId( "AlertsPage-recipients-title", );
  readonly alertRecipientsSubtitleLabel: Locator = this.page.getByTestId( "AlertsPage-recipients-subtitle", );
  readonly companyEmailAddressInput: Locator = this.page.getByTestId( "AlertRecipients-people-picker-Form-Input", );
  readonly companyEmailDropdownList: Locator = this.page.getByTestId( "AlertRecipients-people-picker-IB-Form-People-Picker-Dropdown", );
  readonly companyEmailSuggestionButton: Locator = this.companyEmailDropdownList.locator("button");
  readonly alertsRecipientList: Locator = this.page.locator( '//div[@data-testid="AlertRecipients-selected-list"]//span', );
  readonly companyEmailInputErrorTooltipLabel: Locator = this.page.getByTestId( "AlertRecipients-people-picker-Error-Tooltip", );
  readonly setFrequencyLabel: Locator = this.page.getByTestId( "AlertsPage-frequency-title", );
  readonly setFrequencySubtitleLabel: Locator = this.page.getByTestId( "AlertsPage-frequency-subtitle", );
  readonly noAlertsOption: Locator = this.page.locator("#no-alerts");
  readonly alertImmediatelyOption: Locator = this.page.locator("#immediately");
  readonly alertDailyOption: Locator = this.page.locator("#daily");
  readonly alertWeeklyOption: Locator = this.page.locator("#weekly");
  readonly alertMonthlyOption: Locator = this.page.locator("#monthly");
  readonly discardChargesButton: Locator = this.page.getByTestId( "AlertsPage-discard-button", );
  readonly saveUpdatesButton: Locator = this.page.getByTestId( "AlertsPage-save-button", );
  getHotelAlertDeleteButtonByHotelId(hotelId: string): Locator {
    return this.page.getByTestId(
      `HotelAlertsSelector-remove-button-${hotelId}`,
    );
  }
  getHotelAlertDeleteButtonIndex(index: number): Locator {
    return this.page.locator(
      `//div[@data-testid="HotelAlertsSelector-selected-list-container"]//li[${index + 1}]//following-sibling::button`,
    );
  }
  getHotelOptionByHotelId(hotelId: string): Locator {
    return this.page.getByTestId(
      `HotelAlertsSelector-search-result-${hotelId}`,
    );
  }
  getDynamicEmployeeLabelByEmail(employeeEmail: string): Locator {
    return this.page.locator(
      `//div[@data-testid="AlertRecipients-container"]//span[contains(text(),"${employeeEmail}")]`,
    );
  }
  getRecipientRemoveButtonByEmail(employeeEmail: string): Locator {
    return this.page.locator(
      `//div[@data-testid="AlertRecipients-container"]//span[contains(text(),"${employeeEmail}")]/following-sibling::button`,
    );
  }
  /** Returns suggested hotels list. */
  async getSuggestedHotelsList(): Promise<
    string[]
  > {
    console.log("Get suggested hotels list");
    return this.hotelDropdownAlertsOptions.allTextContents();
  }
  readonly toastNotificationSection = new ToastNotificationSectionComponent();
  readonly reviewChangesModal = new ReviewChangesModalComponent();
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
  /** Click Discard changes button. */
  async clickDiscardChangesButton(): Promise<void> {
    console.log("Click Discard changes button");
    await this.discardChargesButton.click();
  }
  private async switchButton(
    button: Locator,
    switchToOnState: boolean,
  ): Promise<void> {
    if (
      ((await button.getAttribute("aria-checked")) === "true") !==
      switchToOnState
    )
      await button.click();
  }
  /** Click Same Day booking switch button. */
  async clickSameDayBookingButton({
    switchToOnState = true,
  }: SwitchOptions = {}): Promise<void> {
    console.log("Click Same day booking button");
    await this.switchButton(this.sameDayBookingButton, switchToOnState);
  }
  /** Click Arrive on a weekend switch button. */
  async clickWeekendArrivalButton({
    switchToOnState = true,
  }: SwitchOptions = {}): Promise<void> {
    console.log("Click Arrive on a weekend night button");
    await this.switchButton(this.weekendArrivalButton, switchToOnState);
  }
  /** Click part-weekend booking switch button. */
  async clickPartWeekendBookingButton({
    switchToOnState = true,
  }: SwitchOptions = {}): Promise<void> {
    console.log("Click Part-weekend booking button");
    await this.switchButton(this.partWeekendBookingButton, switchToOnState);
  }
  /** Set Hotel alerts location. */
  async setHotelAlertsLocation({
    searchedText,
    pressTab = false,
  }: {
    searchedText: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Set Hotel alerts location=${searchedText}`);
    await this.searchHotelInput.fill(searchedText);
    if (pressTab) await this.searchHotelInput.press("Tab");
    await expect( this.hotelDropdownAlertsContainer, "Hotel alerts dropdown", ).toBeVisible();
  }
  /** Click Hotel option by hotelId. */
  async clickHotelOptionByHotelId({
    hotelId,
  }: {
    hotelId: string;
  }): Promise<void> {
    console.log(`Click Hotel option=${hotelId}`);
    await this.hotelAlertsLabel.scrollIntoViewIfNeeded();
    await this.getHotelOptionByHotelId(hotelId).click();
  }
  private async setAmount(
    input: Locator,
    { amount, pressTab = false }: AmountOptions,
  ): Promise<void> {
    await input.fill(amount);
    if (pressTab) await input.press("Tab");
  }
  /** Set alert amount for united kingdom input. */
  async setUnitedKingdomAmountInput(
    options: AmountOptions,
  ): Promise<void> {
    console.log(`United Kingdom alert amount input = ${options.amount}`);
    await this.setAmount(this.unitedKingdomAlertInput, options);
  }
  /** Set alert amount for greater london input. */
  async setGreaterLondonAmountInput(
    options: AmountOptions,
  ): Promise<void> {
    console.log(`Greater London alert amount input = ${options.amount}`);
    await this.setAmount(this.greaterLondonAlertInput, options);
  }
  /** Set alert amount for germany/ireland input. */
  async setGermanyIrelandAmountInput(
    options: AmountOptions,
  ): Promise<void> {
    console.log(`Germany/Ireland alert amount input = ${options.amount}`);
    await this.setAmount(this.germanyIrelandAlertInput, options);
  }
  /** Set company email recipient input. */
  async setCompanyEmailRecipientInput({
    searchedText,
    pressTab = false,
  }: {
    searchedText: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Company alert recipient=${searchedText}`);
    await this.companyEmailAddressInput.fill(searchedText);
    if (pressTab) await this.companyEmailAddressInput.press("Tab");
  }
  /** Click to select frequency alert option. */
  async selectFrequencyAlertOption({
    alertOption,
  }: {
    alertOption: { name: string | Promise<string> };
  }): Promise<void> {
    console.log("Click frequency alert option");
    const name = await alertOption.name;
    const optionByName: Record<string, Locator> = {
      [await IbStrings.NO_ALERTS.name]: this.noAlertsOption,
      [await IbStrings.IMMEDIATELY.name]: this.alertImmediatelyOption,
      [await IbStrings.DAILY.name]: this.alertDailyOption,
      [await IbStrings.WEEKLY.name]: this.alertWeeklyOption,
      [await IbStrings.MONTHLY.name]: this.alertMonthlyOption,
    };
    const option = optionByName[name];
    if (!option) throw new Error(`Option with ${name} is not available`);
    await option.click();
  }
  /** Search to add alert recipient by email. */
  async searchAndAddAlertRecipientByEmail({
    employeeEmail,
    pressTab = false,
  }: {
    employeeEmail: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Search and add alert recipient by email=${employeeEmail}`);
    await this.setCompanyEmailRecipientInput({
      searchedText: employeeEmail,
      pressTab,
    });
    await expect( this.companyEmailDropdownList, "Company email dropdown", ).toBeVisible();
    await expect( this.companyEmailSuggestionButton, "Company email suggestion", ).toContainText(employeeEmail);
    await this.getDynamicEmployeeLabelByEmail(employeeEmail).click();
  }
  /** Remove alert recipient by email. */
  async removeAlertRecipientByEmail({
    employeeEmail,
  }: {
    employeeEmail: string;
  }): Promise<void> {
    console.log(`Remove alert recipient by email=${employeeEmail}`);
    await this.alertRecipientsLabel.scrollIntoViewIfNeeded();
    await this.getRecipientRemoveButtonByEmail(employeeEmail).click();
  }
  /** Remove hotel alerts by a list of hotel Ids. */
  async removeHotelAlertByHotelId({
    hotelId = [],
  }: { hotelId?: string[] } = {}): Promise<void> {
    console.log("Remove hotel alerts by hotel ID");
    for (const hotel of hotelId) {
      await this.hotelAlertsLabel.scrollIntoViewIfNeeded();
      await this.getHotelAlertDeleteButtonByHotelId(hotel).click();
    }
  }
  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Booking Alerts page");
    await this.validatePageMarker(
      this.bookingAlertsTitleLabel,
      "Booking Alerts",
    );
  }
  /** Validate the hotels suggested for hotel alerts field. */
  async validateSuggestedHotels({
    apiHotelsList,
  }: {
    apiHotelsList: { properties: Array<{ suggestion: string }> };
  }): Promise<void> {
    console.log("Validate suggested hotels");
    const expected = apiHotelsList.properties.map((hotel) => hotel.suggestion);
    const actual = await this.getSuggestedHotelsList();
    await expect( actual.length, "Location field suggested hotels list maximum length", ).toBeLessThanOrEqual(5);
    await expect(actual, "Suggested hotels list").toEqual(expected);
  }
  private async validateAmount(
    input: Locator,
    error: Locator,
    { amount = "0", isValid = true }: { amount?: string; isValid?: boolean },
  ): Promise<void> {
    await expect(input, "Alert amount input value").toHaveValue(amount);
    if (!amount || !isValid)
      await expect(error, "Alert amount error label").toHaveText( await IbStrings.INVALID_INPUT_VALUE.name, );
    else await expect(error, "Alert amount error label").not.toBeVisible();
  }
  /** Validate United Kingdom alert amount input. */
  async validateUnitedKingdomInput(
    options: { amount?: string; isValid?: boolean } = {},
  ): Promise<void> {
    console.log("Validate United Kingdom alert amount input");
    await this.validateAmount(
      this.unitedKingdomAlertInput,
      this.unitedKingdomErrorTooltipLabel,
      options,
    );
  }
  /** Validate Greater London alert amount input. */
  async validateGreaterLondonInput(
    options: { amount?: string; isValid?: boolean } = {},
  ): Promise<void> {
    console.log("Validate Greater London alert amount input");
    await this.validateAmount(
      this.greaterLondonAlertInput,
      this.greaterLondonErrorTooltipLabel,
      options,
    );
  }
  /** Validate Germany / Ireland alert amount input. */
  async validateGermanyIrelandInput(
    options: { amount?: string; isValid?: boolean } = {},
  ): Promise<void> {
    console.log("Validate Germany / Ireland alert amount input");
    await this.validateAmount(
      this.germanyIrelandAlertInput,
      this.germanyIrelandErrorTooltipLabel,
      options,
    );
  }
  /** Validate alert recipients input. */
  async validateAlertRecipientsInput({
    searchedText = "",
    errorDisplayed = false,
    isDisabled = false,
  }: {
    searchedText?: string;
    errorDisplayed?: boolean;
    isDisabled?: boolean;
  } = {}): Promise<void> {
    console.log("Validate alert recipients input");
    await expect( this.companyEmailAddressInput, "Company email input value", ).toHaveValue(searchedText);
    if (errorDisplayed)
      await expect( this.companyEmailInputErrorTooltipLabel, "Alert recipients error tooltip", ).toHaveText( (await IbStrings.NO_EMPLOYEE_FOUND.name) .replace("searchTerm", searchedText) .replace(/%/g, "'"), );
    else
      await expect( this.companyEmailInputErrorTooltipLabel, "Alert recipients error tooltip", ).not.toBeVisible();
    if (isDisabled)
      await expect( this.companyEmailAddressInput, "Alert recipients company address input disabled", ).toBeDisabled();
  }
  /** Validate added alert recipients list length. */
  async validateAddedRecipientsListLength(
    expectedLength: number,
  ): Promise<void> {
    console.log("Validate added alert recipients list length");
    await expect( this.alertsRecipientList, "Added alert recipients list length", ).toHaveCount(expectedLength);
  }
  /** Validate Alert recipients section. */
  async validateAlertRecipientsSection(
    expectedRecipientsList: string[] = [],
  ): Promise<void> {
    console.log("Validate Alert recipients section");
    await expect( this.alertRecipientsLabel, "Alerts recipients title", ).toHaveText(await IbStrings.ALERT_RECIPIENTS.name);
    await expect( this.alertRecipientsSubtitleLabel, "Alert recipients description", ).toHaveText(await IbStrings.ADD_EMAIL_ADDRESSES_FOR.name);
    await this.validateAddedRecipientsListLength(expectedRecipientsList.length);
  }
  /** Validate Booking Alerts button state. */
  async validateBookingAlertsButtonsState(
    state: AlertState = {},
  ): Promise<void> {
    console.log("Validate Booking Alerts button state");
    for (const [button, value, description] of [
      [
        this.sameDayBookingButton,
        state.sameDayBooking ?? false,
        "Same day booking",
      ],
      [
        this.weekendArrivalButton,
        state.weekendArrival ?? false,
        "Weekend arrival",
      ],
      [
        this.partWeekendBookingButton,
        state.partWeekend ?? false,
        "Part weekend booking",
      ],
    ] as const)
      await expect(button, `${description} state`).toHaveAttribute( "aria-checked", String(value), );
  }
  /** Validate Booking alerts section. */
  async validateBookingAlertsSection(
    state: AlertState = {},
  ): Promise<void> {
    console.log("Validate Booking alerts section");
    await expect( this.bookingAlertsLabel, "Booking alerts section title", ).toHaveText(await IbStrings.BOOKING_ALERTS.name);
    await expect( this.bookingAlertsSubtitleLabel, "Booking alerts section description", ).toHaveText(await IbStrings.SELECT_WHEN_EMAIL_ALERTS_WILL_BE_SENT.name);
    await this.validateBookingAlertsButtonsState(state);
  }
  /** Validate hotel alert list. */
  async validateHotelAlertList(
    hotelList: Hotel[] = [],
  ): Promise<void> {
    console.log("Validate hotel alert list");
    await expect( this.savedHotelAlertsOptions, "Hotel alert list length", ).toHaveCount(hotelList.length);
    for (const [index, hotel] of hotelList.entries()) {
      await expect( this.getHotelAlertDeleteButtonIndex(index), "Hotel alert delete button", ).toHaveText(await IbStrings.DELETE.name);
      await expect( this.savedHotelAlertsOptions.nth(index).locator("div"), "Hotel alert name", ).toHaveText(hotel.name);
    }
  }
  /** Validate Hotel alerts section. */
  async validateHotelAlertsSection(
    hotelList: Hotel[] = [],
  ): Promise<void> {
    console.log("Validate Hotel alerts section");
    await expect( this.hotelAlertsLabel, "Hotel alerts section title", ).toHaveText(await IbStrings.HOTEL_ALERTS.name);
    await expect( this.hotelAlertsSubtitleLabel, "Hotel alerts section description", ).toHaveText(await IbStrings.HOTEL_ALERTS_DESCRIPTION.name);
    await expect(this.searchHotelInput, "Hotel alerts input value").toHaveValue( "", );
    await this.validateHotelAlertList(hotelList);
  }
  /** Validate price alerts section. */
  async validatePriceAlertsSection(
    alertPrices: AlertPrices = {},
  ): Promise<void> {
    console.log("Validate Price alerts section");
    await expect( this.priceAlertsLabel, "Price alerts section title", ).toHaveText(await IbStrings.PRICE_ALERTS.name);
    await expect( this.priceAlertsSubtitleLabel, "Price alerts section description", ).toHaveText(await IbStrings.SET_EMAIL_ALERTS.name);
    await expect(this.unitedKingdomLabel, "United Kingdom label").toHaveText( await IbStrings.UNITED_KINGDOM_EXCLUDING_LONDON.name, );
    await this.validateUnitedKingdomInput({ amount: alertPrices.ukAmount });
    await expect(this.greaterLondonLabel, "Greater London label").toHaveText( await IbStrings.GREATER_LONDON.name, );
    await this.validateGreaterLondonInput({ amount: alertPrices.londonAmount });
    await expect(this.germanyIrelandLabel, "Germany Ireland label").toHaveText( await IbStrings.GERMANY_IRELAND.name, );
    await this.validateGermanyIrelandInput({
      amount: alertPrices.irelandAmount,
    });
  }
  /** Validate Booking frequency section. */
  async validateBookingFrequencySection(
    selectedFrequency: string | Promise<string> = IbStrings.NO_ALERTS.name,
  ): Promise<void> {
    console.log("Validate Booking frequency alerts section");
    const frequency = await selectedFrequency;
    await expect( this.setFrequencyLabel, "Set Frequency section title", ).toHaveText(await IbStrings.SET_FREQUENCY.name);
    await expect( this.setFrequencySubtitleLabel, "Set Frequency section description", ).toHaveText(await IbStrings.SELECT_WHEN_EMAIL_ALERTS_ARE_SENT.name);
    for (const [option, expected, label] of [
      [this.noAlertsOption, await IbStrings.NO_ALERTS.name, "No alerts"],
      [
        this.alertImmediatelyOption,
        await IbStrings.IMMEDIATELY.name,
        "Immediately",
      ],
      [this.alertDailyOption, await IbStrings.DAILY.name, "Daily"],
      [this.alertWeeklyOption, await IbStrings.WEEKLY.name, "Weekly"],
      [this.alertMonthlyOption, await IbStrings.MONTHLY.name, "Monthly"],
    ] as const)
      await expect(option, `${label} frequency state`).toHaveAttribute( "aria-checked", String(frequency === expected), );
  }
  /** Validate Booking alerts page labels and page content expected and API values. */
  async validateBookingAlertsLabelsAndContent({
    companyBookingAlerts,
    state = {},
    alertPrices = {},
    selectedFrequency = IbStrings.NO_ALERTS.name,
    hotelList = [],
    expectedRecipientsList = [],
  }: {
    companyBookingAlerts: CompanyBookingAlerts;
    state?: AlertState;
    alertPrices?: AlertPrices;
    selectedFrequency?: string | Promise<string>;
    hotelList?: Hotel[];
    expectedRecipientsList?: string[];
  }): Promise<void> {
    console.log("Validate Booking alerts labels and content");
    await expect( this.bookingAlertsTitleLabel, "Booking alerts page title", ).toHaveText(await IbStrings.BOOKING_ALERTS.name);
    await expect( this.bookingAlertsDescriptionLabel, "Booking alerts description", ).toHaveText(await IbStrings.RECEIVE_EMAIL_ALERTS.name);
    await this.validateBookingAlertsSection(state);
    await expect( companyBookingAlerts.dayOfArrival, "Same day booking API state", ).toBe(state.sameDayBooking ?? false);
    await expect( companyBookingAlerts.weekendArrival, "Weekend arrival API state", ).toBe(state.weekendArrival ?? false);
    await expect( companyBookingAlerts.passThroughWeekend, "Part weekend API state", ).toBe(state.partWeekend ?? false);
    await this.validateHotelAlertsSection(hotelList);
    await expect( [...companyBookingAlerts.bookingAlertHotels].sort(), "Alert hotel IDs", ).toEqual(hotelList.map((hotel) => hotel.id).sort());
    await this.validatePriceAlertsSection(alertPrices);
    await expect( String(companyBookingAlerts.rateCaps.uKWide.amount), "United Kingdom API alert amount", ).toBe(alertPrices.ukAmount ?? "0");
    await expect( String(companyBookingAlerts.rateCaps.greaterLondon.amount), "Greater London API alert amount", ).toBe(alertPrices.londonAmount ?? "0");
    await expect( String(companyBookingAlerts.rateCaps.ireland.amount), "Ireland API alert amount", ).toBe(alertPrices.irelandAmount ?? "0");
    await this.validateAlertRecipientsSection(expectedRecipientsList);
    await expect( [...companyBookingAlerts.recipientEmailAddresses].sort(), "Alert recipient list", ).toEqual([...expectedRecipientsList].sort());
    await this.validateBookingFrequencySection(selectedFrequency);
    await expect(this.saveUpdatesButton, "Save updates button").toHaveText( await IbStrings.SAVE_UPDATES.name, );
    await expect( this.discardChargesButton, "Discard changes button", ).toHaveText(await IbStrings.DISCARD_CHANGES.name);
  }
}
