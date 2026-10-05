import { expect, type Locator } from "@playwright/test";
import { ToastNotificationSectionComponent } from "../../components/pib";
import { Locales } from "../../test-data/locales";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { Strings } from "../../test-data/strings";
import { BasePibPage } from "./basePib.page";
export interface ContactPreferenceSetting {
  smsTypeId: number;
  isSmsSelected: boolean;
}
export interface ContactPreferences {
  details: {
    showSmsStopsToCardholder: boolean;
    sendCardsToCardholder: boolean;
  };
  settings: ContactPreferenceSetting[];
}
export interface ContactAccountHolder {
  accountName: string;
  accountNumber: string;
  registrationRoles: string[];
}
/** Page object for Contact Centre page */
export class ContactCentrePage extends BasePibPage {
  readonly url = "profile/contact-preferences";
  // ######## UI elements/properties ########
  readonly root: Locator = this.page.getByTestId("ContactPreferences");
  readonly backToYourProfileButton: Locator = this.page .getByTestId("ContactPreferences-BackButton") .locator("span");
  readonly contactCenterTitleLabel: Locator = this.root .locator("div") .nth(1) .locator("h1");
  readonly contactCenterSubtitleLabel: Locator = this.root .locator("div") .nth(1) .locator("p");
  readonly contactCenterDescriptionLabel: Locator = this.root .locator("div") .nth(2);
  readonly emailContactTitleLabel: Locator = this.root.locator("h2").first();
  readonly premierInnHotelsCheckbox: Locator = this.page.getByTestId( "ContactPreferences-PremierInnCheckbox", );
  readonly whitbreadRestaurantsCheckbox: Locator = this.page.getByTestId( "ContactPreferences-RestaurantsCheckbox", );
  readonly thirdPartyVendorsCheckbox: Locator = this.page.getByTestId( "ContactPreferences-ThirdPartyCheckbox", );
  readonly saveChangesButton: Locator = this.page.getByTestId( "ContactPreferences-SubmitButton", );
  readonly cancelChangesButton: Locator = this.page.getByTestId( "ContactPreferences-CancelButton", );
  readonly ibPayPreferencesSection: Locator = this.page.getByTestId( "InnBusinessPayPreferences", );
  readonly ibPayPreferencesCompanyNameLabel: Locator = this.ibPayPreferencesSection.locator("h2");
  readonly ibPayPreferencesAccountNumberLabel: Locator = this.page.getByTestId( "InnBusinessPayPreferences-AccountNumber", );
  readonly ibPayPreferencesAccountLabels: Locator = this.page.locator( '[data-testid="InnBusinessPayPreferences-AccountLabels"] span', );
  readonly toastNotificationSection = new ToastNotificationSectionComponent();
  // ######## UI actions/navigation ########
  /** Open IB contact preferences page. */
  async open(): Promise<void> {
    console.log("Open Contact Centre page");
    await this.openPath(this.url);
  }
  /** Click back to your profile button. */
  async clickBackToYourProfileButton(): Promise<void> {
    console.log("Click Back to your profile button");
    await this.backToYourProfileButton.scrollIntoViewIfNeeded();
    await this.backToYourProfileButton.click();
  }
  /** Click PremierInn hotels checkbox. */
  async clickPremierInnHotelsCheckbox(): Promise<void> {
    console.log("Click PremierInn Hotels checkbox");
    await this.premierInnHotelsCheckbox.scrollIntoViewIfNeeded();
    await this.premierInnHotelsCheckbox.click();
  }
  /** Click Whitbread restaurants checkbox. */
  async clickWhitbreadRestaurantsCheckbox(): Promise<void> {
    console.log("Click Whitbread Restaurants checkbox");
    await this.whitbreadRestaurantsCheckbox.scrollIntoViewIfNeeded();
    await this.whitbreadRestaurantsCheckbox.click();
  }
  /** Click third party vendors checkbox. */
  async clickThirdPartyVendorsCheckbox(): Promise<void> {
    console.log("Click third party vendors checkbox");
    await this.thirdPartyVendorsCheckbox.scrollIntoViewIfNeeded();
    await this.thirdPartyVendorsCheckbox.click();
  }
  /** Click Save Changes button. */
  async clickSaveChangesButton(): Promise<void> {
    console.log("Click Save Changes button");
    await this.saveChangesButton.scrollIntoViewIfNeeded();
    await this.saveChangesButton.click();
  }
  /** Click Cancel Changes button. */
  async clickCancelChangesButton(): Promise<void> {
    console.log("Click Cancel Changes button");
    await this.cancelChangesButton.scrollIntoViewIfNeeded();
    await this.cancelChangesButton.click();
  }
  // ######## UI validations ########
  /** Validate Contact centre page content. */
  async validateContactCentrePage(): Promise<void> {
    console.log("Validate Contact centre page");
    await expect( this.backToYourProfileButton, "Back to your profile button", ).toHaveText(await IbStrings.BACK_TO_YOUR_PROFILE.name);
    await expect( this.contactCenterTitleLabel, "Contact centre title", ).toHaveText(await IbStrings.CONTACT_CENTRE.name);
    await expect( this.contactCenterSubtitleLabel, "Contact centre subtitle", ).toHaveText(await IbStrings.MANAGE_YOUR_CONTACT_SETTINGS.name);
    await expect(this.emailContactTitleLabel, "Email contact title").toHaveText( await IbStrings.EMAIL_CONTACT_PREFERENCES.name, );
    await expect( this.premierInnHotelsCheckbox, "Premier Inn hotels checkbox", ).toBeVisible();
    await expect(this.saveChangesButton, "Save changes button").toHaveText( await Strings.SAVE_CHANGES.name, );
    await expect(this.cancelChangesButton, "Cancel changes button").toHaveText( await Strings.CANCEL_CHANGES.name, );
  }
  /** Validate Worldline preferences section visibility. */
  async validateWordlineSectionIsDisplayed(
    isDisplayed: boolean,
  ): Promise<void> {
    console.log("Validate Worldline preferences section");
    if (isDisplayed)
      await expect( this.ibPayPreferencesSection, "Worldline preferences section", ).toBeVisible();
    else
      await expect( this.ibPayPreferencesSection, "Worldline preferences section", ).not.toBeVisible();
  }
  /** Validate Worldline saved preferences. */
  async validateWordlineSavedPreferencesSection({
    accountHolder,
    contactPreferences,
    userType,
  }: {
    accountHolder: ContactAccountHolder;
    contactPreferences: ContactPreferences;
    userType: string;
  }): Promise<void> {
    console.log(`Validate Worldline saved preferences for ${userType}`);
    await this.validateWordlineSectionIsDisplayed(true);
    await expect( this.ibPayPreferencesCompanyNameLabel, "Company name", ).toHaveText(accountHolder.accountName);
    await expect( this.ibPayPreferencesAccountNumberLabel, "Company account number", ).toHaveText(accountHolder.accountNumber.replace(/(.{4})(?=.)/g, "$1 "));
    await expect( this.ibPayPreferencesAccountLabels, "Account type labels", ).toHaveCount(accountHolder.registrationRoles.length);
  }
}
