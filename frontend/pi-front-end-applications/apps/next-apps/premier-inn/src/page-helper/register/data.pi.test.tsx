// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore-file
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';

import { createRegisterPiDataLoaderFn } from './index';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  QueriesLogger: jest.fn().mockImplementation(() => ({
    fetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      queryFn();
      if (queryKey[0] === 'GetStaticContent') {
        return Promise.resolve({
          headerInformation: {},
          footer: {},
          labels: {},
        });
      }
    }),
    prefetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      queryFn();
      if (queryKey[0] === 'seoInformation') {
        return Promise.resolve({
          seoInformation: {},
        });
      }
    }),
    logQueries: jest.fn(),
  })),
  graphQLRequest: jest.fn(),
  getDefaultSessionTracing: jest.fn(),
  getAvailabilityParamsFromUrl: jest.fn(),
  decodeIdToken: jest.fn(),
  axiosRequest: jest.fn(),
  getLoggedInUserInfo: jest.fn(),
  logger: {
    info: jest.fn(),
  },
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  mutations: undefined,
  queries: undefined,
}));

const queryClient = new ReactQuery.QueryClient();

const mockedData = {
  session: {
    accessToken: 'asasasGderg12312',
    user: {},
  },

  resolvedUrl: '/',
  query: {
    reservationId: '12',
    BRAND: 'PI',
  },
  language: 'en',
  country: 'GB',
  res: undefined,
  req: { url: undefined },
  staticData: '',
};

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: () => null,
    set: () => null,
  }));
});
describe('createRegisterPiDataLoaderFn', () => {
  it('should render data loader with expected object', async () => {
    const dataLoaderPi = await createRegisterPiDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderPi).toEqual({
      dehydratedState: { mutations: [], queries: [] },
      staticData: {
        footer: {},
        headerInformation: {},
        labels: {},
      },
    });
  });
});
