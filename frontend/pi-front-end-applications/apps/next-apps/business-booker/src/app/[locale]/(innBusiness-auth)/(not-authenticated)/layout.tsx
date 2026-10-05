import {
  FT_DYNATRACE_RUM_COOKIE_CONSENT,
  LOCALES,
  PathParams,
  FT_IB_REDIRECT_AFTER_LOGIN,
  FT_ONE_TRUST_COOKIE_CONSENT,
} from '@whitbread-eos/api';
import { Toaster, Analytics } from '@whitbread-eos/atoms/ui';
import { CookieConsentClientWrapper } from '@whitbread-eos/layout';
import {
  getServerUnleashToggles,
  ID_TOKEN_COOKIE,
  CONSENT_COOKIE,
  getPathForLocale,
  isOneTrustCookieConsentActive,
} from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';
import { redirect, RedirectType } from 'next/navigation';

import checkValidRedirect from '~utils/checkValidRedirect';

interface LayoutProps {
  children: React.ReactNode;
  params?: Promise<PathParams>;
}

const PAGE_LABEL = 'IB | HMP | Auth';

const Layout: React.FC<LayoutProps> = async ({ children, params }) => {
  const headersList = await headers();
  const currentPath = headersList.get('WB-Url') ?? '';

  let redirectValue = '';
  // extract value from redirectURL query param - e.g. ?redirectURL=/en-gb/business-pay/pay
  if (currentPath.includes('?')) {
    const queryString = currentPath.split('?')[1];
    const params = new URLSearchParams(queryString);
    redirectValue = params.get('redirectURL') ?? '';
    redirectValue = redirectValue.trim();
  }

  const isValidRedirect = checkValidRedirect(redirectValue);

  const flagsFallback = {
    [FT_DYNATRACE_RUM_COOKIE_CONSENT]: false,
    [FT_IB_REDIRECT_AFTER_LOGIN]: false,
    [FT_ONE_TRUST_COOKIE_CONSENT]: false,
  };
  const toggles = await getServerUnleashToggles(PAGE_LABEL, flagsFallback, currentPath, {});

  const resolvedParams = await params;
  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? LOCALES.EN;

  if (!locale || !Object.values(LOCALES).includes(locale)) {
    redirect(`/${LOCALES.EN}/homepage`);
  }

  const cookieStore = await cookies();
  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  if (token) {
    // redirect to value in redirectURL if present and flag is on
    if (toggles?.[FT_IB_REDIRECT_AFTER_LOGIN] && isValidRedirect && redirectValue) {
      return redirect(redirectValue, RedirectType.replace);
    }
    // default to homepage - original behaviour
    return redirect(getPathForLocale(locale, 'homepage'), RedirectType.replace);
  }

  const cookieConsent = cookieStore.get(CONSENT_COOKIE)?.value ?? '';
  const isOneTrustActive = isOneTrustCookieConsentActive(
    toggles?.[FT_ONE_TRUST_COOKIE_CONSENT],
    locale
  );

  return (
    <>
      {children}
      <Toaster />
      <CookieConsentClientWrapper
        show={!cookieConsent && !isOneTrustActive}
        shouldSyncDynatraceConsent={!isOneTrustActive}
        isDynatraceRumCookieConsentEnabled={toggles?.[FT_DYNATRACE_RUM_COOKIE_CONSENT] ?? false}
      />
      <Analytics />
    </>
  );
};

export default Layout;
