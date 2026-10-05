import { FormProps } from '@whitbread-eos/atoms';

export const ROOM_TYPE_KEY = 'roomType';
export const ADULTS_NUMBER_KEY = 'adultsNumber';
export const CHILDREN_NUMBER_KEY = 'childrenNumber';
export const CHILDREN_PARAM = 'children';
export const ADULTS_PARAM = 'adults';

export const INITIAL_ROOM_DETAILS = {
  adults: 1,
  children: 0,
  roomType: 'Double',
  operaRoomType: '',
  roomTypeCode: 'DB',
};

export const INITIAL_ROOM_OCCUPANCY = { displayRoomOccupancy: false, price: '', guests: '' };

export const INITIAL_LEAD_GUEST_DETAILS: FormProps['defaultValues'] = {
  title: '',
  firstName: '',
  lastName: '',
  emailAddress: '',
  address: {
    addressLine1: '',
    addressLine2: '',
    addressLine3: '',
    postalCode: '',
    cityName: '',
    country: '',
    countryCode: '',
  },
};

export const HOTEL_AVAILABILITY_QUERY_KEY = 'hotelAvailability';
export const HOTEL_AVAILABILITY_BB_QUERY_KEY = 'hotelAvailabilityBB';

export const CITYTAX_HOTEL_COUNTRY_EN = 'Germany';
export const CITYTAX_HOTEL_COUNTRY_DE = 'Deutschland';
export const HOTEL_AVAILABILITY_DISCOUNT_RATE_QUERY_KEY = 'hotelAvailabilityDiscountRate';
