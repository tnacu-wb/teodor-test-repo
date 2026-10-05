import { expect, type Locator } from "@playwright/test";
import {
  ApplicationSavedSectionComponent,
  ApplicationSubmittedSectionComponent,
  CardDetailsSectionComponent,
  DirectDebitIframeSectionComponent,
  HeaderAndFooterComponentComponent,
  PaymentDetailsSectionComponent,
  ResumeApplicationSectionComponent,
  ReviewChangesModalComponent,
  SummarySectionComponent,
  ToastNotificationSectionComponent,
  YourCompanyDetailsSectionComponent,
  YourDetailsSectionComponent,
} from "../../components/pib";
import { Locales } from "../../test-data/locales";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";
/** InnBusiness application > Home > Apply now > Start application > Pay Application page */
export class PayApplicationPage extends BasePibPage {
  readonly url = "business-pay/apply";
  // ######## UI elements/properties ########
  readonly pageContainer: Locator = this.page.getByTestId("wizard-page");
  readonly requiredEmployeesCard: Locator = this.page .locator('img[src*="icon-employees-icon.svg"]') .locator("..");
  readonly innBusinessPayLogo: Locator = this.page.locator( 'div.flex.flex-col img[alt="logo"]', );
  readonly applyForInnBusinessPayTitleLabel: Locator = this.page.locator( "div.flex.flex-col > div.text-secondaryColor", );
  readonly registeredAddressTitleLabel: Locator = this.page.locator( '//div[contains(@class, "flex") and contains(@class, "gap-6")]/div[1]//div[contains(@class, "text-[23px]")]', );
  readonly companyRegistrationDetailsTitleLabel: Locator = this.page.locator( '//div[contains(@class, "flex") and contains(@class, "gap-6")]/div[2]//div[contains(@class, "text-[23px]")]', );
  readonly requiredEmployeesTitleLabel: Locator = this.page.locator( '//div[contains(@class, "flex") and contains(@class, "gap-6")]/div[3]//div[contains(@class, "text-[23px]")]', );
  readonly bankAccountTitleLabel: Locator = this.page.locator( '//div[contains(@class, "flex") and contains(@class, "gap-6")]/div[4]//div[contains(@class, "text-[23px]")]', );
  readonly manageEmployeesLink: Locator = this.page .locator('//a[contains(@href, "/manage/employees")]') .first();
  readonly setupAccountTitleLabel: Locator = this.page.locator( '//div[contains(@class, "flex") and contains(@class, "flex-col") and contains(@class, "gap-2")]/div[1]', );
  readonly setupAccountSubtitleLabel: Locator = this.page.locator( '//div[contains(@class, "flex") and contains(@class, "flex-col") and contains(@class, "gap-2")]/div[2]', );
  readonly setupAccountLink: Locator = this.page.locator( 'div[data-testid="landing-bottom-container"] a', );
  readonly startApplicationButton: Locator = this.page.getByTestId("footer-button");
  readonly requiredEmployeesDescriptionLabel: Locator = this.requiredEmployeesCard.locator("div.font-bold + div");
  readonly resumeApplicationSection = new ResumeApplicationSectionComponent();
  readonly savedApplicationSection = new ApplicationSavedSectionComponent();
  readonly yourDetailsSection = new YourDetailsSectionComponent();
  readonly yourCompanyDetails = new YourCompanyDetailsSectionComponent();
  readonly reviewChanges = new ReviewChangesModalComponent();
  readonly cardDetailsSection = new CardDetailsSectionComponent();
  readonly paymentDetailsSection = new PaymentDetailsSectionComponent();
  readonly directDebitSection = new DirectDebitIframeSectionComponent();
  readonly headerAndFooterComponent = new HeaderAndFooterComponentComponent();
  readonly summarySection = new SummarySectionComponent();
  readonly applicationSubmittedSection =
    new ApplicationSubmittedSectionComponent();
  readonly toastNotification = new ToastNotificationSectionComponent();
  // ######## UI actions/navigation ########
  /** Open IB Pay application page. */
  async open(): Promise<void> {
    console.log("Open IB Pay application page");
    await this.openPath(this.url);
    await this.validatePage();
  }
  /** Click Apply Now button. */
  async clickApplyNowButton(): Promise<void> {
    console.log("Click Apply Now button");
    await this.startApplicationButton.click();
  }
  /** Click start application button. */
  async clickStartApplicationButton(): Promise<void> {
    console.log("Click start application button");
    await this.startApplicationButton.scrollIntoViewIfNeeded();
    await this.startApplicationButton.click();
    await expect( this.applyForInnBusinessPayTitleLabel, "Apply for InnBusiness Pay title is hidden", ).not.toBeVisible();
  }
  /** Click PIBA link. */
  async clickPIBALink(): Promise<void> {
    console.log("Click PIBA link");
    await this.setupAccountLink.click();
  }
  // ######## UI validations ########
  /** Validate URL after click on PIBA link. */
  async validateUrlAfterClickOnPIBALink(
    urlBeforeLanguageSelect: string,
    shouldBeChanged = true,
  ): Promise<void> {
    console.log("Validate URL after language selection");
    await this.clickPIBALink();
    const expectedNewLanguage = urlBeforeLanguageSelect.includes(
      Locales.EN_GB_IB_URL,
    )
      ? Locales.DE_DE_IB_URL
      : Locales.EN_GB_IB_URL;
    const expectedOldLanguage = urlBeforeLanguageSelect.includes(
      Locales.EN_GB_IB_URL,
    )
      ? Locales.EN_GB_IB_URL
      : Locales.DE_DE_IB_URL;
    if (shouldBeChanged) {
      await expect(this.page, "URL contains new language segment").toHaveURL( new RegExp(expectedNewLanguage), );
      expect( this.page.url(), "URL no longer contains old language segment", ).not.toContain(expectedOldLanguage);
    } else
      expect(this.page.url(), "URL stays the same").toBe( urlBeforeLanguageSelect, );
  }
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Pay Application page");
    await this.validatePageMarker(this.pageContainer, "Pay Application");
  }
  /** Validate Application Requirements section is displayed. */
  async validateApplicationRequirementsSection(): Promise<void> {
    console.log("Validate Application Requirements section");
    await expect( this.registeredAddressTitleLabel, "Registered address title", ).toHaveText(await IbStrings.REGISTERED_ADDRESS_OF_YOUR_COMPANY.name);
    await expect( this.companyRegistrationDetailsTitleLabel, "Company registration details title", ).toHaveText(await IbStrings.COMPANY_REGISTRATION_DETAILS.name);
    await expect( this.requiredEmployeesTitleLabel, "Required employees title", ).toHaveText(await IbStrings.REQUIRE_EMPLOYEES_ADDED_TO_YOUR_ACCOUNT.name);
    await expect(this.bankAccountTitleLabel, "Bank account title").toHaveText( await IbStrings.BANK_ACCOUNT_AND_SORT_CODE_FOR_YOUR_COMPANY.name, );
    await expect( this.requiredEmployeesDescriptionLabel, "Required employees description", ).toHaveText( await IbStrings.TO_ASSING_PERMISSION_IN_YOUR_NEW_INNBUSINESS_PAY_ACCOUNT .name, );
    await expect( this.setupAccountTitleLabel, "Set up account title", ).toHaveText( Locales.isEnglishWebsite() ? await IbStrings.LOOKING_TO_SET_UP_GERMAN_INNBUSINESS_PAY_ACCOUNT.name : "Möchten Sie ein britisches InnBusiness Pay-Konto einrichten?", );
  }
}
