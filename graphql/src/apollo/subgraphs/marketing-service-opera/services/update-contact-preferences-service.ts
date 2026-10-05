import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { put } from '../../../client/rest-client';

/**
 * This method is used to update the contact preferences.
 *
 * @param contactType
 * @param contactValue
 * @param request contains the preferences request body
 * @param context contains the headers and the client
 * @returns a string message.
 */
export const updateContactPreferences = async (
  {
    contactType,
    contactValue,
    request
  }: { contactType: string; contactValue: string; request: any },
  context: any
): Promise<any> => {
  try {
    const updateContactPreferencesEndpoint = endpoints.UPDATE_CONTACT_PREFERENCES.endpoint
      .replace('{contactType}', contactType)
      .replace('{contactValue}', contactValue);
    const serviceEndpoint = {
      ...endpoints.UPDATE_CONTACT_PREFERENCES,
      endpoint: updateContactPreferencesEndpoint
    };

    const response = await put(serviceEndpoint, updateContactPreferences, request, context);
    return response.data;
  } catch (error: Error | any) {
    handleError(error, { contactType: contactType, request });
  }
};
