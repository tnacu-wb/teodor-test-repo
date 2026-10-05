import { handleError } from '../../../exception/error-handler';
import {
  addFieldIfNotUndefinedAndRequired,
  getURL,
  objectIsNullEmptyOrUndefined
} from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';

/**
 * This method is used to retrieve PIBA card details for an account.
 *
 * @param tetheredUserId
 * @param cardId
 * @param countryCode
 * @param context
 * @returns the response containing the PIBA card details.
 */
export const retrievePIBACardDetails = async (
  {
    tetheredUserId,
    cardId,
    countryCode
  }: { tetheredUserId: string; cardId: string; countryCode: string },
  context: any
): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};

    const pibaCardDetailsEndpoint = endpoints.GET_PIBA_CARD_DETAILS.endpoint
      .replace('{tetheredUserId}', tetheredUserId)
      .replace('{cardId}', cardId);
    addFieldIfNotUndefinedAndRequired(countryCode, 'countryCode', finalMap);
    const serviceEndpoint = {
      ...endpoints.GET_PIBA_CARD_DETAILS,
      endpoint: getURL(pibaCardDetailsEndpoint, finalMap)
    };

    return await get(serviceEndpoint, retrievePIBACardDetails, {}, context);
  } catch (error: Error | any) {
    handleError(error, { countryCode: countryCode });
  }
};
