export type BasicAuth = {
  username: string;
  password: string;
};

export type UserAccount = {
  email: string;
  password: string;
  title?: string;
  role?: string;
  status?: string;
  name: string;
};

export type CardDetails = {
  cardNumber: string;
  holderName: string;
  cvv: string;
  expiryMonth: string;
  expiryYear: string;
  cardType: string;
};

export type PaymentOptions = {
  time: PaymentTime;
  card: CardDetails;
};

export type PaymentTime = 'now' | 'arrival';
export type Device = 'desktop' | 'mobile';

export type SearchCriteria = {
  arrivalDate: Date;
  departureDate: Date;
  nights: number;
  location: string;
  rooms: Room[];
  rate?: HotelRates;
};

export type Room = {
  adultsNumber: number;
  childrenNumber: number;
  roomType: string | RoomType;
  cotRequired: boolean;
  roomNumber?: number;
};

export type RoomType = {
  name: string;
  id: string;
};

export type HotelAvailabilityInput = {
  hotelCode: string;
  arrival: string;
  departure: string;
  rooms: Room[];
  bookingChannel: BookingChannel;
};

export type BookingChannel = {
  channel: string;
  subchannel: string;
  language: string;
};

export type Location = {
  name: string;
  suggestion: string;
  id: string;
  countryCode: string;
};

export type HotelRates = {
  name: string;
  ratePlanCode: string;
  classification: string;
};
