'use client';

import { FormInput, FormPersonTitle } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { Controller, useFormContext } from 'react-hook-form';

type Props = {
  icons: Record<string, string>;
  titleValues: string;
};

export function CompanyNameOfEmployeeForm({ icons, titleValues }: Props) {
  const { t } = useTranslation('payApplication');

  const titleOptions = JSON.parse(titleValues).join(',');

  const {
    control,
    formState: { errors },
    trigger,
    clearErrors,
  } = useFormContext();

  return (
    <div data-testid="CompanyNameOfEmployeeForm-form" className={containerStyle}>
      <Controller
        name="titleEmployee"
        control={control}
        render={({ field }) => (
          <FormPersonTitle
            {...field}
            id="titleEmployee"
            placeholder={t('companyDetails.name.title')}
            errors={errors}
            onBlur={() => {
              trigger('titleEmployee');
            }}
            onFocus={() => {
              clearErrors('titleEmployee');
            }}
            icons={icons}
            titleOptions={titleOptions}
          />
        )}
      />
      <Controller
        name="firstNameEmployee"
        control={control}
        render={({ field }) => (
          <FormInput
            {...field}
            id="firstNameEmployee"
            placeholder={t('companyDetails.name.firstName')}
            errors={errors}
            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            onBlur={() => {
              trigger('firstNameEmployee');
            }}
          />
        )}
      />
      <Controller
        name="lastNameEmployee"
        control={control}
        render={({ field }) => (
          <FormInput
            {...field}
            id="lastNameEmployee"
            placeholder={t('companyDetails.name.lastName')}
            errors={errors}
            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            onBlur={() => {
              trigger('lastNameEmployee');
            }}
          />
        )}
      />
    </div>
  );
}

const containerStyle = 'flex flex-col justify-between mt-2 mobile:mt-4 gap-4 w-full';
