'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { ResetPasswordRequest, LOCALES } from '@whitbread-eos/api';
import {
  Button,
  useToast,
  Alert,
  AlertDescription,
  FormInputShowHide,
  AlertTitle,
  SanitizedContent,
} from '@whitbread-eos/atoms/ui';
import { WizardPage, useWizardContext } from '@whitbread-eos/layout';
import { useTranslation, formatIBAssetsUrl, getPathForLocale } from '@whitbread-eos/utils';
import { resetPassword } from '@whitbread-eos/utils/server';
import { Info } from 'lucide-react';
import { useRouter } from 'next/navigation';
import React, { useState } from 'react';
import { Controller, useForm } from 'react-hook-form';

import { ResetState } from '../types';
import { registerSchema } from './reset-password-form.schema';

interface Props {
  locale: string;
  icons: Record<string, string>;
}

export function ResetPasswordForm({ locale, icons }: Props) {
  const baseDataTestId = 'ResetPasswordForm';

  const router = useRouter();
  const { toast } = useToast();
  const { wizardState } = useWizardContext<ResetState>();
  const { t } = useTranslation('auth');
  const [showErrorAlert, setShowErrorAlert] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);

  const formMethods = useForm({
    resolver: zodResolver(registerSchema(t)),
    defaultValues: {
      password: '',
      confirmPassword: '',
    },
  });

  const onSubmit = async (data: { password: string; confirmPassword: string }) => {
    setIsUpdating(true);
    try {
      const success = await resetPassword(wizardState.passwordToken, {
        customerId: wizardState.email,
        newPassword: data.password,
      } as ResetPasswordRequest);

      if (success) {
        router.push(getPathForLocale(locale?.toLowerCase() as LOCALES, 'account/login'));

        toast({
          content: `${t('reset.success.title')}. ${t('reset.success.subtitle')}`,
          icon: formatIBAssetsUrl(icons['icon.notification.success']),
        });
      } else {
        setIsUpdating(false);
        setShowErrorAlert(true);
      }
    } catch (error) {
      setIsUpdating(false);
      setShowErrorAlert(true);
    }
  };

  return (
    <WizardPage>
      <div data-testid={`${baseDataTestId}-wrapper`} className={containerStyle}>
        <div className={contentContainerStyle}>
          <h2 data-testid={`${baseDataTestId}-title`} className={titleStyle}>
            {t('reset.page.title')}
          </h2>
          <p data-testid={`${baseDataTestId}-description`}>
            <SanitizedContent>{t('reset.page.description')}</SanitizedContent>
          </p>
        </div>
        <form onSubmit={formMethods.handleSubmit(onSubmit)}>
          <div className={formContainerStyle}>
            {showErrorAlert && (
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
            {wizardState.isInvalidKey && (
              <Alert variant="red" data-testid={`${baseDataTestId}-invalidKeyError`}>
                <Info className="w-4 h-4" />
                <AlertTitle>{t('signup.accessRestricted.title')}</AlertTitle>
                <AlertDescription className="text-sm m-0">
                  {t('signup.accessRestricted.description')}
                </AlertDescription>
              </Alert>
            )}
            <div className="flex flex-col gap-2">
              <Controller
                name="password"
                control={formMethods.control}
                render={({ field }) => (
                  <FormInputShowHide
                    {...field}
                    disabled={wizardState.isInvalidKey}
                    id={`${baseDataTestId}-password`}
                    type="password"
                    placeholder={t('reset.password.input.placeholder')}
                    errors={formMethods.formState.errors}
                    errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                    onBlur={() => {
                      formMethods.trigger('password');
                    }}
                    onFocus={() => {
                      formMethods.clearErrors('password');
                    }}
                    showContent={false}
                  />
                )}
              />
              <div data-testid={`${baseDataTestId}-rules`} className="pl-4 flex flex-col gap-1">
                <span className="text-sm font-semibold">
                  {t('reset.password.input.rules.title')}
                </span>
                <ul className="list-disc list-inside flex flex-col gap-1">
                  <li className="text-sm">
                    <span className="ml-[-0.25rem]">{t('reset.password.input.rules.item1')}</span>
                  </li>
                  <li className="text-sm">
                    <span className="ml-[-0.25rem]">{t('reset.password.input.rules.item2')}</span>
                  </li>
                  <li className="text-sm">
                    <span className="ml-[-0.25rem]">{t('reset.password.input.rules.item3')}</span>
                  </li>
                  <li className="text-sm">
                    <span className="ml-[-0.25rem]">{t('reset.password.input.rules.item4')}</span>
                  </li>
                </ul>
              </div>
            </div>
            <Controller
              name="confirmPassword"
              control={formMethods.control}
              render={({ field }) => (
                <FormInputShowHide
                  {...field}
                  disabled={wizardState.isInvalidKey}
                  id={`${baseDataTestId}-confirmPassword`}
                  type="password"
                  placeholder={t('reset.confirmpassword.input.placeholder')}
                  errors={formMethods.formState.errors}
                  errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                  onBlur={() => {
                    formMethods.trigger('confirmPassword');
                  }}
                  onFocus={() => {
                    formMethods.clearErrors('confirmPassword');
                  }}
                  showContent={false}
                />
              )}
            />
            <Button
              type="submit"
              data-testid={`${baseDataTestId}-savePassword`}
              className="w-full"
              disabled={wizardState.isInvalidKey || isUpdating}
              variant={wizardState.isInvalidKey ? 'disabled' : 'default'}
            >
              {t('reset.submit.label')}
            </Button>
          </div>
        </form>
      </div>
    </WizardPage>
  );
}

const containerStyle =
  'max-w-[26.25rem] gap-12 flex flex-col mobile:pt-6 mobile:px-4 pt-12 mx-auto';
const contentContainerStyle = 'flex flex-col gap-4';
const formContainerStyle = 'flex flex-col gap-6';
const titleStyle = 'text-[2.5rem] font-bold text-secondaryColor leading-[110%]';
