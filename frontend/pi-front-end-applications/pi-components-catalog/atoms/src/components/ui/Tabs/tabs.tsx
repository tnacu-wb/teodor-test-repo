/* eslint-disable prettier/prettier */
'use client';

import * as TabsPrimitive from '@radix-ui/react-tabs';
import { cn } from '@whitbread-eos/utils';
import * as React from 'react';

const Tabs = TabsPrimitive.Root;

const TabsList = React.forwardRef<
  React.ElementRef<typeof TabsPrimitive.List>,
  React.ComponentPropsWithoutRef<typeof TabsPrimitive.List>
>(({ className, ...props }, ref) => (
  <TabsPrimitive.List
    ref={ref}
    className={cn('inline-flex items-center justify-center', className)}
    {...props}
  />
));
TabsList.displayName = TabsPrimitive.List.displayName;

const tabsTriggerClassName = cn(
  'inline-flex items-center justify-center whitespace-nowrap text-base font-semibold',
  'disabled:pointer-events-none disabled:opacity-50',
  'min-w-[12.5rem] h-[3.125rem] border-b-2 border-lightGrey4',
  'data-[state=active]:border-primaryColor'
);

const TabsTrigger = React.forwardRef<
  React.ElementRef<typeof TabsPrimitive.Trigger>,
  React.ComponentPropsWithoutRef<typeof TabsPrimitive.Trigger>
>(({ className, ...props }, ref) => (
  <TabsPrimitive.Trigger ref={ref} className={cn(tabsTriggerClassName, className)} {...props} />
));
TabsTrigger.displayName = TabsPrimitive.Trigger.displayName;

type StaticTabsTriggerProps = {
  children?: React.ReactNode;
  className?: string;
  active?: boolean;
};

const StaticTabsTrigger = ({ children, className, active = false, ...props }: StaticTabsTriggerProps) => (
  <span className={cn(tabsTriggerClassName, className)} data-state={active ? 'active' : ''} {...props}>
    {children}
  </span>
);

const TabsContent = React.forwardRef<
  React.ElementRef<typeof TabsPrimitive.Content>,
  React.ComponentPropsWithoutRef<typeof TabsPrimitive.Content>
>(({ className, ...props }, ref) => (
  <TabsPrimitive.Content ref={ref} className={cn('mt-2', className)} {...props} />
));
TabsContent.displayName = TabsPrimitive.Content.displayName;

export { Tabs, TabsList, TabsTrigger, StaticTabsTrigger, TabsContent };
