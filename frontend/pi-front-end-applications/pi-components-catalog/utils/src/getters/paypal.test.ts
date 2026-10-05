import { PAYMENT_FAILED_VALUE } from '@whitbread-eos/api';

import { getPaypalDeviceData, getPaypalOptionsParams } from './paypal';

jest.mock('braintree-web', () => ({
  client: {
    create: jest.fn().mockResolvedValue({}),
  },
  dataCollector: {
    create: jest.fn().mockResolvedValue({
      getDeviceData: jest.fn().mockResolvedValue({ deviceData: 'mock-device-data' }),
    }),
  },
}));

describe('get Paypal Device Data Methods', () => {
  const mockedClientToken = 'clientToken';

  it('should return the Device data', async () => {
    const deviceData = await getPaypalDeviceData(mockedClientToken);
    expect(deviceData).toEqual({ deviceData: 'mock-device-data' });
  });

  it('should return undefined when no token provided', async () => {
    const deviceData = await getPaypalDeviceData(undefined);
    expect(deviceData).toBeUndefined();
  });
});

describe('get Paypal Options Params Methods', () => {
  const approveCallBack = jest.fn();
  const errorCallBack = jest.fn();
  const paypalOptionsParams = getPaypalOptionsParams({
    currencyCode: 'GB',
    approveCallBack,
    errorCallBack,
  });
  it('should return the Paypal Options Params', () => {
    expect(paypalOptionsParams).toEqual({
      createBillingAgreement: expect.any(Function),
      onApprove: expect.any(Function),
      onError: expect.any(Function),
    });
  });
  it('should have onError', () => {
    const onErrorFn = jest.spyOn(console, 'error').mockImplementation();
    paypalOptionsParams.onError({});
    expect(onErrorFn).toHaveBeenCalled();
    expect(errorCallBack).toHaveBeenCalledWith(PAYMENT_FAILED_VALUE);
    onErrorFn.mockRestore();
  });
});
