import { type FormDynamicFieldCompProps } from '@whitbread-eos/atoms';
import { GLOBALS } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

export default function CompanyBillingProfile({
  formField,
  handleSetValue,
}: Readonly<FormDynamicFieldCompProps>) {
  const [triggerSetValues, setTriggerSetValues] = useState(0);

  useEffect(() => {
    if (handleSetValue && triggerSetValues) {
      const companyProfile = formField?.props?.companyProfile;

      if (companyProfile) {
        handleSetValue(`billing_addressSelection`, GLOBALS.addressType.BUSINESS);
        handleSetValue(`billing_companyName`, companyProfile?.name);
        handleSetValue(`billing_addressLine1`, companyProfile?.address?.addressLine1);
        handleSetValue(`billing_addressLine2`, companyProfile?.address?.addressLine2);
        handleSetValue(`billing_addressLine3`, companyProfile?.address?.addressLine3);
        handleSetValue(`billing_addressLine4`, companyProfile?.address?.addressLine4);
        handleSetValue(`billing_cityName`, companyProfile?.address?.addressLine4);
        handleSetValue(`billing_postalCode`, companyProfile?.address?.postalCode);
      }
    }
  }, [handleSetValue, triggerSetValues, formField?.props?.companyProfile]);

  useEffect(() => {
    setTriggerSetValues((prev) => prev + 1);
  }, []);

  return null;
}
