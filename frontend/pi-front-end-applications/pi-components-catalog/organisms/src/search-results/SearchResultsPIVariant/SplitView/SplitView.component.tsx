import { Box, Flex, Text } from '@chakra-ui/react';
import type { FlexProps } from '@chakra-ui/react';
import {
  HeaderInformationData,
  SingleHotelAvailability,
  SRMultiSearchParamsType,
  SRPartialTranslationsType,
  SRFiltersType,
  FilterByLabels,
} from '@whitbread-eos/api';
import {
  Info,
  Notification,
  LoadingSpinner,
  DescriptionBox,
  PromotionsNotification,
  SwitchToggle,
} from '@whitbread-eos/atoms';
import { SRControls, VIEW_TYPE_CONSTANTS } from '@whitbread-eos/molecules';
import { type PromotionsInformation } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

import { ListView, MapViewPIVariant as SRMapView } from '../../../index';
import {
  displayNoHotelsFoundWarning,
  notificationWrapperStyles,
} from '../SearchResultsPIVariant.container';

const { listView } = VIEW_TYPE_CONSTANTS;

const DESKTOP_SPLIT_VIEW_MIN_WIDTH = 1100;
const COMPACT_SPLIT_VIEW_MAX_WIDTH = 1250;

export function useSplitViewLayout() {
  const [width, setWidth] = useState<number | null>(null);

  useEffect(() => {
    const updateWidth = () => setWidth(window.innerWidth);
    updateWidth();
    window.addEventListener('resize', updateWidth);
    return () => window.removeEventListener('resize', updateWidth);
  }, []);

  return {
    isSplitViewSupported: width !== null && width > DESKTOP_SPLIT_VIEW_MIN_WIDTH,
    isCompactSplitView: width !== null && width < COMPACT_SPLIT_VIEW_MAX_WIDTH,
  };
}

interface SplitViewProps {
  isCompactSplitView: boolean;
  controlsLabels: { filterLabels: any; buttonLabels: any };
  multiSearchParams: SRMultiSearchParamsType;
  changeViewType: () => void;
  changeSortValue: (value: any) => void;
  handleChangeFilters: (filters: SRFiltersType) => void;
  defaultFilters: string[];
  isPiSortOrderDropdownEnabled: boolean;
  dynamicFilters: FilterByLabels;
  channel: string;
  showSplitViewTotalResults: boolean;
  showSplitViewPricePerNightToggle: boolean;
  resultsMeta: { total: number; currentResults: number };
  partialTranslations: SRPartialTranslationsType;
  baseDataTestId: string;
  t: (key: string) => string;
  shouldDisplayNoHotelsWarning: boolean;
  headerInformationData: HeaderInformationData;
  promotionBannerData?: PromotionsInformation;
  hasHotels: boolean;
  headerAnnouncement?: string;
  isLoading: boolean;
  filters: string;
  currentPage: number;
  availabilityResult: any;
  language: string;
  roomTypes?: (string | undefined)[];
  hotelList: SingleHotelAvailability[];
  fetchNewHotels: () => void;
  items: SingleHotelAvailability[];
  isPricePerNightEnabledOnPi?: boolean;
  hasMore: boolean;
}

export default function SplitView({
  isCompactSplitView,
  controlsLabels,
  multiSearchParams,
  changeViewType,
  changeSortValue,
  handleChangeFilters,
  defaultFilters,
  isPiSortOrderDropdownEnabled,
  dynamicFilters,
  channel,
  showSplitViewTotalResults,
  showSplitViewPricePerNightToggle,
  resultsMeta,
  partialTranslations,
  baseDataTestId,
  t,
  shouldDisplayNoHotelsWarning,
  headerInformationData,
  promotionBannerData,
  hasHotels,
  headerAnnouncement,
  isLoading,
  filters,
  currentPage,
  availabilityResult,
  language,
  roomTypes,
  hotelList,
  fetchNewHotels,
  items,
  isPricePerNightEnabledOnPi,
  hasMore,
}: Readonly<SplitViewProps>) {
  const [splitViewPricePerNight, setSplitViewPricePerNight] = useState<boolean | null>(
    isPricePerNightEnabledOnPi ? false : null
  );

  return (
    <Flex id="listAndMapContainer" {...splitViewContainerStyles}>
      <Box id="listContainer" {...getSplitViewListContainerStyles(isCompactSplitView)}>
        <Box padding={0} maxWidth="100%" flexShrink={0}>
          <SRControls
            viewType={listView}
            labels={controlsLabels}
            sortValue={multiSearchParams.sort}
            onChangeViewType={changeViewType}
            onChangeSortValue={changeSortValue}
            onChangeFilters={handleChangeFilters}
            defaultFilters={defaultFilters}
            isPiSortOrderDropdownEnabled={isPiSortOrderDropdownEnabled}
            dynamicFilters={dynamicFilters}
            channel={channel}
            hideViewToggle
            isSplitView
            rightContent={
              showSplitViewTotalResults && (
                <Flex alignItems="center" gap="md" width="unset" flexGrow={1}>
                  <Text {...splitViewTotalResultsStyles}>
                    {resultsMeta.total} {partialTranslations.searchInformation.content.totalHotels}
                  </Text>
                  {showSplitViewPricePerNightToggle && (
                    <SwitchToggle
                      baseDataTestId={`${baseDataTestId}-price-per-night-rooms-${
                        splitViewPricePerNight ? 'ppn' : 'tp'
                      }`}
                      first={t('searchresults.list.hotel.pricePerNight')}
                      second={t('searchresults.list.hotel.totalPrice')}
                      onToggle={() => setSplitViewPricePerNight((prev) => !prev)}
                      defaultSelected={false}
                      fontSize="14px"
                    />
                  )}
                </Flex>
              )
            }
          />
        </Box>
        <Box
          id="listScrollContainer"
          padding={0}
          maxWidth="100%"
          flex="1 1 auto"
          minHeight={0}
          overflowY="auto"
        >
          {shouldDisplayNoHotelsWarning &&
            displayNoHotelsFoundWarning(
              headerInformationData?.headerInformation?.results.notifications.noResults
            )}
          <PromotionsNotification promotionBannerData={promotionBannerData} viewType="warning" />
          {hasHotels && headerAnnouncement && (
            <Notification
              variant="info"
              status="info"
              description={<DescriptionBox html={headerAnnouncement} />}
              svg={<Info />}
              isInnerHTML
              wrapperStyles={notificationWrapperStyles}
            />
          )}
          {!isLoading &&
            !hasHotels &&
            filters.length > 0 &&
            displayNoHotelsFoundWarning(
              partialTranslations.searchInformation.content.results?.notifications
                ?.noFilteredHotels ?? ''
            )}
          {isLoading && <LoadingSpinner loadingText={t('searchresults.list.hotel.loading')} />}
          {!isLoading && (
            <ListView
              featureToggle={{ isPricePerNightEnabledOnPi }}
              variant="pi"
              changeViewType={changeViewType}
              headerInformation={headerInformationData}
              baseDataTestId={baseDataTestId}
              currentPage={currentPage}
              isHotelAvailable={availabilityResult}
              language={language}
              multiSearchParams={multiSearchParams}
              roomTypes={roomTypes}
              orderedHotels={hotelList}
              partialTranslations={partialTranslations as SRPartialTranslationsType}
              resultsMeta={resultsMeta}
              hasMore={hasMore}
              items={items}
              fetchNewHotels={fetchNewHotels}
              promotionBannerData={promotionBannerData}
              scrollableTarget="listScrollContainer"
              isSplitView
              pricePerNight={splitViewPricePerNight}
              onTogglePricePerNight={() => setSplitViewPricePerNight((prev) => !prev)}
            />
          )}
        </Box>
      </Box>
      <Box id="mapContainer" {...splitViewMapContainerStyles}>
        {!isLoading && (
          <SRMapView
            isPricePerNightEnabled={isPricePerNightEnabledOnPi}
            variant="pi"
            baseDataTestId={baseDataTestId}
            headerInformation={headerInformationData}
            partialTranslations={partialTranslations as SRPartialTranslationsType}
            locale={language}
            multiSearchParams={multiSearchParams}
            items={items}
            isSplitViewActive
            pricePerNight={splitViewPricePerNight}
          />
        )}
      </Box>
    </Flex>
  );
}

const splitViewContainerStyles = {
  width: '100%',
  alignItems: 'flex-start',
  gap: '10px',
  maxHeight: '80vh',
  pl: 'xlg',
} as FlexProps;

const SPLIT_VIEW_DEFAULT_LIST_WIDTH = '50%';
const SPLIT_VIEW_COMPACT_LIST_WIDTH = '60%';

const getSplitViewListContainerStyles = (isCompactSplitView: boolean) => {
  const listWidth = isCompactSplitView
    ? SPLIT_VIEW_COMPACT_LIST_WIDTH
    : SPLIT_VIEW_DEFAULT_LIST_WIDTH;

  return {
    flex: `0 0 ${listWidth}`,
    maxWidth: listWidth,
    minWidth: '0',
    height: '80vh',
    display: 'flex',
    flexDirection: 'column',
  } as const;
};

const splitViewMapContainerStyles = {
  flex: '1',
  minWidth: '0',
  width: '50%',
  height: '80vh',
  position: 'sticky',
  top: '100px',
  marginTop: '25px',
  marginRight: '20px',
  backgroundColor: 'baseWhite',
  borderRadius: '25px 5px 5px 5px',
  overflow: 'hidden',
} as const;

const splitViewTotalResultsStyles = {
  m: '0',
  w: 'fit-content',
  lineHeight: '40px',
  fontSize: 'sm',
  fontWeight: 'medium',
  color: 'darkGrey1',
  whiteSpace: 'nowrap',
} as const;
