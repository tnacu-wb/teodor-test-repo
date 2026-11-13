import { type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * One thumbnail item from the accessible gallery containing the UI elements, custom actions and validations.
 * Mirrors qa/reference `components/opera/chooseYourBathroom/accessibleGalleryThumbnailItem.js`.
 */
export class AccessibleGalleryThumbnailItemComponent {
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  async galleryRoomImage(): Promise<Locator> {
    return this.container.locator(`span img[alt*="${await Strings.ACCESSIBLE.name}"]`);
  }

  get galleryRoomDescriptionLabel(): Locator {
    return this.container.locator('> div:nth-child(2) > p');
  }

  get galleryRoomIcon(): Locator {
    return this.container.locator('> div:nth-child(2) [data-testid="svg-container"] svg');
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the gallery item visibility and its data. */
  async validateData({ descriptionLabel }: { descriptionLabel: string }): Promise<void> {
    console.log("Validate the item's elements are visible");

    await expect(await this.galleryRoomImage(), 'Image not displayed').toBeVisible();
    await expect(this.galleryRoomDescriptionLabel, 'Description not visible').toBeVisible();
    await expect(this.galleryRoomDescriptionLabel, 'Gallery room description label').toHaveText(descriptionLabel);
    await expect(this.galleryRoomIcon, 'Accessibility icon').toBeVisible();
  }
}
