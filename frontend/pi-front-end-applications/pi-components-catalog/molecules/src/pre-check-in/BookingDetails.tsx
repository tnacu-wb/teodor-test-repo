import { Box, Text, Flex, useMediaQuery, Button, useToken, StyleProps } from '@chakra-ui/react';
import { Input, SingleDatePicker, Error, Notification } from '@whitbread-eos/atoms';
import { formatDataTestId, useCustomLocale } from '@whitbread-eos/utils';
import { add } from 'date-fns';
import { useTranslation } from 'next-i18next';
import { Controller, ControllerRenderProps } from 'react-hook-form';

import {
  fieldProps,
  inputStyle,
  inputIconStyles,
  dataErrorText,
  todayLabel,
  tomorrowLabel,
  bookingDetailsFields,
  searchBookingGridStyles,
} from './common';

interface FieldController {
  field: ControllerRenderProps;
  label: string;
  name: string;
  type: string;
  testId: string;
}

const BookingDetails = ({ control, formField, errors }: any) => {
  const arrivalMinDate = new Date();
  const arrivalMaxDate = add(arrivalMinDate, { years: 1 });
  const { language } = useCustomLocale();
  const { t } = useTranslation();
  const [isLargerThanSm] = useMediaQuery('(min-width: 576px)');
  const [darkGrey2] = useToken('colors', ['darkGrey2']);

  const { bookingReferenceError } = formField.props;

  let datePickerStyles = { ...inputStyle, _placeholder: { color: darkGrey2 } };

  const fieldController = ({ field, label, name, type, testId }: FieldController) => {
    switch (type) {
      case 'input': {
        const labelAndPlaceHolderText =
          label === 'bookingnumber' ? t(`precheckin.${label}`) : t(`precheckin.details.${label}`);
        return (
          <Input
            {...formField.props}
            {...fieldProps(field)}
            type="text"
            showIcon={false}
            placeholderText={labelAndPlaceHolderText}
            label={labelAndPlaceHolderText}
            error={errors?.[name]?.message}
            className="sessioncamhidetext assist-no-show"
          />
        );
      }
      case 'datePicker': {
        const { onChange } = field;
        if (errors?.[name]?.message) {
          datePickerStyles = {
            ...datePickerStyles,
            border: '1px solid var(--chakra-colors-error)',
            borderColor: 'none',
          };
        }
        return (
          <>
            <SingleDatePicker
              {...(language === 'de' ? { locale: 'de' } : { locale: 'en' })}
              name={'arrivalDate'}
              dataTestId={formatDataTestId(formField.testid, testId)}
              minDate={arrivalMinDate}
              maxDate={arrivalMaxDate}
              inputPlaceholder={t(`precheckin.details.${label}`)}
              inputLabel={t(`precheckin.details.${label}`)}
              defaultStartDate={field.value}
              isRightIcon
              popperPlacement={isLargerThanSm ? 'bottom' : 'top'}
              displayDateFormat="dd MMM yyyy"
              dateFormat="dd MMM yyyy"
              labels={{
                todayLabel,
                tomorrowLabel,
              }}
              datepickerStyles={{
                inputGroupStyles: {},
                datepickerInputElementStyles: datePickerStyles,
                bookingDatepickerSize: {},
                iconStyles: inputIconStyles,
              }}
              onSelectDate={(date) => {
                onChange(date);
              }}
            />
            {errors?.[name]?.message && (
              <Text style={dataErrorText} mt="2">
                {errors?.arrivalDate?.message}
              </Text>
            )}
          </>
        );
      }
      default:
        return <></>;
    }
  };

  return (
    <Flex direction="column" gap="xl" mb="18em">
      {!!bookingReferenceError && (
        <Notification
          svg={<Error />}
          status="error"
          description={t('precheckin.details.error')}
          variant="error"
          prefixDataTestId={'BookingDetails'}
        />
      )}
      <Flex wrap="wrap" justifyContent={'space-between'}>
        {bookingDetailsFields.map(({ label, name, testId, type }) => (
          <Box
            {...searchBookingGridStyles}
            key={name}
            data-testid={formatDataTestId(formField.testid, testId)}
          >
            <Controller
              name={name}
              control={control}
              render={({ field }) => fieldController({ field, label, name, type, testId })}
            />
          </Box>
        ))}
        <Box
          {...searchBookingGridStyles}
          data-testid={formatDataTestId(formField.testid, 'searchButtonWrapper')}
        >
          <Button
            data-testid="submit-reg-booking-details-form"
            form="preCheckInBookingDetailsForm"
            type="submit"
            {...btnStyles}
            size="md"
          >
            {t('precheckin.searchbutton')}
          </Button>
        </Box>
      </Flex>
    </Flex>
  );
};

const btnStyles = {
  w: { base: '100%', md: 'var(--chakra-sizes-64)' },
  pr: 0,
  pl: 0,
} as StyleProps;

export default BookingDetails;
