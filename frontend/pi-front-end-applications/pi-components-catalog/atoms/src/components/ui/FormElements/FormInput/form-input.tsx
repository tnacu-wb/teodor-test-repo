import { cn, findError } from '@whitbread-eos/utils';
import Image from 'next/image';
import * as React from 'react';

import { ErrorTooltip } from '../../Tooltip';

export type InputProps = React.InputHTMLAttributes<HTMLInputElement>;

interface IBFormInputProps extends InputProps {
  errors?: Record<string, any>;
  errorIcon: string;
  inputIcon?: string;
  showLabel?: boolean;
  showErrorTooltip?: boolean;
  upperCase?: boolean;
  containerClassName?: string;
  ariaLabel?: string;
}

const FormInput = React.forwardRef<HTMLInputElement, IBFormInputProps>(
  ({ className, ...props }, ref) => {
    const {
      showLabel = true,
      showErrorTooltip = true,
      upperCase,
      name = 'defaultName',
      placeholder,
      id,
      disabled = false,
      type = 'text',
      value,
      onChange,
      errors,
      errorIcon,
      inputIcon,
      containerClassName,
      ariaLabel,
      onBlur = () => {
        return;
      },
      onFocus = () => {
        return;
      },
    } = props;

    const error = findError(name, errors);
    const hasError = !!error;
    const ariaRequired =
      props['aria-required'] ??
      (typeof placeholder === 'string' && placeholder.trimEnd().endsWith('*') ? true : undefined);

    return (
      <div className={containerClassName}>
        <div className="relative flex justify-center items-center">
          {inputIcon && (
            <Image
              className={cn(inputIconStyle, disabled ? disabledStyle : '')}
              src={inputIcon}
              alt={'Input Icon'}
              width={24}
              height={24}
            />
          )}
          <input
            name={name}
            data-testid={`${id}-Form-Input`}
            id={id}
            type={type}
            aria-label={showLabel ? undefined : ariaLabel}
            aria-required={ariaRequired}
            className={cn(
              inputStyle,
              hasError ? '!outline !outline-2 !outline-error !-outline-offset-2' : '',
              inputIcon ? 'pl-14' : '',
              upperCase && value ? 'uppercase' : '',
              className
            )}
            ref={ref}
            value={value}
            disabled={disabled}
            placeholder={placeholder}
            onChange={onChange}
            onBlur={onBlur}
            onFocus={onFocus}
          />
          {showLabel && (
            <label
              htmlFor={id}
              className={cn(
                labelStyle(disabled),
                hasError ? '!text-error' : '',
                !!value || hasError ? '!block' : ''
              )}
              data-testid={`${id}-Label`}
            >
              {placeholder}
            </label>
          )}
        </div>
        {showErrorTooltip && (
          <ErrorTooltip
            icon={errorIcon}
            content={error}
            open={hasError}
            testId={`${id}-Error-Tooltip`}
            className="!flex"
            mobile
          />
        )}
      </div>
    );
  }
);
FormInput.displayName = 'Form Input';

const inputIconStyle = 'absolute left-4 w-6 h-6 text-transparent z-28';
const labelStyle = (disabled: boolean) =>
  `hidden absolute peer-focus:!block ${
    disabled ? 'text-lightGrey1' : 'text-darkGrey1'
  } peer-focus:text-primaryColor -top-2.5 left-0 ml-3 text-sm px-1 bg-baseWhite`;
const inputStyle =
  'peer flex h-14 min-w-[110px] text-darkGrey1 border-lightGrey1 focus:outline-primaryColor focus:outline-2 hover:border-darkGrey1 w-full rounded border bg-transparent p-4 text-base transition-colors placeholder:text-darkGrey2 disabled:cursor-not-allowed disabled:opacity-50 disabled:hover:border-lightGrey1 bg-baseWhite';
const disabledStyle = 'opacity-50 cursor-not-allowed hover:border-lightGrey1';
export { FormInput };
