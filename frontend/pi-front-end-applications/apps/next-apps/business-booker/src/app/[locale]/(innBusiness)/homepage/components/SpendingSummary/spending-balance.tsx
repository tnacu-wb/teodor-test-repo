import { LOCALES, CurrentBalanceItem, Currency } from '@whitbread-eos/api';
import { Skeleton } from '@whitbread-eos/atoms/ui';
import {
  getCountryLanguageByLocale,
  getTranslations,
  getCommonIcons,
} from '@whitbread-eos/utils/server';

import { TextWithInfoTooltip } from '~components/innBusiness/TextWithInfoTooltip';

import {
  formatAmount,
  getCurrencyCodeBasedOnCurrencySymbol,
} from '../../../spending/statements/utils/format-amount';

type Props = {
  locale: LOCALES;
  currentBalance: CurrentBalanceItem;
  baseDataTestId: string;
  isAccountSuspended: boolean;
};

export async function SpendingBalance({
  locale,
  currentBalance,
  baseDataTestId,
  isAccountSuspended,
}: Props) {
  const { language } = getCountryLanguageByLocale(locale);
  const [{ t }, icons] = await Promise.all([
    getTranslations(language, ['homepage', 'spending']),
    getCommonIcons(language),
  ]);

  return (
    <div className={balanceWrapperStyle} data-testid={`${baseDataTestId}-Balance`}>
      <div className={legendItemStyle} data-testid={`${baseDataTestId}-CreditLimitContainer`}>
        <div className={legendInnerStyle} data-testid={`${baseDataTestId}-CreditLimitLabel`}>
          <TextWithInfoTooltip
            baseDataTestId={baseDataTestId}
            locale={locale}
            mainText={t('homepage.home.innbusinessPay.creditLimit')}
            infoText={t('homepage.home.innbusinessPay.creditLimitInfo')}
            icons={icons}
          />
        </div>
        <span
          className={`${amountStyle} ${
            isAccountSuspended ? 'text-lightGrey1' : 'text-primaryColor'
          }`}
          data-testid={`${baseDataTestId}-CreditLimitAmount`}
        >
          {formatAmount(
            currentBalance?.creditLimit?.amount ?? 0,
            getCurrencyCodeBasedOnCurrencySymbol(
              currentBalance?.creditLimit?.currencySymbol ?? Currency.GBP
            ),
            locale
          )}
        </span>
      </div>
      <div className={legendItemStyle} data-testid={`${baseDataTestId}-InterimPaymentsContainer`}>
        <div className={legendInnerStyle} data-testid={`${baseDataTestId}-InterimPaymentsLabel`}>
          <TextWithInfoTooltip
            baseDataTestId={baseDataTestId}
            locale={locale}
            mainText={t('homepage.home.innbusinessPay.interimPayments')}
            infoText={t('homepage.home.innbusinessPay.interimPaymentsInfo')}
            icons={icons}
          />
        </div>
        <span
          className={`${amountStyle} ${
            isAccountSuspended ? 'text-lightGrey1' : 'text-primaryColor'
          }`}
          data-testid={`${baseDataTestId}-InterimPaymentsAmount`}
        >
          {formatAmount(
            currentBalance?.interimPayments?.amount ?? 0,
            getCurrencyCodeBasedOnCurrencySymbol(
              currentBalance?.interimPayments?.currencySymbol ?? Currency.GBP
            ),
            locale
          )}
        </span>
      </div>
      <div className={legendItemStyle} data-testid={`${baseDataTestId}-CurrentBalanceContainer`}>
        <div className={legendInnerStyle} data-testid={`${baseDataTestId}-CurrentBalanceLabel`}>
          <TextWithInfoTooltip
            baseDataTestId={baseDataTestId}
            locale={locale}
            mainText={t('spending.spending.summary.current.balance')}
            infoText={t('homepage.home.innbusinessPay.currentBalanceInfo')}
            icons={icons}
          />
        </div>
        <span
          className={`${amountStyle} ${isAccountSuspended ? 'text-lightGrey1' : ''}`}
          data-testid={`${baseDataTestId}-CurrentBalanceAmount`}
        >
          {formatAmount(
            currentBalance?.currentBalance?.amount ?? 0,
            getCurrencyCodeBasedOnCurrencySymbol(
              currentBalance?.currentBalance?.currencySymbol ?? Currency.GBP
            ),
            locale
          )}
        </span>
      </div>
    </div>
  );
}

type SkeletonProps = {
  t: (key: string) => string;
};

export function SpendingBalanceSkeleton({ t }: SkeletonProps) {
  return (
    <div className={balanceWrapperStyle}>
      <div className={legendItemStyle}>
        <div className={legendInnerStyle}>
          <span>{t('homepage.home.innbusinessPay.creditLimit')}</span>
          <Skeleton className="ml-2 h-6 w-6 rounded-full" />
        </div>
        <Skeleton className={'h-[32px] w-1/2'} />
      </div>
      <div className={legendItemStyle}>
        <div className={legendInnerStyle}>
          <span>{t('homepage.home.innbusinessPay.interimPayments')}</span>
          <Skeleton className="ml-2 h-6 w-6 rounded-full" />
        </div>
        <Skeleton className={'h-[32px] w-1/2'} />
      </div>
      <div className={legendItemStyle}>
        <div className={legendInnerStyle}>
          <span>{t('spending.spending.summary.current.balance')}</span>
          <Skeleton className="ml-2 h-6 w-6 rounded-full" />
        </div>
        <Skeleton className={'h-[32px] w-1/2'} />
      </div>
    </div>
  );
}

const balanceWrapperStyle =
  'flex flex-col space-y-4 md:flex-row md:space-y-0 mt-8 py-8 border-t-[1px] border-t-lightGrey3';
const legendItemStyle = 'flex flex-1 flex-col space-y-2 mobile:relative';
const legendInnerStyle = 'flex flex-row items-center';
const amountStyle = 'text-2xl font-semibold';
