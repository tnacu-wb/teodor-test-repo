import { Currency, LOCALES } from '@whitbread-eos/api';

import { formatAmount, getCurrencyCodeBasedOnCurrencySymbol } from './format-amount';

describe('formatAmount', () => {
  it('formats positive GBP amount correctly', () => {
    expect(formatAmount(1234.56, Currency.GBP_CODE, LOCALES.EN)).toBe('£1,234.56');
  });

  it('formats negative GBP amount correctly', () => {
    expect(formatAmount(-1234.56, Currency.GBP_CODE, LOCALES.EN)).toBe('-£1,234.56');
  });

  it('formats zero GBP amount correctly', () => {
    expect(formatAmount(0, Currency.GBP_CODE, LOCALES.EN)).toBe('£0.00');
  });

  it('formats positive EUR amount correctly', () => {
    expect(formatAmount(1234.56, Currency.EUR_CODE, LOCALES.EN)).toBe('1,234.56 €');
  });

  it('formats negative EUR amount correctly', () => {
    expect(formatAmount(-1234.56, Currency.EUR_CODE, LOCALES.EN)).toBe('-1,234.56 €');
  });

  it('formats zero EUR amount correctly', () => {
    expect(formatAmount(0, Currency.EUR_CODE, LOCALES.EN)).toBe('0.00 €');
  });

  it('formats with different locale', () => {
    expect(formatAmount(1234.56, Currency.GBP_CODE, LOCALES.DE)).toBe('£1.234,56');
    expect(formatAmount(1234.56, Currency.EUR_CODE, LOCALES.DE)).toBe('1.234,56 €');
  });

  it('formats small decimal values', () => {
    expect(formatAmount(0.1, Currency.GBP_CODE, LOCALES.EN)).toBe('£0.10');
    expect(formatAmount(-0.1, Currency.EUR_CODE, LOCALES.EN)).toBe('-0.10 €');
  });

  it('formats large numbers', () => {
    expect(formatAmount(123456789.99, Currency.GBP_CODE, LOCALES.EN)).toBe('£123,456,789.99');
    expect(formatAmount(-123456789.99, Currency.EUR_CODE, LOCALES.EN)).toBe('-123,456,789.99 €');
  });

  it('returns the correct currency code for a currency symbol', () => {
    expect(getCurrencyCodeBasedOnCurrencySymbol(Currency.GBP)).toBe(Currency.GBP_CODE);
    expect(getCurrencyCodeBasedOnCurrencySymbol(Currency.EUR)).toBe(Currency.EUR_CODE);
  });
});
