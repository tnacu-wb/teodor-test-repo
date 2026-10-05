import React from 'react';

export interface HotelPrice {
  hotelId: string;
  hotelName: string;
  hotelLink?: string;
  prices: HotelPriceItem[];
}

export interface HotelPriceItem {
  date: string;
  amount: number | null;
  currency: string;
  roomCategory?: string;
}

export interface FormattedDate {
  weekday: string;
  day: string;
  month?: string;
}

export type SupportedLocales = 'en-GB' | 'de-DE';

export type SortBy = 'distance' | 'price';

export interface DateFormatOptions {
  isSmallerThanSm?: boolean;
  locale?: SupportedLocales;
}

export interface TableHeaderProps {
  currentDates: string[];
  sortedByDate: string | null;
  handleDateSort: (date: string) => void;
  handleScrollLeft: () => void;
  handleScrollRight: () => void;
  isScrollLeftDisabled: boolean;
  isScrollRightDisabled?: boolean;
  isLoading?: boolean;
  isMobile?: boolean;
  isSmallerThanSm?: boolean;
  tableHeaderRef?: React.RefObject<HTMLTableSectionElement>;
  handleSortToggle?: () => void;
  locale?: SupportedLocales;
}

export interface SkeletonTableRowProps {
  isMobile: boolean;
  currentDates: string[];
  index?: number;
}

export interface FloatingHeaderProps {
  currentDates: string[];
  sortedByDate: string | null;
  handleDateSort: (date: string) => void;
  handleScrollLeft: () => void;
  handleScrollRight: () => void;
  isScrollLeftDisabled: boolean;
  isScrollRightDisabled?: boolean;
  isLoading?: boolean;
  isMobile?: boolean;
  isSmallerThanSm?: boolean;
  handleSortToggle?: () => void;
  locale?: SupportedLocales;
  getColumnWidth: () => string;
}

export interface PriceStyleResult {
  textBg: string;
  color: string;
  fontWeight: number | string;
}

export interface HotelAvailability {
  availableDate: string;
  currency: string;
  minimumRate: number;
  rateCode: string;
  rateClassification: string;
  roomType: string;
  roomCategory: string;
  quantity: number;
  hasMlosRestriction: boolean;
  hasClosedRestriction: boolean;
  minimumNights: number;
}

export interface HotelLinks {
  detailsPage?: string;
}

export interface PriceFinderHotel {
  hotelCode: string;
  hotelName: string;
  availabilities?: HotelAvailability[];
  links?: HotelLinks; // Optional field for hotel detail page links
}

export interface LowestRate {
  price: number;
  currency: string;
}

export interface PriceFinderResponse {
  priceFinderOperaHotelAvailabilitiesDtoList: PriceFinderHotel[];
  page: number;
  pageSize: number;
  total: number;
  lowestMonthlyRate: LowestRate;
}

export interface GetLowestRatesByLocationIdResponse {
  getLowestRatesByLocationId: PriceFinderResponse;
}
