import { Flex, FlexProps, Text, TextProps } from '@chakra-ui/react';
import { SummaryOfPaymentsLabels, SummaryOfPaymentsType } from '@whitbread-eos/api';
import { CityTax } from '@whitbread-eos/molecules';
import { formatCurrency, formatDataTestId, formatPriceWithDecimal } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

export interface Props {
  language: string;
  baseDataTestId: string;
  currency: string;
  summaryOfPayments: SummaryOfPaymentsType;
  summaryOfPaymentsLabels: SummaryOfPaymentsLabels;
  isCityTaxEnabled: boolean;
  cityTaxTotal?: number;
  isCityTaxAmendEnabled?: boolean;
}

export const BookingSummaryCost = (props: Props) => {
  const {
    language,
    baseDataTestId,
    currency,
    summaryOfPayments,
    summaryOfPaymentsLabels,
    isCityTaxEnabled,
    isCityTaxAmendEnabled,
    cityTaxTotal,
  } = props;

  const { t } = useTranslation();
  const showDonation = summaryOfPayments.charitable > 0;
  const shouldRenderPayOnArrival =
    summaryOfPayments.balancePaid == 0 && summaryOfPayments.payOnArrival > 0;
  const shouldRenderPIBA = summaryOfPayments.balanceAuthorised > 0;
  const showNonRefundable = summaryOfPayments.nonRefundable > 0;
  const showRefund = summaryOfPayments.refund < 0;
  const showArrivalPrice = summaryOfPayments.payOnArrival >= 0;

  const generatePriceLabel = (priceLabel: string, styles?: TextProps) => {
    return <Text {...styles}>{priceLabel}</Text>;
  };

  const generatePrice = (price: number, styles?: TextProps) => {
    return (
      <Text data-testid={formatDataTestId(baseDataTestId, `${price}`)} {...styles}>
        {(price && price === 0 && price.toFixed(2)) ||
          formatPriceWithDecimal(language, formatCurrency(currency), price, true)}
      </Text>
    );
  };

  const renderPayNowPrices = () => {
    return (
      <Flex {...costWrapperStyles}>
        {showDonation && (
          <Flex {...flexWrapperStyles}>
            <Flex {...labelWrapperStyles}>
              {generatePriceLabel(summaryOfPaymentsLabels.donation, {
                ...labelTextStyles,
              })}
            </Flex>
            <Flex {...priceWrapperStyles}>
              {generatePrice(summaryOfPayments.charitable, { ...labelTextStyles })}
            </Flex>
          </Flex>
        )}

        {showNonRefundable && (
          <Flex {...flexWrapperStyles}>
            <Flex {...labelWrapperStyles}>
              {generatePriceLabel(summaryOfPaymentsLabels.nonRefundable, { ...labelTextStyles })}
            </Flex>
            <Flex {...priceWrapperStyles}>
              {generatePrice(summaryOfPayments.nonRefundable, { ...labelTextStyles })}
            </Flex>
          </Flex>
        )}

        {showRefund && (
          <Flex {...flexWrapperStyles}>
            <Flex {...refundStyles}>
              {generatePriceLabel(summaryOfPaymentsLabels.refund, {
                ...labelTextStyles,
                mb: '0',
              })}
              {generatePriceLabel(summaryOfPaymentsLabels.refundTerms, {
                ...labelTextStyles,
                fontSize: 'sm',
                lineHeight: '2',
              })}
            </Flex>
            <Flex {...priceWrapperStyles}>
              {generatePrice(summaryOfPayments.refund, {
                ...labelTextStyles,
              })}
            </Flex>
          </Flex>
        )}

        <Flex {...flexWrapperStyles}>
          <Flex {...labelWrapperStyles}>
            {generatePriceLabel(summaryOfPaymentsLabels.balancePaid, { ...labelTextStyles })}
          </Flex>
          <Flex {...priceWrapperStyles}>
            {generatePrice(summaryOfPayments.balancePaid, { ...labelTextStyles })}
          </Flex>
        </Flex>

        {showArrivalPrice && (
          <Flex {...flexWrapperStyles}>
            <Flex {...labelWrapperStyles}>
              {generatePriceLabel(summaryOfPaymentsLabels.additionalAmount, {
                ...labelTextStyles,
              })}
            </Flex>
            <Flex {...priceWrapperStyles}>
              {generatePrice(summaryOfPayments.payOnArrival, { ...labelTextStyles })}
            </Flex>
          </Flex>
        )}

        <Flex {...flexWrapperStyles}>
          <Flex {...labelWrapperStyles}>
            {generatePriceLabel(summaryOfPaymentsLabels.totalCost, {
              ...labelTextStyles,
              ...totalPriceTextStyles,
            })}
          </Flex>
          <Flex {...priceWrapperStyles}>
            {generatePrice(summaryOfPayments.totalCost, {
              ...labelTextStyles,
              ...totalPriceTextStyles,
            })}
          </Flex>
        </Flex>
      </Flex>
    );
  };

  const renderPayOnArrivalPrices = () => {
    return (
      <Flex {...costWrapperStyles}>
        {showDonation && (
          <Flex {...flexWrapperStyles}>
            <Flex {...labelWrapperStyles}>
              {generatePriceLabel(summaryOfPaymentsLabels.donation, {
                ...labelTextStyles,
              })}
            </Flex>
            <Flex {...priceWrapperStyles}>
              {generatePrice(summaryOfPayments.charitable, { ...labelTextStyles })}
            </Flex>
          </Flex>
        )}

        {showNonRefundable && (
          <Flex {...flexWrapperStyles}>
            <Flex {...labelWrapperStyles}>
              {generatePriceLabel(summaryOfPaymentsLabels.nonRefundable, { ...labelTextStyles })}
            </Flex>
            <Flex {...priceWrapperStyles}>
              {generatePrice(summaryOfPayments.nonRefundable, { ...labelTextStyles })}
            </Flex>
          </Flex>
        )}

        <Flex {...flexWrapperStyles}>
          <Flex {...labelWrapperStyles}>
            {generatePriceLabel(summaryOfPaymentsLabels.payOnArrival, {
              ...labelTextStyles,
              ...totalPriceTextStyles,
            })}
          </Flex>
          <Flex {...priceWrapperStyles}>
            {generatePrice(summaryOfPayments.payOnArrival, {
              ...labelTextStyles,
              ...totalPriceTextStyles,
            })}
          </Flex>
        </Flex>
      </Flex>
    );
  };

  const renderPIBAPrices = () => {
    return (
      <Flex {...costWrapperStyles}>
        {showDonation && (
          <Flex {...flexWrapperStyles}>
            <Flex {...labelWrapperStyles}>
              {generatePriceLabel(summaryOfPaymentsLabels.donation, {
                ...labelTextStyles,
              })}
            </Flex>
            <Flex {...priceWrapperStyles}>
              {generatePrice(summaryOfPayments.charitable, { ...labelTextStyles })}
            </Flex>
          </Flex>
        )}

        {showNonRefundable && (
          <Flex {...flexWrapperStyles}>
            <Flex {...labelWrapperStyles}>
              {generatePriceLabel(summaryOfPaymentsLabels.nonRefundable, { ...labelTextStyles })}
            </Flex>
            <Flex {...priceWrapperStyles}>
              {generatePrice(summaryOfPayments.nonRefundable, { ...labelTextStyles })}
            </Flex>
          </Flex>
        )}

        <Flex {...flexWrapperStyles}>
          <Flex {...labelWrapperStyles} {...balanceAuthorisedLabelStyles}>
            {generatePriceLabel(summaryOfPaymentsLabels.balanceAuthorised, {
              ...labelTextStyles,
              ...totalPriceTextStyles,
            })}
          </Flex>
          <Flex {...priceWrapperStyles}>
            {generatePrice(summaryOfPayments.balanceAuthorised, {
              ...labelTextStyles,
              ...totalPriceTextStyles,
            })}
          </Flex>
        </Flex>
      </Flex>
    );
  };

  const pibaOrPayNowPrices = shouldRenderPIBA ? renderPIBAPrices() : renderPayNowPrices();
  return (
    <Flex {...containerStyles} data-testid="amend-booking-summary-of-payments">
      {isCityTaxAmendEnabled && isCityTaxEnabled && cityTaxTotal && cityTaxTotal > 0 && (
        <CityTax
          cityTaxTotal={cityTaxTotal}
          baseDataTestId={baseDataTestId}
          language={language}
          currency={currency}
        />
      )}
      {shouldRenderPayOnArrival ? renderPayOnArrivalPrices() : pibaOrPayNowPrices}
      {isCityTaxEnabled && (
        <Text data-testid="city-tax-info-message" align="right" {...cityTaxInfoMessageStyles}>
          {t('hoteldetails.rates.cityTaxAndCharges')}
        </Text>
      )}
    </Flex>
  );
};

const containerStyles = {
  direction: 'column',
  justifyContent: 'flex-end',
  mt: 'lg',
} as FlexProps;

const costWrapperStyles = {
  width: '100%',
  direction: 'column',
  alignItems: 'end',
  justifyContent: 'space-between',
  mr: '0',
} as FlexProps;

const flexWrapperStyles = {
  width: '100%',
  justifyContent: 'flex-end',
} as FlexProps;

const labelWrapperStyles = {
  justifyContent: 'flex-end',
  marginRight: 'xs',
} as FlexProps;

const priceWrapperStyles = {
  justifyContent: 'flex-end',
  minWidth: '6rem',
} as FlexProps;

const refundStyles = {
  ...labelWrapperStyles,
  direction: 'column',
} as FlexProps;

const labelTextStyles = {
  lineHeight: {
    mobile: '2',
    sm: '3',
  },
  fontSize: {
    mobile: 'sm',
    md: 'md',
  },
  color: 'darkGrey1',
  as: 'h6',
  fontWeight: 'normal',
  mb: {
    mobile: 'sm',
    sm: 'md',
  },
  textAlign: 'right',
} as TextProps;

const totalPriceTextStyles = {
  fontSize: 'xl',
  lineHeight: '3',
  marginBottom: { mobile: '0', sm: '0' },
} as TextProps;

const balanceAuthorisedLabelStyles = {
  whiteSpace: { xl: 'nowrap' },
} as TextProps;

const cityTaxInfoMessageStyles = {
  fontWeight: 'var(--chakra-fontWeights-normal)',
  lineHeight: 'var(--chakra-lineHeights-2)',
  color: 'var(--chakra-colors-darkGrey1)',
  fontSize: 'var(--chakra-fontSizes-xxs)',
  mb: 'md',
};
