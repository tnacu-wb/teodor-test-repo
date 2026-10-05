'use client';

import { Slot } from '@radix-ui/react-slot';
import { cn } from '@whitbread-eos/utils';
import { cva, type VariantProps } from 'class-variance-authority';
import * as React from 'react';

export const variantDescriptor = {
  default: cn(
    'bg-secondaryColor text-lg font-semibold text-primary-foreground leading-5',
    'hover:bg-secondaryColorHover'
  ),
  alternativeDefault:
    'bg-background text-lg text-secondaryColor font-semibold border border-secondaryColor hover:text-secondaryColorHover hover:border-secondaryColorHover',
  destructive: 'bg-destructive text-destructive-foreground hover:bg-destructive/90',
  outline:
    'border bg-lightGrey5 text-secondaryColor border-secondaryColor bg-background hover:bg-baseWhite',
  secondary: 'bg-secondary text-secondary-foreground hover:bg-secondary/80',
  ghost: 'hover:bg-accent hover:text-accent-foreground',
  link: 'text-primary underline-offset-4 hover:underline p-0 underline font-normal',
  grey: 'border bg-lightGrey5 text-secondaryColor border-lightGrey5',
  defaultCalendar: 'bg-primary text-primary-foreground hover:bg-primary/90',
  downloadButton:
    'bg-lightGrey5 text-lg font-semibold text-secondaryColor leading-5 hover:bg-baseWhite',
  calendarButton:
    'rounded-full text-base font-semibold hover:border-solid hover:border-2 hover:border-primaryColor hover:bg-baseWhite transition-none duration-0',
  inputCalendarButton:
    'border border-input border-lightGrey2 bg-background font-normal rounded-none justify-start focus-visible:outline focus-visible:outline-2 focus-visible:outline-primaryColor focus-visible:outline-offset-[-2px] focus-visible:ring-0 focus-visible:ring-primaryColor',
  roomOccupancyButton:
    'border border-input border-lightGrey2 bg-background font-normal rounded-none justify-start focus-visible:outline focus-visible:outline-2 focus-visible:outline-primaryColor focus-visible:outline-offset-[-2px] focus-visible:ring-0 focus-visible:ring-primaryColor',
  footerButtons:
    'hover:bg-transparent bg-transparent text-secondaryColor font-semibold text-lg leading-6 w-full',
  desktopSearchButton:
    'bg-baseWhite rounded-l-none rounded-r border border-l-0 border-lightGrey2 hover:outline hover:outline-darkGrey1 hover:outline-1 hover:-outline-offset-1',
  dialogDefault:
    'flex-1 mobile:flex-none text-lg font-semibold rounded text-white bg-primaryColor disabled:bg-lightGrey3 disabled:text-lightGrey1',
  dialogOutline:
    'flex-1 mobile:flex-none text-lg font-semibold rounded text-secondaryColor bg-white border border-secondaryColor disabled:border-lightGrey3 disabled:text-lightGrey1',
  dialogDestructive:
    'flex-1 mobile:flex-none text-lg font-semibold rounded text-white bg-destructive disabled:bg-lightGrey3 disabled:text-lightGrey1',
  findAddressButton:
    'text-lg font-semibold text-secondaryColor leading-5 focus-visible:ring-secondaryColor focus-visible:ring-offset-baseWhite',
  newAddressButton: 'text-sm underline font-medium text-secondaryColor leading-5',
  editButton: 'text-sm underline font-medium text-secondaryColor leading-5',
  saveUpdatesButton:
    'text-lg font-semibold rounded text-white bg-primaryColor disabled:bg-lightGrey3 disabled:text-lightGrey1',
  buttonBGWhiteBorderSecondary: 'text-lg font-semibold text-secondaryColor leading-5',
  cardLinkButton:
    'border text-lg font-semibold bg-lightGrey5 text-secondaryColor border-secondaryColor bg-background hover:bg-secondaryColor hover:text-primary-foreground',
  truncateWithEllipsisButton: 'truncate mobile:max-w-full inline-block',
  disabled: 'bg-lightGrey3 text-darkGrey2 text-lg font-semibold leading-5',
};

const buttonVariants = cva(
  cn(
    'inline-flex items-center justify-center whitespace-nowrap rounded-md text-sm font-medium',
    'ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2',
    'focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none',
    'disabled:opacity-50'
  ),
  {
    variants: {
      variant: variantDescriptor,
      size: {
        default: 'h-14 px-6 py-4 rounded-sm',
        sm: 'h-9 rounded-md px-3',
        lg: 'text-lg font-semibold leading-6 px-12 py-[15px]',
        icon: 'h-10 w-10',
        defaultCalendar: 'h-10 px-4 py-2',
        downloadButton: 'h-14 p-4 rounded-sm',
        calendarButton: 'h-10 w-10 p-0',
        inputCalendarButton: 'h-14 p-4',
        roomOccupancyButton:
          'border border-x-0 border-lightGrey2 rounded-none border-input r h-14 p-4 pr-4 focus-visible:outline-none focus-visible:ring-0 text-base',
        footerButtons: '',
        desktopSearchButton: 'w-14 h-14 min-w-14',
        findAddressButton: 'h-14 px-6 py-4 rounded-sm',
        newAddressButton: 'mt-2',
        editButton: 'h-[22px]',
        buttonBGWhiteBorderSecondary: 'h-14 p-4 rounded-sm',
      },
    },
    defaultVariants: {
      variant: 'default',
      size: 'default',
    },
  }
);

export interface ButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement>, VariantProps<typeof buttonVariants> {
  asChild?: boolean;
  variant?: any;
  size?: any;
}

const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ type = 'button', className, variant, size, asChild = false, ...props }, ref) => {
    const Comp = asChild ? Slot : 'button';
    return (
      <Comp
        className={cn(buttonVariants({ variant, size, className }))}
        ref={ref}
        type={type}
        {...props}
      />
    );
  }
);
Button.displayName = 'Button';

export { Button, buttonVariants };
