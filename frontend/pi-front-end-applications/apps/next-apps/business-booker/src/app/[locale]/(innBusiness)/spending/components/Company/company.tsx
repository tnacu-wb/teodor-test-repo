import { LOCALES, Currency } from '@whitbread-eos/api';
import { getCompanySpending } from '@whitbread-eos/utils/server';

import { SomethingWentWrong } from '~components/innBusiness/SomethingWentWrong';

import Analytics from '../Analytics/analytics';
import { CompanySpendingClient } from './company-spending-client';
import CardList from './components/CardList/card-list';

type Props = {
  locale: LOCALES;
  searchParamAccount?: string;
  token?: string;
  icons: Record<string, string>;
};

const PAGE_NAME = 'Spending and Reporting: Company spending';

export async function Company({ locale, token, icons }: Props) {
  const baseDataTestId = 'CompanyTab';
  const defaultCurrencyCode = locale === LOCALES.DE ? Currency.EUR_NAME : Currency.GBP_NAME;

  const currentDate = new Date();
  const currentMonth = String(currentDate.getMonth() + 1).padStart(2, '0');
  const currentYear = currentDate.getFullYear();
  const pastDate = new Date();
  pastDate.setMonth(pastDate.getMonth() - 11);
  const pastMonth = String(pastDate.getMonth() + 1).padStart(2, '0');
  const pastYear = pastDate.getFullYear();

  let companySpending: any = null;
  try {
    companySpending = await getCompanySpending(
      token,
      `${pastMonth}-${pastYear}`,
      `${currentMonth}-${currentYear}`
    );
  } catch {
    companySpending = null;
  }

  const companySpendingError =
    companySpending === null ||
    (!!companySpending?.errors?.length && !companySpending?.data?.getCompanySpending);

  if (companySpendingError) {
    return (
      <div data-testid={`${baseDataTestId}-container`}>
        <div
          data-testid={`${baseDataTestId}-Spent-This-Month`}
          className={spentThisMonthErrorContainerStyle}
        >
          <SomethingWentWrong
            className={spentThisMonthErrorStyle}
            testId={`${baseDataTestId}-Spent-This-Month-Error`}
          />
        </div>
        <div
          data-testid={`${baseDataTestId}-Spend-Over-Time`}
          className={spendOverTimeErrorContainerStyle}
        >
          <SomethingWentWrong
            className={spendOverTimeErrorStyle}
            testId={`${baseDataTestId}-Spend-Over-Time-Error`}
          />
        </div>
        <CardList icons={icons} locale={locale} />
        <Analytics pageName={PAGE_NAME} validations={['Oops! Something went wrong']} />
      </div>
    );
  }

  const companySpendingData =
    companySpending?.data?.getCompanySpending?.companySpendingDtoList ?? [];

  return (
    <div data-testid={`${baseDataTestId}-container`}>
      <CompanySpendingClient
        locale={locale}
        dataTestId={baseDataTestId}
        spending={companySpendingData}
        icons={icons}
        defaultCurrencyCode={defaultCurrencyCode}
        showSpendingTooltip
      />
      <CardList icons={icons} locale={locale} />
      <Analytics pageName={PAGE_NAME} />
    </div>
  );
}

const spentThisMonthErrorContainerStyle = 'mb-[3rem]';
const spentThisMonthErrorStyle = 'py-6';
const spendOverTimeErrorContainerStyle =
  'w-full border border-lightGrey3 rounded-lg p-[1.5rem] mb-[3rem]';
const spendOverTimeErrorStyle = 'py-12';
