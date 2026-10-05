import {
  LOCALES,
  CurrentBalanceItem,
  CustomerAccountDetails,
  RegistrationRole,
  Scheme,
  PayAccountStatus,
  PayApplicationDetails,
  PayApplicationStatus,
} from '@whitbread-eos/api';
import { Skeleton } from '@whitbread-eos/atoms/ui';
import {
  getCountryLanguageByLocale,
  getTranslations as getTranslationServer,
  getSpendingSummaryV2,
  getUserRolesForAccount,
  getAccountInfo,
  getPayApplications,
  getAccountRegistrationRoleDetails,
} from '@whitbread-eos/utils/server';
import { ExternalLink } from 'lucide-react';

import { NoAccountBanner } from '~components/innBusiness/NoAccountBanner';
import { RolesCheck } from '~components/innBusiness/RolesCheck';
import { SomethingWentWrong } from '~components/innBusiness/SomethingWentWrong';
import { WordlineButton } from '~components/innBusiness/WordlineButton';

import { SpendingBalance, SpendingBalanceSkeleton } from './spending-balance';
import { SpendingLegend, SpendingLegendSkeleton } from './spending-legend';
import { SpendingProgress, SpendingProgressSkeleton } from './spending-progress';

type Props = {
  token: string;
  locale: LOCALES;
  account: CustomerAccountDetails | null;
  bannerHiddenForRoles?: string[];
  wlReturnUrl: string;
  showBanner?: boolean;
  isIBPayOn?: boolean;
};

export async function SpendingSummary({
  locale,
  account,
  bannerHiddenForRoles = [],
  wlReturnUrl,
  token,
  showBanner = true,
  isIBPayOn = false,
}: Props) {
  const baseDataTestId = `SpendingSummary`;
  const { language } = getCountryLanguageByLocale(locale);
  const { isOnlyCardHolder, isOnlyCostCenter, isCardHolderAndCostCenterUser } =
    getAccountRegistrationRoleDetails(account);
  const isCostCenterUser = isOnlyCostCenter || isCardHolderAndCostCenterUser;

  if (account && (isOnlyCardHolder || isCostCenterUser)) {
    return <></>;
  }

  const getAccountInfoPromise =
    !account || isOnlyCardHolder || isCostCenterUser
      ? Promise.resolve(null)
      : getAccountInfo(account.scheme, account.tetheredGuid);
  try {
    const [{ t }, accountInfo, userRoles, payAppResponse] = await Promise.all([
      getTranslationServer(language, ['homepage', 'icons', 'spending']),
      getAccountInfoPromise,
      getUserRolesForAccount(account),
      getPayApplications(token),
    ]);
    const isAccountSuspended = [
      PayAccountStatus.Suspended,
      PayAccountStatus.SuspendedHold,
    ].includes((accountInfo?.status?.toLowerCase() as PayAccountStatus) ?? '');
    const isAccountClosed = accountInfo?.status?.toLowerCase() === PayAccountStatus.Closed;
    const wlIcon = t('icons.icon.manageEmployees-icon');
    const applications: PayApplicationDetails[] =
      payAppResponse?.data?.getPayApplications?.applications.filter(
        (application: PayApplicationDetails) =>
          application.status !== PayApplicationStatus.Cancelled
      ) ?? [];
    const hasApplications = isIBPayOn && applications.length;
    if (isAccountClosed || !showBanner) {
      return <></>;
    }

    if (!account && !hasApplications) {
      return (
        <RolesCheck
          userRoles={userRoles}
          excludedForRoles={{
            wl: [],
            bb: bannerHiddenForRoles,
          }}
        >
          <NoAccountBanner
            locale={locale}
            token={token}
            scheme={(locale === LOCALES.EN ? 'GB' : 'DE') as Scheme}
          />
        </RolesCheck>
      );
    }

    const spendingSummary = !account
      ? null
      : await getSpendingSummaryV2(token, account.scheme, account.tetheredGuid);
    if (account && spendingSummary === null) {
      return (
        <section className={containerStyle} data-testid={`${baseDataTestId}-Container`}>
          <SomethingWentWrong className="py-12" testId={`${baseDataTestId}-Error`} />
        </section>
      );
    }
    const currentBalance: CurrentBalanceItem | null =
      (spendingSummary?.data?.getAccountBalanceSummaryV2 as CurrentBalanceItem) ?? null;

    if (!currentBalance || currentBalance?.tetheredGuid !== account?.tetheredGuid) {
      if (spendingSummary?.errors?.length > 0) {
        return (
          <section className={containerStyle} data-testid={`${baseDataTestId}-Container`}>
            <SomethingWentWrong className="py-12" testId={`${baseDataTestId}-Error`} />
          </section>
        );
      }
      return <></>;
    }

    const wlPostUrl =
      account?.scheme === 'DE'
        ? process?.env?.NEXT_PUBLIC_WORLDLINE_POST_URL_DE
        : process?.env?.NEXT_PUBLIC_WORLDLINE_POST_URL;

    return (
      <RolesCheck
        userRoles={userRoles}
        excludedForRoles={{
          wl: [RegistrationRole.CardHolder],
          bb: [],
        }}
      >
        <section className={containerStyle} data-testid={`${baseDataTestId}-Container`}>
          <h2 className={h2Style} data-testid={`${baseDataTestId}-Title`}>
            {t('homepage.home.innbusinessPay.spendingSummary')}
          </h2>
          <SpendingProgress
            currentBalance={currentBalance}
            baseDataTestId={baseDataTestId}
            isAccountSuspended={isAccountSuspended}
          />
          <SpendingLegend
            locale={locale}
            currentBalance={currentBalance}
            baseDataTestId={baseDataTestId}
            account={account}
            isAccountSuspended={isAccountSuspended}
            wlReturnUrl={wlReturnUrl}
          />
          <SpendingBalance
            locale={locale}
            currentBalance={currentBalance}
            baseDataTestId={baseDataTestId}
            isAccountSuspended={isAccountSuspended}
          />
          <div className={btnWrapperStyle} data-testid={`${baseDataTestId}-ButtonsContainer`}>
            {!isAccountSuspended && (
              <WordlineButton
                baseDataTestId={`${baseDataTestId}-Manage-credit-limit`}
                tetheredGuid={account?.tetheredGuid ?? ''}
                scheme={account?.scheme}
                icon={wlIcon}
                text={t('homepage.home.innbusinessPay.manageCreditLimit')}
                page={'IncreaseCreditLimit.aspx'}
                returnUrl={wlReturnUrl}
                postUrl={wlPostUrl ?? ''}
              />
            )}
            <WordlineButton
              baseDataTestId={`${baseDataTestId}-Make-a-payment`}
              tetheredGuid={account?.tetheredGuid ?? ''}
              scheme={account?.scheme}
              icon={isAccountSuspended ? null : wlIcon}
              iconSvg={isAccountSuspended ? <ExternalLink width={20} height={20} /> : null}
              text={t('spending.spending.summary.payment')}
              page={'CardPayment.aspx'}
              returnUrl={wlReturnUrl}
              variant={isAccountSuspended ? 'default' : 'outline'}
              postUrl={wlPostUrl ?? ''}
            />
          </div>
        </section>
      </RolesCheck>
    );
  } catch {
    return (
      <section className={containerStyle} data-testid={`${baseDataTestId}-Container`}>
        <SomethingWentWrong className="py-12" testId={`${baseDataTestId}-Error`} />
      </section>
    );
  }
}

type SkeletonProps = {
  t: (key: string) => string;
};

export function SpendingSummarySkeleton({ t }: SkeletonProps) {
  return (
    <section className={containerStyle} data-testid={'SpendingSummary-Skeleton'}>
      <h2 className={h2Style}>{t('homepage.home.innbusinessPay.spendingSummary')}</h2>
      <Skeleton className={'h-6 w-full md:w-2/4 lg:w-1/5 mb-6'} />
      <SpendingProgressSkeleton />
      <SpendingLegendSkeleton t={t} />
      <SpendingBalanceSkeleton t={t} />
      <div className={btnWrapperStyle}>
        <Skeleton className={'h-[56px] w-full md:w-[260px]'} />
        <Skeleton className={'h-[56px] w-full md:w-[260px]'} />
      </div>
    </section>
  );
}

const containerStyle = 'mt-4 border-[1px] border-lightGrey3 p-6 rounded-md';
const h2Style = 'text-[1.44rem] leading-[2rem] font-bold mb-6';
const btnWrapperStyle = 'flex flex-col md:flex-row items-left gap-4';
