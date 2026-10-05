import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

export const companyProfile = async (
  { companyId }: { companyId: string },
  context: any
): Promise<any> => {
  try {
    const companyProfileEndpoint = endpoints.COMPANY_PROFILE.endpoint.replace(
      '{companyId}',
      companyId
    );

    const serviceEndpoint = { ...endpoints.COMPANY_PROFILE, endpoint: companyProfileEndpoint };

    return await get(serviceEndpoint, companyProfile, {}, context);
  } catch (error) {
    handleError(error, companyId);
  }
};
