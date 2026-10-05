import type { Locale } from 'date-fns';

type LocalizeWithCustomDay = Locale['localize'] & {
  day: (day: number, options?: { width?: string; context?: string }) => string;
};
const rearrangeDays = (days: string[]) => {
  const lastDay = days[days.length - 1];

  const remainingDays = days.slice(0, days.length - 1);
  return [lastDay, ...remainingDays];
};

const createCustomLocale = (
  baseLocale: Locale,
  months: string[],
  weekdaysShort: string[],
  isShortMonth = false
) => {
  const oldLocalize = baseLocale.localize as LocalizeWithCustomDay;

  const rearrangeShortDays = rearrangeDays(weekdaysShort);

  const newLocalize: LocalizeWithCustomDay = {
    ...oldLocalize,
    month: (value: number): string => {
      const monthName = months[value];
      return isShortMonth ? monthName?.slice(0, 3) : monthName;
    },
    day: (day: number, options?: { width?: string; context?: string }) => {
      if (options?.width === 'wide') {
        return oldLocalize.day(day, options);
      }
      return rearrangeShortDays[day];
    },
  };
  return {
    ...baseLocale,
    localize: newLocalize,
  };
};

export { createCustomLocale };
