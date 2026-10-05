import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { PipelineAction } from '../../../pipeline/PipelineAction';
import { validatePaymentMethod } from '../../payment-methods-entity-service/services/validate-payment-service';
import { ActionContextKeys } from './ActionContextKeys';

export class ValidatePaymentMethodPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let paymentMethodResponse = await validatePaymentMethod(args, context);
    if (paymentMethodResponse != '') throw new Error('Payment method response failed');
    pipelineContext.set(ActionContextKeys.VALIDATE_PAYMENT_METHOD, paymentMethodResponse);
  }
}
