import { expect, type Locator } from "@playwright/test";
import {
  FooterSectionComponent,
  SingleDateCalendarSectionComponent,
} from "../../components/pib";
import { Locales } from "../../test-data/locales";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/** InnBusiness application > Spending > Manage Information Report */
export class ManagementInformationReportPage extends BasePibPage {
  readonly url = "spending/management-information-report";
  static readonly DATE_INPUT =
    'div[data-testid="IB-Single-Date-Picker-Wrapper"] button[data-testid="IB-Date-Picker-Input"]';
  // ######## UI elements/properties ########
  readonly headerLabel: Locator = this.page.locator( 'div[data-testid="ManagementInformationReportPage-title"] h1', );
  readonly subtitleLabel: Locator = this.page.locator( 'div[data-testid="ManagementInformationReportPage-container"] p', );
  readonly includeEmployeeQuestionsCheckbox: Locator = this.page.locator( "button#includeEmployeeQuestions", );
  readonly includeEmployeeQuestionsCheckboxLabel: Locator = this.includeEmployeeQuestionsCheckbox.locator("~ label");
  readonly manageEmployeeQuestionsLink: Locator = this.page.locator("a.inline-block");
  readonly generateReportButton: Locator = this.page.getByTestId("IB-Generate-Report");
  readonly noBookingsErrorLabel: Locator = this.page.locator( "div.bg-tooltipError span", );
  readonly footerSection = new FooterSectionComponent();
  readonly singleDateCalendarSection = new SingleDateCalendarSectionComponent();
  // ######## UI actions/navigation ########
  /** Open IB Manage Information page. */
  async open(): Promise<void> {
    console.log("Open IB Manage Information page");
    await this.openPath(this.url);
    await this.validatePage();
  }
  /** Click Generate report button. */
  async clickGenerateReportButton(): Promise<void> {
    console.log("Click Generate report button");
    await this.generateReportButton.click();
  }
  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Management Information Report page");
    await this.validatePageMarker(
      this.headerLabel,
      "Management Information Report",
    );
  }
  /** Validate header label. */
  async validateHeaderLabel(): Promise<void> {
    console.log("Validate header label");
    await expect(this.headerLabel, "Header label").toHaveText( await IbStrings.MANAGEMENT_INFORMATION_PAGE.name, );
  }
  /** Validate subtitle label. */
  async validateSubtitleLabel(): Promise<void> {
    console.log("Validate subtitle label");
    await expect(this.subtitleLabel, "Subtitle label").toHaveText( await IbStrings.CHOOSE_REPORTING_DATES.name, );
  }
  /** Validate include employee questions section. */
  async validateIncludeEmployeeQuestionsSection({
    isCheckboxChecked,
  }: {
    isCheckboxChecked: boolean;
  }): Promise<void> {
    console.log("Validate include employee questions section");
    await expect( this.includeEmployeeQuestionsCheckbox, "Include employee questions checkbox", ).toBeVisible();
    await expect( this.includeEmployeeQuestionsCheckbox, "Include employee questions checked/unchecked status", ).toHaveAttribute( "data-state", isCheckboxChecked ? "checked" : "unchecked", );
    await expect( this.includeEmployeeQuestionsCheckboxLabel, "Include employee questions label", ).toHaveText(await IbStrings.INCLUDE_EMPLOYEE_QUESTIONS.name);
    await expect( this.manageEmployeeQuestionsLink, "Manage employee questions label", ).toHaveText(await IbStrings.MANAGE_EMPLOYEE_QUESTIONS_IB.name);
    await expect( this.manageEmployeeQuestionsLink, "Manage employee questions link href attribute", ).toHaveAttribute( "href", new RegExp(`${Locales.getIbUrlName()}/manage/questions`), );
  }
  /** Validate generate report button. */
  async validateGenerateReportButton(): Promise<void> {
    console.log("Validate generate report button");
    await expect( this.generateReportButton, "Generate report button", ).toHaveText(await IbStrings.GENERATE_REPORT_IB.name);
  }
  /** Validate no bookings error. */
  async validateNoBookingsError({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate no bookings error");
    if (isDisplayed) {
      await expect( this.noBookingsErrorLabel, "No bookings error label", ).toHaveText(await IbStrings.YOU_HAVE_NO_BOOKINGS.name);
    } else
      await expect( this.noBookingsErrorLabel, "No bookings error label", ).not.toBeVisible();
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
}
