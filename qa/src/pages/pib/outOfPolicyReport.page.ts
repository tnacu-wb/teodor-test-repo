import { expect, type Locator } from "@playwright/test";
import moment from "moment";
import { SingleDateCalendarSectionComponent } from "../../components/pib";
import { Constants } from "../../test-data/constants";
import { Locales } from "../../test-data/locales";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/** InnBusiness application > Spending > Out of policy report page */
export class OutOfPolicyReportPage extends BasePibPage {
  readonly url = "spending/out-of-policy-report";
  // ######## UI elements/properties ########
  readonly headerTitleLabel: Locator = this.page.locator( 'div[data-testid="OutOfPolicyReportPage-title"] h1', );
  readonly headerSubtitleLabel: Locator = this.page.locator( 'div[data-testid="OutOfPolicyReportPage-title"] p', );
  readonly datesTitleLabel: Locator = this.page.locator( 'div[data-testid="OutOfPolicyReportPage-container"] p', );
  readonly datePickerErrorLabel: Locator = this.page.locator( "div.items-start.text-sm span", );
  readonly suggestedDatesTitleLabel: Locator = this.page.locator( 'div[data-testid="OutOfPolicyReportPage-container"] p ~ p', );
  readonly suggestedDateLabels: Locator = this.page.locator( 'div[data-testid="OutOfPolicyReportPage-RadioGroup"] label span', );
  readonly suggestedDateButtons: Locator = this.page.locator( 'div[data-testid="OutOfPolicyReportPage-RadioGroup"] button', );
  readonly downloadReportButton: Locator = this.page.getByTestId( "OutOfPolicyReportPage-Generate-Report", );
  readonly singleDateCalendarSection = new SingleDateCalendarSectionComponent();
  // ######## UI actions/navigation ########
  /** Click date range. */
  async clickDateRange(dateOption: string): Promise<void> {
    console.log(`Click date range=${dateOption}`);
    await expect( this.suggestedDateButtons, "Suggested date buttons", ).toHaveCount(4);
    for (
      let index = 0;
      index < (await this.suggestedDateButtons.count());
      index += 1
    ) {
      if (
        (
          (await this.suggestedDateLabels.nth(index).textContent()) ?? ""
        ).includes(dateOption)
      ) {
        await this.suggestedDateButtons.nth(index).click();
        return;
      }
    }
  }
  /** Click Download report button. */
  async clickDownloadReportButton(): Promise<void> {
    console.log("Click Download report button");
    await this.downloadReportButton.click();
  }
  // ######## UI validations ########
  /** Check we reached the current page by checking page url. */
  async validatePageUrl(): Promise<void> {
    console.log("Validate Out of policy report page URL");
    this.validateUrl(this.url);
  }
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Out of policy report page");
    await this.validatePageMarker(
      this.downloadReportButton,
      "Out of policy report",
    );
  }
  /** Validate header label. */
  async validateHeaderLabel(): Promise<void> {
    console.log("Validate header label");
    await expect(this.headerTitleLabel, "Header title label").toHaveText( await IbStrings.OUT_OF_POLICY_REPORT.name, );
  }
  /** Validate subtitle label. */
  async validateSubtitleLabel(): Promise<void> {
    console.log("Validate subtitle label");
    await expect(this.headerSubtitleLabel, "Header subtitle label").toHaveText( await IbStrings.OUT_OF_POLICY_REPORT_DESCRIPTION.name, );
  }
  /** Validate start date label. */
  async validateStartDateLabel(
    startDateValue: string | null = null,
  ): Promise<void> {
    console.log("Validate start date label");
    await this.singleDateCalendarSection.validateStartDateLabel(startDateValue);
  }
  /** Validate end date label. */
  async validateEndDateLabel(
    endDateValue: string | null = null,
  ): Promise<void> {
    console.log("Validate end date label");
    await this.singleDateCalendarSection.validateEndDateLabel(endDateValue);
  }
  /** Validate reporting dates. */
  async validateReportingDates(): Promise<void> {
    console.log("Validate reporting dates");
    await expect(this.datesTitleLabel, "Dates title label").toHaveText( await IbStrings.OUT_OF_POLICY_REPORT_CHOOSE_REPORTING_DATES.name, );
    await this.singleDateCalendarSection.validateStartDateLabel();
    await this.singleDateCalendarSection.validateEndDateLabel();
  }
  /** Validate suggested reporting dates. */
  async validateSuggestedReportingDates(): Promise<void> {
    console.log("Validate suggested reporting dates");
    const names = [
      await IbStrings.OUT_OF_POLICY_REPORT_YESTERDAY.name,
      await IbStrings.OUT_OF_POLICY_REPORT_LAST_WEEK.name,
      await IbStrings.OUT_OF_POLICY_REPORT_LAST_MONTH.name,
      await IbStrings.OUT_OF_POLICY_REPORT_LAST_YEAR.name,
    ];
    const format = Locales.isEnglishWebsite()
      ? Constants.DAY_SHORT_DATE_FORMAT
      : Constants.DAY_SHORT_DATE_POINT_FORMAT;
    const yesterday = moment().subtract(1, "day");
    const starts = [
      yesterday,
      moment(yesterday).subtract(7, "day"),
      moment(yesterday).subtract(1, "month"),
      moment(yesterday).subtract(1, "year"),
    ];
    await expect( this.suggestedDatesTitleLabel, "Suggested dates title label", ).toHaveText(await IbStrings.OUT_OF_POLICY_REPORT_SELECT_DATES_BELOW.name);
    await expect( this.suggestedDateButtons, "Suggested date button count", ).toHaveCount(4);
    for (let index = 0; index < names.length; index += 1) {
      const end =
        index === 0
          ? ""
          : ` - ${yesterday.format(format).replace("Sep", "Sept")}`;
      const label = `${names[index]} - (${starts[index].format(format).replace("Sep", "Sept")}${end})`;
      await expect( this.suggestedDateLabels.nth(index), `Suggested date title ${index + 1} label`, ).toHaveText(label);
      await expect( this.suggestedDateButtons.nth(index), `Radio button ${index + 1}`, ).toHaveAttribute("data-state", "unchecked");
    }
  }
  /** Validate date range checkboxes. */
  async validateDateRangeCheckboxes(
    dateOption: string,
  ): Promise<void> {
    console.log("Validate date range checkboxes");
    for (
      let index = 0;
      index < (await this.suggestedDateButtons.count());
      index += 1
    ) {
      const selected = (
        (await this.suggestedDateLabels.nth(index).textContent()) ?? ""
      ).includes(dateOption);
      await expect( this.suggestedDateButtons.nth(index), `Radio button ${index + 1} state`, ).toHaveAttribute("data-state", selected ? "checked" : "unchecked");
    }
  }
  /** Validate date picker error. */
  async validateDatePickerError({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate date picker error");
    if (isDisplayed)
      await expect( this.datePickerErrorLabel, "Date picker error label", ).toHaveText(await IbStrings.SELECT_THE_START_AND_END_DATE.name);
    else
      await expect( this.datePickerErrorLabel, "Date picker error label", ).not.toBeVisible();
  }
  /** Validate Download report button. */
  async validateDownloadReportButton(): Promise<void> {
    console.log("Validate Download report button");
    await expect( this.downloadReportButton, "Download report button", ).toHaveText(await IbStrings.OUT_OF_POLICY_REPORT_DOWNLOAD_REPORT.name);
  }
}
