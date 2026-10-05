import { test, expect } from '../../../src/fixtures/pib.fixture';
import {
  ApiBasketCalls,
  ApiBookingConfirmationHelpers,
  ApiCalls,
  ApiContentCalls,
  ApiHelpers,
  ApiResponses,
  ApiSlugsCalls,
} from '../../../src/api';
import { Cards } from '@test-data/cards';
import { Constants } from '@test-data/constants';
import { HotelRates } from '@test-data/hotelRates';
import { Hotels } from '@test-data/hotels';
import { PmsRoomTypes } from '@test-data/pmsRoomTypes';
import { Rooms } from '@test-data/room';
import { SearchCriteriaData } from '@test-data/searchCriteria';
import { Strings } from '@test-data/strings';
import { resetApplicationState } from '../../../src/utils';

test.describe('PIB baseline: manager UK hotel Business Flex POA amendment', () => {
  test('Test Book as travel manager: CNP PoA Booking. Check routing instructions and notes. Amend: change guest details. TestCase ID: 343521 @regression @TC-343521', async ({ pages }) => {
    console.log('https://whitbread.yourzephyr.com/flex/html5/tcr/651;testcaseId=1390860;explore=false;viewType=list;pageSize=50;pageView=search;offset=0;searchText=testcaseId%20in%20%28343521%29;searchType=zql;inRelease=true;currentIndex=1;usageHistoryGridSize=50');

    const hotel = Hotels.DEFAULT_HOTEL;
    const hotelRate = HotelRates.BUSINESS_FLEX;
    const pmsRoomType = PmsRoomTypes.PI_STANDARD_ROOM;
    const stayingNights = 1;
    const card = Cards.DEFAULT_PIBA;
    const passwordOrMemorableInput = 'abcdefgh12345678*&^%!@#$';
    const questionValue = Constants.STRING_WITH_5_CHARACTERS;
    const newEmployeeName = { firstName: 'First', lastName: 'Last' };

    await resetApplicationState();
    console.log('-> Given the application state, sign-in cookies and consent cookies are reset');
    console.log("-> Given I'm logged into Business Booker with a Travel Manager");
    await pages.loginIbPage.performLogin(
      Constants.BB_MANAGER_EMERGENCY_REPORT_NO_RECORDS,
      Constants.BB_MANAGER_EMERGENCY_REPORT_PASSWORD_NO_RECORDS,
    );

    console.log('-> When I search for a UK hotel');
    const hotelDetails = await SearchCriteriaData.getSearchCriteriaAndHotelAvailability({
      hotelName: hotel.name,
      hotelId: hotel.id,
      daysNumberForArrivalDate: 3,
      daysNumber: stayingNights,
      rooms: [Rooms.DOUBLE_1_ADULT_0_CHILDREN],
      ratePlanCode: hotelRate.ratePlanCode,
      pmsRoomType: pmsRoomType.ids[10],
      loggedUser: true,
    });
    expect(hotelDetails.hotelAvailabilityResponse, 'UK hotel availability must be returned').toBeTruthy();

    console.log('-> Then I should be directed to hotel details page of my selected hotel with availability for my search criteria.');
    await pages.homePage.searchConsole.searchHotels({
      searchCriteria: hotelDetails.searchCriteria,
      useSuggestedHotelName: true,
    });
    await pages.hotelDetailsPage.validatePage();
    await pages.hotelDetailsPage.validateNavigatedToHotel(hotel.slug ?? '');
    const apiHotelTitle = await ApiSlugsCalls.graphqlGetHotelTitle({ slug: hotel.slug ?? '' });
    await pages.hotelDetailsPage.validateHotelTitle(apiHotelTitle.title);

    console.log('-> When I select BusinessFlex rate and click the Book now button');
    await pages.hotelDetailsPage.chooseYourRateSection.clickSpecificRateForSpecificRoom({
      roomName: await pmsRoomType.name.name,
      rateName: hotelRate.name,
    });
    await pages.hotelDetailsPage.bookNowSummarySection.clickSeeBreakdownLink();
    const expectedPricesAndDatesPerNight = await pages.hotelDetailsPage.bookNowSummarySection.extractPricesAndDatesPerNight({ rooms: hotelDetails.searchCriteria.rooms });
    const totalPriceRoom = await pages.hotelDetailsPage.bookNowSummarySection.getTotalRoomPrice({ rooms: hotelDetails.searchCriteria.rooms });
    await pages.hotelDetailsPage.bookNowSummarySection.clickBookNow();
    await pages.hotelDetailsPage.bookNowSummarySection.closePremierPlusRoomUpgradeModalIfPresent();
    await pages.chooseYourBathroomPage.clickContinueIfChooseBathroomPageIsDisplayed();

    console.log("-> Then a hold reservation call will be made to Opera for my booking and I'll be directed to the Ancillaries page to continue the flow");
    await pages.ancillariesBBPage.validatePage();
    const basketReferenceId = await pages.ancillariesBBPage.getReservationIdFromUrl();
    await ApiHelpers.validateBasketStatus(ApiResponses.Basket.STATUS_OPEN, basketReferenceId);

    console.log('-> When I click on the "Enter details manually" button link and enter valid details in the displayed fields.');
    await pages.ancillariesBBPage.guestDetailsSection.clickEnterDetailsManuallyLink(1);
    const userProfileDetails = await ApiCalls.getUserProfileDetails({
      emailAddress: Constants.BB_MANAGER_EMERGENCY_REPORT_NO_RECORDS,
      business: true,
    });
    const booker = userProfileDetails.contactDetail ?? {};
    const firstName = String(booker.firstName ?? '');
    const lastName = String(booker.lastName ?? '');
    const email = String(booker.email ?? '');
    console.log('-> Then I should see every field with a green confirmation check mark at the end.');
    await pages.ancillariesBBPage.guestDetailsSection.fillYourDetails({
      title: await Strings.MR_TITLE.name,
      firstName,
      lastName,
      emailAddress: email,
      roomIndex: 0,
    });
    await pages.ancillariesBBPage.guestDetailsSection.validateTitleIsSelected(await Strings.MR_TITLE.name, 1);
    await pages.ancillariesBBPage.guestDetailsSection.validateFirstName({ value: firstName, shouldBeValid: true, roomIndex: 1 });
    await pages.ancillariesBBPage.guestDetailsSection.validateLastName({ value: lastName, shouldBeValid: true, roomIndex: 1 });
    await pages.ancillariesBBPage.guestDetailsSection.validateEmailAddress({ value: email, shouldBeValid: true, roomIndex: 1 });

    console.log('-> When I reach the GDP / Ancillaries page');
    console.log('-> Then I should not see PremierInn Breakfast auto selected.');
    await pages.ancillariesBBPage.bookingSummarySection.expandOnMobile();
    await pages.ancillariesBBPage.bookingSummarySection.validatePreselectedMealNotification(false);
    console.log('-> When I click the Continue button');
    await pages.ancillariesBBPage.clickContinue();

    console.log('-> Then I should be directed to the Payments page');
    await pages.paymentPage.validatePage();
    console.log('-> When I choose Pay on arrival option');
    console.log('-> And guarantee the reservation with a New BAC card And click on Continue button (right side)');
    await pages.paymentPage.paymentTypeSection.selectNewBusinessAccountCardButton();
    await pages.paymentPage.paymentOptions.selectPaymentOptionByLabel(await Strings.PAY_ON_ARRIVAL.name);
    await pages.paymentPage.paymentTypeSection.selectFirstDropdownEmployeeQuestion();
    await pages.paymentPage.paymentTypeSection.setFirstEmployeeQuestion(questionValue);
    await pages.paymentPage.paymentTypeSection.selectPaymentAuthorizationCheckbox();
    await pages.paymentPage.paymentTypeSection.setPaymentAuthorizationPasswordInput(passwordOrMemorableInput);

    console.log('-> Then I should be directed to the Payment details page to enter my Card details');
    await pages.paymentPage.confirmCurrentBooking({
      paymentOptionLabel: await Strings.PAY_ON_ARRIVAL.name,
      card,
      hotel: { countryCode: hotel.countryCode, type: { name: hotel.type } },
      basketReferenceId,
      isBAC: true,
    });

    console.log('-> When I fill in the details for my New BAC card and click the Confirm button');
    console.log('-> Then I should be directed to the Booking Confirmation page having the booking confirmation details displayed and confirmed as follows:');
    console.log('   - the confirmation message - Thank you booking');
    console.log('   - the confirmation email message');
    console.log('   - the Booking reference');
    console.log('   - the Hotel name & address');
    console.log('   - the Hotel phone number');
    await pages.confirmBookingPage.validatePage();
    await ApiHelpers.validateBasketStatus(ApiResponses.Basket.STATUS_COMPLETED, basketReferenceId);
    const basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReferenceId);
    const bookingReference = String(basket.bookingReference);
    expect(bookingReference, 'Completed basket must contain a booking reference').toBeTruthy();
    const bookingConfirmation = await ApiContentCalls.graphqlGetBookingConfirmation({ basketReference: basketReferenceId });
    await basket.validateRoutingInstructions([Constants.TRANSACTION_CODE_ACCOMMODATION]);
    const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: hotel.id, ratePlanCode: hotelRate.ratePlanCode });
    const confirmationHelpers = new ApiBookingConfirmationHelpers(bookingConfirmation);
    await confirmationHelpers.validateHotel(hotel);
    await confirmationHelpers.validateStayingDates(hotelDetails.searchCriteria);
    await confirmationHelpers.validateRoomsOccupancy(hotelDetails.searchCriteria);
    await confirmationHelpers.validateRoomTypes(await pmsRoomType.name.name, hotel.type);
    await confirmationHelpers.validateRatePlan(hotelRate.ratePlanCode);
    await confirmationHelpers.validateRatesPerNight(expectedPricesAndDatesPerNight);
    await confirmationHelpers.validateRoomPrice(totalPriceRoom);
    await confirmationHelpers.validateCurrency(Constants.UK_CURRENCY_CODE);
    await confirmationHelpers.validateBookingFlowId(bookingFlowId);
    await confirmationHelpers.validatePolicyCode(Constants.POLICY_CODE_D1A);
    await confirmationHelpers.validateDepositPoliciesForAllRooms(Constants.POLICY_CODE_D1A);
    await confirmationHelpers.validateTotalCostNoDiscountsNoAmendment(totalPriceRoom[0], await Strings.PAY_ON_ARRIVAL.name);
    await confirmationHelpers.validateCityTax();
    await confirmationHelpers.validatePaymentCard(await Strings.PAY_ON_ARRIVAL.name, card.number);

    console.log('-> When I check the Total cost section displayed under the Map section');
    console.log('-> Then I should have the Total cost displayed correctly');
    console.log('-> When I check in Opera UI platform for my newly submitted reservation');
    console.log('-> Then I should see the reservation having the same details as the ones submitted from the BB website');
    console.log('-> When Amend is true, I click on the Company header->Bookings and search with booking reference in the dashboard');
    await pages.homePage.open();
    await pages.homePage.upcomingBookingsSection.clickViewAllBookings();
    await pages.bookingHistoryPage.bookingHistoryBasePage.validatePage();
    await pages.bookingHistoryPage.bookingHistoryBasePage.findBooking(bookingReference);
    console.log('-> Then the Booking Information Card should be displayed');
    await pages.bookingHistoryPage.bookingHistoryBasePage.bookingHistoryCard.bookingInformationCard.validateBookingReferenceId(bookingReference);
    await pages.bookingHistoryPage.bookingHistoryBasePage.bookingHistoryCard.bookingInformationCard.clickAmendButton();
    console.log('-> When I click on Amend Booking button');
    console.log('-> Then I should see Amend page');
    await pages.amendBookingPage.validatePage();
    console.log('-> When I edit the room and change the guest details');
    await pages.amendBookingPage.clickRoomAndGuestsSection();
    await pages.amendBookingPage.editSpecificRoom({ roomIndex: 0 });
    await pages.amendBookingPage.roomAndGuestSection.clickEnterDetailsManuallyLink();
    await pages.amendBookingPage.roomAndGuestSection.setFirstName(newEmployeeName.firstName);
    await pages.amendBookingPage.roomAndGuestSection.setLastName(newEmployeeName.lastName);
    await pages.amendBookingPage.clickUpdateRoom();
    console.log('-> Then I should see the changes in Amend page');
    await pages.amendBookingPage.validateRoomSuccessfullyUpdated({ roomIndex: 0 });
    await pages.amendBookingPage.validateRoomInfoCardIncludesText({ roomIndex: 0, text: `${newEmployeeName.firstName} ${newEmployeeName.lastName}` });
    console.log('-> When I submit the Amend');
    await pages.amendBookingPage.clickConfirmChanges();
    console.log('-> Then I should be redirected to the BIC');
    await pages.bookingConfirmationPage.validatePage();
    await pages.bookingConfirmationPage.validateAmendSuccessNotification();
    await pages.bookingConfirmationPage.validateTotalCostAmountAndCurrency(String(totalPriceRoom[0]), Constants.UK_CURRENCY_CODE);
  });
});
