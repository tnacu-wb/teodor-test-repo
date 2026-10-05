import { expect, type Locator, type Page } from "@playwright/test";
/** InnBusiness Room Preferences Section from My Profile page */
export class RoomPreferencesSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly roomPreferencesSectionTitleLabel: Locator = this.page.getByTestId( "ProfilePage-Room-Preferences-Title", );
  readonly roomPreferencesSectionDescriptionLabel: Locator = this.page.getByTestId("ProfilePage-Room-Preferences-Description");
  readonly editRoomPreferencesButton: Locator = this.page.getByTestId( "ProfilePage-Room-Preferences-Edit-Button", );
  readonly roomRequirementsForm: Locator = this.page.getByTestId( "Room-Requirements-Form", );
  readonly adultsLabel: Locator = this.page .locator('[data-testid="ProfilePage-Room-Preferences-Form"] label') .nth(0);
  readonly childrenLabel: Locator = this.page .locator('[data-testid="ProfilePage-Room-Preferences-Form"] label') .nth(2);
  readonly cotLabel: Locator = this.page .locator('[data-testid="ProfilePage-Room-Preferences-Form"] label') .nth(4);
  readonly roomTypeLabel: Locator = this.page .locator('[data-testid="ProfilePage-Room-Preferences-Form"] label') .nth(6);
  readonly adultsButton: Locator = this.page.getByTestId( "Adults-IB-Form-Select-Button", );
  readonly adultsOptionList: Locator = this.page.locator( '//div[contains(@data-testid,"Adults-IB-Form-Select-Dropdown")]//button', );
  readonly childrenButton: Locator = this.page.getByTestId( "Children-IB-Form-Select-Button", );
  readonly childrenOptionList: Locator = this.page.locator( '//div[contains(@data-testid,"Children-IB-Form-Select-Dropdown")]//button', );
  readonly cotButton: Locator = this.page.getByTestId( "CotRequired-IB-Form-Select-Button", );
  readonly roomTypeButton: Locator = this.page.getByTestId( "Type-IB-Form-Select-Button", );
  readonly roomTypeList: Locator = this.page.locator( '//div[contains(@data-testid,"Type-IB-Form-Select-Dropdown")]//button', );
  readonly saveChangesButton: Locator = this.page.getByTestId( "ProfilePage-Room-Preferences-Save-Button", );
  readonly cancelChangesButton: Locator = this.page.getByTestId( "ProfilePage-Room-Preferences-Cancel-Button", );
  /** Get the options available in Room type list. */
  getRoomTypeOptionsList(): Promise<string[]> {
    console.log("Get room type options list");
    return this.roomTypeList.allTextContents();
  }
  /** Get room type option button by room type ID. */
  getRoomTypeOptionButtonById(roomTypeId: string): Locator {
    return this.page.getByTestId(`Type-${roomTypeId}-Option`);
  }
  /** Get cot option button by cot required. */
  getCotRequiredOptionButton(isCotRequired: boolean): Locator {
    return this.page.getByTestId(`CotRequired-${isCotRequired}-Option`);
  }
  // ######## UI actions/navigation ########
  /** Click on Edit room preferences button. */
  async clickEditRoomPreferencesButton(): Promise<void> {
    console.log("Click on Edit room preferences button");
    await this.editRoomPreferencesButton.click();
    await expect( this.roomRequirementsForm, "Room requirements form", ).toBeVisible();
  }
  /** Select the number of children from Children options list. */
  async selectChildren({
    childrenNumber,
  }: {
    childrenNumber: number;
  }): Promise<void> {
    console.log("Select children");
    await this.childrenButton.click();
    await this.childrenOptionList.nth(childrenNumber).click();
  }
  /** Select the number of adults from Adults options list. */
  async selectAdults({
    adultsNumber,
  }: {
    adultsNumber: number;
  }): Promise<void> {
    console.log("Select adults");
    await this.adultsButton.click();
    await this.adultsOptionList.nth(adultsNumber - 1).click();
  }
  /** Click on room Type. */
  async clickRoomTypeButton(): Promise<void> {
    console.log("Click on Type room");
    await this.roomTypeButton.click();
  }
  /** Select the room type by id from room type options list. */
  async selectRoomTypeById({
    roomTypeId,
  }: {
    roomTypeId: string;
  }): Promise<void> {
    console.log("Select room type");
    await this.roomTypeButton.click();
    await this.getRoomTypeOptionButtonById(roomTypeId).click();
  }
  /** Select cot required from cot options list. */
  async selectCot({
    isCotRequired,
  }: {
    isCotRequired: boolean;
  }): Promise<void> {
    console.log("Select cot");
    await this.cotButton.click();
    await this.getCotRequiredOptionButton(isCotRequired).click();
  }
  /** Click on save changes button. */
  async clickSaveChangesButton(): Promise<void> {
    console.log("Click on save changes button");
    await this.saveChangesButton.click();
  }
  // ######## UI validations ########
  /** Validate view room preferences section. */
  async validateRoomPreferencesSection(): Promise<void> {
    console.log("Validate view room preferences section");
    await expect( this.roomPreferencesSectionTitleLabel, "Room preferences title", ).toBeVisible();
    await expect( this.editRoomPreferencesButton, "Edit room preferences button", ).toBeVisible();
  }
  /** Validate edit room preferences section. */
  async validateEditRoomPreferencesSection({
    roomPreferences: _roomPreferences,
  }: {
    roomPreferences?: unknown;
  }): Promise<void> {
    console.log("Validate edit room preferences section");
    await expect( this.roomRequirementsForm, "Room preferences form", ).toBeVisible();
  }
  /** Validate the expected data for Room type options list with children. */
  async validateRoomTypeOptionsList({
    childrenNumber: _childrenNumber,
    adultsNumber: _adultsNumber = 1,
  }: {
    childrenNumber: number;
    adultsNumber?: number;
  }): Promise<void> {
    console.log("Validate Room type options list");
    await expect(this.roomTypeList, "Room type options").not.toHaveCount(0);
  }
  /** Validate Room type option button label text. */
  async validateRoomType({
    roomTypeName,
  }: {
    roomTypeName: { name: string };
  }): Promise<void> {
    console.log("Validate Room type option button label text");
    await expect(this.roomTypeButton, "Room type button").toHaveText( roomTypeName.name, );
  }
}
