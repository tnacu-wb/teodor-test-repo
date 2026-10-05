import { post, put } from '../../../../../apollo/client/rest-client';
import {
  initiateCcuiPayment,
  updateDiscount
} from '../../../../../apollo/subgraphs/basket-service/services/ccui-payment-service';
jest.mock('../../../../../apollo/client/rest-client');

describe('initiateCcuiPayment', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const initiateCcuiPaymentEndPoint = {
    endpoint: '/v1/baskets/ccui/GAA-5fc37e9f-2be0-415b-9721-c88b77dcdf7c/pay',
    flowCode: 'DIGITAL_PAY_005',
    axiosClient: expect.any(Function)
  };

  const initiateCcuiPaymentCriteria = {
    paymentOption: 'PAY_NOW',
    subPaymentType: 'subPaymentType'
  };

  it('should call the post function with correct parameters when initiateCcuiPayment is called', async () => {
    await initiateCcuiPayment(
      {
        basketReference: 'GAA-5fc37e9f-2be0-415b-9721-c88b77dcdf7c',
        initiateCcuiPaymentCriteria: initiateCcuiPaymentCriteria
      },
      context
    );
    expect(post).toHaveBeenCalledWith(
      initiateCcuiPaymentEndPoint,
      initiateCcuiPayment,
      initiateCcuiPaymentCriteria,
      context
    );
  });

  it('should handle errors gracefully when initiateCcuiPayment throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      initiateCcuiPayment(
        {
          basketReference: 'GAA-5fc37e9f-2be0-415b-9721-c88b77dcdf7c',
          initiateCcuiPaymentCriteria: initiateCcuiPaymentCriteria
        },
        context
      )
    ).rejects.toThrow('Test error');
  });
});

describe('updateDiscount', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const updateDiscountRequest = {
    basketReference: 'GAA-5fc37e9f-2be0-415b-9721-c88b77dcdf7c'
  };

  (put as jest.Mock).mockResolvedValue({
    data: {}
  });

  const updateDiscountEndPoint = {
    endpoint: '/v1/baskets/ccui/discount',
    flowCode: 'DIGITAL_CRE_003',
    axiosClient: expect.any(Function)
  };

  it('should call the put function with correct parameters when updateDiscount is called', async () => {
    await updateDiscount({ updateDiscountRequest: updateDiscountRequest }, context);
    expect(put).toHaveBeenCalledWith(
      updateDiscountEndPoint,
      updateDiscount,
      updateDiscountRequest,
      context
    );
  });

  it('should handle errors gracefully when updateDiscount throws an error', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      updateDiscount(
        {
          updateDiscountRequest: updateDiscountRequest
        },
        context
      )
    ).rejects.toThrow('Test error');
  });
});
