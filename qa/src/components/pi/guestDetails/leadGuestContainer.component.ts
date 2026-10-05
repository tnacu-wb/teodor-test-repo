import { type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * One lead guest container within the Lead Guest section (Guest Details) containing the UI
 * elements, custom actions and validations. Mirrors qa/reference
 * `components/opera/guestDetails/leadGuestContainer.js` (simplified: PI always uses the
 * per-container scoped fields, so the WebdriverIO body-fallback selector branching is dropped).
 */
export class LeadGuestContainerComponent {
  private readonly container: Locator;
  private readonly roomIndex: number;

  constructor(container: Locator, roomIndex: number) {
    this.container = container;
    this.roomIndex = roomIndex;
  }

  // ######## UI elements/properties ########

  get titleButton(): Locator {
    return this.container.locator('button[data-testid="DropdownComp-GuestDetails-leadGuest-TitleDropdown-menuButton"]');
  }

  get titleValueLabel(): Locator {
    return this.container.locator('div[data-testid="DropdownComp-GuestDetails-leadGuest-TitleDropdown-menuButtonText"]');
  }

  get firstNameInput(): Locator {
    return this.container.locator(
      `input[data-testid="input-leadGuest[${this.roomIndex}][firstName]"], input[data-testid="input-leadGuest[${this.roomIndex}].firstName"]`
    );
  }

  get lastNameInput(): Locator {
    return this.container.locator(
      `input[data-testid="input-leadGuest[${this.roomIndex}][lastName]"], input[data-testid="input-leadGuest[${this.roomIndex}].lastName"]`
    );
  }

  get emailInput(): Locator {
    return this.container.locator(
      `input[data-testid="input-leadGuest[${this.roomIndex}][email]"], input[data-testid="input-leadGuest[${this.roomIndex}].email"]`
    );
  }

  get errorTitleLabel(): Locator {
    return this.container.locator('div[data-testid="GuestDetails-leadGuest-ErrorContainer"] div p');
  }

  // ######## UI actions/navigation ########

  /** Select the given title from the per-room lead-guest title dropdown. */
  async setTitle(title: string): Promise<void> {
    await this.titleButton.click();
    await this.container.getByRole('button', { name: title, exact: true }).click();
  }

  /** Fill this room's lead-guest first/last name and (optionally) title. */
  async fillLeadGuestDetails({
    title,
    firstName,
    lastName,
  }: { title?: string; firstName?: string; lastName?: string } = {}): Promise<void> {
    if (title) {
      await this.setTitle(title);
    }
    if (firstName) {
      await this.firstNameInput.fill(firstName);
    }
    if (lastName) {
      await this.lastNameInput.fill(lastName);
    }
  }

  // ######## UI validations ########

  /** Validate the selected title matches the expected value. */
  async validateTitle(expectedTitle: string): Promise<void> {
    await expect(this.titleValueLabel, `Lead guest room ${this.roomIndex + 1} title`).toHaveText(expectedTitle);
  }
}
