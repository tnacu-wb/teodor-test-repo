'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { LOCALES, requestStatus, DirectDebitOption } from '@whitbread-eos/api';
import {
  Button,
  FormCheckbox,
  SanitizedContent,
  FormSelect,
  FormInput,
} from '@whitbread-eos/atoms/ui';
import { useWizardContext, WizardFooter, WizardPage } from '@whitbread-eos/layout';
import {
  analytics,
  getAuthCookie,
  getPathForLocale,
  useTranslation,
  formatIBAssetsUrl,
} from '@whitbread-eos/utils';
import { submitApplication } from '@whitbread-eos/utils/server';
import { Check } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { Controller, FormProvider, useForm } from 'react-hook-form';
import { z } from 'zod';

import { ReviewChanges } from '~components/innBusiness/ReviewChanges';

import { Analytics } from '../analytics/analytics';
import { useWorldlineErrorHandler } from '../error-handling';
import { PayApplicationState, PayApplicationStep } from '../types';

type Props = {
  locale: LOCALES;
};

const customQuestionOptionValue = 'custom';

export function Summary({ locale }: Readonly<Props>) {
  const { handleWorldlineError } = useWorldlineErrorHandler();
  const token = getAuthCookie();
  const { t } = useTranslation('payApplication');
  const { wizardState, setWizardState, goToStep, icons } = useWizardContext<PayApplicationState>();
  const router = useRouter();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const selectMemorableQuestionOptions = [
    {
      displayValue: t('payapp.memorable.question1'),
      value: t('payapp.memorable.question1'),
    },
    {
      displayValue: t('payapp.memorable.question2'),
      value: t('payapp.memorable.question2'),
    },
    {
      displayValue: t('payapp.memorable.question3'),
      value: t('payapp.memorable.question3'),
    },
    {
      displayValue: t('payapp.memorable.question4'),
      value: t('payapp.memorable.question4'),
    },
    {
      displayValue: t('payapp.memorable.question5'),
      value: t('payapp.memorable.question5'),
    },
    {
      displayValue: t('payapp.memorable.question6'),
      value: customQuestionOptionValue,
    },
  ];

  const schema = z
    .object({
      terms: z.boolean().refine((val: boolean) => val === true, {
        message: t('payapp.directDebit.confirmation.error'),
      }),
      selectMemorableQuestion: z.object({
        displayValue: z.string().min(1, t('payapp.memorable.error')),
        value: z.string(),
      }),
      memorableQuestion: z.string().optional(),
      memorableAnswer: z.string().trim().min(1, t('payapp.memorable.error')),
    })
    .superRefine((data: any, ctx: any) => {
      if (
        data.selectMemorableQuestion.value === customQuestionOptionValue &&
        !data.memorableQuestion?.trim()
      ) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          path: ['memorableQuestion'],
          message: t('payapp.memorable.error'),
        });
      }
    });

  const formMethods = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      terms: false,
      selectMemorableQuestion: {
        value: '',
        displayValue: '',
      },
      memorableQuestion: '',
      memorableAnswer: '',
    },
  });
  const {
    control,
    trigger,
    formState: { errors },
    clearErrors,
    watch,
    getValues,
  } = formMethods;

  const selectMemorableQuestion = watch('selectMemorableQuestion');
  const isCustomQuestion = selectMemorableQuestion.value === customQuestionOptionValue;

  const handleFinish = async () => {
    if (wizardState.isSubmitted || isSubmitting) {
      if (wizardState.isSubmitted) {
        goToStep(PayApplicationStep.APPLICATION_SENT);
      }
      return;
    }

    const isValid = await trigger();
    if (!isValid) return;

    setIsSubmitting(true);

    const response = await submitApplication(
      wizardState.applicationGuid,
      wizardState.applicationId,
      wizardState.scheme,
      wizardState.hostedPageGuid || null,
      isCustomQuestion
        ? getValues('memorableQuestion')
        : getValues('selectMemorableQuestion').value,
      getValues('memorableAnswer'),
      getValues('terms'),
      wizardState.directDebitOption === DirectDebitOption.Direct,
      token
    );

    if (response?.status === requestStatus.success) {
      setWizardState((prev: PayApplicationState) => ({
        ...prev,
        isSubmitted: true,
      }));

      analytics.update({
        innBusiness: {
          ...(window.analyticsData.innBusiness ?? {}),
          applicationReference: wizardState.applicationId,
          businessType: wizardState.companyDetails.companyType,
          companyHotelPolicy: wizardState.companyDetails.hotelBrandPolicy,
          monthlyAccountSpend: wizardState.companyDetails.estMonthlySpend,
          timeTrading:
            typeof wizardState.companyDetails.timeTradingId === 'string'
              ? wizardState.companyDetails.timeTradingId
              : wizardState.companyDetails.timeTradingId?.displayValue || '',
        },
      });

      window._satellite?.track('applicationSubmitted');

      goToStep(PayApplicationStep.APPLICATION_SENT);
    } else {
      handleWorldlineError(response);
    }

    setIsSubmitting(false);
  };

  const continueLater = () => {
    router.push(getPathForLocale(locale, 'homepage'));
  };

  const separator = <div className="form-box-separator" />;
  const checkMark = (
    <div className={checkMarkStyle}>
      <Check width={20} height={20} className="inline" />
    </div>
  );
  const summaryRowElement = (title: string, stepEdit: string) => {
    return (
      <div className={summaryRowStyle}>
        {checkMark}
        <span className={summaryStepTitleStyle}>{title}</span>
        <Button
          className="p-0 h-auto ml-auto"
          variant="editButton"
          onClick={() => {
            goToStep(stepEdit);
          }}
        >
          {t('companyDetails.edit')}
        </Button>
      </div>
    );
  };

  return (
    <FormProvider {...formMethods}>
      <WizardPage
        type="form"
        formTitle={t('payapp.lastStep.title')}
        showBackButton={true}
        onBackClick={() => goToStep(PayApplicationStep.PAYMENT_DETAILS, true)}
        footer={
          <WizardFooter
            linkLabel={t('companyDetails.closeOut')}
            buttonLabel={t('payapp.finish.text')}
            onButtonClick={handleFinish}
            onLinkClick={continueLater}
            buttonDisabled={wizardState.isSubmitted || isSubmitting}
          />
        }
      >
        <div className="form-details-box mt-12">
          {summaryRowElement(
            t('your.details.progress.bar.your.details.title'),
            PayApplicationStep.YOUR_DETAILS
          )}
          {separator}
          {summaryRowElement(
            t('your.details.progress.bar.company.details.title'),
            PayApplicationStep.COMPANY_DETAILS_BUSINESS_TYPE
          )}
          {separator}
          {summaryRowElement(
            t('your.details.progress.bar.card.details.title'),
            PayApplicationStep.CARD_DETAILS
          )}
          {separator}
          {summaryRowElement(
            t('your.details.progress.bar.payment.details.title'),
            PayApplicationStep.PAYMENT_DETAILS
          )}
        </div>

        <div className="form-details-box mt-4">
          <Controller
            name="terms"
            control={control}
            render={({ field }) => (
              <FormCheckbox
                {...field}
                id={'Terms'}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                errors={errors}
                checkboxClassName="mb-auto mt-[2px]"
                label={<SanitizedContent>{t('payapp.lastStep.terms')}</SanitizedContent>}
                onValueChange={() => {
                  clearErrors('terms');
                }}
              />
            )}
          />
        </div>

        <div className="form-details-box mt-4">
          <span className="font-bold mb-4">{t('payapp.memorable.question')}</span>
          <span className="mb-4">{t('payapp.security.text')}</span>
          <span className="mb-6">{t('payapp.memorable.rule')}</span>
          <Controller
            name="selectMemorableQuestion"
            control={control}
            render={({ field }) => (
              <FormSelect
                {...field}
                id="selectMemorableQuestion"
                placeholder={t('payapp.select.memorable.question')}
                errors={errors}
                arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
                options={selectMemorableQuestionOptions}
                clearErrors={() => clearErrors('selectMemorableQuestion')}
                className="mb-6"
              />
            )}
          />
          {isCustomQuestion && (
            <Controller
              name="memorableQuestion"
              control={control}
              render={({ field }) => (
                <FormInput
                  {...field}
                  id="Memorable-Question"
                  type={'text'}
                  placeholder={t('payapp.memorable.question.placeholder')}
                  errors={errors}
                  errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                  containerClassName="mb-6"
                  onBlur={() => {
                    trigger('memorableQuestion');
                  }}
                />
              )}
            />
          )}

          <Controller
            name="memorableAnswer"
            control={control}
            render={({ field }) => (
              <FormInput
                {...field}
                id="Memorable-Answer"
                type={'text'}
                placeholder={t('payapp.memorable.answer')}
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                onBlur={() => {
                  trigger('memorableAnswer');
                }}
              />
            )}
          />
        </div>
        {!isSubmitting && <ReviewChanges />}
        <Analytics pageName="Pay Application" errors={errors} />
      </WizardPage>
    </FormProvider>
  );
}

const checkMarkStyle =
  'flex justify-center align-center bg-success text-baseWhite rounded-full w-6 h-6 pt-0.5';
const summaryRowStyle = 'flex';
const summaryStepTitleStyle = 'text-lg leading-6 font-bold ml-2';
