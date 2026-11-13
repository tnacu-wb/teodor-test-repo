import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * The filters panel (search results page -> 'Filter by') on the legacy ("bart") Premier Inn web
 * application. Mirrors qa/reference `pages/bart/components/searchResults/filterPanel.js`.
 */
export class BartFilterPanelComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly filtersContainer: Locator = this.page.locator('filters');
  readonly closeButton: Locator = this.page.locator('filters-slide close-button');
  readonly filterPanelTitle: Locator = this.page.locator('#filter-header');
  readonly filtersPanelTitle: Locator = this.filterPanelTitle;
  readonly resetFiltersButton: Locator = this.page.locator('#reset-filters-btn');
  readonly parkingLabel: Locator = this.filtersContainer.locator('div > label').nth(1);
  readonly facilitiesLabel: Locator = this.filtersContainer.locator('div > label').nth(2);

  /** The filter checkbox label for the given filter text. */
  getFilterCheckboxByLabel(label: string): Locator {
    return this.page.locator(`text="${label}"`).locator('xpath=parent::label');
  }

  // ######## UI actions/navigation ########

  /** Reset all filters. */
  async resetFilters(): Promise<void> {
    console.log('Reset all filters');
    await this.resetFiltersButton.click();
  }

  /** Close the filters panel. */
  async closePanel(): Promise<void> {
    console.log('Close filters panel');
    await this.closeButton.click();
  }

  /** Check or uncheck a filter by its displayed label. */
  async applyFilter(filterLabel: string, shouldBeChecked = true): Promise<void> {
    const filterElement = this.getFilterCheckboxByLabel(filterLabel);
    const checkbox = filterElement.locator('input').first();
    const isChecked = await checkbox.isChecked();
    if (isChecked !== shouldBeChecked) {
      console.log(`${shouldBeChecked ? 'Checking' : 'Unchecking'} ${filterLabel}`);
      await filterElement.click();
    }
  }

  // ######## UI validations ########

  /** Validate the available filter controls and panel labels. */
  async validateData(): Promise<void> {
    console.log('Validate filter panel data');
    for (const filterLabel of [
      Strings.FREE_PARKING,
      Strings.CHARGEABLE_ON_SITE_PARKING,
      Strings.CHARGEABLE_OFF_SITE_PARKING,
    ]) {
      const filter = this.getFilterCheckboxByLabel(await filterLabel.name);
      await expect(filter.locator('input').first(), `Filter checkbox - ${await filterLabel.name}`).toBeEnabled();
    }
    await expect(this.filtersPanelTitle, 'Filter panel title').toContainText(await Strings.FILTERS_APPLIED_WHEN_SELECTED.name);
    await expect(this.resetFiltersButton, 'Reset filters button text').toContainText(await Strings.RESET_FILTERS.name);
  }
}
