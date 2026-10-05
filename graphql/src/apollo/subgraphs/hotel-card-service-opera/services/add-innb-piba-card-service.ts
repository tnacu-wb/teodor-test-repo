import { handleError } from '../../../exception/error-handler';
import {
  addFieldIfNotUndefinedAndRequired,
  getURL,
  objectIsNullEmptyOrUndefined
} from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { post } from '../../../client/rest-client';

/**
 * This method is used to add an InnBusiness PIBA card.
 *
 * @param tetheredUserGuid
 * @param countryCode
 * @param addInnBPIBACardCriteria
 * @param context
 * @returns the response containing the card details.
 */
export const addInnBPIBACard = async (
  {
    tetheredUserGuid,
    countryCode,
    addInnBPIBACardCriteria
  }: { tetheredUserGuid: string; countryCode: string; addInnBPIBACardCriteria: any },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const addPibaCardEndpoint = endpoints.ADD_INNB_PIBA_CARD.endpoint.replace(
      '{tetheredUserGuid}',
      tetheredUserGuid
    );
    addFieldIfNotUndefinedAndRequired(countryCode, 'countryCode', finalMap);
    const serviceEndpoint = {
      ...endpoints.ADD_INNB_PIBA_CARD,
      endpoint: getURL(addPibaCardEndpoint, finalMap)
    };

    return await post(serviceEndpoint, addInnBPIBACard, addInnBPIBACardCriteria, context);
  } catch (error: Error | any) {
    handleError(error, { finalMap, addInnBPIBACardCriteria });
  }
};
