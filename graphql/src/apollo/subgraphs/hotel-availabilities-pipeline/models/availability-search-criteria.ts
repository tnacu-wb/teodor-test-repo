export interface PipelineAvailabilitySearchCriteria {
  place?: Place;
  startDate?: string;
  endDate?: string;
  rooms: Room[];
  country: string;
  language: string;
  oldWorldChannel: string;
  channel: string;
  subChannel: string;
  companyId?: string;
  sort?: string;
  sortOption?: SortOption;
  page?: number;
  initialPageSize?: number;
  lazyLoadPageSize?: number;
  filters?: string[];
  ratePlanCodes?: string[];
}

interface Room {
  type: string;
  adultsNumber: number;
  childrenNumber: number;
}

interface Place {
  location: string;
  locationFormat: string;
  radiusUnit: string;
  radius: number;
}

interface SortOption {
  rcPriceModifier: number;
  rcDistanceModifier: number;
  rcHubModifier: number;
}
