import type { TextProps } from '@chakra-ui/react';
import { Flex, Text } from '@chakra-ui/react';
import { BartBookingDataPackages } from '@whitbread-eos/api';
import { formatCurrency, formatPrice } from '@whitbread-eos/utils';

export interface Props {
  packages: BartBookingDataPackages[];
  language: string;
  t: (id: string) => string;
}

export default function BartBookingDetailsExtras({ packages, language, t }: Readonly<Props>) {
  return (
    <Flex direction="column">
      {!!packages.length && (
        <Flex>
          <Text {...titleStyle}>{t('ccui.manageBooking.meals')}</Text>
        </Flex>
      )}
      {packages.map((extra) => {
        return (
          <Flex justifyContent="space-between" key={extra.id}>
            <Text {...extrasNameStyle}>{extra.name}</Text>
            <Flex flexDir="column">
              <Text {...priceStyle}>
                {formatPrice(
                  formatCurrency(extra?.cost?.currencyCode ?? ''),
                  (extra?.cost?.amount ?? 0) * extra.days * extra.qty,
                  language
                )}
              </Text>
              <Text {...priceDescriptionStyle}>
                {`${t('ccui.manageBooking.priceFor')} ${extra.days}  ${t(
                  'ccui.manageBooking.nights'
                )}, ${extra.qty} ${t('ccui.manageBooking.guests')} `}
              </Text>
            </Flex>
          </Flex>
        );
      })}
    </Flex>
  );
}
const priceDescriptionStyle = {
  fontWeight: 'normal',
  fontSize: 'sm',
  lineHeight: '2',
  color: 'darkGrey2',
  textAlign: 'right',
} as TextProps;

const priceStyle = {
  fontWeight: 'medium',
  fontSize: 'md',
  color: 'darkGrey2',
  lineHeight: '3',
  alignSelf: 'flex-end',
} as TextProps;

const extrasNameStyle = {
  color: 'darkGrey2',
  fontSize: 'md',
  lineHeight: '3',
  fontWeight: 'normal',
  w: { mobile: '7.25rem', sm: 'full' },
} as TextProps;

const titleStyle = {
  fontWeight: 'semibold',
  fontSize: 'md',
  lineHeight: '3',
  color: 'darkGrey1',
  as: 'span',
} as TextProps;
