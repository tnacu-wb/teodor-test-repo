import { expect, type Locator } from "@playwright/test";
import { RatePresentationSectionComponent } from "@components/pi/hotelDetails/ratePresentationSection.component";

/** BB rate presentation section with the expandable rates control. */
export class RatePresentationSectionBBComponent extends RatePresentationSectionComponent {
  private readonly bbContainer: Locator;

  constructor(container: Locator) {
    super(container);
    this.bbContainer = container;
  }

  // ######## UI elements/properties ########
  get showMoreLessRatesLink(): Locator {
    return this.bbContainer.locator(
      '[data-testid="hdp_roomTypeShowMoreRatesLink"]',
    );
  }

  // ######## UI actions/navigation ########
  /** Expand or collapse the available BB rates when the control is displayed. */
  async clickOnExpandCollapseRatesLink(): Promise<void> {
    console.log("Click on Expand/Collapse rates link");
    if (await this.showMoreLessRatesLink.isVisible()) {
      await this.showMoreLessRatesLink.scrollIntoViewIfNeeded();
      await this.showMoreLessRatesLink.click();
    }
  }

  // ######## UI validations ########
  /** Validate the visibility of the expand/collapse rates link. */
  async validateShowMoreLessRatesLink(isDisplayed = true): Promise<void> {
    console.log(`Validate show more/less link displayed=${isDisplayed}`);
    if (isDisplayed)
      await expect( this.showMoreLessRatesLink, "Show more/less rates link", ).toBeVisible();
    else
      await expect( this.showMoreLessRatesLink, "Show more/less rates link", ).toBeHidden();
  }
}
