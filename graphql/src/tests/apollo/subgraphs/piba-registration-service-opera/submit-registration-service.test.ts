import { post } from '../../../../../src/apollo/client/rest-client';
import { submitRegistration } from '../../../../apollo/subgraphs/piba-registration-service-opera/services/submit-registration-service';
import { endpoints } from '../../../../apollo/subgraphs/piba-registration-service-opera/services/base-service';

jest.mock('../../../../apollo/client/rest-client');

describe('submitRegistration Resolver', () => {
  const headers = { 'Content-Type': 'application/json' };
  const context = { headers };
  const registrationSubmitRequest = {
    registrationCode: 'testRegistrationCode',
    authenticationAnswers: [
      {
        questionId: 33,
        answer: 'testAnswer'
      }
    ],
    registrationDetails: {
      title: 'Mr',
      forename: 'John',
      surname: 'Doe',
      emailAddress: 'Doe@example.com',
      mobileNumber: '+44222333',
      landlineNumber: '+44222333',
      memorableWord: 'memory'
    }
  };

  it('should call the post function when correct parameters are provided', async () => {
    const mockResponse = { status: 'success' };
    (post as jest.Mock).mockResolvedValueOnce(mockResponse);

    const response = await submitRegistration(
      { registrationSubmitRequest: registrationSubmitRequest },
      context
    );

    expect(post).toHaveBeenCalledWith(
      endpoints.SUBMIT_REGISTRATION,
      submitRegistration,
      registrationSubmitRequest,
      context
    );
    expect(response).toEqual(mockResponse);
  });

  it('should handle null response when it is returned gracefully', async () => {
    (post as jest.Mock).mockResolvedValueOnce(null);

    const response = await submitRegistration(
      { registrationSubmitRequest: registrationSubmitRequest },
      context
    );

    expect(response).toEqual(null);
  });
});
