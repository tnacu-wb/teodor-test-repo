import { get } from '../../../../../../src/apollo/client/rest-client';
import { getPaypalRule } from '../../../../../../src/apollo/subgraphs/rules-agent-entity-service/services/paypal-rule-service';
import { endpoints } from '../../../../../../src/apollo/subgraphs/rules-agent-entity-service/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

const context = {};

describe('getPaypalRule', () => {
  it('should return paypal rule when given hotel IDs', async () => {
    const mockResponse = {
      isPayPalPaymentEnabled: true,
      generatedAt: '2025-02-07T13:00:25.998809112'
    };
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);
    const hotelIds = { hotelIds: ['FRAMTI', 'LONEUS'] };
    const result = await getPaypalRule(
      { channel: 'PI', country: 'UK', hotelId: 'LONEUS' },
      context
    );

    expect(result).toEqual(mockResponse);
    expect(get).toHaveBeenCalledWith(
      endpoints.PAYPAL_RULE,
      getPaypalRule,
      { channelId: 'PI', country: 'UK', hotelId: 'LONEUS' },
      context
    );
  });
});
