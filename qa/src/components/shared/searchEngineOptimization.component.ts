import { type Page, type Locator, expect } from '@playwright/test';

/**
 * SEO requirements (meta tags: charset, description, Twitter cards, OpenGraph, favicons)
 * containing the UI elements, custom actions and validations. Mirrors qa/reference
 * `components/common/searchEngineOptimization.js` (condensed: exposes the core meta tags plus a
 * generic `metaTag(property)` lookup for the rest of the reference's exhaustive Twitter/OpenGraph
 * card list, instead of one getter per individual meta property).
 */
export class SearchEngineOptimizationComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly metaCharset: Locator = this.page.locator('meta[charset="utf-8"]');
  readonly metaDescription: Locator = this.page.locator('meta[name="description"]');
  readonly metaTwitterCard: Locator = this.page.locator('meta[property="twitter:card"]');
  readonly metaTwitterTitle: Locator = this.page.locator('meta[property="twitter:title"]');
  readonly metaTwitterDescription: Locator = this.page.locator('meta[property="twitter:description"]');
  readonly metaTwitterImage: Locator = this.page.locator('meta[property="twitter:image"]');
  readonly metaOpenGraphType: Locator = this.page.locator('meta[property="og:type"]');
  readonly metaOpenGraphTitle: Locator = this.page.locator('meta[property="og:title"]');
  readonly metaOpenGraphDescription: Locator = this.page.locator('meta[property="og:description"]');
  readonly metaOpenGraphImage: Locator = this.page.locator('meta[property="og:image"]');
  readonly metaOpenGraphUrl: Locator = this.page.locator('meta[property="og:url"]');

  /** Generic lookup for any other meta tag by its `name` or `property` attribute value. */
  metaTag(nameOrProperty: string): Locator {
    return this.page.locator(`meta[name="${nameOrProperty}"], meta[property="${nameOrProperty}"]`);
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the meta description tag content matches the expected description. */
  async validateMetaDescription(expectedDescription: string): Promise<void> {
    await expect(this.metaDescription, 'Meta description').toHaveAttribute('content', expectedDescription);
  }

  /** Validate the OpenGraph/Twitter title and description tags match the expected page title/description. */
  async validateSocialMetaTags({ title, description }: { title: string; description: string }): Promise<void> {
    await expect(this.metaOpenGraphTitle, 'OpenGraph title').toHaveAttribute('content', title);
    await expect(this.metaOpenGraphDescription, 'OpenGraph description').toHaveAttribute('content', description);
    await expect(this.metaTwitterTitle, 'Twitter title').toHaveAttribute('content', title);
    await expect(this.metaTwitterDescription, 'Twitter description').toHaveAttribute('content', description);
  }
}
