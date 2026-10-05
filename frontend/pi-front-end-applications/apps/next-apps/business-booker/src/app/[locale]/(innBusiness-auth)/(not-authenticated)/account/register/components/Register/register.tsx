'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { Language, UpdatePreferencesRequest, CompanyType } from '@whitbread-eos/api';
import { FormInput, Button, Checkbox, Notification } from '@whitbread-eos/atoms/ui';
import { WizardPage, useWizardContext } from '@whitbread-eos/layout';
import {
  GLOBALS,
  setPageAnalytics,
  analytics,
  formatIBAssetsUrl,
  useTranslation,
  getPathForLocale,
} from '@whitbread-eos/utils';
import { addressSchema, InnBRegistrationStepOne } from '@whitbread-eos/utils/server';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useEffect, useState } from 'react';
import { Controller, useForm, FormProvider } from 'react-hook-form';
import { z } from 'zod';

import { POSTCODE_DE, POSTCODE_UK } from '~components/constants/regex';

import { RegisterValidationState } from '../../page';
import {
  registerSchema,
  InnBRegistrationStepOnePayload,
  CompanyAddressPayload,
} from '../../utils/form-data';
import Analytics from '../Analytics/analytics';
import RegisterAddressFields from '../RegisterAddressFields/register-address-fields';

type Props = {
  icons: Record<string, string>;
  baseDataTestId?: string;
  language: Language;
  token?: string;
  locale: string;
  companyType: string;
};

const PAGE_NAME = 'Create Your Account';
export const UNIQUE_TAXPAYER_REFERENCE_REGEX = /^[a-zA-Z0-9]{4,20}$/;

const Register = ({ baseDataTestId, icons, language, locale, companyType }: Props) => {
  const { t } = useTranslation(['users', 'company', 'auth', 'profile']);
  const schema = registerSchema(t);
  const resolvedAddressSchema = addressSchema(t);
  const [optIn, setOptIn] = useState(false);
  const [showReportError, setShowReportError] = useState<
    { title: string; description: string } | undefined
  >(undefined);
  const [isUpdating, setIsUpdating] = useState(false);

  const { setWizardState, goToNextStep } = useWizardContext<RegisterValidationState>();
  const pathname = usePathname();

  useEffect(() => {
    window?._satellite?.track('signUpStart');
  }, []);

  useEffect(() => {
    if (pathname) {
      setPageAnalytics(pathname, 'PIB', {}, language);
      analytics.update({ businessAccountType: companyType || CompanyType.BUSINESS_BOOKER });
    }
  }, [pathname, language]);

  const mergedSchema = schema
    .merge(resolvedAddressSchema)
    .superRefine(({ uniqueTaxpayerReference }, ctx) => {
      // All countries: 4-20 alphanumeric characters (A-Z, a-z, 0-9), no special characters or spaces
      if (
        uniqueTaxpayerReference &&
        !UNIQUE_TAXPAYER_REFERENCE_REGEX.test(uniqueTaxpayerReference)
      ) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          path: ['uniqueTaxpayerReference'],
          message: 'Invalid input.',
        });
      }
    })
    .superRefine(({ country, postCode }, ctx) => {
      if (country === GLOBALS.country.DE) {
        if (postCode && !POSTCODE_DE.test(postCode)) {
          ctx.addIssue({
            code: z.ZodIssueCode.custom,
            path: ['postCode'],
            message: t('users.userMgmt.employee.add.companyAddress.error.invalidPostcode'),
          });
        }
      } else if (country === GLOBALS.country.GB && postCode && !POSTCODE_UK.test(postCode)) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          path: ['postCode'],
          message: t('users.userMgmt.employee.add.companyAddress.error.invalidPostcode'),
        });
      }
    })
    .superRefine(({ addressLine1 }, ctx) => {
      if (addressLine1 && addressLine1.length > 255) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          path: ['addressLine1'],
          message: t('auth.error.addressLine1.required'),
        });
      }

      if (
        addressLine1 &&
        !/^[-A-Za-z0-9À-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ,\s']{2,35}$/.test(addressLine1)
      ) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          path: ['addressLine1'],
          message: t('auth.error.addressLine1.required'),
        });
      }
    })
    .superRefine(({ addressLine2 }, ctx) => {
      if (addressLine2 && !/^[-A-Za-z0-9À-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ,\s']{2,}$/.test(addressLine2)) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          path: ['addressLine2'],
          message: t('auth.error.addressLine2.invalid'),
        });
      }
    });

  type FormValues = z.infer<typeof mergedSchema>;
  const methods = useForm<FormValues>({
    resolver: zodResolver(mergedSchema),
    defaultValues: {
      emailAddress: '',
      companyName: '',
      postCode: '',
      selectAddress: {
        displayValue: '',
        value: '',
      },
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      addressLine5: '',
      country: '',
      socialMediaType: { displayValue: 'Website', value: 'website' },
      uniqueTaxpayerReference: '',
      socialMediaValue: '',
    },
  });

  const {
    control,
    formState: { errors },
    trigger,
    handleSubmit,
  } = methods;

  const formatRegisterPayload = (data: z.infer<typeof mergedSchema>) => {
    const companyAddressPayload: CompanyAddressPayload = {
      countryCode: data?.country ?? undefined,
      line1: data?.addressLine1 ?? undefined,
      line2: data?.addressLine2 ?? undefined,
      line3: data?.addressLine3 ?? undefined,
      line4: data?.addressLine4 ?? undefined,
      line5: data?.addressLine5 ?? undefined,
      postCode: data?.postCode ?? undefined,
    };

    const updatePreferencesRequest: UpdatePreferencesRequest = {
      brandCodes: ['PINN'],
      optIn: optIn,
      doubleOptIn: data?.country === GLOBALS.country.DE,
      customer: {
        countryOfResidence: data?.country ?? undefined,
        language: language,
      },
      sourceDetails: {
        channel: 'BB',
        journey: 'SIGNUP',
        locale: language === 'en' ? 'UK' : language.toUpperCase(),
      },
    };

    const innBRegistrationStepOnePayload: InnBRegistrationStepOnePayload = {
      companyName: data?.companyName,
      email: data?.emailAddress,
      address: companyAddressPayload,
      language: language,
      uniqueTaxpayerReference: data?.uniqueTaxpayerReference,
      companyType: companyType || undefined,
      updatePreferencesRequest,
    };

    return innBRegistrationStepOnePayload;
  };

  const handleSubmitRegister = async (data: z.infer<typeof mergedSchema>) => {
    setIsUpdating(true);

    const formattedRegisterData = formatRegisterPayload(data);
    const response = await InnBRegistrationStepOne(formattedRegisterData);

    if (response !== null && response?.innBRegistrationStepOne) {
      setWizardState({
        existingCompany: response?.innBRegistrationStepOne?.existingCompany ?? undefined,
        existingEmployee: response?.innBRegistrationStepOne?.existingEmployee ?? undefined,
        email: data?.emailAddress ?? '',
        company: formattedRegisterData?.companyName ?? '',
      });

      goToNextStep();
    } else {
      setIsUpdating(false);
      setShowReportError({
        title: t('auth.error.failure.heading'),
        description: t('auth.error.failure.description'),
      });
    }
  };

  const formErrors = Object.keys(errors).length > 0 ? Object.values(errors) : [];
  const analyticsValidation =
    formErrors || showReportError
      ? [
          ...(formErrors.map((error) => error?.message) as string[]),
          ...(showReportError ? [showReportError.title] : []),
        ]
      : undefined;

  return (
    <>
      <WizardPage
        type="form"
        formTitle={t('auth.signup.accountCreation.title')}
        data-testid={baseDataTestId}
        showBackButton={false}
      >
        <p className={'text-neutral-900 pt-[0.6rem] pb-[1rem]'}>
          {t('auth.signup.accountCreation.description')}
        </p>
        <p className="pb-[3rem]">
          {t('auth.signup.accountCreation.loginLink')}
          <Link className={linkStyle} href={getPathForLocale(locale, 'account/login')}>
            {t('auth.signup.accountExists.emailTaken.loginButton')}
          </Link>
        </p>
        {showReportError && (
          <Notification
            className={'mb-6'}
            type="error"
            icon={formatIBAssetsUrl(icons['icon.notification.error'])}
            title={showReportError.title}
            message={showReportError.description}
          />
        )}
        <FormProvider {...methods}>
          <form
            id={`${baseDataTestId}-Form`}
            onSubmit={handleSubmit(handleSubmitRegister)}
            className="flex flex-col gap-6 mobile:max-w-[100%]"
          >
            <Controller
              name="emailAddress"
              control={control}
              render={({ field }) => (
                <FormInput
                  {...field}
                  id="emailAddress"
                  type={'text'}
                  aria-required
                  placeholder={t('auth.signup.accountCreation.email.placeholder')}
                  errors={errors}
                  errorIcon={icons ? formatIBAssetsUrl(icons?.['icon.notification.error']) : ''}
                  onBlur={() => {
                    trigger('emailAddress');
                  }}
                />
              )}
            />
            <Controller
              name="companyName"
              control={control}
              render={({ field }) => (
                <FormInput
                  {...field}
                  id="companyName"
                  type={'text'}
                  aria-required
                  placeholder={t('auth.signup.accountCreation.companyName.placeholder')}
                  errors={errors}
                  errorIcon={icons ? formatIBAssetsUrl(icons?.['icon.notification.error']) : ''}
                  onBlur={() => {
                    trigger('companyName');
                  }}
                />
              )}
            />
            <RegisterAddressFields
              icons={icons}
              baseDataTestId={baseDataTestId}
              language={language}
            />
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
              data-testid={`${baseDataTestId}-Button`}
              type="submit"
              variant="saveUpdatesButton"
              className="min-w-[18rem] mobile:w-full"
              disabled={isUpdating}
            >
              {t('auth.signup.accountCreation.continue.button')}
            </Button>
          </form>
        </FormProvider>
        <Analytics pageName={PAGE_NAME} checkBox={optIn} validation={analyticsValidation} />
      </WizardPage>
    </>
  );
};

export default Register;
const checkboxStyle = 'mr-2 mt-[0.313rem] border-neutral-600';
const linkStyle =
  'underline text-secondaryColor pl-[0.313rem] hover:no-underline focus:no-underline';
