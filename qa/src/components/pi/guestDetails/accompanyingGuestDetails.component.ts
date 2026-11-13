import { type Page, type Locator } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * The Accompanying Guest Details section (Guest Details) containing the UI elements, custom
 * actions and validations. Mirrors qa/reference
 * `components/opera/guestDetails/accompanyingGuestDetails.js`.
 */
export class AccompanyingGuestDetailsComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly accompanyingGuestDetailsSection: Locator = this.page.locator('div[data-testid="GuestDetails-leadGuest-AccompanyingContainer"]');
  readonly accompanyingGuestDetailsTitleLabel: Locator = this.accompanyingGuestDetailsSection.locator('p').first();
  readonly accompanyingGuestDetailsSubtitleLabel: Locator = this.accompanyingGuestDetailsSection.locator('p').nth(1);

  /** Title dropdown button for the accompanying guest at the given 0-based room index. */
  titleButton(roomIndex: number): Locator {
    return this.page.locator('button[data-testid*="DropdownComp-GuestDetails-leadGuest-accompanyingTitleDropdown-"]').nth(roomIndex);
  }

  /** First name input for the accompanying guest at the given 0-based room index. */
  firstNameInput(roomIndex: number): Locator {
    return this.page.locator(`input[data-testid="input-leadGuest[${roomIndex}][accompanyingfirstName]"]`);
  }

  /** Last name input for the accompanying guest at the given 0-based room index. */
  lastNameInput(roomIndex: number): Locator {
    return this.page.locator(`input[data-testid="input-leadGuest[${roomIndex}][accompanyinglastName]"]`);
  }

  // ######## UI actions/navigation ########

  /** Select a title for the accompanying guest at the given room index. */
  async setTitleButton({ title, roomIndex = 0 }: { title: string; roomIndex?: number }): Promise<void> {
    console.log(`Select "${title}" element from the "Title" field`);
    await this.titleButton(roomIndex).click();
    await this.page.getByRole('button', { name: title, exact: true }).click();
  }

  /** Set the first name for the accompanying guest at the given room index. */
  async setFirstNameInput({ firstName, roomIndex = 0 }: { firstName: string; roomIndex?: number }): Promise<void> {
    console.log(`First Name Input = ${firstName}`);
    await this.firstNameInput(roomIndex).fill(firstName);
  }

  /** Set the last name for the accompanying guest at the given room index. */
  async setLastNameInput({ lastName, roomIndex = 0 }: { lastName: string; roomIndex?: number }): Promise<void> {
    console.log(`Last Name Input = ${lastName}`);
    await this.lastNameInput(roomIndex).fill(lastName);
  }

  /** Fill the accompanying guest details (title/first/last name) for the given room index. */
  async fillAccompanyingGuestDetails({
    title,
    firstName,
    lastName,
    roomIndex = 0,
  }: { title?: string; firstName?: string; lastName?: string; roomIndex?: number } = {}): Promise<void> {
    console.log(`Fill "Accompanying Guest details" Section for Room ${roomIndex + 1}`);
    const resolvedTitle = title ?? (await Strings.MR_TITLE.name);
    if (resolvedTitle) {
      await this.setTitleButton({ title: resolvedTitle, roomIndex });
    }
    if (firstName) {
      await this.setFirstNameInput({ firstName, roomIndex });
    }
    if (lastName) {
      await this.setLastNameInput({ lastName, roomIndex });
    }
  }
}
