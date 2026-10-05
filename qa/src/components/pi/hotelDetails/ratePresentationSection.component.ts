import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * Description section of a rate, containing the UI elements, custom actions and validations.
 * Mirrors qa/reference `components/opera/hotelDetails/ratePresentationSection.js` (trivial PI
 * override) folded with `components/common/hotelDetails/ratePresentationSectionBase.js`.
 */
export class RatePresentationSectionComponent {
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  get roomTypeTitleLabel(): Locator {
    return this.container.locator('b[data-testid="hdp_roomTypeTitle"]');
  }

  get roomTypeDescriptionLabel(): Locator {
    return this.container.locator('p[data-testid="hdp_roomTypeDescription"]');
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the room type title/description for this rate presentation. */
  async validateData({ roomTypeName, roomTypeDescription }: { roomTypeName?: string; roomTypeDescription?: string } = {}): Promise<void> {
    console.log('Validate rate presentation section');
    await expect(this.roomTypeTitleLabel, 'Room type title').toBeVisible();
    if (roomTypeName) {
      await expect(this.roomTypeTitleLabel, 'Room type title text').toContainText(roomTypeName);
    }
    if (roomTypeDescription) {
      await expect(this.roomTypeDescriptionLabel, 'Room type description text').toContainText(roomTypeDescription);
    }
  }
}
