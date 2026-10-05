import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { ROOM_TYPE } from '@whitbread-eos/api';
import { axiosRequest } from '@whitbread-eos/utils';
import { IncomingMessage } from 'http';

import createGuestDetailsPiDataLoaderFn from './data.pi';

const mockGetBookingInformationData = {
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
const getPackagesData = {
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
const getHotelInformationData = {
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
const mockLoggedInUserInfo = {
  operaCompanyId: '2569616',
};
const mockToken =
  'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InAzblU5b3M0RTBubGRMVF9ROHBnbSJ9.eyJodHRwczovL2NjdWkub3BlcmEud2hpdGJyZWFkLmRpZ2l0YWwvcm9sZSI6W10sIndiX2FjY291bnRfbG9jYWxlIjoiZW4iLCJodHRwczovL3ByZW1pZXJpbm4uY29tL2NvbXBhbnlBY2NvdW50SWQiOiJDT01QXzc4NDYwNjNlLWFjZDEtNDkzMS04YzA3LTFjMzNkZGMyYTQyZiIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZW1wbG95ZWVBY2NvdW50SWQiOiJFTVBMX2I0NWZkNzU1LWFhY2UtNGU5OS05MmIxLWRmN2NkZDUwOWU1MiIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZ2xvYmFsQ29tcGFueUlkIjoxMzYxLCJodHRwczovL3ByZW1pZXJpbm4uY29tL2VtYWlsIjoidHJhdmVsaW5nLmJnbEBtYWlsaW5hdG9yLmNvbSIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vb3BlcmFDb21wYW55SWQiOiIyNTY5NjE2Iiwibmlja25hbWUiOiJ0cmF2ZWxpbmcuYmdsIiwicHJvZmlsZSI6eyJhY2Nlc3NMZXZlbCI6IlNVUEVSIiwiY29tcGFueUlkIjoiMzUwODYiLCJlbXBsb3llZUlkIjoiMSIsImlzQnVzaW5lc3MiOnRydWUsInNlc3Npb25JZCI6IjhSdHIxUktlaEFPb21rU2IifSwibmFtZSI6InRyYXZlbGluZy5iZ2xAbWFpbGluYXRvci5jb20iLCJwaWN0dXJlIjoiaHR0cHM6Ly9zLmdyYXZhdGFyLmNvbS9hdmF0YXIvYzYyNjU3YzE4NmFiNWIzMjNjOWFhZWJkYzJiZjExY2M_cz00ODAmcj1wZyZkPWh0dHBzJTNBJTJGJTJGY2RuLmF1dGgwLmNvbSUyRmF2YXRhcnMlMkZ0ci5wbmciLCJ1cGRhdGVkX2F0IjoiMjAyMy0wMy0wN1QxNDo0MTo0Ni4wMDFaIiwiaXNzIjoiaHR0cHM6Ly9hdXRoMC5zYW5kYm94LndoaXRicmVhZC5kaWdpdGFsLyIsImF1ZCI6IjhLT0NKa3o3MXBXRFlhamFNQUxhZWJKdVczQ0Nxb3ZzIiwiaWF0IjoxNjc4MjAwMTA3LCJleHAiOjE2NzgyMDE2MDcsInN1YiI6ImF1dGgwfDYzZWY0MzlkZDc0ZTZmOTZkYjAxZmEzYyIsImF0X2hhc2giOiJJalRkVEJhd2lHRmItRXVIUFNZVk93Iiwibm9uY2UiOiJaSGd3Sms2NVhkZ0g4QUZNV2pFeEpLZFNoZlRYMU5UeSJ9.E11UiJpwGnx5aj4WQvfdsjj5h2_h9MS-kiXMffYaPqk5x9QCegySS6kK0S_Z7rOKJG2QHtKH-wYyIyz-5moDA2u1iT4PLHtdwD4XoXtB-H8aKfvlpuMlCgeH9yoFrlk9NOJFcYiN8oNjL8hUymX4JOvZHOxwMbyJ-HNOcPtuWFd-HZqTrWlNXBu5XH459uDOhZGs1Ll425gbS8tP-Wa7F6liJG7046y4FVELwXZyoksKWFxmCxHrW8gEw3BCh4Ht4F_j2ORPbs0idt2VuNhp6NVuysCh0eks7CeyLLW951KCp40OW3P5P3KoK89T9ty0zpHXHlvCXKDDCmil962d7g';
const queryClient = new ReactQuery.QueryClient();
const mockDecodeIdToken = jest.fn();

const mockedData = {
  session: {
    accessToken: 'asasasGderg12312',
    user: {},
  },
  resolvedUrl: '/',
  query: {
    reservationId: '12',
    BRAND: 'PI',
  },
  language: 'en',
  country: 'GB',
  res: undefined,
  req: {
    url: undefined,
    headers: {},
    method: 'GET',
    connection: {},
    cookies: {},
  } as unknown as IncomingMessage & { cookies: Partial<{ [key: string]: string }> },
  logger: {
    info: jest.fn(),
  },
  featureToggles: {
    release_pi_bb_account_serv_2_serv: true,
  },
  passedBasketDetails: {
    hotelId: 'LONKIN',
    reservationId: '12',
    startDate: '2023-02-23',
    endDate: '2023-02-24',
    adultsNumber: 1,
    childrenNumber: 0,
    nightsNumber: 1,
    bookingFlowId: 'booking-hub',
    rateCode: 'FLEXRATE',
  },
  hasCachedBasketDetails: true,
};

const expectedResult = {
  biQueryInput: {
    basketReference: '12',
    bookingChannelCriteria: {
      channel: 'PI',
      language: 'EN',
      subchannel: 'WEB',
    },
    country: 'GB',
    language: 'en',
  },
  dehydratedState: {
    mutations: [],
    queries: [],
  },
  hiQueryInput: {
    country: 'GB',
    hotelId: 'LONKIN',
    language: 'en',
  },
  pcksQueryInput: {
    adultsNumber: 1,
    basketReferenceId: '12',
    channel: 'PI',
    bookingFlowId: 'booking-hub',
    childrenNumber: 0,
    country: 'GB',
    endDate: '2023-02-24',
    hotelId: 'LONKIN',
    language: 'en',
    nightsNumber: 1,
    startDate: '2023-02-23',
  },
};

const mockGetCookie = jest.fn();
let mockIsJsonValidValue = false;
let mockRedisGetItemValue: string | null = null;

const mockBasketDetailsFromRedis = JSON.stringify({
  bookingFlowId: 'booking-hub',
  hotelId: 'LONKIN',
  rateCode: 'FLEXRATE',
  nightsNumber: 1,
  startDate: '2023-02-23',
  endDate: '2023-02-24',
  childrenNumber: 0,
  adultsNumber: 1,
  reservationId: '12',
});

const invalidJsonObjectExpectedResult = {
  dehydratedState: {
    mutations: [],
    queries: [],
  },
  hiQueryInput: {
    country: 'GB',
    hotelId: 'LONKIN',
    language: 'en',
  },
  pcksQueryInput: {
    adultsNumber: 0,
    basketReferenceId: '',
    bookingFlowId: 'booking-hub',
    channel: 'PI',
    childrenNumber: 0,
    country: 'GB',
    endDate: '2023-02-24',
    hotelId: 'LONKIN',
    language: 'en',
    nightsNumber: 0,
    startDate: '2023-02-23',
  },
  biQueryInput: {
    basketReference: '',
    bookingChannelCriteria: {
      channel: 'PI',
      language: 'EN',
      subchannel: 'WEB',
    },
    country: 'GB',
    language: 'en',
  },
};

const mockCookies = {
  get: mockGetCookie,
  set: mockGetCookie,
};
jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => mockCookies);
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
  };
});

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  graphQLRequest: jest.fn(),
  isJsonValid: () => mockIsJsonValidValue,
  getNightsNumber: jest.fn().mockImplementation(() => 0),
  getMaxValueFromRoomStays: jest.fn().mockImplementation(() => 0),
  decodeFromBase64: () => '{}',
  encodeToBase64: () => '{}',
  getDefaultSessionTracing: () => ({}),
  decodeIdToken: () => mockDecodeIdToken(),
  axiosRequest: jest.fn().mockReturnValue(Promise.resolve({ data: {} })),
  getLoggedInUserInfo: jest.fn().mockImplementation(() => mockLoggedInUserInfo),
  getGQLClient: jest.fn(),
  logger: {
    info: jest.fn(),
    error: jest.fn(),
  },
  QueriesLogger: jest.fn().mockImplementation(() => ({
    fetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      const key = queryKey[0];
      queryFn();
      if (key === 'GetBookingInformation') {
        return Promise.resolve({
          ...mockGetBookingInformationData,
        });
      }
    }),
    prefetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      const key = queryKey[0];
      queryFn();
      switch (key) {
        case 'GetPackages':
          return Promise.resolve({
            data: getPackagesData,
          });
        case 'GetHotelInformation':
          return Promise.resolve({
            ...getHotelInformationData,
          });
        default:
          return Promise.resolve({});
      }
    }),
    logQueries: jest.fn(),
  })),
  ID_TOKEN_COOKIE: 'id_token_cookie',
  WB_SESSION_ID: 'WB-SESSION-ID',
}));

describe('createGuestDetailsPiDataLoaderFn', () => {
  let actualMockGetItem: jest.Mock;
  let actualMockSetItem: jest.Mock;

  beforeEach(() => {
    jest.clearAllMocks();
    mockRedisGetItemValue = mockBasketDetailsFromRedis;
    mockIsJsonValidValue = true;

    // Get references to the actual mocks from the jest.mock
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    const { RedisStorageServer } = require('@whitbread-eos/utils/server');
    const mockInstance = RedisStorageServer.getInstance();
    actualMockGetItem = mockInstance.getItem as jest.Mock;
    actualMockSetItem = mockInstance.setItem as jest.Mock;

    actualMockGetItem.mockResolvedValue(mockRedisGetItemValue);
    actualMockSetItem.mockResolvedValue(undefined);

    jest
      // eslint-disable-next-line @typescript-eslint/no-require-imports
      .spyOn(require('@whitbread-eos/utils'), 'decodeIdToken')
      .mockReturnValue({ email: 'test@test.com' });
    mockGetCookie.mockImplementation((key: string) => {
      if (key === 'id_token_cookie') return mockToken;
      if (key === 'WB-SESSION-ID') return 'mock-session-id';
      return undefined;
    });
  });

  afterEach(() => {
    mockDecodeIdToken.mockReset();
    mockGetCookie.mockReset();
  });

  it('should render data loader with expected object', async () => {
    process.env.NEXT_PUBLIC_ACCOUNT_SERVICE = 'http://hotel-account-service-opera.opera-be';
    const dataLoaderPi = await createGuestDetailsPiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);
    expect(dataLoaderPi).toStrictEqual(expectedResult);
  });

  it('should render data loader with expected object with featureToggle on', async () => {
    process.env.NEXT_PUBLIC_ACCOUNT_SERVICE = 'http://hotel-account-service-opera.opera-be';
    const dataLoaderPI = await createGuestDetailsPiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);

    expect(axiosRequest).toHaveBeenCalledWith({
      method: 'GET',
      url: `http://hotel-account-service-opera.opera-be/customers/hotels/test@test.com?business=false`,
      headers: {
        Authorization: `Bearer ${mockToken}`,
        'WB-SESSION-ID': 'mock-session-id',
      },
    });
    expect(dataLoaderPI).toStrictEqual(expectedResult);
  });

  it('should render data loader with expected object with featureToggle off', async () => {
    process.env.NEXT_PUBLIC_REST_API = 'https://restapi.dit.premierinn.digital';
    const tmpMockData = { ...mockedData };
    tmpMockData.featureToggles.release_pi_bb_account_serv_2_serv = false;

    const dataLoaderPi = await createGuestDetailsPiDataLoaderFn({
      ...tmpMockData,
      queryClient,
    } as any);

    expect(axiosRequest).toHaveBeenCalledWith({
      method: 'GET',
      url: `https://restapi.dit.premierinn.digital/customers/hotels/test@test.com?business=false`,
      headers: {
        Authorization: `Bearer ${mockToken}`,
        'WB-SESSION-ID': 'mock-session-id',
      },
    });
    expect(dataLoaderPi).toStrictEqual(expectedResult);
  });

  it('should create data loader with expected object with invalid json object hasCachedBasketDetails as false', async () => {
    mockIsJsonValidValue = true;
    mockedData.query.reservationId = '';
    mockedData.passedBasketDetails = {
      hotelId: '',
      reservationId: '',
      startDate: '',
      endDate: '',
      adultsNumber: 0,
      childrenNumber: 0,
      nightsNumber: 0,
      bookingFlowId: '',
      rateCode: '',
    };

    const dataLoaderPi = await createGuestDetailsPiDataLoaderFn({
      ...mockedData,
      hasCachedBasketDetails: false,
      queryClient,
    } as any);
    expect(dataLoaderPi).toEqual(invalidJsonObjectExpectedResult);
  });

  describe('hasCachedBasketDetails - Redis cache logic', () => {
    it('should not call Redis setItem when hasCachedBasketDetails is true', async () => {
      // Act
      const result = await createGuestDetailsPiDataLoaderFn({
        ...mockedData,
        hasCachedBasketDetails: true,
        queryClient,
      } as any);

      // Assert
      expect(result).toBeDefined();
      expect(actualMockSetItem).not.toHaveBeenCalled();
    });

    it('should fetch from GraphQL when hasCachedBasketDetails is false', async () => {
      // Act
      const result = await createGuestDetailsPiDataLoaderFn({
        ...mockedData,
        hasCachedBasketDetails: false,
        queryClient,
      } as any);

      // Assert
      expect(result).toBeDefined();
      expect(result.dehydratedState).toBeDefined();
    });

    it('should set basketDetails to Redis when hasCachedBasketDetails is false', async () => {
      // Act
      const result = await createGuestDetailsPiDataLoaderFn({
        ...mockedData,
        hasCachedBasketDetails: false,
        queryClient,
      } as any);

      // Assert
      expect(result).toBeDefined();
      expect(actualMockSetItem).toHaveBeenCalled();
    });

    it('should handle Redis setItem error gracefully', async () => {
      // Arrange
      actualMockSetItem.mockRejectedValueOnce(new Error('Redis setItem failed'));
      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const { logger } = require('@whitbread-eos/utils');

      // Act
      const result = await createGuestDetailsPiDataLoaderFn({
        ...mockedData,
        hasCachedBasketDetails: false,
        queryClient,
      } as any);

      // Assert
      expect(result).toBeDefined();
      expect(actualMockSetItem).toHaveBeenCalled();
      expect(logger.error).toHaveBeenCalledWith(
        { error: expect.any(Error) },
        'PI_GUEST_DETAILS_REDIS_SET_ERROR'
      );
    });
  });
});
