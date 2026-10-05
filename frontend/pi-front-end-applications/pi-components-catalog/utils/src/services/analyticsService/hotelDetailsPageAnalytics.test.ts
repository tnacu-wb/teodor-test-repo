/* eslint-disable @typescript-eslint/no-explicit-any */
import { add, differenceInDays, format } from 'date-fns';

import analytics from './analytics';
import updateHotelDisplayPageAnalytics from './hotelDisplayPageAnalytics';

const analyticsUpdateSpy = jest.spyOn(analytics, 'update').mockReturnValue(undefined);

const mockInput = {
  multiSearchParams: {
    ARRdd: '11',
    ARRmm: '04',
    ARRyyyy: '2023',
    ROOMS: 1,
    NIGHTS: 1,
    CHILD: 1,
    ADULT: 1,
    INTTYP: 'DB',
  } as any,
  hotelName: 'Manchester Old Trafford',
  hotelId: 'MANOLD',
  hotelLabel: ['Manchester Old Trafford'],
  hotelAvailability: 'available',
  hotelFacilityIcons: [
    '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIS.svg',
    '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/LFT.svg',
  ],
  dataHotelAvailability: {
    hotelAvailability: {
      hotelId: 'HEAPTI',
      startDate: '2025-08-08',
      endDate: '2025-08-09',
      available: true,
      limitedAvailability: false,
      mlos: false,
      roomRates: [
        {
          ratePlanCode: 'FLEXRATE',
          promotionCode: null,
          cellCode: null,
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
                    totalNetAmount: 95,
                    baseRateAmount: null,
                    currencyCode: 'GBP',
                    packageCode: null,
                    packageAmount: null,
                    dailyPrices: [
                      {
                        date: '2025-08-08',
                        netPrice: 95,
                      },
                    ],
                  },
                  numberOfRoomsAvailable: 102,
                },
                {
                  pmsRoomType: 'DOUBLE',
                  silentSubstitution: true,
                  cotAvailable: false,
                  roomClass: 'ST',
                  specialRequests: ['SING'],
                  roomPriceBreakdown: {
                    totalNetAmount: 79,
                    baseRateAmount: null,
                    currencyCode: 'GBP',
                    packageCode: null,
                    packageAmount: null,
                    dailyPrices: [
                      {
                        date: '2025-08-08',
                        netPrice: 79,
                      },
                    ],
                  },
                  numberOfRoomsAvailable: 426,
                },
                {
                  pmsRoomType: 'VPPDBL',
                  silentSubstitution: true,
                  cotAvailable: false,
                  roomClass: 'PV',
                  specialRequests: ['SING'],
                  roomPriceBreakdown: {
                    totalNetAmount: 100,
                    baseRateAmount: null,
                    currencyCode: 'GBP',
                    packageCode: null,
                    packageAmount: null,
                    dailyPrices: [
                      {
                        date: '2025-08-08',
                        netPrice: 100,
                      },
                    ],
                  },
                  numberOfRoomsAvailable: 6,
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
          rateDescription: 'Flex rate',
          rateName: 'Flex',
          rateOrder: '1',
          rateTags: [''],
        },
      ],
    },
  },
};

const mockUseFeatureSwitch = jest.fn();
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureSwitch: () => mockUseFeatureSwitch(),
}));

describe('hotelDisplayPageAnalytics', () => {
  afterEach(() => {
    analyticsUpdateSpy.mockReset();
  });

  it('should call analytics.update with correct values', () => {
    updateHotelDisplayPageAnalytics(mockInput);
    expect(analyticsUpdateSpy).toHaveBeenCalledWith({
      analyticsDataSearchResult: {
        searchCheckInDate: '11/04/2023',
        searchCheckOutDate: '12/04/2023',
        searchDaysToCheckIn: differenceInDays(new Date('2023-04-11'), new Date()),
        searchNumberOfAdults: 1,
        searchNumberOfChildren: 1,
        searchNumberOfGuests: 2,
        searchNumberOfNights: 1,
        searchNumberOfRooms: 1,
        searchResults: 1,
        searchResultsDisplayed: [
          {
            bookingSystem: 'Opera',
            hotelAvailability: 'available',
            hotelCode: 'manold',
            hotelDistance: 0,
            hotelFacilityIcons: mockInput.hotelFacilityIcons,
            hotelLabel: ['Manchester Old Trafford'],
            imageLabel: '',
            priceFrom: '',
            voucherCode: '',
          },
        ],
        searchRoomType: 'db',
        searchTerm: 'Manchester Old Trafford',
        searchType: 'list view',
        searchWeekdayFrom: 'tue',
        searchWeekdayFromTo: 'tue-wed',
        searchWeekdayTo: 'wed',
        mapLoaded: false,
      },
      discountTags: '',
      promo: {
        promoName: '',
        promoCode: '',
        eligibility: false,
        promoJourney: false,
      },
    });
  });

  it('should call analytics update with current date, if provided arrival date is invalid', () => {
    mockInput.multiSearchParams.ARRmm = '0';

    updateHotelDisplayPageAnalytics(mockInput);

    const expectedStartDate = new Date();
    const expectedEndDate = add(new Date(), { days: 1 });
    const expectedSearchWeekdayFrom = format(expectedStartDate, 'eee')?.toLowerCase();
    const expectedSearchWeekdayTo = format(expectedEndDate, 'eee')?.toLowerCase();
    const expectedSearchWeekdayFromTo = `${expectedSearchWeekdayFrom}-${expectedSearchWeekdayTo}`;

    expect(analyticsUpdateSpy).toHaveBeenCalledWith({
      analyticsDataSearchResult: {
        searchCheckInDate: format(expectedStartDate, 'dd/MM/yyyy'),
        searchCheckOutDate: format(expectedEndDate, 'dd/MM/yyyy'),
        searchDaysToCheckIn: 0,
        searchNumberOfAdults: 1,
        searchNumberOfChildren: 1,
        searchNumberOfGuests: 2,
        searchNumberOfNights: 1,
        searchNumberOfRooms: 1,
        searchResults: 1,
        searchResultsDisplayed: [
          {
            bookingSystem: 'Opera',
            hotelAvailability: 'available',
            hotelCode: 'manold',
            hotelDistance: 0,
            hotelFacilityIcons: mockInput.hotelFacilityIcons,
            hotelLabel: ['Manchester Old Trafford'],
            imageLabel: '',
            priceFrom: '',
            voucherCode: '',
          },
        ],
        searchRoomType: 'db',
        searchTerm: 'Manchester Old Trafford',
        searchType: 'list view',
        searchWeekdayFrom: expectedSearchWeekdayFrom,
        searchWeekdayFromTo: expectedSearchWeekdayFromTo,
        searchWeekdayTo: expectedSearchWeekdayTo,
        mapLoaded: false,
      },
      discountTags: '',
      promo: {
        promoName: '',
        promoCode: '',
        eligibility: false,
        promoJourney: false,
      },
    });
  });

  it('should call analytics.update with default values', () => {
    mockInput.multiSearchParams.ARRmm = '04';
    mockInput.hotelAvailability = '';
    mockInput.hotelFacilityIcons = [];
    mockInput.hotelLabel = ['no label'];

    updateHotelDisplayPageAnalytics(mockInput);

    expect(analyticsUpdateSpy).toHaveBeenCalledWith({
      analyticsDataSearchResult: {
        searchCheckInDate: '11/04/2023',
        searchCheckOutDate: '12/04/2023',
        searchDaysToCheckIn: differenceInDays(new Date('2023-04-11'), new Date()),
        searchNumberOfAdults: 1,
        searchNumberOfChildren: 1,
        searchNumberOfGuests: 2,
        searchNumberOfNights: 1,
        searchNumberOfRooms: 1,
        searchResults: 1,
        searchResultsDisplayed: [
          {
            bookingSystem: 'Opera',
            hotelAvailability: '',
            hotelCode: 'manold',
            hotelDistance: 0,
            hotelFacilityIcons: [],
            hotelLabel: ['no label'],
            imageLabel: '',
            priceFrom: '',
            voucherCode: '',
          },
        ],
        searchRoomType: 'db',
        searchTerm: 'Manchester Old Trafford',
        searchType: 'list view',
        searchWeekdayFrom: 'tue',
        searchWeekdayFromTo: 'tue-wed',
        searchWeekdayTo: 'wed',
        mapLoaded: false,
      },
      discountTags: '',
      promo: {
        promoName: '',
        promoCode: '',
        eligibility: false,
        promoJourney: false,
      },
    });
  });

  it('should return tomorrow\'s date when isMetaArrivalDayKeywordFlagEnabled is true and ARRdd is "tomorrow"', () => {
    jest.useFakeTimers();
    jest.setSystemTime(new Date('2023-04-11T00:00:00Z'));
    mockInput.multiSearchParams.ARRdd = 'tomorrow';

    updateHotelDisplayPageAnalytics({
      ...mockInput,
      isMetaArrivalDayKeywordFlagEnabled: true,
    });

    expect(analyticsUpdateSpy).toHaveBeenCalledWith({
      analyticsDataSearchResult: {
        searchCheckInDate: '12/04/2023',
        searchCheckOutDate: '13/04/2023',
        searchDaysToCheckIn: 1,
        searchNumberOfAdults: 1,
        searchNumberOfChildren: 1,
        searchNumberOfGuests: 2,
        searchNumberOfNights: 1,
        searchNumberOfRooms: 1,
        searchResults: 1,
        searchResultsDisplayed: [
          {
            bookingSystem: 'Opera',
            hotelAvailability: '',
            hotelCode: 'manold',
            hotelDistance: 0,
            hotelFacilityIcons: [],
            hotelLabel: ['no label'],
            imageLabel: '',
            priceFrom: '',
            voucherCode: '',
          },
        ],
        searchRoomType: 'db',
        searchTerm: 'Manchester Old Trafford',
        searchType: 'list view',
        searchWeekdayFrom: 'wed',
        searchWeekdayFromTo: 'wed-thu',
        searchWeekdayTo: 'thu',
        mapLoaded: false,
      },
      discountTags: '',
      promo: {
        promoName: '',
        promoCode: '',
        eligibility: false,
        promoJourney: false,
      },
    });
    jest.useRealTimers();
  });

  it('should set PromoId to empty string if urlPromo exists but does not match bannerPromo', () => {
    const inputWithPromoMismatch = {
      ...mockInput,
      multiSearchParams: {
        ...mockInput.multiSearchParams,
        PROMOID: 'URLPROMO',
      },
      promoActions: {
        promotionBannerData: {
          promotionCode: 'BANNERPROMO',
        },
      },
    };

    updateHotelDisplayPageAnalytics(inputWithPromoMismatch);

    const lastCall = analyticsUpdateSpy.mock.calls[0][0];
    expect(lastCall.promo.promoCode).toBe('');
    expect(lastCall.promo.eligibility).toBe(false);
    expect(lastCall.promo.promoJourney).toBe(false);
  });

  it('should use uniquePromotions when promoType is UNIQUE', () => {
    const inputWithUniquePromo = {
      ...mockInput,
      multiSearchParams: {
        ...mockInput.multiSearchParams,
        PROMOID: 'UNIQUEPROMO',
      },
      promoActions: {
        promoState: {
          code: 'UNIQUEPROMO',
          type: 'UNIQUE',
        },
      },
      dataHotelAvailability: {
        ...mockInput.dataHotelAvailability,
        hotelAvailability: {
          ...mockInput.dataHotelAvailability.hotelAvailability,
          roomRates: [
            {
              ratePlanCode: 'FLEXRATE',
              promotionCode: 'UNIQUEPROMO',
              roomTypes: [
                {
                  roomType: 'DB',
                  adults: 1,
                  children: 0,
                  rooms: [],
                },
              ],
            },
          ],
        },
        ratesInformation: {
          rateClassifications: [
            {
              rateClassification: 'FLEXRATE',
              rateDescription: 'Flex rate',
              rateName: 'Flex',
              rateOrder: '1',
              rateTags: ['Special'],
            },
          ],
        },
      },
    };
    updateHotelDisplayPageAnalytics(inputWithUniquePromo);
    const lastCall = analyticsUpdateSpy.mock.calls[0][0];
    expect(lastCall.promo).toEqual({
      promoName: 'UNIQUE',
      promoCode: 'UNIQUEPROMO',
    });
  });

  describe('PromoId branch coverage', () => {
    afterEach(() => {
      analyticsUpdateSpy.mockReset();
    });

    it('should use urlPromo when it matches promoState.code', () => {
      const inputMatchingPromo = {
        ...mockInput,
        multiSearchParams: {
          ...mockInput.multiSearchParams,
          PROMOID: 'MATCHPROMO',
        },
        promoActions: {
          promoState: {
            code: 'MATCHPROMO',
          },
        },
      };

      updateHotelDisplayPageAnalytics(inputMatchingPromo);

      const lastCall = analyticsUpdateSpy.mock.calls[0][0];
      expect(lastCall.promo.promoCode).toBe('MATCHPROMO');
    });

    it('should set PromoId to empty string if urlPromo exists but does not match bannerPromo', () => {
      const inputWithPromoMismatch = {
        ...mockInput,
        multiSearchParams: {
          ...mockInput.multiSearchParams,
          PROMOID: 'URLPROMO',
        },
        promoActions: {
          promotionBannerData: {
            promotionCode: 'BANNERPROMO',
          },
        },
      };

      updateHotelDisplayPageAnalytics(inputWithPromoMismatch);

      const lastCall = analyticsUpdateSpy.mock.calls[0][0];
      expect(lastCall.promo.promoCode).toBe('');
      expect(lastCall.promo.eligibility).toBe(false);
      expect(lastCall.promo.promoJourney).toBe(false);
    });
  });

  describe('rateName branch coverage', () => {
    afterEach(() => {
      analyticsUpdateSpy.mockReset();
    });

    it('should return empty string when promoDetails is null or rateTags missing', () => {
      const inputWithoutRateTags = {
        ...mockInput,
        dataHotelAvailability: {
          ...mockInput.dataHotelAvailability,
          ratesInformation: {
            rateClassifications: [],
          },
          hotelAvailability: {
            ...mockInput.dataHotelAvailability.hotelAvailability,
            roomRates: [],
          },
        },
      };

      updateHotelDisplayPageAnalytics(inputWithoutRateTags);

      const lastCall = analyticsUpdateSpy.mock.calls[0][0];
      expect(lastCall.promo.promoName).toBe('');
    });
  });
});
