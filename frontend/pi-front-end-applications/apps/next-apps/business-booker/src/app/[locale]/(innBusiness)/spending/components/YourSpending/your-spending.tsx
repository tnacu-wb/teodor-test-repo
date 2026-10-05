import { LOCALES, Currency } from '@whitbread-eos/api';
import { getYourSpending } from '@whitbread-eos/utils/server';

import { SomethingWentWrong } from '~components/innBusiness/SomethingWentWrong';

import Analytics from '../../components/Analytics/analytics';
import { YourSpendingClient } from './your-spending-client';

type Props = {
  locale: LOCALES;
  token?: string;
  icons: Record<string, string>;
};

const PAGE_NAME = 'Spending and Reporting: Your spending';

export async function YourSpending({ locale, icons, token }: Props) {
  const baseDataTestId = 'YourSpendingTab';
  const defaultCurrencyCode = locale === LOCALES.DE ? Currency.EUR_NAME : Currency.GBP_NAME;

  const currentDate = new Date();
  const currentMonth = String(currentDate.getMonth() + 1).padStart(2, '0');
  const currentYear = currentDate.getFullYear();
  const pastDate = new Date();
  pastDate.setMonth(pastDate.getMonth() - 11);
  const pastMonth = String(pastDate.getMonth() + 1).padStart(2, '0');
  const pastYear = pastDate.getFullYear();

  let yourSpending: any = null;
  const employeeSpendCriteria = {
    fromMonthYear: `${pastMonth}-${pastYear}`,
    toMonthYear: `${currentMonth}-${currentYear}`,
  };
  try {
    yourSpending = await getYourSpending(token, employeeSpendCriteria);
  } catch {
    yourSpending = null;
  }

  const yourSpendingError =
    yourSpending === null ||
    (!!yourSpending?.errors?.length && !yourSpending?.data?.getEmployeeSpend);

  if (yourSpendingError) {
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
        <Analytics pageName={PAGE_NAME} validations={['Oops! Something went wrong']} />
      </div>
    );
  }

  const yourSpendingData = yourSpending?.data?.getEmployeeSpend?.employeeSpendDtoList ?? [];

  return (
    <div data-testid={`${baseDataTestId}-container`}>
      <YourSpendingClient
        locale={locale}
        dataTestId={baseDataTestId}
        spending={yourSpendingData}
        icons={icons}
        defaultCurrencyCode={defaultCurrencyCode}
        showSpendingTooltip
      />
      <Analytics pageName={PAGE_NAME} />
    </div>
  );
}

const spentThisMonthErrorContainerStyle = 'mb-[3rem]';
const spentThisMonthErrorStyle = 'py-6';
const spendOverTimeErrorContainerStyle =
  'w-full border border-lightGrey3 rounded-lg p-[1.5rem] mb-[3rem]';
const spendOverTimeErrorStyle = 'py-12';
