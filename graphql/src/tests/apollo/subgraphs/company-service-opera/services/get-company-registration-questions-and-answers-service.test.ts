import { endpoints } from '../../../../../apollo/subgraphs/company-service-opera/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { retrieveCompanyRegistrationQuestionsAndAnswers } from '../../../../../apollo/subgraphs/company-service-opera/services/get-company-registration-questions-and-answers-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('retrieveCompanyRegistrationQuestionsAndAnswers', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const companyId = 'COMP_1234567';

  it('should call the get function with correct parameters when retrieving company registration questions and answers', async () => {
    await retrieveCompanyRegistrationQuestionsAndAnswers({ companyId }, context);
    const companyRegistrationQuestionsAndAnswersEndpoint =
      endpoints.GET_COMPANY_REGISTRATION_QUESTIONS_AND_ANSWERS.endpoint.replace(
        '{companyId}',
        companyId
      );
    const serviceEndpoint = {
      ...endpoints.GET_COMPANY_REGISTRATION_QUESTIONS_AND_ANSWERS,
      endpoint: companyRegistrationQuestionsAndAnswersEndpoint
    };

    expect(get).toHaveBeenCalledWith(
      serviceEndpoint,
      retrieveCompanyRegistrationQuestionsAndAnswers,
      {},
      context
    );
  });

  it('should handle errors gracefully when retrieving company registration questions and answers fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      retrieveCompanyRegistrationQuestionsAndAnswers({ companyId }, context)
    ).rejects.toThrow('Test error');
  });
});
