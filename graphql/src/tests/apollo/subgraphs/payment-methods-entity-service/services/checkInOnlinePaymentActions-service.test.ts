import { get } from '../../../../../../src/apollo/client/rest-client';
import { getCheckInOnlinePaymentActions } from '../../../../../../src/apollo/subgraphs/payment-methods-entity-service/services/payment-methods-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('checkInOnlinePaymentActions', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  const context = {};

  const args = {
    basketReference: 'AKU-a15b4934-6882-4f48-b31e-71d95b916ce0'
  };

  it('should call the get function with correct parameters', async () => {
    await getCheckInOnlinePaymentActions(args, context);

    expect(get).toHaveBeenCalledWith(
      expect.objectContaining({
        endpoint: '/v1/payment-methods/payment-actions/AKU-a15b4934-6882-4f48-b31e-71d95b916ce0',
        flowCode: 'DIGITAL_PAY_003'
      }),
      getCheckInOnlinePaymentActions,
      null,
      context
    );
  });
});
