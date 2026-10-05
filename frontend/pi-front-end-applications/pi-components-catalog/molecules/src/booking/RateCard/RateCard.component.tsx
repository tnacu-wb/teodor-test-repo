import {
  Box,
  BoxProps,
  Divider,
  HStack,
  Link as TextLink,
  Text,
  VStack,
  Flex,
} from '@chakra-ui/react';
import type {
  Channel,
  HIRateClassification,
  HIRoomClass,
  HIRoomRate,
  HIRoomType,
  HIRoomTypeInfoResponse,
  RoomConfiguration,
} from '@whitbread-eos/api';
import {
  Area,
  FT_PI_BB_CCUI_NONSILENT_SUBSTITUTION_PER_ROOMCLASS,
  FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE,
  FT_PI_ROOM_DETAILS_DRAWER,
} from '@whitbread-eos/api';
import { Card } from '@whitbread-eos/atoms';
import {
  formatAssetsUrl,
  akamaiImageLoader,
  isIVMEnabled,
  useFeatureToggle,
  useSemanticTypography,
  getRoomRatesWithRateInformation,
  hasSameRoomTypeCodes,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import Image from 'next/image';
import Link from 'next/link';
import { useMemo, useState } from 'react';

import SilentSubstitutionNotification from '../../hotel-details/Notifications/SilentSubstitutionNotification';
import RoomsRemainingBadgeComponent from '../../hotel-details/RoomsRemainingBadge';
import { RoomSideDrawer } from '../../hotel-details/SideDrawer';
import RateItem from '../RateItem';

interface Props {
  brand: string;
  channel?: Channel;
  roomClassCode: string;
  roomClass: HIRoomClass;
  pmsRoomType: string;
  roomRates: HIRoomRate[];
  numberOfNights?: number;
  numberOfUnits?: number;
  showAdditionalInfo?: boolean;
  roomTypes?: string[];
  rateClassifications: HIRateClassification[];
  selectedRoomClassAndRate: string;
  setSelectedRoomClassAndRate: (selectedClassAndRate: string) => void;
  roomTypeInformationResponse: HIRoomTypeInfoResponse;
  isLessThanSm: boolean | undefined;
  isLessThanMd: boolean | undefined;
  roomRatesThatMatchRoomClassifications: HIRoomRate[];
  numberOfRoomsAvailable?: number;
  allRoomsStaticDetails?: RoomConfiguration;
  isUrgencyBannerEnabled?: boolean;
}

export default function RateCard({
  brand,
  channel,
  roomClassCode,
  roomClass,
  pmsRoomType,
  roomRates,
  numberOfNights,
  numberOfUnits,
  roomTypes,
  showAdditionalInfo,
  rateClassifications,
  selectedRoomClassAndRate,
  setSelectedRoomClassAndRate,
  roomTypeInformationResponse,
  isLessThanSm,
  isLessThanMd,
  roomRatesThatMatchRoomClassifications,
  numberOfRoomsAvailable,
  allRoomsStaticDetails,
  isUrgencyBannerEnabled,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const getTypographyProps = useSemanticTypography();
  const roomTypeTitleTypographyProps = getTypographyProps(
    roomTypeTitleLegacyTypography,
    roomTypeTitleSemanticTypography
  );
  const isSemanticRoomTypeTitle = 'textStyle' in roomTypeTitleTypographyProps;

  const isBB = channel?.toLowerCase() === Area.BB;
  const isPI = channel?.toLowerCase() === Area.PI;

  const {
    [FT_PI_BB_CCUI_NONSILENT_SUBSTITUTION_PER_ROOMCLASS]:
      isNonSilentSubstituNotificPerRoomClassEnabled,
    [FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE]: isPremPlusAccFeatureFlag,
    [FT_PI_ROOM_DETAILS_DRAWER]: isRoomDetailsDrawerEnabled,
  } = useFeatureToggle();

  const roomRatesWithRateInformation = useMemo(
    () =>
      getRoomRatesWithRateInformation(roomRatesThatMatchRoomClassifications, rateClassifications),
    [roomRatesThatMatchRoomClassifications, rateClassifications]
  );

  const MIN_SHOWN_ROOM_RATES = isBB ? 4 : roomRatesWithRateInformation?.length;
  const [showRates, setShowRates] = useState(MIN_SHOWN_ROOM_RATES);
  const [showRoomDrawer, setShowRoomDrawer] = useState(false);

  if (roomTypeInformationResponse.isLoadingRoomTypeInformation) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (roomTypeInformationResponse?.isErrorRoomTypeInformation) {
    return <Text>{(roomTypeInformationResponse?.errorRoomTypeInformation as Error).message}</Text>;
  }

  const roomClassInformation =
    roomTypeInformationResponse?.dataRoomTypeInformation?.roomTypeInformation?.roomTypes.find(
      (roomClassInfo) =>
        roomClassInfo?.roomTypeCode === pmsRoomType ||
        roomClassInfo?.roomTypeCode.includes(pmsRoomType)
    );

  const matchingTabItem =
    allRoomsStaticDetails?.tabItems?.find((tabItem) =>
      hasSameRoomTypeCodes(tabItem?.roomTypeCode, pmsRoomType)
    ) ?? {};

  const foundMatchingTabItem = matchingTabItem && Object.keys(matchingTabItem).length > 0;

  const roomBaseName =
    allRoomsStaticDetails?.tabGroups?.find(
      (roomGroupConfig) => roomGroupConfig.groupId === roomClassInformation?.groupId
    )?.groupName ?? '';

  const descriptionNoOfLinesBigScreens = isLessThanMd ? 3 : undefined;

  const handleShowMoreRates = () => {
    if (showRates < roomRatesWithRateInformation?.length) {
      setShowRates(roomRatesWithRateInformation?.length);
    } else {
      setShowRates(MIN_SHOWN_ROOM_RATES);
    }
  };
  const showMoreRatesMsg = `${t('pihotelinfo.show')} ${
    roomRatesWithRateInformation?.length - MIN_SHOWN_ROOM_RATES
  } ${t('pihotelinfo.moreRates')}`;

  const getFirstRoomRateDataPerRoomClass = () => {
    if (!roomRates?.length) return [];

    return (
      roomRates?.[0].roomTypes?.flatMap((roomType: HIRoomType) =>
        roomType?.rooms
          ?.filter((room) => room?.roomClass === roomClassCode && !room.silentSubstitution)
          .map((filteredRoom) => ({
            silentSubstitution: filteredRoom?.silentSubstitution,
            roomLabelCode: filteredRoom?.pmsRoomType,
          }))
      ) || []
    );
  };

  return (
    <Box mb="2xl">
      {isNonSilentSubstituNotificPerRoomClassEnabled &&
        renderNonSilentSubstitutionNotificationPerRoomClass()}
      <Card>
        <VStack
          w="full"
          py={{ base: '0', sm: 'lg' }}
          px={{ base: '0', sm: 'xl' }}
          data-testid="hdp_rateCard"
        >
          {renderRateCardDescription()}
          <Box w="full">
            <VStack>
              {roomRatesWithRateInformation
                ?.slice(0, showRates)
                .map((roomRate: HIRoomRate, index: number) => {
                  // index: index of the sorted rates, not availability index
                  // rateIndexFromAvailability: required for the correct radio values
                  const rateIndexFromAvailability = roomRates?.findIndex(
                    (rate) => rate?.ratePlanCode === roomRate?.ratePlanCode
                  );
                  const totalReservationAmount = roomRate?.roomTypes
                    ?.map(
                      (roomType) =>
                        roomType.rooms.filter((room) => room?.roomClass === roomClassCode)[0]
                          ?.roomPriceBreakdown?.totalNetAmount
                    )
                    ?.reduce((sum, pricePerRoomType) => sum + pricePerRoomType, 0);

                  const totalBaseAmount = roomRate?.roomTypes
                    ?.map(
                      (roomType) =>
                        roomType.rooms.filter((room) => room?.roomClass === roomClassCode)[0]
                          ?.roomPriceBreakdown?.baseRateAmount || 0
                    )
                    ?.reduce((sum, baserateAmount) => sum + baserateAmount, 0);

                  return (
                    <Box w="full" key={roomRate?.cellCode}>
                      <RateItem
                        brand={brand}
                        value={`${roomClassCode}-${rateIndexFromAvailability}`}
                        roomRate={roomRate}
                        rateClassification={
                          rateClassifications.find(
                            (el) =>
                              el?.rateClassification === roomRate?.ratePlanCode ||
                              el?.ratePlanCode === roomRate?.ratePlanCode
                          ) as HIRateClassification
                        }
                        selectedRoomClassAndRate={selectedRoomClassAndRate}
                        setSelectedRoomClassAndRate={setSelectedRoomClassAndRate}
                        totalReservationAmount={totalReservationAmount}
                        totalBaseAmount={totalBaseAmount}
                        numberOfNights={numberOfNights}
                        numberOfUnits={numberOfUnits}
                      />
                      {index !== roomRatesWithRateInformation?.length - 1 && <Divider my="md" />}
                    </Box>
                  );
                })}
            </VStack>
            {roomRatesWithRateInformation?.length > MIN_SHOWN_ROOM_RATES && isBB ? (
              <Box textAlign="center" mt={{ base: '0', sm: 'md' }}>
                <TextLink onClick={handleShowMoreRates}>
                  <Text {...seeDetailsLinkStyles} data-testid="hdp_roomTypeShowMoreRatesLink">
                    {showRates < roomRatesWithRateInformation?.length
                      ? showMoreRatesMsg
                      : t('content.showLess')}
                  </Text>
                </TextLink>
              </Box>
            ) : null}
          </Box>
        </VStack>
      </Card>
    </Box>
  );

  function renderRateCardDescription() {
    const shouldDisplayDescAndLink = isPremPlusAccFeatureFlag
      ? !!roomTypes?.length
      : !(roomTypes && roomTypes?.length > 1);
    const isMobile = !!isLessThanMd;
    return (
      /* When multiple room types are available do not show the rate type, image and description */
      /*** As it is required for prem plus UA type section display, enabling this display */
      shouldDisplayDescAndLink && (
        <>
          {isPI && isMobile && (
            <RoomsRemainingBadgeComponent numberOfRoomsAvailable={numberOfRoomsAvailable} />
          )}
          <HStack alignItems="flex-start" mb="lg" w="full">
            <Box flex="4" mr="lg">
              <Box w="full" mb={{ base: '0', sm: 'md' }}>
                {isUrgencyBannerEnabled && isPI && !isMobile && (
                  <RoomsRemainingBadgeComponent numberOfRoomsAvailable={numberOfRoomsAvailable} />
                )}
                <Text
                  {...roomTypeTitleTypographyProps}
                  {...(!isSemanticRoomTypeTitle ? { as: 'b' } : {})}
                  data-testid="hdp_roomTypeTitle"
                >
                  {roomClass}
                </Text>
              </Box>
              {showAdditionalInfo && (
                <Text
                  {...getTypographyProps(
                    roomTypeDescriptionLegacyTypography,
                    roomTypeDescriptionSemanticTypography
                  )}
                  noOfLines={isLessThanSm ? 2 : descriptionNoOfLinesBigScreens}
                  data-testid="hdp_roomTypeDescription"
                >
                  {roomClassInformation?.roomDescription}
                </Text>
              )}

              <Box mt={{ base: '0', sm: 'md' }}>
                {isRoomDetailsDrawerEnabled && isPI ? (
                  foundMatchingTabItem && (
                    <Flex onClick={() => setShowRoomDrawer(true)}>
                      <Text
                        {...roomTypeSeeDetailsLayoutStyles}
                        {...getTypographyProps(
                          roomTypeSeeDetailsLegacyTypography,
                          roomTypeSeeDetailsSemanticTypography
                        )}
                        data-testid="See-Drawer-Details-Button"
                      >
                        {t('pihotelinfo.seeDetails')}
                      </Text>
                    </Flex>
                  )
                ) : (
                  <Link href="#hotel-details-our-rooms" passHref legacyBehavior>
                    <Text
                      {...roomTypeSeeDetailsLayoutStyles}
                      {...getTypographyProps(
                        roomTypeSeeDetailsLegacyTypography,
                        roomTypeSeeDetailsSemanticTypography
                      )}
                      data-testid="hdp_roomTypeSeeDetailsLink"
                    >
                      {t('pihotelinfo.seeDetails')}
                    </Text>
                  </Link>
                )}
              </Box>
            </Box>

            {isRoomDetailsDrawerEnabled && isPI && (
              <RoomSideDrawer
                visible={showRoomDrawer}
                onClose={() => setShowRoomDrawer(false)}
                title={roomBaseName}
                isPremierPlus={roomClassCode === 'PP'}
                roomStaticDetails={matchingTabItem}
                brand={brand}
              />
            )}

            {showAdditionalInfo && roomClassInformation?.roomImage && (
              <Box {...imageContainerStyle} data-testid="hdp_roomTypeImage">
                <Image
                  src={formatAssetsUrl(roomClassInformation?.roomImage)}
                  alt="Room type Image"
                  fill
                  style={{ objectFit: 'cover' }}
                  priority
                  loader={isIVMEnabled() ? akamaiImageLoader : undefined}
                />
              </Box>
            )}
          </HStack>
        </>
      )
    );
  }

  function renderNonSilentSubstitutionNotificationPerRoomClass() {
    const firstRoomRateDataPerRoomClass = getFirstRoomRateDataPerRoomClass();
    if (!firstRoomRateDataPerRoomClass?.length) {
      return null;
    }
    const nonSilentSubstitutedRooms = getNonSilentSubstitutedRooms(firstRoomRateDataPerRoomClass);
    if (!nonSilentSubstitutedRooms.length) {
      return null;
    }
    return (
      <SilentSubstitutionNotification
        brand={brand}
        roomTypeInformationResponse={roomTypeInformationResponse}
        substitutedRooms={nonSilentSubstitutedRooms}
      />
    );
  }
}

export function getNonSilentSubstitutedRooms(
  firstRoomRateDataPerRoomClass: { silentSubstitution: boolean; roomLabelCode: string }[]
) {
  const nonSilentSubstitutedRoom: string[] = [];
  firstRoomRateDataPerRoomClass?.forEach((room) => {
    if (!room?.silentSubstitution && !nonSilentSubstitutedRoom?.includes(room?.roomLabelCode)) {
      nonSilentSubstitutedRoom.push(room?.roomLabelCode);
    }
  });
  return nonSilentSubstitutedRoom;
}

const imageContainerStyle = {
  pos: 'relative',
  w: { base: '28', sm: '19rem', md: '23rem' },
  h: { base: '24', sm: '52' },
  ml: 'lg',
  roundedTopRight: 'xs',
} as BoxProps;

const seeDetailsLinkStyles = {
  display: 'inline-block',
  fontSize: 'sm',
  lineHeight: '2',
  textDecoration: 'underline',
  color: 'btnSecondaryEnabled',
  _hover: {
    cursor: 'pointer',
  },
};

const roomTypeTitleLegacyTypography = {
  fontSize: { base: 'md', sm: 'lg', lg: 'xl' },
  lineHeight: '3',
};

const roomTypeTitleSemanticTypography = {
  textStyle: 'heading-s',
};

const roomTypeDescriptionLegacyTypography = {
  fontSize: { base: 'sm', lg: 'md' },
  lineHeight: { base: '2', lg: '3' },
};

const roomTypeDescriptionSemanticTypography = {
  textStyle: 'body-m-regular',
};

const roomTypeSeeDetailsLayoutStyles = {
  display: 'inline-block',
  textDecoration: 'underline',
  color: 'btnSecondaryEnabled',
  _hover: {
    cursor: 'pointer',
  },
};

const roomTypeSeeDetailsLegacyTypography = {
  fontSize: 'sm',
  lineHeight: '2',
};

const roomTypeSeeDetailsSemanticTypography = {
  textStyle: 'link-s-regular',
};
