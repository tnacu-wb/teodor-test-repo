import '@testing-library/jest-dom';
import { Area, UserType, PAYPAL_PAYMENT, GOOGLE, APPLE } from '@whitbread-eos/api';
import React from 'react';

import { render } from '../../utils/test-utils';
import PaymentDetailsTabs from './PaymentDetailsTabs.component';

const mockedProps = {
  handlePaymentTypeSection: jest.fn(),
  selectedPaymentType: {
    name: 'PIBA',
    type: 'SAVED_CARD',
    order: 1,
    card: {
      token: '5667855671183870034',
      expiryMonth: '12',
      expiryYear: '23',
      type: 'MD',
      logoSrc: '',
      cardHolderName: 'Monica W',
      cardType: 'LEISURE_STORED_CARD',
      cnpRequired: false,
    },
    paymentOptions: [
      {
        type: 'PAY_NOW',
        order: 1,
        enabled: true,
      },
      {
        type: 'PAY_ON_ARRIVAL',
        order: 2,
        enabled: false,
      },
    ],
    enabled: true,
    cnpPreSelected: false,
    cnpOptionAvailable: false,
    reasons: [],
  },
  setSelectedPaymentDetail: jest.fn(),
  t: (key: string) => {
    switch (key) {
      case 'paymentOptions.header':
        return 'When would you like to pay?';
      case 'paymentOptions.title':
        return "Please choose when you'd like to pay for your booking. You may be redirected to your bank provider to verify your details";
      case 'ccui.paymentOption.payNow':
        return 'Pay now';
      case 'ccui.paymentOption.payOnArrival':
        return 'Pay on arrival';
      default:
        return 'default';
    }
  },
  userType: UserType.Leisure,
  variant: Area.PI,
  selectedPaymentDetail: { type: 'default', order: 0, enabled: true },
};

const mockResponse = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    paymentMethods: [
      {
        name: 'PIBA',
        type: 'SAVED_CARD',
        order: 1,
        card: {
          token: '5667855671183870034',
          expiryMonth: '12',
          expiryYear: '23',
          type: 'MD',
          logoSrc: '',
          cardHolderName: 'Monica W',
          cardType: 'LEISURE_STORED_CARD',
          cnpRequired: false,
        },
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
          {
            type: 'PAY_ON_ARRIVAL',
            order: 2,
            enabled: true,
          },
        ],
        acceptedCardTypes: [
          {
            type: 'PI',
            name: 'Business Account',
            logoSrc:
              'https://secure2.premierinn.com/content/dam/global/booking/Business_Account.jpg',
          },
        ],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
      {
        name: 'New Credit / Debit card',
        type: 'NEW_CARD',
        order: 3,
        acceptedCardTypes: [
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: 'https://secure2.premierinn.com/content/dam/global/booking/Mastercard.jpg',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: 'https://secure2.premierinn.com/content/dam/global/booking/AX.jpg',
          },
        ],
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
          {
            type: 'PAY_ON_ARRIVAL',
            order: 2,
            enabled: true,
          },
        ],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
    ],
  },
};

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQuery: () => mockResponse,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useQueryRequest: () => mockResponse,
}));

describe('PaymentDetailsTabs', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue({
      query: { reservationId: 'DLONEU2363516' },
    });
  });

  it('should render PaymentDetailsTabs correctly', function () {
    const { getByText } = render(<PaymentDetailsTabs {...mockedProps} />);

    expect(getByText('When would you like to pay?')).toBeInTheDocument();
    expect(
      getByText(
        "Please choose when you'd like to pay for your booking. You may be redirected to your bank provider to verify your details"
      )
    ).toBeInTheDocument();
    expect(getByText('Pay now')).toBeInTheDocument();
    expect(getByText('Pay on arrival')).toBeInTheDocument();
  });

  it('should render card images in Tabs', function () {
    const { queryAllByTestId } = render(<PaymentDetailsTabs {...mockedProps} />);

    expect(queryAllByTestId('PaymentDetailsTabs-Image-PI').length).toBe(2);
    expect(queryAllByTestId('PaymentDetailsTabs-Image-MC').length).toBe(2);
    expect(queryAllByTestId('PaymentDetailsTabs-Image-VS').length).toBe(2);
  });

  describe('paymentTypesImages', () => {
    it('should render PayPal image when PayPal payment is enabled for PAY_NOW', function () {
      const mockResponseWithPayPal = {
        ...mockResponse,
        data: {
          paymentMethods: [
            {
              name: PAYPAL_PAYMENT,
              type: 'paypal',
              logoSrc: 'https://secure2.premierinn.com/content/dam/global/booking/paypal.jpg',
              paymentOptions: [
                { type: 'PAY_NOW', order: 1, enabled: true },
                { type: 'PAY_ON_ARRIVAL', order: 2, enabled: false },
              ],
              enabled: true,
              cnpPreSelected: false,
              cnpOptionAvailable: false,
              reasons: [],
            },
          ],
        },
      };

      jest.doMock('@whitbread-eos/utils', () => ({
        ...jest.requireActual('@whitbread-eos/utils'),
        formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
        useQueryRequest: () => mockResponseWithPayPal,
      }));

      const { queryAllByTestId } = render(<PaymentDetailsTabs {...mockedProps} />);
      expect(queryAllByTestId('PaymentDetailsTabs-Image-paypal').length).toBeGreaterThanOrEqual(0);
    });

    it('should render Google Pay image when Google payment is enabled for PAY_NOW', function () {
      const mockResponseWithGoogle = {
        ...mockResponse,
        data: {
          paymentMethods: [
            {
              name: GOOGLE,
              type: 'googlepay',
              logoSrc: 'https://secure2.premierinn.com/content/dam/global/booking/googlepay.jpg',
              paymentOptions: [
                { type: 'PAY_NOW', order: 1, enabled: true },
                { type: 'PAY_ON_ARRIVAL', order: 2, enabled: false },
              ],
              enabled: true,
              cnpPreSelected: false,
              cnpOptionAvailable: false,
              reasons: [],
            },
          ],
        },
      };

      jest.doMock('@whitbread-eos/utils', () => ({
        ...jest.requireActual('@whitbread-eos/utils'),
        formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
        useQueryRequest: () => mockResponseWithGoogle,
      }));

      const { queryAllByTestId } = render(<PaymentDetailsTabs {...mockedProps} />);
      expect(queryAllByTestId('PaymentDetailsTabs-Image-googlepay').length).toBeGreaterThanOrEqual(
        0
      );
    });

    it('should render Apple Pay image when Apple payment is enabled for PAY_NOW', function () {
      const mockResponseWithApple = {
        ...mockResponse,
        data: {
          paymentMethods: [
            {
              name: APPLE,
              type: 'applepay',
              logoSrc: 'https://secure2.premierinn.com/content/dam/global/booking/applepay.jpg',
              paymentOptions: [
                { type: 'PAY_NOW', order: 1, enabled: true },
                { type: 'PAY_ON_ARRIVAL', order: 2, enabled: false },
              ],
              enabled: true,
              cnpPreSelected: false,
              cnpOptionAvailable: false,
              reasons: [],
            },
          ],
        },
      };

      jest.doMock('@whitbread-eos/utils', () => ({
        ...jest.requireActual('@whitbread-eos/utils'),
        formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
        useQueryRequest: () => mockResponseWithApple,
      }));

      const { queryAllByTestId } = render(<PaymentDetailsTabs {...mockedProps} />);
      expect(queryAllByTestId('PaymentDetailsTabs-Image-applepay').length).toBeGreaterThanOrEqual(
        0
      );
    });

    it('should render card images only in payOnArrival when only PAY_ON_ARRIVAL is enabled', function () {
      const mockResponsePayOnArrival = {
        ...mockResponse,
        data: {
          paymentMethods: [
            {
              name: 'New Credit / Debit card',
              type: 'NEW_CARD',
              order: 3,
              acceptedCardTypes: [
                {
                  name: 'Visa',
                  type: 'VS',
                  logoSrc: 'https://secure2.premierinn.com/content/dam/global/booking/AX.jpg',
                },
              ],
              paymentOptions: [
                { type: 'PAY_NOW', order: 1, enabled: false },
                { type: 'PAY_ON_ARRIVAL', order: 2, enabled: true },
              ],
              enabled: true,
              cnpPreSelected: false,
              cnpOptionAvailable: false,
              reasons: [],
            },
          ],
        },
      };

      jest.doMock('@whitbread-eos/utils', () => ({
        ...jest.requireActual('@whitbread-eos/utils'),
        formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
        useQueryRequest: () => mockResponsePayOnArrival,
      }));

      const { queryAllByTestId } = render(<PaymentDetailsTabs {...mockedProps} />);
      expect(queryAllByTestId('PaymentDetailsTabs-Image-VS').length).toBeGreaterThanOrEqual(0);
    });
  });
});
