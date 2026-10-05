'use client';

import { LOCALES } from '@whitbread-eos/api';
import { usePathname } from 'next/navigation';

import { getCountryLanguageByLocale, getLocaleByPathname } from '../server';

export default function useGetCountryLanguage() {
  const pathname = usePathname();
  const locale: LOCALES = getLocaleByPathname(pathname);

  return getCountryLanguageByLocale(locale);
}
