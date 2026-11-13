import { HotelFacility } from './hotelFacility';

/**
 * One hotel card information object from hotelAvailabilities call
 */
export class HotelCardInformation {
  [key: string]: unknown;
  brand?: string;
  coordinates?: unknown;
  hotelFacilities: HotelFacility[] = [];
  hotelOpeningDate?: string;
  links?: unknown;
  messagingFlag?: unknown;
  name?: string;
  thumbnailImages?: unknown;

  /**
   * HotelCardInformation constructor
   * @param data object data
   * @param data.hotelCardInformationApiResponse response from API
   * @param data.hotelName hotel name from hotel card response
   */
  constructor(data: { hotelCardInformationApiResponse?: Record<string, unknown>; hotelName?: string } = {}) {
    const hotelCardInformationApiResponse = data.hotelCardInformationApiResponse ?? {};
    this.brand = hotelCardInformationApiResponse.brand as string | undefined;
    this.coordinates = hotelCardInformationApiResponse.coordinates;
    this.hotelOpeningDate = hotelCardInformationApiResponse.hotelOpeningDate as string | undefined;
    this.links = hotelCardInformationApiResponse.links;
    this.messagingFlag = hotelCardInformationApiResponse.messagingFlag;
    this.name = (hotelCardInformationApiResponse.name as string | undefined) || data.hotelName;
    this.thumbnailImages = hotelCardInformationApiResponse.thumbnailImages;

    const hotelFacilities = Array.isArray(hotelCardInformationApiResponse.hotelFacilities)
      ? (hotelCardInformationApiResponse.hotelFacilities as Array<Record<string, unknown>>)
      : [];
    this.hotelFacilities = hotelFacilities.map((hotelFacility) => new HotelFacility({ hotelFacility }));
  }

  static fromResponse(data: { hotelCardInformationApiResponse?: Record<string, unknown>; hotelName?: string }): HotelCardInformation {
    return new HotelCardInformation(data);
  }

}
