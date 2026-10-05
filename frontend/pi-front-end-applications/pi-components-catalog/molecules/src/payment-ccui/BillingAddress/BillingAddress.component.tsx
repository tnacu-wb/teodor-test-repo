import { AddressGuestInput, BillingAddressFormData, CompanyProfile } from '@whitbread-eos/api';
import { Form, FormProps } from '@whitbread-eos/atoms';
import { formatDataTestId, GLOBALS } from '@whitbread-eos/utils';
import { Dispatch, SetStateAction, useCallback, useEffect, useState } from 'react';

import { billingAddressFormConfig } from './billingAddressFormConfig';

interface BillingAddressProps {
  currentBillingAddress: any;
  setBillingAddress: Dispatch<SetStateAction<any>>;
  t: (x: string, y?: { [key: string]: string }) => string;
  currentLang: string | undefined;
  continueToNextStep: (billingAddress?: AddressGuestInput) => void;
  companyProfile?: CompanyProfile;
  isCompanyNameAdvanceEnabled?: boolean;
  isCountryAllowTypingEnabled?: boolean;
}

export default function BillingAddress({
  currentBillingAddress,
  continueToNextStep,
  t,
  currentLang,
  setBillingAddress,
  companyProfile,
  isCompanyNameAdvanceEnabled,
  isCountryAllowTypingEnabled,
}: Readonly<BillingAddressProps>) {
  const baseDataTestId = 'Payment';
  const testid = formatDataTestId(baseDataTestId, 'BillingAdress-MainContainer');
  const [isLocationRequired, setIsLocationRequired] = useState(false);

  const countryCode = () => {
    if (companyProfile?.address?.country) {
      return companyProfile?.address?.country;
    } else if (currentLang === GLOBALS.language.EN) {
      return GLOBALS.localeUpper.GB;
    } else {
      return GLOBALS.localeUpper.DE;
    }
  };

  const [defaultValues, setDefaultValues] = useState<FormProps['defaultValues']>({
    companyName: companyProfile?.name ?? '',
    addressLine1: companyProfile?.address?.addressLine1 ?? '',
    addressLine2: companyProfile?.address?.addressLine2 ?? '',
    addressLine3: companyProfile?.address?.addressLine3 ?? '',
    addressLine4: companyProfile?.address?.addressLine4 ?? '',
    postalCode: companyProfile?.address?.postalCode ?? '',
    cityName: companyProfile?.address?.addressLine4 ?? '',
    postcodeAddress: '',
    addressSelection: companyProfile ? GLOBALS.addressType.BUSINESS : GLOBALS.addressType.HOME,
    billingAddressSelection: companyProfile
      ? GLOBALS.billingAddressType.DIFFERENT
      : GLOBALS.billingAddressType.CURRENT,
    countryCode: countryCode(),
  });

  const getFormState: FormProps['getFormState'] = useCallback(
    (state: any) => {
      setDefaultValues(state as SetStateAction<FormProps['defaultValues']>);
    },
    [setDefaultValues]
  );

  useEffect(() => {
    if ((defaultValues.countryCode as string) === 'DE') {
      setIsLocationRequired(true);
    }
  }, [defaultValues.countryCode]);

  const onSubmitAction = (data: BillingAddressFormData) => {
    let billingAddress: AddressGuestInput;
    if (data.billingAddressSelection === 'DifferentAddress') {
      billingAddress = {
        addressLine1: data.addressLine1,
        addressLine2: data.addressLine2 ?? '',
        addressLine3: data.addressLine3 ?? '',
        addressLine4: data.addressLine4 ?? '',
        cityName: data.cityName ?? '',
        postalCode: data.postalCode.trim(),
        countryCode: data.countryCode,
        companyName: data.companyName,
        addressType: data.addressSelection,
      };
    } else {
      billingAddress = currentBillingAddress;
    }
    continueToNextStep(billingAddress);
  };

  const currentAddressLabel = `${currentBillingAddress?.postalCode} ${currentBillingAddress?.addressLine1}`;

  return (
    <Form
      onChange={setBillingAddress}
      {...billingAddressFormConfig({
        getFormState,
        defaultValues,
        onSubmitAction,
        baseDataTestId,
        testid,
        t,
        currentLang,
        currentAddressLabel,
        isLocationRequired,
        setIsLocationRequired,
        companyProfile,
        isCompanyNameAdvanceEnabled,
        isCountryAllowTypingEnabled,
      })}
    />
  );
}
