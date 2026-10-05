import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { ROOM_TYPE } from '@whitbread-eos/api';
import { Form } from '@whitbread-eos/atoms';
import {
  getBookingInformationData as getBookingInformationDataOriginal,
  getCountriesData as getCountriesDataOriginal,
  getHotelInformation as getHotelInformationOriginal,
  getPostCodeAddresesData as getPostCodeAddresesDataOriginal,
  getPostCodeAddresesInfoData as getPostCodeAddresesInfoDataOriginal,
  getRoomSelectionData as getRoomSelectionDataOriginal,
  singleBookingInformationData as singleBookingInformationDataOriginal,
} from '@whitbread-eos/utils';
import { clearGuestFormData } from '@whitbread-eos/utils/server';
import React from 'react';

import { guestDetailsFormConfig } from './ccuiFormConfig/guestDetailsFormConfig';
import GuestDetailsPageCcui from './page.ccui';
import { fireEvent, render, userEvent, waitFor } from './utils/test-utils';

// Mutable mock data - cloned from shared originals
let getBookingInformationData = JSON.parse(JSON.stringify(getBookingInformationDataOriginal));
let singleBookingInformationData = JSON.parse(JSON.stringify(singleBookingInformationDataOriginal));
let getHotelInformation = JSON.parse(JSON.stringify(getHotelInformationOriginal));
let getCountriesData = JSON.parse(JSON.stringify(getCountriesDataOriginal));
let getPostCodeAddresesData = JSON.parse(JSON.stringify(getPostCodeAddresesDataOriginal));
let getPostCodeAddresesInfoData = JSON.parse(JSON.stringify(getPostCodeAddresesInfoDataOriginal));
let getRoomSelectionData = JSON.parse(JSON.stringify(getRoomSelectionDataOriginal));

//region Mock Objects
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

const queryClient = new ReactQuery.QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
      gcTime: 0,
    },
  },
});
const mockCustomLocale = jest.fn();
const mockUseRouter = jest.fn();
const mockProps = {
  user: {},
  setAnalyticsUser: jest.fn(),
  biQueryInput: {
    basketReference: 'AWM222',
    country: 'GB',
    language: 'en',
    bookingChannelCriteria: {
      channel: 'CCUI' as any,
      language: 'EN',
      subchannel: 'WEB',
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
//endregion

//region Jest Mock
function mockUseQueryRequest(queryKey: any[]) {
  const key = queryKey[0];

  if (typeof key === 'string') {
    switch (key) {
      case 'GetBookingInformation':
        return getBookingInformationData;
      case 'GetPackages':
        return getPackagesData;
      case 'GetHotelInformation':
        return getHotelInformation;
      case 'GetRoomSelection':
        return getRoomSelectionData;
      case 'GetCountries':
        return getCountriesData;
      case 'addresses':
        return getPostCodeAddresesData;
      case 'addresses-info':
        return getPostCodeAddresesInfoData;
      default:
        return {};
    }
  }
}

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  getNoOfDaysInYear: jest.fn(),
  SEO: () => <div></div>,
  LoadingSpinner: () => <div></div>,
  // LeadGuestDetails: () => <div data-testid="LeadGuestDetails"></div>,
}));

const mockUseLocalStorage = jest.fn();

jest.mock('@whitbread-eos/utils', () => {
  const actual = jest.requireActual('@whitbread-eos/utils');
  return {
    ...actual,
    formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
    useCustomLocale: () => mockCustomLocale(),
    invalidateQueries: mockUseQueryRequest,
    useQueryRequest: mockUseQueryRequest,
    useMutationRequest: () => mockMutationResponse,
    usePackages: () => ({
      ...getPackagesData?.data?.packages,
      ...getPackagesData,
    }),
    graphQLRequest: jest.fn(),
    updateAncillariesAnalytics: () => jest.fn(),
    useFeatureToggle: jest.fn(() => mockFeatureToggles),
    useLocalStorage: (...args: unknown[]) => mockUseLocalStorage(...args),
  };
});

// Mockable feature toggles - can be updated per-test
let mockFeatureToggles = {
  release_ccui_accompanying_guest_details: true,
};

const mockMutate = jest.fn();
const mockMutationResponse = {
  isError: false,
  error: { message: 'Error setting reason for stay' },
  mutation: {
    isLoading: false,
    data: {},
    mutate: mockMutate,
  },
};
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  clearGuestFormData: jest.fn().mockResolvedValue(true),
}));
//endregion

// Function to reset mock data before each test
const resetMockData = () => {
  getBookingInformationData = JSON.parse(JSON.stringify(getBookingInformationDataOriginal));
  singleBookingInformationData = JSON.parse(JSON.stringify(singleBookingInformationDataOriginal));
  getHotelInformation = JSON.parse(JSON.stringify(getHotelInformationOriginal));
  getCountriesData = JSON.parse(JSON.stringify(getCountriesDataOriginal));
  getPostCodeAddresesData = JSON.parse(JSON.stringify(getPostCodeAddresesDataOriginal));
  getPostCodeAddresesInfoData = JSON.parse(JSON.stringify(getPostCodeAddresesInfoDataOriginal));
  getRoomSelectionData = JSON.parse(JSON.stringify(getRoomSelectionDataOriginal));
};

//region Unit tests
const defaultFormDetails = {
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
  cityName: '',
  postcodeAddress: '',
  addressSelection: '',
  countryCode: '',
  manualAddressToggle: '',
  basketReferenceId: '',
  leadGuest: [],
  bookingForSomeoneElse: false,
  billing: {
    address: {
      countryCode: '',
      companyName: '',
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      postalCode: '',
      cityName: '',
      billingAddressSelection: '',
    },
    differentBillingAddress: false,
  },
};

describe('GDP CCUI ', () => {
  beforeEach(() => {
    resetMockData(); // Reset mocks before each test
    mockUseLocalStorage.mockImplementation((key: string, initialValue: unknown) => {
      if (key === 'formDetails') {
        return [defaultFormDetails, jest.fn()];
      }
      return [initialValue, jest.fn()];
    });
    mockCustomLocale.mockReturnValue({
      language: 'gb',
      country: 'en',
    });
  });
  it('should render page GDP CCUI details skeleton', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    getPackagesData.data.packages.hotelHasCityTaxForBusiness = true;
    getPackagesData.data.packages.hotelHasCityTaxForLeisure = false;
    mockMutationResponse.isError = false;

    const { getByTestId } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
  });

  it('should render GDP CCUI booking summary', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getPackagesData.data.packages.hotelHasCityTaxForBusiness = false;
    getPackagesData.data.packages.hotelHasCityTaxForLeisure = true;

    const { getByTestId, getByText } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
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

  it('should render GDP CCUI and have continue button', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const { getByTestId } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('BookingSummary-ContinueButton')).toBeInTheDocument();
  });

  it('should render GDP CCUI and have continue button and click it', async () => {
    const mockRouter = {
      query: jest.fn(),
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const { getByTestId } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    const button = getByTestId('BookingSummary-ContinueButton');

    await waitFor(() => {
      fireEvent.click(button);
    });
  });

  it('should render booker tabs', async () => {
    const mockRouter = {
      query: jest.fn(),
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const { getByTestId, getByLabelText, queryByText } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();

    const radioOptionLeisure = getByLabelText('booking.reason.leisure');
    const radioOptionBusiness = getByLabelText('booking.reason.business');

    await userEvent.click(radioOptionLeisure);
    expect(radioOptionLeisure).toBeChecked();
    expect(radioOptionBusiness).not.toBeChecked();

    // DNRQ-58765 - verify the I am booking for myself faux tabs are not visible (Single Booking redesign changes)
    const bookingMyselfMockTab = queryByText('ccui.booking.details.forThemselves');
    const bookingSomeoneelseMockTab = queryByText('booking.guestDetails.iAmBookingForSomeoneElse');
    expect(bookingMyselfMockTab).not.toBeInTheDocument();
    expect(bookingSomeoneelseMockTab).not.toBeInTheDocument();
  });

  it('should render GDP CCUI with info message', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    getBookingInformationData.data.bookingInformation.infoMessages.push('TestInfoMess');
    const { queryByText } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(queryByText('TestInfoMess')).not.toBeInTheDocument();
  });

  it('should render GDP CCUI with loading true', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    getBookingInformationData.isLoading = true;
    const { getByText } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByText('booking.loading')).toBeInTheDocument();
    getBookingInformationData.isLoading = false;
  });

  it('should render GDP CCUI with get booking information error', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getBookingInformationData.isError = true;
    const { getByText } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByText('Error on getting booking information....')).toBeInTheDocument();
    expect(getByText('error booking information')).toBeInTheDocument();
    getBookingInformationData.isError = false;
  });

  it('should render GDP CCUI with get packages  error', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getPackagesData.isError = true;
    const { getByText } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByText('Error on getting packages....')).toBeInTheDocument();
    expect(getByText('error packages')).toBeInTheDocument();
    getPackagesData.isError = false;
  });

  it('should render GDP CCUI with get hotel information error', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getHotelInformation.isError = true;
    const { getByText } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByText('Error on getting hotel information....')).toBeInTheDocument();
    expect(getByText('error hotel information')).toBeInTheDocument();
    getHotelInformation.isError = false;
  });

  it('should render GDP CCUI skeleton with hotelDetails undefined', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getHotelInformation.data = undefined as any;
    const { getByTestId } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
  });

  it('should render page pi skeleton with multi rooms', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getBookingInformationData.data.bookingInformation.reservationByIdList.push({
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
    });
    const { getByTestId } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
  });

  it('should render GDP CCUI skeleton with room selection undefined', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getRoomSelectionData.data = undefined as any;
    const { getByTestId } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
  });

  it('should render GDP CCUI skeleton with meals and booking info undefined', async () => {
    const mockRouter = {
      query: jest.fn(),
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getPackagesData.data = undefined as any;
    getRoomSelectionData.data = undefined as any;

    const { getByText, getByTestId, getByLabelText, container } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
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

    fireEvent.click(dropdownToggle);

    const dropdownOptionTitle = getByTestId('DropdownComp-GuestDetails-Title-InnerDropdown-0');
    fireEvent.click(dropdownOptionTitle);

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

    fireEvent.click(manualAddress);

    const radioOptionHomeAddress = getByTestId('GuestDetails-AddressSelection-PersonalAddress');

    fireEvent.click(radioOptionHomeAddress);

    const inputAddressLine1 = getByTestId('input-addressLine1');

    fireEvent.focus(inputAddressLine1);
    fireEvent.change(inputAddressLine1, { target: { value: 'Street 22 December' } });
    const inputPostAlCode = getByTestId('input-postalCode');

    const postCode = 'CR7 7EZ' as string;
    fireEvent.focus(inputPostAlCode);
    fireEvent.cut(inputPostAlCode);
    fireEvent.change(inputPostAlCode, { target: { value: postCode } });
    expect(inputPostAlCode).toHaveValue(postCode);

    const button = getByTestId('GuestDetails-Submit');

    await waitFor(() => {
      fireEvent.click(button);
    });
  });

  it('should render GDP CCUI skeleton with meals and booking info undefined DE', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockCustomLocale.mockReturnValue({
      language: 'de',
      country: 'de',
      currentLang: 'de',
    });
    getPackagesData.data = undefined as any;
    getRoomSelectionData.data = undefined as any;
    const { getByTestId, getByLabelText, container } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
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

    const inputPhone = getByTestId('GuestDetails-Mobile-phoneNumber');

    fireEvent.focus(inputPhone);
    fireEvent.change(inputPhone, { target: { value: '+40728954441' } });

    const radioOptionHomeAddress = getByTestId('GuestDetails-AddressSelection-PersonalAddress');

    fireEvent.click(radioOptionHomeAddress);

    const inputAddressLine1 = getByTestId('input-addressLine1');

    fireEvent.focus(inputAddressLine1);
    fireEvent.change(inputAddressLine1, { target: { value: 'Street 22 December' } });

    const inputCityNameDe = getByTestId('input-cityName');

    fireEvent.focus(inputCityNameDe);
    fireEvent.change(inputCityNameDe, { target: { value: 'London' } });

    const inputPostAlCode = getByTestId('input-postalCode');
    fireEvent.change(inputPostAlCode, { target: { value: '20097' } });

    expect(inputPostAlCode).toHaveValue('20097');

    const button = getByTestId('GuestDetails-Submit');

    await waitFor(() => {
      fireEvent.click(button);
    });
  });
  it('should re-compute defaultValues when formDetails has reused data with matching basketReferenceId', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const reusedFormDetails = {
      ...defaultFormDetails,
      updated: true,
      basketReferenceId: 'AWM222',
      title: 'Mr',
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@test.com',
      phone: '07777777777',
      leadGuest: [],
    };
    mockUseLocalStorage.mockImplementation((key: string, initialValue: unknown) => {
      if (key === 'formDetails') {
        return [reusedFormDetails, jest.fn()];
      }
      return [initialValue, jest.fn()];
    });

    const { getByTestId } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    await waitFor(() => {
      expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
    });
  });

  it('should not re-compute defaultValues when basketReferenceId does not match', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockUseLocalStorage.mockImplementation((key: string, initialValue: unknown) => {
      if (key === 'formDetails') {
        return [
          { ...defaultFormDetails, updated: true, basketReferenceId: 'DIFFERENT_ID' },
          jest.fn(),
        ];
      }
      return [initialValue, jest.fn()];
    });

    const { getByTestId } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    await waitFor(() => {
      expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
    });
  });

  it('should re-compute defaultValues via mapBookingInformationForReuseBooking when release_ccui_remove_pii_data_from_local_storage is enabled and reUseReservation has an id', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const originalFeatureToggles = { ...mockFeatureToggles };
    mockFeatureToggles = {
      ...mockFeatureToggles,
      release_ccui_remove_pii_data_from_local_storage: true,
    } as typeof mockFeatureToggles;
    getBookingInformationData.isSuccess = true;

    const stableReUseReservation = { id: 'PREV-BASKET-REF' };
    mockUseLocalStorage.mockImplementation((key: string, initialValue: unknown) => {
      if (key === 'reUseReservation') {
        return [stableReUseReservation, jest.fn()];
      }
      if (key === 'formDetails') {
        return [defaultFormDetails, jest.fn()];
      }
      return [initialValue, jest.fn()];
    });

    const { getByTestId } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    await waitFor(() => {
      expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
    });

    mockFeatureToggles = originalFeatureToggles;
  });

  it('should show error when rendering because of RFST', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockMutationResponse.isError = true;

    const { getByTestId } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(mockMutationResponse.error.message).toBe('Error setting reason for stay');
    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
  });
});

const getFormState = jest.fn();
const onSubmit = jest.fn();
const goBack = jest.fn();
const setIsLocationRequired = jest.fn();
const t = jest.fn();
const basketReferenceId = 'GAA1828883';
const baseDataTestId = 'GuestDetails';

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
  billing_companyName: '',
  billing_addressLine1: '',
  billing_addressLine2: '',
  billing_addressLine3: '',
  billing_addressLine4: '',
  billing_cityName: '',
  billing_postalCode: '',
  billing_countryCode: '',
  billingAddressCheckbox: false,
};

describe('GDP CCUI Form component - Single Booking Redesign', () => {
  beforeEach(() => {
    resetMockData(); // Reset mocks before each test
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  const SingleBookingComponent = ({
    isBookingForSomeoneElse,
    currentLang,
    isGermanHotel,
    hotelBrand,
    isBillingAddressEnabled,
    isAdditionalInformationEnabled,
  }: {
    isBookingForSomeoneElse: boolean;
    currentLang: string;
    isGermanHotel: boolean;
    hotelBrand: string;
    isBillingAddressEnabled: boolean;
    isAdditionalInformationEnabled: boolean;
  }) => {
    return (
      <ReactQuery.QueryClientProvider client={new ReactQuery.QueryClient()}>
        <Form
          data-testid="guestDetailsForm"
          {...guestDetailsFormConfig({
            getFormState,
            defaultValues,
            onSubmit,
            baseDataTestId,
            t: (key: string) => {
              switch (key) {
                case 'ccui.booking.details.forThemselves':
                  return 'ccui.booking.details.forThemselves';
                case 'ccui.booking.details.forSomeoneElse':
                  return 'ccui.booking.details.forSomeoneElse';
                case 'ccui.booking.detailsTitle':
                  return 'ccui.booking.detailsTitle';
                case 'booking.contactDetails.guestDetails':
                  return 'booking.contactDetails.guestDetails';
                default:
                  return 'default';
              }
            },
            currentLang,
            currentCountry: 'GB',
            basketReferenceId,
            resetForm: 0,
            brand: 'PI',
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
            isSingleRoomRedesignEnabled: true,
            isMultiRoomRedesignEnabled: true,
            isBookingForSomeoneElse,
            setIsBookingForSomeoneElse: mockSetIsBookingForSomeoneElse,
            isGermanHotel,
            isBillingAddressEnabled,
            isAdditionalInformationEnabled,
            showCheckInInfo: {},
            setShowCheckInInfo: jest.fn(),
          })}
        />
      </ReactQuery.QueryClientProvider>
    );
  };

  const mockSetIsBookingForSomeoneElse = jest.fn();

  it('should render page ccui guest details with Booking for Tabs for Single booking - myself ', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockMutationResponse.isError = false;
    const { getByTestId, queryByTestId, getByRole, rerender, queryByText, queryAllByText } = render(
      <SingleBookingComponent
        isBookingForSomeoneElse={false}
        currentLang={'en'}
        isGermanHotel={false}
        hotelBrand={'PI'}
        isBillingAddressEnabled={false}
        isAdditionalInformationEnabled={false}
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
      name: /ccui.booking.details.forThemselves/i,
    });
    const someoneElseTab = getByRole('radio', {
      name: /ccui.booking.details.forSomeoneElse/i,
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
        isBillingAddressEnabled={false}
        isAdditionalInformationEnabled={false}
      />
    );

    await waitFor(() => {
      // Details of the person making the booking - shown
      expect(queryAllByText('ccui.booking.detailsTitle')).toBeTruthy();
      expect(queryAllByText('ccui.booking.detailsTitle')[0]).toBeInTheDocument();
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
        isBillingAddressEnabled={false}
        isAdditionalInformationEnabled={false}
      />
    );

    await waitFor(() => {
      // Details of the person making the booking - not to be shown
      expect(queryByText('booking.guestDetails.personDetails')).toBeFalsy();
    });
  });
});

describe('GDP CCUI address selection options - De locale, multiroom design feature switch', () => {
  beforeEach(() => {
    resetMockData(); // Reset mocks before each test
    // Enable only accompanying guest feature like original
    mockFeatureToggles = {
      release_ccui_accompanying_guest_details: true,
    };
    mockCustomLocale.mockReturnValue({
      language: 'de',
      country: 'de',
    });
  });
  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should include accompanying guest details with multi rooms for one room', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    // Set brand to PI (non-German hotel) - accompanying guest feature is for non-German hotels
    getHotelInformation.data.hotelInformation.brand = 'PI';
    getBookingInformationData.data.bookingInformation.reservationByIdList.push({
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
    });
    const { getAllByTestId } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    const sections = getAllByTestId('GuestDetails-leadGuest-AccompanyingContainer');
    expect(sections.length).toBeGreaterThan(0);
  });

  it('should include accompanying guest details with multi rooms for each room', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    // Set brand to PI (non-German hotel) - accompanying guest feature is for non-German hotels
    getHotelInformation.data.hotelInformation.brand = 'PI';
    getBookingInformationData.data.bookingInformation.reservationByIdList.push({
      roomStay: {
        adultsNumber: 2,
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
    });
    const { getAllByTestId } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    const sections = getAllByTestId('GuestDetails-leadGuest-AccompanyingContainer');
    expect(sections.length).toBeGreaterThan(1);
  });

  it('should not include accompanying guest details with single room 1 adult', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    getBookingInformationData.data.bookingInformation.reservationByIdList = [
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
    ];
    const { queryByTestId } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(queryByTestId('GuestDetails-leadGuest-AccompanyingContainer')).not.toBeInTheDocument();
  });

  it('should not include accompanying guest details with single room 2 adults german hotel', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    // Keep brand as PID (German hotel) - accompanying guest feature disabled for German hotels
    // Note: default mock already has brand = 'PID' which makes isGermanHotel = true
    // Set up single room with 2 adults
    getBookingInformationData.data.bookingInformation.reservationByIdList = [
      {
        roomStay: {
          adultsNumber: 2,
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
    ];
    const { queryByTestId } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(queryByTestId('GuestDetails-leadGuest-AccompanyingContainer')).not.toBeInTheDocument();
  });
});

describe('GDP CCUI address selection options - UK locale, multiroom design feature switch', () => {
  beforeEach(() => {
    resetMockData(); // Reset mocks before each test
    jest.clearAllMocks();
    // Enable only accompanying guest feature like original
    mockFeatureToggles = {
      release_ccui_accompanying_guest_details: true,
    };
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
  });
  afterEach(() => {
    jest.clearAllMocks();
  });

  it('Display different billing address toggle for German hotels ', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    const BillingAddressComponent = () => {
      return (
        <ReactQuery.QueryClientProvider client={new ReactQuery.QueryClient()}>
          <Form
            {...guestDetailsFormConfig({
              getFormState,
              defaultValues: { ...{ ...defaultValues, billingAddressCheckbox: true } },
              onSubmit,
              baseDataTestId,
              t,
              currentLang: 'de',
              currentCountry: 'GB',
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
              isSingleRoomRedesignEnabled: true,
              isMultiRoomRedesignEnabled: true,
              isBookingForSomeoneElse: false,
              setIsBookingForSomeoneElse: jest.fn(),
              isGermanHotel: true,
              isBillingAddressEnabled: true,
              showCheckInInfo: {},
              setShowCheckInInfo: jest.fn(),
            })}
          />
        </ReactQuery.QueryClientProvider>
      );
    };
    mockMutationResponse.isError = false;
    const { getByTestId, container } = render(<BillingAddressComponent />);

    const billingAddressCheckbox = container.querySelector('input[type="checkbox"]') as HTMLElement;

    await userEvent.click(billingAddressCheckbox);

    expect(billingAddressCheckbox).toBeTruthy();
    expect(getByTestId('GuestDetails-BillingAddress-PersonalAddress')).toBeVisible();

    const radioOptionHomeAddress = getByTestId('GuestDetails-BillingAddress-PersonalAddress');
    expect(radioOptionHomeAddress).toBeInTheDocument();
    fireEvent.click(radioOptionHomeAddress);

    const inputAddressLine1 = getByTestId('input-billing_addressLine1');
    expect(inputAddressLine1).toBeInTheDocument();
    fireEvent.focus(inputAddressLine1);
    fireEvent.change(inputAddressLine1, { target: { value: 'Street 22 December' } });

    const inputPostAlCode = getByTestId('input-billing_postalCode');
    expect(inputPostAlCode).toBeInTheDocument();
    const postCode = 'CR7 7EZ' as string;
    fireEvent.focus(inputPostAlCode);
    fireEvent.change(inputPostAlCode, { target: { value: postCode } });
    expect(inputPostAlCode).toHaveValue(postCode);
  });
});

describe('GDP CCUI - FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE integration', () => {
  const PII_FLAG_KEY = 'release_ccui_remove_pii_data_from_local_storage';
  const mockRouter = { query: jest.fn(), push: jest.fn() };
  let originalFeatureToggles: typeof mockFeatureToggles;
  let originalPackagesData: typeof getPackagesData.data;

  beforeEach(() => {
    resetMockData();
    originalFeatureToggles = { ...mockFeatureToggles };
    originalPackagesData = getPackagesData.data;
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockCustomLocale.mockReturnValue({ language: 'en', country: 'gb' });
    mockMutationResponse.isError = false;
    mockUseLocalStorage.mockImplementation((key: string, initialValue: unknown) => {
      if (key === 'formDetails') {
        return [defaultFormDetails, jest.fn()];
      }
      return [initialValue, jest.fn()];
    });
    (clearGuestFormData as jest.Mock).mockResolvedValue(true);
  });

  afterEach(() => {
    mockFeatureToggles = originalFeatureToggles;
    getPackagesData.data = originalPackagesData;
    jest.clearAllMocks();
  });

  it('should merge cachedGuestDetailsFormData and prefer cached values when flag is true', async () => {
    mockFeatureToggles = {
      ...mockFeatureToggles,
      [PII_FLAG_KEY]: true,
    } as typeof mockFeatureToggles;

    const { getByTestId } = render(
      <GuestDetailsPageCcui
        {...mockProps}
        queryClient={queryClient}
        router={mockRouter as any}
        cachedGuestDetailsFormData={{
          firstName: 'CachedFirstName',
          lastName: 'CachedLastName',
        }}
      />
    );

    await waitFor(() => {
      expect((getByTestId('input-firstName') as HTMLInputElement).value).toBe('CachedFirstName');
      expect((getByTestId('input-lastName') as HTMLInputElement).value).toBe('CachedLastName');
    });
  });

  it('should not use cachedGuestDetailsFormData when flag is false', async () => {
    mockFeatureToggles = {
      ...mockFeatureToggles,
      [PII_FLAG_KEY]: false,
    } as typeof mockFeatureToggles;

    const { getByTestId } = render(
      <GuestDetailsPageCcui
        {...mockProps}
        queryClient={queryClient}
        router={mockRouter as any}
        cachedGuestDetailsFormData={{ firstName: 'CachedFirstName' }}
      />
    );

    await waitFor(() => {
      expect((getByTestId('input-firstName') as HTMLInputElement).value).toBe('');
    });
  });

  it('should not pre-populate form when flag is undefined', async () => {
    mockFeatureToggles = {
      ...mockFeatureToggles,
      [PII_FLAG_KEY]: undefined,
    } as typeof mockFeatureToggles;

    const { getByTestId } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();

    await waitFor(() => {
      expect((getByTestId('input-firstName') as HTMLInputElement).value).toBe('');
    });
  });

  it('should not call clearGuestFormData when flag is false', async () => {
    mockFeatureToggles = {
      ...mockFeatureToggles,
      [PII_FLAG_KEY]: false,
    } as typeof mockFeatureToggles;

    render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    await waitFor(() => {
      expect(clearGuestFormData).not.toHaveBeenCalled();
    });
  });

  it('should populate defaultValues from bkngData when release_ccui_remove_pii_data_from_local_storage flag is enabled', async () => {
    mockFeatureToggles = {
      ...mockFeatureToggles,
      [PII_FLAG_KEY]: true,
    } as typeof mockFeatureToggles;

    mockUseLocalStorage.mockImplementation((key: string, initialValue: unknown) => {
      if (key === 'formDetails') {
        return [
          {
            ...defaultFormDetails,
            updated: true,
            basketReferenceId: mockProps.biQueryInput.basketReference,
            firstName: 'FormDetailsFirstName',
            lastName: 'FormDetailsLastName',
          },
          jest.fn(),
        ];
      }
      return [initialValue, jest.fn()];
    });

    getBookingInformationData.data.bookingInformation = {
      ...getBookingInformationDataOriginal.data.bookingInformation,
      reservationByIdList: [
        {
          billing: { firstName: 'BookingFirstName', lastName: 'BookingLastName', title: 'Herr' },
          reservationGuestList: [
            {
              givenName: 'BookingFirstName',
              surName: 'BookingLastName',
              nameTitle: 'Herr',
              isAccompanyingGuest: false,
            },
          ],
          roomStay:
            getBookingInformationDataOriginal.data.bookingInformation.reservationByIdList[0]
              .roomStay,
        },
      ],
    };

    const { getByTestId } = render(
      <GuestDetailsPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    await waitFor(() => {
      expect((getByTestId('input-firstName') as HTMLInputElement).value).toBe('BookingFirstName');
      expect((getByTestId('input-lastName') as HTMLInputElement).value).toBe('BookingLastName');
    });
  });

  it('should prefer cachedGuestDetailsFormData over bkngData when both are present and release_ccui_remove_pii_data_from_local_storage flag is enabled', async () => {
    mockFeatureToggles = {
      ...mockFeatureToggles,
      [PII_FLAG_KEY]: true,
    } as typeof mockFeatureToggles;

    getBookingInformationData.data.bookingInformation = {
      ...getBookingInformationDataOriginal.data.bookingInformation,
      reservationByIdList: [
        {
          billing: { firstName: 'BookingFirstName', lastName: 'BookingLastName', title: 'Mr' },
          reservationGuestList: [
            {
              givenName: 'BookingFirstName',
              surName: 'BookingLastName',
              nameTitle: 'Mr',
              isAccompanyingGuest: false,
            },
          ],
          roomStay:
            getBookingInformationDataOriginal.data.bookingInformation.reservationByIdList[0]
              .roomStay,
        },
      ],
    };

    const { getByTestId } = render(
      <GuestDetailsPageCcui
        {...mockProps}
        queryClient={queryClient}
        router={mockRouter as any}
        cachedGuestDetailsFormData={{
          firstName: 'CachedFirstName',
          lastName: 'CachedLastName',
        }}
      />
    );

    await waitFor(() => {
      expect((getByTestId('input-firstName') as HTMLInputElement).value).toBe('CachedFirstName');
      expect((getByTestId('input-lastName') as HTMLInputElement).value).toBe('CachedLastName');
    });
  });
});
