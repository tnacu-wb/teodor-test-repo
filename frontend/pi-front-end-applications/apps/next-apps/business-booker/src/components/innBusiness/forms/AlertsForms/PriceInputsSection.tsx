'use client';

import { FormInput } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl } from '@whitbread-eos/utils/server';
import { TFunction } from 'i18next';
import {
  Control,
  Controller,
  FieldErrors,
  UseFormTrigger,
  UseFormClearErrors,
  ControllerRenderProps,
  FieldPath,
} from 'react-hook-form';

export type PriceFormValues = {
  unitedKingdom: string;
  greaterLondon: string;
  germanyIreland: string;
  [key: string]: string;
};

type Props = {
  control: Control<PriceFormValues>;
  errors: FieldErrors<PriceFormValues>;
  trigger: UseFormTrigger<PriceFormValues>;
  clearErrors: UseFormClearErrors<PriceFormValues>;
  icons: Record<string, string>;
  baseTestId?: string;
  ukLabelKey: string;
  londonLabelKey: string;
  euLabelKey: string;
  placeholderKey: string;
  t: TFunction;
  fieldNamePrefix?: string;
};

export const PriceInputsSection = ({
  control,
  errors,
  trigger,
  icons,
  t,
  baseTestId = 'PriceInputsSection',
  ukLabelKey,
  londonLabelKey,
  euLabelKey,
  placeholderKey,
  fieldNamePrefix = '',
}: Readonly<Props>) => {
  const getFieldName = (name: string): string =>
    fieldNamePrefix ? `${fieldNamePrefix}.${name}` : name;

  const handleBlur = (
    field: ControllerRenderProps<PriceFormValues, string>,
    fieldName: string,
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const value = e.target.value === '0' ? '0' : e.target.value.replace(/^0+/, '');
    field.onChange(value);
    trigger(getFieldName(fieldName) as never);
  };

  return (
    <div data-testid={`${baseTestId}-inputs-container`} className={'flex flex-col gap-4 mb-10'}>
      <h4 data-testid={`${baseTestId}-uk-title`} className={headingStyle}>
        {t(ukLabelKey) as string}
      </h4>
      <Controller
        name={getFieldName('unitedKingdom') as FieldPath<PriceFormValues>}
        control={control}
        render={({ field }) => (
          <FormInput
            {...field}
            id={`${baseTestId}-unitedKingdom`}
            type="text"
            ariaLabel={t(ukLabelKey) as string}
            placeholder={t(placeholderKey) as string}
            inputIcon={formatIBAssetsUrl(icons?.['icon.input.pound'])}
            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            errors={errors}
            showLabel={false}
            onBlur={(e: React.ChangeEvent<HTMLInputElement>) =>
              handleBlur(field, 'unitedKingdom', e)
            }
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => field.onChange(e.target.value)}
          />
        )}
      />
      <h4 data-testid={`${baseTestId}-london-title`} className={`${headingStyle} pt-2`}>
        {t(londonLabelKey) as string}
      </h4>
      <Controller
        name={getFieldName('greaterLondon') as FieldPath<PriceFormValues>}
        control={control}
        render={({ field }) => (
          <FormInput
            {...field}
            id={`${baseTestId}-greaterLondon`}
            type="text"
            ariaLabel={t(londonLabelKey) as string}
            showLabel={false}
            errors={errors}
            placeholder={t(placeholderKey) as string}
            inputIcon={formatIBAssetsUrl(icons?.['icon.input.pound'])}
            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            onBlur={(e: React.ChangeEvent<HTMLInputElement>) =>
              handleBlur(field, 'greaterLondon', e)
            }
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => field.onChange(e.target.value)}
          />
        )}
      />
      <h4 data-testid={`${baseTestId}-eu-title`} className={`${headingStyle} pt-2`}>
        {t(euLabelKey) as string}
      </h4>
      <Controller
        name={getFieldName('germanyIreland') as FieldPath<PriceFormValues>}
        control={control}
        render={({ field }) => (
          <FormInput
            {...field}
            id={`${baseTestId}-germanyIreland`}
            type="text"
            ariaLabel={t(euLabelKey) as string}
            placeholder={t(placeholderKey) as string}
            errors={errors}
            inputIcon={formatIBAssetsUrl(icons?.['icon.input.euro'])}
            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            showLabel={false}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => field.onChange(e.target.value)}
            onBlur={(e: React.ChangeEvent<HTMLInputElement>) =>
              handleBlur(field, 'germanyIreland', e)
            }
          />
        )}
      />
    </div>
  );
};

const headingStyle = 'font-bold text-base';
