import { type Page, type Locator, expect } from '@playwright/test';
import { RoomTypeTabSectionComponent } from './roomTypeTabSection.component';

/**
 * 'Our rooms' component from the hotel details page containing the UI elements, custom actions
 * and validations. Mirrors qa/reference `components/opera/hotelDetails/ourRoomsComponent.js`.
 */
export class OurRoomsComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly ourRoomsLabel: Locator = this.page.locator('div#hotel-details-our-rooms h3');
  readonly currentTabButton: Locator = this.page.locator(
    'div[data-testid="roomConfiguration-TabsComponent"] button[role="tab"][aria-selected="true"][data-testid*="-TabButton"]'
  );
  readonly tabsList: Locator = this.page.locator(
    'div[data-testid="roomConfiguration-TabsComponent"] button[role="tab"] h4[data-testid*="-TabButton"]'
  );

  get currentTabPanel(): RoomTypeTabSectionComponent {
    return new RoomTypeTabSectionComponent(this.currentTabButton.locator('h4[data-testid*="-TabButton"]'));
  }

  // ######## UI actions/navigation ########

  /** Click the room-type tab with the given 0-based index. */
  async clickOnTabItemWithIndex(index: number): Promise<void> {
    await this.tabsList.nth(index).scrollIntoViewIfNeeded();
    await this.tabsList.nth(index).click();
  }

  // ######## UI validations ########

  /** Validate the 'Our rooms' section title and the currently-selected tab panel. */
  async validateData({ expectedTabLabel }: { expectedTabLabel?: string } = {}): Promise<void> {
    console.log('Validate current panel');
    await this.ourRoomsLabel.scrollIntoViewIfNeeded();
    await this.validateSectionTitle();
    await this.currentTabPanel.validateData({ expectedTabLabel });
  }

  /** Validate the section title text. */
  async validateSectionTitle(): Promise<void> {
    await expect(this.ourRoomsLabel, 'Our rooms section title').toBeVisible();
  }
}
