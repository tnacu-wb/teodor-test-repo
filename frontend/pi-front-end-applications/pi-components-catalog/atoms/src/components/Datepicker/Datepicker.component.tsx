import {
  Box,
  Flex,
  Input,
  InputGroup,
  InputLeftElement,
  useMediaQuery,
  useOutsideClick,
  useStyleConfig,
} from '@chakra-ui/react';
import type { DatepickerRangeSelectionDate, DatepickerSelectionDate } from '@whitbread-eos/api';
import { CountryCode, DATE_TYPE, FT_PI_TWO_MONTH_SEARCH } from '@whitbread-eos/api';
import { getCookie, isSameDate, TWO_MONTH_SEARCH, useFeatureToggle } from '@whitbread-eos/utils';
import classNames from 'classnames';
import { add, endOfMonth, endOfWeek, format, startOfMonth, startOfWeek } from 'date-fns';
import { de, enGB } from 'date-fns/locale';
import { nanoid } from 'nanoid';
import { ReactNode, useEffect, useRef, useState } from 'react';
import DatePicker, { registerLocale } from 'react-datepicker';

import Calendar from '../../assets/icons/Calendar';
import DatePickerGlobalStyles from '../../theme/components/DatePickerGlobalStyles';
import Button from '../Button';
import Icon from '../Icon';
import { datepickerDate, datepickerLabels, datepickerStyles } from './types';

interface DatepickerProps {
  minDate: Date;
  maxDate?: Date;
  defaultStartDate?: datepickerDate;
  defaultEndDate?: datepickerDate;
  inputPlaceholder?: string;
  dateFormat: string;
  displayDateFormat: string;
  labels?: datepickerLabels;
  locale?: string;
  isDisabled?: boolean;
  hasFooter?: boolean;
  selectsRange?: boolean;
  isError?: boolean;
  closeCalendarOnSelectDate?: boolean;
  datepickerStyles: datepickerStyles;
  onInputChange?: (params: Date | string) => void;
  onSelectDates?: (dates: DatepickerSelectionDate) => void;
  onReset?: () => void;
  onDone?: (dates: DatepickerRangeSelectionDate) => void;
  isDatePickerFocus?: boolean;
  displayDatesNotification?: boolean;
  disableFlip?: boolean;
}

function handleSameDateAsToday(
  endDate: datepickerDate,
  tomorrow: Date,
  dateFormat: string,
  labels?: datepickerLabels,
  locale?: string
) {
  if (endDate === null) {
    return `${labels?.todayLabel} | ${labels?.checkoutLabel}`;
  }
  if (isSameDate(endDate, tomorrow)) {
    return `${labels?.todayLabel} | ${labels?.tomorrowLabel}`;
  }
  return `${labels?.todayLabel} | ${format(
    endDate,
    dateFormat,
    locale === CountryCode.DE ? { locale: de } : { locale: enGB }
  )}`;
}

export function getFormattedDate(
  startDate: datepickerDate,
  endDate: datepickerDate,
  dateFormat: string,
  locale: CountryCode,
  props: {
    labels?: datepickerLabels;
    selectsRange?: boolean;
    isError?: boolean;
    displayDatesNotification?: boolean;
  }
) {
  const { labels, selectsRange: hasDatepickerRange, isError, displayDatesNotification } = props;
  const today = new Date();
  const tomorrow = add(today, { days: 1 });
  const isLocaleGerman = locale === CountryCode.DE;

  const localeOption = { locale: isLocaleGerman ? de : enGB };

  dateFormat = isLocaleGerman ? DATE_TYPE.E_DAY_MONTH_YEAR : dateFormat;
  nanoid();
  if (hasDatepickerRange) {
    if (startDate === null && endDate === null) {
      if (isError) return '';
      if (displayDatesNotification) {
        return `${labels?.checkInLabel} | ${labels?.checkoutLabel}`;
      } else {
        return `${labels?.todayLabel} | ${labels?.tomorrowLabel}`;
      }
    }
    if (isSameDate(startDate, today)) {
      return handleSameDateAsToday(endDate, tomorrow, dateFormat, labels, locale);
    }
    if (isSameDate(startDate, tomorrow)) {
      if (endDate === null) {
        return `${labels?.tomorrowLabel} | ${labels?.checkoutLabel}`;
      }
      return `${labels?.tomorrowLabel} | ${format(endDate, dateFormat, localeOption)}`;
    }
    if (startDate && endDate === null) {
      return `${format(startDate, dateFormat, localeOption)} | ${labels?.checkoutLabel}`;
    }
    return `${format(startDate as Date, dateFormat, localeOption)} | ${format(
      endDate as Date,
      dateFormat,
      localeOption
    )}`;
  }
  return startDate ? format(startDate, dateFormat, localeOption) : '';
}

export default function Datepicker(props: Readonly<DatepickerProps>) {
  const { defaultStartDate, defaultEndDate, isDatePickerFocus } = props;

  const baseDatepickerStyles = useStyleConfig('Datepicker');
  const hasFooter = props.hasFooter ?? false;
  const [startDate, setStartDate] = useState<datepickerDate>(defaultStartDate ?? null);
  const [endDate, setEndDate] = useState<datepickerDate>(defaultEndDate ?? null);
  const [calendarKey, setCalendarKey] = useState(0);
  const ref = useRef<HTMLDivElement>(null) as React.RefObject<HTMLDivElement>;
  const refInput = useRef<HTMLInputElement>(null) as React.RefObject<HTMLInputElement>;
  const [isCalendarOpen, setIsCalendarOpen] = useState(false);
  const { inputStyles, inputIconStyles, resetButtonStyles, doneButtonStyles } = getDatepickerStyles(
    isCalendarOpen,
    props.datepickerStyles
  );
  const [isDesktop] = useMediaQuery('(min-width: 1280px)');
  const [isTouchDevice] = useMediaQuery('(pointer: coarse)');
  const { [FT_PI_TWO_MONTH_SEARCH]: isTwoMonthSearchEnabled } = useFeatureToggle();
  const showTwoMonths =
    isTwoMonthSearchEnabled && getCookie(TWO_MONTH_SEARCH) === 'true' && isDesktop;

  useOutsideClick({
    ref: ref,
    handler: () => setIsCalendarOpen(false),
  });

  registerLocale(CountryCode.DE, de);

  useEffect(() => {
    if (defaultStartDate !== undefined) setStartDate(defaultStartDate);
    if (defaultEndDate !== undefined) setEndDate(defaultEndDate);
  }, [defaultStartDate, defaultEndDate]);

  useEffect(() => {
    isDatePickerFocus && refInput?.current?.focus();
  }, [isDatePickerFocus]);

  return (
    <Box
      __css={baseDatepickerStyles}
      ref={ref}
      onKeyDown={(event) => {
        if (event.key === 'Escape' && isCalendarOpen) {
          setIsCalendarOpen(false);
          refInput.current?.focus();
        }
      }}
    >
      <DatePickerGlobalStyles />
      <DatePicker
        key={calendarKey}
        {...(props.locale === CountryCode.DE
          ? { locale: CountryCode.DE }
          : { locale: CountryCode.EN })}
        selected={startDate}
        startDate={startDate}
        endDate={endDate}
        onChange={handleDatepickerChange}
        onKeyDown={(event) => {
          if (event.key === 'Enter') {
            handleDatepickerChange([startDate, endDate]);
          }
        }}
        customInput={renderInput()}
        minDate={props.minDate}
        maxDate={props?.maxDate}
        useWeekdaysShort={true}
        calendarStartDay={1}
        disabled={props.isDisabled}
        open={isCalendarOpen}
        {...(props.selectsRange && { selectsRange: true })}
        renderDayContents={renderDayContents}
        dayClassName={getDayClassName}
        monthsShown={showTwoMonths ? 2 : 1}
        popperPlacement={'bottom-start' as const}
        popperClassName={showTwoMonths ? 'two-month-calendar' : ''}
        popperProps={
          props.disableFlip
            ? ({
                strategy: 'fixed',
                middleware: [{ name: 'offset', fn: (s: any) => ({ x: s.x, y: s.y + 10 }) }],
              } as any)
            : undefined
        }
      >
        {hasFooter && renderCalendarFooter()}
      </DatePicker>
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

    if (props.selectsRange && !endDate) {
      return resetDate(date) < resetDate(startDate);
    }
    return resetDate(date) < resetDate(props.minDate);
  }

  function renderInput() {
    const displayDateFormat = props.displayDateFormat ?? props.dateFormat;
    const borderStyles = isCalendarOpen ? { borderColor: 'primary' } : {};
    const locale = props.locale === CountryCode.DE ? CountryCode.DE : CountryCode.EN;
    const value = getFormattedDate(startDate, endDate, displayDateFormat, locale, props);
    return (
      <InputGroup
        {...{
          ...props.datepickerStyles.inputGroupStyles,
          minW: '0',
          border: {
            base: `${isCalendarOpen ? '0' : '1px solid var(--chakra-colors-lightGrey1)'}`,
            sm: '0',
          },
        }}
        {...props.datepickerStyles?.errorInputGroupStyles}
        {...borderStyles}
      >
        <Flex w="full">
          <InputLeftElement pointerEvents="none" {...inputIconStyles}>
            <Icon svg={<Calendar />} />
          </InputLeftElement>
          <Input
            aria-label="datepicker-input"
            placeholder={props.inputPlaceholder}
            value={value}
            onChange={(event) => {
              props.onInputChange?.(event.target.value);
            }}
            isDisabled={props.isDisabled}
            onClick={() => {
              setIsCalendarOpen(true);
            }}
            onKeyDown={(event) => {
              if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault();
                event.stopPropagation();
                setIsCalendarOpen(true);
              }
              if (event.altKey && event.key === 'ArrowDown') {
                event.preventDefault();
                setIsCalendarOpen(true);
              }
            }}
            {...inputStyles}
            ref={refInput}
          />
        </Flex>
      </InputGroup>
    );
  }

  function renderCalendarFooter() {
    return (
      <Box textAlign="center" mb="xl" float="left" w="full">
        <Button
          size="sm"
          variant="tertiary"
          onClick={handleReset}
          isDisabled={startDate === null}
          tabIndex={startDate !== null ? 0 : -1}
          {...resetButtonStyles}
        >
          {props.labels?.resetButtonLabel}
        </Button>
        <Button
          size="sm"
          variant="tertiary"
          onClick={handleDone}
          {...doneButtonStyles}
          tabIndex={0}
        >
          {props.labels?.doneButtonLabel}
        </Button>
      </Box>
    );
  }

  function renderDayContents(day: number): ReactNode {
    return (
      <>
        <div className="highlight" />
        <div
          className="day-number"
          tabIndex={isTouchDevice ? undefined : new Date().getDate() === day ? 0 : -1}
        >
          {day}
        </div>
      </>
    );
  }

  function handleDone() {
    setIsCalendarOpen(false);
    props.onDone?.([startDate, endDate]);
  }

  function handleReset() {
    const today = new Date();
    if (props.selectsRange) {
      const tomorrow = add(today, { days: 1 });
      setStartDate(today);
      setEndDate(tomorrow);
    } else {
      setStartDate(today);
      setEndDate(null);
    }
    setCalendarKey((prev) => prev + 1);
    props.onReset?.();
  }

  function handleDatepickerChange(dates: DatepickerSelectionDate) {
    if (props.selectsRange) {
      const [start, end] = dates as DatepickerRangeSelectionDate;
      setStartDate(start);
      if (isSameDate(start, end)) return;
      setEndDate(end);
      props.onSelectDates?.([start, end]);

      return;
    }

    setStartDate(dates as Date);
    props.onSelectDates?.(dates as Date);
    if (props.closeCalendarOnSelectDate) {
      setIsCalendarOpen(false);
    }
  }
}

function getDatepickerStyles(isCalendarOpen: boolean, datepickerStyles: datepickerStyles) {
  const inputStyles = {
    padding:
      'var(--chakra-space-lg) var(--chakra-space-lg) var(--chakra-space-lg) var(--chakra-space-2xl)',
    outline: 'none',
    color: 'var(--chakra-colors-darkGrey1)',
    cursor: 'pointer',
    border: isCalendarOpen
      ? '0.125rem solid var(--chakra-colors-primary)'
      : '0.125rem solid transparent',
    borderRadius: isCalendarOpen ? 'var(--chakra-space-xs)' : 0,
    borderColor: isCalendarOpen ? 'var(--chakra-colors-primary)' : 'transparent',
    borderRight: {
      base: `${isCalendarOpen ? '0.125rem solid var(--chakra-colors-primary)' : '0'}`,
      sm: `${
        isCalendarOpen
          ? '0.125rem solid var(--chakra-colors-primary)'
          : '0.063rem solid var(--chakra-colors-lightGrey4)'
      }`,
    },
    borderTopRightRadius: isCalendarOpen ? 'var(--chakra-space-xs)' : 0,
    borderBottomRightRadius: isCalendarOpen ? 'var(--chakra-space-xs)' : 0,
    textOverflow: 'ellipsis',
    marginLeft: {
      mobile: '-1px',
      xs: '-1px',
      sm: 0,
    },
    _hover: isCalendarOpen
      ? {
          border: '0.125rem solid var(--chakra-colors-primary)',
          borderRadius: 'var(--chakra-space-xs)',
          borderColor: 'var(--chakra-colors-primary)',
        }
      : {
          border: '0.063rem solid var(--chakra-colors-darkGrey1)',
          borderRadius: 'var(--chakra-space-xs)',
          borderColor: 'var(--chakra-colors-darkGrey1)',
        },
    _active: {
      border: '0.125rem solid var(--chakra-colors-primary)',
      borderRadius: 'var(--chakra-space-xs)',
      borderColor: 'var(--chakra-colors-primary)',
    },
    _focus: {
      border: '0.125rem solid var(--chakra-colors-primary)',
      borderRadius: 'var(--chakra-space-xs)',
      borderColor: 'var(--chakra-colors-primary)',
    },
    _disabled: {
      border: 'none',
    },
    ...datepickerStyles.datepickerInputElementStyles,
    ...datepickerStyles?.errorInputElementStyles,
  };

  const inputIconStyles = {
    top: 'var(--chakra-space-md)',
    ...datepickerStyles.iconStyles,
  };

  const responsiveButtonWidth = { mobile: '7.7rem', xs: '9.0625rem', sm: '9.25rem' };

  const resetButtonStyles = {
    mr: 'lg',
    border: 'none',
    boxShadow: 'none',
    _hover: {
      bg: 'baseWhite',
      boxShadow: 'none',
      textDecoration: 'underline',
    },
    _disabled: {
      color: 'lightGrey2',
      cursor: 'default',
      _hover: {
        textDecoration: 'none',
      },
    },
    w: responsiveButtonWidth,
  };

  const doneButtonStyles = {
    w: responsiveButtonWidth,
    _hover: {
      bg: 'baseWhite',
    },
  };

  return { inputStyles, inputIconStyles, resetButtonStyles, doneButtonStyles };
}

function resetDate(date: Date) {
  return date.setHours(12, 0, 0, 0);
}
