import {
  LOCALES,
  CustomerAccountDetails,
  PayAccountStatus,
  Language,
  Currency,
  CountryCode,
  FT_IB_PAY_PIBA_EURO,
  FT_IB_STATEMENTS_INVOICES,
  FT_IB_TRANSACTIONS,
  PayApplicationDetails,
} from '@whitbread-eos/api';
import {
  getTranslations,
  getAccountList,
  getAccountInfo,
  getWorldlineReturnUrl,
  getAccountSpending,
  getUpcomingSpending,
  getPayApplications,
  getAccountRegistrationRoleDetails,
} from '@whitbread-eos/utils/server';
import { Suspense } from 'react';

import { BenefitsBoxes } from '~components/innBusiness/BenefitsBoxes';
import { SomethingWentWrong } from '~components/innBusiness/SomethingWentWrong';
import SuspendedNotification from '~components/innBusiness/SuspendedNotification';

import {
  AccountSelector,
  AccountSelectorSkeleton,
  Applications,
  ApplicationsSkeleton,
  SpendingSummary,
  SpendingSummarySkeleton,
} from '../../../homepage/components';
import { getValidApplications } from '../../../homepage/components/Applications/applications';
import { InnBusinessPayApplySkeleton } from '../../../manage/cards/components/InnBusinessPay/inn-business-pay-apply-skeleton';
import Analytics from '../Analytics/analytics';
import { SpendOverTime } from '../SpendOverTime/spend-over-time';
import CardList from './components/CardList/card-list';
import ManageAccountButton from './components/ManageAccountButton/manage-account-button';

type Props = {
  locale: LOCALES;
  searchParamAccount?: string;
  token: string;
  isTravelManager: boolean;
  language?: Language;
  country?: string;
  toggles?: Record<string, boolean>;
  isBusinessPayManager?: boolean;
};

// Use allSettled so one WL call failing doesn't break the tab; normalize failures to null.
const getSettledValue = <T,>(result: PromiseSettledResult<T>) =>
  result.status === 'fulfilled' ? result.value : null;

const PAGE_NAME = 'Spending and Reporting: Premier Inn InnBusiness Pay';

export async function InnBusinessPay({
  locale,
  searchParamAccount = '',
  token,
  language,
  isTravelManager,
  isBusinessPayManager,
  toggles = {},
}: Props) {
  const baseDataTestId = 'InnBusinessPayTab';
  const [{ t, translations }, accounts] = await Promise.all([
    getTranslations(language, ['spending', 'icons']),
    getAccountList(token),
  ]);
  const icons = translations?.['icons'] || {};

  const account =
    accounts?.find((acc: CustomerAccountDetails) => acc.tetheredGuid === searchParamAccount) ??
    accounts?.[0] ??
    null;
  const tetheredGuid = account?.tetheredGuid ?? '';
  const { isOnlyCardHolder, isOnlyCostCenter, isCardHolderAndCostCenterUser } =
    getAccountRegistrationRoleDetails(account);
  const isCostCenterUser = isOnlyCostCenter || isCardHolderAndCostCenterUser;
  const currentDate = new Date();
  const currentMonth = String(currentDate.getMonth() + 1).padStart(2, '0');
  const currentYear = currentDate.getFullYear();
  const pastDate = new Date();
  pastDate.setMonth(pastDate.getMonth() - 11);
  const pastMonth = String(pastDate.getMonth() + 1).padStart(2, '0');
  const pastYear = pastDate.getFullYear();

  const accountInfoPromise =
    !account || isOnlyCardHolder
      ? Promise.resolve(null)
      : getAccountInfo(account.scheme, account.tetheredGuid);
  const upcomingSpendingPromise =
    account && !isOnlyCardHolder
      ? getUpcomingSpending(token, account.accountNumber, tetheredGuid)
      : Promise.resolve(null);
  const accountSpendingPromise =
    account && !isOnlyCardHolder
      ? getAccountSpending(
          token,
          `${pastMonth}-${pastYear}`,
          `${currentMonth}-${currentYear}`,
          account.accountNumber,
          tetheredGuid
        )
      : Promise.resolve(null);

  const [upcomingSpendingResult, accountInfoResult, accountSpendingResult] =
    await Promise.allSettled([upcomingSpendingPromise, accountInfoPromise, accountSpendingPromise]);

  const accountInfo = getSettledValue(accountInfoResult);
  const accountSpending = getSettledValue(accountSpendingResult);
  const hasAccountSpendingErrors = Boolean(accountSpending?.errors?.length);
  const hasAccountSpendingData = Boolean(accountSpending?.data?.getAccountSpending);
  const accountSpendingError =
    accountSpendingResult.status !== 'fulfilled' ||
    accountSpending === null ||
    (hasAccountSpendingErrors && !hasAccountSpendingData);

  const hasUpcomingSpending =
    upcomingSpendingResult.status === 'fulfilled'
      ? Boolean(upcomingSpendingResult.value?.data?.getAccountUpcomingSpending)
      : undefined;
  const accountSpendingData =
    accountSpending?.data?.getAccountSpending?.accountSpendingDtoList ?? [];

  const wlReturnUrl = getWorldlineReturnUrl(locale, 'spending?tab=innbusiness-pay');
  const wlPostUrl =
    account?.scheme === 'DE'
      ? process?.env?.NEXT_PUBLIC_WORLDLINE_POST_URL_DE
      : process?.env?.NEXT_PUBLIC_WORLDLINE_POST_URL;

  const isAccountSuspended = [PayAccountStatus.Suspended, PayAccountStatus.SuspendedHold].includes(
    (accountInfo?.status?.toLowerCase() as PayAccountStatus) ?? ''
  );

  const defaultCurrencyCode = locale === LOCALES.DE ? Currency.EUR_NAME : Currency.GBP_NAME;
  const accountCurrency = account?.scheme === 'DE' ? Currency.EUR_NAME : Currency.GBP_NAME;

  const isDeLanguage = language === CountryCode.DE;
  const showApply = toggles?.[FT_IB_PAY_PIBA_EURO] || !isDeLanguage;
  let applications: PayApplicationDetails[] = [];
  if (!account) {
    const appsResponse = await getPayApplications(token);
    applications = getValidApplications(appsResponse?.data?.getPayApplications?.applications ?? []);
  }
  const showApplications = applications.length > 0;

  return (
    <>
      <div data-testid={`${baseDataTestId}-container`}>
        <SuspendedNotification account={account} locale={locale} isShown={isAccountSuspended} />
        {account && (
          <>
            <p className={summaryDescriptionStyle}>{t('spending.spending.summary.description')}</p>
            <div className={accountWrapperStyle}>
              <AccountSelector locale={locale} parentDataTestId={baseDataTestId} />
              <ManageAccountButton
                isTravelManager={isTravelManager}
                locale={locale}
                account={account}
                wlPostUrl={wlPostUrl}
                wlReturnUrl={wlReturnUrl}
                isAccountSuspended={isAccountSuspended}
                token={token}
                isBusinessPayManager={isBusinessPayManager}
              />
            </div>
          </>
        )}
        {!account && showApplications && (
          <Suspense fallback={<ApplicationsSkeleton t={t} />}>
            <Applications locale={locale} showApply={showApply} applications={applications} />
            <span>&nbsp;</span>
          </Suspense>
        )}
        {!showApplications && !isCostCenterUser && (
          <Suspense
            key={`spending-${tetheredGuid}`}
            fallback={account ? <SpendingSummarySkeleton t={t} /> : <InnBusinessPayApplySkeleton />}
          >
            <SpendingSummary
              token={token}
              locale={locale}
              account={account}
              wlReturnUrl={wlReturnUrl}
            />
          </Suspense>
        )}
        {!account && <BenefitsBoxes locale={locale} token={token} />}
        {account && !isOnlyCardHolder && !isCostCenterUser && (
          <>
            {accountSpendingError ? (
              <div
                data-testid={`${baseDataTestId}-Spend-Over-Time`}
                className={`${spendOverTimeErrorContainerStyle} ${spendingStyle}`}
              >
                <SomethingWentWrong
                  className={spendOverTimeErrorStyle}
                  testId={`${baseDataTestId}-Spend-Over-Time-Error`}
                />
              </div>
            ) : (
              <SpendOverTime
                locale={locale}
                dataTestId={baseDataTestId}
                spending={accountSpendingData}
                customClass={spendingStyle}
                scheme={account?.scheme}
                showDownload={true}
                account={account}
                language={language}
                token={token}
                fromMonthYear={`${pastMonth}-${pastYear}`}
                toMonthYear={`${currentMonth}-${currentYear}`}
                icons={icons}
                isAccountSuspended={isAccountSuspended}
                defaultCurrencyCode={defaultCurrencyCode}
                selectedCurrency={accountCurrency}
              />
            )}
          </>
        )}
        {account && (
          <CardList
            account={account}
            wlReturnUrl={wlReturnUrl}
            wlPostUrl={wlPostUrl}
            locale={locale}
            isStatementsInvoicesOn={toggles?.[FT_IB_STATEMENTS_INVOICES]}
            isTransactionsOn={toggles?.[FT_IB_TRANSACTIONS]}
          />
        )}
        <Analytics
          pageName={PAGE_NAME}
          notifications={{
            isAccountSuspended: isAccountSuspended,
            hasUpcomingSpending: isAccountSuspended ? undefined : hasUpcomingSpending,
          }}
        />
      </div>
    </>
  );
}

type SkeletonProps = {
  t: (key: string) => string;
};

export function InnBusinessPaySkeleton({ t }: SkeletonProps) {
  return (
    <div data-testid={`IB-Pay-Skeleton`}>
      <p className={summaryDescriptionStyle}>{t('spending.spending.summary.description')}</p>
      <AccountSelectorSkeleton />
      <SpendingSummarySkeleton t={t} />
    </div>
  );
}

const summaryDescriptionStyle = 'mb-4';
const accountWrapperStyle = 'mb-4 flex flex-row justify-between items-center mobile:items-start';
const spendingStyle = 'mt-[3rem]';
const spendOverTimeErrorContainerStyle =
  'w-full border border-lightGrey3 rounded-lg p-[1.5rem] mb-[3rem]';
const spendOverTimeErrorStyle = 'py-12';
