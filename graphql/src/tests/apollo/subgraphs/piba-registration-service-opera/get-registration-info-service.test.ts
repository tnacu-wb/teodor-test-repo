import { get } from '../../../../apollo/client/rest-client';
import { endpoints } from '../../../../apollo/subgraphs/piba-registration-service-opera/services/base-service';
import {
  getRegistrationInfo,
  GetRegistrationInfoArgs
} from '../../../../apollo/subgraphs/piba-registration-service-opera/services/get-registration-info-service';

jest.mock('../../../../apollo/client/rest-client');

describe('getRegistrationInfo Resolver', () => {
  const args: GetRegistrationInfoArgs = {
    registrationCode: 'test-code'
  };
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const mockResponse = {
    registrationCodeInfo: {
      registrationCode: 'test-code',
      primarySchemeCustomerId: 12345,
      schemeCustomerId: 67890,
      registrationRole: 'AccountHolder',
      authenticationQuestions: [
        {
          questionId: 1,
          question: 'What is your favorite color?'
        },
        {
          questionId: 2,
          question: 'What is your mother maiden name?'
        }
      ]
    }
  };

  it('should return registration info data data when valid parameters are provided', async () => {
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);

    const response = await getRegistrationInfo(args, context);
    expect(response).toEqual(mockResponse);
  });

  it('should handle null response when fetching registration info', async () => {
    (get as jest.Mock).mockResolvedValueOnce(null);

    const response = await getRegistrationInfo(args, context);

    expect(response).toEqual(null);
  });

  it('should handle errors gracefully when fetching registration info fails', async () => {
    const error = new Error('Failed to fetch registration info');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getRegistrationInfo(args, context)).rejects.toThrow(
      'Failed to fetch registration info'
    );
  });

  it('should map registrationRole ReportAndInvoices to FinanceUser', async () => {
    const mockResponse = {
      registrationCodeInfo: {
        registrationCode: 'test-code',
        primarySchemeCustomerId: 12345,
        schemeCustomerId: 67890,
        registrationRole: 'ReportsAndInvoices',
        authenticationQuestions: [
          {
            questionId: 1,
            question: 'What is your favorite color?'
          }
        ]
      }
    };

    (get as jest.Mock).mockResolvedValueOnce(mockResponse);

    const response = await getRegistrationInfo(args, context);

    expect(response.registrationCodeInfo.registrationRole).toEqual('FinanceUser');
  });

  it('should map registrationRole MMAReportAndInvoices to FinanceUser', async () => {
    const mockResponse = {
      registrationCodeInfo: {
        registrationCode: 'test-code',
        primarySchemeCustomerId: 12345,
        schemeCustomerId: 67890,
        registrationRole: 'MMAReportsAndInvoices',
        authenticationQuestions: [
          {
            questionId: 1,
            question: 'What is your favorite color?'
          }
        ]
      }
    };

    (get as jest.Mock).mockResolvedValueOnce(mockResponse);

    const response = await getRegistrationInfo(args, context);

    expect(response.registrationCodeInfo.registrationRole).toEqual('FinanceUser');
  });

  it('should throw an error when registrationRole mapping cannot be done', async () => {
    const mockResponse = {
      registrationCodeInfo: {
        registrationCode: 'test-code',
        primarySchemeCustomerId: 12345,
        schemeCustomerId: 67890,
        registrationRole: 'InvalidRole',
        authenticationQuestions: [
          {
            questionId: 1,
            question: 'What is your favorite color?'
          }
        ]
      }
    };

    (get as jest.Mock).mockResolvedValueOnce(mockResponse);

    await expect(getRegistrationInfo(args, context)).rejects.toThrow(
      'Invalid registrationRole: InvalidRole'
    );
  });
});
