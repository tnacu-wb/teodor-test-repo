import {
  InputGroup,
  Input,
  useStyleConfig,
  FormControl,
  FormLabel,
  Box,
  InputRightElement,
  Flex,
} from '@chakra-ui/react';
// import { analytics } from '~utils/analytics';
import { formatDataTestId, isSameDate } from '@whitbread-eos/utils';
import classNames from 'classnames';
import { add, endOfMonth, endOfWeek, format, startOfMonth, startOfWeek } from 'date-fns';
import React, { useRef, useEffect, useState } from 'react';
import DatePicker from 'react-datepicker';

import Calendar from '../../../assets/icons/Calendar';
import DatePickerGlobalStyles from '../../../theme/components/DatePickerGlobalStyles';
import Icon from '../../Icon';
import {
  SingleDatePickerLabels,
  SingleDatepickerDate,
  SingleDatepickerStyles,
  SingleDatePickerProps,
} from './types';

export function getFormattedDate(
  startDate: SingleDatepickerDate,
  dateFormat: string,
  labels: SingleDatePickerLabels
) {
  const today = new Date();
  const tomorrow = add(today, { days: 1 });
  if (isSameDate(startDate, today)) return labels.todayLabel;

  if (isSameDate(startDate, tomorrow)) return labels.tomorrowLabel;

  return format(new Date(startDate), dateFormat);
}

const SingleDatePicker = (props: SingleDatePickerProps) => {
  const {
    inputLabel,
    name,
    isRightIcon,
    defaultStartDate,
    dataTestId = 'inputDatePicker',
    onSelectDate,
    setValue,
    popperPlacement,
    dateFormat,
    datepickerStyles,
    isEnquiry,
    displayDateFormat: showDateFormat,
    ...rest
  } = props;

  const baseDatepickerStyles = useStyleConfig('Datepicker');
  const ref = useRef<HTMLDivElement>(null);
  const displayLabel = inputLabel;
  const maxDate = defaultStartDate
    ? add(defaultStartDate as Date, { days: 364 })
    : add(new Date(), { days: 364 });
  const [startDate, setStartDate] = useState<SingleDatepickerDate>(
    defaultStartDate instanceof Date ? defaultStartDate : new Date()
  );
  const [minDate, setMinDate] = useState<SingleDatepickerDate>(
    defaultStartDate instanceof Date ? defaultStartDate : new Date()
  );

  const [isCalendarOpen, setIsCalendarOpen] = useState(false);
  const { inputBasicStyles, rightInputIconStyles } = getDatepickerStyles(datepickerStyles);

  const inputStyles = {
    ...inputBasicStyles(),
  };

  useEffect(() => {
    const checkIfClickedOutside = (event: MouseEvent) => {
      if (isCalendarOpen && ref.current && !ref.current.contains(event.target as HTMLElement))
        setIsCalendarOpen(false);
    };
    document.addEventListener('mousedown', checkIfClickedOutside);

    return () => {
      document.removeEventListener('mousedown', checkIfClickedOutside);
    };
  }, [isCalendarOpen]);

  useEffect(() => {
    const currentDate = new Date();
    if (isEnquiry) {
      currentDate.setDate(currentDate.getDate() + 1);
      setStartDate(currentDate);
      setValue(name, currentDate);
      setMinDate(currentDate);
      // analytics.update({
      //   date: format(new Date(currentDate), 'dd/MM/yyyy'),
      // });
    } else {
      setStartDate(currentDate);
      setValue(name, currentDate);
      setMinDate(currentDate);
      // analytics.update({
      //   date: format(new Date(currentDate), 'dd/MM/yyyy'),
      // });
    }
  }, [isEnquiry]);

  return (
    <Box __css={baseDatepickerStyles} ref={ref}>
      <DatePickerGlobalStyles />
      <DatePicker
        popperPlacement={popperPlacement}
        selected={startDate}
        startDate={startDate}
        onChange={(d: SingleDatepickerDate | null) => {
          if (d) {
            handleDatepickerChange(d);
          }
        }}
        customInput={renderSingleDateInput()}
        minDate={minDate}
        maxDate={maxDate}
        useWeekdaysShort={true}
        calendarStartDay={1}
        open={isCalendarOpen}
        renderDayContents={renderDayContents}
        dayClassName={getDayClassName}
        openToDate={startDate}
      />
    </Box>
  );
  function getDayClassName(date: Date) {
    const startWeek = startOfWeek(date, { weekStartsOn: 1 });
    const endWeek = endOfWeek(date, { weekStartsOn: 1 });
    const startMonth = startOfMonth(date);
    const endMonth = endOfMonth(date);
    const arr = [
      { 'start-week': startWeek.getDate() === date.getDate() },
      { 'end-week': endWeek.getDate() === date.getDate() },
      { 'start-month': startMonth.getDate() === date.getDate() },
      { 'end-month': endMonth.getDate() === date.getDate() },
      { 'react-datepicker__day--disabled': isDayDisabled(date) },
    ];
    return classNames(arr);
  }
  function isDayDisabled(date: Date) {
    const min = new Date(minDate.getTime());
    const resetMinDate = min.setHours(12, 0, 0, 0);
    return date.setHours(12, 0, 0, 0) < resetMinDate;
  }
  function renderDayContents(day: number) {
    return (
      <>
        <div className="highlight" />
        <div className="day-number">{day}</div>
      </>
    );
  }
  function handleDatepickerChange(d: SingleDatepickerDate) {
    setIsCalendarOpen(false);
    setStartDate(d);
    onSelectDate?.(d);
  }

  function renderSingleDateInput() {
    const displayDateFormat = showDateFormat ?? dateFormat;
    const value = getFormattedDate(startDate, displayDateFormat, props.labels);
    return (
      <FormControl ref={ref} {...rest}>
        <InputGroup display={'flex'} flexDirection={'column'}>
          {displayLabel && (
            <FormLabel
              data-testid={formatDataTestId(dataTestId, `${name}-label`)}
              {...labelStyle()}
              htmlFor={name}
            >
              {inputLabel}
            </FormLabel>
          )}
          <Flex position="relative">
            <Input
              size="lg"
              background={'#F4F4F4'}
              variant="filled"
              aria-label="datepicker-input"
              value={value}
              id={name}
              readOnly="readonly"
              onChange={(event) => {
                props?.onInputChange?.(event.target.value);
              }}
              onClick={() => {
                setIsCalendarOpen(true);
              }}
              onKeyDown={(e) => {
                if (e.key === ' ') {
                  e.preventDefault();
                  setIsCalendarOpen(true);
                }
              }}
              {...inputStyles}
              data-testid={formatDataTestId(dataTestId, 'singleDatePicker')}
            />
            {isRightIcon && (
              <InputRightElement pointerEvents="none" {...rightInputIconStyles()}>
                <Icon svg={<Calendar />} />
              </InputRightElement>
            )}
          </Flex>
        </InputGroup>
      </FormControl>
    );
  }
};
function getDatepickerStyles(datepickerStyles: SingleDatepickerStyles) {
  const inputBasicStyles = () => ({
    outline: 'none',
    cursor: 'pointer',
    border: '1px solid',
    borderColor: 'var(--chakra-colors-primary)',
    borderRadius: '4px',
    textOverflow: 'ellipsis',
    color: 'var(--chakra-colors-darkGrey2)',
    fontSize: 'var(--chakra-fontSizes-md)',
    _active: {
      border: '0.063rem solid var(--chakra-colors-primary)',
      borderRadius: 'var(--chakra-space-xs)',
      borderColor: 'var(--chakra-colors-primary)',
    },
    _target: {
      border: '0.063rem solid var(--chakra-colors-primary)',
      borderRadius: 'var(--chakra-space-xs)',
      borderColor: 'var(--chakra-colors-primary)',
    },
    _disabled: {
      border: 'none',
    },
    _hover: { borderColor: 'none' },
    _focus: {
      zIndex: '0',
      borderWidth: '2px',
      borderColor: 'var(--chakra-colors-primary)',
    },

    _placeholder: {
      color: 'darkGrey2',
    },
    ...datepickerStyles.datepickerInputElementStyles,
    ...datepickerStyles?.errorInputElementStyles,
  });

  const rightInputIconStyles = () => ({
    height: '100%',
    width: '0',
    right: '26px',
    ...datepickerStyles.iconStyles,
  });

  return { inputBasicStyles, rightInputIconStyles };
}

const labelStyle = () => ({
  w: 'fit-content',
  fontSize: 'md',
  fontWeight: 'bold',
  align: 'center',
  zIndex: '1',
  color: '#333333',
});

export default React.memo(SingleDatePicker);
