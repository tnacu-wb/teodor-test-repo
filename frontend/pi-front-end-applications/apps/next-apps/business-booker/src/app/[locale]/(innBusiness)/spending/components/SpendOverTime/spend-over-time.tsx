'use client';

import { Currency, LOCALES } from '@whitbread-eos/api';
import type { CompanySpending, CustomerAccountDetails } from '@whitbread-eos/api';
import { FinancialChart, useToast } from '@whitbread-eos/atoms/ui';
import { useTranslation, cn } from '@whitbread-eos/utils';
import { getSpendOverTimeCSV } from '@whitbread-eos/utils/server';
import { format, parse, subMonths } from 'date-fns';
import { de, enGB } from 'date-fns/locale';
import { useState } from 'react';

import { DownloadButtonContent } from '~components/innBusiness/DownloadButton/download-button-content';
import { TextWithInfoTooltip } from '~components/innBusiness/TextWithInfoTooltip';

import { getLastBookingCurrency } from '../utils/spending-currency';
import { ViewListByMonth } from './components/ViewListByMonth/view-list-by-month';

const isSupportedCurrency = (value?: string | null): value is Currency =>
  value === Currency.EUR_NAME || value === Currency.GBP_NAME;

type Props = {
  locale: LOCALES;
  dataTestId: string;
  spending: CompanySpending[];
  customClass?: string;
  showDownload?: boolean;
  account?: CustomerAccountDetails | null;
  language?: string;
  token?: string;
  fromMonthYear?: string;
  toMonthYear?: string;
  icons?: Record<string, string>;
  scheme?: string;
  isAccountSuspended?: boolean;
  showSpendingTooltip?: boolean;
  defaultCurrencyCode: string;
  selectedCurrency?: string;
  tooltipText?: string;
};

export function SpendOverTime({
  dataTestId,
  locale,
  spending,
  customClass,
  showDownload = false,
  account,
  language,
  token,
  fromMonthYear,
  toMonthYear,
  icons,
  scheme,
  isAccountSuspended = false,
  showSpendingTooltip = false,
  defaultCurrencyCode,
  selectedCurrency,
  tooltipText,
}: Props) {
  const { t } = useTranslation('spending');

  const [activeIndex, setActiveIndex] = useState<number | null>(null);
  const [showToolTip, setShowTooltip] = useState<boolean>(false);
  const [tooltipPosition, setTooltipPosition] = useState<{ x: number; y: number } | null>(null);
  const dateFnsLocale = locale === LOCALES.DE ? de : enGB;
  const { toast } = useToast();
  const schemeCurrencyCode =
    scheme === 'DE' ? Currency.EUR_NAME : scheme === 'GB' ? Currency.GBP_NAME : null;
  const lastBookingCurrency = getLastBookingCurrency(
    spending,
    schemeCurrencyCode ?? defaultCurrencyCode,
    defaultCurrencyCode
  );
  const normalizedSelectedCurrency = isSupportedCurrency(selectedCurrency)
    ? selectedCurrency
    : undefined;
  const activeCurrency =
    normalizedSelectedCurrency || lastBookingCurrency || schemeCurrencyCode || defaultCurrencyCode;

  const generateSpendingData = () => {
    const months = [];

    for (let i = 11; i >= 0; i--) {
      const currentDate = subMonths(new Date(), i);
      const currentMonth = Number(format(currentDate, 'M'));
      const currentYear = Number(format(currentDate, 'yyyy'));
      const foundSpendingValue = (spending ?? []).reduce((total, spendingObj) => {
        if (
          Number(spendingObj?.month) === currentMonth &&
          Number(spendingObj?.year) === currentYear
        ) {
          const bookingCurrency = isSupportedCurrency(spendingObj?.bookingCurrency)
            ? spendingObj?.bookingCurrency
            : defaultCurrencyCode;
          if (bookingCurrency === activeCurrency) {
            return total + (spendingObj?.bookingValue ?? 0);
          }
        }
        return total;
      }, 0);

      months.push({
        month: format(currentDate, 'MMMM yyyy', { locale: dateFnsLocale }),
        spending: foundSpendingValue,
        bookingCurrency: activeCurrency,
      });
    }

    return months;
  };

  const spendingData = generateSpendingData();
  const monthsSpendings = spendingData.filter((item) => item.spending !== 0);
  const mobileHiddenStyle = !monthsSpendings.length ? spendOverTimeMobileHiddenStyle : '';

  const chartConfig = {
    desktop: {
      label: 'Desktop',
      color: '#2563eb',
    },
  };

  const formatXAxisLabel = (value: string) => {
    const parsedDate = parse(value, 'MMMM yyyy', new Date(), { locale: dateFnsLocale });

    return format(parsedDate, 'LLL yy', { locale: dateFnsLocale });
  };

  const formatYAxis = (value: number) => {
    const yValue = (value ?? 0).toLocaleString(locale, {
      maximumFractionDigits: 0,
      minimumFractionDigits: 0,
    });
    let formattedValue;
    if (activeCurrency === Currency.EUR_NAME) {
      formattedValue = value !== 0 ? `${yValue} ${Currency.EUR}` : `0 ${Currency.EUR}`;
    } else {
      formattedValue = value !== 0 ? `${Currency.GBP} ${yValue}` : `${Currency.GBP} 0`;
    }

    return formattedValue;
  };

  const formatSpendingValue = (value: number, isViewList = false, bookingCurrency?: string) => {
    const spendingValue = value.toLocaleString(locale, {
      maximumFractionDigits: 2,
      minimumFractionDigits: 2,
    });
    const viewList = isViewList ? ' ' : '';

    let formattedSpendingValue;
    if (bookingCurrency === Currency.EUR_NAME) {
      formattedSpendingValue = `${spendingValue}${viewList}${Currency.EUR}`;
    } else {
      formattedSpendingValue = `${Currency.GBP}${viewList}${spendingValue}`;
    }

    return formattedSpendingValue;
  };

  const formatTooltip = () => {
    const currentBarValue = spendingData[activeIndex ?? 0]?.spending ?? 0;
    const currentBarCurrency = spendingData[activeIndex ?? 0]?.bookingCurrency;

    return formatSpendingValue(currentBarValue, false, currentBarCurrency);
  };

  const formatLabel = () => {
    const currentMonthValue = spendingData?.[activeIndex ?? 0]?.month;
    return `${t('spending.overPeriod.spendIn')} ${formatXAxisLabel(currentMonthValue)}:`;
  };

  const onMouseEnter = (data: any, index: number) => {
    setActiveIndex(index);
    setTooltipPosition({ x: data.tooltipPosition?.x, y: data.tooltipPosition?.y });
    setShowTooltip(true);
  };

  const onMouseLeave = () => {
    setActiveIndex(null);
    setTooltipPosition(null);
    setShowTooltip(false);
  };

  const handleDownloadSpendOverTimeCSV = async () => {
    window?._satellite?.track('reportDownloaded');
    try {
      await getSpendOverTimeCSV(
        token,
        account?.accountNumber,
        fromMonthYear,
        toMonthYear,
        account?.scheme,
        language
      );
    } catch (error) {
      window?._satellite?.track('error');
      toast({
        content: t('report.error.generic'),
        variant: 'error',
      });
    }
  };

  return (
    <div
      data-testid={`${dataTestId}-Spend-Over-Time`}
      className={`${mobileHiddenStyle} ${cn(spendOverTimeStyle, customClass)}`}
    >
      <div className={titleContainerStyle}>
        <h1 data-testid={`${dataTestId}-Spend-Over-Time-Title`} className={h1Style}>
          {t('spending.reporting.graph.title')}
        </h1>

        {showSpendingTooltip && (
          <div className={tooltipContainerStyle}>
            <TextWithInfoTooltip
              baseDataTestId={dataTestId}
              mainText=""
              infoText={tooltipText || t('spending.reporting.tooltip.text')}
              icons={icons ?? {}}
              locale={locale}
            />
          </div>
        )}
        {showDownload && (
          <DownloadButtonContent
            handleDownloadCSV={handleDownloadSpendOverTimeCSV}
            altText={t('spending.download.file.text')}
            buttonText={t('spending.download.file.text')}
            testId={dataTestId}
            className={downloadButtonStyle}
            icons={icons ?? {}}
          />
        )}
      </div>
      <FinancialChart
        dataTestId={dataTestId}
        locale={locale}
        chartData={spendingData}
        chartConfig={chartConfig}
        className={chartStyle}
        axisDataKey="month"
        barDataKey="spending"
        tooltipNameKey="spending"
        tooltipPosition={tooltipPosition}
        activeIndex={activeIndex}
        showTooltip={showToolTip}
        disabled={isAccountSuspended}
        formatXAxis={formatXAxisLabel}
        formatYAxis={formatYAxis}
        formatTooltip={formatTooltip}
        formatLabel={formatLabel}
        onMouseEnter={onMouseEnter}
        onMouseLeave={onMouseLeave}
      />
      {monthsSpendings.length > 0 && (
        <ViewListByMonth
          dataTestId={dataTestId}
          spendingData={spendingData}
          formatSpendingValue={formatSpendingValue}
          monthsSpendings={monthsSpendings}
          isAccountSuspended={isAccountSuspended}
        />
      )}
    </div>
  );
}

const spendOverTimeStyle = 'w-full border border-lightGrey3 rounded-lg p-[1.5rem] mb-[3rem]';
const spendOverTimeMobileHiddenStyle = 'mobile:hidden';
const h1Style = 'mobile:mb-[1.5rem] text-[1.438rem] font-bold leading-[2rem] mb-[3rem]';
const chartStyle = 'mobile:hidden mb-[2rem]';
const downloadButtonStyle = 'border border-secondaryColor ml-auto';
const titleContainerStyle = 'flex flex-row justify-between items-start';
const tooltipContainerStyle = 'relative flex mr-auto mt-[.1rem] mobile:grow mobile:min-w-[5rem]';
