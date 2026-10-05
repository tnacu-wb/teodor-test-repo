import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { getMultiHotelAvailabilitiesV2 } from '../../hotel-entity-service/services/hotel-availability-service';

export class MultiHotelAvailabilitiesV2PipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let multiHotelAvailabilities = await getMultiHotelAvailabilitiesV2(args, context);
    if (!multiHotelAvailabilities) throw new Error('Multi hotel availabilities are empty');
    pipelineContext.set(ActionContextKeys.MULTI_HOTEL_AVAILABILITIES, multiHotelAvailabilities);
  }
}
