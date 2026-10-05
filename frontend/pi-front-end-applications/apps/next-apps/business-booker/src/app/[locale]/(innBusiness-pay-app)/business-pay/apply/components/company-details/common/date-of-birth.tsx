'use client';

import { FormSelect, ErrorTooltip } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import React, { useState, useEffect } from 'react';
import { Controller, useFormContext } from 'react-hook-form';

interface Props {
  icons: Record<string, string>;
}
interface CustomFieldError {
  value?: {
    message?: string;
    type?: string;
  };
}

const currentYear = new Date().getFullYear();

const monthOptions = Array.from({ length: 12 }, (_, index) => ({
  value: (index + 1).toString(),
  displayValue: (index + 1).toString(),
}));

const yearOptions = Array.from({ length: currentYear - 1900 + 1 }, (_, index) => ({
  value: (currentYear - index).toString(),
  displayValue: (currentYear - index).toString(),
}));

export const DateOfBirthForm = ({ icons }: Readonly<Props>) => {
  const { t } = useTranslation('payApplication');

  const {
    control,
    formState: { errors },
    setValue,
    getValues,
    watch,
    clearErrors,
  } = useFormContext();

  const selectedMonth = watch('month');
  const selectedYear = watch('year');

  const [dayOptions, setDayOptions] = useState(
    Array.from({ length: 31 }, (_, index) => ({
      value: (index + 1).toString(),
      displayValue: (index + 1).toString(),
    }))
  );

  useEffect(() => {
    if (selectedMonth && selectedYear) {
      const daysInMonth = new Date(
        Number(selectedYear.value),
        Number(selectedMonth.value),
        0
      ).getDate();
      setDayOptions(
        Array.from({ length: daysInMonth }, (_, index) => ({
          value: (index + 1).toString(),
          displayValue: (index + 1).toString(),
        }))
      );

      const currentDay = getValues('day');
      if (Number(currentDay) > daysInMonth) {
        setValue('day', { displayValue: '', value: '' });
      }
    }
  }, [selectedMonth, selectedYear, setValue, getValues]);

  const error =
    (errors?.['day'] as CustomFieldError) ||
    (errors?.['month'] as CustomFieldError) ||
    (errors?.['year'] as CustomFieldError);

  const hasError = !!error?.value?.message || !!error?.value?.type;

  return (
    <div data-testid="DateOfBirthForm-container">
      <div className="flex w-full mt-4 justify-between mobile:flex-wrap gap-4">
        <div className={'flex w-full gap-4'}>
          <Controller
            name="day"
            control={control}
            render={({ field }) => (
              <FormSelect
                {...field}
                id="DateOfBirthForm-day"
                placeholder={t('companyDetails.dateOfBirth.day')}
                showLabel={false}
                showErrorTooltip={false}
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
                options={dayOptions}
                className={'w-full mobile:w-auto mobile:flex-1 '}
                clearErrors={() => {
                  clearErrors('day');
                }}
              />
            )}
          />
          <Controller
            name="month"
            control={control}
            render={({ field }) => (
              <FormSelect
                {...field}
                id="DateOfBirthForm-month"
                placeholder={t('companyDetails.dateOfBirth.month')}
                showLabel={false}
                showErrorTooltip={false}
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
                options={monthOptions}
                disabled={false}
                className={'w-full mobile:w-auto mobile:flex-1'}
                clearErrors={() => {
                  clearErrors('month');
                }}
              />
            )}
          />
        </div>
        <Controller
          name="year"
          control={control}
          render={({ field }) => (
            <FormSelect
              {...field}
              id="DateOfBirthForm-year"
              placeholder={t('companyDetails.dateOfBirth.year')}
              showLabel={false}
              showErrorTooltip={false}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
              options={yearOptions}
              disabled={false}
              className={'w-full mobile:w-auto mobile:flex-1'}
              clearErrors={() => {
                clearErrors(['day', 'month', 'year']);
              }}
            />
          )}
        />
      </div>
      <div className={'flex mt-2 w-full'}>
        <ErrorTooltip
          icon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
          content={typeof error?.value?.message === 'string' ? error.value.message : undefined}
          open={hasError}
          testId="DateOfBirthForm-Error-Tooltip"
          className="!flex"
          mobile
        />
      </div>
    </div>
  );
};
