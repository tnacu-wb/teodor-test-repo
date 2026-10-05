import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { CcuiComponent } from '../baseCcui.component';

/** Room panel from the CCUI search console. Mirrors the CCUI reference component. */
export class RoomPanelCcuiComponent extends CcuiComponent {
  private readonly container: Locator;
  constructor(container?: Locator) {
    super();
    this.container = container ?? this.page.locator('div[role="menuitem"]').filter({ has: this.page.locator('h2') }).first();
  }
  // ######## UI elements/properties ########

  get adultsList(): Locator { return this.container.locator('div button[data-testid*="dropdownContent-adults"]'); }
  get roomTypeOptions(): Locator { return this.page.locator('button[data-testid*="DropdownComp-roomPicker-dropdownContent-roomTypeDropdown"]'); }

  // ######## UI actions/navigation ########
  // ######## UI validations ########

  /**
   * Validate room type options for zero children.
   * @param adultsNumber Expected number of adults in the option list.
   */
  /** Validate room-type options for zero children. */
  async validateRoomTypeOptionsListFor0Children({ adultsNumber }: { adultsNumber: number }): Promise<void> { console.log(`Validate room type options list for ${adultsNumber} adults and 0 children`); const expected = adultsNumber === 1 ? [await Strings.SINGLE_GENERIC.name, await Strings.DOUBLE_GENERIC.name, await Strings.TWIN_GENERIC.name, await Strings.ACCESSIBLE_GENERIC.name, await Strings.FAMILY_GENERIC.name] : [await Strings.DOUBLE_GENERIC.name, await Strings.TWIN_GENERIC.name, await Strings.ACCESSIBLE_GENERIC.name, await Strings.FAMILY_GENERIC.name]; await expect(this.roomTypeOptions, 'Room type options for zero children').toHaveText(expected); }
  /**
   * Validate room type options for one child.
   * @param adultsNumber Expected number of adults in the option list.
   */
  /** Validate room-type options for one child. */
  async validateRoomTypeOptionsListFor1Children({ adultsNumber }: { adultsNumber: number }): Promise<void> { console.log(`Validate room type options list for ${adultsNumber} adults and 1 child`); const expected = adultsNumber === 1 ? [await Strings.DOUBLE_GENERIC.name, await Strings.TWIN_GENERIC.name, await Strings.ACCESSIBLE_GENERIC.name, await Strings.FAMILY_GENERIC.name] : [await Strings.ACCESSIBLE_GENERIC.name, await Strings.FAMILY_GENERIC.name]; await expect(this.roomTypeOptions, 'Room type options for one child').toHaveText(expected); }
  /**
   * Validate room type options for two children.
   * @param adultsNumber Expected number of adults in the option list.
   */
  /** Validate room-type options for two children. */
  async validateRoomTypeOptionsListFor2Children({ adultsNumber }: { adultsNumber: number }): Promise<void> { console.log(`Validate room type options list for ${adultsNumber} adults and 2 children`); const expected = adultsNumber === 1 ? [await Strings.ACCESSIBLE_GENERIC.name, await Strings.FAMILY_GENERIC.name] : [await Strings.FAMILY_GENERIC.name]; await expect(this.roomTypeOptions, 'Room type options for two children').toHaveText(expected); }
}