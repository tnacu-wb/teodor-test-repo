'use client';

import { Drawer, DrawerContent, DrawerTitle, DrawerDescription } from '@whitbread-eos/atoms/ui';
import { getPathForLocale } from '@whitbread-eos/utils';
import {
  MANAGE_TABS,
  getLocaleByPathname,
  useTranslation,
  formatIBAssetsUrl,
} from '@whitbread-eos/utils/server';
import Image from 'next/image';
import { useSearchParams, usePathname, useRouter } from 'next/navigation';
import { useState } from 'react';

import SidebarLink from '../SidebarLink/SidebarLink.component';
import {
  spendingSecondLevelLinks,
  spendingTabsLinks,
  spendingStatementsLink,
  spendingTransactionsLink,
} from '../utils/getters';

type Props = {
  icons: Record<string, string>;
  toggleSecondLevel: () => void;
  activeSecondLevelKey: string;
  activeFirstLevelKey: string;
  isTravelManager: boolean;
  isGuestUser: boolean;
  isYourSpendingTabEnabled: boolean;
  availableManageEmployeesTabs?: string[];
  availableCardsTabs?: string[];
  isStatementsPage?: boolean;
  isTransactionsPage?: boolean;
};

const ThirdLevelNavMobile = ({
  icons,
  toggleSecondLevel,
  activeSecondLevelKey,
  activeFirstLevelKey,
  isTravelManager,
  isGuestUser,
  isYourSpendingTabEnabled,
  availableManageEmployeesTabs,
  availableCardsTabs,
  isStatementsPage,
  isTransactionsPage,
}: Readonly<Props>) => {
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);

  const { t } = useTranslation(['common', 'cards', 'users', 'spending', 'layout']);
  const searchParams = useSearchParams();
  const router = useRouter();
  const tabParam = searchParams.get('tab');
  const accountParam = searchParams.get('account');

  const [isOpen, setIsOpen] = useState(false);

  // we don't have to display tabs links for spending and manage in some cases
  if (activeFirstLevelKey === 'spending' && !isYourSpendingTabEnabled && !isTravelManager) {
    return <></>;
  }

  if (activeFirstLevelKey === 'spending' && isYourSpendingTabEnabled && isGuestUser) {
    return <></>;
  }

  if (activeSecondLevelKey === 'manage/employees' && !availableManageEmployeesTabs?.length) {
    return <></>;
  }

  if (activeSecondLevelKey === 'manage/cards' && !availableCardsTabs?.length) {
    return <></>;
  }

  const handleBack = () => {
    if (spendingSecondLevelLinks.includes(activeSecondLevelKey)) {
      router.push(getPathForLocale(locale, 'spending'));
      setIsOpen(false);
    } else if (
      spendingStatementsLink.includes(activeSecondLevelKey) ||
      spendingTransactionsLink.includes(activeSecondLevelKey)
    ) {
      const queryParams = accountParam ? `&account=${accountParam}` : '';
      const targetPath = `spending?tab=innbusiness-pay${queryParams}`;
      router.push(getPathForLocale(locale, targetPath));
      setIsOpen(false);
    } else {
      setIsOpen(false);
      toggleSecondLevel();
    }
  };

  const handleToggle = () => {
    setIsOpen(!isOpen);
  };

  const spendingSecondLevelLinksMap = {
    'spending/management-information-report':
      'layout.innbusinessLayout.menu.spending.options.managementReport',
    'spending/emergency-report': 'layout.innbusinessLayout.menu.spending.options.emergencyReport',
    'spending/out-of-policy-report':
      'layout.innbusinessLayout.menu.spending.options.outOfPolicyReport',
  };

  const spendingStatementsSecondLevelLink = {
    'spending/statements':
      'layout.innbusinessLayout.menu.spending.options.statementsInvoicesPayments',
  };

  const spendingTransactionsSecondLevelLink = {
    'spending/transactions': 'spending.spending.transactions.title',
  };

  const getCurrentThirdLevelLabel = () => {
    let backLinkText = 'Back to Manage';

    switch (activeFirstLevelKey) {
      case 'manage': {
        if (pathname.includes('manage/cards')) {
          if (tabParam === MANAGE_TABS.INN_BUSINESS) {
            return { title: 'cards.cardMgmt.tabs.centrallyStored', backLinkText };
          } else if (
            tabParam === MANAGE_TABS.INN_BUSINESS_PAY ||
            pathname.includes('/innbusiness-pay/')
          ) {
            return { title: 'cards.cardMgmt.tabs.innBusinessPay', backLinkText };
          }
          return { title: 'cards.cardMgmt.tabs.centrallyStored', backLinkText };
        }
        return { title: 'common.layout.menu.manage.label', backLinkText };
      }
      case 'spending': {
        backLinkText = `Back to ${t(
          'layout.innbusinessLayout.menu.spending.options.companySpending'
        )}`;

        if (
          tabParam === 'company' ||
          (activeFirstLevelKey === activeSecondLevelKey &&
            tabParam === null &&
            !isYourSpendingTabEnabled) ||
          (activeFirstLevelKey === activeSecondLevelKey &&
            tabParam === null &&
            isYourSpendingTabEnabled &&
            isTravelManager)
        ) {
          return {
            title: 'layout.innbusinessLayout.menu.spending.options.companySpending',
            backLinkText,
          };
        } else if (
          tabParam === 'innbusiness-pay' ||
          (tabParam === null && isYourSpendingTabEnabled && !isTravelManager)
        ) {
          return { title: 'spending.spending.reporting.tab.innbusinessPay', backLinkText };
        } else if (tabParam === 'your-spending') {
          return { title: 'spending.spending.reporting.tab.employee.spending', backLinkText };
        }
        if (activeSecondLevelKey in spendingSecondLevelLinksMap && !isStatementsPage) {
          return {
            title:
              spendingSecondLevelLinksMap[
                activeSecondLevelKey as keyof typeof spendingSecondLevelLinksMap
              ],
            backLinkText,
          };
        }
        if (activeSecondLevelKey in spendingStatementsSecondLevelLink && isStatementsPage) {
          backLinkText = `Back to ${t(
            'layout.innbusinessLayout.menu.spending.options.businessPay'
          )}`;
          return {
            title:
              spendingStatementsSecondLevelLink[
                activeSecondLevelKey as keyof typeof spendingStatementsSecondLevelLink
              ],
            backLinkText,
          };
        }
        if (activeSecondLevelKey in spendingTransactionsSecondLevelLink && isTransactionsPage) {
          backLinkText = `Back to ${t(
            'layout.innbusinessLayout.menu.spending.options.businessPay'
          )}`;
          return {
            title:
              spendingTransactionsSecondLevelLink[
                activeSecondLevelKey as keyof typeof spendingTransactionsSecondLevelLink
              ],
            backLinkText,
          };
        }
      }
    }
    return { title: '', backLinkText };
  };

  const getThirdsLevelLinks = () => {
    const spendingLinksWithoutStatementsPage =
      isTravelManager &&
      spendingSecondLevelLinks.includes(activeSecondLevelKey) &&
      !isStatementsPage;

    return [
      // Manage Employees Options
      {
        label: t('users.userMgmt.manageEmployees.toggle.innBusiness.label'),
        isActive: tabParam === MANAGE_TABS.INN_BUSINESS || tabParam === null,
        href: getPathForLocale(locale, 'manage/employees'),
        testId: 'Manage-Employees-Inn-Business',
        condition: activeSecondLevelKey === 'manage/employees',
      },
      {
        label: t('users.userMgmt.manageEmployees.toggle.innBusinessPay.label'),
        isActive: tabParam === MANAGE_TABS.INN_BUSINESS_PAY,
        href: getPathForLocale(locale, 'manage/employees?tab=innbusiness-pay'),
        testId: 'Manage-Employees-Inn-Business-Pay',
        condition: activeSecondLevelKey === 'manage/employees',
      },
      // Manage Cards Options
      {
        label: t('cards.cardMgmt.tabs.centrallyStored'),
        isActive:
          tabParam === MANAGE_TABS.INN_BUSINESS ||
          (tabParam === null && !pathname.includes('/innbusiness-pay/')),
        href: getPathForLocale(locale, 'manage/cards'),
        testId: 'Manage-Cards-Inn-Business',
        condition: activeSecondLevelKey.includes('manage/cards'),
      },
      {
        label: t('cards.cardMgmt.tabs.innBusinessPay'),
        isActive:
          tabParam === MANAGE_TABS.INN_BUSINESS_PAY || pathname.includes('/innbusiness-pay/'),
        href: getPathForLocale(locale, 'manage/cards?tab=innbusiness-pay'),
        testId: 'Manage-Cards-Inn-Business-Pay',
        condition: activeSecondLevelKey.includes('manage/cards'),
      },
      // Spending tabs
      {
        label: t('layout.innbusinessLayout.menu.spending.options.companySpending'),
        isActive:
          tabParam === 'company' ||
          (activeSecondLevelKey === activeFirstLevelKey && tabParam === null),
        href: getPathForLocale(locale, 'spending?tab=company'),
        testId: 'Spending-Company',
        condition: isTravelManager && spendingTabsLinks.includes(activeSecondLevelKey),
      },
      {
        label: t('spending.spending.reporting.tab.innbusinessPay'),
        isActive:
          tabParam === MANAGE_TABS.INN_BUSINESS_PAY ||
          (activeSecondLevelKey === activeFirstLevelKey && tabParam === null && !isTravelManager),
        href: getPathForLocale(locale, 'spending?tab=innbusiness-pay'),
        testId: 'Spending-InnBusiness-Pay',
        condition: isYourSpendingTabEnabled
          ? !isGuestUser && spendingTabsLinks.includes(activeSecondLevelKey)
          : isTravelManager && spendingTabsLinks.includes(activeSecondLevelKey),
      },
      {
        label: t('spending.spending.reporting.tab.employee.spending'),
        isActive: tabParam === 'your-spending',
        href: getPathForLocale(locale, 'spending?tab=your-spending'),
        testId: 'Spending-Your-Spending',
        condition:
          isYourSpendingTabEnabled &&
          !isGuestUser &&
          spendingTabsLinks.includes(activeSecondLevelKey),
      },
      // Spending Company pages
      {
        label: t('layout.innbusinessLayout.menu.spending.options.managementReport'),
        isActive: activeSecondLevelKey === 'spending/management-information-report',
        href: getPathForLocale(locale, 'spending/management-information-report'),
        testId: 'Management-Information-Report',
        condition: spendingLinksWithoutStatementsPage,
      },
      {
        label: t('layout.innbusinessLayout.menu.spending.options.emergencyReport'),
        isActive: activeSecondLevelKey === 'spending/emergency-report',
        href: getPathForLocale(locale, 'spending/emergency-report'),
        testId: 'Emergency-Report',
        condition: spendingLinksWithoutStatementsPage,
      },
      {
        label: t('layout.innbusinessLayout.menu.spending.options.outOfPolicyReport'),
        isActive: activeSecondLevelKey === 'spending/out-of-policy-report',
        href: getPathForLocale(locale, 'spending/out-of-policy-report'),
        testId: 'Out-of-Policy-Report',
        condition: spendingLinksWithoutStatementsPage,
      },
      {
        label: t('layout.innbusinessLayout.menu.spending.options.statementsInvoicesPayments'),
        isActive: activeSecondLevelKey === 'spending/statements',
        href: getPathForLocale(locale, 'spending/statements'),
        testId: 'Statements',
        condition:
          isTravelManager &&
          spendingStatementsLink.includes(activeSecondLevelKey) &&
          isStatementsPage,
      },
      {
        label: t('spending.spending.transactions.title'),
        isActive: activeSecondLevelKey === 'spending/transactions',
        href: getPathForLocale(locale, 'spending/transactions'),
        testId: 'Transactions',
        condition:
          isTravelManager &&
          spendingTransactionsLink.includes(activeSecondLevelKey) &&
          isTransactionsPage,
      },
    ];
  };

  const currentThirdLevelLabel = getCurrentThirdLevelLabel();

  return (
    <>
      <div data-testid="Mobile-Nav-Third-Level-Container">
        {!isOpen && (
          <div data-testid="Mobile-Nav-activeLinkThirdLevel" className={activeLinkStyle}>
            <span>{currentThirdLevelLabel.title ? t(currentThirdLevelLabel.title) : ''}</span>
            <button
              className={`${thirdLevelToggleStyle} ${openButtonStyle}`}
              onClick={() => handleToggle()}
              data-testid="Third-Level-Expand-Button"
            >
              <Image
                alt="expand third level icon"
                src={formatIBAssetsUrl(icons['icon.chevron.up.purple'])}
                width={24}
                height={24}
              />
            </button>
          </div>
        )}
      </div>

      <Drawer open={isOpen} onOpenChange={setIsOpen}>
        <DrawerContent hasCloseButton={false} className={'p-0 mt-0'}>
          <DrawerTitle className={drawerTitleStyle}>{'Navigation Menu'}</DrawerTitle>
          <DrawerDescription className={drawerTitleStyle}>{'Navigation Menu'}</DrawerDescription>

          <div data-testid="Third-Level-Drawer" className={thirdLevelDrawerStyle}>
            <button
              className={`${thirdLevelToggleStyle} ${closeButtonStyle}`}
              onClick={() => handleToggle()}
            >
              <Image
                alt="collpase third level icon"
                src={formatIBAssetsUrl(icons['icon.chevron.down.purple'])}
                width={24}
                height={24}
              />
            </button>
            {!spendingTabsLinks.includes(activeSecondLevelKey) && (
              <button
                data-testid="backButton"
                onClick={() => handleBack()}
                className={backButtonStyle}
              >
                <Image
                  width={24}
                  height={24}
                  src={formatIBAssetsUrl(icons['icon.chevron.left.purple'])}
                  alt="back icon"
                />
                <span className={backButtonTextStyle}>{currentThirdLevelLabel.backLinkText}</span>
              </button>
            )}
            <div className={linksContainerStyle}>
              {getThirdsLevelLinks()?.map(
                (itemDetails: any, index) =>
                  itemDetails.condition && (
                    <SidebarLink
                      key={index}
                      label={itemDetails.label}
                      isActive={itemDetails.isActive}
                      href={itemDetails.href}
                      testId={itemDetails.testId}
                      onClick={handleToggle}
                    />
                  )
              )}
            </div>
          </div>
        </DrawerContent>
      </Drawer>
    </>
  );
};

export default ThirdLevelNavMobile;

const activeLinkStyle =
  'flex text-primaryColor items-center text-sm font-bold rounded-lg bg-lightGrey5 hover:border-lightGrey4 p-2 mb-2';
const thirdLevelDrawerStyle = 'px-4 pb-6 pt-20';
const linksContainerStyle = 'flex flex-col gap-2';
const closeButtonStyle = 'absolute right-6 top-4';
const openButtonStyle = 'ml-auto';
const thirdLevelToggleStyle =
  'flex shrink-0 justify-center items-center w-8 h-8 ml-auto rounded-full border border-lightGrey3 bottom-28 -right-4 bg-baseWhite cursor-pointer hover:bg-lightGrey4 active:bg-toggleButtonPressed';
const backButtonStyle = 'flex text-secondaryColor mb-6';
const backButtonTextStyle = 'ml-2 font-semibold';
const drawerTitleStyle = 'hidden';
