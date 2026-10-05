/* eslint-disable prettier/prettier */
'use client';

import * as RadioGroupPrimitive from '@radix-ui/react-radio-group';
import { cn } from '@whitbread-eos/utils';
import { Circle } from 'lucide-react';
import * as React from 'react';

type RadioGroupItemRootRef = React.ElementRef<typeof RadioGroupPrimitive.Item>;

type CustomProps = React.ComponentPropsWithoutRef<typeof RadioGroupPrimitive.Item> & {
  variant?: string;
  bulletFillStyle?: string;
  radioGroupButtonClass?: string;
};

const RadioGroup = React.forwardRef<
    React.ElementRef<typeof RadioGroupPrimitive.Root>,
    React.ComponentPropsWithoutRef<typeof RadioGroupPrimitive.Root> & { hasError?: boolean }
>(({ className, hasError, ...props }, ref) => {
    return <RadioGroupPrimitive.Root className={cn('grid gap-2', hasError ? '!outline !outline-2 !outline-error !-outline-offset-2' : '', className)} {...props} ref={ref} />;
});
RadioGroup.displayName = RadioGroupPrimitive.Root.displayName;

const RadioGroupItem = React.forwardRef<RadioGroupItemRootRef, CustomProps>(
  ({ className, bulletFillStyle, radioGroupButtonClass, ...props }, ref) => {
    return (
      <RadioGroupPrimitive.Item
        ref={ref}
        className={cn(
          'aspect-square h-5 w-5 rounded-full border border-primary text-primary ring-offset-background focus:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50',
          className,
          radioGroupButtonClass
        )}
        {...props}
      >
        <RadioGroupPrimitive.Indicator className="flex items-center justify-center">
          <Circle
            className={cn(
              'h-2.5 w-2.5',
              radioGroupButtonClass ? 'fill-primaryColor' : 'fill-current',
              'text-current',
              bulletFillStyle
            )}
          />
        </RadioGroupPrimitive.Indicator>
      </RadioGroupPrimitive.Item>
    );
  }
);
RadioGroupItem.displayName = RadioGroupPrimitive.Item.displayName;

export { RadioGroup, RadioGroupItem };
