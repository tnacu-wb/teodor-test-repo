import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { SearchConsoleComponent } from "../searchConsole/searchConsole.component";

/**
 * Header section on IB
 */
export class HeaderSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########
  readonly innBusinessLogoLink: Locator = this.page.getByTestId("IB-Logo");
  readonly innBusinessLogoImage: Locator = this.innBusinessLogoLink.locator("img");
  readonly companyNameLabel: Locator = this.page.getByTestId("Company-Name");
  readonly languageSwitcherImageButton: Locator = this.page .getByTestId("Language-Switcher-Button") .locator("img");
  readonly languageDropdown: Locator = this.page.getByTestId( "Language-Switcher-Dropdown", );
  readonly languageSwitcherEnglishButton: Locator = this.page.getByTestId("English-Button");
  readonly languageSwitcherGermanButton: Locator = this.page.getByTestId("Deutsch-Button");
  readonly profileButton: Locator = this.page.getByTestId( "Account-Menu-Button", );
  readonly profileDropdown: Locator = this.page.getByTestId( "Account-Menu-Dropdown", );
  readonly linkAccountButton: Locator = this.page.getByTestId( "link-account-button", );
  readonly myProfileLink: Locator = this.page.getByTestId( "Account-Profile-Link", );
  readonly logOutLink: Locator = this.page.getByTestId("Account-Logout-Link");
  readonly guestAndPaymentFlowContainer: Locator = this.page.getByTestId("BusinessSteps");
  readonly guestDetailsStepLabel: Locator = this.guestAndPaymentFlowContainer .locator("> div > div") .nth(0);
  readonly paymentStepLabel: Locator = this.guestAndPaymentFlowContainer .locator("> div > div") .nth(2);
  readonly searchConsole: SearchConsoleComponent = new SearchConsoleComponent();

  // ######## UI actions/navigation ########
  /** Click profile button. */
  async clickProfileButton(): Promise<void> {
    console.log("Click profile button");
    await this.profileButton.click();
    await expect( this.myProfileLink, "My Profile link after profile menu opens", ).toBeVisible();
  }
  /** Click outside profile dropdown. */
  async clickOutsideProfileDropdown(): Promise<void> {
    console.log("Click outside profile dropdown");
    await this.profileDropdown.click({ position: { x: 0, y: 0 } });
    await expect( this.profileDropdown, "Profile dropdown after outside click", ).not.toBeVisible();
  }
  /** Click Logout link. */
  async clickLogoutLink(): Promise<void> {
    console.log("Click Logout link");
    await this.logOutLink.click();
    await expect( this.logOutLink, "Log out link after logout", ).not.toBeVisible();
  }
  /** Click language switcher button. */
  async clickLanguageSwitcherButton(): Promise<void> {
    console.log("Click language switcher button");
    await this.languageSwitcherImageButton.click();
    await expect(this.languageDropdown, "Language dropdown").toBeVisible();
  }
  /** Click on language option. */
  async clickOnLanguageOption({
    language,
  }: {
    language: string;
  }): Promise<void> {
    console.log(`Change the language to ${language}`);
    const englishOption = this.languageSwitcherEnglishButton;
    await (
      (await englishOption.textContent())?.trim() === language
        ? englishOption
        : this.languageSwitcherGermanButton
    ).click();
    await expect( this.languageDropdown, "Language dropdown after language selection", ).not.toBeVisible();
  }
  /** Await. */
  async clickLinkAccountButton(): Promise<void> {
    console.log("Click link account button");
    await this.linkAccountButton.click();
    await expect( this.linkAccountButton, "Link account button after click", ).not.toBeVisible();
  }
  /** Click Link account button. */
  async clickMyProfileLink(): Promise<void> {
    console.log("Click my profile link");
    await this.myProfileLink.click();
  }
  /** Click my profile link. */
  async clickLogo(): Promise<void> {
    console.log("Click logo");
    await this.innBusinessLogoLink.click();
  }
  /** Click logo. */
  async clickUserProfileAndLogout(): Promise<void> {
    console.log("Click user profile and logout");
    await this.clickProfileButton();
    await this.clickLogoutLink();
  }

  // ######## UI validations ########
  /** Validate Inn Business logo. */
  async validateInnBusinessLogo(): Promise<void> {
    console.log("Validate Inn Business logo");
    await expect(this.innBusinessLogoLink, "Inn Business logo").toBeVisible();
    await expect( this.innBusinessLogoImage, "Inn Business logo image", ).toBeVisible();
  }
  /** Validate company name. */
  async validateCompanyName(companyName: string): Promise<void> {
    console.log("Validate company name");
    await expect(this.companyNameLabel, "Company name").toHaveText(companyName);
  }
  /** Validate profile button. */
  async validateProfileButton({
    firstName,
    lastName,
  }: {
    firstName: string;
    lastName: string;
  }): Promise<void> {
    console.log("Validate profile button");
    await expect(this.profileButton, "Profile button initials").toHaveText( `${firstName[0] ?? ""}${lastName[0] ?? ""}`, );
  }
  /** Validate profile button. */
  async validateProfileLinks(): Promise<void> {
    console.log("Validate profile links");
    await expect(this.myProfileLink, "My Profile link").toHaveText( await IbStrings.MY_PROFILE_IB.name, );
    await expect(this.logOutLink, "Log out link").toHaveText( await IbStrings.LOG_OUT_IB.name, );
  }
  /** Validate profile links. */
  async validateInnBusinessHeader(): Promise<void> {
    console.log("Validate Inn Business header");
    await this.validateInnBusinessLogo();
    await expect( this.searchConsole.searchBarContainer, "Search Bar container", ).toBeVisible();
    await expect(this.companyNameLabel, "Company name").toBeVisible();
    await expect(this.profileButton, "Profile button").toBeVisible();
  }
}
