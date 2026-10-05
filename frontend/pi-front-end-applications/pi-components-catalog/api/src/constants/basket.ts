// Initial value for BasketDetailsState in local storage
import getConfig from 'next/config';

import { RoomClass } from '../enums';
import { HIBasicBasketDetails, HIBasketData } from '../types';

const { publicRuntimeConfig = {} } = getConfig() || {};

export const serverSideCookieOptions = {
  httpOnly: false,
  domain: publicRuntimeConfig.NEXT_PUBLIC_COOKIES_DOMAIN,
  maxAge: 30 * 60 * 1000,
};

export const BASKET_DETAILS_STORAGE_KEY = 'BasketDetailsState';

export const BASIC_BASKET_DETAILS_INITIAL_VALUE: HIBasicBasketDetails = {
  hotelId: '',
  nightsNumber: 0,
  adultsNumber: 0,
  childrenNumber: 0,
  endDate: '',
  startDate: '',
  rateCode: '',
  reservationId: '',
  bookingFlowId: '',
};

export const BASKET_DETAILS_STATE_INITIAL_VALUE: HIBasketData = {
  hotelId: '',
  arrival: '',
  departure: '',
  numberOfUnits: 0,
  numberOfNights: 0,
  selectedRate: {
    ratePlanCode: '',
    roomTypes: [],
    rateCategory: '',
    cellCode: '',
  },
  roomClass: RoomClass.ST,
  rateName: '',
  roomTypeInformationResponse: {
    isLoadingRoomTypeInformation: false,
    dataRoomTypeInformation: {
      roomTypeInformation: {
        roomTypes: [],
      },
    },
    isErrorRoomTypeInformation: false,
    errorRoomTypeInformation: null,
  },
  bookingFlow: {
    bookingFlowItems: [],
  },
  phoneNumber: '',
  brand: 'PI',
  silentSubstitutionLabels: [],
  prevReservationId: '',
};

export const SILENT_SUBSTITUTION_STORAGE_KEY = 'SilentSubstitutionRoomLabels';

export const BASKET_DETAILS_REDIS_TTL = 30 * 60; // 30 minutes
