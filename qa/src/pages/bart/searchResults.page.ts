import { type Locator } from '@playwright/test';
import { BartFilterPanelComponent } from '../../components/shared/bart/searchResults/filterPanel.component';
import { BartHotelCardComponent } from '../../components/shared/bart/searchResults/hotelCard.component';
import { BasePage } from '../shared/base.page';

/**
 * Search results page of the legacy ("bart") Premier Inn web application. Mirrors qa/reference
 * `pages/bart/searchResults.page.js`. Inert/unwired - see session memory.
 */
export class BartSearchResultsPage extends BasePage {
  static readonly HOTEL_CARDS_CONTAINER_SELECTOR = 'ol';
  static readonly HOTEL_CARD_SELECTOR = `${BartSearchResultsPage.HOTEL_CARDS_CONTAINER_SELECTOR} > li`;

  // ######## UI elements/properties ########

  readonly hotelCountLabel: Locator = this.page.locator('total-hotels span').nth(0);
  readonly hotelsFoundLabel: Locator = this.page.locator('total-hotels span').nth(1);
  readonly filterByBtn: Locator = this.page.locator('#filterButton');
  readonly sortDropdown: Locator = this.page.locator('select[name="results-sort-select"]');
  readonly sortDropdownOptionsList: Locator = this.sortDropdown.locator('option');
  readonly mapButton: Locator = this.page.locator('#mapButton');
  readonly hotelCardsList: Locator = this.page.locator(BartSearchResultsPage.HOTEL_CARD_SELECTOR);
  readonly hotelCardsContainer: Locator = this.page.locator(BartSearchResultsPage.HOTEL_CARDS_CONTAINER_SELECTOR);

  /** Hotel card component for the item at the given 0-based index. */
  getHotelCardByIndex(index: number): BartHotelCardComponent {
    return new BartHotelCardComponent(this.hotelCardsList.nth(index));
  }

  // UI components

  readonly filterPanel: BartFilterPanelComponent = new BartFilterPanelComponent();

  // ######## UI actions/navigation ########

  /** Click 'Filter by' to open the filters panel. */
  async openFilterPanel(): Promise<void> {
    await this.filterByBtn.click();
  }

  /** Click the map view button. */
  async clickMapButton(): Promise<void> {
    await this.mapButton.click();
  }

  /** Select a sort option by its visible text. */
  async selectSortOption(optionLabel: string): Promise<void> {
    await this.sortDropdown.selectOption({ label: optionLabel });
  }

  // ######## UI validations ########
}
