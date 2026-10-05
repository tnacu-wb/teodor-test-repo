import { Flex, FlexProps, Text } from '@chakra-ui/react';
import {
  formatCurrency,
  formatDataTestId,
  formatDate,
  formatPrice,
  useSemanticTypography,
  useCustomLocale,
} from '@whitbread-eos/utils';

import ChevronDown from '../../assets/icons/ChevronDown';
import ChevronUp from '../../assets/icons/ChevronUp';
import Icon from '../Icon';

interface Props extends FlexProps {
  roomReservationStartDate: string;
  roomReservationEndDate: string;
  roomNumber: number;
  roomTotalPrice: number;
  showRoomCardInfo: boolean;
  currency: string;
  currentLang: string | undefined;
  t: (x: string, y?: { [key: string]: string }) => string;
  handleExpandCollapse: (roomNumber?: number) => void;
}

export default function RoomCardHeader({
  roomReservationStartDate,
  roomReservationEndDate,
  roomNumber,
  roomTotalPrice,
  currency,
  currentLang,
  showRoomCardInfo,
  handleExpandCollapse,
  t,
}: Readonly<Props>) {
  const baseDataTestId = formatDataTestId('RoomCardHeader', `room${roomNumber}`);
  const { language } = useCustomLocale();
  const getTypographyProps = useSemanticTypography();

  return (
    <Flex
      data-testid={baseDataTestId}
      onClick={() => handleExpandCollapse(roomNumber)}
      {...roomNrContainerStyle}
      mb={showRoomCardInfo ? '2xl' : '0'}
      pb={showRoomCardInfo ? '0' : 'lg'}
      direction={
        showRoomCardInfo
          ? {
              mobile: 'column-reverse',
              xl: 'row',
            }
          : { mobile: 'column-reverse', xl: 'row' }
      }
    >
      <Flex w="full" direction="row" justify="flex-start">
        <Text
          data-testid={formatDataTestId(baseDataTestId, 'Label')}
          {...roomNrLayoutStyles}
          {...getTypographyProps(roomNrLegacyTypography, roomNrSemanticTypography)}
        >
          {t('booking.confirmation.room').replace('[roomNumber]', roomNumber.toString())}
        </Text>
        <Text
          data-testid={formatDataTestId(baseDataTestId, 'Dates')}
          {...getTypographyProps(roomDatesLegacyTypography, roomDatesSemanticTypography)}
        >
          {formatDate(roomReservationStartDate, 'd MMM', currentLang)} -{' '}
          {formatDate(roomReservationEndDate, 'd MMM', currentLang)}
        </Text>
      </Flex>
      <Flex
        {...roomPriceStyle}
        mb={showRoomCardInfo ? { xl: '0', mobile: 'md' } : { mobile: '0' }}
        justifyContent={
          showRoomCardInfo
            ? {
                mobile: 'space-between',
                xl: 'flex-end',
              }
            : { mobile: 'space-between', xl: 'flex-end' }
        }
      >
        <Text
          data-testid={formatDataTestId(baseDataTestId, 'Total-Label')}
          mr="sm"
          {...getTypographyProps({}, roomTotalLabelSemanticTypography)}
        >
          {t('booking.hotel.summary.roomTotal')}
        </Text>
        <Flex alignItems="center">
          <Text
            data-testid={formatDataTestId(baseDataTestId, 'Total-Amount')}
            mr="sm"
            {...getTypographyProps(
              roomTotalAmountLegacyTypography,
              roomTotalAmountSemanticTypography
            )}
          >
            {formatPrice(formatCurrency(currency), roomTotalPrice.toFixed(2), language)}
          </Text>
          <Icon
            svg={
              showRoomCardInfo ? (
                <ChevronUp color="var(--chakra-colors-darkGrey2)" />
              ) : (
                <ChevronDown color="var(--chakra-colors-darkGrey2)" />
              )
            }
          />
        </Flex>
      </Flex>
    </Flex>
  );
}

const roomNrLayoutStyles = {
  mr: 'md',
  w: 'fit-content',
};

const roomNrLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: { mobile: 'xl', xs: '2xl' },
};

const roomNrSemanticTypography = {
  textStyle: 'body-l-emphasis',
};

const roomDatesLegacyTypography = {
  fontSize: { mobile: 'xl', xs: '2xl' },
};

const roomDatesSemanticTypography = {
  textStyle: 'body-l-regular',
};

const roomTotalLabelSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const roomTotalAmountLegacyTypography = {
  fontWeight: 'bold',
};

const roomTotalAmountSemanticTypography = {
  textStyle: 'body-l-emphasis',
};

const roomNrContainerStyle = {
  fontSize: '2xl',
  alignItems: 'center',
  justifyContent: 'space-between',
  cursor: 'pointer',
  p: 'lg',
} as FlexProps;

const roomPriceStyle = {
  fontSize: 'xl',
  w: 'full',
  alignItems: 'center',
};
