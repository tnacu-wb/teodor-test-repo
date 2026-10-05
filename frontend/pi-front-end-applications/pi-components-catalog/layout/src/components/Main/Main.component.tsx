/* eslint-disable prettier/prettier */
'use client';

import { useElementDimensions } from '@whitbread-eos/utils';
import { cn } from '@whitbread-eos/utils';
import { usePathname } from 'next/navigation';
import { useEffect, useState } from 'react';

/* eslint-disable prettier/prettier */

type Props = {
  children: React.ReactNode;
  hasSidebar?: boolean;
  collapsedSidebar?: boolean;
  className?: string;
  id?: string;
};

export default function Main({
  children,
  hasSidebar = true,
  collapsedSidebar = false,
  className,
  id,
}: Props) {
  const pathname = usePathname();
  const [hasLevel2Navigation, setHasLevel2Navigation] = useState(pathname.includes('manage'));
  const { width: sidebarWidth } = useElementDimensions('[data-testid="SidebarDesktop-container"]');

  useEffect(() => {
    setHasLevel2Navigation(pathname.includes('manage'));
  }, [pathname]);

  let sidebarStyle = undefined;
  if (hasSidebar) {
    let widthStyle = hasLevel2Navigation ? sidebarLevel2Style : sidebarLevel1Style;

    if (collapsedSidebar) {
      widthStyle = sidebarCollapsedStyle;
    }

    sidebarStyle = cn(
      widthStyle,
    );
  }

  return (
    <main
      id={id}
      data-testid="Main"
      style={{
        marginLeft: sidebarWidth !== 0 ? sidebarWidth : undefined,
      }}
      className={cn(mainStyle, sidebarStyle, className)}
    >
      {children}
    </main>
  );
}

const mainStyle =
  'flex flex-col grow mobile:grow-0 mobile:overflow-x-hidden mobile:ml-0 mt-[--headerHeight] mobile:mt-0 h-[calc(100vh-var(--headerHeight))] mobile:h-[calc(100vh-81px-73px)] mobile:pb-[100px] ';
const sidebarLevel1Style = 'ml-[--sidebarWidth]';
const sidebarLevel2Style = 'ml-[--sidebarLevel2Width]';
const sidebarCollapsedStyle = 'ml-[--sidebarCollapsedWidth]';
