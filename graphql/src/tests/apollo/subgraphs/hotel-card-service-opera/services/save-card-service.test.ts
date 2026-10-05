import { endpoints } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';
import { initiateSaveCard } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/save-card-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('initiateSaveCard', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const initiateSaveCardRequest = {
    requestId: 'REQ_1234',
    billingAddress: {
      line1: 'Street 1',
      countryCode: 'GB',
      postCode: '123',
      companyName: 'Company',
      type: 'BUSINESS'
    },
    cardDetails: {
      business: true,
      cardType: 'PIBA',
      cardId: '15',
      cardLabel: 'apollo card',
      memorableWord: 'hello',
      cnpRequired: false,
      personalCard: false
    },
    environment: 'http://localhost:7973',
    language: 'en'
  };

  it('should call the post function with correct parameters when initiating save card', async () => {
    await initiateSaveCard({ initiateSaveCardRequest }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.SAVE_CARD,
      initiateSaveCard,
      initiateSaveCardRequest,
      context
    );
  });

  it('should handle errors gracefully when initiating save card', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(initiateSaveCard({ initiateSaveCardRequest }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
