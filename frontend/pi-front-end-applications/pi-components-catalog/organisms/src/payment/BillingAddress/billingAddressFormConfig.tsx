import type { TextProps } from '@chakra-ui/react';
import { Flex, Link, Text } from '@chakra-ui/react';
import { BillingAddressFormData } from '@whitbread-eos/api';
import {
  FORM_FIELD_TYPES,
  FORM_VALIDATIONS,
  FormDynamicFieldCompProps,
  FormProps,
} from '@whitbread-eos/atoms';
import { CountriesDropdown, PostcodeAddress } from '@whitbread-eos/molecules';
import { formatDataTestId, useSemanticTypography } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { Dispatch, SetStateAction, useEffect } from 'react';

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
  isAmendPage?: boolean;
  isLocationRequired: boolean;
  setIsLocationRequired: Dispatch<SetStateAction<boolean>>;
  isCompanyNameAdvanceEnabled?: boolean;
  horizontalRadioButtons?: boolean;
  isCountryAllowTypingEnabled?: boolean;
  isDatatransPage?: boolean;
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
  isAmendPage,
  isCompanyNameAdvanceEnabled,
  horizontalRadioButtons,
  isCountryAllowTypingEnabled,
  isDatatransPage,
}: BillingAddressFormConfigArgsType) => {
  const formValidationSchema = billingAddressFormValidation({
    t,
    currentLang,
    isCompanyNameAdvanceEnabled,
  });

  const DifferentBillingAddressTitleContent = () => {
    const getTypographyProps = useSemanticTypography();
    const { t } = useTranslation(['common']);

    return (
      <Text
        data-testid={formatDataTestId(baseDataTestId, 'BillingAddress-YourDifferentAdress-Title')}
        {...differentBillingAddressTitleLayoutStyle}
        {...getTypographyProps(
          differentBillingAddressTitleLegacyTypography,
          differentBillingAddressTitleSemanticTypography
        )}
      >
        {t('booking.addressTitle')}{' '}
      </Text>
    );
  };

  interface CurrentAddressRadioButtonProps {
    isChecked: boolean;
  }

  const CurrentAddressRadioButton = ({ isChecked }: CurrentAddressRadioButtonProps) => {
    return (
      <Flex direction="column">
        <Text
          data-testid={formatDataTestId(baseDataTestId, 'BillingAddress-UseCurrentAdress-Title')}
          {...addressStyle}
          fontWeight={isChecked || horizontalRadioButtons ? 'semibold' : 'normal'}
        >
          {t('billingAddress.current')}
        </Text>
        {currentAddressLabel && (
          <Text
            data-testid={formatDataTestId(baseDataTestId, 'BillingAddress-UseCurrentAdress-Label')}
            {...(horizontalRadioButtons ? { ...addressLabelStyle } : { ...addressStyle })}
            className="assist-no-show"
            color={horizontalRadioButtons ? 'darkGrey2' : 'initial'}
          >
            {currentAddressLabel}
          </Text>
        )}
      </Flex>
    );
  };

  const ManualAddressToggle = ({
    field,
    handleSetValue,
    errors,
    getValues,
  }: FormDynamicFieldCompProps) => {
    const { name } = field;

    useEffect(() => {
      if (!handleSetValue) {
        return;
      }
      if (errors?.addressLine1 && getValues('billingAddressSelection') === 'DifferentAddress') {
        handleSetValue(name, 'manualAddress');
      }
    }, [handleSetValue, name, errors, getValues]);

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
    showIcon: false,
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
        showIcon: false,
        className: 'sessioncamhidetext assist-no-show',
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
        showIcon: false,
        className: 'sessioncamhidetext assist-no-show',
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
        showIcon: false,
        className: 'sessioncamhidetext assist-no-show',
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
        showIcon: false,
        className: 'sessioncamhidetext assist-no-show',
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
        showIcon: false,
        className: 'sessioncamhidetext assist-no-show',
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
        showIcon: false,
        className: 'sessioncamhidetext assist-no-show',
      },
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'countryCode',
      id: 'homeCountryCode',
      Component: CountriesDropdown,
      label: t('booking.country'),
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
        showIcon: false,
        className: 'sessioncamhidetext assist-no-show',
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
        showIcon: false,
        className: 'sessioncamhidetext assist-no-show',
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
        showIcon: false,
        className: 'sessioncamhidetext assist-no-show',
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
        showIcon: false,
        className: 'sessioncamhidetext assist-no-show',
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
        showIcon: false,
        className: 'sessioncamhidetext assist-no-show',
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
        showIcon: false,
        className: 'sessioncamhidetext assist-no-show',
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
        showIcon: false,
        className: 'sessioncamhidetext assist-no-show',
      },
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'countryCode',
      label: t('booking.country'),
      id: 'businessCountryCode',
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
      hidden: isDatatransPage,
      content: <DifferentBillingAddressTitleContent />,
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'postcodeAddress',
      label: t('booking.addressPostCode'),
      Component: PostcodeAddress,
      styles: {
        ...(isDatatransPage ? datatransPostcodeStyle : inputStyle),
      },
      testid: formatDataTestId(baseDataTestId, 'PostcodeAddress'),
      hidden: currentLang === 'de',
      props: {
        showIcon: false,
        ...(isDatatransPage && {
          // Wraps Input + Button in a Flex row with button right-aligned.
          containerStyle: { display: 'flex', alignItems: 'center', gap: '16px', width: '100%' },
          // Input takes as much space as available; 'full' width respects the container.
          styles: { flex: 1, minWidth: 0 },
          buttonStyle: {
            mt: '0',
            mb: '0',
            width: 'auto',
            flexShrink: 0,
            marginLeft: 'auto',
            size: 'md',
            borderRadius: 'full',
            px: 'lg',
          },
        }),
      },
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
            <BillingAddressTitleContent
              t={t}
              baseDataTestId={baseDataTestId}
              isDatatransPage={isDatatransPage}
            />
          ),
        },
        ...(isDatatransPage
          ? differentBillingAddress
          : [
              {
                type: FORM_FIELD_TYPES.RADIO_GROUP,
                name: 'billingAddressSelection',
                label: 'Details',
                props: {
                  horizontalRadioButtons: horizontalRadioButtons,
                  errorLocation: FORM_VALIDATIONS.FORM_FIELD_ERROR_LOCATION.ON_TOP,
                },
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
                    testid: formatDataTestId(
                      baseDataTestId,
                      'BillingAddressSelection-DifferentAddress'
                    ),
                  },
                ],
                testid: formatDataTestId(baseDataTestId, 'BillingAddressSelection'),
                styles: {
                  ...inputStyle,
                  ...(horizontalRadioButtons && { ...radioStyleWrapper }),
                  pointerEvents: isAmendPage && 'none',
                  color: isAmendPage && 'lightGrey3',
                  border: isAmendPage && '1px solid var(--chakra-colors-lightGrey3)',
                },
                relatedFields: {
                  DifferentAddress: differentBillingAddress,
                },
              },
            ]),
      ],
      onSubmitAction: onSubmitAction,
      formStyles: {
        ...billingAddressStyle,
        ...(isDatatransPage && {
          mb: 'sm',
          mt: '0',
          w: 'full',
        }),
        ...(!isDatatransPage && horizontalRadioButtons && { ...radioStyleWrapper }),
      },
    },
    defaultValues,
    validationSchema: formValidationSchema,
    getFormState,
    resetForm,
  } as FormProps;

  return config;
};

const inputStyle = {
  w: { mobile: 'full', xs: 'full', sm: '25.063rem', md: '27.563rem', xl: '26.25rem' },
};

// For Datatrans the postcode row is full-width (no capped rem value) so the input
// can grow to fill the flex container alongside the Find button.
const datatransPostcodeStyle = {
  w: 'full',
};

const radioStyleWrapper = {
  w: { mobile: 'full', md: 'full', lg: '50.5rem', xl: '54rem' },
};

const differentBillingAddressTitleLayoutStyle = {
  my: 'md',
};

const differentBillingAddressTitleLegacyTypography = {
  fontSize: 'lg',
  fontWeight: 'semibold',
  lineHeight: '4',
} as TextProps;

const differentBillingAddressTitleSemanticTypography = {
  textStyle: 'title-m-emphasis',
} as const;

const billingTitleLayoutStyle = { mb: 'xl' };
const billingTitleDatatransLayoutStyle = { mt: 'sm', mb: 'md' };

const billingTitleLegacyTypography = { fontSize: '2xl', fontWeight: 'semibold', lineHeight: '4' };

const billingTitleSemanticTypography = {
  textStyle: 'heading-m',
} as const;

// Datatrans Billing Address title matches the "Card details" heading style:
// Proxima Nova (secondary), semibold 600, 18px, 120% line-height.
const billingTitleDatatransTypography = {
  fontFamily: 'heading',
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '1.2',
} as TextProps;

const BillingAddressTitleContent = ({
  t,
  baseDataTestId,
  isDatatransPage,
}: {
  t: (id: string) => string;
  baseDataTestId: string;
  isDatatransPage?: boolean;
}) => {
  const getTypographyProps = useSemanticTypography();
  return (
    <Text
      data-testid={formatDataTestId(baseDataTestId, 'BillingAddress-Title')}
      {...(isDatatransPage ? billingTitleDatatransLayoutStyle : billingTitleLayoutStyle)}
      {...(isDatatransPage
        ? billingTitleDatatransTypography
        : getTypographyProps(billingTitleLegacyTypography, billingTitleSemanticTypography))}
    >
      {t('billingAddress.title')}
    </Text>
  );
};

const linkStyles = {
  color: 'btnSecondaryEnabled',
  textDecoration: 'underline',
  mb: 'md',
};

const billingAddressStyle = {
  w: { mobile: 'full', xs: 'full', sm: '25.063rem', md: '27.563rem', xl: '26.25rem' },
  mb: '5xl',
};

const addressStyle = {
  fontSize: 'md',
  lineHeight: '3',
};

const addressLabelStyle = {
  fontSize: 'xs',
};
