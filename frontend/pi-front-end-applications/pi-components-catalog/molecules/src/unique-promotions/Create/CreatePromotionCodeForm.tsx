import { Button, Flex, Text, useMediaQuery, Box, useToken } from '@chakra-ui/react';
import { Checkbox, Input, SingleDatePicker } from '@whitbread-eos/atoms';
import { formatDataTestId, useCustomLocale, usePromoTranslation } from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import { useEffect, useMemo } from 'react';
import { Controller, useWatch } from 'react-hook-form';

import HotelIdAutocompleteField from './HotelIdAutocomplete.component';
import PlatformConfigField from './PlatformConfiguration';
import { isPlatformConfigValid } from './PlatformConfiguration/common';
import {
  todayLabel,
  tomorrowLabel,
  fieldProps,
  getCreatePromotionFormFields,
  handlePromoApiError,
} from './common';
import { inputStyle, styles } from './styles';
import { CreatePromtionCodeFormDetailsProps, FieldController } from './types';
import { useCreatePromoCodeFormContext } from './useCreatePromotionCodeFormContext';

const CreatePromotionCodeForm = ({
  control,
  formField,
  errors,
  getValues,
  handleSetError,
  clearErrors,
  setValue,
  handleSetValue,
  handleClearErrors,
}: CreatePromtionCodeFormDetailsProps) => {
  const t = usePromoTranslation();
  const router = useRouter();
  const [isLargerThanSm] = useMediaQuery('(min-width: 576px)');
  const { country, language } = useCustomLocale();
  const { createPromoError, createPromoIsError, setLoadingTransition } =
    useCreatePromoCodeFormContext();
  const [darkGrey1] = useToken('colors', ['darkGrey1']);

  const expiryDate = useMemo(() => new Date(), []);
  const createPromotionFormFields = useMemo(() => getCreatePromotionFormFields(t), [t]);

  const values = getValues();
  const isGeneric = useWatch({ control, name: 'isGeneric' });
  const isLimitRedemptions = useWatch({ control, name: 'isLimitRedemptions' });

  const goBackToBatchesList = () => {
    setLoadingTransition(true);
    router.replace(`/${country}/${language}/unique-promotions/list`)?.finally(() => {
      setTimeout(() => setLoadingTransition(false), 150);
    });
  };

  let datePickerStyles = {
    ...inputStyle,
    _placeholder: { color: darkGrey1, opacity: 0.8 },
  };
  if (errors?.expiryDate?.message) {
    datePickerStyles = {
      ...datePickerStyles,
      border: '1px solid var(--chakra-colors-error)',
      borderColor: 'none',
    };
  }

  const isFieldVisible = (name: string) => {
    if (name === 'prefix') return !isGeneric;
    if (name === 'genericPromoCode') return !!isGeneric;
    if (name === 'isLimitRedemptions') return !!isGeneric;
    if (name === 'batchCount') return !isGeneric;
    if (name === 'maxRedemptionLimit') return !!isGeneric && !!isLimitRedemptions;
    return true;
  };

  useEffect(() => {
    if (isGeneric) {
      if (clearErrors) {
        clearErrors(['prefix', 'batchCount']);
      }
      if (setValue) {
        setValue('prefix', '');
        setValue('batchCount', undefined);
      }
    } else {
      if (clearErrors) {
        clearErrors(['genericPromoCode', 'isLimitRedemptions', 'maxRedemptionLimit']);
      }
      if (setValue) {
        setValue('genericPromoCode', '');
        setValue('isLimitRedemptions', false);
        setValue('maxRedemptionLimit', undefined);
      }
    }
  }, [isGeneric, clearErrors, setValue]);

  const fieldController = ({ field, label, name, type, testId, isDisabled }: FieldController) => {
    const errorMessage = errors?.[name]?.message as string | undefined;

    if (type === 'text' || type === 'number') {
      return (
        <Input
          {...fieldProps(field)}
          {...formField}
          type={type}
          showIcon={false}
          placeholderText={label}
          label={label}
          error={errorMessage}
          className="assist-no-show"
          isDisabled={isDisabled}
        />
      );
    }
    if (type === 'check') {
      return (
        <Checkbox
          {...fieldProps(field)}
          {...formField}
          isChecked={field.value}
          onChange={(e) => field.onChange(e.target.checked)}
          data-testid={formatDataTestId(formField.testid, testId)}
        >
          {label}
        </Checkbox>
      );
    }
    if (type === 'platformConfig') {
      return (
        <PlatformConfigField
          value={field.value}
          onChange={field.onChange}
          onBlur={field.onBlur}
          label={t.platformConfigurationTitle}
          description={t.configurePlatformText}
          testId={formatDataTestId(formField.testid, testId)}
        />
      );
    }
    if (type === 'datePicker') {
      const datepickerLabels = { todayLabel, tomorrowLabel };
      return (
        <Box
          onBlurCapture={() => {
            field.onBlur();
          }}
        >
          <SingleDatePicker
            {...(language === 'de' ? { locale: 'de' } : { locale: 'en' })}
            data-testid={formatDataTestId(formField.testid, testId)}
            name="expiryDate"
            minDate={expiryDate}
            inputPlaceholder={label}
            inputLabel={label}
            defaultStartDate={field.value ?? null}
            isRightIcon
            popperPlacement={isLargerThanSm ? 'bottom' : 'top'}
            displayDateFormat="dd MMM yyyy"
            dateFormat="dd MMM yyyy"
            labels={datepickerLabels}
            datepickerStyles={{
              inputGroupStyles: {},
              datepickerInputElementStyles: datePickerStyles,
              iconStyles: styles.inputIconStyles,
            }}
            onSelectDate={(date) => {
              field.onChange(date);
              field.onBlur();
            }}
            isError={errorMessage ? true : false}
          />
          {errorMessage && <Text sx={styles.dataErrorText}>{errorMessage}</Text>}
        </Box>
      );
    }
    if (type === 'autoComplete') {
      return (
        <HotelIdAutocompleteField
          formField={{
            ...formField,
            name,
            label,
            type,
            testid: formatDataTestId(formField.testid, testId),
          }}
          field={field}
          errors={errors}
          handleSetValue={handleSetValue ?? setValue}
          handleSetError={handleSetError}
          handleClearErrors={handleClearErrors ?? clearErrors}
        />
      );
    }

    return <></>;
  };

  const visibleErrors = Object.keys(errors).filter((fieldName) => isFieldVisible(fieldName));
  const hasVisibleErrors = visibleErrors.length > 0;

  const isGenericValid = isGeneric
    ? Boolean(values.genericPromoCode) &&
      (!isLimitRedemptions ||
        (values.maxRedemptionLimit >= 1 && values.maxRedemptionLimit <= 1000000))
    : Boolean(values.prefix && values.batchCount);

  const isValid =
    !hasVisibleErrors &&
    values.campaignName &&
    values.operaPromoCode &&
    values.hotelId &&
    values.expiryDate &&
    isGenericValid &&
    isPlatformConfigValid(values.platformConfig);

  useEffect(() => {
    handlePromoApiError({
      createPromoIsError,
      createPromoError,
      duplicatePrefixError: t.duplicatePrefixError,
      expiryDateInvalidError: t.endDateInvalidError,
      promotionsInvalidError: t.promotionsInvalidError,
      handleSetError,
    });
  }, [createPromoIsError, createPromoError]);

  return (
    <Flex sx={styles.createPromotionContainerStyles} as="form" id="createPromotionCodeForm">
      {createPromotionFormFields
        .filter(({ name }) => isFieldVisible(name))
        .map(({ label, name, testId, type, isDisabled }) => (
          <Box key={name} data-testid={formatDataTestId(formField.testid, testId)}>
            <Controller
              name={name}
              control={control}
              render={({ field }) => {
                const modifiedField =
                  name === 'operaPromoCode' || name === 'prefix' || name === 'genericPromoCode'
                    ? {
                        ...field,
                        value: field.value || '',
                        onChange: (e: any) => {
                          const value = e?.target?.value || e;
                          const upperValue = value.toUpperCase();
                          field.onChange(upperValue);
                        },
                      }
                    : name === 'maxRedemptionLimit'
                      ? {
                          ...field,
                          onChange: (e: any) => {
                            const raw = e?.target?.value ?? e;
                            if (raw === '') {
                              field.onChange('');
                              return;
                            }
                            const numeric = Number(raw);
                            if (Number.isNaN(numeric)) return;
                            const clamped = Math.min(1000000, Math.max(1, numeric));
                            field.onChange(clamped);
                          },
                        }
                      : field;

                return fieldController({
                  field: modifiedField,
                  label,
                  name,
                  type,
                  testId,
                  isDisabled,
                });
              }}
            />
          </Box>
        ))}

      <Box
        sx={styles.createPromoButtonStyles}
        data-testid={formatDataTestId(formField.testid, 'creatPromotionCodeButtonWrapper')}
      >
        <Button
          data-testid="cancel-create-promotion-code-form"
          form="createPromotionCodeForm"
          type="button"
          sx={styles.btnCancelStyles}
          onClick={goBackToBatchesList}
        >
          {t.cancelButtonText}
        </Button>

        <Button
          data-testid="submit-create-promotion-code-form"
          form="createPromotionCodeForm"
          type="submit"
          sx={styles.btnSubmitStyles}
          isDisabled={!isValid}
          variant="secondary"
        >
          {t.generateButtonText}
        </Button>
      </Box>
    </Flex>
  );
};

export default CreatePromotionCodeForm;
