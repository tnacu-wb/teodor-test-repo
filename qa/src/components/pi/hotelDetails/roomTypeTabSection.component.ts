import { type Page, type Locator, expect } from '@playwright/test';
import { RoomTypePanelComponent } from './roomTypePanel.component';

/**
 * One tab from the 'Our rooms' component containing the UI elements, custom actions and
 * validations. Mirrors qa/reference `components/opera/hotelDetails/roomTypeTabSection.js`.
 */
export class RoomTypeTabSectionComponent {
  private readonly page: Page = global.page;
  private readonly tabLabel: Locator;

  constructor(tabLabel: Locator) {
    this.tabLabel = tabLabel;
  }

  // ######## UI elements/properties ########

  get tabLabelLocator(): Locator {
    return this.tabLabel;
  }

  get tabLabelTooltip(): Locator {
    return this.page.locator('div[role="alert"] div p').nth(1);
  }

  /** Room type panels visible within this tab's (non-hidden) tab panel. */
  async tabPanelList(): Promise<RoomTypePanelComponent[]> {
    const panels = this.page.locator('div[data-testid="roomConfiguration-TabPanels"] > div:not([hidden]) > div > div');
    const count = await panels.count();
    return Array.from({ length: count }, (_, index) => new RoomTypePanelComponent(panels.nth(index)));
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the currently-selected tab label and its room panels are displayed. */
  async validateData({ expectedTabLabel }: { expectedTabLabel?: string } = {}): Promise<void> {
    console.log('Validate current tab panel');
    if (expectedTabLabel) {
      await expect(this.tabLabel, 'Current tab label').toContainText(expectedTabLabel);
    }
    const panels = await this.tabPanelList();
    expect(panels.length, 'Room type panels count').toBeGreaterThan(0);
  }
}
