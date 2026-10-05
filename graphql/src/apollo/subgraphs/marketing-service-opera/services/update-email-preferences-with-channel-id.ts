import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { put } from '../../../client/rest-client';

/**
 * This method is used to update the contact preferences.
 *
 * @param contactChannelId
 * @param request contains the preferences request body
 * @param context contains the headers and the client
 * @returns a string message.
 */
export const updateEmailPreferencesWithChannelId = async (
  { contactChannelId, request }: { contactChannelId: string; request: any },
  context: any
): Promise<any> => {
  try {
    const updateEmailPreferencesWithChannelIdEndpoint =
      endpoints.UPDATE_EMAIL_PREFERENCES_WITH_CHANNEL_ID.endpoint.replace(
        '{contactChannelId}',
        contactChannelId
      );
    const serviceEndpoint = {
      ...endpoints.UPDATE_EMAIL_PREFERENCES_WITH_CHANNEL_ID,
      endpoint: updateEmailPreferencesWithChannelIdEndpoint
    };

    const response = await put(
      serviceEndpoint,
      updateEmailPreferencesWithChannelId,
      request,
      context
    );
    return response.data;
  } catch (error: Error | any) {
    handleError(error, { request });
  }
};
