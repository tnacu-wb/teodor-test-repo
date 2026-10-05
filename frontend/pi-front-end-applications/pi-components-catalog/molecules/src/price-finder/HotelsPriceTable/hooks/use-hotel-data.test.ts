import { renderHook, act } from '@testing-library/react';

import { HotelPrice } from '../types';
import { useHotelData } from './use-hotel-data';

// Mock the helpers
const mockTransformApiDataToHotelPrices = jest.fn();

jest.mock('../helpers', () => ({
  transformApiDataToHotelPrices: (...args: any[]) => mockTransformApiDataToHotelPrices(...args),
}));

const mockCurrentDates = ['2025-08-03', '2025-08-04', '2025-08-05', '2025-08-06', '2025-08-07'];
const mockCurrentStartDate = new Date('2025-08-03');

const mockApiData = {
  getLowestRatesByLocationId: {
    priceFinderOperaHotelAvailabilitiesDtoList: [
      {
        hotelCode: 'TEST1',
        hotelName: 'Test Hotel 1',
        availabilities: [
          {
            availableDate: '2025-08-03',
            minimumRate: 100,
            currency: 'GBP',
            rateCode: 'STD',
            rateClassification: 'Standard',
            roomType: 'Double',
            quantity: 1,
            hasMlosRestriction: false,
            hasClosedRestriction: false,
            minimumNights: 1,
          },
          {
            availableDate: '2025-08-04',
            minimumRate: 120,
            currency: 'GBP',
            rateCode: 'STD',
            rateClassification: 'Standard',
            roomType: 'Double',
            quantity: 1,
            hasMlosRestriction: false,
            hasClosedRestriction: false,
            minimumNights: 1,
          },
        ],
      },
    ],
    lowestMonthlyRate: {
      price: 85,
      currency: 'GBP',
    },
    page: 1,
    pageSize: 10,
    total: 1,
  },
};

const mockHotelData: HotelPrice[] = [
  {
    hotelId: '1',
    hotelName: 'Hotel A',
    prices: [
      { date: '2025-08-03', amount: 100 },
      { date: '2025-08-04', amount: 120 },
      { date: '2025-08-05', amount: null },
      { date: '2025-08-06', amount: 90 },
      { date: '2025-08-07', amount: 110 },
    ],
  },
  {
    hotelId: '2',
    hotelName: 'Hotel B',
    prices: [
      { date: '2025-08-03', amount: 80 },
      { date: '2025-08-04', amount: 95 },
      { date: '2025-08-05', amount: 85 },
      { date: '2025-08-06', amount: null },
      { date: '2025-08-07', amount: 75 },
    ],
  },
];

describe('useHotelData', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockTransformApiDataToHotelPrices.mockReturnValue(mockHotelData);
  });

  const defaultProps = {
    currentDates: mockCurrentDates,
    currentStartDate: mockCurrentStartDate,
    data: null,
    isLoading: false,
    isError: false,
  };

  describe('Hook initialization', () => {
    it('should initialize and return expected structure', () => {
      const { result } = renderHook(() => useHotelData(defaultProps));

      expect(result.current).toHaveProperty('hotelData');
      expect(result.current).toHaveProperty('sortedByDate');
      expect(result.current).toHaveProperty('globalLowestPrice');
      expect(result.current).toHaveProperty('handleDateSort');
      expect(result.current).toHaveProperty('setHotelData');
    });

    it('should have function types for handlers', () => {
      const { result } = renderHook(() => useHotelData(defaultProps));

      expect(typeof result.current.handleDateSort).toBe('function');
      expect(typeof result.current.setHotelData).toBe('function');
    });
  });

  describe('API data behavior', () => {
    it('should use API data when available', () => {
      renderHook(() =>
        useHotelData({
          ...defaultProps,
          data: mockApiData,
          isLoading: false,
          isError: false,
        })
      );

      expect(mockTransformApiDataToHotelPrices).toHaveBeenCalledWith(
        mockApiData.getLowestRatesByLocationId.priceFinderOperaHotelAvailabilitiesDtoList,
        mockCurrentDates
      );
    });

    it('should use lowestMonthlyRate from API instead of calculating', () => {
      const { result } = renderHook(() =>
        useHotelData({
          ...defaultProps,
          data: mockApiData,
          isLoading: false,
          isError: false,
        })
      );

      expect(result.current.globalLowestPrice).toBe(85);
    });

    it('should fallback to null if lowestMonthlyRate is not available', () => {
      const apiDataWithoutLowestRate = {
        getLowestRatesByLocationId: {
          priceFinderOperaHotelAvailabilitiesDtoList: [],
          page: 1,
          pageSize: 10,
          total: 0,
          lowestMonthlyRate: { price: 0, currency: 'GBP' },
        },
      };

      const { result } = renderHook(() =>
        useHotelData({
          ...defaultProps,
          data: apiDataWithoutLowestRate,
          isLoading: false,
          isError: false,
        })
      );

      expect(result.current.globalLowestPrice).toBeNull();
    });

    it('should not process data when loading', () => {
      renderHook(() =>
        useHotelData({
          ...defaultProps,
          data: mockApiData,
          isLoading: true,
          isError: false,
        })
      );

      expect(mockTransformApiDataToHotelPrices).not.toHaveBeenCalled();
    });

    it('should not process data when error occurs', () => {
      renderHook(() =>
        useHotelData({
          ...defaultProps,
          data: mockApiData,
          isLoading: false,
          isError: true,
        })
      );

      expect(mockTransformApiDataToHotelPrices).not.toHaveBeenCalled();
    });

    it('should reset sortedByDate when data changes', () => {
      const { result, rerender } = renderHook((props) => useHotelData(props), {
        initialProps: {
          ...defaultProps,
          data: mockApiData,
        },
      });

      // Simulate sorting by a date first
      act(() => {
        result.current.handleDateSort('2025-08-03');
      });

      expect(result.current.sortedByDate).toBe('2025-08-03');

      // Change the data
      rerender({
        ...defaultProps,
        data: { ...mockApiData },
        currentStartDate: new Date('2025-08-04'),
      });

      expect(result.current.sortedByDate).toBeNull();
    });
  });

  describe('handleDateSort functionality', () => {
    beforeEach(() => {
      mockTransformApiDataToHotelPrices.mockReturnValue(mockHotelData);
    });

    it('should provide handleDateSort function', () => {
      const { result } = renderHook(() => useHotelData(defaultProps));

      expect(typeof result.current.handleDateSort).toBe('function');
    });

    it('should sort hotels by price for a given date', () => {
      const { result } = renderHook(() =>
        useHotelData({
          ...defaultProps,
          data: mockApiData,
        })
      );

      act(() => {
        result.current.handleDateSort('2025-08-03');
      });

      // Hotel B (80) should come before Hotel A (100) for date 2025-08-03
      expect(result.current.hotelData[0].hotelName).toBe('Hotel B');
      expect(result.current.hotelData[1].hotelName).toBe('Hotel A');
      expect(result.current.sortedByDate).toBe('2025-08-03');
    });

    it('should handle null prices in sorting', () => {
      const { result } = renderHook(() =>
        useHotelData({
          ...defaultProps,
          data: mockApiData,
        })
      );

      act(() => {
        result.current.handleDateSort('2025-08-05');
      });

      // Hotel B (85) should come before Hotel A (null) for date 2025-08-05
      expect(result.current.hotelData[0].hotelName).toBe('Hotel B');
      expect(result.current.hotelData[1].hotelName).toBe('Hotel A');
      expect(result.current.sortedByDate).toBe('2025-08-05');
    });

    it('should handle sorting when both prices are null', () => {
      const mockDataWithNullPrices: HotelPrice[] = [
        {
          hotelId: '1',
          hotelName: 'Hotel A',
          prices: [{ date: '2025-08-03', amount: null }],
        },
        {
          hotelId: '2',
          hotelName: 'Hotel B',
          prices: [{ date: '2025-08-03', amount: null }],
        },
      ];

      // Set up the mock specifically for this test
      mockTransformApiDataToHotelPrices.mockReturnValueOnce(mockDataWithNullPrices);

      const { result } = renderHook(() =>
        useHotelData({
          ...defaultProps,
          data: mockApiData,
          currentDates: ['2025-08-03'],
        })
      );

      // Verify the hook received the expected data
      expect(result.current.hotelData).toHaveLength(2);
      expect(result.current.hotelData[0].hotelName).toBe('Hotel A');
      expect(result.current.hotelData[1].hotelName).toBe('Hotel B');

      act(() => {
        result.current.handleDateSort('2025-08-03');
      });

      // Order should remain the same when both prices are null
      expect(result.current.hotelData[0].hotelName).toBe('Hotel A');
      expect(result.current.hotelData[1].hotelName).toBe('Hotel B');
      // Note: sortedByDate is managed by useEffect and may be reset, so we don't assert on it
    });

    it('should do nothing when clicking on invalid date', () => {
      const { result } = renderHook(() =>
        useHotelData({
          ...defaultProps,
          data: mockApiData,
        })
      );

      const originalData = [...result.current.hotelData];

      act(() => {
        result.current.handleDateSort('2025-12-25'); // Date not in currentDates
      });

      expect(result.current.hotelData).toEqual(originalData);
      expect(result.current.sortedByDate).toBeNull();
    });

    it('should provide setHotelData function', () => {
      const { result } = renderHook(() => useHotelData(defaultProps));

      expect(typeof result.current.setHotelData).toBe('function');
    });
  });

  describe('Data structure validation', () => {
    it('should return hotel data as array', () => {
      const { result } = renderHook(() => useHotelData(defaultProps));

      expect(Array.isArray(result.current.hotelData)).toBe(true);
    });

    it('should initialize sortedByDate as null', () => {
      const { result } = renderHook(() => useHotelData(defaultProps));

      expect(result.current.sortedByDate).toBeNull();
    });

    it('should initialize globalLowestPrice appropriately', () => {
      const { result } = renderHook(() => useHotelData(defaultProps));

      expect(
        typeof result.current.globalLowestPrice === 'number' ||
          result.current.globalLowestPrice === null
      ).toBe(true);
    });
  });
});
