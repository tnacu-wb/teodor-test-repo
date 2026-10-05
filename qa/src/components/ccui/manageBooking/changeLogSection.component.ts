import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { CcuiComponent } from '../baseCcui.component';

/**
 * Change log section that is part of the CCUI Manage Booking page.
 * Mirrors qa/reference/test/pages/components/ccui/manageBooking/changeLogSection.js.
 */
export class ChangeLogSectionComponent extends CcuiComponent {
  // ######## UI elements/properties ########
  readonly changeLogModalContainer: Locator = this.page.locator('section[data-testid="ChangeLog-ModalContent"]');
  readonly changeLogModalHeader: Locator = this.page.locator('[data-testid="ChangeLog-ModalHeader"]');
  readonly changeLogModalTitleLabel: Locator = this.page.locator('[data-testid="ChangeLog-ModalTitle"]');
  readonly changeLogModalCloseIconButton: Locator = this.page.locator('[data-testid="ChangeLog-ModalCloseIcon"]');
  readonly changeLogModalBody: Locator = this.page.locator('[data-testid="ChangeLog-ModalBody"]');
  readonly dateHeaderLabel: Locator = this.changeLogModalContainer.locator('th div').filter({ hasText: /^Date$/ }).first();
  readonly timeHeaderLabel: Locator = this.changeLogModalContainer.locator('th div').filter({ hasText: /^Time$/ }).first();
  readonly actionTypeHeaderLabel: Locator = this.changeLogModalContainer.locator('th').nth(2).locator('div');
  readonly descriptionHeaderLabel: Locator = this.changeLogModalContainer.locator('th div').filter({ hasText: /^Description$/ }).first();
  readonly userHeaderLabel: Locator = this.changeLogModalContainer.locator('th').nth(4).locator('div');
  readonly actionTypeFilterExpandButton: Locator = this.page.locator('[data-testid="FilterExxpand-actionType"]');
  readonly userFilterExpandButton: Locator = this.page.locator('[data-testid="FilterExxpand-user"]');
  readonly actionTypeFilterSearchInput: Locator = this.page.locator('[data-testid="FilterSearch-actionType"]');
  readonly actionTypeFilterApplyButton: Locator = this.page.locator('[data-testid="FilterApply-actionType"]');
  readonly popoverFilterCloseIconButton: Locator = this.page.locator('button[data-testid="TableFilter-PopoverCloseIcon"]');
  readonly changeLogTableRows: Locator = this.changeLogModalContainer.locator('tbody tr');
  readonly actionDescriptionExpandCollapseButtons: Locator = this.page.locator('[data-testid^="Expand-Collapse-Chevron-col-actionDescription-row-"]');

  /**
   * Get action description expand/collapse button by row index.
   * @param rowIndex Zero-based change-log row index.
   */
  getActionDescriptionExpandCollapseButtonByRowIndex({ rowIndex }: { rowIndex: number }): Locator { return this.page.locator(`[data-testid="Expand-Collapse-Chevron-col-actionDescription-row-${rowIndex}"]`); }
  /**
   * Get description cell by row index.
   * @param rowIndex Zero-based change-log row index.
   */
  getDescriptionCellByRowIndex({ rowIndex }: { rowIndex: number }): Locator { return this.changeLogTableRows.nth(rowIndex).locator('td:nth-child(4) div div'); }

  // ######## UI actions/navigation ########
  /** Click Action Type filter expand button. */
  async clickActionTypeFilterExpandButton(): Promise<void> { console.log('Click Action Type filter expand button'); await this.actionTypeFilterExpandButton.scrollIntoViewIfNeeded(); await this.actionTypeFilterExpandButton.click(); }
  /** Click User filter expand button. */
  async clickUserFilterExpandButton(): Promise<void> { console.log('Click User filter expand button'); await this.userFilterExpandButton.scrollIntoViewIfNeeded(); await this.userFilterExpandButton.click(); }
  /**
   * Click action description expand/collapse button by row index.
   * @param rowIndex Zero-based change-log row index.
   */
  async clickActionDescriptionExpandCollapseButtonByRowIndex({ rowIndex }: { rowIndex: number }): Promise<void> { console.log(`Click action description expand/collapse button for row index=${rowIndex}`); const button = this.getActionDescriptionExpandCollapseButtonByRowIndex({ rowIndex }); await button.scrollIntoViewIfNeeded(); await button.click(); }
  /** Click Change Log modal close icon button. */
  async clickChangeLogModalCloseIconButton(): Promise<void> { console.log('Click Change Log modal close icon button'); await this.changeLogModalCloseIconButton.click(); await expect(this.changeLogModalContainer, 'Change Log modal container').toBeHidden(); }

  // ######## UI validations ########
  /** Validate Change Log modal is displayed. */
  async validateChangeLogModalIsDisplayed(): Promise<void> { console.log('Validate Change Log modal is displayed'); await expect(this.changeLogModalContainer, 'Change Log modal content').toBeVisible(); await expect(this.changeLogModalHeader, 'Change Log modal header').toBeVisible(); await expect(this.changeLogModalBody, 'Change Log modal body').toBeVisible(); }
  /** Validate Change Log modal title. */
  async validateChangeLogModalTitle(): Promise<void> { console.log('Validate Change Log modal title'); await expect(this.changeLogModalTitleLabel, 'Change Log modal title').toContainText(await Strings.CHANGE_LOG_TITLE.name); }
  /** Validate Change Log table headers. */
  async validateChangeLogTableHeaders(): Promise<void> { console.log('Validate Change Log table headers are displayed'); await expect(this.dateHeaderLabel, 'Date header').toHaveText(await Strings.DATE_LABEL.name); await expect(this.timeHeaderLabel, 'Time header').toHaveText(await Strings.TIME.name); await expect(this.actionTypeHeaderLabel, 'Action type header').toHaveText(await Strings.ACTION_TYPE.name); await expect(this.descriptionHeaderLabel, 'Description header').toHaveText(await Strings.DESCRIPTION.name); await expect(this.userHeaderLabel, 'User header').toHaveText(await Strings.USER.name); }
  /**
   * Validate Change Log row data.
   * @param rowIndex Zero-based change-log row index.
   * @param expectedRowData Expected values in the change-log row.
   */
  async validateChangeLogRowData({ rowIndex, expectedRowData }: { rowIndex: number; expectedRowData: Record<string, string> }): Promise<void> { console.log(`Validate Change Log row data for row index=${rowIndex}`); const row = this.changeLogTableRows.nth(rowIndex); const cells = [row.locator('td:nth-child(1)'), row.locator('td:nth-child(2)'), row.locator('td:nth-child(3)'), row.locator('td:nth-child(4) div div'), row.locator('td:nth-child(5)')]; const expectedValues = [expectedRowData.date, expectedRowData.time, expectedRowData.actionType, expectedRowData.description, expectedRowData.user]; for (const [index, expectedValue] of expectedValues.entries()) await expect(cells[index], `Change Log cell ${index} for row ${rowIndex}`).toContainText(expectedValue); }
  /**
   * Validate Change Log description data.
   * @param rowIndex Zero-based change-log row index.
   * @param shouldBeContained Whether the description should contain the substring.
   * @param substring Description text to check.
   */
  async validateChangeLogDescriptionData({ rowIndex, shouldBeContained, substring }: { rowIndex: number; shouldBeContained: boolean; substring: string }): Promise<void> { console.log('Validate Change Log description data'); const descriptionCell = this.getDescriptionCellByRowIndex({ rowIndex }); if (shouldBeContained) await expect(descriptionCell, 'Change Log description should contain substring').toContainText(substring); else await expect(descriptionCell, 'Change Log description should not contain substring').not.toContainText(substring); }
}