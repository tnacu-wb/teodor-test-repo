import { LOCALES, PathParams } from '@whitbread-eos/api';
import { Wizard, WizardHeader } from '@whitbread-eos/layout';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  formatIBAssetsUrl,
} from '@whitbread-eos/utils/server';
import React from 'react';

import AccessRestrictedView from './components/AccessRestrictedView/access-restricted-view';
import { AccessRestrictedStep } from './components/types';

type Props = {
  params?: Promise<PathParams>;
};

export default async function AccessRestricted({ params }: Props) {
  const resolvedParams = await params;
  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? LOCALES.EN;
  const { language } = getCountryLanguageByLocale(locale);
  const { t, translations } = await getTranslations(language, ['common', 'icons', 'auth']);
  const icons = translations?.['icons'] ?? {};

  return (
    <TranslationProvider value={translations}>
      <Wizard
        icons={icons}
        header={<WizardHeader logoUrl={formatIBAssetsUrl(t('common.content.header.image'))} />}
        initialState={{}}
        initialStepId={AccessRestrictedStep.ACCESS_RESTRICTED}
        steps={[
          {
            id: AccessRestrictedStep.ACCESS_RESTRICTED,
            component: <AccessRestrictedView locale={locale} />,
          },
        ]}
      />
    </TranslationProvider>
  );
}
