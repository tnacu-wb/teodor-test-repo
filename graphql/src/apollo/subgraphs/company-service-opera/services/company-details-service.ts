import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { get, put } from '../../../client/rest-client';

/**
 * This method is used to fetch company details.
 *
 * @param companyId
 * @param context contains the headers and the client
 * @returns The response containing the company details.
 */
export const getCompanyDetails = async (
  { companyId }: { companyId: string },
  context: any
): Promise<any> => {
  try {
    const companyDetailsEndpoint = endpoints.COMPANY_DETAILS.endpoint.replace(
      '{companyId}',
      companyId
    );
    const serviceEndpoint = {
      ...endpoints.COMPANY_DETAILS,
      endpoint: companyDetailsEndpoint
    };

    return await get(serviceEndpoint, getCompanyDetails, {}, context);
  } catch (error: Error | any) {
    handleError(error, { companyId: companyId });
  }
};

/**
 * This method is used to update company details.
 *
 * @param companyId
 * @param companySummary
 * @param context contains the headers and the client
 * @returns a string.
 */
export const updateCompanyDetails = async (
  { companyId, companySummary }: { companyId: string; companySummary: any },
  context: any
): Promise<any> => {
  try {
    const updateCompanyDetailsEndpoint = endpoints.UPDATE_COMPANY_DETAILS.endpoint.replace(
      '{companyId}',
      companyId
    );
    const serviceEndpoint = {
      ...endpoints.UPDATE_COMPANY_DETAILS,
      endpoint: updateCompanyDetailsEndpoint
    };

    const response = await put(serviceEndpoint, updateCompanyDetails, companySummary, context);
    return response.data;
  } catch (error: Error | any) {
    handleError(error, { companyId: companyId, companySummary });
  }
};
