import '@testing-library/jest-dom';
import { Area, BASKET_STATUS, PAYMENT_ANALYTICS_KEY, SITE_BB } from '@whitbread-eos/api';
import {
  useLocalStorage,
  analytics,
  useQueryRequest,
  deleteCookie,
  updateConfirmationPageAnalytics,
} from '@whitbread-eos/utils';
import { NextRouter } from 'next/router';

import ConfirmationPageBb from './page.bb';
import { mockedGetStaticContent } from './utils/mockResponse';
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
  Icon: () => <div />,
  Printer: () => <div />,
}));

jest.mock('@whitbread-eos/molecules', () => ({
  DataSecuritySection: () => <div />,
  AnnouncementNotification: () => <div />,
  SEO: () => <div />,
  HotelOpeningInformation: () => <div />,
}));

const mockBasketDetailsStateWithTags = {
  rateTags: ['PROMO123'],
  selectedRate: {
    promoKind: 'Unique',
    promotionCode: 'PROMO123',
  },
};

const mockBasketDetailsStateNoTags = {
  rateName: 'Corporate Saver',
  rateTags: [],
};

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

const mockHotelDetails = {
  data: {
    hotelInformation: {
      address: {
        addressLine1: '19 Angel Street',
        addressLine2: 'Sheffield',
        addressLine3: 'South Yorkshire',
        postalCode: 'S3 8LN',
        country: 'United Kingdom (the)',
      },
      name: 'Sheffield City Centre (Angel Street)',
      brand: 'PI',
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
  },
  isLoading: false,
  isError: false,
  error: { message: '' },
};

function mockUseQueryRequest(queryKey) {
  const key = queryKey[0];

  if (key === 'GetBookingConfirmation') {
    return {
      data: bkngMockData,
      isLoading: false,
      refetch: () => null,
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
    return mockHotelDetails;
  }
  if (key === 'GetStaticContent') {
    return Promise.resolve({
      ...mockedGetStaticContent,
    });
  }
  if (key === 'GetPromotionPanel') {
    return {};
  }
}

const mockPollBasketStatus = {
  pollingInProgress: false,
  basketStatus: 'COMPLETED',
  dynamicSpinnerLabel: 'Loading..',
  retryPayment: false,
};
const mockGetCookie = jest.fn().mockReturnValue(null);

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockCustomLocale(),
  invalidateQueries: mockUseQueryRequest,
  useQuery: () => mockUseQueryRequest,
  useQueryRequest: jest.fn(mockUseQueryRequest),
  usePackages: () => ({
    ...getPackagesData?.data?.packages,
    ...getPackagesData,
  }),
  graphQLRequest: jest.fn(),
  usePollBasketStatus: () => mockPollBasketStatus,
  useLocalStorage: jest.fn(() => [mockBasketDetailsStateWithTags]),
  analytics: {
    update: jest.fn(),
    track: jest.fn(),
  },
  logger: {
    info: jest.fn(),
  },
  updateConfirmationPageAnalytics: jest.fn(),
  getCookie: () => mockGetCookie(),
  deleteCookie: jest.fn(),
}));

const mockProps = {
  user: {},
  basketReference: 'basketReference',

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
  staticContentQueryInput: {
    language: '',
    country: '',
    site: SITE_BB,
    businessBooker: false,
  },
  promotionQueryInput: {
    language: '',
    country: '',
    hotelId: '',
    rateCode: '',
  },
};

const mockCustomLocale = jest.fn();

describe('Confirmation page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockPollBasketStatus.basketStatus = BASKET_STATUS.COMPLETED;
    mockPollBasketStatus.pollingInProgress = false;
    mockPollBasketStatus.retryPayment = false;
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
    const { getByText } = render(<ConfirmationPageBb {...mockProps} router={router} />);
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
    const { getByText } = render(<ConfirmationPageBb {...mockProps} router={router} />);
    expect(getByText('booking.summary.continuetoHomepage')).toBeInTheDocument();
    expect(getByText('Thanks For Booking')).toBeInTheDocument();
    expect(getByText('Confirmation details')).toBeInTheDocument();
  });
  it('should render Confirmation page with language de and hotel brand other than pi', async () => {
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
    mockCustomLocale.mockReturnValue({
      language: 'de',
      country: 'de',
    });
    const { getByText } = render(<ConfirmationPageBb {...mockProps} router={router} />);
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
    mockPollBasketStatus.pollingInProgress = true;
    const { getByTestId } = render(<ConfirmationPageBb {...mockProps} router={router} />);
    getByTestId('Loading');
  });
  it('should render FailConfirmation component when basket status is FAILED', async () => {
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

    mockPollBasketStatus.basketStatus = BASKET_STATUS.SECURE_FAILED;
    mockPollBasketStatus.pollingInProgress = false;
    const { getByText } = render(<ConfirmationPageBb {...mockProps} router={router} />);
    expect(getByText('booking.summary.continuetoHomepage')).toBeInTheDocument();
  });

  it('should retry Payment without the Guest Details source marker', () => {
    const router = {
      query: {},
      push: jest.fn(),
    } as unknown as NextRouter;
    mockPollBasketStatus.basketStatus = BASKET_STATUS.FAILED;
    mockPollBasketStatus.retryPayment = true;

    render(<ConfirmationPageBb {...mockProps} router={router} />);

    expect(router.push).toHaveBeenCalledWith(
      '/gb/en/business-booker/booking-business/payment?reservationId=basketReference'
    );
  });

  it('should preserve secure booking while retrying Payment without the Guest Details marker', () => {
    const router = {
      query: { 'secure-booking': 'true' },
      push: jest.fn(),
    } as unknown as NextRouter;
    mockPollBasketStatus.basketStatus = BASKET_STATUS.SECURE_FAILED;
    mockPollBasketStatus.retryPayment = true;

    render(<ConfirmationPageBb {...mockProps} router={router} />);

    expect(router.push).toHaveBeenCalledWith(
      '/gb/en/business-booker/booking-business/payment?reservationId=basketReference&secure-booking=true'
    );
  });

  it('should trigger promo analytics update when basket is COMPLETED and rateTags exist', () => {
    const router = { query: {}, push: jest.fn() } as unknown as NextRouter;

    mockPollBasketStatus.basketStatus = BASKET_STATUS.COMPLETED;
    mockPollBasketStatus.pollingInProgress = false;

    render(<ConfirmationPageBb {...mockProps} router={router} />);

    expect(analytics.track).toHaveBeenCalledWith('promotionBooking');
    expect(analytics.update).toHaveBeenCalledWith(
      expect.objectContaining({
        promo: {
          promoName: 'Unique',
          promoCode: 'PROMO123',
        },
        promoBookingComplete: true,
      })
    );
  });

  it('should set promoBookingComplete to false when no rateTags exist', () => {
    const router = { query: {}, push: jest.fn() } as unknown as NextRouter;

    useLocalStorage.mockReturnValueOnce([mockBasketDetailsStateNoTags]);
    mockPollBasketStatus.basketStatus = BASKET_STATUS.COMPLETED;
    mockPollBasketStatus.pollingInProgress = false;

    render(<ConfirmationPageBb {...mockProps} router={router} />);

    expect(analytics.update).toHaveBeenCalledWith(
      expect.objectContaining({
        promoBookingComplete: false,
      })
    );
  });

  // new test

  it('should format hotel address using PID address format', () => {
    const router = { query: {} } as unknown as NextRouter;

    const mockHotelDetailsPID = {
      data: {
        hotelInformation: {
          address: {
            addressLine1: 'Line1',
            addressLine2: 'Line2',
            addressLine3: 'Line3',
            postalCode: 'ZIP123',
          },
          name: 'PID Hotel',
          brand: 'PID',
          announcement: null,
          importantInfo: null,
        },
      },
      isLoading: false,
      isError: false,
    };

    (useQueryRequest as jest.Mock).mockImplementation((queryKey: any) => {
      if (queryKey[0] === 'GetHotelInformation') {
        return mockHotelDetailsPID;
      }
      return mockUseQueryRequest(queryKey);
    });

    render(<ConfirmationPageBb {...mockProps} router={router} />);

    expect(analytics.update).toHaveBeenCalled();
  });

  it('should treat PI brand correctly in brand condition', () => {
    const mockHotelDetailsPI = {
      data: {
        hotelInformation: {
          brand: 'PI',
          address: {},
          name: 'PI Hotel',
        },
      },
      isLoading: false,
      isError: false,
    };

    (useQueryRequest as jest.Mock).mockImplementation((queryKey: any) => {
      if (queryKey[0] === 'GetHotelInformation') {
        return mockHotelDetailsPI;
      }
      return mockUseQueryRequest(queryKey);
    });

    render(<ConfirmationPageBb {...mockProps} router={{ query: {} } as any} />);

    expect(analytics.update).toHaveBeenCalled();
  });

  it('should not treat non PI/PID brand as special brand', () => {
    const mockHotelDetailsOther = {
      data: {
        hotelInformation: {
          brand: 'HUB',
          address: {},
          name: 'Hub Hotel',
        },
      },
      isLoading: false,
      isError: false,
    };

    (useQueryRequest as jest.Mock).mockImplementation((queryKey: any) => {
      if (queryKey[0] === 'GetHotelInformation') {
        return mockHotelDetailsOther;
      }
      return mockUseQueryRequest(queryKey);
    });

    render(<ConfirmationPageBb {...mockProps} router={{ query: {} } as any} />);

    expect(analytics.update).toHaveBeenCalled();
  });

  it('should call deleteCookie when promotionBoxCodeCookie matches promotionCode', () => {
    const router = {
      query: {},
      push: jest.fn(),
    } as unknown as NextRouter;

    mockGetCookie.mockReturnValue('PROMO123');

    (useLocalStorage as jest.Mock).mockReturnValue([
      {
        selectedRate: {
          promoKind: 'Unique',
          promotionCode: 'PROMO123',
        },
        rateTags: ['PROMO123'],
      },
    ]);

    render(<ConfirmationPageBb {...mockProps} router={router} />);

    expect(deleteCookie).toHaveBeenCalledWith('appliedPromoBoxCode');
  });

  it('should NOT call deleteCookie when promotionBoxCodeCookie does not match promotionCode', () => {
    const router = {
      query: {},
      push: jest.fn(),
    } as unknown as NextRouter;

    mockGetCookie.mockReturnValue('DIFFERENT_PROMO');

    (useLocalStorage as jest.Mock).mockReturnValue([
      {
        selectedRate: {
          promoKind: 'Unique',
          promotionCode: 'PROMO123',
        },
        rateTags: ['PROMO123'],
      },
    ]);

    render(<ConfirmationPageBb {...mockProps} router={router} />);

    expect(deleteCookie).not.toHaveBeenCalled();
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

    render(<ConfirmationPageBb {...mockProps} router={router} />);

    expect(updateConfirmationPageAnalytics).toHaveBeenCalledWith(
      bkngMockData.bookingConfirmation,
      getPackagesData.data.packages.packages,
      paymentAnalyticsData,
      Area.BB
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

    render(<ConfirmationPageBb {...mockProps} router={router} />);

    expect(updateConfirmationPageAnalytics).not.toHaveBeenCalled();

    sessionStorage.removeItem(PAYMENT_ANALYTICS_KEY);
  });

  it('should NOT call updateConfirmationPageAnalytics when paymentAnalytics is not present in sessionStorage', () => {
    const router = {
      query: {},
      push: jest.fn(),
    } as unknown as NextRouter;

    sessionStorage.removeItem(PAYMENT_ANALYTICS_KEY);

    render(<ConfirmationPageBb {...mockProps} router={router} />);

    expect(updateConfirmationPageAnalytics).not.toHaveBeenCalled();
  });
});
