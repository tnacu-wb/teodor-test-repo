import { Box, StyleProps, Text, TextProps } from '@chakra-ui/react';
import { Dropdown, Input } from '@whitbread-eos/atoms';
import { formatDataTestId, useSemanticTypography } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { ReactElement } from 'react';
import { Control, Controller, ControllerRenderProps, FieldValues } from 'react-hook-form';

import {
  errorMessageStyles,
  formErrorStyles,
} from '../LeadGuestDetails/LeadGuestDetails.component';

type Props = {
  control: Control;
  formField: any;
  errors: { [key: string]: { message: string } };
  index: number;
  fieldsRequired?: boolean;
  showIcon?: boolean;
  prefix?: string;
  inputStyle?: StyleProps;
  dropdownStyle?: StyleProps;
};

export default function GuestInputs({
  control,
  formField,
  errors,
  index,
  fieldsRequired = true,
  showIcon = false,
  prefix = '',
  inputStyle = {},
  dropdownStyle = {},
}: Readonly<Props>) {
  const { name } = formField;
  const { t } = useTranslation();
  const getTypographyProps = useSemanticTypography();
  const titleText = `${prefix}title`;
  const firstNameText = `${prefix}firstName`;
  const lastNameText = `${prefix}lastName`;
  const firstNameLabel = fieldsRequired
    ? t('booking.leadGuest.FirstName')
    : t('booking.leadGuest.FirstName').replace(/\s\*$/, '');
  const lastNameLabel = fieldsRequired
    ? t('booking.leadGuest.LastName')
    : t('booking.leadGuest.LastName').replace(/\s\*$/, '');

  const nameInputTypographyStyles = {
    inputElementStyles: getTypographyProps({}, nameInputSemanticTypography),
  };

  const titleDropdownTypographyStyles = {
    menuButtonTextStyles: getTypographyProps({}, titleDropdownButtonSemanticTypography),
    menuItemTextStyles: getTypographyProps({}, titleDropdownItemSemanticTypography),
  };

  const renderDropdownTitle = (
    field: ControllerRenderProps<FieldValues, `${string}[${number}][${string}]`>
  ): ReactElement => {
    const { onChange, ...restOfField } = field;
    return (
      <Dropdown
        {...restOfField}
        showStatusIcon={showIcon}
        onChange={(o: any) => onChange(o?.id)}
        options={formField.dropdownOptions}
        placeholder={t('booking.leadGuest.title')}
        matchWidth
        hasError={Boolean(errors?.[titleText]?.message)}
        dataTestId={formatDataTestId(formField.testid, `${prefix}TitleDropdown`)}
        selectedId={field.value}
        dropdownStyles={{
          menuListStyles: {
            zIndex: 999,
          },
          menuButtonStyles: {
            ...dropdownStyle,
          },
          ...titleDropdownTypographyStyles,
        }}
      />
    );
  };

  return (
    <Box data-testid={formatDataTestId(formField.testid, 'InputsContainer')}>
      <Box {...inputStyle}>
        <Controller
          name={`${name}[${index}][${titleText}]`}
          control={control}
          render={({ field }) => renderDropdownTitle(field)}
        />
        {errors?.[titleText]?.message && (
          <Box
            {...formErrorStyles}
            dataTestId={formatDataTestId(formField.testid, 'ErrorContainer')}
          >
            <Box {...errorMessageStyles}>
              <Text
                {...titleErrorTextStyles}
                {...getTypographyProps({ fontSize: 'xs' }, { textStyle: 'body-s-regular' })}
                dataTestId={formatDataTestId(formField.testid, 'ErrorText')}
              >
                {errors?.[titleText]?.message}
              </Text>
            </Box>
          </Box>
        )}
      </Box>
      <Box {...inputStyle}>
        <Controller
          name={`${name}[${index}][${firstNameText}]`}
          control={control}
          render={({ field }) => {
            return (
              <Input
                {...formField.props}
                {...field}
                type="text"
                placeholderText={firstNameLabel}
                label={firstNameLabel}
                error={errors?.[firstNameText]?.message}
                styles={{
                  ...(formField.props?.styles ?? {}),
                  ...nameInputTypographyStyles,
                }}
                className="sessioncamhidetext assist-no-show"
                dataTestId={formatDataTestId(formField.testid, `${prefix}FirstName`)}
              />
            );
          }}
        />
      </Box>
      <Box {...inputStyle}>
        <Controller
          name={`${name}[${index}][${lastNameText}]`}
          control={control}
          render={({ field }) => {
            return (
              <Input
                {...formField.props}
                {...field}
                type="text"
                placeholderText={lastNameLabel}
                label={lastNameLabel}
                error={errors?.[lastNameText]?.message}
                styles={{
                  ...(formField.props?.styles ?? {}),
                  ...nameInputTypographyStyles,
                }}
                className="sessioncamhidetext assist-no-show"
                dataTestId={formatDataTestId(formField.testid, `${prefix}LastName`)}
              />
            );
          }}
        />
      </Box>
    </Box>
  );
}

const nameInputSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;

const titleDropdownButtonSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;

const titleDropdownItemSemanticTypography = {
  textStyle: 'body-s-regular',
} as TextProps;

const titleErrorTextStyles = {
  color: 'error',
  marginLeft: 'md',
  marginTop: 'sm',
  whiteSpace: 'nowrap',
} as TextProps;
