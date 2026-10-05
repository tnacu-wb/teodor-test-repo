import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";

/** InnBusiness application > share with a colleague section from resume application and application saved pages */
export class ShareWithColleagueSectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly shareWithColleagueSection: Locator = this.page.locator( 'div[data-testid="SavedApplication-form-share-with-colleague"], div[data-testid="ResumeApplication-form-share-with-colleague"]', );
  readonly applicationShareWithAColleagueLabel: Locator = this.page.locator( '[data-testid*="-share-colleague-title"]', );
  readonly applicationNotificationTextLabel: Locator = this.page.locator( "div.bg-notificationAlertBg span", );
  readonly applicationSearchByNameOrEmailInput: Locator = this.page.getByTestId( "PeoplePicker-Form-Input", );
  readonly applicationSuggestionsDropdown: Locator = this.page.getByTestId( "PeoplePicker-IB-Form-People-Picker-Dropdown", );
  readonly employeeSuggestionButton: Locator = this.applicationSuggestionsDropdown.locator("button");
  readonly applicationSearchByNameOrEmailErrorTooltipLabel: Locator = this.page.getByTestId("PeoplePicker-Error-Tooltip");
  readonly applicationShareApplicationButton: Locator = this.page.locator( 'button[data-testid*="-share-app"]', );
  readonly applicationSharedEmail: Locator = this.page.locator( 'span[data-testid*="-participant"]', );
  readonly applicationRemoveSharedButton: Locator = this.page.locator( 'button[data-testid*="delete-shared-link-"]', );
  readonly shareLimitReachedNotificationLabel: Locator = this.page.locator( 'div[data-testid="SavedApplication-notification-limit-5-users"], div[data-testid="ResumeApplication-notification-limit-5-users"]', );
  /** Return employee suggestion by email. */
  getDynamicEmployeeLabelByEmail(employeeEmail: string): Locator {
    return this.applicationSuggestionsDropdown.locator("span", {
      hasText: employeeEmail,
    });
  }
  // ######## UI actions/navigation ########
  /** Click Share application button. */
  async clickShareApplicationButton(): Promise<void> {
    console.log("Click Share application button");
    await this.applicationShareApplicationButton.click();
  }
  /** Click Remove shared button. */
  async clickRemoveSharedApplicationButton(index = 0): Promise<void> {
    console.log("Click Remove shared application button");
    await this.applicationRemoveSharedButton.nth(index).click();
  }
  /** Search by name or email. */
  async searchEmployeeByNameOrEmail({
    userNameOrEmail,
    pressTab = false,
  }: {
    userNameOrEmail: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Search employee ${userNameOrEmail}`);
    await this.applicationSearchByNameOrEmailInput.fill(userNameOrEmail);
    if (pressTab) await this.applicationSearchByNameOrEmailInput.press("Tab");
  }
  /** Search employee by email. */
  async searchEmployeeByEmail(employeeEmail: string): Promise<void> {
    console.log(`Search employee by email ${employeeEmail}`);
    await this.applicationSearchByNameOrEmailInput.fill(employeeEmail);
    await this.validateCardHolderSuggestion(employeeEmail);
    await this.getDynamicEmployeeLabelByEmail(employeeEmail).click();
  }
  // ######## UI validations ########
  /** Validate search by name or email input. */
  async validateSearchByNameInput({
    searchedTerm = "",
    isValid = true,
  }: { searchedTerm?: string; isValid?: boolean } = {}): Promise<void> {
    console.log("Validate Search by name or email input");
    await expect( this.applicationSearchByNameOrEmailInput, "Search employee input", ).toHaveValue(searchedTerm);
    if (!isValid)
      await expect( this.applicationSearchByNameOrEmailErrorTooltipLabel, "Search employee error", ).toBeVisible();
  }
  /** Validate shared email. */
  async validateSharedEmail(sharedEmail: string, index = 0): Promise<void> {
    console.log("Validate shared email");
    await expect( this.applicationSharedEmail.nth(index), "Shared email", ).toHaveText(sharedEmail);
  }
  /** Validate share with a colleague section. */
  async validateShareWithColleagueSection({
    isManagerUser = true,
    isShareClickable = false,
    isMaxShares = false,
  }: {
    isManagerUser?: boolean;
    isShareClickable?: boolean;
    isMaxShares?: boolean;
  } = {}): Promise<void> {
    console.log("Validate share with a colleague section");
    if (!isManagerUser) {
      await expect( this.shareWithColleagueSection, "Share with colleague section", ).not.toBeVisible();
      return;
    }
    if (isMaxShares) {
      await expect( this.applicationShareApplicationButton, "Share application button", ).not.toBeVisible();
      await expect( this.shareLimitReachedNotificationLabel, "Share limit notification", ).toHaveText(await IbStrings.MAXIMUM_USER_LIMIT_REACHED.name);
      return;
    }
    await expect( this.shareWithColleagueSection, "Share with colleague section", ).toBeVisible();
    await expect( this.applicationShareApplicationButton, "Share application button", ).toHaveText(await IbStrings.SHARE_APPLICATION.name);
    if (isShareClickable)
      await expect( this.applicationShareApplicationButton, "Share application button", ).toBeEnabled();
    else
      await expect( this.applicationShareApplicationButton, "Share application button", ).toBeDisabled();
  }
  /** Validate card holder suggestion. */
  async validateCardHolderSuggestion(expectedText: string): Promise<void> {
    console.log("Validate card holder suggestion");
    await expect( this.employeeSuggestionButton, "Employee suggestion", ).toContainText(expectedText);
  }
}
