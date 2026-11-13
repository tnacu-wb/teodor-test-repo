import { type Page, type Locator, expect } from '@playwright/test';
import { Constants } from '@test-data/constants';

/** A single hotel gallery photo shown in the modal. */
export interface HotelGalleryImage {
  imageSrc: string;
  caption: string;
  iconSrc: string;
}

/**
 * The gallery modal that shows up when the 'See all photos' button from a gallery section is pressed.
 * Mirrors qa/reference `components/opera/hotelDetails/galleryModal.js`.
 */
export class GalleryModalComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly modalWindow: Locator = this.page.locator('section[data-testid*="ModalContent"]');
  readonly closeModalButton: Locator = this.page.locator('button[data-testid*="ModalCloseButton"]');
  readonly photoImagesList: Locator = Constants.BROWSER_RESOLUTIONS.isDesktop()
    ? this.page.locator('div.slick-track > div:not(.slick-cloned) div span img')
    : this.page.locator('(//div[@class="slick-track"])[2]//div/img');
  readonly previousImageButton: Locator = this.page.locator('button[title="Previous"]');
  readonly nextImageButton: Locator = this.page.locator('button[title="Next"]');
  readonly currentImage: Locator = this.page.locator('div[aria-hidden="false"]').first().locator('img');
  readonly currentImageContainer: Locator = this.page.locator('div[aria-hidden="false"]').first();
  readonly photoCounterLabel: Locator = this.page.locator('[data-testid*="ModalTitle"]');
  readonly tagLabelsList: Locator = this.page.locator('div.slick-track > div:not(.slick-cloned) [data-testid^="carousel_"] p');

  /** Accessible icon for the photo at the given 1-based index. */
  accessibleIconByIndex(index: number): Locator {
    return this.page.locator('div.slick-track > div:not(.slick-cloned) [data-testid^="carousel_"]').nth(index - 1).locator('img, svg');
  }

  // ######## UI actions/navigation ########

  /** Click previous image button. */
  async clickPreviousImageButton(): Promise<void> {
    await this.previousImageButton.click();
  }

  /** Click next image button. */
  async clickNextImageButton(): Promise<void> {
    await this.nextImageButton.click();
  }

  /** Click on the photo thumbnail with the specified 0-based index. */
  async clickPhotoImageWithIndex(index: number): Promise<void> {
    await this.photoImagesList.nth(index).click();
  }

  /** Close the modal by clicking the 'X' button. */
  async closeModalByButton(): Promise<void> {
    await this.closeModalButton.click();
    await expect(this.modalWindow, 'Modal window visibility').not.toBeVisible();
  }

  /** Close the modal by pressing the Escape key. */
  async closeModalByEscape(): Promise<void> {
    await this.page.keyboard.press('Escape');
    await expect(this.modalWindow, 'Modal window visibility').not.toBeVisible();
  }

  // ######## UI validations ########

  /** Validate the modal window visibility. */
  async validateModalWindowIsDisplayed({ modalIsDisplayed = true }: { modalIsDisplayed?: boolean } = {}): Promise<void> {
    if (modalIsDisplayed) {
      await expect(this.modalWindow, 'Modal window visibility').toBeVisible();
    } else {
      await expect(this.modalWindow, 'Modal window visibility').not.toBeVisible();
    }
  }

  /**
   * Validate the expected data within this section for the currently displayed image.
   * @param galleryImage current image data to validate
   * @param gallerySize the total number of images in the gallery
   * @param currentIndex the current photo's 1-based index in the gallery
   */
  async validateData({
    galleryImage,
    gallerySize,
    currentIndex = 1,
  }: {
    galleryImage: HotelGalleryImage;
    gallerySize: number;
    currentIndex?: number;
  }): Promise<void> {
    console.log(`Validate gallery modal: image #${currentIndex}`);

    await expect(this.closeModalButton, 'Close modal button is NOT clickable').toBeEnabled();
    await expect(this.photoImagesList, 'Photo image list size does NOT match').toHaveCount(gallerySize);

    await expect(this.currentImage, 'Current image').toBeVisible();
    await expect(this.currentImageContainer, 'Current image container class attribute - slick-active').toHaveClass(/slick-active/);
    await expect(this.currentImage, 'Image URL').toHaveAttribute('srcset', new RegExp(escapeRegex(galleryImage.imageSrc)));

    await expect(this.tagLabelsList.nth(currentIndex - 1), 'Image caption').toHaveText(galleryImage.caption);

    if (galleryImage.iconSrc !== '') {
      await expect(this.accessibleIconByIndex(currentIndex), `Accessible icon for photo #${currentIndex}`).toBeVisible();
    }

    const totalCount = await this.photoImagesList.count();
    if (Constants.BROWSER_RESOLUTIONS.isDesktop()) {
      if (currentIndex === 1) {
        await expect(this.previousImageButton, 'Previous button for first image').not.toBeVisible();
      } else {
        await expect(this.previousImageButton, 'Previous image button').toBeEnabled();
      }

      if (currentIndex === totalCount) {
        await expect(this.nextImageButton, 'Next button for last image').not.toBeVisible();
      } else {
        await expect(this.nextImageButton, 'Next image button').toBeEnabled();
      }
    }
    await expect(this.photoCounterLabel, 'Current photo index').toHaveText(`${currentIndex}/${totalCount}`);
  }
}

function escapeRegex(value: string): string {
  return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}
