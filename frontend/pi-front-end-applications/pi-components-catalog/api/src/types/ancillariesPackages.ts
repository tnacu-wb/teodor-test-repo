import { MealItem, MealKids, AncillaryCloseout } from './graphql';

export interface MealItemExtension extends MealItem {
  totalPriceForEntireStay?: number;
  totalPrice?: number;
  upsellType?: string;
}

export interface SelectedMealsPerRoom {
  adults: string[];
  children: string[];
  reservationId?: string;
}

export interface ExtrasPackages {
  currency: string;
  description: string;
  id: string;
  imageSrc: string;
  name: string;
  order: number;
  price: number;
  available: number;
}

export interface SelectedExtrasPackage {
  packagesList: string[];
  reservationId: string | undefined;
  price: number;
  previousEciSelection?: number;
  previousLcoSelection?: number;
}

export interface ExtrasPackagePricePerItem {
  packagesList?: string[];
  reservationId?: string | undefined;
  priceEci?: number;
  priceLco?: number;
  priceWifi?: number;
  priceBOProsecco?: number;
}

export interface ExtrasPackagesPrices {
  eciPrice?: number;
  lcoPrice?: number;
  wifiPrice?: number;
  bOfProseccoPrice?: number;
}

export interface PackagesSelection {
  id?: string;
  noOfSelections?: number;
}
export interface RoomPackageSelection {
  packagesSelection: PackagesSelection[];
  reservationId: string | undefined;
  price: number;
}

export interface MealsSelection {
  title?: string;
  id?: string;
  price?: number;
  noSelections: number;
}

export interface MealsSelectionDetailsPerRoom {
  adultsMeals: MealsSelection[];
  childrenMeals: MealsSelection[];
}

export interface UniqueMealsCounter {
  [key: string]: number;
}

export interface BookingGuests {
  adults: number;
  children: number;
}

export interface AncillaryFilterData {
  arrivalDate: string;
  departureDate: string;
  ancillaryCloseoutData: AncillaryCloseout;
  adultsMeals: MealItemExtension[];
  childrenMeals: MealKids[];
  closedOutMeals?: MealItemExtension[];
}
