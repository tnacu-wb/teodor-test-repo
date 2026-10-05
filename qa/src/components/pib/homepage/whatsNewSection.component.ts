import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";

/** What's new section on IB. */
export class WhatsNewSectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly whatsNewContainer: Locator = this.page.getByTestId("WhatsNew-Container");
  readonly whatsNewLabel: Locator = this.page.getByTestId("WhatsNew-Title");
  readonly articleContainers: Locator = this.page.locator( 'a[data-testid^="WhatsNew-ArticleContainer-"]', );
  readonly firstArticleContainer: Locator = this.page.getByTestId( "WhatsNew-ArticleContainer-0", );
  readonly firstArticleImg: Locator = this.page.getByTestId( "WhatsNew-ArticleImage-0", );
  readonly firstArticleTagLabel: Locator = this.page.getByTestId( "WhatsNew-ArticleBadge-0", );
  readonly firstArticleTitleLabel: Locator = this.page.getByTestId( "WhatsNew-ArticleTitle-0", );
  readonly firstArticleDescriptionLabel: Locator = this.page.getByTestId( "WhatsNew-ArticleDescription-0", );
  readonly firstArticleBelowLink: Locator = this.page.getByTestId( "WhatsNew-ArticleOptionalLink-0", );
  readonly secondArticleContainer: Locator = this.page.getByTestId( "WhatsNew-ArticleContainer-1", );
  readonly secondArticleImg: Locator = this.page.getByTestId( "WhatsNew-ArticleImage-1", );
  readonly secondArticleTagLabel: Locator = this.page.getByTestId( "WhatsNew-ArticleBadge-1", );
  readonly secondArticleTitleLabel: Locator = this.page.getByTestId( "WhatsNew-ArticleTitle-1", );
  readonly secondArticleDescriptionLabel: Locator = this.page.getByTestId( "WhatsNew-ArticleDescription-1", );
  readonly secondArticleBelowLink: Locator = this.page.getByTestId( "WhatsNew-ArticleOptionalLink-1", );
  readonly thirdArticleContainer: Locator = this.page.getByTestId( "WhatsNew-ArticleContainer-2", );
  readonly thirdArticleImg: Locator = this.page.getByTestId( "WhatsNew-ArticleImage-2", );
  readonly thirdArticleTagLabel: Locator = this.page.getByTestId( "WhatsNew-ArticleBadge-2", );
  readonly thirdArticleTitleLabel: Locator = this.page.getByTestId( "WhatsNew-ArticleTitle-2", );
  readonly thirdArticleDescriptionLabel: Locator = this.page.getByTestId( "WhatsNew-ArticleDescription-2", );
  readonly thirdArticleBelowLink: Locator = this.page.getByTestId( "WhatsNew-ArticleOptionalLink-2", );
  readonly articleLinks: Locator = this.whatsNewContainer.locator("a");
  // ######## UI validations ########
  /** Validate What's New. */
  async validateWhatsNew(): Promise<void> {
    console.log("Validate What's New");
    await expect(this.whatsNewContainer, "What's new container").toBeVisible();
    await expect(this.articleContainers, "Article count").toHaveCount(3);
    await expect(this.whatsNewLabel, "What's new label").toHaveText( (await IbStrings.WHAT_S_NEW.name).trim(), );
  }
}
