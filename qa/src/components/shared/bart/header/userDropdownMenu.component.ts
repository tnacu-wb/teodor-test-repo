import { type Page, type Locator, expect } from '@playwright/test';
import { Constants } from '@test-data/constants';
import { Strings } from '@test-data/strings';
import { AccountSettingsPageComponent } from '../accountSettings/settings.component';

/**
 * The user dropdown menu, opened by clicking the logged-in user's name on the header, on the
 * legacy ("bart") Premier Inn web application. Mirrors qa/reference
 * `pages/bart/components/header/UserDropdonwMenu.js` (PI-only branch; the BB-specific branch is
 * out of scope).
 */
export class BartUserDropdownMenuComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly bookingsButton: Locator = this.page.locator(
    '[data-testid="BookingsButton"], [data-testid="BookingsButton-Mobile"], div.dropdown-menu.show > a:nth-child(1)'
  );
  readonly settingsButton: Locator = this.page.locator(
    '[data-testid="SettingsButton"], div.dropdown-menu.show > a:nth-child(2)'
  );
  readonly logOutButton: Locator = this.page.locator('[data-testid="LogoutButton"], div.dropdown-menu.show a:has-text("Log out")');

  readonly settings: AccountSettingsPageComponent = new AccountSettingsPageComponent();

  // ######## UI actions/navigation ########

  /** Click 'Bookings' in the user dropdown menu. */
  async clickBookings(): Promise<void> {
    console.log('Click Bookings button');
    await this.bookingsButton.click();
  }

  /** Click the source-compatible Bookings action. */
  async clickBookingsButton(): Promise<void> {
    console.log('Click source-compatible Bookings button');
    await this.clickBookings();
  }

  /** Click 'Settings' in the user dropdown menu. */
  async clickSettings(): Promise<void> {
    console.log('Click Settings button');
    await this.settingsButton.click();
  }

  /** Click the source-compatible Settings action. */
  async clickSettingsButton(): Promise<void> {
    console.log('Click source-compatible Settings button');
    await this.clickSettings();
  }

  /** Click 'Log out' in the user dropdown menu. */
  async clickLogOut(): Promise<void> {
    console.log('Click Log out button');
    await this.logOutButton.click();
  }

  /** Click the source-compatible Log out action. */
  async clickLogOutButton(): Promise<void> {
    console.log('Click source-compatible Log out button');
    await this.clickLogOut();
  }

  // ######## UI validations ########

  /** Validate the user dropdown buttons and their desktop ordering. */
  async validateDropdownMenu(): Promise<void> {
    console.log('Validate user dropdown menu');
    await expect(this.bookingsButton, 'Bookings button').toBeVisible();
    await expect(this.bookingsButton, 'Bookings button label').toContainText(await Strings.BOOKINGS_GENERIC.name);
    await expect(this.settingsButton, 'Settings button').toBeVisible();
    await expect(this.settingsButton, 'Settings button label').toContainText(await Strings.SETTINGS_GENERIC.name);
    await expect(this.logOutButton, 'Log out button').toBeVisible();
    await expect(this.logOutButton, 'Log out button label').toContainText(await Strings.LOGOUT.name);

    if (Constants.BROWSER_RESOLUTIONS.isDesktop()) {
      const bookingsBox = await this.bookingsButton.boundingBox();
      const logOutBox = await this.logOutButton.boundingBox();
      expect(bookingsBox, 'Bookings button position').not.toBeNull();
      expect(logOutBox, 'Log out button position').not.toBeNull();
      if (bookingsBox && logOutBox) {
        expect(logOutBox.y, 'Log out button should be below Bookings button').toBeGreaterThan(bookingsBox.y);
      }
    }
  }
}
