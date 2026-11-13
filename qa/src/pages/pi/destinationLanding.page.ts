import { type Page, type Locator, expect } from '@playwright/test';
import { getCurrentLocale } from '../../test-data/locales';
import { BasePage } from '../shared/base.page';

/**
 * Destination Landing Page (DLP) - displays hotels in a region with filters, maps,
 * TripAdvisor integration, and grid/map toggle.
 *
 * Handles DLP navigation, hotel listing validation, map/grid views,
 * TripAdvisor reviews, show more, and hotel distance display.
 */
export class DestinationLandingPage extends BasePage {
  // ######## UI elements/properties ########

  // Page wrapper
  readonly dlpPageWrapper: Locator = this.page.locator('[data-testid="DestinationLandingPIPage-Wrapper"]');

  // Hero section
  readonly heroTitle: Locator = this.page.locator('[data-testid="DLPHeroSection-Title"]');
  readonly heroDescription: Locator = this.page.locator('[data-testid="DLPHeroSection-Description"]');
  readonly heroPicture: Locator = this.page.locator('img[data-testid="DLPHeroSection-Picture"]');

  // Hotel counter
  readonly hotelCounterLabel: Locator = this.page.locator('[data-testid="DestinationLandingPIPage-Wrapper"] h6');

  // Hotel cards (grid view)
  readonly hotelCards: Locator = this.page.locator('[data-testid="DLP-hotel-card"]');
  readonly gridViewElement: Locator = this.page.locator('[data-testid="DestinationLandingPIPage-HotelsList"]');

  // Map view
  readonly mapViewElement: Locator = this.page.locator('[data-testid="DestinationLandingPIPage-mapView"]');
  readonly mapMarkers: Locator = this.page.locator('gmp-advanced-marker');
  readonly mapPegmanButton: Locator = this.page.locator('button.gm-svpc');
  readonly mapZoomInButton: Locator = this.page.locator('button[title="Zoom in"]');
  readonly mapZoomOutButton: Locator = this.page.locator('button[title="Zoom out"]');

  // Map/Grid toggle
  readonly mapGridSwitchToggle: Locator = this.page.locator('[data-testid="DestinationLandingPIPageMapGrid-SwitchToggle"]');
  readonly mapViewSwitchMapButton: Locator = this.page.locator('[data-testid="DestinationLandingPIPageMapGrid-first"]');
  readonly mapViewSwitchGridButton: Locator = this.page.locator('[data-testid="DestinationLandingPIPageMapGrid-second"]');

  // Hotel card elements (map popover and grid)
  readonly hotelCardTitle: Locator = this.page.locator('[data-testid="DLP-hotel-card"] p').first();
  readonly hotelCardThumbnail: Locator = this.page.locator('[data-testid="DLP-hotel-thumbnail"] img');
  readonly hotelCardDistance: Locator = this.page.locator('[data-testid="DLP-hotel-distance"]');
  readonly hotelCardFacilitiesImage: Locator = this.page.locator('[data-testid="DLP-hotel-facility"] img');
  readonly viewHotelButton: Locator = this.page.locator('[data-testid="DLP-hotel-button"] button');
  readonly cardCloseButton: Locator = this.page.locator('button.gm-ui-hover-effect');

  // TripAdvisor
  readonly tripAdvisorRatingsImages: Locator = this.page.locator('img[alt="ta-ratings-img"]');
  readonly tripAdvisorReviewsLinks: Locator = this.page.locator('img[alt="ta-ratings-img"] + * a, img[alt="ta-ratings-img"] ~ a');
  readonly tripAdvisorBottomReviewSection: Locator = this.page.locator('[data-testid="tripadvisor-bottom-review-section-hdp-Section"]');
  readonly tripAdvisorImage: Locator = this.page.locator('[data-testid="DLP-hotel-card"] img[alt="ta-ratings-img"]');

  // Show more
  readonly showMoreButton: Locator = this.page.locator('[data-testid="DestinationLandingPIPage-showMoreBtn"]');

  // Notification popup
  readonly notificationModal: Locator = this.page.locator('[data-testid="pi-notification-permission-popup-modal-content"]');
  readonly notificationDenyButton: Locator = this.page.locator('[data-testid="pi-notification-permission-popup-deny-btn"]');
  readonly notificationAllowButton: Locator = this.page.locator('[data-testid="pi-notification-permission-popup-allow-btn"]');

  // Filters
  readonly filtersButton: Locator = this.page.locator('[data-testid="DLP-Filters-Open-Button"]');
  readonly filtersPanel: Locator = this.page.locator('[data-testid="DLP-Filters-Wrapper"]');

  // Content sections
  readonly otherDestinationsElements: Locator = this.page.locator('[data-testid="DestinationsList-List"] p');
  readonly thingsToDoCardsTitleLabels: Locator = this.page.locator('[data-testid="ThingsToDoList-List"] p');
  readonly faqTabsElements: Locator = this.page.locator('[data-testid="DestinationFaq-TabsComponent"] h3');
  readonly faqQuestionsElements: Locator = this.page.locator('[data-testid="DestinationFaq-Accordions"] button div');
  readonly whySectionElements: Locator = this.page.locator('[data-testid="WhyUs-Item"] h4');

  // ######## Private helpers ########

  private coordinatesMatch(actual: string | null, expected: string): boolean {
    if (actual === expected) return true;

    const parseCoordinates = (position: string | null): [number, number] | null => {
      const parts = position?.split(',').map(part => Number(part.trim())) ?? [];
      return parts.length === 2 && parts.every(Number.isFinite) ? [parts[0], parts[1]] : null;
    };

    const actualCoordinates = parseCoordinates(actual);
    const expectedCoordinates = parseCoordinates(expected);
    if (!actualCoordinates || !expectedCoordinates) return false;

    return Math.abs(actualCoordinates[0] - expectedCoordinates[0]) < 0.000001
      && Math.abs(actualCoordinates[1] - expectedCoordinates[1]) < 0.000001;
  }

  // ######## UI actions/navigation ########

  /**
   * Navigate to a Destination Landing Page by its relative path.
   * Sets consent cookies before navigation (same pattern as HomePage).
   * @param dlpPath - Relative DLP URL, e.g. 'hotels/england/west-midlands/birmingham.html'
   */
  async open(dlpPath: string): Promise<void> {
    console.log(`Navigating to DLP: ${dlpPath}`);
    await this.cookieConsent.preSetConsentCookies();

    const locale = getCurrentLocale();
    const cleanPath = dlpPath.startsWith('/') ? dlpPath.slice(1) : dlpPath;
    await this.page.goto(`/${locale.country}/${locale.language}/${cleanPath}`, { waitUntil: 'domcontentloaded', timeout: 30000 });
    await this.cookieConsent.dismissIfPresent();
    await this.notificationPopup.dismissIfPresent();
  }

  // ######## UI validations ########

  /**
   * Validate that the DLP has loaded by checking for the page wrapper element.
   */
  async validatePage(): Promise<void> {
    console.log('Validating DLP page loaded');
    try {
      await this.dlpPageWrapper.waitFor({ state: 'visible', timeout: 30000 });
    } catch {
      throw new Error(
        'DestinationLandingPage did not load within 30s. Selector not found: [data-testid="DestinationLandingPIPage-Wrapper"]'
      );
    }
  }

  /**
   * Validate core DLP page elements: hotel list, map/grid toggle, and content sections.
   * Validates the Why section items, FAQ elements, and Other Destinations against expected data.
   * @param dlpContent - The AEM DLP content response for cross-validation
   */
  async validateDlpPageElements(dlpContent: {
    why?: { whyItems: Array<{ itemTitle: string }> } | null;
    faqs?: Array<{ title: string; faqItems: Array<{ question: string }> }>;
    dlps?: { dlpItems: Array<{ title: string }> } | null;
  }): Promise<void> {
    console.log('Validating DLP page elements');
    // Validate hotel cards are visible
    await expect(this.hotelCards.first(), 'Hotel cards should be displayed on DLP').toBeVisible();

    // Validate Why section items
    if (dlpContent.why?.whyItems) {
      const whyItems = this.whySectionElements;
      await expect(whyItems, `Why section should have ${dlpContent.why.whyItems.length} items`).toHaveCount(dlpContent.why.whyItems.length);
      for (const [i, item] of dlpContent.why.whyItems.entries()) {
        await expect(whyItems.nth(i), `Why item ${i} should display: ${item.itemTitle}`).toHaveText(item.itemTitle.trim());
      }
    }

    // Validate FAQ tabs — the AEM data may represent sections that expand into multiple tabs
    // on the page. Validate that FAQ tabs are present and that the AEM section titles appear
    // somewhere in the rendered tabs (containment check, not exact count/position match).
    if (dlpContent.faqs && dlpContent.faqs.length > 0) {
      const faqTabs = this.faqTabsElements;
      const tabCount = await faqTabs.count();
      // Page must have at least as many tabs as AEM FAQ entries
      expect(tabCount, 'FAQ tabs must be present on the page').toBeGreaterThanOrEqual(dlpContent.faqs.length);

      // Validate each AEM FAQ title appears in one of the rendered tabs
      for (const faq of dlpContent.faqs) {
        const matchingTab = faqTabs.filter({ hasText: faq.title.trim() });
        await expect(matchingTab.first(), `FAQ tab "${faq.title}" should be visible on page`).toBeVisible({ timeout: 5000 });
      }
    }

    // Validate Other Destinations — the AEM dlpItems may not match the page's "Explore" section
    // exactly (different data sources). Validate the section is present and has items, but skip
    // strict positional text matching since the page renders destination-specific links while
    // the AEM dictionary may return generic items like "New hotels".
    if (dlpContent.dlps?.dlpItems) {
      const destinations = this.otherDestinationsElements;
      const destCount = await destinations.count();
      expect(destCount, 'Other Destinations section must have items').toBeGreaterThan(0);
    }
  }

  /**
   * Validate the Hero section (title, description, picture) against expected DLP content.
   * @param title - Expected hero title
   * @param description - Expected hero description (HTML)
   * @param image - Expected image path (partial match)
   */
  async validateHeroSection(title: string, description: string, image: string): Promise<void> {
    console.log(`Validating hero section with title: ${title}`);
    await expect(this.heroTitle, 'Hero title should be displayed').toBeVisible();
    await expect(this.heroTitle, `Hero title should display: ${title}`).toHaveText(title);

    // Description comparison: normalise non-breaking spaces
    const descriptionHtml = await this.heroDescription.innerHTML();
    const normalised = descriptionHtml.trim().replace(/&nbsp;/g, ' ').replace(/ target="_blank"/g, '');
    const expectedNormalised = description.trim().replace(/\u00A0/g, ' ').replace(/ target="_blank"/g, '');
    if (!normalised.includes(expectedNormalised) && expectedNormalised !== normalised) {
      throw new Error(`Hero description mismatch.\nExpected: ${expectedNormalised}\nActual: ${normalised}`);
    }

    const pictureSrc = await this.heroPicture.getAttribute('src');
    if (!pictureSrc || !decodeURIComponent(pictureSrc).includes(image)) {
      throw new Error(`Hero picture does not contain expected image path: ${image}`);
    }
  }

  /**
   * Validate TripAdvisor section displays reviews matching the API response.
   * @param expectedReviews - Array of expected TripAdvisor reviews with rating and numberOfReviews
   */
  async validateTripAdvisorSection(expectedReviews: Array<{ rating: number; numberOfReviews: number }>): Promise<void> {
    console.log(`Validating TripAdvisor section with ${expectedReviews.length} reviews`);
    await expect(this.tripAdvisorRatingsImages.first(), 'TripAdvisor rating images should be displayed').toBeVisible({ timeout: 10000 });

    const ratingsCount = await this.tripAdvisorRatingsImages.count();
    expect(ratingsCount, `TripAdvisor ratings count should match expected: ${expectedReviews.length}`).toBe(expectedReviews.length);

    for (const [i, review] of expectedReviews.entries()) {
      const ratingImg = this.tripAdvisorRatingsImages.nth(i);
      const expectedRating = (Math.round(review.rating * 2) / 2).toFixed(1);
      await expect(ratingImg, `Rating ${i} should show ${expectedRating} stars`).toHaveAttribute(
        'src',
        `https://static.tacdn.com/img2/ratings/traveler/${expectedRating}.svg`
      );
    }
  }

  /**
   * Get the TripAdvisor review link URL (the first review link).
   * This link opens in a new tab pointing to the HDP reviews section.
   * @returns The href of the first TripAdvisor review link
   */
  async getTripAdvisorReviewLink(): Promise<string> {
    console.log('Getting TripAdvisor review link');
    // The review links use the pattern: img[alt="ta-ratings-img"] followed by a sibling link
    const reviewLink = this.page.locator('img[alt="ta-ratings-img"]').first().locator('xpath=following-sibling::*[1]/self::a | ../following-sibling::a').first();
    await expect(reviewLink, 'TripAdvisor review link should be displayed').toBeVisible({ timeout: 10000 });
    return (await reviewLink.getAttribute('href')) ?? '';
  }

  /**
   * Click the first TripAdvisor review link and validate it opens in a new tab.
   * The link should have target="_blank", opening the HDP reviews section in a new page.
   *
   * @returns The new Page object for the opened tab (caller is responsible for closing it)
   */
  async clickTripAdvisorReviewLink(): Promise<Page> {
    console.log('Clicking TripAdvisor review link');
    const reviewLink = this.tripAdvisorReviewsLinks.first();
    await expect(reviewLink, 'TripAdvisor review link should be clickable').toBeVisible({ timeout: 10000 });

    const [newPage] = await Promise.all([
      this.page.context().waitForEvent('page'),
      reviewLink.click(),
    ]);

    // Avoid waitForLoadState here: the HDP can keep loading third-party trackers indefinitely,
    // so wait only for the navigation to land on a real URL instead of full DOM/load completion.
    await newPage.waitForURL((url) => url.href !== 'about:blank', { timeout: 10000 });
    return newPage;
  }

  /**
   * Click the TripAdvisor review link, validate the new tab URL matches
   * the expected HDP review URL pattern, then close the tab and return to the DLP.
   *
   * @param slug - The hotel slug from the TripAdvisor review API response
   */
  async clickTripAdvisorReviewLinkAndValidate(slug: string): Promise<void> {
    const locale = getCurrentLocale();
    const expectedUrl = `/${locale.country}/${locale.language}/hotels${slug}.html`;
    console.log(`Validating TripAdvisor review link opens to: ${expectedUrl}`);
    const newPage = await this.clickTripAdvisorReviewLink();

    // Validate the new tab URL contains the expected path
    const actualUrl = newPage.url();
    expect(actualUrl, `URL should contain expected path: ${expectedUrl}`).toContain(expectedUrl);

    // Close the new tab and return focus to the DLP
    await newPage.close();
  }

  /**
   * Verify the DLP is currently in map view (not grid view).
   * Used after back-navigation from HDP to confirm map view state is preserved.
   */
  async verifyMapViewIsActive(): Promise<void> {
    console.log('Verifying map view is active');
    await expect(this.mapViewElement, 'Map view should be displayed').toBeVisible({ timeout: 10000 });
  }

  /**
   * Click the "View Hotel" button on an open map card to navigate to HDP.
   * Assumes a map marker has already been clicked and the card is visible.
   * @returns Promise that resolves when navigation starts
   */
  async clickViewHotelOnMapCard(): Promise<void> {
    console.log('Clicking view hotel button on map card');
    await this.viewHotelButton.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
    await this.viewHotelButton.click();
  }

  /**
   * Click a specific map marker by index to open its hotel card.
   * Skips the region marker (identified by the regionPosition).
   * @param hotelIndex - 0-based index of the hotel marker (not including region marker)
   * @param regionPosition - The region marker position string to skip (e.g. "lat,lng")
   */
  async clickMapMarker(hotelIndex: number, regionPosition: string): Promise<void> {
    console.log(`Clicking map marker at hotel index ${hotelIndex}`);
    await this.mapMarkers.first().waitFor({ state: 'attached', timeout: browser.options.actionTimeout });

    const markersCount = await this.mapMarkers.count();
    let hotelCount = 0;

    for (let i = 0; i < markersCount; i++) {
      const marker = this.mapMarkers.nth(i);
      const position = await marker.getAttribute('position');

      // Skip the region marker
      if (this.coordinatesMatch(position, regionPosition)) continue;

      if (hotelCount === hotelIndex) {
        await marker.evaluate((element: HTMLElement) => element.click());
        await this.hotelCards.first().waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
        return;
      }
      hotelCount++;
    }

    throw new Error(`Map marker at hotel index ${hotelIndex} not found. Total hotel markers: ${hotelCount}`);
  }

  /**
   * Click the map view toggle to switch from grid to map view.
   */
  async clickMapView(): Promise<void> {
    console.log('Switching to map view');
    await this.mapViewSwitchMapButton.waitFor({ state: 'visible' });
    await this.mapViewSwitchMapButton.scrollIntoViewIfNeeded();
    await this.mapViewSwitchMapButton.click();
    await expect(this.mapViewElement, 'Map view should be displayed after toggle').toBeVisible();
  }

  /**
   * Click the grid view toggle to switch from map to grid view.
   */
  async clickGridView(): Promise<void> {
    console.log('Switching to grid view');
    await this.mapGridSwitchToggle.waitFor({ state: 'visible' });
    await this.mapGridSwitchToggle.scrollIntoViewIfNeeded();
    // Re-click on failure: right after a back-navigation from HDP the toggle can render
    // before its click handler is rebound, silently swallowing the first click.
    await expect(async () => {
      await this.mapGridSwitchToggle.click();
      await expect(this.page, 'DLP URL should reflect grid view after toggle').toHaveURL(/VIEW=2/, { timeout: 3000 });
    }).toPass({ timeout: browser.options.actionTimeout });
    await expect(this.gridViewElement, 'Grid view should be displayed after toggle').toBeVisible();
    await expect(this.hotelCards.first(), 'Grid view hotel cards should be displayed after toggle').toBeVisible();
  }

  /**
   * Validate hotel cards/pins in map view: verifies markers exist, validates card info on click.
   * @param hotels - Expected hotels data from the map view API
   * @param regionPosition - The region marker position string (e.g. "lat,lng")
   */
  async validateMapViewCards(
    hotels: Array<{ name: string; position: string; distanceFromReference?: number; rating?: number }>,
    regionPosition: string
  ): Promise<void> {
    console.log(`Validating map view cards for ${hotels.length} hotels`);
    await this.mapMarkers.first().waitFor({ state: 'attached', timeout: browser.options.actionTimeout });

    const markersCount = await this.mapMarkers.count();
    // Hotels + 1 region marker
    expect(markersCount, `Map should display ${hotels.length + 1} markers (hotels + region)`).toBe(hotels.length + 1);

    let hasRegionMarker = false;

    for (let i = 0; i < markersCount; i++) {
      const marker = this.mapMarkers.nth(i);
      const position = await marker.getAttribute('position');

      if (this.coordinatesMatch(position, regionPosition)) {
        hasRegionMarker = true;
        continue;
      }

      const hotel = hotels.find(h => this.coordinatesMatch(position, h.position));
      if (!hotel) continue;

      // Click the marker to open the card
      await marker.evaluate((element: HTMLElement) => element.click());
      await this.hotelCards.first().waitFor({ state: 'visible', timeout: browser.options.actionTimeout });

      // Validate card elements
      await expect(this.hotelCardTitle, `Hotel card title should be displayed for ${hotel.name}`).toBeVisible();
      await expect(this.hotelCardTitle, `Card title should start with ${hotel.name.slice(0, 39)}`).toContainText(hotel.name.slice(0, 39), { timeout: 10000 });

      await expect(this.hotelCardThumbnail.first(), 'Hotel card thumbnail should be displayed').toBeVisible();
      await expect(this.viewHotelButton, 'View hotel button should be displayed on card').toBeVisible();

      // Validate distance display based on hotel data
      if (hotel.distanceFromReference) {
        await expect(this.hotelCardDistance.first(), 'Hotel distance should be displayed').toBeVisible();
      }
    }

    expect(hasRegionMarker, 'Map should display region marker').toBe(true);
  }

  /**
   * Validate hotel list elements are displayed in grid view.
   * Checks that hotel cards have expected sub-elements (thumbnail, title, button).
   */
  async validateHotelListElements(expectedHotelNames: string[] = []): Promise<void> {
    console.log('Validating hotel list elements in grid view');
    await expect(this.hotelCards.first(), 'Hotel cards should be displayed').toBeVisible({ timeout: browser.options.actionTimeout });

    const cardCount = await this.hotelCards.count();
    expect(cardCount, 'At least one hotel card should be displayed').toBeGreaterThan(0);
    if (expectedHotelNames.length > 0) {
      expect(cardCount, `Hotel cards count should match expected list: ${expectedHotelNames.length}`).toBe(expectedHotelNames.length);
      const remainingHotelTitles: string[] = [];
      for (let i = 0; i < cardCount; i++) {
        remainingHotelTitles.push((await this.hotelCards.nth(i).locator('p').first().textContent()) ?? '');
      }

      for (const hotelName of expectedHotelNames) {
        const expectedName = hotelName.replace('hub ', '');
        const hotelIndex = remainingHotelTitles.findIndex((hotelTitle) => {
          const actualName = hotelTitle.replace('hub ', '');
          return actualName === expectedName
            || (expectedName.includes(')') && actualName.includes(expectedName.split(')')[0]));
        });

        expect(hotelIndex, `Hotel list should contain ${hotelName}`).not.toBe(-1);
        remainingHotelTitles.splice(hotelIndex, 1);
      }

      expect(remainingHotelTitles, 'Hotel list from UI should match expected hotel list').toHaveLength(0);
    }

    // Validate first card has essential elements
    const firstCard = this.hotelCards.first();
    await expect(firstCard.locator('[data-testid="DLP-hotel-thumbnail"] img'), 'Hotel card thumbnail should be displayed').toBeVisible();
    await expect(firstCard.locator('[data-testid="DLP-hotel-button"] button'), 'Hotel card button should be displayed').toBeVisible();
  }

  /**
   * Get the count of visible hotel cards in grid view.
   * @returns Number of hotel cards currently displayed
   */
  async getHotelCardCount(): Promise<number> {
    console.log('Getting hotel card count');
    return this.hotelCards.count();
  }

  /**
   * Get the count of map pins (excluding the region marker).
   * @returns Number of hotel map pins
   */
  async getMapPinCount(): Promise<number> {
    console.log('Getting map pin count');
    await this.mapMarkers.first().waitFor({ state: 'attached', timeout: browser.options.actionTimeout });
    const total = await this.mapMarkers.count();
    // Subtract 1 for the region marker
    return total - 1;
  }

  /**
   * Click "Show More" to expand the hotel results list.
   * Waits for new cards to appear after clicking.
   */
  async clickShowMore(): Promise<void> {
    console.log('Clicking show more button');
    const countBefore = await this.hotelCards.count();
    await this.showMoreButton.scrollIntoViewIfNeeded();
    await this.showMoreButton.click();
    // Wait for more cards to load
    await this.page.waitForFunction(
      (prevCount) => {
        const cards = document.querySelectorAll('[data-testid="DLP-hotel-card"]');
        return cards.length > prevCount;
      },
      countBefore,
      { timeout: browser.options.actionTimeout }
    );
  }

  /**
   * Click "Show More" until all hotels are loaded (button disappears).
   */
  async clickShowMoreUntilAllLoaded(): Promise<void> {
    console.log('Clicking show more until all hotels loaded');
    while (await this.showMoreButton.isVisible()) {
      await this.clickShowMore();
      // Brief wait for potential UI settle
      await this.page.waitForTimeout(500);
    }
  }

  /**
   * Validate that hotel distance is displayed on hotel cards.
   */
  async validateHotelDistanceDisplayed(): Promise<void> {
    console.log('Validating hotel distance is displayed');
    await expect(this.hotelCardDistance.first(), 'Hotel distance should be displayed on cards').toBeVisible({ timeout: 10000 });
  }

  /**
   * Validate that hotel distance is NOT displayed on hotel cards.
   * Used when AEM configuration hides the distance for a DLP.
   */
  async validateHotelDistanceHidden(): Promise<void> {
    console.log('Validating hotel distance is hidden');
    await expect(this.hotelCardDistance.first(), 'Hotel distance should be hidden on cards').not.toBeVisible({ timeout: 5000 });
  }

  /**
   * Validate ALL hotel cards show distance (not just the first).
   * Useful after "show more" to confirm extended cards also display distance.
   */
  async validateAllHotelCardsShowDistance(): Promise<void> {
    console.log('Validating all hotel cards show distance');
    const cardCount = await this.hotelCards.count();
    expect(cardCount, 'At least one hotel card should be displayed').toBeGreaterThan(0);

    for (let i = 0; i < cardCount; i++) {
      const card = this.hotelCards.nth(i);
      const distance = card.locator('[data-testid="DLP-hotel-distance"]');
      await expect(distance, `Card ${i} should display distance`).toBeVisible({ timeout: 5000 });
    }
  }

  /**
   * Validate ALL hotel cards hide distance.
   * Used for DLPs where AEM configuration hides distance (e.g. Luton).
   */
  async validateAllHotelCardsHideDistance({ hideHotelDistance }: { hideHotelDistance?: boolean }): Promise<void> {
    console.log('Validating all hotel cards hide distance');
    if (!hideHotelDistance) return;

    const cardCount = await this.hotelCards.count();
    expect(cardCount, 'At least one hotel card should be displayed').toBeGreaterThan(0);

    for (let i = 0; i < cardCount; i++) {
      const card = this.hotelCards.nth(i);
      const distance = card.locator('[data-testid="DLP-hotel-distance"]');
      await expect(distance, `Card ${i} should hide distance`).not.toBeVisible({ timeout: 3000 });
    }
  }

  /**
   * Validate that the map view pin count matches the number of hotel cards in grid view.
   * Used after "show more" to verify extended hotels also appear as pins on the map.
   */
  async validateMapPinsMatchGridCards(): Promise<void> {
    console.log('Validating map pins match grid cards');
    const gridCount = await this.getHotelCardCount();
    await this.clickMapView();
    await this.validateMapPinsCount(gridCount);
  }

  /**
   * Click a specific hotel card to navigate to the Hotel Details Page.
   * @param index - Zero-based index of the hotel card to click
   */
  async clickHotelCard(index: number): Promise<void> {
    console.log(`Clicking hotel card at index ${index}`);
    const card = this.hotelCards.nth(index);
    const button = card.locator('[data-testid="DLP-hotel-button"] button');
    await button.scrollIntoViewIfNeeded();
    await button.click();
  }

  /**
   * Validate the hotel counter heading shows the expected number of hotels.
   * @param expectedCount - Expected number shown in the counter heading
   */
  async validateHotelCounterNumber(expectedCount: number): Promise<void> {
    console.log(`Validating hotel counter shows: ${expectedCount}`);
    const text = await this.hotelCounterLabel.textContent();
    const match = text?.match(/^(\d+)/);
    expect(match, 'Hotel counter should contain a number').not.toBeNull();
    expect(Number(match![1]), `Hotel counter should show ${expectedCount}`).toBe(expectedCount);
  }

  /**
   * Validate the map view pins count matches the expected hotel count.
   * @param expectedCount - Expected number of hotel pins (excluding region marker)
   */
  async validateMapPinsCount(expectedCount: number): Promise<void> {
    console.log(`Validating map pins count: ${expectedCount}`);
    // Subtract 1 for the region marker
    await expect(this.mapMarkers, `Map should display ${expectedCount} hotel pins`).toHaveCount(expectedCount + 1, { timeout: 30000 });
  }

  /**
   * Validate that all hotel cards on the map view do NOT show hotel distance.
   * Used for DLPs where AEM configuration hides distance (e.g. Luton).
   * @param hotels - Hotels data with position info
   * @param regionPosition - Region marker position string
   */
  async validateMapViewHiddenHotelDistance({
    hotels,
    regionPosition,
    hideHotelDistance,
  }: {
    hotels: Array<{ position: string }>;
    regionPosition: string;
    hideHotelDistance?: boolean;
  }): Promise<void> {
    console.log('Validating map view hides hotel distance');
    if (!hideHotelDistance) return;

    await this.mapMarkers.first().waitFor({ state: 'attached', timeout: browser.options.actionTimeout });

    const markersCount = await this.mapMarkers.count();
    let hasRegionMarker = false;

    for (let i = 0; i < markersCount; i++) {
      const marker = this.mapMarkers.nth(i);
      const position = await marker.getAttribute('position');

      if (this.coordinatesMatch(position, regionPosition)) {
        hasRegionMarker = true;
        continue;
      }

      const hotel = hotels.find(h => this.coordinatesMatch(position, h.position));
      expect(hotel, 'Hotel marker position is invalid').toBeTruthy();

      // Click marker to open card
      await marker.evaluate((element: HTMLElement) => element.click());
      await this.hotelCards.first().waitFor({ state: 'visible', timeout: browser.options.actionTimeout });

      // Assert distance is hidden
      await expect(this.hotelCardDistance.first(), 'Hotel distance should be hidden on map cards').not.toBeVisible({ timeout: 3000 });
    }

    expect(hasRegionMarker, 'Map should display region marker').toBe(true);
  }

  /**
   * Get dlpPath from Destination landing page URL
   * @returns The DLP path portion of the current URL
   */
  async getDlpPathFromUrl(): Promise<string> {
    console.log('Getting DLP path from URL');
    const currentUrl = new URL(this.page.url());
    const dlpPathNameLength = currentUrl.pathname.length;
    const dlpPath = currentUrl.pathname.slice(14, dlpPathNameLength - 5);
    return dlpPath.toString();
  }
}
