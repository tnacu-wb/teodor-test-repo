import { Area } from '@whitbread-eos/api';

import {
  useForDiscountedRateMicroSite,
  useDiscountRateInfoHotelAvailability,
  useUpdateRateName,
  getCorporateDiscountRatePlanCode,
  useGetDiscountRateComapnyId,
  useGetDiscountRateReservationData,
  useForDiscountedRateFlag,
} from './use-discounted-rate';

export const staticContentMockData = {
  headerInformation: {
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
            page: 'travel-industry-rate',
            ratePlanCode: 'FCDNLR30',
            corpId: '15010601',
          },
        ],
      },
    },
  },
};

const mockStaticContentRequest = {
  data: staticContentMockData,
  isError: false,
  isLoading: false,
  error: {
    message: '',
  },
};

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: () => jest.fn(),
  }),
}));

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

const inventories = [
  { availableCount: 92, code: 'FMTRPL' },
  { availableCount: 96, code: 'FMTHRE' },
  { availableCount: 100, code: 'FMFOUR' },
  { availableCount: 100, code: 'FMQUAD' },
  { availableCount: 65, code: 'DOUBLE' },
  { availableCount: 15, code: 'TWINRM' },
  { availableCount: 20, code: 'WETDBL' },
  { availableCount: 93, code: 'ZPLDBL' },
  { availableCount: 17, code: 'LOWDBL' },
];

const mockHotelAvailabilityData = {
  hotelAvailability: {
    hotelId: 'FRAMTI',
    startDate: '2024-10-22',
    endDate: '2024-10-22',
    available: true,
    limitedAvailability: false,
    roomRates: [
      {
        ratePlanCode: 'FCDNLR30',
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
                  totalNetAmount: 44.84,
                  currencyCode: 'EUR',
                  packageCode: null,
                  packageAmount: null,
                  dailyPrices: [{ date: '2024-10-22', netPrice: 44.84 }],
                },
              },
            ],
          },
        ],
      },
    ],
  },
  hotelInventory: {
    roomTypeInventories: inventories,
  },
};

const mockHotelRateInfoData = {
  hotelAvailability: {
    hotelId: 'FRAMTI',
    startDate: '2024-10-22',
    endDate: '2024-10-22',
    available: true,
    limitedAvailability: false,
    roomRates: [
      {
        ratePlanCode: 'FCDNLR30',
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
                  totalNetAmount: 44.84,
                  currencyCode: 'EUR',
                  packageCode: null,
                  packageAmount: null,
                  dailyPrices: [{ date: '2024-10-22', netPrice: 44.84 }],
                },
              },
            ],
          },
        ],
      },
    ],
  },
  hotelInventory: {
    roomTypeInventories: inventories,
  },
  ratesInformation: {
    rateClassifications: [
      {
        additionalDescription: '',
        ratePlanCode: 'FCDNLR30',
        rateOrder: '1',
        rateNotes: '<p>Amend or cancel up to 6pm on arrival day</p>\n',
        rateName: 'Fixed Corporate Discount',
        rateLongDescription: '',
        rateDescription:
          'Pay now or on arrival, fully refundable with free cancellation up to 6pm on the day of arrival',
        rateClassification: 'FCDNLR30',
        rateCategory: 'G',
        rateTags: [],
      },
    ],
  },
};

afterEach(() => {
  jest.clearAllMocks();
});

jest.mock('./use-hotel-availability', () => {
  return {
    useHotelAvailabilityDiscountRate: jest.fn().mockImplementation(() => {
      return {
        data: mockHotelAvailabilityData,
        isLoading: false,
        isError: false,
        error: false,
      };
    }),
    useHotelRatesInformationDiscountRate: jest.fn().mockImplementation(() => {
      return {
        data: mockHotelRateInfoData,
        isLoading: false,
        isError: false,
        error: false,
      };
    }),
  };
});

const mockUseRouter = jest.fn().mockImplementation(() => {
  return { query: { CORPID: '15010601' } };
});

const mockCustomLocale = jest.fn();
jest.mock('./use-request', () => ({
  ...jest.requireActual('./use-request'),
  useQueryRequest: jest.fn().mockImplementation((queryKey) => {
    if (queryKey[0] === 'searchCompanyById') {
      return {
        isLoading: false,
        data: mockCompanyData,
        isError: false,
      };
    }
    if (queryKey[0] === 'GetStaticContent') {
      return mockStaticContentRequest;
    }
    return {};
  }),
}));

jest.mock('../hooks', () => {
  return {
    useCustomLocale: () => mockCustomLocale(),
    useStaticHotelInformation: jest.fn(),
    invalidateQueries: jest.fn(),
    useQuery: () => {
      return {
        data: mockHotelAvailabilityData,
        isError: false,
        isLoading: false,
        isSuccess: true,
      };
    },
    useFeatureToggle: jest.fn().mockReturnValue({ release_pi_discount_rate: true }),
    useForDiscountedRateMicroSite: jest.fn().mockReturnValue(true),
    useForDiscountedRateFlag: jest.fn().mockReturnValue(true),
  };
});

jest.mock('./use-get-country-language', () => {
  return jest.fn().mockReturnValue({
    language: 'en',
    country: 'gb',
  });
});

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('useForDiscountedRateMicroSite', () => {
  it('should return true or false based on corpId and unleash feature flag', () => {
    const isDiscountRateEnabled = useForDiscountedRateMicroSite();
    expect(isDiscountRateEnabled).toBeTruthy();
  });
});

const tirOffers = [
  {
    cellCode: '',
    maxRooms: 2,
    numberOfNights: 9,
    page: 'travelindustryrate-offer',
    ratePlanCode: 'FCDNLR30',
    corpId: '15010601',
  },
];

const bookingData: any = {
  upgradeToFlex: {
    flexRateCode: 'FLEX',
  },
  reservationByIdList: [
    {
      roomStay: {
        ratePlanCode: 'FCDNLR30',
        rateName: 'Fixed Corporate Discount',
        rateExtraInfo: {
          rateName: 'Fixed Corporate Discount',
        },
      },
    },
  ],
};
const language = 'en';
const country = 'gb';

describe('useDiscountRateInfoHotelAvailability', () => {
  it('should return hotelrateInformation data', () => {
    const hotelrateInformation = useDiscountRateInfoHotelAvailability({
      hotelId: 'FRAMTI',
      hotelBrand: 'PID',
      arrival: '2024-10-22',
      departure: '2024-10-23',
      rooms: [{ adultsNumber: 1, childrenNumber: 0, roomType: 'DB', cotRequired: false }],
      offers: tirOffers,
    });
    expect(hotelrateInformation?.hotelAvailabilityResponse).toBeTruthy();
    expect(hotelrateInformation?.dataHotelAvailability).toBeTruthy();
  });
});

describe('useUpdateRateName', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'gb',
      country: 'gb',
    });
    // Mock localStorage
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: jest.fn().mockReturnValue('?CORPID=15010601'),
      },
      writable: true,
    });
  });

  it('should update rate name correctly', async () => {
    useUpdateRateName(bookingData, language, country);
    expect(bookingData).toBeTruthy();
  });
  it('upgradeToFlex disabled', async () => {
    bookingData.upgradeToFlex = null;
    useUpdateRateName(bookingData, language, country);
    expect(bookingData).toBeTruthy();
  });
  it('Different rate plan code', async () => {
    bookingData.reservationByIdList[0].roomStay.ratePlanCode = 'FLEX';
    useUpdateRateName(bookingData, language, country);
    expect(bookingData).toBeTruthy();
  });
});

describe('useUpdateRateName return, if CORPID is null', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'gb',
      country: 'gb',
    });
    // Mock localStorage
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: jest.fn().mockReturnValue(''),
      },
      writable: true,
    });
  });

  it('should update rate name correctly', async () => {
    useUpdateRateName(bookingData, language, country);
    expect(bookingData).toBeTruthy();
  });
});

describe('getCorporateDiscountRatePlanCode', () => {
  it('should return ratePlanCode data', () => {
    const ratesData = [{ isCorporateDiscountAvailable: true, ratePlanCode: 'FCDNLR30' }];
    const ratePlanCode = getCorporateDiscountRatePlanCode(ratesData as any);
    expect(ratePlanCode).toEqual('FCDNLR30');
  });
});

describe('useGetDiscountRateComapnyId', () => {
  it('should return ratePlanCode data', () => {
    const hotelAvailabilityParams = {
      arrival: '2024-11-20',
      bookingChannel: {
        channel: 'PI',
        language: 'en',
        subchannel: 'WEB',
      },
      channel: 'PI',
      companyId: null,
      country: 'gb',
      departure: '2024-11-21',
      hotelId: 'FRAMTI',
      language: 'en',
      rooms: [],
      rateCode: 'FCDNLR30',
      ratePlanCodes: ['FCDNLR30'],
    };

    const companyId = useGetDiscountRateComapnyId(hotelAvailabilityParams as any);
    expect(companyId).toEqual('8986523');
  });
});

describe('useGetDiscountRateReservationData', () => {
  it('should return ratePlanCode data', () => {
    const bookingData = {
      reservationByIdList: [
        {
          reservationId: '2445345',
          roomStay: {
            ratePlanCode: 'FCDNLR30',
          },
        },
      ],
    };

    const offersdataPresent = [
      {
        cellCode: '',
        maxRooms: 2,
        numberOfNights: 9,
        page: 'travel-industry-rate',
        corpId: '15010601',
        ratePlanCode: 'FCDNLR30',
      },
    ];

    const offersdataAbsent = [
      {
        cellCode: '',
        maxRooms: 2,
        numberOfNights: 9,
        page: 'travel-industry-rate',
        corpId: '15010601',
        ratePlanCode: '07090790',
      },
    ];

    const reservationData = useGetDiscountRateReservationData(offersdataPresent, bookingData, 'pi');
    expect(reservationData.isDiscountRate).toBeTruthy();

    const reservationDataAbsent = useGetDiscountRateReservationData(
      offersdataAbsent,
      bookingData,
      'pi'
    );
    expect(reservationDataAbsent.isDiscountRate).toBeFalsy();
  });
  it('should return ratePlanCode data for CCUI variant flow', () => {
    const bookingData = {
      reservationByIdList: [
        {
          reservationId: '2445345',
          roomStay: {
            ratePlanCode: 'FCDNLR30',
          },
        },
      ],
    };

    const offersdataPresent = [
      {
        cellCode: '',
        maxRooms: 2,
        numberOfNights: 9,
        page: 'travel-industry-rate',
        corpId: '15010601',
        ratePlanCode: 'FCDNLR30',
      },
    ];

    const reservationData = useGetDiscountRateReservationData(
      offersdataPresent,
      bookingData,
      Area.CCUI
    );
    expect(reservationData.isDiscountRate).toBeTruthy();
  });
});

describe('useForDiscountedRateFlag hook', () => {
  it('should return true based on unleash feature flag', () => {
    jest.mock('../hooks', () => {
      return {
        useFeatureToggle: jest.fn().mockReturnValue({ release_pi_discount_rate: true }),
      };
    });

    const isDiscountRateEnabled = useForDiscountedRateFlag();
    expect(isDiscountRateEnabled).toBe(true);
  });
});
