'use client';

import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
  Drawer,
  DrawerTrigger,
  DrawerContent,
} from '@whitbread-eos/atoms/ui';
import { useEffect, useState, ReactNode } from 'react';

interface Props {
  children?: ReactNode;
  dataTestId?: string;
  renderItems: () => ReactNode;
  isOpen?: boolean;
  toggleOpen?: (open: boolean) => void;
}

export function ResponsiveDropdown({
  children,
  dataTestId,
  renderItems,
  isOpen = false,
  toggleOpen,
}: Props) {
  const [manageDropdownOpen, setManageDropdownOpen] = useState(isOpen);
  const [isMobileView, setIsMobileView] = useState(
    typeof window !== 'undefined' ? window.innerWidth < 1280 : false
  );

  useEffect(() => {
    const handleResize = () => {
      setIsMobileView(window.innerWidth < 1280);
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const handleToggleOpen = (open: boolean) => {
    setManageDropdownOpen(open);
    if (toggleOpen) {
      toggleOpen(open);
    }
  };

  if (isMobileView) {
    return (
      <div data-testid={`${dataTestId}-container`}>
        <Drawer data-testid={dataTestId}>
          <DrawerTrigger data-testid={`${dataTestId}-trigger`} asChild>
            {children}
          </DrawerTrigger>
          <DrawerContent data-testid={`${dataTestId}-content`} className={mobileContentStyle}>
            {renderItems()}
          </DrawerContent>
        </Drawer>
      </div>
    );
  }

  return (
    <div data-testid={`${dataTestId}-container`}>
      <DropdownMenu
        data-testid={dataTestId}
        open={manageDropdownOpen}
        onOpenChange={handleToggleOpen}
      >
        <DropdownMenuTrigger data-testid={`${dataTestId}-trigger`} asChild>
          {children}
        </DropdownMenuTrigger>
        <DropdownMenuContent
          data-testid={`${dataTestId}-content`}
          className={desktopContentStyle}
          align="end"
          avoidCollisions={false}
          forceMount
        >
          {renderItems()}
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );
}

export const ReponsiveDropdownLinkWrapper = ({
  children,
  shouldCloseDropdown = false,
  closeDropdown = undefined,
}: {
  children: ReactNode;
  shouldCloseDropdown?: boolean;
  closeDropdown?: () => void;
}) => {
  const [isMobileView, setIsMobileView] = useState(
    typeof window !== 'undefined' ? window.innerWidth < 1280 : false
  );

  useEffect(() => {
    const handleResize = () => {
      setIsMobileView(window.innerWidth < 1280);
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  if (isMobileView) {
    return <>{children}</>;
  }

  return (
    <DropdownMenuItem
      onSelect={() => {
        if (shouldCloseDropdown && closeDropdown) {
          closeDropdown();
        }
      }}
    >
      {children}
    </DropdownMenuItem>
  );
};

const desktopContentStyle = 'flex flex-col gap-[0.375rem] w-[18.75rem]';
const mobileContentStyle = 'pt-[4.5rem] pb-6 flex flex-col gap-[0.375rem]';
