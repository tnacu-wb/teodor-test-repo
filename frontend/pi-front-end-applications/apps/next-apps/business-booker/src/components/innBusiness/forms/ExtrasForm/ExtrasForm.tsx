'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { FormRadioGroup } from '@whitbread-eos/atoms/ui';
import { useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import { MutableRefObject, useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

type Props = {
  onSubmit: (data: Record<string, number | boolean>) => void;
  formRef: MutableRefObject<HTMLFormElement | null>;
  icons: Record<string, string>;
  profileDetails?: Record<string, any>;
  onDirtyChange?: (isDirty: boolean) => void;
};

export function ExtrasForm({ onSubmit, profileDetails, formRef, icons, onDirtyChange }: Props) {
  const { t } = useTranslation('profile');

  const mealOptions = [
    {
      value: 11,
      label: t('extraspreferences.mealoptions.pibreakfast'),
    },
    {
      value: 12,
      label: t('extraspreferences.mealoptions.continentalbreakfast'),
    },
    {
      value: 17,
      label: t('extraspreferences.mealoptions.mealdeal'),
    },
    {
      value: 0,
      label: t('extraspreferences.mealoptions.nomeals'),
    },
  ];

  const wifiOptions = [
    {
      value: true,
      label: t('extraspreferences.wifi.always'),
    },
    {
      value: false,
      label: t('extraspreferences.wifi.never'),
    },
  ];

  const invoicingOptions = [
    {
      value: true,
      label: t('extraspreferences.edit.invoice.email'),
    },
    {
      value: false,
      label: t('extraspreferences.edit.invoice.checkin'),
    },
  ];

  const schema = z.object({
    mealOptions: z.number(),
    wifiOptions: z.boolean(),
    invoicingOptions: z.boolean(),
  });

  const {
    control,
    handleSubmit,
    formState: { errors, isDirty },
  } = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      mealOptions: profileDetails?.bookingPreference?.foodPreference ?? 0,
      wifiOptions: profileDetails?.bookingPreference?.preselectWifi ?? false,
      invoicingOptions: profileDetails?.paymentPreference?.electronicInvoiceRequired ?? true,
    },
  });

  useEffect(() => {
    onDirtyChange?.(isDirty);
  }, [isDirty]);

  const handleOptionChange = (value: string, field: any) => {
    field.onChange(value);
  };

  const handleFormSubmit = (data: z.infer<typeof schema>) => {
    const { mealOptions, wifiOptions, invoicingOptions } = data;
    const dataObject = {
      foodPreference: mealOptions,
      preselectWifi: wifiOptions,
      electronicInvoiceRequired: invoicingOptions,
    };
    onSubmit(dataObject);
  };

  return (
    <form
      ref={formRef as MutableRefObject<HTMLFormElement>}
      onSubmit={handleSubmit(handleFormSubmit)}
    >
      <div data-testid="Meal-Options-Container" className={containerStyle}>
        <h4 data-testid="Meals-Heading" className={headingStyle}>
          {t('extraspreferences.meals.title')}
        </h4>

        <Controller
          name="mealOptions"
          control={control}
          render={({ field }) => (
            <FormRadioGroup
              {...field}
              data-testid="Meal-Options-Container-List"
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              errors={errors}
              items={mealOptions}
              onChange={(value: string) => handleOptionChange(value, field)}
              radioGroupItemClass={radioItemStyle}
              radioGroupLabelClass={radioLabelStyle}
              radioGroupButtonClass={radioButtonStyle}
              customId={'mealOption'}
            />
          )}
        />
      </div>

      <div data-testid="Wifi-Options-Container" className={containerStyle}>
        <h4 data-testid="Wifi-Heading" className={headingStyle}>
          {t('extraspreferences.wifi.title')}
        </h4>
        <Controller
          name="wifiOptions"
          control={control}
          render={({ field }) => (
            <FormRadioGroup
              {...field}
              data-testid="Wifi-Options-Container-List"
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              errors={errors}
              items={wifiOptions}
              onChange={(value: string) => handleOptionChange(value, field)}
              radioGroupItemClass={radioItemStyle}
              radioGroupLabelClass={radioLabelStyle}
              radioGroupButtonClass={radioButtonStyle}
              customId={'wifiOption'}
            />
          )}
        />
      </div>

      <div data-testid="Invoicing-Options-Container" className={containerStyle}>
        <h4 data-testid="Invoicing-Heading" className={headingStyle}>
          {t('extraspreferences.edit.invoice.title')}
        </h4>
        <Controller
          name="invoicingOptions"
          control={control}
          render={({ field }) => (
            <FormRadioGroup
              {...field}
              data-testid="Invoicing-Options-Container-List"
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              errors={errors}
              items={invoicingOptions}
              onChange={(value: string) => handleOptionChange(value, field)}
              radioGroupItemClass={radioItemStyle}
              radioGroupLabelClass={radioLabelStyle}
              radioGroupButtonClass={radioButtonStyle}
              customId={'invoicingOption'}
            />
          )}
        />
      </div>
    </form>
  );
}

const containerStyle = 'mt-[2.5rem] mb-[2.5rem]';
const headingStyle = 'font-bold text-base mb-[1rem]';
const radioItemStyle = 'h-auto mb-[1rem]';
const radioLabelStyle = 'font-normal';
const radioButtonStyle = 'border-lightGrey1';
