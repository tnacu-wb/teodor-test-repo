import { type Page, type Locator, expect } from '@playwright/test';
import { ApiCalls } from '@api/graphql/apiCalls';

const ID = 'AncillariesPage';

/**
 * The privacy notice section (Ancillaries -> Find out more) containing the UI elements, custom
 * actions and validations. Mirrors qa/reference `components/opera/ancillaries/privacyNoticeSection.js`.
 */
export class AncillariesPrivacyNoticeSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly privacyContainer: Locator = this.page.locator(`[data-testid="${ID}-PrivacyPolicy-Wrapper"]`);
  readonly privacyTitleLabel: Locator = this.page.locator(`[data-testid="${ID}-PrivacyPolicy-Main-Title"]`);
  readonly privacyTextLabel: Locator = this.page.locator(`[data-testid="${ID}-PrivacyPolicy-Main-Description"]`);
  readonly viewOurPrivacyLink: Locator = this.page.locator(`a[data-testid="${ID}-PrivacyPolicy-PrivacyNotice"]`);
  readonly findOutMoreLabel: Locator = this.page.locator(`[data-testid="${ID}-PrivacyPolicy-ExpandButton"] h3`);
  readonly privacySectionExpanded: Locator = this.page.locator(`[data-testid="${ID}-PrivacyPolicy-Expanded-Wrapper"]`);
  readonly facilityImageList: Locator = this.page.locator(`[data-testid="${ID}-PrivacyPolicy-Expanded-Item-Wrapper"] img`);
  readonly facilityDescriptionLabelsList: Locator = this.page.locator(`[data-testid="${ID}-PrivacyPolicy-Expanded-Item-Wrapper"] div`);

  // ######## UI actions/navigation ########

  /** Expand/collapse the 'Find out more' privacy section. */
  async expandFindOutMore(shouldBeExpanded = true): Promise<void> {
    await this.findOutMoreLabel.scrollIntoViewIfNeeded();
    const isExpanded = await this.privacySectionExpanded.isVisible().catch(() => false);
    if (shouldBeExpanded && !isExpanded) {
      await this.findOutMoreLabel.click();
      await expect(this.privacySectionExpanded).toBeVisible();
    } else if (!shouldBeExpanded && isExpanded) {
      await this.findOutMoreLabel.click();
      await expect(this.privacySectionExpanded).not.toBeVisible();
    }
  }

  // ######## UI validations ########

  /** Validate the privacy notice title/description/link against the AEM ancillaries privacy policy. */
  async validatePrivacyNotice(): Promise<void> {
    console.log('Validate privacy notice section');
    const privacyPolicy = await ApiCalls.graphqlGetAncillariesPrivacyPolicies();
    await expect(this.privacyTitleLabel, 'Privacy notice title').toHaveText(privacyPolicy.name ?? '');
    await expect(this.viewOurPrivacyLink, 'View our privacy notice link').toBeVisible();
  }
}
