import { handleError } from '../../../exception/error-handler';
import { put } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { getURL, addFieldIfNotUndefinedAndRequired } from '../../../utils/base-utils';

/**
 * This method is used to send a put request to replace a damaged or lost WorldLine card.
 *
 * @param tetheredUserGuid
 * @param cardId
 * @param scheme
 * @param replaceCardRequest
 * @param context contains the headers and the client
 *
 * @returns a string containing the response of the replacement.
 */
export const replaceCard = async (
  {
    tetheredUserGuid,
    cardId,
    scheme,
    replaceCardRequest
  }: {
    tetheredUserGuid: string;
    cardId: string;
    scheme: string;
    replaceCardRequest: any;
  },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const replaceCardEndpoint = endpoints.REPLACE_CARD.endpoint
      .replace('{tetheredUserGuid}', tetheredUserGuid)
      .replace('{cardId}', cardId);
    addFieldIfNotUndefinedAndRequired(scheme, 'scheme', finalMap);

    const serviceEndpoint = {
      ...endpoints.REPLACE_CARD,
      endpoint: getURL(replaceCardEndpoint, finalMap)
    };

    const response = await put(serviceEndpoint, replaceCard, replaceCardRequest, context);
    return response.data;
  } catch (error) {
    handleError(error, replaceCardRequest);
  }
};
