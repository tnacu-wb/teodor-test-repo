'use client';

import { LOCALES, MenuInnB, CompanyType, CountryCode } from '@whitbread-eos/api';
import { getPathForLocale } from '@whitbread-eos/utils';
import {
  formatIBAssetsUrl,
  getLocaleByPathname,
  useTranslation,
} from '@whitbread-eos/utils/server';
import { usePathname } from 'next/navigation';
import { useEffect, useRef } from 'react';

import SidebarLink from '../SidebarLink/SidebarLink.component';

// Key for sessionStorage to persist focus intent
const FOCUS_NAV_KEY = 'sidebarNavFocusKey';

type Props = {
  menuLabels: MenuInnB;
  isAccountHolder: boolean;
  isCardHolder?: boolean;
  isBusinessPayManager?: boolean;
  isCollapsed?: boolean;
  activeKey: string;
  isTravelManager: boolean;
  onManageClick?: () => void;
  onSpendingClick?: () => void;
  companyType?: string;
  isMobile?: boolean;
  onKeyDown?: (e: React.KeyboardEvent<HTMLDivElement>) => void;
  isTethered?: boolean;
  isPibaEuroEnabled?: boolean;
};

const FirstLevelNav = ({
  menuLabels,
  isAccountHolder,
  isCardHolder = false,
  isBusinessPayManager = false,
  isCollapsed = false,
  activeKey,
  isTravelManager,
  onManageClick,
  onSpendingClick,
  companyType,
  isMobile = false,
  onKeyDown,
  isTethered,
  isPibaEuroEnabled,
}: Props) => {
  const baseDataTestId = 'FirstLevelNav';
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const { t } = useTranslation();

  const { home, spending, manage, bookings, contact } = menuLabels;
  const country = locale === LOCALES.EN ? 'gb' : 'de';
  const language = locale === LOCALES.EN ? 'en' : 'de';

  const defaultRedirectPage = 'homepage';
  const getManageLink = () => {
    const isNonTMCardHolderOnly = isCardHolder && !isAccountHolder && !isTravelManager;
    if (!isTravelManager && !isCardHolder && !isAccountHolder && !isBusinessPayManager) {
      return defaultRedirectPage;
    }
    if (isNonTMCardHolderOnly) {
      return 'manage/cards';
    }

    return 'manage/employees';
  };
  const isManageTabVisible = getManageLink() !== defaultRedirectPage;
  const getIsActive = (key: string) => activeKey === key;
  const getAriaCurrent = (key: string) => (getIsActive(key) ? 'page' : undefined);
  const isDeLanguage = language === CountryCode.DE;

  // hide spending tab for Business Pay customers in DE if PIBA Euro is not active and not tethered
  const isSpendingTabHidden =
    companyType === CompanyType.BUSINESS_PAY && isDeLanguage && !isPibaEuroEnabled && !isTethered;

  // For bookings (dashboard)
  const isBookingsActive = pathname.includes('account/dashboard');
  const bookingsAriaCurrent = isBookingsActive ? 'page' : undefined;

  // Refs for focus restoration
  const linkRefs = {
    homepage: useRef<HTMLAnchorElement | null>(null),
    spending: useRef<HTMLAnchorElement | null>(null),
    manage: useRef<HTMLAnchorElement | null>(null),
    bookings: useRef<HTMLAnchorElement | null>(null),
    'contact-us': useRef<HTMLAnchorElement | null>(null),
  };

  // Restore focus if a nav key is present in sessionStorage (runs on pathname change)
  useEffect(() => {
    const focusKey = sessionStorage.getItem(FOCUS_NAV_KEY);
    if (focusKey && linkRefs[focusKey as keyof typeof linkRefs]?.current) {
      linkRefs[focusKey as keyof typeof linkRefs].current?.focus();
      sessionStorage.removeItem(FOCUS_NAV_KEY);
    }
  }, [pathname]);

  // Blur nav item if user navigates with back/forward (runs once on mount)
  useEffect(() => {
    const handlePopState = () => {
      const active = document.activeElement;
      for (const ref of Object.values(linkRefs)) {
        if (ref.current && ref.current === active) {
          ref.current.blur();
          sessionStorage.removeItem(FOCUS_NAV_KEY);
        }
      }
    };
    window.addEventListener('popstate', handlePopState);
    return () => {
      window.removeEventListener('popstate', handlePopState);
    };
  }, []);

  // Common handler for nav keydown
  const handleNavKeyDown = (key: string) => (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' || e.key === ' ') {
      sessionStorage.setItem(FOCUS_NAV_KEY, key);
    }
  };

  return (
    <div
      data-testid={`${baseDataTestId}-container`}
      className={`${navBaseStyle} ${isCollapsed ? navCollapsedStyle : navExpandedStyle}`}
      onKeyDown={onKeyDown}
    >
      <SidebarLink
        ref={linkRefs.homepage}
        label={t('layout.menu.home.label')}
        icon={formatIBAssetsUrl(home?.icon ?? '')}
        iconActive={formatIBAssetsUrl(home?.iconActive ?? '')}
        isActive={getIsActive('homepage')}
        isCollapsed={isCollapsed}
        href={getPathForLocale(locale, 'homepage')}
        mainStyle={true}
        testId="Home"
        ariaCurrent={getAriaCurrent('homepage')}
        onKeyDown={handleNavKeyDown('homepage')}
      />

      {!isSpendingTabHidden && (
        <SidebarLink
          ref={linkRefs.spending}
          label={t('layout.menu.spending.label')}
          icon={formatIBAssetsUrl(spending?.icon ?? '')}
          iconActive={formatIBAssetsUrl(spending?.iconActive ?? '')}
          isActive={getIsActive('spending')}
          isCollapsed={isCollapsed}
          href={getPathForLocale(locale, 'spending')}
          onClick={onSpendingClick}
          mainStyle={true}
          testId="Spending"
          ariaCurrent={getAriaCurrent('spending')}
          onKeyDown={handleNavKeyDown('spending')}
        />
      )}

      {isManageTabVisible && (
        <SidebarLink
          ref={linkRefs.manage}
          label={t('layout.menu.manage.label')}
          icon={formatIBAssetsUrl(manage?.icon ?? '')}
          iconActive={formatIBAssetsUrl(manage?.iconActive ?? '')}
          isActive={getIsActive('manage')}
          isCollapsed={isCollapsed}
          href={getPathForLocale(locale, getManageLink())}
          onClick={onManageClick}
          mainStyle={true}
          testId="Manage"
          isButton={isMobile}
          ariaCurrent={getAriaCurrent('manage')}
          onKeyDown={handleNavKeyDown('manage')}
        />
      )}

      {companyType !== CompanyType.BUSINESS_PAY && (
        <SidebarLink
          ref={linkRefs.bookings}
          label={t('layout.menu.bookings.label')}
          icon={formatIBAssetsUrl(bookings?.icon ?? '')}
          iconActive={formatIBAssetsUrl(bookings?.iconActive ?? '')}
          isActive={isBookingsActive}
          isCollapsed={isCollapsed}
          href={`/${country}/${language}/business-booker/account/dashboard.html`}
          mainStyle={true}
          testId="Bookings"
          ariaCurrent={bookingsAriaCurrent}
          onKeyDown={handleNavKeyDown('bookings')}
        />
      )}
      <SidebarLink
        ref={linkRefs['contact-us']}
        label={t('layout.menu.contact.label')}
        icon={formatIBAssetsUrl(contact?.icon ?? '')}
        iconActive={formatIBAssetsUrl(contact?.iconActive ?? '')}
        isActive={getIsActive('contact-us')}
        isCollapsed={isCollapsed}
        href={getPathForLocale(locale, 'contact-us')}
        mainStyle={true}
        testId="ContactUs"
        ariaCurrent={getAriaCurrent('contact-us')}
        onKeyDown={handleNavKeyDown('contact-us')}
      />
    </div>
  );
};

export default FirstLevelNav;

const navBaseStyle =
  'flex flex-col gap-2 mobile:gap-1 mobile:flex-row mobile:w-full mobile:justify-between mobile:w-auto';
const navExpandedStyle = 'w-[11.375rem]';
const navCollapsedStyle = 'w-14 mobile:hidden';
