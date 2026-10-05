import type { FlexProps } from '@chakra-ui/react';
import { Box, Flex, Heading, Text, VStack, Collapse, Link } from '@chakra-ui/react';
import { ROOM_TYPE, HITwinRoomPrice } from '@whitbread-eos/api';
import { RadioButton } from '@whitbread-eos/atoms';
import {
  formatCurrency,
  formatPrice,
  useCustomLocale,
  useScreenSize,
  renderSanitizedHtml,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useState } from 'react';

interface Props {
  rooms: Room[];
  onTwinroomSelection: (roomIndex: number, selection: string) => void;
  twinroomSelections: string[];
  twinRoomPrices: HITwinRoomPrice[] | undefined;
}

interface Room {
  roomType: string;
  adults: number;
  children: number;
  twinroomTypes: (string | undefined)[] | undefined;
}

interface TwinRoomOptionsToggle {
  [key: string]: boolean;
}

export default function TwinroomOptionsComponent({
  rooms,
  onTwinroomSelection,
  twinroomSelections,
  twinRoomPrices,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const { language } = useCustomLocale();
  const currencyCode = twinRoomPrices?.[0]?.currencyCode;

  const [showImprovedTwinDescription, setShowImprovedTwinDescription] =
    useState<TwinRoomOptionsToggle>({});
  const [showStandardTwinDescription, setShowStandardTwinDescription] =
    useState<TwinRoomOptionsToggle>({});

  const improvedTwinToggle = (key: number, value: boolean) => {
    setShowImprovedTwinDescription({ ...showImprovedTwinDescription, [key]: value });
  };

  const standardTwinToggle = (key: number, value: boolean) => {
    setShowStandardTwinDescription({ ...showStandardTwinDescription, [key]: value });
  };

  function formatRatePrice(price: number) {
    return price - Math.floor(price) !== 0 ? price.toFixed(2) : price;
  }

  useEffect(() => {
    rooms.forEach((room, roomIndex) => {
      const isSelectionRequired = selectionRequired(room);

      const twinroomSelected =
        twinroomSelections?.[roomIndex] === ROOM_TYPE.TWIN_TWO_BEDS ||
        twinroomSelections?.[roomIndex] === ROOM_TYPE.TWIN_DOUBLE_SOFA;

      if (isSelectionRequired) {
        const hasTwinTwoBeds = room.twinroomTypes?.includes(ROOM_TYPE.TWIN_TWO_BEDS);
        const hasTwinDoublePlusSofa = room.twinroomTypes?.includes(ROOM_TYPE.TWIN_DOUBLE_SOFA);

        if (hasTwinTwoBeds && !twinroomSelected) {
          onTwinroomSelection(roomIndex, ROOM_TYPE.TWIN_TWO_BEDS);
        } else if (hasTwinDoublePlusSofa && !twinroomSelected) {
          onTwinroomSelection(roomIndex, ROOM_TYPE.TWIN_DOUBLE_SOFA);
        }
      }
    });
  }, [rooms, onTwinroomSelection, twinroomSelections]);

  const { isLessThanXs } = useScreenSize();

  return (
    <>
      <Heading as="h1" data-testid="twin-title" {...headingStyles}>
        {t('seo.chooseTwinRoom.title')}
      </Heading>

      {rooms.map((room, roomIndex) => {
        const isSelectionRequired = selectionRequired(room);

        return (
          <Box key={roomIndex} mt={{ base: 'xl', xs: '3xl' }}>
            <Flex {...roomAndGuestsFlexStyles}>
              <Flex direction="column" mr="md">
                <Heading as="h3" data-testid="twin-room-number" {...roomHeadingStyles}>
                  {t('booking.hotel.summary.room').replace('[roomNumber]', `${roomIndex + 1} `)}
                  {room.roomType}
                </Heading>
                {renderGuestNumbers(room)}
              </Flex>
            </Flex>
            {isSelectionRequired ? (
              renderSelectionOptions(roomIndex)
            ) : (
              <Text mt={{ base: '1', xs: '0' }} data-testid="twin-no-selection-required">
                {t('chooseyourtwin.no.selection.required')}
              </Text>
            )}
          </Box>
        );
      })}
    </>
  );

  // Verify label for roomType is a Twin room one
  function selectionRequired(room: Room) {
    return (
      room.roomType.startsWith('Twin') ||
      room.roomType.startsWith(t('pihotelinfo.chooseTwinRoom.title'))
    );
  }

  function renderGuestNumbers(room: Room) {
    const adultsLabel = `${room.adults} ${
      room.adults > 1 ? t('dashboard.bookings.adults') : t('dashboard.bookings.adult')
    }`;
    const childrenLabel = `, ${room.children} ${
      room.children > 1 ? t('dashboard.bookings.children') : t('dashboard.bookings.child')
    }`;

    return (
      <Text mt="1" lineHeight="3" data-testid="twin-room-guest-numbers">
        {adultsLabel}
        {room.children ? childrenLabel : ''}
      </Text>
    );
  }

  function renderSelectionOptions(roomIndex: number) {
    return (
      <Box maxW="3xl" mt="lg" data-testid="twinroom-options">
        <VStack alignItems="stretch" w="full">
          <Flex flexDirection="row" {...rateItemCardStyle}>
            <RadioButton
              type="improved-twin"
              value="improved-twin"
              onChange={() => onTwinroomSelection(roomIndex, ROOM_TYPE.TWIN_TWO_BEDS)}
              isChecked={twinroomSelections?.[roomIndex] === ROOM_TYPE.TWIN_TWO_BEDS}
              width="full"
              listIndex="left"
            >
              <Box>
                <Text fontWeight="bold" data-testid="improved-twin">
                  {t('twinroom.improvedTwin.title')}
                </Text>
                <Collapse
                  in={showImprovedTwinDescription[roomIndex] || !isLessThanXs}
                  data-testid="improvedTwin-description-collapse"
                >
                  <Box className="formatLinks">
                    {renderSanitizedHtml(t('twinroom.improvedTwin.description'))}
                  </Box>
                </Collapse>
                <Link
                  data-testid="improvedTwin-description-toggle"
                  {...optionsStyles}
                  onClick={() =>
                    improvedTwinToggle(roomIndex, !showImprovedTwinDescription[roomIndex])
                  }
                >
                  {showImprovedTwinDescription[roomIndex]
                    ? t('hoteldetails.hide')
                    : t('hoteldetails.show')}
                </Link>
              </Box>
            </RadioButton>
            {twinRoomPrices && !!twinRoomPrices[0]?.price && (
              <Flex
                {...{
                  ...radioWrapperStyles,
                  ...(twinroomSelections?.[roomIndex] === ROOM_TYPE.TWIN_TWO_BEDS
                    ? checkedStyles
                    : unCheckedStyles),
                }}
              >
                <Text {...roomTotalPriceStyles} data-testid="improvedTwin-price">
                  {formatPrice(
                    currencyCode && formatCurrency(currencyCode),
                    formatRatePrice(twinRoomPrices[0]?.price),
                    language
                  )}
                </Text>
                <Text {...roomTotalPriceTitleStyles}>{t('hoteldetails.rates.total.price')}</Text>
              </Flex>
            )}
          </Flex>
          <Flex flexDirection="row" {...rateItemCardStyle}>
            <RadioButton
              type="sofa-double-twin"
              value="sofa-double-twin"
              onChange={() => onTwinroomSelection(roomIndex, ROOM_TYPE.TWIN_DOUBLE_SOFA)}
              isChecked={twinroomSelections?.[roomIndex] === ROOM_TYPE.TWIN_DOUBLE_SOFA}
              width="full"
              listIndex="left"
            >
              <Box>
                <Text fontWeight="bold" data-testid="double-sofa-twin">
                  {t('twinroom.standardTwin.title')}
                </Text>
                <Collapse
                  in={showStandardTwinDescription[roomIndex] || !isLessThanXs}
                  data-testid="standardTwin-description-collapse"
                >
                  <Box className="formatLinks">
                    {renderSanitizedHtml(t('twinroom.standardTwin.description'))}
                  </Box>
                </Collapse>
                <Link
                  {...optionsStyles}
                  data-testid="standardTwin-description-toggle"
                  onClick={() =>
                    standardTwinToggle(roomIndex, !showStandardTwinDescription[roomIndex])
                  }
                >
                  {showStandardTwinDescription[roomIndex]
                    ? t('hoteldetails.hide')
                    : t('hoteldetails.show')}
                </Link>
              </Box>
            </RadioButton>
            {twinRoomPrices && !!twinRoomPrices[1]?.price && (
              <Flex
                {...{
                  ...radioWrapperStyles,
                  ...(twinroomSelections?.[roomIndex] === ROOM_TYPE.TWIN_DOUBLE_SOFA
                    ? checkedStyles
                    : unCheckedStyles),
                }}
              >
                <Text {...roomTotalPriceStyles} data-testid="double-sofa-twin-price">
                  {formatPrice(
                    currencyCode && formatCurrency(currencyCode),
                    formatRatePrice(twinRoomPrices[1]?.price),
                    language
                  )}
                </Text>
                <Text {...roomTotalPriceTitleStyles}>{t('hoteldetails.rates.total.price')}</Text>
              </Flex>
            )}
          </Flex>
        </VStack>
      </Box>
    );
  }
}

const rateItemCardStyle = {
  maxWidth: '40.12rem',
};

const radioWrapperStyles = {
  maxWidth: '8rem',
  width: 'full',
  flexDirection: 'column',
  justifyContent: 'center',
  alignItems: 'center',
} as const;

const checkedStyles = {
  border: 'solid',
  borderWidth: '2px 2px 2px 0',
  borderColor: 'primary',
  backgroundColor: 'infoTint',
  padding: '10px',
};

const unCheckedStyles = {
  borderLeft: 'solid',
  borderWidth: '1px 1px 1px 0',
  borderColor: 'lightGrey1',
  padding: '10px',
};

const roomTotalPriceStyles = {
  fontSize: { base: 'md', sm: 'lg' },
  lineHeight: '3',
  fontWeight: 'semibold',
};

const roomTotalPriceTitleStyles = {
  fontSize: 'xs',
  lineHeight: '2',
};

const headingStyles = {
  fontWeight: 'semibold',
  fontSize: { base: '3xl', sm: '3xxl' },
  lineHeight: { base: '4', sm: '5' },
};

const roomAndGuestsFlexStyles = {
  mb: 'sm',
  flexDirection: { base: 'column', xs: 'row' },
  w: { base: '100%', sm: '64%', md: '50%' },
} as FlexProps;

const roomHeadingStyles = {
  fontWeight: 'semibold',
  fontSize: '2xl',
  lineHeight: '4',
};

const optionsStyles = {
  display: 'none',
  color: 'btnSecondaryEnabled',
  sx: {
    ':hover': {
      textDecoration: 'none',
    },
    '@media screen and (max-width: 374px)': { display: 'block' },
  },
};
