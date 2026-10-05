'use client';

import { formatIBAssetsUrl, cn } from '@whitbread-eos/utils';
import { MoreHorizontal } from 'lucide-react';
import Image from 'next/image';
import * as React from 'react';

import { Button, ButtonProps } from '../Button';

const Pagination = ({ className, ...props }: React.ComponentProps<'nav'>) => (
  <nav
    role="navigation"
    className={cn('mx-auto flex w-full justify-center', className)}
    {...props}
  />
);
Pagination.displayName = 'Pagination';

const PaginationContent = React.forwardRef<HTMLUListElement, React.ComponentProps<'ul'>>(
  ({ className, ...props }, ref) => (
    <ul ref={ref} className={cn('flex flex-row items-center gap-1', className)} {...props} />
  )
);
PaginationContent.displayName = 'PaginationContent';

const PaginationItem = React.forwardRef<HTMLLIElement, React.ComponentProps<'li'>>(
  ({ className, ...props }, ref) => <li ref={ref} className={cn('', className)} {...props} />
);
PaginationItem.displayName = 'PaginationItem';

type PaginationButtonProps = {
  isActive?: boolean;
} & ButtonProps;

const PaginationButton = ({ className, isActive, children, ...props }: PaginationButtonProps) => (
  <Button
    variant={isActive ? 'default' : 'ghost'}
    className={cn(
      'h-7 w-7 p-0 text-lg font-semibold',
      isActive ? 'text-white' : 'text-secondaryColor hover:text-secondaryColor',
      className
    )}
    {...props}
  >
    {children}
  </Button>
);
PaginationButton.displayName = 'PaginationButton';

const PaginationPrevious = ({
  className,
  icon,
  ...props
}: React.ComponentProps<typeof PaginationButton> & { icon: string }) => (
  <PaginationButton className={cn('flex gap-1', className)} {...props}>
    <Image src={formatIBAssetsUrl(icon || '')} alt={'pagination-previous'} width={28} height={28} />
  </PaginationButton>
);
PaginationPrevious.displayName = 'PaginationPrevious';

const PaginationNext = ({
  className,
  icon,
  ...props
}: React.ComponentProps<typeof PaginationButton> & { icon: string }) => (
  <PaginationButton className={cn('flex gap-1', className)} {...props}>
    <Image src={formatIBAssetsUrl(icon || '')} alt={'pagination-next'} width={28} height={28} />
  </PaginationButton>
);
PaginationNext.displayName = 'PaginationNext';

const PaginationEllipsis = ({ className, ...props }: React.ComponentProps<'span'>) => (
  <span className={cn('flex h-7 w-7 pb-px items-end justify-center', className)} {...props}>
    <MoreHorizontal className="h-4 w-4" />
  </span>
);
PaginationEllipsis.displayName = 'PaginationEllipsis';

export {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationButton,
  PaginationNext,
  PaginationPrevious,
};
