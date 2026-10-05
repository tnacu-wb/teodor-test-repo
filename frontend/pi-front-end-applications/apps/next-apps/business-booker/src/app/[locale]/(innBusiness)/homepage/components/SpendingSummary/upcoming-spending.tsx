import {
  LOCALES,
  CustomerAccountDetails,
  AccountUpcomingSpendingResponse,
  Currency,
} from '@whitbread-eos/api';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
  Skeleton,
} from '@whitbread-eos/atoms/ui';
import {
  getCountryLanguageByLocale,
  getTranslations as getTranslationServer,
  getUpcomingSpending,
  ID_TOKEN_COOKIE,
  getCommonIcons,
  formatIBAssetsUrl,
} from '@whitbread-eos/utils/server';
import { format } from 'date-fns';
import { de, enGB } from 'date-fns/locale';
import { cookies } from 'next/headers';
import Image from 'next/image';

import { TextWithInfoTooltip } from '~components/innBusiness/TextWithInfoTooltip';
import { WordlineButton } from '~components/innBusiness/WordlineButton';

import {
  formatAmount,
  getCurrencyCodeBasedOnCurrencySymbol,
} from '../../../spending/statements/utils/format-amount';

type Props = {
  baseDataTestId: string;
  locale: LOCALES;
  account: CustomerAccountDetails;
  reachesCreditLimit?: boolean;
  wlReturnUrl: string;
};

export async function UpcomingSpending({
  baseDataTestId,
  locale,
  account,
  reachesCreditLimit = false,
  wlReturnUrl,
}: Props) {
  const { language } = getCountryLanguageByLocale(locale);
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const dateLocale = locale === LOCALES.DE ? de : enGB;
  const tetheredGuid = account?.tetheredGuid ?? '';

  try {
    const [{ t }, icons, response] = await Promise.all([
      getTranslationServer(language, ['homepage']),
      getCommonIcons(language),
      getUpcomingSpending(token, account.accountNumber, tetheredGuid),
    ]);
    const alertIcon = icons?.['icon.notification.alert'];

    const upcomingSpending: AccountUpcomingSpendingResponse | null =
      response?.data?.getAccountUpcomingSpending ?? null;
    if (!upcomingSpending) {
      return <></>;
    }

    const currencySymbol =
      upcomingSpending.currency === Currency.EUR_CODE ? Currency.EUR : Currency.GBP;

    const isBefore5AM = new Date().getHours() < 5;
    const lastUpdatedDay = isBefore5AM
      ? format(new Date().setDate(new Date().getDate() - 1), 'dd LLLL yyyy', {
          locale: dateLocale,
        })
      : format(new Date(), 'dd LLLL yyyy', {
          locale: dateLocale,
        });

    const wlPostUrl =
      account?.scheme === 'DE'
        ? process?.env?.NEXT_PUBLIC_WORLDLINE_POST_URL_DE
        : process?.env?.NEXT_PUBLIC_WORLDLINE_POST_URL;

    return (
      <div
        className={`${wrapperStyle} ${reachesCreditLimit ? 'bg-tooltipError' : 'bg-alertYellow1'}`}
        data-testid={`${baseDataTestId}-UpcomingWrapper`}
      >
        <Accordion type="single" collapsible className="w-full">
          <AccordionItem value="item-1" className={'border-0'}>
            <AccordionTrigger className={'py-0 hover:no-underline'}>
              {reachesCreditLimit ? (
                <div className={'flex pl-8 relative'}>
                  {alertIcon && (
                    <Image
                      alt={''}
                      src={formatIBAssetsUrl(alertIcon)}
                      width={16}
                      height={16}
                      className="absolute top-[3px] left-0"
                    />
                  )}
                  <div className={'flex flex-col text-left'}>
                    <h3 className={titleStyle}>
                      {t('homepage.home.upcoming.spending.creditToBeReached')}
                    </h3>
                    <p className={pStyle}>
                      {t('homepage.home.upcoming.spending.creditToBeReachedDescription')} *
                    </p>
                  </div>
                </div>
              ) : (
                <div className={'flex flex-col text-left'}>
                  <h3 className={titleStyle}>{t('homepage.home.upcoming.spending.heading')}</h3>
                  <p className={pStyle}>{t('homepage.home.upcoming.spending.description')} *</p>
                </div>
              )}
            </AccordionTrigger>
            <AccordionContent className={'pb-0'}>
              <div className={cardsWrapper}>
                <div className={cardWrapper}>
                  <div className={cardInnerStyle}>
                    <TextWithInfoTooltip
                      baseDataTestId={baseDataTestId}
                      locale={locale}
                      mainText={t('homepage.home.upcoming.spending.expectedSpendToday')}
                      infoText={t('homepage.home.upcoming.spending.expectedSpendTodayInfo')}
                      icons={icons}
                    />
                  </div>
                  <p className={pStyle}>
                    {format(
                      new Date(upcomingSpending?.expectedSpendTodayDate ?? ''),
                      'dd LLL yyyy',
                      {
                        locale: dateLocale,
                      }
                    )}
                  </p>
                  <span className={amountStyle}>
                    {formatAmount(
                      upcomingSpending?.expectedSpendToday ?? 0,
                      getCurrencyCodeBasedOnCurrencySymbol(currencySymbol),
                      locale
                    )}
                  </span>
                </div>
                <div className={cardWrapper}>
                  <div className={cardInnerStyle}>
                    <TextWithInfoTooltip
                      baseDataTestId={baseDataTestId}
                      locale={locale}
                      mainText={t('homepage.home.upcoming.spending.expectedNextBilling')}
                      infoText={t('homepage.home.upcoming.spending.expectedNextBillingInfo')}
                      icons={icons}
                    />
                  </div>
                  <p className={pStyle}>{`${format(
                    new Date(upcomingSpending?.expectedNextBillingStartDate ?? ''),
                    'dd LLL yyyy',
                    { locale: dateLocale }
                  )} - ${format(
                    new Date(upcomingSpending?.expectedNextBillingEndDate ?? ''),
                    'dd LLL yyyy',
                    { locale: dateLocale }
                  )}`}</p>
                  <span className={amountStyle}>
                    {formatAmount(
                      upcomingSpending?.expectedNextBilling ?? 0,
                      getCurrencyCodeBasedOnCurrencySymbol(currencySymbol),
                      locale
                    )}
                  </span>
                </div>
                <div className={cardWrapper}>
                  <div className={cardInnerStyle}>
                    <TextWithInfoTooltip
                      baseDataTestId={baseDataTestId}
                      locale={locale}
                      mainText={t('homepage.home.upcoming.spending.expectedNextPeriod')}
                      infoText={t('homepage.home.upcoming.spending.expectedNextPeriodInfo')}
                      icons={icons}
                    />
                  </div>
                  <p className={pStyle}>{`${format(
                    new Date(upcomingSpending?.expectedNextPeriodStartDate ?? ''),
                    'dd LLL yyyy',
                    { locale: dateLocale }
                  )} - ${format(
                    new Date(upcomingSpending?.expectedNextPeriodEndDate ?? ''),
                    'dd LLL yyyy',
                    {
                      locale: dateLocale,
                    }
                  )}`}</p>
                  <span className={amountStyle}>
                    {formatAmount(
                      upcomingSpending?.expectedNextPeriod ?? 0,
                      getCurrencyCodeBasedOnCurrencySymbol(currencySymbol),
                      locale
                    )}
                  </span>
                </div>
              </div>
              <p className={pStyle}>
                {t('homepage.home.upcoming.spending.recoverCredit')}{' '}
                <WordlineButton
                  baseDataTestId={`${baseDataTestId}-Inline-Manage-credit-limit`}
                  tetheredGuid={account?.tetheredGuid ?? ''}
                  scheme={account?.scheme}
                  text={t('homepage.home.upcoming.spending.manageLimit')}
                  page={'IncreaseCreditLimit.aspx'}
                  variant="link"
                  size="md"
                  returnUrl={wlReturnUrl}
                  postUrl={wlPostUrl ?? ''}
                />{' '}
                {t('homepage.home.upcoming.spending.or')}{' '}
                <WordlineButton
                  baseDataTestId={`${baseDataTestId}-Inline-Make-a-payment`}
                  tetheredGuid={account?.tetheredGuid ?? ''}
                  scheme={account?.scheme}
                  text={t('homepage.home.upcoming.spending.makePayment')}
                  page={'CardPayment.aspx'}
                  variant="link"
                  size="md"
                  returnUrl={wlReturnUrl}
                  postUrl={wlPostUrl ?? ''}
                />{' '}
                {t('homepage.home.upcoming.spending.outstandingInvoices')}
              </p>
              <p className={`${pStyle} text-darkGrey2 mt-6`}>
                * {t('homepage.home.upcoming.spending.disclaimer')}
              </p>
              <span className={`${pStyle} text-darkGrey2`}>
                {t('homepage.home.innbusinessPay.lastUpdated')}{' '}
                {t('homepage.home.innbusinessPay.lastUpdatedHour')}, {lastUpdatedDay}.
              </span>
            </AccordionContent>
          </AccordionItem>
        </Accordion>
        <span
          className={`${arrowStyle} ${
            reachesCreditLimit ? 'border-b-tooltipError' : 'border-b-alertYellow1'
          }`}
        ></span>
      </div>
    );
  } catch {
    return <></>;
  }
}

export function UpcomingSpendingSkeleton() {
  return <Skeleton className={`h-[92px] mt-4`} data-testid={'UpcomingSpending-Skeleton'} />;
}

const wrapperStyle = 'w-full p-6 rounded-md mt-4 relative';
const titleStyle = 'font-semibold text-[1rem] leading-[1.5rem]';
const cardsWrapper = 'flex flex-col space-y-4 md:flex-row md:space-y-0 mt-6 mb-6';
const cardWrapper = 'flex flex-col flex-1';
const cardInnerStyle = 'flex flex-row items-center mobile:relative';
const amountStyle = 'text-2xl font-semibold mt-2';
const pStyle = 'text-sm font-normal';
const arrowStyle =
  'h-0 w-0 border-x-[12px] border-x-transparent border-b-[12px] absolute top-[-12px] md:left-[41%]';
