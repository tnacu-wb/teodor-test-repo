import { expect, type Locator } from "@playwright/test";
import { AuthInfoCardContainerComponent } from "../../components/pib";
import {
  ConfirmationPageCards,
  type ConfirmationPageCard,
} from "../../test-data/pib/confirmationPageCards";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * Confirmation page class
 */
export class ConfirmationPage extends BasePibPage {
  readonly url = "welcome";
  // ######## UI elements/properties ########
  readonly confirmationPageContainer: Locator = this.page.getByTestId( "Confirmation-wrapper", );
  readonly titleLabel: Locator = this.confirmationPageContainer.locator("h1");
  readonly infoCardsContainer: Locator = this.confirmationPageContainer.locator(":scope > div");
  readonly infoCardContainers: Locator = this.infoCardsContainer.locator(":scope > div");
  readonly skipForNowLink: Locator = this.page.getByTestId( "Confirmation-skip-link", );
  /** Returns info card container element based on index. */
  getInfoCardContainerBasedOnIndex(
    index: number,
  ): AuthInfoCardContainerComponent {
    return new AuthInfoCardContainerComponent(
      this.infoCardContainers.nth(index),
    );
  }
  /** Get info card container based on title. */
  async getInfoCardContainerByTitle(
    title: string,
    exactMatch = true,
  ): Promise<AuthInfoCardContainerComponent> {
    for (
      let index = 0;
      index < (await this.infoCardContainers.count());
      index += 1
    ) {
      const card = this.getInfoCardContainerBasedOnIndex(index);
      const cardTitle = await card.getInfoCardTitleLabel();
      if (exactMatch ? cardTitle === title : cardTitle.includes(title))
        return card;
    }
    throw new Error(`${title} info card container was not found!`);
  }
  // ######## UI actions/navigation ########
  /** Navigate to confirmation page. */
  async open(): Promise<void> {
    console.log("Open confirmation page");
    await this.openPath(this.url);
    await this.validatePage();
  }
  /** Click on skip for now link. */
  async clickOnSkipForNowLink(): Promise<void> {
    console.log("Click on skip for now link");
    await this.skipForNowLink.click();
  }
  /** Click on info card button by title. */
  async clickOnCardCTA({
    cardName,
  }: {
    cardName: ConfirmationPageCard;
  }): Promise<void> {
    console.log(`Click on ${await cardName.title.name} card button`);
    await (
      await this.getInfoCardContainerByTitle(await cardName.title.name)
    ).clickOnCTA();
  }
  // ######## UI validations ########
  /** Validate page. */
  async validatePage(): Promise<void> {
    console.log("Validate confirmation page");
    await this.validatePageMarker(
      this.confirmationPageContainer,
      "Confirmation",
    );
  }
  /** Validate info card by title card. */
  async validateInfoCard({
    cardName,
  }: {
    cardName: ConfirmationPageCard;
  }): Promise<void> {
    console.log(`Validate ${await cardName.title.name} card container`);
    await (
      await this.getInfoCardContainerByTitle(await cardName.title.name)
    ).validateInfoCardContainer();
  }
  /** Validate info cards section. */
  async validateCardsSection(): Promise<void> {
    console.log("Validate cards section");
    for (const card of [
      ConfirmationPageCards.APPLY_FOR_IB_PAY,
      ConfirmationPageCards.TAKE_HOME_TOUR,
      ConfirmationPageCards.ADD_CARD,
      ConfirmationPageCards.ADD_EMPLOYEE,
    ])
      await this.validateInfoCard({ cardName: card });
  }
  /** Validate confirmation page look and feel. */
  async validatePageElements(): Promise<void> {
    console.log("Validate confirmation page elements");
    await expect(this.titleLabel, "Confirmation page title").toHaveText( await IbStrings.AUTH_CONFIRMATION_TITLE.name, );
    await this.validateCardsSection();
    await expect(this.skipForNowLink, "Skip for now link").toHaveText( await IbStrings.AUTH_CONFIRMATION_SKIP_FOR_NOW.name, );
    await expect(this.skipForNowLink, "Skip for now href").toHaveAttribute( "href", expect.stringContaining("homepage"), );
  }
}
