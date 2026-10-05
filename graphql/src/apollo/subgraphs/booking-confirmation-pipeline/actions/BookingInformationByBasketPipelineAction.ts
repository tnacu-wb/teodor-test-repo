import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { getPmsBookingInformation } from '../../hotel-reservation-entity-service/services/hotel-reservation-service';

export class BookingInformationByBasketPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let bookingInformationByBasket = await getPmsBookingInformation(args, context);
    if (!bookingInformationByBasket)
      throw new Error('Booking information by basket response is empty');
    pipelineContext.set(
      ActionContextKeys.BOOKING_INFORMATION_BY_BASKET,
      bookingInformationByBasket
    );
  }
}
