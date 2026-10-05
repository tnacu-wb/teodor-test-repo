import { getServiceEndpoint, objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { post, put } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

export const initiateCcuiPayment = async (
  {
    basketReference,
    initiateCcuiPaymentCriteria
  }: { basketReference: string; initiateCcuiPaymentCriteria?: any },
  context: any
) => {
  try {
    const initiateCcuiPaymentPath = endpoints.INITIATE_CCUI_PAYMENT.endpoint.replace(
      '{basketReference}',
      basketReference.toString()
    );

    const initiateCcuiPaymentEndPoint = getServiceEndpoint(
      initiateCcuiPaymentPath,
      endpoints.INITIATE_CCUI_PAYMENT
    );

    return await post(
      initiateCcuiPaymentEndPoint,
      initiateCcuiPayment,
      initiateCcuiPaymentCriteria,
      context
    );
  } catch (error: Error | any) {
    handleError(error, { initiateCcuiPaymentCriteria, basketReferences: basketReference });
  }
};

export const updateDiscount = async (
  { updateDiscountRequest }: { updateDiscountRequest?: any },
  context: any
) => {
  try {
    const response = await put(
      endpoints.UPDATE_DISCOUNT,
      updateDiscount,
      updateDiscountRequest,
      context
    );
    return response.data;
  } catch (error: Error | any) {
    handleError(error, updateDiscountRequest);
  }
};
