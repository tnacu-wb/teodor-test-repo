import { type Page, type Locator, expect } from '@playwright/test';
import { type HotelFacility } from '@api/response/hotelFacility';
import { FacilityModalRowComponent } from './facilityModalRow.component';

/**
 * The facilities modal that shows up when the 'See all' button from a facilities section is pressed.
 * Mirrors qa/reference `components/opera/hotelDetails/facilitiesModal.js`.
 */
export class FacilitiesModalComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly modal: Locator = this.page.locator('section[data-testid="facilities-modal-ModalContent"]');
  readonly modalTitleLabel: Locator = this.page.locator('div[data-testid="facilities-modal-ModalTitle"]');
  readonly closeModalButton: Locator = this.page.locator('button[data-testid="facilities-modal-ModalCloseButton"]');
  readonly rowContainersList: Locator = this.page.locator('div[data-testid="facilities-list"] > div');

  /** Facility row components for each facility in the modal. */
  async facilitiesList(): Promise<FacilityModalRowComponent[]> {
    const count = await this.rowContainersList.count();
    return Array.from({ length: count }, (_, index) => new FacilityModalRowComponent(this.rowContainersList.nth(index)));
  }

  // ######## UI actions/navigation ########

  /** Close the modal via the close button. */
  async closeModal(): Promise<void> {
    await this.closeModalButton.click();
    await expect(this.modal).not.toBeVisible();
  }

  // ######## UI validations ########

  /** Validate the facilities modal title, close button, and content against the API facility list. */
  async validateData({ apiHotelFacilities }: { apiHotelFacilities: HotelFacility[] }): Promise<void> {
    console.log('Validate facility modal');
    await this.validateModalTitle();
    await expect(this.closeModalButton, 'Close modal button').toBeEnabled();

    const rows = await this.facilitiesList();
    await expect(this.rowContainersList, 'Facilities modal row count').toHaveCount(apiHotelFacilities.length);
    for (const [index, facility] of apiHotelFacilities.entries()) {
      await rows[index].validateFromApi(facility);
    }
  }

  /** Validate the modal's visibility. */
  async validateModalVisibility(isModalDisplayed = false): Promise<void> {
    console.log('Validate facility modal visibility');
    if (isModalDisplayed) {
      await expect(this.modal, 'Facilities modal').toBeVisible();
    } else {
      await expect(this.modal, 'Facilities modal').not.toBeVisible();
    }
  }

  /** Validate the modal title text. */
  async validateModalTitle(): Promise<void> {
    await expect(this.modalTitleLabel, 'Facilities modal title').toBeVisible();
  }
}
