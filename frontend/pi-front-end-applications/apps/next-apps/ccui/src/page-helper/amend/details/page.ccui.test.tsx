import '@testing-library/jest-dom';
import { waitFor } from '@testing-library/react';

import {
  mockedGetStaticContent,
  mockPackagesData,
  mockRoomOccupancyLimitations,
  mockStayRules,
} from '../test-utils';
import { render } from '../utils/test-utils';
import AmendPageCCUI from './page.ccui';

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

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

const mockBookingConfirmationData = {
  bookingData: {},
  bookingIsError: false,
  bookingIsLoading: false,
  bookingError: {
    message: '',
  },
};

const mockHotelInformationResponse = {
  data: { hotelInformation: { brand: 'CCUI' } },
  isLoading: false,
  isError: false,
  error: {
    message: '',
  },
};

jest.mock('@whitbread-eos/organisms', () => ({
  AgentMemo: () => <div data-testid="agent-memo" />,
  AmendContainer: ({ children }: any) => <div data-testid="amend-container">{children}</div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  cn: (...classes) => classes.filter(Boolean).join(' '),
  formatDataTestId: (...ids) => ids.filter(Boolean).join('-'),
  getNoOfDaysInYear: () => 365,
  getAuthCookie: () => null,
  getCookie: () => null,
  isInnBusinessApp: () => false,
  getAmendSectionTranslations: () => ({
    bookingSummaryLabels: {},
    guestDetailsLabels: {},
    additionalServicesLabels: {},
    leadGuestLabels: {},
    roomAvailabilityLabels: {},
  }),
  useFeatureSwitch: () => true,
  invalidateQueries: mockUseQueryRequest,
  useQuery: () => mockUseQueryRequest,
  useQueryRequest: mockUseQueryRequest,
  useBookingConfimationData: () => mockBookingConfirmationData,
  useMutationRequest: () => mockCopyBookingMutationResponse,
  getMaxValueFromRoomStays: jest.fn().mockImplementation(() => 0),
  usePackages: () => ({
    ...mockPackagesData?.data?.packages,
    ...mockPackagesData,
  }),
  useCustomLocale: () => ({ language: 'gb', country: 'gb' }),
  useSessionStorage: () => [null, jest.fn()],
  useAgentMemo: () => ({
    isAgentMemoOpen: false,
    openAgentMemo: jest.fn(),
    closeAgentMemo: jest.fn(),
    setAgentMemoReservationId: jest.fn(),
    agentMemoState: { reservationId: '', variant: '' },
    agentMemoData: null,
    agentMemoCount: 0,
  }),
}));

function mockUseQueryRequest(queryKey) {
  const key = queryKey[0];

  if (typeof key === 'string') {
    switch (key) {
      case 'getBookingConfirmationAmend':
        return mockBookingConfirmationData;
      case 'GetStaticContent':
        return mockedGetStaticContent;
      case 'getStayRules':
        return mockStayRules;
      case 'getRoomOccupancyLimitations':
        return mockRoomOccupancyLimitations;
      case 'AmendSummary':
        return {
          data: {},
          isLoading: false,
          isError: false,
        };
      case 'manageBookingDashBoardAmend':
        return {
          data: {},
          isLoading: false,
          isError: false,
        };
      case 'getHotelInformation':
        return mockHotelInformationResponse;
    }
  }
  return {
    data: undefined,
    isLoading: false,
    isError: false,
    error: null,
  };
}

const mockProps = {
  confirmationInput: {
    bookingReference: 'AKU8410577',
    basketReference: 'AKU8410577',
    token: 'test',
    country: 'GB',
    language: 'en',
  },
};

const mockCustomLocale = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    locale: 'en',
    query: {},
    replace: () => Promise.resolve({}),
    events: {
      on: jest.fn(),
      off: jest.fn(),
    },
  }),
}));
describe('Amend Details Page - CCUI', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'gb',
      country: 'gb',
    });
    mockBookingConfirmationData.bookingIsLoading = false;
    mockStayRules.isLoading = false;
    mockedGetStaticContent.isLoading = false;
    mockRoomOccupancyLimitations.isLoading = false;
    mockHotelInformationResponse.isLoading = false;
    mockBookingConfirmationData.bookingIsError = false;
    mockStayRules.isError = false;
    mockedGetStaticContent.isError = false;
    mockRoomOccupancyLimitations.isError = false;
    mockHotelInformationResponse.isError = false;
    mockCopyBookingMutationResponse.isError = false;
  });

  it('should stay in loading page CCUI', async () => {
    mockBookingConfirmationData.bookingIsLoading = true;
    const { getByTestId } = render(<AmendPageCCUI {...mockProps} />);

    await waitFor(() => expect(getByTestId('loading-spinner')).toBeInTheDocument());
  });

  it('should return booking confirmation error', async () => {
    mockBookingConfirmationData.bookingIsError = true;
    const { getByTestId } = render(<AmendPageCCUI {...mockProps} />);

    await waitFor(() =>
      expect(getByTestId('Amend-error-booking-confirmation')).toBeInTheDocument()
    );
  });

  it('should return stay rules error', async () => {
    mockStayRules.isError = true;
    const { getByTestId } = render(<AmendPageCCUI {...mockProps} />);

    await waitFor(() => expect(getByTestId('Amend-error-stay-rules')).toBeInTheDocument());
  });

  it('should return static content error', async () => {
    mockedGetStaticContent.isError = true;
    const { getByTestId } = render(<AmendPageCCUI {...mockProps} />);

    await waitFor(() => expect(getByTestId('Amend-error-translations')).toBeInTheDocument());
  });

  it('should return room occupancy error', async () => {
    mockRoomOccupancyLimitations.isError = true;
    const { getByTestId } = render(<AmendPageCCUI {...mockProps} />);

    await waitFor(() =>
      expect(getByTestId('Amend-error-roomOccupancyLimitations')).toBeInTheDocument()
    );
  });

  it('should return hotel information error', async () => {
    mockHotelInformationResponse.isError = true;
    const { getByTestId } = render(<AmendPageCCUI {...mockProps} />);

    await waitFor(() => expect(getByTestId('Amend-error-hotel-information')).toBeInTheDocument());
  });

  it('should return copy booking error', async () => {
    mockCopyBookingMutationResponse.isError = true;
    const { getByTestId } = render(<AmendPageCCUI {...mockProps} />);

    await waitFor(() =>
      expect(getByTestId('Amend-error-copy-booking-mutation')).toBeInTheDocument()
    );
  });
});
