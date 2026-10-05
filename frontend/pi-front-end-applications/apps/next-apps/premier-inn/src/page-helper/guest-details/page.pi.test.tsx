import * as ReactQuery from '@tanstack/react-query';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { renderHook } from '@testing-library/react';
import {
  BRANDCODES,
  CREATE_RESERVATION_GUEST,
  MARKETING_CHANNEL,
  MARKETING_JOURNEY,
} from '@whitbread-eos/api';
import { Form } from '@whitbread-eos/atoms';
import {
  gbHotelInfo as gbHotelInfoOriginal,
  getBookingInformationData as getBookingInformationDataOriginal,
  getCountriesData as getCountriesDataOriginal,
  getHotelInformation as getHotelInformationOriginal,
  getPostCodeAddresesData as getPostCodeAddresesDataOriginal,
  getPostCodeAddresesInfoData as getPostCodeAddresesInfoDataOriginal,
  getRoomSelectionData as getRoomSelectionDataOriginal,
  singleBookingInformationData as singleBookingInformationDataOriginal,
  GLOBALS,
  isEmailValid,
  isStringValid,
  useCookieForABTesting,
  useFeatureToggle,
  getDefaultDataFromBooking,
} from '@whitbread-eos/utils';
import { clearGuestFormData } from '@whitbread-eos/utils/server';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

import {
  PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
  PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM,
} from '../../utils/pi-all-pages-constants';
import { act, fireEvent, render, userEvent, waitFor } from '../../utils/test-utils';
import GuestDetailsPage, {
  buildRegisterMarketingPreferencesPayload,
  getAnonymousNewsletterPreferences,
  getDefaultValuesFromFormState,
} from './page.pi';
import { guestDetailsFormConfig } from './piFormConfig/guestDetailsFormConfig';

// Mutable mock data - cloned from shared originals
let gbHotelInfo = JSON.parse(JSON.stringify(gbHotelInfoOriginal));
let getBookingInformationData = JSON.parse(JSON.stringify(getBookingInformationDataOriginal));
let getCountriesData = JSON.parse(JSON.stringify(getCountriesDataOriginal));
let getHotelInformation = JSON.parse(JSON.stringify(getHotelInformationOriginal));
let getPostCodeAddresesData = JSON.parse(JSON.stringify(getPostCodeAddresesDataOriginal));
let getPostCodeAddresesInfoData = JSON.parse(JSON.stringify(getPostCodeAddresesInfoDataOriginal));
let getRoomSelectionData = JSON.parse(JSON.stringify(getRoomSelectionDataOriginal));
let singleBookingInformationData = JSON.parse(JSON.stringify(singleBookingInformationDataOriginal));

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
const mockCustomLocale = jest.fn();
const queryClient = new ReactQuery.QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
      gcTime: 0,
    },
  },
});

const getNewsletterPreferencesData = {
  anonymousNewsletterPreferences: {
    optIn: true,
  },
};

const mockProps = {
  biQueryInput: {
    basketReference: '12',
    country: 'GB',
    language: 'en',
    bookingChannelCriteria: {
      channel: 'PI' as any,
      subchannel: 'WEB',
      language: 'en',
    },
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

const mockUseRouter = jest.fn();
const mockUserData = jest.fn();

const mockMutationResponse = {
  isError: false,
  error: { message: '' },
  mutation: {
    isLoading: false,
    data: {},
    mutate: jest.fn(),
  },
};

const mockUseMutationRequest = jest.fn();
const mockUseAuthToken = jest.fn<
  { token: string | undefined; isAuth0Enabled: boolean; isLoading: boolean },
  []
>(() => ({
  token: 'the-auth-token',
  isAuth0Enabled: false,
  isLoading: false,
}));
//endregion

//region Jest Mock
// Getter functions that return current mock values (using function declaration for hoisting)
function getMockData() {
  return {
    getBookingInformationData,
    getPackagesData,
    getHotelInformation,
    gbHotelInfo,
    getRoomSelectionData,
    getNewsletterPreferencesData,
    getCountriesData,
    getPostCodeAddresesData,
    getPostCodeAddresesInfoData,
  };
}

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  getNoOfDaysInYear: jest.fn(),
  SEO: () => <div></div>,
  // LeadGuestDetails: () => <div data-testid="LeadGuestDetails"></div>,
  Notice: () => <div data-testid="Notice"></div>,
}));

const mockFetchResponse = {
  data: {},
} as any;

const mockOkStatus = { value: true };

global.fetch = jest.fn(() =>
  Promise.resolve({
    json: () => Promise.resolve(mockFetchResponse),
    ok: mockOkStatus.value,
    headers: new Headers(),
    redirected: false,
    status: 200,
    statusText: 'OK',
    type: 'basic',
    url: '',
    body: null,
    bodyUsed: false,
  } as Response)
);

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
  invalidateQueries: (queryKey: unknown[]) => {
    if (!queryKey || !queryKey[0]) return { data: undefined, isLoading: false, isError: false };
    const mocks = getMockData();
    const key = queryKey[0];
    switch (key) {
      case 'GetBookingInformation':
        return mocks.getBookingInformationData || { data: undefined };
      case 'GetPackages':
        return mocks.getPackagesData || { data: undefined };
      case 'GetHotelInformation':
        return queryKey[2] === 'GBE'
          ? mocks.gbHotelInfo || { data: undefined }
          : mocks.getHotelInformation || { data: undefined };
      case 'GetRoomSelection':
        return mocks.getRoomSelectionData || { data: undefined };
      case 'getAnonymousNewsletterPreferences':
        return mocks.getNewsletterPreferencesData || { data: undefined };
      case 'GetCountries':
      case 'getCountriesWithNationality':
        return mocks.getCountriesData || { data: undefined };
      case 'addresses':
        return mocks.getPostCodeAddresesData || { data: undefined };
      case 'addresses-info':
        return mocks.getPostCodeAddresesInfoData || { data: undefined };
      default:
        return { data: undefined, isLoading: false, isError: false };
    }
  },
  useQuery: (queryKey: unknown[]) => {
    if (!queryKey || !queryKey[0]) return { data: undefined, isLoading: false, isError: false };
    const mocks = getMockData();
    const key = queryKey[0];
    switch (key) {
      case 'GetBookingInformation':
        return mocks.getBookingInformationData || { data: undefined };
      case 'GetPackages':
        return mocks.getPackagesData || { data: undefined };
      case 'GetHotelInformation':
        return queryKey[2] === 'GBE'
          ? mocks.gbHotelInfo || { data: undefined }
          : mocks.getHotelInformation || { data: undefined };
      case 'GetRoomSelection':
        return mocks.getRoomSelectionData || { data: undefined };
      case 'getAnonymousNewsletterPreferences':
        return mocks.getNewsletterPreferencesData || { data: undefined };
      case 'GetCountries':
      case 'getCountriesWithNationality':
        return mocks.getCountriesData || { data: undefined };
      case 'addresses':
        return mocks.getPostCodeAddresesData || { data: undefined };
      case 'addresses-info':
        return mocks.getPostCodeAddresesInfoData || { data: undefined };
      default:
        return { data: undefined, isLoading: false, isError: false };
    }
  },
  useQueryRequest: (queryKey: unknown[]) => {
    if (!queryKey || !queryKey[0]) return { data: undefined, isLoading: false, isError: false };
    const mocks = getMockData();
    const key = queryKey[0];
    switch (key) {
      case 'GetBookingInformation':
        return mocks.getBookingInformationData || { data: undefined };
      case 'GetPackages':
        return mocks.getPackagesData || { data: undefined };
      case 'GetHotelInformation':
        return queryKey[2] === 'GBE'
          ? mocks.gbHotelInfo || { data: undefined }
          : mocks.getHotelInformation || { data: undefined };
      case 'GetRoomSelection':
        return mocks.getRoomSelectionData || { data: undefined };
      case 'getAnonymousNewsletterPreferences':
        return mocks.getNewsletterPreferencesData || { data: undefined };
      case 'GetCountries':
      case 'getCountriesWithNationality':
        return mocks.getCountriesData || { data: undefined };
      case 'addresses':
        return mocks.getPostCodeAddresesData || { data: undefined };
      case 'addresses-info':
        return mocks.getPostCodeAddresesInfoData || { data: undefined };
      default:
        return { data: undefined, isLoading: false, isError: false };
    }
  },
  useCookieForABTesting: jest.fn(),
  useMutationRequest: (...args: unknown[]) => {
    mockUseMutationRequest(...args);
    return mockMutationResponse;
  },
  useAuthToken: () =>
    mockUseAuthToken() ?? { token: 'the-auth-token', isAuth0Enabled: false, isLoading: false },
  useRestMutationRequest: () => ({
    mutation: jest.fn(),
  }),
  usePackages: () => {
    const mocks = getMockData();
    return {
      ...mocks.getPackagesData?.data?.packages,
      ...mocks.getPackagesData,
    };
  },
  graphQLRequest: jest.fn().mockImplementation(() => {
    const mocks = getMockData();
    return mocks.getNewsletterPreferencesData;
  }),
  updateAncillariesAnalytics: () => jest.fn(),
  useUserData: () => mockUserData(),
  isStringValid: () => true,
  analytics: {
    update: jest.fn(),
  },
  isEmailValid: jest.fn(),
  useFeatureToggle: jest.fn().mockReturnValue({}),
  getDefaultDataFromBooking: jest.fn(),
}));

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    clearGuestFormData: jest.fn(),
  };
});

jest.mock('lodash', () => ({
  ...jest.requireActual('lodash'),
  debounce: jest.fn((fn) => fn),
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

// Function to reset mock data before each test
const resetMockData = () => {
  gbHotelInfo = JSON.parse(JSON.stringify(gbHotelInfoOriginal));
  getBookingInformationData = JSON.parse(JSON.stringify(getBookingInformationDataOriginal));
  getCountriesData = JSON.parse(JSON.stringify(getCountriesDataOriginal));
  getHotelInformation = JSON.parse(JSON.stringify(getHotelInformationOriginal));
  getPostCodeAddresesData = JSON.parse(JSON.stringify(getPostCodeAddresesDataOriginal));
  getPostCodeAddresesInfoData = JSON.parse(JSON.stringify(getPostCodeAddresesInfoDataOriginal));
  getRoomSelectionData = JSON.parse(JSON.stringify(getRoomSelectionDataOriginal));
  singleBookingInformationData = JSON.parse(JSON.stringify(singleBookingInformationDataOriginal));
};

//region Unit Tests
describe('GDP PI ', () => {
  beforeEach(() => {
    resetMockData(); // Reset mocks before each test
    (useCookieForABTesting as jest.Mock).mockReset().mockReturnValue(false);
    mockCustomLocale.mockReturnValue({
      language: 'gb',
      country: 'en',
    });
    mockUserData.mockReturnValue({ isLoggedIn: false });
    mockUseMutationRequest.mockClear();
  });

  it.skip('should render page pi guest details skeleton', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    getPackagesData.data.packages.hotelHasCityTaxForBusiness = true;
    getPackagesData.data.packages.hotelHasCityTaxForLeisure = false;
    mockMutationResponse.isError = false;

    const { getByTestId } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
  });

  it('should pass the resolved auth token into the create-reservation mutation', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockUseAuthToken.mockReturnValue({
      token: 'the-auth-token',
      isAuth0Enabled: false,
      isLoading: false,
    });

    render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    const createReservationCall = mockUseMutationRequest.mock.calls.find(
      ([gqlTemplateString]) => gqlTemplateString === CREATE_RESERVATION_GUEST
    );
    expect(createReservationCall?.[2]).toBe('the-auth-token');
  });

  it('should not pass a token into the create-reservation mutation while the auth token is still loading', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockUseAuthToken.mockReturnValue({
      token: undefined,
      isAuth0Enabled: true,
      isLoading: true,
    });

    render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    const createReservationCall = mockUseMutationRequest.mock.calls.find(
      ([gqlTemplateString]) => gqlTemplateString === CREATE_RESERVATION_GUEST
    );
    expect(createReservationCall?.[2]).toBeUndefined();
  });

  it('should render GDP PI booking summary', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    getPackagesData.data.packages.hotelHasCityTaxForBusiness = false;
    getPackagesData.data.packages.hotelHasCityTaxForLeisure = true;
    (useCookieForABTesting as jest.Mock).mockImplementation(() => {
      return true;
    });

    const { getByTestId, getByText } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

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

  it('should render GDP PI and have continue button', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    const { getByTestId } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    const button = getByTestId('BookingSummary-ContinueButton');

    expect(button).toBeInTheDocument();

    await userEvent.click(button);
  });

  it.skip('should render GDP PI and have continue button and click it', async () => {
    const mockRouter = {
      query: jest.fn(),
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const { getByTestId, findByLabelText, getByLabelText, getByText, queryByText, container } =
      render(
        <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();

    const radioOptionLeisure = await findByLabelText('booking.reason.leisure');
    const radioOptionBusiness = getByLabelText('booking.reason.business');

    await userEvent.click(radioOptionLeisure);
    expect(radioOptionLeisure).toBeChecked();
    expect(radioOptionBusiness).not.toBeChecked();

    // DNRQ-58765 - verify the I am booking for myself faux tabs are not visible (Single Booking redesign changes)
    const bookingMyselfMockTab = queryByText('booking.guestDetails.iAmBookingForMyself');
    const bookingSomeoneelseMockTab = queryByText('booking.guestDetails.iAmBookingForSomeoneElse');
    expect(bookingMyselfMockTab).not.toBeInTheDocument();
    expect(bookingSomeoneelseMockTab).not.toBeInTheDocument();

    const dropdownToggle = getByTestId('GuestDetails-Title');
    const selector = container.querySelector('[aria-haspopup="menu"]');

    await userEvent.click(dropdownToggle);

    const dropdownOptionTitle = getByTestId('DropdownComp-GuestDetails-Title-InnerDropdown-0');
    await userEvent.click(dropdownOptionTitle);

    expect(selector).toBeInTheDocument();

    const inputFirstName = getByTestId('input-firstName');
    fireEvent.change(inputFirstName, { target: { value: 'Stefan' } });

    const inputLastName = getByTestId('input-lastName');
    fireEvent.change(inputLastName, { target: { value: 'Ciora' } });

    const inputEmail = getByTestId('input-email');
    fireEvent.change(inputEmail, { target: { value: 'stefan.ciora@whitbread.com' } });

    const phoneNumber = '7777777';

    const inputPhone = getByTestId('GuestDetails-Mobile-phoneNumber');
    fireEvent.change(inputPhone, { target: { value: phoneNumber } });
    expect(inputPhone).toBeInTheDocument();
    expect(inputPhone).toHaveValue(phoneNumber);

    const inputLandLine = getByTestId('GuestDetails-Landline-phoneNumber');
    fireEvent.change(inputLandLine, { target: { value: phoneNumber } });
    expect(inputLandLine).toBeInTheDocument();
    expect(inputLandLine).toHaveValue(phoneNumber);

    const manualAddress = getByText('booking.enterManuallAddress');
    expect(manualAddress).toBeInTheDocument();
    await userEvent.click(manualAddress);

    const radioOptionHomeAddress = getByTestId('GuestDetails-AddressSelection-PersonalAddress');
    expect(radioOptionHomeAddress).toBeInTheDocument();
    await userEvent.click(radioOptionHomeAddress);

    const inputAddressLine1 = getByTestId('input-addressLine1');
    expect(inputAddressLine1).toBeInTheDocument();
    fireEvent.change(inputAddressLine1, { target: { value: 'Street 22 December' } });

    const inputPostAlCode = getByTestId('input-postalCode');
    expect(inputPostAlCode).toBeInTheDocument();
    const postCode = 'CR7 7EZ' as string;
    fireEvent.change(inputPostAlCode, { target: { value: postCode } });
    expect(inputPostAlCode).toHaveValue(postCode);

    ///
    const button = getByTestId('BookingSummary-ContinueButton');

    await userEvent.click(button);
  });

  it('should render page with info message', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getBookingInformationData.data.bookingInformation.infoMessages.push('TestInfoMess');
    const { queryByText } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(queryByText('TestInfoMess')).not.toBeInTheDocument();
  });

  it('should render GDP PI with loading true', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getBookingInformationData.isLoading = true;

    const { getByText } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByText('booking.loading')).toBeInTheDocument();
    getBookingInformationData.isLoading = false;
  });

  it('should render GDP PI with get booking information error', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getBookingInformationData.isError = true;
    const { getByText } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByText('Error on getting booking information....')).toBeInTheDocument();
    expect(getByText('error booking information')).toBeInTheDocument();
    getBookingInformationData.isError = false;
  });

  it('should render GDP PI with get packages  error', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getPackagesData.isError = true;
    const { getByText } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByText('Error on getting packages....')).toBeInTheDocument();
    expect(getByText('error packages')).toBeInTheDocument();
    getPackagesData.isError = false;
  });

  it('should render GDP PI with get hotel information error', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getHotelInformation.isError = true;
    const { getByText } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByText('Error on getting hotel information....')).toBeInTheDocument();
    expect(getByText('error hotel information')).toBeInTheDocument();
    getHotelInformation.isError = false;
  });

  it('should render GDP PI skeleton with hotelDetails undefined', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getHotelInformation.data = undefined;
    const { getByTestId } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
  });

  it('should render GDP PI skeleton with room selection undefined', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getRoomSelectionData.data = undefined;
    const { getByTestId } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
  });

  it.skip('should render GDP PI skeleton with meals and booking info undefined', async () => {
    const mockRouter = {
      query: jest.fn(),
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    (getPackagesData as any).data = undefined;
    getRoomSelectionData.data = undefined;

    const { getByText, getByTestId, getByLabelText, container } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();

    const radioOptionLeisure = getByLabelText('booking.reason.leisure');
    const radioOptionBusiness = getByLabelText('booking.reason.business');

    await userEvent.click(radioOptionLeisure);
    expect(radioOptionLeisure).toBeChecked();
    expect(radioOptionBusiness).not.toBeChecked();

    const dropdownToggle = getByTestId('GuestDetails-Title');

    const selector = container.querySelector('[aria-haspopup="menu"]');

    await userEvent.click(dropdownToggle);

    const dropdownOptionTitle = getByTestId('DropdownComp-GuestDetails-Title-InnerDropdown-0');
    await userEvent.click(dropdownOptionTitle);

    expect(selector).toBeInTheDocument();

    const inputFirstName = getByTestId('input-firstName');
    fireEvent.change(inputFirstName, { target: { value: 'Stefan' } });

    const inputLastName = getByTestId('input-lastName');
    fireEvent.change(inputLastName, { target: { value: 'Ciora' } });

    const inputEmail = getByTestId('input-email');
    fireEvent.change(inputEmail, { target: { value: 'stefan.ciora@whitbread.com' } });

    const phoneNumber = '7777777' as string;

    const inputPhone = getByTestId('GuestDetails-Mobile-phoneNumber');
    fireEvent.change(inputPhone, { target: { value: phoneNumber } });

    const inputLandLine = getByTestId('GuestDetails-Landline-phoneNumber');
    fireEvent.change(inputLandLine, { target: { value: phoneNumber } });

    const manualAddress = getByText('booking.enterManuallAddress');

    await userEvent.click(manualAddress);

    const radioOptionHomeAddress = getByTestId('GuestDetails-AddressSelection-PersonalAddress');

    await userEvent.click(radioOptionHomeAddress);

    const inputAddressLine1 = getByTestId('input-addressLine1');

    fireEvent.change(inputAddressLine1, { target: { value: 'Street 22 December' } });
    const inputPostAlCode = getByTestId('input-postalCode');

    const postCode = 'CR7 7EZ' as string;
    fireEvent.change(inputPostAlCode, { target: { value: postCode } });
    expect(inputPostAlCode).toHaveValue(postCode);

    const button = getByTestId('GuestDetails-Submit');

    await userEvent.click(button);
  });

  it('should render GDP PI skeleton with meals and booking info undefined DE', async () => {
    const mockRouter = {
      query: jest.fn(),
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockCustomLocale.mockReturnValue({
      language: 'de',
      country: 'de',
      currentLang: 'de',
    });

    (getPackagesData as any).data = undefined;
    getRoomSelectionData.data = undefined;
    const { getByTestId, getByLabelText, container } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();

    const radioOptionLeisure = getByLabelText('booking.reason.leisure');
    const radioOptionBusiness = getByLabelText('booking.reason.business');

    await userEvent.click(radioOptionLeisure);
    expect(radioOptionLeisure).toBeChecked();
    expect(radioOptionBusiness).not.toBeChecked();

    const dropdownToggle = getByTestId('GuestDetails-Title');

    const selector = container.querySelector('[aria-haspopup="menu"]');

    await userEvent.click(dropdownToggle);

    const dropdownOptionTitle = getByTestId('DropdownComp-GuestDetails-Title-InnerDropdown-0');
    dropdownOptionTitle.click();

    expect(selector).toBeInTheDocument();

    const inputFirstName = getByTestId('input-firstName');
    fireEvent.change(inputFirstName, { target: { value: 'Stefan' } });

    const inputLastName = getByTestId('input-lastName');
    fireEvent.change(inputLastName, { target: { value: 'Ciora' } });

    const inputEmail = getByTestId('input-email');
    fireEvent.change(inputEmail, { target: { value: 'stefan.ciora@whitbread.com' } });

    const phoneNumber = '7777777' as string;

    const inputPhone = getByTestId('GuestDetails-Mobile-phoneNumber');
    fireEvent.change(inputPhone, { target: { value: phoneNumber } });

    const inputLandLine = getByTestId('GuestDetails-Landline-phoneNumber');
    fireEvent.change(inputLandLine, { target: { value: phoneNumber } });

    const manualAddress = getByTestId('GuestDetails-ManualAddressToggle');
    expect(manualAddress).toBeInTheDocument();
    await userEvent.click(manualAddress);

    const radioOptionHomeAddress = getByTestId('GuestDetails-AddressSelection-PersonalAddress');
    expect(radioOptionHomeAddress).toBeInTheDocument();
    await userEvent.click(radioOptionHomeAddress);

    const inputAddressLine1 = getByTestId('input-addressLine1');
    expect(inputAddressLine1).toBeInTheDocument();
    fireEvent.change(inputAddressLine1, { target: { value: 'Street 22 December' } });

    const inputCityName = getByTestId('input-cityName');
    expect(inputCityName).toBeInTheDocument();
    fireEvent.change(inputCityName, { target: { value: 'London' } });

    const inputPostAlCode = getByTestId('input-postalCode');

    const postCode = 'CR7 7EZ' as string;
    fireEvent.change(inputPostAlCode, { target: { value: postCode } });
    expect(inputPostAlCode).toHaveValue(postCode);

    const button = getByTestId('GuestDetails-Submit');

    await userEvent.click(button);
  });
  it('should show error when rendering because of RFST', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockMutationResponse.isError = true;

    const { getByTestId } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
  });
});
//endregion

describe('GDP PI marketing section', () => {
  beforeEach(() => {
    resetMockData();
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
    mockUserData.mockReturnValue({ isLoggedIn: false });
  });

  it('should render marketing section', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockMutationResponse.isError = false;

    const { getByTestId } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-AcceptFutureMailing')).toBeInTheDocument();
  });

  it('should render updateProfileConsent checkbox and privacy notice for a signed-in user', async () => {
    const mockRouter = {
      query: jest.fn(),
    };

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_auth0_login: true,
    });
    mockUserData.mockReturnValue({ isLoggedIn: true });

    const { getAllByTestId, getByTestId } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    // FormCheckbox renders the testid on both the outer wrapper and inner box
    expect(getAllByTestId('GuestDetails-UpdateProfileConsent')[0]).toBeInTheDocument();
    expect(getByTestId('GuestDetails-UpdateProfileConsentNotice')).toBeInTheDocument();
  });

  it('should not render updateProfileConsent section when auth0 is on but the user is not signed in', async () => {
    const mockRouter = {
      query: jest.fn(),
    };

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_auth0_login: true,
    });
    mockUserData.mockReturnValue({ isLoggedIn: false });

    const { queryByTestId } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(queryByTestId('GuestDetails-UpdateProfileConsent')).not.toBeInTheDocument();
    expect(queryByTestId('GuestDetails-UpdateProfileConsentNotice')).not.toBeInTheDocument();
  });

  it('should not render updateProfileConsent section when auth0 flag is disabled', async () => {
    const mockRouter = {
      query: jest.fn(),
    };

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_auth0_login: false,
    });

    const { queryByTestId } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(queryByTestId('GuestDetails-UpdateProfileConsent')).not.toBeInTheDocument();
    expect(queryByTestId('GuestDetails-UpdateProfileConsentNotice')).not.toBeInTheDocument();
  });

  it('should not render updateProfileConsent section for a legacy-logged-in user (auth0 flag off)', async () => {
    const mockRouter = {
      query: jest.fn(),
    };

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_auth0_login: false,
    });
    mockUserData.mockReturnValue({ isLoggedIn: true });

    const { queryByTestId } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(queryByTestId('GuestDetails-UpdateProfileConsent')).not.toBeInTheDocument();
    expect(queryByTestId('GuestDetails-UpdateProfileConsentNotice')).not.toBeInTheDocument();
  });

  it('should not render marketing section if email already subscribed', async () => {
    const mockRouter = {
      query: jest.fn(),
    };

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockMutationResponse.isError = false;

    const { getByTestId } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();

    const inputEmail = getByTestId('input-email');
    fireEvent.change(inputEmail, { target: { value: 'john.dooee@whitbread.com' } });

    const button = getByTestId('GuestDetails-Submit');

    await userEvent.click(button);
  });
});

const getFormState = jest.fn();
const onSubmit = jest.fn();
const goBack = jest.fn();
const setIsLocationRequired = jest.fn();
const t = jest.fn();
const basketReferenceId = 'GAA1828883';
const baseDataTestId = 'GuestDetails';
const mockGetTypographyProps = jest.fn((_, semanticTypography) => semanticTypography);

const defaultValues = {
  reasonForStay: '',
  title: '',
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  landline: '',
  companyName: '',
  addressLine1: '',
  addressLine2: '',
  addressLine3: '',
  addressLine4: '',
  postalCode: '',
  manualAddressToggle: '',
  cityName: '',
  postcodeAddress: '',
  addressSelection: '',
  countryCode: 'GB',
  acceptFutureMailing: 'en',
  whoBookerIsTabs: 'MYSELF',
  billingAddressCheckbox: false,
};

describe('GDP PI Form component - Single Booking Redesign', () => {
  beforeEach(() => {
    resetMockData();
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
    mockUserData.mockReturnValue({ isLoggedIn: false });
  });

  afterEach(() => {
    jest.clearAllMocks();
    queryClient.clear();
  });

  const SingleBookingComponent = ({
    isBookingForSomeoneElse,
    currentLang,
    isGermanHotel,
    hotelBrand,
    isAdditionalInformationEnabled,
    isAccompanyingOn,
    hideMarketingPreferences,
  }: {
    isBookingForSomeoneElse: boolean;
    isAdditionalInformationEnabled?: boolean;
    currentLang: string;
    isGermanHotel: boolean;
    hotelBrand: string;
    isAccompanyingOn?: boolean;
    hideMarketingPreferences?: boolean;
  }) => {
    return (
      <QueryClientProvider client={new QueryClient()}>
        <Form
          data-testid="guestDetailsForm"
          {...guestDetailsFormConfig({
            getTypographyProps: mockGetTypographyProps,
            getFormState,
            defaultValues,
            onSubmit,
            baseDataTestId,
            t: (key: string) => {
              switch (key) {
                case 'booking.guestDetails.iAmBookingForMyself':
                  return 'booking.guestDetails.iAmBookingForMyself';
                case 'booking.guestDetails.iAmBookingForSomeoneElse':
                  return 'booking.guestDetails.iAmBookingForSomeoneElse';
                case 'booking.guestDetails.personDetails':
                  return 'booking.guestDetails.personDetails';
                case 'booking.contactDetails.guestDetails':
                  return 'booking.contactDetails.guestDetails';
                default:
                  return 'default';
              }
            },
            currentLang,
            basketReferenceId,
            resetForm: 0,
            brand: 'PID',
            bkndData: {
              hiData: getBookingInformationData.data.bookingInformation,
              rooms: singleBookingInformationData.reservationByIdList,
            },
            goBack,
            cityTaxMessages: {
              mainBanner: 'mainBanner',
              secondaryBanner: 'string',
              summaryText: 'string',
            },
            updateReasonForStay: jest.fn(),
            isLocationRequired: false,
            setIsLocationRequired,
            hotelBrand,
            isRegisterSelected: false,
            isSingleRoomRedesignEnabled: true,
            isMultiRoomRedesignEnabled: true,
            isBookingForSomeoneElse,
            setIsBookingForSomeoneElse: mockSetIsBookingForSomeoneElse,
            isGermanHotel,
            isBillingAddressEnabled: false,
            isAdditionalInformationEnabled,
            showCheckInInfo: {},
            setShowCheckInInfo: jest.fn(),
            shouldAskForAccompanyingGuest: isAccompanyingOn,
            suppressMarketingCheckbox: hideMarketingPreferences ?? false,
            isRemovePIIDataFromLocalStorageEnabled: false,
          })}
        />
      </QueryClientProvider>
    );
  };

  const mockSetIsBookingForSomeoneElse = jest.fn();

  it('should render page pi guest details with Booking for Tabs for Single booking - myself ', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockMutationResponse.isError = false;
    const { getByTestId, queryByTestId, queryByText, getByRole, rerender } = render(
      <SingleBookingComponent
        isBookingForSomeoneElse={false}
        currentLang={'en'}
        isGermanHotel={false}
        hotelBrand={'PI'}
      />
    );

    expect(getByTestId('GuestDetails-whoBookerIsTabs')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-whoBookerIsTabs-Myself')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-whoBookerIsTabs-Someoneelse')).toBeInTheDocument();

    expect(queryByTestId('GuestDetails-singleBookingBookerDetailsHeading')).not.toBeInTheDocument();
    expect(queryByTestId('GuestDetails-singleBookingGuestDetailsHeading')).toBeInTheDocument();

    const addressToggle = getByTestId('GuestDetails-ManualAddressToggle');
    expect(addressToggle).toBeInTheDocument();

    const myselfTab = getByRole('radio', {
      name: /booking.guestDetails.iAmBookingForMyself/i,
    });
    const someoneElseTab = getByRole('radio', {
      name: /booking.guestDetails.iAmBookingForSomeoneElse/i,
    });

    expect(myselfTab).toBeInTheDocument();
    expect(someoneElseTab).toBeInTheDocument();

    await userEvent.click(someoneElseTab);
    expect(someoneElseTab).toBeChecked();
    expect(mockSetIsBookingForSomeoneElse).toHaveBeenCalledTimes(1);

    rerender(
      <SingleBookingComponent
        isBookingForSomeoneElse={true}
        currentLang={'en'}
        isGermanHotel={false}
        hotelBrand={'PI'}
      />
    );

    await waitFor(() => {
      // Details of the person making the booking - shown
      expect(queryByText('booking.guestDetails.personDetails')).toBeTruthy();
      expect(queryByText('booking.guestDetails.personDetails')).toBeVisible();
    });

    await userEvent.click(myselfTab);
    expect(myselfTab).toBeChecked();
    expect(mockSetIsBookingForSomeoneElse).toHaveBeenCalledTimes(2);
    rerender(
      <SingleBookingComponent
        isBookingForSomeoneElse={false}
        currentLang={'en'}
        isGermanHotel={false}
        hotelBrand={'PI'}
      />
    );

    await waitFor(() => {
      // Details of the person making the booking - not to be shown
      expect(queryByText('booking.guestDetails.personDetails')).toBeFalsy();
    });

    rerender(
      <SingleBookingComponent
        isBookingForSomeoneElse={false}
        isAdditionalInformationEnabled={true}
        currentLang={'en'}
        isGermanHotel={false}
        hotelBrand={'PI'}
      />
    );
    const button = getByTestId('GuestDetails-AdditionalInformation-CheckinButton');

    await userEvent.click(button);
    expect(queryByText('precheckin.additionalfields.dateofbirth')).toBeTruthy();
    await waitFor(() => {
      expect(queryByTestId('GuestDetails-AdditionalInformation--label')).toBeTruthy();
      expect(queryByText('precheckin.details.passport')).not.toBeTruthy(); //initially passport field will be hidden
    });
  });

  it('should render page pi guest details without Booking for Tabs for Multiple booking ', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    const MultiRoomBookingComponent = () => {
      return (
        <QueryClientProvider client={new QueryClient()}>
          <Form
            {...guestDetailsFormConfig({
              getTypographyProps: mockGetTypographyProps,
              getFormState,
              defaultValues,
              onSubmit,
              baseDataTestId,
              t,
              currentLang: 'de',
              basketReferenceId,
              resetForm: 0,
              brand: 'PI',
              bkndData: {
                hiData: getBookingInformationData.data.bookingInformation,
                rooms: getBookingInformationData.data.bookingInformation.reservationByIdList, // 2 rooms
              },
              goBack,
              cityTaxMessages: {
                mainBanner: 'mainBanner',
                secondaryBanner: 'string',
                summaryText: 'string',
              },
              updateReasonForStay: jest.fn(),
              isLocationRequired: false,
              setIsLocationRequired,
              hotelBrand: 'PID',
              isRegisterSelected: false,
              isSingleRoomRedesignEnabled: true,
              isMultiRoomRedesignEnabled: true,
              isBookingForSomeoneElse: false,
              setIsBookingForSomeoneElse: jest.fn(),
              isGermanHotel: true,
              isBillingAddressEnabled: false,
              showCheckInInfo: {},
              setShowCheckInInfo: jest.fn(),
              isRemovePIIDataFromLocalStorageEnabled: false,
            })}
          />
        </QueryClientProvider>
      );
    };
    mockMutationResponse.isError = false;
    const { queryByTestId } = render(<MultiRoomBookingComponent />);

    expect(queryByTestId('GuestDetails-whoBookerIsTabs')).not.toBeInTheDocument();
    expect(queryByTestId('GuestDetails-singleBookingDetailsHeading')).not.toBeInTheDocument();
  });

  it('should include accompanying guest details for multiple bookings', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    const submitFn = jest.fn();
    const MultiRoomBookingComponent = () => {
      return (
        <QueryClientProvider client={new QueryClient()}>
          <Form
            {...guestDetailsFormConfig({
              getTypographyProps: mockGetTypographyProps,
              getFormState,
              defaultValues,
              onSubmit: submitFn,
              baseDataTestId,
              t,
              currentLang: 'de',
              basketReferenceId,
              resetForm: 0,
              brand: 'PI',
              bkndData: {
                hiData: getBookingInformationData.data.bookingInformation,
                rooms: getBookingInformationData.data.bookingInformation.reservationByIdList, // 2 rooms
              },
              goBack,
              cityTaxMessages: {
                mainBanner: 'mainBanner',
                secondaryBanner: 'string',
                summaryText: 'string',
              },
              updateReasonForStay: jest.fn(),
              isLocationRequired: false,
              setIsLocationRequired,
              hotelBrand: 'PID',
              isRegisterSelected: false,
              isSingleRoomRedesignEnabled: true,
              isMultiRoomRedesignEnabled: true,
              isBookingForSomeoneElse: false,
              setIsBookingForSomeoneElse: jest.fn(),
              isGermanHotel: false,
              isBillingAddressEnabled: false,
              showCheckInInfo: {},
              setShowCheckInInfo: jest.fn(),
              shouldAskForAccompanyingGuest: true,
              isRemovePIIDataFromLocalStorageEnabled: false,
            })}
          />
        </QueryClientProvider>
      );
    };
    mockMutationResponse.isError = false;
    const { getByTestId, container, getByText } = render(<MultiRoomBookingComponent />);

    expect(getByTestId('GuestDetails-leadGuest-AccompanyingContainer')).toBeVisible();

    const dropdownToggle = getByTestId('GuestDetails-Title');

    const selector = container.querySelector('[aria-haspopup="menu"]');

    await userEvent.click(dropdownToggle);

    const dropdownOptionTitle = getByTestId('DropdownComp-GuestDetails-Title-InnerDropdown-0');
    dropdownOptionTitle.click();

    expect(selector).toBeInTheDocument();

    const inputFirstName = getByTestId('input-firstName');
    fireEvent.change(inputFirstName, { target: { value: 'Stefan' } });

    const inputLastName = getByTestId('input-lastName');
    fireEvent.change(inputLastName, { target: { value: 'Ciora' } });

    const inputEmail = getByTestId('input-email');
    fireEvent.change(inputEmail, { target: { value: 'stefan.ciora@whitbread.com' } });

    const phoneNumber = '+40728954441' as string;

    const inputPhone = getByTestId('GuestDetails-Mobile-phoneNumber');
    fireEvent.change(inputPhone, { target: { value: phoneNumber } });

    const countrySelector = getByTestId('GuestDetails-Mobile-countrySelector');
    await userEvent.click(countrySelector);

    const country = getByText('United Kingdom (the)');
    await userEvent.click(country);

    const button = getByTestId('GuestDetails-Submit');

    await userEvent.click(button);
  });
});

describe('PI Form component when user is logged in', () => {
  beforeEach(() => {
    resetMockData();
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
    mockUserData.mockReturnValue({ isLoggedIn: true });
  });
  afterEach(() => {
    jest.clearAllMocks();
    queryClient.clear();
  });

  it('should render page pi guest details page when user is logged in', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockMutationResponse.isError = false;

    const { getByTestId, getByText } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
    expect(getByText('booking.login.greeting')).toBeInTheDocument();
  });

  it.skip('should render page pi guest details skeleton', async () => {
    const mockRouter = {
      query: jest.fn(),
    };

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockUserData.mockReturnValue({ isLoggedIn: true });
    mockMutationResponse.isError = false;
    const tmpMockProps = JSON.parse(JSON.stringify(mockProps));
    tmpMockProps.hiQueryInput.country = 'GBE';
    const { getByTestId, container, getByText, findByRole, getByRole } = render(
      <GuestDetailsPage {...tmpMockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
    expect(getByText('booking.login.greeting')).toBeInTheDocument();

    const user = userEvent.setup();
    const radioOptionLeisure = await findByRole('radio', { name: 'booking.reason.leisure' });
    const radioOptionBusiness = getByRole('radio', { name: 'booking.reason.business' });
    await user.click(radioOptionLeisure);
    expect(radioOptionLeisure).toBeChecked();
    expect(radioOptionBusiness).not.toBeChecked();

    const dropdownToggle = getByTestId('GuestDetails-Title');

    const selector = container.querySelector('[aria-haspopup="menu"]');

    await userEvent.click(dropdownToggle);

    const dropdownOptionTitle = getByTestId('DropdownComp-GuestDetails-Title-InnerDropdown-0');
    dropdownOptionTitle.click();

    expect(selector).toBeInTheDocument();

    const inputFirstName = getByTestId('input-firstName');
    fireEvent.change(inputFirstName, { target: { value: 'Stefan' } });

    const inputLastName = getByTestId('input-lastName');
    fireEvent.change(inputLastName, { target: { value: 'Ciora' } });

    const inputEmail = getByTestId('input-email');
    fireEvent.change(inputEmail, { target: { value: 'stefan.ciora@whitbread.com' } });

    const phoneNumber = '+40728954441' as string;

    const inputPhone = getByTestId('GuestDetails-Mobile-phoneNumber');
    fireEvent.change(inputPhone, { target: { value: phoneNumber } });

    const countrySelector = getByTestId('GuestDetails-Mobile-countrySelector');
    await userEvent.click(countrySelector);

    const country = getByText('United Kingdom (the)');
    await userEvent.click(country);

    const manualAddress = getByText('booking.enterManuallAddress');

    await userEvent.click(manualAddress);

    const radioOptionHomeAddress = getByTestId('GuestDetails-AddressSelection-PersonalAddress');

    await userEvent.click(radioOptionHomeAddress);

    const inputAddressLine1 = getByTestId('input-addressLine1');
    fireEvent.focus(inputAddressLine1);
    fireEvent.change(inputAddressLine1, { target: { value: 'Street 22 December' } });

    const inputPostAlCode = getByTestId('input-postalCode');

    const postCode = '10176';
    fireEvent.change(inputPostAlCode, { target: { value: postCode } });
    expect(inputPostAlCode).toHaveValue(postCode);

    const findAddressBtn = getByTestId('PostcodeAddress-FindAddressBtn');

    await userEvent.click(findAddressBtn);

    const button = getByTestId('BookingSummary-ContinueButton');

    await userEvent.click(button);
  });
});
describe('setRegisterSectionSelected', () => {
  it('should set isRegisterSelected to true when selected is true', () => {
    const { result } = renderHook(() => {
      const [isRegisterSelected, setIsRegisterSelected] = useState(false);
      const setRegisterSectionSelected = (selected: boolean) => {
        setIsRegisterSelected(selected);
      };
      return { isRegisterSelected, setRegisterSectionSelected };
    });

    act(() => {
      result.current.setRegisterSectionSelected(true);
    });

    expect(result.current.isRegisterSelected).toBe(true);
  });

  it('should set isRegisterSelected to false when selected is false', () => {
    const { result } = renderHook(() => {
      const [isRegisterSelected, setIsRegisterSelected] = useState(true);
      const setRegisterSectionSelected = (selected: boolean) => {
        setIsRegisterSelected(selected);
      };
      return { isRegisterSelected, setRegisterSectionSelected };
    });

    act(() => {
      result.current.setRegisterSectionSelected(false);
    });

    expect(result.current.isRegisterSelected).toBe(false);
  });
});

describe('useEffect for bkngSuccess', () => {
  let setUpdatingTotalCostMock: jest.Mock;

  beforeEach(() => {
    resetMockData();
    setUpdatingTotalCostMock = jest.fn();
  });

  const MockComponent = ({ bkngSuccess, bkngData }: { bkngSuccess: boolean; bkngData: any }) => {
    const [updatingTotalCost, setUpdatingTotalCost] = useState(true);

    useEffect(() => {
      if (bkngSuccess) {
        setUpdatingTotalCost(false);
      }
    }, [bkngData]);

    useEffect(() => {
      setUpdatingTotalCostMock(updatingTotalCost);
    }, [updatingTotalCost]);

    return null;
  };

  it('should set updatingTotalCost to false when bkngSuccess is true', () => {
    render(<MockComponent bkngSuccess={true} bkngData={{}} />);

    expect(setUpdatingTotalCostMock).toHaveBeenCalledWith(false);
  });

  it('should not change updatingTotalCost when bkngSuccess is false', () => {
    render(<MockComponent bkngSuccess={false} bkngData={{}} />);

    expect(setUpdatingTotalCostMock).toHaveBeenCalledWith(true);
  });
});

describe('useEffect for svBknData', () => {
  const pushMock = jest.fn();
  const mockRouter = {
    query: jest.fn(),
    push: pushMock,
  };

  beforeEach(() => {
    resetMockData();
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
  });

  afterEach(() => {
    jest.clearAllMocks();
    queryClient.clear();
  });

  const MockComponent = ({
    svBknData,
    svBknIsError,
    svBknIsSuccess,
    currentCountry,
    currentLang,
    bkngData,
  }: {
    svBknData: any;
    svBknIsError: boolean;
    svBknIsSuccess: boolean;
    currentCountry: string;
    currentLang: string;
    bkngData: any;
  }) => {
    const router = useRouter();

    useEffect(() => {
      if (
        !svBknIsError &&
        svBknIsSuccess &&
        svBknData &&
        svBknData.createReservationGuest &&
        isStringValid(svBknData.createReservationGuest.basketReference)
      ) {
        const paymentSearchParams = new URLSearchParams({
          reservationId: svBknData.createReservationGuest.basketReference,
          [PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM]: PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
        });

        router.push(
          `/${currentCountry}/${currentLang}${
            bkngData?.bookingInformation?.bookingFlowId
              ? `/${bkngData?.bookingInformation?.bookingFlowId}`
              : ''
          }/payment?${paymentSearchParams.toString()}`
        );
      }
    }, [svBknData, svBknIsError, svBknIsSuccess, router]);

    return null;
  };

  it('should mark payment navigation as coming from guest details when reservation guest is saved', () => {
    const svBknData = {
      createReservationGuest: {
        basketReference: 'test-basket-reference',
      },
    };
    const svBknIsError = false;
    const svBknIsSuccess = true;
    const currentCountry = 'GB';
    const currentLang = 'en';
    const bkngData = {
      bookingInformation: {
        bookingFlowId: 'test-flow-id',
      },
    };
    const expectedPaymentSearchParams = new URLSearchParams({
      reservationId: svBknData.createReservationGuest.basketReference,
      [PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM]: PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
    });

    render(
      <MockComponent
        svBknData={svBknData}
        svBknIsError={svBknIsError}
        svBknIsSuccess={svBknIsSuccess}
        currentCountry={currentCountry}
        currentLang={currentLang}
        bkngData={bkngData}
      />
    );

    expect(pushMock).toHaveBeenCalledWith(
      `/GB/en/test-flow-id/payment?${expectedPaymentSearchParams.toString()}`
    );
  });

  it('should not call router.push when svBknIsError is true', () => {
    const svBknData = {
      createReservationGuest: {
        basketReference: 'test-basket-reference',
      },
    };
    const svBknIsError = true;
    const svBknIsSuccess = true;
    const currentCountry = 'GB';
    const currentLang = 'en';
    const bkngData = {
      bookingInformation: {
        bookingFlowId: 'test-flow-id',
      },
    };

    render(
      <MockComponent
        svBknData={svBknData}
        svBknIsError={svBknIsError}
        svBknIsSuccess={svBknIsSuccess}
        currentCountry={currentCountry}
        currentLang={currentLang}
        bkngData={bkngData}
      />
    );

    expect(pushMock).not.toHaveBeenCalled();
  });
});

describe('useEffect for marketingPreferences', () => {
  const marketingPreferencesMock = jest.fn();

  const MockComponent = ({
    registerIsError,
    registerIsSuccess,
    registerData,
  }: {
    registerIsError: boolean;
    registerIsSuccess: boolean;
    registerData: any;
  }) => {
    useEffect(() => {
      if (!registerIsError && registerIsSuccess && registerData) {
        marketingPreferencesMock();
      }
    }, [registerData, registerIsError, registerIsSuccess]);

    return null;
  };

  afterEach(() => {
    jest.clearAllMocks();
    queryClient.clear();
  });

  it('should call marketingPreferences when conditions are met', () => {
    render(
      <MockComponent
        registerIsError={false}
        registerIsSuccess={true}
        registerData={{ someData: 'test' }}
      />
    );

    expect(marketingPreferencesMock).toHaveBeenCalledTimes(1);
  });

  it('should not call marketingPreferences when registerIsError is true', () => {
    render(
      <MockComponent
        registerIsError={true}
        registerIsSuccess={true}
        registerData={{ someData: 'test' }}
      />
    );

    expect(marketingPreferencesMock).not.toHaveBeenCalled();
  });

  it('should not call marketingPreferences when registerIsSuccess is false', () => {
    render(
      <MockComponent
        registerIsError={false}
        registerIsSuccess={false}
        registerData={{ someData: 'test' }}
      />
    );

    expect(marketingPreferencesMock).not.toHaveBeenCalled();
  });

  it('should not call marketingPreferences when registerData is null', () => {
    render(<MockComponent registerIsError={false} registerIsSuccess={true} registerData={null} />);

    expect(marketingPreferencesMock).not.toHaveBeenCalled();
  });
});

describe('getLeadGuestFormat', () => {
  const mockFormatAdditionalInformation = jest.fn();
  const mockFormatAccompanyingGuest = jest.fn();

  beforeEach(() => {
    resetMockData();
    jest.resetAllMocks();
  });

  const getLeadGuestFormat = (data: any, item: any) => {
    const {
      title,
      firstName,
      lastName,
      countryCode,
      addressLine1,
      addressLine2,
      addressLine3,
      addressLine4,
      postcodeAddress,
    } = data;
    const {
      title: itemTitle,
      firstName: itemFirstName,
      lastName: itemLastName,
      countryCode: itemCountryCode,
      addressLine1: itemAddressLine1,
      addressLine2: itemAddressLine2,
      addressLine3: itemAddressLine3,
      addressLine4: itemAddressLine4,
      postcodeAddress: itemPostcodeAddress,
      stayInThisRoom,
    } = item;

    const addressCountryCode = stayInThisRoom ? countryCode : itemCountryCode;
    const isGermany = addressCountryCode === GLOBALS.localeUpper.DE;

    let guest: any = {
      sameAsBooker: !!stayInThisRoom,
      stayingGuestDetails: {
        title: stayInThisRoom ? title : itemTitle,
        firstName: stayInThisRoom ? firstName : itemFirstName,
        lastName: stayInThisRoom ? lastName : itemLastName,
        address: {
          countryCode: stayInThisRoom ? countryCode : itemCountryCode,
          addressLine1: stayInThisRoom ? addressLine1 : itemAddressLine1,
          addressLine2: stayInThisRoom ? addressLine2 : itemAddressLine2,
          addressLine3: stayInThisRoom ? addressLine3 : itemAddressLine3,
          ...(!isGermany && { addressLine4: stayInThisRoom ? addressLine4 : itemAddressLine4 }),
          ...(isGermany && { cityName: stayInThisRoom ? addressLine4 : itemAddressLine4 }),
          postalCode: stayInThisRoom ? postcodeAddress : itemPostcodeAddress,
        },
      },
    };

    if (data.isAdditionalInformationEnabled) {
      const { dateOfBirth, passport, nationality, consent } = item;
      const { email } = stayInThisRoom ? data : item;
      if (consent !== false) {
        guest.stayingGuestDetails.emailAddress = email;

        if (dateOfBirth || passport || nationality) {
          guest.stayingGuestDetails.additionalDetails = mockFormatAdditionalInformation(
            dateOfBirth,
            passport,
            nationality
          );
        }
      }
    }

    if (data.shouldUseAccompanyingGuest && item) {
      guest = mockFormatAccompanyingGuest(guest, item);
    }

    return guest;
  };

  it('should return guest with sameAsBooker true when stayInThisRoom is true', () => {
    const data = {
      title: 'Mr',
      firstName: 'John',
      lastName: 'Doe',
      countryCode: 'GB',
      addressLine1: '123 Street',
      addressLine2: 'Apt 4',
      addressLine3: '',
      addressLine4: '',
      postcodeAddress: '12345',
      isAdditionalInformationEnabled: false,
      shouldUseAccompanyingGuest: false,
    };

    const item = {
      stayInThisRoom: true,
    };

    const result = getLeadGuestFormat(data, item);

    expect(result.sameAsBooker).toBe(true);
    expect(result.stayingGuestDetails.title).toBe('Mr');
    expect(result.stayingGuestDetails.firstName).toBe('John');
    expect(result.stayingGuestDetails.lastName).toBe('Doe');
    expect(result.stayingGuestDetails.address.countryCode).toBe('GB');
  });

  it('should add additionalDetails when isAdditionalInformationEnabled is true', () => {
    const data = {
      title: 'Mr',
      firstName: 'John',
      lastName: 'Doe',
      countryCode: 'GB',
      addressLine1: '123 Street',
      addressLine2: 'Apt 4',
      addressLine3: '',
      addressLine4: '',
      postcodeAddress: '12345',
      isAdditionalInformationEnabled: true,
      shouldUseAccompanyingGuest: false,
    };

    const item = {
      stayInThisRoom: false,
      dateOfBirth: '1990-01-01',
      passport: '123456789',
      nationality: { value: 'British' },
      consent: true,
      email: 'john.doe@example.com',
    };

    mockFormatAdditionalInformation.mockReturnValue({
      dob: '1990-01-01',
      passportNumber: '123456789',
      nationality: 'British',
    });

    const result = getLeadGuestFormat(data, item);

    expect(result.stayingGuestDetails.additionalDetails).toEqual({
      dob: '1990-01-01',
      passportNumber: '123456789',
      nationality: 'British',
    });
    expect(result.stayingGuestDetails.emailAddress).toBe('john.doe@example.com');
  });

  it('should call formatAccompanyingGuest when shouldUseAccompanyingGuest is true', () => {
    const data = {
      shouldUseAccompanyingGuest: true,
    };

    const item = {};

    mockFormatAccompanyingGuest.mockReturnValue({ formatted: true });

    const result = getLeadGuestFormat(data, item);

    expect(mockFormatAccompanyingGuest).toHaveBeenCalledWith(expect.any(Object), item);
    expect(result).toEqual({ formatted: true });
  });
});

describe('useEffect for debouncedgetAnonymousNewsletterPreferences', () => {
  const setShouldHideMarketingPreferencesMock = jest.fn();
  const debouncedgetAnonymousNewsletterPreferencesMock = jest.fn();

  const MockComponent = ({
    bookerEmail,
    shouldCheckMarketingPreferences,
  }: {
    bookerEmail: string;
    shouldCheckMarketingPreferences: boolean;
  }) => {
    useEffect(() => {
      if (!shouldCheckMarketingPreferences || !debouncedgetAnonymousNewsletterPreferencesMock) {
        return;
      }

      if (!bookerEmail || !isEmailValid(bookerEmail)) {
        setShouldHideMarketingPreferencesMock(false);
        return;
      }

      debouncedgetAnonymousNewsletterPreferencesMock(bookerEmail);
    }, [bookerEmail, shouldCheckMarketingPreferences]);

    return null;
  };

  afterEach(() => {
    jest.clearAllMocks();
    queryClient.clear();
  });

  it('should call debouncedgetAnonymousNewsletterPreferences when conditions are met', () => {
    (isEmailValid as jest.Mock).mockReturnValue(true);

    render(<MockComponent bookerEmail="test@example.com" shouldCheckMarketingPreferences={true} />);

    expect(debouncedgetAnonymousNewsletterPreferencesMock).toHaveBeenCalledWith('test@example.com');
  });

  it('should not call debouncedgetAnonymousNewsletterPreferences when shouldCheckMarketingPreferences is false', () => {
    render(
      <MockComponent bookerEmail="test@example.com" shouldCheckMarketingPreferences={false} />
    );

    expect(debouncedgetAnonymousNewsletterPreferencesMock).not.toHaveBeenCalled();
  });

  it('should set shouldHideMarketingPreferences to false when email is invalid', () => {
    (isEmailValid as jest.Mock).mockReturnValue(false);

    render(<MockComponent bookerEmail="invalid-email" shouldCheckMarketingPreferences={true} />);

    expect(setShouldHideMarketingPreferencesMock).toHaveBeenCalledWith(false);
  });
});

describe('useEffect for isPageDesignChangeEnabled', () => {
  const setFormDetailsMock = jest.fn();

  const MockComponent = ({
    isPageDesignChangeEnabled,
    INITIAL_GUEST_DETAILS_FORM_DATA,
  }: {
    isPageDesignChangeEnabled: boolean;
    INITIAL_GUEST_DETAILS_FORM_DATA: any;
  }) => {
    useEffect(() => {
      if (isPageDesignChangeEnabled) {
        setFormDetailsMock(INITIAL_GUEST_DETAILS_FORM_DATA);
      }
    }, [INITIAL_GUEST_DETAILS_FORM_DATA]);

    return null;
  };

  afterEach(() => {
    jest.clearAllMocks();
    queryClient.clear();
  });

  it('should call setFormDetails when isPageDesignChangeEnabled is true', () => {
    render(
      <MockComponent
        isPageDesignChangeEnabled={true}
        INITIAL_GUEST_DETAILS_FORM_DATA={{ test: 'data' }}
      />
    );

    expect(setFormDetailsMock).toHaveBeenCalledWith({ test: 'data' });
  });

  it('should not call setFormDetails when isPageDesignChangeEnabled is false', () => {
    render(
      <MockComponent
        isPageDesignChangeEnabled={false}
        INITIAL_GUEST_DETAILS_FORM_DATA={{ test: 'data' }}
      />
    );

    expect(setFormDetailsMock).not.toHaveBeenCalled();
  });
});

describe('useEffect for defaultValues.countryCode', () => {
  const setIsLocationRequiredMock = jest.fn();

  const MockComponent = ({
    formData,
    biQueryInput,
    defaultValues,
  }: {
    formData: any;
    biQueryInput: any;
    defaultValues: any;
  }) => {
    useEffect(() => {
      if (formData?.basketReferenceId !== biQueryInput?.basketReference) {
        if ((defaultValues?.countryCode as string) === 'DE') {
          setIsLocationRequiredMock(true);
        }
      } else if ((formData?.countryCode as string) === 'DE') {
        setIsLocationRequiredMock(true);
      }
    }, [defaultValues.countryCode]);

    return null;
  };

  afterEach(() => {
    jest.clearAllMocks();
    queryClient.clear();
  });

  it('should call setIsLocationRequired when formData.basketReferenceId is different and countryCode is DE', () => {
    render(
      <MockComponent
        formData={{ basketReferenceId: '123' }}
        biQueryInput={{ basketReference: '456' }}
        defaultValues={{ countryCode: 'DE' }}
      />
    );

    expect(setIsLocationRequiredMock).toHaveBeenCalledWith(true);
  });

  it('should call setIsLocationRequired when formData.countryCode is DE', () => {
    render(
      <MockComponent
        formData={{ countryCode: 'DE', basketReferenceId: '123' }}
        biQueryInput={{ basketReference: '123' }}
        defaultValues={{ countryCode: 'GB' }}
      />
    );

    expect(setIsLocationRequiredMock).toHaveBeenCalledWith(true);
  });

  it('should not call setIsLocationRequired when conditions are not met', () => {
    render(
      <MockComponent
        formData={{ basketReferenceId: '123' }}
        biQueryInput={{ basketReference: '123' }}
        defaultValues={{ countryCode: 'GB' }}
      />
    );

    expect(setIsLocationRequiredMock).not.toHaveBeenCalled();
  });
});

describe('getFormState', () => {
  afterEach(() => {
    jest.clearAllMocks();
    queryClient.clear();
  });

  it('should return early when form is not initialized', () => {
    const result = getDefaultValuesFromFormState({
      isInitialized: false,
      isRemovePIIDataFromLocalStorageEnabled: true,
      bookingAcceptFutureMailing: true,
      state: { firstName: 'John', acceptFutureMailing: false },
    });

    expect(result).toBeUndefined();
  });

  it('should set acceptFutureMailing from booking information when isRemovePIIDataFromLocalStorageEnabled is true', () => {
    const result = getDefaultValuesFromFormState({
      isInitialized: true,
      isRemovePIIDataFromLocalStorageEnabled: true,
      bookingAcceptFutureMailing: true,
      state: { firstName: 'John', acceptFutureMailing: false },
    });

    expect(result).toEqual({
      firstName: 'John',
      acceptFutureMailing: true,
    });
  });

  it('should set next state directly when isRemovePIIDataFromLocalStorageEnabled is false', () => {
    const nextState = { firstName: 'Jane', acceptFutureMailing: false };
    const result = getDefaultValuesFromFormState({
      isInitialized: true,
      isRemovePIIDataFromLocalStorageEnabled: false,
      bookingAcceptFutureMailing: true,
      state: nextState,
    });

    expect(result).toEqual(nextState);
  });
});

describe('saveFormDataInLocalStorage', () => {
  const setFormDetailsMock = jest.fn();
  const bkngDataMock = {
    bookingInformation: {
      reservationByIdList: [{ stayInThisRoom: true }],
    },
  };
  const biQueryInputMock = { basketReference: 'test-basket-reference' };
  const isBillingAddressDisplayedMock = true;
  const isAdditionalInformationEnabledMock = true;
  const isSomeoneElseAndSingleRoomRedesignMock = false;

  const saveFormDataInLocalStorage = (formData: any) => {
    const multiroom = bkngDataMock?.bookingInformation?.reservationByIdList?.length > 1;
    const bookerIsNotGuest = multiroom
      ? !formData.leadGuest?.some((guest: { stayInThisRoom: boolean }) => !!guest.stayInThisRoom)
      : formData.bookingForSomeoneElse || isSomeoneElseAndSingleRoomRedesignMock;

    setFormDetailsMock((formDetails: any) => {
      if (formData) {
        formDetails.addressLine1 = formData.addressLine1;
        formDetails.addressLine2 = formData.addressLine2;
        formDetails.addressLine3 = formData.addressLine3;
        formDetails.addressLine4 = formData.addressLine4;
        formDetails.postcodeAddress = formData.postcodeAddress;
        formDetails.addressSelection = formData.addressSelection;
        formDetails.cityName = formData.cityName;
        formDetails.companyName = formData.companyName;
        formDetails.countryCode = formData.countryCode;
        formDetails.email = formData.email;
        formDetails.firstName = formData.firstName;
        formDetails.landline = formData.landline;
        formDetails.lastName = formData.lastName;
        formDetails.manualAddressToggle = formData.manualAddressToggle;
        formDetails.phone = formData.phone;
        formDetails.postalCode = formData.postalCode;
        formDetails.reasonForStay = formData.reasonForStay;
        formDetails.title = formData.title;
        formDetails.bookingForSomeoneElse = bookerIsNotGuest;
        formDetails.leadGuest = formData.leadGuest;
        formDetails.basketReferenceId = biQueryInputMock.basketReference;
        formDetails.updated = true;
        formDetails.isBillingAddressDisplayed = isBillingAddressDisplayedMock;
        formDetails.billing = {
          address: {
            countryCode: formData.billing_countryCode,
            companyName: formData.billing_companyName,
            addressLine1: formData.billing_addressLine1,
            addressLine2: formData.billing_addressLine2,
            addressLine3: formData.billing_addressLine3,
            addressLine4: formData.billing_addressLine4,
            cityName: formData.billing_cityName,
            postalCode: formData.billing_postalCode,
            addressType: formData.billingAddressCheckbox
              ? formData.billing_addressSelection
              : formData.addressSelection,
          },
          title: formData.title,
          firstName: formData.firstName,
          lastName: formData.lastName,
          email: formData.email,
          telephone: formData.phone,
          differentBillingAddress: formData.billingAddressCheckbox,
        };
        if (isAdditionalInformationEnabledMock) {
          const { dateOfBirth, nationality, passport } = formData;
          if (dateOfBirth)
            formDetails.dateOfBirth = 'formatted-date'; // Mocked formatDate result
          else formDetails.dateOfBirth = dateOfBirth;
          formDetails.nationality = nationality;
          formDetails.passport = passport;
        }
        return formDetails;
      }
    });
  };

  afterEach(() => {
    jest.clearAllMocks();
    queryClient.clear();
  });

  it('should update formDetails with provided formData', () => {
    const formData = {
      addressLine1: '123 Main St',
      addressLine2: 'Apt 4B',
      addressLine3: '',
      addressLine4: '',
      postcodeAddress: '12345',
      addressSelection: 'HOME',
      cityName: 'Berlin',
      companyName: 'Test Company',
      countryCode: 'DE',
      email: 'test@example.com',
      firstName: 'John',
      landline: '123456789',
      lastName: 'Doe',
      manualAddressToggle: '',
      phone: '987654321',
      postalCode: '12345',
      reasonForStay: 'Business',
      title: 'Mr',
      bookingForSomeoneElse: false,
      leadGuest: [{ stayInThisRoom: true }],
      billing_countryCode: 'DE',
      billing_companyName: 'Test Billing Company',
      billing_addressLine1: '456 Billing St',
      billing_addressLine2: '',
      billing_addressLine3: '',
      billing_addressLine4: '',
      billing_cityName: 'Munich',
      billing_postalCode: '67890',
      billing_addressSelection: 'BUSINESS',
      billingAddressCheckbox: true,
      dateOfBirth: '1990-01-01',
      nationality: 'German',
      passport: '123456789',
    };

    saveFormDataInLocalStorage(formData);

    expect(setFormDetailsMock).toHaveBeenCalled();
    expect(setFormDetailsMock).toHaveBeenCalledWith(expect.any(Function));

    const updateFunction = setFormDetailsMock.mock.calls[0][0];
    const updatedFormDetails = updateFunction({});
    expect(updatedFormDetails).toEqual({
      addressLine1: '123 Main St',
      addressLine2: 'Apt 4B',
      addressLine3: '',
      addressLine4: '',
      postcodeAddress: '12345',
      addressSelection: 'HOME',
      cityName: 'Berlin',
      companyName: 'Test Company',
      countryCode: 'DE',
      email: 'test@example.com',
      firstName: 'John',
      landline: '123456789',
      lastName: 'Doe',
      manualAddressToggle: '',
      phone: '987654321',
      postalCode: '12345',
      reasonForStay: 'Business',
      title: 'Mr',
      bookingForSomeoneElse: false,
      leadGuest: [{ stayInThisRoom: true }],
      basketReferenceId: 'test-basket-reference',
      updated: true,
      isBillingAddressDisplayed: true,
      billing: {
        address: {
          countryCode: 'DE',
          companyName: 'Test Billing Company',
          addressLine1: '456 Billing St',
          addressLine2: '',
          addressLine3: '',
          addressLine4: '',
          cityName: 'Munich',
          postalCode: '67890',
          addressType: 'BUSINESS',
        },
        title: 'Mr',
        firstName: 'John',
        lastName: 'Doe',
        email: 'test@example.com',
        telephone: '987654321',
        differentBillingAddress: true,
      },
      dateOfBirth: 'formatted-date',
      nationality: 'German',
      passport: '123456789',
    });
  });
});

describe('saveFormDataInLocalStorage - Additional Information', () => {
  const setFormDetailsMock = jest.fn();
  const formatDateMock = jest.fn();

  const saveFormDataInLocalStorage = (formData: any) => {
    setFormDetailsMock((formDetails: any) => {
      if (formData.dateOfBirth) {
        formDetails.dateOfBirth = formatDateMock(formData.dateOfBirth.toString(), 'yyyy-MM-dd');
      } else {
        formDetails.dateOfBirth = formData.dateOfBirth;
      }
      formDetails.nationality = formData.nationality;
      formDetails.passport = formData.passport;
      return formDetails;
    });
  };

  afterEach(() => {
    jest.clearAllMocks();
    queryClient.clear();
  });

  it('should format dateOfBirth when provided', () => {
    const formData = {
      dateOfBirth: '1990-01-01',
      nationality: 'German',
      passport: '123456789',
    };

    formatDateMock.mockReturnValue('1990-01-01');

    saveFormDataInLocalStorage(formData);

    expect(setFormDetailsMock).toHaveBeenCalledWith(expect.any(Function));
    const updateFunction = setFormDetailsMock.mock.calls[0][0];
    const updatedFormDetails = updateFunction({});
    expect(updatedFormDetails.dateOfBirth).toBe('1990-01-01');
    expect(updatedFormDetails.nationality).toBe('German');
    expect(updatedFormDetails.passport).toBe('123456789');
  });

  it('should assign dateOfBirth as-is when not provided', () => {
    const formData = {
      dateOfBirth: null,
      nationality: 'German',
      passport: '123456789',
    };

    saveFormDataInLocalStorage(formData);

    expect(setFormDetailsMock).toHaveBeenCalledWith(expect.any(Function));
    const updateFunction = setFormDetailsMock.mock.calls[0][0];
    const updatedFormDetails = updateFunction({});
    expect(updatedFormDetails.dateOfBirth).toBeNull();
    expect(updatedFormDetails.nationality).toBe('German');
    expect(updatedFormDetails.passport).toBe('123456789');
  });
});

describe('marketingPreferences', () => {
  const formDataMock = {
    email: 'test@example.com',
    title: 'Mr',
    firstName: 'John',
    lastName: 'Doe',
    countryCode: 'GB',
    acceptFutureMailing: true,
  };

  afterEach(() => {
    jest.clearAllMocks();
    queryClient.clear();
  });

  it('should build register marketing payload for GB locale', () => {
    const payload = buildRegisterMarketingPreferencesPayload(formDataMock, 'en');

    expect(payload).toEqual({
      customerId: 'test@example.com',
      title: 'Mr',
      firstName: 'John',
      lastName: 'Doe',
      countryOfResidence: 'GB',
      locale: 'UK',
      optIn: true,
      doubleOptIn: false,
      language: 'en',
      channel: MARKETING_CHANNEL,
      journey: MARKETING_JOURNEY,
      brandCodes: BRANDCODES,
    });
  });

  it('should build register marketing payload for DE locale and enable doubleOptIn', () => {
    const payload = buildRegisterMarketingPreferencesPayload(
      {
        ...formDataMock,
        countryCode: 'DE',
      },
      'de'
    );

    expect(payload).toEqual({
      customerId: 'test@example.com',
      title: 'Mr',
      firstName: 'John',
      lastName: 'Doe',
      countryOfResidence: 'DE',
      locale: 'DE',
      optIn: true,
      doubleOptIn: true,
      language: 'de',
      channel: MARKETING_CHANNEL,
      journey: MARKETING_JOURNEY,
      brandCodes: BRANDCODES,
    });
  });
});

describe('setReasonForStay', () => {
  const urfsMutationMock = {
    mutate: jest.fn(),
  };
  const queryClientMock = {
    invalidateQueries: jest.fn(),
  };
  const setUpdatingTotalCostMock = jest.fn();
  const bkngDataMock = {
    bookingInformation: {
      hotelId: 'HOTEL123',
    },
  };
  const biQueryInputMock = {
    basketReference: 'BASKET123',
  };

  const setReasonForStay = (reasonForStay: string) => {
    setUpdatingTotalCostMock(true);
    urfsMutationMock.mutate(
      {
        hotelId: bkngDataMock.bookingInformation.hotelId,
        reasonForStay: reasonForStay,
        basketReference: biQueryInputMock.basketReference,
      },
      {
        onSuccess: () => {
          queryClientMock.invalidateQueries({ queryKey: ['GetBookingInformation'] });
          setUpdatingTotalCostMock(false);
        },
        onError: () => {
          console.log('Error setting reason for stay');
        },
      }
    );
  };

  afterEach(() => {
    jest.clearAllMocks();
    queryClient.clear();
  });

  it('should call urfsMutation and invalidate queries on success', () => {
    urfsMutationMock.mutate.mockImplementation((_, { onSuccess }) => onSuccess());

    setReasonForStay('Business');

    expect(setUpdatingTotalCostMock).toHaveBeenCalledWith(true);
    expect(urfsMutationMock.mutate).toHaveBeenCalledWith(
      {
        hotelId: 'HOTEL123',
        reasonForStay: 'Business',
        basketReference: 'BASKET123',
      },
      expect.any(Object)
    );
    expect(queryClientMock.invalidateQueries).toHaveBeenCalledWith({
      queryKey: ['GetBookingInformation'],
    });
    expect(setUpdatingTotalCostMock).toHaveBeenCalledWith(false);
  });

  it('should log error on mutation failure', () => {
    const consoleLogMock = jest.spyOn(console, 'log').mockImplementation();
    urfsMutationMock.mutate.mockImplementation((_, { onError }) => onError());

    setReasonForStay('Business');

    expect(consoleLogMock).toHaveBeenCalledWith('Error setting reason for stay');
    consoleLogMock.mockRestore();
  });
});

describe('getAnonymousNewsletterPreferences', () => {
  const queryClientMock = {
    fetchQuery: jest.fn(),
  };
  const setMarketingPreferencesMock = jest.fn();

  beforeEach(() => {
    resetMockData();
    jest.clearAllMocks();
  });

  it('should set isSubscribed to true when isDEOptInEnabled is true and suppressMarketingCheckbox is true', async () => {
    queryClientMock.fetchQuery.mockResolvedValueOnce({
      anonymousNewsletterPreferences: {
        suppressMarketingCheckbox: true,
        optIn: true,
      },
    });

    await getAnonymousNewsletterPreferences(
      'test@example.com',
      queryClientMock,
      setMarketingPreferencesMock,
      true,
      'DE',
      'de'
    );

    expect(queryClientMock.fetchQuery).toHaveBeenCalledWith({
      queryKey: ['getAnonymousNewsletterPreferences', 'PINN', 'test@example.com', 'DE', 'de'],
      queryFn: expect.any(Function),
      gcTime: 0,
      staleTime: 0,
    });

    expect(setMarketingPreferencesMock).toHaveBeenCalled();
    // expect(analyticsUpdateMock).toHaveBeenCalledWith({ optInCustomer: true });
  });

  it('should set isSubscribed to false when isdeOptInEnabled is true and suppressMarketingCheckbox is false', async () => {
    queryClientMock.fetchQuery.mockResolvedValueOnce({
      anonymousNewsletterPreferences: {
        suppressMarketingCheckbox: false,
        optIn: true,
      },
    });

    await getAnonymousNewsletterPreferences(
      'test@example.com',
      queryClientMock,
      setMarketingPreferencesMock,
      true,

      'de',
      'de'
    );

    expect(setMarketingPreferencesMock).toHaveBeenCalled();
    // expect(analyticsUpdateMock).toHaveBeenCalledWith({ optInCustomer: true });
  });

  it('should set isSubscribed to true when isDEOptInEnabled is false and optIn is true', async () => {
    queryClientMock.fetchQuery.mockResolvedValueOnce({
      anonymousNewsletterPreferences: {
        optIn: true,
      },
    });

    await getAnonymousNewsletterPreferences(
      'test@example.com',
      queryClientMock,
      setMarketingPreferencesMock,
      true
    );

    expect(setMarketingPreferencesMock).toHaveBeenCalled();
    // expect(analyticsUpdateMock).toHaveBeenCalledWith({ optInCustomer: true });
  });

  it('should set isSubscribed to false when isDEOptInEnabled is false and optIn is false', async () => {
    queryClientMock.fetchQuery.mockResolvedValueOnce({
      anonymousNewsletterPreferences: {
        optIn: false,
      },
    });

    await getAnonymousNewsletterPreferences(
      'test@example.com',
      queryClientMock,
      setMarketingPreferencesMock,
      true
    );

    expect(setMarketingPreferencesMock).toHaveBeenCalled();
    // expect(analyticsUpdateMock).toHaveBeenCalledWith({ optInCustomer: false });
  });

  it('should handle errors gracefully and set isSubscribed to false', async () => {
    queryClientMock.fetchQuery.mockRejectedValueOnce(new Error('Network error'));

    await getAnonymousNewsletterPreferences(
      'test@example.com',
      queryClientMock,
      setMarketingPreferencesMock,
      true,
      'DE',
      'de'
    );

    expect(setMarketingPreferencesMock).toHaveBeenCalled();
    // expect(analyticsUpdateMock).toHaveBeenCalledWith({ optInCustomer: undefined });
  });

  it('should call analytics.update with optInCustomer when optIn is true', async () => {
    queryClientMock.fetchQuery.mockResolvedValueOnce({
      anonymousNewsletterPreferences: {
        optIn: true,
      },
    });

    await getAnonymousNewsletterPreferences(
      'test@example.com',
      queryClientMock,
      setMarketingPreferencesMock,
      true
    );

    expect(setMarketingPreferencesMock).toHaveBeenCalled();
    // expect(analyticsUpdateMock).toHaveBeenCalledWith({ optInCustomer: true });
  });

  it('should call analytics.update with optInCustomer when optIn is false', async () => {
    queryClientMock.fetchQuery.mockResolvedValueOnce({
      anonymousNewsletterPreferences: {
        optIn: false,
      },
    });

    await getAnonymousNewsletterPreferences(
      'test@example.com',
      queryClientMock,
      setMarketingPreferencesMock,
      true
    );

    expect(setMarketingPreferencesMock).toHaveBeenCalled();
    // expect(analyticsUpdateMock).toHaveBeenCalledWith({ optInCustomer: false });
  });
});

describe('GDP PI - FT_PI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE integration', () => {
  const mockRouter = { query: jest.fn(), push: jest.fn() };
  const PII_FLAG_KEY = 'release_pi_remove_pii_data_from_local_storage';

  beforeEach(() => {
    resetMockData();
    mockCustomLocale.mockReturnValue({ language: 'en', country: 'gb' });
    mockUserData.mockReturnValue({ isLoggedIn: false });
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockMutationResponse.isError = false;
  });

  afterEach(() => {
    jest.clearAllMocks();
    queryClient.clear();
  });

  it('should pre-populate form with booking data when flag is true', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [PII_FLAG_KEY]: true,
    });

    (getDefaultDataFromBooking as jest.Mock).mockReturnValue({
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@test.com',
    });

    const { getByTestId, findByTestId } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();

    await waitFor(() => {
      expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
    });

    const firstNameInput = await findByTestId('input-firstName');
    expect(firstNameInput).toBeInTheDocument();
    expect((firstNameInput as HTMLInputElement).value).toBe('John');
  });

  it('should merge cachedGuestDetailsFormData and prefer cached values when flag is true', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [PII_FLAG_KEY]: true,
    });

    (getDefaultDataFromBooking as jest.Mock).mockReturnValue({
      firstName: 'BookingFirstName',
      lastName: 'BookingLastName',
    });

    const { findByTestId } = render(
      <GuestDetailsPage
        {...mockProps}
        queryClient={queryClient}
        router={mockRouter as any}
        cachedGuestDetailsFormData={{
          firstName: 'CachedFirstName',
          lastName: 'CachedLastName',
        }}
      />
    );

    const firstNameInput = await findByTestId('input-firstName');
    const lastNameInput = await findByTestId('input-lastName');

    expect((firstNameInput as HTMLInputElement).value).toBe('CachedFirstName');
    expect((lastNameInput as HTMLInputElement).value).toBe('CachedLastName');
  });

  it('should render form with empty defaults when flag is false', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [PII_FLAG_KEY]: false,
    });

    const { getByTestId } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();

    await waitFor(() => {
      const firstNameInput = getByTestId('input-firstName');
      expect((firstNameInput as HTMLInputElement).value).toBe('');
    });
  });

  it('should not pre-populate form when flag is undefined', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [PII_FLAG_KEY]: undefined,
    });

    const { getByTestId } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();

    await waitFor(() => {
      const firstNameInput = getByTestId('input-firstName');
      expect((firstNameInput as HTMLInputElement).value).toBe('');
    });
  });

  it('should not use cachedGuestDetailsFormData when flag is false', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [PII_FLAG_KEY]: false,
    });

    const { getByTestId } = render(
      <GuestDetailsPage
        {...mockProps}
        queryClient={queryClient}
        router={mockRouter as any}
        cachedGuestDetailsFormData={{ firstName: 'CachedFirstName' }}
      />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();

    await waitFor(() => {
      const firstNameInput = getByTestId('input-firstName');
      expect((firstNameInput as HTMLInputElement).value).toBe('');
    });
  });

  it('should not clear cached GDP data before submit when there is no cachedGuestDetailsFormData', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [PII_FLAG_KEY]: true,
    });

    render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    await waitFor(() => {
      expect(clearGuestFormData).not.toHaveBeenCalled();
    });
  });
});
