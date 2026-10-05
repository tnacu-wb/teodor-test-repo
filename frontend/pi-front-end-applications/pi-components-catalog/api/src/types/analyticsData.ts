import { DlpItem, HotelInformationOptional, ThingsToDo, Promo, SoftBundle } from './graphql';
import { PromoTypes } from './hotelInformation';

export interface AnalyticsData {
  browserTimeZone?: string;
  currencyCode?: string;
  cellCode?: string;
  lettingTypes?: string;
  environment?: string;
  language?: string;
  pageName?: string;
  pageType?: string;
  pageURL?: string;
  siteType?: string;
  funnel_step?: string;
  userID?: string;
  userLoggedIn?: string;
  userFN?: string;
  userLN?: string;
  userCountry?: string;
  userTelephone?: string;
  userMobile?: string;
  CCUI?: boolean;
  userLevel?: string;
  currentTime?: string;
  breakpoint?: string;
  analyticsDataSearchResult?: AnalyticsDataSearchResult;
  FromToDate?: FromToDate;
  rateCode?: string;
  productSelectedRate?: string;
  RoomTypes?: string;
  RoomNames?: string;
  productDetails?: Array<ProductDetailsInterface>;
  upsells?: UpsellsInterface;
  validation?: string;
  bookingReasonForStay?: string;
  wifiAccessAllowed?: boolean;
  wifiOption?: string;
  bookingStepsTotal?: number;
  bookingCurrentStep?: number;
  alcoholAllowed?: boolean;
  carParkingAllowed?: boolean;
  cardNotPresent?: boolean;
  dinnerAllowance?: boolean;
  otherChargesAllowed?: boolean;
  piba?: string;
  paymentCardSelected?: string;
  cardType?: string;
  paymentCards?: string;
  paymentCardTypes?: string;
  paymentTakenNow?: string;
  paymentSessionID?: string;
  paymentTemplateID?: string;
  paymentLoadTime?: string;
  hasPaymentFailure?: boolean;
  echoID?: string;
  contentComponentOrder?: string;
  loginFormComponentsOrder?: string;
  bookingPanelComponentsOrder?: string;
  dashboard?: DashboardAnalytics;
  change?: string;
  revenue?: number;
  revenueChange?: number;
  extrasRevenueChange?: number;
  foodRevenueChange?: number;
  nightsChange?: number;
  roomTypeChange?: boolean;
  roomsChange?: number;
  totalRevenueChange?: number;
  sessionId?: string;
  companyID?: string;
  dailyRates?: Array<RatesPerDate[]>;
  donationAmount?: number;
  paypal?: boolean;
  gp?: boolean;
  ap?: boolean;
  allowances?: string[];
  guestDetails?: AnalyticsGuestDetails;
  gbf?: AnalyticsGroupBooking;
  userHashedEA?: string;
  signupID?: string;
  marketingOptIn?: boolean;
  marketingOptOut?: boolean;
  marketingOptInChoice?: boolean;
  optInCustomer?: boolean;
  paymentOutage?: boolean;
  dlp?: DlpAnalytics;
  paymentDecline?: boolean;
  declineReasonCode?: string;
  paypalDeclined?: boolean;
  paypalDeclineReason?: string;
  mapReference?: google.maps.Map;
  mapMarkerReferences?: google.maps.Marker[];
  innBusiness?: AnalyticsDataInnBusiness;
  innBusinessPay?: AnalyticsDataInnBusinessPay;
  manageEmployees?: AnalyticsDataManageEmployees;
  liveChat?: AnalyticsDataLiveChat;
  upsellPopup?: PremiumPopUpRoomUpgrade;
  loginClicked?: boolean;
  logInSuccessful?: boolean;
  logInUnsuccessful?: boolean;
  authJourneyType?: string;
  authStep?: string;
  authRedirectPageType?: string;
  authAttemptCount?: number;
  authEntryPoint?: string;
  authErrorType?: string;
  authErrorMessage?: string;
  searchResults?: {
    priceFinder?: PriceFinder;
  };
  promoBookingComplete?: boolean;
  discountTags?: string;
  promo?: PromoTypes;
  secureBookingAction?: boolean | false;
  secureBookingComplete?: boolean | false;
  restaurants?: RestaurantsAnalyticsData;
  bundleSelected?: boolean;
  bundleInfoSelected?: boolean;
  bundleInfo?: SoftBundle[];
  upgradeBundleSelected?: boolean;
  upgradeBundle?: string;
  bundleRevenue?: string;
  restaurantBanner?: boolean;
  selectedSearch?: SearchLocation | SearchHotel;
  businessAccountType?: string;
  roomsOffered?: RoomOffer[];
}

export type AnalyticsDataManageEmployees = {
  innBusiness?: boolean;
  innBusinessPay?: boolean;
};

export type AnalyticsDataInnBusiness = {
  stays?: number;
  bookings?: number;
  applications?: number;
  validation?: string;
  activeUsers?: number;
  resendActivation?: number;
  deactivated?: number;
  addEmployee?: boolean;
  employeeRole?: string;
  employee?: 'individual' | 'bulk upload';
  userRole?: {
    travelManagers?: number;
    booker?: number;
    selfBooker?: number;
    guest?: number;
  };
  activeSearchReturned?: boolean;
  manageEmployees?: number;
  manageCards?: number;
  numOfStays?: number;
  companySector?: string;
  bookingFrequency?: string;
  numOfEmployees?: string;
  profileCompletion?: boolean;
  checkBox?: boolean;
  tab?: string;
  activeCards?: number;
  expiredCards?: number;
  dispatchedCards?: number;
  cancelledCards?: number;
  showCancelledCards?: boolean;
  activateCards?: number;
  visitedCardsPages?: Set<number>;
  cardId?: string;
  cardType?: string;
  monthlyAccountSpend?: string;
  companyHotelPolicy?: string;
  applicationReference?: string;
  timeTrading?: string;
  businessType?: string;
  editEmployee?: boolean;
  deleteEmployee?: boolean;
  invoices?: number;
  transactions?: number;
  selectedSetCreditLimit?: boolean;
  selectedRestrictedUsage?: boolean;
  cardReplaceReason?: string;
  cardReplaceAddress?: CardReplaceAddress;
};

export type AnalyticsDataInnBusinessPay = {
  accounts?: number;
};

export type AnalyticsDataLiveChat = {
  available?: boolean;
  opened?: boolean;
  endChat?: boolean;
  chatTranscript?: boolean;
  minimiseChat?: boolean;
};

export interface DashboardAnalytics {
  cancelledBookings?: number; // status CANCELLED
  checkedinBookings?: number; // ?
  futureBookings?: number; // status UPCOMING
  stayedBookings?: number; // ?
  totalBookings?: number;
  totalOperaBookings?: number; // sourcePms OPERA
  totalBartBookings?: number; // sourcePms BART
  moreThanNineNights?: number; // how many have more than 9 nights stay (based on arrival and departure dates)
  moreThanFourRooms?: number; // how many have more than 4 rooms (stayingGuests.length - there is one main guest is in each room)
  // string to be populated with fields that have been filled for this search
  // extended serch is hidden, so currently only these might be in the final string
  // "booking ref, booking surname, arrival date"
  searchBookingResults?: string; // "third party booking ref, booking ref, booking surname, guest surname, arrival date, postcode, email, tel num, cancellation date, company name"
  bookingsReturned?: number; // equal to totalBookings maybe
  // Data for the following is only available when expanding a reservation, not available in the search result
  // These won't be filled in
  bookingCategoryGroup?: number; // nbr of bookings made for a group
  bookingCategoryThirdParty?: number; // nbr of bookings from third party
  // Data for the Cancel Booking dashboard
  cancelBookingID?: string;
  cancelNights?: number;
  cancelRooms?: number;
  userAgentEmail?: string;
}

export interface SRHotelTypeAnalytics {
  bookingSystem: string;
  hotelAvailability: string; // Specific hotel is available
  hotelCode: string;
  hotelDistance: number;
  hotelFacilityIcons: string[];
  hotelLabel: string[];
  hotelRates?: HotelRates[];
  hotelResultPosition?: number;
  imageLabel?: string;
  priceFrom?: string;
  voucherCode?: string;
}

export interface AnalyticsDataSearchResult {
  newSearch?: boolean;
  addedResults?: number;
  searchCheckInDate: string | null;
  searchCheckOutDate: string | null;
  searchDaysToCheckIn: number | null; // number of full days before arrival date example 0 for today 1 for tomorrow
  searchFilter?: string; // Here filters have been applied to the search
  searchNumberOfAdults: number;
  searchNumberOfChildren: number;
  searchNumberOfGuests: number;
  searchNumberOfNights: number | null;
  searchNumberOfRooms: number;
  searchResults: number;
  searchResultsDisplayed?: SRHotelTypeAnalytics[] | 0;
  searchRoomType: string; // how would it look if you booked two or three rooms - how would this look? (Delimiters)
  searchSort?: string; //Search has been sorted by price, could also be set to distance considering search sort
  searchTerm: string;
  searchType: string;
  searchWeekdayFrom: string | null;
  searchWeekdayFromTo: string | null;
  searchWeekdayTo: string | null;
  mapLoaded?: boolean;
}

export interface HotelRates {
  cellCode: string;
  currencyCode: string;
  description: string;
  lettingType: string;
  price: string;
  rateCode: string;
  text: string;
}

export interface FromToDate {
  ArrivalDay: string;
  DepartureDay: string;
  FromToDay: string;
}
export interface ProductInfoInterface {
  sku?: string;
  totalNumberOfRooms?: string;
  roomAdults?: string;
  roomChildren?: string;
  numberOfGuests?: string;
  startDate?: string;
  endDate?: string;
  numberOfNights?: string;
  daysToCheckIn?: string;
  FoodPerRoom?: Array<FoodPerRoom>;
  dailyRates?: Array<DailyRate[]>;
}

export interface ProductDetailsInterface {
  type: string;
  quantity: number;
  price: {
    basePrice: string;
  };
  productInfo: ProductInfoInterface;
  AvailabilityFoodRatePlan?: Array<FoodRatePlan>;
  rawRatePlan?: RawRatePlan;
}

export interface FoodRatePlan {
  code: string;
  legend: string;
  foodUpsell: boolean;
  price: {
    amount: string;
    currency?: string;
  };
  totalCostPerGuest: {
    amount: number;
    currency?: string;
  };
  freeBreakfastCode: string;
  freeBreakfastOption: boolean;
}
export interface FoodPerRoom {
  codes: Array<FoodPerRoomCodes>;
  roomNumber: string;
}
export interface FoodPerRoomCodes {
  adults?: number;
  children?: number;
  code: string;
  price: {
    amount: number;
    currency?: string;
  };
}
export interface RatesPerDate {
  startDate: string;
  pricePerNight: number;
  cityTaxPerNight: number;
}
export interface DailyRate {
  date: {
    shortDay: string;
    day: string;
    date: number;
    month: string;
    shortMonth: string;
    year: number;
  };
  backupDate: string;
  price: {
    amount: string;
    currency?: string;
  };
  cityTax: number;
}
export interface RawRatePlan {
  upsellItems: Array<FoodRatePlan>;
  totalCost: {
    amount: string;
    currency?: string;
  };
  totalFoodCost: {
    amount: number;
    currency?: string;
  };
}

export interface UpsellsInterface {
  rooms: Array<UpsellsSelection[]>;
}
export interface UpsellsSelection {
  code: string;
  legend: string;
  quantity: number;
  price: string;
  currency?: string;
  freeBreakfastCode?: string;
  freeBreakfastOption?: boolean;
}
export interface AnalyticsPrice {
  cartTotal: {
    amount: string;
    currency: string;
  };
  currency: string;
  voucherCode?: string;
  voucherDiscount?: string;
}
export interface AnalyticsDataCartConfirmation {
  BusinessAccountCardExtrasUpsells?: object;
  CartItems?: Array<ProductDetailsInterface>;
  InvoiceType?: string;
  bookingCity?: string;
  bookingId?: string | string[] | null;
  bookingPanelComponentsOrder?: string;
  bookingZipCode?: string;
  contentComponentOrder?: string;
  businessAccQuestions?: string;
  cardType?: string;
  paymentSessionID?: string;
  paymentTakenNow?: string;
  paymentTemplateID?: string;
  price?: AnalyticsPrice;
  productSelectedRate?: string;
  typeOfTrip?: string;
  userDefinedQuestions?: string;
  userisSubscribed?: boolean;
  validation?: string;
  paymentOutage?: boolean;
}

export interface SearchedResultsAnalyticsLabelsConstants {
  AVAILABLE_HOTEL: string;
  UNAVAILABLE_HOTEL: string;
  SOLD_OUT_CONSTANT: string;
  LAST_FEW_ROOMS_CONSTANT: string;
  OPEN_SOON_CONSTANT: string;
  NO_LABEL_CONSTANT: string;
  PREMIER_PLUS_LABEL: string;
  PREMIER_PLUS_FACILITY_CODE: string;
  MLOS_LABEL: string;
}

export interface AnalyticsPageDetails {
  pathMatch: string;
  pageName: string;
  pageType: string;
  page: string;
}

export interface AnalyticsDataAmend {
  change: string;
  revenue: number;
  extrasRevenueChange: number;
  foodRevenueChange: number;
  nightsChange: number;
  roomTypeChange: boolean;
  roomsChange: number;
  totalRevenueChange: number;
  validation: string;
}

export interface AnalyticsRoomDetails {
  isChanged?: boolean;
  roomType?: string;
  changeAdults?: string;
}
export interface AnalyticsGuestDetails {
  addCheckinInfo: boolean;
  guestConsent: boolean;
  addressLine1: boolean;
  addressLine2: boolean;
  addressLine3: boolean;
  postalCode: boolean;
  location: boolean;
  countrySelection: boolean;
  email: boolean;
  birthDate: boolean;
  nationality: boolean;
  passportNum: boolean;
}

export interface AnalyticsGroupBooking {
  booker?: string;
  typeOfStay?: string;
  schoolGroup?: boolean;
  reasonForVisit?: string;
  hotelSelected?: string;
  checkInDate?: string;
  checkOutDate?: string;
  packageType?: string;
  childrenStaying?: boolean;
  accessibleRoomRequired?: boolean;
  totalRooms?: string;
  commentsSubmitted?: boolean;
  singleOccupancy?: string;
  doubleOccupancy?: string;
  twinOccupancy?: string;
  familyOf21A1C?: string;
  familyOf32A1C?: string;
  familyOf31A2C?: string;
  familyOf42A2C?: string;
  accessibleSingle?: string;
  accessibleDouble?: string;
  accessibleTwin?: string;
  caseID?: string;
  validation?: string;
}

export interface DlpDestinationAnalytics {
  destinationLocationName: string;
  destinationPosition: number;
}

export interface DlpHotelsAnalytics {
  hotelName: string;
  orderPosition: number;
  hotelCode: string;
  tripadvisorRating?: number;
  tripadvisorReviewsCount?: number;
  hotelLabels: string[];
}

export interface DlpAnalytics {
  locationName: string;
  hotelDisplayedCount: number;
  filterType: string;
  destinations: DlpDestinationAnalytics[];
  hotels: DlpHotelsAnalytics[];
  thingsToDo?: DlpAnalyticsSectionObject[];
  travelGuides?: DlpAnalyticsSectionObject[];
  hotelDistanceFromSearch?: number;
  mapReference?: boolean;
  filterSectionOpened?: boolean;
  filtersCleared?: boolean;
  searchFilter?: string;
}

export interface DlpAnalyticsRequest {
  locationName?: string;
  hotelDisplayedCount?: number;
  filterType?: string;
  destinations?: DlpItem[];
  hotels: HotelInformationOptional[];
  thingsToDo?: ThingsToDo;
  mapReference: boolean;
  premierPlusLabel: string;
  promos?: Promo[];
  funnel_step?: string;
}

export interface DlpAnalyticsSectionObject {
  tileName?: string;
  tilePosition?: number;
}
export interface PremiumPopUpRoomUpgrade {
  upgradeTotalPrice?: string;
  upgradeIncrementalPrice?: string;
  currentRoom?: string;
  upgradeRoom?: string;
}

export interface PriceFinder {
  searchLocation?: string;
  defaultLocation?: boolean;
  defaultDate?: boolean;
  monthSelected?: string;
  weekSelected?: string[];
  numberOfResults?: number;
  pageNumber?: number;
  numberOfSoldOutResults?: number;
  sortBy?: string;
}

export interface RestaurantsAnalyticsData {
  brandID?: number;
  brandName?: string;
  currencyCode?: string;
  language?: string;
  pageName?: string;
  pageType?: string;
  pageURL?: string;
  restaurantID?: string | number;
  userID?: string;
  userLoggedIn?: string;
  allowMarketing?: boolean;
  amcv?: string;
  arrivalTime?: string;
  bookingFormState?: string;
  bookingReference?: string;
  date?: string;
  guests?: number;
  adults?: number;
  children?: number;
  isUserDataConfirmTerms?: boolean;
  isUserDataClickedOnSubmission?: boolean;
  isUserDataConfirmInput?: boolean;
  isUserDataEmail?: boolean;
  isUserDataForeName?: boolean;
  isUserDataSurName?: boolean;
  isUserDataTel?: boolean;
  isUserDataTitle?: boolean;
  menuType?: string;
  requestsComments?: string | boolean;
  additionalRequirements?: boolean;
  unavailableTimes?: string;
  sessionType?: string;
  highChairs?: number;
  wheelChairAccess?: boolean;
  largeGroupsEnquiry?: boolean;
  sessionUnavailability?: string;
}

export interface CardReplaceAddress {
  title?: string;
  forename?: string;
  surname?: string;
  addressLine1: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  addressLine5?: string;
  postCode?: string;
  country?: string;
}

export interface SearchLocation {
  suggestion?: string;
  placeId?: string;
}

export interface SearchHotel {
  code?: string;
  brand?: string;
  suggestion?: string;
  geometry?: Geometry;
}

export interface Geometry {
  type?: string;
  coordinates?: number[];
}

export interface RoomOfferRate {
  rateName: string;
  rateCode: string;
  price: number;
  currency: string;
}

export interface RoomOffer {
  roomClass: string;
  roomType: string;
  roomsLeft: number | null;
  position: number;
  rates: RoomOfferRate[];
  isSubstitution: boolean;
  substitutionCode: string | null;
}
