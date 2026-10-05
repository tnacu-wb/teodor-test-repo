'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { BookingAllowancesCriteria } from '@whitbread-eos/api';
import { SanitizedContent, Switch } from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';
import React, { MutableRefObject, useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';

import { PriceInputsSection } from '../AlertsForms/PriceInputsSection';
import type { PriceFormValues } from '../AlertsForms/PriceInputsSection';

type Props = {
  onSubmit: (data: {
    unitedKingdom: number;
    greaterLondon: number;
    germanyIreland: number;
    includeAlcohol: boolean;
  }) => void;
  formRef: MutableRefObject<HTMLFormElement | null>;
  bookingAllowances: BookingAllowancesCriteria;
  icons: Record<string, string>;
  onDirtyChange?: (isDirty: boolean) => void;
};

export const PrePaidAllowancesForm = ({
  onSubmit,
  formRef,
  icons,
  bookingAllowances,
  onDirtyChange,
}: Readonly<Props>) => {
  const { t } = useTranslation('company');
  const initialIncludeAlcohol = bookingAllowances?.allowAlcohol ?? false;
  const initialUnitedKingdom =
    bookingAllowances?.maxDinnerBudgets?.uKWide?.amount?.toString() || '';
  const initialGreaterLondon =
    bookingAllowances?.maxDinnerBudgets?.greaterLondon?.amount?.toString() || '';
  const initialGermanyIreland =
    bookingAllowances?.maxDinnerBudgets?.ireland?.amount?.toString() || '';
  const [includeAlcohol, setIncludeAlcohol] = useState<boolean>(initialIncludeAlcohol);
  const inputGenericError = t('coMngt.alerts.price.input.error');

  const schema = z.object({
    unitedKingdom: z
      .string()
      .regex(/^\d+$/, { message: inputGenericError })
      .refine((value) => Number(value) >= 0 && Number(value) <= 999, {
        message: inputGenericError,
      }),
    greaterLondon: z
      .string()
      .regex(/^\d+$/, { message: inputGenericError })
      .refine((value) => Number(value) >= 0 && Number(value) <= 999, {
        message: inputGenericError,
      }),
    germanyIreland: z
      .string()
      .regex(/^\d+$/, { message: inputGenericError })
      .refine((value) => Number(value) >= 0 && Number(value) <= 999, {
        message: inputGenericError,
      }),
  });

  const {
    control,
    handleSubmit,
    formState: { errors },
    trigger,
    clearErrors,
    watch,
  } = useForm<PriceFormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      unitedKingdom: initialUnitedKingdom,
      greaterLondon: initialGreaterLondon,
      germanyIreland: initialGermanyIreland,
    },
  });
  const unitedKingdomValue = watch('unitedKingdom') ?? '';
  const greaterLondonValue = watch('greaterLondon') ?? '';
  const germanyIrelandValue = watch('germanyIreland') ?? '';

  useEffect(() => {
    const isDirty =
      unitedKingdomValue !== initialUnitedKingdom ||
      greaterLondonValue !== initialGreaterLondon ||
      germanyIrelandValue !== initialGermanyIreland ||
      includeAlcohol !== initialIncludeAlcohol;

    onDirtyChange?.(isDirty);
  }, [
    unitedKingdomValue,
    greaterLondonValue,
    germanyIrelandValue,
    includeAlcohol,
    initialUnitedKingdom,
    initialGreaterLondon,
    initialGermanyIreland,
    initialIncludeAlcohol,
    onDirtyChange,
  ]);

  const handleCheckedChange = () => {
    setIncludeAlcohol(!includeAlcohol);
  };

  const renderSwitch = () => (
    <div className={switchContainerStyle}>
      <Switch
        checked={includeAlcohol}
        onCheckedChange={() => handleCheckedChange()}
        data-testid={`PrePaidAllowancesForm-${includeAlcohol}-switcher`}
        className={switchStyle}
      />
      <div
        className={switchLabelStyle}
        data-testid={`PrePaidAllowancesForm-${includeAlcohol}-label-container`}
      >
        <span className={'text-darkGrey2 text-normal text-sm'}>
          {t('coMngt.allowances.prepaid.alcohol.label')}
        </span>
      </div>
    </div>
  );

  const handleSubmitForm = (data: PriceFormValues) => {
    const finalData = {
      unitedKingdom: Number(data.unitedKingdom),
      greaterLondon: Number(data.greaterLondon),
      germanyIreland: Number(data.germanyIreland),
      includeAlcohol,
    };
    onSubmit(finalData);
  };

  const renderPrePaidAllowancesForm = () => {
    return (
      <div data-testid="PrePaidAllowancesForm-container" className={'max-w-[620px]'}>
        <div className={'pb-10 pt-12'}>
          <h4
            data-testid="PrePaidAllowancesForm-title-prepaid-allowances"
            className={`${headingStyle} pb-2`}
          >
            {t('coMngt.allowances.prepaid.title')}
          </h4>
          <span
            data-testid="PrePaidAllowancesForm-description-prepaid-allowances"
            className={'text-darkGrey2'}
          >
            <SanitizedContent>{t('coMngt.allowances.prepaid.subtitle')}</SanitizedContent>
          </span>
        </div>

        <PriceInputsSection
          control={control}
          errors={errors}
          trigger={trigger}
          clearErrors={clearErrors}
          icons={icons}
          t={t}
          baseTestId="PrePaidAllowancesForm"
          ukLabelKey="coMngt.alerts.price.uk.label"
          londonLabelKey="coMngt.allowances.prepaid.london.label"
          euLabelKey="coMngt.allowances.prepaid.eu.label"
          placeholderKey="coMngt.alerts.price.placeholder"
        />
        <div data-testid="PrePaidAllowancesForm-prepaid-allowances-container">
          <div className="flex flex-col gap-4 pb-12">{renderSwitch()}</div>
        </div>
      </div>
    );
  };

  return (
    <form
      ref={formRef as MutableRefObject<HTMLFormElement>}
      onSubmit={handleSubmit(handleSubmitForm)}
      className={formStyle}
    >
      {renderPrePaidAllowancesForm()}
    </form>
  );
};

const formStyle = 'flex flex-col border-b-[1px] border-lightGrey2';
const headingStyle = 'font-bold text-base';
const switchContainerStyle = 'flex gap-4 items-center';
const switchLabelStyle = 'flex flex-col mobile:order-1';
const switchStyle = 'mobile:order-2';
