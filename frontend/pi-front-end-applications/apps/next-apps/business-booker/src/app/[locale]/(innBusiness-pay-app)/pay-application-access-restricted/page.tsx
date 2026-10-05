import {
  FT_DYNATRACE_RUM_COOKIE_CONSENT,
  LOCALES,
  PathParams,
  FT_ONE_TRUST_COOKIE_CONSENT,
} from '@whitbread-eos/api';
import { Analytics } from '@whitbread-eos/atoms/ui';
import { Wizard, WizardHeader, CookieConsentClientWrapper } from '@whitbread-eos/layout';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  formatIBAssetsUrl,
  CONSENT_COOKIE,
  getServerUnleashToggles,
  isOneTrustCookieConsentActive,
} from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';
import React from 'react';

import { AuthGuard } from '~components/innBusiness/AuthGuard/auth-guard';

import PayApplicationAccessRestrictedView from './components/PayApplicationAccessRestrictedView/pay-application-access-restricted-view';
import { PayApplicationAccessRestrictedStep } from './components/types';

type Props = {
  params?: Promise<PathParams>;
};

export default async function PayApplicationAccessRestricted({ params }: Props) {
  const resolvedParams = await params;
  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? LOCALES.EN;
  const { language } = getCountryLanguageByLocale(locale);
  const { t, translations } = await getTranslations(language, ['common', 'icons', 'auth']);
  const icons = translations?.['icons'] ?? {};
  const cookieStore = await cookies();
  const headersList = await headers();
  const currentPath = headersList.get('WB-Url') ?? '';
  const flagsFallback = {
    [FT_DYNATRACE_RUM_COOKIE_CONSENT]: false,
    [FT_ONE_TRUST_COOKIE_CONSENT]: false,
  };
  const toggles = await getServerUnleashToggles(
    'IB | PAYAPP | Access Restricted',
    flagsFallback,
    currentPath,
    {}
  );

  const cookieConsent = cookieStore.get(CONSENT_COOKIE)?.value ?? '';
  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';
  const isOneTrustActive = isOneTrustCookieConsentActive(
    toggles?.[FT_ONE_TRUST_COOKIE_CONSENT],
    locale
  );

  return (
    <TranslationProvider value={translations}>
      <Wizard
        icons={icons}
        header={<WizardHeader logoUrl={formatIBAssetsUrl(t('common.content.header.image'))} />}
        initialState={{}}
        initialStepId={PayApplicationAccessRestrictedStep.ACCESS_RESTRICTED}
        steps={[
          {
            id: PayApplicationAccessRestrictedStep.ACCESS_RESTRICTED,
            component: <PayApplicationAccessRestrictedView locale={locale} />,
          },
        ]}
      />
      <CookieConsentClientWrapper
        show={!cookieConsent && !isOneTrustActive}
        shouldSyncDynatraceConsent={!isOneTrustActive}
        isDynatraceRumCookieConsentEnabled={toggles?.[FT_DYNATRACE_RUM_COOKIE_CONSENT] ?? false}
      />
      <AuthGuard secureUrl={secureUrl} locale={locale} />

      <Analytics />
    </TranslationProvider>
  );
}
