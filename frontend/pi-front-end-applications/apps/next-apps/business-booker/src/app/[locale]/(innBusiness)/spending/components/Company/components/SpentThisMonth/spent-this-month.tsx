'use client';

import { Currency, LOCALES } from '@whitbread-eos/api';
import { useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import { format } from 'date-fns';
import { de, enGB } from 'date-fns/locale';
import { Check, ChevronDown, ChevronUp } from 'lucide-react';
import Image from 'next/image';
import { useState } from 'react';

import type { CurrencyTotals } from '../../../utils/spending-currency';

interface Props {
  locale: LOCALES;
  dataTestId: string;
  currencyOrder: string[];
  currencyTotals: CurrencyTotals;
  selectedCurrency: string;
  onSelectCurrency: (currency: string) => void;
  icons?: Record<string, string>;
}

export function SpentThisMonth({
  locale,
  dataTestId,
  currencyOrder,
  currencyTotals,
  selectedCurrency,
  onSelectCurrency,
  icons,
}: Props) {
  const { t } = useTranslation('spending');
  const currentDate = new Date();
  const dateFnsLocale = locale === LOCALES.DE ? de : enGB;

  const formattedCurrentDate = format(currentDate, 'd LLL yyyy', { locale: dateFnsLocale });
  const firstDayOfMonth = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1);
  const formattedFirstDayOfMonth = format(firstDayOfMonth, 'd LLL yyyy', { locale: dateFnsLocale });
  const [isMobileOpen, setIsMobileOpen] = useState(false);

  const formatValue = (value: number, bookingCurrency?: string) => {
    const spendingValue = value.toLocaleString(locale, {
      maximumFractionDigits: 2,
      minimumFractionDigits: 2,
    });

    return bookingCurrency === Currency.EUR_NAME
      ? `${spendingValue} ${Currency.EUR}`
      : `${Currency.GBP}${spendingValue}`;
  };

  const euroFlagSrc = icons?.['icon.flag.country.eu']
    ? formatIBAssetsUrl(icons['icon.flag.country.eu'])
    : '';
  const gbFlagSrc = icons?.['icon.flag.country.gb']
    ? formatIBAssetsUrl(icons['icon.flag.country.gb'])
    : '';

  const renderFlag = (currencyCode: string, size: 'lg' | 'sm') => {
    const iconSrc = currencyCode === Currency.EUR_NAME ? euroFlagSrc : gbFlagSrc;
    if (!iconSrc) {
      return null;
    }

    const dimension = size === 'sm' ? 24 : 32;
    return (
      <Image
        src={iconSrc}
        alt={currencyCode}
        width={dimension}
        height={dimension}
        className={size === 'sm' ? flagSmallStyle : flagLargeStyle}
      />
    );
  };

  const currencyCards = currencyOrder.map((currencyCode) => {
    const isSelected = currencyCode === selectedCurrency;
    const isEuro = currencyCode === Currency.EUR_NAME;
    const totalValue = isEuro ? currencyTotals.EUR : currencyTotals.GBP;
    const flag = renderFlag(currencyCode, 'lg');
    const cardClass = isSelected ? selectedCardStyle : unselectedCardStyle;

    return (
      <button
        key={currencyCode}
        type="button"
        data-testid={`${dataTestId}-Spent-This-Month-Card-${currencyCode}`}
        className={`${currencyCardStyle} ${cardClass}`}
        onClick={() => onSelectCurrency(currencyCode)}
      >
        <div className={desktopCardIconWrapperStyle}>{flag}</div>
        <div className={desktopCardAmountStyle}>{formatValue(totalValue, currencyCode)}</div>
        <div className={desktopCardCurrencyCodeStyle}>{currencyCode}</div>
      </button>
    );
  });

  const selectedIsEuro = selectedCurrency === Currency.EUR_NAME;
  const selectedTotal = selectedIsEuro ? currencyTotals.EUR : currencyTotals.GBP;
  const selectedFlag = renderFlag(selectedCurrency, 'sm');
  const mobileTriggerStateStyle = isMobileOpen ? mobileTriggerOpenStyle : mobileTriggerClosedStyle;

  return (
    <div data-testid={`${dataTestId}-Spent-This-Month`} className={containerStyle}>
      <h1 data-testid={`${dataTestId}-Spent-This-Month-Subheading`} className={titleStyle}>
        {t('spending.reporting.subheading')}
      </h1>
      <p
        data-testid={`${dataTestId}-Spent-This-Month-Date`}
        className={dateStyle}
      >{`${formattedFirstDayOfMonth} - ${formattedCurrentDate} (${t(
        'spending.reporting.today'
      )})`}</p>
      <div className={desktopCardsContainerStyle}>{currencyCards}</div>
      <div className={mobileContainerStyle}>
        <button
          type="button"
          data-testid={`${dataTestId}-Spent-This-Month-Mobile-Trigger`}
          className={`${mobileTriggerStyle} ${mobileTriggerStateStyle}`}
          onClick={() => setIsMobileOpen((prev) => !prev)}
        >
          <div className={mobileTriggerAmountWrapperStyle}>
            <div className={mobileIconWrapperStyle}>{selectedFlag}</div>
            <div className={mobileTriggerAmountStyle}>
              {formatValue(selectedTotal, selectedCurrency)}
            </div>
          </div>
          <div className={mobileTriggerRightWrapperStyle}>
            <div className={mobileTriggerCurrencyCodeStyle}>{selectedCurrency}</div>
            <div className={mobileChevronContainerStyle}>
              {isMobileOpen ? (
                <ChevronUp className={mobileChevronIconStyle} />
              ) : (
                <ChevronDown className={mobileChevronIconStyle} />
              )}
            </div>
          </div>
        </button>
        {isMobileOpen && (
          <div className={mobileDropdownStyle}>
            {currencyOrder.map((currencyCode) => {
              const isEuro = currencyCode === Currency.EUR_NAME;
              const totalValue = isEuro ? currencyTotals.EUR : currencyTotals.GBP;
              const isSelected = currencyCode === selectedCurrency;

              return (
                <button
                  key={`mobile-${currencyCode}`}
                  type="button"
                  data-testid={`${dataTestId}-Spent-This-Month-Mobile-Option-${currencyCode}`}
                  className={`${mobileOptionBaseStyle} ${
                    isSelected ? mobileOptionSelectedStyle : mobileOptionUnselectedStyle
                  }`}
                  onClick={() => {
                    onSelectCurrency(currencyCode);
                    setIsMobileOpen(false);
                  }}
                >
                  <div className={mobileOptionContentWrapperStyle}>
                    <div className={mobileIconWrapperStyle}>{renderFlag(currencyCode, 'sm')}</div>
                    <div className={mobileOptionAmountStyle}>
                      {formatValue(totalValue, currencyCode)}
                    </div>
                  </div>
                  <div className={mobileOptionCurrencyCodeStyle}>{currencyCode}</div>
                  <div className={mobileTickContainerStyle}>
                    {isSelected && <Check className={mobileTickStyle} />}
                  </div>
                </button>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}

const containerStyle = 'mb-[3rem]';
const titleStyle = 'text-base font-semibold';
const dateStyle = 'text-[.875rem] leading-[1.25rem] mb-[.5rem]';
const flagLargeStyle = 'w-8 h-8';
const flagSmallStyle = 'w-6 h-6';
const selectedCardStyle = 'bg-tooltipInfo border-2 border-primaryColor shadow-variantTooltip';
const unselectedCardStyle = 'bg-baseWhite border border-lightGrey3';
const currencyCardStyle = 'flex-1 p-6 rounded-lg flex justify-start items-center gap-3 text-left';
const desktopCardsContainerStyle = 'w-full flex justify-start items-start gap-6 mobile:hidden';
const desktopCardIconWrapperStyle = 'w-8 h-8 flex items-center justify-center shrink-0';
const desktopCardAmountStyle =
  'flex-1 text-darkGrey1 text-xl font-bold leading-6 whitespace-nowrap';
const desktopCardCurrencyCodeStyle =
  'text-darkGrey2 text-sm font-semibold leading-4 whitespace-nowrap shrink-0';
const mobileContainerStyle = 'hidden mobile:block';
const mobileTriggerStyle =
  'self-stretch px-4 py-4 rounded border border-lightGrey3 inline-flex justify-between items-center gap-2 w-full';
const mobileTriggerClosedStyle = 'bg-lightGrey5';
const mobileTriggerOpenStyle = 'bg-lightGrey4';
const mobileTriggerAmountWrapperStyle = 'flex flex-1 justify-start items-center gap-3 min-w-0';
const mobileIconWrapperStyle = 'w-6 h-6 flex items-center justify-center shrink-0';
const mobileTriggerAmountStyle = 'text-darkGrey1 text-xl font-bold leading-6 whitespace-nowrap';
const mobileTriggerRightWrapperStyle = 'flex items-center gap-2 shrink-0';
const mobileTriggerCurrencyCodeStyle = 'text-secondaryColor text-sm font-semibold leading-4';
const mobileChevronContainerStyle = 'w-6 h-6 flex items-center justify-center';
const mobileChevronIconStyle = 'h-4 w-4';
const mobileDropdownStyle =
  'self-stretch mt-1 bg-baseWhite rounded border border-lightGrey3 shadow-variantTooltip overflow-hidden';
const mobileOptionBaseStyle =
  'w-full px-4 py-4 inline-flex justify-start items-center gap-2 border-b border-lightGrey3 last:border-b-0 text-left first:rounded-t last:rounded-b';
const mobileOptionSelectedStyle = 'bg-baseWhite';
const mobileOptionUnselectedStyle = 'bg-baseWhite';
const mobileOptionContentWrapperStyle = 'flex-1 flex items-center gap-2';
const mobileOptionAmountStyle =
  'flex-1 text-darkGrey1 text-base font-semibold leading-5 line-clamp-1';
const mobileOptionCurrencyCodeStyle = 'text-darkGrey2 text-sm font-semibold leading-4';
const mobileTickContainerStyle = 'w-6 h-6 flex items-center justify-center';
const mobileTickStyle = 'h-4 w-4 text-primaryColor';
