'use client';

import { Language } from '@whitbread-eos/api';
import { GLOBALS, cn, formatIBAssetsUrl } from '@whitbread-eos/utils';
import { getCountriesList } from '@whitbread-eos/utils/server';
import Image from 'next/image';
import * as React from 'react';
import { useState, useEffect } from 'react';

import { Popover, PopoverTrigger, PopoverContent } from '../../Popover/index';
import { Skeleton } from '../../Skeleton/index';
import { ErrorTooltip } from '../../Tooltip/index';

interface IBFormPhoneOption {
  dialingCode: string;
  countryName: string;
  flagSrc: string;
  countryCode: string;
}

interface PhoneValue {
  prefix: string;
  phoneNumber: string;
  countryCode?: string;
}

interface IBFormPhoneProps {
  language: Language;
  name: string;
  placeholder: string;
  id: string;
  disabled?: boolean;
  value: PhoneValue;
  onChange?: (value: PhoneValue) => void;
  onBlur?: React.FocusEventHandler<HTMLInputElement>;
  onFocus?: React.FocusEventHandler<HTMLInputElement>;
  errors?: Record<string, any>;
  errorIcon: string;
  arrowIcon: string;
  className: string;
}

const FormPhone = React.forwardRef<HTMLInputElement, IBFormPhoneProps>(
  (
    {
      language,
      className,
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
      arrowIcon,
    },
    ref
  ) => {
    const [options, setOptions] = useState<Array<IBFormPhoneOption>>([]);
    const [selectedOption, setSelectedOption] = useState<IBFormPhoneOption>();
    const [isLoading, setIsLoading] = useState(true);
    const [isOpen, setIsOpen] = useState(false);

    const error = errors?.[name]?.phoneNumber || errors?.[name]?.prefix;
    const hasError = !!error?.type;
    const defaultCountry =
      language === GLOBALS.language.EN ? GLOBALS.localeUpper.GB : GLOBALS.localeUpper.DE;

    useEffect(() => {
      const fetchListOfCountries = async () => {
        const countriesList = await getCountriesList(language);
        if (!countriesList) {
          return;
        }
        const sortedCountriesList = sortOptions(countriesList);
        setIsLoading(false);
        setOptions(sortedCountriesList);

        const defaultOption = sortedCountriesList.find((option: IBFormPhoneOption) => {
          if (value?.countryCode) {
            return option.countryCode === value.countryCode;
          } else if (value?.prefix) {
            return value.prefix === '+44'
              ? option.countryCode === 'GB'
              : option.dialingCode === value.prefix;
          }
          return option.countryCode === defaultCountry;
        });
        onChange({ ...value, prefix: defaultOption?.dialingCode ?? '' });
        setSelectedOption(defaultOption);
      };
      if (!disabled) {
        fetchListOfCountries();
      }
    }, []);

    const sortOptions = (options: Array<IBFormPhoneOption>) => {
      return options.sort((a, b) => {
        if (a.countryCode === defaultCountry) return -1;
        if (b.countryCode === defaultCountry) return 1;
        return a.countryName.localeCompare(b.countryName);
      });
    };

    const handleNumberChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      onChange({ ...value, phoneNumber: e.target.value });
    };

    const handleOptionChange = (option: IBFormPhoneOption) => {
      onChange({ ...value, prefix: option.dialingCode });
      setSelectedOption(option);
      setIsOpen(!isOpen);
    };

    const toggleDropdown = () => {
      if (disabled) return;
      setIsOpen(!isOpen);
    };

    const renderCountrySelect = () => {
      return isLoading ? (
        <Skeleton className={countrySkeletonStyle} />
      ) : (
        <PopoverTrigger asChild>
          <div className={countrySelectStyle} onClick={toggleDropdown}>
            <Image
              className={cn(inputIconStyle, disabled ? disabledStyle : '')}
              src={formatIBAssetsUrl(selectedOption?.flagSrc ?? '')}
              alt={'Country Flag'}
              width={24}
              height={24}
              unoptimized={true}
            />
            <span
              className={cn(selectButtonStyle, disabled ? disabledStyle : '')}
              data-testid={`${id}-IB-Form-Select-Button`}
            >
              {selectedOption?.dialingCode ?? ''}
            </span>
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
        </PopoverTrigger>
      );
    };

    const renderButton = (
      <div className="relative flex justify-center items-center">
        {!disabled && renderCountrySelect()}
        <input
          name={name}
          data-testid={`${id}-Form-Input`}
          id={id}
          className={cn(
            inputStyle,
            hasError ? '!outline !outline-2 !outline-error !-outline-offset-2' : '',
            isOpen ? 'outline outline-primaryColor outline-2 -outline-offset-2' : '',
            disabled ? 'pl-4' : '',
            className
          )}
          ref={ref}
          value={value?.phoneNumber ?? ''}
          disabled={disabled}
          placeholder={placeholder}
          type="text"
          onChange={handleNumberChange}
          onBlur={onBlur}
          onFocus={onFocus}
        />
        <label
          htmlFor={id}
          className={cn(
            labelStyle,
            hasError ? '!text-error' : '',
            value?.phoneNumber || hasError ? '!block' : '',
            isOpen ? 'text-primaryColor' : ''
          )}
          data-testid={`${id}-Label`}
        >
          {placeholder}
        </label>
      </div>
    );

    const renderOptions = () => {
      return options?.map((option: IBFormPhoneOption) => (
        <button
          key={option.countryName}
          data-testid={`${id}-${option.countryCode}-Option`}
          type="button"
          className={optionStyle}
          onClick={() => handleOptionChange(option)}
        >
          <Image
            className="w-6 h-6"
            src={formatIBAssetsUrl(option.flagSrc)}
            alt={`${option.countryName} Flag`}
            width={24}
            height={24}
            unoptimized={true}
          />
          <span className="w-[2.85rem]">{option.dialingCode}</span>
          <span>{option.countryName}</span>
        </button>
      ));
    };

    return (
      <div data-testid={`${id}-IB-Form-Select`} className="relative">
        <Popover open={isOpen} onOpenChange={toggleDropdown}>
          {renderButton}
          <ErrorTooltip
            icon={errorIcon}
            content={error?.message}
            open={hasError}
            testId={`${id}-Error-Tooltip`}
            className="!flex"
            mobile
          />
          <PopoverContent
            align="start"
            avoidCollisions={false}
            data-testid={`${id}-IB-Form-Select-Dropdown`}
            className={popoverStyle}
          >
            {renderOptions()}
          </PopoverContent>
        </Popover>
      </div>
    );
  }
);
FormPhone.displayName = 'Form Select';

const inputIconStyle = 'w-6 h-6 text-transparent z-28';
const labelStyle =
  'hidden absolute peer-focus:!block text-darkGrey1 peer-focus:text-primaryColor -top-2.5 left-0 ml-3 text-sm px-1 bg-baseWhite';
const selectButtonStyle =
  'flex text-darkGrey1 bg-transparent text-base transition-colors w-[3.2rem]';
const popoverStyle = 'w-full flex flex-col max-h-60 overflow-scroll p-0';
const optionStyle =
  'flex items-center gap-4 w-full hover:bg-lightGrey5 text-left text-sm py-2.5 px-4 focus-visible:outline-none';
const arrowIconStyle = 'w-6 h-6 cursor-pointer';
const disabledStyle = 'opacity-50 cursor-not-allowed hover:border-lightGrey1';
const inputStyle =
  'peer flex h-14 text-darkGrey1 border-lightGrey1 focus:outline-primaryColor focus:outline-2 hover:border-darkGrey1 w-full rounded border bg-transparent p-4 pl-[8.45rem] text-base transition-colors placeholder:text-darkGrey2 disabled:cursor-not-allowed disabled:opacity-50 disabled:hover:border-lightGrey1 bg-baseWhite';
const countrySelectStyle =
  'absolute left-0 flex justify-center items-center w-[8.45rem] h-full p-4 gap-[2px] cursor-pointer';
const countrySkeletonStyle = 'absolute left-0 w-[6.375rem] h-6 m-4';

export { FormPhone };
