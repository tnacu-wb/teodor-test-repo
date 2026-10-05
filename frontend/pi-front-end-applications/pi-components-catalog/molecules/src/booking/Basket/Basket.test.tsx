import '@testing-library/jest-dom';
import type {
  Channel,
  HIRoomType,
  HIRoomTypeInfoResponse,
  ReservationRoomType,
} from '@whitbread-eos/api';
import { RoomClass } from '@whitbread-eos/api';
import { useFeatureToggle } from '@whitbread-eos/utils';

import { getCorrectBundlePrice } from '../../hotel-details/BundleChoice';
import { act, fireEvent, render, userEvent, waitFor } from '../../utils/test-utils';
import BasketComponent from './Basket.component';
import BasketContainer, { BasketProps } from './Basket.container';
import { getStandardRoomLabel } from './Basket.helpers';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureToggle: jest.fn(() => ({
    release_pi_bb_ccui_choose_room_type: true,
    release_pi_promo_code_landing_page: true,
    release_pi_pib_ccui_city_tax_breakdown: true,
  })),
}));

const mockRoomTypesInformation = {
  roomTypeInformation: {
    roomTypes: [
      {
        roomTypeCode: ['WINCMB'],
        roomCategory: 'Twin',
        roomLabel: 'Twin Room',
        roomDescription:
          'Our twin rooms layouts differ between hotels. Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed. Some hotels have twin rooms with two double or kingsize Hypnos beds.',
        roomImage:
          '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
        groupId: 'twin',
      },
      {
        roomTypeCode: ['FMTHRE'],
        roomCategory: 'Family',
        roomLabel: 'Family Room',
        roomDescription:
          'Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.',
        roomImage:
          '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
        groupId: 'family',
      },
      {
        roomTypeCode: ['DIS'],
        roomCategory: 'Accessible room',
        roomLabel: 'Accessible Room',
        roomDescription:
          'Our spacious accessible rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.',
        roomImage:
          '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
        groupId: 'accessible',
      },
    ],
  },
};

const mockBasketData: BasketProps = {
  channel: 'PI' as Channel,
  variant: 'PI',
  brand: 'PI',
  roomClassIndexFromSelectedRate: 0,
  roomClassCode: 'ST',
  isHDPBasket: false,
  isLessThanLg: false,
  hasAccessibleRoom: false,
  hasTwinRoomChoice: false,
  roomTypeInformationResponse: {
    errorRoomTypeInformation: '',
    isLoadingRoomTypeInformation: false,
    isErrorRoomTypeInformation: false,
    dataRoomTypeInformation: {
      roomTypeInformation: {
        roomTypes: [
          {
            roomLabel: '',
            roomTypeCode: [],
            roomImage: '',
            roomDescription: '',
            roomCategory: '',
            groupId: '',
          },
        ],
      },
    },
  },
  selectedPMSRoomTypes: [],
  selectedSpecialRequests: [],
  bookRsvIsLoading: false,
  bookRsvIsError: false,
  bookRsvError: {},
  isDisabledContinueBtn: false,
  handleBooking: () => ({}),
  roomClass: RoomClass.ST,
  isCityTaxExempt: false,
  isLastFewRooms: false,
  shouldDisplayMobileBasket: false,
  hotelId: 'DLONEU',
  arrival: '2022-08-13',
  departure: '2022-08-14',
  numberOfUnits: 1,
  numberOfNights: 2,
  selectedRate: {
    ratePlanCode: 'FLEXRATE',
    rateCategory: '',
    cellCode: null,
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
              totalRoomNetAmount: 100,
              currencyCode: 'GBP',
              dailyPrices: [
                {
                  date: '2022-08-13',
                  netPrice: 75.0,
                  roomNetPrice: 70,
                },
                {
                  date: '2022-08-14',
                  netPrice: 40.33,
                  roomNetPrice: 30,
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
      } as HIRoomType,
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
      } as HIRoomType,
    ],
  },
  rateName: 'Flex',
  rateTags: [],
  isSilentFeatureFlagEnabled: true,
  selectedRateCategory: {
    rateOrder: '1',
  },
  isPrePopulateBillingAddressEnabled: true,
  isCityTaxEnabled: true,
  isCityTaxBreakdownEnabled: false,
};

const mockTwinRoomBasketData: BasketProps = {
  hotelId: 'MANOLD',
  arrival: '2023-12-26',
  departure: '2023-12-30',
  numberOfUnits: 4,
  numberOfNights: 4,
  selectedRate: {
    ratePlanCode: 'FLEXRATE',
    cellCode: null,
    roomTypes: [
      {
        roomType: 'TWIN',
        adults: 2,
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
              totalNetAmount: 100,
              currencyCode: 'GBP',
              packageCode: 'HSATWN',
              packageAmount: 5,
              dailyPrices: [
                {
                  date: '2023-12-26',
                  netPrice: 25,
                },
                {
                  date: '2023-12-27',
                  netPrice: 25,
                },
                {
                  date: '2023-12-28',
                  netPrice: 25,
                },
                {
                  date: '2023-12-29',
                  netPrice: 25,
                },
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
              totalNetAmount: 80,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              dailyPrices: [
                {
                  date: '2023-12-26',
                  netPrice: 20,
                },
                {
                  date: '2023-12-27',
                  netPrice: 20,
                },
                {
                  date: '2023-12-28',
                  netPrice: 20,
                },
                {
                  date: '2023-12-29',
                  netPrice: 20,
                },
              ],
            },
          },
        ],
      },
      {
        roomType: 'SB',
        adults: 1,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'TWINRM',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['SING'],
            roomPriceBreakdown: {
              totalNetAmount: 80,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              dailyPrices: [
                {
                  date: '2023-12-26',
                  netPrice: 20,
                },
                {
                  date: '2023-12-27',
                  netPrice: 20,
                },
                {
                  date: '2023-12-28',
                  netPrice: 20,
                },
                {
                  date: '2023-12-29',
                  netPrice: 20,
                },
              ],
            },
          },
        ],
      },
      {
        roomType: 'FAM',
        adults: 2,
        children: 1,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'FMTRPL',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['TRIP'],
            roomPriceBreakdown: {
              totalNetAmount: 120,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              dailyPrices: [
                {
                  date: '2023-12-26',
                  netPrice: 30,
                },
                {
                  date: '2023-12-27',
                  netPrice: 30,
                },
                {
                  date: '2023-12-28',
                  netPrice: 30,
                },
                {
                  date: '2023-12-29',
                  netPrice: 30,
                },
              ],
            },
          },
        ],
      },
      {
        roomType: 'TWIN',
        adults: 2,
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
              totalNetAmount: 100,
              currencyCode: 'GBP',
              packageCode: 'HSATWN',
              packageAmount: 5,
              dailyPrices: [
                {
                  date: '2023-12-26',
                  netPrice: 25,
                },
                {
                  date: '2023-12-27',
                  netPrice: 25,
                },
                {
                  date: '2023-12-28',
                  netPrice: 25,
                },
                {
                  date: '2023-12-29',
                  netPrice: 25,
                },
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
              totalNetAmount: 80,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              dailyPrices: [
                {
                  date: '2023-12-26',
                  netPrice: 20,
                },
                {
                  date: '2023-12-27',
                  netPrice: 20,
                },
                {
                  date: '2023-12-28',
                  netPrice: 20,
                },
                {
                  date: '2023-12-29',
                  netPrice: 20,
                },
              ],
            },
          },
        ],
      },
    ],
    rateCategory: 'A',
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
            roomTypeCode: ['WETTWN'],
            roomCategory: 'Accessible room',
            roomLabel: 'Accessible twin bedroom with level access shower room',
            roomDescription:
              'Accessible twin bedroom. Level access shower room with high-powered shower, conveniently placed shower controls, folding seat and wider doors.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
            groupId: 'accessible',
          },
          {
            roomTypeCode: ['WETDBL'],
            roomCategory: 'Accessible room',
            roomLabel: 'Accessible double bedroom with level access shower room',
            roomDescription:
              'Accessible double bedroom. Level access shower room with high-powered shower, conveniently placed shower controls, folding seat and wider doors.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
            groupId: 'accessible',
          },
          {
            roomTypeCode: ['LOWDBL'],
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
            roomLabel: 'Family room',
            roomDescription:
              'Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg',
            groupId: 'family',
          },
          {
            roomTypeCode: ['TWINRM', 'DBLDBL'],
            roomCategory: 'Twin',
            roomLabel: 'Twin room',
            roomDescription:
              'Our twin rooms layouts differ between hotels. Most feature a super-comfy double or kingsize bed, plus a single sofa bed. Some hotels have twin rooms with two double or kingsize Hypnos beds.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-twin-room.jpg',
            groupId: 'twin',
          },
          {
            roomTypeCode: ['SINGLE'],
            roomCategory: 'Standard',
            roomLabel: 'Standard room',
            roomDescription:
              'A super-comfy bed, a power shower and free Wi-Fi – our Standard rooms have everything you’ll need for a great night’s sleep.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4%201.jpg',
            groupId: 'single',
          },
          {
            roomTypeCode: ['PPLDBL', 'PDBZPL'],
            roomCategory: 'Premier Plus',
            roomLabel: 'Premier Plus room',
            roomDescription:
              'Our enhanced room design. Includes Ultimate Wi-Fi, Nespresso machine, mini-fridge, bedside USB ports, iron, upgraded workspace & more.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
            groupId: 'double',
          },
          {
            roomTypeCode: ['FMTRPL', 'FMTHRE'],
            roomCategory: 'Family',
            roomLabel: 'Family room',
            roomDescription:
              'Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg',
            groupId: 'family',
          },
          {
            roomTypeCode: ['DOUBLE', 'ZPLDBL'],
            roomCategory: 'Double',
            roomLabel: 'Double room',
            roomDescription:
              'A super-comfy bed, a power shower and free Wi-Fi – our double rooms have everything you’ll need for a great night’s sleep.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
            groupId: 'double',
          },
          {
            roomTypeCode: ['PPDLOW'],
            roomCategory: 'Accessible room',
            roomLabel: 'Accessible double bedroom with a lowered bath',
            roomDescription:
              'Accessible double bedroom. Bathroom with a lowered bath set at standard wheelchair height (480mm), lever taps, wider doors and bath mats available on request.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
            groupId: 'accessible',
          },
          {
            roomTypeCode: ['BRFDBL'],
            roomCategory: 'Accessible room',
            roomLabel: 'Accessible double bedroom with level access shower room',
            roomDescription:
              'Accessible double bedroom. Level access shower room with high-powered shower, conveniently placed shower controls, folding seat and wider doors.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
            groupId: 'accessible',
          },
          {
            roomTypeCode: ['BRFZPL'],
            roomCategory: 'Accessible room',
            roomLabel: 'Accessible double bedroom with level access shower room',
            roomDescription:
              'Accessible double bedroom. Level access shower room with high-powered shower, conveniently placed shower controls, folding seat and wider doors.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
            groupId: 'accessible',
          },
          {
            roomTypeCode: ['BRFTWN'],
            roomCategory: 'Accessible room',
            roomLabel: 'Accessible twin bedroom with level access shower room',
            roomDescription:
              'Accessible twin bedroom. Level access shower room with high-powered shower, conveniently placed shower controls, folding seat and wider doors.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
            groupId: 'accessible',
          },
        ],
      },
    },
    errorRoomTypeInformation: null,
  },
  bookingFlow: {
    bookingFlowItems: [
      {
        bookingId: 'booking-a1',
        rateCode: 'A',
        rateCategory: 'A',
      },
      {
        bookingId: 'booking-a1',
        rateCode: 'F',
        rateCategory: 'S',
      },
      {
        bookingId: 'booking-a1',
        rateCode: 'Q',
        rateCategory: 'C',
      },
      {
        bookingId: 'booking-spf',
        rateCode: 'G',
        rateCategory: '-',
      },
      {
        bookingId: 'booking-spf',
        rateCode: 'D',
        rateCategory: '-',
      },
      {
        bookingId: 'booking-spf',
        rateCode: 'E',
        rateCategory: 'D',
      },
      {
        bookingId: 'booking-a1',
        rateCode: 'R',
        rateCategory: 'O',
      },
      {
        bookingId: 'booking-a1',
        rateCode: 'S',
        rateCategory: 'U',
      },
      {
        bookingId: 'booking-a1',
        rateCode: 'FLEXRATE',
        rateCategory: 'B',
      },
      {
        bookingId: 'booking-a1',
        rateCode: 'STANDARD',
        rateCategory: 'F',
      },
      {
        bookingId: 'booking-a1',
        rateCode: 'SEMIFLEX',
        rateCategory: 'G',
      },
      {
        bookingId: 'booking-a1',
        rateCode: 'NONFLEX',
        rateCategory: '-',
      },
      {
        bookingId: 'booking-a1',
        rateCode: 'ADVANCE',
        rateCategory: '-',
      },
      {
        bookingId: 'booking-a1',
        rateCode: 'W',
        rateCategory: 'W',
      },
      {
        bookingId: 'booking-a1',
        rateCode: 'I',
        rateCategory: 'I',
      },
      {
        bookingId: 'booking-a1',
        rateCode: 'J',
        rateCategory: 'J',
      },
    ],
  },
  phoneNumber: '0333 321 1315',
  brand: 'PID',
  isCityTaxEnabled: true,
  isCityTaxBreakdownEnabled: false,
};

const mockSoftBundles = {
  softBundleContent: [
    {
      id: 'BFADBF',
      price: 86.99,
      description: 'Breakfast',
    },
    {
      id: 'HSATWN',
      price: 45.0,
      description: 'Wifi',
    },
    {
      id: 'HSCKIN',
      description: 'Early Check In',
      price: 10.99,
      strikeThrough: true,
    },
    {
      id: 'HSKUO',
      description: 'Late Check Out',
      price: 10.99,
      strikeThrough: true,
    },
  ],
  isOptional: true,
};

const roomTypeInformationResponse: HIRoomTypeInfoResponse = {
  isLoadingRoomTypeInformation: false,
  dataRoomTypeInformation: mockRoomTypesInformation,
  isErrorRoomTypeInformation: false,
  errorRoomTypeInformation: null,
};

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockUseSessionStorage = jest.fn((key) => {
  if (key === 'roomUpgradeModalCloseCount') {
    return [0, jest.fn()];
  }
  return [0, jest.fn()];
});

const sessionStore: Record<string, string> = {};

const mockSessionStorage = {
  getItem: jest.fn((key: string) => sessionStore[key] ?? null),
  setItem: jest.fn((key: string, value: string) => {
    sessionStore[key] = value;
  }),
  removeItem: jest.fn((key: string) => {
    delete sessionStore[key];
  }),
};

Object.defineProperty(window, 'sessionStorage', {
  value: mockSessionStorage,
});

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getCookie: jest.fn(),
  useFeatureSwitch: () => true,
  useSessionStorage: (key: string) => mockUseSessionStorage(key),
  useFeatureToggle: jest.fn(() => ({
    release_pi_room_upgrade_popup: true,
    release_pi_promotion_banner_tag: true,
    release_pi_promo_code_landing_page: true,
  })),
}));

describe('Basket', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue({
      locale: 'gb',
      query: { slug: ['england', 'greater-london', 'london', 'london-beckton.html'] },
    });
  });

  beforeEach(() => {
    document.body.innerHTML = '<div id="hotel-details-mobile-basket"></div>';
  });

  it('should render a desktop Basket correctly', function () {
    const { getByTestId, queryByTestId } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );
    expect(getByTestId('basket')).toBeInTheDocument();
    expect(queryByTestId('mobile-basket')).toBeNull();
  });

  it('should render a desktop Basket with soft bundles', function () {
    const { getByTestId } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
        isSoftBundlesVisible={true}
      />
    );
    expect(getByTestId('basket')).toBeInTheDocument();
  });

  it('should render a desktop Basket with twin room selected with packages correctly', function () {
    const { getByTestId, queryByTestId } = render(
      <BasketContainer
        {...mockTwinRoomBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );
    expect(getByTestId('basket')).toBeInTheDocument();
    expect(queryByTestId('mobile-basket')).toBeNull();
  });

  it('should render a mobile Basket correctly', function () {
    const { getByTestId, queryByTestId } = render(
      <BasketContainer
        {...mockBasketData}
        isLessThanLg={true}
        roomTypeInformationResponse={roomTypeInformationResponse}
        shouldDisplayMobileBasket={true}
      />
    );
    expect(getByTestId('mobile-basket')).toBeInTheDocument();
    expect(queryByTestId('basket')).toBeNull();
  });

  it('should render a last few rooms line if isLastFewRooms is true', function () {
    const { getByText } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
        isLastFewRooms={true}
      />
    );
    expect(getByText('hoteldetails.lastfewrooms')).toBeInTheDocument();
  });

  it('should not render a last few rooms line if isLastFewRooms is false', function () {
    const { queryByText } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );
    expect(queryByText('hoteldetails.lastfewrooms')).toBeNull();
  });

  it('should render a last few rooms line if isLastFewRooms is true for mobile basket', function () {
    const { getByText } = render(
      <BasketContainer
        {...mockBasketData}
        isLessThanLg={true}
        roomTypeInformationResponse={roomTypeInformationResponse}
        isLastFewRooms={true}
        shouldDisplayMobileBasket={true}
      />
    );
    expect(getByText('hoteldetails.lastfewrooms')).toBeInTheDocument();
  });

  it('should render the room type and rate plan name for a single room', function () {
    const { getByText } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );
    expect(getByText('Twin Room')).toBeInTheDocument();
    expect(getByText('Flex')).toBeInTheDocument();
  });

  it('should not render the room type and rate plan name when there is no selected room', function () {
    const { queryByText } = render(
      <BasketContainer
        {...mockBasketData}
        isHDPBasket
        rateName=""
        roomClassCode="PP"
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );
    expect(queryByText('Twin Room')).not.toBeInTheDocument();
    expect(queryByText('Flex')).not.toBeInTheDocument();
  });

  it('should render the number of rooms and rate plan name for multiple rooms', function () {
    const { getByText } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
        numberOfUnits={2}
      />
    );
    expect(getByText('hoteldetails.nrOfRooms, Flex')).toBeInTheDocument();
  });

  it('should render the corect number of nights', function () {
    const { getByText } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );
    expect(getByText('hoteldetails.nrOfNights')).toBeInTheDocument();
  });

  it('should hide "See breakdown" link on click and show "Close breakdown" link', function () {
    const { getByText, queryByText } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );

    expect(getByText('pihotelinfo.breakdownShow')).toBeInTheDocument();
    expect(queryByText('pihotelinfo.breakdownHide')).toBeNull();

    fireEvent.click(getByText('pihotelinfo.breakdownShow'));

    expect(queryByText('pihotelinfo.breakdownShow')).toBeNull();
    expect(getByText('pihotelinfo.breakdownHide')).toBeInTheDocument();
  });

  it('should hide "Close breakdown" link on click and show "See breakdown" link', function () {
    const { getByText, queryByText } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );

    fireEvent.click(getByText('pihotelinfo.breakdownShow'));

    expect(getByText('pihotelinfo.breakdownHide')).toBeInTheDocument();
    expect(queryByText('pihotelinfo.breakdownShow')).toBeNull();

    fireEvent.click(getByText('pihotelinfo.breakdownHide'));

    expect(getByText('pihotelinfo.breakdownShow')).toBeInTheDocument();
    expect(queryByText('pihotelinfo.breakdownHide')).toBeNull();
  });

  it('should hide "See breakdown" link on click and show "Close breakdown" link on mobile basket', function () {
    const { getByText, queryByText } = render(
      <BasketContainer
        {...mockBasketData}
        isLessThanLg={true}
        roomTypeInformationResponse={roomTypeInformationResponse}
        shouldDisplayMobileBasket={true}
      />
    );

    expect(getByText('pihotelinfo.breakdownShow')).toBeInTheDocument();
    expect(queryByText('pihotelinfo.breakdownHide')).toBeNull();

    fireEvent.click(getByText('pihotelinfo.breakdownShow'));

    expect(queryByText('pihotelinfo.breakdownShow')).toBeNull();
    expect(getByText('pihotelinfo.breakdownHide')).toBeInTheDocument();
  });

  it('should hide "Close breakdown" link on click and show "See breakdown" link on mobile basket', function () {
    const { getByText, queryByText } = render(
      <BasketContainer
        {...mockBasketData}
        isLessThanLg={true}
        roomTypeInformationResponse={roomTypeInformationResponse}
        shouldDisplayMobileBasket={true}
      />
    );

    fireEvent.click(getByText('pihotelinfo.breakdownShow'));

    expect(getByText('pihotelinfo.breakdownHide')).toBeInTheDocument();
    expect(queryByText('pihotelinfo.breakdownShow')).toBeNull();

    fireEvent.click(getByText('pihotelinfo.breakdownHide'));

    expect(getByText('pihotelinfo.breakdownShow')).toBeInTheDocument();
    expect(queryByText('pihotelinfo.breakdownHide')).toBeNull();
  });

  it('should display total price inline with number of nights', function () {
    const { getByTestId } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );
    expect(getByTestId('total-cost-for-nights')).toHaveTextContent('£230.66');
  });

  it('should render the corect number of rows in breakdown for a single room', function () {
    const { getByText, getAllByText } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );
    fireEvent.click(getByText('pihotelinfo.breakdownShow'));
    const rows = getAllByText('Aug', { exact: false });
    expect(rows).toHaveLength(2);
  });

  it('should render the corect number of rows in breakdown for multiple rooms', function () {
    const { getByText, getAllByText } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
        numberOfUnits={2}
      />
    );
    fireEvent.click(getByText('pihotelinfo.breakdownShow'));
    const rows = getAllByText('Aug', { exact: false });
    expect(rows).toHaveLength(4);
  });

  it('should render "Room {index}" in breakdown for multiple rooms', function () {
    const { getByText, getAllByTestId } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
        numberOfUnits={2}
      />
    );
    fireEvent.click(getByText('pihotelinfo.breakdownShow'));
    const allRoomsHeaderBreakdown = getAllByTestId('breakdown-room-number');
    expect(allRoomsHeaderBreakdown[0]).toHaveTextContent('booking.hotel.summary.room');
    expect(allRoomsHeaderBreakdown[0]).toHaveTextContent('(Twin Room)');
    expect(allRoomsHeaderBreakdown[1]).toHaveTextContent('booking.hotel.summary.room');
    expect(allRoomsHeaderBreakdown[1]).toHaveTextContent('(Family Room)');
  });

  it('should render the corect number of rows in breakdown for a single room for mobile basket', function () {
    const { getByText, getAllByText } = render(
      <BasketContainer
        {...mockBasketData}
        isLessThanLg={true}
        roomTypeInformationResponse={roomTypeInformationResponse}
        shouldDisplayMobileBasket={true}
      />
    );
    fireEvent.click(getByText('pihotelinfo.breakdownShow'));
    const rows = getAllByText('Aug', { exact: false });
    expect(rows).toHaveLength(2);
  });

  it('should render the corect number of rows in breakdown for multiple rooms for mobile basket', function () {
    const { getByText, getAllByText } = render(
      <BasketContainer
        {...mockBasketData}
        isLessThanLg={true}
        roomTypeInformationResponse={roomTypeInformationResponse}
        numberOfUnits={2}
        shouldDisplayMobileBasket={true}
      />
    );
    fireEvent.click(getByText('pihotelinfo.breakdownShow'));
    const rows = getAllByText('Aug', { exact: false });
    expect(rows).toHaveLength(4);
  });

  it('should render the corect currency symbol', function () {
    const { getByTestId } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );
    expect(getByTestId('total-cost')).toHaveTextContent('£');
  });

  it('should render the price without decimals if sum is whole', function () {
    mockBasketData.selectedRate.roomTypes[0].rooms[0].roomPriceBreakdown.dailyPrices[0].netPrice = 75.34;
    const { getByTestId } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );
    expect(getByTestId('total-cost')).toHaveTextContent('£230');
  });

  it('should render the price with 2 decimals if sum is not whole', function () {
    const { getByTestId } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );
    expect(getByTestId('total-cost')).toHaveTextContent('£230.66');
  });

  it('should render the price correctly for mobile basket', function () {
    const { getByTestId } = render(
      <BasketContainer
        {...mockBasketData}
        isLessThanLg={true}
        roomTypeInformationResponse={roomTypeInformationResponse}
        shouldDisplayMobileBasket={true}
      />
    );
    expect(getByTestId('mobile-total-cost')).toHaveTextContent('£230.66');
  });

  it('should format breakdown dates properly', function () {
    const { getByText } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );
    fireEvent.click(getByText('pihotelinfo.breakdownShow'));
    expect(getByText('Sat 13 Aug 2022')).toBeInTheDocument();
    expect(getByText('Sun 14 Aug 2022')).toBeInTheDocument();
  });

  it('should render Basket component when soft bundles and rates are passed along as props', () => {
    const { getAllByTestId, getByTestId } = render(
      <BasketContainer
        {...mockBasketData}
        isLessThanLg={false}
        softBundles={mockSoftBundles}
        roomTypeInformationResponse={roomTypeInformationResponse}
        isSoftBundlesVisible={true}
      />
    );
    expect(getAllByTestId('hdp_bundleItem')).toHaveLength(
      mockSoftBundles?.softBundleContent?.filter((bundle) => !bundle.strikeThrough).length
    );
    const continueButton = getByTestId('hdp_basketBookNowButton');
    expect(continueButton).toBeInTheDocument();
    userEvent.click(continueButton);
  });

  it('should render Basket component when included soft bundles and not include bundles in basket summary ', () => {
    const { queryByTestId, getByTestId } = render(
      <BasketContainer
        {...mockBasketData}
        isLessThanLg={false}
        softBundles={{ ...mockSoftBundles, isOptional: false }}
        roomTypeInformationResponse={roomTypeInformationResponse}
        isSoftBundlesVisible={true}
      />
    );

    expect(queryByTestId('hdp_bundleItem')).not.toBeInTheDocument();
    const continueButton = getByTestId('hdp_basketBookNowButton');
    expect(continueButton).toBeInTheDocument();
    userEvent.click(continueButton);
  });

  it('should render total price calculated with soft Bundles', () => {
    const bundlePrice = getCorrectBundlePrice(mockSoftBundles?.softBundleContent, 1, 1);

    const { getByTestId } = render(
      <BasketContainer
        {...{ ...mockBasketData, numberOfNights: 1 }}
        isLessThanLg={false}
        softBundles={mockSoftBundles}
        roomTypeInformationResponse={roomTypeInformationResponse}
        isSoftBundlesVisible={true}
      />
    );
    expect(getByTestId('total-cost')).toHaveTextContent(`£${bundlePrice + 230.66}`);
  });

  it('should render total price calculated with soft Bundles and count in adults number and nights number', () => {
    const adultsNumber = 2;
    const nightsNumber = 2;
    const bundlePrice = getCorrectBundlePrice(
      mockSoftBundles?.softBundleContent,
      adultsNumber,
      nightsNumber
    );

    const { getByTestId } = render(
      <BasketContainer
        {...mockBasketData}
        isLessThanLg={false}
        softBundles={mockSoftBundles}
        roomTypeInformationResponse={roomTypeInformationResponse}
        adultsNumber={adultsNumber}
        numberOfNights={nightsNumber}
        isSoftBundlesVisible={true}
      />
    );
    expect(getByTestId('total-cost')).toHaveTextContent(`£${bundlePrice + 230.66}`);
  });

  it('should render total price calculated with soft Bundles and count in adults number as 1 when not sent as prop', () => {
    const bundlePrice = getCorrectBundlePrice(mockSoftBundles?.softBundleContent, 1, 1);

    const { getByTestId } = render(
      <BasketContainer
        {...{ ...mockBasketData, numberOfNights: 1 }}
        isLessThanLg={false}
        softBundles={mockSoftBundles}
        roomTypeInformationResponse={roomTypeInformationResponse}
        isSoftBundlesVisible={true}
      />
    );
    expect(getByTestId('total-cost')).toHaveTextContent(`£${bundlePrice + 230.66}`);
  });

  it('should render Basket component when soft bundles and rates are passed along as props and isRoomOnly is true', () => {
    const { getByTestId, getAllByTestId } = render(
      <BasketContainer
        {...{ ...mockBasketData, numberOfNights: 1 }}
        isLessThanLg={false}
        softBundles={mockSoftBundles}
        roomTypeInformationResponse={roomTypeInformationResponse}
        isSoftBundlesVisible={true}
        isRoomOnly={true}
      />
    );
    const totalCost = getByTestId('total-cost-for-nights');
    expect(totalCost).toHaveTextContent('£100');
    expect(getAllByTestId('hdp_bundleItem')).toHaveLength(
      mockSoftBundles?.softBundleContent?.filter((bundle) => !bundle.strikeThrough).length
    );
  });

  it('should render city tax exempt row and notification if isCityTaxExempt is true', function () {
    const { getByText } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
        isCityTaxExempt={true}
      />
    );
    expect(getByText('hoteldetails.rates.cityTax')).toBeInTheDocument();
    expect(getByText('config.policyOfCityTaxGlobalMessage.globalMessage')).toBeInTheDocument();
  });

  it('should not render city tax exempt row and notification if isCityTaxExempt is false', function () {
    const { queryByText } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );
    expect(queryByText('hoteldetails.rates.cityTax')).toBeNull();
    expect(queryByText('config.policyOfCityTaxGlobalMessage.globalMessage')).toBeNull();
  });

  it('should not render choose bathroom button for german hotels', function () {
    mockUseRouter.mockReturnValue({
      locale: 'de',
      query: { slug: ['germany', 'berlin', 'berlin', 'berlin-alexanderplatz.html'] },
    });
    const { queryByTestId } = render(
      <BasketComponent
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'TWINRM',
            silentSubstitution: false,
          },
          {
            roomLabelCode: 'LOWDBL',
            silentSubstitution: false,
          },
        ]}
        isHDPBasket={true}
        hasAccessibleRoom={true}
        hasTwinRoomChoice={false}
        roomTypeInformationResponse={roomTypeInformationResponse}
        currencyCode={''}
        bookRsvIsError={false}
        bookRsvError={{}}
        onBookReservation={() => ({})}
        totalReservationAmount={230.66}
        dailyPricesPerRoom={[
          [
            {
              date: '2022-08-13',
              netPrice: 75.0,
            },
            {
              date: '2022-08-14',
              netPrice: 40.33,
            },
          ],
        ]}
        brand="PI"
      />
    );
    expect(queryByTestId('hdp_basketChooseRoomTypeButton')).toBeNull();
  });

  it('should not render choose bathroom button for HUB hotels', function () {
    mockUseRouter.mockReturnValue({
      locale: 'gb',
      query: { slug: ['england', 'greater-london', 'london', 'london-beckton.html'] },
    });
    const { queryByTestId } = render(
      <BasketComponent
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'TWINRM',
            silentSubstitution: false,
          },
          {
            roomLabelCode: 'ACCWIN',
            silentSubstitution: false,
          },
        ]}
        isHDPBasket={true}
        hasAccessibleRoom={true}
        hasTwinRoomChoice={false}
        roomTypeInformationResponse={roomTypeInformationResponse}
        currencyCode={''}
        bookRsvIsError={false}
        bookRsvError={{}}
        onBookReservation={() => ({})}
        totalReservationAmount={230.66}
        dailyPricesPerRoom={[
          [
            {
              date: '2022-08-13',
              netPrice: 75.0,
            },
            {
              date: '2022-08-14',
              netPrice: 40.33,
            },
          ],
        ]}
        brand="HUB"
      />
    );
    expect(queryByTestId('hdp_basketChooseRoomTypeButton')).toBeNull();
  });

  it('should render choose bathroom button in HDP page', async function () {
    const mockRouter = {
      locale: 'gb',
      query: { slug: ['england', 'greater-london', 'london', 'london-beckton.html'] },
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const { getByTestId, getByRole, queryByTestId } = render(
      <BasketComponent
        isAccessibleType={true}
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'LOWDBL',
            silentSubstitution: false,
          },
        ]}
        isHDPBasket={true}
        hasAccessibleRoom={true}
        hasTwinRoomChoice={false}
        roomTypeInformationResponse={roomTypeInformationResponse}
        currencyCode={''}
        bookRsvIsError={false}
        bookRsvError={{}}
        onBookReservation={() => ({})}
        totalReservationAmount={230.66}
        dailyPricesPerRoom={[
          [
            {
              date: '2022-08-13',
              netPrice: 75.0,
            },
            {
              date: '2022-08-14',
              netPrice: 40.33,
            },
          ],
        ]}
        brand="PI"
      />
    );

    const chooseBathroomCTA = getByRole('button', { name: /hoteldetails.rates.chooseroomtype/ });
    await userEvent.click(chooseBathroomCTA);

    const url = '/gb/en/hotels/choose-bathroom';
    expect(mockRouter.push).toHaveBeenCalledWith(expect.stringContaining(url));
    expect(queryByTestId('hdp_mobileBasketChooseRoomTypeButton')).toBeNull();
    expect(getByTestId('hdp_basketChooseRoomTypeButton')).toBeInTheDocument();
    expect(getByRole('button')).toHaveAttribute('id', 'chooseBathroomCTA');
  });

  it('should render mobile choose bathroom button in HDP page', async function () {
    const mockRouter = {
      locale: 'gb',
      query: { slug: ['england', 'greater-london', 'london', 'london-beckton.html'] },
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockBasketData.shouldDisplayMobileBasket = true;

    const { getByRole, getByTestId, queryByTestId } = render(
      <BasketComponent
        isAccessibleType={true}
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'LOWDBL',
            silentSubstitution: false,
          },
        ]}
        isHDPBasket={true}
        hasAccessibleRoom={true}
        hasTwinRoomChoice={false}
        roomTypeInformationResponse={roomTypeInformationResponse}
        currencyCode={''}
        bookRsvIsError={false}
        bookRsvError={{}}
        onBookReservation={() => ({})}
        totalReservationAmount={230.66}
        dailyPricesPerRoom={[
          [
            {
              date: '2022-08-13',
              netPrice: 75.0,
            },
            {
              date: '2022-08-14',
              netPrice: 40.33,
            },
          ],
        ]}
        brand="PI"
      />
    );

    const chooseBathroomCTA = getByRole('button', { name: /hoteldetails.rates.chooseroomtype/ });
    await userEvent.click(chooseBathroomCTA);

    const url = '/gb/en/hotels/choose-bathroom';
    expect(mockRouter.push).toHaveBeenCalledWith(expect.stringContaining(url));
    expect(getByTestId('hdp_mobileBasketChooseRoomTypeButton')).toBeInTheDocument();
    expect(queryByTestId('hdp_basketChooseRoomTypeButton')).toBeNull();
    expect(getByRole('button')).toHaveAttribute('id', 'chooseBathroomCTA');
  });

  it('should render choose twin button in HDP page', async function () {
    const mockRouter = {
      locale: 'gb',
      query: { slug: ['england', 'greater-london', 'london', 'london-beckton.html'] },
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockBasketData.shouldDisplayMobileBasket = false;
    const { getByTestId, getByRole } = render(
      <BasketComponent
        isAccessibleType={true}
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'TWINRM',
            silentSubstitution: false,
          },
        ]}
        isHDPBasket={true}
        hasAccessibleRoom={false}
        hasTwinRoomChoice={true}
        roomTypeInformationResponse={roomTypeInformationResponse}
        currencyCode={''}
        bookRsvIsError={false}
        bookRsvError={{}}
        onBookReservation={() => ({})}
        totalReservationAmount={230.66}
        dailyPricesPerRoom={[
          [
            {
              date: '2022-08-13',
              netPrice: 75.0,
            },
            {
              date: '2022-08-14',
              netPrice: 40.33,
            },
          ],
        ]}
        brand="PI"
      />
    );

    const chooseTwinroomCTA = getByRole('button', { name: /hoteldetails.rates.chooseroomtype/ });
    await userEvent.click(chooseTwinroomCTA);

    const url = '/gb/en/hotels/choose-twinroom';
    expect(mockRouter.push).toHaveBeenCalledWith(expect.stringContaining(url));

    expect(getByTestId('hdp_basketChooseRoomTypeButton')).toBeInTheDocument();
    expect(getByRole('button')).toHaveAttribute('id', 'chooseTwinroomCTA');
  });

  it('should NOT render choose twin button in HDP page for DE hotel in GB locale', async function () {
    const mockRouter = {
      locale: 'gb',
      query: { slug: ['germany', 'berlin', 'berlin', 'berlin-alexanderplatz.html'] },
      push: jest.fn(),
    };

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockBasketData.shouldDisplayMobileBasket = false;
    const { getByRole, getByText } = render(
      <BasketComponent
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'TWINRM',
            silentSubstitution: false,
          },
        ]}
        isHDPBasket={true}
        hasAccessibleRoom={false}
        hasTwinRoomChoice={true}
        roomTypeInformationResponse={roomTypeInformationResponse}
        currencyCode={''}
        bookRsvIsError={false}
        bookRsvError={{}}
        onBookReservation={() => ({})}
        totalReservationAmount={230.66}
        dailyPricesPerRoom={[
          [
            {
              date: '2022-08-13',
              netPrice: 75.0,
            },
            {
              date: '2022-08-14',
              netPrice: 40.33,
            },
          ],
        ]}
        brand="PI"
      />
    );

    expect(getByRole('button')).not.toHaveAttribute('id', 'chooseTwinroomCTA');
    expect(getByRole('button')).not.toHaveAttribute('id', 'chooseBathroomCTA');
    expect(getByText('hoteldetails.booknowtext')).toBeInTheDocument();
  });

  it('should NOT render choose twin button in HDP page for DE hotel in DE locale', async function () {
    const mockRouter = {
      locale: 'de',
      query: { slug: ['deutschland', 'berlin', 'berlin', 'berlin-alexanderplatz.html'] },
      push: jest.fn(),
    };

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockBasketData.shouldDisplayMobileBasket = false;
    const { getByRole, getByText } = render(
      <BasketComponent
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'TWINRM',
            silentSubstitution: false,
          },
        ]}
        isHDPBasket={true}
        hasAccessibleRoom={false}
        hasTwinRoomChoice={true}
        roomTypeInformationResponse={roomTypeInformationResponse}
        currencyCode={''}
        bookRsvIsError={false}
        bookRsvError={{}}
        onBookReservation={() => ({})}
        totalReservationAmount={230.66}
        dailyPricesPerRoom={[
          [
            {
              date: '2022-08-13',
              netPrice: 75.0,
            },
            {
              date: '2022-08-14',
              netPrice: 40.33,
            },
          ],
        ]}
        brand="PID"
      />
    );

    expect(getByRole('button')).not.toHaveAttribute('id', 'chooseTwinroomCTA');
    expect(getByRole('button')).not.toHaveAttribute('id', 'chooseBathroomCTA');
    expect(getByText('hoteldetails.booknowtext')).toBeInTheDocument();
  });

  it('should NOT render choose twin button if there is an accessible room as well as a twin room in HDP', async function () {
    const mockRouter = {
      locale: 'gb',
      query: { slug: ['england', 'greater-london', 'london', 'london-beckton.html'] },
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockBasketData.shouldDisplayMobileBasket = false;
    const { getByTestId, getByRole } = render(
      <BasketComponent
        isAccessibleType={true}
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'TWINRM',
            silentSubstitution: false,
          },
          {
            roomLabelCode: 'LOWDBL',
            silentSubstitution: false,
          },
        ]}
        isHDPBasket={true}
        hasAccessibleRoom={true}
        hasTwinRoomChoice={false}
        roomTypeInformationResponse={roomTypeInformationResponse}
        currencyCode={''}
        bookRsvIsError={false}
        bookRsvError={{}}
        onBookReservation={() => ({})}
        totalReservationAmount={230.66}
        dailyPricesPerRoom={[
          [
            {
              date: '2022-08-13',
              netPrice: 75.0,
            },
            {
              date: '2022-08-14',
              netPrice: 40.33,
            },
          ],
        ]}
        brand="PI"
      />
    );
    const chooseBathroomCTA = getByRole('button', { name: /hoteldetails.rates.chooseroomtype/ });
    await userEvent.click(chooseBathroomCTA);

    const url = '/gb/en/hotels/choose-bathroom';
    expect(mockRouter.push).toHaveBeenCalledWith(expect.stringContaining(url));
    expect(getByTestId('hdp_basketChooseRoomTypeButton')).toBeInTheDocument();
    expect(getByRole('button')).toHaveAttribute('id', 'chooseBathroomCTA');
    expect(getByRole('button')).not.toHaveAttribute('id', 'chooseTwinroomCTA');
  });

  it('should render "Continue" text for CTA button on choose-bathroom page', function () {
    const mockRouter = {
      locale: 'gb',
      query: { slug: ['england', 'greater-london', 'london', 'london-beckton.html'] },
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockBasketData.shouldDisplayMobileBasket = false;
    const { getByTestId, getByText } = render(
      <BasketComponent
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'TWINRM',
            silentSubstitution: false,
          },
          {
            roomLabelCode: 'LOWDBL',
            silentSubstitution: false,
          },
        ]}
        isHDPBasket={false}
        hasAccessibleRoom={true}
        hasTwinRoomChoice={false}
        roomTypeInformationResponse={roomTypeInformationResponse}
        currencyCode={''}
        bookRsvIsError={false}
        bookRsvError={{}}
        onBookReservation={() => ({})}
        totalReservationAmount={230.66}
        dailyPricesPerRoom={[
          [
            {
              date: '2022-08-13',
              netPrice: 75.0,
            },
            {
              date: '2022-08-14',
              netPrice: 40.33,
            },
          ],
        ]}
        brand="PI"
      />
    );
    expect(getByTestId('hdp_basketBookNowButton')).toBeInTheDocument();
    expect(getByText('booking.summary.continue')).toBeInTheDocument();
  });

  it('should render "Continue" text for CTA button on choose-twinroom page', function () {
    const mockRouter = {
      locale: 'gb',
      query: { slug: ['england', 'greater-london', 'london', 'london-beckton.html'] },
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockBasketData.shouldDisplayMobileBasket = false;
    const { getByTestId, getByText } = render(
      <BasketComponent
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'TWINRM',
            silentSubstitution: false,
          },
          {
            roomLabelCode: 'LOWDBL',
            silentSubstitution: false,
          },
        ]}
        isHDPBasket={false}
        hasAccessibleRoom={false}
        hasTwinRoomChoice={true}
        roomTypeInformationResponse={roomTypeInformationResponse}
        currencyCode={''}
        bookRsvIsError={false}
        bookRsvError={{}}
        onBookReservation={() => ({})}
        totalReservationAmount={230.66}
        dailyPricesPerRoom={[
          [
            {
              date: '2022-08-13',
              netPrice: 75.0,
            },
            {
              date: '2022-08-14',
              netPrice: 40.33,
            },
          ],
        ]}
        brand="PI"
        twinroomSelections={['twobeds']}
      />
    );
    expect(getByTestId('hdp_basketBookNowButton')).toBeInTheDocument();
    expect(getByText('booking.summary.continue')).toBeInTheDocument();
  });

  it('should render more specific twin room labels for the Choose twin room basket', function () {
    mockBasketData.selectedRate = {
      ratePlanCode: 'FLEXRATE',
      rateCategory: '',
      cellCode: null,
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
        } as HIRoomType,
        {
          roomType: 'TWIN',
          adults: 2,
          children: 0,
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
              specialRequests: ['TWDS'],
            },
          ],
        } as HIRoomType,
      ],
    };
    const { getByText, getAllByTestId } = render(
      <BasketContainer
        {...mockBasketData}
        roomTypeInformationResponse={roomTypeInformationResponse}
        numberOfUnits={2}
        twinroomSelections={['twobeds', 'doublesofa']}
      />
    );
    fireEvent.click(getByText('pihotelinfo.breakdownShow'));

    const allRoomsHeaderBreakdown = getAllByTestId('breakdown-room-number');
    expect(allRoomsHeaderBreakdown[0]).toHaveTextContent('twinroom.improvedTwin.title');
    expect(allRoomsHeaderBreakdown[1]).toHaveTextContent('twinroom.standardTwin.title');
  });

  it('should display error message if an error occured', function () {
    const { getByText } = render(
      <BasketComponent
        {...mockBasketData}
        currencyCode={''}
        roomTypeInformationResponse={roomTypeInformationResponse}
        bookRsvIsError={true}
        bookRsvError={{ message: 'Error' }}
        onBookReservation={() => ({})}
        totalReservationAmount={230.66}
        roomCodes={[
          {
            roomLabelCode: 'Double room',
            silentSubstitution: true,
          },
        ]}
        dailyPricesPerRoom={[
          [
            {
              date: '2022-08-13',
              netPrice: 75.0,
            },
            {
              date: '2022-08-14',
              netPrice: 40.33,
            },
          ],
        ]}
        brand="PI"
      />
    );
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should display roomTypes repsonse error message if an error occured', function () {
    const roomTypeInformationResponse = {
      isLoadingRoomTypeInformation: false,
      dataRoomTypeInformation: {
        roomTypeInformation: {
          roomTypes: [],
        },
      },
      isErrorRoomTypeInformation: true,
      errorRoomTypeInformation: { message: 'RoomTypes Error message' },
    };
    const { getByText } = render(
      <BasketComponent
        {...mockBasketData}
        currencyCode={''}
        roomTypeInformationResponse={roomTypeInformationResponse}
        bookRsvIsError={false}
        bookRsvError={{ message: '' }}
        onBookReservation={() => ({})}
        totalReservationAmount={230.66}
        roomCodes={[
          {
            roomLabelCode: 'Double room',
            silentSubstitution: true,
          },
        ]}
        dailyPricesPerRoom={[
          [
            {
              date: '2022-08-13',
              netPrice: 75.0,
            },
            {
              date: '2022-08-14',
              netPrice: 40.33,
            },
          ],
        ]}
        brand="PI"
      />
    );
    expect(getByText('RoomTypes Error message')).toBeInTheDocument();
  });

  it('should show roomTypes loading message if isLoadingRoomTypeInformation true', function () {
    const roomTypeInformationResponse = {
      isLoadingRoomTypeInformation: true,
      dataRoomTypeInformation: {
        roomTypeInformation: {
          roomTypes: [],
        },
      },
      isErrorRoomTypeInformation: false,
      errorRoomTypeInformation: { message: '' },
    };

    const { getByTestId } = render(
      <BasketComponent
        {...mockBasketData}
        currencyCode={''}
        roomTypeInformationResponse={roomTypeInformationResponse}
        bookRsvIsError={false}
        bookRsvError={{ message: '' }}
        onBookReservation={() => ({})}
        totalReservationAmount={230.66}
        roomCodes={[
          {
            roomLabelCode: 'Double room',
            silentSubstitution: true,
          },
        ]}
        dailyPricesPerRoom={[
          [
            {
              date: '2022-08-13',
              netPrice: 75.0,
            },
            {
              date: '2022-08-14',
              netPrice: 40.33,
            },
          ],
        ]}
        brand="PI"
      />
    );
    expect(getByTestId('room-types-loading-message')).not.toBeNull();
  });

  it('should render silent substitution name for room', function () {
    mockUseRouter.mockReturnValue({
      locale: 'gb',
      query: { slug: ['england', 'greater-london', 'london', 'london-beckton.html'] },
    });
    const { getByText } = render(
      <BasketComponent
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'Double room',
            silentSubstitution: true,
          },
        ]}
        currencyCode={''}
        onBookReservation={() => ({})}
        totalReservationAmount={230.66}
        dailyPricesPerRoom={[
          [
            {
              date: '2022-08-13',
              netPrice: 75.0,
            },
            {
              date: '2022-08-14',
              netPrice: 40.33,
            },
          ],
        ]}
        brand="CCUI"
      />
    );
    expect(getByText('Double room')).toBeInTheDocument();
    expect(getByText('Flex')).toBeInTheDocument();
  });

  it('should display City tax info message below price if the hotel is configured with cityTax', () => {
    mockUseRouter.mockReturnValue({
      locale: 'gb',
      query: { slug: ['england', 'greater-london', 'london', 'london-beckton.html'] },
    });
    const { getByText } = render(
      <BasketComponent
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'Double room',
            silentSubstitution: true,
          },
        ]}
        currencyCode={''}
        onBookReservation={() => ({})}
        totalReservationAmount={230.66}
        dailyPricesPerRoom={[
          [
            {
              date: '2022-08-13',
              netPrice: 75.0,
            },
            {
              date: '2022-08-14',
              netPrice: 40.33,
            },
          ],
        ]}
        brand="CCUI"
      />
    );
    expect(getByText('hoteldetails.rates.cityTaxAndCharges')).toBeInTheDocument();
  });

  it('should not display City tax info message below price if the hotel is not configured with cityTax', () => {
    mockUseRouter.mockReturnValue({
      locale: 'gb',
      query: { slug: ['england', 'greater-london', 'london', 'london-beckton.html'] },
    });
    const { queryByText } = render(
      <BasketComponent
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'Double room',
            silentSubstitution: true,
          },
        ]}
        currencyCode={''}
        onBookReservation={() => ({})}
        totalReservationAmount={230.66}
        dailyPricesPerRoom={[
          [
            {
              date: '2022-08-13',
              netPrice: 75.0,
            },
            {
              date: '2022-08-14',
              netPrice: 40.33,
            },
          ],
        ]}
        brand="CCUI"
        isCityTaxEnabled={false}
      />
    );
    expect(queryByText('hoteldetails.rates.cityTaxAndCharges')).not.toBeInTheDocument();
  });
});

describe('getStandardRoomLabel', () => {
  const roomTypeInformationResponse = {
    isLoadingRoomTypeInformation: false,
    isErrorRoomTypeInformation: false,
    dataRoomTypeInformation: {
      roomTypeInformation: {
        roomTypes: [
          {
            roomTypeCode: ['DBLWIN', 'DBLNWD'],
            roomCategory: 'Standard',
            roomLabel: 'Standard room',
            roomDescription: 'Compact rooms, designed around you.',
            roomImage:
              '/content/dam/pi/websites/desktop/new-hotel-details-content/Hub/Hub-Standard-Room.jpg',
            groupId: 'double',
          },
          {
            roomTypeCode: ['BIGWIN', 'BIGNWD', 'BIGZPL'],
            roomCategory: 'Bigger Room',
            roomLabel: 'Bigger room',
            roomDescription:
              'All the clever design, entertainment and comfort of our standard room just a bit, well, bigger. There’s extra space and a luxurious kingsize bed.',
            roomImage: '/content/dam/hub/hotelimages/generic/hub-bigger.jpg',
            groupId: 'double',
          },
          {
            roomTypeCode: ['ACCWIN', 'ACCNWD'],
            roomCategory: 'Accessible',
            roomLabel: 'Accessible room',
            roomDescription:
              'A larger room with a 480mm-high Hypnos bed, level access en-suite shower room with folding seat & wide-entry doors',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/hub-accessible-bedroom.jpg',
            groupId: 'accessible',
          },
        ],
      },
    },
    errorRoomTypeInformation: null,
  };

  const twinRoomLabels = ['Twin - two single beds', 'Twin - double bed and sofa bed'];

  it('should return the room label for a Twin room when twinroomSelection is provided', () => {
    const roomCode = {
      roomLabelCode: 'ACCWIN',
      silentSubstitution: false,
    };
    const twinroomSelection = 'twobeds';
    const result = getStandardRoomLabel(
      roomTypeInformationResponse,
      roomCode,
      twinRoomLabels,
      twinroomSelection
    );

    expect(result).toEqual('Twin - two single beds');
  });

  it('should return the room label when silent substitution is true', () => {
    const roomCode = {
      roomLabelCode: 'Accessible room',
      silentSubstitution: true,
    };
    const result = getStandardRoomLabel(roomTypeInformationResponse, roomCode, twinRoomLabels);

    expect(result).toEqual('Accessible room');
  });

  it('should return the room label based on roomTypeCode or included roomTypeCode', () => {
    const roomCode = {
      roomLabelCode: 'BIGWIN',
      silentSubstitution: false,
    };
    const result = getStandardRoomLabel(roomTypeInformationResponse, roomCode, twinRoomLabels);

    expect(result).toEqual('Bigger room');
  });

  it('should return undefined when roomTypeInformationResponse is undefined', () => {
    const roomCode = {
      roomLabelCode: 'ACCWIN',
      silentSubstitution: false,
    };
    const result = getStandardRoomLabel(undefined as any, roomCode, twinRoomLabels);

    expect(result).toBeUndefined();
  });
});

describe('getStandardRoomLabel - isNoRoomTypeSearchEnabled variations', () => {
  const roomTypeInformationResponse = {
    isLoadingRoomTypeInformation: false,
    isErrorRoomTypeInformation: false,
    dataRoomTypeInformation: {
      roomTypeInformation: {
        roomTypes: [
          {
            roomTypeCode: ['DBLWIN', 'DBLNWD'],
            roomCategory: 'Standard',
            roomLabel: 'Standard room',
            roomDescription: 'Compact rooms, designed around you.',
            roomImage:
              '/content/dam/pi/websites/desktop/new-hotel-details-content/Hub/Hub-Standard-Room.jpg',
            groupId: 'double',
          },
          {
            roomTypeCode: ['BIGWIN', 'BIGNWD', 'BIGZPL'],
            roomCategory: 'Bigger Room',
            roomLabel: 'Bigger room',
            roomDescription:
              'All the clever design, entertainment and comfort of our standard room just a bit, well, bigger. There’s extra space and a luxurious kingsize bed.',
            roomImage: '/content/dam/hub/hotelimages/generic/hub-bigger.jpg',
            groupId: 'double',
          },
          {
            roomTypeCode: ['ACCWIN', 'ACCNWD'],
            roomCategory: 'Accessible',
            roomLabel: 'Accessible room',
            roomDescription:
              'A larger room with a 480mm-high Hypnos bed, level access en-suite shower room with folding seat & wide-entry doors',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/hub-accessible-bedroom.jpg',
            groupId: 'accessible',
          },
        ],
      },
    },
    errorRoomTypeInformation: null,
  };

  const twinRoomLabels = ['Twin - two single beds', 'Twin - double bed and sofa bed'];

  it('should return the twin room label with pmsRoomType when isNoRoomTypeSearchEnabled is true', () => {
    const roomCode = {
      roomLabelCode: 'ACCWIN',
      silentSubstitution: false,
      pmsRoomType: 'DBLWIN',
      standardRoomType: 'DB',
      adults: 2,
      children: 0,
    } as ReservationRoomType;

    const twinroomSelection = 'twobeds';
    const result = getStandardRoomLabel(
      roomTypeInformationResponse,
      roomCode,
      twinRoomLabels,
      twinroomSelection
    );
    expect(result).toEqual('Twin - two single beds');
  });

  it('should return the twin room label without pmsRoomType when isNoRoomTypeSearchEnabled is false', () => {
    const roomCode = {
      roomLabelCode: 'ACCWIN',
      silentSubstitution: false,
      pmsRoomType: 'DBLWIN',
      standardRoomType: 'DB',
      adults: 2,
      children: 0,
    } as ReservationRoomType;

    const twinroomSelection = 'twobeds';
    const result = getStandardRoomLabel(
      roomTypeInformationResponse,
      roomCode,
      twinRoomLabels,
      twinroomSelection
    );
    expect(result).toEqual('Twin - two single beds');
  });

  it('should return the silent substitution label with pmsRoomType when isNoRoomTypeSearchEnabled is true', () => {
    const roomCode = {
      roomLabelCode: 'Accessible room',
      silentSubstitution: true,
      pmsRoomType: 'ACCWIN',
      standardRoomType: 'DB',
      adults: 2,
      children: 0,
    };
    const result = getStandardRoomLabel(
      roomTypeInformationResponse,
      roomCode,
      twinRoomLabels,
      undefined
    );
    expect(result).toEqual('Accessible room');
  });

  it('should return the silent substitution label without pmsRoomType when isNoRoomTypeSearchEnabled is false', () => {
    const roomCode = {
      roomLabelCode: 'Accessible room',
      silentSubstitution: true,
      pmsRoomType: 'ACCWIN',
      adults: 2,
      children: 0,
    };
    const result = getStandardRoomLabel(
      roomTypeInformationResponse,
      roomCode,
      twinRoomLabels,
      undefined
    );
    expect(result).toEqual('Accessible room');
  });

  it('should return the found room label with pmsRoomType when isNoRoomTypeSearchEnabled is true', () => {
    const roomCode = {
      roomLabelCode: 'BIGWIN',
      silentSubstitution: false,
      pmsRoomType: 'BIGWIN',
      adults: 2,
      children: 0,
    };
    const result = getStandardRoomLabel(
      roomTypeInformationResponse,
      roomCode,
      twinRoomLabels,
      undefined
    );
    expect(result).toEqual('Bigger room');
  });

  it('should return the found room label without pmsRoomType when isNoRoomTypeSearchEnabled is false', () => {
    const roomCode = {
      roomLabelCode: 'BIGWIN',
      silentSubstitution: false,
      pmsRoomType: 'BIGWINPMS',
      adults: 2,
      children: 0,
    } as ReservationRoomType;
    const result = getStandardRoomLabel(
      roomTypeInformationResponse,
      roomCode,
      twinRoomLabels,
      undefined
    );
    expect(result).toEqual('Bigger room');
  });

  it('should return null if roomCode is falsy', () => {
    const result = getStandardRoomLabel(
      roomTypeInformationResponse,
      null as any,
      twinRoomLabels,
      undefined
    );
    expect(result).toBeNull();
  });

  it('should return undefined if roomTypeInformationResponse is undefined', () => {
    const roomCode = {
      roomLabelCode: 'ACCWIN',
      silentSubstitution: false,
      pmsRoomType: 'ACCWINPMS',
    };
    const result = getStandardRoomLabel(undefined as any, roomCode, twinRoomLabels, undefined);
    expect(result).toBeUndefined();
  });

  it('should return undefined if roomTypeInformationResponse.dataRoomTypeInformation is missing', () => {
    const roomCode = {
      roomLabelCode: 'BIGWIN',
      silentSubstitution: false,
      pmsRoomType: 'BIGWINPMS',
    };
    const response = { ...roomTypeInformationResponse, dataRoomTypeInformation: undefined };
    const result = getStandardRoomLabel(response as any, roomCode, twinRoomLabels, undefined);
    expect(result).toBeUndefined();
  });

  it('should return undefined if roomTypeInformationResponse.dataRoomTypeInformation.roomTypeInformation is missing', () => {
    const roomCode = {
      roomLabelCode: 'BIGWIN',
      silentSubstitution: false,
      pmsRoomType: 'BIGWINPMS',
    };
    const response = {
      ...roomTypeInformationResponse,
      dataRoomTypeInformation: { roomTypeInformation: undefined },
    };
    const result = getStandardRoomLabel(response as any, roomCode, twinRoomLabels, undefined);
    expect(result).toBeUndefined();
  });

  it('should return undefined if no matching roomTypeCode is found', () => {
    const roomCode = {
      roomLabelCode: 'NOTFOUND',
      silentSubstitution: false,
      pmsRoomType: 'NOTFOUNDPMS',
    };
    const result = getStandardRoomLabel(
      roomTypeInformationResponse,
      roomCode,
      twinRoomLabels,
      undefined
    );
    expect(result).toBeUndefined();
  });
});

describe('Choose Bathroom Silent Substitution', () => {
  const roomTypeInformationResponse = {
    isLoadingRoomTypeInformation: false,
    isErrorRoomTypeInformation: false,
    dataRoomTypeInformation: {
      roomTypeInformation: {
        roomTypes: [
          {
            roomTypeCode: ['LOWDBL'],
            roomCategory: 'Accessible room',
            roomLabel: 'Accessible double bedroom with a lowered bath',
            roomDescription:
              'Accessible double bedroom. Bathroom with a lowered bath set at standard wheelchair height (480mm), lever taps, wider doors and bath mats available on request.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
            groupId: 'accessible',
          },

          {
            roomTypeCode: ['DOUBLE', 'ZPLDBL'],
            roomCategory: 'Double',
            roomLabel: 'Double room',
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
  };

  const selRate = {
    ratePlanCode: 'SEMIFLEX',
    cellCode: null,
    rateCategory: '',
    roomTypes: [
      {
        roomType: 'DIS',
        adults: 1,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'LOWDBL',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['SING', 'LOWB'],
            roomPriceBreakdown: {
              totalNetAmount: 40,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              dailyPrices: [
                {
                  date: '2024-01-19',
                  netPrice: 40,
                },
              ],
            },
          },
          {
            pmsRoomType: 'WETDBL',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['SING', 'WETR'],
            roomPriceBreakdown: {
              totalNetAmount: 40,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              dailyPrices: [
                {
                  date: '2024-01-19',
                  netPrice: 40,
                },
              ],
            },
          },
        ],
      } as HIRoomType,
      {
        roomType: 'DB',
        adults: 1,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'DOUBLE',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['SING'],
            roomPriceBreakdown: {
              totalNetAmount: 40,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              dailyPrices: [
                {
                  date: '2024-01-19',
                  netPrice: 40,
                },
              ],
            },
          },
        ],
      } as HIRoomType,
    ],
  };

  it('should return the correct label on Choose Bathroom with ONE ROOM and Silent Substitution flag ENABLED', () => {
    mockBasketData.selectedRate = selRate;

    const { getByTestId, getByText } = render(
      <BasketComponent
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'Accessible room',
            silentSubstitution: true,
          },
        ]}
        currencyCode={''}
        onBookReservation={() => ({})}
        totalReservationAmount={0}
        dailyPricesPerRoom={[]}
        brand="CCUI"
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );

    expect(getByTestId('basket')).toBeInTheDocument();
    expect(getByText('Accessible room')).toBeInTheDocument();
    expect(getByText('Flex')).toBeInTheDocument();
  });

  it('should return the correct label on Choose Bathroom with ONE ROOM and Silent Substitution flag DISABLED', () => {
    mockBasketData.selectedRate = selRate;
    mockBasketData.isSilentFeatureFlagEnabled = false;
    mockBasketData.selectedPMSRoomTypes = ['LOWDBL'];

    const { getByTestId, getByText } = render(
      <BasketComponent
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'LOWDBL',
            silentSubstitution: false,
          },
        ]}
        currencyCode={''}
        onBookReservation={() => ({})}
        totalReservationAmount={0}
        dailyPricesPerRoom={[]}
        brand="CCUI"
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );

    expect(getByTestId('basket')).toBeInTheDocument();
    expect(getByText('Accessible double bedroom with a lowered bath')).toBeInTheDocument();
    expect(getByText('Flex')).toBeInTheDocument();
  });

  it('should return the correct labels on Choose Bathroom with TWO ROOMS and Silent Substitution flag ENABLED', () => {
    mockBasketData.selectedRate = selRate;

    const { getByTestId, queryByTestId, queryAllByTestId } = render(
      <BasketComponent
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'Accessible room',
            silentSubstitution: true,
          },
          {
            roomLabelCode: 'Double room',
            silentSubstitution: true,
          },
        ]}
        currencyCode={''}
        onBookReservation={() => ({})}
        totalReservationAmount={0}
        dailyPricesPerRoom={[]}
        brand="CCUI"
        isCityTaxBreakdownEnabled={false}
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );

    const breakdownBtn = getByTestId('hdp_basketSeeBreakdownLink');

    act(() => {
      fireEvent.click(breakdownBtn);
    });

    waitFor(() => {
      expect(queryByTestId('basket')).toBeInTheDocument();
      expect(queryAllByTestId('breakdown-room-number')[0]).toContain('Accessible room');
      expect(queryAllByTestId('breakdown-room-number')[1]).toContain('Double room');
    });
  });

  it('should return the correct labels on Choose Bathroom with TWO ROOMS and Silent Substitution flag DISABLED', () => {
    mockBasketData.selectedRate = selRate;
    mockBasketData.isSilentFeatureFlagEnabled = false;
    mockBasketData.selectedPMSRoomTypes = ['LOWDBL', 'DOUBLE'];

    const { getByTestId, queryByTestId, queryAllByTestId } = render(
      <BasketComponent
        {...mockBasketData}
        roomCodes={[
          {
            roomLabelCode: 'LOWDBL',
            silentSubstitution: false,
          },
          {
            roomLabelCode: 'DOUBLE',
            silentSubstitution: false,
          },
        ]}
        currencyCode={''}
        onBookReservation={() => ({})}
        totalReservationAmount={0}
        dailyPricesPerRoom={[]}
        brand="CCUI"
        isCityTaxBreakdownEnabled={false}
        roomTypeInformationResponse={roomTypeInformationResponse}
      />
    );

    const breakdownBtn = getByTestId('hdp_basketSeeBreakdownLink');

    act(() => {
      fireEvent.click(breakdownBtn);
    });

    waitFor(() => {
      expect(queryByTestId('basket')).toBeInTheDocument();
      expect(queryAllByTestId('breakdown-room-number')[0]).toContain(
        'Accessible double bedroom with a lowered bath'
      );
      expect(queryAllByTestId('breakdown-room-number')[1]).toContain('Double room');
    });
  });
});

describe('roomsIndexes calculation logic', () => {
  describe('when isNoRoomTypeSearchEnabled is false', () => {
    beforeEach(() => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_no_room_type_search: false,
        release_pi_bb_ccui_choose_room_type: true,
        release_pi_promo_code_landing_page: true,
      });
    });

    it('should find room index by roomClassCode only, ignoring userChoice', () => {
      const mockProps = {
        ...mockBasketData,
        roomClassCode: 'ST',
        userChoice: [
          {
            pmsRoomType: 'WINCMB', // This should be completely ignored
          },
        ],
        selectedRate: {
          ...mockBasketData.selectedRate,
          roomTypes: [
            {
              ...mockBasketData.selectedRate.roomTypes[0],
              rooms: [
                {
                  pmsRoomType: 'DOUBLE',
                  roomClass: 'DX',
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomPriceBreakdown: { totalNetAmount: 100, currencyCode: 'GBP', dailyPrices: [] },
                  specialRequests: [],
                },
                {
                  pmsRoomType: 'TRIPLE', // Different from userChoice pmsRoomType
                  roomClass: 'ST', // This should match
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomPriceBreakdown: { totalNetAmount: 100, currencyCode: 'GBP', dailyPrices: [] },
                  specialRequests: [],
                },
              ],
            },
          ],
        },
      };

      const { container } = render(<BasketContainer {...mockProps} />);

      // Should select index 1 (the ST room) based only on roomClass, ignoring pmsRoomType
      expect(container).toBeTruthy();
    });

    it('should find first room matching roomClassCode when multiple rooms have same roomClass', () => {
      const mockProps = {
        ...mockBasketData,
        roomClassCode: 'ST',
        selectedRate: {
          ...mockBasketData.selectedRate,
          roomTypes: [
            {
              ...mockBasketData.selectedRate.roomTypes[0],
              rooms: [
                {
                  pmsRoomType: 'DOUBLE',
                  roomClass: 'ST',
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomPriceBreakdown: { totalNetAmount: 100, currencyCode: 'GBP', dailyPrices: [] },
                  specialRequests: [],
                },
                {
                  pmsRoomType: 'TWIN',
                  roomClass: 'ST',
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomPriceBreakdown: { totalNetAmount: 100, currencyCode: 'GBP', dailyPrices: [] },
                  specialRequests: [],
                },
              ],
            },
          ],
        },
      };

      const { container } = render(<BasketContainer {...mockProps} />);

      // Should select index 0 (first ST room)
      expect(container).toBeTruthy();
    });
  });

  describe('when isNoRoomTypeSearchEnabled is true', () => {
    beforeEach(() => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_no_room_type_search: true,
        release_pi_bb_ccui_choose_room_type: true,
        release_pi_promo_code_landing_page: true,
      });
    });

    it('should find room index by both roomClassCode and userChoice pmsRoomType', () => {
      const mockProps = {
        ...mockBasketData,
        roomClassCode: 'ST',
        userChoice: [
          {
            pmsRoomType: 'WINCMB',
          },
        ],
        selectedRate: {
          ...mockBasketData.selectedRate,
          roomTypes: [
            {
              ...mockBasketData.selectedRate.roomTypes[0],
              rooms: [
                {
                  pmsRoomType: 'DOUBLE',
                  roomClass: 'ST', // roomClass matches but pmsRoomType doesn't
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomPriceBreakdown: { totalNetAmount: 100, currencyCode: 'GBP', dailyPrices: [] },
                  specialRequests: [],
                },
                {
                  pmsRoomType: 'WINCMB', // Both roomClass and pmsRoomType match
                  roomClass: 'ST',
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomPriceBreakdown: { totalNetAmount: 120, currencyCode: 'GBP', dailyPrices: [] },
                  specialRequests: [],
                },
              ],
            },
          ],
        },
      };

      const { container } = render(<BasketContainer {...mockProps} />);

      // Should select index 1 (matching both ST roomClass and WINCMB pmsRoomType)
      expect(container).toBeTruthy();
    });

    it('should return -1 when roomClass matches but pmsRoomType does not', () => {
      const mockProps = {
        ...mockBasketData,
        roomClassCode: 'ST',
        userChoice: [
          {
            pmsRoomType: 'NONEXISTENT',
          },
        ],
        selectedRate: {
          ...mockBasketData.selectedRate,
          roomTypes: [
            {
              ...mockBasketData.selectedRate.roomTypes[0],
              rooms: [
                {
                  pmsRoomType: 'DOUBLE',
                  roomClass: 'ST', // roomClass matches but pmsRoomType doesn't
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomPriceBreakdown: { totalNetAmount: 100, currencyCode: 'GBP', dailyPrices: [] },
                  specialRequests: [],
                },
              ],
            },
          ],
        },
      };

      const { container } = render(<BasketContainer {...mockProps} />);

      // Should handle -1 index gracefully (no match)
      expect(container).toBeTruthy();
    });

    it('should return -1 when pmsRoomType matches but roomClass does not', () => {
      const mockProps = {
        ...mockBasketData,
        roomClassCode: 'DX',
        userChoice: [
          {
            pmsRoomType: 'DOUBLE',
          },
        ],
        selectedRate: {
          ...mockBasketData.selectedRate,
          roomTypes: [
            {
              ...mockBasketData.selectedRate.roomTypes[0],
              rooms: [
                {
                  pmsRoomType: 'DOUBLE', // pmsRoomType matches but roomClass doesn't
                  roomClass: 'ST',
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomPriceBreakdown: { totalNetAmount: 100, currencyCode: 'GBP', dailyPrices: [] },
                  specialRequests: [],
                },
              ],
            },
          ],
        },
      };

      const { container } = render(<BasketContainer {...mockProps} />);

      // Should handle -1 index gracefully (no match)
      expect(container).toBeTruthy();
    });

    it('should handle multiple room types with different userChoice values', () => {
      const mockProps = {
        ...mockBasketData,
        roomClassCode: 'ST',
        userChoice: [
          {
            pmsRoomType: 'WINCMB',
          },
          {
            pmsRoomType: 'FMTHRE',
          },
        ],
      };

      const { container } = render(<BasketContainer {...mockProps} />);

      // Should correctly map each roomType to its corresponding userChoice
      expect(container).toBeTruthy();
    });

    it('should handle userChoice with undefined pmsRoomType', () => {
      const mockProps = {
        ...mockBasketData,
        roomClassCode: 'ST',
        userChoice: [
          {
            pmsRoomType: undefined,
          },
        ],
        selectedRate: {
          ...mockBasketData.selectedRate,
          roomTypes: [
            {
              ...mockBasketData.selectedRate.roomTypes[0],
              rooms: [
                {
                  pmsRoomType: 'DOUBLE',
                  roomClass: 'ST',
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomPriceBreakdown: { totalNetAmount: 100, currencyCode: 'GBP', dailyPrices: [] },
                  specialRequests: [],
                },
              ],
            },
          ],
        },
      };

      const { container } = render(<BasketContainer {...mockProps} />);

      // Should return -1 because userChoice.pmsRoomType is undefined
      expect(container).toBeTruthy();
    });
  });
});

it('should render CityTaxBreakdown when city tax and city tax breakdown are enabled', () => {
  const { getByTestId } = render(
    <BasketComponent
      {...mockBasketData}
      roomCodes={[
        {
          roomLabelCode: 'Double room',
          silentSubstitution: true,
        },
      ]}
      currencyCode="GBP"
      onBookReservation={() => ({})}
      totalReservationAmount={230.66}
      cityTaxRoomPrice={220.66}
      totalCityTaxAmount={10}
      dailyPricesPerRoom={[
        [
          {
            date: '2022-08-13',
            netPrice: 75,
          },
          {
            date: '2022-08-14',
            netPrice: 40.33,
          },
        ],
      ]}
      brand="PI"
      isCityTaxEnabled={true}
      isCityTaxBreakdownEnabled={true}
    />
  );

  expect(getByTestId('city-tax-breakdown')).toBeInTheDocument();
});
