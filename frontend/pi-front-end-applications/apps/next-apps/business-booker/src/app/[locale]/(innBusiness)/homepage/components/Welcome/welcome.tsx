import { LOCALES, BUSINESS_BOOKER_USER_ROLES, Currency } from '@whitbread-eos/api';
import { Skeleton } from '@whitbread-eos/atoms/ui';
import {
  getPathForLocale,
  getUserDetails,
  getCompanyDetails,
  ID_TOKEN_COOKIE,
  getCompanySpending,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import Link from 'next/link';

type Props = {
  parentDataTestId: string;
  locale: LOCALES;
  t: (key: string) => string;
};

export async function Welcome({ locale, parentDataTestId, t }: Props) {
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const dataTestId = `${parentDataTestId}-Welcome`;
  const userDetails = await getUserDetails(token);
  if (!userDetails || !userDetails?.companyId) {
    throw new Error();
  }

  const isTravelManager = userDetails?.business?.accessLevel === BUSINESS_BOOKER_USER_ROLES.SUPER;
  const companyDetails = await getCompanyDetails(userDetails.companyId, token, false);

  let amount, currency;
  if (isTravelManager) {
    try {
      const currentDate = new Date();
      const month = String(currentDate.getMonth() + 1).padStart(2, '0');
      const year = currentDate.getFullYear();
      const response = await getCompanySpending(token, `${month}-${year}`, `${month}-${year}`);
      amount = response?.data?.getCompanySpending?.companySpendingDtoList?.[0]?.bookingValue ?? 0;
      currency =
        response?.data?.getCompanySpending?.companySpendingDtoList?.[0]?.bookingCurrency ?? '';
    } catch (error) {
      amount = 0;
      currency = '';
    }
  }

  const finalAmount = amount?.toLocaleString(locale ?? LOCALES.EN, {
    maximumFractionDigits: 2,
    minimumFractionDigits: 2,
  });
  const finalCurrency = currency === Currency.EUR_NAME ? Currency.EUR : Currency.GBP;

  const renderSpent = () => {
    if (finalCurrency === Currency.EUR) {
      if (locale.toLowerCase() === LOCALES.DE) {
        return (
          <>
            {finalAmount} {finalCurrency} {t('homepage.home.spent')}
          </>
        );
      }

      return (
        <>
          {t('homepage.home.spent')} {finalAmount} {finalCurrency}
        </>
      );
    }

    if (locale.toLowerCase() === LOCALES.DE) {
      return (
        <>
          {finalCurrency}
          {finalAmount} {t('homepage.home.spent')}
        </>
      );
    }

    return (
      <>
        {t('homepage.home.spent')} {finalCurrency}
        {finalAmount}
      </>
    );
  };

  return (
    <>
      <h1 data-testid={`${dataTestId}-Title`} className={h1Style}>{`${t('homepage.home.welcome')} ${
        userDetails?.contactDetail?.firstName ?? ''
      }`}</h1>
      {amount && isTravelManager ? (
        <h2 data-testid={`${dataTestId}-SubTitle`} className={h2Style}>
          {companyDetails?.requestedCompany?.companyDetails?.companyName ?? ''}{' '}
          {locale.toLowerCase() === LOCALES.EN && (
            <Link
              className={amountStyle}
              href={getPathForLocale(locale.toLowerCase() as LOCALES, `spending`)}
              data-testid={`${dataTestId}-SpendingLink`}
              prefetch
            >
              {renderSpent()}
            </Link>
          )}{' '}
          {t('homepage.home.thisMonth')}{' '}
          {locale.toLowerCase() === LOCALES.DE && (
            <Link
              className={amountStyle}
              href={getPathForLocale(locale.toLowerCase() as LOCALES, `spending`)}
              data-testid={`${dataTestId}-SpendingLinkDe`}
              prefetch
            >
              {renderSpent()}
            </Link>
          )}
        </h2>
      ) : (
        <h2 data-testid={`${dataTestId}-SubTitle`} className={h2Style}>
          {t('homepage.home.welcome.your')}{' '}
          {companyDetails?.requestedCompany?.companyDetails?.companyName ?? ''}{' '}
          {t('homepage.home.welcome.account')}
        </h2>
      )}
    </>
  );
}

export function WelcomeSkeleton() {
  return (
    <>
      <Skeleton className={'h-12 w-full md:w-2/4 lg:w-1/5 mb-4'} />
      <Skeleton className={'h-8 w-full md:w-3/4 lg:w-1/3 mb-11'} />
    </>
  );
}

const h1Style =
  'text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor mobile:text-4xl mb-4';
const h2Style = 'text-[1.44rem] leading-[2rem] mb-12';
const amountStyle = 'text-secondaryColor underline underline-offset-[0.3rem]';
