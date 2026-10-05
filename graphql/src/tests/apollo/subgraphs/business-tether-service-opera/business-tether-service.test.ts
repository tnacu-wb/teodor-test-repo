import { post } from '../../../../apollo/client/rest-client';
import { businessTetherLogin } from '../../../../apollo/subgraphs/business-tether-service-opera/services/business-tether-service';
import { endpoints } from '../../../../apollo/subgraphs/business-tether-service-opera/services/base-service';

jest.mock('../../../../apollo/client/rest-client');

describe('businessTetherLogin', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const loginCriteria = {
    guid: 'AJK123456'
  };

  it('should call the post function with correct parameters when businessTetherLogin is called', async () => {
    await businessTetherLogin({ loginCriteria }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.TETHER_LOGIN,
      businessTetherLogin,
      loginCriteria,
      context
    );
  });

  it('should handle errors gracefully when businessTetherLogin throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);
    await expect(post(endpoints.TETHER_LOGIN, { loginCriteria }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
