import { renderHook } from '@testing-library/react';
import { analytics } from '@whitbread-eos/utils';

import { TABLE_CONFIG } from '../constants';
import { useLowestRates, RatesCriteria } from './use-lowest-rates';

// Mock the API and utils
const mockGetLowestRatesByLocationId = jest.fn();
const mockUseQueryRequest = jest.fn();
const mockTransformApiDataToHotelPrices = jest.fn();

jest.mock('@whitbread-eos/api', () => ({
  GET_LOWEST_RATES_BY_LOCATION_ID: (...args: any[]) => mockGetLowestRatesByLocationId(...args),
}));

jest.mock('@whitbread-eos/utils', () => ({
  useQueryRequest: (...args: any[]) => mockUseQueryRequest(...args),
  analytics: { update: jest.fn() },
}));

jest.mock('../helpers', () => ({
  transformApiDataToHotelPrices: (...args: any[]) => mockTransformApiDataToHotelPrices(...args),
}));

describe('useLowestRates', () => {
  const defaultCriteria: RatesCriteria = {
    locationId: 'test-location',
    arrival: '2025-08-14',
    daysRange: 7,
    showMinimumNights: false,
    page: 1,
    initialPageSize: TABLE_CONFIG.PAGE_SIZE,
    lazyLoadPageSize: TABLE_CONFIG.LAZY_LOAD_PAGE_SIZE,
    sortBy: 'DISTANCE',
  };

  const updatedCriteria: RatesCriteria = {
    locationId: '',
    arrival: '2025-08-14',
    daysRange: 7,
    showMinimumNights: false,
    page: 1,
    initialPageSize: TABLE_CONFIG.PAGE_SIZE,
    lazyLoadPageSize: TABLE_CONFIG.LAZY_LOAD_PAGE_SIZE,
    sortBy: 'DISTANCE',
  };

  const defaultCurrentDates = ['2025-08-14', '2025-08-15', '2025-08-16'];

  const mockApiResponse = {
    getLowestRatesByLocationId: {
      priceFinderOperaHotelAvailabilitiesDtoList: [
        {
          hotelCode: '1',
          hotelName: 'Test Hotel',
          links: {
            detailsPage: 'https://example.com',
          },
          availabilities: [
            { availableDate: '2025-08-14', minimumRate: 100, currency: 'GBP' },
            { availableDate: '2025-08-15', minimumRate: 120, currency: 'GBP' },
          ],
        },
      ],
      lowestMonthlyRate: {
        price: 100,
        currency: 'GBP',
      },
    },
  };

  beforeEach(() => {
    jest.clearAllMocks();

    // Set up default mocks
    mockUseQueryRequest.mockReturnValue({
      data: null,
      isLoading: false,
      isError: false,
      isSuccess: false,
      isFetching: false,
      error: null,
      refetch: jest.fn(),
    });

    mockTransformApiDataToHotelPrices.mockReturnValue([
      {
        hotelId: '1',
        hotelName: 'Test Hotel',
        hotelLink: 'https://example.com',
        prices: [
          { date: '2025-08-14', amount: 100 },
          { date: '2025-08-15', amount: 120 },
        ],
      },
    ]);
  });

  describe('Hook initialization', () => {
    it('should initialize and return expected structure', () => {
      const { result } = renderHook(() => useLowestRates(defaultCriteria, defaultCurrentDates));

      expect(result.current).toHaveProperty('data');
      expect(result.current).toHaveProperty('hotelData');
      expect(result.current).toHaveProperty('globalLowestPrice');
      expect(result.current).toHaveProperty('lowestMonthlyRate');
      expect(result.current).toHaveProperty('isLoading');
      expect(result.current).toHaveProperty('isError');
      expect(result.current).toHaveProperty('isFetching');
      expect(result.current).toHaveProperty('refetch');
    });

    it('should call useQueryRequest with simple query key', () => {
      renderHook(() => useLowestRates(defaultCriteria, defaultCurrentDates));
      expect(analytics.update).toHaveBeenCalledWith({
        searchResults: {
          priceFinder: {
            defaultDate: true,
            defaultLocation: true,
            monthSelected: 'August',
            pageNumber: 1,
            searchLocation: 'priceFinder.MVP.customConfig.defaultLocationName',
            sortBy: 'DISTANCE',
            weekSelected: ['2025-08-14', '2025-08-15', '2025-08-16'],
          },
        },
      });

      expect(mockUseQueryRequest).toHaveBeenCalledWith(
        [
          'lowestRatesByLocationId',
          'test-location',
          '2025-08-14',
          'DISTANCE',
          undefined,
          1,
          20,
          20,
        ],
        expect.any(Function),
        { criteria: defaultCriteria },
        { enabled: true }
      );
    });
  });

  describe('Data transformation', () => {
    it('should transform hotel data when API data is available', () => {
      mockUseQueryRequest.mockReturnValue({
        data: mockApiResponse,
        isLoading: false,
        isError: false,
        isSuccess: true,
        isFetching: false,
        error: null,
        refetch: jest.fn(),
      });

      const { result } = renderHook(() => useLowestRates(defaultCriteria, defaultCurrentDates));

      expect(mockTransformApiDataToHotelPrices).toHaveBeenCalledWith(
        mockApiResponse.getLowestRatesByLocationId.priceFinderOperaHotelAvailabilitiesDtoList,
        defaultCurrentDates
      );

      expect(result.current.hotelData).toEqual([
        {
          hotelId: '1',
          hotelName: 'Test Hotel',
          hotelLink: 'https://example.com',
          prices: [
            { date: '2025-08-14', amount: 100 },
            { date: '2025-08-15', amount: 120 },
          ],
        },
      ]);

      expect(result.current.globalLowestPrice).toBe(100);
      expect(result.current.lowestMonthlyRate).toEqual({ price: 100, currency: 'GBP' });
    });

    it('should return empty array when no API data', () => {
      mockUseQueryRequest.mockReturnValue({
        data: null,
        isLoading: false,
        isError: false,
        isSuccess: false,
        isFetching: false,
        error: null,
        refetch: jest.fn(),
      });

      const { result } = renderHook(() => useLowestRates(defaultCriteria, defaultCurrentDates));

      expect(result.current.hotelData).toEqual([]);
      expect(result.current.globalLowestPrice).toBeNull();
      expect(result.current.lowestMonthlyRate).toBeNull();
    });
  });

  describe('Options handling', () => {
    it('should respect enabled option', () => {
      renderHook(() => useLowestRates(updatedCriteria, defaultCurrentDates, { enabled: false }));

      expect(mockUseQueryRequest).toHaveBeenCalledWith(
        ['lowestRatesByLocationId', '', '2025-08-14', 'DISTANCE', undefined, 1, 20, 20],
        expect.any(Function),
        { criteria: updatedCriteria },
        { enabled: false }
      );
    });
  });

  describe('Query key consistency', () => {
    it('should generate consistent query keys for same criteria', () => {
      const { rerender } = renderHook(
        ({ criteria }) => useLowestRates(criteria, defaultCurrentDates),
        { initialProps: { criteria: defaultCriteria } }
      );

      const firstCall = mockUseQueryRequest.mock.calls[0][0];
      mockUseQueryRequest.mockClear();

      rerender({ criteria: defaultCriteria });

      const secondCall = mockUseQueryRequest.mock.calls[0][0];
      expect(firstCall).toEqual(secondCall);
    });
  });
});
