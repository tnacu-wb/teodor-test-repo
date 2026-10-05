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
  CountryCode,
} from '@whitbread-eos/api';
import { Drawer, DrawerContent, DrawerTitle, DrawerDescription } from '@whitbread-eos/atoms/ui';
import { useFeatureToggle } from '@whitbread-eos/utils';
import {
  formatIBAssetsUrl,
  getLocaleByPathname,
  useTranslation,
  getAvailableTabs,
  getCountryLanguageByLocale,
} from '@whitbread-eos/utils/server';
import Image from 'next/image';
import { usePathname } from 'next/navigation';
import { useMemo, useState } from 'react';

import FirstLevelNav from '../FirstLevelNav';
import SecondLevelNav from '../SecondLevelNav';
import ThirdLevelNavMobile from '../ThirdLevelNavMobile';
import { getSecondLevelLinks } from '../utils/getters';

type Props = {
  userRole: string;
  isAccountHolder: boolean;
  isTethered: boolean;
  menuLabels: MenuInnB;
  icons: Record<string, string>;
  isCollapsedDefault?: boolean;
  isCardHolder: boolean;
  accounts?: CustomerAccountDetails[];
  companyType?: string;
};

const SidebarMobile = ({
  userRole,
  menuLabels,
  icons,
  isAccountHolder,
  isCardHolder,
  isTethered: isTetheredAccount,
  accounts = [],
  companyType,
}: Readonly<Props>) => {
  const baseDataTestId = 'SidebarMobile';
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const { t } = useTranslation(['common', 'cards', 'users', 'spending']);

  const [isSecondLevelOpen, setIsSecondLevelOpen] = useState(false);

  const { language } = getCountryLanguageByLocale(locale);
  const isDeLanguage = language === CountryCode.DE;

  const handleSecondLevelToggle = (): void => {
    setIsSecondLevelOpen(!isSecondLevelOpen);
  };

  const handleManageClick = (): void => {
    setActiveSideBar('manage');
    setIsSecondLevelOpen(!isSecondLevelOpen);
  };

  const handleSpending = (): void => {
    setActiveSideBar('spending');
    setIsSecondLevelOpen(!isSecondLevelOpen);
  };

  const hasThirdLevel = () => {
    const thirdLevelPaths = ['manage/employees', 'manage/cards', 'spending'];
    return thirdLevelPaths?.some((path) => pathname?.includes(path)) || false;
  };

  const activeSecondLevelKey = pathname?.split(`/${locale}/`)?.[1];
  const activeFirstLevelKey = activeSecondLevelKey?.split('/')[0];
  const [activeSideBar, setActiveSideBar] = useState(activeFirstLevelKey);

  const {
    [FT_IB_OUT_OF_POLICY_REPORT]: isOutOfPolicyReportActive,
    [FT_IB_BOOKING_ALERTS]: isBookingAlertsActive,
    [FT_IB_COST_CENTRE_MANAGEMENT]: isCostCentreManagementActive,
    [FT_IB_PAY_PIBA_EURO]: isPibaEuroEnabled,
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
      isTethered: isTetheredAccount,
      isOutOfPolicyReportActive,
      isBookingAlertsActive,
      isCostCentreManagementActive,
      isYourSpendingTabEnabled,
      isPibaEuroActive: isPibaEuroEnabled,
    },
    locale,
    true,
    t,
    undefined,
    accounts
  );
  type SidebarLinkType = typeof secondLevelLinks;

  const hasAvailableLinks = useMemo(() => {
    return secondLevelLinks[activeSideBar as keyof SidebarLinkType]?.some(
      (item: { condition: boolean }) => item.condition
    );
  }, [activeSideBar, secondLevelLinks]);

  const availableManageEmployeesTabs = getAvailableTabs(
    isTravelManager,
    isTetheredAccount,
    isAccountHolder,
    isCardHolder,
    false,
    isDeLanguage,
    isPibaEuroEnabled,
    false,
    isBusinessPayManager
  );

  const availableCardsTabs = getAvailableTabs(
    isTravelManager,
    isTetheredAccount,
    isAccountHolder,
    isCardHolder,
    true,
    isDeLanguage,
    isPibaEuroEnabled,
    false,
    isBusinessPayManager
  );

  const isStatementsPage = activeSecondLevelKey === 'spending/statements';
  const isTransactionsPage = activeSecondLevelKey === 'spending/transactions';

  return (
    <nav
      data-testid={`${baseDataTestId}-container`}
      className={`${containerBaseStyle} ${hasThirdLevel() ? expandedStyle : ''}`}
    >
      {hasAvailableLinks && (
        <Drawer open={isSecondLevelOpen} onOpenChange={setIsSecondLevelOpen}>
          <DrawerContent hasCloseButton={false} className={'p-0 mt-0'}>
            <DrawerTitle className={drawerTitleStyle}>{'Navigation Menu'}</DrawerTitle>
            <DrawerDescription className={drawerTitleStyle}>{'Navigation Menu'}</DrawerDescription>

            <div className={secondLevelDrawerStyle}>
              <button
                className={secondLevelToggleStyle}
                onClick={() => handleSecondLevelToggle()}
                data-testid="Second-Level-Collapse-Icon"
              >
                <Image
                  alt="collapse second level icon"
                  src={formatIBAssetsUrl(icons['icon.chevron.down.purple'])}
                  width={24}
                  height={24}
                />
              </button>
              <SecondLevelNav
                icons={icons}
                onLinkClick={handleSecondLevelToggle}
                secondLevelLinks={secondLevelLinks[activeSideBar as keyof SidebarLinkType]}
              />
            </div>
          </DrawerContent>
        </Drawer>
      )}

      {hasThirdLevel() && (
        <ThirdLevelNavMobile
          isTravelManager={isTravelManager}
          isGuestUser={isGuestUser}
          isYourSpendingTabEnabled={isYourSpendingTabEnabled}
          icons={icons}
          toggleSecondLevel={handleSecondLevelToggle}
          activeSecondLevelKey={activeSecondLevelKey}
          activeFirstLevelKey={activeFirstLevelKey}
          availableManageEmployeesTabs={availableManageEmployeesTabs}
          availableCardsTabs={availableCardsTabs}
          isStatementsPage={isStatementsPage}
          isTransactionsPage={isTransactionsPage}
        />
      )}

      <FirstLevelNav
        menuLabels={menuLabels}
        isAccountHolder={isAccountHolder}
        isMobile={true}
        onManageClick={handleManageClick}
        onSpendingClick={handleSpending}
        activeKey={activeFirstLevelKey}
        isTravelManager={isTravelManager}
        isBusinessPayManager={isBusinessPayManager}
        isTethered={isTetheredAccount}
        isPibaEuroEnabled={isPibaEuroEnabled}
        companyType={companyType}
      />
    </nav>
  );
};

export default SidebarMobile;

const containerBaseStyle =
  'fixed bottom-0 left-0 hidden mobile:flex flex-col w-full max-h-mobileSidebarHeight border-t border-lightGrey3 bg-baseWhite z-30 px-4 py-2 mobile:min-w-[23.4rem]';
const expandedStyle = 'rounded-t-lg';
const secondLevelDrawerStyle = 'px-4 pb-6 pt-20';
const secondLevelToggleStyle =
  'absolute right-6 top-4 flex justify-center items-center w-8 h-8 rounded-full border border-lightGrey3 bg-baseWhite cursor-pointer hover:bg-lightGrey4 active:bg-toggleButtonPressed';
const drawerTitleStyle = 'hidden';
