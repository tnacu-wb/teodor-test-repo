import { type Locator, expect } from '@playwright/test';
import { Constants } from '../../test-data/constants';
import { Strings } from '../../test-data/strings';
import { BasePage } from '../shared/base.page';
import { SearchResultsListViewComponent } from '../../components/pi/searchResults/searchResultsListView.component';

/**
 * Search Results Page (SRP) - list of hotels matching a search, with map/list toggle,
 * sorting and filtering. Mirrors the PI-relevant subset of qa/reference `searchResults.page.js`
 * (BB/CCUI-specific project branching and IB filter workaround are out of scope for PI).
 */
export class SearchResultsPage extends BasePage {

  // ######## UI elements/properties ########

  readonly searchResultsListView: SearchResultsListViewComponent = new SearchResultsListViewComponent();
  readonly alertDescriptionLabel: Locator = this.page.locator('[data-testid="AlertDescription"]');
  readonly alertDescriptionInHotelListContainerLabel: Locator = this.page.locator(
    '.infinite-scroll-component [data-testid="AlertDescription"]'
  );
  readonly switchViewButton: Locator = this.page.locator('button[data-testid="controls-map-or-list-button"]');
  readonly mobileSwitchViewButton: Locator = this.page.locator('[data-testid*="-view-button"]');
  readonly sortDropdown: Locator = this.page.locator('button[data-testid="DropdownComp-sort-by-menuButton"]');
  readonly sortDropdownList: Locator = this.page.locator('div[data-testid="DropdownComp-sort-by-entireList"] button');
  readonly sortByDistanceButton: Locator = this.page.locator('button[data-testid^="DropdownComp-sort-by-"][value="DISTANCE"]');
  readonly sortByPriceButton: Locator = this.page.locator('button[data-testid^="DropdownComp-sort-by-"][value="PRICE"]');
  readonly filterByButton: Locator = this.page.locator('button[data-testid="SRP-controls-filter-by-button"]');
  readonly mapContainer: Locator = this.page.locator('[data-testid="SRP-mapView"]');

  // ######## UI actions/navigation ########

  /** Click the map view button (or the combined switch-view button on mobile). */
  async clickMapViewButton({ applyResizeWorkaround = true }: { applyResizeWorkaround?: boolean } = {}): Promise<void> {
    console.log('Click map view button');
    if (Constants.BROWSER_RESOLUTIONS.isDesktop() && applyResizeWorkaround) {
      const viewportSize = this.page.viewportSize();
      const [tabletWidth, tabletHeight] = Constants.BROWSER_RESOLUTIONS.tablet.split(',').map(Number);
      await this.page.setViewportSize({ width: tabletWidth, height: tabletHeight });
      await this.page.reload();
      if (viewportSize) {
        await this.page.setViewportSize(viewportSize);
      }
    }
    const button = Constants.BROWSER_RESOLUTIONS.isDesktop() ? this.switchViewButton : this.mobileSwitchViewButton;
    await this.clickSwitchViewButton(button);
    await this.mapContainer.waitFor({ state: 'attached' });
  }

  /** Click the list view button (or the combined switch-view button on mobile). */
  async clickListViewButton(): Promise<void> {
    console.log('Click list view button');
    const button = Constants.BROWSER_RESOLUTIONS.isDesktop() ? this.switchViewButton : this.mobileSwitchViewButton;
    await this.clickSwitchViewButton(button);
    await this.searchResultsListView.hotelCardsContainer.waitFor({ state: 'attached' });
  }

  /** Click the given switch-view button. */
  private async clickSwitchViewButton(switchViewButton: Locator): Promise<void> {
    console.log('Click search-results view switch button');
    await switchViewButton.scrollIntoViewIfNeeded();
    await switchViewButton.click();
  }

  /** Click the "Filter by" button. */
  async clickFilterByButton(): Promise<void> {
    console.log('Click Filter by button');
    await this.filterByButton.scrollIntoViewIfNeeded();
    await this.filterByButton.click();
  }

  /** Sort the search results by price. */
  async sortByPrice(): Promise<void> {
    console.log('Sort search results by price');
    await this.clickSortByOption(this.sortByPriceButton);
  }

  /** Sort the search results by distance. */
  async sortByDistance(): Promise<void> {
    console.log('Sort search results by distance');
    await this.clickSortByOption(this.sortByDistanceButton);
  }

  /** Click on the specified sorting option in the sort dropdown. */
  private async clickSortByOption(optionElement: Locator): Promise<void> {
    console.log('Select search-results sort option');
    await this.sortDropdown.scrollIntoViewIfNeeded();
    await this.sortDropdown.click();
    await optionElement.click();
    await this.searchResultsListView.waitForLoadingHotelCards();
  }

  // ######## UI validations ########

  /** Check we reached the current page by checking the hotel cards container is displayed. */
  async validatePage(): Promise<void> {
    console.log('Validate Search Results page');
    await expect(this.searchResultsListView.hotelCardsContainer, 'Hotel cards container').toBeVisible({ timeout: 60000 });
  }

  /** Validate the alert description for hotel not found. */
  async validateAlertDescriptionHotelNotFound(): Promise<void> {
    console.log('Validate hotel-not-found alert');
    await expect(this.alertDescriptionLabel, 'Alert description is incorrect').toContainText(await Strings.SORRY_WE_COULDNT_FIND_ANY_HOTELS.name);
  }

  /** Validate the alert description for hotel not matching criteria. */
  async validateAlertDescriptionHotelNotMatchingCriteria(): Promise<void> {
    console.log('Validate hotel-not-matching-criteria alert');
    await expect(this.alertDescriptionLabel, 'Alert description is incorrect').toContainText(await Strings.WE_COULDNT_FIND_ANY_HOTELS.name);
  }

  /** Validate the alert description for hotel fully booked. */
  async validateAlertDescriptionHotelFullyBooked(): Promise<void> {
    console.log('Validate fully-booked hotel alert');
    await expect(this.alertDescriptionInHotelListContainerLabel, 'Alert description is incorrect').toContainText(await Strings.THIS_HOTEL_IS_FULLY_BOOKED.name);
  }

  /** Validate the alert description for hotel opening soon. */
  async validateAlertDescriptionHotelOpeningSoon(): Promise<void> {
    console.log('Validate hotel-opening-soon alert');
    await expect(this.alertDescriptionInHotelListContainerLabel, 'Alert description is incorrect').toContainText(await Strings.THIS_HOTEL_WILL_BE_OPENING_SOON.name);
  }

  /** Validate the map view button visibility (desktop label check, mobile visibility-only check). */
  async validateMapViewButton({ isDisplayed = true }: { isDisplayed?: boolean } = {}): Promise<void> {
    if (Constants.BROWSER_RESOLUTIONS.isDesktop()) {
      if (isDisplayed) {
        await expect(this.switchViewButton, 'Map view button label').toContainText(await Strings.MAP_VIEW.name);
      } else {
        await expect(this.switchViewButton, 'Map view button visibility').toBeHidden();
      }
    } else {
      if (isDisplayed) await expect(this.mobileSwitchViewButton, 'Switch view type button').toBeVisible();
      else await expect(this.mobileSwitchViewButton, 'Switch view type button').toBeHidden();
    }
  }

  /** Validate the list view button visibility (desktop label check, mobile visibility-only check). */
  async validateListViewButton({ isDisplayed = true }: { isDisplayed?: boolean } = {}): Promise<void> {
    if (Constants.BROWSER_RESOLUTIONS.isDesktop()) {
      if (isDisplayed) {
        await expect(this.switchViewButton, 'List view button label').toContainText(await Strings.LIST_VIEW.name);
      } else {
        await expect(this.switchViewButton, 'List view button visibility').toBeHidden();
      }
    } else {
      if (isDisplayed) await expect(this.mobileSwitchViewButton, 'Switch view type button').toBeVisible();
      else await expect(this.mobileSwitchViewButton, 'Switch view type button').toBeHidden();
    }
  }

  /** Validate the "Filter by" button visibility. */
  async validateFilterByButton({ isDisplayed }: { isDisplayed: boolean }): Promise<void> {
    if (isDisplayed) {
      await expect(this.filterByButton, 'Filter by button').toBeVisible();
      await expect(this.filterByButton, 'Filter by button').toContainText(await Strings.FILTER_BY.name);
    } else {
      await expect(this.filterByButton, 'Filter by button').toBeHidden();
    }
  }

  /** Validate the sort dropdown visibility. */
  async validateSortDropdown({ isDisplayed }: { isDisplayed: boolean }): Promise<void> {
    if (isDisplayed) {
      await expect(this.sortDropdown, 'Sort Dropdown').toBeVisible();
    } else {
      await expect(this.sortDropdown, 'Sort Dropdown').toBeHidden();
    }
  }

  /** Validate the map container visibility. */
  async validateMapContainer({ isDisplayed }: { isDisplayed: boolean }): Promise<void> {
    console.log('Validate the map container');
    if (isDisplayed) {
      await expect(this.mapContainer, 'Map container').toBeVisible();
    } else {
      await expect(this.mapContainer, 'Map container').toBeHidden();
    }
  }

  /** Validate the view type recorded in the current URL ("VIEW=1" map, "VIEW=2" list). */
  async validateUrlViewType({ viewType }: { viewType: number }): Promise<void> {
    expect(this.page.url(), 'The URL does not have the correct view type').toContain(`VIEW=${viewType}`);
  }

  /** Validate the MLOS (minimum length of stay) alert label. */
  async validateMlosAlertLabel({ isDisplayed }: { isDisplayed: boolean }): Promise<void> {
    console.log('Validate MLOS alert label');
    if (isDisplayed) {
      await expect(this.alertDescriptionLabel, 'Alert label').toBeVisible();
      await expect(this.alertDescriptionLabel, 'MLOS alert label text').toContainText(await Strings.THIS_HOTEL_HAS_A_MINIMUM_LENGTH_OF_STAY_RESTRICTION.name);
    } else {
      await expect(this.alertDescriptionLabel, 'Alert label').toBeHidden();
    }
  }

  /**
   * Validate the Search Results Page has the expected data in List View mode.
   */
  async validateDataForListView(): Promise<void> {
    console.log('Validate Search Results Page data in List View mode');
    await expect(this.searchResultsListView.hotelCardsContainer, 'Hotel cards container').toBeVisible();
    await this.searchResultsListView.validateListContainer({ isDisplayed: true });
    await this.validateFilterByButton({ isDisplayed: true });
    await this.validateMapViewButton({ isDisplayed: true });
    await this.validateMapContainer({ isDisplayed: !Constants.BROWSER_RESOLUTIONS.isDesktop() });
  }

  /**
   * Validate the Search Results Page has the expected data in Map View mode.
   */
  async validateDataForMapView(): Promise<void> {
    console.log('Validate Search Results Page data in Map View mode');
    await expect(this.mapContainer, 'Map container').toBeVisible({ timeout: 30000 });
    await this.validateFilterByButton({ isDisplayed: true });
    await this.validateListViewButton({ isDisplayed: true });
    await this.validateMapContainer({ isDisplayed: true });
    await this.searchResultsListView.validateListContainer({ isDisplayed: false });
  }
}
