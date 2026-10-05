import { RateNotifications, RoomSelectionRateCard } from '..';
import { Box, Divider, Flex, FlexProps, Text, TextProps } from '@chakra-ui/react';
import {
  AcceptedRoomCodes,
  Currency,
  HIAEMroomType,
  HIAvailabilityRates,
  HIRateClassification,
  HIRoomPriceBreakdown,
  HIRoomType,
  HIStandardRoomType,
  ROOM_CODES,
  RoomTypeLabels,
  SearchRoomCodes,
  StandardRoomType,
  UserChoice,
} from '@whitbread-eos/api';
import {
  Accessible,
  ChevronDown,
  ChevronUp,
  BedTwin,
  DoubleBed,
  Dropdown,
  DropdownOption,
  FamilyRoom,
  OneAdult,
  SingleBed,
  Icon,
} from '@whitbread-eos/atoms';
import {
  formatCurrency,
  formatDataTestId,
  formatPrice,
  getNightsNumber,
  renderSanitizedHtml,
  swapKeysAndValues,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

import { RateNotificationsProps } from '../Notifications/RateNotifications/RateNotifications.component';

interface Props extends RateNotificationsProps {
  data: {
    hotelAvailability: HIAvailabilityRates;
    ratesInformation: HIAEMroomType[];
    rateClassifications: HIRateClassification[];
  };
  activeRate: string;
  room: HIRoomType;
  userChoice: UserChoice[];
  onHandleChange: (e: any) => void;
  onHandleClick: (option: string | string[]) => void;
  openRoomNumber: number;
  setOpenRoomNumber: (roomNumber: number) => void;
  price?: { currency: string; amount: number };
}

export default function BedDropdown({
  data,
  activeRate,
  room,
  userChoice,
  onHandleClick,
  onHandleChange,
  openRoomNumber,
  setOpenRoomNumber,
  currentClassRoomTypes,
  isNonSilentSubstituNotificPerRoomClassEnabled,
  brand,
  roomTypeInformationResponse,
  hasAccessibleRoom,
  accessibilityInfo,
  cot,
  specialRoomLimitMessage,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const { roomNumber, rooms, adults, children } = room;
  const price = formatPrice(
    formatCurrency(rooms[0].roomPriceBreakdown?.currencyCode),
    rooms[0].roomPriceBreakdown?.totalNetAmount
  );

  const { hotelAvailability, ratesInformation } = data;
  const activeRoomChoice = userChoice[(roomNumber ?? 1) - 1];
  const isOpen = openRoomNumber === roomNumber;
  const matchedActiveRate = hotelAvailability?.hotelAvailability?.roomRates?.find(
    (rate) => rate.ratePlanCode === activeRate
  );
  const roomsNumber = matchedActiveRate?.roomTypes.length ?? 1;
  const matchedActiveRoom = matchedActiveRate?.roomTypes?.find(
    (room) => room.roomNumber === openRoomNumber
  );

  const roomClassesWithPrices = matchedActiveRoom?.rooms
    ?.filter((room) => room.roomType === activeRoomChoice?.roomType.code)
    ?.reduce<{ roomClass: HIAEMroomType; roomPriceBreakdown: HIRoomPriceBreakdown | undefined }[]>(
      (acc, room) => {
        const roomClass = ratesInformation?.find((rate) =>
          rate.roomTypeCode.includes(room.pmsRoomType)
        );
        if (roomClass && !acc.some((entry) => entry.roomClass.roomLabel === roomClass.roomLabel)) {
          acc.push({ roomClass, roomPriceBreakdown: room.roomPriceBreakdown });
        }
        return acc;
      },
      []
    );

  const activePmsRoomTypeLabel = ratesInformation?.find((rate) =>
    rate.roomTypeCode.includes(activeRoomChoice?.pmsRoomType)
  )?.roomLabel;

  const typeLabels: RoomTypeLabels = {
    single: StandardRoomType.SB,
    double: StandardRoomType.DB,
    accessible: StandardRoomType.DIS,
    twin: StandardRoomType.TWIN,
    family: StandardRoomType.FAM,
  };
  const roomCodes = swapKeysAndValues(ROOM_CODES as any);
  const nightsNumber = getNightsNumber(
    hotelAvailability.hotelAvailability.startDate,
    hotelAvailability.hotelAvailability.endDate
  );
  const totalPeople = adults + children;
  const baseDataTestId = `BedDropdown-Room${roomNumber}`;

  return (
    <Flex
      {...roomWrapperStyle}
      margin={{ mobile: isOpen ? 'null' : 'md', sm: 'inherit' }}
      marginTop={{ mobile: 'inherit', sm: 'md' }}
      gap="md"
      background={isOpen ? '#E5F2F4' : '#F8F8F8'}
      borderTop={
        isOpen
          ? '1px solid var(--chakra-colors-primary)'
          : '1px solid var(--chakra-colors-darkGrey1)'
      }
      data-testid={formatDataTestId(baseDataTestId, 'wrapper')}
    >
      <Flex {...roomHeaderStyle} data-testid={formatDataTestId(baseDataTestId, 'header')}>
        <Flex {...numberTextStyle} data-testid={formatDataTestId(baseDataTestId, 'numberText')}>
          {roomsNumber > 1 ? (
            <Flex
              justifyContent="space-between"
              alignItems="center"
              w="full"
              data-testid={formatDataTestId(baseDataTestId, 'headerRow')}
            >
              <Flex
                direction="row"
                alignItems="center"
                data-testid={formatDataTestId(baseDataTestId, 'roomLabelRow')}
              >
                <Flex
                  background="primary"
                  w="1.5rem"
                  h="1.5rem"
                  borderRadius="50%"
                  justifyContent="center"
                  alignItems="center"
                  cursor="pointer"
                  onClick={() => setOpenRoomNumber(isOpen ? 0 : (roomNumber ?? 1))}
                  data-testid={formatDataTestId(baseDataTestId, 'toggle')}
                >
                  <Icon
                    mt={isOpen ? '0.125rem' : 'null'}
                    svg={isOpen ? <ChevronDown color="white" /> : <ChevronUp color="white" />}
                    data-testid={formatDataTestId(baseDataTestId, 'toggleIcon')}
                  />
                </Flex>
                <Text
                  w="4.125rem"
                  ml="1.125rem"
                  data-testid={formatDataTestId(baseDataTestId, 'roomLabel')}
                >
                  {t('search.room')} {roomNumber}
                </Text>
              </Flex>
              <Flex
                direction="row"
                alignItems="center"
                gap="0.75rem"
                data-testid={formatDataTestId(baseDataTestId, 'priceRow')}
              >
                {!isOpen && (
                  <Flex
                    direction="row"
                    display={{ mobile: 'flex', md: 'none' }}
                    alignItems="center"
                    data-testid={formatDataTestId(baseDataTestId, 'mobilePrice')}
                  >
                    <Text
                      fontSize="xs"
                      color="darkGrey2"
                      width="2.5rem"
                      fontWeight="normal"
                      data-testid={formatDataTestId(baseDataTestId, 'mobilePriceLabel')}
                    >
                      {t('booking.total')}
                    </Text>
                    <Text
                      as="span"
                      fontSize="xl"
                      fontWeight="bold"
                      data-testid={formatDataTestId(baseDataTestId, 'mobilePriceValue')}
                    >
                      {price}
                    </Text>
                  </Flex>
                )}
              </Flex>
            </Flex>
          ) : (
            <Text w="4.125rem" data-testid={formatDataTestId(baseDataTestId, 'roomLabelSingle')}>
              {t('search.room')}
            </Text>
          )}
          <Text
            as="span"
            display={{ mobile: 'none', md: 'block' }}
            ml={{ mobile: '0', md: roomsNumber > 1 ? '1.75rem' : 'sm', lg: 'sm' }}
            {...classTextStyle}
            data-testid={formatDataTestId(baseDataTestId, 'pmsRoomType')}
          >
            {activePmsRoomTypeLabel}
          </Text>
        </Flex>
        <Flex
          {...bedNumberTextStyle}
          justifyContent={{ mobile: 'space-between', md: 'inherit' }}
          data-testid={formatDataTestId(baseDataTestId, 'bedNumberText')}
        >
          <Box>
            <Text
              as="span"
              display={{ mobile: 'block', md: 'none' }}
              mb="xmd"
              ml={{ mobile: '0', md: roomsNumber > 1 ? '1.75rem' : 'sm', lg: 'sm' }}
              {...classTextStyle}
              data-testid={formatDataTestId(baseDataTestId, 'pmsRoomTypeMobile')}
            >
              {activePmsRoomTypeLabel}
            </Text>
          </Box>
          <Flex
            direction={{ mobile: 'row', md: 'column', lg: 'row' }}
            data-testid={formatDataTestId(baseDataTestId, 'peopleRow')}
          >
            <Flex direction="row" data-testid={formatDataTestId(baseDataTestId, 'peopleIcons')}>
              {Array.from({ length: totalPeople }).map((_, index) => (
                <Icon
                  key={index}
                  svg={<OneAdult transform="scale(0.77)" />}
                  ml="-0.2rem"
                  data-testid={formatDataTestId(baseDataTestId, `personIcon${index + 1}`)}
                />
              ))}
            </Flex>
            <Text
              as="span"
              ml={{ mobile: '0.313rem', md: '0', lg: '0.625rem' }}
              data-testid={formatDataTestId(baseDataTestId, 'peopleLabel')}
            >
              {adults} {adults > 1 ? t('search.adults') : t('search.adult')}
              {children > 0 &&
                `, ${children} ${children > 1 ? t('search.children') : t('search.child')}`}
            </Text>
          </Flex>
        </Flex>
        <Flex
          direction="row"
          alignItems="center"
          gap="0.75rem"
          data-testid={formatDataTestId(baseDataTestId, 'dropdownRow')}
        >
          {!isOpen && (
            <Flex
              direction={{ md: 'column', lg: 'row' }}
              display={{ mobile: 'none', md: 'flex' }}
              alignItems={{ md: 'flex-start', lg: 'center' }}
              data-testid={formatDataTestId(baseDataTestId, 'desktopPrice')}
            >
              <Text
                fontSize="xs"
                color="darkGrey2"
                width="2.875rem"
                data-testid={formatDataTestId(baseDataTestId, 'desktopPriceLabel')}
              >
                {nightsNumber} {nightsNumber > 1 ? t('pihotelinfo.nights') : t('pihotelinfo.night')}
              </Text>
              <Text
                as="span"
                fontSize="md"
                fontWeight="semibold"
                ml="var(--chakra-space-sm)"
                data-testid={formatDataTestId(baseDataTestId, 'desktopPriceValue')}
              >
                {price}
              </Text>
            </Flex>
          )}
          <Dropdown
            dataTestId={formatDataTestId(baseDataTestId, 'roomTypeDropdown')}
            onChange={onHandleChange}
            dropdownStyles={{
              menuButtonStyles: { w: { mobile: 'full', md: '13.563rem', lg: '14.813rem' } },
            }}
            matchWidth
            placeholder={activeRoomChoice?.roomType?.label}
            disabled={roomsNumber > 1 && !isOpen}
            icon={activeRoomChoice?.roomType?.icon}
            selectedId={activeRoomChoice?.roomType?.id}
            options={getDropdownOptions(
              Array.from(
                new Set(
                  matchedActiveRoom?.rooms?.map((room) => room.roomType)
                ) as unknown as HIStandardRoomType[]
              ),
              typeLabels,
              roomCodes as Partial<SearchRoomCodes>
            )}
          />
        </Flex>
      </Flex>
      <Divider data-testid={formatDataTestId(baseDataTestId, 'divider')} />
      {isOpen ? (
        <Flex
          gap="0.625rem"
          direction="column"
          mb="xlg"
          data-testid={formatDataTestId(baseDataTestId, 'rateCards')}
        >
          <RateNotifications
            accessibilityInfo={accessibilityInfo}
            brand={brand}
            cot={cot}
            hasAccessibleRoom={hasAccessibleRoom}
            isNonSilentSubstituNotificPerRoomClassEnabled={
              isNonSilentSubstituNotificPerRoomClassEnabled
            }
            roomTypeInformationResponse={roomTypeInformationResponse}
            specialRoomLimitMessage={specialRoomLimitMessage as unknown as typeof Box}
            currentClassRoomTypes={currentClassRoomTypes}
          />
          {roomClassesWithPrices?.map(({ roomClass, roomPriceBreakdown }) => {
            const price = {
              currency: roomPriceBreakdown?.currencyCode ?? Currency.GBP_NAME,
              amount: roomPriceBreakdown?.totalNetAmount ?? 0,
            };

            return (
              <Box
                key={roomClass.roomLabel}
                data-testid={formatDataTestId(baseDataTestId, 'rateCardBox')}
              >
                <RoomSelectionRateCard
                  data={roomClass}
                  price={price}
                  onHandleClick={onHandleClick}
                  activePmsRoomType={activeRoomChoice?.pmsRoomType}
                />
              </Box>
            );
          })}
        </Flex>
      ) : null}
      {isOpen && activeRoomChoice?.roomType.code === ROOM_CODES.twin && (
        <Text {...otherBedTextStyle} data-testid={formatDataTestId(baseDataTestId, 'otherBedText')}>
          {renderSanitizedHtml(
            t('hoteldetails.rates.grid.showDifferentRooms').replace('{price}', price)
          )}
        </Text>
      )}
    </Flex>
  );
}

export const getDropdownOptions = (
  availableOptions: string[],
  labels: RoomTypeLabels,
  codes: Partial<SearchRoomCodes>
): DropdownOption[] => {
  return availableOptions.map((option: string) => {
    const codeKey = (codes as Record<string, string | undefined>)[option] as
      | keyof RoomTypeLabels
      | undefined;

    const typeName = codeKey ? labels[codeKey] : option;
    const roomTypeOption = typeName.charAt(0).toUpperCase() + typeName.slice(1);

    return {
      id: roomTypeOption,
      icon: getRoomTypeIcon(option),
      label: roomTypeOption,
      code: option as AcceptedRoomCodes,
    };
  });
};

export const getRoomTypeIcon = (option: string): React.ReactElement => {
  switch (option) {
    case ROOM_CODES.single:
      return <SingleBed />;
    case ROOM_CODES.accessible:
      return <Accessible />;
    case ROOM_CODES.twin:
      return <BedTwin />;
    case ROOM_CODES.family:
      return <FamilyRoom />;
    case ROOM_CODES.double:
    default:
      return <DoubleBed />;
  }
};

const roomWrapperStyle = {
  borderBottom: '1px solid #D9D9D966',
  direction: 'column',
  px: 'var(--chakra-space-xlg)',
} as FlexProps;

const roomHeaderStyle = {
  direction: { mobile: 'column', md: 'row' },
  w: 'full',
  alignItems: { md: 'center' },
  justifyContent: 'space-between',
  gap: { mobile: 'xmd', lg: '0' },
  pt: '0.938rem',
} as FlexProps;

const numberTextStyle = {
  fontSize: 'lg',
  fontWeight: 'bold',
  flexDirection: { mobile: 'column', lg: 'row' },
  alignItems: { mobile: 'flex-start', md: 'center' },
  gap: { mobile: 'xmd', md: '0' },
} as FlexProps;

const classTextStyle = {
  fontSize: 'sm',
  fontWeight: 'normal',
};

const bedNumberTextStyle = {
  fontSize: 'sm',
  fontWeight: 'normal',
  direction: { mobile: 'row', md: 'column', lg: 'row' },
} as FlexProps;

const otherBedTextStyle = {
  fontWeight: 'medium',
  fontSize: 'sm',
  color: 'tertiary',
  textDecoration: 'underline',
  textAlign: 'right',
  pb: '1.25rem',
} as TextProps;
