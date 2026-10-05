import { LOCALES, PathParams, SearchParams } from '@whitbread-eos/api';
import { Wizard, WizardHeader } from '@whitbread-eos/layout';
import {
  formatIBAssetsUrl,
  getTranslations,
  getCountryLanguageByLocale,
  TranslationProvider,
  getPathForLocale,
  ID_TOKEN_COOKIE,
  validateResetKey,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';
import React from 'react';

import { ResetPasswordForm } from './components/ResetPasswordForm/reset-password-form';
import { ResetState, ResetStep } from './components/types';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

export default async function ResetPage({ params, searchParams }: Props) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;
  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? LOCALES.EN;
  const { language } = getCountryLanguageByLocale(locale);
  const key = resolvedSearchParams?.key ?? '';
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? null;

  if (!key || token) {
    redirect(getPathForLocale(locale, 'homepage'));
  }

  const [{ t, translations }, resetDetails] = await Promise.all([
    getTranslations(language, ['common', 'icons', 'auth', 'spending']),
    validateResetKey({
      resetKey: key,
    }),
  ]);

  const icons = translations?.['icons'] ?? {};
  const isInvalidKey = !resetDetails || !resetDetails.valid;

  return (
    <TranslationProvider value={translations}>
      <Wizard<ResetState>
        icons={icons}
        initialStepId={ResetStep.RESET_FORM}
        initialState={{
          email: resetDetails?.emailAddress ?? '',
          passwordToken: key,
          isInvalidKey,
        }}
        header={
          <WizardHeader
            logoUrl={formatIBAssetsUrl(t('common.content.header.image'))}
            logoRedirectUrl="account/login"
          />
        }
        steps={[
          {
            id: ResetStep.RESET_FORM,
            component: <ResetPasswordForm locale={locale} icons={icons} />,
          },
        ]}
      />
    </TranslationProvider>
  );
}
