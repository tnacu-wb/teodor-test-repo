import AnalyticsProviderCCUI from './AnalyticsProviderCCUI';
import AnalyticsProvider from './AnalyticsProviderContainer';
import updateAmendPageAnalytics from './amendPageAnalytics';
import analytics, {
  timeTrackerSeconds,
  restaurantFormAnalytics,
  analyticsConfirmation,
  analyticsTrackings,
} from './analytics';
import updateAncillariesAnalytics, { extrasPackagesAnalyticsMap } from './ancillariesAnalytics';
import initConfirmationAnalytics, {
  analyticsConfirmationPageName,
  updateConfirmationAnalytics,
  updateConfirmationPageAnalytics,
} from './confirmationPageAnalytics';
import updateDashboardAnalytics from './dashboardAnalytics';
import updateDestinationPageAnalytics from './destinationPageAnalytics';
import updateHotelDisplayPageAnalytics from './hotelDisplayPageAnalytics';
import updateSearchResultsAnalytics from './searchResultsAnalytics';
import setAnalyticsUser from './setAnalyticsUser';
import setPageAnalytics, { formatAnalyticsFunnelStep } from './setPageAnalytics';

export {
  AnalyticsProvider,
  AnalyticsProviderCCUI,
  restaurantFormAnalytics,
  analytics,
  analyticsConfirmation,
  initConfirmationAnalytics,
  updateConfirmationAnalytics,
  updateSearchResultsAnalytics,
  updateHotelDisplayPageAnalytics,
  timeTrackerSeconds,
  setAnalyticsUser,
  analyticsConfirmationPageName,
  formatAnalyticsFunnelStep,
  setPageAnalytics,
  updateDashboardAnalytics,
  updateAmendPageAnalytics,
  analyticsTrackings,
  updateAncillariesAnalytics,
  updateConfirmationPageAnalytics,
  extrasPackagesAnalyticsMap,
  updateDestinationPageAnalytics,
};

export { getHotelLabel, labelsConstants } from './searchResultsAnalytics';
