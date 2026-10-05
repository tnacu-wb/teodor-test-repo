'use client';

import * as TooltipPrimitive from '@radix-ui/react-tooltip';
import { cn } from '@whitbread-eos/utils';
import Image from 'next/image';
import * as React from 'react';
import { LegacyRef } from 'react';

const TooltipProvider = TooltipPrimitive.Provider;

const TooltipRoot = TooltipPrimitive.Root;

const TooltipTrigger = TooltipPrimitive.Trigger;

const TooltipContent = React.forwardRef<
  React.ElementRef<typeof TooltipPrimitive.Content>,
  React.ComponentPropsWithoutRef<typeof TooltipPrimitive.Content>
>(({ className, sideOffset = 4, ...props }, ref) => (
  <TooltipPrimitive.Content
    ref={ref}
    sideOffset={sideOffset}
    className={cn(
      tooltipContentBaseStyle,
      'z-50 overflow-hidden bg-popover text-popover-foreground flex',
      className
    )}
    {...props}
  />
));
TooltipContent.displayName = TooltipPrimitive.Content.displayName;

type TooltipArrowProps = {
  className?: string;
};

export function TooltipArrow({ className }: TooltipArrowProps) {
  return (
    <div
      className={cn(
        'h-0 w-0 border-x-[12px] border-x-transparent border-b-[12px] absolute top-[-12px]',
        className ? className : 'left-4'
      )}
    ></div>
  );
}

type InfoTooltipProps = {
  children?: React.ReactNode;
  content: React.ReactNode;
  className?: string;
  arrowClassName?: string;
  testId?: string;
  mobile?: boolean;
  hoverVariant?: boolean;
  open: boolean;
  ref?: LegacyRef<HTMLDivElement>;
};

const InfoTooltip = React.forwardRef<HTMLDivElement, InfoTooltipProps>(
  (
    {
      children,
      content,
      className,
      arrowClassName,
      testId,
      mobile = false,
      hoverVariant = false,
      open,
    },
    ref
  ) => {
    if (mobile && open) {
      return (
        <>
          <div
            ref={ref}
            data-testid={testId}
            className={cn(
              tooltipContentBaseStyle,
              'bg-tooltipInfo relative mt-4 hidden mobile:flex',
              className
            )}
          >
            {content}
            <TooltipArrow className={cn('border-b-tooltipInfo', arrowClassName)} />
          </div>
        </>
      );
    }

    return (
      <>
        {hoverVariant ? (
          <TooltipProvider>
            <TooltipRoot>
              <TooltipTrigger asChild>{children}</TooltipTrigger>
              <TooltipContent
                side="bottom"
                align="start"
                className={cn(
                  'bg-tooltipInfo mobile:hidden overflow-visible relative mt-3 left-[-15px]',
                  className
                )}
                data-testid={testId}
              >
                <>
                  <span className={infoTooltipTitleStyle}>{content}</span>
                </>
                <TooltipArrow className={cn('border-b-tooltipInfo', arrowClassName)} />
              </TooltipContent>
            </TooltipRoot>
          </TooltipProvider>
        ) : (
          <TooltipProvider>
            <TooltipRoot open={open}>
              <TooltipTrigger asChild>{children}</TooltipTrigger>
              <TooltipContent
                side="bottom"
                align="start"
                className={cn(
                  'bg-tooltipInfo mobile:hidden overflow-visible relative mt-3 left-[-15px]',
                  className
                )}
                data-testid={testId}
              >
                {content}
                <TooltipArrow className={cn('border-b-tooltipInfo', arrowClassName)} />
              </TooltipContent>
            </TooltipRoot>
          </TooltipProvider>
        )}
      </>
    );
  }
);
InfoTooltip.displayName = 'InfoTooltip';

type ErrorTooltipProps = {
  children?: React.ReactNode;
  content: React.ReactNode;
  open: boolean;
  icon?: string;
  className?: string;
  arrowClassName?: string;
  testId?: string;
  mobile?: boolean;
  errorId?: string;
};

function ErrorTooltip({
  children,
  content,
  open,
  icon,
  className,
  arrowClassName,
  testId,
  mobile = false,
  errorId,
}: ErrorTooltipProps) {
  if (mobile && open) {
    return (
      <div
        id={errorId}
        role="status"
        aria-live="polite"
        aria-atomic="true"
        data-testid={testId}
        className={cn(
          tooltipContentBaseStyle,
          'bg-tooltipError relative mt-4 hidden mobile:flex',
          className
        )}
      >
        {icon && (
          <Image src={icon} alt="error" width={16} height={16} className="mt-0.5 mr-2 w-4 h-4" />
        )}
        {content}
        <TooltipArrow className={cn('border-b-tooltipError', arrowClassName)} />
      </div>
    );
  }

  // Accessibility pattern: prevents duplicate screen reader announcements from Radix Tooltip
  // - Visible tooltip uses aria-hidden to prevent Radix's internal duplication
  // - Separate visually-hidden div provides proper aria-live announcements
  return (
    <>
      <TooltipProvider>
        <TooltipRoot open={open}>
          <TooltipTrigger asChild>{children}</TooltipTrigger>
          <TooltipContent
            side="bottom"
            align="start"
            className={cn(
              'bg-tooltipError mobile:hidden overflow-visible relative mt-3',
              className
            )}
            data-testid={testId}
            aria-hidden="true"
          >
            {icon && (
              <Image src={icon} alt="error" width={16} height={16} className="mt-0.5 mr-2" />
            )}
            {content}
            <TooltipArrow className={cn('border-b-tooltipError', arrowClassName)} />
          </TooltipContent>
        </TooltipRoot>
      </TooltipProvider>

      {/* Visually-hidden aria-live region for screen reader announcements */}
      <div id={errorId} role="status" aria-live="polite" aria-atomic="true" className="sr-only">
        {open ? content : ''}
      </div>
    </>
  );
}

const tooltipContentBaseStyle =
  'items-start text-sm font-normal min-h-9 rounded-[3px] px-4 py-2 border-0';
const infoTooltipTitleStyle = 'font-normal text-sm';

export { InfoTooltip, ErrorTooltip, TooltipRoot, TooltipTrigger, TooltipContent, TooltipProvider };
