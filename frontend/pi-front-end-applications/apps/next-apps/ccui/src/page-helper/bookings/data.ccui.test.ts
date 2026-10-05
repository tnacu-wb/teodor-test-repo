import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';

import createBookingsCcuiDataLoaderFn from './data.ccui';
import { mockedGetStaticContent } from './mockData';

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  dehydrate: () => ({}),
}));

jest.mock('@whitbread-eos/utils', () => ({
  graphQLRequest: jest.fn(),
  getDefaultSessionTracing: jest.fn(),
  useQueryRequest: () => ({}),
  QueriesLogger: jest.fn().mockImplementation(() => ({
    prefetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      queryFn();
      if (queryKey[0] === 'GetStaticContent')
        return Promise.resolve({
          ...mockedGetStaticContent,
        });
    }),
    logQueries: jest.fn(),
  })),
  getGQLClient: jest.fn(),
}));

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: () => null,
    set: () => null,
  }));
});

const mockedData = {
  session: {
    accessToken: 'bookingsAT123',
    user: {},
  },
  resolvedUrl: '/',
  language: 'en',
  country: 'GB',
  req: { url: undefined },
  res: undefined,
  query: undefined,
  logger: {
    info: jest.fn(),
  },
};

const expectedResult = {
  dehydratedState: {},
  user: {},
};

const queryClient = new ReactQuery.QueryClient();

describe('createBookingsCcuiDataLoaderFn', () => {
  it('should render data loader with expected object', async () => {
    const dataLoader = await createBookingsCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);
    expect(dataLoader).toEqual(expectedResult);
  });
});
