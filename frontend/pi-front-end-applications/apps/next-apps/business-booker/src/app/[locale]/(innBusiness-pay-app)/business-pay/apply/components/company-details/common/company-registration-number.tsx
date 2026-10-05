'use client';

import { Scheme } from '@whitbread-eos/api';
import { FormCompanyRegistrationNumber } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl } from '@whitbread-eos/utils/server';
import React from 'react';
import { Controller, useFormContext } from 'react-hook-form';

interface Props {
  onLookupSuccess: (data: any) => void;
  icons: Record<string, string>;
  scheme: Scheme;
}

export const CompanyRegistrationNumber = ({ icons, scheme, onLookupSuccess }: Readonly<Props>) => {
  const {
    formState: { errors },
    setError,
    trigger,
    control,
  } = useFormContext();

  const handleLookupSuccess = (data: any) => {
    if (onLookupSuccess) {
      onLookupSuccess(data);
    }
  };

  return (
    <Controller
      name="companyRegNum"
      control={control}
      render={({ field }) => (
        <FormCompanyRegistrationNumber
          {...field}
          id="CompanyRegistrationNumber"
          errors={errors}
          errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
          setError={setError}
          onBlur={() => {
            trigger('companyRegNum');
          }}
          scheme={scheme}
          className={'mb-8'}
          onLookupSuccess={handleLookupSuccess}
        />
      )}
    />
  );
};
