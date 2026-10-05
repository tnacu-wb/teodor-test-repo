'use client';

import { LOCALES } from '@whitbread-eos/api';
import { Button, Alert, AlertDescription, SanitizedContent } from '@whitbread-eos/atoms/ui';
import { useWizardContext, WizardPage } from '@whitbread-eos/layout';
import { useTranslation, getPathForLocale, getCountryLanguageByLocale } from '@whitbread-eos/utils';
import { forgotPassword } from '@whitbread-eos/utils/server';
import { CircleCheck, Info } from 'lucide-react';
import Link from 'next/link';
import React, { useState } from 'react';

import { ForgotConfirmationState, ForgotState } from '../types';

interface Props {
  locale: LOCALES;
}

export function ConfirmationView({ locale }: Props) {
  const baseDataTestId = 'EmailSentConfirmation';
  const { language } = getCountryLanguageByLocale(locale);
  const [isUpdating, setIsUpdating] = useState(false);

  const { wizardState, setWizardState } = useWizardContext<ForgotState>();

  const { t } = useTranslation('auth');

  const resendEmail = async () => {
    setIsUpdating(true);
    try {
      const response = await forgotPassword(language, true, {
        username: wizardState.email,
      });

      if (response.success) {
        setIsUpdating(false);
        setWizardState({
          ...wizardState,
          confirmationState: ForgotConfirmationState.SUCCESS,
        });
      } else {
        setIsUpdating(false);
        setWizardState({
          ...wizardState,
          confirmationState: ForgotConfirmationState.ERROR,
        });
      }
    } catch (error) {
      setIsUpdating(false);
      setWizardState({
        ...wizardState,
        confirmationState: ForgotConfirmationState.ERROR,
      });
    }
  };

  return (
    <WizardPage>
      <div data-testid={`${baseDataTestId}-wrapper`} className={containerStyle}>
        <h2 data-testid={`${baseDataTestId}-title`} className={titleStyle}>
          {t('forgotten.page.title')}
        </h2>
        <div className={contentContainerStyle}>
          {wizardState.confirmationState === ForgotConfirmationState.DEFAULT && (
            <Alert variant="success" data-testid={`${baseDataTestId}-defaultMessage`}>
              <CircleCheck className="w-4 h-4" />
              <AlertDescription className="text-sm m-0">
                {t('forgotten.page.resend.defaultAlert')}
              </AlertDescription>
            </Alert>
          )}
          <p>
            <SanitizedContent
              replacements={{
                '{email}': `<strong>${wizardState.email || ''}</strong>`,
              }}
            >
              {t('forgotten.page.resend.description1')}
            </SanitizedContent>
          </p>
          <p>{t('forgotten.page.resend.description2')}</p>
        </div>
        <div className={contentContainerStyle}>
          {wizardState.confirmationState === ForgotConfirmationState.SUCCESS && (
            <Alert variant="success" data-testid={`${baseDataTestId}-successMessage`}>
              <CircleCheck className="w-4 h-4" />
              <AlertDescription className="text-sm m-0">
                {t('forgotten.page.resend.successAlert')}
              </AlertDescription>
            </Alert>
          )}
          {wizardState.confirmationState === ForgotConfirmationState.ERROR && (
            <Alert variant="red" data-testid={`${baseDataTestId}-errorMessage`}>
              <Info className="w-4 h-4" />
              <AlertDescription className="text-sm m-0">
                <SanitizedContent
                  replacements={{
                    '{contactUrl}': getPathForLocale(locale, 'contact-us'),
                  }}
                >
                  {t('forgotten.page.resend.errorAlert')}
                </SanitizedContent>
              </AlertDescription>
            </Alert>
          )}
          <Button
            variant="link"
            data-testid={`${baseDataTestId}-resendEmail`}
            className={backToLoginStyle}
            onClick={resendEmail}
            disabled={isUpdating}
          >
            {t('forgotten.noemail.link')}
          </Button>
        </div>
        <Link
          className="w-full"
          href={getPathForLocale(locale.toLowerCase() as LOCALES, `account/login`)}
        >
          <Button data-testid={`${baseDataTestId}-backToLogin`} className="w-full">
            {t('forgotten.page.back')}
          </Button>
        </Link>
      </div>
    </WizardPage>
  );
}

const containerStyle =
  'max-w-[26.25rem] gap-12 flex flex-col mobile:pt-6 mobile:px-4 pt-12 mx-auto';
const contentContainerStyle = 'flex flex-col gap-4';
const titleStyle = 'text-[2.5rem] font-bold text-secondaryColor leading-[110%]';
const backToLoginStyle = 'text-base p-0 h-[1.5rem] text-secondaryColor';
