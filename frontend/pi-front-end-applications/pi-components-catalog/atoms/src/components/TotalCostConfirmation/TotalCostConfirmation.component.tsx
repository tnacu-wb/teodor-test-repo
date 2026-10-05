import { Box, Flex, FlexProps, Spacer, Text, TextProps } from '@chakra-ui/react';
import { formatCurrency, formatPrice } from '@whitbread-eos/utils';
import React from 'react';

interface Props {
  totalCostAmount?: number;
  currency: string;
  language: string | undefined;
  t: (x: string, y?: { [key: string]: string }) => string;
}

export default function TotalCostConfirmation({
  totalCostAmount,
  currency,
  language,
  t,
}: Readonly<Props>) {
  if (!totalCostAmount) return null;

  return (
    <Box data-testid={'confirmation-totalCost'} {...totalCostWrapperStyle}>
      <Box p="lg">
        <Flex w="full">
          <Flex direction="column">
            <Text as="b" data-testid="totalCost-label">{`${t(
              'booking.confirmation.totalCost'
            )}:`}</Text>
            <Text data-testid="totalCost-paymentMessage">
              {t('booking.confirmation.paymentTaken')}
            </Text>
          </Flex>
          <Spacer />
          <Text data-testid={'totalCost-amount'} {...totalCostAmountStyle}>
            {formatPrice(formatCurrency(currency), totalCostAmount.toFixed(2), language)}
          </Text>
        </Flex>
      </Box>
    </Box>
  );
}

const totalCostWrapperStyle = {
  mb: '2xl',
  backgroundColor: 'lightGrey5',
} as FlexProps;

const totalCostAmountStyle = {
  as: 'b',
  fontSize: '3xxl',
  color: 'darkGrey1',
} as TextProps;
