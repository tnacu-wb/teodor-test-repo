'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { LOCALES } from '@whitbread-eos/api';
import {
  Button,
  FormInput,
  Alert,
  AlertDescription,
  AlertTitle,
  SanitizedContent,
} from '@whitbread-eos/atoms/ui';
import { useWizardContext, WizardPage } from '@whitbread-eos/layout';
import {
  useTranslation,
  formatIBAssetsUrl,
  getPathForLocale,
  getCountryLanguageByLocale,
} from '@whitbread-eos/utils';
import { forgotPassword } from '@whitbread-eos/utils/server';
import { Info } from 'lucide-react';
import Link from 'next/link';
import React, { useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

import { ForgotConfirmationState, ForgotState } from '../types';

interface Props {
  locale: LOCALES;
  icons: Record<string, string>;
}

export function ForgotForm({ locale, icons }: Props) {
  const baseDataTestId = 'ForgotForm';
  const { language } = getCountryLanguageByLocale(locale);
  const { setWizardState, goToNextStep } = useWizardContext<ForgotState>();
  const [showNotExistingAlert, setShowNotExistingAlert] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);

  const { t } = useTranslation('auth');

  const formMethods = useForm({
    resolver: zodResolver(
      z.object({
        email: z.string().email(t('forgotten.email.input.error')),
      })
    ),
    defaultValues: {
      email: '',
    },
  });

  const onSubmit = async (data: { email: string }) => {
    setIsUpdating(true);
    try {
      const response = await forgotPassword(language, true, {
        username: data.email,
      });

      if (!response.success) {
        setIsUpdating(false);
        setShowNotExistingAlert(true);
      } else {
        setWizardState({
          email: data.email,
          confirmationState: ForgotConfirmationState.DEFAULT,
        });

        goToNextStep();
      }
    } catch (error) {
      setIsUpdating(false);
      setWizardState({
        email: data.email,
        confirmationState: ForgotConfirmationState.ERROR,
      });

      goToNextStep();
    }
  };

  return (
    <WizardPage>
      <div data-testid={`${baseDataTestId}-wrapper`} className={containerStyle}>
        <div className={contentContainerStyle}>
          <h2 data-testid={`${baseDataTestId}-title`} className={titleStyle}>
            {t('forgotten.page.title')}
          </h2>
          <p data-testid={`${baseDataTestId}-description`}>{t('forgotten.page.subtitle')}</p>
        </div>
        <form onSubmit={formMethods.handleSubmit(onSubmit)}>
          <div className={formContainerStyle}>
            {showNotExistingAlert && (
              <Alert variant="red" data-testid={`${baseDataTestId}-accountNotExist`}>
                <Info className="w-4 h-4" />
                <AlertTitle className="font-semibold">
                  {t('forgotten.page.existingAccountAlert.title')}
                </AlertTitle>
                <AlertDescription className="text-sm">
                  <SanitizedContent
                    replacements={{ '{registerUrl}': getPathForLocale(locale, 'account/register') }}
                  >
                    {t('forgotten.page.existingAccountAlert.description')}
                  </SanitizedContent>
                </AlertDescription>
              </Alert>
            )}
            <Controller
              name="email"
              control={formMethods.control}
              render={({ field }) => (
                <FormInput
                  {...field}
                  id={`${baseDataTestId}-email`}
                  type="text"
                  placeholder={t('forgotten.email.input.placeholder')}
                  errors={formMethods.formState.errors}
                  errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                  onBlur={() => {
                    formMethods.trigger('email');
                  }}
                  onFocus={() => {
                    formMethods.clearErrors('email');
                  }}
                />
              )}
            />
            <Button
              type="submit"
              data-testid={`${baseDataTestId}-sendEmail`}
              className="w-full"
              disabled={isUpdating}
            >
              {t('forgotten.submit.label')}
            </Button>
          </div>
        </form>
        <Link
          className="w-full"
          href={getPathForLocale(locale.toLowerCase() as LOCALES, `account/login`)}
        >
          <Button
            variant="link"
            data-testid={`${baseDataTestId}-backToLogin`}
            className={backToLoginStyle}
          >
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
const formContainerStyle = 'flex flex-col gap-6';
const titleStyle = 'text-[2.5rem] font-bold text-secondaryColor leading-[110%]';
const backToLoginStyle = 'text-base p-0 h-[1.5rem] text-secondaryColor w-full';
