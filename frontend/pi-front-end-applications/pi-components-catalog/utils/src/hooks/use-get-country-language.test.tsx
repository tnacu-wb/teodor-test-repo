import { LOCALES } from '@whitbread-eos/api';

import useGetCountryLanguage from './use-get-country-language';

jest.mock('next/navigation', () => ({
  usePathname: () => {
    return '/en-gb/homepage';
  },
}));

const mockLocale = LOCALES.EN;

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getLocaleByPathname: () => {
      return mockLocale;
    },
  };
});

describe('useDebounce', () => {
  it('should debounce the callback', () => {
    const result = useGetCountryLanguage();

    expect(result.language).toBe('en');
  });
});
