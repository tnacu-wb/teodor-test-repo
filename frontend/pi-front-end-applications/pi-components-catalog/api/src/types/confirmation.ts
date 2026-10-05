import type { SITE } from '../constants/site';
import { Coordinates } from './graphql';

interface HotelDirectionData {
  coordinates: Coordinates;
  directions: string;
  brand: string;
}

export interface HotelInformationData {
  hotelInformation: HotelDirectionData;
}

export interface PromotionPanel {
  image: string;
  name: string;
  description: string;
  linkLabel: string;
  linkPath: string;
}

export interface NewsletterSignup {
  introViewText: string;
  introViewTitle: string;
  signUpButtonText: string;
}

export interface StaticContentQueryInput {
  country: string;
  language: string;
  site: SITE;
  businessBooker: boolean;
}

export interface PromotionQueryInput {
  country: string;
  language: string;
  hotelId: string;
  rateCode: string;
  bookingChannel?: string;
}

export interface RoomsExpanded {
  roomNumber: number;
  expanded: boolean;
}
