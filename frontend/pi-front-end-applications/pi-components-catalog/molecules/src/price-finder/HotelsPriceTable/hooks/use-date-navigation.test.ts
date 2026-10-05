import { renderHook, act } from '@testing-library/react';

import { useDateNavigation } from './use-date-navigation';

// Mock the current date to ensure consistent test results
const mockCurrentDate = '2025-08-03';
jest.useFakeTimers();
jest.setSystemTime(new Date(mockCurrentDate));

// Mock the constants to make tests predictable
jest.mock('../constants', () => ({
  TABLE_CONFIG: {
    DAYS_TO_SHOW: 7,
    SCROLL_OFFSET: 7,
    MAX_BACKWARD_DAYS: 0,
  },
}));

// Mock the generateDates helper
jest.mock('../helpers', () => ({
  generateDates: jest.fn((startDate: Date, daysToShow: number): string[] => {
    const dates: string[] = [];
    for (let i = 0; i < daysToShow; i++) {
      const date = new Date(startDate);
      date.setDate(date.getDate() + i);
      dates.push(date.toISOString().split('T')[0]);
    }
    return dates;
  }),
}));

const satelliteTrack = jest.fn();
const satelliteLoaded = jest.fn();

describe('useDateNavigation', () => {
  afterAll(() => {
    jest.useRealTimers();
  });

  beforeEach(() => {
    (window as any)._satellite = { track: satelliteTrack };
    (window as any).__satelliteLoaded = { track: satelliteLoaded };
  });

  describe('Initial state', () => {
    it('should initialize with startOffset of 0', () => {
      const { result } = renderHook(() => useDateNavigation());

      expect(result.current.startOffset).toBe(0);
    });

    it('should initialize with current date as start date', () => {
      const { result } = renderHook(() => useDateNavigation());

      const expectedDate = new Date(mockCurrentDate);
      expect(result.current.currentStartDate).toEqual(expectedDate);
    });

    it('should initialize with 7 dates starting from current date', () => {
      const { result } = renderHook(() => useDateNavigation());

      expect(result.current.currentDates).toEqual([
        '2025-08-03',
        '2025-08-04',
        '2025-08-05',
        '2025-08-06',
        '2025-08-07',
        '2025-08-08',
        '2025-08-09',
      ]);
    });

    it('should initialize arrival as ISO string of current date', () => {
      const { result } = renderHook(() => useDateNavigation());

      expect(result.current.arrival).toBe('2025-08-03');
    });

    it('should initialize with scroll left disabled', () => {
      const { result } = renderHook(() => useDateNavigation());

      expect(result.current.isScrollLeftDisabled).toBe(true);
    });
  });

  describe('Integration with constants', () => {
    it('should use TABLE_CONFIG.DAYS_TO_SHOW for generating dates', () => {
      const { result } = renderHook(() => useDateNavigation());

      // Should generate 7 dates as per mocked TABLE_CONFIG
      expect(result.current.currentDates).toHaveLength(7);
    });

    it('should use TABLE_CONFIG.MAX_BACKWARD_DAYS for boundary check', () => {
      const { result } = renderHook(() => useDateNavigation());

      // Should start at 0 (MAX_BACKWARD_DAYS) and have scroll left disabled
      expect(result.current.startOffset).toBe(0);
      expect(result.current.isScrollLeftDisabled).toBe(true);
    });
  });

  describe('Date generation', () => {
    it('should generate correct sequential dates', () => {
      const { result } = renderHook(() => useDateNavigation());

      expect(result.current.currentDates).toEqual([
        '2025-08-03',
        '2025-08-04',
        '2025-08-05',
        '2025-08-06',
        '2025-08-07',
        '2025-08-08',
        '2025-08-09',
      ]);
    });
  });

  describe('Function availability', () => {
    it('should provide navigation functions', () => {
      const { result } = renderHook(() => useDateNavigation());

      expect(typeof result.current.handleScrollLeft).toBe('function');
      expect(typeof result.current.handleScrollRight).toBe('function');
    });

    it('should provide all expected properties', () => {
      const { result } = renderHook(() => useDateNavigation());

      expect(result.current).toHaveProperty('startOffset');
      expect(result.current).toHaveProperty('currentStartDate');
      expect(result.current).toHaveProperty('currentDates');
      expect(result.current).toHaveProperty('arrival');
      expect(result.current).toHaveProperty('handleScrollLeft');
      expect(result.current).toHaveProperty('handleScrollRight');
      expect(result.current).toHaveProperty('isScrollLeftDisabled');
    });
  });

  describe('Scrolling behavior', () => {
    it('should enable scroll left after scrolling right', () => {
      const { result } = renderHook(() => useDateNavigation());

      // Initially scroll left should be disabled
      expect(result.current.isScrollLeftDisabled).toBe(true);
      expect(result.current.startOffset).toBe(0);

      // Scroll right once
      act(() => {
        result.current.handleScrollRight();
      });

      // Now scroll left should be enabled
      expect(result.current.startOffset).toBe(7);
      expect(result.current.isScrollLeftDisabled).toBe(false);
    });

    it('should disable scroll left when scrolling back to start', () => {
      const { result } = renderHook(() => useDateNavigation());

      // Scroll right first
      act(() => {
        result.current.handleScrollRight();
      });

      expect(result.current.startOffset).toBe(7);
      expect(result.current.isScrollLeftDisabled).toBe(false);

      // Scroll left back to start
      act(() => {
        result.current.handleScrollLeft();
      });

      expect(result.current.startOffset).toBe(0);
      expect(result.current.isScrollLeftDisabled).toBe(true);
    });

    it('should respect MAX_BACKWARD_DAYS boundary', () => {
      const { result } = renderHook(() => useDateNavigation());

      // Try to scroll left from initial position
      act(() => {
        result.current.handleScrollLeft();
      });

      // Should remain at boundary (MAX_BACKWARD_DAYS = 0)
      expect(result.current.startOffset).toBe(0);
      expect(result.current.isScrollLeftDisabled).toBe(true);
    });

    it('should not scroll right beyond max allowed date', () => {
      const { result } = renderHook(() => useDateNavigation());

      // Scroll right many times to approach the limit
      // Max allowed is today + 364 days, so we need to scroll enough times
      // Each scroll is 7 days, so ~52 scrolls would get us close
      for (let i = 0; i < 60; i++) {
        act(() => {
          result.current.handleScrollRight();
        });

        // If scrolling is disabled, we've hit the limit
        if (result.current.isScrollRightDisabled) {
          break;
        }
      }

      // Should be disabled when we approach the 364-day limit
      expect(result.current.isScrollRightDisabled).toBe(true);
    });
  });

  describe('External date behavior', () => {
    it('should use external date as base when provided', () => {
      const externalDate = '2025-09-15';
      const { result } = renderHook(() => useDateNavigation(undefined, externalDate));

      const expectedDate = new Date(externalDate);
      expect(result.current.currentStartDate).toEqual(expectedDate);
      expect(result.current.arrival).toBe(externalDate);
    });

    it('should reset to show 1st of month when external date changes', () => {
      const { result, rerender } = renderHook(
        ({ externalDate }) => useDateNavigation(undefined, externalDate),
        { initialProps: { externalDate: '2025-09-15' } }
      );

      // Scroll right first to change offset
      act(() => {
        result.current.handleScrollRight();
      });

      expect(result.current.startOffset).toBe(7);

      // Change external date - should reset offset to 0
      rerender({ externalDate: '2025-10-01' });

      expect(result.current.startOffset).toBe(0);
      expect(result.current.currentStartDate).toEqual(new Date('2025-10-01'));
    });

    it('should not reset offset when external date stays the same', () => {
      const externalDate = '2025-09-15';
      const { result, rerender } = renderHook(
        ({ externalDate }) => useDateNavigation(undefined, externalDate),
        { initialProps: { externalDate } }
      );

      // Scroll right to change offset
      act(() => {
        result.current.handleScrollRight();
      });

      expect(result.current.startOffset).toBe(7);

      // Re-render with same external date - offset should remain
      rerender({ externalDate });

      expect(result.current.startOffset).toBe(7);
    });

    it('should use external date for scroll calculations', () => {
      const externalDate = '2025-12-01';
      const { result } = renderHook(() => useDateNavigation(undefined, externalDate));

      // Scroll right should use external date as base
      act(() => {
        result.current.handleScrollRight();
      });

      const expectedDate = new Date(externalDate);
      expectedDate.setDate(expectedDate.getDate() + 7); // SCROLL_OFFSET
      expect(result.current.currentStartDate).toEqual(expectedDate);
    });

    it('should handle external date in scroll left calculations', () => {
      const externalDate = '2025-12-15';
      const { result } = renderHook(() => useDateNavigation(undefined, externalDate));

      // First scroll right to enable scroll left
      act(() => {
        result.current.handleScrollRight();
      });

      // Then scroll left should use external date as base
      act(() => {
        result.current.handleScrollLeft();
      });

      const expectedDate = new Date(externalDate);
      expect(result.current.currentStartDate).toEqual(expectedDate);
    });
  });

  describe('onDateChange callback behavior', () => {
    it('should call onDateChange when scrolling right', () => {
      const mockOnDateChange = jest.fn();
      const { result } = renderHook(() => useDateNavigation(mockOnDateChange));

      act(() => {
        result.current.handleScrollRight();
      });

      expect(mockOnDateChange).toHaveBeenCalledWith('2025-08-10');
    });

    it('should call onDateChange when scrolling left', () => {
      const mockOnDateChange = jest.fn();
      const { result } = renderHook(() => useDateNavigation(mockOnDateChange));

      // First scroll right to enable scroll left
      act(() => {
        result.current.handleScrollRight();
      });

      mockOnDateChange.mockClear();

      act(() => {
        result.current.handleScrollLeft();
      });

      expect(mockOnDateChange).toHaveBeenCalledWith('2025-08-03');
    });

    it('should not call onDateChange when callback is not provided', () => {
      const { result } = renderHook(() => useDateNavigation());

      // Should not throw error when no callback provided
      act(() => {
        result.current.handleScrollRight();
      });

      act(() => {
        result.current.handleScrollLeft();
      });

      // Test passes if no error is thrown
      expect(result.current.startOffset).toBe(0);
    });

    it('should call onDateChange with external date base when scrolling right', () => {
      const mockOnDateChange = jest.fn();
      const externalDate = '2025-12-01';
      const { result } = renderHook(() => useDateNavigation(mockOnDateChange, externalDate));

      act(() => {
        result.current.handleScrollRight();
      });

      expect(mockOnDateChange).toHaveBeenCalledWith('2025-12-08');
    });

    it('should call onDateChange with external date base when scrolling left', () => {
      const mockOnDateChange = jest.fn();
      const externalDate = '2025-12-15';
      const { result } = renderHook(() => useDateNavigation(mockOnDateChange, externalDate));

      // First scroll right to enable scroll left
      act(() => {
        result.current.handleScrollRight();
      });

      mockOnDateChange.mockClear();

      act(() => {
        result.current.handleScrollLeft();
      });

      // When scrolling left, it uses the formatted date string from the calculation
      expect(mockOnDateChange).toHaveBeenCalledWith('2025-12-08');
    });

    it('should handle scroll left boundary with onDateChange callback', () => {
      const mockOnDateChange = jest.fn();
      const { result } = renderHook(() => useDateNavigation(mockOnDateChange));

      // Try to scroll left from initial position (should hit today boundary)
      act(() => {
        result.current.handleScrollLeft();
      });

      // Should call onDateChange with today's date (boundary enforcement)
      expect(mockOnDateChange).toHaveBeenCalledWith('2025-08-03');
    });
  });

  describe('Scroll disabled state calculations', () => {
    it('should disable scroll right when approaching max date limit', () => {
      // Today is 2025-08-03 (our mock date)
      // Max allowed date = today + 364 days = 2026-08-02
      // Next scroll start = current + 7 (SCROLL_OFFSET)
      // Next scroll end = next scroll start + 7 - 1 (DAYS_TO_SHOW - 1) = next scroll start + 6
      // So we need: current + 7 + 6 > 2026-08-02
      // This means current > 2026-08-02 - 13 = 2026-07-20
      const nearLimitDate = '2026-07-28'; // This should make next scroll exceed the limit
      const { result } = renderHook(() => useDateNavigation(undefined, nearLimitDate));

      // Should be disabled when next scroll would exceed 364 days from today
      expect(result.current.isScrollRightDisabled).toBe(true);
    });

    it('should enable scroll right when well within date limit', () => {
      const { result } = renderHook(() => useDateNavigation());

      // Initially should be enabled (we're at current date)
      expect(result.current.isScrollRightDisabled).toBe(false);
    });

    it('should correctly calculate scroll left disabled state', () => {
      const futureDate = '2025-12-01';
      const { result } = renderHook(() => useDateNavigation(undefined, futureDate));

      // Should enable scroll left when not showing today's date
      expect(result.current.isScrollLeftDisabled).toBe(false);
    });
  });
});
