import { post } from '../../../../../src/apollo/client/rest-client';
import { authenticateRegistration } from '../../../../apollo/subgraphs/piba-registration-service-opera/services/authenticate-registration-service';
import { endpoints } from '../../../../apollo/subgraphs/piba-registration-service-opera/services/base-service';

jest.mock('../../../../apollo/client/rest-client');

describe('authenticateRegistration Resolver', () => {
  const authenticateRegistrationRequest = {
    registrationCode: 'testRegistrationCode',
    authenticationAnswers: [
      {
        question: 'testQuestion',
        answer: 'testAnswer'
      }
    ]
  };

  it('should call the post function when correct parameters are provided', async () => {
    const mockResponse = { status: 'success' };
    (post as jest.Mock).mockResolvedValueOnce(mockResponse);

    const response = await authenticateRegistration({ authenticateRegistrationRequest }, {});

    expect(post).toHaveBeenCalledWith(
      endpoints.AUTHENTICATE_REGISTRATION,
      authenticateRegistration,
      authenticateRegistrationRequest,
      {}
    );
    expect(response).toEqual(mockResponse);
  });

  it('should handle null response when it is returned gracefully', async () => {
    (post as jest.Mock).mockResolvedValueOnce(null);

    const response = await authenticateRegistration({ authenticateRegistrationRequest }, {});

    expect(response).toEqual(null);
  });
});
