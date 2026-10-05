import { endpoints } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { retrieveAccountRegisteredUsers } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/get-account-registered-users-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('retrieveAccountRegisteredUsers', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const tetheredUserId = '123_456_789';
  const countryCode = 'GB';

  it('should call the get function with correct parameters when retrieving account registered users', async () => {
    await retrieveAccountRegisteredUsers({ tetheredUserId, countryCode }, context);
    const accountRegisteredUsersEndpoint = endpoints.GET_ACCOUNT_REGISTERED_USERS.endpoint.replace(
      '{tetheredUserId}',
      tetheredUserId
    );
    const serviceEndpoint = {
      ...endpoints.GET_ACCOUNT_REGISTERED_USERS,
      endpoint: accountRegisteredUsersEndpoint
    };

    expect(get).toHaveBeenCalledWith(
      serviceEndpoint,
      retrieveAccountRegisteredUsers,
      { countryCode: countryCode },
      context
    );
  });

  it('should handle errors gracefully when retrieving account registered users', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      retrieveAccountRegisteredUsers({ tetheredUserId, countryCode }, context)
    ).rejects.toThrow('Test error');
  });
});
