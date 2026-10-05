'use client';

import {
  BUSINESS_BOOKER_USER_ROLES,
  FT_IB_OUT_OF_POLICY_REPORT,
  FT_IB_BOOKING_ALERTS,
  MenuInnB,
  FT_IB_COST_CENTRE_MANAGEMENT,
  CustomerAccountDetails,
  FT_IB_PAY_PIBA_EURO,
  FT_IB_YOUR_SPENDING,
} from '@whitbread-eos/api';
import { useFeatureToggle } from '@whitbread-eos/utils';
import {
  formatIBAssetsUrl,
  getLocaleByPathname,
  useTranslation,
} from '@whitbread-eos/utils/server';
import { usePathname, useSearchParams } from 'next/navigation';
import { useEffect, useRef, useState } from 'react';

import FirstLevelNav from './FirstLevelNav/index';
import SecondLevelNav from './SecondLevelNav/index';
import { SidebarToggle } from './SidebarToggle/index';
import {
  getSecondLevelLinks,
  spendingSecondLevelLinks,
  spendingStatementsLink,
  spendingTransactionsLink,
} from './utils/getters';

type Props = {
  userRole: string;
  isAccountHolder: boolean;
  menuLabels: MenuInnB;
  icons: Record<string, string>;
  collapsed?: boolean;
  isCardHolder: boolean;
  accounts?: CustomerAccountDetails[];
  companyType?: string;
  isTethered: boolean;
};

const Sidebar = ({
  menuLabels,
  icons,
  collapsed,
  isAccountHolder,
  isCardHolder,
  userRole,
  accounts = [],
  companyType,
  isTethered,
}: Readonly<Props>) => {
  const baseDataTestId = 'SidebarDesktop';
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const activeSecondLevelKey = pathname.split(`/${locale}/`)?.[1];
  const activeFirstLevelKey = activeSecondLevelKey?.split('/')[0];
  const { t } = useTranslation(['common', 'cards', 'users', 'spending']);
  const searchParams = useSearchParams();
  const accountParam = searchParams.get('account');

  const {
    [FT_IB_OUT_OF_POLICY_REPORT]: isOutOfPolicyReportActive,
    [FT_IB_BOOKING_ALERTS]: isBookingAlertsActive,
    [FT_IB_COST_CENTRE_MANAGEMENT]: isCostCentreManagementActive,
    [FT_IB_PAY_PIBA_EURO]: isPibaEuroActive,
    [FT_IB_YOUR_SPENDING]: isYourSpendingTabEnabled,
  } = useFeatureToggle();

  const isTravelManager = userRole === BUSINESS_BOOKER_USER_ROLES.SUPER;
  const isBusinessPayManager = userRole === BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_MANAGER;
  const isGuestUser = userRole === BUSINESS_BOOKER_USER_ROLES.STAYER;
  const secondLevelLinks = getSecondLevelLinks(
    { activeFirstLevelKey, activeSecondLevelKey },
    {
      isAccountHolder,
      isCardHolder,
      isTravelManager,
      isBusinessPayManager,
      isGuestUser,
      isTethered,
      isOutOfPolicyReportActive,
      isBookingAlertsActive,
      isCostCentreManagementActive,
      isPibaEuroActive,
      isYourSpendingTabEnabled,
    },
    locale,
    false,
    t,
    accountParam,
    accounts
  );
  type SidebarLinkType = typeof secondLevelLinks;

  const checkSpendingLinkCondition = (firstLevelKey: string, secondLevelKey: string) => {
    const spendingLinkIncludesKey =
      spendingSecondLevelLinks.includes(secondLevelKey) ||
      spendingStatementsLink.includes(secondLevelKey) ||
      spendingTransactionsLink.includes(secondLevelKey);

    if (firstLevelKey === 'spending') {
      return spendingLinkIncludesKey;
    }

    return true;
  };

  const spendingLinkCondition = checkSpendingLinkCondition(
    activeFirstLevelKey,
    activeSecondLevelKey
  );

  const hasSecondLevel =
    (secondLevelLinks[activeFirstLevelKey as keyof SidebarLinkType]
      ? secondLevelLinks[activeFirstLevelKey as keyof SidebarLinkType]?.some(
          (item: { condition: boolean }) => item.condition
        )
      : false) && spendingLinkCondition;

  const [isCollapsed, setIsCollapsed] = useState(!!collapsed || hasSecondLevel);
  const [showSecondLevelNav, setShowSecondLevelNav] = useState(true);

  useEffect(() => {
    if (!collapsed) {
      if (hasSecondLevel) {
        setIsCollapsed(true);
      } else {
        setIsCollapsed(false);
      }
    }
  }, [pathname, hasSecondLevel, collapsed]);

  // Show SecondLevelNav when first level changes
  useEffect(() => {
    setShowSecondLevelNav(true);
  }, [activeFirstLevelKey]);

  // handle keyboard navigation; and show SecondLevelNav where relevant keys are pressed
  const handleFirstLevelKeyDown = (e: React.KeyboardEvent) => {
    e.stopPropagation();
    // Always handle Enter or Space for navigation/click, regardless of second level nav
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      // Optionally, trigger navigation/click for the focused FirstLevelNav item
      const focused = document.activeElement as HTMLElement | null;
      if (focused && typeof focused.click === 'function') {
        focused.click();
      }
      if (hasSecondLevel) {
        setShowSecondLevelNav(true);
      }
      return;
    }
    if (e.key === 'ArrowRight' && hasSecondLevel && secondLevelNavRef.current) {
      // Focus the first focusable element in SecondLevelNav
      const focusable = secondLevelNavRef.current.querySelector<HTMLElement>(
        'a, button, [tabindex]:not([tabindex="-1"])'
      );
      if (focusable) {
        focusable.focus();
        e.preventDefault();
      }
    }
  };

  const handleToggle = (): void => {
    setIsCollapsed(!isCollapsed);
  };
  const secondLevelNavRef = useRef<HTMLDivElement>(null);
  const firstLevelNavRef = useRef<HTMLDivElement>(null);

  // Handler for left arrow or escape in SecondLevelNav
  const handleSecondLevelNavClose = () => {
    setTimeout(() => {
      // focus the active FirstLevelNav item using aria-current="page":
      let firstLevelActive: HTMLElement | null = null;
      if (firstLevelNavRef.current) {
        firstLevelActive = firstLevelNavRef.current.querySelector(
          '[data-testid$="Sidebar-Link"][aria-current="page"]'
        );
        // focus first Sidebar-Link with tabindex if no active found
        if (!(firstLevelActive instanceof HTMLElement)) {
          firstLevelActive = firstLevelNavRef.current.querySelector(
            '[data-testid$="Sidebar-Link"][tabindex]'
          );
        }
      }
      if (firstLevelActive instanceof HTMLElement) {
        firstLevelActive.setAttribute('tabindex', '0');
        firstLevelActive.focus();
      }
    }, 0);
  };

  return (
    <div className={containerBaseStyle} data-testid={`${baseDataTestId}-container`}>
      <nav className={fullNavContainerStyle}>
        <div ref={firstLevelNavRef} data-testid="FirstLevelNav-ref-container">
          <FirstLevelNav
            menuLabels={menuLabels}
            isAccountHolder={isAccountHolder}
            isCardHolder={isCardHolder}
            isCollapsed={isCollapsed}
            activeKey={activeFirstLevelKey}
            isTravelManager={isTravelManager}
            isBusinessPayManager={isBusinessPayManager}
            companyType={companyType}
            onKeyDown={handleFirstLevelKeyDown}
            isTethered={isTethered}
            isPibaEuroEnabled={isPibaEuroActive}
          />
        </div>
        {hasSecondLevel && showSecondLevelNav && (
          <div ref={secondLevelNavRef}>
            <SecondLevelNav
              secondLevelLinks={secondLevelLinks[activeFirstLevelKey as keyof SidebarLinkType]}
              icons={icons}
              autoFocusFirst
              onCloseSecondLevelNav={handleSecondLevelNavClose}
              onLeftArrowBack={handleSecondLevelNavClose}
            />
          </div>
        )}
      </nav>
      <SidebarToggle
        collapseIcon={formatIBAssetsUrl(icons['icon.chevron.left.purple'])}
        expandIcon={formatIBAssetsUrl(icons['icon.chevron.right.purple'])}
        isCollapsed={isCollapsed}
        onToggle={handleToggle}
      />
    </div>
  );
};

export default Sidebar;

const fullNavContainerStyle = 'flex gap-6';
const containerBaseStyle =
  'fixed flex flex-col grow-0 shrink-0 h-contentHeight top-[--headerHeight] pr-[0.938rem] pt-12 pb-9 pl-[4.125rem] border-r border-lightGrey3 bg-baseWhite z-30 mobile:hidden';
