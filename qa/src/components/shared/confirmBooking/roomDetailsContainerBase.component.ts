import { type Locator, type Page } from '@playwright/test';

/** Shared room-details card on the confirmation page. */
export class RoomDetailsContainerBaseComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly container: Locator;

  constructor(roomIndex: number) {
    const roomNumber = roomIndex + 1;
    this.container = this.page.locator(`[data-testid="RoomCardHeader-room${roomNumber}"]`);
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########
}