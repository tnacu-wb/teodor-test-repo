export type SearchAvailabilityResponseType = {
  hotelId: string;
  available: boolean;
  roomRates: {
    ratePlanCode: string;
  }[];
};

export type StayDetailsStateLocalStorageType = {
  data: {
    info: {
      suggestion: {
        location: {
          latitude: string | number;
          longitude: string | number;
        };
        placeId: string;
        hotelId: string;
      };
    };
  };
};

export type SearchRoomType = {
  id?: string;
  adults: number;
  children: number;
  shouldIncludeCot: boolean;
  roomType?: string;
  shouldBeAccessible?: boolean;
};

export type SearchSummaryLabels = {
  [key: string]: string;
};

export type AcceptedRoomCodes =
  | 'PB'
  | 'DB'
  | 'DIS'
  | 'RB'
  | 'FAM'
  | 'SB'
  | 'TWIN'
  | 'WETTWN'
  | 'WETDBL'
  | 'LOWDBL'
  | 'LOWTWN'
  | 'FMQUAD'
  | 'FMTRPL'
  | 'FMTHRE'
  | 'EXTDBL'
  | 'PPLDBL'
  | 'SINGLE'
  | 'DOUBLE'
  | 'TWINRM'
  | 'ZPLDBL';

export type AcceptedRoomTypes = 'single' | 'double' | 'accessible' | 'twin' | 'family';

export type SearchPropertyType = {
  code: string;
  brand: string;
  suggestion: string;
  geometry: {
    type: string;
    coordinates: number[];
  };
};

export type SearchPlaceType = {
  suggestion: string;
  placeId: string;
};

export type SearchManagedPlaceType = {
  placeId: string;
  managedPlaceId?: string;
  suggestion: string;
  geometry: {
    type: string;
    coordinates: number[];
  };
};

export type SearchPropertiesType = SearchPropertyType[];
export type SearchPlacesType = SearchPlaceType[];
export type SearchManagedPlacesType = SearchManagedPlaceType[];

export interface SearchSuggestions {
  managedPlaces: SearchManagedPlacesType;
  places: SearchPlacesType;
  properties: SearchPropertiesType;
}

export type SearchLocationType =
  | SearchPlaceType
  | SearchPropertyType
  | SearchManagedPlaceType
  | undefined;

export type SearchBrandType = 'PI' | 'HUB' | 'ZIP' | 'PID';

export type SearchBookingDateType = Date | null;

export type SearchPartialTranslationsType = {
  content: {
    global: {
      addRoom: string;
      adult: string;
      adults: string;
      adultsLabel: string;
      child: string;
      children: string;
      childrenLabel: string;
      done: string;
      night: string;
      room: string;
      rooms: string;
      today: string;
      tomorrow: string;
      single: string;
      double: string;
      accessible: string;
      accessibleOrBarrierFree: string;
      twin: string;
      family: string;
      offers: PromotionOffer[];
    };
  };
  datePicker: {
    reset: string;
    checkOut: string;
  };
  form: {
    where: string;
    adultsHelperText: string;
    checkout: string;
    childrenHelperText: string;
    cotLimit: string;
    includeCot: string;
    removeRoom: string;
    roomType: string;
  };
  results: {
    notifications: {
      ccuiGroupBookingMessage: string;
      groupBookingHeader: string;
      groupBookingMessage: string;
      emp01groupBookingMessage: string;
      noResults: string;
      groupBookingFormPageMessage: string;
    };
  };
  config: {
    api: {
      bookingChannel: {
        business: string;
        leisure: string;
      };
    };
    roomCodes: {
      [key in AcceptedRoomTypes]: AcceptedRoomCodes;
    };
  };
};

export type SearchAEMTranslationsType = {
  adultsLabel: string;
  childrenLabel: string;
  roomTypeLabel: string;
  locationPlaceholder: string;
  submitButtonLabel: string;
  datepickerCheckinLabel: string;
  datepickerCheckoutLabel: string;
  locationErrorLabel?: string;
  numberOfNightsPlaceholder?: string;
  contractRateInputPlaceholder?: string;
  promotionCategoryPlaceholder?: string;
  datepickerInvalidDates?: string;
  priceFinderLocationPlaceholder?: string;
  accessibleRoom?: string;
};

export interface SearchRoomOccupancy {
  adultsNumber: number;
  childrenNumber: number;
  acceptedRoomTypes: AcceptedRoomCodes[];
}
export interface SearchRoomOccupancyLimitationsType {
  roomOccupancyLimitations: {
    roomOccupancies: SearchRoomOccupancy[];
  };
}

export type SearchRoomOccupancyInputTypes = {
  [key: string]: number | any[] | string;
};

export interface ScreenSize {
  isLessThanXs: boolean | undefined;
  isLessThanSm: boolean | undefined;
  isLessThanMobile: boolean | undefined;
  isLessThanMd: boolean | undefined;
  isLessThanLg: boolean | undefined;
  isLessThanXl: boolean | undefined;
}

export type SearchRequestParamsType = {
  searchTerm: string;
  placeId?: string;
  location?: number[];
  code?: string;
  brand?: string;
  ARRdd: number;
  ARRmm: number;
  ARRyyyy: number;
  nights: number;
  roomsNumber: number;
  rooms?: SearchRoomType[];
  PROMOID?: string;
};

export type SearchQueryURLParamsType = SearchRequestParamsType & SearchRoomOccupancyInputTypes;

export type SearchStayRulesResponseType = {
  maxNightsLimitation: {
    maxNights: number;
  };
  globalConfig: {
    maxRoomsLim: {
      maxRooms: number;
      maxRoomsAmend: number;
    };
  };
  maxArrivalDateLimitation: {
    maxArrivalDate: number;
  };
};

export type SearchRoomCodes = Partial<{
  [key in AcceptedRoomCodes]: string;
}>;

export type ObjKeyAccessType = {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [key: string]: any;
};

export type AdultsChildrenNumberKey = 'adultsNumber' | 'childrenNumber';

export type RoomTypeLabels = {
  [key in AcceptedRoomTypes]: string;
};

export type RoomPickerLabels = {
  roomsWarningTitle: string;
  roomsWarningDescription: string;
  roomsWarningDescriptionCCUI: string;
  addMoreRoomsLabel: string;
  removeRoomButtonLabel: string;
  doneButtonLabel: string;
  adult: string;
  adults: string;
  adultsLabel: string;
  adultsMaxPerRoomLabel: string;
  child: string;
  children: string;
  childrenLabel: string;
  childrenAgeLabel: string;
  cotLimit: string;
  cotLabel: string;
  room: string;
  rooms: string;
  roomLabel: string;
  roomTypeLabel: string;
  single: string;
  double: string;
  accessible: string;
  twin: string;
  family: string;
  accessibleRoom: string;
};

export type RoomPickerPlaceholderType = { adults: number; children: number; rooms: number };

export interface RoomPickerStyles {
  boxWrapperStyles: string;
  roomPickerInputElementStyles: string;
}

export type MappedRoomLabelsType = {
  [key: string]: string;
};

export interface SingleHotelSearchLabelsType {
  bookingChannel: string;
  mappedRoomLabels: MappedRoomLabelsType[];
}

export interface SearchHotelAvailability {
  ARRyyyy: number | null;
  ARRmm: number | null;
  ARRdd: number | null;
  nights: number | null;
  rooms?: SearchRoomType[];
  code?: string;
}

export interface SearchCcuiReducer {
  items?: SearchSuggestions | null;
  searchConsoleIsActive?: boolean;
  errorCode?: [string, string] | [undefined, undefined];
  searchSummaryActive?: boolean;
  startDate?: SearchBookingDateType;
  endDate?: SearchBookingDateType;
  savedNights?: number;
  locationInputError?: boolean;
}
export interface PromotionOffer {
  cellCode: string;
  maxRooms: number;
  numberOfNights: number;
  page: string;
  corpId: string;
  ratePlanCode: string;
}
