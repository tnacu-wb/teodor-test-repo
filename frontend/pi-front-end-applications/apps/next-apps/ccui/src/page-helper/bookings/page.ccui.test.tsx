import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { LanguageEnum } from '@whitbread-eos/api';
import type { TableRow } from '@whitbread-eos/api';
import { upperOnlyFirst, formatDate } from '@whitbread-eos/utils';
import { format, parseISO } from 'date-fns';
import { de, enGB } from 'date-fns/locale';
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';

import { fireEvent, render, screen, waitFor } from '../../utils/page-test-utils';
import { bookingsResultsMockData, searchRulesMockData, staticContentMockData } from './mockData';
import BookingsPageCcui, {
  getBookingStatus,
  mapedResults,
  setInputValuesInSessionStorage,
  resultsWithChangedStatus,
} from './page.ccui';

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
function mockUseQueryRequest(queryKey: any) {
  const key = queryKey[0];

  if (typeof key === 'string') {
    if (key === 'GetSearchBookingsResults') {
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
const mockMapedResults = jest.fn();
const mockGetItem = jest.fn();
const mockUseTranslation = jest.fn();
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
        case 'ccui.manageBooking.arrivalDate':
          return 'arrivalDate';
        default:
          return '';
      }
    },
  };
};

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => mockUseTranslation(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
  invalidateQueries: mockUseQueryRequest,
  useQuery: () => mockUseQueryRequest,
  useQueryRequest: mockUseQueryRequest,
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

const mockCustomLocale = jest.fn();

describe('Page Bookings ', () => {
  beforeEach(() => {
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
    mockMapedResults({
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
        getEntriesByType: jest.fn().mockReturnValue([{ type: 'test' }]),
        measure: jest.fn(),
      },
    });
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: jest.fn(() => null),
        setItem: jest.fn(() => null),
      },
      writable: true,
    });
    Object.defineProperty(window, 'sessionStorage', {
      value: {
        removeItem: jest.fn(() => null),
        getItem: jest.fn(() => 'ccuiPrevSearchCriteria'),
        setItem: jest.fn(() => null),
      },
      writable: true,
    });

    Object.defineProperty(window, 'location', {
      writable: true,
      value: { assign: jest.fn() },
    });

    mockUseTranslation.mockImplementation(() => {
      return mockUseTranslationResponse();
    });

    mockStaticContentRequest.isError = false;
    mockStaticContentRequest.error.message = '';
    mockStaticContentRequest.isLoading = false;
  });
  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should display an error message', async () => {
    mockStaticContentRequest.isError = true;
    mockStaticContentRequest.error = new Error('Test Get static content Err');

    const { getByText } = render(<BookingsPageCcui {...mockProps} queryClient={queryClient} />);

    expect(getByText('Test Get static content Err')).toBeInTheDocument();
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
      expect(window.localStorage.setItem).toHaveBeenCalledWith(
        'SearchBookingFormBookingReference',
        JSON.stringify('MAH7346157')
      );
      expect(window.localStorage.setItem).toHaveBeenCalledWith(
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

  it('should persist data in form after reload', async () => {
    mockGetSessionStorageValues.mockReturnValue({
      bookingReference: 'MAH7346157',
      bookerLastName: 'testerqa',
    });

    render(<BookingsPageCcui {...mockProps} queryClient={queryClient} />);
    const submitButton = screen.getByTestId('SearchBookingsPage-Submit-Button');

    await waitFor(() => {
      fireEvent.click(submitButton);
    });

    window.location.reload = jest.fn();
    window.location.reload();
    expect(window.location.reload).toHaveBeenCalled();

    await waitFor(() => {
      expect(window.sessionStorage.removeItem).toHaveBeenCalledWith('ccuiPrevSearchCriteria');
    });

    expect(screen.getByTestId('input-bookingReference')).toHaveValue('MAH7346157');
    expect(screen.getByTestId('input-bookerLastName')).toHaveValue('testerqa');
    expect(submitButton).toBeEnabled();
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
      expect(window.localStorage.setItem).toHaveBeenCalledWith(
        'SearchBookingFormBookingReference',
        JSON.stringify('')
      );
      expect(window.localStorage.setItem).toHaveBeenCalledWith(
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

  it('should reset form on clear search', async () => {
    const { getByTestId } = render(<BookingsPageCcui {...mockProps} queryClient={queryClient} />);

    const inputBookingId = getByTestId('input-bookingReference') as HTMLInputElement;
    const inputBookingLastName = getByTestId('input-bookerLastName') as HTMLInputElement;
    const inputArrivalDatePicker = getByTestId('SingleDatePicker') as HTMLInputElement;

    fireEvent.change(inputBookingId, { target: { value: 'MAH7346157' } });
    fireEvent.change(inputBookingLastName, { target: { value: 'testerqa' } });
    const initialDateValue = inputArrivalDatePicker.value;

    expect(inputBookingId).toHaveValue('MAH7346157');
    expect(inputBookingLastName).toHaveValue('testerqa');

    const resetBtn = getByTestId('SearchBookingsPage-Reset-Button');
    fireEvent.click(resetBtn);

    await waitFor(() => {
      expect(inputBookingId).toHaveValue('');
      expect(inputBookingLastName).toHaveValue('');
      expect(inputArrivalDatePicker).toHaveValue(initialDateValue);
    });
  });
  it('returns correct value for getBookingStatus function', async () => {
    const status = 'past';
    const formattedStatus = `${status.charAt(0).toUpperCase()}${status.slice(1).toLowerCase()}`;
    const bookingStatus = { value: 'Past', label: 'Past' };

    (upperOnlyFirst as jest.Mock).mockReturnValue(formattedStatus);
    const { t } = mockUseTranslation();
    const result = getBookingStatus('past', t);

    expect(upperOnlyFirst).toHaveBeenCalled();
    expect(result).toEqual(bookingStatus);
  });
  it('returns correct value for mapedResults function', async () => {
    (upperOnlyFirst as jest.Mock).mockImplementationOnce((statusValue) => {
      return `${statusValue.charAt(0).toUpperCase()}${statusValue.slice(1).toLowerCase()}`;
    });
    const mockedFunc = jest.fn().mockImplementationOnce((status) => {
      const formattedStatus = upperOnlyFirst(status);

      if (formattedStatus === 'UPCOMING') {
        return {
          value: formattedStatus,
          label: upperOnlyFirst(t('account.dashboard.booking.upcoming')),
        };
      }

      if (formattedStatus === 'CANCELLED') {
        return {
          value: formattedStatus,
          label: upperOnlyFirst(t('account.dashboard.booking.cancelled')),
        };
      }

      return {
        value: formattedStatus,
        label: upperOnlyFirst(t('account.dashboard.booking.past')),
      };
    });
    jest.mock('./page.ccui', () => ({
      ...jest.requireActual('./page.ccui'),
      getBookingStatus: mockedFunc,
    }));
    const { t } = mockUseTranslation();
    const bookings = mockGetSearchBookingResultsRequest.data.searchBookings.bookings;
    const result = mapedResults(bookings, t);
    const expectedResult = [
      {
        cells: [
          {
            id: 'BookedFor',
            value: 'Prof gyytd room',
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
            label: 'Past',
          },
          {
            id: 'SourcePms',
            value: 'Past',
          },
        ],
        bookingReference: 'MAH7346157',
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
});
