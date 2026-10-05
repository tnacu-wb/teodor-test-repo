import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { getBookingInformationAuthenticatedWithToken } from '../../hotel-reservation-entity-service/services/hotel-reservation-service';

export class BookingInformationByBasketAuthenticatedWithTokenPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    const bookingInformationByBasketAuthenticatedWithToken =
      await getBookingInformationAuthenticatedWithToken(args, context);
    if (!bookingInformationByBasketAuthenticatedWithToken)
      throw new Error('Booking information by basket authenticated with token response is empty');
    pipelineContext.set(
      ActionContextKeys.BOOKING_INFORMATION_BY_BASKET,
      bookingInformationByBasketAuthenticatedWithToken
    );
  }
}
