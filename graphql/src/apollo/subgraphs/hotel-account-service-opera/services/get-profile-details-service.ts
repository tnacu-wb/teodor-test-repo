import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';

/**
 * This method is used to fetch profile details.
 *
 * @param customerId
 * @param business
 * @param context contains the headers and the client
 * @returns The response containing the profile details.
 */
export const getProfileDetails = async (
  {
    customerId,
    business,
    innBusiness
  }: { customerId: string; business: boolean; innBusiness: boolean },
  context: any
): Promise<any> => {
  try {
    const companyDetailsEndpoint = endpoints.GET_PROFILE_DETAILS.endpoint.replace(
      '{customerId}',
      customerId
    );
    const serviceEndpoint = {
      ...endpoints.GET_PROFILE_DETAILS,
      endpoint: companyDetailsEndpoint
    };

    return await get(serviceEndpoint, getProfileDetails, { business, innBusiness }, context);
  } catch (error: Error | any) {
    handleError(error, { customerId: customerId });
  }
};
