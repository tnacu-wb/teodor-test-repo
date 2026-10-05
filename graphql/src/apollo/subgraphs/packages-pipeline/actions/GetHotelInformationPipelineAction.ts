import { PipelineAction } from '../../../pipeline/PipelineAction';
import { getHotelInformation } from '../../content-entity-service/services/hotel-information-service';
import { ActionContextKeys } from './ActionContextKeys';

export class GetHotelInformationPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: any): Promise<void> {
    let getHotelInformationResponse = await getHotelInformation(args, context);
    if (!getHotelInformationResponse) throw new Error('getHotelInformation response is empty');
    pipelineContext.set(ActionContextKeys.GET_HOTEL_INFORMATION, getHotelInformationResponse);
  }
}
