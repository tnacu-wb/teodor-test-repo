import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { LeadGuestContainerComponent } from './leadGuestContainer.component';

/**
 * The Lead Guest section (Guest Details) containing the UI elements, custom actions and validations.
 * Mirrors qa/reference `components/opera/guestDetails/leadGuestSection.js`.
 */
export class LeadGuestSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly leadGuestCheckbox: Locator = this.page.locator('div[data-testid="GuestDetails-leadGuest-Checkbox"] span');
  readonly leadGuestCheckboxLabel: Locator = this.page.locator('div[data-testid="GuestDetails-leadGuest-Checkbox"] p');
  readonly leadGuestHeaderLabel: Locator = this.page.locator('p[data-testid="GuestDetails-leadGuest-Header"]');
  readonly leadGuestContainers: Locator = this.page.locator('p[data-testid*="GuestDetails-leadGuest-Header"] ~ div');

  // ######## UI actions/navigation ########

  /** Click the 'I'm booking for someone else' checkbox, revealing the per-room lead guest sections. */
  async clickLeadGuestCheckbox(): Promise<void> {
    await this.leadGuestCheckbox.scrollIntoViewIfNeeded();
    await this.leadGuestCheckbox.click();
    await this.leadGuestHeaderLabel.waitFor({ state: 'visible' });
  }

  /** Get the per-room lead guest container components. */
  async getLeadGuestContainersArray(): Promise<LeadGuestContainerComponent[]> {
    const count = await this.leadGuestContainers.count();
    return Array.from({ length: count }, (_, index) => new LeadGuestContainerComponent(this.leadGuestContainers.nth(index), index));
  }

  // ######## UI validations ########

  /** Validate the lead guest checkbox and its label. */
  async validateLeadGuestCheckbox(): Promise<void> {
    console.log('Validate Lead Guest Checkbox');
    await expect(this.leadGuestCheckbox, 'Lead Guest checkbox').toBeVisible();
    await expect(this.leadGuestCheckboxLabel, 'Lead Guest checkbox label text').toHaveText(await Strings.IM_BOOKING_FOR_SOMEONE_ELSE.name);
  }

  /** Validate the lead guest section header. */
  async validateLeadGuestHeader(): Promise<void> {
    console.log('Validate Lead Guest Header');
    await expect(this.leadGuestHeaderLabel, 'Lead Guest header label text').toHaveText(await Strings.WHOS_THE_LEAD_CONTACT_FOR_THE_ROOM.name);
  }

  /** Validate the number of lead guest containers matches the expected room count. */
  async validateLeadGuestRoomsNumber(expectedRoomsNumber: number): Promise<void> {
    console.log('Validate Lead Guest rooms number');
    await expect(this.leadGuestContainers, 'Number of rooms').toHaveCount(expectedRoomsNumber);
  }
}
