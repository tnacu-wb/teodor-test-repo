import {
  Box,
  Text,
  BoxProps,
  Divider,
  Flex,
  Grid,
  GridItem,
  ChakraProps,
  Link,
} from '@chakra-ui/react';
import {
  ExtrasPackages,
  FS_SILENT_SUBSTITUTION,
  paymentOptions,
  RoomPackageSelection,
  RoomsExpanded,
  SilentSubstitutionLocalStorage,
} from '@whitbread-eos/api';
import {
  formatCurrency,
  formatPrice,
  getCurrentReservationStorageData,
  useSemanticTypography,
  useFeatureSwitch,
} from '@whitbread-eos/utils';
import { useState, useEffect } from 'react';

import RoomCard from './RoomCard.component';

function renderPaymentConfirmationText(paymentOption: string) {
  switch (paymentOption) {
    case paymentOptions.PAY_NOW:
      return 'booking.confirmation.paymentTaken';
    case paymentOptions.PAY_ON_ARRIVAL:
      return 'booking.confirmation.reservedWithCard';
    case paymentOptions.RESERVE_WITHOUT_CARD:
      return 'booking.confirmation.reservedWithoutCard';
    default:
      return paymentOption;
  }
}

interface RoomDetailsData {
  roomReservationStartDate: string;
  roomReservationEndDate: string;
  leadGuestTitle: string;
  leadGuestName: string;
  roomType: string;
  roomTypeDescription: string;
  rateType: string;
  rateTypeDescription: string;
  roomGroup: string;
  roomPrice: number;
  roomTotalPrice: number;
  ratesPerNight: {
    pricePerNight: number;
    startDate: string;
    cityTaxPerNight: number;
  }[];
  packages: {
    computedPrice: number;
    description: string;
    totalQuantity: number;
    unitPrice: number;
  }[];
  adultMealDescription: string[];
  childrenMealDescription: string[];
  mealPrice: number;
  extrasRoomSelection?: RoomPackageSelection[];
  packagesExtrasItems?: ExtrasPackages[];
}

interface RoomDetailsProps {
  roomDetails: RoomDetailsData[];
  currency: string;
  bookingTotalCost: number;
  donations?: {
    amount: string;
    currency: string;
  };
  selectedPaymentOption: string;
  taxesMessage?: string;
  brand?: string;
}

interface Props extends BoxProps {
  data: RoomDetailsProps;
  basketReference: string;
  currentLang: string | undefined;
  t: (x: string, y?: { [key: string]: string }) => string;
}

export default function RoomDetails({ data, basketReference, currentLang, t }: Readonly<Props>) {
  const {
    roomDetails,
    donations = null,
    bookingTotalCost,
    currency,
    selectedPaymentOption,
    taxesMessage,
    brand,
  } = data;
  const baseDataTestId = 'RoomDetailsSection';

  const [cardsExpanded, setCardsExpanded] = useState<RoomsExpanded[]>([]);
  const [showExpanded, setShowExpanded] = useState(true);
  const getTypographyProps = useSemanticTypography();

  const showExpandCollapseButton = roomDetails.length !== 1;

  const isCardsExpandedIndividually = cardsExpanded.every((element) => !element.expanded);
  const isCardsCollapsedIndividually = cardsExpanded.every((element) => element.expanded);

  const isSilentFeatureFlagEnabled = useFeatureSwitch({
    featureSwitchKey: FS_SILENT_SUBSTITUTION,
  });
  const currentReservationRoomData = isSilentFeatureFlagEnabled
    ? getCurrentReservationStorageData(basketReference)
    : ({} as SilentSubstitutionLocalStorage);

  useEffect(() => {
    if (isCardsExpandedIndividually) {
      setShowExpanded(true);
    }
    if (isCardsCollapsedIndividually) {
      setShowExpanded(false);
    }
  }, [isCardsExpandedIndividually, isCardsCollapsedIndividually]);
  if (!roomDetails.length) {
    return null;
  }

  const calcRoomsTotal = () => {
    let roomsTotal = 0;
    roomDetails.forEach((room) => (roomsTotal += room.roomPrice));
    return roomsTotal;
  };

  const calcExtrasTotal = () => {
    let extrasTotal = 0;
    roomDetails.forEach((room) => {
      room?.packages?.forEach(
        (pkg) =>
          (extrasTotal += !pkg.description.includes('Charity')
            ? pkg.computedPrice * room?.ratesPerNight?.length
            : 0)
      );
    });
    return extrasTotal;
  };

  return (
    <Box {...contentWrapperStyle} data-testid={baseDataTestId}>
      <Box {...roomDetailsTitleContainer}>
        <Text
          {...roomDetailsTitleLayoutStyles}
          {...getTypographyProps(
            roomDetailsTitleLegacyTypography,
            roomDetailsTitleSemanticTypography
          )}
          sx={{ '@media print': { display: 'none' } }}
        >
          {t('booking.roomdetails.title')}
        </Text>
        {showExpandCollapseButton && (
          <Link onClick={() => handleExpandCollapse()} {...expandCollapseButtonStyle}>
            {showExpanded
              ? t('booking.roomdetails.expandAll')
              : t('booking.roomdetails.collapseAll')}
          </Link>
        )}
      </Box>
      {renderRoomCards()}
      <Box display="none" sx={{ '@media print': { display: 'block' } }}>
        <Divider my="lg" borderBottomWidth="2px" />
        <Flex justify="space-between" mb="md">
          <Text>{t('account.dashboard.roomsTotal')}</Text>
          <Text fontWeight="bold">
            {formatPrice(formatCurrency(currency), calcRoomsTotal().toFixed(2), currentLang)}
          </Text>
        </Flex>
        <Flex justify="space-between" mb="md">
          <Text>{t('account.dashboard.extrasTotal')}</Text>
          <Text fontWeight="bold">
            {formatPrice(formatCurrency(currency), calcExtrasTotal().toFixed(2), currentLang)}
          </Text>
        </Flex>
        {donations?.amount && (
          <Flex justify="space-between" mb="md">
            <Text>{t('booking.confirmation.donationMessage')}</Text>
            <Text fontWeight="bold">
              {formatPrice(formatCurrency(currency), donations?.amount, currentLang)}
            </Text>
          </Flex>
        )}

        <Grid templateColumns="repeat(2, 1fr)" mb="md">
          <GridItem>
            <Text fontWeight="bold" mb="md">
              {t('booking.confirmation.totalCost')}:
            </Text>
            <Text>{t(renderPaymentConfirmationText(selectedPaymentOption))}</Text>
          </GridItem>
          <GridItem textAlign="right">
            <Text fontWeight="bold" fontSize="4xl">
              {formatPrice(formatCurrency(currency), bookingTotalCost.toFixed(2), currentLang)}
            </Text>
            {!!taxesMessage?.length && <Text>{taxesMessage}</Text>}
          </GridItem>
        </Grid>
        <Divider my="lg" borderBottomWidth="2px" />
      </Box>
    </Box>
  );

  function handleExpandCollapse(roomNumber?: number) {
    setCardsExpanded((prevState: RoomsExpanded[]) =>
      prevState.map((cardItem: RoomsExpanded) => {
        let expanded = !cardItem.expanded;
        if (roomNumber && cardItem.roomNumber !== roomNumber) {
          expanded = cardItem.expanded;
        }
        return {
          ...cardItem,
          expanded,
        };
      })
    );
  }

  function renderRoomCards() {
    return roomDetails.map((room, index) => {
      const roomData =
        (isSilentFeatureFlagEnabled &&
          currentReservationRoomData?.value[index]?.silentSubstitution && {
            ...{
              ...room,
              roomType: currentReservationRoomData?.value[index]?.roomLabelCode ?? '',
              roomTypeDescription: null,
            },
          }) ||
        room;

      return (
        <RoomCard
          t={t}
          key={`room-${index + 1}-key`}
          room={roomData}
          currency={currency}
          roomNumber={index + 1}
          currentLang={currentLang}
          taxesMessage={taxesMessage}
          brand={brand}
          isCardExpanded={
            roomDetails.length === 1 ? !cardsExpanded[0]?.expanded : cardsExpanded[index]?.expanded
          }
          cardsExpanded={cardsExpanded}
          setCardsExpanded={setCardsExpanded}
          handleExpandCollapse={handleExpandCollapse}
        />
      );
    });
  }
}

const contentWrapperStyle = {
  w: 'full',
  mb: '2xl',
};

const roomDetailsTitleLayoutStyles = {
  mb: 'md',
};

const roomDetailsTitleLegacyTypography = {
  fontSize: '2xl',
  fontWeight: 'semibold',
};

const roomDetailsTitleSemanticTypography = {
  textStyle: 'heading-m',
};

const expandCollapseButtonStyle = {
  fontSize: '0.875rem',
  fontWeight: 'semibold',
  textDecoration: 'underline',
  lineHeight: '1.25rem',
  color: '#511E62',
};

const roomDetailsTitleContainer = {
  display: 'flex',
  flexDirection: 'row',
  justifyContent: 'space-between',
} as ChakraProps;
