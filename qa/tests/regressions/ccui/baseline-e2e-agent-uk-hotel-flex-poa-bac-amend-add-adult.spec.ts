import { test, expect } from '../../../src/fixtures/ccui.fixture';
import type { PricePerNight } from '../../../src/pages/pi/hotelDetails.page';
import {
  ApiBookingConfirmationHelpers,
  ApiCalls,
  ApiContentCalls,
  ApiHelpers,
  ApiResponses,
  ApiSlugsCalls,
} from '../../../src/api';
import { Cards } from '@test-data/cards';
import { Constants } from '@test-data/constants';
import { GuestData } from '@test-data/guestData';
import { HotelRates } from '@test-data/hotelRates';
import { Hotels } from '@test-data/hotels';
import { Locales } from '@test-data/locales';
import { Rooms } from '@test-data/room';
import { SearchCriteriaData } from '@test-data/searchCriteria';
import { Strings } from '@test-data/strings';
import { resetApplicationState } from '../../../src/utils';

test.describe('CCUI baseline book as agent POA BAC amendment', () => {
  test('Test Book as Agent: UK Hotel, 1 night, 1 Single room, 1 adult, Flex rate, Leisure as purpose of stay, home address, POA [BAC] and Amend by add adult. TestCase ID: 379925 @regression @TC-379925', async ({ pages }) => {
    console.log('https://whitbread.yourzephyr.com/flex/html5/tcr/467;testcaseId=1280692;viewType=list;pageSize=50;pageView=search;offset=0;searchText=testcaseId%20%3D%20379925;searchType=zql;inRelease=true;currentIndex=1;usageHistoryGridSize=50');

    const hotel = Hotels.LONDON_HEATHROW_AIRPORT;
    const stayingNights = 1;
    const hotelRate = HotelRates.PI_FLEX;
    const currency = Constants.UK_CURRENCY_CODE;
    const hasCityTax = false;
    const card = Cards.DEFAULT_PIBA;
    const guestInfo = GuestData.generateGuestInfo();

    let basketReferenceId = '';
    let bookingReferenceID = '';
    let totalCostOnPaymentPage = '';
    let expectedPricesAndDatesPerNight: PricePerNight[][] = [];
    let totalPriceRoom: number[] = [];

    await resetApplicationState();
    console.log('-> Given I want to book a room as a Agent, on the UK CCUI site');
    await pages.loginPage.loginCcuiUser(Constants.ROLE_STANDARD_AGENT);

    console.log('-> When I search for a specific UK location for 1 night, 1 adult, 1 single room and click on the Search button');
    const hotelDetails = await SearchCriteriaData.getSearchCriteriaAndHotelAvailability({
      hotelName: hotel.name,
      hotelId: hotel.id,
      daysNumberForArrivalDate: 20,
      daysNumber: stayingNights,
      rooms: [Rooms.SINGLE_1_ADULT_0_CHILDREN],
      ratePlanCode: hotelRate.ratePlanCode,
    });
    expect(hotelDetails.hotelAvailabilityResponse, 'Hotel availability must be confirmed before booking').toBeTruthy();

    console.log('-> Then I should be directed to Search result page where all hotels related to search will be available');
    console.log('-> When I click on view details of any hotel in SRP');
    console.log('-> Then I should be directed to Hotel details page of my selected Hotel');
    await pages.homePage.searchConsole.searchHotels({
      searchCriteria: hotelDetails.searchCriteria,
      useSuggestedHotelName: true,
    });
    const apiHotelTitle = await ApiSlugsCalls.graphqlGetHotelTitle({ slug: hotel.slug });
    await pages.hotelDetailsPage.validatePage();
    await pages.hotelDetailsPage.validateNavigatedToHotel(hotel.slug ?? '');
    await pages.hotelDetailsPage.validateHotelTitle(apiHotelTitle.title);

    console.log('-> When I select FLEX rate available and check the Booking summary panel');
    await pages.hotelDetailsPage.chooseYourRateSection.clickSpecificRateForSpecificRoom({
      roomName: await Strings.STANDARD_ROOM.name,
      rateName: hotelRate.name,
    });
    const selectedRoomName = await pages.hotelDetailsPage.chooseYourRateSection.getSelectedRoomName();

    console.log('-> Then it should be populated with the selected rate');
    await pages.hotelDetailsPage.bookNowSummarySection.validateBookingSummaryRatePlan({
      ratePlan: await pages.hotelDetailsPage.chooseYourRateSection.getSelectedRateName(),
      roomsCount: hotelDetails.searchCriteria.rooms.length,
    });
    await pages.hotelDetailsPage.bookNowSummarySection.clickSeeBreakdownLink();
    expectedPricesAndDatesPerNight = await pages.hotelDetailsPage.bookNowSummarySection.extractPricesAndDatesPerNight({ rooms: hotelDetails.searchCriteria.rooms });
    totalPriceRoom = await pages.hotelDetailsPage.bookNowSummarySection.getTotalRoomPrice({ rooms: hotelDetails.searchCriteria.rooms });

    console.log('-> When I click on Book now button');
    await pages.hotelDetailsPage.bookNowSummarySection.clickBookNow();
    await pages.chooseYourBathroomPage.clickContinueIfChooseBathroomPageIsDisplayed();

    console.log(`-> Then:
            - a hold reservation call will be made to Opera for my booking
            - I'll be directed to the Ancillaries page to continue the flow`);
    await pages.ancillariesCcuiPage.validatePage();
    basketReferenceId = await pages.ancillariesCcuiPage.getBasketReferenceIdFromUrl();
    expect(basketReferenceId, 'basketReferenceId must be extracted from the Ancillaries URL').toBeTruthy();
    await ApiHelpers.validateBasketStatus(ApiResponses.Basket.STATUS_OPEN, basketReferenceId);

    console.log('-> When I click on the continue button from the Ancillaries page displayed under Booking summary');
    await pages.ancillariesCcuiPage.clickContinueButton();

    console.log(`-> Then the breakfast should not be added
            -> And I should be directed to Guest Details page`);
    await pages.guestDetailsPage.validatePage();
    await expect(pages.guestDetailsPage.bookingSummarySection.adultMealsList, 'Guest details booking summary should not show adult meals').toHaveCount(0);

    console.log(`-> When I choose the booking purpose - Leisure and add User details
            -> And add a home address
            -> And click on the Continue button displayed under the billing address`);
    await pages.guestDetailsPage.selectReasonForStay(GuestData.DEFAULT_REASON_FOR_STAY);
    await pages.guestDetailsPage.fillBookerInformation(guestInfo);
    await pages.guestDetailsPage.fillAddress(GuestData.GUEST_ADDRESS);
    const normalisedGuestDetails = await pages.guestDetailsPage.readNormalisedGuestDetails();
    await pages.guestDetailsPage.clickContinueToPayment();

    console.log('-> Then I should be directed to the Payments Details page');
    await pages.paymentCcuiPage.validatePage();
    totalCostOnPaymentPage = await pages.paymentCcuiPage.totalCostSection.getTotalCostAmount();
    expect(totalCostOnPaymentPage, 'Total cost on CCUI payment page must be captured').toBeTruthy();

    console.log('-> When I select POA + Card (BAC)');
    await pages.paymentCcuiPage.clickOnSelectedPaymentOption(Strings.PAY_ON_ARRIVAL_CCUI.name);
    await pages.paymentCcuiPage.paymentTypeSection.clickToSelectPaymentTypeByLabel(await Strings.NEW_PIBA_UK.name);

    console.log(`-> Then 2 new sections are displayed:
                     -> Card present status' section is displayed with:
                     -> Card present option unselected and enabled
                     -> CNP unselected and enabled
                     -> Launch Eckoh' CTA`);
    await pages.paymentCcuiPage.cardPresentStatus.validateCardPresentElementLabels();
    await pages.paymentCcuiPage.cardPresentStatus.validateCardPresentStatus(false);
    await pages.paymentCcuiPage.cardPresentStatus.clickOnCardPresentOption(await Strings.CARD_PRESENT.name);
    await pages.paymentCcuiPage.validateEckohButtonIsDisplayed();

    console.log('-> When I press the "Launch Eckoh" CTA');
    console.log('-> Then the Eckoh panel will be displayed with an error (no authorization)');
    console.log('-> When I close the Eckoh panel after successful validation');
    await pages.paymentCcuiPage.openEckohIframeAndBypassApiWebhook({ basketReference: basketReferenceId, card });

    console.log(`-> Then 2 new sections are displayed on the page:
                     -> Card holder section
                     -> Billing address`);
    await pages.paymentCcuiPage.cardHolderName.validateCardHolderNameElementsAreDisplayed();
    if (Locales.isEnglishWebsite()) {
      await pages.paymentCcuiPage.billingAddressSection.validateBillingAddressElements(GuestData.GUEST_ADDRESS);
    }

    console.log(`-> When I fill in the Card holder name section - First name + Last name
            -> And use the supplied business billing address from GDP`);
    await pages.paymentCcuiPage.cardHolderName.setCardholderFirstNameLastName(guestInfo.firstName, guestInfo.lastName);

    console.log('-> Then the Total Cost section is displayed');
    await pages.paymentCcuiPage.totalCostSection.validateTotalCostSectionIsDisplayed();

    console.log('-> When I check the T&C checkbox');
    await pages.paymentCcuiPage.totalCostSection.checkUncheckRoomRatePolicyCheckboxBasedOnCurrentState(true);

    console.log(`-> Then the room policy overlay is displayed
                        And the 'Confirm booking' CTA is enabled`);
    await pages.paymentCcuiPage.totalCostSection.validateRoomRatePolicyModalIsDisplayed(true);
    await pages.paymentCcuiPage.totalCostSection.closeRoomRatePolicyModalIfDisplayed();
    await pages.paymentCcuiPage.totalCostSection.validateConfirmBookingButtonIsEnabled(true);

    console.log(`-> When I close the room policy overlay
                        And I select the Customer email option
                        And I click on 'Confirm booking' CTA`);
    await pages.paymentCcuiPage.totalCostSection.clickOnSendEmailConfirmation();
    await pages.paymentCcuiPage.totalCostSection.clickOnConfirmBookingButton();

    console.log(`-> Then the 'Confirm booking' page is displayed with all the details
                        And all the below information with the values entered in previous steps are sent in the booking confirmation call for the relevant booking reference`);
    console.log('Not all elements can be find on UI, they will be validated on the last step.');
    await pages.confirmBookingPageCcui.validatePage();
    const basketDetails = await ApiHelpers.waitForBasketStatus(basketReferenceId, ApiResponses.Basket.STATUS_COMPLETED);
    bookingReferenceID = String(basketDetails.bookingReference ?? await pages.confirmBookingPageCcui.getBookingReference());
    expect(bookingReferenceID, 'Booking reference must be available after CCUI confirmation').toBeTruthy();
    const bookingConfirmation = await ApiContentCalls.graphqlGetBookingConfirmation({ basketReference: basketReferenceId }) as Record<string, unknown>;
    const bookingFlowId = String(await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: hotel.id, ratePlanCode: hotelRate.ratePlanCode }));

    await pages.confirmBookingPageCcui.validateTotalCostCityTaxMessage(hasCityTax);
    await pages.confirmBookingPageCcui.totalCostSection.validateTotalCostPayOnArrivalPaymentMessage();
    await pages.confirmBookingPageCcui.validateRoomCityTaxMessage(hasCityTax);

    console.log('-> When I check in Opera UI platform for my newly submitted reservation');
    console.log('-> Then I should see the reservation having the same details as the ones submitted from the CCUI website in this flow');
    const bookingConfirmationHelpers = new ApiBookingConfirmationHelpers(bookingConfirmation);
    await bookingConfirmationHelpers.validateHotel(hotel);
    await bookingConfirmationHelpers.validateStayingDates(hotelDetails.searchCriteria);
    await bookingConfirmationHelpers.validateRoomsOccupancy(hotelDetails.searchCriteria);
    await bookingConfirmationHelpers.validateRoomTypes(selectedRoomName, hotel.type);
    await bookingConfirmationHelpers.validateRatePlan(hotelRate.ratePlanCode);
    await bookingConfirmationHelpers.validateRatesPerNight(expectedPricesAndDatesPerNight, 0, hasCityTax);
    await bookingConfirmationHelpers.validateCurrency(currency);
    await bookingConfirmationHelpers.validateBookingFlowId(bookingFlowId);
    await bookingConfirmationHelpers.validatePolicyCode(Constants.POLICY_CODE_D1A);
    await bookingConfirmationHelpers.validateRoomPrice(totalPriceRoom);
    await bookingConfirmationHelpers.validateCityTax(hasCityTax);
    await bookingConfirmationHelpers.validatePaymentCard(await Strings.PAY_ON_ARRIVAL_CCUI.name, card.number);
    await bookingConfirmationHelpers.validateGuestDetailsAgainstBookingConfirmation({
      validateBillingAddress: false,
      guestDetails: {
        reasonForStay: Constants.REASON_FOR_STAY.leisure,
        booker: {
          title: normalisedGuestDetails.title,
          firstName: guestInfo.firstName,
          lastName: guestInfo.lastName,
          emailAddress: guestInfo.emailAddress,
          mobile: normalisedGuestDetails.mobile,
          mobilePrefix: normalisedGuestDetails.mobilePrefix,
          landline: normalisedGuestDetails.landline,
          landlinePrefix: normalisedGuestDetails.landlinePrefix,
          acceptFutureMailing: false,
          address: GuestData.GUEST_ADDRESS,
        },
        stayingGuests: [],
      },
      stayingGuestsAndRoomDetails: [{
        title: normalisedGuestDetails.title,
        firstName: guestInfo.firstName,
        lastName: guestInfo.lastName,
        adultsNumber: 1,
        childrenNumber: 0,
      }],
    });

    console.log('-> When I navigate to home page and click on Manage booking on header');
    console.log(`-> Then I should land on home page
            -> And should be seeing the Options to search a booking`);
    await pages.homePage.open();
    await pages.homePage.headerSection.clickOnManageBookingButton();

    console.log('-> When I Enter Booking reference in the Booking confirmation number field and Click on Search a Booking');
    console.log('-> Then the booking should be displayed');
    await pages.manageBookingCcuiPage.findBooking(bookingReferenceID);

    console.log('-> When I click on ID&V button in the BIC');
    console.log('-> Then ID&V window should open');
    await pages.manageBookingCcuiPage.bookingInformationCard.clickIdvButton();

    console.log('-> When I Select Yes to DPA Override? and Click on close ID&V');
    await pages.manageBookingCcuiPage.bookingInformationCard.idvSection.clickDpaOverrideYes();
    await pages.manageBookingCcuiPage.bookingInformationCard.idvSection.clickXButton();

    const isAmendmentDisabled = await pages.manageBookingCcuiPage.bookingInformationCard.getBookingReasonLabelText() === await Strings.PLEASE_USE_OPERA_UI_PIBA.name;
    expect(isAmendmentDisabled, 'PIBA booking must require amendment in Opera UI').toBe(true);
    console.log('-> Then Amend button should be disabled');
    console.log('-> The booking cannot be amended in CCUI');
    await pages.manageBookingCcuiPage.bookingInformationCard.validateAmendBookingButtonIsDisplayed(true, false);
  });
});
