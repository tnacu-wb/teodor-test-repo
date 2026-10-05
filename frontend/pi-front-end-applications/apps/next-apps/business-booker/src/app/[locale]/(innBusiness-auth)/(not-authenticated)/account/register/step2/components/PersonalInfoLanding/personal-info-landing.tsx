'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { BUSINESS_BOOKER_USER_ROLES, Language } from '@whitbread-eos/api';
import { CompanyType } from '@whitbread-eos/api';
import { FormPersonTitle, FormInput, FormPhone, Button } from '@whitbread-eos/atoms/ui';
import { useWizardContext, WizardPage } from '@whitbread-eos/layout';
import {
  useTranslation,
  formatIBAssetsUrl,
  formatAnalyticsFunnelStep,
  analytics,
} from '@whitbread-eos/utils';
import { useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

import { RegisterPersonalInformationState } from '../types';

interface Props {
  baseDataTestId?: string;
  language: Language;
  accessLevel?: string;
}

export function PersonalInfoLanding({ baseDataTestId, language, accessLevel }: Props) {
  const { t } = useTranslation(['auth', 'users']);
  const { wizardState, setWizardState, icons, goToNextStep } =
    useWizardContext<RegisterPersonalInformationState>();

  useEffect(() => {
    const funnelStep = formatAnalyticsFunnelStep('PIB', 'Confirm your Account - Details', language);
    const companyType =
      accessLevel === BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_MANAGER ||
      accessLevel === BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_USER
        ? CompanyType.BUSINESS_PAY
        : CompanyType.BUSINESS_BOOKER;

    analytics.update({
      siteType: 'PIB',
      funnel_step: funnelStep,
      userLevel: accessLevel,
      businessAccountType: companyType,
    });
  }, [language, accessLevel]);

  const schema = z.object({
    title: z.object({
      displayValue: z.string().min(1, t('users.userMgmt.employee.add.form.title.required')),
      value: z.string(),
    }),
    firstName: z
      .string()
      .min(2, t('users.userMgmt.employee.edit.error.firstNameFormat'))
      .max(30, t('users.userMgmt.employee.edit.error.firstNameFormat'))
      .regex(/^[A-Za-z\s-]+$/, {
        message: t('users.userMgmt.employee.edit.error.firstNameFormat'),
      }),
    lastName: z
      .string()
      .min(2, t('users.userMgmt.employee.edit.error.lastNameFormat'))
      .max(30, t('users.userMgmt.employee.edit.error.lastNameFormat'))
      .regex(/^[A-Za-z\s-]+$/, { message: t('users.userMgmt.employee.edit.error.lastNameFormat') }),
    phoneNumber: z.object({
      prefix: z.string(),
      phoneNumber: z
        .string()
        .min(6, t('users.userMgmt.employee.edit.error.telephoneFormat'))
        .regex(/^\d+$/, { message: t('users.userMgmt.employee.edit.error.telephoneFormat') }),
    }),
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
      title: {
        displayValue: wizardState.title,
        value: wizardState.title,
      },
      firstName: wizardState.firstName,
      lastName: wizardState.lastName,
      phoneNumber: { prefix: wizardState.phone.prefix, phoneNumber: wizardState.phone.phoneNumber },
    },
  });

  const handleClick = (data: Record<string, any>) => {
    setWizardState({
      ...wizardState,
      title: data.title.value,
      firstName: data.firstName,
      lastName: data.lastName,
      phone: {
        prefix: data.phoneNumber.prefix,
        phoneNumber: data.phoneNumber.phoneNumber,
      },
    });

    goToNextStep();
  };

  return (
    <WizardPage
      type="form"
      formTitle={t('auth.signup.personalInfo.title')}
      className={containerStyle}
      showBackButton={false}
    >
      <h2 data-testid={`${baseDataTestId}-PersonalInfoLanding-Title`} className={descriptionStyle}>
        {t('auth.signup.personalInfo.description')}
      </h2>
      <form
        id={`${baseDataTestId}-PersonalInfoLanding-Form`}
        data-testid={`${baseDataTestId}-PersonalInfoLanding-Form`}
        className={formStyle}
        onSubmit={handleSubmit(handleClick)}
      >
        <Controller
          name="title"
          control={control}
          render={({ field }) => (
            <FormPersonTitle
              {...field}
              id="Title"
              showLabel={false}
              placeholder={t('auth.signup.personalInfo.titleDropdown.label')}
              errors={errors}
              icons={icons}
              titleOptions={t('auth.signup.form.nameTitles')}
            />
          )}
        />
        <Controller
          name="firstName"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              id="FirstName"
              showLabel={false}
              type={'text'}
              placeholder={t('auth.signup.personalInfo.firstName.placeholder')}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            />
          )}
        />
        <Controller
          name="lastName"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              id="LastName"
              showLabel={false}
              type={'text'}
              placeholder={t('auth.signup.personalInfo.lastName.placeholder')}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            />
          )}
        />
        <Controller
          name="phoneNumber"
          control={control}
          render={({ field }) => (
            <FormPhone
              {...field}
              id="PhoneNumber"
              showLabel={false}
              language={language}
              placeholder={t('auth.signup.personalInfo.contactNumber.label')}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
            />
          )}
        />
        <Button
          variant="dialogDefault"
          className={buttonStyle}
          data-testid="footer-button"
          type="submit"
        >
          {t('auth.signup.personalInfo.continue.button')}
        </Button>
      </form>
    </WizardPage>
  );
}

const containerStyle = 'mobile:grow-0';
const descriptionStyle = 'mt-[1rem] mb-[3rem]';
const formStyle = 'flex flex-col gap-6';
const buttonStyle = 'w-full h-14 mt-[1.5rem]';
