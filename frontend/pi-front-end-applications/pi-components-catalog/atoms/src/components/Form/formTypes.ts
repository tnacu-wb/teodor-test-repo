import { StyleProps } from '@chakra-ui/react';
import { FunctionComponent, ReactNode } from 'react';
import type { Control, FieldError, FieldErrors, UseFormSetValue } from 'react-hook-form';
import * as yup from 'yup';

import { ButtonProps } from '../Button/Button.component';
import { DropdownOption } from './../Dropdown/Dropdown.component';
import { FORM_BUTTON_TYPES, FORM_FIELD_TYPES } from './formConstants';

type FormFieldsType = typeof FORM_FIELD_TYPES;
type FormButtonsType = typeof FORM_BUTTON_TYPES;
type FormSetValueKeyType = string | number | object;

export type FormFieldsValuesType = FormFieldsType[keyof FormFieldsType];
export type FormButtonsValuesType = FormButtonsType[keyof FormButtonsType];
export type FormRadioGroupOptionType = {
  value: string;
  label: string;
  testid?: string;
  isChecked?: boolean;
  Component?: FunctionComponent<any>;
};

export type FormDropdownOptionType = DropdownOption;

export type ValidationType = (value: string) => boolean | string;

export type ResetActionType = <
  T extends { [x: string]: string | number | boolean | object | undefined },
>(
  values?: T,
  options?: Record<string, boolean>
) => void;

export interface FieldsType {
  type: FormFieldsValuesType;
  id?: string;
  name: string;
  label: string;
  relatedFields?: {
    [key: string]: FieldsType[];
  };
  props?: {
    [key: string]: any;
  };
  styles?: StyleProps;
  options?: FormRadioGroupOptionType[];
  dropdownOptions?: FormDropdownOptionType[];
  content?: ReactNode;
  testid?: string;
  Component?: FunctionComponent<any>;
  hidden?: boolean;
  action?: (data?: object) => void;
  onChange?: (value: string | object | number | boolean | undefined) => void;
  bkndData?: any;
  dependantOn?: string | string[];
  isDisabled?: boolean;
  onChangeAction?: (
    value: string | number | object | undefined,
    handleSetValue: UseFormSetValue<{ [key: string]: FormSetValueKeyType }> | undefined
  ) => void;
  errorStyles?: { containerStyle?: StyleProps; textStyle?: StyleProps };
}
export interface ButtonsType {
  type: FormButtonsValuesType;
  label: string;
  action: (data?: object) => void;
  styles?: StyleProps;
  props: Omit<ButtonProps, 'children' | 'onClick' | 'type'>;
  testid?: string;
}

export interface FormProps {
  id?: string;
  autoComplete?: string;
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
  validationSchema?: yup.AnyObjectSchema;
  getFormState?: (
    data?: object | FormProps['defaultValues'],
    errors?: FieldErrors<{ [key: string]: string | number }>
  ) => void;
  defaultErrors?: {
    [key: string]: FieldError;
  };
  testid?: string;
  resetForm?: number;
  fieldsetDisabled?: boolean;
  onChange?: any;
}

export interface FormWithAccordianProps extends Omit<FormProps, 'elements'> {
  accordionIndex: number;
  handleAccordionToggle: (data: number) => void;
  accordians: {
    title: string;
    onToggleSection: () => void;
    elements: {
      formStyles?: StyleProps;
      fieldsContainerStyles?: StyleProps;
      buttonsContainerStyles?: StyleProps;
      fields: FieldsType[];
      buttons?: ButtonsType[];
      bottomFields?: FieldsType[];
      onSubmitAction?: (data?: object) => void;
    };
  }[];
}
export interface FormButtonsProps {
  buttonsContainerStyles?: StyleProps;
  buttons: ButtonsType[];
}
export interface FormErrorProps {
  errors?: FieldErrors<{ [key: string]: string | number }>;
  name: string;
  message?: string;
  extraStyles?: { containerStyle?: StyleProps; textStyle?: StyleProps };
}
export interface FormFieldProps {
  control: Control<any>;
  formField: FieldsType;
  onClick?: () => void;
  errors?: FieldErrors<{ [key: string]: string | number }>;
  getValues: (name?: string) => any;
  handleSetValue?: UseFormSetValue<{ [key: string]: FormSetValueKeyType }>;
  handleResetField?: (fieldName: string, options?: Record<string, boolean | any>) => void;
  handleSetError?: (
    fieldName: string,
    error: Record<string, string>,
    config?: { shouldFocus: boolean }
  ) => void;
  handleClearErrors?: (fieldName?: string | string[]) => void;
  handleTriggerValidation?: (fieldsName: string | string[]) => void;
  reset?: ResetActionType;
  btnProps?: any;
  onChangeAction?: (
    value: string | number | object | undefined,
    handleSetValue: UseFormSetValue<{ [key: string]: FormSetValueKeyType }> | undefined
  ) => void;
}
export interface FormRelatedFieldsProps {
  fieldName: string;
  relatedFields: {
    [key: string]: FieldsType[];
  };
  control: Control<any>;
  errors?: FieldErrors<{ [key: string]: string | number }>;
  getValues: (name?: string) => any;
  handleSetValue?: UseFormSetValue<{ [key: string]: string | number | object }>;
  handleResetField?: (fieldName: string, options?: Record<string, boolean | any>) => void;
  handleSetError?: (
    fieldName: string,
    error: Record<string, string>,
    config?: { shouldFocus: boolean }
  ) => void;
  handleClearErrors?: (fieldName?: string | string[]) => void;
  reset?: ResetActionType;
}
export interface FormInputProps {
  control: Control<any>;
  formField: FieldsType;
  errors?: FieldErrors<{ [key: string]: string | number }>;
}

export interface FormTextAreaProps {
  control: Control<any>;
  formField: FieldsType;
  errors?: FieldErrors<{ [key: string]: string | number }>;
}

export interface FormRadioGroupProps {
  control: Control<any>;
  formField: FieldsType;
  errors?: FieldErrors<{ [key: string]: string | number }>;
  handleSetValue?: UseFormSetValue<{ [key: string]: FormSetValueKeyType }>;
  onChangeAction?: (
    value: string | number | object | undefined,
    handleSetValue: UseFormSetValue<{ [key: string]: FormSetValueKeyType }> | undefined
  ) => void;
}

export interface FormDropdownProps {
  control: Control<any>;
  formField: FieldsType;
  errors?: FieldErrors<{ [key: string]: string | number }>;
  handleSetValue?: UseFormSetValue<{ [key: string]: FormSetValueKeyType }>;
  onChangeAction?: (
    value: string | number | object | undefined,
    handleSetValue: UseFormSetValue<{ [key: string]: FormSetValueKeyType }> | undefined
  ) => void;
}
export interface FormDynamicFieldProps {
  control: Control<any>;
  formField: FieldsType;
  errors?: FieldErrors<{ [key: string]: string | number }>;
  handleSetValue?: UseFormSetValue<{ [key: string]: FormSetValueKeyType }>;
  handleResetField?: (fieldName: string, options?: Record<string, boolean | any>) => void;
  handleSetError?: (
    fieldName: string,
    error: Record<string, string>,
    config?: { shouldFocus: boolean }
  ) => void;
  handleClearErrors?: (fieldName?: string | string[]) => void;
  handleTriggerValidation?: (fieldsName: string | string[]) => void;
  getValues: (name?: string) => any;
  reset?: ResetActionType;
}

export interface FormCheckboxProps {
  control: Control<any>;
  formField: FieldsType;
  errors?: FieldErrors<{ [key: string]: string | number }>;
}

export interface FormDynamicFieldCompProps {
  formField: FieldsType;
  errors?: FieldErrors<{ [key: string]: string | number }>;
  field: {
    name: string;
    value: string;
    onChange: (val: string | boolean | number) => void;
    onBlur: () => void;
  };
  handleSetValue?: UseFormSetValue<{ [key: string]: FormSetValueKeyType }>;
  handleResetField?: (fieldName: string, options?: Record<string, boolean | any>) => void;
  handleSetError?: (
    fieldName: string,
    error: Record<string, string>,
    config?: { shouldFocus: boolean }
  ) => void;
  handleClearErrors?: (fieldName?: string | string[]) => void;
  handleTriggerValidation?: (fieldsName: string | string[]) => void;
  getValues: (name?: string) => any;
  reset?: ResetActionType;
  control?: Control<any>;
}
