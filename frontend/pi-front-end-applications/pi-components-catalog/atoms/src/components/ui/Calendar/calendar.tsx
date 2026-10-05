'use client';

import { cn } from '@whitbread-eos/utils';
import * as React from 'react';
import { DayPicker, DayPickerRangeProps } from 'react-day-picker';

import { buttonVariants } from '../Button';

export type CalendarProps = DayPickerRangeProps;

interface ExtraCalendarProps extends CalendarProps {
  isCurrentMonth?: boolean;
  shouldHideNextMonthIcon?: boolean;
  isCheckoutDay365?: boolean;
  hideHeader?: boolean;
}

function Calendar({
  className,
  classNames,
  showOutsideDays = false,
  locale,
  isCurrentMonth,
  shouldHideNextMonthIcon,
  isCheckoutDay365,
  hideHeader = false,
  ...props
}: ExtraCalendarProps) {
  function findTargetMonth(element: Element | null): HTMLTableSectionElement | null {
    while (element && element.tagName !== 'DIV') {
      element = element.parentElement;
    }
    return element as HTMLTableSectionElement | null;
  }

  React.useEffect(() => {
    if (hideHeader) {
      const startDay = document.querySelector('.day-range-start');
      const targetMonth = startDay && findTargetMonth(startDay);
      if (targetMonth) {
        targetMonth.scrollIntoView({ behavior: 'instant', block: 'start' });
      }
    }
  }, [hideHeader]);

  return (
    <DayPicker
      locale={locale}
      showOutsideDays={showOutsideDays}
      className={cn('pt-4 px-2 pb-6 mobile:flex mobile:justify-center', className)}
      classNames={{
        months: `flex flex-col space-y-4 sm:space-x-4 sm:space-y-0 ${
          hideHeader ? 'sm:flex-col' : 'sm:flex-row'
        }`,
        month: 'space-y-4',
        caption: `flex pt-1 relative items-center ${
          hideHeader ? 'justify-start' : 'justify-center'
        }`,
        caption_label: 'leading-6 text-[1.125rem] font-semibold',
        nav: 'space-x-1 flex items-center',
        nav_button: cn(
          buttonVariants({ variant: 'calendarButton' }),
          'h-6 w-6 bg-transparent p-0 opacity-100  hover:!border-0'
        ),
        nav_button_previous: isCurrentMonth ? 'hidden' : 'absolute left-1',
        nav_button_next: shouldHideNextMonthIcon ? 'hidden' : 'absolute right-1',
        nav_icon: 'w-[0.5rem] h-[0.813rem]',
        table: 'w-full border-collapse',
        head_row: hideHeader ? 'hidden' : 'flex gap-2',
        head_cell: hideHeader
          ? 'hidden'
          : `text-muted-foreground font-medium text-sm h-10 w-10 flex justify-center items-center`,
        row: 'flex w-full mt-2',
        cell: `h-10 w-10 ${
          hideHeader ? 'md:w-14' : ''
        } px-1 first:pl-0 last:pr-0 box-content [&:has([aria-selected].only-start)]:ib-transparent [&:has([aria-selected].day-range-start)]:ib-day-range-start [&:has([aria-selected].day-range-end)]:ib-day-range-end text-center text-base p-0 relative [&:has([aria-selected].day-range-start)]:rounded-l-full [&:has([aria-selected].day-range-end)]:rounded-r-full [&:has([aria-selected])]:bg-linearGradient [&:has(.in-hover-range)]:bg-linearGradient [&:has(.in-hover-range.only-start)]:bg-linearGradient [&:has(.in-hover-range.only-start)]:!rounded-r-none focus-within:relative z-20`,
        day: cn(
          buttonVariants({ variant: 'calendarButton', size: 'calendarButton' }),
          isCheckoutDay365 ? 'disabled:opacity-100' : 'disabled:opacity-50'
        ),
        day_range_end: 'day-range-end hover:!bg-primaryColor hover:!text-primary-foreground',
        day_range_start: 'day-range-start hover:!bg-primaryColor hover:!text-primary-foreground',
        day_range_middle: 'aria-selected:text-darkGrey1 hover:!bg-white !bg-transparent',
        day_selected: 'bg-primaryColor text-primary-foreground',
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
Calendar.displayName = 'Calendar';

export { Calendar };
