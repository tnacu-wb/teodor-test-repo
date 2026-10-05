import { CreatePaymentCriteria } from '../../hotel-reservation-entity-service/models/create-payment-criteria';
import { PipelineManager } from '../../../pipeline/manager/PipelineManager';
import { handleError } from '../../../exception/error-handler';
import { ActionContextKeys } from '../actions/ActionContextKeys';
import { ValidatePaymentMethodPipelineAction } from '../actions/ValidatePaymentMethodPipelineAction';
import { SaveCharityPackagePipelineAction } from '../actions/SaveCharityPackagePipelineAction';
import { InitiatePaymentPipelineAction } from '../actions/InitiatePaymentPipelineAction';
import { InitiatePaypalPaymentPipelineAction } from '../actions/InitiatePaypalPaymentPipelineAction';
import { logPipelineError } from '../../../utils/base-utils';

export const getInitiatePayment = async (
  {
    basketReference,
    createPaymentCriteria
  }: { basketReference: string; createPaymentCriteria: CreatePaymentCriteria },
  context: any
): Promise<any> => {
  const pipelineManager = new PipelineManager();

  // Define the pipeline actions in order
  const actions = [
    new ValidatePaymentMethodPipelineAction(),
    new SaveCharityPackagePipelineAction(),
    new InitiatePaymentPipelineAction()
  ];

  try {
    let pipelineContext = await pipelineManager.manage(
      actions,
      { basketReference, createPaymentCriteria },
      context
    );

    let initPayment = pipelineContext.get(ActionContextKeys.INITIATE_PAYMENT);
    return initPayment;
  } catch (error) {
    logPipelineError(error, { basketReference, createPaymentCriteria }, getInitiatePayment);
    throw error;
  }
};

export const getInitiatePaypalPayment = async (
  {
    basketReference,
    createPaymentCriteria
  }: { basketReference: string; createPaymentCriteria: CreatePaymentCriteria },
  context: any
): Promise<any> => {
  const pipelineManager = new PipelineManager();

  // Define the pipeline actions in order
  const actions = [
    new ValidatePaymentMethodPipelineAction(),
    new SaveCharityPackagePipelineAction(),
    new InitiatePaypalPaymentPipelineAction()
  ];

  try {
    let pipelineContext = await pipelineManager.manage(
      actions,
      { basketReference, createPaymentCriteria },
      context
    );

    let initPaypalPayment = pipelineContext.get(ActionContextKeys.INITIATE_PAYMENT);
    return initPaypalPayment;
  } catch (error) {
    logPipelineError(error, { basketReference, createPaymentCriteria }, getInitiatePaypalPayment);
    throw error;
  }
};
