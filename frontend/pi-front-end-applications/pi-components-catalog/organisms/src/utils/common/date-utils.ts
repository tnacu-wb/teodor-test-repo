export interface MonthData {
  value: string;
  label: string;
  date: Date;
  price?: number;
}

const createUTCDate = (year: number, month: number, day = 1): Date => {
  return new Date(Date.UTC(year, month, day));
};

const formatMonthLabel = (date: Date, locale: 'en-GB' | 'de-DE', currentYear: number): string => {
  const month = date.toLocaleString(locale, {
    timeZone: 'UTC',
    month: 'long',
  });
  const year = date.getUTCFullYear();
  const suffix = year > currentYear ? ` '${String(year).slice(-2)}` : '';
  return `${month}${suffix}`;
};

export const generateMonthData = (
  maxMonths = 12,
  locale: 'en-GB' | 'de-DE' = 'en-GB'
): MonthData[] => {
  const today = new Date();
  const currentUTCYear = today.getUTCFullYear();
  const currentUTCMonth = today.getUTCMonth();
  const basePrice = 80;

  return Array.from({ length: maxMonths }, (_, i) => {
    const date = createUTCDate(currentUTCYear, currentUTCMonth + i);
    const label = formatMonthLabel(date, locale, currentUTCYear);
    const value = `${date.getUTCFullYear()}-${String(date.getUTCMonth() + 1).padStart(2, '0')}`;
    const price = basePrice + i * 10;

    return { value, label, date, price };
  });
};
