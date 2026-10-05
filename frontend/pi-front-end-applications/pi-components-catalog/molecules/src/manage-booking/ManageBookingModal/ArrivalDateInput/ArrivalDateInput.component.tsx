import { Box, StyleProps, useMediaQuery } from '@chakra-ui/react';
import { SingleDatePicker } from '@whitbread-eos/atoms';
import { formatDataTestId, useCustomLocale } from '@whitbread-eos/utils';
import { add } from 'date-fns';
import { Controller } from 'react-hook-form';

export type ManageBookingDateType = Date | null;

export default function ArrivalDate({ formField, control }: any) {
  const testId = formField?.testid;
  const defaultDate = new Date();
  const todayLabel = formField.props?.todayLabel || 'Today';
  const tomorrowLabel = formField.props?.tomorrowLabel || 'Tomorrow';
  const maxDate = add(new Date(), { days: 364 });
  const { language } = useCustomLocale();
  const [isLargerThanSm] = useMediaQuery('(min-width: 576px)');

  return (
    <Box data-testid={formatDataTestId(testId, 'Container')}>
      <Controller
        name={formField?.name}
        control={control}
        render={({ field }) => {
          const { onChange } = field;
          return (
            <SingleDatePicker
              {...(language === 'de' ? { locale: 'de' } : { locale: 'en' })}
              name={formField?.name}
              minDate={defaultDate}
              maxDate={maxDate}
              inputPlaceholder={formField?.label}
              inputLabel={formField?.label}
              defaultStartDate={defaultDate}
              isRightIcon
              popperPlacement={isLargerThanSm ? 'bottom' : 'top'}
              displayDateFormat="EEE d MMMM yyyy"
              dateFormat="dd MMM yyyy"
              labels={{
                todayLabel,
                tomorrowLabel,
              }}
              datepickerStyles={{
                inputGroupStyles: {},
                datepickerInputElementStyles: inputStyle,
                bookingDatepickerSize: {},
                iconStyles: inputIconStyles,
              }}
              onSelectDate={(date) => {
                onChange(date);
              }}
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
} as StyleProps;

const inputIconStyles = {
  top: 'var(--chakra-space-sm)',
};
