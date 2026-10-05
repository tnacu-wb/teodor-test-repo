import { BoxProps, Flex, Text } from '@chakra-ui/react';
import { LowestRoomRate } from '@whitbread-eos/api';
import { PencePrice } from '@whitbread-eos/atoms';

interface Props {
  data: {
    lowestRoomRate: LowestRoomRate;
    numberOfNights?: number | null;
  };
  pricePerNight?: boolean | null;
  locale: string;
  testId: string;
  labels: {
    priceFrom: string;
    perNight: string;
    nights: string;
  };
  styles: {
    containerStyles?: BoxProps;
    labelStyles?: BoxProps;
    priceStyles?: BoxProps;
    pricePerNight?: BoxProps;
  };
  isPricePerNightEnabled?: boolean;
}

export default function HotelLowestRate({
  data,
  pricePerNight,
  locale,
  labels,
  testId,
  styles,
  isPricePerNightEnabled,
}: Readonly<Props>) {
  const { lowestRoomRate, numberOfNights } = data;
  const calculatedPrice = pricePerNight
    ? lowestRoomRate.netTotal / (numberOfNights || 1)
    : lowestRoomRate.netTotal;

  return (
    <Flex {...styles.containerStyles} data-testid={testId}>
      <Text {...styles.labelStyles}>{labels.priceFrom}</Text>
      <Text {...styles.priceStyles}>
        <PencePrice
          price={calculatedPrice}
          currency={lowestRoomRate.currencyCode}
          language={locale}
          size="s"
        />
      </Text>
      {isPricePerNightEnabled && (numberOfNights as number) > 1 && (
        <Text {...styles.pricePerNight}>
          {pricePerNight ? labels.perNight : `${numberOfNights} ${labels.nights}`}
        </Text>
      )}
    </Flex>
  );
}
