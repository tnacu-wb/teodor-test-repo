import { type Locator, expect } from '@playwright/test';
import { Strings } from '../../test-data/strings';
import { BasePage } from './base.page';
import { AmendEntireStayMealsSectionComponent } from '../../components/shared/amendBooking/amendEntireStayMealsSection.component';
import { AmendStayDatesSectionComponent } from '../../components/shared/amendBooking/amendStayDatesSection.component';
import { RoomAndGuestSectionComponent } from '../../components/shared/amendBooking/roomAndGuestSection.component';
import { AmendBookingSummarySectionComponent } from '../../components/shared/amendBooking/bookingSummarySection.component';

/**
 * Amendment Booking Page - handles the flow of modifying an existing booking.
 *
 * The primary use case is changing room type from Double to Family (by adding a child).
 * Includes room/guest section editing, availability checking, cost validation,
 * and confirmation.
 *
 * **Indexing convention:** All room references in this page object are **0-indexed**.
 * Room 0 is the first room, room 1 is the second room, etc.
 */
export class AmendBookingPage extends BasePage {
  // ######## UI elements/properties ########

  // Page loaded indicator. The amend page renders multiple equivalent signals depending on
  // the booking state and client-side flow. Wait for the booking shell or the primary editing
  // controls rather than a single stale title selector.
  readonly pageLoadedIndicator: Locator = this.page.locator([
    '[data-testid="amend-page-title"]',
    '[data-testid="amend-rooms-and-guests-section"]',
    '[data-testid="amend-booking-summary-section"]',
    '[data-testid="amend-confirm-changes-button"]',
  ].join(', '));
  readonly pageTitleLabel: Locator = this.page.getByTestId('amend-page-title');
  readonly amendmentPageErrorAlert: Locator = this.page.locator('main').getByRole('alert');
  readonly sectionButtons: Locator = this.page.locator('main button[data-testid^="Button-"]');
  readonly stayDatesSectionButton: Locator = this.sectionButtons.nth(0);
  readonly roomAndGuestsSectionButton: Locator = this.sectionButtons.nth(1);
  readonly yourMealsSectionButton: Locator = this.sectionButtons.nth(2);
  readonly extrasSectionButton: Locator = this.sectionButtons.nth(3);

  // Rooms and Guests section
  readonly addRoomCard: Locator = this.page.locator('[data-testid="add-room-card-amend"]');

  // Availability
  readonly checkAvailabilityButton: Locator = this.page.getByTestId('add-room-modal-availability-button');

  // Room update
  readonly updateRoomButton: Locator = this.page.getByTestId('add-edit-room-button');

  // Confirm changes
  readonly confirmChangesButton: Locator = this.page.locator('button[name="confirm-changes-button"]');
  readonly roomAvailableLabel: Locator = this.page.getByTestId('AlertDescription');
  readonly roomUnavailableLabel: Locator = this.page.locator('[data-testid="ModalBody"] [status="error"] + [data-testid="AlertDescription"]');
  readonly payOnArrivalRadioButton: Locator = this.page.getByTestId('radio-box-inside_PAY_ON_ARRIVAL');
  readonly payNowRadioButton: Locator = this.page.getByTestId('radio-box-inside_PAY_NOW');
  readonly cancelEditButton: Locator = this.page.getByTestId('add-room-close-button');
  readonly extrasDropdown: Locator = this.extrasSectionButton;
  readonly privacyPolicyTitleLabel: Locator = this.page.getByTestId('amend-PrivacyPolicy-Main-Title');
  readonly privacyPolicyDescriptionLabel: Locator = this.page.getByTestId('amend-PrivacyPolicy-Main-Description');
  readonly privacyPolicyNoticeLink: Locator = this.page.getByTestId('amend-PrivacyPolicy-PrivacyNotice');
  readonly privacyPolicyExpandButton: Locator = this.page.getByTestId('amend-PrivacyPolicy-ExpandButton');
  readonly privacyPolicyExtendedSection: Locator = this.page.getByTestId('amend-PrivacyPolicy-Expanded-Wrapper');

  // Booking summary / total cost
  readonly totalCostAmount: Locator = this.page.locator(
    '[data-testid="amend-booking-summary-of-payments"] h6[data-testid^="amend-booking-summary-"]'
  ).last();

  readonly stayDatesSection = new AmendStayDatesSectionComponent();
  readonly roomAndGuestSection = new RoomAndGuestSectionComponent();
  readonly yourMealsSection = new AmendEntireStayMealsSectionComponent();
  readonly bookingSummarySection = new AmendBookingSummarySectionComponent();

  /** Return the room edit button for a zero-based room index. */
  editRoomButton(roomIndex: number): Locator {
    return this.page.locator(
      `button[data-testid="room-info-card-amend-edit-button-${roomIndex + 1}"]`,
    );
  }

  // ######## UI actions/navigation ########

  /**
   * Open the Rooms and Guests editing section on the amendment page.
   */
  async clickRoomAndGuestsSection(): Promise<void> {
    console.log('Click Room and Guests section');
    await this.roomAndGuestsSectionButton.waitFor({ state: 'visible' });
    await this.roomAndGuestsSectionButton.click();
  }

  /** Open the add-room modal from the room-and-guests section. */
  async clickAddRoomCard(): Promise<void> {
    console.log('Click add room card');
    await this.addRoomCard.first().waitFor({ state: 'visible' });
    await this.addRoomCard.first().click();
  }

  /** Select the number of adults in the add/edit room modal. */
  async selectNumberOfAdults(count: number): Promise<void> {
    console.log(`Select ${count} adults for room`);
    await this.roomAndGuestSection.selectAdults(count);
  }

  /** Fill and submit the mandatory fields for an added room. */
  async addRoom(title: string, firstName: string, lastName: string): Promise<void> {
    console.log(`Add room for guest ${title} ${firstName} ${lastName}`);
    await this.roomAndGuestSection.setMandatoryFields(title, firstName, lastName);
    await this.clickCheckAvailability();
    await this.clickUpdateRoom();
  }

  /** Open the stay-dates section so arrival/departure values can be amended. */
  async clickStayDates(): Promise<void> {
    console.log('Click Stay Dates section');
    await this.stayDatesSectionButton.waitFor({ state: 'visible', timeout: 30000 });
    await this.stayDatesSectionButton.click();
  }

  /** Open the meals section for amendment-level meal changes. */
  async clickYourMealsSection(): Promise<void> {
    console.log('Click Your Meals section');
    await this.yourMealsSectionButton.waitFor({ state: 'visible', timeout: 30000 });
    await this.yourMealsSectionButton.click();
  }

  /** Open the Extras section. */
  async clickExtrasDropdown(): Promise<void> {
    console.log('Click Extras section');
    await this.extrasDropdown.waitFor({ state: 'visible' });
    await this.extrasDropdown.click();
  }

  /** Expand the amendment privacy-policy details. */
  async clickPrivacyPolicyFindOutMore(): Promise<void> {
    console.log('Click privacy policy Find out more');
    await this.privacyPolicyExpandButton.waitFor({ state: 'visible' });
    await this.privacyPolicyExpandButton.click();
  }

  /** Select Pay on arrival and verify the radio option is checked. */
  async selectPayOnArrivalRadioButton(): Promise<void> {
    console.log('Click on Pay on arrival option');
    await this.payOnArrivalRadioButton.waitFor({ state: 'visible', timeout: 30000 });
    await this.payOnArrivalRadioButton.click();
    await expect(this.payOnArrivalRadioButton, 'Pay on arrival option should be selected').toHaveAttribute('data-checked', '');
  }

  /** Select Pay now and verify the radio option is checked. */
  async selectPayNowRadioButton(): Promise<void> {
    console.log('Click on Pay now option');
    await this.payNowRadioButton.waitFor({ state: 'visible', timeout: 30000 });
    await this.payNowRadioButton.click();
    await expect(this.payNowRadioButton, 'Pay now option should be selected').toHaveAttribute('data-checked', '');
  }

  /**
   * Edit a specific room by its index.
   * Opens the edit controls for the specified room.
   *
   * @param options.roomIndex - 0-indexed room number (0 = first room)
   */
  async editSpecificRoom(options: { roomIndex: number }): Promise<void> {
    const { roomIndex } = options;
    console.log(`Edit room at index ${roomIndex}`);
    // The real DOM uses 1-indexed room numbers: room-info-card-amend-edit-button-1
    const editButton = this.editRoomButton(roomIndex);
    await editButton.waitFor({ state: 'visible', });
    await editButton.click();
  }

  /**
   * Select the number of children for the room being edited.
   *
   * @param count - Number of children to set (e.g. 1 for one child)
   */
  async selectNumberOfChildren(count: number): Promise<void> {
    console.log(`Select ${count} children for room`);
    await this.roomAndGuestSection.selectChildren(count);
  }

  /**
   * Select a room type from the available options.
   *
   * @param roomType - The room type name to select (e.g. 'Family')
   */
  async selectRoomType(roomType: string): Promise<void> {
    console.log(`Select room type=${roomType}`);
    await this.roomAndGuestSection.selectRoomType(roomType);
  }

  /**
   * Trigger availability check for the new room configuration.
   * Waits for the check availability button to be enabled and clicks it.
   */
  async clickCheckAvailability(): Promise<void> {
    console.log('Click check availability button');
    await this.roomAndGuestSection.checkAvailability();
  }

  /**
   * Confirm the room update after availability has been checked.
   * Waits for the update room button to appear (indicating availability is confirmed).
   */
  async clickUpdateRoom(): Promise<void> {
    console.log('Click update room button');
    await this.roomAndGuestSection.clickAddEditRoomButton();
  }

  /** Close the add/edit room modal. */
  async closeEditModal(): Promise<void> {
    console.log('Close edit room modal');
    await this.cancelEditButton.waitFor({ state: 'visible' });
    await this.cancelEditButton.click();
  }

  /**
   * Confirm the amendment changes.
   * Clicks the confirm button and waits for navigation to the confirmation/history page.
   */
  async clickConfirmChanges(): Promise<void> {
    console.log('Click confirm changes button');
    const confirmButton = this.confirmChangesButton.or(this.page.getByRole('button', { name: await Strings.CONFIRM_CHANGES.name }));
    await confirmButton.waitFor({ state: 'visible', timeout: 30000 });
    await confirmButton.click();
    await this.page.waitForURL(url =>
      url.pathname.endsWith('/amend/booking-confirmation') && url.searchParams.get('status') === 'success',
      { timeout: 30000 },
    );
  }

  // ######## UI validations ########

  /**
   * Return the displayed amendment booking-summary total after validating it is available.
   *
   * @returns The non-empty total text shown in the amendment summary.
   */
  async getTotalCostAmount(): Promise<string> {
    console.log('Validate and read amendment booking summary total');
    await expect(
      this.totalCostAmount,
      'Amendment booking summary total must remain visible'
    ).toBeVisible();
    const totalCost = (await this.totalCostAmount.textContent())?.trim() ?? '';
    expect(totalCost, 'Amendment booking summary total must be available').toBeTruthy();
    return totalCost;
  }

  /**
   * Validate that updating an amendment changes the displayed booking-summary total.
   *
   * @param originalTotal - Total displayed before the amendment was updated.
   */
  async validateTotalCostHasChanged(originalTotal: string): Promise<void> {
    console.log(`Validate amendment total changed from ${originalTotal}`);
    let previousTotal = originalTotal;
    let stablePolls = 0;
    await expect
      .poll(async () => {
        const currentTotal = await this.getTotalCostAmount();
        console.log(`Current amendment total while polling: ${currentTotal}`);
        if (currentTotal === originalTotal) {
          stablePolls = 0;
        } else if (currentTotal === previousTotal) {
          stablePolls++;
        } else {
          stablePolls = 0;
        }
        previousTotal = currentTotal;
        return stablePolls >= 2;
      }, {
        message: 'Amendment booking summary total should change after the update',
        timeout: 30000,
        intervals: [1000, 2000, 2000],
      })
      .toBe(true);
  }

  /**
   * Validate that the Amendment Booking Page has fully loaded.
   * Reloads once when the page renders its transient error state instead of amendment data.
   * @throws Error if the page loaded indicator is not visible within 30s
   */
  async validatePage(): Promise<void> {
    console.log('Validate amendment page loaded');
    const amendmentPageState = this.pageTitleLabel.or(this.amendmentPageErrorAlert).first();
    await expect(amendmentPageState, 'Amendment page title or error state').toBeVisible({ timeout: 30000 });
    if (await this.amendmentPageErrorAlert.isVisible()) {
      console.log('Amendment page returned an error state; refresh once.');
      await this.page.reload({ waitUntil: 'domcontentloaded' });
    }
    await expect(this.pageTitleLabel, 'Amendment booking page title').toBeVisible({ timeout: 30000 });
  }

  /** Validate the amendment page title. */
  async validatePageTitle(): Promise<void> {
    console.log('Validate Amendment booking page title');
    await expect(this.pageTitleLabel, 'Amendment page title').toHaveText(await Strings.AMEND_YOUR_BOOKING.name);
  }

  /** Validate the availability result after checking a room. */
  async validateRoomAvailableLabel(isAvailable = true): Promise<void> {
    console.log(`Validate room available label: ${isAvailable}`);
    const label = isAvailable ? this.roomAvailableLabel : this.roomUnavailableLabel;
    const expectedText = isAvailable ? await Strings.ROOM_AVAILABLE.name : await Strings.ROOM_NOT_AVAILABLE.name;
    await expect(label, 'Room availability result').toBeVisible({ timeout: 30000 });
    await expect(label, 'Room availability result text').toContainText(expectedText);
  }

  /** Validate the privacy-policy/data-collection section. */
  async validateDataCollectionSection(bookingFlowDictionary: {
    privacyPolicy: { title: string; description: string; linkLabel: string; moreInfoLabel: string };
  }): Promise<void> {
    console.log('Validate Data Collection section');
    const policy = bookingFlowDictionary.privacyPolicy;
    const description = policy.description.replace(/<([^>]+)>|\n/g, '').replace('&nbsp;', '');
    await expect(this.privacyPolicyTitleLabel, 'Data collection title').toHaveText(policy.title);
    await expect(this.privacyPolicyDescriptionLabel, 'Data collection description').toHaveText(description);
    await expect(this.privacyPolicyNoticeLink, 'Data collection privacy notice link').toHaveText(policy.linkLabel);
    await expect(this.privacyPolicyExpandButton, 'Data collection expand button').toHaveText(policy.moreInfoLabel.trim());
    await expect(this.privacyPolicyExtendedSection, 'Data collection extended section').toBeVisible();
  }

  /**
   * Validate that a room info card contains the expected text.
   *
   * @param options.roomIndex - 0-indexed room number (0 = first room)
   * @param options.text - Text expected to be present in the room info card
   */
  async validateRoomInfoCardIncludesText(options: { roomIndex: number; text: string }): Promise<void> {
    const { roomIndex, text } = options;
    console.log(`Validate room ${roomIndex} info card contains: ${text}`);
    // Real DOM uses 1-indexed: room-info-card-amend-1
    const roomNumber = roomIndex + 1;
    const roomInfoCard = this.page.locator(
      `[data-testid="room-info-card-amend-${roomNumber}"]`
    );
    await expect(roomInfoCard, `Room ${roomNumber} info card should be visible before checking its contents`).toBeVisible();
    await expect(roomInfoCard, `Room ${roomNumber} info card should contain text: ${text}`).toContainText(text);
  }

  /**
   * Validate that the edited room has returned to the amendment page in its updated state.
   */
  async validateRoomSuccessfullyUpdated(options: { roomIndex: number }): Promise<void> {
    console.log(`Validate room ${options.roomIndex} successfully updated`);
    const roomNumber = options.roomIndex + 1;
    const roomInfoCard = this.page.locator(`[data-testid="room-info-card-amend-${roomNumber}"]`);
    const successNotification = this.page.locator('[data-testid="edit-room-success-notification-Alert"]');
    await expect(successNotification, `Room ${roomNumber} update success notification should be visible`).toBeVisible();
    await expect(roomInfoCard, `Room info card ${roomNumber} should be visible`).toBeVisible({ });
    await expect(this.page.locator(`[data-testid="room-info-card-amend-edit-button-${roomNumber}"]`), `Edit button for room ${roomNumber} should be enabled after the card refresh`).toBeEnabled({ timeout: 30000 });
  }

  /**
   * Validate the number of adults and children displayed for specific room indexes.
   *
   * @param options.roomIndexesArray - Array of 0-indexed room numbers to validate
   * @param options.expectedAdults - Expected number of adults per room
   * @param options.expectedChildren - Expected number of children per room
   */
  async validateAdultsAndChildrenNumbersForRoomIndexes(options: {
    roomIndexesArray: number[];
    expectedAdults: number;
    expectedChildren: number;
  }): Promise<void> {
    const { roomIndexesArray, expectedAdults, expectedChildren } = options;
    console.log(`Validate adults/children for rooms: ${roomIndexesArray.join(', ')} (${expectedAdults} adults, ${expectedChildren} children)`);

    for (const roomIndex of roomIndexesArray) {
      // Real DOM uses 1-indexed: room-info-card-amend-1
      const roomNumber = roomIndex + 1;
      const roomInfoCard = this.page.locator(
        `[data-testid="room-info-card-amend-${roomNumber}"]`
      );
      await expect(roomInfoCard, `Room ${roomNumber} info card should be visible before checking guest counts`).toBeVisible();

      const adultsLabel = expectedAdults === 1 ? await Strings.ADULT.name : await Strings.ADULTS.name;
      await expect(roomInfoCard, `Room ${roomNumber} should show ${expectedAdults} adult(s)`).toContainText(`${expectedAdults} ${adultsLabel}`, { ignoreCase: true });

      if (expectedChildren > 0) {
        const childrenLabel = expectedChildren === 1 ? await Strings.CHILD.name : await Strings.CHILDREN.name;
        await expect(roomInfoCard, `Room ${roomNumber} should show ${expectedChildren} child(ren)`).toContainText(`${expectedChildren} ${childrenLabel}`, { ignoreCase: true });
      }
    }
  }

  /**
   * Validate the total cost display on the amendment page.
   * Checks whether the cost has changed from, or remains the same as, the given baseline value.
   *
   * @param expectedCost - The baseline total cost string to compare against (e.g. '£85.00')
   * @param isChanged - Whether the cost is expected to have changed from expectedCost (false = cost unchanged)
   */
  async validateTotalCostIsUpdated(expectedCost: string, isChanged: boolean): Promise<void> {
    console.log(`Validate total cost is ${isChanged ? 'changed from' : 'unchanged at'} ${expectedCost}`);
    const normalizedExpectedCost = Number.parseFloat(expectedCost.replace(/[^0-9.-]/g, ''));

    const totalCost = expect.poll(async () => {
      const currentTotal = await this.getTotalCostAmount();
      return Number.parseFloat(currentTotal.replace(/[^0-9.-]/g, ''));
    }, {
      message: `Total cost should ${isChanged ? 'change from' : 'remain'} ${expectedCost}`,
      timeout: 30000,
      intervals: [500, 1000, 2000],
    });

    if (isChanged) {
      await totalCost.not.toBe(normalizedExpectedCost);
    } else {
      await totalCost.toBe(normalizedExpectedCost);
    }

    // the total cost can recalculate in multiple steps; wait until 2 consecutive reads match before returning the settled value
    await expect(async () => {
      const firstRead = await this.getTotalCostAmount();
      await new Promise((resolve) => setTimeout(resolve, 300));
      const secondRead = await this.getTotalCostAmount();
      expect(firstRead, 'Total cost amount did not stabilize after update').toBe(secondRead);
    }).toPass({ timeout: 10000 });
  }

  /**
   * Validate the exact total cost amount and currency code displayed.
   *
   * @param expectedCost - The expected total cost string (e.g. '85.00')
   * @param currencyCode - The expected currency code (e.g. 'GBP' or '£')
   */
  async validateTotalCostAmountAndCurrency(expectedCost: string, currencyCode: string): Promise<void> {
    console.log(`Validate total cost amount ${expectedCost} and currency ${currencyCode}`);
    await this.totalCostAmount.waitFor({ state: 'visible', });
    const costText = await this.totalCostAmount.textContent() ?? '';
    // Check for the amount (may be formatted as "£92.20" or "92.20")
    const numericAmount = expectedCost.replace(/[^0-9.]/g, '');
    expect(costText, `Total cost should contain amount ${numericAmount}`).toContain(numericAmount);
    // Check currency — accept both symbol (£) and code (GBP)
    const currencySymbol = currencyCode === 'GBP' ? '£' : currencyCode;
    expect(costText, `Total cost should contain currency ${currencySymbol}`).toContain(currencySymbol);
  }

}
