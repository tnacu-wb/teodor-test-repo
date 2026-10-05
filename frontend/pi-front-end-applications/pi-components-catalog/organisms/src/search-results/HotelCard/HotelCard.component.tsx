import type { BoxProps, FlexProps } from '@chakra-ui/react';
import { Box, Flex, Image, Link, Text } from '@chakra-ui/react';
import type {
  SingleHotelAvailability,
  SRMultiSearchParamsType,
  SRPartialTranslationsType,
  SRVariantType,
} from '@whitbread-eos/api';
import {
  Area,
  FS_ENABLE_META_ARRIVAL_DAY_KEYWORD_PI,
  FT_PI_BB_CCUI_SRP_MULTIPLE_IMAGES,
} from '@whitbread-eos/api';
import { Button, Card, ChevronRight, Icon } from '@whitbread-eos/atoms';
import {
  DISTANCE_FROM_SEARCH_INITIAL_VALUE,
  SEARCH_REFERRER_INITIAL_VALUE,
  SRHotelBadges,
  SRHotelDiscountApplied,
  SRHotelDistance,
  SRHotelFacilities,
  SRHotelLastFewRooms,
  SRHotelLowestRate,
  SRHotelOpeningInformation,
  SRHotelSoldOut,
  SRHotelThumbnail,
  HotelThumbnailCarousel,
  SRHotelTitle,
} from '@whitbread-eos/molecules';
import {
  formatAssetsUrl,
  formatDataTestId,
  useCustomLocale,
  useLocalStorage,
  useFeatureSwitch,
  useSemanticTypography,
  useFeatureToggle,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { useMemo } from 'react';

import { setMapMarkerHoverState } from '../../utils/mapMarkerRegistry';
import { IGNORED_KEYS_FOR_HDP_URL } from '../constants';
import {
  formatDateSearchQueryUrl,
  getSearchQueryUrl,
  hotelOpensSoon,
  hotelFlagBannerBaseStyles,
  hotelFlagBannerImageStyles,
} from '../utilities';

type ResponsiveState = {
  mobile: boolean;
  xs: boolean;
  sm: boolean;
};

export interface Props {
  data: SingleHotelAvailability;
  locale: string;
  partialTranslations: SRPartialTranslationsType;
  roomTypes: (string | undefined)[] | undefined;
  multiSearchParams: SRMultiSearchParamsType;
  variant: SRVariantType;
  pricePerNight: boolean | null;
  isPricePerNightEnabled?: boolean;
  eagerLoad?: boolean;
  isSplitView?: boolean;
  responsive: ResponsiveState;
}

const CCUI_VARIANT = 'ccui';

export default function HotelCard({
  data,
  multiSearchParams,
  locale,
  partialTranslations,
  roomTypes,
  variant,
  pricePerNight,
  isPricePerNightEnabled,
  eagerLoad,
  isSplitView,
  responsive,
}: Readonly<Props>) {
  const router = useRouter();
  const { mobile, xs, sm } = responsive;
  const { reservationId } = router.query;
  const { t } = useTranslation();
  const getTypographyProps = useSemanticTypography();

  // Compute typography styles based on feature toggle
  const titleStyles = {
    ...titleStylesLayout,
    ...getTypographyProps(titleLegacyTypography, titleSemanticTypography),
  };

  const hotelDistanceTextStyles = {
    ...hotelDistanceLayoutStyles,
    ...getTypographyProps(hotelDistanceLegacyTypography, hotelDistanceSemanticTypography),
  };

  const hotelDistanceTextStylesSmall = {
    ...hotelDistanceSmallLayoutStyles,
    ...getTypographyProps(hotelDistanceLegacyTypography, hotelDistanceSemanticTypography),
  };

  const hotelLowestRateLabelStyles = {
    ...hotelLowestRateLabelStylesLayout,
    ...getTypographyProps(
      hotelLowestRateLabelLegacyTypography,
      hotelLowestRateLabelSemanticTypography
    ),
  };

  const hotelLowestRatePriceStyles = {
    ...hotelLowestRatePriceStylesLayout,
    ...getTypographyProps(
      hotelLowestRatePriceLegacyTypography,
      hotelLowestRatePriceSemanticTypography
    ),
  };

  const hotelButtonTypographyStyles = getTypographyProps(
    hotelButtonLegacyTypography,
    hotelButtonSemanticTypography
  );

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

  const baseDataTestId = 'SRP';

  const { language, country } = useCustomLocale();
  const { [FT_PI_BB_CCUI_SRP_MULTIPLE_IMAGES]: isSRPMultipleImagesEnabled = false } =
    useFeatureToggle();

  const hotelInformation = data?.hotelInformation;
  const hotelAvailability = data?.hotelAvailability;

  const hotelSlug = hotelInformation?.links?.detailsPage ?? '';
  const hotelBrand = hotelInformation?.brand ?? '';

  const searchQueryUrl = getSearchQueryUrl(router, IGNORED_KEYS_FOR_HDP_URL, (key, value) =>
    formatDateSearchQueryUrl(key, value, isMetaArrivalDayKeywordFlagEnabled)
  );

  const URLToRedirect = `/${country}/${language}${
    variant === 'bb' ? '/business-booker' : ''
  }/hotels${hotelSlug}.html?${searchQueryUrl}${
    reservationId && variant === CCUI_VARIANT ? `&reservationId=${reservationId}` : ''
  }&BRAND=${hotelBrand}`;

  const imageURLHotelExterior = `${hotelInformation?.thumbnailImages?.[0]?.imageSrc ?? '/'}`;
  const imageAltText = `${data?.name} ${
    hotelInformation?.thumbnailImages?.[0]?.tags[0] ?? 'exterior'
  }`;
  const hotelBrandLogos = {
    hubLogo: partialTranslations?.searchInformation?.content?.global?.brand?.hubBadge ?? '',
    zipLogo: partialTranslations?.searchInformation?.content?.global?.brand?.zipBadge ?? '',
  };

  const hotelOpeningDate = hotelInformation?.hotelOpeningDate ?? '';
  const isHotelOpeningSoon = useMemo(
    () => hotelOpensSoon(hotelOpeningDate, multiSearchParams),
    [hotelOpeningDate, multiSearchParams]
  );
  const isHotelSoldOut = !hotelAvailability?.available && !isHotelOpeningSoon;
  const hasMlosRestriction = hotelAvailability?.hasMlosRestriction;

  const lowestRoomRate = hotelAvailability?.lowestRoomRate;
  const numberOfNights = multiSearchParams?.numberOfNights;

  const hotelLimitedAvailability = hotelAvailability?.limitedAvailability;

  const cellCodesArr = multiSearchParams?.cellCodes;
  const isCellCodeMatched = cellCodesArr?.some(
    (item) => item === data?.hotelAvailability?.cellCode
  );
  const isDiscountApplied =
    variant === Area.PI && isCellCodeMatched && hotelAvailability?.available && !isHotelOpeningSoon;

  const hotelLastFewRoomsLabel =
    partialTranslations?.searchInformation?.content?.results?.result?.availabilityWarning ?? '';

  const hotelDistanceLabels = {
    distanceUnitPlural:
      partialTranslations?.searchInformation?.content?.results?.result?.distanceUnitPlural ?? '',
    fromLocation:
      partialTranslations?.searchInformation?.content?.results?.result?.fromLocation ?? '',
  };
  const lowestRoomLabels = {
    priceFrom: partialTranslations?.searchInformation?.content?.results?.result?.priceFrom ?? '',
    perNight: t('searchresults.list.hotel.perNight'),
    nights: t('amend.nights'),
  };

  const hotelBadgesLabels = {
    premierPlus:
      partialTranslations?.searchInformation?.content?.results?.result?.facilities
        ?.premierPlusRoom ?? '',
    openingSoon:
      partialTranslations?.searchInformation?.content?.results?.result?.openingSoon ?? '',
    mlos: t('search.mlos.tag'),
  };
  const hotelSoldOutLabel =
    partialTranslations?.searchInformation?.content?.results?.result?.fullyBooked ?? '';
  const hotelButtonlabel =
    partialTranslations?.searchInformation?.content?.results?.result?.viewDetails ?? '';

  const hotelOfferLabel =
    partialTranslations?.searchInformation?.content?.results?.result?.promotions?.discountApplied ??
    '';

  const distance = hotelAvailability?.distance ?? 0;
  const srchReferrer = router?.asPath;
  const hotelFlagBanner = hotelInformation?.hotelFlags?.flagBanner;
  const shouldShowHotelBanner =
    hotelInformation?.hotelFlags?.isEnabled === true && Boolean(hotelFlagBanner?.text?.trim());
  const hotelFlagBannerId = `${baseDataTestId}-${data.hotelId}-hotel-flag-banner`;

  const handleMapMarkerHover = (isHovering: boolean) => {
    if (!isSplitView) return;
    setMapMarkerHoverState(data.hotelId, isHovering);
  };

  return (
    <Box {...getWrapperStyles(isSplitView)}>
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
        href={URLToRedirect}
        aria-describedby={shouldShowHotelBanner ? hotelFlagBannerId : undefined}
        {...linkStyles}
        onClick={() => handleCardClick(distance, srchReferrer)}
        onMouseEnter={() => handleMapMarkerHover(true)}
        onMouseLeave={() => handleMapMarkerHover(false)}
        onFocus={() => handleMapMarkerHover(true)}
        onBlur={() => handleMapMarkerHover(false)}
      >
        <Card
          {...cardContainerStyles}
          data-testid={formatDataTestId(baseDataTestId, 'hotel-card')}
          {...({ hotelcode: data.hotelId } as Record<string, string>)}
        >
          <Flex direction="column">
            {isSRPMultipleImagesEnabled ? (
              <HotelThumbnailCarousel
                brand={hotelBrand}
                brandLogos={hotelBrandLogos}
                styles={hotelThumbnailWrapperStyles}
                testId={formatDataTestId(baseDataTestId, 'hotel-thumbnail-carousel')}
                thumbnailImages={hotelInformation?.thumbnailImages ?? []}
                name={data?.name}
              />
            ) : (
              <SRHotelThumbnail
                imageData={{
                  imageSrc: imageURLHotelExterior,
                  imageAlt: imageAltText,
                }}
                brand={hotelBrand}
                brandLogos={hotelBrandLogos}
                testId={formatDataTestId(baseDataTestId, 'hotel-thumbnail')}
                styles={hotelThumbnailWrapperStyles}
                eagerLoad={eagerLoad}
              />
            )}

            {sm && (
              <SRHotelFacilities
                facilities={hotelInformation?.hotelFacilities ?? []}
                roomTypes={roomTypes}
                testId={formatDataTestId(baseDataTestId, 'hotel-facilities')}
              />
            )}
          </Flex>
          <Flex {...getHotelDetailsBoxStyles(isSplitView)}>
            <Flex alignItems="center">
              <SRHotelTitle
                title={data.name}
                testId={formatDataTestId(baseDataTestId, 'hotel-title')}
                styles={titleStyles}
              />
              {variant === CCUI_VARIANT && (
                <SRHotelBadges
                  testId={formatDataTestId(baseDataTestId, 'hotel-badge')}
                  styles={hotelBadgeStyles}
                />
              )}
            </Flex>
            <SRHotelDistance
              distance={hotelAvailability?.distance ?? 0}
              unit={hotelAvailability?.unit ?? ''}
              labels={hotelDistanceLabels}
              testId={formatDataTestId(baseDataTestId, 'hotel-distance')}
              styles={mobile || xs || sm ? hotelDistanceTextStylesSmall : hotelDistanceTextStyles}
            />
            <SRHotelBadges
              hasMlosRestriction={hasMlosRestriction}
              hotelOpeningDate={hotelOpeningDate}
              labels={hotelBadgesLabels}
              testId={formatDataTestId(baseDataTestId, 'hotel-badges')}
              isHotelOpeningSoon={isHotelOpeningSoon}
              hotelFacilities={hotelInformation?.hotelFacilities ?? []}
              messagingFlag={hotelInformation?.messagingFlag ?? {}}
              styles={hotelBadgesStyles}
            />
            {!sm && (
              <SRHotelFacilities
                facilities={hotelInformation?.hotelFacilities ?? []}
                roomTypes={roomTypes}
                testId={formatDataTestId(baseDataTestId, 'hotel-facilities')}
              />
            )}
          </Flex>
          <Flex
            {...getHotelLabelsPriceBoxStyles(isSplitView)}
            {...(isPricePerNightEnabled &&
              (numberOfNights as number) > 1 && { ...alignBottomLabels })}
          >
            <Box>
              {partialTranslations?.searchInformation?.content?.results?.result &&
                isHotelOpeningSoon && (
                  <SRHotelOpeningInformation
                    hotelOpeningDate={hotelOpeningDate}
                    labels={partialTranslations?.searchInformation?.content?.results?.result}
                    testId={formatDataTestId(baseDataTestId, 'hotel-opening-information')}
                  />
                )}
              {hotelLimitedAvailability && (
                <SRHotelLastFewRooms
                  label={hotelLastFewRoomsLabel}
                  testId={formatDataTestId(baseDataTestId, 'hotel-last-few-rooms')}
                />
              )}
              {isDiscountApplied && (
                <SRHotelDiscountApplied
                  label={hotelOfferLabel}
                  testId={formatDataTestId(baseDataTestId, 'hotel-offer-discount')}
                />
              )}
              {isHotelSoldOut && (
                <SRHotelSoldOut
                  label={hotelSoldOutLabel}
                  testId={formatDataTestId(baseDataTestId, 'hotel-soldout')}
                  styles={hotelSoldOutStyles}
                />
              )}
            </Box>
            <Flex {...hotelPriceButtonBoxStyles}>
              <Box>
                {lowestRoomRate && !isHotelOpeningSoon && (
                  <SRHotelLowestRate
                    data={{ lowestRoomRate, numberOfNights }}
                    pricePerNight={pricePerNight}
                    locale={locale}
                    labels={lowestRoomLabels}
                    testId={formatDataTestId(baseDataTestId, 'lowest-rate')}
                    isPricePerNightEnabled={isPricePerNightEnabled}
                    styles={{
                      containerStyles:
                        isPricePerNightEnabled && (numberOfNights as number) > 1
                          ? {
                              ...hotelLowestRateContainerStyles,
                              ...hotelLowestRateContainerPricePerNightStyles,
                            }
                          : hotelLowestRateContainerStyles,
                      labelStyles:
                        isPricePerNightEnabled && (numberOfNights as number) > 1
                          ? {
                              ...hotelLowestRateLabelStyles,
                              ...hotelLowestRateLabelPricePerNightStyles,
                            }
                          : hotelLowestRateLabelStyles,
                      priceStyles:
                        isPricePerNightEnabled && (numberOfNights as number) > 1
                          ? { ...hotelLowestRatePriceStyles, ...pricePerNightStyles }
                          : hotelLowestRatePriceStyles,
                      pricePerNight: perNightLabel,
                    }}
                  />
                )}
              </Box>

              <Box
                {...hotelButtonBoxStyles}
                {...(isPricePerNightEnabled &&
                  (numberOfNights as number) > 1 && { ...hotelButtonBoxPricePerNightStyles })}
                data-testid={formatDataTestId(baseDataTestId, 'hotel-button')}
              >
                <Button
                  size={mobile || xs || sm ? 'xxs' : 'xsm'}
                  variant={mobile || xs || sm ? 'circle' : 'primary'}
                  {...buttonStyles(shouldShowHotelBanner)}
                >
                  {(mobile || xs || sm) && (
                    <Icon svg={<ChevronRight color="var(--chakra-colors-baseWhite)" />} />
                  )}
                  {!mobile && !xs && !sm && (
                    <Text as="span" {...hotelButtonTypographyStyles}>
                      {hotelButtonlabel}
                    </Text>
                  )}
                </Button>
              </Box>
            </Flex>
          </Flex>
        </Card>
      </Link>
    </Box>
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

const getWrapperStyles = (isSplitView?: boolean): BoxProps => ({
  position: 'relative',
  m: 'var(--chakra-space-lg) auto',
  w: isSplitView
    ? 'full'
    : {
        mobile: 'full',
        md: '45rem',
        lg: '50.5rem',
        xl: '54rem',
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
  _focusVisible: {
    outline: '2px solid var(--chakra-colors-primary)',
    outlineOffset: '2px',
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

const cardContainerStyles: BoxProps = {
  bgColor: 'baseWhite',
  flexDirection: {
    mobile: 'column',
    sm: 'row',
  },
  w: 'full',
  h: 'auto',
  minH: '8.125rem',
  border: '1px solid var(--chakra-colors-lightGrey4)',
  borderRadius: 'var(--chakra-space-radiusSmall)',
  boxShadow: 'none',
  margin: 'var(--chakra-space-sm) auto 0',
  padding: {
    mobile: 0,
    sm: 'md',
  },
  _hover: {
    cursor: 'pointer',
    boxShadow: '0 0 var(--chakra-space-sm) 0 var(--chakra-colors-lightGrey2)',
    transitionProperty: 'border, box-shadow',
    transitionDuration: '0s, 0s',
    transitionTimingFunction: 'ease, ease',
    transitionDelay: '0s, 0s',
  },
};

const titleStylesLayout = {
  color: 'darkGrey1',
};

const titleLegacyTypography = {
  fontSize: 'md',
  lineHeight: 'var(--chakra-lineHeights-3)',
  fontWeight: 'semibold',
};

const titleSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const hotelThumbnailWrapperStyles = {
  w: {
    mobile: 'full',
    sm: '7.5rem',
    md: '11.75rem',
  },
  h: {
    mobile: '10.188rem',
    xs: '12.375rem',
    sm: '4.313rem',
    md: '7.25rem',
  },
  mb: { base: '0', sm: '0.875rem', md: '0' },
};

const hotelDistanceLayoutStyles = {
  paddingTop: {
    mobile: 'sm',
    sm: 'xs',
  },
  paddingBottom: 'sm',
};

const hotelDistanceLegacyTypography = {
  fontSize: 'sm',
  fontWeight: 'normal',
  lineHeight: '2',
};

const hotelDistanceSemanticTypography = {
  textStyle: 'body-s-regular',
};

const hotelDistanceSmallLayoutStyles = {
  ...hotelDistanceLayoutStyles,
  flexGrow: 2,
};

const hotelLowestRateContainerStyles: FlexProps = {
  pt: {
    sm: 'sm',
  },
  pb: {
    md: 'md',
  },
  flexDirection: {
    mobile: 'column',
    md: 'row',
  },
};

const hotelLowestRateContainerPricePerNightStyles = {
  display: {
    mobile: 'flex',
    md: 'block',
  },
  width: 'full',
  padding: {
    sm: '0',
  },
  paddingBottom: {
    md: 'sm',
  },
  flexDirection: {
    mobile: 'column',
    md: 'row',
  },
  flexWrap: {
    md: 'wrap',
  },
} as FlexProps;

const hotelLowestRateLabelStylesLayout = {
  color: 'darkGrey1',
  alignSelf: {
    mobile: 'flex-start',
    md: 'flex-end',
  },
  pr: 'xs',
};

const hotelLowestRateLabelLegacyTypography = {
  fontSize: 'sm',
  lineHeight: '2',
  fontWeight: 'normal',
};

const hotelLowestRateLabelSemanticTypography = {
  textStyle: 'body-s-regular',
};

const hotelLowestRateLabelPricePerNightStyles = {
  width: {
    mobile: 'full',
  },
  pr: {
    mobile: '0',
    xs: 'xs',
  },
  flex: '100%',
};

const hotelLowestRatePriceStylesLayout = {
  color: 'darkGrey1',
};

const hotelLowestRatePriceLegacyTypography = {
  fontWeight: 'bold',
  lineHeight: '3',
  fontSize: {
    mobile: 'lg',
    sm: 'xl',
  },
};

const hotelLowestRatePriceSemanticTypography = {
  textStyle: 'heading-s',
};

const getHotelDetailsBoxStyles = (isSplitView?: boolean) =>
  ({
    px: {
      mobile: 'sm',
      sm: 'md',
    },
    py: {
      mobile: 'sm',
      sm: 0,
    },
    w: {
      mobile: '100%',
      sm: '16.3125rem',
      md: '19.6875rem',
      lg: '25.6875rem',
      xl: '29.1875rem',
    },
    ...(isSplitView && {
      sx: { '@media screen and (max-width: 1600px)': { width: '22rem' } },
    }),
    flexDirection: 'column',
  }) as FlexProps;

const getHotelLabelsPriceBoxStyles = (isSplitView?: boolean): FlexProps => ({
  marginLeft: 'auto',
  borderLeft: {
    sm: '0.0625rem solid var(--chakra-colors-lightGrey4)',
  },
  w: {
    mobile: '100%',
    sm: '8.875rem',
    md: '12.5rem',
    lg: '12rem',
  },
  px: {
    mobile: 'sm',
    sm: 0,
  },
  pl: {
    sm: 'md',
  },
  mr: {
    lg: '-md',
  },
  ...(isSplitView && {
    sx: { '@media screen and (max-width: 1600px)': { marginRight: 0 } },
  }),
  pb: {
    mobile: 'md',
    sm: '0',
  },
  flexDirection: {
    mobile: 'row',
    sm: 'column',
  },
  justifyContent: {
    mobile: 'space-between',
    sm: 'flex-end',
  },
  alignItems: {
    mobile: 'center',
    sm: 'initial',
  },
});

const hotelPriceButtonBoxStyles = {
  flexDirection: {
    mobile: 'row',
    md: 'column',
  },
  justifyContent: {
    mobile: 'space-between',
  },
} as FlexProps;

const hotelButtonBoxStyles = {
  pl: {
    mobile: 'sm',
    md: '0',
  },
  pr: {
    mobile: '0',
  },
  alignSelf: {
    mobile: 'end',
    md: 'start',
  },
};

const hotelButtonBoxPricePerNightStyles = {
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  alignSelf: {
    mobile: 'auto',
    md: 'start',
  },
};

const buttonStyles = (shouldShowHotelBanner: boolean) => ({
  bgColor: shouldShowHotelBanner ? 'tertiary' : 'primary',
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

const hotelButtonLegacyTypography = {
  fontSize: 'lg',
  lineHeight: '3',
  fontWeight: 'semibold',
};

const hotelButtonSemanticTypography = {
  textStyle: 'label-xl',
};

const hotelBadgesStyles = {
  mb: {
    mobile: 'sm',
    sm: '0',
    md: 'sm',
  },
};

const hotelBadgeStyles = {
  ml: 'sm',
};

const hotelSoldOutStyles = {
  mb: {
    mobile: '0',
    sm: 'md',
  },
};

const perNightLabel = {
  display: 'inline-block',
  width: {
    mobile: 'full',
    md: 'auto',
  },
  pr: {
    mobile: '0',
    xs: 'xs',
  },
  flex: '1',
  fontSize: 'sm',
  alignContent: {
    md: 'end',
  },
};

const pricePerNightStyles = {
  display: 'inline-block',
  width: {
    mobile: 'full',
    md: 'auto',
  },
  pr: {
    mobile: '0',
    xs: 'xs',
  },
  flex: '0',
};

const alignBottomLabels = {
  alignItems: {
    mobile: 'end',
    sm: 'normal',
  },
};

const hotelFlagBannerStyles = (backgroundColour?: string | null): FlexProps => ({
  ...hotelFlagBannerBaseStyles(backgroundColour),
  maxW: 'full',
  position: 'absolute',
  pointerEvents: 'none',
  transform: {
    mobile: 'none',
    sm: 'translateY(calc(var(--chakra-space-md) + 1px - 100%))',
  },
  top: 0,
  left: 0,
  w: {
    mobile: 'full',
    sm: 'fit-content',
  },
  zIndex: 1,
});
