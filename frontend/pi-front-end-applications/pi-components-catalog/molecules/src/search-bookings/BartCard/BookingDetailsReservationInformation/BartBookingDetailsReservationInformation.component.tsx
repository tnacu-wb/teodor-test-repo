import type { TextProps } from '@chakra-ui/react';
import { Flex, Text } from '@chakra-ui/react';
import { Badge } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import upperFirst from 'lodash/upperFirst';

export interface Props {
  bookingType: string;
  sourcePms: string;
  rateType: string;
  isBart: boolean;
  baseDataTestId: string;
  t: (id: string) => string;
}

export default function BartBookingDetailsReservationInformationComponent({
  bookingType,
  baseDataTestId,
  sourcePms,
  rateType,
  isBart,
  t,
}: Readonly<Props>) {
  const badgeColorByPMS = isBart ? 'zipSecondary' : 'primary';

  return (
    <Flex
      direction="column"
      mb="lg"
      data-testid={formatDataTestId(baseDataTestId, 'BartBookingdDetailsReservationInformation')}
    >
      <Flex {...divStyles}>
        <Badge variant="primary" badgecolor={badgeColorByPMS} {...getBadgeStyles}>
          {upperFirst(sourcePms.toLowerCase())}
        </Badge>
      </Flex>
      <Flex justifyContent="space-between" mt="lg">
        <Text {...labelStyle}>
          {t('ccui.manageBooking.typeOfBooking')}
          <Text as="span" fontWeight="normal">
            {`: ${bookingType}`}
          </Text>
        </Text>
      </Flex>
      <Flex justifyContent="space-between">
        <Text {...labelStyle}>
          {t('ccui.manageBooking.rateType')}
          <Text as="span" fontWeight="normal">
            {`: ${rateType}`}
          </Text>
        </Text>
      </Flex>
    </Flex>
  );
}

const getBadgeStyles = {
  lineHeight: 'var(--chakra-lineHeights-2)',
};
const divStyles = {
  width: '3.563rem',
};

const labelStyle = {
  fontWeight: 'semibold',
  fontSize: 'md',
  lineHeight: '3',
  color: 'darkGrey1',
  as: 'span',
} as TextProps;
