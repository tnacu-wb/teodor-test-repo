export class AvailabilityByIdsSearchCriteria {
  hotels: { identifier: string }[];
  arrival: string;
  departure: string;
  negotiatedRates?: {
    rateDisplaySets: string[];
    globalCompanyId: string;
  };
  ratePlanCodes?: string[];
  rooms: {
    adultsNumber: number;
    childrenNumber: number;
    roomType: string;
    cotRequired?: boolean;
    pmsRoomType?: string;
  }[];
  bookingChannel?: {
    channel: string;
    subchannel: string;
    language: string;
  };
  vatNotRequired?: boolean;
  isOta?: boolean;

  constructor(data: any) {
    this.hotels = data.hotels;
    this.arrival = data.arrival;
    this.departure = data.departure;
    this.negotiatedRates = data.negotiatedRates;
    this.ratePlanCodes = data.ratePlanCodes;
    this.rooms = data.rooms;
    this.bookingChannel = data.bookingChannel;
    this.vatNotRequired = data.vatNotRequired;
    this.isOta = data.isOta;
  }
}
