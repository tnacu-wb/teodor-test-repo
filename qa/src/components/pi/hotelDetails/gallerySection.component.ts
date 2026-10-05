import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { Constants } from '@test-data/constants';
import { ApiSlugsCalls } from '@api/graphql/apiSlugsCalls';
import { GalleryModalComponent } from './galleryModal.component';

/**
 * Hotel photo gallery section on the hotel details page containing the UI elements, custom
 * actions and validations. Mirrors qa/reference `components/opera/hotelDetails/gallerySection.js`.
 */
export class GallerySectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly imagesList: Locator = this.page.locator('div[data-testid="thumbnails"] img');
  readonly seeAllPhotosButton: Locator = this.page.locator('button[data-testid="hdp_hotelGallerySeeAllPhotos"]');
  readonly mobileSingleImage: Locator = this.page.locator('div[data-testid="singleThumbnail"] img');
  readonly mobileCountImagesLabel: Locator = this.page.locator('div[data-testid="hdp_hotelGalleryImagesCount"]');

  // UI components

  readonly galleryModal: GalleryModalComponent = new GalleryModalComponent();

  // ######## UI actions/navigation ########

  /** Click the thumbnail image with the given 0-based index. */
  async clickImageWithIndex(index: number): Promise<void> {
    await this.imagesList.nth(index).scrollIntoViewIfNeeded();
    await this.imagesList.nth(index).click();
  }

  /** Open the gallery modal (desktop: 'See all photos' button, mobile: single image tap). */
  async openGalleryModal(): Promise<void> {
    if (!Constants.BROWSER_RESOLUTIONS.isDesktop()) {
      await this.mobileSingleImage.click();
    } else {
      await this.seeAllPhotosButton.click();
      await this.galleryModal.validateModalWindowIsDisplayed();
    }
  }

  // ######## UI validations ########

  /** Validate the gallery thumbnails against the API photo gallery (falls back to fetching it). */
  async validateData(options: { apiGalleryPhotosCount?: number } = {}): Promise<void> {
    console.log('Validate gallery section');
    const apiGalleryPhotosCount = options.apiGalleryPhotosCount ?? (await ApiSlugsCalls.graphqlGetHotelPhotoGallery()).length;

    if (Constants.BROWSER_RESOLUTIONS.isDesktop()) {
      expect(apiGalleryPhotosCount, 'Gallery photos list size').toBeGreaterThanOrEqual(3);
      await expect(this.imagesList, 'Thumbnail images list size').toHaveCount(3);
      await expect(this.seeAllPhotosButton, `${await Strings.SEE_ALL_PHOTOS.name} button`).toBeEnabled();
    } else {
      await expect(this.mobileSingleImage, 'Mobile single thumbnail image').toBeVisible();
      await expect(this.mobileCountImagesLabel, 'Mobile image count label').toContainText(`${apiGalleryPhotosCount}`);
    }
  }
}
