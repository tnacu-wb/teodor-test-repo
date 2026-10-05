import Image from 'next/image';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import React, { forwardRef } from 'react';

interface Props {
  label: string;
  icon?: string;
  iconActive?: string;
  href?: string;
  isActive: boolean;
  isCollapsed?: boolean;
  mainStyle?: boolean;
  testId?: string;
  onClick?: () => void;
  isButton?: boolean;
  secondLevelTitle?: boolean;
  tabIndex?: number;
  autoFocus?: boolean;
  ariaCurrent?: 'page' | undefined;
  onKeyDown?: (e: React.KeyboardEvent<HTMLAnchorElement | HTMLButtonElement>) => void;
}

const SidebarLink = forwardRef<HTMLAnchorElement | HTMLButtonElement, Props>(
  (
    {
      label,
      icon = '',
      iconActive = '',
      isActive,
      isCollapsed = false,
      href,
      mainStyle = false,
      testId = '',
      isButton = false,
      secondLevelTitle = false,
      onClick,
      tabIndex,
      autoFocus,
      ariaCurrent,
      onKeyDown,
    }: Readonly<Props>,
    ref: React.Ref<HTMLAnchorElement | HTMLButtonElement>
  ) => {
    const pathname = usePathname();

    const isNewPath = (path: string) => {
      return path.includes('en-gb') || path.includes('de-de');
    };

    const isAnchor = () => {
      const pagePath = isNewPath(pathname);
      const linkPath = isNewPath(href ?? '');

      return pagePath !== linkPath;
    };

    const linkContent = (
      <>
        {mainStyle || secondLevelTitle ? (
          <Image
            src={isActive ? iconActive : icon}
            alt={isCollapsed ? label : ''}
            aria-hidden={!isCollapsed}
            width={24}
            height={24}
            className={iconStyle}
          />
        ) : (
          isActive && (
            <div className={bulletPointContainer}>
              <div className={bulletPointStyle} />
            </div>
          )
        )}
        {!isCollapsed && (
          <span
            className={`${mainStyle ? textStyle : secondTextStyle}  ${
              isActive ? activeTextStyle : inactiveTextStyle
            } ${secondLevelTitle ? activeSecondLevel : ''}`}
          >
            {label}
          </span>
        )}
      </>
    );

    const defaultSidebarLinkStyle = `${mainStyle ? linkStyle : secondLinkStyle(secondLevelTitle)} ${
      isActive ? activeLinkStyle : ''
    }`;

    if (isButton) {
      return (
        <button
          ref={ref as React.Ref<HTMLButtonElement>}
          key={label}
          className={defaultSidebarLinkStyle}
          data-testid={`${testId}-Sidebar-Link`}
          onClick={() => onClick?.()}
          tabIndex={tabIndex}
          autoFocus={autoFocus}
          aria-current={ariaCurrent} // aria-current, not camelCase - as native element
          onKeyDown={onKeyDown}
        >
          {linkContent}
        </button>
      );
    }

    if (isAnchor()) {
      return (
        <a
          ref={ref as React.Ref<HTMLAnchorElement>}
          href={href ?? '/'}
          key={label}
          className={defaultSidebarLinkStyle}
          data-testid={`${testId}-Sidebar-Link`}
          onClick={() => onClick?.()}
          tabIndex={tabIndex}
          autoFocus={autoFocus}
          aria-current={ariaCurrent}
          onKeyDown={onKeyDown}
        >
          {linkContent}
        </a>
      );
    }

    return (
      <Link
        href={href ?? '/'}
        key={label}
        className={defaultSidebarLinkStyle}
        data-testid={`${testId}-Sidebar-Link`}
        onClick={() => onClick?.()}
        tabIndex={tabIndex}
        autoFocus={autoFocus}
        ref={ref as React.Ref<HTMLAnchorElement>}
        aria-current={ariaCurrent}
        onKeyDown={onKeyDown}
      >
        {linkContent}
      </Link>
    );
  }
);
SidebarLink.displayName = 'SidebarLink';

export default SidebarLink;

const linkStyle =
  'flex p-4 cursor-pointer rounded-lg hover:bg-lightGrey4 mobile:flex-col mobile:justify-center mobile:items-center mobile:p-1 mobile:basis-24 mobile:gap-1';
const activeLinkStyle = 'bg-lightGrey5';
const iconStyle = 'w-6 h-6 text-transparent';
const textStyle =
  'mobile:text-sm overflow-hidden text-ellipsis mobile:overflow-visible mobile:leading-5 leading-6 ml-4 mobile:ml-0 mobile:w-[4.688rem] mobile:text-center mobile:w-full mobile:tracking-tight';
const activeTextStyle = 'text-primaryColor font-bold';
const activeSecondLevel = 'text-secondaryColor font-semibold';
const inactiveTextStyle = 'font-normal';

const secondLinkStyle = (secondLevelTitle: boolean) =>
  `flex mobile:w-full p-2 border-lightGrey5 rounded-lg ${
    secondLevelTitle ? '' : 'border'
  } cursor-pointer gap-2 hover:bg-lightGrey4 hover:border-lightGrey4 mobile:text-sm mobile:items-center`;
const secondTextStyle = 'overflow-hidden text-ellipsis xl:whitespace-nowrap';
const bulletPointContainer = 'flex h-6 items-center';
const bulletPointStyle = 'flex shrink-0 bg-primaryColor w-2 h-2 rounded-full';
