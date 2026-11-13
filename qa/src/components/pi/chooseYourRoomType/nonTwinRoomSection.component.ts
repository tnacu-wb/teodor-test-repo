import { type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { type Room } from '@test-data/room';

/**
 * Non-twin room component that requires no room-type selection.
 * Mirrors qa/reference `components/opera/chooseYourRoomType/nonTwinRoomSection.js`.
 */
export class NonTwinRoomSectionComponent {
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

  get noTwinSelectionLabel(): Locator {
    return this.container.locator('p[data-testid="twin-no-selection-required"]');
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate data against room info. */
  async validateData(roomData: Room): Promise<void> {
    console.log('Validate non-twin room data');

    await this.roomContainer.scrollIntoViewIfNeeded();

    await expect(this.roomContainer, 'Room container').toBeVisible();
    await expect(this.roomNumberLabel, 'Room members number label').toContainText(`${await Strings.ROOM.name} ${roomData.roomNumber}`);

    let computedMembersLabel = `${roomData.adultsNumber} ${roomData.adultsNumber > 1 ? await Strings.ADULTS.name : await Strings.ADULT.name}`;
    if (roomData.childrenNumber > 0) {
      computedMembersLabel += `, ${roomData.childrenNumber} ${roomData.childrenNumber > 1 ? await Strings.CHILDREN.name : await Strings.CHILD.name}`;
    }
    await expect(this.roomMembersLabel, 'Room members label').toContainText(computedMembersLabel);
    await expect(this.noTwinSelectionLabel, 'No selection required label').toHaveText(await Strings.NO_SELECTION_REQUIRED.name);
  }
}
