export class PriceFinderLocationSearchCriteria {
  // Mandatory Fields
  locationId: string;
  arrival: string;
  daysRange: number;
  page: number;
  initialPageSize: number;
  lazyLoadPageSize: number;
  country: string;
  language: string;

  // Optional Fields
  showMinimumNights?: boolean;
  sortBy?: SortingOption;
  sortDate?: string;

  /**
   * Room type filter: SB, DB, FAM, TWIN, DIS. Accepts multiple values as an array.
   * If it's just one, pass it as a single string - Added it for convinience
   */
  filterByRoomType?: RoomTypeFilter[];

  constructor(data: any) {
    this.locationId = data.locationId;
    this.arrival = data.arrival;
    this.daysRange = data.daysRange;
    this.page = data.page;
    this.initialPageSize = data.initialPageSize;
    this.lazyLoadPageSize = data.lazyLoadPageSize;
    this.country = data.country;
    this.language = data.language;
    this.showMinimumNights = data.showMinimumNights;
    this.sortBy = data.sortBy || SortingOption.DISTANCE;
    this.sortDate = data.sortDate;
    this.filterByRoomType = Array.isArray(data.filterByRoomType)
      ? data.filterByRoomType
      : data.filterByRoomType
        ? [data.filterByRoomType]
        : undefined;
  }
}

// Enum for optional filtering by room type in PriceFinderLocationSearchCriteria
export enum RoomTypeFilter {
  SB = 'SB', // Single room types
  DB = 'DB', // Double room
  FAM = 'FAM', // Family room
  TWIN = 'TWIN', // Twin room
  DIS = 'DIS' // Accessible room
}

export enum SortingOption {
  DISTANCE,
  PRICE
}
