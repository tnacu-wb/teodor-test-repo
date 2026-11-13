import { RoomDetailsSectionBaseComponent } from '../../../components/shared/confirmBooking/roomDetailsSectionBase.component';
import { RoomDetailsContainerComponent } from './roomDetailsContainer.component';

/** Business Booker room-details section on the confirmation page. */
export class RoomDetailsSectionComponent extends RoomDetailsSectionBaseComponent {
  // ######## UI elements/properties ########

  /** Get a BB room-details card by zero-based room index. */
  override getRoomDetailsContainerByIndex(index: number): RoomDetailsContainerComponent {
    return new RoomDetailsContainerComponent(index);
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########
}