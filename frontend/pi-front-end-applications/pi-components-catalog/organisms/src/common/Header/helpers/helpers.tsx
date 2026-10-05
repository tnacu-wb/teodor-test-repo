import type { CountryOption } from '@whitbread-eos/api';
import { Icon } from '@whitbread-eos/atoms';
import { formatAssetsUrl, getSecureTwoURL } from '@whitbread-eos/utils';
import { useEffect, useRef } from 'react';

export function getListOfLanguagesForSwitcher(countries: CountryOption[]) {
  const countriesForSwitcher = countries.map((country: CountryOption) => {
    const isEnglishLanguage = country.language === 'English';
    return {
      locale: isEnglishLanguage ? 'en' : 'de',
      languageName: country.language,
      icon: (
        <Icon
          src={formatAssetsUrl(country.flagUrl)}
          alt={country.language}
          cursor="pointer"
          data-testid={isEnglishLanguage ? 'britishFlag' : 'germanFlag'}
        />
      ),
    };
  });
  return countriesForSwitcher;
}

export const isBusinessPage = (pathname: string) => {
  const businessPaths = [
    '/business.html',
    '/business-blog.html',
    '/business/meeting-rooms.html',
    '/business/new-hotels-for-business.html',
    '/business/travel-partners.html',
    '/business/business-termsandconditions.html',
    '/business/business-form.html',
    '/faq/business.html',
  ];
  return businessPaths.some((path) => pathname.includes(path));
};

export const getPibLoginRedirectUrl = (domain: string, language: string, country: string) => {
  return `${domain}/${
    language && country ? `${language}-${country}` : 'en-gb'
  }/account/login?intcmp=piLogInModalLink`;
};

/**
 * Normalizes getSecureTwoURL() (which may return a bare hostname without a
 * protocol on non-www domains) into a full origin comparable to MessageEvent.origin.
 */
export const getSecureTwoOrigin = () => {
  const rawSecureTwoUrl = getSecureTwoURL();
  return rawSecureTwoUrl && rawSecureTwoUrl.startsWith('http')
    ? new URL(rawSecureTwoUrl).origin
    : `${window.location.protocol}//${rawSecureTwoUrl}`.replace(/\/$/, '');
};

export function useLogoutWithRedirect(
  country: string,
  language: string,
  isAuth0Active: boolean,
  navigateToLogout: (returnTo?: string) => void
): () => void {
  const pendingLogoutRef = useRef(false);
  const pendingReasonRef = useRef('');
  const homeUrl = `/${country}/${language}/home.html`;

  useEffect(() => {
    if (isAuth0Active) return;

    const handleMessages = (message: MessageEvent) => {
      const action = typeof message?.data === 'string' ? message.data : message.data?.action;

      if (action === 'USER_LOG_OUT' && message.origin === window.location.origin) {
        pendingLogoutRef.current = true;
        pendingReasonRef.current =
          typeof message.data === 'string' ? '' : (message.data?.reason ?? '');
        const authIframe = document.getElementById('authIframe') as HTMLIFrameElement;
        if (authIframe?.contentWindow) {
          authIframe.contentWindow.postMessage(
            JSON.stringify({ action: 'logout' }),
            getSecureTwoOrigin()
          );
        }
      }

      if (
        action === 'userLoggedOut' &&
        message.origin === getSecureTwoOrigin() &&
        pendingLogoutRef.current
      ) {
        pendingLogoutRef.current = false;
        if (pendingReasonRef.current === 'SESSION_EXPIRED') {
          window.location.assign(`${homeUrl}?sessionExpired=true`);
        } else {
          window.location.assign(homeUrl);
        }
        pendingReasonRef.current = '';
      }
    };

    window.addEventListener('message', handleMessages);
    return () => window.removeEventListener('message', handleMessages);
  }, [country, language, isAuth0Active]);

  return () => {
    if (isAuth0Active) {
      navigateToLogout(`${window.location.origin}${homeUrl}`);
      return;
    }

    const authIframe = document.getElementById('authIframe') as HTMLIFrameElement;
    if (authIframe?.contentWindow) {
      pendingLogoutRef.current = true;
      pendingReasonRef.current = '';
      authIframe.contentWindow.postMessage(
        JSON.stringify({ action: 'logout' }),
        getSecureTwoOrigin()
      );
    }
  };
}
