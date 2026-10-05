import { Box, Flex, Text, TextProps } from '@chakra-ui/react';
import {
  BOOKING_TYPE,
  DonationPackage,
  GuaranteeCodes,
  paymentOptions,
  Price,
} from '@whitbread-eos/api';
import { PromoTag } from '@whitbread-eos/atoms';
import { formatCurrency, formatPrice, useCustomLocale } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

export interface Props {
  currency: string;
  totalCost: string;
  previousTotal: string;
  balanceOutstanding: string;
  paymentOption: string;
  newTotal: string;
  donationPkg: DonationPackage | undefined;
  shouldDisplayCityTaxMessage: boolean;
  paidWithPiba: boolean;
  bookingStatus: string;
  dinnerAllowance: Price | null | undefined;
  isAmendPage?: boolean;
  rateTags?: string[];
}

export default function BookingDetailsTotalCostComponent({
  totalCost,
  previousTotal,
  balanceOutstanding,
  currency,
  paymentOption,
  newTotal,
  donationPkg,
  shouldDisplayCityTaxMessage,
  dinnerAllowance,
  paidWithPiba = false,
  bookingStatus = '',
  isAmendPage,
  rateTags,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const { language } = useCustomLocale();
  const { country } = useCustomLocale();
  const displayDonation = donationPkg && country !== 'de';
  const status =
    bookingStatus === 'FUTURE'
      ? BOOKING_TYPE.UPCOMING
      : BOOKING_TYPE[bookingStatus as keyof typeof BOOKING_TYPE];
  const displayPreAuthSpend =
    paidWithPiba && [BOOKING_TYPE.UPCOMING, BOOKING_TYPE.PAST].includes(status);
  if (paymentOption === GuaranteeCodes.PAY_ON_ARRIVAL && Number(previousTotal) !== 0) {
    return payNowTotalCost();
  }

  switch (paymentOption) {
    case paymentOptions.PAY_ON_ARRIVAL:
    case GuaranteeCodes.PAY_ON_ARRIVAL:
    case GuaranteeCodes.ACCOUNT_TO_COMPANY:
    case paymentOptions.RESERVE_WITHOUT_CARD:
    case GuaranteeCodes.RESERVE_WITHOUT_CARD: {
      return payOnArrivalTotalCost();
    }

    case paymentOptions.PAY_NOW:
    case GuaranteeCodes.PAY_NOW: {
      return payNowTotalCost();
    }
    default: {
      return <></>;
    }
  }

  function payOnArrivalTotalCost() {
    return (
      <Flex flexDir="column" mt="4xl">
        {displayDonation && <Box mb="4xl">{donation(donationPkg)}</Box>}
        {showPromoTags(rateTags ?? [])}
        <Flex justifyContent="space-between">
          <Flex flexDir="column" textAlign="start">
            <Text data-testid="pay-on-arrival-total-cost-label" {...priceTitleStyle}>
              {t('dashboard.bookings.totalCost')}
            </Text>
            {displayPreAuthSpend && (
              <Text data-testid="preauth-text">{t('dashboard.bookings.preAuthorisedSpend')}</Text>
            )}
            {dinnerAllowance && (
              <Text data-testid="dinner-allowance-text">{`${t(
                'dashboard.bookings.dinnerAllowance'
              )} - ${formatPrice(
                formatCurrency(dinnerAllowance.currency ?? ''),
                Number(dinnerAllowance.amount).toFixed(2),
                language
              )}`}</Text>
            )}
          </Flex>
          <Flex flexDir="column" textAlign="end">
            <Text data-testid="pay-on-arrival-total-cost" {...priceTitleStyle}>
              {formatPrice(formatCurrency(currency), Number(totalCost).toFixed(2), language)}
            </Text>
            <Text {...priceDescriptionStyle}>
              {t(getBalanceOutstandingSubLabel(+balanceOutstanding, isAmendPage))}
            </Text>
          </Flex>
        </Flex>
        {shouldDisplayCityTaxMessage && cityTaxMessage()}
      </Flex>
    );
  }

  function payNowTotalCost() {
    return (
      <Flex flexDir="column">
        {displayDonation && <Box mt="4xl">{donation(donationPkg)}</Box>}

        <Flex justifyContent="space-between" mt="4xl">
          <Text data-testid="previousTotal" {...priceTitleStyle}>
            {t('dashboard.bookings.previousTotal')}
          </Text>
          <Text {...priceTitleStyle} data-testid="previousTotalSumLabel">
            {formatPrice(formatCurrency(currency), Number(previousTotal).toFixed(2), language)}
          </Text>
        </Flex>
        <Flex justifyContent="space-between" mt={'xl'}>
          <Flex flexDir="column" textAlign="left">
            <Text data-testid="balanceOutstandingLabel" {...priceTitleStyle}>
              {t('dashboard.bookings.outstandingBalance')}
            </Text>
            <Text
              {...priceDescriptionStyle}
              textAlign="left"
              data-testid="balanceOutstandingDescriptionLabel"
            >
              {t(getBalanceOutstandingSubLabel(+balanceOutstanding, isAmendPage, true))}
            </Text>
          </Flex>
          <Text
            {...priceTitleStyle}
            alignSelf="flex-start"
            data-testid="balanceOutstandingSumLabel"
          >
            {formatPrice(formatCurrency(currency), Number(balanceOutstanding).toFixed(2), language)}
          </Text>
        </Flex>

        <Flex flexDir="column" mt={{ mobile: '5xl', lg: '3xl' }}>
          {showPromoTags(rateTags ?? [])}
          <Flex
            justifyContent="space-between"
            mt={displayDonation ? { mobile: 'xs', lg: '0', xl: 'xs' } : '0'}
          >
            <Flex flexDir="column" textAlign="start">
              <Text data-testid="newTotalLabel" {...priceTitleStyle}>
                {t('dashboard.bookings.newTotal')}
              </Text>
              {displayPreAuthSpend && (
                <Text data-testid="preauth-text">{t('dashboard.bookings.preAuthorisedSpend')}</Text>
              )}
            </Flex>

            <Flex flexDir="column" textAlign="end">
              <Text {...priceTitleStyle} data-testid="newTotalSumLabel">
                {formatPrice(formatCurrency(currency), Number(newTotal).toFixed(2), language)}
              </Text>
              {previousTotal > newTotal && (
                <Text {...priceDescriptionStyle} data-testid="balanceStatus">
                  {t('dashboard.bookings.balancePaid')}
                </Text>
              )}
            </Flex>
          </Flex>
        </Flex>
        {shouldDisplayCityTaxMessage && cityTaxMessage()}
      </Flex>
    );
  }

  function donation(donationPkg: DonationPackage) {
    return (
      <Flex justifyContent="space-between">
        <Text {...donationTitleStyle} data-testid="charityLabel">
          {t('account.dashboard.gosh')}
        </Text>
        <Text {...donationPriceStyle} data-testid="charitySumLabel">
          {formatPrice(
            formatCurrency(donationPkg.currency as string),
            Number(donationPkg.unitPrice).toFixed(2),
            language
          )}
        </Text>
      </Flex>
    );
  }

  function cityTaxMessage() {
    return (
      <Flex justifyContent="flex-end">
        <Text {...priceDescriptionStyle}>{t('booking.overview.includeCityTax')}</Text>
      </Flex>
    );
  }
}

function getBalanceOutstandingSubLabel(
  balanceOutstanding: number,
  isAmendPage?: boolean,
  isPayNow?: boolean
) {
  if (isAmendPage) {
    if (balanceOutstanding < 0) {
      return 'amend.refundTerms';
    }
    return 'dashboard.bookings.toPayOnArrival';
  }

  if (isPayNow) {
    return 'dashboard.bookings.toPayOnArrival';
  }
  return 'dashboard.bookings.paymentOnArrival';
}

function showPromoTags(rateTags: string[]) {
  if (!rateTags || rateTags?.length === 0) return null;
  return (
    <Flex data-testid="manage_booking_discountPromoTag" justifyContent="flex-end">
      <PromoTag rateDiscountTags={rateTags} customStyleName="rateItemStyles" />
    </Flex>
  );
}

const donationTitleStyle = {
  color: 'darkGrey2',
  fontSize: { mobile: 'md', sm: 'lg' },
  lineHeight: '3',
  fontWeight: 'normal',
  w: { mobile: '8.56rem', sm: 'auto' },
  as: 'h6',
} as TextProps;

const donationPriceStyle = {
  fontWeight: 'medium',
  fontSize: { mobile: 'md', sm: 'lg' },
  color: 'darkGrey2',
  lineHeight: '3',
  as: 'h6',
  alignSelf: 'flex-start',
} as TextProps;

const priceTitleStyle = {
  fontWeight: 'bold',
  fontSize: { mobile: 'lg', sm: 'xl' },
  lineHeight: '3',
  color: 'darkGrey1',
  as: 'h6',
} as TextProps;

const priceDescriptionStyle = {
  fontWeight: 'normal',
  fontSize: 'sm',
  lineHeight: '2',
  color: 'darkGrey2',
  textAlign: 'right',
} as TextProps;
