import { Box, type BoxProps, Flex, Link, Text, type TextProps } from '@chakra-ui/react';
import type { RegisterPersonalDetails } from '@whitbread-eos/api';
import {
  FORM_BUTTON_TYPES,
  FORM_FIELD_TYPES,
  FormDynamicFieldCompProps,
  FormProps,
  Icon,
  Tick24,
} from '@whitbread-eos/atoms';
import {
  CountriesDropdown,
  EmailUpdates,
  PhoneSelector,
  PostcodeAddress,
  RegisterProfile,
} from '@whitbread-eos/molecules';
import {
  formatDataTestId,
  formatGuestTitleOptions,
  GLOBALS,
  renderSanitizedHtml,
} from '@whitbread-eos/utils';
import { Dispatch, SetStateAction, useEffect } from 'react';

import validateForm from './formValidation';

interface RegisterDetailsFormConfigArgsType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: (data: RegisterPersonalDetails) => void;
  baseDataTestId: string;
  t: (id: string) => string;
  currentLang: string | undefined;
  resetForm?: number;
  clearPhoneFields?: boolean;
  isLocationRequired: boolean;
  setIsLocationRequired: Dispatch<SetStateAction<boolean>>;
  bkngData?: any;
  registerIsError?: boolean;
  isCompanyNameAdvanceEnabled?: boolean;
  isCountrySelectorFilterableEnabled?: boolean;
}

declare module 'yup' {
  interface ArraySchema<T> {
    unique(a: string, message: string): ArraySchema<T>;
  }
}

const EmailWrapper = (props: FormDynamicFieldCompProps | any) => {
  return <EmailUpdates {...props} />;
};

export const registerDetailsFormConfig = ({
  getFormState,
  defaultValues,
  onSubmit,
  baseDataTestId,
  t,
  currentLang,
  resetForm,
  clearPhoneFields,
  isLocationRequired,
  bkngData,
  setIsLocationRequired,
  registerIsError,
  isCompanyNameAdvanceEnabled,
  isCountrySelectorFilterableEnabled,
}: RegisterDetailsFormConfigArgsType) => {
  const { formValidationSchema } = validateForm({ t, currentLang, isCompanyNameAdvanceEnabled });
  const titleOptions = formatGuestTitleOptions(t('common.nameTitles'));
  const errorsOrder = [
    'title',
    'firstName',
    'lastName',
    'password',
    'confirmPassword',
    'email',
    'phone',
    'addressSelection',
    'companyName',
    'addressLine1',
    'addressLine2',
    'addressLine3',
    'addressLine4',
    'postalCode',
    'cityName',
    'acceptTermsConditions',
  ];

  const ManualAddressToggle = ({ field, handleSetValue, errors }: FormDynamicFieldCompProps) => {
    const { name } = field;

    // Trigger automatic expansion of manual address, when no Postal Code is entered
    useEffect(() => {
      if (!handleSetValue) {
        return;
      }
      if (
        errors?.addressLine1 ||
        (bkngData && currentLang === GLOBALS.language.EN) ||
        currentLang === GLOBALS.language.DE
      ) {
        handleSetValue(name, 'manualAddress');
      }
    }, [handleSetValue, name, errors]);

    useEffect(() => {
      if (handleSetValue && registerIsError && currentLang === GLOBALS.language.DE) {
        handleSetValue('addressSelection', GLOBALS.addressType.HOME);
      }
    }, [registerIsError]);

    if (field.value === 'manualAddress') {
      return null;
    }

    const enterManualAddress = () => {
      if (!handleSetValue) {
        return;
      }
      handleSetValue(name, 'manualAddress');
    };

    return (
      <Link {...linkStyles} as="button" type="button" onClick={enterManualAddress}>
        {t('account.register.enterManualAddress')}
      </Link>
    );
  };

  const homeAddressFields = [
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine1',
      id: 'homeAddressLine1',
      label: t('account.register.address1'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-AddressLine1'),
      styles: inputStyle,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine2',
      id: 'homeAddressLine2',
      label: t('account.register.address2'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-AddressLine2'),
      styles: inputStyle,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine3',
      id: 'homeAddressLine3',
      label: t('account.register.address3'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-AddressLine3'),
      styles: inputStyle,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine4',
      id: 'homeAddressLine4',
      label: t('account.register.address4'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-AddressLine4'),
      styles: inputStyle,
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
      label: t('account.register.addressPostCodeShort'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-PostalCode'),
      styles: inputStyle,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'cityName',
      id: 'homeAddressCityName',
      label: t('account.register.addressCity'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-CityName'),
      styles: inputStyle,
      hidden: !isLocationRequired,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
      },
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'countryCode',
      label: 'Country',
      Component: CountriesDropdown,
      testid: formatDataTestId(baseDataTestId, 'CountrySelector'),
      styles: {
        ...inputStyle,
        mt: 'xs',
      },
      props: {
        showIcon: false,
        setIsLocationRequired,
        currentLang,
        isCountrySelectorFilterableEnabled,
      },
    },
  ];

  const companyAddressFields = [
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'companyName',
      id: 'businessCompanyName',
      label: t('account.register.addressCompanyName'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-CompanyName'),
      styles: inputStyle,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine1',
      id: 'businessAddressLine1',
      label: t('account.register.address1'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-AddressLine1'),
      styles: inputStyle,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine2',
      id: 'businessAddressLine2',
      label: t('account.register.address2'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-AddressLine2'),
      styles: inputStyle,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine3',
      id: 'businessAddressLine3',
      label: t('account.register.address3'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-AddressLine3'),
      styles: inputStyle,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'addressLine4',
      id: 'businessAddressLine4',
      label: t('account.register.address4'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-AddressLine4'),
      styles: inputStyle,
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
      label: t('account.register.addressPostCodeShort'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress-PostalCode'),
      styles: inputStyle,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
      },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'cityName',
      id: 'businessAddressCityName',
      label: t('account.register.addressCity'),
      testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress-CityName'),
      styles: inputStyle,
      hidden: !isLocationRequired,
      props: {
        showIcon: false,
        className: 'sessioncamhidetext',
      },
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'countryCode',
      label: 'Country',
      Component: CountriesDropdown,
      testid: formatDataTestId(baseDataTestId, 'CountrySelector'),
      styles: {
        ...inputStyle,
        mt: 'xs',
      },
      props: {
        showIcon: false,
        setIsLocationRequired,
        currentLang,
        isCountrySelectorFilterableEnabled,
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
            label: t('account.register.privateAddress'),
            testid: formatDataTestId(baseDataTestId, 'AddressSelection-PersonalAddress'),
          },
          {
            value: 'BUSINESS',
            label: t('account.register.businessAddress'),
            testid: formatDataTestId(baseDataTestId, 'AddressSelection-CompanyAddress'),
          },
        ],
        testid: formatDataTestId(baseDataTestId, 'AddressSelection'),
        styles: { ...inputStyle, mt: 0, mb: 'lg' },
        relatedFields: {
          HOME: homeAddressFields,
          BUSINESS: companyAddressFields,
        },
        hidden: currentLang === GLOBALS.language.DE,
      },
    ],
  };

  const addressConfigDE = {
    manualAddress: [
      {
        type: FORM_FIELD_TYPES.RADIO_GROUP,
        name: 'addressSelection',
        label: 'Details',
        testid: formatDataTestId(baseDataTestId, 'AddressSelection'),
        relatedFields: {
          HOME: homeAddressFields,
        },
        hidden: currentLang === GLOBALS.language.EN,
      },
    ],
  };

  const config = {
    id: 'registerDetailsForm',
    elements: {
      fields: [
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'createAccount',
          content: <Text {...titleStyle}>{t('account.register.registerLabel')}</Text>,
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'reasonsToStay',
          content: <Text {...subTitleStyle}>{t('account.reasonsLabel')}</Text>,
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'reasonItem1',
          content: (
            <Flex alignItems="flex-end">
              <Icon svg={<Tick24 color="var(--chakra-colors-darkGrey2)" />} />
              <Text {...textStyle}>{t('account.register.reasonItem1')}</Text>
            </Flex>
          ),
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'reasonItem2',
          content: (
            <Flex alignItems="flex-end">
              <Icon svg={<Tick24 color="var(--chakra-colors-darkGrey2)" />} />
              <Text {...textStyle}>{t('account.register.reasonItem2')}</Text>
            </Flex>
          ),
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'reasonItem3',
          content: (
            <Flex alignItems="flex-end">
              <Icon svg={<Tick24 color="var(--chakra-colors-darkGrey2)" />} />
              <Text {...textStyle}>{t('account.register.reasonItem3')}</Text>
            </Flex>
          ),
        },
        {
          type: FORM_FIELD_TYPES.DROPDOWN,
          name: 'title',
          dropdownOptions: titleOptions,
          testid: formatDataTestId(baseDataTestId, 'Title'),
          styles: { maxW: '8.5rem', mt: '4xl' },
          props: {
            showStatusIcon: false,
            placeholder: t('account.register.placeholder.title'),
            accessibilityRole: 'combobox',
          },
        },
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          name: 'firstName',
          label: t('account.register.placeholder.firstName'),
          testid: formatDataTestId(baseDataTestId, 'FirstName'),
          props: {
            showIcon: false,
            className: 'sessioncamhidetext',
          },
          styles: inputStyle,
        },
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          name: 'lastName',
          label: t('account.register.placeholder.lastName'),
          testid: formatDataTestId(baseDataTestId, 'LastName'),
          props: {
            showIcon: false,
            className: 'sessioncamhidetext',
          },
          styles: inputStyle,
        },
        {
          type: FORM_FIELD_TYPES.INPUT_EMAIL,
          name: 'email',
          label: t('account.register.placeholder.email'),
          testid: formatDataTestId(baseDataTestId, 'Email'),
          props: {
            showIcon: false,
            className: 'sessioncamhidetext',
          },
          styles: inputStyle,
        },
        {
          type: FORM_FIELD_TYPES.INPUT_PASSWORD,
          name: 'password',
          label: t('account.register.placeholder.password'),
          testid: formatDataTestId(baseDataTestId, 'Password'),
          props: {
            showIcon: false,
            className: 'sessioncamhidetext',
            autoComplete: 'new-password',
          },
          styles: inputStyle,
        },
        {
          type: FORM_FIELD_TYPES.INPUT_PASSWORD,
          name: 'confirmPassword',
          label: t('account.register.placeholder.confirmPassword'),
          testid: formatDataTestId(baseDataTestId, 'ConfirmPassword'),
          props: {
            showIcon: false,
            className: 'sessioncamhidetext',
            autoComplete: 'new-password',
          },
          styles: inputStyle,
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'prePopulateRegisterField',
          label: 'prePopulateRegisterField',
          Component: RegisterProfile,
          props: {
            bkngData,
            currentLang,
          },
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'addressTitle',
          content: <Text {...subTitleStyle}>{t('account.register.contactDetailsLabel')}</Text>,
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'phone',
          label: t('account.register.mobileNumber'),
          Component: PhoneSelector,
          styles: {
            ...inputStyle,
            mt: 'xl',
            ...(currentLang === GLOBALS.language.DE && {
              mb: 0,
            }),
          },
          props: {
            clearField: clearPhoneFields,
            showIcon: false,
            currentLang,
            className: 'sessioncamhidetext',
          },
          testid: formatDataTestId(baseDataTestId, 'Mobile'),
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'postcodeAddress',
          label: t('account.register.addressPostCodeShort'),
          Component: PostcodeAddress,
          styles: {
            ...inputStyle,
          },
          testid: formatDataTestId(baseDataTestId, 'PostcodeAddress'),
          hidden: currentLang === 'de',
          props: {
            showIcon: false,
          },
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'manualAddressToggle',
          label: '',
          Component: ManualAddressToggle,
          relatedFields: addressSelectionConfig,
          testid: formatDataTestId(baseDataTestId, 'ManualAddressToggle'),
          styles: {
            mb: currentLang === 'de' ? 'xl' : 0,
          },
          hidden: currentLang === GLOBALS.language.DE,
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'manualAddressToggle-de',
          label: '',
          Component: ManualAddressToggle,
          relatedFields: addressConfigDE,
          testid: formatDataTestId(baseDataTestId, 'ManualAddressToggle-DE'),
          styles: {
            mb: 0,
          },
          hidden: currentLang === GLOBALS.language.EN,
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'acceptFutureMailing',
          Component: EmailWrapper,
          styles: {
            mt: '5xl',
            mb: '0',
            width: {
              mobile: '100%',
              sm: '33.375rem',
              md: '33.375rem',
              lg: '26.25rem',
              xl: '26.25rem',
            },
          },
          props: {
            currentLang,
          },
          testid: formatDataTestId(baseDataTestId, 'AcceptFutureMailing'),
        },
        {
          type: FORM_FIELD_TYPES.CHECKBOX,
          name: 'acceptTermsConditions',
          styles: {
            ...inputStyle,
          },
          label: (
            <Flex alignItems="flex-end">
              <Box className="formatLinks">
                {renderSanitizedHtml(t('account.register.checkboxPrivacy'))}
              </Box>
            </Flex>
          ),
          testid: formatDataTestId(baseDataTestId, 'AcceptTermsConditions'),
        },
      ],

      buttons: [
        {
          type: FORM_BUTTON_TYPES.SUBMIT,
          label: t('account.register.registerLabel'),
          action: onSubmit,
          props: {
            variant: 'primary',
            size: 'full',
          },
          styles: { ...continueTextStyle, ...continueButtonSectionStyle },
          testid: formatDataTestId(baseDataTestId, 'CreateAccount'),
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
  width: { mobile: 'full', lg: '26.25rem' },
  mb: 'sm',
} as BoxProps;

const continueTextStyle = {
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '3',
  color: 'baseWhite',
} as TextProps;

const inputStyle = {
  width: {
    mobile: '100%',
    sm: '33.375rem',
    md: '33.375rem',
    lg: '26.25rem',
    xl: '26.25rem',
  },
};

const titleStyle = {
  fontSize: '3xxl',
  fontWeight: 'semibold',
  lineHeight: '4',
  color: 'darkGrey1',
  mt: 'lg',
} as TextProps;

const subTitleStyle = {
  fontSize: '2xl',
  fontWeight: 'semibold',
  lineHeight: '4',
  color: 'darkGrey1',
  mt: '4xl',
} as TextProps;

const textStyle = {
  fontSize: 'md',
  mt: 'md',
  ml: 'md',
} as TextProps;

const linkStyles = {
  color: 'btnSecondaryEnabled',
  textDecoration: 'underline',
};
