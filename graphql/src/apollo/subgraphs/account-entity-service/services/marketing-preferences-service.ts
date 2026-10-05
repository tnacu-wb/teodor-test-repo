import { put } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';
import { UpdateMarketingPreferencesRequest } from '../models/update-marketing-preferences-request';

export const updateMarketingPreferences = async (
  {
    updateMarketingPreferencesRequest
  }: { updateMarketingPreferencesRequest: UpdateMarketingPreferencesRequest },
  context: any
): Promise<any> => {
  try {
    let response = await put(
      endpoints.UPDATE_MARKETING_PREFERENCES,
      updateMarketingPreferences,
      updateMarketingPreferencesRequest,
      context
    );

    if (response?.status == 204) {
      return response.data;
    }
    return response;
  } catch (error: Error | any) {
    handleError(error, updateMarketingPreferencesRequest);
  }
};
