import { type Locator } from "@playwright/test";
import {
  ApplicationsSectionComponent,
  BannerPromoSectionComponent,
  ExistingAccountSectionComponent,
  FooterSectionComponent,
  HeaderSectionComponent,
  HomepageInnBusinessPaySectionComponent,
  MenuContainerComponent,
  NotificationsSectionComponent,
  SearchConsoleComponent,
  UpcomingBookingsSectionComponent,
  WhatsNewSectionComponent,
} from "../../components/pib";
import { FeaturesToggles } from "../../utils/featuresToggles";
import { BasePibPage } from "./basePib.page";

/**
 * Home page of the Inn Business application
 */
export class HomePage extends BasePibPage {
  readonly url = "homepage";
  // ######## UI elements/properties ########
  readonly bodyContainer: Locator = this.page.locator("main.grow");
  readonly menuContainer = new MenuContainerComponent();
  readonly headerSection = new HeaderSectionComponent();
  readonly searchConsole = new SearchConsoleComponent();
  readonly notifications = new NotificationsSectionComponent();
  readonly bannerPromo = new BannerPromoSectionComponent();
  readonly innBusinessPay = new HomepageInnBusinessPaySectionComponent();
  readonly applicationsSection = new ApplicationsSectionComponent();
  readonly upcomingBookingsSection = new UpcomingBookingsSectionComponent();
  readonly whatsNewSection = new WhatsNewSectionComponent();
  readonly footerSection = new FooterSectionComponent();
  readonly existingAccountSection = new ExistingAccountSectionComponent();
  // ######## UI actions/navigation ########
  /** Open IB homepage. */
  async open(): Promise<void> {
    console.log("Open IB homepage");
    await this.openPath(this.url);
    await this.validatePage();
    await FeaturesToggles.applyDefaultFeaturesTogglesOverrides();
  }
  /** Click to apply for an InnBusiness Pay application based on the visible home-page container. */
  async clickToApplyForPayApplication(): Promise<void> {
    console.log("Click to apply for InnBusiness Pay application");
    const applicationsContainerVisible = await this.applicationsSection.applicationsContainer
      .waitFor({ state: "visible", timeout: 5000 })
      .then(() => true)
      .catch(() => false);
    if (applicationsContainerVisible) {
      await this.applicationsSection.clickApplyForANewAccountButton();
    } else {
      await this.innBusinessPay.innBusinessPayNoAccountBanner.clickApplyNowButton();
    }
  }
  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate IB homepage");
    await this.validatePageMarker(this.bodyContainer, "IB home");
  }
  /** Validate body container. */
  async validateBodyContainer(): Promise<void> {
    console.log("Validate body container");
    await this.validatePageMarker(this.bodyContainer, "IB home body");
  }
}
