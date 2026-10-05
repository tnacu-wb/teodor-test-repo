import { post } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/account-entity-service/services/base-service';
import { createAccount } from '../../../../../apollo/subgraphs/account-entity-service/services/account-registration-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('createAccount', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  const context = {};
  const createAccountRequest = {
    password: 'Héllo12ß',
    language: 'EN',
    country: 'GB',
    title: 'Mr',
    firstName: 'Maru',
    lastName: 'Maru',
    mobile: '07777777777',
    emailAddress: 'maru@mailinator.com',
    address: {
      addressLine1: 'Line One',
      addressLine4: 'Line Four',
      countryCode: 'D',
      postalCode: 'NW11 7RJ',
      addressType: 'BUSINESS'
    }
  };

  it('should call the post function with correct parameters when createAccount is called', async () => {
    await createAccount({ createAccountRequest: createAccountRequest }, context);
    expect(post).toHaveBeenCalledWith(
      endpoints.CREATE_ACCOUNT,
      createAccount,
      createAccountRequest,
      context
    );
  });

  it('should handle errors gracefully when post function throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      createAccount({ createAccountRequest: createAccountRequest }, context)
    ).rejects.toThrow('Test error');
  });
});
