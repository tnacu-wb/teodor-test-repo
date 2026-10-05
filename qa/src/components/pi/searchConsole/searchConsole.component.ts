import { type Page, type Locator } from '@playwright/test';
import type { SearchCriteriaData } from '../../../test-data/searchCriteria';
import { CalendarComponent } from '../../shared/calendar.component';
import { RoomPickerComponent } from './roomPicker.component';
import { SearchResultsListViewComponent } from '../searchResults/searchResultsListView.component';

export { CalendarComponent, RoomPickerComponent };

/**
 * Room configuration for the search console room picker.
 */
export interface RoomConfig {
  adults: number;
  children: number;
}

/** Search parameters accepted by the HDP search console. */
export interface HdpSearchOptions {
  hotelName: string;
  checkInDate: string;
  checkOutDate: string;
  rooms: RoomConfig[];
}

export interface CriteriaSearchOptions {
  searchCriteria: SearchCriteriaData;
  useSuggestedHotelName?: boolean;
}

/**
 * PI Search Console Component — the location/date/room search widget
 * on premierinn.com (Homepage, HDP).
 * Selectors mirror qa/reference searchConsoleBase.js (PI / non-'pib' branch).
 */
export class SearchConsoleComponent {
  private readonly page: Page = global.page;
  private readonly actionTimeout = global.browser?.options?.actionTimeout ?? 15000;

  // ######## UI elements/properties ########

  /** Collapsed read-only summary shown after a search has been performed. */
  readonly searchSummaryLocationLabel: Locator = this.page.locator('p[data-testid="search-summary-location"]');
  readonly locationInput: Locator = this.page.locator('input[data-testid="locationPicker-locationPlaceholder"]');
  readonly locationClearButton: Locator = this.page.locator('div[data-testid="locationPicker-clearLocationButton"]');
  readonly suggestionsList: Locator = this.page.locator('[data-testid="locationPicker-autocompleteList"]');
  readonly suggestedHotelsList: Locator = this.suggestionsList.locator('[data-testid="locationPicker-hotelsLabel"] ~ li[aria-selected="false"]');
  readonly datesButton: Locator = this.page.locator('input[aria-label="datepicker-input"]:visible').first();
  readonly submitButton: Locator = this.page.locator('button[name="search-button"]:visible').first();
  readonly roomPicker: RoomPickerComponent = new RoomPickerComponent();
  readonly calendar: CalendarComponent = new CalendarComponent();
  readonly searchResultsListView: SearchResultsListViewComponent = new SearchResultsListViewComponent();

  // ######## UI actions/navigation ########

  /**
   * Expand the search console from its collapsed summary state, if present.
   */
  async expandIfCollapsed(): Promise<void> {
    console.log('Expand search console if collapsed');
    try {
      await this.searchSummaryLocationLabel.waitFor({ state: 'visible', timeout: 2000 });
      await this.searchSummaryLocationLabel.click();
    } catch {
      // Already expanded (e.g. first visit to Homepage) — nothing to do.
    }
  }

  /**
   * Clear the pre-filled location input.
   */
  async clearLocation(): Promise<void> {
    console.log('Clear location input');
    await this.expandIfCollapsed();
    await this.locationInput.waitFor({ state: 'visible', timeout: this.actionTimeout });
    await this.locationInput.click();
    await this.locationClearButton.click();
  }

  /**
   * Type a hotel name and select the first matching autocomplete hotel suggestion.
   * Retries typing since the debounced autocomplete call occasionally misses the first attempt.
   */
  async searchForHotel(hotelName: string): Promise<void> {
    console.log(`Search for hotel=${hotelName}`);
    await this.locationInput.waitFor({ state: 'visible', timeout: this.actionTimeout });
    await this.locationInput.click();
    await this.page.context().setHTTPCredentials(null); // Clear any HTTP auth credentials to avoid interfering with autocomplete requests
    await this.locationInput.fill(hotelName.substring(0, hotelName.length - 1));
    await this.locationInput.press(hotelName.charAt(hotelName.length - 1));
    const firstSuggestion = this.suggestionsList.locator('li, [role="option"]').first();
    await firstSuggestion.waitFor({ state: 'visible', timeout: this.actionTimeout });
    await firstSuggestion.click();

    const httpAuthUsername = global.browser?.options?.httpAuthUsername;
    if (httpAuthUsername) {
      await this.page.context().setHTTPCredentials({
        username: httpAuthUsername,
        password: global.browser?.options?.httpAuthPassword ?? '',
      });
    }
  }

  /**
   * Click the submit/search button and wait for the resulting page navigation to settle,
   * so callers don't race a transitional render with stale availability data.
   */
  async submit(): Promise<void> {
    console.log('Click search submit button');
    await this.submitButton.waitFor({ state: 'visible', timeout: this.actionTimeout });
    await this.submitButton.click({ force: true });
    await this.page.waitForLoadState('networkidle', { timeout: 30000 }).catch(() => {});
  }

  /**
   * Set check-in and check-out dates using the calendar picker.
   */
  async setDates(checkInDate: string, checkOutDate: string): Promise<void> {
    console.log(`Set dates checkIn=${checkInDate} checkOut=${checkOutDate}`);
    await this.datesButton.click();
    await this.selectDate(checkInDate);
    await this.selectDate(checkOutDate);
  }

  /** Select a single date in the calendar picker (used by `setDates` for check-in/check-out). */
  private async selectDate(isoDate: string): Promise<void> {
    console.log(`Select date=${isoDate}`);
    await this.calendar.selectDate(new Date(isoDate));
  }

  /**
   * Set room configuration (adults/children) via the per-room dropdowns.
   */
  async setRooms(rooms: RoomConfig[]): Promise<void> {
    console.log(`Set rooms=${JSON.stringify(rooms)}`);
    await this.roomPicker.dropdownButton.click();
    await this.roomPicker.dropdownPanel.waitFor({ state: 'visible' });

    for (let i = 0; i < rooms.length; i++) {
      const room = rooms[i];
      await this.roomPicker.selectAdults(i, room.adults);
      await this.roomPicker.selectChildren(i, room.children);
    }

    await this.roomPicker.doneButton.click();
  }

  /**
   * Perform a full search: set location, dates, rooms, then submit.
   */
  async performSearch(options: HdpSearchOptions | CriteriaSearchOptions): Promise<void> {
    const isCriteriaSearch = 'searchCriteria' in options;
    const hotelName = isCriteriaSearch ? options.searchCriteria.location.name : options.hotelName;
    const checkInDate = isCriteriaSearch
      ? options.searchCriteria.arrivalDate.toISOString().slice(0, 10)
      : options.checkInDate;
    const checkOutDate = isCriteriaSearch
      ? options.searchCriteria.departureDate.toISOString().slice(0, 10)
      : options.checkOutDate;
    const rooms = isCriteriaSearch
      ? options.searchCriteria.rooms.map(room => ({ adults: room.adultsNumber, children: room.childrenNumber }))
      : options.rooms;

    console.log(`Perform search hotelName=${hotelName} checkIn=${checkInDate} checkOut=${checkOutDate}`);
    const submittedUrl = this.page.url();
    await this.clearLocation();
    await this.searchForHotel(hotelName);
    await this.setDates(checkInDate, checkOutDate);
    await this.setRooms(rooms);
    await this.submit();

    if (isCriteriaSearch) {
      await this.page.waitForURL(url => url.toString() !== submittedUrl, { timeout: global.browser.options.navigationTimeout }).catch(() => {});
      if (options.useSuggestedHotelName && !this.page.url().includes('/hotels/')) {
        await this.searchResultsListView.openHotelDetailsByHotelName(hotelName);
      }
    }
  }

  // ######## UI validations ########
}
