import type { FlexProps } from '@chakra-ui/react';
import { Box, Flex, Image, Link, Text } from '@chakra-ui/react';
import type {
  SingleHotelAvailability,
  SRMultiSearchParamsType,
  SRPartialTranslationsType,
} from '@whitbread-eos/api';
import { Area, FS_ENABLE_META_ARRIVAL_DAY_KEYWORD_PI } from '@whitbread-eos/api';
import { Button, Card, ChevronRight, Icon } from '@whitbread-eos/atoms';
import {
  DISTANCE_FROM_SEARCH_INITIAL_VALUE,
  SEARCH_REFERRER_INITIAL_VALUE,
  SRHotelBadges,
  SRHotelDiscountApplied,
  SRHotelDistance,
  SRHotelLastFewRooms,
  SRHotelLowestRate,
  SRHotelSoldOut,
  SRHotelThumbnail,
  SRHotelTitle,
} from '@whitbread-eos/molecules';
import {
  formatAssetsUrl,
  formatDataTestId,
  useCustomLocale,
  useLocalStorage,
  useFeatureSwitch,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { useMemo } from 'react';

import { APP_VARIANT, IGNORED_KEYS_FOR_HDP_URL } from '../constants';
import {
  formatDateSearchQueryUrl,
  getSearchQueryUrl,
  hotelOpensSoon,
  hotelFlagBannerBaseStyles,
  hotelFlagBannerImageStyles,
} from '../utilities';

export interface Props {
  data: SingleHotelAvailability;
  locale: string;
  partialTranslations: SRPartialTranslationsType;
  multiSearchParams: SRMultiSearchParamsType;
  variant?: string;
  isPricePerNightEnabled?: boolean;
  isSplitView?: boolean;
}

export default function MapHotelCard({
  data,
  partialTranslations,
  multiSearchParams,
  locale,
  variant,
  isPricePerNightEnabled,
  isSplitView,
}: Readonly<Props>) {
  const router = useRouter();
  const { language, country } = useCustomLocale();
  const { reservationId } = router.query;
  const { t } = useTranslation();
  // use helm (server side) feature switch to ensure toggle ready before page load
  const isMetaArrivalDayKeywordFlagEnabled = useFeatureSwitch({
    featureSwitchKey: FS_ENABLE_META_ARRIVAL_DAY_KEYWORD_PI,
    fallbackValue: false,
  });

  const [, setDistanceFromSearch] = useLocalStorage(
    'DistanceFromSearch',
    DISTANCE_FROM_SEARCH_INITIAL_VALUE
  );

  const [, setSearchReferrer] = useLocalStorage('SearchReferrer', SEARCH_REFERRER_INITIAL_VALUE);

  const hotelInformation = data?.hotelInformation;
  const hotelAvailability = data?.hotelAvailability;

  const hotelSlug = hotelInformation?.links?.detailsPage;
  const hotelBrand = hotelInformation?.brand ?? '';
  const queryParamsUrl = getSearchQueryUrl(router, IGNORED_KEYS_FOR_HDP_URL, (key, value) =>
    formatDateSearchQueryUrl(key, value, isMetaArrivalDayKeywordFlagEnabled)
  );
  const hotelPageUrl = `/${country}/${language}${
    variant === 'bb' ? '/business-booker' : ''
  }/hotels${hotelSlug}.html?${queryParamsUrl}${
    reservationId && variant === APP_VARIANT.CCUI ? `&reservationId=${reservationId}` : ''
  }&BRAND=${hotelBrand}${isSplitView ? '&intcmp=combined_map_tile' : ''}`;

  const imageURLHotelExterior = `${hotelInformation?.thumbnailImages?.[0]?.imageSrc ?? '/'}`;
  const imageAltText = `${data?.name} ${
    hotelInformation?.thumbnailImages?.[0]?.tags[0] ?? 'exterior'
  }`;
  const baseDataTestId = 'SRPMapView';
  const hotelBrandLogos = {
    hubLogo: partialTranslations?.searchInformation?.content?.global?.brand?.hubBadge ?? '',
    zipLogo: partialTranslations?.searchInformation?.content?.global?.brand?.zipBadge ?? '',
  };
  const soldOut = !hotelAvailability?.available;

  const hotelLimitedAvailability = hotelAvailability?.limitedAvailability;

  const hotelDistanceLabels = {
    distanceUnitPlural:
      partialTranslations?.searchInformation?.content?.results?.result?.distanceUnitPlural ?? '',
    fromLocation:
      partialTranslations?.searchInformation?.content?.results?.result?.fromLocation ?? '',
  };

  const hotelBadgesLabels = {
    premierPlus:
      partialTranslations?.searchInformation?.content?.results?.result?.facilities
        ?.premierPlusRoom ?? '',
    openingSoon:
      partialTranslations?.searchInformation?.content?.results?.result?.openingSoon ?? '',
    mlos: '',
  };

  const hotelLastFewRoomsLabel =
    partialTranslations?.searchInformation?.content?.results?.result?.availabilityWarning ?? '';
  const lowestRoomRate = hotelAvailability?.lowestRoomRate;

  const hotelOpeningDate = hotelInformation?.hotelOpeningDate ?? '';

  const isHotelOpeningSoon = useMemo(
    () => hotelOpensSoon(hotelOpeningDate, multiSearchParams),
    [hotelOpeningDate, multiSearchParams]
  );

  const cellCodesArr = multiSearchParams?.cellCodes;
  const isCellCodeMatched = cellCodesArr?.some(
    (item) => item === data?.hotelAvailability?.cellCode
  );
  const isDiscountApplied =
    variant === Area.PI && isCellCodeMatched && hotelAvailability?.available && !isHotelOpeningSoon;

  const hotelSoldOutLabel =
    partialTranslations?.searchInformation?.content?.results?.result?.fullyBooked ?? '';
  const lowestRoomLabels = {
    priceFrom: partialTranslations?.searchInformation?.content?.results?.result?.priceFrom ?? '',
    perNight: t('searchresults.list.hotel.perNight'),
    nights: t('amend.nights'),
  };

  const hotelOfferLabel =
    partialTranslations?.searchInformation?.content?.results?.result?.promotions?.discountApplied ??
    '';

  const distance = hotelAvailability?.distance ?? 0;
  const srchReferrer = router?.asPath;
  const hotelFlagBanner = hotelInformation?.hotelFlags?.flagBanner;
  const shouldShowHotelBanner =
    hotelInformation?.hotelFlags?.isEnabled === true && Boolean(hotelFlagBanner?.text?.trim());
  const hotelFlagBannerId = `${baseDataTestId}-${data.hotelId}-hotel-flag-banner`;

  return (
    <>
      {shouldShowHotelBanner && (
        <Flex
          id={hotelFlagBannerId}
          {...hotelFlagBannerStyles(hotelFlagBanner?.backgroundColour)}
          data-testid={formatDataTestId(baseDataTestId, 'hotel-flag-banner')}
        >
          {hotelFlagBanner?.backgroundImage && (
            <Image
              src={formatAssetsUrl(hotelFlagBanner.backgroundImage)}
              alt=""
              {...hotelFlagBannerImageStyles}
            />
          )}
          <Text textStyle="label-s" color={hotelFlagBanner?.textColour ?? 'baseWhite'}>
            {hotelFlagBanner?.text}
          </Text>
        </Flex>
      )}
      <Link
        href={hotelPageUrl}
        aria-describedby={shouldShowHotelBanner ? hotelFlagBannerId : undefined}
        {...linkStyles}
        onClick={() => handleCardClick(distance, srchReferrer)}
      >
        <Box {...wrapperStyles}>
          <Card {...cardStyles(shouldShowHotelBanner)}>
            <Flex direction="column">
              <Flex direction="row">
                <SRHotelThumbnail
                  imageData={{
                    imageSrc: imageURLHotelExterior,
                    imageAlt: imageAltText,
                  }}
                  brand={hotelBrand}
                  brandLogos={hotelBrandLogos}
                  testId={formatDataTestId(baseDataTestId, 'hotel-thumbnail')}
                  styles={thumbnailStyles}
                />
                <Flex direction="column">
                  <SRHotelTitle
                    styles={hotelTitleStyles}
                    title={data.name}
                    testId={formatDataTestId(baseDataTestId, 'hotel-title')}
                  />
                  <SRHotelDistance
                    distance={hotelAvailability?.distance ?? 0}
                    unit={hotelAvailability?.unit ?? ''}
                    labels={hotelDistanceLabels}
                    testId={formatDataTestId(baseDataTestId, 'hotel-distance')}
                    styles={hotelDistanceStyles}
                  />
                </Flex>
              </Flex>
              {variant === APP_VARIANT.CCUI && (
                <SRHotelBadges
                  testId={formatDataTestId(baseDataTestId, 'hotel-badge')}
                  styles={hotelBadgeStyles}
                />
              )}
              <SRHotelBadges
                hotelOpeningDate={hotelOpeningDate}
                labels={hotelBadgesLabels}
                testId={formatDataTestId(baseDataTestId, 'hotel-badges')}
                isHotelOpeningSoon={isHotelOpeningSoon}
                hotelFacilities={hotelInformation?.hotelFacilities ?? []}
                isColumnDisplay={variant === APP_VARIANT.CCUI}
                styles={hotelBadgesStyles}
                messagingFlag={hotelInformation?.messagingFlag ?? {}}
              />
              <Box border="1px solid var(--chakra-colors-lightGrey4)"></Box>
              <Flex
                direction="row"
                justifyContent="space-between"
                alignItems="center"
                {...bottomCardStyles}
              >
                <Box justifyContent="flex-start" alignItems="baseline" {...hotelLabelsStyles}>
                  {isDiscountApplied && (
                    <SRHotelDiscountApplied
                      label={hotelOfferLabel}
                      testId={formatDataTestId(baseDataTestId, 'hotel-offer-discount')}
                    />
                  )}
                  {hotelLimitedAvailability && (
                    <SRHotelLastFewRooms
                      label={hotelLastFewRoomsLabel}
                      testId={formatDataTestId(baseDataTestId, 'hotel-last-few-rooms')}
                      styles={hotelLastFewRoomsStyles}
                    />
                  )}
                  {soldOut && !hotelLimitedAvailability && (
                    <SRHotelSoldOut
                      label={hotelSoldOutLabel}
                      testId={formatDataTestId(baseDataTestId, 'hotel-soldout')}
                    />
                  )}
                </Box>
                <Flex justifyContent="flex-end" alignItems="center">
                  {lowestRoomRate && !isHotelOpeningSoon && (
                    <SRHotelLowestRate
                      data={{ lowestRoomRate }}
                      locale={locale}
                      labels={lowestRoomLabels}
                      testId={formatDataTestId(baseDataTestId, 'lowest-rate')}
                      styles={hotelLowestRateStyles}
                      isPricePerNightEnabled={isPricePerNightEnabled}
                    />
                  )}
                  {
                    <Box>
                      <Button size="xxs" variant="circle" {...buttonStyles(shouldShowHotelBanner)}>
                        <Icon svg={<ChevronRight color="var(--chakra-colors-baseWhite)" />} />
                      </Button>
                    </Box>
                  }
                </Flex>
              </Flex>
            </Flex>
          </Card>
        </Box>
      </Link>
    </>
  );

  function handleCardClick(distance: number, srchReferrer: string) {
    const distanceFromSearch = {
      data: {
        distance: distance,
      },
    };
    setDistanceFromSearch(distanceFromSearch);

    const referrer = {
      data: {
        referrer: srchReferrer,
      },
    };
    setSearchReferrer(referrer);
  }
}

const wrapperStyles = {
  m: 'auto',
  w: '18.75rem',
  h: 'auto',
  cursor: 'pointer',
};

const cardStyles = (shouldShowHotelBanner: boolean): FlexProps => ({
  padding: 'sm',
  borderRadius: 'base',
  borderTopLeftRadius: shouldShowHotelBanner ? '0' : 'base',
  borderTopRightRadius: shouldShowHotelBanner ? '0' : 'base',
  border: '0',
  ...(shouldShowHotelBanner && {
    boxShadow: 'none',
  }),
});

const hotelFlagBannerStyles = (backgroundColour?: string | null): FlexProps => ({
  ...hotelFlagBannerBaseStyles(backgroundColour),
  m: 'auto',
  w: '18.75rem',
});

const thumbnailStyles = {
  w: '6.813rem',
  h: '4.625rem',
  mr: 'sm',
  mb: 'sm',
};

const hotelTitleStyles = {
  w: { mobile: '9.813rem', md: '10.5rem' },
  fontSize: 'sm',
  fontWeight: 'semibold',
};

const hotelDistanceStyles = {
  w: { mobile: '8rem', md: '10.5rem' },
  fontSize: 'sm',
  fontWeight: 'normal',
  mt: 'xs',
};

const bottomCardStyles = {
  paddingRight: { mobile: 'sm', md: '0' },
  mt: 'sm',
};

const hotelLabelsStyles = {
  left: '0',
};

const hotelLowestRateStyles = {
  containerStyles: {
    p: '0',
  },
  labelStyles: {
    fontSize: 'sm',
    fontWeight: 'normal',
    alignSelf: 'center',
    mr: 'sm',
  },
  priceStyles: {
    fontSize: 'md',
    fontWeight: 'bold',
    alignSelf: 'center',
  },
};

const buttonStyles = (shouldShowHotelBanner: boolean) => ({
  bgColor: shouldShowHotelBanner ? 'tertiary' : 'primary',
  ml: 'sm',
  alignSelf: 'center',
  ...(shouldShowHotelBanner && {
    border: '1px solid var(--chakra-colors-tertiary)',
  }),
  _hover: {
    bg: shouldShowHotelBanner
      ? 'var(--chakra-colors-btnSecondaryHoverBg) radial-gradient(circle, transparent 1%, var(--chakra-colors-btnSecondaryHoverBg) 1%) center/15000%'
      : 'var(--chakra-colors-btnPrimaryHoverBg) radial-gradient(circle, transparent 1%, var(--chakra-colors-btnPrimaryHoverBg) 1%) center/15000%',
    ...(shouldShowHotelBanner && {
      border: '1px solid var(--chakra-colors-tertiary)',
    }),
    boxShadow: '0 4px 8px var(--chakra-colors-lightGrey1)',
    _disabled: {
      bg: 'lightGrey3',
    },
  },
});

const linkStyles = {
  cursor: 'default',
  _hover: {
    textDecoration: 'none',
  },
  _focus: {
    textDecoration: 'none',
    boxShadow: 'none',
  },
  _visited: {
    textDecoration: 'none',
  },
  _link: {
    textDecoration: 'none',
  },
  _active: {
    textDecoration: 'none',
  },
};

const hotelLastFewRoomsStyles = {
  mr: 'sm',
};

const hotelBadgesStyles = {
  mb: 'sm',
};

const hotelBadgeStyles = {
  mb: 'sm',
};
