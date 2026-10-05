import { BoxProps } from '@chakra-ui/react';

import { PopperPlacement } from '../../SingleDatePicker/types';

export type SingleDatepickerDate = Date;

export type SingleDatepickerStyles = {
  inputGroupStyles: BoxProps;
  datepickerInputElementStyles: BoxProps;
  bookingDatepickerSize?: BoxProps;
  iconStyles: BoxProps;
  errorInputGroupStyles?: BoxProps;
  errorInputElementStyles?: BoxProps;
  errorMarginBottom?: { sm: string; xs: string; mobile: string };
};

export type SingleDatePickerProps = {
  minDate?: Date;
  inputLabel: string;
  name: string;
  dateFormat: string;
  displayDateFormat: string | null;
  labels: SingleDatePickerLabels;
  maxDate?: Date;
  defaultStartDate?: SingleDatepickerDate | string;
  defaultEndDate?: SingleDatepickerDate;
  datepickerStyles: SingleDatepickerStyles;
  isDisabled?: boolean;
  isError?: boolean;
  isRightIcon?: boolean;
  onSelectDate: (date: SingleDatepickerDate) => void;
  onInputChange?: (params: Date | string) => void;
  onReset?: () => void;
  onDone?: (date: SingleDatepickerDate) => void;
  dataTestId?: string;
  popperPlacement?: PopperPlacement;
  skipFormatRules?: boolean;
  isEnquiry?: boolean;
  setValue?: any;
};

export type SingleDatePickerLabels = {
  todayLabel: string;
  tomorrowLabel: string;
};
