import type { FlexProps, TextProps } from '@chakra-ui/react';
import { Flex, Text } from '@chakra-ui/react';
import { type GuestDetailsFormStateLocalResetType } from '@whitbread-eos/api';
import { ChevronLeft24, FormDynamicFieldCompProps, Icon } from '@whitbread-eos/atoms';
import {
  formatDataTestId,
  useAuthToken,
  useSemanticTypography,
  useCustomLocale,
  useLocalStorage,
} from '@whitbread-eos/utils';
import { setGuestFormData } from '@whitbread-eos/utils/server';
import { useTranslation } from 'next-i18next';
import { useEffect } from 'react';

import { INITIAL_GUEST_DETAILS_FORM_DATA } from '../../utils/constants';

export default function BackButton({
  handleSetValue,
  handleResetField,
  getValues,
  reset,
  formField,
}: Readonly<FormDynamicFieldCompProps>) {
  const { language: currentLang } = useCustomLocale();
  const { t } = useTranslation();
  const getTypographyProps = useSemanticTypography();

  const [formDetails, setFormDetails] = useLocalStorage(
    'formDetails',
    INITIAL_GUEST_DETAILS_FORM_DATA
  );

  const { token: authToken, isLoading: isAuthTokenLoading } = useAuthToken();

  const resetFormFields = (basketReferenceId: string, defaultValues: { [key: string]: any }) => {
    localStorage.removeItem('formDetails');
    const defaultSetValues: { [key: string]: any } = {};
    for (const key in defaultValues) {
      if (defaultValues[key] !== '') {
        defaultSetValues[key] = defaultValues[key];
      }
    }
    setFormDetails({
      ...INITIAL_GUEST_DETAILS_FORM_DATA,
      basketReferenceId,
      ...defaultSetValues,
    });

    // trigger UI update on title dropdown
    if (
      formDetails['title'] &&
      formDetails['title'].length > 0 &&
      handleSetValue &&
      handleResetField
    ) {
      handleSetValue('title', '');
      handleResetField('title', { defaultValue: '' });
    }
  };

  useEffect(() => {
    if (isAuthTokenLoading) return;

    if (
      formDetails &&
      handleSetValue &&
      formField?.props?.isRemovePIIDataFromLocalStorageEnabled === false
    ) {
      if (!authToken) {
        if (formDetails['basketReferenceId'] !== formField?.props?.basketReferenceId) {
          resetFormFields(formField?.props?.basketReferenceId, formField?.props?.defaultValues);
        } else {
          triggerReset(formDetails);
        }
      } else {
        formDetails.updated &&
          formDetails['basketReferenceId'] === formField?.props?.basketReferenceId &&
          triggerReset(formDetails);
      }
    }
  }, [
    formDetails,
    formField?.props?.basketReferenceId,
    formField?.props?.isRemovePIIDataFromLocalStorageEnabled,
    handleSetValue,
    reset,
    authToken,
    isAuthTokenLoading,
  ]);

  const triggerReset = (formDetails: any) => {
    const resetObject: GuestDetailsFormStateLocalResetType = {};
    let shouldReset = false;
    for (const key of Object.keys(formDetails)) {
      if (!['basketReferenceId', 'updated'].includes(key)) {
        if (formDetails[key] !== '') {
          shouldReset = true;
        }
        resetObject[key] = formDetails[key];
      }
    }
    if (shouldReset) {
      reset?.(resetObject);
    }

    if (formDetails.postalCode?.length || currentLang === 'de') {
      handleSetValue?.('manualAddressToggle', 'manualAddress');
    }
  };

  const sanitizePIIFields = (formData: any) => {
    if (!formData || typeof formData !== 'object') {
      return formData;
    }

    /* eslint-disable @typescript-eslint/no-unused-vars */
    const {
      additionalInformation,
      passport,
      nationality,
      dateOfBirth,
      anonRfs,
      backButton,
      userProfile,
      ...restData
    } = formData;
    /* eslint-enable @typescript-eslint/no-unused-vars */

    const sanitizedLeadGuest = Array.isArray(restData.leadGuest)
      ? restData.leadGuest.map((guest: any) => {
          if (!guest || typeof guest !== 'object') {
            return guest;
          }

          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          const { passport, nationality, consent, dateOfBirth, ...guestRest } = guest;
          return guestRest;
        })
      : restData.leadGuest;

    return {
      ...restData,
      companyName: formData?.addressSelection === 'BUSINESS' ? formData.companyName : '',
      basketReferenceId: formField?.props?.basketReferenceId,
      leadGuest: sanitizedLeadGuest,
    };
  };

  const handleClick = async () => {
    const formData = getValues();
    if (formField?.props?.isRemovePIIDataFromLocalStorageEnabled) {
      await setGuestFormData(formField?.props?.basketReferenceId, sanitizePIIFields(formData));
    } else {
      setFormDetails((formDetails: any) => {
        if (formData) {
          formDetails.addressLine1 = formData.addressLine1;
          formDetails.addressLine2 = formData.addressLine2;
          formDetails.addressLine3 = formData.addressLine3;
          formDetails.addressLine4 = formData.addressLine4;
          formDetails.postcodeAddress = formData.postcodeAddress;
          formDetails.addressSelection = formData.addressSelection;
          formDetails.cityName = formData.cityName;
          formDetails.companyName = formData.companyName;
          formDetails.countryCode = formData.countryCode;
          formDetails.email = formData.email;
          formDetails.firstName = formData.firstName;
          formDetails.landline = formData.landline;
          formDetails.lastName = formData.lastName;
          formDetails.manualAddressToggle = formData.manualAddressToggle;
          formDetails.phone = formData.phone;
          formDetails.postalCode = formData.postalCode;
          formDetails.reasonForStay = formData.reasonForStay;
          formDetails.title = formData.title;
          formDetails.bookingForSomeoneElse = formData.bookingForSomeoneElse;
          formDetails.leadGuest = formData.leadGuest;
          formDetails.basketReferenceId = formField?.props?.basketReferenceId;
          formDetails.updated = true;
          return formDetails;
        }
      });
    }
    formField?.props?.goBack?.();
  };

  return (
    <Flex
      onClick={() => handleClick()}
      {...buttonWrapper}
      data-testid={formatDataTestId('GuestDetails', 'BackToAncillariesButton')}
    >
      <Icon svg={<ChevronLeft24 />} />
      <Text {...textStyle} {...getTypographyProps(textLegacyTypography, textSemanticTypography)}>
        {t('booking.summary.back')}
      </Text>
    </Flex>
  );
}

const buttonWrapper = {
  alignItems: 'center',
  w: 'fit-content',
  justifyContent: 'flex-start',
  cursor: 'pointer',
} as FlexProps;

const textStyle = {
  pl: 'md',
  color: 'darkGrey1',
} as TextProps;

const textLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: 'md',
  lineHeight: '3',
} as TextProps;

const textSemanticTypography = {
  textStyle: 'link-m-regular',
} as TextProps;
