'use client';

import { LOCALES } from '@whitbread-eos/api';
import type { CompanySpending } from '@whitbread-eos/api';
import { useTranslation } from '@whitbread-eos/utils';
import { useEffect, useMemo, useState } from 'react';

import { SpentThisMonth } from '../Company/components/SpentThisMonth/spent-this-month';
import { SpendOverTime } from '../SpendOverTime/spend-over-time';
import { getCurrencyOrder, getMonthCurrencyTotals } from '../utils/spending-currency';

type Props = {
  locale: LOCALES;
  dataTestId: string;
  spending: CompanySpending[];
  icons: Record<string, string>;
  defaultCurrencyCode: string;
  showSpendingTooltip?: boolean;
};

export function YourSpendingClient({
  locale,
  dataTestId,
  spending,
  icons,
  defaultCurrencyCode,
  showSpendingTooltip = true,
}: Props) {
  const { t } = useTranslation('spending');
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
        tooltipText={t('spending.reporting.employee.tooltip.text')}
        icons={icons}
        selectedCurrency={selectedCurrency}
        defaultCurrencyCode={defaultCurrencyCode}
      />
    </>
  );
}
