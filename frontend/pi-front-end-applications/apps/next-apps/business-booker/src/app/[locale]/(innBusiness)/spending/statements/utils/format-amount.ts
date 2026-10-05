import { Currency, LOCALES } from '@whitbread-eos/api';

export const formatAmount = (amount: number, currencyCode: string, locale: LOCALES) => {
  const isNegative = amount < 0;
  const formattedAmount = Math.abs(amount).toLocaleString(locale, {
    maximumFractionDigits: 2,
    minimumFractionDigits: 2,
  });

  if (currencyCode === Currency.GBP_CODE) {
    return `${isNegative && amount ? '-' : ''}${Currency.GBP}${formattedAmount}`;
  }

  return `${isNegative && amount ? '-' : ''}${formattedAmount} ${Currency.EUR}`;
};

export const getCurrencyCodeBasedOnCurrencySymbol = (currencySymbol: string) =>
  currencySymbol === Currency.EUR ? Currency.EUR_CODE : Currency.GBP_CODE;
