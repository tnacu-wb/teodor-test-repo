/* eslint-disable @typescript-eslint/no-explicit-any */
import { differenceInDays } from 'date-fns';

import analytics from './analytics';
import updateSearchResultsAnalytics, {
  getHotelLabel,
  getSearchResults,
  labelsConstants,
} from './searchResultsAnalytics';

const analyticsUpdateSpy = jest.spyOn(analytics, 'update').mockReturnValue(undefined);

const mockInput = {
  isNewSearch: false,
  multiSearchParams: {
    arrivalDay: 1,
    arrivalMonth: 1,
    arrivalYear: 1,
    coordinates: '',
    bookingChannel: '',
    location: '',
    numberOfNights: 1,
    placeId: '',
    rooms: [{ type: '', adultsNumber: 1, childrenNumber: 1 }],
    sort: '',
    filters: 'none',
  } as any,
  startDate: new Date('2023-05-10'),
  endDate: new Date('2023-05-15'),
  viewType: 'list view',
  searchResults: 2,
  searchResultsDisplayed: [
    {
      bookingSystem: 'Opera',
      hotelAvailability: {
        available: true,
        pmsSource: 'OPERA' as any,
        distance: 1,
        unit: 'km',
        limitedAvailability: false,
        lowestRoomRate: { netTotal: 200, currencyCode: 'GBP' },
      },
      hotelCode: 'manold',
      hotelId: 'MANOLD',
      name: 'Manchester Old Trafford',
      hotelInformation: {
        thumbnailImages: [{ imageSrc: 'link.png', tags: ['hotel'] }],
        coordinates: { latitude: 23, longitude: 56 },
        messagingFlag: { text: 'hub', color: 'purple' },
        brand: 'PI',
        links: { detailsPage: 'details page' },
        hotelOpeningDate: '',
        hotelFacilities: [
          {
            code: 'DIS',
            description: 'Barrierefreie Zimmer',
            icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIS.svg',
            isVisible: true,
            name: 'Barrierefreie Zimmer',
            weight: 0,
          },
          {
            code: 'LFT',
            description: 'Fahrstuhl',
            icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/LFT.svg',
            isVisible: false,
            name: 'Fahrstuhl',
            weight: 1,
          },
        ],
      },
      hotelDistance: 0,
      hotelFacilityIcons: [],
      hotelLabel: 'Manchester Old Trafford',
      imageLabel: '',
      priceFrom: '',
      voucherCode: '',
    },
    {
      bookingSystem: 'Opera',
      hotelAvailability: {
        available: true,
        pmsSource: 'OPERA' as any,
        distance: 1,
        unit: 'km',
        limitedAvailability: false,
        lowestRoomRate: { netTotal: 200, currencyCode: 'GBP' },
        hasMlosRestriction: true,
      },
      hotelCode: 'heapti',
      hotelId: 'HEAPTI',
      name: 'London Heathrow Airport (M4/J4)',
      hotelInformation: {
        thumbnailImages: [{ imageSrc: 'link.png', tags: ['hotel'] }],
        coordinates: { latitude: 23, longitude: 56 },
        messagingFlag: { text: 'New hotel', color: 'purple' },
        brand: 'PI',
        links: { detailsPage: 'details page' },
        hotelOpeningDate: '',
        hotelFacilities: [
          {
            code: 'PRR',
            description: 'Premier Plus room',
            icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/PRR.svg',
            isVisible: false,
            name: 'Premier Plus room',
            weight: 1,
          },
        ],
      },
      hotelDistance: 0,
      hotelFacilityIcons: [],
      hotelLabel: 'London Heathrow Airport (M4/J4)',
      imageLabel: '',
      priceFrom: '',
      voucherCode: '',
    },
  ],
  addedResults: 1,
};

describe('searchResultsAnalytics', () => {
  describe('updateSearchResultsAnalytics Method', () => {
    afterEach(() => {
      analyticsUpdateSpy.mockReset();
    });

    it('should call analytics.update with correct values', () => {
      updateSearchResultsAnalytics(mockInput);
      expect(analyticsUpdateSpy).toHaveBeenCalledWith({
        analyticsDataSearchResult: {
          newSearch: false,
          addedResults: 1,
          searchCheckInDate: '10/05/2023',
          searchCheckOutDate: '15/05/2023',
          searchDaysToCheckIn: differenceInDays(new Date('2023-05-10'), new Date()),
          searchFilter: 'none',
          searchNumberOfAdults: 1,
          searchNumberOfChildren: 1,
          searchNumberOfGuests: 2,
          searchNumberOfNights: 1,
          searchNumberOfRooms: 1,
          searchResults: 2,
          searchResultsDisplayed: [
            {
              bookingSystem: 'Opera',
              hotelAvailability: 'available',
              hotelCode: 'manold',
              hotelDistance: 1,
              hotelFacilityIcons: [
                '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIS.svg',
                '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/LFT.svg',
              ],
              hotelLabel: ['no label'],
              hotelResultPosition: 1,
              hotelRates: [
                {
                  cellCode: '',
                  currencyCode: 'GBP',
                  description: '',
                  lettingType: '',
                  price: '200.00',
                  rateCode: '',
                  text: '',
                },
              ],
            },
            {
              bookingSystem: 'Opera',
              hotelAvailability: 'available',
              hotelCode: 'heapti',
              hotelDistance: 1,
              hotelFacilityIcons: [
                '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/PRR.svg',
              ],
              hotelLabel: ['New hotel', 'MLOS', 'Premier Plus'],
              hotelRates: [
                {
                  cellCode: '',
                  currencyCode: 'GBP',
                  description: '',
                  lettingType: '',
                  price: '200.00',
                  rateCode: '',
                  text: '',
                },
              ],
              hotelResultPosition: 2,
            },
          ],
          searchRoomType: '',
          searchSort: '',
          searchTerm: '',
          searchType: 'list view',
          searchWeekdayFrom: 'wed',
          searchWeekdayFromTo: 'wed-mon',
          searchWeekdayTo: 'mon',
        },
        promo: {
          bannerType: undefined,
          promoName: undefined,
          promoCode: '',
          eligibility: false,
        },
      });
    });

    it('should call analytics.update with correct values', () => {
      mockInput.addedResults = 5;
      mockInput.viewType = 'map view';
      mockInput.multiSearchParams.rooms = [{ type: 'TWIN', adultsNumber: 1, childrenNumber: 1 }];
      mockInput.multiSearchParams.sort = 'Ascending';
      mockInput.multiSearchParams.location = 'LONDON';

      updateSearchResultsAnalytics(mockInput);

      expect(analyticsUpdateSpy).toHaveBeenCalledWith({
        analyticsDataSearchResult: {
          newSearch: false,
          addedResults: 5,
          searchCheckInDate: '10/05/2023',
          searchCheckOutDate: '15/05/2023',
          searchDaysToCheckIn: differenceInDays(new Date('2023-05-10'), new Date()),
          searchFilter: 'none',
          searchNumberOfAdults: 1,
          searchNumberOfChildren: 1,
          searchNumberOfGuests: 2,
          searchNumberOfNights: 1,
          searchNumberOfRooms: 1,
          searchResults: 2,
          searchResultsDisplayed: [],
          searchRoomType: 'Twin room',
          searchSort: 'ascending',
          searchTerm: 'london',
          searchType: 'map view',
          searchWeekdayFrom: 'wed',
          searchWeekdayFromTo: 'wed-mon',
          searchWeekdayTo: 'mon',
        },
        promo: {
          promoCode: '',
          promoName: undefined,
          eligibility: false,
        },
      });
    });

    describe('getSearchResults Method', () => {
      it('should format search results displayed for available hotel', () => {
        const hotelInformation = getSearchResults(
          mockInput.searchResultsDisplayed,
          labelsConstants
        );
        expect(hotelInformation).toEqual([
          {
            bookingSystem: 'Opera',
            hotelAvailability: 'available',
            hotelCode: 'manold',
            hotelDistance: 1,
            hotelFacilityIcons: [
              '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIS.svg',
              '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/LFT.svg',
            ],
            hotelLabel: ['no label'],
            hotelRates: [
              {
                cellCode: '',
                currencyCode: 'GBP',
                description: '',
                lettingType: '',
                price: '200.00',
                rateCode: '',
                text: '',
              },
            ],
            hotelResultPosition: 1,
          },
          {
            bookingSystem: 'Opera',
            hotelAvailability: 'available',
            hotelCode: 'heapti',
            hotelDistance: 1,
            hotelFacilityIcons: [
              '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/PRR.svg',
            ],
            hotelLabel: ['New hotel', 'MLOS', 'Premier Plus'],
            hotelRates: [
              {
                cellCode: '',
                currencyCode: 'GBP',
                description: '',
                lettingType: '',
                price: '200.00',
                rateCode: '',
                text: '',
              },
            ],
            hotelResultPosition: 2,
          },
        ]);
      });

      it('should format search results displayed for unavailable hotel', () => {
        mockInput.searchResultsDisplayed[0].hotelAvailability.available = false;

        const hotelInformation = getSearchResults(
          mockInput.searchResultsDisplayed,
          labelsConstants,
          new Date('2023-05-20')
        );
        expect(hotelInformation).toEqual([
          {
            bookingSystem: 'Opera',
            hotelAvailability: 'unavailable',
            hotelCode: 'manold',
            hotelDistance: 1,
            hotelFacilityIcons: [
              '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIS.svg',
              '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/LFT.svg',
            ],
            hotelLabel: ['no label'],
            hotelRates: [
              {
                cellCode: '',
                currencyCode: 'GBP',
                description: '',
                lettingType: '',
                price: '200.00',
                rateCode: '',
                text: '',
              },
            ],
            hotelResultPosition: 1,
          },
          {
            bookingSystem: 'Opera',
            hotelAvailability: 'available',
            hotelCode: 'heapti',
            hotelDistance: 1,
            hotelFacilityIcons: [
              '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/PRR.svg',
            ],
            hotelLabel: ['New hotel', 'MLOS', 'Premier Plus'],
            hotelRates: [
              {
                cellCode: '',
                currencyCode: 'GBP',
                description: '',
                lettingType: '',
                price: '200.00',
                rateCode: '',
                text: '',
              },
            ],
            hotelResultPosition: 2,
          },
        ]);
      });
    });

    describe('getHotelLabel Method', () => {
      let mockHotelInput;

      beforeEach(() => {
        mockHotelInput = { ...mockInput };
      });

      it('should return no label if hotel is available and does not have limited availability', () => {
        const testHotel = { ...mockHotelInput.searchResultsDisplayed[0] };
        testHotel.hotelAvailability.available = true;
        const label = getHotelLabel(testHotel, labelsConstants, new Date('2023-05-20'));
        expect(label).toEqual(['no label']);
      });

      it('should return no label if hotel is unavailable', () => {
        const testHotel = { ...mockHotelInput.searchResultsDisplayed[0] };
        testHotel.hotelAvailability.available = false;
        const label = getHotelLabel(testHotel, labelsConstants, new Date('2023-05-20'));
        expect(label).toEqual(['no label']);
      });

      it('should return Premier Plus if hotel has Premier Plus facility', () => {
        const testHotel = { ...mockHotelInput.searchResultsDisplayed[1] };
        const label = getHotelLabel(testHotel, labelsConstants, new Date('2023-05-20'));
        expect(label).toEqual(['New hotel', 'MLOS', 'Premier Plus']);
      });

      it('should return last few rooms if the hotel has limited availability', () => {
        const testHotel = { ...mockHotelInput.searchResultsDisplayed[0] };
        testHotel.hotelAvailability.available = true;
        testHotel.hotelAvailability.limitedAvailability = true;
        const label = getHotelLabel(testHotel, labelsConstants, new Date('2023-05-20'));
        expect(label).toEqual(['last few rooms']);
      });

      it('should return open soon if the hotel has an assigned hotelOpeningDate', () => {
        const testHotel = { ...mockHotelInput.searchResultsDisplayed[0] };
        testHotel.hotelAvailability.available = true;
        testHotel.hotelAvailability.limitedAvailability = false;
        testHotel.hotelInformation.hotelOpeningDate = '2023-05-15';
        const label = getHotelLabel(testHotel, labelsConstants, new Date('2023-05-10'));
        expect(label).toEqual(['open soon']);
      });
    });

    it('should return empty promoCode when urlPromo does not match bannerPromo', () => {
      const inputWithPromoMismatch = {
        ...mockInput,
        multiSearchParams: {
          ...mockInput.multiSearchParams,
          promoId: 'URLPROMO',
        },
        promotionBannerData: {
          promotionCode: 'BANNERPROMO',
          promoBannerTitle: '<span><b>Free breakfast</b></span> Get 20% off',
        },
      };

      updateSearchResultsAnalytics(inputWithPromoMismatch);

      expect(analyticsUpdateSpy).toHaveBeenCalledWith(
        expect.objectContaining({
          promo: {
            promoName: 'Free breakfast',
            promoCode: '',
            eligibility: false,
          },
        })
      );
    });

    it('should return promoCode when urlPromo matches bannerPromo', () => {
      const inputWithPromoMatch = {
        ...mockInput,
        multiSearchParams: {
          ...mockInput.multiSearchParams,
          promoId: 'MATCHPROMO',
        },
        promotionBannerData: {
          promotionCode: 'MATCHPROMO',
          promoBannerTitle: '<span><b>Free breakfast</b></span> Get 20% off',
        },
      };

      updateSearchResultsAnalytics(inputWithPromoMatch);

      expect(analyticsUpdateSpy).toHaveBeenCalledWith(
        expect.objectContaining({
          promo: {
            promoName: 'Free breakfast',
            promoCode: 'MATCHPROMO',
            eligibility: true,
          },
        })
      );
    });
  });
});
