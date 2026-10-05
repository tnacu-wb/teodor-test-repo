import { post } from '../../../../apollo/client/rest-client';
import { businessTether } from '../../../../apollo/subgraphs/business-tether-service-opera/services/business-tether';
import { endpoints } from '../../../../apollo/subgraphs/business-tether-service-opera/services/base-service';

jest.mock('../../../../apollo/client/rest-client');

describe('businessTetherLogin', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const tetherLinkRequest = {
    linkCode: 'qyg-ujn-xes',
    linkId: '3089503200100352',
    memorableWord: 'Hello12egt',
    saveInCdh: true
  };

  const request = {
    tetherLinkRequest: tetherLinkRequest
  };

  it('should call the post function with correct parameters when businessTether is called', async () => {
    await businessTether(request, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.BUSINESS_TETHER,
      businessTether,
      request.tetherLinkRequest,
      context
    );
  });

  it('should handle errors gracefully when businessTether throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      post(endpoints.BUSINESS_TETHER, { loginCriteria: request }, context)
    ).rejects.toThrow('Test error');
  });
});
