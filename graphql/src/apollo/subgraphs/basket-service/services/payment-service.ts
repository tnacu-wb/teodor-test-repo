import { endpoints } from './base-service';
import { getServiceEndpoint } from '../../../utils/base-utils';
import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

export const initiatePayment = async (args: any, context: any) => {
  try {
    const initiatePaymentPath = endpoints.INITIATE_PAYMENT.endpoint.replace(
      '{basketReference}',
      args.basketReference
    );

    const initiatePaymentEndPoint = getServiceEndpoint(
      initiatePaymentPath,
      endpoints.INITIATE_PAYMENT
    );

    return await post(
      initiatePaymentEndPoint,
      initiatePayment,
      args.createPaymentCriteria,
      context
    );
  } catch (error: Error | any) {
    handleError(error, args);
  }
};

export const initiatePaypalPayment = async (args: any, context: any) => {
  try {
    const initiatePaypalPaymentPath = endpoints.INITIATE_PAYPAL_PAYMENT.endpoint.replace(
      '{basketReference}',
      args.basketReference
    );

    const initiatePaypalPaymentEndPoint = getServiceEndpoint(
      initiatePaypalPaymentPath,
      endpoints.INITIATE_PAYPAL_PAYMENT
    );

    return await post(
      initiatePaypalPaymentEndPoint,
      initiatePaypalPayment,
      args.createPaymentCriteria,
      context
    );
  } catch (error: Error | any) {
    handleError(error, args);
  }
};
