'use client';

import { CountryLanguage, PI } from '@whitbread-eos/api';
import { getNoOfDaysInYear } from '@whitbread-eos/utils';
import { getCookieConsentInfo } from '@whitbread-eos/utils/server';
import React, { createContext, useContext, ReactNode, useEffect, useState } from 'react';

interface CookieConsentProviderProps {
  children: ReactNode;
  countryLanguageResolver: () => CountryLanguage;
  initialData?: CookieConsentDialogData | null;
}

export type CookieConsentDialogData = {
  cookieConsentData: any;
  countryLanguage?: CountryLanguage;
};

export const CONSENT_COOKIE = 'consent_cookie';
export const ONE_YEAR_IN_MINUTES = 60 * 24 * getNoOfDaysInYear(new Date().getFullYear());

export const CookieConsentContext = createContext<any>(null);
export const useCookieConsent = () => useContext(CookieConsentContext);

export const CookieConsentProvider = ({
  children,
  countryLanguageResolver,
  initialData = null,
}: Readonly<CookieConsentProviderProps>) => {
  const countryLanguage = countryLanguageResolver();
  const { country, language } = countryLanguage;
  const [data, setData] = useState<CookieConsentDialogData | null>(initialData);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;

    setLoading(true);

    getCookieConsentInfo(country, language, PI).then((result: any) => {
      if (isMounted) {
        setData({
          cookieConsentData: result,
          countryLanguage: countryLanguage,
        });
        setLoading(false);
      }
    });
    return () => {
      isMounted = false;
    };
  }, [country, language]);

  if (loading || !data) {
    return null;
  }

  // Provide data to children via context
  return <CookieConsentContext.Provider value={data}>{children}</CookieConsentContext.Provider>;
};
