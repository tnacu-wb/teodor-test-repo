import { test, expect } from '../../../src/fixtures/pi.fixture';
import type { BookingDetailsIntroExpected, RoomDetailsExpected, TotalCostExpected } from '../../../src/pages/pi';
import type { PricePerNight } from '../../../src/pages/pi/hotelDetails.page';
import {
  ApiBookingConfirmationHelpers,
  ApiBasketCalls,
  ApiHelpers,
  ApiSlugsCalls,
  ApiCalls,
  ApiContentCalls,
  ApiResponses,
  ApiDictionary,
} from '../../../src/api';
import { GuestData } from '@test-data/guestData';
import { Hotels } from '@test-data/hotels';
import { Cards } from '@test-data/cards';
import { Constants } from '@test-data/constants';
import { HotelRates } from '@test-data/hotelRates';
import { Locales } from '@test-data/locales';
import { PmsRoomTypes } from '@test-data/pmsRoomTypes';
import { Rooms } from '@test-data/room';
import { SearchCriteriaData } from '@test-data/searchCriteria';
import { Strings } from '@test-data/strings';
import {
  FeaturesToggles,
  PriceHelpers,
  resetApplicationState,
} from '../../../src/utils';

type AncillariesAdultMeal = {
  id?: string;
  name?: string;
  price?: number;
};

/**
 * Baseline E2E Test: Guest UK Hotel 1-night 1-room Flex POA PIBA + Amendment + Cancellation
 *
 * Full customer journey covering:
 * - DLP validation (page elements, TripAdvisor, map/grid views, distance toggles)
 * - Hotel search via HDP search console
 * - Booking flow (ancillaries with meals, guest details, payment with PIBA POA)
 * - Booking confirmation with API cross-validation
 * - Amendment (Double → Family room type change)
 * - Cancellation and status verification
 *
 * This is a single compact journey test grouped under a Playwright test.describe block.
 */
test.describe('PI Baseline E2E - Guest PIBA Pay on Arrival Amendment', () => {

  test('Test Book as Guest user: 1 night, 1 room (Double), 2 adults, Flex rate, meals, donations - POA BAC card and Amend by change room type. TestCase ID: 380779.', async ({ pages }) => {
    console.log('https://whitbread.yourzephyr.com/flex/html5/tcr/467;testcaseId=1282640;viewType=list;pageSize=50;pageView=search;offset=0;searchText=testcaseId%20%3D%20380779;searchType=zql;inRelease=true;currentIndex=1;usageHistoryGridSize=50');

  const guestInfo = GuestData.generateGuestInfo();
  const hotel = Hotels.LONDON_HEATHROW_AIRPORT;
  const hotelRate = HotelRates.PI_FLEX;
  const hasCityTax = false;

  const toIsoDate = (date: Date): string => date.toISOString().slice(0, 10);

  console.log('-> Given I want to book a room as a PI guest user');
  await resetApplicationState();
  await FeaturesToggles.overrideFeaturesToggles([{ name: 'release_pi_web_push_notifications', value: true }]);

  const hotelDetails = await SearchCriteriaData.getSearchCriteriaAndHotelAvailability({
    hotelName: hotel.name,
    hotelId: hotel.id,
    daysNumberForArrivalDate: 3,
    daysNumber: 1,
    rooms: [Rooms.DOUBLE_2_ADULTS_0_CHILDREN, Rooms.FAMILY_2_ADULTS_1_CHILDREN],
    ratePlanCode: hotelRate.ratePlanCode,
  });
  expect(hotelDetails.hotelAvailabilityResponse, 'DOUBLE and FAMILY room availability must be confirmed before booking').toBeTruthy();
  const searchCriteria = new SearchCriteriaData({
    ...hotelDetails.searchCriteria,
    rooms: [hotelDetails.searchCriteria.rooms[0]],
  });

  const dlpPage = pages.destinationLandingPage;
  const primaryDlpPath = Hotels.DLP_PATHS.BIRMINGHAM;
  const hiddenDistanceDlpPath = Hotels.DLP_PATHS.LUTON;

    console.log('-> When I navigate to Destination landing page (DLP)');
    await dlpPage.open(primaryDlpPath);
    await dlpPage.validatePage();
    console.log('-> Then I should see that the DLP page is displayed with its elements');

    await dlpPage.notificationPopup.dismissIfPresent();
    const dlpPath = await dlpPage.getDlpPathFromUrl();

    const dlpContent = await ApiDictionary.fetchDlpContentServiceDictionary({
      dlpPath,
    });

    const graphqlDlpContent = await ApiContentCalls.graphqlGetDlpContentService({
      dlpPath,
    }) as any;

    await ApiHelpers.validateAemDlpContentServiceDictionaryAgainstDlpInformationAPIResponse({
      dictionary: dlpContent as any,
      apiData: graphqlDlpContent as any,
    });
    await dlpPage.validateDlpPageElements(dlpContent as any);

    console.log(`-> When I check the custom notification prompt\n                        And I click “Allow”`);
    console.log('-> Then the custom prompt closes, the close count is increased by one and the last closed time is updated');

    const hotelIds = (dlpContent as any).hotels?.map((hotel: any) => hotel.code).filter(Boolean) ?? [];

    console.log('-> When I check Trip Advisor on Grid view');
    console.log('-> Then I can click on reviews link & a new tab will open with HDP focused on reviews section.');
    const tripAdvisorReviews = await ApiCalls.graphqlGetHotelTripAdvisorReview({
      hotelIds,
      longitudeRef: (dlpContent as any).coordinates?.longitude ?? 0,
      latitudeRef: (dlpContent as any).coordinates?.latitude ?? 0,
    }) as Array<{ rating: number; numberOfReviews: number; slug?: string }>;
    const expectedReviews = tripAdvisorReviews.map((review) => ({
      rating: review.rating,
      numberOfReviews: review.numberOfReviews,
    }));
    await dlpPage.validateTripAdvisorSection(expectedReviews);
    const tripAdvisorReviewSlug = tripAdvisorReviews[0].slug!;
    await dlpPage.clickTripAdvisorReviewLinkAndValidate(tripAdvisorReviewSlug);

    await dlpPage.open(primaryDlpPath);

    console.log('-> When I click on "Map View"');
    console.log('-> Then the page should change from a list view to a map view');
    await dlpPage.clickMapView();

    console.log('-> When I check hotel card pins from the Map view');
    console.log(`-> Then each hotel will display:   \n                        - hotel image\n                        - hotel name\n                        - distance from the location\n                        - facilities\n                        - TripAdvisor rating\n                        - option to go to the hotel detail page`);
    const dlpMap = (dlpContent as any).map ?? (dlpContent as any).coordinates;
    const mapViewHotels = await ApiCalls.graphqlGetHotelsInformationForDLPMapView({
      hotelIds,
      longitudeRef: dlpMap.longitude,
      latitudeRef: dlpMap.latitude,
      tripAdvisorDataRequired: true,
    }) as any;
    const regionPosition = `${dlpMap.latitude},${dlpMap.longitude}`;
    const mapViewHotelsForValidation = mapViewHotels.map((h: any) => ({
      name: h.name,
      position: h.position ?? `${h.coordinates?.latitude ?? h.latitude},${h.coordinates?.longitude ?? h.longitude}`,
      distanceFromReference: h.distanceFromReference,
      hotelFacilities: h.hotelFacilities,
      rating: h.rating ?? h.tripAdvisorReviews?.rating,
    }));
    await dlpPage.validateMapViewCards(mapViewHotelsForValidation, regionPosition);

    console.log(`-> When I click on a map pin\n                        And I click on the hotel card details`);
    console.log(`-> Then I am redirected to the Hotel detail page for that hotel\n                        And "Back" action from HDP brings me back on DLP - Map view`);

    await dlpPage.clickMapMarker(0, regionPosition);
    await dlpPage.clickViewHotelOnMapCard();
    await pages.hotelDetailsPage.validatePage();
    await page.goBack({ waitUntil: 'domcontentloaded' });
    await dlpPage.validatePage();
    await dlpPage.verifyMapViewIsActive();
    await dlpPage.clickGridView();

    console.log('-> When I check the search results in map view, after loading more hotels');
    console.log('-> Then I should see that the extended list of hotels from "Show more" - Grid view, are visible on the Map view also');
    await dlpPage.validateHotelListElements();

    await dlpPage.validateHotelDistanceDisplayed();
    await dlpPage.validateAllHotelCardsShowDistance();

    console.log('-> When I check the hotels list from Grid view, after loading more hotels');
    console.log('-> Then I should see all hotels displayed in the list');
    await dlpPage.clickShowMoreUntilAllLoaded();
    const hotelList = await ApiHelpers.getDlpHotelsListFromDictionary({ dlpPath: primaryDlpPath });
    await dlpPage.validateHotelListElements(hotelList);
    const hotelCardCount = await dlpPage.getHotelCardCount();
    expect(hotelCardCount, 'DLP grid must display at least one hotel card').toBeGreaterThan(0);

    // Map view depends on Google Maps loading
    await dlpPage.validateMapPinsMatchGridCards();
    await dlpPage.clickGridView();
    console.log('-> When I go to another Destination landing page where Distance from search is not available (restricted from AEM)');
    console.log('-> Then I should see that the Distance from search is not displayed on hotel cards, on both Grid and Map view');
    // The AEM configuration controls whether distance is shown or hidden per-DLP.
    // Fetch the DLP content to check the actual hideHotelDistance flag before asserting.
    await dlpPage.open(hiddenDistanceDlpPath);
    await dlpPage.validatePage();
    await dlpPage.notificationPopup.dismissIfPresent();
    const hiddenCurrentDlpPath = await dlpPage.getDlpPathFromUrl();
    const hiddenDlpContent = await ApiDictionary.fetchDlpContentServiceDictionary({
      dlpPath: hiddenCurrentDlpPath,
    }) as any;
    await ApiContentCalls.graphqlGetDlpContentService({
      dlpPath: hiddenCurrentDlpPath,
    }) as any;
    await dlpPage.validateAllHotelCardsHideDistance({ hideHotelDistance: hiddenDlpContent.map?.hideHotelDistance });

    await dlpPage.clickMapView();
    const hiddenMapViewHotels = await ApiCalls.graphqlGetHotelsInformationForDLPMapView({
      hotelIds: hiddenDlpContent.hotels?.map((hotel: any) => hotel.code).filter(Boolean) ?? [],
      longitudeRef: hiddenDlpContent.map.longitude,
      latitudeRef: hiddenDlpContent.map.latitude,
      tripAdvisorDataRequired: true,
    }) as any[];
    await dlpPage.validateMapViewHiddenHotelDistance({
      hotels: hiddenMapViewHotels.map((h: any) => ({ position: h.position ?? `${h.coordinates?.latitude ?? h.latitude},${h.coordinates?.longitude ?? h.longitude}` })),
      regionPosition: `${hiddenDlpContent.map.latitude},${hiddenDlpContent.map.longitude}`,
      hideHotelDistance: hiddenDlpContent.map?.hideHotelDistance,
    });

    console.log('-> When I search for a specific PI brand Hotel for 1 room for 2 adults for 1 night and click on the Search button.');
    await pages.hotelDetailsPage.open(hotel.slug!);
    await pages.homePage.searchConsole.performSearch({
      searchCriteria,
      useSuggestedHotelName: true,
    });
    await pages.hotelDetailsPage.validatePage();
    await pages.hotelDetailsPage.validateNavigatedToHotel(hotel.slug!);
    console.log('-> Then I should be directed to Hotel details page of my selected hotel.');
    const hotelTitleData = await ApiSlugsCalls.graphqlGetHotelTitle({
      slug: hotel.slug,
    });
    await pages.hotelDetailsPage.validateHotelTitle(hotelTitleData.title);

    console.log('-> When I select FLEX rate available and check the Booking summary panel');
    await pages.hotelDetailsPage.chooseYourRateSection.clickSpecificRateForSpecificRoom({
      roomName: await PmsRoomTypes.PI_STANDARD_ROOM.name.name,
      rateName: HotelRates.PI_FLEX.name,
    });
    const selectedRoomName = await pages.hotelDetailsPage.chooseYourRateSection.getSelectedRoomName();

    console.log('-> Then it should be populated with the selected rate and room class information');
    await pages.hotelDetailsPage.bookNowSummarySection.validateBookingSummaryRatePlan({
      ratePlan: await pages.hotelDetailsPage.chooseYourRateSection.getSelectedRateName(),
      roomsCount: searchCriteria.rooms.length,
    });
    await pages.hotelDetailsPage.bookNowSummarySection.validateTotalPriceRate();
    await pages.hotelDetailsPage.bookNowSummarySection.clickSeeBreakdownLink();
    const expectedPricesPerNight = await pages.hotelDetailsPage.bookNowSummarySection.extractPricesAndDatesPerNight({
      rooms: searchCriteria.rooms,
    });
    const roomPricesPerNight = await pages.hotelDetailsPage.bookNowSummarySection.getTotalRoomPrice({
      rooms: searchCriteria.rooms,
    });

    console.log('-> When I click on Book now button.');
    await pages.hotelDetailsPage.bookNowSummarySection.clickBookNow();

    await pages.hotelDetailsPage.bookNowSummarySection.closePremierPlusRoomUpgradeModalIfPresent();

    await pages.chooseYourBathroomPage.clickContinueIfChooseBathroomPageIsDisplayed();

    // ─── Ancillaries Page ────────────────────────────────────────────────────────
    console.log('-> Then I\'ll be directed to the Ancillaries page to continue the flow');
    await pages.ancillariesPage.validatePage();
    const basketReferenceId = await pages.ancillariesPage.getBasketReferenceIdFromUrl();
    expect(basketReferenceId, 'basketReferenceId must be extracted from the URL').toBeTruthy();
    const basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReferenceId);
    await basket.validateBasketStatus(ApiResponses.Basket.STATUS_OPEN);
    await pages.ancillariesPage.expandSectionOnMobile();
    console.log('-> When I select "Breakfast" for both adults and check the Booking summary panel.');
    console.log(`-> Then I should see the meals cost added to my reservation per night for each adult \n            for which was selected from each room`);
    const standardBreakfastTitle = global.browser?.options?.locale === Locales.GB_EN.name
      ? await Strings.PREMIER_INN_BREAKFAST.name
      : await Strings.BREAKFAST_ANCILLARIES.name;
    const bookingSummaryTotalBeforeMeals = await pages.ancillariesPage.getBookingSummaryTotal();
    const bookingFlowIdForAncillaries = String(await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({
      hotelId: hotel.id,
      ratePlanCode: hotelRate.ratePlanCode,
    }));
    const adultMeals = await ApiCalls.graphqlGetAncillariesAdultMeals({
      hotelId: hotel.id,
      startDate: searchCriteria.arrivalDate,
      endDate: searchCriteria.departureDate,
      adultsNumber: searchCriteria.getAdults(),
      childrenNumber: searchCriteria.getChildren(),
      nightsNumber: searchCriteria.nights,
      bookingFlowId: bookingFlowIdForAncillaries,
      basketReferenceId,
    }) as AncillariesAdultMeal[];
    const breakfastMeal = adultMeals.find(meal => meal.id === Constants.PI_BREAKFAST_PACKAGE_CODE);
    expect(breakfastMeal, `Adult meal with package code "${Constants.PI_BREAKFAST_PACKAGE_CODE}" must be available for total cost validation`).toBeTruthy();
    const breakfastMealPrice = breakfastMeal?.price;
    expect(typeof breakfastMealPrice, `Adult meal with package code "${Constants.PI_BREAKFAST_PACKAGE_CODE}" must have a price for total cost validation`).toBe('number');
    const breakfastMealName = breakfastMeal?.name?.trim() || standardBreakfastTitle;
    await pages.ancillariesPage.addBreakfastForAdults(2, { breakfastTitle: breakfastMealName, exactMatch: false });

    const bookingSummaryTotal = await pages.ancillariesPage.getBookingSummaryTotal();
    const expectedBookingSummaryTotal = PriceHelpers.getPriceAmountFromUiLabel(bookingSummaryTotalBeforeMeals)
      + ((breakfastMealPrice ?? 0) * searchCriteria.getAdults() * searchCriteria.nights);
    expect(PriceHelpers.getPriceAmountFromUiLabel(bookingSummaryTotal), 'Booking summary total should include selected adult meals').toBe(
      Number(expectedBookingSummaryTotal.toFixed(2))
    );
    await PriceHelpers.validateCurrencySymbol(
      PriceHelpers.getPriceCurrencySymbolFromUiLabel(bookingSummaryTotal),
      Constants.UK_CURRENCY_CODE,
      'Booking summary total'
    );

    await pages.ancillariesPage.validateAdultMealsPerRoom(0, standardBreakfastTitle);

    console.log('-> When I click on the continue button from the Ancillaries page panel');
    await pages.ancillariesPage.clickContinue();

    // ─── Guest Details Page ──────────────────────────────────────────────────────
    console.log('-> Then I should be directed to Guest details');
    await pages.guestDetailsPage.validatePage();
    await pages.guestDetailsPage.expandBookingSummaryOnMobile();
    await pages.guestDetailsPage.selectReasonForStay(GuestData.DEFAULT_REASON_FOR_STAY);
    await pages.guestDetailsPage.fillBookerInformation(guestInfo);
    await pages.guestDetailsPage.selectManualAddressEntry();
    await pages.guestDetailsPage.fillAddress(GuestData.GUEST_ADDRESS);
    const normalisedGuestDetails = await pages.guestDetailsPage.readNormalisedGuestDetails();
    expect(normalisedGuestDetails.title, 'Normalised guest title must be captured for confirmation validation').toBeTruthy();
    console.log(`-> When I choose the booking purpose as 'Leisure'\n                     ->  And click on the Continue button`);
    await pages.guestDetailsPage.clickContinueToPayment();

    // ─── Payment Page ────────────────────────────────────────────────────────────
    console.log('-> Then I should be directed to the Payments page');
    await pages.paymentPage.validatePage();

    const totalCostBeforeDonation = await pages.paymentPage.getTotalCost();

    console.log('-> When I check the booking summary from the Payments page with £3 donation');
    console.log('-> Then  I should see that the donation is included in the total price.');
    if (global.browser?.options?.locale === Locales.GB_EN.name) {
      await pages.paymentPage.selectDonation(await Strings.DONATE_THREE_POUNDS.name);
      await pages.paymentPage.validateTotalCostIsUpdated(totalCostBeforeDonation, true);
    }
    const totalCostWithDonations = await pages.paymentPage.getTotalCost();
    await pages.paymentPage.validateTotalCostAmountAndCurrency(totalCostWithDonations, Constants.UK_CURRENCY_CODE);

    console.log(`-> When I select the pay on arrival option \n                    -> And guarantee the reservation with a New Credit/ Debit card\n                    -> And click on Continue button`);
    console.log('-> Then I should be directed to the Payment Details page to enter my Card details');
    await pages.paymentPage.selectPayOnArrival();

    await pages.paymentPage.selectPIBACard();
    await pages.paymentPage.clickContinueToPaymentDetails();
    // ─── Payment Details Page (Worldline iframe) ─────────────────────────────────
    console.log('-> When I fill in the details for my New BAC card and click the Confirm button.');
    await pages.paymentDetailsPage.switchToPaymentDetailsIFrame();
    await pages.paymentDetailsPage.enterCardDetails(Cards.DEFAULT_PIBA_CARD);
    await pages.paymentDetailsPage.submitPayment();
    // ─── 3D Secure Challenge ─────────────────────────────────────────────────────
    await pages.threeDSecurePage.confirmPayment();
    await pages.threeDSecurePage.waitForConfirmationPage();

    expect(basketReferenceId, 'basketReferenceId must be available from the ancillaries step').toBeTruthy();
    expect(totalCostWithDonations, 'totalCostWithDonations must be captured from the payment step').toBeTruthy();

    console.log('-> Then I should be directed to the Booking Confirmation page having the booking information displayed and confirmed.');
    await pages.confirmBookingPage.validatePage();

    const confirmationBookingReference = await pages.confirmBookingPage.getBookingReference();
    expect(confirmationBookingReference, 'Booking reference must be present on confirmation page').toBeTruthy();

    const basketDetails = await ApiHelpers.waitForBasketStatus(basketReferenceId, ApiResponses.Basket.STATUS_COMPLETED);
    expect(basketDetails.bookingReference, 'Completed basket must contain a booking reference').toBeTruthy();
    const bookingReference = String(basketDetails.bookingReference);

    const bookingFlowId = String(await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: hotel.id, ratePlanCode: HotelRates.PI_FLEX.ratePlanCode }));
    expect(bookingFlowId, 'bookingFlowId must be retrieved after booking confirmation').toBeTruthy();

    const hotelInformation = await ApiContentCalls.graphqlGetHotelInformation({
      hotelId: hotel.id,
    });

    const introExpected: BookingDetailsIntroExpected = {
      bookingReference,
      guestName: `${guestInfo.firstName} ${guestInfo.lastName}`,
      guestTitle: normalisedGuestDetails.title,
      hotelName: hotelInformation.name ?? hotel.name,
      hotelType: hotel.type ?? 'hotel',
    };
    await pages.confirmBookingPage.validateBookingDetailsIntro(introExpected);

    const mealsResponse = await ApiCalls.graphqlGetMealsPackages({
      hotelId: hotel.id,
      startDate: searchCriteria.arrivalDate,
      endDate: searchCriteria.departureDate,
      adultsNumber: searchCriteria.getAdults(),
      childrenNumber: searchCriteria.getChildren(),
      nightsNumber: searchCriteria.nights,
      bookingFlowId,
      basketReferenceId,
    }) as {
      adultMeals?: Array<{ id: string; name: string; price?: number }>;
      roomSelection?: Array<{ packagesSelection?: Array<{ id: string; noOfSelections?: number }> }>;
    };
    const adultMeal = mealsResponse.adultMeals?.find(meal => meal.id === Constants.PI_BREAKFAST_PACKAGE_CODE);
    const selectedAdultMeal = mealsResponse.roomSelection?.[0]?.packagesSelection?.find(packageSelection => (
      packageSelection.id === Constants.PI_BREAKFAST_PACKAGE_CODE
    ));

    const mealsPackageBe = adultMeal && selectedAdultMeal
      ? {
        packageCode: adultMeal.id,
        totalQuantity: selectedAdultMeal.noOfSelections ?? searchCriteria.getAdults(),
        unitPrice: adultMeal.price ?? 0,
      }
      : { packageCode: '', totalQuantity: 0, unitPrice: 0 };

    const roomDetailsExpected: RoomDetailsExpected = {
      roomType: Hotels.RoomTypes.DOUBLE,
      checkIn: toIsoDate(searchCriteria.arrivalDate),
      checkOut: toIsoDate(searchCriteria.departureDate),
      adults: searchCriteria.getAdults(),
      meals: adultMeal ? adultMeal.name : undefined,
    };
    await pages.confirmBookingPage.validateRoomDetails(roomDetailsExpected, 1);
    await pages.confirmBookingPage.validateHotelDirections(hotelInformation.directions ?? '');

    const totalCostExpected: TotalCostExpected = {
      amount: totalCostWithDonations,
      currencyCode: Constants.UK_CURRENCY_CODE,
      paymentLabel: await Strings.PAY_ON_ARRIVAL.name,
    };
    await pages.confirmBookingPage.validateTotalCost(totalCostExpected);

    // ─── Opera / Booking Confirmation API Cross-Validation ───────────────────────
    const bookingConfirmation = await ApiContentCalls.graphqlGetBookingConfirmation({
      basketReference: basketReferenceId,
      bookingChannel: 'WEB',
    }) as Record<string, any>;
    const bookingConfirmationHelpers = new ApiBookingConfirmationHelpers(bookingConfirmation);

    await bookingConfirmationHelpers.validateHotel(hotel);
    await bookingConfirmationHelpers.validateStayingDates(searchCriteria);
    await bookingConfirmationHelpers.validateRoomsOccupancy(searchCriteria);
    await bookingConfirmationHelpers.validateRoomTypes(selectedRoomName, hotel.type ?? 'hotel');
    await bookingConfirmationHelpers.validateRatePlan(hotelRate.ratePlanCode);

    await bookingConfirmationHelpers.validateRatesPerNight(expectedPricesPerNight);
    await bookingConfirmationHelpers.validateRoomPrice(roomPricesPerNight);

    await bookingConfirmationHelpers.validateCurrency(Constants.UK_CURRENCY_CODE);
    await bookingConfirmationHelpers.validateBookingFlowId(bookingFlowId);
    await bookingConfirmationHelpers.validateMealPackagesPerRoom([mealsPackageBe], searchCriteria);
    await bookingConfirmationHelpers.validateDepositPoliciesForAllRooms(Constants.POLICY_CODE_D1A);
    const totalCostNumeric = parseFloat(totalCostWithDonations.replace(/[^0-9.]/g, ''));
    await bookingConfirmationHelpers.validateTotalCostNoDiscountsNoAmendment(
      totalCostNumeric,
      Constants.PAYMENT_OPTION.payOnArrival,
    );
    await bookingConfirmationHelpers.validatePaymentCard(
      Constants.PAYMENT_OPTION.payOnArrival,
      Cards.DEFAULT_PIBA_CARD.number,
    );
    console.log('-> When I check in Opera UI platform for my newly submitted reservation');
    console.log('-> Then I should see the reservation having the same details as the ones submitted from the PI website');
    await bookingConfirmationHelpers.validateCityTax(hasCityTax);
    await bookingConfirmationHelpers.validateGuestDetailsAgainstBookingConfirmation(
      {
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
            address: {
              ...GuestData.GUEST_ADDRESS,
              addressLine1: normalisedGuestDetails.addressLine1,
              addressLine2: normalisedGuestDetails.addressLine2,
              addressLine3: normalisedGuestDetails.addressLine3,
              postalCode: normalisedGuestDetails.postalCode,
            },
          },
          stayingGuests: [],
        },
        stayingGuestsAndRoomDetails: [{
          firstName: guestInfo.firstName,
          lastName: guestInfo.lastName,
          adultsNumber: searchCriteria.rooms[0].adultsNumber,
          childrenNumber: searchCriteria.rooms[0].childrenNumber,
        }],
      }
    );
    console.log('[Confirmation] API cross-validation completed successfully');

    console.log('-> When I check in Opera UI platform the payment type and methods for my reservation');
    console.log('-> When I want to amend a reservation, and I click on the Manage booking and Submit the booking reference and Arrival date');
    expect(bookingReference, 'bookingReference must be available from the confirmation step').toBeTruthy();
    await pages.homePage.open();
    await pages.manageBookingPage.openModal();
    await pages.manageBookingPage.validateModalVisible(true);
    await pages.manageBookingPage.searchBooking({
      reference: bookingReference,
      surname: guestInfo.lastName,
      arrivalDate: toIsoDate(searchCriteria.arrivalDate),
    });
    console.log('-> Then Booking information Card should be displayed');
    await pages.manageBookingPage.validatePage();
    await pages.manageBookingPage.validateBookingReferenceId(bookingReference);
    console.log('-> When I edit the room to change the room type to Family by adding a child');
    await pages.manageBookingPage.clickAmendBookingButton();
    await pages.amendBookingPage.validatePage();
    await pages.amendBookingPage.clickRoomAndGuestsSection();
    await pages.amendBookingPage.editSpecificRoom({ roomIndex: 0 });
    await pages.amendBookingPage.selectNumberOfChildren(GuestData.FAMILY_ROOM_OCCUPANCY.childrenNumber);
    const familyRoomType = await Strings.FAMILY_GENERIC.name;
    const familyRoomLabel = await Strings.FAMILY_ROOM_LOWER_CASE.name;
    await pages.amendBookingPage.selectRoomType(familyRoomType);
    await pages.amendBookingPage.clickCheckAvailability();
    await pages.amendBookingPage.clickUpdateRoom();
    await pages.amendBookingPage.validateRoomInfoCardIncludesText({ roomIndex: 0, text: familyRoomLabel });
    await pages.amendBookingPage.validateRoomSuccessfullyUpdated({ roomIndex: 0 });
    await pages.amendBookingPage.validateAdultsAndChildrenNumbersForRoomIndexes({
      roomIndexesArray: [0],
      expectedAdults: GuestData.FAMILY_ROOM_OCCUPANCY.adultsNumber,
      expectedChildren: GuestData.FAMILY_ROOM_OCCUPANCY.childrenNumber,
    });
    console.log('-> Then the changes should be successfully saved and the total price remains the same in the booking summary side of the screen');
    await pages.amendBookingPage.validateTotalCostIsUpdated(totalCostWithDonations, false);
    await pages.amendBookingPage.validateTotalCostAmountAndCurrency(totalCostWithDonations, Constants.UK_CURRENCY_CODE);
    console.log('-> When I press Confirm changes button');
    await pages.amendBookingPage.clickConfirmChanges();
    console.log('-> Then I am being redirected to the booking confirmation page and all the changes made for that reservation are reflected in the page');
    await pages.bookingConfirmationPage.validatePage();
    await pages.bookingConfirmationPage.validateAmendSuccessNotification(true);
    const adultsLabel = await Strings.ADULTS.name;
    const childLabel = await Strings.CHILD.name;
    const familyRoomOccupancyLabel = `2 ${adultsLabel}, 1 ${childLabel}`;
    await pages.bookingConfirmationPage.validateRoomAdultsAndChildren([0], [familyRoomOccupancyLabel]);
    await pages.bookingConfirmationPage.validateRoomTypeLabel({ roomTypeLabelAem: familyRoomLabel });
    const numericTotalAfterAmend = Number.parseFloat(totalCostWithDonations.replace(/[^0-9.]/g, '')).toFixed(2);
    await pages.bookingConfirmationPage.validateTotalCostAmountAndCurrency(numericTotalAfterAmend, Constants.UK_CURRENCY_CODE);
    const amendedBasketDetails = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReferenceId);
    await amendedBasketDetails.validateBasketStatus(ApiResponses.Basket.STATUS_COMPLETED);
    await pages.bookingHistoryPage.validateBookingReferenceId(bookingReference);
    await pages.bookingHistoryPage.validateLeadGuestName(`${guestInfo.firstName} ${guestInfo.lastName}`);
    await pages.bookingHistoryPage.validateRoomGuests(familyRoomOccupancyLabel);
    await pages.bookingHistoryPage.validatePayOnArrivalTotalCost(totalCostWithDonations);
    console.log('[Amendment] Amendment flow completed successfully');

    expect(bookingReference, 'bookingReference must be available for cancellation').toBeTruthy();
    expect(basketReferenceId, 'basketReferenceId must be available for API cancellation validation').toBeTruthy();
    await pages.homePage.open();
    await pages.manageBookingPage.openModal();
    await pages.manageBookingPage.validateModalVisible(true);
    await pages.manageBookingPage.searchBooking({
      reference: bookingReference,
      surname: guestInfo.lastName,
      arrivalDate: toIsoDate(searchCriteria.arrivalDate),
    });
    await pages.manageBookingPage.validatePage();
    console.log('-> When I cancel the booking');
    await pages.manageBookingPage.clickCancelBookingButton();
    await pages.manageBookingPage.confirmCancellation();
    await pages.manageBookingPage.validateCancellationSuccessMessage(bookingReference);
    await pages.manageBookingPage.closeCancelModal();
    console.log('-> Then I should see Booking status as \'Cancelled\'');
    const cancelledBasketDetails = await ApiHelpers.waitForBasketStatus(basketReferenceId, ApiResponses.Basket.STATUS_CANCELLED);
    await cancelledBasketDetails.validatePaymentStatus(ApiResponses.Basket.PAYMENT_STATUS_COMPLETED);
    await ApiHelpers.validateReservationIsCancelled(hotel.id, cancelledBasketDetails);

    // Re-open manage booking and validate cancelled BIC state for Family occupancy.
    await pages.homePage.open();
    await pages.manageBookingPage.openModal();
    await pages.manageBookingPage.validateModalVisible(true);
    await pages.manageBookingPage.searchBooking({
      reference: bookingReference,
      surname: guestInfo.lastName,
      arrivalDate: toIsoDate(searchCriteria.arrivalDate),
    });
    await pages.manageBookingPage.validatePage();
    await pages.manageBookingPage.validateBookingReferenceId(bookingReference);
    await pages.bookingHistoryPage.validateCanceledBIC({
      roomsList: [{ adultsNumber: GuestData.FAMILY_ROOM_OCCUPANCY.adultsNumber, childrenNumber: GuestData.FAMILY_ROOM_OCCUPANCY.childrenNumber }],
    });
    console.log('[Cancellation] Cancellation flow completed successfully');
    }
  );
});
