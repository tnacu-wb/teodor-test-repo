'use client';

import {
  AddressInfo,
  AddressType,
  Language,
  CountryCode,
  ShortCountry,
  LegacyCountryCode,
} from '@whitbread-eos/api';
import * as React from 'react';

import { CompanyAddress } from '~components/innBusiness/forms/CompanyAddressForm/CompanyAddress';
import { TypeOfAddress } from '~components/innBusiness/forms/CompanyAddressForm/TypeOfAddress';
import { CompanyName } from '~components/innBusiness/forms/CompanyDetailsForm/CompanyName';

type DifferentAddressFormProps = {
  icons: Record<string, string>;
  language?: Language;
  addressFormRef: React.MutableRefObject<HTMLFormElement | null>;
  companyNameFormRef: React.MutableRefObject<HTMLFormElement | null>;
  addressTypeFormRef: React.MutableRefObject<HTMLFormElement | null>;
  onAddressSubmit: (data: AddressInfo) => void;
  onAddressChange?: (address: AddressInfo) => void;
  onAddressTypeSubmit: (data: Record<string, string>) => void;
  onAddressTypeChange: (type: AddressType) => void;
  onCompanyNameSubmit: (data: Record<string, string>) => void;
  onCompanyNameChange?: (data: string) => void;
  addressType: AddressType;
  companyName?: string;
  postalCode?: string;
  profileDetails?: {
    contactDetail?: {
      address?: {
        line1?: string;
        line2?: string;
        line3?: string;
        line4?: string;
        line5?: string;
        postCode?: string;
        countryCode?: string;
      };
    };
  };
};

const DifferentAddressForm = ({
  icons,
  language = CountryCode.EN,
  addressFormRef,
  companyNameFormRef,
  addressTypeFormRef,
  onAddressSubmit,
  onAddressChange,
  onCompanyNameSubmit,
  onCompanyNameChange,
  onAddressTypeSubmit,
  onAddressTypeChange,
  addressType,
  companyName = '',
  postalCode,
  profileDetails,
}: DifferentAddressFormProps) => {
  const isBusinessAddress = addressType === AddressType.Business;

  const handleAddressSearch = React.useCallback((e?: React.FormEvent) => {
    e?.preventDefault();
  }, []);

  const addressData = profileDetails?.contactDetail?.address
    ? {
        addressLine1: profileDetails.contactDetail.address.line1 ?? '',
        addressLine2: profileDetails.contactDetail.address.line2 ?? '',
        addressLine3: profileDetails.contactDetail.address.line3 ?? '',
        addressLine4: profileDetails.contactDetail.address.line4 ?? '',
        addressLine5: profileDetails.contactDetail.address.line5 ?? '',
        postCode: profileDetails.contactDetail.address.postCode ?? '',
        country: profileDetails.contactDetail.address.countryCode
          ? profileDetails.contactDetail.address.countryCode === LegacyCountryCode.DE
            ? ShortCountry.DE
            : profileDetails.contactDetail.address.countryCode
          : '',
      }
    : undefined;

  return (
    <div className={containerStyle}>
      <TypeOfAddress
        formRef={addressTypeFormRef}
        onSubmit={onAddressTypeSubmit}
        icons={icons}
        companyName={companyName}
        onAddressTypeChange={onAddressTypeChange}
      />

      {isBusinessAddress && (
        <CompanyName
          formRef={companyNameFormRef}
          onSubmit={onCompanyNameSubmit}
          onCompanyNameChange={onCompanyNameChange}
          icons={icons}
          companyName={companyName}
          isAddressType
        />
      )}

      <CompanyAddress
        onSubmit={onAddressSubmit}
        onAddressChange={onAddressChange}
        icons={icons}
        formRef={addressFormRef}
        onOpen={handleAddressSearch}
        isCompanyDetailsContainer
        language={language}
        isProfilePage
        postalCode={postalCode}
        addressData={addressData}
      />
    </div>
  );
};

export default React.memo(DifferentAddressForm);

const containerStyle = 'space-y-6';
