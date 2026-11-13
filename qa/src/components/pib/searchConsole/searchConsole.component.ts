import { expect, type Locator, type Page } from "@playwright/test";
import { Constants } from "../../../test-data/constants";
import type { SearchCriteria } from "../../../test-data/searchCriteria";
import { CalendarComponent } from "../../shared/calendar.component";
import { SearchResultsListViewComponent } from "../../pi/searchResults/searchResultsListView.component";
import { RoomsPanelComponent } from "./roomsPanel.component";

/**
 * Search console/bar component containing UI elements, custom actions and validations available only for IB
 */
export class SearchConsoleComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly searchBarContainer: Locator = this.page.locator('[data-testid="IB-Search-Container-Desktop"], [data-testid="IB-Search-Container-Mobile"], [data-testid="SearchBar-Container"], [data-testid="search-bar"]');
  readonly searchBarSummaryContainer: Locator = this.searchBarContainer;
  readonly locationInput: Locator = this.searchBarContainer.locator('[data-testid="IB-Location-Input"]:visible').first();
  readonly clearLocationButton: Locator = this.searchBarContainer.locator('[data-testid="IB-Location-Clear"]:visible').first();
  readonly searchButton: Locator = this.searchBarContainer.locator('[data-testid="IB-Search-Button"]:visible').first();
  readonly datesButton: Locator = this.searchBarContainer.locator('[data-testid="IB-Date-Picker-Input"]:visible').first();
  readonly searchSummaryLocationLabel: Locator = this.page.locator('[data-testid="search-summary-location"]');
  readonly searchSummaryEditButton: Locator = this.searchSummaryLocationLabel;
  readonly searchSummaryDatesLabel: Locator = this.page.locator('[data-testid="IB-Date-Picker-Text"]');
  readonly roomsPickerButton: Locator = this.searchBarContainer.locator('[data-testid="Room-Occupancy-Button"]:visible').first();
  readonly roomsPickerLabel: Locator = this.roomsPickerButton.locator('span');
  readonly suggestionsContainer: Locator = this.page.locator('[data-testid="Location-Results-Container"], [data-testid="locationPicker-autocompleteList"]');
  readonly suggestedLocations: Locator = this.page.locator('[data-testid="IB-Places-List"] > div');
  readonly suggestedHotels: Locator = this.page.locator('[data-testid="IB-Hotels-List"] > button');
  readonly roomsPanel: RoomsPanelComponent = new RoomsPanelComponent();
  readonly searchResultsListView: SearchResultsListViewComponent = new SearchResultsListViewComponent();
  readonly calendar = new CalendarComponent();

  // ######## UI actions/navigation ########

  /** Set the search location and optionally wait for predictive suggestions. */
  async setLocation({ searchedText, waitForSuggestionsContainer = true }: { searchedText: string; waitForSuggestionsContainer?: boolean }): Promise<void> {
    console.log(`Set IB search location=${searchedText}`);
    if (await this.searchSummaryLocationLabel.isVisible().catch(() => false)) {
      await this.searchSummaryEditButton.click();
    }
    await this.locationInput.waitFor({ state: "visible", timeout: 40000 });
    await this.locationInput.fill(searchedText);
    if (waitForSuggestionsContainer) {
      await this.suggestionsContainer.waitFor({ state: "visible", timeout: 30000 });
    }
  }

  /** Select a suggested location by index. */
  async selectSuggestedLocationByIndex(index: number): Promise<void> {
    console.log(`Select IB suggested location index=${index}`);
    await this.suggestedLocations.nth(index).click();
  }

  /** Select a suggested hotel by index. */
  async selectSuggestedHotelByIndex(index: number): Promise<void> {
    console.log(`Select IB suggested hotel index=${index}`);
    await this.suggestedHotels.nth(index).click();
  }

  /** Click the room picker. */
  async clickRoomsPicker(): Promise<void> {
    console.log("Click IB rooms picker");
    await this.roomsPickerButton.click();
  }

  /** Click the search button. */
  async clickSearchButton(): Promise<void> {
    console.log("Click IB search button");
    await this.searchButton.click();
  }

  /** Click the location field. */
  async clickLocationField(): Promise<void> {
    console.log("Click IB location field");
    await this.locationInput.click();
  }

  /** Click the date-picker field. */
  async clickDatesPickerInputField(): Promise<void> {
    console.log("Click IB dates picker field");
    await this.datesButton.click();
  }

  /** Clear the location field. */
  async clickClearLocationButton(): Promise<void> {
    console.log("Clear IB location field");
    await this.clearLocationButton.click();
  }

  /** Clear location and reset the date picker. */
  async clearLocationAndDateFields(): Promise<void> {
    console.log("Clear IB search console fields");
    await this.clickClearLocationButton();
    await this.calendar.clickReset();
  }

  /** Select an arrival and departure date range. */
  async selectDatesRange({ arrivalDate, departureDate }: { arrivalDate: Date; departureDate: Date }): Promise<void> {
    console.log(`Select IB dates arrival=${arrivalDate.toISOString()} departure=${departureDate.toISOString()}`);
    await this.clickDatesPickerInputField();
    await this.calendar.selectDate(arrivalDate);
    await this.calendar.selectDate(departureDate);
    await this.calendar.clickDone();
  }

  /** Search for a hotel from the IB homepage search console. */
  async searchHotels(options: {
    hotelName?: string;
    arrivalDate?: Date;
    departureDate?: Date;
    searchCriteria?: SearchCriteria;
    performSearch?: boolean;
    useSuggestedHotelName?: boolean;
    selectRooms?: boolean;
  }): Promise<void> {
    const searchCriteria = options.searchCriteria;
    const hotelName = options.hotelName ?? searchCriteria?.location.name ?? "";
    const arrivalDate = options.arrivalDate ?? searchCriteria?.arrivalDate ?? new Date();
    const departureDate = options.departureDate ?? searchCriteria?.departureDate ?? new Date(Date.now() + 86400000);
    const performSearch = options.performSearch ?? true;
    console.log(`Search IB hotel=${hotelName}`);
    await this.setLocation({ searchedText: hotelName });
    if (options.useSuggestedHotelName === false) {
      await this.selectSuggestedLocationByIndex(0);
    } else {
      await this.selectSuggestedHotelByIndex(0);
    }
    await this.selectDatesRange({ arrivalDate, departureDate });
    if (options.selectRooms !== false && searchCriteria?.rooms) {
      await this.clickRoomsPicker();
      await this.roomsPanel.selectRooms({ roomsList: searchCriteria.rooms });
    }
    const submittedUrl = this.page.url();
    if (performSearch) await this.clickSearchButton();
    if (performSearch) {
      await this.page
        .waitForURL((url) => url.toString() !== submittedUrl, { timeout: browser.options.navigationTimeout })
        .catch(() => {});
    }
    await this.page.waitForLoadState("domcontentloaded");
    if (performSearch && options.useSuggestedHotelName && !this.page.url().includes('/hotels/')) {
      await this.searchResultsListView.openHotelDetailsByHotelName(hotelName);
    }
  }

  /** Get suggested locations list. */
  async getSuggestedLocationsList(): Promise<string[]> {
    console.log("Get suggested locations");
    return (await this.suggestedLocations.allTextContents()).map((text) => text.trim()).filter(Boolean);
  }
  /** Get suggested hotels list. */
  async getSuggestedHotelsList(): Promise<string[]> {
    console.log("Get suggested hotels");
    return (await this.suggestedHotels.allTextContents()).map((text) => text.trim()).filter(Boolean);
  }
  // ######## UI validations ########
  /** Validate predictive suggestions list shown in Location field. */
  async validateLocationPredictiveSuggestions(): Promise<void> {
    console.log("Validate location predictive suggestions");
    await expect( this.suggestionsContainer, "Suggestions container", ).toBeVisible();
    const suggestedLocations = await this.getSuggestedLocationsList();
    const suggestedHotels = await this.getSuggestedHotelsList();
    console.log(`Suggested locations count: ${suggestedLocations.length}`);
    console.log(`Suggested hotels count: ${suggestedHotels.length}`);
    expect( suggestedLocations.length, `Suggested locations count should be <= ${Constants.MAX_LOCATION_SUGGESTIONS_NUMBER}`, ).toBeLessThanOrEqual(Constants.MAX_LOCATION_SUGGESTIONS_NUMBER);
    expect( suggestedHotels.length, `Suggested hotels count should be <= ${Constants.MAX_LOCATION_SUGGESTIONS_NUMBER}`, ).toBeLessThanOrEqual(Constants.MAX_LOCATION_SUGGESTIONS_NUMBER);
  }
}
