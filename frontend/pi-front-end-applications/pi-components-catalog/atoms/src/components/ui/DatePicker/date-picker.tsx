'use client';

import { LOCALES, DATE_TYPE, FormInnB } from '@whitbread-eos/api';
import { useElementDimensions } from '@whitbread-eos/utils';
import { cn, formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { addDays, format } from 'date-fns';
import { de, enGB } from 'date-fns/locale';
import Image from 'next/image';
import * as React from 'react';
import { useState, useEffect } from 'react';
import { DateRange } from 'react-day-picker';

import { Button, buttonVariants } from '../Button';
import { Calendar } from '../Calendar';
import { Dialog, DialogContent, DialogHeader, DialogFooter, DialogTitle } from '../Dialog';
import { Popover, PopoverContent, PopoverTrigger } from '../Popover';
import { ErrorTooltip } from '../Tooltip';
import { createCustomLocale } from './createCustomLocale';

export interface Props {
  className: React.HTMLAttributes<HTMLDivElement>;
  locale: string;
  formLabels: FormInnB | Record<string, never>;
  icons: Record<string, string>;
  mobile: boolean;
  onDateChange: (date: DateRange | undefined) => void;
  dateFromUrl: DateRange | undefined;
  showError: boolean;
  setShowError: (arg: boolean) => void;
  onOpenChange: (open: boolean) => void;
}

const today = new Date();

const isFirstDayInMonth = (day: Date) => {
  const date = new Date(day);
  const firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
  return date.getDate() === firstDay.getDate();
};

const isLastDayInMonth = (day: Date) => {
  const date = new Date(day);
  const lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);
  return date.getDate() === lastDay.getDate();
};

const isCurrentMonth = (currentMonth: Date, today: Date) => {
  return (
    currentMonth.getFullYear() === today.getFullYear() &&
    currentMonth.getMonth() === today.getMonth()
  );
};

const isBeforeToday = (day: Date) => {
  today.setHours(0, 0, 0, 0);
  return day < today;
};

const isAfter364Days = (day: Date) => {
  const oneYearLater = new Date(today);
  oneYearLater.setDate(today.getDate() + 364);
  return day > oneYearLater;
};

const shouldHideNextMonthIcon = (currentMonth: Date) => {
  const lastVisibleDayOfMonth = new Date(
    currentMonth.getFullYear(),
    currentMonth.getMonth() + 1,
    0
  );

  return isAfter364Days(lastVisibleDayOfMonth);
};

const textSeparator = (firstText: string, secondText: string) => {
  return (
    <>
      {firstText}
      <div className={separatorStyle}>|</div>
      {secondText}
    </>
  );
};

const getWeekDays = (locale: string): { short: string; full: string }[] => {
  const baseDate = new Date(2025, 7, 3);
  const weekDays: { short: string; full: string }[] = [];
  const localeObj = locale === 'de' ? de : enGB;
  for (let i = 1; i <= 7; i++) {
    const date = new Date(baseDate);
    date.setDate(baseDate.getDate() + i - baseDate.getDay());
    weekDays.push({
      short: format(date, 'EEE', { locale: localeObj }),
      full: format(date, 'EEEE', { locale: localeObj }),
    });
  }
  return weekDays;
};

export function DatePickerWithRange({
  className,
  locale,
  formLabels,
  icons,
  mobile = false,
  onDateChange,
  dateFromUrl,
  showError,
  setShowError,
  onOpenChange,
}: Readonly<Props>) {
  const { months = [], weekdaysShort = [] } = formLabels?.datePicker || {};

  const localeFormatted = (isShortMonth?: boolean) => {
    return locale === LOCALES.EN
      ? createCustomLocale(enGB, months, weekdaysShort, isShortMonth)
      : createCustomLocale(de, months, weekdaysShort, isShortMonth);
  };

  const { t } = useTranslation(['common', 'layout']);

  const [date, setDate] = useState<DateRange | undefined>({
    from: undefined,
    to: undefined,
  });
  const [datesOnModalOpen, setDatesOnModalOpen] = useState<DateRange | undefined>({
    from: undefined,
    to: undefined,
  });

  useEffect(() => {
    setDate({ from: dateFromUrl?.from, to: dateFromUrl?.to });
    if (dateFromUrl?.from) {
      setCurrentMonth(dateFromUrl.from);
    }
  }, [dateFromUrl]);

  const [isResetDisabled, setIsResetDisabled] = useState<boolean>(true);
  const [hoverDate, setHoverDate] = useState<Date | undefined>(undefined);
  const [popoverOpen, setPopoverOpen] = useState<boolean>(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const baseDataTestId = 'DatePicker';

  const [currentMonth, setCurrentMonth] = useState<Date>(new Date());

  const find364DayOfPeriod = new Date(today);
  find364DayOfPeriod.setDate(today.getDate() + 364);

  const find365DayOfPeriod = new Date(find364DayOfPeriod.getTime() + 1000 * 60 * 60 * 24);

  const last14DaysStart = new Date(find364DayOfPeriod);
  last14DaysStart.setDate(find364DayOfPeriod.getDate() - 13);

  function resetCalendar() {
    setDate({ from: undefined, to: undefined });
    setShowError(false);
    onDateChange({ from: undefined, to: undefined });
    setIsResetDisabled(true);
    setCurrentMonth(today);
    // Focus today's date
    setTimeout(() => {
      const currentDateElement = document.querySelector('.current-date');
      if (currentDateElement) {
        (currentDateElement as HTMLElement).focus();
      }
    }, 0);
  }

  const onModalClose = () => {
    setIsModalOpen(false);
    setDate(datesOnModalOpen);
  };

  const handleDayClick = (newDate: Date) => {
    setHoverDate(undefined);
    setIsResetDisabled(false);

    if (date?.to && newDate.getTime() === find365DayOfPeriod.getTime()) return;

    //checkin date not selected || checkout date selected
    if (!date?.from || date?.to) {
      setDate({ from: newDate, to: undefined });
      onDateChange({ from: newDate, to: undefined });
      setShowError(false);
      return;
    }
    // checkin selected and checkout not selected
    if (date?.from && !date?.to && newDate < date?.from) {
      setDate({ from: undefined, to: undefined });
      onDateChange({ from: undefined, to: undefined });
      setIsResetDisabled(true);
      setShowError(false);
      return;
    }
    if (date?.from && !date?.to && newDate > date?.from) {
      setDate({ ...date, to: newDate });
      onDateChange({ ...date, to: newDate });
      setShowError(false);
      return;
    }
    if (!date?.from || !date?.to) {
      setDate({ from: undefined, to: undefined });
      onDateChange({ from: undefined, to: undefined });
      setIsResetDisabled(true);
      setShowError(false);
      return;
    }

    setDate({ from: newDate, to: undefined });
    onDateChange({ from: newDate, to: undefined });
    setShowError(false);
  };

  const handleDayEnter = (hoverDay: Date) => {
    // checkin date not selected || checkout date selected => exit function
    if (!date?.from || date?.to) {
      return;
    }

    if (hoverDay.getTime() === date?.from.getTime()) {
      setHoverDate(undefined);
      return;
    }
    setHoverDate(hoverDay);
  };

  const isInHoverRange = (day: Date) => {
    if (!date?.from || !hoverDate) {
      return false;
    }
    const checkedDay = new Date(day);
    return checkedDay >= date?.from && checkedDay <= hoverDate;
  };
  const dateFromFormatted =
    date?.from && format(date?.from, DATE_TYPE.D_MMM_YY, { locale: localeFormatted(true) });
  const dateToFormatted =
    date?.to && format(date?.to, DATE_TYPE.D_MMM_YY, { locale: localeFormatted(true) });

  const renderInputInfo = () => {
    const todayFormatted = format(new Date(), DATE_TYPE.D_MMM_YY, {
      locale: localeFormatted(true),
    });
    const tomorrowFormatted = format(addDays(new Date(), 1), DATE_TYPE.D_MMM_YY, {
      locale: localeFormatted(true),
    });

    // checkin date is today and checkout date is tomorrow
    if (dateFromFormatted === todayFormatted && dateToFormatted === tomorrowFormatted) {
      return textSeparator(t('common.content.global.today'), t('common.content.global.tomorrow'));
    }
    //checkin date is selected, checkout date not selected, checkin date is today
    if (dateFromFormatted && !dateToFormatted && dateFromFormatted === todayFormatted) {
      return textSeparator(
        t('common.content.global.today'),
        t('common.content.form.datePicker.checkOut')
      );
    }
    //checkin date is selected, checkout date is selected and checkin date is today
    if (dateFromFormatted && dateToFormatted && dateFromFormatted === todayFormatted) {
      return textSeparator(t('common.content.global.today'), dateToFormatted);
    }
    //checkin date is selected, checkout date not selected and checkin date is tomorrow
    if (dateFromFormatted && !dateToFormatted && dateFromFormatted === tomorrowFormatted) {
      return textSeparator(
        t('common.content.global.tomorrow'),
        t('common.content.form.datePicker.checkOut')
      );
    }
    //checkin date is selected, checkout date is not selected and checkin date is not tomorrow
    if (dateFromFormatted && !dateToFormatted && dateFromFormatted !== tomorrowFormatted) {
      return textSeparator(dateFromFormatted, t('common.content.form.datePicker.checkOut'));
    }
    // check in date selected, check out date selected, checkin date is tomorrow
    if (dateFromFormatted && dateToFormatted && dateFromFormatted === tomorrowFormatted) {
      return textSeparator(t('common.content.global.tomorrow'), dateToFormatted);
    }
    // check in date selected, check out date selected, checkin date is not tomorrow
    if (dateFromFormatted && dateToFormatted && dateFromFormatted !== tomorrowFormatted) {
      return textSeparator(dateFromFormatted, dateToFormatted);
    }
    // checkin date and checkout date are not selected
    if (!dateFromFormatted && !dateToFormatted) {
      return textSeparator(t('common.content.global.today'), t('common.content.global.tomorrow'));
    }
    //default return
    return (
      dateFromFormatted && dateToFormatted && textSeparator(dateFromFormatted, dateToFormatted)
    );
  };

  const handleMonthChange = (month: Date) => {
    setCurrentMonth(month);
  };

  const isInTheNextNDays = (day: Date, numberOfDays: number) => {
    if (!date?.from) return;
    const dayDifference = (day.getTime() - date?.from.getTime()) / (1000 * 3600 * 24);
    return dayDifference >= 0 && dayDifference <= numberOfDays;
  };

  const disabledDays = (day: Date) => {
    if (date?.to && day.getTime() === find365DayOfPeriod.getTime()) return true;

    if (date?.to && day > date?.to) return isAfter364Days(day);
    if (date?.to) return isBeforeToday(day);

    //allow day 365 to be selected only for checkout if checkin date is in last 14 days of entire allowed period
    if (
      date?.from &&
      date?.from >= last14DaysStart &&
      date?.from <= find364DayOfPeriod &&
      day.getTime() === find365DayOfPeriod.getTime()
    )
      return false;

    return isBeforeToday(day) || (date?.from && !isInTheNextNDays(day, 14)) || isAfter364Days(day);
  };

  const { height: headerHeight } = useElementDimensions('header');

  const handleButtonClick = () => {
    setDatesOnModalOpen(date);
  };

  const renderButton = (
    <Button
      onClick={handleButtonClick}
      data-testid="IB-Date-Picker-Input"
      id="date"
      variant="inputCalendarButton"
      className={cn(
        buttonVariants({ variant: 'inputCalendarButton', size: 'inputCalendarButton' }),
        'hover:border-darkGrey1 mobile:rounded w-full',
        !date && 'text-muted-foreground',
        showError && '-outline-offset-2 outline outline-2 outline-error',
        popoverOpen &&
          'outline outline-2 outline-primaryColor hover:outline-primaryColor outline-offset-[-2px] border-transparent'
      )}
    >
      <Image
        className="mr-3 h-6 w-6"
        loading="eager"
        src={formatIBAssetsUrl(formLabels?.calendarIcon ?? '')}
        alt="CalendarIcon"
        width={24}
        height={24}
        data-testid="IB-Date-Picker-Calendar-Icon"
      />
      <div className={textInput} data-testid="IB-Date-Picker-Text">
        {renderInputInfo()}
      </div>
    </Button>
  );

  useEffect(() => {
    onOpenChange(popoverOpen);
  }, [popoverOpen]);

  function getDateRangeNights(date?: { from?: Date; to?: Date }): number {
    if (!date?.from && !date?.to) {
      return 0;
    } else if (date?.from && !date?.to) {
      return 1;
    } else if (date?.from && date?.to) {
      const diffTime = date.to.getTime() - date.from.getTime();
      return Math.round(diffTime / (1000 * 60 * 60 * 24));
    }
    return 0;
  }

  const nights = getDateRangeNights(date);
  const getStayLabel = () =>
    nights > 1
      ? `${nights} ${t('common.content.global.nights')}`
      : `${nights} ${t('common.content.global.night')}`;

  const weekDays = locale === LOCALES.DE ? getWeekDays('de') : getWeekDays('en'); // [{ short: 'Mon', full: 'Monday' }, ...]

  const displayDatePickerModal = () => (
    <Dialog open={isModalOpen} data-testid={`${baseDataTestId}-Dialog`} onOpenChange={onModalClose}>
      <DialogContent
        className={cn(
          'fixed inset-0 h-dvh w-screen bg-white overflow-hidden',
          'flex flex-col min-h-0 p-0 gap-0',

          '!absolute !top-0 !left-0 !right-0 !bottom-0 !translate-x-0 !translate-y-0 !rounded-none !max-w-none',
          'mobile:!h-dvh mobile:!overflow-hidden'
        )}
        data-testid={`${baseDataTestId}-Dialog-Content`}
      >
        <div className="flex flex-col h-full min-h-0 gap-0">
          <DialogHeader
            data-testid={`${baseDataTestId}-Dialog-Header`}
            className="flex-col gap-6 pb-8 border-b mb-0 mx-[-1rem] px-[1rem]"
          >
            <DialogTitle
              className="m-0 truncate pr-[90px]"
              data-testid={`${baseDataTestId}-Dialog-Title`}
            >
              {t('layout.innbusinessLayout.header.search.date')}
            </DialogTitle>
            <div>
              <a
                className="absolute right-[70px] top-[35px] underline text-secondaryColor"
                onClick={() => resetCalendar()}
              >
                {t('common.content.form.datePicker.reset')}
              </a>
            </div>
          </DialogHeader>
          <div className="flex-1 min-h-0 overflow-y-auto">
            <div className="absolute flex justify-between -mt-8 text-darkGrey2 text-sm left-1/2 -translate-x-1/2 w-full max-w-[340px] md:max-w-[450px] text-center">
              {weekDays.map((day, idx) => (
                <div key={idx} className="flex-1 text-center">
                  <span className="sr-only">{day.full}</span>
                  <span aria-hidden="true">{day.short}</span>
                </div>
              ))}
            </div>
            <Calendar
              onDayMouseEnter={handleDayEnter}
              mode="range"
              month={currentMonth}
              onMonthChange={handleMonthChange}
              defaultMonth={date?.from}
              selected={date}
              onDayClick={handleDayClick}
              numberOfMonths={12}
              locale={localeFormatted()}
              modifiers={{
                fadedEndOfMonth: (date: Date) => isLastDayInMonth(date),
                fadedStartOfMonth: (date: Date) => isFirstDayInMonth(date),
                onlyFromSelected: () => !!date?.from && !date?.to,
                isInHoverRange: (date: Date) => isInHoverRange(date),
              }}
              modifiersClassNames={{
                fadedEndOfMonth: 'ib-faded-end-of-month',
                fadedStartOfMonth: 'ib-faded-start-of-month',
                onlyFromSelected: 'only-start',
                isInHoverRange: 'in-hover-range',
                checkinLastMonth: 'day-checkin-last-month',
              }}
              isCurrentMonth={isCurrentMonth(currentMonth, today)}
              shouldHideNextMonthIcon={true}
              isCheckoutDay365
              disabled={disabledDays}
              hideHeader={true}
              components={{
                IconLeft: () => (
                  <Image
                    className={iconStyle}
                    src={formatIBAssetsUrl(icons['icon.chevron.left'])}
                    alt="DatePicker Icon Left"
                    width={24}
                    height={24}
                  />
                ),
                IconRight: () => (
                  <Image
                    className={iconStyle}
                    src={formatIBAssetsUrl(icons['icon.chevron.right'])}
                    alt="DatePicker Icon Right"
                    width={24}
                    height={24}
                  />
                ),
              }}
            />
          </div>
        </div>
        <DialogFooter className="flex flex-row -mx-[16px]">
          <div className="flex bg-lightGrey5 justify-center">
            <div className="flex justify-center items-center py-[12px]">
              <div className="flex flex-col md:w-[266px] md:pl-[80px] sm:pl-[30px] w-[170px] pl-[20px] truncate">
                <span className="text-sm">
                  {t('layout.innbusinessLayout.header.search.arriving')}
                </span>
                <span className={date?.from ? 'font-bold' : 'text-darkGrey2 text-base'}>
                  {date?.from
                    ? format(date?.from, 'EEE dd MMM yy', { locale: localeFormatted(true) })
                    : t('layout.innbusinessLayout.header.search.selectDate')}
                </span>
              </div>
              <div className="pl-0 sm:pl-[12px] md:pl-[12px]">
                <Image
                  alt={'Arrow Right Icon'}
                  src={formatIBAssetsUrl(icons['icon.arrow.right'])}
                  width={24}
                  height={24}
                  data-testid={`${baseDataTestId}-arrow-right-icon`}
                />
              </div>
              <div className="flex flex-col md:w-[266px] sm:w-[170px] pl-[12px] w-[170px] truncate">
                <span className="text-sm">
                  {t('layout.innbusinessLayout.header.search.leaving')}
                </span>
                <span className={date?.to ? 'font-bold' : 'text-darkGrey2 text-base'}>
                  {date?.to
                    ? format(date?.to, 'EEE dd MMM yy', { locale: localeFormatted(true) })
                    : t('layout.innbusinessLayout.header.search.selectDate')}
                </span>
              </div>
            </div>
          </div>
          <div className="flex flex-row gap-[20px] justify-center items-center">
            <div
              className={cn('w-[170px] md:w-[266px] md:pl-[70px] pl-[18px] break-words', {
                invisible: !date?.from,
              })}
            >{`${t('layout.innbusinessLayout.header.search.stay')}: ${getStayLabel()} `}</div>
            <Button
              data-testid="IB-Date-Picker-Calendar-buttons-Done"
              className={cn(
                buttonVariants({ variant: 'default', size: 'footerButtons' }),
                'border border-secondaryColor hover:shadow hover:shadow-lightGrey2 w-[170px] md:w-[266px]'
              )}
              onClick={() => (mobile ? setIsModalOpen(false) : setPopoverOpen(false))}
              disabled={!date?.from}
            >
              {t('common.content.form.datePicker.done')}
            </Button>
          </div>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );

  return (
    <>
      <div
        className={cn('flex gap-2 mobile:relative', className)}
        data-testid={'IB-Date-Picker-Wrapper'}
      >
        <Popover open={popoverOpen} onOpenChange={mobile ? setIsModalOpen : setPopoverOpen}>
          {showError ? (
            <ErrorTooltip
              icon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              className={errorTooltipStyle}
              content={t('common.content.form.invalidNights')}
              open={showError}
              testId="IB-Date-Picker-ErrorTooltip"
            >
              <PopoverTrigger asChild>{renderButton}</PopoverTrigger>
            </ErrorTooltip>
          ) : (
            <PopoverTrigger asChild>{renderButton}</PopoverTrigger>
          )}
          {mobile && displayDatePickerModal()}
          {!mobile && (
            <PopoverContent
              className={containerMobileStyle}
              align="start"
              data-testid={'IB-Date-Picker-Container'}
              popoverStyling={mobile ? { height: `calc(100svh - ${headerHeight}px -120px)` } : {}}
            >
              <Calendar
                onDayMouseEnter={handleDayEnter}
                mode="range"
                month={currentMonth}
                onMonthChange={handleMonthChange}
                defaultMonth={date?.from}
                selected={date}
                onDayClick={handleDayClick}
                numberOfMonths={1}
                locale={localeFormatted()}
                modifiers={{
                  fadedEndOfMonth: (date: Date) => isLastDayInMonth(date),
                  fadedStartOfMonth: (date: Date) => isFirstDayInMonth(date),
                  onlyFromSelected: () => !!date?.from && !date?.to,
                  isInHoverRange: (date: Date) => isInHoverRange(date),
                  currentDate: (day: Date) => day.toDateString() === today.toDateString(), // today's date
                }}
                modifiersClassNames={{
                  fadedEndOfMonth: 'ib-faded-end-of-month',
                  fadedStartOfMonth: 'ib-faded-start-of-month',
                  onlyFromSelected: 'only-start',
                  isInHoverRange: 'in-hover-range',
                  checkinLastMonth: 'day-checkin-last-month',
                  currentDate: 'current-date',
                }}
                isCurrentMonth={isCurrentMonth(currentMonth, today)}
                shouldHideNextMonthIcon={shouldHideNextMonthIcon(currentMonth)}
                isCheckoutDay365
                disabled={disabledDays}
                components={{
                  IconLeft: () => (
                    <Image
                      className={iconStyle}
                      src={formatIBAssetsUrl(icons['icon.chevron.left'])}
                      alt="DatePicker Icon Left"
                      width={24}
                      height={24}
                    />
                  ),
                  IconRight: () => (
                    <Image
                      className={iconStyle}
                      src={formatIBAssetsUrl(icons['icon.chevron.right'])}
                      alt="DatePicker Icon Right"
                      width={24}
                      height={24}
                    />
                  ),
                }}
              />
              <div
                data-testid="IB-Date-Picker-Calendar-buttons"
                className="mx-2 mb-2 flex justify-between gap-5"
              >
                <Button
                  data-testid="IB-Date-Picker-Calendar-buttons-Reset"
                  className={cn(
                    buttonVariants({ variant: 'footerButtons', size: 'footerButtons' }),
                    'border-none hover:underline disabled:text-lightGrey2'
                  )}
                  onClick={() => resetCalendar()}
                  disabled={isResetDisabled}
                >
                  {t('common.content.form.datePicker.reset')}
                </Button>
                <Button
                  data-testid="IB-Date-Picker-Calendar-buttons-Done"
                  className={cn(
                    buttonVariants({ variant: 'footerButtons', size: 'footerButtons' }),
                    'border border-secondaryColor hover:shadow hover:shadow-lightGrey2'
                  )}
                  onClick={() => setPopoverOpen(false)}
                >
                  {t('common.content.form.datePicker.done')}
                </Button>
              </div>
            </PopoverContent>
          )}
        </Popover>
      </div>
      <ErrorTooltip
        icon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
        content={t('common.content.form.invalidNights')}
        open={showError}
        testId="IB-Date-Picker-ErrorTooltip-Mobile"
        mobile
      />
    </>
  );
}

const separatorStyle = 'inline-flex w-px h-6 mx-4 bg-lightGrey2 text-transparent';
const textInput = 'text-base font-normal text-left text-darkGrey2 truncate flex';
const iconStyle = 'h-6 w-6';
const containerMobileStyle =
  'mobile:left-0 mobile:top-[100%] mobile:border-0 mobile:border-t mobile:border-lightGrey3 mobile:w-full mobile:rounded-none mobile:overflow-y-auto mobile:justify-center mobile:grid';
const errorTooltipStyle = 'max-w-[90%]';
