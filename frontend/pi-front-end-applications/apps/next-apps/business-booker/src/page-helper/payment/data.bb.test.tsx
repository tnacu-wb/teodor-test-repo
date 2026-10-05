import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { ROOM_TYPE } from '@whitbread-eos/api';

import { mockedGetStaticContentData } from '../../mocks/payment';
import createPaymentBbDataLoaderFn from './data.bb';

const mockedBookingInformation = {
  bookingInformation: {
    hotelId: 'LONKIN',
    totalCost: 141,
    currencyCode: 'GBP',
    bookingFlowId: 'booking-hub',
    infoMessages: [''],
    reservationByIdList: [
      {
        roomStay: {
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2023-02-23',
          departureDate: '2023-02-24',
          ratePlanCode: 'FLEXRATE',
          rateExtraInfo: {
            rateName: 'Flex',
          },
          roomExtraInfo: {
            roomType: ROOM_TYPE.STANDARD_BIGGER,
            roomName: 'Bigger Room',
          },
          accessibleRoom: {
            isAccessible: false,
            phoneNumber: '0333 321 3104',
          },
        },
      },
    ],
    upgradeToFlex: {
      amount: null,
      currency: null,
      flexRateCode: null,
    },
  },
};

const mockedHotelInformation = {
  hotelInformation: {
    address: {
      addressLine1: '50 Wharfdale Road',
      addressLine2: 'London',
      addressLine3: '',
      postalCode: 'N1 9FA',
      country: 'United Kingdom (the)',
    },
    name: 'hub London Kings Cross',
    brand: 'HUB',
    announcement: {
      endDate: '21/07/2022',
      showAnnouncement: 'true',
      startDate: '08/01/2021',
      text: 'Get all the latest updates on our response to&nbsp;<a href="/gb/en/covid-19.html" target="_blank"><u>COVID-19</u></a>&nbsp;and see how we’re keeping guests safe with our&nbsp;<a href="/gb/en/why/cleanliness.html" target="_blank"><u>Premier Inn CleanProtect<sup>TM</sup></u></a>&nbsp;promise.&nbsp;<br>\r\n',
      title: '',
      type: 'info',
    },
    importantInfo: null,
  },
};

const mockedPackages = {
  packages: {
    packages: {
      meals: [
        {
          allergyInfoLabel: 'Allergy & nutrition info',
          allergyInfoSrc: '/content/dam/global/restaurants/Global/breakfast-allergy.pdf',
          currency: 'GBP',
          description:
            '<p>Add our unlimited, all-you-can-eat breakfast and look forward to freshly cooked bacon, fluffy hash browns, succulent sausages, eggs the way you like them, cereals, fresh fruit, croissants and much more.</p>\r\n',
          id: 'BFADBF',
          bartId: '11',
          imageSrc: '/content/dam/global/restaurants/Global/full-breakfast-booking.png',
          name: 'Premier Inn Breakfast',
          price: 9.5,
          order: 1,
          freeBreakfastOption: true,
          freeBreakfastCode: 'BFCHDF',
          freeBreakfastMaxPerMeal: 2,
          menu: {
            menuSrc: '/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
            name: 'Breakfast menu',
          },
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
          menu: null,
        },
      ],
    },
    restaurant: {
      logoSrc: '/content/dam/global/restaurants/HDE/lounge logo.jpg',
      messageDescription: null,
      messageHeader: null,
      noMealsFound: false,
      restaurantNotFound: false,
    },
    privacyPolicy: {
      description:
        '<p>We need to collect and keep some mandatory information in order to process your booking. Full details about how we use your data are set out in our Privacy notice. Premier Inn Hotels Limited (company no. 5137608) is a member of the Whitbread Group, the parent of which is Whitbread Group PLC (company no. 29423). Registered office: Whitbread Court, Houghton Hall Business Park, Porz Avenue, Dunstable LU5 5XE.</p>\n',
      linkLabel: 'View our Privacy Notice',
      linkSrc: '/gb/en/terms/privacy-policy.html',
      moreInfoLabel: 'Find out more',
      moreInfo: [
        {
          description:
            '<p><b>VeriSign</b><br>\n<br>\n</p>\n<p>Premier Inn takes the security of any information we hold very seriously, and will always implement security measures that are in line with, or exceed current best practices and recommendations. Where necessary, and in common with other websites, we use SSL (Secure Sockets Layer) encryption to ensure that information provided to us is not visible to anybody else when in transit between your computer and our servers. You can tell when SSL is in use by the presence of a small &quot;padlock&quot; symbol in the status bar or next to the address bar of your web browser. In addition, our web servers are housed behind a secure firewall that prevents access to our databases from unauthorised users. All of our servers are housed in a secure environment with high levels of physical security, and access is only permitted to a handful of security screened staff.</p>\n',
          image: '/content/dam/global/booking/verisign.png',
        },
        {
          description:
            '<p><b>MasterCard</b><br>\n<br>\n</p>\n<p>MasterCard SecureCode is a service to enhance your existing MasterCard account. A private code means added protection against unauthorized use of your card when you shop at participating online retailers. Once youve registered and created your own private SecureCode, you will be automatically prompted by your financial institution at checkout to provide your SecureCode each time you make a purchase with a participating online merchant. Your SecureCode is quickly confirmed by your financial institution and then your purchase is completed. Your SecureCode will never be shared with the merchant. Its just like entering your PIN at an ATM. When you correctly enter your SecureCode during a purchase at a participating online merchant, you confirm that you are the authorized cardholder and your purchase is then completed. If an incorrect SecureCode is entered, the purchase will not be completed. Even if someone knows your credit or debit card number, the purchase cannot be completed without your SecureCode at a participating merchant. How do I sign up for MasterCard?<br>\n<br>\n</p>\n<p>Choosing your own private SecureCode is quick and easy. When shopping online at a participating merchant, you will be prompted to create your own SecureCode prior to checkout. When this happens, a pop up window will appear and you will be guided through the simple enrolment process before your purchase is completed. Once you have created your private SecureCode, you will use it for future purchases at participating online merchants.</p>\n',
          image: '/content/dam/hub/app/MasterCard.jpg',
        },
        {
          description:
            '<p><b>Verified by Visa</b><br>\n<br>\n</p>\n<p>Verified by Visa is a new security service that tells on-line retailers and banks that you are a genuine cardholder when you shop on-line. It allows you to use a personal password to confirm your identity and protect your Visa card when you use your card on the Internet, providing greater reassurance and security. Through a simple checkout process, Verified by Visa confirms your identity when you make purchases in participating online stores. Its convenient and it works with your existing Visa Card. Verified by Visa is easy to use. You register your card just once and create your own password. Then, when you make purchases at participating online stores, a Verified by Visa window will appear. Simply enter your password and click submit. Your identity is verified and your purchase is secure.<br>\n<br>\n</p>\n<p><b>How do I sign up for Verified by Visa?</b><br>\n<br>\n</p>\n<p>Visit the Verified by Visa website to register your Visa Card online, alternatively contact your bank who can register your card for Verified by Visa for you. Once your bank has activated your card, Verified by Visa protects you at every participating on-line store. When you shop at a participating on-line store, your card will be automatically recognized as protected by Verified by Visa. When you are completing your purchase, your issuing bank will verify your password.</p>\n',
          image: '/content/dam/global/booking/privacy_icon_visa_verified.png',
        },
      ],
      name: 'We keep your personal data safe and secure.',
    },
    hotelHasCityTaxForLeisure: false,
    hotelHasCityTaxForBusiness: false,
  },
};

const mockedTermsAndConditions = {
  termsAndConditions:
    '<p>By confirming your booking you are agreeing to our&nbsp;<a href="/content/pi/websites/desktop/gb/en/unsecured/terms/booking-terms-and-conditions.html" target="_blank">Terms and Conditions</a>. Cancellations must be made before 1pm on your arrival day.&nbsp;</p>',
};

const mockedPaymentInforMessages = {
  paymentInfoMessages: [
    {
      paymentType: 'PAY_NOW',
      messages: [
        '<p>Pay now, fully refundable with free cancellation up to 1pm on the day of arrival</p>\n',
        '<p>Unfortunately, Business Account cards cannot be used to book this hotel online.</p>\n',
      ],
    },
    {
      paymentType: 'PAY_ON_ARRIVAL',
      messages: [
        '<p>Pay on arrival, with free cancellation up to 1pm on the day of arrival</p>\n',
        "If you're paying with cash on arrival, we’ll ask for photo ID at reception. You can use any of the following: a passport, driving licence, ID card or a police warrant card.",
        '<p>Unfortunately, Business Account cards cannot be used to reserve this hotel online. You can use a different card to secure your booking now and pay on arrival with your Business Account card.</p>\n',
      ],
    },
  ],
};

const mockedBookingFlowInfo = {
  bookingFlowInformation: {
    brand: 'pi',
    bookingFlowSteps: [
      {
        id: 'ancillaries',
        step: '1',
        title: 'Choose your meals',
      },
      {
        id: 'guestDetails',
        step: '2',
        title: 'Your details',
      },
      {
        id: 'payment',
        step: '3',
        title: 'Payment details',
      },
      {
        id: 'confirmation',
        step: '4',
        title: 'Confirmation',
      },
    ],
  },
};

const mockedData = {
  res: undefined,
  req: { url: undefined, headers: { host: '' } },
  session: {
    accessToken: 'asasasGderg12312',
    user: {},
  },
  query: {
    reservationId: '12',
    BRAND: 'BB',
    'secure-booking': 'true',
  },
  language: 'en',
  country: 'GB',
  resolvedUrl: '',
};
let mockIsJsonValidValue = false;
let mockRedisGetItemValue: string | null = null;

const mockBasketDetailsFromRedis = JSON.stringify({
  bookingFlowId: 'booking-hub',
  hotelId: 'LONKIN',
  rateCode: 'FLEXRATE',
  nightsNumber: 0,
  startDate: '2023-02-23',
  endDate: '2023-02-24',
  childrenNumber: 0,
  adultsNumber: 1,
  reservationId: '12',
});

jest.mock('@whitbread-eos/utils/server', () => {
  const mockGetItem = jest.fn();
  const mockSetItem = jest.fn();
  return {
    RedisStorageServer: {
      getInstance: jest.fn().mockReturnValue({
        getItem: mockGetItem,
        setItem: mockSetItem,
      }),
    },
    RedisKeyPrefix: {
      HRS: 'Hotel-Reservation-Entity-Service::ReservationCache',
    },
    getInnBusinessServerSideProps: jest.fn().mockResolvedValue(undefined),
  };
});

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  mutations: undefined,
  queries: undefined,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  graphQLRequest: jest.fn(),
  isJsonValid: () => mockIsJsonValidValue,
  getNightsNumber: jest.fn().mockImplementation(() => 0),
  getMaxValueFromRoomStays: jest.fn().mockImplementation((reservationList) => {
    // Return undefined if the list is empty or undefined
    if (!reservationList || reservationList.length === 0) {
      return undefined;
    }
    return 0;
  }),
  getAuthCookie: jest.fn().mockImplementation(() => ''),
  encodeToBase64: () => '{}',
  decodeFromBase64: () => '{}',
  getDefaultSessionTracing: () => ({}),
  logger: {
    info: jest.fn(),
    error: jest.fn(),
  },
  QueriesLogger: jest.fn().mockImplementation(() => ({
    fetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      const key = queryKey[0];
      queryFn();
      switch (key) {
        case 'GetBookingInformation':
          // Return empty data when reservationId is undefined/null
          if (!mockedData.query.reservationId) {
            return Promise.resolve({
              bookingInformation: {
                hotelId: undefined,
                bookingFlowId: undefined,
                reservationByIdList: [],
              },
            });
          }
          return Promise.resolve(mockedBookingInformation);
        default:
          return Promise.resolve({});
      }
    }),
    prefetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      const key = queryKey[0];
      queryFn();
      switch (key) {
        case 'GetPackages':
          return Promise.resolve({
            data: mockedPackages,
          });
        case 'GetHotelInformation':
          return Promise.resolve({
            data: mockedHotelInformation,
          });
        case 'GetTermsAndConditions':
          return Promise.resolve({
            data: mockedTermsAndConditions,
          });
        case 'GetPaymentInfoMessages':
          return Promise.resolve({
            data: mockedPaymentInforMessages,
          });
        case 'GetStaticContent':
          return Promise.resolve({
            data: mockedGetStaticContentData,
          });
        case 'GetBookingFlowInformation':
          return Promise.resolve({
            data: mockedBookingFlowInfo,
          });
        case 'basket':
          return Promise.resolve({
            paymentOption: 'RESERVE_WITHOUT_CARD',
            status: 'COMPLETED',
          });
        default:
          return Promise.resolve({});
      }
    }),
    logQueries: jest.fn(),
  })),
  useMutationRequest: () => ({
    mutation: {
      mutate: jest.fn(),
    },
    isSuccess: false,
  }),
}));

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: () => null,
    set: () => null,
  }));
});

const queryClient = new ReactQuery.QueryClient();

const expectedResult = {
  basketReference: '',
  dehydratedState: { mutations: [], queries: [] },
  pcksQueryInput: {
    country: 'GB',
    language: 'en',
    hotelId: '',
    channel: 'BB',
    adultsNumber: undefined,
    childrenNumber: undefined,
    startDate: '',
    endDate: '',
    bookingFlowId: '',
    nightsNumber: 0,
    basketReferenceId: '',
  },
  hiQueryInput: { country: 'GB', language: 'en', hotelId: '' },
  innBusiness: undefined,
};
const expectedResultWIthJsonValidFalse = {
  dehydratedState: { mutations: [], queries: [] },
  pcksQueryInput: {
    country: 'GB',
    language: 'en',
    hotelId: 'LONKIN',
    channel: 'BB',
    adultsNumber: 0,
    childrenNumber: 0,
    startDate: '2023-02-23',
    endDate: '2023-02-24',
    bookingFlowId: 'booking-hub',
    nightsNumber: 0,
    basketReferenceId: '12',
  },
  hiQueryInput: { country: 'GB', language: 'en', hotelId: 'LONKIN' },
  basketReference: '12',
  innBusiness: undefined,
};

describe('createPaymentBbDataLoaderFn', () => {
  let actualMockGetItem: jest.Mock;
  let actualMockSetItem: jest.Mock;

  beforeEach(() => {
    // Reset mockedData.query to original values
    mockedData.query = {
      reservationId: '12',
      BRAND: 'BB',
      'secure-booking': 'true',
    };

    mockRedisGetItemValue = mockBasketDetailsFromRedis;
    mockIsJsonValidValue = true;

    // Get references to the actual mocks from the jest.mock
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    const utilsServer = require('@whitbread-eos/utils/server');
    const mockInstance = utilsServer.RedisStorageServer.getInstance();
    actualMockGetItem = mockInstance.getItem as jest.Mock;
    actualMockSetItem = mockInstance.setItem as jest.Mock;

    actualMockGetItem.mockResolvedValue(mockRedisGetItemValue);
    actualMockSetItem.mockResolvedValue(undefined);
  });

  it('should create data loader with expected object', async () => {
    mockIsJsonValidValue = false;
    const dataLoaderBB = await createPaymentBbDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderBB).toStrictEqual(expectedResultWIthJsonValidFalse);
  });
  it('should create data loader(with no query client data) with expected object', async () => {
    mockedData.query = { reservationId: undefined, BRAND: undefined };
    mockRedisGetItemValue = null;
    mockIsJsonValidValue = false;
    actualMockGetItem.mockResolvedValue(null);
    const dataLoaderBB = await createPaymentBbDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderBB).toEqual({ ...expectedResult });
  });

  it('should handle secure-booking query param and fetch basket status', async () => {
    mockIsJsonValidValue = false;
    mockedData.query = {
      reservationId: '12',
      BRAND: 'BB',
      'secure-booking': 'true',
    };
    const dataLoaderBB = await createPaymentBbDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderBB.basketReference).toBe('12');
    expect(dataLoaderBB.pcksQueryInput.basketReferenceId).toBe('12');
    expect(dataLoaderBB.innBusiness).toBeUndefined();
  });

  it('should handle missing reservationId and fallback to empty basketReference', async () => {
    mockIsJsonValidValue = false;
    mockedData.query = {};
    const dataLoaderBB = await createPaymentBbDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderBB.basketReference).toBe('');
    expect(dataLoaderBB.pcksQueryInput.basketReferenceId).toBe('');
  });

  it('should not call innBusinessServerSideProps if not innBusinessApp', async () => {
    mockIsJsonValidValue = false;
    const featureToggles = {};
    const dataLoaderBB = await createPaymentBbDataLoaderFn({
      ...mockedData,
      queryClient,
      featureToggles,
      req: { url: undefined, headers: { host: 'notinnbusiness.com' } },
    });
    expect(dataLoaderBB.innBusiness).toBeUndefined();
  });

  it('should handle missing hotelId in basketDetails gracefully', async () => {
    mockRedisGetItemValue = null;
    mockIsJsonValidValue = false;
    mockedData.query = { reservationId: undefined, BRAND: undefined };
    actualMockGetItem.mockResolvedValue(null);
    const dataLoaderBB = await createPaymentBbDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderBB.pcksQueryInput.hotelId).toBe('');
    expect(dataLoaderBB.hiQueryInput.hotelId).toBe('');
  });

  it('should handle missing adultsNumber and childrenNumber in basketDetails', async () => {
    mockRedisGetItemValue = null;
    mockIsJsonValidValue = false;
    mockedData.query = { reservationId: undefined, BRAND: undefined };
    actualMockGetItem.mockResolvedValue(null);
    const dataLoaderBB = await createPaymentBbDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderBB.pcksQueryInput.adultsNumber).toBeUndefined();
    expect(dataLoaderBB.pcksQueryInput.childrenNumber).toBeUndefined();
  });

  describe('hasCachedBasketDetails - Redis cache logic', () => {
    it('should set hasCachedBasketDetails to true when Redis returns valid JSON', async () => {
      // Arrange
      mockRedisGetItemValue = mockBasketDetailsFromRedis;
      mockIsJsonValidValue = true;
      actualMockGetItem.mockResolvedValue(mockRedisGetItemValue);

      // Act
      const result = await createPaymentBbDataLoaderFn({
        ...mockedData,
        queryClient,
      });

      // Assert
      expect(result).toBeDefined();
      expect(result.dehydratedState).toBeDefined();
      expect(actualMockGetItem).toHaveBeenCalledWith(
        'Hotel-Reservation-Entity-Service::ReservationCache::12'
      );
    });

    it('should set hasCachedBasketDetails to false when Redis returns null', async () => {
      // Arrange
      mockRedisGetItemValue = null;
      mockIsJsonValidValue = false;
      actualMockGetItem.mockResolvedValue(null);

      // Act
      const result = await createPaymentBbDataLoaderFn({
        ...mockedData,
        queryClient,
      });

      // Assert
      expect(result).toBeDefined();
      expect(actualMockGetItem).toHaveBeenCalled();
      expect(result.dehydratedState).toBeDefined();
    });

    it('should set hasCachedBasketDetails to false when Redis returns invalid JSON', async () => {
      // Arrange
      mockRedisGetItemValue = 'invalid-json-string';
      mockIsJsonValidValue = false;
      actualMockGetItem.mockResolvedValue(mockRedisGetItemValue);

      // Act
      const result = await createPaymentBbDataLoaderFn({
        ...mockedData,
        queryClient,
      });

      // Assert
      expect(result).toBeDefined();
      expect(actualMockGetItem).toHaveBeenCalled();
    });

    it('should handle Redis getInstance error gracefully and continue execution', async () => {
      // Arrange
      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const utilsServer = require('@whitbread-eos/utils/server');
      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const { logger } = require('@whitbread-eos/utils');

      const redisError = new Error('Redis connection error');
      utilsServer.RedisStorageServer.getInstance.mockImplementationOnce(() => {
        throw redisError;
      });

      // Act
      const result = await createPaymentBbDataLoaderFn({
        ...mockedData,
        queryClient,
      });

      // Assert
      expect(result).toBeDefined();
      expect(utilsServer.RedisStorageServer.getInstance).toHaveBeenCalled();
      expect(logger.error).toHaveBeenCalledWith({ error: redisError }, 'PAYMENT_PAGE_REDIS_ERROR');
    });

    it('should handle Redis getItem error gracefully', async () => {
      // Arrange
      actualMockGetItem.mockRejectedValueOnce(new Error('Redis getItem failed'));
      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const { logger } = require('@whitbread-eos/utils');

      // Act
      const result = await createPaymentBbDataLoaderFn({
        ...mockedData,
        queryClient,
      });

      // Assert
      expect(result).toBeDefined();
      expect(actualMockGetItem).toHaveBeenCalled();
      expect(logger.error).toHaveBeenCalledWith(
        { error: expect.any(Error) },
        'PAYMENT_PAGE_REDIS_ERROR'
      );
    });
  });
});
