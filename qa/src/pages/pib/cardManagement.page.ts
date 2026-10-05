import { expect, type Locator } from "@playwright/test";
import {
  CentrallyStoredSectionComponent,
  InnBusinessPaySectionComponent,
  MenuContainerComponent,
  ToastNotificationSectionComponent,
} from "../../components/pib";
import { Constants } from "../../test-data/constants";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * InnBusiness application > Manage > Card management
 */
export class CardManagementPage extends BasePibPage {
  readonly url = "manage/cards";

  // ######## UI elements/properties ########
  readonly pageContainer: Locator = this.page.getByTestId( "ManageCardsPage-container", );
  readonly centrallyStoredTab: Locator = Constants.BROWSER_RESOLUTIONS.isDesktop() ? this.page.getByTestId("ManageCardsPage-centrallyStoredTab") : this.page.getByTestId("Manage-Cards-Inn-Business-Sidebar-Link");
  readonly innBusinessPayTab: Locator = Constants.BROWSER_RESOLUTIONS.isDesktop() ? this.page.getByTestId("ManageCardsPage-innbusinessPayTab") : this.page.getByTestId("Manage-Cards-Inn-Business-Pay-Sidebar-Link");
  readonly thirdLevelExpandButton: Locator = this.page.getByTestId( "Third-Level-Expand-Button", );
  readonly thirdLevelActiveButtonLabel: Locator = this.page .getByTestId("Mobile-Nav-activeLinkThirdLevel") .locator("span");
  readonly cardManagementTooltipImage: Locator = this.page.getByTestId( "InnBusinessTab-title-icon", );
  readonly cardManagementTooltipLabel: Locator = this.page.locator( '//div[@data-testid="InnBusinessTab-title-section"]//div/span[1]', );

  // UI components
  readonly innBusinessPaySection = new InnBusinessPaySectionComponent();
  readonly centrallyStoredSection = new CentrallyStoredSectionComponent();
  readonly menuContainer = new MenuContainerComponent();
  readonly toastNotificationSection = new ToastNotificationSectionComponent();

  // ######## UI actions/navigation ########
  /** Open IB Card Management page, optionally authorizing the secure host first. */
  async open({ isAuthorised = false }: { isAuthorised?: boolean } = {}): Promise<void> {
    console.log("Open IB Card Management page");
    if (isAuthorised) {
      const { httpAuthUsername, httpAuthPassword, secureUrl } = global.browser.options;
      if (!httpAuthUsername || !secureUrl) throw new Error('Card Management authorization requires HTTP credentials and a secure URL');
      await this.page.context().setHTTPCredentials({ username: httpAuthUsername, password: httpAuthPassword ?? '' });
      await this.page.goto(secureUrl, { waitUntil: 'domcontentloaded' });
    }
    await this.openPath(this.url);
    await this.validatePage();
  }
  /** Click Centrally Stored tab. */
  async clickCentrallyStoredTab(): Promise<void> {
    console.log("Click Centrally Stored tab");
    if (!Constants.BROWSER_RESOLUTIONS.isDesktop())
      await this.thirdLevelExpandButton.click();
    await this.centrallyStoredTab.click();
  }
  /** Click InnBusiness Pay tab. */
  async clickInnBusinessPayTab(): Promise<void> {
    console.log("Click InnBusiness Pay tab");
    if (!Constants.BROWSER_RESOLUTIONS.isDesktop())
      await this.thirdLevelExpandButton.click();
    await this.innBusinessPayTab.click();
  }

  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Card Management page");
    await this.validatePageMarker(this.pageContainer, "Card Management");
  }
  /** Validate Centrally Stored tab is displayed. */
  async validateCentrallyStoredTab({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate Centrally Stored tab");
    if (isDisplayed)
      await expect( this.centrallyStoredTab, "Centrally Stored tab", ).toBeVisible();
    else
      await expect( this.centrallyStoredTab, "Centrally Stored tab", ).not.toBeVisible();
  }
  /** Validate InnBusiness Pay tab is displayed. */
  async validateInnBusinessPayTab({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate InnBusiness Pay tab");
    if (isDisplayed)
      await expect(this.innBusinessPayTab, "InnBusiness Pay tab").toBeVisible();
    else
      await expect( this.innBusinessPayTab, "InnBusiness Pay tab", ).not.toBeVisible();
  }
  /** Validate Centrally Stored tab is active. */
  async validateCentrallyStoredTabActive(): Promise<void> {
    console.log("Validate Centrally Stored tab is active");
    if (Constants.BROWSER_RESOLUTIONS.isDesktop())
      await expect( this.centrallyStoredTab, "Centrally Stored tab data-state attribute", ).toHaveAttribute("data-state", /active/);
    else
      await expect( this.thirdLevelActiveButtonLabel, "Third level active button label", ).toHaveText(await IbStrings.CENTRALLY_STORED.name);
  }
  /** Validate InnBusiness Pay tab is active. */
  async validateInnBusinessPayTabActive(): Promise<void> {
    console.log("Validate InnBusiness Pay tab is active");
    if (Constants.BROWSER_RESOLUTIONS.isDesktop())
      await expect( this.innBusinessPayTab, "InnBusiness Pay tab data-state attribute", ).toHaveAttribute("data-state", /active/);
    else
      await expect( this.thirdLevelActiveButtonLabel, "Third level active button label", ).toHaveText(await IbStrings.INN_BUSINESS_PAY.name);
  }
  /** Validate Centrally Stored tab URL. */
  async validateCentrallyStoredTabUrl(): Promise<void> {
    console.log("Validate Centrally Stored tab URL");
    this.validateUrl(`${this.url}?tab=centrally-stored`);
  }
  /** Validate InnBusiness Pay tab URL. */
  async validateInnBusinessPayTabUrl(): Promise<void> {
    console.log("Validate InnBusiness Pay tab URL");
    this.validateUrl(`${this.url}?tab=innbusiness-pay`);
  }
  /** Validate card management tooltip. */
  async validateCardManagementTooltip(
    isCentrallyStoredTabDisplayed = true,
  ): Promise<void> {
    console.log("Validate Card Management tooltip");
    await this.cardManagementTooltipImage.hover();
    await expect( this.cardManagementTooltipImage, "Card management tooltip is open", ).toHaveAttribute("data-state", "delayed-open");
    const text = isCentrallyStoredTabDisplayed
      ? await IbStrings.CENTRALLY_STORED_TABLE_TOOLTIP.name
      : await IbStrings.INN_BUSINESS_PAY_TABLE_TOOLTIP.name;
    await expect( this.cardManagementTooltipLabel, "Card Management tooltip text", ).toHaveText(text.replace(/([a-z0-9\)\):.])([A-Z])/g, "$1 $2").trim());
    await this.cardManagementTooltipImage.click();
  }
  /** Validate toast message. */
  async validateToastMessage(expectedMessage: string): Promise<void> {
    console.log(`Validate toast message: ${expectedMessage}`);
    await this.toastNotificationSection.validateToastNotification({
      message: expectedMessage,
    });
  }
}
