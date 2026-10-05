// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore-file
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { ServerResponse, IncomingMessage } from 'http';

import createAncillariesPiDataLoaderFn from './data.pi';

//region Mock Objects
const getBookingInformationData = {
  bookingInformation: {
    hotelId: 'LONKIN',
    bookingFlowId: 'booking-hub',
    reservationByIdList: [
      {
        roomStay: {
          arrivalDate: '2023-02-23',
          departureDate: '2023-02-24',
        },
      },
    ],
  },
};

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
  res: {} as ServerResponse,
  req: { url: undefined, cookies: {} } as IncomingMessage & {
    cookies: Partial<{ [key: string]: string }>;
  },
  logger: {
    info: jest.fn(),
  },
  staticData: '',
};

const expectedResult = {
  biQueryInput: {
    basketReference: '12',
    bookingChannelCriteria: {
      channel: 'PI',
      language: 'EN',
      subchannel: 'WEB',
    },
    country: 'GB',
    language: 'en',
  },
  dehydratedState: {
    mutations: [],
    queries: [],
  },
  hiQueryInput: {
    country: 'GB',
    hotelId: 'LONKIN',
    language: 'en',
  },
  pcksQueryInput: {
    adultsNumber: 0,
    basketReferenceId: '12',
    bookingFlowId: 'booking-hub',
    channel: 'PI',
    childrenNumber: 0,
    country: 'GB',
    endDate: '2023-02-24',
    hotelId: 'LONKIN',
    language: 'en',
    nightsNumber: 1,
    startDate: '2023-02-23',
  },
};

const expectedResultWithDefaultValue = {
  ...expectedResult,
  biQueryInput: {
    ...expectedResult.biQueryInput,
    basketReference: '',
  },
  pcksQueryInput: {
    ...expectedResult.pcksQueryInput,
    basketReferenceId: '',
  },
};

let mockIsJsonValidValue = false;
let mockRedisGetItemValue: string | null = null;

const mockBasketDetailsFromRedis = JSON.stringify({
  bookingFlowId: 'booking-hub',
  hotelId: 'LONKIN',
  rateCode: 'FLEXRATE',
  nightsNumber: 1,
  startDate: '2023-02-23',
  endDate: '2023-02-24',
  childrenNumber: 0,
  adultsNumber: 0,
  reservationId: '12',
});

const queryClient = new ReactQuery.QueryClient();
//endregion

const mockToken = 'mock-id-token';
const mockSetCookie = jest.fn();
//region Jest Mock
const mockCookies = {
  get: (key: string) => {
    if (key === 'id_token_cookie') return mockToken;
    if (key === 'WB-SESSION-ID') return 'mock-session-id';
    return undefined;
  },
  set: mockSetCookie,
};
jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => mockCookies);
});

jest.mock('@whitbread-eos/utils/server', () => {
  const mockGetItem = jest.fn();
  const mockSetItem = jest.fn();
  return {
    RedisStorageServer: {
      getInstance: jest.fn().mockReturnValue({
        getItem: mockGetItem,
        setItem: mockSetItem,
      }),
    },
    RedisKeyPrefix: {
      HRS: 'Hotel-Reservation-Entity-Service::ReservationCache',
    },
  };
});

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  QueriesLogger: jest.fn().mockImplementation(() => ({
    fetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      queryFn();
      return getBookingInformationData;
    }),
    prefetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      queryFn();
      switch (queryKey[0]) {
        case 'GetPackages':
          return {};
        case 'GetHotelInformation':
          return {};
      }
    }),
    logQueries: jest.fn(),
  })),
  isJsonValid: () => mockIsJsonValidValue,
  getNightsNumber: jest.fn().mockImplementation(() => 1),
  getMaxValueFromRoomStays: jest.fn().mockImplementation(() => 0),
  logger: {
    info: jest.fn(),
    error: jest.fn(),
  },
  graphQLRequest: jest.fn(),
  getGQLClient: jest.fn(),
}));
//endregion

//region Unit Tests
describe('createAncillariesPiDataLoaderFn', () => {
  let actualMockGetItem: jest.Mock;
  let actualMockSetItem: jest.Mock;

  beforeEach(() => {
    jest.clearAllMocks();
    mockRedisGetItemValue = mockBasketDetailsFromRedis;
    mockIsJsonValidValue = true;

    // Get references to the actual mocks from the jest.mock
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    const { RedisStorageServer } = require('@whitbread-eos/utils/server');
    const mockInstance = RedisStorageServer.getInstance();
    actualMockGetItem = mockInstance.getItem as jest.Mock;
    actualMockSetItem = mockInstance.setItem as jest.Mock;

    actualMockGetItem.mockResolvedValue(mockRedisGetItemValue);
    actualMockSetItem.mockResolvedValue(undefined);
  });
  it('should render data loader with expected object', async () => {
    const dataLoaderPi = await createAncillariesPiDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderPi).toStrictEqual(expectedResult);
  });

  it('should render data loader with expected object and basketReference is defaultValue', async () => {
    mockedData.query.reservationId = '';
    const dataLoaderPi = await createAncillariesPiDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderPi).toStrictEqual(expectedResultWithDefaultValue);
  });

  describe('hasCachedBasketDetails - Redis cache logic', () => {
    it('should set hasCachedBasketDetails to true when Redis returns valid JSON', async () => {
      // Arrange
      mockRedisGetItemValue = mockBasketDetailsFromRedis;
      mockIsJsonValidValue = true;
      actualMockGetItem.mockResolvedValue(mockRedisGetItemValue);
      mockedData.query.reservationId = '12';

      // Act
      const result = await createAncillariesPiDataLoaderFn({
        ...mockedData,
        queryClient,
      });

      // Assert
      expect(result).toBeDefined();
      expect(result.dehydratedState).toBeDefined();
      expect(actualMockGetItem).toHaveBeenCalledWith(
        'Hotel-Reservation-Entity-Service::ReservationCache::12'
      );
    });

    it('should set hasCachedBasketDetails to false when Redis returns null', async () => {
      // Arrange
      mockRedisGetItemValue = null;
      mockIsJsonValidValue = false;
      actualMockGetItem.mockResolvedValue(null);

      // Act
      const result = await createAncillariesPiDataLoaderFn({
        ...mockedData,
        queryClient,
      });

      // Assert
      expect(result).toBeDefined();
      expect(actualMockGetItem).toHaveBeenCalled();
      expect(result.dehydratedState).toBeDefined();
    });

    it('should set hasCachedBasketDetails to false when Redis returns invalid JSON', async () => {
      // Arrange
      mockRedisGetItemValue = 'invalid-json-string';
      mockIsJsonValidValue = false;
      actualMockGetItem.mockResolvedValue(mockRedisGetItemValue);

      // Act
      const result = await createAncillariesPiDataLoaderFn({
        ...mockedData,
        queryClient,
      });

      // Assert
      expect(result).toBeDefined();
      expect(actualMockGetItem).toHaveBeenCalled();
    });

    it('should handle Redis getInstance error gracefully and continue execution', async () => {
      // Arrange
      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const utilsServer = require('@whitbread-eos/utils/server');
      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const { logger } = require('@whitbread-eos/utils');

      const redisError = new Error('Redis connection error');
      utilsServer.RedisStorageServer.getInstance.mockImplementationOnce(() => {
        throw redisError;
      });

      // Act
      const result = await createAncillariesPiDataLoaderFn({
        ...mockedData,
        queryClient,
      });

      // Assert
      expect(result).toBeDefined();
      expect(utilsServer.RedisStorageServer.getInstance).toHaveBeenCalled();
      expect(logger.error).toHaveBeenCalledWith(
        { error: redisError },
        'PI_ANCILLARIES_REDIS_ERROR'
      );
    });

    it('should handle Redis getItem error gracefully', async () => {
      // Arrange
      actualMockGetItem.mockRejectedValueOnce(new Error('Redis getItem failed'));
      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const { logger } = require('@whitbread-eos/utils');

      // Act
      const result = await createAncillariesPiDataLoaderFn({
        ...mockedData,
        queryClient,
      });

      // Assert
      expect(result).toBeDefined();
      expect(actualMockGetItem).toHaveBeenCalled();
      expect(logger.error).toHaveBeenCalledWith(
        { error: expect.any(Error) },
        'PI_ANCILLARIES_REDIS_ERROR'
      );
    });
  });
});
//endregion
