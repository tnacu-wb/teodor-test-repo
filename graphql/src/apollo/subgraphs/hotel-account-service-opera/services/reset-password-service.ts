import { endpoints } from './base-service';
import { put } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { addFieldIfNotUndefinedAndRequired, getURL } from '../../../utils/base-utils';

export const resetPassword = async ({ payload }: { payload: any }, context: any): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};
    const business = true;
    addFieldIfNotUndefinedAndRequired(business, 'business', finalMap);

    const serviceEndpoint = {
      ...endpoints.RESET_PASSWORD,
      endpoint: getURL(endpoints.RESET_PASSWORD.endpoint, finalMap)
    };

    const response = await put(serviceEndpoint, resetPassword, payload, context);

    return response.data;
  } catch (error) {
    handleError(error, payload);
  }
};
