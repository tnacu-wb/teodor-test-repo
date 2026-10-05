import { expect, type Locator, type Page } from "@playwright/test";
import { ReportCardContainerComponent } from "./reportCardContainer.component";
/** Report card section - Spending and reporting tabs */
export class ReportCardsSectionComponent {
  private readonly page: Page = global.page;
  readonly reportCardsContainerSelector: string;
  readonly reportCardContainersSelector: string;
  /** Report cards section constructor. */
  constructor({
    reportCardsContainerSelector,
    reportCardContainersSelector,
  }: {
    reportCardsContainerSelector: string;
    reportCardContainersSelector: string;
  }) {
    this.reportCardsContainerSelector = reportCardsContainerSelector;
    this.reportCardContainersSelector = reportCardContainersSelector;
  }
  // ######## UI elements/properties ########
  /** Report Cards Container. */
  get reportCardsContainer(): Locator {
    return this.page.locator(this.reportCardsContainerSelector);
  }
  /** Report Card Containers. */
  get reportCardContainers(): Locator {
    return this.page.locator(this.reportCardContainersSelector);
  }
  /** Get report card by index. */
  getReportCardContainerBasedOnIndex(
    index: number,
  ): ReportCardContainerComponent {
    return new ReportCardContainerComponent({
      containerSelector: `${this.reportCardContainersSelector}:nth-child(${index + 1})`,
    });
  }
  // ######## UI actions/navigation ########
  /** Find report card by title. */
  async getReportCardContainerByTitle(
    title: string,
    exactMatch = true,
  ): Promise<ReportCardContainerComponent> {
    console.log(`Get report card ${title}`);
    const count = await this.reportCardContainers.count();
    for (let index = 0; index < count; index += 1) {
      const card = this.getReportCardContainerBasedOnIndex(index);
      const cardTitle = await card.getReportCardTitleLabel();
      if (exactMatch ? cardTitle === title : cardTitle.includes(title))
        return card;
    }
    throw new Error(`${title} report card container was not found!`);
  }
  /** Click report card by title. */
  async clickOnReportCardByTitle(reportCardTitle: {
    name: Promise<string>;
  }): Promise<void> {
    const title = await reportCardTitle.name;
    console.log(`Click report card ${title}`);
    await (await this.getReportCardContainerByTitle(title)).clickOnCard();
  }
  // ######## UI validations ########
  /** Validate report card. */
  async validateReportCard({
    reportCardTitle,
    isDisplayed = true,
  }: {
    reportCardTitle: { name: Promise<string> };
    isDisplayed?: boolean;
  }): Promise<void> {
    const title = await reportCardTitle.name;
    console.log(`Validate report card ${title}`);
    const count = await this.reportCardContainers.count();
    const found = await Promise.all(
      Array.from(
        { length: count },
        async (_, index) =>
          (await this.getReportCardContainerBasedOnIndex(
            index,
          ).getReportCardTitleLabel()) === title,
      ),
    ).then((items) => items.includes(true));
    await expect(found, `Report card ${title} visibility`).toBe(isDisplayed);
    if (isDisplayed)
      await (
        await this.getReportCardContainerByTitle(title)
      ).validateReportCardContainer();
  }
}
