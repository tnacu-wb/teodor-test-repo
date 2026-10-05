import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";

/** Upcoming bookings section on IB. */
export class UpcomingBookingsSectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly upcomingBookingsContainer: Locator = this.page.getByTestId( "UpcomingBookings-Container", );
  readonly upcomingBookingsLabel: Locator = this.page.getByTestId( "UpcomingBookings-Heading", );
  readonly viewAllBookingsLink: Locator = this.page.getByTestId( "UpcomingBookings-ViewAllBookings", );
  readonly hotelImg: Locator = this.page.getByTestId( "UpcomingBookings-Booking-image", );
  readonly hotelTitleLabel: Locator = this.page.getByTestId( "UpcomingBookings-BookingCard-HotelName", );
  readonly arrivingLabel: Locator = this.page.getByTestId( "UpcomingBookings-BookingCard-ArrivingLabel", );
  readonly arrivingDateLabel: Locator = this.page.getByTestId( "UpcomingBookings-BookingCard-ArrivingDate", );
  readonly arrivingCheckInLabel: Locator = this.page.getByTestId( "UpcomingBookings-BookingCard-ArrivingCheckIn", );
  readonly leavingLabel: Locator = this.page.getByTestId( "UpcomingBookings-BookingCard-LeavingLabel", );
  readonly leavingDateLabel: Locator = this.page.getByTestId( "UpcomingBookings-BookingCard-LeavingDate", );
  readonly leavingCheckOutLabel: Locator = this.page.getByTestId( "UpcomingBookings-BookingCard-LeavingCheckOut", );
  readonly viewBookingDetailsLink: Locator = this.page.getByTestId( "UpcomingBookings-BookingCard-ViewDetails", );
  readonly myStaysLabel: Locator = this.page.getByTestId( "UpcomingBookings-StaysLabel", );
  readonly myStaysIcon: Locator = this.page.getByTestId( "UpcomingBookings-Stays-icon", );
  readonly myStaysNumberLabel: Locator = this.page.getByTestId( "UpcomingBookings-StaysCounter", );
  readonly bookingsLabel: Locator = this.page.getByTestId( "UpcomingBookings-BookingsLabel", );
  readonly bookingsIcon: Locator = this.page.getByTestId( "UpcomingBookings-Bookings-icon", );
  readonly bookingsNumberLabel: Locator = this.page.getByTestId( "UpcomingBookings-BookingsCounter", );
  readonly searchIcon: Locator = this.page.getByTestId( "UpcomingBookings-Search-icon", );
  readonly noBookingTitleLabel: Locator = this.page.getByTestId( "UpcomingBookings-NoBookingTitle", );
  readonly noBookingSubtitleLabel: Locator = this.page.getByTestId( "UpcomingBookings-NoBookingSubTitle", );
  // ######## UI actions/navigation ########
  /** Click View All Bookings. */
  async clickViewAllBookings(): Promise<void> {
    console.log("Click View All Bookings");
    const bookingsUrl = /\/business-booker\/account\/dashboard\.html/;
    await Promise.all([
      this.page.waitForURL(bookingsUrl, { timeout: browser.options.navigationTimeout }),
      this.viewAllBookingsLink.click(),
    ]);
  }
  // ######## UI validations ########
  /** Validate My stays. */
  async validateMyStays({
    noOfBookings,
  }: {
    noOfBookings: number;
  }): Promise<void> {
    console.log(`Validate My stays for noOfBookings=${noOfBookings}`);
    await expect(this.myStaysLabel, "My stays label").toHaveText( await IbStrings.MY_STAYS.name, );
    await expect(this.myStaysIcon, "My stays icon").toBeVisible();
    await expect(this.myStaysNumberLabel, "My stays number").toHaveText( String(noOfBookings), );
  }
  /** Validate Bookings. */
  async validateBookings({
    shouldUserSeeBookings,
    noOfBookings = null,
  }: {
    shouldUserSeeBookings: boolean;
    noOfBookings?: number | null;
  }): Promise<void> {
    console.log("Validate Bookings");
    if (!shouldUserSeeBookings) {
      await expect(this.bookingsLabel, "Bookings label").not.toBeVisible();
      return;
    }
    await expect(this.bookingsLabel, "Bookings label").toHaveText( await IbStrings.BOOKINGS_IB.name, );
    await expect(this.bookingsIcon, "Bookings icon").toBeVisible();
    await expect(this.bookingsNumberLabel, "Bookings number").toHaveText( String(noOfBookings), );
  }
  /** Validate upcoming booking or no-bookings state. */
  async validateUpcomingBooking({
    isTravelManagerOrBooker,
    upcomingBooking,
    hasStays,
  }: {
    isTravelManagerOrBooker: boolean;
    upcomingBooking?: { hotelName: string; stays: number; bookings: number };
    hasStays: boolean;
  }): Promise<void> {
    console.log("Validate Upcoming booking");
    await expect( this.upcomingBookingsContainer, "Upcoming bookings container", ).toBeVisible();
    await expect( this.upcomingBookingsLabel, "Upcoming bookings label", ).toHaveText(await IbStrings.UPCOMING_BOOKINGS.name);
    if (hasStays && upcomingBooking)
      await expect(this.hotelTitleLabel, "Hotel title").toHaveText( upcomingBooking.hotelName, );
    else {
      await expect(this.searchIcon, "Search icon").toBeVisible();
      await expect(this.noBookingTitleLabel, "No booking title").toHaveText( await IbStrings.NO_TRIPS_BOOKED.name, );
    }
    await this.validateMyStays({ noOfBookings: upcomingBooking?.stays ?? 0 });
    await this.validateBookings({
      shouldUserSeeBookings: isTravelManagerOrBooker,
      noOfBookings: upcomingBooking?.bookings ?? 0,
    });
  }
}
