import { endpoints } from './base-service';
import { put } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

/**
 * This method is used to update company business questions.
 *
 * @param companyId
 * @param questionId
 * @param userQuestion
 * @param context contains the headers and the client
 * @returns the response containing a string.
 */
export const updateBusinessQuestions = async (
  {
    companyId,
    questionId,
    userQuestion
  }: { companyId: string; questionId: string; userQuestion: any },
  context: any
): Promise<any> => {
  try {
    const updateBusinessQuestionsEndpoint = endpoints.UPDATE_BUSINESS_QUESTIONS.endpoint
      .replace('{companyId}', companyId)
      .replace('{questionId}', questionId);
    const serviceEndpoint = {
      ...endpoints.UPDATE_BUSINESS_QUESTIONS,
      endpoint: updateBusinessQuestionsEndpoint
    };

    const response = await put(serviceEndpoint, updateBusinessQuestions, userQuestion, context);
    return response.data;
  } catch (error: Error | any) {
    handleError(error, { companyId: companyId, questionId: questionId, userQuestion });
  }
};
