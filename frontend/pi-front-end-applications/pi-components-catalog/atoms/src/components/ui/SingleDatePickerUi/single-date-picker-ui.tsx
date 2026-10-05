'use client';

import { LOCALES, FormInnB } from '@whitbread-eos/api';
import { cn, formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { de, enGB } from 'date-fns/locale';
import Image from 'next/image';
import * as React from 'react';
import { useState, useCallback, KeyboardEvent } from 'react';
import { ActiveModifiers, Matcher } from 'react-day-picker';

import { Button, buttonVariants } from '../Button';
import { CalendarSingle } from '../CalendarSingle';
import { createCustomLocale } from '../DatePicker/createCustomLocale';
import { Popover, PopoverContent, PopoverTrigger } from '../Popover';

export interface Props {
  fromMonth: Date | undefined;
  toMonth: Date | undefined;
  className?: string;
  locale?: string;
  formLabels?: FormInnB | Record<string, never>;
  icons?: Record<string, string>;
  onDateChange: (date: Date | undefined) => void;
  selectedDay: Date | undefined;
  setCurrentMonth: (date: Date | undefined) => void;
  currentMonth: Date | undefined;
  disabled?: Matcher | Matcher[] | undefined;
  showError?: string | undefined;
  setShowError?: (arg: string | undefined) => void;
  placeholder?: string;
  buttonClassName?: string;
}

export function SingleDatePickerUi({
  fromMonth,
  toMonth,
  setCurrentMonth,
  selectedDay,
  currentMonth,
  disabled,
  className,
  locale,
  formLabels,
  icons,
  onDateChange,
  showError = undefined,
  setShowError,
  placeholder,
  buttonClassName,
}: Readonly<Props>) {
  const { months = [], weekdaysShort = [] } = formLabels?.datePicker || {};
  const [isResetDisabled, setIsResetDisabled] = useState(true);
  const localeFormatted = (isShortMonth?: boolean) => {
    return locale === LOCALES.EN
      ? createCustomLocale(enGB, months, weekdaysShort, isShortMonth)
      : createCustomLocale(de, months, weekdaysShort, isShortMonth);
  };

  const { t } = useTranslation();

  const [popoverOpen, setPopoverOpen] = useState<boolean>(false);

  const handleDayClick = (newDate: Date) => {
    if (onDateChange) {
      onDateChange(newDate);
      setIsResetDisabled(false);
    }
    if (setShowError) {
      setShowError(undefined);
    }
  };

  const handleMonthChange = (month: Date) => {
    setCurrentMonth(month);
  };

  const onDayKeyPress = (
    day: React.SetStateAction<Date | undefined>,
    activemodifiers: ActiveModifiers,
    event: KeyboardEvent
  ) => {
    const deleteEvents = ['Delete', 'Backspace', 'Del', 'Clear'];

    if (deleteEvents.includes(event.key)) {
      onDateChange(undefined);
    }
  };

  function resetCalendar() {
    onDateChange(undefined);
    setShowError && setShowError(undefined);
    setIsResetDisabled(true);
    setCurrentMonth(new Date());
  }

  const renderButton = useCallback(() => {
    return (
      <Button
        data-testid="IB-Date-Picker-Input"
        id="date"
        variant="inputCalendarButton"
        size="inputCalendarButton"
        className={cn(
          buttonClassName,
          buttonVariants({ variant: 'inputCalendarButton', size: 'inputCalendarButton' }),
          'hover:border-darkGrey1 rounded min-w-[12.625rem] mobile:min-w-auto flex justify-between',
          !selectedDay && 'text-muted-foreground',
          showError && '-outline-offset-2 outline outline-2 outline-error',
          popoverOpen &&
            'outline outline-2 outline-primaryColor hover:outline-primaryColor outline-offset-[-2px] border-transparent'
        )}
      >
        <div className="flex w-full">
          <Image
            className="mr-3 h-6 w-6"
            loading="eager"
            src={formatIBAssetsUrl(icons?.['icon.input.datepicker'] ?? '')}
            alt="calendar icon"
            width={24}
            height={24}
            data-testid="IB-Date-Picker-Calendar-Icon"
          />
          <div className={textInput} data-testid="IB-Date-Picker-Text">
            {selectedDay ? (
              <span>
                {' '}
                {selectedDay.toLocaleDateString(locale, {
                  day: '2-digit',
                  month: 'short',
                  year: '2-digit',
                })}
              </span>
            ) : (
              <span>{placeholder}</span>
            )}
          </div>
          <Image
            className="h-6 w-6 ml-auto"
            loading="eager"
            src={formatIBAssetsUrl(icons?.['icon.chevron.down'] ?? '')}
            alt="dropdown icon"
            width={24}
            height={24}
            data-testid="IB-Date-Picker-Dropdown-Icon"
          />
        </div>
      </Button>
    );
  }, [icons, placeholder, popoverOpen, selectedDay, showError]);

  return (
    <>
      <div className={cn('flex gap-2', className)} data-testid={'IB-Single-Date-Picker-Wrapper'}>
        <Popover open={popoverOpen} onOpenChange={setPopoverOpen}>
          <PopoverTrigger asChild>{renderButton()}</PopoverTrigger>
          <PopoverContent className="w-auto p-4" align="start">
            <CalendarSingle
              fromMonth={fromMonth}
              toMonth={toMonth}
              onMonthChange={handleMonthChange}
              onDayKeyPress={onDayKeyPress}
              month={currentMonth}
              defaultMonth={currentMonth || selectedDay}
              onDayClick={handleDayClick}
              numberOfMonths={1}
              mode="single"
              selected={selectedDay}
              locale={localeFormatted()}
              disabled={disabled}
              components={{
                IconLeft: () => (
                  <Image
                    className={iconStyle}
                    src={formatIBAssetsUrl(icons?.['icon.chevron.left'])}
                    alt="DatePicker Icon Left"
                    width={24}
                    height={24}
                  />
                ),
                IconRight: () => (
                  <Image
                    className={iconStyle}
                    src={formatIBAssetsUrl(icons?.['icon.chevron.right'])}
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
                onClick={() => {
                  resetCalendar();
                }}
                disabled={isResetDisabled}
                type="button"
              >
                {t('content.form.datePicker.reset')}
              </Button>
              <Button
                type="button"
                data-testid="IB-Date-Picker-Calendar-buttons-Done"
                className={cn(
                  buttonVariants({ variant: 'footerButtons', size: 'footerButtons' }),
                  'border border-secondaryColor hover:shadow hover:shadow-lightGrey2'
                )}
                onClick={() => setPopoverOpen(false)}
              >
                {t('content.form.datePicker.done')}
              </Button>
            </div>
          </PopoverContent>
        </Popover>
      </div>
    </>
  );
}

const textInput = 'text-base font-normal text-left text-darkGrey2 truncate inline-block';
const iconStyle = 'h-6 w-6';
