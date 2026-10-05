'use client';

import { Language, FormattedAddress } from '@whitbread-eos/api';
import { getAuthCookie, GLOBALS } from '@whitbread-eos/utils';
import { getPostCodeAddresses, getVariant } from '@whitbread-eos/utils/server';
import { useState } from 'react';
import { useFormContext } from 'react-hook-form';

import { AddressDropdown } from '~components/innBusiness/forms/CompanyAddressForm/AddressDropdown';
import { FindAddress } from '~components/innBusiness/forms/CompanyAddressForm/FindAddress';
import { ManualAddress } from '~components/innBusiness/forms/CompanyAddressForm/ManualAddress';

type Props = {
  icons: Record<string, string>;
  language: Language;
  onOpen?: () => void;
  onDirtyChange?: (isDirty: boolean) => void;
  address?: FormattedAddress;
  variant?: string;
  isIBPayApp?: boolean;
};

export type DropdownOptions = {
  displayValue: string;
  value: string;
};

export function CompanyAddressFields({
  icons,
  language,
  onOpen,
  onDirtyChange,
  address,
  variant,
  isIBPayApp = false,
}: Readonly<Props>) {
  const idTokenCookie = getAuthCookie();
  const [options, setOptions] = useState<DropdownOptions[] | undefined>(undefined);

  const handlePostCodeSubmit = async (data: any) => {
    if (onOpen) {
      onOpen();
    }

    const getAddresses = async () => {
      const postCodeAddresses = await getPostCodeAddresses(data.postCode, idTokenCookie);
      const mappedOptions =
        postCodeAddresses?.map((item: Record<string, string>) => ({
          value: item.id,
          displayValue: item.addressText,
        })) ?? [];
      setOptions(mappedOptions);
    };
    await getAddresses();
  };

  const { watch, setValue } = useFormContext();

  const selectedCountry = watch(getVariant('country', variant));
  const isGermanAddress = [GLOBALS.country.DE, GLOBALS.country.D].includes(selectedCountry);

  const handleFoundAddressSelected = (value: FormattedAddress) => {
    setValue(getVariant('addressLine1', variant), value?.addressLine1 ?? '');
    setValue(getVariant('addressLine2', variant), value?.addressLine2 ?? '');
    setValue(getVariant('addressLine3', variant), value?.addressLine3 ?? '');
    setValue(getVariant('addressLine4', variant), value?.addressLine4 ?? '');
    setValue(getVariant('addressLine5', variant), value?.addressLine5 ?? '');
    setValue(getVariant('postCode', variant), value?.postalCode ?? '');
    setValue(
      getVariant('country', variant),
      value?.country
        ? value.country === GLOBALS.country.D
          ? GLOBALS.country.DE
          : value.country
        : ''
    );
  };

  return (
    <>
      <div className={isGermanAddress ? 'hidden' : 'block'}>
        <FindAddress
          onSubmit={handlePostCodeSubmit}
          icons={icons}
          dropdownOptions={options}
          address={address}
          onDirtyChange={onDirtyChange}
        />
        <AddressDropdown
          icons={icons}
          dropdownOptions={options}
          onAddressChange={handleFoundAddressSelected}
        />
      </div>
      <ManualAddress
        icons={icons}
        language={language}
        onOpen={onOpen}
        showManualEntryLink={!isGermanAddress}
        variant={variant}
        isIBPayApp={isIBPayApp}
      />
    </>
  );
}
