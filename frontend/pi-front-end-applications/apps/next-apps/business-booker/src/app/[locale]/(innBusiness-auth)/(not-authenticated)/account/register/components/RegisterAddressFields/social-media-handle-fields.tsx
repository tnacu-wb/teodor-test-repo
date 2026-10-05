'use client';

import { FormSelect, FormInput } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl } from '@whitbread-eos/utils/server';
import React from 'react';
import { useFormContext, Controller } from 'react-hook-form';

type SocialMediaHandleFieldsProps = {
  headingStyle: string;
  subHeadingStyle: string;
  icons: Record<string, string>;
  socialMediaValuePlaceholder: string;
  socialMediaOptions: { value: string; displayValue: string }[];
  handleSocialMediaChange: (socialMedia: {
    value: 'website' | 'facebook' | 'instagram' | 'linkedin' | 'twitter' | 'other';
  }) => void;
};

const SocialMediaHandleFields = ({
  headingStyle,
  subHeadingStyle,
  icons,
  socialMediaOptions,
  socialMediaValuePlaceholder,
  handleSocialMediaChange,
}: SocialMediaHandleFieldsProps) => {
  const {
    control,
    formState: { errors },
    trigger,
  } = useFormContext();
  return (
    <>
      <div>
        <h3 className={headingStyle}>Your company’s social media handle</h3>
        <p className={subHeadingStyle}>(optional)</p>
        <p>This can reduce the amount of time it takes to confirm your company details.</p>
      </div>
      <Controller
        name="socialMediaType"
        control={control}
        render={({ field }) => (
          <FormSelect
            {...field}
            id="socialMediaType"
            type={'text'}
            placeholder={'Social Media Handle'}
            showLabel={false}
            errors={errors}
            arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
            errorIcon={icons ? formatIBAssetsUrl(icons?.['icon.notification.error']) : ''}
            onBlur={() => {
              trigger('socialMediaType');
            }}
            options={socialMediaOptions}
            onChange={handleSocialMediaChange}
          />
        )}
      />
      <Controller
        name="socialMediaValue"
        control={control}
        render={({ field }) => (
          <FormInput
            {...field}
            id="socialMediaValue"
            type={'text'}
            placeholder={socialMediaValuePlaceholder}
            errors={errors}
            errorIcon={icons ? formatIBAssetsUrl(icons?.['icon.notification.error']) : ''}
            onBlur={() => {
              trigger('socialMediaValue');
            }}
          />
        )}
      />
    </>
  );
};

export default SocialMediaHandleFields;
