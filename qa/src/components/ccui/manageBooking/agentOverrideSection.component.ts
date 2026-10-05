import { expect, type Locator } from '@playwright/test';
import { ApiCalls } from '@api/graphql/apiCalls';
import { Strings } from '../../../test-data/strings';
import { CcuiComponent } from '../baseCcui.component';

/**
 * Agent override section that is part of the CCUI Manage Booking page.
 * Mirrors qa/reference/test/pages/components/ccui/manageBooking/agentOverrideSection.js.
 */
export class AgentOverrideSectionComponent extends CcuiComponent {
  // ######## UI elements/properties ########
  readonly modal: Locator = this.page.locator('section[data-testid="ModalContent"]');
  readonly titleLabel: Locator = this.page.locator('div[data-testid="ModalTitle"]');
  readonly agentOverrideContainer: Locator = this.page.locator('div[data-testid="AgentOverrideModal-Container"]');
  readonly selectAReasonLabel: Locator = this.agentOverrideContainer.locator('p').first();
  readonly selectAReasonInput: Locator = this.page.locator('button[data-testid="DropdownComp-AgentOverrideModal-SelectReason-InnerDropdown-menuButton"]');
  readonly selectAReasonInputPlaceholder: Locator = this.selectAReasonInput.locator('span span');
  readonly selectAReasonValueLabel: Locator = this.page.locator('div[data-testid="DropdownComp-AgentOverrideModal-SelectReason-InnerDropdown-buttonSelectorLabel"]');
  readonly selectAReasonDropDown: Locator = this.page.locator('div[data-testid="DropdownComp-AgentOverrideModal-SelectReason-InnerDropdown-entireList"]');
  readonly selectAReasonDropDownList: Locator = this.page.locator('div[data-testid="DropdownComp-AgentOverrideModal-SelectReason-InnerDropdown-entireList"] button[data-testid^="DropdownComp-AgentOverrideModal-SelectReason-InnerDropdown-"]');
  readonly callerNameInput: Locator = this.page.locator('input[data-testid="input-callerName"]');
  readonly callerNameFormErrorMessageLabel: Locator = this.page.locator('div[data-testid="input-callerName-FormErrorMessage"]');
  readonly saveButton: Locator = this.page.locator('button[data-testid="AgentOverrideModal-Submit-Button"]');
  readonly cancelButton: Locator = this.page.locator('button[data-testid="AgentOverrideModal-Close"]');
  readonly xButton: Locator = this.page.locator('button[data-testid="ModalCloseButton"]');
  readonly managerNameField: Locator = this.page.locator('div[data-testid="AgentOverrideModal-ManagerName"]');
  readonly managerNameInput: Locator = this.page.locator('input[data-testid="input-managerName"]');

  /**
   * Get cancellation reasons matching the supplied text.
   * @param hotelId Text used to identify the cancellation reason.
   */
  async getCancellationReasonsByText(hotelId: string): Promise<Array<Record<string, unknown>>> {
    console.log('Get cancellation reasons array');
    return ApiCalls.getCancellationReasons(hotelId);
  }
  /**
   * Get special cancellation reasons matching the supplied text.
   * @param hotelId Text used to identify the special cancellation reason.
   */
  async getSpecialCancellationReasonsByText(hotelId: string): Promise<Array<Record<string, unknown>>> {
    console.log('Get special cancellation reasons array');
    const reasons = await this.getCancellationReasonsByText(hotelId);
    return reasons.filter((reason) => String(reason.name ?? '').startsWith('MA'));
  }

  // ######## UI actions/navigation ########
  /**
   * Click Select a Reason dropdown input.
   * @param isDropdownDisplayed Whether the dropdown should be displayed after clicking.
   */
  async clickSelectAReasonDropDownInput(isDropdownDisplayed = true): Promise<void> { console.log('Click Select a reason dropdown input'); await this.selectAReasonInput.scrollIntoViewIfNeeded(); await this.selectAReasonInput.click(); await this.validateDisplayState(this.selectAReasonDropDown, 'Select a reason dropdown', isDropdownDisplayed); }
  /** Close modal by clicking outside modal. */
  async closeModalByClickingOutsideOfModal(): Promise<void> { console.log('Close modal by clicking outside modal'); await this.page.mouse.click(0, 0); await expect(this.modal, 'Agent override modal').toBeHidden(); }
  /** Click Save button. */
  async clickSaveButton(): Promise<void> { console.log('Click Save button'); await this.saveButton.scrollIntoViewIfNeeded(); await this.saveButton.click(); await expect(this.agentOverrideContainer, 'Agent override container').toBeHidden(); }
  /** Close Agent Override modal. */
  async closeAgentOverrideModal(): Promise<void> { console.log("Click the 'Close' booking modal button"); await this.xButton.click(); await expect(this.modal, 'Agent override modal').toBeHidden(); }
  /**
   * Select reason from dropdown.
   * @param reason Reason label to select.
   */
  async selectReasonFromDropdown(reason: string): Promise<void> { console.log(`Select reason=${reason}`); const options = await this.selectAReasonDropDownList.allTextContents(); const optionIndex = options.findIndex((option) => option === reason); if (optionIndex < 0) throw new Error(`Cancellation reason was not found: ${reason}`); await this.selectAReasonDropDownList.nth(optionIndex).click(); }
  /**
   * Set caller name.
   * @param callerName Caller name to enter, or null to clear the field.
   * @param pressTab Whether to move focus away after filling.
   */
  async setCallerName(callerName: string | null = null, pressTab = true): Promise<void> { console.log('Set caller name'); await this.fillInput(this.callerNameInput, callerName, pressTab); }
  /**
   * Set manager name.
   * @param managerName Manager name to enter, or null to clear the field.
   * @param pressTab Whether to move focus away after filling.
   */
  async setManagerName(managerName: string | null = null, pressTab = true): Promise<void> { console.log('Set manager name'); await this.fillInput(this.managerNameInput, managerName, pressTab); }
  /**
   * Set the standard automation manager name.
   * @param pressTab Whether to move focus away after filling.
   */
  async setManagerNameFromRegex(pressTab = true): Promise<void> { console.log('Set manager name from regex'); await this.setManagerName('Automation Manager', pressTab); }

  // ######## UI validations ########
  /** Validate modal title. */
  async validateModalTitle(): Promise<void> { console.log('Validate Agent Override modal title'); await expect(this.titleLabel, 'Agent override modal title').toBeVisible(); await expect(this.titleLabel, 'Agent override modal title text').toContainText(await Strings.AGENT_OVERRIDE.name); }
  /** Validate Select a Reason title. */
  async validateSelectAReasonTitle(): Promise<void> { console.log('Validate Select a reason title'); await expect(this.selectAReasonLabel, 'Select a reason title').toBeVisible(); await expect(this.selectAReasonLabel, 'Select a reason title text').toContainText(await Strings.SELECT_A_REASON.name); }
  /**
   * Validate Select a Reason input.
   * @param expectedValue Expected selected reason value.
   */
  async validateSelectAReasonInput(expectedValue: string): Promise<void> { console.log(`Validate Select a reason input=${expectedValue}`); await expect(this.selectAReasonInput, 'Select a reason input').toBeVisible(); if (expectedValue) await expect(this.selectAReasonValueLabel, 'Select a reason input value text').toContainText(expectedValue); else await expect(this.selectAReasonInputPlaceholder, 'Select a reason input placeholder').toContainText(await Strings.SELECT_A_REASON.name); }
  /**
   * Validate Select a Reason dropdown list.
   * @param hotelId Text identifying the expected cancellation reason option.
   */
  async validateSelectAReasonDropDownList(hotelId: string): Promise<void> { console.log('Validate Select a reason dropdown list'); const cancellationReasons = await this.getCancellationReasonsByText(hotelId); await expect(this.selectAReasonDropDown, 'Select a reason dropdown').toBeVisible(); await expect(this.selectAReasonDropDownList, 'Cancellation reason list length').toHaveCount(cancellationReasons.length); for (const [index, reason] of cancellationReasons.entries()) await expect(this.selectAReasonDropDownList.nth(index), `Cancellation reason option ${index}`).toHaveText(String(reason.name ?? '')); }
  /**
   * Validate caller name and its error state.
   * @param value Expected caller name value.
   * @param shouldBeValid Whether the caller name should be valid.
   * @param errorFeedback Expected caller-name error feedback.
   */
  async validateCallerName({ value = '', shouldBeValid = false, errorFeedback = Strings.INVALID_CHARACTERS_IN_CALLER_NAME.name }: { value?: string; shouldBeValid?: boolean; errorFeedback?: string | Promise<string> } = {}): Promise<void> { console.log('Validate caller name'); await expect(this.callerNameInput, 'Caller name value').toHaveValue(value); await this.validateDisplayState(this.callerNameFormErrorMessageLabel, `Caller name error: ${await errorFeedback}`, !shouldBeValid); if (!shouldBeValid) await expect(this.callerNameFormErrorMessageLabel, 'Caller name error feedback').toContainText(await errorFeedback); }
  /**
   * Validate Save button.
   * @param isClickable Whether the Save button should be enabled.
   */
  async validateSaveButton(isClickable = false): Promise<void> { console.log('Validate Save button'); await this.validateEnabledState(this.saveButton, 'Save button clickable state', isClickable); await expect(this.saveButton, 'Save button label').toContainText(await Strings.SAVE.name); }
  /** Validate Cancel button. */
  async validateCancelButton(): Promise<void> { console.log('Validate Cancel button'); await expect(this.cancelButton, 'Cancel button').toBeVisible(); await expect(this.cancelButton, 'Cancel button label').toContainText(await Strings.CANCEL_AGENT_OVERRIDE.name); }
  /** Validate Close modal button. */
  async validateCloseModalButton(): Promise<void> { console.log('Validate Close modal button'); await expect(this.xButton, 'Close modal button').toBeVisible(); }
  /** Validate manager name field is displayed. */
  async validateManagerNameFieldIsDisplayed(): Promise<void> { console.log('Validate manager name field is displayed'); await expect(this.managerNameField, 'Manager name field').toBeVisible(); }
  /**
   * Validate manager name and its error state.
   * @param value Expected manager name value.
   * @param shouldBeValid Whether the manager name should be valid.
   * @param errorFeedback Expected manager-name error feedback.
   */
  async validateManagerName({ value = '', shouldBeValid = false, errorFeedback = Strings.INVALID_CHARACTERS_IN_MANAGER_NAME.name }: { value?: string; shouldBeValid?: boolean; errorFeedback?: string | Promise<string> } = {}): Promise<void> { console.log('Validate manager name'); await expect(this.managerNameInput, 'Manager name value').toHaveValue(value); const errorLocator = this.page.getByText(await errorFeedback, { exact: true }).first(); await this.validateDisplayState(errorLocator, 'Manager name error feedback', !shouldBeValid); }
}