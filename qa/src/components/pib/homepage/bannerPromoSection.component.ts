import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";

/** Banner promo section on homepage. */
export class BannerPromoSectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly bannerPromoContainer: Locator = this.page.getByTestId( "HomePage-BannerPromo", );
  readonly bannerPromoTitleLabel: Locator = this.page.getByTestId( "HomePage-BannerPromo-Title", );
  readonly bannerPromoDescriptionLabel: Locator = this.page.getByTestId( "HomePage-BannerPromo-Description", );
  readonly bannerPromoLink: Locator = this.bannerPromoContainer.locator("a");
  readonly bannerPromoImage: Locator = this.page.getByTestId( "HomePage-BannerPromo-Image", );
  // ######## UI validations ########
  /** Validate text banner promo. */
  async validateTextBannerPromo(): Promise<void> {
    console.log("Validate text banner promo");
    await expect( this.bannerPromoContainer, "Banner promo container", ).toBeVisible();
    await expect(this.bannerPromoTitleLabel, "Banner promo title").toHaveText( await IbStrings.HOME_BANNER_PROMO_TITLE.name, );
    await expect( this.bannerPromoDescriptionLabel, "Banner promo description", ).toHaveText(await IbStrings.HOME_BANNER_PROMO_DESCRIPTION.name);
  }
  /** Validate image banner promo. */
  async validateImageBannerPromo(): Promise<void> {
    console.log("Validate image banner promo");
    await expect( this.bannerPromoContainer, "Banner promo container", ).toBeVisible();
    await expect( this.bannerPromoImage, "Banner promo image source", ).toHaveAttribute( "src", expect.stringContaining(await IbStrings.HOME_BANNER_PROMO_IMAGE.name), );
  }
  /** Validate banner promo and optional link. */
  async validateBannerPromo(linkDisplayed: boolean): Promise<void> {
    console.log("Validate banner promo");
    if (await IbStrings.HOME_BANNER_PROMO_IMAGE.name)
      await this.validateImageBannerPromo();
    else await this.validateTextBannerPromo();
    if (linkDisplayed) {
      await expect(this.bannerPromoLink, "Banner promo link").toHaveAttribute( "href", await IbStrings.HOME_BANNER_PROMO_LINK.name, );
      await expect( this.bannerPromoLink, "Banner promo link target", ).toHaveAttribute( "target", await IbStrings.HOME_BANNER_PROMO_LINK_TARGET.name, );
    } else
      await expect(this.bannerPromoLink, "Banner promo link").not.toBeVisible();
  }
}
