import { ExtrasPackagePricePerItem, MealsSelection } from './ancillariesPackages';
import { AcceptedRoomCodes } from './search';

export interface BookingReservationRooms {
  roomId: string;
  roomType: string;
  roomCost: Cost;
  adults: number;
  children: number;
  cot: boolean;
  guest: PersonalDetails;
  kidsMeal: BookingPackageDetails[];
  adultsMeal: BookingPackageDetails[];
}

export interface BookingPackageDetails {
  packageCode: string;
  description: string;
  totalPrice: Cost;
  noSelections: number;
}

type PersonalDetails = {
  title: string;
  firstName: string;
  lastName: string;
};

export type Cost = {
  amount: number;
  currency: string;
};

export type RoomTypeLabelCode = {
  roomLabel: string;
  roomTypeCode: AcceptedRoomCodes[];
};

export interface RoomDetails {
  leadGuestName: string;
  roomType: string;
  roomPrice?: number;
  cot: boolean;
  noAdults: number;
  noChildren: number;
  noNights?: number;
  adultMealDescription: MealsSelection[];
  childrenMealDescription?: MealsSelection[];
  extrasPackageRoom?: ExtrasPackagePricePerItem;
}
