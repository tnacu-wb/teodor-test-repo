import {
  FT_DYNATRACE_RUM_COOKIE_CONSENT,
  PathParams,
  LOCALES,
  FT_IB_REDIRECT_AFTER_LOGIN,
  FT_ONE_TRUST_COOKIE_CONSENT,
} from '@whitbread-eos/api';
import { Analytics } from '@whitbread-eos/atoms/ui';
import { CookieConsentClientWrapper } from '@whitbread-eos/layout';
import {
  getServerUnleashToggles,
  getCountryLanguageByLocale,
  ID_TOKEN_COOKIE,
  CONSENT_COOKIE,
  getTranslations,
  TranslationProvider,
  getPathForLocale,
  getRegistrationInfo,
  getUserDetails,
  getDetailsFromToken,
  isOneTrustCookieConsentActive,
} from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';
import { redirect } from 'next/navigation';

import { AuthGuard } from '~components/innBusiness/AuthGuard/auth-guard';
import getPageUrlBeforeRedirectToLogin from '~utils/getPageUrlBeforeRedirectToLogin';

import { RegistrationWizard } from './components/RegistrationWizard';

const PAGE_LABEL = 'IB | BUSACC | Business Account Registration Details';
const LOG_PAGE_NAME = 'business-pay-register-details' as const;

type Props = {
  params?: Promise<
    PathParams & {
      registrationCode: string;
    }
  >;
};

export default async function PayApplicationApply({ params }: Props) {
  const resolvedParams = await params;
  const baseDataTestId = 'RegisterIbPage';
  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? '';
  const headersList = await headers();
  if (!locale || !Object.values(LOCALES).includes(locale)) {
    redirect(`/${LOCALES.EN}/homepage`);
  }

  const flagsFallback = {
    [FT_DYNATRACE_RUM_COOKIE_CONSENT]: false,
    [FT_IB_REDIRECT_AFTER_LOGIN]: false,
    [FT_ONE_TRUST_COOKIE_CONSENT]: false,
  };

  const { language } = getCountryLanguageByLocale(locale);
  const currentPath = headersList.get('WB-Url') ?? '';
  const registrationCode = resolvedParams?.registrationCode;

  const cookieStore = await cookies();
  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { employeeId, companyId } = getDetailsFromToken(token);
  const logContext = { pageName: LOG_PAGE_NAME, userId: employeeId, companyId };
  const [toggles, { translations }, registrationInfo, userDetails] = await Promise.all([
    getServerUnleashToggles(PAGE_LABEL, flagsFallback, currentPath, {}),
    getTranslations(language, ['auth', 'spending']),
    getRegistrationInfo(token, registrationCode, logContext),
    getUserDetails(token),
  ]);

  const isRedirectAfterLoginEnabled = toggles?.[FT_IB_REDIRECT_AFTER_LOGIN] ?? false;
  if (!token) {
    getPageUrlBeforeRedirectToLogin(currentPath, locale, isRedirectAfterLoginEnabled);
  }

  if (!registrationCode) {
    redirect(getPathForLocale(locale, 'business-pay/register'));
  }

  if (!registrationInfo?.registrationCodeInfo?.registrationCode) {
    redirect(getPathForLocale(locale, 'business-pay/register'));
  }

  const cookieConsent = cookieStore.get(CONSENT_COOKIE)?.value ?? '';
  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';
  const isOneTrustActive = isOneTrustCookieConsentActive(
    toggles?.[FT_ONE_TRUST_COOKIE_CONSENT],
    locale
  );

  return (
    <TranslationProvider value={translations}>
      <RegistrationWizard
        baseDataTestId={baseDataTestId}
        locale={locale}
        registrationCodeInfo={registrationInfo.registrationCodeInfo}
      />
      <CookieConsentClientWrapper
        show={!cookieConsent && !isOneTrustActive}
        shouldSyncDynatraceConsent={!isOneTrustActive}
        isDynatraceRumCookieConsentEnabled={toggles?.[FT_DYNATRACE_RUM_COOKIE_CONSENT] ?? false}
      />
      <Analytics userDetails={userDetails} />
      <AuthGuard secureUrl={secureUrl} locale={locale} />
    </TranslationProvider>
  );
}
