import { handleError } from '../../../exception/error-handler';
import { objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { post } from '../../../client/rest-client';

/**
 * This method is used to delete a company card.
 *
 * @param companyId
 * @param cardId
 * @param context contains the headers and the client
 * @returns a string containing the response of the deletion.
 */
export const deleteCompanyCard = async (
  { companyId, cardId }: { companyId: string; cardId: string },
  context: any
): Promise<any> => {
  try {
    const deleteCompanyCardEndpoint = endpoints.DELETE_COMPANY_CARD.endpoint
      .replace('{companyId}', companyId)
      .replace('{cardId}', cardId);
    const serviceEndpoint = {
      ...endpoints.DELETE_COMPANY_CARD,
      endpoint: deleteCompanyCardEndpoint
    };

    return await post(serviceEndpoint, deleteCompanyCard, {}, context);
  } catch (error: Error | any) {
    handleError(error, {});
  }
};
