import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { GalleryModalComponent } from '../hotelDetails/galleryModal.component';
import { AccessibleGalleryThumbnailItemComponent } from './accessibleGalleryThumbnailItem.component';

/**
 * Photo gallery section displayed in the 'Choose your bathroom' page containing the UI elements, custom actions and validations.
 * Mirrors qa/reference `components/opera/chooseYourBathroom/accessibleGallerySection.js`.
 */
export class AccessibleGallerySectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  /** List of accessible gallery thumbnail items. */
  async thumbnailItems(): Promise<AccessibleGalleryThumbnailItemComponent[]> {
    const label = await Strings.ACCESSIBLE.name;
    const items = this.page.locator(`span img[alt*="${label}"] >> xpath=../../..`);
    const count = await items.count();
    const thumbnails: AccessibleGalleryThumbnailItemComponent[] = [];
    for (let index = 0; index < count; index++) {
      thumbnails.push(new AccessibleGalleryThumbnailItemComponent(items.nth(index)));
    }
    return thumbnails;
  }

  readonly seeAllPhotosButton: Locator = this.page.locator('button[data-testid="hdp_AccessibleGallerySeeAllPhotos"]');
  readonly sectionContainer: Locator = this.page.locator('section[data-testid="Section"]');

  // UI components

  readonly galleryModal: GalleryModalComponent = new GalleryModalComponent();

  // ######## UI actions/navigation ########

  /** Click the photo with the given 0-based index. */
  async clickImageWithIndex(index: number): Promise<void> {
    const thumbnails = await this.thumbnailItems();
    const image = await thumbnails[index].galleryRoomImage();
    await image.scrollIntoViewIfNeeded();
    await image.click();
  }

  /** Open photo gallery modal by clicking the 'See all photos' button. */
  async openGalleryModal(): Promise<void> {
    await this.seeAllPhotosButton.click();
    await this.galleryModal.validateModalWindowIsDisplayed();
  }

  // ######## UI validations ########

  /** Validate accessibility gallery elements' visibility and their data. */
  async validateData({ descriptionLabels }: { descriptionLabels: string[] }): Promise<void> {
    console.log('Validate gallery section');

    await expect(this.sectionContainer, 'Section container is not displayed').toBeVisible();

    const thumbnails = await this.thumbnailItems();
    expect(thumbnails.length, 'Gallery items array length').toBeGreaterThan(0);
    await this.validateNumberOfImages(descriptionLabels.length);
    for (const [index, item] of thumbnails.entries()) {
      await item.validateData({ descriptionLabel: descriptionLabels[index] });
    }

    await expect(this.seeAllPhotosButton, 'See all photos button is not displayed').toBeVisible();
    await expect(this.seeAllPhotosButton, `${await Strings.SEE_ALL_PHOTOS.name} button should be clickable`).toBeEnabled();
    await expect(this.seeAllPhotosButton, `${await Strings.SEE_ALL_PHOTOS.name} label`).toHaveText(await Strings.SEE_ALL_PHOTOS.name);
  }

  /** Validate the number of thumbnail images is the expected one. */
  async validateNumberOfImages(expectedNumberOfImages: number): Promise<void> {
    console.log('Validate the number of images is as expected');
    const thumbnails = await this.thumbnailItems();
    expect(thumbnails.length, 'Number of images not as expected.').toBe(expectedNumberOfImages);
  }
}
