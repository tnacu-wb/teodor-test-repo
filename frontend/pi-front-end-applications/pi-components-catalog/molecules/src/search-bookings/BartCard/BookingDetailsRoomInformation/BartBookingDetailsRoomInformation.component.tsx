import type { TextProps } from '@chakra-ui/react';
import { Flex, Text } from '@chakra-ui/react';
import { formatCurrency, formatPrice } from '@whitbread-eos/utils';
import { useCallback } from 'react';

export interface Props {
  roomNumber: number;
  firstName: string;
  lastName: string;
  roomName: string;
  noAdults: number;
  noKids: number;
  price: number;
  currency: string;
  language: string;
  t: (id: string) => string;
}

export default function BartBookingDetailsRoomInformationComponent({
  roomNumber,
  firstName,
  lastName,
  roomName,
  noAdults,
  noKids,
  price,
  currency,
  language,
  t,
}: Readonly<Props>) {
  const getAdultsPluralLabel = useCallback(
    (value: number) => (value === 1 ? t('account.dashboard.adult') : t('account.dashboard.adults')),
    [t]
  );

  const getChildrenPluralLabel = useCallback(
    (value: number) =>
      value === 1 ? t('booking.mealChoose.child') : t('account.dashboard.children'),
    [t]
  );

  return (
    <Flex direction="column">
      <Flex>
        <Text {...roomNumberStyle}>
          {t('booking.summary.room')} {roomNumber}
          <Text as="span" fontWeight="normal">{` ${firstName} ${lastName}`}</Text>
        </Text>
      </Flex>
      <Flex justifyContent="space-between" mb="lg">
        <Text {...roomDetailsStyle}>
          {roomName}
          {noAdults > 0 && (
            <Text as="span">{` - ${noAdults} ${getAdultsPluralLabel(noAdults)}`}</Text>
          )}
          {noKids > 0 && <Text as="span">{` - ${noKids} ${getChildrenPluralLabel(noKids)}`}</Text>}
        </Text>
        <Text {...priceStyle}>{formatPrice(formatCurrency(currency), price, language)}</Text>
      </Flex>
    </Flex>
  );
}

const priceStyle = {
  fontWeight: 'medium',
  fontSize: 'md',
  color: 'darkGrey2',
  lineHeight: '3',
} as TextProps;

const roomDetailsStyle = {
  color: 'darkGrey2',
  fontSize: 'md',
  lineHeight: '3',
  fontWeight: 'normal',
  w: 'full',
} as TextProps;

const roomNumberStyle = {
  fontWeight: 'semibold',
  fontSize: 'md',
  lineHeight: '3',
  color: 'darkGrey1',
  as: 'span',
} as TextProps;
