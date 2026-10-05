import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { CcuiComponent } from '../baseCcui.component';

/**
 * ID&V section that is part of the CCUI Manage Booking page.
 * Mirrors qa/reference/test/pages/components/ccui/manageBooking/idvSection.js.
 */
export class IdvSectionComponent extends CcuiComponent {
  // ######## UI elements/properties ########
  readonly idvContainer: Locator = this.page.locator('[data-testid="IDVModal"]');
  readonly titleLabel: Locator = this.page.locator('h2[data-testid="IDVModal-ModalTitle"]');
  readonly personalInformationLabel: Locator = this.page.locator('h2[data-testid="IDVModal-PersonalInformation-Heading"]');
  readonly bookingInformationLabel: Locator = this.page.locator('h2[data-testid="IDVModal-BookingInfo-Heading"]');
  readonly dpaStatusLabel: Locator = this.page.locator('h2[data-testid="IDVModal-DpaStatus-Heading"]');
  readonly xButton: Locator = this.page.locator('button[data-testid="ModalCloseButton"]');
  readonly closeIdvButton: Locator = this.page.locator('button[data-testid="IDVModal-CloseButton"]');
  readonly bookerNameLabel: Locator = this.page.locator('[data-testid="IDVModal-PersonalInformation-BookerName-Label"] p').first();
  readonly bookingBookerNameLabel: Locator = this.page.locator('[data-testid="IDVModal-PersonalInformation-BookerName-Label"] p').nth(1);
  readonly bookerNameCheckbox: Locator = this.page.locator('label[data-testid="IDVModal-PersonalInformation-BookerName-Checkbox"] input');
  readonly guestNameLabel: Locator = this.page.locator('[data-testid="IDVModal-PersonalInformation-GuestName-Label"] p').first();
  readonly bookingGuestNameLabel: Locator = this.page.locator('[data-testid="IDVModal-PersonalInformation-GuestName-Label"] p').nth(1);
  readonly guestNameCheckbox: Locator = this.page.locator('label[data-testid="IDVModal-PersonalInformation-GuestName-Checkbox"] input');
  readonly addressLabel: Locator = this.page.locator('[data-testid="IDVModal-PersonalInformation-Address-Label"] p').first();
  readonly bookingAddressLabel: Locator = this.page.locator('[data-testid="IDVModal-PersonalInformation-Address-Label"] p').nth(1);
  readonly addressCheckbox: Locator = this.page.locator('label[data-testid="IDVModal-PersonalInformation-Address-Checkbox"] input');
  readonly postcodeLabel: Locator = this.page.locator('[data-testid="IDVModal-PersonalInformation-Postcode-Label"] p').first();
  readonly bookingPostcodeLabel: Locator = this.page.locator('[data-testid="IDVModal-PersonalInformation-Postcode-Label"] p').nth(1);
  readonly postcodeCheckbox: Locator = this.page.locator('label[data-testid="IDVModal-PersonalInformation-Postcode-Checkbox"] input');
  readonly telephoneNumberLabel: Locator = this.page.locator('[data-testid="IDVModal-PersonalInformation-TelephoneNumber-Label"] p').first();
  readonly bookingTelephoneNumberLabel: Locator = this.page.locator('[data-testid="IDVModal-PersonalInformation-TelephoneNumber-Label"] p').nth(1);
  readonly telephoneNumberCheckbox: Locator = this.page.locator('label[data-testid="IDVModal-PersonalInformation-TelephoneNumber-Checkbox"] input');
  readonly cardUsedLabel: Locator = this.page.locator('[data-testid="IDVModal-PersonalInformation-CardUsedToMakeBooking-Label"] p').first();
  readonly bookingCardUsedLabel: Locator = this.page.locator('[data-testid="IDVModal-PersonalInformation-CardUsedToMakeBooking-Label"] p').nth(1);
  readonly cardUsedCheckbox: Locator = this.page.locator('label[data-testid="IDVModal-PersonalInformation-CardUsedToMakeBooking-Checkbox"] input');
  readonly reservationNumberLabel: Locator = this.page.locator('[data-testid="IDVModal-BookingInfo-ReservationNumber-Label"] p').first();
  readonly bookingReservationNumberLabel: Locator = this.page.locator('[data-testid="IDVModal-BookingInfo-ReservationNumber-Label"] p').nth(1);
  readonly reservationNumberCheckbox: Locator = this.page.locator('label[data-testid="IDVModal-BookingInfo-ReservationNumber-Checkbox"] input');
  readonly hotelNameLabel: Locator = this.page.locator('[data-testid="IDVModal-BookingInfo-HotelName-Label"] p').first();
  readonly bookingHotelNameLabel: Locator = this.page.locator('[data-testid="IDVModal-BookingInfo-HotelName-Label"] p').nth(1);
  readonly hotelNameCheckbox: Locator = this.page.locator('label[data-testid="IDVModal-BookingInfo-HotelName-Checkbox"] input');
  readonly arrivalDateLabel: Locator = this.page.locator('[data-testid="IDVModal-BookingInfo-ArrivalDate-Label"] p').first();
  readonly bookingArrivalDateLabel: Locator = this.page.locator('[data-testid="IDVModal-BookingInfo-ArrivalDate-Label"] p').nth(1);
  readonly arrivalDateCheckbox: Locator = this.page.locator('label[data-testid="IDVModal-BookingInfo-ArrivalDate-Checkbox"] input');
  readonly departureDateLabel: Locator = this.page.locator('[data-testid="IDVModal-BookingInfo-DepartureDate-Label"] p').first();
  readonly bookingDepartureDateLabel: Locator = this.page.locator('[data-testid="IDVModal-BookingInfo-DepartureDate-Label"] p').nth(1);
  readonly departureDateCheckbox: Locator = this.page.locator('label[data-testid="IDVModal-BookingInfo-DepartureDate-Checkbox"] input');
  readonly emailAddressLabel: Locator = this.page.locator('[data-testid="IDVModal-BookingInfo-EmailAddress-Label"] p').first();
  readonly bookingEmailAddressLabel: Locator = this.page.locator('[data-testid="IDVModal-BookingInfo-EmailAddress-Label"] p').nth(1);
  readonly emailAddressCheckbox: Locator = this.page.locator('label[data-testid="IDVModal-BookingInfo-EmailAddress-Checkbox"] input');
  readonly dpaPassedLabel: Locator = this.page.locator('p[data-testid="IDVModal-DpaStatus-DpaPassed-Label"]');
  readonly dpaOverrideLabel: Locator = this.page.locator('p[data-testid="IDVModal-DpaStatus-DpaOverride-Label"]');
  readonly eCnpPasswordLabel: Locator = this.page.locator('[data-testid="IDVModal-DpaStatus-ECnpPassword"] p').first();
  readonly bookingECnpPasswordLabel: Locator = this.page.locator('[data-testid="IDVModal-DpaStatus-ECnpPassword"] p').nth(1);
  readonly dpaPassedYesRadioButton: Locator = this.idvContainer.locator('span[data-testid="IDVModal-DpaStatus-DpaPassed-RadioOption-TRUE"] + span');
  readonly dpaPassedNoRadioButton: Locator = this.idvContainer.locator('span[data-testid="IDVModal-DpaStatus-DpaPassed-RadioOption-FALSE"] + span');
  readonly dpaOverrideYesRadioButton: Locator = this.idvContainer.locator('span[data-testid="IDVModal-DpaStatus-DpaOverride-RadioOption-TRUE"] + span');
  readonly dpaOverrideNoRadioButton: Locator = this.idvContainer.locator('span[data-testid="IDVModal-DpaStatus-DpaOverride-RadioOption-FALSE"] + span');

  // ######## UI actions/navigation ########
  /** Click Booker Name checkbox. */
  async clickBookerNameCheckbox(): Promise<void> { console.log('Click Booker Name checkbox'); await this.bookerNameCheckbox.scrollIntoViewIfNeeded(); await this.bookerNameCheckbox.click(); }
  /** Click Postcode checkbox. */
  async clickPostcodeCheckbox(): Promise<void> { console.log('Click Postcode checkbox'); await this.postcodeCheckbox.scrollIntoViewIfNeeded(); await this.postcodeCheckbox.click(); }
  /** Click Reservation Number checkbox. */
  async clickReservationNumberCheckbox(): Promise<void> { console.log('Click Reservation number checkbox'); await this.reservationNumberCheckbox.scrollIntoViewIfNeeded(); await this.reservationNumberCheckbox.click(); }
  /** Click Hotel Name checkbox. */
  async clickHotelNameCheckbox(): Promise<void> { console.log('Click Hotel name checkbox'); await this.hotelNameCheckbox.scrollIntoViewIfNeeded(); await this.hotelNameCheckbox.click(); }
  /** Click Arrival Date checkbox. */
  async clickArrivalDateCheckbox(): Promise<void> { console.log('Click Arrival date checkbox'); await this.arrivalDateCheckbox.scrollIntoViewIfNeeded(); await this.arrivalDateCheckbox.click(); }
  /** Click DPA passed Yes. */
  async clickDpaPassedYes(): Promise<void> { console.log('Click DPA Passed? Yes radio button'); await this.dpaPassedYesRadioButton.scrollIntoViewIfNeeded(); await this.dpaPassedYesRadioButton.click(); }
  /** Click DPA passed No. */
  async clickDpaPassedNo(): Promise<void> { console.log('Click DPA Passed? No radio button'); await this.dpaPassedNoRadioButton.scrollIntoViewIfNeeded(); await this.dpaPassedNoRadioButton.click(); }
  /** Click DPA override Yes. */
  async clickDpaOverrideYes(): Promise<void> { console.log('Click DPA Override? Yes radio button'); await this.dpaOverrideYesRadioButton.scrollIntoViewIfNeeded(); await this.dpaOverrideYesRadioButton.click(); }
  /** Click DPA override No. */
  async clickDpaOverrideNo(): Promise<void> { console.log('Click DPA Override? No radio button'); await this.dpaOverrideNoRadioButton.scrollIntoViewIfNeeded(); await this.dpaOverrideNoRadioButton.click(); }
  /** Click Close IDV. */
  async clickCloseIdv(): Promise<void> { console.log('Click Close ID&V'); await this.closeIdvButton.scrollIntoViewIfNeeded(); await this.closeIdvButton.click(); await expect(this.idvContainer, 'IDV container').toBeHidden(); }
  /** Click X button. */
  async clickXButton(): Promise<void> { console.log('Click X icon'); await this.xButton.scrollIntoViewIfNeeded(); await this.xButton.click(); await expect(this.idvContainer, 'IDV container').toBeHidden(); }

  // ######## UI validations ########
  /**
   * Validate overlay displayed state.
   * @param isDisplayed Whether the ID&V overlay should be displayed.
   */
  async validateOverlayDisplayed(isDisplayed = true): Promise<void> { console.log('Validate overlay displayed state'); await this.validateDisplayState(this.idvContainer, 'IDV container', isDisplayed); }
  /** Validate overlay. */
  async validateOverlay(): Promise<void> { console.log('Validate the overlay'); await expect(this.idvContainer, 'IDV container').toBeVisible(); await expect(this.titleLabel, 'IDV title').toContainText(await Strings.IDV_TITLE.name); await expect(this.personalInformationLabel, 'Personal information label').toContainText(await Strings.PERSONAL_INFORMATION.name); await expect(this.bookingInformationLabel, 'Booking information label').toContainText(await Strings.BOOKING_INFORMATION.name); await expect(this.closeIdvButton, 'Close IDV button').toContainText(await Strings.CLOSE_IDV.name); await expect(this.xButton, 'IDV close icon').toBeVisible(); }
  /**
   * Validate the displayed booker name and optional checkbox state.
   * @param bookerName Expected booker name.
   * @param checked Whether the associated checkbox should be checked.
   */
  async validateBookerName(bookerName: string, checked = false): Promise<void> { await this.validateTextAndCheckbox('Booker name', await Strings.BOOKER_NAME.name, bookerName, checked, this.bookerNameLabel, this.bookingBookerNameLabel, this.bookerNameCheckbox); }
  /**
   * Validate the displayed guest name and optional checkbox state.
   * @param guestName Expected guest name.
   * @param checked Whether the associated checkbox should be checked.
   */
  async validateGuestName(guestName: string, checked = false): Promise<void> { await this.validateTextAndCheckbox('Guest name', await Strings.GUEST_NAME.name, guestName, checked, this.guestNameLabel, this.bookingGuestNameLabel, this.guestNameCheckbox); }
  /**
   * Validate the displayed address and optional checkbox state.
   * @param address Expected address text.
   * @param checked Whether the associated checkbox should be checked.
   */
  async validateAddress(address: string, checked = false): Promise<void> { await this.validateTextAndCheckbox('Address', await Strings.ADDRESS.name, address, checked, this.addressLabel, this.bookingAddressLabel, this.addressCheckbox); }
  /**
   * Validate the displayed postcode and optional checkbox state.
   * @param postcode Expected postcode.
   * @param checked Whether the associated checkbox should be checked.
   */
  async validatePostcode(postcode: string, checked = false): Promise<void> { await this.validateTextAndCheckbox('Postcode', await Strings.POSTCODE.name, postcode, checked, this.postcodeLabel, this.bookingPostcodeLabel, this.postcodeCheckbox); }
  /**
   * Validate the displayed telephone number and optional checkbox state.
   * @param telephoneNumber Expected telephone number.
   * @param checked Whether the associated checkbox should be checked.
   */
  async validateTelephoneNumber(telephoneNumber: string, checked = false): Promise<void> { await this.validateTextAndCheckbox('Telephone number', await Strings.TELEPHONE_NUMBER_IDV.name, telephoneNumber, checked, this.telephoneNumberLabel, this.bookingTelephoneNumberLabel, this.telephoneNumberCheckbox); }
  /**
   * Validate the displayed card-used value and optional checkbox state.
   * @param cardUsed Expected card-used text.
   * @param checked Whether the associated checkbox should be checked.
   */
  async validateCardUsed(cardUsed = '', checked = false): Promise<void> { await this.validateTextAndCheckbox('Card used', await Strings.CARD_USED_TO_MAKE_BOOKING.name, cardUsed, checked, this.cardUsedLabel, this.bookingCardUsedLabel, this.cardUsedCheckbox); }
  /**
   * Validate the displayed reservation number and optional checkbox state.
   * @param reservationNumber Expected reservation number.
   * @param checked Whether the associated checkbox should be checked.
   */
  async validateReservationNumber(reservationNumber: string, checked = false): Promise<void> { await this.validateTextAndCheckbox('Reservation number', await Strings.RESERVATION_NUMBER.name, reservationNumber, checked, this.reservationNumberLabel, this.bookingReservationNumberLabel, this.reservationNumberCheckbox); }
  /**
   * Validate the displayed hotel name and optional checkbox state.
   * @param hotelName Expected hotel name.
   * @param checked Whether the associated checkbox should be checked.
   */
  async validateHotelName(hotelName: string, checked = false): Promise<void> { await this.validateTextAndCheckbox('Hotel name', await Strings.HOTEL_NAME.name, hotelName, checked, this.hotelNameLabel, this.bookingHotelNameLabel, this.hotelNameCheckbox); }
  /**
   * Validate the displayed arrival date and optional checkbox state.
   * @param arrivalDate Expected arrival date.
   * @param checked Whether the associated checkbox should be checked.
   */
  async validateArrivalDate(arrivalDate: string, checked = false): Promise<void> { await this.validateTextAndCheckbox('Arrival date', await Strings.ARRIVAL_DATE.name, arrivalDate, checked, this.arrivalDateLabel, this.bookingArrivalDateLabel, this.arrivalDateCheckbox); }
  /**
   * Validate the displayed departure date and optional checkbox state.
   * @param departureDate Expected departure date.
   * @param checked Whether the associated checkbox should be checked.
   */
  async validateDepartureDate(departureDate: string, checked = false): Promise<void> { await this.validateTextAndCheckbox('Departure date', await Strings.DEPARTURE_DATE.name, departureDate, checked, this.departureDateLabel, this.bookingDepartureDateLabel, this.departureDateCheckbox); }
  /**
   * Validate the displayed email address and optional checkbox state.
   * @param emailAddress Expected email address.
   * @param checked Whether the associated checkbox should be checked.
   */
  async validateEmailAddress(emailAddress: string, checked = false): Promise<void> { await this.validateTextAndCheckbox('Email address', await Strings.EMAIL_ADDRESS.name, emailAddress, checked, this.emailAddressLabel, this.bookingEmailAddressLabel, this.emailAddressCheckbox); }
  /**
   * Validate the selected DPA-passed radio option.
   * @param yesValue Whether the Yes option should be selected.
   */
  async validateDpaPassed(yesValue = false): Promise<void> { console.log('Validate DPA passed'); await expect(yesValue ? this.dpaPassedYesRadioButton : this.dpaPassedNoRadioButton, 'DPA passed radio state').toBeChecked(); }
  /**
   * Validate the selected DPA-override radio option.
   * @param yesValue Whether the Yes option should be selected.
   */
  async validateDpaOverride(yesValue = false): Promise<void> { console.log('Validate DPA override'); await expect(yesValue ? this.dpaOverrideYesRadioButton : this.dpaOverrideNoRadioButton, 'DPA override radio state').toBeChecked(); }
  /**
   * Validate the ECNP password when one is supplied.
   * @param eCnpPassword Expected ECNP password text.
   */
  async validateECnpPassword(eCnpPassword = ''): Promise<void> { console.log('Validate ECNP password'); if (eCnpPassword) await expect(this.idvContainer, 'ECNP password').toContainText(eCnpPassword); }

  /** Validate text and, when provided, the checked state of a related checkbox. */
  private async validateTextAndCheckbox(description: string, labelText: string, valueText: string, checked: boolean, label: Locator, value: Locator, checkbox?: Locator): Promise<void> { console.log(`Validate ${description}=${valueText} and its checkbox=${checked}`); await expect(label, `${description} label`).toContainText(labelText); if (valueText) await expect(value, `${description} value`).toContainText(valueText); if (checkbox) await expect(checkbox, `${description} checkbox checked state`).toBeChecked({ checked }); }
}