import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';
import { post } from '../../../client/rest-client';

export const deleteCustomQuestion = async (
  { companyId, questionId }: { companyId: string; questionId: string },
  context: any
): Promise<any> => {
  try {
    let endpoint = endpoints.DELETE_CUSTOM_QUESTION.endpoint;
    endpoint = endpoint.replace('{companyId}', companyId);
    endpoint = endpoint.replace('{questionId}', questionId);

    const serviceEndpoint = {
      ...endpoints.DELETE_CUSTOM_QUESTION,
      endpoint: endpoint
    };

    return await post(serviceEndpoint, deleteCustomQuestion, {}, context);
  } catch (error: Error | any) {
    handleError(error, { companyId, questionId });
  }
};
