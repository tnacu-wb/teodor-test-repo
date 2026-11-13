import { type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { type Room } from '@test-data/room';
import { TwinRoomRadioOptionComponent } from './twinRoomRadioOption.component';

/**
 * Twin room component within the 'Choose your twin room type' page.
 * Mirrors qa/reference `components/opera/chooseYourRoomType/twinRoomSection.js`.
 */
export class TwinRoomSectionComponent {
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  get roomContainer(): Locator {
    return this.container;
  }

  get roomNumberLabel(): Locator {
    return this.container.locator('h3[data-testid="twin-room-number"]');
  }

  get roomMembersLabel(): Locator {
    return this.container.locator('p[data-testid="twin-room-guest-numbers"]');
  }

  /** Room type radio options within this twin room section. */
  async roomTypesOptionsList(): Promise<TwinRoomRadioOptionComponent[]> {
    const options = this.container.locator('div.css-9qs6if');
    const count = await options.count();
    const list: TwinRoomRadioOptionComponent[] = [];
    for (let index = 0; index < count; index++) {
      list.push(new TwinRoomRadioOptionComponent(options.nth(index)));
    }
    return list;
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate room's data. */
  async validateData(roomData: Room): Promise<void> {
    console.log('Validate twin room section');

    await this.roomContainer.scrollIntoViewIfNeeded();

    await expect(this.roomContainer, 'Room container').toBeVisible();
    await expect(this.roomNumberLabel, 'Room number label').toContainText(
      `${await Strings.ROOM.name} ${roomData.roomNumber} ${await Strings.TWIN_ROOM.name}`
    );

    let computedMembersLabel = `${roomData.adultsNumber} ${roomData.adultsNumber > 1 ? await Strings.ADULTS.name : await Strings.ADULT.name}`;
    if (roomData.childrenNumber > 0) {
      computedMembersLabel += `, ${roomData.childrenNumber} ${roomData.childrenNumber > 1 ? await Strings.CHILDREN.name : await Strings.CHILD.name}`;
    }
    await expect(this.roomMembersLabel, 'Rooms members label').toContainText(computedMembersLabel);

    const roomTypesOptionsList = await this.roomTypesOptionsList();
    expect(roomTypesOptionsList.length, 'No room types options in array.').toBeGreaterThan(0);
    const isFirstRadioSelected = await roomTypesOptionsList[0].isOptionSelected();
    await roomTypesOptionsList[0].validateData({
      title: await Strings.TWIN_TWO_SINGLE_BEDS.name,
      description: await Strings.TWIN_TWO_SINGLE_BEDS_TEXT.name,
      isSelected: isFirstRadioSelected,
    });
    await roomTypesOptionsList[1].validateData({
      title: await Strings.TWIN_DOUBLE_BED_AND_SOFA.name,
      description: await Strings.TWIN_DOUBLE_BED_AND_SOFA_TEXT.name,
      isSelected: !isFirstRadioSelected,
    });
  }
}
