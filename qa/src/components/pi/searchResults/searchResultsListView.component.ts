import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { HotelCardComponent } from './hotelCard.component';

/**
 * Search Results Page list view component. Mirrors the PI-relevant subset of
 * qa/reference `components/common/searchResults/searchResultsListView.js`
 * (`components/opera/searchResults/searchResultsListView.js` adds no PI-specific overrides).
 */
export class SearchResultsListViewComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly hotelCardsContainer: Locator = this.page.locator(
    '.infinite-scroll-component, [data-testid="SRP-price-per-night-wrapper"]'
  );
  readonly hotelCardsList: Locator = this.hotelCardsContainer.locator('> div:has(a)');
  readonly hotelsFoundLabel: Locator = this.page.locator(
    'button[data-testid="SRP-controls-filter-by-button"] ~ p, button[data-testid="SRP-controls-filter-by-button"] + p'
  );
  readonly loadingLabel: Locator = this.page.locator('p[data-testid="SRP-loading"]');

  /** Get the number of hotel cards currently rendered in the DOM. */
  async getVisibleHotelsNumber(): Promise<number> {
    return this.hotelCardsList.count();
  }

  /** Get the total number of hotels found, from the "Hotels found" label if present. */
  async getHotelsNumber(): Promise<number> {
    if (!(await this.hotelsFoundLabel.isVisible())) {
      return this.getVisibleHotelsNumber();
    }
    const text = (await this.hotelsFoundLabel.textContent()) ?? '';
    const total = Number.parseInt(text.match(/\d+/)?.[0] ?? '', 10);
    return Number.isNaN(total) ? this.getVisibleHotelsNumber() : total;
  }

  /** Get the hotel card by index (0-based). */
  getSearchResultCardByIndex(index: number): HotelCardComponent {
    return new HotelCardComponent(this.hotelCardsList.nth(index));
  }

  /**
   * Get the hotel card by hotel name.
   * @param hotelName name of the hotel to search in the results
   * @returns the matching hotel card, or undefined if not found among currently rendered cards
   */
  async getSearchResultCardByName(hotelName: string): Promise<HotelCardComponent | undefined> {
    const count = await this.hotelCardsList.count();
    for (let index = 0; index < count; index++) {
      const card = this.getSearchResultCardByIndex(index);
      const cardName = (await card.nameLabel.textContent()) ?? '';
      if (cardName === hotelName || hotelName.includes(cardName.slice(0, Math.min(39, cardName.length)))) {
        return card;
      }
    }
    return undefined;
  }

  // ######## UI actions/navigation ########

  /** Open hotel details by clicking "View details" for the given hotel name. */
  async openHotelDetailsByHotelName(hotelName: string): Promise<void> {
    console.log(`Open hotel details for hotel=${hotelName}`);
    await this.scrollToHotelCardByName(hotelName);
    const hotelCard = await this.getSearchResultCardByName(hotelName);
    await hotelCard?.clickViewDetails();
  }

  /** Open hotel details by clicking "View details" on the hotel card at the given index. */
  async openHotelDetailsByIndex(index: number): Promise<void> {
    console.log(`Open hotel details for hotel index=${index}`);
    await this.getSearchResultCardByIndex(index).clickViewDetails();
  }

  /** Scroll to the hotel card at the given index (0-based), triggering lazy-loading of following cards. */
  async scrollToHotelCardIndex(index = 0): Promise<void> {
    console.log(`Scroll to hotel card index=${index}`);
    const hotelCard = this.getSearchResultCardByIndex(index);
    await hotelCard.hotelCard.scrollIntoViewIfNeeded();
  }

  /**
   * Scroll to the hotel card matching the given hotel name, loading further pages as needed.
   * Mirrors reference `scrollToHotelCardByName`.
   */
  async scrollToHotelCardByName(hotelName: string): Promise<void> {
    console.log(`Scroll to ${hotelName} hotel card`);
    await this.hotelCardsContainer.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
    const totalHotelsFound = await this.getHotelsNumber();

    let lastHotelCardIndex = (await this.hotelCardsList.count()) - 1;
    let loadingAttempts = 0;

    while ((await this.hotelCardsList.count()) <= totalHotelsFound && loadingAttempts < 10) {
      const hotelCard = await this.getSearchResultCardByName(hotelName);
      if (hotelCard) {
        await hotelCard.nameLabel.scrollIntoViewIfNeeded();
        break;
      }
      await this.scrollToHotelCardIndex(lastHotelCardIndex);
      await this.waitForLoadingHotelCards();

      const newLastHotelCardIndex = (await this.hotelCardsList.count()) - 1;
      if (lastHotelCardIndex === newLastHotelCardIndex) {
        loadingAttempts++;
        continue;
      }
      loadingAttempts = 0;
      lastHotelCardIndex = newLastHotelCardIndex;
    }

    if (!(await this.getSearchResultCardByName(hotelName))) {
      throw new Error(`${hotelName} hotel card was not found in Search Results Page after scrolling through all available hotel cards!`);
    }
  }

  /**
   * Display all available hotel cards (or up to a specific index), scrolling to trigger lazy-loading pagination.
   * @param specificHotelCardIndex if provided, only display cards up to (and including) this index
   */
  async displayHotelCards(specificHotelCardIndex?: number): Promise<void> {
    console.log(`Display hotel cards through index=${specificHotelCardIndex ?? 'all'}`);
    const totalHotelsFound = await this.getHotelsNumber();
    const numberOfHotelCardsToBeDisplayed =
      specificHotelCardIndex === undefined ? totalHotelsFound : specificHotelCardIndex + 1;

    let hotelCardsCount = await this.hotelCardsList.count();
    let lastHotelCardIndex = hotelCardsCount - 1;
    let loadingAttempts = 0;

    while (hotelCardsCount < numberOfHotelCardsToBeDisplayed && loadingAttempts < 10) {
      await this.scrollToHotelCardIndex(lastHotelCardIndex);
      await this.waitForLoadingHotelCards();

      hotelCardsCount = await this.hotelCardsList.count();
      const newLastHotelCardIndex = hotelCardsCount - 1;
      if (lastHotelCardIndex === newLastHotelCardIndex) {
        loadingAttempts++;
        continue;
      }
      loadingAttempts = 0;
      lastHotelCardIndex = newLastHotelCardIndex;
    }

    if (hotelCardsCount < numberOfHotelCardsToBeDisplayed) {
      throw new Error('Search results are not loaded in Search Results page!');
    }
  }

  /** Wait until the "loading" indicator for hotel cards has finished (if it briefly appears). */
  async waitForLoadingHotelCards(): Promise<void> {
    console.log('Wait for hotel cards loading to finish');
    await this.loadingLabel.waitFor({ state: 'visible', timeout: 2000 }).catch(() => {});
    await this.loadingLabel.waitFor({ state: 'hidden' });
  }

  /** Get hotel names from all hotel cards, scrolling to load all cards via lazy-loading. */
  async getAllSearchResultsHotelNames(): Promise<string[]> {
    console.log('Get all hotel names from search results');
    await this.displayHotelCards();
    const hotelNamesList: string[] = [];
    const count = await this.getVisibleHotelsNumber();
    for (let index = 0; index < count; index++) {
      const card = this.getSearchResultCardByIndex(index);
      await card.nameLabel.scrollIntoViewIfNeeded();
      hotelNamesList.push((await card.nameLabel.textContent()) ?? '');
    }
    return hotelNamesList;
  }

  // ######## UI validations ########

  /** Validate the list container visibility. */
  async validateListContainer({ isDisplayed }: { isDisplayed: boolean }): Promise<void> {
    console.log('Validate the list container');
    if (isDisplayed) {
      await expect(this.hotelCardsContainer, 'List container').toBeVisible();
    } else {
      await expect(this.hotelCardsContainer, 'List container').toBeHidden();
    }
  }

  /** Validate that all hotel cards are displayed and their number matches the total found. */
  async validateAllHotelCardsAreDisplayed(): Promise<void> {
    console.log('Validate all hotel cards are displayed');
    const hotelsFoundTotal = await this.getHotelsNumber();
    for (let index = 0; index < hotelsFoundTotal; index++) {
      await expect(this.hotelCardsList.nth(index), `hotel card index: ${index}`).toBeVisible();
    }
    await expect(this.hotelCardsList, 'Hotel cards list length').toHaveCount(hotelsFoundTotal);
  }

  /** Validate hotel names labels displayed on the page against an expected list. */
  async validateHotelNamesLabels({ expectedHotelNamesList }: { expectedHotelNamesList: string[] }): Promise<void> {
    console.log('Validate hotel names labels');
    const actualHotelNamesList = await this.getAllSearchResultsHotelNames();
    expect(actualHotelNamesList, 'Hotel names list is not as expected').toEqual(expectedHotelNamesList);
  }

  /** Validate the "Hotels found" label text. */
  async validateHotelsFoundLabel(expectedLabel?: string): Promise<void> {
    const label = expectedLabel ?? (await Strings.HOTELS_FOUND_GENERIC.name);
    console.log('Validate Hotels found Label');
    await expect(this.hotelsFoundLabel, `${label} label`).toContainText(label);
  }
}
