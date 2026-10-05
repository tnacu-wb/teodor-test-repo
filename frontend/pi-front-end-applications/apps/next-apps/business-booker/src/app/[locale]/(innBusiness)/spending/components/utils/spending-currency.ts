import { Currency } from '@whitbread-eos/api';
import type { CompanySpending } from '@whitbread-eos/api';

export type CurrencyTotals = {
  GBP: number;
  EUR: number;
};

const normalizeCurrency = (currency?: string | null, fallbackCurrency?: string) => {
  if (currency === Currency.EUR_NAME || currency === Currency.GBP_NAME) {
    return currency;
  }

  return fallbackCurrency;
};

const sortByDate = (a: CompanySpending, b: CompanySpending) => {
  const yearDiff = (a.year ?? 0) - (b.year ?? 0);
  if (yearDiff !== 0) {
    return yearDiff;
  }

  return Number(a.month ?? 0) - Number(b.month ?? 0);
};

export const getLastBookingCurrency = (
  spending: CompanySpending[],
  fallbackCurrency: string,
  missingCurrencyFallback?: string
): string => {
  const fallbackForMissing = missingCurrencyFallback ?? fallbackCurrency;
  const nonZeroSpendings =
    spending?.filter((item) => typeof item?.bookingValue === 'number' && item.bookingValue !== 0) ??
    [];

  if (!nonZeroSpendings.length) {
    return fallbackCurrency;
  }

  const sorted = [...nonZeroSpendings].sort(sortByDate);
  const lastSpending = sorted[sorted.length - 1];
  const normalizedCurrency = normalizeCurrency(lastSpending?.bookingCurrency, fallbackForMissing);

  return normalizedCurrency ?? fallbackCurrency;
};

export const getMonthCurrencyTotals = (
  spending: CompanySpending[],
  month: number,
  year: number,
  fallbackCurrency: string
): CurrencyTotals => {
  return (spending ?? []).reduce<CurrencyTotals>(
    (totals, item) => {
      if (Number(item?.month) !== month || Number(item?.year) !== year) {
        return totals;
      }

      const normalizedCurrency = normalizeCurrency(item?.bookingCurrency, fallbackCurrency);
      if (normalizedCurrency === Currency.EUR_NAME) {
        totals.EUR += item?.bookingValue ?? 0;
      } else {
        totals.GBP += item?.bookingValue ?? 0;
      }

      return totals;
    },
    { GBP: 0, EUR: 0 }
  );
};

export const getCurrencyOrder = (totals: CurrencyTotals) => {
  let primaryCurrency: string;

  if (totals.GBP > totals.EUR) {
    primaryCurrency = Currency.GBP_NAME;
  } else if (totals.EUR > totals.GBP) {
    primaryCurrency = Currency.EUR_NAME;
  } else {
    primaryCurrency = Currency.GBP_NAME;
  }

  const secondaryCurrency =
    primaryCurrency === Currency.GBP_NAME ? Currency.EUR_NAME : Currency.GBP_NAME;

  return [primaryCurrency, secondaryCurrency];
};
