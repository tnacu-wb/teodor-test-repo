import '@testing-library/jest-dom';
import type { Channel, HIRoomClass, HIRoomRate } from '@whitbread-eos/api';
import { ROOM_TYPE } from '@whitbread-eos/api';
import { isIVMEnabled, useFeatureToggle } from '@whitbread-eos/utils';
import React from 'react';

import { render, screen, userEvent } from '../../utils/test-utils';
import RateCard from './RateCard.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  isIVMEnabled: jest.fn(),
  useFeatureToggle: jest.fn(() => ({
    release_pi_web_push_notifications: false,
    release_pi_bb_ccui_premier_plus_accessible_room: true,
  })),
}));

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const roomRates = [
  {
    ratePlanCode: 'FLEXRATE',
    rateCategory: '',
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

const roomRatesWithNonSilentSubstitution: HIRoomRate[] = [
  {
    ratePlanCode: 'FLEXRATE',
    promotionCode: undefined,
    cellCode: null,
    rateCategory: '',
    roomTypes: [
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
            numberOfRoomsAvailable: 1,
            softBundles: {},
            roomPriceBreakdown: {
              totalNetAmount: 90,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              effectiveRateAmount: 90,
              totalCityTaxAmount: 0,
              dailyPrices: [
                {
                  date: '2024-12-18',
                  netPrice: 90,
                  effectiveRate: 90,
                },
              ],
            },
          },
          {
            pmsRoomType: 'WETDBL',
            silentSubstitution: false,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['SING'],
            numberOfRoomsAvailable: 1,
            softBundles: {},
            roomPriceBreakdown: {
              totalNetAmount: 90,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              effectiveRateAmount: 90,
              totalCityTaxAmount: 0,
              dailyPrices: [
                {
                  date: '2024-12-18',
                  netPrice: 90,
                  effectiveRate: 90,
                },
              ],
            },
          },
        ],
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
            numberOfRoomsAvailable: 1,
            softBundles: {},
            roomPriceBreakdown: {
              totalNetAmount: 90,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              effectiveRateAmount: 90,
              totalCityTaxAmount: 0,
              dailyPrices: [
                {
                  date: '2024-12-18',
                  netPrice: 90,
                  effectiveRate: 90,
                },
              ],
            },
          },
          {
            pmsRoomType: 'WETDBL',
            silentSubstitution: false,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['SING'],
            numberOfRoomsAvailable: 1,
            softBundles: {},
            roomPriceBreakdown: {
              totalNetAmount: 90,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              effectiveRateAmount: 90,
              totalCityTaxAmount: 0,
              dailyPrices: [
                {
                  date: '2024-12-18',
                  netPrice: 90,
                  effectiveRate: 90,
                },
              ],
            },
          },
        ],
      },
      {
        roomType: 'TWIN',
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
            numberOfRoomsAvailable: 1,
            softBundles: {},
            roomPriceBreakdown: {
              totalNetAmount: 90,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              effectiveRateAmount: 90,
              totalCityTaxAmount: 0,
              dailyPrices: [
                {
                  date: '2024-12-18',
                  netPrice: 90,
                  effectiveRate: 90,
                },
              ],
            },
          },
          {
            pmsRoomType: 'TWINRM',
            silentSubstitution: false,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['SING'],
            numberOfRoomsAvailable: 1,
            softBundles: {},
            roomPriceBreakdown: {
              totalNetAmount: 90,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              effectiveRateAmount: 90,
              totalCityTaxAmount: 0,
              dailyPrices: [
                {
                  date: '2024-12-18',
                  netPrice: 90,
                  effectiveRate: 90,
                },
              ],
            },
          },
        ],
      },
    ],
  },
];

const rateCardProps = {
  brand: 'pi',
  channel: 'PI' as Channel,
  roomClassIndex: 0,
  roomClass: 'Standard Room' as HIRoomClass,
  pmsRoomType: ROOM_TYPE.DOUBLE,
  roomRates: [...roomRates],
  selectedRoomClassAndRate: '',
  setSelectedRoomClassAndRate: jest.fn(),
  rateClassifications: [
    {
      ratePlanCode: 'FLEXRATE',
      rateClassification: 'FLEXRATE',
      rateCategory: '',
      rateDescription:
        'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
      rateName: 'Flex',
      rateOrder: '1',
    },
    {
      ratePlanCode: 'STANDARD',
      rateClassification: 'STANDARD',
      rateCategory: '',
      rateDescription:
        'Pay now, non-refundable. Amendable check in date at the same hotel up to 1pm on the day of arrival',
      rateName: 'Standard',
      rateOrder: '5',
    },
  ],
  roomTypeInformationResponse: {
    isLoadingRoomTypeInformation: false,
    isErrorRoomTypeInformation: false,
    errorRoomTypeInformation: null,
    dataRoomTypeInformation: {
      roomTypeInformation: {
        roomTypes: [
          {
            roomCategory: 'Double',
            roomDescription:
              'A super-comfy Hypnos bed, a power shower and free Wi-Fi – our double rooms have everything you’ll need for a great night’s sleep.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
            roomLabel: 'Double Room',
            roomTypeCode: [ROOM_TYPE.DOUBLE],
            groupId: 'double',
          },
        ],
      },
    },
  },
  isLessThanSm: false,
  isLessThanMd: false,
  roomRatesThatMatchRoomClassifications: [...roomRates],
  roomClassCode: 'ST',
};

const rateCardWithNonSilentSubstitutionProps = {
  brand: 'pi',
  channel: 'PI' as Channel,
  roomClassIndex: 0,
  roomClass: 'Standard Room' as HIRoomClass, //
  pmsRoomType: ROOM_TYPE.WET_DOUBLE, //
  roomRates: [...roomRatesWithNonSilentSubstitution],
  selectedRoomClassAndRate: '',
  setSelectedRoomClassAndRate: jest.fn(),
  rateClassifications: [
    {
      ratePlanCode: 'FLEXRATE',
      rateClassification: 'FLEXRATE',
      rateCategory: '',
      rateDescription:
        'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
      rateName: 'Flex',
      rateOrder: '10',
    },
    {
      ratePlanCode: 'STANDARD',
      rateClassification: 'STANDARD',
      rateCategory: '',
      rateDescription:
        'Pay now, non-refundable. Amendable check in date at the same hotel up to 1pm on the day of arrival',
      rateName: 'Standard',
      rateOrder: '20',
    },
  ],
  roomTypeInformationResponse: {
    isLoadingRoomTypeInformation: false,
    isErrorRoomTypeInformation: false,
    errorRoomTypeInformation: null,
    dataRoomTypeInformation: {
      roomTypeInformation: {
        roomTypes: [
          {
            roomTypeCode: [ROOM_TYPE.DOUBLE],
            roomCategory: 'Double',
            roomLabel: 'Double Room',
            roomDescription:
              'A super-comfy Hypnos bed, a power shower and free Wi-Fi – our double rooms have everything you’ll need for a great night’s sleep.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
            groupId: 'double',
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
            roomTypeCode: ['PPLDBL', 'PDBZPL'],
            roomCategory: 'Premier Plus',
            roomLabel: 'Premier Plus room',
            roomDescription:
              'Our enhanced room design. Includes Ultimate Wi-Fi, coffee machine, mini-fridge, bedside USB ports, iron, upgraded workspace & more.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
            groupId: 'double',
          },
          {
            roomTypeCode: ['TWINRM', 'DBLDBL'],
            roomCategory: 'Twin',
            roomLabel: 'Twin room',
            roomDescription:
              'Our twin rooms layouts differ between hotels. Most feature a super-comfy double or kingsize bed, plus a single sofa bed. Some hotels have twin rooms with two double or kingsize Hypnos beds.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/id4-light/ID4-light-room2.1.jpg',
            groupId: 'twin',
          },
        ],
      },
    },
  },
  isLessThanSm: false,
  isLessThanMd: false,
  roomRatesThatMatchRoomClassifications: [...roomRatesWithNonSilentSubstitution],
  roomClassCode: 'ST', // selected rate class
};

const singleRoomTypeRateCardProps = {
  ...rateCardProps,
  roomRates: [
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
          ],
        },
      ],
    } as HIRoomRate,
  ],
  numberOfNights: 4,
  numberOfUnits: 1,
  roomTypes: ['DB'],
  showAdditionalInfo: true,
};

const multipleRoomsRateCardProps = {
  ...rateCardProps,
  roomRates: [
    {
      ratePlanCode: 'FLEXRATE',
      roomTypes: [
        {
          roomType: 'DB',
          adults: 1,
          children: 0,
          cotRequested: false,
          rooms: [
            {
              pmsRoomType: ROOM_TYPE.STANDARD,
              silentSubstitution: true,
              cotAvailable: false,
              roomClass: 'ST',
              roomPriceBreakdown: {
                totalNetAmount: 544,
                currencyCode: 'GBP',
                dailyPrices: [
                  {
                    date: '2022-12-25',
                    netPrice: 136,
                  },
                  {
                    date: '2022-12-26',
                    netPrice: 136,
                  },
                  {
                    date: '2022-12-27',
                    netPrice: 136,
                  },
                  {
                    date: '2022-12-28',
                    netPrice: 136,
                  },
                ],
              },
              specialRequests: ['DBLE'],
            },
            {
              pmsRoomType: ROOM_TYPE.STANDARD_BIGGER,
              silentSubstitution: false,
              cotAvailable: false,
              roomClass: 'BG',
              roomPriceBreakdown: {
                totalNetAmount: 564,
                currencyCode: 'GBP',
                dailyPrices: [
                  {
                    date: '2022-12-25',
                    netPrice: 141,
                  },
                  {
                    date: '2022-12-26',
                    netPrice: 141,
                  },
                  {
                    date: '2022-12-27',
                    netPrice: 141,
                  },
                  {
                    date: '2022-12-28',
                    netPrice: 141,
                  },
                ],
              },
              specialRequests: ['DBLE'],
            },
          ],
        },
        {
          roomType: 'DB',
          adults: 1,
          children: 0,
          cotRequested: false,
          rooms: [
            {
              pmsRoomType: ROOM_TYPE.STANDARD,
              silentSubstitution: true,
              cotAvailable: false,
              roomClass: 'ST',
              roomPriceBreakdown: {
                totalNetAmount: 544,
                currencyCode: 'GBP',
                dailyPrices: [
                  {
                    date: '2022-12-25',
                    netPrice: 136,
                  },
                  {
                    date: '2022-12-26',
                    netPrice: 136,
                  },
                  {
                    date: '2022-12-27',
                    netPrice: 136,
                  },
                  {
                    date: '2022-12-28',
                    netPrice: 136,
                  },
                ],
              },
              specialRequests: ['DBLE'],
            },
            {
              pmsRoomType: ROOM_TYPE.STANDARD_BIGGER,
              silentSubstitution: false,
              cotAvailable: false,
              roomClass: 'BG',
              roomPriceBreakdown: {
                totalNetAmount: 564,
                currencyCode: 'GBP',
                dailyPrices: [
                  {
                    date: '2022-12-25',
                    netPrice: 141,
                  },
                  {
                    date: '2022-12-26',
                    netPrice: 141,
                  },
                  {
                    date: '2022-12-27',
                    netPrice: 141,
                  },
                  {
                    date: '2022-12-28',
                    netPrice: 141,
                  },
                ],
              },
              specialRequests: ['DBLE'],
            },
          ],
        },
      ],
    } as HIRoomRate,
    {
      ratePlanCode: 'STANDARD',
      roomTypes: [
        {
          roomType: 'DB',
          adults: 1,
          children: 0,
          cotRequested: false,
          rooms: [
            {
              pmsRoomType: ROOM_TYPE.STANDARD,
              silentSubstitution: true,
              cotAvailable: false,
              roomClass: 'ST',
              roomPriceBreakdown: {
                totalNetAmount: 448,
                currencyCode: 'GBP',
                dailyPrices: [
                  {
                    date: '2022-12-25',
                    netPrice: 112,
                  },
                  {
                    date: '2022-12-26',
                    netPrice: 112,
                  },
                  {
                    date: '2022-12-27',
                    netPrice: 112,
                  },
                  {
                    date: '2022-12-28',
                    netPrice: 112,
                  },
                ],
              },
              specialRequests: ['DBLE'],
            },
            {
              pmsRoomType: ROOM_TYPE.STANDARD_BIGGER,
              silentSubstitution: false,
              cotAvailable: false,
              roomClass: 'BG',
              roomPriceBreakdown: {
                totalNetAmount: 468,
                currencyCode: 'GBP',
                dailyPrices: [
                  {
                    date: '2022-12-25',
                    netPrice: 117,
                  },
                  {
                    date: '2022-12-26',
                    netPrice: 117,
                  },
                  {
                    date: '2022-12-27',
                    netPrice: 117,
                  },
                  {
                    date: '2022-12-28',
                    netPrice: 117,
                  },
                ],
              },
              specialRequests: ['DBLE'],
            },
          ],
        },
        {
          roomType: 'DB',
          adults: 1,
          children: 0,
          cotRequested: false,
          rooms: [
            {
              pmsRoomType: ROOM_TYPE.STANDARD,
              silentSubstitution: true,
              cotAvailable: false,
              roomClass: 'ST',
              roomPriceBreakdown: {
                totalNetAmount: 448,
                currencyCode: 'GBP',
                dailyPrices: [
                  {
                    date: '2022-12-25',
                    netPrice: 112,
                  },
                  {
                    date: '2022-12-26',
                    netPrice: 112,
                  },
                  {
                    date: '2022-12-27',
                    netPrice: 112,
                  },
                  {
                    date: '2022-12-28',
                    netPrice: 112,
                  },
                ],
              },
              specialRequests: ['DBLE'],
            },
            {
              pmsRoomType: ROOM_TYPE.STANDARD_BIGGER,
              silentSubstitution: false,
              cotAvailable: false,
              roomClass: 'BG',
              roomPriceBreakdown: {
                totalNetAmount: 468,
                currencyCode: 'GBP',
                dailyPrices: [
                  {
                    date: '2022-12-25',
                    netPrice: 117,
                  },
                  {
                    date: '2022-12-26',
                    netPrice: 117,
                  },
                  {
                    date: '2022-12-27',
                    netPrice: 117,
                  },
                  {
                    date: '2022-12-28',
                    netPrice: 117,
                  },
                ],
              },
              specialRequests: ['DBLE'],
            },
          ],
        },
      ],
    } as HIRoomRate,
  ],
  numberOfNights: 4,
  numberOfUnits: 2,
  roomTypes: ['DB'],
  showAdditionalInfo: false,
};

const fiveRates = [
  {
    ratePlanCode: 'FLEXRATE',
    roomTypes: [
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
              totalNetAmount: 135.0,
              currencyCode: 'GBP',
              dailyPrices: [
                {
                  date: '2023-05-12',
                  netPrice: 135.0,
                },
              ],
            },
          },
        ],
      },
    ],
  } as HIRoomRate,
  {
    ratePlanCode: 'SEMIFLEX',
    roomTypes: [
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
              totalNetAmount: 40.0,
              currencyCode: 'GBP',
              dailyPrices: [
                {
                  date: '2023-05-12',
                  netPrice: 40.0,
                },
              ],
            },
          },
        ],
      },
    ],
  } as HIRoomRate,
  {
    ratePlanCode: 'ADVANCE',
    roomTypes: [
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
              totalNetAmount: 35.0,
              currencyCode: 'GBP',
              dailyPrices: [
                {
                  date: '2023-05-12',
                  netPrice: 35.0,
                },
              ],
            },
          },
        ],
      },
    ],
  } as HIRoomRate,
  {
    ratePlanCode: 'STANDARD',
    roomTypes: [
      {
        roomType: 'DB',
        adults: 1,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'FMTRPL',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['SING'],
            roomPriceBreakdown: {
              totalNetAmount: 30.0,
              currencyCode: 'GBP',
              dailyPrices: [
                {
                  date: '2023-05-12',
                  netPrice: 30.0,
                },
              ],
            },
          },
        ],
      },
    ],
  } as HIRoomRate,
  {
    ratePlanCode: 'NONFLEX',
    roomTypes: [
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
              totalNetAmount: 25.0,
              currencyCode: 'GBP',
              dailyPrices: [
                {
                  date: '2023-05-12',
                  netPrice: 25.0,
                },
              ],
            },
          },
        ],
      },
    ],
  } as HIRoomRate,
];

const fiveRateCardProps = {
  roomClassCode: 'ST',
  brand: 'pi',
  channel: 'BB' as Channel,
  roomClassIndex: 0,
  roomClass: 'Standard Room' as HIRoomClass,
  pmsRoomType: 'DOUBLE',
  roomRates: [...fiveRates],
  selectedRoomClassAndRate: '',
  setSelectedRoomClassAndRate: jest.fn(),
  rateClassifications: [
    {
      ratePlanCode: 'FLEXRATE',
      rateClassification: 'FLEXRATE',
      rateCategory: '',
      rateDescription: 'Flex description',
      rateName: 'Flex Rate',
      rateOrder: '4',
    },
    {
      ratePlanCode: 'NONFLEX',
      rateClassification: 'NONFLEX',
      rateCategory: '',
      rateDescription:
        'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
      rateName: 'Non-Flex Rate',
      rateOrder: '1',
    },
    {
      ratePlanCode: 'SEMIFLEX',
      rateClassification: 'SEMIFLEX',
      rateCategory: '',
      rateDescription: 'Semi-Flex description',
      rateName: 'Semi-Flex Rate',
      rateOrder: '3',
    },
    {
      ratePlanCode: 'ADVANCE',
      rateClassification: 'ADVANCE',
      rateCategory: '',
      rateDescription: 'Advance description',
      rateName: 'Advance Rate',
      rateOrder: '5',
    },
    {
      ratePlanCode: 'STANDARD',
      rateClassification: 'STANDARD',
      rateCategory: '',
      rateDescription:
        'Pay now, non-refundable. Amendable check in date at the same hotel up to 1pm on the day of arrival',
      rateName: 'Standard Rate',
      rateOrder: '3',
    },
  ],
  roomTypeInformationResponse: {
    isLoadingRoomTypeInformation: false,
    isErrorRoomTypeInformation: false,
    errorRoomTypeInformation: null,
    dataRoomTypeInformation: {
      roomTypeInformation: {
        roomTypes: [
          {
            roomCategory: 'Double',
            roomDescription:
              'A super-comfy Hypnos bed, a power shower and free Wi-Fi – our double rooms have everything you’ll need for a great night’s sleep.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
            roomLabel: 'Double Room',
            roomTypeCode: ['DOUBLE'],
            groupId: 'double',
          },
        ],
      },
    },
  },
  isLessThanSm: false,
  isLessThanMd: false,
  roomRatesThatMatchRoomClassifications: [...fiveRates],
  numberOfNights: 4,
  numberOfUnits: 1,
  roomTypes: ['DB'],
  showAdditionalInfo: true,
};

const roomRatesWithbasePrice = [
  {
    ratePlanCode: 'FLEXRATE',
    rateCategory: '',
    roomTypes: [
      {
        roomType: 'DOUBLE',
        adults: 2,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'DOUBLE',
            silentSubstitution: false,
            roomClass: 'ST',
            cotAvailable: false,
            roomPriceBreakdown: {
              totalNetAmount: 115.33,
              baseRateAmount: 30,
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
            pmsRoomType: 'FMLDBL',
            silentSubstitution: false,
            roomClass: 'ST',
            cotAvailable: true,
            roomPriceBreakdown: {
              totalNetAmount: 115.33,
              baseRateAmount: 65,
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
          },
        ],
      },
    ],
  } as HIRoomRate,
];

const rateCardWithBasePriceProps = {
  brand: 'pi',
  channel: 'PI' as Channel,
  roomClassIndex: 0,
  roomClass: 'Standard Room' as HIRoomClass,
  pmsRoomType: ROOM_TYPE.DOUBLE,
  roomRates: roomRatesWithbasePrice,
  selectedRoomClassAndRate: '',
  setSelectedRoomClassAndRate: jest.fn(),
  rateClassifications: [
    {
      ratePlanCode: 'FLEXRATE',
      rateClassification: 'FLEXRATE',
      rateCategory: '',
      rateDescription:
        'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
      rateName: 'Flex',
      rateOrder: '1',
    },
  ],
  roomTypeInformationResponse: {
    isLoadingRoomTypeInformation: false,
    isErrorRoomTypeInformation: false,
    errorRoomTypeInformation: null,
    dataRoomTypeInformation: {
      roomTypeInformation: {
        roomTypes: [
          {
            roomCategory: 'Double',
            roomDescription:
              'A super-comfy Hypnos bed, a power shower and free Wi-Fi – our double rooms have everything you’ll need for a great night’s sleep.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
            roomLabel: 'Double Room',
            roomTypeCode: [ROOM_TYPE.DOUBLE],
            groupId: 'double',
          },
        ],
      },
    },
  },
  isLessThanSm: false,
  isLessThanMd: false,
  roomRatesThatMatchRoomClassifications: roomRatesWithbasePrice,
  roomClassCode: 'ST',
};

describe('RateCard component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should display RateCard component', () => {
    const { getByTestId } = render(<RateCard {...rateCardProps} />);
    expect(getByTestId('hdp_rateCard')).toBeInTheDocument();
  });

  it('should show a loading message if isLoading prop is true', () => {
    const { getByText } = render(
      <RateCard
        {...rateCardProps}
        roomTypeInformationResponse={{
          ...rateCardProps.roomTypeInformationResponse,
          isLoadingRoomTypeInformation: true,
        }}
      />
    );
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should show an error message if isError prop is true', () => {
    const { getByText } = render(
      <RateCard
        {...rateCardProps}
        roomTypeInformationResponse={{
          ...rateCardProps.roomTypeInformationResponse,
          errorRoomTypeInformation: { message: 'Error' },
          isErrorRoomTypeInformation: true,
        }}
      />
    );
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should render room class name - Standard Room', () => {
    (isIVMEnabled as jest.Mock).mockImplementation(() => true);
    const { getByText } = render(<RateCard {...rateCardProps} />);
    expect(getByText('Flex')).toBeInTheDocument();
  });

  it('should render Rate Card with 2 plans', () => {
    const standardRate = {
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
                totalNetAmount: 125.33,
                currencyCode: 'GBP',
                dailyPrices: [
                  {
                    date: '2022-08-13',
                    netPrice: 85.0,
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
                totalNetAmount: 125.33,
                currencyCode: 'GBP',
                dailyPrices: [
                  {
                    date: '2022-08-13',
                    netPrice: 85.0,
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
                totalNetAmount: 125.33,
                currencyCode: 'GBP',
                dailyPrices: [
                  {
                    date: '2022-08-13',
                    netPrice: 85.0,
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
    } as HIRoomRate;

    rateCardProps.roomRates.push(standardRate);
    rateCardProps.roomRatesThatMatchRoomClassifications.push(standardRate);
    const { getByText, getAllByRole } = render(<RateCard {...rateCardProps} />);

    expect(getByText('Flex')).toBeInTheDocument();
    expect(getByText('Standard')).toBeInTheDocument();
    expect(getAllByRole('radio').length).toEqual(2);
  });

  // New rate card display scenarios
  // Scenario 1. User is booking single room with other class except for Standard, i.e: Premier Plus, Bigger Room
  it('should render Rate Card with description', () => {
    const { getByText, getByTestId } = render(<RateCard {...singleRoomTypeRateCardProps} />);

    expect(
      getByText(
        rateCardProps.roomTypeInformationResponse.dataRoomTypeInformation.roomTypeInformation
          .roomTypes[0].roomDescription
      )
    ).toBeInTheDocument();
    expect(getByTestId('hdp_roomTypeDescription')).toBeInTheDocument();
  });

  // 2  User is booking multiple rooms of the same type,Other class except for Standard (i.e: Premier Plus, Bigger Room) is available
  it('should render Rate Card with rates and without description and image', () => {
    const { queryByTestId, getByText } = render(<RateCard {...multipleRoomsRateCardProps} />);

    expect(queryByTestId('hdp_roomTypeImage')).toBeNull();
    expect(queryByTestId('hdp_roomTypeDescription')).not.toBeInTheDocument();

    expect(getByText('Flex')).toBeInTheDocument();
    expect(getByText('Standard')).toBeInTheDocument();
    expect(queryByTestId('hdp_roomTypeTitle')).toBeInTheDocument();
  });

  // 3.  User is booking multiple rooms of different type
  it('should render Rate Card with only rates and without title, description and image for PI', () => {
    const { queryByTestId, getByText } = render(
      <RateCard
        {...rateCardProps}
        showAdditionalInfo={false}
        numberOfNights={4}
        numberOfUnits={2}
        roomTypes={['DB', 'FAM']}
      />
    );

    expect(queryByTestId('hdp_roomTypeImage')).toBeNull();
    expect(queryByTestId('hdp_roomTypeDescription')).not.toBeInTheDocument();
    expect(queryByTestId('hdp_roomTypeTitle')).toBeInTheDocument();

    expect(getByText('Flex')).toBeInTheDocument();
    expect(getByText('Standard')).toBeInTheDocument();
  });

  it('should render See details link', () => {
    const { queryByText, queryByTestId } = render(<RateCard {...rateCardProps} />);

    expect(queryByText('pihotelinfo.seeDetails')).not.toBeInTheDocument();
    expect(queryByTestId('hdp_roomTypeSeeDetailsLink')).not.toBeInTheDocument();
  });

  it('should render Rate Card without room image', () => {
    const { queryByTestId } = render(<RateCard {...rateCardProps} pmsRoomType="BIGWIN" />);
    expect(queryByTestId('hdp_roomTypeImage')).toBeNull();
  });

  it('should not render roomImage if none provided in roomClassInformation', () => {
    const { queryByTestId } = render(
      <RateCard
        {...rateCardProps}
        roomTypeInformationResponse={{
          ...rateCardProps.roomTypeInformationResponse,
          dataRoomTypeInformation: {
            roomTypeInformation: {
              roomTypes: [
                {
                  roomCategory: 'Standard',
                  roomDescription:
                    "A super-comfy Hypnos bed, a power shower and free Wi-Fi - our Standard rooms have everything you'll need for a great night's sleep.",
                  roomImage: '',
                  roomLabel: 'Standard Room',
                  roomTypeCode: [ROOM_TYPE.SINGLE],
                  groupId: 'single',
                },
              ],
            },
          },
        }}
      />
    );
    expect(queryByTestId('hdp_roomTypeImage')).toBeNull();
  });

  it('should not render Show X more rates link - for PI', () => {
    rateCardProps.channel = 'PI' as Channel;
    const { queryByTestId } = render(<RateCard {...rateCardProps} />);
    expect(queryByTestId('hdp_roomTypeShowMoreRatesLink')).not.toBeInTheDocument();
  });

  it('hide shox X rates textlink - for CCUI', async () => {
    fiveRateCardProps.channel = 'CCUI' as Channel;
    render(<RateCard {...fiveRateCardProps} />);

    const noOfRateRadioBtns = screen.getAllByRole('radio').length;

    expect(noOfRateRadioBtns).toBe(5);
    expect(screen.queryByTestId('hdp_roomTypeShowMoreRatesLink')).not.toBeInTheDocument();
  });

  it('should render Show X more rates link and hide rates if over max of 4 - for BB', async () => {
    fiveRateCardProps.channel = 'BB' as Channel;
    render(<RateCard {...fiveRateCardProps} />);

    const noOfRateRadioBtns = screen.getAllByRole('radio').length;
    expect(noOfRateRadioBtns).toBe(4);
    expect(screen.queryByTestId('hdp_roomTypeShowMoreRatesLink')).toBeInTheDocument();
  });

  it('should render all rates when Show X rates link clicked for BB', async () => {
    fiveRateCardProps.channel = 'BB' as Channel;
    render(<RateCard {...fiveRateCardProps} />);

    expect(screen.getAllByRole('radio').length).toBe(4);
    // click to show more rates (max shown for BB is 4)
    await userEvent.click(screen.getByTestId('hdp_roomTypeShowMoreRatesLink'));
    expect(screen.getAllByRole('radio').length).toBe(5);
    // click again - will toggle back to hidden any rates after max of 4
    await userEvent.click(screen.getByTestId('hdp_roomTypeShowMoreRatesLink'));
    expect(screen.getAllByRole('radio').length).toBe(4);
  });

  it("should not render rates that don't exist in ratesInformation query", async () => {
    fiveRateCardProps.channel = 'PI' as Channel;
    fiveRateCardProps.rateClassifications = [
      {
        ratePlanCode: 'FLEXRATE',
        rateClassification: 'FLEXRATE',
        rateCategory: '',
        rateDescription: 'Flex description',
        rateName: 'Flex Rate',
        rateOrder: '4',
      },
      {
        ratePlanCode: 'NONFLEX',
        rateClassification: 'NONFLEX',
        rateCategory: '',
        rateDescription:
          'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
        rateName: 'Non-Flex Rate',
        rateOrder: '1',
      },
    ];
    render(<RateCard {...fiveRateCardProps} />);

    const noOfRateRadioBtns = screen.getAllByRole('radio').length;
    expect(noOfRateRadioBtns).toBe(2);
  });

  it('should not render non-silent substitutiion notification for a room class rate card (if room silentSubstitution is false) and Feature toggles false', () => {
    const { queryByTestId } = render(<RateCard {...rateCardWithNonSilentSubstitutionProps} />);
    expect(queryByTestId('substitution-notification')).not.toBeInTheDocument();
  });

  it('should render non-silent substitution notification for a room class rate card (if room silentSubstitution is false) and Feature toggle is true', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_bb_ccui_nonsilent_substitution_per_roomclass: true,
    });
    const { getByTestId } = render(<RateCard {...rateCardWithNonSilentSubstitutionProps} />);
    expect(getByTestId('substitution-notification')).toBeInTheDocument();
  });

  it('should render all non-silent substitutiion unique room types in the notification for a room class rate card (if room silentSubstitution is false and Feature toggle is true)', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_bb_ccui_nonsilent_substitution_per_roomclass: true,
    });
    const { getByText } = render(<RateCard {...rateCardWithNonSilentSubstitutionProps} />);
    expect(
      getByText(
        'searchresults.list.restrictions.available searchresults.list.restrictions.offer Accessible double bedroom with level access shower room, Twin room'
      )
    ).toBeInTheDocument();
  });

  it('should not render non-silent substitutiion notification for a room class rate card (if room silentSubstitution is true)', () => {
    rateCardWithNonSilentSubstitutionProps.roomClassCode = 'PP'; // the selected rate class
    const { queryByTestId } = render(<RateCard {...rateCardWithNonSilentSubstitutionProps} />);
    expect(queryByTestId('substitution-notification')).not.toBeInTheDocument();
  });

  it('should not render rate item with total base price when not available', () => {
    const { queryByTestId } = render(<RateCard {...rateCardProps} />);

    expect(queryByTestId('hdp_rateItemBasePrice')).not.toBeInTheDocument();
  });

  it('should render rate item with the correct total base price when available', () => {
    const { getByTestId } = render(<RateCard {...rateCardWithBasePriceProps} />);

    expect(getByTestId('hdp_rateItemBasePrice')).toBeInTheDocument();
    expect(getByTestId('hdp_rateItemBasePrice')).toHaveTextContent('£95');
  });
});
