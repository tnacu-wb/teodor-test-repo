import { LOCALES, PathParams } from '@whitbread-eos/api';
import { Wizard, WizardHeader } from '@whitbread-eos/layout';
import {
  formatIBAssetsUrl,
  getTranslations,
  getCountryLanguageByLocale,
  TranslationProvider,
} from '@whitbread-eos/utils/server';
import React from 'react';

import { ConfirmationView } from './components/ConfirmationView/confirmation-view';
import { ForgotForm } from './components/ForgotForm/forgot-form';
import { ForgotConfirmationState, ForgotState, ForgotStep } from './components/types';

type Props = {
  params?: Promise<PathParams>;
};

export default async function ResetPage({ params }: Props) {
  const resolvedParams = await params;
  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? LOCALES.EN;
  const { language } = getCountryLanguageByLocale(locale);
  const { t, translations } = await getTranslations(language, ['common', 'icons', 'auth']);
  const icons = translations?.['icons'] ?? {};

  return (
    <TranslationProvider value={translations}>
      <Wizard<ForgotState>
        icons={icons}
        initialStepId={ForgotStep.FORGOT_FORM}
        initialState={{
          email: '',
          confirmationState: ForgotConfirmationState.DEFAULT,
        }}
        header={<WizardHeader logoUrl={formatIBAssetsUrl(t('common.content.header.image'))} />}
        steps={[
          {
            id: ForgotStep.FORGOT_FORM,
            component: <ForgotForm locale={locale} icons={icons} />,
          },
          {
            id: ForgotStep.FORGOT_CONFIRMATION,
            component: <ConfirmationView locale={locale} />,
          },
        ]}
      />
    </TranslationProvider>
  );
}
