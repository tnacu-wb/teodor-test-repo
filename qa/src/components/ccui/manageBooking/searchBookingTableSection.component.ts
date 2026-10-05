import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { CcuiComponent } from '../baseCcui.component';
import { SearchBookingTableRowComponent } from './searchBookingTableRow.component';

/** Search Booking Table section that is part of the CCUI Manage Booking page. */
export class SearchBookingTableSectionComponent extends CcuiComponent {
  // ######## UI elements/properties ########

  readonly resultListTableRows: Locator = this.page.locator('tr[data-testid^="SearchBookingsPage-Table-Row-"]');
  readonly headerBookedForLabel: Locator = this.page.locator('th[data-testid="SearchBookingsPage-TableHeader-BookedFor"]');
  readonly headerBookedByLabel: Locator = this.page.locator('th[data-testid="SearchBookingsPage-TableHeader-BookedBy"]');
  readonly headerHotelLabel: Locator = this.page.locator('th[data-testid="SearchBookingsPage-TableHeader-Hotel"]');
  readonly headerDateLabel: Locator = this.page.locator('th[data-testid="SearchBookingsPage-TableHeader-Date"]');
  readonly headerPriceLabel: Locator = this.page.locator('th[data-testid="SearchBookingsPage-TableHeader-Price"]');
  readonly headerStatusLabel: Locator = this.page.locator('th[data-testid="SearchBookingsPage-TableHeader-Status"]');
  readonly headerPMSLabel: Locator = this.page.locator('th[data-testid="SearchBookingsPage-TableHeader-SourcePms"]');
  readonly loadMoreButton: Locator = this.page.locator('div[data-testid="SearchBookingsPage-Table-Container"] + div button');
  readonly noBookingsNotificationLabel: Locator = this.page.locator('div[data-testid="SearchBookingsPage-NoBookingNotification"]');
  readonly loadingLabel: Locator = this.page.locator('p[data-testid="SearchBookingsPage-Table-loading-message"]');
  readonly expandOrCollapseButton: Locator = this.page.locator('//*[@data-testid="hdp_basketHideBreakdownLink"]');
  readonly maxResultsNotificationLabel: Locator = this.page.locator('div[data-testid="SearchBookingsPage-Table-Container"] div[data-testid="AlertDescription"]');

  /** Get search booking table rows array. */
  async getSearchBookingTableRowsArray(): Promise<SearchBookingTableRowComponent[]> { console.log('Get search booking table rows array'); await this.resultListTableRows.first().waitFor({ state: 'attached' }); const rowCount = await this.resultListTableRows.count(); return Array.from({ length: rowCount }, (_, rowIndex) => new SearchBookingTableRowComponent(rowIndex)); }

  // ######## UI actions/navigation ########
  /** Click Load more button. */
  async clickLoadMore(): Promise<void> { console.log('Click Load More button'); await this.loadMoreButton.scrollIntoViewIfNeeded(); await this.loadMoreButton.click(); await this.loadingLabel.waitFor({ state: 'hidden', timeout: 30000 }); }
  /** Click expand or collapse button. */
  async clickExpandOrCollapse(): Promise<void> { console.log('Click Expand or collapse button'); await this.expandOrCollapseButton.scrollIntoViewIfNeeded(); await this.expandOrCollapseButton.click(); await this.expandOrCollapseButton.waitFor({ state: 'visible' }); }

  // ######## UI validations ########

  /**
   * Validate Header is displayed.
   * @param isPriceDisplayed Whether the Price header should be displayed.
   */
  async validateHeader(isPriceDisplayed = false): Promise<void> { console.log('Validate Table Header'); await expect(this.headerBookedForLabel, 'Booked for header').toHaveText(await Strings.BOOKED_FOR.name); await expect(this.headerBookedByLabel, 'Booked by header').toHaveText(await Strings.BOOKED_BY.name); await expect(this.headerHotelLabel, 'Hotel header').toHaveText(await Strings.HOTEL.name); await expect(this.headerDateLabel, 'Date header').toHaveText(await Strings.DATE.name); await this.validateDisplayState(this.headerPriceLabel, 'Price header', isPriceDisplayed); await expect(this.headerStatusLabel, 'Status header').toHaveText(await Strings.STATUS.name); await expect(this.headerPMSLabel, 'PMS header').toHaveText(await Strings.PMS.name); }
  /**
   * Validate Bookings length.
   * @param expectedLength Expected number of bookings.
   * @param shouldHaveMinimumLength Whether expectedLength is a minimum rather than an exact count.
   */
  async validateBookingsLength({ expectedLength = 1, shouldHaveMinimumLength = false }: { expectedLength?: number; shouldHaveMinimumLength?: boolean } = {}): Promise<void> { console.log('Validate Table Bookings Length'); await this.loadingLabel.waitFor({ state: 'hidden', timeout: 30000 }); const rowCount = await this.resultListTableRows.count(); if (shouldHaveMinimumLength) expect(rowCount, `Table should have at least=${expectedLength} bookings`).toBeGreaterThanOrEqual(expectedLength); else expect(rowCount, `Table should have=${expectedLength} bookings`).toBe(expectedLength); await this.validateDisplayState(this.noBookingsNotificationLabel, 'No bookings notification', expectedLength === 0); }
  /** Validate No Bookings Notification. */
  async validateNoBookingsNotification(): Promise<void> { console.log('Validate No Bookings Notification'); await expect(this.noBookingsNotificationLabel, 'No bookings notification label text').toHaveText(await Strings.NO_BOOKINGS_MATCHING_YOUR_SEARCH.name); }
  /**
   * Validate Bookings Dates are displayed in ascending order.
   * @param bookings Booking date values in display order.
   */
  async validateBookingsDatesAscendingOrder(bookings: number[]): Promise<void> { console.log('Validate bookings dates are in ascending order'); const sortedBookingDates = [...bookings].sort((left, right) => right - left); expect(bookings, 'Bookings are displayed in ascending order').toEqual(sortedBookingDates); }
  /**
   * Validate Load More button.
   * @param isDisplayed Whether the Load More button should be displayed.
   */
  async validateLoadMoreButton({ isDisplayed }: { isDisplayed: boolean }): Promise<void> { console.log(`Validate Load More button is displayed=${isDisplayed}`); await this.validateDisplayState(this.loadMoreButton, 'Load More button', isDisplayed); }
  /**
   * Validate expand or collapse button.
   * @param isDisplayed Whether the control should be displayed.
   * @param isExpanded Whether the control should show the expanded state.
   */
  async validateExpandOrCollapseButtonIsDisplayed({ isDisplayed, isExpanded }: { isDisplayed: boolean; isExpanded: boolean }): Promise<void> { console.log(`Validate expand or collapse button is displayed=${isDisplayed}`); await this.validateDisplayState(this.expandOrCollapseButton, 'Expand or collapse button', isDisplayed); if (isDisplayed) await expect(this.expandOrCollapseButton, 'Expand or collapse button label').toHaveText(isExpanded ? await Strings.CLOSE.name : await Strings.OPEN.name); }
  /**
   * Validate the max results notification is displayed.
   * @param isDisplayed Whether the notification should be displayed.
   */
  async validateMaxResultsNotificationIsDisplayed(isDisplayed = true): Promise<void> { console.log(`Validate the max results notification is displayed=${isDisplayed}`); await this.validateDisplayState(this.maxResultsNotificationLabel, 'Max results notification label', isDisplayed); if (isDisplayed) await expect(this.maxResultsNotificationLabel, 'Max results notification label text').toHaveText(await Strings.MAX_AMOUNT_OF_RESULTS.name); }
}