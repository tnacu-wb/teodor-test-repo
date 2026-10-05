import {
  SingleHotelAvailability,
  Room,
  Place,
  SearchInformation,
  PromotionsInformation,
} from './graphql';

export type SRResponseType = {
  hotelAvailabilities: {
    multiHotelAvailabilities: SingleHotelAvailability[];
    page: number;
    pageSize: number;
    total: number;
    promotionsInformation?: PromotionsInformation;
  };
};

export type OperaPmsSource = 'OPERA';

export type SRParamsForQuery = {
  startDate: string | null;
  endDate: string | null;
  rooms: Room[];
  place: Place;
  oldWorldChannel: string;
  channel: string;
  subChannel: string;
  companyId?: string;
  page: number;
  initialPageSize: number;
  lazyLoadPageSize: number;
  country: string;
  language: string | undefined;
  sort: string;
  filters?: string;
  ratePlanCodes?: string[] | [];
  sortOption?: SortOption;
};

export type SRMultiSearchParamsType = {
  arrivalDay: number | null;
  arrivalMonth: number | null;
  arrivalYear: number | null;
  location: string;
  numberOfNights: number | null;
  rooms: Room[];
  placeId?: string;
  coordinates?: string;
  bookingChannel: string;
  sort: string;
  code?: string;
  filters?: string;
  cellCodes?: string[] | [];
  corpId?: string;
  compId?: string | undefined;
  promoId?: string | string[];
};

export type SRPartialTranslationsType = {
  searchInformation: SearchInformation;
};

export type SRSortType = {
  [key: string | number]: string;
};

export type ImageThumbnailData = {
  imageSrc: string;
  imageAlt: string;
};

export type SRHotelFilters = {
  name?: string;
  code?: string;
};

type CCUI = 'ccui';
type PI = 'pi';
type BB = 'bb';

export type SRVariantType = CCUI | PI | BB;

export type SRFiltersType = string[] | [];
export type GetParamsForQueryFunctionParamsType = {
  startDate: Date | null;
  endDate: Date | null;
  multiSearchParams: SRMultiSearchParamsType;
  place: Place;
  partialTranslations: SRPartialTranslationsType;
  currentPage: number;
  country: string;
  language: string;
  filters?: string;
  cellCodes?: string[] | [];
  isIBEnabled?: boolean;
  sortOption?: SortOption;
};
export type SRErrorResponseHotelAvailabilities = {
  response: {
    data: {
      hotelAvailabilities: null;
    };
    errors: {
      message: string;
      errorType: string;
    }[];
  };
};

export type SRErrorResponseHotelAvailabilitiesV2 = {
  response: {
    data: {
      hotelAvailabilitiesV2: null;
    };
    errors: {
      message: string;
      errorType: string;
    }[];
  };
};

export type SortOption = {
  rcPriceModifier: number;
  rcDistanceModifier: number;
  rcHubModifier?: number;
};
