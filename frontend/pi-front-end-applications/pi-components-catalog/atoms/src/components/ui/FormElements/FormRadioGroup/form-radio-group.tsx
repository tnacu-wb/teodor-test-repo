'use client';

import { cn } from '@whitbread-eos/utils';
import * as React from 'react';

import { Label } from '../../Label';
import { RadioGroup, RadioGroupItem } from '../../RadioGroup';
import { ErrorTooltip } from '../../Tooltip/index';

interface Props {
  items: {
    value: string;
    label: React.ReactNode;
    disabled?: boolean;
  }[];
  onChange?: (...event: any[]) => void;
  errors?: Record<string, any>;
  errorIcon?: string;
  variant?: string;
  selectedValue?: string;
  radioGroupItemClass?: string;
  radioGroupLabelClass?: string;
  radioGroupButtonClass?: string;
  customId?: string;
}

const FormRadioGroupItem = ({
  value,
  label,
  variant = 'default',
  selectedValue,
  radioGroupItemClass,
  radioGroupLabelClass,
  radioGroupButtonClass,
  customId,
  disabled,
}: {
  value: string;
  label: React.ReactNode;
  variant?: string;
  selectedValue?: string;
  radioGroupItemClass?: string;
  radioGroupLabelClass?: string;
  radioGroupButtonClass?: string;
  customId?: string;
  hasError: boolean;
  disabled?: boolean;
}) => {
  const isSelectedValue = selectedValue === value;

  const variantBoxStyle = () => {
    switch (variant) {
      case 'col-styled':
        return cn(
          radioGroupItemStyle,
          colItemStyledStyle,
          radioGroupItemClass,
          isSelectedValue ? selectedOptionStyled : ''
        );
      case 'row':
        return cn(
          radioGroupItemStyleAddressVariant,
          isSelectedValue
            ? 'outline outline-2 outline-primaryColor -outline-offset-2 first:rounded-l-sm last:rounded-r-sm'
            : ''
        );
      default:
        return cn(radioGroupItemStyle, radioGroupItemClass);
    }
  };

  const variantItemStyle = () => {
    switch (variant) {
      case 'col-styled':
        return cn(
          'border-2 self-start mt-[2px]',
          isSelectedValue ? selectedOptionBulletStyle : 'border-lightGrey1'
        );
      case 'row':
        return cn(isSelectedValue ? 'border-2 border-primaryColor' : 'border-2 border-lightGrey1');
      default:
        return '';
    }
  };

  const variantBulletStyle = () => {
    switch (variant) {
      case 'col-styled':
        return cn('fill-primaryColor text-primaryColor');
      case 'row':
        return cn('fill-primaryColor text-primaryColor');
      default:
        return '';
    }
  };

  const variantLabelStyle = () => {
    switch (variant) {
      case 'col-styled':
        return cn(radioGroupItemLabelStyle, 'w-full');
      case 'row':
        return cn(radioGroupItemLabelStyle);
      default:
        return cn(radioGroupItemLabelStyle, radioGroupLabelClass);
    }
  };

  return (
    <Label
      htmlFor={customId ? customId : value}
      className={cn(variantBoxStyle(), disabled ? 'cursor-not-allowed opacity-50' : '')}
    >
      <RadioGroupItem
        value={value}
        id={customId ? customId : value}
        className={variantItemStyle()}
        bulletFillStyle={variantBulletStyle()}
        variant={variant}
        radioGroupButtonClass={radioGroupButtonClass}
        disabled={disabled}
      />
      <div
        className={cn(variantLabelStyle(), disabled ? 'cursor-not-allowed' : '')}
        id={customId ? customId : value}
      >
        {label}
      </div>
    </Label>
  );
};

export const FormRadioGroup = React.forwardRef<
  React.ElementRef<typeof RadioGroup>,
  React.ComponentPropsWithoutRef<typeof RadioGroup> & Props
>(
  (
    {
      items,
      onChange,
      errors,
      variant = 'default',
      selectedValue,
      name = 'radioName',
      errorIcon,
      radioGroupItemClass,
      radioGroupLabelClass,
      radioGroupButtonClass,
      customId,
      className,
      ...props
    },
    ref
  ) => {
    const error = errors?.[name];
    const hasError = !!error?.type;

    const variantStyle = () => {
      switch (variant) {
        case 'col-styled':
          return 'gap-0';
        case 'row':
          return 'w-full flex flex-row justify-between gap-0';
        default:
          return '';
      }
    };

    return (
      <>
        <RadioGroup
          ref={ref}
          onValueChange={onChange}
          className={cn(radioGroupStyle, variantStyle(), className)}
          hasError={hasError}
          {...props}
        >
          {items.map((item, index) => (
            <FormRadioGroupItem
              key={item.value}
              value={item.value}
              label={item.label}
              variant={variant}
              selectedValue={selectedValue}
              radioGroupItemClass={radioGroupItemClass}
              radioGroupLabelClass={radioGroupLabelClass}
              radioGroupButtonClass={radioGroupButtonClass}
              customId={customId ? `${customId}-${index + 1}` : undefined}
              hasError={hasError}
              disabled={item?.disabled ?? false}
            />
          ))}
        </RadioGroup>

        <ErrorTooltip
          icon={errorIcon}
          content={error?.message}
          open={hasError}
          testId={`${name}-Error-Tooltip`}
          className="!flex"
          mobile
        />
      </>
    );
  }
);
FormRadioGroup.displayName = RadioGroup.displayName;

const radioGroupStyle = 'mt-2';
const radioGroupItemStyle = 'flex items-center space-x-2 h-14 cursor-pointer';
const radioGroupItemStyleAddressVariant =
  'flex w-full space-x-2 h-14 p-5 items-center cursor-pointer border border-lightGrey3';
const radioGroupItemLabelStyle = 'font-semibold text-base cursor-pointer';
const colItemStyledStyle =
  'border border-t-0 first:border-t p-4 bg-white first:rounded-t last:rounded-b h-[unset]';
const selectedOptionStyled = 'outline outline-2 outline-primaryColor -outline-offset-2';
const selectedOptionBulletStyle = 'color-primaryColor border-primaryColor';
