import { handleError } from '../../../exception/error-handler';
import { objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { put } from '../../../client/rest-client';

/**
 * This method is used to update a payment card for a company.
 *
 * @param companyId
 * @param cardId
 * @param updatePaymentCardRequest
 * @param context contains the headers and the client
 * @returns the response containing a message representing the status of the request.
 */
export const updatePaymentCard = async (
  {
    companyId,
    cardId,
    updatePaymentCardRequest
  }: { companyId: string; cardId: string; updatePaymentCardRequest: any },
  context: any
): Promise<any> => {
  try {
    const updatePaymentCardEndpoint = endpoints.UPDATE_PAYMENT_CARD.endpoint
      .replace('{companyId}', companyId)
      .replace('{cardId}', cardId);
    const serviceEndpoint = {
      ...endpoints.UPDATE_PAYMENT_CARD,
      endpoint: updatePaymentCardEndpoint
    };

    const response = await put(
      serviceEndpoint,
      updatePaymentCard,
      updatePaymentCardRequest,
      context
    );
    if (response.status === 204) {
      return objectIsNullEmptyOrUndefined(response.data) ? '{success=true}' : response.data;
    } else {
      throw new Error(JSON.stringify({ status: response.status, data: response.data }));
    }
  } catch (error: Error | any) {
    handleError(error, updatePaymentCardRequest);
  }
};
