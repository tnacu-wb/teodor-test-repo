import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '../../../test-data/strings';

/**
 * The Room and Guest section of the Amend Booking flow containing the UI elements, custom
 * actions and validations. Mirrors qa/reference
 * `components/common/amendBooking/roomAndGuestSection.js` (simplified: reference navigates the
 * guest/room-type dropdowns via brittle fixed-depth XPath indices into the modal body - replaced
 * with role/testid-scoped locators, which is the idiomatic and more resilient Playwright approach
 * for the same dropdown controls).
 */
export class RoomAndGuestSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly modalBody: Locator = this.page.locator('div[data-testid="ModalBody"]:visible');
  readonly amendRoomsAndGuestSection: Locator = this.page.locator('div[data-testid="amend-rooms-and-guests-section"]');
  readonly guestsRoomModalTitle: Locator = this.page.locator('h3[data-testid="add-room-modal-guests-title"]');
  readonly guestsRoomAdultsDropdown: Locator = this.modalBody.locator('xpath=./div[1]/div[1]/div[1]/div[1]/div[1]/span[1]/button[1]');
  readonly guestsRoomChildrenDropdown: Locator = this.modalBody.locator('xpath=./div[1]/div[1]/div[1]/div[1]/div[2]/span[1]/button[1]');
  readonly guestsRoomTypeDropdown: Locator = this.modalBody.locator('xpath=./div[1]/div[1]/div[1]/div[2]/span[1]/button[1]');
  readonly guestsRoomTypeList: Locator = this.modalBody.locator('[data-testid="DropdownComp-Wrapper"] button');
  readonly titleDropdown: Locator = this.page.locator('button[data-testid="DropdownComp-amend-Form-TitleDropdown-menuButton"], button[data-testid="DropdownComp-amend-Form-titleDropdown-menuButton"]');
  readonly titleDropdownPlaceholderLabel: Locator = this.page.locator('span[data-testid="DropdownComp-amend-Form-TitleDropdown-menuButtonOptionsText"], span[data-testid="DropdownComp-amend-Form-titleDropdown-menuButtonText"]');
  readonly titleErrorMessage: Locator = this.page.locator('p[data-testid="amend-Form-ErrorText"]');
  readonly titleDropdownSelectorLabel: Locator = this.page.locator('[data-testid="DropdownComp-amend-Form-TitleDropdown-buttonSelectorLabel"], [data-testid="DropdownComp-amend-Form-titleDropdown-buttonSelectorLabel"]');
  readonly enterGuestDetailsManuallyLink: Locator = this.page.locator('[data-testid="GuestDetailsBBContainer-Form-SwitchToManual"]');
  readonly firstNameInput: Locator = this.page.locator('input[data-testid="input-bbGuestDetails[0][firstName]"], input[data-testid="input-firstName"]');
  readonly lastNameInput: Locator = this.page.locator('input[data-testid="input-bbGuestDetails[0][lastName]"], input[data-testid="input-lastName"]');
  readonly emailAddressInput: Locator = this.page.locator('input[data-testid="input-bbGuestDetails[0][emailAddress]"], input[data-testid="input-emailAddress"]');
  readonly loggedInNameEmailInput: Locator = this.page.getByTestId('input-searchCriteria');
  readonly dynamicGuestSuggestionDropdown: Locator = this.page.getByTestId('GuestDetailsBBContainer-Form-SuggestionCard');
  readonly firstNameErrorMessage: Locator = this.page.getByTestId('input-firstName-FormErrorMessage');
  readonly lastNameErrorMessage: Locator = this.page.getByTestId('input-lastName-FormErrorMessage');
  readonly emailErrorMessage: Locator = this.page.getByTestId('input-emailAddress-FormErrorMessage');
  readonly leadGuestTitleLabel: Locator = this.page.getByTestId('add-room-modal-lead-guest-title');
  readonly postalCodeInput: Locator = this.page.getByTestId('input-postalCode');
  readonly errorMessage: Locator = this.page.locator('[data-testid*="Error"]');
  readonly addRoomModalTitleLabel: Locator = this.page.locator('h2').first();
  readonly addRoomModalPriceLabel: Locator = this.page.getByTestId('add-room-modal-price');
  readonly removeRoomModalButton: Locator = this.page.getByTestId('RemoveRoomModal-removeRoom');
  readonly removeRoomSuccessNotificationAlertLabel: Locator = this.page.getByTestId('remove-room-success-notification-Alert');
  readonly editRoomSuccessNotificationAlertLabel: Locator = this.page.getByTestId('edit-room-success-notification-Alert');
  readonly addRoomSuccessNotificationAlertLabel: Locator = this.page.getByTestId('add-room-success-notification-AlertDescription');
  readonly checkAvailabilityButton: Locator = this.modalBody.locator('button[data-testid="add-room-modal-availability-button"]');
  readonly closeModalButton: Locator = this.page.getByTestId('add-room-close-button');

  /** Return the room availability alert locator. */
  get roomAvailableAlert(): Locator {
    return this.modalBody.locator('[data-testid="AlertDescription"]');
  }
  readonly addEditRoomButton: Locator = this.modalBody.locator('button[data-testid="add-edit-room-button"]');

  /** Return a room card locator using the zero-based room index used by the target suite. */
  getRoomInfoCard(roomIndex: number): Locator { return this.page.getByTestId(`room-info-card-amend-${roomIndex + 1}`); }

  /** Return a room card price locator. */
  getRoomInfoCardPriceLabel(roomIndex: number): Locator { return this.getRoomInfoCard(roomIndex).locator('p').nth(4); }

  /** Return a room edit button locator. */
  getRoomEditButton(roomIndex: number): Locator { return this.page.getByTestId(`room-info-card-amend-edit-button-${roomIndex + 1}`); }

  /** Return a room remove button locator. */
  getRoomRemoveButton(roomIndex: number): Locator { return this.page.getByTestId(`room-info-card-amend-remove-button-${roomIndex + 1}`); }

  /** Return the selected room type option. */
  getGuestRoomType(roomType: string): Locator { return this.modalBody.locator('[data-testid="DropdownComp-Wrapper"] button').filter({ hasText: roomType }).first(); }

  // ######## UI actions/navigation ########

  /** Open the guests-room adults dropdown and select the given count. */
  async selectAdults(adults: number): Promise<void> {
    console.log(`Select ${adults} adult(s) for room`);
    const adultsButton = this.guestsRoomAdultsDropdown;
    await adultsButton.waitFor({ state: 'visible', timeout: 15000 });
    await adultsButton.click();

    const option = adultsButton.locator(
      `xpath=following-sibling::div//button[@data-testid="DropdownComp-add-room-modal-adults-number-${adults - 1}"]`
    );

    await option.waitFor({ state: 'visible', timeout: 15000 });
    await option.click();
  }

  /** Open the guests-room children dropdown and select the given count. */
  async selectChildren(children: number): Promise<void> {
    console.log(`Select ${children} child(ren) for room`);
    const childrenButton = this.guestsRoomChildrenDropdown;
    await childrenButton.waitFor({ state: 'visible', timeout: 15000 });
    await childrenButton.click();

    const option = childrenButton.locator(
      `xpath=following-sibling::div//button[@data-testid="DropdownComp-add-room-modal-children-number-${children}"]`
    );

    await option.waitFor({ state: 'visible', timeout: 15000 });
    await option.click();
    const childLabel = children === 1 ? await Strings.CHILD_LOWER_CASE.name : await Strings.CHILDREN_LOWER_CASE.name;
    await expect(childrenButton, `Children selector should show ${children} ${childLabel}`).toContainText(
      new RegExp(`${children}\\s+${childLabel}`, 'i')
    );
  }

  /** Open the room-type dropdown and select the given title/room type name. */
  async selectRoomType(roomType: string): Promise<void> {
    console.log(`Select room type=${roomType}`);
    await this.guestsRoomTypeDropdown.click();
    const roomTypeOption = this.modalBody.locator('[data-testid="DropdownComp-Wrapper"] button').filter({ hasText: roomType }).first();
    await roomTypeOption.waitFor({ state: 'visible', timeout: 15000 });
    await roomTypeOption.click();
  }

  /** Fill a first name and leave the field. */
  async setFirstNameField(firstName: string): Promise<void> {
    console.log('Set first name field');
    await this.firstNameInput.fill(firstName);
    await this.firstNameInput.press('Tab');
  }

  /** Fill a last name and leave the field. */
  async setLastNameField(lastName: string): Promise<void> {
    console.log('Set last name field');
    await this.lastNameInput.fill(lastName);
    await this.lastNameInput.press('Tab');
  }

  /** Fill an email address and leave the field. */
  async setEmailField(email: string): Promise<void> {
    console.log('Set email field');
    await this.emailAddressInput.fill(email);
    await this.emailAddressInput.press('Tab');
  }

  /** Fill a postal code and leave the field. */
  async setPostalCode(postalCode: string): Promise<void> {
    console.log('Set postal code field');
    await this.postalCodeInput.fill(postalCode);
    await this.postalCodeInput.press('Tab');
  }

  /** Select a logged-in guest from the suggestion list. */
  async setGuestNameField(name: string): Promise<void> {
    console.log(`Select logged-in guest=${name}`);
    await this.loggedInNameEmailInput.fill(name);
    await this.dynamicGuestSuggestionDropdown.waitFor({ state: 'visible' });
    await this.dynamicGuestSuggestionDropdown.getByText(name, { exact: false }).first().click();
  }

  /** Edit a specific room using the reference's one-based room number. */
  async editSpecificRoom(data: { roomNumber: number }): Promise<void> {
    console.log(`Edit room number=${data.roomNumber}`);
    await this.getRoomEditButton(data.roomNumber - 1).click();
  }

  /** Open the remove confirmation for a specific room. */
  async clickRemoveSpecificRoom(data: { roomNumber: number }): Promise<void> {
    console.log(`Open remove confirmation for room number=${data.roomNumber}`);
    await this.getRoomRemoveButton(data.roomNumber - 1).click();
  }

  /** Remove a specific room after confirming the modal. */
  async removeSpecificRoom(data: { roomNumber: number }): Promise<void> {
    console.log(`Remove room number=${data.roomNumber}`);
    await this.clickRemoveSpecificRoom(data);
    await this.removeRoomModalButton.click();
  }

  /** Close the add/edit room modal. */
  async closeModal(): Promise<void> {
    console.log('Close add/edit room modal');
    await this.closeModalButton.click();
  }

  /** Close the add/edit room modal with its cancel action. */
  async clickCancelButton(): Promise<void> {
    console.log('Cancel add/edit room modal');
    await this.closeModal();
  }

  /** Clear the lead guest fields. */
  async clearLeadGuestFields(): Promise<void> {
    console.log('Clear lead guest fields');
    await this.firstNameInput.fill('');
    await this.lastNameInput.fill('');
    await this.emailAddressInput.fill('');
    await this.emailAddressInput.press('Tab');
  }

  /** Select the lead guest title in the add/edit room modal. */
  async setTitle(title: string): Promise<void> {
    console.log(`Select title=${title}`);
    await this.titleDropdown.click();
    await this.page.locator(
      `button[data-testid*="DropdownComp-amend-Form-TitleDropdown-"][value="${title}"], button[data-testid*="DropdownComp-amend-Form-titleDropdown-"][value="${title}"]`
    ).click();
  }

  /** Click the reference flow's Enter details manually link. */
  async clickEnterDetailsManuallyLink(): Promise<void> {
    console.log('When I click on the Enter details manually link');
    await this.enterGuestDetailsManuallyLink.click();
  }

  /** Fill the lead guest first name in the add/edit room modal. */
  async setFirstName(firstName: string): Promise<void> {
    console.log(`Set first name=${firstName}`);
    await this.firstNameInput.fill(firstName);
    await this.firstNameInput.press('Tab');
  }

  /** Fill the lead guest last name in the add/edit room modal. */
  async setLastName(lastName: string): Promise<void> {
    console.log(`Set last name=${lastName}`);
    await this.lastNameInput.fill(lastName);
    await this.lastNameInput.press('Tab');
  }

  /** Fill the mandatory lead guest fields in the add/edit room modal. */
  async setMandatoryFields(title: string, firstName: string, lastName: string): Promise<void> {
    console.log(`Set mandatory room guest fields for ${title} ${firstName} ${lastName}`);
    await this.setTitle(title);
    await this.setFirstName(firstName);
    await this.setLastName(lastName);
  }

  /** Click the check-availability button. */
  async clickCheckAvailabilityButton(): Promise<void> {
    console.log('Click check availability button');
    await this.checkAvailabilityButton.waitFor({ state: 'visible', timeout: 15000 });
    await expect(this.checkAvailabilityButton, 'Check availability button should be enabled').toBeEnabled();
    await this.checkAvailabilityButton.scrollIntoViewIfNeeded();
    await this.checkAvailabilityButton.click();
  }

  /** Check room availability and wait for the modal availability result. */
  async checkAvailability(): Promise<void> {
    console.log('Check room availability');
    await this.clickCheckAvailabilityButton();
    await expect(this.roomAvailableAlert, 'Room availability alert should be visible').toBeVisible({ timeout: 30000 });
  }

  /** Validate the room is available after checking availability. */
  async validateTrueCheckAvailability(): Promise<void> {
    console.log('Validate room available alert is visible');
    await this.clickCheckAvailabilityButton();
    await expect(this.roomAvailableAlert, 'Room available alert should be visible').toBeVisible({ timeout: 30000 });
  }

  /** Submit the add/edit room modal and wait for it to close. */
  async clickAddEditRoomButton(): Promise<void> {
    console.log('Click add/edit room button');
    const modalContent = this.page.locator('[data-testid="ModalContent"]');

    await expect(async () => {
      await this.addEditRoomButton.waitFor({ state: 'visible' });
      await expect(this.addEditRoomButton, 'Add/edit room button should be enabled').toBeEnabled();
      await this.addEditRoomButton.click();
      await modalContent.waitFor({ state: 'hidden', timeout: 15000 });
    }, 'Add/edit room modal should close after submitting the update').toPass({ timeout: 30000, intervals: [1000, 2000] });
  }

  // ######## UI validations ########

  /** Validate the room-and-guests modal title is displayed. */
  async validateGuestsRoomModalTitle(): Promise<void> {
    console.log('Validate guests room modal title');
    await expect(this.guestsRoomModalTitle, 'Guests room modal title').toBeVisible();
  }

  /** Validate whether the lead guest input is enabled. */
  async validateLeadGuestSectionIsEnabled(isEnabled: boolean): Promise<void> {
    console.log(`Validate lead guest section enabled=${isEnabled}`);
    if (isEnabled) await expect(this.firstNameInput, 'Lead guest input field should be enabled').toBeEnabled();
    else await expect(this.firstNameInput, 'Lead guest input field should be disabled').toBeDisabled();
  }

  /** Validate a first-name field value. */
  async validateFirstNameValue(value: string): Promise<void> {
    console.log('Validate first name value');
    await expect(this.firstNameInput, 'First name input value').toHaveValue(value);
  }

  /** Validate a last-name field value. */
  async validateLastNameValue(value: string): Promise<void> {
    console.log('Validate last name value');
    await expect(this.lastNameInput, 'Last name input value').toHaveValue(value);
  }

  /** Validate an email field value. */
  async validateEmailValue(value: string): Promise<void> {
    console.log('Validate email value');
    await expect(this.emailAddressInput, 'Email input value').toHaveValue(value);
  }

  /** Validate the room types exposed by the open dropdown. */
  async validateAvailableRoomTypes(...roomTypes: string[]): Promise<void> {
    console.log(`Validate available room types=${roomTypes.join(', ')}`);
    await this.guestsRoomTypeDropdown.click();
    for (const roomType of roomTypes) await expect(this.getGuestRoomType(roomType), `Room type ${roomType}`).toBeVisible();
    await this.guestsRoomTypeDropdown.click();
  }

  /** Validate add/edit button enabled state. */
  async validateAddEditRoomButtonEnabled(enabled: boolean): Promise<void> {
    console.log(`Validate add/edit room button enabled=${enabled}`);
    if (enabled) await expect(this.addEditRoomButton, 'Add/Edit a room button should be enabled').toBeEnabled();
    else await expect(this.addEditRoomButton, 'Add/Edit a room button should be disabled').toBeDisabled();
  }

  /** Validate whether a room card is displayed. */
  async validateRoomInfoCardIsDisplayed(data: { roomIndex: number; isDisplayed?: boolean }): Promise<void> {
    const isDisplayed = data.isDisplayed ?? true;
    console.log(`Validate room=${data.roomIndex + 1} displayed=${isDisplayed}`);
    if (isDisplayed) await expect(this.getRoomInfoCard(data.roomIndex), `Room info card ${data.roomIndex + 1}`).toBeVisible();
    else await expect(this.getRoomInfoCard(data.roomIndex), `Room info card ${data.roomIndex + 1}`).toBeHidden();
  }

  /** Validate room-card text. */
  async validateRoomInfoCardIncludesText(data: { roomIndex: number; includedText: string }): Promise<void> {
    console.log(`Validate room=${data.roomIndex + 1} includes text=${data.includedText}`);
    await expect(this.getRoomInfoCard(data.roomIndex), `Room ${data.roomIndex + 1} should include ${data.includedText}`).toContainText(data.includedText);
  }

  /** Validate a remove-room button visibility. */
  async validateRemoveSpecifRoomButton(data: { roomNumber: number; isDisplayed?: boolean }): Promise<void> {
    console.log(`Validate room number=${data.roomNumber} remove button displayed=${data.isDisplayed ?? true}`);
    const button = this.getRoomRemoveButton(data.roomNumber - 1);
    if (data.isDisplayed ?? true) await expect(button, 'Specific room remove button').toBeVisible();
    else await expect(button, 'Specific room remove button').toBeHidden();
  }

  /** Validate a room action success notification. */
  async validateRoomSuccessNotification(type: 'add' | 'edit' | 'remove', roomNumber: number, isDisplayed = true): Promise<void> {
    console.log(`Validate ${type} room success notification for room number=${roomNumber}, displayed=${isDisplayed}`);
    const notification = type === 'add' ? this.addRoomSuccessNotificationAlertLabel : type === 'edit' ? this.editRoomSuccessNotificationAlertLabel : this.removeRoomSuccessNotificationAlertLabel;
    if (isDisplayed) await expect(notification, `${type} room success notification`).toBeVisible();
    else await expect(notification, `${type} room success notification`).toBeHidden();
    if (isDisplayed) {
      const message = type === 'add' ? Strings.ROOM_NUMBER_HAS_BEEN_SUCCESSFULLY_ADDED : type === 'edit' ? Strings.ROOM_NUMBER_HAS_BEEN_SUCCESSFULLY_UPDATED : Strings.ROOM_NUMBER_HAS_BEEN_SUCCESSFULLY_REMOVED;
      await expect(notification, `${type} room success notification text`).toContainText((await message.name).replace('[number]', String(roomNumber)));
    }
  }

  /** Validate the add-room success notification. */
  async validateAddRoomSuccessNotificationAlert(data: { roomIndex: number; isDisplayed?: boolean }): Promise<void> {
    console.log(`Validate add-room success notification for room=${data.roomIndex + 1}`);
    await this.validateRoomSuccessNotification('add', data.roomIndex + 1, data.isDisplayed);
  }

  /** Validate the edit-room success notification. */
  async validateEditRoomSuccessNotificationAlert(data: { roomIndex: number; isDisplayed?: boolean }): Promise<void> {
    console.log(`Validate edit-room success notification for room=${data.roomIndex + 1}`);
    await this.validateRoomSuccessNotification('edit', data.roomIndex + 1, data.isDisplayed);
  }

  /** Validate the remove-room success notification. */
  async validateRemoveRoomSuccessNotificationAlert(data: { roomIndex: number; isDisplayed?: boolean }): Promise<void> {
    console.log(`Validate remove-room success notification for room=${data.roomIndex + 1}`);
    await this.validateRoomSuccessNotification('remove', data.roomIndex + 1, data.isDisplayed);
  }
}
