import { Box, type BoxProps, Text, type TextProps } from '@chakra-ui/react';
import { Area, backendDataType, GDPersonalDetails, HotelBrand } from '@whitbread-eos/api';
import {
  FORM_BUTTON_TYPES,
  FORM_FIELD_TYPES,
  FORM_VALIDATIONS,
  FormDynamicFieldCompProps,
  FormProps,
  Info,
  Notification,
} from '@whitbread-eos/atoms';
import {
  AdditionalInformation,
  AnonRFS,
  CountriesDropdown,
  EmailOptOut,
  EmailUpdates,
  GuestDetailsBackButton as BackButton,
  LeadGuestDetails,
  Notice,
  PhoneSelector,
  PostcodeAddress,
  UserProfile,
} from '@whitbread-eos/molecules';
import {
  analytics,
  formatAssetsUrl,
  formatDataTestId,
  formatGuestTitleOptions,
} from '@whitbread-eos/utils';
import dynamic from 'next/dynamic';
import { Dispatch, SetStateAction, useEffect } from 'react';

// validation rules set here
import validateForm from './formValidation';

const Link = dynamic(
  async () => {
    const { Link } = await import('@chakra-ui/react');
    return { default: Link };
  },
  {
    ssr: false,
  }
);
interface GuestDetailsFormConfigArgsType {
  getTypographyProps: (legacyTypography: TextProps, semanticTypography: TextProps) => TextProps;
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: (data: GDPersonalDetails) => void;
  baseDataTestId: string;
  t: (id: string) => string;
  currentLang: string | undefined;
  basketReferenceId: string;
  resetForm?: number;
  brand?: string;
  bkndData: backendDataType;
  goBack: () => void;
  cityTaxMessages: {
    mainBanner: string;
    secondaryBanner: string;
    summaryText: string;
  };
  updateReasonForStay: (reasonForStay: string) => void;
  isLocationRequired: boolean;
  setIsLocationRequired: Dispatch<SetStateAction<boolean>>;
  hotelBrand?: string;
  isRegisterSelected: boolean;
  isSingleRoomRedesignEnabled: boolean;
  isMultiRoomRedesignEnabled: boolean;
  isBillingAddressEnabled: boolean;
  isBookingForSomeoneElse: boolean;
  setIsBookingForSomeoneElse: Dispatch<SetStateAction<boolean>>;
  isGermanHotel?: boolean;
  isAdditionalInformationEnabled?: boolean;
  showCheckInInfo: { [key: string]: boolean };
  setShowCheckInInfo: Dispatch<SetStateAction<{ [key: string]: boolean }>>;
  isCompanyNameAdvanceEnabled?: boolean;
  shouldAskForAccompanyingGuest?: boolean;
  isDifferentBillingAddress?: boolean;
  suppressMarketingCheckbox?: boolean;
  isDEOptInEnabled?: boolean;
  submitButtonDisabled?: boolean;
  isConsolidateMobileLandlineEnabled?: boolean;
  isCountryAllowTypingEnabled?: boolean;
  isRemovePIIDataFromLocalStorageEnabled: boolean;
  setCountryOfResidence?: Dispatch<SetStateAction<string>>;
  isAuth0Enabled?: boolean;
  isUserSignedIn?: boolean;
  privacyPolicyLinkPath?: string;
}

declare module 'yup' {
  interface ArraySchema<T> {
    unique(a: string, message: string): ArraySchema<T>;
  }
}

const EmailWrapper = (props: FormDynamicFieldCompProps | any) => {
  return props?.formField?.props?.isDEOptInEnabled ? (
    <EmailOptOut {...props} />
  ) : (
    <EmailUpdates {...props} />
  );
};

export const guestDetailsFormConfig = ({
  getTypographyProps,
  getFormState,
  defaultValues,
  onSubmit,
  baseDataTestId,
  t,
  currentLang,
  basketReferenceId,
  resetForm,
  brand,
  bkndData,
  goBack,
  cityTaxMessages,
  updateReasonForStay,
  isLocationRequired,
  setIsLocationRequired,
  hotelBrand,
  isRegisterSelected,
  isSingleRoomRedesignEnabled,
  isMultiRoomRedesignEnabled,
  isBookingForSomeoneElse,
  setIsBookingForSomeoneElse,
  isGermanHotel,
  isBillingAddressEnabled,
  isAdditionalInformationEnabled,
  showCheckInInfo,
  setShowCheckInInfo,
  isCompanyNameAdvanceEnabled,
  shouldAskForAccompanyingGuest = false,
  isDifferentBillingAddress,
  suppressMarketingCheckbox = false,
  isDEOptInEnabled,
  submitButtonDisabled,
  isConsolidateMobileLandlineEnabled,
  isCountryAllowTypingEnabled,
  isRemovePIIDataFromLocalStorageEnabled,
  setCountryOfResidence,
  isAuth0Enabled = false,
  isUserSignedIn = false,
  privacyPolicyLinkPath,
}: GuestDetailsFormConfigArgsType) => {
  // Validation
  const { formValidationSchema } = validateForm({
    t,
    bkndData,
    currentLang,
    isRegisterSelected,
    isSingleRoomRedesignEnabled,
    isMultiRoomRedesignEnabled,
    isBookingForSomeoneElse,
    isGermanHotel,
    isAdditionalInformationEnabled,
    isCompanyNameAdvanceEnabled,
    isAccompanyingGuestEnabled: shouldAskForAccompanyingGuest,
    isConsolidateMobileLandlineEnabled,
  });

  const guestTitleOptions = formatGuestTitleOptions(t('common.nameTitles'));
  const privacyPolicyIconPath = t('booking.privacyPolicyIcon');
  const normalizedPrivacyPolicyIconPath =
    privacyPolicyIconPath && privacyPolicyIconPath !== 'booking.privacyPolicyIcon'
      ? privacyPolicyIconPath.replace(/^"|"$/g, '').trim()
      : '';
  const resolvedPrivacyPolicyIconPath = /^https?:\/\//.test(normalizedPrivacyPolicyIconPath)
    ? normalizedPrivacyPolicyIconPath
    : formatAssetsUrl(
        normalizedPrivacyPolicyIconPath || '/content/dam/global/icons/common/privacy-icon.svg'
      );

  const errorsOrder = [
    'reasonForStay',
    'title',
    'firstName',
    'lastName',
    'email',
    'phone',
    'landline',
    'addressSelection',
    'companyName',
    'addressLine1',
    'addressLine2',
    'addressLine3',
    'addressLine4',
    'postalCode',
    'cityName',
    'dateOfBirth',
    'nationality',
    'passport',
    'billing_addressSelection',
    'billing_companyName',
    'billing_addressLine1',
    'billing_postalCode',
    'billing_cityName',
  ];

  const ManualAddressToggle = ({
    field,
    handleSetValue,
    errors,
    formField,
  }: FormDynamicFieldCompProps) => {
    const { name } = field;
    const fieldName = formField.props?.fieldName;
    // Trigger automatic expansion of manual address, when no Postal Code is entered
    useEffect(() => {
      if (!handleSetValue) {
        return;
      }
      if (errors?.addressLine1) {
        handleSetValue(name, fieldName);
      }
    }, [handleSetValue, name, errors]);

    if (field.value === fieldName) {
      return null;
    }

    const enterManualAddress = () => {
      if (!handleSetValue) {
        return;
      }
      handleSetValue(name, fieldName);
    };

    const manualAddressLinkTypographyStyles = {
      textStyle: getTypographyProps({}, manualAddressLinkSemanticTypography).textStyle,
    };

    return (
      <Link
        {...linkStyles}
        {...manualAddressLinkTypographyStyles}
        as="button"
        type="button"
        onClick={enterManualAddress}
      >
        {t('booking.enterManuallAddress')}
      </Link>
    );
  };

  const isDataNotificationHidden = () =>
    getIsDataNotificationHidden(currentLang, bkndData?.hiData?.hotelInformation?.brand);

  const isSingleRoomBooking = bkndData.rooms?.length === 1 ? true : false;

  const singleBookingReasonForStayRedesignStyle = isSingleRoomRedesignEnabled
    ? singleBookingStyle
    : inputStyle;

  const inputStyleFields =
    isSingleRoomRedesignEnabled && isSingleRoomBooking ? singleBookingStyleFields : inputStyle;

  const isUpdateProfileConsentVisible = isAuth0Enabled && isUserSignedIn;

  const subHeadingLayoutStyles =
    isSingleRoomRedesignEnabled || isMultiRoomRedesignEnabled || isBillingAddressEnabled
      ? ({ color: 'darkGrey1' } as TextProps)
      : ({ color: 'darkGrey1', pt: 'lg', pb: 'var(--chakra-space-3)' } as TextProps);

  const subHeadingLegacyTypography =
    isSingleRoomRedesignEnabled || isMultiRoomRedesignEnabled || isBillingAddressEnabled
      ? heading2LegacyTypography
      : heading1LegacyTypography;

  const noBorderOrPadding =
    !isSingleRoomBooking && isSingleRoomRedesignEnabled ? noBordersPadding : '';

  const inputFieldTypographyStyles = {
    inputElementStyles: getTypographyProps({}, bodyMRegularSemanticTypography),
  };

  const countrySelectorTypographyStyles = {
    inputElementStyles: getTypographyProps({}, bodyMRegularSemanticTypography),
  };

  const continueButtonTypographyStyles = getTypographyProps(
    continueButtonLegacyTypography,
    labelXlSemanticTypography
  );

  const postcodeAddressButtonTypographyStyles = getTypographyProps({}, labelXlSemanticTypography);

  const postcodeAddressDropdownTypographyStyles = {
    menuButtonTextStyles: getTypographyProps({}, bodyMRegularSemanticTypography),
    menuItemTextStyles: getTypographyProps(
      postcodeAddressDropdownItemLegacyTypography,
      bodySRegularSemanticTypography
    ),
  };

  const countryProps = {
    showIcon: false,
    setIsLocationRequired,
    isCountrySelectorFilterableEnabled: isCountryAllowTypingEnabled,
    styles: countrySelectorTypographyStyles,
  };

  const homeAddressFields = [
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine1',
      id: 'homeAddressLine1',
      label: t('booking.addressAddress1'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-AddressLine1'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // home address line 1
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine2',
      id: 'homeAddressLine2',
      label: t('booking.addressAddress2'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-AddressLine2'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // home address line 2
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine3',
      id: 'homeAddressLine3',
      label: t('booking.addressAddress3'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-AddressLine3'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // home address line 3
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine4',
      id: 'homeAddressLine4',
      label: t('booking.addressAddress4'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-AddressLine4'),
      styles: inputStyleFields,
      hidden: isLocationRequired,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // home address line 4
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'postalCode',
      id: 'homePostalCode',
      label: t('booking.addressPostCode'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-PostalCode'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // home postal code
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'cityName',
      id: 'homeAddressCityName',
      label: t('booking.addressCity'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-CityName'),
      styles: inputStyleFields,
      hidden: !isLocationRequired,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // home city name
      },
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'countryCode',
      Component: CountriesDropdown,
      testid: formatDataTestId(baseDataTestId, 'CountrySelector'),
      props: countryProps,
      styles: {
        ...inputStyleFields,
        pt: 'xs',
      },
      label: t('booking.country'),
    },
  ];

  const billingHomeAddressFields = [
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'billing_addressLine1',
      id: 'billingHomeAddressLine1',
      label: t('booking.addressAddress1'),
      testid: formatDataTestId(baseDataTestId, 'BillingAddress-PersonalAddress-AddressLine1'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // billing address line 1
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'billing_addressLine2',
      id: 'billingHomeAddressLine2',
      label: t('booking.addressAddress2'),
      testid: formatDataTestId(baseDataTestId, 'BillingAddress-PersonalAddress-AddressLine2'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // billing address line 2
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'billing_addressLine3',
      id: 'billingHomeAddressLine3',
      label: t('booking.addressAddress3'),
      testid: formatDataTestId(baseDataTestId, 'BillingAddress-PersonalAddress-AddressLine3'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // billing address line 3
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'billing_addressLine4',
      id: 'billingHomeAddressLine4',
      label: t('booking.addressAddress4'),
      testid: formatDataTestId(baseDataTestId, 'BillingAddress-PersonalAddress-AddressLine4'),
      styles: inputStyleFields,
      hidden: isLocationRequired,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // billing address line 4
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'billing_postalCode',
      id: 'billingHomePostalCode',
      label: t('booking.addressPostCode'),
      testid: formatDataTestId(baseDataTestId, 'BillingAddress-PersonalAddress-PostalCode'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // billing postal code
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'billing_cityName',
      id: 'homeAddressCityName',
      label: t('booking.addressCity'),
      testid: formatDataTestId(baseDataTestId, 'BillingAddress-PersonalAddress-CityName'),
      styles: inputStyleFields,
      hidden: !isLocationRequired,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // billing city name
      },
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'billing_countryCode',
      Component: CountriesDropdown,
      props: countryProps,
      testid: formatDataTestId(baseDataTestId, 'CountrySelector'),
      label: t('booking.country'),
      styles: {
        ...inputStyleFields,
        pt: 'xs',
        pb: 'lg',
      },
    },
  ];

  const companyAddressFields = [
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'companyName',
      id: 'businessCompanyName',
      label: t('booking.addressCompanyName'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-CompanyName'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // company name
      },
      hidden: isGermanHotel && isMultiRoomRedesignEnabled,
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine1',
      id: 'businessAddressLine1',
      label: t('booking.addressAddress1'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-AddressLine1'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // company address line 1
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine2',
      id: 'businessAddressLine2',
      label: t('booking.addressAddress2'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-AddressLine2'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // company address line 2
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine3',
      id: 'businessAddressLine3',
      label: t('booking.addressAddress3'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-AddressLine3'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // company address line 3
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine4',
      id: 'businessAddressLine4',
      label: t('booking.addressAddress4'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-AddressLine4'),
      styles: inputStyleFields,
      hidden: isLocationRequired,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // company address line 4
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'postalCode',
      id: 'businessPostalCode',
      label: t('booking.addressPostCode'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-PostalCode'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // company postal code
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'cityName',
      id: 'businessAddressCityName',
      label: t('booking.addressCity'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-CityName'),
      styles: inputStyleFields,
      hidden: !isLocationRequired,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // company city name
      },
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'countryCode',
      props: countryProps,
      Component: CountriesDropdown,
      label: t('booking.country'),
      testid: formatDataTestId(baseDataTestId, 'CountrySelector'),
      styles: {
        ...inputStyleFields,
        pt: 'xs',
      },
    },
  ];

  const billingCompanyAddressFields = [
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'billing_companyName',
      id: 'billingBusinessCompanyName',
      label: t('booking.addressCompanyName'),
      testid: formatDataTestId(baseDataTestId, 'BillingAddress-CompanyAddress-CompanyName'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // billing company name
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'billing_addressLine1',
      id: 'billingBusinessAddressLine1',
      label: t('booking.addressAddress1'),
      testid: formatDataTestId(baseDataTestId, 'BillingAddress-CompanyAddress-AddressLine1'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // billing company address line 1
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'billing_addressLine2',
      id: 'billingBusinessAddressLine2',
      label: t('booking.addressAddress2'),
      testid: formatDataTestId(baseDataTestId, 'BillingAddress-CompanyAddress-AddressLine2'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // billing company address line 2
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'billing_addressLine3',
      id: 'billingBusinessAddressLine3',
      label: t('booking.addressAddress3'),
      testid: formatDataTestId(baseDataTestId, 'BillingAddress-CompanyAddress-AddressLine3'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // billing company address line 3
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'billing_addressLine4',
      id: 'billingBusinessAddressLine4',
      label: t('booking.addressAddress4'),
      testid: formatDataTestId(baseDataTestId, 'BillingAddress-CompanyAddress-AddressLine4'),
      styles: inputStyleFields,
      hidden: isLocationRequired,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // billing company address line 4
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'billing_postalCode',
      id: 'billingBusinessPostalCode',
      label: t('booking.addressPostCode'),
      testid: formatDataTestId(baseDataTestId, 'BillingAddress-CompanyAddress-PostalCode'),
      styles: inputStyleFields,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
        styles: inputFieldTypographyStyles, // billing company postal code
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'billing_cityName',
      id: 'billingBusinessAddressCityName',
      label: t('booking.addressCity'),
      testid: formatDataTestId(baseDataTestId, 'BillingAddress-CompanyAddress-CityName'),
      styles: inputStyleFields,
      hidden: !isLocationRequired,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
      },
    },
    {
      label: t('booking.country'),
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'billing_countryCode',
      props: countryProps,
      Component: CountriesDropdown,
      testid: formatDataTestId(baseDataTestId, 'CountrySelector'),
      styles: {
        ...inputStyleFields,
        pt: 'xs',
      },
    },
  ];

  const addressSelectionConfig = {
    manualAddress: [
      {
        type: FORM_FIELD_TYPES.RADIO_GROUP,
        name: 'addressSelection',
        label: 'Details',
        options: [
          {
            value: 'HOME',
            label: t('booking.privateAddress'),
            testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress'),
          },
          {
            value: 'BUSINESS',
            label: t('booking.businessAddress'),
            testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress'),
          },
        ],
        testid: formatDataTestId(baseDataTestId, 'AddressSelection'),
        styles:
          isGermanHotel && isMultiRoomRedesignEnabled
            ? { ...inputHiddenStyle, mt: 0 }
            : { ...inputStyleFields, mt: 0, pb: '4' },
        relatedFields: {
          HOME: homeAddressFields,
          BUSINESS:
            isGermanHotel && isMultiRoomRedesignEnabled ? homeAddressFields : companyAddressFields,
        },
      },
    ],
  };

  const billingAddressSelectionConfig = {
    billingAddress: [
      {
        type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
        name: 'billingAddressNotification',
        hidden: !isBillingAddressEnabled,
        content: (
          <Box
            {...inputStyleFields}
            data-testid={formatDataTestId(baseDataTestId, 'billingAddressNotification')}
          >
            <Notification
              {...inputStyle}
              description={t('booking.guestDetails.invoiceMsg')}
              variant="infoGrey"
              status="info"
              svg={<Info />}
              isInnerHTML
            />
          </Box>
        ),
      },
      {
        type: FORM_FIELD_TYPES.RADIO_GROUP,
        name: 'billing_addressSelection',
        label: 'Details',
        options: [
          {
            value: 'HOME',
            label: t('booking.privateAddress'),
            testid: formatDataTestId(baseDataTestId, 'BillingAddress-PersonalAddress'),
          },
          {
            value: 'BUSINESS',
            label: t('booking.businessAddress'),
            testid: formatDataTestId(baseDataTestId, 'BillingAddress-CompanyAddress'),
          },
        ],
        testid: formatDataTestId(baseDataTestId, 'BillingAddress'),
        styles: { ...inputStyleFields, mt: 0, pb: '8', pt: 'xs' },
        relatedFields: {
          HOME: billingHomeAddressFields,
          BUSINESS: billingCompanyAddressFields,
        },
      },
    ],
  };

  // De hotel alternative address config - to set Home address as default and fields opened

  const differentBillingAddress = [
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'billing_postcodeAddress',
      label: t('booking.addressPostCode'),
      Component: PostcodeAddress,
      styles: { ...inputStyleFields },
      testid: formatDataTestId(baseDataTestId, 'BillingPostcodeAddress'),
      hidden: currentLang === 'de',
      props: {
        showIcon: false,
        fieldName: 'billing_',
        styles: inputFieldTypographyStyles, // billing postcode address
        buttonLabelTypography: postcodeAddressButtonTypographyStyles,
        dropdownStyles: postcodeAddressDropdownTypographyStyles,
      },
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'billing_manualAddressToggle',
      label: '',
      Component: ManualAddressToggle,
      props: {
        fieldName: 'billingAddress',
      },
      styles: { pb: currentLang === 'de' ? 'sm' : 0, ...inputStyleFields },
      relatedFields: billingAddressSelectionConfig,
      testid: formatDataTestId(baseDataTestId, 'BillingManualAddressToggle'),
    },
  ];

  const config = {
    id: 'guestDetailsForm',
    elements: {
      fields: [
        {
          type: FORM_FIELD_TYPES.INPUT_EMAIL,
          hidden: !isRegisterSelected,
          name: 'email',
          label: t('booking.contactDetails.email'),
          testid: formatDataTestId(baseDataTestId, 'Email'),
          props: {
            showIcon: false,
            className: 'sessioncamhidetext',
            styles: inputFieldTypographyStyles, // register contact email
          },
          styles: registerInputStyle,
        },
        {
          type: FORM_FIELD_TYPES.INPUT_PASSWORD,
          name: 'password',
          hidden: !isRegisterSelected,
          label: t('booking.login.labelPwdPlcHolder'),
          testid: formatDataTestId(baseDataTestId, 'Password'),
          props: {
            showIcon: false,
            className: 'sessioncamhidetext',
            styles: inputFieldTypographyStyles, // register password field
          },
          styles: registerInputStyle,
        },
        {
          type: FORM_FIELD_TYPES.INPUT_PASSWORD,
          hidden: !isRegisterSelected,
          name: 'confirmPassword',
          label: t('booking.login.labelPwdConfirmPlcHolder'),
          testid: formatDataTestId(baseDataTestId, 'ConfirmPassword'),
          props: {
            showIcon: false,
            className: 'sessioncamhidetext',
            styles: inputFieldTypographyStyles, // register confirm password
          },
          styles: registerInputStyle,
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'userProfile',
          label: 'userProfile',
          Component: UserProfile,
          props: {
            basketReferenceId,
            updateReasonForStay,
            isGermanHotel,
            isMultiRoomRedesignEnabled,
            setIsLocationRequired,
            setCountryOfResidence,
            currentLang,
            isRemovePIIDataFromLocalStorageEnabled,
            defaultValues,
          },
          styles: { m: 0 },
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'anonRfs',
          label: 'anonRfs',
          Component: AnonRFS,
          props: {
            basketReferenceId,
            updateReasonForStay,
            hotelBrand,
          },
          styles: { m: 0 },
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'cityTaxExcempt',
          hidden: !cityTaxMessages.mainBanner.length,
          content: (
            <Box mb="3xl" data-testid={formatDataTestId(baseDataTestId, 'CityTax-MainBanner')}>
              <Notification
                description={cityTaxMessages.mainBanner}
                maxWidth="full"
                variant="infoGrey"
                status="info"
                svg={<Info />}
                isInnerHTML
              />
            </Box>
          ),
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'reasonForStayTitle',
          content: (
            <Text
              data-testid={formatDataTestId(baseDataTestId, 'ReasonForStay-Title')}
              {...textColorStyles}
              {...getTypographyProps(heading1LegacyTypography, headingMSemanticTypography)}
            >
              {t('booking.detailsMessage')}
            </Text>
          ),
        },
        {
          type: FORM_FIELD_TYPES.RADIO_GROUP,
          name: 'reasonForStay',
          options: [
            {
              value: 'LEI',
              label: t('booking.reason.leisure'),
              testid: formatDataTestId(baseDataTestId, 'ReasonForStay-Leisure'),
              isChecked: brand === HotelBrand.HUB,
            },
            {
              value: 'BUS',
              label: t('booking.reason.business'),
              testid: formatDataTestId(baseDataTestId, 'ReasonForStay-Business'),
            },
          ],
          testid: formatDataTestId(baseDataTestId, 'ReasonForStay'),
          styles: { ...singleBookingReasonForStayRedesignStyle, mt: 'xl' },
          onChange: (value: string) => onChangeBookingReason(value, updateReasonForStay),
          props: {
            errorLocation: FORM_VALIDATIONS.FORM_FIELD_ERROR_LOCATION.ON_TOP,
          },
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'costWithCityTax',
          hidden: !cityTaxMessages.secondaryBanner.length,
          content: (
            <Box mb="3xl" data-testid={formatDataTestId(baseDataTestId, 'CityTax-SecondaryBanner')}>
              <Notification
                description={cityTaxMessages.secondaryBanner}
                maxWidth="full"
                variant="infoGrey"
                status="info"
                svg={<Info />}
                isInnerHTML
              />
            </Box>
          ),
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'personalDetails',
          content: (
            <Box>
              <Text
                mt="xl"
                {...textColorStyles}
                {...getTypographyProps(subHeadingLegacyTypography, headingMSemanticTypography)}
              >
                {t('booking.detailsTitle')}
              </Text>
            </Box>
          ),
        },
        {
          type: FORM_FIELD_TYPES.RADIO_GROUP,
          name: 'whoBookerIsTabs',
          hidden: !isSingleRoomRedesignEnabled || !isSingleRoomBooking,
          options: [
            {
              value: 'MYSELF',
              label: t('booking.guestDetails.iAmBookingForMyself'),
              testid: formatDataTestId(baseDataTestId, 'whoBookerIsTabs-Myself'),
            },
            {
              value: 'SOMEONEELSE',
              label: t('booking.guestDetails.iAmBookingForSomeoneElse'),
              testid: formatDataTestId(baseDataTestId, 'whoBookerIsTabs-Someoneelse'),
            },
          ],
          testid: formatDataTestId(baseDataTestId, 'whoBookerIsTabs'),
          styles: { ...mockTabsStyle, mt: 'md' },
          onChange: (value: string) => {
            if (value === 'MYSELF') {
              setIsBookingForSomeoneElse(false);
            }
            if (value === 'SOMEONEELSE') {
              setIsBookingForSomeoneElse(true);
            }
          },
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'DataNotification ContactPreference',
          Component: Notice,
          styles: { ...inputStyleFields, pt: 'lg', pb: '0' },
          hidden: isDataNotificationHidden(),
          props: {
            description: t('booking.header.privacyPolicy.message'),
          },
          testid: formatDataTestId(baseDataTestId, 'DataNotification'),
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'bookerDetails',
          hidden: !isSingleRoomRedesignEnabled || !isSingleRoomBooking,
          testid: formatDataTestId(baseDataTestId, 'singleBookingDetailsHeading'),
          content: (
            <Box {...inputStyleFields} pb="0" pt="md">
              <Text
                {...subHeadingLayoutStyles}
                {...getTypographyProps(subHeadingLegacyTypography, headingMSemanticTypography)}
                data-testid={formatDataTestId(
                  baseDataTestId,
                  isBookingForSomeoneElse
                    ? 'singleBookingBookerDetailsHeading'
                    : 'singleBookingGuestDetailsHeading'
                )}
              >
                {isBookingForSomeoneElse
                  ? t('booking.guestDetails.personDetails')
                  : t('booking.contactDetails.guestDetails')}
              </Text>
            </Box>
          ),
        },
        {
          type: FORM_FIELD_TYPES.DROPDOWN,
          name: 'title',
          dropdownOptions: guestTitleOptions,
          testid: formatDataTestId(baseDataTestId, 'Title'),
          styles: { pt: 'lg', ...inputStyleFields, ...titleDropdownStyle, ...noBorderOrPadding },
          props: {
            showStatusIcon: false,
            placeholder: t('booking.contactDetails.title'),
            dropdownStyles: {
              menuButtonTextStyles: getTypographyProps({}, bodyMRegularSemanticTypography),
              menuItemTextStyles: getTypographyProps({}, bodySRegularSemanticTypography),
            },
          },
        },
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          name: 'firstName',
          label: t('booking.contactDetails.name'),
          testid: formatDataTestId(baseDataTestId, 'FirstName'),
          props: {
            showIcon: false,
            className: 'sessioncamhidetext',
            styles: inputFieldTypographyStyles, // single room first name
          },
          styles: inputStyleFields,
        },
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          name: 'lastName',
          label: t('booking.contactDetails.surname'),
          testid: formatDataTestId(baseDataTestId, 'LastName'),
          props: {
            showIcon: false,
            className: 'sessioncamhidetext',
            styles: inputFieldTypographyStyles, // single room last name
          },
          styles: { ...inputStyleFields },
        },
        {
          type: FORM_FIELD_TYPES.INPUT_EMAIL,
          hidden: isRegisterSelected,
          name: 'email',
          label: t('booking.contactDetails.email'),
          testid: formatDataTestId(baseDataTestId, 'Email'),
          props: {
            showIcon: false,
            className: 'sessioncamhidetext',
            styles: inputFieldTypographyStyles, // single room email
          },
          styles: { ...inputStyleFields },
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'phone',
          label: isConsolidateMobileLandlineEnabled
            ? t('groupBooking.contactDetails.yourContact.phoneNumber')
            : t('booking.contactDetails.mobile'),
          Component: PhoneSelector,
          styles: { ...inputStyleFields },
          props: {
            showIcon: false,
            currentLang,
            className: 'sessioncamhidetext',
            styles: inputFieldTypographyStyles, // single room mobile
          },
          testid: formatDataTestId(baseDataTestId, 'Mobile'),
          ...(!isConsolidateMobileLandlineEnabled ? { dependantOn: 'landline' } : {}),
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'landline',
          label: t('booking.contactDetails.telephone'),
          dependantOn: 'phone',
          Component: PhoneSelector,
          styles: { ...inputStyleFields, pb: '2xl' },
          props: {
            showIcon: false,
            currentLang,
            className: 'sessioncamhidetext',
            styles: inputFieldTypographyStyles, // single room landline
          },
          testid: formatDataTestId(baseDataTestId, 'Landline'),
          hidden: isConsolidateMobileLandlineEnabled,
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'addressTitle',
          content: (
            <Box {...inputStyleFields} {...(!isSingleRoomBooking && { pb: 'md' })}>
              <Text
                {...subHeadingLayoutStyles}
                {...getTypographyProps(subHeadingLegacyTypography, headingMSemanticTypography)}
              >
                {t('booking.addressTitle')}
              </Text>
            </Box>
          ),
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'DataNotification AccountManager',
          Component: Notice,
          styles: { ...inputStyleFields, pt: '0', pb: '0' },
          hidden: isDataNotificationHidden(),
          props: {
            description: t('booking.header.accountManager.message'),
          },
          testid: formatDataTestId(baseDataTestId, 'DataNotification'),
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'postcodeAddress',
          label: t('booking.addressPostCode'),
          Component: PostcodeAddress,
          styles: { ...inputStyleFields, pb: '0', marginBottom: 0 },
          testid: formatDataTestId(baseDataTestId, 'PostcodeAddress'),
          hidden: currentLang === 'de',
          props: {
            showIcon: false,
            styles: inputFieldTypographyStyles, // contact details postcode
            buttonLabelTypography: postcodeAddressButtonTypographyStyles,
            dropdownStyles: postcodeAddressDropdownTypographyStyles,
          },
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'manualAddressToggle',
          label: '',
          Component: ManualAddressToggle,
          relatedFields: addressSelectionConfig,
          props: {
            fieldName: 'manualAddress',
          },
          testid: formatDataTestId(baseDataTestId, 'ManualAddressToggle'),
          styles: { ...inputStyleFields },
        },
        {
          type: FORM_FIELD_TYPES.CHECKBOX,
          name: 'billingAddressCheckbox',
          label: t('booking.guestDetails.differentBillingAddress'),
          styles: {
            ...inputStyleFields,
            mb: '0',
          },
          testid: formatDataTestId(baseDataTestId, 'BillingAddressCheckbox'),
          hidden: !isBillingAddressEnabled,
          relatedFields: {
            true:
              currentLang === 'de'
                ? billingAddressSelectionConfig?.billingAddress
                : differentBillingAddress,
          },
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'billingAddressNotification',
          hidden:
            (isDifferentBillingAddress && isBillingAddressEnabled) || !isBillingAddressEnabled,
          content: (
            <Box
              {...inputStyleFields}
              data-testid={formatDataTestId(baseDataTestId, 'billingAddressNotification')}
            >
              <Notification
                {...inputStyle}
                description={t('booking.guestDetails.invoiceMsg')}
                variant="infoGrey"
                status="info"
                svg={<Info />}
                isInnerHTML
              />
            </Box>
          ),
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'additionalInformation',
          Component: AdditionalInformation,
          styles: { ...inputStyleFields, pt: 'xl' },
          hidden:
            !isSingleRoomBooking || !isAdditionalInformationEnabled || isBookingForSomeoneElse,
          testid: formatDataTestId(baseDataTestId, 'AdditionalInformation'),
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'guestDetails',
          hidden:
            (!isBookingForSomeoneElse && isSingleRoomRedesignEnabled) ||
            !isSingleRoomBooking ||
            !isSingleRoomRedesignEnabled,
          content: (
            <Box
              {...inputStyleFields}
              data-testid={formatDataTestId(baseDataTestId, 'guestDetailsSomeoneElseHeading')}
            >
              <Text
                mt="xl"
                {...subHeadingLayoutStyles}
                {...getTypographyProps(subHeadingLegacyTypography, headingMSemanticTypography)}
              >
                {t('booking.contactDetails.guestDetails')}
              </Text>
            </Box>
          ),
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          hidden:
            !isSingleRoomRedesignEnabled ||
            (isSingleRoomRedesignEnabled && isBookingForSomeoneElse && isSingleRoomBooking),
          name: 'yourDetailsBottomBorder',
          content: (
            <Box
              mb={shouldAskForAccompanyingGuest ? 0 : 1}
              {...(isSingleRoomBooking && { ...singleBookingStyleBorders })}
              {...(isSingleRoomBooking &&
                isSingleRoomRedesignEnabled &&
                !shouldAskForAccompanyingGuest && { ...singleBookingStyleBorders })}
            ></Box>
          ),
        },
        {
          type: FORM_FIELD_TYPES.CHECKBOX,
          name: 'updateProfileConsent',
          label: t('booking.saveDetailsCheckboxLabel'),
          testid: formatDataTestId(baseDataTestId, 'UpdateProfileConsent'),
          styles: {
            ...inputStyleFields,
            borderLeft: '0',
            borderRight: '0',
            mt: 'xl',
            mb: '0',
          },
          hidden: !isUpdateProfileConsentVisible,
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'updateProfileConsentNotice',
          styles: {
            ...inputStyleFields,
            borderLeft: '0',
            borderRight: '0',
            pt: 'xs',
          },
          content: (
            <Box
              data-testid={formatDataTestId(baseDataTestId, 'UpdateProfileConsentNotice')}
              display="flex"
              alignItems="center"
              gap="var(--chakra-space-xmd)"
            >
              <Box
                data-testid={formatDataTestId(baseDataTestId, 'UpdateProfileConsentNotice-Icon')}
                display="inline-flex"
                alignItems="center"
                justifyContent="center"
                flexShrink={0}
              >
                <Box
                  as="img"
                  src={resolvedPrivacyPolicyIconPath}
                  alt=""
                  width="4"
                  height="4"
                  objectFit="contain"
                  objectPosition="center"
                  display="block"
                />
              </Box>
              {privacyPolicyLinkPath ? (
                <Link
                  href={privacyPolicyLinkPath}
                  target="_blank"
                  rel="noopener noreferrer"
                  title={t('booking.privacyPolicyLinkUrl')}
                  color="btnSecondaryEnabled"
                  display="inline-flex"
                  alignItems="center"
                  lineHeight="1"
                  fontSize="small"
                  textDecoration="underline"
                >
                  {t('booking.privacyPolicyLinkLabel')}
                </Link>
              ) : (
                <Text
                  color="btnSecondaryEnabled"
                  fontSize="small"
                  display="inline-flex"
                  alignItems="center"
                  lineHeight="1"
                >
                  {t('booking.privacyPolicyLinkLabel')}
                </Text>
              )}
            </Box>
          ),
          testid: formatDataTestId(baseDataTestId, 'UpdateProfileConsentNotice'),
          hidden: !isUpdateProfileConsentVisible,
        },
        // START OF LeadGuest blocks
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          dropdownOptions: guestTitleOptions,
          name: 'leadGuest',
          bkndData: bkndData,
          testid: formatDataTestId(baseDataTestId, 'leadGuest'),
          Component: LeadGuestDetails,
          styles: {
            pt:
              isBookingForSomeoneElse ||
              (isSingleRoomBooking && isSingleRoomRedesignEnabled && shouldAskForAccompanyingGuest)
                ? '0'
                : '5',
            mb: isAdditionalInformationEnabled ? 0 : 'md',
          },
          props: {
            showIcon: false,
            area: Area.PI,
            currentLang,
            setIsLocationRequired,
            isLocationRequired,
            isBookingForSomeoneElse,
            isGermanHotel,
            isSingleRoomRedesignEnabled,
            isMultiRoomRedesignEnabled,
            isAdditionalInformationEnabled,
            showCheckInInfo,
            setShowCheckInInfo,
            shouldAskForAccompanyingGuest,
          },
        },
        // END OF LeadGuest blocks
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'acceptFutureMailing',
          Component: EmailWrapper,
          styles: {
            mt: '3xl',
          },
          props: {
            currentLang,
            isDEOptInEnabled,
            defaultValues,
            isRemovePIIDataFromLocalStorageEnabled,
          },
          testid: formatDataTestId(baseDataTestId, 'AcceptFutureMailing'),
          hidden: suppressMarketingCheckbox,
        },
      ],
      buttons: [
        {
          type: FORM_BUTTON_TYPES.SUBMIT,
          label: t('booking.summary.continue'),
          action: onSubmit,
          props: {
            variant: 'primary',
            size: 'full',
            isDisabled: submitButtonDisabled,
            ...continueButtonTypographyStyles,
          },
          styles: {
            ...continueTextStyle,
            ...continueButtonSectionStyle,
          },
          testid: formatDataTestId(baseDataTestId, 'Submit'),
        },
      ],
      bottomFields: [
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'backButton',
          label: 'backButton',
          Component: BackButton,
          props: {
            basketReferenceId,
            goBack,
            defaultValues,
            isRemovePIIDataFromLocalStorageEnabled,
          },
          styles: {
            mb: 0,
          },
        },
      ],
    },
    errorsOrder,
    defaultValues,
    validationSchema: formValidationSchema,
    getFormState,
    resetForm,
  } as FormProps;

  return config;
};

const onChangeBookingReason = (
  value: string,
  updateReasonForStay: (reasonForStay: string) => void
) => {
  if (value === 'LEI') {
    analytics.update({ bookingReasonForStay: 'leisure' });
    updateReasonForStay(value);
  }
  if (value === 'BUS') {
    analytics.update({ bookingReasonForStay: 'business' });
    updateReasonForStay(value);
  }
};

const getIsDataNotificationHidden = (currentLang: string | undefined, brand: string) => {
  if (currentLang === 'de' && brand !== 'PID') {
    return false;
  }
  return true;
};

const continueButtonSectionStyle = {
  width: { mobile: 'full', md: '72' },
  mb: 'sm',
} as BoxProps;

const continueTextStyle = {
  color: 'baseWhite',
} as TextProps;

const continueButtonLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '3',
} as TextProps;

const labelXlSemanticTypography = {
  textStyle: 'label-xl',
} as TextProps;

const bodyMRegularSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;

const postcodeAddressDropdownItemLegacyTypography = {
  fontSize: 'sm',
  lineHeight: '1',
} as TextProps;

const bodySRegularSemanticTypography = {
  textStyle: 'body-s-regular',
} as TextProps;

const inputStyle = {
  maxW: {
    mobile: '100%',
    sm: '16.375rem',
    md: '21.75rem',
    lg: '24.5rem',
    xl: '26.25rem',
  },
};

const inputHiddenStyle = {
  display: 'none',
};

const singleBookingStyleBorders = {
  maxW: {
    md: '100%',
    xl: '55rem',
  },
  borderBottom: '1px solid var(--chakra-colors-lightGrey2)',
};

const singleBookingStyle = {
  maxW: {
    md: '100%',
    xl: '55rem',
  },
  sx: {
    '.chakra-stack': {
      flexDirection: {
        mobile: 'column',
        sm: 'row',
      },
      '> div': {
        flex: '1',
        borderRadius: '0',
        ':first-of-type': {
          borderRadius: 'var(--chakra-space-1) 0 0 var(--chakra-space-1)',
        },
        ':last-child': {
          borderRadius: '0 var(--chakra-space-1) var(--chakra-space-1) 0',
        },
      },
    },
  },
};

const mockTabsStyle = {
  maxW: {
    md: '100%',
    xl: '55rem',
  },
  mt: '0',
  mb: '0',
  sx: {
    '.chakra-radio__control': {
      opacity: '0',
      height: '0',
      width: '0',
      position: 'fixed',
    },
    p: {
      fontSize: 'lg',
    },
    '.chakra-stack': {
      flexDirection: 'row',
      '> div': {
        flex: '1',
        borderRadius: '0',
        padding: '0',
        borderBottom: '0',
        borderColor: 'var(--chakra-colors-lightGrey2)',
        borderWidth: '1px',
        display: 'flex',
      },
      '.chakra-radio': {
        padding: 'var(--chakra-space-4)',
        backgroundColor: 'var(--chakra-colors-lightGrey3)',
        width: 'var(--chakra-sizes-full)',
        justifyContent: 'center',
        color: 'var(--chakra-colors-darkGrey2)',
        fontWeight: '400',
      },
      '[data-checked]': {
        backgroundColor: 'var(--chakra-colors-baseWhite)',
        '> p': {
          color: 'var(--chakra-colors-darkGrey1)',
          fontWeight: '400',
        },
      },
    },
  },
};

const singleBookingStyleFields = {
  paddingLeft: '5',
  paddingRight: '5',
  marginBottom: '0',
  paddingBottom: 'var(--chakra-space-md)',
  borderLeft: '1px solid var(--chakra-colors-lightGrey2)',
  borderRight: '1px solid var(--chakra-colors-lightGrey2)',
  maxW: {
    md: '100%',
    xl: '55rem',
  },
  sx: {
    '.chakra-input__group, .chakra-button, .chakra-radio-group, .chakra-menu__menu-button': {
      maxW: {
        mobile: '100%',
        sm: '16.375rem',
        md: '21.75rem',
        lg: '24.5rem',
        xl: '26.25rem',
      },
    },
  },
};

const noBordersPadding = {
  paddingLeft: '0',
  paddingRight: '0',
  borderLeft: '0',
  borderRight: '0',
};

const titleDropdownStyle = {
  sx: {
    '.chakra-menu__menu-button': { maxW: '8.5rem' },
  },
};

const registerInputStyle = {
  maxW: {
    md: '20.25rem',
    lg: '23.25rem',
    xl: '24.84rem',
  },
};

const textColorStyles = {
  color: 'darkGrey1',
} as TextProps;

const headingMSemanticTypography = {
  textStyle: 'heading-m',
} as TextProps;

const linkStyles = {
  color: 'btnSecondaryEnabled',
  textDecoration: 'underline',
};

const manualAddressLinkSemanticTypography = {
  textStyle: 'link-m-regular',
} as TextProps;

const heading2LegacyTypography = {
  fontSize: 'xl',
  fontWeight: '600',
  lineHeight: '3',
} as TextProps;

const heading1LegacyTypography = {
  fontSize: '2xl',
  fontWeight: 'semibold',
  lineHeight: '4',
} as TextProps;
