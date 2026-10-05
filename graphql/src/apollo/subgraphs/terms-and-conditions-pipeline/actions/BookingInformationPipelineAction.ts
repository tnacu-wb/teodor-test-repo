import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { fetchBookingInformation } from '../../content-entity-service/services/booking-information-service';
import { ActionContextKeys } from './ActionContextKeys';

export class BookingInformationPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let bookingInformation = await fetchBookingInformation(args, context, pipelineContext);
    if (!bookingInformation) throw new Error('Booking information response is empty');
    pipelineContext.set(ActionContextKeys.BOOKING_INFORMATION, bookingInformation);
  }
}
