import '@testing-library/jest-dom';
import { cleanup, screen } from '@testing-library/react';
import {
  QueryHotelInformationArgs,
  PackagesCriteria,
  ROOM_TYPE,
  BASKET_STATUS,
  paymentOptions as PaymentType,
} from '@whitbread-eos/api';
import {
  analytics,
  useFeatureToggle,
  applyDefaultPaymentRestrictions,
  isSecureBookingPage,
} from '@whitbread-eos/utils';

import {
  PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
  PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM,
} from '../../utils/pi-all-pages-constants';
import { fireEvent, render, userEvent, waitFor } from '../../utils/test-utils';
import PaymentPagePi from './page.pi';

const mockPCKSQueryInput: PackagesCriteria = {
  country: 'UK',
  language: 'en',
  hotelId: '123',
  adultsNumber: 1,
  childrenNumber: 1,
  startDate: '',
  endDate: '',
  nightsNumber: 1,
  bookingFlowId: '123',
  basketReferenceId: '123',
};

const mockHiQueryInput: QueryHotelInformationArgs = {
  country: 'UK',
  language: 'en',
  hotelId: '123',
};

const mockGetPackagesData = {
  data: {
    packages: {
      packages: {
        roomSelection: [
          {
            packagesSelection: [
              {
                id: 'BFADCT',
                noOfSelections: 1,
              },
              {
                id: 'BFADCT2',
                noOfSelections: 2,
              },
            ],
          },
        ],
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
  isError: false,
  isLoading: false,
  error: {
    message: 'error packages',
  },
};

const mockDonationPackagesData = {
  data: {
    donations: {
      donationPackages: [
        { code: 'BFADCT', unitPrice: '' },
        { code: '', unitPrice: 0 },
      ],
    },
  },
  refetch: () => null,
  isLoading: false,
};

const mockHotelInformation = {
  isLoading: false,
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
};

const getBookingInformationData = {
  isLoading: false,
  isError: false,
  error: {
    message: 'error booking information',
  },
  data: {
    bookingInformation: {
      hotelId: 'MANOLD',
      totalCost: 999,
      currencyCode: 'GBP',
      bookingFlowId: 'booking-a1',
      infoMessages: [
        '<p>Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival</p>\n',
      ],
      reservationByIdList: [
        {
          roomStay: {
            adultsNumber: 1,
            childrenNumber: 0,
            arrivalDate: '2023-03-23',
            departureDate: '2023-03-24',
            ratePlanCode: 'FLEXRATE',
            rateExtraInfo: {
              rateName: 'Flex',
            },
            roomExtraInfo: {
              roomType: ROOM_TYPE.PREMIER_PLUS,
              roomName: 'Premier Plus Room',
            },
            accessibleRoom: {
              isAccessible: false,
              phoneNumber: '0333 321 1315',
            },
          },
          reservationGuestList: [
            {
              givenName: 'Orange',
              surName: 'Test',
              nameTitle: 'Mr',
              email: 'orange_test@mailinator.com',
            },
          ],
          billing: {
            address: {
              addressLine1: '',
              addressLine2: '',
              addressLine3: '',
              addressLine4: '',
              country: '',
              postalCode: '',
              companyName: '',
              cityName: '',
              addressType: '',
            },
          },
        },
        {
          roomStay: {
            adultsNumber: 1,
            childrenNumber: 0,
            arrivalDate: '2023-03-23',
            departureDate: '2023-03-24',
            ratePlanCode: 'FLEXRATE',
            rateExtraInfo: {
              rateName: 'Flex',
            },
            roomExtraInfo: {
              roomType: ROOM_TYPE.PREMIER_PLUS,
              roomName: 'Premier Plus Room',
            },
            accessibleRoom: {
              isAccessible: false,
              phoneNumber: '0333 321 1315',
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
  },
};

const getPaymentStatus = {
  isLoading: false,
  isError: false,
  error: {
    message: 'error booking information',
  },
  data: { basket: { status: 'OPEN' } },
};

const getPaymentInfoMessages = {
  isLoading: false,
  data: {
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
  },
};

const getPaymentMethod = {
  isLoading: false,
  data: {
    paymentMethods: [
      {
        name: 'PIBA',
        type: 'SAVED_CARD',
        order: 1,
        card: {
          token: '5667855671183870034',
          expiryMonth: '12',
          expiryYear: '23',
          type: 'MD',
          logoSrc: '',
          cardHolderName: 'Monica W',
          cardType: 'LEISURE_STORED_CARD',
          cnpRequired: false,
        },
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
          {
            type: 'PAY_ON_ARRIVAL',
            order: 2,
            enabled: false,
          },
        ],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
      {
        name: 'CARD',
        type: 'SAVED_CARD',
        order: 2,
        card: {
          token: '5667855671183870034',
          expiryMonth: '12',
          expiryYear: '23',
          type: 'MD',
          logoSrc: '',
          cardHolderName: 'Monica W',
          cardType: 'LEISURE_STORED_CARD',
          cnpRequired: false,
        },
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
          {
            type: 'PAY_ON_ARRIVAL',
            order: 2,
            enabled: false,
          },
        ],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: ['EXPIRED'],
      },
      {
        name: 'New Credit / Debit card',
        type: 'NEW_CARD',
        order: 3,
        acceptedCardTypes: [
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
        ],
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
          {
            type: 'PAY_ON_ARRIVAL',
            order: 2,
            enabled: false,
          },
        ],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
      {
        name: 'New Business Account card',
        type: 'NEW_PIBA',
        order: 4,
        acceptedCardTypes: [
          {
            name: 'Business Account',
            type: 'PI',
            logoSrc: '',
          },
        ],
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
          {
            type: 'PAY_ON_ARRIVAL',
            order: 2,
            enabled: false,
          },
        ],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
    ],
  },
};

function mockUseQueryRequest(queryKey) {
  const key = queryKey[0];

  if (typeof key === 'string') {
    switch (key) {
      case 'GetBookingInformation':
        return getBookingInformationData;
      case 'GetPaymentStatus':
        return getPaymentStatus;
      case 'GetHotelInformation':
        return {
          data: { hotelInformation: mockHotelInformation },
        };
      case 'getPaymentMethods':
        return getPaymentMethod;
      case 'GetDonations':
        return mockDonationPackagesData;
      case 'GetPaymentInfoMessages':
        return getPaymentInfoMessages;
      default:
        return {};
    }
  }
}
const mockCustomLocale = jest.fn();
const mockUseSessionStorage = jest.fn((key) => {
  if (key === 'paymentStatus') {
    return ['failed', jest.fn()];
  } else if (key === 'paymentFailureDescription') {
    return ['PAYMENT_CONTACT_BANK_EXCEPTION_VALUE', jest.fn()];
  }
  return ['', jest.fn()];
});
const getImportantMessagesData = { data: [''] };
const mockedMutation = {
  mutation: {
    mutate: jest.fn(),
  },
  data: { initiatePayment: { status: '' } },
  isError: false,
  error: {},
  isSuccess: true,
};

const mockPaymentComplete = {
  isPaymentComplete: false,
  cardType: '',
};
const mockAddBasketIdToCookie = jest.fn();

const mockedPaypalMutation = {
  mutation: {
    mutate: jest.fn(),
  },
  isSuccess: false,
  isLoading: false,
  isError: false,
  error: {},
  data: {},
};

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),

  useTranslation: () => ({
    t: (key: string) => {
      if (key === 'errors.payment.PAYMENT_CONTACT_BANK_EXCEPTION_VALUE') {
        return 'Contact bank';
      } else {
        return key;
      }
    },
  }),
}));

jest.mock('@whitbread-eos/utils', () => {
  const actual = jest.requireActual('@whitbread-eos/utils');
  return {
    ...actual,
    useMutationRequest: (mutation: { definitions: { name: { value: string } }[] }) => {
      if (mutation?.definitions?.[0]?.name?.value === 'InitiatePaypalPayment') {
        return mockedPaypalMutation;
      }
      return mockedMutation;
    },
    ...jest.requireActual('@whitbread-eos/utils'),
    useCustomLocale: () => mockCustomLocale(),
    invalidateQueries: mockUseQueryRequest,
    useQueryRequest: mockUseQueryRequest,
    useIPageSubmission: () => mockPaymentComplete,
    setAnalyticsUser: jest.fn(),
    graphQLRequest: jest.fn(),
    analytics: {
      update: jest.fn(),
      track: jest.fn(),
      remove: jest.fn(),
    },
    usePackages: () => ({
      ...mockGetPackagesData?.data?.packages,
      ...mockGetPackagesData,
    }),
    getImportantMessages: () => getImportantMessagesData.data,
    updateAncillariesAnalytics: () => jest.fn(),
    useSessionStorage: (key: string) => mockUseSessionStorage(key),
    useFeatureToggle: jest.fn(() => ({})),
    applyDefaultPaymentRestrictions: jest.fn(),
    isSecureBookingPage: jest.fn().mockReturnValue(false),
    addBasketIdToCookie: (basketId: string) => mockAddBasketIdToCookie(basketId),
  };
});
jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  setAnalyticsUser: jest.fn(),
}));

const mockUseRouter = jest.fn().mockReturnValue({
  locale: 'en',
  query: {
    reservationId: '12',
  },
  push: jest.fn(() => Promise.resolve({})),
  back: jest.fn(() => Promise.resolve({})),
});
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockProps = {
  basketReference: '12',
  biQueryInput: {
    basketReference: '12',
    country: 'GB',
    language: 'en',
  },
  hiQueryInput: mockHiQueryInput,
  pcksQueryInput: mockPCKSQueryInput,
};
const mockRouter = {
  query: {} as Record<string, string>,
  push: jest.fn(() => Promise.resolve({})),
  back: jest.fn(() => Promise.resolve({})),
};

global.scrollTo = jest.fn();

describe('PI Payment page', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });

    // Reset shared mutable state
    mockedMutation.data = { initiatePayment: { status: '' } };
    mockedMutation.isError = false;
    mockedMutation.error = {};
    mockedMutation.isSuccess = true;

    mockedPaypalMutation.isSuccess = false;
    mockedPaypalMutation.isLoading = false;
    mockedPaypalMutation.isError = false;
    mockedPaypalMutation.error = {};
    mockedPaypalMutation.data = {};

    mockPaymentComplete.isPaymentComplete = false;
    mockPaymentComplete.cardType = '';

    getPaymentStatus.data = { basket: { status: 'OPEN' } };
    mockRouter.query = {};

    (isSecureBookingPage as jest.Mock).mockReturnValue(false);
  });
  afterEach(() => {
    cleanup();
    jest.clearAllMocks();
  });

  it('should render Payment Page PI and have continue button and click it', async () => {
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const { getAllByTestId, getByTestId, container } = render(<PaymentPagePi {...mockProps} />);

    const optionPN = getByTestId('radio-box-wrapper_PAY_NOW');
    const optionPOA = getByTestId('radio-box-wrapper_PAY_ON_ARRIVAL');

    expect(optionPN).toBeInTheDocument();
    expect(optionPOA).toBeInTheDocument();

    userEvent.click(optionPN);

    const radioOptionPN = container.querySelector(
      'div[data-testid="radio-box-wrapper_PAY_NOW"] input'
    );
    const radioOptionPOA = container.querySelector(
      'div[data-testid="radio-box-wrapper_PAY_ON_ARRIVAL"] input'
    );
    expect(radioOptionPN).toBeChecked();
    expect(radioOptionPOA).not.toBeChecked();

    const optionCard = getByTestId('payment-type-method_option-CARD');

    expect(optionCard).toBeInTheDocument();

    const radioOptionCard = container.querySelector(
      'div[data-testid="radio-box-wrapper_payment-type-radio-0"] input'
    );
    expect(radioOptionCard).toBeChecked();

    const radioOptionDonation = container.querySelector(
      'div[data-testid="radio-box-wrapper_DonationRadio-0"] input'
    );
    userEvent.click(radioOptionDonation);

    await waitFor(() => {
      expect(radioOptionDonation).toBeChecked();
    });

    const buttons = getAllByTestId('submitButton');
    await waitFor(() => {
      expect(applyDefaultPaymentRestrictions).toHaveBeenCalledTimes(1);
      fireEvent.click(buttons[0]);
    });
  });

  it('should render PI Payment page', async () => {
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const { getByTestId } = render(<PaymentPagePi {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('Donation-Container')).toBeInTheDocument();
      expect(getByTestId('paymentPageSection')).toBeInTheDocument();
      expect(getByTestId('paymentPageSection_content')).toBeInTheDocument();
    });
  });
  it('should render PI Payment page with no packages', async () => {
    mockGetPackagesData.data.packages.packages.roomSelection = [];
    const { getByTestId } = render(<PaymentPagePi {...mockProps} />);
    expect(getByTestId('paymentPageSection')).toBeInTheDocument();
    expect(getByTestId('paymentPageSection_content')).toBeInTheDocument();
  });
  it('should render page with no payment methods', async () => {
    getPaymentMethod.data.paymentMethods = [];

    const { getByTestId } = render(<PaymentPagePi {...mockProps} />);

    expect(getByTestId('paymentPageSection')).toBeInTheDocument();
  });

  it('should render page with initial basket status PAY PENDING', async () => {
    getPaymentStatus.data.basket.status = BASKET_STATUS.PAY_PENDING;

    const { getByTestId } = render(<PaymentPagePi {...mockProps} />);

    expect(getByTestId('paymentPageSection')).toBeInTheDocument();
  });

  it('should render page with methods', async () => {
    getPaymentMethod.data.paymentMethods = [
      {
        name: 'PIBA',
        type: 'SAVED_CARD',
        order: 1,
        card: {
          token: '5667855671183870034',
          expiryMonth: '12',
          expiryYear: '23',
          type: 'MD',
          logoSrc: '',
          cardHolderName: 'Monica W',
          cardType: 'LEISURE_STORED_CARD',
          cnpRequired: false,
        },
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
          {
            type: 'PAY_ON_ARRIVAL',
            order: 2,
            enabled: false,
          },
        ],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
      {
        name: 'CARD',
        type: 'SAVED_CARD',
        order: 2,
        card: {
          token: '5667855671183870034',
          expiryMonth: '12',
          expiryYear: '23',
          type: 'MD',
          logoSrc: '',
          cardHolderName: 'Monica W',
          cardType: 'LEISURE_STORED_CARD',
          cnpRequired: false,
        },
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
          {
            type: 'PAY_ON_ARRIVAL',
            order: 2,
            enabled: false,
          },
        ],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: ['EXPIRED'],
      },
      {
        name: 'New Credit / Debit card',
        type: 'NEW_CARD',
        order: 3,
        acceptedCardTypes: [
          {
            name: 'MasterCard',
            type: 'MC',
            logoSrc: '',
          },
          {
            name: 'Visa',
            type: 'VS',
            logoSrc: '',
          },
        ],
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
          {
            type: 'PAY_ON_ARRIVAL',
            order: 2,
            enabled: false,
          },
        ],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
      {
        name: 'New Business Account card',
        type: 'NEW_PIBA',
        order: 4,
        acceptedCardTypes: [
          {
            name: 'Business Account',
            type: 'PI',
            logoSrc: '',
          },
        ],
        paymentOptions: [
          {
            type: 'PAY_NOW',
            order: 1,
            enabled: true,
          },
          {
            type: 'PAY_ON_ARRIVAL',
            order: 2,
            enabled: false,
          },
        ],
        enabled: true,
        cnpPreSelected: false,
        cnpOptionAvailable: false,
        reasons: [],
      },
    ];

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const { getByTestId } = render(<PaymentPagePi {...mockProps} />);

    expect(getByTestId('paymentPageSection')).toBeInTheDocument();
  });
  it('should render page with hotelRelated messages ', async () => {
    getPaymentInfoMessages.data = undefined;
    getImportantMessagesData.data = [];

    const { getByTestId } = render(<PaymentPagePi {...mockProps} />);
    expect(getByTestId('paymentPageSection')).toBeInTheDocument();
  });
  it('should render page with BookingInfoMesseges undefined ', async () => {
    getBookingInformationData.data.bookingInformation.infoMessages = undefined;
    getImportantMessagesData.data = [];

    render(<PaymentPagePi {...mockProps} />);
  });
  it('should render page with PaymentType with mutation successful with PAYMENT_REQUIRED ', async () => {
    mockedMutation.error = '';
    mockedMutation.isSuccess = true;
    mockedMutation.isError = false;
    mockedMutation.data.initiatePayment.status = 'PAYMENT_REQUIRED';

    render(<PaymentPagePi {...mockProps} />);
  });
  it('should render page with PaymentType with mutation successful with NOT_REQUIRED ', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_pib_swap_payment_options: true,
    });
    mockedMutation.error = '';
    mockedMutation.isSuccess = true;
    mockedMutation.isError = false;
    mockedMutation.data.initiatePayment.status = 'NOT_REQUIRED';

    render(<PaymentPagePi {...mockProps} />);
  });

  it('should preserve ancillaries history when back to details is clicked after opening payment from GDP', async () => {
    mockedMutation.error = {
      response: { errors: [{ errorInfo: { globalErrTextTemplate: 'error' } }] },
    };
    mockedMutation.isSuccess = false;
    mockedMutation.isError = true;
    mockRouter.query = {
      [PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM]: PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
    };

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const { getByTestId } = render(<PaymentPagePi {...mockProps} />);
    window.HTMLElement.prototype.scrollIntoView = jest.fn();

    const backText = getByTestId('backToPageContainer');
    userEvent.click(backText);
    await waitFor(() => {
      expect(mockRouter.back).toHaveBeenCalled();
      expect(mockRouter.push).not.toHaveBeenCalled();
    });
  });

  it('should call router back when back button is clicked on secure booking page', async () => {
    (isSecureBookingPage as jest.Mock).mockReturnValue(true);
    const { getByTestId } = render(<PaymentPagePi {...mockProps} />);
    const backText = getByTestId('backToPageContainer');
    userEvent.click(backText);

    await waitFor(() => {
      expect(mockRouter.back).toHaveBeenCalled();
    });
  });

  it('should navigate explicitly to GDP when Payment was not opened from GDP', async () => {
    mockedMutation.error = {
      response: { errors: [{ message: '{"globalErrTextTemplate":"error"}' }] },
    };
    mockedMutation.isSuccess = false;
    mockedMutation.isError = true;

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const { getByTestId } = render(<PaymentPagePi {...mockProps} />);
    window.HTMLElement.prototype.scrollIntoView = jest.fn();

    const backText = getByTestId('backToPageContainer');
    userEvent.click(backText);
    await waitFor(() => {
      expect(mockRouter.push).toHaveBeenCalledWith(
        'gb/en/booking-a1/guest-details?reservationId=12'
      );
      expect(mockRouter.back).not.toHaveBeenCalled();
    });
  });

  it('should render page with isPaymentComplete true', async () => {
    mockPaymentComplete.isPaymentComplete = true;
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    render(<PaymentPagePi {...mockProps} />);
  });
  it('should render page with contact bank error when error is returned from Basket status', async () => {
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const { getAllByText } = render(
      <PaymentPagePi
        pcksQueryInput={mockPCKSQueryInput}
        hiQueryInput={mockHiQueryInput}
        basketReference="basketReference"
      />
    );
    expect(await getAllByText('Contact bank')[0]).toBeInTheDocument();
  });
  it('removes event listener on unmount', () => {
    mockPaymentComplete.isPaymentComplete = true;
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    const removeEventListenerSpy = jest.spyOn(window, 'removeEventListener');

    const { unmount } = render(<PaymentPagePi {...mockProps} />);
    const event = new Event('beforeunload');
    window.dispatchEvent(event);

    unmount();
    expect(removeEventListenerSpy).toHaveBeenCalledWith('beforeunload', expect.any(Function));
  });

  it('should update analyitcs with payment outage data', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      kill_switch_pi_bb_ccui_disable_payments: true,
    });

    render(
      <PaymentPagePi
        pcksQueryInput={mockPCKSQueryInput}
        hiQueryInput={mockHiQueryInput}
        basketReference="basketReference"
      />
    );
    expect(analytics.update).toBeCalledWith({
      paymentCardSelected: PaymentType.RESERVE_WITHOUT_CARD,
      paymentOutage: true,
      cardType: PaymentType.RESERVE_WITHOUT_CARD,
    });
  });

  it('should hide cnp option during payment outage', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      kill_switch_pi_bb_ccui_disable_payments: true,
    });

    render(
      <PaymentPagePi
        pcksQueryInput={mockPCKSQueryInput}
        hiQueryInput={mockHiQueryInput}
        basketReference="basketReference"
      />
    );
    expect(screen.queryByTestId('PaymentAuth-Container')).not.toBeInTheDocument();
    expect(screen.queryByTestId('BusinessAllowances-Container')).not.toBeInTheDocument();
  });

  it('should call addBasketIdToCookie when page loads with basketReference', async () => {
    render(<PaymentPagePi {...mockProps} />);

    await waitFor(() => {
      expect(mockAddBasketIdToCookie).toHaveBeenCalledWith('12');
    });
  });

  it('should not call addBasketIdToCookie when basketReference is null', async () => {
    const propsWithoutBasket = {
      ...mockProps,
      basketReference: null,
    };

    render(<PaymentPagePi {...propsWithoutBasket} />);

    await waitFor(() => {
      expect(mockAddBasketIdToCookie).not.toHaveBeenCalled();
    });
  });

  it('should not call addBasketIdToCookie when basketReference is undefined', async () => {
    const propsWithoutBasket = {
      ...mockProps,
      basketReference: undefined,
    };

    render(<PaymentPagePi {...propsWithoutBasket} />);

    await waitFor(() => {
      expect(mockAddBasketIdToCookie).not.toHaveBeenCalled();
    });
  });

  it('should show "Back to payment methods" link when in CARD_DETAILS step', async () => {
    jest.clearAllMocks();

    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_pib_payment_back_to_options_link: true,
    });

    // Mock useMutationRequest to return isSuccess as true and isLoading as false for both mutations
    jest
      // eslint-disable-next-line @typescript-eslint/no-require-imports
      .spyOn(require('@whitbread-eos/utils'), 'useMutationRequest')
      .mockImplementation((mutation) => {
        if (mutation?.includes('createPaypalPaymentMutation')) {
          // Return paypal mutation mock with isLoading: false
          return {
            ...mockedPaypalMutation,
            isLoading: false,
            isSuccess: false,
          };
        }
        // Return mock with isSuccess: true and isLoading: false for INITIATE_PAYMENT_MUTATION
        return {
          mutation: {
            mutate: jest.fn(),
          },
          data: {
            initiatePayment: {
              status: 'PAYMENT_REQUIRED',
              paymentRequiredDetails: {
                paymentRedirect: '<iframe src="https://example.com"></iframe>',
                sessionId: 'test-session-id',
                template: 'test-template-id',
                providerUrl: 'https://example.com',
              },
            },
          },
          isError: false,
          error: {},
          isSuccess: true,
          isLoading: false,
        };
      });

    mockPaymentComplete.isPaymentComplete = false;
    getPaymentStatus.data.basket.status = 'OPEN';

    // Explicitly set paypal mutation to have isLoading: false
    mockedPaypalMutation.isLoading = false;
    mockedPaypalMutation.isSuccess = false;

    mockHotelInformation.isLoading = false;
    getBookingInformationData.isLoading = false;
    getBookingInformationData.isError = false;
    mockGetPackagesData.isLoading = false;
    mockGetPackagesData.isError = false;
    getPaymentInfoMessages.isLoading = false;
    getPaymentMethod.isLoading = false;

    mockUseSessionStorage.mockImplementation((key) => {
      if (key === 'paymentStatus') return ['', jest.fn()];
      if (key === 'paymentFailureDescription') return ['', jest.fn()];
      if (key === 'paymentFailureCode') return ['', jest.fn()];
      return ['', jest.fn()];
    });
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    // Spy on window.scrollTo
    const scrollToSpy = jest.spyOn(window, 'scrollTo');

    render(<PaymentPagePi {...mockProps} />);

    // Wait for the CARD_DETAILS step to be rendered
    await waitFor(() => {
      expect(screen.queryByText('booking.loading')).not.toBeInTheDocument();
    });
    expect(screen.getAllByText('booking.backToPaymentMethods.text').length).toBe(2);

    const backToPaymentMethodsLinks = screen.queryAllByText('booking.backToPaymentMethods.text');
    // Simulate click
    backToPaymentMethodsLinks[0].click();
    expect(scrollToSpy).toHaveBeenCalledWith(0, 0);
    scrollToSpy.mockRestore();
  });

  it('should detect WiFi ancillaries when WIFI package is selected', async () => {
    mockGetPackagesData.data.packages.packages.roomSelection = [
      {
        packagesSelection: [
          {
            id: 'ULTIMATE_WIFI', // must match one value from WIFI_IDS
            noOfSelections: 1,
          },
        ],
      },
    ];

    render(<PaymentPagePi {...mockProps} />);

    // Replace this expectation based on actual effect of the flag
    await waitFor(() => {
      expect(applyDefaultPaymentRestrictions).toHaveBeenCalled();
    });
  });
});
// Note: bookingFlowId sessionStorage persistence is tested in DatatransPage.test.tsx —
// that logic lives in DatatransPage, not in page.pi.
