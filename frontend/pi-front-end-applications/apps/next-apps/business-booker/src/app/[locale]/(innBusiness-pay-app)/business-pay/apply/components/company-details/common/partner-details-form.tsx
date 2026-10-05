'use client';

import { FormInput, FormPersonTitle } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import React from 'react';
import { Controller, useFormContext } from 'react-hook-form';

interface Props {
  icons: Record<string, string>;
  titleValues: string;
}

export const PartnerDetailsForm = ({ icons, titleValues }: Readonly<Props>) => {
  const { t } = useTranslation('payApplication');

  const titleOptions = JSON.parse(titleValues).join(',');

  const {
    control,
    formState: { errors },
    trigger,
    clearErrors,
  } = useFormContext();

  return (
    <div data-testid="PartnerDetailsForm-container">
      <div className={'pb-6'}>
        <h4 data-testid={`title`} className={headingStyle}>
          {t('companyDetails.partnerDetails')}
        </h4>
        <span>{t('companyDetails.partnerDetails.description')}</span>
      </div>
      <div className={'mb-2 space-y-6'}>
        <Controller
          name="numberOfPartners"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              id="numberOfPartners"
              placeholder={t('companyDetails.noOfPartners')}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              onBlur={() => {
                trigger('numberOfPartners');
              }}
            />
          )}
        />
        <Controller
          name="title"
          control={control}
          render={({ field }) => (
            <FormPersonTitle
              {...field}
              id="title"
              placeholder={t('companyDetails.name.title')}
              errors={errors}
              onBlur={() => {
                trigger('title');
              }}
              onFocus={() => {
                clearErrors('title');
              }}
              icons={icons}
              titleOptions={titleOptions}
            />
          )}
        />
        <Controller
          name="foreName"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              id="foreName"
              placeholder={t('companyDetails.partner.firstName')}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              onBlur={() => {
                trigger('foreName');
              }}
            />
          )}
        />
        <Controller
          name="lastName"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              id="lastName"
              placeholder={t('companyDetails.partner.lastName')}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              onBlur={() => {
                trigger('lastName');
              }}
            />
          )}
        />
      </div>
    </div>
  );
};

const headingStyle = 'font-bold text-base pb-2';
