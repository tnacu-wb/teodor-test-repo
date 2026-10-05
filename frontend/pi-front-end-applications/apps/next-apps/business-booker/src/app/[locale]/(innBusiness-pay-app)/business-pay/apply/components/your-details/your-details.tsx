'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { Customer, requestStatus } from '@whitbread-eos/api';
import { FormInput, FormSelect } from '@whitbread-eos/atoms/ui';
import { WizardPage, WizardFooter, useWizardContext } from '@whitbread-eos/layout';
import {
  useTranslation,
  getLocaleByPathname,
  getPathForLocale,
  formatIBAssetsUrl,
  getAuthCookie,
} from '@whitbread-eos/utils';
import { updateAppContactDetails, updateResumeUrl } from '@whitbread-eos/utils/server';
import { usePathname, useRouter } from 'next/navigation';
import { useState } from 'react';
import { Controller, FormProvider, useForm } from 'react-hook-form';
import { z } from 'zod';

import { ReviewChanges } from '~components/innBusiness/ReviewChanges';

import { Analytics } from '../analytics/analytics';
import { useWorldlineErrorHandler } from '../error-handling';
import { PayApplicationState, PayApplicationStep } from '../types';

type Props = {
  titleValues: string[];
  userDetails: Customer;
  isCurrentUserInitiator: boolean;
};

export function YourDetails({ titleValues, userDetails, isCurrentUserInitiator }: Props) {
  const baseDataTestId = 'YourDetails';
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const { t } = useTranslation('payApplication');
  const { wizardState, setWizardState, goToNextStep, icons } =
    useWizardContext<PayApplicationState>();
  const token = getAuthCookie();
  const { handleWorldlineError } = useWorldlineErrorHandler();
  const [requestPending, setRequestPending] = useState(false);
  const router = useRouter();
  const {
    title: userTitle = '',
    firstName: userFirstName = '',
    lastName: userLastName = '',
    email: userEmail = '',
  } = userDetails.contactDetail ?? {};
  const contactTitle = wizardState.contactDetails?.title || userTitle || '';
  const contactFirstName = wizardState.contactDetails?.foreName || userFirstName || '';
  const contactLastName = wizardState.contactDetails?.lastName || userLastName || '';
  const contactEmail = wizardState.contactDetails?.email || userEmail || '';
  const isTitleMismatch = !contactTitle || !titleValues.includes(contactTitle);
  const titleOptions = titleValues.map((value) => ({ value, displayValue: value }));

  const schema = z
    .object({
      title: z.object({
        displayValue: z.string().min(1, t('your.details.position.required')),
        value: z.string(),
      }),
      landLineNumber: z
        .string()
        .min(10, t('your.details.position.required'))
        .regex(/^\+?\d+$/, t('your.details.position.required'))
        .or(z.literal('')),
      mobileNumber: z
        .string()
        .min(10, t('your.details.position.required'))
        .regex(/^\d+$/, t('your.details.position.required'))
        .or(z.literal('')),
      position: z
        .string()
        .trim()
        .min(1, t('your.details.position.required'))
        .max(30, t('your.details.position.required')),
    })
    .superRefine((data, ctx) => {
      if (!data.landLineNumber && !data.mobileNumber) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          path: ['landLineNumber'],
          message: t('your.details.landline.required'),
        });
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          path: ['mobileNumber'],
          message: t('your.details.landline.required'),
        });
      }
    });

  type FormValues = z.infer<typeof schema>;

  const getDefaultTitle = () => {
    if (wizardState.contactDetails?.title) {
      return wizardState.contactDetails.title;
    }

    if (!isTitleMismatch && contactTitle) {
      return contactTitle;
    }

    return '';
  };

  const defaultTitle = getDefaultTitle();

  const methods = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      title: {
        displayValue: defaultTitle,
        value: defaultTitle,
      },
      landLineNumber: wizardState.contactDetails?.telephone ?? '',
      mobileNumber: wizardState.contactDetails?.mobile ?? '',
      position: wizardState.contactDetails?.position ?? '',
    },
  });

  const {
    control,
    formState: { errors },
    trigger,
    handleSubmit,
    setValue,
    clearErrors,
  } = methods;

  const save = async (data: FormValues, isSaveAndClose: boolean) => {
    setRequestPending(true);

    if (!isCurrentUserInitiator) {
      if (wizardState.applicationId && wizardState.applicationGuid) {
        try {
          await updateResumeUrl(
            token,
            wizardState.applicationId,
            wizardState.applicationGuid,
            PayApplicationStep.COMPANY_DETAILS_BUSINESS_TYPE
          );
        } catch (error) {
          console.error('Error updating resume url for non-initiator:', error);
        }
      }

      setRequestPending(false);
      goToNextStep();
      return;
    }

    const contactDetails = {
      title: data.title.value || contactTitle,
      foreName: contactFirstName,
      lastName: contactLastName,
      position: data.position,
      telephone: data.landLineNumber,
      mobile: data.mobileNumber,
      email: contactEmail,
    };

    const result = await updateAppContactDetails(token, {
      applicationGuid: wizardState.applicationGuid,
      applicationId: wizardState.applicationId,
      scheme: wizardState.scheme,
      resumeUrl: PayApplicationStep.COMPANY_DETAILS_BUSINESS_TYPE,
      ...contactDetails,
    });

    if (result?.status === requestStatus.success) {
      setWizardState((prev: PayApplicationState) => ({
        ...prev,
        contactDetails,
      }));

      if (isSaveAndClose) {
        router.push(
          getPathForLocale(
            locale,
            `business-pay/pay-application-save?applicationGuid=${wizardState.applicationGuid}&applicationId=${wizardState.applicationId}`
          )
        );
      } else {
        goToNextStep();
      }
    } else {
      handleWorldlineError(result);
    }

    setRequestPending(false);
  };

  return (
    <WizardPage
      type="form"
      formTitle={t('your.details.title')}
      showBackButton={false}
      footer={
        <WizardFooter
          linkLabel={t('your.details.saveAndClose')}
          linkDisabled={requestPending}
          buttonLabel={t('your.details.continue')}
          buttonDisabled={requestPending}
          onLinkClick={handleSubmit((data: FormValues) => save(data, true))}
          onButtonClick={handleSubmit((data: FormValues) => save(data, false))}
        />
      }
    >
      <FormProvider {...methods}>
        <form id={`${baseDataTestId}-Form`}>
          <div className={subtitleStyle}>{t('your.details.description')}</div>
          <div className={formStyle}>
            <div className={fieldStyle}>
              <div className={fieldLabelStyle}>{t('your.details.fullName.label')}</div>

              {isTitleMismatch && (
                <Controller
                  name="title"
                  control={control}
                  render={({ field }) => (
                    <FormSelect
                      {...field}
                      id="title"
                      placeholder={t('companyDetails.name.title')}
                      className={titleSelectStyle}
                      buttonClassName={titleSelectDropdownStyle}
                      popoverClassName={titleSelectDropdownStyle}
                      showLabel={false}
                      errors={errors}
                      arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
                      errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                      onBlur={() => {
                        trigger('title');
                      }}
                      onFocus={() => {
                        clearErrors('title');
                      }}
                      options={titleOptions}
                      onChange={(data: any) => setValue('title', data)}
                    />
                  )}
                />
              )}

              {isTitleMismatch ? (
                <div>{`${contactFirstName} ${contactLastName}`}</div>
              ) : (
                <div>{`${contactTitle} ${contactFirstName} ${contactLastName}`}</div>
              )}
            </div>
            <div className={fieldStyle}>
              <div className={fieldLabelStyle}>{t('your.details.email.label')}</div>
              <div>{contactEmail}</div>
            </div>
            <div className={fieldStyle}>
              <div className={fieldLabelStyle}>{t('your.details.contactNumber.label')}</div>
              <div className={fieldHintStyle}>{t('your.details.contactNumber.note')}</div>
              <Controller
                name="landLineNumber"
                control={control}
                render={({ field }) => (
                  <FormInput
                    {...field}
                    id="landLineNumber"
                    type={'text'}
                    placeholder={t('your.details.landline.placeholder')}
                    errors={errors}
                    errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                    onBlur={() => {
                      trigger('landLineNumber');
                    }}
                  />
                )}
              />
              <div className={fieldHintStyle}>{t('your.details.landline.hint')}</div>
              <Controller
                name="mobileNumber"
                control={control}
                render={({ field }) => (
                  <FormInput
                    {...field}
                    id="mobileNumber"
                    type={'text'}
                    placeholder={t('your.details.mobile.placeholder')}
                    errors={errors}
                    errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                    onBlur={() => {
                      trigger('mobileNumber');
                    }}
                  />
                )}
              />
              <div className={fieldHintStyle}>{t('your.details.mobile.hint')}</div>
            </div>
            <div className={fieldStyle}>
              <div className={fieldLabelStyle}>{t('your.details.position.title')}</div>
              <Controller
                name="position"
                control={control}
                render={({ field }) => (
                  <FormInput
                    {...field}
                    id="position"
                    type={'text'}
                    placeholder={t('your.details.position.title')}
                    errors={errors}
                    errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                    onBlur={() => {
                      trigger('position');
                    }}
                  />
                )}
              />
            </div>
          </div>
        </form>
      </FormProvider>
      {!requestPending && <ReviewChanges />}
      <Analytics
        track={'Pay Application: Your Details'}
        pageName="Pay Application: Your Details"
        errors={errors}
      />
    </WizardPage>
  );
}

const subtitleStyle = 'mt-4';
const formStyle = 'mt-12 p-6 border border-lightGrey3 bg-white rounded-lg';
const fieldStyle =
  'flex flex-col gap-2 py-6 first:pt-0 last:pb-0 border-b last:border-0 border-lightGrey3';
const fieldLabelStyle = 'font-bold mb-2';
const fieldHintStyle = 'font-normal text-sm mb-6';
const titleSelectStyle = 'mb-6';
const titleSelectDropdownStyle = 'w-[136px]';
