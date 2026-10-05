'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { FormattedAddress, IBFormSelectOption } from '@whitbread-eos/api';
import { FormSelect } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { getFormattedAddress } from '@whitbread-eos/utils/server';
import { useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

import { DropdownOptions } from '~components/innBusiness/forms/CompanyAddressForm/CompanyAddressFields';

type Props = {
  icons: Record<string, string>;
  dropdownOptions?: DropdownOptions[];
  onAddressChange: (address: FormattedAddress) => void;
};

export function AddressDropdown({ icons, dropdownOptions, onAddressChange }: Props) {
  const { t } = useTranslation('users');

  const schema = z.object({
    selectAddress: z.object({
      displayValue: z.string(),
      value: z.string(),
    }),
  });

  const { control, watch, setValue, trigger } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      selectAddress: {
        displayValue: '',
        value: '',
      },
    },
  });

  const handleAddressChangeFormat = async (addressId: string) => {
    const formattedAddress = await getFormattedAddress(addressId);
    onAddressChange(formattedAddress);
  };

  const isOptionsAvailable = dropdownOptions && dropdownOptions.length > 0;
  const selectedAddress = watch('selectAddress.displayValue');

  useEffect(() => {
    if (isOptionsAvailable) {
      setValue('selectAddress', dropdownOptions[0]);
      handleAddressChangeFormat(dropdownOptions[0].value);
    }
  }, [selectedAddress, setValue, dropdownOptions, isOptionsAvailable]);

  return (
    isOptionsAvailable && (
      <div data-testid={'CompanyAddressForm-AddressDropdown'} className={formStyle}>
        <Controller
          name="selectAddress"
          control={control}
          render={({ field }) => (
            <FormSelect
              {...field}
              id="selectAddress"
              placeholder={t('userMgmt.employee.add.companyAddress.selectAddress')}
              arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
              className="w-full"
              options={dropdownOptions}
              onBlur={() => {
                trigger('selectAddress');
              }}
              onChange={async (address: IBFormSelectOption) => {
                await handleAddressChangeFormat(address.value);
              }}
            />
          )}
        />
      </div>
    )
  );
}

const formStyle = 'mt-6';
