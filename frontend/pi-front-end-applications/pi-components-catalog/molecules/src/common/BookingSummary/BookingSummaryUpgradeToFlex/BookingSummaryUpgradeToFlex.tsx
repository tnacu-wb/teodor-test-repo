import type { TextProps } from '@chakra-ui/react';
import { Box, Flex, Text } from '@chakra-ui/react';
import { BookingSummaryUpgradeToFlexProps } from '@whitbread-eos/api';
import { Button } from '@whitbread-eos/atoms';
import { formatCurrency, formatDataTestId, useSemanticTypography } from '@whitbread-eos/utils';

export interface Props extends BookingSummaryUpgradeToFlexProps {
  prefixDataTestId?: string;
  t: (x: string, y?: { [key: string]: string }) => string;
}

export default function BookingSummaryUpgradeToFlex({
  currency,
  upgradeToFlexCallBack,
  amount,
  t,
  prefixDataTestId,
}: Readonly<Props>) {
  const baseDataTestId = formatDataTestId(prefixDataTestId, 'UpgradeToFlex');
  const getTypographyProps = useSemanticTypography();

  return (
    <Flex flexDirection="column" data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
      <Box {...cancelBookingLayoutStyle}>
        <Text
          {...getTypographyProps(cancelBookingLegacyTypography, cancelBookingSemanticTypography)}
        >
          {`${t('booking.hotel.summary.upgradeToPremierFlexibleFirst')} `}
          <Text
            as="span"
            {...getTypographyProps(
              costForCancelMessageLegacyTypography,
              costForCancelMessageSemanticTypography
            )}
            data-testid={formatDataTestId(baseDataTestId, 'CostForCancel-Message')}
          >
            {formatCurrency(currency)}
            {`${amount.toFixed(2)}`}
          </Text>
          <Text as="span" fontWeight="semibold">
            {` ${t('booking.hotel.summary.upgradeToPremierFlexibleSecond')}`}
          </Text>
        </Text>
      </Box>
      <Button
        variant="secondary"
        size="sm"
        {...buttonStyle}
        onClick={upgradeToFlexCallBack}
        data-testid={formatDataTestId(baseDataTestId, 'Button')}
      >
        <Text {...getTypographyProps({}, buttonLabelSemanticTypography)}>
          {t('booking.hotel.summary.upgradeToPremierFlexible')}
        </Text>
      </Button>
    </Flex>
  );
}

const buttonStyle = {
  width: { mobile: '100%', sm: '64', md: '100%', xl: '100%' },
  mt: 'md',
};

const cancelBookingLayoutStyle = {
  width: { mobile: '100%', lg: '100%' },
  color: 'darkGrey1',
} as TextProps;

const cancelBookingLegacyTypography = {
  lineHeight: 2,
  fontWeight: 'normal',
  fontSize: 'sm',
} as TextProps;

const cancelBookingSemanticTypography = {
  textStyle: 'body-s-regular',
} as TextProps;

const costForCancelMessageLegacyTypography = {
  fontWeight: 'semibold',
} as TextProps;

const costForCancelMessageSemanticTypography = {
  textStyle: 'body-s-emphasis',
} as TextProps;

const buttonLabelSemanticTypography = {
  textStyle: 'label-xl',
} as TextProps;
