import { Box, type BoxProps, type TextProps, Link, Text } from '@chakra-ui/react';
import { HotelBrand, backendDataType, Area, FS_DISPLAY_GUEST_ACCOUNT } from '@whitbread-eos/api';
import type { AddressGuestInput, CompanyProfile } from '@whitbread-eos/api';
import {
  Button,
  FORM_BUTTON_TYPES,
  FORM_FIELD_TYPES,
  FORM_VALIDATIONS,
  FormDynamicFieldCompProps,
  FormProps,
  Info,
  Notification,
} from '@whitbread-eos/atoms';
import {
  AnonRFS,
  GuestDetailsBackButton as BackButton,
  CountriesDropdown,
  LeadGuestDetails,
  PhoneSelector,
  PostcodeAddress,
  CompanyBillingProfile,
  AdditionalInformation,
} from '@whitbread-eos/molecules';
import {
  analytics,
  formatDataTestId,
  formatGuestTitleOptions,
  useFeatureSwitch as isFeatureFlagEnabled,
  GLOBALS,
} from '@whitbread-eos/utils';
import { Dispatch, SetStateAction, useEffect } from 'react';

import validateForm from './formValidation';

export interface GDPersonalDetails extends AddressGuestInput {
  title: string;
  firstName: string;
  lastName: string;
  email?: string;
  phone: string;
  landline: string;
}

interface GuestDetailsFormConfigArgsType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: (data: GDPersonalDetails) => void;
  baseDataTestId: string;
  t: (id: string) => string;
  currentLang: string | undefined;
  currentCountry: string | undefined;
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
  isSingleRoomRedesignEnabled: boolean;
  isMultiRoomRedesignEnabled: boolean;
  isBillingAddressEnabled: boolean;
  isBookingForSomeoneElse: boolean;
  setIsBookingForSomeoneElse: Dispatch<SetStateAction<boolean>>;
  isGermanHotel?: boolean;
  companyProfile?: CompanyProfile;
  isAdditionalInformationEnabled?: boolean;
  showCheckInInfo: { [key: string]: boolean };
  setShowCheckInInfo: Dispatch<SetStateAction<{ [key: string]: boolean }>>;
  shouldAskForAccompanyingGuest?: boolean;
  isDifferentBillingAddress?: boolean;
  isCompanyNameAdvanceEnabled?: boolean;
  submitButtonDisabled?: boolean;
  isConsolidateMobileLandlineEnabled?: boolean;
  isCountryAllowTypingEnabled?: boolean;
  isRemovePIIDataFromLocalStorageEnabled?: boolean;
}

declare module 'yup' {
  interface ArraySchema<T> {
    unique(a: string, message: string): ArraySchema<T>;
  }
}

export const guestDetailsFormConfig = ({
  getFormState,
  defaultValues,
  onSubmit,
  baseDataTestId,
  t,
  currentLang,
  currentCountry,
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
  isGermanHotel,
  isBillingAddressEnabled,
  isSingleRoomRedesignEnabled,
  isBookingForSomeoneElse,
  setIsBookingForSomeoneElse,
  isMultiRoomRedesignEnabled,
  companyProfile,
  isAdditionalInformationEnabled,
  showCheckInInfo,
  setShowCheckInInfo,
  shouldAskForAccompanyingGuest = false,
  isDifferentBillingAddress,
  isCompanyNameAdvanceEnabled,
  submitButtonDisabled,
  isConsolidateMobileLandlineEnabled,
  isCountryAllowTypingEnabled,
  isRemovePIIDataFromLocalStorageEnabled,
}: GuestDetailsFormConfigArgsType) => {
  const { formValidationSchema } = validateForm({
    t,
    bkndData,
    currentLang,
    isSingleRoomRedesignEnabled,
    isMultiRoomRedesignEnabled,
    isGermanHotel,
    isBookingForSomeoneElse,
    isCompanyNameAdvanceEnabled,
    isConsolidateMobileLandlineEnabled,
  });
  const guestTitleOptions = formatGuestTitleOptions(t('common.nameTitles'));
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
    'billing_addressSelection',
    'billing_companyName',
    'billing_addressLine1',
    'billing_postalCode',
    'billing_cityName',
  ];

  const isGuestAccountEnabled = isFeatureFlagEnabled({
    featureSwitchKey: FS_DISPLAY_GUEST_ACCOUNT,
    fallbackValue: false,
  });

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

    return (
      <Link {...linkStyles} onClick={() => enterManualAddress()}>
        {t('booking.enterManuallAddress')}
      </Link>
    );
  };

  const isSingleRoomBooking = bkndData.rooms.length === 1 ? true : false;

  const singleBookingReasonForStayRedesignStyle = isSingleRoomRedesignEnabled
    ? singleBookingStyle
    : inputStyle;

  const searchForBtnStyles =
    isSingleRoomRedesignEnabled || isMultiRoomRedesignEnabled
      ? searchForButtonRedesignStyles
      : searchForButtonStyles;

  const inputStyleFields =
    isSingleRoomRedesignEnabled && isSingleRoomBooking ? singleBookingStyleFields : inputStyle;

  const multiRoomHeading =
    !isSingleRoomBooking && isSingleRoomRedesignEnabled ? heading2 : heading1;

  const subHeading =
    isSingleRoomRedesignEnabled || isMultiRoomRedesignEnabled || isBillingAddressEnabled
      ? heading2
      : titleStyle;

  const countryProps = {
    setIsLocationRequired,
    isCountrySelectorFilterableEnabled: isCountryAllowTypingEnabled,
    showIcon: false,
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
      },
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'countryCode',
      id: 'homeCountryCode',
      Component: CountriesDropdown,
      testid: formatDataTestId(baseDataTestId, 'CountrySelector'),
      props: countryProps,
      styles: {
        ...inputStyleFields,
        pt: 'xs',
        pb: 'lg',
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
      },
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      label: t('booking.country'),
      name: 'billing_countryCode',
      Component: CountriesDropdown,
      props: countryProps,
      testid: formatDataTestId(baseDataTestId, 'CountrySelector'),
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
      },
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'countryCode',
      label: t('booking.country'),
      id: 'businessCountryCode',
      props: countryProps,
      Component: CountriesDropdown,
      testid: formatDataTestId(baseDataTestId, 'CountrySelector'),
      styles: {
        ...inputStyleFields,
        mt: 'xs',
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
            : { ...inputStyleFields, mt: 0, pb: '8' },
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
      props: { showIcon: false, fieldName: 'billing_' },
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

  const searchForBookingsRedirect = () => {
    window.location.href = `/${currentCountry}/${currentLang}/bookings?reservationId=${basketReferenceId}`;
  };

  const reuseDetailsRedirect = () => {
    window.location.href = `/${currentCountry}/${currentLang}/search-account?reservationId=${basketReferenceId}`;
  };

  const config = {
    id: 'guestDetailsForm',
    elements: {
      fields: [
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
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'reasonForStayTitle',
          content: (
            <Text
              data-testid={formatDataTestId(baseDataTestId, 'ReasonForStay-Title')}
              {...leisureOrBusinessStyle}
            >
              {t('ccui.booking.detailsMessage')}
            </Text>
          ),
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'cityTaxExcempt',
          hidden: !cityTaxMessages.mainBanner.length,
          content: (
            <Box mt="md" data-testid={formatDataTestId(baseDataTestId, 'CityTax-MainBanner')}>
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
          onChange: (value: string) => {
            if (value === 'LEI') {
              analytics.update({ bookingReasonForStay: 'leisure' });
              updateReasonForStay(value);
            }
            if (value === 'BUS') {
              analytics.update({ bookingReasonForStay: 'business' });
              updateReasonForStay(value);
            }
          },
          props: {
            errorLocation: FORM_VALIDATIONS.FORM_FIELD_ERROR_LOCATION.ON_TOP,
          },
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'costWithCityTax',
          hidden: !cityTaxMessages.secondaryBanner.length,
          content: (
            <Box mt="md" data-testid={formatDataTestId(baseDataTestId, 'CityTax-SecondaryBanner')}>
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
          name: 'searchForBooking',
          content: (
            <Box {...searchForBtnStyles}>
              <Button
                {...inputStyle}
                size="full"
                variant="tertiary"
                onClick={searchForBookingsRedirect}
              >
                {t('ccui.manageBooking.searchBooking')}
              </Button>
              {isGuestAccountEnabled && (
                <Button
                  {...{ ...inputStyle }}
                  size="full"
                  variant="tertiary"
                  onClick={reuseDetailsRedirect}
                >
                  {t('ccui.manageBooking.searchMyPI')}
                </Button>
              )}
            </Box>
          ),
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'personalDetails',
          content: (
            <Box>
              <Text mt="xl" {...multiRoomHeading}>
                {t('ccui.booking.detailsTitle')}
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
              label: t('ccui.booking.details.forThemselves'),
              testid: formatDataTestId(baseDataTestId, 'whoBookerIsTabs-Myself'),
            },
            {
              value: 'SOMEONEELSE',
              label: t('ccui.booking.details.forSomeoneElse'),
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
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'bookerDetails',
          hidden: !isSingleRoomRedesignEnabled || !isSingleRoomBooking,
          testid: formatDataTestId(baseDataTestId, 'singleBookingDetailsHeading'),
          content: (
            <Box {...inputStyleFields} pb="0" pt="md">
              <Text
                {...subHeading}
                data-testid={formatDataTestId(
                  baseDataTestId,
                  isBookingForSomeoneElse
                    ? 'singleBookingBookerDetailsHeading'
                    : 'singleBookingGuestDetailsHeading'
                )}
              >
                {isBookingForSomeoneElse
                  ? t('ccui.booking.detailsTitle')
                  : t('booking.contactDetails.guestDetails')}
              </Text>
            </Box>
          ),
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'addressDetails',
          hidden:
            (!isBookingForSomeoneElse && isSingleRoomRedesignEnabled) ||
            !isSingleRoomBooking ||
            !isSingleRoomRedesignEnabled,
          content: (
            <Box {...inputStyleFields}>
              <Text>{t('booking.guestDetails.address.description')}</Text>
            </Box>
          ),
        },
        {
          type: FORM_FIELD_TYPES.DROPDOWN,
          name: 'title',
          dropdownOptions: guestTitleOptions,
          testid: formatDataTestId(baseDataTestId, 'Title'),
          styles: { pt: 'lg', ...inputStyleFields, ...titleDropdownStyle },
          props: {
            showStatusIcon: false,
            placeholder: t('booking.contactDetails.title'),
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
          },
          styles: inputStyleFields,
        },
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          name: 'email',
          label: t('ccui.booking.contactDetails.email'),
          testid: formatDataTestId(baseDataTestId, 'Email'),
          props: {
            showIcon: false,
            className: 'sessioncamhidetext',
          },
          styles: inputStyleFields,
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'phone',
          label: isConsolidateMobileLandlineEnabled
            ? t('groupBooking.contactDetails.yourContact.phoneNumber')
            : t('booking.contactDetails.mobile'),
          Component: PhoneSelector,
          styles: {
            ...inputStyleFields,
          },
          props: {
            showIcon: false,
            currentLang,
            className: 'sessioncamhidetext',
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
          },
          testid: formatDataTestId(baseDataTestId, 'Landline'),
          hidden: isConsolidateMobileLandlineEnabled,
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'addressTitle',
          content: (
            <Box {...inputStyleFields} {...(!isSingleRoomBooking && { pb: 'md' })}>
              <Text {...subHeading}>
                {isBookingForSomeoneElse
                  ? t('ccui.booking.addressTitle')
                  : t('ccui.booking.guestDetails.address.heading')}
              </Text>
            </Box>
          ),
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'postcodeAddress',
          label: t('booking.addressPostCode'),
          Component: PostcodeAddress,
          styles: { ...inputStyleFields, pb: '0', marginBottom: 0 },
          testid: formatDataTestId(baseDataTestId, 'PostcodeAddress'),
          hidden: currentLang === 'de',
          props: { showIcon: false },
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
          mt: 'xl',
          styles: { pb: currentLang === 'de' ? 'sm' : 0, ...inputStyleFields },
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
            true: GLOBALS.language.DE
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
              <Text {...subHeading} mt="xl">
                {t('booking.contactDetails.guestDetails')}
              </Text>
            </Box>
          ),
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'additionalInformation',
          Component: AdditionalInformation,
          testid: formatDataTestId(baseDataTestId, 'AdditionalInformation'),
          hidden:
            !isSingleRoomBooking || !isAdditionalInformationEnabled || isBookingForSomeoneElse,
          styles: { pt: '3xl', pb: currentLang === 'de' ? 'sm' : 0, ...inputStyleFields },
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
              {...(isSingleRoomBooking &&
                isSingleRoomRedesignEnabled &&
                !shouldAskForAccompanyingGuest && { ...singleBookingStyleBorders })}
            ></Box>
          ),
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
            area: Area.CCUI,
            hotelBrand,
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
          },
          styles: { ...continueTextStyle, ...continueButtonSectionStyle },
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
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'companyBillingProfile',
          label: 'companyBillingProfile',
          Component: CompanyBillingProfile,
          props: {
            companyProfile,
            currentLang,
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

const continueButtonSectionStyle = {
  width: { mobile: 'full', md: '72' },
  mb: 'sm',
} as BoxProps;

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

const titleDropdownStyle = {
  sx: {
    '.chakra-menu__menu-button': { maxW: '8.5rem' },
  },
};

const continueTextStyle = {
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '3',
  color: 'baseWhite',
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

const titleStyle = {
  fontSize: '2xl',
  fontWeight: 'semibold',
  lineHeight: '4',
  color: 'darkGrey1',
  pt: 'lg',
  pb: 'var(--chakra-space-3)',
} as TextProps;

const leisureOrBusinessStyle = {
  fontSize: '2xl',
  fontWeight: 'semibold',
  lineHeight: '4',
  color: 'darkGrey1',
};

const linkStyles = {
  color: 'btnSecondaryEnabled',
  textDecoration: 'underline',
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

const singleBookingStyleBorders = {
  maxW: {
    md: '100%',
    xl: '55rem',
  },
  borderBottom: '1px solid var(--chakra-colors-lightGrey2)',
};

const searchForButtonRedesignStyles = {
  gap: 'lg',
  display: {
    mobile: 'block',
    sm: 'flex',
  },
  mt: { mobile: '4', sm: '5' },
  mb: '5',
  sx: {
    '.chakra-button': {
      mt: { mobile: '5', sm: '0' },
      maxWidth: 'none',
    },
  },
};

const searchForButtonStyles = {
  sx: {
    '.chakra-button': {
      display: 'block',
      ':last-child': {
        mt: 'md',
      },
    },
  },
};

const heading1 = {
  fontSize: '2xl',
  fontWeight: '600',
  lineHeight: '4',
  color: 'darkGrey1',
};

const heading2 = {
  fontSize: 'xl',
  fontWeight: '600',
  lineHeight: '3',
  color: 'darkGrey1',
};
