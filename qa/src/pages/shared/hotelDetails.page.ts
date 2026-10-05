import { type Page, type Locator, expect } from '@playwright/test';
import { SearchConsoleComponent } from '../../components/pi/searchConsole/searchConsole.component';
import { BasePage } from './base.page';
import { ApiDictionary } from '../../api/aem/apiDictionary';
import { ApiSlugsCalls } from '../../api/graphql/apiSlugsCalls';
import { getCurrentLocale, Locales } from '../../test-data/locales';
import { Strings } from '../../test-data/strings';
import { GalleryModalComponent } from '../../components/pi/hotelDetails/galleryModal.component';
import { GallerySectionComponent } from '../../components/pi/hotelDetails/gallerySection.component';
import { HotelFacilitiesSectionComponent } from '../../components/pi/hotelDetails/hotelFacilitiesSection.component';
import { HotelParkingSectionComponent } from '../../components/pi/hotelDetails/hotelParkingSection.component';
import { LocationSectionComponent } from '../../components/pi/hotelDetails/locationSection.component';
import { OurRatesExplainedModalComponent } from '../../components/pi/hotelDetails/ourRatesExplainedModal.component';
import { PiHotelInformationSectionComponent } from '../../components/pi/hotelDetails/piHotelInformationSection.component';
import { OurRoomsComponent } from '../../components/pi/hotelDetails/ourRoomsComponent.component';
import { RestaurantSectionComponent } from '../../components/pi/hotelDetails/restaurantSection.component';
import { ReservationSummarySectionComponent } from '../../components/pi/hotelDetails/reservationSummarySection.component';
import { ChooseYourRateComponent } from '../../components/pi/hotelDetails/chooseYourRateComponent.component';

export type { HdpSearchOptions } from '../../components/pi/searchConsole/searchConsole.component';
export type { PricePerNight } from '../../components/pi/hotelDetails/reservationSummarySection.component';

/**
 * Hotel Details Page (HDP) - displays room options, rates, and booking summary.
 * Handles rate selection, Book Now action, the optional Choose Your Bathroom interstitial,
 * and the search console (location, dates, rooms) which may be pre-filled when navigating
 * directly to an HDP by slug.
 *
 * Composes shared components:
 * - CookieConsentComponent (cookie banner handling)
 * - NotificationPopupComponent (notification permission popup)
 * - SearchConsoleComponent (location, dates, rooms, submit)
 */
export class HotelDetailsPage extends BasePage {
  // ######## UI elements/properties ########

  readonly searchConsole: SearchConsoleComponent = new SearchConsoleComponent();

  // UI components (opera/hotelDetails)
  readonly hotelInformationSection: PiHotelInformationSectionComponent = new PiHotelInformationSectionComponent();
  readonly gallerySection: GallerySectionComponent = new GallerySectionComponent();
  readonly galleryModal: GalleryModalComponent = new GalleryModalComponent();
  readonly hotelFacilitiesSection: HotelFacilitiesSectionComponent = new HotelFacilitiesSectionComponent();
  readonly hotelParkingSection: HotelParkingSectionComponent = new HotelParkingSectionComponent();
  readonly locationSection: LocationSectionComponent = new LocationSectionComponent();
  readonly ourRatesExplainedModal: OurRatesExplainedModalComponent = new OurRatesExplainedModalComponent();
  readonly ourRoomsComponent: OurRoomsComponent = new OurRoomsComponent();
  readonly restaurantSection: RestaurantSectionComponent = new RestaurantSectionComponent();
  readonly bookNowSummarySection: ReservationSummarySectionComponent = new ReservationSummarySectionComponent();
  /** @deprecated Use bookNowSummarySection, matching the reference page API. */
  readonly reservationSummarySection: ReservationSummarySectionComponent = this.bookNowSummarySection;
  readonly chooseYourRateSection: ChooseYourRateComponent = new ChooseYourRateComponent();

  readonly alertLabel: Locator = this.page.locator('div[data-testid="Alert"] div[data-testid="AlertDescription"]');
  readonly tripAdvisorRatingSection: Locator = this.page.locator('div[data-testid="tripadvisor-top-rating-section-hdp"]');
  readonly tripAdvisorRatingSectionImage: Locator = this.tripAdvisorRatingSection.locator('img');
  readonly tripAdvisorSeeReviewsButton: Locator = this.page.locator('button[data-testid="Button-See_reviews"], button[data-testid="Button-Bewertungen_anzeigen"]');
  readonly tripAdvisorReviewSection: Locator = this.page.locator('section[data-testid="tripadvisor-bottom-review-section-hdp-Section"]');
  readonly tripAdvisorReviewSectionTitles: Locator = this.page.locator('div[data-testid="tripadvisor-user-review-section"] div span').locator('xpath=../../div[4]');
  readonly tripAdvisorAwardButtons: Locator = this.page.locator('img[data-testid="tripadvisor-top-rating-award-button"]');
  readonly tripAdvisorAwardIcons: Locator = this.page.locator('img[data-testid="tripadvisor-modal-award-icon"]');
  readonly tripAdvisorAwardModalCloseButton: Locator = this.page.locator('header[data-testid="ModalHeader"] button[data-testid="ModalCloseButton"]');
  readonly breadcrumbElements: Locator = this.page.locator('div[data-testid="breadcrumbs-hdp"] nav ol li > *');
  readonly announcementNotificationSection: Locator = this.page.locator('div[data-testid="announcement-notification-info-Alert"]');
  readonly announcementNotificationText: Locator = this.page.locator('div[data-testid="announcement-notification-info-AlertDescription"]');

  // ######## UI actions/navigation ########

  /**
   * Navigate directly to a Hotel Details Page using its slug URL.
   * Sets consent cookies before navigation to prevent the cookie banner.
   *
   * @param slug - The hotel slug path, e.g. '/hotels/england/west-sussex/crawley/london-gatwick-airport-south-london-road.html'
   * @param search - Optional search values used to avoid bootstrapping the page with stale default dates.
   */
  async open(slug: string, search?: { arrivalDate?: Date; nights?: number; rooms?: number; adults?: number; children?: number }): Promise<void> {
    console.log(`Opening hotel details page: ${slug}`);
    const arrivalDate = search?.arrivalDate ?? new Date();
    const nights = search?.nights ?? 2;
    const rooms = search?.rooms ?? 1;
    const adults = search?.adults ?? 1;
    const children = search?.children ?? 0;
    const defaultSearchParams = `ARRdd=${String(arrivalDate.getDate()).padStart(2, '0')}&ARRmm=${arrivalDate.getMonth() + 1}&ARRyyyy=${arrivalDate.getFullYear()}&NIGHTS=${nights}&ROOMS=${rooms}&ADULT1=${adults}&CHILD1=${children}&COT1=0&INTTYP1=DB`;
    const separator = slug.includes('?') ? '&' : '?';
    await this.openLocalizedPath(`${slug}${separator}${defaultSearchParams}`);
  }

  /** Open a hotel details page using its slug and default search parameters. */
  async openHotelDetailsBySlug(slug: string): Promise<void> {
    console.log(`Opening hotel by slug: ${slug}`);
    await this.open(slug);
  }

  /** Click the regional breadcrumb link. */
  async clickBreadcrumbRegion(): Promise<void> {
    console.log('Click regional breadcrumb link');
    const breadcrumbCount = await this.breadcrumbElements.count();
    if (breadcrumbCount < 3) {
      throw new Error(`Expected at least 3 breadcrumb elements but found ${breadcrumbCount}.`);
    }
    await this.breadcrumbElements.nth(breadcrumbCount - 3).click();
  }

  /** Open the TripAdvisor award modal. */
  async clickTripAdvisorAward(): Promise<void> {
    console.log('Click TripAdvisor award');
    await this.tripAdvisorAwardButtons.first().scrollIntoViewIfNeeded();
    await this.tripAdvisorAwardButtons.first().click();
  }

  /** Close the TripAdvisor award modal. */
  async closeTripAdvisorAward(): Promise<void> {
    console.log('Close TripAdvisor award modal');
    await this.tripAdvisorAwardModalCloseButton.click();
  }

  /** Open the TripAdvisor reviews section. */
  async clickTripAdvisorSeeReviewsButton(): Promise<void> {
    console.log('Click TripAdvisor See reviews button');
    await this.tripAdvisorSeeReviewsButton.scrollIntoViewIfNeeded();
    await this.tripAdvisorSeeReviewsButton.click();
  }

  /**
   * Wait for the page to navigate to the ancillaries page after Book Now
   * and handling optional modals/interstitials.
   * Validates that the URL contains '/ancillaries'.
   */
  async waitForAncillariesPage(): Promise<void> {
    console.log('Waiting for navigation to ancillaries page');
    await this.page.waitForURL(/\/ancillaries/, { timeout: 30000 });
  }

  // ######## UI validations ########

  /** Validate that the page URL contains the expected hotel slug after search/navigation. */
  async validateNavigatedToHotel(slug: string): Promise<void> {
    console.log(`Validating navigated to hotel with slug: ${slug}`);
    await expect(this.page, `Page URL should contain hotel slug: ${slug}`).toHaveURL(new RegExp(slug.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')));
  }

  /** Validate the displayed hotel title. */
  async validateHotelTitle(expectedTitle?: string): Promise<void> {
    console.log(`Validating hotel title: ${expectedTitle}`);
    await this.hotelInformationSection.validateHotelTitle({ apiHotelTitle: expectedTitle ?? '' });
  }

  /** Validate the visibility and content of the minimum-length-of-stay alert. */
  async validateMlosAlertLabel({ isDisplayed }: { isDisplayed: boolean }): Promise<void> {
    console.log(`Validate MLOS alert displayed=${isDisplayed}`);
    if (!isDisplayed) {
      await expect(this.alertLabel, 'MLOS alert label should not be visible').not.toBeVisible();
      return;
    }

    await expect(this.alertLabel, 'MLOS alert label should be visible').toBeVisible();
    await expect(this.alertLabel, 'MLOS alert label text should match').toContainText(
      await Strings.THIS_HOTEL_HAS_A_MINIMUM_LENGTH_OF_STAY_RESTRICTION.name
    );
  }

  /** Validate the TripAdvisor rating image against the current hotel API response. */
  async validateTripAdvisorRatingFormat({ slug }: { slug: string }): Promise<void> {
    console.log(`Validate TripAdvisor rating for ${slug}`);
    const tripAdvisorData = await ApiSlugsCalls.graphqlGetHotelTripAdvisorReviews({ slug });
    await expect(this.tripAdvisorRatingSectionImage, 'TripAdvisor rating image should be visible').toBeVisible();
    await expect(this.tripAdvisorRatingSectionImage, 'TripAdvisor rating image should contain the API rating').toHaveAttribute(
      'src',
      new RegExp(String(tripAdvisorData.rating ?? ''))
    );
  }

  /** Validate the localized TripAdvisor review count against the current hotel API response. */
  async validateTripAdvisorNumberOfReviewsFormat({ slug }: { slug: string }): Promise<void> {
    console.log(`Validate TripAdvisor review count for ${slug}`);
    const tripAdvisorData = await ApiSlugsCalls.graphqlGetHotelTripAdvisorReviews({ slug });
    const reviewCount = tripAdvisorData.numberOfReviews;
    if (reviewCount === undefined) {
      throw new Error(`TripAdvisor API returned no review count for slug "${slug}".`);
    }
    const locale = getCurrentLocale().name === Locales.DE_DE.name ? 'de-DE' : 'en-GB';
    const reviewLabel = await Strings.REVIEWS.name;
    const formattedCount = new Intl.NumberFormat(locale).format(reviewCount);
    const expectedFormattedText = `(${formattedCount} ${reviewLabel})`;
    const expectedRawText = `(${reviewCount} ${reviewLabel})`;

    await expect(this.tripAdvisorRatingSection, 'TripAdvisor rating section should contain the API review count').toContainText(
      new RegExp(`\\(${formattedCount.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}|${reviewCount}\\)\\s+${reviewLabel}`, 'i')
    ).catch(async () => {
      const actualText = await this.tripAdvisorRatingSection.innerText();
      expect(
        actualText.includes(expectedFormattedText) || actualText.includes(expectedRawText),
        `TripAdvisor reviews count should match ${expectedFormattedText} or ${expectedRawText}`
      ).toBe(true);
    });
  }

  /** Validate the announcement visibility and its AEM text for the supplied hotel. */
  async validateAnnouncementNotificationSection({
    isDisplayed = true,
    hotelId,
  }: {
    isDisplayed?: boolean;
    hotelId: string;
  }): Promise<void> {
    console.log(`Validate announcement notification displayed=${isDisplayed}`);
    if (!isDisplayed) {
      await expect(this.announcementNotificationSection, 'Announcement section should not be visible').not.toBeVisible();
      return;
    }

    const hotelDictionary = await ApiDictionary.fetchHotelDirectoryDictionary({ hotelId });
    const announcement = hotelDictionary.announcement as { text?: string } | undefined;
    await expect(this.announcementNotificationSection, 'Announcement section should be visible').toBeVisible();
    await expect(this.announcementNotificationText, 'Announcement description should match AEM').toContainText(announcement?.text ?? '');
  }

  /** Validate required room occupancy query parameters in the current HDP URL. */
  async validateSearchParamsInCurrentUrl({
    roomsList,
  }: {
    roomsList: Array<{ adultsNumber: number; childrenNumber: number; roomType: { id: string } }>;
  }): Promise<void> {
    const currentUrl = new URL(this.page.url());
    expect(currentUrl.searchParams.get('ROOMS'), 'ROOMS query parameter should match selected rooms').toBe(`${roomsList.length}`);
    for (const [index, room] of roomsList.entries()) {
      const roomNumber = index + 1;
      expect(currentUrl.searchParams.get(`ADULT${roomNumber}`), `ADULT${roomNumber} query parameter should match`).toBe(`${room.adultsNumber}`);
      expect(currentUrl.searchParams.get(`CHILD${roomNumber}`), `CHILD${roomNumber} query parameter should match`).toBe(`${room.childrenNumber}`);
      expect(currentUrl.searchParams.get(`INTTYP${roomNumber}`), `INTTYP${roomNumber} query parameter should match`).toBe(room.roomType.id);
    }
  }

  /**
   * Validate that the Hotel Details Page has fully loaded.
   * Waits for the page loaded indicator to be visible within 30s.
   * @throws Error with page name and selector if indicator not found
   */
  async validatePage(): Promise<void> {
    console.log('Validating hotel details page loaded');
    try {
      await this.hotelInformationSection.hotelNameLabel.waitFor({ state: 'visible', timeout: 30000 });
    } catch {
      throw new Error(
        `HotelDetailsPage did not load within 30s. Selector not found: h1[data-testid="hdp_hotelTitle"]`
      );
    }
  }

}
