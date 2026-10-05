import { type Locator, type Page } from '@playwright/test';
import { RoomDetailsContainerBaseComponent } from './roomDetailsContainerBase.component';

/** Shared room-details section on the confirmation page. */
export class RoomDetailsSectionBaseComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly roomDetailsSectionContainer: Locator = this.page.locator('[data-testid="RoomDetailsSection"]');
  readonly roomList: Locator = this.roomDetailsSectionContainer.locator('[data-testid*="RoomCardHeader-room"]');

  /** Get a room-details card by zero-based room index. */
  getRoomDetailsContainerByIndex(index: number): RoomDetailsContainerBaseComponent {
    return new RoomDetailsContainerBaseComponent(index);
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########
}