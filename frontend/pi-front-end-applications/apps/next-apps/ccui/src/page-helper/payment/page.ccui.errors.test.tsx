import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import { PaymentsErrorHandling } from './page.ccui.errors';

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

const mockCustomLocale = jest.fn();
const mockUseSessionStorage = jest.fn(() => [{}, jest.fn()]);
const mockUseTranslation = jest.fn();

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => mockUseTranslation(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useMutationRequest: () => ({
    mutation: {
      mutate: jest.fn(),
    },
    isSuccess: false,
  }),
  useQueryRequest: (queryKey) => {
    if (queryKey[0] === 'GetBookingInformation') {
      return {
        data: {
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
        },
      };
    }
    if (queryKey === 'GetHotelInformation') {
      return {
        data: {
          country: 'GB',
          hotelId: '1',
          language: 'en',
        },
      };
    }
    if (queryKey[0] === 'GetPackages') {
      return {
        data: {
          packages: {},
        },
      };
    }
  },
  graphQLRequest: jest.fn(),
  usePackages: () => ({
    ...mockGetPackagesData?.data?.packages,
    ...mockGetPackagesData,
  }),
  useSessionStorage: () => mockUseSessionStorage(),
}));
jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  setAnalyticsUser: jest.fn(),
}));

const expectedResult = {
  accessToken: 'asasasGderg12312',
  basketReference: '12',
  dehydratedState: {
    mutations: undefined,
    queries: undefined,
  },
  hiQueryInput: {
    country: 'GB',
    hotelId: '1',
    language: 'en',
  },
  pcksQueryInput: {
    adultsNumber: 0,
    bookingFlowId: '31',
    childrenNumber: 0,
    country: 'GB',
    endDate: '23-11-2023',
    hotelId: '1',
    language: 'en',
    nightsNumber: NaN,
    startDate: '22-11-2023',
  },
};

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('Payment Errors page', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue({
      locale: 'gb',
    });
    mockUseTranslation.mockReturnValue({
      t: (key) => {
        switch (key) {
          case 'errors.payment.PAYMENT_CONTACT_BANK_EXCEPTION_VALUE':
            return 'Error with bank';
          default:
            return key;
        }
      },
    });
  });
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
  });
  it('should render Payment Errors page', async () => {
    const queryClient = new ReactQuery.QueryClient();
    const { getByTestId } = render(
      <ReactQuery.QueryClientProvider client={queryClient}>
        <PaymentsErrorHandling user={undefined} {...expectedResult} />
      </ReactQuery.QueryClientProvider>
    );
    await waitFor(() => {
      expect(getByTestId('paymentErrorHandling')).toBeInTheDocument();
      expect(getByTestId('paymentErrorHandling-backToDetails_leisure')).toBeInTheDocument();
      expect(getByTestId('paymentErrorHandling_wrapperTitle')).toBeInTheDocument();
    });
  });
  it('should render the BackToPaymentsPage button ', () => {
    const queryClient = new ReactQuery.QueryClient();
    const mockRouter = {
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    const { getByTestId } = render(
      <ReactQuery.QueryClientProvider client={queryClient}>
        <PaymentsErrorHandling user={undefined} {...expectedResult} />
      </ReactQuery.QueryClientProvider>
    );
    const button = getByTestId('paymentErrorHandling-backToDetails_back-entire');
    expect(button).toBeInTheDocument();
    fireEvent.click(button);
    expect(mockRouter.push).toHaveBeenCalledWith('/gb/en/payment?reservationId=12');
  });
  it('should render Payment Errors page DE', async () => {
    mockCustomLocale.mockReturnValue({
      language: 'de',
    });
    const queryClient = new ReactQuery.QueryClient();
    const { getByTestId } = render(
      <ReactQuery.QueryClientProvider client={queryClient}>
        <PaymentsErrorHandling user={undefined} {...expectedResult} />
      </ReactQuery.QueryClientProvider>
    );
    expect(getByTestId('paymentErrorHandling')).toBeInTheDocument();
    expect(getByTestId('paymentErrorHandling-backToDetails_leisure')).toBeInTheDocument();
    expect(getByTestId('paymentErrorHandling_wrapperTitle')).toBeInTheDocument();
  });

  it('should render Payment Errors when error is planet specific', async () => {
    const errorInfo = {
      errCode: 246,
      debugMessage: '5',
      globalErrTextTemplate: 'PAYMENT_CONTACT_BANK_EXCEPTION_VALUE',
    };
    mockUseSessionStorage.mockReturnValue([errorInfo, jest.fn()]);
    const queryClient = new ReactQuery.QueryClient();
    const { getByText } = render(
      <ReactQuery.QueryClientProvider client={queryClient}>
        <PaymentsErrorHandling user={undefined} {...expectedResult} />
      </ReactQuery.QueryClientProvider>
    );
    expect(getByText('Error with bank')).toBeInTheDocument();
  });
});
