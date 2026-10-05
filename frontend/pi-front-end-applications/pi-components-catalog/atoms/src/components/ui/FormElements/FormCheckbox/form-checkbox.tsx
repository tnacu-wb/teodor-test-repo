'use client';

import { findError, cn } from '@whitbread-eos/utils';
import * as React from 'react';

import { Checkbox } from '../../Checkbox/index';
import { ErrorTooltip } from '../../Tooltip/index';

export type InputProps = React.InputHTMLAttributes<HTMLInputElement>;

interface IBFormInputProps {
  label: string;
  value: boolean;
  onChange: (checked: boolean) => void;
  onValueChange?: (checked?: boolean) => void;
  labelClassName?: string;
  className?: string;
  name?: string;
  id?: string;
  disabled?: boolean;
  checkboxClassName?: string;
  errors?: Record<string, any>;
  errorIcon: string;
}

const FormCheckbox = React.forwardRef<HTMLInputElement, IBFormInputProps>(
  (
    {
      className,
      labelClassName,
      name = 'defaultName',
      label,
      id,
      disabled = false,
      value,
      onChange, //this is the react-hook-form field passed onChange, you shouldn't use it. Use onValueChange instead
      onValueChange,
      checkboxClassName,
      errors,
      errorIcon,
    },
    ref
  ) => {
    const error = findError(name, errors);
    const hasError = !!error;

    const handleChange = (value: boolean) => {
      onChange(value);
      onValueChange?.(value);
    };

    return (
      <div className="flex flex-col">
        <div ref={ref} className={cn('flex items-center', className)}>
          <Checkbox
            id={id}
            checked={value}
            onCheckedChange={handleChange}
            className={cn(
              checkboxStyle,
              checkboxClassName,
              hasError ? '!outline !outline-2 !outline-destructive !-outline-offset-2' : ''
            )}
            disabled={disabled}
            data-testid={`${id}-Form-Checkbox`}
            aria-checked={value}
            name={name}
          />
          <label
            htmlFor={id}
            className={cn(labelStyle, labelClassName)}
            data-testid={`${id}-Label`}
          >
            {label}
          </label>
        </div>
        <ErrorTooltip
          icon={errorIcon}
          content={error}
          open={hasError}
          testId={`${id}-Error-Tooltip`}
          className="!flex"
          mobile
        />
      </div>
    );
  }
);
FormCheckbox.displayName = 'FormCheckbox';

export { FormCheckbox };

const checkboxStyle = 'w-5 h-5 border-lightGrey1 text-primaryColor';
const labelStyle = 'ml-3';
