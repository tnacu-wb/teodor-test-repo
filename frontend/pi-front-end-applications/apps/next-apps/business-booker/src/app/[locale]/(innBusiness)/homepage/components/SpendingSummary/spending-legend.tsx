import { LOCALES, CurrentBalanceItem, CustomerAccountDetails, Currency } from '@whitbread-eos/api';
import { Skeleton } from '@whitbread-eos/atoms/ui';
import {
  getCountryLanguageByLocale,
  getTranslations,
  getCommonIcons,
} from '@whitbread-eos/utils/server';
import { Suspense } from 'react';

import { TextWithInfoTooltip } from '~components/innBusiness/TextWithInfoTooltip';

import {
  formatAmount,
  getCurrencyCodeBasedOnCurrencySymbol,
} from '../../../spending/statements/utils/format-amount';
import { CreditLimitExceed } from './credit-limit-exceed';
import { UpcomingSpending, UpcomingSpendingSkeleton } from './upcoming-spending';

type Props = {
  locale: LOCALES;
  currentBalance: CurrentBalanceItem;
  baseDataTestId: string;
  account: CustomerAccountDetails | null;
  isAccountSuspended: boolean;
  wlReturnUrl: string;
};

export async function SpendingLegend({
  locale,
  currentBalance,
  baseDataTestId,
  account,
  isAccountSuspended,
  wlReturnUrl,
}: Props) {
  const { language } = getCountryLanguageByLocale(locale);
  const [{ t }, icons] = await Promise.all([
    getTranslations(language, ['homepage']),
    getCommonIcons(language),
  ]);
  const remainingSpend = currentBalance?.available?.amount ?? 0;
  const totalLimit = currentBalance?.creditLimit?.amount ?? 0;
  const reachesCreditLimit = totalLimit === 0 || remainingSpend / totalLimit <= 0.1;

  return (
    <>
      <div className={legendWrapperStyle} data-testid={`${baseDataTestId}-Legend`}>
        <div className={legendItemStyle} data-testid={`${baseDataTestId}-SpentContainer`}>
          <div className={legendInnerStyle} data-testid={`${baseDataTestId}-SpentLabel`}>
            <div
              className={`w-[20px] h-[20px] bg-primaryColor mr-2 rounded-sm ${
                isAccountSuspended ? 'opacity-50' : ''
              }`}
            />
            <TextWithInfoTooltip
              baseDataTestId={baseDataTestId}
              locale={locale}
              mainText={t('homepage.home.innbusinessPay.spent')}
              infoText={t('homepage.home.innbusinessPay.spentInfo')}
              icons={icons}
            />
          </div>
          <span
            className={`${amountStyle} ${isAccountSuspended ? 'text-lightGrey1' : ''}`}
            data-testid={`${baseDataTestId}-SpentAmount`}
          >
            {formatAmount(
              currentBalance?.outstanding?.amount ?? 0,
              getCurrencyCodeBasedOnCurrencySymbol(
                currentBalance?.outstanding?.currencySymbol ?? Currency.GBP
              ),
              locale
            )}
          </span>
        </div>
        <div className={legendItemStyle} data-testid={`${baseDataTestId}-NewTransactionsContainer`}>
          <div className={legendInnerStyle} data-testid={`${baseDataTestId}-NewTransactionsLabel`}>
            <div
              className={`w-[20px] h-[20px] mr-2 ${isAccountSuspended ? 'opacity-50' : ''}`}
              style={{
                backgroundImage:
                  'repeating-linear-gradient(135deg, var(--primaryColor) 10px, var(--primaryColor) 12px, transparent 12px, transparent 16px)',
              }}
            />
            <TextWithInfoTooltip
              baseDataTestId={baseDataTestId}
              locale={locale}
              mainText={t('homepage.home.innbusinessPay.newTransactions')}
              infoText={t('homepage.home.innbusinessPay.newTransactionsInfo')}
              icons={icons}
            />
          </div>
          <span
            className={`${amountStyle} ${isAccountSuspended ? 'text-lightGrey1' : ''}`}
            data-testid={`${baseDataTestId}-NewTransactionsAmount`}
          >
            {formatAmount(
              currentBalance?.newTransactions?.amount ?? 0,
              getCurrencyCodeBasedOnCurrencySymbol(
                currentBalance?.newTransactions?.currencySymbol ?? Currency.GBP
              ),
              locale
            )}
          </span>
        </div>
        <div
          className={`${legendItemStyle} hidden md:flex`}
          data-testid={`${baseDataTestId}-RemainingContainer`}
        >
          <div className={legendInnerStyle} data-testid={`${baseDataTestId}-RemainingLabel`}>
            <div
              className={`w-[20px] h-[20px] bg-white mr-2 rounded-sm border-2 border-primaryColor ${
                isAccountSuspended ? 'opacity-50' : ''
              }`}
            />
            <TextWithInfoTooltip
              baseDataTestId={baseDataTestId}
              locale={locale}
              mainText={t('homepage.home.innbusinessPay.remainingSpend')}
              infoText={t('homepage.home.innbusinessPay.remainingSpendInfo')}
              icons={icons}
            />
          </div>
          <span
            className={`${amountStyle} ${
              remainingSpend === 0
                ? 'text-destructive'
                : isAccountSuspended
                  ? 'text-lightGrey1'
                  : 'text-primaryColor'
            }`}
            data-testid={`${baseDataTestId}-RemainingAmount`}
          >
            {isAccountSuspended
              ? t('homepage.home.innbusinessPay.notAvailable')
              : formatAmount(
                  remainingSpend,
                  getCurrencyCodeBasedOnCurrencySymbol(
                    currentBalance?.available?.currencySymbol ?? Currency.GBP
                  ),
                  locale
                )}
          </span>
        </div>
      </div>
      {account && !isAccountSuspended && (
        <Suspense fallback={<UpcomingSpendingSkeleton />}>
          <UpcomingSpending
            baseDataTestId={baseDataTestId}
            locale={locale}
            account={account}
            reachesCreditLimit={reachesCreditLimit}
            wlReturnUrl={wlReturnUrl}
          />
        </Suspense>
      )}
      <div
        className={`${legendItemStyle} flex md:hidden mt-6`}
        data-testid={`${baseDataTestId}-RemainingContainerMobile`}
      >
        <div className={legendInnerStyle} data-testid={`${baseDataTestId}-RemainingLabelMobile`}>
          <div className="w-[20px] h-[20px] bg-white mr-2 rounded-sm border-2 border-primaryColor" />
          <TextWithInfoTooltip
            baseDataTestId={baseDataTestId}
            locale={locale}
            mainText={t('homepage.home.innbusinessPay.remainingSpend')}
            infoText={t('homepage.home.innbusinessPay.remainingSpendInfo')}
            icons={icons}
          />
        </div>
        <span
          className={`${amountStyle} ${
            remainingSpend === 0
              ? 'text-destructive'
              : isAccountSuspended
                ? 'text-lightGrey1'
                : 'text-primaryColor'
          }`}
          data-testid={`${baseDataTestId}-RemainingAmountMobile`}
        >
          {isAccountSuspended
            ? t('homepage.home.innbusinessPay.notAvailable')
            : formatAmount(
                remainingSpend,
                getCurrencyCodeBasedOnCurrencySymbol(
                  currentBalance?.available?.currencySymbol ?? Currency.GBP
                ),
                locale
              )}
        </span>
      </div>
      {account && isAccountSuspended && (
        <CreditLimitExceed locale={locale} baseDataTestId={baseDataTestId} />
      )}
    </>
  );
}

type SkeletonProps = {
  t: (key: string) => string;
};

export function SpendingLegendSkeleton({ t }: SkeletonProps) {
  return (
    <div className={legendWrapperStyle}>
      <div className={legendItemStyle}>
        <div className={legendInnerStyle}>
          <div className="w-[20px] h-[20px] bg-primaryColor mr-2 rounded-sm" />
          <span>{t('homepage.home.innbusinessPay.spent')}</span>
          <Skeleton className="ml-2 h-6 w-6 rounded-full" />
        </div>
        <Skeleton className={'h-[40px] w-1/2'} />
      </div>
      <div className={legendItemStyle}>
        <div className={legendInnerStyle}>
          <div
            className="w-[20px] h-[20px] mr-2"
            style={{
              backgroundImage:
                'repeating-linear-gradient(135deg, var(--primaryColor) 10px, var(--primaryColor) 12px, transparent 12px, transparent 16px)',
            }}
          />
          <span>{t('homepage.home.innbusinessPay.newTransactions')}</span>
          <Skeleton className="ml-2 h-6 w-6 rounded-full" />
        </div>
        <Skeleton className={'h-[40px] w-1/2'} />
      </div>
      <div className={legendItemStyle}>
        <div className={legendInnerStyle}>
          <div className="w-[20px] h-[20px] bg-white mr-2 rounded-sm border-2 border-primaryColor" />
          <span>{t('homepage.home.innbusinessPay.remainingSpend')}</span>
          <Skeleton className="ml-2 h-6 w-6 rounded-full" />
        </div>
        <Skeleton className={'h-[40px] w-1/2'} />
      </div>
    </div>
  );
}

const legendWrapperStyle = 'flex flex-col space-y-4 md:flex-row md:space-y-0 mt-4';
const legendItemStyle = 'flex flex-1 flex-col space-y-2 mobile:relative';
const legendInnerStyle = 'flex flex-row items-center';
const amountStyle = 'text-4xl font-semibold';
