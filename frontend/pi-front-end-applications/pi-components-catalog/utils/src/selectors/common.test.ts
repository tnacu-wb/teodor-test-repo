/* eslint-disable @typescript-eslint/no-explicit-any */
import {
  BFStep,
  RateExtraInfo,
  ReservationById,
  HotelInformation,
  Address,
} from '@whitbread-eos/api';

import { bookingGuestCount, enhanceCheckoutStepList, hotelInformationSelector } from './common';

const mockDataHIResponse: HotelInformation = {
  importantInfo: {
    title: '',
    infoItems: [
      {
        endDate: '',
        priority: '',
        text: '',
        startDate: '',
      },
    ],
  },
  brand: 'PI',
  name: 'holborn',
  headline: 'headline',
  galleryImages: [
    {
      imageSrc: '/image-src.png',
      thumbnailSrc: '/thumbnail.png',
      alt: 'img-alt',
      caption: 'caption 1',
      iconSrc: '/icon-src.png',
    },
  ],
  hotelFacilities: [
    {
      code: 'SHW',
      name: 'Shower',
      description: 'this hotel includes a shower',
      weight: 5,
      icon: 'show icon',
      isVisible: true,
    },
  ],
  hotelId: '',
  coordinates: {
    latitude: 53.46524,
    longitude: -2.28816,
  },
  roomConfiguration: {
    tabGroups: [
      {
        groupId: 'DBL',
        groupName: 'Double room',
      },
    ],
    tabItems: [
      {
        roomType: 'room type',
        roomName: 'room title',
        roomDescription: 'This is a double room',
        facilities: [
          {
            code: 'ABC',
            name: 'facilities titlee',
            description: 'facilities description',
            weight: 1,
            icon: 'facilities icon',
            isVisible: true,
          },
        ],
        images: [
          {
            imageSrc: '/image-src.png',
            thumbnailSrc: '/thumbnail.png',
            alt: 'img-alt',
            caption: 'caption 1',
            iconSrc: '/icon-src.png',
          },
        ],
      },
    ],
  },
  parkingDescription: 'free parking',
  links: {
    detailsPage: '/link.html',
  },
  restaurant: {
    menus: [
      {
        name: 'brewyer fayre',
        description: 'description for brewyer fayre',
        imageSrc: '/image-src.png',
        menuSrc: '/menu.pdf',
        menuLabel: 'menu label',
        disclaimer: 'this is a disclaimer',
      },
    ],
    logoSrc: '/logo-src.png',
    name: 'brewyes fayre logo',
    description: 'this is a description',
  },
  address: {
    addressLine1: '120',
    addressLine2: 'Holborn',
    addressLine3: 'London',
    addressLine4: '32',
    country: 'United Kingdom',
    postalCode: 'EC1N 2TD',
  },
  satNavDirections: 'turn left',
  directions: 'turn left',
  transportInformation: ['take bus'],
};

const mockReservationListItems: ReservationById[] = [
  {
    roomStay: {
      accessibleRoom: {
        isAccessible: true,
        phoneNumber: '0333 321 3104',
      },
      adultsNumber: 1,
      arrivalDate: '2022-06-20',
      childrenNumber: 0,
      departureDate: '2022-06-22',
      infoMessages: [],
      rateName: 'Flex',
      roomType: 'Double',
      rateExtraInfo: {
        rateName: 'Flex',
      } as RateExtraInfo,
      ratePlanCode: 'FLEXRATE',
      roomExtraInfo: {
        roomType: 'Double',
        roomName: 'Double',
        roomDescription: '',
      },
    },
    billing: {
      address: {
        addressLine1: '',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        country: '',
        postalCode: '',
      },
      email: '',
      firstName: '',
      lastName: '',
      telephone: '',
      title: '',
    },
    reservationGuestList: [
      {
        givenName: '',
        surName: '',
      },
    ],
    depositPolicies: [
      {
        policyCode: '',
        amountPaid: {
          amount: 0,
          currencyCode: '',
        },
        amountDue: {
          amount: 0,
          currencyCode: '',
        },
      },
    ],
  },
];

const mockDEAddress: Address = {
  addressLine1: 'Europa-Allee 44',
  addressLine2: 'Frankfurt/Main',
  addressLine3: '',
  country: 'Germany',
  postalCode: '60327',
};

describe('common selectors', () => {
  describe('hotelInformationSelector Method', () => {
    it('should return an object with hotel name and address', () => {
      expect(hotelInformationSelector(mockDataHIResponse)).toEqual({
        hotelAddress: ['120', 'Holborn', 'London', '32', 'EC1N 2TD'],
        hotelName: 'holborn',
        hotelCountry: 'United Kingdom',
        hotelBrand: 'PI',
      });
    });

    it('should return an object with hotel name and address line 4 as an empty string', () => {
      mockDataHIResponse.address.addressLine4 = '';
      expect(hotelInformationSelector(mockDataHIResponse)).toEqual({
        hotelAddress: ['120', 'Holborn', 'London', 'EC1N 2TD'],
        hotelName: 'holborn',
        hotelCountry: 'United Kingdom',
        hotelBrand: 'PI',
      });
    });

    it('should return null if hotel name is undefined', () => {
      expect(hotelInformationSelector(undefined as any)).toEqual(null);
    });

    it('should return empty array for hotelAddress if address is undefined', () => {
      mockDataHIResponse.address = undefined as any;
      expect(hotelInformationSelector(mockDataHIResponse)).toEqual({
        hotelAddress: [],
        hotelCountry: undefined,
        hotelName: 'holborn',
        hotelBrand: 'PI',
      });
    });

    it('should return formatted hotelAddress for DE if postalCode is present', () => {
      mockDataHIResponse.brand = 'PID';
      mockDataHIResponse.address = mockDEAddress;
      expect(hotelInformationSelector(mockDataHIResponse)).toEqual({
        hotelAddress: ['Europa-Allee 44', '60327 Frankfurt/Main'],
        hotelCountry: 'Germany',
        hotelName: 'holborn',
        hotelBrand: 'PID',
      });
    });

    it('should return formatted hotelAddress for DE if postalCode is not present', () => {
      mockDataHIResponse.brand = 'PID';
      mockDataHIResponse.address = mockDEAddress;
      mockDataHIResponse.address.postalCode = '';
      expect(hotelInformationSelector(mockDataHIResponse)).toEqual({
        hotelAddress: ['Europa-Allee 44', 'Frankfurt/Main'],
        hotelCountry: 'Germany',
        hotelName: 'holborn',
        hotelBrand: 'PID',
      });
    });
  });

  const listSteps: BFStep[] = [
    {
      id: 'ancillaries',
      step: '1',
      title: 'Choose your meals',
    },
    {
      id: 'guestDetails',
      step: '2',
      title: 'Your details',
    },
    {
      id: 'payment',
      step: '3',
      title: 'Payment details',
    },
    {
      id: 'spf',
      step: '4',
      title: 'Call SPF',
    },
    {
      id: 'confirmation',
      step: '5',
      title: 'Confirmation',
    },
  ];

  describe('enhanceCheckoutStepList Method', () => {
    it('should return a list of steps and active step should be 1', () => {
      expect(enhanceCheckoutStepList(listSteps, 'ancillaries')).toEqual({
        activeStep: 1,
        steps: [
          { id: 1, title: 'Choose your meals' },
          { id: 2, title: 'Your details' },
          { id: 3, title: 'Payment details' },
          { id: 4, title: 'Call SPF' },
        ],
      });
    });

    it('should return a list of steps and active step should be 2', () => {
      expect(enhanceCheckoutStepList(listSteps, 'guest-details')).toEqual({
        activeStep: 2,
        steps: [
          { id: 1, title: 'Choose your meals' },
          { id: 2, title: 'Your details' },
          { id: 3, title: 'Payment details' },
          { id: 4, title: 'Call SPF' },
        ],
      });
    });

    it('should return a list of steps and active step should be 3', () => {
      expect(enhanceCheckoutStepList(listSteps, 'payment')).toEqual({
        activeStep: 3,
        steps: [
          { id: 1, title: 'Choose your meals' },
          { id: 2, title: 'Your details' },
          { id: 3, title: 'Payment details' },
          { id: 4, title: 'Call SPF' },
        ],
      });
    });

    it('should return a list of steps and active step should be 4', () => {
      expect(enhanceCheckoutStepList(listSteps, 'spf')).toEqual({
        activeStep: 4,
        steps: [
          { id: 1, title: 'Choose your meals' },
          { id: 2, title: 'Your details' },
          { id: 3, title: 'Payment details' },
          { id: 4, title: 'Call SPF' },
        ],
      });
    });

    it('should return a list of steps and active step should be 5', () => {
      expect(enhanceCheckoutStepList(listSteps, 'confirmation')).toEqual({
        activeStep: 5,
        steps: [
          { id: 1, title: 'Choose your meals' },
          { id: 2, title: 'Your details' },
          { id: 3, title: 'Payment details' },
          { id: 4, title: 'Call SPF' },
        ],
      });
    });
  });

  describe('bookingGuestCount Method', () => {
    it('should return data in correct format', () => {
      expect(bookingGuestCount(mockReservationListItems)).toEqual({
        adultsNumber: [1],
        childrenNumber: [0],
      });
    });

    it('should return number of guests', () => {
      mockReservationListItems[0].roomStay = undefined as any;
      expect(bookingGuestCount(mockReservationListItems)).toEqual({
        adultsNumber: [0],
        childrenNumber: [0],
      });
    });
  });
});
