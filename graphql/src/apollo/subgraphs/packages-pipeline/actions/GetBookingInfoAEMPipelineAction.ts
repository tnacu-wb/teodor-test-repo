import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { getBookingInfoAem } from '../../content-entity-service/services/get-booking-info-aem-service';

export class GetBookingInfoAEMPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let getBookingInfoAemResponse = await getBookingInfoAem(args, context, pipelineContext);
    if (!getBookingInfoAemResponse) throw new Error('getBookingInfoAemResponse response is empty');
    pipelineContext.set(ActionContextKeys.GET_BOOKING_INFO_AEM, getBookingInfoAemResponse);
  }
}
