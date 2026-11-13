import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

interface RegularGuestDetails {
  title?: string | null;
  firstName: string;
  lastName: string;
  emailAddress: string;
  nationality: string;
  passportNumber?: string;
  mobilePrefix: string;
  mobile: string;
}

/**
 * 'Add regular guests' section on the Account Settings page, legacy ("bart") Premier Inn web
 * application. Mirrors qa/reference `pages/bart/components/accountSettings/regularGuests.js`.
 */
export class RegularGuestsSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly regularGuestSection: Locator = this.page.locator('div[data-test="AccountSettings"] > div > section').nth(3);
  readonly regularGuestSectionTitleLabel: Locator = this.regularGuestSection.locator('h4');
  readonly regularGuestSectionDescriptionLabel: Locator = this.regularGuestSection.locator('p');
  readonly addGuestButton: Locator = this.regularGuestSection.locator('button');
  readonly addRegularGuestForm: Locator = this.page.locator('form[data-test="regular-guests-form"]');
  readonly addRegularGuestTitleLabel: Locator = this.page.locator('form[data-test="regular-guests-form"] ~ div h4');
  readonly editRoomPreferencesInfoLabel: Locator = this.page.locator('form[data-test="regular-guests-form"] ~ div div');
  readonly titleDropdown: Locator = this.page.locator('form[data-test="regular-guests-form"] select#title');
  readonly selectCountryDropdown: Locator = this.page.locator('form[data-test="regular-guests-form"] select#nationality');
  readonly countryDialingCodeDropdown: Locator = this.page.locator('form[data-test="regular-guests-form"] select#dialingCountryCode');
  readonly firstNameInput: Locator = this.page.locator('form[data-test="regular-guests-form"] input#firstName');
  readonly lastNameInput: Locator = this.page.locator('form[data-test="regular-guests-form"] input#lastName');
  readonly emailInput: Locator = this.page.locator('form[data-test="regular-guests-form"] input#email');
  readonly passportNumberInput: Locator = this.page.locator('form[data-test="regular-guests-form"] input#passportNumber');
  readonly mobilePhoneInput: Locator = this.page.locator('form[data-test="regular-guests-form"] input#mobile');
  readonly saveGuestButton: Locator = this.addRegularGuestForm.locator('button').first();
  readonly cancelChangesPasswordButton: Locator = this.addRegularGuestForm.locator('button').nth(1);
  readonly guestUpdatedSuccessNotificationLabel: Locator = this.page.locator('div[data-test="AccountSettings"] > div > section:nth-child(4) div[class*="pi-notification"] > div');

  /** Return the displayed regular guest name label. */
  getRegularGuestNameLabel(guestName: string): Locator {
    return this.page.locator('span').filter({ hasText: guestName }).first();
  }

  /** Return the displayed email for a regular guest. */
  getRegularGuestEmailLabel(guestName: string): Locator {
    return this.getRegularGuestNameLabel(guestName).locator('xpath following-sibling::span[1]');
  }

  /** Return the delete action for a regular guest. */
  getRegularGuestDeleteButton(guestName: string): Locator {
    return this.getRegularGuestNameLabel(guestName).locator('xpath following-sibling::button[1]');
  }

  /** Return the edit action for a regular guest. */
  getRegularGuestEditButton(guestName: string): Locator {
    return this.getRegularGuestNameLabel(guestName).locator('xpath following-sibling::button[2]');
  }

  // ######## UI actions/navigation ########

  /** Click 'Add guest'. */
  async clickAddGuest(): Promise<void> {
    console.log('Click Add guest');
    await this.addGuestButton.click();
  }

  /** Open the title dropdown. */
  async clickTitleButton(): Promise<void> {
    console.log('Click Title button');
    await this.titleDropdown.click();
  }

  /** Select a title option by its visible label. */
  async clickTitleValueBtn(titleValue: string): Promise<void> {
    console.log(`Select title ${titleValue}`);
    await this.titleDropdown.selectOption({ label: titleValue });
  }

  /** Choose a regular guest title. */
  async setTitleButton(title: string): Promise<void> {
    console.log(`Set regular guest title: ${title}`);
    await this.clickTitleButton();
    await this.clickTitleValueBtn(title);
  }

  /** Fill the regular guest first name. */
  async setFirstNameInput({ firstName, pressTab = false }: { firstName: string; pressTab?: boolean }): Promise<void> {
    console.log(`First Name Input = ${firstName}`);
    await this.firstNameInput.fill(firstName);
    if (pressTab) await this.firstNameInput.press('Tab');
  }

  /** Fill the regular guest last name. */
  async setLastNameInput({ lastName, pressTab = false }: { lastName: string; pressTab?: boolean }): Promise<void> {
    console.log(`Last Name Input = ${lastName}`);
    await this.lastNameInput.fill(lastName);
    if (pressTab) await this.lastNameInput.press('Tab');
  }

  /** Fill the regular guest email address. */
  async setEmailAddressInput({ emailAddress = '', pressTab = true }: { emailAddress?: string; pressTab?: boolean } = {}): Promise<void> {
    console.log(`Email Address Input = ${emailAddress}`);
    await this.emailInput.fill(emailAddress);
    if (pressTab) await this.emailInput.press('Tab');
  }

  /** Select the regular guest nationality. */
  async setGuestNationality(countryName: string): Promise<void> {
    console.log('Set guest nationality');
    await this.selectCountryDropdown.selectOption({ label: countryName });
  }

  /** Select the regular guest mobile prefix. */
  async setMobilePrefix(countryName: string): Promise<void> {
    console.log('Set mobile phone prefix');
    await this.countryDialingCodeDropdown.selectOption({ label: countryName });
  }

  /** Fill the regular guest mobile phone number. */
  async setMobilePhoneInput({ mobilePhone, pressTab = false }: { mobilePhone: string; pressTab?: boolean }): Promise<void> {
    console.log(`Mobile Phone Input = ${mobilePhone}`);
    await this.mobilePhoneInput.fill(mobilePhone);
    if (pressTab) await this.mobilePhoneInput.press('Tab');
  }

  /** Fill all regular guest details and select the requested title. */
  async fillRegularGuestDetails(details: RegularGuestDetails & { selectTitle?: boolean }): Promise<void> {
    console.log('Fill Regular guest details');
    const { title, firstName, lastName, emailAddress, nationality, passportNumber, mobilePrefix, mobile, selectTitle = true } = details;
    if (selectTitle) {
      await this.setTitleButton(title ?? await Strings.MR_TITLE.name);
    }
    await this.setFirstNameInput({ firstName });
    await this.setLastNameInput({ lastName });
    await this.setEmailAddressInput({ emailAddress });
    await this.setGuestNationality(nationality);
    if (passportNumber) await this.passportNumberInput.fill(passportNumber);
    await this.setMobilePrefix(`${nationality} (${mobilePrefix})`);
    await this.setMobilePhoneInput({ mobilePhone: mobile });
  }

  /** Click Save guest. */
  async clickSaveGuestButton(): Promise<void> {
    console.log('Click Save guest button');
    await this.saveGuestButton.scrollIntoViewIfNeeded();
    await this.saveGuestButton.click();
  }

  // ######## UI validations ########

  /** Validate the regular guests section title is displayed. */
  async validateSectionTitleIsDisplayed(): Promise<void> {
    console.log('Validate regular guests section title');
    await expect(this.regularGuestSectionTitleLabel, 'Regular guests section title').toBeVisible();
  }

  /** Validate that the add-regular-guest form is displayed. */
  async validateAddRegularGuestFormIsDisplayed(): Promise<void> {
    console.log('Validate add regular guest form');
    await expect(this.addRegularGuestForm, 'Add regular guest form').toBeVisible();
    await expect(this.addRegularGuestTitleLabel, 'Add regular guest form title').toContainText(await Strings.ADD_REGULAR_GUEST.name);
    await expect(this.editRoomPreferencesInfoLabel, 'Add regular guest form info').toContainText(await Strings.REGULAR_GUEST_SECTION_INFO.name);
  }

  /** Validate the saved regular guest details and that the form closed. */
  async validateRegularGuestDetailsSuccessfullyUpdated(guestName: string, email: string): Promise<void> {
    console.log(`Validate regular guest details for ${guestName}`);
    await expect(this.guestUpdatedSuccessNotificationLabel, 'Regular guest success notification').toContainText(await Strings.REGULAR_GUEST_SUCCESSFUL_UPDATE_INFO.name);
    await expect(this.addRegularGuestForm, 'Add regular guest form').toBeHidden();
    await expect(this.getRegularGuestNameLabel(guestName), 'Regular guest name').toHaveText(guestName);
    await expect(this.getRegularGuestEmailLabel(guestName), 'Regular guest email').toHaveText(email);
    await expect(this.getRegularGuestDeleteButton(guestName), 'Regular guest delete button').toBeVisible();
    await expect(this.getRegularGuestEditButton(guestName), 'Regular guest edit button').toBeVisible();
  }

  /** Validate regular guest data returned by CDH. */
  async validateCDHRegularGuestsInfo(customerAccount: { AdditionalGuests: Array<Record<string, string>> }, guestDetails: {
    title: string;
    firstName: string;
    lastName: string;
    emailAddress: string;
    mobilePrefix: string;
    mobile: string;
    nationality: string;
  }): Promise<void> {
    console.log(`Validate CDH regular guest details for ${guestDetails.emailAddress}`);
    const guest = customerAccount.AdditionalGuests.find(({ Email }) => Email === guestDetails.emailAddress.toLowerCase());
    expect(guest, 'CDH regular guest should be present').toBeDefined();
    expect(guest!.Title, 'CDH regular guest title').toBe(guestDetails.title);
    expect(guest!.FirstName, 'CDH regular guest first name').toBe(guestDetails.firstName);
    expect(guest!.LastName, 'CDH regular guest last name').toBe(guestDetails.lastName);
    expect(guest!.Email, 'CDH regular guest email').toBe(guestDetails.emailAddress.toLowerCase());
    expect(guest!.Mobile, 'CDH regular guest mobile').toBe(`${guestDetails.mobilePrefix}${guestDetails.mobile}`);
    expect(guest!.Nationality, 'CDH regular guest nationality').toBe(guestDetails.nationality);
  }
}
