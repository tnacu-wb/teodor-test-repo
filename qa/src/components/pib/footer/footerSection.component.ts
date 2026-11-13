import { expect, type Locator, type Page } from "@playwright/test";

/**
 * Footer section containing the UI elements, custom actions and validations
 */
export class FooterSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly footerSection: Locator = this.page.getByTestId("IB-Footer");
  readonly linksContainer: Locator = this.footerSection.locator( "div.links-container", );
  readonly footerColumns: Locator = this.linksContainer.locator(":scope > div");
  readonly footerQuickLinksSection: Locator = this.page.getByTestId( "IB-Footer-Quick-Links", );
  readonly copyrightLabel: Locator = this.footerQuickLinksSection .locator("span") .first();
  readonly footerQuickLinks: Locator = this.footerQuickLinksSection.locator("a");
  readonly footerSocialMediaLinksSection: Locator = this.page.getByTestId( "IB-Social-Media-Links", );
  readonly socialMediaLinks: Locator = this.footerSocialMediaLinksSection.locator("a");
  readonly termsAndConditionsLink: Locator = this.footerQuickLinksSection .locator("a") .first();
  /** Returns footer column element based on index. */
  getFooterColumnBasedOnIndex(index: number): Locator {
    return this.footerColumns.nth(index);
  }
  /** Returns footer column label element based on index. */
  getFooterColumnLabelBasedOnIndex(index: number): Locator {
    return this.getFooterColumnBasedOnIndex(index).locator("span");
  }
  /** Returns footer column list of link elements based column on index. */
  getFooterColumnLinkListBasedOnIndex(index: number): Locator {
    return this.getFooterColumnBasedOnIndex(index).locator("div a");
  }
  /** Returns Quick link element based on index. */
  getQuickLinkBasedOnIndex(index: number): Locator {
    return this.footerQuickLinks.nth(index);
  }
  /** Returns Social Media link element based on index. */
  getSocialMediaLinkBasedOnIndex(index: number): Locator {
    return this.socialMediaLinks.nth(index);
  }
  /** Returns Social Media image element based on index. */
  getSocialMediaIconBasedOnIndex(index: number): Locator {
    return this.getSocialMediaLinkBasedOnIndex(index).locator("img");
  }
  // ######## UI actions/navigation ########
  /** Click on Terms and Conditions link. */
  async clickTermsAndConditionsLink(): Promise<void> {
    console.log("Click Terms and Conditions link");
    await this.termsAndConditionsLink.click();
  }
  // ######## UI validations ########
  /** Validate footer container is displayed. */
  async validateFooterIsDisplayed(): Promise<void> {
    console.log("Validate footer container");
    await expect(this.footerSection, "Footer section").toBeVisible();
  }
  /** Validate columns in footer section. */
  async validateFooterColumns(
    expectedFooterColumnsList: Array<{
      columnTitle: string;
      linkItems: Array<{ linkText: string; linkPath?: string }>;
    }>,
  ): Promise<void> {
    console.log("Validate footer columns");
    await expect(this.footerColumns, "Footer columns list length").toHaveCount( expectedFooterColumnsList.length, );
    for (const [index, column] of expectedFooterColumnsList.entries()) {
      if (column.columnTitle)
        await expect( this.getFooterColumnLabelBasedOnIndex(index), "Footer column label", ).toHaveText(column.columnTitle);
      const columnLinks = this.getFooterColumnLinkListBasedOnIndex(index);
      await expect(columnLinks, "Footer column link count").toHaveCount( column.linkItems.length, );
      for (const [linkIndex, link] of column.linkItems.entries()) {
        const footerLink = columnLinks.nth(linkIndex);
        await expect(footerLink, "Footer link text").toHaveText(link.linkText);
        if (link.linkPath)
          await expect(footerLink, "Footer link path").toHaveAttribute( "href", link.linkPath, );
      }
    }
  }
  /** Validate Copyright label. */
  async validateCopyrightLabel(expectedCopyright: string): Promise<void> {
    console.log("Validate Copyright label");
    await expect(this.copyrightLabel, "Copyright label").toContainText( expectedCopyright.replace("&copy;", ""),
    );
  }
  /** Validate Quick links in footer section. */
  async validateQuickLinks(
    expectedQuickLinksList: Array<{ linkText: string; linkPath: string }>,
  ): Promise<void> {
    console.log("Validate quick links section");
    await expect(this.footerQuickLinks, "Quick Links list length").toHaveCount( expectedQuickLinksList.length, );
    for (const [index, link] of expectedQuickLinksList.entries()) {
      await expect( this.getQuickLinkBasedOnIndex(index), "Quick link label", ).toHaveText(link.linkText);
      await expect( this.getQuickLinkBasedOnIndex(index), "Quick link path", ).toHaveAttribute("href", link.linkPath);
    }
  }
}
