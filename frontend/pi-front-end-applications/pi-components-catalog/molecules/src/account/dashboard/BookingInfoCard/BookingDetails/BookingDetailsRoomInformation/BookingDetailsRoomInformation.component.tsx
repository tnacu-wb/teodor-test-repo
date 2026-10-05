import type { TextProps } from '@chakra-ui/react';
import { Flex, Text } from '@chakra-ui/react';
import { BC_RESERVATION_STATUS, MealsSelection, RoomDetails } from '@whitbread-eos/api';
import { formatCurrency, formatPrice, useCustomLocale } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useCallback } from 'react';

import { BookingDetailsExtrasComponent } from '../BookingDetailsExtras';

export interface Props extends RoomDetails {
  currencyCode?: string;
  bookingStatus: string;
  roomNumber: number;
  showExtras?: boolean;
  area?: string;
}

export default function BookingDetailsRoomInformationComponent({
  leadGuestName,
  roomPrice,
  roomType,
  childrenMealDescription,
  noChildren,
  adultMealDescription,
  noAdults,
  noNights,
  currencyCode,
  bookingStatus,
  roomNumber,
  extrasPackageRoom,
  showExtras = true,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const { language } = useCustomLocale();

  const getAdultsPluralLabel = useCallback(
    (value: number) => t(value === 1 ? 'account.dashboard.adult' : 'account.dashboard.adults'),
    [t]
  );

  const getChildrenPluralLabel = useCallback(
    (value: number) => t(value === 1 ? 'account.dashboard.child' : 'account.dashboard.children'),
    [t]
  );
  const showMeals = showExtras && adultMealDescription?.length > 0;
  const renderExtrasSection = showMeals || (extrasPackageRoom?.packagesList?.length as number) > 0;

  return (
    <Flex direction="column">
      <Text {...roomNumberStyle} data-testid={`roomNumber-${roomNumber}`}>
        {t('booking.summary.room')} {roomNumber}
        <Text
          {...leadNameStyle}
          data-testid="leadGuestName"
          className="sessioncamhidetext assist-no-show"
        >
          {` ${leadGuestName}`}
        </Text>
      </Text>
      <Flex justifyContent="space-between">
        <Text {...roomDetailsStyle} data-testid="roomType">
          {roomType}
          {noAdults > 0 && (
            <Text as="span">{` - ${noAdults} ${getAdultsPluralLabel(noAdults)}`}</Text>
          )}
          {noChildren > 0 && (
            <Text as="span">{`, ${noChildren} ${getChildrenPluralLabel(noChildren)}`}</Text>
          )}
        </Text>
        {bookingStatus !== BC_RESERVATION_STATUS.CANCELLED && (
          <Text {...priceStyle} data-testid="roomPriceLabel">
            {formatPrice(
              currencyCode && formatCurrency(currencyCode),
              roomPrice?.toFixed(2),
              language
            )}
          </Text>
        )}
      </Flex>
      {renderExtrasSection && (
        <>
          <Text mt={{ mobile: 'md', lg: 'lg' }} {...roomNumberStyle}>
            {t('account.dashboard.extras')}
          </Text>
          <BookingDetailsExtrasComponent
            adultMealDescription={adultMealDescription}
            childrenMealDescription={childrenMealDescription as MealsSelection[]}
            currencyCode={currencyCode as string}
            extrasPackageRoom={extrasPackageRoom}
            language={language}
            noNights={noNights as number}
            bookingStatus={bookingStatus}
            showEciLco={renderExtrasSection}
            showMeals={showMeals}
          />
        </>
      )}
    </Flex>
  );
}

const leadNameStyle = {
  color: 'darkGrey2',
  as: 'span',
  fontWeight: 'normal',
  fontSize: 'md',
  lineHeight: '3',
} as TextProps;

const priceStyle = {
  fontWeight: 'medium',
  fontSize: { mobile: 'md', sm: 'lg' },
  color: 'darkGrey2',
  lineHeight: '3',
} as TextProps;

const roomDetailsStyle = {
  color: 'darkGrey2',
  fontSize: { mobile: 'md', sm: 'lg' },
  lineHeight: '3',
  fontWeight: 'normal',
  w: { mobile: '7.25rem', sm: 'full' },
} as TextProps;

const roomNumberStyle = {
  fontWeight: 'semibold',
  fontSize: { mobile: 'md', sm: 'lg' },
  lineHeight: '3',
  color: 'darkGrey1',
  as: 'span',
} as TextProps;
