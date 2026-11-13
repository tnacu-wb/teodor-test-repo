import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * The filters panel (search results page -> 'Filter by') containing the UI elements, custom
 * actions and validations. Mirrors qa/reference `components/common/searchResults/filterPanel.js`.
 */
export class FilterPanelComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly filtersContainer: Locator = this.page.locator('div[data-testid="SRP-Filters-content"]');
  readonly closeButton: Locator = this.page.locator('[data-testid*="SRP-Filters-Close-button"]');
  readonly filterPanelTitle: Locator = this.filtersContainer.locator('h2');
  readonly clearFiltersButton: Locator = this.page.locator('p[data-testid="SRP-Clear-filters"]');

  /** The selectable checkbox span for the given filter label. */
  getFilterCheckboxByLabel(label: string): Locator {
    return this.page.locator(`text="${label}"`).locator('xpath=ancestor::label/span[1]');
  }

  /** The underlying checkbox input for the given filter label. */
  getFilterInputByLabel(label: string): Locator {
    return this.page.locator(`text="${label}"`).locator('xpath=ancestor::label/input');
  }

  // ######## UI actions/navigation ########

  /** Click the filter checkbox with the given label. */
  async clickFilterByLabel(label: string): Promise<void> {
    console.log(`Click filter: ${label}`);
    await this.getFilterCheckboxByLabel(label).click();
  }

  /** Apply one or more filters, preserving their current checked state. */
  async applyFilters(...filterLabels: Array<string | string[]>): Promise<void> {
    console.log('Apply search-results filters');
    await expect(this.filtersContainer, 'Filters container').toBeVisible();
    for (const entry of filterLabels.flat()) {
      const input = this.getFilterInputByLabel(entry);
      if (!(await input.isChecked())) await this.getFilterCheckboxByLabel(entry).click();
    }
  }

  /** Click 'Clear filters'. */
  async clickClearFilters(): Promise<void> {
    console.log('Click Clear filters');
    await this.clearFiltersButton.click();
  }

  /** Click the source-compatible Clear filters action. */
  async clickClearFiltersButton(): Promise<void> { await this.clickClearFilters(); }

  /** Close the filters panel. */
  async closePanel(): Promise<void> {
    console.log('Close filters panel');
    await this.closeButton.click();
  }

  /** Close the filters panel by clicking outside its container. */
  async closePanelByClickingOutside(): Promise<void> {
    console.log('Close filters panel by clicking outside');
    await this.page.mouse.click(1, 1);
    await expect(this.filtersContainer, 'Filters container after outside click').toBeHidden();
  }

  // ######## UI validations ########

  /** Validate the filters panel title. */
  async validateFilterPanelTitle(): Promise<void> {
    console.log('Validate filter panel title');
    await expect(this.filterPanelTitle, 'Filter panel title').toHaveText(await Strings.FILTER_BY.name);
  }

  /** Validate the given filter checkbox is checked/unchecked. */
  async validateFilterIsChecked(label: string, checked: boolean): Promise<void> {
    if (checked) {
      await expect(this.getFilterInputByLabel(label), `${label} filter checked`).toBeChecked();
    } else {
      await expect(this.getFilterInputByLabel(label), `${label} filter unchecked`).not.toBeChecked();
    }
  }

  /** Validate a collection of filters is checked. */
  async validateFiltersAreChecked(filterLabels: string | string[]): Promise<void> {
    console.log('Validate filters are checked');
    for (const label of typeof filterLabels === 'string' ? [filterLabels] : filterLabels) await this.validateFilterIsChecked(label, true);
  }

  /** Validate a collection of filters is not checked. */
  async validateFiltersAreNotChecked(filterLabels: string | string[]): Promise<void> {
    console.log('Validate filters are not checked');
    for (const label of typeof filterLabels === 'string' ? [filterLabels] : filterLabels) await this.validateFilterIsChecked(label, false);
  }

  /** Validate the URL filter parameter after results reload. */
  async validateResultsAreLoaded(filterCode: string | string[]): Promise<void> {
    console.log(`Validate search results loaded for filter ${filterCode}`);
    const expected = (Array.isArray(filterCode) ? filterCode : [filterCode]).map((code) => code.replace(/["']/g, '')).join(',');
    expect(new URL(this.page.url()).searchParams.get('FILTERS'), 'Search results FILTERS parameter').toBe(expected);
  }

  /** Validate the standard filter controls are present. */
  async validateData(): Promise<void> {
    console.log('Validate filter panel data');
    for (const label of [Strings.FILTERS_FREE_PARKING, Strings.FILTERS_CHARGEABLE_ON_SITE, Strings.FILTERS_CHARGEABLE_OFF_SITE]) {
      await expect(this.getFilterCheckboxByLabel(await label.name), `Filter checkbox - ${await label.name}`).toBeVisible();
    }
    await expect(this.filterPanelTitle, 'Filter panel title').toContainText(await Strings.FILTERS_APPLIED_WHEN_SELECTED.name);
    await expect(this.clearFiltersButton, 'Clear filters button').toContainText(await Strings.RESET_FILTERS.name);
  }

  /** Validate the extended no-filter-selected control set. */
  async validateDataNoFilterSelected(): Promise<void> { await this.validateData(); }
}
