import PaymentTypeContainer from '.';
import '@testing-library/jest-dom';
import { PaymentMethod } from '@whitbread-eos/api';
import React from 'react';

import { render } from '../../utils/test-utils';

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
            enabled: false,
          },
        ],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
      {
        name: 'CARD',
        type: 'SAVED_CARD',
        order: 2,
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
        reasons: ['EXPIRED'],
      },
      {
        name: 'New Credit / Debit card',
        type: 'NEW_CARD',
        order: 3,
        acceptedCardTypes: [
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
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
            enabled: false,
          },
        ],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
      {
        name: 'New Business Account card',
        type: 'NEW_PIBA',
        order: 4,
        acceptedCardTypes: [
          {
            name: 'Business Account',
            type: 'PI',
            logoSrc: '',
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
            enabled: false,
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

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQuery: () => mockResponse,
}));

const mockUseAuthToken = jest.fn(() => ({
  token: 'the-auth-token',
  isAuth0Enabled: false,
  isLoading: false,
}));
const mockUseQueryRequest = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useQueryRequest: (...args: unknown[]) => {
    mockUseQueryRequest(...args);
    return mockResponse;
  },
  useAuthToken: () => mockUseAuthToken(),
}));

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: (key: string) => {
      switch (key) {
        case 'cc.title':
          return 'Payment type';
        case 'cc.subTitle':
          return 'Payment will be handled by a secure third party.';
        default:
          return 'default';
      }
    },
  }),
}));

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const paymentTypeProps = {
  selectedPaymentDetail: { type: 'default', order: 0, enabled: true },
  onPaymentTypeClick: jest.fn(),
  selectedPaymentType: {} as PaymentMethod,
};

describe('PaymentType', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
      query: { reservationId: 'DLONEU2363516' },
    });
  });

  beforeEach(() => {
    mockUseQueryRequest.mockClear();
    mockUseAuthToken.mockReturnValue({
      token: 'the-auth-token',
      isAuth0Enabled: false,
      isLoading: false,
    });
  });

  it('should fetch payment methods with the resolved auth token once loaded', () => {
    render(<PaymentTypeContainer {...paymentTypeProps} />);

    const [, , , reactQueryOptions, accessToken] = mockUseQueryRequest.mock.calls[0];
    expect(reactQueryOptions).toMatchObject({ enabled: true });
    expect(accessToken).toBe('the-auth-token');
  });

  it('should NOT enable the payment methods query while the auth token is still loading', () => {
    mockUseAuthToken.mockReturnValue({
      token: undefined,
      isAuth0Enabled: true,
      isLoading: true,
    });

    render(<PaymentTypeContainer {...paymentTypeProps} />);

    const [, , , reactQueryOptions, accessToken] = mockUseQueryRequest.mock.calls[0];
    expect(reactQueryOptions).toMatchObject({ enabled: false });
    expect(accessToken).toBeUndefined();
  });

  it('should render a PaymentType with correct text, 1 reason and 4 radio buttons', function () {
    const { getAllByRole, getByText } = render(<PaymentTypeContainer {...paymentTypeProps} />);
    expect(getByText('Payment type')).toBeInTheDocument();
    expect(getByText('Payment will be handled by a secure third party.')).toBeInTheDocument();
    expect(getAllByRole('radio')).toHaveLength(4);
  });

  it('should render null if there are no payment methods', function () {
    // @ts-expect-error Type 'null' is not assignable to type '{ paymentMethods: ({ name: string; type: string; order: number; card: { token: string; expiryMonth: string; expiryYear: string; type: string; logoSrc: string; cardHolderName: string; cardType: string; cnpRequired: boolean; }; ... 5 more ...; acceptedCardTypes?: undefined; } | { ...; })[]; }'.
    mockResponse.data = null;
    const { queryAllByRole, queryByText } = render(<PaymentTypeContainer {...paymentTypeProps} />);
    expect(queryByText('Payment type')).toBeNull();
    expect(queryByText('Payment will be handled by a secure third party.')).toBeNull();
    expect(queryAllByRole('radio')).toHaveLength(0);
  });
});
