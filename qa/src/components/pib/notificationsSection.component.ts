import { expect, type Locator, type Page } from "@playwright/test";
/** Notifications section on Homepage/Spending page */
export class NotificationsSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly notificationsAccountSuspendedContainer: Locator = this.page.getByTestId("Notifications-AccountSuspended");
  readonly notificationsAccountSuspendedTitleLabel: Locator = this.notificationsAccountSuspendedContainer.locator("h5");
  readonly notificationsAccountSuspendedDescriptionLabel: Locator = this.notificationsAccountSuspendedContainer.locator("div");
  readonly notificationsProfileUpdateRequiredContainer: Locator = this.page.getByTestId("Notifications-ProfileUpdateRequired");
  readonly notificationsProfileUpdateRequiredCloseButton: Locator = this.notificationsProfileUpdateRequiredContainer.locator("button");
  readonly notificationsProfileUpdateRequiredTitleLabel: Locator = this.notificationsProfileUpdateRequiredContainer.locator("h5");
  readonly notificationsProfileUpdateRequiredDescriptionLabel: Locator = this.notificationsProfileUpdateRequiredContainer.locator("div");
  readonly editProfileLink: Locator = this.notificationsProfileUpdateRequiredContainer.locator("div a");
  readonly notificationsAdHocContainer: Locator = this.page.getByTestId( "Notifications-AdHocNotification", );
  readonly notificationsAdHocTitleLabel: Locator = this.notificationsAdHocContainer.locator("h5");
  readonly notificationsAdHocDescriptionLabel: Locator = this.notificationsAdHocContainer.locator("div");
  readonly notificationEmployeeRequestsContainer: Locator = this.page.getByTestId("Notifications-EmployeeRequests");
  readonly notificationEmployeeRequestsLabel: Locator = this.notificationEmployeeRequestsContainer.locator("div");
  readonly notificationEmployeeRequestsCloseButton: Locator = this.page.getByTestId("Notifications-EmployeeRequests-CloseButton");
  readonly notificationEmployeeRequestsManageEmployeesLink: Locator = this.notificationEmployeeRequestsLabel.locator("a");
  readonly notificationIncompleteMainEmployeeContainer: Locator = this.page.getByTestId("Notifications-IncompleteMainEmployee");
  readonly notificationIncompleteMainEmployeeTitleLabel: Locator = this.notificationIncompleteMainEmployeeContainer.locator("h5");
  readonly notificationIncompleteMainEmployeeDescriptionLabel: Locator = this.notificationIncompleteMainEmployeeContainer.locator("div");
  readonly notificationIncompleteMainEmployeeLink: Locator = this.notificationIncompleteMainEmployeeContainer.locator("div a");
  readonly makeAPaymentButton: Locator = this.page.getByTestId( "Notifications-AccountSuspended-Inline-Make-a-payment-Button", );
  /** Get notification color. */
  async getNotificationColorByElement(
    notificationContainer: Locator,
  ): Promise<string> {
    console.log("Get notification color");
    return notificationContainer.evaluate(
      (element) => getComputedStyle(element).backgroundColor,
    );
  }
  // ######## UI actions/navigation ########
  /** Click on edit profile link. */
  async clickEditProfileLink(): Promise<void> {
    console.log("Click on edit profile link");
    await this.editProfileLink.click();
  }
  /** Click close button from review changes modal. */
  async clickCloseButton(): Promise<void> {
    console.log("Click on close button");
    await this.notificationsProfileUpdateRequiredCloseButton.click();
  }
  /** Click on make a payment button. */
  async clickMakeAPaymentButton(): Promise<void> {
    console.log("Click on make a payment button");
    await this.makeAPaymentButton.click();
  }
  /** Click on Manage employees link. */
  async clickManageEmployeesLink(): Promise<void> {
    console.log("Click on Manage employees link");
    await this.notificationEmployeeRequestsManageEmployeesLink.click();
  }
  /** Validate account suspended notification. */
  async clickNotificationEmployeeRequestsCloseButton(): Promise<void> {
    console.log("Click on Notification Employee Requests Close button");
    await this.notificationEmployeeRequestsCloseButton.click();
  }
  // ######## UI validations ########
  /** Validate account suspended notification is not displayed. */
  async validateAccountSuspendedNotification(_data: {
    accountName: string;
    accountNumber: string;
  }): Promise<void> {
    console.log("Validate account suspended notification");
    await expect( this.notificationsAccountSuspendedContainer, "Account suspended notification", ).toBeVisible();
  }
  /** Validate profile update required notification. */
  async validateAccountSuspendedNotificationIsNotDisplayed(): Promise<void> {
    console.log("Validate account suspended notification is not displayed");
    await expect( this.notificationsAccountSuspendedContainer, "Account suspended notification", ).not.toBeVisible();
  }
  /** Validate incomplete main employee notification. */
  async validateProfileUpdateRequiredNotification(): Promise<void> {
    console.log("Validate profile update required notification");
    await expect( this.notificationsProfileUpdateRequiredContainer, "Profile update notification", ).toBeVisible();
  }
  /** Validate click on “Edit personal profile” redirects the user to “My profile”page. */
  async validateIncompleteMainEmployeeNotification(): Promise<void> {
    console.log("Validate incomplete main employee notification");
    await expect( this.notificationIncompleteMainEmployeeContainer, "Incomplete employee notification", ).toBeVisible();
  }
  /** Validate user can close profile update required notification. */
  async validateUserIsRedirectedToMyProfilePage(): Promise<void> {
    console.log(
      "Validate click on Edit personal profile redirects to My profile",
    );
    await this.clickEditProfileLink();
  }
  /** Validate ad-hoc notification. */
  async validateCloseProfileUpdateRequiredNotification(): Promise<void> {
    console.log(
      "Validate profile update required notification can be discarded",
    );
    await this.clickCloseButton();
    await expect( this.notificationsProfileUpdateRequiredContainer, "Profile update notification", ).not.toBeVisible();
  }
  /** Validate Employee requests notification. */
  async validateAdHocNotification({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate ad-hoc notification");
    if (isDisplayed)
      await expect( this.notificationsAdHocContainer, "Ad-hoc notification", ).toBeVisible();
    else
      await expect( this.notificationsAdHocContainer, "Ad-hoc notification", ).not.toBeVisible();
  }
  /** Validate employee requests notification. */
  async validateEmployeeRequestsNotification({
    isDisplayed = true,
    noOfEmployeeRequests: _noOfEmployeeRequests,
  }: {
    isDisplayed?: boolean;
    noOfEmployeeRequests?: number;
  } = {}): Promise<void> {
    console.log("Validate Employee requests notification");
    if (isDisplayed)
      await expect( this.notificationEmployeeRequestsContainer, "Employee requests notification", ).toBeVisible();
    else
      await expect( this.notificationEmployeeRequestsContainer, "Employee requests notification", ).not.toBeVisible();
  }
}
