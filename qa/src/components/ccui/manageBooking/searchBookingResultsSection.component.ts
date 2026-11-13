import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { CcuiComponent } from '../baseCcui.component';
import { CalendarComponent } from '../../shared/calendar.component';

type SearchReservationInfo = {
  reservationDetails?: {
    basketReference?: string;
    hotelId?: string;
    reservations?: Array<{ roomStay?: { departureDate?: string } }>;
  };
  guestDetails?: {
    booker?: {
      firstName?: string;
      address?: { postalCode?: string };
      emailAddress?: string;
      mobile?: string;
    };
  };
};

/** Search Booking Results section that is part of the CCUI Manage Booking page. */
export class SearchBookingResultsSectionComponent extends CcuiComponent {
  static readonly HOTEL_NAME_CSS = 'div[data-testid="SearchBookingsPage-HotelName"]';
  static readonly HOTEL_SUGGESTIONS_TABLE_CSS = 'section[data-testid="SearchBookingsLocationPicker-autocompleteList"]';

  // ######## UI elements/properties ########

  readonly searchBookingResultsTitleLabel: Locator = this.page.locator('p[data-testid="SearchBookingsPage-Header"]');
  readonly searchBookingResultsDescriptionLabel: Locator = this.page.locator('p[data-testid="SearchBookingsPage-Description"]');
  readonly bookingReferenceInput: Locator = this.page.locator('input[data-testid="input-bookingReference"]');
  readonly referenceLabel: Locator = this.page.locator('label[data-testid="input-bookingReference-label"]');
  readonly bookingReferenceErrorMessageLabel: Locator = this.page.locator('//div[@data-testid = "input-bookingReference-FormErrorMessage"]');
  readonly bookingSurnameInput: Locator = this.page.locator('input[data-testid="input-bookerLastName"]');
  readonly surnameLabel: Locator = this.page.locator('label[data-testid="input-bookingSurname-label"]');
  readonly bookingSurnameErrorMessageLabel: Locator = this.page.locator('//div[@data-testid = "input-bookerLastName-FormErrorMessage"]');
  readonly arrivalDateContainer: Locator = this.page.locator('div[data-testid="SearchBookingsPage-ArrivalDate-Container"]');
  readonly calendarMonthContainer: Locator = this.page.locator('//div[@class="react-datepicker__month-container"]');
  readonly arrivalDateInput: Locator = this.page.locator('input[data-testid="SingleDatePicker"]');
  readonly toggleSearchCriteriaLink: Locator = this.page.locator('div[data-testid="SearchBookingsPage-ExtendedSearchCriteria"] a');
  readonly guestSurnameInput: Locator = this.page.locator('input[data-testid="input-guestLastName"]');
  readonly bookerPostcodeInput: Locator = this.page.locator('input[data-testid="input-bookerPostcode"]');
  readonly hotelNameInput: Locator = this.page.locator(`${SearchBookingResultsSectionComponent.HOTEL_NAME_CSS} input[data-testid="SearchBookingsLocationPicker-locationPlaceholder"]`);
  readonly hotelSuggestionsTable: Locator = this.page.locator(SearchBookingResultsSectionComponent.HOTEL_SUGGESTIONS_TABLE_CSS);
  readonly hotelSuggestionsList: Locator = this.page.locator(`${SearchBookingResultsSectionComponent.HOTEL_SUGGESTIONS_TABLE_CSS} > div`);
  readonly hotelSuggestionsLabels: Locator = this.page.locator(`${SearchBookingResultsSectionComponent.HOTEL_SUGGESTIONS_TABLE_CSS} > div span`);
  readonly hotelNameDeleteIcon: Locator = this.page.locator(`${SearchBookingResultsSectionComponent.HOTEL_NAME_CSS} div[data-testid="SearchBookingsLocationPicker-inputRight"]`);
  readonly hotelLocationInput: Locator = this.page.locator('div[data-testid="SearchBookingsPage-HotelLocation"] input[data-testid="SearchBookingsLocationPicker-locationPlaceholder"]');
  readonly emailAddressInput: Locator = this.page.locator('input[data-testid="input-bookerEmail"]');
  readonly telephoneNumberInput: Locator = this.page.locator('input[data-testid="SearchBookingsPage-TelephoneNumber-phoneNumber"]');
  readonly cancellationDateInput: Locator = this.page.locator('div[data-testid="SearchBookingsPage-CancellationDate-datePicker"] input[data-testid="SingleDatePicker"]');
  readonly companyNameInput: Locator = this.page.locator('input[data-testid="input-companyName"]');
  readonly thirdPartyBookingReferenceInput: Locator = this.page.locator('input[data-testid="input-thirdPartyBookingReferenceNumber"]');
  readonly searchForBookingButton: Locator = this.page.locator('button[data-testid="SearchBookingsPage-Submit-Button"]');
  readonly clearButton: Locator = this.page.locator('div[data-testid="SearchBookingsPage-Reset-Button"]');
  readonly loadingMessageLabel: Locator = this.page.locator('//p[@data-testid="SearchBookingsPage-Table-loading-message"]');

  // ######## UI components ########

  readonly datePicker = this.arrivalDateInput;
  readonly calendar = new CalendarComponent();

  /** Get selected hotel search value from the input, or the visible selected hotel label. */
  async getSelectedHotelNameSearchValue(): Promise<string> { console.log('Get selected hotel search value'); const inputValue = await this.hotelNameInput.inputValue().catch(() => ''); if (inputValue) return inputValue; const hotelNameFieldText = await this.page.locator(SearchBookingResultsSectionComponent.HOTEL_NAME_CSS).innerText().catch(() => ''); const placeholderAttribute = await this.hotelNameInput.getAttribute('placeholder'); return [hotelNameFieldText, placeholderAttribute].filter(Boolean).join(' ').replace(/\s+/g, ' ').trim(); }

  // ######## UI actions/navigation ########
  /**
   * Create the search object input from reservation information.
   * @param reservationInfo Reservation information used to populate search fields.
   */
  async createSearchObjectInput(reservationInfo: SearchReservationInfo): Promise<Record<string, unknown>> {
    console.log('Create search object input');
    const arrivalDate = new Date();
    arrivalDate.setDate(arrivalDate.getDate() + 2);
    return {
      bookingReferenceInput: reservationInfo.reservationDetails?.basketReference,
      bookingSurnameInput: reservationInfo.guestDetails?.booker?.firstName,
      guestSurnameInput: reservationInfo.guestDetails?.booker?.firstName,
      bookerPostcodeInput: reservationInfo.guestDetails?.booker?.address?.postalCode,
      hotelNameInput: reservationInfo.reservationDetails?.hotelId,
      hotelLocationInput: '',
      emailAddressInput: reservationInfo.guestDetails?.booker?.emailAddress,
      telephoneNumberInput: reservationInfo.guestDetails?.booker?.mobile,
      cancellationDateInput: reservationInfo.reservationDetails?.reservations?.[0]?.roomStay?.departureDate,
      companyNameInput: '',
      thirdPartyBookingReferenceInput: '',
      arrivalDateInput: arrivalDate,
    };
  }
  /** Click ArrivalDateField. */
  async clickArrivalDateField(): Promise<void> { console.log('Click ArrivalDateField'); await this.arrivalDateInput.click(); }
  /** Click on Extend Search Criteria link. */
  async clickToggleSearchCriteriaLink(): Promise<void> { console.log('Click on Extend Search Criteria link'); await this.toggleSearchCriteriaLink.click(); }
  /**
   * Set value on Booking Reference Input.
   * @param value Booking reference value.
   * @param pressTab Whether to move focus away after filling.
   */
  async setBookingReferenceInput({ value, pressTab = true }: { value: string; pressTab?: boolean }): Promise<void> { console.log(`Set Booking Reference input=${value}`); await this.fillInput(this.bookingReferenceInput, value, pressTab); }
  /**
   * Check value on Booking Reference Input.
   * @param value Expected booking reference value.
   */
  async checkBookingReferenceInputValue(value: string): Promise<void> { console.log(`Check Booking Reference input=${value}`); await expect(this.bookingReferenceInput, 'Booking Reference Input not as expected').toHaveValue(value); }
  /** Clear value of Booking Reference Input. */
  async clearBookingReferenceInput(): Promise<void> { console.log('Clear Booking Reference input'); await this.bookingReferenceInput.fill(''); }
  /**
   * Set value on Booking Surname Input.
   * @param value Booking surname value.
   * @param pressTab Whether to move focus away after filling.
   */
  async setBookingSurnameInput({ value, pressTab = true }: { value: string; pressTab?: boolean }): Promise<void> { console.log(`Set Booking Surname input=${value}`); await this.fillInput(this.bookingSurnameInput, value, pressTab); }
  /** Click Hotel Name input. */
  async clickHotelNameInput(): Promise<void> { console.log('Click Hotel Name input'); await this.hotelNameInput.click(); }
  /**
   * Set Hotel Name input.
   * @param value Hotel name value.
   * @param pressTab Whether to move focus away after filling.
   */
  async setHotelNameInput({ value, pressTab = true, suggestionsDisplayed = true }: { value: string; pressTab?: boolean; suggestionsDisplayed?: boolean }): Promise<void> { console.log(`Set Hotel Name input=${value}`); await this.fillInput(this.hotelNameInput, value, pressTab); await this.validateDisplayState(this.hotelSuggestionsTable, 'Hotel suggestions table', suggestionsDisplayed); }
  /**
   * Select Hotel Name from dropdown.
   * @param hotelName Hotel name to select.
   */
  async selectHotelNameFromDropdown(hotelName: string): Promise<void> { console.log(`Select Hotel Name=${hotelName}`); await this.hotelSuggestionsLabels.filter({ hasText: hotelName }).first().click(); }
  /** Click Hotel Name Delete icon. */
  async clickHotelNameDeleteIcon(): Promise<void> { console.log('Click Hotel Name Delete icon'); await this.hotelNameDeleteIcon.click(); }
  /**
   * Set Hotel Location input.
   * @param value Hotel location value.
   * @param pressTab Whether to move focus away after filling.
   */
  async setHotelLocationInput({ value, pressTab = true }: { value: string; pressTab?: boolean }): Promise<void> { console.log(`Set Hotel Location input=${value}`); await this.fillInput(this.hotelLocationInput, value, pressTab); }
  /** Click Search for a booking button. */
  async clickSearchForBooking(): Promise<void> { console.log('Click Search for a booking button'); await this.searchForBookingButton.click(); await this.loadingMessageLabel.waitFor({ state: 'hidden', timeout: 30000 }); }
  /**
   * Set Booking And Surname Input.
   * @param reference Booking reference value.
   * @param surname Booking surname value.
   */
  async setBookingReferenceAndSurnameInput({ reference, surname }: { reference: string; surname: string }): Promise<void> { await this.setBookingReferenceInput({ value: reference, pressTab: true }); await this.setBookingSurnameInput({ value: surname, pressTab: true }); }
  /** Click Clear search button. */
  async clickClearSearch(): Promise<void> { console.log('Click Clear search button'); await this.clearButton.click(); }
  /**
   * Wait until the hotel name search field shows the selected hotel.
   * @param hotelName Selected hotel name expected in the field.
   */
  async waitForSelectedHotelNameSearchValue(hotelName: string): Promise<void> { console.log(`Wait for selected hotel name: ${hotelName}`); await expect.poll(() => this.getSelectedHotelNameSearchValue(), 'Hotel suggestion was selected').toContain(hotelName); }
  /**
   * Set Arrival Date input for extended search criteria.
   * @param value Arrival date value.
   * @param pressTab Whether to move focus away after filling.
   */
  async setArrivalDateInputForExtendedSearch({ value, pressTab = true }: { value: Date | string; pressTab?: boolean }): Promise<void> { console.log(`Set Arrival Date input=${value}`); if (value instanceof Date) { await this.arrivalDateInput.click(); await this.calendar.selectDate(value); if (await this.calendar.doneButton.isVisible()) await this.calendar.clickDone(); return; } await this.fillInput(this.arrivalDateInput, value, pressTab); }
  /**
   * Set Hotel Name input for extended search criteria.
   * @param hotelName Hotel name value.
   */
  async setHotelNameInputForExtendedSearch(hotelName: string): Promise<void> { console.log(`Set Hotel Name input=${hotelName}`); await this.fillInput(this.hotelNameInput, hotelName, false); await this.validateDisplayState(this.hotelSuggestionsTable, 'Hotel suggestions table'); }
  /**
   * Select Hotel Name from dropdown for extended search criteria.
   * @param hotelName Hotel name to select.
   */
  async selectHotelNameFromDropdownForExtendedSearch(hotelName: string): Promise<void> { console.log(`Select Hotel Name=${hotelName}`); const expectedHotelName = hotelName.trim().toLowerCase(); const matchingSuggestion = this.hotelSuggestionsLabels.filter({ hasText: new RegExp(expectedHotelName.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), 'i') }).first(); await matchingSuggestion.click(); await this.waitForSelectedHotelNameSearchValue(hotelName); }
  /**
   * Set Email Address input for extended search criteria.
   * @param value Email address value.
   * @param pressTab Whether to move focus away after filling.
   */
  async setEmailAddressInputForExtendedSearch({ value, pressTab = true }: { value: string; pressTab?: boolean }): Promise<void> { console.log(`Set Email Address input=${value}`); await this.fillInput(this.emailAddressInput, value, pressTab); }

  // ######## UI validations ########

  /** Validate Search Booking Results Title is displayed. */
  async validateSearchBookingResultsTitleLabel(): Promise<void> { console.log('Validate Search Booking Results Title'); await expect(this.searchBookingResultsTitleLabel, 'Search booking results title').toHaveText(await Strings.SEARCH_BOOKING_RESULTS.name); }
  /** Validate Description is displayed. */
  async validateSearchBookingResultsDescriptionLabel(): Promise<void> { console.log('Validate Description'); await expect(this.searchBookingResultsDescriptionLabel, 'Search booking results description').toHaveText(await Strings.PLEASE_ENTER_A_REF_NO.name); }
  /**
   * Validate Booking Reference Input is displayed.
   * @param value Expected booking reference value.
   */
  async validateBookingReferenceInput(value = ''): Promise<void> { console.log('Validate Booking Reference Input'); await expect(this.bookingReferenceInput, 'Booking Reference value').toHaveValue(value); }
  /**
   * Validate Booking Surname Input is displayed.
   * @param value Expected booking surname value.
   */
  async validateBookingSurnameInput(value = ''): Promise<void> { console.log('Validate Booking Surname Input'); await expect(this.bookingSurnameInput, 'Booking Surname value').toHaveValue(value); }
  /**
   * Validate Arrival Date Input is displayed.
   * @param value Expected arrival date value.
   */
  async validateArrivalDateInput(value = ''): Promise<void> { console.log('Validate Arrival Date Input'); await expect(this.arrivalDateInput, 'Arrival Date value').toHaveValue(value); }
  /**
   * Validate Show Criteria link is displayed.
   * @param isDisplayed Whether the expanded search criteria state is expected.
   */
  async validateShowMoreSearchCriteriaLink(isDisplayed = true): Promise<void> { console.log('Validate Show link'); await this.validateDisplayState(this.toggleSearchCriteriaLink, 'Show more/less search criteria link', true); await expect(this.toggleSearchCriteriaLink, 'Show more/less search criteria label').toContainText(isDisplayed ? await Strings.SHOW_MORE.name : await Strings.SHOW_LESS.name); }
  /** Validate Search for a booking button is displayed. */
  async validateSearchForBookingButton(): Promise<void> { console.log('Validate Search for a booking button'); await expect(this.searchForBookingButton, 'Search for a booking button').toHaveText(await Strings.SEARCH_FOR_A_BOOKING.name); }
  /**
   * Validate Search for a booking button is enabled.
   * @param isEnabled Whether the button should be enabled.
   */
  async validateSearchForBookingButtonIsEnabled({ isEnabled = true }: { isEnabled?: boolean } = {}): Promise<void> { console.log(`Validate Search for booking button enabled=${isEnabled}`); await this.validateEnabledState(this.searchForBookingButton, 'Search for booking button enabled state', isEnabled); }
  /**
   * Validate error messages for booking reference and surname are displayed.
   * @param referenceIsDisplayed Whether the booking-reference error should be displayed.
   * @param surnameIsDisplayed Whether the surname error should be displayed.
   */
  async validateErrorMessagesBookingReferenceAndSurname({ referenceIsDisplayed = false, surnameIsDisplayed = false }: { referenceIsDisplayed?: boolean; surnameIsDisplayed?: boolean } = {}): Promise<void> { console.log('Validate Errors Messages for Booking reference and Surname'); await this.validateDisplayState(this.bookingReferenceErrorMessageLabel, 'Booking reference error', referenceIsDisplayed); await this.validateDisplayState(this.bookingSurnameErrorMessageLabel, 'Booking surname error', surnameIsDisplayed); }
  /** Validate Clear Button is displayed. */
  async validateClearButton(): Promise<void> { console.log('Validate Clear button'); await expect(this.clearButton, 'Clear search button').toContainText(await Strings.CLEAR_SEARCH.name); }
  /**
   * Validate Guest Surname Input.
   * @param value Expected guest surname value.
   */
  async validateGuestSurnameInput(value = ''): Promise<void> { console.log('Validate Guest Surname Input'); await expect(this.guestSurnameInput, 'Guest Surname value').toHaveValue(value); }
  /**
   * Validate Booker Postcode Input.
   * @param value Expected booker postcode value.
   */
  async validateBookerPostcodeInput(value = ''): Promise<void> { console.log('Validate Booker Postcode Input'); await expect(this.bookerPostcodeInput, 'Booker Postcode value').toHaveValue(value); }
  /**
   * Validate Hotel Name Input.
   * @param value Expected hotel name value.
   */
  async validateHotelNameInput(value = ''): Promise<void> { console.log('Validate Hotel Name Input'); await expect(this.hotelNameInput, 'Hotel Name value').toHaveValue(value); }
  /**
   * Validate Hotel suggestions list.
   * @param isDisplayed Whether the suggestions table should be displayed.
   * @param hotelSuggestion Optional hotel name expected in the suggestions.
   */
  async validateHotelSuggestionsList({ isDisplayed = true, hotelSuggestion }: { isDisplayed?: boolean; hotelSuggestion?: string }): Promise<void> { console.log('Validate Hotel suggestions list'); await this.validateDisplayState(this.hotelSuggestionsTable, 'Hotel suggestions table', isDisplayed); if (hotelSuggestion) await expect(this.hotelSuggestionsLabels.filter({ hasText: hotelSuggestion }).first(), 'Hotel suggestions should include hotel name').toBeVisible(); }
  /** Validate Hotel Name delete icon. */
  async validateHotelNameDeleteIcon(): Promise<void> { console.log('Validate Hotel Name Delete icon'); await expect(this.hotelNameDeleteIcon, 'Hotel Name Delete icon').toBeVisible(); }
  /**
   * Validate Hotel Location Input.
   * @param value Expected hotel location value.
   */
  async validateHotelLocationInput(value = ''): Promise<void> { console.log('Validate Hotel Location Input'); await expect(this.hotelLocationInput, 'Hotel Location value').toHaveValue(value); }
  /**
   * Validate Email Address Input.
   * @param value Expected email address value.
   */
  async validateEmailAddressInput(value = ''): Promise<void> { console.log('Validate Email Address Input'); await expect(this.emailAddressInput, 'Email Address value').toHaveValue(value); }
  /**
   * Validate Telephone Number Input.
   * @param value Expected telephone number value.
   */
  async validateTelephoneNumberInput(value = ''): Promise<void> { console.log('Validate Telephone Number Input'); await expect(this.telephoneNumberInput, 'Telephone Number value').toHaveValue(value); }
  /**
   * Validate Cancellation Date Input.
   * @param value Expected cancellation date value.
   */
  async validateCancellationDateInput(value = ''): Promise<void> { console.log('Validate Cancellation Date Input'); await expect(this.cancellationDateInput, 'Cancellation Date value').toHaveValue(value); }
  /**
   * Validate Company Name Input.
   * @param value Expected company name value.
   */
  async validateCompanyNameInput(value = ''): Promise<void> { console.log('Validate Company Name Input'); await expect(this.companyNameInput, 'Company Name value').toHaveValue(value); }
  /**
   * Validate 3rd Party booking Reference Number Input.
   * @param value Expected third-party booking reference value.
   */
  async validateThirdPartyBookingReferenceInput(value = ''): Promise<void> { console.log('Validate 3rd Party Booking Reference Input'); await expect(this.thirdPartyBookingReferenceInput, '3rd Party Booking Reference value').toHaveValue(value); }
  /** Validate Search Criteria Show More Form. */
  async validateSearchCriteriaShowMoreForm(): Promise<void> { console.log('Validate Search Criteria Show More Form'); await this.validateGuestSurnameInput(); await this.validateBookerPostcodeInput(); await this.validateHotelNameInput(); await this.validateHotelLocationInput(); await this.validateEmailAddressInput(); await this.validateTelephoneNumberInput(); await this.validateCancellationDateInput(); await this.validateCompanyNameInput(); await this.validateThirdPartyBookingReferenceInput(); }
  /**
   * Validate Search Criteria Form is displayed.
   * @param isShowMoreDisplayed Whether the extended criteria link should show its expanded label.
   */
  async validateSearchCriteriaForm(isShowMoreDisplayed = true): Promise<void> { console.log('Validate Search Criteria Form is displayed'); await this.validateSearchBookingResultsTitleLabel(); await this.validateSearchBookingResultsDescriptionLabel(); await this.validateBookingReferenceInput(); await this.validateBookingSurnameInput(); await this.validateArrivalDateInput(); await this.validateShowMoreSearchCriteriaLink(isShowMoreDisplayed); await this.validateSearchForBookingButton(); await this.validateClearButton(); }
  /** Validate Calendar Month Container is opened. */
  async validateCalendarMonthContainerIsOpened(): Promise<void> { console.log('Validate Calendar Month Container is opened'); await expect(this.calendarMonthContainer, 'Calendar Month Container').toBeVisible(); }
  /**
   * Validate Hotel Name, Arrival Date and Email Address search fields contain the expected values.
   * @param hotelName Expected hotel name.
   * @param emailAddress Expected email address.
   */
  async validateHotelArrivalDateAndEmailSearchValues({ hotelName, emailAddress }: { hotelName: string; emailAddress: string }): Promise<void> { console.log('Validate Hotel Name, Arrival Date and Email Address search values'); expect(await this.getSelectedHotelNameSearchValue(), 'Hotel name search field value').toContain(hotelName); await expect(this.arrivalDateInput, 'Arrival date search field value').not.toHaveValue(''); await expect(this.emailAddressInput, 'Email address search field value').toHaveValue(emailAddress); }
}