'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import {
  BUSINESS_BOOKER_USER_ROLES,
  CountryCode,
  FormattedAddress,
  Language,
  LOCALES,
  RegistrationQuestionWithAnswer,
  requestStatus,
  ShortCountry,
  UpdatePreferencesRequest,
} from '@whitbread-eos/api';
import {
  FormPersonTitle,
  FormInput,
  FormPhone,
  Button,
  FormInputShowHide,
  FormSelect,
  SanitizedContent,
  Notification,
  Checkbox,
} from '@whitbread-eos/atoms/ui';
import { useWizardContext, WizardPage } from '@whitbread-eos/layout';
import { GLOBALS, useInnBusinessLogin, parsePhoneNumber } from '@whitbread-eos/utils';
import { useTranslation, formatIBAssetsUrl, getPathForLocale } from '@whitbread-eos/utils';
import {
  defaultQuestionsAndSchema,
  parseAnswersObj,
  updateEmployeeDetails,
  addressSchema,
  getMarketingPreferences,
} from '@whitbread-eos/utils/server';
import { useState, useEffect } from 'react';
import { Controller, useForm, FormProvider } from 'react-hook-form';
import { z } from 'zod';

import { CompanyAddressFields } from '~components/innBusiness/forms/CompanyAddressForm/CompanyAddressFields';

import { EmployeeActivationState } from '../../page';
import { employeeActivationSchema } from '../../utils/form-data';
import Analytics from '../Analytics/analytics';

interface Props {
  baseDataTestId?: string;
  language: Language;
  country: string;
  companyRegistrationQuestions: RegistrationQuestionWithAnswer[];
  secureUrl: string;
  locale: LOCALES;
}

export function EmployeeActivation({
  baseDataTestId,
  language,
  country,
  companyRegistrationQuestions,
  secureUrl,
  locale,
}: Props) {
  const { t } = useTranslation(['auth', 'users', 'spending', 'profile']);
  const isGermanLanguage = language === CountryCode.DE.toLowerCase();
  const countryByLanguage = isGermanLanguage ? ShortCountry.DE : ShortCountry.GB;
  const { wizardState, icons } = useWizardContext<EmployeeActivationState>();
  const {
    title,
    firstName,
    lastName,
    emailAddress,
    phoneNumber,
    mobileNumber,
    employeeId,
    companyId,
    companyName,
    address,
    activationKey,
    accessLevel,
  } = wizardState;
  const [isAccountUpdating, setIsAccountUpdating] = useState(false);
  const [showErrorNotification, setShowErrorNotification] = useState(false);
  const [optIn, setOptIn] = useState(false);
  const employeeSchema = employeeActivationSchema(t);
  const employeeAddressSchema = addressSchema(t);
  const { questionsSchemaObj, defaultQuestionsObj } = defaultQuestionsAndSchema(
    companyRegistrationQuestions,
    t('auth.signup.security.question1.error'),
    t('auth.signup.security.question2.error'),
    true
  );
  const fetchMarketingPreferences = async () => {
    const preferencesResponse = await getMarketingPreferences('PINN', emailAddress);

    if (preferencesResponse) {
      setOptIn(preferencesResponse.optIn);
    }
  };

  useEffect(() => {
    fetchMarketingPreferences();
  }, []);

  const PAGE_NAME = 'Employee Activation';

  const countryByAddress = address?.country
    ? address.country === 'D'
      ? GLOBALS.country.DE
      : address.country
    : countryByLanguage;

  const defaultAddress: FormattedAddress = address
    ? {
        ...address,
        postalCode: address.postCode,
        country: countryByAddress,
        label: '',
      }
    : {
        addressLine1: '',
        postalCode: '',
        label: '',
        country: countryByAddress,
      };

  const mergedSchema = employeeSchema
    .merge(employeeAddressSchema)
    .merge(z.object(questionsSchemaObj));

  const methods = useForm({
    mode: 'onSubmit',
    reValidateMode: 'onSubmit',
    resolver: zodResolver(mergedSchema),
    defaultValues: {
      ...{
        title: {
          displayValue: title ?? '',
          value: title ?? '',
        },
        firstName: firstName ?? '',
        lastName: lastName ?? '',
        emailAddress: emailAddress,
        phoneNumber: parsePhoneNumber(phoneNumber ?? '', language),
        alternatePhoneNumber: parsePhoneNumber(mobileNumber ?? '', language),
        addressLine1: address?.addressLine1 ?? '',
        addressLine2: address?.addressLine2 ?? '',
        addressLine3: address?.addressLine3 ?? '',
        addressLine4: address?.addressLine4 ?? '',
        addressLine5: address?.addressLine5 ?? '',
        postCode: address?.postCode ?? '',
        country: countryByAddress,
        createPassword: '',
      },
      ...defaultQuestionsObj,
    },
  });

  const {
    control,
    formState: { errors },
    trigger,
    handleSubmit,
    clearErrors,
  } = methods;

  const formErrors = (Object.keys(errors).length > 0 ? Object.values(errors) : []) as any;
  const analyticsValidation = formErrors
    ? (formErrors.map((error: Record<string, any>) => {
        const errorMessage =
          error?.message || error?.displayValue?.message || error?.phoneNumber?.message;

        return errorMessage.split('.').join('');
      }) as string[])
    : undefined;

  const parseOptions = (options: string[] | null) => {
    return options?.map((option, index) => ({
      value: (index + 1).toString(),
      displayValue: option,
    }));
  };

  const excludeKeys = [
    'customerReferenceAnswer',
    'purchaseOrderAnswer',
    'title',
    'firstName',
    'lastName',
    'emailAddress',
    'phoneNumber',
    'alternatePhoneNumber',
    'createPassword',
  ];

  const loginSchema = z.object({
    email: z.string().email('Enter a valid value in this required field.'),
    password: z.string().min(6, 'Enter a valid value in this required field.'),
  });

  const { handleLogin, isSubmitting: isLoggingIn } = useInnBusinessLogin(
    getPathForLocale(
      locale,
      accessLevel === BUSINESS_BOOKER_USER_ROLES.SUPER ||
        accessLevel === BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_MANAGER
        ? 'welcome'
        : 'homepage'
    ),
    secureUrl,
    loginSchema
  );

  const createAccount = async (data: Record<string, any>) => {
    setIsAccountUpdating(true);
    const answersObj = parseAnswersObj(data, excludeKeys);
    const localeCountry = String(locale).split('-')[1]?.toUpperCase();
    const marketingLocale = localeCountry === GLOBALS.country.GB ? 'UK' : localeCountry || 'UK';

    const updatePreferencesRequest: UpdatePreferencesRequest = {
      brandCodes: ['PINN'],
      optIn: optIn,
      doubleOptIn: data?.country === GLOBALS.country.DE,
      customer: {
        title: data?.title?.value,
        firstName: data?.firstName,
        lastName: data?.lastName,
        countryOfResidence: data?.country ?? undefined,
        language: language,
      },
      sourceDetails: {
        channel: 'BB',
        journey: 'ACTIVATE',
        locale: marketingLocale,
      },
    };

    const employeeActivationParams = {
      title: data.title.value,
      firstName: data.firstName,
      lastName: data.lastName,
      emailAddress: data.emailAddress,
      phoneNumber: `${data.phoneNumber.prefix}${data.phoneNumber.phoneNumber}`,
      mobileNumber: data.alternatePhoneNumber.phoneNumber
        ? `${data.alternatePhoneNumber.prefix}${data.alternatePhoneNumber.phoneNumber}`
        : '',
      password: data.createPassword,
      id: employeeId,
      address: {
        addressLine1: data.addressLine1,
        addressLine2: data.addressLine2,
        addressLine3: data.addressLine3,
        addressLine4: data.addressLine4,
        addressLine5: data.addressLine5,
        postCode: data.postCode,
        country: data.country,
      },
      employeeAnswers: answersObj,
      accessLevel: accessLevel,
      updatePreferencesRequest,
    };

    try {
      const updateEmployeeResponse = await updateEmployeeDetails(
        companyId,
        employeeId,
        language?.toUpperCase(),
        employeeActivationParams,
        '',
        activationKey,
        updatePreferencesRequest
      );

      if (updateEmployeeResponse?.status === requestStatus.success) {
        setIsAccountUpdating(false);
        setShowErrorNotification(false);
        handleLogin({
          email: data.emailAddress,
          password: data.createPassword,
        });
      } else {
        setIsAccountUpdating(false);
        setShowErrorNotification(true);
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    } catch {
      setIsAccountUpdating(false);
      setShowErrorNotification(true);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  const renderQuestions = companyRegistrationQuestions?.map((question, count) => {
    const isOptionalQuestion = !question.mandatory && question.type !== 'select';

    return (
      <div data-testid={`Registration-Question-${count}`} key={question.id}>
        <span data-testid={`Registration-Question-Label-${count}`} className={questionLabelStyle}>
          {`${question.label} ${
            isOptionalQuestion ? t('auth.signup.security.question.optional') : ''
          }`}
        </span>
        {question.type === 'select' ? (
          <Controller
            name={question.id}
            control={control}
            render={({ field }) => (
              <FormSelect
                {...field}
                showLabel={false}
                id={question.id}
                className={questionInputStyle}
                placeholder={t('auth.signup.security.question1.placeholder')}
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
                options={parseOptions(question.options)}
                onBlur={() => {
                  trigger(question.id);
                }}
                onFocus={() => {
                  clearErrors(question.id);
                }}
              />
            )}
          />
        ) : (
          <Controller
            name={question.id as string}
            control={control}
            render={({ field }) => (
              <FormInput
                {...field}
                showLabel={false}
                id={question.id}
                className={questionInputStyle}
                type={'text'}
                placeholder={t('auth.signup.security.question2.placeholder')}
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                onBlur={() => {
                  trigger(question.id);
                }}
              />
            )}
          />
        )}
      </div>
    );
  });

  return (
    <WizardPage
      type="form"
      formTitle={t('auth.signup.page.title')}
      className={containerStyle}
      showBackButton={false}
    >
      <h2 data-testid={`${baseDataTestId}-EmployeeActivation-Title`} className={descriptionStyle}>
        {t('auth.signup.page.subtitle').replace('{name}', `${companyName}`)}
      </h2>
      {showErrorNotification && (
        <Notification
          className={'mb-6'}
          type="error"
          icon={formatIBAssetsUrl(icons['icon.notification.error'])}
          title={t('auth.error.failure.heading')}
          message={t('auth.error.failure.description')}
        />
      )}
      <FormProvider {...methods}>
        <form
          id={`${baseDataTestId}-EmployeeActivation-Form`}
          data-testid={`${baseDataTestId}-EmployeeActivation-Form`}
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
          <div className={sectionStyle}>
            <h3 className="font-bold">{t('auth.signup.personalInfo.name.sectionTitle')}</h3>
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
                  onBlur={() => {
                    trigger('title');
                  }}
                  onFocus={() => {
                    clearErrors('title');
                  }}
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
                  onBlur={() => {
                    trigger('firstName');
                  }}
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
                  onBlur={() => {
                    trigger('lastName');
                  }}
                />
              )}
            />
            <hr />

            <h3 className="font-bold">{t('auth.signup.contact.sectionTitle')}</h3>
            <Controller
              name="emailAddress"
              control={control}
              render={({ field }) => (
                <FormInput
                  {...field}
                  id="CompanyEmailAddress"
                  type={'text'}
                  placeholder={t('auth.signup.contact.email.placeholder')}
                  errors={errors}
                  errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                  onBlur={() => {
                    trigger('emailAddress');
                  }}
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
                  onBlur={() => {
                    trigger('phoneNumber');
                  }}
                />
              )}
            />
            <Controller
              name="alternatePhoneNumber"
              control={control}
              render={({ field }) => (
                <FormPhone
                  {...field}
                  className="placeholder-shown:text-ellipsis"
                  id="Alternate-Phone-Number"
                  language={language}
                  showLabel={false}
                  placeholder={t('auth.signup.contact.alternatePhone.placeholder')}
                  errors={errors}
                  errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                  arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
                  onBlur={() => {
                    trigger('alternatePhoneNumber');
                  }}
                />
              )}
            />

            <hr />

            <h3 className="font-bold">{t('auth.signup.address.sectionTitle')}</h3>
            <CompanyAddressFields icons={icons} language={language} address={defaultAddress} />

            <hr />

            <h3 className="font-bold">{t('auth.signup.password.sectionTitle')}</h3>
            <Controller
              name="createPassword"
              control={control}
              render={({ field }) => (
                <FormInputShowHide
                  {...field}
                  id="CreatePassword"
                  type="password"
                  showLabel={false}
                  placeholder={t('auth.signup.password.input.placeholder')}
                  errors={errors}
                  errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                  showContent={false}
                  onBlur={() => {
                    trigger('createPassword');
                  }}
                />
              )}
            />
            <div className={instructionsStyle}>
              <h4 data-testid={`${baseDataTestId}-list-title`} className={listTttleStyle}>
                {t('auth.signup.password.rules.title')}
              </h4>
              <ul data-testid={`${baseDataTestId}-list-content`} className={listContentStyle}>
                <li>{t('auth.signup.password.rule1')}</li>
                <li>{t('auth.signup.password.rule2')}</li>
                <li>{t('auth.signup.password.rule3')}</li>
                <li>{t('auth.signup.password.rule4')}</li>
              </ul>
            </div>
          </div>

          {companyRegistrationQuestions?.length > 0 && (
            <div
              data-testid={`${baseDataTestId}-Registration-Questions-Wrapper`}
              className={sectionStyle}
            >
              {renderQuestions}
            </div>
          )}

          <span data-testid={`${baseDataTestId}-Contact-Info`} className={contactInfoStyle}>
            <SanitizedContent>{t('auth.signup.contactUs.info')}</SanitizedContent>
          </span>

          <div className="inline-flex items-start">
            <Checkbox
              id="optIn"
              checked={optIn}
              onCheckedChange={() => {
                setOptIn(!optIn);
              }}
              className={checkboxStyle}
            />
            <label htmlFor="optIn">
              {t('auth.signup.accountCreation.newsletter.checkbox.label')}
            </label>
          </div>

          <Button
            variant="dialogDefault"
            className={buttonStyle}
            data-testid="footer-button"
            type="submit"
            disabled={isAccountUpdating || isLoggingIn}
          >
            {t('auth.signup.submit.button')}
          </Button>
        </form>
      </FormProvider>
      <Analytics pageName={PAGE_NAME} validation={analyticsValidation} />
    </WizardPage>
  );
}

const containerStyle = 'mobile:grow-0';
const descriptionStyle = 'mt-[1rem] mb-[3rem]';
const formStyle = 'flex flex-col gap-6';
const sectionStyle =
  'flex flex-col gap-6 bg-white p-[1.5rem] rounded-[.5rem] border border-solid border-lightGrey3';
const instructionsStyle = 'text-[.875rem] pl-[1rem]';
const listTttleStyle = 'font-semibold mb-[.25rem]';
const listContentStyle = 'list-disc pl-[1.5rem]';
const buttonStyle = 'w-full h-14';
const questionLabelStyle = 'flex font-bold text-base mb-[1.5rem]';
const questionInputStyle = 'mobile:min-w-full mr-auto border-lightGrey1';
const contactInfoStyle = 'my-[1.5rem]';
const checkboxStyle = 'mr-2 mt-[0.313rem] border-neutral-600';
