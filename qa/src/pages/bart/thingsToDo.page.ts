import { type Locator } from '@playwright/test';
import { BasePage } from '../shared/base.page';

/**
 * 'Things to Do' page of the legacy ("bart") Premier Inn web application. Mirrors qa/reference
 * `pages/bart/thingsToDo.page.js`. Inert/unwired - see session memory.
 */
export class ThingsToDoPage extends BasePage {
  static readonly DIT_URLS = [
    'https://www.premierinn.com/gb/en/things-to-do/inverness/loch-ness.html',
    'https://www.premierinn.com/gb/en/things-to-do/inverness/restaurants.html',
    'https://www.premierinn.com/gb/en/things-to-do/inverness/attractions.html',
    'https://www.premierinn.com/gb/en/things-to-do/inverness/cawdor-castle.html',
    'https://www.premierinn.com/gb/en/things-to-do/sunderland/restaurants.html',
  ];

  static readonly UAT_URLS = [
    'https://www.premierinn.com/gb/en/why/family.html',
    'https://www.premierinn.com/gb/en/short-breaks/family-breaks.html',
    'https://www.premierinn.com/gb/en/short-breaks/hiking-guide.html',
    'https://www.premierinn.com/gb/en/why/food.html',
    'https://www.premierinn.com/gb/en/short-breaks/cycling-trails.html',
  ];

  // ######## UI elements/properties ########

  readonly pageTitle: Locator = this.page.locator('h1');

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the current URL matches the expected 'things to do' URL pattern. */
  async validatePageUrl(): Promise<void> {
    console.log('Validating Things to Do URL');
    await this.page.waitForURL(url => url.href.includes('things-to-do') || url.href.includes('?') || url.href.includes('/short-breaks/'));
  }
}
