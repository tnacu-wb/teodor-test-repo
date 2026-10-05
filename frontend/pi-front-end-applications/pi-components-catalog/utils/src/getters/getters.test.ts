/* eslint-disable @typescript-eslint/no-explicit-any */
import {
  BCReservationListItem,
  BCRoomStay,
  ReservationById,
  RoomStay,
  HIRoomType,
  Packages,
  RoomTypeLabelCode,
  SOURCE_SYSTEM,
  ExtrasId,
} from '@whitbread-eos/api';
import React from 'react';

import {
  getBartRoomTypeLabels,
  getBookingDetailsForCard,
  getCardEnding,
  getDistanceUnitBasedOnLocale,
  getDistanceUnitBasedOnLocaleSb,
  getExtrasPackagePrice,
  getIDVPassedStatus,
  getMaxValueFromHDPRoomTypes,
  getMaxValueFromRoomStays,
  getMaxValueFromRoomStaysBC,
  getMealForCard,
  getMealQuantity,
  getNightsNumber,
  getNoOfDaysInYear,
  getObjRoomLabelForRoomCodes,
  getPlaceholderString,
  getRoomDetailsForCard,
  getRoomsPlaceholder,
  getRoomTypeLabelsBySourceSystem,
  getSecureTwoURL,
  getSelectedDonationPackage,
  getSessionStorageValuesForBookings,
  getSortedCountries,
  getSortedCountriesByCurrentLang,
  getSortedCountriesByCurrentLangCcui,
  getTotalPeople,
  extrasNamingCheck,
  getCentrallyStoredCardBillingAddress,
  getTodayTomorrowDate,
  getUserAndRoles,
} from './getters';

let windowSpy;
let mockDataCountries;

const summaryLabels = {
  adult: 'adult',
  adults: 'adults',
  child: 'child',
  children: 'children',
  room: 'room',
  rooms: 'rooms',
};

const mockPackages: Packages = {
  roomSelection: [
    {
      packagesSelection: [
        {
          id: 'meal1',
          noOfSelections: 2,
        },
        {
          id: 'meal2',
          noOfSelections: 3,
        },
      ],
    },
  ],
  meals: [],
  mealsKids: [],
};

const mockBartRoomTypeLabelsObj = {
  DB: 'dashboard.bookings.doubleRoom',
  DIS: 'dashboard.bookings.doubleRoom',
  DOUBLE: 'dashboard.bookings.doubleRoom',
  FAM: 'dashboard.bookings.familyRoom',
  FMQUAD: 'dashboard.bookings.familyRoom',
  FMTRPL: 'dashboard.bookings.familyRoom',
  LOWDBL: 'dashboard.bookings.doubleRoom',
  TB: 'dashboard.bookings.familyRoom',
  TBT: 'dashboard.bookings.twinRoom',
  TWIN: 'dashboard.bookings.twinRoom',
};

const mockUseTranslationFn = (key: string) => {
  switch (key) {
    case 'dashboard.bookings.doubleRoom':
      return 'Double Room';
    case 'dashboard.bookings.familyRoom':
      return 'Family Room';
    case 'dashboard.bookings.twinRoom':
      return 'Twin - double bed and sofa bed';
    default:
      return 'default';
  }
};

const mockTranslationFn = (t: string) => t;

const mockRoomTypeLabels: RoomTypeLabelCode[] = [
  { roomLabel: 'Standard Extra room', roomTypeCode: ['PB'] },
  { roomLabel: 'Accessible Room', roomTypeCode: ['DIS'] },
  { roomLabel: 'Premier Plus Room', roomTypeCode: ['RB'] },
  { roomLabel: 'Family Room', roomTypeCode: ['FAM'] },
  { roomLabel: 'Premier Plus Room', roomTypeCode: ['RB'] },
  { roomLabel: 'Standard Room', roomTypeCode: ['SB'] },
  { roomLabel: 'Twin Room', roomTypeCode: ['TWIN'] },
  { roomLabel: 'Accessible twin bedroom with a lowered bath', roomTypeCode: ['LOWTWN'] },
  {
    roomLabel: 'Accessible twin bedroom with level access shower room',
    roomTypeCode: ['WETTWN'],
  },
  {
    roomLabel: 'Accessible double bedroom with level access shower room',
    roomTypeCode: ['WETDBL'],
  },
  { roomLabel: 'Accessible double bedroom with a lowered bath', roomTypeCode: ['LOWDBL'] },
  { roomLabel: 'Family Room', roomTypeCode: ['FMQUAD'] },
  { roomLabel: 'Twin Room', roomTypeCode: ['TWINRM'] },
  { roomLabel: 'Standard Room', roomTypeCode: ['SINGLE'] },
  { roomLabel: 'Premier Plus Room', roomTypeCode: ['PPLDBL'] },
  { roomLabel: 'Standard Extra room', roomTypeCode: ['EXTDBL'] },
  { roomLabel: 'Family Room', roomTypeCode: ['FMTRPL'] },
  { roomLabel: 'Double Room', roomTypeCode: ['DOUBLE'] },
  { roomLabel: 'Double Room', roomTypeCode: ['ZPLDBL'] },
];

const mockSourceSystemBart = SOURCE_SYSTEM.BART;

const mockMeal = {
  description: 'Meal Deal',
  noSelections: 1,
  packageCode: '17',
  totalPrice: {
    amount: 24.99,
    currency: 'GBP',
  },
};

const mockReservationDetails = {
  arrivalDate: '2023-05-30',
  basketReference: null,
  bookingReference: 'BFMR97757',
  bookingStatus: 'FUTURE',
  cancellationInfoResponse: {
    amendable: true,
    cancelable: true,
    ruleCompliant: true,
    aemLabelKey: '',
  },
  departureDate: '2023-05-31',
  dinnerAllowance: null,
  donationsPackage: null,
  hotelCode: 'LONBRO',
  hotelHasCityTaxForLeisure: false,
  newTotal: {
    amount: 135,
    currency: 'GBP',
  },
  nights: 1,
  noOfRooms: 1,
  outstandingAmount: {
    amount: 0,
    currency: 'GBP',
  },
  payment: {
    cardType: 'VI',
  },
  paymentOption: 'PAY_NOW',
  prepaidAmount: {
    amount: 135,
    currency: 'GBP',
  },
  previousTotal: {
    amount: 135,
    currency: 'GBP',
  },
  rateDescription:
    'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
  rateType: 'Flex',
  refund: {
    amount: 0,
    currency: 'GBP',
  },
  rooms: [
    {
      adults: 1,
      adultsMeal: [],
      children: 0,
      cot: false,
      guest: { firstName: 'John', lastName: 'Lewis', title: 'Mr' },
      kidsMeal: [],
      roomCost: { amount: 135, currency: 'GBP' },
      roomId: 'RBS,1',
      roomType: 'DB',
    },
  ],
  sourceSystem: SOURCE_SYSTEM.BART,
  totalCost: {
    amount: 135,
    currency: 'GBP',
  },
};

const mockReservationDetailsNoPackageCode = {
  arrivalDate: '2023-05-30',
  basketReference: null,
  bookingReference: 'BFMR97757',
  bookingStatus: 'FUTURE',
  cancellationInfoResponse: {
    amendable: true,
    cancelable: true,
    ruleCompliant: true,
    aemLabelKey: '',
  },
  departureDate: '2023-05-31',
  dinnerAllowance: null,
  donationsPackage: {
    description: 'On-line Charitable Pledge',
    noSelections: 1,
    packageCode: '145',
    totalPrice: {
      amount: 5,
      currency: 'GBP',
    },
  },
  hotelCode: 'LONBRO',
  hotelHasCityTaxForLeisure: false,
  newTotal: {
    amount: 135,
    currency: 'GBP',
  },
  nights: 1,
  noOfRooms: 1,
  outstandingAmount: {
    amount: 0,
    currency: 'GBP',
  },
  payment: {
    cardType: 'VI',
  },
  paymentOption: 'PAY_NOW',
  prepaidAmount: {
    amount: 135,
    currency: 'GBP',
  },
  previousTotal: {
    amount: 135,
    currency: 'GBP',
  },
  rateDescription:
    'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
  rateType: 'Flex',
  refund: {
    amount: 0,
    currency: 'GBP',
  },
  rooms: [
    {
      adults: 1,
      adultsMeal: [],
      children: 0,
      cot: false,
      guest: { firstName: 'John', lastName: 'Lewis', title: 'Mr' },
      kidsMeal: [],
      roomCost: { amount: 135, currency: 'GBP' },
      roomId: 'RBS,1',
      roomType: 'DB',
    },
  ],
  sourceSystem: SOURCE_SYSTEM.BART,
  totalCost: {
    amount: 135,
    currency: 'GBP',
  },
};

const mockRoom = {
  adults: 1,
  adultsMeal: [],
  children: 0,
  cot: false,
  guest: {
    firstName: 'John',
    lastName: 'Lewis',
    title: 'Mr',
  },
  kidsMeal: [],
  roomCost: {
    amount: 135,
    currency: 'GBP',
  },
  roomId: 'RBS,1',
  roomType: 'DB',
};

const mockRoomWithMeals = {
  adults: 1,
  adultsMeal: [
    {
      description: 'Premier Inn Breakfast',
      noSelections: 2,
      packageCode: '11',
      totalPrice: {
        amount: 21,
        currency: 'GBP',
      },
    },
  ],
  children: 0,
  cot: false,
  guest: {
    firstName: 'John',
    lastName: 'Lewis',
    title: 'Mr',
  },
  kidsMeal: [
    {
      description: 'Free Child Breakfast',
      noSelections: 1,
      packageCode: '15',
      totalPrice: {
        amount: 0,
        currency: 'GBP',
      },
    },
  ],
  extrasItems: [
    {
      description: 'Early check-in',
      noSelections: 1,
      packageCode: 'HSCKIN',
      totalPrice: {
        amount: 12,
        currency: 'GBP',
      },
    },
    {
      description: 'Late check-out',
      noSelections: 1,
      packageCode: 'HSCOU2',
      totalPrice: {
        amount: 10,
        currency: 'GBP',
      },
    },
    {
      description: 'Ultime Wi-Fi',
      noSelections: 1,
      packageCode: 'FI24HR',
      totalPrice: {
        amount: 5,
        currency: 'GBP',
      },
    },
  ],
  roomCost: {
    amount: 135,
    currency: 'GBP',
  },
  roomId: 'RBS,1',
  roomType: 'DB',
};

const mockBookedRoomType = 'Double Room';

const mockDataCountriesWithIso = {
  countries: [
    {
      countryCode: 'A',
      countryCodeISO: 'AT',
      countryLegend: 'Austria',
      passportRequired: true,
      dialingCode: '+43',
      flagImg: '/content/dam/global/flags/Austria.png',
    },
    {
      countryCode: 'AFG',
      countryCodeISO: 'AF',
      countryLegend: 'Afghanistan',
      passportRequired: true,
      dialingCode: '+93',
      flagImg: '/content/dam/global/flags/Afghanistan.png',
    },
    {
      countryCode: 'DE',
      countryCodeISO: 'DE',
      countryLegend: 'Germany',
      passportRequired: true,
      dialingCode: '+49',
      flagImg: '/content/dam/global/flags/Germany.png',
    },
    {
      countryCode: 'GB',
      countryCodeISO: 'GB',
      countryLegend: 'United Kingdom',
      passportRequired: true,
      dialingCode: '+44',
      flagImg: '/content/dam/global/flags/United_Kingdom.png',
    },
  ],
};

const mockPackagesExtrasItems = [
  {
    currency: 'GBP',
    description: 'Check out any time until 2pm (normal check-out time is 12pm).',
    id: 'HSCOU2',
    imageSrc: '/content/dam/global/extras/late-checkout.png',
    name: 'Late check-out',
    order: 2,
    price: 15,
    available: 8,
  },
  {
    currency: 'GBP',
    description: 'Check in any time from 11am (normal check-in time is 3pm).',
    id: 'HSCKIN',
    imageSrc: '/content/dam/global/extras/early-check-in.png',
    name: 'Early check-in',
    order: 1,
    price: 12,
    available: 9,
  },
];

describe('common getters', () => {
  describe('getNightsNumber Method', () => {
    it('should return the correct number of nights between two dates', function () {
      const firstDate = '2022-09-01';
      const secondDate = '2022-09-03';
      expect(getNightsNumber(firstDate, secondDate)).toEqual(2);
      const thirdDate = '2022-09-01';
      const fourthDate = '2022-09-05';
      expect(getNightsNumber(thirdDate, fourthDate)).toEqual(4);
      const fifthDate = '2022-10-01';
      const sixthDate = '2022-09-05';
      expect(getNightsNumber(fifthDate, sixthDate)).toEqual(-25);
    });
  });

  describe('getNoOfDaysInYear Method', () => {
    it('should return 365 days for a non-leap year', function () {
      expect(getNoOfDaysInYear(2023)).toEqual(365);
    });

    it('should return 366 days for a leap year', function () {
      expect(getNoOfDaysInYear(2024)).toEqual(366);
    });
  });

  describe('getDistanceUnitBasedOnLocale Method', () => {
    it('should return miles for english locale', function () {
      expect(getDistanceUnitBasedOnLocale('en')).toEqual('MILES');
    });

    it('should return kilometers for german locale', function () {
      expect(getDistanceUnitBasedOnLocale('de')).toEqual('KILOMETERS');
    });
  });

  describe('getDistanceUnitBasedOnLocaleSb Method', () => {
    it('should return mi for english locale', function () {
      expect(getDistanceUnitBasedOnLocaleSb('en')).toEqual('mi');
    });

    it('should return km for german locale', function () {
      expect(getDistanceUnitBasedOnLocaleSb('de')).toEqual('km');
    });
  });

  describe('getCardEnding Method', () => {
    it('should return last 4 digits for a card number', function () {
      expect(getCardEnding('1111-2222-3333-4444')).toEqual('4444');
    });

    it('should return empty string if no card number is provided', function () {
      expect(getCardEnding('')).toEqual('');
    });
  });

  describe('getSecureTwoURL Method', () => {
    const mockPublicRuntimeConfig = jest.fn();
    jest.mock('next/config', () => () => mockPublicRuntimeConfig());

    beforeEach(() => {
      windowSpy = jest.spyOn(window, 'window', 'get');
    });

    afterEach(() => {
      windowSpy.mockRestore();
      mockPublicRuntimeConfig.mockRestore();
    });

    it('should return secure2 url', function () {
      mockPublicRuntimeConfig.mockImplementationOnce(() => ({
        publicRuntimeConfig: {
          NEXT_PUBLIC_SECURE2_URL: 'https://secure2.premierinn.com',
        },
      }));
      const url = getSecureTwoURL();
      expect(url).toEqual('localhost');
    });

    it('should replace www with https://secure2 for window.location.hostname', function () {
      windowSpy.mockImplementation(() => ({ location: { hostname: 'www.localhost.com' } }));
      mockPublicRuntimeConfig.mockImplementationOnce(() => ({
        publicRuntimeConfig: {
          SOME_OTHER_KEY: 'https://localhost.com',
        },
      }));
      const url = getSecureTwoURL();
      expect(url).toEqual('https://secure2.localhost.com');
    });

    it('should return empty string if window is not defined', function () {
      windowSpy.mockImplementation(() => undefined);
      mockPublicRuntimeConfig.mockImplementationOnce(() => ({
        publicRuntimeConfig: {
          SOME_OTHER_KEY: 'https://localhost.com',
        },
      }));
      const url = getSecureTwoURL();
      expect(url).toEqual('');
    });

    it('should return window.location.hostname if getConfig returns falsy value and window is defined', function () {
      mockPublicRuntimeConfig.mockImplementationOnce(() => ({}));
      const url = getSecureTwoURL();
      expect(url).toEqual('localhost');
    });
  });

  describe('getSortedCountries Method', () => {
    it('should return undefined if no countries are passed', function () {
      const sortedCountriesOne = getSortedCountries(undefined);
      const sortedCountriesTwo = getSortedCountries({ countries: [] });
      expect(sortedCountriesOne).toEqual(undefined);
      expect(sortedCountriesTwo).toEqual(undefined);
    });

    it('should return alphabetically sorted countries by countryLegend', function () {
      const sortedCountries = getSortedCountries(mockDataCountriesWithIso);
      expect(sortedCountries).toEqual(mockDataCountriesWithIso.countries);

      const mockDataReversedCountries = { countries: mockDataCountriesWithIso.countries.reverse() };
      const sortedReversedCountries = getSortedCountries(mockDataReversedCountries);
      expect(sortedReversedCountries).toEqual(mockDataCountriesWithIso.countries);
    });
  });

  describe('getSortedCountriesByCurrentLang Method', () => {
    beforeEach(() => {
      mockDataCountries = [
        {
          countryCode: 'A',
          countryName: 'Austria',
          passportRequired: true,
          dialingCode: '+43',
          flagSrc: '/content/dam/global/flags/Austria.png',
        },
        {
          countryCode: 'AFG',
          countryName: 'Afghanistan',
          passportRequired: true,
          dialingCode: '+93',
          flagSrc: '/content/dam/global/flags/Afghanistan.png',
        },
        {
          countryCode: 'DE',
          countryName: 'Germany',
          passportRequired: true,
          dialingCode: '+49',
          flagSrc: '/content/dam/global/flags/Germany.png',
        },
        {
          countryCode: 'GB',
          countryName: 'United Kingdom',
          passportRequired: true,
          dialingCode: '+44',
          flagSrc: '/content/dam/global/flags/United_Kingdom.png',
        },
      ];
    });

    it('should return an empty array if no countries are passed', function () {
      const sortedCountriesOne = getSortedCountriesByCurrentLang(undefined as any, 'en');
      const sortedCountriesTwo = getSortedCountriesByCurrentLang([], 'en');
      expect(sortedCountriesOne).toEqual([]);
      expect(sortedCountriesTwo).toEqual([]);
    });

    it('should return UK first sorted country', function () {
      const dataCountriesCopy = [...mockDataCountries];
      const sortedCountries = getSortedCountriesByCurrentLang(mockDataCountries, 'en');
      expect(sortedCountries[0]).toEqual(dataCountriesCopy[3]);
    });

    it('should return DE first sorted country', function () {
      const dataCountriesCopy = [...mockDataCountries];
      const sortedCountries = getSortedCountriesByCurrentLang(mockDataCountries, 'de');
      expect(sortedCountries[0]).toEqual(dataCountriesCopy[2]);
    });

    it('should sort countries list alphabetically descending if UK or DE countries are missing from the array of ascending sorted countries', function () {
      const dataCountriesCopy = [...mockDataCountries];
      const sortedCountries = getSortedCountriesByCurrentLang(mockDataCountries.slice(0, 2), 'en');
      expect(sortedCountries).toEqual(dataCountriesCopy.slice(0, 2).reverse());
    });

    it('should sort countries list alphabetically ascending if UK or DE countries are missing from the array of descending sorted countries', function () {
      const dataCountriesCopy = [...mockDataCountries];
      const sortedCountries = getSortedCountriesByCurrentLang(
        mockDataCountries.slice(0, 2).reverse(),
        'en'
      );
      expect(sortedCountries).toEqual(dataCountriesCopy.slice(0, 2).reverse());
    });

    it('should keep same order sort for countries list if they are already sorted', function () {
      const dataCountriesCopy = [...mockDataCountries];
      const sortedCountries = getSortedCountriesByCurrentLang(
        [mockDataCountries[0], mockDataCountries[0]],
        'en'
      );
      expect(sortedCountries).toEqual([dataCountriesCopy[0], dataCountriesCopy[0]]);
    });
  });

  describe('getSortedCountriesByCurrentLangCcui Method', () => {
    beforeEach(() => {
      mockDataCountries = [
        {
          countryCode: 'A',
          countryCodeISO: 'AT',
          countryLegend: 'Austria',
          passportRequired: true,
          dialingCode: '+43',
          flagImg: '/content/dam/global/flags/Austria.png',
        },
        {
          countryCode: 'AFG',
          countryCodeISO: 'AF',
          countryLegend: 'Afghanistan',
          passportRequired: true,
          dialingCode: '+93',
          flagImg: '/content/dam/global/flags/Afghanistan.png',
        },
        {
          countryCode: 'DE',
          countryCodeISO: 'DE',
          countryLegend: 'Germany',
          passportRequired: true,
          dialingCode: '+49',
          flagImg: '/content/dam/global/flags/Germany.png',
        },
        {
          countryCode: 'GB',
          countryCodeISO: 'GB',
          countryLegend: 'United Kingdom',
          passportRequired: true,
          dialingCode: '+44',
          flagImg: '/content/dam/global/flags/United_Kingdom.png',
        },
      ];
    });

    it('should return an empty array if no countries are passed', function () {
      const sortedCountriesOne = getSortedCountriesByCurrentLangCcui(undefined as any, 'en');
      const sortedCountriesTwo = getSortedCountriesByCurrentLangCcui([], 'en');
      expect(sortedCountriesOne).toEqual([]);
      expect(sortedCountriesTwo).toEqual([]);
    });

    it('should return UK first sorted country', function () {
      const dataCountriesCopy = [...mockDataCountries];
      const sortedCountries = getSortedCountriesByCurrentLangCcui(mockDataCountries, 'en');
      expect(sortedCountries[0]).toEqual(dataCountriesCopy[3]);
    });

    it('should return DE first sorted country', function () {
      const dataCountriesCopy = [...mockDataCountries];
      const sortedCountries = getSortedCountriesByCurrentLangCcui(mockDataCountries, 'de');
      expect(sortedCountries[0]).toEqual(dataCountriesCopy[2]);
    });

    it('should sort countries list alphabetically ascending if UK or DE countries are missing from the array of descending sorted countries', function () {
      const dataCountriesCopy = [...mockDataCountries];
      const sortedCountries = getSortedCountriesByCurrentLangCcui(
        mockDataCountries.slice(0, 2),
        'en'
      );
      expect(sortedCountries).toEqual(dataCountriesCopy.slice(0, 2).reverse());
    });

    it('should sort countries list alphabetically descending if UK or DE countries are missing from the array of ascending sorted countries', function () {
      const dataCountriesCopy = [...mockDataCountries];
      const sortedCountries = getSortedCountriesByCurrentLangCcui(
        mockDataCountries.slice(0, 2).reverse(),
        'en'
      );
      expect(sortedCountries).toEqual(dataCountriesCopy.slice(0, 2).reverse());
    });

    it('should keep same order sort for countries list if they are already sorted', function () {
      const dataCountriesCopy = [...mockDataCountries];
      const sortedCountries = getSortedCountriesByCurrentLangCcui(
        [mockDataCountries[0], mockDataCountries[0]],
        'en'
      );
      expect(sortedCountries).toEqual([dataCountriesCopy[0], dataCountriesCopy[0]]);
    });
  });

  describe('getMaxValueFromRoomStays Method', () => {
    it('should return the max value from room stays', function () {
      const mockData = [
        {
          roomStay: {
            adultsNumber: 1,
            childrenNumber: 4,
          } as RoomStay,
        } as ReservationById,
        {
          roomStay: {
            adultsNumber: 3,
            childrenNumber: 2,
          } as RoomStay,
        } as ReservationById,
      ];
      expect(getMaxValueFromRoomStays(mockData, 'adultsNumber')).toEqual(3);
      expect(getMaxValueFromRoomStays(mockData, 'childrenNumber')).toEqual(4);
    });

    it('should return 0 if an empty array is passed for room stays', function () {
      expect(getMaxValueFromRoomStays([], 'adultsNumber')).toEqual(0);
      expect(getMaxValueFromRoomStays([], 'childrenNumber')).toEqual(0);
    });
  });

  describe('getMaxValueFromRoomStaysBC Method', () => {
    it('should return the max value from room stays', function () {
      const mockData = [
        {
          roomStay: {
            adultsNumber: 1,
            childrenNumber: 4,
          } as BCRoomStay,
        } as BCReservationListItem,
        {
          roomStay: {
            adultsNumber: 3,
            childrenNumber: 2,
          } as BCRoomStay,
        } as BCReservationListItem,
      ];
      expect(getMaxValueFromRoomStaysBC(mockData, 'adultsNumber')).toEqual(3);
      expect(getMaxValueFromRoomStaysBC(mockData, 'childrenNumber')).toEqual(4);
    });

    it('should return 0 if an empty array is passed for room stays', function () {
      expect(getMaxValueFromRoomStaysBC([], 'adultsNumber')).toEqual(0);
      expect(getMaxValueFromRoomStaysBC([], 'childrenNumber')).toEqual(0);
    });
  });

  describe('getMaxValueFromHDPRoomTypes Method', () => {
    it('should return the max value from HDP room stays', function () {
      const mockData = [
        {
          roomType: 'FAM',
          adults: 2,
          children: 1,
          cotRequested: false,
          rooms: [
            {
              pmsRoomType: 'FAMTRPL',
              silentSubstitution: false,
              roomClass: 'ST',
              cotAvailable: false,
              roomPriceBreakdown: {
                totalNetAmount: 200,
                currencyCode: 'GBP',
                dailyPrices: [
                  {
                    date: '2023-05-14',
                    netPrice: 200,
                  },
                ],
              },
              specialRequests: [],
            },
          ],
        } as HIRoomType,
        {
          roomType: 'FAM',
          adults: 2,
          children: 2,
          cotRequested: true,
          rooms: [
            {
              pmsRoomType: 'FAMTRPL',
              silentSubstitution: false,
              roomClass: 'ST',
              cotAvailable: false,
              roomPriceBreakdown: {
                totalNetAmount: 300,
                currencyCode: 'GBP',
                dailyPrices: [
                  {
                    date: '2023-05-14',
                    netPrice: 200,
                  },
                ],
              },
              specialRequests: [],
            },
          ],
        } as HIRoomType,
      ];
      expect(getMaxValueFromHDPRoomTypes(mockData, 'adults')).toEqual(2);
      expect(getMaxValueFromHDPRoomTypes(mockData, 'children')).toEqual(2);
    });

    it('should return 0 if an empty array is passed for room stays', function () {
      expect(getMaxValueFromRoomStays([], 'adultsNumber')).toEqual(0);
      expect(getMaxValueFromRoomStays([], 'childrenNumber')).toEqual(0);
    });
  });

  describe('getTotalPeople Method', () => {
    it('should return the correct number of adults and children', function () {
      expect(
        getTotalPeople([
          { adults: 2, children: 1, roomType: 'FAM', shouldIncludeCot: false },
          { adults: 1, children: 0, roomType: 'SB', shouldIncludeCot: false },
        ])
      ).toEqual({ adults: 3, children: 1 });
    });

    it('should return 0 adults and 0 children if no rooms array is passed', function () {
      expect(getTotalPeople([])).toEqual({ adults: 0, children: 0 });
    });
  });

  describe('getPlaceholderString Method', () => {
    it('should return 1 adult, 1 room', function () {
      expect(getPlaceholderString({ adults: 1, children: 0, rooms: 1 }, summaryLabels)).toEqual(
        '1 adult, 1 room'
      );
    });

    it('should return 1 adult, 2 rooms', function () {
      expect(getPlaceholderString({ adults: 1, children: 0, rooms: 2 }, summaryLabels)).toEqual(
        '1 adult, 2 rooms'
      );
    });

    it('should return 2 adults, 1 room', function () {
      expect(getPlaceholderString({ adults: 2, children: 0, rooms: 1 }, summaryLabels)).toEqual(
        '2 adults, 1 room'
      );
    });

    it('should return 2 adults, 2 rooms', function () {
      expect(getPlaceholderString({ adults: 2, children: 0, rooms: 2 }, summaryLabels)).toEqual(
        '2 adults, 2 rooms'
      );
    });

    it('should return 1 adult, 1 child, 1 room', function () {
      expect(getPlaceholderString({ adults: 1, children: 1, rooms: 1 }, summaryLabels)).toEqual(
        '1 adult, 1 child, 1 room'
      );
    });

    it('should return 1 adult, 1 child, 2 rooms', function () {
      expect(getPlaceholderString({ adults: 1, children: 1, rooms: 2 }, summaryLabels)).toEqual(
        '1 adult, 1 child, 2 rooms'
      );
    });

    it('should return 1 adult, 2 children, 1 room', function () {
      expect(getPlaceholderString({ adults: 1, children: 2, rooms: 1 }, summaryLabels)).toEqual(
        '1 adult, 2 children, 1 room'
      );
    });

    it('should return 1 adult, 2 children, 2 rooms', function () {
      expect(getPlaceholderString({ adults: 1, children: 2, rooms: 2 }, summaryLabels)).toEqual(
        '1 adult, 2 children, 2 rooms'
      );
    });

    it('should return 2 adults, 1 child, 1 room', function () {
      expect(getPlaceholderString({ adults: 2, children: 1, rooms: 1 }, summaryLabels)).toEqual(
        '2 adults, 1 child, 1 room'
      );
    });

    it('should return 2 adults, 1 child, 2 rooms', function () {
      expect(getPlaceholderString({ adults: 2, children: 1, rooms: 2 }, summaryLabels)).toEqual(
        '2 adults, 1 child, 2 rooms'
      );
    });

    it('should return 2 adults, 2 children, 1 room', function () {
      expect(getPlaceholderString({ adults: 2, children: 2, rooms: 1 }, summaryLabels)).toEqual(
        '2 adults, 2 children, 1 room'
      );
    });

    it('should return 2 adults, 2 children, 2 rooms', function () {
      expect(getPlaceholderString({ adults: 2, children: 2, rooms: 2 }, summaryLabels)).toEqual(
        '2 adults, 2 children, 2 rooms'
      );
    });
  });

  describe('getRoomsPlaceholder Method', () => {
    it('should return the correct room summary for 2 adults 1 room', function () {
      expect(
        getRoomsPlaceholder(
          [{ adults: 2, children: 0, roomType: 'DB', shouldIncludeCot: false }],
          summaryLabels
        )
      ).toEqual('2 adults, 1 room');
    });

    it('should return the correct room summary for 3 adults, 1 child, 2 rooms', function () {
      expect(
        getRoomsPlaceholder(
          [
            { adults: 2, children: 1, roomType: 'FAM', shouldIncludeCot: false },
            { adults: 1, children: 0, roomType: 'SB', shouldIncludeCot: false },
          ],
          summaryLabels
        )
      ).toEqual('3 adults, 1 child, 2 rooms');
    });

    it('should return undefined if no rooms array is passed', function () {
      expect(getRoomsPlaceholder([], summaryLabels)).toEqual(undefined);
    });
  });

  describe('getSelectedDonationPackage Method', () => {
    it('should return correct donation package', function () {
      const donationPackages = [
        {
          code: 'ZCHRY3',
          currency: 'GBP',
          unitPrice: 5,
        },
        {
          code: 'ZCHRY4',
          currency: 'GBP',
          unitPrice: 3,
        },
        {
          code: 'ZCHRY5',
          currency: 'GBP',
          unitPrice: 1,
        },
      ];

      const selectedPackages = {
        packagesSelection: [
          {
            id: 'BFADCT',
            noOfSelections: 1,
          },
          {
            id: 'ZCHRY5',
            noOfSelections: 1,
          },
        ],
      };

      expect(getSelectedDonationPackage(donationPackages, selectedPackages)).toEqual({
        code: 'ZCHRY5',
        currency: 'GBP',
        unitPrice: 1,
      });
    });

    it('should return undefined if no donation package is found for the given room selection', function () {
      const donationPackages = [
        {
          code: 'ZCHRY3',
          currency: 'GBP',
          unitPrice: 5,
        },
        {
          code: 'ZCHRY4',
          currency: 'GBP',
          unitPrice: 3,
        },
        {
          code: 'ZCHRY5',
          currency: 'GBP',
          unitPrice: 1,
        },
      ];
      const selectedPackages = {
        packagesSelection: [],
      };
      expect(getSelectedDonationPackage(donationPackages, selectedPackages)).toEqual(undefined);
    });
  });

  describe('getIDVPassedStatus Method', () => {
    it('should return false if dpaPassed is false and dpaOverride is false', function () {
      const testDpaInfoParam = { dpaPassed: false, dpaOverride: false };
      expect(getIDVPassedStatus(testDpaInfoParam)).toEqual(false);
    });

    it('should return false if dpaPassed is true and dpaOverride is true', function () {
      const testDpaInfoParam = { dpaPassed: true, dpaOverride: true };
      expect(getIDVPassedStatus(testDpaInfoParam)).toEqual(false);
    });

    it('should return true if dpaPassed is true and dpaOverride is false', function () {
      const testDpaInfoParam = { dpaPassed: true, dpaOverride: false };
      expect(getIDVPassedStatus(testDpaInfoParam)).toEqual(true);
    });

    it('should return true if dpaPassed is false and dpaOverride is true', function () {
      const testDpaInfoParam = { dpaPassed: false, dpaOverride: true };
      expect(getIDVPassedStatus(testDpaInfoParam)).toEqual(true);
    });
  });

  describe('getBartRoomTypeLabels method', () => {
    it('should return bartRoomTypeLabels', function () {
      expect(getBartRoomTypeLabels(mockBartRoomTypeLabelsObj, mockUseTranslationFn)).toEqual({
        DB: 'Double Room',
        DIS: 'Double Room',
        DOUBLE: 'Double Room',
        FAM: 'Family Room',
        FMQUAD: 'Family Room',
        FMTRPL: 'Family Room',
        LOWDBL: 'Double Room',
        TB: 'Family Room',
        TBT: 'Twin - double bed and sofa bed',
        TWIN: 'Twin - double bed and sofa bed',
      });
    });
  });

  describe('getRoomTypeLabelsBySourceSystem method', () => {
    it('should return roomTypeLabelsBySourceSystem', function () {
      expect(
        getRoomTypeLabelsBySourceSystem(
          mockRoomTypeLabels,
          mockSourceSystemBart,
          mockUseTranslationFn
        )
      ).toEqual({
        DB: 'Double Room',
        DIS: 'Double Room',
        DOUBLE: 'Double Room',
        FAM: 'Family Room',
        FMQUAD: 'Family Room',
        FMTRPL: 'Family Room',
        LOWDBL: 'Double Room',
        TB: 'Family Room',
        TBT: 'Twin - double bed and sofa bed',
        TWIN: 'Twin - double bed and sofa bed',
      });
    });

    it('should return roomTypeLabelsBySourceSystem with no sourceSystem', function () {
      expect(
        getRoomTypeLabelsBySourceSystem(mockRoomTypeLabels, undefined, mockUseTranslationFn)
      ).toEqual({
        DIS: 'Accessible Room',
        DOUBLE: 'Double Room',
        EXTDBL: 'Standard Extra room',
        FAM: 'Family Room',
        FMQUAD: 'Family Room',
        FMTRPL: 'Family Room',
        LOWDBL: 'Accessible double bedroom with a lowered bath',
        TWIN: 'Twin Room',
        LOWTWN: 'Accessible twin bedroom with a lowered bath',
        PB: 'Standard Extra room',
        PPLDBL: 'Premier Plus Room',
        RB: 'Premier Plus Room',
        SB: 'Standard Room',
        SINGLE: 'Standard Room',
        TWINRM: 'Twin Room',
        WETDBL: 'Accessible double bedroom with level access shower room',
        WETTWN: 'Accessible twin bedroom with level access shower room',
        ZPLDBL: 'Double Room',
      });
    });
  });

  describe('getMealForCard method', () => {
    it('should return mealForCard', function () {
      expect(getMealForCard(mockMeal)).toEqual({
        title: 'Meal Deal',
        id: '17',
        noSelections: 1,
        price: 24.99,
      });
    });
  });

  describe('getRoomDetailsForCard', () => {
    it('should return roomDetailsForCard with no meals and no extras', () => {
      expect(
        getRoomDetailsForCard(mockReservationDetails as any, mockRoom, mockBookedRoomType)
      ).toEqual({
        adultMealDescription: [],
        childrenMealDescription: [],
        cot: false,
        extrasPackageRoom: {
          packagesList: [],
          priceEci: 0,
          priceLco: 0,
          priceWifi: 0,
          priceBOProsecco: 0,
        },
        leadGuestName: 'John Lewis',
        mealPrice: 0,
        noAdults: 1,
        noChildren: 0,
        noNights: 1,
        roomPrice: 135,
        roomType: 'Double Room',
      });
    });

    it('should return roomDetailsForCard with meals and extras', () => {
      expect(
        getRoomDetailsForCard(mockReservationDetails, mockRoomWithMeals, mockBookedRoomType)
      ).toEqual({
        adultMealDescription: [
          {
            id: '11',
            noSelections: 2,
            price: 21,
            title: 'Premier Inn Breakfast',
          },
        ],
        childrenMealDescription: [
          {
            id: '15',
            noSelections: 1,
            price: 0,
            title: 'Free Child Breakfast',
          },
        ],
        cot: false,
        extrasPackageRoom: {
          packagesList: ['FI24HR', 'HSCOU2', 'HSCKIN'],
          priceEci: 12,
          priceLco: 10,
          priceWifi: 5,
          priceBOProsecco: 0,
        },
        leadGuestName: 'John Lewis',
        mealPrice: 0,
        noAdults: 1,
        noChildren: 0,
        noNights: 1,
        roomPrice: 135,
        roomType: 'Double Room',
      });
    });
  });

  describe('getBookingDetailsForCard method', () => {
    it('should return bookingDetailsForCard', function () {
      const mockHotelName = 'Manchester Bury';
      const mockBookedBy = 'Boatyness Mcboatss';
      const mockGuestSurname = 'Mcboatss';

      expect(
        getBookingDetailsForCard(
          mockReservationDetails,
          mockHotelName,
          mockBookedBy,
          mockGuestSurname,
          mockRoomTypeLabels,
          mockUseTranslationFn
        )
      ).toEqual({
        arrivalDate: '2023-05-30',
        balanceOutstanding: '0',
        bookedBy: 'Boatyness Mcboatss',
        bookedFor: 'John Lewis',
        cancellationInfoResponse: {
          amendable: true,
          cancelable: true,
          ruleCompliant: true,
          aemLabelKey: '',
        },
        cardType: 'VI',
        currencyCode: 'GBP',
        dinnerAllowance: null,
        donationPkg: undefined,
        guestSurname: 'Mcboatss',
        hotelId: 'LONBRO',
        hotelName: 'Manchester Bury',
        newTotal: '135',
        noNights: 1,
        paymentOption: 'PAY_NOW',
        previousTotal: '135',
        rateType: 'Flex',
        roomDetails: [
          {
            adultMealDescription: [],
            childrenMealDescription: [],
            cot: false,
            extrasPackageRoom: {
              packagesList: [],
              priceEci: 0,
              priceLco: 0,
              priceWifi: 0,
              priceBOProsecco: 0,
            },
            leadGuestName: 'John Lewis',
            mealPrice: 0,
            noAdults: 1,
            noChildren: 0,
            noNights: 1,
            roomPrice: 135,
            roomType: 'Double Room',
          },
        ],
        shouldDisplayCityTaxMessage: false,
        totalCost: '135',
      });
    });

    it('should return bookingDetailsForCard with no packageCode', function () {
      const mockHotelName = 'Manchester Bury';
      const mockBookedBy = 'Boatyness Mcboatss';
      const mockGuestSurname = 'Mcboatss';

      expect(
        getBookingDetailsForCard(
          mockReservationDetailsNoPackageCode,
          mockHotelName,
          mockBookedBy,
          mockGuestSurname,
          mockRoomTypeLabels,
          mockUseTranslationFn
        )
      ).toEqual({
        arrivalDate: '2023-05-30',
        balanceOutstanding: '0',
        bookedBy: 'Boatyness Mcboatss',
        bookedFor: 'John Lewis',
        cancellationInfoResponse: {
          amendable: true,
          cancelable: true,
          ruleCompliant: true,
          aemLabelKey: '',
        },
        cardType: 'VI',
        currencyCode: 'GBP',
        dinnerAllowance: null,
        donationPkg: {
          code: '145',
          currency: 'GBP',
          unitPrice: 5,
        },
        guestSurname: 'Mcboatss',
        hotelId: 'LONBRO',
        hotelName: 'Manchester Bury',
        newTotal: '135',
        noNights: 1,
        paymentOption: 'PAY_NOW',
        previousTotal: '135',
        rateType: 'Flex',
        roomDetails: [
          {
            adultMealDescription: [],
            childrenMealDescription: [],
            cot: false,
            extrasPackageRoom: {
              packagesList: [],
              priceEci: 0,
              priceLco: 0,
              priceWifi: 0,
              priceBOProsecco: 0,
            },
            leadGuestName: 'John Lewis',
            mealPrice: 0,
            noAdults: 1,
            noChildren: 0,
            noNights: 1,
            roomPrice: 135,
            roomType: 'Double Room',
          },
        ],
        shouldDisplayCityTaxMessage: false,
        totalCost: '135',
      });
    });
  });

  describe('getObjRoomLabelForRoomCodes method', () => {
    it('should return objRoomLabelForRoomCodes', function () {
      expect(
        getObjRoomLabelForRoomCodes({
          roomLabel: 'Double Room',
          roomTypeCode: ['DOUBLE', 'ZPLDBL'],
        })
      ).toEqual({
        DOUBLE: 'Double Room',
        ZPLDBL: 'Double Room',
      });
    });
  });

  describe('getSessionStorageValuesForBookings method', () => {
    it('should return ', function () {
      expect(getSessionStorageValuesForBookings()).toEqual({});
    });
  });

  describe('getMealQuantity method', () => {
    it('should return the correct quantity for a valid mealId and roomId', () => {
      const mealId = 'meal1';
      const roomIndex = 0;
      const result = getMealQuantity(mockPackages, mealId, roomIndex);
      expect(result).toBe(2);
    });
    it('should return 0 for an invalid mealId', () => {
      const mealId = 'invalidMeal';
      const roomIndex = 0;
      const result = getMealQuantity(mockPackages, mealId, roomIndex);
      expect(result).toBe(0);
    });

    describe('extrasNamingCheck method', () => {
      it('should return ', function () {
        expect(extrasNamingCheck(mockTranslationFn, ExtrasId.EARLY_CHECK_IN)).toEqual(
          'ancillaries.extras.HSCKIN.name'
        );

        expect(extrasNamingCheck(mockTranslationFn, ExtrasId.LATE_CHECK_OUT)).toEqual(
          'ancillaries.extras.HSCOU2.name'
        );

        expect(extrasNamingCheck(mockTranslationFn, ExtrasId.ULTIMATE_WIFI)).toEqual(
          'ancillaries.extras.FI24HR.name'
        );
        expect(extrasNamingCheck(mockTranslationFn, ExtrasId.BOTTLE_OF_PROSECCO)).toEqual(
          'ancillaries.extras.DBPROS.name'
        );
      });
    });
  });

  describe('getExtrasPackagePrice method', () => {
    it('should return the correct price for Early check-in', () => {
      const result = getExtrasPackagePrice('HSCKIN', 'GBP', mockPackagesExtrasItems);
      expect(result).toBe('£12.00');
    });

    it('should return the correct price for Late check-out', () => {
      const result = getExtrasPackagePrice('HSCOU2', 'GBP', mockPackagesExtrasItems);
      expect(result).toBe('£15.00');
    });

    it('should return null with no packages', () => {
      const result = getExtrasPackagePrice('HSCOU2', 'GBP', []);
      expect(result).toBeNull();
    });
  });

  describe('getCentrallyStoredCardBillingAddress Method', () => {
    it('should return the right billingAddress by the provided cardToken', function () {
      const cardToken = 'jH7e09b';
      const billingAddress = {
        line1: '120 Holborn',
        postCode: 'EC1N 2TD',
        countryCode: 'GB',
        type: 'BUSINESS',
        companyName: 'Whitbread',
      };
      const storedCards = [
        { cardToken: 'dss0dlk89', billingAddress: {} },
        { cardToken: 'jH7e09b', billingAddress: billingAddress },
      ];
      expect(getCentrallyStoredCardBillingAddress(cardToken, storedCards)).toEqual(billingAddress);
    });
  });

  describe('getTodayTomorrowDate Method', () => {
    beforeAll(() => {
      jest.useFakeTimers();
      jest.setSystemTime(new Date('2025-03-19T00:00:00Z'));
    });

    afterAll(() => {
      jest.useRealTimers();
    });

    it('should return the correct date params for today meta keyword ', () => {
      const result = getTodayTomorrowDate('today', undefined, undefined);
      expect(result).toEqual({ ARRdd: '19', ARRmm: '03', ARRyyyy: '2025' });
    });
    it('should return the correct date params for tomorrow meta keyword ', () => {
      const result = getTodayTomorrowDate('tomorrow', undefined, undefined);
      expect(result).toEqual({ ARRdd: '20', ARRmm: '03', ARRyyyy: '2025' });
    });
    it('should return the same date params if no meta keyword ', () => {
      const result = getTodayTomorrowDate('24', '03', '2025');
      expect(result).toEqual({ ARRdd: '24', ARRmm: '03', ARRyyyy: '2025' });
    });
  });
});

describe('getUserAndRoles', () => {
  const Wrapper = (props: any) => React.createElement('div', props, props.children);
  const Inner = (props: any) => React.createElement('div', props, props.children);

  it('should read direct user and roles', () => {
    const element = React.createElement(
      Wrapper,
      { user: 'directUser', roles: ['directRole1', 'directRole2'] },
      React.createElement(Inner, null)
    );

    const result = getUserAndRoles(element);

    expect(result).toEqual({
      user: 'directUser',
      roles: ['directRole1', 'directRole2'],
    });
  });

  it('should read nested user and roles', () => {
    const element = React.createElement(
      Wrapper,
      null,
      React.createElement(Inner, { user: 'nestedUser', roles: ['nestedRole1', 'nestedRole2'] })
    );

    const result = getUserAndRoles(element);

    expect(result).toEqual({
      user: 'nestedUser',
      roles: ['nestedRole1', 'nestedRole2'],
    });
  });

  it('nested should override direct', () => {
    const element = React.createElement(
      Wrapper,
      { user: 'directUser', roles: ['directRole1'] },
      React.createElement(Inner, { user: 'nestedUser', roles: ['nestedRole1', 'nestedRole2'] })
    );

    const result = getUserAndRoles(element);

    expect(result).toEqual({
      user: 'nestedUser',
      roles: ['nestedRole1', 'nestedRole2'],
    });
  });

  it('should read nested user/roles when children is an array', () => {
    const arrayChildren = [
      'text node',
      null,
      React.createElement(Inner, {
        user: 'arrayUser',
        roles: ['arrayRole1', 'arrayRole2'],
      }),
      123,
    ];

    const element = React.createElement(Wrapper, null, arrayChildren);

    const result = getUserAndRoles(element);

    expect(result).toEqual({
      user: 'arrayUser',
      roles: ['arrayRole1', 'arrayRole2'],
    });
  });
});
