'use client';

import { LOCALES } from '@whitbread-eos/api';
import type { CompanySpending } from '@whitbread-eos/api';
import { useEffect, useMemo, useState } from 'react';

import { SpendOverTime } from '../SpendOverTime/spend-over-time';
import { getCurrencyOrder, getMonthCurrencyTotals } from '../utils/spending-currency';
import { SpentThisMonth } from './components/SpentThisMonth/spent-this-month';

type Props = {
  locale: LOCALES;
  dataTestId: string;
  spending: CompanySpending[];
  icons: Record<string, string>;
  defaultCurrencyCode: string;
  showSpendingTooltip?: boolean;
};

export function CompanySpendingClient({
  locale,
  dataTestId,
  spending,
  icons,
  defaultCurrencyCode,
  showSpendingTooltip = true,
}: Props) {
  const currentDate = new Date();
  const currentMonth = currentDate.getMonth() + 1;
  const currentYear = currentDate.getFullYear();

  const currencyTotals = useMemo(
    () => getMonthCurrencyTotals(spending, currentMonth, currentYear, defaultCurrencyCode),
    [spending, currentMonth, currentYear, defaultCurrencyCode]
  );

  const currencyOrder = useMemo(() => getCurrencyOrder(currencyTotals), [currencyTotals]);

  const [selectedCurrency, setSelectedCurrency] = useState(currencyOrder[0] ?? defaultCurrencyCode);

  useEffect(() => {
    setSelectedCurrency(currencyOrder[0] ?? defaultCurrencyCode);
  }, [currencyOrder, defaultCurrencyCode]);

  return (
    <>
      <SpentThisMonth
        locale={locale}
        dataTestId={dataTestId}
        currencyOrder={currencyOrder}
        currencyTotals={currencyTotals}
        selectedCurrency={selectedCurrency}
        onSelectCurrency={setSelectedCurrency}
        icons={icons}
      />
      <SpendOverTime
        locale={locale}
        dataTestId={dataTestId}
        spending={spending}
        showSpendingTooltip={showSpendingTooltip}
        icons={icons}
        selectedCurrency={selectedCurrency}
        defaultCurrencyCode={defaultCurrencyCode}
      />
    </>
  );
}
