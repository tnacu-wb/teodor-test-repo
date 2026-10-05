'use client';

import { cn, useTranslation } from '@whitbread-eos/utils';
import { Check } from 'lucide-react';
import React from 'react';

export enum BusinessStepType {
  GUEST_DETAILS_PAGE = 'GUEST_DETAILS_PAGE',
  PAYMENT_PAGE = 'PAYMENT_PAGE',
  BOOKING_CONFIRMATION_PAGE = 'BOOKING_CONFIRMATION_PAGE',
}

export enum BusinessStepStatus {
  Pending,
  Active,
  Completed,
}

export type BusinessStep = {
  label: string;
  status: BusinessStepStatus;
};

type BaseProps = {
  className?: string;
  lineClassName?: string;
};

type BusinessStepsProps =
  | (BaseProps & { type: BusinessStepType; steps?: never })
  | (BaseProps & { type?: never; steps: BusinessStep[] });

export function BusinessSteps({ className, lineClassName, type, steps }: BusinessStepsProps) {
  const { t } = useTranslation(['layout']);

  const BusinessStepMapping: Record<BusinessStepType, BusinessStep[]> = {
    [BusinessStepType.GUEST_DETAILS_PAGE]: [
      {
        label: t('layout.innbusinessLayout.bookingFlow.step.guestDetails.label'),
        status: BusinessStepStatus.Active,
      },
      {
        label: t('layout.innbusinessLayout.bookingFlow.step.payment.label'),
        status: BusinessStepStatus.Pending,
      },
    ],
    [BusinessStepType.PAYMENT_PAGE]: [
      {
        label: t('layout.innbusinessLayout.bookingFlow.step.guestDetails.label'),
        status: BusinessStepStatus.Completed,
      },
      {
        label: t('layout.innbusinessLayout.bookingFlow.step.payment.label'),
        status: BusinessStepStatus.Active,
      },
    ],
    [BusinessStepType.BOOKING_CONFIRMATION_PAGE]: [
      {
        label: t('layout.innbusinessLayout.bookingFlow.step.guestDetails.label'),
        status: BusinessStepStatus.Completed,
      },
      {
        label: t('layout.innbusinessLayout.bookingFlow.step.payment.label'),
        status: BusinessStepStatus.Completed,
      },
    ],
  };

  const baseDataTestId = 'BusinessSteps';
  const resolvedSteps = type ? BusinessStepMapping[type] : steps!;
  const currentStep = resolvedSteps.find((step) => step.status === BusinessStepStatus.Active);

  return (
    <div data-testid={baseDataTestId} className={cn(containerStyle, className)}>
      <div className={stepContainerStyle}>
        {resolvedSteps.map((step, index) => (
          <React.Fragment key={index}>
            {step.status === BusinessStepStatus.Pending && (
              <div className={cn(baseStepStyle, pendingStepStyle)}>
                {index + 1}
                <div className={labelStyle}>{step.label}</div>
              </div>
            )}

            {step.status === BusinessStepStatus.Active && (
              <div className={cn(baseStepStyle, activeStepStyle)}>
                {index + 1}
                <div className={labelStyle}>{step.label}</div>
              </div>
            )}

            {step.status === BusinessStepStatus.Completed && (
              <div className={cn(baseStepStyle, completedStepStyle)}>
                <Check width={20} height={20} className="inline" />
                <div className={labelStyle}>{step.label}</div>
              </div>
            )}

            {index < resolvedSteps.length - 1 && (
              <div
                className={cn(
                  baseLineStyle,
                  lineClassName,
                  resolvedSteps[index].status === BusinessStepStatus.Completed
                    ? completedLineStyle
                    : ''
                )}
              ></div>
            )}
          </React.Fragment>
        ))}
      </div>

      {currentStep && <div className={currentStepLabelStyle}>{currentStep.label}</div>}
    </div>
  );
}

const containerStyle = 'w-[250px] mobile:w-full mobile:mt-6';
const stepContainerStyle = 'flex w-full h-12 mobile:h-auto mobile:justify-center justify-evenly';
const baseStepStyle = 'relative w-6 h-6 rounded-full flex justify-center align-center';
const pendingStepStyle = 'bg-lightGrey4 text-darkGrey2';
const activeStepStyle = 'bg-darkGrey1 text-baseWhite font-bold';
const completedStepStyle = 'bg-primaryColor text-baseWhite pt-0.5';
const baseLineStyle = 'w-[47px] mobile:w-[136px] h-px mx-[26px] mobile:mx-2 my-3 bg-lightGrey1';
const completedLineStyle = 'bg-primaryColor h-0.5';
const labelStyle = 'mobile:hidden absolute text-black top-7 whitespace-nowrap';
const currentStepLabelStyle = 'hidden mobile:flex justify-center font-bold mt-2';
