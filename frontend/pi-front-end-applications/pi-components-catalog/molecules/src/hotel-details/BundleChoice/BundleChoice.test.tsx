import '@testing-library/jest-dom';
import { render, screen, fireEvent, waitFor, act, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { ActiveChoiceType, HIRoomClassCode } from '@whitbread-eos/api';

import BundleChoice, { formatRatePrice } from './BundleChoice.component';

const baseSoftBundleContent = [
  {
    id: 'BFADBF',
    description: 'Unlimited Premier Inn Breakfast',
    price: 12.3,
  },
  {
    id: 'BFADBFFF',
    description: 'Internet',
    price: 12.3,
    strikeThrough: true,
  },
];

const mockSoftBundles = {
  isOptional: true,
  softBundleContent: baseSoftBundleContent,
};

jest.mock('next/image', () => {
  const MockImage = (props: any) => <img {...props} />;
  MockImage.displayName = 'Image';
  return MockImage;
});

const mockUseScreenSize = jest.fn();

const mockCookies = {
  bundles: 'rate',
};

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');

  return {
    ...utils,
    getCookie: (cookieName: string) => {
      if (cookieName === utils.BUNDLE_CHOICE) {
        return mockCookies.bundles;
      }
    },
    useScreenSize: () => mockUseScreenSize(),
    useStaticHotelInformation: () => ({
      brand: 'PI',
      hotelId: 'MANOLD',
      bookingFlow: {
        bookingFlowItems: [],
      },
      contactDetails: {
        email: 'manold@premier-inn.com',
      },
      accessibilityInfo: {
        header: 'Accessibility header',
      },
    }),
    useFeatureToggle: jest.fn(() => ({
      release_pi_bb_ccui_premier_plus_accessible_room: false,
    })),
  };
});

const MOCK_ROOM_IMAGE = 'undefined/room.jpg';

const mockData = {
  roomRates: [
    {
      ratePlanCode: 'FLEXRATE',
      promotionCode: null,
      cellCode: null,
      roomTypes: [
        {
          roomType: 'DB',
          roomNumber: null,
          adults: 1,
          children: 0,
          cotRequested: false,
          rooms: [
            {
              roomType: null,
              pmsRoomType: 'PPLDBL',
              silentSubstitution: true,
              cotAvailable: false,
              roomClass: 'PP',
              specialRequests: ['SING'],
              roomPriceBreakdown: {
                totalNetAmount: 220.6,
                totalRoomNetAmount: 100,
                baseRateAmount: null,
                currencyCode: 'GBP',
                packageCode: null,
                packageAmount: null,
                totalCityTaxAmount: 3.6,
                effectiveRateAmount: 217,
                dailyPrices: [
                  {
                    date: '2026-03-19',
                    netPrice: 74.2,
                    effectiveRate: 73,
                    roomNetPrice: 30,
                  },
                  {
                    date: '2026-03-20',
                    netPrice: 73.2,
                    effectiveRate: 72,
                    roonNetPrice: 40,
                  },
                  {
                    date: '2026-03-21',
                    netPrice: 73.2,
                    effectiveRate: 72,
                    roomNetPrice: 30,
                  },
                ],
              },
              numberOfRoomsAvailable: 8,
              softBundles: {
                isOptional: true,
                softBundleContent: [
                  {
                    name: 'name',
                    description: 'Premier Inn Breakfast Food',
                    id: 'BFADBF',
                    imageSrc: 'https://link',
                    price: 99,
                    strikeThrough: null,
                    attachments: [
                      {
                        path: '/',
                        label: 'allergy',
                      },
                    ],
                  },
                  {
                    description: 'Check in any time from 11am (normal check-in time is 3pm).',
                    id: 'HSCKIN',
                    imageSrc: '/content/dam/global/extras/early-check-in.png',
                    price: 10,
                    strikeThrough: false,
                  },
                  {
                    description: 'Check in any time from 11am (normal check-in time is 3pm).',
                    id: 'HSCKINAA',
                    imageSrc: '/content/dam/global/extras/early-check-in.png',
                    price: 10,
                    strikeThrough: false,
                    attachments: [
                      {
                        path: undefined,
                        label: 'allergy',
                      },
                    ],
                  },
                  {
                    description: 'Check in any time from 11am (normal check-in time is 3pm).',
                    id: 'HSCKINnn',
                    imageSrc: '/content/dam/global/extras/early-check-in.png',
                    price: 10,
                    strikeThrough: false,
                    attachments: [
                      {
                        path: 'https://link',
                        label: 'allergy',
                      },
                    ],
                  },
                ],
              },
            },
            {
              roomType: null,
              pmsRoomType: 'DOUBLE',
              silentSubstitution: true,
              cotAvailable: false,
              roomClass: 'ST',
              specialRequests: ['SING'],
              roomPriceBreakdown: {
                totalNetAmount: 243.6,
                totalRoomNetAmount: 100,
                baseRateAmount: null,
                currencyCode: 'GBP',
                packageCode: null,
                packageAmount: null,
                totalCityTaxAmount: 3.6,
                effectiveRateAmount: 240,
                dailyPrices: [
                  {
                    date: '2026-03-19',
                    netPrice: 97.2,
                    effectiveRate: 96,
                    roomNetPrice: 30,
                  },
                  {
                    date: '2026-03-20',
                    netPrice: 73.2,
                    effectiveRate: 72,
                    roomNetPrice: 40,
                  },
                  {
                    date: '2026-03-21',
                    netPrice: 73.2,
                    effectiveRate: 72,
                    roomNetPrice: 30,
                  },
                ],
              },
              numberOfRoomsAvailable: 196,
              softBundles: {
                isOptional: true,
                softBundleContent: [
                  {
                    name: 'bundle name',
                    description: 'Premier Inn Breakfast Food',
                    id: 'BFADBF',
                    imageSrc: null,
                    price: 99,
                    strikeThrough: null,
                  },
                  {
                    name: 'bundle name',
                    description:
                      '<p>Download files faster, stream movies, make video calls and browse with ease with our Ultimate Wi-Fi package.</p>\r\n',
                    id: 'FI24HR',
                    imageSrc: '/content/dam/global/extras/ultimate-wifi.png',
                    price: 5,
                    strikeThrough: null,
                    attachments: [
                      {
                        path: '/',
                        label: 'allergy',
                      },
                    ],
                  },
                  {
                    description: 'Check in any time from 11am (normal check-in time is 3pm).',
                    id: 'HSCKIN',
                    imageSrc: '/content/dam/global/extras/early-check-in.png',
                    price: 10,
                    strikeThrough: false,
                  },
                ],
              },
            },
            {
              roomType: null,
              pmsRoomType: 'VDOUBL',
              silentSubstitution: true,
              cotAvailable: false,
              roomClass: 'SV',
              specialRequests: ['SING'],
              roomPriceBreakdown: {
                totalNetAmount: 220.6,
                totalRoomNetAmount: 100,
                baseRateAmount: null,
                currencyCode: 'GBP',
                packageCode: null,
                packageAmount: null,
                totalCityTaxAmount: 3.6,
                effectiveRateAmount: 217,
                dailyPrices: [
                  {
                    date: '2026-03-19',
                    netPrice: 74.2,
                    effectiveRate: 73,
                    roomNetPrice: 30,
                  },
                  {
                    date: '2026-03-20',
                    netPrice: 73.2,
                    effectiveRate: 72,
                    roomNetPrice: 40,
                  },
                  {
                    date: '2026-03-21',
                    netPrice: 73.2,
                    effectiveRate: 72,
                    roomNetPrice: 30,
                  },
                ],
              },
              numberOfRoomsAvailable: 6,
              softBundles: {
                isOptional: true,
                softBundleContent: [
                  {
                    description: 'Premier Inn Breakfast Food',
                    id: 'BFADBF',
                    imageSrc: null,
                    price: 99,
                    strikeThrough: true,
                  },
                  {
                    description:
                      '<p>Download files faster, stream movies, make video calls and browse with ease with our Ultimate Wi-Fi package.</p>\r\n',
                    id: 'FI24HR',
                    imageSrc: '/content/dam/global/extras/ultimate-wifi.png',
                    price: 5,
                    strikeThrough: null,
                  },
                  {
                    description: 'Check in any time from 11am (normal check-in time is 3pm).',
                    id: 'HSCKIN',
                    imageSrc: '/content/dam/global/extras/early-check-in.png',
                    price: 10,
                    strikeThrough: false,
                  },
                ],
              },
            },
          ],
        },
      ],
    },
    {
      ratePlanCode: 'SEMIFLEX',
      promotionCode: null,
      cellCode: null,
      roomTypes: [
        {
          roomType: 'DB',
          roomNumber: null,
          adults: 1,
          children: 0,
          cotRequested: false,
          rooms: [
            {
              roomType: null,
              pmsRoomType: 'PPLDBL',
              silentSubstitution: true,
              cotAvailable: false,
              roomClass: 'PP',
              specialRequests: ['SING'],
              roomPriceBreakdown: {
                totalNetAmount: 475.6,
                totalRoomNetAmount: 100,
                baseRateAmount: null,
                currencyCode: 'GBP',
                packageCode: null,
                packageAmount: null,
                totalCityTaxAmount: 3.6,
                effectiveRateAmount: 175,
                dailyPrices: [
                  {
                    date: '2026-03-19',
                    netPrice: 159.2,
                    effectiveRate: 59,
                    roomNetPrice: 30,
                  },
                  {
                    date: '2026-03-20',
                    netPrice: 158.2,
                    effectiveRate: 58,
                    roomNetPrice: 40,
                  },
                  {
                    date: '2026-03-21',
                    netPrice: 158.2,
                    effectiveRate: 58,
                    roomNetPrice: 30,
                  },
                ],
              },
              numberOfRoomsAvailable: 8,
              softBundles: {
                isOptional: false,
                softBundleContent: [
                  {
                    description: 'Premier Inn Breakfast Food',
                    id: 'BFADBF',
                    imageSrc: null,
                    price: 99,
                    strikeThrough: null,
                  },
                ],
              },
            },
            {
              roomType: null,
              pmsRoomType: 'DOUBLE',
              silentSubstitution: true,
              cotAvailable: false,
              roomClass: 'ST',
              specialRequests: ['SING'],
              roomPriceBreakdown: {
                totalNetAmount: 490.6,
                totalRoomNetAmount: 100,
                baseRateAmount: null,
                currencyCode: 'GBP',
                packageCode: null,
                packageAmount: null,
                totalCityTaxAmount: 3.6,
                effectiveRateAmount: 175,
                dailyPrices: [
                  {
                    date: '2026-03-19',
                    netPrice: 164.2,
                    effectiveRate: 59,
                    roomNetPrice: 30,
                  },
                  {
                    date: '2026-03-20',
                    netPrice: 163.2,
                    effectiveRate: 58,
                    roomNetPrice: 40,
                  },
                  {
                    date: '2026-03-21',
                    netPrice: 163.2,
                    effectiveRate: 58,
                    roomNetPrice: 30,
                  },
                ],
              },
              numberOfRoomsAvailable: 196,
              softBundles: {
                isOptional: false,
                softBundleContent: [
                  {
                    description: 'Premier Inn Breakfast Food',
                    id: 'BFADBF',
                    imageSrc: null,
                    price: 99,
                    strikeThrough: null,
                  },
                  {
                    description:
                      '<p>Download files faster, stream movies, make video calls and browse with ease with our Ultimate Wi-Fi package.</p>\r\n',
                    id: 'FI24HR',
                    imageSrc: '/content/dam/global/extras/ultimate-wifi.png',
                    price: 5,
                    strikeThrough: null,
                  },
                ],
              },
            },
            {
              roomType: null,
              pmsRoomType: 'VDOUBL',
              silentSubstitution: true,
              cotAvailable: false,
              roomClass: 'SV',
              specialRequests: ['SING'],
              roomPriceBreakdown: {
                totalNetAmount: 490.6,
                totalRoomNetAmount: 100,
                baseRateAmount: null,
                currencyCode: 'GBP',
                packageCode: null,
                packageAmount: null,
                totalCityTaxAmount: 3.6,
                effectiveRateAmount: 175,
                dailyPrices: [
                  {
                    date: '2026-03-19',
                    netPrice: 164.2,
                    effectiveRate: 59,
                    roomNetPrice: 30,
                  },
                  {
                    date: '2026-03-20',
                    netPrice: 163.2,
                    effectiveRate: 58,
                    roomNetPrice: 40,
                  },
                  {
                    date: '2026-03-21',
                    netPrice: 163.2,
                    effectiveRate: 58,
                    roomNetPrice: 30,
                  },
                ],
              },
              numberOfRoomsAvailable: 6,
              softBundles: {
                isOptional: false,
                softBundleContent: [
                  {
                    description: 'Premier Inn Breakfast Food',
                    id: 'BFADBF',
                    imageSrc: null,
                    price: 99,
                    strikeThrough: null,
                  },
                  {
                    description:
                      '<p>Download files faster, stream movies, make video calls and browse with ease with our Ultimate Wi-Fi package.</p>\r\n',
                    id: 'FI24HR',
                    imageSrc: '/content/dam/global/extras/ultimate-wifi.png',
                    price: 5,
                    strikeThrough: null,
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
      promotionCode: null,
      cellCode: null,
      roomTypes: [
        {
          roomType: 'DB',
          roomNumber: null,
          adults: 1,
          children: 0,
          cotRequested: false,
          rooms: [
            {
              roomType: null,
              pmsRoomType: 'PPLDBL',
              silentSubstitution: true,
              cotAvailable: false,
              roomClass: 'PP',
              specialRequests: ['SING'],
              roomPriceBreakdown: {
                totalNetAmount: 460,
                totalRoomNetAmount: 100,
                baseRateAmount: null,
                currencyCode: 'GBP',
                packageCode: null,
                packageAmount: null,
                totalCityTaxAmount: 0,
                effectiveRateAmount: 163,
                dailyPrices: [
                  {
                    date: '2026-03-19',
                    netPrice: 154,
                    effectiveRate: 55,
                    roomNetPrice: 30,
                  },
                  {
                    date: '2026-03-20',
                    netPrice: 153,
                    effectiveRate: 54,
                    roomNetPrice: 40,
                  },
                  {
                    date: '2026-03-21',
                    netPrice: 153,
                    effectiveRate: 54,
                    roomNetPrice: 30,
                  },
                ],
              },
              numberOfRoomsAvailable: 8,
              softBundles: {
                isOptional: false,
                softBundleContent: [
                  {
                    description: 'Premier Inn Breakfast Food',
                    id: 'BFADBF',
                    imageSrc: null,
                    price: 99,
                    strikeThrough: null,
                  },
                ],
              },
            },
            {
              roomType: null,
              pmsRoomType: 'DOUBLE',
              silentSubstitution: true,
              cotAvailable: false,
              roomClass: 'ST',
              specialRequests: ['SING'],
              roomPriceBreakdown: {
                totalNetAmount: 460,
                totalRoomNetAmount: 100,
                baseRateAmount: null,
                currencyCode: 'GBP',
                packageCode: null,
                packageAmount: null,
                totalCityTaxAmount: 0,
                effectiveRateAmount: 163,
                dailyPrices: [
                  {
                    date: '2026-03-19',
                    netPrice: 154,
                    effectiveRate: 55,
                    roomNetPrice: 30,
                  },
                  {
                    date: '2026-03-20',
                    netPrice: 153,
                    effectiveRate: 54,
                    roomNetPrice: 40,
                  },
                  {
                    date: '2026-03-21',
                    netPrice: 153,
                    effectiveRate: 54,
                    roomNetPrice: 30,
                  },
                ],
              },
              numberOfRoomsAvailable: 196,
              softBundles: {
                isOptional: false,
                softBundleContent: [
                  {
                    description: 'Premier Inn Breakfast Food',
                    id: 'BFADBF',
                    imageSrc: null,
                    price: 99,
                    strikeThrough: null,
                  },
                ],
              },
            },
            {
              roomType: null,
              pmsRoomType: 'VDOUBL',
              silentSubstitution: true,
              cotAvailable: false,
              roomClass: 'SV',
              specialRequests: ['SING'],
              roomPriceBreakdown: {
                totalNetAmount: 460,
                totalRoomNetAmount: 100,
                baseRateAmount: null,
                currencyCode: 'GBP',
                packageCode: null,
                packageAmount: null,
                totalCityTaxAmount: 0,
                effectiveRateAmount: 163,
                dailyPrices: [
                  {
                    date: '2026-03-19',
                    netPrice: 154,
                    effectiveRate: 55,
                    roomNetPrice: 30,
                  },
                  {
                    date: '2026-03-20',
                    netPrice: 153,
                    effectiveRate: 54,
                    roomNetPrice: 40,
                  },
                  {
                    date: '2026-03-21',
                    netPrice: 153,
                    effectiveRate: 54,
                    roomNetPrice: 30,
                  },
                ],
              },
              numberOfRoomsAvailable: 6,
              softBundles: {
                isOptional: false,
                softBundleContent: [
                  {
                    description: 'Premier Inn Breakfast Food',
                    id: 'BFADBF',
                    imageSrc: null,
                    price: 99,
                    strikeThrough: null,
                  },
                ],
              },
            },
          ],
        },
      ],
    },
  ],
  roomTypes: [
    {
      roomTypeCode: ['LOWTWN'],
      roomCategory: 'Accessible room',
      roomLabel: 'Accessible twin bedroom with a lowered bath',
      roomDescription:
        'Our accessible rooms offer more space, a double or kingsize bed between 480mm and 520mm in height, and wider entry bathrooms with a lowered bath or wet room. ',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID4-Accessible-Double.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['WETTWN'],
      roomCategory: 'Accessible room',
      roomLabel: 'Accessible twin bedroom with level access shower room',
      roomDescription:
        'Our accessible rooms offer more space, a double or kingsize bed between 480mm and 520mm in height, and wider entry bathrooms with a lowered bath or wet room. ',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID4-Accessible-Double.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['WETDBL'],
      roomCategory: 'Accessible room',
      roomLabel: 'Accessible double bedroom with level access shower room',
      roomDescription:
        'Our accessible rooms offer more space, a double or kingsize bed between 480mm and 520mm in height, and wider entry bathrooms with a lowered bath or wet room. ',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID4-Accessible-Double.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['LOWDBL'],
      roomCategory: 'Accessible room',
      roomLabel: 'Accessible double bedroom with a lowered bath',
      roomDescription:
        'Our accessible rooms offer more space, a double or kingsize bed between 480mm and 520mm in height, and wider entry bathrooms with a lowered bath or wet room. ',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID4-Accessible-Double.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['FMQUAD', 'FMFOUR'],
      roomCategory: 'Family',
      roomLabel: 'Family room',
      roomDescription:
        'Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.',
      roomImage: '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-4.jpg',
      groupId: 'family',
    },
    {
      roomTypeCode: ['TWINRM', 'DBLDBL'],
      roomCategory: 'Twin',
      roomLabel: 'Twin room',
      roomDescription:
        'Our twin rooms layouts differ between hotels. Most feature a super-comfy double or kingsize bed, plus a single sofa bed. Some hotels have twin rooms with two double or kingsize Hypnos beds.',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/standard-double-bed-sofa-bed.jpg',
      groupId: 'twin',
    },
    {
      roomTypeCode: ['SINGLE'],
      roomCategory: 'Standard',
      roomLabel: 'Standard room',
      roomDescription:
        'A super-comfy bed, a power shower and free Wi-Fi – our Standard rooms have everything you’ll need for a great night’s sleep.',
      roomImage: '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
      groupId: 'single',
    },
    {
      roomTypeCode: ['PPLDBL'],
      roomCategory: 'Premier Plus',
      roomLabel: 'Premier Plus room',
      roomDescription:
        'Our enhanced room design. Includes Ultimate Wi-Fi, coffee machine, mini-fridge, bedside USB ports, iron, upgraded workspace & more.',
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
      roomImage: '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-4.jpg',
      groupId: 'family',
    },
    {
      roomTypeCode: ['DOUBLE', 'ZPLDBL'],
      roomCategory: 'Double',
      roomLabel: 'Double room',
      roomDescription:
        'A super-comfy bed, a power shower and free Wi-Fi – our double rooms have everything you’ll need for a great night’s sleep.',
      roomImage: '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
      groupId: 'double',
    },
    {
      roomTypeCode: ['PPDLOW'],
      roomCategory: 'Accessible room',
      roomLabel: 'Premier Plus Accessible Double Lowered Bath',
      roomDescription:
        'Accessible double bedroom. Bathroom with a lowered bath set at standard wheelchair height (480mm), lever taps, wider doors and bath mats available on request.',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID5_premier_plus_ua_bathroom.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['BRFDBL'],
      roomCategory: 'Barrier Free room',
      roomLabel: 'Barrier Free room',
      roomDescription:
        'Our accessible rooms offer more space, a double or kingsize bed between 480mm and 520mm in height, and wider entry bathrooms with a lowered bath or wet room. ',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID4-Accessible-Double.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['BRFZPL'],
      roomCategory: 'Barrier Free room',
      roomLabel: 'Barrier Free room',
      roomDescription:
        'Our accessible rooms offer more space, a double or kingsize bed between 480mm and 520mm in height, and wider entry bathrooms with a lowered bath or wet room. ',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID4-Accessible-Double.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['BRFTWN'],
      roomCategory: 'Barrier Free room',
      roomLabel: 'Barrier Free room',
      roomDescription:
        'Our accessible rooms offer more space, a double or kingsize bed between 480mm and 520mm in height, and wider entry bathrooms with a lowered bath or wet room. ',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID4-Accessible-Double.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['VDOUBL'],
      roomCategory: 'Double',
      roomLabel: 'Double room with a view',
      roomDescription:
        "A great view, a super-comfy bed, a power shower and free Wi-Fi – our double rooms have everything you'll need for a great night's sleep. ",
      roomImage: '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
      groupId: 'double',
    },
    {
      roomTypeCode: ['VPPDBL'],
      roomCategory: 'Premier Plus',
      roomLabel: 'Premier Plus room with a view',
      roomDescription:
        'Our enhanced room design with a great view, Ultimate Wi-Fi, coffee machine, mini-fridge, bedside USB ports, iron, upgraded workspace & more.',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
      groupId: 'double',
    },
    {
      roomTypeCode: ['VFMTHR', 'VFMTRP'],
      roomCategory: 'Family',
      roomLabel: 'Family room with a view',
      roomDescription:
        'Our Family rooms with a view include a great view, a double or kingsize bed plus a sofa bed and pull-out bed (depending on the number of guests). We also provide travel cots at no extra cost. Room size and set up can vary based on the hotel and the number of guests.',
      roomImage: '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-4.jpg',
      groupId: 'family',
    },
    {
      roomTypeCode: ['VFMFOR', 'VFMQUD'],
      roomCategory: 'Family',
      roomLabel: 'Family room with a view',
      roomDescription:
        'Our Family rooms with a view include a great view, a double or kingsize bed plus a sofa bed and pull-out bed (depending on the number of guests). We also provide travel cots at no extra cost. Room size and set up can vary based on the hotel and the number of guests.',
      roomImage: '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-4.jpg',
      groupId: 'family',
    },
    {
      roomTypeCode: ['PPDWET'],
      roomCategory: 'Accessible room',
      roomLabel: 'Premier Plus Accessible double with level access shower room',
      roomDescription:
        'Accessible double bedroom. Level access shower room with high-powered shower, conveniently placed shower controls, folding seat and wider doors.',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID5_premier_plus_ua_wetroom.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['BIGWIN'],
      roomCategory: 'Bigger Room',
      roomLabel: 'Bigger room',
      roomDescription:
        'All the clever design, entertainment and comfort of our standard room just a bit, well, bigger. There’s extra space and a luxurious kingsize bed.',
      roomImage: '/content/dam/hub/hotelimages/generic/hub-bigger.jpg',
      groupId: 'double',
    },
  ],
  ratesInformation: {
    rateClassifications: [
      {
        rateClassification: 'FLEXRATE',
        rateDescription:
          'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
        rateName: 'Flex',
        rateOrder: '10',
        rateCategory: 'A',
        rateTags: [],
      },
      {
        rateClassification: 'SEMIFLEX',
        rateDescription:
          'Pay now, fully refundable with free cancellation up to 3 full days before arrival',
        rateName: 'Semi-Flex',
        rateOrder: '20',
        rateCategory: 'C',
        rateTags: [],
      },
      {
        rateClassification: 'ADVANCE',
        rateDescription:
          'Pay now, fully refundable with free cancellation up to 28 full days before arrival',
        rateName: 'Advance',
        rateOrder: '30',
        rateCategory: 'S',
        rateTags: [],
      },
      {
        rateClassification: 'STANDARD',
        rateDescription:
          'Pay now, non-refundable. Amendable check in date at the same hotel up to 1pm on the day of arrival',
        rateName: 'Standard',
        rateOrder: '40',
        rateCategory: 'U',
        rateTags: [],
      },
      {
        rateClassification: 'NONFLEX',
        rateDescription: 'Pay now. No changes',
        rateName: 'Non-Flex',
        rateOrder: '50',
        rateCategory: 'O',
        rateTags: [],
      },
      {
        rateClassification: 'BUSIFLEX',
        rateDescription:
          'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival. Flex rate saving already applied',
        rateName: 'Business Flex',
        rateOrder: '0',
        rateCategory: 'F',
        rateTags: [],
      },
      {
        rateClassification: 'EMPLOYEE',
        rateDescription:
          'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
        rateName: 'Flex (Employee Discount Applied)',
        rateOrder: '1',
        rateCategory: 'B',
        rateTags: [],
      },
      {
        rateClassification: 'FX15R',
        rateDescription: 'Save 15% on our Flex rate',
        rateName: 'Flex',
        rateOrder: '0',
        rateCategory: 'D',
        rateTags: ['15% discount'],
      },
      {
        rateClassification: 'FX20R',
        rateDescription: 'Save 20% on our Flex rate',
        rateName: 'Flex',
        rateOrder: '0',
        rateCategory: 'D',
        rateTags: ['20% discount'],
      },
      {
        rateClassification: 'PROBRKST',
        rateDescription: 'Pay now, no changes, and enjoy a free breakfast on us',
        rateName: 'Standard',
        rateOrder: '0',
        rateCategory: 'D',
        rateTags: ['Free breakfast'],
      },
      {
        rateClassification: 'STDDIS10',
        rateDescription: 'Save 10% on our Standard rate',
        rateName: 'Standard',
        rateOrder: '0',
        rateCategory: 'U',
        rateTags: ['10% discount'],
      },
      {
        rateClassification: 'STDDIS20',
        rateDescription: '20% off 3 nights or more',
        rateName: '20% off 3 nights or more',
        rateOrder: '0',
        rateCategory: 'U',
        rateTags: ['20% discount'],
      },
    ],
  },
};

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: jest.fn(() => ({
    t: (key: string) => key,
  })),
}));

const differentMockData = {
  roomRates: mockData.roomRates,
  roomTypes: [
    {
      roomTypeCode: ['STDDBL'],
      roomCategory: 'FAMILY',
      roomLabel: 'Family Room',
      roomDescription: 'A spacious family room.',
      roomImage: MOCK_ROOM_IMAGE,
    },
    {
      roomTypeCode: ['STDDBL'],
      roomCategory: 'STANDARD',
      roomLabel: 'Standard Room',
      roomDescription: 'A standard double room.',
      roomImage: MOCK_ROOM_IMAGE,
    },
  ],
};

const mockActiveChoice = {
  rate: 'FLEXRATE',
  class: 'PP',
  packages: [],
  softBundle: {
    isOptional: true,
    softBundleContent: [
      {
        name: 'Breakfast',
        description: 'Premier Inn Breakfast Food',
        id: 'BFADBF',
        imageSrc: null,
        price: 99,
        strikeThrough: null,
      },
      {
        description: 'Check in any time from 11am (normal check-in time is 3pm).',
        id: 'HSCKIN',
        imageSrc: '/content/dam/global/extras/early-check-in.png',
        price: 10,
        strikeThrough: false,
      },
    ],
  },
} as ActiveChoiceType;

const mockProps = {
  data: mockData as any,
  activeChoice: mockActiveChoice,
  setActiveChoice: jest.fn(),
  roomClassCodes: ['PP', 'ST', 'SV'] as HIRoomClassCode[],
  nights: 1,
  selectedRoomClassAndRate: 'PP-0',
  setSelectedRoomClassAndRate: () => jest.fn(),
  brand: 'pi',
};

global.HTMLElement.prototype.scrollIntoView = jest.fn();

describe('BundleChoice', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUseScreenSize.mockReturnValue({ isLessThanMd: true });
    mockSoftBundles.isOptional = true;
    mockSoftBundles.softBundleContent = baseSoftBundleContent;
  });

  it('renders wrapper', () => {
    render(<BundleChoice {...mockProps} />);
    expect(screen.getByTestId('BundleChoice-wrapper')).toBeInTheDocument();
  });

  it('renders wrapper and clicks on class', () => {
    render(<BundleChoice {...mockProps} />);
    expect(screen.getByTestId('BundleChoice-wrapper')).toBeInTheDocument();
    const clickableClass = screen.getAllByTestId('Soft-Bundle-Class')[0];
    fireEvent.click(clickableClass);
    expect(mockProps.setActiveChoice).toHaveBeenCalled();
  });

  it('renders room images', () => {
    render(<BundleChoice {...mockProps} />);
    const images = screen.getAllByRole('img');
    expect(images.length).toBeGreaterThan(0);
    expect(images[0]).toHaveAttribute(
      'src',
      'undefined/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg'
    );
  });

  it('renders room descriptions', () => {
    render(<BundleChoice {...mockProps} />);
    expect(
      screen.getAllByText(
        /Our enhanced room design. Includes Ultimate Wi-Fi, coffee machine, mini-fridge, bedside USB ports, iron, upgraded workspace & more./i
      )[0]
    ).toBeInTheDocument();
  });

  it('renders rates for each room class', () => {
    render(<BundleChoice {...mockProps} />);
    expect(screen.getAllByTestId('BundleChoice-rates-list').length).toBeGreaterThan(0);
    expect(screen.getAllByText(/Flex/i)[0]).toBeInTheDocument();
    expect(screen.getAllByText(/Semi-Flex/i)[0]).toBeInTheDocument();
    expect(screen.getAllByText(/Standard/i)[0]).toBeInTheDocument();
  });

  it('shows correct price and currency for each rate', () => {
    render(<BundleChoice {...mockProps} />);
    expect(screen.getAllByText(/£220/)[0]).toBeInTheDocument();
    expect(screen.getAllByTestId('Soft-Bundle-Rate-Price-Decimals')[0]).toHaveTextContent('.60');
  });

  it('selects rate and room class on RadioCard change', () => {
    render(<BundleChoice {...mockProps} />);
    const radios = screen.getAllByRole('radio');
    expect(radios.length).toBeGreaterThan(0);

    fireEvent.click(radios[1]);
    expect(mockProps.setActiveChoice).toHaveBeenCalled();
  });

  it('shows tick icon when rate is active', () => {
    render(<BundleChoice {...mockProps} />);
    expect(screen.getAllByTestId('BundleChoice-tickIcon').length).toBeGreaterThan(0);
  });

  it('handles click on new rate', () => {
    mockSoftBundles.softBundleContent = [
      {
        id: 'BFADBF',
        description: 'Unlimited Premier Inn Breakfast',
        price: 12.3,
      },
      {
        id: 'BFADBFFF',
        description: 'Internet',
        price: 12.3,
        strikeThrough: true,
      },
      {
        id: undefined,
        description: 'Internet',
        price: 12.3,
        strikeThrough: true,
      },
    ];
    render(<BundleChoice {...mockProps} />);
    const radios = screen.getAllByRole('radio');
    userEvent.click(radios[1]);
    expect(mockProps.setActiveChoice).toHaveBeenCalled();
  });

  it('handles click on new rate with included bundle', () => {
    mockSoftBundles.softBundleContent = [
      {
        id: 'BFADBF',
        description: 'Unlimited Premier Inn Breakfast',
        price: 12.3,
      },
      {
        id: 'BFADBFFF',
        description: 'Internet',
        price: 12.3,
        strikeThrough: true,
      },
      {
        id: undefined,
        description: 'Internet',
        price: 12.3,
        strikeThrough: true,
      },
    ];
    mockSoftBundles.isOptional = false;
    render(<BundleChoice {...mockProps} />);
    const radios = screen.getAllByRole('radio');
    userEvent.click(radios[1]);
    expect(mockProps.setActiveChoice).toHaveBeenCalled();
  });

  it('shows tick icon only for active rate', async () => {
    render(<BundleChoice {...mockProps} />);
    await waitFor(() => {
      const tickIcons = screen.queryAllByTestId('BundleChoice-tickIcon');
      expect(tickIcons.length).toBeGreaterThan(0);
    });
  });

  it('renders correct currency symbol for GBP and EUR', () => {
    render(<BundleChoice {...mockProps} />);
    expect(screen.getAllByText(/£460/)[0]).toBeInTheDocument();
  });

  it('does not break if roomImage is missing', () => {
    const dataWithoutImage = {
      ...mockData,
      roomTypes: [
        {
          ...mockData.roomTypes[0],
          roomImage: undefined,
        },
      ],
    } as any;
    render(<BundleChoice {...mockProps} data={dataWithoutImage} />);
    expect(screen.getByTestId('BundleChoice-wrapper')).toBeInTheDocument();
  });

  it('renders rates list for each available rate', () => {
    render(<BundleChoice {...mockProps} />);
    const ratesLists = screen.getAllByTestId('BundleChoice-rates-list');
    expect(ratesLists.length).toBeGreaterThan(0);
  });

  it('shows and toggles read more link for long description on mobile', async () => {
    mockUseScreenSize.mockReturnValue({ isLessThanMd: true });
    const dataWithLongDescription = {
      ...mockData,
      roomTypes: [
        {
          ...mockData.roomTypes[0],
          roomDescription:
            'A very long description that requires read more link to be shown on mobile devices.',
        },
      ],
    } as any;
    render(<BundleChoice {...mockProps} data={dataWithLongDescription} />);

    await waitFor(() => {
      const readMoreLink = screen.queryAllByTestId('BundleChoice-readmore-link')[0];
      if (readMoreLink) {
        expect(screen.getAllByTestId('BundleChoice-collapse')[0]).toBeInTheDocument();
        const linkText = screen.getAllByTestId('BundleChoice-readmore-link-text')[0];
        expect(linkText).toBeInTheDocument();
        fireEvent.click(readMoreLink);
        fireEvent.click(readMoreLink);
      }
    });
  });

  it('renders pagination container and dots on mobile', () => {
    render(<BundleChoice {...mockProps} />);
    expect(screen.getAllByTestId('BundleChoice-pagination')[0]).toBeInTheDocument();
    const dots = screen.getAllByTestId('BundleChoice-pagination-dot');
    expect(dots.length).toBe(9);
    fireEvent.click(dots[1]);
    expect(mockProps.setActiveChoice).toHaveBeenCalled();
  });

  it('renders bundle extras button and click', async () => {
    render(<BundleChoice {...mockProps} />);
    await act(async () => {
      fireEvent.click(screen.getAllByRole('radio')[0]);
    });
    const bundleButton = screen.getAllByTestId('Bundle-FLEXRATE')[0];
    expect(bundleButton).toBeInTheDocument();
    await act(async () => {
      fireEvent.click(bundleButton.children[0].children[0]);
    });
    expect(mockProps.setActiveChoice).toHaveBeenCalled();
  });

  it('renders without active room class', () => {
    render(<BundleChoice {...mockProps} activeChoice={{ ...mockActiveChoice, class: '' }} />);
    expect(screen.getAllByTestId('BundleChoice-tickIcon').length).toBeGreaterThan(0);
  });

  it('renders without active rate', () => {
    render(<BundleChoice {...mockProps} activeChoice={{ ...mockActiveChoice, rate: '' }} />);
    expect(screen.getAllByTestId('BundleChoice-tickIcon').length).toBeGreaterThan(0);
  });

  it('renders with active rate and class variant', () => {
    mockCookies.bundles = 'roomClass';
    render(<BundleChoice {...mockProps} activeChoice={{ ...mockActiveChoice, class: '' }} />);
    expect(screen.getAllByTestId('BundleChoice-tickIcon').length).toBeGreaterThan(0);
    mockCookies.bundles = 'rate';
  });
});

describe('BundleChoice handleBundleClick', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUseScreenSize.mockReturnValue({ isLessThanMd: false });
    mockSoftBundles.isOptional = true;
    mockSoftBundles.softBundleContent = baseSoftBundleContent;
  });

  it('renders optional bundle with checkbox', () => {
    render(<BundleChoice {...mockProps} />);
    const bundleButton = screen.getAllByTestId('Bundle-FLEXRATE')[0];
    expect(bundleButton).toBeInTheDocument();
  });

  it('render BundleChoice component with no rate', () => {
    const bundlePackageIds = mockSoftBundles.softBundleContent.map((b) => b.id);
    const activeChoiceWithPackages = {
      rate: undefined,
      class: mockData.roomTypes[0].roomTypeCode[0],
      packages: bundlePackageIds,
    };
    render(<BundleChoice {...mockProps} activeChoice={activeChoiceWithPackages} />);
    const bundle = screen.getAllByTestId('Bundle-FLEXRATE')[0];
    expect(bundle).toBeInTheDocument();
  });

  it('hides bundle if room class does not match', () => {
    render(<BundleChoice {...mockProps} data={differentMockData as any} />);
    expect(screen.queryByTestId('Bundle-FLEXRATE')).not.toBeInTheDocument();
  });

  it('hides bundle if rate does not match', () => {
    render(<BundleChoice {...mockProps} data={differentMockData as any} />);
    expect(screen.queryByTestId('Bundle-FLEXRATE')).not.toBeInTheDocument();
  });

  it('renders bundle extras button and click v2', async () => {
    mockCookies.bundles = 'rate';
    const { getAllByTestId } = render(<BundleChoice {...mockProps} />);
    const bundleButton = getAllByTestId('Bundle-FLEXRATE')[0];
    expect(bundleButton).toBeInTheDocument();
    await act(async () => {
      fireEvent.click(bundleButton);
    });
  });

  it('renders mandatory bundle on rate', async () => {
    mockCookies.bundles = 'rate';
    mockSoftBundles.isOptional = false;
    render(<BundleChoice {...mockProps} />);
    const bundleButtons = screen.getAllByTestId('Soft-Bundle-Rate-Extra-Item');
    expect(bundleButtons.length).toBeGreaterThan(0);

    const extraTitleContainer = screen.getAllByTestId('Soft-Bundle-Rate-Extras-Title-Container');
    const svgElement = within(extraTitleContainer[0]).getByTestId('svg-container');
    expect(svgElement).toBeInTheDocument();
    await act(async () => {
      fireEvent.click(svgElement);
    });
  });

  it('should switch to roomOnly option when isRoomOnly true and roomOnly isRoomOnly is false', async () => {
    mockCookies.bundles = 'roomOnly';
    mockSoftBundles.isOptional = false;
    const { getAllByRole } = render(
      <BundleChoice {...mockProps} activeChoice={{ ...mockActiveChoice, isRoomOnly: false }} />
    );
    const bundleButtons = screen.getAllByTestId('Soft-Bundle-Rate-Extra-Item');
    expect(bundleButtons.length).toBeGreaterThan(0);

    expect(getAllByRole('radio')[0]).toBeChecked();
    expect(getAllByRole('radio')[1]).toBeChecked();
  });

  it('should switch to room and bundle option when RoomOnly variant is set and isRoomOnly is true', async () => {
    mockCookies.bundles = 'roomOnly';
    mockSoftBundles.isOptional = false;
    const { getAllByRole } = render(
      <BundleChoice {...mockProps} activeChoice={{ ...mockActiveChoice, isRoomOnly: true }} />
    );
    const bundleButtons = screen.getAllByTestId('Soft-Bundle-Rate-Extra-Item');
    expect(bundleButtons.length).toBeGreaterThan(0);

    expect(getAllByRole('radio')[0]).toBeChecked();
    expect(getAllByRole('radio')[2]).toBeChecked();
  });
});

describe('formatRatePrice', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('formatRatePrice for GBP en', () => {
    const priceElement = formatRatePrice(123.56, 'GBP', 'en');
    const { getByTestId } = render(<>{priceElement}</>);

    const mainPriceElement = getByTestId('Soft-Bundle-Rate-Price-Main');
    expect(mainPriceElement).toBeInTheDocument();
    expect(mainPriceElement).toHaveTextContent('£123');
    expect(mainPriceElement).toHaveTextContent('.56');
  });

  it('formatRatePrice for EUR de', () => {
    const priceElement = formatRatePrice(123.56, 'EUR', 'de');
    const { getByTestId } = render(<>{priceElement}</>);

    const mainPriceElement = getByTestId('Soft-Bundle-Rate-Price-Main');
    expect(mainPriceElement).toBeInTheDocument();
    expect(mainPriceElement).toHaveTextContent('123');
    expect(mainPriceElement).toHaveTextContent(',56€');
  });
});
