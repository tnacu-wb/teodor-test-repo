import { expect, type Locator, type Page } from "@playwright/test";
import { ConfirmationPageCards } from "../../../test-data/pib/confirmationPageCards";

/**
 * Info card container - Auth - Confirmation page
 */
export class AuthInfoCardContainerComponent {
  private readonly page: Page = global.page;

  /** Info card container constructor. */
  constructor(container: Locator) {
    this.sectionContainer = container;
    this.infoCardIcon = container.locator("img");
    this.infoCardTitleLabel = container.locator("h3");
    this.infoCardDescriptionLabel = container.locator("p");
    this.infoCardButton = container.locator("button");
  }

  // ######## UI elements/properties ########
  readonly sectionContainer: Locator;
  readonly infoCardIcon: Locator;
  readonly infoCardTitleLabel: Locator;
  readonly infoCardDescriptionLabel: Locator;
  readonly infoCardButton: Locator;

  // ######## UI actions/navigation ########
  /** Get info card title label from actual container. */
  async getInfoCardTitleLabel(): Promise<string> {
    console.log("Get info card title label");
    return ((await this.infoCardTitleLabel.textContent()) ?? "").trim();
  }

  /** Click on card button. */
  async clickOnCTA(): Promise<void> {
    console.log("Click on card CTA");
    await this.infoCardButton.scrollIntoViewIfNeeded();
    await this.infoCardButton.click();
  }

  // ######## UI validations ########
  /** Validate info card container. */
  async validateInfoCardContainer(): Promise<void> {
    console.log("Validate auth info card container");
    const infoCardTitle = await this.getInfoCardTitleLabel();
    const infoCard = await ConfirmationPageCards.getCardByTitle(infoCardTitle);
    await expect( this.sectionContainer, `${infoCardTitle} card container`, ).toBeVisible();
    await expect( this.infoCardIcon, `${infoCardTitle} card icon`, ).toHaveAttribute("src", new RegExp(await infoCard.icon.name));
    await expect( this.infoCardTitleLabel, `${infoCardTitle} card title`, ).toHaveText(await infoCard.title.name);
    await expect( this.infoCardDescriptionLabel, `${infoCardTitle} card description`, ).toHaveText(await infoCard.description.name);
    await expect( this.infoCardButton, `${infoCardTitle} card button`, ).toHaveText(await infoCard.button.name);
  }
}
