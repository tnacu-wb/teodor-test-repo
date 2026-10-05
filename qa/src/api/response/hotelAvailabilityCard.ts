import { HotelCardInformation } from './hotelCardInformation';

/**
 * One hotel card object from hotelAvailabilities call
 * this call is made when the user search available hotels for a specific location. The response of this call is displayed in Search Results page.
 */
export class HotelAvailabilityCard {
  [key: string]: unknown;
  hotelAvailability?: boolean;
  hotelId?: string;
  hotelInformation?: HotelCardInformation;
  name?: string;

  /**
   * HotelAvailabilityCard constructor
   * @param data object data
   * @param data.hotelCardApiResponse response from API
   */
  constructor(data: { hotelCardApiResponse?: Record<string, unknown> } = {}) {
    const hotelCardApiResponse = data.hotelCardApiResponse ?? {};
    this.hotelAvailability = hotelCardApiResponse.hotelAvailability as boolean | undefined;
    this.hotelId = hotelCardApiResponse.hotelId as string | undefined;
    this.name = hotelCardApiResponse.name as string | undefined;
    this.hotelInformation = new HotelCardInformation({
      hotelCardInformationApiResponse: hotelCardApiResponse.hotelInformation as Record<string, unknown>,
      hotelName: hotelCardApiResponse.name as string,
    });
  }

  static fromResponse(data: { hotelCardApiResponse?: Record<string, unknown> }): HotelAvailabilityCard {
    return new HotelAvailabilityCard(data);
  }
}
