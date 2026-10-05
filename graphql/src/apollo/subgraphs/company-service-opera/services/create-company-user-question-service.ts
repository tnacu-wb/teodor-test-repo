import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';
import { post } from '../../../client/rest-client';
import { objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { ManagementInformationQuestionCriteria } from '../models/management-information-question-criteria';

export const createCompanyUserQuestion = async (
  {
    companyId,
    userQuestion
  }: { companyId: string; userQuestion: ManagementInformationQuestionCriteria },
  context: any
): Promise<any> => {
  try {
    let endpoint = endpoints.CREATE_COMPANY_USER_QUESTION.endpoint;
    endpoint = endpoint.replace('{companyId}', companyId);

    const serviceEndpoint = {
      ...endpoints.CREATE_COMPANY_USER_QUESTION,
      endpoint: endpoint
    };

    return await post(serviceEndpoint, createCompanyUserQuestion, userQuestion, context);
  } catch (error: Error | any) {
    handleError(error, { companyId, userQuestion });
  }
};
