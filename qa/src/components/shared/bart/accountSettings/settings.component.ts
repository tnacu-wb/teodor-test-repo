import { type Page, type Locator, expect } from '@playwright/test';
import { YourProfileSectionComponent } from './yourProfile.component';
import { PasswordSectionComponent } from './password.component';
import { BartAccountPaymentSectionComponent } from './payment.component';
import { RegularGuestsSectionComponent } from './regularGuests.component';
import { ExtrasPreferencesSectionComponent } from './extrasPreferences.component';
import { RoomPreferencesSectionComponent } from './roomPreferences.component';
import { ManagePermissionsSectionComponent } from './managePermissions.component';

/**
 * Account Settings page composing all its sub-sections, on the legacy ("bart") Premier Inn web
 * application. Mirrors qa/reference `pages/bart/components/accountSettings/settings.js`.
 */
export class AccountSettingsPageComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly yourProfileSectionTitleLabel: Locator = this.page.locator('div[data-test="AccountSettings"] > div > section:nth-child(1) h4');
  readonly editProfileButton: Locator = this.page.locator('div[data-test="AccountSettings"] > div > section:nth-child(1) button');
  readonly passwordSectionTitleLabel: Locator = this.page.locator('div[data-test="AccountSettings"] > div > section:nth-child(2) h4');
  readonly changePasswordButton: Locator = this.page.locator('div[data-test="AccountSettings"] > div > section:nth-child(2) button');
  readonly paymentSectionTitleLabel: Locator = this.page.locator('div[data-test="AccountSettings"] > div > section:nth-child(3) h4');
  readonly paymentSectionDescriptionLabel: Locator = this.page.locator('div[data-test="AccountSettings"] > div > section:nth-child(3) p');
  readonly addPaymentCardButton: Locator = this.page.locator('div[data-test="AccountSettings"] > div > section:nth-child(3) button');
  readonly addRegularGuestButton: Locator = this.page.locator('div[data-test="AccountSettings"] > div > section:nth-child(4) > div button');
  readonly deleteRegularGuestButtonList: Locator = this.page.locator('div[data-test="AccountSettings"] > div > section:nth-child(4) address button');
  readonly confirmDeleteRegularGuestButton: Locator = this.page.locator('div[data-test="AccountSettings"] > div > div:nth-child(4) button').nth(1);
  readonly roomPreferencesSectionTitleLabel: Locator = this.page.locator('div[data-test="AccountSettings"] > div > div:first-child h4');
  readonly roomPreferencesDescriptionLabel: Locator = this.page.locator('div[data-test="AccountSettings"] > div > div:first-child p');
  readonly editRoomPreferencesButton: Locator = this.page.locator('div[data-test="AccountSettings"] > div > div:first-child button');
  readonly extrasPreferencesSectionTitleLabel: Locator = this.page.locator('div[data-test="AccountSettings"] > div > div:nth-child(2) h4');
  readonly extrasPreferencesDescriptionLabel: Locator = this.page.locator('div[data-test="AccountSettings"] > div > div:nth-child(2) p');
  readonly addMealsAndExtrasButton: Locator = this.page.locator('div[data-test="AccountSettings"] > div > div:nth-child(2) button');
  readonly contactAndPermissionsSectionTitleLabel: Locator = this.page.locator('div[data-test="AccountSettings"] > div > section:nth-child(5) h4');
  readonly contactAndPermissionsDescriptionLabel: Locator = this.page.locator('div[data-test="AccountSettings"] > div > section:nth-child(5) p');
  readonly managePermissionsButton: Locator = this.page.locator('div[data-test="AccountSettings"] > div > section:nth-child(5) button');
  readonly deleteProfileTitleLabel: Locator = this.page.locator('div[data-test="AccountSettings"] > div > div:nth-child(3) h4');
  readonly deleteProfileDescriptionLabel: Locator = this.page.locator('div[data-test="AccountSettings"] > div > div:nth-child(3) p');
  readonly deleteProfileButton: Locator = this.page.locator('div[data-test="AccountSettings"] > div > div:nth-child(3) button');
  readonly deleteProfileModal: Locator = this.page.locator('div[data-test="AccountSettings"] > div > div[class="pt3 wb-modal"] > div');
  readonly deleteProfileCloseModalButton: Locator = this.deleteProfileModal.locator('button').first();
  readonly confirmDeleteProfileButton: Locator = this.deleteProfileModal.locator('button').nth(1);
  readonly deleteProfileModalCancelButton: Locator = this.deleteProfileModal.locator('button').nth(2);
  readonly businessAddressRadioButton: Locator = this.page.locator('span.wb-radio__label__inner span').nth(1);
  readonly companyNameInput: Locator = this.page.locator('input#companyName');
  readonly companyNameErrorMessageInput: Locator = this.page.locator('.form-item-msg.form-item-msg--error');
  readonly saveChangesButton: Locator = this.page.locator('.form-item-msg.form-item-msg--error');

  // UI components

  readonly yourProfile: YourProfileSectionComponent = new YourProfileSectionComponent();
  readonly password: PasswordSectionComponent = new PasswordSectionComponent();
  readonly payment: BartAccountPaymentSectionComponent = new BartAccountPaymentSectionComponent();
  readonly regularGuests: RegularGuestsSectionComponent = new RegularGuestsSectionComponent();
  readonly extrasPreferences: ExtrasPreferencesSectionComponent = new ExtrasPreferencesSectionComponent();
  readonly roomPreferences: RoomPreferencesSectionComponent = new RoomPreferencesSectionComponent();
  readonly managePermissions: ManagePermissionsSectionComponent = new ManagePermissionsSectionComponent();
  readonly contactAndPermissionsCenter: ManagePermissionsSectionComponent = this.managePermissions;

  // ######## UI actions/navigation ########

  /** Click 'Edit profile'. */
  async clickEditProfile(): Promise<void> {
    console.log('Click Edit Profile button');
    await this.editProfileButton.click();
  }

  /** Click the legacy-named Edit Profile action. */
  async clickEditProfileButton(): Promise<void> {
    await this.clickEditProfile();
  }

  /** Click 'Change password'. */
  async clickChangePassword(): Promise<void> {
    console.log('Click Change Password button');
    await this.changePasswordButton.click();
  }

  /** Click the legacy-named Change Password action. */
  async clickChangePasswordButton(): Promise<void> {
    await this.clickChangePassword();
  }

  // ######## UI validations ########

  /** Validate the Account Settings page's top-level section titles are displayed. */
  async validatePage(): Promise<void> {
    console.log('Validate Account Settings page');
    await expect(this.yourProfileSectionTitleLabel, 'Your profile section title').toBeVisible();
    await expect(this.passwordSectionTitleLabel, 'Password section title').toBeVisible();
    await expect(this.paymentSectionTitleLabel, 'Payment section title').toBeVisible();
  }

  /** Click Add Payment Card. */
  async clickAddPaymentCardButton(): Promise<void> {
    console.log('Click Add Payment Card button');
    await this.addPaymentCardButton.click();
  }

  /** Click the account settings save-changes action. */
  async clickSaveChangesButton(): Promise<void> {
    console.log('Click save changes button');
    await this.saveChangesButton.click();
  }

  /** Set the company name when a value is supplied. */
  async setCompanyName(companyName: string | null = null): Promise<void> {
    if (companyName) {
      console.log(`Company Name Input = ${companyName}`);
      await this.companyNameInput.fill(companyName);
      await this.companyNameInput.press('Tab');
    }
  }

  /** Click Add Regular Guest. */
  async clickAddRegularGuestButton(): Promise<void> {
    console.log('Click Add Regular Guest button');
    await this.addRegularGuestButton.scrollIntoViewIfNeeded();
    await this.addRegularGuestButton.click();
  }

  /** Click the business address radio button. */
  async clickBusinessAddressRadioButton(): Promise<void> {
    console.log('Click Business address radio button');
    await this.businessAddressRadioButton.scrollIntoViewIfNeeded();
    await this.businessAddressRadioButton.click();
  }

  /** Click a regular guest delete button by index. */
  async clickDeleteRegularGuestButtonByIndex(indexOfDeleteButton = 0): Promise<void> {
    console.log('Click Delete Regular Guest button');
    await this.deleteRegularGuestButtonList.nth(indexOfDeleteButton).scrollIntoViewIfNeeded();
    await this.deleteRegularGuestButtonList.nth(indexOfDeleteButton).click();
  }

  /** Remove one regular guest when the account has six guests. */
  async removeOneRegularGuest(): Promise<void> {
    console.log('Remove one regular guest if there are 6 regular guests already added');
    if (await this.deleteRegularGuestButtonList.count() === 6) {
      await this.clickDeleteRegularGuestButtonByIndex();
      await this.clickConfirmDeleteRegularGuestButton();
    } else {
      console.log(`There are ${await this.deleteRegularGuestButtonList.count()} regular guests.`);
    }
  }

  /** Confirm deleting a regular guest. */
  async clickConfirmDeleteRegularGuestButton(): Promise<void> {
    console.log('Click Confirm Delete Regular Guest button');
    await this.confirmDeleteRegularGuestButton.click();
  }

  /** Open room preferences. */
  async clickEditRoomPreferencesButton(): Promise<void> {
    console.log('Click Edit Room Preferences button');
    await this.editRoomPreferencesButton.click();
  }

  /** Open meals and extras preferences. */
  async clickMealsAndExtrasButton(): Promise<void> {
    console.log('Click Meals and Extras button');
    await this.addMealsAndExtrasButton.click();
  }

  /** Open contact and permissions preferences. */
  async clickManagePermissionsButton(): Promise<void> {
    console.log('Click Manage Permissions button');
    await this.managePermissionsButton.click();
  }

  /** Open the delete profile modal. */
  async clickDeleteProfileButton(): Promise<void> {
    console.log('Click Delete Profile button');
    await this.deleteProfileButton.click();
  }

  /** Confirm profile deletion from the modal. */
  async clickConfirmDeleteProfileButton(): Promise<void> {
    console.log('Click Confirm Delete Profile from modal');
    await this.confirmDeleteProfileButton.click();
  }

  /** Validate the company name input value. */
  async validateCompanyName(expectedCompanyName: string): Promise<void> {
    console.log(`Validate company name: ${expectedCompanyName}`);
    await expect(this.companyNameInput, 'Company name input').toHaveValue(expectedCompanyName);
  }

  /** Validate the company name error message. */
  async validateCompanyNameErrorMessage(errorMessage: string): Promise<void> {
    console.log(`Validate company name error: ${errorMessage}`);
    await expect(this.companyNameErrorMessageInput, 'Company name error message').toContainText(errorMessage);
  }
}
