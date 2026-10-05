import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * 'Your profile' (edit profile) section on the Account Settings page, legacy ("bart") Premier
 * Inn web application. Mirrors qa/reference `pages/bart/components/accountSettings/yourProfile.js`.
 */
export class YourProfileSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly editProfileTitleLabel: Locator = this.page.locator('form[name="user-profile-form"] ~ h4');
  readonly userEmailLabel: Locator = this.page.locator('form[name="user-profile-form"] ~ h5');
  readonly titleDropdown: Locator = this.page.locator('form[data-test="user-profile-form"] select#title');
  readonly firstNameInput: Locator = this.page.locator('form[data-test="user-profile-form"] input#firstName');
  readonly lastNameInput: Locator = this.page.locator('form[data-test="user-profile-form"] input#lastName');
  readonly countryDialingCodeDropdown: Locator = this.page.locator('form[data-test="user-profile-form"] select#dialingCountryCode');
  readonly countryDialingCodeDropdownItemsList: Locator = this.page.locator('form[data-test="user-profile-form"] select#dialingCountryCode option');
  readonly mobilePhoneInput: Locator = this.page.locator('form[data-test="user-profile-form"] input#mobile');
  readonly telephoneInput: Locator = this.page.locator('form[data-test="user-profile-form"] input#telephone');
  readonly profilePostcodeInput: Locator = this.page.locator('form[data-test="user-profile-form"] h6 ~ input').first();
  readonly findNewAddressButton: Locator = this.page.locator('div[data-test="AccountSettings"] button').filter({ hasText: Strings.FIND_NEW_ADDRESS.data.default ?? '' });
  readonly postcodeTitleLabel: Locator = this.page.locator('form[data-test="user-profile-form"] h6');
  readonly homeAddressRadioButton: Locator = this.page.locator('form[data-test="user-profile-form"] input#homeAddress');
  readonly businessAddressRadioButton: Locator = this.page.locator('form[data-test="user-profile-form"] input#businessAddress');
  readonly newPostcodeInput: Locator = this.page.locator('form[data-test="user-profile-form"] input#line4').locator('xpath following::input[1]');
  readonly selectCountryDropdown: Locator = this.page.locator('form[data-test="user-profile-form"] select#countryCode');
  readonly selectCountryDropdownItemsList: Locator = this.page.locator('form[data-test="user-profile-form"] select#countryCode option');
  readonly saveChangesButton: Locator = this.page.locator('div[data-test="AccountSettings"] button').filter({ hasText: 'Save changes' });
  readonly cancelChangesButton: Locator = this.page.locator('div[data-test="AccountSettings"] button').filter({ hasText: 'Cancel changes' });
  readonly addressLineOneInput: Locator = this.page.locator('form[data-test="user-profile-form"] input#line1');
  readonly addressLineTwoInput: Locator = this.page.locator('form[data-test="user-profile-form"] input#line2');
  readonly addressLineThreeInput: Locator = this.page.locator('form[data-test="user-profile-form"] input#line3');
  readonly addressLineFourInput: Locator = this.page.locator('form[data-test="user-profile-form"] input#line4');

  // ######## UI actions/navigation ########

  /** Fill first/last name and mobile phone number. */
  async fillProfileDetails({ firstName, lastName, mobile }: { firstName: string; lastName: string; mobile: string }): Promise<void> {
    console.log('Fill profile details');
    await this.firstNameInput.fill(firstName);
    await this.lastNameInput.fill(lastName);
    await this.mobilePhoneInput.fill(mobile);
  }

  // ######## UI validations ########

  /** Validate the 'Your profile' section title is displayed. */
  async validateEditProfileTitleIsDisplayed(): Promise<void> {
    console.log('Validate edit profile title');
    await expect(this.editProfileTitleLabel, 'Edit profile title').toBeVisible();
  }
}
