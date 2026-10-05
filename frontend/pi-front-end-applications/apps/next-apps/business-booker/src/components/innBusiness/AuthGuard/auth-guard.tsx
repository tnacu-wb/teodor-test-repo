'use client';

import { LOCALES } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  ID_TOKEN_COOKIE,
  PIB_MANUAL_LOGOUT_FLAG,
  useCookieWatcher,
} from '@whitbread-eos/utils';
import { getPathForLocale } from '@whitbread-eos/utils/server';
import { useEffect } from 'react';

type Props = {
  secureUrl: string;
  locale: LOCALES;
};

export function AuthGuard({ secureUrl, locale }: Readonly<Props>) {
  const isAuthenticated = useCookieWatcher(ID_TOKEN_COOKIE);
  const { language, country } = getCountryLanguageByLocale(locale);

  useEffect(() => {
    if (typeof window !== 'undefined' && !isAuthenticated) {
      const isManualLogout = sessionStorage?.getItem(PIB_MANUAL_LOGOUT_FLAG) === 'true';
      const loginPath = isManualLogout ? 'account/login' : 'account/login?timeout=true';
      sessionStorage?.removeItem(PIB_MANUAL_LOGOUT_FLAG);
      window.location.href = getPathForLocale(locale, loginPath);
    }
  }, [isAuthenticated, locale]);

  return (
    <iframe
      src={`${secureUrl}/${country}/${language}/business-booker/common/login.html`}
      style={{ display: 'none' }}
      id="authIframe"
      title="InnBusiness authentication"
      data-testid={'InnBusiness-Iframe'}
    ></iframe>
  );
}
