//This component is build on top of DatePicker component to handle it as Form fields
import React, { useEffect, useState } from 'react';

import Datepicker from '../Datepicker';
import FormError from '../Form/FormError';
import { type FormDynamicFieldCompProps } from '../Form/formTypes';

export default function DatePickerFormFieldComponent({
  field,
  handleSetValue,
  errors = {},
  formField: { props, errorStyles },
}: Readonly<FormDynamicFieldCompProps>) {
  const [isError, setIsError] = useState(false);
  useEffect(() => {
    setIsError(!!errors[field.name]);
  }, [errors[field.name]]);
  return (
    <>
      <Datepicker
        locale={props?.locale}
        minDate={new Date()}
        datepickerStyles={datepickerStyles}
        dateFormat={'dd MMM yyyy'}
        displayDateFormat="dd MMM yyyy"
        selectsRange={true}
        labels={{
          resetButtonLabel: props?.labels?.resetButtonLabel,
          doneButtonLabel: props?.labels?.doneButtonLabel,
          todayLabel: props?.labels?.todayLabel,
          tomorrowLabel: props?.labels?.tomorrowLabel,
          checkoutLabel: props?.labels?.checkoutLabel,
          checkInLabel: props?.labels?.checkInLabel,
        }}
        hasFooter={true}
        onSelectDates={(value) => handleSetValue?.(field.name, value)}
        displayDatesNotification={true}
        onReset={() => handleSetValue?.(field.name, [])}
        onDone={(value) => (value.includes(null) ? setIsError(true) : setIsError(false))}
      />
      {isError && <FormError errors={errors} name={field.name} extraStyles={errorStyles} />}
    </>
  );
}

const inputGroupStyles = {
  w: 'full',
  h: 'var(--chakra-space-4xl)',
  border: {
    base: '1px solid var(--chakra-colors-lightGrey1)',
    sm: 0,
  },
  borderRadius: {
    base: 'var(--chakra-space-xs)',
    sm: 0,
  },
};

const bookingDatepickerSize = {
  w: {
    base: '49%',
    sm: 'full',
    lg: '19.5rem',
    xl: '19.781rem',
  },
};

const datepickerInputElementStyles = {
  h: '100%',
  w: '100%',
  flex: { base: '1 1 50%', sm: '1 1 auto' },
  mr: '0',
  _placeholder: {
    color: 'var(--chakra-colors-darkGrey1)',
  },
  borderRight: '0.063rem solid var(--chakra-colors-lightGrey1)',
  border: '0.063rem solid var(--chakra-colors-lightGrey1)',
  borderRadius: 'var(--chakra-space-xs)',
  borderColor: 'var(--chakra-colors-lightGrey1)',
  borderTopRightRadius: 'var(--chakra-space-xs)',
  borderBottomRightRadius: 'var(--chakra-space-xs)',
};

const iconStyles = {
  top: 'var(--chakra-space-sm)',
};
const datepickerStyles = {
  inputGroupStyles,
  bookingDatepickerSize,
  datepickerInputElementStyles,
  iconStyles,
};
