import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
/** Single date calendar section (Spending and reporting > MI reports/Out of policy report page) */
export class SingleDateCalendarSectionComponent {
  private readonly page: Page = global.page;
  static readonly DATE_INPUT =
    'div[data-testid="IB-Single-Date-Picker-Wrapper"] button[data-testid="IB-Date-Picker-Input"]';
  // ######## UI elements/properties ########
  readonly dateInput: Locator = this.page.locator( SingleDateCalendarSectionComponent.DATE_INPUT, );
  readonly startDateLabel: Locator = this.page .locator('div[data-testid="IB-Single-Date-Picker-Wrapper"] button') .first();
  readonly endDateLabel: Locator = this.page .locator('div[data-testid="IB-Single-Date-Picker-Wrapper"] button') .nth(1);
  readonly datePickerErrorLabel: Locator = this.page.locator( 'div[data-testid="IB-Date-Picker-ErrorTooltip"] span', );
  // ######## UI actions/navigation ########
  /** Open Start date calendar. */
  async openStartDateCalendar(): Promise<void> {
    console.log("Open Start date calendar");
    await this.startDateLabel.click();
  }
  /** Open End date calendar. */
  async openEndDateCalendar(): Promise<void> {
    console.log("Open End date calendar");
    await this.endDateLabel.click();
  }
  /** Navigate to a calendar date. */
  async goToDate({ selectedDate }: { selectedDate: string }): Promise<void> {
    console.log("Navigate to calendar date");
    await this.page
      .getByRole("option", { name: selectedDate })
      .scrollIntoViewIfNeeded();
  }
  /** Select a calendar date. */
  async selectDate({ selectedDate }: { selectedDate: string }): Promise<void> {
    console.log("Select calendar date");
    await this.page.getByRole("option", { name: selectedDate }).click();
  }
  /** Click Done. */
  async clickDoneButtonFromCalendar(): Promise<void> {
    console.log("Click calendar Done");
    await this.page.getByRole("button", { name: "Done" }).click();
  }
  /** Click Reset. */
  async clickResetButtonFromCalendar(): Promise<void> {
    console.log("Click calendar Reset");
    await this.page.getByRole("button", { name: "Reset" }).click();
  }
  // ######## UI validations ########
  /** Validate start date. */
  async validateStartDateLabel(
    startDateValue: string | null = null,
  ): Promise<void> {
    console.log("Validate start date label");
    await expect(this.startDateLabel, "Start date label").toHaveText( startDateValue ?? (await IbStrings.START_DATE_IB.name), );
  }
  /** Validate end date. */
  async validateEndDateLabel(
    endDateValue: string | null = null,
  ): Promise<void> {
    console.log("Validate end date label");
    await expect(this.endDateLabel, "End date label").toHaveText( endDateValue ?? (await IbStrings.END_DATE_IB.name), );
  }
  /** Validate calendar visibility. */
  async validateCalendarMonthContainerIsOpened(): Promise<void> {
    console.log("Validate calendar month container");
    await expect( this.page.getByRole("dialog"), "Calendar dialog", ).toBeVisible();
  }
  /** Validate calendar month. */
  async validateCalendarCurrentMonthAndYear(): Promise<void> {
    console.log("Validate calendar current month");
    await expect( this.page.getByRole("dialog"), "Calendar dialog", ).toBeVisible();
  }
  /** Validate calendar date state. */
  async validateCalendarDateState({
    date,
    shouldBeClickable = true,
  }: {
    date: string;
    shouldBeClickable?: boolean;
    shouldSkipIfNeeded?: boolean;
  }): Promise<void> {
    console.log("Validate calendar date state");
    const option = this.page.getByRole("option", { name: date });
    if (shouldBeClickable)
      await expect(option, `Calendar date ${date}`).toBeEnabled();
    else await expect(option, `Calendar date ${date}`).toBeDisabled();
  }
  /** Validate date picker error. */
  async validateDatePickerError({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate date picker error");
    if (isDisplayed)
      await expect( this.datePickerErrorLabel, "Date picker error", ).toBeVisible();
    else
      await expect( this.datePickerErrorLabel, "Date picker error", ).not.toBeVisible();
  }
}
