import { PreferencesGetRequest } from '../models/preferences-get-request';
import { addFieldsToMap } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';
import { get } from '../../../client/rest-client';

/**
 * This method is used to fetch the contact preferences.
 *
 * @param preferencesGetRequest
 * @param context contains the headers and the client
 * @returns The response containing the contact preferences.
 */
export const retrieveContactPreferences = async (
  { request }: { request: PreferencesGetRequest },
  context: any
): Promise<any> => {
  try {
    const getContactPreferencesEndpoint = endpoints.GET_CONTACT_PREFERENCES.endpoint
      .replace('{contactType}', request.contactType)
      .replace('{contactValue}', request.contactValue);
    const serviceEndpoint = {
      ...endpoints.GET_CONTACT_PREFERENCES,
      endpoint: getContactPreferencesEndpoint
    };

    let finalMap: { [key: string]: any } = {};

    const fieldToAdd = [
      { key: 'contactChannelId', value: request.contactChannelId, required: false },
      { key: 'brandCodes', value: request.brandCodes, required: true },
      { key: 'business', value: request.business, required: false },
      { key: 'countryOfResidence', value: request.countryOfResidence ?? 'gb', required: false },
      { key: 'language', value: request.language ?? 'en', required: false }
    ];

    addFieldsToMap(fieldToAdd, finalMap);

    return await get(serviceEndpoint, retrieveContactPreferences, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, request);
  }
};
