import { handleError } from '../../../exception/error-handler';
import {
  addFieldIfNotUndefinedAndRequired,
  getURL,
  objectIsNullEmptyOrUndefined
} from '../../../utils/base-utils';
import { post } from '../../../client/rest-client';
import { endpoints } from './base-service';

/**
 * This method is used to send a post request to activate an InnBusiness PIBA card.
 *
 * @param tetheredUserId
 * @param cardId
 * @param countryCode
 * @param context contains the headers and the client
 *
 * @returns a string containing the response of the activation.
 */
export const activateInnBPIBACard = async (
  {
    tetheredUserId,
    cardId,
    countryCode
  }: { tetheredUserId: string; cardId: string; countryCode: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const activateCardEndpoint = endpoints.ACTIVATE_INNB_PIBA_CARD.endpoint
      .replace('{tetheredUserId}', tetheredUserId)
      .replace('{cardId}', cardId);
    addFieldIfNotUndefinedAndRequired(countryCode, 'countryCode', finalMap);
    const serviceEndpoint = {
      ...endpoints.ACTIVATE_INNB_PIBA_CARD,
      endpoint: getURL(activateCardEndpoint, finalMap)
    };

    return await post(serviceEndpoint, activateInnBPIBACard, {}, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
