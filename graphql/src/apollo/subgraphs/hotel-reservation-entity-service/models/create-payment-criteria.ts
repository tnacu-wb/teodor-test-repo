export interface CreatePaymentCriteria {
  booking: BookingRequest;
  payment: PaymentRequest;
  charityPackageCode?: string;
  hotelId?: string;
  requestId: string;
  businessAccount?: BusinessAccount;
  specialRequests?: string[];
  bookingNotes?: string[];
  companyQuestionAndAnswerDetails?: CompanyQuestionAndAnswerDetails;
  tmpBasketRef?: string;
  isCiol?: boolean;
  useCache?: boolean;
}

interface PaymentRequest {
  billing: Billing;
  card?: CardRequest;
  environment: string;
  subType: string;
  type: string;
  sca?: Sca;
  businessItems?: BusinessItems;
  pibaCardPresent?: boolean;
  paypalNonce?: string;
  paypalDeviceData?: string;
}

interface BusinessAllowance {
  budget: number;
  allowance: string;
  isAuthorised: boolean;
}

interface BusinessItems {
  purchaseOrderNumber: string;
  customReferenceNumber: string;
  businessAllowances?: BusinessAllowance[];
  businessNotes?: string;
}

interface Sca {
  captureMethod?: string;
  protocolVersion?: string;
  eci?: number;
  xid: string;
  dsTransactionID?: string;
  transStatus?: string;
  cavv?: number;
  authorisedAmount?: number;
}

interface CardRequest {
  cardholderName: string;
  cardType: string;
  expiryMonth: string;
  expiryYear: string;
  token: string;
  cnpRequired?: boolean;
  logoUrl?: string;
  type?: string;
  last4Digits?: string;
}

interface Billing {
  address: AddressRequest;
  cardBillingAddress?: AddressRequest;
  differentBillingAddress?: boolean;
  bookerIsNotGuest?: boolean;
  email: string;
  firstName: string;
  lastName: string;
  telephone?: string;
  title: string;
}

interface AddressRequest {
  addressType?: string;
  companyName?: string;
  country?: string;
  addressLine1: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  cityName?: string;
  postalCode?: string;
}

interface BookingRequest {
  businessSite: BusinessSite;
  channel: string;
  journey: string;
  language: string;
  rooms: RoomType[];
  type: string;
  arrivalDate?: string;
  departureDate?: string;
  leadGuest?: GuestRequest;
}

interface BusinessSite {
  identifier: string;
  name: string;
  type: string;
  location: string;
}

interface RoomType {
  adultsNumber: number;
  rate: string;
  type: string;
}

interface GuestRequest {
  name: string;
  previousBookings?: number;
  registered?: boolean;
  registeredSince?: string;
}

interface BusinessAccount {
  purchaseOrder?: string;
  customerReference?: string;
  cardNotPresentAuth?: string;
  breakfastCodeReq?: number;
  dinnerAllowance?: number;
  alcoholAllowed?: string;
  carParkingAllowed?: string;
  wifiAllowed?: string;
  otherChargesAllowed?: string;
}

interface CompanyQuestionAndAnswerDetails {
  purchaseOrderQuestionAndAnswer?: CompanyQuestionAndAnswer;
  customerReferenceQuestionAndAnswer?: CompanyQuestionAndAnswer;
  userDefinedQuestionAndAnswers?: CompanyQuestionAndAnswer[];
}

interface CompanyQuestionAndAnswer {
  question?: string;
  answer?: string;
  questionHeader?: string;
}
