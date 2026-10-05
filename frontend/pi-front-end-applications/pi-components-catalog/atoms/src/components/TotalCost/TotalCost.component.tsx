import { Box, Divider, Flex, FlexProps, Text, TextProps } from '@chakra-ui/react';
import { BASKET_DETAILS_STATE_INITIAL_VALUE, BASKET_DETAILS_STORAGE_KEY } from '@whitbread-eos/api';
import {
  useLocalStorage,
  PromoActionsType,
  renderSanitizedHtml,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import React from 'react';

import { formatCurrency, formatDataTestId, formatPrice } from '../../utils/formatters';
import PromoTag from '../PromoTag';
import SubPrice from '../SubPrice';

interface Props {
  totalCostAmount?: number;
  currency: string;
  language: string | undefined;
  donation?: {
    description: string;
    unitPrice: number;
    totalQuantity: number;
    computedPrice: number;
  };
  selectedPaymentOption: string;
  t: (x: string, y?: { [key: string]: string }) => string;
  isCCUI?: boolean;
  taxesMessage?: string;
  promoActions?: PromoActionsType;
  cityTaxTotal?: number;
  isCityTaxBreakdownEnabled?: boolean;
}

const paymentOptions = {
  PAY_NOW: 'PAY_NOW',
  PAY_ON_ARRIVAL: 'PAY_ON_ARRIVAL',
  RESERVE_WITHOUT_CARD: 'RESERVE_WITHOUT_CARD',
  ACCOUNT_COMPANY: 'ACCOUNT_COMPANY',
};

export default function TotalCost({
  totalCostAmount,
  currency,
  language,
  donation,
  selectedPaymentOption,
  t,
  isCCUI,
  taxesMessage,
  promoActions,
  cityTaxTotal,
  isCityTaxBreakdownEnabled,
}: Readonly<Props>) {
  const [basketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );
  const getTypographyProps = useSemanticTypography();
  const baseDataTestId = 'TotalCostConfirm';

  const cityTaxStyles = {
    priceStyle: {
      fontWeight: '600',
    },
  };

  const roomPrice = {
    label: t('hoteldetails.rates.bundles.roomPrice'),
    price: totalCostAmount && cityTaxTotal ? totalCostAmount - cityTaxTotal : 0,
  };

  const cityTax = {
    label: t('booking.cityTax.label'),
    price: cityTaxTotal || 0,
  };

  const tooltipContent = {
    title: t('booking.cityTax.tooltip.title'),
    description: renderSanitizedHtml(t('booking.cityTax.tooltip.description')),
  };

  return (
    <Box
      data-testid={formatDataTestId(baseDataTestId, 'container')}
      {...totalCostWrapperStyle}
      sx={{ '@media print': { display: 'none' } }}
    >
      <Box p="lg">
        {donation && (
          <>
            <Flex {...totalCostDonationContainerStyle}>
              <Text
                {...donationTextStyle}
                data-testid={formatDataTestId(baseDataTestId, 'donationLabel')}
              >
                {t('booking.confirmation.donationMessage')}
              </Text>
              <Text
                {...donationAmountStyle}
                data-testid={formatDataTestId(baseDataTestId, 'donationAmount')}
              >
                {formatPrice(formatCurrency(currency), donation.computedPrice.toFixed(2), language)}
              </Text>
            </Flex>
            <Divider {...donationDividerStyle} />
          </>
        )}

        {isCityTaxBreakdownEnabled && !!cityTaxTotal && (
          <>
            <Text {...hotelDirectionsHeadingStyles} data-testid="paymentSummary-label">
              {t('booking.confirmation.paymentSummary')}
            </Text>

            <SubPrice
              currencyCode={currency}
              language={language}
              label={roomPrice.label}
              price={roomPrice.price}
              infoIcon={false}
              defaultLayout={true}
              cityTaxStyles={cityTaxStyles}
            />

            <SubPrice
              currencyCode={currency}
              language={language}
              label={cityTax.label}
              price={cityTax.price}
              infoIcon={true}
              defaultLayout={true}
              tooltipContent={tooltipContent}
              cityTaxStyles={cityTaxStyles}
            />
            <Divider my="md" />
          </>
        )}
        <Flex {...totalCostContainerStyle}>
          <Flex {...totalCostLabelStyle} direction="column">
            <Text
              as="b"
              data-testid={formatDataTestId(baseDataTestId, 'label')}
              {...getTypographyProps({}, totalCostLabelSemanticTypography)}
            >{`${t('booking.confirmation.totalCost')}:`}</Text>
            <Text
              data-testid={formatDataTestId(baseDataTestId, 'paymentMessage')}
              {...getTypographyProps({}, paymentMessageSemanticTypography)}
            >
              {t(renderPaymentConfirmationText(selectedPaymentOption, isCCUI))}
            </Text>
          </Flex>
          <Flex {...totalCostAmountStyle}>
            <Text
              data-testid={formatDataTestId(baseDataTestId, 'amount')}
              {...getTypographyProps({}, totalCostAmountSemanticTypography)}
            >
              {formatPrice(formatCurrency(currency), totalCostAmount?.toFixed(2), language)}
            </Text>
          </Flex>
        </Flex>
        {basketDetailsState?.rateTags && basketDetailsState?.rateTags.length > 0 && (
          <Flex data-testid="hdp_discountPromoTag" justifyContent="end" marginTop="-0.6rem">
            <PromoTag
              rateDiscountTags={basketDetailsState?.rateTags}
              customStyleName="BookingSummaryCard"
              promoActions={promoActions}
            ></PromoTag>
          </Flex>
        )}
        {!!taxesMessage?.length && (
          <Flex
            data-testid={formatDataTestId(baseDataTestId, 'includeCityTax-wrapper')}
            {...infoTextStyle}
          >
            <Text data-testid={formatDataTestId(baseDataTestId, 'TaxesMessage')}>
              {taxesMessage}
            </Text>
          </Flex>
        )}
      </Box>
    </Box>
  );
  function renderPaymentConfirmationText(paymentOption: string, isCCUI = false) {
    switch (paymentOption) {
      case paymentOptions.PAY_NOW:
        return isCCUI
          ? 'ccui.booking.confirmation.paymentTaken'
          : 'booking.confirmation.paymentTaken';
      case paymentOptions.PAY_ON_ARRIVAL:
        return isCCUI
          ? 'ccui.booking.confirmation.reservedWithCard'
          : 'booking.confirmation.reservedWithCard';
      case paymentOptions.RESERVE_WITHOUT_CARD:
        return isCCUI
          ? 'ccui.booking.confirmation.reservedWithoutCard'
          : 'booking.confirmation.reservedWithoutCard';
      case paymentOptions.ACCOUNT_COMPANY:
        return 'ccui.booking.confirmation.accountToCompany';
      default:
        return paymentOption;
    }
  }
}

const totalCostWrapperStyle = {
  mb: '2xl',
  color: 'darkGrey1',
  backgroundColor: 'lightGrey5',
} as FlexProps;

const totalCostDonationContainerStyle = {
  w: 'full',
  justifyContent: 'space-between',
  lineHeight: 'var(--chakra-lineHeights-3)',
} as FlexProps;

const donationTextStyle = {
  as: 'b',
  fontSize: 'md',
  flex: { mobile: 1, md: 'unset' },
} as TextProps;

const donationAmountStyle = {
  as: 'b',
  fontSize: 'xl',
  flex: { mobile: 1, md: 'unset' },
  display: { mobile: 'flex', md: 'inherit' },
  justifyContent: { mobile: 'flex-end', md: 'unset' },
  alignItems: { mobile: 'flex-end', md: 'center' },
} as TextProps;

const donationDividerStyle = {
  my: 'md',
  border: '1px solid var(--chakra-colors-lightGrey1)',
} as TextProps;

const totalCostContainerStyle = {
  w: 'full',
  justifyContent: 'space-between',
  lineHeight: 'var(--chakra-lineHeights-3)',
} as FlexProps;

const totalCostLabelStyle = {
  w: 'full',
  lineHeight: 'var(--chakra-lineHeights-3)',
  flex: { mobile: 1, md: 'unset' },
  maxW: { mobile: '7.5rem', sm: 'none', md: 'none' },
} as FlexProps;

const totalCostAmountStyle = {
  as: 'b',
  fontSize: '3xxl',
  lineHeight: 'var(--chakra-lineHeights-5)',
  flex: { mobile: 1, md: 'unset' },
  justifyContent: { mobile: 'flex-end', md: 'inherit' },
  alignItems: { mobile: 'flex-end', md: 'center' },
} as FlexProps;

const totalCostLabelSemanticTypography = {
  textStyle: 'body-l-emphasis',
};

const paymentMessageSemanticTypography = {
  textStyle: 'body-m-regular',
};

const totalCostAmountSemanticTypography = {
  textStyle: 'heading-m',
};

const infoTextStyle = {
  justifyContent: 'flex-end',
  fontWeight: 'medium',
  lineHeight: '2',
  color: 'darkGrey1',
  fontSize: 'sm',
} as TextProps;

const hotelDirectionsHeadingStyles = {
  fontWeight: 'semibold',
  fontSize: '2xl',
  lineHeight: '4',
  mb: 'lg',
};
