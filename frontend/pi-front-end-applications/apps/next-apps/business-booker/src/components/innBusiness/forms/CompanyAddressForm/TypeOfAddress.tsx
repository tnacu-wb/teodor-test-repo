'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { AddressType } from '@whitbread-eos/api';
import { FormRadioGroup } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { MutableRefObject, useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

type Props = {
  onSubmit: (data: any) => void;
  icons: Record<string, string>;
  companyName: string;
  onAddressTypeChange: (type: AddressType) => void;
  formRef: MutableRefObject<HTMLFormElement | null>;
};

export function TypeOfAddress({
  onSubmit,
  icons,
  companyName,
  formRef,
  onAddressTypeChange,
}: Props) {
  const { t } = useTranslation(['company', 'profile']);

  const items = [
    {
      value: AddressType.Home,
      label: t('profile.profile.radio.address.home'),
    },
    {
      value: AddressType.Business,
      label: t('profile.profile.radio.address.business'),
    },
  ];

  const schema = z.object({
    addressType: z.string(),
  });

  const {
    control,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      addressType: companyName ? AddressType.Business : AddressType.Home,
    },
  });

  const addressTypeWatch = watch('addressType');

  useEffect(() => {
    if (addressTypeWatch) {
      onAddressTypeChange(addressTypeWatch);
    }
  });

  const handleOptionChange = (value: string, field: any) => {
    field.onChange(value);
  };

  const renderComponent = () => {
    return (
      <div data-testid="AddressType-form" className={containerStyle}>
        <Controller
          name="addressType"
          control={control}
          render={({ field }) => (
            <FormRadioGroup
              {...field}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              errors={errors}
              items={items}
              onChange={(value: string) => handleOptionChange(value, field)}
              variant="row"
              selectedValue={addressTypeWatch}
            />
          )}
        />
      </div>
    );
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} ref={formRef as MutableRefObject<HTMLFormElement>}>
      {renderComponent()}
    </form>
  );
}

const containerStyle = 'flex flex-col justify-between mb-6 mt-2 mobile:mt-4 gap-4 w-full';
