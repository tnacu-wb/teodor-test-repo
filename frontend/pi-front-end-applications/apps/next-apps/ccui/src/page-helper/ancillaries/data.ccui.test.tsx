// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore-file
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';

import createAncillariesCcuiDataLoaderFn from './data.ccui';

//region Mock Objects
const getBookingInformationData = {
  bookingInformation: {
    hotelId: 'LONKIN',
    reservationByIdList: [
      {
        roomStay: {
          arrivalDate: '2023-02-23',
          departureDate: '2023-02-24',
        },
      },
    ],
    bookingFlowId: 'booking-hub',
  },
};

const mockedData = {
  session: {
    tokenSet: {
      accessToken: 'asasasGderg12312',
    },
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
};

const expectedResult = {
  accessToken: 'asasasGderg12312',
  roles: undefined,
  user: {},
  biQueryInput: {
    basketReference: '12',
    bookingChannelCriteria: {
      channel: 'CCUI',
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
    channel: 'CCUI',
    childrenNumber: 0,
    country: 'GB',
    endDate: '2023-02-24',
    hotelId: 'LONKIN',
    language: 'en',
    nightsNumber: 1,
    startDate: '2023-02-23',
  },
};

const expectedResultDE = {
  accessToken: 'asasasGderg12312',
  roles: undefined,
  user: {},
  biQueryInput: {
    basketReference: '12',
    bookingChannelCriteria: {
      channel: 'CCUI',
      language: 'DE',
      subchannel: 'WEB',
    },
    country: 'GB',
    language: 'de',
  },
  dehydratedState: {
    mutations: [],
    queries: [],
  },
  hiQueryInput: {
    country: 'GB',
    hotelId: 'LONKIN',
    language: 'de',
  },
  pcksQueryInput: {
    adultsNumber: 0,
    basketReferenceId: '12',
    bookingFlowId: 'booking-hub',
    channel: 'CCUI',
    childrenNumber: 0,
    country: 'GB',
    endDate: '2023-02-24',
    hotelId: 'LONKIN',
    language: 'de',
    nightsNumber: 1,
    startDate: '2023-02-23',
  },
};

const expectedResultWithBasketUndefined = {
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

const queryClient = new ReactQuery.QueryClient();
const jsonValidMock = jest.fn();
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

const decodeResultsMock = jest.fn().mockResolvedValue({
  hotelId: 'LONEUS',
  startDate: '2024-10-17',
  endDate: '2024-10-18',
  nightsNumber: 1,
  rateCode: 'FLEXRATE',
  adultsNumber: 1,
  childrenNumber: 0,
  reservationId: 'AKU-3a4c09ae-7199-4472-bcc0-3379afcab379',
  bookingFlowId: 'booking-a1',
});
//endregion

//region Jest Mock
jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: () => null,
    set: () => null,
  }));
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
        case 'GetStaticContent':
          return {};
      }
    }),
    logQueries: jest.fn(),
  })),
  logger: {
    info: jest.fn(),
    error: jest.fn(),
  },
  graphQLRequest: jest.fn(),
  decodeFromBase64: () => decodeResultsMock(),
  isJsonValid: () => jsonValidMock(),
  getNightsNumber: jest.fn().mockImplementation(() => 1),
  getMaxValueFromRoomStaysBC: jest.fn().mockImplementation(() => 1),
}));
//endregion

//region Unit Tests
describe('createAncillariesCcuiDataLoaderFn', () => {
  let actualMockGetItem: jest.Mock;
  let actualMockSetItem: jest.Mock;

  beforeEach(() => {
    // Reset mockedData.query to original values
    mockedData.query = {
      reservationId: '12',
      BRAND: 'PI',
    };
    mockedData.language = 'en';

    mockRedisGetItemValue = mockBasketDetailsFromRedis;
    jsonValidMock.mockReturnValue(true);

    // Get references to the actual mocks from the jest.mock
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    const utilsServer = require('@whitbread-eos/utils/server');
    const mockInstance = utilsServer.RedisStorageServer.getInstance();
    actualMockGetItem = mockInstance.getItem as jest.Mock;
    actualMockSetItem = mockInstance.setItem as jest.Mock;

    actualMockGetItem.mockResolvedValue(mockRedisGetItemValue);
    actualMockSetItem.mockResolvedValue(undefined);
  });

  it('should render data loader with expected object', async () => {
    jsonValidMock.mockReturnValue(false);
    const dataLoaderPi = await createAncillariesCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);
    expect(dataLoaderPi).toStrictEqual(expectedResult);
  });

  it('should render data loader with expected object and basketReference undefined', async () => {
    mockedData.query.reservationId = '';
    jsonValidMock.mockReturnValue(false);
    mockRedisGetItemValue = null;
    actualMockGetItem.mockResolvedValue(null);

    const dataLoaderPi = await createAncillariesCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);
    expect(dataLoaderPi).toStrictEqual(expectedResultWithBasketUndefined);
  });
  it('should render data loader with cookie populated', async () => {
    mockedData.query.reservationId = '12';
    jsonValidMock.mockReturnValue(true);
    decodeResultsMock.mockReturnValue(
      JSON.stringify({
        hotelId: 'LONKIN',
        startDate: '2023-02-23',
        endDate: '2023-02-24',
        nightsNumber: 1,
        rateCode: 'FLEXRATE',
        adultsNumber: 0,
        childrenNumber: 0,
        reservationId: 'AKU-3a4c09ae-7199-4472-bcc0-3379afcab379',
        bookingFlowId: 'booking-hub',
      })
    );

    const dataLoaderPi = await createAncillariesCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);
    expect(dataLoaderPi).toStrictEqual(expectedResult);
  });
  it('should render data loader with expected object for language DE', async () => {
    jsonValidMock.mockReturnValue(false);
    mockedData.language = 'de';
    const dataLoaderPi = await createAncillariesCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);
    expect(dataLoaderPi).toStrictEqual(expectedResultDE);
  });

  describe('hasCachedBasketDetails - Redis cache logic', () => {
    it('should set hasCachedBasketDetails to true when Redis returns valid JSON', async () => {
      // Arrange
      mockRedisGetItemValue = mockBasketDetailsFromRedis;
      jsonValidMock.mockReturnValue(true);
      actualMockGetItem.mockResolvedValue(mockRedisGetItemValue);

      // Act
      const result = await createAncillariesCcuiDataLoaderFn({
        ...mockedData,
        queryClient,
      } as any);

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
      jsonValidMock.mockReturnValue(false);
      actualMockGetItem.mockResolvedValue(null);

      // Act
      const result = await createAncillariesCcuiDataLoaderFn({
        ...mockedData,
        queryClient,
      } as any);

      // Assert
      expect(result).toBeDefined();
      expect(actualMockGetItem).toHaveBeenCalled();
      expect(result.dehydratedState).toBeDefined();
    });

    it('should set hasCachedBasketDetails to false when Redis returns invalid JSON', async () => {
      // Arrange
      mockRedisGetItemValue = 'invalid-json-string';
      jsonValidMock.mockReturnValue(false);
      actualMockGetItem.mockResolvedValue(mockRedisGetItemValue);

      // Act
      const result = await createAncillariesCcuiDataLoaderFn({
        ...mockedData,
        queryClient,
      } as any);

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
      const result = await createAncillariesCcuiDataLoaderFn({
        ...mockedData,
        queryClient,
      } as any);

      // Assert
      expect(result).toBeDefined();
      expect(utilsServer.RedisStorageServer.getInstance).toHaveBeenCalled();
      expect(logger.error).toHaveBeenCalledWith(
        { error: redisError },
        'CCUI_ANCILLARIES_REDIS_ERROR'
      );
    });

    it('should handle Redis getItem error gracefully', async () => {
      // Arrange
      actualMockGetItem.mockRejectedValueOnce(new Error('Redis getItem failed'));
      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const { logger } = require('@whitbread-eos/utils');

      // Act
      const result = await createAncillariesCcuiDataLoaderFn({
        ...mockedData,
        queryClient,
      } as any);

      // Assert
      expect(result).toBeDefined();
      expect(actualMockGetItem).toHaveBeenCalled();
      expect(logger.error).toHaveBeenCalledWith(
        { error: expect.any(Error) },
        'CCUI_ANCILLARIES_REDIS_ERROR'
      );
    });
  });
});
//endregion
