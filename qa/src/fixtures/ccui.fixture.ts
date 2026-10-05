import {
	AncillariesCcuiPage,
	ConfirmBookingPageCcui,
	ConfirmationErrorPage,
	HomePage,
	LoginPage,
	ManageBookingPage,
	PaymentAmendPage,
	PaymentErrorPage,
	PaymentPageCcui,
	RepeatBookingCcuiPage,
	SearchResultsCcuiPage,
} from '../pages/ccui';
import {
	AmendBookingPage,
	BookingConfirmationPage,
	BookingHistoryPage,
	ChooseYourBathroomPage,
	GuestDetailsPage,
	HotelDetailsPage,
} from '../pages/shared';
import { test as base } from './base.fixture';

// ─── CCUI Fixture Types ─────────────────────────────────────────────────────────

/**
 * CCUI-specific page object fixtures.
 * Each fixture creates a fresh page object instance scoped to the test.
 */
type Pages = {
	homePage: HomePage;
	loginPage: LoginPage;
	manageBookingPage: ManageBookingPage;
	manageBookingCcuiPage: ManageBookingPage;
	searchResultsCcuiPage: SearchResultsCcuiPage;
	ancillariesCcuiPage: AncillariesCcuiPage;
	confirmBookingPage: ConfirmBookingPageCcui;
	confirmBookingPageCcui: ConfirmBookingPageCcui;
	confirmationErrorPage: ConfirmationErrorPage;
	paymentAmendPage: PaymentAmendPage;
	paymentErrorPage: PaymentErrorPage;
	paymentCcuiPage: PaymentPageCcui;
	repeatBookingCcuiPage: RepeatBookingCcuiPage;
	hotelDetailsPage: HotelDetailsPage;
	chooseYourBathroomPage: ChooseYourBathroomPage;
	guestDetailsPage: GuestDetailsPage;
	amendBookingPage: AmendBookingPage;
	bookingConfirmationPage: BookingConfirmationPage;
	bookingHistoryPage: BookingHistoryPage;
};

declare global {
	var ccuiPages: Pages;
}

/**
 * CCUI-specific test fixture type.
 * Extends the base fixture with all CCUI page object fixtures.
 */
type CCUIFixtures = {
	pages: Pages;
};

// ─── CCUI Extended Test ─────────────────────────────────────────────────────────

/**
 * CCUI test fixture — use this in all ccui.premierinn.com test specs.
 *
 * Inherits base fixtures (appPage with dialog handling) and adds
 * all CCUI page objects as injectable fixtures.
 *
 * Usage in specs:
 *   import { test, expect } from '@fixtures/ccui.fixture';
 *
 *   test('CCUI: search booking', async () => {
 *     // Add CCUI page objects when they are implemented.
 *   });
 */
export const test = base.extend<CCUIFixtures>({
	pages: async ({ page }, use) => {
		global.page = page;

		const pages = {
			homePage: new HomePage(),
			loginPage: new LoginPage(),
			manageBookingPage: new ManageBookingPage(),
			manageBookingCcuiPage: new ManageBookingPage(),
			searchResultsCcuiPage: new SearchResultsCcuiPage(),
			ancillariesCcuiPage: new AncillariesCcuiPage(),
			confirmBookingPage: new ConfirmBookingPageCcui(),
			confirmBookingPageCcui: new ConfirmBookingPageCcui(),
			confirmationErrorPage: new ConfirmationErrorPage(),
			paymentAmendPage: new PaymentAmendPage(),
			paymentErrorPage: new PaymentErrorPage(),
			paymentCcuiPage: new PaymentPageCcui(),
			repeatBookingCcuiPage: new RepeatBookingCcuiPage(),
			hotelDetailsPage: new HotelDetailsPage(),
			chooseYourBathroomPage: new ChooseYourBathroomPage(),
			guestDetailsPage: new GuestDetailsPage(),
			amendBookingPage: new AmendBookingPage(),
			bookingConfirmationPage: new BookingConfirmationPage(),
			bookingHistoryPage: new BookingHistoryPage(),
		};
		global.ccuiPages = pages as Pages;
		await use(pages);
	},
});

export { expect } from '@playwright/test';
