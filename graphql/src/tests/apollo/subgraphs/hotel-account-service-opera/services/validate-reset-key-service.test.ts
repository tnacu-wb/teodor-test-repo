import { post } from '../../../../../apollo/client/rest-client';
import { validateResetKey } from '../../../../../apollo/subgraphs/hotel-account-service-opera/services/validate-reset-key-service';
import { endpoints } from '../../../../../apollo/subgraphs/hotel-account-service-opera/services/base-service';

jest.mock('../../../../../apollo/client/rest-client', () => ({
  post: jest.fn()
}));

describe('validateResetKey', () => {
  const context = {};
  const validateResetKeyRequest = { resetKey: '123' };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should successfully validate the reset key', async () => {
    const mockResponse = {
      valid: true,
      emailAddress: 'email@test.com'
    };
    (post as jest.Mock).mockResolvedValue(mockResponse);

    const result = await validateResetKey({ validateResetKeyRequest }, context);

    expect(post).toHaveBeenCalledWith(
      {
        endpoint: endpoints.VALIDATE_RESET_KEY.endpoint,
        flowCode: 'DIGITAL_ACC_018',
        axiosClient: expect.any(Function)
      },
      validateResetKey,
      validateResetKeyRequest,
      context
    );

    expect(result).toEqual(mockResponse);
  });

  it('should handle errors gracefully when validation call fails', async () => {
    const mockError = new Error('Failed to validate reset key');
    (post as jest.Mock).mockRejectedValueOnce(mockError);

    await expect(validateResetKey({ validateResetKeyRequest }, context)).rejects.toThrow(
      'Failed to validate reset key'
    );
  });
});
