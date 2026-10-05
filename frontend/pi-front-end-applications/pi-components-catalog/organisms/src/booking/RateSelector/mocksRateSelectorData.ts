export const mockHotelAvailabilityData = {
  hotelAvailability: {
    hotelId: 'LONEUS',
    startDate: '2022-08-13',
    endDate: '2022-08-14',
    available: true,
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
      },
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
      },
    ],
  },
  ratesInformation: {
    rateClassifications: [
      {
        rateClassification: 'FLEXRATE',
        rateDescription:
          'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
        rateName: 'Flex',
        rateOrder: '1',
        rateTags: [],
      },
      {
        rateClassification: 'STANDARD',
        rateDescription:
          'Pay now, non-refundable. Amendable check in date at the same hotel up to 1pm on the day of arrival',
        rateName: 'Standard',
        rateOrder: '5',
        rateTags: [],
      },
    ],
  },
  // needed for RateCard
  roomTypeInformation: {
    roomTypes: [
      {
        roomLabel: 'Standard Room',
        roomImage:
          '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4%201.jpg',
        roomDescription:
          "A super-comfy Hypnos bed, a power shower and free Wi-Fi - our Standard rooms have everything you'll need for a great night's sleep.",
        groupId: 'double',
      },
    ],
  },
};

export const mockRoomTypeInformation = {
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
};

const roomClassConfig = [
  {
    code: 'PP',
    order: 1,
    availableUpgrades: ['PV'],
  },
  {
    code: 'PV',
    order: 2,
    availableUpgrades: [],
  },
  {
    code: 'PC',
    order: 3,
    availableUpgrades: [],
  },
  {
    code: 'PS',
    order: 4,
    availableUpgrades: [],
  },
  {
    code: 'SU',
    order: 5,
    availableUpgrades: [],
  },
  {
    code: 'SE',
    order: 6,
    availableUpgrades: [],
  },
  {
    code: 'BG',
    order: 7,
    availableUpgrades: [],
  },
  {
    code: 'ST',
    order: 8,
    availableUpgrades: ['PP', 'PV'],
  },
  {
    code: 'SF',
    order: 9,
    availableUpgrades: [],
  },
  {
    code: 'SV',
    order: 10,
    availableUpgrades: [],
  },
  {
    code: 'SC',
    order: 11,
    availableUpgrades: [],
  },
  {
    code: 'SS',
    order: 12,
    availableUpgrades: [],
  },
  {
    code: 'PSE',
    order: 13,
    availableUpgrades: [],
  },
];

const roomUpgradeOptions = {
  priceText: 'Available for {{price}} per night',
  primaryButtonText: 'Upgrade for {{price}}',
  secondaryButtonText: 'Keep your booking',
  roomUpgrades: [
    {
      description:
        '<p>Handpicked for their stunning view, these rooms are only available at a small selection of Premier Inn hotels, making your experience truly one of a kind.</p>\n',
      heading: 'Upgrade to a room with a view',
      imageUrl:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/standard-double-bed-sofa-bed.jpg',
      roomClass: 'SV',
    },
    {
      description:
        '<p>Upgrade to one of our limited rooms with a view and get everything in our Standard room plus:<ul><li>Superior rainfall shower</li><li>Luxury toiletries</li><li>Ultimate Wi-Fi</li><li>Upgraded workspace</li><li>Air conditioning</li><li>Coffee machine</li><li>Mini-fridge</li><li>Improved refreshments</li><li>Iron & more</li></ul></p>\n',
      heading: 'Upgrade to a Premier Plus room',
      imageUrl:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
      roomClass: 'PP',
    },
    {
      description:
        '<p>Upgrade to one of our limited rooms with a view and get everything in our Standard room plus:<ul><li>Superior rainfall shower</li><li>Luxury toiletries</li><li>Ultimate Wi-Fi</li><li>Upgraded workspace</li><li>Air conditioning</li><li>Coffee machine</li><li>Mini-fridge</li><li>Improved refreshments</li><li>Iron & more</li></ul></p>\n',
      heading: 'Upgrade to a Premier Plus room with a view',
      imageUrl:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
      roomClass: 'PV',
    },
  ],
};

export const mockRoomClassConfig = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    roomClassConfig: {
      roomClassConfig: roomClassConfig,
    },
  },
};

export const mockGlobalConfig = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    globalConfig: {
      maxRoomsLim: {
        maxRooms: 9,
        maxRoomsAmend: 1,
      },
      roomClassConfig: roomClassConfig,
      roomUpgradeOptions: roomUpgradeOptions,
    },
  },
};
