import { get } from '../../../../../../src/apollo/client/rest-client';
import {
  getPaymentMethods,
  getCcuiPaymentMethods
} from '../../../../../../src/apollo/subgraphs/payment-methods-entity-service/services/payment-methods-service';
import { endpoints } from '../../../../../../src/apollo/subgraphs/payment-methods-entity-service/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

const context = {};

describe('getPaymentMethods', () => {
  it('should return all the payment methods', async () => {
    const args = {
      basketReference: 'AKU-a15b4934-6882-4f48-b31e-71d95b916ce0',
      language: 'en',
      country: 'gb',
      userType: 'LEISURE',
      clientChannel: 'PI'
    };

    const request = {
      paymentMethodsCriteria: args
    };
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);
    const result = await getPaymentMethods(request, context);

    expect(result).toEqual(mockResponse);
    expect(get).toHaveBeenCalledWith(endpoints.PAYMENT_METHODS, getPaymentMethods, args, context);
  });
});

describe('getCcuiPaymentMethods', () => {
  it('should return all the CCUI payment methods', async () => {
    const args = {
      basketReference: 'AKU-a15b4934-6882-4f48-b31e-71d95b916ce0',
      language: 'en',
      country: 'gb',
      userId: 'user1',
      userType: 'LEISURE',
      changePaymentBIC: true
    };

    const request = {
      paymentCcuiMethodsCriteria: args
    };
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);
    const result = await getCcuiPaymentMethods(request, context);

    expect(result).toEqual(mockResponse);
    expect(get).toHaveBeenCalledWith(
      endpoints.CCUI_PAYMENT_METHODS,
      getCcuiPaymentMethods,
      args,
      context
    );
  });
});

const mockResponse = {
  paymentMethods: [
    {
      name: 'CARD',
      type: 'NEW_CARD',
      subType: null,
      order: 1,
      logoSrc: null,
      acceptedCardTypes: [
        {
          type: 'MC',
          name: 'Mastercard Credit',
          logoSrc: '/content/dam/global/booking/Mastercard.jpg'
        },
        {
          type: 'AX',
          name: 'American Express',
          logoSrc: '/content/dam/global/booking/AX.jpg'
        }
      ],
      card: null,
      paymentOptions: [
        {
          type: 'PAY_NOW',
          order: 1,
          enabled: true
        },
        {
          type: 'PAY_ON_ARRIVAL',
          order: 2,
          enabled: true
        }
      ],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
      bookingAllowances: null,
      clientId: null,
      clientToken: null
    },
    {
      name: 'PIBA',
      type: 'NEW_PIBA',
      subType: 'PIBAGB',
      order: 2,
      logoSrc: null,
      acceptedCardTypes: [
        {
          type: 'PI',
          name: 'Business Account',
          logoSrc: '/content/dam/global/booking/Business_Account.jpg'
        }
      ],
      card: null,
      paymentOptions: [
        {
          type: 'PAY_NOW',
          order: 1,
          enabled: false
        },
        {
          type: 'PAY_ON_ARRIVAL',
          order: 2,
          enabled: true
        }
      ],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: true,
      reasons: [],
      bookingAllowances: null,
      clientId: null,
      clientToken: null
    },
    {
      name: 'PAYPAL',
      type: 'PAYPAL',
      subType: null,
      order: 3,
      logoSrc: null,
      acceptedCardTypes: [
        {
          type: 'PP',
          name: 'Paypal',
          logoSrc: '/content/dam/global/booking/paypal-logo-2.png'
        }
      ],
      card: null,
      paymentOptions: [
        {
          type: 'PAY_NOW',
          order: 1,
          enabled: true
        },
        {
          type: 'PAY_ON_ARRIVAL',
          order: 2,
          enabled: false
        }
      ],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
      bookingAllowances: null,
      clientId: 'Af5tWcftry239W03bvHOPcJndrOT13w5rAcgGRcvSemKywxIzYkUSQytHFeIPQJJ3e4aTccLC74X-qSF',
      clientToken: 'eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJleUowZVhBaU9='
    },
    {
      name: 'APPLE',
      type: 'AP',
      subType: null,
      order: 4,
      logoSrc: '/content/dam/global/booking/ApplePay.jpg',
      acceptedCardTypes: [
        {
          type: 'MC',
          name: 'Mastercard Credit',
          logoSrc: '/content/dam/global/booking/Mastercard.jpg'
        },
        {
          type: 'AX',
          name: 'American Express',
          logoSrc: '/content/dam/global/booking/AX.jpg'
        }
      ],
      card: null,
      paymentOptions: [
        {
          type: 'PAY_NOW',
          order: 1,
          enabled: true
        },
        {
          type: 'PAY_ON_ARRIVAL',
          order: 2,
          enabled: false
        }
      ],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
      bookingAllowances: null,
      clientId: null,
      clientToken: null
    },
    {
      name: 'GOOGLE',
      type: 'GP',
      subType: null,
      order: 5,
      logoSrc: '/content/dam/global/booking/GooglePay.png',
      acceptedCardTypes: [
        {
          type: 'MC',
          name: 'Mastercard Credit',
          logoSrc: '/content/dam/global/booking/Mastercard.jpg'
        },
        {
          type: 'AX',
          name: 'American Express',
          logoSrc: '/content/dam/global/booking/AX.jpg'
        }
      ],
      card: null,
      paymentOptions: [
        {
          type: 'PAY_NOW',
          order: 1,
          enabled: true
        },
        {
          type: 'PAY_ON_ARRIVAL',
          order: 2,
          enabled: false
        }
      ],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
      bookingAllowances: null,
      clientId: null,
      clientToken: null
    }
  ]
};
