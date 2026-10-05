import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { fetchBookingRateInformation } from '../../content-entity-service/services/booking-information-service';
import { ActionContextKeys } from './ActionContextKeys';

export class BookingRateInformationPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let rateInformation = await fetchBookingRateInformation(args, context, pipelineContext);
    if (!rateInformation) throw new Error('Rate information response is empty');
    pipelineContext.set(ActionContextKeys.RATE_INFORMATION, rateInformation);
  }
}
