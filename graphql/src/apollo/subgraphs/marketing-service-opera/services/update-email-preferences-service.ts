import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { put } from '../../../client/rest-client';

/**
 * This method is used to update the email preferences.
 *
 * @param request contains the preferences request body
 * @param context contains the headers and the client
 * @returns a string message.
 */
export const updateEmailPreferences = async (
  { request }: { request: any },
  context: any
): Promise<any> => {
  try {
    const serviceEndpoint = endpoints.UPDATE_EMAIL_PREFERENCES;
    const response = await put(serviceEndpoint, updateEmailPreferences, request, context);
    return response.data;
  } catch (error: Error | any) {
    handleError(error, { request });
  }
};
