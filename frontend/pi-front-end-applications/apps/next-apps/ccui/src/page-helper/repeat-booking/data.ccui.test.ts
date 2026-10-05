import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';

import createRepeatPageCcuiDataLoaderFn from './data.ccui';

const mockGetCookie = jest.fn();
jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: mockGetCookie,
    set: mockGetCookie,
  }));
});

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
    logQueries: jest.fn(),
  })),
  logger: {
    info: jest.fn(),
  },
  graphQLRequest: jest.fn(),
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  mutations: undefined,
  queries: undefined,
}));

const queryClient = new ReactQuery.QueryClient();

const mockedData = {
  session: {
    user: {},
  },
  language: 'en',
  country: 'GB',
  req: { url: undefined },
};

const expectedResult = {
  user: {},
  dehydratedState: {
    mutations: [],
    queries: [],
  },
};

describe('createRepeatPageCcuiDataLoaderFn', () => {
  it('should render data loader with expected object', async () => {
    const dataLoaderCcui = await createRepeatPageCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);

    expect(dataLoaderCcui).toStrictEqual(expectedResult);
  });
});
