import { endpoints } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { retrievePaymentCards } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/get-payment-cards-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('retrievePaymentCards', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const companyId = 'COMP_1234567';

  it('should call the get function with correct parameters when retrieving payment cards', async () => {
    await retrievePaymentCards({ companyId }, context);
    const paymentCardsEndpoint = endpoints.GET_PAYMENT_CARDS.endpoint.replace(
      '{companyId}',
      companyId
    );
    const serviceEndpoint = {
      ...endpoints.GET_PAYMENT_CARDS,
      endpoint: paymentCardsEndpoint
    };

    expect(get).toHaveBeenCalledWith(serviceEndpoint, retrievePaymentCards, {}, context);
  });

  it('should handle errors gracefully when retrieving payment cards', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(retrievePaymentCards({ companyId }, context)).rejects.toThrow('Test error');
  });
});
