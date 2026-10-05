import { Flex, FlexProps, Spacer, Text, TextProps } from '@chakra-ui/react';
import type {
  AmendReservation,
  AmendRoomsAndGuestsLabels,
  BookingSummaryLabels,
  BookingSummaryRoomInformationProps,
  ExtrasPackagesPrices,
} from '@whitbread-eos/api';
import {
  ExtrasId,
  PackagesSelection,
  EARLY_CHECKIN_IDS,
  LATE_CHECKOUT_IDS,
  PROSECCO_IDS,
  WIFI_IDS,
} from '@whitbread-eos/api';
import {
  extrasNamingCheck,
  formatCurrency,
  formatDataTestId,
  formatPriceWithDecimal,
  upperOnlyFirst,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

export interface BookingSummaryRoomDetailsProps {
  language: string;
  baseDataTestId: string;
  bookingSummaryLabels: BookingSummaryLabels;
  roomsAndGuestsLabels: AmendRoomsAndGuestsLabels;
  reservation: AmendReservation;
  roomNumber: number;
  currency: string;
  roomPackages: BookingSummaryRoomInformationProps;
  noNights: number;
  extrasItemsPrices?: ExtrasPackagesPrices;
}

export const BookingSummaryRoomDetails = (props: BookingSummaryRoomDetailsProps) => {
  const {
    language,
    baseDataTestId,
    bookingSummaryLabels,
    roomsAndGuestsLabels,
    reservation,
    roomNumber,
    currency,
    roomPackages,
    noNights,
    extrasItemsPrices,
  } = props;
  const { roomStay, reservationGuestList } = reservation;
  const { t } = useTranslation();

  const checkPriceForExtras = (extrasId: string) => {
    if (EARLY_CHECKIN_IDS.includes(extrasId as (typeof EARLY_CHECKIN_IDS)[number])) {
      return extrasItemsPrices?.eciPrice;
    }
    if (LATE_CHECKOUT_IDS.includes(extrasId as (typeof LATE_CHECKOUT_IDS)[number])) {
      return extrasItemsPrices?.lcoPrice;
    }
    if (WIFI_IDS.includes(extrasId as (typeof WIFI_IDS)[number])) {
      return extrasItemsPrices?.wifiPrice;
    }
    if (PROSECCO_IDS.includes(extrasId as (typeof PROSECCO_IDS)[number])) {
      return extrasItemsPrices?.bOfProseccoPrice;
    }
    return '';
  };

  const showExtrasAndMeals =
    (roomPackages?.selectedMeals && roomPackages.selectedMeals.adultsMeals.length > 0) ||
    (roomPackages?.selectedExtrasList &&
      roomPackages?.selectedExtrasList.packagesSelection.length > 0);

  const getNumberOfChildren = () => {
    if (roomStay?.childrenNumber === 0) {
      return '';
    } else {
      return `, ${roomStay.childrenNumber} ${
        roomStay.childrenNumber === 1
          ? roomsAndGuestsLabels.roomModalLabels.roomAvailabilityLabels.child
          : roomsAndGuestsLabels.roomModalLabels.roomAvailabilityLabels.children
      }`;
    }
  };
  return (
    <Flex {...detailsWrapperStyle}>
      <Text data-testid={formatDataTestId(baseDataTestId, 'room-number')} {...subtitleTextStyle}>
        {roomsAndGuestsLabels.roomLabel} {roomNumber + 1}
      </Text>
      <Text
        className="sessioncamhidetext assist-no-show"
        data-testid={formatDataTestId(baseDataTestId, 'room-lead-guest')}
        {...infoTextStyle}
      >
        {upperOnlyFirst(reservationGuestList[0]?.nameTitle || '')}{' '}
        {reservationGuestList[0]?.givenName} {reservationGuestList[0]?.surName}
      </Text>
      <Text data-testid={formatDataTestId(baseDataTestId, 'room-occupancy')} {...infoTextStyle}>
        {roomStay?.adultsNumber}{' '}
        {roomStay?.adultsNumber === 1
          ? roomsAndGuestsLabels.roomModalLabels.roomAvailabilityLabels.adult
          : roomsAndGuestsLabels.roomModalLabels.roomAvailabilityLabels.adults}
        {getNumberOfChildren()}
      </Text>
      <Flex>
        <Text data-testid={formatDataTestId(baseDataTestId, 'room-name')} maxW="12.125rem">
          {roomStay?.roomExtraInfo?.roomName}
        </Text>
        <Spacer />
        <Text data-testid={formatDataTestId(baseDataTestId, 'room-price')} {...priceTextStyles}>
          {formatPriceWithDecimal(language, formatCurrency(currency), roomStay.roomPrice, true)}
        </Text>
      </Flex>
      {showExtrasAndMeals && (
        <>
          <Text
            data-testid={formatDataTestId(baseDataTestId, `${roomNumber}-meals-and-extras-label`)}
            {...extrasTextStyle}
          >
            {bookingSummaryLabels.mealsLabel} & {bookingSummaryLabels.extrasLabel}
          </Text>
          <Flex mt="sm" flexDirection="column">
            {roomPackages?.selectedMeals?.adultsMeals?.map((adultMeal) => (
              <Flex justifyContent="space-between" key={adultMeal.id}>
                <Text
                  data-testid={formatDataTestId(
                    baseDataTestId,
                    `room-${roomNumber}-meals-${adultMeal.id}`
                  )}
                >
                  {adultMeal.noSelections} x {adultMeal.title}
                </Text>

                <Text
                  data-testid={formatDataTestId(
                    baseDataTestId,
                    `room-${roomNumber}-meal-price-${adultMeal.id}`
                  )}
                  {...priceTextStyles}
                >
                  {adultMeal.price &&
                    formatPriceWithDecimal(
                      language,
                      formatCurrency(currency),
                      adultMeal.price * adultMeal.noSelections * noNights,
                      true
                    )}
                </Text>
              </Flex>
            ))}
            {roomPackages?.selectedMeals?.childrenMeals?.map((childrenMeal) => {
              const mealPrice = childrenMeal.price ?? 0;
              return (
                <Flex justifyContent="space-between" key={childrenMeal.id}>
                  <Text
                    data-testid={formatDataTestId(
                      baseDataTestId,
                      `room-${roomNumber}-meals-${childrenMeal.id}`
                    )}
                  >
                    {childrenMeal.noSelections} x {childrenMeal.title}
                  </Text>

                  <Text
                    data-testid={formatDataTestId(
                      baseDataTestId,
                      `room-${roomNumber}-meal-price-${childrenMeal.id}`
                    )}
                    {...priceTextStyles}
                  >
                    {formatPriceWithDecimal(
                      language,
                      formatCurrency(currency),
                      mealPrice * childrenMeal.noSelections * noNights,
                      true
                    )}
                  </Text>
                </Flex>
              );
            })}
            {roomPackages?.selectedExtrasList?.packagesSelection?.map(
              (extrasPackage: PackagesSelection) => {
                return (
                  <Flex
                    justifyContent="space-between"
                    key={`room-${roomNumber}-extras-${extrasPackage.id}`}
                  >
                    <Text
                      data-testid={formatDataTestId(
                        baseDataTestId,
                        `room-${roomNumber}-extras-${extrasPackage.id}`
                      )}
                      {...(extrasPackage?.id &&
                      !WIFI_IDS.includes(extrasPackage.id as (typeof WIFI_IDS)[number]) &&
                      !PROSECCO_IDS.includes(extrasPackage.id as (typeof PROSECCO_IDS)[number])
                        ? eciLcoTextStyle
                        : {})}
                    >
                      {extrasNamingCheck(t, extrasPackage?.id)}
                    </Text>

                    <Text
                      data-testid={formatDataTestId(
                        baseDataTestId,
                        `room-${roomNumber}-extras-price-${extrasPackage.id}`
                      )}
                      {...priceTextStyles}
                      {...(extrasPackage.id !== ExtrasId.ULTIMATE_WIFI &&
                      extrasPackage.id !== ExtrasId.BOTTLE_OF_PROSECCO
                        ? eciLcoTextStyle
                        : {})}
                    >
                      {formatPriceWithDecimal(
                        language,
                        formatCurrency(currency),
                        Number(checkPriceForExtras(extrasPackage?.id as string)),
                        true
                      )}
                    </Text>
                  </Flex>
                );
              }
            )}
          </Flex>
        </>
      )}
    </Flex>
  );
};

const detailsWrapperStyle = {
  direction: 'column',
  pb: 'lg',
} as FlexProps;

const subtitleTextStyle = {
  pt: 'lg',
  pb: 'sm',
  fontSize: 'xl',
  lineHeight: '3',
  fontWeight: 'semibold',
};

const extrasTextStyle = {
  fontWeight: 'semibold',
  pt: { mobile: 'lg', lg: 'md' },
};

const infoTextStyle = {
  lineHeight: '3',
  fontSize: 'md',
  color: 'darkGrey1',
  as: 'h6',
  fontWeight: 'normal',
  mb: 'sm',
} as TextProps;

const eciLcoTextStyle = {
  textDecoration: 'line-through',
  color: 'lightGrey1',
};
const priceTextStyles = {
  w: '5rem',
  textAlign: 'right',
} as TextProps;
