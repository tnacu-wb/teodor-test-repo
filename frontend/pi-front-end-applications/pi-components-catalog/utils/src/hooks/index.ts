import useAmendCookieValidation from './use-amend-cookie-validation';
import useBookingConfimationData from './use-booking-confirmation-data';
import useBookingSpinner from './use-booking-spinner';
import useCompanyDetails from './use-company-details';
import useCookieForABTesting from './use-cookie-for-ab-testing';
import useCookieWatcher from './use-cookie-watcher';
import useCustomLocale from './use-custom-locale';
import useCustomLocaleAppRouter from './use-custom-locale-app-router';
import useDebounce from './use-debounce';
import {
  useForDiscountedRateMicroSite,
  useForDiscountedRateFlag,
  useDiscountRateInfoHotelAvailability,
  getRatePlanCode,
  getCorporateDiscountRatePlanCode,
  useUpdateRateName,
  useGetDiscountRateComapnyId,
  useGetDiscountRateReservationData,
} from './use-discounted-rate';
import useElementDimensions from './use-element-dimensions';
import useElementVisited from './use-element-visited';
import useFeatureSwitch from './use-feature-switch';
import useFeatureToggle from './use-feature-toggle';
import useGetCountryLanguage from './use-get-country-language';
import { useHorizontalScroll } from './use-horizontal-scroll';
import {
  useHotelAvailability,
  useHotelAvailabilityBB,
  useHotelRatesInformationBB,
  useHotelAvailabilityCCUI,
  useHotelRatesInformationCCUI,
  useHotelRatesInformationDiscountRate,
  useHotelAvailabilityDiscountRate,
} from './use-hotel-availability';
import useHotelBrands from './use-hotelBrand';
import useInnBusinessLogin from './use-innbusiness-login';
import useIsExternalSearch from './use-is-external-search';
import useMobileControlsDisplay from './use-mobile-controls-display';
import useSetOrientation from './use-orientation';
import useOutsideClick from './use-outside-click';
import usePackages from './use-packages';
import { usePaymentAnalytics } from './use-payment-analytics';
import usePaymentData from './use-payment-data';
import usePaymentMethod from './use-payment-method';
import { usePaymentPaypal } from './use-payment-paypal';
import usePollBasketStatus from './use-poll-basket-status';
import usePromoTranslation from './use-promo-translation';
import usePromotionsNotification from './use-promotions-notification';
import useSetScreenSize, { useScreenSize } from './use-screensize';
import useScrollVisibility from './use-scroll-visibility';
import useScrolledPast from './use-scrolled-past';
import useSemanticTypography from './use-semantic-typography';
import useSilentRoomsMatch from './use-silent-rooms-match';
import useSoftBundles from './use-soft-bundles';
import useStaticHotelInformation from './use-static-hotel-information';
import useUpdateSearchParams from './use-update-search-params';
import useUserDetails from './use-user-details';
import { useAuth0AccessToken } from './useAuth0AccessToken';

export {
  useSetOrientation,
  useScreenSize,
  useSetScreenSize,
  useStaticHotelInformation,
  useAmendCookieValidation,
  useCustomLocale,
  useCustomLocaleAppRouter,
  useBookingSpinner,
  useCookieForABTesting,
  useHotelAvailability,
  useHotelAvailabilityBB,
  useHotelRatesInformationBB,
  useHotelAvailabilityCCUI,
  useHotelRatesInformationCCUI,
  usePackages,
  usePaymentData,
  usePaymentAnalytics,
  useUserDetails,
  useCompanyDetails,
  useDebounce,
  useBookingConfimationData,
  usePollBasketStatus,
  usePromoTranslation,
  useFeatureSwitch,
  useFeatureToggle,
  useIsExternalSearch,
  useMobileControlsDisplay,
  useScrolledPast,
  useElementVisited,
  useHotelBrands,
  useSilentRoomsMatch,
  usePaymentPaypal,
  useGetCountryLanguage,
  useUpdateSearchParams,
  useScrollVisibility,
  usePaymentMethod,
  useElementDimensions,
  useForDiscountedRateMicroSite,
  useForDiscountedRateFlag,
  useHotelRatesInformationDiscountRate,
  useHotelAvailabilityDiscountRate,
  useDiscountRateInfoHotelAvailability,
  getRatePlanCode,
  getCorporateDiscountRatePlanCode,
  useUpdateRateName,
  useGetDiscountRateComapnyId,
  useGetDiscountRateReservationData,
  usePromotionsNotification,
  useCookieWatcher,
  useInnBusinessLogin,
  useOutsideClick,
  useHorizontalScroll,
  useSoftBundles,
  useSemanticTypography,
  useAuth0AccessToken,
};

export * from './use-ipage-submission';
export * from './use-local-storage';
export * from './use-orientation';
export * from './use-request';
export * from './use-session-storage';
