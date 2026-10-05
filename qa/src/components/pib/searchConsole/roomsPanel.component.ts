import { type Locator, type Page } from "@playwright/test";
import type { Room } from "../../../test-data/room";
import { RoomPanelComponent } from "./roomPanel.component";

/**
 * The rooms panel from IB search bar
 */
export class RoomsPanelComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly roomsContainer: Locator = this.page.locator( '[data-testid="roomPickerMenu"], [data-testid="roomPickerMenu-variant"]', );
  readonly addRoomButton: Locator = this.page.locator('[data-testid="IB-Add-Room-Button"]');
  readonly doneButton: Locator = this.page.locator('[data-testid="IB-Room-Occupancy-Done-Button"]');

  /** Get one room panel according to the index. */
  getRoomPanelByIndex(index: number): RoomPanelComponent {
    return new RoomPanelComponent(this.roomsContainer, index);
  }

  // ######## UI actions/navigation ########
  /** Select the requested room configurations and close the room picker. */
  async selectRooms({ roomsList, clickDoneButton = true }: { roomsList: Room[]; clickDoneButton?: boolean }): Promise<void> {
    console.log(`Select IB rooms count=${roomsList.length}`);
    for (let index = 1; index < roomsList.length; index++) {
      await this.addRoomButton.click();
    }
    for (const [index, room] of roomsList.entries()) {
      await this.getRoomPanelByIndex(index).selectRoom({ room });
    }
    if (clickDoneButton) await this.doneButton.click();
  }

  /** Click the add-room control. */
  async clickAddAnotherRoomButton(): Promise<void> {
    console.log("Click IB add another room button");
    await this.addRoomButton.click();
  }

  /** Click the room-picker Done control. */
  async clickDoneButton(): Promise<void> {
    console.log("Click IB room-picker Done button");
    await this.doneButton.click();
  }

  // ######## UI validations ########
}
