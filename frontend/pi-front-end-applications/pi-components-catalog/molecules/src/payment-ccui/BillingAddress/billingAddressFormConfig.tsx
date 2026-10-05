import type { TextProps } from '@chakra-ui/react';
import { Flex, Link, Text } from '@chakra-ui/react';
import { BillingAddressFormData, CompanyProfile } from '@whitbread-eos/api';
import { FORM_FIELD_TYPES, FormDynamicFieldCompProps, FormProps } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import { Dispatch, SetStateAction, useEffect } from 'react';

import { CountriesDropdown, PostcodeAddress } from '../../guest-details';
import billingAddressFormValidation from './billingAddressFormValidation';

interface BillingAddressFormConfigArgsType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmitAction: (data: BillingAddressFormData) => void;
  baseDataTestId: string;
  t: (id: string) => string;
  currentLang: string | undefined;
  currentAddressLabel?: string;
  resetForm?: number;
  testid: string;
  isLocationRequired: boolean;
  setIsLocationRequired: Dispatch<SetStateAction<boolean>>;
  companyProfile?: CompanyProfile;
  isCompanyNameAdvanceEnabled?: boolean;
  isCountryAllowTypingEnabled?: boolean;
}

export const billingAddressFormConfig = ({
  getFormState,
  defaultValues,
  onSubmitAction,
  baseDataTestId,
  t,
  currentLang,
  currentAddressLabel,
  resetForm,
  testid,
  isLocationRequired,
  setIsLocationRequired,
  companyProfile,
  isCompanyNameAdvanceEnabled,
  isCountryAllowTypingEnabled,
}: BillingAddressFormConfigArgsType) => {
  const formValidationSchema = billingAddressFormValidation({
    t,
    currentLang,
    isCompanyNameAdvanceEnabled,
  });

  interface CurrentAddressRadioButtonProps {
    isChecked: boolean;
  }

  const CurrentAddressRadioButton = ({ isChecked }: CurrentAddressRadioButtonProps) => {
    return (
      <Flex direction="column">
        <Text
          data-testid={formatDataTestId(baseDataTestId, 'BillingAddress-UseCurrentAdress-Title')}
          {...addressStyle}
          fontWeight={isChecked ? 'semibold' : 'normal'}
        >
          {t('billingAddress.current')}
        </Text>
        {currentAddressLabel && (
          <Text
            data-testid={formatDataTestId(baseDataTestId, 'BillingAddress-UseCurrentAdress-Label')}
            {...addressStyle}
          >
            {currentAddressLabel}
          </Text>
        )}
      </Flex>
    );
  };

  const ManualAddressToggle = ({ field, handleSetValue, errors }: FormDynamicFieldCompProps) => {
    const { name } = field;

    // Trigger automatic expansion of manual address, when no Postal Code is entered
    useEffect(() => {
      if (!handleSetValue) {
        return;
      }
      if (errors?.addressLine1 || companyProfile) {
        handleSetValue(name, 'manualAddress');
      }
    }, [handleSetValue, name, errors]);

    if (field.value === 'manualAddress') {
      return null;
    }

    return (
      <Link
        {...linkStyles}
        onClick={() => {
          if (!handleSetValue) {
            return;
          }
          handleSetValue(name, 'manualAddress');
        }}
      >
        {t('booking.enterManuallAddress')}
      </Link>
    );
  };

  const countryProps = {
    setIsLocationRequired,
    isCountrySelectorFilterableEnabled: isCountryAllowTypingEnabled,
  };

  const homeAddressFields = [
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine1',
      id: 'homeAddressLine1',
      label: t('booking.addressAddress1'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-AddressLine1'),
      styles: inputStyle,
      props: {
        showIcon: true,
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine2',
      id: 'homeAddressLine2',
      label: t('booking.addressAddress2'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-AddressLine2'),
      styles: inputStyle,
      props: {
        showIcon: true,
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine3',
      id: 'homeAddressLine3',
      label: t('booking.addressAddress3'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-AddressLine3'),
      styles: inputStyle,
      props: {
        showIcon: true,
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine4',
      id: 'homeAddressLine4',
      label: t('booking.addressAddress4'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-AddressLine4'),
      styles: inputStyle,
      hidden: isLocationRequired,
      props: {
        showIcon: true,
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'postalCode',
      id: 'homePostalCode',
      label: t('booking.addressPostCode'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-PostalCode'),
      styles: inputStyle,
      props: {
        showIcon: true,
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'cityName',
      id: 'homeAddressCityName',
      label: t('booking.addressCity'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-CityName'),
      styles: inputStyle,
      hidden: !isLocationRequired,
      props: {
        showIcon: true,
      },
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'countryCode',
      label: t('booking.country'),
      id: 'homeCountryCode',
      Component: CountriesDropdown,
      testid: formatDataTestId(baseDataTestId, 'CountrySelector'),
      styles: {
        ...inputStyle,
        mt: 'xs',
      },
      props: countryProps,
    },
  ];

  const companyAddressFields = [
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'companyName',
      id: 'businessCompanyName',
      label: t('booking.addressCompanyName'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-CompanyName'),
      styles: inputStyle,
      props: {
        showIcon: true,
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine1',
      id: 'businessAddressLine1',
      label: t('booking.addressAddress1'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-AddressLine1'),
      styles: inputStyle,
      props: {
        showIcon: true,
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine2',
      id: 'businessAddressLine2',
      label: t('booking.addressAddress2'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-AddressLine2'),
      styles: inputStyle,
      props: {
        showIcon: true,
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine3',
      id: 'businessAddressLine3',
      label: t('booking.addressAddress3'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-AddressLine3'),
      styles: inputStyle,
      props: {
        showIcon: true,
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine4',
      id: 'businessAddressLine4',
      label: t('booking.addressAddress4'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-AddressLine4'),
      styles: inputStyle,
      hidden: isLocationRequired,
      props: {
        showIcon: true,
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'postalCode',
      id: 'businessPostalCode',
      label: t('booking.addressPostCode'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-PostalCode'),
      styles: inputStyle,
      props: {
        showIcon: true,
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'cityName',
      id: 'businessAddressCityName',
      label: t('booking.addressCity'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-CityName'),
      styles: inputStyle,
      hidden: !isLocationRequired,
      props: {
        showIcon: true,
      },
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'countryCode',
      id: 'businessCountryCode',
      label: t('booking.country'),
      Component: CountriesDropdown,
      testid: formatDataTestId(baseDataTestId, 'CountrySelector'),
      styles: {
        ...inputStyle,
        mt: 'xs',
      },
      props: { ...countryProps },
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
            testid: formatDataTestId(baseDataTestId, 'addressSelection-PersonalAddress'),
          },
          {
            value: 'BUSINESS',
            label: t('booking.businessAddress'),
            testid: formatDataTestId(baseDataTestId, 'addressSelection-CompanyAddress'),
          },
        ],
        testid: formatDataTestId(baseDataTestId, 'addressSelection'),
        styles: { ...inputStyle, mt: 0 },
        relatedFields: {
          HOME: homeAddressFields,
          BUSINESS: companyAddressFields,
        },
      },
    ],
  };

  const differentBillingAddress = [
    {
      type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
      name: 'addressTitle',
      content: (
        <Text
          data-testid={formatDataTestId(baseDataTestId, 'BillingAddress-YourDifferentAdress-Title')}
          {...titleStyle}
        >
          {t('booking.addressTitle')}{' '}
        </Text>
      ),
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'postcodeAddress',
      label: t('booking.addressPostCode'),
      Component: PostcodeAddress,
      styles: {
        ...postcodeAddressStyle,
      },
      testid: formatDataTestId(baseDataTestId, 'PostcodeAddress'),
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'manualAddressToggle',
      label: '',
      Component: ManualAddressToggle,
      relatedFields: addressSelectionConfig,
      testid: formatDataTestId(baseDataTestId, 'ManualAddressToggle'),
    },
  ];

  const config = {
    id: 'billingAddressForm',
    testid,
    elements: {
      fields: [
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'addressTitle',
          content: (
            <Text
              data-testid={formatDataTestId(baseDataTestId, 'BillingAddress-Title')}
              {...billingTitleStyle}
            >
              {t('billingAddress.title')}
            </Text>
          ),
        },
        {
          type: FORM_FIELD_TYPES.RADIO_GROUP,
          name: 'billingAddressSelection',
          label: 'Details',
          options: [
            {
              value: 'CurrentAddress',
              label: t('billingAddress.current'),
              Component: CurrentAddressRadioButton,
              testid: formatDataTestId(baseDataTestId, 'BillingAddressSelection-SameAddress'),
            },
            {
              value: 'DifferentAddress',
              label: t('billingAddress.different'),
              testid: formatDataTestId(baseDataTestId, 'BillingAddressSelection-DifferentAddress'),
            },
          ],
          testid: formatDataTestId(baseDataTestId, 'BillingAddressSelection'),
          styles: { ...inputStyle, mt: 0, mb: 'md' },
          relatedFields: {
            DifferentAddress: differentBillingAddress,
          },
        },
      ],
      onSubmitAction: onSubmitAction,
      formStyles: { ...billingAddressStyle },
    },
    defaultValues,
    validationSchema: formValidationSchema,
    getFormState,
    resetForm,
  } as FormProps;

  return config;
};

const inputStyle = {
  w: {
    mobile: 'full',
    xs: 'full',
    sm: '25.063rem',
    md: '27.563rem',
    lg: '24.5rem',
    xl: '26.25rem',
  },
  mb: 'lg',
};

const postcodeAddressStyle = {
  w: {
    mobile: 'full',
    xs: 'full',
    sm: '25.063rem',
    md: '27.563rem',
    lg: '24.5rem',
    xl: '26.25rem',
  },
  mb: 'sm',
};

const titleStyle = {
  my: 'md',
  fontSize: 'lg',
  fontWeight: 'semibold',
  lineHeight: '3',
} as TextProps;

const billingTitleStyle = { mb: 'xl', fontSize: '2xl', fontWeight: 'semibold', lineHeight: '4' };

const linkStyles = {
  color: 'darkGrey1',
  textDecoration: 'underline',
  mb: 'md',
};

const billingAddressStyle = {
  w: { mobile: 'full', xs: 'full', sm: '25.063rem', md: '27.563rem', xl: '26.25rem' },
  mb: '5xl',
  color: 'darkGrey1',
};

const addressStyle = {
  fontSize: 'md',
  lineHeight: '3',
};
