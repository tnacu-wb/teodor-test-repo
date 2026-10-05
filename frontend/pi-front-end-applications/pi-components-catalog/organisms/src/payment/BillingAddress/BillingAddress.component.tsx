import { AddressGuestInput, BillingAddressFormData } from '@whitbread-eos/api';
import { Form, FormProps } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import { SetStateAction, useCallback, useEffect, useState } from 'react';

import { billingAddressFormConfig } from './billingAddressFormConfig';

interface BillingAddressProps {
  currentBillingAddress: any;
  continueToNextStep: (billingAddress?: AddressGuestInput) => void;
  t: (x: string, y?: { [key: string]: string }) => string;
  currentLang: string | undefined;
  isAmendPage?: boolean;
  hasError?: boolean;
  setBillingAddress?: React.Dispatch<AddressGuestInput>;
  isCompanyNameAdvanceEnabled?: boolean;
  horizontalRadioButtons?: boolean;
  isCountryAllowTypingEnabled?: boolean;
  isDatatransPage?: boolean;
}

export default function BillingAddress({
  currentBillingAddress,
  continueToNextStep,
  t,
  currentLang,
  isAmendPage,
  hasError,
  setBillingAddress,
  isCompanyNameAdvanceEnabled,
  horizontalRadioButtons,
  isCountryAllowTypingEnabled,
  isDatatransPage = false,
}: Readonly<BillingAddressProps>) {
  const baseDataTestId = 'Payment';
  const testid = formatDataTestId(baseDataTestId, 'BillingAdress-MainContainer');
  const [isLocationRequired, setIsLocationRequired] = useState(false);

  const [defaultValues, setDefaultValues] = useState<FormProps['defaultValues']>({
    companyName: '',
    addressLine1: '',
    addressLine2: '',
    addressLine3: '',
    addressLine4: '',
    postalCode: '',
    cityName: '',
    postcodeAddress: '',
    addressSelection: 'HOME',
    billingAddressSelection: isDatatransPage ? 'DifferentAddress' : 'CurrentAddress',
    countryCode: currentLang === 'en' ? 'GB' : 'DE',
    manualAddressToggle: currentLang === 'en' ? '' : 'manualAddress',
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

  const extractBillingAddress = (data: BillingAddressFormData) => {
    let billingAddress: AddressGuestInput;
    if (data.billingAddressSelection === 'DifferentAddress') {
      billingAddress = {
        addressLine1: data.addressLine1,
        addressLine2: data.addressLine2 ?? '',
        addressLine3: data.addressLine3 ?? '',
        addressLine4: data.addressLine4 ?? '',
        postalCode: data.postalCode.trim(),
        countryCode: data.countryCode,
        companyName: data.companyName,
      };
    } else {
      billingAddress = currentBillingAddress;
    }
    return billingAddress;
  };
  const onChangeAction = (data: BillingAddressFormData) => {
    const billingAddress = extractBillingAddress(data);
    setBillingAddress?.(billingAddress);
  };

  const onSubmitAction = (data: BillingAddressFormData) => {
    const billingAddress = extractBillingAddress(data);
    !hasError && continueToNextStep(billingAddress);
  };

  const currentAddressLabel = `${currentBillingAddress?.postalCode} ${currentBillingAddress?.addressLine1}`;

  return (
    <Form
      {...billingAddressFormConfig({
        getFormState,
        defaultValues,
        onSubmitAction,
        baseDataTestId,
        testid,
        t,
        currentLang,
        currentAddressLabel,
        isAmendPage,
        isLocationRequired,
        setIsLocationRequired,
        isCompanyNameAdvanceEnabled,
        horizontalRadioButtons,
        isCountryAllowTypingEnabled,
        isDatatransPage,
      })}
      onChange={onChangeAction}
    />
  );
}
