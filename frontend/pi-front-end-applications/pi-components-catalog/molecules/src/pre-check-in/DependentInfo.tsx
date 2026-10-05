import { Box, Flex, Text, useMediaQuery } from '@chakra-ui/react';
import {
  Button,
  FieldsType,
  Input,
  Line,
  MultiSelect,
  SingleDatePicker,
} from '@whitbread-eos/atoms';
import { formatDataTestId, useCustomLocale } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { Control, Controller, ControllerRenderProps, FieldErrors } from 'react-hook-form';

import { DATE_FORMAT } from '../utils/constants';
import {
  bookDetailGridStyles,
  dataErrorText,
  dependencyFields,
  fieldProps,
  getNationality,
  handleNationalityChange,
  inputIconStyles,
  inputStyle,
  showDependencyPassportField,
  todayLabel,
  tomorrowLabel,
} from './common';

interface DependentsError {
  dependents: FieldErrors<{ [key: string]: { [key: string]: string | number } }>;
}

interface Props {
  getValues: () => void;
  index: number;
  removeDependent: (index: number) => void;
  control: Control;
  formField: FieldsType;
  errors: DependentsError;
  nationalities: {
    value: string;
  }[];
  handleSetValue: (fieldName: string) => void;
}

interface FieldController {
  field: ControllerRenderProps;
  label: string;
  name: string;
  type: string;
  testId: string;
}

const DependentInfo = ({
  getValues,
  index: dependentIndex,
  control,
  removeDependent,
  nationalities,
  formField,
  errors,
  handleSetValue,
}: Props) => {
  const { t } = useTranslation();
  const { language } = useCustomLocale();
  const [isLargerThanSm] = useMediaQuery('(min-width: 576px)');

  let datePickerStyles = {
    ...inputStyle,
  };

  if (errors?.dependents?.[dependentIndex]?.['dateofbirth']?.message) {
    datePickerStyles = {
      ...datePickerStyles,
      border: '1px solid var(--chakra-colors-error)',
      borderColor: 'none',
    };
  }

  const fieldController = ({ field, label, name, type }: FieldController) => {
    switch (type) {
      case 'input':
        return (
          <Input
            {...formField.props}
            {...fieldProps(field)}
            type="text"
            showIcon={false}
            placeholderText={t(`precheckin.details.${label}`)}
            label={t(`precheckin.details.${label}`)}
            error={errors?.dependents?.[dependentIndex]?.[name]?.message}
            className="sessioncamhidetext assist-no-show"
          />
        );
      case 'datePicker':
        return (
          <>
            <SingleDatePicker
              {...(language === 'de' ? { locale: 'de' } : { locale: 'en' })}
              {...formField.props}
              {...fieldProps(field)}
              minDate={new Date(new Date().getFullYear() - 100, 0, 1)} // 100 years ago
              maxDate={new Date()} // Current date
              inputPlaceholder={t(`precheckin.details.${label}`)}
              inputLabel={t(`precheckin.details.${label}`)}
              defaultStartDate={field.value || null}
              isRightIcon
              popperPlacement={isLargerThanSm ? 'bottom' : 'top'}
              displayDateFormat={DATE_FORMAT}
              dateFormat={DATE_FORMAT}
              customHeader
              labels={{
                todayLabel,
                tomorrowLabel,
              }}
              datepickerStyles={{
                inputGroupStyles: {},
                datepickerInputElementStyles: datePickerStyles,
                iconStyles: inputIconStyles,
              }}
              onSelectDate={field.onChange}
              isError={!!errors?.dependents?.[dependentIndex]?.[name]?.message}
            />
            {errors?.dependents?.[dependentIndex]?.[name]?.message && (
              <Text style={dataErrorText} mt="2">
                {errors?.dependents?.[dependentIndex]?.[name]?.message}
              </Text>
            )}
          </>
        );
      case 'autoComplete':
        return (
          <>
            <MultiSelect
              name={name}
              options={nationalities}
              dataTestId={formatDataTestId(formField.testid, formField.testid)}
              value={getNationality(field.value, nationalities)}
              onChange={(fieldProp) =>
                handleNationalityChange(
                  fieldProp,
                  field,
                  `dependents[${dependentIndex}].passport`,
                  handleSetValue
                )
              }
              placeholder={t(`precheckin.details.${label}`)}
              styles={{
                control: (baseStyles) => ({
                  ...baseStyles,
                  border: errors?.dependents?.[dependentIndex]?.[label]
                    ? '2px solid var(--chakra-colors-error)'
                    : '1px solid var(--chakra-colors-lightGrey1)',
                  minHeight: 'var(--chakra-space-4xl)',
                  fontSize: 'var(--chakra-fontSizes-md)',
                }),
              }}
              isClearable={true}
            />
            {errors?.dependents?.[dependentIndex]?.[label] && (
              <Text style={dataErrorText} mt="2">
                {t('precheckin.errors.empty.nationality')}
              </Text>
            )}
          </>
        );

      default:
        return <></>;
    }
  };

  const fieldValues = getValues();

  return (
    <Flex wrap="wrap" justifyContent="space-between" flexDirection="column">
      <Box
        {...bookDetailGridStyles}
        mb={0}
        data-testid={formatDataTestId(formField.testid, `dependents-${dependentIndex}-guest`)}
      >
        <Box {...BoxStyle}>
          <Flex wrap="nowrap" justifyContent="space-between" flexDirection="row">
            <Text {...titleStyle}>{`${t('precheckin.guest')}  ${dependentIndex + 1}`}</Text>
            <DeleteBtn
              removeDependent={() => removeDependent(dependentIndex)}
              data-testid={`${t('deleteguest')}  ${dependentIndex + 1}`}
            />
          </Flex>
        </Box>
        {dependencyFields.map(({ label, name, testId, type }) => {
          const formattedName = `dependents[${dependentIndex}].${name}`;

          return (
            !!(
              name !== 'passport' || !showDependencyPassportField(fieldValues, dependentIndex)
            ) && (
              <Box
                {...bookDetailGridStyles}
                data-testid={formatDataTestId(
                  formField.testid,
                  `dependents-${dependentIndex}-${testId}`
                )}
                key={`dependents[${dependentIndex}].${name}`}
              >
                <Controller
                  name={formattedName}
                  control={control}
                  render={({ field }) => fieldController({ field, label, name, type, testId })}
                />
              </Box>
            )
          );
        })}
      </Box>
    </Flex>
  );
};

export default DependentInfo;

const DeleteBtn = ({ removeDependent }: { removeDependent: () => void }) => (
  <Button type="button" variant="circle" size="sm" {...deleteBtnStyle} onClick={removeDependent}>
    <Line />
  </Button>
);

const deleteBtnStyle = {
  width: '8',
  height: '8',
  borderRadius: '50%',
  borderColor: 'primary',
  color: 'primary',
};

const titleStyle = {
  fontSize: 'xl',
  fontWeight: 'semibold',
  color: 'baseBlack',
};

const BoxStyle = {
  verticalAlign: 'middle',
};
