import '@testing-library/jest-dom';
import {
  BASKET_STATUS,
  CCUI_INITIATE_PAYMENT_PROCESS,
  CcuiCardType,
  EckohStatus,
  FT_CCUI_GDP_BILLING_ADDRESS,
  FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE,
  INITIATE_ECKOH_IFRAME,
  ROOM_TYPE,
  UPDATE_DISCOUNT,
  UPDATE_EMAIL,
  UserRoles,
} from '@whitbread-eos/api';
import { analytics, useFeatureToggle } from '@whitbread-eos/utils';
import * as React from 'react';

import { render, userEvent, fireEvent, waitFor } from '../../utils/test-utils';
import { PaymentPageCcui } from './page.ccui';

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
  isLoading: false,
  isError: false,
  error: {
    message: 'error packages',
  },
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
      discount: 0,
      bookingFlowId: 'booking-a1',
      infoMessages: [
        '<p>Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival</p>\n',
      ],
      reservationByIdList: [
        {
          billing: {},
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
  data: { basket: { status: '' } },
};

const getPaymentsCcui = {
  data: {
    paymentCcuiMethods: [
      {
        cnpOptionAvailable: false,
        cnpPreSelected: false,
        enabled: true,
        name: 'CARD',
        order: 1,
        reasons: [],
        subType: null,
        type: 'NEW_CARD',
        paymentOptions: [
          {
            enabled: true,
            order: 1,
            type: 'PAY_NOW',
          },
          {
            enabled: true,
            order: 2,
            type: 'PAY_ON_ARRIVAL',
          },
        ],
        acceptedCardTypes: [
          {
            logoSrc: '/content/dam/global/booking/Mastercard.jpg',
            name: 'Mastercard Credit',
            type: 'MC',
          },
          {
            logoSrc: '/content/dam/global/booking/AX.jpg',
            name: 'American Express',
            type: 'AX',
          },
          {
            logoSrc: '/content/dam/global/booking/dinersclub.jpg',
            name: 'Diners Club',
            type: 'DN',
          },
          {
            logoSrc: '/content/dam/global/booking/Visa_Debit.jpg',
            name: 'Visa Debit',
            type: 'VS',
          },
          {
            logoSrc: '/content/dam/global/booking/Electron_white_v.jpg',
            name: 'Electron',
            type: 'VS',
          },
          {
            logoSrc: '/content/dam/global/booking/maestro.jpg',
            name: 'Maestro',
            type: 'MA',
          },
          {
            logoSrc: '/content/dam/global/booking/MD.jpg',
            name: 'Mastercard Debit',
            type: 'MC',
          },
          {
            logoSrc: '/content/dam/global/booking/VC.jpg',
            name: 'Visa Credit',
            type: 'VS',
          },
        ],
      },
    ],
  },
};

const mockEckohStatusData = {
  data: { eckohRecordingStatus: { status: EckohStatus.SUCCESS } },
  isLoading: false,
  isError: false,
  isSuccess: true,
  error: {
    message: 'error recording status',
  },
};

const getHotelInformationData = {
  data: {
    hotelInformation: {
      address: {
        addressLine1: 'Sir Alex Ferguson Way',
        addressLine2: 'Trafford Park',
        addressLine3: 'Manchester',
        postalCode: 'M17 1WS',
        country: 'United Kingdom (the)',
      },
      name: 'Manchester Old Trafford',
      brand: 'PI',
      announcement: {
        endDate: '21/07/2022',
        showAnnouncement: 'true',
        startDate: '08/01/2021',
        text: 'Get all the latest updates on our response to&nbsp;<a href="/gb/en/covid-19.html" target="_blank"><u>COVID-19</u></a>&nbsp;and see how we’re keeping guests safe with our&nbsp;<a href="/gb/en/why/cleanliness.html" target="_blank"><u>Premier Inn CleanProtect<sup>TM</sup></u></a>&nbsp;promise.&nbsp;<br>\r\n',
        title: '',
        type: 'info',
      },
      importantInfo: {
        title: 'Important Information',
        infoItems: [
          {
            text: 'The bathrooms in this hotel have showers only.',
            priority: '1',
            startDate: '30/09/2022',
            endDate: '01/01/2024',
          },
          {
            text: 'Another important information message with highest priority.',
            priority: '10',
            startDate: '30/09/2022',
            endDate: '01/01/2024',
          },
          {
            text: 'Parking is not going to be available during this period of time due to some works on the street.',
            priority: '3',
            startDate: '30/09/2022',
            endDate: '01/01/2024',
          },
        ],
      },
    },
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'error hotel information',
  },
};

const createMockMutationResponse = () => ({
  mutation: {
    mutate: jest.fn(),
    mutateAsync: jest.fn().mockResolvedValue({}),
  },
  isSuccess: false,
  isError: false,
  error: '',
});

function resetMutationResponse(response: {
  mutation: {
    mutate: { mockReset: () => void };
    mutateAsync: { mockReset: () => void; mockResolvedValue: () => void };
  };
  isSuccess: boolean;
  isError: boolean;
  error: string;
}) {
  response.mutation.mutate.mockReset();
  response.mutation.mutateAsync.mockReset();
  response.mutation.mutateAsync.mockResolvedValue();
  response.isSuccess = false;
  response.isError = false;
  response.error = '';
}

const mockMutationResponse = createMockMutationResponse();
const mockUpdateDiscountMutationResponse = createMockMutationResponse();
const mockUpdateEmailMutationResponse = createMockMutationResponse();
const mockInitiateIframeMutationResponse = createMockMutationResponse();
const mockRefetchBooking = jest.fn();
const mockUseMutationRequest = jest.fn((key) => {
  switch (key) {
    case CCUI_INITIATE_PAYMENT_PROCESS:
      return mockMutationResponse;
    case INITIATE_ECKOH_IFRAME:
      return mockInitiateIframeMutationResponse;
    case UPDATE_DISCOUNT:
      return mockUpdateDiscountMutationResponse;
    case UPDATE_EMAIL:
      return mockUpdateEmailMutationResponse;
    default:
      return createMockMutationResponse();
  }
});

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  Input: ({ name, onBlur, onChange, value, isDisabled, placeholderText }: any) => (
    <input
      data-testid={`input-${name}`}
      disabled={isDisabled}
      name={name}
      onBlur={onBlur}
      onChange={(event) => onChange?.(event.target.value)}
      placeholder={placeholderText}
      value={value}
    />
  ),
  Tooltip: () => <div />,
}));

function mockUseQueryRequest(queryKey) {
  const key = Array.isArray(queryKey) ? queryKey[0] : queryKey;

  if (typeof key === 'string') {
    switch (key) {
      case 'GetEckohStatus':
        return mockEckohStatusData;
      case 'GetPaymentStatus':
        return getPaymentStatus;
      case 'GetBookingInformation':
        return {
          ...getBookingInformationData,
          refetch: mockRefetchBooking,
        };
      case 'GetHotelInformation':
        return getHotelInformationData;
      case 'getPaymentMethodsCCUI':
        return getPaymentsCcui;
      default:
        return {};
    }
  }
}

const mockCustomLocale = jest.fn();
const mockUseLocalStorage = jest.fn();
const mockSetPaymentFailure = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
  invalidateQueries: mockUseQueryRequest,
  useQueryRequest: mockUseQueryRequest,
  useMutationRequest: (key: string) => mockUseMutationRequest(key),
  useLocalStorage: () => mockUseLocalStorage(),
  useAgentMemo: () => ({ agentMemoState: { reservationId: '12' } }),
  setAnalyticsUser: jest.fn(),
  analytics: {
    update: jest.fn(),
  },
  usePackages: () => ({
    ...mockGetPackagesData?.data?.packages,
    ...mockGetPackagesData,
  }),
  graphQLRequest: jest.fn(),
  updateAncillariesAnalytics: () => jest.fn(),
  useFeatureToggle: jest.fn(() => ({})),
  useSessionStorage: jest.fn(() => [null, mockSetPaymentFailure]),
}));

jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  setAnalyticsUser: jest.fn(),
}));

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    locale: 'en',
    query: {
      reservationId: '12',
    },
    push: jest.fn(),
  }),
}));

const mockProps = {
  query: {
    reservationId: '12',
    BRAND: 'CCUI',
  },
  routerPush: jest.fn(),
  accessToken: 'accessToken',
  basketReference: 'basketReference',
  user: { 'https://ccui.opera.whitbread.digital/role': [UserRoles.MANAGER] },
  setAnalyticsUser: jest.fn(),
  resourceIdByRoles: ['CC_Role05'],
  biQueryInput: {
    basketReference: 'AWM222',
    country: 'GB',
    language: 'en',
  },
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

const mockRouter = {
  push: jest.fn(),
  query: jest.fn(),
};

const mockFormDetails = {
  reasonForStay: 'LEI',
  title: 'Mr',
  firstName: 'Tom',
  lastName: 'Smith',
  email: 'german@email.com',
  phone: '+49078234234234',
  landline: null,
  companyName: '',
  addressLine1: 'Kurfürstenstraße 52',
  addressLine2: '',
  addressLine3: '',
  addressLine4: '',
  postalCode: '10785',
  cityName: 'Berlin',
  postcodeAddress: '',
  addressSelection: 'HOME',
  countryCode: 'DE',
  manualAddressToggle: 'manualAddress',
  basketReferenceId: 'GAA-1807f47d-8935-480c-a5d9-46b639d06cd2',
  leadGuest: [],
  bookingForSomeoneElse: false,
  billing: {},
  whoBookerIsTabs: 'MYSELF',
  dateOfBirth: '',
  nationality: '',
  passport: '',
  updated: true,
};

describe('Payment page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    resetMutationResponse(mockMutationResponse);
    resetMutationResponse(mockUpdateDiscountMutationResponse);
    resetMutationResponse(mockUpdateEmailMutationResponse);
    resetMutationResponse(mockInitiateIframeMutationResponse);
    getBookingInformationData.data.bookingInformation.discount = 0;
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'en',
    });

    mockUseLocalStorage.mockReturnValue([mockFormDetails, jest.fn()]);
  });
  it('should render Payment page CCUI', async () => {
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getPaymentStatus.data.basket.status = BASKET_STATUS.FAILED;
    const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);
    await waitFor(() => {
      expect(getByTestId('paymentPageSection')).toBeInTheDocument();
      expect(getByTestId('paymentPageSection_wrapper')).toBeInTheDocument();
      expect(getByTestId('paymentPageSection_wrapperTitle')).toBeInTheDocument();
      expect(getByTestId('paymentPageSection_title')).toBeInTheDocument();
      expect(getByTestId('paymentPageSection_wrapperTitleDescription')).toBeInTheDocument();
      expect(getByTestId('paymentPageSection_titleDescription')).toBeInTheDocument();
    });
  });
  it('should render CCUI Payment page with no packages', async () => {
    mockGetPackagesData.data.packages.packages.roomSelection = [];
    const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);
    await waitFor(() => {
      expect(getByTestId('paymentPageSection')).toBeInTheDocument();
    });
  });
  it('should render Payment Page CCUI booking summary', async () => {
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockGetPackagesData.data.packages.hotelHasCityTaxForBusiness = false;
    mockGetPackagesData.data.packages.hotelHasCityTaxForLeisure = true;

    const { getByTestId, getByText } = render(<PaymentPageCcui {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('BookingSummary-DesktopVariant-Wrapper')).toBeInTheDocument();
      expect(
        getByText(
          '2 hoteldetails.bookingsummary.rooms, 1 booking.summary.night | 23 Mar - 24 Mar | £999.00 | booking.summary.rate Flex'
        )
      ).toBeInTheDocument();
      expect(getByTestId('BookingSummary-MobileVariant-SectionWrapper')).toBeInTheDocument();
      expect(getByTestId('BookingSummary-MobileVariant-SectionHeader')).toBeInTheDocument();

      expect(
        getByTestId('BookingSummary-DesktopVariant-TotalCost-TotalCostPrice')
      ).toBeInTheDocument();

      expect(getByTestId('BookingSummary-DesktopVariant-TotalCost-CostAmount')).toBeInTheDocument();

      expect(getByTestId('BookingSummary-DesktopVariant-TotalCost-VATMessage')).toBeInTheDocument();
      expect(
        getByTestId('BookingSummary-DesktopVariant-StayDatesInformation-ArrivalDate')
      ).toBeInTheDocument();
      expect(
        getByTestId('BookingSummary-DesktopVariant-StayDatesInformation-DepartureDate')
      ).toBeInTheDocument();
      expect(
        getByTestId('BookingSummary-DesktopVariant-StayDatesInformation-NightsNumber')
      ).toBeInTheDocument();
      expect(
        getByTestId('BookingSummary-DesktopVariant-RoomInformation-Wrapper')
      ).toBeInTheDocument();
    });
  });

  it('should render important info message on BookingSummary-InfoMessages when hideOnBookingFlow is false', async () => {
    const originalInfoItems = getHotelInformationData.data.hotelInformation.importantInfo.infoItems;
    try {
      getHotelInformationData.data.hotelInformation.importantInfo.infoItems = originalInfoItems.map(
        (item) => ({ ...item, hideOnBookingFlow: false })
      );

      const { getByTestId, getByText } = render(<PaymentPageCcui {...mockProps} />);

      await waitFor(() => {
        expect(getByTestId('BookingSummary-InfoMessages')).toBeInTheDocument();
        expect(getByText(/The bathrooms in this hotel have showers only\./)).toBeInTheDocument();
        expect(
          getByText(/Another important information message with highest priority\./)
        ).toBeInTheDocument();
        expect(
          getByText(
            /Parking is not going to be available during this period of time due to some works on the street\./
          )
        ).toBeInTheDocument();
      });
    } finally {
      getHotelInformationData.data.hotelInformation.importantInfo.infoItems = originalInfoItems;
    }
  });

  it('should not render any important info message when all items have hideOnBookingFlow true', async () => {
    const originalInfoItems = getHotelInformationData.data.hotelInformation.importantInfo.infoItems;
    try {
      getHotelInformationData.data.hotelInformation.importantInfo.infoItems = originalInfoItems.map(
        (item) => ({ ...item, hideOnBookingFlow: true })
      );

      const { queryByTestId, queryByText } = render(<PaymentPageCcui {...mockProps} />);

      await waitFor(() => {
        expect(queryByTestId('BookingSummary-InfoMessages')).not.toBeInTheDocument();
        expect(
          queryByText(/The bathrooms in this hotel have showers only\./)
        ).not.toBeInTheDocument();
      });
    } finally {
      getHotelInformationData.data.hotelInformation.importantInfo.infoItems = originalInfoItems;
    }
  });
  it('should render Payment Page CCUI booking summary with discount', async () => {
    getBookingInformationData.data.bookingInformation.discount = 22;

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockGetPackagesData.data.packages.hotelHasCityTaxForBusiness = false;
    mockGetPackagesData.data.packages.hotelHasCityTaxForLeisure = true;

    const { getByTestId, getByText } = render(<PaymentPageCcui {...mockProps} />);

    expect(getByTestId('BookingSummary-DesktopVariant-Wrapper')).toBeInTheDocument();
    expect(
      getByText(
        '2 hoteldetails.bookingsummary.rooms, 1 booking.summary.night | 23 Mar - 24 Mar | £999.00 | booking.summary.rate Flex'
      )
    ).toBeInTheDocument();
    expect(getByTestId('BookingSummary-MobileVariant-SectionWrapper')).toBeInTheDocument();
    expect(getByTestId('BookingSummary-MobileVariant-SectionHeader')).toBeInTheDocument();

    //display discount text
    expect(getByTestId('BookingSummary-DesktopVariant-TotalCost-DiscountName')).toBeInTheDocument();
    //display discount value
    expect(
      getByTestId('BookingSummary-DesktopVariant-TotalCost-DiscountPrice')
    ).toBeInTheDocument();

    //newTotalCost text
    expect(
      getByTestId('BookingSummary-DesktopVariant-TotalCost-NewTotalCostName')
    ).toBeInTheDocument();

    //previousTotalCost text
    expect(
      getByTestId('BookingSummary-DesktopVariant-TotalCost-PreviousTotalCostName')
    ).toBeInTheDocument();
    //previousTotalCost value
    expect(
      getByTestId('BookingSummary-DesktopVariant-TotalCost-PreviousTotalCostPrice')
    ).toBeInTheDocument();

    //newTotalCost value
    expect(
      getByTestId('BookingSummary-DesktopVariant-TotalCost-TotalCostValue')
    ).toBeInTheDocument();

    expect(getByTestId('BookingSummary-DesktopVariant-TotalCost-VATMessage')).toBeInTheDocument();
    expect(
      getByTestId('BookingSummary-DesktopVariant-StayDatesInformation-ArrivalDate')
    ).toBeInTheDocument();
    expect(
      getByTestId('BookingSummary-DesktopVariant-StayDatesInformation-DepartureDate')
    ).toBeInTheDocument();
    expect(
      getByTestId('BookingSummary-DesktopVariant-StayDatesInformation-NightsNumber')
    ).toBeInTheDocument();
    expect(
      getByTestId('BookingSummary-DesktopVariant-RoomInformation-Wrapper')
    ).toBeInTheDocument();
  });
  it('should render page with  reservationByIdList undefined', async () => {
    getBookingInformationData.data.bookingInformation.currencyCode = 'EUR';
    const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);

    expect(getByTestId('paymentPageSection')).toBeInTheDocument();
  });
  it('should render page when user is agent', async () => {
    mockProps.user['https://ccui.opera.whitbread.digital/role'][0] = UserRoles.AGENT;
    const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);

    expect(getByTestId('paymentPageSection')).toBeInTheDocument();
  });
  it('should render page with PIBA checked', async () => {
    getPaymentsCcui.data.paymentCcuiMethods = [
      {
        cnpOptionAvailable: true,
        cnpPreSelected: false,
        enabled: true,
        name: 'PIBA UK',
        order: 2,
        reasons: [],
        subType: 'PIBAGB',
        type: 'NEW_PIBA',
        paymentOptions: [
          {
            enabled: false,
            order: 1,
            type: 'PAY_NOW',
          },
          {
            enabled: true,
            order: 2,
            type: 'PAY_ON_ARRIVAL',
          },
        ],
        acceptedCardTypes: [
          {
            logoSrc: '/content/dam/global/booking/Business_Account.jpg',
            name: 'Business Account',
            type: 'PI',
          },
        ],
      },
    ];
    const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);

    expect(getByTestId('paymentPageSection')).toBeInTheDocument();
  });
  it('should render page with A2C checked', async () => {
    getPaymentsCcui.data.paymentCcuiMethods = [
      {
        cnpOptionAvailable: false,
        cnpPreSelected: false,
        enabled: true,
        name: 'Account to company',
        order: 4,
        reasons: [],
        subType: null,
        type: 'ACCOUNT_COMPANY',
        paymentOptions: [
          {
            enabled: false,
            order: 1,
            type: 'PAY_NOW',
          },
          {
            enabled: true,
            order: 2,
            type: 'PAY_ON_ARRIVAL',
          },
        ],
        acceptedCardTypes: null,
      },
    ];
    const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);

    expect(getByTestId('paymentPageSection')).toBeInTheDocument();
  });
  it('should render page with CC checked and pay on arrival', async () => {
    mockCustomLocale.mockReturnValue({
      language: 'gb',
      country: 'en',
    });
    mockProps.user['https://ccui.opera.whitbread.digital/role'][0] = UserRoles.MANAGER;
    getPaymentsCcui.data.paymentCcuiMethods = [
      {
        cnpOptionAvailable: false,
        cnpPreSelected: false,
        enabled: true,
        name: 'CARD',
        order: 1,
        reasons: [],
        subType: null,
        type: 'NEW_CARD',
        paymentOptions: [
          {
            enabled: false,
            order: 1,
            type: 'PAY_NOW',
          },
          {
            enabled: true,
            order: 2,
            type: 'PAY_ON_ARRIVAL',
          },
        ],
        acceptedCardTypes: [
          {
            logoSrc: '/content/dam/global/booking/Mastercard.jpg',
            name: 'Mastercard Credit',
            type: 'MC',
          },
          {
            logoSrc: '/content/dam/global/booking/AX.jpg',
            name: 'American Express',
            type: 'AX',
          },
          {
            logoSrc: '/content/dam/global/booking/dinersclub.jpg',
            name: 'Diners Club',
            type: 'DN',
          },
          {
            logoSrc: '/content/dam/global/booking/Visa_Debit.jpg',
            name: 'Visa Debit',
            type: 'VS',
          },
          {
            logoSrc: '/content/dam/global/booking/Electron_white_v.jpg',
            name: 'Electron',
            type: 'VS',
          },
          {
            logoSrc: '/content/dam/global/booking/maestro.jpg',
            name: 'Maestro',
            type: 'MA',
          },
          {
            logoSrc: '/content/dam/global/booking/MD.jpg',
            name: 'Mastercard Debit',
            type: 'MC',
          },
          {
            logoSrc: '/content/dam/global/booking/VC.jpg',
            name: 'Visa Credit',
            type: 'VS',
          },
        ],
      },
    ];

    const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);

    expect(getByTestId('paymentPageSection')).toBeInTheDocument();
  });
  it('should render page with NonGuaranteed checked', async () => {
    mockProps.user['https://ccui.opera.whitbread.digital/role'][0] = UserRoles.MANAGER;
    getPaymentsCcui.data.paymentCcuiMethods = [
      {
        cnpOptionAvailable: false,
        cnpPreSelected: false,
        enabled: true,
        name: 'Non-guaranteed booking',
        order: 5,
        reasons: [],
        subType: null,
        type: 'RESERVE_WITHOUT_CARD',
        paymentOptions: [
          {
            enabled: false,
            order: 1,
            type: 'PAY_NOW',
          },
          {
            enabled: true,
            order: 2,
            type: 'PAY_ON_ARRIVAL',
          },
        ],
        acceptedCardTypes: null,
      },
    ];

    getBookingInformationData.data.bookingInformation.hotelId = 'BERALX';
    mockProps.pcksQueryInput.hotelId = 'BERALX';
    mockProps.hiQueryInput.hotelId = 'BERALX';

    const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);

    expect(getByTestId('paymentPageSection')).toBeInTheDocument();
  });
  it('should render page with POA and Non Guaranteed Booking and click on submit Button', async () => {
    mockProps.user['https://ccui.opera.whitbread.digital/role'][0] = UserRoles.MANAGER;
    getPaymentsCcui.data.paymentCcuiMethods = [
      {
        cnpOptionAvailable: false,
        cnpPreSelected: false,
        enabled: true,
        name: 'Non-guaranteed booking',
        order: 5,
        reasons: [],
        subType: null,
        type: 'RESERVE_WITHOUT_CARD',
        paymentOptions: [
          {
            enabled: false,
            order: 1,
            type: 'PAY_NOW',
          },
          {
            enabled: true,
            order: 2,
            type: 'PAY_ON_ARRIVAL',
          },
        ],
        acceptedCardTypes: null,
      },
    ];

    getBookingInformationData.data.bookingInformation.hotelId = 'BERALX';
    mockProps.pcksQueryInput.hotelId = 'BERALX';
    mockProps.hiQueryInput.hotelId = 'BERALX';

    const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);

    expect(getByTestId('paymentPageSection')).toBeInTheDocument();

    const optionPN = getByTestId('radio-box-wrapper_PAY_NOW');
    const optionPOA = getByTestId('radio-box-wrapper_PAY_ON_ARRIVAL');

    expect(optionPN).toBeInTheDocument();
    expect(optionPOA).toBeInTheDocument();

    userEvent.click(optionPOA);

    const optionNGB = getByTestId('radio-box-wrapper_payment-type-radio-0');

    expect(optionNGB).toBeInTheDocument();

    userEvent.click(optionNGB);

    const optionAnyCustomer = getByTestId('radio-box-wrapper_typeOfCaller_ANY_CUSTOMER');

    userEvent.click(optionAnyCustomer);

    const optionRoomRatesPolicies = getByTestId('totalCostSection_room-rate-policies-wrapper');

    userEvent.click(optionRoomRatesPolicies);

    const button = getByTestId('totalCostSection_confirm-booking-total-cost');

    await waitFor(() => {
      fireEvent.click(button);
    });
  });
  it('should render Payment page with discount null', async () => {
    getBookingInformationData.data.bookingInformation.discount = null;

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getPaymentStatus.data.basket.status = BASKET_STATUS.FAILED;
    render(<PaymentPageCcui {...mockProps} />);
  });
  it('should render Payment page with discount 0', async () => {
    getBookingInformationData.data.bookingInformation.discount = 0;

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getPaymentStatus.data.basket.status = BASKET_STATUS.FAILED;
    render(<PaymentPageCcui {...mockProps} />);
  });

  it('should skip discount update mutation when discount is empty and booking discount is zero', async () => {
    getBookingInformationData.data.bookingInformation.discount = 0;

    const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);
    const discountInput = await waitFor(() => getByTestId('input-discount'));

    fireEvent.blur(discountInput);

    expect(mockUpdateDiscountMutationResponse.mutation.mutateAsync).not.toHaveBeenCalled();
    expect(mockRefetchBooking).not.toHaveBeenCalled();
  });

  it('should skip discount update mutation when discount is zero and booking discount is zero', async () => {
    getBookingInformationData.data.bookingInformation.discount = 0;

    const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);
    const discountInput = await waitFor(() => getByTestId('input-discount'));

    fireEvent.change(discountInput, { target: { value: '0' } });
    fireEvent.blur(discountInput);

    expect(mockUpdateDiscountMutationResponse.mutation.mutateAsync).not.toHaveBeenCalled();
    expect(mockRefetchBooking).not.toHaveBeenCalled();
  });

  it('should call discount update mutation when booking discount is not zero', async () => {
    getBookingInformationData.data.bookingInformation.discount = 10;

    const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);
    const discountInput = await waitFor(() => getByTestId('input-discount'));

    fireEvent.blur(discountInput);

    expect(mockUpdateDiscountMutationResponse.mutation.mutateAsync).toHaveBeenCalled();
  });

  it('should render Payment page with PIBA, POA and CNP', async () => {
    getBookingInformationData.data.bookingInformation.totalCost = null;

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    getPaymentsCcui.data.paymentCcuiMethods = [
      {
        cnpOptionAvailable: true,
        cnpPreSelected: false,
        enabled: true,
        name: 'PIBA UK',
        order: 2,
        reasons: [],
        subType: 'PIBAGB',
        type: 'NEW_PIBA',
        paymentOptions: [
          {
            enabled: false,
            order: 1,
            type: 'PAY_NOW',
          },
          {
            enabled: true,
            order: 2,
            type: 'PAY_ON_ARRIVAL',
          },
        ],
        acceptedCardTypes: [
          {
            logoSrc: '/content/dam/global/booking/Business_Account.jpg',
            name: 'Business Account',
            type: 'PI',
          },
        ],
      },
    ];

    const { getByTestId, container } = render(<PaymentPageCcui {...mockProps} />);

    expect(getByTestId('paymentPageSection')).toBeInTheDocument();

    const optionCNP = getByTestId('radio-box-wrapper_CNP (Card not present)');

    expect(optionCNP).toBeInTheDocument();

    const radioOptionCNP = container.querySelector(
      'div[data-testid="radio-box-wrapper_CNP (Card not present)"] input'
    );

    userEvent.click(radioOptionCNP);

    await waitFor(() => {
      expect(radioOptionCNP).toBeChecked();
    });
  });
  it('should call payment mutation with all booking information', async () => {
    getPaymentStatus.data.basket.status = BASKET_STATUS.OPEN;
    getBookingInformationData.data.bookingInformation.reservationByIdList[0].billing = {
      email: 'royalpayne@email.com',
      address: {
        addressType: 'HOME',
        companyName: '',
        addressLine1: '1 Royal Payne Street',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        cityName: 'Hamburger',
        country: 'UK',
        postalCode: 'LS25 5AD',
        billingAddressSelection: 'CurrentAddress',
      },
    };
    getPaymentsCcui.data.paymentCcuiMethods = [
      {
        acceptedCardTypes: [],
        cnpOptionAvailable: false,
        cnpPreSelected: false,
        enabled: true,
        name: 'Non-guaranteed booking',
        subType: null,
        order: 5,
        paymentOptions: [
          {
            enabled: false,
            order: 1,
            type: 'PAY_NOW',
          },
          {
            enabled: true,
            order: 2,
            type: 'PAY_ON_ARRIVAL',
          },
        ],
        reasons: [],
        type: 'RESERVE_WITHOUT_CARD',
      },
    ];

    const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);

    // Wait for the radio button to appear in the document

    const radioButton = getByTestId('radio-box-wrapper_PAY_ON_ARRIVAL');
    expect(radioButton).toBeInTheDocument();
    await userEvent.click(radioButton);

    const payWithoutCardRadio = getByTestId('radio-box-wrapper_payment-type-radio-0');
    expect(payWithoutCardRadio).toBeInTheDocument();
    await userEvent.click(payWithoutCardRadio);
    const roomRateCheckbox = getByTestId('roomRatePolicies_checkbox');
    expect(roomRateCheckbox).toBeInTheDocument();
    await userEvent.click(roomRateCheckbox);

    const modalCloseButton = getByTestId('roomRatePolicies-ModalCloseButton');
    expect(modalCloseButton).toBeInTheDocument();
    await userEvent.click(modalCloseButton);
    const confirmBookingButton = getByTestId('totalCostSection_confirm-booking-total-cost');
    expect(confirmBookingButton).toBeInTheDocument();
    await userEvent.click(confirmBookingButton);

    await waitFor(() => {
      expect(mockMutationResponse.mutation.mutateAsync).toHaveBeenCalledWith({
        basketReference: 'basketReference',
        paymentOption: 'RESERVE_WITHOUT_CARD',
        subPaymentType: null,
        sendMail: true,
        cardPresent: false,
        companyNumber: '',
        companyId: '',
        charges: '',
        typeOfCaller: 'ANY_CUSTOMER',
        addressCompanyName: '',
        requestId: expect.any(String),
        type: 'Non-guaranteed booking',
        subType: 'MOTO',
        firstName: '',
        lastName: '',
        title: '',
        email: 'royalpayne@email.com',
        telephone: '',
        bookerIsNotGuest: false,
        differentBillingAddress: false,
        addressLine1: '1 Royal Payne Street',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        cityName: 'Hamburger',
        postalCode: 'LS25 5AD',
        country: 'GB',
        companyName: '',
        addressType: 'CurrentAddress',
        card: {
          cardHolderLastName: '',
          cardHolderFirstName: '',
          cardHolderAddress: {
            addressLine1: '1 Royal Payne Street',
            addressLine2: '',
            addressLine3: '',
            addressLine4: '',
            cityName: 'Hamburger',
            postalCode: 'LS25 5AD',
            country: 'GB',
            addressType: 'HOME',
          },
        },
        bookingType: 'PAY_ON_ARRIVAL',
        journey: 'BOOKING',
        channel: 'CCC',
        language: 'en',
        identifier: expect.any(String),
        bookingBusinessSiteType: 'HOTEL',
        name: 'Manchester Old Trafford',
        location: 'Sir Alex Ferguson Way',
        businessItems: null,
      });
    });
  });
  it('should call payment mutation with different billing address', async () => {
    getPaymentStatus.data.basket.status = BASKET_STATUS.OPEN;
    getBookingInformationData.data.bookingInformation.reservationByIdList[0].billing = {
      email: 'royalpayne@email.com',
      address: {
        addressType: 'HOME',
        companyName: '',
        addressLine1: '1 Royal Payne Street',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        cityName: 'Hamburger',
        country: 'DE',
        postalCode: 'LS25 5AD',
        billingAddressSelection: 'CurrentAddress',
      },
    };
    getPaymentsCcui.data.paymentCcuiMethods = [
      {
        acceptedCardTypes: [],
        cnpOptionAvailable: false,
        cnpPreSelected: false,
        enabled: true,
        name: 'Non-guaranteed booking',
        subType: null,
        order: 5,
        paymentOptions: [
          {
            enabled: false,
            order: 1,
            type: 'PAY_NOW',
          },
          {
            enabled: true,
            order: 2,
            type: 'PAY_ON_ARRIVAL',
          },
        ],
        reasons: [],
        type: 'RESERVE_WITHOUT_CARD',
      },
    ];

    mockFormDetails.billing = {
      address: {
        companyName: '',
        countryCode: 'DE',
        addressLine1: 'Brühlweg 10',
        postalCode: '60318',
        cityName: 'Hamburger',
        billingAddressSelection: 'HOME',
      },
      differentBillingAddress: true,
      isBillingAddressDisplayed: true,
    };

    const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);

    // Wait for the radio button to appear in the document

    const radioButton = getByTestId('radio-box-wrapper_PAY_ON_ARRIVAL');
    expect(radioButton).toBeInTheDocument();
    await userEvent.click(radioButton);

    const payWithoutCardRadio = getByTestId('radio-box-wrapper_payment-type-radio-0');
    expect(payWithoutCardRadio).toBeInTheDocument();
    await userEvent.click(payWithoutCardRadio);
    const roomRateCheckbox = getByTestId('roomRatePolicies_checkbox');
    expect(roomRateCheckbox).toBeInTheDocument();
    await userEvent.click(roomRateCheckbox);

    await waitFor(() => {
      const modalCloseButton = getByTestId('roomRatePolicies-ModalCloseButton');
      expect(modalCloseButton).toBeInTheDocument();
      userEvent.click(modalCloseButton);
    });

    const confirmBookingButton = getByTestId('totalCostSection_confirm-booking-total-cost');
    expect(confirmBookingButton).toBeInTheDocument();
    userEvent.click(confirmBookingButton);
    await waitFor(() => {
      expect(mockMutationResponse.mutation.mutateAsync).toHaveBeenCalledWith({
        basketReference: 'basketReference',
        paymentOption: 'RESERVE_WITHOUT_CARD',
        subPaymentType: null,
        sendMail: true,
        cardPresent: false,
        companyNumber: '',
        companyId: '',
        charges: '',
        typeOfCaller: 'ANY_CUSTOMER',
        addressCompanyName: '',
        requestId: expect.any(String),
        type: 'Non-guaranteed booking',
        subType: 'MOTO',
        firstName: '',
        lastName: '',
        title: '',
        email: 'royalpayne@email.com',
        telephone: '',
        bookerIsNotGuest: false,
        differentBillingAddress: true,
        addressLine1: 'Brühlweg 10',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        cityName: 'Hamburger',
        postalCode: '60318',
        country: 'DE',
        companyName: '',
        addressType: 'HOME',
        card: {
          cardHolderLastName: '',
          cardHolderFirstName: '',
          cardHolderAddress: {
            addressLine1: '1 Royal Payne Street',
            addressLine2: '',
            addressLine3: '',
            addressLine4: '',
            cityName: 'Hamburger',
            postalCode: 'LS25 5AD',
            country: 'DE',
            addressType: 'HOME',
          },
        },
        bookingType: 'PAY_ON_ARRIVAL',
        journey: 'BOOKING',
        channel: 'CCC',
        language: 'en',
        identifier: expect.any(String),
        bookingBusinessSiteType: 'HOTEL',
        name: 'Manchester Old Trafford',
        location: 'Sir Alex Ferguson Way',
        businessItems: null,
      });
    });
  });
  it('should render Payment page with Initiate payment process error and redirect to payments-errors page', async () => {
    mockMutationResponse.error = 'Error initiate payment process';
    mockMutationResponse.isSuccess = false;
    mockMutationResponse.isError = true;

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    render(<PaymentPageCcui {...mockProps} />);
  });

  it('should render Payment page with Initiate payment process error and redirect to payments-errors page with session storage set', async () => {
    const errorInfo = {
      errCode: 135,
      debugMessage:
        'A fraud check was triggered and no payment has been made. Payment status from threec is FAILURE',
      globalErrTextTemplate: 'fraud_check_failed',
    };
    mockMutationResponse.error = {
      response: {
        data: null,
        errors: [
          {
            path: ['initiateCcuiPayment'],
            data: null,
            errorType: '409',
            errorInfo: errorInfo,
            locations: [
              {
                line: 42,
                column: 5,
                sourceName: null,
              },
            ],
            message: 'fraud_check_failed',
          },
        ],
      },
    };
    mockMutationResponse.isSuccess = false;
    mockMutationResponse.isError = true;

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    render(<PaymentPageCcui {...mockProps} />);

    await waitFor(() => {
      expect(mockSetPaymentFailure).toHaveBeenCalledWith(errorInfo);
    });
  });

  it('should render Payment page with Initiate payment process error (Apollo) and redirect to payments-errors page with session storage set', async () => {
    const errorInfo = {
      errCode: 135,
      debugMessage: 'error',
      globalErrTextTemplate: 'error',
    };

    const errorMessage = JSON.stringify(errorInfo);
    mockMutationResponse.error = {
      response: {
        data: null,
        errors: [
          {
            path: ['initiateCcuiPayment'],
            locations: [
              {
                line: 42,
                column: 5,
                sourceName: null,
              },
            ],
            message: errorMessage,
          },
        ],
      },
    };
    mockMutationResponse.isSuccess = false;
    mockMutationResponse.isError = true;

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    render(<PaymentPageCcui {...mockProps} />);

    await waitFor(() => {
      expect(mockSetPaymentFailure).toHaveBeenCalledWith(errorInfo);
    });
  });

  it('should render Payment page with basketStatus completed', async () => {
    getPaymentStatus.data.basket.status = BASKET_STATUS.COMPLETED;
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    render(<PaymentPageCcui {...mockProps} />);
  });
  it('should use email from formDetails (GDP) over stale billing email during repeat booking', async () => {
    const emailA = 'michael.schiele@email.com';
    const emailB = 'stefan.oberdorfer@email.com';

    getPaymentStatus.data.basket.status = BASKET_STATUS.OPEN;
    getBookingInformationData.data.bookingInformation.reservationByIdList[0].billing = {
      email: emailA,
      address: {
        addressType: 'HOME',
        companyName: 'LYNQTECH GmbH',
        addressLine1: '1 Royal Payne Street',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        cityName: 'Hamburger',
        country: 'UK',
        postalCode: 'LS25 5AD',
        billingAddressSelection: 'CurrentAddress',
      },
    };
    getPaymentsCcui.data.paymentCcuiMethods = [
      {
        acceptedCardTypes: [],
        cnpOptionAvailable: false,
        cnpPreSelected: false,
        enabled: true,
        name: 'Non-guaranteed booking',
        subType: null,
        order: 5,
        paymentOptions: [
          {
            enabled: false,
            order: 1,
            type: 'PAY_NOW',
          },
          {
            enabled: true,
            order: 2,
            type: 'PAY_ON_ARRIVAL',
          },
        ],
        reasons: [],
        type: 'RESERVE_WITHOUT_CARD',
      },
    ];

    mockUseLocalStorage.mockReturnValue([
      {
        ...mockFormDetails,
        email: emailB,
        basketReferenceId: mockProps.basketReference,
      },
      jest.fn(),
    ]);

    (useFeatureToggle as jest.Mock).mockReturnValue({});
    mockMutationResponse.error = '';
    mockMutationResponse.isError = false;
    mockMutationResponse.isSuccess = false;

    const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);

    const radioButton = await waitFor(() => getByTestId('radio-box-wrapper_PAY_ON_ARRIVAL'));
    expect(radioButton).toBeInTheDocument();
    await userEvent.click(radioButton);

    const payWithoutCardRadio = getByTestId('radio-box-wrapper_payment-type-radio-0');
    expect(payWithoutCardRadio).toBeInTheDocument();
    await userEvent.click(payWithoutCardRadio);
    const roomRateCheckbox = getByTestId('roomRatePolicies_checkbox');
    expect(roomRateCheckbox).toBeInTheDocument();
    await userEvent.click(roomRateCheckbox);

    const modalCloseButton = getByTestId('roomRatePolicies-ModalCloseButton');
    expect(modalCloseButton).toBeInTheDocument();
    await userEvent.click(modalCloseButton);
    const confirmBookingButton = getByTestId('totalCostSection_confirm-booking-total-cost');
    expect(confirmBookingButton).toBeInTheDocument();
    await userEvent.click(confirmBookingButton);

    await waitFor(() => {
      expect(mockMutationResponse.mutation.mutateAsync).toHaveBeenCalledWith(
        expect.objectContaining({
          email: emailB,
        })
      );
    });
  });
  it('should update analyitcs with payment outage data', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      kill_switch_pi_bb_ccui_disable_payments: true,
    });

    render(<PaymentPageCcui {...mockProps} />);
    expect(analytics.update).toBeCalledWith({
      paymentCardSelected: CcuiCardType.NON_GUARANTEED,
      paymentOutage: true,
      cardType: CcuiCardType.NON_GUARANTEED,
    });
  });

  const reserveWithoutCardPaymentMethod = [
    {
      acceptedCardTypes: [],
      cnpOptionAvailable: false,
      cnpPreSelected: false,
      enabled: true,
      name: 'Non-guaranteed booking',
      subType: null,
      order: 5,
      paymentOptions: [
        { enabled: false, order: 1, type: 'PAY_NOW' },
        { enabled: true, order: 2, type: 'PAY_ON_ARRIVAL' },
      ],
      reasons: [],
      type: 'RESERVE_WITHOUT_CARD',
    },
  ];

  async function triggerConfirmBooking(getByTestId: (id: string) => HTMLElement) {
    const radioButton = await waitFor(() => getByTestId('radio-box-wrapper_PAY_ON_ARRIVAL'));
    await userEvent.click(radioButton);
    const payWithoutCardRadio = getByTestId('radio-box-wrapper_payment-type-radio-0');
    await userEvent.click(payWithoutCardRadio);
    const roomRateCheckbox = getByTestId('roomRatePolicies_checkbox');
    await userEvent.click(roomRateCheckbox);
    const modalCloseButton = getByTestId('roomRatePolicies-ModalCloseButton');
    await userEvent.click(modalCloseButton);
    const confirmBookingButton = getByTestId('totalCostSection_confirm-booking-total-cost');
    await userEvent.click(confirmBookingButton);
  }

  describe('FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE', () => {
    beforeEach(() => {
      getPaymentsCcui.data.paymentCcuiMethods = reserveWithoutCardPaymentMethod;
      getPaymentStatus.data.basket.status = BASKET_STATUS.OPEN;
      getBookingInformationData.data.bookingInformation.reservationByIdList = [
        {
          billing: {
            firstName: 'John',
            lastName: 'Booker',
            title: 'Mr',
            email: 'firstroom@email.com',
            telephone: '',
            address: {
              addressType: 'HOME',
              companyName: '',
              addressLine1: '1 Test Street',
              addressLine2: '',
              addressLine3: '',
              addressLine4: '',
              cityName: 'London',
              country: 'UK',
              postalCode: 'SW1A 1AA',
              billingAddressSelection: 'CurrentAddress',
            },
          },
          reservationGuestList: [
            {
              givenName: 'John',
              surName: 'Booker',
              nameTitle: 'Mr',
              isAccompanyingGuest: false,
              email: null,
            },
          ],
          roomStay: {
            adultsNumber: 1,
            childrenNumber: 0,
            arrivalDate: '2023-03-23',
            departureDate: '2023-03-24',
            ratePlanCode: 'FLEXRATE',
            rateExtraInfo: { rateName: 'Flex' },
            roomExtraInfo: { roomType: ROOM_TYPE.PREMIER_PLUS, roomName: 'Premier Plus Room' },
            accessibleRoom: { isAccessible: false, phoneNumber: '0333 321 1315' },
          },
        },
      ] as any;
    });

    it('should use firstRoom billing email when flag is enabled, ignoring formData differentBillingAddress', async () => {
      mockUseLocalStorage.mockReturnValue([
        {
          ...mockFormDetails,
          basketReferenceId: mockProps.basketReference,
          billing: {
            differentBillingAddress: true,
            email: 'formbilling@email.com',
          },
        },
        jest.fn(),
      ]);
      (useFeatureToggle as jest.Mock).mockReturnValue({
        [FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE]: true,
      });

      const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);
      await triggerConfirmBooking(getByTestId);

      await waitFor(() => {
        expect(mockMutationResponse.mutation.mutateAsync).toHaveBeenCalledWith(
          expect.objectContaining({
            email: 'firstroom@email.com',
            differentBillingAddress: false,
          })
        );
      });
    });

    it('should use formData billing data when flag is disabled and differentBillingAddress is set', async () => {
      mockUseLocalStorage.mockReturnValue([
        {
          ...mockFormDetails,
          basketReferenceId: mockProps.basketReference,
          billing: {
            differentBillingAddress: true,
            isBillingAddressDisplayed: true,
            address: {
              companyName: '',
              countryCode: 'UK',
              addressLine1: '2 Form Street',
              postalCode: 'SW1A 2AA',
              cityName: 'London',
              billingAddressSelection: 'HOME',
            },
          },
        },
        jest.fn(),
      ]);
      (useFeatureToggle as jest.Mock).mockReturnValue({
        [FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE]: false,
      });

      const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);
      await triggerConfirmBooking(getByTestId);

      await waitFor(() => {
        expect(mockMutationResponse.mutation.mutateAsync).toHaveBeenCalledWith(
          expect.objectContaining({
            // emailAddress state comes from formData.email when flag is disabled and basket matches
            email: mockFormDetails.email,
            differentBillingAddress: true,
          })
        );
      });
    });

    it('should derive bookerIsNotGuest from reservations when flag is enabled', async () => {
      // formData says booking is for someone else, but reservations say guest IS the booker
      mockUseLocalStorage.mockReturnValue([
        {
          ...mockFormDetails,
          bookingForSomeoneElse: true,
          billing: {},
        },
        jest.fn(),
      ]);
      (useFeatureToggle as jest.Mock).mockReturnValue({
        [FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE]: true,
      });

      const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);
      await triggerConfirmBooking(getByTestId);

      await waitFor(() => {
        expect(mockMutationResponse.mutation.mutateAsync).toHaveBeenCalledWith(
          expect.objectContaining({
            // guest (John Booker Mr) matches billing (John Booker Mr) → not booking for someone else
            bookerIsNotGuest: false,
          })
        );
      });
    });

    it('should use formData bookingForSomeoneElse when flag is disabled', async () => {
      // formData says booking is for someone else; reservations say guest IS the booker
      mockUseLocalStorage.mockReturnValue([
        {
          ...mockFormDetails,
          bookingForSomeoneElse: true,
          billing: {},
        },
        jest.fn(),
      ]);
      (useFeatureToggle as jest.Mock).mockReturnValue({
        [FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE]: false,
      });

      const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);
      await triggerConfirmBooking(getByTestId);

      await waitFor(() => {
        expect(mockMutationResponse.mutation.mutateAsync).toHaveBeenCalledWith(
          expect.objectContaining({
            // formData.bookingForSomeoneElse=true wins
            bookerIsNotGuest: true,
          })
        );
      });
    });

    it('should set isBillingAddressDisplayed from hotel brand and language when flag is enabled', async () => {
      getHotelInformationData.data.hotelInformation.brand = 'PID';
      mockCustomLocale.mockReturnValue({ language: 'de', country: 'de' });
      mockUseLocalStorage.mockReturnValue([{ ...mockFormDetails, billing: {} }, jest.fn()]);
      (useFeatureToggle as jest.Mock).mockReturnValue({
        [FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE]: true,
        [FT_CCUI_GDP_BILLING_ADDRESS]: true,
      });

      const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);

      await waitFor(() => {
        expect(getByTestId('paymentPageSection')).toBeInTheDocument();
      });

      // Reset brand after test
      getHotelInformationData.data.hotelInformation.brand = 'PI';
    });

    it('should not set isBillingAddressDisplayed for non-German hotel even when flag is enabled', async () => {
      getHotelInformationData.data.hotelInformation.brand = 'PI';
      mockCustomLocale.mockReturnValue({ language: 'de', country: 'de' });
      mockUseLocalStorage.mockReturnValue([{ ...mockFormDetails, billing: {} }, jest.fn()]);
      (useFeatureToggle as jest.Mock).mockReturnValue({
        [FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE]: true,
        [FT_CCUI_GDP_BILLING_ADDRESS]: true,
      });

      const { getByTestId } = render(<PaymentPageCcui {...mockProps} />);

      await waitFor(() => {
        expect(getByTestId('paymentPageSection')).toBeInTheDocument();
      });
    });
  });
});
