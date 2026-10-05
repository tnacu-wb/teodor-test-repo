import { PIBACardsCriteria } from '../models/piba-cards-criteria';
import { handleError } from '../../../exception/error-handler';
import {
  addFieldIfNotUndefinedAndRequired,
  getURL,
  objectIsNullEmptyOrUndefined
} from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { post } from '../../../client/rest-client';

/**
 * This method is used to retrieve all PIBA cards for an account.
 *
 * @param tetheredUserId
 * @param countryCode
 * @param pibaCardsCriteria
 * @param context contains the headers and the client
 * @returns the response containing all account PIBA cards.
 */
export const retrieveAllPIBACards = async (
  {
    tetheredUserId,
    countryCode,
    pibaCardsCriteria
  }: { tetheredUserId: string; countryCode: string; pibaCardsCriteria: PIBACardsCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const allPibaCardsEndpoint = endpoints.GET_ALL_PIBA_CARDS.endpoint.replace(
      '{tetheredUserId}',
      tetheredUserId
    );
    addFieldIfNotUndefinedAndRequired(countryCode, 'countryCode', finalMap);
    const serviceEndpoint = {
      ...endpoints.GET_ALL_PIBA_CARDS,
      endpoint: getURL(allPibaCardsEndpoint, finalMap)
    };

    return await post(serviceEndpoint, retrieveAllPIBACards, pibaCardsCriteria, context);
  } catch (error: Error | any) {
    handleError(error, [pibaCardsCriteria, { countryCode: countryCode }]);
  }
};
