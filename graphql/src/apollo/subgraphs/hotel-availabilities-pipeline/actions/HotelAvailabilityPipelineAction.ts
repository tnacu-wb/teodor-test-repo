import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { getHotelAvailabilities } from '../../hotel-entity-service/services/hotel-availability-service';
export class HotelAvailabilityPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    const criteria = { availabilitySearchCriteria: args.availabilityCriteria };
    let availability = await getHotelAvailabilities(criteria, context);
    if (!availability) throw new Error('Hotel availability response is empty');
    pipelineContext.set(ActionContextKeys.HOTEL_AVAILABILITY, availability);
  }
}
