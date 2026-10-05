import { BASKET_STATUS } from '@whitbread-eos/api';
import { LoadingSpinner } from '@whitbread-eos/atoms';
import React from 'react';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import AmendPaymentContainer from './AmendPayment.container';
import { mockedPackagesData } from './mockedPackagesData';
import { mockedPaymentMethodCCUI } from './mockedPaymentMethodCCUI';
import { mockedRatesInformation } from './mockedRatesInformation';

const mockUseScreenSize = jest.fn();
const mockedProps = {
  summaryOfPayments: {
    charitable: 0,
    previousTotal: 75,
    balancePaid: 0,
    payOnArrival: 75,
    refund: 0,
    nonRefundable: 0,
    totalCost: 75,
    balanceAuthorised: 0,
    navigationOptions: {
      amendPaymentPage: false,
    },
    paymentOptions: {
      payNow: false,
      payOnArrival: false,
    },
    paymentCardDetails: {
      cardNumberMasked: null,
      token: null,
      expirationDate: null,
      cardType: null,
      cardHolderName: null,
      cardNumberLast4Digits: null,
      cardLogoSrc: null,
      cardName: null,
    },
  },
  bookingReference: 'GAA4509993',
  originalBasketReference: 'GAA-7f77f2ba-26e0-483b-8e30-1897f0c19d9d',
  temporaryBasketReference: 'AA-205efcc4-a4c3-4173-925b-5e499e2a881b',
  paymentOptionsData: {
    paymentOptions: {
      discount: '0',
      paymentOption: {
        payNow: false,
        payOnArrival: true,
      },
      paymentType: 'ACCOUNT_COMPANY',
      cardPresent: {
        display: false,
        value: false,
      },
      eckoh: {
        display: false,
        enabled: true,
      },
      cardHolderName: {
        display: false,
        name: 'abc',
        surname: 'cde',
      },
      billingAddress: {
        display: true,
      },
      emailPreference: {
        display: true,
        send: false,
        emailAddress: 'abc@as.ro',
      },
      allowances: {
        display: true,
        values: ['ultimate wifi', 'meal deal'],
      },
      purchaseOrder: {
        display: false,
        value: null,
      },
      companyRef: {
        display: false,
        value: null,
      },
      a2cDetails: {
        display: true,
        number: '',
        name: 'Test name',
        address: null,
        postcode: null,
      },
      preAuthCharges: {
        display: false,
        charges: [],
      },
      hotelName: 'Frankfurt Messe',
      hotelCode: 'FRAMTI',
      brand: 'PID',
      companyId: null,
    },
  },
};
const mockMutationResponse = {
  mutation: {
    mutate: jest.fn(),
    mutateAsync: jest.fn().mockResolvedValue({}),
  },
  isSuccess: false,
  isError: false,
  isLoading: false,
  error: '',
};

const mockUsePollBasketStatus = {
  pollingInProgress: true,
  basketStatus: BASKET_STATUS.NOT_REQUIRED,
  dynamicSpinnerLabel: [],
  retryPayment: true,
};

const mockCustomLocale = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockCustomLocale(),
  usePackages: () => ({
    ...mockedPackagesData?.packages?.packages,
    ...mockedPackagesData,
  }),
  useMutationRequest: () => mockMutationResponse,

  useQueryRequest: jest.fn().mockImplementation((queryKey: string | any[]) => {
    let queryKeyValue = queryKey;
    if (Array.isArray(queryKey)) {
      queryKeyValue = queryKey[0];
    }
    switch (queryKeyValue) {
      case 'GetPackages':
        return mockedPackagesData;
      case 'ratesInformation':
        return {
          data: { ...mockedRatesInformation },
        };
      case 'getPaymentMethodsCCUI':
        return {
          data: { ...mockedPaymentMethodCCUI },
        };
      default:
        return {};
    }
  }),
}));

const mockRouter = {
  push: jest.fn(),
  query: {},
  back: jest.fn(),
};

const mockUseRouter = jest.fn();
const mockClick = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    query: {},
    back: mockClick,
    push: jest.fn(),
  }),
}));

describe('Amend Payment Container', () => {
  beforeEach(() => {
    jest.clearAllMocks();

    mockUseScreenSize.mockReturnValue({ isLessThanLg: false });
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'en',
    });
    mockUseRouter.mockReturnValue(mockRouter);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  const loadingSpinnerStyle = {
    margin: {
      sm: 'lg',
    },
  };
  it('renders LoadingSpinner with the specified style and ignores other props', () => {
    const loadingSpinner = render(
      <LoadingSpinner loadingText="booking.loading" wrapperStyle={loadingSpinnerStyle} />
    ).getByTestId('loading-spinner');

    expect(loadingSpinner).toBeInTheDocument();
    expect(loadingSpinner).toHaveStyle({
      margin: 'lg',
    });

    expect(loadingSpinner).toHaveTextContent('booking.loading');
  });

  it('should render Amend Payment Container', () => {
    const { getByTestId } = render(<AmendPaymentContainer {...mockedProps} />);

    expect(getByTestId('A2cPaymentContainer')).toBeInTheDocument();
    expect(getByTestId('CardDetails-Container')).toBeInTheDocument();
    expect(getByTestId('PaymentType-Container')).toBeInTheDocument();
  });

  it('should Pay on Arrival checkbox be enabled', () => {
    const { getByTestId } = render(<AmendPaymentContainer {...mockedProps} />);

    const optionPN = getByTestId('radio-box-wrapper_PAY_NOW');
    const optionPOA = getByTestId('radio-box-wrapper_PAY_ON_ARRIVAL');

    expect(optionPN).toBeInTheDocument();
    expect(optionPOA).toBeInTheDocument();
  });

  it('should render Total Cost section', () => {
    const { getByTestId } = render(<AmendPaymentContainer {...mockedProps} />);
    expect(getByTestId('totalCostSection_total-cost')).toBeInTheDocument();
  });
  it('should display loading spinner until basket is updated ', async () => {
    mockMutationResponse.isSuccess = false;
    mockMutationResponse.isLoading = true;

    const { getByTestId } = render(<AmendPaymentContainer {...mockedProps} />);

    await waitFor(() => expect(getByTestId('loading-spinner')).toBeInTheDocument());
  });

  it('should display AccountToCompanyPreAuthorisedCharges component ', () => {
    mockMutationResponse.isLoading = false;
    const { getByTestId } = render(<AmendPaymentContainer {...mockedProps} />);

    expect(getByTestId('AccountToCompanyPreAuthorisedCharges')).toBeInTheDocument();
  });

  it('should display AmendPaymentA2CDetails component ', () => {
    const { getByTestId } = render(<AmendPaymentContainer {...mockedProps} />);

    expect(getByTestId('amend-payment-a2c_title')).toBeInTheDocument();
  });

  it('should call back function on back button', () => {
    const { getByTestId } = render(<AmendPaymentContainer {...mockedProps} />);

    const backBtn = getByTestId('backToPage');
    expect(backBtn).toBeInTheDocument();
    fireEvent.click(backBtn);
  });

  it('should have the confirm booking button enabled', async () => {
    const { getByTestId } = render(<AmendPaymentContainer {...mockedProps} />);
    const checkBox = getByTestId('roomRatePolicies_checkbox');
    const confirmButton = getByTestId('totalCostSection_confirm-booking-total-cost');

    fireEvent.click(checkBox);

    await waitFor(() => {
      expect(getByTestId('roomRatePolicies-ModalContent')).toBeVisible();
      expect(confirmButton).toBeEnabled();
    });

    fireEvent.click(confirmButton);

    mockMutationResponse.isSuccess = true;
    mockUsePollBasketStatus.basketStatus = BASKET_STATUS.AMENDED;
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    waitFor(() => {
      expect(mockRouter.push).toHaveBeenCalledWith('/gb/en/amend/booking-confirmation');
    });
  });
});
