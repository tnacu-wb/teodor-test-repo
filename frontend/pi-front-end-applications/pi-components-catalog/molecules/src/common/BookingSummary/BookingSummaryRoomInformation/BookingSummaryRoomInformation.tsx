import type { DividerProps, TextProps } from '@chakra-ui/react';
import { Box, Divider, Flex, Text } from '@chakra-ui/react';
import {
  SilentSubstitutionLocalStorage,
  BookingSummaryRoomInformationProps,
  FS_SILENT_SUBSTITUTION,
  PackagesSelection,
} from '@whitbread-eos/api';
import { Accessible32, Notification } from '@whitbread-eos/atoms';
import {
  formatDataTestId,
  useFeatureSwitch,
  getCurrentReservationStorageData,
  displayStorageSubstitutionLabels,
  extrasNamingCheck,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import { useCallback, useEffect, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';

export interface Props {
  t: (x: string, y?: { [key: string]: string }) => string;
  roomInformation: BookingSummaryRoomInformationProps[];
  prefixDataTestId?: string;
  isExtrasDisplayed?: boolean;
}

export default function BookingSummaryRoomInformation({
  t,
  roomInformation,
  prefixDataTestId,
  isExtrasDisplayed,
}: Readonly<Props>) {
  const getTypographyProps = useSemanticTypography();

  const getAdultsPluralLabel = useCallback(
    (value: number) => (value === 1 ? t('account.dashboard.adult') : t('account.dashboard.adults')),
    [t]
  );

  const getChildrenPluralLabel = useCallback(
    (value: number) =>
      value === 1 ? t('booking.mealChoose.child') : t('account.dashboard.children'),
    [t]
  );

  const baseDataTestId = formatDataTestId(prefixDataTestId, 'RoomInformation');
  const hotelPhoneNumber = t('booking.hotel.summary.accessibleContact');

  const isSilentFeatureFlagEnabled = useFeatureSwitch({
    featureSwitchKey: FS_SILENT_SUBSTITUTION,
  });

  const router = useRouter();
  const reservationId = (router?.query?.reservationId as string) || '';

  const [currentReservationRoomData, setCurrentReservationRoomData] =
    useState<SilentSubstitutionLocalStorage | null>(null);

  // Defer localStorage read to useEffect to avoid SSR/client hydration mismatch
  useEffect(() => {
    if (isSilentFeatureFlagEnabled) {
      setCurrentReservationRoomData(getCurrentReservationStorageData(reservationId));
    }
  }, [isSilentFeatureFlagEnabled, reservationId]);

  return (
    <Flex flexDirection="column" data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
      {roomInformation.map((room: BookingSummaryRoomInformationProps, index: number) => (
        <Box key={room.roomName}>
          <Box py={{ mobile: 'md', lg: 'lg' }}>
            <Divider {...dividerStyles} />
          </Box>

          <Box>
            <Text
              {...roomInfoLayoutStyle}
              {...getTypographyProps(roomNumberLegacyTypography, roomNumberSemanticTypography)}
              data-testid={formatDataTestId(baseDataTestId, 'RoomNumber')}
            >
              {t('booking.summary.room')} {`${index + 1} `}
              {room.roomName && (
                <Text
                  as="span"
                  {...roomInfoLayoutStyle}
                  {...getTypographyProps(
                    roomInfoRegularLegacyTypography,
                    roomInfoRegularSemanticTypography
                  )}
                  data-testid={formatDataTestId(baseDataTestId, 'RoomName')}
                >
                  (
                  {displayStorageSubstitutionLabels(
                    currentReservationRoomData?.value?.[index],
                    room.roomName,
                    isSilentFeatureFlagEnabled
                  )}
                  )
                </Text>
              )}
            </Text>
            {!!room.nrAdults && (
              <Text
                {...roomInfoLayoutStyle}
                {...getTypographyProps(
                  roomInfoRegularLegacyTypography,
                  roomInfoRegularSemanticTypography
                )}
                data-testid={formatDataTestId(baseDataTestId, 'AdultsNumber')}
              >{`${room.nrAdults} ${getAdultsPluralLabel(room.nrAdults)}`}</Text>
            )}

            {!!room.nrChildren && room.nrChildren > 0 && (
              <Text
                {...roomInfoLayoutStyle}
                {...getTypographyProps(
                  roomInfoRegularLegacyTypography,
                  roomInfoRegularSemanticTypography
                )}
                data-testid={formatDataTestId(baseDataTestId, 'ChildrenNumber')}
              >{`${room.nrChildren} ${getChildrenPluralLabel(room.nrChildren)}`}</Text>
            )}

            {room.selectedMeals && room.selectedMeals.adultsMeals?.length === 0 && (
              <Text
                {...roomInfoLayoutStyle}
                pt="sm"
                {...getTypographyProps(
                  roomInfoSmallLegacyTypography,
                  roomInfoSmallSemanticTypography
                )}
                data-testid={formatDataTestId(baseDataTestId, 'NoMealsSelected')}
              >
                {t('booking.summary.noMeals')}
              </Text>
            )}

            {room?.selectedExtrasList &&
              room?.selectedExtrasList?.packagesSelection?.length === 0 &&
              isExtrasDisplayed && (
                <Text
                  {...roomInfoLayoutStyle}
                  pt="sm"
                  {...getTypographyProps(
                    roomInfoSmallLegacyTypography,
                    roomInfoSmallSemanticTypography
                  )}
                  data-testid={formatDataTestId(baseDataTestId, 'NoExtrasSelected')}
                >
                  {t('ancillaries.extras.not.available')}
                </Text>
              )}
            {room.selectedMeals && room.selectedMeals.adultsMeals.length > 0 && (
              <Box pt="sm">
                {room.selectedMeals.adultsMeals?.map((adultMeal) => (
                  <Text
                    key={uuidv4()}
                    {...roomInfoLayoutStyle}
                    {...getTypographyProps(
                      roomInfoSmallLegacyTypography,
                      roomInfoSmallSemanticTypography
                    )}
                    data-testid={formatDataTestId(baseDataTestId, 'AdultMeal')}
                  >{`${adultMeal.title} ${t('upsell.label.for')} ${
                    adultMeal.noSelections
                  } ${getAdultsPluralLabel(adultMeal.noSelections ?? 0)}`}</Text>
                ))}
              </Box>
            )}
            {room?.selectedMeals?.childrenMeals.map((childMeal) => (
              <Text
                key={uuidv4()}
                {...roomInfoLayoutStyle}
                {...getTypographyProps(
                  roomInfoSmallLegacyTypography,
                  roomInfoSmallSemanticTypography
                )}
                data-testid={formatDataTestId(baseDataTestId, 'ChildrenMeal')}
              >{`${childMeal.title} ${t('upsell.label.for')} ${
                childMeal.noSelections
              } ${getChildrenPluralLabel(childMeal.noSelections ?? 0)}`}</Text>
            ))}

            {room?.selectedExtrasList &&
              room?.selectedExtrasList?.packagesSelection?.length > 0 &&
              isExtrasDisplayed && (
                <>
                  {room?.selectedExtrasList?.packagesSelection
                    ?.sort()
                    ?.map((extrasPackage: PackagesSelection) => {
                      return (
                        <Box key={extrasPackage?.id} pt="sm">
                          <Text
                            {...roomInfoLayoutStyle}
                            {...getTypographyProps(
                              roomInfoSmallLegacyTypography,
                              roomInfoSmallSemanticTypography
                            )}
                            data-testid={formatDataTestId(baseDataTestId, extrasPackage?.id)}
                          >
                            {extrasNamingCheck(t, extrasPackage?.id)}
                          </Text>
                        </Box>
                      );
                    })}
                </>
              )}

            {room?.accessibleRoom?.isAccessible && room?.accessibleRoom?.phoneNumber && (
              <Box mt="lg">
                <Notification
                  maxWidth="full"
                  status="info"
                  description={hotelPhoneNumber.replace(
                    '{{PhoneNumber.hotelNumber}}',
                    room.accessibleRoom.phoneNumber
                  )}
                  variant="accessible"
                  svg={<Accessible32 />}
                />
              </Box>
            )}
          </Box>
        </Box>
      ))}
    </Flex>
  );
}

const roomInfoLayoutStyle = {
  color: 'darkGrey1',
} as TextProps;

const roomNumberLegacyTypography = {
  lineHeight: '3',
  fontSize: 'md',
  fontWeight: 'semibold',
} as TextProps;

const roomNumberSemanticTypography = {
  textStyle: 'body-m-emphasis',
} as TextProps;

const roomInfoRegularLegacyTypography = {
  fontWeight: 'normal',
  fontSize: 'md',
} as TextProps;

const roomInfoRegularSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;

const roomInfoSmallLegacyTypography = {
  fontWeight: 'normal',
  fontSize: 'sm',
  lineHeight: 2,
} as TextProps;

const roomInfoSmallSemanticTypography = {
  textStyle: 'body-s-regular',
} as TextProps;

const dividerStyles = {
  borderColor: 'lightGrey4',
  opacity: '1',
} as DividerProps;
