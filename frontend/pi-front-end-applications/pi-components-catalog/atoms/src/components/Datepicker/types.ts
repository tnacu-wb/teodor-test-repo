import { BoxProps } from '@chakra-ui/react';

export type datepickerDate = Date | null;

export type datepickerLabels = {
  resetButtonLabel: string;
  doneButtonLabel: string;
  todayLabel: string;
  tomorrowLabel: string;
  checkoutLabel: string;
  checkInLabel?: string;
};

export type datepickerStyles = {
  inputGroupStyles: BoxProps;
  datepickerInputElementStyles: BoxProps;
  bookingDatepickerSize?: BoxProps;
  iconStyles: BoxProps;
  errorInputGroupStyles?: BoxProps;
  errorInputElementStyles?: BoxProps;
  errorMarginBottom?: { sm: string; xs: string; mobile: string };
};

export type datepickerTranslations = {
  content: {
    global: {
      done: string;
      today: string;
      tomorrow: string;
    };
  };
  form: {
    checkout: string;
  };
  datePicker: { reset: string; checkOut: string };
};
