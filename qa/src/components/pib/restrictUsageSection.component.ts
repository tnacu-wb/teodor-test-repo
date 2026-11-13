import { expect, type Locator, type Page } from "@playwright/test";
import { CalendarComponent } from "../../components/shared/calendar.component";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { Strings } from "../../test-data/strings";
/** InnBusiness application > Card management > Restrict usage section */
export class RestrictUsageSectionComponent {
  private readonly page: Page = global.page;
  private readonly calendar = new CalendarComponent();
  static readonly DATE_INPUT =
    'div[data-testid="IB-Single-Date-Picker-Wrapper"] button[data-testid="IB-Date-Picker-Input"]';
  // ######## UI elements/properties ########
  readonly restrictCardUsageCheckbox: Locator = this.page.getByTestId( "Date-Restriction-Form-Checkbox", );
  readonly restrictCardUsageLabel: Locator = this.restrictCardUsageCheckbox.locator("xpath=following-sibling::label");
  readonly restrictCardUsageDescriptionLabel: Locator = this.restrictCardUsageCheckbox.locator( "xpath=../../following-sibling::span", );
  readonly restrictUsageInfoTooltipLabel: Locator = this.restrictCardUsageCheckbox.locator( "xpath=../../following-sibling::div/div[1]//span", );
  readonly dateInput: Locator = this.page.locator( RestrictUsageSectionComponent.DATE_INPUT, );
  readonly startDateLabel: Locator = this.dateInput .locator('div[data-testid="IB-Date-Picker-Text"] span') .first();
  readonly endDateLabel: Locator = this.dateInput .locator('div[data-testid="IB-Date-Picker-Text"] span') .nth(1);
  readonly startDateDatePickerErrorLabel: Locator = this.page.getByTestId( "Start-Date-Picker-Error-Tooltip", );
  readonly endDateDatePickerErrorLabel: Locator = this.page.getByTestId( "End-Date-Picker-Error-Tooltip", );
  // ######## UI actions/navigation ########
  /** Click Restrict usage checkbox. */
  async clickRestrictUsageCheckbox(): Promise<void> {
    console.log("Click Restrict usage checkbox");
    await this.restrictCardUsageCheckbox.click();
  }
  /** Open Start date calendar. */
  async openStartDateCalendar(): Promise<void> {
    console.log("Open Start date calendar");
    await this.startDateLabel.click();
    await this.calendar.currentMonthLabel.waitFor({ state: "visible" });
  }
  /** Open End date calendar. */
  async openEndDateCalendar(): Promise<void> {
    console.log("Open End date calendar");
    await this.endDateLabel.click();
    await this.calendar.currentMonthLabel.waitFor({ state: "visible" });
  }
  /** Navigate to a date on calendar. */
  async goToDate({
    selectedDate,
  }: {
    selectedDate: Date;
  }): Promise<void> {
    console.log("Navigate to a date on calendar");
    await this.calendar.goToDate(selectedDate);
  }
  /** Select the selected date from the calendar. */
  async selectDate({
    selectedDate,
  }: {
    selectedDate: Date;
  }): Promise<void> {
    console.log("Select the selected date from the calendar");
    await this.calendar.selectDate(selectedDate);
  }
  /** Set the calendar restrict usage dates by selecting start and end dates. */
  async setCalendarRestrictUsageDates({
    startDate,
    endDate,
  }: {
    startDate: Date;
    endDate: Date;
  }): Promise<void> {
    console.log("Set calendar restrict usage dates");
    await this.clickRestrictUsageCheckbox();
    await this.openStartDateCalendar();
    await this.goToDate({ selectedDate: startDate });
    await this.selectDate({ selectedDate: startDate });
    await this.clickDoneButtonFromCalendar();
    await this.openEndDateCalendar();
    await this.goToDate({ selectedDate: endDate });
    await this.selectDate({ selectedDate: endDate });
    await this.clickDoneButtonFromCalendar();
  }
  /** Click Done button after date is selected. */
  async clickDoneButtonFromCalendar(): Promise<void> {
    console.log("Click Done button");
    await this.page.getByRole("button", { name: "Done" }).click();
  }
  /** Click Reset button. */
  async clickResetButtonFromCalendar(): Promise<void> {
    console.log("Click Reset button");
    await this.page.getByRole("button", { name: "Reset" }).click();
  }
  // ######## UI validations ########
  /** Validate restrict usage section. */
  async validateRestrictUsageSection({
    isDisplayed = true,
  }: {
    isDisplayed?: boolean;
    isSectionExpanded?: boolean;
  } = {}): Promise<void> {
    console.log("Validate restrict usage section");
    if (isDisplayed)
      await expect( this.restrictCardUsageCheckbox, "Restrict usage checkbox", ).toBeVisible();
    else
      await expect( this.restrictCardUsageCheckbox, "Restrict usage checkbox", ).not.toBeVisible();
    if (isDisplayed) {
      await expect(this.restrictCardUsageLabel, "Restrict usage label").toContainText(await IbStrings.RESTRICT_USAGE.name);
      await expect(this.restrictCardUsageDescriptionLabel, "Restrict usage description").toContainText(await IbStrings.RESTRICT_USE_BETWEEN_DATES.name);
    }
  }
  /** Validate start date label. */
  async validateStartDateLabel(
    startDateValue: string | null = null,
  ): Promise<void> {
    console.log("Validate start date label");
    await expect(this.startDateLabel, "Start date label").toHaveText( startDateValue ?? /.+/, );
  }
  /** Validate end date label. */
  async validateEndDateLabel(
    endDateValue: string | null = null,
  ): Promise<void> {
    console.log("Validate end date label");
    await expect(this.endDateLabel, "End date label").toHaveText( endDateValue ?? /.+/, );
  }
  /** Validate start date date picker error. */
  async validateStartDateDatePickerError({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate start date date picker error");
    if (isDisplayed)
      await expect( this.startDateDatePickerErrorLabel, "Start date error", ).toBeVisible();
    else
      await expect( this.startDateDatePickerErrorLabel, "Start date error", ).not.toBeVisible();
    if (isDisplayed)
      await expect(this.startDateDatePickerErrorLabel, "Start date error text").toContainText(await Strings.PLEASE_ENTER_A_VALID_DATE.name);
  }
  /** Validate end date date picker error. */
  async validateEndDateDatePickerError({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate end date date picker error");
    if (isDisplayed)
      await expect( this.endDateDatePickerErrorLabel, "End date error", ).toBeVisible();
    else
      await expect( this.endDateDatePickerErrorLabel, "End date error", ).not.toBeVisible();
    if (isDisplayed)
      await expect(this.endDateDatePickerErrorLabel, "End date error text").toContainText(await Strings.PLEASE_ENTER_A_VALID_DATE.name);
  }
}
