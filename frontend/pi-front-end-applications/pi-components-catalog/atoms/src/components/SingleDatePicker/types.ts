import { datepickerDate, datepickerStyles } from '../Datepicker/types';

export type PopperPlacement =
  | 'top'
  | 'top-start'
  | 'top-end'
  | 'bottom'
  | 'bottom-start'
  | 'bottom-end'
  | 'left'
  | 'left-start'
  | 'left-end'
  | 'right'
  | 'right-start'
  | 'right-end';

export type SingleDatePickerProps = {
  minDate: Date;
  inputLabel: string;
  name: string;
  inputPlaceholder: string;
  dateFormat: string;
  displayDateFormat: string;
  labels: SingleDatePickerLabels;
  maxDate?: Date;
  defaultStartDate?: datepickerDate | string;
  defaultEndDate?: datepickerDate;
  datepickerStyles: datepickerStyles;
  locale?: string;
  isDisabled?: boolean;
  isError?: boolean;
  isRightIcon?: boolean;
  onSelectDate: (date: datepickerDate) => void;
  onInputChange?: (params: Date | string) => void;
  onReset?: () => void;
  onDone?: (date: datepickerDate) => void;
  dataTestId?: string;
  popperPlacement?: PopperPlacement;
  skipFormatRules?: boolean;
  customHeader?: boolean;
  isClearable?: boolean;
  openToDate?: Date;
};

export type SingleDatePickerLabels = {
  todayLabel: string;
  tomorrowLabel: string;
};

export type { datepickerDate, datepickerStyles };
