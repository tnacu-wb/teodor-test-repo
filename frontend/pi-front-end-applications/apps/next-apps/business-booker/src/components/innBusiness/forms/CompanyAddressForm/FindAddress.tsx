'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { FormattedAddress } from '@whitbread-eos/api';
import { FormInput, Button } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

import { DropdownOptions } from '~components/innBusiness/forms/CompanyAddressForm/CompanyAddressFields';

type Props = {
  onSubmit: (data: any) => void;
  icons: Record<string, string>;
  dropdownOptions?: DropdownOptions[];
  address?: FormattedAddress;
  onDirtyChange?: (isDirty: boolean) => void;
};

export function FindAddress({ onSubmit, icons, dropdownOptions, address, onDirtyChange }: Props) {
  const { t } = useTranslation('users');

  const schema = z.object({
    postCode: z.string().min(1, t('userMgmt.employee.add.companyAddress.error.invalidPostcode')),
  });

  const {
    control,
    formState: { errors },
    trigger,
    setError,
    watch,
  } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      postCode: address?.postalCode ?? '',
    },
  });

  useEffect(() => {
    const subscription = watch(() => {
      onDirtyChange?.(true);
    });
    return () => subscription?.unsubscribe();
  }, [watch, onDirtyChange]);

  // Manual form submission handler to avoid nested form issues
  const handlePostCodeSubmit = async () => {
    const isValid = await trigger('postCode');
    if (isValid) {
      const data = { postCode: control._formValues.postCode };
      onSubmit(data);
    }
  };

  useEffect(() => {
    if (Array.isArray(dropdownOptions) && !dropdownOptions?.length) {
      setError('postCode', {
        type: 'manual',
        message: t('userMgmt.employee.add.companyAddress.error.invalidPostcode'),
      });
    }
  }, [setError, dropdownOptions]);

  return (
    <div className={formStyle}>
      <div data-testid={'CompanyAddressForm-findAddress'} className={containerStyle}>
        <Controller
          name="postCode"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              id="postCode"
              type={'text'}
              placeholder={t('userMgmt.employee.add.companyAddress.postcode')}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              upperCase={true}
            />
          )}
        />
        <Button
          variant="findAddressButton"
          size="findAddressButton"
          className={buttonStyle}
          type="button"
          onClick={handlePostCodeSubmit}
          data-testid={'CompanyAddressForm-findAddressButton'}
        >
          <span> {t('userMgmt.employee.button.findpostcode')}</span>
        </Button>
      </div>
    </div>
  );
}

const formStyle = 'mt-6';
const buttonStyle = 'flex-1 border border-secondaryColor mobile:px-4';
const containerStyle = 'flex justify-between mt-4 gap-4 w-full';
