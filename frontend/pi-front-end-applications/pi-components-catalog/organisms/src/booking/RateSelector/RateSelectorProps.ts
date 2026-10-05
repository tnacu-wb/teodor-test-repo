// Import necessary types
import { QueryClient } from '@tanstack/react-query';
import type {
  Channel,
  HIHotelAvailabilityResponse,
  RoomTypeCodeMapped,
  CompanyData,
} from '@whitbread-eos/api';
import type { PromoActionsType } from '@whitbread-eos/utils';

// Interface definition
export interface RateSelectorProps {
  channel: Channel;
  variant: string;
  queryClient: QueryClient;
  hotelAvailabilityResponse: HIHotelAvailabilityResponse;
  isParentAnalytics: boolean;
  isHotelOpeningSoon: boolean;
  arrival?: string;
  departure?: string;
  numberOfUnits?: number;
  numberOfNights?: number;
  isLessThanSm: boolean | undefined;
  isLessThanMd: boolean | undefined;
  isLessThanLg: boolean | undefined;
  isCityTaxEnabled?: boolean;
  prevReservationId?: string;
  mappedRoomLabels: RoomTypeCodeMapped;
  isSilentFeatureFlagEnabled?: boolean;
  thirdParties?: string;
  companyData?: CompanyData;
  isPrePopulateBillingAddressEnabled?: boolean;
  targetRatePlanCode?: string;
  globalTranslationForRooms?: { [key: string]: string };
  promoActions?: PromoActionsType;
  isCityTaxBreakdownEnabled?: boolean;
  isSoftBundlesVisible?: boolean;
  hasRateSelectorTitleDescription?: boolean;
}
