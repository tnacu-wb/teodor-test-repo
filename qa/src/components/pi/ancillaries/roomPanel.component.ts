import { type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { type Room } from '@test-data/room';

const ID = 'AncillariesPage';

/**
 * One room panel from the vertical strip section on the Ancillaries page containing the UI
 * elements, custom actions and validations. Mirrors qa/reference
 * `components/opera/ancillaries/roomPanel.js`.
 */
export class AncillariesRoomPanelComponent {
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  get roomIndexLabel(): Locator {
    return this.container.locator(`p[data-testid*="${ID}-BookingSummary"][data-testid*="RoomInformation-RoomNumber"]`);
  }

  get noOfAdultsLabel(): Locator {
    return this.container.locator(`p[data-testid*="${ID}-BookingSummary"][data-testid*="RoomInformation-AdultsNumber"]`);
  }

  get noOfChildrenLabel(): Locator {
    return this.container.locator(`p[data-testid*="${ID}-BookingSummary"][data-testid*="RoomInformation-ChildrenNumber"]`);
  }

  get noMealsSelectedLabel(): Locator {
    return this.container.locator(`p[data-testid*="${ID}-BookingSummary"][data-testid*="RoomInformation-NoMealsSelected"]`);
  }

  get foodOptionsList(): Locator {
    return this.container.locator('p[data-testid*="Meal"]:not([data-testid*="NoMealsSelected"])');
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate this room panel's number matches the given room and its meal-selection state. */
  async validateData(room: Room, { hasMealsSelected = true }: { hasMealsSelected?: boolean } = {}): Promise<void> {
    console.log(`Validate room panel against room #${room.roomNumber} and hasMealsSelected ${hasMealsSelected}`);
    await this.roomIndexLabel.scrollIntoViewIfNeeded();
    await expect(this.roomIndexLabel, `Room ${room.roomNumber} label text`).toContainText(`Room ${room.roomNumber}`);
    if (hasMealsSelected) {
      await expect(this.foodOptionsList.first(), 'Selected meal options').toBeVisible();
    } else {
      await expect(this.noMealsSelectedLabel, 'No meals selected label').toBeVisible();
    }
  }
}
