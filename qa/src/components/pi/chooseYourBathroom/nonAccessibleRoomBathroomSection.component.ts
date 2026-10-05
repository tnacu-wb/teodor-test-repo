import { type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { type Room } from '@test-data/room';

/**
 * Non-accessible room component that requires no bathroom selection.
 * Mirrors qa/reference `components/opera/chooseYourBathroom/nonAccessibleRoomBathroomSection.js`.
 */
export class NonAccessibleRoomBathroomSectionComponent {
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

  get noBathroomSelectionLabel(): Locator {
    return this.container.locator('p[data-testid="accessible-no-selection-required"]');
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate data against room info. */
  async validateData(roomData: Room): Promise<void> {
    console.log('Validate non-accessible room data');

    await this.roomContainer.scrollIntoViewIfNeeded();

    await expect(this.roomContainer, 'Room container').toBeVisible();
    await expect(this.roomNumberLabel, 'Room number not expected').toContainText(`${await Strings.ROOM.name} ${roomData.roomNumber}`);

    let computedMembersLabel = `${roomData.adultsNumber} ${roomData.adultsNumber > 1 ? await Strings.ADULTS.name : await Strings.ADULT.name}`;
    if (roomData.childrenNumber > 0) {
      computedMembersLabel += `, ${roomData.childrenNumber} ${roomData.childrenNumber > 1 ? await Strings.CHILDREN.name : await Strings.CHILD.name}`;
    }
    await expect(this.roomMembersLabel, 'Room members label').toContainText(computedMembersLabel);

    await expect(this.noBathroomSelectionLabel, 'No bathroom selection label').toHaveText(await Strings.NO_BATHROOM_SELECTION.name);
  }
}
