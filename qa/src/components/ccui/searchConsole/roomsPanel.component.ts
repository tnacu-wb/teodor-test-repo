import { RoomPanelCcuiComponent } from './roomPanel.component';
import { CcuiComponent } from '../baseCcui.component';

/** Rooms panel from the CCUI search console. */
export class RoomsPanelCcuiComponent extends CcuiComponent {
  // ######## UI elements/properties ########

  /**
   * Get the room panel for a zero-based index.
   *
   * The current CCUI implementation exposes one reusable room panel, so the
   * index is retained for compatibility and does not change the returned component.
   * @param _index Zero-based room-panel index retained for the shared component API.
   * @returns The reusable room-panel component.
   */
  getRoomPanelByIndex(index: number): RoomPanelCcuiComponent { return new RoomPanelCcuiComponent(this.page.locator('div[role="menuitem"]').filter({ has: this.page.locator('h2') }).nth(index)); }

  // ######## UI actions/navigation ########

  // ######## UI validations ########
}