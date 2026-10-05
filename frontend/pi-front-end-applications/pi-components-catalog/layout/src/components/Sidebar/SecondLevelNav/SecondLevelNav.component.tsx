'use client';

import { formatIBAssetsUrl } from '@whitbread-eos/utils';
import { usePathname } from 'next/navigation';
import { useEffect } from 'react';
import { useRef } from 'react';

import SidebarLink from '../SidebarLink/SidebarLink.component';

type Props = {
  secondLevelLinks?: linkDetails[];
  icons: Record<string, string>;
  onLinkClick?: () => void;
  autoFocusFirst?: boolean;
  onCloseSecondLevelNav?: () => void;
  onLeftArrowBack?: () => void;
};

type linkDetails = {
  type?: string;
  key: string;
  label: string;
  isActive: boolean;
  href: string;
  condition?: boolean;
};

const SecondLevelNav = ({
  secondLevelLinks,
  icons,
  onLinkClick,
  autoFocusFirst,
  onCloseSecondLevelNav,
  onLeftArrowBack,
}: Readonly<Props>) => {
  const baseDataTestId = 'SecondLevelNav';
  const pathname = usePathname() ?? '';

  const links = secondLevelLinks ?? [];
  // visible links
  const visibleLinks = links.filter((itemDetails: linkDetails) => itemDetails.condition);
  const getHrefPathname = (href: string) => href.split('?')[0];
  // Find the index of the active link (matches current route)
  const activeIdx = visibleLinks.findIndex(
    (itemDetails: linkDetails) => getHrefPathname(itemDetails.href) === pathname
  );
  const linkRefs = useRef<(HTMLAnchorElement | HTMLButtonElement | null)[]>([]);
  // Add ref for the container to allow focusing back
  const containerRef = useRef<HTMLDivElement>(null);

  // Blur nav item if user reloads the page (on mount)
  useEffect(() => {
    const active = document.activeElement;
    for (const ref of linkRefs.current) {
      if (ref && ref === active) {
        ref.blur();
      }
    }
  }, []);

  // Blur nav item if user navigates with back/forward
  useEffect(() => {
    const handlePopState = () => {
      const active = document.activeElement;
      for (const ref of linkRefs.current) {
        if (ref && ref === active) {
          ref.blur();
        }
      }
    };
    window.addEventListener('popstate', handlePopState);
    return () => {
      window.removeEventListener('popstate', handlePopState);
    };
  }, []);

  // Keyboard navigation handler
  const handleKeyDown: React.KeyboardEventHandler<HTMLDivElement> = (e) => {
    e.stopPropagation(); // Prevent event propagating to parent components
    const currentIdx = linkRefs.current.findIndex((ref) => ref === document.activeElement);
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      const nextIdx = (currentIdx + 1) % linkRefs.current.length;
      linkRefs.current[nextIdx]?.focus();
    }
    if (e.key === 'ArrowUp') {
      e.preventDefault();
      const prevIdx = (currentIdx - 1 + linkRefs.current.length) % linkRefs.current.length;
      linkRefs.current[prevIdx]?.focus();
    }
    // Space key should activate the focused link/button (like Enter)
    if (e.key === ' ') {
      e.preventDefault();
      linkRefs.current[currentIdx]?.click();
    }
    // For Escape or ArrowLeft, just call the parent handler
    if (e.key === 'Escape' || e.key === 'ArrowLeft') {
      e.preventDefault();
      if (e.key === 'ArrowLeft') {
        onLeftArrowBack?.();
      } else {
        onCloseSecondLevelNav?.();
      }
    }
  };

  return (
    <div
      ref={containerRef}
      data-testid={`${baseDataTestId}-container`}
      className={secondLevelNavStyle}
      tabIndex={-1}
      onKeyDown={handleKeyDown}
    >
      {links.map(
        (itemDetails: linkDetails, idx: number) =>
          itemDetails.condition && (
            <SidebarLink
              key={itemDetails.key}
              label={itemDetails.label}
              // Highlight as active if the current pathname matches the link's href
              isActive={getHrefPathname(itemDetails.href) === pathname}
              href={itemDetails.href}
              testId={itemDetails.key}
              onClick={() => onLinkClick?.()}
              secondLevelTitle={itemDetails?.type === 'title'}
              icon={
                itemDetails?.type === 'title' ? formatIBAssetsUrl(icons?.['icon.chevron.left']) : ''
              }
              iconActive={
                itemDetails?.type === 'title'
                  ? formatIBAssetsUrl(icons?.['icon.chevron.left.purple'])
                  : ''
              }
              // Add tabIndex and autoFocus to the active link
              tabIndex={autoFocusFirst && idx === activeIdx ? 0 : -1}
              autoFocus={!!autoFocusFirst && idx === activeIdx}
              // Attach ref for keyboard navigation
              ref={(el) => {
                const visibleIdx = visibleLinks.findIndex((v) => v.key === itemDetails.key);
                linkRefs.current[visibleIdx] = el;
              }}
            />
          )
      )}
    </div>
  );
};

export default SecondLevelNav;

const secondLevelNavStyle = 'flex flex-col gap-2 max-w-none xl:max-w-[260px]';
