import { Box } from '@chakra-ui/react';
import type { DatepickerRangeSelectionDate, DatepickerSelectionDate } from '@whitbread-eos/api';
import {
  Datepicker,
  datepickerDate,
  datepickerStyles,
  datepickerTranslations,
  InfoMessage,
} from '@whitbread-eos/atoms';
import { add, differenceInDays } from 'date-fns';
import { useTranslation } from 'next-i18next';
import { useState } from 'react';

interface BookingDatepickerProps {
  maxNumberOfNights: number;
  maxArrivalDate: number;
  displayDateFormat: string;
  datepickerStyles: datepickerStyles;
  onSelectDates: (props: DatepickerSelectionDate) => void;
  inputPlaceholderDatepicker?: string;
  defaultStartDate?: datepickerDate;
  defaultEndDate?: datepickerDate;
  partialTranslations: datepickerTranslations;
  locale?: string;
  showErrorMessage?: boolean;
  errorMessage?: string;
  isError?: boolean;
  isLessThanSm: boolean | undefined;
  isDatePickerFocus?: boolean;
  displayDatesNotification?: boolean;
  disableFlip?: boolean;
}

export default function BookingDatepickerComponent({
  maxNumberOfNights,
  maxArrivalDate,
  displayDateFormat,
  onSelectDates,
  inputPlaceholderDatepicker,
  defaultStartDate,
  datepickerStyles,
  defaultEndDate,
  partialTranslations,
  locale,
  showErrorMessage,
  errorMessage,
  isError,
  isLessThanSm,
  isDatePickerFocus,
  displayDatesNotification,
  disableFlip,
}: Readonly<BookingDatepickerProps>) {
  const [maxDate, setMaxDate] = useState(add(new Date(), { days: maxArrivalDate }));
  const displayNewDatesNotification = displayDatesNotification && !defaultStartDate;
  const { t } = useTranslation();
  return (
    <Box position="relative" {...datepickerStyles.bookingDatepickerSize}>
      <Datepicker
        {...(locale === 'de' ? { locale: 'de' } : {})}
        minDate={new Date()}
        maxDate={maxDate}
        inputPlaceholder={
          inputPlaceholderDatepicker?.toString() ??
          `${partialTranslations?.content?.global?.today} | ${partialTranslations?.content?.global?.tomorrow}`
        }
        dateFormat={'dd MMM yyyy'}
        displayDateFormat={displayDateFormat}
        labels={{
          resetButtonLabel: partialTranslations.datePicker.reset,
          doneButtonLabel: partialTranslations.content.global.done,
          todayLabel: partialTranslations.content.global.today,
          tomorrowLabel: partialTranslations.content.global.tomorrow,
          checkoutLabel: partialTranslations.datePicker.checkOut,
          checkInLabel: t('dashboard.bookings.checkIn'),
        }}
        hasFooter={true}
        selectsRange={true}
        datepickerStyles={datepickerStyles}
        onSelectDates={handleSelectDates}
        onReset={handleReset}
        defaultStartDate={defaultStartDate}
        defaultEndDate={defaultEndDate}
        isError={isError}
        isDatePickerFocus={isDatePickerFocus}
        displayDatesNotification={displayDatesNotification}
        disableFlip={disableFlip}
      />
      {!isLessThanSm && showErrorMessage && errorMessage && (
        <InfoMessage infoMessage={errorMessage} otherStyles={{ ...alertStyles }} />
      )}
      {displayNewDatesNotification && (
        <InfoMessage
          infoMessage={t('ccui.search.enterDates')}
          otherStyles={{ ...newDatesNotifStyles }}
          variant={'Info'}
        />
      )}
    </Box>
  );

  function handleSelectDates(dates: DatepickerSelectionDate) {
    const [arrivalDate, departureDate] = dates as DatepickerRangeSelectionDate;

    onSelectDates?.(dates);

    if (departureDate) {
      setMaxDate(add(new Date(), { days: maxArrivalDate }));
      return;
    }

    if (
      arrivalDate &&
      differenceInDays(add(new Date(), { days: maxArrivalDate }), arrivalDate) < maxNumberOfNights
    ) {
      setMaxDate(add(new Date(maxDate), { days: 1 }));
      return;
    }

    arrivalDate && setMaxDate(add(new Date(arrivalDate), { days: maxNumberOfNights }));
  }

  function handleReset() {
    setMaxDate(add(new Date(), { days: maxArrivalDate }));

    onSelectDates?.([null, null]);
  }
}

const alertStyles = {
  w: {
    base: 'full',
    sm: '20rem',
    lg: 'full',
  },
  zIndex: 100,
};

const newDatesNotifStyles = {
  backgroundColor: 'tooltipInfo',
  w: {
    base: 'full',
    sm: '20rem',
    lg: 'full',
  },
  zIndex: 100,
  _before: {
    content: '" "',
    position: 'absolute',
    top: '-0.15rem',
    left: 'xs',
    width: '1.5rem',
    height: '0.75rem',
    zIndex: '-1',
    transform: 'rotate(-45deg)',
    bgColor: 'tooltipInfo',
    borderRadius: '3',
  },
};
