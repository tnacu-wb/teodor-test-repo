import { type Locator, type Page } from '@playwright/test';

/** Privacy notice section shared by confirmation and payment journeys. */
export class PrivacyNoticeSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly privacyContainer: Locator = this.page.getByTestId('PaymentPage-PrivacyPolicy-Wrapper');
  readonly privacyTitleLabel: Locator = this.page.getByTestId('PaymentPage-PrivacyPolicy-Main-Title');
  readonly privacyTextLabel: Locator = this.page.getByTestId('PaymentPage-PrivacyPolicy-Main-Description');
  readonly viewOurPrivacyLink: Locator = this.page.getByTestId('PaymentPage-PrivacyPolicy-PrivacyNotice');
  readonly privacySectionExpanded: Locator = this.page.getByTestId('PaymentPage-PrivacyPolicy-Expanded-Wrapper');
  readonly findOutMoreButton: Locator = this.page.getByTestId('PaymentPage-PrivacyPolicy-ExpandButton');

  // ######## UI actions/navigation ########

  // ######## UI validations ########
}