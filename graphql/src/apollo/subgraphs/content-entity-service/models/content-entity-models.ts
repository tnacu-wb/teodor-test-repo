export interface HotelInformationResponse {
  brand: string;
  rateClassifications: { ratePlanCode: string; rateCategory: string }[];
  bookingFlow: {
    bookingFlowItems: { rateCategory: string; bookingIdBB: string; bookingId: string }[];
  };
}

export interface RateInformationResponse {
  rateClassifications: { ratePlanCode: string; rateCategory: string }[];
}

export interface CategoryLabelsRequest {
  language: string;
  country: string;
  category: string;
  labels: string[];
}

export interface HotelInformationRequest {
  language: string; // Required
  hotelId: string; // Required
  country: string; // Required
  bookingChannel: BookingChannelCriteria;
  stayStartDate?: string; // Optional
  stayEndDate?: string; // Optional
}

export interface BookingChannelCriteria {
  channel: Channel;
  subchannel: string;
  language?: string; // Optional
}

export enum Channel {
  PI,
  BB,
  CCUI,
  DISTR,
  EMPLOYEE
}
