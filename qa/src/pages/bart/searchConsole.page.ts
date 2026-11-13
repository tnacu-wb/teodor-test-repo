import { type Locator } from '@playwright/test';
import { BartRoomsPanelComponent } from '../../components/shared/bart/searchConsole/roomsPanel.component';
import { BasePage } from '../shared/base.page';

/**
 * Search console/bar section of the legacy ("bart") Premier Inn web application. Mirrors
 * qa/reference `pages/bart/searchConsole.page.js`. Inert/unwired - see session memory.
 */
export class BartSearchConsolePage extends BasePage {
  // ######## UI elements/properties ########

  readonly searchBtn: Locator = this.page.locator('#search-console__form-button');
  readonly locationInput: Locator = this.page.locator('.search-console__location input');
  readonly clearLocationBtn: Locator = this.page.locator('.clear-input');
  readonly datesPicker: Locator = this.page.locator('[class*="search-console__dates"]');
  readonly calendarTitleLabel: Locator = this.page.locator('.dropdown-menu.show .calendar-heading');
  readonly calendarNextMonthBtn: Locator = this.page.locator('.dropdown-menu.show button[class*="date-picker-right-arrow"]');
  readonly calendarPreviousMonthBtn: Locator = this.page.locator('.dropdown-menu.show button[class*="date-picker-left-arrow"]');
  readonly calendarDoneBtn: Locator = this.page.locator('.dropdown-menu.show .calendar-footer button').nth(1);
  readonly calendarResetBtn: Locator = this.page.locator('.dropdown-menu.show .calendar-footer button').nth(0);
  readonly roomsPicker: Locator = this.page.locator('.search-console__rooms');
  readonly searchSummaryLocationLabel: Locator = this.page.locator('.search-summary__location--text');
  readonly searchSummaryDatesLabel: Locator = this.page.locator('.search-summary__dates--text');
  readonly searchSummaryRoomsLabel: Locator = this.page.locator('.search-summary__details--text');
  readonly hotelSearchConsole: Locator = this.page.locator('.search-summary');
  readonly changeSearchBtn: Locator = this.page.locator('#search-summary-change-btn');
  readonly suggestedLocationsList: Locator = this.page.locator('li[class*="location-suggestion"]');
  readonly suggestedHotelsList: Locator = this.page.locator('li[class*="hotel-suggestion"]');

  // UI components

  readonly roomsPanel: BartRoomsPanelComponent = new BartRoomsPanelComponent();

  // ######## UI actions/navigation ########

  /** Search for a location/hotel by name in the location input. */
  async searchForLocation(locationName: string): Promise<void> {
    console.log(`Searching for location: ${locationName}`);
    await this.locationInput.fill(locationName);
  }

  /** Click the search button to submit the search console form. */
  async clickSearch(): Promise<void> {
    await this.searchBtn.click();
  }

  // ######## UI validations ########
}
