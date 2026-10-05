import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { fetchRateInformation } from '../../content-entity-service/services/rates-information-service';

export class RateInformationPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let rateInformation = await fetchRateInformation(args, context, pipelineContext);
    if (!rateInformation) throw new Error('Rate information response is empty');
    pipelineContext.set(ActionContextKeys.RATE_INFORMATION, rateInformation);
  }
}
