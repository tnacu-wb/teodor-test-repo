import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { Claims } from '@whitbread-eos/api';

import {
  mockedSummaryOfPaymentsData,
  mockedGetStaticContentData,
  mockedMealPackagesData,
  mockedRestaurantDataWithClosure,
  mockedRestaurantData,
  mockBookingConfirmationAuthenticatedResponse,
  mockedAmendPaymentData,
} from './mockTestsData';
import { PaymentPageAmend } from './page.ccui';

const mockedGetPackages = {
  privacyPolicy: mockedMealPackagesData.packages.privacyPolicy,
  restaurant: mockedRestaurantData,
  restaurantClosure: mockedRestaurantDataWithClosure,
  packages: mockedMealPackagesData.packages.packages,
  isLoading: false,
  isSuccess: true,
  isError: false,
  error: {
    message: 'test error',
  },
  isFetching: false,
};

const mockUseRouter = jest.fn();
const mockedManageBookingData = {
  bookingConfirmation: {
    bookingFlowId: 'booking-a1',
    hotelId: 'LONEUS',
    hotelName: 'London Euston',
    infoMessages: ['<p>Free cancellation up to 1pm on the day of arrival</p>\n'],
    currencyCode: 'GBP',
    totalCost: 199.95,
    previousTotal: 0,
    newTotal: 199.95,
    channel: 'PI',
    companyId: '2455921',
    reservationByIdList: [
      {
        reservationId: '738074',
        reservationGuestList: [
          {
            givenName: 'Tilica',
            surName: 'Franaru',
            nameTitle: 'Prof',
            email: null,
          },
        ],
        billing: {
          address: {
            addressLine1: 'London Road North',
            addressLine2: '',
            addressLine3: '',
            addressLine4: 'LOWESTOFT',
            companyName: 'United Reformed Church',
            country: 'GB',
            postalCode: 'NR32 1HB',
          },
          email: 'cristiadrian@mailinator.com',
        },
        roomStay: {
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2024-04-01',
          departureDate: '2024-04-06',
          ratePlanCode: 'FLEXRATE',
          roomExtraInfo: {
            roomType: 'DOUBLE',
            roomName: 'Double room',
            groupId: 'double',
          },
          accessibleRoom: {
            phoneNumber: '0333 321 1262',
            isAccessible: true,
          },
          roomPrice: 75,
        },
      },
      {
        reservationId: '738073',
        reservationGuestList: [
          {
            givenName: 'Cristi',
            surName: 'Normal',
            nameTitle: 'Mr',
            email: 'cristiadrian@mailinator.com',
          },
        ],
        billing: {
          address: {
            addressLine1: 'London Road North',
            addressLine2: '',
            addressLine3: '',
            addressLine4: 'LOWESTOFT',
            companyName: 'United Reformed Church',
            country: 'GB',
            postalCode: 'NR32 1HB',
          },
          email: 'cristiadrian@mailinator.com',
        },
        roomStay: {
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2024-04-01',
          departureDate: '2024-04-06',
          ratePlanCode: 'FLEXRATE',
          roomExtraInfo: {
            roomType: 'LOWDBL',
            roomName: 'Accessible double bedroom with a lowered bath',
            groupId: 'accessible',
          },
          accessibleRoom: {
            phoneNumber: '0333 321 1262',
            isAccessible: true,
          },
          roomPrice: 75,
        },
      },
    ],
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useAgentMemo: () => ({
    isAgentMemoOpen: false,
    openAgentMemo: jest.fn(),
    closeAgentMemo: jest.fn(),
    setAgentMemoReservationId: jest.fn(),
    agentMemoState: { reservationId: '', variant: '' },
    agentMemoData: null,
    agentMemoCount: 0,
  }),
  useBookingConfimationData: () => ({
    bookingData: mockBookingConfirmationAuthenticatedResponse.data.bookingConfirmationAuthenticated,
    bookingError: mockBookingConfirmationAuthenticatedResponse.error,
    bookingIsError: mockBookingConfirmationAuthenticatedResponse.isError,
    bookingIsLoading: mockBookingConfirmationAuthenticatedResponse.isLoading,
    bookingIsSuccess: mockBookingConfirmationAuthenticatedResponse.isSuccess,
    bookingRefetch: jest.fn(),
  }),
  useQueryRequest: jest.fn().mockImplementation((queryKey) => {
    let queryKeyValue = queryKey;
    if (Array.isArray(queryKey)) {
      queryKeyValue = queryKey[0];
    }
    switch (queryKeyValue) {
      case 'GetStaticContent':
        return {
          ...mockedGetStaticContentData,
        };
      case 'getBookingConfirmationAmend':
        return {
          data: {
            ...mockedManageBookingData,
          },
        };
      case 'AmendSummary':
        return mockedSummaryOfPaymentsData;

      case 'GetPackages':
        return mockedGetPackages;
      case 'paymentOptions':
        return {
          data: {
            ...mockedAmendPaymentData,
          },
        };
      case 'getBookingConfirmationAuthenticated':
        return {
          data: {
            ...mockBookingConfirmationAuthenticatedResponse,
          },
        };
      default:
        return {};
    }
  }),
  usePackages: () => ({
    ...mockedGetPackages.packages,
    ...mockedGetPackages,
  }),
}));
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/organisms', () => ({
  BookingSummaryWrapper: ({ children }) => (
    <div data-testid="BookingSummarySection">{children}</div>
  ),
  AmendPaymentCCUI: ({ children }) => <div data-testid="AmendPaymentA2C">{children}</div>,
  ChangePaymentCCUI: ({ children }) => <div data-testid="ChangePaymentContainer">{children}</div>,
  AgentMemo: () => <div data-testid="agent-memo" />,

  getAmendSectionTranslations: jest.fn().mockResolvedValue({
    bookingSummaryLabels: {},
    summaryOfPaymentsLabels: {},
    notificationLabels: {},
    removeRoomModalLabels: {},
    _stayDatesLabels: {},
    leadGuestValidationLabels: {},
    leadGuestLabels: {},
    roomAvailabilityLabels: {},
  }),
}));

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: () => jest.fn(),
  }),
}));

describe('Amend Payment page', () => {
  const mockUser = 'agent' as unknown as Claims;

  beforeEach(() => {
    mockBookingConfirmationAuthenticatedResponse.isError = false;
  });
  afterAll(() => {
    jest.resetAllMocks();
  });

  it('should render Payment page and find title and description', async () => {
    const { getByTestId } = render(<PaymentPageAmend user={mockUser} />);

    expect(getByTestId('amend-payment')).toBeInTheDocument();
    expect(getByTestId('amend-payment_title')).toBeInTheDocument();
    expect(getByTestId('amend-payment_titleDescription')).toBeInTheDocument();
    expect(getByTestId('amend-payment_wrapperTitle')).toBeInTheDocument();
    expect(getByTestId('amend-payment_wrapperTitleDescription')).toBeInTheDocument();
  });
  it('should find error message for booking confirmation request', () => {
    mockBookingConfirmationAuthenticatedResponse.isError = true;
    const { getByTestId } = render(<PaymentPageAmend user={mockUser} />);
    expect(getByTestId('amend-payment-page-request-error')).toBeInTheDocument();
  });
  it('should render the Payment page and find booking summary wrapper section', () => {
    const { getByTestId } = render(<PaymentPageAmend user={mockUser} />);
    expect(getByTestId('BookingSummarySection')).toBeInTheDocument();
  });
  it('should render Payment page with changePaymentBIC=TRUE', () => {
    mockUseRouter.mockReturnValue({
      query: { changePaymentBIC: 'true' },
    });
    const { getByTestId } = render(<PaymentPageAmend user={mockUser} />);
    expect(getByTestId('amend-payment')).toBeInTheDocument();
  });
});
