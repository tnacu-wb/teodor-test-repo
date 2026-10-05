import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { ReportCardsSectionComponent } from "./reportCardsSection.component";
/** InnBusiness application > Spending > InnBusiness Pay section */
export class SpendingInnBusinessPaySectionComponent {
  private readonly page: Page = global.page;
  static readonly IB_PAY_TAB_URL = "/spending?tab=innbusiness-pay";
  static readonly REPORT_CARDS_CONTAINER_XPATH =
    '//a[@data-testid="AboutReportsCard"]/parent::form/parent::div';
  static readonly REPORT_CARD_CONTAINERS_XPATH =
    '//a[@data-testid="AboutReportsCard"]/parent::form/parent::div//a';
  // ######## UI elements/properties ########
  readonly innBusinessPaySection: Locator = this.page.getByTestId( "InnBusinessPayTab-container", );
  readonly descriptionLabel: Locator = this.innBusinessPaySection.locator("p");
  readonly linkAnExistingAccountButton: Locator = this.page.getByTestId( "NoAccountBanner-LinkAccountButton", );
  readonly manageAccountButton: Locator = this.page.getByTestId( "ManageAccountButton-trigger", );
  readonly manageAccountLinks: Locator = this.page.locator( 'div[role="menuitem"]', );
  readonly reportCards = new ReportCardsSectionComponent({
    reportCardsContainerSelector:
      SpendingInnBusinessPaySectionComponent.REPORT_CARDS_CONTAINER_XPATH,
    reportCardContainersSelector:
      SpendingInnBusinessPaySectionComponent.REPORT_CARD_CONTAINERS_XPATH,
  });
  // ######## UI actions/navigation ########
  /** Navigate to Inn Business Pay tab. */
  async navigateToInnBusinessPayTab(): Promise<void> {
    console.log("Navigate to Inn Business Pay tab");
    await this.page.goto(
      `${global.browser.options.baseUrl}${SpendingInnBusinessPaySectionComponent.IB_PAY_TAB_URL}`,
    );
  }
  /** Toggle Manage account dropdown. */
  async toggleManageAccountDropdown(shouldBeOpened: boolean): Promise<void> {
    console.log("Toggle Manage account dropdown");
    await this.manageAccountButton.click();
    if (shouldBeOpened)
      await expect( this.manageAccountLinks.first(), "Manage account menu", ).toBeVisible();
    else
      await expect( this.manageAccountLinks.first(), "Manage account menu", ).not.toBeVisible();
  }
  /** Click Manage account option. */
  async clickManageAccountOption(option: string): Promise<void> {
    console.log(`Click Manage account option ${option}`);
    await this.manageAccountLinks.filter({ hasText: option }).click();
  }
  // ######## UI validations ########
  /** Validate spending subtitle. */
  async validateSubtitle(): Promise<void> {
    console.log("Validate Inn Business Pay subtitle");
    await expect( this.descriptionLabel, "Inn Business Pay description", ).toHaveText(await IbStrings.DISPLAYING_ALL_SPENDING_FROM.name);
  }
  /** Validate Manage account button. */
  async validateManageAccountButton({
    isEnabled = true,
  }: { isEnabled?: boolean } = {}): Promise<void> {
    console.log("Validate Manage account button");
    await expect(this.manageAccountButton, "Manage account button").toHaveText( await IbStrings.MANAGE_ACCOUNT.name, );
    if (isEnabled)
      await expect( this.manageAccountButton, "Manage account button", ).toBeEnabled();
    else
      await expect( this.manageAccountButton, "Manage account button", ).toBeDisabled();
  }
  /** Validate Manage account content. When exactMatch is false, only checks the supplied links are present among the displayed ones. */
  async validateManageAccountContent({
    linksArray,
    exactMatch = true,
  }: {
    linksArray: { name: Promise<string> }[];
    exactMatch?: boolean;
  }): Promise<void> {
    console.log("Validate Manage account content");
    if (exactMatch) {
      await expect( this.manageAccountLinks, "Manage account link count", ).toHaveCount(linksArray.length);
      for (const [index, link] of linksArray.entries())
        await expect( this.manageAccountLinks.nth(index), `Manage account link ${index}`, ).toHaveText(await link.name);
      return;
    }
    const displayedLinkNames = await this.manageAccountLinks.allTextContents();
    for (const link of linksArray) {
      const linkName = await link.name;
      expect(displayedLinkNames, `Manage account link ${linkName}`).toContain(linkName);
    }
  }
}
