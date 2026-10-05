'use client';

import { cn } from '@whitbread-eos/utils';
import {
  DayKeyboardEventHandler,
  DayPicker,
  DayPickerSingleProps,
  Matcher,
} from 'react-day-picker';

import { buttonVariants } from '../Button';

export type CalendarProps = DayPickerSingleProps;

interface ExtraCalendarProps extends CalendarProps {
  fromMonth: Date | undefined;
  toMonth: Date | undefined;
  disabled?: Matcher | Matcher[] | undefined;
  onDayKeyPress: DayKeyboardEventHandler;
  className?: string;
}

function CalendarSingle({
  classNames,
  className,
  locale,
  fromMonth,
  toMonth,
  disabled,
  onDayKeyPress,
  ...props
}: ExtraCalendarProps) {
  return (
    <DayPicker
      locale={locale}
      showOutsideDays={false}
      fromMonth={fromMonth}
      toMonth={toMonth}
      disabled={disabled}
      onDayKeyDown={onDayKeyPress}
      className={cn('pt-4 px-2 pb-6 mobile:flex mobile:justify-center', className)}
      classNames={{
        months: 'flex justify-center flex-col sm:flex-row space-y-4 sm:space-x-4 sm:space-y-0',
        month: 'space-y-4',
        caption: 'flex justify-center pt-1 relative items-center',
        caption_label: 'leading-6 text-[1.125rem] font-semibold',
        nav: 'space-x-1 flex items-center',
        nav_button: cn(
          buttonVariants({ variant: 'calendarButton' }),
          'h-6 w-6 bg-transparent p-0 opacity-100 hover:!border-0'
        ),
        nav_button_previous: 'absolute left-1',
        nav_button_next: 'absolute right-1',
        nav_icon: 'w-[0.5rem] h-[0.813rem]',
        table: 'w-full border-collapse',
        head_row: 'flex gap-2',
        head_cell:
          'text-muted-foreground font-medium text-sm h-10 w-10 flex justify-center items-center',
        row: 'flex w-full mt-2',
        cell: 'h-10 w-10 px-1 first:pl-0 last:pr-0 box-content text-center text-base p-0 relative focus-within:relative z-20',
        day: cn(
          buttonVariants({ variant: 'calendarButton', size: 'calendarButton' }),
          'disabled:opacity-50'
        ),
        day_selected: 'bg-primaryColor text-primary-foreground hover:bg-primaryColor',
        day_today: 'day-today',
        day_outside: 'day-outside',
        day_disabled: 'text-lightGrey2 disabled:opacity-100',
        day_hidden: 'invisible',
        ...classNames,
      }}
      data-testid={'IB-Date-Picker-Calendar'}
      {...props}
    />
  );
}
CalendarSingle.displayName = 'CalendarSingle';

export { CalendarSingle };
