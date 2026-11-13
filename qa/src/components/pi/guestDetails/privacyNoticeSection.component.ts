import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { Constants } from '@test-data/constants';
import { ApiCalls } from '@api/graphql/apiCalls';
import { type AncillariesPrivacyPolicies } from '@api/response/ancillariesPrivacyPolices';

/**
 * The Privacy Notice section (Guest Details) containing the UI elements, custom actions and validations.
 * Mirrors qa/reference `components/opera/guestDetails/privacyNoticeSection.js`.
 */
export class PrivacyNoticeSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly privacyNoticeContainer: Locator = this.page.locator('div.css-1g0pvjv div[data-testid="Alert"]');
  readonly privacyNoticeIcon: Locator = this.privacyNoticeContainer.locator('div[data-testid="svg-container"]');
  readonly privacyNoticeLabel: Locator = this.privacyNoticeContainer.locator('div[data-testid="AlertDescription"]');
  readonly privacyNoticeLink: Locator = this.privacyNoticeLabel.locator('a');
  readonly dataCollectionSecurityTitleLabel: Locator = this.page.locator('h3[data-testid="GuestDetails-PrivacyPolicy-Main-Title"]');
  readonly dataCollectionSecurityDescriptionLabel: Locator = this.page.locator('div[data-testid="GuestDetails-PrivacyPolicy-Main-Description"] p');
  readonly dataCollectionSecurityViewMoreLink: Locator = this.page.locator('div[data-testid="GuestDetails-PrivacyPolicy-Main-Description"] + a');
  readonly dataCollectionSecurityFindOutMoreLink: Locator = this.page.locator('div[data-testid="GuestDetails-PrivacyPolicy-ExpandButton"] h3');
  readonly dataCollectionSecurityExpandedContainer: Locator = this.page.locator('div[data-testid="GuestDetails-PrivacyPolicy-Expanded-Wrapper"]');
  readonly dataCollectionSecurityItemContainer: Locator = this.page.locator('div[data-testid="GuestDetails-PrivacyPolicy-Expanded-Item-Wrapper"]');

  // ######## UI actions/navigation ########

  /** Click the Privacy Notice link. */
  async clickPrivacyNoticeLink(): Promise<void> {
    console.log('Click Privacy Notice Link');
    await this.privacyNoticeLink.scrollIntoViewIfNeeded();
    await this.privacyNoticeLink.click();
  }

  /** Click the 'View our Privacy Notice' link. */
  async clickViewPrivacyNoticeLink(): Promise<void> {
    console.log('Click View Privacy Notice Link');
    await this.dataCollectionSecurityViewMoreLink.scrollIntoViewIfNeeded();
    await this.dataCollectionSecurityViewMoreLink.click();
  }

  /** Expand/collapse the 'Find out more' Data Collection Security section. */
  async expandFindOutMore(shouldBeExpanded = true): Promise<void> {
    console.log('Click Find out more link');
    await this.dataCollectionSecurityFindOutMoreLink.scrollIntoViewIfNeeded();
    await this.dataCollectionSecurityFindOutMoreLink.click();
    if (shouldBeExpanded) {
      await expect(this.dataCollectionSecurityExpandedContainer).toBeVisible();
    } else {
      await expect(this.dataCollectionSecurityExpandedContainer).not.toBeVisible();
    }
  }

  // ######## UI validations ########

  /** Validate the Privacy Notice banner (icon, text, link). */
  async validatePrivacyNotice(): Promise<void> {
    console.log('Validate Privacy Notice');
    await expect(this.privacyNoticeContainer, 'Privacy notice container').toBeVisible();
    await expect(this.privacyNoticeIcon, 'Privacy notice icon').toBeVisible();
    const personalInfoText = (await Strings.YOUR_PERSONAL_INFORMATION.name).replace(/<a\b[^>]*>(.*?)<\/a>/i, '').replace(/&nbsp;/g, ' ');
    await expect(this.privacyNoticeLabel, 'Privacy notice label').toContainText(personalInfoText);
    await expect(this.privacyNoticeLink, 'Privacy notice link should be visible').toBeVisible();
  }

  /** Validate the Data Collection Security section against the AEM privacy policy content. */
  async validateDataCollectionSecurity(isExpanded = false): Promise<void> {
    console.log('Validate Data Collection Security');

    const apiPrivacyPolicy = await ApiCalls.graphqlGetAncillariesPrivacyPolicies();
    await this.validateDataCollectionSecurityTitle(apiPrivacyPolicy.name ?? '');
    await this.validateDataCollectionSecurityDescription(apiPrivacyPolicy.description ?? '');
    await this.validateDataCollectionSecurityViewMore(apiPrivacyPolicy);
    await this.validateDataCollectionSecurityFindOutMore(apiPrivacyPolicy.moreInfoLabel ?? '');

    if (isExpanded) {
      await expect(this.dataCollectionSecurityExpandedContainer, 'DCS expanded container').toBeVisible();
      await this.validateDataCollectionSecurityExpandedContainer(apiPrivacyPolicy);
    } else {
      await expect(this.dataCollectionSecurityExpandedContainer, 'DCS expanded container').not.toBeVisible();
    }
  }

  /** Validate the Data Collection Security title. */
  async validateDataCollectionSecurityTitle(title: string): Promise<void> {
    await expect(this.dataCollectionSecurityTitleLabel, `DCS title should have text ${title}`).toHaveText(title);
  }

  /** Validate the Data Collection Security description. */
  async validateDataCollectionSecurityDescription(description: string): Promise<void> {
    const descriptionLabel = description.replace(/<([^>]+)>/g, '').trim().replace(/&nbsp;/g, '');
    await expect(this.dataCollectionSecurityDescriptionLabel, `DCS description should have text ${descriptionLabel}`).toContainText(descriptionLabel);
  }

  /** Validate the Data Collection Security 'View our Privacy Notice' link. */
  async validateDataCollectionSecurityViewMore(apiPrivacyPolicy: AncillariesPrivacyPolicies): Promise<void> {
    await expect(this.dataCollectionSecurityViewMoreLink, `DCS "View our Privacy Notice" link should have text ${apiPrivacyPolicy.linkLabel}`).toHaveText(
      apiPrivacyPolicy.linkLabel ?? ''
    );
    const viewMoreLink = `${Constants.ASSETS_URL}${apiPrivacyPolicy.linkSrc ?? ''}`;
    await expect(this.dataCollectionSecurityViewMoreLink, `DCS "View our Privacy Notice" link should be ${viewMoreLink}`).toHaveAttribute('href', viewMoreLink);
  }

  /** Validate the Data Collection Security 'Find out more' link. */
  async validateDataCollectionSecurityFindOutMore(moreInfoLabel: string): Promise<void> {
    await expect(this.dataCollectionSecurityFindOutMoreLink, `DCS "Find out more" link should have text ${moreInfoLabel}`).toHaveText(moreInfoLabel.trim());
  }

  /** Validate the Data Collection Security expanded container's 3 items (image + description). */
  async validateDataCollectionSecurityExpandedContainer(apiPrivacyPolicy: AncillariesPrivacyPolicies): Promise<void> {
    console.log('Validate Data Collection Security expanded container');
    const moreInfo = apiPrivacyPolicy.moreInfo ?? [];
    expect(moreInfo.length, 'DCS should have only 3 items').toBe(3);

    const items = this.dataCollectionSecurityItemContainer;
    await expect(items, 'DCS should have only 3 items').toHaveCount(3);
    for (const [index, info] of moreInfo.entries()) {
      const item = items.nth(index);
      const image = item.locator('img');
      await expect(image, 'DCS image').toBeVisible();
      const imgSrc = `${Constants.ASSETS_URL}${info.image ?? ''}`;
      await expect(image, `DCS item should have the image=${imgSrc}`).toHaveAttribute('src', imgSrc);

      const description = item.locator('div');
      const apiDescription = (info.description ?? '').replace(/<([^>]+)>/g, '').replace(/&quot;/g, '"').replace(/&nbsp;/g, '').replace(/\n\s*/g, '');
      await expect(description, `DCS item should have the description ${apiDescription}`).toContainText(apiDescription);
    }
  }

  /** Validate the section does not exist in the DOM. */
  async validateSecurityPolicyComponentDoNotExistsInDOM(): Promise<void> {
    await expect(this.dataCollectionSecurityTitleLabel, 'dataCollectionSecurityTitleLabel does exist.').toHaveCount(0);
    await expect(this.dataCollectionSecurityDescriptionLabel, 'dataCollectionSecurityDescriptionLabel does exist.').toHaveCount(0);
    await expect(this.dataCollectionSecurityViewMoreLink, 'dataCollectionSecurityViewMoreLink does exist.').toHaveCount(0);
    await expect(this.dataCollectionSecurityFindOutMoreLink, 'dataCollectionSecurityFindOutMoreLink does exist.').toHaveCount(0);
  }
}
