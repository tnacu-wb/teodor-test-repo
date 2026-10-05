import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { put } from '../../../client/rest-client';
import { addFieldIfNotUndefinedAndRequired, getURL } from '../../../utils/base-utils';

export const updateProfileDetails = async (
  {
    customerId,
    business,
    innBusiness,
    payload
  }: { customerId: string; business?: boolean; innBusiness?: boolean; payload: any },
  context: any
): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};

    const companyDetailsEndpoint = endpoints.UPDATE_PROFILE_DETAILS.endpoint.replace(
      '{customerId}',
      customerId
    );
    addFieldIfNotUndefinedAndRequired(business, 'business', finalMap);
    addFieldIfNotUndefinedAndRequired(innBusiness, 'innBusiness', finalMap);

    const serviceEndpoint = {
      ...endpoints.UPDATE_PROFILE_DETAILS,
      endpoint: getURL(companyDetailsEndpoint, finalMap)
    };
    const response = await put(serviceEndpoint, updateProfileDetails, payload, context);

    return response.data;
  } catch (error: Error | any) {
    handleError(error, { customerId: customerId });
  }
};
