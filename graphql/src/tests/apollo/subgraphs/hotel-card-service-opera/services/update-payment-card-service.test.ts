import { endpoints } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/base-service';
import { put } from '../../../../../apollo/client/rest-client';
import { updatePaymentCard } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/update-payment-card-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('updatePaymentCard', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const companyId = 'COMP_123-456';
  const cardId = '1';
  const updatePaymentCardRequest = {
    cardLabel: 'ApolloVISA1',
    cardId: '1',
    cardHolderName: 'ApolloCard1',
    cardNumber: '1111222233334444',
    cardToken: '111122223333444445642',
    cardType: 'AC',
    cnpBusinessAccountPassword: '123Card',
    cnpBusinessAccountUsername: '',
    cnpRequired: true,
    expiryDate: '1027',
    billingAddress: {
      line1: '1 Apollo House',
      postCode: 'APP 1HO',
      countryCode: 'GB',
      type: 'VI'
    }
  };

  it('should call the put function with correct parameters when updating payment card and return success=true', async () => {
    (put as jest.Mock).mockReturnValue({ status: 204, data: '' });
    const response = await updatePaymentCard(
      { companyId, cardId, updatePaymentCardRequest },
      context
    );
    const updatePaymentCardEndpoint = endpoints.UPDATE_PAYMENT_CARD.endpoint
      .replace('{companyId}', companyId)
      .replace('{cardId}', cardId);
    const serviceEndpoint = {
      ...endpoints.UPDATE_PAYMENT_CARD,
      endpoint: updatePaymentCardEndpoint
    };

    expect(put).toHaveBeenCalledWith(
      serviceEndpoint,
      updatePaymentCard,
      updatePaymentCardRequest,
      context
    );
    expect(response).toBe('{success=true}');
  });

  it('should handle errors gracefully when updating payment card', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      updatePaymentCard({ companyId, cardId, updatePaymentCardRequest }, context)
    ).rejects.toThrow('Test error');
  });

  it('should throw error when status code is not 204', async () => {
    const errorMessage =
      '{"message":"{\\"status\\":500,\\"data\\":\\"Internal Server Error\\"}","errors":[{"field":"Error","message":"{\\"status\\":500,\\"data\\":\\"Internal Server Error\\"}"}],"errorType":404}';
    (put as jest.Mock).mockReturnValue({ status: 500, data: 'Internal Server Error' });

    await expect(
      updatePaymentCard({ companyId, cardId, updatePaymentCardRequest }, context)
    ).rejects.toThrow(new Error(errorMessage));
  });
});
