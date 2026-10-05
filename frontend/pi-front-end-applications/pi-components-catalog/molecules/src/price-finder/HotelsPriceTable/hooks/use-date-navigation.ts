import { AnalyticsData } from '@whitbread-eos/api';
import { useState, useCallback, useMemo, useLayoutEffect, startTransition } from 'react';

import { TABLE_CONFIG } from '../constants';
import { generateDates } from '../helpers';

declare global {
  interface Window {
    analyticsData: AnalyticsData;
    // satellite required for adobe analytics on the confirmation Page
    __satelliteLoaded: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    _satellite: any;
  }
}

export const useDateNavigation = (
  onDateChange?: (newDate: string) => void,
  externalDate?: string
) => {
  const [startOffset, setStartOffset] = useState(0);

  // Track the last external date to detect real external changes
  const [lastExternalDate, setLastExternalDate] = useState(externalDate);

  useLayoutEffect(() => {
    // When external date changes (month selection), reset to show 1st of selected month
    // Use layoutEffect for synchronous updates to prevent visual flashing
    if (externalDate && externalDate !== lastExternalDate) {
      setLastExternalDate(externalDate);
      setStartOffset(0); // Reset to show 1st of the selected month
    }
  }, [externalDate, lastExternalDate]);

  const currentStartDate = useMemo(() => {
    if (externalDate) {
      // If external date is provided, use it as the base date + offset
      const date = new Date(externalDate);
      date.setDate(date.getDate() + startOffset);
      return date;
    }
    const date = new Date();
    date.setDate(date.getDate() + startOffset);
    return date;
  }, [startOffset, externalDate]);

  const { currentDates, arrival } = useMemo(() => {
    const dates = generateDates(currentStartDate, TABLE_CONFIG.DAYS_TO_SHOW);
    const arrivalString = currentStartDate.toISOString().split('T')[0];
    return {
      currentDates: dates,
      arrival: arrivalString,
    };
  }, [currentStartDate]);

  const handleScrollLeft = useCallback(() => {
    const newOffset = startOffset - TABLE_CONFIG.SCROLL_OFFSET;

    const baseDate = externalDate ? new Date(externalDate) : new Date();
    baseDate.setDate(baseDate.getDate() + newOffset);

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const todayLocalString = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(
      2,
      '0'
    )}-${String(today.getDate()).padStart(2, '0')}`;

    let finalOffset = newOffset;
    let finalStartDate = baseDate;

    const baseDateString = `${baseDate.getFullYear()}-${String(baseDate.getMonth() + 1).padStart(
      2,
      '0'
    )}-${String(baseDate.getDate()).padStart(2, '0')}`;

    if (baseDateString < todayLocalString) {
      finalOffset = 0;
      finalStartDate = new Date(today);
    }

    // Batch state updates to prevent multiple renders
    setStartOffset(finalOffset);

    if (onDateChange) {
      // Use local date string format to avoid timezone conversion issues
      const newDateString = `${finalStartDate.getFullYear()}-${String(
        finalStartDate.getMonth() + 1
      ).padStart(2, '0')}-${String(finalStartDate.getDate()).padStart(2, '0')}`;
      setLastExternalDate(newDateString);

      // Use startTransition to make parent updates non-blocking for smoother scrolling
      startTransition(() => {
        onDateChange(newDateString);
      });
    }
    window.__satelliteLoaded && window._satellite.track('priceFinderHotelSelected');
  }, [startOffset, onDateChange, externalDate, setLastExternalDate]);

  const handleScrollRight = useCallback(() => {
    const newOffset = startOffset + TABLE_CONFIG.SCROLL_OFFSET;

    const baseDate = externalDate ? new Date(externalDate) : new Date();
    baseDate.setDate(baseDate.getDate() + newOffset);

    const endDate = new Date(baseDate);
    endDate.setDate(endDate.getDate() + TABLE_CONFIG.DAYS_TO_SHOW - 1);

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const maxAllowedDate = new Date(today);
    maxAllowedDate.setDate(maxAllowedDate.getDate() + 371);

    if (endDate > maxAllowedDate) {
      return;
    }

    // Batch state updates to prevent multiple renders
    setStartOffset(newOffset);

    if (onDateChange) {
      const newDateString = baseDate.toISOString().split('T')[0];
      setLastExternalDate(newDateString);

      // Use startTransition to make parent updates non-blocking for smoother scrolling
      startTransition(() => {
        onDateChange(newDateString);
      });
    }
    window.__satelliteLoaded && window._satellite.track('priceFinderHotelSelected');
  }, [startOffset, onDateChange, externalDate, setLastExternalDate]);

  // Calculate if scroll left should be disabled based on whether current view includes today's date
  const isScrollLeftDisabled = useMemo(() => {
    const today = new Date();
    today.setHours(0, 0, 0, 0); // Reset time for accurate date comparison

    const currentStartDateCopy = new Date(currentStartDate);
    currentStartDateCopy.setHours(0, 0, 0, 0);

    const currentEndDate = new Date(currentStartDateCopy);
    currentEndDate.setDate(currentEndDate.getDate() + TABLE_CONFIG.DAYS_TO_SHOW - 1);

    // Disable scroll left if today's date is within the current date range
    const todayInRange = today >= currentStartDateCopy && today <= currentEndDate;

    return todayInRange;
  }, [currentStartDate]);

  const isScrollRightDisabled = useMemo(() => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const maxAllowedDate = new Date(today);
    maxAllowedDate.setDate(maxAllowedDate.getDate() + 371);

    const currentStartDateCopy = new Date(currentStartDate);
    currentStartDateCopy.setHours(0, 0, 0, 0);

    const nextScrollStartDate = new Date(currentStartDateCopy);
    nextScrollStartDate.setDate(nextScrollStartDate.getDate() + TABLE_CONFIG.SCROLL_OFFSET);

    const nextScrollEndDate = new Date(nextScrollStartDate);
    nextScrollEndDate.setDate(nextScrollEndDate.getDate() + TABLE_CONFIG.DAYS_TO_SHOW - 1);

    return nextScrollEndDate > maxAllowedDate;
  }, [currentStartDate]);

  return {
    startOffset,
    currentStartDate,
    currentDates,
    arrival,
    handleScrollLeft,
    handleScrollRight,
    isScrollLeftDisabled,
    isScrollRightDisabled,
  };
};
