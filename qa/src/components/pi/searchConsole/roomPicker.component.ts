import { type Page, type Locator } from '@playwright/test';

/**
 * Room Picker sub-component within the search console (PI / non-IB variant).
 * Selectors mirror qa/reference roomsPanelBase.js + roomPanel.js (non-'pib' branch).
 */
export class RoomPickerComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly dropdownButton: Locator = this.page.locator('button[data-testid="DropdownComp-roomPicker-dropdown-menuButton"]');
  readonly dropdownPanel: Locator = this.page.locator('[data-testid="roomPickerMenu"], [data-testid="roomPickerMenu-variant"]');
  readonly doneButton: Locator = this.dropdownPanel.locator('button[data-testid="doneButton"]');
  readonly addRoomButton: Locator = this.dropdownPanel.locator('[data-testid="addMoreRooms"]');

  // ######## UI actions/navigation ########

  /** Container for a single room's controls within the dropdown panel (0-based index). */
  roomContainer(roomIndex: number): Locator {
    return this.dropdownPanel.locator('[role="menuitem"]').nth(roomIndex);
  }

  /** Select the adults count for the given room (0-based index) via its dropdown. */
  async selectAdults(roomIndex: number, adults: number): Promise<void> {
    console.log(`Select ${adults} adults for room index=${roomIndex}`);
    const room = this.roomContainer(roomIndex);
    await room.locator('button[data-testid*="-adults-menuButton"]').click();
    await room.locator('button[data-testid*="-adults-"]:not([data-testid$="-menuButton"])').nth(adults - 1).click();
  }

  /** Select the children count for the given room (0-based index) via its dropdown. */
  async selectChildren(roomIndex: number, children: number): Promise<void> {
    console.log(`Select ${children} children for room index=${roomIndex}`);
    const room = this.roomContainer(roomIndex);
    await room.locator('button[data-testid*="-children-menuButton"]').click();
    await room.locator('button[data-testid*="dropdownContent-children"]').nth(children).click();
  }

  // ######## UI validations ########
}
