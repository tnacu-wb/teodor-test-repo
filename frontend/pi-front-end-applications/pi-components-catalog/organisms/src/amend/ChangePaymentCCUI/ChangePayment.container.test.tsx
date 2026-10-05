import { BASKET_STATUS, EckohStatus } from '@whitbread-eos/api';
import { LoadingSpinner } from '@whitbread-eos/atoms';
import React from 'react';

import { fireEvent, render, waitFor, userEvent } from '../../utils/test-utils';
import { mockedPackagesData } from '../AmendPaymentCCUI/mockedPackagesData';
import { mockedPaymentMethodCCUI } from '../AmendPaymentCCUI/mockedPaymentMethodCCUI';
import { mockedRatesInformation } from '../AmendPaymentCCUI/mockedRatesInformation';
import ChangePaymentContainer from './ChangePayment.container';
import { mockedBookingConfirmationData } from './mockedBookingConfirmationData.ts';
import { mockedHotelInformationData } from './mockedHotelInformationData';

const mockUseScreenSize = jest.fn();
const mockedProps = {
  summaryOfPayments: {
    charitable: 0,
    previousTotal: 73,
    balancePaid: 0,
    payOnArrival: 73,
    refund: 0,
    nonRefundable: 0,
    totalCost: 73,
    balanceAuthorised: 0,
    navigationOptions: {
      amendPaymentPage: false,
    },
    paymentOptions: {
      payNow: false,
      payOnArrival: false,
    },
    paymentCardDetails: {
      cardNumberMasked: '',
      token: '',
      expirationDate: '',
      cardType: '',
      cardHolderName: '',
      cardNumberLast4Digits: '',
      cardLogoSrc: '',
      cardName: '',
    },
  },
  paymentOptionsData: {
    paymentOptions: {
      discount: '0',
      paymentOption: {
        payNow: false,
        payOnArrival: true,
      },
      paymentType: 'RESERVE_WITHOUT_CARD',
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
        display: false,
        values: null,
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
        name: null,
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
  user: {
    'https://ccui.opera.whitbread.digital/role': ['ROLE-G-SC-CCUI-Manager'],
    wb_account_locale: 'en',
    nickname: 'automatic_manager',
    name: 'automatic_tests_manager@fake.domain.fake',
    picture:
      'https://s.gravatar.com/avatar/5129b93fd54aeb14981cb1932e5608aa?s=480&r=pg&d=https%3A%2F%2Fcdn.auth0.com%2Favatars%2Fau.png',
    updated_at: '2024-08-27T07:06:32.690Z',
    email: 'automatic_tests_manager@fake.domain.fake',
    email_verified: true,
    sub: 'auth0|63514d8d0852af256b52fe8c',
    sid: 'ifaJw0iI7iPoz0yoa1p_H55dCqrRUCyM',
  },
  basketReference: 'GAA-7f77f2ba-26e0-483b-8e30-1897f0c19d9d',
  hiData: mockedHotelInformationData,
  bcData: mockedBookingConfirmationData,
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

const getPaymentStatus = {
  isLoading: false,
  isError: false,
  error: {
    message: 'error booking information',
  },
  data: { basket: { status: 'OPEN' } },
};

const mockEckohStatusData = {
  data: { eckohRecordingStatus: { status: EckohStatus.SUCCESS } },
  isLoading: false,
  isError: false,
  isSuccess: true,
};

const mockedTranslations = {
  'ccui.accountToCompany.search': 'Search',
  'booking.summary.back': 'Back',
  'ccui.search.companyName.placeholder': 'Company name',
  'ccui.paymentOption.payNow': 'Pay now',
  'ccui.paymentOption.payOnArrival': 'Pay on arrival',
  'ccui.accountToCompany.accountNumber.placeholder': 'A/c to company number',
  'ccui.accountToCompany.description':
    'Enter at least one of these fields to search for full company details',
  'ccui.accountToCompany.title': 'Account to company details',
};
const companyName1 = 'Premier Aluminium Systems';
const companyName2 = 'Premier Business Audio';
const mockQueryRequestResponse = {
  isError: false,
  isFetching: false,
  error: { message: '' },
  data: {
    searchCompanies: {
      companies: [
        {
          name: companyName1,
          address: {
            addressLine1: '4th floor',
            addressLine2: '120 Holborn',
            addressLine3: 'London',
            country: 'UK',
            postalCode: 'EC1N 2TD',
          },
          telephoneNumber: '020 7806 5480',
          corpId: 'XDJ233DNG',
          companyId: '2569623',
          active: true,
          arNumber: '341343',
          profileType: 'Business',
          restricted: false,
          language: 'EN',
        },
        {
          name: companyName2,
          address: {
            addressLine1: '4th floor',
            addressLine2: '120 Holborn',
            addressLine3: 'London',
            country: 'UK',
            postalCode: 'EC1N 2TD',
          },
          telephoneNumber: '020 7806 5480',
          corpId: 'XDJ233DNG',
          companyId: '2569624',
          arNumber: '341345',
          active: true,
          profileType: 'Business',
          restricted: true,
          restrictedReason: 'Not cleared',
          language: 'EN',
        },
      ],
    },
  },
  refetch: jest.fn(),
};
const mockCustomLocale = jest.fn();
const mockUseLocalStorage = jest.fn();
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockCustomLocale(),
  useLocalStorage: () => mockUseLocalStorage(),
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
      case 'GetEckohStatus':
        return mockEckohStatusData;
      case 'searchA2CPaymentCompanies':
        return mockQueryRequestResponse;
      case 'GetPaymentStatus':
        return getPaymentStatus;
      default:
        return {};
    }
  }),
}));

const mockBack = jest.fn();
const mockPush = jest.fn();
const mockLocalStorageGetItem = jest.fn();
const mockLocalStorageSetItem = jest.fn();
const mockBeforePopState = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    query: {},
    back: mockBack,
    push: mockPush,
    beforePopState: mockBeforePopState,
    asPath: '/current-path',
  }),
}));

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: (key: string) => {
      return mockedTranslations[key] || 'default';
    },
  }),
}));

describe('Change Payment Container', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUseLocalStorage.mockReturnValue(['', jest.fn()]);

    mockUseScreenSize.mockReturnValue({ isLessThanLg: false });
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });

    global.sessionStorage = {
      getItem(): string | null {
        return null;
      },
      key(): string | null {
        return null;
      },
      length: 0,
      removeItem: () => jest.fn(),
      setItem: () => jest.fn(),
      clear: () => jest.fn(),
    };

    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: (...args: string[]) => mockLocalStorageGetItem(...args),
        setItem: (...args: string[]) => mockLocalStorageSetItem(...args),
      },
    });
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

  it('should render Change Payment Container', () => {
    const { getByTestId } = render(<ChangePaymentContainer {...mockedProps} />);

    expect(getByTestId('ChangePaymentContainer')).toBeInTheDocument();
    expect(getByTestId('CardDetails-Container')).toBeInTheDocument();
    expect(getByTestId('PaymentType-Container')).toBeInTheDocument();
  });

  it('should render Pay now and POA selected as default', () => {
    const { getByTestId, container } = render(<ChangePaymentContainer {...mockedProps} />);

    const optionPN = getByTestId('radio-box-wrapper_PAY_NOW');
    const optionPOA = getByTestId('radio-box-wrapper_PAY_ON_ARRIVAL');

    expect(optionPN).toBeInTheDocument();
    expect(optionPOA).toBeInTheDocument();

    const radioOptionPN = container.querySelector(
      'div[data-testid="radio-box-wrapper_PAY_NOW"] input'
    );
    const radioOptionPOA = container.querySelector(
      'div[data-testid="radio-box-wrapper_PAY_ON_ARRIVAL"] input'
    );
    expect(radioOptionPN).not.toBeChecked();
    expect(radioOptionPOA).toBeChecked();
  });

  it('should render Card Status Section after selecting CC payment type ', async () => {
    const { getByTestId } = render(<ChangePaymentContainer {...mockedProps} />);

    const optionCC = getByTestId('radio-box-wrapper_payment-type-radio-0');
    expect(optionCC).toBeInTheDocument();

    userEvent.click(optionCC);
    waitFor(() => {
      expect(getByTestId('cardStatusSection')).toBeInTheDocument();
    });
  });

  it('should render Total Cost section', () => {
    const { getByTestId } = render(<ChangePaymentContainer {...mockedProps} />);
    expect(getByTestId('totalCostSection_total-cost')).toBeInTheDocument();
  });

  it('should call back function on back button', async () => {
    const { getByText } = render(<ChangePaymentContainer {...mockedProps} />);

    const backBtn = getByText('Back');
    expect(backBtn).toBeInTheDocument();

    userEvent.click(backBtn);

    mockLocalStorageGetItem.mockReturnValue(true);
    expect(mockBeforePopState).toHaveBeenCalled();

    expect(mockBack).toHaveBeenCalledTimes(1);
  });

  it('should call setIsBackFlag=true on back', () => {
    const setIsBackFlag = jest.fn();
    mockUseLocalStorage.mockReturnValue(['', setIsBackFlag]);
    render(<ChangePaymentContainer {...mockedProps} />);

    const beforePopStateCallback = mockBeforePopState.mock.calls[0][0];
    beforePopStateCallback({ as: '/new-path' });
    expect(setIsBackFlag).toHaveBeenCalledWith(true);
  });

  it('should render card status section with card present, cnp options and eckoh button  ', async () => {
    mockedPaymentMethodCCUI.paymentCcuiMethods = [
      {
        cnpOptionAvailable: false,
        cnpPreSelected: false,
        enabled: true,
        name: 'CARD',
        order: 1,
        reasons: [],
        subType: null,
        type: 'NEW_CARD',
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
      },
    ];
    const { getByTestId } = render(<ChangePaymentContainer {...mockedProps} />);
    const radioButtonCP = getByTestId('radio-box-wrapper_Card present');
    const radioButtonCNP = getByTestId('radio-box-wrapper_CNP (Card not present)');

    expect(radioButtonCP).toBeInTheDocument();
    expect(radioButtonCNP).toBeInTheDocument();

    const launchButton = getByTestId('launchEckoh_launchButton');
    expect(launchButton).toBeInTheDocument();
    fireEvent.click(launchButton);
    const firstModal = getByTestId('launchEckoh-ModalContent');

    await waitFor(() => {
      expect(firstModal).toBeVisible();
    });
  });

  it('should render page with A2C checked ', () => {
    mockedPaymentMethodCCUI.paymentCcuiMethods = [
      {
        cnpOptionAvailable: false,
        cnpPreSelected: false,
        enabled: true,
        name: 'Account to company',
        order: 4,
        reasons: [],
        subType: null,
        type: 'ACCOUNT_COMPANY',
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
        acceptedCardTypes: null,
      },
    ];
    const { getByTestId } = render(<ChangePaymentContainer {...mockedProps} />);
    expect(getByTestId('accountToCompanyContainer')).toBeInTheDocument();
  });

  it('should have the confirm booking button enabled when changing in A2C company payment type', async () => {
    mockedPaymentMethodCCUI.paymentCcuiMethods = [
      {
        cnpOptionAvailable: false,
        cnpPreSelected: false,
        enabled: true,
        name: 'Account to company',
        order: 4,
        reasons: [],
        subType: null,
        type: 'ACCOUNT_COMPANY',
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
        acceptedCardTypes: null,
      },
    ];
    const { getByTestId, container, getAllByRole } = render(
      <ChangePaymentContainer {...mockedProps} />
    );
    expect(getByTestId('accountToCompanyContainer')).toBeInTheDocument();

    const optionPOA = getByTestId('radio-box-wrapper_PAY_ON_ARRIVAL');

    expect(optionPOA).toBeInTheDocument();

    const radioOptionPOA = container.querySelector(
      'div[data-testid="radio-box-wrapper_PAY_ON_ARRIVAL"] input'
    );
    expect(radioOptionPOA).toBeChecked();

    const accountNumberField = getByTestId('input-accountNumber');
    const accountNumberValidInput = '167003';
    await userEvent.type(accountNumberField, accountNumberValidInput, { delay: 0.1 });

    const searchButton = getByTestId('AccountToCompanyDetails-Search');
    userEvent.click(searchButton);

    await waitFor(() => {
      expect(getByTestId('CompanySelection-ModalContent')).toBeInTheDocument();
      const firstCompany = getAllByRole('row')[1];
      userEvent.click(firstCompany);
    });

    await waitFor(() => {
      const verifyCta = getByTestId('CompanySelection-ModalVerifyButton');
      expect(verifyCta).toBeInTheDocument();
      userEvent.click(verifyCta);
    });

    const checkBox = getByTestId('roomRatePolicies_checkbox');
    expect(checkBox).toBeInTheDocument();
    const confirmButton = getByTestId('totalCostSection_confirm-booking-total-cost');
    fireEvent.click(checkBox);

    await waitFor(() => {
      expect(getByTestId('roomRatePolicies-ModalContent')).toBeVisible();
      expect(confirmButton).toBeEnabled();
    });
    fireEvent.click(confirmButton);
  });

  it('should call back action when mutation is successful', async () => {
    mockedPaymentMethodCCUI.paymentCcuiMethods = [
      {
        cnpOptionAvailable: false,
        cnpPreSelected: false,
        enabled: true,
        name: 'Account to company',
        order: 4,
        reasons: [],
        subType: null,
        type: 'ACCOUNT_COMPANY',
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
        acceptedCardTypes: null,
      },
    ];
    mockMutationResponse.isSuccess = true;
    getPaymentStatus.data.basket.status = BASKET_STATUS.COMPLETED;
    render(<ChangePaymentContainer {...mockedProps} />);
    expect(mockBack).toHaveBeenCalledTimes(1);
  });

  it('should render Payment page with Initiate payment process error and redirect to payments-errors page', async () => {
    mockMutationResponse.error = 'Error initiate payment process';
    mockMutationResponse.isSuccess = false;
    mockMutationResponse.isError = true;
    mockMutationResponse.isLoading = false;
    getPaymentStatus.data.basket.status = BASKET_STATUS.FAILED;
    const confirmationStarted = true;
    jest.spyOn(React, 'useState').mockImplementation(() => [confirmationStarted, jest.fn()]);
    render(<ChangePaymentContainer {...mockedProps} />);

    await waitFor(() => {
      expect(mockPush).toHaveBeenCalledWith(
        '/gb/en/payment-errors?reservationId=GAA-7f77f2ba-26e0-483b-8e30-1897f0c19d9d'
      );
    });
  });
});
