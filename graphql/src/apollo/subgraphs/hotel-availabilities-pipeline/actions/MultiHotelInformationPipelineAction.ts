import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { getMultiHotelInformation } from '../../content-entity-service/services/hotel-information-service';

export class MultiHotelInformationPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let hotelAvailabilities = pipelineContext.get(
      ActionContextKeys.MULTI_HOTEL_AVAILABILITIES
    ).hotelAvailabilities;
    if (hotelAvailabilities && hotelAvailabilities.length > 0) {
      let multiHotelInformation = await getMultiHotelInformation(
        args,
        hotelAvailabilities,
        context
      );
      if (multiHotelInformation.length == 0) throw new Error('Multi hotel information is empty');
      pipelineContext.set(ActionContextKeys.MULTI_HOTEL_INFORMATION, multiHotelInformation);
    }
  }
}
