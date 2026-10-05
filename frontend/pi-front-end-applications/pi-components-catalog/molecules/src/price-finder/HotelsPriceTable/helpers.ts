import { ROOM_CODES } from '@whitbread-eos/api';

import { THEME_COLORS } from './constants';
import {
  HotelPrice,
  FormattedDate,
  DateFormatOptions,
  PriceStyleResult,
  PriceFinderHotel,
} from './types';

export const generateDates = (startDate: Date, days: number): string[] => {
  const result: string[] = [];
  const baseTime = startDate.getTime();

  for (let i = 0; i < days; i++) {
    const currentDate = new Date(baseTime + i * 24 * 60 * 60 * 1000);
    result.push(currentDate.toISOString().split('T')[0]);
  }
  return result;
};

export const formatDate = (isoDate: string, options?: DateFormatOptions): FormattedDate => {
  const { isSmallerThanSm = false, locale = 'en-GB' } = options || {};
  const date = new Date(isoDate);

  // Handle invalid dates
  if (isNaN(date.getTime())) {
    return { weekday: '', day: '' };
  }

  const dtfOptions: Intl.DateTimeFormatOptions = {
    weekday: 'short',
    day: 'numeric',
    month: 'short',
  };

  try {
    const parts = new Intl.DateTimeFormat(locale, dtfOptions).formatToParts(date);
    let weekday = parts.find((p) => p.type === 'weekday')?.value || '';
    const day = parts.find((p) => p.type === 'day')?.value || '';
    let month = parts.find((p) => p.type === 'month')?.value || '';

    if (isSmallerThanSm) {
      // Replaces 3-letter day with 2-letter version
      weekday = weekday.slice(0, 2);
    }

    // Always truncate month to 3 characters
    month = month.slice(0, 3);

    // Return separate month for the 1st day of the month
    const isFirstDayOfMonth = date.getDate() === 1;
    const monthToReturn = isFirstDayOfMonth ? month : undefined;

    return { weekday, day, month: monthToReturn };
  } catch (error) {
    // Fallback for any formatting errors
    return { weekday: '', day: '' };
  }
};

export const isToday = (dateString: string): boolean => {
  const today = new Date().toISOString().split('T')[0];
  return dateString === today;
};

export const getPriceStyle = (
  price: number | null,
  lowestPriceForComparison: number | null,
  highlightedPriceRange = 5,
  priceConfig?: {
    highlightedPriceRangeStep?: number;
    highlightedPriceRangeMin?: number;
    highlightedPricePrimaryColour?: string;
    highlightedPriceSecondaryColour?: string;
    path?: string;
  }
): PriceStyleResult => {
  if (!price)
    return {
      textBg: 'transparent',
      color: THEME_COLORS.disabledColor,
      fontWeight: 500,
    };

  // Extract color values with fallbacks to THEME_COLORS
  const primaryColour = priceConfig?.highlightedPricePrimaryColour ?? THEME_COLORS.lowestPrice;
  const secondaryColour =
    priceConfig?.highlightedPriceSecondaryColour ?? THEME_COLORS.nearLowestPrice;

  // Check if we're in campaign mode (path is set and not 'default')
  const path = priceConfig?.path || '';
  const isCampaignMode = !!path && path !== 'default';

  // Determine the rangeMin based on campaign mode
  let rangeMin: number;
  if (isCampaignMode && priceConfig?.highlightedPriceRangeMin !== undefined) {
    // Campaign mode: use highlightedPriceRangeMin from priceFinderConfig
    rangeMin = priceConfig.highlightedPriceRangeMin;
  } else {
    // Default mode: use globalLowestPrice
    if (!lowestPriceForComparison) {
      return { textBg: 'transparent', color: 'black !important', fontWeight: 500 };
    }
    rangeMin = lowestPriceForComparison;
  }

  const rangeStep = priceConfig?.highlightedPriceRangeStep ?? highlightedPriceRange;

  // Apply primary color if price is at or below rangeMin
  if (price <= rangeMin) {
    return {
      textBg: primaryColour,
      color: 'black !important',
      fontWeight: 500,
    };
  }

  // Apply secondary color only in default mode (not campaign) when within range
  if (!isCampaignMode && price <= rangeMin + rangeStep) {
    return {
      textBg: secondaryColour,
      color: 'black !important',
      fontWeight: 500,
    };
  }

  // Otherwise return transparent
  return { textBg: 'transparent', color: 'black', fontWeight: 500 };
};

export const transformApiDataToHotelPrices = (
  apiData: PriceFinderHotel[],
  currentDates: string[]
): HotelPrice[] => {
  return apiData.map((hotel) => ({
    hotelId: hotel.hotelCode,
    hotelName: hotel.hotelName,
    hotelLink: hotel.links?.detailsPage,
    prices: currentDates.map((date) => {
      // Find availability for this specific date
      const availability = hotel.availabilities?.find((avail) => avail.availableDate === date);

      // Return the minimumRate if available and > 0, otherwise null
      return {
        date,
        amount:
          availability?.minimumRate && availability.minimumRate > 0
            ? availability.minimumRate
            : null,
        currency: availability?.currency || 'GBP',
        roomCategory: availability?.roomCategory ?? ROOM_CODES.single,
      };
    }),
  }));
};

export const setRoomTypeOccupancy = (roomTypeCode: string) => {
  switch (roomTypeCode) {
    case ROOM_CODES.double:
      return { roomTypeCode: ROOM_CODES.double, adults: '2', children: '0' };
    case ROOM_CODES.family:
      return { roomTypeCode: ROOM_CODES.family, adults: '2', children: '1' };
    case ROOM_CODES.twin:
      return { roomTypeCode: ROOM_CODES.twin, adults: '2', children: '0' };
    case ROOM_CODES.accessible:
      return { roomTypeCode: ROOM_CODES.accessible, adults: '1', children: '0' };
    default:
      return { roomTypeCode: ROOM_CODES.single, adults: '1', children: '0' };
  }
};

export const buildHotelQueryParams = (selectedDate: string, roomTypeCode: string): string => {
  const date = new Date(selectedDate);
  const day = String(date.getDate()).padStart(2, '0');
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const year = String(date.getFullYear());
  const roomTypeOccupancy = setRoomTypeOccupancy(roomTypeCode);

  const params = new URLSearchParams({
    ARRdd: day,
    ARRmm: month,
    ARRyyyy: year,
    NIGHTS: '1',
    ROOMS: '1',
    ADULT1: roomTypeOccupancy.adults,
    CHILD1: roomTypeOccupancy.children,
    COT1: '0',
    INTTYP1: roomTypeOccupancy.roomTypeCode,
  });

  return params.toString();
};
