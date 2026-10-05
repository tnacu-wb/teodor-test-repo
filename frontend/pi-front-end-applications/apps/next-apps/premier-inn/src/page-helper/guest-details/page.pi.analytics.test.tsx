import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import {
  getBookingInformationData as getBookingInformationDataOriginal,
  getHotelInformation as getHotelInformationOriginal,
  getCountriesData as getCountriesDataOriginal,
  getPostCodeAddresesData as getPostCodeAddresesDataOriginal,
  getPostCodeAddresesInfoData as getPostCodeAddresesInfoDataOriginal,
  defaultValuesWithGuests,
  getRoomSelectionData as getRoomSelectionDataOriginal,
} from '@whitbread-eos/utils';
import React from 'react';

import { fireEvent, render } from '~utils/test-utils';

import GuestDetailsPage from './page.pi';

// Mutable mock data - cloned from shared originals
const getBookingInformationData = JSON.parse(JSON.stringify(getBookingInformationDataOriginal));
const getHotelInformation = JSON.parse(JSON.stringify(getHotelInformationOriginal));
const getCountriesData = JSON.parse(JSON.stringify(getCountriesDataOriginal));
const getPostCodeAddresesData = JSON.parse(JSON.stringify(getPostCodeAddresesDataOriginal));
const getPostCodeAddresesInfoData = JSON.parse(JSON.stringify(getPostCodeAddresesInfoDataOriginal));
const getRoomSelectionData = JSON.parse(JSON.stringify(getRoomSelectionDataOriginal));

beforeEach(() => {
  // Reset the modules back to their original state.
  // In this case, we are setting `node-fetch` back
  // to the original, unmocked version
  jest.unmock('next/dynamic');
});
const getHotelInformationPID = {
  ...getHotelInformation,
  data: {
    ...getHotelInformation.data,
    hotelInformation: {
      ...getHotelInformation.data.hotelInformation,
      brand: 'PID',
    },
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
const queryClient = new ReactQuery.QueryClient();

const mockProps = {
  biQueryInput: {
    basketReference: '12',
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
//endregion

//region Jest Mock
function mockUseQueryRequest(queryKey) {
  const key = queryKey[0];

  if (typeof key === 'string') {
    switch (key) {
      case 'GetBookingInformation':
        return getBookingInformationData;
      case 'GetPackages':
        return getPackagesData;
      case 'GetHotelInformation':
        return getHotelInformationPID;
      case 'GetRoomSelection':
        return getRoomSelectionData;
      case 'GetCountries':
        return getCountriesData;
      case 'getCountriesWithNationality':
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

function mockDynamic(props) {
  const { setShowCheckInInfo, showCheckInInfo } = props.elements.fields.find(
    (field) => field.name === 'leadGuest'
  ).props;

  if (!Object.keys(showCheckInInfo).some((key) => showCheckInInfo[key])) {
    setShowCheckInInfo({ test: true });
  }
  const onSubmit = () => props.elements.buttons[0].action(defaultValuesWithGuests);

  return (
    <form data-testid="mocked-form" onSubmit={() => onSubmit()}>
      <button type="submit">Submit</button>
    </form>
  );
}

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  getNoOfDaysInYear: jest.fn(),
  SEO: () => <div></div>,
  LeadGuestDetails: () => <div data-testid="LeadGuestDetails"></div>,
  Notice: () => <div data-testid="Notice"></div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
  invalidateQueries: mockUseQueryRequest,
  useQuery: () => mockUseQueryRequest,
  useQueryRequest: mockUseQueryRequest,
  useMutationRequest: () => mockMutationResponse,
  useRestMutationRequest: () => ({
    mutation: jest.fn(),
  }),
  usePackages: () => ({
    ...getPackagesData?.data?.packages,
    ...getPackagesData,
  }),
  graphQLRequest: jest.fn(),
  updateAncillariesAnalytics: () => jest.fn(),
  useUserData: () => mockUserData(),
  analytics: {
    update: jest.fn().mockReturnValue({
      guestDetails: {
        addCheckinInfo: false,
        guestConsent: false,
        addressLine1: false,
        addressLine2: false,
        addressLine3: false,
        postalCode: false,
        location: false,
        countrySelection: false,
        email: false,
        birthDate: false,
        nationality: false,
        passportNum: false,
      },
    }),
  },
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('next/dynamic', () => {
  return jest.fn(() => {
    return mockDynamic;
  });
});

describe('PI Form component when user is logged in for form submit without window mock', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
    mockUserData.mockReturnValue({ isLoggedIn: true });
  });
  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should render page pi guest details page when user mock organisms', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockMutationResponse.isError = false;

    const { getByTestId } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    const formButton = getByTestId('mocked-form');

    expect(formButton).toBeInTheDocument();
    fireEvent.submit(formButton);

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
  });
});

describe('PI Form component when user is logged in for form submit', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
    mockUserData.mockReturnValue({ isLoggedIn: true });
    global.originalWindow = { ...window };

    // Mock window.piConfig
    Object.defineProperty(window, 'piConfig', {
      writable: true,
      value: {
        paymentsRedesign: {
          mode: 'testMode', // Mock mode as needed
        },
      },
    });
  });
  afterEach(() => {
    window.piConfig = global.originalWindow.piConfig;
    jest.clearAllMocks();
  });

  it('should render page pi guest details page when user mock organisms', async () => {
    const mockRouter = {
      query: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockMutationResponse.isError = false;

    const { getByTestId } = render(
      <GuestDetailsPage {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    const formButton = getByTestId('mocked-form');

    expect(formButton).toBeInTheDocument();
    fireEvent.submit(formButton);

    expect(getByTestId('GuestDetails-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GuestDetails-PageContent')).toBeInTheDocument();
  });
});
