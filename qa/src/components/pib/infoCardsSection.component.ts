import { type Locator, type Page } from "@playwright/test";
import { SpendingAndReportingCards } from "../../test-data/pib/spendingAndReportingCards";
import { InfoCardContainerComponent } from "./infoCardContainer.component";

/**
 * Info cards section - Card management, Spending and reporting
 */
export class InfoCardsSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########
  readonly infoCardsContainer: Locator = this.page.getByTestId( "BenefitsBoxes-Container", );
  readonly infoCardContainers: Locator = this.infoCardsContainer.locator(":scope > div");

  /** Returns info card container element based on index. */
  getInfoCardContainerBasedOnIndex(index: number): InfoCardContainerComponent {
    return new InfoCardContainerComponent(this.infoCardContainers.nth(index));
  }

  /** Get info card container based on title. */
  async getInfoCardContainerByTitle(
    title: string,
    exactMatch = true,
  ): Promise<InfoCardContainerComponent> {
    console.log(`Get ${title} info card container`);
    await this.infoCardsContainer.scrollIntoViewIfNeeded();
    const count = await this.infoCardContainers.count();
    for (let index = 0; index < count; index += 1) {
      const card = this.getInfoCardContainerBasedOnIndex(index);
      const cardTitle = await card.getInfoCardTitleLabel();
      if (exactMatch ? cardTitle === title : cardTitle.includes(title))
        return card;
    }
    throw new Error(`${title} info card container was not found!`);
  }

  // ######## UI validations ########
  /** Validate info card by title card. */
  async validateInfoCardByTitle(infoCardTitle: {
    name: Promise<string>;
  }): Promise<void> {
    const title = await infoCardTitle.name;
    console.log(`Validate ${title} info card container`);
    await (
      await this.getInfoCardContainerByTitle(title)
    ).validateInfoCardContainer();
  }

  /** Validate info cards section. */
  async validateInfoCardSection(): Promise<void> {
    console.log("Validate info card section");
    await this.validateInfoCardByTitle(
      SpendingAndReportingCards.INTEREST_FREE_CREDIT.title,
    );
    await this.validateInfoCardByTitle(
      SpendingAndReportingCards.EXPENSE_MANAGEMENT.title,
    );
    await this.validateInfoCardByTitle(
      SpendingAndReportingCards.CONSOLIDATED_INVOICES.title,
    );
  }
}
