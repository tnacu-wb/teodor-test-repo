import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { Area } from '@whitbread-eos/api';
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';

import { render } from '../../utils/page-test-utils';
import { Page } from './index';
import { bookingsResultsMockData, searchRulesMockData, staticContentMockData } from './mockData';

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

function mockUseQueryRequest(queryKey: any) {
  const key = queryKey[0];

  if (typeof key === 'string') {
    if (key === 'GetSearchBookingsResults') {
      return mockGetSearchBookingResultsRequest;
    }
    if (key === 'GetHotelInformation') {
      return {};
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

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
  invalidateQueries: mockUseQueryRequest,
  useQuery: () => mockUseQueryRequest,
  useQueryRequest: mockUseQueryRequest,
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

describe('Page Bookings Wrapper', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'gb',
      country: 'gb',
    });
    mockGetSessionStorageValues.mockReturnValue({
      bookingReference: '',
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
      },
      writable: true,
    });
    mockStaticContentRequest.isError = false;
    mockStaticContentRequest.error.message = '';
    mockStaticContentRequest.isLoading = false;
  });
  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should display an error message', async () => {
    const { getByTestId } = render(
      <Page {...mockProps} variant={Area.CCUI} queryClient={queryClient} enhancedSearch />
    );

    expect(getByTestId('SearchBookingsPage-Wrapper')).toBeInTheDocument();
  });

  it('should display an error message', async () => {
    const { getByTestId } = render(
      <Page {...mockProps} variant={Area.PI} queryClient={queryClient} />
    );

    expect(getByTestId('SearchBookingsPage-Wrapper')).toBeInTheDocument();
  });
});
