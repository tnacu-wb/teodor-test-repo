import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { getBookingInformationAuthenticated } from '../../hotel-reservation-entity-service/services/hotel-reservation-service';

export class BookingInformationByBasketAuthenticatedPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    const bookingInformationByBasketAuthenticated = await getBookingInformationAuthenticated(
      args,
      context
    );
    if (!bookingInformationByBasketAuthenticated)
      throw new Error('Booking information by basket authenticated response is empty');
    pipelineContext.set(
      ActionContextKeys.BOOKING_INFORMATION_BY_BASKET,
      bookingInformationByBasketAuthenticated
    );
  }
}
