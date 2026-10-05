/* eslint-disable @typescript-eslint/no-explicit-any */
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';

import { mockDlpInformation, mockGetHotelsInformation } from '../../../mocks/destination';
import createDLPPiDataLoaderFn from './data.pi';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  QueriesLogger: jest.fn().mockImplementation(() => ({
    fetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      const key = queryKey[0];
      const region = queryKey[3];
      queryFn();
      if (key === 'dlpInformation' && region == 'england/greater-london/london') {
        return Promise.resolve({ dlpInformation: mockDlpInformation });
      }
      if (key === 'dlpInformation' && region == 'england/bristol') {
        return Promise.resolve({ dlpInformation: { ...mockDlpInformation, hotels: [] } });
      }
      if (key === 'getHotelsInformation') {
        return Promise.resolve(mockGetHotelsInformation);
      }
    }),
    logQueries: jest.fn(),
  })),
  graphQLRequest: () => jest.fn().mockReturnValue(mockGetHotelsInformation),
  getGQLClient: jest.fn(),
  logger: {
    info: jest.fn(),
  },
  ID_TOKEN_COOKIE: 'id_token_cookie',
  WB_SESSION_ID: 'WB-SESSION-ID',
}));

const mockToken = 'mock-id-token';
jest.mock('cookies', () => {
  return function () {
    return {
      get: (key: string) => {
        if (key === 'id_token_cookie') return mockToken;
        if (key === 'WB-SESSION-ID') return 'mock-session-id';
        return undefined;
      },
    };
  };
});

const queryClient = new ReactQuery.QueryClient();

const mockedData = {
  language: 'en',
  country: 'GB',
  req: {
    url: '/_next/data/development/gb/hotels/england/greater-london/london.html',
  },
  query: {
    slug: ['england', 'greater-london', 'london.html'],
  },
};

const mockedDataNoHotels = {
  language: 'en',
  country: 'GB',
  req: {
    url: '/_next/data/development/gb/hotels/england/bristol.html',
  },
  query: {
    slug: ['england', 'bristol.html'],
  },
};

const expectedResult = {
  dehydratedState: {
    mutations: [],
    queries: [],
  },
  dlpQueryKey: ['dlpInformation', 'en', 'GB', 'england/greater-london/london'],
  hotelsInformationQueryKey: [
    'getHotelsInformation',
    'GB',
    'en',
    [
      'NEWPTI',
      'NEWMTI',
      'NEWTEA',
      'NEWTHY',
      'BANBRI',
      'HEAPTI',
      'LONSTM',
      'LONKIN',
      'HEIBAH',
      'BURSTA',
      'LUTREG',
      'LONEUS',
      'MANOLD',
      'BOLBAR',
      'FRAMTI',
      'FRESUD',
      'DUBSOU',
      'STUAIR',
      'BRILEW',
      'BRIQUI',
    ],
    1.6178,
    54.9783,
    true,
  ],
  searchInformationQueryKey: ['searchInformation', 'en', 'GB'],
};

const expectedResultNoHotels = {
  dehydratedState: {
    mutations: [],
    queries: [],
  },
  dlpQueryKey: ['dlpInformation', 'en', 'GB', 'england/bristol'],
  hotelsInformationQueryKey: ['getHotelsInformation', 'GB', 'en', [], 1.6178, 54.9783, true],
  searchInformationQueryKey: ['searchInformation', 'en', 'GB'],
};

describe('createDLPPiDataLoaderFn', () => {
  it('should render data loader with expected object', async () => {
    const dataLoaderPi = await createDLPPiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);

    expect(dataLoaderPi).toStrictEqual(expectedResult);
  });

  it('should render data loader without hotel information when no hotels available', async () => {
    const dataLoaderPi = await createDLPPiDataLoaderFn({
      ...mockedDataNoHotels,
      queryClient,
    } as any);

    expect(dataLoaderPi).toStrictEqual(expectedResultNoHotels);
  });
});
