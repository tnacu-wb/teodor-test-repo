import { test as base } from './base.fixture';
import {
  HomePage,
  AncillariesPage,
  PaymentPage,
  PaymentDetailsPage,
  ThreeDSecurePage,
  ConfirmBookingPage,
  ManageBookingPage,
  DestinationLandingPage,
  SearchResultsPage,
  ChooseYourRoomTypePage,
} from '../pages/pi';
import {
  AmendBookingPage,
  BookingConfirmationPage,
  BookingHistoryPage,
  ChooseYourBathroomPage,
  GuestDetailsPage,
  HotelDetailsPage,
} from '../pages/shared';
import {
  BartHomePage,
  BuyOurBedPage,
  BartHotelDetailsPage,
  LocalTaxesPage,
  NewHotelsPage,
  PressePage,
  ResetMyAccountPage,
  BartSearchConsolePage,
  BartSearchResultsPage,
  TermsAndConditionsPage,
  ThingsToDoPage,
} from '../pages/bart';

// ─── PI Fixture Types ───────────────────────────────────────────────────────────

/**
 * PI-specific page object fixtures.
 * Each fixture creates a fresh page object instance scoped to the test.
 */
type Pages = {
  homePage: HomePage;
  hotelDetailsPage: HotelDetailsPage;
  ancillariesPage: AncillariesPage;
  guestDetailsPage: GuestDetailsPage;
  paymentPage: PaymentPage;
  paymentDetailsPage: PaymentDetailsPage;
  threeDSecurePage: ThreeDSecurePage;
  confirmBookingPage: ConfirmBookingPage;
  bookingConfirmationPage: BookingConfirmationPage;
  bookingHistoryPage: BookingHistoryPage;
  manageBookingPage: ManageBookingPage;
  amendBookingPage: AmendBookingPage;
  destinationLandingPage: DestinationLandingPage;
  searchResultsPage: SearchResultsPage;
  chooseYourBathroomPage: ChooseYourBathroomPage;
  chooseYourRoomTypePage: ChooseYourRoomTypePage;
  bartHomePage: BartHomePage;
  buyOurBedPage: BuyOurBedPage;
  bartHotelDetailsPage: BartHotelDetailsPage;
  localTaxesPage: LocalTaxesPage;
  newHotelsPage: NewHotelsPage;
  pressePage: PressePage;
  resetMyAccountPage: ResetMyAccountPage;
  bartSearchConsolePage: BartSearchConsolePage;
  bartSearchResultsPage: BartSearchResultsPage;
  termsAndConditionsPage: TermsAndConditionsPage;
  thingsToDoPage: ThingsToDoPage;
};

declare global {
  var piPages: Pages;
}

/**
 * PI-specific test fixture type.
 * Extends the base fixture with all PI page object fixtures.
 */
type PIFixtures = {
  pages: Pages;
};

// ─── PI Extended Test ───────────────────────────────────────────────────────────

/**
 * PI test fixture — use this in all premierinn.com test specs.
 *
 * Inherits base fixtures (appPage with dialog handling) and adds
 * all PI page objects as injectable fixtures.
 *
 * Usage in specs:
 *   import { test, expect } from '@fixtures/pi.fixture';
 *
 *   test('book a room', async ({ homePage, hotelDetailsPage, paymentPage }) => {
 *     await homePage.goto();
 *     // ...
 *   });
 */
export const test = base.extend<PIFixtures>({
  pages: async ({ page }, use) => {
    global.page = page;

    const pages = {
      homePage: new HomePage(),
      hotelDetailsPage: new HotelDetailsPage(),
      ancillariesPage: new AncillariesPage(),
      guestDetailsPage: new GuestDetailsPage(),
      paymentPage: new PaymentPage(),
      paymentDetailsPage: new PaymentDetailsPage(),
      threeDSecurePage: new ThreeDSecurePage(),
      confirmBookingPage: new ConfirmBookingPage(),
      bookingConfirmationPage: new BookingConfirmationPage(),
      bookingHistoryPage: new BookingHistoryPage(),
      manageBookingPage: new ManageBookingPage(),
      amendBookingPage: new AmendBookingPage(),
      destinationLandingPage: new DestinationLandingPage(),
      searchResultsPage: new SearchResultsPage(),
      chooseYourBathroomPage: new ChooseYourBathroomPage(),
      chooseYourRoomTypePage: new ChooseYourRoomTypePage(),
      bartHomePage: new BartHomePage(),
      buyOurBedPage: new BuyOurBedPage(),
      bartHotelDetailsPage: new BartHotelDetailsPage(),
      localTaxesPage: new LocalTaxesPage(),
      newHotelsPage: new NewHotelsPage(),
      pressePage: new PressePage(),
      resetMyAccountPage: new ResetMyAccountPage(),
      bartSearchConsolePage: new BartSearchConsolePage(),
      bartSearchResultsPage: new BartSearchResultsPage(),
      termsAndConditionsPage: new TermsAndConditionsPage(),
      thingsToDoPage: new ThingsToDoPage(),
    };
    global.piPages = pages as Pages;
    await use(pages);
  },
});

export { expect } from '@playwright/test';
