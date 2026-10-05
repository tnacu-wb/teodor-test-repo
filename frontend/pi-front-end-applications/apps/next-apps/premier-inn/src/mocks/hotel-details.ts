import type { HIBasketData } from '@whitbread-eos/api';

const mockAddress = {
  addressLine1: '27-29 Red Lion Street',
  addressLine2: 'Holborn',
  addressLine3: 'London',
  addressLine4: 'London',
  country: 'England',
  postalCode: 'WC1R 4PS',
};
const mockSatNavDirections = 'WC1R 4PS';
const mockWhatThreeWords = '///truth.drums.bowls';

export const mockStaticData = {
  hotelId: 'LONEUS',
  name: 'London Euston',
  brand: 'PI',
  address: mockAddress,
  satNavDirections: mockSatNavDirections,
  whatThreeWords: mockWhatThreeWords,
  isLoading: false,
  isError: false,
  cityTax: {
    isCityTaxHotel: true,
    isCityTaxBusinessHotel: true,
    percentage: 5,
    effectiveFrom: '2023-01-01',
    bookingDateFrom: '2022-12-01',
  },
  hotelOpeningDate: '',
  headline: 'test',
  hotelFacilities: [
    {
      code: 'COP',
      description: '',
      icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/COP.svg',
      isVisible: true,
      name: 'Chargeable offsite parking',
      weight: 0,
    },
    {
      code: 'DIN',
      description: '',
      icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIN.svg',
      isVisible: true,
      name: 'Restaurant',
      weight: 20,
    },
  ],
  ROOMS: '1',
  error: {
    message: '',
  },
  faq: {
    title: 'London Kings Cross FAQs',
    faqItems: [
      {
        question: 'Can I check in early?',
        answer:
          '<p>Early check-in is available from 11am for an additional £10, and this will be payable at reception on arrival. Early check-in is subject to availability and not available at all Premier Inn hotels.&nbsp;&nbsp;</p>\r\n',
      },
    ],
  },
};

export const mockAvailability = {
  data: {
    hotelAvailability: {
      hotelId: 'LONEUS',
      startDate: '2022-08-13',
      endDate: '2022-08-14',
      available: true,
      roomRates: [
        {
          ratePlanCode: 'FCDNLR30',
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
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'error',
  },
};

export const mockRatesInformation = {
  data: {
    ratesInformation: {
      rateClassifications: [
        {
          rateClassification: 'STANDARD',
          rateDescription: 'STANDARD rate',
          rateName: 'STANDARD',
          rateOrder: 1,
          rateTags: [],
        },
        {
          rateClassification: 'FLEXRATE',
          rateDescription: 'FLEX rate',
          rateName: 'FLEXRATE',
          rateOrder: 2,
          rateTags: [],
        },
        {
          rateClassification: 'FCDNLR30',
          rateDescription: 'FCDNLR30 rate',
          rateName: 'Travel rate',
          rateOrder: 2,
          rateTags: [],
        },
      ],
    },
  },
};

export const visualDisplayContext = {
  isLessThanLg: false,
  isLessThanMd: false,
  isLessThanSm: false,
  isLessThanXs: false,
};

export const mockCompanyData = {
  companyProfile: {
    name: 'Travel Industry Rate ',
    address: {
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      country: 'DE',
      postalCode: '12526',
    },
    telephoneNumber: '999999999999',
    corpId: '15010601',
    companyId: '8986523',
    profileType: 'Company',
    language: 'DE',
    active: true,
    negotiatedRateEnabled: true,
  },
};

export const mockHeaderStaticData = {
  headerInformation: {
    announcement: {
      browserCompatibilityMessage:
        '<p>Looks like your web browser isn’t supported. Try downloading <a href="https://www.google.com/chrome/">Google Chrome</a>, <a href="https://www.microsoft.com/en-us/edge">Microsoft Edge</a> or <a href="https://www.mozilla.org/en-CA/firefox/new/">Firefox</a> for a better online experience with us.</p>\n',
      text: null,
      type: 'info',
    },
    config: {
      api: {
        bookingChannel: {
          business: 'CBT',
          leisure: 'WEB',
        },
      },
      authentication: {
        accountLinks: [
          {
            icon: 'bookings',
            title: 'Bookings',
            url: 'https://www.dit.premierinn.digital/gb/en/account/dashboard.html',
          },
          {
            icon: 'settings',
            title: 'Settings',
            url: 'https://www.dit.premierinn.digital/gb/en/account/profile.html',
          },
        ],
        business: {
          businessAccountLinks: null,
        },
        businessAccountCardRedirectPath:
          'https://www.businessaccount.premierinn.com/Secure/Login.aspx?INTCMP=PI_BA_BA_1',
      },
      bookingSearch: {
        show: true,
        dashboardRedirect: {
          bookingReference: 'bookingReference',
          cookie: {
            domain: 'premierinn.digital',
            minutesTillExpiry: '30',
            name: 'pi.single-booking',
          },
          url: '/gb/en/account/dashboard.html',
        },
      },
      roomCodes: {
        accessible: 'DIS',
        double: 'DB',
        family: 'FAM',
        single: 'SB',
        twin: 'TWIN',
      },
    },
    content: {
      global: {
        offers: [
          {
            cellCode: 'EMP01',
            maxRooms: 2,
            numberOfNights: 9,
            page: 'employee-offer',
          },
          {
            cellCode: '',
            maxRooms: 2,
            numberOfNights: 9,
            page: 'travelindustryrate-offer',
            ratePlanCode: 'FCDNLR30',
          },
        ],
        thirdParties: 'TRA,TRIVAGO,GHF,GHF3,GHFPP,GLBC,BMP,BMF,YEXT',
      },
    },
  },
};

export const mockMutationRequest = {
  mutation: {
    mutate: jest.fn(),
  },
  isError: false,
  error: null,
  isLoading: false,
  isSuccess: false,
  data: {},
  onSuccess: jest.fn(),
};

export const mockEmptyBasketDetails = {
  hotelId: null,
};

export const mockBasketDetailsStateWithTwoRooms = {
  hotelId: 'MANOLD',
  arrival: '2023-07-01',
  departure: '2023-07-04',
  numberOfUnits: 2,
  numberOfNights: 3,
  selectedRate: {
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
        roomType: 'DIS',
        adults: 1,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'WETDBL',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['SING', 'WETR'],
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

export const mockBasketDetailsStateWithAccessibleRoomSubstitutedForDouble = {
  hotelId: 'HEAPTI',
  arrival: '2025-07-01',
  departure: '2025-07-02',
  numberOfUnits: 1,
  numberOfNights: 1,
  selectedRate: {
    ratePlanCode: 'FLEXRATE',
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
            roomPriceBreakdown: {
              totalNetAmount: 99,
              currencyCode: 'GBP',
              dailyPrices: [{ date: '2025-07-01', netPrice: 99 }],
            },
          },
          {
            pmsRoomType: 'WETDBL',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['SING', 'WETR'],
            roomPriceBreakdown: {
              totalNetAmount: 99,
              currencyCode: 'GBP',
              dailyPrices: [{ date: '2025-07-01', netPrice: 99 }],
            },
          },
        ],
      },
    ],
  },
  roomClass: 'Standard Room',
  rateName: 'Flex Rate',
  phoneNumber: '0333 321 1315',
  brand: 'PI',
} as HIBasketData;

export const mockBasketDetailsState = {
  hotelId: 'MANOLD',
  arrival: '2023-07-22',
  departure: '2023-07-23',
  numberOfUnits: 1,
  numberOfNights: 1,
  selectedRate: {
    ratePlanCode: 'FLEXRATE',
    roomTypes: [
      {
        roomType: 'DIS',
        adults: 1,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'WETDBL',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['SING', 'WETR'],
            roomPriceBreakdown: {
              totalNetAmount: 999,
              currencyCode: 'GBP',
              dailyPrices: [{ date: '2023-07-22', netPrice: 999 }],
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
  silentSubstitutionLabels: ['Accessible Room'],
} as HIBasketData;

export const mockHotelInventory = {
  isLoading: false,
  isError: false,
  error: { message: 'Error!' },
  data: {
    hotelInventory: {
      roomTypeInventories: [
        {
          availableCount: 2,
          code: 'LOWDBL',
        },
        { availableCount: 51, code: 'FMTRPL' },
        { availableCount: 48, code: 'FMQUAD' },
        { availableCount: 4, code: 'WETDBL' },
        { availableCount: 4, code: 'LOWDBL' },
        { availableCount: 0, code: 'WETTWN' },
        { availableCount: 2, code: 'BRFDBL' },
        {
          availableCount: 22,
          code: 'PPLDBL',
        },
      ],
    },
  },
};

export const mockBasketDetailsStateWithAccessibleDoubleRoomAndLoweredAndWetRoom = {
  hotelId: 'LONEUS',
  arrival: '2023-06-24',
  departure: '2023-06-25',
  numberOfUnits: 1,
  numberOfNights: 1,
  selectedRate: {
    ratePlanCode: 'FLEXRATE',
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
              totalNetAmount: 45,
              currencyCode: 'GBP',
              dailyPrices: [{ date: '2023-06-24', netPrice: 45 }],
            },
          },
          {
            pmsRoomType: 'WETDBL',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['SING', 'WETR'],
            roomPriceBreakdown: {
              totalNetAmount: 45,
              currencyCode: 'GBP',
              dailyPrices: [{ date: '2023-06-24', netPrice: 45 }],
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
  phoneNumber: '0333 321 1262',
  brand: 'PI',
} as HIBasketData;

export const mockBasketDetailsStateWithAccessibleTwinRoomAndLoweredAndWetRoom = {
  hotelId: 'WORHIG',
  arrival: '2024-01-05',
  departure: '2024-01-07',
  numberOfUnits: 1,
  numberOfNights: 2,
  selectedRate: {
    ratePlanCode: 'FLEXRATE',
    roomTypes: [
      {
        roomType: 'DIS',
        adults: 2,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'LOWTWN',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['TWIN', 'LOWB'],
            roomPriceBreakdown: {
              totalNetAmount: 256,
              currencyCode: 'GBP',
              dailyPrices: [
                { date: '2024-01-05', netPrice: 128 },
                { date: '2024-01-06', netPrice: 128 },
              ],
            },
          },
          {
            pmsRoomType: 'WETTWN',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['TWIN', 'WETR'],
            roomPriceBreakdown: {
              totalNetAmount: 256,
              currencyCode: 'GBP',
              dailyPrices: [
                { date: '2024-01-05', netPrice: 128 },
                { date: '2024-01-06', netPrice: 128 },
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
  phoneNumber: null,
  brand: 'PI',
};
