import { LOCALES, CustomerAccountDetails, RegistrationRole, Scheme } from '@whitbread-eos/api';
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
  Drawer,
  DrawerTrigger,
  DrawerContent,
  DrawerTitle,
  DrawerDescription,
  DrawerClose,
  SearchParamLink,
  WorldlineLink,
} from '@whitbread-eos/atoms/ui';
import {
  getTranslations,
  getCountryLanguageByLocale,
  formatIBAssetsUrl,
  getSearchParams,
  cn,
  getAccountList,
  ID_TOKEN_COOKIE,
  formatAccountNumber,
  getWorldlineReturnUrl,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import Image from 'next/image';

import { INN_BUSINESS_PAY_TAB_SEARCH_PARAM } from '~components/constants/constants';
import { Analytics } from '~components/innBusiness/ManageEmployeesAnalytics';

type Props = {
  locale?: LOCALES;
  className?: string;
  mobile?: boolean;
  isUserManagement?: boolean;
  accountHolderIcon?: string;
  manageEmployeeLabel?: string;
  hideDropdown?: boolean;
  prefetchAccounts?: boolean;
  filterByScheme?: Scheme;
};

const CLEAR_SEARCH_PARAMS = [
  INN_BUSINESS_PAY_TAB_SEARCH_PARAM.CANCELLED_CARDS,
  INN_BUSINESS_PAY_TAB_SEARCH_PARAM.ONLY_MY_CARDS,
  INN_BUSINESS_PAY_TAB_SEARCH_PARAM.PAGE_INDEX,
];

const BADGE_MAPPING = {
  [RegistrationRole.AccountCardHolder]: [
    'cards.cardMgmt.badge.accountHolder',
    'cards.cardMgmt.roles.options.cardHolder',
  ],
  [RegistrationRole.AccountHolder]: ['cards.cardMgmt.badge.accountHolder'],
  [RegistrationRole.CardHolder]: ['cards.cardMgmt.roles.options.cardHolder'],
  [RegistrationRole.CostCentreUser]: ['Cost centre user'],
  [RegistrationRole.FinanceUser]: ['cards.cardMgmt.roles.options.financeUser'],
  [RegistrationRole.FinanceUserCardHolder]: [
    'cards.cardMgmt.roles.options.financeUser',
    'cards.cardMgmt.roles.options.cardHolder',
  ],
};

function formatAccount(account: CustomerAccountDetails) {
  return `(*${account.accountNumber?.slice(-4)}) ${account.accountName}`;
}

export async function AccountHolder({
  locale,
  className,
  mobile,
  isUserManagement = false,
  accountHolderIcon,
  manageEmployeeLabel,
  hideDropdown = false,
  prefetchAccounts = false,
  filterByScheme = undefined,
}: Props) {
  const { language } = getCountryLanguageByLocale(locale);
  const cookieStore = await cookies();
  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  if (!token || token.trim() === '') {
    return <></>;
  }

  const [{ t }, searchParams] = await Promise.all([
    getTranslations(language, ['cards', 'icons']),
    getSearchParams(),
  ]);

  let accounts = await getAccountList(token);
  const baseDataTestId = 'AccountHolder';
  const hasMultipleAccounts = accounts && accounts.length > 1;
  const showDropdown = hasMultipleAccounts && !hideDropdown;
  accounts = filterByScheme
    ? accounts.filter((acc: CustomerAccountDetails) => acc.scheme === filterByScheme)
    : accounts;

  let currentAccount = accounts[0];
  const searchParamId = searchParams.get('account');
  if (searchParamId) {
    const account = accounts.find(
      (account: CustomerAccountDetails) => String(account.tetheredGuid) === searchParamId
    );
    if (account) {
      currentAccount = account;
    }
  }

  const renderDropdown = (children: React.ReactNode) => {
    return (
      <DropdownMenu>
        <DropdownMenuTrigger asChild>{children}</DropdownMenuTrigger>
        <DropdownMenuContent align="start" className={dropdownContentStyle}>
          {accounts.map((account: CustomerAccountDetails) => (
            <SearchParamLink
              key={account.tetheredGuid}
              name={INN_BUSINESS_PAY_TAB_SEARCH_PARAM.ACCOUNT}
              value={account.tetheredGuid}
              clear={CLEAR_SEARCH_PARAMS}
              prefetch={prefetchAccounts}
              searchParams={searchParams}
            >
              <DropdownMenuItem className={dropdownItemStyle}>
                {formatAccount(account)}
              </DropdownMenuItem>
            </SearchParamLink>
          ))}
        </DropdownMenuContent>
      </DropdownMenu>
    );
  };

  const renderDrawer = (children: React.ReactNode) => {
    return (
      <Drawer>
        <DrawerTrigger asChild>{children}</DrawerTrigger>
        <DrawerContent className={drawerContentStyle}>
          <DrawerTitle className={drawerTitleStyle}>
            {t('cards.cardMgmt.badge.accountHolder')}
          </DrawerTitle>
          <DrawerDescription className={drawerTitleStyle}>
            {t('cards.cardMgmt.badge.accountHolder')}
          </DrawerDescription>

          <div className={drawerContentInnerStyle}>
            {accounts.map((account: CustomerAccountDetails) => (
              <SearchParamLink
                key={account.tetheredGuid}
                name={INN_BUSINESS_PAY_TAB_SEARCH_PARAM.ACCOUNT}
                value={account.tetheredGuid}
                clear={CLEAR_SEARCH_PARAMS}
                searchParams={searchParams}
              >
                <DrawerClose className={drawerItemStyle}>{formatAccount(account)}</DrawerClose>
              </SearchParamLink>
            ))}
          </div>
        </DrawerContent>
      </Drawer>
    );
  };

  const renderBadges = (account: CustomerAccountDetails, testId = 'badge') => {
    const badges = Array.from(
      new Set(account.registrationRoles?.map((role) => BADGE_MAPPING[role]).flat())
    );

    return (
      <>
        {badges.map((badge: string, index: number) => (
          <span key={index} className={badgeStyle} data-testid={testId}>
            {t(badge)}
          </span>
        ))}
      </>
    );
  };

  const renderContent = () => {
    return (
      <div data-testid="AccountHolder" className={cn(containerStyle, className)}>
        <div className={nameStyle}>
          {currentAccount.accountName}
          {showDropdown && (
            <Image
              alt="arrow-down"
              src={formatIBAssetsUrl(t('icons.icon.chevron.down.purple'))}
              width={24}
              height={24}
              className={chevronStyle}
            />
          )}
        </div>
        <span className={codeStyle}>{formatAccountNumber(currentAccount.accountNumber)}</span>
        {renderBadges(currentAccount)}
      </div>
    );
  };

  const renderContentUserManagement = () => {
    const filteredAccounts = accounts.filter((account: CustomerAccountDetails) =>
      account.registrationRoles?.includes(RegistrationRole.AccountHolder)
    );

    return (
      <div
        data-testid={`${baseDataTestId}-container`}
        className={cn(containerStyleUserManagement, className)}
      >
        {filteredAccounts.map((account: CustomerAccountDetails, index: number) => (
          <div
            key={index}
            className={accountStyle}
            data-testid={`${baseDataTestId}-Account-${index + 1}`}
          >
            <span
              data-testid={`${baseDataTestId}-Account-${index + 1}-name`}
              className={nameStyleUserManagement}
            >
              {account.accountName}
            </span>
            <span data-testid={`${baseDataTestId}-Account-${index + 1}-code`} className={codeStyle}>
              {formatAccountNumber(account.accountNumber)}
            </span>
            {renderBadges(account, `${baseDataTestId}-Account-${index + 1}-badge`)}
            <div
              className={tableActionsStyle}
              data-testid={`${baseDataTestId}-Account-${index + 1}-manage-employees`}
            >
              <div className={tableActionsRightStyle}>
                <WorldlineLink
                  tetheredGuid={account.tetheredGuid}
                  className={linkStyle}
                  data-testid={`${baseDataTestId}-Account-${index + 1}-manage-employees-Link`}
                  worldlinePostUrl={
                    account.scheme === 'GB'
                      ? process.env.NEXT_PUBLIC_WORLDLINE_POST_URL
                      : process.env.NEXT_PUBLIC_WORLDLINE_POST_URL_DE
                  }
                  worldlineRequestedPage="UserList.aspx"
                  worldlineReturnUrl={getWorldlineReturnUrl(
                    locale,
                    'manage/employees?tab=innbusiness-pay'
                  )}
                  scheme={account.scheme}
                >
                  {manageEmployeeLabel}
                </WorldlineLink>
                <Image
                  alt={'Manage employees'}
                  src={formatIBAssetsUrl(accountHolderIcon)}
                  width={12}
                  height={12}
                  data-testid={`${baseDataTestId}-Account-${index + 1}-manage-employees-icon`}
                />
              </div>
            </div>
          </div>
        ))}
        <Analytics innBusinessPayAccounts={filteredAccounts.length} />
      </div>
    );
  };

  const accountNameComponent = renderContent();

  if (mobile) {
    return showDropdown ? renderDrawer(accountNameComponent) : accountNameComponent;
  }

  if (isUserManagement) {
    return renderContentUserManagement();
  }

  return showDropdown ? renderDropdown(accountNameComponent) : accountNameComponent;
}

const dropdownContentStyle = 'overflow-y-auto max-h-80';
const dropdownItemStyle = 'w-80 h-11 font-medium pl-4';
const drawerTitleStyle = 'hidden';
const drawerContentStyle = 'pb-0';
const drawerContentInnerStyle = 'overflow-y-auto max-h-80 pb-6';
const drawerItemStyle = 'font-semibold text-lg h-11 w-full pl-6 text-left';
const containerStyle = 'w-full';
const containerStyleUserManagement = 'mobile:w-full mobile:grid-cols-none grid grid-cols-2 gap-4';
const nameStyle = 'text-2xl font-bold flex';
const nameStyleUserManagement = 'text-2xl text-darkGrey1 font-bold flex';
const chevronStyle = 'ml-3';
const codeStyle = 'mt-2 inline-block mobile:block';
const badgeStyle =
  'px-2 h-6 rounded-full border border-lightGrey4 text-sm align-middle inline-block ml-2 mobile:mt-2';
const accountStyle =
  'p-4 cursor-pointer hover:shadow-variantAccountHolder rounded-sm bg-white border border-lightGrey3';
const tableActionsRightStyle = 'ml-auto flex items-center';
const tableActionsStyle =
  'mt-12 flex justify-between items-center w-full mobile:flex-col mobile:items-start';
const linkStyle = 'p-2 cursor-pointer gap-2 underline text-secondaryColor';
