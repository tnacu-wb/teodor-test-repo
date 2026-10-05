'use client';

import { LOCALES } from '@whitbread-eos/api';
import { Button, SanitizedContent, Alert, AlertDescription } from '@whitbread-eos/atoms/ui';
import { useWizardContext, WizardPage } from '@whitbread-eos/layout';
import {
  useTranslation,
  getPathForLocale,
  getCountryLanguageByLocale,
  analytics,
  formatAnalyticsFunnelStep,
} from '@whitbread-eos/utils';
import { resendActivationEmail } from '@whitbread-eos/utils/server';
import { Info, CircleCheck } from 'lucide-react';
import { useRouter } from 'next/navigation';
import React, { useCallback, useEffect, useState } from 'react';

import { RegisterValidationState } from '../../page';
import Analytics from '../Analytics/analytics';

enum ToastState {
  SUCCESS = 'success',
  ERROR = 'red',
  NOT_SHOWED = 'NOT_SHOWED',
}

interface Props {
  locale: LOCALES;
}

const PAGE_NAME = 'Confirm Account';

export default function ConfirmationEmail({ locale }: Props) {
  const baseDataTestId = 'ConfirmationEmail';
  const router = useRouter();

  const { t } = useTranslation('auth');
  const { wizardState } = useWizardContext<RegisterValidationState>();
  const { language } = getCountryLanguageByLocale(locale);
  const [toastState, setToastState] = useState<ToastState>(ToastState.NOT_SHOWED);

  const handleResend = useCallback(async () => {
    const result = await resendActivationEmail(wizardState.email, wizardState.company, language);

    setToastState(result !== null ? ToastState.SUCCESS : ToastState.ERROR);
  }, [wizardState.email, wizardState.company, language]);

  useEffect(() => {
    const funnelStep = formatAnalyticsFunnelStep('PIB', 'Create Account - Confirm Email', language);
    analytics.update({ funnel_step: funnelStep });
  }, [language]);

  return (
    <WizardPage type="form" showBackButton={false} formTitle={t('signup.confirm.email.heading')}>
      <div data-testid={`${baseDataTestId}-wrapper`} className={wrapperStyle}>
        <div className={contentContainerStyle}>
          <p data-testid={`${baseDataTestId}-description`}>
            <SanitizedContent
              replacements={{ '\\[address\\]': `<strong>${wizardState.email}</strong>` }}
            >
              {t('signup.confirm.email.description')}
            </SanitizedContent>
          </p>
        </div>
        <div className={resendContainerStyle}>
          {toastState !== ToastState.NOT_SHOWED && (
            <Alert variant={toastState} data-testid={`${baseDataTestId}-${toastState}-alert`}>
              {toastState === ToastState.SUCCESS ? (
                <CircleCheck className="w-4 h-4" />
              ) : (
                <Info className="w-4 h-4" />
              )}
              <AlertDescription>
                {toastState === ToastState.SUCCESS ? (
                  <SanitizedContent>{t('resendEmail.success.message')}</SanitizedContent>
                ) : (
                  <SanitizedContent>{t('resendEmail.failure.message')}</SanitizedContent>
                )}
              </AlertDescription>
            </Alert>
          )}
          <Button
            onClick={handleResend}
            variant="link"
            data-testid={`${baseDataTestId}-resend`}
            className={resendStyle}
          >
            {t('signup.confirm.email.resend.link')}
          </Button>
        </div>
        <Button
          data-testid={`${baseDataTestId}-back-to-home`}
          onClick={() => {
            router.push(getPathForLocale(locale, 'account/login'));
          }}
          variant="default"
        >
          {t('signup.confirm.email.backToHome.button')}
        </Button>
      </div>
      <Analytics pageName={PAGE_NAME} />
    </WizardPage>
  );
}

const wrapperStyle = 'w-full flex gap-12 flex flex-col justify-center mobile:pt-6 mobile:px-4 pt-4';
const contentContainerStyle = 'flex flex-col gap-4';
const resendContainerStyle = 'w-full flex flex-col justify-center gap-4';
const resendStyle = 'text-base p-0 h-[1.5rem] text-secondaryColor';
