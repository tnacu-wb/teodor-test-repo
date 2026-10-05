'use client';

import {
  getDynatraceConsentPaths,
  syncDynatraceConsentFromCookie,
  useCustomLocaleAppRouter,
} from '@whitbread-eos/utils';
import { useEffect } from 'react';

import { CookieConsentDialog } from './CookieConsentDialog.component';
import { CookieConsentProvider } from './CookieConsentProvider';

interface CookieConsentClientWrapperProps {
  show: boolean;
  shouldSyncDynatraceConsent?: boolean;
  isDynatraceRumCookieConsentEnabled?: boolean;
}

export const CookieConsentClientWrapper = ({
  show,
  shouldSyncDynatraceConsent = false,
  isDynatraceRumCookieConsentEnabled = false,
}: CookieConsentClientWrapperProps) => {
  const countryLanguage = useCustomLocaleAppRouter();
  const { country, language } = countryLanguage;

  useEffect(() => {
    if (!shouldSyncDynatraceConsent) {
      return;
    }

    syncDynatraceConsentFromCookie({
      isEnabled: isDynatraceRumCookieConsentEnabled,
      paths: getDynatraceConsentPaths(language, country),
      domain: process.env.NEXT_PUBLIC_COOKIES_DOMAIN,
    });
  }, [country, isDynatraceRumCookieConsentEnabled, language, shouldSyncDynatraceConsent]);

  return (
    <>
      {show && (
        <CookieConsentProvider countryLanguageResolver={() => countryLanguage}>
          <CookieConsentDialog
            isDynatraceRumCookieConsentEnabled={isDynatraceRumCookieConsentEnabled}
          />
        </CookieConsentProvider>
      )}
    </>
  );
};
