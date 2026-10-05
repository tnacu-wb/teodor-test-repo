import {
  FT_DYNATRACE_RUM_COOKIE_CONSENT,
  FT_IB_REDIRECT_AFTER_LOGIN,
  FT_ONE_TRUST_COOKIE_CONSENT,
  LOCALES,
  PathParams,
} from '@whitbread-eos/api';
import { Toaster, Analytics } from '@whitbread-eos/atoms/ui';
import { CookieConsentClientWrapper } from '@whitbread-eos/layout';
import {
  getServerUnleashToggles,
  ID_TOKEN_COOKIE,
  CONSENT_COOKIE,
  getUserDetails,
  isOneTrustCookieConsentActive,
} from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';
import { redirect } from 'next/navigation';

import { AuthGuard } from '~components/innBusiness/AuthGuard/auth-guard';
import getPageUrlBeforeRedirectToLogin from '~utils/getPageUrlBeforeRedirectToLogin';

interface LayoutProps {
  children: React.ReactNode;
  params?: Promise<PathParams>;
}

const PAGE_LABEL = 'IB | HMP | Auth';

const Layout: React.FC<LayoutProps> = async ({ children, params }) => {
  const resolvedParams = await params;
  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? LOCALES.EN;
  if (!locale || !Object.values(LOCALES).includes(locale)) {
    redirect(`/${LOCALES.EN}/homepage`);
  }

  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';

  const headersList = await headers();
  const currentPath = headersList.get('WB-Url') ?? '';
  const flagsFallback = {
    [FT_DYNATRACE_RUM_COOKIE_CONSENT]: false,
    [FT_IB_REDIRECT_AFTER_LOGIN]: false,
    [FT_ONE_TRUST_COOKIE_CONSENT]: false,
  };
  const [toggles, userDetails] = await Promise.all([
    getServerUnleashToggles(PAGE_LABEL, flagsFallback, currentPath, {}),
    getUserDetails(token),
  ]);

  const isRedirectAfterLoginEnabled = toggles?.[FT_IB_REDIRECT_AFTER_LOGIN] ?? false;
  if (!token) {
    getPageUrlBeforeRedirectToLogin(currentPath, locale, isRedirectAfterLoginEnabled);
  }

  const cookieConsent = cookieStore.get(CONSENT_COOKIE)?.value ?? '';
  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';
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
      <Analytics userDetails={userDetails} />
      <AuthGuard secureUrl={secureUrl} locale={locale} />
    </>
  );
};

export default Layout;
