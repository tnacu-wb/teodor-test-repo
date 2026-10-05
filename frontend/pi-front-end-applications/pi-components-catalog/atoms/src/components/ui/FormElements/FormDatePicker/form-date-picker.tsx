'use client';

import { FormInnB } from '@whitbread-eos/api';
import { useState } from 'react';
import * as React from 'react';

import { SingleDatePickerUi } from '../../SingleDatePickerUi';
import { ErrorTooltip } from '../../Tooltip';

type Props = {
  id: string;
  name: string;
  value: Date;
  errors?: Record<string, any>;
  errorIcon: string;
  placeholder: string;
  onChange?: (value: Date | undefined) => void;
  onBlur?: () => void;
  onFocus?: () => void;
  locale: string;
  calendarLabels: FormInnB | Record<string, never>;
  icons: Record<string, string>;
  disableDays?: { before: Date; after: Date }[];
  disableMonthBefore?: Date;
  disableMonthAfter?: Date;
  containerClassName?: string;
  buttonClassName?: string;
};

const FormDatePicker = ({
  id,
  value,
  onChange = () => {
    return;
  },
  errors,
  name,
  errorIcon,
  locale,
  calendarLabels,
  icons,
  placeholder,
  disableMonthBefore,
  disableMonthAfter,
  disableDays,
  containerClassName,
  buttonClassName,
}: Props) => {
  const [date, setDate] = useState<Date | undefined>(value);
  const [currentMonthStart, setCurrentMonthStart] = useState<Date | undefined>(new Date());
  const error = errors?.[name];
  const hasError = !!error?.type;

  const handleDateChange = (newDate: Date | undefined) => {
    setDate(newDate);
    onChange(newDate);
  };

  return (
    <div className={containerClassName}>
      <SingleDatePickerUi
        fromMonth={disableMonthBefore}
        toMonth={disableMonthAfter}
        selectedDay={date}
        currentMonth={currentMonthStart}
        setCurrentMonth={setCurrentMonthStart}
        className={datePickerStyle}
        locale={locale}
        formLabels={calendarLabels}
        icons={icons}
        onDateChange={(value: Date | undefined) => {
          handleDateChange(value);
        }}
        showError={hasError ? 'error' : undefined}
        placeholder={placeholder}
        disabled={disableDays}
        buttonClassName={buttonClassName}
      />
      <ErrorTooltip
        icon={errorIcon}
        content={error?.message}
        open={hasError}
        testId={`${id}-Error-Tooltip`}
        className="!flex ib-word-break"
        mobile
      />
    </div>
  );
};
const datePickerStyle = 'max-h-[3.5rem]';

export { FormDatePicker };
