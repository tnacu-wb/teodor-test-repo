import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { fetchHotelInformationForBooking } from '../../content-entity-service/services/hotel-information-service';

export class HotelInformationForBookingPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let hotelInformationForBooking = await fetchHotelInformationForBooking(
      args,
      context,
      pipelineContext
    );
    if (!hotelInformationForBooking)
      throw new Error('Hotel information for booking response is empty');
    pipelineContext.set(
      ActionContextKeys.HOTEL_INFORMATION_FOR_BOOKING,
      hotelInformationForBooking
    );
  }
}
