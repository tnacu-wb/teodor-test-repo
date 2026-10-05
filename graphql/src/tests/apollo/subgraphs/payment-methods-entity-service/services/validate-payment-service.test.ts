import { post } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/payment-methods-entity-service/services/base-service';
import { validatePaymentMethod } from '../../../../../apollo/subgraphs/payment-methods-entity-service/services/validate-payment-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('validatePaymentMethod', () => {
  const context = {};

  beforeEach(() => {
    jest.clearAllMocks();
  });
  const basketReference = 'AQN-e8692e41-f65f-472d-84cc-3eb1751b7767';
  const createPaymentCriteria = {
    booking: {
      businessSite: {
        identifier: 'HEAPTI',
        name: 'London Heathrow Airport (M4/J4)',
        type: 'HOTEL',
        location: 'HEAPTI'
      },
      channel: 'PI',
      journey: 'BOOKING',
      language: 'en',
      rooms: [
        {
          adultsNumber: 1,
          rate: 'FLEXRATE',
          type: 'DOUBLE'
        },
        {
          adultsNumber: 1,
          rate: 'FLEXRATE',
          type: 'DOUBLE'
        }
      ],
      arrivalDate: '2025-04-25',
      departureDate: '2025-04-27',
      type: 'PAY_ON_ARRIVAL'
    },
    payment: {
      billing: {
        address: {
          addressLine1: '1 Middlesex Street',
          addressLine2: '',
          addressLine3: '',
          addressLine4: 'LONDON',
          postalCode: 'E1 7AA',
          country: 'GB',
          addressType: 'BUSINESS'
        },
        email: 'alinna.gore@gmail.com',
        firstName: 'Alina',
        lastName: 'Gore',
        telephone: '+440771496994',
        title: 'Mr',
        differentBillingAddress: false,
        bookerIsNotGuest: false
      },
      environment: 'https://www.dit.premierinn.digital',
      subType: 'ECOMM',
      type: 'CARD',
      pibaCardPresent: true
    },
    charityPackageCode: 'ZCHRY1',
    hotelId: 'HEAPTI',
    requestId: '3c58f258-516b-40e1-822f-e03284db5945'
  };

  const args = { basketReference, createPaymentCriteria };

  it('should call get when validatePaymentMethod is called with correct parameters', async () => {
    await validatePaymentMethod(args, context);
    expect(post).toHaveBeenCalledWith(
      endpoints.PAYMENT_METHODS,
      validatePaymentMethod,
      {
        basketReference: 'AQN-e8692e41-f65f-472d-84cc-3eb1751b7767',
        type: 'CARD',
        selectedPaymentOption: 'PAY_ON_ARRIVAL'
      },
      context
    );
  });

  it('should handle errors gracefully when validatePaymentMethod fails', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);
    await expect(validatePaymentMethod(args, context)).rejects.toThrow('Test error');
  });
});
