import {
  HIRoomRate,
  HIRoomCode,
  HIBasketData,
  RoomTypeCodeMapped,
  ROOM_TYPE,
  HIRoomType,
  RoomClassCodes,
  RoomClass,
  DeRoomClass,
  PromoKind,
  GET_DASHBOARD_BASKET,
} from '@whitbread-eos/api';
import { add, format } from 'date-fns';

import type { PromotionsInformation } from '../getters';
import { MAX_ROOMS_SEARCH_LIMIT } from '../global-constants';
import { executeGraphQLQuery } from '../server';
import { staticHotelInformationIB, getHotelInformationIB } from '../server/getters/getters';
import {
  getAvailabilityParamsFromUrl,
  getStaticHotelInformationQueryDateDataFromUrl,
  getAvailabilityRoomClassIndexes,
  getAvailableRoomTypes,
  getHotelAvailabilityQueryKey,
  getRateClassification,
  getRoomClassByRoomClassCode,
  getRoomRatesThatMatchRoomClassifications,
  isHotelOpeningSoon,
  getSelectedPMSRoomTypesAndSpecialRequests,
  getTwinRoomSelectionPrices,
  getRoomTypesFromQuery,
  isFaqValidForRender,
  isAccessibleRoomType,
  getAccessibleRoomData,
  getPIBrandText,
  getRoomClassByCodeAndType,
  isPremierPlusAccessibleRoomByCode,
  isPremierPlusAccessibleRoomByText,
  getSelectedRoomClassCode,
  getPMSRoomTypeByRoomTypeAndBathroom,
  getBathRoomSelectedPMSRoomTypesAndSpecialRequests,
  getRoomClassTextForGAllery,
  filterRoomsByRoomClass,
  isItBarrierFree,
  createRoomTypeMapping,
  getAccessibleRoomSelectedPMSRoomTypesAndSpecialRequests,
  hasMatchingPromotionCode,
  getActivePromotionCode,
  getRoomRatesWithRateInformation,
  RoomClassItem,
  getDashboardBasket,
  getBrandForUnleashContext,
  extractPromoBoxData,
} from './hotel-details';

jest.mock('../server/getters/getters', () => ({
  staticHotelInformationIB: jest.fn(),
  getHotelInformationIB: jest.fn(),
}));

jest.mock('../server', () => ({
  executeGraphQLQuery: jest.fn(),
}));

/**Translate function */
const translateFn = (key: string) => {
  switch (key) {
    case 'accessible.double':
      return 'Accessible Double';
    case 'accessible.twin':
      return 'Accessible Twin';
    default:
      return key;
  }
};

const roomTypes = ['DIS'];
const unleashPremPlusAccFeatureFlag = true;
const roomRates = [
  {
    ratePlanCode: 'FLEXRATE',
    roomTypes: [
      {
        roomType: 'TWIN',
        adults: 2,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'WINCMB',
            silentSubstitution: false,
            roomClass: 'ST',
            cotAvailable: false,
            roomPriceBreakdown: {
              totalNetAmount: 115.33,
              currencyCode: 'GBP',
              dailyPrices: [
                {
                  date: '2022-08-13',
                  netPrice: 75.0,
                },
                {
                  date: '2022-08-14',
                  netPrice: 40.33,
                },
              ],
            },
            specialRequests: ['TWDS'],
          },
          {
            pmsRoomType: 'TWDoubleBed',
            silentSubstitution: false,
            roomClass: 'ST',
            cotAvailable: false,
            roomPriceBreakdown: {
              totalNetAmount: 115.33,
              currencyCode: 'GBP',
              dailyPrices: [
                {
                  date: '2022-08-13',
                  netPrice: 75.0,
                },
                {
                  date: '2022-08-14',
                  netPrice: 40.33,
                },
              ],
            },
            specialRequests: ['TWDS'],
          },
        ],
      },
      {
        roomType: 'FAM',
        adults: 2,
        children: 1,
        cotRequested: true,
        rooms: [
          {
            pmsRoomType: 'FMTHRE',
            silentSubstitution: false,
            roomClass: 'ST',
            cotAvailable: true,
            roomPriceBreakdown: {
              totalNetAmount: 115.33,
              currencyCode: 'GBP',
              dailyPrices: [
                {
                  date: '2022-08-13',
                  netPrice: 75.0,
                },
                {
                  date: '2022-08-14',
                  netPrice: 40.33,
                },
              ],
            },
            specialRequests: ['TRIP'],
          },
        ],
      },
    ],
  } as HIRoomRate,
  {
    ratePlanCode: 'STANDARD',
    roomTypes: [
      {
        roomType: 'TWIN',
        adults: 2,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'WINCMB',
            silentSubstitution: false,
            roomClass: 'ST',
            cotAvailable: false,
            roomPriceBreakdown: {
              totalNetAmount: 115.33,
              currencyCode: 'GBP',
              dailyPrices: [
                {
                  date: '2022-08-13',
                  netPrice: 75.0,
                },
                {
                  date: '2022-08-14',
                  netPrice: 40.33,
                },
              ],
            },
            specialRequests: ['TWDS'],
          },
          {
            pmsRoomType: 'TWDoubleBed',
            silentSubstitution: false,
            roomClass: 'ST',
            cotAvailable: false,
            roomPriceBreakdown: {
              totalNetAmount: 115.33,
              currencyCode: 'GBP',
              dailyPrices: [
                {
                  date: '2022-08-13',
                  netPrice: 75.0,
                },
                {
                  date: '2022-08-14',
                  netPrice: 40.33,
                },
              ],
            },
            specialRequests: ['TWDS'],
          },
        ],
      },
      {
        roomType: 'FAM',
        adults: 2,
        children: 1,
        cotRequested: true,
        rooms: [
          {
            pmsRoomType: 'FMTHRE',
            silentSubstitution: false,
            roomClass: 'ST',
            cotAvailable: true,
            roomPriceBreakdown: {
              totalNetAmount: 115.33,
              currencyCode: 'GBP',
              dailyPrices: [
                {
                  date: '2022-08-13',
                  netPrice: 75.0,
                },
                {
                  date: '2022-08-14',
                  netPrice: 40.33,
                },
              ],
            },
            specialRequests: ['TRIP'],
          },
        ],
      },
    ],
  } as HIRoomRate,
];

const rateClassifications = [
  {
    additionalDescription: '',
    rateClassification: 'FLEXRATE',
    rateDescription:
      'Pay now, fully refundable with free cancellation up to 1pm on the day of arrival',
    rateLongDescription: '',
    rateName: 'Flex Rate',
    rateNotes: '<p>Flex: Amend or cancel up to 1pm on arrival day</p>\n',
    rateOrder: '10',
    ratePlanCode: 'FLEXRATE',
    rateCategory: '',
  },
  {
    additionalDescription: '',
    rateClassification: '',
    rateDescription:
      'Pay now, fully refundable with free cancellation up to 3 full days before arrival',
    rateLongDescription: '',
    rateName: 'Semi-Flex Rate',
    rateNotes:
      '<p>Semi-Flex: Amend or cancel up to three full days before arrival date. Your arrival date can be amended up to 1pm on the day you’re due to arrive.</p>\n',
    rateOrder: '20',
    ratePlanCode: 'SEMIFLEX',
    rateCategory: '',
  },
];

const mockBasketDetailsStateWithTwoRooms = {
  hotelId: 'MANOLD',
  arrival: '2023-07-01',
  departure: '2023-07-04',
  numberOfUnits: 2,
  numberOfNights: 3,
  selectedRate: {
    ratePlanCode: 'FLEXRATE',
    cellCode: null,
    rateCategory: 'F',
    roomTypes: [
      {
        roomType: 'DB' as HIRoomCode,
        adults: 1,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'DOUBLE',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['DBLE'],
            roomPriceBreakdown: {
              totalNetAmount: 2997,
              currencyCode: 'GBP',
              dailyPrices: [
                { date: '2023-07-01', netPrice: 999 },
                { date: '2023-07-02', netPrice: 999 },
                { date: '2023-07-03', netPrice: 999 },
              ],
            },
          },
        ],
      },
      {
        roomType: 'TWIN' as HIRoomCode,
        adults: 1,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'TWINRM',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['TW2S'],
            roomPriceBreakdown: {
              totalNetAmount: 2997,
              currencyCode: 'GBP',
              dailyPrices: [
                { date: '2023-07-01', netPrice: 999 },
                { date: '2023-07-02', netPrice: 999 },
                { date: '2023-07-03', netPrice: 999 },
              ],
            },
          },
          {
            pmsRoomType: 'FMTRPL',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['TWDS'],
            roomPriceBreakdown: {
              totalNetAmount: 2992,
              currencyCode: 'GBP',
              dailyPrices: [
                { date: '2023-07-01', netPrice: 999 },
                { date: '2023-07-02', netPrice: 999 },
                { date: '2023-07-03', netPrice: 999 },
              ],
            },
          },
        ],
      },
    ],
  },
  roomClass: 'Standard Room',
  rateName: 'Flex Rate',
  roomTypeInformationResponse: {
    isLoadingRoomTypeInformation: false,
    isErrorRoomTypeInformation: false,
    dataRoomTypeInformation: {
      roomTypeInformation: {
        roomTypes: [
          {
            roomTypeCode: ['LOWTWN'],
            roomCategory: 'Accessible room',
            roomLabel: 'Accessible twin bedroom with a lowered bath',
            roomDescription:
              'Accessible twin bedroom. Bathroom with a lowered bath set at standard wheelchair height (480mm), lever taps, wider doors and bath mats available on request.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
            groupId: 'accessible',
          },
          {
            roomTypeCode: ['WETTWN', 'BRFTWN'],
            roomCategory: 'Accessible room',
            roomLabel: 'Accessible twin bedroom with level access shower room',
            roomDescription:
              'Accessible twin bedroom. Level access shower room with high-powered shower, conveniently placed shower controls, folding seat and wider doors.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
            groupId: 'accessible',
          },
          {
            roomTypeCode: ['WETDBL', 'BRFDBL', 'BRFZPL'],
            roomCategory: 'Accessible room',
            roomLabel: 'Accessible double bedroom with level access shower room',
            roomDescription:
              'Accessible double bedroom. Level access shower room with high-powered shower, conveniently placed shower controls, folding seat and wider doors.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
            groupId: 'accessible',
          },
          {
            roomTypeCode: ['LOWDBL', 'PPDLOW'],
            roomCategory: 'Accessible room',
            roomLabel: 'Accessible double bedroom with a lowered bath',
            roomDescription:
              'Accessible double bedroom. Bathroom with a lowered bath set at standard wheelchair height (480mm), lever taps, wider doors and bath mats available on request.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
            groupId: 'accessible',
          },
          {
            roomTypeCode: ['FMQUAD', 'FMFOUR'],
            roomCategory: 'Family',
            roomLabel: 'Family Room',
            roomDescription:
              'Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg',
            groupId: 'family',
          },
          {
            roomTypeCode: ['TWINRM', 'DBLDBL'],
            roomCategory: 'Twin',
            roomLabel: 'Twin Room',
            roomDescription:
              'Our twin rooms layouts differ between hotels. Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed. Some hotels have twin rooms with two double or kingsize Hypnos beds.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-twin-room.jpg',
            groupId: 'twin',
          },
          {
            roomTypeCode: ['SINGLE'],
            roomCategory: 'Standard',
            roomLabel: 'Standard Room',
            roomDescription:
              'A super-comfy bed, a power shower and free Wi-Fi – our Standard rooms have everything you’ll need for a great night’s sleep.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4%201.jpg',
            groupId: 'single',
          },
          {
            roomTypeCode: ['PPLDBL', 'PDBZPL', 'PFAMIL'],
            roomCategory: 'Premier Plus',
            roomLabel: 'Premier Plus Room',
            roomDescription:
              'Our enhanced room design. Includes Ultimate Wi-Fi, Nespresso machine, mini-fridge, bedside USB ports, iron, upgraded workspace & more.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
            groupId: 'double',
          },
          {
            roomTypeCode: ['FMTRPL', 'FMTHRE'],
            roomCategory: 'Family',
            roomLabel: 'Family Room',
            roomDescription:
              'Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg',
            groupId: 'family',
          },
          {
            roomTypeCode: ['DOUBLE', 'ZPLDBL'],
            roomCategory: 'Double',
            roomLabel: 'Double Room',
            roomDescription:
              'A super-comfy bed, a power shower and free Wi-Fi – our double rooms have everything you’ll need for a great night’s sleep.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
            groupId: 'double',
          },
        ],
      },
    },
    errorRoomTypeInformation: null,
  },
  bookingFlow: {
    bookingFlowItems: [
      { bookingId: 'booking-a1', rateCode: 'A' },
      { bookingId: 'booking-a1', rateCode: 'F' },
      { bookingId: 'booking-a1', rateCode: 'Q' },
      { bookingId: 'booking-spf', rateCode: 'G' },
      { bookingId: 'booking-spf', rateCode: 'D' },
      { bookingId: 'booking-spf', rateCode: 'E' },
      { bookingId: 'booking-a1', rateCode: 'R' },
      { bookingId: 'booking-a1', rateCode: 'S' },
      { bookingId: 'booking-a1', rateCode: 'FLEXRATE' },
      { bookingId: 'booking-a1', rateCode: 'STANDARD' },
      { bookingId: 'booking-a1', rateCode: 'SEMIFLEX' },
      { bookingId: 'booking-a1', rateCode: 'NONFLEX' },
      { bookingId: 'booking-a1', rateCode: 'ADVANCE' },
    ],
  },
  phoneNumber: '0333 321 1315',
  brand: 'PI',
} as HIBasketData;

describe('createRoomTypeMapping', () => {
  it('should create the correct room type mapping', () => {
    const mockT = (key: string) => {
      const translations: Record<string, string> = {
        'accessible.double': 'Accessible Double',
        'accessible.twin': 'Accessible Twin',
      };
      return translations[key] || key;
    };

    const mapping = createRoomTypeMapping(mockT);

    expect(mapping).toEqual({
      'Accessible Double': {
        wet: 'WETDBL',
        lowered: 'LOWDBL',
        premierPlus: {
          lowered: 'PPDLOW',
          wet: 'PPDWET',
        },
      },
      'Accessible Twin': {
        lowered: 'LOWTWN',
        wet: 'WETTWN',
      },
    });
  });
});

describe('hotel-details getters', () => {
  describe('getAvailabilityParamsFromUrl Method', () => {
    it('should return the arrival, departure and rooms data from a given hotel slug url', function () {
      const testUrl =
        'hotels/england/greater-london/london/hub-london-tower-bridge.html?&ARRdd=24&ARRmm=08&ARRyyyy=2022&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=HUB&CORPID=2153';
      const expectedOutput = {
        arrival: '2022-08-24',
        departure: '2022-08-25',
        numberOfNights: 1,
        rooms: [
          {
            adultsNumber: 1,
            childrenNumber: 0,
            roomType: 'DB',
            cotRequired: false,
          },
        ],
        empOfferCodes: [],
        corpId: '2153',
        promoId: null,
      };
      expect(getAvailabilityParamsFromUrl(testUrl)).toEqual(expectedOutput);
    });

    it('should return the arrival date as the current date if the given date params are not valid', function () {
      const testUrl =
        'hotels/england/greater-london/london/hub-london-tower-bridge.html?&ARRdd=24&ARRmm=0&ARRyyyy=2022&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=HUB&CORPID=2153';
      const expectedArrivalDate = format(new Date(), 'yyyy-MM-dd');
      const expectedDepartureDate = format(add(new Date(), { days: 1 }), 'yyyy-MM-dd');
      const expectedOutput = {
        arrival: expectedArrivalDate,
        departure: expectedDepartureDate,
        numberOfNights: 1,
        rooms: [
          {
            adultsNumber: 1,
            childrenNumber: 0,
            roomType: 'DB',
            cotRequired: false,
          },
        ],
        empOfferCodes: [],
        corpId: '2153',
        promoId: null,
      };
      expect(getAvailabilityParamsFromUrl(testUrl)).toEqual(expectedOutput);
    });

    it('should return an empty array of rooms if the user did not select the rooms number', function () {
      const testUrl =
        'hotels/england/greater-london/london/hub-london-tower-bridge.html?&ARRdd=24&ARRmm=08&ARRyyyy=2022&NIGHTS=1&ROOMS=0&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=HUB&CORPID=2153';
      const expectedOutput = {
        arrival: '2022-08-24',
        departure: '2022-08-25',
        numberOfNights: 1,
        rooms: [],
        empOfferCodes: [],
        corpId: '2153',
        promoId: null,
      };
      expect(getAvailabilityParamsFromUrl(testUrl)).toEqual(expectedOutput);
    });

    it('should return cellcode in an array when cellcode is present in the query', function () {
      const testUrl =
        'hotels/england/greater-london/london/hub-london-tower-bridge.html?&ARRdd=24&ARRmm=08&ARRyyyy=2022&NIGHTS=1&ROOMS=0&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=HUB&CELLCODES=EMP01';
      const expectedOutput = {
        arrival: '2022-08-24',
        departure: '2022-08-25',
        numberOfNights: 1,
        rooms: [],
        empOfferCodes: ['EMP01'],
        corpId: null,
        promoId: null,
      };
      expect(getAvailabilityParamsFromUrl(testUrl)).toEqual(expectedOutput);
    });

    it('should return empty string for the roomType field while isNoRoomTypeSearch is enabled', function () {
      const testUrl =
        'hotels/england/greater-london/london/hub-london-tower-bridge.html?&ARRdd=24&ARRmm=08&ARRyyyy=2022&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=HUB&CORPID=2153';
      const isNoRoomTypeSearch = true;
      const expectedOutput = {
        arrival: '2022-08-24',
        departure: '2022-08-25',
        numberOfNights: 1,
        rooms: [
          {
            adultsNumber: 1,
            childrenNumber: 0,
            roomType: '',
            cotRequired: false,
          },
        ],
        empOfferCodes: [],
        corpId: '2153',
        promoId: null,
      };
      expect(getAvailabilityParamsFromUrl(testUrl, isNoRoomTypeSearch)).toEqual(expectedOutput);
    });

    it('should cap the rooms loop instead of hanging when ROOMS is an absurdly large crafted value', () => {
      const testUrl =
        'hotels/england/greater-london/london/hub-london-tower-bridge.html?ARRdd=24&ARRmm=08&ARRyyyy=2022&NIGHTS=1&ROOMS=833406678&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB';

      // Production passes only the query string - see data.{pi,bb,ccui}.ts, which
      // call this with `req.url!.split('?')[1]`.
      expect(getAvailabilityParamsFromUrl(testUrl.split('?')[1]).rooms).toHaveLength(
        MAX_ROOMS_SEARCH_LIMIT
      );
    });
  });

  describe('getStaticHotelInformationQueryDateDataFromUrl Method', () => {
    it('should return static hotel information key and payload stay dates for a valid arrival date and nights', () => {
      const testUrl =
        'hotels/england/greater-london/london/hub-london-tower-bridge.html?ARRdd=24&ARRmm=08&ARRyyyy=2022&NIGHTS=2';

      expect(getStaticHotelInformationQueryDateDataFromUrl(testUrl)).toEqual({
        staticHotelInformationQueryKeyDates: ['2022-08-24', '2022-08-26'],
        staticHotelInformationQueryPayloadDates: {
          stayStartDate: '2022-08-24',
          stayEndDate: '2022-08-26',
        },
      });
    });

    it('should return empty key and payload dates for an invalid arrival date', () => {
      const testUrl =
        'hotels/england/greater-london/london/hub-london-tower-bridge.html?ARRdd=24&ARRmm=0&ARRyyyy=2022&NIGHTS=2';

      expect(getStaticHotelInformationQueryDateDataFromUrl(testUrl)).toEqual({
        staticHotelInformationQueryKeyDates: [],
        staticHotelInformationQueryPayloadDates: {},
      });
    });

    it('should return empty key and payload dates when nights are missing', () => {
      const testUrl =
        'hotels/england/greater-london/london/hub-london-tower-bridge.html?ARRdd=24&ARRmm=08&ARRyyyy=2022';

      expect(getStaticHotelInformationQueryDateDataFromUrl(testUrl)).toEqual({
        staticHotelInformationQueryKeyDates: [],
        staticHotelInformationQueryPayloadDates: {},
      });
    });
  });

  describe('getHotelAvailabilityQueryKey Method', () => {
    it('should return the correct availability query keys based on english params', function () {
      const expectedOutput = [
        'hotelAvailability',
        'en',
        'gb',
        'pi',
        'MANOLD',
        '2023-05-15',
        '2023-05-18',
        '{"roomType":"DB"}',
        '[]',
      ];
      expect(
        getHotelAvailabilityQueryKey(
          'en',
          'gb',
          'PI',
          'MANOLD',
          '2023-05-15',
          '2023-05-18',
          {
            roomType: 'DB',
          },
          undefined,
          ''
        )
      ).toEqual(expectedOutput);
    });

    it('should return the correct empoffer query keys', function () {
      const expectedOutput = [
        'hotelAvailability',
        'en',
        'gb',
        'pi',
        'MANOLD',
        '2023-05-15',
        '2023-05-18',
        '{"roomType":"DB"}',
        '["EMP01"]',
      ];
      expect(
        getHotelAvailabilityQueryKey(
          'en',
          'gb',
          'PI',
          'MANOLD',
          '2023-05-15',
          '2023-05-18',
          {
            roomType: 'DB',
          },
          ['EMP01'],
          '',
          undefined
        )
      ).toEqual(expectedOutput);
    });

    it('should return the correct query keys with landingPagePromoCode and promoKind', function () {
      const expectedOutput = [
        'hotelAvailability',
        'en',
        'gb',
        'pi',
        'MANOLD',
        '2023-05-15',
        '2023-05-18',
        '{"roomType":"DB"}',
        '["EMP01"]',
        'PROMO10',
        PromoKind.LandingPage,
      ];
      expect(
        getHotelAvailabilityQueryKey(
          'en',
          'gb',
          'PI',
          'MANOLD',
          '2023-05-15',
          '2023-05-18',
          { roomType: 'DB' },
          ['EMP01'],
          'PROMO10',
          PromoKind.LandingPage
        )
      ).toEqual(expectedOutput);
    });

    it('should return the correct availability query keys based on german params', function () {
      const expectedOutput = [
        'hotelAvailability',
        'de',
        'de',
        'hub',
        'MANOLD',
        '2023-05-15',
        '2023-05-18',
        '{"roomType":"SB"}',
        '[]',
      ];
      expect(
        getHotelAvailabilityQueryKey(
          'de',
          'de',
          'HUB',
          'MANOLD',
          '2023-05-15',
          '2023-05-18',
          {
            roomType: 'SB',
          },
          undefined,
          ''
        )
      ).toEqual(expectedOutput);
    });

    it('should return the correct availability query keys based on soft bundles', function () {
      const expectedOutput = [
        'hotelAvailability',
        'de',
        'de',
        'hub',
        'MANOLD',
        '2023-05-15',
        '2023-05-18',
        '{"roomType":"SB"}',
        '[]',
        'rate',
      ];
      expect(
        getHotelAvailabilityQueryKey(
          'de',
          'de',
          'HUB',
          'MANOLD',
          '2023-05-15',
          '2023-05-18',
          {
            roomType: 'SB',
          },
          [],
          '',
          undefined,
          'rate'
        )
      ).toEqual(expectedOutput);
    });

    it('should getAvailableRoomTypes', function () {
      expect(getAvailableRoomTypes(undefined)).toEqual([]);
    });

    it('should getRoomClassByRoomClassCode', function () {
      const expectedOutput = 'Standard Room';
      expect(getRoomClassByRoomClassCode('ST', 'en')).toEqual(expectedOutput);
    });

    it('should getRoomClassByRoomClassCode DE', function () {
      const expectedOutput = 'Standard Zimmer';
      expect(getRoomClassByRoomClassCode('ST', 'de')).toEqual(expectedOutput);
    });

    it('should getRateClassification', function () {
      const expectedOutput = {
        additionalDescription: '',
        rateClassification: 'FLEXRATE',
        rateDescription:
          'Pay now, fully refundable with free cancellation up to 1pm on the day of arrival',
        rateLongDescription: '',
        rateName: 'Flex Rate',
        rateNotes: '<p>Flex: Amend or cancel up to 1pm on arrival day</p>\n',
        rateOrder: '10',
        ratePlanCode: 'FLEXRATE',
        rateCategory: '',
      };
      expect(getRateClassification('FLEXRATE', rateClassifications)).toEqual(expectedOutput);
      expect(getRateClassification('FLEXRATE', undefined)).toEqual(undefined);
    });

    it('should getRateClassification', function () {
      const expectedOutput = {
        additionalDescription: '',
        rateClassification: '',
        rateDescription:
          'Pay now, fully refundable with free cancellation up to 3 full days before arrival',
        rateLongDescription: '',
        rateName: 'Semi-Flex Rate',
        rateNotes:
          '<p>Semi-Flex: Amend or cancel up to three full days before arrival date. Your arrival date can be amended up to 1pm on the day you’re due to arrive.</p>\n',
        rateOrder: '20',
        ratePlanCode: 'SEMIFLEX',
        rateCategory: '',
      };
      expect(getRateClassification('SEMIFLEX', rateClassifications)).toEqual(expectedOutput);
    });

    it('should getRoomRatesThatMatchRoomClassifications', function () {
      const expectedOutput = [
        {
          ratePlanCode: 'FLEXRATE',
          roomTypes: [
            {
              roomType: 'TWIN',
              adults: 2,
              children: 0,
              cotRequested: false,
              rooms: [
                {
                  pmsRoomType: 'WINCMB',
                  silentSubstitution: false,
                  roomClass: 'ST',
                  cotAvailable: false,
                  roomPriceBreakdown: {
                    totalNetAmount: 115.33,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-08-13',
                        netPrice: 75.0,
                      },
                      {
                        date: '2022-08-14',
                        netPrice: 40.33,
                      },
                    ],
                  },
                  specialRequests: ['TWDS'],
                },
                {
                  pmsRoomType: 'TWDoubleBed',
                  silentSubstitution: false,
                  roomClass: 'ST',
                  cotAvailable: false,
                  roomPriceBreakdown: {
                    totalNetAmount: 115.33,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-08-13',
                        netPrice: 75.0,
                      },
                      {
                        date: '2022-08-14',
                        netPrice: 40.33,
                      },
                    ],
                  },
                  specialRequests: ['TWDS'],
                },
              ],
            },
            {
              roomType: 'FAM',
              adults: 2,
              children: 1,
              cotRequested: true,
              rooms: [
                {
                  pmsRoomType: 'FMTHRE',
                  silentSubstitution: false,
                  roomClass: 'ST',
                  cotAvailable: true,
                  roomPriceBreakdown: {
                    totalNetAmount: 115.33,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-08-13',
                        netPrice: 75.0,
                      },
                      {
                        date: '2022-08-14',
                        netPrice: 40.33,
                      },
                    ],
                  },
                  specialRequests: ['TRIP'],
                },
              ],
            },
          ],
        } as HIRoomRate,
        {
          ratePlanCode: 'STANDARD',
          roomTypes: [
            {
              roomType: 'TWIN',
              adults: 2,
              children: 0,
              cotRequested: false,
              rooms: [
                {
                  pmsRoomType: 'WINCMB',
                  silentSubstitution: false,
                  roomClass: 'ST',
                  cotAvailable: false,
                  roomPriceBreakdown: {
                    totalNetAmount: 115.33,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-08-13',
                        netPrice: 75.0,
                      },
                      {
                        date: '2022-08-14',
                        netPrice: 40.33,
                      },
                    ],
                  },
                  specialRequests: ['TWDS'],
                },
                {
                  pmsRoomType: 'TWDoubleBed',
                  silentSubstitution: false,
                  roomClass: 'ST',
                  cotAvailable: false,
                  roomPriceBreakdown: {
                    totalNetAmount: 115.33,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-08-13',
                        netPrice: 75.0,
                      },
                      {
                        date: '2022-08-14',
                        netPrice: 40.33,
                      },
                    ],
                  },
                  specialRequests: ['TWDS'],
                },
              ],
            },
            {
              roomType: 'FAM',
              adults: 2,
              children: 1,
              cotRequested: true,
              rooms: [
                {
                  pmsRoomType: 'FMTHRE',
                  silentSubstitution: false,
                  roomClass: 'ST',
                  cotAvailable: true,
                  roomPriceBreakdown: {
                    totalNetAmount: 115.33,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-08-13',
                        netPrice: 75.0,
                      },
                      {
                        date: '2022-08-14',
                        netPrice: 40.33,
                      },
                    ],
                  },
                  specialRequests: ['TRIP'],
                },
              ],
            },
          ],
        } as HIRoomRate,
      ];
      expect(getRoomRatesThatMatchRoomClassifications(rateClassifications, roomRates)).toEqual(
        expectedOutput
      );
      expect(getRoomRatesThatMatchRoomClassifications(rateClassifications, undefined)).toEqual([]);
    });

    it('should getAvailabilityRoomClassIndexes', function () {
      const expectedOutput = ['ST'];
      expect(getAvailabilityRoomClassIndexes(roomRates)).toEqual(expectedOutput);
      expect(getAvailabilityRoomClassIndexes(undefined)).toEqual(undefined);
    });

    it('should return the correct availability query keys with rateName', function () {
      const expectedOutput = [
        'hotelAvailability',
        'en',
        'gb',
        'pi',
        'MANOLD',
        '2023-05-15',
        '2023-05-18',
        '{"roomType":"DB"}',
        '[]',
        'Flex Rate',
      ];

      expect(
        getHotelAvailabilityQueryKey(
          'en',
          'gb',
          'PI',
          'MANOLD',
          '2023-05-15',
          '2023-05-18',
          {
            roomType: 'DB',
          },
          [],
          '',
          undefined,
          undefined,
          'Flex Rate'
        )
      ).toEqual(expectedOutput);
    });

    it('should return the correct availability query keys with rateName and roomClass', function () {
      const expectedOutput = [
        'hotelAvailability',
        'en',
        'gb',
        'pi',
        'MANOLD',
        '2023-05-15',
        '2023-05-18',
        '{"roomType":"DB"}',
        '[]',
        'Flex Rate',
        'Standard Room',
      ];

      expect(
        getHotelAvailabilityQueryKey(
          'en',
          'gb',
          'PI',
          'MANOLD',
          '2023-05-15',
          '2023-05-18',
          {
            roomType: 'DB',
          },
          [],
          '',
          undefined,
          undefined,
          'Flex Rate',
          'Standard Room'
        )
      ).toEqual(expectedOutput);
    });

    it('should return the correct availability query keys with rateName, roomClass and isPromoBox', function () {
      const expectedOutput = [
        'hotelAvailability',
        'en',
        'gb',
        'pi',
        'MANOLD',
        '2023-05-15',
        '2023-05-18',
        '{"roomType":"DB"}',
        '[]',
        'Flex Rate',
        'Standard Room',
        true,
      ];

      expect(
        getHotelAvailabilityQueryKey(
          'en',
          'gb',
          'PI',
          'MANOLD',
          '2023-05-15',
          '2023-05-18',
          {
            roomType: 'DB',
          },
          [],
          '',
          undefined,
          undefined,
          'Flex Rate',
          'Standard Room',
          true
        )
      ).toEqual(expectedOutput);
    });

    it('should return the correct availability query keys with all optional params', function () {
      const expectedOutput = [
        'hotelAvailability',
        'en',
        'gb',
        'pi',
        'MANOLD',
        '2023-05-15',
        '2023-05-18',
        '{"roomType":"DB"}',
        '["EMP01"]',
        'PROMO10',
        PromoKind.LandingPage,
        'bundle',
        'Flex Rate',
        'Standard Room',
        true,
      ];

      expect(
        getHotelAvailabilityQueryKey(
          'en',
          'gb',
          'PI',
          'MANOLD',
          '2023-05-15',
          '2023-05-18',
          { roomType: 'DB' },
          ['EMP01'],
          'PROMO10',
          PromoKind.LandingPage,
          'bundle',
          'Flex Rate',
          'Standard Room',
          true
        )
      ).toEqual(expectedOutput);
    });
    it('should append rateName, roomClass and isPromoBox when provided', () => {
      const result = getHotelAvailabilityQueryKey(
        'en',
        'gb',
        'PI',
        'MANOLD',
        '2023-05-15',
        '2023-05-18',
        { roomType: 'DB' },
        ['EMP01'],
        'PROMO10',
        'UNIQUE' as PromoKind,
        'bundle',
        'Flex Rate',
        'Standard Room',
        true
      );

      expect(result).toEqual([
        'hotelAvailability',
        'en',
        'gb',
        'pi',
        'MANOLD',
        '2023-05-15',
        '2023-05-18',
        '{"roomType":"DB"}',
        '["EMP01"]',
        'PROMO10',
        'UNIQUE',
        'bundle',
        'Flex Rate',
        'Standard Room',
        true,
      ]);
    });
  });

  describe('getHotelAvailabilityQueryKey - Soft Bundles', () => {
    const mockParams = {
      language: 'en',
      country: 'gb',
      brand: 'PI',
      hotelId: '12345',
      arrival: '2026-02-10',
      departure: '2026-02-12',
      rooms: [{ adults: 2, children: 0 }],
      empOfferCodes: ['CODE1', 'CODE2'],
      landingPagePromoCode: 'PROMO123',
    };

    it('should include softBundle in query key when provided', () => {
      const softBundle = 'bundle-abc-123';

      const result = getHotelAvailabilityQueryKey(
        mockParams.language,
        mockParams.country,
        mockParams.brand,
        mockParams.hotelId,
        mockParams.arrival,
        mockParams.departure,
        mockParams.rooms,
        mockParams.empOfferCodes,
        mockParams.landingPagePromoCode,
        softBundle
      );

      expect(result).toContain(softBundle);
      expect(result[result.length - 1]).toBe(softBundle);
    });

    it('should not include softBundle in query key when not provided', () => {
      const result = getHotelAvailabilityQueryKey(
        mockParams.language,
        mockParams.country,
        mockParams.brand,
        mockParams.hotelId,
        mockParams.arrival,
        mockParams.departure,
        mockParams.rooms,
        mockParams.empOfferCodes,
        mockParams.landingPagePromoCode
      );

      expect(result).not.toContain('bundle');
      expect(result[result.length - 1]).toBe(mockParams.landingPagePromoCode);
    });

    it('should include softBundle even when landingPagePromoCode is empty', () => {
      const softBundle = 'bundle-xyz';

      const result = getHotelAvailabilityQueryKey(
        mockParams.language,
        mockParams.country,
        mockParams.brand,
        mockParams.hotelId,
        mockParams.arrival,
        mockParams.departure,
        mockParams.rooms,
        mockParams.empOfferCodes,
        '',
        softBundle
      );

      expect(result).toContain(softBundle);
      expect(result[result.length - 1]).toBe(softBundle);
    });
  });

  describe('isHotelOpeningSoon Method', () => {
    it('should return the correct isHotelOpeningSoon value', function () {
      expect(isHotelOpeningSoon('2023-04-14', '2023-04-13')).toEqual(true);
      expect(isHotelOpeningSoon('', '2023-04-13')).toEqual(false);
      expect(isHotelOpeningSoon('2023-04-14', '')).toEqual(false);
      expect(isHotelOpeningSoon('2023-04-14', '2023-04-15')).toEqual(false);
      expect(isHotelOpeningSoon('14-04-2023', '13-04-2023')).toEqual(false);
    });
  });

  describe('getSelectedPMSRoomTypesAndSpecialRequests', () => {
    const mockUseLocalStorage = jest.fn();

    it('should return selectedPMSRoomTypesAndSpecialRequests when two rooms are selected (DOUBLE and TWIN)  with selected, twin - two single beds', () => {
      mockUseLocalStorage.mockReturnValue([mockBasketDetailsStateWithTwoRooms, jest.fn()]);
      const twinroomSelections = ['', 'twobeds'];
      const expectedResponse = {
        selectedPMSRoomTypes: ['DOUBLE', 'TWINRM'],
        selectedSpecialRequests: [['DBLE'], ['TW2S']],
      };
      const selectedPMSRoomTypesAndSpecialRequests = getSelectedPMSRoomTypesAndSpecialRequests(
        twinroomSelections,
        mockBasketDetailsStateWithTwoRooms
      );

      expect(selectedPMSRoomTypesAndSpecialRequests).toEqual(expectedResponse);
    });

    it('should return selectedPMSRoomTypesAndSpecialRequests when two rooms are selected (DOUBLE and TWIN) with selected, twin - double sofa', () => {
      mockUseLocalStorage.mockReturnValue([mockBasketDetailsStateWithTwoRooms, jest.fn()]);
      const twinroomSelections = ['', 'doublesofa'];
      const expectedResponse = {
        selectedPMSRoomTypes: ['DOUBLE', 'FMTRPL'],
        selectedSpecialRequests: [['DBLE'], ['TWDS']],
      };
      const selectedPMSRoomTypesAndSpecialRequests = getSelectedPMSRoomTypesAndSpecialRequests(
        twinroomSelections,
        mockBasketDetailsStateWithTwoRooms
      );
      expect(selectedPMSRoomTypesAndSpecialRequests).toEqual(expectedResponse);
    });
  });

  describe('getTwinRoomSelectionPrices', () => {
    const mockUseLocalStorage = jest.fn();

    it('should return getTwinRoomSelectionPrices when two rooms are selected (DOUBLE and TWIN) with selected, twin - double sofa', () => {
      mockUseLocalStorage.mockReturnValue([mockBasketDetailsStateWithTwoRooms, jest.fn()]);
      const expectedResponse = [
        {
          twinRoomType: 'TW2S',
          currencyCode: 'GBP',
          price: 2997,
        },
        { twinRoomType: 'TWDS', currencyCode: 'GBP', price: 2992 },
      ];
      const twinRoomPrices = getTwinRoomSelectionPrices(
        mockBasketDetailsStateWithTwoRooms?.selectedRate
      );
      expect(twinRoomPrices).toEqual(expectedResponse);
    });
  });
});

describe('getRoomTypesFromQuery', () => {
  const mapping: RoomTypeCodeMapped = {
    DIS: 'Accessible',
    DB: 'Double',
    FAM: 'Family',
    SB: 'Single',
    TWIN: 'Twin',
  };
  const roomLabel = 'room';

  it('should returns an array of room types based on the query parameters', () => {
    const routerQuery = {
      ROOMS: '3',
      INTTYP1: 'DB',
      INTTYP2: 'DIS',
      INTTYP3: 'SB',
    };

    const result = getRoomTypesFromQuery(routerQuery, mapping, roomLabel);
    expect(result).toEqual(['Double room', 'Accessible room', 'Single room']);
  });

  it('should handles non-numeric ROOMS value by defaulting to 1', () => {
    const routerQuery = {
      ROOMS: 'invalid', // non-numeric value
      INTTYP1: 'DB',
    };

    const result = getRoomTypesFromQuery(routerQuery, mapping, roomLabel);
    expect(result).toEqual(['Double room']);
  });

  it('should cap the loop instead of hanging when ROOMS is an absurdly large crafted value', () => {
    const routerQuery = {
      ROOMS: '833406678',
      INTTYP1: 'DB',
    };

    const result = getRoomTypesFromQuery(routerQuery, mapping, roomLabel);
    expect(result).toHaveLength(MAX_ROOMS_SEARCH_LIMIT);
  });

  it('should bound the loop by the AEM maxRooms when the caller supplies it', () => {
    const routerQuery = {
      ROOMS: '833406678',
      INTTYP1: 'DB',
    };

    // CCUI's configured maximum - the real limit wins over the safety ceiling.
    const result = getRoomTypesFromQuery(routerQuery, mapping, roomLabel, 9);
    expect(result).toHaveLength(9);
  });
});

describe('isFaqValidForRender Method', () => {
  it('should return the correct isFaqValidForRender value', function () {
    expect(isFaqValidForRender({}, false)).toEqual(false);
    expect(isFaqValidForRender({}, true)).toEqual(false);
    expect(isFaqValidForRender(null, false)).toEqual(null);
    expect(isFaqValidForRender(null, true)).toEqual(null);
    const mockFaqData = {
      title: '',
      faqItems: [],
    };
    expect(isFaqValidForRender(mockFaqData, false)).toEqual(false);
    expect(isFaqValidForRender(mockFaqData, true)).toEqual(false);
    const mockFaqData1 = {
      faqItems: [
        {
          answer:
            '<p>Early check-in is available from 11am for an additional £10, and this will be payable at reception on arrival. Early check-in is subject to availability and not available at all Premier Inn hotels. &nbsp;</p>\r\n',
          question: 'Can I check in early? ',
        },
      ],
      title: 'London Kings Cross FAQs',
    };
    expect(isFaqValidForRender(mockFaqData1, false)).toEqual(false);
    expect(isFaqValidForRender(mockFaqData1, true)).toEqual(true);
  });
});

describe('getAccessibleRoomData Method', () => {
  it('should return only accessible rooms', () => {
    const roomTypes = [
      {
        isRoomAccessible: true,
        roomType: 'DB',
        rooms: [
          { pmsRoomType: 'PPLDBL', roomClass: 'PP' },
          { pmsRoomType: 'WETDBL', roomClass: 'ST' },
        ],
      },
    ] as HIRoomType[];

    const expectedOutput = [
      {
        isRoomAccessible: true,
        roomType: 'DB',
        rooms: [{ pmsRoomType: 'WETDBL', roomClass: 'ST' }],
      },
    ];

    expect(getAccessibleRoomData(roomTypes)).toEqual(expectedOutput);
  });

  it('should return rooms in availability when there are no accessible rooms and isRoomAccessible is false', () => {
    const roomTypes = [
      {
        roomType: 'DB',
        rooms: [
          { pmsRoomType: 'WETDBL', roomClass: 'ST' },
          { pmsRoomType: 'BRFZPL', roomClass: 'ST' },
        ],
      },
    ] as HIRoomType[];

    const expectedOutput = [
      {
        isRoomAccessible: true,
        roomType: 'DB',
        rooms: [
          { pmsRoomType: 'WETDBL', roomClass: 'ST' },
          { pmsRoomType: 'BRFZPL', roomClass: 'ST' },
        ],
      },
    ];

    expect(getAccessibleRoomData(roomTypes, true)).toEqual(expectedOutput);
  });

  it('should return rooms in availability when there are no accessible rooms and isRoomAccessible is false and chooseRoomTypePage is true', () => {
    const roomTypes = [
      {
        isRoomAccessible: false,
        roomType: 'DB',
        rooms: [
          { pmsRoomType: 'PPLDBL', roomClass: 'PP' },
          { pmsRoomType: 'DOUBLE', roomClass: 'ST' },
        ],
      },
    ] as HIRoomType[];

    const expectedOutput = [
      {
        isRoomAccessible: false,
        roomType: 'DB',
        rooms: [
          { pmsRoomType: 'PPLDBL', roomClass: 'PP' },
          { pmsRoomType: 'DOUBLE', roomClass: 'ST' },
        ],
      },
    ];

    const chooseRoomTypePage = true;
    expect(getAccessibleRoomData(roomTypes, chooseRoomTypePage)).toEqual(expectedOutput);
  });
});

describe('isAccessibleRoomType', () => {
  it('should return true for accessible room types', () => {
    const accessibleRoomTypes = [ROOM_TYPE.WET_DOUBLE, ROOM_TYPE.LOWERED_TWIN];
    accessibleRoomTypes.forEach((roomType) => {
      expect(isAccessibleRoomType(roomType)).toBe(true);
    });
  });

  it('should return false for non-accessible room types', () => {
    const nonAccessibleRoomTypes = [ROOM_TYPE.DOUBLE, ROOM_TYPE.PREMIER_PLUS];
    nonAccessibleRoomTypes.forEach((roomType) => {
      expect(isAccessibleRoomType(roomType)).toBe(false);
    });
  });
});

describe('getPIBrandText', () => {
  const t = (s: string) => s;

  it('should return Premier Inn text for PI brand', () => {
    const brand = 'PI';
    const expectedOutput = ' search.new.premierInn ';
    expect(getPIBrandText(t, brand)).toEqual(expectedOutput);
  });

  it('should return Premier Inn text for PID brand', () => {
    const brand = 'PID';
    const expectedOutput = ' search.new.premierInn ';
    expect(getPIBrandText(t, brand)).toEqual(expectedOutput);
  });

  it('should return empty text for other brands', () => {
    const brand = 'HUB';
    const expectedOutput = ' ';
    expect(getPIBrandText(t, brand)).toEqual(expectedOutput);
  });
});

describe('getRoomClassByCodeAndType', () => {
  const t = (s: string) => s;
  it('should return Premier Plus Accessible room text for roomClassCode PP and roomType DIS', () => {
    const roomClass = '';
    const roomClassCode = RoomClassCodes.PREMIER_PLUS_ROOM;
    const roomType = 'DIS';
    const roomClassConfiguration: RoomClassItem[] = [
      {
        code: 'PP',
        title: 'Premier Plus room',
      },
    ];
    const pmsType = getRoomClassByCodeAndType(
      roomClass,
      roomClassCode,
      roomType,
      roomTypes as HIRoomCode[],
      t,
      unleashPremPlusAccFeatureFlag,
      roomClassConfiguration
    );
    expect(pmsType).toEqual(roomClassConfiguration[0].title);
  });

  it('should return Standard Accessible room text for roomClassCode PP and roomType DIS when feature flag is off', () => {
    const roomClass = '';
    const roomClassCode = RoomClassCodes.PREMIER_PLUS_ROOM;
    const roomType = 'DIS';
    const roomClassConfiguration: RoomClassItem[] = [
      {
        code: 'PP',
        title: 'Premier Plus room',
      },
    ];
    const pmsType = getRoomClassByCodeAndType(
      roomClass,
      roomClassCode,
      roomType,
      roomTypes as HIRoomCode[],
      t,
      false,
      roomClassConfiguration
    );
    expect(pmsType).toEqual(roomClassConfiguration[0].title);
  });

  it('should return Standard Accessible room text for roomClassCode ST and roomType DB', () => {
    const roomClass = RoomClass.ST;
    const roomClassCode = RoomClassCodes.STANDARD_ROOM;
    const roomType = 'DB';
    const roomClassConfiguration = [
      {
        code: 'ST',
        title: 'Standard room',
      },
    ];
    const pmsType = getRoomClassByCodeAndType(
      roomClass,
      roomClassCode,
      roomType,
      roomTypes as HIRoomCode[],
      t,
      unleashPremPlusAccFeatureFlag,
      roomClassConfiguration
    );
    expect(pmsType).toEqual(roomClassConfiguration[0].title);
  });

  it('should return Standard Accessible room text for roomClassCode empty and roomType DB', () => {
    const roomClass = RoomClass.ST;
    const roomClassCode = '';
    const roomType = 'DB';
    const roomClassConfiguration: RoomClassItem[] = [
      {
        code: 'ST',
        title: 'Standard Room',
      },
    ];
    const pmsType = getRoomClassByCodeAndType(
      roomClass,
      roomClassCode,
      roomType,
      roomTypes as HIRoomCode[],
      t,
      unleashPremPlusAccFeatureFlag,
      roomClassConfiguration
    );
    expect(pmsType).toEqual(roomClassConfiguration[0].title);
  });

  it('should return Standard Accessible room text for roomClassCode empty and roomType DB when feature flag is off', () => {
    const roomClass = RoomClass.ST;
    const roomClassCode = '';
    const roomType = 'DB';
    const roomClassConfiguration: RoomClassItem[] = [
      {
        code: 'ST',
        title: 'Standard Room',
      },
    ];
    const pmsType = getRoomClassByCodeAndType(
      roomClass,
      roomClassCode,
      roomType,
      roomTypes as HIRoomCode[],
      t,
      false,
      roomClassConfiguration
    );
    expect(pmsType).toEqual(roomClassConfiguration[0].title);
  });

  it('should return Standard room with city view text for roomClassCode SV and roomType FAM when feature flag is off', () => {
    const roomClass = 'Standard room with city view';
    const roomClassCode = 'SV';
    const roomType = 'FAM';
    const roomClassConfiguration: RoomClassItem[] = [
      {
        code: 'SV',
        title: 'Standard room with city view',
      },
    ];
    const pmsType = getRoomClassByCodeAndType(
      roomClass,
      roomClassCode,
      roomType,
      roomTypes as HIRoomCode[],
      t,
      false,
      roomClassConfiguration
    );
    expect(pmsType).toEqual(roomClassConfiguration[0].title);
  });

  it('should return Premier plus room with city view text for roomClassCode PV and roomType DB when feature flag is off', () => {
    const roomClass = 'Premier plus room with city view';
    const roomClassCode = 'PV';
    const roomType = 'DB';
    const roomClassConfiguration: RoomClassItem[] = [
      {
        code: 'PV',
        title: 'Premier plus room with city view',
      },
    ];
    const pmsType = getRoomClassByCodeAndType(
      roomClass,
      roomClassCode,
      roomType,
      roomTypes as HIRoomCode[],
      t,
      false,
      roomClassConfiguration
    );
    expect(pmsType).toEqual(roomClassConfiguration[0].title);
  });
});

describe('isPremierPlusAccessibleRoomByCode', () => {
  it('should return true for roomClassCode PP and roomType DIS', () => {
    const roomClassCode = RoomClassCodes.PREMIER_PLUS_ROOM;
    const roomType = 'DIS';
    const isPremierPlusAccessible = isPremierPlusAccessibleRoomByCode(
      roomClassCode,
      roomType,
      unleashPremPlusAccFeatureFlag
    );
    expect(isPremierPlusAccessible).toBe(true);
  });

  it('should return false for roomClassCode PP and roomType DIS when feature flag is off', () => {
    const roomClassCode = RoomClassCodes.PREMIER_PLUS_ROOM;
    const roomType = 'DIS';
    const isPremierPlusAccessible = isPremierPlusAccessibleRoomByCode(
      roomClassCode,
      roomType,
      false
    );
    expect(isPremierPlusAccessible).toBe(false);
  });

  it('should return false for roomClassCode ST and roomType DIS', () => {
    const roomClassCode = RoomClassCodes.STANDARD_ROOM;
    const roomType = 'DIS';
    const isPremierPlusAccessible = isPremierPlusAccessibleRoomByCode(
      roomClassCode,
      roomType,
      unleashPremPlusAccFeatureFlag
    );
    expect(isPremierPlusAccessible).toBe(false);
  });

  it('should return false for roomClassCode ST and roomType DIS whe feature flag is off', () => {
    const roomClassCode = RoomClassCodes.STANDARD_ROOM;
    const roomType = 'DIS';
    const isPremierPlusAccessible = isPremierPlusAccessibleRoomByCode(
      roomClassCode,
      roomType,
      false
    );
    expect(isPremierPlusAccessible).toBe(false);
  });
});

describe('isPremierPlusAccessibleRoomByText', () => {
  it('should return true for roomClass containing Premier Plus in English', () => {
    const roomClass = RoomClass.PP;
    const language = 'en';
    const result = isPremierPlusAccessibleRoomByText(roomClass, language);
    expect(result).toBe(true);
  });

  it('should return false for roomClass not containing Premier Plus in English', () => {
    const roomClass = RoomClass.ST;
    const language = 'en';
    const result = isPremierPlusAccessibleRoomByText(roomClass, language);
    expect(result).toBe(false);
  });

  it('should return true for roomClass containing translated Premier Plus in German', () => {
    const roomClass = DeRoomClass.PP;
    const language = 'de';
    const result = isPremierPlusAccessibleRoomByText(roomClass, language);
    expect(result).toBe(true);
  });

  it('should return false for roomClass not containing translated Premier Plus in German', () => {
    const roomClass = DeRoomClass.ST;
    const language = 'de';
    const result = isPremierPlusAccessibleRoomByText(roomClass, language);
    expect(result).toBe(false);
  });

  it('should return false for an empty roomClass in English', () => {
    const roomClass = '';
    const language = 'en';
    const result = isPremierPlusAccessibleRoomByText(roomClass, language);
    expect(result).toBe(false);
  });

  it('should return false for an empty roomClass in German', () => {
    const roomClass = '';
    const language = 'de';
    const result = isPremierPlusAccessibleRoomByText(roomClass, language);
    expect(result).toBe(false);
  });
});

describe('getSelectedRoomClassCode', () => {
  it('should return correct room class code for valid English room class name', () => {
    const roomClassName = RoomClass.PP;
    const language = 'en';
    const result = getSelectedRoomClassCode(roomClassName, language);
    expect(result).toBe('PP');
  });

  it('should return undefined for empty room class name in English', () => {
    const roomClassName = '';
    const language = 'en';
    const result = getSelectedRoomClassCode(roomClassName, language);
    expect(result).toBeUndefined();
  });

  it('should return undefined for empty room class name in German', () => {
    const roomClassName = '';
    const language = 'de';
    const result = getSelectedRoomClassCode(roomClassName, language);
    expect(result).toBeUndefined();
  });
});

describe('getPMSRoomTypeByRoomTypeAndBathroom', () => {
  it('should return the correct premier plus lowered for room type Accessible double and bathroom lowered and isPremierPlusAccessibleRoomSelected is true', () => {
    const roomTypeSelections = ['Accessible Double'];
    const bathroomSelections = [ROOM_TYPE.LOWERED];
    const accessibleRoomIndex = 0;
    const isPremierPlusAccessibleRoomSelected = true;

    const result = getPMSRoomTypeByRoomTypeAndBathroom(
      translateFn,
      true,
      accessibleRoomIndex,
      roomTypeSelections,
      bathroomSelections,
      isPremierPlusAccessibleRoomSelected
    );

    expect(result).toBe(ROOM_TYPE.PREMIER_PLUS_LOWERED_DOUBLE);
  });

  it('should return the correct bathroom premier plus wet for room type Accessible double and bathroom wet and isPremierPlusAccessibleRoomSelected is true', () => {
    const roomTypeSelections = ['Accessible Double'];
    const bathroomSelections = [ROOM_TYPE.WET];
    const accessibleRoomIndex = 0;
    const isPremierPlusAccessibleRoomSelected = true;
    const result = getPMSRoomTypeByRoomTypeAndBathroom(
      translateFn,
      true,
      accessibleRoomIndex,
      roomTypeSelections,
      bathroomSelections,
      isPremierPlusAccessibleRoomSelected
    );
    expect(result).toBe(ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE);
  });

  it('should return the correct bathroom wet for room type Accessible double and bathroom wet and isPremierPlusAccessibleRoomSelected is false', () => {
    const roomTypeSelections = ['Accessible Double'];
    const bathroomSelections = [ROOM_TYPE.WET];
    const accessibleRoomIndex = 0;
    const isPremierPlusAccessibleRoomSelected = false;
    const result = getPMSRoomTypeByRoomTypeAndBathroom(
      translateFn,
      true,
      accessibleRoomIndex,
      roomTypeSelections,
      bathroomSelections,
      isPremierPlusAccessibleRoomSelected
    );
    expect(result).toBe(ROOM_TYPE.WET_DOUBLE);
  });

  it('should return the correct bathroom wet for room type Accessible double and bathroom wet and isPremPlusAccFeatureFlag is false', () => {
    const roomTypeSelections = ['Accessible Double'];
    const bathroomSelections = [ROOM_TYPE.WET];
    const accessibleRoomIndex = 0;
    const isPremierPlusAccessibleRoomSelected = false;
    const result = getPMSRoomTypeByRoomTypeAndBathroom(
      translateFn,
      false,
      accessibleRoomIndex,
      roomTypeSelections,
      bathroomSelections,
      isPremierPlusAccessibleRoomSelected
    );
    expect(result).toBe(ROOM_TYPE.WET_DOUBLE);
  });

  it('should return the correct bathroom wet for room type Accessible double and bathroom wet and isPremPlusAccFeatureFlag is false and isPremierPlusAccessibleRoomSelected is true', () => {
    const roomTypeSelections = ['Accessible Double'];
    const bathroomSelections = [ROOM_TYPE.WET];
    const accessibleRoomIndex = 0;
    const isPremierPlusAccessibleRoomSelected = true;
    const result = getPMSRoomTypeByRoomTypeAndBathroom(
      translateFn,
      false,
      accessibleRoomIndex,
      roomTypeSelections,
      bathroomSelections,
      isPremierPlusAccessibleRoomSelected
    );
    expect(result).toBe(ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE);
  });
});

describe('getBathRoomSelectedPMSRoomTypesAndSpecialRequests - Non-Accessible Room', () => {
  it('should return the correct PMS room types and special requests for non-accessible room', () => {
    const roomTypeSelections = ['TWIN'];
    const bathroomSelections = ['LOWERED'];
    const basketDetailsState = {
      selectedRate: {
        roomTypes: [
          {
            roomType: 'TWIN',
            rooms: [
              {
                pmsRoomType: 'TWINRM',
                specialRequests: ['TWDS'],
              },
            ],
          },
        ],
      },
    };
    const language = 'en';

    const result = getBathRoomSelectedPMSRoomTypesAndSpecialRequests(
      translateFn,
      true,
      roomTypeSelections,
      bathroomSelections,
      basketDetailsState,
      language
    );

    expect(result.selectedPMSRoomTypes).toEqual(['TWINRM']);
    expect(result.selectedSpecialRequests).toEqual([['TWDS']]);
  });
});

describe('getBathRoomSelectedPMSRoomTypesAndSpecialRequests', () => {
  it('should exclude WETR special request for accessible room with lowered bathroom', () => {
    const roomTypeSelections = ['Accessible Double'];
    const bathroomSelections = ['LOWERED'];
    const basketDetailsState = {
      selectedRate: {
        roomTypes: [
          {
            roomType: 'DB',
            rooms: [
              {
                pmsRoomType: 'TWINRM',
                specialRequests: ['TWDS'],
              },
            ],
          },
        ],
      },
      roomClass: RoomClass.ST,
    };
    const language = 'en';

    const result = getBathRoomSelectedPMSRoomTypesAndSpecialRequests(
      translateFn,
      true,
      roomTypeSelections,
      bathroomSelections,
      basketDetailsState,
      language
    );

    expect(result.selectedPMSRoomTypes).toEqual(['TWINRM']);
  });

  it('should exclude WETR special request for prem plus accessible room with lowered bathroom', () => {
    const roomTypeSelections = ['Accessible Double'];
    const bathroomSelections = ['wet'];
    const basketDetailsState = {
      selectedRate: {
        roomTypes: [
          {
            roomType: 'DIS',
            rooms: [
              {
                pmsRoomType: 'PPDLOW',
                specialRequests: ['TWDS'],
              },
            ],
          },
        ],
      },
      roomClass: RoomClass.PP,
    };
    const language = 'en';

    const result = getBathRoomSelectedPMSRoomTypesAndSpecialRequests(
      translateFn,
      true,
      roomTypeSelections,
      bathroomSelections,
      basketDetailsState,
      language
    );

    expect(result.selectedPMSRoomTypes).toEqual(['PPDWET']);
  });
});

describe('getAccessibleRoomSelectedPMSRoomTypesAndSpecialRequests', () => {
  it('should exclude WETR special request for accessible room with lowered bathroom', () => {
    const roomTypeSelections = ['Accessible Double'];
    const accessibleRoomTypeSelections = ['str'];
    const basketDetailsState = {
      selectedRate: {
        roomTypes: [
          {
            roomType: 'DB',
            rooms: [
              {
                pmsRoomType: 'LOWDBL',
                specialRequests: ['LOWR'],
              },
            ],
          },
        ],
      },
      roomClass: RoomClass.ST,
    };

    const roomAvailability = [
      {
        availableCount: 7,
        code: 'BRFDBL',
      },
      {
        availableCount: 0,
        code: 'WETDBL',
      },
      {
        availableCount: 8,
        code: 'LOWDBL',
      },
    ];

    const result = getAccessibleRoomSelectedPMSRoomTypesAndSpecialRequests(
      roomTypeSelections,
      accessibleRoomTypeSelections,
      basketDetailsState,
      roomAvailability
    );

    expect(result.selectedPMSRoomTypes).toEqual(['LOWDBL']);
  });

  it('should exclude WETR special request for standard accessible room with wet bathroom', () => {
    const roomTypeSelections = ['Accessible Double'];
    const accessibleRoomTypeSelections = ['str'];
    const basketDetailsState = {
      selectedRate: {
        roomTypes: [
          {
            roomType: 'DIS',
            rooms: [
              {
                pmsRoomType: 'WETDBL',
                specialRequests: ['WETR'],
              },
            ],
          },
        ],
      },
      roomClass: RoomClass.ST,
    };

    const roomAvailability = [
      {
        availableCount: 7,
        code: 'BRFDBL',
      },
      {
        availableCount: 10,
        code: 'WETDBL',
      },
      {
        availableCount: 8,
        code: 'LOWDBL',
      },
    ];

    const result = getAccessibleRoomSelectedPMSRoomTypesAndSpecialRequests(
      roomTypeSelections,
      accessibleRoomTypeSelections,
      basketDetailsState,
      roomAvailability
    );

    expect(result.selectedPMSRoomTypes).toEqual(['WETDBL']);
  });
});

describe('getRoomClassTextForGAllery', () => {
  it('should return "premierPlusAccessible" when isPremierPlusAccessibleRoomByCode returns true', () => {
    const basketDetailsState = {
      roomClass: RoomClass.PP,
      selectedRate: {
        roomTypes: [{ roomType: 'DIS' }],
      },
    };

    const result = getRoomClassTextForGAllery(
      basketDetailsState,
      'en',
      unleashPremPlusAccFeatureFlag
    );
    expect(result).toBe('premierPlusAccessible');
  });

  it('should return "premierPlusAccessible" when isPremierPlusAccessibleRoomByCode returns true and language is german', () => {
    const basketDetailsState = {
      roomClass: DeRoomClass.PP,
      selectedRate: {
        roomTypes: [{ roomType: 'DIS' }],
      },
    };

    const result = getRoomClassTextForGAllery(
      basketDetailsState,
      'de',
      unleashPremPlusAccFeatureFlag
    );
    expect(result).toBe('premierPlusAccessible');
  });

  it('should return "accessible" when isPremierPlusAccessibleRoomByCode returns true', () => {
    const basketDetailsState = {
      roomClass: RoomClass.ST,
      selectedRate: {
        roomTypes: [{ roomType: 'DIS' }],
      },
    };

    const result = getRoomClassTextForGAllery(
      basketDetailsState,
      'en',
      unleashPremPlusAccFeatureFlag
    );
    expect(result).toBe('accessible');
  });

  it('should return "accessible" when isPremierPlusAccessibleRoomByCode returns true and language is german', () => {
    const basketDetailsState = {
      roomClass: DeRoomClass.ST,
      selectedRate: {
        roomTypes: [{ roomType: 'DIS' }],
      },
    };

    const result = getRoomClassTextForGAllery(
      basketDetailsState,
      'de',
      unleashPremPlusAccFeatureFlag
    );
    expect(result).toBe('accessible');
  });
});

describe('filterRoomsByRoomClass', () => {
  const mockRoomTypeData: HIRoomType[] = [
    {
      roomType: 'DIS',
      adults: 2,
      children: 0,
      cotRequested: false,
      rooms: [
        {
          pmsRoomType: 'PPDLOW',
          silentSubstitution: true,
          cotAvailable: false,
          roomClass: 'PP',
          specialRequests: ['DBLE', 'LOWB'],
          roomPriceBreakdown: {
            totalNetAmount: 79,
            currencyCode: 'GBP',
            packageCode: null,
            packageAmount: null,
            dailyPrices: [{ date: '2025-06-19', netPrice: 79 }],
          },
        },
        {
          pmsRoomType: 'PPDWET',
          silentSubstitution: true,
          cotAvailable: false,
          roomClass: 'PP',
          specialRequests: ['DBLE', 'WETR'],
          roomPriceBreakdown: {
            totalNetAmount: 79,
            currencyCode: 'GBP',
            packageCode: null,
            packageAmount: null,
            dailyPrices: [{ date: '2025-06-19', netPrice: 79 }],
          },
        },
        {
          pmsRoomType: 'LOWDBL',
          silentSubstitution: true,
          cotAvailable: false,
          roomClass: 'ST',
          specialRequests: ['DBLE', 'LOWB'],
          roomPriceBreakdown: {
            totalNetAmount: 90,
            currencyCode: 'GBP',
            packageCode: null,
            packageAmount: null,
            dailyPrices: [{ date: '2025-06-19', netPrice: 90 }],
          },
        },
      ] as HIRoomRate[],
    },
    {
      roomType: 'DB',
      adults: 1,
      children: 0,
      cotRequested: false,
      rooms: [
        {
          pmsRoomType: 'PPLDBL',
          silentSubstitution: true,
          cotAvailable: false,
          roomClass: 'PP',
          specialRequests: ['SING'],
          roomPriceBreakdown: {
            totalNetAmount: 90,
            currencyCode: 'GBP',
            packageCode: null,
            packageAmount: null,
            dailyPrices: [{ date: '2025-06-19', netPrice: 90 }],
          },
        },
        {
          pmsRoomType: 'DOUBLE',
          silentSubstitution: true,
          cotAvailable: false,
          roomClass: 'ST',
          specialRequests: ['SING'],
          roomPriceBreakdown: {
            totalNetAmount: 90,
            currencyCode: 'GBP',
            packageCode: null,
            packageAmount: null,
            dailyPrices: [{ date: '2025-06-19', netPrice: 90 }],
          },
        },
      ] as HIRoomRate[],
    },
    {
      roomType: 'FAM',
      adults: 2,
      children: 1,
      cotRequested: false,
      rooms: [
        {
          pmsRoomType: 'FMTHRE',
          silentSubstitution: true,
          cotAvailable: false,
          roomClass: 'ST',
          specialRequests: ['TRIP'],
          roomPriceBreakdown: {
            totalNetAmount: 90,
            currencyCode: 'GBP',
            packageCode: null,
            packageAmount: null,
            dailyPrices: [{ date: '2025-06-19', netPrice: 90 }],
          },
        },
      ] as HIRoomRate[],
    },
  ];

  it('returns full list if allowedRoomClass is falsy', () => {
    expect(filterRoomsByRoomClass(mockRoomTypeData, '', unleashPremPlusAccFeatureFlag)).toEqual(
      mockRoomTypeData
    );
    expect(
      filterRoomsByRoomClass(mockRoomTypeData, undefined as any, unleashPremPlusAccFeatureFlag)
    ).toEqual(mockRoomTypeData);
  });

  it('filters for roomClass = "PP"', () => {
    const result = filterRoomsByRoomClass(mockRoomTypeData, 'PP', unleashPremPlusAccFeatureFlag);
    expect(result).toEqual([
      {
        roomType: 'DIS',
        adults: 2,
        children: 0,
        cotRequested: false,
        rooms: [
          expect.objectContaining({ pmsRoomType: 'PPDLOW', roomClass: 'PP' }),
          expect.objectContaining({ pmsRoomType: 'PPDWET', roomClass: 'PP' }),
        ],
      },
      {
        roomType: 'DB',
        adults: 1,
        children: 0,
        cotRequested: false,
        rooms: [expect.objectContaining({ pmsRoomType: 'PPLDBL', roomClass: 'PP' })],
      },
    ]);
  });

  it('filters for roomClass = "ST"', () => {
    const result = filterRoomsByRoomClass(mockRoomTypeData, 'ST', unleashPremPlusAccFeatureFlag);
    expect(result).toEqual([
      {
        roomType: 'DIS',
        adults: 2,
        children: 0,
        cotRequested: false,
        rooms: [expect.objectContaining({ pmsRoomType: 'LOWDBL', roomClass: 'ST' })],
      },
      {
        roomType: 'DB',
        adults: 1,
        children: 0,
        cotRequested: false,
        rooms: [expect.objectContaining({ pmsRoomType: 'DOUBLE', roomClass: 'ST' })],
      },
      {
        roomType: 'FAM',
        adults: 2,
        children: 1,
        cotRequested: false,
        rooms: [expect.objectContaining({ pmsRoomType: 'FMTHRE', roomClass: 'ST' })],
      },
    ]);
  });

  it('returns empty array for unmatched room class', () => {
    const result = filterRoomsByRoomClass(mockRoomTypeData, 'ZZ', unleashPremPlusAccFeatureFlag);
    expect(result).toEqual([]);
  });

  it('handles empty roomTypesData input', () => {
    expect(filterRoomsByRoomClass([], 'PP', unleashPremPlusAccFeatureFlag)).toEqual([]);
  });
});

describe('isItBarrierFree', () => {
  it('should return true if a room with matching roomClass and special request code exists', () => {
    const selectedRoomCodes = [
      {
        rooms: [
          {
            roomClass: 'STD',
            specialRequests: ['SR1', 'SR2'],
          },
        ],
      },
    ];
    const barrierRoomsCodes = ['SR2', 'SR3'];
    const selectedRoomClassCode = 'STD';

    expect(
      isItBarrierFree(selectedRoomCodes as any, barrierRoomsCodes, selectedRoomClassCode)
    ).toBe(true);
  });

  it('should return false if no room matches both the class and special request codes', () => {
    const selectedRoomCodes = [
      {
        rooms: [
          {
            roomClass: 'STD',
            specialRequests: ['SR1'],
          },
        ],
      },
    ];
    const barrierRoomsCodes = ['SR2', 'SR3'];
    const selectedRoomClassCode = 'STD';

    expect(
      isItBarrierFree(selectedRoomCodes as any, barrierRoomsCodes, selectedRoomClassCode)
    ).toBe(false);
  });
});

describe('hasMatchingPromotionCode', () => {
  const roomRates = [
    { ratePlanCode: 'FX30', promotionCode: 'PROMO1' },
    { ratePlanCode: 'FLEX', promotionCode: 'PROMO2' },
  ];

  it('returns true if matching promotion code found', () => {
    expect(hasMatchingPromotionCode('PROMO1', roomRates as HIRoomRate[])).toBe(true);
  });

  it('returns false if no match found', () => {
    expect(hasMatchingPromotionCode('PROMOX', roomRates as HIRoomRate[])).toBe(false);
  });

  it('returns false if invalid args', () => {
    expect(hasMatchingPromotionCode('', [])).toBe(false);
    expect(hasMatchingPromotionCode('PROMO', null as any)).toBe(false);
  });
});

describe('getActivePromotionCode (updated logic)', () => {
  const baseBanner = {
    promotionCode: 'FX20RU',
    isWithinPromoWindow: true,
    showPromo: true,
  } as PromotionsInformation;

  it('returns PROMOID when PROMOID matches bannerCode and promo is enabled', () => {
    const result = getActivePromotionCode(baseBanner, 'FX20RU');
    expect(result).toBe('FX20RU');
  });

  it('returns empty when isWithinPromoWindow = false', () => {
    const banner = {
      ...baseBanner,
      isWithinPromoWindow: false,
    } as PromotionsInformation;

    const result = getActivePromotionCode(banner, 'FX20RU');
    expect(result).toBe('');
  });

  it('returns bannerCode when PROMOID does not match and banner is valid and within window', () => {
    const result = getActivePromotionCode(baseBanner, 'ST10R');
    expect(result).toBe('FX20RU');
  });

  it('returns bannerCode when PROMOID is empty and banner is valid and within window', () => {
    const result = getActivePromotionCode(baseBanner, '');
    expect(result).toBe('FX20RU');
  });

  it('returns empty string when promo is disabled', () => {
    const banner = {
      ...baseBanner,
      showPromo: false,
    };

    const result = getActivePromotionCode(banner, 'FX20RU');
    expect(result).toBe('');
  });

  it('returns empty string when banner is outside promo window (second branch fails)', () => {
    const banner = {
      ...baseBanner,
      isWithinPromoWindow: false,
    };

    const result = getActivePromotionCode(banner, '');
    expect(result).toBe('');
  });

  it('returns empty string when bannerCode is empty', () => {
    const banner = {
      ...baseBanner,
      promotionCode: '',
    };

    const result = getActivePromotionCode(banner, '');
    expect(result).toBe('');
  });
});

describe('getRoomRatesWithRateInformation', () => {
  it('should return correct results', () => {
    const result = getRoomRatesWithRateInformation(roomRates, rateClassifications);
    expect(result.length).toBe(1);
  });
});

describe('getDashboardBasket', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call executeGraphQLQuery with GET_DASHBOARD_BASKET and basket reference', async () => {
    (executeGraphQLQuery as jest.Mock).mockResolvedValue({
      hotelId: 'HOTEL_123',
    });

    const result = await getDashboardBasket('BASKET_REF');

    expect(executeGraphQLQuery).toHaveBeenCalledTimes(1);
    expect(executeGraphQLQuery).toHaveBeenCalledWith(
      GET_DASHBOARD_BASKET,
      {
        basketReference: 'BASKET_REF',
      },
      expect.any(Function)
    );

    expect(result).toEqual({
      hotelId: 'HOTEL_123',
    });
  });
});

describe('getBrandForUnleashContext', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should return provided brand without making any API calls', async () => {
    const result = await getBrandForUnleashContext({
      language: 'en',
      brand: 'PI',
    });

    expect(result).toBe('PI');
    expect(staticHotelInformationIB).not.toHaveBeenCalled();
    expect(getHotelInformationIB).not.toHaveBeenCalled();
    expect(executeGraphQLQuery).not.toHaveBeenCalled();
  });

  it('should return brand from staticHotelInformationIB for string slug', async () => {
    (staticHotelInformationIB as jest.Mock).mockResolvedValue({
      brand: 'PI',
    });

    const result = await getBrandForUnleashContext({
      language: 'en',
      slug: 'london/westminster',
    });

    expect(staticHotelInformationIB).toHaveBeenCalledWith('/hotels/london/westminster', 'en');
    expect(result).toBe('PI');
  });

  it('should join slug array before calling staticHotelInformationIB', async () => {
    (staticHotelInformationIB as jest.Mock).mockResolvedValue({
      brand: 'HI',
    });

    const result = await getBrandForUnleashContext({
      language: 'en',
      slug: ['london', 'westminster'],
    });

    expect(staticHotelInformationIB).toHaveBeenCalledWith('/hotels/london/westminster', 'en');
    expect(result).toBe('HI');
  });

  it('should return brand from hotel information using basket reference', async () => {
    (executeGraphQLQuery as jest.Mock).mockResolvedValue({
      hotelId: '12345',
    });

    (getHotelInformationIB as jest.Mock).mockResolvedValue({
      brand: 'PI',
    });

    const result = await getBrandForUnleashContext({
      language: 'en',
      basketReference: 'BASKET123',
    });

    expect(executeGraphQLQuery).toHaveBeenCalledWith(
      GET_DASHBOARD_BASKET,
      { basketReference: 'BASKET123' },
      expect.any(Function)
    );

    expect(getHotelInformationIB).toHaveBeenCalledWith('12345', 'en');
    expect(result).toBe('PI');
  });

  it('should return undefined when basket has no hotelId', async () => {
    (executeGraphQLQuery as jest.Mock).mockResolvedValue({});

    const result = await getBrandForUnleashContext({
      language: 'en',
      basketReference: 'BASKET123',
    });

    expect(getHotelInformationIB).not.toHaveBeenCalled();
    expect(result).toBeUndefined();
  });

  it('should return undefined when no brand, slug or basketReference is provided', async () => {
    const result = await getBrandForUnleashContext({
      language: 'en',
    });

    expect(result).toBeUndefined();
    expect(staticHotelInformationIB).not.toHaveBeenCalled();
    expect(getHotelInformationIB).not.toHaveBeenCalled();
    expect(executeGraphQLQuery).not.toHaveBeenCalled();
  });
});

describe('extractPromoBoxData', () => {
  const promotionBannerData = {
    promotionCode: 'PROMO1',
    showPromo: true,
    promoBox: {
      title: 'Fallback Promo',
    },
  } as PromotionsInformation;

  const hotelAvailability = {
    promotionsInformation: {
      promotionCode: 'PROMO2',
      showPromo: true,
      promoBox: {
        title: 'Latest Promo',
      },
    },
  } as any;

  it('should return hotel availability promotions when feature flag is enabled', () => {
    expect(extractPromoBoxData(true, hotelAvailability, promotionBannerData)).toEqual(
      hotelAvailability.promotionsInformation
    );
  });

  it('should fallback to promotionBannerData when feature flag is disabled', () => {
    expect(extractPromoBoxData(false, hotelAvailability, promotionBannerData)).toEqual(
      promotionBannerData
    );
  });

  it('should fallback to promoBox from promotionBannerData when latest promoBox is missing', () => {
    const hotelAvailabilityWithoutPromoBox = {
      promotionsInformation: {
        promotionCode: 'PROMO2',
        showPromo: true,
      },
    } as any;

    expect(
      extractPromoBoxData(true, hotelAvailabilityWithoutPromoBox, promotionBannerData)
    ).toEqual({
      promotionCode: 'PROMO2',
      showPromo: true,
      promoBox: promotionBannerData.promoBox,
    });
  });

  it('should return undefined when both promotion sources are unavailable', () => {
    expect(extractPromoBoxData(true, undefined, undefined)).toBeUndefined();
    expect(extractPromoBoxData(false, undefined, undefined)).toBeUndefined();
  });

  it('should return hotel availability data when promotionBannerData is undefined', () => {
    expect(extractPromoBoxData(true, hotelAvailability, undefined)).toEqual(
      hotelAvailability.promotionsInformation
    );
  });

  it('should fallback to promotionBannerData when feature flag is enabled but hotel availability promotions are missing', () => {
    const hotelAvailabilityWithoutPromotions = {} as any;

    expect(
      extractPromoBoxData(true, hotelAvailabilityWithoutPromotions, promotionBannerData)
    ).toEqual(promotionBannerData);
  });
});
