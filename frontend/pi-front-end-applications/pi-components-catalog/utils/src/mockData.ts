import { ReservationById } from '@whitbread-eos/api';

export const bknReservationsMock: ReservationById[] = [
  {
    additionalGuestInfo: {
      purposeOfStay: '',
    },
    reservationId: '3749886',
    roomStay: {
      adultsNumber: 2,
      childrenNumber: 0,
      arrivalDate: '2024-03-01',
      departureDate: '2024-03-02',
      ratePlanCode: 'FLEXRATE',
      rateExtraInfo: {
        rateName: 'Flex',
        rateClassification: '',
      },
      roomExtraInfo: {
        roomType: 'DOUBLE',
        roomName: 'Double room',
        roomDescription: '',
      },
      accessibleRoom: {
        isAccessible: false,
        phoneNumber: '0333 321 1262',
      },
    },
    reservationGuestList: [
      {
        givenName: '',
        surName: 'TEMPORARY',
        nameTitle: undefined,
      },
    ],
    billing: undefined,
  },
  {
    additionalGuestInfo: {
      purposeOfStay: '',
    },
    reservationId: '3749885',
    roomStay: {
      adultsNumber: 2,
      childrenNumber: 0,
      arrivalDate: '2024-03-01',
      departureDate: '2024-03-02',
      ratePlanCode: 'FLEXRATE',
      rateExtraInfo: {
        rateName: 'Flex',
        rateClassification: '',
      },
      roomExtraInfo: {
        roomType: 'FMTRPL',
        roomName: 'Family room',
        roomDescription: '',
      },
      accessibleRoom: {
        isAccessible: false,
        phoneNumber: '0333 321 1262',
      },
    },
    reservationGuestList: [
      {
        givenName: '',
        surName: 'TEMPORARY',
        nameTitle: undefined,
      },
    ],
    billing: undefined,
  },
  {
    additionalGuestInfo: {
      purposeOfStay: '',
    },
    reservationId: '3749884',
    roomStay: {
      adultsNumber: 2,
      childrenNumber: 1,
      arrivalDate: '2024-03-01',
      departureDate: '2024-03-02',
      ratePlanCode: 'FLEXRATE',
      rateExtraInfo: {
        rateName: 'Flex',
        rateClassification: '',
      },
      roomExtraInfo: {
        roomType: 'FMTRPL',
        roomName: 'Family room',
        roomDescription: '',
      },
      accessibleRoom: {
        isAccessible: false,
        phoneNumber: '0333 321 1262',
      },
    },
    reservationGuestList: [
      {
        givenName: '',
        surName: 'TEMPORARY',
        nameTitle: undefined,
      },
    ],
    billing: undefined,
  },
  {
    additionalGuestInfo: {
      purposeOfStay: '',
    },
    reservationId: '3749883',
    roomStay: {
      adultsNumber: 1,
      childrenNumber: 0,
      arrivalDate: '2024-03-01',
      departureDate: '2024-03-02',
      ratePlanCode: 'FLEXRATE',
      rateExtraInfo: {
        rateName: 'Flex',
        rateClassification: '',
      },
      roomExtraInfo: {
        roomType: 'DOUBLE',
        roomName: 'Double room',
        roomDescription: '',
      },
      accessibleRoom: {
        isAccessible: false,
        phoneNumber: '0333 321 1262',
      },
    },
    reservationGuestList: [
      {
        givenName: '',
        surName: 'TEMPORARY',
        nameTitle: undefined,
      },
    ],
    billing: undefined,
  },
];

export const MOCK_SOFT_BUNDLES = [
  {
    packageCodes: [
      {
        id: 'BFADBF',
        description: 'Unlimited Premier Inn Breakfast',
        price: 10.99,
      },
      {
        id: 'FI24HR',
        description: 'Ultimate Wi-fi',
        price: 10.99,
      },
      {
        id: 'HSCKIN',
        description: 'Early Check In',
        price: 10.99,
      },
    ],
    rate: ['FLEXRATE', 'SEMIFLEX'],
    roomClass: ['BIGWIN', 'DBLWIN', 'FMTRPL', 'DOUBLE'],
    isOptional: true,
  },
];
