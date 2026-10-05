import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import PaymentTypeContainer from './PaymentType.container';

const mockOnPaymentTypeClick = jest.fn();
const mockUseRouter = jest.fn();

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));

const props = {
  onPaymentTypeClick: mockOnPaymentTypeClick,
  selectedPaymentDetail: { type: 'default', order: 0, enabled: true },
  selectedPaymentType: {
    name: '',
    type: '',
    subType: '',
    order: 0,
    paymentOptions: [{ type: '', order: 0, enabled: true }],
    enabled: true,
    cnpPreSelected: true,
    cnpOptionAvailable: true,
    reasons: [],
  },
};

interface Data {
  isLoading: boolean;
  isError: boolean;
  isSuccess: boolean;
  data?: unknown;
}

const mockResponse: Data = {
  isLoading: false,
  isError: false,
  isSuccess: true,
};

const mockData = {
  paymentCcuiMethods: [
    {
      acceptedCardTypes: [
        {
          logoSrc: '/content/dam/global/booking/Mastercard.jpg',
          name: 'Mastercard Credit',
          type: 'MC',
        },
        {
          logoSrc: '/content/dam/global/booking/AX.jpg',
          name: 'American Express',
          type: 'AX',
        },
        {
          logoSrc: '/content/dam/global/booking/dinersclub.jpg',
          name: 'Diners Club',
          type: 'DN',
        },
        {
          logoSrc: '/content/dam/global/booking/Visa_Debit.jpg',
          name: 'Visa Debit',
          type: 'VS',
        },
        {
          logoSrc: '/content/dam/global/booking/Electron_white_v.jpg',
          name: 'Electron',
          type: 'VS',
        },
        {
          logoSrc: '/content/dam/global/booking/maestro.jpg',
          name: 'Maestro',
          type: 'MA',
        },
        {
          logoSrc: '/content/dam/global/booking/MD.jpg',
          name: 'Mastercard Debit',
          type: 'MC',
        },
        {
          logoSrc: '/content/dam/global/booking/VC.jpg',
          name: 'Visa Credit',
          type: 'VS',
        },
      ],
      cnpOptionAvailable: false,
      cnpPreSelected: false,
      enabled: true,
      name: 'CARD',
      order: 1,
      paymentOptions: [
        {
          enabled: false,
          order: 1,
          type: 'PAY_NOW',
        },
        {
          enabled: true,
          order: 2,
          type: 'PAY_ON_ARRIVAL',
        },
      ],
      reasons: [],
      type: 'NEW_CARD',
    },
    {
      acceptedCardTypes: [
        {
          logoSrc: '/content/dam/global/booking/Business_Account.jpg',
          name: 'Business Account',
          type: 'PI',
        },
      ],
      cnpOptionAvailable: true,
      cnpPreSelected: false,
      enabled: true,
      name: 'PIBA UK',
      order: 2,
      paymentOptions: [
        {
          enabled: false,
          order: 1,
          type: 'PAY_NOW',
        },
        {
          enabled: true,
          order: 2,
          type: 'PAY_ON_ARRIVAL',
        },
      ],
      reasons: [],
      type: 'NEW_PIBA',
    },
    {
      acceptedCardTypes: [
        {
          logoSrc: '/content/dam/global/booking/Business_Account.jpg',
          name: 'Business Account',
          type: 'PI',
        },
      ],
      cnpOptionAvailable: true,
      cnpPreSelected: false,
      enabled: true,
      name: 'PIBA EU',
      order: 3,
      paymentOptions: [
        {
          enabled: false,
          order: 1,
          type: 'PAY_NOW',
        },
        {
          enabled: true,
          order: 2,
          type: 'PAY_ON_ARRIVAL',
        },
      ],
      reasons: [],
      type: 'NEW_PIBA',
    },
    {
      acceptedCardTypes: null,
      cnpOptionAvailable: false,
      cnpPreSelected: false,
      enabled: true,
      name: 'Account to company',
      order: 4,
      paymentOptions: [
        {
          enabled: false,
          order: 1,
          type: 'PAY_NOW',
        },
        {
          enabled: true,
          order: 2,
          type: 'PAY_ON_ARRIVAL',
        },
      ],
      reasons: [],
      type: 'ACCOUNT_COMPANY',
    },
    {
      acceptedCardTypes: null,
      cnpOptionAvailable: false,
      cnpPreSelected: false,
      enabled: true,
      name: 'Non-guaranteed booking',
      order: 5,
      paymentOptions: [
        {
          enabled: false,
          order: 1,
          type: 'PAY_NOW',
        },
        {
          enabled: true,
          order: 2,
          type: 'PAY_ON_ARRIVAL',
        },
      ],
      reasons: [],
      type: 'NON_GUARANTEED',
    },
  ],
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => mockResponse,
}));

describe('<PaymentTypeContainer />', () => {
  it('should render the container with no data', () => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
      query: { reservationId: 'DUNCRO6988257', role: 'manager' },
    });

    const { getByTestId } = render(<PaymentTypeContainer {...props} />);
    expect(getByTestId('paymentTypeContainer_noData')).toBeInTheDocument();
  });
  it('should render the container with data', () => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
      query: { reservationId: 'DUNCRO6988257', role: 'manager' },
    });

    mockResponse.data = mockData;

    const { getByTestId } = render(<PaymentTypeContainer {...props} />);
    expect(getByTestId('paymentTypeContainer_withData')).toBeInTheDocument();
  });

  it('should render the container for DE', () => {
    mockUseRouter.mockReturnValue({
      locale: 'de',
      query: { reservationId: 'DUNCRO6988257', role: 'manager' },
    });

    const { getByTestId } = render(<PaymentTypeContainer {...props} />);
    expect(getByTestId('paymentTypeContainer_withData')).toBeInTheDocument();
  });

  it('should render the PaymentType component', () => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
      query: { reservationId: 'DUNCRO6988257', role: 'manager' },
    });

    const { queryByText } = render(<PaymentTypeContainer {...props} />);
    expect(queryByText('cc.title')).toBeInTheDocument();
  });
});
