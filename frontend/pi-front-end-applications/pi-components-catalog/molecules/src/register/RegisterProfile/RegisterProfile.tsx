import { type FormDynamicFieldCompProps } from '@whitbread-eos/atoms';
import { GLOBALS } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

export default function RegisterProfile({
  formField,
  handleSetValue,
}: Readonly<FormDynamicFieldCompProps>) {
  const [triggerSetValues, setTriggerSetValues] = useState(0);

  useEffect(() => {
    if (handleSetValue && triggerSetValues) {
      const profileData =
        formField?.props?.bkngData?.bookingConfirmation?.reservationByIdList[0]
          ?.reservationBooker || {};
      const { firstName, lastName, email, mobile, landline, title, address } = profileData;
      const {
        addressType,
        companyName,
        addressLine1,
        addressLine2,
        addressLine3,
        addressLine4,
        cityName,
        countryCode,
        postalCode,
      } = address || {};

      if (profileData) {
        handleSetValue(`title`, title);
        handleSetValue(`firstName`, firstName);
        handleSetValue(`lastName`, lastName);
        handleSetValue(`email`, email);
        handleSetValue(`phone`, mobile || landline);
        handleSetValue(
          `addressSelection`,
          formField?.props?.currentLang === GLOBALS.language.DE
            ? GLOBALS.addressType.HOME
            : addressType
        );
        handleSetValue(`companyName`, companyName);
        handleSetValue(`addressLine1`, addressLine1);
        handleSetValue(`addressLine2`, addressLine2);
        handleSetValue(`addressLine3`, addressLine3);
        countryCode === GLOBALS.localeUpper.DE
          ? handleSetValue(`cityName`, cityName)
          : handleSetValue(`addressLine4`, addressLine4);
        handleSetValue(`postalCode`, postalCode);
        handleSetValue(`countryCode`, countryCode);
      }
    }
  }, [handleSetValue, triggerSetValues, formField?.props?.bkngData]);

  useEffect(() => {
    setTriggerSetValues((prev) => prev + 1);
  }, []);

  return null;
}
