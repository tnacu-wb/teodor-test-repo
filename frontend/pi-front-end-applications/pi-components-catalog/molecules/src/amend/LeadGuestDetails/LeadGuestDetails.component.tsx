import { Box, StyleProps } from '@chakra-ui/react';
import {
  AmendLeadGuestLabels,
  AmendLeadGuestValidationLabels,
  FT_CCUI_AMEND_GUEST_ADDRESS,
  FT_PI_AMEND_GUEST_ADDRESS,
  ReservationLeadGuestType,
} from '@whitbread-eos/api';
import { Dropdown, Form, FORM_FIELD_TYPES, FormProps, Input } from '@whitbread-eos/atoms';
import { formatDataTestId, GLOBALS, useFeatureToggle } from '@whitbread-eos/utils';
import { Dispatch, SetStateAction } from 'react';
import { Controller } from 'react-hook-form';

import { CountriesDropdown } from '../../guest-details';
import { INITIAL_LEAD_GUEST_DETAILS } from '../RoomModal/constants';
import { leadGuestFormConfig } from './';

interface Props {
  labels: AmendLeadGuestLabels;
  validationLabels: AmendLeadGuestValidationLabels;
  baseDataTestId: string;
  leadGuestDetails: FormProps['defaultValues'];
  getFormState: FormProps['getFormState'];
  onSubmit: (guestDetails: FormProps['defaultValues']) => void;
  isEdit?: boolean;
  setGuestDetails?: (value: ReservationLeadGuestType) => void;
  setIsLocationRequired: Dispatch<SetStateAction<boolean>>;
  hotelCountry: string;
  language: string;
}

type Field = {
  name: string;
  onBlur: () => void;
  value: any;
  onChange: (value: any) => void;
};

type FormFieldPropTypes = {
  name: string;
  label: string;
  type: 'dropdown' | 'input' | 'email';
  featureFlagEnabled?: boolean;
  countriesIncluded?: string[];
};

export function LeadGuestDetailsForm({ control, formField, errors }: any) {
  const {
    [FT_PI_AMEND_GUEST_ADDRESS]: isAmendGuestAddressEnabledForPI,
    [FT_CCUI_AMEND_GUEST_ADDRESS]: isAmendGuestAddressEnabledForCCUI,
  } = useFeatureToggle();

  const { useTooltip, setGuestDetails, setIsLocationRequired, hotelCountry, language } =
    formField.props;

  const isFeatureEnabled = (featureFlagEnabled?: boolean, countriesIncluded?: string[]) => {
    return featureFlagEnabled && countriesIncluded?.includes(hotelCountry);
  };

  const formFieldControls: FormFieldPropTypes[] = [
    { name: 'title', label: 'title', type: 'dropdown' },
    { name: 'firstName', label: 'firstName', type: 'input' },
    { name: 'lastName', label: 'lastName', type: 'input' },
    {
      name: 'emailAddress',
      label: 'emailAddress',
      type: 'email',
    },
    {
      name: 'addressLine1',
      label: 'addressLine1',
      type: 'input',
      featureFlagEnabled: isAmendGuestAddressEnabledForPI || isAmendGuestAddressEnabledForCCUI,
      countriesIncluded: ['Germany', 'Deutschland'],
    },
    {
      name: 'addressLine2',
      label: 'addressLine2',
      type: 'input',
      featureFlagEnabled: isAmendGuestAddressEnabledForPI || isAmendGuestAddressEnabledForCCUI,
      countriesIncluded: ['Germany', 'Deutschland'],
    },
    {
      name: 'addressLine3',
      label: 'addressLine3',
      type: 'input',
      featureFlagEnabled: isAmendGuestAddressEnabledForPI || isAmendGuestAddressEnabledForCCUI,
      countriesIncluded: ['Germany', 'Deutschland'],
    },
    {
      name: 'cityName',
      label: 'city',
      type: 'input',
      featureFlagEnabled: isAmendGuestAddressEnabledForPI || isAmendGuestAddressEnabledForCCUI,
      countriesIncluded: ['Germany', 'Deutschland'],
    },
    {
      name: 'postalCode',
      label: 'postalCode',
      type: 'input',
      featureFlagEnabled: isAmendGuestAddressEnabledForPI || isAmendGuestAddressEnabledForCCUI,
      countriesIncluded: ['Germany', 'Deutschland'],
    },
  ];

  const errorStyles = (fieldHasError: boolean): StyleProps =>
    useTooltip && fieldHasError ? tooltipErrorStyles : {};

  const fieldProps = (field: Field) => {
    return {
      name: field.name,
      onBlur: field.onBlur,
      value: field.value,
      onChange: (value: any) => {
        field.onChange(value);
        setGuestDetails((prevValue: any) => ({
          ...prevValue,
          [field.name]: value,
        }));
      },
    };
  };

  return (
    <Box>
      {formFieldControls.map(
        ({ name, label, type, featureFlagEnabled, countriesIncluded }: FormFieldPropTypes) =>
          (isFeatureEnabled(featureFlagEnabled, countriesIncluded) ||
            (!featureFlagEnabled && !countriesIncluded)) && (
            <Box key={name} {...(type === 'input' && errorStyles(errors))}>
              <Controller
                name={name}
                control={control}
                render={({ field }: any) => {
                  return (
                    <>
                      {type === 'input' && (
                        <Input
                          {...formField.props}
                          {...fieldProps(field)}
                          data-testid={formatDataTestId(formField.testid, name)}
                          styles={inputStyles}
                          type="text"
                          showIcon={false}
                          placeholderText={formField.props.labels[label]}
                          label={formField.props.labels[label]}
                          error={errors[name]?.message}
                          className="sessioncamhidetext assist-no-show"
                        />
                      )}
                      {type === 'email' && (
                        <Input
                          {...formField.props}
                          {...fieldProps(field)}
                          data-testid={formatDataTestId(formField.testid, name)}
                          styles={inputStyles}
                          type="text"
                          showIcon={false}
                          placeholderText={formField.props.labels[label]}
                          label={formField.props.labels[label]}
                          error={errors[name]?.message}
                          className="sessioncamhidetext assist-no-show"
                          emailFieldProps={{
                            inputMode: 'email' as const,
                            autoCapitalize: 'off',
                            autoCorrect: 'off',
                            autoComplete: 'email',
                          }}
                        />
                      )}
                      {type === 'dropdown' && (
                        <Dropdown
                          {...formField.props}
                          {...fieldProps(field)}
                          dataTestId={formatDataTestId(formField.testid, `${label}Dropdown`)}
                          onChange={(title: any) => {
                            setGuestDetails((prevValue: any) => ({
                              ...prevValue,
                              [name]: title?.id,
                            }));
                            field.onChange(title?.id);
                          }}
                          showStatusIcon={false}
                          options={formField.dropdownOptions}
                          placeholder={formField.props.labels[name]}
                          hasError={Boolean(errors[name]?.message)}
                          selectedId={field.value}
                          matchWidth
                          dropdownStyles={{
                            ...dropdownStyles,
                            menuButtonStyles: renderDropdownStyles(Boolean(errors[name]?.message)),
                          }}
                          onBlur={field.onBlur}
                        />
                      )}
                    </>
                  );
                }}
              />
            </Box>
          )
      )}
      {(isAmendGuestAddressEnabledForPI || isAmendGuestAddressEnabledForCCUI) &&
        ['Germany', 'Deutschland'].includes(hotelCountry) && (
          <Box {...{ pt: '1rem' }}>
            <Controller
              name="countryCode"
              control={control}
              defaultValue={
                language === GLOBALS.language.EN ? GLOBALS.localeUpper.GB : GLOBALS.localeUpper.DE
              }
              render={({ field }) => {
                return (
                  <CountriesDropdown
                    formField={{
                      label: 'Country',
                      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
                      name: 'countryCode',
                      testid: formatDataTestId(`leadGuest`, 'CountrySelector'),
                      props: {
                        showIcon: false,
                        setIsLocationRequired,
                      },
                    }}
                    getValues={() => ''}
                    field={field}
                  />
                );
              }}
            />
          </Box>
        )}
    </Box>
  );
}

export function LeadGuestDetails({
  labels,
  validationLabels,
  baseDataTestId,
  leadGuestDetails,
  getFormState,
  isEdit,
  onSubmit,
  setGuestDetails,
  setIsLocationRequired,
  hotelCountry,
  language,
}: Readonly<Props>) {
  return (
    <Box data-testid={formatDataTestId(baseDataTestId, 'lg-section')}>
      <Form
        {...leadGuestFormConfig({
          getFormState,
          defaultValues: isEdit ? leadGuestDetails : INITIAL_LEAD_GUEST_DETAILS,
          onSubmit,
          baseTestId: baseDataTestId,
          labels,
          validationLabels,
          isEdit,
          setGuestDetails,
          setIsLocationRequired,
          hotelCountry,
          language,
        })}
      />
    </Box>
  );
}

const inputWrapperStyles = {
  marginTop: 'md',
};

const inputElementStyles = {
  borderRadius: 'var(--chakra-space-xs)',
  border: '1px solid',
  borderColor: 'var(--chakra-colors-lightGrey1)',
  _hover: {
    borderColor: 'var(--chakra-colors-darkGrey1)',
  },
  _invalid: {
    boxShadow: 'none',
    borderColor: 'var(--chakra-colors-alert)',
  },
};

const inputStyles = {
  inputWrapperStyles,
  inputElementStyles,
};

const dropdownStyles = {
  menuButtonStyles: {
    borderColor: 'var(--chakra-colors-lightGrey1)',
  },
  menuListStyles: {
    py: 0,
    maxHeight: '12.5rem',
  },
};

const tooltipErrorStyles = {
  marginBottom: 'var(--chakra-space-4xl)',
} as StyleProps;

const renderDropdownStyles = (hasError: boolean) => {
  return {
    border: '1px solid',
    borderColor: hasError ? 'var(--chakra-colors-error)' : 'lightGrey1',
    borderRadius: 'var(--chakra-radii-md)',
  };
};
