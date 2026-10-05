import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { LanguageEnum, FT_CCUI_BOOKING_HISTORY_REDESIGN } from '@whitbread-eos/api';
import { TableRow, ResultsCcui } from '@whitbread-eos/api';
import { upperOnlyFirst, formatDate } from '@whitbread-eos/utils';
import { format, parseISO } from 'date-fns';
import { de, enGB } from 'date-fns/locale';
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';

import { fireEvent, render, screen, waitFor } from '../../utils/page-test-utils';
import { bookingsResultsMockData, searchRulesMockData, staticContentMockData } from './mockData';
import BookingsPageCcui, {
  mappedResults,
  getBookingStatus,
  setInputValuesInSessionStorage,
  resultsWithChangedStatus,
} from './page.ccui.enhancedSearch';

const mockStaticContentRequest = {
  data: staticContentMockData,
  isError: false,
  isLoading: false,
  error: {
    message: '',
  },
};

const mockGetSearchRulesRequest = {
  data: searchRulesMockData,
  isError: false,
  isLoading: false,
  error: {
    message: '',
  },
};

const mockGetSearchBookingResultsRequest = {
  data: bookingsResultsMockData,
  isError: false,
  isLoading: false,
  error: {
    message: '',
  },
};

const mockedFormValues = {
  bookerLastName: 'John',
  guestLastName: 'Snow',
  arrivalDate: '2023-09-13',
  bookerPostcode: '12345',
  hotelDetails: { name: 'Hotel Test', code: '1234' },
  hotelLocation: 'London',
  bookerEmail: 'john@mailinator.com',
  bookerPhone: '',
  cancellationDate: '2023-09-12',
  companyName: '',
  thirdPartyBookingReferenceNumber: '',
  extendedSearchCriteria: '',
  hotelId: '',
};

const mockedFormValuesMissingDates = {
  bookerLastName: 'John',
  guestLastName: 'Snow',
  arrivalDate: '',
  bookerPostcode: '12345',
  hotelDetails: { name: 'Hotel Test', code: '1234' },
  hotelLocation: 'London',
  bookerEmail: 'john@mailinator.com',
  bookerPhone: '',
  cancellationDate: '',
  companyName: '',
  thirdPartyBookingReferenceNumber: '',
  extendedSearchCriteria: '',
  hotelId: '',
};

const bookingReferenceRPB = 'TEST123';
const encodedMockValues =
  'eyJib29rZXJMYXN0TmFtZSI6IkpvaG4iLCJhcnJpdmFsRGF0ZSI6IjIwMjMtMDktMTMiLCJib29raW5nUmVmZXJlbmNlUlBCIjoiVEVTVDEyMyIsImd1ZXN0TGFzdE5hbWUiOiJTbm93IiwiYm9va2VyUG9zdGNvZGUiOiIxMjM0NSIsImhvdGVsRGV0YWlscyI6eyJuYW1lIjoiSG90ZWwgVGVzdCIsImNvZGUiOiIxMjM0In0sImhvdGVsTG9jYXRpb24iOiJMb25kb24iLCJib29rZXJFbWFpbCI6ImpvaG5AbWFpbGluYXRvci5jb20iLCJib29rZXJQaG9uZSI6IiIsImNvbXBhbnlOYW1lIjoiIiwiZXh0ZW5kZWRTZWFyY2hDcml0ZXJpYSI6IiIsInRoaXJkUGFydHlCb29raW5nUmVmZXJlbmNlTnVtYmVyIjoiIiwiaG90ZWxJZCI6IjEyMzQifQ==';
const encodedMockMissingDatesValues =
  'eyJib29rZXJMYXN0TmFtZSI6IkpvaG4iLCJhcnJpdmFsRGF0ZSI6IiIsImJvb2tpbmdSZWZlcmVuY2VSUEIiOiJURVNUMTIzIiwiZ3Vlc3RMYXN0TmFtZSI6IlNub3ciLCJib29rZXJQb3N0Y29kZSI6IjEyMzQ1IiwiaG90ZWxEZXRhaWxzIjp7Im5hbWUiOiJIb3RlbCBUZXN0IiwiY29kZSI6IjEyMzQifSwiaG90ZWxMb2NhdGlvbiI6IkxvbmRvbiIsImJvb2tlckVtYWlsIjoiam9obkBtYWlsaW5hdG9yLmNvbSIsImJvb2tlclBob25lIjoiIiwiY2FuY2VsbGF0aW9uRGF0ZSI6IiIsImNvbXBhbnlOYW1lIjoiIiwiZXh0ZW5kZWRTZWFyY2hDcml0ZXJpYSI6IiIsInRoaXJkUGFydHlCb29raW5nUmVmZXJlbmNlTnVtYmVyIjoiIiwiaG90ZWxJZCI6IjEyMzQifQ==';
const dateType = 'yyyy-MM-dd';
const mockedSearchData: TableRow[] = [
  {
    cells: [
      { id: 'BookedFor', value: '' },
      { id: 'BookedBy', value: '' },
      {
        id: 'Hotel',
        value: '',
      },
      { id: 'Date', value: '' },

      { id: 'Status', value: '', label: '' },
      { id: 'SourcePms', value: '' },
    ],
    bookingReference: 'MAH7346157',
  },
  {
    cells: [
      { id: 'BookedFor', value: '' },
      { id: 'BookedBy', value: '' },
      {
        id: 'Hotel',
        value: '',
      },
      { id: 'Date', value: '' },

      { id: 'Status', value: '', label: '' },
      { id: 'SourcePms', value: '' },
    ],
    bookingReference: 'MAH7346156',
  },
];

const mockGetNewSearchResultsRequest = jest.fn().mockResolvedValue({
  data: {
    results: [],
  },
});

function mockUseQueryRequest(queryKey: any) {
  const key = queryKey[0];

  if (typeof key === 'string') {
    if (key === 'GetEnhancedSearchBookingsResults') {
      mockGetNewSearchResultsRequest();
      return mockGetSearchBookingResultsRequest;
    }
    if (key === 'GetHotelInformation') {
      return {
        data: {
          country: 'GB',
          hotelId: '1',
          language: 'en',
        },
      };
    }
    if (key === 'GetStaticContent') {
      return mockStaticContentRequest;
    }
    if (key === 'getSearchRules') {
      return mockGetSearchRulesRequest;
    }
  }
}

const mockProps: any = {
  user: {},
  setAnalyticsUser: jest.fn(),
  router: {
    query: {
      searchLocation: '',
      ARRdd: '',
      ARRmm: '',
      ARRyyyy: '',
      NIGHTS: '',
      ROOMS: '',
    },
  },
};

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  SEO: () => <div></div>,
}));

const queryClient = new ReactQuery.QueryClient();
const mockGetSessionStorageValues = jest.fn();
const mockGetBookingStatus = jest.fn();
const mockMappedResults = jest.fn();
const mockGetItem = jest.fn();
const mockLocalStorageGetItem = jest.fn();
const mockLocalStorageSetItem = jest.fn();
const mockLocalStorageRemoveItem = jest.fn();
const mockUseTranslation = jest.fn();
const manageBookingNotificationTitle = 'Manage booking notification title';
const manageBookingNotificationDescription = 'Manage booking notification description';

const mockUseTranslationResponse = (isvalidContent = true) => {
  return {
    t: (key: string) => {
      switch (key) {
        case 'account.dashboard.booking.upcoming':
          return isvalidContent ? 'Upcoming Booking' : '';
        case 'account.dashboard.booking.cancelled':
          return isvalidContent ? 'Cancelled Booking' : '';
        case 'account.dashboard.booking.past':
          return isvalidContent ? 'Past Booking' : '';
        case 'account.dashboard.booking.checkedIn':
          return isvalidContent ? 'Checked in' : '';
        case 'ccui.manageBooking.notification.title':
          return isvalidContent ? manageBookingNotificationTitle : '';
        case 'ccui.manageBooking.notification.description':
          return isvalidContent ? manageBookingNotificationDescription : '';
        case 'ccui.manageBooking.arrivalDate':
          return 'arrivalDate';
        case 'ccui.manageBooking.showMore':
          return 'showMore';
        default:
          return '';
      }
    },
  };
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
  invalidateQueries: jest.fn().mockImplementation((queryKey) => mockUseQueryRequest(queryKey)),
  useQuery: () => jest.fn().mockImplementation((queryKey) => mockUseQueryRequest(queryKey)),
  useQueryRequest: jest.fn().mockImplementation((queryKey) => mockUseQueryRequest(queryKey)),
  upperOnlyFirst: jest.fn(),
  formatDate: jest.fn(),
  getLoggedInUserInfo: () => ({
    accessLevel: 'SUPER',
    employeeId: '',
    companyId: '',
    sessionId: '',
  }),
  useRestMutationRequest: () => ({
    mutation: jest.fn(),
  }),
  usePackages: () => ({}),
  getSessionStorageValuesForBookings: () => mockGetSessionStorageValues(),
  graphQLRequest: () => jest.fn(),
  useFeatureToggle: jest.fn(),
}));

jest.mock('@whitbread-eos/organisms', () => ({
  CCUISearchContainer: () => <div />,
  AgentMemo: () => <div />,
  ResultListContainer: () => <div />,
  AmendBookingConfirmationContainer: () => <div />,
  APP_VARIANT: {
    CCUI: 'ccui',
    PI: 'pi',
    BB: 'bb',
  },
}));

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => mockUseTranslation(),
}));

const mockCustomLocale = jest.fn();

jest.setTimeout(15000);

describe('Page Bookings ', () => {
  beforeEach(() => {
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    jest.spyOn(require('@whitbread-eos/utils'), 'useFeatureToggle').mockReturnValue({
      [FT_CCUI_BOOKING_HISTORY_REDESIGN]: false,
    });
    global.sessionStorage = {
      getItem: (...args: string[]) => mockGetItem(...args),
      key(): string | null {
        return null;
      },
      length: 0,
      removeItem: () => jest.fn(),
      setItem: () => jest.fn(),
      clear: () => jest.fn(),
    };
    mockCustomLocale.mockReturnValue({
      language: 'gb',
      country: 'gb',
    });
    mockGetSessionStorageValues.mockReturnValue({
      bookingReference: '',
    });
    mockGetItem.mockReturnValue({
      item: 'ccuiPrevSearchCriteria',
    });
    mockGetBookingStatus({
      value: '',
      label: 'test',
    });
    mockMappedResults({
      cells: [
        { id: 'BookedFor', value: '' },
        { id: 'BookedBy', value: '' },
        {
          id: 'Hotel',
          value: '',
        },
        { id: 'Date', value: '' },

        { id: 'Status', value: '', label: '' },
        { id: 'SourcePms', value: '' },
      ],
      bookingReference: 'MAH7346157',
    });
    Object.defineProperty(window, 'performance', {
      value: {
        getEntriesByType: jest.fn().mockReturnValue([{ type: 'reload' }]),
        measure: jest.fn(),
      },
    });
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: (...args: string[]) => mockLocalStorageGetItem(...args),
        setItem: (...args: string[]) => mockLocalStorageSetItem(...args),
        removeItem: (...args: string[]) => mockLocalStorageRemoveItem(...args),
      },
    });
    Object.defineProperty(window, 'sessionStorage', {
      value: {
        removeItem: jest.fn(() => null),
        getItem: jest.fn(() => 'ccuiPrevSearchCriteria'),
        setItem: jest.fn(() => null),
      },
      writable: true,
    });
    mockStaticContentRequest.isError = false;
    mockStaticContentRequest.error.message = '';
    mockStaticContentRequest.isLoading = false;

    mockUseTranslation.mockImplementation(() => {
      return mockUseTranslationResponse();
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should display an error message when GetStaticContent fails', async () => {
    const errorMessage = 'Test Get static content error';
    mockStaticContentRequest.isError = true;
    mockStaticContentRequest.error = new Error(errorMessage);

    const { getByText } = render(<BookingsPageCcui {...mockProps} queryClient={queryClient} />);

    expect(getByText(errorMessage)).toBeInTheDocument();
  });

  it('should display loading message', async () => {
    mockStaticContentRequest.isLoading = true;

    const { getByTestId } = render(<BookingsPageCcui {...mockProps} queryClient={queryClient} />);

    expect(getByTestId('loading-message')).toBeInTheDocument();
  });

  it('should render the bookings page ', async () => {
    const { getByTestId } = render(<BookingsPageCcui {...mockProps} queryClient={queryClient} />);
    expect(getByTestId('SearchBookingsPage-Header')).toBeInTheDocument();
    expect(getByTestId('SearchBookingsPage-Search')).toBeInTheDocument();
    expect(getByTestId('SearchBookingsPage-SearchFormWrapper')).toBeInTheDocument();
    expect(getByTestId('SearchBookingsPage-ResultListContainer')).toBeInTheDocument();
  });

  it('should render additional search criteria bookings page ', async () => {
    const { getByText } = render(<BookingsPageCcui {...mockProps} queryClient={queryClient} />);

    const extendSearchCriteriaBtn = getByText('showMore');
    expect(extendSearchCriteriaBtn).toBeInTheDocument();
  });

  it('should find the search button disabled if not a input is filled with a value ', async () => {
    const { getByTestId } = render(<BookingsPageCcui {...mockProps} queryClient={queryClient} />);
    const submitButton = getByTestId('SearchBookingsPage-Submit-Button');
    expect(submitButton).toBeDisabled();
  });

  it('should change fields values after typing in them ', async () => {
    const { getByTestId, getByPlaceholderText } = render(
      <BookingsPageCcui {...mockProps} queryClient={queryClient} />
    );
    const inputBookingRef = getByTestId('input-bookingReference');
    const inputBookingLastName = getByTestId('input-bookerLastName');
    const inputarrivalDate = getByPlaceholderText('arrivalDate');
    fireEvent.change(inputBookingRef, {
      target: { value: 'MAH7346157' },
    });
    fireEvent.change(inputarrivalDate, {
      target: { value: '' },
    });
    fireEvent.change(inputBookingLastName, {
      target: { value: 'testerqa' },
    });

    expect(getByTestId('input-bookingReference')).toHaveValue('MAH7346157');
    expect(getByTestId('input-bookerLastName')).toHaveValue('testerqa');
    expect(getByPlaceholderText('arrivalDate')).toHaveValue('');
  });

  it('should prefill the search console if search data is in sessionStorage and enable search button', async () => {
    mockGetSessionStorageValues.mockReturnValue({
      bookingReference: 'MAH7346157',
      bookerLastName: 'testerqa',
    });
    const { getByTestId } = render(<BookingsPageCcui {...mockProps} queryClient={queryClient} />);
    const submitButton = getByTestId('SearchBookingsPage-Submit-Button');
    expect(screen.getByTestId('input-bookingReference')).toHaveValue('MAH7346157');
    expect(screen.getByTestId('input-bookerLastName')).toHaveValue('testerqa');
    expect(submitButton).toBeEnabled();
  });

  it('should add data in localStorage if input data is valid and submit button was clicked', async () => {
    mockGetSessionStorageValues.mockReturnValue({
      bookingReference: 'MAH7346157',
      bookerLastName: 'testerqa',
    });
    render(<BookingsPageCcui {...mockProps} queryClient={queryClient} />);
    const submitButton = screen.getByTestId('SearchBookingsPage-Submit-Button');

    await waitFor(() => {
      fireEvent.click(submitButton);
    });

    await waitFor(() => {
      expect(mockLocalStorageSetItem).toHaveBeenCalledWith(
        'SearchBookingFormBookingReference',
        JSON.stringify('MAH7346157')
      );
      expect(mockLocalStorageSetItem).toHaveBeenCalledWith(
        'SearchBookingFormAllParams',
        JSON.stringify({
          bookerLastName: 'testerqa',
          guestLastName: '',
          arrivalDate: '',
          bookerPostcode: '',
          hotelDetails: { name: '', code: '' },
          hotelLocation: '',
          bookerEmail: '',
          bookerPhone: '',
          cancellationDate: '',
          companyName: '',
          thirdPartyBookingReferenceNumber: '',
        })
      );
    });
  });

  it('should remove previous search criteria from sessionStorage if submit button was clicked', async () => {
    mockGetSessionStorageValues.mockReturnValue({
      bookingReference: 'MAH7346157',
      bookerLastName: 'testerqa',
    });
    render(<BookingsPageCcui {...mockProps} queryClient={queryClient} />);
    const submitButton = screen.getByTestId('SearchBookingsPage-Submit-Button');

    await waitFor(() => {
      fireEvent.click(submitButton);
    });

    await waitFor(() => {
      expect(window.sessionStorage.removeItem).toHaveBeenCalledWith('ccuiPrevSearchCriteria');
    });
  });

  it('should remove previous search criteria from sessionStorage if submit button was clicked', async () => {
    mockGetSessionStorageValues.mockReturnValue({
      bookingReference: 'MAH7346157',
      bookerLastName: 'testerqa',
    });
    render(<BookingsPageCcui {...mockProps} queryClient={queryClient} />);
    const submitButton = screen.getByTestId('SearchBookingsPage-Submit-Button');

    await waitFor(() => {
      fireEvent.click(submitButton);
    });

    await waitFor(() => {
      expect(window.sessionStorage.removeItem).toHaveBeenCalledWith('ccuiPrevSearchCriteria');
    });
  });

  it('should remove previous search criteria from sessionStorage and reset booking reference from localstorage if clear button was clicked', async () => {
    mockGetSessionStorageValues.mockReturnValue({
      bookingReference: 'MAH7346157',
      bookerLastName: 'testerqa',
    });
    render(<BookingsPageCcui {...mockProps} queryClient={queryClient} />);
    const clearButton = screen.getByTestId('SearchBookingsPage-Reset-Button');

    await waitFor(() => {
      fireEvent.click(clearButton);
    });

    await waitFor(() => {
      expect(mockLocalStorageSetItem).toHaveBeenCalledWith(
        'SearchBookingFormBookingReference',
        JSON.stringify('')
      );
      expect(mockLocalStorageSetItem).toHaveBeenCalledWith(
        'SearchBookingFormAllParams',
        JSON.stringify({
          bookerLastName: '',
          guestLastName: '',
          arrivalDate: '',
          bookerPostcode: '',
          hotelDetails: { name: '', code: '' },
          hotelLocation: '',
          bookerEmail: '',
          bookerPhone: '',
          cancellationDate: '',
          companyName: '',
          thirdPartyBookingReferenceNumber: '',
        })
      );
      expect(window.sessionStorage.removeItem).toHaveBeenCalledWith('ccuiPrevSearchCriteria');
    });
  });

  it('should display a notification message when valid content is added in AEM', async () => {
    const { getByRole, getByText } = render(
      <BookingsPageCcui {...mockProps} queryClient={queryClient} />
    );
    expect(getByRole('status')).toBeInTheDocument();
    expect(getByText(manageBookingNotificationTitle)).toBeInTheDocument();
    expect(getByText(manageBookingNotificationDescription)).toBeInTheDocument();
  });

  it('should not display a notification message when no content is added in AEM', async () => {
    mockUseTranslation.mockImplementation(() => {
      return mockUseTranslationResponse(false);
    });

    const { queryByRole, queryByText } = render(
      <BookingsPageCcui {...mockProps} queryClient={queryClient} />
    );
    expect(queryByRole('status')).not.toBeInTheDocument();
    expect(queryByText(manageBookingNotificationTitle)).not.toBeInTheDocument();
    expect(queryByText(manageBookingNotificationDescription)).not.toBeInTheDocument();
  });

  it('should delete data from localStorage and sessionStorage ', async () => {
    mockLocalStorageGetItem.mockReturnValue('true');

    mockGetSessionStorageValues.mockReturnValue({
      bookingReference: 'MAH7346157',
      bookerLastName: 'testerqa',
    });
    render(<BookingsPageCcui {...mockProps} queryClient={queryClient} />);

    expect(window.sessionStorage.removeItem).toHaveBeenCalledWith('ccuiPrevSearchCriteria');
    mockLocalStorageGetItem.mockReset();
  });

  it('restores the booking card by searching the persisted reference after returning from the change-payment page', async () => {
    (window.performance.getEntriesByType as jest.Mock).mockReturnValue([{ type: 'navigate' }]);
    (window.sessionStorage.getItem as jest.Mock).mockReturnValue(null);

    mockLocalStorageGetItem.mockImplementation((key: string) => {
      switch (key) {
        case 'SearchBookingFormBookingReference':
          return JSON.stringify('MAH7346157');
        case 'SearchBookingFormAllParams':
          return JSON.stringify({ bookerLastName: 'testerqa' });
        case 'isBackFromChangePaymentPageCCUI':
          return JSON.stringify(true);
        default:
          return null;
      }
    });

    const fetchQuerySpy = jest.spyOn(queryClient, 'fetchQuery').mockResolvedValue({
      searchBookingsCcui: {
        results: [],
        hasMore: false,
        responseLimitExceeded: false,
        pageResults: 0,
        searchResults: 0,
      },
    } as never);

    render(<BookingsPageCcui {...mockProps} queryClient={queryClient} />);

    await waitFor(() => {
      expect(fetchQuerySpy).toHaveBeenCalledWith(
        expect.objectContaining({ queryKey: ['GetEnhancedSearchBookingsResults'] })
      );
    });

    fetchQuerySpy.mockRestore();
  });

  it('returns correct value for getBookingStatus function for Past Status', async () => {
    const status = 'past';
    const formattedStatus = `${status.charAt(0).toUpperCase()}${status.slice(1).toLowerCase()}`;
    const bookingStatus = { value: 'Past', label: 'Past' };

    (upperOnlyFirst as jest.Mock).mockReturnValue(formattedStatus);
    const { t } = mockUseTranslation();
    const result = getBookingStatus(status, t);

    expect(upperOnlyFirst).toHaveBeenCalled();
    expect(result).toEqual(bookingStatus);
  });

  it('returns correct value for getBookingStatus function for Cancelled Status', async () => {
    const status = 'cancelled';
    const formattedStatus = `${status.charAt(0).toUpperCase()}${status.slice(1).toLowerCase()}`;
    const bookingStatus = { value: 'Cancelled', label: 'Cancelled' };

    (upperOnlyFirst as jest.Mock).mockReturnValue(formattedStatus);
    const { t } = mockUseTranslation();
    const result = getBookingStatus(status, t);

    expect(upperOnlyFirst).toHaveBeenCalled();
    expect(result).toEqual(bookingStatus);
  });

  it('returns correct value for getBookingStatus function for Cancelled Status', async () => {
    const status = 'checked-in';
    const formattedStatus = `${status.charAt(0).toUpperCase()}${status.slice(1).toLowerCase()}`;
    const bookingStatus = { value: 'Checked-in', label: 'Checked-in' };

    (upperOnlyFirst as jest.Mock).mockReturnValue(formattedStatus);
    const { t } = mockUseTranslation();
    const result = getBookingStatus(status, t);

    expect(upperOnlyFirst).toHaveBeenCalled();
    expect(result).toEqual(bookingStatus);
  });

  it('returns correct value for getBookingStatus function for unknown Status', async () => {
    const status = 'unknown';
    const bookingStatus = { value: 'Unknown', label: 'Unknown' };

    (upperOnlyFirst as jest.Mock).mockReturnValue('Unknown');
    const { t } = mockUseTranslation();
    const result = getBookingStatus(status, t);

    expect(upperOnlyFirst).toHaveBeenCalled();
    expect(result).toEqual(bookingStatus);
  });

  it('returns correct value for getBookingStatus function for Upcoming Status', async () => {
    const status = 'upcoming';
    const formattedStatus = `${status.charAt(0).toUpperCase()}${status.slice(1).toLowerCase()}`;
    const bookingStatus = { value: 'Upcoming', label: 'Upcoming' };

    (upperOnlyFirst as jest.Mock).mockReturnValue(formattedStatus);
    const { t } = mockUseTranslation();
    const result = getBookingStatus(status, t);

    expect(upperOnlyFirst).toHaveBeenCalled();
    expect(result).toEqual(bookingStatus);
  });

  it('returns correct value for mappedResults function', async () => {
    (upperOnlyFirst as jest.Mock).mockImplementationOnce((statusValue) => {
      return `${statusValue.charAt(0).toUpperCase()}${statusValue.slice(1).toLowerCase()}`;
    });
    const { t } = mockUseTranslation();
    const operaConfNumber = 'TEST20';
    const bookings: ResultsCcui[] = mockGetSearchBookingResultsRequest.data.searchBookings.bookings;
    const result = mappedResults(bookings, operaConfNumber, t);
    const expectedResult = [
      {
        cells: [
          {
            id: 'BookedFor',
            value: '',
          },
          {
            id: 'BookedBy',
            value: 'Mrs tester testerqa',
          },
          {
            id: 'Hotel',
            value: 'London Kings Cross - hub by Premier Inn',
          },
          {
            id: 'Date',
            value: '2023-09-13',
          },
          {
            id: 'Status',
            value: 'Upcoming',
            label: 'Upcoming',
          },
          {
            id: 'SourcePms',
            value: 'Upcoming',
          },
        ],
        bookingReference: 'MAH7346157',
        operaConfNumber: 'TEST20',
        hotelId: 'LONKIN',
        bookerLastName: 'testerqa',
      },
    ];
    expect(upperOnlyFirst).toHaveBeenCalled();
    expect(result).toEqual(expectedResult);
  });
  it('sets correct values in sessionStorage when arivalDate and cancellationDate exist and setInputValuesInSessionStorage is called', async () => {
    (formatDate as jest.Mock).mockImplementationOnce((date, formatDate, language) => {
      const isoDate = date.includes('-')
        ? parseISO(date)
        : parseISO(format(new Date(date), dateType));
      return format(isoDate, formatDate, {
        locale: language === LanguageEnum.GERMAN ? de : enGB,
      });
    });

    setInputValuesInSessionStorage(mockedFormValues, bookingReferenceRPB);
    expect(window.sessionStorage.setItem).toHaveBeenCalledWith(
      'ccuiPrevSearchCriteria',
      encodedMockValues
    );
  });
  it('sets correct values in sessionStorage when arivalDate and cancellationDate do not exist and setInputValuesInSessionStorage is called', async () => {
    (formatDate as jest.Mock).mockImplementationOnce((date, formatDate, language) => {
      const isoDate = date.includes('-')
        ? parseISO(date)
        : parseISO(format(new Date(date), dateType));
      return format(isoDate, formatDate, {
        locale: language === LanguageEnum.GERMAN ? de : enGB,
      });
    });

    setInputValuesInSessionStorage(mockedFormValuesMissingDates, bookingReferenceRPB);
    expect(window.sessionStorage.setItem).toHaveBeenCalledWith(
      'ccuiPrevSearchCriteria',
      encodedMockMissingDatesValues
    );
  });
  it('does not call sessionStorage setItem in setInputValuesInSessionStorage when window is undefined', async () => {
    const mockEncodeToBase64 = jest.fn();
    jest.mock('@whitbread-eos/utils', () => ({
      ...jest.requireActual('@whitbread-eos/utils'),
      encodeToBase64: mockEncodeToBase64,
    }));
    jest.spyOn(global as any, 'window', 'get').mockReturnValue(undefined);
    (formatDate as jest.Mock).mockImplementationOnce((date, formatDate, language) => {
      const isoDate = date.includes('-')
        ? parseISO(date)
        : parseISO(format(new Date(date), dateType));
      return format(isoDate, formatDate, {
        locale: language === LanguageEnum.GERMAN ? de : enGB,
      });
    });

    setInputValuesInSessionStorage(mockedFormValues, bookingReferenceRPB);
    expect(global.window).toBeUndefined();
    expect(mockEncodeToBase64).not.toHaveBeenCalled();
    jest.restoreAllMocks();
  });
  it('returns correct number of cancelled bookings for resultsWithChangedStatus function', async () => {
    (upperOnlyFirst as jest.Mock).mockImplementationOnce((statusValue) => {
      return `${statusValue.charAt(0).toUpperCase()}${statusValue.slice(1).toLowerCase()}`;
    });
    const bookingReference = 'MAH7346157';
    const { t } = mockUseTranslation();
    const result = resultsWithChangedStatus(mockedSearchData, bookingReference, t);
    const cancelledItemsCount = result.reduce((count, booking) => {
      const matchingCells = booking.cells.filter((cell) => cell.value === 'Cancelled');
      return count + matchingCells.length;
    }, 0);

    expect(cancelledItemsCount).toBe(1);
    expect(upperOnlyFirst).toHaveBeenCalled();
  });

  it('returns correct value for mappedResults function with redesign enabled', async () => {
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    jest.spyOn(require('@whitbread-eos/utils'), 'useFeatureToggle').mockReturnValue({
      [FT_CCUI_BOOKING_HISTORY_REDESIGN]: true,
    });
    (upperOnlyFirst as jest.Mock).mockImplementationOnce((statusValue) => {
      return `${statusValue.charAt(0).toUpperCase()}${statusValue.slice(1).toLowerCase()}`;
    });
    const { t } = mockUseTranslation();
    const operaConfNumber = 'TEST20';
    const bookings: ResultsCcui[] = mockGetSearchBookingResultsRequest.data.searchBookings.bookings;
    const isBookingHistoryRedesignCCUIEnabled = true;
    const language = 'en';

    bookings[0].totalCost = 90.55;
    bookings[0].currencyCode = 'GBP';
    bookings[0].guests = [{ title: 'Mr', firstName: 'John', lastName: 'Smith' }];

    const result = mappedResults(
      bookings,
      operaConfNumber,
      t,
      isBookingHistoryRedesignCCUIEnabled,
      language
    );

    // Update expectedResult to match redesignCells structure
    const expectedResult = [
      {
        cells: [
          { id: 'Hotel', value: 'London Kings Cross - hub by Premier Inn' },
          { id: 'Date', value: 'Wed, 13 Sep 23 - Fri, 15 Sep 23' },
          { id: 'BookedFor', value: 'Mr John Smith' },
          { id: 'Price', value: '£90.55' },
          { id: 'BookedBy', value: 'Mrs tester testerqa' },
          { id: 'Status', value: 'Upcoming', label: 'Upcoming' },
        ],
        bookingReference: 'MAH7346157',
        operaConfNumber: 'TEST20',
        hotelId: 'LONKIN',
        bookerLastName: 'testerqa',
      },
    ];
    expect(upperOnlyFirst).toHaveBeenCalled();
    expect(result).toEqual(expectedResult);
  });

  it('returns empty strings when values are missing', async () => {
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    jest.spyOn(require('@whitbread-eos/utils'), 'useFeatureToggle').mockReturnValue({
      [FT_CCUI_BOOKING_HISTORY_REDESIGN]: true,
    });
    (upperOnlyFirst as jest.Mock).mockImplementationOnce((statusValue) => {
      return `${statusValue.charAt(0).toUpperCase()}${statusValue.slice(1).toLowerCase()}`;
    });
    const { t } = mockUseTranslation();
    const operaConfNumber = 'TEST20';
    const baseBooking = mockGetSearchBookingResultsRequest.data.searchBookings.bookings[0];
    const isBookingHistoryRedesignCCUIEnabled = true;
    const language = 'en';

    const booking: ResultsCcui = {
      ...baseBooking,
      totalCost: undefined,
      guests: [{ title: 'Mr', firstName: 'John', lastName: 'Smith' }],
      arrivalDate: '',
      departureDate: '',
      hotelName: '',
      currencyCode: '',
    };

    const result = mappedResults(
      [booking],
      operaConfNumber,
      t,
      isBookingHistoryRedesignCCUIEnabled,
      language
    );

    const expectedResult = [
      {
        cells: [
          { id: 'Hotel', value: '' },
          { id: 'Date', value: '' },
          { id: 'BookedFor', value: 'Mr John Smith' },
          { id: 'Price', value: '' },
          { id: 'BookedBy', value: 'Mrs tester testerqa' },
          { id: 'Status', value: 'Upcoming', label: 'Upcoming' },
        ],
        bookingReference: 'MAH7346157',
        operaConfNumber: 'TEST20',
        hotelId: 'LONKIN',
        bookerLastName: 'testerqa',
      },
    ];
    expect(upperOnlyFirst).toHaveBeenCalled();
    expect(result).toEqual(expectedResult);
  });
});

describe('mappedResults date formatting', () => {
  const t = (key: string) => key;

  it('formats arrival and departure dates in German when locale is de', () => {
    const bookings = [
      {
        hotelName: 'Test Hotel',
        arrivalDate: '2025-10-13',
        departureDate: '2025-10-15',
        guests: [{ title: 'Herr', firstName: 'Johann', lastName: 'Schmidt' }],
        booker: { title: 'Frau', firstName: 'Anna', lastName: 'Muster' },
        totalCost: 90.55,
        currencyCode: 'EUR',
        status: 'upcoming',
        bookingReference: 'DE123',
        operaConfNumber: 'CONFDE',
        hotelId: 'BERLIN',
        bookerLastName: 'Muster',
        sourceSystem: 'Opera',
      },
    ];

    const result = mappedResults(
      bookings,
      'CONFDE',
      t,
      true, // isBookingHistoryRedesignCCUIEnabled
      'de' // language
    );

    // German formatted dates
    expect(result[0].cells.find((cell) => cell.id === 'Date')?.value).toBe(
      'Mo., 13 Okt 25 - Mi., 15 Okt 25'
    );
  });

  it('formats arrival and departure dates in English when locale is en', () => {
    const bookings = [
      {
        hotelName: 'Test Hotel',
        arrivalDate: '2025-10-13',
        departureDate: '2025-10-15',
        guests: [{ title: 'Mr', firstName: 'John', lastName: 'Smith' }],
        booker: { title: 'Mrs', firstName: 'Jane', lastName: 'Doe' },
        totalCost: 90.55,
        currencyCode: 'GBP',
        status: 'upcoming',
        bookingReference: 'EN123',
        operaConfNumber: 'CONFEN',
        hotelId: 'LONDON',
        bookerLastName: 'Doe',
        sourceSystem: 'Opera',
      },
    ];

    const result = mappedResults(
      bookings,
      'CONFEN',
      t,
      true, // isBookingHistoryRedesignCCUIEnabled
      'en' // language
    );

    // English formatted dates
    expect(result[0].cells.find((cell) => cell.id === 'Date')?.value).toBe(
      'Mon, 13 Oct 25 - Wed, 15 Oct 25'
    );
  });
});
