import { type Locator, expect } from '@playwright/test';

/**
 * One panel within the 'Our rooms' tab containing the UI elements, custom actions and validations.
 * Mirrors qa/reference `components/opera/hotelDetails/roomTypePanel.js` (simplified: facilities
 * modal/gallery composition delegated to `FacilitiesModalComponent`/`GalleryModalComponent`
 * directly rather than instantiated per-panel).
 */
export class RoomTypePanelComponent {
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  get roomTypeImage(): Locator {
    return this.container.locator('span img');
  }

  get viewGalleryButton(): Locator {
    return this.container.locator('button').first();
  }

  get mobileExpandButton(): Locator {
    return this.container.locator('div[data-testid="room-images__expand"]');
  }

  get roomTypeNameLabel(): Locator {
    return this.container.locator('p').first();
  }

  get roomTypeDescriptionLabel(): Locator {
    return this.container.locator('p').nth(1);
  }

  get roomFacilitiesList(): Locator {
    return this.container.locator('div[data-testid="room-facilities-wrap"] > div img');
  }

  get seeAllFacilitiesLink(): Locator {
    return this.container.locator('div[data-testid="room-facilities-wrap"] a');
  }

  // ######## UI actions/navigation ########

  /** Open the room's photo gallery modal. */
  async openRoomPhotoGallery(isDesktop: boolean): Promise<void> {
    if (isDesktop) {
      await this.viewGalleryButton.click();
    } else {
      await this.mobileExpandButton.scrollIntoViewIfNeeded();
      await this.mobileExpandButton.click();
    }
  }

  /** Open the modal listing all this room's facilities. */
  async openRoomFacilityModal(): Promise<void> {
    await this.seeAllFacilitiesLink.scrollIntoViewIfNeeded();
    await this.seeAllFacilitiesLink.click();
  }

  // ######## UI validations ########

  /** Validate this room panel's name and description against the API room-type panel data. */
  async validateData({ roomName, roomDescription }: { roomName: string; roomDescription: string }): Promise<void> {
    console.log('Validate panel');
    await expect(this.roomTypeNameLabel, 'Room title label').toContainText(roomName);
    await expect(this.roomTypeDescriptionLabel, 'Room description label').toContainText(roomDescription.trim());
  }
}
