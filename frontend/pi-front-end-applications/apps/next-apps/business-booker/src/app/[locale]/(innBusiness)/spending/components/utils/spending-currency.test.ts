import { Currency } from '@whitbread-eos/api';

import {
  getCurrencyOrder,
  getLastBookingCurrency,
  getMonthCurrencyTotals,
} from './spending-currency';

describe('spending-currency utils', () => {
  describe('getLastBookingCurrency', () => {
    it('returns fallback when there are no non-zero spendings', () => {
      expect(getLastBookingCurrency([], Currency.GBP_NAME)).toBe(Currency.GBP_NAME);
      expect(
        getLastBookingCurrency(
          [{ year: 2024, month: '1', bookingValue: 0, bookingCurrency: 'EUR' } as any],
          Currency.EUR_NAME
        )
      ).toBe(Currency.EUR_NAME);
    });

    it('returns the currency from the most recent non-zero spending', () => {
      const spending = [
        { year: 2024, month: '12', bookingValue: 150, bookingCurrency: 'GBP' },
        { year: 2025, month: '1', bookingValue: 200, bookingCurrency: 'EUR' },
        { year: 2024, month: '2', bookingValue: 100, bookingCurrency: 'GBP' },
      ] as any;

      expect(getLastBookingCurrency(spending, Currency.GBP_NAME)).toBe(Currency.EUR_NAME);
    });

    it('uses missing currency fallback when the last booking has no currency', () => {
      const spending = [
        { year: 2025, month: '2', bookingValue: 100, bookingCurrency: null },
      ] as any;

      expect(getLastBookingCurrency(spending, Currency.GBP_NAME, Currency.EUR_NAME)).toBe(
        Currency.EUR_NAME
      );
    });
  });

  describe('getMonthCurrencyTotals', () => {
    it('sums totals for the requested month and year', () => {
      const spending = [
        { year: 2024, month: '2', bookingValue: 100, bookingCurrency: 'GBP' },
        { year: 2024, month: '2', bookingValue: 50, bookingCurrency: 'EUR' },
        { year: 2024, month: '2', bookingValue: 25, bookingCurrency: null },
        { year: 2024, month: '1', bookingValue: 999, bookingCurrency: 'EUR' },
        { year: 2023, month: '2', bookingValue: 999, bookingCurrency: 'EUR' },
      ] as any;

      expect(getMonthCurrencyTotals(spending, 2, 2024, Currency.GBP_NAME)).toEqual({
        GBP: 125,
        EUR: 50,
      });
    });
  });

  describe('getCurrencyOrder', () => {
    it('returns GBP first when GBP has the highest total', () => {
      expect(getCurrencyOrder({ GBP: 200, EUR: 100 })).toEqual([
        Currency.GBP_NAME,
        Currency.EUR_NAME,
      ]);
    });

    it('returns EUR first when EUR has the highest total', () => {
      expect(getCurrencyOrder({ GBP: 50, EUR: 100 })).toEqual([
        Currency.EUR_NAME,
        Currency.GBP_NAME,
      ]);
    });

    it('defaults to GBP first when totals are equal', () => {
      expect(getCurrencyOrder({ GBP: 10, EUR: 10 })).toEqual([
        Currency.GBP_NAME,
        Currency.EUR_NAME,
      ]);
    });

    it('defaults to GBP first when totals are both zero', () => {
      expect(getCurrencyOrder({ GBP: 0, EUR: 0 })).toEqual([Currency.GBP_NAME, Currency.EUR_NAME]);
    });
  });
});
