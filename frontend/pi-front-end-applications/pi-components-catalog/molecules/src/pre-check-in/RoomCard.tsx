import {
  Box,
  BoxProps,
  Flex,
  FlexProps,
  HeadingProps,
  Text,
  TextProps,
  StyleProps,
} from '@chakra-ui/react';
import { ChevronRight, Notification, Success } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

interface Props {
  testid: string;
  roomIndex?: number;
  room: any;
  roomCardContainerStyle?: any;
  displayIcon?: boolean;
  clickEventRequired?: boolean;
  setURLParamToReservationId?: (bookingReference: string, reservationId: string) => any;
  preCheckInStatus?: boolean;
}

const RoomCard = ({
  testid,
  roomIndex = 0,
  room,
  roomCardContainerStyle = {},
  displayIcon = true,
  clickEventRequired = true,
  setURLParamToReservationId = () => <></>,
  preCheckInStatus = false,
}: Props) => {
  const { t } = useTranslation();
  const roomNumber = (number: number) => number + 1;

  const formatText = (adults: number, children: number) => {
    const childStr = children > 1 ? 'account.dashboard.children' : 'account.dashboard.child';
    const adultStr = adults > 1 ? 'account.dashboard.adults' : 'account.dashboard.adult';

    let membersDetails = adults ? `${adults} ${t(adultStr)}` : '';
    membersDetails += children ? ` ${children} ${t(childStr)}` : '';
    return membersDetails.trim();
  };

  const clickEvent = clickEventRequired
    ? {
        onClick: () => {
          !preCheckInStatus &&
            setURLParamToReservationId(room.bookingReference, room.reservationId);
        },
      }
    : {};

  return (
    <>
      <Box
        {...boxParentStyle}
        {...roomCardContainerStyle?.boxParentStyle}
        data-testid={formatDataTestId(testid, `Wrapper-${roomNumber(roomIndex)}`)}
        {...clickEvent}
        {...(preCheckInStatus ? disabledStyle : '')}
      >
        <Flex direction={'row'}>
          <Box flex="12">
            <Flex
              {...boxesContainerStyle}
              {...roomCardContainerStyle?.boxesContainerStyle}
              data-testid={formatDataTestId(testid, `ContainerRoom-${roomNumber(roomIndex)}`)}
            >
              <Box {...firstColumnStyle} {...roomCardContainerStyle?.firstColumnStyle}>
                <Flex direction="column" align="flex-start">
                  <Text
                    {...headingStyle}
                    {...roomCardContainerStyle?.headingStyle}
                    data-testid={formatDataTestId(testid, `HeaderRoom-${roomNumber(roomIndex)}`)}
                  >
                    {t('booking.leadGuest.ForRoom').replace(
                      '[roomNumber]',
                      String(roomNumber(roomIndex))
                    )}
                  </Text>
                  <Text {...subHeadingStyle} textTransform="capitalize">
                    {`${room?.firstName} ${room?.lastName}`}
                  </Text>
                </Flex>
              </Box>
              <Box {...secondColumnStyle} {...roomCardContainerStyle?.secondColumnStyle}>
                <Text {...textStyle} {...roomCardContainerStyle?.textStyle}>
                  {`${room?.roomName} ${formatText(room?.adultsNumber, room?.childrenNumber)}`}
                </Text>
              </Box>
              <Box
                {...thirdColumnStyle}
                {...roomCardContainerStyle?.iconStyle}
                display={{ base: 'none', md: 'flex' }}
              >
                {displayIcon && <ChevronRight style={{ justifyContent: 'center' }} />}
              </Box>
            </Flex>
          </Box>
          <Box
            {...thirdColumnStyle}
            {...roomCardContainerStyle?.iconStyle}
            display={{ base: 'flex', md: 'none' }}
            flex={1}
          >
            {displayIcon && <ChevronRight style={{ justifyContent: 'center' }} />}
          </Box>
        </Flex>
      </Box>

      {!!preCheckInStatus && displayIcon && (
        <Box mb="2xl" mt="md">
          <Notification
            status="success"
            variant="success"
            svg={<Success style={{ marginTop: 'var(--chakra-space-xs)' }} />}
            prefixDataTestId={`room-checked-in-${room.reservationId}`}
            showCloseButton
            description={
              <Box {...sxStyles}>
                <Text fontWeight={'semibold'}>{t('precheckin.room.completetitle')}</Text>
                <Text>{t('precheckin.room.completemsg')}</Text>
              </Box>
            }
            wrapperStyles={{ borderRadius: 'var(--chakra-space-sm)' }}
          />
        </Box>
      )}
    </>
  );
};

export default RoomCard;

const sxStyles = {
  pt: 0,
  mt: 0,
  fontWeight: 'normal',
  lineHeight: 3,
  p: { pb: 'xs' },
  fontSize: 'md',
} as StyleProps;

const boxParentStyle = {
  border: '1px solid var(--chakra-colors-lightGrey1)',
  mb: '3xl',
  px: { mobile: 'md', md: 'xl' },
  py: { mobile: 'md', md: 'lg' },
  cursor: 'pointer',
  borderRadius: 'var(--chakra-space-sm)',
  width: '100%',
  _hover: {
    borderColor: 'var(--chakra-colors-darkGrey1)',
  },
};

const disabledStyle = {
  ...boxParentStyle,
  color: 'lightGrey1',
  opacity: 0.4,
  mb: 'sm',
  pointerEvents: 'none',
};

const boxesContainerStyle = {
  p: { mobile: 'md md', md: 'xl xl' },
  direction: { mobile: 'column', md: 'row' },
  cursor: 'pointer',
  alignItems: 'center',
  justifyContent: 'space-between',
  width: '100%',
} as FlexProps;

const firstColumnStyle = {
  w: { mobile: '100%', md: 'auto' },
  minW: '15rem',
} as BoxProps;

const secondColumnStyle = {
  w: { mobile: '100%', md: 'auto' },
  alignSelf: 'flex-end',
  pt: { mobile: 'lg' },
} as BoxProps;

const thirdColumnStyle = {
  ml: 'auto',
  w: { mobile: '10px', md: 'auto' },
  alignSelf: 'center',
  textAlign: 'right',
  justifyContent: 'flex-end',
} as BoxProps;

const headingStyle = {
  fontSize: 'xl',
  fontWeight: 'semibold',
  color: 'baseBlack',
  mb: 'md',
} as HeadingProps;

const subHeadingStyle = {
  fontSize: 'lg',
  fontWeight: 'medium',
  color: 'var(--chakra-colors-chakra-body-text)',
  textTransform: 'capitalize',
} as HeadingProps;

const textStyle = {
  color: 'var(--chakra-colors-darkGrey1)',
  fontWeight: 'sm',
  textTransform: 'capitalize',
} as TextProps;
