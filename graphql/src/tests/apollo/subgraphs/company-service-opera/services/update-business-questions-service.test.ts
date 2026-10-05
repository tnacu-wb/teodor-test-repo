import { endpoints } from '../../../../../apollo/subgraphs/company-service-opera/services/base-service';
import { put } from '../../../../../apollo/client/rest-client';
import { updateBusinessQuestions } from '../../../../../apollo/subgraphs/company-service-opera/services/update-business-questions-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('updateBusinessQuestions', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const companyId = 'COMP_1234567';
  const questionId = 'test_id';
  const userQuestion = {
    questionId: 'test_id',
    label: 'Test question',
    mandatory: false,
    managementHeader: 'customer reference',
    active: false,
    location: 'B',
    managementInformationAnswer: {
      answerType: 'F',
      answers: ['Test Answer']
    },
    type: 'customer reference',
    positionId: 12
  };

  it('should call the put function with correct parameters when updating business questions', async () => {
    (put as jest.Mock).mockReturnValue({ data: '' });
    await updateBusinessQuestions({ companyId, questionId, userQuestion }, context);
    const updateBusinessQuestionsEndpoint = endpoints.UPDATE_BUSINESS_QUESTIONS.endpoint
      .replace('{companyId}', companyId)
      .replace('{questionId}', questionId);
    const serviceEndpoint = {
      ...endpoints.UPDATE_BUSINESS_QUESTIONS,
      endpoint: updateBusinessQuestionsEndpoint
    };

    expect(put).toHaveBeenCalledWith(
      serviceEndpoint,
      updateBusinessQuestions,
      userQuestion,
      context
    );
  });

  it('should handle errors gracefully when updating business questions fails', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      updateBusinessQuestions({ companyId, questionId, userQuestion }, context)
    ).rejects.toThrow('Test error');
  });
});
