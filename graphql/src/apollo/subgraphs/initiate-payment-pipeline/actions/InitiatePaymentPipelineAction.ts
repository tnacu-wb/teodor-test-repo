import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { initiatePayment } from '../../basket-service/services/payment-service';
import { ActionContextKeys } from './ActionContextKeys';

export class InitiatePaymentPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let initiatePaymentResponse = await initiatePayment(args, context);
    if (!initiatePaymentResponse) throw new Error('Initiate payment response is empty');
    pipelineContext.set(ActionContextKeys.INITIATE_PAYMENT, initiatePaymentResponse);
  }
}
