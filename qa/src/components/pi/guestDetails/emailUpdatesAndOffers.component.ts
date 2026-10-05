import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { Locales } from '@test-data/locales';

/**
 * The Email Updates and Offers section (Guest Details) containing the UI elements, custom actions and validations.
 * Mirrors qa/reference `components/opera/guestDetails/emailUpdatesAndOffers.js`.
 */
export class EmailUpdatesAndOffersComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly emailUpdatesContainer: Locator = this.page.locator('div[data-testid="GuestDetails-AcceptFutureMailing"]');
  readonly emailUpdatesHeaderLabel: Locator = this.page.locator('p[data-testid="GuestDetails-AcceptFutureMailing-Header"]');
  readonly emailUpdatesLabel: Locator = this.page.locator('div[data-testid="GuestDetails-AcceptFutureMailing-Description"]');
  readonly emailUpdatesCheckbox: Locator = this.page.locator('label[data-testid="GuestDetails-AcceptFutureMailing-CheckboxContainer"] input');
  readonly emailUpdatesCheckboxLabel: Locator = this.page.locator('p[data-testid="GuestDetails-AcceptFutureMailing-CheckboxText"]');

  // ######## UI actions/navigation ########

  /** Click the Email Updates and Offers checkbox. */
  async clickEmailUpdatesCheckbox(): Promise<void> {
    console.log('Click Email updates and offers checkbox');
    await this.emailUpdatesCheckbox.scrollIntoViewIfNeeded();
    await this.emailUpdatesCheckbox.click();
  }

  // ######## UI validations ########

  /** Validate the Email Updates and Offers section content, accounting for locale/country-selected wording. */
  async validateEmailUpdatesAndOffers({ isGermanySelected = false }: { isGermanySelected?: boolean } = {}): Promise<void> {
    console.log('Validate Email Updates and Offers');

    const isDEWebsite = global.browser?.options?.locale === Locales.DE_DE.name;
    await expect(this.emailUpdatesContainer, 'Email updates and offers container').toBeVisible();
    await expect(this.emailUpdatesHeaderLabel, 'Email updates and offers header label').toHaveText(await Strings.EMAIL_UPDATES_AND_OFFERS.name);

    const emailUpdatesLabelText = isDEWebsite || isGermanySelected
      ? await Strings.WE_WOULD_LIKE_TO_INFORM_YOU_NEWS_AND_OFFERS_GERMAN_RESIDENT.name
      : await Strings.WE_WOULD_LIKE_TO_SEND_YOU_NEWS_AND_OFFERS_NEGATIVE.name;
    await expect(this.emailUpdatesLabel, 'Email updates and offers label').toContainText(emailUpdatesLabelText);

    const emailUpdatesCheckboxText = isDEWebsite || isGermanySelected
      ? await Strings.IF_YOU_DO_NOT_WISH_TO_RECEIVE_EMAIL_MARKETING_GERMAN.name
      : await Strings.I_DONT_WANT_TO_RECEIVE_EMAIL_MARKETING_ZIP_HUB.name;
    await expect(this.emailUpdatesCheckboxLabel, 'Email updates and offers checkbox label').toContainText(emailUpdatesCheckboxText);
  }

  /** Validate whether the Accept Future Mailing checkbox is checked. */
  async validateAcceptFutureMailingCheckbox(checked = false): Promise<void> {
    console.log(`Validate if AcceptFutureMailing is checked or not - ${checked}`);
    if (checked) {
      await expect(this.emailUpdatesCheckbox, 'AcceptFutureMailing is checked').toBeChecked();
    } else {
      await expect(this.emailUpdatesCheckbox, 'AcceptFutureMailing is unchecked').not.toBeChecked();
    }
  }

  /** Validate the whole section does not exist in the DOM. */
  async validateIfElementsDoNotExistsInDOM(): Promise<void> {
    await expect(this.emailUpdatesContainer, 'emailUpdatesContainer does exist.').toHaveCount(0);
    await expect(this.emailUpdatesHeaderLabel, 'emailUpdatesHeaderLabel does exist.').toHaveCount(0);
    await expect(this.emailUpdatesLabel, 'emailUpdatesLabel does exist.').toHaveCount(0);
    await expect(this.emailUpdatesCheckbox, 'emailUpdatesCheckbox does exist.').toHaveCount(0);
    await expect(this.emailUpdatesCheckboxLabel, 'emailUpdatesCheckboxLabel does exist.').toHaveCount(0);
  }

  /** Validate the Email Updates and Offers section visibility. */
  async validateEmailUpdatesAndOffersIsDisplayed({ isDisplayed = true }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log('Validate Email Updates and Offers section is displayed');
    if (isDisplayed) {
      await expect(this.emailUpdatesContainer, 'Email updates and offers container').toBeVisible();
      await expect(this.emailUpdatesHeaderLabel, 'Email updates and offers header label').toBeVisible();
      await expect(this.emailUpdatesLabel, 'Email updates and offers label').toBeVisible();
      await expect(this.emailUpdatesCheckbox, 'Email updates and offers checkbox').toBeVisible();
      await expect(this.emailUpdatesCheckboxLabel, 'Email updates and offers checkbox label').toBeVisible();
    } else {
      await expect(this.emailUpdatesContainer, 'Email updates and offers container').not.toBeVisible();
    }
  }
}
