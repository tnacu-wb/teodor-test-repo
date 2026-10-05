import { post } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/hotel-register-service-opera/services/base-service';
import { appsRegistration } from '../../../../../apollo/subgraphs/hotel-register-service-opera/services/apps-register-service';

jest.mock('../../../../../apollo/client/rest-client');
beforeAll(() => {
  jest.clearAllMocks();
});

describe('appsRegistration', () => {
  const context = {};
  const appsRegistrationRequest = {
    contactDetail: {
      fistName: 'aaa',
      lastName: 'bbb',
      email: 'aa@aa.com'
    },
    password: 'test'
  };

  it('should call method when appsRegistration is called with correct parameters', async () => {
    await appsRegistration({ appsRegistrationRequest: appsRegistrationRequest }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.APPS_ACCOUNT_REGISTRATION,
      appsRegistration,
      appsRegistrationRequest,
      context
    );
  });

  it('should handle errors when appsRegistration fails correctly', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValue(error);
    await expect(
      appsRegistration({ appsRegistrationRequest: appsRegistrationRequest }, context)
    ).rejects.toThrow('Test error');
  });
});
