import { Flex, Text, TextProps } from '@chakra-ui/react';
import {
  BC_RESERVATION_STATUS,
  MealsSelection,
  ExtrasPackagePricePerItem,
  EARLY_CHECKIN_IDS,
  LATE_CHECKOUT_IDS,
  PROSECCO_IDS,
  WIFI_IDS,
} from '@whitbread-eos/api';
import { formatCurrency, formatPrice, extrasNamingCheck } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

export interface Props {
  adultMealDescription: MealsSelection[];
  childrenMealDescription: MealsSelection[];
  currencyCode: string;
  language: string;
  noNights: number;
  bookingStatus: string;
  extrasPackageRoom?: ExtrasPackagePricePerItem;
  showEciLco?: boolean;
  showMeals?: boolean;
}
export default function BookingDetailsExtrasComponent({
  adultMealDescription,
  childrenMealDescription,
  currencyCode,
  language,
  noNights,
  bookingStatus,
  extrasPackageRoom,
  showEciLco,
  showMeals = true,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const nightTranslation =
    noNights > 1 ? t('dashboard.bookings.nights') : t('dashboard.bookings.night');

  const priceForExtras = (extrasId: string) => {
    if (EARLY_CHECKIN_IDS.includes(extrasId as (typeof EARLY_CHECKIN_IDS)[number])) {
      return extrasPackageRoom?.priceEci?.toFixed(2);
    }

    if (LATE_CHECKOUT_IDS.includes(extrasId as (typeof LATE_CHECKOUT_IDS)[number])) {
      return extrasPackageRoom?.priceLco?.toFixed(2);
    }

    if (WIFI_IDS.includes(extrasId as (typeof WIFI_IDS)[number])) {
      return extrasPackageRoom?.priceWifi?.toFixed(2);
    }

    if (PROSECCO_IDS.includes(extrasId as (typeof PROSECCO_IDS)[number])) {
      return extrasPackageRoom?.priceBOProsecco?.toFixed(2);
    }

    return '';
  };

  return (
    <Flex flexDir="column" width="full">
      {showMeals &&
        adultMealDescription?.map((adultMeal: MealsSelection, index: number) => {
          return renderMeal(
            adultMeal?.title ?? '',
            adultMeal?.noSelections ?? 0,
            true,
            index,
            adultMeal.price as number
          );
        })}

      {showMeals &&
        childrenMealDescription?.length > 0 &&
        childrenMealDescription?.map((childMeal: MealsSelection, index: number) => {
          return renderMeal(
            childMeal?.title ?? '',
            childMeal?.noSelections ?? 0,
            false,
            index,
            childMeal.price as number
          );
        })}
      {showEciLco &&
        extrasPackageRoom?.packagesList?.map((extrasPackageId: string, index) => (
          <Flex
            flexDir="row"
            justifyContent="space-between"
            key={`${extrasPackageRoom?.reservationId}-${index} `}
          >
            <Text {...titleStyle} data-testid={`extras-name`}>
              {extrasNamingCheck(t, extrasPackageId)}
            </Text>
            {bookingStatus !== BC_RESERVATION_STATUS.CANCELLED && (
              <Flex flexDir="column" data-testid={`extras-price`}>
                <Text {...priceStyle}>
                  {formatPrice(
                    formatCurrency(currencyCode),
                    priceForExtras(extrasPackageId),
                    language
                  )}
                </Text>
              </Flex>
            )}
          </Flex>
        ))}
    </Flex>
  );

  function renderMeal(
    title: string,
    noSelections: number,
    isAdultMeal: boolean,
    index: number,
    price = 0
  ) {
    const calculatedPrice = (price * noSelections * noNights).toFixed(2);
    let guestTranslation = '';
    let childTranslation = '';

    if (isAdultMeal) {
      guestTranslation = t(
        noSelections > 1 ? 'dashboard.bookings.guests' : 'dashboard.bookings.guest'
      );
    } else {
      childTranslation = t(
        noSelections > 1 ? 'dashboard.bookings.children' : 'dashboard.bookings.child'
      );
    }

    return (
      <Flex flexDir="row" justifyContent="space-between" key={index + title}>
        <Text {...titleStyle} data-testid="mealName">
          {title}
        </Text>
        {bookingStatus !== BC_RESERVATION_STATUS.CANCELLED && (
          <Flex flexDir="column">
            <Text {...priceStyle}>
              {formatPrice(formatCurrency(currencyCode), calculatedPrice, language)}
            </Text>
            {isAdultMeal ? (
              <Text {...priceDescriptionStyle}>{`${t(
                'dashboard.bookings.priceFor'
              )} ${noNights} ${nightTranslation}, ${noSelections} ${guestTranslation}`}</Text>
            ) : (
              <Text {...priceDescriptionStyle}>{`${t(
                'dashboard.bookings.priceFor'
              )} ${noSelections} ${childTranslation}`}</Text>
            )}
          </Flex>
        )}
      </Flex>
    );
  }
}

const titleStyle = {
  color: 'darkGrey2',
  fontSize: { mobile: 'md', sm: 'lg' },
  lineHeight: '3',
  fontWeight: 'normal',
  w: { mobile: '8.56rem', sm: 'auto' },
  as: 'h6',
} as TextProps;

const priceStyle = {
  fontWeight: 'medium',
  fontSize: { mobile: 'md', sm: 'lg' },
  color: 'darkGrey2',
  lineHeight: '3',
  as: 'h6',
  alignSelf: 'flex-end',
} as TextProps;

const priceDescriptionStyle = {
  fontWeight: 'normal',
  fontSize: 'sm',
  lineHeight: '2',
  color: 'darkGrey2',
  textAlign: 'right',
} as TextProps;
