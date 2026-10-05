import { handleError } from '../../../exception/error-handler';
import { objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';

/**
 * This method is used to fetch the payment cards for a company.
 *
 * @param companyId
 * @param context contains the headers and the client
 * @returns The response containing the payment cards list.
 */
export const retrievePaymentCards = async (
  { companyId }: { companyId: string },
  context: any
): Promise<any> => {
  try {
    const paymentCardsEndpoint = endpoints.GET_PAYMENT_CARDS.endpoint.replace(
      '{companyId}',
      companyId
    );
    const serviceEndpoint = {
      ...endpoints.GET_PAYMENT_CARDS,
      endpoint: paymentCardsEndpoint
    };

    return await get(serviceEndpoint, retrievePaymentCards, {}, context);
  } catch (error: Error | any) {
    handleError(error, {});
  }
};
