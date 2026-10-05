import {
  AgentOverrideModal,
  BookingDetails,
  BookingDetailsController,
  BookingHistoryCancelBookingModal,
  BookingHistoryInfoCardContainer,
  BookingInfoCard,
  BookingInfoCardWrapper,
  BookingsHistory,
  CancelBookingModal,
  HotelDetails,
  getTableConfig,
  getTableRedesignDesktopConfig,
  getDashboardRedesignTabletConfig,
  getDashboardRedesignMobileConfig,
} from './account';
import {
  AmendBookingConfirmationContainer,
  AmendContainer,
  AmendPayment,
  BookingSummaryWrapper,
  AmendPaymentCCUI,
  ChangePaymentCCUI,
} from './amend';
import { MealSelection, ExtrasSection } from './ancillaries';
import {
  Auth0SignIn,
  AuthContentManagerBBVariant,
  AuthContentManagerPIVariant,
  AuthGuard,
  LogInBBVariant,
  LoginPIVariant,
  NewPassword,
  OptionalAuthentication,
  ResetPasswordBBVariant,
  ResetPasswordPIVariant,
} from './authentication';
import { RateSelector } from './booking';
import PaymentDetailsTabs from './booking/PaymentDetailsTabs';
import PaymentTypeContainer from './booking/PaymentType';
import MonthTabsCarousel from './carousel-view/MonthTabsCarousel';
import {
  AgentMemo,
  BookingSummary,
  BookingSummaryCard,
  BookingSummaryMobile,
  BookingSummaryProps,
  ChangeLog,
  FooterWrapper,
  Header,
  HeaderProps,
} from './common';
import DLPHotelCard from './destination/HotelCard';
import DLPMapHotelCard from './destination/MapHotelCard';
import MapViewDLPVariant from './destination/MapViewDLPVariant';
import { BookingSummaryContainer, GuestDetailsBBContainer } from './guest-details';
import { Location } from './hotel-details';
import BillingAddress from './payment/BillingAddress';
import TotalCostPayment from './payment/TotalCostPayment';
import { PreCheckInForm, PreCheckInFormBookingDetails } from './pre-check-in';
import ResultsContainer from './search-account/ResultsContainer';
import ResultListContainer from './search-bookings/ResultsTableContainer';
import {
  APP_VARIANT,
  HotelCard as SRHotelCard,
  ListView,
  MapHotelCard as SRMapHotelCard,
  MapViewBBVariant,
  MapViewCCUIVariant,
  MapViewPIVariant,
  SearchResultsBBVariant,
  SearchResultsCCUIVariant,
  SearchResultsPIVariant,
} from './search-results';
import { getFallbackSearchPlace } from './search-results/utilities';
import BBSearchContainer from './search/bb/Search.container';
import CCUISearchContainer from './search/ccui/Search.container';
import SearchQueryWrapper from './search/pi';
import { getSearchParams } from './search/utilities';
import { appWrapper } from './search/utilities/appWrapper';
import { CreatePromotionCodeFormDetails } from './unique-promotions';
import { generateMonthData, type MonthData } from './utils/common/date-utils';

const CCUISearchContainerWrapper = appWrapper(CCUISearchContainer);
const BBSearchContainerWrapper = appWrapper(BBSearchContainer);

export type { BookingSummaryProps };
export { CreatePromotionCodeFormDetails };
export { getSearchParams };
export {
  SearchQueryWrapper as PISearchContainer,
  CCUISearchContainerWrapper as CCUISearchContainer,
  BBSearchContainerWrapper as BBSearchContainer,
};
export { AgentMemo, BookingSummary, BookingSummaryCard, BookingSummaryMobile };
export { BookingSummaryContainer };
export { ChangeLog };
export { GuestDetailsBBContainer };
export {
  BookingDetailsController,
  HotelDetails,
  CancelBookingModal,
  BookingDetails,
  BookingInfoCard,
  BookingInfoCardWrapper,
  AgentOverrideModal,
  BookingsHistory,
  BookingHistoryCancelBookingModal,
  BookingHistoryInfoCardContainer,
  getTableConfig,
  getTableRedesignDesktopConfig,
  getDashboardRedesignTabletConfig,
  getDashboardRedesignMobileConfig,
};

export { ResultListContainer };
export { DLPHotelCard, MapViewDLPVariant, DLPMapHotelCard };
export { ResultsContainer as SearchAccountResultsContainer };
export {
  AmendContainer,
  AmendBookingConfirmationContainer,
  AmendPayment,
  BookingSummaryWrapper,
  AmendPaymentCCUI,
  ChangePaymentCCUI,
};

export { FooterWrapper };

export { Header };
export type { HeaderProps };
export { MealSelection, ExtrasSection };
export {
  ListView,
  MapViewPIVariant,
  MapViewBBVariant,
  MapViewCCUIVariant,
  SRHotelCard,
  SRMapHotelCard,
  SearchResultsPIVariant,
  SearchResultsBBVariant,
  SearchResultsCCUIVariant,
  APP_VARIANT,
};
export {
  Auth0SignIn,
  AuthGuard,
  AuthContentManagerPIVariant,
  AuthContentManagerBBVariant,
  LoginPIVariant,
  LogInBBVariant,
  NewPassword,
  OptionalAuthentication,
  ResetPasswordPIVariant,
  ResetPasswordBBVariant,
};
export { RateSelector };
export { Location };
export { BillingAddress };
export { TotalCostPayment };
export { PaymentTypeContainer };
export { PaymentDetailsTabs };
export { getFallbackSearchPlace };
export { PreCheckInForm };
export { PreCheckInFormBookingDetails };
export { MonthTabsCarousel };
export { generateMonthData, type MonthData };
