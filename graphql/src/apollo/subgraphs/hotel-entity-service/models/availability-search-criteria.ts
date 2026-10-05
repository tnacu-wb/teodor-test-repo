export class AvailabilitySearchCriteria {
  hotel: { identifier: string };
  arrival: string;
  departure: string;
  ratePlanCodes?: string[];
  companyId?: string;
  rooms: {
    adultsNumber: number;
    childrenNumber: number;
    roomType: string;
    cotRequired?: boolean;
  }[];
  bookingChannel?: {
    channel: string;
    subchannel: string;
    language: string;
  };
  country?: string;
  softBundle?: string;
  promotionCode?: string;
  originalBasketReference?: string;
  promoKind?: string;
  isPromoBox?: boolean;
  brand?: string;
  rateName?: string;
  roomClass?: string;

  constructor(data: any) {
    this.hotel = data.hotel;
    this.arrival = data.arrival;
    this.departure = data.departure;
    this.ratePlanCodes = data.ratePlanCodes;
    this.companyId = data.companyId;
    this.rooms = data.rooms;
    this.bookingChannel = data.bookingChannel;
    this.country = data.country;
    this.softBundle = data.softBundle;
    this.promotionCode = data.promotionCode;
    this.originalBasketReference = data.originalBasketReference;
    this.promoKind = data.promoKind;
    this.isPromoBox = data.isPromoBox;
    this.brand = data.brand;
    this.rateName = data.rateName;
    this.roomClass = data.roomClass;
  }
}
