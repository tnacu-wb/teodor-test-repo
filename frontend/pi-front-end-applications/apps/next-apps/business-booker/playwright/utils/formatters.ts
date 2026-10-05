import type { Locale } from 'date-fns';
import { de, enUS } from 'date-fns/locale';

function removeDotFromDEMonths(germanLocale: Locale): Locale {
  const customGermanLocale = { ...germanLocale };

  if (germanLocale.localize?.month) {
    customGermanLocale.localize = {
      ...germanLocale.localize,
      month: (
        n: Parameters<typeof germanLocale.localize.month>[0],
        options?: Parameters<typeof germanLocale.localize.month>[1]
      ) => {
        const month = germanLocale.localize?.month(n, options);
        return month?.replace('.', '') ?? '';
      },
    };
  }
  return customGermanLocale;
}

export function getLocale(language: string): Locale {
  if (language === 'en') {
    return enUS;
  } else if (language === 'de') {
    return removeDotFromDEMonths(de);
  } else {
    return enUS;
  }
}

export function getCountry(language: string) {
  if (language === 'de') {
    return 'de';
  }
  return 'gb';
}

export function getLocaleAsString(language: string) {
  if (language === 'en') {
    return 'en-US';
  } else if (language === 'de') {
    return 'de-DE';
  } else {
    return 'en-US';
  }
}

export function extractCardNumber(text: string) {
  const match = text.match(/\*\*\*\*\d{4}$/);
  if (!match || !match[0]) return;
  return match[0];
}
