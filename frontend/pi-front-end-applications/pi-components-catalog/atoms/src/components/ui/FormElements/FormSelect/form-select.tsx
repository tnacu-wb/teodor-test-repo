'use client';

import { IBFormSelectOption } from '@whitbread-eos/api';
import { cn } from '@whitbread-eos/utils';
import Image from 'next/image';
import * as React from 'react';
import { useState, useEffect } from 'react';

import { Popover, PopoverTrigger, PopoverContent } from '../../Popover/index';
import { Skeleton } from '../../Skeleton/index';
import { ErrorTooltip } from '../../Tooltip';

export interface IBFormSelectProps {
  name: string;
  placeholder: string;
  id: string;
  disabled: boolean;
  value: IBFormSelectOption;
  onChange?: (value: IBFormSelectOption) => void;
  onBlur?: () => void;
  onFocus?: () => void;
  errors?: Record<string, any>;
  errorIcon: string;
  inputIcon?: string;
  arrowIcon: string;
  className: string;
  buttonClassName?: string;
  popoverClassName?: string;
  options: Array<IBFormSelectOption>;
  showLabel?: boolean;
  isLoading?: boolean;
  showErrorTooltip?: boolean;
  clearErrors?: () => void;
}

const FormSelect = React.forwardRef<HTMLButtonElement, IBFormSelectProps>(
  (
    {
      showLabel = true,
      className,
      buttonClassName,
      popoverClassName,
      name,
      placeholder,
      id,
      disabled = false,
      value,
      onChange = () => {
        return;
      },
      onBlur = () => {
        return;
      },
      onFocus = () => {
        return;
      },
      errors,
      errorIcon,
      inputIcon = '',
      arrowIcon,
      options,
      isLoading = false,
      showErrorTooltip = true,
      clearErrors,
    },
    ref
  ) => {
    const [selectedOption, setSelectedOption] = useState<IBFormSelectOption>(value);
    const [isOpen, setIsOpen] = useState(false);
    const error = errors?.[name]?.value || errors?.[name]?.displayValue;
    const hasError = !!error?.type || !!error?.message;

    useEffect(() => {
      setSelectedOption(value);
    }, [value]);

    const handleChange = (value: IBFormSelectOption) => {
      onChange(value);
      setSelectedOption(value);
      setIsOpen(false);
      clearErrors?.();
    };

    const toggleDropdown = (value: boolean) => {
      if (disabled) return;
      setIsOpen(value);
      if (value) {
        onFocus();
      } else {
        onBlur();
      }
    };

    const hasValue =
      selectedOption?.value !== null &&
      selectedOption?.value !== undefined &&
      selectedOption?.value !== '';

    const renderButton = (
      <div className={cn('relative flex justify-center items-center', buttonClassName)}>
        {(inputIcon || selectedOption?.icon) && (
          <Image
            className={cn(inputIconStyle, disabled ? disabledStyle : '')}
            src={inputIcon ? inputIcon : (selectedOption?.icon ?? '')}
            alt={'Select Icon'}
            width={24}
            height={24}
            unoptimized={true}
          />
        )}
        <button
          type="button"
          ref={ref}
          disabled={disabled}
          className={cn(
            selectButtonStyle,
            isOpen ? 'outline outline-primaryColor outline-2 -outline-offset-2' : '',
            hasError ? '!outline !outline-2 !outline-error !-outline-offset-2' : '',
            disabled ? disabledStyle : '',
            inputIcon || selectedOption?.icon ? 'pl-14' : ''
          )}
          onClick={() => toggleDropdown(!isOpen)}
          data-testid={`${id}-IB-Form-Select-Button`}
        >
          {isLoading ? (
            <Skeleton className={countrySkeletonStyle} />
          ) : hasValue ? (
            <span className={textStyle}>{selectedOption.displayValue}</span>
          ) : (
            <span className={`${placeholderStyle} ${textStyle}`}>{placeholder}</span>
          )}
        </button>
        {showLabel && (
          <label
            htmlFor={id}
            className={cn(
              labelStyle,
              hasError ? '!text-error' : '',
              hasValue || hasError ? '!block' : '',
              disabled ? 'text-lightGrey1' : '',
              isOpen ? '!block text-primaryColor' : ''
            )}
            data-testid={`${id}-Label`}
          >
            {placeholder}
          </label>
        )}
        <Image
          className={cn(
            arrowIconStyle,
            isOpen ? 'transform -scale-y-100' : '',
            disabled ? disabledStyle : ''
          )}
          src={arrowIcon}
          alt={'Iron icon'}
          width={24}
          height={24}
        />
      </div>
    );

    const renderOptions = () => {
      return options?.map((option: IBFormSelectOption, index) => (
        <button
          key={`${option.value}-${index}`}
          data-testid={`${id}-${option.value}-Option`}
          type="button"
          className={optionStyle}
          onClick={() => handleChange(option)}
        >
          {option.icon && (
            <Image
              src={option.icon}
              alt={option.displayValue}
              width={24}
              height={24}
              unoptimized={true}
              className="mr-4"
            />
          )}
          <span>{option.displayValue}</span>
        </button>
      ));
    };

    return (
      <div data-testid={`${id}-IB-Form-Select`} className={cn('relative', className)}>
        <Popover open={isOpen} onOpenChange={() => toggleDropdown(!isOpen)}>
          <PopoverTrigger asChild>{renderButton}</PopoverTrigger>
          {showErrorTooltip && !isOpen && (
            <ErrorTooltip
              icon={errorIcon}
              content={error?.message}
              open={hasError}
              testId={`${id}-Error-Tooltip`}
              className="!flex"
              mobile
            />
          )}
          <PopoverContent
            align="start"
            avoidCollisions={false}
            data-testid={`${id}-IB-Form-Select-Dropdown`}
            className={cn(popoverStyle, popoverClassName)}
          >
            {renderOptions()}
          </PopoverContent>
        </Popover>
      </div>
    );
  }
);
FormSelect.displayName = 'Form Select';

const inputIconStyle = 'absolute left-4 w-6 h-6 text-transparent z-28';
const labelStyle =
  'hidden absolute text-darkGrey1 -top-2.5 left-0 ml-3 text-sm px-1 bg-baseWhite peer-focus:text-primaryColor';
const selectButtonStyle =
  'peer flex h-14 text-darkGrey1 border-lightGrey1 hover:border-darkGrey1 w-full rounded border bg-transparent p-4 pr-14 text-base transition-colors focus:outline-primaryColor bg-baseWhite';
const popoverStyle = 'w-full flex flex-col max-h-60 overflow-scroll p-0';
const placeholderStyle = 'text-darkGrey2';
const optionStyle =
  'flex w-full items-center hover:bg-lightGrey5 text-left text-sm py-2.5 px-4 focus-visible:outline-none';
const arrowIconStyle = 'absolute right-4 w-6 h-6 cursor-pointer';
const disabledStyle = 'opacity-50 cursor-not-allowed hover:border-lightGrey1';
const textStyle = 'truncate';
const countrySkeletonStyle = 'absolute left-0 w-[6.375rem] h-6 ml-4';
export { FormSelect };
