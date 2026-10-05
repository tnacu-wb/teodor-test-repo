import { expect, type Locator, type Page } from '@playwright/test';
import { BasePage } from '../shared/base.page';
import { ManageBookingModalComponent } from '../../components/pi/manageBooking/manageBookingModal.component';
import { BookingInformationCardComponent } from '../../components/shared/manageBooking/bookingInformationCard.component';
import { CancelBookingModalComponent } from '../../components/shared/manageBooking/cancelBookingModal.component';
import { ApiCalls, ApiDictionary } from '../../api';

type DashboardReservation = {
  reservationDetails: {
    basketReference: string;
  };
};

type BookingCancellableReservation = DashboardReservation & {
  guestDetails: {
    booker: {
      lastName: string;
    };
  };
  reservationDetails: DashboardReservation['reservationDetails'] & {
    reservations: Array<{
      roomStay: {
        arrivalDate: string;
      };
    }>;
  };
};

type HotelAddress = {
  fullAddress: string;
  postcode: string;
  city?: string;
};

/**
 * Manage Booking Page - handles the manage booking modal, booking search,
 * booking information card display, and action buttons (amend, cancel).
 *
 * The modal is opened from the header navigation ("Manage Booking" / "Find Booking" link).
 * After searching with booking reference, surname, and arrival date, it displays a
 * booking information card with hotel details, dates, and action buttons.
 *
 * Composes the same sub-components as qa/reference test/pages/pi/manageBooking.page.js:
 * the header ManageBookingModal, the BookingInformationCardSectionBase, and CancelBookingModal.
 */
export class ManageBookingPage extends BasePage {

  // ######## UI elements/properties ########

  // Header navigation trigger (desktop and mobile)
  readonly manageBookingNavButton: Locator = this.page.locator('[data-testid="ManageBookingButton"]');
  readonly manageBookingNavButtonMobile: Locator = this.page.locator('[data-testid="Global-TriggerManageModal"]');
  readonly contactUsButton: Locator = this.page.locator('[data-testid="BookingDetailsControllerContainer"] button, [data-testid="BookingDetailsControllerContainer"] a');
  readonly contactUsButtonUrl: Locator = this.page.locator('[data-testid="BookingDetailsControllerContainer"] a');
  // Locale-agnostic: matches the generic dashboard error banner regardless of translated text
  readonly dashboardErrorAlert: Locator = this.page.locator('main').getByRole('alert').first();

  private contactUsPage: Page | undefined;
  private readonly url = 'account/dashboard';

  // UI components

  readonly modal: ManageBookingModalComponent = new ManageBookingModalComponent();
  readonly bookingInformationCard: BookingInformationCardComponent = new BookingInformationCardComponent();
  readonly cancelBookingModal: CancelBookingModalComponent = new CancelBookingModalComponent();

  // ######## UI actions/navigation ########

  /**
   * Open a confirmed booking directly on the dashboard.
   * @param reservationInfo - reservation details containing the basket reference
   */
  async open(reservationInfo: DashboardReservation): Promise<void> {
    console.log(`Open Manage Booking dashboard for basket=${reservationInfo.reservationDetails.basketReference}`);
    await this.openLocalizedPath(await this.buildPageUrl(reservationInfo));
  }

  /**
   * Build the direct Manage Booking dashboard URL.
   * @param reservationInfo - reservation details containing the basket reference
   * @param additionalParams - optional query parameters to append
   * @returns localized-path suffix for the dashboard
   */
  async buildPageUrl(reservationInfo: DashboardReservation, ...additionalParams: Array<{ key: string; value: string }>): Promise<string> {
    const params = new URLSearchParams({ bookingReference: reservationInfo.reservationDetails.basketReference });
    additionalParams.forEach(({ key, value }) => params.append(key, value));
    return `/${this.url}?${params.toString()}`;
  }

  /**
   * Open the Manage Booking modal via header navigation.
   * Detects viewport size and uses the appropriate trigger (desktop vs mobile).
   */
  async openModal(): Promise<void> {
    console.log('Open Manage Booking modal');
    const viewportWidth = this.page.viewportSize()?.width ?? 1280;

    if (viewportWidth >= 1024) {
      await this.manageBookingNavButton.waitFor({ state: 'visible' });
      await this.manageBookingNavButton.click();
    } else {
      // Mobile: open burger menu first, then click manage booking
      const burgerMenu = this.page.locator('[data-testid="burgerMenu"]');
      await burgerMenu.click();
      await this.manageBookingNavButtonMobile.waitFor({ state: 'visible' });
      await this.manageBookingNavButtonMobile.click();
    }

    // Wait for modal to be visible
    await this.modal.modalContent.waitFor({ state: 'visible' });
  }

  /**
   * Search for a booking using the booking reference, surname, and arrival date.
   * @param options - The search criteria
   * @param options.reference - The booking reference (e.g. "AKU9491086")
   * @param options.surname - The guest's surname
   * @param options.arrivalDate - The arrival date (format: YYYY-MM-DD or locale date string)
   */
  async searchBooking(options: {
    reference: string;
    surname: string;
    arrivalDate: string;
  }): Promise<void> {
    console.log(`Search Manage Booking reference=${options.reference} arrivalDate=${options.arrivalDate}`);
    await this.modal.searchBooking({
      bookingReference: options.reference,
      bookingSurname: options.surname,
      arrivalDate: options.arrivalDate,
    });

    // The dashboard can briefly render a generic error after the search. Refresh the
    // dashboard a few times so the booking request can recover before failing.
    let refreshAttempts = 0;
    await expect.poll(async () => {
      const urlLooksCorrect = /\/account\/dashboard(?:[/?]|$)/.test(this.page.url());
      const bookingCardVisible = await this.bookingInformationCard.wrapper.isVisible().catch(() => false)
        || await this.bookingInformationCard.bookingReferenceLabel.isVisible().catch(() => false);
      if (urlLooksCorrect && bookingCardVisible) return true;

      if (urlLooksCorrect && refreshAttempts < 3 && await this.dashboardErrorAlert.isVisible().catch(() => false)) {
        refreshAttempts += 1;
        console.log(`Refresh Manage Booking dashboard after transient error (attempt ${refreshAttempts}/3)`);
        await this.page.reload({ waitUntil: 'domcontentloaded' });
      }

      return false;
    }, {
      timeout: 45000,
      intervals: [1000, 3000, 5000],
      message: `Manage Booking search for ${options.reference} did not render a booking card within the dashboard`,
    }).toBeTruthy();
  }

  /**
   * Click the "Amend Booking" button to initiate the amendment flow.
   */
  async clickAmendBookingButton(): Promise<void> {
    console.log('Click Amend Booking button');
    await this.bookingInformationCard.clickAmendButton();
  }

  /**
   * Click the "Cancel Booking" button to initiate the cancellation flow.
   */
  async clickCancelBookingButton(): Promise<void> {
    console.log('Click Cancel Booking button');
    await this.bookingInformationCard.clickCancelButton();
    await this.cancelBookingModal.container.waitFor({ state: 'visible' });
  }

  /**
   * Confirm the cancellation in the cancel booking modal.
   */
  async confirmCancellation(): Promise<void> {
    console.log('Confirm booking cancellation');
    await this.cancelBookingModal.confirmCancellation();
  }

  /**
   * Close the cancel modal after cancellation success message is displayed.
   */
  async closeCancelModal(): Promise<void> {
    console.log('Close cancellation modal');
    await this.cancelBookingModal.close();
  }

  /**
   * Close the manage booking modal without searching.
   */
  async closeModal(): Promise<void> {
    console.log('Closing Manage Booking modal');
    await this.modal.clickCloseManageBookingModal();
  }

  /**
   * Open the Contact Us page from the booking-actions section in a new tab.
   */
  async clickContactUsButton(): Promise<void> {
    console.log('Click Contact Us button');
    const dashboardLabels = await ApiDictionary.fetchDashboardLabels();
    const expectedLabel = String(dashboardLabels['dashboard.bookings.contactUsButton'] ?? '');
    const button = this.contactUsButton.filter({ hasText: expectedLabel });
    await expect(button, 'Contact Us button should be visible before it is clicked').toBeVisible();
    await button.scrollIntoViewIfNeeded();
    const [contactUsPage] = await Promise.all([
      this.page.context().waitForEvent('page'),
      button.click(),
    ]);
    this.contactUsPage = contactUsPage;
  }

  // ######## UI validations ########

  /**
   * Validate that the Manage Booking modal is displayed.
   */
  async validateModalVisible(isDisplayed = true): Promise<void> {
    console.log(`Validate Manage Booking modal is ${isDisplayed ? 'visible' : 'not visible'}`);
    await this.modal.validateVisible(isDisplayed);
  }

  /**
   * Validate that the booking information card page/content is displayed.
   */
  async validatePage(): Promise<void> {
    console.log('Validate Manage booking page was reached');
    await this.bookingInformationCard.validateVisible();
  }

  /**
   * Validate the booking information title.
   */
  async validateBookingInformationTitle(): Promise<void> {
    console.log('Validate booking information title');
    await this.bookingInformationCard.validateBookingInformationTitle();
  }

  /**
   * Validate the hotel name displayed on the booking information card.
   * @param expectedText - expected hotel name
   */
  async validateHotelNameLabel(expectedText: string): Promise<void> {
    console.log(`Validate hotel name=${expectedText}`);
    await this.bookingInformationCard.validateHotelNameLabel(expectedText);
  }

  /**
   * Validate the check-in label.
   */
  async validateCheckInLabel(): Promise<void> {
    console.log('Validate check-in label');
    await this.bookingInformationCard.validateCheckInLabel();
  }

  /**
   * Validate the displayed check-in date/time.
   * @param arrivalDate - expected arrival date/time text
   */
  async validateCheckInDate(arrivalDate: string): Promise<void> {
    console.log(`Validate check-in date=${arrivalDate}`);
    await this.bookingInformationCard.validateCheckInDate(arrivalDate);
  }

  /**
   * Validate the check-out label.
   */
  async validateCheckOutLabel(): Promise<void> {
    console.log('Validate check-out label');
    await this.bookingInformationCard.validateCheckOutLabel();
  }

  /**
   * Validate the displayed check-out date/time.
   * @param departureDate - expected departure date/time text
   */
  async validateCheckOutDate(departureDate: string): Promise<void> {
    console.log(`Validate check-out date=${departureDate}`);
    await this.bookingInformationCard.validateCheckOutDate(departureDate);
  }

  /**
   * Validate amend and cancel button visibility for a booking rate plan.
   * @param isDisplayed - whether the action buttons should be displayed
   * @param ratePlanCode - booking rate plan code
   */
  async validateAmendCancelButtonAreDisplayed(isDisplayed: boolean, ratePlanCode: string): Promise<void> {
    console.log(`Validate amend/cancel buttons isDisplayed=${isDisplayed} ratePlanCode=${ratePlanCode}`);
    await this.bookingInformationCard.validateAmendCancelButtonAreDisplayed(isDisplayed, ratePlanCode);
  }

  /**
   * Validate Early Check-in and Late Check-out details for a room.
   * @param options - ECI/LCO name, price, currency, and zero-based room index
   */
  async validateEciLcoWithPrices(options: { eciLcoName: string; extrasPrice: number; currency: string; roomIndex: number }): Promise<void> {
    console.log('Validate Early Check-in and Late Check-out details');
    await this.bookingInformationCard.validateEciLcoWithPrices(options);
  }

  /**
   * Validate the booking reference displayed on the booking information card.
   * @param expectedReference - The expected booking reference string
   */
  async validateBookingReference(expectedReference: string): Promise<void> {
    console.log(`Validate booking reference=${expectedReference}`);
    await this.bookingInformationCard.validateBookingReference(expectedReference);
  }

  /**
   * Validate the booking reference ID displayed on the booking information card (exact match).
   * Used after re-searching a booking (e.g. post-cancellation) to confirm the correct booking is shown.
   * @param expectedReference - The exact expected booking reference string
   */
  async validateBookingReferenceId(expectedReference: string): Promise<void> {
    console.log(`Validate booking reference ID=${expectedReference}`);
    await this.bookingInformationCard.validateBookingReferenceId(expectedReference);
  }

  /**
   * Validate the booking status label displayed on the booking information card.
   * @param expectedLabel - expected booking status text
   */
  async validateBookingInformationCardStatus(expectedLabel: string): Promise<void> {
    console.log(`Validate booking status=${expectedLabel}`);
    await this.bookingInformationCard.validateBookingInformationCardStatus(expectedLabel);
  }

  /**
   * Validate the booking information card displays the expected details.
   * @param expected - The expected values to validate against
   * @param expected.hotelName - Expected hotel name
   * @param expected.dates - Expected date text (e.g. check-in or check-out)
   * @param expected.guests - Expected guest information text
   */
  async validateBookingInformationCard(expected: {
    hotelName?: string;
    dates?: string;
    guests?: string;
  }): Promise<void> {
    console.log(`Validate booking information card details=${JSON.stringify(expected)}`);
    await this.bookingInformationCard.validateDetails(expected);
  }

  /**
   * Validate the booking card hotel address using its content-service address.
   * @param options - hotel address returned by content service
   */
  async validateBookingCardHotelAddress({ hotelAddress }: { hotelAddress: HotelAddress }): Promise<void> {
    const address = hotelAddress.postcode === ''
      ? hotelAddress.fullAddress
      : `${hotelAddress.fullAddress}, ${hotelAddress.postcode}`;
    console.log(`Validate the booking card hotel address is ${address}`);
    await this.bookingInformationCard.validateHotelAddress(address);
  }

  /**
   * Validate the booking card hotel address including its city.
   * @param options - hotel address returned by content service
   */
  async validateBookingCardHotelFullAddress({ hotelAddress }: { hotelAddress: Required<HotelAddress> }): Promise<void> {
    const address = `${hotelAddress.fullAddress}, ${hotelAddress.city}, ${hotelAddress.postcode}`;
    console.log(`Validate the booking card hotel full address is ${address}`);
    await this.bookingInformationCard.validateHotelAddress(address);
  }

  /**
   * Validate the booking card parking information.
   * @param hotelParking - parking HTML/text returned by hotel content service
   */
  async validateBookingCardParkingInfo(hotelParking: string): Promise<void> {
    console.log(`Validate booking card parking=${hotelParking}`);
    console.log(`Validate the booking card hotel parking is ${hotelParking}`);
    await this.bookingInformationCard.validateHotelParkingInfo(hotelParking);
  }

  /**
   * Validate the hotel image, address, and parking information as one card section.
   * @param options - expected hotel card data
   */
  async validateBookingHotelInfo({ isDisplayed = true, hotelThumbnail, hotelAddress, hotelParking }: {
    isDisplayed?: boolean;
    hotelThumbnail: string;
    hotelAddress: HotelAddress;
    hotelParking: string;
  }): Promise<void> {
    console.log('Validate booking hotel information');
    const address = hotelAddress.postcode === ''
      ? hotelAddress.fullAddress
      : `${hotelAddress.fullAddress}, ${hotelAddress.postcode}`;
    await this.bookingInformationCard.validateBookingHotelInfo({
      isDisplayed,
      hotelThumbnail,
      hotelAddress: address,
      hotelParking,
    });
  }

  /**
   * Validate the charitable pledge label against the AEM labels dictionary.
   */
  async validateCharitablePledgeAemKey(): Promise<void> {
    console.log('Validate charitable pledge label');
    const labelsDictionary = await ApiDictionary.fetchLabelsDictionary();
    const expectedLabel = String(labelsDictionary['account.dashboard.gosh'] ?? '');
    await this.bookingInformationCard.validateCharitablePledgeLabel(expectedLabel);
  }

  /**
   * Validate that a booking's cancellable status matches the Manage Booking API.
   * @param options - reservation and hotel details needed for the API request
   */
  async validateBookingStatus({ reservationInfo, bookingReference, hotel, isBookingActive = true }: {
    reservationInfo: BookingCancellableReservation;
    bookingReference: string;
    hotel: { id: string };
    isBookingActive?: boolean;
  }): Promise<void> {
    console.log(`Validate the booking is ${isBookingActive ? 'still active' : 'no longer active'}`);
    const findBookingResponse = await ApiCalls.graphqlFindBooking({
      basketReference: bookingReference,
      lastName: reservationInfo.guestDetails.booker.lastName,
      arrivalDate: reservationInfo.reservationDetails.reservations[0]?.roomStay.arrivalDate ?? '',
    });
    const manageBookingResponse = await ApiCalls.graphqlManageBooking({
      basketReference: reservationInfo.reservationDetails.basketReference,
      hotelId: hotel.id,
      token: findBookingResponse.token,
    });
    expect(manageBookingResponse.isCancellable, `Booking cancellability should be ${String(isBookingActive)}`).toBe(isBookingActive);
  }

  /**
   * Validate that the Contact Us link and label match the dashboard dictionary.
   */
  async validateContactUsButtonLabel(): Promise<void> {
    const dashboardLabels = await ApiDictionary.fetchDashboardLabels();
    const expectedLabel = String(dashboardLabels['dashboard.bookings.contactUsButton'] ?? '');
    const expectedUrl = String(dashboardLabels['dashboard.bookings.contactUsButton.url'] ?? '');
    const button = this.contactUsButton.filter({ hasText: expectedLabel });
    console.log(`Validate the label of Contact Us button using: ${expectedLabel}`);
    await expect(button, 'Contact Us button should be visible').toBeVisible();
    await expect(button, 'Contact Us button label should match the dashboard dictionary').toContainText(expectedLabel);
    const href = await this.contactUsButtonUrl.getAttribute('href');
    expect(href, 'Contact Us link should provide an href').not.toBeNull();
    expect(href ?? '', `Contact Us link should contain ${expectedUrl}`).toContain(expectedUrl);
  }

  /**
   * Validate that Contact Us opened in a new tab, then close that tab.
   */
  async validateContactUsPageInNewTab(): Promise<void> {
    console.log('Validating that Contact Us page loaded in a new tab');
    const context = this.page.context();
    expect(context.pages().length, 'Contact Us should open exactly one additional tab').toBe(2);
    const contactUsPage = this.contactUsPage ?? context.pages()[1];
    expect(contactUsPage, 'Contact Us page should be available in the browser context').toBeDefined();
    const dashboardLabels = await ApiDictionary.fetchDashboardLabels();
    const expectedUrl = String(dashboardLabels['dashboard.bookings.contactUsButton.url'] ?? '');
    await expect(contactUsPage!, 'Contact Us page should navigate away from about:blank').not.toHaveURL('about:blank');
    expect(contactUsPage!.url(), `Contact Us page URL should contain ${expectedUrl}`).toContain(expectedUrl);
    await contactUsPage!.close();
    this.contactUsPage = undefined;
  }

  /**
   * Validate that the dashboard URL rendered by the UI matches the generic dictionary.
   */
  async validateUrlFeVsBe(): Promise<void> {
    const dictionary = await ApiDictionary.fetchGenericDictionary() as {
      config?: { bookingSearch?: { dashboardRedirect?: { operaUrl?: string } } };
    };
    const expectedUrl = dictionary.config?.bookingSearch?.dashboardRedirect?.operaUrl;
    expect(expectedUrl, 'Generic dictionary should define the Opera dashboard redirect URL').toBeTruthy();
    expect(new URL(this.page.url()).pathname, 'Manage Booking URL should match the Opera dashboard redirect URL').toBe(expectedUrl);
  }

  /**
   * Validate the cancellation success message is displayed with the booking reference.
   * @param bookingReference - The booking reference that should appear in the success message
   */
  async validateCancellationSuccessMessage(bookingReference: string): Promise<void> {
    console.log(`Validate cancellation success message for booking=${bookingReference}`);
    await this.cancelBookingModal.validateCancellationSuccessMessage(bookingReference);
  }

  /**
   * Validate that the booking shows a cancelled status in the UI.
   * Used after re-searching a cancelled booking to confirm status display.
   */
  async validateCancelledStatus(): Promise<void> {
    await this.bookingInformationCard.validateCancelledStatus();
  }
}
