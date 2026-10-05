import { Box, BoxProps, Divider, Flex, FlexProps, Text, TextProps } from '@chakra-ui/react';
import {
  EARLY_CHECKIN_IDS,
  LATE_CHECKOUT_IDS,
  ExtrasPackages,
  HotelBrand,
  PackagesSelection,
  RoomPackageSelection,
} from '@whitbread-eos/api';
import {
  extrasNamingCheck,
  formatCurrency,
  formatDataTestId,
  formatDate,
  formatPrice,
  getExtrasPackagePrice,
  useSemanticTypography,
  useCustomLocale,
} from '@whitbread-eos/utils';

interface Props extends BoxProps {
  t: (x: string, y?: { [key: string]: string }) => string;
  currentLang: string | undefined;
  leadGuestTitle: string;
  leadGuestName: string;
  roomType: string;
  roomTypeDescription: string | null;
  rateType: string;
  rateTypeDescription: string;
  roomGroup: string;
  roomReservationEndDate: string;
  roomTotalPrice: number;
  ratesPerNight: { pricePerNight: number; startDate: string; cityTaxPerNight: number }[];
  roomNumber: number;
  adultMealDescription: string[];
  childrenMealDescription: string[];
  mealPrice: number;
  extrasRoomSelection?: RoomPackageSelection[];
  packagesExtrasItems?: ExtrasPackages[];
  currency: string;
  taxesMessage?: string;
  brand?: string;
}

export default function RoomCardInfo({
  leadGuestTitle,
  leadGuestName,
  roomType,
  roomTypeDescription,
  rateType,
  rateTypeDescription,
  roomGroup,
  roomReservationEndDate,
  roomTotalPrice,
  roomNumber,
  ratesPerNight,
  adultMealDescription,
  childrenMealDescription,
  mealPrice,
  extrasRoomSelection,
  packagesExtrasItems,
  currency,
  taxesMessage,
  brand,
  currentLang,
  t,
}: Readonly<Props>) {
  const baseDataTestId = formatDataTestId('RoomCardInfo', `room${roomNumber}`);
  const leftId = (str: string) => formatDataTestId('LeftColumn', str);
  const rightId = (str: string) => formatDataTestId('RightColumn', str);
  const { language } = useCustomLocale();
  const getTypographyProps = useSemanticTypography();

  const currentRoom = extrasRoomSelection?.[roomNumber - 1];

  const extrasLabelCheck = (extrasId: string) =>
    currentRoom?.packagesSelection.some((extrasPackage: PackagesSelection) =>
      extrasPackage?.id?.includes(extrasId)
    );

  return (
    <Box data-testid={baseDataTestId} p="lg" pt={0}>
      <Flex {...columnsStyle}>
        {/* left column */}
        <Box data-testid={formatDataTestId(baseDataTestId, leftId('Main'))} {...leftColumnStyle}>
          <Box {...leftColBoxStyle}>
            <Text
              data-testid={formatDataTestId(baseDataTestId, leftId('LeadGuest-Label'))}
              {...getTypographyProps(
                leadGuestLabelLegacyTypography,
                leadGuestLabelSemanticTypography
              )}
            >
              {t('booking.confirmation.leadGuest')}
            </Text>
            <Text
              data-testid={formatDataTestId(baseDataTestId, leftId('LeadGuest-Name'))}
              {...getTypographyProps(
                leadGuestNameLegacyTypography,
                leadGuestNameSemanticTypography
              )}
              className="sessioncamhidetext assist-no-show"
            >
              {leadGuestTitle} {leadGuestName}
            </Text>
          </Box>

          <Box {...leftColBoxStyle}>
            <Text
              data-testid={formatDataTestId(baseDataTestId, leftId('RoomLabel'))}
              {...getTypographyProps(roomLabelLegacyTypography, roomLabelSemanticTypography)}
            >
              {t('booking.confirmation.yourRoom')}
            </Text>
            <Text
              data-testid={formatDataTestId(baseDataTestId, leftId('RoomDetails'))}
              {...getTypographyProps({}, roomDetailsSemanticTypography)}
            >
              <Text as="b">{roomType}</Text>
              {roomTypeDescription && ` - ${roomTypeDescription}`}
            </Text>
          </Box>

          <Box {...leftColBoxStyle}>
            <Text
              data-testid={formatDataTestId(baseDataTestId, leftId('RoomRate-Label'))}
              {...getTypographyProps(
                roomRateLabelLegacyTypography,
                roomRateLabelSemanticTypography
              )}
            >
              {t('booking.confirmation.yourRate')}
            </Text>
            <Text
              data-testid={formatDataTestId(baseDataTestId, leftId('RoomRate-Details'))}
              {...getTypographyProps({}, roomRateDetailsSemanticTypography)}
            >
              <Text as="b">{`${brand === HotelBrand.HUB ? 'hub' : ''} ${rateType}`}</Text> -{' '}
              {rateTypeDescription}
            </Text>
          </Box>

          <Box {...leftColBoxStyle}>
            <Text
              data-testid={formatDataTestId(baseDataTestId, leftId('RoomGroup-Label'))}
              {...getTypographyProps(
                roomGroupLabelLegacyTypography,
                roomGroupLabelSemanticTypography
              )}
            >
              {t('booking.confirmation.yourGroup')}
            </Text>
            <Text
              data-testid={formatDataTestId(baseDataTestId, leftId('RoomGroup-Details'))}
              {...getTypographyProps({}, roomGroupDetailsSemanticTypography)}
            >
              {roomGroup}
            </Text>
          </Box>
        </Box>

        {/* right column */}
        <Flex data-testid={formatDataTestId(baseDataTestId, rightId('Main'))} {...rightColumnStyle}>
          <Box w="full">
            <Text
              data-testid={formatDataTestId(baseDataTestId, rightId('Arrival-Label'))}
              {...getTypographyProps(arrivalLabelLegacyTypography, arrivalLabelSemanticTypography)}
            >
              {t('booking.hotel.summary.arriving')}
            </Text>
            {ratesPerNight?.map((night, index) => (
              <Flex
                direction="column"
                data-testid={formatDataTestId(baseDataTestId, rightId(`RatePerNight-${index}`))}
                key={`${night.startDate}-${index}`}
              >
                <Flex justifyContent="space-between" alignItems="center">
                  <Text
                    {...arrivalDayLayoutStyles}
                    {...getTypographyProps(
                      arrivalDayLegacyTypography,
                      arrivalDaySemanticTypography
                    )}
                    data-testid={formatDataTestId(baseDataTestId, rightId(`Arrival-Day-${index}`))}
                  >
                    {formatDate(night.startDate, 'eeee', currentLang)}
                  </Text>
                  {index === 0 && (
                    <Text
                      data-testid={formatDataTestId(baseDataTestId, rightId('Arrival-Checkin'))}
                      {...arrivalCheckinLayoutStyles}
                      {...getTypographyProps(
                        arrivalCheckinLegacyTypography,
                        arrivalCheckinSemanticTypography
                      )}
                      textAlign="right"
                    >
                      {EARLY_CHECKIN_IDS.some((id) => extrasLabelCheck(id))
                        ? t('ancillaries.booking.summary.checkin')
                        : t('booking.confirmation.checkinTime')}
                    </Text>
                  )}
                </Flex>
                <Flex
                  justify="space-between"
                  alignItems="center"
                  data-testid={formatDataTestId(
                    baseDataTestId,
                    rightId(`Room-PricePerNight-${index}`)
                  )}
                >
                  <Text
                    data-testid={formatDataTestId(baseDataTestId, rightId('Leaving-Date'))}
                    color="darkGrey2"
                    {...getTypographyProps(
                      arrivalDateLegacyTypography,
                      arrivalDateSemanticTypography
                    )}
                  >
                    {language === 'en'
                      ? formatDate(night.startDate, 'd MMM yyyy', currentLang)
                      : formatDate(night.startDate, 'E dd MMM yyyy', currentLang)}
                  </Text>
                  <Text
                    data-testid={formatDataTestId(baseDataTestId, rightId('PricePerNight-CityTax'))}
                    {...getTypographyProps(
                      arrivalPriceLegacyTypography,
                      arrivalPriceSemanticTypography
                    )}
                  >
                    {formatPrice(
                      formatCurrency(currency),
                      ((night?.pricePerNight ?? 0) + (night?.cityTaxPerNight ?? 0)).toFixed(2),
                      language
                    )}
                  </Text>
                </Flex>
                <Divider {...dividerStyle} />
              </Flex>
            ))}
          </Box>

          <Box w="full">
            <Text
              data-testid={formatDataTestId(baseDataTestId, rightId('Leaving-Label'))}
              {...getTypographyProps(leavingLabelLegacyTypography, leavingLabelSemanticTypography)}
            >
              {t('booking.hotel.summary.checkout')}
            </Text>
            <Flex justifyContent="space-between" alignItems="center">
              <Text
                data-testid={formatDataTestId(baseDataTestId, rightId('Leaving-Day'))}
                {...leavingDayLayoutStyles}
                {...getTypographyProps(leavingDayLegacyTypography, leavingDaySemanticTypography)}
              >
                {formatDate(roomReservationEndDate, 'eeee', currentLang)}
              </Text>
              <Text
                data-testid={formatDataTestId(baseDataTestId, rightId('Leaving-CheckOut'))}
                {...leavingCheckoutLayoutStyles}
                {...getTypographyProps(
                  leavingCheckoutLegacyTypography,
                  leavingCheckoutSemanticTypography
                )}
                textAlign="right"
              >
                {LATE_CHECKOUT_IDS.some((id) => extrasLabelCheck(id))
                  ? t('ancillaries.booking.summary.checkout')
                  : t('booking.confirmation.checkoutTime')}
              </Text>
            </Flex>
            <Text
              data-testid={formatDataTestId(baseDataTestId, rightId('Leaving-Date'))}
              color="darkGrey2"
              {...getTypographyProps(leavingDateLegacyTypography, leavingDateSemanticTypography)}
            >
              {language === 'en'
                ? formatDate(roomReservationEndDate, 'd MMM yyyy', currentLang)
                : formatDate(roomReservationEndDate, 'E dd MMM yyyy', currentLang)}
            </Text>
          </Box>

          {currentRoom && currentRoom?.packagesSelection?.length > 0 && (
            <>
              <Divider {...dividerStyle} />
              <Box w="full">
                <Text
                  data-testid={formatDataTestId(baseDataTestId, rightId('Extras-Label'))}
                  fontWeight="bold"
                  fontSize="md"
                >
                  {t('account.dashboard.extras')}
                </Text>

                {currentRoom?.packagesSelection?.map((roomExtras: PackagesSelection) => {
                  const { id } = roomExtras;
                  const extrasLabelsCheck = `${extrasNamingCheck(t, id)}`;

                  return (
                    <Flex justifyContent="space-between" alignItems="center" key={id}>
                      <Text
                        data-testid={formatDataTestId(
                          baseDataTestId,
                          rightId(`${extrasLabelsCheck}`)
                        )}
                        {...extrasRoomStyle}
                      >
                        {extrasLabelsCheck}
                      </Text>
                      <Text
                        data-testid={formatDataTestId(baseDataTestId, rightId('Extras-Price'))}
                        textAlign="right"
                        {...{ extrasRoomStyle, fontWeight: 'bold', fontSize: 'xl' }}
                      >
                        {id && getExtrasPackagePrice(id, language, packagesExtrasItems)}
                      </Text>
                    </Flex>
                  );
                })}
              </Box>
            </>
          )}

          {(adultMealDescription.length > 0 || childrenMealDescription.length > 0) && (
            <>
              <Divider {...dividerStyle} />
              <Box w="full">
                <Text
                  data-testid={formatDataTestId(baseDataTestId, rightId('Meals-Label'))}
                  fontWeight="bold"
                  fontSize="md"
                >
                  {t('booking.hotel.summary.meals')}
                </Text>
                <Flex justifyContent="space-between" alignItems="flex-end">
                  <Flex direction="column">
                    {adultMealDescription.length > 0 &&
                      adultMealDescription.map((adultMealItem) => (
                        <Text
                          data-testid={formatDataTestId(baseDataTestId, rightId('Meals-Adults'))}
                          fontWeight="medium"
                          fontSize="md"
                          key={`adultMealItem-${adultMealItem}`}
                        >
                          {adultMealItem}
                        </Text>
                      ))}
                    {childrenMealDescription.length > 0 &&
                      childrenMealDescription.map((childrenMealItem) => (
                        <Text
                          data-testid={formatDataTestId(baseDataTestId, rightId('Meals-Children'))}
                          fontWeight="medium"
                          fontSize="md"
                          key={`childrenMealItem-${childrenMealItem}`}
                        >
                          {childrenMealItem}
                        </Text>
                      ))}
                  </Flex>
                  <Text
                    data-testid={formatDataTestId(baseDataTestId, rightId('Meals-Cost'))}
                    fontWeight="bold"
                    fontSize="xl"
                  >
                    {formatPrice(formatCurrency(currency), mealPrice.toFixed(2), language)}
                  </Text>
                </Flex>
              </Box>
            </>
          )}
        </Flex>
      </Flex>

      <Flex justifyContent="flex-end" fontSize="xl" mt="md">
        <Text
          data-testid={formatDataTestId(baseDataTestId, rightId('RoomTotalPrice-Label'))}
          mr="sm"
          {...getTypographyProps({}, roomTotalPriceLabelSemanticTypography)}
        >
          {t('booking.confirmation.roomTotalMD').replace('[roomNumber]', roomNumber.toString())}
        </Text>
        <Text
          data-testid={formatDataTestId(baseDataTestId, rightId('RoomTotalPrice-Amount'))}
          {...getTypographyProps(
            roomTotalPriceAmountLegacyTypography,
            roomTotalPriceAmountSemanticTypography
          )}
        >
          {formatPrice(formatCurrency(currency), roomTotalPrice.toFixed(2), language)}
        </Text>
      </Flex>

      {!!taxesMessage?.length && (
        <Flex
          justifyContent="flex-end"
          {...infoTextStyle}
          data-testid={formatDataTestId(baseDataTestId, 'CityTaxMessage')}
        >
          {taxesMessage}
        </Flex>
      )}
    </Box>
  );
}

const leftColBoxStyle = {
  mb: 'xl',
};

const leadGuestLabelLegacyTypography = {
  fontWeight: 'bold',
  fontSize: 'md',
};

const leadGuestLabelSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const leadGuestNameLegacyTypography = {
  fontSize: 'lg',
};

const leadGuestNameSemanticTypography = {
  textStyle: 'body-m-regular',
};

const roomLabelLegacyTypography = {
  fontWeight: 'bold',
  fontSize: 'md',
};

const roomLabelSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const roomDetailsSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const roomRateLabelLegacyTypography = {
  fontWeight: 'bold',
  fontSize: 'md',
};

const roomRateLabelSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const roomRateDetailsSemanticTypography = {
  textStyle: 'body-m-regular',
};

const roomGroupLabelLegacyTypography = {
  fontWeight: 'bold',
  fontSize: 'md',
};

const roomGroupLabelSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const roomGroupDetailsSemanticTypography = {
  textStyle: 'body-m-regular',
};

const arrivalLabelLegacyTypography = {
  fontWeight: 'bold',
  fontSize: 'md',
};

const arrivalLabelSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const arrivalDayLayoutStyles = {
  color: 'btnSecondaryEnabled',
};

const arrivalDayLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: 'lg',
};

const arrivalDaySemanticTypography = {
  textStyle: 'body-m-regular',
};

const arrivalCheckinLayoutStyles = {
  w: { sm: 'full', xs: '7.875rem', mobile: '6.125rem' },
};

const arrivalCheckinLegacyTypography = {
  fontWeight: 'medium',
  fontSize: 'md',
};

const arrivalCheckinSemanticTypography = {
  textStyle: 'body-m-regular',
};

const arrivalDateLegacyTypography = {
  fontWeight: 'medium',
  fontSize: 'md',
};

const arrivalDateSemanticTypography = {
  textStyle: 'body-m-regular',
};

const arrivalPriceLegacyTypography = {
  fontSize: 'xl',
  fontWeight: 'bold',
};

const arrivalPriceSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const leavingLabelLegacyTypography = {
  fontWeight: 'bold',
  fontSize: 'md',
};

const leavingLabelSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const leavingDayLayoutStyles = {
  color: 'btnSecondaryEnabled',
};

const leavingDayLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: 'lg',
};

const leavingDaySemanticTypography = {
  textStyle: 'body-m-regular',
};

const leavingCheckoutLayoutStyles = {
  w: { sm: 'full', xs: '7.875rem', mobile: '6.125rem' },
};

const leavingCheckoutLegacyTypography = {
  fontWeight: 'medium',
  fontSize: 'md',
};

const leavingCheckoutSemanticTypography = {
  textStyle: 'body-m-regular',
};

const leavingDateLegacyTypography = {
  fontWeight: 'medium',
  fontSize: 'md',
};

const leavingDateSemanticTypography = {
  textStyle: 'body-m-regular',
};

const roomTotalPriceLabelSemanticTypography = {
  textStyle: 'body-l-regular',
};

const roomTotalPriceAmountLegacyTypography = {
  fontWeight: 'bold',
};

const roomTotalPriceAmountSemanticTypography = {
  textStyle: 'body-l-emphasis',
};

const rightColumnStyle = {
  w: { xl: '33.5rem', mobile: 'full' },
  backgroundColor: 'lightGrey5',
  p: 'lg',
  direction: 'column',
  h: 'fit-content',
  mb: { xl: '0', mobile: '2xl' },
} as FlexProps;

const dividerStyle = {
  my: 'sm',
  borderColor: 'lightGrey3',
};

const columnsStyle = {
  justifyContent: 'space-between',
  direction: {
    xl: 'row',
    mobile: 'column',
  },
} as FlexProps;

const leftColumnStyle = {
  w: { xl: '12.5rem', mobile: 'full' },
  mb: { xl: '0', mobile: '3xl' },
};

const infoTextStyle = {
  justifyContent: 'flex-end',
  fontWeight: 'medium',
  lineHeight: '2',
  color: 'darkGrey1',
  fontSize: 'sm',
} as TextProps;

const extrasRoomStyle = {
  fontWeight: 'medium',
  fontSize: 'md',
  w: { sm: 'full', xs: '7.875rem', mobile: '6.125rem' },
};
