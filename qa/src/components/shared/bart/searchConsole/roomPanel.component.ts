import { type Locator, expect } from '@playwright/test';
import { type Room, Rooms } from '@test-data/room';
import { Strings } from '@test-data/strings';
import { RoomTypesData } from '@test-data/roomTypes';

/**
 * One room panel item from the search bar's rooms picker on the legacy ("bart") Premier Inn web
 * application. Mirrors qa/reference `pages/bart/components/searchConsole/roomPanel.js`.
 */
export class BartRoomPanelComponent {
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  get roomTitleLabel(): Locator {
    return this.container.locator('div.room__title');
  }

  get removeRoomBtn(): Locator {
    return this.container.locator('button.room__remove-room');
  }

  get adultsLabel(): Locator {
    return this.container.locator('[id*="-adults__label"]');
  }

  get adultsBtn(): Locator {
    return this.container.locator('[id*="-adults"] button').first();
  }

  get adultsList(): Locator {
    return this.container.locator('ul[id*="-adults_list"] li[id*="-adults-"]');
  }

  get adultsHelperTextLabel(): Locator {
    return this.container.locator('[id*="-adults"] div[class*="wb-new-select__helper-text"]');
  }

  get childrenLabel(): Locator {
    return this.container.locator('[id*="-children__label"]');
  }

  get childrenBtn(): Locator {
    return this.container.locator('[id*="-children"] button').first();
  }

  get childrenList(): Locator {
    return this.container.locator('ul[id*="-children_list"] li[id*="-children-"]');
  }

  get childrenHelperTextLabel(): Locator {
    return this.container.locator('[id*="-children"] div[class*="wb-new-select__helper-text"]');
  }

  get cotSwitch(): Locator {
    return this.container.locator('label.wb-toggle');
  }

  get cotLabel(): Locator {
    return this.container.locator('label.wb-toggle__label');
  }

  get cotHelperTextLabel(): Locator {
    return this.container.locator('span.wb-toggle__span');
  }

  get roomTypeLabel(): Locator {
    return this.container.locator('[id*="-roomType__label"]');
  }

  get roomTypeBtn(): Locator {
    return this.container.locator('[id*="-roomType"] button').first();
  }

  get roomTypeList(): Locator {
    return this.container.locator('ul[id*="-roomType_list"] li[id*="-roomType-"]');
  }

  get adultsValueLabel(): Locator {
    return this.container.locator('[id*="-adults__value"]');
  }

  get childrenValueLabel(): Locator {
    return this.container.locator('[id*="-children__value"]');
  }

  // ######## UI actions/navigation ########

  /** Click the remove-room button for this room panel. */
  async clickRemoveRoom(): Promise<void> {
    console.log('Click remove room button');
    await this.removeRoomBtn.click();
  }

  /** Select a room type option by its displayed label. */
  async selectRoomType(roomType: string): Promise<void> {
    console.log(`Select room type: ${roomType}`);
    const option = this.roomTypeList.filter({ hasText: roomType }).first();
    await option.click();
  }

  /** Select the room occupancy, cot preference, and room type. */
  async selectRoom(roomData: Room): Promise<void> {
    console.log('Select room for data:', JSON.stringify(roomData));
    await this.adultsBtn.click();
    await this.adultsList.nth(roomData.adultsNumber - 1).click();
    await this.childrenBtn.click();
    await this.childrenList.nth(roomData.childrenNumber).click();

    const cotInput = this.cotSwitch.locator('input');
    if ((await cotInput.isChecked()) !== roomData.cotRequired) {
      await this.cotSwitch.click();
    }

    await this.roomTypeBtn.click();
    await this.selectRoomType(await roomData.roomType.name.name);
  }

  // ######## UI validations ########

  /** Validate the room title is displayed. */
  async validateRoomTitleIsDisplayed(): Promise<void> {
    console.log('Validate room title is displayed');
    await expect(this.roomTitleLabel, 'Room title').toBeVisible();
  }

  /** Validate the room panel controls and values against the expected room data. */
  async validateData(room: Room = Rooms.createRoom()): Promise<void> {
    console.log(`Validate room panel data for room ${room.roomNumber ?? 1}`);
    const roomNumber = room.roomNumber ?? 1;
    const maxAdults = 2;
    const maxChildren = 2;
    const roomTypes = room.childrenNumber > 0
      ? [await RoomTypesData.FAMILY.name.name]
      : [
          await RoomTypesData.SINGLE.name.name,
          await RoomTypesData.DOUBLE.name.name,
          await RoomTypesData.ACCESSIBLE.name.name,
        ];

    await expect(this.roomTitleLabel, 'Room title label').toContainText(`${await Strings.ROOM.name} ${roomNumber}`);
    await expect(this.adultsLabel, 'Adults label').toContainText(await Strings.ADULTS.name);
    await expect(this.adultsBtn, 'Room adults value').toHaveText(`${room.adultsNumber}`);
    await this.adultsBtn.click();
    await expect(this.adultsList, 'Adults list size').toHaveCount(maxAdults);
    for (let index = 0; index < maxAdults; index++) {
      const expectedAdultsValue = `${index + 1} ${index === 0 ? await Strings.ADULT_LOWER_CASE.name : await Strings.ADULTS_LOWER_CASE.name}`;
      await expect(this.adultsList.nth(index), 'Adults valid value').toHaveText(expectedAdultsValue);
    }
    await this.adultsBtn.click();
    await expect(this.adultsHelperTextLabel, 'Adults helper text label').toContainText(await Strings.MAX_TWO_PER_ROOM_GENERIC.name);

    await expect(this.childrenLabel, 'Children label').toContainText(await Strings.CHILDREN.name);
    await expect(this.childrenBtn, 'Room children value').toHaveText(`${room.childrenNumber}`);
    await this.childrenBtn.click();
    await expect(this.childrenList, 'Children list size').toHaveCount(maxChildren + 1);
    for (let index = 0; index <= maxChildren; index++) {
      const expectedChildrenValue = `${index} ${index === 1 ? await Strings.CHILD_LOWER_CASE.name : await Strings.CHILDREN_LOWER_CASE.name}`;
      await expect(this.childrenList.nth(index), 'Children valid value').toHaveText(expectedChildrenValue);
    }
    await this.childrenBtn.click();
    await expect(this.childrenHelperTextLabel, 'Children helper text label').toContainText(await Strings.TWO_FIFTEEN_YEARS_GENERIC.name);

    await expect(this.cotSwitch.locator('input'), 'Cot switch on status').toBeChecked({ checked: room.cotRequired });
    await expect(this.cotLabel, 'Cot label').toContainText(await Strings.INCLUDE_A_COT_GENERIC.name);
    await expect(this.cotHelperTextLabel, 'Cot helper text label').toContainText('0-2 years');

    await expect(this.roomTypeLabel, 'Room type label').toContainText(await Strings.ROOM_TYPE.name);
    await expect(this.roomTypeBtn, 'Room type value').toHaveText(await room.roomType.name.name);
    await this.roomTypeBtn.click();
    await expect(this.roomTypeList, 'Room type list size').toHaveCount(roomTypes.length);
    for (let index = 0; index < roomTypes.length; index++) {
      await expect(this.roomTypeList.nth(index), 'Room type valid value').toHaveText(roomTypes[index]);
    }
    await this.roomTypeBtn.click();
  }
}
