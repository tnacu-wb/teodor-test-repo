import { type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { type Room } from '@test-data/room';
import { AccessibleBathroomRadioOptionComponent } from './accessibleBathroomRadioOption.component';
import { NoRoomTypeNotificationComponent } from './noRoomTypeNotification.component';

/**
 * Bathroom chooser component for accessible rooms within the 'Choose your bathroom' page.
 * Mirrors qa/reference `components/opera/chooseYourBathroom/accessibbleRoomBathroomSection.js`.
 */
export class AccessibleRoomBathroomSectionComponent {
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  get roomContainer(): Locator {
    return this.container;
  }

  get roomNumberLabel(): Locator {
    return this.container.locator('h3[data-testid="accessible-room-number"]');
  }

  get roomMembersLabel(): Locator {
    return this.container.locator('p[data-testid="accessible-room-guest-numbers"]');
  }

  get dropdownContainer(): Locator {
    return this.container.locator('[data-testid="DropdownComp-Wrapper"]');
  }

  get dropdownSelectedOptionLabel(): Locator {
    return this.container.locator('[data-testid="DropdownComp-undefined-buttonSelectorLabel"]');
  }

  get dropdownAccessibleIcon(): Locator {
    return this.container.locator('[data-testid="DropdownComp-undefined-menuButtonText"] [data-testid="svg-container"] svg');
  }

  /** Build the list of room type radio options. */
  async roomTypesOptionsList(): Promise<AccessibleBathroomRadioOptionComponent[]> {
    const options = this.container.locator('[data-testid*="radio-box-wrapper_"]');
    const count = await options.count();
    const list: AccessibleBathroomRadioOptionComponent[] = [];
    for (let index = 0; index < count; index++) {
      list.push(new AccessibleBathroomRadioOptionComponent(options.nth(index)));
    }
    return list;
  }

  /** Room type options that are disabled (unavailable). */
  async unavailableRoomTypeOptions(): Promise<AccessibleBathroomRadioOptionComponent[]> {
    const filtered: AccessibleBathroomRadioOptionComponent[] = [];
    for (const item of await this.roomTypesOptionsList()) {
      if (await item.isOptionDisabled()) {
        filtered.push(item);
      }
    }
    return filtered;
  }

  // UI components

  noRoomAvailableNotification(): NoRoomTypeNotificationComponent {
    return new NoRoomTypeNotificationComponent(this.container);
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate room's data. */
  async validateData(roomData: Room): Promise<void> {
    console.log('Validate accessible room bathroom section');

    await this.roomContainer.scrollIntoViewIfNeeded();

    await expect(this.roomContainer, 'Room container').toBeVisible();
    await expect(this.roomNumberLabel, 'Room number label').toContainText(`${await Strings.ROOM.name} ${roomData.roomNumber}`);

    let computedMembersLabel = `${roomData.adultsNumber} ${roomData.adultsNumber > 1 ? await Strings.ADULTS.name : await Strings.ADULT.name}`;
    if (roomData.childrenNumber > 0) {
      computedMembersLabel += `, ${roomData.childrenNumber} ${roomData.childrenNumber > 1 ? await Strings.CHILDREN.name : await Strings.CHILD.name}`;
    }
    await expect(this.roomMembersLabel, 'Rooms members label').toContainText(computedMembersLabel);

    await expect(this.dropdownContainer, 'Dropdown container').toBeVisible();
    await expect(this.dropdownSelectedOptionLabel, 'Dropdown selected option label').toContainText(await Strings.ACCESSIBLE.name);
    await expect(this.dropdownAccessibleIcon, 'Accessible icon').toBeVisible();

    const roomTypesOptions = await this.roomTypesOptionsList();
    expect(roomTypesOptions.length, 'No room types options in array.').toBeGreaterThan(0);
    const isFirstRadioDisabled = await roomTypesOptions[0].isOptionDisabled();
    const isFirstRadioSelected = !isFirstRadioDisabled;
    await roomTypesOptions[0].validateData({
      title: await Strings.LOWERED_BATHROOM.name,
      description: await Strings.LOWERED_BATHROOM_TEXT.name,
      isSelected: isFirstRadioSelected,
    });
    await roomTypesOptions[1].validateData({
      title: await Strings.WET_ROOM.name,
      description: await Strings.WET_ROOM_TEXT.name,
      isSelected: !isFirstRadioSelected,
    });

    const unavailableRoomTypes = await this.unavailableRoomTypeOptions();
    expect(unavailableRoomTypes.length, 'Unexpected number of unavailable room types.').toBeLessThanOrEqual(1);
    if (unavailableRoomTypes.length > 0) {
      const roomTypeLabel = await unavailableRoomTypes[0].roomTypeLabel.innerText();
      await this.noRoomAvailableNotification().validateData({ roomTypeLabel });
    }
  }
}
