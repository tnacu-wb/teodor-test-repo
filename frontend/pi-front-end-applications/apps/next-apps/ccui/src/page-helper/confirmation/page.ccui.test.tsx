import '@testing-library/jest-dom';
import { Area, BASKET_STATUS, PAYMENT_ANALYTICS_KEY } from '@whitbread-eos/api';
import { analytics, useLocalStorage, useQueryRequest } from '@whitbread-eos/utils';
import * as utils from '@whitbread-eos/utils';
import { NextRouter } from 'next/router';

import ConfirmationPageCcui from './page.ccui';
import { render } from './utils/test-utils';

jest.mock('@whitbread-eos/atoms', () => ({
  Card: () => <div />,
  ConfirmationDetails: () => <div>Confirmation details</div>,
  FailConfirmation: () => <div />,
  HotelDirections: () => <div />,
  Info: () => <div />,
  LoadingSpinner: () => <div />,
  Notification: () => <div />,
  RoomDetails: () => <div />,
  ThanksForBooking: () => <div>Thanks For Booking</div>,
  TotalCost: () => <div />,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  AgentMemo: () => <div />,
}));

jest.mock('@whitbread-eos/molecules', () => ({
  CreateMyPiAccountContainer: () => <div />,
  HotelOpeningInformation: () => <div />,
}));

const mockBasketResponse = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    basket: {
      paymentOption: 'PAY_ON_ARRIVAL',
      status: 'COMPLETED',
      hotelId: 'LONEUS',
    },
  },
};

const bkngMockData = {
  bookingConfirmation: {
    bookingFlowId: 'booking-a1',
    hotelId: 'LONEUS',
    hotelName: 'London Euston',
    currencyCode: 'GBP',
    newTotal: 400.0,
    totalCost: 360.0,
    previousTotal: 0.0,
    bookingReference: 'test',
    reservationByIdList: [
      {
        reservationGuestList: [
          {
            givenName: 'GuestOne',
            surName: 'Test',
            nameTitle: 'Mrs',
            email: 'test@test.com',
          },
        ],
        reservationId: '1447709',
        roomStay: {
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2023-07-18',
          departureDate: '2023-07-22',
          ratePlanCode: 'FLEXRATE',
          roomExtraInfo: {
            roomName: 'Family Room',
            roomType: 'FMTRPL',
          },
          roomPrice: 45.0,
        },
        reservationPackageList: [
          {
            description: 'Charity',
            unitPrice: 0,
          },
        ],
      },
      {
        reservationGuestList: [
          {
            givenName: 'GuestOne',
            surName: 'Test',
            nameTitle: 'Mrs',
            email: 'test2@test.com',
          },
        ],
        reservationId: '1447707',
        roomStay: {
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2023-07-18',
          departureDate: '2023-07-22',
          ratePlanCode: 'FLEXRATE',
          roomExtraInfo: {
            roomName: 'Family Room',
            roomType: 'FMTRPL',
          },
          roomPrice: 45.0,
        },
      },
    ],
  },
};

const getPackagesData = {
  data: {
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
            imageSrc:
              'https://secure2.premierinn.com/content/dam/global/restaurants/Global/full-breakfast-booking.png',
            name: 'Premier Inn Breakfast',
            price: 9.99,
            order: 1,
            freeBreakfastOption: true,
            freeBreakfastCode: 'BFCHDF',
            freeBreakfastMaxPerMeal: 2,
            menu: {
              menuSrc:
                'https://secure2.premierinn.com/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
              name: 'Breakfast menu',
            },
          },
          {
            allergyInfoLabel: 'Allergy & nutrition info',
            allergyInfoSrc:
              'https://secure2.premierinn.com/content/dam/global/restaurants/Global/breakfast-allergy.pdf',
            currency: 'GBP',
            description:
              '<p>A lighter start with tasty pastries, American pancakes, fruit and cereals. Includes smoothies and juices.</p>\r\n',
            id: 'BFADCT',
            bartId: '12',
            imageSrc:
              'https://secure2.premierinn.com/content/dam/global/restaurants/Global/continental-breakfast-booking.png',
            name: 'Continental Breakfast',
            price: 7.99,
            order: 2,
            freeBreakfastOption: false,
            freeBreakfastCode: '',
            freeBreakfastMaxPerMeal: 2,
            menu: {
              menuSrc:
                'https://secure2.premierinn.com/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
              name: 'Breakfast menu',
            },
          },
          {
            allergyInfoLabel: 'Allergy & nutrition info',
            allergyInfoSrc:
              'https://secure2.premierinn.com/content/dam/global/restaurants/Global/pi-band2-allergy-nutrition.pdf',
            currency: 'GBP',
            description:
              '<p>Save up to 20% off your bill with our Meal Deal offer! Enjoy a delicious two-course dinner, a selected drink* and wake up to our famous, unlimited all-you-can-eat Premier Inn Breakfast the next day.</p>\r\n',
            id: 'MDP',
            bartId: '17',
            imageSrc:
              'https://secure2.premierinn.com/content/dam/global/restaurants/Global/meal-deal-booking.png',
            name: 'Meal Deal',
            price: 24.99,
            order: 3,
            freeBreakfastOption: true,
            freeBreakfastCode: 'BFCHDF',
            freeBreakfastMaxPerMeal: 2,
            menu: {
              menuSrc:
                'https://secure2.premierinn.com/content/dam/global/restaurants/Global/pi-dinner-menu-band2.pdf',
              name: 'Dinner menu',
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
            imageSrc:
              'https://secure2.premierinn.com/content/dam/global/restaurants/Global/child-breakfast.jpg',
            name: 'Free breakfast for kids',
            order: 0,
            menu: null,
          },
        ],
        roomSelection: [
          {
            packagesSelection: [],
          },
        ],
      },
      restaurant: {
        logoSrc:
          'https://secure2.premierinn.com/content/dam/global/restaurants/THY/Thyme-logo-165x73.jpg',
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
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'error packages',
  },
};

const mockUseBookingSpinner = {
  isTimerAchieved: false,
  dynamicSpinnerLabel: '',
};

function mockUseQueryRequest(queryKey) {
  const key = queryKey[0];

  if (key === 'GetBookingConfirmation') {
    return {
      data: bkngMockData,
      isLoading: false,
    };
  }
  if (key === 'GetBasket') {
    return {
      ...mockBasketResponse,
      refetch: () => null,
    };
  }
  if (key === 'GetPackages') {
    return getPackagesData;
  }
  if (key === 'GetHotelInformation') {
    return {
      data: { hotelInformation: {} },
      isLoading: false,
    };
  }
}
const mockGetCookie = jest.fn().mockReturnValue(null);

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockCustomLocale(),
  invalidateQueries: mockUseQueryRequest,
  useQuery: () => mockUseQueryRequest,
  useQueryRequest: jest.fn(mockUseQueryRequest),
  useBookingSpinner: () => mockUseBookingSpinner,
  usePackages: () => ({
    ...getPackagesData?.data?.packages,
    ...getPackagesData,
  }),
  usePollBasketStatus: () => ({
    pollingInProgress: false,
    basketStatus: 'COMPLETED',
    dynamicSpinnerLabel: 'Loading..',
  }),
  setAnalyticsUser: jest.fn(),
  graphQLRequest: jest.fn(),
  analytics: {
    update: jest.fn(),
    track: jest.fn(),
  },
  logger: {
    info: jest.fn(),
  },
  updateConfirmationPageAnalytics: jest.fn(),
  useLocalStorage: jest.fn(() => [
    {
      rateName: '',
      rateTags: [],
    },
    jest.fn(),
  ]),
  getCookie: () => mockGetCookie(),
  deleteCookie: jest.fn(),
}));

jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  setAnalyticsUser: jest.fn(),
}));

const mockProps = {
  user: {},
  basketReference: 'basketReference',
  setAnalyticsUser: jest.fn(),
  hiQueryInput: {
    country: 'GB',
    hotelId: 'LONKIN',
    language: 'en',
  },
  pcksQueryInput: {
    adultsNumber: 1,
    basketReferenceId: '12',
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

const mockCustomLocale = jest.fn();

describe('Confirmation page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockGetCookie.mockReturnValue(null);
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
  });

  it('should render Confirmation page', async () => {
    const router = {
      query: {
        searchLocation: '',
        ARRdd: '',
        ARRmm: '',
        ARRyyyy: '',
        NIGHTS: '',
        ROOMS: '',
      },
    } as unknown as NextRouter;
    const { getByText } = render(<ConfirmationPageCcui {...mockProps} router={router} />);
    expect(getByText('booking.summary.continuetoHomepage')).toBeInTheDocument();
    expect(getByText('Thanks For Booking')).toBeInTheDocument();
    expect(getByText('Confirmation details')).toBeInTheDocument();
  });
  it('should render Confirmation page with PAY NOW', async () => {
    const router = {
      query: {
        searchLocation: '',
        ARRdd: '',
        ARRmm: '',
        ARRyyyy: '',
        NIGHTS: '',
        ROOMS: '',
      },
    } as unknown as NextRouter;
    mockBasketResponse.data.basket.paymentOption = 'PAY_NOW';
    const { getByText } = render(<ConfirmationPageCcui {...mockProps} router={router} />);
    expect(getByText('booking.summary.continuetoHomepage')).toBeInTheDocument();
    expect(getByText('Thanks For Booking')).toBeInTheDocument();
    expect(getByText('Confirmation details')).toBeInTheDocument();
  });
  it('should render Confirmation page with isTimerAchieved true', async () => {
    const router = {
      query: {
        searchLocation: '',
        ARRdd: '',
        ARRmm: '',
        ARRyyyy: '',
        NIGHTS: '',
        ROOMS: '',
      },
    } as unknown as NextRouter;
    mockUseBookingSpinner.isTimerAchieved = true;
    const { getByText } = render(<ConfirmationPageCcui {...mockProps} router={router} />);
    expect(getByText('booking.summary.continuetoHomepage')).toBeInTheDocument();
    expect(getByText('Thanks For Booking')).toBeInTheDocument();
    expect(getByText('Confirmation details')).toBeInTheDocument();
  });
  it('should render Confirmation page with loading state', async () => {
    const router = {
      query: {
        searchLocation: '',
        ARRdd: '',
        ARRmm: '',
        ARRyyyy: '',
        NIGHTS: '',
        ROOMS: '',
      },
    } as unknown as NextRouter;
    mockUseBookingSpinner.isTimerAchieved = false;
    mockBasketResponse.data.basket.status = BASKET_STATUS.PROCESSING;
    const { getByTestId } = render(<ConfirmationPageCcui {...mockProps} router={router} />);
    getByTestId('Loading');
  });
  it('should parse paymentAnalytics from localStorage when present', async () => {
    const router = {
      query: {
        searchLocation: '',
        ARRdd: '',
        ARRmm: '',
        ARRyyyy: '',
        NIGHTS: '',
        ROOMS: '',
      },
    } as unknown as NextRouter;

    mockUseBookingSpinner.isTimerAchieved = false;
    mockBasketResponse.data.basket.status = 'COMPLETED';
    mockBasketResponse.data.basket.paymentOption = 'PAY_ON_ARRIVAL';

    (useLocalStorage as jest.Mock).mockReturnValueOnce([
      JSON.stringify({
        paymentMethod: 'VISA',
        amount: 400,
      }),
      jest.fn(),
    ]);

    const { getByText } = render(<ConfirmationPageCcui {...mockProps} router={router} />);

    expect(getByText('Thanks For Booking')).toBeInTheDocument();
  });

  it('should use PID address format when brand is PID', () => {
    const router = { query: {} } as any;

    const hiDataWithPID = {
      data: {
        hotelInformation: {
          brand: 'PID',
          name: 'Test Hotel',
          address: {
            addressLine1: 'Line1',
            addressLine2: 'Line2',
            addressLine3: 'Line3',
            postalCode: 'ZIP123',
          },
          contactDetails: {
            hotelNationalPhone: '123456789',
          },
        },
      },
      isLoading: false,
    };

    (useQueryRequest as jest.Mock).mockImplementation((queryKey: any) => {
      if (queryKey[0] === 'GetHotelInformation') {
        return hiDataWithPID;
      }
      return mockUseQueryRequest(queryKey);
    });

    render(<ConfirmationPageCcui {...mockProps} router={router} />);

    expect(analytics.update).toHaveBeenCalled();
  });

  it('should NOT call deleteCookie when selectedRate is undefined (no promotionCode)', () => {
    const router = {
      query: {},
      push: jest.fn(),
    } as unknown as NextRouter;

    mockGetCookie.mockReturnValue('SOME_UNRELATED_VALUE');

    (utils.useLocalStorage as jest.Mock).mockReturnValue([
      {
        rateName: '',
        rateTags: [],
      },
    ]);

    render(<ConfirmationPageCcui {...mockProps} router={router} />);

    expect(utils.deleteCookie).not.toHaveBeenCalled();
  });

  it('should call deleteCookie when promotionBoxCodeCookie matches promotionCode', () => {
    const router = {
      query: {},
      push: jest.fn(),
    } as unknown as NextRouter;

    mockGetCookie.mockReturnValue('PROMO123');

    (utils.useLocalStorage as jest.Mock).mockReturnValue([
      {
        selectedRate: {
          promoKind: 'Unique',
          promotionCode: 'PROMO123',
        },
        rateTags: ['PROMO123'],
      },
    ]);

    render(<ConfirmationPageCcui {...mockProps} router={router} />);

    expect(utils.deleteCookie).toHaveBeenCalledWith('appliedPromoBoxCode');
  });

  it('should NOT call deleteCookie when promotionBoxCodeCookie does not match promotionCode', () => {
    const router = {
      query: {},
      push: jest.fn(),
    } as unknown as NextRouter;

    mockGetCookie.mockReturnValue('DIFFERENT_PROMO');

    (utils.useLocalStorage as jest.Mock).mockReturnValue([
      {
        selectedRate: {
          promoKind: 'Unique',
          promotionCode: 'PROMO123',
        },
        rateTags: ['PROMO123'],
      },
    ]);

    render(<ConfirmationPageCcui {...mockProps} router={router} />);

    expect(utils.deleteCookie).not.toHaveBeenCalled();
  });

  it('should call updateConfirmationPageAnalytics when bkngData, packages exist and basketReference matches', () => {
    const router = {
      query: {},
      push: jest.fn(),
    } as unknown as NextRouter;

    const paymentAnalyticsData = {
      basketReference: 'basketReference',
      paymentMethod: 'VISA',
      amount: 400,
    };

    sessionStorage.setItem(PAYMENT_ANALYTICS_KEY, JSON.stringify(paymentAnalyticsData));

    render(<ConfirmationPageCcui {...mockProps} router={router} />);

    expect(utils.updateConfirmationPageAnalytics).toHaveBeenCalledWith(
      bkngMockData.bookingConfirmation,
      getPackagesData.data.packages.packages,
      paymentAnalyticsData,
      Area.CCUI
    );

    sessionStorage.removeItem(PAYMENT_ANALYTICS_KEY);
  });

  it('should NOT call updateConfirmationPageAnalytics when basketReference does not match', () => {
    const router = {
      query: {},
      push: jest.fn(),
    } as unknown as NextRouter;

    const paymentAnalyticsData = {
      basketReference: 'someOtherBasketReference',
      paymentMethod: 'VISA',
      amount: 400,
    };

    sessionStorage.setItem(PAYMENT_ANALYTICS_KEY, JSON.stringify(paymentAnalyticsData));

    render(<ConfirmationPageCcui {...mockProps} router={router} />);

    expect(utils.updateConfirmationPageAnalytics).not.toHaveBeenCalled();

    sessionStorage.removeItem(PAYMENT_ANALYTICS_KEY);
  });

  it('should NOT call updateConfirmationPageAnalytics when paymentAnalytics is not present in sessionStorage', () => {
    const router = {
      query: {},
      push: jest.fn(),
    } as unknown as NextRouter;

    sessionStorage.removeItem(PAYMENT_ANALYTICS_KEY);

    render(<ConfirmationPageCcui {...mockProps} router={router} />);

    expect(utils.updateConfirmationPageAnalytics).not.toHaveBeenCalled();
  });
});

describe('Promo analytics in Confirmation page', () => {
  const router = {
    query: {},
  } as any;

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should set promoCode and promoBookingComplete true when rateTags exist', () => {
    (useLocalStorage as jest.Mock).mockReturnValue([
      {
        selectedRate: {
          promoKind: 'Unique',
          promotionCode: 'PROMO123',
        },
        rateTags: ['PROMO123'],
      },
    ]);

    render(<ConfirmationPageCcui {...mockProps} router={router} />);

    const analyticsUpdate = analytics.update;
    expect(analytics.track).toHaveBeenCalledWith('promotionBooking');
    expect(analyticsUpdate).toHaveBeenCalledWith(
      expect.objectContaining({
        promo: {
          promoName: 'Unique',
          promoCode: 'PROMO123',
        },
        promoBookingComplete: true,
      })
    );
  });

  it('should set promoBookingComplete false when rateTags is empty array', () => {
    (useLocalStorage as jest.Mock).mockReturnValue([
      {
        rateTags: [],
        selectedRate: {
          promoKind: 'Unique',
          promotionCode: 'Promo123',
        },
      },
    ]);

    render(<ConfirmationPageCcui {...mockProps} router={router} />);

    const analyticsUpdate = analytics.update;
    expect(analytics.track).toHaveBeenCalledWith('promotionBooking');
    expect(analyticsUpdate).toHaveBeenCalledWith(
      expect.objectContaining({
        promo: {
          promoName: 'Unique',
          promoCode: 'Promo123',
        },
        promoBookingComplete: false,
      })
    );
  });

  it('should set promoBookingComplete false when rateTags is undefined', () => {
    (useLocalStorage as jest.Mock).mockReturnValue([
      {
        rateTags: undefined,
      },
    ]);

    render(<ConfirmationPageCcui {...mockProps} router={router} />);

    const analyticsUpdate = analytics.update;
    expect(analytics.track).toHaveBeenCalledWith('promotionBooking');
    expect(analyticsUpdate).toHaveBeenCalledWith(
      expect.objectContaining({
        promoBookingComplete: false,
      })
    );
  });
});
