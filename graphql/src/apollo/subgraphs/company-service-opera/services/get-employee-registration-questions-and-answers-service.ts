import { handleError } from '../../../exception/error-handler';
import { objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';

/**
 * This method is used to fetch the registration questions and answers for a company employee.
 *
 * @param companyId
 * @param employeeId
 * @param context contains the headers and the client
 * @returns The response containing the registration answered questions.
 */
export const retrieveEmployeeRegistrationQuestionsAndAnswers = async (
  { companyId, employeeId }: { companyId: string; employeeId: string },
  context: any
): Promise<any> => {
  try {
    const employeeRegistrationQuestionsAndAnswersEndpoint =
      endpoints.GET_EMPLOYEE_REGISTRATION_QUESTIONS_AND_ANSWERS.endpoint
        .replace('{companyId}', companyId)
        .replace('{employeeId}', employeeId);
    const serviceEndpoint = {
      ...endpoints.GET_EMPLOYEE_REGISTRATION_QUESTIONS_AND_ANSWERS,
      endpoint: employeeRegistrationQuestionsAndAnswersEndpoint
    };

    return await get(serviceEndpoint, retrieveEmployeeRegistrationQuestionsAndAnswers, {}, context);
  } catch (error: Error | any) {
    handleError(error, {});
  }
};
