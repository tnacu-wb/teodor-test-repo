import { expect, type Locator } from "@playwright/test";
import { SpendingAndReportingCards } from "../../test-data/pib/spendingAndReportingCards";

/**
 * Info card container - Spending and reporting
 */
export class InfoCardContainerComponent {
  /** Info card container constructor. */
  constructor(container: Locator) {
    this.sectionContainer = container;
    this.infoCardIcon = container.locator("img");
    this.infoCardTitleLabel = container.locator("span").first();
    this.infoCardDescriptionLabel = container.locator("span").nth(1);
  }

  // ######## UI elements/properties ########
  readonly sectionContainer: Locator;
  readonly infoCardIcon: Locator;
  readonly infoCardTitleLabel: Locator;
  readonly infoCardDescriptionLabel: Locator;

  // ######## UI actions/navigation ########
  /** Get info card title label from actual container. */
  async getInfoCardTitleLabel(): Promise<string> {
    console.log("Get info card title label");
    return ((await this.infoCardTitleLabel.textContent()) ?? "").trim();
  }

  /** Click on report card. */
  async clickOnCard(): Promise<void> {
    console.log("Click on report card");
    await this.sectionContainer.scrollIntoViewIfNeeded();
    await this.sectionContainer.click();
  }

  // ######## UI validations ########
  /** Validate info card container. */
  async validateInfoCardContainer(): Promise<void> {
    console.log("Validate spending and reporting info card container");
    const infoCardTitle = await this.getInfoCardTitleLabel();
    const infoCard =
      await SpendingAndReportingCards.getCardByTitle(infoCardTitle);
    await expect( this.sectionContainer, `Info card ${infoCardTitle} container`, ).toBeVisible();
    await expect( this.infoCardIcon, `Info card ${infoCardTitle} icon`, ).toHaveAttribute("src", new RegExp(await infoCard.icon.name));
    await expect( this.infoCardTitleLabel, `Info card ${infoCardTitle} title`, ).toHaveText(await infoCard.title.name);
    await expect( this.infoCardDescriptionLabel, `Info card ${infoCardTitle} description`, ).toHaveText(await infoCard.description.name);
  }
}
