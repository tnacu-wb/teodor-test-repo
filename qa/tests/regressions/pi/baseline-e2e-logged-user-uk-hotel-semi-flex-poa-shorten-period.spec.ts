import { test, expect } from '../../../src/fixtures/pi.fixture';
import {
  ApiBookingConfirmationHelpers,
  ApiBasketCalls,
  ApiCalls,
  ApiContentCalls,
  ApiDictionary,
  ApiHelpers,
  ApiReservationCalls,
  ApiResponses,
} from '../../../src/api';
import { HotelAvailabilityInput } from '../../../src/api/requests';
import type { ReservationInfo as ApiReservationInfo } from '../../../src/api/response';
import { Cards } from '@test-data/cards';
import { Constants } from '@test-data/constants';
import { GuestDetailsData } from '@test-data/guestDetails';
import { HotelRates } from '@test-data/hotelRates';
import { Hotels } from '@test-data/hotels';
import { Locales } from '@test-data/locales';
import { Locations } from '@test-data/locations';
import { PmsRoomTypes } from '@test-data/pmsRoomTypes';
import { Rooms } from '@test-data/room';
import { SearchCriteriaData } from '@test-data/searchCriteria';
import { Strings } from '@test-data/strings';
import { PriceHelpers, resetApplicationState } from '../../../src/utils';

type ReservationDetails = {
  basketReference: string;
  reservations: Array<{
    roomStay: { arrivalDate: string; departureDate: string };
  }>;
};

test.describe('End2end: Amend, Logged in user, PI UK Hotel, Semi-Flex, pay now, shorten period', () => {
  test.beforeAll(() => {
    test.skip(
      !Locales.isEnglishWebsite(),
      'This E2E baseline test runs only on the English website.'
    );
  });

  test('Test Amend, Logged in user: shorten period and verify changes. TestCase ID: 266200.', async ({ pages }) => {
    console.log('https://whitbread.yourzephyr.com/flex/html5/tcr/467;testcaseId=1079889;viewType=list;pageSize=50;pageView=search;offset=0;searchText=testcaseId%20in%20%28266200%29;searchType=zql;inRelease=true;currentIndex=1;usageHistoryGridSize=50');

    const hotel = Hotels.DEFAULT_HOTEL;
    const hasCityTax = true; // LONDON_HEATHROW_AIRPORT does not apply city tax.
    const stayingNights = 4;
    const nightsToBeRemoved = 2;
    const guestDetails = {
      ...GuestDetailsData.DEFAULT_GUEST,
      booker: {
        ...GuestDetailsData.DEFAULT_GUEST.booker,
        emailAddress: Constants.PI_LEISURE_EMAIL,
      },
    };

    await resetApplicationState();
    // Given I want to amend a Pay Now reservation as a PI logged in user that has
    // Pay Now, Semi-Flex rate, within a UK hotel.
    await pages.bartHomePage.navigateToHotelDetailsPageAndLoginIntoAccount({ hotel });

    console.log('-> Given I want to amend a Pay Now reservation as a PI logged in user that has Pay Now, Semi-Flex rate, within a UK hotel');
    // Make sure availability also exists for amending the reservation with new dates.
    const availability = await SearchCriteriaData.getSearchCriteriaAndHotelAvailability({
      hotelName: hotel.name,
      hotelId: hotel.id,
      daysNumberForArrivalDate: 3,
      daysNumber: stayingNights,
      rooms: [
        Rooms.FAMILY_2_ADULTS_2_CHILDREN,
        Rooms.DOUBLE_2_ADULTS_0_CHILDREN,
        Rooms.FAMILY_2_ADULTS_2_CHILDREN,
      ],
      ratePlanCode: HotelRates.PI_SEMI_FLEX.ratePlanCode,
      loggedUser: true,
    });
    expect(availability.hotelAvailabilityResponse, 'Semi-Flex availability must be confirmed').toBeTruthy();
    const availabilityStartDate = new Date(`${availability.hotelAvailabilityResponse.startDate}T00:00:00`);
    const daysFromToday = Math.ceil(
      (availabilityStartDate.getTime() - new Date().setHours(0, 0, 0, 0)) / (24 * 60 * 60 * 1000)
    );

    // Create the initial reservation before adding packages, matching the reference journey.
    const hotelAvailabilityInput = await HotelAvailabilityInput.createHotelAvailabilityInputWithInterval({
      hotelId: hotel.id,
      daysNumber: stayingNights,
      rooms: [Rooms.FAMILY_2_ADULTS_2_CHILDREN],
    });
    const reservationInfo = await ApiReservationCalls.createReservationViaApi({
      guestDetails,
      stayingNights,
      daysFromToday,
      randomStartDate: false,
      hotelAvailabilityInput,
      ratePlanCode: HotelRates.PI_SEMI_FLEX.ratePlanCode,
      loggedUser: true,
    });
    const reservationDetails = reservationInfo.reservationDetails as ReservationDetails;
    const basketReferenceId = reservationDetails.basketReference;
    const reservation = reservationDetails.reservations[0];

    const basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReferenceId);
    console.log('-> Given the initial reservation has breakfast and children meal packages');
    const initialRoomsSelections = basket.items.map((item) => ({
      reservationId: String(item.sourceId),
      packagesSelection: [
        { id: Constants.PI_BREAKFAST_PACKAGE_CODE, noOfSelections: 1 },
        { id: Constants.KIDS_MEAL_PACKAGE_CODE, noOfSelections: 1 },
      ],
    }));
    const emptyRoomsSelections = basket.items.map((item) => ({
      reservationId: String(item.sourceId),
      packagesSelection: [],
    }));
    await ApiReservationCalls.graphqlUpdateReservationPackagesByReservation({
      basketReferenceId,
      hotelId: hotel.id,
      arrivalDate: reservation.roomStay.arrivalDate,
      departureDate: reservation.roomStay.departureDate,
      roomsSelections: initialRoomsSelections,
      previousRoomsSelections: emptyRoomsSelections,
    });
    await ApiHelpers.confirmReservationViaApi({
      reservationInfo: reservationInfo as unknown as ApiReservationInfo,
      hotel,
      paymentOption: Constants.PAYMENT_OPTION.payNow,
      card: Cards.VISA_CARD,
    });
    const completedBasket = await ApiHelpers.waitForBasketStatus(
      basketReferenceId,
      ApiResponses.Basket.STATUS_COMPLETED
    );
    expect(completedBasket.bookingReference, 'Completed basket must contain a booking reference').toBeTruthy();

    // When I open Manage Booking and submit the booking reference and arrival date.
    await pages.hotelDetailsPage.openHotelDetailsBySlug(hotel.slug ?? '');
    await pages.manageBookingPage.openModal();
    await pages.manageBookingPage.validateModalVisible(true);
    await pages.manageBookingPage.searchBooking({
      reference: String(completedBasket.bookingReference),
      surname: guestDetails.booker.lastName,
      arrivalDate: reservation.roomStay.arrivalDate,
    });
    await pages.manageBookingPage.validatePage();
    const dashboardLabels = await ApiDictionary.fetchDashboardLabels();
    const cancelAmendPolicy = String(dashboardLabels['dashboard.bookings.error.cancel.semiflex'] ?? '');
    await pages.manageBookingPage.bookingInformationCard.validateAmendCancelPoliciesText(cancelAmendPolicy);
    const cancellationPolicies = await ApiCalls.graphqlGetCancellationPolicies({
      hotelId: hotel.id,
      basketRef: basketReferenceId,
      arrivalDate: reservation.roomStay.arrivalDate,
      ratePlanCode: HotelRates.PI_SEMI_FLEX.ratePlanCode,
    });
    await pages.manageBookingPage.clickAmendBookingButton();
    await pages.amendBookingPage.validatePage();

    // Shorten stay period so that the new period remains within the initial stay interval.
    console.log('-> When I want to shorten the stay period so that the new period is within the initial stay interval');
    const originalTotal = await pages.amendBookingPage.getTotalCostAmount();
    await pages.amendBookingPage.clickStayDates();
    await pages.amendBookingPage.stayDatesSection.selectNightsFromDropdown(stayingNights - nightsToBeRemoved);

    console.log('-> Then the check in/out dates will be successfully saved and the total price updated in the booking summary side of the screen');
    await pages.amendBookingPage.stayDatesSection.validateCheckoutDateUpdate(
      stayingNights - nightsToBeRemoved
    );
    await pages.amendBookingPage.validateTotalCostHasChanged(originalTotal);

    // Add a room (Room no. 2).
    console.log('-> When I want to add a room to the reservation');
    await pages.amendBookingPage.clickRoomAndGuestsSection();
    await pages.amendBookingPage.clickAddRoomCard();
    await pages.amendBookingPage.addRoom(guestDetails.booker.title ?? 'Mr', 'NewGuest', 'Tester');
    await pages.amendBookingPage.validateRoomInfoCardIncludesText({ roomIndex: 1, text: 'NewGuest Tester' });

    // Add a guest to the second room.
    console.log('-> When I want to add a guest to an existing room');
    await pages.amendBookingPage.editSpecificRoom({ roomIndex: 1 });
    await pages.amendBookingPage.selectNumberOfAdults(2);
    await pages.amendBookingPage.clickCheckAvailability();
    await pages.amendBookingPage.clickUpdateRoom();
    await pages.amendBookingPage.validateRoomSuccessfullyUpdated({ roomIndex: 1 });
    await pages.amendBookingPage.validateAdultsAndChildrenNumbersForRoomIndexes({
      roomIndexesArray: [1],
      expectedAdults: 2,
      expectedChildren: 0,
    });

    // Add a child to the second room.
    console.log('-> When I want to add a kid to an existing room');
    await pages.amendBookingPage.editSpecificRoom({ roomIndex: 1 });
    await pages.amendBookingPage.selectNumberOfChildren(1);
    await pages.amendBookingPage.clickCheckAvailability();
    await pages.amendBookingPage.clickUpdateRoom();
    await pages.amendBookingPage.validateRoomSuccessfullyUpdated({ roomIndex: 1 });
    await pages.amendBookingPage.validateAdultsAndChildrenNumbersForRoomIndexes({
      roomIndexesArray: [1],
      expectedAdults: 2,
      expectedChildren: 1,
    });

    // Add a meal for an adult to the first room.
    console.log('-> When I want to add a meal for an adult to the reservation');
    await pages.amendBookingPage.clickYourMealsSection();
    await pages.amendBookingPage.yourMealsSection.clickRoomTabButton(0);
    const adultBreakfast = pages.amendBookingPage.yourMealsSection.adultMealContainerByTitle(await Strings.PREMIER_INN_BREAKFAST.name);
    await adultBreakfast.clickAddMealButton();
    await pages.amendBookingPage.yourMealsSection.validateMealSelectionNotification();

    // Remove breakfast for a child from the first room.
    console.log('-> When I want to remove breakfast for a kid from the reservation');
    const childBreakfast = pages.amendBookingPage.yourMealsSection.childBreakfastMealContainer();
    await childBreakfast.clickRemoveMealButton();
    await pages.amendBookingPage.yourMealsSection.validateMealSelectionNotification();

    const amendedTotal = await pages.amendBookingPage.getTotalCostAmount();

    // When I press the Confirm changes button.
    await pages.amendBookingPage.clickConfirmChanges();
    console.log('-> Then I am being redirected to the booking confirmation page and the amended reservation is displayed');
    await pages.bookingConfirmationPage.validatePage();
    await pages.bookingConfirmationPage.validateAmendSuccessNotification(true);
    await pages.bookingConfirmationPage.validateRoomAdultsAndChildren(
      [0, 1],
      [
        `2 ${await Strings.ADULTS.name}, 2 ${await Strings.CHILDREN.name}`,
        `2 ${await Strings.ADULTS.name}, 1 ${await Strings.CHILD.name}`,
      ],
    );
    await pages.bookingConfirmationPage.validateNewTotalCostAmountAndCurrency(
      PriceHelpers.getPriceAmountFromUiLabel(amendedTotal),
      Constants.UK_CURRENCY_CODE,
    );

    const amendedBasket = await ApiHelpers.waitForBasketStatus(
      basketReferenceId,
      ApiResponses.Basket.STATUS_COMPLETED
    );
    expect(amendedBasket.bookingReference, 'Amended basket must contain a booking reference').toBeTruthy();

    const amendedArrivalDate = new Date(reservation.roomStay.arrivalDate);
    const amendedDepartureDate = new Date(amendedArrivalDate);
    amendedDepartureDate.setUTCDate(amendedDepartureDate.getUTCDate() + stayingNights - nightsToBeRemoved);
    const amendedSearchCriteria = new SearchCriteriaData({
      arrivalDate: amendedArrivalDate,
      departureDate: amendedDepartureDate,
      nights: stayingNights - nightsToBeRemoved,
      location: Locations.LONDON,
      rate: HotelRates.PI_SEMI_FLEX,
      rooms: [Rooms.FAMILY_2_ADULTS_2_CHILDREN, Rooms.FAMILY_2_ADULTS_1_CHILDREN],
    });
    const amendedBookingConfirmation = await ApiContentCalls.graphqlGetBookingConfirmation({
      basketReference: basketReferenceId,
      bookingChannel: 'WEB',
    });
    const amendedBookingConfirmationHelpers = new ApiBookingConfirmationHelpers(amendedBookingConfirmation);
    const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({
      hotelId: hotel.id,
      ratePlanCode: HotelRates.PI_SEMI_FLEX.ratePlanCode,
    });
    await amendedBookingConfirmationHelpers.validateHotel(hotel);
    await amendedBookingConfirmationHelpers.validateStayingDates(amendedSearchCriteria);
    await amendedBookingConfirmationHelpers.validateRoomsOccupancy(amendedSearchCriteria);
    await amendedBookingConfirmationHelpers.validateRoomTypes(
      await PmsRoomTypes.PI_STANDARD_ROOM.name.name,
      hotel.type,
    );
    await amendedBookingConfirmationHelpers.validateRatePlan(HotelRates.PI_SEMI_FLEX.ratePlanCode);
    await amendedBookingConfirmationHelpers.validateCurrency(Constants.UK_CURRENCY_CODE);
    await amendedBookingConfirmationHelpers.validateBookingFlowId(bookingFlowId);
    await amendedBookingConfirmationHelpers.validatePolicyCode(Constants.POLICY_CODE_D1);
    const mealsPackages = await ApiCalls.graphqlGetMealsPackages({
      hotelId: hotel.id,
      nightsNumber: stayingNights - nightsToBeRemoved,
      startDate: reservation.roomStay.arrivalDate,
      endDate: amendedDepartureDate.toISOString().slice(0, 10),
      adultsNumber: 2,
      childrenNumber: 2,
      bookingFlowId,
      basketReferenceId,
    });
    const adultBreakfastPackage = (mealsPackages.adultMeals ?? []).find((meal) => meal.id === Constants.PI_BREAKFAST_PACKAGE_CODE);
    expect(adultBreakfastPackage, 'Adult breakfast package must be available for amended booking validation').toBeTruthy();
    if (!adultBreakfastPackage) {
      throw new Error('Adult breakfast package must be available for amended booking validation');
    }
    await amendedBookingConfirmationHelpers.validateMealPackagesPerRoom([
      {
        packageCode: adultBreakfastPackage.id ?? Constants.PI_BREAKFAST_PACKAGE_CODE,
        totalQuantity: 2,
        unitPrice: adultBreakfastPackage.price ?? 0,
      },
    ], amendedSearchCriteria);
    await amendedBookingConfirmationHelpers.validatePaymentCard(
      Constants.PAYMENT_OPTION.payNow,
      Cards.VISA_CARD.number,
    );
    await amendedBookingConfirmationHelpers.validateCityTax(hasCityTax);
    await amendedBookingConfirmationHelpers.validateDepositFolios(amendedBasket);

    const amendedCancellationPolicies = await ApiCalls.graphqlGetCancellationPolicies({
      hotelId: hotel.id,
      ratePlanCode: HotelRates.PI_SEMI_FLEX.ratePlanCode,
      basketRef: basketReferenceId,
      arrivalDate: reservation.roomStay.arrivalDate,
    });
    const getCancellationPoliciesText = (
      policies: Array<{ text?: string }> | { text?: string } | null | undefined,
    ): string => Array.isArray(policies)
      ? policies.map((policy) => policy.text ?? '').join(' ')
      : policies?.text ?? '';
    expect(
      getCancellationPoliciesText(amendedCancellationPolicies),
      'Cancellation policy must remain unchanged after amendment',
    ).toBe(getCancellationPoliciesText(cancellationPolicies));

    console.log('-> When I cancel the amended reservation from Manage Booking');
    await pages.homePage.open();
    await pages.manageBookingPage.openModal();
    await pages.manageBookingPage.validateModalVisible(true);
    await pages.manageBookingPage.searchBooking({
      reference: String(amendedBasket.bookingReference),
      surname: guestDetails.booker.lastName,
      arrivalDate: reservation.roomStay.arrivalDate,
    });
    await pages.manageBookingPage.validateBookingReferenceId(String(amendedBasket.bookingReference));
    await pages.manageBookingPage.clickCancelBookingButton();
    await pages.manageBookingPage.confirmCancellation();
    await pages.manageBookingPage.validateCancellationSuccessMessage(String(amendedBasket.bookingReference));
    await pages.manageBookingPage.closeCancelModal();

    console.log('-> Then the booking status is Cancelled and the reservation is cancelled in the API');
    const cancelledBasket = await ApiHelpers.waitForBasketStatus(
      basketReferenceId,
      ApiResponses.Basket.STATUS_CANCELLED
    );
    await ApiHelpers.validateReservationIsCancelled(hotel.id, cancelledBasket);
    await pages.manageBookingPage.validateBookingReferenceId(String(amendedBasket.bookingReference));
    await pages.bookingHistoryPage.validateCanceledBIC({
      roomsList: [{ adultsNumber: Rooms.FAMILY_2_ADULTS_2_CHILDREN.adultsNumber, childrenNumber: Rooms.FAMILY_2_ADULTS_2_CHILDREN.childrenNumber }],
    });

  });
});
