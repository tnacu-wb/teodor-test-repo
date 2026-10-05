import { forgotPassword } from '../../../../../apollo/subgraphs/hotel-account-service-opera/services/forgot-password-service';
import { post } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/hotel-account-service-opera/services/base-service';

jest.mock('../../../../../apollo/client/rest-client', () => ({
  post: jest.fn()
}));

describe('forgotPassword', () => {
  const context = { headers: {} };
  const forgottenPasswordRequest = { username: 'test@example.com' };
  const language = 'de';
  const innBusiness = true;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call the forgotPassword API with correct headers, params, and payload', async () => {
    const mockResponse = { success: true };
    (post as jest.Mock).mockResolvedValue(mockResponse);

    const result = await forgotPassword(
      { language, innBusiness, forgottenPasswordRequest },
      context
    );

    const expectedHeaders = {
      language: 'de'
    };

    const expectedServiceEndpoint = {
      ...endpoints.FORGOT_PASSWORD,
      endpoint: `${endpoints.FORGOT_PASSWORD.endpoint}?innBusiness=true`
    };

    expect(post).toHaveBeenCalledWith(
      expectedServiceEndpoint,
      forgotPassword,
      forgottenPasswordRequest,
      { headers: expectedHeaders }
    );
    expect(result).toEqual(mockResponse);
  });

  it('should handle errors gracefully when the API call fails', async () => {
    const mockError = new Error('Failed to call forgotPassword API');
    (post as jest.Mock).mockRejectedValueOnce(mockError);

    await expect(
      forgotPassword({ language, innBusiness, forgottenPasswordRequest }, context)
    ).rejects.toThrow('Failed to call forgotPassword API');
  });
});
