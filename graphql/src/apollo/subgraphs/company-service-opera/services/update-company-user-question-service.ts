import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { put } from '../../../client/rest-client';

/**
 * This method is used to update company user questions.
 *
 * @param companyId
 * @param questionId
 * @param userQuestion
 * @param context
 * @returns the response containing a string.
 */
export const updateCompanyUserQuestion = async (
  {
    companyId,
    questionId,
    userQuestion
  }: { companyId: string; questionId: string; userQuestion: any },
  context: any
): Promise<any> => {
  try {
    const updateCompanyUserQuestionEndpoint = endpoints.UPDATE_COMPANY_USER_QUESTION.endpoint
      .replace('{companyId}', companyId)
      .replace('{questionId}', questionId);
    const serviceEndpoint = {
      ...endpoints.UPDATE_COMPANY_USER_QUESTION,
      endpoint: updateCompanyUserQuestionEndpoint
    };

    const response = await put(serviceEndpoint, updateCompanyUserQuestion, userQuestion, context);
    return response.data;
  } catch (error: Error | any) {
    handleError(error, { companyId: companyId, questionId: questionId, userQuestion });
  }
};
