import { type Locator } from '@playwright/test';
import { BasePage } from './base.page';

/**
 * Group Web Form page - allows clients to make a group booking (minimum 10 rooms). Mirrors
 * qa/reference `components/common/groupWebForm.page.js`.
 */
export class GroupWebFormPage extends BasePage {
  // ######## UI elements/properties ########

  readonly titleButton: Locator = this.page.locator('button[data-testid="DropdownComp-GroupBookingsPage-Title-InnerDropdown-menuButton"]');
  readonly firstNameInput: Locator = this.page.locator('input#firstName');
  readonly lastNameInput: Locator = this.page.locator('input#lastName');
  readonly phoneNumberInput: Locator = this.page.locator('input[name="phoneNumber"]');
  readonly emailInput: Locator = this.page.locator('input#emailAddress');
  readonly continueButtonContactDetails: Locator = this.page.locator('[data-testid="GroupBookingsPage-ContactDetailsContinue"]');
  readonly bookerTypesCheckbox: Locator = this.page.locator('div[data-testid="GroupBookingsPage-BookerType"] div[data-testid="radio-box-wrapper"]');
  readonly stayTypesCheckbox: Locator = this.page.locator('div[data-testid="GroupBookingsPage-Purpose"] div[data-testid="radio-box-wrapper"]');
  readonly selectReasonDropdown: Locator = this.page.locator('button[data-testid="DropdownComp-GroupBookingsPage-reasonForVisit-InnerDropdown-menuButton"]');
  readonly otherReasonOfStayInput: Locator = this.page.locator('input[data-testid="input-reasonForVisitOther"]');
  readonly continueButtonBookingDetails: Locator = this.page.locator('button[data-testid="GroupBookingsPage-BookingDetailsContinue"]');
  readonly datePicker: Locator = this.page.locator('input[aria-label="datepicker-input"]');
  readonly totalCountOfRooms: Locator = this.page.locator('[data-fieldname="RoomTotalCount"] strong:nth-child(2)');
  readonly travelingWithChildrenCheckbox: Locator = this.page.locator('div[data-fieldname="isTravellingWithChild"] label');
  readonly accessibleRoomCheckbox: Locator = this.page.locator('div[data-fieldname="isAccessibleRoom"] label');
  readonly atLeastOneAdultNotification: Locator = this.page.locator('div[data-testid="GroupBookingsPage-atLeastOneAdultNotification"]');
  readonly textArea: Locator = this.page.locator('textarea[name="comments"]');

  // ######## UI actions/navigation ########

  /** Fill contact details (first name, last name, phone, email) and continue. */
  async fillContactDetails({
    firstName,
    lastName,
    phoneNumber,
    email,
  }: { firstName: string; lastName: string; phoneNumber: string; email: string }): Promise<void> {
    console.log(`Fill group contact details for ${firstName} ${lastName}`);
    await this.firstNameInput.fill(firstName);
    await this.lastNameInput.fill(lastName);
    await this.phoneNumberInput.fill(phoneNumber);
    await this.emailInput.fill(email);
    await this.continueButtonContactDetails.click();
  }

  // ######## UI validations ########
}
