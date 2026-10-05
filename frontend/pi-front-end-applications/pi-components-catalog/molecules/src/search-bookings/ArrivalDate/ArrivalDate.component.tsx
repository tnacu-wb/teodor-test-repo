import { Box, StyleProps } from '@chakra-ui/react';
import { SingleDatePicker } from '@whitbread-eos/atoms';
import { formatDataTestId, useCustomLocale } from '@whitbread-eos/utils';
import { add, sub } from 'date-fns';
import { Controller, useWatch } from 'react-hook-form';

export default function ArrivalDate({ formField, control }: any) {
  const testId = formField?.testid || 'ArrivalDate';
  const { language } = useCustomLocale();

  // Handle date reset from form
  const defaultStartDate = useWatch({ name: 'arrivalDate', control });
  const maxDate = add(new Date(), { days: 364 });
  const minDate = sub(new Date(), { days: 364 });

  return (
    <Box {...inputStyle} data-testid={formatDataTestId(testId, 'Container')}>
      <Controller
        name={formField?.name}
        control={control}
        render={({ field }) => {
          const { onChange } = field;
          return (
            <SingleDatePicker
              minDate={minDate}
              maxDate={maxDate}
              inputPlaceholder={formField?.label}
              inputLabel={formField?.label}
              name={formField?.name}
              datepickerStyles={{
                inputGroupStyles: {},
                datepickerInputElementStyles: inputStyle,
                bookingDatepickerSize: {},
                iconStyles: inputIconStyles,
              }}
              defaultStartDate={defaultStartDate}
              displayDateFormat="dd MMM yyyy"
              dateFormat="dd MMM yyyy"
              onSelectDate={(date: any) => {
                onChange(date);
              }}
              labels={{
                todayLabel: formField?.props?.singleDatePickerLabels.todayLabel,
                tomorrowLabel: formField?.props?.singleDatePickerLabels.tomorrowLabel,
              }}
              locale={language}
              data-testid={formatDataTestId(testId, 'datePicker')}
            />
          );
        }}
      />
    </Box>
  );
}

const inputStyle = {
  height: 'var(--chakra-space-4xl)',
  _placeholder: { opacity: 0.7, color: 'var(--chakra-darkGrey1)' },
  borderColor: 'var(--chakra-colors-lightGrey1)',
} as StyleProps;

const inputIconStyles = {
  top: 'var(--chakra-space-sm)',
};
