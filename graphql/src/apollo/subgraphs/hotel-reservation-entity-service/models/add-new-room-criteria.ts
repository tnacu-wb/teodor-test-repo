import { BookingChannelCriteria } from './booking-channel-criteria';
import { GuestAddress } from '../../account-entity-service/models/guest-address';

export class AddNewRoomCriteria {
  bookingChannel: BookingChannelCriteria;
  tempBookingRef: string;
  roomOccupancy: RoomOccupancyAmend;
  leadGuest: LeadGuest;
  roomType: string;
  token: string;
  ratePlanCode: string;
  specialRequests: string[];

  constructor(args: any) {
    this.bookingChannel = new BookingChannelCriteria(args.bookingChannel);
    this.tempBookingRef = args.tempBookingRef;
    this.roomOccupancy = new RoomOccupancyAmend(args.roomOccupancy);
    this.leadGuest = new LeadGuest(args.leadGuest);
    this.roomType = args.roomType;
    this.token = args.token;
    this.ratePlanCode = args.ratePlanCode;
    this.specialRequests = args.specialRequests;
  }
}

class RoomOccupancyAmend {
  adults: number;
  children: number;
  cotRequired: boolean;

  constructor(data: any) {
    this.adults = data.adults;
    this.children = data.children;
    this.cotRequired = data.cotRequired;
  }
}

class LeadGuest {
  title: string;
  firstName: string;
  lastName: string;
  email: string;
  address: GuestAddress;

  constructor(data: any) {
    this.title = data.title;
    this.firstName = data.firstName;
    this.lastName = data.lastName;
    this.email = data.email;
    this.address = new GuestAddress(data.address);
  }
}
