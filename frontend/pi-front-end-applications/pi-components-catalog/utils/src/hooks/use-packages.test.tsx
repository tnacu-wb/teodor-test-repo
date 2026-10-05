import { renderHook } from '@testing-library/react';

import usePackages from './use-packages';

const mockCustomLocale = jest.fn();
jest.mock('./use-custom-locale', () => () => mockCustomLocale());

const mockQueryRequest = jest.fn();
jest.mock('./use-request', () => ({
  ...jest.requireActual('./use-request'),
  useQueryRequest: () => mockQueryRequest(),
}));

const mockQueryRequestResponse = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    packages: {
      hotelHasCityTaxForBusiness: false,
      hotelHasCityTaxForLeisure: false,
      restaurant: {
        logoSrc: 'logoSrc',
        restaurantNotFound: false,
        noMealsFound: false,
        messageHeader: 'Message header',
        messageDescription: 'Message description',
      },
      privacyPolicy: {
        name: 'privacy policy',
        description: 'privacy policy description',
        linkLabel: 'See privacy policy',
        linkSrc: 'privacyPolicyUrl',
        moreInfoLabel: 'More info',
        moreInfo: [
          {
            image: 'image.png',
            description: 'description',
          },
        ],
      },
      packages: {
        meals: [
          {
            bardId: 's',
            allergyInfoLabel: 'Allergy & nutrition info',
            allergyInfoSrc: '/content/dam/global/restaurants/Global/breakfast-allergy.pdf',
            currency: 'GBP',
            description:
              '<p>Add our unlimited, all-you-can-eat breakfast and look forward to freshly cooked bacon, fluffy hash browns, succulent sausages, eggs the way you like them, cereals, fresh fruit, croissants and much more.</p>\r\n',
            id: 'BFADBF',
            imageSrc: '/content/dam/global/restaurants/Global/full-breakfast-booking.png',
            name: 'Premier Inn Breakfast',
            price: 9.5,
            order: 2,
            freeBreakfastOption: true,
            freeBreakfastCode: 'BFCHDF',
            freeBreakfastMaxPerMeal: 2,
          },
          {
            allergyInfoLabel: 'Allergy & nutrition info',
            allergyInfoSrc: '/content/dam/global/restaurants/Global/breakfast-allergy.pdf',
            currency: 'GBP',
            description:
              '<p>A lighter start with tasty pastries, American pancakes, fruit and cereals. Includes smoothies and juices.</p>\r\n',
            id: 'BFADCT',
            imageSrc: '/content/dam/global/restaurants/Global/continental-breakfast-booking.png',
            name: 'Continental Breakfast',
            price: 7.5,
            order: 3,
            freeBreakfastOption: false,
            freeBreakfastCode: '',
            freeBreakfastMaxPerMeal: 2,
          },
          {
            allergyInfoLabel: null,
            allergyInfoSrc: null,
            currency: 'GBP',
            description:
              '<p>Save up to 20% off your bill with our Meal Deal offer! Enjoy a delicious two-course dinner, a selected drink* and wake up to our famous, unlimited all-you-can-eat Premier Inn Breakfast the next day.</p>\r\n',
            id: 'MDP',
            imageSrc: '/content/dam/global/restaurants/Global/meal-deal-booking.png',
            name: 'Meal Deal',
            price: 24.99,
            order: 0,
            freeBreakfastOption: true,
            freeBreakfastCode: 'BFCHDF',
            freeBreakfastMaxPerMeal: 2,
          },
        ],
        mealsKids: [
          {
            allergyInfoLabel: null,
            allergyInfoSrc: null,
            currency: null,
            description:
              '<p>Up to two kids eat breakfast for free when an adult orders a Premier Inn Breakfast or Meal Deal.</p>\r\n',
            id: 'BFCHDF',
            imageSrc: '/content/dam/global/restaurants/Global/child-breakfast.jpg',
            name: 'Free breakfast for kids',
            order: 0,
          },
        ],
        roomSelection: [
          {
            packagesSelection: [
              {
                id: 'BFADCT',
                noOfSelections: 1,
              },
            ],
          },
        ],
      },
    },
  },
};

const testInput = {
  adultsNumber: 1,
  childrenNumber: 0,
  nightsNumber: 1,
  basketReferenceId: 'GAA1828883',
  hotelId: 'MANOLD',
  startDate: '2023-05-14',
  endDate: '2023-05-15',
  bookingFlowId: 'booking-a1',
  options: [],
  upsellItemsAllowed: undefined,
};

describe('usePackages', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });
    mockQueryRequest.mockReturnValue(mockQueryRequestResponse);
  });

  afterEach(() => {
    mockQueryRequest.mockReset();
  });

  it('should return packages data, error obj, isLoading and isError flags', () => {
    const { result } = renderHook(() => usePackages(testInput));
    const {
      isLoading,
      isError,
      error,
      packages,
      privacyPolicy,
      restaurant,
      hotelHasCityTaxForBusiness,
      hotelHasCityTaxForLeisure,
    } = result.current;

    expect(isLoading).toBe(false);
    expect(isError).toBe(false);
    expect(error).toEqual({ message: '' });
    expect(packages).toEqual(mockQueryRequestResponse.data.packages.packages);
    expect(privacyPolicy).toEqual(mockQueryRequestResponse.data.packages.privacyPolicy);
    expect(restaurant).toEqual(mockQueryRequestResponse.data.packages.restaurant);
    expect(hotelHasCityTaxForBusiness).toEqual(
      mockQueryRequestResponse.data.packages.hotelHasCityTaxForBusiness
    );
    expect(hotelHasCityTaxForLeisure).toEqual(
      mockQueryRequestResponse.data.packages.hotelHasCityTaxForLeisure
    );
  });

  it('should return default packages data, error obj, isLoading and isError flags if request gives no data back', () => {
    mockQueryRequest.mockReturnValue({ ...mockQueryRequestResponse, data: null });
    const { result } = renderHook(() => usePackages(testInput));
    const {
      isLoading,
      isError,
      error,
      packages,
      privacyPolicy,
      restaurant,
      hotelHasCityTaxForBusiness,
      hotelHasCityTaxForLeisure,
    } = result.current;

    expect(isLoading).toBe(false);
    expect(isError).toBe(false);
    expect(error).toEqual({ message: '' });
    expect(packages).toEqual({});
    expect(privacyPolicy).toEqual({});
    expect(restaurant).toEqual({});
    expect(hotelHasCityTaxForBusiness).toEqual(false);
    expect(hotelHasCityTaxForLeisure).toEqual(false);
  });

  it('should call useQueryRequest when retrieving packages', () => {
    renderHook(() => usePackages(testInput));

    expect(mockQueryRequest).toHaveBeenCalledTimes(1);
  });

  it('should call useQueryRequest when retrieving packages', () => {
    testInput.upsellItemsAllowed = ['s'];
    renderHook(() => usePackages(testInput));

    expect(mockQueryRequest).toHaveBeenCalledTimes(1);
  });
});
