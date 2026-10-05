export enum BusinessQuestionType {
  PurchaseOrder = 'purchase_order_number',
  CustomerReference = 'customer_reference',
}

export enum BusinessQuestionDefaultHeader {
  PurchaseOrder = 'Purchase order number',
  CustomerReference = 'Customer reference',
}
export enum BusinessQuestionDefaultQuestionId {
  PurchaseOrder = '2',
  CustomerReference = '1',
}
export enum BusinessQuestionWhenTo {
  Register = 'Register',
  Booking = 'Booking',
}
export enum TypeOfDeleteModal {
  Card = 'Card',
  Question = 'Question',
}
export enum CompanyBookingAllowances {
  PIB = 'premierInnBreakfast',
  CB = 'continentalBreakfast',
  FCB = 'freeChildBreakfast',
  MD = 'mealDeal',
  HB = 'hubBreakfast',
  UW = 'ultimateWifi',
}
export enum PayApplicationStatus {
  Started = 'Incomplete Application',
  Submitted = 'Outstanding',
  Approved = 'Accepted',
  Rejected = 'Rejected',
  Cancelled = 'Cancelled',
}
export enum PayAccountStatus {
  Active = 'current',
  Suspended = 'stop',
  SuspendedHold = 'hold',
  Closed = 'closed',
}
export enum BusinessType {
  Charity = 'Charity',
  Non_Profit_Charity = 'Gemeinnützige GmbH (gGmbH)',
  Non_Profit_Assoc = 'Gemeinnütziger Verein',
  Registered_Non_Profit_Charity = 'Eingetragener gemeinnütziger Verein',
  GovernmentFundedSchool = 'Gov. funded School/College',
  Government = 'Government',
  Government_DE = 'Regierungsorganisation',
  LimitedCompany = 'Limited Company or LLP',
  LimitedCompany_DE = 'Gesellschaft mit beschränkter Haftung',
  LimitedCompany_Partnership_Shares = 'Kommanditgesellschaft auf Aktien',
  LimitedCompany_Partnership = 'Kommanditgesellschaft',
  Partnership = 'Partnership',
  Partnership_DE = 'Partnerschaftsgesellschaft',
  Partnership_General = 'Offene Handelsgesellschaft',
  PublicLimited = 'Public Limited Company',
  PublicLimited_DE = 'Aktiengesellschaft',
  SoleTrader = 'Sole Trader',
  SoleTrader_Registered_Merchant = 'Eingetragener Kaufmann',
  SoleTrader_Merchant = 'Kaufmann',
  SoleTrader_Proprietorship = 'Einzelunternehmen',
  Other_Entrepreneurial_Society = 'Unternehmergesellschaft',
  Other_Association_Incl_Charity = 'Eingetragener Verein',
  Other = 'Other',
  Other_DE = 'Andere',
}

export enum HotelPolicy {
  PI = 'Only Premier Inn',
  PI_AND_OTHERS = 'Premier Inn and other hotel brands',
  NOT_SET = 'No set policy',
}
export enum WLCountryCode {
  GB = 'GBR',
  DE = 'DEU',
}

export enum CARD_STATUS_WL_TYPE {
  PENDING = 'PENDING',
  CURRENT = 'CURRENT',
  HOT = 'HOT',
  CANCELLED = 'CANCELLED',
}

export enum CARD_STATUS_TYPE {
  DISPATCHING = 'dispatching',
  ACTIVATE = 'activate',
  ACTIVE = 'active',
  NOT_ACTIVATED = 'notActivated',
  CANCELLED = 'cancelled',
  EXPIRED = 'expired',
}
