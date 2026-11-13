import { type Page, type Locator } from '@playwright/test';
import { type Room } from '@test-data/room';
import { Strings } from '@test-data/strings';
import { BartRoomPanelComponent } from './roomPanel.component';

/**
 * The rooms panel from the search bar on the legacy ("bart") Premier Inn web application.
 * Mirrors qa/reference `pages/bart/components/searchConsole/roomsPanel.js`.
 */
export class BartRoomsPanelComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly roomsContainer: Locator = this.page.locator('div#room-picker');
  readonly addRoomBtn: Locator = this.roomsContainer.locator('button.wb-btn-circle.plus');
  readonly addAnotherRoomLabel: Locator = this.roomsContainer.locator('div[class*="room-picker-menu__add-room-text"]');
  readonly doneBtn: Locator = this.roomsContainer.locator('> button');
  readonly roomPanelList: Locator = this.roomsContainer.locator('div.room');

  /** Room panel component for the room at the given 0-based index. */
  getRoomPanelByIndex(index: number): BartRoomPanelComponent {
    return new BartRoomPanelComponent(this.roomPanelList.nth(index));
  }

  // ######## UI actions/navigation ########

  /** Click 'Add another room'. */
  async clickAddRoom(): Promise<void> {
    console.log('Add another room');
    await this.addRoomBtn.click();
  }

  /** Click 'Done' to close the rooms panel. */
  async clickDone(): Promise<void> {
    console.log('Click Done on rooms panel');
    await this.doneBtn.click();
  }

  /** Select each requested room, adding room panels as needed. */
  async selectRooms(roomsData: Room[]): Promise<void> {
    console.log('Select rooms for given data');
    for (const [index, roomData] of roomsData.entries()) {
      if (index > 0) {
        await this.clickAddRoom();
      }
      await this.getRoomPanelByIndex(index).selectRoom(roomData);
    }
  }

  // ######## UI validations ########

  /** Validate the rooms panel controls and expected room count. */
  async validateData({ roomsCount = 1 }: { roomsCount?: number } = {}): Promise<void> {
    console.log(`Validate rooms panel data for ${roomsCount} room(s)`);
    await this.addRoomBtn.waitFor({ state: 'visible' });
    await expect(this.addAnotherRoomLabel, 'Add another room label').toContainText(await Strings.ADD_ANOTHER_ROOM.name);
    await expect(this.roomPanelList, 'Rooms list size').toHaveCount(roomsCount);
    await expect(this.doneBtn, 'Done button label').toHaveText(await Strings.DONE.name);
  }
}
