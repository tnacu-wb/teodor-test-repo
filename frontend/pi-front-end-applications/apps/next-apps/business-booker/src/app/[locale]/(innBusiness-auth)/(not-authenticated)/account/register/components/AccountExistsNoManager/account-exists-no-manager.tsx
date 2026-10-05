'use client';

import { LOCALES } from '@whitbread-eos/api';
import { SanitizedContent, Button } from '@whitbread-eos/atoms/ui';
import { WizardPage } from '@whitbread-eos/layout';
import {
  useTranslation,
  getPathForLocale,
  analytics,
  formatAnalyticsFunnelStep,
  getCountryLanguageByLocale,
} from '@whitbread-eos/utils';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';

import Analytics from '../Analytics/analytics';

interface Props {
  locale: LOCALES;
}

const PAGE_NAME = 'Confirm Account';

export default function AccountExistsNoManager({ locale }: Props) {
  const baseDataTestId = 'AccountExistsNoManager';
  const router = useRouter();
  const { language } = getCountryLanguageByLocale(locale);
  const { t } = useTranslation('auth');

  useEffect(() => {
    const funnelStep = formatAnalyticsFunnelStep(
      'PIB',
      'Create Account - Existing Company',
      language
    );
    analytics.update({ funnel_step: funnelStep });
  }, [language]);

  return (
    <WizardPage
      type="form"
      showBackButton={false}
      formTitle={t('signup.accountExists.noManager.heading')}
    >
      <div data-testid={`${baseDataTestId}-wrapper`} className={wrapperStyle}>
        <div className={contentContainerStyle}>
          <p data-testid={`${baseDataTestId}-description`}>
            <SanitizedContent>{t('signup.accountExists.noManager.description')}</SanitizedContent>
          </p>
        </div>
        <Button
          onClick={() => {
            router.push(getPathForLocale(locale, 'account/login'));
          }}
          data-testid={`${baseDataTestId}-Button`}
          variant="default"
        >
          {t('signup.accountExists.noManager.button')}
        </Button>
      </div>
      <Analytics pageName={PAGE_NAME} />
    </WizardPage>
  );
}

const wrapperStyle =
  'w-full flex  gap-12 flex flex-col justify-center mobile:pt-6 mobile:px-4 pt-4';
const contentContainerStyle = 'flex flex-col gap-4';
