import { AnalyticsDataAmend } from '@whitbread-eos/api';

import analytics from './analytics';

const updateAmendPageAnalytics = (
  {
    change,
    revenue,
    extrasRevenueChange,
    foodRevenueChange,
    nightsChange,
    roomTypeChange,
    roomsChange,
    totalRevenueChange,
    validation,
  }: AnalyticsDataAmend,
  originalBookingReference: string
) => {
  const amendAnalytics = {
    change: change,
    revenue: revenue,
    extrasRevenueChange: extrasRevenueChange,
    foodRevenueChange: foodRevenueChange,
    nightsChange: nightsChange,
    roomTypeChange: roomTypeChange,
    roomsChange: roomsChange,
    totalRevenueChange: totalRevenueChange,
    validation: validation,
  };

  window.sessionStorage.setItem(
    'AmendAnalytics',
    JSON.stringify({
      ...amendAnalytics,
      originalBookingReference: originalBookingReference,
    })
  );

  return analytics.update({ ...amendAnalytics });
};

export default updateAmendPageAnalytics;
