import { Box, StyleProps, Text } from '@chakra-ui/react';
import type {
  HIHotelInventoryResponse,
  RoomConfiguration,
  HIRoomTypeInfoResponse,
  TabGroup,
} from '@whitbread-eos/api';
import { ROOM_TYPE, FT_PI_BB_CCUI_ROOMS_DISCLAIMER } from '@whitbread-eos/api';
import { Section, Tabs, Button, Notification, Info } from '@whitbread-eos/atoms';
import { useFeatureToggle, renderSanitizedHtml, useSemanticTypography } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useState } from 'react';

import { HotelRoomContent } from '../HotelRoomContent';

interface Props {
  isLoading: boolean;
  isError: boolean;
  data: RoomConfiguration | undefined;
  error: unknown;
  isPremierInn: boolean;
  isLessThanSm: boolean | undefined;
  isLessThanMd: boolean | undefined;
  isLessThanLg: boolean | undefined;
  hotelInventoryResponse?: HIHotelInventoryResponse;
  roomTypeInformationResponse?: HIRoomTypeInfoResponse;
  isDisplayRates?: boolean;
  brand: string;
}

export default function HotelRoomsComponent({
  isLoading,
  isError,
  data,
  error,
  isPremierInn,
  isLessThanSm,
  isLessThanMd,
  isLessThanLg,
  hotelInventoryResponse,
  roomTypeInformationResponse,
  isDisplayRates,
  brand,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const { [FT_PI_BB_CCUI_ROOMS_DISCLAIMER]: isRoomsDisclaimerEnabled } = useFeatureToggle();
  const [isHydrated, setIsHydrated] = useState(false);
  useEffect(() => {
    setIsHydrated(true);
  }, []);

  const getTypographyProps = useSemanticTypography();

  const headingLegacyTypography = {
    fontWeight: 'semibold',
    fontSize: { base: '2xl', md: '3xl' },
    lineHeight: '4',
  };

  const headingSemanticTypography = {
    textStyle: 'heading-m',
  };

  const disclaimerLegacyTypography = {
    fontSize: 'sm',
  };

  const disclaimerSemanticTypography = {
    textStyle: 'body-s-regular',
  };

  const tabsStyles = () => ({
    '.chakra-tabs__tablist': {
      w: {
        xl: `calc(100% / 2 - 1.5rem + 1.5rem / 2)`,
        lg: `calc(100% / 2 - 1.5rem + 1.5rem / 2)`,
        md: '39.813rem',
        mobile: 'full',
      },
      borderBottom: 'none',
    },
    '.chakra-tabs__tab': {
      w: 'full',
      maxWidth: {
        sm: '15rem',
        xs: '8.4rem',
        mobile: '4.65rem',
      },
      borderBottom: '2px solid var(--chakra-colors-lightGrey2)',
      _selected: {
        fontWeight: 'semibold',
        borderBottom: '2px solid var(--chakra-colors-primary)',
      },
    },
    '.chakra-tabs__tab h4': {
      maxWidth: {
        sm: '10rem',
        xs: '5.5rem',
        mobile: '4.65rem',
      },
      textOverflow: {
        xs: 'inherit',
        mobile: 'ellipsis',
      },
      whiteSpace: {
        xs: 'normal',
        mobile: 'nowrap',
      },
      overflow: {
        xs: 'inherit',
        mobile: 'hidden',
      },
      fontSize: {
        sm: '1rem',
        mobile: '.9375rem',
      },
    },
  });

  return (
    <Section dataTestId="hdp_ourRooms">
      <Box pb="4" pt="3xl" id="hotel-details-our-rooms">
        <Text
          as="h3"
          data-testid="hdp_ourRooms-title"
          {...getTypographyProps(headingLegacyTypography, headingSemanticTypography)}
        >
          {t('hoteldetails.roomtypes.title')}
        </Text>
        {renderTabs()}
        {renderSeeRatesButton(isDisplayRates)}
        {isRoomsDisclaimerEnabled && renderRoomsDisclaimer(brand)}
      </Box>
    </Section>
  );

  function renderTabs() {
    if (isLoading) {
      return <Text>{t('searchresults.list.hotel.loading')}</Text>;
    }

    if (isError) {
      return <Text>{(error as Error).message}</Text>;
    }

    if (!data) {
      return null;
    }

    // hotelInventory query
    if (hotelInventoryResponse) {
      if (hotelInventoryResponse?.isLoadingHotelInventory) {
        return (
          <Text data-testid="hotel-inventory-loading-message">
            {t('searchresults.list.hotel.loading')}
          </Text>
        );
      }

      if (hotelInventoryResponse?.isErrorHotelInventory) {
        return <Text>{(hotelInventoryResponse.errorHotelInventory as Error).message}</Text>;
      }
    }

    // AEM roomTypeInformation query
    if (roomTypeInformationResponse) {
      if (roomTypeInformationResponse?.isLoadingRoomTypeInformation) {
        return (
          <Text data-testid="room-types-loading-message">
            {t('searchresults.list.hotel.loading')}
          </Text>
        );
      }

      if (roomTypeInformationResponse?.isErrorRoomTypeInformation) {
        return (
          <Text>{(roomTypeInformationResponse.errorRoomTypeInformation as Error).message}</Text>
        );
      }
    }

    return (
      <Tabs
        mt={6}
        pt={4}
        showRoomInventory={!isPremierInn}
        prefixDataTestId="roomConfiguration"
        sx={{ ...tabsStyles() }}
        labelStyles={{
          selected: { textStyle: 'body-m-emphasis' },
          unselected: { textStyle: 'body-m-regular' },
        }}
        styles={{
          tab: {
            w: 'full',
            maxWidth: {
              sm: '15rem',
              xs: '8.4rem',
              mobile: 'unset',
            },
            borderBottom: '2px solid var(--chakra-colors-lightGrey2)',
            pr: { md: '0', mobile: '1.5rem' },
            pl: { md: '0', mobile: '1.5rem' },
            mb: 0,
            _selected: {
              fontWeight: 'semibold',
              borderBottom: '2px solid var(--chakra-colors-primary)',
            },
          } as StyleProps,
          tabList: {
            w: {
              xl: `calc(98% / 2 - 1.5rem + 1.5rem / 2)`,
              lg: `calc(98% / 2 - 1.5rem + 1.5rem / 2)`,
              md: '38.813rem',
              mobile: 'unset',
            },
            borderBottom: 'none',
            ml: { md: 0, mobile: '-1rem' },
            mr: { md: 0, mobile: '-1rem' },
            pl: { md: 0, mobile: '1rem' },
            pr: { md: 0, mobile: '1rem' },
            overflow: { mobile: 'auto' },
          },
        }}
        options={data?.tabGroups
          ?.filter((tabGroup: TabGroup) =>
            data?.tabItems?.some((tabItem) => tabItem?.roomType === tabGroup?.groupId)
          )
          .map((tabGroup: TabGroup, index: number) => ({
            index,
            label: tabGroup?.groupName ?? '',
            content: (
              <HotelRoomContent
                tabItems={data?.tabItems?.filter(
                  (tabItem) => tabItem?.roomType === tabGroup?.groupId
                )}
                {...{
                  isLessThanSm,
                  isLessThanMd,
                  isLessThanLg,
                }}
              />
            ),
            ...(!isPremierInn && {
              roomTypeInventoryCount: getTotalInventoryAvailCountPerTabRoomType(
                tabGroup?.groupId ?? ''
              ), // tabGroup.groupId - e.g family
              roomTypeInventoryRoomTypesWithCount: getInventoryRoomTypeLabelsWithCount(
                tabGroup?.groupId ?? ''
              ),
            }),
          }))}
      />
    );
  }

  function renderSeeRatesButton(isDisplayRates: boolean | undefined) {
    const seeRatesBtnStyles = {
      mt: 'sm',
      fontSize: 'md',
      height: 'var(--chakra-space-xl)',
      width: { mobile: 'full', sm: '48', lg: '40', xl: '12%' },
    };

    const handleClickScroll = () => {
      const rateCardElement = document.querySelector('[data-testid="hdp_rateCard"]');
      if (rateCardElement) {
        rateCardElement.scrollIntoView({ behavior: 'smooth' });
      }
    };

    if (isPremierInn) {
      return (
        <Button
          variant="secondary"
          size="md"
          data-testid="hdp_roomsSeeRates"
          {...seeRatesBtnStyles}
          style={{ display: isHydrated && isDisplayRates ? 'block' : 'none' }}
          onClick={handleClickScroll}
        >
          {t('hoteldetails.roomtypes.rates.button')}
        </Button>
      );
    }
  }

  function renderRoomsDisclaimer(brand: string) {
    const description = renderSanitizedHtml(t(`hotelInfo.disclaimer.${brand}`));
    const disclaimerProps = getTypographyProps(
      disclaimerLegacyTypography,
      disclaimerSemanticTypography
    );
    const descriptionTextStyle =
      'textStyle' in disclaimerProps ? (disclaimerProps.textStyle as string) : undefined;
    const descriptionStrongTextStyle = descriptionTextStyle ? 'body-s-emphasis' : undefined;

    return (
      <Box mt="md" mb="lg">
        <Notification
          status="info"
          variant="infoGrey"
          svg={<Info />}
          description={<>{description}</>}
          prefixDataTestId="hdp_rooms_disclaimer"
          descriptionTextStyle={descriptionTextStyle}
          descriptionStrongTextStyle={descriptionStrongTextStyle}
        />
      </Box>
    );
  }

  // map roomTypeInformation query roomTypeCode to hotelInventory query code
  function mapRoomTypeInfoToHotelInvViaRoomTypeCode(tabGroupId: string) {
    if (
      !roomTypeInformationResponse?.dataRoomTypeInformation ||
      !hotelInventoryResponse?.dataHotelInventory
    ) {
      return null;
    }

    const matchedRoomTypeCodes =
      roomTypeInformationResponse?.dataRoomTypeInformation?.roomTypeInformation?.roomTypes.filter(
        (item) =>
          hotelInventoryResponse?.dataHotelInventory?.hotelInventory?.roomTypeInventories.find(
            ({ code }) => item.roomTypeCode === code || item.roomTypeCode.includes(code)
          )
      );

    const matchedRoomTypeGroupIdToTabsGroupId = matchedRoomTypeCodes?.filter((item) =>
      matchRoomTypeGroupIdToTabsGroupId(tabGroupId, item.roomTypeCode, item?.groupId)
    );

    return matchedRoomTypeGroupIdToTabsGroupId;
  }

  // Compare 'double' room type/classes to roomTypeCode and assign - double as the roomTypeGroupId, if not already double
  // to ensure the total count for each tab 'roomType' is correct
  function matchRoomTypeGroupIdToTabsGroupId(
    tabGroupId: string,
    roomTypeCode: string | string[],
    roomTypeGroupId: string
  ) {
    // DBLWIN - Standard
    // BIGWIN - Bigger Room
    // EXTDBL - Standard Extra
    // PPLDBL - Premier Plus
    const doubleRoomClassRoomCodes = [
      ROOM_TYPE.STANDARD,
      ROOM_TYPE.STANDARD_BIGGER,
      ROOM_TYPE.STANDARD_EXTRA,
      ROOM_TYPE.PREMIER_PLUS,
    ];

    // categorise the 'double room' classes
    if (
      (doubleRoomClassRoomCodes.indexOf(String(roomTypeCode)) !== -1 ||
        doubleRoomClassRoomCodes.some((el) => roomTypeCode.indexOf(el) !== -1)) &&
      roomTypeGroupId !== 'double'
    ) {
      roomTypeGroupId = 'double';
    }
    return roomTypeGroupId?.toLowerCase().includes(tabGroupId.toLowerCase());
  }

  function getInventoryRoomTypeLabelsWithCount(tabGroupId: string) {
    const mappedRoomTypeDataPerTab = mapRoomTypeInfoToHotelInvViaRoomTypeCode(tabGroupId);
    const isFamilyRoom =
      tabGroupId.toLowerCase() === 'family' || tabGroupId.toLowerCase() === 'familie';

    if (!mappedRoomTypeDataPerTab) return null;

    if (isFamilyRoom) {
      const inventoryRoomTypeLabels = mappedRoomTypeDataPerTab.map((item) => {
        let label = '';
        if (
          item.roomTypeCode === ROOM_TYPE.FAMILY_QUAD ||
          item.roomTypeCode.includes(ROOM_TYPE.FAMILY_QUAD)
        ) {
          label = t('hoteldetails.roomtypes.quad');
        }
        if (
          item.roomTypeCode === ROOM_TYPE.FAMILY_TRIPLE ||
          item.roomTypeCode.includes(ROOM_TYPE.FAMILY_TRIPLE)
        ) {
          label = t('hoteldetails.roomtypes.triple');
        }
        return label;
      });

      const inventoryAvailCountsPerRoomType = getInventoryAvailCountPerRoomType(tabGroupId);

      if (!inventoryAvailCountsPerRoomType) return null;

      const inventoryRoomTypeLabelsWithCount = inventoryRoomTypeLabels
        .map(function (item, i) {
          return item + ` (${inventoryAvailCountsPerRoomType[i]}) `;
        })
        .join('')
        .toString();

      return inventoryRoomTypeLabelsWithCount;
    } else {
      const inventoryRoomTypeLabelsWithCount = '';
      return inventoryRoomTypeLabelsWithCount;
    }
  }

  function getInventoryAvailCountPerRoomType(tabGroupId: string) {
    if (!hotelInventoryResponse) return null;

    const mappedRoomTypeDataPerTab = mapRoomTypeInfoToHotelInvViaRoomTypeCode(tabGroupId);

    if (!mappedRoomTypeDataPerTab) return null;

    return hotelInventoryResponse?.dataHotelInventory?.hotelInventory?.roomTypeInventories
      .filter((item) =>
        mappedRoomTypeDataPerTab.find(
          ({ roomTypeCode }) => item.code === roomTypeCode || roomTypeCode.includes(item.code)
        )
      )
      .map((roomTypeMatch) => roomTypeMatch.availableCount);
  }

  function getTotalInventoryAvailCountPerTabRoomType(tabGroupId: string) {
    const inventoryAvailCountsPerRoomType = getInventoryAvailCountPerRoomType(tabGroupId);

    if (!inventoryAvailCountsPerRoomType) return null;

    const totalInventoryAvailCountPerRoomType =
      inventoryAvailCountsPerRoomType.reduce((a, b) => a + b, 0) || 0;

    return totalInventoryAvailCountPerRoomType;
  }
}
