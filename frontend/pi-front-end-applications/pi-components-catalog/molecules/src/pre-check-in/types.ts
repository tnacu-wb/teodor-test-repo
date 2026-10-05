import { BookingConfirmation } from '@whitbread-eos/api';

export interface Reservation {
  basketReference: string;
  hotelId: string;
  reasonForStay: string;
  sendEmailConfirmation: boolean;
  sendEmailInvoice: boolean;
  preCheckIn: boolean;
  title: string;
  acceptFutureMailing: boolean;
  emailAddress: string;
  firstName?: string;
  lastName?: string;
  stayingGuests: StayingGuest[];
  companyName?: string;
  addressLine1: string;
  addressLine2: string;
  addressLine3: string;
  addressLine4?: string;
  addressType?: string;
  cityName?: string;
  countryCode?: string;
  postalCode: string;
  mobile: string;
  landline?: string;
  language: string;
}

interface StayingGuest {
  sameAsBooker: boolean;
  stayingGuestDetails: StayingGuestDetails;
  reservationId: string;
  isAccompanyingGuest: boolean;
}

interface StayingGuestDetails {
  title: string;
  firstName?: string;
  lastName?: string;
  profileId: string;
}

interface GuestData {
  key: string;
  value: string;
}

export interface BookingData {
  title: string;
  rows: GuestData[];
}

export interface ReviewDataType {
  bookingReference: string;
  hotelId: string;
  hotelName: string;
  firstName: string;
  lastName: string;
  arrivalDate: Date;
  scheduledDate: Date;
  departureDate: Date;
  address: string;
  city: string;
  country: string;
  postalCode: string;
  dateOfBirth: Date;
  nationality: { value: string; label: string };
  passport: string;
  dependent: string;
  dependents: Dependent[];
  noOfRooms: number;
  roomNo: number;
  preCheckInStatus: boolean;
  deRegCardCompleted: boolean;
}

export interface Dependent {
  firstname: string;
  lastname: string;
  dateofbirth: Date;
  nationality: { value: string; label: string };
  passport: string;
}

export interface ReviewModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly data?: Partial<ReviewDataType>;
  bookingConfirmation?: Readonly<BookingConfirmation>;
  handleScaSuccess: (url: string) => Promise<void>;
  hotelAddress: string;
}

export interface Nationality {
  value: string;
  label: string;
  image: string;
  countryName: string;
}

interface Address {
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  postalCode?: string;
}

export interface GetHotelAddressParams {
  address?: Address;
  brand?: string;
}
