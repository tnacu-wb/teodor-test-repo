import '@testing-library/jest-dom';
import { waitFor } from '@testing-library/react';
import { render } from '@testing-library/react';

// Import mock data from local test-utils to avoid circular dependencies
import {
  mockBookingConfirmationAmend,
  mockedGetStaticContent,
  mockRoomOccupancyLimitations,
  mockStayRules,
} from '../test-utils';
import AmendPageBb from './page.bb';

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockCustomLocale = jest.fn();
const mockHotelInformationResponse = {
  data: { hotelInformation: { brand: 'PI' } },
  isLoading: false,
  isError: false,
  error: {
    message: '',
  },
};

const mockCopyBookingMutationResponse = {
  isError: false,
  error: { message: '' },
  mutation: {
    mutateAsync: jest.fn().mockResolvedValue({}),
    isLoading: false,
    data: {},
    mutate: jest.fn(),
  },
};

jest.mock('@whitbread-eos/utils', () => {
  // eslint-disable-next-line @typescript-eslint/no-require-imports
  const mocks = require('../test-utils');

  const mockUseQueryRequest = (queryKey: any[]) => {
    const key = queryKey[0];
    if (typeof key === 'string') {
      switch (key) {
        case 'getBookingConfirmationAmend':
          return mocks.mockBookingConfirmationAmend;
        case 'GetStaticContent':
          return mocks.mockedGetStaticContent;
        case 'getStayRules':
          return mocks.mockStayRules;
        case 'getRoomOccupancyLimitations':
          return mocks.mockRoomOccupancyLimitations;
        case 'getHotelInformation':
          return (
            global.mockHotelInformationResponse || {
              data: { hotelInformation: { brand: 'PI' } },
              isLoading: false,
              isError: false,
              error: { message: '' },
            }
          );
        case 'AmendSummary':
        case 'manageBookingDashBoardAmend':
        case 'getPaymentMethods':
          return { data: {}, isLoading: false, isError: false };
      }
    }
  };

  return {
    cn: (...classes: any[]) => classes.filter(Boolean).join(' '),
    getNoOfDaysInYear: () => 365,
    getAuthCookie: () => null,
    getCookie: () => null,
    getIsBillingAddressDisplayed: () => false,
    getPaymentError: () => undefined,
    isInnBusinessApp: () => false,
    getAmendSectionTranslations: () => ({
      bookingSummaryLabels: {},
      guestDetailsLabels: {},
      additionalServicesLabels: {},
      leadGuestLabels: {},
      roomAvailabilityLabels: {},
    }),
    useFeatureSwitch: () => true,
    formatDataTestId: (...ids: string[]) => ids.filter(Boolean).join('-'),
    useCustomLocale: () => global.mockCustomLocale?.() || { language: 'gb', country: 'gb' },
    invalidateQueries: mockUseQueryRequest,
    useQuery: () => mockUseQueryRequest,
    useQueryRequest: mockUseQueryRequest,
    useMutationRequest: () =>
      global.mockCopyBookingMutationResponse || {
        isError: false,
        error: { message: '' },
        mutation: {
          mutateAsync: jest.fn().mockResolvedValue({}),
          isLoading: false,
          data: {},
          mutate: jest.fn(),
        },
      },
    useRestMutationRequest: () => ({ mutation: jest.fn() }),
    usePackages: () => ({
      ...mocks.mockPackagesData?.data?.packages,
      ...mocks.mockPackagesData,
    }),
  };
});

const mockProps = {
  confirmationInput: {
    bookingReference: 'AKU8410577',
    basketReference: 'AKU8410577',
    token: 'test',
    country: 'GB',
    language: 'en',
  },
  pcksQueryInput: {
    endDate: '',
    basketReferenceId: '',
    nightsNumber: 1,
    childrenNumber: 1,
    startDate: '',
    country: '',
    hotelId: '',
    language: '',
    bookingFlowId: '',
    adultsNumber: 1,
  },
};

// Make mocks available globally for jest.mock factory
(global as any).mockCustomLocale = mockCustomLocale;
(global as any).mockHotelInformationResponse = mockHotelInformationResponse;
(global as any).mockCopyBookingMutationResponse = mockCopyBookingMutationResponse;

describe('Amend Details Page - BB', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'gb',
      country: 'gb',
    });
    mockBookingConfirmationAmend.isLoading = false;
    mockStayRules.isLoading = false;
    mockedGetStaticContent.isLoading = false;
    mockRoomOccupancyLimitations.isLoading = false;
    mockHotelInformationResponse.isLoading = false;
    mockBookingConfirmationAmend.isError = false;
    mockStayRules.isError = false;
    mockedGetStaticContent.isError = false;
    mockRoomOccupancyLimitations.isError = false;
    mockHotelInformationResponse.isError = false;
    mockCopyBookingMutationResponse.isError = false;
  });

  it('should stay in loading page bb', async () => {
    mockBookingConfirmationAmend.isLoading = true;
    const { getByTestId } = render(<AmendPageBb {...mockProps} />);

    await waitFor(() => expect(getByTestId('loading-spinner')).toBeInTheDocument());
  });

  it('should return booking confirmation error', async () => {
    mockBookingConfirmationAmend.isError = true;
    const { getByTestId } = render(<AmendPageBb {...mockProps} />);

    await waitFor(() =>
      expect(getByTestId('Amend-error-booking-confirmation')).toBeInTheDocument()
    );
  });

  it('should return stay rules error', async () => {
    mockStayRules.isError = true;
    const { getByTestId } = render(<AmendPageBb {...mockProps} />);

    await waitFor(() => expect(getByTestId('Amend-error-stay-rules')).toBeInTheDocument());
  });

  it('should return static content error', async () => {
    mockedGetStaticContent.isError = true;
    const { getByTestId } = render(<AmendPageBb {...mockProps} />);

    await waitFor(() => expect(getByTestId('Amend-error-translations')).toBeInTheDocument());
  });

  it('should return room occupancy error', async () => {
    mockRoomOccupancyLimitations.isError = true;
    const { getByTestId } = render(<AmendPageBb {...mockProps} />);

    await waitFor(() =>
      expect(getByTestId('Amend-error-roomOccupancyLimitations')).toBeInTheDocument()
    );
  });

  it('should return hotel information error', async () => {
    mockHotelInformationResponse.isError = true;
    const { getByTestId } = render(<AmendPageBb {...mockProps} />);

    await waitFor(() => expect(getByTestId('Amend-error-hotel-information')).toBeInTheDocument());
  });

  it('should return copy booking error', async () => {
    mockCopyBookingMutationResponse.isError = true;
    const { getByTestId } = render(<AmendPageBb {...mockProps} />);

    await waitFor(() =>
      expect(getByTestId('Amend-error-copy-booking-mutation')).toBeInTheDocument()
    );
  });
});
