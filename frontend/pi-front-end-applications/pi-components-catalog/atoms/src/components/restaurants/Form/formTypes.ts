import { StyleProps } from '@chakra-ui/react';
import { ReactNode, FunctionComponent, Dispatch, SetStateAction } from 'react';
import type { Control, FieldErrors, FieldError, UseFormSetValue } from 'react-hook-form';
import * as z from 'zod';

import { ButtonProps } from '../../Button/Button.component';
import { DropdownOption } from '../../Dropdown/Dropdown.component';
import { FORM_FIELD_TYPES, FORM_BUTTON_TYPES } from './formContants';

type GenericFunction = (...args: any[]) => any;
type FormFieldsType = typeof FORM_FIELD_TYPES;
type FormButtonsType = typeof FORM_BUTTON_TYPES;
export type FormSetValueKeyType = string | number | object;

export type FormFieldsValuesType = FormFieldsType[keyof FormFieldsType];
export type FormButtonsValuesType = FormButtonsType[keyof FormButtonsType];
export type FormDropdownOptionType = DropdownOption;

export interface TableBookingDetails {
  firstname: string;
  lastname: string;
  email: string;
  phone: string;
  adult: string;
  child: string;
}
export interface FieldsType {
  optional?: boolean;
  className?: string;
  isPrivacStatement?: boolean;
  tabLabel?: string;
  privactStatementLinkText?: string;
  tapLabel?: string;
  type?: FormFieldsValuesType;
  enquiryForm?: FieldsType[];
  tableBookingForm?: FieldsType[];
  userDetails?: FieldsType[];
  id?: string;
  name: string;
  optionalText?: string;
  label?: string;
  charLimit?: number;
  relatedFields?: FieldsType[];
  props?: {
    [key: string]: string | number | boolean | object;
  };
  styles?: StyleProps;
  dropdownOptions?: FormDropdownOptionType[];
  content?: ReactNode;
  testid?: string;
  Component?: FunctionComponent<any>;
  hidden?: boolean;
  action?: (data?: object) => void;
  onChange?: (value: string | object | number | undefined) => void;
}
export interface FormErrorProps {
  errors?: FieldErrors<{ [key: string]: string | number }>;
  name: string;
  message?: string;
}

export interface ButtonsType {
  type: FormButtonsValuesType;
  label: string[];
  action: (data?: object) => void;
  styles?: StyleProps;
  props: {
    variant: ButtonProps['variant'];
    size: ButtonProps['size'];
    disabled?: ButtonProps['disabled'];
  };
  testid?: string;
}
export interface FormButtonsProps {
  isEnquiry?: boolean;
  formStepOneCompleted?: boolean;
  buttonsContainerStyles?: StyleProps;
  buttons: ButtonsType[];
}

export interface FormFieldProps {
  adult?: number;
  childrenToggle?: string;
  optionalText?: string;
  enquirySubheading?: string;
  enquiryHeading?: string;
  specialRequestLabel?: string;
  childrenValue?: number;
  isEnquiry?: boolean;
  time?: string;
  setIsEnquiry?: Dispatch<SetStateAction<boolean>>;
  setIsMenuOptionAvailable?: Dispatch<SetStateAction<boolean>>;
  selectedDateValue?: string;
  control: Control<any>;
  formField: FieldsType;
  formStepOneCompleted?: boolean;
  errors?: FieldErrors<{ [key: string]: string | number }>;
  optional?: boolean;
  isInitialFormValid?: () => boolean;
  getValues?: (name?: string) => any;
  selectedTimeSlot?: string;
  setValue?: any;
  handleSetValue?: UseFormSetValue<{ [key: string]: FormSetValueKeyType }>;
  clearErrors?: () => void;
  handleResetField?: (fieldName: string, options?: Record<string, boolean | any>) => void;
  reset?: <T>(values?: T, options?: Record<string, boolean>) => void;
  handleSetError?: (
    fieldName: string,
    error: Record<string, string>,
    config?: { shouldFocus: boolean }
  ) => void;
  handleClearErrors?: (fieldName?: string | string[]) => void;
  handleTriggerValidation?: (fieldsName: string | string[]) => void;
}
export interface FormRelatedFieldsProps {
  fieldName: string;
  relatedFields: FieldsType[];
  control: Control<any>;
  errors?: FieldErrors<{ [key: string]: string | number }>;
  getValues?: (name?: string) => any;
  handleSetValue?: UseFormSetValue<{ [key: string]: string | number | object }>;
  handleResetField?: (fieldName: string, options?: Record<string, boolean>) => void;
  reset?: <T>(values?: T, options?: Record<string, boolean>) => void;
}
export interface FormProps {
  id?: string;
  autoComplete?: string;
  childrenToggle?: string;
  enquiryHeading?: string;
  enquirySubheading?: string;
  optionalText?: string;
  specialRequestLabel?: string;
  returnToHome?: string;
  locationName?: string;
  subLocationName?: string;
  setIsEnquiry?: Dispatch<SetStateAction<boolean>>;
  isEnquiry?: boolean;
  elements: {
    formStyles?: StyleProps;
    fieldsContainerStyles?: StyleProps;
    buttonsContainerStyles?: StyleProps;
    fields: FieldsType[];
    buttons?: ButtonsType[];
    bottomFields?: FieldsType[];
    onSubmitAction?: (data?: object) => void;
  };
  errorsOrder?: string[];
  defaultValues: {
    [key: string]: string | number | boolean | object;
  };
  validationSchema?: z.AnyZodObject | z.AnyZodObject[];
  setIsMenuOptionAvailable?: Dispatch<SetStateAction<boolean>>;
  getFormState?: (
    data?: object | FormProps['defaultValues'],
    errors?: FieldErrors<{ [key: string]: string | number }>
  ) => void;
  defaultErrors?: {
    [key: string]: FieldError;
  };
  testid?: string;
  resetForm?: number;
  onChange?: GenericFunction;
}

export interface SubmitBook {
  [key: string]: string | number | boolean | object;
}

export interface Occassion {
  id: string;
  available: boolean;
  name: string;
}

export interface Slots {
  dates: SlotDates[];
  date: string;
}

interface SlotDates {
  breakFastAvailable: boolean;
  dinnerAvailable: boolean;
  lunchAvailable: boolean;
  sessionDto: sessionDto[];
}

interface sessionDto {
  breakFast: {
    time: string;
    available: boolean;
    totalCapacity: number;
    remainingCapacity: number;
    canEnquire: boolean;
    closed: boolean;
  };
}
export interface FormFields {
  adults: number;
  adultsByEnquiry: string;
  children: number;
  childrenByEnquiry: string;
  consent: boolean;
  date: string;
  emailAddress: string;
  firstname: string;
  highchair: number;
  lastname: string;
  occasionId: string;
  privacyStatement: boolean;
  siteId: string;
  specialRequest: string;
  telephoneNumber: string;
  time: string;
  wheelchair: boolean;
}

export interface Menu {
  id: string;
  name: string;
  available: boolean;
}

export interface MenusData {
  menu: {
    menus: Menu[];
  };
}

export interface Errors {
  message: string;
}
export interface Error {
  response: {
    errors: Errors[];
  };
}
