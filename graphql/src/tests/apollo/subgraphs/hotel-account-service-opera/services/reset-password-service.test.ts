import { resetPassword } from '../../../../../apollo/subgraphs/hotel-account-service-opera/services/reset-password-service';
import { put } from '../../../../../apollo/client/rest-client';

jest.mock('../../../../../apollo/client/rest-client', () => ({
  put: jest.fn()
}));

describe('resetPassword', () => {
  const context = { user: 'test-user' };
  const payload = {
    customerId: '12345',
    newPassword: 'newPassword123'
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should successfully reset the password and verify the URL', async () => {
    const mockResponse = { passwordChanged: true };
    (put as jest.Mock).mockResolvedValue(mockResponse);

    const result = await resetPassword({ payload }, context);

    const expectedURL = '/auth/hotels/forgot-password?business=true';

    expect(put).toHaveBeenCalledWith(
      {
        endpoint: expectedURL,
        method: 'PUT',
        flowCode: 'DIGITAL_ACC_016',
        axiosClient: expect.any(Function)
      },
      resetPassword,
      payload,
      context
    );
  });

  it('should handle errors gracefully when updating business questions fails', async () => {
    const mockError = new Error('Failed to reset password');
    (put as jest.Mock).mockRejectedValueOnce(mockError);

    await expect(resetPassword({ payload }, context)).rejects.toThrow('Failed to reset password');
  });
});
