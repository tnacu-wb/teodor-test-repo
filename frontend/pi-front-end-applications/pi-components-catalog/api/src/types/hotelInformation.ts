import { DeRoomClass, RoomClass, StandardRoomType } from '../enums';
import { BookingFlow, GlobalConfig, PromotionsInformation, SoftBundles } from './graphql';

export interface HIAvailabilityRates {
  hotelAvailability: {
    mlos?: boolean;
    limitedAvailability: boolean;
    hotelId: string;
    startDate: string;
    endDate: string;
    available: boolean;
    roomRates: HIRoomRate[];
    promotionsInformation?: PromotionsInformation;
  };
  ratesInformation: HIRatesInformation;
  hotelInventory?: HIHotelInventoryRoomTypeInventories;
}

export interface HIHotelInventoryRoomTypeInventories {
  roomTypeInventories: HIHotelInventoryRoomType[];
}

export interface HIRoomRate {
  ratePlanCode: string;
  roomTypes: HIRoomType[];
  rateCategory?: string;
  cellCode: string | null;
  promotionCode?: string;
  promoKind?: PromoKind;
}

export interface HIRoomType {
  roomType: HIRoomCode;
  adults: number;
  children: number;
  cotRequested: boolean;
  rooms: HIRoom[];
  roomNumber?: number;
  isRoomAccessible?: boolean;
}

export type HIStandardRoomType =
  | StandardRoomType.DB
  | StandardRoomType.FAM
  | StandardRoomType.DIS
  | StandardRoomType.SB
  | StandardRoomType.TWIN;

export type HIRoomCode = 'DB' | 'FAM' | 'DIS' | 'SB' | 'TWIN';

export interface HIRoom {
  pmsRoomType: string;
  silentSubstitution: boolean;
  roomClass: HIRoomClassCode;
  cotAvailable: boolean;
  roomPriceBreakdown: HIRoomPriceBreakdown;
  specialRequests: string[];
  numberOfRoomsAvailable: number;
  roomType?: HIStandardRoomType | null;
  softBundles: SoftBundles;
  isSubstitution?: boolean;
  substitution?: string | null;
}

export interface HIRatePlan {
  ratePlanCode: string;
  roomPriceBreakdown: HIRoomPriceBreakdown;
}

export interface HIRoomPriceBreakdown {
  effectiveRateAmount: number;
  totalCityTaxAmount: number;
  totalNetAmount: number;
  totalRoomNetAmount?: number;
  currencyCode: string;
  dailyPrices: HIDailyPrice[];
  packageCode: string | null;
  packageAmount: number | null;
  priceBreakdownResponseList?: HIPriceBreakdown[];
  baseRateAmount?: number;
}

export interface HIDailyPrice {
  date: string;
  netPrice: number;
  effectiveRate: number;
  roomNetPrice?: number;
}

export interface HIPriceBreakdown {
  summaryDate: string;
  net: number;
}

export interface HIRatesInformation {
  rateClassifications: HIRateClassification[];
}

export interface HIRateClassification {
  rateClassification: string;
  rateDescription: string;
  rateName: string;
  rateOrder: string;
  ratePlanCode: string;
  rateCategory: string;
  isCorporateDiscountAvailable?: boolean;
  rateTags?: string[];
}
export interface PromoTypes {
  promoName?: string;
  promoCode?: string | string[];
  eligibility?: boolean;
  promoJourney?: boolean;
  bannerType?: string;
  validation?: string;
  promoBoxVisible?: boolean;
}
export interface HIBasketBookConfirmation {
  createReservation: {
    basketReference: string;
  };
}

export type HIRoomClass =
  | RoomClass.ST
  | RoomClass.SE
  | RoomClass.PP
  | RoomClass.BG
  | RoomClass.PSE
  | RoomClass.SW
  | RoomClass.BW
  | DeRoomClass.ST
  | DeRoomClass.SE
  | DeRoomClass.PP
  | DeRoomClass.BG
  | DeRoomClass.PSE
  | DeRoomClass.SW
  | DeRoomClass.BW;

export type HIRoomClassCode = 'ST' | 'SE' | 'PP' | 'BG' | 'PSE' | 'SW' | 'BW';

export interface HIAvailableFlag {
  country: string;
  language: string;
  brand: string;
  hotelId: string;
  hotelAvailability: {
    available: boolean;
  };
}

export interface HIInfoItems {
  text: string;
  priority: string;
  startDate: string;
  endDate: string;
}

export interface HIAEMroomType {
  roomCategory: string;
  roomDescription: string;
  roomImage: string;
  roomLabel: string;
  roomTypeCode: string | string[];
  groupId: string;
}

export interface HIHotelAvailabilityResponse {
  isLoadingHotelAvailability: boolean;
  dataHotelAvailability: HIAvailabilityRates;
  isErrorHotelAvailability: boolean;
  errorHotelAvailability: unknown;
}

export interface HIHotelInventory {
  hotelInventory: {
    roomTypeInventories: HIHotelInventoryRoomType[];
  };
}

export interface HIHotelInventoryRoomType {
  code: string;
  availableCount: number;
}

export interface HIHotelInventoryResponse {
  isLoadingHotelInventory: boolean;
  dataHotelInventory: HIHotelInventory;
  isErrorHotelInventory: boolean;
  errorHotelInventory: unknown;
}

// AEM roomTypeInformation
export interface HIAEMroomTypesInfo {
  roomTypeInformation: {
    roomTypes: HIAEMroomType[];
  };
}

export interface HIRoomTypeInfoResponse {
  isLoadingRoomTypeInformation: boolean;
  dataRoomTypeInformation: HIAEMroomTypesInfo;
  isErrorRoomTypeInformation: boolean;
  errorRoomTypeInformation: unknown;
}

export interface HIGlobalConfigResponse {
  isLoadingGlobalConfig: boolean;
  dataGlobalConfig: { globalConfig: GlobalConfig };
  isErrorGlobalConfig: boolean;
  errorGlobalConfig: unknown;
}

export interface HIBasketData {
  hotelId: string;
  arrival: string;
  departure: string;
  numberOfUnits: number;
  numberOfNights: number;
  selectedRate: HIRoomRate;
  roomClass: HIRoomClass;
  rateName: string;
  rateTags?: string[];
  roomTypeInformationResponse: HIRoomTypeInfoResponse;
  bookingFlow: BookingFlow | undefined;
  phoneNumber: string;
  brand: string;
  silentSubstitutionLabels: string[];
  rateDescription?: string;
  isCityTaxEnabled?: boolean;
  softBundles?: SoftBundles;
  isSoftBundlesVisible?: boolean;
  prevReservationId?: string;
}

export interface HIBasicBasketDetails {
  hotelId: string;
  adultsNumber: number;
  childrenNumber: number;
  startDate: string;
  endDate: string;
  nightsNumber: number;
  rateCode: string;
  reservationId: string;
  bookingFlowId: string;
}

export interface HIVisualDisplayContext {
  isLessThanXs: boolean | undefined;
  isLessThanSm: boolean | undefined;
  isLessThanMd: boolean | undefined;
  isLessThanLg: boolean | undefined;
}

export interface HITwinRoomPrice {
  twinRoomType: string;
  currencyCode: string;
  price: number;
}
export interface StaticHotelType {
  brand: string;
  code: string;
  title: string;
}

export interface RoomUpgradeContent {
  priceText: string | undefined;
  primaryButtonText: string | undefined;
  secondaryButtonText: string | undefined;
  price: number;
  description: string;
  heading: string | undefined;
  imageUrl: string;
  roomClass: string;
  pmsRoomTypes: string[];
  selectedRoomClass: string;
}

export enum PromoKind {
  LandingPage = 'LANDING_PAGE',
  SiteWide = 'SITE_WIDE',
  Generic = 'GENERIC',
  Unique = 'UNIQUE',
}

export interface UserChoice {
  roomNumber: number;
  roomType: {
    id: string;
    icon: React.ReactElement;
    label: string;
    code: string;
  };
  pmsRoomType: string;
}

export interface ActiveChoiceType {
  rate: string;
  class: string;
  softBundle?: SoftBundles;
  isRoomOnly?: boolean;
}
