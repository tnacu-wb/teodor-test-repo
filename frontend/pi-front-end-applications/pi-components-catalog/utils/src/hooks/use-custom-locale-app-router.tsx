'use client';

import { CountryLanguage } from '@whitbread-eos/api';
import { usePathname } from 'next/navigation';

import { getLocaleByPathname, getCountryLanguageByLocale } from '../server';

export const computeCountryLanguageFromPathname = (pathname: string | null): CountryLanguage => {
  if (pathname) {
    const locale = getLocaleByPathname(pathname);
    return getCountryLanguageByLocale(locale || '');
  } else {
    return { country: 'gb', language: 'en' } as CountryLanguage;
  }
};

const useCustomLocaleAppRouter = (): CountryLanguage => {
  const pathname = usePathname();
  return computeCountryLanguageFromPathname(pathname);
};

export default useCustomLocaleAppRouter;
