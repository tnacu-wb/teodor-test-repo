import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { getMultiHotelAvailabilities } from '../../hotel-entity-service/services/hotel-availability-service';

export class MultiHotelAvailabilitiesPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let multiHotelAvailabilities = await getMultiHotelAvailabilities(args, context);
    if (!multiHotelAvailabilities) throw new Error('Multi hotel availabilities are empty');
    pipelineContext.set(ActionContextKeys.MULTI_HOTEL_AVAILABILITIES, multiHotelAvailabilities);
  }
}
