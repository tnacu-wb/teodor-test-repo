import { AcceptedRoomCodes, AcceptedRoomTypes } from '@whitbread-eos/api';
import { add } from 'date-fns';

export const mockedRoomDropdownRoomCodes = {
  accessible: 'DIS',
  double: 'DB',
  family: 'FAM',
  single: 'SB',
  twin: 'TWIN',
};

export const mockedRoomDropdownLabels = {
  single: 'Single',
  double: 'Double',
  accessible: 'Accessible',
  twin: 'Twin',
  family: 'Family',
};

export const mockedRoomAvailabilityLabels = {
  adult: 'adult',
  adults: 'adults',
  child: 'child',
  children: 'children',
  addRoom: 'Add a room',
  roomAvailable: 'Room available',
  roomsUnavailable: 'Room unavailable',
  roomsUnavailableDescription: 'Room unavailable',
  checkRoomAvailability: 'Check availability',
  leadGuest: 'Lead Guest',
  cancelBtn: 'Cancel',
  guests: 'Guests',
  update: 'Update',
  roomSuccessfullyAdded: 'Room has been successfuly added',
  roomSuccessfullyUpdated: 'Room has been successfuly updated',
  cityTaxNotIncluded: '',
};

export const mockedLeadGuestDetailsLabels = {
  guestTitle: "'Mr','Mrs','Ms','Miss','Master','Dr','Lord','Lady','Sir','Col','Prof','Rev'",
  title: 'Title',
  firstName: 'First name *',
  lastName: 'Last name *',
  email: 'Email address',
  addressLine1: 'Address Line 1',
  addressLine2: 'Address Line 2',
  addressLine3: 'Address Line 3',
  postalCode: 'EC1N 2TD',
  city: 'London',
  country: 'GB',
};

export const mockedRemoveRoomModalLabels = {
  title: 'Remove room',
  confirmLabel: 'Are you sure you want to remove this room?',
  notificationLabel: 'This room type and rates may not be available in the future',
  removeModalRoom: 'Remove Room',
  cancelModalRoom: 'Cancel',
  roomSuccessfullRemoved: 'Room has been successfully removed',
};

export const mockedLeadGuestValidationLabels = {
  titleError: 'Please select your title',
  firstNameRequiredError: 'Please enter your first name (max 20 characters)',
  firstNameMinError: 'Please enter your first name in full.',
  lastNameRequiredError: 'Please enter your last name (max 30 characters)',
  firstNameInvalidError: 'Invalid characters in first name',
  lastNameInvalidError: 'Invalid characters in last name',
  emailInvalidError: 'Please enter a valid email address',
};

export const mockedNotificationLabels = {
  title: 'Your meal selection has been reset',
  description: "Please select a meal in the 'Your meal' section.",
};

export const mockedRoomsAndGuestsLabels = {
  roomModalLabels: {
    roomDropdownLabels: mockedRoomDropdownLabels,
    roomDropdownRoomCodes: mockedRoomDropdownRoomCodes,
    roomAvailabilityLabels: mockedRoomAvailabilityLabels,
    leadGuestLabels: mockedLeadGuestDetailsLabels,
    leadGuestValidationLabels: mockedLeadGuestValidationLabels,
    notificationLabels: mockedNotificationLabels,
  },
  removeRoomModalLabels: mockedRemoveRoomModalLabels,
  roomLabel: 'Add a room',
  edit: 'Edit',
  remove: 'Remove',
};

export const mockedAmendReservation = {
  reservationGuestList: [
    {
      givenName: 'Guest One',
      surName: 'Test',
      nameTitle: 'Mrs.',
    },
  ],
  roomStay: {
    adultsNumber: 1,
    childrenNumber: 0,
    arrivalDate: '2023-07-18',
    departureDate: '2023-07-22',
    ratePlanCode: 'FLEXRATE',
    roomExtraInfo: {
      roomName: 'Double room',
      groupId: 'double',
    },
    roomPrice: 45,
  },
};

export const mockednullishTitleAmendReservation = {
  ...mockedAmendReservation,
  reservationGuestList: [
    {
      givenName: 'Guest One',
      surName: 'Test',
      nameTitle: null,
    },
  ],
  reservationId: '',
  billing: '',
};

export const mockedRoomsAndGuestsData = {
  currencyCode: 'GBP',
  reservations: [mockedAmendReservation],
};

const acceptedRoomTypes: Record<AcceptedRoomTypes, AcceptedRoomCodes> = {
  family: 'FAM',
  single: 'SB',
  double: 'DB',
  twin: 'TWIN',
  accessible: 'DIS',
};

export const mockedRoomRules = {
  roomOccupancyLimitations: {
    roomOccupancies: [
      {
        acceptedRoomTypes: [acceptedRoomTypes.family],
        adultsNumber: 2,
        childrenNumber: 2,
      },
      {
        acceptedRoomTypes: [acceptedRoomTypes.family],
        adultsNumber: 2,
        childrenNumber: 1,
      },
      {
        acceptedRoomTypes: [
          acceptedRoomTypes.double,
          acceptedRoomTypes.twin,
          acceptedRoomTypes.accessible,
        ],
        adultsNumber: 2,
        childrenNumber: 0,
      },
      {
        acceptedRoomTypes: [acceptedRoomTypes.family],
        adultsNumber: 1,
        childrenNumber: 2,
      },
      {
        acceptedRoomTypes: [acceptedRoomTypes.family],
        adultsNumber: 1,
        childrenNumber: 1,
      },
      {
        acceptedRoomTypes: [
          acceptedRoomTypes.single,
          acceptedRoomTypes.double,
          acceptedRoomTypes.accessible,
        ],
        adultsNumber: 1,
        childrenNumber: 0,
      },
    ],
  },
};

export const mockedHotelAvailabilityParams = {
  arrival: '2024-03-30',
  bookingChannel: {
    channel: 'PI',
    language: 'en',
    subchannel: 'WEB',
  },
  channel: 'PI',
  brand: 'pi',
  country: 'gb',
  departure: '2024-04-03',
  hotelId: 'LONEUS',
  language: 'en',
  rateCode: 'FLEXRATE',
  ratePlanCodes: ['FLEXRATE'],
  rooms: [],
};

export const mockedHotelAvailabilityResponse = {
  hotelAvailability: {
    hotelId: 'LONEUS',
    startDate: '2023-09-28',
    endDate: '2023-09-28',
    available: true,
    roomRates: [
      {
        ratePlanCode: 'FLEXRATE',
        cellCode: 'EMP01',
        roomTypes: [
          {
            roomType: 'DB',
            adults: 2,
            children: 0,
            rooms: [
              {
                pmsRoomType: 'DOUBLE',
                silentSubstitution: true,
                roomPriceBreakdown: {
                  totalNetAmount: 15,
                  currencyCode: 'GBP',
                  dailyPrices: [
                    {
                      date: '2023-09-28',
                      netPrice: 15,
                    },
                  ],
                },
              },
            ],
          },
        ],
      },
      {
        ratePlanCode: 'ADVANCE',
        cellCode: null,
        roomTypes: [
          {
            roomType: 'DB',
            adults: 2,
            children: 0,
            rooms: [
              {
                pmsRoomType: 'DOUBLE',
                silentSubstitution: true,
                roomPriceBreakdown: {
                  totalNetAmount: 35,
                  currencyCode: 'GBP',
                  dailyPrices: [
                    {
                      date: '2023-09-28',
                      netPrice: 35,
                    },
                  ],
                },
              },
            ],
          },
        ],
      },
      {
        ratePlanCode: 'STANDARD',
        cellCode: null,
        roomTypes: [
          {
            roomType: 'DB',
            adults: 2,
            children: 0,
            rooms: [
              {
                pmsRoomType: 'FMTRPL',
                silentSubstitution: true,
                roomPriceBreakdown: {
                  totalNetAmount: 30,
                  currencyCode: 'GBP',
                  dailyPrices: [
                    {
                      date: '2023-09-28',
                      netPrice: 30,
                    },
                  ],
                },
              },
            ],
          },
        ],
      },
      {
        ratePlanCode: 'NONFLEX',
        cellCode: null,
        roomTypes: [
          {
            roomType: 'DB',
            adults: 2,
            children: 0,
            rooms: [
              {
                pmsRoomType: 'DOUBLE',
                silentSubstitution: true,
                roomPriceBreakdown: {
                  totalNetAmount: 25,
                  currencyCode: 'GBP',
                  dailyPrices: [
                    {
                      date: '2023-09-28',
                      netPrice: 25,
                    },
                  ],
                },
              },
            ],
          },
        ],
      },
      {
        ratePlanCode: 'FIXEDM09',
        cellCode: null,
        roomTypes: [
          {
            roomType: 'DB',
            adults: 2,
            children: 0,
            rooms: [
              {
                pmsRoomType: 'DOUBLE',
                silentSubstitution: true,
                roomPriceBreakdown: {
                  totalNetAmount: 1052.98,
                  currencyCode: 'GBP',
                  dailyPrices: [
                    {
                      date: '2023-09-28',
                      netPrice: 1052.98,
                    },
                  ],
                },
              },
            ],
          },
        ],
      },
    ],
  },
};

export const mockedHotelAvailabilitywithoutRatesResponse = {
  hotelAvailability: {
    hotelId: 'LONEUS',
    startDate: '2023-09-28',
    endDate: '2023-09-28',
    available: true,
    roomRates: [],
  },
};

export const mockedStayDatesParams = {
  data: {
    hotelName: 'Manchester Old Trafford',
    arrivalDate: new Date(),
    departureDate: add(new Date(), { days: 1 }),
    maxNights: 9,
    maxArrivalDate: 364,
    maxRooms: 4,
    originalArrivalDate: new Date(),
  },
  labels: {
    arrivalDate: 'Arrival date',
    nightsLabel: 'Nights',
    nightOption: 'night',
    nightsOption: 'nights',
    checkOut: 'Check out:',
    hotel: 'Hotel',
    yourStayDatesTitle: 'Your stay dates',
    invalidNights: 'Invalid nights',
    numberOfNightsErrorMessage: 'Number of nights error',
  },
  isLoading: false,
  promotionsData: {
    showPromo: true,
    isWithinPromoWindow: true,
    promotionCode: 'ST10R',
    landingPage: '',
    promoBannerColour: '#511E62',
    promoBannerIcon: '/content/dam/global/icons/common/price-tag-orange-16.svg',
    promoBannerTitle: "<span style='color: #FDB913;'><b>Summer Sale: 10% off</b></span>",
    promoBannerSubtitle:
      '<b>Select one of our hotels to see your discount.</b> Prices shown here don’t include your discount yet.',
    promoInvalidMessage: null,
    promoExpiredMessage: null,
    promoAmendMessage: '<b>Your booking includes a promotion.</b> Cancel this booking and rebook.',
  },
};
