import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * The restaurant section within the hotel details page containing the UI elements, custom
 * actions and validations. Mirrors qa/reference
 * `components/opera/hotelDetails/restaurantSection.js` (simplified: per-menu-name PDF/read-more
 * link lookups are exposed as parameterised locator methods rather than the reference's async
 * getter-per-call pattern).
 */
export class RestaurantSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly restaurantContainer: Locator = this.page.locator(
    'div[data-testid="restaurantConfiguration-TabsComponent"], [data-testid*="-menu"]'
  );
  readonly titleLabel: Locator = this.page.locator('h3[data-testid="hotel-restaurant-section"]');
  readonly logoImage: Locator = this.page.locator('img[data-testid="hotel-restaurant-logo"]');
  readonly foodTabsList: Locator = this.page.locator('div[role="tablist"] h4');
  readonly presentationImagesList: Locator = this.page.locator('[data-testid*="-menu-image"] img');
  readonly menuButtonList: Locator = this.page.locator('div[data-testid="hdp_restaurantMenuContent"] button');

  /** Menu item description paragraphs for the given menu name. */
  menuDescriptionLabelsByName(menuName: string): Locator {
    return this.page.locator(`div[data-testid="${menuName}-menu"] p`);
  }

  /** PDF menu button for the given menu name. */
  pdfMenuButtonByName(menuName: string): Locator {
    return this.page.locator(`div[data-testid="${menuName}-menu"] button`);
  }

  /** 'Read more/less' link for the given menu name. */
  readMoreLessLinkByName(menuName: string): Locator {
    return this.page.locator(`div[data-testid="${menuName}-menu"] div[data-testid="hdp_restaurantMenuContent"] a`);
  }

  // ######## UI actions/navigation ########

  /** Scroll the restaurant logo image into view. */
  async scrollToRestaurantLogoImage(): Promise<void> {
    await this.logoImage.scrollIntoViewIfNeeded();
  }

  /** Click the food tab with the given 0-based index. */
  async clickFoodTabWithIndex(index: number): Promise<void> {
    await this.foodTabsList.nth(index).click();
  }

  // ######## UI validations ########

  /** Validate the restaurant section title and logo are displayed. */
  async validateData(): Promise<void> {
    console.log('Validate restaurant section');
    await expect(this.titleLabel, 'Restaurant section title').toHaveText(await Strings.RESTAURANT.name);
    await expect(this.logoImage, 'Restaurant logo').toBeVisible();
  }
}
