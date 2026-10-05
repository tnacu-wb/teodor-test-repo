import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { CcuiComponent } from '../baseCcui.component';

/** The Search Booking Table Row containing the UI elements, custom actions and validations. */
export class SearchBookingTableRowComponent extends CcuiComponent {
  /**
   * Create a search-booking table-row component.
   * @param rowIndex Zero-based table-row index.
   */
  constructor(private readonly rowIndex: number) { super(); }

  // ######## UI elements/properties ########

  /** Return the booking row locator. */
  get bookingRow(): Locator { return this.page.locator(`tr[data-testid="SearchBookingsPage-Table-Row-${this.rowIndex}"]`); }
  /** Return the booked-for cell locator. */
  get bookedForLabel(): Locator { return this.bookingRow.locator('td[data-testid^="SearchBookingsPage-Table-Cell-BookedFor"]'); }
  /** Return the booked-by cell locator. */
  get bookedByLabel(): Locator { return this.bookingRow.locator('td[data-testid^="SearchBookingsPage-Table-Cell-BookedBy"]'); }
  /** Return the hotel cell locator. */
  get hotelLabel(): Locator { return this.bookingRow.locator('td[data-testid^="SearchBookingsPage-Table-Cell-Hotel"]'); }
  /** Return the date cell locator. */
  get dateLabel(): Locator { return this.bookingRow.locator('td[data-testid^="SearchBookingsPage-Table-Cell-Date"]'); }
  /** Return the price cell locator. */
  get priceLabel(): Locator { return this.bookingRow.locator('td[data-testid^="SearchBookingsPage-Table-Cell-Price"]'); }
  /** Return the status cell locator. */
  get statusLabel(): Locator { return this.bookingRow.locator('td[data-testid^="SearchBookingsPage-Table-Cell-Status"]'); }
  /** Return the PMS cell locator. */
  get pmsLabel(): Locator { return this.bookingRow.locator('td[data-testid^="SearchBookingsPage-Table-Cell-SourcePms"]'); }
  /** Return the expand/collapse link locator. */
  get expandLink(): Locator { return this.bookingRow.locator('a[data-testid="hdp_basketHideBreakdownLink"]'); }

  // ######## UI actions/navigation ########
  // ######## UI validations ########

  /**
   * Validate booked for.
   * @param bookedFor Expected booked-for value.
   */
  async validateBookedFor(bookedFor: string): Promise<void> { console.log(`Validate booked for=${bookedFor}`); await expect(this.bookedForLabel, 'Booked for').toBeVisible(); await expect(this.bookedForLabel, `Booking number=${this.rowIndex} should have ${bookedFor} label`).toHaveText(bookedFor); }
  /**
   * Validate booked by.
   * @param bookedBy Expected booked-by value.
   */
  async validateBookedBy(bookedBy: string): Promise<void> { console.log(`Validate booked by=${bookedBy}`); await expect(this.bookedByLabel, 'Booked by').toBeVisible(); await expect(this.bookedByLabel, `Booking number=${this.rowIndex} should have ${bookedBy} label`).toHaveText(bookedBy); }
  /**
   * Validate hotel name.
   * @param hotelName Expected hotel name.
   */
  async validateHotel(hotelName: string): Promise<void> { console.log(`Validate hotel name=${hotelName}`); await expect(this.hotelLabel, 'Hotel name').toBeVisible(); await expect(this.hotelLabel, `Booking number=${this.rowIndex} should have ${hotelName} label`).toHaveText(hotelName); }
  /**
   * Validate date.
   * @param date Expected booking date.
   */
  async validateDate(date: string): Promise<void> { console.log(`Validate date=${date}`); await expect(this.dateLabel, 'Date').toBeVisible(); await expect(this.dateLabel, `Booking number=${this.rowIndex} should have ${date} label`).toHaveText(date); }
  /**
   * Validate status.
   * @param status Expected booking status.
   */
  async validateStatus(status: string | Promise<string>): Promise<void> { const resolvedStatus = await status; console.log(`Validate status=${resolvedStatus}`); await expect(this.statusLabel, 'Status').toBeVisible(); await expect(this.statusLabel, `Booking number=${this.rowIndex} should have ${resolvedStatus} label`).toHaveText(resolvedStatus); }
  /**
   * Validate PMS.
   * @param pmsValue Expected PMS value.
   */
  async validatePms(pmsValue: string): Promise<void> { console.log(`Validate PMS=${pmsValue}`); await expect(this.pmsLabel, 'PMS').toBeVisible(); await expect(this.pmsLabel, `Booking number=${this.rowIndex} should have ${pmsValue} label`).toHaveText(pmsValue); }
  /**
   * Validate expand link.
   * @param isExpanded Whether the row should be expanded.
   */
  async validateExpandLink(isExpanded: boolean): Promise<void> { console.log(`Validate expanded link=${isExpanded}`); const expandLinkLabel = isExpanded ? await Strings.CLOSE.name : await Strings.OPEN.name; await expect(this.expandLink, 'Expand link').toBeVisible(); await expect(this.expandLink, `Booking number=${this.rowIndex} should have ${expandLinkLabel} label`).toHaveText(expandLinkLabel); }
  /**
   * Validate all expected values in the booking row.
   * @param bookedFor Expected booked-for value.
   * @param bookedBy Expected booked-by value.
   * @param hotelName Expected hotel name.
   * @param date Expected booking date.
   * @param status Expected booking status.
   * @param pmsValue Expected PMS value.
   * @param isExpanded Whether the row should be expanded.
   */
  async validateBookingRow({ bookedFor, bookedBy, hotelName, date, status, pmsValue, isExpanded }: { bookedFor: string; bookedBy: string; hotelName: string; date: string; status: string | Promise<string>; pmsValue: string; isExpanded: boolean }): Promise<void> { console.log('Validate booking row'); await this.validateBookedFor(bookedFor); await this.validateBookedBy(bookedBy); await this.validateHotel(hotelName); await this.validateDate(date); await this.validateStatus(status); await this.validatePms(pmsValue); await this.validateExpandLink(isExpanded); }
}