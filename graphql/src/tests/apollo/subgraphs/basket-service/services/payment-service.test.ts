import { post } from '../../../../../apollo/client/rest-client';
import {
  initiatePayment,
  initiatePaypalPayment
} from '../../../../../apollo/subgraphs/basket-service/services/payment-service';
import { CreatePaymentCriteria } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/models/create-payment-criteria';

jest.mock('../../../../../apollo/client/rest-client');

describe('initiatePayment', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  const context = {};
  const initiatePaymentEndPoint = {
    endpoint: '/v1/baskets/GAA-5fc37e9f/pay',
    flowCode: 'DIGITAL_PAY_003',
    axiosClient: expect.any(Function)
  };

  const createPaymentCriteria: CreatePaymentCriteria = {
    isCiol: undefined,
    tmpBasketRef: undefined,
    bookingNotes: undefined,
    specialRequests: undefined,
    requestId: '175f773a',
    hotelId: 'HEAPTI',
    charityPackageCode: undefined,
    booking: {
      businessSite: {
        identifier: 'HEAPTI',
        name: 'London Euston',
        type: 'HOTEL',
        location: 'HEAPTI'
      },
      channel: 'PI',
      journey: 'BOOKING',
      language: 'en',
      rooms: [
        {
          adultsNumber: 2,
          rate: 'FLEXRATE',
          type: 'PPLDBL'
        }
      ],
      type: 'PAY_ON_ARRIVAL',
      arrivalDate: '2025-04-10',
      departureDate: '2025-04-11',
      leadGuest: undefined
    },
    payment: {
      billing: {
        address: {
          addressType: undefined,
          companyName: undefined,
          country: 'GB',
          addressLine1: 'High',
          addressLine2: 'no. 192',
          addressLine3: 'Winchester',
          addressLine4: 'London',
          cityName: undefined,
          postalCode: 'E15 6AN'
        },
        cardBillingAddress: undefined,
        differentBillingAddress: undefined,
        bookerIsNotGuest: undefined,
        email: 'test_auto@mail.com',
        firstName: 'Emma',
        lastName: 'Karateautomation',
        telephone: '+492345667789',
        title: 'Mrs'
      },
      card: undefined,
      environment: 'DIT',
      subType: 'ECOMM',
      type: 'CARD',
      sca: undefined,
      businessItems: undefined,
      pibaCardPresent: true,
      paypalNonce: undefined,
      paypalDeviceData: undefined
    },
    businessAccount: undefined,
    companyQuestionAndAnswerDetails: undefined
  };
  const basketReference = 'GAA-5fc37e9f';
  const args = { basketReference, createPaymentCriteria };

  it('should call the get function with correct parameters when initiatePayment is called', async () => {
    await initiatePayment(args, context);
    expect(post).toHaveBeenCalledWith(
      initiatePaymentEndPoint,
      initiatePayment,
      args.createPaymentCriteria,
      context
    );
  });

  it('should handle errors gracefully when initiatePayment throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(initiatePayment(args, context)).rejects.toThrow('Test error');
  });
});

describe('initiatePaypalPayment', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  const context = {};
  const initiatePaypalPaymentEndPoint = {
    endpoint: '/v1/baskets/GAA-5fc37e9f/pp-pay',
    flowCode: 'DIGITAL_PAY_004',
    axiosClient: expect.any(Function)
  };

  const createPaymentCriteria: CreatePaymentCriteria = {
    isCiol: undefined,
    tmpBasketRef: undefined,
    bookingNotes: undefined,
    specialRequests: undefined,
    requestId: '175f773a',
    hotelId: 'HEAPTI',
    charityPackageCode: undefined,
    booking: {
      businessSite: {
        identifier: 'HEAPTI',
        name: 'London Euston',
        type: 'HOTEL',
        location: 'HEAPTI'
      },
      channel: 'PI',
      journey: 'BOOKING',
      language: 'en',
      rooms: [
        {
          adultsNumber: 2,
          rate: 'FLEXRATE',
          type: 'PPLDBL'
        }
      ],
      type: 'PAY_ON_ARRIVAL',
      arrivalDate: '2025-04-10',
      departureDate: '2025-04-11',
      leadGuest: undefined
    },
    payment: {
      billing: {
        address: {
          addressType: undefined,
          companyName: undefined,
          country: 'GB',
          addressLine1: 'High',
          addressLine2: 'no. 192',
          addressLine3: 'Winchester',
          addressLine4: 'London',
          cityName: undefined,
          postalCode: 'E15 6AN'
        },
        cardBillingAddress: undefined,
        differentBillingAddress: undefined,
        bookerIsNotGuest: undefined,
        email: 'test_auto@mail.com',
        firstName: 'Emma',
        lastName: 'Karateautomation',
        telephone: '+492345667789',
        title: 'Mrs'
      },
      card: undefined,
      environment: 'DIT',
      subType: 'ECOMM',
      type: 'CARD',
      sca: undefined,
      businessItems: undefined,
      pibaCardPresent: true,
      paypalNonce: undefined,
      paypalDeviceData: undefined
    },
    businessAccount: undefined,
    companyQuestionAndAnswerDetails: undefined
  };
  const basketReference = 'GAA-5fc37e9f';
  const args = { basketReference, createPaymentCriteria };

  it('should call the get function with correct parameters when initiatePaypalPayment is called', async () => {
    await initiatePaypalPayment(args, context);
    expect(post).toHaveBeenCalledWith(
      initiatePaypalPaymentEndPoint,
      initiatePaypalPayment,
      args.createPaymentCriteria,
      context
    );
  });

  it('should handle errors gracefully when initiatePaypalPayment throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(initiatePaypalPayment(args, context)).rejects.toThrow('Test error');
  });
});
