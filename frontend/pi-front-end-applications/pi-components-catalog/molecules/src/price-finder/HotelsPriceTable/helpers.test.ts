import {
  generateDates,
  formatDate,
  isToday,
  transformApiDataToHotelPrices,
  getPriceStyle,
} from './helpers';

// Mock the constants
jest.mock('./constants', () => ({
  HOTEL_NAMES: ['Hotel A', 'Hotel B', 'Hotel C'],
  THEME_COLORS: {
    lowestPrice: '#E6FFFA',
    nearLowestPrice: '#B2F5EA',
    disabledColor: '#A0AEC0',
  },
}));

// Mock the current date to ensure consistent tests
const mockCurrentDate = '2025-08-03';
jest.useFakeTimers();
jest.setSystemTime(new Date(mockCurrentDate));

describe('Helper Functions', () => {
  afterAll(() => {
    jest.useRealTimers();
  });

  describe('generateDates', () => {
    it('should generate correct number of dates', () => {
      const startDate = new Date('2025-08-03');
      const dates = generateDates(startDate, 5);

      expect(dates).toHaveLength(5);
    });

    it('should generate sequential dates starting from startDate', () => {
      const startDate = new Date('2025-08-03');
      const dates = generateDates(startDate, 3);

      expect(dates).toEqual(['2025-08-03', '2025-08-04', '2025-08-05']);
    });

    it('should return ISO date strings', () => {
      const startDate = new Date('2025-08-03');
      const dates = generateDates(startDate, 2);

      dates.forEach((date) => {
        expect(date).toMatch(/^\d{4}-\d{2}-\d{2}$/);
      });
    });

    it('should handle single day', () => {
      const startDate = new Date('2025-08-03');
      const dates = generateDates(startDate, 1);

      expect(dates).toEqual(['2025-08-03']);
    });

    it('should handle zero days', () => {
      const startDate = new Date('2025-08-03');
      const dates = generateDates(startDate, 0);

      expect(dates).toEqual([]);
    });

    it('should handle dates crossing month boundaries', () => {
      const startDate = new Date('2025-07-30');
      const dates = generateDates(startDate, 5);

      expect(dates).toEqual(['2025-07-30', '2025-07-31', '2025-08-01', '2025-08-02', '2025-08-03']);
    });

    it('should not mutate the original startDate', () => {
      const startDate = new Date('2025-08-03');
      const originalTime = startDate.getTime();

      generateDates(startDate, 7);

      expect(startDate.getTime()).toBe(originalTime);
    });
  });

  describe('formatDate', () => {
    const testDate = '2025-08-03'; // Sunday

    it('should format date with default options', () => {
      const formatted = formatDate(testDate);

      expect(formatted.weekday).toBe('Sun');
      expect(formatted.day).toBe('3');
    });

    it('should use short weekday format for larger screens', () => {
      const formatted = formatDate(testDate, { isSmallerThanSm: false });

      expect(formatted.weekday).toBe('Sun');
    });

    it('should use 2-letter weekday format for smaller screens', () => {
      const formatted = formatDate(testDate, { isSmallerThanSm: true });

      expect(formatted.weekday).toBe('Su');
    });

    it('should respect custom locale', () => {
      const formatted = formatDate(testDate, { locale: 'de-DE' });

      expect(formatted.weekday).toMatch(/So\.?/); // German for Sunday (with or without period)
      expect(formatted.day).toBe('3');
    });

    it('should handle different dates correctly', () => {
      const mondayDate = '2025-08-04';
      const formatted = formatDate(mondayDate);

      expect(formatted.weekday).toBe('Mon');
      expect(formatted.day).toBe('4');
    });

    it('should handle edge case when window is undefined (SSR)', () => {
      // Test should work normally since we're not in browser environment
      const formatted = formatDate(testDate, { isSmallerThanSm: false });

      expect(formatted.weekday).toBe('Sun');
      expect(formatted.day).toBe('3');
    });

    it('should handle malformed date strings gracefully', () => {
      const formatted = formatDate('invalid-date');

      // Should return empty strings for invalid dates
      expect(formatted.weekday).toBe('');
      expect(formatted.day).toBe('');
    });
  });

  describe('isToday', () => {
    it("should return true for today's date", () => {
      const today = new Date().toISOString().split('T')[0];

      expect(isToday(today)).toBe(true);
    });

    it('should return false for yesterday', () => {
      const yesterday = new Date();
      yesterday.setDate(yesterday.getDate() - 1);
      const yesterdayString = yesterday.toISOString().split('T')[0];

      expect(isToday(yesterdayString)).toBe(false);
    });

    it('should return false for tomorrow', () => {
      const tomorrow = new Date();
      tomorrow.setDate(tomorrow.getDate() + 1);
      const tomorrowString = tomorrow.toISOString().split('T')[0];

      expect(isToday(tomorrowString)).toBe(false);
    });

    it('should return false for completely different date', () => {
      expect(isToday('2020-01-01')).toBe(false);
    });

    it('should handle malformed date strings', () => {
      expect(isToday('invalid-date')).toBe(false);
    });
  });

  describe('transformApiDataToHotelPrices', () => {
    const currentDates = ['2025-08-03', '2025-08-04', '2025-08-05'];

    const mockApiData = [
      {
        hotelCode: 'HOTEL_A_CODE',
        hotelName: 'HOTEL_A',
        availabilities: [
          {
            availableDate: '2025-08-03',
            minimumRate: 100,
            currency: 'EUR',
            rateCode: 'RC1',
            rateClassification: 'Standard',
            roomType: 'Deluxe',
            roomCategory: 'SB',
            quantity: 1,
            hasMlosRestriction: false,
            hasClosedRestriction: false,
            minimumNights: 1,
          },
          {
            availableDate: '2025-08-04',
            minimumRate: 0, // Should be null
            currency: 'EUR',
            rateCode: 'RC2',
            rateClassification: 'Standard',
            roomType: 'Deluxe',
            roomCategory: 'SB',
            quantity: 1,
            hasMlosRestriction: false,
            hasClosedRestriction: false,
            minimumNights: 1,
          },
          {
            availableDate: '2025-08-05',
            minimumRate: 120,
            currency: 'EUR',
            rateCode: 'RC3',
            rateClassification: 'Standard',
            roomType: 'Deluxe',
            roomCategory: 'SB',
            quantity: 1,
            hasMlosRestriction: false,
            hasClosedRestriction: false,
            minimumNights: 1,
          },
        ],
      },
      {
        hotelCode: 'HOTEL_B_CODE',
        hotelName: 'HOTEL_B',
        availabilities: [
          {
            availableDate: '2025-08-03',
            minimumRate: 80,
            currency: 'EUR',
            rateCode: 'RC1',
            rateClassification: 'Standard',
            roomType: 'Deluxe',
            roomCategory: 'SB',
            quantity: 1,
            hasMlosRestriction: false,
            hasClosedRestriction: false,
            minimumNights: 1,
          },
          {
            availableDate: '2025-08-05',
            minimumRate: 90,
            currency: 'EUR',
            rateCode: 'RC2',
            rateClassification: 'Standard',
            roomType: 'Deluxe',
            roomCategory: 'SB',
            quantity: 1,
            hasMlosRestriction: false,
            hasClosedRestriction: false,
            minimumNights: 1,
          },
          // Missing 2025-08-04 - should be null
        ],
      },
    ];

    it('should transform API data to HotelPrice format', () => {
      const result = transformApiDataToHotelPrices(mockApiData, currentDates);

      expect(result).toHaveLength(2);
      expect(result[0].hotelName).toBe('HOTEL_A');
      expect(result[1].hotelName).toBe('HOTEL_B');
    });

    it('should map availability data to prices correctly', () => {
      const result = transformApiDataToHotelPrices(mockApiData, currentDates);

      expect(result[0].prices).toEqual([
        { date: '2025-08-03', amount: 100, currency: 'EUR', roomCategory: 'SB' },
        { date: '2025-08-04', amount: null, currency: 'EUR', roomCategory: 'SB' }, // minimumRate = 0
        { date: '2025-08-05', amount: 120, currency: 'EUR', roomCategory: 'SB' },
      ]);
    });

    it('should handle missing availability for dates', () => {
      const result = transformApiDataToHotelPrices(mockApiData, currentDates);

      expect(result[1].prices).toEqual([
        { date: '2025-08-03', amount: 80, currency: 'EUR', roomCategory: 'SB' },
        { date: '2025-08-04', amount: null, currency: 'GBP', roomCategory: 'SB' }, // No availability
        { date: '2025-08-05', amount: 90, currency: 'EUR', roomCategory: 'SB' },
      ]);
    });

    it('should set null for zero or negative rates', () => {
      const dataWithZeroRate = [
        {
          hotelCode: 'HOTEL_ZERO',
          hotelName: 'Hotel Zero',
          availabilities: [
            {
              availableDate: '2025-08-03',
              minimumRate: 0,
              currency: 'EUR',
              rateCode: 'RC1',
              rateClassification: 'Standard',
              roomType: 'Deluxe',
              roomCategory: 'SB',
              quantity: 1,
              hasMlosRestriction: false,
              hasClosedRestriction: false,
              minimumNights: 1,
            },
            {
              availableDate: '2025-08-04',
              minimumRate: -10,
              currency: 'EUR',
              rateCode: 'RC2',
              rateClassification: 'Standard',
              roomType: 'Deluxe',
              roomCategory: 'SB',
              quantity: 1,
              hasMlosRestriction: false,
              hasClosedRestriction: false,
              minimumNights: 1,
            },
          ],
        },
      ];

      const result = transformApiDataToHotelPrices(dataWithZeroRate, ['2025-08-03', '2025-08-04']);

      expect(result[0].prices).toEqual([
        { date: '2025-08-03', amount: null, currency: 'EUR', roomCategory: 'SB' },
        { date: '2025-08-04', amount: null, currency: 'EUR', roomCategory: 'SB' },
      ]);
    });

    it('should handle empty API data', () => {
      const result = transformApiDataToHotelPrices([], currentDates);

      expect(result).toEqual([]);
    });

    it('should handle hotels without availabilities property', () => {
      const dataWithoutAvailabilities = [
        { hotelCode: 'HOTEL_NO_AVAIL', hotelName: 'Hotel No Availability' },
      ];

      const result = transformApiDataToHotelPrices(dataWithoutAvailabilities, currentDates);

      expect(result[0].prices).toEqual([
        { date: '2025-08-03', amount: null, currency: 'GBP', roomCategory: 'SB' },
        { date: '2025-08-04', amount: null, currency: 'GBP', roomCategory: 'SB' },
        { date: '2025-08-05', amount: null, currency: 'GBP', roomCategory: 'SB' },
      ]);
    });
  });

  describe('getPriceStyle', () => {
    const THEME_COLORS = {
      lowestPrice: '#E6FFFA',
      nearLowestPrice: '#B2F5EA',
      disabledColor: '#A0AEC0',
    };

    describe('Default behavior without priceConfig', () => {
      it('should return disabled style when price is null', () => {
        const result = getPriceStyle(null, 100);

        expect(result).toEqual({
          textBg: 'transparent',
          color: THEME_COLORS.disabledColor,
          fontWeight: 500,
        });
      });

      it('should return transparent style when lowestPriceForComparison is null', () => {
        const result = getPriceStyle(100, null);

        expect(result).toEqual({
          textBg: 'transparent',
          color: 'black !important',
          fontWeight: 500,
        });
      });

      it('should return lowestPrice style when price equals lowestPriceForComparison', () => {
        const result = getPriceStyle(100, 100);

        expect(result).toEqual({
          textBg: THEME_COLORS.lowestPrice,
          color: 'black !important',
          fontWeight: 500,
        });
      });

      it('should return nearLowestPrice style when price is within range', () => {
        const result = getPriceStyle(103, 100, 5);

        expect(result).toEqual({
          textBg: THEME_COLORS.nearLowestPrice,
          color: 'black !important',
          fontWeight: 500,
        });
      });

      it('should return nearLowestPrice style when price equals lowestPrice + range', () => {
        const result = getPriceStyle(105, 100, 5);

        expect(result).toEqual({
          textBg: THEME_COLORS.nearLowestPrice,
          color: 'black !important',
          fontWeight: 500,
        });
      });

      it('should return transparent style when price exceeds range', () => {
        const result = getPriceStyle(106, 100, 5);

        expect(result).toEqual({
          textBg: 'transparent',
          color: 'black',
          fontWeight: 500,
        });
      });
    });

    describe('With priceConfig parameter', () => {
      const priceConfig = {
        highlightedPriceRangeStep: 10,
        highlightedPriceRangeMin: 50,
        highlightedPricePrimaryColour: '#FFD700',
        highlightedPriceSecondaryColour: '#FFA500',
      };

      it('should use priceConfig rangeMin instead of lowestPriceForComparison', () => {
        const result = getPriceStyle(50, 100, 5, priceConfig);

        expect(result).toEqual({
          textBg: '#FFD700', // primaryColour
          color: 'black !important',
          fontWeight: 500,
        });
      });

      it('should use priceConfig primaryColour when price equals lowestPriceForComparison (default mode)', () => {
        // Without path, uses default mode with lowestPriceForComparison (100)
        const result = getPriceStyle(100, 100, 5, priceConfig);

        expect(result.textBg).toBe('#FFD700');
      });

      it('should use priceConfig secondaryColour when price is within rangeStep (default mode)', () => {
        // Without path, uses lowestPriceForComparison (100) + rangeStep (10)
        const result = getPriceStyle(105, 100, 5, priceConfig);

        expect(result).toEqual({
          textBg: '#FFA500', // secondaryColour
          color: 'black !important',
          fontWeight: 500,
        });
      });

      it('should use priceConfig rangeStep for range calculation (default mode)', () => {
        // 110 = 100 (lowestPriceForComparison) + 10 (rangeStep)
        const result = getPriceStyle(110, 100, 5, priceConfig);

        expect(result).toEqual({
          textBg: '#FFA500',
          color: 'black !important',
          fontWeight: 500,
        });
      });

      it('should return transparent when price exceeds lowestPriceForComparison + rangeStep (default mode)', () => {
        // 111 > 100 + 10
        const result = getPriceStyle(111, 100, 5, priceConfig);

        expect(result).toEqual({
          textBg: 'transparent',
          color: 'black',
          fontWeight: 500,
        });
      });

      it('should fallback to defaults when priceConfig has undefined values', () => {
        const partialConfig = {
          highlightedPriceRangeStep: undefined,
          highlightedPriceRangeMin: undefined,
          highlightedPricePrimaryColour: undefined,
          highlightedPriceSecondaryColour: undefined,
        };

        const result = getPriceStyle(100, 100, 5, partialConfig);

        expect(result).toEqual({
          textBg: THEME_COLORS.lowestPrice,
          color: 'black !important',
          fontWeight: 500,
        });
      });

      it('should handle partial priceConfig with some undefined values', () => {
        const partialConfig = {
          highlightedPriceRangeMin: 80,
          highlightedPricePrimaryColour: '#00FF00',
        };

        const result = getPriceStyle(80, 100, 5, partialConfig);

        expect(result).toEqual({
          textBg: '#00FF00', // Custom primary colour
          color: 'black !important',
          fontWeight: 500,
        });
      });

      it('should use custom colors even without rangeMin (default behavior)', () => {
        const configWithOnlyColors = {
          highlightedPricePrimaryColour: '#FFD700',
          highlightedPriceSecondaryColour: '#FFA500',
        };

        // When price equals lowestPriceForComparison, should use custom primary color
        const resultLowest = getPriceStyle(100, 100, 5, configWithOnlyColors);
        expect(resultLowest).toEqual({
          textBg: '#FFD700', // Custom primary color instead of THEME_COLORS
          color: 'black !important',
          fontWeight: 500,
        });

        // When price is in range, should use custom secondary color
        const resultInRange = getPriceStyle(103, 100, 5, configWithOnlyColors);
        expect(resultInRange).toEqual({
          textBg: '#FFA500', // Custom secondary color instead of THEME_COLORS
          color: 'black !important',
          fontWeight: 500,
        });
      });

      it('should use only primary color when only primary is provided', () => {
        const configWithOnlyPrimary = {
          highlightedPricePrimaryColour: '#FF0000',
        };

        const result = getPriceStyle(100, 100, 5, configWithOnlyPrimary);

        expect(result).toEqual({
          textBg: '#FF0000', // Custom primary color
          color: 'black !important',
          fontWeight: 500,
        });
      });

      it('should fallback secondary to THEME_COLORS when only primary is provided', () => {
        const configWithOnlyPrimary = {
          highlightedPricePrimaryColour: '#FF0000',
        };

        const result = getPriceStyle(103, 100, 5, configWithOnlyPrimary);

        expect(result).toEqual({
          textBg: THEME_COLORS.nearLowestPrice, // Falls back to theme color
          color: 'black !important',
          fontWeight: 500,
        });
      });
    });

    describe('Edge cases', () => {
      it('should handle zero price', () => {
        const result = getPriceStyle(0, 100);

        expect(result).toEqual({
          textBg: 'transparent',
          color: THEME_COLORS.disabledColor,
          fontWeight: 500,
        });
      });

      it('should handle negative price', () => {
        const result = getPriceStyle(-10, 100);

        // Negative prices (-10) are <= lowestPrice (100), so use primary color
        expect(result).toEqual({
          textBg: '#E6FFFA', // THEME_COLORS.lowestPrice (from mock)
          color: 'black !important',
          fontWeight: 500,
        });
      });

      it('should handle very large price differences', () => {
        const result = getPriceStyle(10000, 100, 5);

        expect(result).toEqual({
          textBg: 'transparent',
          color: 'black',
          fontWeight: 500,
        });
      });

      it('should handle decimal prices', () => {
        const result = getPriceStyle(102.5, 100, 5);

        expect(result).toEqual({
          textBg: THEME_COLORS.nearLowestPrice,
          color: 'black !important',
          fontWeight: 500,
        });
      });
    });

    describe('Campaign mode vs Default mode logic', () => {
      const priceConfig = {
        highlightedPriceRangeStep: 10,
        highlightedPriceRangeMin: 50,
        highlightedPricePrimaryColour: '#FFD700',
        highlightedPriceSecondaryColour: '#FFA500',
      };

      describe('Campaign mode (path is set)', () => {
        it('should use highlightedPriceRangeMin as rangeMin when price <= rangeMin', () => {
          const result = getPriceStyle(50, 100, 5, {
            ...priceConfig,
            path: 'campaign/test',
          });

          expect(result).toEqual({
            textBg: '#FFD700', // primaryColour
            color: 'black !important',
            fontWeight: 500,
          });
        });

        it('should use highlightedPriceRangeMin when price is less than rangeMin', () => {
          const result = getPriceStyle(40, 100, 5, {
            ...priceConfig,
            path: 'campaign/test',
          });

          expect(result).toEqual({
            textBg: '#FFD700', // primaryColour
            color: 'black !important',
            fontWeight: 500,
          });
        });

        it('should NOT use secondary color in campaign mode', () => {
          const result = getPriceStyle(55, 100, 5, {
            ...priceConfig,
            path: 'campaign/test',
          });

          expect(result).toEqual({
            textBg: 'transparent',
            color: 'black',
            fontWeight: 500,
          });
        });

        it('should return transparent when price exceeds rangeMin in campaign mode', () => {
          const result = getPriceStyle(65, 100, 5, {
            ...priceConfig,
            path: 'campaign/test',
          });

          expect(result).toEqual({
            textBg: 'transparent',
            color: 'black',
            fontWeight: 500,
          });
        });
      });

      describe('Default mode (path is empty, undefined, or "default")', () => {
        it('should use lowestPriceForComparison as rangeMin when path is empty', () => {
          const result = getPriceStyle(100, 100, 5, { ...priceConfig, path: '' });

          expect(result).toEqual({
            textBg: '#FFD700', // primaryColour
            color: 'black !important',
            fontWeight: 500,
          });
        });

        it('should use lowestPriceForComparison as rangeMin when path is undefined', () => {
          const result = getPriceStyle(100, 100, 5, { ...priceConfig });

          expect(result).toEqual({
            textBg: '#FFD700', // primaryColour
            color: 'black !important',
            fontWeight: 500,
          });
        });

        it('should use lowestPriceForComparison as rangeMin when path is "default"', () => {
          const result = getPriceStyle(100, 100, 5, { ...priceConfig, path: 'default' });

          expect(result).toEqual({
            textBg: '#FFD700', // primaryColour
            color: 'black !important',
            fontWeight: 500,
          });
        });

        it('should use secondary color in default mode when within range', () => {
          const result = getPriceStyle(105, 100, 5, { ...priceConfig, path: '' });

          expect(result).toEqual({
            textBg: '#FFA500', // secondaryColour
            color: 'black !important',
            fontWeight: 500,
          });
        });

        it('should use secondary color when path is "default" and within range', () => {
          const result = getPriceStyle(105, 100, 5, { ...priceConfig, path: 'default' });

          expect(result).toEqual({
            textBg: '#FFA500', // secondaryColour
            color: 'black !important',
            fontWeight: 500,
          });
        });

        it('should use secondary color with rangeStep from config', () => {
          const result = getPriceStyle(108, 100, 5, { ...priceConfig });

          expect(result).toEqual({
            textBg: '#FFA500', // secondaryColour
            color: 'black !important',
            fontWeight: 500,
          });
        });

        it('should return transparent when price exceeds range in default mode', () => {
          const result = getPriceStyle(115, 100, 5, { ...priceConfig, path: '' });

          expect(result).toEqual({
            textBg: 'transparent',
            color: 'black',
            fontWeight: 500,
          });
        });
      });

      describe('When config is incomplete', () => {
        it('should use default mode when highlightedPriceRangeMin is undefined', () => {
          const incompleteConfig = {
            highlightedPriceRangeStep: 10,
            highlightedPricePrimaryColour: '#FFD700',
            highlightedPriceSecondaryColour: '#FFA500',
            path: 'campaign/test',
          };

          const result = getPriceStyle(100, 100, 5, incompleteConfig);

          // Should use custom primary color, using default mode with lowestPriceForComparison
          expect(result).toEqual({
            textBg: '#FFD700', // Custom color is used
            color: 'black !important',
            fontWeight: 500,
          });
        });

        it('should use THEME_COLORS fallback when highlightedPricePrimaryColour is undefined with custom range', () => {
          const incompleteConfig = {
            highlightedPriceRangeStep: 10,
            highlightedPriceRangeMin: 50,
            highlightedPriceSecondaryColour: '#FFA500',
            path: 'campaign/test',
          };

          // Price 100 > rangeMin 50, and path is set, so should return transparent
          const result = getPriceStyle(100, 100, 5, incompleteConfig);

          expect(result).toEqual({
            textBg: 'transparent', // Transparent because price exceeds range and path is set
            color: 'black',
            fontWeight: 500,
          });
        });

        it('should use THEME_COLORS fallback for primary when undefined with custom range and matching price', () => {
          const incompleteConfig = {
            highlightedPriceRangeStep: 10,
            highlightedPriceRangeMin: 100,
            highlightedPriceSecondaryColour: '#FFA500',
            path: '',
          };

          // Price 100 = rangeMin 100, should use THEME_COLORS.lowestPrice as fallback
          const result = getPriceStyle(100, 100, 5, incompleteConfig);

          expect(result).toEqual({
            textBg: THEME_COLORS.lowestPrice, // Falls back to theme color
            color: 'black !important',
            fontWeight: 500,
          });
        });
      });
    });
  });
});
