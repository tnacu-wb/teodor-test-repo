import { handleError } from '../../../exception/error-handler';
import {
  addFieldIfNotUndefinedAndRequired,
  getURL,
  objectIsNullEmptyOrUndefined
} from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { put } from '../../../client/rest-client';

/**
 * This method is used to update an InnBusiness PIBA card.
 *
 * @param tetheredUserGuid
 * @param cardId
 * @param countryCode
 * @param updateInnBPIBACardRequest
 * @param context contains the headers and the client
 * @returns the response containing details about updated card.
 */
export const updateInnBPIBACard = async (
  {
    tetheredUserGuid,
    cardId,
    countryCode,
    updateInnBPIBACardRequest
  }: {
    tetheredUserGuid: string;
    cardId: string;
    countryCode: string;
    updateInnBPIBACardRequest: any;
  },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const updateInnBPIBACardEndpoint = endpoints.UPDATE_INNB_PIBA_CARD.endpoint
      .replace('{tetheredUserGuid}', tetheredUserGuid)
      .replace('{cardId}', cardId);
    addFieldIfNotUndefinedAndRequired(countryCode, 'countryCode', finalMap);
    const serviceEndpoint = {
      ...endpoints.UPDATE_INNB_PIBA_CARD,
      endpoint: getURL(updateInnBPIBACardEndpoint, finalMap)
    };

    const response = await put(
      serviceEndpoint,
      updateInnBPIBACard,
      updateInnBPIBACardRequest,
      context
    );
    return response.data;
  } catch (error: Error | any) {
    handleError(error, { finalMap, updateInnBPIBACardRequest });
  }
};
