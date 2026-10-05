import { useState, useEffect, useCallback } from 'react';

import { transformApiDataToHotelPrices } from '../helpers';
import { HotelPrice, GetLowestRatesByLocationIdResponse } from '../types';

interface UseHotelDataProps {
  currentDates: string[];
  currentStartDate: Date;
  data: GetLowestRatesByLocationIdResponse | null;
  isLoading: boolean;
  isError: boolean;
}

export const useHotelData = ({
  currentDates,
  currentStartDate,
  data,
  isLoading,
  isError,
}: UseHotelDataProps) => {
  const [hotelData, setHotelData] = useState<HotelPrice[]>([]);
  const [sortedByDate, setSortedByDate] = useState<string | null>(null);
  const [globalLowestPrice, setGlobalLowestPrice] = useState<number | null>(null);

  useEffect(() => {
    if (data && !isLoading && !isError) {
      const typedData = data as GetLowestRatesByLocationIdResponse;
      const apiData =
        typedData?.getLowestRatesByLocationId?.priceFinderOperaHotelAvailabilitiesDtoList || [];

      const transformedData = transformApiDataToHotelPrices(apiData, currentDates);
      setHotelData(transformedData);

      // Use lowestMonthlyRate from API response instead of calculating it
      const lowestMonthlyRate =
        typedData?.getLowestRatesByLocationId?.lowestMonthlyRate?.price || null;
      setGlobalLowestPrice(lowestMonthlyRate);
    }
    setSortedByDate(null);
  }, [currentStartDate, data, isLoading, isError, currentDates]);

  const handleDateSort = useCallback(
    (clickedDate: string) => {
      const dateIndex = currentDates.indexOf(clickedDate);
      if (dateIndex === -1) return;

      setHotelData((prevData) => {
        const sortedHotels = [...prevData].sort((a, b) => {
          const priceA = a.prices[dateIndex]?.amount;
          const priceB = b.prices[dateIndex]?.amount;

          if (priceA === null && priceB === null) return 0;
          if (priceA === null) return 1;
          if (priceB === null) return -1;

          return priceA - priceB;
        });
        return sortedHotels;
      });

      setSortedByDate(clickedDate);
    },
    [currentDates]
  );

  return {
    hotelData,
    sortedByDate,
    globalLowestPrice,
    handleDateSort,
    setHotelData,
  };
};
