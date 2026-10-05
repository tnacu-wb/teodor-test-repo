import { MealsSelectionDetailsPerRoom } from './ancillariesPackages';
import { CurrencyType } from './common';
import { AccessibleRoom } from './graphql';

export type BookingSummaryVariantType = 'mobile' | 'desktop';

export interface BookingSummaryHotelInformationProps {
  hotelName: string;
  hotelAddress: string[];
  hotelCountry?: string;
  hotelBrand?: string;
}

export interface BookingSummaryTotalCostProps {
  initialTotalCost?: number;
  amount?: number;
  currency: string;
  meals?: MealsSelectionDetailsPerRoom[];
  donations?: number;
  showVATMessage?: boolean;
  discount?: number;
  newTotalCost?: number;
  previousTotalCost?: number;
}

export interface BookingSummaryRateInformationProps {
  rate: string;
  noRooms: number;
  noNights: number;
  rateDescription?: string;
  rateTags?: string[];
}

export interface BookingSummaryRoomInformationProps {
  roomName?: string;
  roomType?: string;
  nrAdults?: number;
  nrChildren?: number;
  selectedMeals?: MealsSelectionDetailsPerRoom;
  accessibleRoom?: AccessibleRoom;
  reservationId?: string;
  selectedExtrasList?: any;
}

export interface BookingSummaryStayDatesInformationProps {
  arrivalDate: string;
  departureDate: string;
  noNights: number;
}

export interface BookingDataReservationDetailsProps {
  arrivalDate: string;
  departureDate: string;
  currency: CurrencyType;
  noRooms: number;
  noNights: number;
}

export interface BookingSummaryUpgradeToFlexProps {
  showUpgradeToFlex: boolean;
  initialRate: number;
  amount: number;
  currency: string;
  upgradeToFlexCallBack: () => void;
}

export interface BookingSummaryDataProps {
  hotelInformation?: BookingSummaryHotelInformationProps | null;
  totalCost?: BookingSummaryTotalCostProps | null;
  rateInformation?: BookingSummaryRateInformationProps | null;
  stayDatesInformation?: BookingSummaryStayDatesInformationProps | null;
  roomInformation?: BookingSummaryRoomInformationProps[] | null;
  updateToFlex?: BookingSummaryUpgradeToFlexProps | null;
  showAutocompleteMealsNotification?: boolean;
  termsAndConditionsText?: string;
  onclickBillingFormHandler?: () => void;
  onSubmitBtnText?: string;
  paymentStepState?: string;
  cityTaxTotal?: number;
}
