import { CurrencyAmountType } from './graphql';
import { AcceptedRoomCodes } from './search';

export type SBForm = {
  bookingReference?: string;
  bookerLastName?: string;
  arrivalDate?: string;
  guestLastName?: string;
  bookerPostcode?: string;
  hotelDetails?: SBHotelDetails;
  hotelId?: string;
  hotelLocation?: string;
  bookerEmail?: string;
  bookerPhone?: string;
  cancellationDate?: string;
  companyName?: string;
  thirdPartyBookingReferenceNumber?: string;
  limit?: number;
  offset?: number;
  extendedSearchCriteria?: string;
};

export type SBHotelDetails = {
  name: string;
  code: string;
};

export type Cell = {
  id: string;
  value: string;
  label?: string;
};

export type TableRow = {
  bookingReference: string;
  cells: Cell[];
  isExpandedByDefault?: boolean;
  hotelId?: string;
  bookerLastName?: string;
  operaConfNumber?: string;
};

export interface ExtendedTableRow extends TableRow {
  AccountId: string;
}

export type TableHeader = {
  id: string;
  text: string;
};

export declare type validateBookingFormParams = {
  t: (id: string) => string;
  enhancedSearch?: boolean;
};

export type RoomInfoBart = {
  roomTypeCode: AcceptedRoomCodes[];
  roomCategory: string;
  roomLabel: string;
  roomDescription: string;
  roomImage: string;
};

export type BartBookingDataPackages = {
  id: string;
  name: string;
  qty: number;
  days: number;
  cost?: CurrencyAmountType;
};

export type BartHotelDetails = {
  hotelInformation: {
    brand: string;
    links: {
      detailsPage: string;
    };
    address: string;
    parkingDescription: string;
    galleryImages: {
      alt: string;
      thumbnailSrc: string;
    }[];
  };
};

export type BookingResults = {
  hasMore: boolean;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  bartId: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  bartName: any;
  offset: number;
  searchData: TableRow[];
  limitExceeded: boolean;
  isLoading: boolean;
  isSuccess: boolean;
  isError: boolean;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  error: any;
};

export type EnhancedSearchBookingResults = {
  searchData: TableRow[];
  hasMore: boolean;
  limitExceeded: boolean;
  pageNumber: number;
  searchResults: number;
  totalResults: number;
  isLoading: boolean;
  isSuccess: boolean;
  isError: boolean;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  error: any;
};

export type ClearHotelFieldsState = {
  clearHotelLocation: boolean;
  clearHotelName: boolean;
  setClearHotelLocation: (value: boolean) => void;
  setClearHotelName: (value: boolean) => void;
};

export interface DpaInfo {
  dpaPassed: boolean;
  dpaOverride: boolean;
}

export interface SearchBookingsSessionStorage {
  bookingReference?: string;
  bookerLastName?: string;
  arrivalDate?: string;
  bookingReferenceRPB?: string;
  guestLastName?: string;
  hotelDetails?: string;
  bookerPostcode?: string;
  hotelLocation?: string;
  bookerEmail?: string;
  bookerPhone?: string;
  cancellationDate?: string;
  companyName?: string;
  thirdPartyBookingReferenceNumber?: string;
  extendedSearchCriteria?: string;
  hotelId?: string;
}

export type IdvStatus = {
  passed: boolean;
  bookingReference: string;
};
