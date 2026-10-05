import { handleError } from '../../../exception/error-handler';
import { objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';

/**
 * This method is used to fetch the registration questions and answers for a company.
 *
 * @param companyId
 * @param context contains the headers and the client
 * @returns The response containing the registration answered questions.
 */
export const retrieveCompanyRegistrationQuestionsAndAnswers = async (
  { companyId }: { companyId: string },
  context: any
): Promise<any> => {
  try {
    const companyRegistrationQuestionsAndAnswersEndpoint =
      endpoints.GET_COMPANY_REGISTRATION_QUESTIONS_AND_ANSWERS.endpoint.replace(
        '{companyId}',
        companyId
      );
    const serviceEndpoint = {
      ...endpoints.GET_COMPANY_REGISTRATION_QUESTIONS_AND_ANSWERS,
      endpoint: companyRegistrationQuestionsAndAnswersEndpoint
    };

    return await get(serviceEndpoint, retrieveCompanyRegistrationQuestionsAndAnswers, {}, context);
  } catch (error: Error | any) {
    handleError(error, {});
  }
};
