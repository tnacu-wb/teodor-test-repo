import { test, expect } from '../../../src/fixtures/base.fixture';
import {
  HomePage,
  HotelDetailsPage,
  AncillariesPage,
  GuestDetailsPage,
  PaymentPage,
  PaymentDetailsPage,
  ThreeDSecurePage,
  ConfirmBookingPage,
  ChooseYourBathroomPage,
} from '../../../src/pages/pi';
import { GuestData } from '@test-data/guestData';
import { Hotels } from '@test-data/hotels';
import { Cards } from '@test-data/cards';

test.describe('TC-468889: Guest PIBA Pay on Arrival Booking', () => {
  test.setTimeout(90_000);

  test('TC-468889: Guest user books UK hotel with PIBA Pay on Arrival', async ({ page }) => {
    // Build hotel slug URL with search parameters
    const checkIn = new Date(GuestData.CHECK_IN_DATE);
    const arrDD = String(checkIn.getDate()).padStart(2, '0');
    const arrMM = String(checkIn.getMonth() + 1).padStart(2, '0');
    const arrYYYY = String(checkIn.getFullYear());
    const hotelFullUrl = `/gb/en${Hotels.DEFAULT_HOTEL.slug}?ARRdd=${arrDD}&ARRmm=${arrMM}&ARRyyyy=${arrYYYY}&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=PI`;

    // Step 1: Navigate to homepage (sets cookies), then to hotel details page
    const homePage = new HomePage();
    await homePage.open();
    await page.goto(hotelFullUrl, { waitUntil: 'domcontentloaded', timeout: 30000 });

    // Step 2: Validate Hotel Details Page, select Flex rate, and click Book Now
    const hotelDetailsPage = new HotelDetailsPage();
    await hotelDetailsPage.validatePage();
    await hotelDetailsPage.chooseYourRateSection.clickFlexRate();
    await hotelDetailsPage.bookNowSummarySection.clickBookNow();

    // Step 3: Handle optional Choose Your Bathroom interstitial
    const chooseYourBathroomPage = new ChooseYourBathroomPage();
    await chooseYourBathroomPage.clickContinueIfChooseBathroomPageIsDisplayed();

    // Step 4: Validate Ancillaries Page and click Continue (no meals)
    const ancillariesPage = new AncillariesPage();
    await ancillariesPage.validatePage();
    await ancillariesPage.clickContinue();

    // Step 5: Fill guest details and submit
    const guestDetailsPage = new GuestDetailsPage();
    await guestDetailsPage.validatePage();
    await guestDetailsPage.selectReasonForStay('leisure');
    await guestDetailsPage.fillBookerInformation(GuestData.GUEST_INFO);
    await guestDetailsPage.fillAddress(GuestData.GUEST_ADDRESS);
    await guestDetailsPage.clickContinue();

    // Step 6: Select payment options and continue to payment details
    const paymentPage = new PaymentPage();
    await paymentPage.validatePage();
    await paymentPage.selectPayOnArrival();
    await paymentPage.selectNewBusinessAccountCard();
    await paymentPage.clickContinueToPaymentDetails();

    // Step 7: Switch to payment iframe, fill PIBA card details, confirm booking
    const paymentDetailsPage = new PaymentDetailsPage();
    paymentDetailsPage.switchToIframe();
    await paymentDetailsPage.fillCardDetails(Cards.DEFAULT_PIBA_CARD);
    
    // Wait for card validation to complete before clicking confirm
    await page.waitForTimeout(2000);
    await paymentDetailsPage.clickConfirmBooking();

    // Step 8: Handle 3D Secure challenge (if displayed)
    const threeDSecurePage = new ThreeDSecurePage();
    await threeDSecurePage.confirmPayment();

    // Wait for navigation to confirmation page
    await page.waitForURL('**/confirmation**', { timeout: 60000 });

    // Step 9: Assert booking confirmation page shows total cost and booking reference
    const confirmBookingPage = new ConfirmBookingPage();
    await confirmBookingPage.validatePage();

    // Verify total cost is visible and matches a currency pattern (e.g., £123.45)
    await expect(confirmBookingPage.totalCostAmount).toBeVisible();
    const totalCostText = await confirmBookingPage.totalCostAmount.textContent();
    expect(totalCostText).toMatch(/[£€$]\s?\d+(\.\d{2})?/);

    // Verify booking reference is visible and non-empty
    await expect(confirmBookingPage.bookingReference).toBeVisible();
    const bookingRefText = await confirmBookingPage.bookingReference.textContent();
    expect(bookingRefText?.trim().length).toBeGreaterThan(0);
  });
});
