import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { getRatesInformationV2 } from '../../content-entity-service/services/rates-information-service';

export class RateInformationPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let rateInfo = await getRatesInformationV2(args, context);
    if (!rateInfo) throw new Error('Rate Information response is empty');
    pipelineContext.set(ActionContextKeys.RATE_INFORMATION, rateInfo);
  }
}
