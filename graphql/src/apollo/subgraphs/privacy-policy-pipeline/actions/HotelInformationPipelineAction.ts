import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { fetchHotelInformation } from '../../content-entity-service/services/hotel-information-service';
import { ActionContextKeys } from './ActionContextKeys';

export class HotelInformationPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let hotelInformation = await fetchHotelInformation(args, context);
    if (!hotelInformation) throw new Error('Hotel information response is empty');
    pipelineContext.set(ActionContextKeys.HOTEL_INFORMATION, hotelInformation);
  }
}
