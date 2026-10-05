'use client';

import { cn } from '@whitbread-eos/utils';
import Image from 'next/image';
import * as React from 'react';
import { useState, useRef, useEffect } from 'react';

import { Popover, PopoverTrigger, PopoverContent } from '../Popover/index';

interface SelectOption {
  value: string | number;
  displayValue: string;
}

interface SelectProps {
  value: string | number;
  onOptionChange: (value: string | number) => void;
  triggerIcon: string;
  triggerContent: string;
  label: string;
  arrowIcon: string;
  testId: string;
  options: SelectOption[];
  showValueInstead: boolean;
}

const Select = ({
  label,
  value,
  arrowIcon,
  testId,
  triggerIcon,
  options,
  onOptionChange,
  showValueInstead = false,
}: Readonly<SelectProps>) => {
  const [isOpen, setIsOpen] = useState(false);
  const optionsRefs = useRef<(HTMLButtonElement | null)[]>([]); // Ref for each option button

  const handleChange = (value: string | number) => {
    onOptionChange(value);
    setIsOpen(!isOpen);
  };
  const findDisplayValue = (options: SelectOption[], value: string | number) => {
    const foundOption = options.find((option) => option.value === value);
    return foundOption ? foundOption.displayValue : '';
  };

  const handleTriggerKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    e.stopPropagation(); // Prevent the event from propagating to parent components
    if (e.key === 'Enter' || e.key === ' ') {
      if (!isOpen) {
        e.preventDefault(); // Prevent default only when toggling the dropdown
      }
      setIsOpen(!isOpen); // Toggle dropdown
      if (!isOpen) {
        setTimeout(() => {
          optionsRefs.current[0]?.focus(); // Focus the first option when opening
        }, 0);
      }
    } else if (e.key === 'ArrowDown') {
      e.preventDefault();
      if (!isOpen) {
        setIsOpen(true); // open dropdown
        setTimeout(() => {
          optionsRefs.current[0]?.focus();
        }, 0);
      }
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLButtonElement>, index: number) => {
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      const nextIndex = (index + 1) % options.length; // Loop to the first option if at the end
      optionsRefs.current[nextIndex]?.focus(); // Move focus to the next option
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      const prevIndex = (index - 1 + options.length) % options.length; // Loop to the last option if at the beginning
      optionsRefs.current[prevIndex]?.focus(); // Move focus to the previous option
    } else if (e.key === 'Enter') {
      e.preventDefault();
      onOptionChange(options[index].value); // Select the focused option
      setIsOpen(false); // Close dropdown
    } else if (e.key === 'Escape') {
      e.preventDefault();
      setIsOpen(false);
      (
        document.querySelector(`[data-testid="${testId}-IB-Select-Trigger"]`) as HTMLElement
      )?.focus(); // Return focus to the trigger
    }
  };

  useEffect(() => {
    if (isOpen) {
      const dropdown = document.querySelector(
        `[data-testid="${testId}-IB-Select-Dropdown"]`
      ) as HTMLElement;
      dropdown?.focus(); // Move focus to dropdown container
    }
  }, [isOpen]);

  const renderButton = (
    <div
      data-testid={`${testId}-IB-Select-Trigger`}
      className={cn(
        'relative flex',
        selectButtonStyle, // focus styles
        isOpen ? 'outline outline-primaryColor outline-2 -outline-offset-2' : '',
        triggerIcon ? 'pl-14' : ''
      )}
      tabIndex={0}
      role="button"
      aria-expanded={isOpen}
      aria-haspopup="listbox"
      onClick={() => setIsOpen(!isOpen)} // Toggle dropdown on click
      onKeyDown={handleTriggerKeyDown} // Handle keyboard events
    >
      {triggerIcon && (
        <Image
          className={inputIconStyle}
          src={triggerIcon}
          alt={'Select Icon'}
          width={24}
          height={24}
          data-testid={`${testId}-IB-Select-Icon`}
        />
      )}
      <div>{showValueInstead ? value : findDisplayValue(options, value)}</div>
      <span
        className={cn(labelStyle, isOpen ? 'text-primaryColor' : '')}
        data-testid={`${testId}-IB-Select-Label`}
      >
        {label}
      </span>
      <Image
        className={cn(arrowIconStyle, isOpen ? 'transform -scale-y-100' : '')}
        src={arrowIcon}
        alt={'Iron icon'}
        width={24}
        height={24}
      />
    </div>
  );

  const renderOptions = () => {
    return options?.map((option: SelectOption, index: number) => (
      <button
        key={option.value}
        data-testid={`${testId}-${index}-Option`}
        type="button"
        className={optionStyle}
        tabIndex={-1} // Remove from default tab order; focus programmatically
        role="option"
        aria-selected={value === option.value}
        ref={(el) => {
          optionsRefs.current[index] = el;
        }}
        onClick={() => handleChange(option.value)}
        onKeyDown={(e) => handleKeyDown(e, index)}
      >
        {option.displayValue}
      </button>
    ));
  };

  return (
    <div data-testid={`${testId}-IB-Select`} className="relative">
      <Popover open={isOpen} onOpenChange={() => setIsOpen(!isOpen)}>
        <PopoverTrigger asChild>{renderButton}</PopoverTrigger>
        <PopoverContent
          align="start"
          avoidCollisions={false}
          data-testid={`${testId}-IB-Select-Dropdown`}
          className={popoverStyle}
          role="listbox"
          tabIndex={-1}
        >
          {renderOptions()}
        </PopoverContent>
      </Popover>
    </div>
  );
};
Select.displayName = 'Select';

const inputIconStyle = 'absolute left-4 w-6 h-6 text-transparent z-40';
const labelStyle =
  'block absolute text-darkGrey1 -top-2.5 left-0 ml-3 text-sm px-1 bg-baseWhite peer-focus:text-primaryColor';
const selectButtonStyle =
  'peer flex h-14 text-darkGrey1 border-lightGrey1 hover:border-darkGrey1 w-full rounded border bg-transparent p-4 pr-14 text-base transition-colors focus:outline-primaryColor focus:outline-2 focus:outline-offset-2 focus-visible:outline-primaryColor bg-baseWhite';
const popoverStyle = 'w-full flex flex-col max-h-60 overflow-scroll p-0';
const optionStyle =
  'w-full hover:bg-lightGrey5 text-left text-sm py-2.5 px-4 focus:outline-none focus:bg-lightGrey5 focus-visible:outline-none focus-visible:bg-lightGrey5';
const arrowIconStyle = 'absolute right-4 w-6 h-6 cursor-pointer';

export { Select };
