import { LOCALES, PathParams, FT_IB_REDIRECT_AFTER_LOGIN } from '@whitbread-eos/api';
import { Wizard, WizardHeader } from '@whitbread-eos/layout';
import {
  formatIBAssetsUrl,
  getTranslations,
  getCountryLanguageByLocale,
  TranslationProvider,
  getServerUnleashToggles,
} from '@whitbread-eos/utils/server';
import { Metadata } from 'next';
import { headers } from 'next/headers';
import React from 'react';

import { LoginForm } from './components/LoginForm';
import { LoginState, LoginStep } from './components/types';

type Props = {
  params?: Promise<PathParams>;
};
const PAGE_LABEL = 'IB | Login Page';

export const metadata: Metadata = {
  robots: {
    index: true,
    follow: true,
  },
};

export default async function LoginPage({ params }: Props) {
  const resolvedParams = await params;
  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? LOCALES.EN;
  const { language } = getCountryLanguageByLocale(locale);
  const { t, translations } = await getTranslations(language, ['common', 'icons', 'auth']);
  const icons = translations?.['icons'] ?? {};

  const headerList = await headers();
  const currentPath = headerList.get('WB-Url') ?? '';

  const flagsFallback = {
    [FT_IB_REDIRECT_AFTER_LOGIN]: false,
  };
  const [toggles] = await Promise.all([
    getServerUnleashToggles(PAGE_LABEL, flagsFallback, currentPath, {}),
  ]);

  // set flag for redirect after login - server side
  const isRedirectAfterLoginEnabled = toggles?.[FT_IB_REDIRECT_AFTER_LOGIN] ?? false;
  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';

  return (
    <TranslationProvider value={translations}>
      <Wizard<LoginState>
        icons={icons}
        initialStepId={LoginStep.LOGIN_FORM}
        initialState={{
          email: '',
          password: '',
          redirect: 'homepage',
        }}
        header={
          <WizardHeader
            logoUrl={formatIBAssetsUrl(t('common.content.header.image'))}
            logoRedirectUrl="account/login"
          />
        }
        steps={[
          {
            id: LoginStep.LOGIN_FORM,
            component: (
              <LoginForm
                baseDataTestId="LoginPage"
                icons={icons}
                locale={locale}
                secureUrl={secureUrl}
                isRedirectAfterLoginEnabled={isRedirectAfterLoginEnabled}
              />
            ),
          },
        ]}
      />
    </TranslationProvider>
  );
}
