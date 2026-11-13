import { expect, type Locator, type Page } from "@playwright/test";
import { SpendingAndReportingCards } from "../../../test-data/pib/spendingAndReportingCards";
/** Report card container - Spending and reporting */
export class ReportCardContainerComponent {
  private readonly page: Page = global.page;
  readonly containerSelector: string;
  /** Report card container constructor. */
  constructor({ containerSelector }: { containerSelector: string }) {
    this.containerSelector = containerSelector;
  }
  // ######## UI elements/properties ########
  /** Section Container. */
  get sectionContainer(): Locator {
    return this.page.locator(this.containerSelector);
  }
  /** Report Card Icon. */
  get reportCardIcon(): Locator {
    return this.sectionContainer.locator("img");
  }
  /** Report Card Title Label. */
  get reportCardTitleLabel(): Locator {
    return this.sectionContainer.locator("h3");
  }
  /** Report Card Description Label. */
  get reportCardDescriptionLabel(): Locator {
    return this.sectionContainer.locator("p");
  }
  // ######## UI actions/navigation ########
  /** Get report card title. */
  async getReportCardTitleLabel(): Promise<string> {
    console.log("Get report card title");
    return this.reportCardTitleLabel
      .textContent()
      .then((value) => value?.trim() ?? "");
  }
  /** Click report card. */
  async clickOnCard(): Promise<void> {
    console.log("Click report card");
    await this.sectionContainer.click();
  }
  // ######## UI validations ########
  /** Validate report card container. */
  async validateReportCardContainer(): Promise<void> {
    console.log("Validate report card container");
    const title = await this.getReportCardTitleLabel();
    const reportCard = await SpendingAndReportingCards.getCardByTitle(title);
    await expect(this.sectionContainer, `Report card ${title}`).toBeVisible();
    await expect( this.reportCardIcon, `Report card ${title} icon`, ).toHaveAttribute( "src", expect.stringContaining(await reportCard.icon.name), );
    await expect( this.reportCardTitleLabel, `Report card ${title} title`, ).toHaveText(await reportCard.title.name);
    await expect( this.reportCardDescriptionLabel, `Report card ${title} description`, ).toHaveText(await reportCard.description.name);
  }
}
