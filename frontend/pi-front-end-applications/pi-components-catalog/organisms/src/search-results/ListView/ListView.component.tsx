import { Box, Flex, Text, useMediaQuery } from '@chakra-ui/react';
import {
  Area,
  SingleHotelAvailability,
  SRMultiSearchParamsType,
  SRPartialTranslationsType,
  SRVariantType,
  HeaderInformationData,
} from '@whitbread-eos/api';
import {
  InfiniteScroller,
  MapExpand,
  SwitchToggle,
  PromotionsNotification,
} from '@whitbread-eos/atoms';
import { SRHotelNotification } from '@whitbread-eos/molecules';
import {
  formatDataTestId,
  useScreenSize,
  useSemanticTypography,
  type PromotionsInformation,
  useMobileControlsDisplay,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useState } from 'react';

import {
  MapViewBBVariant as SRMapViewBB,
  MapViewPIVariant as SRMapViewPI,
  SRHotelCard,
} from '../../index';

interface Props {
  fetchNewHotels: () => void;
  items: SingleHotelAvailability[];
  hasMore: boolean;
  resultsMeta: {
    total: number;
    currentResults: number;
  };
  partialTranslations: SRPartialTranslationsType;
  orderedHotels: SingleHotelAvailability[];
  roomTypes?: (string | undefined)[];
  multiSearchParams: SRMultiSearchParamsType;
  language: string;
  currentPage: number;
  baseDataTestId: string;
  isHotelAvailable?: {
    isAvailable: boolean | undefined;
    isOpeningSoon: boolean | undefined | '';
    hasMlos: boolean | undefined;
  };
  variant: SRVariantType;
  changeViewType: () => void;
  headerInformation: HeaderInformationData;
  featureToggle: {
    isPricePerNightEnabledOnPi?: boolean;
    isPricePerNightEnabledOnBb?: boolean;
    isPricePerNightEnabledOnCcui?: boolean;
  };
  promotionBannerData?: PromotionsInformation;
  scrollableTarget?: string;
  isSplitView?: boolean;
  pricePerNight?: boolean | null;
  onTogglePricePerNight?: () => void;
}

const SCROLL_THRESHOLD_CONSTANT = 0.95;
const MAP_SMALL_ITEMS_NUM = 3;

function shouldShowTotalResults(variant: SRVariantType, featureToggle: Props['featureToggle']) {
  return (
    (variant === Area.PI && !featureToggle.isPricePerNightEnabledOnPi) ||
    (variant === Area.BB && !featureToggle.isPricePerNightEnabledOnBb) ||
    (variant === Area.CCUI && !featureToggle.isPricePerNightEnabledOnCcui)
  );
}

function isPricePerNightEnabled(featureToggle: Props['featureToggle']) {
  return (
    featureToggle.isPricePerNightEnabledOnPi ||
    featureToggle.isPricePerNightEnabledOnBb ||
    featureToggle.isPricePerNightEnabledOnCcui
  );
}

// Helper to determine default pricePerNight selection (Default for PI, BB = PPN and for CCUI = TP)
function getDefaultPricePerNight(variant: SRVariantType, featureToggle: Props['featureToggle']) {
  if (isPricePerNightEnabled(featureToggle)) {
    if (variant === Area.CCUI || variant === Area.PI) return false;
    if (variant === Area.BB) return true;
  }
  return null;
}

export default function ListView({
  fetchNewHotels,
  items,
  hasMore,
  partialTranslations,
  resultsMeta,
  orderedHotels,
  roomTypes,
  multiSearchParams,
  language,
  currentPage,
  baseDataTestId,
  isHotelAvailable,
  variant,
  changeViewType,
  headerInformation,
  featureToggle,
  promotionBannerData,
  scrollableTarget,
  isSplitView,
  pricePerNight: pricePerNightProp,
  onTogglePricePerNight,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const getTypographyProps = useSemanticTypography();
  const { fullyBooked = '', openingSoon = '' } =
    partialTranslations?.searchInformation?.content?.results?.notifications || {};
  const isMlos = t('search.mlos.ccui.notification');
  const { isLessThanMd } = useScreenSize();
  const defaultPricePerNight = getDefaultPricePerNight(variant, featureToggle);
  const [internalPricePerNight, setInternalPricePerNight] = useState<boolean | null>(
    defaultPricePerNight
  );
  const pricePerNight = isSplitView
    ? pricePerNightProp !== undefined
      ? pricePerNightProp
      : defaultPricePerNight
    : internalPricePerNight;
  const togglePricePerNight = isSplitView
    ? (onTogglePricePerNight ?? (() => {}))
    : () => setInternalPricePerNight((prev) => !prev);

  const showTotalResults = shouldShowTotalResults(variant, featureToggle) && resultsMeta.total > 0;
  const enableTotalPriceForSingleNight =
    isPricePerNightEnabled(featureToggle) &&
    multiSearchParams?.numberOfNights === 1 &&
    resultsMeta.total > 0;

  const enableTotalPriceForMultiNights =
    isPricePerNightEnabled(featureToggle) &&
    multiSearchParams?.numberOfNights != null &&
    multiSearchParams.numberOfNights > 1 &&
    resultsMeta.total > 0;

  const controlsDisplay = useMobileControlsDisplay(variant);

  const [mobile] = useMediaQuery('(max-width: 374px)');

  const [xs] = useMediaQuery('(min-width: 375px) and (max-width: 575px)');

  const [sm] = useMediaQuery('(min-width: 576px) and (max-width: 767px)');

  const responsive = {
    mobile,
    xs,
    sm,
  };

  return (
    <>
      <InfiniteScroller
        next={fetchNewHotels}
        dataLength={items.length}
        hasMore={hasMore}
        scrollThreshold={SCROLL_THRESHOLD_CONSTANT}
        style={{ overflow: 'hidden' }}
        scrollableTarget={scrollableTarget}
      >
        {!isSplitView && (showTotalResults || enableTotalPriceForSingleNight) && (
          <Text
            {...getTotalResultsTextStyles(isSplitView)}
            {...getTypographyProps(totalResultsLegacyTypography, totalResultsSemanticTypography)}
          >
            {resultsMeta.total} {partialTranslations.searchInformation.content.totalHotels}
          </Text>
        )}
        {!isSplitView && enableTotalPriceForMultiNights && (
          <Flex
            justifyContent="space-between"
            alignItems="center"
            {...getPricePerNightWrapperStyles(isSplitView)}
            {...getTypographyProps(totalResultsLegacyTypography, totalResultsSemanticTypography)}
            data-testid={formatDataTestId(baseDataTestId, 'price-per-night-wrapper')}
          >
            {resultsMeta.total} {partialTranslations.searchInformation.content.totalHotels}
            <SwitchToggle
              baseDataTestId={`${baseDataTestId}-price-per-night-rooms-${
                pricePerNight ? 'ppn' : 'tp'
              }`}
              first={t('searchresults.list.hotel.pricePerNight')}
              second={t('searchresults.list.hotel.totalPrice')}
              onToggle={togglePricePerNight}
              defaultSelected={defaultPricePerNight === true}
            />
          </Flex>
        )}
        {variant !== Area.CCUI && isLessThanMd && !controlsDisplay && (
          <Box height="75px" position="relative">
            <Box
              data-testid="mobile-map-view-button"
              {...mobileViewTypeButtonStyles}
              onClick={changeViewType}
            >
              <MapExpand />
            </Box>
            {variant === Area.PI && (
              <SRMapViewPI
                items={items.slice(0, MAP_SMALL_ITEMS_NUM)}
                multiSearchParams={multiSearchParams}
                locale={language}
                partialTranslations={partialTranslations}
                headerInformation={headerInformation}
                baseDataTestId={baseDataTestId}
                isPricePerNightEnabled={isPricePerNightEnabled(featureToggle) as boolean}
              />
            )}
            {variant === Area.BB && (
              <SRMapViewBB
                items={items.slice(0, MAP_SMALL_ITEMS_NUM)}
                multiSearchParams={multiSearchParams}
                locale={language}
                partialTranslations={partialTranslations}
                headerInformation={headerInformation}
                baseDataTestId={baseDataTestId}
                isPricePerNightEnabled={isPricePerNightEnabled(featureToggle) as boolean}
              />
            )}
          </Box>
        )}
        {isHotelAvailable &&
          (!isHotelAvailable?.isAvailable ||
            isHotelAvailable?.isOpeningSoon ||
            isHotelAvailable.hasMlos) && (
            <SRHotelNotification
              description={
                isHotelAvailable.hasMlos
                  ? isMlos
                  : isHotelAvailable?.isOpeningSoon
                    ? openingSoon
                    : fullyBooked
              }
            />
          )}
        <PromotionsNotification promotionBannerData={promotionBannerData} viewType="banner" />
        {orderedHotels.map((hotelData: SingleHotelAvailability, index) => (
          <SRHotelCard
            roomTypes={roomTypes}
            data={hotelData}
            multiSearchParams={multiSearchParams}
            partialTranslations={partialTranslations}
            key={hotelData.name}
            locale={language || 'en'}
            variant={variant}
            pricePerNight={pricePerNight}
            eagerLoad={variant === Area.PI && index < 4}
            isPricePerNightEnabled={isPricePerNightEnabled(featureToggle) as boolean}
            isSplitView={isSplitView}
            responsive={responsive}
          />
        ))}
        {hasMore && (
          <Text
            key={currentPage}
            align="center"
            {...loadingTextStyles}
            data-testid={formatDataTestId(baseDataTestId, 'loading')}
          >
            {t('searchresults.list.hotel.loading')}
          </Text>
        )}
      </InfiniteScroller>
    </>
  );
}

const loadingTextStyles = {
  color: 'darkGrey1',
  fontSize: 'xl',
  fontWeight: 'bold',
  lineHeight: '3',
  my: '2xl',
};

const getTotalResultsTextStyles = (isSplitView?: boolean) => ({
  m: 'auto',
  mb: 'var(--chakra-space-sm)',
  color: 'darkGrey1',
  w: isSplitView
    ? 'fit-content'
    : {
        mobile: 'full',
        md: '45rem',
        lg: '50.5rem',
        xl: '54rem',
      },
});

const getPricePerNightWrapperStyles = (isSplitView?: boolean) => ({
  ...getTotalResultsTextStyles(isSplitView),
  ...(isSplitView && { w: 'unset', flexGrow: 1 }),
});

const totalResultsLegacyTypography = {
  lineHeight: '2',
  fontSize: 'sm',
  fontWeight: 'medium',
};

const totalResultsSemanticTypography = {
  textStyle: 'body-s-regular',
};

const mobileViewTypeButtonStyles = {
  pos: 'absolute',
  top: 'var(--chakra-space-lg)',
  right: 'var(--chakra-space-sm)',
  zIndex: '10',
  background: 'white',
  padding: 'var(--chakra-space-sm)',
  cursor: 'pointer',
} as const;
