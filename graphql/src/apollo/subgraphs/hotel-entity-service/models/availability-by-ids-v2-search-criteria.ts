import { RoomSearchV2 } from './room-search-v2';
import { Rate } from './rate';
import { BookingChannelCriteria } from '../../hotel-reservation-entity-service/models/booking-channel-criteria';

export class AvailabilityByIdsV2SearchCriteria {
  arrivalDate: string;
  departureDate: string;
  rooms: RoomSearchV2[];
  hotelIds: string[];
  rates?: Rate;
  bookingChannel?: BookingChannelCriteria;
  vatNotRequired?: boolean;
  isOta?: boolean;

  constructor(data: any) {
    this.arrivalDate = data.arrivalDate;
    this.departureDate = data.departureDate;
    this.rooms = data.rooms;
    this.hotelIds = data.hotelIds;
    this.rates = data.rates;
    this.bookingChannel = data.bookingChannel;
    this.vatNotRequired = data.vatNotRequired;
    this.isOta = data.isOta;
  }
}
