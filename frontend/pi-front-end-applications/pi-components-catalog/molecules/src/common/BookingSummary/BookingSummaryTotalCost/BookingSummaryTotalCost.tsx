import type { FlexProps, HeadingProps, TextProps } from '@chakra-ui/react';
import { Box, Flex, Heading, Spacer, Text } from '@chakra-ui/react';
import { BookingSummaryTotalCostProps } from '@whitbread-eos/api';
import { PencePrice, theme } from '@whitbread-eos/atoms';
import {
  formatCurrency,
  formatDataTestId,
  formatPrice,
  getTextStyleTypographyOverride,
  useSemanticTypography,
} from '@whitbread-eos/utils';

export interface Props extends BookingSummaryTotalCostProps {
  totalCostAmount: number;
  t: (x: string, y?: { [key: string]: string }) => string;
  language: string | undefined;
  prefixDataTestId?: string;
  newTotalCost?: number;
  currency: string;
  isDiscountApplied?: boolean;
  taxesMessage?: string;
}

export default function BookingSummaryTotalCost({
  currency,
  showVATMessage = true,
  newTotalCost,
  totalCostAmount,
  discount = 0,
  t,
  language,
  prefixDataTestId,
  previousTotalCost,
  isDiscountApplied,
  taxesMessage,
}: Readonly<Props>) {
  const baseDataTestId = formatDataTestId(prefixDataTestId, 'TotalCost');
  const getTypographyProps = useSemanticTypography();
  const totalCostPriceTypographyProps = getTypographyProps(
    totalCostPriceLegacyTypography,
    totalCostPriceSemanticTypography
  );
  const totalCostPriceResolvedTypography =
    'textStyle' in totalCostPriceTypographyProps
      ? getTextStyleTypographyOverride(totalCostPriceTypographyProps, theme.textStyles)
      : totalCostPriceTypographyProps;

  const showDiscountFields = isDiscountApplied || discount > 0;

  return (
    <Flex {...detailsWrapperStyle} data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
      <Box>
        {showDiscountFields ? (
          <Box>
            <Text
              as="p"
              {...discountSectionStyles.title}
              data-testid={formatDataTestId(baseDataTestId, 'DiscountName')}
            >
              {`${t('ccui.payment.confirmBooking.discount.title')} `}
              <Text
                as="span"
                fontWeight={700}
                data-testid={formatDataTestId(baseDataTestId, 'DiscountPrice')}
              >
                {formatPrice(formatCurrency(currency), discount.toFixed(2), language)}
              </Text>
            </Text>
            <Flex>
              <Text
                {...infoTextStyle}
                as="s"
                data-testid={formatDataTestId(baseDataTestId, 'PreviousTotalCostName')}
              >
                {t('ccui.payment.confirmBooking.discount.previousTotalCostMessage')}
              </Text>
              <Spacer />
              <Text
                as="s"
                {...previousDiscountAmount}
                data-testid={formatDataTestId(baseDataTestId, 'PreviousTotalCostPrice')}
              >
                {formatPrice(formatCurrency(currency), previousTotalCost?.toFixed(2), language)}
              </Text>
            </Flex>
          </Box>
        ) : null}

        {showDiscountFields ? (
          <Box mt="4">
            <Heading
              {...totalCostSectionStyles.title}
              as="h6"
              data-testid={formatDataTestId(baseDataTestId, 'NewTotalCostName')}
            >
              {t('ccui.payment.confirmBooking.newTotalCost')}
            </Heading>
          </Box>
        ) : (
          <Box>
            <Heading
              as="h2"
              {...totalCostPriceLayoutStyle}
              {...totalCostPriceResolvedTypography}
              data-testid={formatDataTestId(baseDataTestId, 'TotalCostPrice')}
            >
              {t('booking.summary.totalPrice')}
            </Heading>
          </Box>
        )}

        {showDiscountFields ? (
          <Text
            {...priceTextStyle}
            as="h2"
            data-testid={formatDataTestId(baseDataTestId, 'TotalCostValue')}
          >
            <PencePrice
              currency={currency}
              price={newTotalCost ?? 0}
              language={language}
              size="m"
            />
          </Text>
        ) : (
          <Text
            as="h2"
            {...costAmountLayoutStyle}
            {...getTypographyProps(costAmountLegacyTypography, costAmountSemanticTypography)}
            data-testid={formatDataTestId(baseDataTestId, 'CostAmount')}
          >
            <PencePrice currency={currency} price={totalCostAmount} language={language} size="m" />
          </Text>
        )}
        {showVATMessage && (
          <Text
            {...infoMessageLayoutStyle}
            {...getTypographyProps(infoMessageLegacyTypography, infoMessageSemanticTypography)}
            data-testid={formatDataTestId(baseDataTestId, 'VATMessage')}
          >
            {t('booking.summary.vat-included')}
          </Text>
        )}
        {!!taxesMessage?.length && (
          <Text
            {...infoMessageLayoutStyle}
            {...getTypographyProps(infoMessageLegacyTypography, infoMessageSemanticTypography)}
            data-testid={formatDataTestId(baseDataTestId, 'TaxesMessage')}
          >
            {taxesMessage}
          </Text>
        )}
      </Box>
    </Flex>
  );
}

const detailsWrapperStyle = {
  direction: 'column',
} as FlexProps;

const previousDiscountAmount = {
  lineHeight: '3',
  color: 'darkGrey1',
  fontWeight: 'semibold',
  fontFamily: 'body',
} as TextProps;

const infoTextStyle = {
  fontWeight: 'normal',
  lineHeight: '2',
  color: 'darkGrey1',
  fontSize: 'xs',
} as TextProps;

const priceTextStyle = {
  mt: { mobile: '0', sm: 'xs' },
  fontWeight: 'bold',
  fontSize: '3xl',
  lineHeight: '4',
  color: 'darkGrey1',
  fontFamily: 'body',
} as TextProps;
const discountSectionStyles = {
  title: {
    fontSize: 'xs',
    lineHeight: 2,
    fontWeight: 'normal',
    fontFamily: 'body',
    color: 'darkGrey1',
  } as TextProps,
  previousTotalCost: {
    fontSize: 'md',
    lineHeight: 3,
    fontWeight: 'normal',
    color: 'lightGrey1',
    fontFamily: 'body',
    textDecorationLine: 'line-through',
  } as TextProps,
};
const totalCostSectionStyles = {
  title: {
    fontSize: 'md',
    lineHeight: 3,
    fontWeight: 'semibold',
    color: 'darkGrey1',
    fontFamily: 'body',
  } as HeadingProps,
  hotel: {
    fontSize: 'lg',
    lineHeight: 3,
    fontWeight: 'semibold',
    color: 'darkGrey1',
    fontFamily: 'body',
  } as HeadingProps,
};

const totalCostPriceLayoutStyle = {
  color: 'darkGrey1',
} as HeadingProps;

const totalCostPriceLegacyTypography = {
  fontSize: 'md',
  lineHeight: 3,
  fontWeight: 'semibold',
  fontFamily: 'body',
} as HeadingProps;

const totalCostPriceSemanticTypography = {
  textStyle: 'body-m-emphasis',
} as HeadingProps;

const costAmountLayoutStyle = {
  mt: { mobile: '0', sm: 'xs' },
  color: 'darkGrey1',
} as TextProps;

const costAmountLegacyTypography = {
  fontWeight: 'bold',
  fontSize: '3xl',
  lineHeight: '4',
  fontFamily: 'body',
} as TextProps;

const costAmountSemanticTypography = {
  textStyle: 'heading-m',
} as TextProps;

const infoMessageLayoutStyle = {
  color: 'darkGrey1',
} as TextProps;

const infoMessageLegacyTypography = {
  fontWeight: 'normal',
  lineHeight: '2',
  fontSize: 'xs',
} as TextProps;

const infoMessageSemanticTypography = {
  textStyle: 'body-s-regular',
} as TextProps;
