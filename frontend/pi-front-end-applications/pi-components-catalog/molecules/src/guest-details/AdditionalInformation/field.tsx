import { Box, StyleProps, Text, useMediaQuery, useToken } from '@chakra-ui/react';
import { Input, SingleDatePicker, MultiSelect } from '@whitbread-eos/atoms';
import { formatDataTestId, useCustomLocale, getYearsAgoFromNow } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useState } from 'react';

import {
  dataErrorText,
  inputIconStyles,
  todayLabel,
  tomorrowLabel,
  fieldProps,
  handleNationalityChange,
} from './common';

type BaseStyles = Record<string, any>;

const FieldController = ({
  field,
  label,
  name,
  type,
  testId,
  errMsg,
  formField,
  nationalities,
  isDisabled,
  handleSetValue,
  fieldToClear,
}: any) => {
  const { t } = useTranslation();
  const [toggleDobInfo, setToggleDobInfo] = useState(false);
  const [lightGrey1, primary] = useToken('colors', ['lightGrey1', 'primary']);
  const [spacexl, space4xl] = useToken('space', ['xl', '4xl']);
  const [fontMd] = useToken('fontSizes', ['md']);
  const [darkGrey2] = useToken('colors', ['darkGrey2']);
  const { language } = useCustomLocale();
  const [isLargerThanSm] = useMediaQuery('(min-width: 576px)');
  const minDate = getYearsAgoFromNow(100);

  const multiSelectStyles = {
    control: (baseStyles: BaseStyles, state: any) => ({
      ...baseStyles,
      borderColor: state?.menuIsOpen ? primary : lightGrey1,
      minHeight: space4xl,
      fontSize: fontMd,
      boxShadow: 'none',
      borderWidth: state?.menuIsOpen ? '2px' : '1px',
      borderRadius: '0.375rem',
      ':hover': { borderColor: lightGrey1 },
      ...(state.isDisabled && { backgroundColor: '#fff' }),
    }),
    multiValue: (baseStyles: BaseStyles) => ({
      ...baseStyles,
      minHeight: spacexl,
      alignItems: 'center',
    }),
    multiValueRemove: (baseStyles: BaseStyles) => ({
      ...baseStyles,
      ':hover': {
        backgroundColor: 'none',
        color: lightGrey1,
      },
    }),
    placeholder: (baseStyles: BaseStyles) => ({
      ...baseStyles,
      color: darkGrey2,
    }),
    valueContainer: (baseStyles: any) => ({
      ...baseStyles,
      padding: '2px 16px',
    }),
    container: (baseStyles: BaseStyles, state: any) => ({
      ...baseStyles,
      '&:hover': { borderColor: primary },
      ...(state.isDisabled && { opacity: '0.4' }),
    }),
  };

  useEffect(() => {
    let timeoutId: ReturnType<typeof setTimeout>;
    if (toggleDobInfo) timeoutId = setTimeout(() => setToggleDobInfo(false), 3000);

    return () => clearTimeout(timeoutId);
  }, [toggleDobInfo]);

  useEffect(() => {
    if (handleSetValue && isDisabled) {
      handleSetValue(name, '');
    }
  }, [isDisabled, handleSetValue]);

  switch (type) {
    case 'input': {
      const inputLabel =
        label === 'email'
          ? t('precheckin.additionalfields.email')
          : t(`precheckin.details.${label}`);
      return (
        <Input
          {...formField.props}
          {...fieldProps(field)}
          type="text"
          isDisabled={isDisabled}
          showIcon={false}
          placeholderText={inputLabel}
          label={inputLabel}
          error={field?.value ? (errMsg ?? '') : ''}
          className="sessioncamhidetext assist-no-show"
        />
      );
    }
    case 'datePicker':
      return (
        <Box position="relative" mb={toggleDobInfo ? '3xl' : ''} maxW={multiSelectMaxW}>
          <SingleDatePicker
            minDate={minDate}
            maxDate={new Date()}
            {...(language === 'de' ? { locale: 'de' } : { locale: 'en' })}
            name={name}
            inputPlaceholder={t(`precheckin.additionalfields.${label}`)}
            inputLabel={t(`precheckin.additionalfields.${label}`)}
            defaultStartDate={field.value ? new Date(field.value) : null}
            isRightIcon
            popperPlacement={isLargerThanSm ? 'bottom' : 'top'}
            displayDateFormat="dd MMM yyyy"
            dataTestId={formatDataTestId(formField.testid, testId)}
            dateFormat="dd MMM yyyy"
            customHeader
            isDisabled={isDisabled}
            isClearable
            labels={{
              todayLabel,
              tomorrowLabel,
            }}
            datepickerStyles={{
              inputGroupStyles: {},
              datepickerInputElementStyles: inputStyle,
              iconStyles: inputIconStyles,
            }}
            onSelectDate={field.onChange}
          />

          {errMsg && (
            <Text style={dataErrorText} mt="2">
              {errMsg}
            </Text>
          )}
        </Box>
      );

    case 'autoComplete':
      return (
        <>
          <Box maxW={multiSelectMaxW} data-testid={formatDataTestId(formField.testid, testId)}>
            <MultiSelect
              options={nationalities}
              dataTestId={formatDataTestId(formField.testid, testId)}
              value={field.value}
              onChange={(fieldProp) =>
                handleNationalityChange(fieldProp, field, fieldToClear, handleSetValue)
              }
              placeholder={t(`precheckin.additionalfields.nationalities`)}
              styles={multiSelectStyles}
              isClearable
              isDisabled={isDisabled}
              closeMenuOnSelect
            />
          </Box>
          {errMsg && (
            <Text style={dataErrorText} mt="2">
              {errMsg}
            </Text>
          )}
        </>
      );

    default:
      return <></>;
  }
};

export default FieldController;

const multiSelectMaxW = {
  mobile: '100%',
  sm: '16.375rem',
  md: '21.75rem',
  lg: '24.5rem',
  xl: '26.25rem',
};

export const inputStyle = {
  height: 'var(--chakra-space-4xl)',
  borderColor: 'lightGrey1',
  borderRadius: 'var(--chakra-radii-md)',
} as StyleProps;
