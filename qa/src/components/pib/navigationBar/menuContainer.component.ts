import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";

/**
 * Menu container on IB
 */
export class MenuContainerComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly sideBar: Locator = this.page.locator( 'nav.flex div[data-testid="FirstLevelNav-container"], nav[data-testid="SidebarMobile-container"]', );
  readonly homeIcon: Locator = this.sideBar .getByTestId("Home-Sidebar-Link") .locator("img");
  readonly spendingIcon: Locator = this.sideBar .getByTestId("Spending-Sidebar-Link") .locator("img");
  readonly manageIcon: Locator = this.sideBar.locator( '[data-testid="Manage-Sidebar-Link"] img', );
  readonly bookingsIcon: Locator = this.sideBar .getByTestId("Bookings-Sidebar-Link") .locator("img");
  readonly contactIcon: Locator = this.sideBar .getByTestId("ContactUs-Sidebar-Link") .locator("img");
  readonly secondLevelNav: Locator = this.page .locator( '[data-testid="SecondLevelNav-container"], [data-testid="SidebarMobile-activeLinkSecondLevel"]', ) .first();
  readonly manageEmployeesLabel: Locator = this.secondLevelNav.getByTestId( "ManageEmployees-Sidebar-Link", );
  readonly bookingAllowancesLabel: Locator = this.secondLevelNav.getByTestId( "BookingAllowances-Sidebar-Link", );
  readonly cardManagementLabel: Locator = this.secondLevelNav.getByTestId( "CardManagement-Sidebar-Link", );
  readonly costCentresLabel: Locator = this.secondLevelNav.getByTestId( "CostCentreManagement-Sidebar-Link", );
  readonly employeeQuestionsLabel: Locator = this.secondLevelNav.getByTestId( "EmployeeQuestions-Sidebar-Link", );
  readonly companyDetailsLabel: Locator = this.secondLevelNav.getByTestId( "CompanyDetails-Sidebar-Link", );
  readonly mobileSecondLevelCollapseIcon: Locator = this.page.getByTestId( "Second-Level-Collapse-Icon", );
  /** Get second level sidebar element label by text. */
  getSecondLevelSidebarElementLabelByTitle(title: string): Locator {
    return this.secondLevelNav.locator("span").filter({ hasText: title });
  }
  /** Element based on the toggle state. */
  getToggleSidebarIcon(isExpanded = true): Locator {
    return this.page
      .getByRole("button", {
        name: `${isExpanded ? "Collapse" : "Expand"} sidebar`,
      })
      .locator("img");
  }
  // ######## UI actions/navigation ########
  /** Click Toggle Side bar arrow icon in order to collapse/expand sidebar menu. */
  async clickToggleSidebarArrow({
    isExpanded = true,
  }: { isExpanded?: boolean } = {}): Promise<void> {
    console.log("Click Toggle Sidebar arrow");
    await this.getToggleSidebarIcon(isExpanded).click();
  }
  /** Click mobile collapse arrow icon in order to collapse sidebar second level menu. */
  async clickMobileCollapseArrow(): Promise<void> {
    console.log("Click mobile collapse arrow");
    await this.mobileSecondLevelCollapseIcon.click();
  }
  /** Click Sidebar Spending option icon. */
  async clickSpendingIconOption(): Promise<void> {
    console.log("Click Sidebar Spending option");
    await this.spendingIcon.click();
  }
  /** Click Sidebar Manage option icon. */
  async clickManageIconOption(): Promise<void> {
    console.log("Click Sidebar Manage option");
    await this.manageIcon.click();
  }
  /** Click Home option icon. */
  async clickHomeIconOption(): Promise<void> {
    console.log("Click Home option");
    await this.homeIcon.click();
  }
  /** Click Bookings option icon. */
  async clickBookingsIconOption(): Promise<void> {
    console.log("Click Bookings option");
    await this.bookingsIcon.click();
  }
  /** Click Contact option icon. */
  async clickContactIconOption(): Promise<void> {
    console.log("Click Contact option");
    await this.contactIcon.click();
  }
  /** Access booking allowances page by clicking booking allowances label. */
  async clickBookingAllowancesLabel(): Promise<void> {
    console.log("Click Booking allowances");
    await this.bookingAllowancesLabel.click();
  }
  /** Access Card Management page by clicking Card Management label. */
  async clickCardManagementLabel(): Promise<void> {
    console.log("Click Card Management");
    await this.cardManagementLabel.click();
    await expect(this.page.getByTestId('ManageCardsPage-container'), 'Card Management page loaded').toBeVisible();
  }
  /** Click Cost Centres Management label. */
  async clickCostCentresLabel(): Promise<void> {
    console.log("Click Cost Centres Management");
    await this.costCentresLabel.click();
  }
  // ######## UI validations ########
  /** Validate first level navigation sidebar. */
  async validateNavigationBarFirstLevelMenuItems({
    isManagerUser = false,
  }: { isExpanded?: boolean; isManagerUser?: boolean } = {}): Promise<void> {
    console.log("Validate first level navigation sidebar");
    await expect(this.homeIcon, "Home icon").toBeVisible();
    await expect( this.sideBar.getByTestId("Home-Sidebar-Link"), "Home label", ).toContainText(await IbStrings.HOME.name);
    await expect( this.sideBar.getByTestId("Spending-Sidebar-Link"), "Spending label", ).toContainText(await IbStrings.SPENDING.name);
    await expect( this.sideBar.getByTestId("Bookings-Sidebar-Link"), "Bookings label", ).toContainText(await IbStrings.BOOKINGS.name);
    await expect( this.sideBar.getByTestId("ContactUs-Sidebar-Link"), "Contact label", ).toContainText(await IbStrings.CONTACT.name);
    if (isManagerUser)
      await expect(this.manageIcon, "Manage icon").toBeVisible();
    else
      await expect( this.manageIcon, "Manage icon for non-manager", ).not.toBeVisible();
  }
  /** Validate second level navigation sidebar. */
  async validateNavigationBarSecondLevelMenuItems({
    isGermanCompany = false,
  }: { isGermanCompany?: boolean } = {}): Promise<void> {
    console.log("Validate second level navigation sidebar");
    await expect( this.manageEmployeesLabel, "Manage employees label", ).toContainText(await IbStrings.MANAGE_EMPLOYEES.name);
    await expect( this.bookingAllowancesLabel, "Booking allowances label", ).toContainText(await IbStrings.BOOKING_ALLOWANCES.name);
    await expect( this.cardManagementLabel, "Card management label", ).toContainText(await IbStrings.CARD_MANAGEMENT.name);
    if (isGermanCompany)
      await expect(this.costCentresLabel, "Cost centres label").toContainText( await IbStrings.MANAGE_COST_CENTRES.name, );
    await expect( this.employeeQuestionsLabel, "Employee questions label", ).toContainText(await IbStrings.EMPLOYEE_QUESTIONS.name);
    await expect( this.companyDetailsLabel, "Company details label", ).toContainText(await IbStrings.COMPANY_DETAILS_MENU.name);
  }
  /** Validate second lever sidebar menu item is selected. */
  async validateSecondLevelSidebarItemIsSelected({
    menuItemTitle,
  }: {
    menuItemTitle: string;
  }): Promise<void> {
    console.log(`Validate selected sidebar item=${menuItemTitle}`);
    await expect( this.getSecondLevelSidebarElementLabelByTitle(menuItemTitle), "Selected sidebar item", ).toHaveClass(/font-bold/);
  }
}
