import { Box, Flex, Text, TextProps } from '@chakra-ui/react';
import { CurrencyAmountType, paymentOptions } from '@whitbread-eos/api';
import { formatCurrency, formatPrice, removeHtmlTags } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

export interface Props {
  totalCost: CurrencyAmountType;
  donationPkg: CurrencyAmountType;
  balanceOutstanding: CurrencyAmountType;
  previousTotal: CurrencyAmountType;
  language: string;
  rateDescription: string;
  baseDataTestId: string;
  shouldDisplayCityTaxMessage: boolean;
  paymentOption: string;
}

export default function BartBookingDetailsTotalCostComponent({
  previousTotal,
  balanceOutstanding,
  shouldDisplayCityTaxMessage,
  totalCost,
  donationPkg,
  paymentOption,
  rateDescription,
  language,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);

  const displayDonation = (donationPkg?.amount ?? 0) > 0;
  switch (paymentOption) {
    case paymentOptions.PAY_ON_ARRIVAL:
    case paymentOptions.RESERVE_WITHOUT_CARD: {
      return payOnArrivalTotalCost();
    }

    case paymentOptions.PAY_NOW: {
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
        <Flex justifyContent="space-between">
          <Flex flexDir="column" textAlign="start">
            <Text data-testid="pay-on-arrival-total-cost-label" {...priceTitleStyle}>
              {t('dashboard.bookings.totalCost')}
            </Text>
          </Flex>
          <Flex flexDir="column" textAlign="end">
            <Text data-testid="pay-on-arrival-total-cost" {...priceTitleStyle}>
              {formatPrice(
                formatCurrency(totalCost?.currencyCode ?? ''),
                Number(totalCost.amount).toFixed(2),
                language
              )}
            </Text>
            <Text {...priceDescriptionStyle}>{t('dashboard.bookings.paymentOnArrival')}</Text>
          </Flex>
        </Flex>
        {shouldDisplayCityTaxMessage && cityTaxMessage()}
        <Flex flexDir="column" textAlign="end">
          <Text {...rateDescriptionStyle}>{removeHtmlTags(rateDescription)}</Text>
        </Flex>
      </Flex>
    );
  }

  function payNowTotalCost() {
    return (
      <Flex flexDir="column">
        {displayDonation && <Box mt="4xl">{donation(donationPkg)}</Box>}

        <Flex justifyContent="space-between" mt="4xl">
          <Text data-testid="balancePaid" {...priceTitleStyle}>
            {t('dashboard.bookings.balancePaid')}
          </Text>
          <Text {...priceTitleStyle} data-testid="balancePaidSumLabel">
            {formatPrice(
              formatCurrency(previousTotal?.currencyCode ?? ''),
              Number(previousTotal.amount).toFixed(2),
              language
            )}
          </Text>
        </Flex>
        <Flex justifyContent="space-between" mt={'lg'}>
          <Flex flexDir="column" textAlign="left">
            <Text data-testid="balanceOutstandingLabel" {...priceTitleStyle}>
              {t('dashboard.bookings.outstandingBalance')}
            </Text>
            <Text
              {...priceDescriptionStyle}
              textAlign="left"
              data-testid="balanceOutstandingDescriptionLabel"
            >
              {t('dashboard.bookings.toPayOnArrival')}
            </Text>
          </Flex>
          <Text
            {...priceTitleStyle}
            alignSelf="flex-start"
            data-testid="balanceOutstandingSumLabel"
          >
            {formatPrice(
              formatCurrency(balanceOutstanding?.currencyCode ?? ''),
              Number(balanceOutstanding.amount).toFixed(2),
              language
            )}
          </Text>
        </Flex>

        <Flex flexDir="column" mt="lg">
          <Flex
            justifyContent="space-between"
            mt={displayDonation ? { mobile: 'xs', lg: '0', xl: 'xs' } : '0'}
          >
            <Flex flexDir="column" textAlign="start">
              <Text data-testid="totalCostLabel" {...priceTitleStyle}>
                {t('dashboard.bookings.totalCost')}
              </Text>
            </Flex>

            <Flex flexDir="column" textAlign="end">
              <Text {...priceTitleStyle} data-testid="newTotalSumLabel">
                {formatPrice(
                  formatCurrency(totalCost?.currencyCode ?? ''),
                  Number(totalCost.amount).toFixed(2),
                  language
                )}
              </Text>
              <Text {...priceDescriptionStyle} data-testid="balanceStatus">
                {t('dashboard.bookings.balancePaid')}
              </Text>
            </Flex>
          </Flex>
        </Flex>
        {shouldDisplayCityTaxMessage && cityTaxMessage()}
        <Flex flexDir="column" textAlign="end">
          <Text {...rateDescriptionStyle}>{removeHtmlTags(rateDescription)}</Text>
        </Flex>
      </Flex>
    );
  }

  function donation(donationPkg: CurrencyAmountType) {
    return (
      <Flex direction="column">
        <Flex>
          <Text {...titleStyle}>{t('ccui.manageBooking.extras')}</Text>
        </Flex>
        <Flex justifyContent="space-between" mb="lg">
          <Text {...donationTitleStyle} data-testid="charityLabel">
            {t('account.dashboard.gosh')}
          </Text>
          <Text {...donationPriceStyle} data-testid="charitySumLabel">
            {formatPrice(
              formatCurrency(donationPkg?.currencyCode ?? ''),
              Number(donationPkg.amount).toFixed(2),
              language
            )}
          </Text>
        </Flex>
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

const rateDescriptionStyle = {
  fontWeight: 'normal',
  fontSize: 'md',
  mt: 'sm',
  lineHeight: '3',
  color: 'darkGrey2',
  textAlign: 'right',
} as TextProps;

const titleStyle = {
  fontWeight: 'semibold',
  fontSize: 'md',
  lineHeight: '3',
  color: 'darkGrey1',
  as: 'span',
} as TextProps;
