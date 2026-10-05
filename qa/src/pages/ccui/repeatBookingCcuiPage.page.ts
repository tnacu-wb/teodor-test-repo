import { expect, type Locator } from '@playwright/test';
import { HeaderSectionComponent } from '../../components/ccui/homePage/headerSection.component';
import { SearchConsoleComponent } from '../../components/ccui/searchConsole/searchConsole.component';
import { BasePage } from '../shared/base.page';

/**
 * Repeat Booking page from Opera CCUI environment containing the UI elements, custom actions and validations.
 */
export class RepeatBookingCcuiPage extends BasePage {
  // ######## properties ########

  // ######## UI elements/properties ########

  readonly repeatBookingLabel: Locator = this.page.locator('//div[@data-testid="repeat-booking-page-title"]');
  readonly selectedBookingLabel: Locator = this.page.locator('//div[@data-testid="repeat-booking-page-title"]/following-sibling::p');
  readonly covidNotificationLabel: Locator = this.page.locator('//div[@data-testid="AlertColumn"]');

  // UI components

  readonly searchConsole: SearchConsoleComponent = new SearchConsoleComponent();
  readonly headerSection: HeaderSectionComponent = new HeaderSectionComponent();

  // ######## UI actions/navigation ########

  /**
   * Continues from Ancillaries to Guest Details
   */
  async continueFromAncillariesToGuestDetails(): Promise<void> {
    console.log('Continues from Ancillaries to Guest Details');
    await global.ccuiPages.ancillariesCcuiPage.clickContinueButton();
    await expect(this.page, 'Repeat booking should leave ancillaries after continue').not.toHaveURL(/\/ancillaries/, { timeout: 30000 });
  }

  // ######## UI validations ########

  /**
   * Validate that the header is displayed on the home page.
   * @param isDisplayed true -> element is displayed / false -> element not displayed.
   */
  async validateHeaderContainerIsDisplayed(isDisplayed: boolean): Promise<void> {
    console.log('Validate header container display state');
    if (isDisplayed) {
      await expect(this.headerSection.headerContainer, 'Header').toBeVisible();
    } else {
      await expect(this.headerSection.headerContainer, 'Header').toBeHidden();
    }
  }

  /**
   * Validate selected booking title is displayed.
   * @param isDisplayed true -> element is displayed / false -> element not displayed.
   */
  async validateSelectedBookingTitleIsDisplayed(isDisplayed: boolean): Promise<void> {
    console.log('Validate selected booking title is displayed');
    if (isDisplayed) {
      await expect(this.selectedBookingLabel, 'Selected Booking title').toBeVisible();
    } else {
      await expect(this.selectedBookingLabel, 'Selected Booking title').toBeHidden();
    }
  }

  /**
   * Validate Covid 19 Notification is displayed.
   * @param isDisplayed true -> element is displayed / false -> element not displayed.
   */
  async validateCovid19NotificationIsDisplayed(isDisplayed: boolean): Promise<void> {
    console.log('Validate Covid 19 Notification is displayed');
    if (isDisplayed) {
      await expect(this.covidNotificationLabel, 'Covid 19 Notification').toBeVisible();
    } else {
      await expect(this.covidNotificationLabel, 'Covid 19 Notification').toBeHidden();
    }
  }

  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log('Validate Repeat Booking page was reached');
    await expect(this.repeatBookingLabel, 'Repeat booking label').toBeVisible();
  }
}