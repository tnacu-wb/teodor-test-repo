import { deleteCustomQuestion } from '../../../../../apollo/subgraphs/company-service-opera/services/delete-custom-question-service';
import { endpoints } from '../../../../../apollo/subgraphs/company-service-opera/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';

jest.mock('../../../../../apollo/client/rest-client');
jest.mock('../../../../../apollo/exception/error-handler', () => ({
  handleError: jest.fn()
}));

describe('deleteCustomQuestion', () => {
  const companyId = '123';
  const questionId = '456';
  const context = {};

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should make a POST request to the correct endpoint when deleting a custom question', async () => {
    await deleteCustomQuestion({ companyId, questionId }, context);

    expect(post).toHaveBeenCalledTimes(1);
    expect(post).toHaveBeenCalledWith(
      {
        ...endpoints.DELETE_CUSTOM_QUESTION,
        endpoint: endpoints.DELETE_CUSTOM_QUESTION.endpoint
          .replace('{companyId}', companyId)
          .replace('{questionId}', questionId)
      },
      deleteCustomQuestion,
      {},
      context
    );
  });
});
