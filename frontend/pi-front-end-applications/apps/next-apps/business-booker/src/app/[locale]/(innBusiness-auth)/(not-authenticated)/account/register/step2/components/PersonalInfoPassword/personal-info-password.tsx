'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { Language, LOCALES, UpdatePreferencesRequest, HashType } from '@whitbread-eos/api';
import { FormInputShowHide, Button, SanitizedContent, Notification } from '@whitbread-eos/atoms/ui';
import { useWizardContext, WizardPage } from '@whitbread-eos/layout';
import {
  GLOBALS,
  useInnBusinessLogin,
  useTranslation,
  formatIBAssetsUrl,
  getPathForLocale,
  hashString,
  analytics,
  formatAnalyticsFunnelStep,
} from '@whitbread-eos/utils';
import { registrationStepTwo, getMarketingPreferences } from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

import { RegisterPersonalInformationState } from '../types';

interface Props {
  baseDataTestId?: string;
  locale: LOCALES;
  secureUrl: string;
  language: Language;
  country: string;
}

export function PersonalInfoPassword({
  baseDataTestId,
  locale,
  secureUrl,
  language,
  country,
}: Props) {
  const { t } = useTranslation(['auth', 'spending']);
  const { wizardState, icons, goToPreviousStep } =
    useWizardContext<RegisterPersonalInformationState>();
  const [isUpdating, setIsUpdating] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [showErrorNotification, setShowErrorNotification] = useState(false);
  const [marketingPreferences, setMarketingPreferences] = useState({
    optIn: false,
    doubleOptIn: false,
  });
  const { title, firstName, lastName, emailAddress, activationKey, countryCode } = wizardState;
  const passwordRegex = /^(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,}$/;
  const router = useRouter();

  useEffect(() => {
    const funnelStep = formatAnalyticsFunnelStep(
      'PIB',
      'Confirm your Account - Set Password',
      language
    );

    analytics.update({ funnel_step: funnelStep });
  }, [language]);

  const passwordSchema = z
    .string()
    .min(8, { message: t('auth.signup.password.validation') })
    .regex(passwordRegex, { message: t('auth.signup.password.validation') })
    .refine(
      (value) => {
        const normalizedValue = value.toLowerCase();
        const max2sameConsecutiveCharacters = /(.)\1\1/;
        return !max2sameConsecutiveCharacters.test(normalizedValue);
      },
      {
        message: t('auth.signup.password.validation'),
      }
    );

  const schema = z.object({
    setPassword: passwordSchema,
  });

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm({
    mode: 'onSubmit',
    reValidateMode: 'onSubmit',
    resolver: zodResolver(schema),
    defaultValues: {
      setPassword: '',
    },
  });

  const loginSchema = z.object({
    email: z.string().email('Enter a valid value in this required field.'),
    password: z.string().min(6, 'Enter a valid value in this required field.'),
  });
  const {
    handleLogin,
    isSubmitting,
    isError: isLoginError,
  } = useInnBusinessLogin(getPathForLocale(locale, 'welcome'), secureUrl, loginSchema);

  if (isSubmitted && isLoginError) {
    router.push(getPathForLocale(locale, 'account/login'));
  }

  const fetchMarketingPreferences = async () => {
    const preferencesResponse = await getMarketingPreferences('PINN', emailAddress);

    if (preferencesResponse) {
      setMarketingPreferences({
        optIn: preferencesResponse.optIn,
        doubleOptIn: preferencesResponse.secondOptIn,
      });
    }
  };

  useEffect(() => {
    fetchMarketingPreferences();
  }, []);

  const createAccount = async (data: Record<string, any>) => {
    setIsUpdating(true);
    const localeCountry = String(locale).split('-')[1]?.toUpperCase();
    const marketingLocale = localeCountry === GLOBALS.country.GB ? 'UK' : localeCountry || 'UK';

    const updatePreferencesRequest: UpdatePreferencesRequest = {
      brandCodes: ['PINN'],
      optIn: marketingPreferences.optIn,
      doubleOptIn: marketingPreferences.doubleOptIn,
      customer: {
        title,
        firstName,
        lastName,
        countryOfResidence: countryCode,
        language: language,
      },
      sourceDetails: {
        channel: 'BB',
        journey: 'ACTIVATE',
        locale: marketingLocale,
      },
    };

    const registrationParams = {
      activationKey,
      title,
      firstName,
      lastName,
      phoneNumber: `${wizardState.phone.prefix}${wizardState.phone.phoneNumber}`,
      password: data.setPassword,
      updatePreferencesRequest,
    };

    try {
      const stepTwoResponse = await registrationStepTwo(registrationParams);

      if (stepTwoResponse !== null) {
        if (stepTwoResponse?.companyId) {
          analytics.update({
            signupID: stepTwoResponse?.companyId,
          });
        } else {
          const hashedEmail = await hashString(stepTwoResponse?.email, HashType.SHA256);
          analytics.update({
            signupID: hashedEmail,
          });
        }
        setShowErrorNotification(false);
        handleLogin({
          email: stepTwoResponse?.email,
          password: data.setPassword,
        });
        setIsSubmitted(true);
      } else {
        setIsUpdating(false);
        setShowErrorNotification(true);
      }
    } catch {
      if (!isLoginError) {
        setIsUpdating(false);
        setShowErrorNotification(true);
      }
    }
  };

  return (
    <WizardPage
      type="form"
      formTitle={t('auth.signup.password.title')}
      className={containerStyle}
      onBackClick={goToPreviousStep}
    >
      <form
        id={`${baseDataTestId}-PersonalInfoPassword-Form`}
        data-testid={`${baseDataTestId}-PersonalInfoPassword-Form`}
        className={formStyle}
        onSubmit={handleSubmit(createAccount)}
      >
        <iframe
          src={`${secureUrl}/${country}/${language}/business-booker/common/login.html`}
          style={{ display: 'none' }}
          id="authIframe"
          title="BB login"
          data-testid={`${baseDataTestId}-Iframe`}
        ></iframe>
        {showErrorNotification && (
          <Notification
            className={'mb-6'}
            type="error"
            icon={formatIBAssetsUrl(icons['icon.notification.error'])}
            title={t('auth.error.failure.heading')}
            message={t('auth.error.failure.description')}
          />
        )}
        <Controller
          name="setPassword"
          control={control}
          render={({ field }) => (
            <FormInputShowHide
              {...field}
              id="SetPassword"
              type="password"
              placeholder={t('auth.signup.password.input.placeholder')}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              showContent={false}
            />
          )}
        />
        <div className={instructionsStyle}>
          <h4 data-testid={`${baseDataTestId}-list-title`} className={listTttleStyle}>
            {t('auth.signup.password.instructions')}
          </h4>
          <ul data-testid={`${baseDataTestId}-list-content`} className={listContentStyle}>
            <li>{t('auth.signup.password.rule1')}</li>
            <li>{t('auth.signup.password.rule2')}</li>
            <li>{t('auth.signup.password.rule3')}</li>
            <li>{t('auth.signup.password.rule4')}</li>
          </ul>
        </div>
        <div className={termsStyle}>
          <SanitizedContent>{t('auth.signup.password.terms.text')}</SanitizedContent>
        </div>
        <Button
          variant="dialogDefault"
          className={buttonStyle}
          data-testid="footer-button"
          type="submit"
          disabled={isUpdating || isSubmitting}
        >
          {t('auth.signup.password.createAccount.button')}
        </Button>
      </form>
    </WizardPage>
  );
}

const containerStyle = 'mobile:grow-0';
const formStyle = 'flex flex-col mt-[3rem]';
const instructionsStyle = 'text-[.875rem]  mt-[.5rem] pl-[1rem]';
const listTttleStyle = 'font-semibold mb-[.25rem]';
const listContentStyle = 'list-disc pl-[1.5rem]';
const termsStyle = 'mt-[3rem] text-base';
const buttonStyle = 'w-full h-14 mt-[3rem]';
