import { PipelineAction } from '../../../pipeline/PipelineAction';
import { ActionContextKeys } from './ActionContextKeys';
import { fetchBookingInfoMessages } from '../../content-entity-service/services/booking-information-service';

export class BookingInfoMessagesPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: any): Promise<void> {
    let bookingInfoMessages = await fetchBookingInfoMessages(args, context, pipelineContext);
    if (!bookingInfoMessages) throw new Error('Booking info messages response is empty');
    pipelineContext.set(ActionContextKeys.BOOKING_INFO_MESSAGES, bookingInfoMessages);
  }
}
