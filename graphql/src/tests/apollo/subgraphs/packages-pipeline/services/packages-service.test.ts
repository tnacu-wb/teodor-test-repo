import { packages } from '../../../../../apollo/subgraphs/packages-pipeline/service/packages-service';
import { get } from '../../../../../apollo/client/rest-client';
import { PipelineManager } from '../../../../../apollo/pipeline/manager/PipelineManager';
import { ActionContextKeys } from '../../../../../apollo/subgraphs/packages-pipeline/actions/ActionContextKeys';

jest.mock('../../../../../apollo/client/rest-client');

describe('packages', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const packagesCriteria = {
    adultsNumber: 2,
    childrenNumber: 0,
    nightsNumber: 1,
    basketReferenceId: 'null',
    hotelId: 'LONEUS',
    startDate: '2025-06-21',
    endDate: '2025-06-22',
    bookingFlowId: 'booking-a1',
    language: 'en',
    country: 'gb',
    channel: 'PI'
  };

  const bookingInfoAEMMock = {
    upsellItems: [
      {
        code: 'BFADBF',
        order: 1,
        bartId: 11
      },
      {
        code: 'BFGROL',
        order: 1,
        bartId: 11
      },
      {
        code: 'DBR',
        order: 1,
        bartId: 11
      }
    ],
    restaurantClosedTitle: 'Restaurant unavailable',
    restaurantClosedMessage:
      "We're sorry, the restaurant at this hotel is closed on your selected dates.",
    privacyPolicy: {
      name: 'Personal Data',
      description: 'Collect Data'
    }
  };

  const mealsAemMock = {
    upsellItems: [
      {
        code: 'BFADBF',
        name: 'Premier Inn Breakfast',
        description: 'Description of Premier Inn Breakfast',
        shortDescription: 'Unlimited breakfast.',
        additionalInfo: '',
        images: ['breakfast.jpg'],
        attachments: [
          {
            path: 'breakfast.pdf',
            label: 'Breakfast menu',
            type: 'menu'
          }
        ],
        show: true,
        freeBreakfastOption: true,
        freeBreakfastCode: 'BFCHDF',
        freeBreakfastTrigger: true,
        order: 2,
        freeBreakfastMaxPerMeal: 2
      },
      {
        code: 'BFCHDF',
        name: 'Free breakfast for kids',
        description: 'Description of Free Breakfast for Kids',
        shortDescription:
          'Up to two kids eat breakfast for free when an adult orders a Premier Inn Breakfast or Meal Deal.',
        additionalInfo: '',
        images: ['kids-breakfast.jpg'],
        attachments: [
          {
            path: 'allergy-nutrition-breakfast.pdf',
            label: 'Allergy & nutrition info',
            type: 'allergy'
          }
        ],
        show: true,
        freeBreakfastOption: false,
        freeBreakfastCode: '',
        freeBreakfastTrigger: false,
        order: 0,
        freeBreakfastMaxPerMeal: 2
      },
      {
        code: 'DBR',
        name: 'Dinner and Drink Bundle for £19.99',
        description: '2-course dinner and drink £19.99 per adult.',
        shortDescription: '2-course dinner and drink £19.99 per adult.',
        additionalInfo: '',
        images: ['pi-meal-deal.jpg'],
        attachments: [
          {
            path: 'allergy-nutrition-breakfast.pdf',
            label: 'Allergy & nutrition info',
            type: 'allergy'
          }
        ],
        show: true,
        freeBreakfastOption: true,
        freeBreakfastCode: 'DBCHDF',
        freeBreakfastTrigger: false,
        upsellType: 'dinner',
        freeBreakfastMaxPerMeal: '2',
        order: 4
      },
      {
        code: 'DBCHDF',
        name: 'Free Childrens Dinner',
        description: 'Description of Free Dinner for Kids',
        shortDescription:
          'With every paying adult  selecting the 2-course dinner and drink £19.99, a child will eat dinner for free. Please select to add to booking.',
        additionalInfo: '',
        images: ['kids-dinner.jpg'],
        attachments: [
          {
            path: 'allergy-nutrition-breakfast.pdf',
            label: 'Allergy & nutrition info',
            type: 'allergy'
          }
        ],
        show: true,
        freeBreakfastOption: false,
        freeBreakfastCode: '',
        freeBreakfastTrigger: false,
        order: 0,
        freeBreakfastMaxPerMeal: 5
      }
    ]
  };

  const logoAemMock = {
    restaurant: {
      menus: [
        {
          name: 'Breakfast',
          description: 'Breakfast menu',
          imageSrc: 'breakfast.jpg',
          menuSrc: 'breakfast.pdf',
          menuLabel: 'Breakfast Menu',
          disclaimer:
            'Depending on your selected hotel and dates you stay, we will be offering different breakfast and evening options.'
        }
      ],
      logoSrc: 'logo.jpg',
      name: 'Thyme Bar & Grill',
      description:
        'Our delicious in-house restaurant serves up everything from classic dishes to gourmet meals.',
      bookingCardImage: '/content/dam/premier-inn/food-at-the-social.jpg',
      bookingCardTitle: 'Food at the Social',
      bookingCardDescription: 'Book a table at our Restaurant',
      bookingCardBackgroundImage: '/content/dam/global/restaurants/BackgroundImageRestaurants.png'
    }
  };

  const operaPackagesMock = {
    restaurant: {
      logoSrc: null,
      restaurantNotFound: false,
      noMealsFound: false
    },
    packages: {
      meals: [
        {
          name: 'Premier Inn Breakfast Food',
          id: 'BFADBF',
          price: 99,
          idImg: null,
          idDesc: null,
          allergyInfoSrc: null,
          currency: 'GBP'
        },
        {
          name: 'Dinner and Drink Bundle',
          id: 'DBR',
          price: 1999,
          idImg: null,
          idDesc: null,
          allergyInfoSrc: null,
          currency: 'GBP'
        }
      ],
      extrasItems: [
        {
          id: 'HSCKIN',
          name: 'Early check-in',
          description: 'Check in any time from 11am (normal check-in time is 3pm).',
          imageSrc: 'early-check-in.png',
          price: 10,
          currency: 'GBP',
          available: 27,
          order: 2
        }
      ]
    },
    hotelHasCityTaxForLeisure: false,
    hotelHasCityTaxForBusiness: false
  };

  const hotelInfoMock = {
    ancillaryCloseout: {
      items: [
        {
          startDate: '2025-06-01',
          endDate: '2025-06-30',
          upsellCodes: 'BFADBF,BFGROL,DBR'
        }
      ]
    }
  };

  it('should return the correct packages data when provided with correct packagesCriteria', async () => {
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(bookingInfoAEMMock));
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(mealsAemMock));
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(logoAemMock));
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(operaPackagesMock));

    const response = await packages({ packagesCriteria }, context);

    expect(response.restaurant.restaurantNotFound).toBeFalsy();
    expect(response.restaurant.noMealsFound).toBeFalsy();
    expect(response.packages.meals.length).toBeGreaterThan(0);
    expect(response.packages.meals[0].id).toEqual('BFADBF');
    expect(response.packages.meals[0].price).toEqual(99);
    expect(response.packages.meals[0].bartId).toEqual(11);
    expect(response.packages.extrasItems[0].id).toEqual('HSCKIN');

    const expectedKidsMealCodes = mealsAemMock.upsellItems
      .filter((item) => item.freeBreakfastOption)
      .map((item) => item.freeBreakfastCode)
      .filter(Boolean);

    const kidsMeals = response.packages.mealsKids;
    expect(kidsMeals).toBeDefined();
    expect(kidsMeals.length).toBe(expectedKidsMealCodes.length);

    expectedKidsMealCodes.forEach((code) => {
      const kidMeal = kidsMeals.find((meal: any) => meal.id === code);
      expect(kidMeal).toBeDefined();
      expect(kidMeal.allergyInfoLabel).toEqual('Allergy & nutrition info');
    });
  });

  it('should have no meals when channel is DISTR and hotelInfo has ancillaryCloseout within given date range', async () => {
    const packagesCriteriaDISTR = {
      ...packagesCriteria,
      channel: 'DISTR'
    };

    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(bookingInfoAEMMock));
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(mealsAemMock));
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(logoAemMock));
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(operaPackagesMock));
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(hotelInfoMock));

    const response = await packages({ packagesCriteria: packagesCriteriaDISTR }, context);

    expect(response.restaurant.noMealsFound).toBeTruthy();
    expect(response.packages.meals.length).toEqual(0);
    expect(response.restaurant.messageHeader).toEqual('Restaurant unavailable');
    expect(response.restaurant.messageDescription).toEqual(
      "We're sorry, the restaurant at this hotel is closed on your selected dates."
    );
  });

  it('should handle errors gracefully when fetching packages', async () => {
    const expectedMessage = 'getBookingInfoAemResponse response is empty';
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(null));

    await expect(packages({ packagesCriteria }, context)).rejects.toThrow(expectedMessage);
  });

  it('should keep HSCKIN and HSCOU2 in roomSelection even when they are not in extrasItems', async () => {
    const pipelineContextMap = new Map<ActionContextKeys, any>();
    pipelineContextMap.set(ActionContextKeys.GET_SAVED_PACKAGES_OPERA, {
      roomsSelections: [
        {
          reservationId: 'RES-1',
          packagesSelection: [
            { id: 'HSCKIN', noOfSelections: 1 },
            { id: 'HSCOU2', noOfSelections: 1 },
            { id: 'NOT_ALLOWED', noOfSelections: 1 }
          ]
        }
      ],
      roomsSelectionsAmendExtras: []
    });
    pipelineContextMap.set(ActionContextKeys.GET_BOOKING_INFO_AEM, bookingInfoAEMMock);
    pipelineContextMap.set(ActionContextKeys.GET_MEALS_AEM, { upsellItems: [] });
    pipelineContextMap.set(ActionContextKeys.GET_LOGO_AEM, logoAemMock);
    pipelineContextMap.set(ActionContextKeys.GET_PACKAGES_OPERA, {
      restaurant: { restaurantNotFound: false },
      packages: { meals: [], mealsKids: [], extrasItems: [] }
    });

    const manageSpy = jest.spyOn(PipelineManager.prototype, 'manage').mockResolvedValueOnce({
      get: (key: ActionContextKeys) => pipelineContextMap.get(key)
    } as any);

    const response = await packages({ packagesCriteria }, context);

    const selectedIds = response.packages.roomSelection[0].packagesSelection.map((p: any) => p.id);
    expect(selectedIds).toEqual(['HSCKIN', 'HSCOU2']);

    manageSpy.mockRestore();
  });
});
