import { AnalyticsData, GET_LOWEST_RATES_BY_LOCATION_ID } from '@whitbread-eos/api';
import { analytics, useQueryRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useMemo, useRef } from 'react';

import { transformApiDataToHotelPrices } from '../helpers';
import { GetLowestRatesByLocationIdResponse, HotelPrice, LowestRate } from '../types';

declare global {
  interface Window {
    analyticsData: AnalyticsData;
    // satellite required for adobe analytics on the confirmation Page
    __satelliteLoaded: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    _satellite: any;
  }
}

export interface RatesCriteria {
  locationId: string;
  arrival: string;
  daysRange: number;
  showMinimumNights: boolean;
  page: number;
  initialPageSize: number;
  lazyLoadPageSize: number;
  sortBy: 'DISTANCE' | 'PRICE';
  sortDate?: string;
  filterByRoomType?: string[];
}

export interface UseLowestRatesOptions {
  enabled?: boolean;
}

export interface UseLowestRatesResult {
  // Data
  data: GetLowestRatesByLocationIdResponse | null;
  hotelData: HotelPrice[];
  globalLowestPrice: number | null;
  lowestMonthlyRate: LowestRate | null;

  // Loading states
  isLoading: boolean;
  isError: boolean;
  isFetching: boolean;

  // Query management
  refetch: () => void;
}

type HotelsPriceFinder = {
  availabilities?: string[];
  hotelId?: string;
  hotelName?: string;
  links: {
    detailsPage: string;
  };
};

export const useLowestRates = (
  criteria: RatesCriteria,
  currentDates: string[],
  options: UseLowestRatesOptions = {},
  locationName?: string
): UseLowestRatesResult => {
  const { enabled = true } = options;
  const { t } = useTranslation();
  const defaultLocationName = t('priceFinder.MVP.customConfig.defaultLocationName');
  const { data, isLoading, isError, isFetching, refetch } = useQueryRequest(
    [
      'lowestRatesByLocationId',
      criteria.locationId,
      criteria.arrival,
      criteria.sortBy,
      criteria.sortDate,
      criteria.page,
      criteria.initialPageSize,
      criteria.lazyLoadPageSize,
      criteria.filterByRoomType,
    ],
    GET_LOWEST_RATES_BY_LOCATION_ID,
    { criteria },
    {
      enabled: enabled && !!criteria.locationId,
    }
  );

  const monthSelected = new Date(currentDates[0]);

  const numberOfSoldOutResults =
    data?.getLowestRatesByLocationId?.priceFinderOperaHotelAvailabilitiesDtoList.filter(
      (hotel: HotelsPriceFinder) => (hotel.availabilities?.length ?? 0) === 0
    );

  const renderCount = useRef(0);
  const previousCriteria = useRef<RatesCriteria | null>(null);

  useEffect(() => {
    if (!criteria.locationId) return;

    const criteriaHasChanged =
      previousCriteria.current &&
      JSON.stringify(previousCriteria.current) !== JSON.stringify(criteria);

    analytics?.update({
      searchResults: {
        priceFinder: {
          searchLocation: locationName ?? defaultLocationName,
          defaultLocation: !criteriaHasChanged,
          defaultDate: !criteriaHasChanged,
          monthSelected: monthSelected.toLocaleString('en-GB', {
            month: 'long',
          }),
          weekSelected: currentDates,
          pageNumber: criteria.page,
          sortBy: criteria.sortBy,
        },
      },
    });

    renderCount.current += 1;

    previousCriteria.current = { ...criteria };
  }, [criteria]);

  useEffect(() => {
    analytics?.update({
      searchResults: {
        priceFinder: {
          ...window?.analyticsData?.searchResults?.priceFinder,
          numberOfResults:
            data?.getLowestRatesByLocationId?.priceFinderOperaHotelAvailabilitiesDtoList?.length ??
            0,
          numberOfSoldOutResults: numberOfSoldOutResults?.length ?? 0,
        },
      },
    });
  }, [data]);

  // Transform hotel data
  const hotelData = useMemo(() => {
    if (!data?.getLowestRatesByLocationId?.priceFinderOperaHotelAvailabilitiesDtoList) {
      return [];
    }

    const apiData = data.getLowestRatesByLocationId.priceFinderOperaHotelAvailabilitiesDtoList;
    return transformApiDataToHotelPrices(apiData, currentDates);
  }, [data, currentDates]);

  // Extract lowestMonthlyRate for MonthTabsCarousel component
  const lowestMonthlyRate = useMemo(() => {
    const rate = data?.getLowestRatesByLocationId?.lowestMonthlyRate;
    if (!rate?.price) {
      return null;
    }
    return {
      price: rate.price,
      currency: rate.currency || 'GBP', // Default to GBP if currency is missing
    };
  }, [data]);

  // Calculate globalLowestPrice from all hotel minimumRates in the query results
  const globalLowestPrice = useMemo(() => {
    if (!data?.getLowestRatesByLocationId?.priceFinderOperaHotelAvailabilitiesDtoList) {
      return null;
    }

    const hotels = data.getLowestRatesByLocationId.priceFinderOperaHotelAvailabilitiesDtoList;
    let lowestPrice: number | null = null;

    hotels.forEach((hotel: any) => {
      hotel.availabilities?.forEach((availability: any) => {
        const rate = availability.minimumRate;
        if (rate && rate > 0) {
          if (lowestPrice === null || rate < lowestPrice) {
            lowestPrice = rate;
          }
        }
      });
    });

    return lowestPrice;
  }, [data]);

  return {
    // Data
    data: data || null,
    hotelData,
    globalLowestPrice,
    lowestMonthlyRate,

    // Loading states
    isLoading,
    isError,
    isFetching,

    // Query management
    refetch,
  };
};
