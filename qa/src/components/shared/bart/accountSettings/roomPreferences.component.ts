import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * 'Edit room preferences' section on the Account Settings page, legacy ("bart") Premier Inn web
 * application. Mirrors qa/reference `pages/bart/components/accountSettings/roomPreferences.js`.
 */
export class RoomPreferencesSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly editRoomPreferencesTitleLabel: Locator = this.page.locator('form[data-test="room-requirements-form"] ~ h4');
  readonly adultsLabel: Locator = this.page.locator('form[data-test="room-requirements-form"] p[for="adults"]');
  readonly adultsDropdown: Locator = this.page.locator('form[data-test="room-requirements-form"] select#adults');
  readonly adultsDropdownItemsList: Locator = this.page.locator('form[data-test="room-requirements-form"] select#adults option');
  readonly childrenLabel: Locator = this.page.locator('form[data-test="room-requirements-form"] p[for="children"]');
  readonly childrenDropdown: Locator = this.page.locator('form[data-test="room-requirements-form"] select#children');
  readonly childrenDropdownItemsList: Locator = this.page.locator('form[data-test="room-requirements-form"] select#children option');
  readonly cotLabel: Locator = this.page.locator('form[data-test="room-requirements-form"] p[for="cotRequired"]');
  readonly cotDropdown: Locator = this.page.locator('form[data-test="room-requirements-form"] select#cotRequired');
  readonly cotDropdownItemsList: Locator = this.page.locator('form[data-test="room-requirements-form"] select#cotRequired option');
  readonly roomTypeLabel: Locator = this.page.locator('form[data-test="room-requirements-form"] p[for="type"]');
  readonly roomTypeDropdown: Locator = this.page.locator('form[data-test="room-requirements-form"] select#type');
  readonly roomTypeDropdownItemsList: Locator = this.page.locator('form[data-test="room-requirements-form"] select#type option');
  readonly saveChangesButton: Locator = this.page.locator('form[name="room-requirements-form"] button').first();
  readonly cancelChangesButton: Locator = this.page.locator('form[name="room-requirements-form"] button').nth(1);
  readonly roomPreferencesSuccessNotificationLabel: Locator = this.page.locator('div[data-test="AccountSettings"] > div > div:first-child div[class*="pi-notification"] > div');

  // ######## UI actions/navigation ########

  /** Set room preferences values and save changes. */
  async setRoomPreferencesAndSaveChanges({ adultsValue, childrenValue, cot, roomType }: {
    adultsValue: number;
    childrenValue: number;
    cot: string;
    roomType: string;
  }): Promise<void> {
    console.log(`Set room preferences and save changes: Adults=${adultsValue}, Children=${childrenValue}, Cot=${cot}, Type=${roomType}`);
    await this.adultsDropdown.selectOption({ label: `${adultsValue}` });
    await this.childrenDropdown.selectOption({ label: `${childrenValue}` });
    await this.cotDropdown.selectOption({ label: cot });
    await this.roomTypeDropdown.selectOption({ label: roomType });
    await this.saveChangesButton.scrollIntoViewIfNeeded();
    await this.saveChangesButton.click();
  }

  /** Select the number of adults, children, and room type preference. */
  async setRoomPreferences({ adults, children, roomType }: { adults: string; children: string; roomType: string }): Promise<void> {
    console.log(`Set room preferences: Adults=${adults}, Children=${children}, Type=${roomType}`);
    await this.adultsDropdown.selectOption(adults);
    await this.childrenDropdown.selectOption(children);
    await this.roomTypeDropdown.selectOption(roomType);
  }

  // ######## UI validations ########

  /** Validate the 'Edit room preferences' title is displayed. */
  async validateEditRoomPreferencesTitleIsDisplayed(): Promise<void> {
    console.log('Validate edit room preferences title');
    await expect(this.editRoomPreferencesTitleLabel, 'Edit room preferences title').toBeVisible();
  }

  /** Validate the room-preference form labels and their localized text. */
  async validateEditRoomPreferencesFormLabels(): Promise<void> {
    console.log('Validate Edit room preferences form labels are displayed: Adults, Children, Cot, Type');
    await expect(this.adultsLabel, 'Adults label').toBeVisible();
    await expect(this.adultsLabel, 'Adults label text').toContainText(await Strings.ADULTS.name);
    await expect(this.childrenLabel, 'Children label').toBeVisible();
    await expect(this.childrenLabel, 'Children label text').toContainText(await Strings.CHILDREN.name);
    await expect(this.cotLabel, 'Cot label').toBeVisible();
    await expect(this.cotLabel, 'Cot label text').toContainText(await Strings.COT.name);
    await expect(this.roomTypeLabel, 'Type label').toBeVisible();
    await expect(this.roomTypeLabel, 'Type label text').toContainText('Type');
  }

  /** Validate the successful room-preference update and closed form. */
  async validateRoomPreferencesUpdatedSuccessfully({ successMessage }: { successMessage: string }): Promise<void> {
    console.log(`Validate room preferences update success message: "${successMessage}"`);
    await expect(this.roomPreferencesSuccessNotificationLabel, 'Room preferences success notification').toBeVisible();
    await expect(this.roomPreferencesSuccessNotificationLabel, 'Room preferences success notification text').toContainText(successMessage);
    await expect(this.adultsDropdown, 'Edit room preferences form adults dropdown').toBeHidden();
  }
}
