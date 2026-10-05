'use client';

import { FormInput } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import React from 'react';
import { Controller, useFormContext } from 'react-hook-form';

interface Props {
  icons: Record<string, string>;
}

export const RegisteredCharityNumberForm = ({ icons }: Readonly<Props>) => {
  const { t } = useTranslation('payApplication');

  const {
    control,
    formState: { errors },
    trigger,
  } = useFormContext();

  return (
    <div data-testid="RegisteredCharityNumberForm-container">
      <h4 data-testid={`title`} className={headingStyle}>
        {t('companyDetails.registered.charity.label')}
      </h4>
      <div className={'mb-8'}>
        <Controller
          name="charityNumber"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              id="RegisteredCharityNumberForm"
              type="text"
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              onBlur={() => {
                trigger('charityNumber');
              }}
            />
          )}
        />
      </div>
    </div>
  );
};

const headingStyle = 'font-bold text-base pb-6';
