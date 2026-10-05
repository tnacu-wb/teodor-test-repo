import { endpoints } from '../../../../../apollo/subgraphs/company-service-opera/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { retrieveEmployeeRegistrationQuestionsAndAnswers } from '../../../../../apollo/subgraphs/company-service-opera/services/get-employee-registration-questions-and-answers-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('retrieveEmployeeRegistrationQuestionsAndAnswers', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const companyId = 'COMP_1234567';
  const employeeId = 'EMP_1234567';

  it('should call the get method with the correct parameters when retrieving employee registration questions and answers', async () => {
    await retrieveEmployeeRegistrationQuestionsAndAnswers({ companyId, employeeId }, context);
    const employeeRegistrationQuestionsAndAnswersEndpoint =
      endpoints.GET_EMPLOYEE_REGISTRATION_QUESTIONS_AND_ANSWERS.endpoint
        .replace('{companyId}', companyId)
        .replace('{employeeId}', employeeId);

    const expectedEndpoint = {
      ...endpoints.GET_EMPLOYEE_REGISTRATION_QUESTIONS_AND_ANSWERS,
      endpoint: employeeRegistrationQuestionsAndAnswersEndpoint
    };
    expect(get).toHaveBeenCalledWith(
      expectedEndpoint,
      retrieveEmployeeRegistrationQuestionsAndAnswers,
      {},
      context
    );
  });

  it('should handle errors gracefully when retrieving employee registration questions and answers fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      retrieveEmployeeRegistrationQuestionsAndAnswers({ companyId, employeeId }, context)
    ).rejects.toThrow('Test error');
  });
});
