import { CountryCode, CustomerAccountDetails, Scheme, LOCALES } from '@whitbread-eos/api';
import {
  getPathForLocale,
  getCountryLanguageByLocale,
  showCardManagementForBusinessPayManager,
} from '@whitbread-eos/utils/server';

interface ActiveLevels {
  activeFirstLevelKey: string;
  activeSecondLevelKey: string;
}

interface UserRoles {
  isAccountHolder: boolean;
  isCardHolder: boolean;
  isTravelManager: boolean;
  isBusinessPayManager: boolean;
  isGuestUser: boolean;
  isTethered: boolean;
  isOutOfPolicyReportActive?: boolean;
  isBookingAlertsActive?: boolean;
  isCostCentreManagementActive?: boolean;
  isPibaEuroActive?: boolean;
  isYourSpendingTabEnabled?: boolean;
}

const linkArray = {
  manageEmployees: 'manage/employees',
  bookingAllowances: 'manage/allowances',
  bookingAlerts: 'manage/alerts',
  cardManagement: 'manage/cards',
  employeeQuestions: 'manage/questions',
  companyDetails: 'manage/company',
  costCentres: 'manage/cost-centres',
};

export const spendingSecondLevelLinks = [
  'spending/management-information-report',
  'spending/emergency-report',
  'spending/out-of-policy-report',
];

export const spendingStatementsLink = ['spending/statements'];

export const spendingTransactionsLink = ['spending/transactions'];

export const spendingTabsLinks = [
  'spending',
  'spending?tab=company',
  'spending?tab=innbusiness-pay',
  'spending?tab=your-spending',
];

export const getSecondLevelLinks = (
  activeLevels: ActiveLevels,
  userRoles: UserRoles,
  locale: LOCALES,
  mobile = false,
  t: (key: string) => string,
  searchParams?: string | null,
  accounts?: CustomerAccountDetails[]
): { manage: Array<any>; spending?: Array<any> } => {
  const { activeSecondLevelKey } = activeLevels;
  const {
    isAccountHolder,
    isCardHolder,
    isTravelManager,
    isBusinessPayManager,
    isGuestUser,
    isTethered,
    isOutOfPolicyReportActive,
    isBookingAlertsActive,
    isCostCentreManagementActive,
    isPibaEuroActive = false,
    isYourSpendingTabEnabled = false,
  } = userRoles;

  const buildInnBusinessPayPath = (searchParams?: string | null): string => {
    const queryParams = searchParams ? `&account=${searchParams}` : '';
    return `spending?tab=innbusiness-pay${queryParams}`;
  };

  const buildStatementsPath = (searchParams?: string | null): string => {
    const queryParams = searchParams ? `?account=${searchParams}` : '';
    return `${spendingStatementsLink[0]}${queryParams}`;
  };

  const isSpendingPageActive =
    spendingSecondLevelLinks.includes(activeSecondLevelKey) ||
    spendingTabsLinks.includes(activeSecondLevelKey);

  const isStatementsPageActive = spendingStatementsLink.includes(activeSecondLevelKey);
  const isTransactionsPageActive = spendingTransactionsLink.includes(activeSecondLevelKey);
  const hasDeAccounts = accounts?.some((account) => account.scheme === ('DE' as Scheme));

  const showCardManagementForBPM = () => {
    const { language } = getCountryLanguageByLocale(locale);
    const isDeSite = language === CountryCode.DE;

    return showCardManagementForBusinessPayManager(
      isBusinessPayManager,
      isDeSite,
      isTethered,
      isPibaEuroActive,
      isAccountHolder,
      isCardHolder
    );
  };

  const spendingMobile = [
    {
      label: t('spending.spending.reporting.tab.company.spending'),
      isActive: false,
      href: getPathForLocale(locale, 'spending?tab=company'),
      testId: 'Spending-Company',
      condition:
        isTravelManager &&
        (spendingSecondLevelLinks.includes(activeSecondLevelKey) ||
          spendingTabsLinks.includes(activeSecondLevelKey)),
    },
    {
      label: t('spending.spending.reporting.tab.innbusinessPay'),
      isActive: false,
      href: getPathForLocale(locale, 'spending?tab=innbusiness-pay'),
      testId: 'Spending-InnBusiness-Pay',
      condition: isYourSpendingTabEnabled
        ? !isGuestUser &&
          (spendingSecondLevelLinks.includes(activeSecondLevelKey) ||
            spendingTabsLinks.includes(activeSecondLevelKey))
        : isTravelManager &&
          (spendingSecondLevelLinks.includes(activeSecondLevelKey) ||
            spendingTabsLinks.includes(activeSecondLevelKey)),
    },
    {
      label: t('spending.spending.reporting.tab.employee.spending'),
      isActive: false,
      href: getPathForLocale(locale, 'spending?tab=your-spending'),
      testId: 'Spending-Your-Spending',
      condition:
        isYourSpendingTabEnabled &&
        !isGuestUser &&
        (spendingSecondLevelLinks.includes(activeSecondLevelKey) ||
          spendingTabsLinks.includes(activeSecondLevelKey)),
    },
  ];
  const spendingDesktop = [
    {
      type: 'title',
      key: 'CompanySpending',
      label: t('layout.innbusinessLayout.menu.spending.options.companySpending'),
      isActive: activeSecondLevelKey === 'spending',
      href: getPathForLocale(locale, 'spending'),
      condition: isTravelManager,
    },
    {
      key: 'ManagementInformationReport',
      label: t('layout.innbusinessLayout.menu.spending.options.managementReport'),
      isActive: activeSecondLevelKey === spendingSecondLevelLinks[0],
      href: getPathForLocale(locale, spendingSecondLevelLinks[0]),
      condition: isTravelManager,
    },
    {
      key: 'EmergencyReport',
      label: t('layout.innbusinessLayout.menu.spending.options.emergencyReport'),
      isActive: activeSecondLevelKey === spendingSecondLevelLinks[1],
      href: getPathForLocale(locale, spendingSecondLevelLinks[1]),
      condition: isTravelManager,
    },
    {
      key: 'OutOfPolicyReport',
      label: t('layout.innbusinessLayout.menu.spending.options.outOfPolicyReport'),
      isActive: activeSecondLevelKey === spendingSecondLevelLinks[2],
      href: getPathForLocale(locale, spendingSecondLevelLinks[2]),
      condition: isOutOfPolicyReportActive && isTravelManager,
    },
  ];
  const spendingItems = isSpendingPageActive
    ? mobile
      ? spendingMobile
      : spendingDesktop
    : isStatementsPageActive || isTransactionsPageActive
      ? [
          {
            type: 'title',
            key: 'PremierInnBusinessPay',
            label: t('layout.innbusinessLayout.menu.spending.options.businessPay'),
            isActive: activeSecondLevelKey === 'spending',
            href: getPathForLocale(locale, buildInnBusinessPayPath(searchParams)),
            condition: isTravelManager,
          },
          {
            key: 'Statements',
            label: t('layout.innbusinessLayout.menu.spending.options.statementsInvoicesPayments'),
            isActive: activeSecondLevelKey === spendingStatementsLink[0],
            href: getPathForLocale(locale, buildStatementsPath(searchParams)),
            condition: isTravelManager,
          },
          {
            key: 'Transactions',
            label: t('spending.spending.transactions.title'),
            isActive: activeSecondLevelKey === spendingTransactionsLink[0],
            href: getPathForLocale(locale, spendingTransactionsLink[0]),
            condition: isTravelManager,
          },
        ]
      : undefined;

  return {
    manage: [
      {
        key: 'ManageEmployees',
        label: t('common.layout.menu.manage.options.employees'),
        isActive: activeSecondLevelKey === linkArray.manageEmployees,
        href: getPathForLocale(locale, linkArray.manageEmployees),
        condition: isAccountHolder || isTravelManager || isBusinessPayManager,
      },
      {
        key: 'BookingAllowances',
        label: t('common.layout.menu.manage.options.allowances'),
        isActive: activeSecondLevelKey === linkArray.bookingAllowances,
        href: getPathForLocale(locale, linkArray.bookingAllowances),
        condition: isTravelManager,
      },
      {
        key: 'BookingAlerts',
        label: t('common.layout.menu.manage.options.alerts'),
        isActive: activeSecondLevelKey === linkArray.bookingAlerts,
        href: getPathForLocale(locale, linkArray.bookingAlerts),
        condition: isBookingAlertsActive && isTravelManager,
      },
      {
        key: 'CardManagement',
        label: t('common.layout.menu.manage.options.cards'),
        isActive: activeSecondLevelKey === linkArray.cardManagement,
        href: getPathForLocale(locale, linkArray.cardManagement),
        condition: isTravelManager || showCardManagementForBPM() || isCardHolder || isAccountHolder,
      },
      {
        key: 'CostCentreManagement',
        label: t('layout.innbusinessLayout.menu.manage.options.costCentres'),
        isActive: activeSecondLevelKey === linkArray.costCentres,
        href: getPathForLocale(locale, linkArray.costCentres),
        condition: isCostCentreManagementActive && hasDeAccounts,
      },
      {
        key: 'EmployeeQuestions',
        label: t('common.layout.menu.manage.options.questions'),
        isActive: activeSecondLevelKey === linkArray.employeeQuestions,
        href: getPathForLocale(locale, linkArray.employeeQuestions),
        condition: isTravelManager,
      },
      {
        key: 'CompanyDetails',
        label: t('common.layout.menu.manage.options.company'),
        isActive: activeSecondLevelKey === linkArray.companyDetails,
        href: getPathForLocale(locale, linkArray.companyDetails),
        condition: isTravelManager || isBusinessPayManager,
      },
    ],
    spending: spendingItems,
  };
};
