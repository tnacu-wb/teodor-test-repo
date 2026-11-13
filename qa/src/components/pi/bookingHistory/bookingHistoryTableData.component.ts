import { type Page, type Locator } from '@playwright/test';

/**
 * One row's data from the PI Booking History table containing the UI elements, custom actions
 * and validations. Mirrors qa/reference
 * `components/opera/bookingHistory/bookingHistoryTableData.js` (extends
 * `components/common/bookingHistory/bookingHistoryTableDataBase.js`).
 */
export class BookingHistoryTableDataComponent {
  private readonly page: Page = global.page;
  private readonly row: Locator;

  constructor(row: Locator) {
    this.row = row;
  }

  // ######## UI elements/properties ########

  get bookedForLabel(): Locator {
    return this.row.locator('td').nth(0);
  }

  get dateLabel(): Locator {
    return this.row.locator('td').nth(1);
  }

  get hotelLabel(): Locator {
    return this.row.locator('td').nth(2);
  }

  get priceLabel(): Locator {
    return this.row.locator('td').nth(3);
  }

  get statusLabel(): Locator {
    return this.row.locator('td').nth(4);
  }

  get viewDropdown(): Locator {
    return this.row.locator('[data-testid*="view"]');
  }

  get closeDropdown(): Locator {
    return this.row.locator('[data-testid*="close"]');
  }
}
