'use client';

import { LOCALES } from '@whitbread-eos/api';
import { BusinessSteps, BusinessStepStatus, useWizardContext } from '@whitbread-eos/layout';
import { useCustomLocaleAppRouter, useTranslation } from '@whitbread-eos/utils';

import { PayApplicationState, PayApplicationStep } from '../types';

export function HeaderSteps() {
  const { t } = useTranslation('payApplication');
  const { country, language } = useCustomLocaleAppRouter();
  const isGermanLocale = `${language}-${country}` === LOCALES.DE;

  const { currentStep, steps } = useWizardContext<PayApplicationState>();

  if (
    currentStep.id === PayApplicationStep.LANDING ||
    currentStep.id === PayApplicationStep.APPLICATION_SENT ||
    currentStep.id === PayApplicationStep.APPLICATION_SENT_FAILED
  ) {
    return null;
  }

  const getStepStatus = (startId: PayApplicationStep, endId: PayApplicationStep) => {
    const index = steps.findIndex((step) => step.id === currentStep.id);
    const startIndex = steps.findIndex((step) => step.id === startId);
    const endIndex = steps.findIndex((step) => step.id === endId);

    if (index < startIndex) {
      return BusinessStepStatus.Pending;
    }

    if (index >= startIndex && index <= endIndex) {
      return BusinessStepStatus.Active;
    }

    return BusinessStepStatus.Completed;
  };

  return (
    <BusinessSteps
      className={isGermanLocale ? stepsDEStyle : stepsStyle}
      lineClassName={lineStyle}
      steps={[
        {
          label: t('your.details.progress.bar.your.details.title'),
          status: getStepStatus(PayApplicationStep.YOUR_DETAILS, PayApplicationStep.YOUR_DETAILS),
        },
        {
          label: t('your.details.progress.bar.company.details.title'),
          status: getStepStatus(
            PayApplicationStep.COMPANY_DETAILS_BUSINESS_TYPE,
            PayApplicationStep.COMPANY_DETAILS_ADDITIONAL_DETAILS
          ),
        },
        {
          label: t('your.details.progress.bar.card.details.title'),
          status: getStepStatus(
            PayApplicationStep.CARD_DETAILS,
            PayApplicationStep.CARD_DETAILS_ADD_CARD
          ),
        },
        {
          label: t('your.details.progress.bar.payment.details.title'),
          status: getStepStatus(
            PayApplicationStep.PAYMENT_DETAILS,
            PayApplicationStep.PAYMENT_DETAILS_DIRECT_DEBIT
          ),
        },
        {
          label: t('your.details.progress.bar.payment.summary.title'),
          status: getStepStatus(PayApplicationStep.SUMMARY, PayApplicationStep.SUMMARY),
        },
      ]}
    />
  );
}

const stepsStyle = 'w-[600px] mobile:pr-0 pr-[50px]';
const stepsDEStyle = 'w-[800px] mobile:pr-0 pr-[30px]';
const lineStyle = 'mobile:w-[32px]';
