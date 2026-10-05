import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { CcuiComponent } from '../baseCcui.component';
import { AgentOverrideSectionComponent } from './agentOverrideSection.component';
import { ChangeLogSectionComponent } from './changeLogSection.component';
import { IdvSectionComponent } from './idvSection.component';

/** Booking Information card section that is part of the CCUI Manage Booking page. */
export class BookingInformationCardSectionComponent extends CcuiComponent {
  // ######## UI elements/properties ########
  readonly bookingInfoCardContainer: Locator = this.page.locator('[data-testid*="BookingInfoCard"], [data-testid*="ReservationInfo"]').first();
  readonly bookingActionsContainer: Locator = this.page.locator('[data-testid*="BookingActions"], [data-testid*="Actions"]').first();
  readonly bookingActionsLabels: Locator = this.bookingActionsContainer.locator('p,a,button');
  readonly bookingReferenceTitleLabel: Locator = this.page.locator('[data-testid*="BookingReference"][data-testid*="Title"], p').filter({ hasText: /Booking reference/i }).first();
  readonly bookingReferenceLabel: Locator = this.page.locator('[data-testid*="BookingReference"][data-testid*="Id"], [data-testid*="BookingReference"]').last();
  readonly cancelBookingButton: Locator = this.page.getByRole('button', { name: /Cancel booking|Buchung stornieren/i });
  readonly amendBookingButton: Locator = this.page.getByTestId('BookingDetailsController-AmendButton');
  readonly bookingReasonLabel: Locator = this.page.locator('h6[data-testid="BookingDetailsControllerReasonLabel"]');
  readonly cancelBookingPopup: Locator = this.page.locator('[data-testid*="CancelBooking"], section[role="dialog"]').first();
  readonly confirmCancelBookingButton: Locator = this.cancelBookingPopup.getByRole('button', { name: /Cancel booking|Confirm|Stornieren/i });
  readonly cancelSuccessMessage: Locator = this.page.locator('[data-testid*="Cancel"][data-testid*="Success"], [data-testid="AlertDescription"]');
  readonly cancelModalCloseButton: Locator = this.cancelBookingPopup.locator('button[aria-label="Close"], [data-testid*="CloseButton"]').first();
  readonly overridePoliciesLabel: Locator = this.bookingActionsContainer.locator('p').filter({ hasText: /Override/i }).first();
  readonly overridePoliciesValidationIcon: Locator = this.page.locator('[data-testid="BookingActions-OverridenSuccess"]');
  readonly bookingDetailsContainer: Locator = this.page.locator('div[data-testid="operaCardReservationInfo-BartBookingdDetailsReservationInformation"]');
  readonly badgeLabel: Locator = this.bookingDetailsContainer.locator('.chakra-badge');
  readonly bookingTypeLabel: Locator = this.bookingDetailsContainer.locator('div > span').nth(1);
  readonly bookingChannelLabel: Locator = this.bookingDetailsContainer.locator('div > span').nth(2);
  readonly rateTypeLabel: Locator = this.bookingDetailsContainer.locator('div > span').nth(3);
  readonly bookingTypeRepeatLabel: Locator = this.bookingDetailsContainer.locator('div > p').nth(0);
  readonly rateTypeRepeatLabel: Locator = this.bookingDetailsContainer.locator('div > p').nth(1);
  readonly paymentTypeRepeatLabel: Locator = this.bookingDetailsContainer.locator(`text=${Strings.PAYMENT_TYPE.data.default ?? 'Payment type'}`).first();
  readonly idvButton: Locator = this.page.locator('button[data-testid="BookingDetailsController-CtaButton"]');
  readonly overridenNotificationContainer: Locator = this.page.locator('div[data-testid="BookingInfoCard-OverridenNotification"]');
  readonly overridenNotificationIcon: Locator = this.overridenNotificationContainer.locator('div[data-testid="svg-container"]');
  readonly overridenNotificationLabel: Locator = this.overridenNotificationContainer.locator('div[data-testid="AlertDescription"]');
  readonly repeatBookingLink: Locator = this.bookingActionsContainer.locator('p,a').filter({ hasText: /Repeat booking/i }).first();
  readonly changeLogLabel: Locator = this.bookingActionsContainer.locator('p').filter({ hasText: /Change log/i }).first();
  readonly dateLabels: Locator = this.bookingInfoCardContainer.locator('[data-testid*="Date"], time');
  readonly idvSection: IdvSectionComponent = new IdvSectionComponent();
  readonly agentOverrideSection: AgentOverrideSectionComponent = new AgentOverrideSectionComponent();
  readonly changeLogSection: ChangeLogSectionComponent = new ChangeLogSectionComponent();
  /** Check whether the booking information card is displayed. */
  async checkIfBookingInfoCardIsDisplayed(): Promise<boolean> { console.log('Check if booking info card is displayed'); return this.bookingInfoCardContainer.isVisible(); }

  /** Get booking dates as ISO `YYYY-MM-DD` strings. */
  async getBookingsDatesArray(): Promise<string[]> {
    console.log('Get Bookings Dates array');
    return (await this.dateLabels.allTextContents()).map((dateLabel) => {
      const parsedDate = new Date(dateLabel.trim());
      if (Number.isNaN(parsedDate.getTime())) throw new Error(`Unable to parse booking date: ${dateLabel}`);
      return parsedDate.toISOString().slice(0, 10);
    });
  }
  /** Get booking reason label text. */
  async getBookingReasonLabelText(): Promise<string> { console.log('Get booking reason label text'); return (await this.bookingReasonLabel.textContent())?.trim() ?? ''; }

  // ######## UI actions/navigation ########
  /** Click ID&V button and wait for its section to open. */
  async clickIdvButton(): Promise<void> { console.log('Click ID&V button'); await this.idvButton.scrollIntoViewIfNeeded(); await this.idvButton.click(); await this.idvSection.idvContainer.waitFor({ state: 'visible' }); }
  /** Click Override Policies label and wait for the override section to open. */
  async clickOverridePoliciesLabel(): Promise<void> { console.log('Click Override Policies Label'); await this.overridePoliciesLabel.scrollIntoViewIfNeeded(); await this.overridePoliciesLabel.click(); await this.agentOverrideSection.agentOverrideContainer.waitFor({ state: 'visible' }); }
  /** Click Change log label and wait for its modal to open. */
  async clickChangeLogLabel(): Promise<void> { console.log('Click Change log link'); await this.changeLogLabel.scrollIntoViewIfNeeded(); await this.changeLogLabel.click(); await this.changeLogSection.changeLogModalContainer.waitFor({ state: 'visible' }); }
  /** Click Repeat Booking action link from the booking information card. */
  async clickRepeatBookingLinkOnBIC(): Promise<void> { console.log('Click Repeat Booking link'); await this.repeatBookingLink.scrollIntoViewIfNeeded(); await this.repeatBookingLink.click(); }
  /** Click Amend booking button. */
  async clickAmendBookingButton(): Promise<void> { console.log('Click Amend booking button'); await this.amendBookingButton.scrollIntoViewIfNeeded(); await this.amendBookingButton.click(); }
  /** Click Cancel booking button. */
  async clickCancelBookingButton(): Promise<void> { console.log('Click Cancel booking button'); await this.cancelBookingButton.scrollIntoViewIfNeeded(); await this.cancelBookingButton.click(); }
  /** Click Confirm cancel booking button. */
  async clickConfirmCancelBookingButton(): Promise<void> { console.log('Click Confirm cancel booking button'); await this.confirmCancelBookingButton.scrollIntoViewIfNeeded(); await this.confirmCancelBookingButton.click(); }
  /** Close cancel modal. */
  async closeCancelModal(): Promise<void> { console.log('Close cancel modal'); await this.cancelModalCloseButton.click(); await expect(this.cancelBookingPopup, 'Cancel booking popup').toBeHidden(); }

  // ######## UI validations ########
  /** Validates that BIC is Displayed. */
  async validateBICIsDisplayed(): Promise<void> { console.log('Validate BIC is displayed'); await expect(this.bookingDetailsContainer, 'Booking Details Container').toBeVisible(); }
  /**
   * Validate Booking reference title.
   * @param bookingReference Expected booking reference.
   */
  async validateBookingReferenceTitle(bookingReference: string): Promise<void> { console.log(`Validate booking reference=${bookingReference}`); await expect(this.bookingReferenceTitleLabel, 'Booking reference title').toBeVisible(); await expect(this.bookingReferenceTitleLabel, 'Booking reference title text').toContainText(`${await Strings.BOOKING_REFERENCE_CCUI.name}:`); await expect(this.bookingReferenceLabel, `${bookingReference} label`).toContainText(bookingReference); }
  /** Validate Actions container. */
  async validateActionsContainer(): Promise<void> { console.log('Validate Actions container'); await expect(this.bookingActionsContainer, 'Booking actions container').toBeVisible(); }
  /**
   * Validate booking action buttons and their states.
   * @param isAmendEnabled Whether Amend should be enabled.
   * @param isCancelEnabled Whether Cancel should be enabled.
   * @param isAmendDisplayed Whether Amend should be displayed.
   * @param isCancelDisplayed Whether Cancel should be displayed.
   */
  async validateButtons({ isAmendEnabled = false, isCancelEnabled = false, isAmendDisplayed = true, isCancelDisplayed = true }: { isAmendEnabled?: boolean; isCancelEnabled?: boolean; isAmendDisplayed?: boolean; isCancelDisplayed?: boolean } = {}): Promise<void> { console.log('Validate buttons'); await this.validateDisplayState(this.amendBookingButton, 'Amend booking button', isAmendDisplayed); if (isAmendDisplayed) await this.validateEnabledState(this.amendBookingButton, 'Amend booking button enabled state', isAmendEnabled); await this.validateDisplayState(this.cancelBookingButton, 'Cancel booking button', isCancelDisplayed); if (isCancelDisplayed) await this.validateEnabledState(this.cancelBookingButton, 'Cancel booking button enabled state', isCancelEnabled); }
  /**
   * Validate Amend Booking button display and enabled state.
   * @param isDisplayed Whether Amend should be displayed.
   * @param isEnabled Whether Amend should be enabled.
   */
  async validateAmendBookingButtonIsDisplayed(isDisplayed = true, isEnabled = true): Promise<void> { console.log(`Validate Amend Booking button displayed=${isDisplayed}, enabled=${isEnabled}`); await this.validateDisplayState(this.amendBookingButton, 'Amend booking button', isDisplayed); if (isDisplayed) await this.validateEnabledState(this.amendBookingButton, 'Amend booking button enabled state', isEnabled); }
  /** Validate cancel booking popup is displayed. */
  async validateCancelBookingPopupDisplayed(): Promise<void> { console.log('Validate cancel booking popup displayed'); await expect(this.cancelBookingPopup, 'Cancel booking popup').toBeVisible(); }
  /**
   * Validate successful cancel booking message is displayed.
   * @param bookingReference Booking reference expected in the success message.
   */
  async validateSuccessfulCancelBookingMessageIsDisplayed(bookingReference: string): Promise<void> { console.log(`Validate successful cancel booking message for ${bookingReference}`); await expect(this.cancelSuccessMessage, 'Successful cancel booking message').toContainText(bookingReference); }
  /**
   * Validate badge.
   * @param isOpera Whether the Opera badge should be displayed; otherwise BART is expected.
   */
  async validateBadge(isOpera = true): Promise<void> { console.log('Validate badge'); await expect(this.badgeLabel, 'Badge label').toHaveText(isOpera ? await Strings.OPERA.name : await Strings.BART.name); }
  /**
   * Validate booking type.
   * @param bookingType Expected booking type.
   */
  async validateBookingType(bookingType: string): Promise<void> { console.log(`Validate booking type=${bookingType}`); await expect(this.bookingTypeLabel, 'Booking type label').toContainText(bookingType); }
  /**
   * Validate booking type from Repeat booking flow.
   * @param bookingType Expected booking type.
   */
  async validateBookingTypeFromRepeatBooking(bookingType: string): Promise<void> { console.log(`Validate booking type=${bookingType}`); await expect(this.bookingTypeRepeatLabel, 'Booking type label').toContainText(bookingType); }
  /**
   * Validate booking channel.
   * @param bookingChannel Expected booking channel.
   */
  async validateBookingChannel(bookingChannel: string): Promise<void> { console.log(`Validate booking channel=${bookingChannel}`); await expect(this.bookingChannelLabel, 'Booking channel label').toContainText(bookingChannel); }
  /**
   * Validate rate type.
   * @param rateType Expected rate type.
   */
  async validateRateType(rateType: string): Promise<void> { console.log(`Validate rate type=${rateType}`); await expect(this.rateTypeLabel, 'Rate type label').toContainText(rateType); }
  /**
   * Validate rate type from Repeat booking flow.
   * @param rateType Expected rate type.
   */
  async validateRateTypeFromRepeatBooking(rateType: string): Promise<void> { console.log(`Validate rate type=${rateType}`); await expect(this.rateTypeRepeatLabel, 'Rate type label').toContainText(rateType); }
  /** Validate payment type is displayed. */
  async validatePaymentTypeLabelIsDisplayed(isDisplayed = true): Promise<void> { console.log('Validate payment type label'); await this.validateDisplayState(this.paymentTypeRepeatLabel, 'Payment type label', isDisplayed); }
  /**
   * Validate payment type text from BIC.
   * @param paymentType Expected payment type.
   */
  async validatePaymentTypeText(paymentType: string): Promise<void> { console.log(`Validate payment type text=${paymentType}`); await expect(this.paymentTypeRepeatLabel, 'Payment type label').toContainText(paymentType); }
  /**
   * Validate Over-ride policies link.
   * @param isValidated Whether the override validation icon should be displayed.
   */
  async validateOverridePoliciesLink(isValidated = false): Promise<void> { console.log(`Validate Over-ride policies link and its validation=${isValidated}`); await this.validateActionLink({ webElement: this.overridePoliciesLabel, labelText: Strings.OVERRIDE_POLICIES.name }); await this.validateDisplayState(this.overridePoliciesValidationIcon, 'Over-ride policies validation icon', isValidated); }
  /**
   * Validate an action link and its label.
   * @param webElement Action-link locator to validate.
   * @param labelText Expected action-link label.
   */
  async validateActionLink({ webElement, labelText }: { webElement: Locator; labelText: string | Promise<string> }): Promise<void> { const resolvedLabelText = await labelText; console.log(`Validate action link=${resolvedLabelText}`); await expect(webElement, resolvedLabelText).toBeVisible(); await expect(webElement, `${resolvedLabelText} text`).toContainText(resolvedLabelText); }
  /** Validate Override notification. */
  async validateOverrideNotification(): Promise<void> { console.log('Validate Override notification'); await expect(this.overridenNotificationContainer, 'Override notification container').toBeVisible(); await expect(this.overridenNotificationIcon, 'Override notification icon').toBeVisible(); await expect(this.overridenNotificationLabel, 'Override notification label').toContainText(await Strings.ROOM_RATE_OVER_RIDE_POLICIES.name); }
}