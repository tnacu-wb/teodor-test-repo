import { type Page, type Locator, expect } from '@playwright/test';

/**
 * The Search Results map view containing the UI elements, custom actions and validations.
 * Mirrors qa/reference `components/common/searchResults/searchResultsMapView.js`.
 */
export class SearchResultsMapViewComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly mapContainer: Locator = this.page.locator('div[data-testid="SRP-mapView"]');
  readonly hotelPinsOnMap: Locator = this.mapContainer.locator('div[role="button"][aria-label]');
  readonly infoCardHotelThumbnail: Locator = this.mapContainer.locator('div[data-testid="SRPMapView-hotel-thumbnail"]');
  readonly infoCardHotelTitle: Locator = this.mapContainer.locator('p[data-testid="SRPMapView-hotel-title"]');
  readonly infoCardHotelDistance: Locator = this.mapContainer.locator('p[data-testid="SRPMapView-hotel-distance"]');
  readonly infoCardHotelLowestRate: Locator = this.mapContainer.locator('div[data-testid="SRPMapView-lowest-rate"]');
  readonly infoCardHotelBadges: Locator = this.mapContainer.locator('div[data-testid="SRPMapView-hotel-badges"]');
  readonly infoCardHotelHubBadge: Locator = this.mapContainer.locator('div[data-testid="srp_hotel-brand-logo"]');
  readonly infoCardHotelMigrationBadge: Locator = this.mapContainer.locator('div[data-testid="SRPMapView-hotel-migration-badge"]');
  readonly infoCardHotelLastFewRooms: Locator = this.mapContainer.locator('p[data-testid="SRPMapView-hotel-last-few-rooms"]');
  readonly infoCardHotelSoldOutBadge: Locator = this.mapContainer.locator('p[data-testid="SRPMapView-hotel-soldout"]');
  readonly infoCardHotelArrowButton: Locator = this.mapContainer.locator('button div[data-testid="svg-container"]');

  // ######## UI actions/navigation ########

  /** Click the map pin with the given 0-based index. */
  async clickHotelPin(index: number): Promise<void> {
    console.log(`Click hotel pin ${index}`);
    await this.hotelPinsOnMap.nth(index).click();
  }

  /** Click the opened hotel's image thumbnail. */
  async clickImageThumbnailForOpenedHotelInfoCard(): Promise<void> { console.log('Click map info-card hotel thumbnail'); await this.infoCardHotelThumbnail.click(); }

  /** Return map pins whose aria-label contains the requested badge. */
  async getClickableSrpMapViewElementsByBadge(badgeName: string): Promise<Locator[]> {
    console.log(`Get clickable map elements with ${badgeName}`);
    const elements: Locator[] = [];
    for (let index = 0; index < await this.hotelPinsOnMap.count(); index++) {
      const pin = this.hotelPinsOnMap.nth(index);
      if ((await pin.getAttribute('aria-label') ?? '').includes(badgeName)) elements.push(pin);
    }
    return elements;
  }

  /** Return clickable map pins matching the country currency prefix. */
  async getClickableSRPMapElements(countryCode: string): Promise<Locator[]> {
    console.log(`Get clickable map elements for ${countryCode}`);
    const currency = countryCode === 'GB' ? '£' : '€';
    await expect(this.mapContainer.locator(`div[role="button"][aria-label*="${currency}"]`).first(), 'Available SRP map price pin').toBeVisible()
      .catch(() => console.log('No available hotel price pins were displayed on the SRP map'));
    const elements: Locator[] = [];
    for (let index = 0; index < await this.hotelPinsOnMap.count(); index++) {
      const pin = this.hotelPinsOnMap.nth(index);
      const label = await pin.getAttribute('aria-label');
      if (label?.includes(currency)) elements.push(pin);
    }
    return elements;
  }

  /** Close the open map info card. */
  async closeOpenedMapViewInfoCard(): Promise<void> {
    console.log('Close opened map info card');
    const closeButton = this.mapContainer.getByRole('button', { name: 'Close' });
    if (await closeButton.isVisible()) await closeButton.click();
  }

  /** Return the currently displayed info-card hotel title. */
  async returnInfoCardHotelTitle(): Promise<string> { return this.infoCardHotelTitle.innerText(); }

  /** Click a supplied map pin locator. */
  async clickOnMapPin(pinElement: Locator): Promise<void> { console.log('Click supplied map pin'); await pinElement.click(); }

  /** Check whether the info card currently shows the given hotel name. */
  async isDesiredHotelDisplayedInSrpMapView(hotelName: string): Promise<boolean> {
    console.log(`Check desired map hotel: ${hotelName}`);
    await this.infoCardHotelTitle.waitFor({ state: 'visible' });
    const displayedTitle = await this.infoCardHotelTitle.innerText();
    const normalizeHotelTitle = (title: string): string => title.replace(/[^A-Za-z0-9]+/g, ' ').trim().toLowerCase();
    return normalizeHotelTitle(displayedTitle).includes(normalizeHotelTitle(hotelName));
  }

  // ######## UI validations ########

  /** Validate the map container is displayed with at least one hotel pin. */
  async validateMapIsDisplayed(): Promise<void> {
    console.log('Validate map is displayed');
    await expect(this.mapContainer, 'SRP map container').toBeVisible();
    expect(await this.hotelPinsOnMap.count(), 'SRP map hotel pins count').toBeGreaterThan(0);
  }

  /** Validate the info card hotel title matches the expected hotel name. */
  async validateInfoCardHotelTitle(expectedHotelName: string): Promise<void> {
    console.log(`Validate map info-card hotel title: ${expectedHotelName}`);
    await expect(this.infoCardHotelTitle, 'SRP map info card hotel title').toContainText(expectedHotelName);
  }

  /** Validate info-card element visibility. */
  async validateInfoCardThumbnailDisplay(isDisplayed = true): Promise<void> { await this.validateVisibility(this.infoCardHotelThumbnail, 'Info card hotel thumbnail', isDisplayed); }
  async validateInfoCardHotelTitleDisplay(isDisplayed = true): Promise<void> { await this.validateVisibility(this.infoCardHotelTitle, 'Info card hotel title', isDisplayed); }
  async validateInfoCardDistanceDisplay(isDisplayed = true): Promise<void> { await this.validateVisibility(this.infoCardHotelDistance, 'Info card distance', isDisplayed); }
  async validateInfoCardLowestRateDisplay(isDisplayed = true): Promise<void> { await this.validateVisibility(this.infoCardHotelLowestRate, 'Info card lowest rate', isDisplayed); }
  async validateInfoCardHotelHubBadgeDisplay(isDisplayed = true): Promise<void> { await this.validateVisibility(this.infoCardHotelHubBadge, 'Info card hub badge', isDisplayed); }
  async validateInfoCardHotelBadgeDisplay(isDisplayed = true): Promise<void> { await this.validateVisibility(this.infoCardHotelBadges, 'Info card hotel badges', isDisplayed); }
  async validateInfoCardHotelLastFewRoomsBadgeDisplay(isDisplayed = true): Promise<void> { await this.validateVisibility(this.infoCardHotelLastFewRooms, 'Info card last few rooms', isDisplayed); }
  async validateInfoCardSoldOutBadgeIsDisplay(isDisplayed = true): Promise<void> { await this.validateVisibility(this.infoCardHotelSoldOutBadge, 'Info card sold out', isDisplayed); }
  async validateInfoCardMigrationBadgeIsDisplay(isDisplayed = true): Promise<void> { await this.validateVisibility(this.infoCardHotelMigrationBadge, 'Info card migration badge', isDisplayed); }
  async validateInfoCardHotelArrowButtonIsDisplayed(): Promise<void> { await expect(this.infoCardHotelArrowButton, 'Hotel card arrow to HDP').toBeVisible(); }
  async validateHotelPinsOnTheMap({ isDisplayed }: { isDisplayed: boolean }): Promise<void> { await this.validateVisibility(this.hotelPinsOnMap, 'Hotel pins on map', isDisplayed); }

  /** Validate a map info card and its requested badge state. */
  async validateSRPMapViewInfoCard(options: { isRateDisplayed?: boolean; isHotelHub?: boolean; isHotelPremium?: boolean; isLastFewRooms?: boolean; isSoldOut?: boolean; hotelTitle?: string } = {}): Promise<void> {
    console.log('Validate SRP map info card');
    await this.validateInfoCardThumbnailDisplay();
    await this.validateInfoCardHotelTitleDisplay();
    await this.validateInfoCardDistanceDisplay();
    await this.validateInfoCardLowestRateDisplay(options.isRateDisplayed ?? true);
    if (options.hotelTitle) await this.validateInfoCardHotelTitle(options.hotelTitle);
    if (options.isHotelHub) await this.validateInfoCardHotelHubBadgeDisplay();
    if (options.isHotelPremium) await this.validateInfoCardHotelBadgeDisplay();
    if (options.isLastFewRooms) await this.validateInfoCardHotelLastFewRoomsBadgeDisplay();
    if (options.isSoldOut) await this.validateInfoCardSoldOutBadgeIsDisplay();
  }

  private async validateVisibility(locator: Locator, description: string, isDisplayed: boolean): Promise<void> {
    console.log(`Validate ${description} displayed=${isDisplayed}`);
    if (isDisplayed) await expect(locator, description).toBeVisible();
    else await expect(locator, description).toBeHidden();
  }
}
