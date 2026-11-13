import { type Locator } from "@playwright/test";
import moment from "moment";
import { MenuContainerComponent } from "../../components/pib";
import { BookingHistoryBasePageComponent } from "../../components/shared/bookingHistory/bookingHistoryBasePage.component";
import { BasePibPage } from "./basePib.page";

/** IB booking history page */
export class BookingHistoryPage extends BasePibPage {
  // ######## UI elements/properties ########
  readonly bookingsDatePicker: Locator = this.page.locator( '//input[@data-testid="SingleDatePicker"]', );
  readonly bookingsCalendarNextButton: Locator = this.page.locator( ".react-datepicker__navigation--next", );
  readonly bookingsCalendarPrevButton: Locator = this.page.locator( ".react-datepicker__navigation--previous", );
  readonly bookingsCalendarCurrentMonthLabel: Locator = this.page.locator( ".react-datepicker__current-month", );
  readonly bookingHistoryBasePage = new BookingHistoryBasePageComponent();
  readonly menuContainer = new MenuContainerComponent();
  // ######## UI actions/navigation ########
  /** Click Date picker field. */
  async clickDatePickerInputField(): Promise<void> {
    console.log("Click booking date picker field");
    await this.bookingsDatePicker.click();
  }
  /** Click a certain day in the calendar. */
  async clickCalendarDay(day: number | string): Promise<void> {
    console.log(`Click calendar day: ${day}`);
    const paddedDay = String(day).padStart(3, "0");
    await this.page
      .locator(
        `//div[contains(@class, "react-datepicker__day--${paddedDay}") and not(contains(@class, "--outside-month"))]`,
      )
      .click();
  }
  /** Navigate to a specific month and year in the date picker. */
  async navigateToTargetMonthYear(
    monthName: string,
    year: string | number,
  ): Promise<void> {
    console.log(`Navigate booking calendar to ${monthName} ${year}`);
    const target = moment(`${monthName} ${year}`, "MMMM YYYY");
    while (
      !moment(
        await this.bookingsCalendarCurrentMonthLabel.textContent(),
        "MMMM YYYY",
      ).isSame(target, "month")
    ) {
      const current = moment(
        await this.bookingsCalendarCurrentMonthLabel.textContent(),
        "MMMM YYYY",
      );
      await (
        current.isBefore(target, "month")
          ? this.bookingsCalendarNextButton
          : this.bookingsCalendarPrevButton
      ).click();
    }
  }
  // ######## UI validations ########
}
