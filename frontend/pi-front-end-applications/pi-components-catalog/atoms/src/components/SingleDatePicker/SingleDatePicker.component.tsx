import { CloseIcon } from '@chakra-ui/icons';
import {
  Box,
  FormControl,
  FormLabel,
  Input,
  InputGroup,
  InputLeftElement,
  InputRightElement,
  useStyleConfig,
} from '@chakra-ui/react';
import { formatDate, isSameDate } from '@whitbread-eos/utils';
import classNames from 'classnames';
import { add, endOfMonth, endOfWeek, format, startOfMonth, startOfWeek } from 'date-fns';
import { de } from 'date-fns/locale';
import { ReactNode, useEffect, useRef, useState } from 'react';
import DatePicker, { ReactDatePickerCustomHeaderProps, registerLocale } from 'react-datepicker';

import Calendar from '../../assets/icons/Calendar';
import DatePickerGlobalStyles from '../../theme/components/DatePickerGlobalStyles';
import { formatDataTestId } from '../../utils/formatters';
import Icon from '../Icon';
import CustomHeader from './CustomHeader';
import {
  datepickerDate,
  datepickerStyles,
  SingleDatePickerLabels,
  SingleDatePickerProps,
} from './types';

export function getFormattedDate(
  startDate: datepickerDate,
  dateFormat: string,
  labels: SingleDatePickerLabels,
  skipFormatRules?: boolean | undefined,
  locale?: string
) {
  if (skipFormatRules) {
    return startDate ? format(startDate, dateFormat) : '';
  }
  const today = new Date();
  const tomorrow = add(today, { days: 1 });
  if (isSameDate(startDate, today)) {
    return `${labels.todayLabel}`;
  }
  if (isSameDate(startDate, tomorrow)) {
    return `${labels.tomorrowLabel}`;
  }
  return startDate ? formatDate(startDate.toDateString(), dateFormat, locale) : '';
}

export default function SingleDatePicker(props: Readonly<SingleDatePickerProps>) {
  const {
    inputLabel,
    name,
    isDisabled,
    inputPlaceholder,
    defaultStartDate,
    isError,
    dataTestId,
    isRightIcon,
    onSelectDate,
    locale,
    popperPlacement,
    skipFormatRules,
    minDate,
    maxDate,
    dateFormat,
    datepickerStyles,
    displayDateFormat: showDateFormat,
    customHeader,
    isClearable,
    openToDate,
    ...rest
  } = props;

  const baseDatepickerStyles = useStyleConfig('Datepicker');
  registerLocale('de', de);
  const [startDate, setStartDate] = useState<datepickerDate>(
    defaultStartDate instanceof Date ? defaultStartDate : null
  );
  const ref = useRef<HTMLDivElement>(null);
  const [isCalendarOpen, setIsCalendarOpen] = useState(false);
  const { inputBasicStyles, rightInputIconStyles, leftInputIconStyles, clearIconStyles } =
    getDatepickerStyles(datepickerStyles);

  const inputDisabledStyles = isDisabled ? disabledStyles : undefined;

  const inputStyles = {
    ...inputBasicStyles(isError, isRightIcon),
    ...inputDisabledStyles,
  };

  const displayLabel = inputLabel;

  useEffect(() => {
    const checkIfClickedOutside = (event: MouseEvent) => {
      if (isCalendarOpen && ref.current && !ref.current.contains(event.target as HTMLElement)) {
        setIsCalendarOpen(false);
      }
    };
    document.addEventListener('mousedown', checkIfClickedOutside);

    return () => {
      document.removeEventListener('mousedown', checkIfClickedOutside);
    };
  }, [isCalendarOpen]);

  // Handle date reset from container
  useEffect(() => {
    if (!defaultStartDate) {
      setStartDate(null);
    }
  }, [defaultStartDate]);

  return (
    <Box __css={baseDatepickerStyles} ref={ref} {...datepickerStyles.datepickerInputElementStyles}>
      <DatePickerGlobalStyles />
      <DatePicker
        locale={locale}
        popperPlacement={popperPlacement}
        selected={startDate}
        startDate={startDate}
        onChange={(d: datepickerDate) => {
          handleDatepickerChange(d);
        }}
        renderCustomHeader={customHeader ? renderCustomHeader : undefined}
        customInput={renderSingleDateInput()}
        minDate={minDate}
        maxDate={maxDate}
        useWeekdaysShort={true}
        calendarStartDay={1}
        disabled={props.isDisabled}
        open={isCalendarOpen}
        renderDayContents={renderDayContents}
        dayClassName={getDayClassName}
        openToDate={startDate ?? (openToDate || new Date())}
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
    if (!startDate) return false;
    const resetMinDate = minDate.setHours(12, 0, 0, 0);
    return date.setHours(12, 0, 0, 0) < resetMinDate;
  }

  function renderSingleDateInput() {
    const displayDateFormat = showDateFormat ?? dateFormat;
    const value = getFormattedDate(
      startDate,
      displayDateFormat,
      props.labels,
      skipFormatRules,
      locale
    );

    return (
      <FormControl ref={ref} {...rest}>
        <InputGroup>
          {isRightIcon ? (
            <InputRightElement pointerEvents="none" {...rightInputIconStyles(isDisabled)}>
              <Icon svg={<Calendar />} />
            </InputRightElement>
          ) : (
            <InputLeftElement pointerEvents="none" {...leftInputIconStyles(isDisabled)}>
              <Icon svg={<Calendar />} />
            </InputLeftElement>
          )}

          {isClearable && value && (
            <InputRightElement
              {...clearIconStyles(isRightIcon)}
              onClick={() => {
                setStartDate(null);
                onSelectDate?.(null);
              }}
            >
              <Icon svg={<CloseIcon w="3" color="lightGrey1" />} />
            </InputRightElement>
          )}
          {displayLabel && (
            <FormLabel
              data-testid={formatDataTestId(dataTestId, `${name}-label`)}
              pos="absolute"
              {...labelStyle(isError, value)}
              htmlFor={name}
            >
              {inputLabel}
            </FormLabel>
          )}
          <Input
            placeholder={inputPlaceholder || inputLabel}
            aria-label="datepicker-input"
            value={value}
            onChange={(event) => {
              props?.onInputChange?.(event.target.value);
            }}
            isDisabled={isDisabled}
            onClick={() => {
              setIsCalendarOpen(true);
            }}
            {...inputStyles}
            data-testid={formatDataTestId(dataTestId, 'SingleDatePicker')}
          />
        </InputGroup>
      </FormControl>
    );
  }

  function renderDayContents(day: number): ReactNode {
    return (
      <>
        <div className="highlight" />
        <div className="day-number">{day}</div>
      </>
    );
  }

  function handleDatepickerChange(d: datepickerDate) {
    setIsCalendarOpen(false);
    setStartDate(d);
    onSelectDate?.(d);
  }
}

function getDatepickerStyles(datepickerStyles: datepickerStyles) {
  const inputBasicStyles = (error: boolean | undefined, isRightIcon: boolean | undefined) => {
    return {
      padding: 'var(--chakra-space-lg) var(--chakra-space-lg) var(--chakra-space-lg) 0',
      paddingLeft: isRightIcon ? 'var(--chakra-space-md)' : 'var(--chakra-space-2xl)',
      outline: 'none',

      cursor: 'pointer',
      border: '0.063rem solid var(--chakra-colors-lightGrey1)',

      borderRadius: 'var(--chakra-space-xs)',
      borderTopRightRadius: 'var(--chakra-space-xs)',
      borderBottomRightRadius: 'var(--chakra-space-xs)',
      textOverflow: 'ellipsis',
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
      _focus: { zIndex: '0', borderWidth: '2px', borderColor: error ? 'error' : 'primary' },

      _placeholder: {
        color: 'darkGrey2',
      },
      ...datepickerStyles.datepickerInputElementStyles,
      ...datepickerStyles?.errorInputElementStyles,
    };
  };

  const rightInputIconStyles = (isDisabled: boolean | undefined) => {
    return {
      top: '0.313rem',
      right: 'var(--chakra-space-sm)',
      ...datepickerStyles.iconStyles,
      opacity: isDisabled ? '0.4' : 'unset',
    };
  };

  const leftInputIconStyles = (isDisabled: boolean | undefined) => {
    return {
      top: 'var(--chakra-space-md)',
      ...datepickerStyles.iconStyles,
      opacity: isDisabled ? '0.4' : 'unset',
    };
  };

  const clearIconStyles = (isRightIcon: boolean | undefined) => ({
    ...datepickerStyles.iconStyles,
    right: isRightIcon ? '2xl' : 'sm',
  });

  return { inputBasicStyles, rightInputIconStyles, leftInputIconStyles, clearIconStyles };
}

function renderCustomHeader({
  date,
  changeYear,
  changeMonth,
  decreaseMonth,
  increaseMonth,
  prevMonthButtonDisabled,
  nextMonthButtonDisabled,
}: ReactDatePickerCustomHeaderProps) {
  return (
    <CustomHeader
      date={date}
      changeYear={changeYear}
      changeMonth={changeMonth}
      decreaseMonth={decreaseMonth}
      increaseMonth={increaseMonth}
      prevMonthButtonDisabled={prevMonthButtonDisabled}
      nextMonthButtonDisabled={nextMonthButtonDisabled}
    />
  );
}

const labelStyle = (error: boolean | undefined, value: string) => ({
  h: '1.25rem',
  w: 'fit-content',
  fontSize: 'sm',
  fontWeight: 'normal',
  align: 'center',
  px: 'xs',
  ml: '0.750rem',
  top: '-0.625rem',
  backgroundColor: 'baseWhite',
  zIndex: '1',
  color: error ? 'error' : 'darkGrey1',
  display: value ? 'block' : 'none',
  _focus: { color: error ? 'error' : 'primary', display: 'block' },
});

const disabledStyles = {
  _disabled: {
    opacity: '0.4',
    cursor: 'not-allowed',
  },
  _active: {
    opacity: '0.4',
  },
};
