import { type Page, type Locator } from '@playwright/test';
import { BookingHistoryTableDataComponent } from './bookingHistoryTableData.component';

/**
 * The booking table section on the PI Booking History page containing the UI elements, custom
 * actions and validations. Mirrors qa/reference
 * `components/opera/bookingHistory/bookingHistoryTable.js` (extends
 * `components/common/bookingHistory/bookingHistoryTableBase.js`; simplified: date/status
 * extraction uses raw `innerText()` rather than reference's `moment`-based date parsing -
 * `qa/src/pages/pi/bookingHistory.page.ts` already owns the page's own date-parsing helpers).
 */
export class BookingHistoryTableComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly rows: Locator = this.page.locator('tbody tr[data-testid="MyDashboard-Table-Row"]');

  /** The table-data component for the row at the given 0-based index. */
  getBookingHistoryTableDataByIndex(index: number): BookingHistoryTableDataComponent {
    return new BookingHistoryTableDataComponent(this.rows.nth(index));
  }

  // ######## UI actions/navigation ########

  /** Extract {date, status} text info for every displayed booking row. */
  async extractTableDataInfo(): Promise<Array<{ dateInfo: string; statusInfo: string }>> {
    const count = await this.rows.count();
    const tableInfo: Array<{ dateInfo: string; statusInfo: string }> = [];
    for (let index = 0; index < count; index++) {
      const rowData = this.getBookingHistoryTableDataByIndex(index);
      tableInfo.push({
        dateInfo: (await rowData.dateLabel.innerText()).trim(),
        statusInfo: (await rowData.statusLabel.innerText()).trim(),
      });
    }
    return tableInfo;
  }

  // ######## UI validations ########
}
