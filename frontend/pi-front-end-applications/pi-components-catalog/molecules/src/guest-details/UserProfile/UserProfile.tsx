import {
  PurposeOfStay,
  PurposeOfStayAnalytics,
  LanguageEnum,
  ShortCountry,
} from '@whitbread-eos/api';
import { FieldsType, type FormDynamicFieldCompProps } from '@whitbread-eos/atoms';
import {
  analytics,
  decodeIdToken,
  GLOBALS,
  setCookie,
  useAuth0User,
  useAuthToken,
  useRestQueryRequest,
  useUserData,
} from '@whitbread-eos/utils';
import getConfig from 'next/config';
import { useEffect, useState } from 'react';

import { extractFormDetailsFlags } from '../AnonRFS';

export default function UserProfile({ reset, formField }: Readonly<FormDynamicFieldCompProps>) {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const { isLoggedIn } = useUserData();
  const [triggerSetValues, setTriggerSetValues] = useState(0);
  const formDetailsFlags = extractFormDetailsFlags(formField?.props?.basketReferenceId);

  const { token, isAuth0Enabled, isLoading: isTokenLoading } = useAuthToken();
  const { user: auth0User } = useAuth0User(isAuth0Enabled);
  const email = isAuth0Enabled
    ? (auth0User?.email ?? '')
    : (decodeIdToken(token ?? '')?.email ?? '');

  const { data, isSuccess, isFetching } = useRestQueryRequest(
    ['userDetails', token],
    'GET',
    `${publicRuntimeConfig.NEXT_PUBLIC_REST_API}/customers/hotels/${email}?business=false`,
    { Authorization: `Bearer ${token}` },
    { enabled: isLoggedIn && !formDetailsFlags.updated && !!email && !isTokenLoading }
  );
  const isGermanHotel = formField?.props?.isGermanHotel;
  const isMultiRoomRedesignEnabled = formField?.props?.isMultiRoomRedesignEnabled;
  const setIsLocationRequired = formField.props?.setIsLocationRequired;
  const setCountryOfResidence = formField.props?.setCountryOfResidence;
  const currentLang = formField?.props?.currentLang;
  const isRemovePIIDataFromLocalStorageEnabled =
    !!formField?.props?.isRemovePIIDataFromLocalStorageEnabled;
  const hasDefaultBasketReferenceId = !!formField?.props?.defaultValues?.basketReferenceId;
  const updateWhenIsRemovePIIDataFromLocalStorage =
    !isRemovePIIDataFromLocalStorageEnabled || !hasDefaultBasketReferenceId;

  useEffect(() => {
    if (
      isSuccess &&
      reset &&
      isLoggedIn &&
      triggerSetValues &&
      !formDetailsFlags.updated &&
      updateWhenIsRemovePIIDataFromLocalStorage
    ) {
      handleUserProfileUpdate(
        data,
        formField,
        reset,
        isGermanHotel,
        isMultiRoomRedesignEnabled,
        currentLang,
        setIsLocationRequired,
        setCountryOfResidence
      );
    }
  }, [
    isSuccess,
    data,
    reset,
    triggerSetValues,
    isLoggedIn,
    formDetailsFlags.updated,
    updateWhenIsRemovePIIDataFromLocalStorage,
  ]);

  useEffect(() => {
    setTriggerSetValues((prev) => prev + 1);
  }, []);

  if (isLoggedIn && isFetching && !formDetailsFlags.updated) {
    return <span>Loading user profile...</span>;
  }

  return null;
}

function handleUserProfileUpdate(
  data: any,
  formField: FieldsType,
  reset: any,
  isGermanHotel: boolean,
  isMultiRoomRedesignEnabled: boolean,
  currentLang: LanguageEnum,
  setIsLocationRequired: any,
  setCountryOfResidence?: (countryCode: string) => void
) {
  const {
    businessUse,
    contactDetail: {
      title,
      firstName,
      lastName,
      email,
      mobile,
      address: { companyName, countryCodeISO, line1, line2, line3, line4, postCode, type },
    },
  } = data;

  setCookie('userData', JSON.stringify({ firstName }), 24 * 60 * 60 * 1000);

  if (businessUse === true) {
    analytics.update({ bookingReasonForStay: PurposeOfStayAnalytics.BUSINESS });
    formField?.props?.updateReasonForStay?.(PurposeOfStay.BUSINESS);
  }

  const countryCode = getCountryCode(countryCodeISO, currentLang);

  const resetObject = {
    reasonForStay: businessUse === true ? PurposeOfStay.BUSINESS : '',
    title,
    firstName,
    lastName,
    email,
    phone: mobile,
    landline: '',
    manualAddressToggle: 'manualAddress',
    addressSelection: isGermanHotel && isMultiRoomRedesignEnabled ? GLOBALS.addressType.HOME : type,
    companyName: isGermanHotel && isMultiRoomRedesignEnabled ? '' : companyName,
    addressLine1: line1,
    addressLine2: line2,
    addressLine3: line3,
    addressLine4: line4,
    cityName: line4,
    postalCode: postCode,
    countryCode: countryCode,
    postcodeAddress: '',
  };

  if (typeof setIsLocationRequired === 'function') {
    setIsLocationRequired(countryCode === ShortCountry.DE);
  }

  if (typeof setCountryOfResidence === 'function') {
    setCountryOfResidence(countryCode.toLowerCase());
  }

  reset(resetObject);
}

function getCountryCode(countryCodeISO: any, currentLang: LanguageEnum) {
  if (countryCodeISO) {
    return countryCodeISO;
  }

  return currentLang === LanguageEnum.GERMAN ? ShortCountry.DE : ShortCountry.GB;
}
