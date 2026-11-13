import { expect, type Locator } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { CcuiComponent } from '../baseCcui.component';
import { RoomDetailsContainerComponent } from './roomDetailsContainer.component';

/**
 * Room details section from the CCUI confirmation page.
 * Mirrors qa/reference/test/pages/components/ccui/confirmBooking/roomDetailsSection.js
 * and its common room-details section base component.
 */
export class RoomDetailsSectionComponent extends CcuiComponent {
  // ######## UI elements/properties ########

  /** Return the room-details section container. */
  readonly roomDetailsSectionContainer: Locator = this.page.locator('[data-testid="RoomDetailsSection"]');
  /** Return the localized room-details heading. */
  readonly roomDetailsLabel: Locator = this.roomDetailsSectionContainer.locator('p').filter({ hasText: Strings.ROOM_DETAILS.data.default ?? '' }).first();
  /** Return all room-card header containers in display order. */
  readonly roomDetailsContainers: Locator = this.roomDetailsSectionContainer.locator('div[data-testid^="RoomCardHeader-room"]');

  /**
   * Get a room-details container component by zero-based index.
   * @param roomDetailsIndex Zero-based room-details container index.
   * @returns The composed room-details container component.
   */
  getRoomDetailsContainerByIndex(roomDetailsIndex: number): RoomDetailsContainerComponent { return new RoomDetailsContainerComponent(this.roomDetailsContainers.nth(roomDetailsIndex), roomDetailsIndex); }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate that the room-details heading is displayed with its localized label. */
  async validateRoomDetailsLabel(): Promise<void> {
    console.log('Validate Room details label is correctly displayed');
    await this.roomDetailsLabel.scrollIntoViewIfNeeded();
    await expect(this.roomDetailsLabel, 'Room details label').toBeVisible();
    await expect(this.roomDetailsLabel, 'Room details label text').toContainText(await Strings.ROOM_DETAILS.name);
  }
}