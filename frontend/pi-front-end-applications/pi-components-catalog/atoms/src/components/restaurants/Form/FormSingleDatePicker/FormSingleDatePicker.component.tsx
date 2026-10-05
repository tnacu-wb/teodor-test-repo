import { Flex, StyleProps, useMediaQuery } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { Controller } from 'react-hook-form';

import SingleDatePicker from '../../Calendar/SingleDatePicker.component';
import { SingleDatepickerDate } from '../../Calendar/types';
import FormError from '../FormError';
import { FormFieldProps } from '../formTypes';

function FormSingleDatePicker({ control, setValue, formField, errors, isEnquiry }: FormFieldProps) {
  const queryClient = useQueryClient();

  // Handle date picker state
  const [defaultStartDate] = useState<Date>(new Date());
  const todayLabel = 'Today';
  const tomorrowLabel = 'Tomorrow';
  const [isLargerThanSm] = useMediaQuery('(min-width: 576px)');
  return (
    <Flex
      direction="column"
      {...{ ...defaultStyles, ...formField.styles }}
      data-testid={formField.testid}
      data-fieldname={formField.name}
    >
      <Controller
        name={formField.name}
        control={control}
        render={({ field }) => {
          const { onChange, ...restOfField } = field;
          return (
            <SingleDatePicker
              {...restOfField}
              {...formField}
              isEnquiry={isEnquiry}
              setValue={setValue}
              isRightIcon={formField?.props?.isRightIcon as boolean}
              displayDateFormat="EEEE MMMM do yyyy"
              dateFormat="yyyy-MM-dd"
              defaultStartDate={defaultStartDate}
              labels={{
                todayLabel,
                tomorrowLabel,
              }}
              inputLabel={formField.label as string}
              popperPlacement={isLargerThanSm ? 'bottom' : 'top'}
              onSelectDate={(date: SingleDatepickerDate) => {
                setValue('time', '');
                onChange(date);
                queryClient.invalidateQueries({ queryKey: ['getSlots', date] });
              }}
              datepickerStyles={{
                inputGroupStyles: {},
                datepickerInputElementStyles: inputStyle,
                bookingDatepickerSize: inputIconStyles,
                iconStyles: {},
              }}
            />
          );
        }}
      />
      <FormError errors={errors} name={formField.name} />
    </Flex>
  );
}

const defaultStyles = {
  mb: 'var(--chakra-space-lg)',
};
const inputStyle = {
  h: 'var(--chakra-space-3xl)',
  _placeholder: { opacity: 0.7, color: 'var(--chakra-darkGrey1)' },
} as StyleProps;

const inputIconStyles = {
  top: 'var(--chakra-space-sm)',
};
export default FormSingleDatePicker;
