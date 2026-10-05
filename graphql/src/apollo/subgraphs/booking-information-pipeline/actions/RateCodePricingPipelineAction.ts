import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { fetchRateCodePricing } from '../../hotel-entity-service/services/rate-code-pricing-service';

export class RateCodePricingPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let rateCodePricing = await fetchRateCodePricing(args, context, pipelineContext);
    if (!rateCodePricing) throw new Error('Rate code pricing response is empty');
    pipelineContext.set(ActionContextKeys.RATE_CODE_PRICING, rateCodePricing);
  }
}
