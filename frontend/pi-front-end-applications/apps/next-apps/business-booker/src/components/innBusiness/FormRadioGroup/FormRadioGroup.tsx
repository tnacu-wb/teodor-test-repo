'use client';

import { ErrorTooltip, Label, RadioGroup, RadioGroupItem } from '@whitbread-eos/atoms/ui';
import * as React from 'react';

type RadioVariant = 'default' | 'address' | 'payment';

type RadioItem = {
  value: string;
  label: string;
  description?: string;
  icons?: React.ReactNode[];
  testid?: string;
};

type RadioGroupProps = React.ComponentPropsWithoutRef<typeof RadioGroup> & {
  items: RadioItem[];
  onChange?: (value: string) => void;
  errors?: Record<string, { message: string }>;
  errorIcon?: string;
  variant?: RadioVariant;
  selectedValue?: string;
  className?: string;
};

type RadioGroupItemProps = RadioItem & {
  variant?: RadioVariant;
  selectedValue?: string;
  className?: string;
};

const PaymentRadioContent: React.FC<Pick<RadioItem, 'label' | 'icons'>> = ({ label, icons }) => (
  <div className={styles.radioContent}>
    <span className={styles.radioLabelText}>{label}</span>
    {icons && icons.length > 0 && <div className={styles.radioIconsContainer}>{icons}</div>}
  </div>
);

const AddressRadioContent: React.FC<Pick<RadioItem, 'label' | 'description'>> = ({
  label,
  description,
}) => (
  <div className={styles.radioContent}>
    <span className={styles.radioLabelText}>{label}</span>
    {description && <p className={styles.radioDescription}>{description}</p>}
  </div>
);

const DefaultRadioContent: React.FC<Pick<RadioItem, 'value' | 'label'>> = ({ value, label }) => (
  <div className={styles.defaultRadioContainer}>
    <RadioGroupItem value={value} id={value} className={styles.radioBase} />
    <Label className={styles.defaultRadioLabel} htmlFor={value}>
      {label}
    </Label>
  </div>
);

const FormRadioGroupItem = React.memo<RadioGroupItemProps>(
  ({
    value,
    label,
    description,
    icons,
    testid,
    variant = 'default',
    selectedValue,
    className = '',
  }) => {
    const isSelected = value === selectedValue;
    const radioClasses = `${styles.radioBase} ${
      isSelected ? styles.radioSelected : styles.radioUnselected
    }`;
    const labelClasses = `${styles.radioLabelBase} ${className} ${
      isSelected ? styles.radioLabelSelected : styles.radioLabelUnselected
    }`;

    if (variant === 'default') {
      return <DefaultRadioContent value={value} label={label} />;
    }

    return (
      <Label htmlFor={value} className={labelClasses} data-testid={testid}>
        <RadioGroupItem value={value} id={value} className={radioClasses} />
        {variant === 'payment' ? (
          <PaymentRadioContent label={label} icons={icons} />
        ) : (
          <AddressRadioContent label={label} description={description} />
        )}
      </Label>
    );
  }
);

FormRadioGroupItem.displayName = 'FormRadioGroupItem';

const FormRadioGroup = React.forwardRef<React.ElementRef<typeof RadioGroup>, RadioGroupProps>(
  (
    {
      items,
      onChange,
      errors,
      variant = 'default',
      selectedValue,
      name = 'radioName',
      errorIcon,
      className,
      ...props
    },
    ref
  ) => {
    const error = errors?.[name];
    const hasError = Boolean(error?.message);

    const getItemClassName = (index: number) => {
      const isFirst = index === 0;
      const isLast = index === items.length - 1;
      return `${isFirst ? styles.radioFirstItem : ''} ${isLast ? styles.radioLastItem : ''}`.trim();
    };

    return (
      <>
        <RadioGroup
          ref={ref}
          onValueChange={onChange}
          className={`${className || ''} ${styles.radioGroup}`.trim()}
          {...props}
        >
          <div className={styles.radioGroupContainer}>
            {items.map((item: any, index: number) => (
              <FormRadioGroupItem
                key={item.value}
                {...item}
                variant={variant}
                selectedValue={selectedValue}
                className={getItemClassName(index)}
              />
            ))}
          </div>
        </RadioGroup>

        {hasError && (
          <ErrorTooltip
            icon={errorIcon}
            content={error?.message}
            open={hasError}
            testId={`${name}-Error-Tooltip`}
            className={styles.errorTooltip}
            mobile
          />
        )}
      </>
    );
  }
);

FormRadioGroup.displayName = 'FormRadioGroup';

export { FormRadioGroup, type RadioGroupProps, type RadioItem, type RadioVariant };

const styles = {
  radioBase:
    'relative w-5 h-5 rounded-full transition-colors before:content-[""] before:block before:w-3 before:h-3 before:rounded-full before:absolute before:top-1/2 before:left-1/2 before:-translate-x-1/2 before:-translate-y-1/2',
  radioSelected: 'border-2 border-primaryColor before:bg-primaryColor',
  radioUnselected: 'border border-lightGrey1 hover:border-primaryColor/50 before:bg-transparent',
  radioLabelBase:
    'w-full sm:w-[531px] min-h-[44px] flex flex-row items-start p-4 gap-2 cursor-pointer',
  radioLabelSelected: 'bg-lightGrey5 border-2 border-primaryColor',
  radioLabelUnselected: 'bg-white border border-lightGrey3',
  radioContent: 'flex flex-col items-start gap-1 flex-1',
  radioLabelText: 'font-semibold text-base leading-[150%] text-darkGrey1 w-full',
  radioIconsContainer: 'flex gap-1 mt-2',
  radioDescription: 'text-sm leading-[140%] text-darkGrey2 w-full',
  defaultRadioContainer: 'flex items-center space-x-2 h-14',
  defaultRadioLabel: 'font-medium text-base text-darkGrey1  cursor-pointer',
  radioGroup: 'w-full',
  radioGroupContainer: 'flex flex-col items-start w-full',
  radioFirstItem: 'rounded-t-[4px]',
  radioLastItem: 'rounded-b-[4px]',
  errorTooltip: '!flex',
};
