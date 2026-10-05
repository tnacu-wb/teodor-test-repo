import { endpoints } from '../../../../../apollo/subgraphs/company-service-opera/services/base-service';
import { put } from '../../../../../apollo/client/rest-client';
import { updateCompanyUserQuestion } from '../../../../../apollo/subgraphs/company-service-opera/services/update-company-user-question-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('updateCompanyUserQuestion', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const companyId = 'COMP_1234567';
  const questionId = 'test_id';
  const userQuestion = {
    questionId: 'test_id',
    label: 'Test question',
    mandatory: false,
    managementHeader: 'Test question',
    active: false,
    location: 0,
    managementInformationAnswer: {
      answerType: 0,
      answers: ['Test Answer']
    },
    type: 'test q',
    positionId: 12
  };

  it('should call the put function with correct parameters when updating company user question', async () => {
    (put as jest.Mock).mockReturnValue({ data: '' });
    await updateCompanyUserQuestion({ companyId, questionId, userQuestion }, context);

    const serviceEndpoint = {
      ...endpoints.UPDATE_COMPANY_USER_QUESTION,
      endpoint: '/companies/COMP_1234567/admin/employee-questions/test_id'
    };

    expect(put).toHaveBeenCalledWith(
      serviceEndpoint,
      updateCompanyUserQuestion,
      userQuestion,
      context
    );
  });

  it('should handle errors gracefully when updating company user question fails', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      updateCompanyUserQuestion({ companyId, questionId, userQuestion }, context)
    ).rejects.toThrow('Test error');
  });
});
