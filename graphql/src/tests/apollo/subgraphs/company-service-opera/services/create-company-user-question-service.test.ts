import { endpoints } from '../../../../../apollo/subgraphs/company-service-opera/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';
import { createCompanyUserQuestion } from '../../../../../apollo/subgraphs/company-service-opera/services/create-company-user-question-service';
import { ManagementInformationQuestionCriteria } from '../../../../../apollo/subgraphs/company-service-opera/models/management-information-question-criteria';

jest.mock('../../../../../apollo/client/rest-client');
jest.mock('../../../../../apollo/exception/error-handler', () => ({
  handleError: jest.fn()
}));
jest.mock('../../../../../apollo/utils/base-utils', () => ({
  objectIsNullEmptyOrUndefined: jest.fn()
}));

describe('createCompanyUserQuestion', () => {
  const companyId = 'COMP_c0c4c99c-33fd-4392-b351-32c0ff4a9d41';
  const userQuestion: ManagementInformationQuestionCriteria = {
    location: 0,
    questionId: 'COQU_750fd3f5-8c5c-4fb8-8702-b8db50c413bc',
    label: 'qweqwe',
    mandatory: false,
    managementHeader: 'dadada',
    active: true,
    managementInformationAnswer: {
      answerType: 0,
      answers: ['plplp']
    },
    positionId: 5
  };
  const context = {};

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should make a POST request to the correct endpoint when creating a company user question', async () => {
    await createCompanyUserQuestion({ companyId, userQuestion }, context);

    expect(post).toHaveBeenCalledTimes(1);
    expect(post).toHaveBeenCalledWith(
      {
        ...endpoints.CREATE_COMPANY_USER_QUESTION,
        endpoint: endpoints.CREATE_COMPANY_USER_QUESTION.endpoint.replace('{companyId}', companyId)
      },
      createCompanyUserQuestion,
      userQuestion,
      context
    );
  });
});
