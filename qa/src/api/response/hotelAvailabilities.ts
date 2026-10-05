import { HotelAvailabilityCard } from './hotelAvailabilityCard';

/**
 * One single page of hotels available for a specific location from API response
 * this call is made when the user is searching available hotels for a specific location. The response of this call is displayed in Search Results page.
 */
export class HotelAvailabilities {
  [key: string]: unknown;
  multiHotelAvailabilities: HotelAvailabilityCard[] = [];
  page?: number;
  pageSize?: number;
  total?: number;

  /**
   * HotelAvailabilities constructor
   * @param data object data
   * @param data.hotelAvailabilitiesApiResponse response from API
   */
  constructor(
    data: {
      hotelAvailabilitiesApiResponse?: {
        multiHotelAvailabilities?: Array<Record<string, unknown>>;
        page?: number;
        pageSize?: number;
        total?: number;
      };
    } = {},
  ) {
    const hotelAvailabilitiesApiResponse = data.hotelAvailabilitiesApiResponse ?? {};
    this.page = hotelAvailabilitiesApiResponse.page;
    this.pageSize = hotelAvailabilitiesApiResponse.pageSize;
    this.total = hotelAvailabilitiesApiResponse.total;
    const multiHotelAvailabilities = hotelAvailabilitiesApiResponse.multiHotelAvailabilities ?? [];
    this.multiHotelAvailabilities = multiHotelAvailabilities.map(
      (hotelCardDetails) => new HotelAvailabilityCard({ hotelCardApiResponse: hotelCardDetails }),
    );
  }

  static fromResponse(data: {
    hotelAvailabilitiesApiResponse?: {
      multiHotelAvailabilities?: Array<Record<string, unknown>>;
      page?: number;
      pageSize?: number;
      total?: number;
    };
  }): HotelAvailabilities {
    return new HotelAvailabilities(data);
  }

}
