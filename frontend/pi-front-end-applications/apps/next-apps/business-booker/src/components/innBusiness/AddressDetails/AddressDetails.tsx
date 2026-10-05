'use client';

import { AddressInfo, Language, CountryDetails } from '@whitbread-eos/api';
import { Skeleton } from '@whitbread-eos/atoms/ui';
import { getCountriesList, getCountryName } from '@whitbread-eos/utils/server';
import { useEffect, useState } from 'react';
import * as React from 'react';

type Props = {
  addressInfo?: AddressInfo;
  language: Language;
  isCompanyInformation?: boolean;
  companyName?: string;
};

export function AddressDetails({
  addressInfo,
  language,
  isCompanyInformation = false,
  companyName,
}: Props) {
  const [countryName, setCountryName] = useState<string>('');
  const [countries, setCountries] = useState<CountryDetails[]>([]);

  useEffect(() => {
    const setCountriesList = async () => {
      const countries = await getCountriesList(language);
      setCountries(countries);
    };

    setCountriesList();
  }, [language]);

  useEffect(() => {
    setCountryName(getCountryName(addressInfo?.country, countries));
  }, [addressInfo, countries]);

  return (
    <div data-testid="AddressDetails" className={divStyle}>
      {isCompanyInformation && companyName && (
        <span data-testid="AddressDetails-company-name">{companyName}</span>
      )}
      <span data-testid="AddressDetails-addressLine1">{addressInfo?.addressLine1}</span>
      {addressInfo?.addressLine2 && (
        <span data-testid="AddressDetails-addressLine2">{addressInfo?.addressLine2}</span>
      )}
      {addressInfo?.addressLine3 && (
        <span data-testid="AddressDetails-addressLine3">{addressInfo?.addressLine3}</span>
      )}
      {addressInfo?.addressLine4 && (
        <span data-testid="AddressDetails-addressLine4">{addressInfo?.addressLine4}</span>
      )}
      {addressInfo?.addressLine5 && (
        <span data-testid="AddressDetails-addressLine5">{addressInfo?.addressLine5}</span>
      )}
      {addressInfo?.postCode && (
        <span data-testid="AddressDetails-postCode">{addressInfo?.postCode}</span>
      )}
      {countryName ? (
        <span data-testid="AddressDetails-countryName">{countryName}</span>
      ) : (
        <Skeleton className="h-6 w-[6rem]" />
      )}
    </div>
  );
}

const divStyle = 'flex flex-col items-start text-base font-normal';
