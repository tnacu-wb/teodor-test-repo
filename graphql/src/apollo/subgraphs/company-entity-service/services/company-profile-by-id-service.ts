import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

export const companyProfileById = async ({ id }: { id: string }, context: any): Promise<any> => {
  try {
    const companyProfileEndpoint = endpoints.COMPANY_PROFILE_BY_ID.endpoint.replace('{id}', id);

    const serviceEndpoint = {
      ...endpoints.COMPANY_PROFILE_BY_ID,
      endpoint: companyProfileEndpoint
    };

    const response = await get(serviceEndpoint, companyProfileById, {}, context);

    return response && Object.keys(response).length ? response : {};
  } catch (error) {
    handleError(error, id);
  }
};
